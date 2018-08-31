package com.unistack.tamboo.mgt.dao.calc;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.calc.OfflineDataSourceAuth;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;


public interface OfflineDataSourceAuthDao extends BaseDao<OfflineDataSourceAuth,Integer> {

    //delete from OfflineDataSourceAuth odsa where odsa.dataSourceId = :dataSourceId and odsa.userGroup = :userGroup

    @Transactional
    @Query(value = "delete from OfflineDataSourceAuth odsa where odsa.dataSourceId = :dataSourceId and odsa.userGroup = :userGroup")
    @Modifying
    void deleteOfflineDataSourceAuth(@Param("userGroup") String userGroup,@Param("dataSourceId") int dataSourceId);
}