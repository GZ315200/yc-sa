package com.unistack.tamboo.mgt.model.collect;

import javax.persistence.*;

/**
 * @program: tamboo-sa
 * @description:
 * @author: Asasin
 * @create: 2018-06-07 15:40
 **/
@Entity
@Table(name = "data_source_conf", schema = "sa-mgt", catalog = "")
public class DataSourceConf {
    private Long dataSourceId;
    private String dataKey;
    private String dataValue;
    private Long id;

    @Basic
    @Column(name = "data_source_id")
    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    @Basic
    @Column(name = "data_key")
    public String getDataKey() {
        return dataKey;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    @Basic
    @Column(name = "data_value")
    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int result = dataSourceId != null ? dataSourceId.hashCode() : 0;
        result = 31 * result + (dataKey != null ? dataKey.hashCode() : 0);
        result = 31 * result + (dataValue != null ? dataValue.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
    