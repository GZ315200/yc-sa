package com.unistack.tamboo.mgt.model.monitor;


import javax.persistence.*;

/**
 * @author anning
 * @date 2018/6/6 下午4:34
 * @description: topic和groupId映射表  sink-时间戳 topic_name com.unistack.tamboo.sa.dc.connect-sink-时间戳
 */
@Entity
@Table(name = "m_topic_groupid")
public class MTopicGroupId {

    private String connectorName;
    private String topic_name;
    private String group_id;
    private Integer status;
    private Integer wfId;
    private String config;

    public void setWfId(Integer wfId) {
        this.wfId = wfId;
    }

    @Column(name = "wf_id",nullable = false)
    public Integer getWfId() {
        return wfId;
    }


    @Column(name = "config",nullable = false)
    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }


    @Column(name = "status",nullable = false)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    @Id
    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }


    @Column(name = "topic_name",nullable = false)
    public String getTopic_name() {
        return topic_name;
    }


    public void setTopic_name(String topic_name) {
        this.topic_name = topic_name;
    }

    @Column(name = "group_id",nullable = false)
    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }


}
