package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;

import java.util.Iterator;

/**
 * @ClassName FindLocationFilter
 * @Description TODO {"type":"Locate","filedCount":1 ,"fields":[{"sourceField":"输入的值#String","locationField":"查找出现的值#int"}]}
 * @Author unistack
 * @Date 2018/7/23 16:05
 * @Version 1.0
 */
public class FindLocationFilter implements Filter {
    private String sourceField = "";
    private String locationField = "";

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
        initFindLocation(jsonConfig);
    }

    /**
     * @param jsonConfig 初始化文件
     * @return
     * @throws
     * @author cyq
     * @methodDec 初始化配置文件拿到key值
     * @date 2018/7/23 15:34
     */
    private void initFindLocation(JSONObject jsonConfig) {
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

            if ("locationField".equals(str[i])) {
                locationField = str[i];
            }

        }
    }

    /**
     * @param source 过滤值
     * @return
     * @throws
     * @author cyq
     * @methodDec 真正的需要过滤的值
     * @date 2018/7/23 15:35
     */
    @Override
    public JSONObject filter(JSONObject source) throws Exception {
        if (source.containsKey(sourceField) && source.containsKey(locationField)) {
            String sourceFieldValue = source.getString(sourceField);
            int addStrValue = source.getInteger(locationField);
            char result = locationMethod(sourceFieldValue, addStrValue);
            if (result != ' ') {
                source.put(sourceField,result);
            }

            return source;
        } else {
            source.put("msg", "key不存在！");
            return source;

        }
    }

    /**
     * @param sourceFieldValue 输入的值    addStrValue   查找的值
     * @return
     * @throws
     * @author cyq
     * @methodDec 过滤条件的逻辑处理
     * @date 2018/7/23 15:38
     */
    private char locationMethod(String sourceFieldValue, int addStrValue) {
        char result =' ';
        if (sourceFieldValue == null || "".equals(sourceFieldValue)) {
            return result;
        }
        if (addStrValue >= 0 && addStrValue <= sourceFieldValue.length()) {
             result = sourceFieldValue.charAt(addStrValue );
            return result;

        }
        return result ;


    }

}
