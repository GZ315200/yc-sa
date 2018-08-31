package com.unistack.tamboo.mgt.model.monitor;

import javax.persistence.*;

/**
 * @author Gyges Zean
 * @date 2018/7/31
 */
@Entity
@Table(name = "m_zk_service", schema = "sa-mgt", catalog = "")
public class MZkService {
    private String host;
    private String packetsSent;
    private String packetsReceived;
    private String minRequestLatency;
    private String avgRequestLatency;
    private String maxRequestLatency;
    private String version;
    private String ticktime;
    private String startTime;
    private String clientPort;
    private String state;
    private String outstandingRequest;
    private String maxClientCnxnsPerHost;
    private String timestamp;

    @Id
    @Column(name = "host")
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Basic
    @Column(name = "packets_sent")
    public String getPacketsSend() {
        return packetsSent;
    }

    public void setPacketsSend(String packetsSent) {
        this.packetsSent = packetsSent;
    }

    @Basic
    @Column(name = "packets_received")
    public String getPacketsReceived() {
        return packetsReceived;
    }

    public void setPacketsReceived(String packetsReceived) {
        this.packetsReceived = packetsReceived;
    }

    @Basic
    @Column(name = "min_request_latency")
    public String getMinRequestLatency() {
        return minRequestLatency;
    }

    public void setMinRequestLatency(String minRequestLatency) {
        this.minRequestLatency = minRequestLatency;
    }

    @Basic
    @Column(name = "avg_request_latency")
    public String getAvgRequestLatency() {
        return avgRequestLatency;
    }

    public void setAvgRequestLatency(String avgRequestLatency) {
        this.avgRequestLatency = avgRequestLatency;
    }

    @Basic
    @Column(name = "max_request_latency")
    public String getMaxRequestLatency() {
        return maxRequestLatency;
    }

    public void setMaxRequestLatency(String maxRequestLatency) {
        this.maxRequestLatency = maxRequestLatency;
    }

    @Basic
    @Column(name = "version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Basic
    @Column(name = "ticktime")
    public String getTicktime() {
        return ticktime;
    }

    public void setTicktime(String ticktime) {
        this.ticktime = ticktime;
    }

    @Basic
    @Column(name = "start_time")
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @Basic
    @Column(name = "client_port")
    public String getClientPort() {
        return clientPort;
    }

    public void setClientPort(String clientPort) {
        this.clientPort = clientPort;
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
    @Column(name = "outstanding_request")
    public String getOutstandingRequest() {
        return outstandingRequest;
    }

    public void setOutstandingRequest(String outstandingRequest) {
        this.outstandingRequest = outstandingRequest;
    }

    @Basic
    @Column(name = "max_client_cnxns_per_host")
    public String getMaxClientCnxnsPerHost() {
        return maxClientCnxnsPerHost;
    }

    public void setMaxClientCnxnsPerHost(String maxClientCnxnsPerHost) {
        this.maxClientCnxnsPerHost = maxClientCnxnsPerHost;
    }

    @Basic
    @Column(name = "timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MZkService that = (MZkService) o;

        if (host != null ? !host.equals(that.host) : that.host != null) return false;
        if (packetsSent != null ? !packetsSent.equals(that.packetsSent) : that.packetsSent != null) return false;
        if (packetsReceived != null ? !packetsReceived.equals(that.packetsReceived) : that.packetsReceived != null)
            return false;
        if (minRequestLatency != null ? !minRequestLatency.equals(that.minRequestLatency) : that.minRequestLatency != null)
            return false;
        if (avgRequestLatency != null ? !avgRequestLatency.equals(that.avgRequestLatency) : that.avgRequestLatency != null)
            return false;
        if (maxRequestLatency != null ? !maxRequestLatency.equals(that.maxRequestLatency) : that.maxRequestLatency != null)
            return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        if (ticktime != null ? !ticktime.equals(that.ticktime) : that.ticktime != null) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        if (clientPort != null ? !clientPort.equals(that.clientPort) : that.clientPort != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (outstandingRequest != null ? !outstandingRequest.equals(that.outstandingRequest) : that.outstandingRequest != null)
            return false;
        if (maxClientCnxnsPerHost != null ? !maxClientCnxnsPerHost.equals(that.maxClientCnxnsPerHost) : that.maxClientCnxnsPerHost != null)
            return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + (packetsSent != null ? packetsSent.hashCode() : 0);
        result = 31 * result + (packetsReceived != null ? packetsReceived.hashCode() : 0);
        result = 31 * result + (minRequestLatency != null ? minRequestLatency.hashCode() : 0);
        result = 31 * result + (avgRequestLatency != null ? avgRequestLatency.hashCode() : 0);
        result = 31 * result + (maxRequestLatency != null ? maxRequestLatency.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (ticktime != null ? ticktime.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (clientPort != null ? clientPort.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (outstandingRequest != null ? outstandingRequest.hashCode() : 0);
        result = 31 * result + (maxClientCnxnsPerHost != null ? maxClientCnxnsPerHost.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
}
