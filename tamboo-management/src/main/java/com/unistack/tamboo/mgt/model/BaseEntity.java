package com.unistack.tamboo.mgt.model;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
@MappedSuperclass
public abstract class BaseEntity {

    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;
    private String remarks;


    @PrePersist
    void createdTime() {
        this.createTime = this.updateTime = new Date();
    }

    @PreUpdate
    void updateTime() {
        this.updateTime = new Date();
    }


    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "update_time")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "create_by")
    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Column(name = "update_by")
    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    @Column(length = 100)
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("createTime", createTime)
                .add("updateTime", updateTime)
                .add("createBy", createBy)
                .add("updateBy", updateBy)
                .add("remarks", remarks)
                .toString();
    }
}
