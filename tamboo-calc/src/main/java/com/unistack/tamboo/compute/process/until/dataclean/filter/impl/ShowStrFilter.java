package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;

import java.util.Iterator;

/**
 * @ClassName ShowStrFilter
 * @Description TODO  {"type":"ShowStr","filedCount":1 ,"fields":[{"sourceField":"输入的值#string","length":"截取的长度#int"，"location":"1左边或2右边截取#int"}]}
 * @Author unistack
 * @Date 2018/7/20 11:18
 * @Version 1.0
 */
public class ShowStrFilter implements Filter {
    private String sourceField = "sourceField";
    private String length = "length";
    private String location = "location";

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
        initMethod(jsonConfig);
    }
    /**
     * @author      cyq
     * @methodDec   初始化配置文件拿到key值
     * @param        jsonConfig  初始化文件
     * @return
     * @exception
     * @date        2018/7/23 15:34
     */
    private void initMethod(JSONObject jsonConfig) {
        Iterator<String> iteratorConfig = jsonConfig.keySet().iterator();
        String[] str = new String[jsonConfig.size()];
        int count = 0;
        while (iteratorConfig.hasNext()) {
            String key = iteratorConfig.next();
            str[count++] = key;
        }
        for (int i = 0; i < str.length; i++) {
            if (sourceField.equals(str[i])) {
                sourceField = str[i];
            }

            if (length.equals(str[i])) {
                length = str[i];
            }
            if (location.equals(str[i])) {
                location = str[i];
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
        if (source.containsKey(sourceField) && source.containsKey(length) && source.containsKey(location)) {
            String sourceFieldValue = source.getString(sourceField);
            int lengthValue = source.getInteger(length);
            int locationValue = source.getInteger(location);
            String value = cutWorld(sourceFieldValue, lengthValue, locationValue);
            if (!"".equals(value)) {
                source.put(sourceField, value);
                source.put(length, lengthValue);
                source.put(location, locationValue);
                return source;

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
     * @param       sourceFieldValue   输入的值    lengthValue 取得长度值  locationValue   位置左或者右
     * @return
     * @exception
     * @date        2018/7/23 15:38
     */
    private String cutWorld(String sourceFieldValue, int lengthValue, int locationValue) {
        //1代表左边
        int firstLocation = 1;
        int secondLocation = 2;
        if (locationValue == firstLocation) {

            return sourceFieldValue.substring(0, lengthValue);

        }
        //2代表右边
        if (locationValue == secondLocation) {
            return sourceFieldValue.substring(sourceFieldValue.length() - lengthValue, sourceFieldValue.length());
        }
        return "";
    }
}
