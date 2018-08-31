package com.unistack.tamboo.mgt.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.CommonUtils;
import com.unistack.tamboo.commons.utils.JsonUtils;
import com.unistack.tamboo.commons.utils.OkHttpResponse;
import com.unistack.tamboo.commons.utils.OkHttpUtils;
import com.unistack.tamboo.mgt.dao.monitor.MCollectDao;
import com.unistack.tamboo.mgt.model.monitor.MCollect;
import com.unistack.tamboo.mgt.model.monitor.MCollectorVo;
import com.unistack.tamboo.mgt.service.BaseService;
import com.unistack.tamboo.mgt.service.MqService;
import com.unistack.tamboo.mgt.utils.MonitorUtils;
import com.unistack.tamboo.mgt.utils.PropertiesUtil;
import org.apache.commons.httpclient.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Gyges Zean
 * @date 2018/5/29
 */

@Service
public class FlumeDataCollectorService extends BaseService {

    public static  Logger log = LoggerFactory.getLogger(FlumeDataCollectorService.class);

    private static  long INTERVAL = PropertiesUtil.flumeCollectInterval();

    @Autowired
    private MCollectDao mCollectDao;
    @Autowired
    private MqService mqService;


    public void receiveFlumeDataCollector() {
        try {
            List<MCollectorVo> mCollectorVos = MonitorUtils.getRunningUrls();
            for ( MCollectorVo dcMonitor : mCollectorVos) {
                try {
                    long currentTime = CommonUtils.formatTimestamp(System.currentTimeMillis());
                    OkHttpResponse response = OkHttpUtils.get(dcMonitor.getUrl());
                    if (response.getCode() != HttpStatus.SC_OK) {
                        continue;
                    }
                    if (response.getCode() == HttpStatus.SC_OK) {
                        long successCount = getSuccessCount(response.getBody());
                        long timestamp = CommonUtils.formatTimestamp(currentTime);
                        assembleMCollectInfo(successCount, dcMonitor.getDataSourceId(), dcMonitor.getTopicId(), timestamp);
                    }
                } catch (Exception e) {
                    log.error("Failed to receive flume source data.", e);
                }
            }
        } catch (Exception e) {
            log.error("receiveFlumeDataCollector", e);
        }
    }


    private void assembleMCollectInfo(long colCount, Long datasourceId, Long topicId, long currentTime) {
        String key = FLUME_RECORD + topicId;

        long preMaxColSize = getPreSuccessCount(datasourceId, topicId, currentTime);
        long preMessageRate = messageRate(datasourceId, topicId, currentTime);
        long curColSize = CommonUtils.formatString(colCount);
        MCollect mCollect = new MCollect();
        mCollect.setPeakColSize(curColSize);
        mCollect.setPeakTime(new Date(currentTime));
        mCollect.setCollectType(String.valueOf(topicId));
        mCollect.setDataSourceId(datasourceId);
        mCollect.setTotalColSize(curColSize);
        mCollect.setTopicId(topicId);
        mCollect.setTimestamp(new Date());
        long messageRate = computeMessageRate(curColSize - preMaxColSize, INTERVAL);
        mCollect.setColRate(messageRate);
        mqService.setBytesValue(key.getBytes(), JsonUtils.toByteArray(mCollect), INTERVAL);
//        如果当前采集总数大于历史总数则计算当前增量作为峰值。
        mCollectDao.save(mCollect);
        if (messageRate > preMessageRate) {
            mCollectDao.updateMessageRate(messageRate, new Date(), topicId);
        }
        log.info("flume flume data size:{}", curColSize);
    }


    /**
     * 获取上一次成功发送的message数
     *
     * @param datasourceId
     * @return
     */
    private Long getPreSuccessCount(Long datasourceId, Long topicId, long createTime) {
        return getMCollect(datasourceId, topicId, createTime).getPeakColSize();
    }


    private Long messageRate(Long datasourceId, Long topicId, long createTime) {
        return getMCollect(datasourceId, topicId, createTime).getColRate();
    }


