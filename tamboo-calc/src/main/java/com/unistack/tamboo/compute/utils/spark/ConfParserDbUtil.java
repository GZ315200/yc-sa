package com.unistack.tamboo.compute.utils.spark;

import com.unistack.tamboo.compute.exception.DataFormatErrorException;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hero.li
 * 主要是标签解析模块使用的数据库帮助类 <br/>
 * 这个类作为数据库的帮助类，和数据库打交道 <br/>
 * MYSQL_CONNS 用来持有与mysql的连接 <br/>
 * ORACLE_CONNS 用来持有与oracle的连接 <br/>
 * correspond 用来存放key和类型的对应关系，即当需要通过某个key取出该key对应的连接时，<br/>
 * 先判断这个key对应的是那种数据库的连接，然后选择从MYSQL_CONNS或者ORACLE_CONNS中去拿数据<br/>
 */
public class ConfParserDbUtil implements java.io.Serializable{
    private static Logger LOGGER = Logger.getLogger(ConfParserDbUtil.class);
    private  static Map<String,Connection> MYSQL_CONNS = new ConcurrentHashMap<>();
    private  static Map<String,Connection> ORACLE_CONNS = new ConcurrentHashMap<>();
    private  static Map<String,String> CORRESPOND = new ConcurrentHashMap<>();

    /**
     * 通过连接信息连接数据库并把连接持有 <br/>
     *  <dbSource type="mysql" sourceId="mysql1">
     *        <url></url>
     *        <username></username>
     *        <password></password>
     *  </dbSource>
     * @param e
     * @return
     */
    public boolean addConnection(Element e){
        String type = e.attributeValue("type");
        String sourceId = e.attributeValue("sourceId");

        String driver;
        boolean isMysql = "mysql".equals(type);
        if(isMysql){
            driver = "com.mysql.jdbc.Driver";
            CORRESPOND.put(sourceId,"mysql");
        }else{
            driver = "oracle.jdbc.OracleDriver";
            CORRESPOND.put(sourceId,"oracle");
        }

        //这儿可以优化
        String dbUrl = e.element("url").getText()+"?useSSL=false&characterEncoding=utf8";
        String user = e.element("username").getText();
        String password = e.element("password").getText();
        Connection currentConn = getConnection(sourceId);
        if(null != currentConn){
            LOGGER.info("sourceId["+sourceId+"]已经初始化过，不必重复初始化！");
            return true;
        }

        LOGGER.info("sourceId=["+sourceId+"]  类型为["+(isMysql?"mysql":"oracle")+"]  url["+dbUrl+"]  username["+user+"]  password["+password+"]");
        try {
            Class.forName(driver);
            Connection conn = DriverManager.getConnection(dbUrl,user,password);
            if(isMysql) {
                MYSQL_CONNS.put(sourceId,conn);
            } else {
                ORACLE_CONNS.put(sourceId,conn);
            }
        }catch(ClassNotFoundException e1){
            e1.printStackTrace();
            LOGGER.error("驱动加载异常.....sourceId=["+sourceId+"]初始化",e1);
            return false;
        }catch(SQLException e1){
            e1.printStackTrace();
            LOGGER.error("连接获取异常...",e1);
            return false;
        }
        LOGGER.info("sourceId");
        return true;
    }

    public Connection getConnection(String sourceId){
        String type = CORRESPOND.get(sourceId);
        if(null == type){
            LOGGER.info(sourceId+" 没有对应的数据库类型,无法获取连接!");
            return null;
        }

        switch(type){
            case "mysql"  : return MYSQL_CONNS.get(sourceId);
            case "oracle" : return ORACLE_CONNS.get(sourceId);
        }
        LOGGER.info(sourceId+" 对应的数据库类型既不是mysql也不是oracle，无法获取连接，返回null");
        return null;
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
     * @throws DataFormatErrorException
     */
    public Object executeSQL(String sourceId,String sql) throws DataFormatErrorException {
        if(null == sql || "".equals(sql)) {
            throw new IllegalArgumentException("参数错误!该sql不能执行sql="+sql);
        }

        boolean isUpdate = sql.trim().startsWith("u") || sql.trim().startsWith("U");
        Connection conn = getConnection(sourceId);
        if(null == conn){
            throw new DataFormatErrorException("无效的sourceId,["+sourceId+"]对应的数据库连接为空!");
        }

        try {
            Statement statement = conn.createStatement();
            if(isUpdate) {
                return statement.executeUpdate(sql);
            } else {
                return statement.executeQuery(sql);
            }
        }catch(SQLException e){
            e.printStackTrace();
            LOGGER.error("sql执行异常,sql="+sql,e);
            //throw new DataFormatErrorException("sql执行异常,sql="+sql);
            return null;
        }
    }

    public int executeUpdate(String sourceId,String sql){
        try {
            return (Integer)executeSQL(sourceId,sql);
        }catch(DataFormatErrorException e) {
            e.printStackTrace();
            return -2;
        }
    }

    public ResultSet executeQuery(String sourceId, String sql){
        try{
            return (ResultSet) executeSQL(sourceId,sql);
        }catch(DataFormatErrorException e) {
            e.printStackTrace();
            return null;
        }
    }

}
