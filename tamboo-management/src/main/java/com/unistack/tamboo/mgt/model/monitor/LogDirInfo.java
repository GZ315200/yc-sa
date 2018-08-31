package com.unistack.tamboo.mgt.model.monitor;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

import java.util.List;
import java.util.Set;

/**
 * @author Gyges Zean
 * @date 2018/5/23
 */

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class LogDirInfo {

    private Integer brokerId;

    private String host;

    private Set<JSONObject> data;


    private List<TopicOffsetVo> topicOffsetVos;

    public Integer getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(Integer brokerId) {
        this.brokerId = brokerId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Set<JSONObject> getData() {
        return data;
    }

    public void setData(Set<JSONObject> data) {
        this.data = data;
    }

    public List<TopicOffsetVo> getTopicOffsetVos() {
        return topicOffsetVos;
    }

    public void setTopicOffsetVos(List<TopicOffsetVo> topicOffsetVos) {
        this.topicOffsetVos = topicOffsetVos;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("brokerId", brokerId)
                .add("host", host)
                .add("data", data)
                .add("topicOffsetVos", topicOffsetVos)
                .toString();
    }
}
