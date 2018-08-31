package com.unistack.tamboo.mgt.dao.calc;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.calc.DataFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface dataCleanDao extends BaseDao<DataFilter,Integer> {

    @Query(value="select d from DataFilter d where d.filterName like %:filterNameOrType% or d.filterType like %:filterNameOrType% order by d.filterAddTime desc")
    Page<DataFilter> getDataFilter(Pageable pageable, @Param("filterNameOrType") String filterNameOrType);

    @Query(value = "select d.interacteParamConf from DataFilter d where d.interacteParamConf is not null order by d.filterAddTime desc")
    List<String> getDataFilterConf();
}