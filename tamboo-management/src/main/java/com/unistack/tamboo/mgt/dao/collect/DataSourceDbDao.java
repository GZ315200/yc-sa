package com.unistack.tamboo.mgt.dao.collect;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.collect.DataSourceDb;

public interface DataSourceDbDao extends BaseDao<DataSourceDb,Integer> {
    DataSourceDb getByDataSourceId(Long dataSourceId);

}
