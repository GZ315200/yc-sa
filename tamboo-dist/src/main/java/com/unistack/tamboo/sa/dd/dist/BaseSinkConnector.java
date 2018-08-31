package com.unistack.tamboo.sa.dd.dist;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.OkHttpResponse;
import com.unistack.tamboo.commons.utils.OkHttpUtils;
import com.unistack.tamboo.sa.dd.constant.DdType;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author anning
 * @date 2018/5/23 下午7:44
 * @description: base kafka sink connector
 */
public class BaseSinkConnector implements KafkaSink {

    private  Logger logger = LoggerFactory.getLogger(BaseSinkConnector.class);


    @Override
    public JSONObject checkConfig(DdType ddType, JSONObject config) {
        return null;
    }

    @Override
    public JSONObject createConfigJson(DdType ddType, JSONObject dconfig, String ss) {
        return null;
    }

    /**
     * 向集群发送Connector配置  config从 createConfigJson方法来
     *
     * @param config
     * @return
     */
    @Override
    public JSONObject startDataSink(String connectorUrl, JSONObject config) {
        JSONObject result = new JSONObject();
        String connectorName = config.getString("name");
        String startConnectorUrl = "http://" + connectorUrl + "/connectors";
//        HttpClient httpClient = new HttpClient();
//        PostMethod postMethod = new PostMethod(startConnectorUrl);
        try {
            OkHttpResponse okHttpResponse = OkHttpUtils.post(startConnectorUrl, config.toJSONString());
//            postMethod.setRequestEntity(new StringRequestEntity(config.toJSONString(), "application/json", "UTF-8"));
            int statusCode = okHttpResponse.getCode();
            switch (statusCode) {
                case HttpStatus.SC_OK:          //200
                case HttpStatus.SC_CREATED:    //201
                    result = DdUtil.succeedResult(connectorName + " 创建成功！");
                    break;
                case HttpStatus.SC_CONFLICT:   //409
                    result = DdUtil.failResult("创建connector冲突，查看connector名称是否已存在！");
                    break;
                default:
                    result = DdUtil.failResult("创建connector失败！状态码= " + statusCode);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            result = DdUtil.failResult("连接connector服务失败！");
        }
        return result;
    }

    @Override
    public JSONObject stopDataSink(String connectorUrl, String connector_name) {
        JSONObject result;
        String deleteConnectorUrl = "http://" + connectorUrl + "/connectors/" + connector_name;
//        HttpClient httpClient = new HttpClient();
//        DeleteMethod deleteMethod = new DeleteMethod(deleteConnectorUrl);
        try {
            OkHttpResponse okHttpResponse = OkHttpUtils.delete(deleteConnectorUrl);
            int statusCode = okHttpResponse.getCode();
            switch (statusCode) {
                case HttpStatus.SC_NOT_FOUND:   //404
                    result = DdUtil.failResult("未发现删除的connector!");
                    break;
                case HttpStatus.SC_NO_CONTENT:    //204
                    result = DdUtil.succeedResult("成功停止connector!");
                    break;
                default:
                    result = DdUtil.failResult("出现异常，状态码=" + statusCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = DdUtil.failResult("连接connector服务失败！");
        }
        logger.info(result.toJSONString());
        return result;
    }

    /**
     * 获得运行的connector
     *
     * @return
     */
    @Override
    public JSONObject getRunningSinkConnector(String connectorUrl) {
        JSONObject result;
        String showConnectorUrl = "http://" + connectorUrl + "/connectors";
//        HttpClient httpClient = new HttpClient();
//        GetMethod getMethod = new GetMethod(showConnectorUrl);
        try {
            OkHttpResponse okHttpResponse = OkHttpUtils.get(showConnectorUrl);
            int statusCode = okHttpResponse.getCode();
            if (statusCode != HttpStatus.SC_OK) {
                result = DdUtil.failResult("获取运行中的连接器失败！");
            } else {
                String responseBodyAsString = okHttpResponse.getBody();
                JSONArray runningConnectorArray = JSONArray.parseArray(responseBodyAsString);
                result = DdUtil.succeedResult(runningConnectorArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = DdUtil.failResult("连接connector服务失败！");
        }
        return result;
    }


    @Override
    public JSONObject getTaskStatus(String connectorUrl, String connector_name) {
        JSONObject result = new JSONObject(true);
        String taskStatusUrl = "http://" + connectorUrl + "/connectors/" + connector_name + "/tasks/0/status";
//        HttpClient httpClient = new HttpClient();
//        GetMethod getMethod = new GetMethod(taskStatusUrl);
        try {
            OkHttpResponse okHttpResponse = OkHttpUtils.get(taskStatusUrl);
            int statusCode = okHttpResponse.getCode();
            result.put("connector_name", connector_name);
            JSONObject status = JSONObject.parseObject(okHttpResponse.getBody());

            //如果task处于异常状态，trace字段值即为异常信息，但是并不能直观看出异常原因，暂时不返回
            if (status.containsKey("trace")) {
                status.remove("trace");
            }

            result.put("status", status);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn(e.getMessage());
            result = DdUtil.failResult("连接connector服务失败！");
        }

        return result;
    }
}
