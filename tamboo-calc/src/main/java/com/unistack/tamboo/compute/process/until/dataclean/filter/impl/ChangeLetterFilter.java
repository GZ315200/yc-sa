package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import com.unistack.tamboo.compute.process.until.dataclean.filter.FilterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * @ClassName ChangeLetterFilter
 * @Description TODO  1变大写，2变小写{"type":"ChangeLetter","fields":[{"control"："1" ,"fieldLetter":"原始字段#string","targetLetter":"目标格式#string"}]}
 * @Author cyq
 * @Date 2018/7/18 9:38
 * @Version 1.0
 */
@FilterType("changeLetter")
public class ChangeLetterFilter implements Filter {
    private static Logger LOGGER = LoggerFactory.getLogger(ChangeLetterFilter.class);
    private String fieldLetter = "";
    private String controlLetter = "";
    private  String CONTROL_ONE = "1";
    private  String CONTROL_TWO = "2";

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
        JSONObject arrayLetter = config.getJSONArray("fields").getJSONObject(0);
        initMethod(arrayLetter);
    }

    /**
     * @param source 过滤的配置文件
     * @return JSONObject   返回需要的值
     * @throws
     * @author cyq
     * @methodDec 用于过滤json数据
     * @date 2018/7/18 15:14
     */
    @Override
    public JSONObject filter(JSONObject source) throws Exception {

        String fieldValueLetter = "";
        if (source.containsKey(fieldLetter)) {
            fieldValueLetter = source.getString(fieldLetter);
        }
        if (source.containsKey(controlLetter)) {
            String controlLetterValue = source.getString(controlLetter);
            if (CONTROL_ONE.equals(controlLetterValue)) {
                String upperTarget = fieldValueLetter.toUpperCase();
                source.put(controlLetter, CONTROL_ONE);
                source.put(fieldLetter, upperTarget);

                return source;
            }
            if (CONTROL_TWO.equals(controlLetterValue)) {
                String lowerTarget = fieldValueLetter.toLowerCase();
                source.put(controlLetter, CONTROL_TWO);
                source.put(fieldLetter, lowerTarget);

                return source;
            }

        }
        return source;

    }

    /**
     * @param sourceLetter 模板传进来解析的JSONArray
     * @return JSONArray        封装完的使用的JSONArray
     * @throws
     * @author cyq
     * @methodDec 得到里面的JSONArray数组用于key为fields情况
     * @date 2018/7/18 12:29
     *//*
    private JSONArray getJsonArray(JSONObject sourceLetter) {
       // JSONObject jsonObject = new JSONObject();
       // JSONArray jsonArray = new JSONArray();
        String fieldValueLetter = "";
        if (sourceLetter.containsKey(fieldKeyLetter)) {
            fieldValueLetter = sourceLetter.getString(fieldKeyLetter);
        }
        if (sourceLetter.containsKey(controlLetter)) {
            String controlLetterValue = sourceLetter.getString(controlLetter);
            if (CONTROL_ONE.equals(controlLetterValue)) {
                String upperTarget = fieldValueLetter.toUpperCase();
                sourceLetter.put(controlLetter, CONTROL_ONE);
                sourceLetter.put(fieldKeyLetter, fieldValueLetter);
                sourceLetter.put(targetKeyLetter, upperTarget);

                return sourceLetter;
            }
            if (CONTROL_TWO.equals(controlLetterValue)) {
                String lowerTarget = fieldValueLetter.toLowerCase();
                jsonObject.put(controlLetter, CONTROL_TWO);
                jsonObject.put(fieldKeyLetter, fieldValueLetter);
                jsonObject.put(targetKeyLetter, lowerTarget);
                jsonArray.add(0, jsonObject);
                return jsonArray;
            }

        }
        return jsonArray;

    }*/

    /**
     * @param arrayLetter 用于解析初始化拿到key 的JSONObject
     * @return
     * @throws
     * @author cyq
     * @methodDec 初始化封装
     * @date 2018/7/18 12:29
     */
    private void initMethod(JSONObject arrayLetter) {
        Iterator<String> sIterator = arrayLetter.keySet().iterator();
        String[] str = new String[arrayLetter.size()];
        int count = 0;
        while (sIterator.hasNext()) {
            String key = sIterator.next();
            str[count++] = key;
        }
        for (int i = 0; i < str.length; i++) {
            if ("controlLetter".equals(str[i])) {
                controlLetter = str[i];
            }

            if ("fieldLetter".equals(str[i])) {
                fieldLetter = str[i];
            }

        }

    }
}
