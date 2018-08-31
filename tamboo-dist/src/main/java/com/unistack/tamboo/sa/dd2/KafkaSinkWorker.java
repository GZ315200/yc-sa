package com.unistack.tamboo.sa.dd2;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.OkHttpResponse;
import com.unistack.tamboo.commons.utils.OkHttpUtils;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author anning
 * @date 2018/7/20 下午5:34
 * @description: kafka sink worker接口
 */
public interface KafkaSinkWorker {
    public static final Logger logger = LoggerFactory.getLogger(KafkaSinkWorker.class);


    String START_CONNECTOR_URL = "%s/connectors";
    String DELETE_CONNECTOR_URL = "%s/connectors/%s";
    String SHOW_CONNECTOR_URL = "%s/connectors";
    String TASK_STATUS_URL = "%s/connectors/%s/tasks/0/status";
    String PAUSE_SINK_URL = "%s/connectors/%s/pause";
    String RESUME_SINK_URL = "%s/connectors/%s/resume";
    String RESTART_SINK_URL = "%s/connectors/%s/restart";

    JSONObject checkConfig(JSONObject var1);

    JSONObject createConfigJson(JSONObject var1);

    default JSONObject startDataSink(String connectorUrl, JSONObject config) {
        new JSONObject();
        String connectorName = config.getString("name");
        String format = String.format(START_CONNECTOR_URL, connectorUrl);
        logger.info("=============== url===>" + format);
        JSONObject result;
        try {
            OkHttpResponse okHttpResponse = OkHttpUtils.post(format, config.toJSONString());
            System.out.println("返回信息 =====> " + okHttpResponse.getBody());
            int statusCode = okHttpResponse.getCode();
            switch (statusCode) {
                case HttpStatus.SC_OK:
                case HttpStatus.SC_CREATED:
                    result = DdUtil.succeedResult(connectorName + " 创建成功！");
                    break;
                case HttpStatus.SC_CONFLICT:
                    result = DdUtil.failResult("创建connector冲突，查看connector名称是否已存在！");
                    break;
                default:
                    result = DdUtil.failResult("创建connector失败！状态码= " + statusCode);
            }
        } catch (UnsupportedEncodingException e) {
            logger.error("start sink connector error ========>" + e.toString());
            result = DdUtil.failResult("不支持的编码类型！");
        } catch (IOException e) {
            logger.error("start sink connector error ========>" + e.toString());
            result = DdUtil.failResult("连接connector服务失败！");
        }

        return result;
    }

    /**
     * 停止
     *
     * @param connectorUrl
     * @param connectorName
     * @return
     */
    static JSONObject stopDataSink(String connectorUrl, String connectorName) {
        String format = String.format(DELETE_CONNECTOR_URL, connectorUrl, connectorName);

        JSONObject result;
        try {
            OkHttpResponse okHttpResponse = OkHttpUtils.delete(format);
            int statusCode = okHttpResponse.getCode();
            switch (statusCode) {
                case HttpStatus.SC_NO_CONTENT:
                    result = DdUtil.succeedResult("成功停止connector!");
                    break;
                case HttpStatus.SC_NOT_FOUND:
                    result = DdUtil.failResult("未发现删除的connector!");
                    break;
                default:
                    result = DdUtil.failResult("出现异常，状态码=" + statusCode);
            }
        } catch (IOException e) {
            logger.error("stop connector:" + connectorName + "========>" + e.toString());
            result = DdUtil.failResult("连接connector服务失败！");
        }

        logger.info(result.toJSONString());
        return result;
    }


    static JSONObject getRunningSinkConnector(String connectorUrl) {

        String format = String.format(SHOW_CONNECTOR_URL, connectorUrl);
        JSONObject result;
        try {
            OkHttpResponse okHttpResponse = OkHttpUtils.get(format);
            int statusCode = okHttpResponse.getCode();
            if (statusCode != HttpStatus.SC_OK) {
                result = DdUtil.failResult("获取运行中的连接器失败！");
            } else {
                String responseBodyAsString = okHttpResponse.getBody();
                JSONArray runningConnectorArray = JSONArray.parseArray(responseBodyAsString);
                result = DdUtil.succeedResult(runningConnectorArray);
            }
        } catch (Exception e) {
            logger.error("get running connector,conenct url:" + connectorUrl + "========>" + e.toString());
            result = DdUtil.failResult("连接connector服务失败！");
        }

        return result;
    }

    static JSONObject getTaskStatus(String connectorUrl, String connectorName) {
        String format = String.format(TASK_STATUS_URL, connectorUrl, connectorName);

        try {
            OkHttpResponse okHttpResponse = OkHttpUtils.get(format);
            int statusCode = okHttpResponse.getCode();
            JSONObject status = JSONObject.parseObject(okHttpResponse.getBody());
            /*if (status.containsKey("trace")) {
                status.remove("trace");
            }*/
            status.put("connectName", connectorName);
            return DdUtil.succeedResult(status);
        } catch (Exception e) {
            logger.error(e.toString());
            return DdUtil.failResult("连接connector服务失败！");
        }
    }

    static JSONObject pauseSink(String connectorUrl, String connectName) {
        String format = String.format(PAUSE_SINK_URL, connectorUrl, connectName);

        try {
            OkHttpResponse put = OkHttpUtils.put(format);
            int statusCode = put.getCode();
            return statusCode != HttpStatus.SC_ACCEPTED ? DdUtil.failResult(connectName + " 暂停失败 " + put.getBody()) : DdUtil.succeedResult(connectName);
        } catch (Exception e) {
            logger.error(e.toString());
            return DdUtil.failResult("连接connector服务失败！");
        }
    }

    static JSONObject resumeSink(String connectorUrl, String connectName) {
        String format = String.format(RESUME_SINK_URL, connectorUrl, connectName);

        try {
            OkHttpResponse put = OkHttpUtils.put(format);
            int statusCode = put.getCode();
            return statusCode != HttpStatus.SC_ACCEPTED ? DdUtil.failResult(connectName + " 恢复失败 " + put.getBody()) : DdUtil.succeedResult(connectName);
        } catch (Exception e) {
            logger.error(e.toString());
            return DdUtil.failResult("连接connector服务失败！");
        }
    }

    static JSONObject restartSink(String connectorUrl, String connectName) {
        String format = String.format(RESTART_SINK_URL, connectorUrl, connectName);

        try {
            OkHttpResponse post = OkHttpUtils.post(format, (new JSONObject()).toJSONString());
            int code = post.getCode();
            return code != HttpStatus.SC_NO_CONTENT ? DdUtil.failResult(connectName + " 重启失败 " + post.getBody()) : DdUtil.succeedResult(connectName);
        } catch (Exception e) {
            logger.error(e.toString());
            return DdUtil.failResult("连接connector服务失败！");
        }
    }
}
