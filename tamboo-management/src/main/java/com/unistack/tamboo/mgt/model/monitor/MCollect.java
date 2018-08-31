package com.unistack.tamboo.mgt.model.monitor;

import com.google.common.base.MoreObjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Gyges Zean
 * @date 2018/5/23
 */
@Entity
@Table(name = "m_collect")
public class MCollect {

    private String collectType;
    private Long dataSourceId;
    private Long peakColSize;
    private Long totalColSize;
    private Date peakTime;
    private Long colRate;
    private Date timestamp;
    private Long topicId;


    public MCollect() {
    }


    @Column(name = "collect_type")
    public String getCollectType() {
        return collectType;
    }

    public void setCollectType(String collectType) {
        this.collectType = collectType;
    }


    @Column(name = "data_source_id")
    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @Column(name = "peak_col_size")
    public Long getPeakColSize() {
        return peakColSize;
    }

    public void setPeakColSize(Long peakColSize) {
        this.peakColSize = peakColSize;
    }

    @Column(name = "total_col_size")
    public Long getTotalColSize() {
        return totalColSize;
    }

    public void setTotalColSize(Long totalColSize) {
        this.totalColSize = totalColSize;
    }

    @Column(name = "peak_time")
    public Date getPeakTime() {
        return peakTime;
    }

    public void setPeakTime(Date peakTime) {
        this.peakTime = peakTime;
    }


    @Column(name = "col_rate")
    public Long getColRate() {
        return colRate;
    }

    public void setColRate(Long colRate) {
        this.colRate = colRate;
    }

    @Column(name = "timestamp")
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Id
    @Column(name = "topic_id")
    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("collectType", collectType)
                .add("dataSourceId", dataSourceId)
                .add("peakColSize", peakColSize)
                .add("totalColSize", totalColSize)
                .add("peakTime", peakTime)
                .add("colRate", colRate)
                .add("timestamp", timestamp)
                .add("topicId", topicId)
                .toString();
    }
}
