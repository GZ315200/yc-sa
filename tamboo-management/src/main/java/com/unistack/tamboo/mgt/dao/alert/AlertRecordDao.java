package com.unistack.tamboo.mgt.dao.alert;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.monitor.AlertRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/6/25
 */
public interface AlertRecordDao extends BaseDao<AlertRecord,Long>{

    Page<AlertRecord> getByResolved(boolean resolved, Pageable pageable);

    @Query(value="select a from AlertRecord a where id in :ids")
    List<AlertRecord> findByIds(@Param("ids") List<Long> ids);
}
