package com.unistack.tamboo.mgt.utils;


import com.unistack.tamboo.commons.utils.PropertiesConst;

/**
 * @author Gyges Zean
 * @date 2018/6/4
 */
public class PropertiesUtil {

    private static  String PROP_NAME = "config/application.properties";

    private static PropertiesConst prop;

    static {
        String path = System.getProperty("user.dir") + "/";
        prop = PropertiesConst.define()
                .loadProps(path, PROP_NAME)
                .build();
    }


    public static String get(String key) {
        return prop.getString(key);
    }

    public static int getInt(String key) {
        return Integer.parseInt(prop.getString(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(prop.getString(key));
    }

    public static long getLong(String key) {
        return Long.parseLong(prop.getString(key));
    }

    public static long offsetCollectInterval() {
        return Long.parseLong(prop.getString("monitor.offset.interval"));
    }

    public static long flumeCollectInterval() {
        return Long.parseLong(prop.getString("monitor.source.data.interval"));
    }

    public static long sparkCollectInterval() {
        return Long.parseLong(prop.getString("monitor.streaming.data.interval"));
    }

    public static long connectCollectInterval() {
        return Long.parseLong(prop.getString("monitor.sink.data.interval"));
    }


}
