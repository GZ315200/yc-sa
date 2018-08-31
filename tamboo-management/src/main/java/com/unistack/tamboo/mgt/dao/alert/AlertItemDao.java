package com.unistack.tamboo.mgt.dao.alert;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.monitor.AlertItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Gyges Zean
 * @date 2018/6/25
 */
public interface AlertItemDao extends BaseDao<AlertItem, String> {

    @Transactional
    @Modifying
    @Query("update AlertItem a set a.active =:is_active where a.alertName =:alert_name")
    void updateAlertStatus(@Param("is_active") boolean isActive, @Param("alert_name") String alertName);
}
