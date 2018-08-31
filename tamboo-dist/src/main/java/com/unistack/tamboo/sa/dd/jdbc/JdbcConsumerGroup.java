package com.unistack.tamboo.sa.dd.jdbc;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.sa.dd.ConsumerGroup;
import com.unistack.tamboo.sa.dd.constant.DdType;
import com.unistack.tamboo.sa.dd.SinkService;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anning
 * @date 2018/6/4 上午9:48
 * @description: 多线程消费 线程数由tasks_max指定
 */
public class JdbcConsumerGroup implements ConsumerGroup {
    private List<JdbcConsumerRunnable> consumers;
    private String groupId;
    private String connectorName;

    //用来存放connectName和下发数据库表的映射
    public static ConcurrentHashMap<String,ArrayList<String>> schemaMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String,ArrayList<String>> schemaTypeMap = new ConcurrentHashMap<>();
    public static List<String> whiteList = new ArrayList<>();
    public static String pkField ;
    public JdbcConsumerGroup() {
    }

    public JdbcConsumerGroup(String connectorName, String groupId, String topic, DdType ddType, JSONObject args) {
        int tasks_max =args.getString("tasks_max")!=null?Integer.valueOf(args.getString("tasks_max")):1;
        consumers = new ArrayList<>(tasks_max);
        String brokerList = TambooConfig.KAFKA_BROKER_LIST;
//        String brokerList = "192.168.1.193:9093";
//        String brokerList = "localhost:9092";
        this.groupId = groupId;
        this.connectorName = connectorName;
        if (StringUtils.isNotBlank(args.getString("whiteList"))) {
            whiteList = Arrays.asList(StringUtils.split(args.getString("whiteList").trim(), ","));
        }
        if (StringUtils.isNotBlank(args.getString("pk_field"))){
            pkField = args.getString("pk_field").trim();
        }
        SinkService.connectionHashMap.put(connectorName,new ArrayList<>());
        SinkService.consumerHashMap.put(connectorName,new ArrayList<>());
        SinkService.threadHashMap.put(connectorName,new ArrayList<>());
        for (int i = 0; i < tasks_max; i++) {
            JdbcConsumerRunnable consumerRunnable = new JdbcConsumerRunnable(connectorName,i, brokerList, groupId, topic, ddType, args);
            consumers.add(consumerRunnable);
        }
    }

    @Override
    public ConsumerGroup initGroup(String connectorName, String groupId, String topic, DdType ddType, JSONObject args){
        return new JdbcConsumerGroup(connectorName,groupId,topic,ddType,args);
    }

    @Override
    public void execute() {
        for (JdbcConsumerRunnable consumerRunnable :
                consumers) {
//            new Thread(consumerRunnable).start();
            Thread thread1 = new Thread(consumerRunnable);
            thread1.setName("sink-jdbc-"+new Date().getTime());
            ArrayList<Thread> threadArrayList = SinkService.threadHashMap.get(connectorName);
            threadArrayList.add(thread1);
            thread1.start();
        }
    }


    /*public JSONObject createTable(String topic){
        Properties props = new Properties();
        props.put("bootstrap.servers", brokerList);
        props.put("group.id", DdUtil.date2String(new Date()));
        props.put("enable.auto.commit", "false");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> createTableConsumer = new KafkaConsumer<>(props);
        createTableConsumer.subscribe(Arrays.asList(topic));
        ConsumerRecords<String, String> poll = createTableConsumer.poll(1000);
        if (!poll.isEmpty()){
            for (ConsumerRecord<String, String> cr:poll) {

            }
        }
        return null;
    }*/
}
