package com.unistack.tamboo.mgt.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

/**
 * @author Gyges Zean
 * @date 2018/7/9
 */

@Service
public class MqService {

    private  Logger LOGGER = LoggerFactory.getLogger(MqService.class);

    private  String CHARSET = "UTF-8";

    @Resource
    private
    StringRedisTemplate stringRedisTemplate;


    /**
     * 删除数据
     *
     * @param keyBytes
     */
    public void delValue( byte[] keyBytes) {
        stringRedisTemplate.delete(new String(keyBytes));
    }


    public void delValue( String keyBytes) {
        stringRedisTemplate.delete(keyBytes);
    }


    /**
     * 使用连接池的方式 get
     *
     * @param keyBytes
     * @return
     */
    public byte[] getBytesValue( byte[] keyBytes) {
        return stringRedisTemplate.execute(
                (RedisCallback<byte[]>) connection
                        ->
                        connection.get(keyBytes));
    }


    /**
     * 使用连接池的方式 set
     *
     * @param keyBytes
     * @return
     */
    public byte[] setBytesValue( byte[] keyBytes, byte[] valueBytes, long expireTime) {
        return stringRedisTemplate.execute((RedisCallback<byte[]>) connection -> {
            connection.set(keyBytes, valueBytes);
            if (-1 != expireTime) {
                connection.expire(keyBytes, expireTime);
            }
            return new byte[0];
        });
    }



    /**
     * 是否存在这个key
     *
     * @param key
     * @return
     */
    public boolean isExist( String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 获取字符串类型的key 的 value
     *
     * @param key
     * @return
     */
    public String get( String key) {
        try {
            byte[] bytesValue = getBytesValue(key.getBytes());
            if (null == bytesValue) {
                return null;
            } else {
                return new String(bytesValue, CHARSET);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            LOGGER.error("转码失败 #" + key);
            return null;
        }
    }


    public void set( String key,  String value,  long expireTime) {
        setBytesValue(key.getBytes(), value.getBytes(), expireTime);
    }

}
