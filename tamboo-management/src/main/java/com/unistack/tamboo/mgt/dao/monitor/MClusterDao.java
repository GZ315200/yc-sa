package com.unistack.tamboo.mgt.dao.monitor;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.monitor.MCluster;

/**
 * @author Gyges Zean
 * @date 2018/5/23
 */
public interface MClusterDao extends BaseDao<MCluster, String> {

    MCluster findMClusterByClusterType(String clusterType);
}
