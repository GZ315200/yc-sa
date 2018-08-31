package com.unistack.tamboo.mgt.model.monitor;

import com.google.common.base.MoreObjects;

/**
 * @author Gyges Zean
 * @date 2018/7/25
 * 用于封装关于集群信息中，topic number，topic data size，clusterId
 */
public class BrokerInfoVo {



    /**
     * brokers的状态
     */
    private String brokerState;


    /**
     * topic数
     */
    private String topicNumber;

    /**
     * topic 数据量
     */
    private String topicDataSize;

    /**
     * 主机信息
     */
    private String host;

    /**
     * zookeeper 连接状态
     */
    private String zKState;

    /**
     * zookeeper 请求延时ms
     */
    private String zKRequestLatencyMs;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getzKState() {
        return zKState;
    }

    public void setzKState(String zKState) {
        this.zKState = zKState;
    }

    public String getzKRequestLatencyMs() {
        return zKRequestLatencyMs;
    }

    public void setzKRequestLatencyMs(String zKRequestLatencyMs) {
        this.zKRequestLatencyMs = zKRequestLatencyMs;
    }

    public String getBrokerState() {
        return brokerState;
    }

    public void setBrokerState(String brokerState) {
        this.brokerState = brokerState;
    }

    public String getTopicNumber() {
        return topicNumber;
    }

    public void setTopicNumber(String topicNumber) {
        this.topicNumber = topicNumber;
    }

    public String getTopicDataSize() {
        return topicDataSize;
    }

    public void setTopicDataSize(String topicDataSize) {
        this.topicDataSize = topicDataSize;
    }



    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("brokerState", brokerState)
                .add("topicNumber", topicNumber)
                .add("topicDataSize", topicDataSize)
                .add("host", host)
                .add("zKState", zKState)
                .add("zKRequestLatencyMs", zKRequestLatencyMs)
                .toString();
    }
}
