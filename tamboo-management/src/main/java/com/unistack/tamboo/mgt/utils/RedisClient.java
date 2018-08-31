package com.unistack.tamboo.mgt.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unistack.tamboo.commons.utils.JedisHelper;
import com.unistack.tamboo.commons.utils.PropertiesConst;
import com.unistack.tamboo.commons.utils.errors.DataException;

import java.io.IOException;
import java.util.Map;

/**
 * @author Gyges Zean
 * @date 2018/5/23
 */
public class RedisClient {

    public static  String PROP_NAME = "config/application.properties";

    private static ObjectMapper o = new ObjectMapper();

    private static JedisHelper jedis;

    static {
        String path = System.getProperty("user.dir") + "/";

        PropertiesConst prop = PropertiesConst.define()
                .loadProps(path, PROP_NAME)
                .build();
        String host = prop.getString("spring.redis.host");
        int port = prop.getInt("spring.redis.port");
        String password = prop.getString("spring.redis.password");
        jedis = JedisHelper.defineJedis()
                .host(host)
                .port(port)
                .password(password)
                .build();
        jedis.setDatabase(1);
    }


    public static String set(byte[] key, byte[] bytes, long expireTime, int database) {
        jedis.setDatabase(database);
        return jedis.set(key, bytes, expireTime);
    }

    public static String set(byte[] key, byte[] bytes, int database) {
        jedis.setDatabase(database);
        return jedis.set(key, bytes);
    }


    public static void set(String key, Long value, int database) {
        jedis.setDatabase(database);
        jedis.set(key, value);

    }


    public static String get(String key, int database) {
        jedis.setDatabase(database);
        return jedis.get(key);
    }


    public static byte[] get(byte[] key) {
        return jedis.get(key);
    }


    public static Long getLong(String key, int database) {
        if (isExist(key, database)) {
            jedis.setDatabase(database);
            return Long.valueOf(jedis.get(key));
        }
        return 0L;
    }


    /**
     * get byte[]
     *
     * @param key
     * @return
     */
    public static byte[] get(byte[] key, int database) {
        jedis.setDatabase(database);
        return jedis.get(key);
    }


    /**
     * this key is exist or not
     *
     * @param key
     * @return
     */
    public static boolean isExist(String key, int database) {
        jedis.setDatabase(database);
        return jedis.isExist(key);
    }

    public static Long del(String key, int database) {
        jedis.setDatabase(database);
        return jedis.del(key);
    }

    public static Long del(int database, String... keys) {
        jedis.setDatabase(database);
        return jedis.del(keys);
    }


    /**
     * get map of value by byte[] key.
     *
     * @param key
     * @return
     * @throws IOException
     */
    @SuppressWarnings(value = "unchecked")
    public static Map<String, Object> getMap(byte[] key, int database) {
        jedis.setDatabase(database);
        byte[] value = jedis.get(key);
        try {
            return o.readValue(value, Map.class);
        } catch (IOException e) {
            throw new DataException("No data find out, {}", e);
        }
    }


    public static void main(String[] args) {
        RedisClient.get("1", 2);
    }


}
