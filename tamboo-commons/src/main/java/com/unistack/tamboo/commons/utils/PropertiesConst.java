package com.unistack.tamboo.commons.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.unistack.tamboo.commons.utils.JedisHelper.PROP_NAME;

/**
 * @author Gyges Zean
 * @date 2018/4/25
 * Supply the read a properties file content method
 */
public class PropertiesConst {

    public static  Logger log = LoggerFactory.getLogger(PropertiesConst.class);

    private static Properties props;

    public PropertiesConst(Properties props) {
        PropertiesConst.props = props;
    }

    public static class PropertiesBuilder implements AutoCloseable {

        private Properties props;

        private FileInputStream i = null;

        public PropertiesBuilder() {
            this.props = new Properties();
        }

        public PropertiesBuilder loadProps(String path, String fileName) {
            try {
                i = new FileInputStream(path + fileName);
                props.load(new InputStreamReader(i, "UTF-8"));
                return this;
            } catch (IOException e) {
                log.error("", e);
            }
            return null;
        }

        public PropertiesConst build() {
            return new PropertiesConst(props);
        }

        @Override
        public void close() throws Exception {
            i.close();
        }
    }


    public static PropertiesBuilder define() {
        return new PropertiesBuilder();
    }

    public String getString(String key) {
        String value = props.getProperty(key.trim());
        checkNotNull(value, "value is empty");
        return value.trim();
    }

    public int getInt(String key) {
        return Integer.valueOf(getString(key));
    }

    public Long getLong(String key) {
        return Long.valueOf(getString(key));
    }

    public Double getDouble(String key) {
        return Double.valueOf(getString(key));
    }

    private String getProperty(String key, String defaultValue) {
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            value = defaultValue;
        }
        return value.trim();
    }


    public static void main(String[] args) {

        String path = System.getProperty("user.dir") + "/config/";
        System.out.println(path);
        PropertiesConst prop = PropertiesConst.define()
                .loadProps(path, PROP_NAME)
                .build();
        System.out.println(prop.getString("mgt.redis.host"));
    }

}
