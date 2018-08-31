package com.unistack.tamboo.mgt.dao.monitor;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.monitor.MSparkMessage;
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
public interface MSparkMessageDao extends BaseDao<MSparkMessage, String> {

    List<MSparkMessage> findAllByTimestampBetween(Date from, Date to);

    MSparkMessage findMSparkMessageByAppIdAndTimestampIsBetween(String appId, Date from, Date to);

    @Modifying
    @Transactional
    @Query("update MSparkMessage s set s.calRate =:cal_rate , s.peakTime =:peak_time where s.appId =:app_id")
    void updateMessageRate(@Param("cal_rate") Long calRate, @Param("peak_time") Date peakTime, @Param("app_id") String appId);

}
