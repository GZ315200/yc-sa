package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;

import java.util.Iterator;

/**
 * @ClassName LocateFilter
 * @Description TODO  {"type":"Locate","filedCount":1 ,"fields":[{"sourceField":"输入的值#String","findField":"查找出现的值#String"}]}
 * @Author unistack
 * @Date 2018/7/23 14:05
 * @Version 1.0
 */
public class LocateFilter implements Filter {
    private String sourceField = "";
    private String findField = "";
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
        initLocateStr(jsonConfig);
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

        if (source.containsKey(sourceField) && source.containsKey(findField)) {
            String sourceFieldValue = source.getString(sourceField);
            String addStrValue = source.getString(findField);
            int finalValue = locateMethod(sourceFieldValue, addStrValue);
            if (finalValue != -1) {
                source.put("sourceField", finalValue);

            }
            return source;
        } else {
            source.put("msg", "key不存在！");
            return source;

        }
    }
    /**
     * @author      cyq
     * @methodDec   过滤条件的逻辑处理
     * @param       sourceFieldValue   输入的值    addStrValue   查找的值
     * @return
     * @exception
     * @date        2018/7/23 15:38
     */
    private int locateMethod(String sourceFieldValue, String addStrValue) {
        if (sourceFieldValue == null || "".equals(sourceFieldValue)) {
            return -1;
        }
        if (addStrValue == null || "".equals(addStrValue)) {
            return -1;
        }

        if (sourceFieldValue.contains(addStrValue)) {
            char addStr = addStrValue.charAt(0);
            for (int i = 0; i < sourceFieldValue.length(); i++) {
                if (sourceFieldValue.charAt(i) == addStr) {
                    int count = i ;
                    return count ;
                }

            }

        }
        return -1;


    }

    /**
     * @author      cyq
     * @methodDec   初始化配置文件拿到key值
     * @param        jsonConfig  初始化文件
     * @return
     * @exception
     * @date        2018/7/23 15:34
     */
    private void initLocateStr(JSONObject jsonConfig) {
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

            if ("findField".equals(str[i])) {
                findField = str[i];
            }

        }
    }
}
