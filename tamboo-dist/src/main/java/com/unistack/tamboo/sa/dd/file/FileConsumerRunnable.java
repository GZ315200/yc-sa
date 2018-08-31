package com.unistack.tamboo.sa.dd.file;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.util.ConsumerUtils;
import com.unistack.tamboo.sa.dd.constant.DdType;
import com.unistack.tamboo.sa.dd.SinkService;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

/**
 * @author anning
 * @date 2018/6/12 下午7:46
 * @description: 下发file的线程
 */
public class FileConsumerRunnable implements Runnable {
    private static  Logger logger = LoggerFactory.getLogger(FileConsumerRunnable.class);
    private  KafkaConsumer<String, String> consumer;
    private JSONObject args;
    private String name;
    private ConsumerFileWorker worker;
    private int partitionIndex;
    private String connectName;
    private ArrayList<ConsumerRecord<String, String>> buffer = new ArrayList<>();
     int minBatchSize = 2;
    private long fetchMessageTimeL;

    public String getConnectName() {
        return connectName;
    }

    public FileConsumerRunnable(String connectorName, int partitionIndex, String brokerList, String group_id, String topic, DdType ddType, JSONObject args) {
        Properties props = ConsumerUtils.getConsumerProps(brokerList, group_id, args);

        //创建的consumer注册到JdbcService的consumer map中，key为connectName代表本次下发
        this.consumer = new KafkaConsumer<>(props);

        ArrayList<KafkaConsumer> consumerList = SinkService.consumerHashMap.get(connectorName);
        consumerList.add(consumer);

//        consumer.assign(Arrays.asList(new TopicPartition(topic,partitionIndex)));
        consumer.subscribe(Arrays.asList(topic));

        this.name = ddType.getConnectorType();
        this.args = args;
        this.partitionIndex = partitionIndex;
        this.connectName = connectorName;
    }

    @Override
    public void run() {
        try {
            while (SinkService.statusMap.get(connectName)) {
                ConsumerRecords<String, String> poll = consumer.poll(5000);
                for (ConsumerRecord<String, String> records : poll) {
                    if (StringUtils.isNotBlank(records.value())) {
                        buffer.add(records);
                    } else {
                        logger.warn("发现record为空，offset为：" + records.offset());
                    }
                    this.fetchMessageTimeL = new Date().getTime();
                }
                if (buffer.size() > 0) {
                    if (buffer.size() >= minBatchSize || new Date().getTime() - fetchMessageTimeL > 30000) {
                        JSONObject jsonObject = worker.insertInto(connectName,args, buffer);
                        consumer.commitSync();
                        buffer.clear();
                    }
                }
            }
        } finally {
            SinkService.threadHashMap.remove(connectName);
            consumer.close();
        }
    }
}
