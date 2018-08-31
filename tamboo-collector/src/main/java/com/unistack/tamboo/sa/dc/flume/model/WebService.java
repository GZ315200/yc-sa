package com.unistack.tamboo.sa.dc.flume.model;
import com.google.common.base.MoreObjects;
import com.unistack.tamboo.sa.dc.flume.common.DcConfig;


/**
 * @program: tamboo-sa
 * @description: webervice source model
 * @author: Asasin
 * @create: 2018-05-16 11:06
 **/
public class WebService extends DcConfig {
    private String postUrl;
    private String soapXml;
    private String soapAction;
    private Integer intervalMillis;
    private String inputCharset;
    //sink
    private String topic;
    private String username;
    private String password;
    private String servers;
    //开放http监控端口
    private String path;
    private String httpPort;

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public String getSoapXml() {
        return soapXml;
    }

    public void setSoapXml(String soapXml) {
        this.soapXml = soapXml;
    }

    public String getSoapAction() {
        return soapAction;
    }

    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    public Integer getIntervalMillis() {
        return intervalMillis;
    }

    public void setIntervalMillis(Integer intervalMillis) {
        this.intervalMillis = intervalMillis;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getInputCharset() {
        return inputCharset;
    }

    public void setInputCharset(String inputCharset) {
        this.inputCharset = inputCharset;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("postUrl", postUrl)
                .add("soapXml", soapXml)
                .add("soapAction", soapAction)
                .add("intervalMillis", intervalMillis)
                .add("inputCharset", inputCharset)
                .add("topic", topic)
                .add("username", username)
                .add("password", password)
                .add("servers", servers)
                .add("path", path)
                .add("httpPort", httpPort)
                .toString();
    }
}
