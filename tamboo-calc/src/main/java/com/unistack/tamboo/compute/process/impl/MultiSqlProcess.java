package com.unistack.tamboo.compute.process.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.databricks.spark.avro.SchemaConverters;
import com.google.common.collect.Lists;
import com.unistack.tamboo.commons.utils.SchemaUtils;
import com.unistack.tamboo.compute.calc.CalcStop;
import com.unistack.tamboo.compute.process.StreamProcess;
import com.unistack.tamboo.compute.utils.KafkaUtil;
import com.unistack.tamboo.compute.utils.spark.AvroUtil;
import com.unistack.tamboo.compute.utils.spark.CalcConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import scala.collection.Iterator;
import java.io.File;
import java.io.IOException;
import java.util.*;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;


/**
 * @author hero.li
 *   globalConfig对应的消息格式:{"template":{"streamTables":[{"streamTable":"hy4","alias":"hy4","topicName":"source_hy4_test4"}],"datasources":[{"creator":"admin","addTime":1530849832000,"ip":"192.168.1.191","dbName":"test","dbType":"MYSQL","dbtable":"lyzx_offline1","dataSourceDesc":"193上面的test库","isActive":"1","dataSourceName":"191_test","dataSourceId":22576,"password":"welcome1","port":"3308","db":{"creator":"admin","dataSourceId":22576,"password":"welcome1","addTime":1530849832000,"port":"3308","ip":"192.168.1.191","dbName":"test","dbType":"MYSQL","dataSourceDesc":"193上面的test库","isActive":"1","dataSourceName":"191_test","username":"root"},"username":"root"}],"windowLen":2,"windowSlide":1,"group":["admin"],"sql":"select a.id as a_id,b.sink as b_sink,c.yh_code as c_yhCode from hy3  a inner join hy4  b on a.id= b.id inner join lyzx_offline1 c on a.id = c.id"},"type":14,"appName":"calc_source_hy3_test3_14_20180808122254","queue":"root.admin","kafka":{"acl_username":"Tu_adhpoc5","from_topic":"source_hy3_test3","to_topic":"admin-383-today_test3-1533702174314-E1","group_id":"admin-383-today_test3-20180808122254","alias":"hy3","acl_password":"Tp_nbrvwwp"}}
 */
public class MultiSqlProcess implements StreamProcess {
    private static Logger LOGGER = Logger.getLogger(MultiSqlProcess.class);

    private JSONObject kafka;
    private String appName;
    private JSONArray streamTables;
    private SparkSession spark;
    private JSONArray datasources;
    private String sql;
    private String toTopic;

    public MultiSqlProcess(JSONObject globalConfig){
        kafka = globalConfig.getJSONObject("kafka");
        toTopic = kafka.getString("to_topic");
        appName = globalConfig.getString("appName");
        JSONObject template = globalConfig.getJSONObject("template");
        streamTables = template.getJSONArray("streamTables");
        datasources = template.getJSONArray("datasources");
        sql = template.getString("sql");
        LOGGER.info("SPARK:SQL模块初始化完成");
    }

