package com.unistack.tamboo.mgt.model.monitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * @author Gyges Zean
 * @date 2018/7/27
 * 用于封装计算服务节点总worker的基本信息
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalcServiceInfoVo {


    private String aliveworkers;

    private String cores;

    private String coresused;

    private String memory;

    private String memoryused;

    private String activeapps;

    private String completedapps;

    private String activeAppsSize;

    private String completedAppsSize;

    private String status;

    private String timestamp;

    private List<MCaclWorkersInfo> caclWorkersInfoList;

    public CalcServiceInfoVo() {
    }

    public CalcServiceInfoVo(String aliveworkers, String cores, String coresused, String memory, String memoryused,
                             String activeapps, String completedapps, String status, String timestamp) {
        this.aliveworkers = aliveworkers;
        this.cores = cores;
        this.coresused = coresused;
        this.memory = memory;
        this.memoryused = memoryused;
        this.activeapps = activeapps;
        this.completedapps = completedapps;
        this.status = status;
        this.timestamp = timestamp;
    }


    public String getActiveAppsSize() {
        return activeAppsSize;
    }

    public void setActiveAppsSize(String activeAppsSize) {
        this.activeAppsSize = activeAppsSize;
    }

    public String getCompletedAppsSize() {
        return completedAppsSize;
    }

    public void setCompletedAppsSize(String completedAppsSize) {
        this.completedAppsSize = completedAppsSize;
    }

    public String getAliveworkers() {
        return aliveworkers;
    }

    public void setAliveworkers(String aliveworkers) {
        this.aliveworkers = aliveworkers;
    }

    public String getCores() {
        return cores;
    }

    public void setCores(String cores) {
        this.cores = cores;
    }

    public String getCoresused() {
        return coresused;
    }

    public void setCoresused(String coresused) {
        this.coresused = coresused;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getMemoryused() {
        return memoryused;
    }

    public void setMemoryused(String memoryused) {
        this.memoryused = memoryused;
    }

    public String getActiveapps() {
        return activeapps;
    }

    public void setActiveapps(String activeapps) {
        this.activeapps = activeapps;
    }

    public String getCompletedapps() {
        return completedapps;
    }

    public void setCompletedapps(String completedapps) {
        this.completedapps = completedapps;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<MCaclWorkersInfo> getCaclWorkersInfoList() {
        return caclWorkersInfoList;
    }

    public void setCaclWorkersInfoList(List<MCaclWorkersInfo> caclWorkersInfoList) {
        this.caclWorkersInfoList = caclWorkersInfoList;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("aliveworkers", aliveworkers)
                .add("cores", cores)
                .add("coresused", coresused)
                .add("memory", memory)
                .add("memoryused", memoryused)
                .add("activeapps", activeapps)
                .add("completedapps", completedapps)
                .add("activeAppsSize", activeAppsSize)
                .add("completedAppsSize", completedAppsSize)
                .add("status", status)
                .add("timestamp", timestamp)
                .add("caclWorkersInfoList", caclWorkersInfoList)
                .toString();
    }
}
