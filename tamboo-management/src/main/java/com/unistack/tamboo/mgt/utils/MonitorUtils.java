package com.unistack.tamboo.mgt.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.unistack.tamboo.commons.utils.CommonUtils;
import com.unistack.tamboo.mgt.action.DataDist2Action;
import com.unistack.tamboo.mgt.common.enums.DataSourceEnum;
import com.unistack.tamboo.mgt.dao.alert.AlertItemDao;
import com.unistack.tamboo.mgt.dao.alert.AlertRecordDao;
import com.unistack.tamboo.mgt.dao.collect.DataSourceGroupDao;
import com.unistack.tamboo.mgt.dao.collect.DataSourceHostDao;
import com.unistack.tamboo.mgt.dao.collect.DataSourceListDao;
import com.unistack.tamboo.mgt.dao.collect.TopicInfoDao;
import com.unistack.tamboo.mgt.dao.monitor.*;
import com.unistack.tamboo.mgt.helper.TopicHelper;
import com.unistack.tamboo.mgt.model.collect.DataSourceGroup;
import com.unistack.tamboo.mgt.model.collect.DataSourceHost;
import com.unistack.tamboo.mgt.model.collect.DataSourceList;
import com.unistack.tamboo.mgt.model.collect.TopicInfo;
import com.unistack.tamboo.mgt.model.dashboard.RealTimeData;
import com.unistack.tamboo.mgt.model.monitor.*;
import com.unistack.tamboo.mgt.service.BaseService;
import com.unistack.tamboo.mgt.service.MqService;
import com.unistack.tamboo.mgt.service.dataFlow.CalcService;
import com.unistack.tamboo.mgt.service.dataFlow.DataFlowService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * @author Gyges Zean
 * @date 2018/5/24
 */
public class MonitorUtils extends BaseService {

    public static  Logger logger = LoggerFactory.getLogger(SecUtils.class);

    private static TopicInfoDao topicInfoDao = SpringContextHolder.getBean(TopicInfoDao.class);
    private static DataSourceListDao dataSourceListDao = SpringContextHolder.getBean(DataSourceListDao.class);
    private static DataSourceGroupDao dataSourceGroupDao = SpringContextHolder.getBean(DataSourceGroupDao.class);
    private static DataSourceHostDao dataSourceHostDao = SpringContextHolder.getBean(DataSourceHostDao.class);

    private static MClusterDao mClusterDao = SpringContextHolder.getBean(MClusterDao.class);
    private static TopicHelper topicHelper = SpringContextHolder.getBean(TopicHelper.class);

    private static AlertItemDao alertItemDao = SpringContextHolder.getBean(AlertItemDao.class);
    private static AlertRecordDao alertRecordDao = SpringContextHolder.getBean(AlertRecordDao.class);

    private static MOffsetRecordDao mOffsetRecordDao = SpringContextHolder.getBean(MOffsetRecordDao.class);
    private static MCollectDao mCollectDao = SpringContextHolder.getBean(MCollectDao.class);
    private static MSparkMessageDao mSparkMessageDao = SpringContextHolder.getBean(MSparkMessageDao.class);
    private static MOffsetSDao mOffsetSDao = SpringContextHolder.getBean(MOffsetSDao.class);

    private static MHistoryOffsetDao mHistoryOffsetDao = SpringContextHolder.getBean(MHistoryOffsetDao.class);
    private static MTopicGroupIdDao mTopicGroupIdDao = SpringContextHolder.getBean(MTopicGroupIdDao.class);
    private static MConnectDao mConnectDao = SpringContextHolder.getBean(MConnectDao.class);

    private static DataFlowService dataFlowService = SpringContextHolder.getBean(DataFlowService.class);

    private static CalcService calcService = SpringContextHolder.getBean(CalcService.class);

    private static MqService mqService = SpringContextHolder.getBean(MqService.class);


    /**
     * 获取所有告警信息列表
     *
     * @return
     */
    public static List<AlertItem> getAlertItems() {
        return alertItemDao.findAll();
    }


    public static AlertRecord saveAlertRecord(AlertRecord alertRecord) {
        return alertRecordDao.save(alertRecord);
    }


