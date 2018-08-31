package com.unistack.tamboo.compute.utils.spark;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.confluent.kafka.schemaregistry.client.rest.RestService;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import scala.collection.Iterator;

import java.util.Optional;

/**
 * @author hero.li
 */
public class AvroUtil{
    private static Logger LOGGER = Logger.getLogger(AvroUtil.class);

    /**
     * 把Row转换为GenericRecord
     * @param fields
     * @param row
     * @param avroRecord
     * @return
     */
    public static GenericRecord rowToAvroRecord(JSONArray fields, Row row,GenericRecord avroRecord){
        for(int i=0;i<fields.size();i++){
            JSONObject item = fields.getJSONObject(i);
            String name = item.getString("name");
            Object value = row.getAs(name);
            avroRecord.put(name,value);
        }
        return  avroRecord;
    }

    public static GenericRecord jsonToAvroRecord(JSONArray fields, JSONObject row, GenericRecord avroRecord){
        for(int i=0;i<fields.size();i++){
            JSONObject item = fields.getJSONObject(i);
            String name = item.getString("name");
            Object value = row.get(name);
            avroRecord.put(name,value);
        }
        return  avroRecord;
    }

    /**
     * 通过topic名称获取schema
     * @param topic
     * @return
     */
    public static Optional<Schema> getSchema(String topic){
        RestService restService = new RestService(CalcConfig.KAFKA_REGISTRY_URL);
        String subjectValueName = topic + "-value";
        try{
            io.confluent.kafka.schemaregistry.client.rest.entities.Schema confluent_schema = restService.getLatestVersion(subjectValueName);
            Schema.Parser parser = new Schema.Parser();
            Schema schema = parser.parse(confluent_schema.getSchema());
            return Optional.ofNullable(schema);
        } catch (Exception e){
            LOGGER.error("SPARK:流数据表[" + topic + "]schema获取失败!", e);
            e.printStackTrace();
            return Optional.empty();
        }
    }


    /**
     * 根据 Dataset <Row> 获取字符串格式的 schema
     * @param joinResult
     * @return
     */
    public static JSONObject getStringSchema(Dataset<Row> joinResult) {
        JSONObject schemaResult = new JSONObject();
        schemaResult.put("type", "record");
        schemaResult.put("name", "alarm");

        JSONArray fields = new JSONArray();
        StructType schema = joinResult.schema();
        Iterator<StructField> itr = schema.iterator();

        while (itr.hasNext()) {
            StructField item = itr.next();
            String name = item.name();
            String typeName = item.dataType().typeName();
            JSONObject field = new JSONObject();
            field.put("name", name);
            if ("integer".equals(typeName)) {
                typeName = "int";
            }
            field.put("type", typeName);
            fields.add(field);
        }
        schemaResult.put("fields", fields);
        return schemaResult;
    }



    //    private static byte[] rowToByte(Row row,JSONObject JsonSchema,Schema schema){
//        Injection<GenericRecord,byte[]> recordInjection = GenericAvroCodecs.toBinary(schema);
//        JSONArray fields = JsonSchema.getJSONArray("fields");
//        GenericData.Record avroRecord = new GenericData.Record(schema);
//
//        for(int i=0;i<fields.size();i++){
//            JSONObject item = fields.getJSONObject(i);
//            String name = item.getString("name");
//            Object value = row.getAs(name);
//            avroRecord.put(name,value);
//        }
//
//        byte[] bytes = recordInjection.apply(avroRecord);
//        return bytes;
//    }
//
//    private static Schema initSchema(String schema){
//        schema = schema.replace("integer","int");
//        Schema.Parser parser = new Schema.Parser();
//        Schema Schema = parser.parse(schema);
//        return Schema;
//    }

    private static int registerSchema(JSONObject schemaJson, String subjectName) {
        try {
            RestService restService = new RestService(CalcConfig.KAFKA_REGISTRY_URL);
            int id = restService.registerSchema(schemaJson.toJSONString(), subjectName);
            return id;
        } catch (Exception e) {
            return -1;
        }
    }


}
