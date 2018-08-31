package com.unistack.tamboo.mgt.dao.collect;

import com.unistack.tamboo.mgt.dao.BaseDao;
import com.unistack.tamboo.mgt.model.collect.TopicInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;

public interface TopicInfoDao extends BaseDao<TopicInfo, Long> {

    /**
     * 根据topicType查询topicInfo
     *
     * @param topicTypes
     * @return
     */
    List<TopicInfo> getTopicInfosByTopicType(Integer topicTypes);

    /**
     * 根据topicType查询数据源Topic和开放Topic
     *
     * @param topicType
     * @return
     */
    List<TopicInfo> findByTopicTypeNot(int topicType);


    TopicInfo getTopicInfoByTopicName(String topicName);

    TopicInfo getTopicInfoByTopicId(long topicId);

    int countTopicInfoByTopicName(String topicName);

//    TopicInfo add(TopicInfo topicInfo);

    int deleteByTopicId(int topic_id);

    int deleteByTopicName(ArrayList<TopicInfo> topicInfos);

    int countTopicInfoByTopicId(long topicId);

    @Query(value="select t from TopicInfo t , DataSourceList d where d.topicId=t.topicId and d.dataSourceName=:dbSourceName ")
    TopicInfo queryTopicByDataSourceName(@Param("dbSourceName")String dbSourceName);
}