    /**
     * @param
     * @return
     */
    private MCollect getMCollect(Long datasourceId, Long topicId, long createTime) {
        String key = SPARK_RECORD + topicId;
        byte[] data = mqService.getBytesValue(key.getBytes());
//        取上一条信息作为增量
        if (Objects.isNull(data)) {
            MCollect collect = mCollectDao.getMCollectByTopicIdAndTimestampIsBetween(topicId, new Date(getZeroTime()), new Date());
            if (Objects.isNull(collect)) {
                return makeupZero(datasourceId, topicId, createTime);
            } else {
                return collect;
            }
        } else {
            return JSON.parseObject(data, MCollect.class);
        }
    }


    private MCollect makeupZero(Long datasourceId, Long topicId, long createTime) {
        MCollect mCollect = new MCollect();
        mCollect.setCollectType(String.valueOf(topicId));
        mCollect.setTimestamp(new Date(createTime));
        mCollect.setPeakColSize(0L);
        mCollect.setColRate(0L);
        mCollect.setTotalColSize(0L);
        mCollect.setPeakTime(new Date());
        return mCollect;
    }


    /**
     * 获取
     *
     * @param json
     * @return
     */
    private long getSuccessCount(String json) {
        Map<String, Map<String, Object>> result = JsonUtils.toMap(json);
        for (Map.Entry<String, Map<String, Object>> entry : result.entrySet()) {
            String key = entry.getKey();
            Map<String, Object> value = result.get(key);
            for (Map.Entry<String, Object> dataEntry : value.entrySet()) {
                if (dataEntry.getKey().equals("EventDrainSuccessCount")) {
                    return CommonUtils.formatString((String) value.get(dataEntry.getKey()));
                }
            }
        }
        return 0L;
    }


//    public static void main(String[] args) {
//        String json = "{\n" +
//                "\"CHANNEL.execChannel\": {\n" +
//                "\"ChannelCapacity\": \"10000\",\n" +
//                "\"ChannelFillPercentage\": \"98.81\",\n" +
//                "\"Type\": \"CHANNEL\",\n" +
//                "\"ChannelSize\": \"9881\",\n" +
//                "\"EventTakeSuccessCount\": \"1009210\",\n" +
//                "\"EventTakeAttemptCount\": \"1009317\",\n" +
//                "\"StartTime\": \"1527321633328\",\n" +
//                "\"EventPutAttemptCount\": \"1019211\",\n" +
//                "\"EventPutSuccessCount\": \"1019191\",\n" +
//                "\"StopTime\": \"0\"\n" +
//                "},\n" +
//                "\"SINK.kafkaSink\": {\n" +
//                "\"ConnectionCreatedCount\": \"0\",\n" +
//                "\"BatchCompleteCount\": \"0\",\n" +
//                "\"BatchEmptyCount\": \"6\",\n" +
//                "\"EventDrainAttemptCount\": \"0\",\n" +
//                "\"StartTime\": \"1527321633864\",\n" +
//                "\"BatchUnderflowCount\": \"1\",\n" +
//                "\"ConnectionFailedCount\": \"0\",\n" +
//                "\"ConnectionClosedCount\": \"0\",\n" +
//                "\"Type\": \"SINK\",\n" +
//                "\"RollbackCount\": \"0\",\n" +
//                "\"EventDrainSuccessCount\": \"1009210\",\n" +
//                "\"KafkaEventSendTimer\": \"7732\",\n" +
//                "\"StopTime\": \"0\"\n" +
//                "},\n" +
//                "\"SOURCE.execSource\": {\n" +
//                "\"EventReceivedCount\": \"1019211\",\n" +
//                "\"AppendBatchAcceptedCount\": \"0\",\n" +
//                "\"Type\": \"SOURCE\",\n" +
//                "\"EventAcceptedCount\": \"1019191\",\n" +
//                "\"AppendReceivedCount\": \"0\",\n" +
//                "\"StartTime\": \"1527321633769\",\n" +
//                "\"AppendAcceptedCount\": \"0\",\n" +
//                "\"OpenConnectionCount\": \"0\",\n" +
//                "\"AppendBatchReceivedCount\": \"0\",\n" +
//                "\"StopTime\": \"0\"\n" +
//                "}\n" +
//                "}";
//        FlumeDataCollectorService flumeDataCollectorService = new FlumeDataCollectorService();
//        System.out.println(flumeDataCollectorService.getSuccessCount(json));
//
//    }

}