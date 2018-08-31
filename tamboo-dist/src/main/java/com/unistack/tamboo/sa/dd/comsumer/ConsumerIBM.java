package com.unistack.tamboo.sa.dd.comsumer;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

/**
 * @author anning
 * @date 2018/6/4 上午10:59
 * @description: 数据下发到ibm
 */
public class ConsumerIBM implements ConsumerWorker{



    @Override
    public JSONObject checkWorkerConfig(JSONObject json) {
        return null;
    }

    @Override
    public JSONObject send(int partitionNum, JSONObject json, List<ConsumerRecord<String, String>> msg) {
        return null;
    }


    @Override
    public JSONObject close(JSONObject json) {
        return null;
    }



}