    /**
     * 获取实际集群信息
     *
     * @return
     */
    public static List<String> actualBrokers() {
        return topicHelper.brokerList();
    }


    /**
     * 获取期望的broker信息
     *
     * @return
     */
    public static List<String> expectBrokers() {
        MCluster cluster = mClusterDao.findMClusterByClusterType(EClusterType.KAFKA.name());
        List<String> brokers = Lists.newArrayList();
        String[] clusters = cluster.getServers().trim().split(",");
        brokers.addAll(Arrays.asList(clusters));
        return brokers;
    }


    /**
     * 根据topicId获取topicName
     *
     * @param topicName
     * @return
     */
    public static Long getTopicId(String topicName) {
        return topicInfoDao.getTopicInfoByTopicName(topicName).getTopicId();
    }


    /**
     * 获取所有topicId
     *
     * @return
     */
    public static List<Long> listAllTopicId() {
        List<TopicInfo> topicInfos = topicInfoDao.findAll();
        List<Long> topicIds = Lists.newArrayList();
        topicInfos.forEach(topicInfo -> {
            topicIds.add(topicInfo.getTopicId());
        });
        return topicIds;
    }


    public static long getDatasourceId(String collectType) {
        DataSourceList datasource = dataSourceListDao.getDataSourceListByDataSourceType(collectType);
        return datasource.getDataSourceId();
    }


    /**
     * 根据topicId 获取最近一次更新的数据
     *
     * @param topicId
     * @return
     */
    public static MOffsetS getMOffsetS(Long topicId) {
        return mOffsetSDao.getFirstByTopicIdOrderByAcceptTimeDesc(topicId);
    }


    /**
     * 根据topicId获取峰值数
     *
     * @param topicId
     * @return
     */
    public static MOffsetRecord getOffsetMonitorRecord(Long topicId) {
        String key = KAFKA_RECORD + topicId;
        MOffsetRecord mOffsetRecord = JSON.parseObject(mqService.get(key), MOffsetRecord.class);
        if (Objects.isNull(mOffsetRecord)) {
            return mOffsetRecordDao.findAllByTopicId(topicId);
        } else {
            return mOffsetRecord;
        }
    }

    /**
     * 获取所有topic的峰值
     *
     * @return
     */
    public static List<MOffsetRecord> getOffsetMonitorAllRecord() {
        return mOffsetRecordDao.findAllByCreateTimeBetween(new Date(getZeroTime()), new Date());
    }


    /**
     * 根据appId获取spark streaming data峰值
     *
     * @param appId
     * @return
     */
    public static MSparkMessage getStreamingMonitorRecord(String appId) {
        String key = SPARK_RECORD + appId;
        MSparkMessage sparkMessage = JSON.parseObject(mqService.get(key), MSparkMessage.class);
        if (Objects.isNull(sparkMessage)) {
            return mSparkMessageDao.findOne(appId);
        } else {
            return sparkMessage;
        }
    }


    /**
     * 获取所有的spark streaming data 计算的峰值
     *
     * @return
     */
    public static List<MSparkMessage> getStreamingMonitorAllRecord() {
        return mSparkMessageDao.findAllByTimestampBetween(new Date(getZeroTime()), new Date());
    }


    /**
     * 根据topicId获取采集数据的峰值
     *
     * @param
     * @return
     */
    public static MCollect getSourceCollectRecordByTopicId(Integer topicId) {
        String key = FLUME_RECORD + topicId;
        MCollect mCollect = JSON.parseObject(mqService.get(key), MCollect.class);
        if (Objects.isNull(mCollect)) {
            return mCollectDao.getMCollectByTopicIdAndTimestampIsBetween(Long.valueOf(topicId), new Date(getZeroTime()), new Date());
        } else {
            return mCollect;
        }
    }

    /**
     * 获取所有采集信息的峰值
     *
     * @return
     */
    public static List<MCollect> getSourceCollectAllRecord() {
        return mCollectDao.findAllByTimestampBetween(new Date(getZeroTime()), new Date());
    }


    /**
     * 获取所有sink的信息
     *
     * @return
     */
    public static List<MConnect> getConnectSinkData() {
        return mConnectDao.findAllByCreateTimeBetween(new Date(getZeroTime()), new Date());
    }


