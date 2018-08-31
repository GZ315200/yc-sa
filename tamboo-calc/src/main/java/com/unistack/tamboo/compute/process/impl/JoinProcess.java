package com.unistack.tamboo.compute.process.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.SchemaUtils;
import com.unistack.tamboo.commons.utils.calc.DbMetaDataUtil;
import com.unistack.tamboo.compute.model.DbColumns;
import com.unistack.tamboo.compute.process.StreamProcess;
import com.unistack.tamboo.compute.process.until.joinprocess.JsonJoin;
import com.unistack.tamboo.compute.utils.KafkaUtil;
import com.unistack.tamboo.compute.utils.commons.JDBCUtil;
import com.unistack.tamboo.compute.utils.spark.AvroUtil;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author hero.li
 * 这个类用来做数据的join操作，即通过数据库链接获取数据/修改数据，最后发送出去
 * join数据格式:{"template":{"result":{"type":"json"},"datasource":{"creator":"admin","dataSourceId":22576,"password":"welcome1","addTime":1530849832000,"port":"3308","ip":"192.168.1.191","dbName":"test","dbType":"MYSQL","dataSourceDesc":"193上面的test库","isActive":"1","dataSourceName":"191_test","username":"root"},"dataType":"json","join":{"condition":[{"tableCol":"id","originCol":"age"}],"updateCol":[{"tableCol":"","originCol":""}],"joinType":"select","tableName":"lyzx_offline1"},"group":["admin"]},"type":12,"appName":"calc_source_hy3_test3_12_20180809115541","queue":"root.admin","kafka":{"acl_username":"Tu_adhpoc5","from_topic":"source_hy3_test3","to_topic":"admin-383-join_test1-1533786941311-C1","group_id":"admin-383-join_test1-20180809115541","alias":"hy3","acl_password":"Tp_nbrvwwp"}}
 */
public class JoinProcess implements StreamProcess {
    private static Logger LOGGER = Logger.getLogger(StreamProcess.class);
    /**
     * toTopic 表示要目标topic名字
     * config  join中的数据源、数据类型、以及join条件
     */
    private String toTopic;
    private JSONObject config;
    private Properties outputInfo;
    private JSONObject datasource;
    private JSONObject outTopicJsonSchema;
    private Map<String,DbColumns> columnsMap;

    public JoinProcess(JSONObject globalConfig){
        this.toTopic    = globalConfig.getJSONObject("kafka").getString("to_topic");
        this.config     = globalConfig.getJSONObject("template");
        this.outputInfo = KafkaUtil.getKafkaAvroProducerProperties();
        this.datasource = config.getJSONObject("datasource");
        String tableName = config.getJSONObject("join").getString("tableName");
        datasource.put("tableName",tableName);
        outTopicJsonSchema = initTopicSchema();
    }

