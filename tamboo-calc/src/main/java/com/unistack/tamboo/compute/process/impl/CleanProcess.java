package com.unistack.tamboo.compute.process.impl;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.SchemaUtils;
import com.unistack.tamboo.compute.process.StreamProcess;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import com.unistack.tamboo.compute.process.until.dataclean.filter.impl.XmlToJsonFilter;
import com.unistack.tamboo.compute.process.until.dataclean.util.CommonUtils;
import com.unistack.tamboo.compute.utils.KafkaUtil;
import com.unistack.tamboo.compute.utils.spark.AvroUtil;
import com.unistack.tamboo.compute.utils.spark.CalcConfig;
import io.confluent.kafka.schemaregistry.client.rest.RestService;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


/**
 * @author hero.li
 * 数据清洗实现类
 */
public class CleanProcess implements StreamProcess{
    private static long count = 0;
    private static Logger LOGGER = LoggerFactory.getLogger(CleanProcess.class);
    private static Map<String, Object> CLEANER = new HashMap<>();
    private String toTopic;
    private String fromTopic;
    private JSONArray cleanerList;
    private JSONObject outSchema;

    public CleanProcess(JSONObject config){
        this.toTopic = config.getJSONObject("kafka").getString("to_topic");
        this.fromTopic = config.getJSONObject("kafka").getString("from_topic");
        this.cleanerList = config.getJSONObject("template").getJSONArray("calcInfoList");

        Optional<Schema> op_schema = AvroUtil.getSchema(fromTopic);
        Schema tempSchema = op_schema.get();
        outSchema = schemaProcess(tempSchema);
        LOGGER.info("SPARK:初始化schema="+ outSchema);
        JSONObject registerResult = SchemaUtils.registerSchema(JSONObject.parseObject(outSchema.toString().replace("integer", "int")), toTopic + "-value");
        LOGGER.info("SPARK:向输出topic注册schema的结果：" + registerResult);
    }

    private void renameSchemaProcess(JSONArray schemaFields, JSONArray filterFields) {
        for(int j=0;j<filterFields.size();j++){
            JSONObject target = filterFields.getJSONObject(j);
            String newName = target.getString("new");
            String oldName = target.getString("old");
            for(int k=0;k<schemaFields.size();k++){
                JSONObject target_o = schemaFields.getJSONObject(k);
                String name = target_o.getString("name");
                if(name.equalsIgnoreCase(oldName)){
                    target_o.remove(oldName);
                    target_o.put("name",newName);
                }
            }
        }
    }

    @Override
    public void logic(JavaInputDStream<ConsumerRecord<String, String>> line){
        line.foreachRDD(rdd ->{
            if (rdd.count() > 0){
                rdd.foreachPartition(itr ->{
                        Properties props = KafkaUtil.getKafkaAvroProducerProperties();
                        KafkaProducer p = new KafkaProducer(props);
                        Schema.Parser parser = new Schema.Parser();
                        LOGGER.info("SPARK:foreachPartitions_inSchema="+ outSchema);
                        Schema schema = parser.parse(outSchema.toString());
                        LOGGER.info("SPARK:parser.parse_schema="+schema);
                        while (itr.hasNext()){
                            Object record = itr.next().value();
                            LOGGER.info("SPARK:(数据清洗任务)接收的数据:" + record);
                            Optional<JSONObject> result = dataClean(record2JSON((GenericData.Record)record));
                            if(result.isPresent()){
                                if (++count % 50 == 0){
                                    LOGGER.info("SPARK:(数据清洗任务)数据处理量:==>" + count);
                                }
                                GenericRecord avroRecord = new GenericData.Record(schema);
                                Set<Map.Entry<String,Object>> entries = result.get().getJSONObject("data").entrySet();
                                for(Map.Entry<String,Object> me : entries){
                                    String key = me.getKey();
                                    Object value = me.getValue();
                                    avroRecord.put(key, value);
                                }

                                //注意:如果数据清洗出现错误则发送的还是原始数据
                                ProducerRecord<Object, byte[]> data = new ProducerRecord(toTopic, String.valueOf(avroRecord.hashCode()), avroRecord);
                                p.send(data);
                                LOGGER.info("SPARK:(数据清洗任务)发送的数据:" + avroRecord);
                            }
                        }
                });
            }
        });
    }

