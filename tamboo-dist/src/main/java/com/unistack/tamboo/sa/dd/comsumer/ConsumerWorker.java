package com.unistack.tamboo.sa.dd.comsumer;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

/**
 * @author anning
 * @date 2018/6/4 上午11:06
 * @description: 数据下发接口
 */
public interface ConsumerWorker {
    JSONObject checkWorkerConfig(JSONObject json);
    JSONObject send(int partitionNum,JSONObject json,List<ConsumerRecord<String,String>> msg);
    JSONObject close(JSONObject json);
}