    public static MConnect getSinktRecordByConnectName(String connectName) {
        String key = CONNECT_RECORD + connectName;
        MConnect connect = JSON.parseObject(mqService.get(key), MConnect.class);
        if (Objects.isNull(connect)) {
            return mConnectDao.findOne(connectName);
        } else {
            return connect;
        }
    }

    /**
     * 获取数据采集总量
     *
     * @return
     */
    public  static RealTimeData summaryDataCollect() {
        RealTimeData realTimeData = new RealTimeData();
        List<MCollect> collects = getSourceCollectAllRecord();
        long totalRecords = 0L;
        for (MCollect mCollect : collects) {
            totalRecords = mCollect.getTotalColSize() + totalRecords;
        }
        realTimeData.setTotalMessage(CommonUtils.formatLong(totalRecords));
        return realTimeData;
    }


    /**
     * 获取数据总线的总量
     *
     * @return
     */
    public  static RealTimeData summaryDataHub() {
        RealTimeData realTimeData = new RealTimeData();
        List<MOffsetRecord> collects = getOffsetMonitorAllRecord();
        long totalRecords = 0L;
        for (MOffsetRecord offsetRecord : collects) {
            totalRecords = offsetRecord.getOffsetTotal() + totalRecords;
        }
        realTimeData.setTotalMessage(CommonUtils.formatLong(totalRecords));
        return realTimeData;
    }


    /**
     * 获取数据计算的总量
     *
     * @return
     */
    public  static RealTimeData summaryDataStreaming() {
        RealTimeData realTimeData = new RealTimeData();
        List<MSparkMessage> collects = getStreamingMonitorAllRecord();
        long totalRecords = 0L;
        for (MSparkMessage mSparkMessage : collects) {
            totalRecords = mSparkMessage.getTotalCalSize() + totalRecords;
        }
        realTimeData.setTotalMessage(CommonUtils.formatLong(totalRecords));
        return realTimeData;
    }


    /**
     * 获取数据计算的总量
     *
     * @return
     */
    public  static RealTimeData summaryConnectSinkData() {
        RealTimeData realTimeData = new RealTimeData();
        List<MConnect> connects = getConnectSinkData();
        long totalRecords = 0L;
        for (MConnect collect : connects) {
            totalRecords = collect.getTotalData() + totalRecords;
        }
        realTimeData.setTotalMessage(CommonUtils.formatLong(totalRecords));
        return realTimeData;
    }


    /**
     * 获得当前运行中的connector offset信息
     *
     * @return
     */
    public static List<MTopicGroupIdR> getConnectorMonnitoring() throws Exception {
        List<MTopicGroupIdR> list = new ArrayList<>();
        List<String> running = DataDist2Action.showRunningConnector();
//        if (running.getBoolean("isSucceed")) {
//            JSONArray connector_array = running.getJSONArray("msg");
        for (String connectorName1 :
                running) {
//                String connectorName1 = (String) connectorName;
            MTopicGroupId m1 = mTopicGroupIdDao.findByConnectorName(connectorName1);
            String group_id = m1.getGroup_id();
            MHistoryOffset lastMHistoryByGroupId = mHistoryOffsetDao.getLastMHistoryByGroupId(group_id);
            if (lastMHistoryByGroupId != null) {
                MTopicGroupIdR mr = new MTopicGroupIdR();
                mr.setGroup_id(lastMHistoryByGroupId.getGroup());
                mr.setTopic_id(lastMHistoryByGroupId.getTopicId());
                mr.setConsumer_lag(lastMHistoryByGroupId.getLogEndOffset() - lastMHistoryByGroupId.getOffset());
                mr.setTotal_data(lastMHistoryByGroupId.getOffset());
                mr.setConnect_name(connectorName1);
                String ip = StringUtils.split(lastMHistoryByGroupId.getConsumerId(), "/")[1];
                mr.setConsumer_ip(ip);
                list.add(mr);
            }
        }
//        }
        return list;
    }

    public static List<MOffsetS> getThirtyMinMonitors(long topicId) {
        Date date = new Date(System.currentTimeMillis() - 1800000L);
        return mOffsetSDao.getThirtyMinMonitors(topicId, date);
    }


