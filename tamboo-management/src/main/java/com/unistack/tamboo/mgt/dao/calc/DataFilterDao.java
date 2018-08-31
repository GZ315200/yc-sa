package com.unistack.tamboo.mgt.dao.calc;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.calc.DataFilter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DataFilterDao extends BaseDao<DataFilter,Integer> {

    @Query(value="select  d.filterId from DataFilter d where d.filterType = :filterType ")
    String getCustomFilterMess(@Param("filterType") String filterType);
}
