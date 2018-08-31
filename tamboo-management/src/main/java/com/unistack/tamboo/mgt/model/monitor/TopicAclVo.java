package com.unistack.tamboo.mgt.model.monitor;

import com.google.common.base.MoreObjects;

/**
 * @author Gyges Zean
 * @date 2018/5/23
 * 用于存放topic的acl信息
 */
public class TopicAclVo {

    private String topic;

    private String resourceType;

    private String permissionType;

    private String operation;

    private String principal;

    private String host;

    public TopicAclVo() {
    }


    public TopicAclVo(String topic, String resourceType, String permissionType, String operation, String principal, String host) {
        this.topic = topic;
        this.resourceType = resourceType;
        this.permissionType = permissionType;
        this.operation = operation;
        this.principal = principal;
        this.host = host;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("topic", topic)
                .add("resourceType", resourceType)
                .add("permissionType", permissionType)
                .add("operation", operation)
                .add("principal", principal)
                .add("host", host)
                .toString();
    }
}
