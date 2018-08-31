package com.unistack.tamboo.mgt.model.collect;

import com.google.common.base.MoreObjects;

import javax.persistence.*;

/**
 * @program: tamboo-sa
 * @description:
 * @author: Asasin
 * @create: 2018-05-17 15:26
 **/
@Entity
@Table(name = "data_source_dict", schema = "sa-mgt", catalog = "")
public class DataSourceDict {
    private int id;
    private String dataSourceType;
    private String confName;
    private Byte isneed;

    public void setId(Integer id) {
        this.id = id;
    }

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    @Column(name = "conf_name")
    public String getConfName() {
        return confName;
    }

    public void setConfName(String confName) {
        this.confName = confName;
    }

    @Basic
    @Column(name = "isneed")
    public Byte getIsneed() {
        return isneed;
    }

    public void setIsneed(Byte isneed) {
        this.isneed = isneed;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("dataSourceType", dataSourceType)
                .add("confName", confName)
                .add("isneed", isneed)
                .toString();
    }

    @Override
    public int hashCode() {
        int result = dataSourceType != null ? dataSourceType.hashCode() : 0;
        result = 31 * result + (confName != null ? confName.hashCode() : 0);
        result = 31 * result + (isneed != null ? isneed.hashCode() : 0);
        result = 31 * result + id;
        return result;
    }
}
    