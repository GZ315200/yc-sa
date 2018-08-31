package com.unistack.tamboo.commons.utils.invoking;


import com.fasterxml.jackson.databind.JsonNode;

public class InvokingConfig{

    private JsonNode conf;

    public InvokingConfig() {
    }

    public JsonNode getConf() {

        return conf;
    }

    public void setConf(JsonNode conf) {
        this.conf = conf;
    }
}
