package com.unistack.tamboo.compute.process.until.dataclean.util;

import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.InvalidParameterException;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class DBUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(DBUtil.class);

    private  static Map<String,Connection> MYSQL_CONNS = new ConcurrentHashMap<>();
    private  static Map<String,Connection> ORACLE_CONNS = new ConcurrentHashMap<>();
    private  static Map<String,String> correspond = new ConcurrentHashMap<>();

    /**
     * 通过连接信息连接数据库并把连接持有 <br/>
     *  <dbSource type="mysql" sourceId="mysql1">
     *        <url></url>
     *        <username></username>
     *        <password></password>
     *  </dbSource>
     *
     *
     * @param e
     * @return
     */
    public void addConnection(Element e){

        String type = e.attributeValue("type");
        String sourceId = e.attributeValue("sourceId");

        String driver;
        boolean isMysql = "mysql".equals(type);
        if(isMysql){
            driver = "com.mysql.jdbc.Driver";
            correspond.put(sourceId,"mysql");
        }else{
            driver = "oracle.jdbc.OracleDriver";
            correspond.put(sourceId,"oracle");
        }

        String dbUrl = e.element("url").getText()+"?useSSL=false&characterEncoding=utf8";
        String user = e.element("username").getText();
        String password = e.element("password").getText();
        LOGGER.info("url["+dbUrl+"]  username["+user+"]  password["+password+"]");
        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(dbUrl,user,password);
            if(isMysql)
                MYSQL_CONNS.put(sourceId,conn);
            else
                ORACLE_CONNS.put(sourceId,conn);
        }catch(ClassNotFoundException e1){
            e1.printStackTrace();
        }catch(SQLException e1){
            e1.printStackTrace();
        }
    }

    public Connection getConnection(String sourceId){
        String type = correspond.get(sourceId);
        if("mysql".equals(type)){
            return MYSQL_CONNS.get(sourceId);
        }else{
            return ORACLE_CONNS.get(sourceId);
        }
    }

    /**
     * 执行修改语句，包括insert，update，delete<br/>
     * @param updateElement 代表存储SQL语句的叶子节点,如下所示: <br/>
     * <updateSql id="source1">
     *   update xxx set pdate = @FlightInfo.Pdate where aocId = @FlightInfo.AocId
     * </updateSql>
     *
     * @param originEle  原始收到的xml信息,执行sql所用的参数也在这个xml里面<br/>
     * @param flag  1 代表执行update 返回int
     *              2 代表执行select 返回ResultSet
     * @return
     */

    /**
     * 执行SQL语句 <br/>
     * @param sourceId 数据库id编号 <br/>
     * @param sql
     * @return
     * @throws InvalidParameterException
     */
    public Object executeSQL(String sourceId, String sql) throws InvalidParameterException {
        if(null == sql || "".equals(sql))
            throw new IllegalArgumentException("参数错误!该sql不能执行sql="+sql);

        String head = sql.substring(0,1);
        boolean isUpdate = head.equalsIgnoreCase("u");
        Connection conn = getConnection(sourceId);
        if(null == conn){
            throw new InvalidParameterException("无效的sourceId,["+sourceId+"]对应的数据库连接为空!");
        }

        try {
            Statement statement = conn.createStatement();
            if(isUpdate)
                return statement.executeUpdate(sql);
            else
                return statement.executeQuery(sql);
        }catch(SQLException e){
            e.printStackTrace();
            LOGGER.error("sql执行异常,sql="+sql,e);
            throw new InvalidParameterException("sql执行异常,sql="+sql);
        }
    }

    public int executeUpdate(String sourceId, String sql){
        try {
            return (Integer)executeSQL(sourceId,sql);
        }catch(InvalidParameterException e) {
            e.printStackTrace();
            return -2;
        }
    }


    public ResultSet executeQuery(String sourceId, String sql){
        try {
            return (ResultSet) executeSQL(sourceId,sql);
        }catch (InvalidParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

}