    /**
     * 下面这一段代码是我写的,说实话真的很烂，而且还是烂的没法看的烂，
     * 写这段代码的时候我的内❤心是拒绝的，实在是没办法了我才这样写的
     * <p>
     * 之所以这样写是因为原本计划平台上处理的数据格式都是json格式的，
     * 后来发现东航那边处理的数据需要是xml格式的，所以想着转换一下，然而事情并没有这么简单
     * 前台的服务添加组件中没有一个xml->json格式的转换器，所以就把转换功能放入清洗模块，但是
     * 在清洗模块中都是实现自com.unistack.tamboo.compute.process.until.dataclean.filter.Filter
     * 这个接口中处理的数据都是json，
     * 那么问题来了，也就是说非要在处理json格式的模块中强行塞入一个xml->json转换的功能，我也很无奈，
     * 我也很绝望啊！
     * <p>
     * 目前采用的办法是
     * 根据cleanerList中的项item的type属性,如果是
     *
     * @param streamData
     * @return
     */
    public Optional<JSONObject> dataClean(JSONObject streamData){
//        JSONObject jsonData = null;
        for (int i = 0; i < cleanerList.size(); i++){
            try {
                JSONObject config = cleanerList.getJSONObject(i);
                String filterName = config.getString("type");
                Object oFilter = CLEANER.containsKey(filterName) ? CLEANER.get(filterName) : getFilter(filterName);

                if (null == oFilter){
                    LOGGER.error("SPARK:" + filterName + "实体类获取失败!,data=" + streamData + " ,config=" + config);
                    return Optional.empty();
                }

                if ("XmlToJson".equals(filterName)) {
                    // xml格式的数据转换为json格式
                    XmlToJsonFilter xf = (XmlToJsonFilter) oFilter;
                    JSONObject jsonResult = xf.filter(streamData.toJSONString());
                    JSONObject result = new JSONObject();
                    result.put("code", "200");
                    result.put("msg", "");
                    result.put("data", jsonResult);
                    return Optional.of(result);
                } else {
                    Filter f = (Filter) oFilter;
                    f.init(config);
                    JSONObject result = f.filter(streamData);
                    if (null == result){
                        LOGGER.info("SPARK:数据清洗结果为空!");
                        return Optional.empty();
                    }
                }
            }catch(Exception e){
                return Optional.empty();
            }
        }

        JSONObject result = new JSONObject();
        result.put("code", "200");
        result.put("msg","");
        result.put("data", streamData);
        return Optional.of(result);
    }

