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
import static org.apache.spark.sql.functions.*;
import static org.apache.spark.sql.types.DataTypes.IntegerType;



public class StructedStreamTest2 {



//192.168.1.110:9093,192.168.1.111:9093,192.168.1.112:9093
    private static  String ips = "192.168.1.110:9093,192.168.1.111:9093,192.168.1.112:9093";

    public static void main(String[] args) {
//        System.setProperty("java.security.auth.login.config","/Users/frank/Desktop/shell/lyh.conf");

        SparkSession spark = SparkSession
                                .builder()
                                .appName("app")
                                .master("local[6]")
                                .getOrCreate();


        Dataset<Row> df1 = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers",ips)
                .option("startingOffsets", "earliest")
//                .option("security.protocol","SASL_PLAINTEXT")
//                .option("sasl.mechanism","PLAIN")
                .option("subscribe", "yh1")
                .load();


        MetadataBuilder b = new MetadataBuilder();
        StructField[] fields = {
                new StructField("id",IntegerType, true,b.build()),
                new StructField("age",IntegerType, true,b.build()),
                new StructField("height",IntegerType, true,b.build())
        };

        StructType type = new StructType(fields);
        Dataset<Row> d1 = df1
                .withWatermark("timestamp","1 hours")
                .selectExpr("CAST(value AS STRING)")
                .select(from_json(col("value"),type).as("v"))
                .selectExpr("v.id","v.age","v.name");


        Dataset<Row> df2 = spark
                .readStream()
                .format("kafka")
                .option("kafka.bootstrap.servers",ips)
                .option("startingOffsets", "earliest")
//                .option("security.protocol","SASL_PLAINTEXT")
//                .option("sasl.mechanism","PLAIN")
                .option("subscribe", "yh2")
                .load();

        StructField[] fields2={
                new StructField("yh2_id",IntegerType, true,b.build()),
                new StructField("yh2_age",IntegerType, true,b.build()),
                new StructField("yh2_name",IntegerType, true,b.build())
        };

        StructType type2 = new StructType(fields2);
        Dataset<Row> d2 = df2
                .withWatermark("timestamp","1 hours")
                .selectExpr("CAST(value AS STRING)")
                .select(from_json(col("value"),type2).as("v"))
                .selectExpr("v.yh2_id","v.yh2_age","v.yh2_name");


        StreamingQuery query = d1.join(d2,expr("id = yh2_id"))
                .writeStream()
                .format("console")
                .outputMode(OutputMode.Append())
                .start();

        try{
            query.awaitTermination();
        }catch(StreamingQueryException e){
            e.printStackTrace();
        }



//  query = df \
//  .selectExpr("CAST(userId AS STRING) AS key", "to_json(struct(*)) AS value") \
//  .writeStream \
//  .format("kafka") \
//  .option("kafka.bootstrap.servers", "host1:port1,host2:port2") \
//  .option("topic", "topic1") \
//  .option("checkpointLocation", "/path/to/HDFS/dir") \
//  .start()
    }
}