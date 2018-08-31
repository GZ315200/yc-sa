package com.unistack.tamboo.mgt.model.collect;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * @program: tamboo-sa
 * @description: 新建并启动DataSource封装对象
 * @author: Asasin
 * @create: 2018-06-02 15:56
 **/
public class DataSourceListAS {
    private DataSourceList dataSourceList;
    private DataSourceDb dataSourceDb;
    private JSONObject jsonObject;
    private List<String> userGroups;

    public DataSourceList getDataSourceList() {
        return dataSourceList;
    }

    public void setDataSourceList(DataSourceList dataSourceList) {
        this.dataSourceList = dataSourceList;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public List<String> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<String> userGroups) {
        this.userGroups = userGroups;
    }

    public DataSourceDb getDataSourceDb() {
        return dataSourceDb;
    }

    public void setDataSourceDb(DataSourceDb dataSourceDb) {
        this.dataSourceDb = dataSourceDb;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("dataSourceList", dataSourceList)
                .add("dataSourceDb", dataSourceDb)
                .add("jsonObject", jsonObject)
                .add("userGroups", userGroups)
                .toString();
    }
}

    