package com.unistack.tamboo.mgt.model.collect;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @program: tamboo-sa
 * @description:
 * @author: Asasin
 * @create: 2018-06-05 17:48
 **/
@Entity
@Table(name = "data_flow_info", schema = "sa-mgt", catalog = "")
public class DataFlowInfo {
    private int wfId;
    private String createBy;
    private Timestamp createTime;
    private String remarks;
    private String updateBy;
    private Timestamp updateTime;
    private Integer calcCpu;
    private Integer calcMem;
    private String conf;
    private Integer dataSourceId;
    private Integer flag;
    private Integer topicNum;
    private Integer userId;
    private String wfDsc;
    private String wfName;


    public DataFlowInfo() {
    }

    public DataFlowInfo(int wfId, String createBy, Timestamp createTime, String remarks, String updateBy, Timestamp updateTime, Integer calcCpu, Integer calcMem, String conf,
                        Integer dataSourceId, Integer flag, Integer topicNum, Integer userId, String wfDsc, String wfName) {
        this.wfId = wfId;
        this.createBy = createBy;
        this.createTime = createTime;
        this.remarks = remarks;
        this.updateBy = updateBy;
        this.updateTime = updateTime;
        this.calcCpu = calcCpu == null ? 0 : calcCpu;
        this.calcMem = calcMem == null ? 0 : calcMem;
        this.conf = conf;
        this.dataSourceId = dataSourceId;
        this.flag = flag;
        this.topicNum = topicNum == null ? 0 : topicNum;
        this.userId = userId;
        this.wfDsc = wfDsc;
        this.wfName = wfName;
    }

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "wf_id")
    public int getWfId() {
        return wfId;
    }

    public void setWfId(int wfId) {
        this.wfId = wfId;
    }

    @Basic
    @Column(name = "create_by")
    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Basic
    @Column(name = "create_time")
    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Column(name = "remarks")
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @Basic
    @Column(name = "update_by")
    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    @Basic
    @Column(name = "update_time")
    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Basic
    @Column(name = "calc_cpu")
    public Integer getCalcCpu() {
        return calcCpu == null ? 0 : calcCpu;
    }

    public void setCalcCpu(Integer calcCpu) {
        this.calcCpu = calcCpu;
    }

    @Basic
    @Column(name = "calc_mem")
    public Integer getCalcMem() {
        return calcMem == null ? 0 : calcMem;
    }

    public void setCalcMem(Integer calcMem) {
        this.calcMem = calcMem;
    }

    @Basic
    @Column(name = "conf")
    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    @Basic
    @Column(name = "dataSource_id")
    public Integer getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Integer dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @Basic
    @Column(name = "flag")
    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    @Basic
    @Column(name = "topic_num")
    public Integer getTopicNum() {
        return topicNum == null ? 0 : topicNum;
    }

    public void setTopicNum(Integer topicNum) {
        this.topicNum = topicNum;
    }

    @Basic
    @Column(name = "user_id")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Basic
    @Column(name = "wf_dsc")
    public String getWfDsc() {
        return wfDsc;
    }

    public void setWfDsc(String wfDsc) {
        this.wfDsc = wfDsc;
    }

    @Basic
    @Column(name = "wf_name")
    public String getWfName() {
        return wfName;
    }

    public void setWfName(String wfName) {
        this.wfName = wfName;
    }

    @Override
    public int hashCode() {
        int result = wfId;
        result = 31 * result + (createBy != null ? createBy.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (remarks != null ? remarks.hashCode() : 0);
        result = 31 * result + (updateBy != null ? updateBy.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        result = 31 * result + (calcCpu != null ? calcCpu.hashCode() : 0);
        result = 31 * result + (calcMem != null ? calcMem.hashCode() : 0);
        result = 31 * result + (conf != null ? conf.hashCode() : 0);
        result = 31 * result + (dataSourceId != null ? dataSourceId.hashCode() : 0);
        result = 31 * result + (flag != null ? flag.hashCode() : 0);
        result = 31 * result + (topicNum != null ? topicNum.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (wfDsc != null ? wfDsc.hashCode() : 0);
        result = 31 * result + (wfName != null ? wfName.hashCode() : 0);
        return result;
    }
}
    