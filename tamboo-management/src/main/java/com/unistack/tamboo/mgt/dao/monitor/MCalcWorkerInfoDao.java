package com.unistack.tamboo.mgt.dao.monitor;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.monitor.MCaclWorkersInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Gyges Zean
 * @date 2018/7/27
 */
public interface MCalcWorkerInfoDao extends BaseDao<MCaclWorkersInfo,String> {

    @Query("select m from MCaclWorkersInfo m where m.host like %:query%")
    Page<MCaclWorkersInfo> getALlCalcInfo(@Param(value = "query") String query, Pageable pageable);


    @Override
    Page<MCaclWorkersInfo> findAll(Pageable pageable);
}
