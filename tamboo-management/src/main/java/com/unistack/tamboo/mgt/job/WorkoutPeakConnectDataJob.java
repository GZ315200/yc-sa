package com.unistack.tamboo.mgt.job;

import com.alibaba.fastjson.JSON;
import com.unistack.tamboo.commons.utils.JsonUtils;
import com.unistack.tamboo.mgt.dao.monitor.MConnectDao;
import com.unistack.tamboo.mgt.model.monitor.MConnect;
import com.unistack.tamboo.mgt.model.monitor.MTopicGroupIdR;
import com.unistack.tamboo.mgt.service.BaseService;
import com.unistack.tamboo.mgt.service.MqService;
import com.unistack.tamboo.mgt.utils.MonitorUtils;
import com.unistack.tamboo.mgt.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Gyges Zean
 * @date 2018/6/7
 */

@Component
public class WorkoutPeakConnectDataJob extends BaseService {

    public static  Logger log = LoggerFactory.getLogger(WorkoutPeakOffsetJob.class);


    @Autowired
    private MConnectDao connectDao;

    @Autowired
    private MqService mqService;

    private static  Long INTERVAL = PropertiesUtil.connectCollectInterval();


    public void receiveTheSinkMonitorData() {
        try {
            List<MTopicGroupIdR> mTopicGroupIdRS = MonitorUtils.getConnectorMonnitoring();

            for ( MTopicGroupIdR mTopicGroupIdR : mTopicGroupIdRS) {

                MConnect connect = new MConnect();

                long curOffset = mTopicGroupIdR.getTotal_data();
                long preOffset = getLastOffset(mTopicGroupIdR.getConnect_name());

                long preMsgRate = getLastDataRate(mTopicGroupIdR.getConnect_name());

                connect.setConnectName(mTopicGroupIdR.getConnect_name());
                connect.setPeakTime(new Date(System.currentTimeMillis()));
                connect.setPeakNum(curOffset);
                connect.setTotalData(curOffset);
                long incrOffset = curOffset - preOffset;
                long msgRate = computeMessageRate(incrOffset, INTERVAL);
                connect.setMessageRate(msgRate);
                connect.setTopicId(mTopicGroupIdR.getTopic_id());
                connect.setGroupId(mTopicGroupIdR.getGroup_id());
                connect.setConsumerLag(mTopicGroupIdR.getConsumer_lag());
                connect.setCreateTime(new Date());
                connect.setConsumerIp(mTopicGroupIdR.getConsumer_ip());
                mqService.setBytesValue((CONNECT_RECORD + mTopicGroupIdR.getConnect_name()).getBytes(),
                        JsonUtils.toByteArray(connect), INTERVAL);
                // peak
                if (curOffset > preOffset) {
                    connectDao.save(connect);
                }

                if (msgRate > preMsgRate) {
                    connectDao.updateMessageRate(mTopicGroupIdR.getConnect_name(), new Date(), msgRate);
                }

            }
        } catch (Exception e) {
            log.error("Failed to receive the sink monitor data", e);
        }

    }


    /**
     * 获取上一个offset peak 值
     *
     * @param connectName
     * @return
     */
    private long getLastOffset(String connectName) {
        return getMConnect(connectName).getPeakNum();
    }

    private long getLastDataRate(String connectName) {
        return getMConnect(connectName).getMessageRate();
    }


    private MConnect getMConnect(String connectName) {
        byte[] m = mqService.getBytesValue((CONNECT_RECORD + connectName).getBytes());
        if (Objects.isNull(m)) {
            MConnect mConnect = connectDao.getMConnectByConnectNameAndCreateTimeBetween(connectName, new Date(getZeroTime()), new Date());
            if (Objects.isNull(mConnect)) {
                return makeupZero(connectName);
            } else {
                return mConnect;
            }
        } else {
            return JSON.parseObject(m, MConnect.class);
        }
    }

    private MConnect makeupZero(String connectName) {
        MConnect mConnect = new MConnect();
        mConnect.setConnectName(connectName);
        mConnect.setPeakNum(0L);
        mConnect.setMessageRate(0L);
        mConnect.setTotalData(0L);
        mConnect.setPeakTime(new Date());
        return mConnect;
    }


}
