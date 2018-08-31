package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;

import java.util.Iterator;

/**
 * @ClassName TakeAsciiFilter
 * @Description TODO    {"type":"TakeAscii","filedCount":1 ,"fields":[{"sourceField":"输入的值#String"}]}
 * @Author unistack
 * @Date 2018/7/19 9:31
 * @Version 1.0
 */
public class TakeAsciiFilter implements Filter {

    private String sourceField = "";

    /**
     * @author      cyq
     * @methodDec   用于初始化配置文件
     * @param       config  过滤文件
     * @return      
     * @exception   
     * @date        2018/7/23 15:29
     */
    @Override
    public void init(JSONObject config) throws Exception {
        JSONObject initConfig = config.getJSONArray("fields").getJSONObject(0);
        initAsciiMethod(initConfig);
    }

    /**
     * @author      cyq
     * @methodDec   
     * @param       initConfig  需要初始化的值
     * @return      
     * @exception   
     * @date        2018/7/23 15:29
     */
    private void initAsciiMethod(JSONObject initConfig) {
        Iterator<String> iteratorAscii = initConfig.keySet().iterator();
        String[] str = new String[initConfig.size()];
        int count = 0;
        while (iteratorAscii.hasNext()) {
            String key = iteratorAscii.next();
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
     * @methodDe    依据条件过滤方法
     * @param       source   需要过滤的值
     * @return      
     * @exception   
     * @date        2018/7/23 15:30
     */
    @Override
    public JSONObject filter(JSONObject source) throws Exception {

        if (source.containsKey(sourceField)) {
            String sourceValue = source.getString("sourceField");
            if ("".equals(sourceValue)) {
                source.put("msg", "值为空了！");
                return source;
            } else {
                char firstWord = sourceValue.charAt(0);
                int changeWorld = (int) firstWord;
                source.put(sourceField, changeWorld);
                return source;
            }
        }

        return source;
    }

}
