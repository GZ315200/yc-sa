package com.unistack.tamboo.compute.process;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.api.java.JavaInputDStream;

/**
 * @author hero.li
 */
public interface StreamProcess extends java.io.Serializable{

    void logic(JavaInputDStream<ConsumerRecord<String,String>> line);
}