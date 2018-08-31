package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;

import java.util.Iterator;

/**
 * @ClassName PadStrFilter
 * @Description TODO  {"type":"PadStr","filedCount":1 ,"fields":[{"sourceField":"输入的值#int","getLength":"添加连接多长#int","addStr":"连接字符串#string","location":"1代表左，2代表右#int"}]}
 * @Author unistack
 * @Date 2018/7/23 10:52
 * @Version 1.0
 */
public class PadStrFilter implements Filter {
    private String sourceField = "";
    private String length = "";
    private String addStr = "";
    private String location = "";
    private int LOCATION_ONE = 1;
    private int LOCATION_TWO = 2;
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
        initPadStr(jsonConfig);
    }
    /**
     * @author      cyq
     * @methodDec   初始化配置文件拿到key值
     * @param        jsonConfig  初始化文件
     * @return
     * @exception
     * @date        2018/7/23 15:34
     */
    private void initPadStr(JSONObject jsonConfig) {
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

            if ("length".equals(str[i])) {
                length = str[i];
            }
            if ("addStr".equals(str[i])) {
                addStr = str[i];
            }
            if ("location".equals(str[i])) {
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
        if (source.containsKey(sourceField) && source.containsKey(length) && source.containsKey(addStr) && source.containsKey(location)) {
            String sourceFieldValue = source.getString(sourceField);
            int getLengthValue = source.getInteger(length);
            String addStrValue = source.getString(addStr);
            int locationValue = source.getInteger(location);
            String value = changeMethod(sourceFieldValue, getLengthValue, addStrValue, locationValue);
            source.put("sourceField",value);
            return source;
        } else {
            source.put("msg", "key不存在！");
            return source;

        }
    }
    /**
     * @author      cyq
     * @methodDec   过滤条件的逻辑处理
     * @param       sourceFieldValue   输入的值    getLengthValue 取得长度值  addStrValue   增添的值  locationValue  左还是右
     * @return
     * @exception
     * @date        2018/7/23 15:38
     */
    private String changeMethod(String sourceFieldValue, int getLengthValue, String addStrValue, int locationValue) {
        //左开始
        String result = "";
        int count = sourceFieldValue.length();
        int addCount = addStrValue.length();
        int need = getLengthValue - addCount;
        if (locationValue == LOCATION_ONE) {

            if (need >= 0 && getLengthValue <= (addCount + count)) {
                String subStr = sourceFieldValue.substring(0, need );
                result = addStrValue + subStr;
                return result;
            }
        }
        //右开始
        if (locationValue == LOCATION_TWO) {

            if (need >= 0 && getLengthValue <= (addCount + getLengthValue)) {
                int calc = count - need ;
               // int needAll = count - calc ;
                String subStr = sourceFieldValue.substring(calc, sourceFieldValue.length() );
                result = subStr  + addStrValue;
                return result;
            }
        }
        return result;


    }
}
