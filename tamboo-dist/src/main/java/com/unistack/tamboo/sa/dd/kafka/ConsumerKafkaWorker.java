package com.unistack.tamboo.sa.dd.kafka;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.SinkWorker;
import com.unistack.tamboo.sa.dd.comsumer.ConsumerWorker;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.TopicPartition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * @author anning
 * @date 2018/6/4 上午10:56
 * @description: 数据下发到kafka
 */
public class ConsumerKafkaWorker implements SinkWorker {


    public JSONObject insertInto(Producer<String, String> kafkProducer, JSONObject json, List<ConsumerRecord<String, String>> msg,String groupId) {
        JSONObject result = new JSONObject(true);
        String sink_bootstrap_servers = json.getString("kafka_sink_servers");
        String topic = json.getString("sink_topic");
//        Producer<String,String> kafkProducer = init(sink_bootstrap_servers);
        /*for (int i = 0; i < msg.size(); i++) {
            try {
                kafkProducer.send(new ProducerRecord<>(topic, msg.get(i).value())).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                result = DdUtil.failResult("下发到kafka topic时出现异常！");
                kafkProducer.abortTransaction();
            }
        }*/
//            producerWithIndex.sendOffsetsToTransaction();

        Map<TopicPartition,OffsetAndMetadata> writtenOffsets = new HashMap<>();
        for (int i = 0; i < msg.size(); i++) {
            kafkProducer.send(new ProducerRecord<>(topic, msg.get(i).value()));
            writtenOffsets.put(new TopicPartition(topic,msg.get(i).partition()),new OffsetAndMetadata(msg.get(i).offset()+1));
        }
        kafkProducer.sendOffsetsToTransaction(writtenOffsets,groupId);

        return result;
    }

    @Override
    public JSONObject checkConfig(JSONObject json) {

        return DdUtil.succeedResult("");
    }
}
