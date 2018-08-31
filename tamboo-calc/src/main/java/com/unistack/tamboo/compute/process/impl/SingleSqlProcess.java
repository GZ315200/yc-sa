package com.unistack.tamboo.compute.process.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.SchemaUtils;
import com.unistack.tamboo.compute.process.StreamProcess;
import com.unistack.tamboo.compute.utils.KafkaUtil;
import com.unistack.tamboo.compute.utils.commons.CalcJsonUtil;
import com.unistack.tamboo.compute.utils.spark.AvroUtil;
import com.unistack.tamboo.compute.utils.spark.CalcConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.Iterator;

import java.util.List;
import java.util.Map;


/**
 * @author hero.li
 * spark sql处理流数据
 */
public class SingleSqlProcess implements StreamProcess {
    private static Logger LOGGER = LoggerFactory.getLogger(SingleSqlProcess.class);
    private JSONObject config;
    private String appName;
    private int windowLen;
    private int windowSlide;
    private String sql;
    private String streamTable;
    private JSONArray datasources;
    private String toTopic;
    private boolean isRegisted = false;


    public SingleSqlProcess(JSONObject config){
        this.config = config;
        this.appName = config.getString("appName");
        JSONObject template = config.getJSONObject("template");
        this.windowLen = template.getInteger("windowLen");
        this.windowSlide = template.getInteger("windowSlide");
        this.sql = template.getString("sql");
        this.datasources = template.getJSONArray("datasources");
        this.toTopic = config.getJSONObject("kafka").getString("to_topic");
        this.streamTable = config.getJSONObject("kafka").getString("from_topic");
    }

    @Override
    public void logic(JavaInputDStream<ConsumerRecord<String,String>> line) {
            line.map(r -> (Object)r.value())
//                .window(Durations.milliseconds(CalcConfig.CALC_BATCH_INTERVAL * windowLen), Durations.milliseconds(CalcConfig.CALC_BATCH_INTERVAL * windowSlide))
                .map(m->m.toString())
                .foreachRDD(rdd ->{
                    JavaRDD<String> cacheRdd = rdd.cache();
                    if (cacheRdd.count() > 0){
                        SparkSession spark = SparkSession.builder().appName(appName).getOrCreate();
                        Dataset<Row> onlineDf = spark.read().json(spark.createDataset(cacheRdd.rdd(),Encoders.STRING()));
                        onlineDf.createOrReplaceTempView(streamTable);
                        onlineDf.cache();

                        for(int i = 0; i < datasources.size(); i++){
                            JSONObject item = datasources.getJSONObject(i);
                            Map<String, String> dbMap = CalcJsonUtil.jsonToDbMap(item);
                            Dataset<Row> offlineDf = spark.read().format("jdbc").options(dbMap).load();
                            offlineDf.createOrReplaceTempView(item.getString("dbtable"));
                        }

                        try{
                            Dataset<Row> dataSet = spark.sql(sql);
                            //5、将执行的结果发送到输出topic中
                            JSONObject stringSchema = AvroUtil.getStringSchema(dataSet);
                            JSONObject registerResult = SchemaUtils.registerSchema(stringSchema,toTopic + "-value");
                            LOGGER.info("SPARK: singleSql_join的结果schema="+stringSchema);
                            LOGGER.info("SPARK: MultiSql_join结果schema注册结果"+registerResult);

                            List<JSONObject> objectList = CalcJsonUtil.dataSetToJsonList(dataSet);
                            if (!objectList.isEmpty()){
                                Producer p = new KafkaProducer<>(KafkaUtil.getKafkaAvroProducerProperties());
                                for (JSONObject item : objectList){
                                    String msg = item.toString();
                                    p.send(new ProducerRecord(toTopic, String.valueOf(System.currentTimeMillis()), msg));
                                    LOGGER.info("SPARK:SQL发送的数据:" + msg);
                                }
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                            LOGGER.error("SPARK:SQL执行SQL时异常:sql=" + sql, e);
                        }
                    }
                });
    }
}