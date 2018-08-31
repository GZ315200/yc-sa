package com.unistack.tamboo.mgt.job;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.unistack.tamboo.commons.utils.JsonUtils;
import com.unistack.tamboo.message.kafka.bean.ConsumerOffset;
import com.unistack.tamboo.message.kafka.runtime.Runner;
import com.unistack.tamboo.mgt.dao.collect.TopicInfoDao;
import com.unistack.tamboo.mgt.dao.monitor.MHistoryOffsetDao;
import com.unistack.tamboo.mgt.dao.monitor.MOffsetSDao;
import com.unistack.tamboo.mgt.model.collect.TopicInfo;
import com.unistack.tamboo.mgt.model.monitor.MHistoryOffset;
import com.unistack.tamboo.mgt.model.monitor.MOffsetS;
import com.unistack.tamboo.mgt.service.BaseService;
import com.unistack.tamboo.mgt.service.MqService;
import com.unistack.tamboo.mgt.helper.TopicHelper;
import com.unistack.tamboo.mgt.utils.MonitorUtils;
import com.unistack.tamboo.mgt.utils.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Gyges Zean
 * @date 2018/5/24
 */

@Component(value = "offsetCollectorService")
public class OffsetCollectorService extends BaseService {

    private static  Logger log = LoggerFactory.getLogger(OffsetCollectorService.class);

    @Autowired
    private MOffsetSDao mOffsetSDao;

    @Autowired
    private TopicInfoDao topicInfoDao;

    @Autowired
    private TopicHelper topicHelper;

    @Autowired
    private MHistoryOffsetDao mHistoryOffsetDao;

    @Autowired
    private MqService mqService;

    private static  Long INTERVAL = PropertiesUtil.offsetCollectInterval();

    /**
     * 存储消费族offset
     *
     * @param runner
     */
    public void storeConsumerGroupOffsets(Runner runner) {
        try {
            String bootstrapServer = topicHelper.bootstrapServers();

            List<ConsumerOffset> consumerOffsets = runner.getConsumerGroups(bootstrapServer, listAllTopicNameForCollector());

            assembleMHistoryOffset(consumerOffsets);
            assembleMOffsetSRecord(consumerOffsets);

            log.info("Finished save offset into table, size {}", consumerOffsets.size());
        } catch (Exception e) {
            log.error("Failed to flume the offset.", e);
        }
    }


    /**
     * 封装插入秒表的监控信息
     *
     * @param
     * @return
     */
    private void assembleMOffsetSRecord(List<ConsumerOffset> consumerOffsets) throws Exception {
        List<MOffsetS> mOffsetSList = Lists.newArrayList();
        for ( ConsumerOffset consumerOffset : consumerOffsets) {
            MOffsetS mOffsetS = new MOffsetS();
            Long topicId = getTopicId(consumerOffset.getTopic());
            long lastOffset = getLastOffset(topicId);
            Long offset = consumerOffset.getOffset();
            mOffsetS.setTopicId(topicId);
            mOffsetS.setUuid(uuid());
            mOffsetS.setAcceptTime(new Timestamp(consumerOffset.getTimestamp()));
            mOffsetS.setOffsetAdd(offset);

            if (consumerOffset.getOffset() > lastOffset) {
                long incrOffset = consumerOffset.getOffset() - lastOffset;
                long messageRate = computeMessageRate(incrOffset, INTERVAL);
                mOffsetS.setMessageRate(messageRate);
                mqService.setBytesValue((OFFSET_COLLECT + topicId).getBytes(), JsonUtils.toByteArray(mOffsetS), INTERVAL);
                mOffsetSList.add(mOffsetS);
            }
        }
        mOffsetSDao.save(mOffsetSList);
    }

    private synchronized String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    public Long getLastOffset(Long topicId) {
        return getMOffsetS(topicId).getOffsetAdd();
    }

    /**
     * 获取上一条数据作为此次增量
     *
     * @returnshan
     */
    private MOffsetS getMOffsetS(Long topicId) {
        byte[] mOffsetSByte = mqService.getBytesValue((OFFSET_COLLECT + topicId).getBytes());
        if (Objects.isNull(mOffsetSByte)) {
            MOffsetS mOffsetS = MonitorUtils.getMOffsetS(topicId);
            if (Objects.isNull(mOffsetS)) {
                return makeUpZero(topicId);
            } else {
                return mOffsetS;
            }
        } else {
            return JSON.parseObject(mOffsetSByte, MOffsetS.class);
        }
    }


    private MOffsetS makeUpZero(Long topicId) {
        MOffsetS mOffsetS = new MOffsetS();
        mOffsetS.setAcceptTime(new Timestamp(System.currentTimeMillis()));
        mOffsetS.setMessageRate(0L);
        mOffsetS.setOffsetAdd(0L);
        mOffsetS.setTopicId(topicId);
        return mOffsetS;
    }


    private List<String> listAllTopicNameForCollector() {
        List<String> topicName = Lists.newArrayList();
        List<TopicInfo> topicInfos = topicInfoDao.findAll();
        topicInfos.forEach(topicInfo -> {
            topicName.add(topicInfo.getTopicName());
        });
        return topicName;
    }


    /**
     * 获取topic 的Id
     *
     * @param topicName
     * @return
     */
    public Long getTopicId(String topicName) {
        return MonitorUtils.getTopicId(topicName);
    }


    /**
     * 封装历史的offset的信息
     *
     * @param consumerOffsets
     * @return
     */
    private void assembleMHistoryOffset(List<ConsumerOffset> consumerOffsets) {
        for ( ConsumerOffset consumerOffset : consumerOffsets) {
            MHistoryOffset mHistoryOffset = new MHistoryOffset();
            mHistoryOffset.setOffset(consumerOffset.getOffset());
            mHistoryOffset.setTimestamp(new Timestamp(System.currentTimeMillis()));
            mHistoryOffset.setTopicId(getTopicId(consumerOffset.getTopic()));
            mHistoryOffset.setPartition(consumerOffset.getPartition());
            mHistoryOffset.setGroup(consumerOffset.getGroup());
            mHistoryOffset.setConsumerId(consumerOffset.getConsumerId());
            mHistoryOffset.setLogEndOffset(consumerOffset.getLogEndOffset());
            mHistoryOffset.setLogStartOffset(consumerOffset.getLogStartOffset());
            mHistoryOffsetDao.save(mHistoryOffset);
        }
    }

}
