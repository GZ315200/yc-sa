package com.unistack.tamboo.mgt.model.calc;

import com.unistack.tamboo.commons.utils.calc.DbMetaDataUtil;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author hero.li
 */
@Entity
@Table(name = "offline_data_source", schema = "sa-mgt", catalog = "")
public class OfflineDataSource {
    private int dataSourceId;
    private String dataSourceName;
    private String dataSourceDesc;
    private String dbType;
    private String ip;
    private String port;
    private String dbName;
    private String username;
    private String password;
    private Timestamp addTime;
    private String creator;
    private String isActive;
    private String remark1;
    private String remark2;
    private String remark3;
    private String remark4;
    private String remark5;


    @GeneratedValue(strategy= GenerationType.AUTO)
    @Id
    @Column(name = "data_source_id")
    public int getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(int dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @Column(name="data_source_name")
    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @Column(name="data_source_desc")
    public String getDataSourceDesc() {
        return dataSourceDesc;
    }

    public void setDataSourceDesc(String dataSourceDesc) {
        this.dataSourceDesc = dataSourceDesc;
    }

    @Column(name = "ip")
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Column(name = "port")
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Column(name = "db_name")
    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Column(name="username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name="password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name="add_time")
    public Timestamp getAddTime() {
        return addTime;
    }

    public void setAddTime(Timestamp addTime) {
        this.addTime = addTime;
    }

    @Column(name="creator")
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Column(name="is_active")
    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    @Column(name="remark1")
    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1;
    }

    @Column(name="remark2")
    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2;
    }

    @Column(name="remark3")
    public String getRemark3() {
        return remark3;
    }

    public void setRemark3(String remark3) {
        this.remark3 = remark3;
    }

    @Column(name="remark4")
    public String getRemark4() {
        return remark4;
    }

    public void setRemark4(String remark4) {
        this.remark4 = remark4;
    }

    @Column(name="remark5")
    public String getRemark5() {
        return remark5;
    }

    public void setRemark5(String remark5) {
        this.remark5 = remark5;
    }

    @Column(name="db_type")
    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String toUrl(){
        return DbMetaDataUtil.toUrl(dbType,ip,port,dbName);
    }
}