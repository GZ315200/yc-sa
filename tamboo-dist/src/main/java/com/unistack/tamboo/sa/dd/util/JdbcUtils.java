package com.unistack.tamboo.sa.dd.util;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.constant.DdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author anning
 * @date 2018/6/19 下午5:16
 * @description: jdbc utils
 */
public class JdbcUtils {
    private static  Logger logger = LoggerFactory.getLogger(JdbcUtils.class);

    public static JSONObject getConnection(DdType ddType, JSONObject args) {
        JSONObject getResult ;
        Connection connection = null;
        String errmsg = null;
//        String jdbc_sink_ip = args.getString("ip");
//        String jdbc_sink_port = args.getString("port");
//        String jdbc_sink_database = args.getString("database");
//        String jdbcUrl = String.format(ddType.getFormat(), jdbc_sink_ip, jdbc_sink_port, jdbc_sink_database); // + "?useSSL=false"
        String jdbcUrl = args.getString("jdbc_url");
        String username = args.getString("user_name");
        String password = args.getString("user_pwd");
        try {
            Class.forName(ddType.getDriverClass());
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            getResult = DdUtil.succeedResult(connection);
        } catch (ClassNotFoundException e) {
            errmsg = String.format("未找到数据库加载类: %s======>%s",ddType.getDriverClass(),e.toString());
            logger.error(errmsg);
            getResult = DdUtil.failResult(errmsg);
        } catch (SQLException e) {
            errmsg = String.format("建立数据库连接时出现异常：======>%s",e.toString());
            logger.error(errmsg);
            getResult = DdUtil.failResult(errmsg);
        }
        return getResult;
    }

    public static void flatJson(JSONObject json,String preStr,String separator,JSONObject distJson){
        for (String key : json.keySet()){
            if ("JSONObject".equalsIgnoreCase(json.get(key).getClass().getSimpleName())){
                flatJson(json.getJSONObject(key),preStr+separator+key,separator,distJson);
            }else {
                distJson.put(preStr+separator+key,json.get(key));
            }
        }
    }

    public static void flatJsonFirst(JSONObject json,String separator,JSONObject distJson){
//        distJson.clear();
        for (String key : json.keySet()){
            if ("JSONObject".equalsIgnoreCase(json.get(key).getClass().getSimpleName())){
                flatJson(json.getJSONObject(key),key,separator,distJson);
            }else {
                distJson.put(key,json.get(key));
            }
        }
    }

    public static void main(String[] args) {
        JSONObject srcJson = new JSONObject(true).fluentPut("name","anning")
                .fluentPut("age",26).fluentPut("info",new JSONObject().fluentPut("intres","book"))
                .fluentPut("company",new JSONObject().fluentPut("addr",new JSONObject().fluentPut("city","shanghai")
                .fluentPut("people",10)));
//        srcJson = new JSONObject(true).fluentPut("name","anning").fluentPut("age",26);
        System.out.println(srcJson);
        JSONObject distJson = new JSONObject(true);
        flatJsonFirst(srcJson,"_",distJson);
        System.out.println(distJson);
    }

}
