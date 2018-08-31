package com.unistack.tamboo.mgt.model.dataFlow;

import com.unistack.tamboo.mgt.model.collect.TopicInfo;


public class CalcNode {
    private String node;
    private int type;
    private String conf;
    private int cpu;
    private int mem;
    private TopicInfo topic_from;
    private TopicInfo topic_to;

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public int getMem() {
        return mem;
    }

    public void setMem(int mem) {
        this.mem = mem;
    }

    public TopicInfo getTopic_from() {
        return topic_from;
    }

    public void setTopic_from(TopicInfo topic_from) {
        this.topic_from = topic_from;
    }

    public TopicInfo getTopic_to() {
        return topic_to;
    }

    public void setTopic_to(TopicInfo topic_to) {
        this.topic_to = topic_to;
    }
}
