package com.unistack.tamboo.commons.utils.calc;

import com.alibaba.fastjson.JSONObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author hero.li
 */
public class DbMetaDataUtil{
    /**
     * 通过数据库信息获取某张表的schema
     * @param dataSourceInfo
     * @return
     */
    public static Optional<List<JSONObject>> getColumnInfo(JSONObject dataSourceInfo){
        String dbType = dataSourceInfo.getString("dbType");
        String ip = dataSourceInfo.getString("ip");
        String port = dataSourceInfo.getString("port");
        String dbName = dataSourceInfo.getString("dbName");
        String url = toUrl(dbType, ip, port, dbName);
        String username = dataSourceInfo.getString("username");
        String password = dataSourceInfo.getString("password");
        String tableName = dataSourceInfo.getString("tableName");

        String driver = "MYSQL".equalsIgnoreCase(dbType)?"com.mysql.cj.jdbc.Driver":("ORACLE".equalsIgnoreCase(dbType)?"oracle.jdbc.driver.OracleDriver":"com.microsoft.sqlserver.jdbc.SQLServerDriver");
        Connection conn = null;
        try{
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getColumns(conn.getCatalog(),"%", tableName, "%");

            List<JSONObject> result = new ArrayList<>();
            while(rs.next()){
                JSONObject column  = new JSONObject();
                String DATA_TYPE = numberToStringType(rs.getString("DATA_TYPE"));
                int COLUMN_SIZE = rs.getInt("COLUMN_SIZE");
                String COLUMN_NAME = rs.getString("COLUMN_NAME");

                column.put("DATA_TYPE",DATA_TYPE);
                column.put("COLUMN_SIZE",COLUMN_SIZE);
                column.put("COLUMN_NAME",COLUMN_NAME);
                result.add(column);
            }
            return Optional.of(result);
        }catch(Exception e){
            e.printStackTrace();
            return Optional.empty();
        }finally{
            if(conn != null){try{conn.close();}catch(SQLException e){e.printStackTrace();}}
        }
    }

    public static String toUrl(String dbType,String ip,String port,String dbName){
        if("MYSQL".equals(dbType)){
            return "jdbc:mysql://"+ip+":"+port+"/"+dbName+"?useSSL=false&characterEncoding=utf8";
            //+"?characterEncoding=utf-8&useSSL=true";
        }else if("ORACLE".equals(dbType)){
            return "jdbc:oracle:thin:@"+ip+":"+port+":"+dbName;
        }else if("SQLSERVER".equals(dbType)){
            return "jdbc:sqlserver://"+ip+":"+port+";databaseName="+dbName;
        }
        return "";
    }


    /**
     * 	-6->tinyint,5->smallint1,4->mediumint1,4->int,
     * 	-5->bigint,
     * 	7 ->float1,
     * 	8 ->double,91->date,92-> time,93->datetime,93->timestamp1
     * 	int 	->  4   5 -6
     * 	long 	->  -5
     * 	float   ->  7
     * 	double  ->  8
     * @param type
     * @return
     */
    private static String numberToStringType(String type){
        if("4".equals(type) || "5".equals(type) || "-6".equals(type)){
            return "int";
        }
        if("-5".equals(type)){
            return "long";
        }
        if("7".equals(type)){
            return "float";
        }
        if("8".equals(type)){
            return "double";
        }

        return "string";
    }


}
