package com.unistack.tamboo.mgt.service.dataFlow;


import com.unistack.tamboo.mgt.model.collect.TopicInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public interface TopicInfoService {

    public TopicInfo add(TopicInfo topicInfo) ;
    public int deleteById(int topic_id) ;

    int deleteByTopicNames(ArrayList<TopicInfo> topicInfos);
    TopicInfo getByTopicId(int topicId);

    void deleteByTopicIds(Long[] longs);

    TopicInfo queryTopicByDataSourceName(String table);
}
