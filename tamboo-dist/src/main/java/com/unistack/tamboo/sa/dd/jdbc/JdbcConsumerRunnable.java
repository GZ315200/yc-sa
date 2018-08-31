package com.unistack.tamboo.sa.dd.jdbc;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.util.ConsumerUtils;
import com.unistack.tamboo.sa.dd.constant.DdType;
import com.unistack.tamboo.sa.dd.invoking.DdInvoking;
import com.unistack.tamboo.sa.dd.SinkService;
import com.unistack.tamboo.sa.dd.util.JdbcUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author anning
 * @date 2018/5/26 下午4:16
 * @description: kafka runnable
 */
public class JdbcConsumerRunnable implements Runnable {
    private static  Logger logger = LoggerFactory.getLogger(JdbcConsumerRunnable.class);
    private  KafkaConsumer<String, String> consumer;
    private  AtomicBoolean closed = new AtomicBoolean(false);
    private static Lock lock = new ReentrantLock();

    private String name;
    private JdbcSinkWorker worker;
     int minBatchSize = 50;
    int partitionIndex;
    private JSONObject args;   //sink kafka 各种参数 包括sasl信息等
    private DdType ddType;
    public Connection connection;
    private String connectName;

    private boolean status = true;         //当前下发的状态
//    private ArrayList<ConsumerRecord<String, String>> buffer = new ArrayList<>();
    private ArrayList<JSONObject> buffer =new ArrayList<>();
    private long fetchMessageTimeL;

    private JSONObject srcJson;
//    private JSONObject distJson = new JSONObject();

    public JdbcConsumerRunnable(String connectorName, int partitionIndex, String brokerList, String group_id, String topic, DdType ddType, JSONObject args) {
        Properties props = ConsumerUtils.getConsumerProps(brokerList, group_id, args);

        JSONObject getConnectionResult = JdbcUtils.getConnection(ddType, args);
        if (getConnectionResult.getBoolean("isSucceed")) {
            this.connection = (Connection) getConnectionResult.get("msg");
        } else {
            logger.error(getConnectionResult.getString("msg"));
            SinkService.stopSink(connectorName);
        }
        //创建的connection注册到JdbcService的connection Map中,key为connectName代表本次下发
        ArrayList<Connection> connectionArrayList = SinkService.connectionHashMap.get(connectorName);
        connectionArrayList.add(this.connection);

        //创建的consumer注册到JdbcService的consumer map中，key为connectName代表本次下发
        this.consumer = new KafkaConsumer<String, String>(props);

        ArrayList<KafkaConsumer> consumerList = SinkService.consumerHashMap.get(connectorName);
        consumerList.add(consumer);

//        consumer.assign(Arrays.asList(new TopicPartition(topic,partitionIndex)));
        consumer.subscribe(Arrays.asList(topic));
        this.name = ddType.getConnectorType();
        worker = DdInvoking.getJdbcSinkWorkerByname(name);
        this.args = args;
        this.partitionIndex = partitionIndex;
        this.connectName = connectorName;

        this.fetchMessageTimeL = new Date().getTime();

    }


    @Override
    public void run() {
        try {
            while (SinkService.statusMap.get(connectName)) {
                ConsumerRecords<String, String> poll = consumer.poll(5000);
                for (ConsumerRecord<String, String> record :
                        poll) {
                    if (StringUtils.isNotBlank(record.value())) {
                        srcJson = JSONObject.parseObject(record.value());
                        JSONObject distJson = new JSONObject();
                        JdbcUtils.flatJsonFirst(srcJson,"_",distJson);
                        buffer.add(distJson);
                    } else {
                        logger.warn("record为空，offset为：" + record.offset());
                    }
//                    System.out.println(Thread.currentThread().getName()+"==========>"+record.toString());
                    this.fetchMessageTimeL = new Date().getTime();
                }

                if (buffer.size() > 0) {
                    if (buffer.size() >= minBatchSize || new Date().getTime() - fetchMessageTimeL > 30000) {
                        //如果没有存储数据库表元数据信息，则加载表信息  这部分加锁
                        if (JdbcConsumerGroup.schemaMap.get(connectName) == null) {
                            String table_name = args.getString("table_name");
//                            JSONObject oneRecord = JSONObject.parseObject(buffer.get(0).value());
                            JSONObject oneRecord = buffer.get(0);
                            lock.lock();
                            try {
                                //处理whiteList
                                if (JdbcConsumerGroup.whiteList.size() > 0) {
                                    JSONObject whiteJson = new JSONObject();
                                    for (String key : oneRecord.keySet()) {
                                        if (JdbcConsumerGroup.whiteList.contains(key)) {
                                            whiteJson.put(key, oneRecord.get(key));
                                        }
                                    }
                                    oneRecord = whiteJson;
                                }
                                boolean getOrCreateTable = worker.getOrCreateTable(connectName, connection, table_name, oneRecord);
                                //如果创建表或者获得表元数据失败 停止下发
                                if (!getOrCreateTable) {
                                    JdbcConsumerGroup.schemaMap.remove(connectName);
                                    SinkService.stopSink(connectName);
                                }
                            } finally {
                                lock.unlock();
                            }
                        }
                        worker.insertInto(connectName, connection, args, buffer);
//                    if (insertResult.getBoolean("isSucceed")) {
                        consumer.commitSync();
//                    } else {
//                        status = false;    //下发状态
//                        SinkService.isRunning = false;
//                    }
                        buffer.clear();
                        this.fetchMessageTimeL = new Date().getTime();
                    }
                }
            }
        } /*catch (Exception e) {
//            Thread.currentThread().interrupt();
        } */ finally {
            SinkService.threadHashMap.remove(connectName);
            consumer.commitAsync();
            consumer.close();
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                logger.error("关闭数据库连接失败：======>" + e.toString());
            }
        }
    }
}
