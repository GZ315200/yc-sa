package com.unistack.tamboo.sa.dd.invoking;

import com.unistack.tamboo.sa.dd.ActiveMq.ActiveMqConsumerGroup;
import com.unistack.tamboo.sa.dd.ConsumerGroup;
import com.unistack.tamboo.sa.dd.comsumer.ConsumerWorker;
import com.unistack.tamboo.sa.dd.file.FileConsumerGroup;
import com.unistack.tamboo.sa.dd.jdbc.JdbcConsumerGroup;
import com.unistack.tamboo.sa.dd.jdbc.JdbcSinkWorker;
import com.unistack.tamboo.sa.dd.SinkWorker;
import com.unistack.tamboo.sa.dd.constant.DdType;
import com.unistack.tamboo.sa.dd.constant.WorkerType;
import com.unistack.tamboo.sa.dd.dist.KafkaSink;
import com.unistack.tamboo.sa.dd.kafka.KafkaConsumerGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * @author anning
 * @date 2018/5/22 下午12:19
 * @description: invoking class
 */
public class DdInvoking {
    private static  Logger logger = LoggerFactory.getLogger(DdInvoking.class);

    private  static Map<String,Supplier<ConsumerGroup>> map = new HashMap<>();
    static {
        map.put("mysql", JdbcConsumerGroup::new);
        map.put("oracle",JdbcConsumerGroup::new);
        map.put("sqlserver",JdbcConsumerGroup::new);
        map.put("postgre",JdbcConsumerGroup::new);
        map.put("file", FileConsumerGroup::new);
        map.put("kafka", KafkaConsumerGroup::new);
        map.put("activemq", ActiveMqConsumerGroup::new);
    }


    public static KafkaSink getConnetorInstance(String typeName) {
        String kinds= Objects.requireNonNull(DdType.getDdTypeByName(typeName)).getKinds();
        KafkaSink o = null;
        try {
            Class<?> connectorClass = Class.forName(kinds);
            o = (KafkaSink)connectorClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return o;
    }

    public static ConsumerWorker getWorker(String name){
        String clazz = Objects.requireNonNull(WorkerType.getClazzByName(name)).getClazz();
        ConsumerWorker consumerWorker =null;
        try {
            Class<?> aClass = Class.forName(clazz);
            consumerWorker = (ConsumerWorker) aClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return consumerWorker;
    }

    public static SinkWorker getWorkerByName(String name){
//        String clazz = Objects.requireNonNull(WorkerType.getClazzByName(name)).getClazz();
        SinkWorker jdbcWorker =null;
        String clazz ;
        WorkerType clazzByName = WorkerType.getClazzByName(name.toLowerCase());
        if (clazzByName==null){
            return null;
        }else {
            clazz = clazzByName.getClazz();
        }

        try {
            Class<?> aClass = Class.forName(clazz);
            jdbcWorker = (SinkWorker) aClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return jdbcWorker;
    }

    public static JdbcSinkWorker getJdbcSinkWorkerByname(String name){
        String clazz = Objects.requireNonNull(WorkerType.getClazzByName(name)).getClazz();
        JdbcSinkWorker jdbcSinkWorker = null;
        try {
            Class<?> aClass = Class.forName(clazz);
            jdbcSinkWorker = (JdbcSinkWorker) aClass.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return jdbcSinkWorker;
    }


    public static ConsumerGroup getConsumerGroupByName(String name){
        Supplier<ConsumerGroup> consumerGroupSupplier = map.get(name);
        if (consumerGroupSupplier!=null) return consumerGroupSupplier.get();
        throw new IllegalArgumentException("no such ConsumerGroup for "+name);
//        return consumerGroup;
    }
}
