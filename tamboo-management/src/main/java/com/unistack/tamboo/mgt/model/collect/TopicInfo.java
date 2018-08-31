package com.unistack.tamboo.mgt.model.collect;

import com.google.common.base.MoreObjects;

import javax.persistence.*;

/**
 * @program: tamboo-sa
 * @description:
 * @author: Asasin
 * @create: 2018-05-22 15:56
 **/
@Entity
@Table(name = "topic_info", schema = "sa-mgt", catalog = "")
public class TopicInfo {
    private long topicId;
    private String topicName;
    private Integer topicPartition;
    private Integer topicReplication;
    private String topicAclType;
    private String topicInfo;
    private String topicAclUsername;
    private String topicAclPassword;
    private String topicMsg;
    private String conf;
    private Integer topicType;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "topic_id")
    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    @Basic
    @Column(name = "topic_partition")
    public Integer getTopicPartition() {
        return topicPartition;
    }

    public void setTopicPartition(Integer topicPartition) {
        this.topicPartition = topicPartition;
    }

    @Basic
    @Column(name = "topic_replication")
    public Integer getTopicReplication() {
        return topicReplication;
    }

    public void setTopicReplication(Integer topicReplication) {
        this.topicReplication = topicReplication;
    }

    @Basic
    @Column(name = "topic_acl_type")
    public String getTopicAclType() {
        return topicAclType;
    }

    public void setTopicAclType(String topicAclType) {
        this.topicAclType = topicAclType;
    }

    @Basic
    @Column(name = "topic_info")
    public String getTopicInfo() {
        return topicInfo;
    }

    public void setTopicInfo(String topicInfo) {
        this.topicInfo = topicInfo;
    }

    @Basic
    @Column(name = "topic_acl_username")
    public String getTopicAclUsername() {
        return topicAclUsername;
    }

    public void setTopicAclUsername(String topicAclUsername) {
        this.topicAclUsername = topicAclUsername;
    }

    @Basic
    @Column(name = "topic_acl_password")
    public String getTopicAclPassword() {
        return topicAclPassword;
    }

    public void setTopicAclPassword(String topicAclPassword) {
        this.topicAclPassword = topicAclPassword;
    }

    @Basic
    @Column(name = "topic_name")
    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    @Basic
    @Column(name = "topic_msg")
    public String getTopicMsg() {
        return topicMsg;
    }

    public void setTopicMsg(String topicMsg) {
        this.topicMsg = topicMsg;
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
    @Column(name = "topic_type")
    public Integer getTopicType() {
        return topicType;
    }

    public void setTopicType(Integer topicType) {
        this.topicType = topicType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("topicId", topicId)
                .add("topicPartition", topicPartition)
                .add("topicReplication", topicReplication)
                .add("topicAclType", topicAclType)
                .add("topicInfo", topicInfo)
                .add("topicAclUsername", topicAclUsername)
                .add("topicAclPassword", topicAclPassword)
                .add("topicName", topicName)
                .add("topicMsg", topicMsg)
                .add("conf", conf)
                .add("topicType", topicType)
                .toString();
    }
}
    