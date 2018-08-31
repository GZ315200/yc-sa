package com.unistack.tamboo.mgt.model.monitor;

import com.google.common.base.MoreObjects;

/**
 * @author Gyges Zean
 * @date 2018/5/23
 * 用于存放topic的offset信息
 */
public class TopicOffsetVo {

    private String topic;

    private OffsetSizePartition offsetSizePartition;



    public TopicOffsetVo(String topic, OffsetSizePartition offsetSizePartition) {
        this.topic = topic;
        this.offsetSizePartition = offsetSizePartition;
    }

    public TopicOffsetVo() {
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }


    public OffsetSizePartition getOffsetSizePartition() {
        return offsetSizePartition;
    }

    public void setOffsetSizePartition(OffsetSizePartition offsetSizePartition) {
        this.offsetSizePartition = offsetSizePartition;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TopicOffsetVo that = (TopicOffsetVo) o;

        if (topic != null ? !topic.equals(that.topic) : that.topic != null) return false;
        return offsetSizePartition != null ? offsetSizePartition.equals(that.offsetSizePartition) : that.offsetSizePartition == null;
    }

    @Override
    public int hashCode() {
        int result = topic != null ? topic.hashCode() : 0;
        result = 31 * result + (offsetSizePartition != null ? offsetSizePartition.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("topic", topic)
                .add("offsetSizePartition", offsetSizePartition)
                .toString();
    }
}
