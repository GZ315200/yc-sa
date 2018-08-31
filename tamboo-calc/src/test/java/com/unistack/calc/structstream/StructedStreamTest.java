package com.unistack.calc.structstream;

import com.alibaba.fastjson.JSONObject;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.StreamingQuery;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.junit.Test;

import java.util.Arrays;

import static org.apache.spark.sql.functions.expr;

public class StructedStreamTest {

    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .master("local")
                .appName("JavaStructuredNetworkWordCount")
                .getOrCreate();

        Dataset<Row> lines = spark
                .readStream()
                .format("socket")
                .option("host", "localhost")
                .option("port", 9999)
                .load();

        Dataset<String> words = lines.as(Encoders.STRING());
        Dataset<Row> s1 = words.groupBy("value").count();


//        Dataset<Row> lines1 = spark
//                .readStream()
//                .format("socket")
//                .option("host", "localhost")
//                .option("port", 9988)
//                .load();
//
//        Dataset<String> words1 = lines1.as(Encoders.STRING());
//        Dataset<Row> s2 = words1.groupBy("value").count();
//
//        s1.withWatermark("id1","2 hours");
//        s2.withWatermark("id2","2 hours");
//
//        s1.join(s2,expr("id1 = id2"));
        StreamingQuery query = s1.writeStream()
                .outputMode("complete")
                .format("console")
                .start();

        try{query.awaitTermination();}catch(StreamingQueryException e){e.printStackTrace();}

    }

    @Test
    public void test1(){
        String[] names = {"戴沐白","奥斯卡","唐三","马红骏","小舞","宁荣荣","朱竹清"};
        JSONObject data = new JSONObject();
        for(int i=0;i<50;i++){
            data.put("id1",String.valueOf(i+1));
            data.put("code","s1");
            data.put("name",names[i%names.length]);
            System.out.println(data);
        }
    }
}
