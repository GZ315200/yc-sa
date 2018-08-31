package com.unistack.tamboo.compute.utils.spark;


//import com.unistack.tamboo.compute.MQConfig;
//import com.unistack.tamboo.compute.utils.KafkaConf;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.Producer;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import java.com.unistack.tamboo.commons.tools.util.Optional;
//import java.com.unistack.tamboo.commons.tools.util.Properties;
//import java.com.unistack.tamboo.commons.tools.util.concurrent.Future;


public class MesgSend{
//    private Optional<Producer> producer = Optional.empty();
//
//    public MesgSend(){}
//
//    /**
//     * 初始化发送对象,让其在一个MesgSend对象上只有一个
//     */
//    private void initProducer(){
//        if(!producer.isPresent()){
//            synchronized(this){
//                if(!producer.isPresent())
//                    producer = getProducer();
//            }
//        }
//    }
//
//
//    public Future<?> sendMessage(String value){
//        return sendMessage("",value);
//    }
//
//
//    public Future<?> sendMessage(String key,String value){
//        initProducer();
//        Future f = producer.get().send(new ProducerRecord(KafkaConf.TO_TOPIC,key,value));
//        return f;
//    }
//
//
//    private Optional getProducer(){
//        Properties toInfo = new Properties();
//        toInfo.put("key.serializer",MQConfig.to_key_serializer);
//        toInfo.put("value.serializer",MQConfig.to_value_serializer);
//        toInfo.put("bootstrap.servers",KafkaConf.BROKER_LIST);
//        Producer p = new KafkaProducer(toInfo);
//        return Optional.of(p);
//    }


}