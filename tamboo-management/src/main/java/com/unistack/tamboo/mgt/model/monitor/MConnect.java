package com.unistack.tamboo.mgt.model.monitor;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Gyges Zean
 * @date 2018/6/7
 */
@Entity
@Table(name = "m_connect", schema = "sa-mgt", catalog = "")
public class MConnect {
    private String connectName;
    private Long topicId;
    private String groupId;
    private Long consumerLag;
    private Long peakNum;
    private Date peakTime;
    private Long messageRate;
    private Long totalData;
    private String consumerIp;
    private Date createTime;

    @Id
    @Column(name = "connect_name")
    public String getConnectName() {
        return connectName;
    }

    public void setConnectName(String connectName) {
        this.connectName = connectName;
    }

    @Basic
    @Column(name = "topic_id")
    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    @Basic
    @Column(name = "group_id")
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Basic
    @Column(name = "consumer_lag")
    public Long getConsumerLag() {
        return consumerLag;
    }

    public void setConsumerLag(Long consumerLag) {
        this.consumerLag = consumerLag;
    }

    @Basic
    @Column(name = "peak_num")
    public Long getPeakNum() {
        return peakNum;
    }

    public void setPeakNum(Long peakNum) {
        this.peakNum = peakNum;
    }

    @Basic
    @Column(name = "peak_time")
    public Date getPeakTime() {
        return peakTime;
    }

    public void setPeakTime(Date peakTime) {
        this.peakTime = peakTime;
    }

    @Basic
    @Column(name = "message_rate")
    public Long getMessageRate() {
        return messageRate;
    }

    public void setMessageRate(Long messageRate) {
        this.messageRate = messageRate;
    }

    @Basic
    @Column(name = "total_data")
    public Long getTotalData() {
        return totalData;
    }

    public void setTotalData(Long totalData) {
        this.totalData = totalData;
    }

    @Basic
    @Column(name = "consumer_ip")
    public String getConsumerIp() {
        return consumerIp;
    }

    public void setConsumerIp(String consumerIp) {
        this.consumerIp = consumerIp;
    }

    @Basic
    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MConnect mConnect = (MConnect) o;

        if (connectName != null ? !connectName.equals(mConnect.connectName) : mConnect.connectName != null)
            return false;
        if (topicId != null ? !topicId.equals(mConnect.topicId) : mConnect.topicId != null) return false;
        if (groupId != null ? !groupId.equals(mConnect.groupId) : mConnect.groupId != null) return false;
        if (consumerLag != null ? !consumerLag.equals(mConnect.consumerLag) : mConnect.consumerLag != null)
            return false;
        if (peakNum != null ? !peakNum.equals(mConnect.peakNum) : mConnect.peakNum != null) return false;
        if (peakTime != null ? !peakTime.equals(mConnect.peakTime) : mConnect.peakTime != null) return false;
        if (messageRate != null ? !messageRate.equals(mConnect.messageRate) : mConnect.messageRate != null)
            return false;
        if (totalData != null ? !totalData.equals(mConnect.totalData) : mConnect.totalData != null) return false;
        if (consumerIp != null ? !consumerIp.equals(mConnect.consumerIp) : mConnect.consumerIp != null) return false;
        if (createTime != null ? !createTime.equals(mConnect.createTime) : mConnect.createTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = connectName != null ? connectName.hashCode() : 0;
        result = 31 * result + (topicId != null ? topicId.hashCode() : 0);
        result = 31 * result + (groupId != null ? groupId.hashCode() : 0);
        result = 31 * result + (consumerLag != null ? consumerLag.hashCode() : 0);
        result = 31 * result + (peakNum != null ? peakNum.hashCode() : 0);
        result = 31 * result + (peakTime != null ? peakTime.hashCode() : 0);
        result = 31 * result + (messageRate != null ? messageRate.hashCode() : 0);
        result = 31 * result + (totalData != null ? totalData.hashCode() : 0);
        result = 31 * result + (consumerIp != null ? consumerIp.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        return result;
    }
}
