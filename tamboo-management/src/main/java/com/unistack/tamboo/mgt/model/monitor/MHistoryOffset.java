package com.unistack.tamboo.mgt.model.monitor;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author Gyges Zean
 * @date 2018/5/25
 */
@Entity
@Table(name = "m_history_offset")
public class MHistoryOffset {

    private long id;
    private Long topicId;
    private String group;
    private Integer partition;
    private Long offset;
    private Long logEndOffset;
    private Long logStartOffset;
    private String consumerId;
    private String ip;
    private Timestamp timestamp;


    public MHistoryOffset() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "topic_id")
    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    @Basic
    @Column(name = "group_id")
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Basic
    @Column(name = "h_partition")
    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    @Basic
    @Column(name = "h_offset")
    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    @Basic
    @Column(name = "log_end_offset")
    public Long getLogEndOffset() {
        return logEndOffset;
    }

    public void setLogEndOffset(Long logEndOffset) {
        this.logEndOffset = logEndOffset;
    }

    @Basic
    @Column(name = "log_start_offset")
    public Long getLogStartOffset() {
        return logStartOffset;
    }

    public void setLogStartOffset(Long logStartOffset) {
        this.logStartOffset = logStartOffset;
    }

    @Basic
    @Column(name = "consumer_id")
    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    @Basic
    @Column(name = "h_ip")
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Basic
    @Column(name = "h_timestamp")
    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("topicId", topicId)
                .add("group", group)
                .add("partition", partition)
                .add("offset", offset)
                .add("logEndOffset", logEndOffset)
                .add("logStartOffset", logStartOffset)
                .add("consumerId", consumerId)
                .add("ip", ip)
                .add("timestamp", timestamp)
                .toString();
    }
}
