package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;

import java.util.Iterator;

/**
 * @ClassName ConcatFilter
 * @Description TODO   {"type":"ConcatFilter","filedCount":2 ,"fields":[{"sourceFirstField":"输入的值#string","sourceSecondField":"输入的值#String"}]}
 * @Author unistack
 * @Date 2018/7/19 14:11
 * @Version 1.0
 */
public class ConcatFilter implements Filter {
    private String sourceFirstField ="" ;
    private String sourceSecondField ="" ;

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
       JSONObject jsonObject =  config.getJSONArray("fields").getJSONObject(0);
       initConcat(jsonObject);

    }
    /**
     * @author      cyq
     * @methodDec   初始化配置文件拿到key值
     * @param        jsonObject  初始化文件
     * @return
     * @exception
     * @date        2018/7/23 15:34
     */
    private void initConcat( JSONObject jsonObject) {
        Iterator<String> iteratorConfig = jsonObject.keySet().iterator();
        String[] str = new String[jsonObject.size()];
        int count = 0;
        while (iteratorConfig.hasNext()) {
            String key = iteratorConfig.next();
            str[count++] = key;
            System.out.println(key);
        }
        for(int i= 0 ; i < str.length ; i++){
            if("sourceFirstField".equals(str[i])) {
                sourceFirstField = str[i] ;
            }
            if("sourceSecondField".equals(str[i])) {
                sourceSecondField = str[i] ;
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

        if (source.containsKey(sourceFirstField) && source.containsKey(sourceSecondField)) {
            String sourceFirstValue = source.getString(sourceFirstField);
            String sourceSecondValue = source.getString(sourceSecondField);
            String targetFieldValue = sourceFirstValue + sourceSecondValue;
            source.put(sourceFirstField, sourceFirstValue);
            source.put(sourceSecondField, sourceSecondValue);
            source.put("targetField", targetFieldValue);

            return source ;

        }else{

            source.put("msg","没有此key");

            return source ;
        }


    }
}
