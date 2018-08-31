package com.unistack.tamboo.mgt.model.collect;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

/**
 * @program: tamboo-sa
 * @description: 用户组配置的和已使用的资源封装对象
 * @author: Asasin
 * @create: 2018-05-28 16:11
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataFlowInfoGU {
    private String groupName;

    //配置的cpu
    private Integer sourceCpu;
    //配置的内存
    private Integer sourceMem;
    //配置的topicNum
    private Integer topicNum;
    private Integer topicSize;
    //已使用的cpu
    private Integer usedCpu;
    //已使用的内存
    private Integer usedMem;
    //已使用的topicNum
    private Integer usedTopicNum;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getSourceCpu() {
        return sourceCpu;
    }

    public void setSourceCpu(Integer sourceCpu) {
        this.sourceCpu = sourceCpu;
    }

    public Integer getSourceMem() {
        return sourceMem;
    }

    public void setSourceMem(Integer sourceMem) {
        this.sourceMem = sourceMem;
    }

    public Integer getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(Integer topicNum) {
        this.topicNum = topicNum;
    }

    public Integer getUsedCpu() {
        return usedCpu;
    }

    public void setUsedCpu(Integer usedCpu) {
        this.usedCpu = usedCpu;
    }

    public Integer getUsedMem() {
        return usedMem;
    }

    public void setUsedMem(Integer usedMem) {
        this.usedMem = usedMem;
    }

    public Integer getUsedTopicNum() {
        return usedTopicNum;
    }

    public void setUsedTopicNum(Integer usedTopicNum) {
        this.usedTopicNum = usedTopicNum;
    }

    public Integer getTopicSize() {
        return topicSize;
    }

    public void setTopicSize(Integer topicSize) {
        this.topicSize = topicSize;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("groupName", groupName)
                .add("sourceCpu", sourceCpu)
                .add("sourceMem", sourceMem)
                .add("topicNum", topicNum)
                .add("topicSize", topicSize)
                .add("usedCpu", usedCpu)
                .add("usedMem", usedMem)
                .add("usedTopicNum", usedTopicNum)
                .toString();
    }
}
    