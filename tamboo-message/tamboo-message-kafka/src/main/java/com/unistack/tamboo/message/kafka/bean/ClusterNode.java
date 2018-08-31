package com.unistack.tamboo.message.kafka.bean;

import com.google.common.base.MoreObjects;

/**
 * @author Gyges Zean
 * @date 2018/5/7
 *
 * the detail of all of cluster node,hosts & nodeId
 */
public class ClusterNode {

    private String hosts; // ip和端口号

    private String idString; // 属于主机的id

    private String rack; //使用 rack来平衡副本，broker 配置example : broker.rack = "RACK1"

    public ClusterNode() {

    }

    public ClusterNode(String hosts, String idString, String rack) {
        this.hosts = hosts;
        this.idString = idString;
        this.rack = rack;
    }

    public String getRack() {
        return rack;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClusterNode that = (ClusterNode) o;

        if (hosts != null ? !hosts.equals(that.hosts) : that.hosts != null) return false;
        if (idString != null ? !idString.equals(that.idString) : that.idString != null) return false;
        return rack != null ? rack.equals(that.rack) : that.rack == null;
    }

    @Override
    public int hashCode() {
        int result = hosts != null ? hosts.hashCode() : 0;
        result = 31 * result + (idString != null ? idString.hashCode() : 0);
        result = 31 * result + (rack != null ? rack.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("hosts", hosts)
                .add("idString", idString)
                .add("rack", rack)
                .toString();
    }
}
