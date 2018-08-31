package com.unistack.tamboo.sa.dd.jdbc;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import static com.unistack.tamboo.sa.dd.jdbc.JdbcConsumerGroup.*;
import static java.util.stream.Collectors.toList;

/**
 * @author anning
 * @date 2018/6/27 下午3:33
 * @description: sink pgsql
 */
public class ConsumerPOSTGREWorker implements JdbcSinkWorker {
    private static  Logger logger = LoggerFactory.getLogger(ConsumerPOSTGREWorker.class);
    private static volatile Set<String> alterPostGreSqlList = new HashSet<>();
    private static Lock postGreLock = new ReentrantLock();

    @Override
    public boolean getOrCreateTable(String connectName, Connection connection, String tableName, JSONObject record) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            String isExistSql = "select 1 from information_schema.tables where table_schema = 'public' and table_name = '" + tableName + "'";
            ResultSet rs = statement.executeQuery(isExistSql);
            int isExistInt = 0;
            if (rs.next()) {
                isExistInt = rs.getInt(1);
            }
            if (isExistInt == 0) {
                String createSql = getCreateSql(tableName, record, "postgre");
                statement.executeUpdate(createSql);
            }
            ResultSet resultSet = statement.executeQuery("SELECT * FROM \"" + tableName + "\"");
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            ArrayList<String> filedList = new ArrayList<>();
            ArrayList<String> typeList = new ArrayList<>();
            for (int i = 0; i < columnCount; i++) {
                String columnName = metaData.getColumnName(i + 1);
                String columnTypeName = metaData.getColumnTypeName(i + 1);
                filedList.add(columnName);
                typeList.add(columnTypeName);
            }
            schemaMap.put(connectName, filedList);
            schemaTypeMap.put(connectName, typeList);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public JSONObject insertInto(String connectName, Connection connection, JSONObject json, List<JSONObject> list) {
        JSONObject result;
        String table_name = json.getString("table_name");
        Statement statement = null;
        Statement alterSatement = null;
        JSONObject record = new JSONObject();
        List<String> setExValue = new ArrayList<>();
        List<String> collect = new ArrayList<>();
        List<Object> valueList = new ArrayList<>();


        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            alterSatement = connection.createStatement();
            String insertSql;
            for (int i = 0; i < list.size(); i++) {
                record = list.get(i);
                insertSql = StringUtils.isNotBlank(pkField) ? getUpsertSql(connectName, table_name, record,
                        setExValue, collect, valueList) : getInsertSql(connectName, table_name, record);
                statement.addBatch(insertSql);
            }

            postGreLock.lock();
            try {
                if (alterPostGreSqlList.size() > 0) {
                    for (String alterSql : alterPostGreSqlList) {
                        alterSatement.addBatch(alterSql);
                    }
                    alterSatement.executeBatch();
                    alterPostGreSqlList.clear();
                }
            } finally {
                postGreLock.unlock();
            }
            statement.executeBatch();
            result = DdUtil.succeedResult(" ");
        } catch (Exception e) {
            result = DdUtil.failResult("本次批量导入未能完全成功!");
            logger.error("数据导入到数据库失败 ======> " + e.toString());
            logger.error("出错数据：" + record.toString());
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (alterSatement != null) {
                    alterSatement.close();
                }
                connection.commit();
            } catch (SQLException e) {
                logger.error(e.toString());
            }
        }
        return result;
    }

    @Override
    public String getInsertSql(String connectName, String tableName, JSONObject record) {
        StringBuilder builder = new StringBuilder("INSERT INTO ");
        builder.append(tableName);
        builder.append(" (");
        Set<String> rkey = record.keySet();
        List<String> valueList = new ArrayList<>();
        if (whiteList.size() > 0) {
            Stream<String> ws = whiteList.stream();
            Stream<String> filterStream = ws.filter(x -> record.get(x) != null);
            List<String> list = filterStream.collect(toList());
            valueList = list.stream().map(x -> "'" + record.get(x) + "'").collect(toList());
            builder.append(StringUtils.join(list, ","));



        } else {
            for (String key : record.keySet()) {
                //在这里可以判断数据类型 用来判断是否在添加字段时加上'' ,可以用来转换boolean类型
                valueList.add("'" + record.getString(key) + "'");
                if (!schemaMap.get(connectName).contains(key)) {
                    String addFieldSql = getAddFieldSql(tableName, key, record.get(key), "postgre");
                    schemaMap.get(connectName).add(key);
                    alterPostGreSqlList.add(addFieldSql);
                }
            }
            builder.append(StringUtils.join(rkey, ","));
        }
        builder.append(") VALUES(");
        builder.append(StringUtils.join(valueList, ","));
        builder.append(")");
        return builder.toString();
    }

    public String getUpsertSql(String connectName, String tableName, JSONObject record, List<String> setExValue, List<String> collect, List<Object> valueList) {
        Set<String> rkey = record.keySet();
//        List<String> setExValue = new ArrayList<>();
        StringBuilder b = new StringBuilder();
//        List<String> flume = new ArrayList<>();
//        List<Object> valueList = new ArrayList<>();
        b.append("INSERT INTO ");
        b.append(tableName);
        b.append("(");
        if (whiteList.size() > 0) {
            for (String wField:whiteList){
                collect.add(wField);
                setExValue.add(wField+"= EXCLUDED." + wField);
                if (Objects.isNull(record.get(wField))){
                    valueList.add("null");
                }else {
                    valueList.add("'" + record.get(wField) + "'");
                }
            }
        } else {   //没有whiteList的情况
            for (String key : rkey) {
                if (!schemaMap.get(connectName).contains(key)) {
                    alterPostGreSqlList.add(getAddFieldSql(tableName, key, record.get(key), "postgre"));
                    schemaMap.get(connectName).add(key);
                }
                collect.add(key);
                valueList.add("'"+record.get(key)+"'");
                setExValue.add(key + "= EXCLUDED." + key);
            }
        }
        b.append(StringUtils.join(collect, ","));
        b.append(") VALUES(");
        b.append(StringUtils.join(valueList, ","));
        b.append(") ON CONFLICT (");
        b.append(pkField);
        b.append(") DO UPDATE SET ");
        b.append(StringUtils.join(setExValue, ","));

        setExValue.clear();
        valueList.clear();
        collect.clear();

        return b.toString();
    }
}
