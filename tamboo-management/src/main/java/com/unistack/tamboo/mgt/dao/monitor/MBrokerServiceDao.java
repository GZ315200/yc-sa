package com.unistack.tamboo.mgt.dao.monitor;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.monitor.MBrokerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Gyges Zean
 * @date 2018/7/26
 */
public interface MBrokerServiceDao extends BaseDao<MBrokerService, String> {


    @Query("select m from MBrokerService m where m.host like %:query%")
    Page<MBrokerService> getALlBrokerInfo(@Param(value = "query") String query, Pageable pageable);


    @Override
    Page<MBrokerService> findAll(Pageable pageable);
}