    /**
     * 通过反射获取filter
     *
     * @param filterName
     * @return
     */
    private Object getFilter(String filterName) {
        try {
            Class<?> clazz = Class.forName("com.unistack.tamboo.compute.process.until.dataclean.filter.impl." + filterName + "Filter");
            Object filter = clazz.getConstructor().newInstance();
            CLEANER.put(filterName, filter);
            return filter;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 对于schema的处理,因为当数据清洗时schema有可能也会发生变化,所以schema也要跟着做处理
     * @param outputSchema
     * @return
     */
    private JSONObject schemaProcess(Schema outputSchema){
        JSONObject schemaJson = JSONObject.parseObject(outputSchema.toString());
        JSONArray schemaFields = schemaJson.getJSONArray("fields");

        for(int i=0;i<this.cleanerList.size();i++){
            JSONObject o  = this.cleanerList.getJSONObject(i);
            String type = o.getString("type");
            JSONArray filterFields = o.getJSONArray("fields");
            if("rename".equalsIgnoreCase(type)){
                renameSchemaProcess(schemaFields,filterFields);
                continue;
            }
            if("remove".equalsIgnoreCase(type)){
                removeSchemaProcess(schemaFields,filterFields);
                continue;
            }
            if("add".equalsIgnoreCase(type)){
                addSchemaProcess(schemaFields,i,filterFields);
                continue;
            }
            if("keep".equalsIgnoreCase(type)){
                keepSchemaProcess(schemaJson,schemaFields,i,filterFields);
                continue;
            }
            if("Underline".equalsIgnoreCase(type)){
                underlineSchemaProcess(schemaFields,i,filterFields);
                continue;
            }
        }
        return schemaJson;
    }

    private void underlineSchemaProcess(JSONArray schemaFields, int i, JSONArray filterFields) {
        for(int j=0;j<filterFields.size();j++){
            JSONObject item = filterFields.getJSONObject(i);
            String underlineField = item.getString("UnderlineField");
            for(int k=0;k<schemaFields.size();k++){
                JSONObject target_o = schemaFields.getJSONObject(k);
                String name = target_o.getString("name");
                if(name.equals(underlineField)){
                    String s = CommonUtils.camel2Underline(underlineField);
                    target_o.put("name",s);
                    break;
                }
            }
        }
    }

    private void keepSchemaProcess(JSONObject schemaJson, JSONArray schemaFields, int i, JSONArray filterFields) {
        for(int j=0;j<filterFields.size();j++){
            JSONObject item = filterFields.getJSONObject(i);
            String keepField = item.getString("keepField");
            int idx = -1;
            for(int k=0;k<schemaFields.size();k++){
                JSONObject schemaItem = schemaFields.getJSONObject(k);
                String name = schemaItem.getString("name");
                if(keepField.equals(name)){
                    idx = k;
                    break;
                }
            }
            if(idx != -1){
                JSONObject target_keepField = schemaFields.getJSONObject(idx);
                JSONArray keepArr = new JSONArray();
                keepArr.add(target_keepField);
                schemaJson.put("fields",keepArr);
            }
        }
    }

    private void addSchemaProcess(JSONArray schemaFields, int i, JSONArray filterFields) {
        for(int j=0;j<filterFields.size();j++){
            JSONObject item = filterFields.getJSONObject(i);
            String addKey = item.getString("addKey");
            Object addValue = item.get("addValue");

            String addValueType = "string";
            String simpleName = addValue.getClass().getSimpleName();
            if(simpleName.equalsIgnoreCase("integer")){
                addValueType = "int";
            }

            JSONObject o1 = new JSONObject();
            o1.put("name",addKey);
            o1.put("type",addValueType);
            schemaFields.add(o1);
        }
    }

    private void removeSchemaProcess(JSONArray schemaFields, JSONArray filterFields) {
        //{"type":"Remove","filedCount":2,"fields":[{"removeFiled":"目标字段#string"}]}
        for(int j=0;j<filterFields.size();j++){
            String removeFiled = filterFields.getJSONObject(j).getString("removeFiled");
            int idx = -1;
            for(int k=0;k<schemaFields.size();k++){
                JSONObject schemaItem = schemaFields.getJSONObject(k);
                String name = schemaItem.getString("name");
                if(removeFiled.equals(name)){
                    idx = k;
                    break;
                }
            }
            if(idx != -1){schemaFields.remove(idx);}
        }
    }

    /**
     * 由于把record直接toString()转换为字符串会丢失部分类型信息，这个方法用于在不丢失类型信息的前期下
     * 把GenericData.Record转换为JSONObject 后期看能不能有更好的方法
     * @param record
     * @return
     */
    private JSONObject record2JSON(GenericData.Record record){
        JSONObject o = new JSONObject();
        List<Schema.Field> fields = record.getSchema().getFields();
        for(Schema.Field f : fields){
            String key = f.name();
            Object value = record.get(key);
            o.put(key,value);
        }
        return o;
    }
}