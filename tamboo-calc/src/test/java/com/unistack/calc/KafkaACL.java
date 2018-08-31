package com.unistack.calc;


import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.ConfigHelper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.SaslConfigs;
import org.junit.Test;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class KafkaACL {
    private static  String ips = "192.168.0.193:9093,192.168.0.194:9093,192.168.0.195:9093";
//    private static  String ips = "192.168.1.194:9092";

    @Test
    public void testStringFormat(){
        Properties props = new Properties();
        props.put("bootstrap.servers",ips);
        props.put("group.id",String.valueOf(System.currentTimeMillis()));
        props.put("auto.offset.reset","earliest");
        props.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        props.put("security.protocol","SASL_PLAINTEXT");
        props.put("sasl.mechanism","PLAIN");
        props.put("sasl.mechanism", "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG, ConfigHelper.jaasConfigProperty("PLAIN","Tu_adhpoc5","Tp_nbrvwwp").value());

        KafkaConsumer<String,String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("source_test_zean_m_offset_d"));

        while(true){
            System.out.println("====>>>>");
            ConsumerRecords<String,String> records = consumer.poll(1000);
            for (ConsumerRecord<String,String> record : records){
                String value = record.value();
                if(value != null){
                    System.out.println(record.offset()+"====="+value);
                }
            }
        }
    }


    @Test
    public void testWriteStringFormat() throws ExecutionException, InterruptedException {
        System.setProperty("java.security.auth.login.config","/Users/frank/Desktop/shell/lyh.conf");
        Properties props = new Properties();

        props.put("bootstrap.servers",ips);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("security.protocol", "SASL_PLAINTEXT");
        props.put("sasl.mechanism","PLAIN");

        Producer<String, String> producer = new KafkaProducer<>(props);
        JSONObject o = new JSONObject();
//        String[] names = {"戴沐白","奥斯卡","唐三","马红骏","小舞","宁荣荣","朱竹清"};
//        String[] weapons = {"白虎","香肠","蓝银皇","凤凰","柔骨兔","七宝琉璃塔","幽冥灵猫"};
//        String[] degrees = {"67强攻系战魂帝","61级食物系魂帝","66级控制系战魂帝","59级强攻系战魂帝","57级敏攻系战魂王","62级辅助系魂帝","62级敏攻系战魂帝"};

        //yh1 =>  student
        //yh3 =>  school
        for(int i =0; i<1000; i++){
            o.put("yh5_id",String.valueOf(i+1));
            o.put("yh5_age",String.valueOf(i+1));
            o.put("yh5_name",String.valueOf(i+1));
            Future<RecordMetadata> f = producer.send(new ProducerRecord<>("yh5", String.valueOf(System.currentTimeMillis()), o.toString()));
            System.out.println("==>"+f.get()+"             "+o);
        }
        producer.close();
    }


//    @Test
//    public void test3(){
//        String s = "";
//        JSONObject joinResultJsonSchema = JSONObject.parseObject(s);
//        Properties props = new Properties();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, CalcConfig.KAFKA_BROKER_LIST);
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,io.confluent.kafka.serializers.KafkaAvroSerializer.class.getName());
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,io.confluent.kafka.serializers.KafkaAvroSerializer.class.getName());
//        props.put("schema.registry.url", CalcConfig.KAFKA_REGISTRY_URL);
//        props.put("security.protocol", "SASL_PLAINTEXT");
//        props.put("sasl.mechanism", "PLAIN");
//
//        KafkaProducer producer = new KafkaProducer(props);
//        Schema.Parser parser = new Schema.Parser();
//        Schema schema = parser.parse(joinResultJsonSchema.toString());
//        GenericRecord avroRecord = new GenericData.Record(schema);
//
//        JSONArray fields = joinResultJsonSchema.getJSONArray("fields");
//
//
//        avroRecord.put(name,value);
//        System.out.println("SPARK:MultiSql要发送送的数据:" + avroRecord);
//        ProducerRecord<Object, Object> record = new ProducerRecord<>("", String.valueOf(avroRecord.hashCode()), avroRecord);
//        producer.send(record);
//
//
//    }
}