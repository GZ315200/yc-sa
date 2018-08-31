package com.unistack.tamboo.mgt.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.unistack.tamboo.commons.utils.CommonUtils;
import com.unistack.tamboo.commons.utils.JsonUtils;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.commons.utils.enums.EDatabaseType;
import com.unistack.tamboo.mgt.common.MonitorKeyField;
import com.unistack.tamboo.mgt.dao.monitor.MBrokerServiceDao;
import com.unistack.tamboo.mgt.dao.monitor.MCollectDao;
import com.unistack.tamboo.mgt.dao.monitor.MZkServiceDao;
import com.unistack.tamboo.mgt.helper.JMXHelper;
import com.unistack.tamboo.mgt.helper.TopicHelper;
import com.unistack.tamboo.mgt.model.monitor.*;
import com.unistack.tamboo.mgt.utils.MonitorUtils;
import com.unistack.tamboo.mgt.utils.RedisClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.QueryExp;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author Gyges Zean
 * @date 2018/7/17
 * <p>
 * Supply the collection metrics method for monitoring the zookeeper and Brokers
 */

@Service
public class MetricsService extends MonitorKeyField {

    public static  Logger logger = LoggerFactory.getLogger(MetricsService.class);

    @Autowired
    private TopicHelper topicHelper;

    @Autowired
    private MBrokerServiceDao mBrokerServiceDao;

    @Autowired
    private MZkServiceDao mZkServiceDao;

    @Autowired
    private MCollectDao mCollectDao;


    /**
     * 采集zookeeper的metrics信息
     *
     * @param timestamp
     */
    public void zookeeperMetricsInKafka(long timestamp) {
        JSONObject result = new JSONObject();
        result.put("timestamp", timestamp);
        try {
            ObjectName latencyName = new ObjectName("kafka.server:type=ZooKeeperClientMetrics,name=ZooKeeperRequestLatencyMs");
            ObjectName stateName = new ObjectName("kafka.server:type=SessionExpireListener,name=SessionState");

            List<BrokerInfo> brokerInfoList = topicHelper.getBrokerInfoList();
            Map<BrokerInfo, Map<ObjectName, Object>> latencyMetrics = concurrentlyScanJMX(brokerInfoList, latencyName, null, "Count");
            Map<BrokerInfo, Map<ObjectName, Object>> stateMetrics = concurrentlyScanJMX(brokerInfoList, stateName, null, "Value");

            JSONArray array = new JSONArray(2);
            assembleTheMetricsData(array, latencyMetrics, "name");
            assembleTheMetricsData(array, stateMetrics, "name");

            result.put(ZOOKEEPER_METRICS, array);

            RedisClient.set(ZOOKEEPER_METRICS.getBytes(), JsonUtils.toByteArray(result), EDatabaseType.KAFKA.getCode());
//            logger.info("get zookeeper metrics: " + result.toJSONString());
        } catch (Exception e) {
            logger.error("Failed to collect zookeeper metrics", e);
        }
    }


    /**
     * 采集brokers的metrics的信息
     *
     * @param timestamp
     */
    public void getBrokersMetrics(long timestamp) {
        JSONObject result = new JSONObject();
        result.put("timestamp", timestamp);
        try {
            ObjectName brokersName = new ObjectName("kafka.server:type=KafkaServer,name=*,*");
            ObjectName controllerName = new ObjectName("kafka.controller:type=KafkaController,name=*,*");

            List<BrokerInfo> brokerInfoList = topicHelper.getBrokerInfoList();
            Map<BrokerInfo, Map<ObjectName, Object>> brokersMetrics = concurrentlyScanJMX(brokerInfoList, brokersName, null, "Value");
            Map<BrokerInfo, Map<ObjectName, Object>> controllerMetrics = concurrentlyScanJMX(brokerInfoList, controllerName, null, "Value");
            JSONArray array = new JSONArray(2);
            assembleTheMetricsData(array, brokersMetrics, "name");
            assembleTheMetricsData(array, controllerMetrics, "name");

            result.put(BROKERS_METRICS, array);

            RedisClient.set(BROKERS_METRICS.getBytes(), JsonUtils.toByteArray(result), EDatabaseType.KAFKA.getCode());
//            logger.info("get brokers metrics: " + result.toJSONString());
        } catch (Exception e) {
            logger.error("Failed to collect brokers metrics", e);
        }
    }


