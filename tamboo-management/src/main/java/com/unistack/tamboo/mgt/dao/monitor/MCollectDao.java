package com.unistack.tamboo.mgt.dao.monitor;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.monitor.MCollect;
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
public interface MCollectDao extends BaseDao<MCollect, String> {

    List<MCollect> findAllByTimestampBetween(Date from, Date to);

    MCollect getMCollectByCollectType(String id);

    MCollect getMCollectByTopicIdAndTimestampIsBetween(Long topicId, Date from, Date to);


    @Modifying
    @Transactional
    @Query("update MCollect mc set mc.colRate =:col_rate , mc.peakTime =:peak_time where mc.topicId =:topic_id")
    void updateMessageRate(@Param("col_rate") Long colRate, @Param("peak_time") Date peakTime, @Param("topic_id") Long topicId);


}
