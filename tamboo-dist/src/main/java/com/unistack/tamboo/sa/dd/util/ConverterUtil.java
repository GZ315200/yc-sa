package com.unistack.tamboo.sa.dd.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author anning
 * @date 2018/5/28 下午2:31
 * @description: json转成connect需要的schema
 */
public class ConverterUtil {

    private static  Logger logger = LoggerFactory.getLogger(ConverterUtil.class);

    /**
     *
     * @param data
     * @return
     */
    public static JSONObject jsonWithSchema(JSONObject data) {
        JSONObject schema_json = new JSONObject(true);


        JSONObject schema = new JSONObject(true);
        JSONArray fields = new JSONArray();

        for (String fieldName : data.keySet()) {
            JSONObject field = new JSONObject();
            String fieldType = data.get(fieldName).getClass().getSimpleName();
            System.out.println(fieldType);
            field.put("field", fieldName);
            field.put("type", map.get(fieldType));
            field.put("optional", true);
            fields.add(field);
        }

        schema.put("type", "struct");
        schema.put("fields", fields);
        schema.put("optinonal", false);
        schema.put("name", "any");

        schema_json.put("schema", schema);
        schema_json.put("payload", data);
        return schema_json;
    }

    public static void main(String[] args) {
        JSONObject ss = new JSONObject();
        ss.put("name", "anning22");
        ss.put("age", 2600000000000L);
        ss.put("is", true);
        System.out.println(jsonWithSchema(ss).toJSONString());

        write("anning","anning","test");
    }

    private static Map<String, String> map = new HashMap<>();

    static {
        map.put("Integer", "int64");
        map.put("Double", "float64");
        map.put("Boolean", "boolean");
        map.put("Long", "int64");
        map.put("Float", "float64");
        map.put("String", "string");
        map.put("String[]", "string");
    }

    public static JSONObject write(String username, String password, String topicName) {
        StringBuffer stringBuffer = new StringBuffer();
        String jassConfFileName = topicName +"_jaas.conf";
        String path = ConverterUtil.class.getResource("").getPath() + "../../jaas/" + jassConfFileName;
        stringBuffer.append("KafkaClient {").append("\n")
                .append("   org.apache.kafka.common.security.plain.PlainLoginModule required").append("\n")
                .append("   username=\"").append(username).append("\"").append("\n")
                .append("   password=\"").append(password).append("\";").append("\n")
                .append("};");
        try {
            FileUtils.writeStringToFile(new File(path),stringBuffer.toString(),"utf-8",false);
        } catch (IOException e) {
            logger.error("创建jaas.conf失败！");
            return DdUtil.failResult("创建jaas.conf失败！");
        }
        return DdUtil.succeedResult("创建jaas.conf成功！");
    }

}
