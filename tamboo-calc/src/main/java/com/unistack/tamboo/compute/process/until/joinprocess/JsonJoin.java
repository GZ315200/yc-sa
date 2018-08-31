package com.unistack.tamboo.compute.process.until.joinprocess;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.model.DbColumns;
import com.unistack.tamboo.compute.utils.commons.CalcJsonUtil;
import com.unistack.tamboo.compute.utils.spark.SqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import scala.tools.nsc.Global;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


/**
 * @author hero.li
 */
public class JsonJoin implements java.io.Serializable{
    private static AtomicLong SELECT_SQL_ERROR_COUNT = new AtomicLong(0);
    private static AtomicLong SELECT_SQL_CORRECT_COUNT = new AtomicLong(0);
    private static AtomicLong UPDATE_SQL_CORRECT_COUNT = new AtomicLong(0);
    private static AtomicLong UPDATE_SQL_ERROR_COUNT = new AtomicLong(0);

    private static Logger LOGGER = Logger.getLogger(JsonJoin.class);
    private JSONObject config;
    private Connection conn;
    private Map<String,DbColumns> columnsMap;

    public JsonJoin(JSONObject config,Connection conn,Map<String,DbColumns> columnsMap){
        this.config = config;
        this.conn = conn;
        this.columnsMap = columnsMap;
    }

    public Optional<JSONObject> jsonJoin(String jsonMsgStr){
        JSONObject jsonMsg;
        try{
            jsonMsg = JSONObject.parseObject(jsonMsgStr);
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.info("SPARK:数据不是标准格式的JSON格式:"+jsonMsgStr);
            return Optional.empty();
        }

        JSONObject join = config.getJSONObject("join");
        String joinType = join.getString("joinType");
        String tableName = join.getString("tableName");
        JSONArray condition = join.getJSONArray("condition");
        JSONArray updateCol = join.getJSONArray("updateCol");

        switch(joinType){
            case "select":
                String selectSql = assembleSelect(tableName,condition,jsonMsg);
                Optional<List<JSONObject>> data = getData(selectSql,conn);
                JSONObject item =  data.isPresent() ? data.get().get(0) : null;
                return Optional.ofNullable(item);
            case "update":
                String updateSql = assembleUpdateSql(tableName,updateCol,condition,jsonMsg);
                getCount(updateSql,conn);
                return Optional.ofNullable(jsonMsg);
            default:
                throw new RuntimeException("不支持的join类型["+joinType+"]");
        }
    }


    /**
     * 通过表名tableName,和条件conditions 组装select类型的sql
     * @param tableName
     * @param conditions
     * @return
     */
    private String assembleSelect(String tableName,JSONArray conditions,JSONObject targetData){
        String first = "select * from "+tableName+" where ";
        return getWhereCondition(first,conditions,targetData).toString();
    }

    /**
     * 根据表的schema组装sql语句 where 之后的部分
     * @param first   前缀语句  eg: select * from t1 where    或者   update t1 set col1='A' where
     * @param conditions  条件的数组
     *      [{"tableCol":"filter_type","originCol":"filter_type"}]
     *         tableCol => 表的列名       originCol=>流数据中的列名
     *
     * @param targetData     具体的数据项/原始流数据
     * @return
     */
    private StringBuilder getWhereCondition(String first, JSONArray conditions,JSONObject targetData){
        StringBuilder seconds = StringUtils.isBlank(first) ? new StringBuilder() : new StringBuilder(first);

        for(int i=0;i<conditions.size();i++){
            JSONObject item = conditions.getJSONObject(i);
            String tableCol = item.getString("tableCol");
            String originCol = item.getString("originCol");

            String originData = null;
            try{
                originData = CalcJsonUtil.getValue(targetData,originCol);
            }catch(Exception e){
                LOGGER.error("数据["+targetData+"]没有该项["+originCol+"]",e);
            }


            if(i != 0){
                seconds.append(" and ");
            }
            DbColumns columns = columnsMap.get(tableCol);
            /**
             * 数据类型
             * 4   ->  int
             * 12  ->  varchar
             * 91  ->  date
             * 1   ->  char
             * -6      TINYINT
             * 5       SMALLINT
             * 4       int
             * -5      bigint
             * 7       FLOAT
             * 8       DOUBLE
             * 91      date
             * 92      time
             * 91      YEAr
             * 93      datetime
             * 93      TIMESTATE
             */
            int dataType = Integer.parseInt(columns.getDataType());
            if(dataType >= 4 && dataType <= 8){
                seconds.append(tableCol)
                        .append(" =")
                        .append(originData)
                        .append(" ");
            }else{
                seconds.append(tableCol);
                if(originData != null) {
                    seconds.append("='").append(originData).append("' ");
                }else{
                    seconds.append(" is null ");
                }
            }
        }
        return seconds;
    }