    /**
     * @param line
     */
    @Override
    public void logic(JavaInputDStream<ConsumerRecord<String, String>> line){
        //1、获取sparkSession
        SparkSession.Builder builder = SparkSession.builder().appName(appName);
        if (CalcConfig.CALC_IS_TEST){
            builder.master("local[6]");
        }
        spark = builder.getOrCreate();
        LOGGER.info("SPARK:MultiSqlSparkSession初始化成功!");

        //2、注册流数据表
        boolean streamTablesResister = streamTablesResister(streamTables, kafka);
        if(!streamTablesResister){
            LOGGER.error("SPARK:流数据表注册失败!程序终止!");
            return;
        }

        //3、注册离线数据表
        boolean offlineTableRegister = offlineTableRegister();
        if (!offlineTableRegister){
            LOGGER.error("SPARK:离线数据表注册失败!程序终止!");
            return;
        }

        //4、执行join操作
        Dataset<Row> joinResult;
        try{
            joinResult = spark.sql(sql);
        }catch(Exception e){
            LOGGER.error("SPARK:sql执行异常!程序退出,sql=" + sql, e);
            return;
        }

        LOGGER.info("SPARK:MultiSql_集群join成功");
        //如果checkPoint存在就删除
        File checkPointFile = new File(CalcConfig.CALC_MULTISQL_CHECK_POINT_PATH);
        if(checkPointFile.exists()){
            try{
                FileUtils.deleteDirectory(checkPointFile);
                LOGGER.info("SPARK:checkPoint ");
            }catch (IOException e){e.printStackTrace();}
        }

        //5、将执行的结果发送到输出topic中
        JSONObject joinResultJsonSchema = AvroUtil.getStringSchema(joinResult);
        LOGGER.info("SPARK: MultiSql join的结果schema:"+joinResultJsonSchema);
        JSONObject registerStatus = SchemaUtils.registerSchema(joinResultJsonSchema, toTopic + "-value");
        LOGGER.info("SPARK: MultiSql join结果schema注册结果:"+registerStatus);

        StreamingQuery query =
                joinResult
                    .mapPartitions(itr->{
                        Properties props = KafkaUtil.getKafkaAvroProducerProperties();
                        KafkaProducer producer = new KafkaProducer(props);
                        Schema.Parser parser = new Schema.Parser();
                        Schema schema = parser.parse(joinResultJsonSchema.toString());
                        GenericRecord avroRecord = new GenericData.Record(schema);
                        JSONArray fields = joinResultJsonSchema.getJSONArray("fields");
                        while(itr.hasNext()){
                            Row row = itr.next();
                            AvroUtil.rowToAvroRecord(fields, row, avroRecord);
                            LOGGER.info("SPARK:MultiSql要发送送的数据:" + avroRecord);
                            ProducerRecord<Object, Object> record = new ProducerRecord<>(toTopic, String.valueOf(avroRecord.hashCode()), avroRecord);
                            producer.send(record);
                        }
                        return Collections.EMPTY_LIST.iterator();
                    },Encoders.STRING())
                    .writeStream()
                    .format("console")
                    .outputMode(OutputMode.Append())
//                            .format("kafka")
//                            .option("kafka.bootstrap.servers",CalcConfig.KAFKA_BROKER_LIST)
//                            .option("topic",toTopic)
//                            .option("kafka.security.protocol", "SASL_PLAINTEXT")
//                            .option("kafka.sasl.mechanism", "PLAIN")
//                            .option("checkpointLocation",CalcConfig.CALC_MULTISQL_CHECK_POINT_PATH)
                    .start();
        if(!CalcConfig.CALC_IS_TEST){new CalcStop(appName, query).lunchStop();}
    }

