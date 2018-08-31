package com.unistack.tamboo.mgt.dao.monitor;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.monitor.MTopicGroupId;

import java.util.List;

/**
 * @author anning
 * @date 2018/6/6 下午4:30
 * @description:
 */
public interface MTopicGroupIdDao extends BaseDao<MTopicGroupId, String> {
    MTopicGroupId findByConnectorName(String connector_name);

    List<MTopicGroupId> findByStatus(Integer status);

    List<MTopicGroupId> findByWfId(Integer wf_id);
}
