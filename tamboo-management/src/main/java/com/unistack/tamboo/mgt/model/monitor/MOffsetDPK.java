package com.unistack.tamboo.mgt.model.monitor;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Gyges Zean
 * @date 2018/6/5
 */
public class MOffsetDPK implements Serializable {
    private long topicId;
    private Date acceptTime;

    @Column(name = "topic_id")
    @Id
    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }


    @Column(name = "accept_time")
    @Id
    public Date getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(Date acceptTime) {
        this.acceptTime = acceptTime;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MOffsetDPK that = (MOffsetDPK) o;

        if (topicId != that.topicId) return false;
        return acceptTime != null ? acceptTime.equals(that.acceptTime) : that.acceptTime == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (topicId ^ (topicId >>> 32));
        result = 31 * result + (acceptTime != null ? acceptTime.hashCode() : 0);
        return result;
    }

}
