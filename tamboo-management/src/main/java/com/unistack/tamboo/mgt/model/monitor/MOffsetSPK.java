package com.unistack.tamboo.mgt.model.monitor;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Gyges Zean
 * @date 2018/6/5
 */
public class MOffsetSPK implements Serializable {
    private Long topicId;
    private Date acceptTime;
    private String uuid;

    @Column(name = "topic_id")
    @Id
    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
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


    @Id
    @Column(name = "uuid")
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MOffsetSPK that = (MOffsetSPK) o;

        if (topicId != null ? !topicId.equals(that.topicId) : that.topicId != null) return false;
        if (acceptTime != null ? !acceptTime.equals(that.acceptTime) : that.acceptTime != null) return false;
        return uuid != null ? uuid.equals(that.uuid) : that.uuid == null;
    }

    @Override
    public int hashCode() {
        int result = topicId != null ? topicId.hashCode() : 0;
        result = 31 * result + (acceptTime != null ? acceptTime.hashCode() : 0);
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        return result;
    }
}
