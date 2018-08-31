package com.unistack.tamboo.compute.utils;

import com.unistack.tamboo.compute.utils.spark.CalcConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author hero.li
 * 这个类用来快速获取某个连接的基本信息
 */
public class KafkaUtil implements Serializable{
    /**
     * 快速获取连接kafka的最基本配置<br/>
     * 配置项包括kafka集群的IP地址<b>bootstrap.servers<b/>从配置文件中获取<br/>
     * 键值的解码方式<b>key.deserializer<b/>和<b>value.deserializer<b/>,默认使用StringDeserializer<br/>
     * 组id <b>group.id<b/> 从配置文件中取
     * @return
     */
    public static Map<String,Object> getInputKafkaInfo(){
        Map<String, Object> kafkaParam = getInput();
        kafkaParam.put("key.deserializer", KafkaAvroDeserializer.class.getName());
        kafkaParam.put("value.deserializer",KafkaAvroDeserializer.class.getName());
        kafkaParam.put("auto.offset.reset","earliest");
        kafkaParam.put("group.id",String.valueOf(System.currentTimeMillis()));
        kafkaParam.put("schema.registry.url",CalcConfig.KAFKA_REGISTRY_URL);
        return kafkaParam;
    }

    /**
     * 获取输出源，即数据输出到哪台服务器的最基本配置信息<br/>
     * props.put(SaslConfigs.SASL_JAAS_CONFIG, ConfigHelper.jaasConfigProperty("PLAIN","Tu_adhpoc5","Tp_nbrvwwp").value());
     * @return
     */
    public Properties getOutputInfo(){
        Properties props = getOutput();
        props.put("key.serializer", KafkaAvroSerializer.class.getName());
        props.put("value.serializer",KafkaAvroSerializer.class.getName());
        props.put("schema.registry.url",CalcConfig.KAFKA_REGISTRY_URL);
        return props;
    }

    public static Properties getKafkaAvroProducerProperties(){
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, CalcConfig.KAFKA_BROKER_LIST);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class.getName());
        props.put("schema.registry.url", CalcConfig.KAFKA_REGISTRY_URL);
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");
        return props;
    }


    /**
     * 获取读取kafka信息的最基本的配置
     * @return
     */
    private static Map<String,Object> getInput(){
        Map<String,Object> kafkaParam = new HashMap<>(16,1);
        kafkaParam.put("bootstrap.servers", CalcConfig.KAFKA_BROKER_LIST);
        kafkaParam.put("auto.offset.reset","earliest");
        kafkaParam.put("security.protocol", "SASL_PLAINTEXT");
        kafkaParam.put("sasl.mechanism", "PLAIN");
        return kafkaParam;
    }

    /**
     *  获取生产数据的kafka信息的最基本的配置
     * @return
     */
    private Properties getOutput(){
        Properties props = new Properties();
        props.put("bootstrap.servers", CalcConfig.KAFKA_BROKER_LIST);
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism", "PLAIN");
        return props;
    }
}