package com.unistack.tamboo.mgt.utils;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Gyges Zean
 * @date 2018/5/17
 */
public class CacheUtils {

    private static Logger logger = LoggerFactory.getLogger(CacheUtils.class);


    private static  String SYS_CACHE = "sysCache";


    private static LoadingCache<String, Object> cacheM = CacheBuilder.newBuilder()
            .maximumSize(10000) //最大存储大小
            .initialCapacity(1000) //初始容量
            .expireAfterAccess(12, TimeUnit.HOURS) //过期时间12小时
            .build(new CacheLoader<String, Object>() {
                //                调用时如果key没有对应值调用此方法
                @Override
                public String load(String key) throws Exception {
                    return "null";
                }
            });

    /**
     * key - Object value
     *
     * @param key
     * @param value
     */
    public static void put(String key, Object value) {
        cacheM.put(key, value);
    }


    /**
     * 获得value
     *
     * @param key
     * @return
     */
    public static Object getValue(String key) {
        Object value = null;
        try {
            value = cacheM.get(key);
            if ("null".equals(value)) {
                return null;
            }
            return value;
        } catch (ExecutionException e) {
            logger.error("localCache get Error", e);
            e.printStackTrace();
        }
        return null;
    }


}
