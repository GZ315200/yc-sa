package com.unistack.tamboo.sa.dc.flume.model;

import com.google.common.base.MoreObjects;
import com.unistack.tamboo.sa.dc.flume.common.DcConfig;

import java.util.List;
import java.util.Map;

/**
 * @program: tamboo-sa
 * @description: taildir source model
 * @author: Asasin
 * @create: 2018-05-16 11:06
 **/
public class TailDir extends DcConfig {
    //source
    private String positionFile;
    private String filegroups;
    private Map<String,String> filegroupsPath;
    private String batchSize;
    private String batchDurationMillis;
    private String fileHeader;
    private String inputCharset;

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

    public String getInputCharset() {
        return inputCharset;
    }

    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
    }

    public Map<String, String> getFilegroupsPath() {
        return filegroupsPath;
    }

    public void setFilegroupsPath(Map<String, String> filegroupsPath) {
        this.filegroupsPath = filegroupsPath;
    }

    public String getPositionFile() {
        return positionFile;
    }

    public void setPositionFile(String positionFile) {
        this.positionFile = positionFile;
    }

    public String getFilegroups() {
        return filegroups;
    }

    public void setFilegroups(String filegroups) {
        this.filegroups = filegroups;
    }

    public String getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(String batchSize) {
        this.batchSize = batchSize;
    }

    public String getBatchDurationMillis() {
        return batchDurationMillis;
    }

    public void setBatchDurationMillis(String batchDurationMillis) {
        this.batchDurationMillis = batchDurationMillis;
    }

    public String getFileHeader() {
        return fileHeader;
    }

    public void setFileHeader(String fileHeader) {
        this.fileHeader = fileHeader;
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("positionFile", positionFile)
                .add("filegroups", filegroups)
                .add("filegroupsPath", filegroupsPath)
                .add("batchSize", batchSize)
                .add("batchDurationMillis", batchDurationMillis)
                .add("fileHeader", fileHeader)
                .add("inputCharset", inputCharset)
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
    