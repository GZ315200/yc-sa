package com.unistack.tamboo.sa.dd.jdbc;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.SinkWorker;
import com.unistack.tamboo.sa.dd.constant.DdType;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import com.unistack.tamboo.sa.dd.util.JdbcUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static com.unistack.tamboo.sa.dd.jdbc.JdbcConsumerGroup.pkField;
import static com.unistack.tamboo.sa.dd.jdbc.JdbcConsumerGroup.schemaMap;
import static com.unistack.tamboo.sa.dd.jdbc.JdbcConsumerGroup.whiteList;

/**
 * @author anning
 * @date 2018/6/13 下午8:45
 * @description: jdbc sink worker
 */
public interface JdbcSinkWorker extends SinkWorker {

    default JSONObject checkConfig(JSONObject args) {
        String type = args.getString("type");
        DdType ddType = DdType.getDdTypeByName(type.toLowerCase());
        if (Objects.isNull(ddType)) {
            return DdUtil.failResult("未找到对应的type:" + type);
        }
        JSONObject json = args.getJSONObject("fields");
//        String table_name = json.getString("table_name");
        int tasks_max = 1;
                /*json.getInteger("tasks_max");
        if (tasks_max > 20) {
            return DdUtil.failResult("tasks_max不能大于20!");
        }*/
        String topic = args.getString("topic_name");
        String whitelistString = json.getString("whitelist");
        String pk_field = json.getString("pk_field");

        if (StringUtils.isNotBlank(pk_field) && pk_field.contains(",")) {
            return DdUtil.failResult("无法将指定字段设置为主键：" + pk_field.trim());
        }

        if (StringUtils.isNotBlank(whitelistString) && StringUtils.isNotBlank(pk_field)) {
            String[] wl = StringUtils.split(whitelistString, ",");
            if (!ArrayUtils.contains(wl, pk_field.trim())) {
                return DdUtil.failResult("字段白名单:" + whitelistString + " 中未包含主键:" + pk_field);
            }
        }


        if (StringUtils.isBlank(topic)) {
            return DdUtil.failResult("topic不能为空！");
        }
        try {
            JSONObject getResult = JdbcUtils.getConnection(ddType, json);
            if (getResult.getBoolean("isSucceed")) {
                Connection connection = (Connection) getResult.get("msg");
                connection.close();
            } else {
                return getResult;
            }
        } catch (SQLException e) {
            return DdUtil.failResult("数据库建立连接失败 ======>" + e.toString());
        }
        return DdUtil.succeedResult("");
    }

    /**
     * 获得表结构
     *
     * @param connectName 唯一标识
     * @param connection  连接
     * @param tableName   表名
     * @param record      kafka record
     * @return
     */
    boolean getOrCreateTable(String connectName, Connection connection, String tableName, JSONObject record);

    /**
     * 插入数据
     *
     * @param connection 数据库连接
     * @param args       参数
     * @param list       record list
     * @return
     */
    JSONObject insertInto(String connectName, Connection connection, JSONObject args, List<JSONObject> list);


    /**
     * 构建建表语句
     *
     * @param tableName 表名
     * @param record    kafka record
     * @return CREATE SQL
     */
    default String getCreateSql(String tableName, JSONObject record, String dataBaseType) {
        StringBuilder builder = new StringBuilder("CREATE TABLE ");
        if (dataBaseType.equalsIgnoreCase("mysql")) {
            builder.append("IF NOT EXISTS ");
        }
        if (dataBaseType.equalsIgnoreCase("oracle")) {
            builder.append("\"");
        }
        builder.append(tableName);
        if (dataBaseType.equalsIgnoreCase("oracle")) {
            builder.append("\" ");
        }
        builder.append("(");
        List<String> fieldListMap = new ArrayList<>();
        String typeMap;
        for (String field : record.keySet()) {
            typeMap = field + " " + DdUtil.getSqlType(record.get(field).getClass().getSimpleName(), dataBaseType);
            fieldListMap.add(typeMap);
        }

        builder.append(StringUtils.join(fieldListMap, ","));
        if (StringUtils.isNotBlank(pkField)) {
            builder.append(", PRIMARY KEY ( ").append(pkField).append(" )");
        }
        builder.append(")");
        return builder.toString();
    }

    /**
     * 构建增加字段语句
     *
     * @param tableName 表名
     * @param fieldName 字段名
     * @param value     字段值，用来获得字段类型
     * @return ALTER SQL
     */
    default String getAddFieldSql(String tableName, String fieldName, Object value, String dataBaseType) {
        StringBuilder builder = new StringBuilder("ALTER TABLE ");
        if (dataBaseType.equalsIgnoreCase("oracle")) {
            builder.append("\"");
            builder.append(tableName);
            builder.append("\"");
        } else {
            builder.append(tableName);
        }
        builder.append(" ADD ");
        builder.append(fieldName);
        builder.append(" ");
        builder.append(DdUtil.getSqlType(value.getClass().getSimpleName(), dataBaseType));
        return builder.toString();
    }

