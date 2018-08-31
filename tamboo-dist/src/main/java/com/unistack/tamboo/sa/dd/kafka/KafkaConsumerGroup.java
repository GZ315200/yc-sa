package com.unistack.tamboo.sa.dd.kafka;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.sa.dd.ConsumerGroup;
import com.unistack.tamboo.sa.dd.SinkService;
import com.unistack.tamboo.sa.dd.constant.DdType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anning
 * @date 2018/6/25 上午9:25
 * @description: sink kafka
 */
public class KafkaConsumerGroup implements ConsumerGroup {
    private List<KafkaConsumerRunnable> consumers;
    private String connectorName;

    public KafkaConsumerGroup() {
    }

    private KafkaConsumerGroup(String connectorName, String groupId, String topic, DdType ddType, JSONObject args) {
        int tasks_max =args.getString("tasks_max")!=null?Integer.valueOf(args.getString("tasks_max")):1;
        consumers = new ArrayList<>(tasks_max);
        String brokerList = TambooConfig.KAFKA_BROKER_LIST;
//        String brokerList = "localhost:9092";
        this.connectorName = connectorName;
        SinkService.consumerHashMap.put(connectorName, new ArrayList<>());
        SinkService.threadHashMap.put(connectorName, new ArrayList<>());
        for (int i = 0; i < tasks_max; i++) {
            KafkaConsumerRunnable kafkaConsumerRunnable = new KafkaConsumerRunnable(connectorName, brokerList, groupId, topic, args);
            consumers.add(kafkaConsumerRunnable);
        }
    }

    @Override
    public ConsumerGroup initGroup(String connectorName, String groupId, String topic, DdType ddType, JSONObject args) {
        return new KafkaConsumerGroup(connectorName,groupId,topic,ddType,args);
    }

    @Override
    public void execute() {
        for (KafkaConsumerRunnable kafkaConsumerRunnable :
                consumers) {
            Thread thread1 = new Thread(kafkaConsumerRunnable);
            ArrayList<Thread> threadArrayList = SinkService.threadHashMap.get(connectorName);
            threadArrayList.add(thread1);
            thread1.start();
        }
    }
}
