package com.unistack.tamboo.mgt.action;

import com.unistack.tamboo.mgt.helper.TopicHelper;
import com.unistack.tamboo.mgt.utils.SpringContextHolder;


public class MqAction {

    private static TopicHelper topicHelper = SpringContextHolder.getBean(TopicHelper.class);


    public static boolean mQcreateTopic(String topic, int partition, short replica, String username, String password, boolean isCreateForAcl) {
        return topicHelper.createTopicAndAcl(topic, partition, replica, username, password);
    }

    public static boolean mQdelTopic(String topic) {
        return topicHelper.deleteTopic(topic);
    }


}
