package com.unistack.tamboo.mgt.dao.monitor;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.monitor.MOffsetRecord;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/5/23
 */
public interface MOffsetRecordDao extends BaseDao<MOffsetRecord, Long> {

    List<MOffsetRecord> findAllByCreateTimeBetween(Date from, Date to);

    MOffsetRecord findAllByTopicId(Long topicId);

    @Modifying
    @Transactional
    @Query("update MOffsetRecord mr set mr.messageRate =:message_rate , mr.peakTime =:peak_time where mr.topicId =:topic_id")
    void updateMessageRate(@Param("topic_id") Long topicId, @Param("peak_time") Date peakTime, @Param("message_rate") Long messageRate);

}
