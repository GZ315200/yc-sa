package com.unistack.tamboo.commons.utils;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unistack.tamboo.commons.utils.errors.DataException;
import kafka.utils.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Gyges Zean
 * @date 2018/5/2
 */
public class JsonUtils {

    public static  Logger log = LoggerFactory.getLogger(JsonUtils.class);

    public static Map<String, Object> jsonToMap(String json) {
        ObjectMapper o = new ObjectMapper();
        try {
            return o.readValue(json, Map.class);
        } catch (IOException e) {
            throw new DataException("Failed to Convert json to Map");
        }
    }

    public static Map<String, Map<String, Object>> toMap(String json) {
        ObjectMapper o = new ObjectMapper();
        try {
            return o.readValue(json, Map.class);
        } catch (IOException e) {
            throw new DataException("Failed to Convert to Map");
        }
    }


    public static byte[] toByteArray(Object v) {
        ObjectMapper o = new ObjectMapper();
        try {
            return o.writeValueAsBytes(v);
        } catch (JsonProcessingException e) {
            throw new DataException("Failed to write value to byte array", e);
        }
    }


    public static <K,V> Map<K,V> toMap(JSONObject json){
        Set<Map.Entry<String,Object>> set = json.entrySet();
        HashMap<K, V> result = new HashMap<>();
        for(Map.Entry kv : set){
            result.put((K)kv.getKey(),(V)kv.getValue());
        }
        return result;
    }

    public static void main(String[] args){
        String v = "{\"topic\":\"topic\",\"offset_size\":9999999,\"total_size\":0,\"memory\":\"5KB/s\"}";
        JSONObject j1 = JSONObject.parseObject(v);
        Map<String, String> m = JsonUtils.<String, String>toMap(j1);
        System.out.println(m);

//        System.out.println(jsonToMap(v));

    }
}
