package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;

import java.util.Iterator;

/**
 * @ClassName ReverseFilter
 * @Description TODO {"type":"Reverse","filedCount":1 ,"fields":[{"sourceField":"输入的值#String"}]}
 * @Author unistack
 * @Date 2018/7/23 15:04
 * @Version 1.0
 */
public class ReverseFilter implements Filter {
    private String sourceField = "";
    /**
     * @param config 配置文件
     * @return
     * @throws
     * @author cyq
     * @methodDec 初始化配置文件
     * @date 2018/7/18 15:11
     */
    @Override
    public void init(JSONObject config) throws Exception {
        JSONObject jsonConfig = config.getJSONArray("fields").getJSONObject(0);
        initReverse(jsonConfig);
    }
    /**
     * @author      cyq
     * @methodDec   初始化配置文件拿到key值
     * @param        jsonConfig  初始化文件
     * @return
     * @exception
     * @date        2018/7/23 15:34
     */
    private void initReverse(JSONObject jsonConfig) {
        Iterator<String> iteratorConfig = jsonConfig.keySet().iterator();
        String[] str = new String[jsonConfig.size()];
        int count = 0;
        while (iteratorConfig.hasNext()) {
            String key = iteratorConfig.next();
            str[count++] = key;
        }
        for (int i = 0; i < str.length; i++) {
            if ("sourceField".equals(str[i])) {
                sourceField = str[i];
            }

        }
    }
    /**
     * @author      cyq
     * @methodDec   真正的需要过滤的值
     * @param        source   过滤值
     * @return
     * @exception
     * @date        2018/7/23 15:35
     */
    @Override
    public JSONObject filter(JSONObject source) throws Exception {
        if (source.containsKey(sourceField)) {
            String sourceFieldValue = source.getString(sourceField);
            String reverseStr = relpace(sourceFieldValue);
            source.put("sourceField",reverseStr);
            return source;
        } else {
            source.put("msg", "key不存在！");
            return source;

        }

    }
    /**
     * @author      cyq
     * @methodDec   过滤条件的逻辑处理
     * @param       sourceFieldValue   输入的值
     * @exception
     * @date        2018/7/23 15:38
     */
    private String relpace(String sourceFieldValue) {
        if (sourceFieldValue == null || "".equals(sourceFieldValue)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        return sb.append(sourceFieldValue).reverse().toString();

    }
}
