package com.unistack.tamboo.sa.dd.constant;

/**
 * @author anning
 * @date 2018/5/30 上午10:02
 * @description: kafka record的转换格式
 */
public enum ConverterType {
    STRING("org.apache.kafka.connect.storage.StringConverter"),
    JSON("org.apache.kafka.connect.json.JsonConverter"),
    AVRO("io.confluent.connect.avro.AvroConverter");

    private String converter;
    ConverterType(String converter){
        this.converter=converter;
    }

    public String getConverter(){
        return converter;
    }

}
