package com.unistack.tamboo.sa.dd.jdbc;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.unistack.tamboo.sa.dd.jdbc.JdbcConsumerGroup.*;
import static com.unistack.tamboo.sa.dd.jdbc.JdbcConsumerGroup.whiteList;

/**
 * @author anning
 * @date 2018/6/21 下午2:08
 * @description: sql server dist
 */
public class ConsumerSQLSERVERWorker implements JdbcSinkWorker {
    private static  Logger logger = LoggerFactory.getLogger(ConsumerSQLSERVERWorker.class);
    private static Set<String> alterSqlList2 = new HashSet<>();
    private static Lock sqlServerLock = new ReentrantLock();

    @Override
    public boolean getOrCreateTable(String connectName, Connection connection, String tableName, JSONObject record) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            String isExistSql = "select * from sysobjects where name = '" + tableName + "' and TYPE = 'U'";
            ResultSet rs = statement.executeQuery(isExistSql);
            if (!rs.next()) {
                String createSql = getCreateSql(tableName, record, "sqlserver");
                statement.executeUpdate(createSql);
            }
            /*if (isExist == 0) {
                String createSql = getCreateSql(tableName, record, "sqlserver");
                statement.executeUpdate(createSql);
            }*/
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
            logger.error(e.toString());
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
        ArrayList<String> valueAsKey = new ArrayList<>();
        ArrayList<String> setValue = new ArrayList<>();
        ArrayList<String> sField = new ArrayList<>();
        ArrayList<String> incomingField = new ArrayList<>();
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            alterSatement = connection.createStatement();
            String insertSql;
            for (int i = 0; i < list.size(); i++) {
                record = list.get(i);
                if (StringUtils.isNotBlank(pkField)) {
                    insertSql = getUpsertSql(connectName, table_name, record, "sqlserver", alterSqlList2,
                            valueAsKey, setValue, sField, incomingField);
                } else insertSql = getInsertSql(connectName, table_name, record);
                statement.addBatch(insertSql);
            }

            sqlServerLock.lock();
            try {
                if (alterSqlList2.size() > 0) {
                    for (String alterSql : alterSqlList2) {
                        alterSatement.addBatch(alterSql);
                    }
                    alterSatement.executeBatch();
                    alterSqlList2.clear();
                }
            }finally {
                sqlServerLock.unlock();
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

    /*@Override
    public String getUpsertSql(String connectName, String tableName, JSONObject record) {

        return null;
    }*/

    @Override
    public String getInsertSql(String connectName, String tableName, JSONObject record) {
        StringBuilder builder = new StringBuilder("INSERT INTO \"");
        builder.append(tableName);
        builder.append("\" (");
        Set<String> rkey = record.keySet();
        ArrayList<String> valueList = new ArrayList<>();
        if (whiteList.size() > 0) {
//            ka = whiteList.toArray(new String[JdbcConsumerGroup.whiteList.size()]);
            List<String> list = new ArrayList<>();
            for (String whiteField : whiteList) {
                if (record.get(whiteField) != null) {
                    valueList.add("'" + record.get(whiteField) + "'");
                }else {
                    valueList.add("null");
                }
                list.add(whiteField);
            }
            builder.append(StringUtils.join(list, ","));
        } else {
            for (String key : record.keySet()) {
                //在这里可以判断数据类型 用来判断是否在添加字段时加上'' ,可以用来转换boolean类型
                valueList.add("'" + record.getString(key) + "'");
                if (!schemaMap.get(connectName).contains(key)) {
                    String addFieldSql = getAddFieldSql(tableName, key, record.get(key), "sqlServer");
                    alterSqlList2.add(addFieldSql);
                    schemaMap.get(connectName).add(key);
                }
            }
            builder.append(StringUtils.join(rkey, ","));
        }
        builder.append(") VALUES(");
        builder.append(StringUtils.join(valueList, ","));
        builder.append(")");
        return builder.toString();
    }

    @Override
    public JSONObject checkConfig(JSONObject json) {
        return DdUtil.succeedResult("");
    }
}
