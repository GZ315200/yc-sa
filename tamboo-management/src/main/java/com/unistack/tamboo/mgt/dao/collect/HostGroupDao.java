package com.unistack.tamboo.mgt.dao.collect;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.collect.HostGroup;

import java.util.List;

public interface HostGroupDao extends BaseDao<HostGroup, Integer> {

    List<HostGroup> getHostGroupsByHostId(Integer hostId);
}