    private StringBuilder getSetCondition(String first,JSONArray conditions,JSONObject targetData){
        StringBuilder seconds = StringUtils.isBlank(first) ? new StringBuilder() : new StringBuilder(first);

        for(int i=0;i<conditions.size();i++){
            JSONObject item = conditions.getJSONObject(i);
            String tableCol = item.getString("tableCol");
            String originCol = item.getString("originCol");

            String originData = null;
            try{
                originData = CalcJsonUtil.getValue(targetData,originCol);
            }catch(Exception e){
                LOGGER.error("数据["+targetData+"]没有该项["+originCol+"]",e);
            }

            if(i != 0) {
                seconds.append(" , ");
            }
            DbColumns columns = columnsMap.get(tableCol);
            int dataType = Integer.parseInt(columns.getDataType());
            if(dataType >= 4 && dataType <= 8){
                String attr = item.getString("attr");
                seconds.append(tableCol)
                        .append(" = ");

                if("accumulate".equals(attr)) {
                    seconds.append(tableCol)
                            .append("+");
                }

                seconds.append(originData)
                        .append(" ");
            }else{
                seconds.append(tableCol)
                        .append("='")
                        .append(originData)
                        .append("' ");

            }
        }
        return seconds;
    }


    private Optional<List<JSONObject>> getData(String sql,Connection conn){
        try{
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            List<JSONObject> resultList = SqlUtil.resultSetToJSON(resultSet);
            LOGGER.info("SPARK:selectSql执行成功["+ SELECT_SQL_CORRECT_COUNT.incrementAndGet() +"]:sql="+sql +"  查询到的总条数:"+resultList.size());
            return resultList.isEmpty() ? Optional.empty() : Optional.of(resultList);
        }catch(SQLException e){
            LOGGER.error("SPARK:selectSql执行异常["+ SELECT_SQL_ERROR_COUNT.incrementAndGet() +"]:sql="+sql);
        }
        return Optional.empty();
    }


    /**
     * 按照表名tableName、要修该的列updateCol、条件组装sql
     * @param tableName
     * @param updateCol
     * @param conditions
     * @return
     */
    private String assembleUpdateSql(String tableName,JSONArray updateCol,JSONArray conditions,JSONObject jsonMsg){
        String head = "update "+tableName +" set ";
        StringBuilder head_sets = getSetCondition(head, updateCol,jsonMsg);
        head_sets.append(" where ");
        StringBuilder updateSql = getWhereCondition(head_sets.toString(),conditions,jsonMsg);
        return updateSql.toString();
    }

    private Optional<Integer> getCount(String sql,Connection conn){
        try{
            Statement statement = conn.createStatement();
            int count = statement.executeUpdate(sql);
            LOGGER.info("SPARK:updateSql执行成功["+UPDATE_SQL_CORRECT_COUNT.incrementAndGet()+"] sql="+sql+"  影响的条数:"+count);
            return  Optional.of(count);
        }catch(SQLException e){
            LOGGER.error("SPARK:updateSql执行异常["+ UPDATE_SQL_ERROR_COUNT.incrementAndGet()+"] sql="+sql);
            return Optional.empty();
        }
    }
}
