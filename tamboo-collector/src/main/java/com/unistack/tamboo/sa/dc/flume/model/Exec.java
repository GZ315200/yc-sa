package com.unistack.tamboo.sa.dc.flume.model;

import com.google.common.base.MoreObjects;
import com.unistack.tamboo.sa.dc.flume.common.DcConfig;

/**
 * @program: tamboo-sa
 * @description: exec source model
 * @author: Asasin
 * @create: 2018-05-18 15:00
 **/
public class Exec extends DcConfig {
    private String inputCharset;
    private String command;
    //sink
    private String topic;
    private String username;
    private String password;
    private String servers;
    //开放http监控端口
    private String ip;
    private String path;
    private String user;
    private String httpPort;

    public String getIp() {

        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getInputCharset() {
        return inputCharset;
    }

    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
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
                .add("command", command)
                .add("topic", topic)
                .add("username", username)
                .add("password", password)
                .add("servers", servers)
                .add("ip", ip)
                .add("path", path)
                .add("user", user)
                .add("httpPort", httpPort)
                .toString();
    }
}
    