package com.unistack.tamboo.sa.dd2;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.errors.GeneralServiceException;
import com.unistack.tamboo.sa.SinkUtils;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import com.unistack.tamboo.sa.dd2.worker.WorkerInvoking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import static com.unistack.tamboo.sa.dd2.constant.Constants.IS_SUCCEED;
import static java.util.stream.Collectors.toCollection;

/**
 * @author anning
 * @date 2018/7/20 下午5:22
 * @description: service
 */
public class ConnectSinkService {

    public static final Logger log = LoggerFactory.getLogger(ConnectSinkService.class);

    private static String connectUrl;

    static {
        connectUrl = SinkUtils.getFirstConnectUrl();
//        connectUrl = "http://192.168.0.110:8083";
    }

    public static JSONObject checkConfig(JSONObject args) {
        String typeName = args.getString("type");
        KafkaSinkWorker sinkWorkerByTypeName = WorkerInvoking.getSinkWorkerByTypeName(typeName);
        return sinkWorkerByTypeName.checkConfig(args);
    }

    public static JSONObject startSink(JSONObject args) {
        String type = args.getString("type");
        JSONObject fields = args.getJSONObject("fields");
        KafkaSinkWorker sinkWorker = WorkerInvoking.getSinkWorkerByTypeName(type);
        String connectName = args.getOrDefault("connectName", "sink-" + type.toLowerCase() + "-" + DdUtil.date2String(new Date())).toString();
        String topicName = args.getString("topic_name");
        fields.put("connectName", connectName);
        fields.put("topic_name", topicName);
        if ("oracle".equalsIgnoreCase(type)) {
            fields.put("tableName", fields.getString("tableName").toUpperCase());
        }

        JSONObject configJson = sinkWorker.createConfigJson(fields);
        if (!configJson.getBoolean(IS_SUCCEED)) {
            return configJson;
        }

        JSONObject msg = sinkWorker.startDataSink(connectUrl, configJson.getJSONObject("msg"));
        msg.fluentPut("connectName", connectName).fluentPut("topic", topicName)
                .fluentPut("groupId", "connect-" + connectName);
        System.out.println("===================启动成功。。。。。。。");
        return msg;
    }

    public static JSONObject stopSink(String connectName) {
        return KafkaSinkWorker.stopDataSink(connectUrl, connectName);
    }

    /**
     * UNASSIGNED,RUNNING,PAUSED,FAILED
     *
     * @param connectName string
     * @return json
     */
    public static JSONObject getStatusByConnectName(String connectName) {
        return KafkaSinkWorker.getTaskStatus(connectUrl, connectName);
    }

    /**
     * 获得所有运行中的sink connector
     *
     * @return json
     */
    public static JSONObject showRunningConnectors() throws Exception {
        JSONObject runningSinkConnector = KafkaSinkWorker.getRunningSinkConnector(connectUrl);
        if (Objects.isNull(runningSinkConnector)) {
            throw new GeneralServiceException("got error results");
        }
        JSONArray allConnectors = runningSinkConnector.getJSONArray("msg");
        Object[] connectorNames = allConnectors.toArray(new Object[allConnectors.size()]);
        JSONArray runningSinkConnectors = Arrays.stream(connectorNames).filter(connectorName -> connectorName.toString().startsWith("sink-"))
                .collect(toCollection(JSONArray::new));
        return DdUtil.succeedResult(runningSinkConnectors);
    }

    public static JSONObject pauseSink(String connectName) {
        return KafkaSinkWorker.pauseSink(connectUrl, connectName);
    }

    public static JSONObject resumeSink(String connectName) {
        return KafkaSinkWorker.resumeSink(connectUrl, connectName);
    }

    public static JSONObject restartSink(String connectName) {
        return KafkaSinkWorker.restartSink(connectUrl, connectName);
    }


}
