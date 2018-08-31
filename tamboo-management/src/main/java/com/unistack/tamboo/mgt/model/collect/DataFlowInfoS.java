package com.unistack.tamboo.mgt.model.collect;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

/**
 * @program: tamboo-sa
 * @description: 用户组剩余资源封装对象
 * @author: Asasin
 * @create: 2018-05-28 15:52
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataFlowInfoS extends DataSourceGroup{
    private String groupName;
    //剩余的cpu
    private int SurplusCpu;
    //剩余的内存
    private int SurplusMem;
    //剩余的topicNum
    private int SurplusTopicNum;

    @Override
    public String getGroupName() {
        return groupName;
    }

    @Override
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getSurplusCpu() {
        return SurplusCpu;
    }

    public void setSurplusCpu(int surplusCpu) {
        SurplusCpu = surplusCpu;
    }

    public int getSurplusMem() {
        return SurplusMem;
    }

    public void setSurplusMem(int surplusMem) {
        SurplusMem = surplusMem;
    }

    public int getSurplusTopicNum() {
        return SurplusTopicNum;
    }

    public void setSurplusTopicNum(int surplusTopicNum) {
        SurplusTopicNum = surplusTopicNum;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("groupName", groupName)
                .add("SurplusCpu", SurplusCpu)
                .add("SurplusMem", SurplusMem)
                .add("SurplusTopicNum", SurplusTopicNum)
                .toString();
    }
}
    