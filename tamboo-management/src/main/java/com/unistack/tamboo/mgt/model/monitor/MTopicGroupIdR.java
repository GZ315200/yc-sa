package com.unistack.tamboo.mgt.model.monitor;

import java.io.Serializable;

/**
 * @author anning
 * @date 2018/6/6 下午5:33
 * @description: 返回封装类
 */
public class MTopicGroupIdR implements Serializable{
    private long topic_id;
    private String group_id;
    private long consumer_lag;
    private long total_data;
    private String consumer_ip;
    private String connect_name;


    public String getConnect_name() {
        return connect_name;
    }

    public void setConnect_name(String connect_name) {
        this.connect_name = connect_name;
    }

    public long getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(long topic_id) {
        this.topic_id = topic_id;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public long getConsumer_lag() {
        return consumer_lag;
    }

    public void setConsumer_lag(long consumer_lag) {
        this.consumer_lag = consumer_lag;
    }

    public long getTotal_data() {
        return total_data;
    }

    public void setTotal_data(long total_data) {
        this.total_data = total_data;
    }

    public String getConsumer_ip() {
        return consumer_ip;
    }

    public void setConsumer_ip(String consumer_ip) {
        this.consumer_ip = consumer_ip;
    }

    @Override
    public String toString() {
        return "MTopicGroupIdR{" +
                "topic_id=" + topic_id +
                ", group_id='" + group_id + '\'' +
                ", consumer_lag=" + consumer_lag +
                ", total_data=" + total_data +
                ", consumer_ip='" + consumer_ip + '\'' +
                ", connect_name='" + connect_name + '\'' +
                '}';
    }
}
