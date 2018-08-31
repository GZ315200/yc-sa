package com.unistack.tamboo.mgt.dao.collect;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.collect.TopicGroup;

import java.util.List;


public interface TopicGroupDao extends BaseDao<TopicGroup, Integer> {
    List<TopicGroup> getTopicGroupByTopicId(long topicId);


}
