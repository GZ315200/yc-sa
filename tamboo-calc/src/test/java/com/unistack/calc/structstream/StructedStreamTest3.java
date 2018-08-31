package com.unistack.calc.structstream;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.MetadataBuilder;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import static org.apache.spark.sql.functions.*;
import static org.apache.spark.sql.types.DataTypes.StringType;


/**
 * onlineStreaming  and offlineStreaming join
 */
public class StructedStreamTest3 {
    private static  String ips = "192.168.1.194:9092";

    public static void main(String[] args) throws StreamingQueryException {
        SparkSession spark = SparkSession.builder().appName("app").master("local[6]").getOrCreate();

        Dataset<Row> df1 = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers",ips)
                .option("startingOffsets", "earliest")
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

        Dataset<Row> df2 = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers",ips)
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



        Dataset<Row> df7 = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers",ips)
                .option("startingOffsets", "earliest")
                .option("subscribe", "yh7")
                .load();

        StructField[] fields7={
                new StructField("yh7_id",StringType, true,b.build()),
                new StructField("yh7_age",StringType, true,b.build()),
                new StructField("yh7_name",StringType, true,b.build())
        };

        StructType type7 = new StructType(fields7);
        Dataset<Row> d7 = df7
                .withWatermark("timestamp","1 hours")
                .selectExpr("CAST(value AS STRING)")
                .select(from_json(col("value"),type7).as("v"))
                .selectExpr("v.yh7_id","v.yh7_age","v.yh7_name");


        Dataset<Row> df8 = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers",ips)
                .option("startingOffsets", "earliest")
                .option("subscribe", "yh8")
                .load();

        StructField[] fields8={
                new StructField("yh8_id",StringType, true,b.build()),
                new StructField("yh8_age",StringType, true,b.build()),
                new StructField("yh8_name",StringType, true,b.build())
        };

        StructType type8 = new StructType(fields8);
        Dataset<Row> d8 = df8
                .withWatermark("timestamp","1 hours")
                .selectExpr("CAST(value AS STRING)")
                .select(from_json(col("value"),type8).as("v"))
                .selectExpr("v.yh8_id","v.yh8_age","v.yh8_name");


        Dataset<Row> df9 = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers",ips)
                .option("startingOffsets", "earliest")
                .option("subscribe", "yh9")
                .load();

        StructField[] fields9={
                new StructField("yh9_id",StringType, true,b.build()),
                new StructField("yh9_age",StringType, true,b.build()),
                new StructField("yh9_name",StringType, true,b.build())
        };

        StructType type9 = new StructType(fields9);
        Dataset<Row> d9 = df9
                .withWatermark("timestamp","1 hours")
                .selectExpr("CAST(value AS STRING)")
                .select(from_json(col("value"),type9).as("v"))
                .selectExpr("v.yh9_id","v.yh9_age","v.yh9_name");




        Map<String, String> map = new HashMap<>();
        map.put("url", "jdbc:mysql://192.168.1.191:3308/test?user=root&password=welcome1&characterEncoding=UTF8");
        map.put("dbtable", "t1_1w");
        Dataset<Row> table1 = spark.read().format("jdbc").options(map).load();
        Dataset<Row> d1 = table1.select(col("id").as("yh1_id"), col("age").as("yh1_age"), col("height").as("yh1_height"));



        map.put("url", "jdbc:mysql://192.168.1.191:3308/test?user=root&password=welcome1&characterEncoding=UTF8");
        map.put("dbtable", "t2_1w");
        Dataset<Row> table2 = spark.read().format("jdbc").options(map).load();
        Dataset<Row> d2 = table2.select(col("id").as("yh2_id"), col("age").as("yh2_age"), col("height").as("yh2_height"));

        map.put("url", "jdbc:mysql://192.168.1.191:3308/test?user=root&password=welcome1&characterEncoding=UTF8");
        map.put("dbtable", "t3_1w");
        Dataset<Row> table3 = spark.read().format("jdbc").options(map).load();
        Dataset<Row> d3 = table3.select(col("id").as("yh3_id"), col("age").as("yh3_age"), col("height").as("yh3_height"));


        Dataset<Row> result =
              d5.join(d6, expr("yh5_id = yh6_id"))
                .join(d7, expr("yh5_id = yh7_id"))
                .join(d8, expr("yh5_id = yh8_id"))
                .join(d9, expr("yh5_id = yh9_id"))
                .join(d1, expr("yh5_id = yh1_id"))
                .join(d2, expr("yh5_id = yh2_id"))
                .join(d3, expr("yh5_id = yh3_id"))
                ;

        String checkPointPath = "./checkpoint";
        File checkPointFile = new File(checkPointPath);
        if(checkPointFile.exists()){
            boolean delete = checkPointFile.delete();
            System.out.println("========="+delete);
        }


        StreamingQuery query =
              result
                  .selectExpr("CAST(yh5_id AS STRING) AS key","to_json(struct(*)) AS value")
                  .writeStream()
                  .format("kafka")
                  .option("kafka.bootstrap.servers",ips)
                  .option("topic", "yh_x")
                  .option("checkpointLocation",checkPointPath)
                  .start();

        try{
            query.awaitTermination();
        }catch(StreamingQueryException e){
            e.printStackTrace();
        }

    }
}