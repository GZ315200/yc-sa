package com.unistack.tamboo.mgt.model.monitor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * @author Gyges Zean
 * @date 2018/5/25
 */
public class Throughput {

    @JsonProperty(value = "total_offset")
    private Long totalOffset;

    @JsonProperty(value = "message_rate")
    private Long messageRate;

    private Long timestamp;

    public Throughput() {
    }

    public Throughput(Long totalOffset, Long messageRate, Long timestamp) {
        this.totalOffset = totalOffset;
        this.messageRate = messageRate;
        this.timestamp = timestamp;
    }

    public Long getTotalOffset() {
        return totalOffset;
    }

    public void setTotalOffset(Long totalOffset) {
        this.totalOffset = totalOffset;
    }

    public Long getMessageRate() {
        return messageRate;
    }

    public void setMessageRate(Long messageRate) {
        this.messageRate = messageRate;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("totalOffset", totalOffset)
                .add("messageRate", messageRate)
                .add("timestamp", timestamp)
                .toString();
    }
}
