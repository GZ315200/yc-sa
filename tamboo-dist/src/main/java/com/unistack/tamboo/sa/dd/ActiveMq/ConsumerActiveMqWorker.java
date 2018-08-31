package com.unistack.tamboo.sa.dd.ActiveMq;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.SinkWorker;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.List;

/**
 * @author anning
 * @date 2018/6/26 下午6:58
 * @description: activemq worker
 */
public class ConsumerActiveMqWorker implements SinkWorker{
    @Override
    public JSONObject checkConfig(JSONObject json) {

        return null;
    }

    public JSONObject insertInto(String connectName,JSONObject args, List<ConsumerRecord<String, String>> list){
        
        return null;
    }

}
