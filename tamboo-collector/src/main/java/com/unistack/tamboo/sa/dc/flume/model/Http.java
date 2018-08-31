package com.unistack.tamboo.sa.dc.flume.model;

import com.google.common.base.MoreObjects;
import com.unistack.tamboo.sa.dc.flume.common.DcConfig;

/**
 * @program: tamboo-sa
 * @description: http source实体类
 * @author: Asasin
 * @create: 2018-08-08 15:22
 **/
public class Http extends DcConfig {
    //source
    private String inputCharset;
    private String bind;
    private String port;
    //sink
    private String topic;
    private String username;
    private String password;
    private String servers;
    //开放http监控端口
    private String path;
    private String httpPort;

    public String getInputCharset() {
        return inputCharset;
    }

    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
    }

    public String getBind() {
        return bind;
    }

    public void setBind(String bind) {
        this.bind = bind;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServers() {
        return servers;
    }

    public void setServers(String servers) {
        this.servers = servers;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(String httpPort) {
        this.httpPort = httpPort;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("inputCharset", inputCharset)
                .add("bind", bind)
                .add("port", port)
                .add("topic", topic)
                .add("username", username)
                .add("password", password)
                .add("servers", servers)
                .add("path", path)
                .add("httpPort", httpPort)
                .toString();
    }
}
    