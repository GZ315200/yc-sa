package com.unistack.tamboo.mgt.model.monitor;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * @author Gyges Zean
 * @date 2018/7/27
 */
@Entity
@Table(name = "m_cacl_workers_info", schema = "sa-mgt", catalog = "")
public class MCaclWorkersInfo {
    private String workerId;
    private String host;
    private String port;
    private String cores;
    private String coresUsed;
    private String coresFree;
    private String memory;
    private String memoryUse;
    private String memoryFree;
    private String state;
    private String lastHeartbeat;

    @Id
    @Column(name = "workerId")
    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    @Basic
    @Column(name = "host")
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Basic
    @Column(name = "port")
    @JsonIgnore
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Basic
    @Column(name = "cores")
    public String getCores() {
        return cores;
    }

    public void setCores(String cores) {
        this.cores = cores;
    }

    @Basic
    @Column(name = "cores_used")
    public String getCoresUsed() {
        return coresUsed;
    }

    public void setCoresUsed(String coresUsed) {
        this.coresUsed = coresUsed;
    }

    @Basic
    @Column(name = "cores_free")
    public String getCoresFree() {
        return coresFree;
    }

    public void setCoresFree(String coresFree) {
        this.coresFree = coresFree;
    }

    @Basic
    @Column(name = "memory")
    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    @Basic
    @Column(name = "memory_use")
    public String getMemoryUse() {
        return memoryUse;
    }

    public void setMemoryUse(String memoryUse) {
        this.memoryUse = memoryUse;
    }

    @Basic
    @Column(name = "memory_free")
    public String getMemoryFree() {
        return memoryFree;
    }

    public void setMemoryFree(String memoryFree) {
        this.memoryFree = memoryFree;
    }

    @Basic
    @Column(name = "state")
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Basic
    @Column(name = "last_heartbeat")
    public String getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(String lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MCaclWorkersInfo that = (MCaclWorkersInfo) o;

        if (workerId != null ? !workerId.equals(that.workerId) : that.workerId != null) return false;
        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        if (port != null ? !port.equals(that.port) : that.port != null) return false;
        if (cores != null ? !cores.equals(that.cores) : that.cores != null) return false;
        if (coresUsed != null ? !coresUsed.equals(that.coresUsed) : that.coresUsed != null) return false;
        if (coresFree != null ? !coresFree.equals(that.coresFree) : that.coresFree != null) return false;
        if (memory != null ? !memory.equals(that.memory) : that.memory != null) return false;
        if (memoryUse != null ? !memoryUse.equals(that.memoryUse) : that.memoryUse != null) return false;
        if (memoryFree != null ? !memoryFree.equals(that.memoryFree) : that.memoryFree != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (lastHeartbeat != null ? !lastHeartbeat.equals(that.lastHeartbeat) : that.lastHeartbeat != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = workerId != null ? workerId.hashCode() : 0;
        result = 31 * result + (host != null ? host.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (cores != null ? cores.hashCode() : 0);
        result = 31 * result + (coresUsed != null ? coresUsed.hashCode() : 0);
        result = 31 * result + (coresFree != null ? coresFree.hashCode() : 0);
        result = 31 * result + (memory != null ? memory.hashCode() : 0);
        result = 31 * result + (memoryUse != null ? memoryUse.hashCode() : 0);
        result = 31 * result + (memoryFree != null ? memoryFree.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (lastHeartbeat != null ? lastHeartbeat.hashCode() : 0);
        return result;
    }
}
