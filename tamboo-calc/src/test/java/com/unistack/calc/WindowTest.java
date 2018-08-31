package com.unistack.calc;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

public class WindowTest {
    public static void main(String[] args) throws InterruptedException {
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount");
        JavaStreamingContext jssc = new JavaStreamingContext(conf,Durations.seconds(2));


        JavaReceiverInputDStream<String> lines = jssc.socketTextStream("localhost", 9999);
        lines.window(Durations.seconds(8),Durations.seconds(2))
            .foreachRDD(rdd->{
                System.out.println("============================");
                rdd.foreach(item-> System.out.println(item));
                System.out.println("============================");
            });

        jssc.start();
        jssc.awaitTermination();
    }
}
