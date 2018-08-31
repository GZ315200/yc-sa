package com.unistack.tamboo.mgt.model.collect;

import javax.persistence.*;
import java.util.Date;

/**
 * @program: tamboo-sa
 * @description:
 * @author: Asasin
 * @create: 2018-07-20 16:45
 **/
@Entity
@Table(name = "data_source_list", schema = "sa-mgt", catalog = "")
public class DataSourceList {
    private long dataSourceId;
    private String dataSourceType;
    private String dataSourceName;
    private Long topicId;
    private String dataSourceDsc;
    private String dataModel;
    private Date createTime;
    private Integer flag;
    private Integer useNum;
    private Long httpPort;
    private String createBy;
    private Integer hostId;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "data_source_id")
    public long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @Basic
    @Column(name = "data_source_type")
    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    @Basic
    @Column(name = "data_source_name")
    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @Basic
    @Column(name = "topic_id")
    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }

    @Basic
    @Column(name = "data_source_dsc")
    public String getDataSourceDsc() {
        return dataSourceDsc;
    }

    public void setDataSourceDsc(String dataSourceDsc) {
        this.dataSourceDsc = dataSourceDsc;
    }

    @Basic
    @Column(name = "data_model")
    public String getDataModel() {
        return dataModel;
    }

    public void setDataModel(String dataModel) {
        this.dataModel = dataModel;
    }

    @Basic
    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Basic
    @Column(name = "flag")
    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    @Basic
    @Column(name = "use_num")
    public Integer getUseNum() {
        return useNum;
    }

    public void setUseNum(Integer useNum) {
        this.useNum = useNum;
    }

    @Basic
    @Column(name = "httpPort")
    public Long getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(Long httpPort) {
        this.httpPort = httpPort;
    }

    @Basic
    @Column(name = "create_by")
    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Basic
    @Column(name = "host_id")
    public Integer getHostId() {
        return hostId;
    }

    public void setHostId(Integer hostId) {
        this.hostId = hostId;
    }

    @Override
    public int hashCode() {
        int result = (int) (dataSourceId ^ (dataSourceId >>> 32));
        result = 31 * result + (dataSourceType != null ? dataSourceType.hashCode() : 0);
        result = 31 * result + (dataSourceName != null ? dataSourceName.hashCode() : 0);
        result = 31 * result + (topicId != null ? topicId.hashCode() : 0);
        result = 31 * result + (dataSourceDsc != null ? dataSourceDsc.hashCode() : 0);
        result = 31 * result + (dataModel != null ? dataModel.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (flag != null ? flag.hashCode() : 0);
        result = 31 * result + (useNum != null ? useNum.hashCode() : 0);
        result = 31 * result + (httpPort != null ? httpPort.hashCode() : 0);
        result = 31 * result + (createBy != null ? createBy.hashCode() : 0);
        result = 31 * result + (hostId != null ? hostId.hashCode() : 0);
        return result;
    }
}
