package com.unistack.tamboo.mgt.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "data_flow_work", schema = "sa-mgt", catalog = "")
public class CalcInfo {
    private int dataWf;
    private int wfId;
    private int calcType;
    private String calcConf;
    private int topicIdFrom;
    private String calcId;
//    private String topicFrom;
    private String topicFromGroup;
    private int topicIdTo;
//    private String topicTo;
    private String topicToGroup;
    private int cpuNum;
    private int memSize;
    private Date createTime;
    private int flag;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "data_wf")
    public int getDataWf() {
        return dataWf;
    }

    public void setDataWf(int dataWf) {
        this.dataWf = dataWf;
    }
    @Basic
    @Column(name = "wf_id")
    public int getWfId() {
        return wfId;
    }

    public void setWfId(int wfId) {
        this.wfId = wfId;
    }
    @Basic
    @Column(name = "calc_type")
    public int getCalcType() {
        return calcType;
    }

    public void setCalcType(int calcType) {
        this.calcType = calcType;
    }
    @Basic
    @Column(name = "calc_conf")
    public String getCalcConf() {
        return calcConf;
    }

    public void setCalcConf(String calcConf) {
        this.calcConf = calcConf;
    }
    @Basic
    @Column(name = "topic_id_from")
    public int getTopicIdFrom() {
        return topicIdFrom;
    }

    public void setTopicIdFrom(int topicIdFrom) {
        this.topicIdFrom = topicIdFrom;
    }

//    public String getTopicFrom() {
//        return topicFrom;
//    }
//
//    public void setTopicFrom(String topicFrom) {
//        this.topicFrom = topicFrom;
//    }
    @Basic
    @Column(name = "topic_from_group")
    public String getTopicFromGroup() {
        return topicFromGroup;
    }

    public void setTopicFromGroup(String topicFromGroup) {
        this.topicFromGroup = topicFromGroup;
    }

    @Basic
    @Column(name = "topic_id_to")
    public int getTopicIdTo() {
        return topicIdTo;
    }

   public void setTopicIdTo(int topicIdTo) {
        this.topicIdTo = topicIdTo;
    }

//    public String getTopicTo() {
//        return topicTo;
//    }
//
//    public void setTopicTo(String topicTo) {
//        this.topicTo = topicTo;
//    }

    @Basic
    @Column(name = "topic_to_group")
   public String getTopicToGroup() {
        return topicToGroup;
    }

    public void setTopicToGroup(String topicToGroup) {
        this.topicToGroup = topicToGroup;
    }

    @Basic
    @Column(name = "cpu_num")
   public int getCpuNum() {
        return cpuNum;
    }

    public void setCpuNum(int cpuNum) {
        this.cpuNum = cpuNum;
    }

    @Basic
    @Column(name = "mem_size")
    public int getMemSize() {
        return memSize;
    }

    public void setMemSize(int memSize) {
        this.memSize = memSize;
    }
    @Basic
    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    @Basic
    @Column(name = "flag")
    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Basic
    @Column(name = "calc_id")
    public String getCalcId() {
        return calcId;
    }

    public void setCalcId(String calcId) {
        this.calcId = calcId;
    }

}
