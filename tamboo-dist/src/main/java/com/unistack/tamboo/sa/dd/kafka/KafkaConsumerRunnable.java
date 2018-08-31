package com.unistack.tamboo.sa.dd.kafka;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.unistack.tamboo.sa.dd.SinkService;
import com.unistack.tamboo.sa.dd.constant.DdType;
import com.unistack.tamboo.sa.dd.file.ConsumerFileWorker;
import com.unistack.tamboo.sa.dd.file.FileConsumerRunnable;
import com.unistack.tamboo.sa.dd.util.ConsumerUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author anning
 * @date 2018/6/25 上午9:30
 * @description: kafka sink runnable
 */
public class KafkaConsumerRunnable implements Runnable{

    private static  Logger logger = LoggerFactory.getLogger(KafkaConsumerRunnable.class);
    private  KafkaConsumer<String, String> consumer;
    private JSONObject args;
    private ConsumerKafkaWorker worker;
    private String connectName;
    private ArrayList<ConsumerRecord<String, String>> buffer = new ArrayList<>();
    private  int minBatchSize = 2;
    private long fetchMessageTimeL;
    private static Producer<String, String> kafkProducer;
    private static String groupId ;
    private Map<TopicPartition,OffsetAndMetadata> writtenOffsets = new HashMap<>();

    public String getConnectName() {
        return connectName;
    }

    public KafkaConsumerRunnable(String connectorName, String brokerList, String group_id, String topic, JSONObject args) {
        Properties props = ConsumerUtils.getConsumerProps(brokerList, group_id, args);

        //创建的consumer注册到JdbcService的consumer map中，key为connectName代表本次下发
        this.consumer = new KafkaConsumer<>(props);

        ArrayList<KafkaConsumer> consumerList = SinkService.consumerHashMap.get(connectorName);
        consumerList.add(consumer);

//        consumer.assign(Arrays.asList(new TopicPartition(topic,partitionIndex)));
        consumer.subscribe(Arrays.asList(topic));
        this.args = args;
        String kafka_sink_servers = args.getString("kafka_sink_servers");
        if (kafkProducer==null) {  //多线程状态下同用一个producer，暂没考虑多个kafka下发的情况
            kafkProducer = init(kafka_sink_servers);
            //添加事务
            //初始化事务 保证新的事务在一个正确状态下启动
            kafkProducer.initTransactions();
        }
        this.connectName = connectorName;
        groupId = group_id;
    }

    @Override
    public void run() {
        try {
            while (SinkService.statusMap.get(connectName)) {
                ConsumerRecords<String, String> poll = consumer.poll(5000);

                //开始事务
                kafkProducer.beginTransaction();

                for (ConsumerRecord<String, String> records : poll) {
                    if (StringUtils.isNotBlank(records.value())) {
                        buffer.add(records);
                    } else {
                        logger.warn("kafka record为空，offset为：" + records.offset());
                    }
                    this.fetchMessageTimeL = new Date().getTime();
                }
                if (buffer.size() > 0) {
                    if (buffer.size() >= minBatchSize || new Date().getTime() - fetchMessageTimeL > 30000) {
//                        worker.insertInto(kafkProducer,args, buffer,groupId);
                        String topic = args.getString("sink_topic");

                        for (int i = 0; i < buffer.size(); i++) {
                            ConsumerRecord<String, String> record = buffer.get(i);
                            kafkProducer.send(new ProducerRecord<>(topic, record.value()));
                            writtenOffsets.put(new TopicPartition(topic, record.partition()),new OffsetAndMetadata(record.offset()+1));
                        }
                        kafkProducer.sendOffsetsToTransaction(writtenOffsets,groupId);
//                        consumer.commitSync();
                        kafkProducer.commitTransaction();
                        buffer.clear();
                    }
                }
            }
        } finally {
            SinkService.threadHashMap.remove(connectName);
            consumer.close();
            kafkProducer.close();
        }
    }

    private Producer<String,String> init(String kafka_servers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", kafka_servers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("acks", "1");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put("batch.size", 323840);     //数据量达到 批量发送
        props.put(ProducerConfig.LINGER_MS_CONFIG, 100);           //时间间隔 批量发送
        props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, 104857960);  //发送的每条消息大小上限
//        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);   //幂等
        props.put("transactional.id",connectName);                      //保证事务id的稳定性，并且支持重启
        return new KafkaProducer<>(props);
    }
}
