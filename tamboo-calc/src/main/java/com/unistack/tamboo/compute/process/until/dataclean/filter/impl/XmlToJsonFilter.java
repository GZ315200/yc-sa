package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;


import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.utils.commons.XmlUtil;


public class XmlToJsonFilter{
    private XmlUtil xmlUtil = new XmlUtil();

    public JSONObject filter(String  xml) throws Exception {
        JSONObject jsonData = xmlUtil.xml2Json(xml);
        return jsonData;
    }
}
