package com.unistack.tamboo.mgt.model.monitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/7/25
 * 封装关于集群
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MClusterServiceVo {


    /**
     * brokers的数量
     */
    private String brokersNumber;

    /**
     * 集群id
     */
    private String clusterId;

    /**
     * 集群信息
     */
    private List<BrokerInfoVo> brokerInfoVo;

    /**
     * controller的状态
     */
    @JsonProperty(value = "activeController")
    private String activeControllerCount;

    /**
     * 所有topic的总数
     */
    @JsonProperty(value = "totalTopics")
    private String globalTopicCount;

    /**
     * 所有partition的总数
     */
    @JsonProperty(value = "totalPartitions")
    private String globalPartitionCount;

    /**
     * zookeeper status
     */
    @JsonProperty(value = "zookeeper")
    private String zkState;

    /**
     * 时间戳
     */
    @JsonProperty(value = "heartbeat")
    private String timestamp;


    public String getBrokersNumber() {
        return brokersNumber;
    }

    public void setBrokersNumber(String brokersNumber) {
        this.brokersNumber = brokersNumber;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public List<BrokerInfoVo> getBrokerInfoVo() {
        return brokerInfoVo;
    }

    public void setBrokerInfoVo(List<BrokerInfoVo> brokerInfoVo) {
        this.brokerInfoVo = brokerInfoVo;
    }

    public String getActiveControllerCount() {
        return activeControllerCount;
    }

    public void setActiveControllerCount(String activeControllerCount) {
        this.activeControllerCount = activeControllerCount;
    }

    public String getZkState() {
        return zkState;
    }

    public void setZkState(String zkState) {
        this.zkState = zkState;
    }

    public String getGlobalTopicCount() {
        return globalTopicCount;
    }

    public void setGlobalTopicCount(String globalTopicCount) {
        this.globalTopicCount = globalTopicCount;
    }

    public String getGlobalPartitionCount() {
        return globalPartitionCount;
    }

    public void setGlobalPartitionCount(String globalPartitionCount) {
        this.globalPartitionCount = globalPartitionCount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("brokersNumber", brokersNumber)
                .add("clusterId", clusterId)
                .add("brokerInfoVo", brokerInfoVo)
                .add("activeControllerCount", activeControllerCount)
                .add("globalTopicCount", globalTopicCount)
                .add("globalPartitionCount", globalPartitionCount)
                .add("zkState", zkState)
                .add("timestamp", timestamp)
                .toString();
    }
}
