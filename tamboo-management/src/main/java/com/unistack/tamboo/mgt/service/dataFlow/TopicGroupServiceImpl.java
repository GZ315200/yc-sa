package com.unistack.tamboo.mgt.service.dataFlow;

import com.unistack.tamboo.mgt.dao.collect.TopicGroupDao;
import com.unistack.tamboo.mgt.model.collect.TopicGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TopicGroupServiceImpl implements TopicGroupService{

    @Autowired
    TopicGroupDao topicGroupDao;

    @Override
    public void save(TopicGroup topicGroup) {
        topicGroupDao.save(topicGroup);
    }
}
