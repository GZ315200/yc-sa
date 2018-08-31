package com.unistack.tamboo.sa;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.OkHttpResponse;
import com.unistack.tamboo.commons.utils.OkHttpUtils;
import com.unistack.tamboo.commons.utils.TambooConfig;
import com.unistack.tamboo.sa.dd.util.DdUtil;
import com.unistack.tamboo.sa.dd2.constant.Constants;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;

import static com.unistack.tamboo.sa.dd2.constant.Constants.HTTP_PRE;
import static com.unistack.tamboo.sa.dd2.constant.Constants.IS_SUCCEED;

/**
 * @author anning
 * @date 2018/7/20 下午5:44
 * @description: sink utils
 */
public class SinkUtils {
    private static  Logger logger = LoggerFactory.getLogger(SinkUtils.class);

    /**
     * 检查jdbc sink 参数是否有效
     * @param conf 传入参数
     * @return json
     */
    public static JSONObject checkConfig(JSONObject conf) {
        String topic = conf.getString("topic_name");
        String type = conf.getString("type");
        JSONObject fields = conf.getJSONObject("fields");
        String jdbcUrl = fields.getString("jdbc_url");
        String username = fields.getString("user_name");
        String password = fields.getString("user_pwd");
//        int tasks_max = 1;
        String whitelistString = fields.getString("whiteList");
        String pk_field = fields.getString("pk_field");
        if (StringUtils.isNotBlank(pk_field) && pk_field.contains(",")) {
            return DdUtil.failResult("无法将指定字段设置为主键：" + pk_field.trim());
        } else {
            if (StringUtils.isNotBlank(whitelistString) && StringUtils.isNotBlank(pk_field)) {
                String[] wl = StringUtils.split(whitelistString, ",");
                if (!ArrayUtils.contains(wl, pk_field.trim())) {
                    return DdUtil.failResult("字段白名单:" + whitelistString + " 中未包含主键:" + pk_field);
                }
            }

            return StringUtils.isBlank(topic) ? DdUtil.failResult("topic不能为空！") : getConnection(type, jdbcUrl, username, password);
        }
    }

    /**
     * 判断数据库能否连接
     * @param dataBaseType  数据库类型
     * @param jdbcURL   jdbc url
     * @param userName  用户名
     * @param passwd    密码
     * @return  json
     */
    public static JSONObject getConnection(String dataBaseType, String jdbcURL, String userName, String passwd) {
        Connection connection = null;

        JSONObject result;
        try {
            Properties properties = new Properties();
            properties.setProperty("user", userName);
            properties.setProperty("password", passwd);
            properties.setProperty("useSSL", "false");
            if (!"mysql".equalsIgnoreCase(dataBaseType)) {
                Class.forName(Constants.DriverClass.getDriverClassByDatabaseTyoe(dataBaseType));
            }

            connection = DriverManager.getConnection(jdbcURL, properties);
            result = DdUtil.succeedResult("数据库连接成功！");
            return result;
        } catch (SQLException var18) {
            logger.error("数据库连接失败 ========>" + var18.toString());
            result = DdUtil.failResult("连接数据库失败");
        } catch (ClassNotFoundException var19) {
            logger.error("未找到" + dataBaseType + "对应加载类 ========>" + var19.toString());
            result = DdUtil.failResult("未找到" + dataBaseType + "对应加载类！");
            return result;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException var17) {
                logger.error("连接数据库失败 ========>" + var17.toString());
                return DdUtil.failResult("连接数据库失败！");
            }
        }
        return result;
    }

    /**
     * 检查所有配置的connectUrl
     * @return json
     */
    public static JSONObject checkAllConnectUrl(){
        String[] kafkaConnectorUrls = StringUtils.split(TambooConfig.KAFKA_CONNECTOR_URL, ",");
        for (int i = 0; i < kafkaConnectorUrls.length; i++) {
            JSONObject connectCheck = SinkUtils.checkConnectUrl(kafkaConnectorUrls[i]);
            if (!connectCheck.getBoolean(IS_SUCCEED)){
                return connectCheck;
            }
        }
        return DdUtil.succeedResult("OK");
    }


    /**
     * 检查connector url 是否正常连接
     * @param connectUrl url
     * @return json
     */
    public static JSONObject checkConnectUrl(String connectUrl){
        String okUrl = makeHttpPre(connectUrl);
        OkHttpResponse response = OkHttpUtils.get(okUrl + "/connectors");
        int code = response.getCode();
        if (HttpStatus.SC_OK!=code){
            return DdUtil.failResult("连接不上connect_url:"+okUrl);
        }
        return DdUtil.succeedResult("OK");
    }

    /**
     * 检查registry url 是否正常连接
     * @return json
     */
    public static JSONObject checkRegistryUrl(){
        String kafkaRegistryUrl = makeHttpPre(TambooConfig.KAFKA_REGISTRY_URL);
        OkHttpResponse response = OkHttpUtils.get(kafkaRegistryUrl + "/subjects");
        int code = response.getCode();
        if (HttpStatus.SC_OK!=code){
            return DdUtil.failResult("连接不上registry_url:"+ kafkaRegistryUrl);
        }
        return DdUtil.succeedResult("OK");
    }

    /**
     * 获得第一个(随意一个)connectUrl
     * @return
     */
    public static String getFirstConnectUrl(){
        String[] kafkaConnectorUrls = StringUtils.split(TambooConfig.KAFKA_CONNECTOR_URL, ",");
        int i = new Random().nextInt(kafkaConnectorUrls.length);
        return makeHttpPre(kafkaConnectorUrls[i]);
    }

    /**
     * 判断url是否是以http开头,如果不是则加上http://
     * @param url
     * @return
     */
    public static String makeHttpPre(String url){
        if (!url.startsWith(HTTP_PRE)){
            url = HTTP_PRE+url;
        }
        return url;
    }
}
