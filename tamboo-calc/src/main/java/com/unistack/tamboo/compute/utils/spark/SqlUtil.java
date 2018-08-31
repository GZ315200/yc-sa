package com.unistack.tamboo.compute.utils.spark;

import com.unistack.tamboo.compute.exception.DataFormatErrorException;
import com.unistack.tamboo.compute.exception.SqlMatchException;
import com.unistack.tamboo.compute.utils.commons.CalcJsonUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class SqlUtil implements java.io.Serializable{
    private static Logger LOGGER = Logger.getLogger(SqlUtil.class);
    private static final Pattern p = Pattern.compile("@([\\.\\w]+)\\b");

    /**
     * 解析模糊的SQL 将其转换为预编译sql和 "参数"即目标值得节点<br/>
     * eg:有如下sql<br/>
     *  select SENDER,RECEIVER FROM USER WHERE ID = @META.ID <br/>
     *  最终要的结果是一个数组，数组有两项 <br/>
     *  第一项是预编译的sql: select SENDER,RECEIVER FROM USER WHERE ID = ? <br/>
     *  第二项是目标值得节点: [META.ID] 是一个List<br/>
     *  这样做是为了解耦，因为此时并不知道ID的值,只知道这个ID对应的值是META.ID,这个值可能从一个<br/>
     *  xml中获取也有可能从一个json中获取。
     * @param vagueSql
     *      模糊sql，类似于 select SENDER,RECEIVER FROM USER WHERE ID = @META.ID <br/>
     *      vagueSql的写法规定: 引用的值必须是 @level1.level2.level3
     * @return
     * 返回数组的第一项是一个字符串 <br/>
     * 第二项是一个 List<String> 里面存放参数值的目标节点<br/>
     */
    public Object[] ParseSql(String vagueSql) throws SqlMatchException{
        //模糊的SQL类似于注释中的update语句，其中的参数来源于另外一个xml文件
        Matcher m = p.matcher(vagueSql);

        List<String> nodeName = new ArrayList<>();
        boolean isMatch = false;
        while(m.find()){
            nodeName.add(m.group(1));
            isMatch = true;
        }

        if(!isMatch)
            throw new SqlMatchException("模糊SQL:["+vagueSql+"]书写不规范,无法解析!");

        String realSql = vagueSql;
        for(String nodeItem : nodeName){
            realSql = realSql.replace(nodeItem.trim(),"?");
        }

        Object[] result = new Object[2];
        result[0] = realSql;
        result[1] = nodeName;
        return result;
    }


    /**
     * 通过上一步操作的结果和目标值返回一条可执行的sql<br/>
     * @param parseSql
     * @param target
     * @return
     */
    public static String getExeSql(Object[] parseSql, JSONObject target) throws DataFormatErrorException {
        if(null == parseSql || parseSql.length != 2 || null == target){
            throw new IllegalArgumentException("参数异常！");
        }

        String preparSql = String.valueOf(parseSql[0]);
        List<String> nodeNames = (List<String>)parseSql[1];

        for(String item : nodeNames){
            String value = CalcJsonUtil.getValue(target,item);
            preparSql = preparSql.replaceFirst("\\?",value);
        }
        return preparSql.replace("@","");
    }


    public static List<JSONObject> resultSetToJSON(ResultSet rs){
        List<JSONObject> jsonList = new ArrayList<>();
        try {
            while(rs.next()){
                JSONObject result = new JSONObject();
                ResultSetMetaData metaData = rs.getMetaData();
                for (int i=1;i <= metaData.getColumnCount();i++){
                    String columnName =metaData.getColumnLabel(i);
                    Object value = rs.getObject(columnName);
                    result.put(columnName,value);
                }
                jsonList.add(result);
            }
            return jsonList.isEmpty() ? Collections.emptyList() : jsonList;
        }catch(SQLException e){
            e.printStackTrace();
            LOGGER.error("resultSetToJSON失败!");
            return Collections.emptyList();
        }finally{
            if(null != rs) {
                try{rs.close();}catch(SQLException e){e.printStackTrace();}
            }
        }
    }
}