    /**
     * 实现各中数据库的uosert功能
     *
     * @param connectName 唯一标识
     * @param tableName   表名
     * @param record      kafka record
     * @return
     */
    default String getUpsertSql(String connectName, String tableName, JSONObject record, String dataBaseType, Set<String> alterSqlList,
                                ArrayList<String> valueAsKey, ArrayList<String> setValue, ArrayList<String> sField,
                                ArrayList<String> incomingField) {
        StringBuilder builder = new StringBuilder();
        builder.append("merge into \"");
        builder.append(tableName);
        builder.append("\" t using (select ");
        if (whiteList.size() > 0) {
            StringBuilder sbd = new StringBuilder();
            /*for (String key : record.keySet()) {
                if (whiteList.contains(key)) {
                    valueAsKey.add("'" + record.get(key) + "' as " + key);
                    if (!key.equalsIgnoreCase(pkField)) {
                        setValue.add(sbd.append("t.").append(key).append("=incoming.").append(key).toString());
                        sbd.setLength(0);
                    }
                    if (dataBaseType.equalsIgnoreCase("oracle"))
                        sField.add("t." + key);
                    else sField.add(key);
                    incomingField.add("incoming." + key);
                }
            }*/
            for (String wField : whiteList) {
                if (Objects.isNull(record.get(wField))){
                    valueAsKey.add("null as "+wField);
                }else {
                    valueAsKey.add("'" + record.get(wField) + "' as " + wField);
                }
                if (!wField.equalsIgnoreCase(pkField)) {
                    setValue.add(sbd.append("t.").append(wField).append("=incoming.").append(wField).toString());
                    sbd.setLength(0);
                }
                if (dataBaseType.equalsIgnoreCase("oracle"))
                    sField.add("t." + wField);
                else sField.add(wField);
                incomingField.add("incoming." + wField);
            }


        } else {
            //添加没有whiteList的情况
            StringBuilder sbd = new StringBuilder();
            for (String key : record.keySet()) {
                valueAsKey.add("'" + record.getString(key) + "' as " + key);

                if (!key.equalsIgnoreCase(pkField)) {
                    setValue.add(sbd.append("t.").append(key).append("=incoming.").append(key).toString());
                    sbd.setLength(0);
                }
                if (dataBaseType.equalsIgnoreCase("oracle"))
                    sField.add("t." + key);
                else sField.add(key);
                incomingField.add("incoming." + key);
                if (dataBaseType.equalsIgnoreCase("oracle")) {
                    if (!schemaMap.get(connectName).contains(key.toUpperCase())) {
                        String addFieldSql = getAddFieldSql(tableName, key, record.get(key), dataBaseType);
                        alterSqlList.add(addFieldSql);
                        schemaMap.get(connectName).add(key.toUpperCase());
                    }
                } else if (dataBaseType.equalsIgnoreCase("sqlserver")) {
                    if (!schemaMap.get(connectName).contains(key)) {
                        String addFieldSql = getAddFieldSql(tableName, key, record.get(key), dataBaseType);
                        alterSqlList.add(addFieldSql);
                        schemaMap.get(connectName).add(key);
                    }
                }
            }

        }

        builder.append(StringUtils.join(valueAsKey, ","));
        if (dataBaseType.equalsIgnoreCase("sqlserver"))
            builder.append(") incoming on(");
        else if (dataBaseType.equalsIgnoreCase("oracle"))
            builder.append(" FROM dual) incoming on(");
        builder.append("t.").append(pkField).append("=incoming.").append(pkField).append(")");
        builder.append("when matched then update set ");
        builder.append(StringUtils.join(setValue, ","));
        builder.append(" when not matched then insert(");
        builder.append(StringUtils.join(sField, ","));
        builder.append(") values(");
        builder.append(StringUtils.join(incomingField, ","));
        builder.append(")");
        if (dataBaseType.equalsIgnoreCase("sqlserver")) {
            builder.append(";");
        }

        valueAsKey.clear();
        setValue.clear();
        sField.clear();
        incomingField.clear();

        return builder.toString();
    }

    /**
     * @param connectName 唯一标识
     * @param tableName   表名
     * @param record      kafka record
     * @return
     */
    String getInsertSql(String connectName, String tableName, JSONObject record);

}
