package com.unistack.tamboo.mgt.model.monitor;

import com.google.common.base.MoreObjects;

import javax.persistence.*;

/**
 * @author Gyges Zean
 * @date 2018/6/25
 */
@Entity
@Table(name = "alert_record", schema = "sa-mgt", catalog = "")
public class AlertRecord {


    private Long id;
    private Long categoryId;
    private String message;
    private Integer level;
    private String category;
    private String ip;
    private String clusterName;
    private String collectName;
    private String alertName;
    private Boolean resolved;
    private String flag;
    private Long alertCount;
    private Long timestamp;


    public AlertRecord() {
    }

    public AlertRecord(Object alertName, String category, Long categoryId) {
        this.categoryId = categoryId;
        this.timestamp = System.currentTimeMillis();
        this.category = category;
        this.alertName = alertName.getClass().getSimpleName();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "category_id")
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Basic
    @Column(name = "message")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Basic
    @Column(name = "level")
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Basic
    @Column(name = "category")
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Basic
    @Column(name = "ip")
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Basic
    @Column(name = "cluster_name")
    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    @Basic
    @Column(name = "collect_name")
    public String getCollectName() {
        return collectName;
    }

    public void setCollectName(String collectName) {
        this.collectName = collectName;
    }

    @Basic
    @Column(name = "alert_name")
    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }

    @Basic
    @Column(name = "resolved")
    public Boolean getResolved() {
        return resolved;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }

    @Basic
    @Column(name = "alert_count")
    public Long getAlertCount() {
        return alertCount;
    }

    public void setAlertCount(Long alertCount) {
        this.alertCount = alertCount;
    }

    @Basic
    @Column(name = "flag")
    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Basic
    @Column(name = "timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("categoryId", categoryId)
                .add("message", message)
                .add("level", level)
                .add("category", category)
                .add("ip", ip)
                .add("clusterName", clusterName)
                .add("collectName", collectName)
                .add("alertName", alertName)
                .add("resolved", resolved)
                .add("flag", flag)
                .add("alertCount", alertCount)
                .add("timestamp", timestamp)
                .toString();
    }
}
