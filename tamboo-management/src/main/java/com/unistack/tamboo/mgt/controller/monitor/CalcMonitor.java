package com.unistack.tamboo.mgt.controller.monitor;

/**
* @program: tamboo-sa
* @description:
* @author: Asasin
* @create: 2018-05-26 20:05
**/
public class CalcMonitor{
    private String applicationName;
    private String url_cluster;

    public String getUrl_cluster(){
        return url_cluster;
    }

    public void setUrl_cluster(String url_cluster) {
        this.url_cluster = url_cluster;
    }

    public String getApplicationName() {
        return applicationName;
    }






    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}