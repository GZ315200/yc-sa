package com.unistack.tamboo.mgt.service.dataFlow;

import com.unistack.tamboo.mgt.dao.collect.TopicInfoDao;
import com.unistack.tamboo.mgt.model.collect.TopicInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class TopicInfoServiceImpl implements TopicInfoService {


    @Autowired
    TopicInfoDao topicInfoDao;

    @Override
    public TopicInfo add(TopicInfo topicInfo) {
        return topicInfoDao.saveAndFlush(topicInfo);
    }

    @Override
    public int deleteById(int topic_id) {
        return topicInfoDao.deleteByTopicId(topic_id);
    }

    @Override
    public int deleteByTopicNames(ArrayList<TopicInfo> topicInfos) {
         topicInfoDao.delete(topicInfos);
        return 1;
    }

    @Override
    public TopicInfo getByTopicId(int topicId) {
        return topicInfoDao.getOne((long)topicId);
    }

    @Override
    public void deleteByTopicIds(Long[] longs) {

    }

    @Override
    public TopicInfo queryTopicByDataSourceName(String dbSourceName) {
        return topicInfoDao.queryTopicByDataSourceName(dbSourceName);
    }
}