    @Override
    public void logic(JavaInputDStream<ConsumerRecord<String,String>> line){
        if(null == outTopicJsonSchema){
            LOGGER.info("SPARK:输出topic的schema初始化失败,程序退出");
            return;
        }

        line.foreachRDD(rdd->{
            rdd.foreachPartition(itr->{
                Optional<Connection> op_conn = JDBCUtil.getConnection(datasource);
                if(op_conn.isPresent()){
                    Connection conn = op_conn.get();
                    try{
                        String tableName = config.getJSONObject("join").getString("tableName");
                        Optional<Map<String,DbColumns>> opSchema = getDbSchema(conn,tableName);
                        columnsMap = opSchema.get();
                        JsonJoin jsonJoin = new JsonJoin(config,conn,columnsMap);
                        Schema.Parser parser = new Schema.Parser();
                        Schema schema = parser.parse(outTopicJsonSchema.toString());
                        JSONArray fields = outTopicJsonSchema.getJSONArray("fields");

                        KafkaProducer<Object,Object> producer = new KafkaProducer<>(outputInfo);
                        while(itr.hasNext()){
                            ConsumerRecord<String,String> record = itr.next();
                            Object value = record.value();
                            LOGGER.info("SPARK:join任务接受到的数据:"+value);
                            long start = System.currentTimeMillis();
                            Optional<JSONObject> result = jsonJoin.jsonJoin(value.toString());
                            if(result.isPresent()){
                                long end = System.currentTimeMillis();
                                JSONObject data = result.get();
                                LOGGER.info("SPARK:join任务发送的数据:"+data+" 耗时:"+(end-start));
                                GenericRecord avroRecord = new GenericData.Record(schema);

                                AvroUtil.jsonToAvroRecord(fields,data, avroRecord);
                                LOGGER.info("SPARK:MultiSql要发送送的数据:" + avroRecord);
                                ProducerRecord<Object,Object> r = new ProducerRecord<>(toTopic,String.valueOf(avroRecord.hashCode()),avroRecord);
                                producer.send(r);
                            }
                        }
                    }catch(Exception e){
                        LOGGER.error("SPARK:join执行期间异常",e);
                    }finally{
                        try{conn.close();}catch(SQLException e){LOGGER.error("SPARK:连接关闭异常",e);}
                    }
                }else{
                    LOGGER.error("SPARK: Join模块 获取数据库连接失败!");
                }
            });
        });
    }

    private static Optional<Map<String,DbColumns>> getDbSchema(Connection conn,String tableName){
        try{
            DatabaseMetaData metaData = conn.getMetaData();
            String catalog = conn.getCatalog();
            Map<String,DbColumns> schema = new HashMap<>(16);
            ResultSet rs = metaData.getColumns(catalog,"%",tableName,"%");
            while(rs.next()){
                String columnName = rs.getString("COLUMN_NAME");
                DbColumns column = new DbColumns.Builder()
                        .dataType(rs.getString("DATA_TYPE"))
                        .columnSize(rs.getString("COLUMN_SIZE"))
                        .columnName(columnName)
                        .builder();
                schema.put(columnName,column);
            }
            return schema.isEmpty() ? Optional.empty() : Optional.of(schema);
        }catch(SQLException e){
            LOGGER.error("SPARK:数据库schema出初始化失败!表="+tableName,e);
            return Optional.empty();
        }
    }


    private JSONObject initTopicSchema(){
        Optional<List<JSONObject>> opColumnInfo = DbMetaDataUtil.getColumnInfo(datasource);
        if(!opColumnInfo.isPresent() || opColumnInfo.get().isEmpty()){
            LOGGER.error("SPARK:join模块获取数据库元数据失败！");
            return null;
        }

        List<JSONObject> columnInfos = opColumnInfo.get();
        JSONObject jsonSchema = toJsonSchemaWithJsonList(columnInfos);
        LOGGER.info("SPARK:JoinProcess输出的schema:"+jsonSchema);
        JSONObject registerStatus = SchemaUtils.registerSchema(jsonSchema, toTopic + "-value");
        LOGGER.info("SPARK:JoinProcess输出schema注册结果:"+registerStatus);

        boolean succeed = registerStatus.getBoolean("succeed");
        if(!succeed){
            LOGGER.info("SPARK:schema注册失败,原因:"+registerStatus.getString("msg"));
        }
        return succeed ? jsonSchema : null;
    }


    private JSONObject toJsonSchemaWithJsonList(List<JSONObject> columnInfos){
        JSONObject schemaResult = new JSONObject();
        schemaResult.put("type", "record");
        schemaResult.put("name", "joinProcess");

        JSONArray fields = new JSONArray();
        for(JSONObject item : columnInfos){
            String columnName = item.getString("COLUMN_NAME");
            String columnType = item.getString("DATA_TYPE");
            JSONObject field = new JSONObject();
            field.put("name",columnName);
            field.put("type",columnType);
            fields.add(field);
        }
        schemaResult.put("fields",fields);
        return schemaResult;
    }
}