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
import java.util.stream.Stream;

import static com.unistack.tamboo.sa.dd.jdbc.JdbcConsumerGroup.*;
import static java.util.stream.Collectors.toList;

/**
 * @author anning
 * @date 2018/6/20 下午2:51
 * @description: oracle
 */
public class ConsumerORACLEWorker implements JdbcSinkWorker {
    private static  Logger logger = LoggerFactory.getLogger(ConsumerORACLEWorker.class);
    private static Set<String> alterSqlList = new HashSet<>();
    private static Lock oracleLock = new ReentrantLock();

    @Override
    public boolean getOrCreateTable(String connectName, Connection connection, String tableName, JSONObject record) {
        Statement statement =null;
        try {
            statement = connection.createStatement();
            String isExistSql = "select count(*) from user_tables where table_name = '" + tableName + "'";
            ResultSet rs = statement.executeQuery(isExistSql);
            int isExist = 0;
            if (rs.next()) {
                isExist = rs.getInt(1);
            }
            if (isExist == 0) {
                String createSql = getCreateSql(tableName, record, "oracle");
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
            logger.error(e.toString());
            return false;
        }finally {
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
            for (int i = 0; i < list.size(); i++) {
                record = list.get(i);
                String insertSql;
                if (StringUtils.isNotBlank(pkField)) {
                    insertSql = getUpsertSql(connectName, table_name, record,"oracle",alterSqlList,
                            valueAsKey,setValue,sField,incomingField);
                } else insertSql = getInsertSql(connectName, table_name, record);
//                System.out.println(insertSql);
                statement.addBatch(insertSql);
            }
            oracleLock.lock();
            try {
                if (alterSqlList.size() > 0) {
                    for (String alterSql : alterSqlList) {
                        alterSatement.addBatch(alterSql);
                    }
                    alterSqlList.clear();
                    alterSatement.executeBatch();
                }
            }finally {
                oracleLock.unlock();
            }
            statement.executeBatch();
            result = DdUtil.succeedResult(" ");
        } catch (Exception e) {
            result = DdUtil.failResult("本次批量导入未能完全成功!");
            logger.error("数据导入到数据库失败 ======>" + e.toString());
            logger.error("出错数据：" + record.toString());
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


    /*
    merge into "sinkjdbc2" s using(select 'anning2' as name ,22 as age from dual) incoming on(incoming.name=s.name)
        when matched then
            update set s.age=incoming.age
        when not matched then
            insert(s.name,s.age) values(incoming.name,incoming.age)
     */


    @Override
    public String getInsertSql(String connectName, String tableName, JSONObject record) {
        StringBuilder builder = new StringBuilder("INSERT INTO \"");
        builder.append(tableName);
        builder.append("\" (");
        Set<String> rkey = record.keySet();
        List<String> valueList = new ArrayList<>();
        if (whiteList.size() > 0) {
//            ka = whiteList.toArray(new String[JdbcConsumerGroup.whiteList.size()]);
            List<String> list = new ArrayList<>();
            for (String whiteField : whiteList) {
                list.add(whiteField);
                if (record.get(whiteField) != null) {
                    valueList.add("'" + record.get(whiteField) + "'");
                }else {
                    valueList.add("null");
                }
//                valueList.add("'" + record.getOrDefault(whiteField, null) + "'");
            }
            builder.append(StringUtils.join(list, ","));
        } else {
            for (String key : record.keySet()) {
                //在这里可以判断数据类型 用来判断是否在添加字段时加上'' ,可以用来转换boolean类型
                valueList.add("'" + record.getString(key) + "'");
                if (!schemaMap.get(connectName).contains(key.toUpperCase())) {
                    String addFieldSql = getAddFieldSql(tableName, key, record.get(key), "oracle");
                    alterSqlList.add(addFieldSql);
                    schemaMap.get(connectName).add(key.toUpperCase());
                }
            }
            builder.append(StringUtils.join(rkey, ","));
        }
        builder.append(") VALUES(");
        builder.append(StringUtils.join(valueList, ","));
        builder.append(")");
        return builder.toString();
    }

}
