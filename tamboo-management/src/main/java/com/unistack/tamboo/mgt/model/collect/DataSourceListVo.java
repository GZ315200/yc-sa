package com.unistack.tamboo.mgt.model.collect;


import com.google.common.base.MoreObjects;

/**
 * @program: tamboo-sa
 * @description: DataSourceListVo
 * @author: Asasin
 * @create: 2018-05-17 15:04
 **/
public class DataSourceListVo extends DataSourceList {
    private String dataSourceType;
    private String dataSourceName;
    private String dataSourceDsc;
    private String dataIp;
    private String dataModel;
    private Integer useNum;


    @Override
    public String getDataSourceType() {
        return dataSourceType;
    }

    @Override
    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    @Override
    public String getDataSourceName() {
        return dataSourceName;
    }

    @Override
    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    @Override
    public String getDataSourceDsc() {
        return dataSourceDsc;
    }

    @Override
    public void setDataSourceDsc(String dataSourceDsc) {
        this.dataSourceDsc = dataSourceDsc;
    }

    @Override
    public String getDataModel() {
        return dataModel;
    }

    @Override
    public void setDataModel(String dataModel) {
        this.dataModel = dataModel;
    }

    @Override
    public Integer getUseNum() {
        return useNum;
    }

    @Override
    public void setUseNum(Integer useNum) {
        this.useNum = useNum;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("dataSourceType", dataSourceType)
                .add("dataSourceName", dataSourceName)
                .add("dataSourceDsc", dataSourceDsc)
                .add("dataIp", dataIp)
                .add("dataModel", dataModel)
                .add("useNum", useNum)
                .toString();
    }
}
    