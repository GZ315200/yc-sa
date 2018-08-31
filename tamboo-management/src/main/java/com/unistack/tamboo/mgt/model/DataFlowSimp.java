package com.unistack.tamboo.mgt.model;

import com.unistack.tamboo.mgt.model.dataFlow.CurveBase;

public class DataFlowSimp {
    private int wf_id;

    private long countRecords;

    private int latency;

    private int warning;

    private int distCount;

    private String conf;

    private CurveBase[] collector;

    private CurveBase[] dist;

    public int getWf_id() {
        return wf_id;
    }

    public void setWf_id(int wf_id) {
        this.wf_id = wf_id;
    }

    public long getCountRecords() {
        return countRecords;
    }

    public void setCountRecords(long countRecords) {
        this.countRecords = countRecords;
    }

    public int getLatency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }

    public int getWarning() {
        return warning;
    }

    public void setWarning(int warning) {
        this.warning = warning;
    }

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }

    public CurveBase[] getCollector() {
        return collector;
    }

    public void setCollector(CurveBase[] collector) {
        this.collector = collector;
    }

    public CurveBase[] getDist() {
        return dist;
    }

    public void setDist(CurveBase[] dist) {
        this.dist = dist;
    }

    public int getDistCount() {
        return distCount;
    }

    public void setDistCount(int distCount) {
        this.distCount = distCount;
    }
}
