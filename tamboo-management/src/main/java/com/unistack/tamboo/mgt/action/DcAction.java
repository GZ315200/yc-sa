package com.unistack.tamboo.mgt.action;

import com.unistack.tamboo.commons.utils.errors.GeneralServiceException;
import com.unistack.tamboo.sa.dc.flume.common.DcConfig;
import com.unistack.tamboo.sa.dc.flume.common.DcResult;
import com.unistack.tamboo.sa.dc.flume.invoking.DcInvoking;
import com.unistack.tamboo.sa.dc.flume.source.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: tamboo-sa
 * @description: dc控制类
 * @author: Asasin
 * @create: 2018-05-28 18:40
 **/
public class DcAction {
    private static  Logger logger = LoggerFactory.getLogger(DcAction.class);

    /**
     * 创建和启动采集器
     *
     * @param dcConfig
     * @return
     */
    public static DcResult createAndStartDataCollector(DcConfig dcConfig) {
        DcInvoking dcInvoking = new DcInvoking();
        DcResult dcResult = dcInvoking.createDataCollector(dcConfig);
//        DcMonitor dcMonitor = new DcMonitor();
//        String httpPort = dcConfig.getConf().getString("httpPort");
//        String topic = dcConfig.getConf().getString("topic");
//        String url = "";
//        String ip = dcConfig.getConf().getString("ip");
//        if(StringUtils.isBlank(ip)){
//            url = "http://localhost:" + httpPort + "/metrics";
//        }else {
//            url = "http://" + ip + ":" + httpPort + "/metrics";
//        }
//        String type = dcConfig.getConf().getString("type");
//        dcMonitor.setTopic(topic);
//        dcMonitor.setUrl(url);
//        dcMonitor.setType(type);
//        MonitorQueue.setDcQueue(dcMonitor);
        return dcResult;
    }

    /**
     * 停止采集器
     * DcConfig包括：DcType dcType采集类型，JSONObject conf配置文件
     * dcType：WEBSERVICE，EXEC，SYSLOG，NETCAT，TAILDIR
     * conf:参数样例如下，所有key和value都为String类型，不同采集类型，source部分参数不同。
     * {
     * "type":"exec",
     * "command":"tail -f /home/flume/logs/zf/test.txt",
     * "inputCharset":"utf-8",
     * "topic":"test",
     * "username":"admin",
     * "password":"YWRtaW4xMjM=",
     * "servers":"192.168.1.193:9092,192.168.1.194:9093",
     * "ip":"192.168.1.202",
     * "user":"root",
     * "pwd":"hzq @2017",
     * "path":"/home/zc/",
     * "httpPort":"6045"
     * }
     *
     * @param dcConfig
     * @return
     */
    public static DcResult stopCollector(DcConfig dcConfig) {
        try {
            Class invoke = Class.forName(dcConfig.getDcType().getTypeName());
            switch (dcConfig.getDcType()) {
                case WEBSERVICE:
                    WebServiceSource webServiceSource = (WebServiceSource) invoke.newInstance();
                    return webServiceSource.stopCollector(dcConfig);
                case EXEC:
                    ExecSource execSource = (ExecSource) invoke.newInstance();
                    return execSource.stopCollector(dcConfig);
                case SYSLOG:
                    SysLogSource sysLogSource = (SysLogSource) invoke.newInstance();
                    return sysLogSource.stopCollector(dcConfig);
                case NETCAT:
                    NetCatSource netCatSource = (NetCatSource) invoke.newInstance();
                    return netCatSource.stopCollector(dcConfig);
                case TAILDIR:
                    TailDirSource tailDirSource = (TailDirSource) invoke.newInstance();
                    return tailDirSource.stopCollector(dcConfig);
                case HTTP:
                    HttpSource httpSource = (HttpSource) invoke.newInstance();
                    return httpSource.stopCollector(dcConfig);
//                case KAFKA:
//                    KafkaSource kafkaSource = (KafkaSource) invoke.newInstance();
//                    return kafkaSource.stopCollector(dcConfig);
                default:
                    return new DcResult(false, "dcType must be specified right");
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new GeneralServiceException(e);
        }
    }
}
