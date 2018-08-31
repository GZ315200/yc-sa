package com.unistack.calc.structstream;


import org.apache.commons.io.FileUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.MetadataBuilder;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;
import static org.apache.spark.sql.types.DataTypes.StringType;

public class StructedStreamTest5{
//    private static  String ips = "192.168.1.194:9092";
    private static  String ips = "192.168.1.110:9093,192.168.1.111:9093,192.168.1.112:9093";





    public static void main(String[] args) {

        System.setProperty("java.security.auth.login.config","/Users/frank/Desktop/shell/lyh.conf");

        SparkSession spark = SparkSession.builder().appName("app")
                .master("local[6]")
                .getOrCreate();

        Dataset<Row> df1 = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers",ips)
                .option("startingOffsets", "earliest")
                .option("kafka.security.protocol", "SASL_PLAINTEXT")
                .option("kafka.sasl.mechanism", "PLAIN")
                .option("subscribe","yh5")
                .load();

        MetadataBuilder b = new MetadataBuilder();
        StructField[] fields = {
                new StructField("yh5_id",StringType, true,b.build()),
                new StructField("yh5_age",StringType, true,b.build()),
                new StructField("yh5_name",StringType, true,b.build())
        };

        StructType type = new StructType(fields);
        Dataset<Row> d5 = df1
                .withWatermark("timestamp","1 hours")
                .selectExpr("CAST(value AS STRING)")
                .select(from_json(col("value"),type).as("v"))
                .selectExpr("v.yh5_id","v.yh5_age","v.yh5_name");

        d5.createOrReplaceTempView("d5");

        Dataset<Row> df2 = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers",ips)
                .option("kafka.security.protocol", "SASL_PLAINTEXT")
                .option("kafka.sasl.mechanism", "PLAIN")
                .option("startingOffsets", "earliest")
                .option("subscribe", "yh6")
                .load();

        StructField[] fields2={
                new StructField("yh6_id",StringType, true,b.build()),
                new StructField("yh6_age",StringType, true,b.build()),
                new StructField("yh6_name",StringType, true,b.build())
        };

        StructType type2 = new StructType(fields2);
        Dataset<Row> d6 = df2
                .withWatermark("timestamp","1 hours")
                .selectExpr("CAST(value AS STRING)")
                .select(from_json(col("value"),type2).as("v"))
                .selectExpr("v.yh6_id","v.yh6_age","v.yh6_name");

        d6.createOrReplaceTempView("d6");





        Map<String, String> map = new HashMap<>();
        map.put("url", "jdbc:mysql://192.168.1.191:3308/test?user=root&password=welcome1&characterEncoding=UTF8");
        map.put("dbtable", "t1_1w");
        Dataset<Row> table1 = spark.read().format("jdbc").options(map).load();
        Dataset<Row> d1 = table1.select(col("id").as("yh1_id"), col("age").as("yh1_age"), col("height").as("yh1_height"));


        d1.createOrReplaceTempView("d1");





        Dataset<Row> join_result = spark.sql("select * from d5 t5 inner join d6 t6 on t5.yh5_id = t6.yh6_id  inner join d1 t1 on t5.yh5_id = t1.yh1_id");

        String checkPointPath = "./checkpoint";
        File checkPointFile = new File(checkPointPath);
        if(checkPointFile.exists()){
            try{
                FileUtils.deleteDirectory(checkPointFile);}catch(IOException e){e.printStackTrace();}
        }

        StreamingQuery query =
                join_result
                        // "CAST(yh5_id AS STRING) AS key",
                        .selectExpr("to_json(struct(*)) AS value")
                        .writeStream()
                        .format("kafka")
                        .option("kafka.bootstrap.servers",ips)
                        .option("topic","yh_x1")
                        .option("kafka.security.protocol", "SASL_PLAINTEXT")
                        .option("kafka.sasl.mechanism", "PLAIN")
                        .option("checkpointLocation",checkPointPath)
                        .start();

        try{
            query.awaitTermination();
        }catch(StreamingQueryException e){
            e.printStackTrace();
        }
    }




}