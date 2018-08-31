package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;

import java.util.Iterator;

/**
 * @ClassName ChangeBinaryFilter
 * @Description TODO  {"type":"TakeAscii","filedCount":1 ,"fields":[{"sourceField":"输入的值#int"}]}
 * @Author unistack
 * @Date 2018/7/19 12:00
 * @Version 1.0
 */
public class ChangeBinaryFilter implements Filter {
    private String sourceField = "";

    /**
     * @author      cyq
     * @methodDec   初始化方法
     * @param        config 配置过滤文件
     * @return
     * @exception
     * @date        2018/7/23 15:34
     */
    @Override
    public void init(JSONObject config) throws Exception {
        JSONObject jsonConfig = config.getJSONArray("fields").getJSONObject(0);
        initBinary(jsonConfig);
    }

    /**
     * @author      cyq
     * @methodDec   初始化配置文件拿到key值
     * @param        jsonConfig  初始化文件
     * @return
     * @exception
     * @date        2018/7/23 15:34
     */
    private void initBinary(JSONObject jsonConfig) {
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
            String valueSource = source.getString(sourceField);
            int valueIntSource = Integer.parseInt(valueSource);
            String binaryValue = Integer.toBinaryString(valueIntSource);
            int bit = 8 - binaryValue.length();
            if (binaryValue.length() < 8) {
                for (int j = 0; j < bit; j++) {
                    binaryValue = "0" + binaryValue;
                }
            }
            source.put(sourceField,binaryValue);
            return source ;
        }else{
            source.put("msg","没有此key");
            return source ;
        }

    }
}
