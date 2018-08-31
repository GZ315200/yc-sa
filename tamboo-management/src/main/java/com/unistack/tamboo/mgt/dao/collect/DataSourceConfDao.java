package com.unistack.tamboo.mgt.dao.collect;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.collect.DataSourceConf;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface DataSourceConfDao extends BaseDao<DataSourceConf,Long> {
    List<DataSourceConf> findAllByDataSourceId(long datSourceId);

    @Modifying
    @Transactional
    @Query(value="delete from DataSourceConf d where d.id in (:ids) ")
    int deleteByIds(@Param("ids")List<Long> ids);
}


