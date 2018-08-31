package com.unistack.tamboo.mgt.model.monitor;

import com.google.common.base.MoreObjects;

/**
 * @author Gyges Zean
 * @date 2018/7/27
 * 用于封装zookeeper的host,port,jmxPort信息
 */
public class ZookeeperInfo {

    private String host;

    private String jmxPort;

    public ZookeeperInfo() {
    }

    public ZookeeperInfo(String host, String jmxPort) {
        this.host = host;
        this.jmxPort = jmxPort;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getJmxPort() {
        return jmxPort;
    }

    public void setJmxPort(String jmxPort) {
        this.jmxPort = jmxPort;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("host", host)
                .add("jmxPort", jmxPort)
                .toString();
    }
}
