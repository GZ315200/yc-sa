package com.unistack.tamboo.compute.utils;

import com.unistack.tamboo.commons.utils.enums.EDatabaseType;
import com.unistack.tamboo.compute.utils.spark.CalcConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


/**
 * @author hero.li
 */
public class RedisUtil{
    private static JedisPool jp;
    private EDatabaseType dbModule;

    static{
//        CalcConfig.init();
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(5);
//        config.setMaxIdle(TambooConfig.REDIS_MAX_IDLE);
//        config.setMaxWaitMillis(TambooConfig.REDIS_MAX_WAIT_MILLS);

        //"192.168.1.101"     6379
        jp = new JedisPool(config,CalcConfig.REDIS_HOST,CalcConfig.REDIS_PORT);
//        JedisPoolConfig config = new JedisPoolConfig();
//        config.setMaxTotal(RedisConf.REDIS_MAX_TOTAL);
//        config.setMaxIdle(RedisConf.REDIS_MAX_IDLE);
//        config.setMaxWaitMillis(RedisConf.REDIS_MAX_WAIT_MILLS);
//        jp = new JedisPool(config,"192.168.1.101",6379);
    }

    public RedisUtil(EDatabaseType dbModule){
        this.dbModule = dbModule;
    }

    public Jedis getResource(){
        Jedis jedis = jp.getResource();
        //"unistack@2018"
        jedis.auth(CalcConfig.REDIS_PASSWORD);
        jedis.select(dbModule.getCode());
        return jedis;
    }
}