    public void getProducerMetrics(long timestamp) {
        List<MCollectorVo> topics = MonitorUtils.getConnectRunning();

        JSONObject result = new JSONObject();
        result.put("timestamp", timestamp);
        try {
            List<String> connectUrl = topicHelper.connect();
            JSONArray array = new JSONArray();
            for (MCollectorVo topic : topics) {
                JSONObject topicData = new JSONObject();
                String pattern = "kafka.producer:type=producer-topic-metrics,client-id=producer-*,topic=" + topic.getTopicName();
                ObjectName producersName = new ObjectName(pattern);
                Map<String, Map<String, Object>> metrics = concurrentlyScanConnectJMX(connectUrl, producersName, null,
                        Arrays.asList("record-retry-rate",
                                "record-send-rate", "record-retry-total", "compression-rate",
                                "record-error-total", "byte-rate", "record-error-rate", "byte-total", "record-send-total"));

                for (Map.Entry<String, Map<String, Object>> entry : metrics.entrySet()) {
                    String host = entry.getKey();
                    Map<String, Object> data = entry.getValue();
                    JSONObject metric = new JSONObject(metrics.size());
                    for (Map.Entry<String, Object> object : data.entrySet()) {
                        metric.put(object.getKey(), object.getValue());
                    }
                    topicData.put(topic.getTopicName(), metric);
                    array.add(topicData);
                    result.put(host, array);
                }
            }
            RedisClient.set((CONNECT_METRICS).getBytes(), JsonUtils.toByteArray(result), EDatabaseType.KAFKA.getCode());
        } catch (Exception e) {
            logger.error("Failed to collect producers metrics", e);
        }
    }


    /**
     * 将采集到的数据保存在数据库中
     * * @param host
     *
     * @param
     */
    public void saveConnectRecordToDb() {
        List<MCollectorVo> topics = MonitorUtils.getConnectRunning();
        JSONObject object = JSON.parseObject(RedisClient.get(CONNECT_METRICS.getBytes()), JSONObject.class);

        List<String> connectUrl = topicHelper.connect();
        for (String host : connectUrl) {

            JSONArray arr = object.getJSONArray(host);
            if (Objects.isNull(arr)) {
                continue;
            }
            Long recordSendTotal = 0L;
            Long recordSendRate = 0L;
            for (int i = 0; i < arr.size(); i++) {
                for (MCollectorVo mCollectorVo : topics) {
                    MCollect collect = new MCollect();
                    JSONObject o = arr.getJSONObject(i);
                    if (Objects.isNull(o)) {
                        continue;
                    }
                    JSONObject data = o.getJSONObject(mCollectorVo.getTopicName());
                    if (Objects.isNull(data)) {
                        continue;
                    }
                    if (data.size() == 0) {
                        data.put("record-send-rate", 0.0);
                        data.put("record-send-total", 0.0);
                    }
                    collect.setTopicId(mCollectorVo.getTopicId());
                    collect.setDataSourceId(mCollectorVo.getDataSourceId());
                    collect.setCollectType(mCollectorVo.getType());
                    collect.setPeakTime(new Date());
                    collect.setTimestamp(new Date());
                    recordSendRate = CommonUtils.formatDoubleToLong(data.getFloat("record-send-rate"));
                    recordSendTotal = CommonUtils.formatDoubleToLong(data.getFloat("record-send-total"));
                    collect.setTotalColSize(recordSendTotal);
                    collect.setColRate(recordSendRate);
                    collect.setPeakColSize(recordSendTotal);
                    mCollectDao.save(collect);

                }
            }
        }
    }


