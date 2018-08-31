package com.unistack.tamboo.sa.dd.dist;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.constant.DdType;

/**
 * @author anning
 * @date 2018/5/22 上午11:10
 */
public interface KafkaSink {

    /**
     * check config
     *
     * @param config
     * @return
     */
    JSONObject checkConfig(DdType ddType, JSONObject config);

    /**
     * 生成发送的参数 JSON格式
     *
     * @param ddType,dconfig
     * @return
     */
    JSONObject createConfigJson(DdType ddType, JSONObject dconfig, String connectName);

    /**
     * 启动Connector 即POST config
     *
     * @param config
     * @return
     */
    JSONObject startDataSink(String connectorUrl, JSONObject config);

    /**
     * 停止Connector 删除 DELETE
     *
     * @return
     */
    JSONObject stopDataSink(String connectorUrl, String connector_name);

    /**
     * 获得KAFKA集群中正在运行的Connector
     *
     * @return
     */
    JSONObject getRunningSinkConnector(String connectorUrl);

    /**
     * 获得connector的task状态，监控用
     * @param connectorUrl
     * @param connector_name
     * @return
     */
    JSONObject getTaskStatus(String connectorUrl, String connector_name);

}
