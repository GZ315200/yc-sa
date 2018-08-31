package com.unistack.tamboo.mgt.model.calc;

import javax.persistence.*;
import java.sql.Timestamp;


/**
 * 数据源权限
 */
@Entity
@Table(name = "offline_data_source_auth", schema = "sa-mgt", catalog = "")
public class OfflineDataSourceAuth {
    private int offlineDataSourceAuthId;
    private String userGroup;
    private int dataSourceId;
    private Timestamp addTime;
    private Timestamp updateTime;
    private String isActive;
    private String creator;
    private String remark1;
    private String remark2;
    private String remark3;


    @GeneratedValue(strategy=GenerationType.AUTO)
    @Id
    @Column(name = "offline_data_source_auth_id")
    public int getOfflineDataSourceAuthId(){
        return offlineDataSourceAuthId;
    }

    public void setOfflineDataSourceAuthId(int offlineDataSourceAuthId) {
        this.offlineDataSourceAuthId = offlineDataSourceAuthId;
    }

    @Column(name = "user_group")
    public String getUserGroup(){
        return userGroup;
    }

    public void setUserGroup(String userGroup){
        this.userGroup= userGroup;
    }

    @Column(name = "data_source_id")
    public int getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(int dataSourceId){
        this.dataSourceId = dataSourceId;
    }

    @Column(name = "add_time")
    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime){
        this.addTime = addTime;
    }

    @Column(name = "update_time")
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Column(name = "is_active")
    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    @Column(name = "creator")
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Column(name = "remark1")
    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    @Column(name = "remark2")
    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2){
        this.remark2 = remark2;
    }

    @Column(name = "remark3")
    public String getRemark3() {
        return remark3;
    }

    public void setRemark3(String remark3) {
        this.remark3 = remark3;
    }
}