    /**
     * 组装metrics的数据
     *
     * @param metrics
     * @param exps
     */
    private JSONArray assembleTheMetricsData(JSONArray array, Map<BrokerInfo, Map<ObjectName, Object>> metrics, String... exps) {
        for (Map.Entry<BrokerInfo, Map<ObjectName, Object>> entry : metrics.entrySet()) {
            JSONObject data = new JSONObject();
            JSONObject result = new JSONObject();
            String host = entry.getKey().getHost();
            int port = entry.getKey().getPort();
            int brokerId = entry.getKey().getBrokerId();
            String hostPort = host + ":" + port;
            result.put("host", host + "-" + brokerId);
            result.put("brokerId", brokerId);
            Map<ObjectName, Object> objectMap = entry.getValue();
            for (Map.Entry<ObjectName, Object> objectEntry : objectMap.entrySet()) {
                ObjectName name = objectEntry.getKey();
                for (String exp : exps) {
                    Object attr = name.getKeyProperty(exp);
                    Object value = objectEntry.getValue();
                    result.put(String.valueOf(attr), value);
                }
            }
            data.put(hostPort, result);
            array.add(data);
        }
        return array;
    }


    private Map<BrokerInfo, Map<ObjectName, Object>> concurrentlyScanJMX( List<BrokerInfo> brokerInfoList,  ObjectName objectName,
                                                                          QueryExp exp,  String attr) {
        Map<BrokerInfo, Map<ObjectName, Object>> result = Maps.newHashMap();
        Map<BrokerInfo, Future<Map<ObjectName, Object>>> taskList = Maps.newHashMap();
        for ( BrokerInfo info : brokerInfoList) {
            FutureTask<Map<ObjectName, Object>> task = new FutureTask<Map<ObjectName, Object>>(
                    () -> {
                        Map<ObjectName, Object> brokerResult = Maps.newHashMap();
                        JMXHelper helper = JMXHelper.getInstance(info.getHost().trim(), info.getJmxPort().toString().trim());
                        for (ObjectName obj : helper.queryNames(objectName, exp)) {
                            brokerResult.put(obj, helper.getAttribute(obj, attr));
                        }
//                        logger.info("got kafka JMX Metrics: type = {}, Broker = {}, ObjectCount = {}, cost = {}",
//                                objectName.getKeyProperty("type"), info.getHost(), brokerResult.size(),
//                                System.currentTimeMillis() - start);
                        return brokerResult;
                    });
            taskList.put(info, task);
            new Thread(task).start();
        }
        for (BrokerInfo info : taskList.keySet()) {
            try {
                Map<ObjectName, Object> value = taskList.get(info).get();
                result.put(info, value);
            } catch (Exception e) {
                logger.error("failed to get broker metrics. broker_info = " + info, e);
            }
        }
        return result;
    }


