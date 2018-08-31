package com.unistack.tamboo.compute.utils.spark;

import com.alibaba.fastjson.JSONObject;
import javassist.*;
import org.apache.log4j.Logger;
import java.lang.reflect.Field;
import java.util.Iterator;


/**
 * @author hero.li
 * 动态生成对象的工具类
 */
public class DynamicClassUtil {
    private static Logger LOGGER = Logger.getLogger(DynamicClassUtil.class);

//    public static void main(String[] args) throws NotFoundException, CannotCompileException, IllegalAccessException,InstantiationException{
//        JSONObject fieldsJson = new JSONObject();
//        fieldsJson.put("name","xiao ming");
//        fieldsJson.put("age",27);
//
//        // 创建一个名称为Student的类
//        Object o = DynamicClassUtil.createObject("Student",fieldsJson);
//        // 创建Student类的对象
//        Object s1 = o.getClass().newInstance();
//
//        Iterator<String> itr = fieldsJson.keySet().iterator();
//        while(itr.hasNext()){
//            String fieldName = itr.next();
//            Object fieldValue = fieldsJson.get(fieldName);
//            DynamicClassUtil.setFieldValue(s1,fieldName,fieldValue);
//        }
//
//        Field[] fields = s1.getClass().getDeclaredFields();
//        for(Field f : fields){
//            System.out.println(f.getName()  +"   "+f.get(s1));
//        }
//    }

    /**
     * 为对象动态增加属性，并同时为属性赋值
     * @param className
     *            需要创建的java类的名称
     * @param fieldJson
     *            字段-字段值的属性map，需要添加的属性
     * @return
     * @throws NotFoundException
     * @throws CannotCompileException
     *
     */
    public static Object createObject(String className, JSONObject fieldJson) throws NotFoundException, CannotCompileException, IllegalAccessException,InstantiationException {
        // 获取javassist类池
        ClassPool pool = ClassPool.getDefault();
        // 创建javassist类
        CtClass ctClass = pool.makeClass(className,pool.get(DynamicClassUtil.class.getName()));

        //1、构建类的字节码
        Iterator<String> itr = fieldJson.keySet().iterator();
        while(itr.hasNext()){
            String fieldName = itr.next();
            Object fieldValue = fieldJson.get(fieldName);
            String fieldType = fieldValue.getClass().getName();

            CtField ctField = new CtField(pool.get(fieldType),fieldName,ctClass);
            ctField.setModifiers(Modifier.PUBLIC);
            ctClass.addField(ctField);
        }

        //2、创建类的对象
        Class c = ctClass.toClass();
        Object targetObject = c.newInstance();

        //3、为对象的属性赋值
        while(itr.hasNext()){
            String fieldName = itr.next();
            Object fieldValue = fieldJson.get(fieldName);
            setFieldValue(targetObject, fieldName, fieldValue);
        }
        return targetObject;
    }


    /**
     * 给对象属性赋值
     * @param dObject
     * @param fieldName
     * @param val
     * @return
     */
    public static void setFieldValue(Object dObject, String fieldName, Object val){
        try{
            //获取对象的属性域
            Field fu = dObject.getClass().getDeclaredField(fieldName);
            fu.setAccessible(true);
            fu.set(dObject,val);
        }catch(Exception e){
            LOGGER.error("SPARK:赋值失败",e);
            e.printStackTrace();
        }
    }
}