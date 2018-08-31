package com.unistack.tamboo.mgt.model.monitor;

import com.google.common.base.MoreObjects;

/**
 * @author Gyges Zean
 * @date 2017/12/6
 * 用于返回监控1小时内的数据
 */
public class MonitorRecordVo {


    private double messageRate;

    private long offsetAdd;
    /**
     * 时间戳
     */
    private Long timestamp;


    public MonitorRecordVo() {
    }


    public double getMessageRate() {
        return messageRate;
    }

    public void setMessageRate(double messageRate) {
        this.messageRate = messageRate;
    }

    public long getOffsetAdd() {
        return offsetAdd;
    }

    public void setOffsetAdd(long offsetAdd) {
        this.offsetAdd = offsetAdd;
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
                .add("messageRate", messageRate)
                .add("offsetAdd", offsetAdd)
                .add("timestamp", timestamp)
                .toString();
    }
}
