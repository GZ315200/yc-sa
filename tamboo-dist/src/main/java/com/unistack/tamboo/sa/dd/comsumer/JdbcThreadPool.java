package com.unistack.tamboo.sa.dd.comsumer;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @author anning
 * @date 2018/6/8 上午10:56
 * @description:
 */
public class JdbcThreadPool {
    public static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

    public static JSONObject startJdbcSink() {


        for (int i = 0; i < 10; i++)
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
//                    KafkaConsumer<String, String> consumer = getConsumer();
//                    ConsumerRecords<String, String> poll = consumer.poll(1000);
//                    System.out.println(poll.count());
//                    System.out.println(Thread.currentThread().toString());
                    System.out.println(111);
                }
            });


        return null;
    }

    public static KafkaConsumer<String, String> getConsumer(){
        String brokerList = "localhost:9092";
        String group_id = "groupId";
        String topic = "testjdbc";
        Properties props = new Properties();
        props.put("bootstrap.servers", brokerList);
        props.put("group.id", group_id);
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList(topic));
        return consumer;
    }

    public static void main(String[] args) {
        startJdbcSink();
//        stopThreadPoo();
    }

    public static void stopThreadPoo(){
        fixedThreadPool.shutdownNow();
    }

}
