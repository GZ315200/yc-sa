package com.unistack.tamboo.sa.dd2.worker;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.sa.SinkUtils;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import com.unistack.tamboo.sa.dd2.KafkaSinkWorker;
import com.unistack.tamboo.sa.dd2.constant.Constants;
import org.apache.commons.lang3.StringUtils;

import static com.unistack.tamboo.sa.dd2.constant.ConnectorConfig.*;
import static com.unistack.tamboo.sa.dd2.constant.Constants.IS_SUCCEED;

/**
 * @author anning
 * @date 2018/7/20 下午5:42
 * @description: jdbc sink worker
 */
public class JdbcWorker implements KafkaSinkWorker{
    @Override
    public JSONObject checkConfig(JSONObject fields) {
        JSONObject connectCheck = SinkUtils.checkAllConnectUrl();
        if (!connectCheck.getBoolean(IS_SUCCEED)){
            return connectCheck;
        }

        JSONObject registryCheck = SinkUtils.checkRegistryUrl();
        if (!registryCheck.getBoolean(IS_SUCCEED)){
            return registryCheck;
        }
        return SinkUtils.checkConfig(fields);
    }

    @Override
    public JSONObject createConfigJson(JSONObject fields) {
        try {
            String jdbcUrl = fields.getString("jdbc_url");
            String tableName = fields.getString("table_name");
            String userName = fields.getString("user_name");
            String userPwd = fields.getString("user_pwd");
            String pkField = fields.getString("pk_field");
            String whiteList = fields.getString("whiteList");
            String connectName = fields.getString("connectName");
            String topicName = fields.getString("topic_name");
            Integer tasks_max = Integer.valueOf(fields.getOrDefault("tasks_max", "1").toString());
            String registryUrl = SinkUtils.makeHttpPre(TambooConfig.KAFKA_REGISTRY_URL);
            JSONObject connectorMeta = new JSONObject(true);
            connectorMeta.put(CONNECT_NAME, connectName);
            JSONObject config = new JSONObject(true);
            config.fluentPut(CONNECT_CLASS, Constants.ConnectorClass.JDBC_SINK_CONNECTOR.cClazz)
                    .fluentPut(CONNECT_URL, jdbcUrl)
                    .fluentPut(CONNECT_USER, userName)
                    .fluentPut(CONNECT_PASSWORD, userPwd)
                    .fluentPut(TOPICS, topicName)
                    .fluentPut(TASKS_MAX, tasks_max)
                    .fluentPut(TABLE_NAME_FORMAT, tableName)
                    .fluentPut(AUTO_CREATE, true)
                    .fluentPut(AUTO_EVOLVE, true)
                    .fluentPut(KEY_CONVERTER_SCHEMAS_ENABLE, true)
                    .fluentPut(VALUE_CONVERTER_SCHEMAS_ENABLE, true)
                    .fluentPut(KEY_SCHEMA_REGISTRY_URL, registryUrl)
                    .fluentPut(VALUE_SCHEMA_REGISTRY_URL, registryUrl)
                    .fluentPut(KEY_CONVERTER, Constants.ConverterType.AVRO.converter)
                    .fluentPut(VALUE_CONVERTER, Constants.ConverterType.AVRO.converter);
            if (StringUtils.isNotBlank(pkField)) {
                config.fluentPut(PK_MODE, Constants.Pk_mode.RECORD_VALUE.mode)
                        .fluentPut(PK_FIELDS, pkField)
                        .fluentPut(INSERT_MODE, Constants.InsertMode.UPSERT.mode);
            }

            if (StringUtils.isNotBlank(whiteList)) {
                config.put(WHITE_LIST, whiteList);
            }

            connectorMeta.put(CONNECT_CONFIG, config);
            return DdUtil.succeedResult(connectorMeta);
        } catch (Exception e) {
            return DdUtil.failResult(e.toString());
        }
    }
}
