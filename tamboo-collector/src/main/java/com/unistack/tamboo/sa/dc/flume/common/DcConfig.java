package com.unistack.tamboo.sa.dc.flume.common;


import com.alibaba.fastjson.JSONObject;


public class DcConfig{
    private DcType dcType;

    private JSONObject conf;

    public DcConfig() {
    }

    public DcType getDcType() {
        return dcType;
    }

    public void setDcType(DcType dcType) {
       this.dcType = dcType;
    }

    public JSONObject getConf() {

        return conf;
    }

    public void setConf(JSONObject conf) {

        this.conf = conf;
    }
}
