package com.unistack.tamboo.mgt.utils;


import com.unistack.tamboo.mgt.model.Filter;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class DBUtil {
    private static String driver="com.mysql.jdbc.Driver";
    private static String url="jdbc:mysql://192.168.1.191:3308/tamboo_manager";
    private static String user="root";
    private static String password="welcome1";
    private static Properties jdbcProp = new Properties();

    private static Connection conn;
    static{
        conn = getConnection();
    }

    private static  String queryAll = "select * from filter";
    private static Connection getConnection(){
        try{
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url,user,password);
            return conn;
        }catch(ClassNotFoundException e){
            e.printStackTrace();
            throw new RuntimeException("mysql驱动包异常!");
        }catch(SQLException e){
            e.printStackTrace();
            throw new RuntimeException("Connection初始化出错!");
        }
    }


    private Optional<Object> executeSQL(String sql){
        boolean isQuery = sql.trim().startsWith("S") || sql.trim().startsWith("s");
        try{
            Statement statement = conn.createStatement();
            Object result = isQuery ? statement.executeQuery(sql) : statement.executeUpdate(sql);
            return Optional.of(result);
        }catch(SQLException e){
            e.printStackTrace();
            return Optional.empty();
        }
    }


    public Optional<List<Filter>> query(String sql){
        Optional<Object> result = executeSQL(sql);
        return result.isPresent() ? resultToFilter((ResultSet)result.get()) : Optional.empty();
    }


    public Optional<List<Filter>> queryAll(){
        return query(queryAll);
    }

    public Optional<Integer> update(String sql){
        Optional<Object> result = executeSQL(sql);
        return result.isPresent() ? Optional.ofNullable((Integer)result.get()) : Optional.empty();
    }

    /**
     * 把一个ResultSet转换为Filter <br/>
     * 这儿有很大的优化空间,先这样写吧！
     * @param rs
     * @return
     */
    private Optional<List<Filter>> resultToFilter(ResultSet rs){
        Field[] fields = Filter.class.getDeclaredFields();
        List<Filter> resultList = new ArrayList<>();
        try{
            while(rs.next()){
                Filter f = new Filter();
                for(Field field : fields){
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    String typeName = field.getType().getTypeName();
                    if("int".equals(typeName)){
                        int result = rs.getInt(fieldName);
                        field.set(f,result);
                    }else if("java.lang.String".equals(typeName)){
                        String result = rs.getString(fieldName);
                        field.set(f,result);
                    }else if("java.com.unistack.tamboo.commons.tools.util.Date".equals(typeName)){
                        Date result = rs.getDate(fieldName);
                        field.set(f,result);
                    }
                }
                resultList.add(f);
            }
            return Optional.of(resultList);
        }catch(SQLException e){
            e.printStackTrace();
            return Optional.empty();
        } catch (IllegalAccessException e){
            e.printStackTrace();
            return Optional.empty();
        }
    }


}
