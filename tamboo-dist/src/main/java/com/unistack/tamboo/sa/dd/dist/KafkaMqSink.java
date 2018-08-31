package com.unistack.tamboo.sa.dd.dist;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.constant.DdType;
import com.unistack.tamboo.sa.dd.util.DdUtil;

import java.util.Date;

/**
 * @author anning
 * @date 2018/6/2 下午4:19
 * @description: 数据下发到另外的kafka集群
 */
public class KafkaMqSink implements KafkaSink{


    @Override
    public JSONObject checkConfig(DdType ddType, JSONObject config) {


        return DdUtil.succeedResult("");
    }

    @Override
    public JSONObject createConfigJson(DdType ddType, JSONObject dconfig, String connectorName) {

        return dconfig;
    }

    @Override
    public JSONObject startDataSink(String connectorUrl, JSONObject config) {
        String kafka_sink_servers = config.getString("kafka_sink_servers");
        int tasks_max = config.getIntValue("tasks_max");
        String topic = config.getString("topic");
        String sink_topic = config.getString("sink_topic");
        String group_id = "kafka_sink_"+DdUtil.date2String(new Date());
//        JdbcConsumerGroup consumerGroup = new JdbcConsumerGroup(tasks_max, group_id, topic, "kafka",ddType,config);
//        consumerGroup.execute();

        return null;
    }

    @Override
    public JSONObject stopDataSink(String connectorUrl, String connector_name) {
        return null;
    }

    @Override
    public JSONObject getRunningSinkConnector(String connectorUrl) {
        return null;
    }

    @Override
    public JSONObject getTaskStatus(String connectorUrl, String connector_name) {
        return null;
    }


}
