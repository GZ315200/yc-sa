package com.unistack.tamboo.compute.utils.commons;


import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.utils.spark.CalcConfig;
import org.apache.log4j.Logger;
import java.sql.*;
import java.util.Optional;


/**
 * @author hero.li
 */
public class JDBCUtil{
    private static Logger LOGGER = Logger.getLogger(JDBCUtil.class);

    public static JSONObject getAppConf(String appName){
        JSONObject result = new JSONObject();
        Connection conn = null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(CalcConfig.CALC_JDBC_URL,CalcConfig.CALC_JDBC_USER,CalcConfig.CALC_JDBC_PASSWORD);
            String sql = "select * from  data_flow_work where CALC_ID='"+appName+"'";
            PreparedStatement ptmt = conn.prepareStatement(sql);
            ResultSet resultSet = ptmt.executeQuery();


            if(resultSet.next()){
                String calcConf = resultSet.getString("CALC_CONF");
                String wfId = resultSet.getString("WF_ID");
                result.put("calcConf",calcConf);
                result.put("wfId",wfId);
                resultSet.close();
                return result;
            }
            LOGGER.info("SPARK:从数据库中读取到的配置信息:"+result);
            return result;
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }catch(SQLException e){
            e.printStackTrace();
        }finally{
            if(null != conn){try{conn.close();}catch(SQLException e){e.printStackTrace();}}
        }
        return result;
    }

    public static Optional<JSONObject> getDataFlowInfo(Connection conn, String appName){
        PreparedStatement ps = null;
        try{
            ps = conn.prepareStatement("select * from data_flow_info where wf_id in (select wf_id from data_flow_work where calc_id = '"+appName+"')");
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                JSONObject result = new JSONObject();
                result.put("wfId",resultSet.getString("WF_ID"));
                result.put("flag",resultSet.getString("FLAG"));
                result.put("wfName",resultSet.getString("WF_NAME"));
                result.put("wfDsc",resultSet.getString("WF_DSC"));
                result.put("wfCpu",resultSet.getString("CALC_CPU"));
                result.put("wfMem",resultSet.getString("CALC_MEM"));
                return Optional.of(result);
            }
            return Optional.empty();
        }catch(SQLException e){
            e.printStackTrace();
            return Optional.empty();
        }finally{
            if(null != ps){try{ps.close();}catch(SQLException e){e.printStackTrace();}}
        }
    }



    /**
     * 获取一个数据连接池的单例实例 <br/>
     * 如果是同一个数据库则返回同一个对象 identifier相同则代表同一个数据库 <br/>
     *
     * @param datasource  {"password":"welcome1","port":"3308","ip":"192.168.1.191","dbName":"test","dbType":"MYSQL","username":"root"} <br/>
     * @return
     */
    public static Optional<Connection> getConnection(JSONObject datasource){
        String ip = datasource.getString("ip");
        String port = datasource.getString("port");
        String dbName = datasource.getString("dbName");
        try{
            String dbType   = datasource.getString("dbType");
            String url      = toUrl(dbType,ip,port,dbName);
            String user = datasource.getString("username");
            String password = datasource.getString("password");
            LOGGER.info("SPARK:dbType="+dbType+" ,url="+url+" ,username="+user+" ,password="+password);
            String driver =  "mysql".equalsIgnoreCase(dbType) ? "com.mysql.cj.jdbc.Driver" : ("oracle".equals(dbType)?"oracle.jdbc.driver.OracleDriver":"com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(url,user,password);
            return Optional.of(conn);
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.info("SPARK:数据库连接池创建异常!");
            return Optional.empty();
        }
    }

    private static String toUrl(String dbType,String ip,String port,String dbName){
        String mysql = "MYSQL";
        if(mysql.equalsIgnoreCase(dbType)){
            return "jdbc:mysql://"+ip+":"+port+"/"+dbName+"?characterEncoding=utf-8&useSSL=false";
        }

        String oracle = "ORACLE";
        if(oracle.equals(dbType)){
            return "jdbc:oracle:thin:@"+ip+":"+port+":"+dbName;
        }

        String sqlServer = "SQLSERVER";
        if(sqlServer.equals(dbType)){
            return "jdbc:sqlserver://"+ip+":"+port+";databaseName="+dbName;
        }
        return "";
    }

}