    /**
     * 获取brokers的metrics值。
     *
     * @return
     */
    public MClusterServiceVo getBrokersMetrics() {
        byte[] info = RedisClient.get(BROKERS_METRICS.getBytes());
        JSONObject data = JSON.parseObject(info, JSONObject.class);
        List<BrokerInfoVo> brokerInfoVos = Lists.newArrayList();
        MClusterServiceVo serviceVo = new MClusterServiceVo();
        List<String> realBrokers = topicHelper.getRealBrokers();

        String controller = topicHelper.controller();

        JSONArray array = data.getJSONArray(BROKERS_METRICS);

        for (int i = 0; i < array.size(); i++) {
            BrokerInfoVo brokerInfoVo = new BrokerInfoVo();
            for (String broker : realBrokers) {

                if (topicHelper.isAlive(broker)) {
                    brokerInfoVo.setBrokerState(BrokerState.ALIVE);
                } else {
                    brokerInfoVo.setBrokerState(BrokerState.DOWN);
                }
                JSONObject object = array.getJSONObject(i).getJSONObject(broker);
                if (!Objects.isNull(object)) {
                    //如果是controller
                    if (controller.equals(broker)) {
                        serviceVo.setActiveControllerCount(object.getString(ACTIVE_CONTROLLER_COUNT));
                        serviceVo.setGlobalPartitionCount(object.getString(GLOBAL_PARTITION_COUNT));
                        serviceVo.setGlobalTopicCount(object.getString(GLOBAL_TOPIC_COUNT));
                    }

                    String brokerId = object.getString(BROKER_ID);
                    String cluster = object.getString(CLUSTER_ID);
                    String brokerState = object.getString(BROKER_STATE);
                    String host = broker.split(":")[0] + "-" + topicHelper.getBrokerId(broker);
                    if (StringUtils.isBlank(cluster)) {
                        continue;
                    }
                    if (StringUtils.isBlank(brokerState)) {
                        continue;
                    }
                    serviceVo.setClusterId(cluster);
                    serviceVo.setBrokersNumber(brokerState);
                    getTopicInfo(brokerId, brokerInfoVo);
                    brokerInfoVo.setHost(host);
                    getZookeeperInfo(broker, brokerInfoVo);
                    brokerInfoVos.add(brokerInfoVo);
                }
            }
            serviceVo.setBrokerInfoVo(brokerInfoVos);
        }
        long timestamp = CommonUtils.formatString(data.getString(TIMESTAMP)) / 1000;
        serviceVo.setTimestamp(CommonUtils.formatLong(timestamp));
        mBrokerServiceDao.deleteAll();
        saveMBrokerServiceInfo(brokerInfoVos, serviceVo);
        return serviceVo;
    }


    private void getTopicInfo(String brokerId, BrokerInfoVo brokerInfoVo) {
        LogDirInfo logDirInfo = topicHelper.describeLogDir2(brokerId);
        List<TopicOffsetVo> topicOffsetVos = logDirInfo.getTopicOffsetVos();
        Long topicTotalSize = 0L;
        Set<String> topicInfos = Sets.newHashSet();

        for (TopicOffsetVo topicOffsetVo : topicOffsetVos) {
            topicInfos.add(topicOffsetVo.getTopic());
            topicTotalSize = topicOffsetVo.getOffsetSizePartition().getSize() + topicTotalSize;
        }

        brokerInfoVo.setTopicNumber(String.valueOf(topicInfos.size()));
        brokerInfoVo.setTopicDataSize(String.valueOf(topicTotalSize));
    }


