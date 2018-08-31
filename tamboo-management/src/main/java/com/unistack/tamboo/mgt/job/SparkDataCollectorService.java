package com.unistack.tamboo.mgt.job;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.unistack.tamboo.commons.utils.JsonUtils;
import com.unistack.tamboo.commons.utils.OkHttpResponse;
import com.unistack.tamboo.commons.utils.OkHttpUtils;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.mgt.dao.monitor.MSparkMessageDao;
import com.unistack.tamboo.mgt.model.monitor.MSparkMessage;
import com.unistack.tamboo.mgt.service.BaseService;
import com.unistack.tamboo.mgt.service.MqService;
import com.unistack.tamboo.mgt.utils.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Gyges Zean
 * @date 2018/5/29
 */

@Service
public class SparkDataCollectorService extends BaseService {

    public static final Logger log = LoggerFactory.getLogger(SparkDataCollectorService.class);

    private static final long INTERVAL = PropertiesUtil.sparkCollectInterval();

    @Autowired
    private MSparkMessageDao mSparkMessageDao;

    @Autowired
    private MqService mqService;


    public void receiveCalculateData() {
        String calUrl = "http://" + TambooConfig.CALC_CLUSTER_IPS + ":4040/api/v1/applications";
        try {
            OkHttpResponse response = OkHttpUtils.get(calUrl);
            if (response.getCode() == 0) {
                return;
            }
            if (response.getCode() == HttpStatus.SC_OK) {
                requestCalculateData(System.currentTimeMillis(), response.getBody(), calUrl);
            }
        } catch (Exception e) {
            log.error("spark cluster data for monitor get a failed", e);
        }
    }


    private void requestCalculateData(long currentTime, String result, String url) {
        try {
            for (final String appId : getAppInfoId(result)) {
                if (!(calStreamingMessage(url, appId, currentTime))) {
                    continue;
                }
            }
        } catch (Exception e) {
            log.error("requestCalculateData", e);
        }

    }


    /**
     * 获取当前运行的应用的id
     *
     * @param json
     * @return
     */
    private List<String> getAppInfoId(String json) {
        List<String> appInfoIds = Lists.newArrayList();
        JSONArray data = JSON.parseArray(json);
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            appInfoIds.add(jsonObject.getString("id"));
        }
        return appInfoIds;
    }


    private boolean calStreamingMessage(String url, String appId, long currentTime) throws Exception {
        String statisticsUrl = url + "/" + appId + "/streaming/statistics";
        String json = OkHttpUtils.get(statisticsUrl).getBody();
        if (json.contains("no streaming listener attached to")) {
            return false;
        }
        JSONObject data = JSON.parseObject(json);
        long latency = data.getLong("avgSchedulingDelay");
        //处理的条数
        long numProcessedRecords = data.getLong("numProcessedRecords");
        long preProcessedRecords = getLastProcessedRecords(appId, currentTime);

        long preMsgRate = getMSparkPackageMsg(appId, currentTime).getCalRate();

        MSparkMessage mSparkMessage = new MSparkMessage();
        long increCount = numProcessedRecords - preProcessedRecords;
        long msgRate = computeMessageRate(increCount, INTERVAL);
        mSparkMessage.setAppId(appId);
        mSparkMessage.setCalRate(msgRate);
        mSparkMessage.setPeakTime(new Date(currentTime));
        mSparkMessage.setPeakCalSize(numProcessedRecords);
        mSparkMessage.setLatency(latency);
        mSparkMessage.setTimestamp(new Date());
        mSparkMessage.setTotalCalSize(numProcessedRecords);
        mqService.setBytesValue((SPARK_RECORD + appId).getBytes(), JsonUtils.toByteArray(mSparkMessage), INTERVAL);
        if (numProcessedRecords > preProcessedRecords) {
            mSparkMessageDao.save(mSparkMessage);
        }

        if (msgRate > preMsgRate) {
            mSparkMessageDao.updateMessageRate(msgRate, new Date(), appId);
        }
        log.info("spark flume data size:{}", numProcessedRecords);
        return true;
    }


    private MSparkMessage getMSparkPackageMsg(String appId, long currentTime) {
        String key = SPARK_RECORD + appId;
        byte[] data = mqService.getBytesValue(key.getBytes());
//        如果上一条数据不存在则将前一条数据作为当前数据量
        if (Objects.isNull(data)) {
//           当天内该appId计算的
            MSparkMessage mSparkMessage = mSparkMessageDao.findMSparkMessageByAppIdAndTimestampIsBetween(appId, new Date(getZeroTime()), new Date());
            if (Objects.isNull(mSparkMessage)) {
                return makeupZero(appId, currentTime);
            } else {
                return mSparkMessage;
            }
        } else {
            return JSON.parseObject(data, MSparkMessage.class);
        }
    }


    private MSparkMessage makeupZero(String appName, long createTime) {
        MSparkMessage mSparkMessage = new MSparkMessage();
        mSparkMessage.setAppId(appName);
        mSparkMessage.setTimestamp(new Date(createTime));
        mSparkMessage.setPeakCalSize(0L);
        mSparkMessage.setCalRate(0L);
        mSparkMessage.setTotalCalSize(0L);
        mSparkMessage.setLatency(0L);
        mSparkMessage.setPeakTime(new Date());
        return mSparkMessage;
    }


    private long getLastProcessedRecords(String appId, long currentTime) {
        return getMSparkPackageMsg(appId, currentTime).getPeakCalSize();
    }


    /**
     * 获取当前运行的执行器的信息
     *
     * @param url
     * @param appId
     * @return
     * @throws IOException
     */
    private JSONObject getExecutorsInfo(String url, String appId) throws IOException {
        String executorsUrl = url + "/" + appId + "/allexecutors";
        JSONArray jsonArray = new JSONArray();
        JSONObject object = new JSONObject();
        String json = OkHttpUtils.get(executorsUrl).getBody();
        JSONArray data = JSON.parseArray(json);
        for (int i = 0; i < data.size(); i++) {
            JSONObject returnData = new JSONObject();
            JSONObject jsonObject = data.getJSONObject(i);
            String id = jsonObject.getString("id");
            String alive = jsonObject.getString("isActive");
            String host = jsonObject.getString("hostPort");
            String memoryUsed = jsonObject.getString("memoryUsed");
            returnData.put("executor_id", id);
            returnData.put("alive", alive);
            returnData.put("host", host);
            returnData.put("memoryUsed", memoryUsed);
            jsonArray.add(returnData);
        }
        object.put(appId, jsonArray);
        return object;
    }


}
