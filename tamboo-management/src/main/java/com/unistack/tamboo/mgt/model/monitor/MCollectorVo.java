package com.unistack.tamboo.mgt.model.monitor;

import com.google.common.base.MoreObjects;

/**
 * @program: tamboo-sa
 * @description: 监控信息封装类
 * @author: Asasin
 * @create: 2018-06-28 19:46
 **/
public class MCollectorVo {
    private Long dataSourceId;
    private Long topicId;
    private String topicName;
    private String url;
    private String type;

    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("dataSourceId", dataSourceId)
                .add("topicId", topicId)
                .add("topicName", topicName)
                .add("url", url)
                .add("type", type)
                .toString();
    }
}
    