    private void getZookeeperInfo(String broker, BrokerInfoVo brokerInfoVo) {
        byte[] info = RedisClient.get(ZOOKEEPER_METRICS.getBytes());
        JSONObject data = JSON.parseObject(info, JSONObject.class);
        JSONArray array = data.getJSONArray(ZOOKEEPER_METRICS);
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i).getJSONObject(broker);
            if (!Objects.isNull(object)) {
                String zKRequestLatencyMs = object.getString(ZOOKEEPER_REQUEST_LATENCY_MS);
                String zKState = object.getString(SESSION_STATE);
                if (StringUtils.isNotBlank(zKState)) {
                    brokerInfoVo.setzKState(zKState);
                }
                if (StringUtils.isNotBlank(zKRequestLatencyMs)) {
                    brokerInfoVo.setzKRequestLatencyMs(zKRequestLatencyMs);
                }
            }
        }
    }


    private void saveMBrokerServiceInfo(List<BrokerInfoVo> brokerInfoVos, MClusterServiceVo mcs) {
        List<MBrokerService> brokerServices = Lists.newArrayList();

        for (BrokerInfoVo brokerInfoVo : brokerInfoVos) {
            MBrokerService brokerService = new MBrokerService();
            brokerService.setHost(brokerInfoVo.getHost());
            brokerService.setTopicDataSize(brokerInfoVo.getTopicDataSize());
            brokerService.setTopicNumber(brokerInfoVo.getTopicNumber());
            brokerService.setzKRequestLatencyMs(brokerInfoVo.getzKRequestLatencyMs());
            brokerService.setzKState(brokerInfoVo.getzKState());
            brokerService.setBrokerState(brokerInfoVo.getBrokerState());

            brokerService.setTimestamp(mcs.getTimestamp());
            brokerService.setGlobalTopicCount(mcs.getGlobalTopicCount());
            brokerService.setGlobalPartitionCount(mcs.getGlobalPartitionCount());
            brokerService.setActiveControllerCount(mcs.getActiveControllerCount());
            brokerService.setClusterId(mcs.getClusterId());
            brokerService.setBrokersNumber(mcs.getBrokersNumber());
            brokerServices.add(brokerService);
        }
        mBrokerServiceDao.save(brokerServices);

    }


    public void getZookeeperMetrics(long timestamp) {
        JSONObject result = new JSONObject();
        result.put("timestamp", timestamp);
        try {
            ObjectName zookeeperName = new ObjectName("org.apache.ZooKeeperService:name0=StandaloneServer_port-1");

            Map<ZookeeperInfo, Map<String, Object>> zkMetrics = concurrentlyScanZkJMX(topicHelper.zookeeperInfos(), zookeeperName, null
                    , Arrays.asList("StartTime", "Version", "MaxClientCnxnsPerHost", "ClientPort",
                            "AvgRequestLatency", "MaxRequestLatency", "MinRequestLatency",
                            "PacketsSent", "PacketsReceived", "TickTime", "OutstandingRequests"));

            assembleZkData(result, zkMetrics);
            RedisClient.set(ZOOKEEPER_METRICS_DATA.getBytes(), JsonUtils.toByteArray(result), EDatabaseType.KAFKA.getCode());
//            logger.info("get brokers metrics: " + result.toJSONString());
        } catch (Exception e) {
            logger.error("Failed to collect brokers metrics", e);
        }
    }

    /**
     * 组装zookeeper的信息
     *
     * @param data
     * @param zkMetrics
     */
    private void assembleZkData(JSONObject data, Map<ZookeeperInfo, Map<String, Object>> zkMetrics) {
        JSONObject result = new JSONObject();
        String host = "";
        ZookeeperInfo zookeeperInfo = null;
        for (Map.Entry<ZookeeperInfo, Map<String, Object>> entry : zkMetrics.entrySet()) {
            zookeeperInfo = entry.getKey();
            host = zookeeperInfo.getHost();
            Map<String, Object> metrics = entry.getValue();
            if (metrics.size() > 0) {
                result.put("state", "ALIVE");
            } else {
                result.put("state", "DOWN");
            }
            result.putAll(metrics);
            assembleZkDataToDB(zookeeperInfo, result);
        }
        data.put(host, result);
    }


    /**
     * 组装zookeeper 的信息保存到db
     *
     * @param zookeeperInfo
     * @param result
     */
    private void assembleZkDataToDB(ZookeeperInfo zookeeperInfo, JSONObject result) {
        MZkService zkService = new MZkService();
        zkService.setHost(zookeeperInfo.getHost());
        zkService.setClientPort(result.getString("ClientPort"));
        zkService.setAvgRequestLatency(result.getString("AvgRequestLatency"));
        zkService.setMaxClientCnxnsPerHost(result.getString("MaxClientCnxnsPerHost"));
        zkService.setMaxRequestLatency(result.getString("MaxRequestLatency"));
        zkService.setMinRequestLatency(result.getString("MinRequestLatency"));
        zkService.setOutstandingRequest(result.getString("OutstandingRequests"));
        zkService.setPacketsReceived(result.getString("PacketsReceived"));
        zkService.setPacketsSend(result.getString("PacketsSent"));
        String version = result.getString("Version").split(",")[0].trim();
        zkService.setVersion(version);
        zkService.setState(result.getString("state"));
        zkService.setTimestamp(CommonUtils.formatLong(System.currentTimeMillis() / 1000));
        zkService.setTicktime(result.getString("TickTime"));
        zkService.setStartTime(result.getString("StartTime"));

        mZkServiceDao.save(zkService);
    }


    private Map<ZookeeperInfo, Map<String, Object>> concurrentlyScanZkJMX( List<ZookeeperInfo> zookeeperList,  ObjectName objectName,
                                                                           QueryExp exp,  List<String> attrs) {
        Map<ZookeeperInfo, Map<String, Object>> result = Maps.newHashMap();
        Map<ZookeeperInfo, Future<Map<String, Object>>> taskList = Maps.newHashMap();
        for ( ZookeeperInfo info : zookeeperList) {
            FutureTask<Map<String, Object>> task = new FutureTask<Map<String, Object>>(
                    () -> {
                        long start = System.currentTimeMillis();
                        Map<String, Object> brokerResult = Maps.newHashMap();
                        JMXHelper helper = JMXHelper.getInstance(info.getHost().trim(), info.getJmxPort().trim());
                        for (ObjectName obj : helper.queryNames(objectName, exp)) {
                            for (String attr : attrs) {
                                brokerResult.put(attr, helper.getAttribute(obj, attr));
                            }
                        }
//                        logger.info("got kafka JMX Metrics: type = {}, Broker = {}, ObjectCount = {}, cost = {}",
//                                objectName.getKeyProperty("type"), info.getHost(), brokerResult.size(),
//                                System.currentTimeMillis() - start);
                        return brokerResult;
                    });
            taskList.put(info, task);
            new Thread(task).start();
        }

        for (ZookeeperInfo info : taskList.keySet()) {
            try {
                Map<String, Object> value = taskList.get(info).get();
                result.put(info, value);
            } catch (Exception e) {
                logger.error("failed to get broker metrics. broker_info = " + info, e);
            }
        }
        return result;
    }


    private Map<String, Map<String, Object>> concurrentlyScanConnectJMX( List<String> urls,  ObjectName objectName,
                                                                         QueryExp exp,  List<String> attrs) {
        Map<String, Map<String, Object>> result = Maps.newHashMap();
        Map<String, Future<Map<String, Object>>> taskList = Maps.newHashMap();
        for ( String host : urls) {
            FutureTask<Map<String, Object>> task = new FutureTask<Map<String, Object>>(
                    () -> {
                        long start = System.currentTimeMillis();
                        Map<String, Object> brokerResult = Maps.newHashMap();
                        JMXHelper helper = JMXHelper.getInstance(host.trim(), TambooConfig.CONNECT_JMXPORT.trim());
                        for (ObjectName obj : helper.queryNames(objectName, exp)) {
                            for (String attr : attrs) {
                                brokerResult.put(attr, helper.getAttribute(obj, attr));
                            }
                        }
                        logger.info("got kafka JMX Metrics: type = {}, Broker = {}, ObjectCount = {}, cost = {}",
                                objectName.getKeyProperty("type"), host, brokerResult.size(),
                                System.currentTimeMillis() - start);
                        return brokerResult;
                    });
            taskList.put(host, task);
            new Thread(task).start();
        }

        for (String info : taskList.keySet()) {
            try {
                Map<String, Object> value = taskList.get(info).get();
                result.put(info, value);
            } catch (Exception e) {
                logger.error("failed to get broker metrics. broker_info = " + info, e);
            }
        }
        return result;
    }


    public static void main(String[] args) {
        try {
            MetricsService service = new MetricsService();
            ObjectName latencyName = new ObjectName("kafka.producer:type=producer-topic-metrics,client-id=producer-*,topic=connect3_kafkatable3");

            List<BrokerInfo> brokerInfos = Lists.newArrayList();

            BrokerInfo brokerInfo = new BrokerInfo();
            brokerInfo.setJmxPort(8888);
            brokerInfo.setHost("192.168.0.103");
            brokerInfos.add(brokerInfo);

//            Map<BrokerInfo, Map<String, Object>> metrics = service.
//                    concurrentlyScanConnectJMX(brokerInfos, latencyName, null, Arrays.asList("record-retry-rate",
//                            "record-send-rate", "record-retry-total", "compression-rate",
//                            "record-error-total", "byte-rate", "record-error-rate", "byte-total", "record-send-total"));

//            System.out.println(JSON.toJSON(metrics));

        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
    }
}
