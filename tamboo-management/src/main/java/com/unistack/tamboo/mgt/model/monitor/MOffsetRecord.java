package com.unistack.tamboo.mgt.model.monitor;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Gyges Zean
 * @date 2018/5/23
 */
@Entity
@Table(name = "m_offset", schema = "sa-mgt", catalog = "")
public class MOffsetRecord {

    private long topicId;
    private Long offsetTotal;
    private Long messageRate;
    private Long peakNum;
    private Date peakTime;
    private Date createTime;


    public MOffsetRecord() {
    }

    @Id
    @Column(name = "topic_id")
    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    @Basic
    @Column(name = "offset_total")
    public Long getOffsetTotal() {
        return offsetTotal;
    }

    public void setOffsetTotal(Long offsetTotal) {
        this.offsetTotal = offsetTotal;
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
    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Column(name = "message_rate")
    public Long getMessageRate() {
        return messageRate;
    }

    public void setMessageRate(Long messageRate) {
        this.messageRate = messageRate;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("topicId", topicId)
                .add("offsetTotal", offsetTotal)
                .add("messageRate", messageRate)
                .add("peakNum", peakNum)
                .add("peakTime", peakTime)
                .add("createTime", createTime)
                .toString();
    }
}
