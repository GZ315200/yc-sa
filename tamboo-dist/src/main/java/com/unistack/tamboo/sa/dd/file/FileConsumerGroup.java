package com.unistack.tamboo.sa.dd.file;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.sa.dd.ConsumerGroup;
import com.unistack.tamboo.sa.dd.constant.DdType;
import com.unistack.tamboo.sa.dd.SinkService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anning
 * @date 2018/6/12 下午7:37
 * @description: 下发文件
 */
public class FileConsumerGroup implements ConsumerGroup {
    private List<FileConsumerRunnable> consumers;
    private String connectorName;

    public FileConsumerGroup() {
    }

    public FileConsumerGroup(String connectorName, String groupId, String topic, DdType ddType, JSONObject args) {
        int tasks_max =args.getString("tasks_max")!=null?Integer.valueOf(args.getString("tasks_max")):1;
        consumers = new ArrayList<>(tasks_max);
        String brokerList = TambooConfig.KAFKA_BROKER_LIST;
//        String brokerList = "localhost:9092";
        this.connectorName = connectorName;
        SinkService.consumerHashMap.put(connectorName,new ArrayList<>());
        SinkService.threadHashMap.put(connectorName,new ArrayList<>());
        for (int i = 0; i < tasks_max; i++) {
            FileConsumerRunnable fileConsumerRunnable = new FileConsumerRunnable(connectorName,i, brokerList, groupId, topic, ddType, args);
            consumers.add(fileConsumerRunnable);
        }
    }

    @Override
    public ConsumerGroup initGroup(String connectorName, String groupId, String topic, DdType ddType, JSONObject args) {
        return new FileConsumerGroup(connectorName,groupId,topic,ddType,args);
    }

    @Override
    public void execute() {
        for (FileConsumerRunnable fileConsumerRunnable :
                consumers) {
            Thread thread1 = new Thread(fileConsumerRunnable);
            ArrayList<Thread> threadArrayList = SinkService.threadHashMap.get(connectorName);
            threadArrayList.add(thread1);
            thread1.start();
        }
    }
}
