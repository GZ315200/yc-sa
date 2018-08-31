package com.unistack.tamboo.sa.dd2.worker;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import com.unistack.tamboo.sa.dd2.KafkaSinkWorker;
import com.unistack.tamboo.sa.dd2.constant.Constants;

import static com.unistack.tamboo.sa.dd2.constant.ConnectorConfig.*;
import static com.unistack.tamboo.sa.dd2.constant.ConnectorConfig.TASKS_MAX;

/**
 * @author anning
 * @date 2018/7/20 下午5:40
 * @description: file worker
 */
public class FileWorker implements KafkaSinkWorker{
    @Override
    public JSONObject checkConfig(JSONObject var1) {
        return DdUtil.succeedResult("OK");
    }

    @Override
    public JSONObject createConfigJson(JSONObject json) {
        String connectName = json.getString("connectName");
        String topicName = json.getString("topic_name");
        String typeName = json.getString("type_name");
        String file = json.getString("file");
        Integer tasks_max = Integer.valueOf(json.getOrDefault("tasks_max", "1").toString());
        JSONObject connectorMeta = new JSONObject(true);
        connectorMeta.put(CONNECT_NAME, connectName);
        JSONObject config = new JSONObject(true);
        config.fluentPut(CONNECT_CLASS, Constants.ConnectorClass.FILE_SINK_CONNECTOR)
                .fluentPut(FILE,file)
                .fluentPut(TOPICS,topicName)
                .fluentPut(TASKS_MAX,tasks_max);
        connectorMeta.put("config",config);
        return connectorMeta;
    }
}
