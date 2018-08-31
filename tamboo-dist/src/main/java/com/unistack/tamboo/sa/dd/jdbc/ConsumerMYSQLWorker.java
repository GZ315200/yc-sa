package com.unistack.tamboo.sa.dd.jdbc;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.constant.DdType;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import com.unistack.tamboo.sa.dd.util.JdbcUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.unistack.tamboo.sa.dd.jdbc.JdbcConsumerGroup.*;

/**
 * @author anning
 * @date 2018/6/7 下午6:12
 * @description: 下发到mysql
 */
public class ConsumerMYSQLWorker implements JdbcSinkWorker {
    private static  Logger logger = LoggerFactory.getLogger(ConsumerMYSQLWorker.class);
    private static Lock mysqlWorkerLock = new ReentrantLock();
    private static Set<String> alterSqlList = new HashSet<>();

    /**
     * 插入数据
     *
     * @param connection 数据库连接
     * @param json       参数
     * @param list       list record
     * @return
     */
    @Override
    public JSONObject insertInto(String connectName, Connection connection, JSONObject json, List<JSONObject> list) {
        JSONObject result = new JSONObject();
        String table_name = json.getString("table_name");
//        String whitelist = json.getString("whitelist");
        JSONObject record = new JSONObject();

        Statement statement = null;
        Statement alterSatement = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            alterSatement = connection.createStatement();
            if (StringUtils.isNotBlank(pkField)) {
                for (int i = 0; i < list.size(); i++) {
                    record = list.get(i);
                    String insertSql = getUpsertSql(connectName, table_name, record);
//                    System.out.println(insertSql);
                    statement.addBatch(insertSql);
                }
            } else {
                for (int i = 0; i < list.size(); i++) {
//                    String value = list.get(i).value();
                    record = list.get(i);
                    String insertSql = getInsertSql(connectName, table_name, record);
//                    System.out.println(insertSql);
                    statement.addBatch(insertSql);
                }
            }
            mysqlWorkerLock.lock();
            try {
                if (alterSqlList.size() > 0) {
                    for (String alterSql : alterSqlList) {
                        alterSatement.addBatch(alterSql);
                    }
                    alterSatement.executeBatch();
                    alterSqlList.clear();
                }
            } finally {
                mysqlWorkerLock.unlock();
            }
            statement.executeBatch();

            result = DdUtil.succeedResult("");
        } catch (Exception e) {
            result = DdUtil.failResult("本次批量导入未能完全成功!");
            logger.error("数据导入到数据库失败 ======>" + e.toString());
            logger.error("出错数据：" + record.toString());

            //目前不回滚
            /*try {
                conn.rollback();
            } catch (SQLException e1) {
                logger.error("数据写入数据库出现异常，回滚失败！");
            }*/
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (alterSatement != null) {
                    alterSatement.close();
                }
                //这一批次数据有错误的情况也要提交
                connection.commit();
            } catch (SQLException e) {
                logger.error(e.toString());
            }
        }
        return result;
    }

    public String getInsertSql(String connectName, String tableName, JSONObject record) {

        StringBuilder builder = new StringBuilder("INSERT INTO ");
        builder.append(tableName);
        builder.append("(");
        Set<String> rkey = record.keySet();
        ArrayList<String> valueList = new ArrayList<>();
        if (whiteList.size() > 0) {
//            ka = whiteList.toArray(new String[JdbcConsumerGroup.whiteList.size()]);
            for (String whiteField : whiteList) {
                if (!schemaMap.get(connectName).contains(whiteField)) {
                    String addFieldSql = getAddFieldSql(tableName, whiteField, record.get(whiteField), "mysql");
                    alterSqlList.add(addFieldSql);
//                    System.out.println(addFieldSql);
                    schemaMap.get(connectName).add(whiteField);
                }
                valueList.add("'" + record.getOrDefault(whiteField, null) + "'");

            }
            builder.append(StringUtils.join(whiteList, ","));
        } else {
            for (String key : record.keySet()) {
                //在这里可以判断数据类型 用来判断是否在添加字段时加上'' ,可以用来转换boolean类型
                valueList.add("'" + record.getString(key) + "'");
                if (!schemaMap.get(connectName).contains(key)) {
                    String addFieldSql = getAddFieldSql(tableName, key, record.get(key), "mysql");
                    alterSqlList.add(addFieldSql);
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

    /**
     * 先建表 再获得表元数据信息
     *
     * @param connection
     * @param tableName
     * @param record
     */
    public boolean getOrCreateTable(String connectName, Connection connection, String tableName, JSONObject record) {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            String createSql = getCreateSql(tableName, record, "mysql");
            int isCreate = statement.executeUpdate(createSql);
            ResultSet resultSet = statement.executeQuery("SELECT * FROM  " + tableName);
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

    public String getUpsertSql(String connectName, String tableName, JSONObject record) {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ");
        builder.append(tableName);
        builder.append("(");
        Set<String> rkey = record.keySet();
        ArrayList<String> keyList = new ArrayList<>();
        ArrayList<String> valueList = new ArrayList<>();
        if (whiteList.size() > 0) {
            for (String whiteField : record.keySet()) {
                if (whiteList.contains(whiteField)) {
                    if (!JdbcConsumerGroup.schemaMap.get(connectName).contains(whiteField)) {
                        String addFieldSql = getAddFieldSql(tableName, whiteField, record.get(whiteField), "mysql");
                        alterSqlList.add(addFieldSql);
                        schemaMap.get(connectName).add(whiteField);
                    }
                    if (Objects.isNull(record.get(whiteField))) {
                        valueList.add("null");
                    } else {
                        valueList.add("'" + record.get(whiteField) + "'");
                    }
                    keyList.add(whiteField);
                }
            }

            builder.append(StringUtils.join(keyList, ","));
        } else {
            for (String key : record.keySet()) {
                //在这里可以判断数据类型 用来判断是否在添加字段时加上'' ,可以用来转换boolean类型
                valueList.add("'" + record.getString(key) + "'");
                if (!JdbcConsumerGroup.schemaMap.get(connectName).contains(key)) {
                    String addFieldSql = getAddFieldSql(tableName, key, record.get(key), "mysql");
                    alterSqlList.add(addFieldSql);
                    schemaMap.get(connectName).add(key);
                }
            }
            builder.append(StringUtils.join(rkey, ","));
        }

        builder.append(") VALUES(");
        builder.append(StringUtils.join(valueList, ","));
        builder.append(")");

//        if (StringUtils.isNotBlank(JdbcConsumerGroup.pkField)){
        builder.append(" on duplicate key update ");
        builder.append("");
        ArrayList<String> upsertList = new ArrayList<>();
        if (whiteList.size() > 0) {
            for (String key : keyList) {
                if (record.get(key) == null) {
                    upsertList.add(key + "=null");
                } else {
                    upsertList.add(key + "='" + record.get(key) + "'");
                }
//                upsertList.add(key + "=" + record.getOrDefault(key, null));
            }
        } else {
            for (String key : record.keySet()) {
//                upsertList.add(key + "=" + record.getOrDefault(key, null));
                if (record.get(key) == null) {
                    upsertList.add(key + "=null");
                } else {
                    upsertList.add(key + "='" + record.get(key) + "'");
                }
            }
        }
        builder.append(StringUtils.join(upsertList, ","));

//        }
        return builder.toString();
    }

}
