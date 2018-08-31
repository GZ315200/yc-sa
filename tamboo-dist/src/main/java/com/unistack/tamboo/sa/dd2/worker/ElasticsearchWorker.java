package com.unistack.tamboo.sa.dd2.worker;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.OkHttpResponse;
import com.unistack.tamboo.commons.utils.OkHttpUtils;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.sa.SinkUtils;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import com.unistack.tamboo.sa.dd2.KafkaSinkWorker;
import com.unistack.tamboo.sa.dd2.constant.Constants;
import org.apache.commons.httpclient.HttpStatus;

import java.util.Arrays;

import static com.unistack.tamboo.sa.dd2.constant.ConnectorConfig.*;

/**
 * @author anning
 * @date 2018/7/20 下午5:40
 * @description: es worker
 */
public class ElasticsearchWorker implements KafkaSinkWorker{
    @Override
    public JSONObject checkConfig(JSONObject json) {
        JSONObject connectCheck = SinkUtils.checkAllConnectUrl();
        if (!connectCheck.getBoolean(Constants.IS_SUCCEED)){
            return connectCheck;
        }

        JSONObject registryCheck = SinkUtils.checkRegistryUrl();
        if (!registryCheck.getBoolean(Constants.IS_SUCCEED)){
            return registryCheck;
        }

        String connectionUrl = json.getJSONObject("fields").getString("connection_url");
        String[] esUrlArr = connectionUrl.split(",");
        boolean b = Arrays.stream(esUrlArr).allMatch(url -> {
            if (!url.startsWith(Constants.HTTP_PRE)&&!url.startsWith(Constants.HTTP_PRE)) {
                url = Constants.HTTP_PRE+url;
            }
            OkHttpResponse okHttpResponse = OkHttpUtils.get(url);
            int code = okHttpResponse.getCode();
            return code == HttpStatus.SC_OK;
        });
        if (!b) {
            return DdUtil.failResult("es的http访问url不可用!");
        }
        return DdUtil.succeedResult("OK");
    }

    @Override
    public JSONObject createConfigJson(JSONObject fields) {
        String connectName = fields.getString("connectName");
        String topicName = fields.getString("topic_name");
        String schemaIgnore = fields.getString("schema_ignore");
        String keyIgnore = fields.getString("key_ignore");
        String connectionUrl = fields.getString("connection_url");
        String typeName = fields.getString("type_name");
        Integer tasks_max = Integer.valueOf(fields.getOrDefault("tasks_max", "1").toString());
        String registryUrl = SinkUtils.makeHttpPre(TambooConfig.KAFKA_REGISTRY_URL);
        JSONObject connectorMeta = new JSONObject(true);
        connectorMeta.put(CONNECT_NAME, connectName);
        JSONObject config = new JSONObject(true);
        config.fluentPut(CONNECT_CLASS, Constants.ConnectorClass.ES_SINK_CONNECTOR.cClazz)
                .fluentPut(TASKS_MAX,tasks_max)
                .fluentPut(TOPICS,topicName)
                .fluentPut(KEY_IGNORE,keyIgnore)
                .fluentPut(ES_CONNECTION_URL,connectionUrl)
                .fluentPut(TYPE_NAME,typeName)
                .fluentPut(SCHEMA_IGNORE,schemaIgnore)
                .fluentPut(KEY_SCHEMA_REGISTRY_URL,registryUrl)
                .fluentPut(VALUE_SCHEMA_REGISTRY_URL,registryUrl)
                .fluentPut(KEY_CONVERTER,Constants.ConverterType.AVRO.converter)
                .fluentPut(VALUE_CONVERTER,Constants.ConverterType.AVRO.converter);
        connectorMeta.put(CONNECT_CONFIG,config);
        return connectorMeta;
    }
}
