package com.unistack.tamboo.mgt.model.monitor;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Gyges Zean
 * @date 2018/5/29
 */
@Entity
@Table(name = "m_spark_message", schema = "sa-mgt", catalog = "")
public class MSparkMessage {
    private String appId;
    private Long totalCalSize;
    private Long peakCalSize;
    private Date peakTime;
    private Long latency;
    private Long calRate;
    private Date timestamp;

    @Id
    @Column(name = "app_id")
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Basic
    @Column(name = "total_cal_size")
    public Long getTotalCalSize() {
        return totalCalSize;
    }

    public void setTotalCalSize(Long totalCalSize) {
        this.totalCalSize = totalCalSize;
    }

    @Column(name = "latency")
    public Long getLatency() {
        return latency;
    }

    public void setLatency(Long latency) {
        this.latency = latency;
    }

    @Basic
    @Column(name = "peak_cal_size")
    public Long getPeakCalSize() {
        return peakCalSize;
    }

    public void setPeakCalSize(Long peakCalSize) {
        this.peakCalSize = peakCalSize;
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
    @Column(name = "cal_rate")
    public Long getCalRate() {
        return calRate;
    }

    public void setCalRate(Long calRate) {
        this.calRate = calRate;
    }

    @Basic
    @Column(name = "timestamp")
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("appId", appId)
                .add("totalCalSize", totalCalSize)
                .add("peakCalSize", peakCalSize)
                .add("peakTime", peakTime)
                .add("latency", latency)
                .add("calRate", calRate)
                .add("timestamp", timestamp)
                .toString();
    }
}
