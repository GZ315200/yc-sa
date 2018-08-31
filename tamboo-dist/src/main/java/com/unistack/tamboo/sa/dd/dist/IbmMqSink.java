package com.unistack.tamboo.sa.dd.dist;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.constant.DdType;

/**
 * @author anning
 * @date 2018/6/4 上午10:40
 * @description: 数据下发到IBM的MQ
 */
public class IbmMqSink implements KafkaSink{
    @Override
    public JSONObject checkConfig(DdType ddType, JSONObject config) {
        return null;
    }

    @Override
    public JSONObject createConfigJson(DdType ddType, JSONObject dconfig, String connectName) {
        return null;
    }

    @Override
    public JSONObject startDataSink(String connectorUrl, JSONObject config) {
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
