package com.unistack.tamboo.mgt.model.monitor;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author Gyges Zean
 * @date 2018/6/5
 */
@Entity
@Table(name = "m_offset_s", schema = "sa-mgt", catalog = "")
@IdClass(MOffsetSPK.class)
public class MOffsetS {
    private Long topicId;
    private Long offsetAdd;
    private Long messageRate;
    private Timestamp acceptTime;
    private String uuid;

    @Id
    @Column(name = "topic_id")
    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    @Basic
    @Column(name = "offset_add")
    public Long getOffsetAdd() {
        return offsetAdd;
    }

    public void setOffsetAdd(Long offsetAdd) {
        this.offsetAdd = offsetAdd;
    }

    @Basic
    @Column(name = "message_rate", length = 20)
    public Long getMessageRate() {
        return messageRate;
    }

    public void setMessageRate(Long messageRate) {
        this.messageRate = messageRate;
    }


    @Id
    @Column(name = "accept_time")
    public Timestamp getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(Timestamp acceptTime) {
        this.acceptTime = acceptTime;
    }

    @Id
    @Column(name = "uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("topicId", topicId)
                .add("offsetAdd", offsetAdd)
                .add("messageRate", messageRate)
                .add("acceptTime", acceptTime)
                .toString();
    }
}
