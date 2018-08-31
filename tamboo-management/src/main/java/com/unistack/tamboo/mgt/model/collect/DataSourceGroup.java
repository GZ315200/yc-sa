package com.unistack.tamboo.mgt.model.collect;

import com.google.common.base.MoreObjects;

import javax.persistence.*;

/**
 * @program: tamboo-sa
 * @description:
 * @author: Asasin
 * @create: 2018-06-05 16:09
 **/
@Entity
@Table(name = "data_source_group", schema = "sa-mgt", catalog = "")
public class DataSourceGroup {
    private String groupName;
    private Integer calcSourceCpu;
    private Integer calcSourceMem;

    private Integer topicNum;
    private Integer topicSize;
    private String description;

    @Id
    @Column(name = "group_name")
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Basic
    @Column(name = "calc_source_cpu")
    public Integer getCalcSourceCpu() {
        return calcSourceCpu;
    }

    public void setCalcSourceCpu(Integer calcSourceCpu) {
        this.calcSourceCpu = calcSourceCpu;
    }

    @Basic
    @Column(name = "calc_source_mem")
    public Integer getCalcSourceMem() {
        return calcSourceMem;
    }

    public void setCalcSourceMem(Integer calcSourceMem) {
        this.calcSourceMem = calcSourceMem;
    }

    @Basic
    @Column(name = "topic_num")
    public Integer getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(Integer topicNum) {
        this.topicNum = topicNum;
    }

    @Basic
    @Column(name = "topic_size")
    public Integer getTopicSize() {
        return topicSize;
    }

    public void setTopicSize(Integer topicSize) {
        this.topicSize = topicSize;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("groupName", groupName)
                .add("calcSourceCpu", calcSourceCpu)
                .add("calcSourceMem", calcSourceMem)
                .add("topicNum", topicNum)
                .add("topicSize", topicSize)
                .add("description", description)
                .toString();
    }
}
    