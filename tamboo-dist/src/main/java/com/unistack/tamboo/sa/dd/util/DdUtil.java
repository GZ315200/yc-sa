package com.unistack.tamboo.sa.dd.util;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.constant.DdType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author anning
 * @date 2018/5/22 上午11:37
 * @description: 工具类
 */
public class DdUtil {

    public static JSONObject succeedResult(Object msg){
        JSONObject result = new JSONObject(true);
        result.put("isSucceed",true);
        result.put("msg",msg);
        return result;
    }


    public static JSONObject failResult(Object msg){
        JSONObject result = new JSONObject(true);
        result.put("isSucceed",false);
        result.put("msg",msg);
        return result;
    }


    public static String date2String(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd-HHmmss");
        String format = simpleDateFormat.format(date);
        return format;
    }

    public static String getJdbcUrl(DdType ddType,JSONObject ddConfig) {
        String format = ddType.getFormat();
        String jdbc_sink_ip = ddConfig.getString("jdbc_sink_ip");
        String jdbc_sink_port = ddConfig.getString("jdbc_sink_port");
        String database = ddConfig.getString("jdbc_sink_database");
        String format1 = String.format(format, jdbc_sink_ip, jdbc_sink_port, database);
        return format1;
    }

    public static String getSqlType(String javaType, String databaseType){
        Map<String,String> typeMap = new HashMap<>();
        String numberType;
        if (databaseType.equalsIgnoreCase("oracle"))
            numberType = "number(38,2)";
        else if (databaseType.equalsIgnoreCase("mysql"))
            numberType = "double(38,2)";
        else if (databaseType.equalsIgnoreCase("sqlserver"))
            numberType = "decimal(38,2)";
        else if (databaseType.equalsIgnoreCase("postgre")){
            numberType = "numeric";
        }else numberType = "numeric";
        String stringType;
        if (databaseType.equalsIgnoreCase("postgre")){
            stringType = "text";
        }else stringType = "varchar(255)";


        typeMap.put("String",stringType);
        typeMap.put("Integer",numberType);
        typeMap.put("Boolean","tinyint(4)");
        typeMap.put("Float",numberType);
        typeMap.put("String[]",stringType);
        typeMap.put("Long",numberType);
        typeMap.put("Double",numberType);
        typeMap.put("JSONObject",stringType);
        if (typeMap.get(javaType)!=null){
            return typeMap.get(javaType);
        }
        return null;
    }

}
