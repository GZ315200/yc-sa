package com.unistack.tamboo.mgt.model.monitor;

import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/5/23
 */
public class TopicPartitionVo {

    private String topic;

    private boolean internal;

    private List<Integer> partitions;


    public TopicPartitionVo() {
    }

    public TopicPartitionVo(String topic, boolean internal, List<Integer> partitions) {
        this.topic = topic;
        this.internal = internal;
        this.partitions = partitions;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }

    public List<Integer> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<Integer> partitions) {
        this.partitions = partitions;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("topic", topic)
                .add("internal", internal)
                .add("partitions", partitions)
                .toString();
    }
}