    /**
     * 获取运行中的connect names
     *
     * @return
     */
    public static List<String> getConnectNames() {
        List<String> connect = Lists.newArrayList();
        List<MTopicGroupId> byStatus = mTopicGroupIdDao.findByStatus(1);
        byStatus.forEach(mTopicGroupId -> {
            connect.add(mTopicGroupId.getConnectorName());
        });
        return connect;
    }


    /**
     * 获取运行中的topic name
     *
     * @return
     */
    public static List<String> getConnectTopicNames() {
        List<String> topics = Lists.newArrayList();
        List<MTopicGroupId> byStatus = mTopicGroupIdDao.findByStatus(1);
        byStatus.forEach(mTopicGroupId -> {
            topics.add(mTopicGroupId.getTopic_name());
        });
        return topics;
    }


    public static List<MCollectorVo> getRunningUrls() {
        String[] strings = new String[]{"MYSQL", "ORACLE", "SQLSERVER", "PG", "HBASE"};
        List<DataSourceList> lists = dataSourceListDao.getDataSourceListByFlumeFlag(1, strings);
        List<MCollectorVo> mCollectorVos = new ArrayList<>();
        for (DataSourceList dataSourceList : lists) {
            MCollectorVo mCollectorVo = new MCollectorVo();
            DataSourceHost dataSourceHost = dataSourceHostDao.getDataSourceHostsById(dataSourceList.getHostId());
            String url = "http://" + dataSourceHost.getIp() + ":" + dataSourceList.getHttpPort() + "/metrics";
            mCollectorVo.setUrl(url);
            mCollectorVo.setType(dataSourceList.getDataSourceType());
            mCollectorVo.setDataSourceId(dataSourceList.getDataSourceId());
            mCollectorVo.setTopicId(dataSourceList.getTopicId());
            mCollectorVos.add(mCollectorVo);
        }
        return mCollectorVos;
    }


    public static List<MCollectorVo> getConnectRunning() {
        String[] strings = new String[]{"MYSQL", "ORACLE", "SQLSERVER", "PG", "HBASE"};
        List<DataSourceList> lists = dataSourceListDao.getDataSourceListByFlagConnectFlag(1, strings);
        List<MCollectorVo> mCollectorVos = new ArrayList<>();
        for (DataSourceList dataSourceList : lists) {
            MCollectorVo mCollectorVo = new MCollectorVo();
            mCollectorVo.setType(dataSourceList.getDataSourceType());
            mCollectorVo.setDataSourceId(dataSourceList.getDataSourceId());
            Long topicId = dataSourceList.getTopicId();
            TopicInfo topicInfo = topicInfoDao.getTopicInfoByTopicId(topicId);
            mCollectorVo.setTopicId(topicId);
            mCollectorVo.setTopicName(topicInfo.getTopicName());
            mCollectorVos.add(mCollectorVo);
        }
        return mCollectorVos;
    }

    /**
     * 获取所有运行的dataSource
     *
     * @return
     */
    public static List<DataSourceList> getRunningSource() {
        return dataSourceListDao.getDataSourceListByFlag(DataSourceEnum.DataSourceState.STARTED.getValue());
    }

    /**
     * 获取所有运行的dataSource的topicId
     *
     * @return
     */
    public static List<Long> getRunningTopicIds() {
        List<DataSourceList> lists = dataSourceListDao.getDataSourceListByFlag(DataSourceEnum.DataSourceState.STARTED.getValue());
        List<Long> runningTopicIds = new ArrayList<>();
        for (DataSourceList dataSourceList : lists) {
            runningTopicIds.add(dataSourceList.getTopicId());
        }
        return runningTopicIds;
    }



    /**
     * 修改dataSource的flag
     *
     * @param dataSourceId
     * @param flag
     */
    public static void updateFlumeState(Long dataSourceId, int flag) {
        dataSourceListDao.setFlag(dataSourceId, flag);
    }


    public static void updateSparkState(String appName, boolean isRunning) {
        dataFlowService.changeCalcStatus(appName, isRunning);
    }


    public static List<String> getAppNames() {
        return calcService.getAllRunningCalcAppnames();
    }


