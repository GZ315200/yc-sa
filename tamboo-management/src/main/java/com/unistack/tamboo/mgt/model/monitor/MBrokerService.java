package com.unistack.tamboo.mgt.model.monitor;

import javax.persistence.*;

/**
 * @author Gyges Zean
 * @date 2018/7/26
 */
@Entity
@Table(name = "m_broker_service", schema = "sa-mgt", catalog = "")
public class MBrokerService {
    private String host;
    private String brokersNumber;
    private String clusterId;
    private String activeControllerCount;
    private String globalTopicCount;
    private String globalPartitionCount;
    private String timestamp;
    private String brokerState;
    private String topicNumber;
    private String topicDataSize;
    private String zKState;
    private String zKRequestLatencyMs;

    @Id
    @Column(name = "host")
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Basic
    @Column(name = "brokersNumber")
    public String getBrokersNumber() {
        return brokersNumber;
    }

    public void setBrokersNumber(String brokersNumber) {
        this.brokersNumber = brokersNumber;
    }

    @Basic
    @Column(name = "clusterId")
    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    @Basic
    @Column(name = "activeControllerCount")
    public String getActiveControllerCount() {
        return activeControllerCount;
    }

    public void setActiveControllerCount(String activeControllerCount) {
        this.activeControllerCount = activeControllerCount;
    }

    @Basic
    @Column(name = "globalTopicCount")
    public String getGlobalTopicCount() {
        return globalTopicCount;
    }

    public void setGlobalTopicCount(String globalTopicCount) {
        this.globalTopicCount = globalTopicCount;
    }

    @Basic
    @Column(name = "globalPartitionCount")
    public String getGlobalPartitionCount() {
        return globalPartitionCount;
    }

    public void setGlobalPartitionCount(String globalPartitionCount) {
        this.globalPartitionCount = globalPartitionCount;
    }

    @Basic
    @Column(name = "timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Basic
    @Column(name = "brokerState")
    public String getBrokerState() {
        return brokerState;
    }

    public void setBrokerState(String brokerState) {
        this.brokerState = brokerState;
    }

    @Basic
    @Column(name = "topicNumber")
    public String getTopicNumber() {
        return topicNumber;
    }

    public void setTopicNumber(String topicNumber) {
        this.topicNumber = topicNumber;
    }

    @Basic
    @Column(name = "topicDataSize")
    public String getTopicDataSize() {
        return topicDataSize;
    }

    public void setTopicDataSize(String topicDataSize) {
        this.topicDataSize = topicDataSize;
    }

    @Basic
    @Column(name = "zKState")
    public String getzKState() {
        return zKState;
    }

    public void setzKState(String zKState) {
        this.zKState = zKState;
    }

    @Basic
    @Column(name = "zKRequestLatencyMs")
    public String getzKRequestLatencyMs() {
        return zKRequestLatencyMs;
    }

    public void setzKRequestLatencyMs(String zKRequestLatencyMs) {
        this.zKRequestLatencyMs = zKRequestLatencyMs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MBrokerService that = (MBrokerService) o;

        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        if (brokersNumber != null ? !brokersNumber.equals(that.brokersNumber) : that.brokersNumber != null)
            return false;
        if (clusterId != null ? !clusterId.equals(that.clusterId) : that.clusterId != null) return false;
        if (activeControllerCount != null ? !activeControllerCount.equals(that.activeControllerCount) : that.activeControllerCount != null)
            return false;
        if (globalTopicCount != null ? !globalTopicCount.equals(that.globalTopicCount) : that.globalTopicCount != null)
            return false;
        if (globalPartitionCount != null ? !globalPartitionCount.equals(that.globalPartitionCount) : that.globalPartitionCount != null)
            return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        if (brokerState != null ? !brokerState.equals(that.brokerState) : that.brokerState != null) return false;
        if (topicNumber != null ? !topicNumber.equals(that.topicNumber) : that.topicNumber != null) return false;
        if (topicDataSize != null ? !topicDataSize.equals(that.topicDataSize) : that.topicDataSize != null)
            return false;
        if (zKState != null ? !zKState.equals(that.zKState) : that.zKState != null) return false;
        if (zKRequestLatencyMs != null ? !zKRequestLatencyMs.equals(that.zKRequestLatencyMs) : that.zKRequestLatencyMs != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + (brokersNumber != null ? brokersNumber.hashCode() : 0);
        result = 31 * result + (clusterId != null ? clusterId.hashCode() : 0);
        result = 31 * result + (activeControllerCount != null ? activeControllerCount.hashCode() : 0);
        result = 31 * result + (globalTopicCount != null ? globalTopicCount.hashCode() : 0);
        result = 31 * result + (globalPartitionCount != null ? globalPartitionCount.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (brokerState != null ? brokerState.hashCode() : 0);
        result = 31 * result + (topicNumber != null ? topicNumber.hashCode() : 0);
        result = 31 * result + (topicDataSize != null ? topicDataSize.hashCode() : 0);
        result = 31 * result + (zKState != null ? zKState.hashCode() : 0);
        result = 31 * result + (zKRequestLatencyMs != null ? zKRequestLatencyMs.hashCode() : 0);
        return result;
    }
}
