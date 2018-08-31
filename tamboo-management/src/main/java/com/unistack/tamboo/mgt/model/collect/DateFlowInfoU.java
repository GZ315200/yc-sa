package com.unistack.tamboo.mgt.model.collect;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

/**
 * @program: tamboo-sa
 * @description: 用户组使用资源信息封装对象
 * @author: Asasin
 * @create: 2018-05-24 14:12
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DateFlowInfoU extends DataSourceGroup{
    private String groupName;
    //已使用的cpu
    private Integer usedCpu;
    //已使用的的mem
    private Integer usedMem;
    //已使用的topicNum
    private Integer usedTopicNum;
    //已使用的topicSize
    private Integer usedTopicSize;

    @Override
    public String getGroupName() {
        return groupName;
    }

    @Override
    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    @Override
    public Integer getTopicSize() {
        return usedTopicSize;
    }

    @Override
    public void setTopicSize(Integer usedTopicSize) {
        this.usedTopicSize = usedTopicSize;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("groupName", groupName)
                .add("usedCpu", usedCpu)
                .add("usedMem", usedMem)
                .add("usedTopicNum", usedTopicNum)
                .add("topicSize", usedTopicSize)
                .toString();
    }
}
    