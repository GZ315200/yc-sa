package com.unistack.tamboo.sa.dd.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.constant.DdType;
import com.unistack.tamboo.sa.dd.dist.BaseSinkConnector;
import com.unistack.tamboo.sa.dd.dist.KafkaSink;
import com.unistack.tamboo.sa.dd.invoking.DdInvoking;
import com.unistack.tamboo.sa.dd.util.DdUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.unistack.tamboo.sa.dd.util.DdUtil.date2String;

/**
 * @author anning
 * @date 2018/5/23 下午4:04
 * @description: 提供接口
 */
public class DdService {

    private BaseSinkConnector baseSinkConnector = new BaseSinkConnector();

    /**
     * @param args json {"type":"mysql/oracle/hdfs","config":{"k1":"v1","k2":"v2"...}}
     * @param connectorUrl kafka ip:port
     * @return json {"isSucceed":boolean,"msg":msg,"connectorName":name}
     */
    public JSONObject startKafkaSinkConnector(JSONObject args, String connectorUrl) {
        JSONObject result;
        String connectName = "sink-" + date2String(new Date());
        String type = args.getString("type");
        DdType ddTypeByName = DdType.getDdTypeByName(type);
        KafkaSink kafkaSink = DdInvoking.getConnetorInstance(type);
        JSONObject config = args.getJSONObject("fields");
        JSONObject checkResult = kafkaSink.checkConfig(ddTypeByName, config);
        if (checkResult.getBoolean("isSucceed")) {
            JSONObject configJson = kafkaSink.createConfigJson(ddTypeByName, config, connectName);
            result = kafkaSink.startDataSink(connectorUrl, configJson);
            result.put("connectorName", connectName);
            result.put("group_id","connect-"+connectName);
            result.put("topic",config.getString("topics"));
        } else {
            result = checkResult;
        }
        return result;
    }


    /**
     * @param connectorUrl kafka server ip:port
     * @return json {"isSucceed":boolean,"msg":running_connector_array}
     */
    public JSONObject showRunningConnector(String connectorUrl){
        return baseSinkConnector.getRunningSinkConnector(connectorUrl);
    }

    /**
     * stop delete connector
     *
     * @param connectorUrl kafka server ip:port
     * @param name         stop connector name
     * @return json {"isSucceed":boolean,"msg":msg}
     */
    public JSONObject stopKafkaSinkConnectorByName(String name, String connectorUrl) {
        /*BaseSinkConnector baseSinkConnector = new BaseSinkConnector();
        JSONObject runningSinkConnector = baseSinkConnector.getRunningSinkConnector(kafkaIp, kafkaConnectPort);
        JSONArray msg = runningSinkConnector.getJSONArray("msg");
        for (Object runningConnector:
             msg) {
        }*/
        return baseSinkConnector.stopDataSink(connectorUrl, name);
    }

    /**
     * 停止所有运行中的connector    后面设置为只停止数据下发模块的connector
     * @param connectorUrl kafka server ip:port
     * @return
     */
    public JSONObject stopAllConnector(String connectorUrl) {
        JSONObject result;
        JSONObject runningSinkConnector = baseSinkConnector.getRunningSinkConnector(connectorUrl);
        List<String> fail_connector = new ArrayList<>();
        if (runningSinkConnector.getBoolean("isSucceed")) {
            JSONArray msg = runningSinkConnector.getJSONArray("msg");
            if (msg.size() == 0) {
                result = DdUtil.failResult("未发现运行中的connector!");
            } else {
                for (Object connectorName :
                        msg) {
                    String connectorName1 = (String) connectorName;
                    JSONObject stopResult = stopKafkaSinkConnectorByName(connectorName1, connectorUrl);
                    if (!stopResult.getBoolean("isSucceed")) {
                        fail_connector.add(connectorName1);
                    }
                }
                if (fail_connector.size() > 0) {
                    result = DdUtil.failResult("部分连接器停止失败：" + fail_connector);
                } else {
                    result = DdUtil.succeedResult("所有连接器都已关闭！");
                }
            }
        } else {
            result = runningSinkConnector;
        }
        return result;
    }


    /**
     * 获得集群内所有connector的运行状态信息
     *
     * @param connectorUrl
     * @return
     */
    public JSONObject getTaskStatus(String connectorUrl) {
        JSONObject result;
        JSONArray status_list = new JSONArray();
        JSONObject runningSinkConnector = baseSinkConnector.getRunningSinkConnector(connectorUrl);
        if (runningSinkConnector.getBoolean("isSucceed")) {
            JSONArray msg = runningSinkConnector.getJSONArray("msg");
            if (msg.size() == 0) {
                result = DdUtil.succeedResult("[]");
            } else {
                for (Object connectorName :
                        msg) {
                    String connectorName1 = (String) connectorName;
                    JSONObject taskStatus = baseSinkConnector.getTaskStatus(connectorUrl, connectorName1);
                    status_list.add(taskStatus);
                }
                result = DdUtil.succeedResult(status_list);
            }
        } else {
            result = runningSinkConnector;
        }
        return result;
    }

    /**
     * 通过connector名称来查询状态
     *
     * @param connectorUrl kafka ip:port
     * @param name         查询的connetor名称
     * @return
     */
    public JSONObject getTaskStatusByConnectorName(String connectorUrl, String name) {
        return baseSinkConnector.getTaskStatus(connectorUrl, name);
    }


}

