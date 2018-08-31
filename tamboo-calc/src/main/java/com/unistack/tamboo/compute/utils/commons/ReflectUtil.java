package com.unistack.tamboo.compute.utils.commons;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Timestamp;
import java.util.Optional;

public class ReflectUtil {
    private static Logger LOGGER = Logger.getLogger(ReflectUtil.class);
    private static Method addURL = null ;
    static{
        try{
            addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class }) ;
            addURL.setAccessible(true);
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.error("反射工具类初始化失败!",e);
        }
    }


    /**
     *
     * 动态加载Jar包中的类到内存中并获取对象
     *
     * @param jarPath
     *    jar文件所在路径   eg:  /Users/frank/Desktop/shell/mytest2/v4.jar
     * @param className   v4.jar 中的类路径 eg:com.lyzx.model.M1
     * @return
     *
     *  Optional<Object> op
     *  如果op为空则表明获取类失败
     */
    public static Optional<Object> load(String jarPath, String className){
        try{
            File file = new File(jarPath);
            addURL.invoke(ClassLoader.getSystemClassLoader(), new Object[] { file.toURI().toURL() });
            Object target = Class.forName(className, false, ClassLoader.getSystemClassLoader()).newInstance();
            return Optional.ofNullable(target);
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.error("动态加载类失败!",e);
            return Optional.empty();
        }
    }


//    public static void main(String[] args) throws Exception {
//        Optional<Object> o = load("D:\\workproject\\tamboo-sa\\tamboo-management\\src\\main\\java\\com\\unistack\\tamboo\\mgt\\config\\tamboo-calc-1.0.0.jar", "com.unistack.tamboo.compute.process.until.dataclean.filter.test.LocateFilter");
//        if(o.isPresent()){
//            Filter filter = (Filter)o.get();
//            filter.init(JSON.parseObject("{\"type\":\"Locate\",\"filedCount\":1 ,\"fields\":[{\"sourceField\":\"输入的值#String\",\"findField\":\"查找出现的值#String\"}]}"));
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("sourceField","helo");
//            jsonObject.put("findField","l");
//            filter.filter(jsonObject);
//            System.out.println(jsonObject);
//            System.out.println(new Timestamp(System.currentTimeMillis()));
//        }else{
//            System.out.println("======");
//        }
//    }

}