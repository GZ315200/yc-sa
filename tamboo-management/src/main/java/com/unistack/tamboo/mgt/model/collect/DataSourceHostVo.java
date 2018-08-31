package com.unistack.tamboo.mgt.model.collect;

import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * @program: tamboo-sa
 * @description: dataSourceHost封装信息
 * @author: Asasin
 * @create: 2018-07-20 14:57
 **/
public class DataSourceHostVo extends DataSourceHost {
    private DataSourceHost dataSourceHost;
    private List<String> userGroups;

    public DataSourceHost getDataSourceHost() {
        return dataSourceHost;
    }

    public void setDataSourceHost(DataSourceHost dataSourceHost) {
        this.dataSourceHost = dataSourceHost;
    }

    public List<String> getUserGroups() {
        return userGroups;
    }

    public void setUserGroups(List<String> userGroups) {
        this.userGroups = userGroups;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("dataSourceHost", dataSourceHost)
                .add("userGroups", userGroups)
                .toString();
    }
}
    