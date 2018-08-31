package com.unistack.calc.structstream;


import com.unistack.tamboo.commons.utils.ConfigHelper;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.types.MetadataBuilder;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.from_json;
import static org.apache.spark.sql.types.DataTypes.StringType;

public class StructedStreamTest6 {
//    private static  String ips = "192.168.1.194:9092";
    private static  String ips = "192.168.1.110:9093,192.168.1.111:9093,192.168.1.112:9093";
    public static void main(String[] args){
        System.out.println(SaslConfigs.SASL_JAAS_CONFIG);
        System.out.println(ConfigHelper.jaasConfigProperty("PLAIN","Tu_adhpoc5","Tp_nbrvwwp").value());

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
//                .option(SaslConfigs.SASL_JAAS_CONFIG,ConfigHelper.jaasConfigProperty("PLAIN","Tu_adhpoc5","Tp_nbrvwwp").value())
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
        Dataset<Row> join_result = spark.sql("select * from d5 t5");

        StreamingQuery query =
                join_result
                        .selectExpr("to_json(struct(*)) AS value")
                        .writeStream()
                        .format("console")
                        .outputMode(OutputMode.Append())
                        .start();

        try{
            query.awaitTermination();
        }catch(StreamingQueryException e){
            e.printStackTrace();
        }
    }
}