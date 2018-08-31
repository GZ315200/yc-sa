package com.unistack.tamboo.mgt.model.monitor;

import com.google.common.base.MoreObjects;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Gyges Zean
 * @date 2018/5/23
 */
@Entity
@Table(name = "m_cluster")
public class MCluster {

    private String clusterType;
    private String zookeeperUrl;
    private String servers;
    private Date timestamp;

    @Id
    @Column(name = "cluster_type")
    public String getClusterType() {
        return clusterType;
    }

    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
    }

    @Column(name = "servers")
    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }


    @Column(name = "timestamp")
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Column(name = "zookeeper_url")
    public String getZookeeperUrl() {
        return zookeeperUrl;
    }

    public void setZookeeperUrl(String zookeeperUrl) {
        this.zookeeperUrl = zookeeperUrl;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clusterType", clusterType)
                .add("servers", servers)
                .add("timestamp", timestamp)
                .toString();
    }
}
