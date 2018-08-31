package com.unistack.tamboo.sa.dc.flume.util;

import com.unistack.tamboo.commons.utils.errors.GeneralServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

public class PropertiesUtil extends Properties{
    private static  Logger log = LoggerFactory.getLogger(PropertiesUtil.class);
    private static  long serialVersionUID = 1L;

    private List<Object> keyList = new ArrayList<Object>();

    /**
     * 默认构造方法
     */
    public PropertiesUtil() {

    }

    /**
     * 从指定路径加载信息到Properties
     * @param path
     */
    public PropertiesUtil(String path) {
        try {
//            InputStream is = new FileInputStream(path);
            InputStream is=this.getClass().getResourceAsStream(path);
            this.load(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("指定文件不存在！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重写put方法，按照property的存入顺序保存key到keyList，遇到重复的后者将覆盖前者。
     */
    @Override
    public synchronized Object put(Object key, Object value) {
        this.removeKeyIfExists(key);
        keyList.add(key);
        return super.put(key, value);
    }


    /**
     * 重写remove方法，删除属性时清除keyList中对应的key。
     */
    @Override
    public synchronized Object remove(Object key) {
        this.removeKeyIfExists(key);
        return super.remove(key);
    }

    /**
     * keyList中存在指定的key时则将其删除
     */
    private void removeKeyIfExists(Object key) {
        keyList.remove(key);
    }


    /**
     * 获取Properties中所有的值
     * @return
     */
    public Map<String,String> getValues(PropertiesUtil propertiesUtil) {
        Map<String,String> map = new HashMap<String,String>();
        List<Object> keys = propertiesUtil.getKeyList();
        for (Object key : keys) {
            map.put(key.toString(),propertiesUtil.getProperty(key.toString()));
        }
        return map;
    }

    /**
     * 获取Properties中key的有序集合
     * @return
     */
    public List<Object> getKeyList() {
        return keyList;
    }

    /**
     * 保存Properties到指定文件，默认使用UTF-8编码
     * @param path 指定文件路径
     */
    public void store(String path) {
        this.store(path, "UTF-8");
    }

    /**
     * 保存Properties到指定文件，并指定对应存放编码
     * @param path 指定路径
     * @param charset 文件编码
     */
    public void store(String path, String charset) {
        if (path != null && !"".equals(path)) {
            try {
                OutputStream os = new FileOutputStream(new File(path));
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, charset));

                this.store(bw, null);
                bw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("存储路径不能为空!");
        }
    }

    /**
     * 获取dcConfig配置信息
     * @return
     */
    public Properties getBaseConf(){
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
//            inputStream = new FileInputStream("config/collectConf" + File.separator + "dcConfig.properties");
            inputStream = this.getClass().getClassLoader().getResourceAsStream("dcConfig.properties");
            properties.load(inputStream);
        } catch (Exception e) {
            throw new GeneralServiceException("dcConfig.properties 配置文件加载异常!");
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }
}

