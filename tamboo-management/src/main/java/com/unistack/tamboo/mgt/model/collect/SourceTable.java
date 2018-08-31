package com.unistack.tamboo.mgt.model.collect;

import com.google.common.base.MoreObjects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @program: tamboo-sa
 * @description: 集群存储计算总资源medel
 * @author: Asasin
 * @create: 2018-05-24 17:56
 **/
@Entity
@Table(name = "source_table")
public class SourceTable {

    private int id;
    private int cpu;
    private int mem;
    private int topicNum;
    private int topicSize;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "cpu")
    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    @Column(name = "mem")
    public int getMem() {
        return mem;
    }

    public void setMem(int mem) {
        this.mem = mem;
    }

    @Column(name = "topic_num")
    public int getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(int topicNum) {
        this.topicNum = topicNum;
    }

    @Column(name = "topic_size")
    public int getTopicSize() {
        return topicSize;
    }

    public void setTopicSize(int topicSize) {
        this.topicSize = topicSize;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("cpu", cpu)
                .add("mem", mem)
                .add("topicNume", topicNum)
                .add("topicSize", topicSize)
                .toString();
    }
}
