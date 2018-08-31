package com.unistack.tamboo.mgt.model.monitor;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Gyges Zean
 * @date 2018/6/25
 */
@Entity
@Table(name = "alert_item", schema = "sa-mgt", catalog = "")
public class AlertItem {
    private String alertName;
    private String className;
    private Integer alertInterval;
    private Boolean active;
    private Integer alertLevel;
    private String threshold;
    private Date createTime;
    private Date updateTime;

    public AlertItem() {
    }

    public AlertItem(String alertName, String className, Integer alertInterval,
                     Boolean active, Integer alertLevel, String threshold, Date createTime, Date updateTime) {
        this.alertName = alertName;
        this.className = className;
        this.alertInterval = alertInterval;
        this.active = active == null ? true : active;
        this.alertLevel = alertLevel;
        this.threshold = threshold;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }


    @Id
    @Column(name = "alert_name")
    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }

    @Basic
    @Column(name = "class_name")
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Basic
    @Column(name = "alert_interval")
    public Integer getAlertInterval() {
        return alertInterval;
    }

    public void setAlertInterval(Integer alertInterval) {
        this.alertInterval = alertInterval == null ? 10 : alertInterval;
    }

    @Basic
    @Column(name = "active")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active == null ? true : active;
    }

    @Basic
    @Column(name = "alert_level")
    public Integer getAlertLevel() {
        return alertLevel;
    }

    public void setAlertLevel(Integer alertLevel) {
        this.alertLevel = alertLevel;
    }

    @Basic
    @Column(name = "threshold")
    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    @Basic
    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime == null ? new Date() : createTime;
    }

    @Basic
    @Column(name = "update_time")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime == null ? new Date() : updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AlertItem alertItem = (AlertItem) o;

        if (alertName != null ? !alertName.equals(alertItem.alertName) : alertItem.alertName != null) return false;
        if (className != null ? !className.equals(alertItem.className) : alertItem.className != null) return false;
        if (alertInterval != null ? !alertInterval.equals(alertItem.alertInterval) : alertItem.alertInterval != null)
            return false;
        if (active != null ? !active.equals(alertItem.active) : alertItem.active != null) return false;
        if (alertLevel != null ? !alertLevel.equals(alertItem.alertLevel) : alertItem.alertLevel != null) return false;
        if (threshold != null ? !threshold.equals(alertItem.threshold) : alertItem.threshold != null) return false;
        if (createTime != null ? !createTime.equals(alertItem.createTime) : alertItem.createTime != null) return false;
        if (updateTime != null ? !updateTime.equals(alertItem.updateTime) : alertItem.updateTime != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = alertName != null ? alertName.hashCode() : 0;
        result = 31 * result + (className != null ? className.hashCode() : 0);
        result = 31 * result + (alertInterval != null ? alertInterval.hashCode() : 0);
        result = 31 * result + (active != null ? active.hashCode() : 0);
        result = 31 * result + (alertLevel != null ? alertLevel.hashCode() : 0);
        result = 31 * result + (threshold != null ? threshold.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        return result;
    }
}
