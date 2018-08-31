package com.unistack.tamboo.commons.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unistack.tamboo.commons.utils.enums.EDatabaseType;
import com.unistack.tamboo.commons.utils.errors.DataException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.Pool;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

/**
 * @author Gyges Zean
 * @date 2018/5/22
 * 使用jedisHelper 进行对redis 的访问
 */
public class JedisHelper implements AutoCloseable {

    private ObjectMapper objectMapper = new ObjectMapper();

    private static  int DEFAULT_TIMEOUT = 30000;

    private static  int DEFAULT_PORT = 6379;

    private static  int DEFAULT_DATABASE = 0;

    private static  String EX = "EX";

    public static  String NX = "NX";

    public static  String PROP_NAME = "application.properties";

    private int database;

    private JedisPool jedisPool;

    public JedisHelper(String host, int port, String password, int timeout) {
        JedisPoolConfig poolConfig = buildPoolConfig();
        jedisPool = new JedisPool(poolConfig, host, port == 0 ? DEFAULT_PORT : port,
                timeout == 0 ? DEFAULT_TIMEOUT : timeout, password, database == 0 ? DEFAULT_DATABASE : database);
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    @Override
    public void close() throws Exception {
        jedisPool.close();
    }


    public static class JedisBuilder {

        private String host;

        private int port;

        private String password = "";

        private int timeout = 0;

        public JedisBuilder() {
        }

        public JedisBuilder host(String host) {
            if (Objects.isNull(host)) {
                throw new DataException("Host should be specified.");
            }
            this.host = host;
            return this;
        }

        public JedisBuilder port(int port) {
            this.port = port;
            return this;
        }

        public JedisBuilder password(String password) {
            this.password = password;
            return this;
        }

        public JedisBuilder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public JedisHelper build() {
            return new JedisHelper(host, port, password, timeout);
        }

    }


    public static JedisBuilder defineJedis() {
        return new JedisBuilder();
    }


    private JedisPoolConfig buildPoolConfig() {
         JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(100000);
        poolConfig.setMaxIdle(500);
        poolConfig.setMinIdle(50);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(10).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(10).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }


    /**
     * @param key
     * @param bytes
     * @return
     */
    public String set(byte[] key, byte[] bytes) {
        return jedisPool.getResource().set(key, bytes);
    }


    public String set(byte[] key, byte[] bytes, long expireTime) {
        return jedisPool.getResource().set(key, bytes, NX.getBytes(), EX.getBytes(), expireTime);
    }


    /**
     * set String value with expireTime return string
     *
     * @param key
     * @param value
     * @return
     */
    public String setWithExp(String key, String value, long expireTime) {
        return jedisPool.getResource().set(key, value, NX, EX, expireTime);
    }

    public void set(String k, Long v) {
        jedisPool.getResource().set(k, String.valueOf(v));
    }

    /**
     * get byte[]
     *
     * @param key
     * @return
     */
    public byte[] get(byte[] key) {
        return jedisPool.getResource().get(key);
    }

    /**
     * get String value from from String key.
     *
     * @param key
     * @return
     */
    public String get(String key) {
        return jedisPool.getResource().get(key);
    }


    public Long del(String key) {
        return jedisPool.getResource().del(key);
    }

    public Long del(String... keys) {
        return jedisPool.getResource().del(keys);
    }

    /**
     * get map of value by byte[] key.
     *
     * @param key
     * @return
     * @throws IOException
     */
    @SuppressWarnings(value = "unchecked")
    public Map<String, Object> getMap(byte[] key) throws IOException {
        byte[] value = jedisPool.getResource().get(key);
        return objectMapper.readValue(value, Map.class);
    }

    public boolean exists(String key) {
        return jedisPool.getResource().exists(key);
    }

    /**
     * this key is exist or not
     *
     * @param key
     * @return
     */
    public boolean isExist(String key) {
        return jedisPool.getResource().exists(key);
    }

}