    /**
     * 获取connect实时监控速率
     *
     * @return
     */
    public  static Long getConnectRuntimeRate() {
        List<String> connectNames = getConnectNames();
        Long sumDataRate = 0L;
        for (String connect : connectNames) {
            String key = CONNECT_RECORD + connect;
            if (mqService.isExist(key)) {
                MConnect mConnect = JSON.parseObject(mqService.get(key), MConnect.class);
                sumDataRate = sumDataRate + mConnect.getMessageRate();
            } else {
                continue;
            }
        }
        return sumDataRate;
    }


    /**
     * 获取collector的实时监控速率
     *
     * @return
     */
    public  static Long getCollectorRuntimeRate() {
        List<Long> topicIds = getRunningTopicIds();
        Long sumDataRate = 0L;
        for (Long topicId : topicIds) {
            String key = FLUME_RECORD + topicId;
            if (mqService.isExist(key)) {
                MCollect mCollect = JSON.parseObject(mqService.get(key), MCollect.class);
                sumDataRate = sumDataRate + mCollect.getColRate();
            } else {
                continue;
            }
        }
        return sumDataRate;
    }


    /**
     * 获取数据总线的实时监控速率
     *
     * @return
     */
    public  static Long getHubRuntimeRate() {
        List<Long> topicIds = listAllTopicId();
        Long sumDataRate = 0L;
        for (Long topicId : topicIds) {
            String key = OFFSET_COLLECT + topicId;
            if (mqService.isExist(key)) {
                MOffsetS mOffsetS = JSON.parseObject(mqService.get(key), MOffsetS.class);
                sumDataRate = sumDataRate + mOffsetS.getMessageRate();
            } else {
                continue;
            }
        }
        return sumDataRate;
    }


    /**
     * 获取数据总线的实时监控速率
     *
     * @return
     */
    public  static Long getSparkRuntimeRate() {
        List<String> appNames = calcService.getAllRunningCalcAppnames();
        Long sumDataRate = 0L;
        for (String appName : appNames) {
            String key = SPARK_RECORD + appName;
            if (mqService.isExist(key)) {
                MSparkMessage mSparkMessage = JSON.parseObject(mqService.get(key), MSparkMessage.class);
                sumDataRate = sumDataRate + mSparkMessage.getCalRate();
            } else {
                continue;
            }
        }
        return sumDataRate;
    }


    /**
     * 封装DashBoard monitor data
     *
     * @return
     */
    public static JSONObject monitorDataRate() {
        JSONObject data = new JSONObject(4);
        try {
            RealTimeData flume = new RealTimeData(CommonUtils.formatLong(getCollectorRuntimeRate()),
                    summaryDataCollect().getTotalMessage());
            RealTimeData kafka = new RealTimeData(CommonUtils.formatLong(getHubRuntimeRate()),
                    summaryDataHub().getTotalMessage());
            RealTimeData spark = new RealTimeData(CommonUtils.formatLong(getSparkRuntimeRate()),
                    summaryDataStreaming().getTotalMessage());
            RealTimeData connect = new RealTimeData(CommonUtils.formatLong(getConnectRuntimeRate()),
                    summaryConnectSinkData().getTotalMessage());

            data.put(String.valueOf(SummaryType.TD), flume);
            data.put(String.valueOf(SummaryType.TM), kafka);
            data.put(String.valueOf(SummaryType.TS), spark);
            data.put(String.valueOf(SummaryType.TC), connect);

        } catch (Exception e) {
            logger.error("", e);
        }
        return data;
    }

    /**
     * 保存用户组resource资源信息
     *
     * @param groupName
     * @param description
     */
    public static void saveDataSourceGroup(String groupName, String description) {
        DataSourceGroup dataSourceGroup = new DataSourceGroup();
        dataSourceGroup.setGroupName(groupName);
        dataSourceGroup.setDescription(description);
        dataSourceGroup.setTopicSize(0);
        dataSourceGroup.setCalcSourceMem(0);
        dataSourceGroup.setTopicNum(0);
        dataSourceGroup.setCalcSourceCpu(0);
        dataSourceGroupDao.save(dataSourceGroup);
    }

}
