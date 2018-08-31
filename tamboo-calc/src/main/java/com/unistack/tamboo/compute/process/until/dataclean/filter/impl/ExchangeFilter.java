package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import com.unistack.tamboo.compute.process.until.dataclean.filter.FilterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * @ClassName ExchangeFilter
 * @Description 过滤规则：{"type":"exchange","fields":[{"exchangeFromField":"输入字段#string","exchangeToField":"输入字段#string"}]}
 * @Author unistack
 * @Date 2018/7/13 15:23
 * @Version 1.0
 */
@FilterType("exchange")
public class ExchangeFilter implements Filter {
    private static Logger LOGGER = LoggerFactory.getLogger(ExchangeFilter.class);
    private String exchangeFromField = "";
    private String exchangeToField = "";

    /**
     * @param config 配置文件
     * @return
     * @throws
     * @author cyq
     * @methodDec 用于初始化模板
     * @date 2018/7/18 15:10
     */
    @Override
    public void init(JSONObject config) throws Exception {
        JSONObject arrayExchange = config.getJSONArray("fields").getJSONObject(0);
        initMethod(arrayExchange);
    }

    @Override
    /**
     * @author cyq
     * @methodDec 数值交换过滤
     * @param       [source]  过滤的配置文件
     * @return JSONObject   最终展示的结果
     * @exception
     * @date 2018/7/13 16:31
     */

    public JSONObject filter(JSONObject source) throws Exception {
        String valueToExchange = "";
        String valueFromExchange = "";
        if (source.containsKey(exchangeToField)) {
            valueToExchange = source.getString(exchangeToField);
        }
        if (source.containsKey(exchangeFromField)) {
            valueFromExchange = source.getString(exchangeFromField);
        }
        if ("".equals(valueToExchange) || "".equals(valueFromExchange)) {
            source.put("msg", "值为空！");
        } else {
            source.put(exchangeFromField, valueToExchange);
            source.put(exchangeToField, valueFromExchange);

            return source;

        }
        return source;
    }

    /**
     * @param arrayExchange 用于解析初始化拿到key 的JSONObject
     * @return
     * @throws
     * @author cyq
     * @methodDec 初始化封装
     * @date 2018/7/18 12:29
     */
    private void initMethod(JSONObject arrayExchange) {
        Iterator<String> sIterator = arrayExchange.keySet().iterator();
        String[] str = new String[2];
        int count = 0;
        while (sIterator.hasNext()) {
            String key = sIterator.next();
            str[count++] = key;
        }
        for (int i = 0; i < str.length; i++) {
            if ("exchangeFromField".equals(str[i])) {
                exchangeFromField = str[i];
            }

            if ("exchangeToField".equals(str[i])) {
                exchangeToField = str[i];
            }

        }

    }

}
