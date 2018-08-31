package com.unistack.tamboo.sa.dc.flume.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @program: tamboo-sa
 * @description: 用于一些数据类型的转换
 * @author: Asasin
 * @create: 2018-05-10 15:20
 **/
public class DataConversionUtil {

    public static JSONObject MapToJSONObject(Map map){
        return JSONObject.parseObject(JSON.toJSONString(map));
    }

}
    