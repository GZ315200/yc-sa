package com.unistack.tamboo.mgt.model.monitor;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Gyges Zean
 * @date 2018/6/5
 */
@Entity
@Table(name = "m_offset_d", schema = "sa-mgt", catalog = "")
@IdClass(MOffsetDPK.class)
public class MOffsetD {
    private long topicId;
    private Date acceptTime;
    private Long messageRate;
    private Long offsetAdd;

    @Id
    @Column(name = "topic_id")
    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    @Basic
    @Column(name = "accept_time")
    public Date getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(Date acceptTime) {
        this.acceptTime = acceptTime;
    }

    @Basic
    @Column(name = "message_rate")
    public Long getMessageRate() {
        return messageRate;
    }

    public void setMessageRate(Long messageRate) {
        this.messageRate = messageRate;
    }

    @Id
    @Column(name = "offset_add")
    public Long getOffsetAdd() {
        return offsetAdd;
    }

    public void setOffsetAdd(Long offsetAdd) {
        this.offsetAdd = offsetAdd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MOffsetD mOffsetD = (MOffsetD) o;

        if (topicId != mOffsetD.topicId) return false;
        if (acceptTime != null ? !acceptTime.equals(mOffsetD.acceptTime) : mOffsetD.acceptTime != null) return false;
        if (messageRate != null ? !messageRate.equals(mOffsetD.messageRate) : mOffsetD.messageRate != null)
            return false;
        if (offsetAdd != null ? !offsetAdd.equals(mOffsetD.offsetAdd) : mOffsetD.offsetAdd != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (topicId ^ (topicId >>> 32));
        result = 31 * result + (acceptTime != null ? acceptTime.hashCode() : 0);
        result = 31 * result + (messageRate != null ? messageRate.hashCode() : 0);
        result = 31 * result + (offsetAdd != null ? offsetAdd.hashCode() : 0);
        return result;
    }
}
