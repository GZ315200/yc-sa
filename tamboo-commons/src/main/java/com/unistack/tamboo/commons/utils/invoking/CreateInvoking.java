package com.unistack.tamboo.commons.utils.invoking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateInvoking {
    
    public static  Logger log = LoggerFactory.getLogger(CreateInvoking.class);

    private String conf_path;   //配置文件地址

    public String getConf_path() {

        return conf_path;
    }

    public void setConf_path(String conf_path) {

        this.conf_path = conf_path;
    }

//    public InvokeResult createDataCollector(InvokingConfig config) {
//
//        try {
//            Class invoke = Class.forName(config.getDcType().getTypeName());
//            CreateInvoking invoking = (CreateInvoking) invoke.newInstance();
//            return invoking.createDataCollector(config);
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
//            e.printStackTrace();
//        } ly {
//            return new InvokeResult(false, String.format("Class.forName(?) ?", dc.getDcType().getTypeName(), "ERROR"));
//        }
//    }
}
