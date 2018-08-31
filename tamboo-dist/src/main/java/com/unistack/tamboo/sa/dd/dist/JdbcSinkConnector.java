package com.unistack.tamboo.sa.dd.dist;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.sa.dd.constant.ConverterType;
import com.unistack.tamboo.sa.dd.constant.DdType;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author anning
 * @date 2018/5/22 上午11:27
 */
public class JdbcSinkConnector extends BaseSinkConnector implements KafkaSink {
    private  Logger logger = LoggerFactory.getLogger(JdbcSinkConnector.class);
    private static  String JDBC_CONNECTOR_CLASS = "JdbcSinkConnector";

    @Override
    public JSONObject checkConfig(DdType ddType,JSONObject dconfig) {
//        String connection_url = getJdbcUrl(ddType,dconfig);
        String connection_url = dconfig.getString("jdbc_url");
        String table_name_format = dconfig.getString("table_name");
        int tasks_max = dconfig.getIntValue("tasks_max");
        if (StringUtils.isBlank(dconfig.getString("topics"))) {
            return DdUtil.failResult("topic不能为空！");
        }else {
            if (dconfig.getString("topics").contains(",")){
                return DdUtil.failResult("每次只能指定一个topic且topic中不能含有,字符!");
            }
        }

        String jdbc_username = dconfig.getString("jdbc_username");
        String jdbc_password = dconfig.getString("jdbc_password");
        String driverClassName;
        try {
            driverClassName = ddType.getDriverClass();
//            Class.forName(driverClassName);
            Connection connection = DriverManager.getConnection(connection_url, jdbc_username, jdbc_password);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            String errmsg = "数据库连接失败！" + connection_url;
            logger.info(errmsg);
            return DdUtil.failResult(errmsg);
        } /*catch (ClassNotFoundException e) {
            e.printStackTrace();
            String errmsg = "未找到数据库连接JAR包！" + connection_url;
            logger.info(errmsg);
            return DdUtil.failResult(errmsg);
        }*/

        return DdUtil.succeedResult("");
    }

    @Override
    public JSONObject createConfigJson(DdType ddType,JSONObject dconfig,String connectorName) {
//        String connection_url = getJdbcUrl(ddType,dconfig);
        String connection_url = dconfig.getString("jdbc_url");


        int tasks_max = dconfig.getIntValue("tasks_max");
        if (tasks_max < 1) tasks_max = 1;

        String topics = dconfig.getString("topics");

        String jdbc_username = dconfig.getString("jdbc_username");
        String jdbc_password = dconfig.getString("jdbc_password");

        String security_protocol = dconfig.getString("security_protocol");
        String keystore_localtion = dconfig.getString("keystore_localtion");


        String table_name_format;
        if (StringUtils.isBlank(dconfig.getString("table_name"))) {
            table_name_format = topics.toUpperCase();
        }else {
            table_name_format= dconfig.getString("table_name").toUpperCase()+"_"+topics.toUpperCase();
        }

        String all_connect_url = connection_url + "?useSSL=false";
        JSONObject connectorMeta = new JSONObject(true);
        connectorMeta.put("name", connectorName);

        JSONObject connectorConfig = new JSONObject(true);
        connectorConfig.put("connector.class", JDBC_CONNECTOR_CLASS);
        connectorConfig.put("connection.url", all_connect_url);
        connectorConfig.put("connection.user",jdbc_username);
        connectorConfig.put("connection.password",jdbc_password);
        connectorConfig.put("topics", topics);
        connectorConfig.put("tasks_max", tasks_max);
        connectorConfig.put("table.name.format", table_name_format);

        if (!StringUtils.isBlank(dconfig.getString("whitelist"))) {
            connectorConfig.put("fields.whitelist",dconfig.getString("whitelist"));
        }

        if (!StringUtils.isBlank(dconfig.getString("pk_field"))){
            connectorConfig.put("pk.mode","record_value");
            connectorConfig.put("pk.fields",dconfig.getString("pk_fields"));
            connectorConfig.put("insert.mode","upsert");
        }

        connectorConfig.put("auto.create",true);

        connectorConfig.put("key.converter", ConverterType.STRING.getConverter());
        connectorConfig.put("value.converter",ConverterType.JSON.getConverter());

        connectorConfig.put("key.converter.schemas.enable",true);
        connectorConfig.put("value.converter.schemas.enable",true);

        //加入ssl验证
        /*if (!StringUtils.isBlank(dconfig.getString("security.protocol")))
            connectorConfig.put("security.protocol",dconfig.getString("security.protocol"));

        connectorConfig.put("ssl.truststore.location",dconfig.getString("ssl.truststore.location"));
        connectorConfig.put("ssl.truststore.password",dconfig.getString("ssl.truststore.password"));
        connectorConfig.put("ssl.keystore.location",dconfig.getString("ssl.keystore.location"));
        connectorConfig.put("ssl.keystore.password",dconfig.getString("ssl.keystore.password"));*/

        //加入sasl acl验证
        connectorConfig.put("com.unistack.tamboo.commons.tools.sasl.jaas.config","");         //jass.conf路径
        connectorConfig.put("com.unistack.tamboo.commons.tools.sasl.mechanism","PLAIN");
        connectorConfig.put("security.protocol","SASL_PLAINTEXT");

        connectorMeta.put("config", connectorConfig);

        return connectorMeta;
    }


}
