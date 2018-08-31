package com.unistack.tamboo.mgt.service.dataFlow;

import com.unistack.tamboo.mgt.model.collect.TopicGroup;
import org.springframework.stereotype.Service;

@Service
public interface TopicGroupService {
    void save(TopicGroup topicGroup);
}
