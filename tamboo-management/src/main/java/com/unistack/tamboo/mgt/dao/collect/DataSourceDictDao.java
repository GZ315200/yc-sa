package com.unistack.tamboo.mgt.dao.collect;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.collect.DataSourceDict;


import java.util.Set;

public interface DataSourceDictDao extends BaseDao<DataSourceDict,Long> {

    Set<DataSourceDict> findByDataSourceType(String dataSourceType);

}