    /**
     * 流数据表的注册,schema处理等
     *
     * @param streamTables SQL图标中添加的流数据表
     * @param kafka        输入图标中表示的流数据表
     *                     注册表名时别名优先于topic名称
     *                     获取schema时必须用topic名称
     */
    private boolean streamTablesResister(JSONArray streamTables, JSONObject kafka) {
        String from_topic = kafka.getString("from_topic");
        String kafka_alias = kafka.getString("alias");
        JSONObject streamTableItem = new JSONObject();
        streamTableItem.put("topicName", from_topic);
        streamTableItem.put("alias", kafka_alias);
        streamTables.add(streamTableItem);

        for(int i = 0; i < streamTables.size(); i++){
            JSONObject item = streamTables.getJSONObject(i);
            String topicName = item.getString("topicName");
            Optional<Schema> op_schema = AvroUtil.getSchema(topicName);
            if (!op_schema.isPresent()){
                LOGGER.error("SPARK:流数据表[" + topicName + "]schema获取失败!");
                return false;
            }

            Schema schema = op_schema.get();
            StructType type = (StructType) SchemaConverters.toSqlType(schema).dataType();

            Dataset<Row> df = spark
                    .readStream()
                    .format("kafka")
                    .option("kafka.bootstrap.servers", CalcConfig.KAFKA_BROKER_LIST)
                    .option("startingOffsets", "earliest")
                    .option("kafka.security.protocol", "SASL_PLAINTEXT")
                    .option("kafka.sasl.mechanism", "PLAIN")
//                    .option(SaslConfigs.SASL_JAAS_CONFIG,ConfigHelper.jaasConfigProperty("PLAIN","Tu_adhpoc5","Tp_nbrvwwp").value())
                    .option("subscribe", topicName)
                    .load();

            Iterator<StructField> itr = type.iterator();
            List<String> names = Lists.newArrayList();
            while (itr.hasNext()) {
                StructField fieldItem = itr.next();
                String name = fieldItem.name();
                names.add("v." + name);
            }

            String alias = item.getString("alias");
            String registerViewName = StringUtils.isBlank(alias) ? topicName : alias;

            Dataset<Row> d = df
                    .withWatermark("timestamp", "1 hours")
                    .select(col("value"))
                    .mapPartitions(itr1 -> {
                        LOGGER.info("SPARK:_KAFKA_REGISTRY_URL=" + CalcConfig.KAFKA_REGISTRY_URL);
                        KafkaAvroDeserializer kafkaAvroDeserializer = new KafkaAvroDeserializer();
                        Map<String, Object> props = new HashMap(2, 1);
                        props.put("schema.registry.url", CalcConfig.KAFKA_REGISTRY_URL);
                        kafkaAvroDeserializer.configure(props, false);
                        Schema schema1 = AvroUtil.getSchema(topicName).get();
                        List<String> datas = new ArrayList<>();
                        while(itr1.hasNext()){
                            Row row = itr1.next();
                            String data = kafkaAvroDeserializer.deserialize(topicName,row.getAs("value"),schema1).toString();
                            LOGGER.info("SPARK:MultiSql_ 从topic[" + topicName + "]对应的数据流表[" + registerViewName + "]中获取的join数据:" + data);
                            datas.add(data);
                        }
                        return datas.iterator();
                    },Encoders.STRING())
                    .select(from_json(col("value"),type).as("v"))
                    .selectExpr(names.toArray(new String[names.size()]));

            d.createOrReplaceTempView(registerViewName);
            LOGGER.info("SPARK:topic[" + topicName + "]对应的流数据表[" + registerViewName + "]注册成功");
        }
        LOGGER.info("SPARK:MultiSql 流数据表注册完成!表数量:" + streamTables.size());
        return true;
    }

    /**
     * 注册离线表，离线表目前只考虑了mysql
     */
    private boolean offlineTableRegister(){
        for (int i = 0; i < datasources.size(); i++) {
            Map<String, String> map = new HashMap<>(8, 1);
            JSONObject item = datasources.getJSONObject(i);
            String dbType = item.getString("dbType");
            String dbtable = item.getString("dbtable");
            String ip = item.getString("ip");
            String port = item.getString("port");
            String dbName = item.getString("dbName");

            map.put("user",item.getString("username"));
            map.put("password", item.getString("password"));
            map.put("dbtable", dbtable);

            if("mysql".equalsIgnoreCase(dbType)){
                map.put("url", "jdbc:mysql://"+ip+":"+port+"/"+dbName + "?characterEncoding=UTF8");
                map.put("driver", "com.mysql.jdbc.Driver");
            }

            if("oracle".equalsIgnoreCase(dbType)){
                map.put("url","jdbc:oracle:thin:@//"+ip+":"+port+"/"+dbName);
                map.put("driver","oracle.jdbc.driver.OracleDriver");
            }

            Dataset<Row> t = spark.read().format("jdbc").options(map).load();
            try{
                t.createOrReplaceTempView(dbtable);
            }catch (Exception e){
                LOGGER.error("SPARK:离线数据表[" + dbtable + "]注册失败,数据库类型["+dbType+"]", e);
                return false;
            }
            LOGGER.info("SPARK:离线数据表[" + dbtable + "]注册成功");
        }
        LOGGER.info("SPARK:离线数据表注册完成,表数量:" + datasources.size());
        return true;
    }
}