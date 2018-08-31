package com.unistack.tamboo.mgt.dao.monitor;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.monitor.MConnect;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/6/7
 */
public interface MConnectDao extends BaseDao<MConnect, String> {


    MConnect getMConnectByConnectNameAndCreateTimeBetween(String connectName,Date from,Date to);

    List<MConnect> findAllByCreateTimeBetween(Date from, Date to);

    @Modifying
    @Transactional
    @Query("update MConnect mc set mc.messageRate =:message_rate , mc.peakTime =:peak_time where mc.connectName =:connect_name")
    void updateMessageRate(@Param("connect_name") String connectName, @Param("peak_time") Date peakTime, @Param("message_rate") Long messageRate);


}
