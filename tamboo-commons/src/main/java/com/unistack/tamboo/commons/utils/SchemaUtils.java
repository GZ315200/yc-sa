package com.unistack.tamboo.commons.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.confluent.kafka.schemaregistry.client.rest.RestService;
import io.confluent.kafka.schemaregistry.client.rest.entities.Config;
import io.confluent.kafka.schemaregistry.client.rest.entities.Schema;
import io.confluent.kafka.schemaregistry.client.rest.entities.SchemaString;
import io.confluent.kafka.schemaregistry.client.rest.entities.requests.ConfigUpdateRequest;
import io.confluent.kafka.schemaregistry.client.rest.exceptions.RestClientException;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.common.errors.SerializationException;

import java.io.IOException;
import java.util.*;

/**
 * 也可不适用此工具类，直接获得restService自行操作
 *
 * @author anning
 * @date 2018/7/16 下午6:43
 * @description: schema的添加查找...操作
 */

public class SchemaUtils {
    private static final String HTTP_PRE = "http://";
    private static RestService restService = new RestService(makeHttpPre(TambooConfig.KAFKA_REGISTRY_URL));

    public static String makeHttpPre(String url){
        if (!url.startsWith(HTTP_PRE)){
            url = HTTP_PRE+url;
        }
        return url;
    }

    /**
     * schema的兼容模式
     */
    public enum CompatibilityLevel {
        //不兼容
        NONE("none"),
        //全兼容
        FULL("FULL"),
        //向前兼容
        FORWARD("FORWARD"),
        //向后兼容
        BACKWARD("BACKWARD");
        public String type;

        CompatibilityLevel(String type) {
            this.type = type;
        }

        public static CompatibilityLevel getCompatibilityByType(String type) {
            for (CompatibilityLevel compatibility : CompatibilityLevel.values()) {
                if (type.equalsIgnoreCase(compatibility.type)) {
                    return compatibility;
                }
            }
            return null;
        }
    }


    public static JSONObject getSucceed(Object o) {
        return new JSONObject(true).fluentPut("succeed", true)
                .fluentPut("msg", o);
    }

    public static JSONObject getFailed(Object o) {
        return new JSONObject(true).fluentPut("succeed", false)
                .fluentPut("msg", o);
    }


    /**
     * 向scheme registry 添加一个schema
     *
     * @param schemaJson  schema json
     * @param subjectName subject名称
     * @return json 成功：{"succeed":true,"msg":id} id为返回的schema存储的id
     * 失败  {"succeed":false,"msg":string} string为错误信息
     */
    public static JSONObject registerSchema(JSONObject schemaJson, String subjectName) {
        try {
            int id = restService.registerSchema(schemaJson.toJSONString(), subjectName);
            return getSucceed(id);
        } catch (IOException e) {
            return getFailed(e.toString());
        } catch (RestClientException e) {
            int errorCode = e.getErrorCode();
            String errMsg;
            switch (errorCode) {
                case 42201:
                    errMsg = "格式错误/无效的schema";
                    break;
                case 409:
                    errMsg = "schema不兼容，具体可从config中的帮助图标中查看!";
                    break;
                default:
                    errMsg = e.toString();
            }
            return getFailed(errMsg);
        }
    }

    /**
     * 判断subject下该schema是否存在
     *
     * @param schemaJson
     * @param subjectName
     * @return
     */
    public static JSONObject checkSubjectIsExist(JSONObject schemaJson, String subjectName) {
        try {
            Schema schema = restService.lookUpSubjectVersion(schemaJson.toJSONString(), subjectName);
            return getSucceed(schema.getSchema());
        } catch (IOException | RestClientException e) {
            return getFailed(e.toString());
        }
    }


    /**
     * 通过id获取shcema
     *
     * @param schemaId id
     * @return json 成功{"succeed":true,"msg":schema json}
     */

    public static JSONObject getSchemaById(int schemaId) {
        try {
            SchemaString schema = restService.getId(schemaId);
            return getSucceed(JSONObject.parseObject(schema.getSchemaString()));
        } catch (IOException | RestClientException e) {
            return getFailed(e.toString());
        }
    }


    /**
     * 获得schema registry中所有的subjects
     *
     * @return json 成功{"succeed":true,"msg":list<String>}
     */

    public static JSONObject getAllSubjects() {
        try {
            List<String> allSubjects = restService.getAllSubjects();
            Collections.sort(allSubjects);
            return getSucceed(allSubjects);
        } catch (IOException | RestClientException e) {
            return getFailed(e.toString());
        }
    }


    /**
     * 获取subject下的所有schema version
     *
     * @param subject subjectName
     * @return json 成功{"succeed":true,"msg":list<Integer>}
     */

    public static JSONObject getVersionsBySubject(String subject) {
        try {
            List<Integer> allVersions = restService.getAllVersions(subject);
            return getSucceed(allVersions);
        } catch (IOException | RestClientException e) {
            return getFailed(e.toString());
        }
    }


    /**
     * 获取指定subject下的一个版本schema信息
     *
     * @param subject subjectName
     * @param version versions,可从getVersionsBySubject中获得
     * @return json 成功{"succeed":true,"msg":schema json}
     */

    public static JSONObject getSchemaBySubjectAndVersion(String subject, int version) {
        try {
            Schema schema = restService.getVersion(subject, version);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("schema", schema.getSchema());
            jsonObject.put("id", schema.getId());
            jsonObject.put("version", schema.getVersion());
            jsonObject.put("subject", schema.getSubject());
            return getSucceed(jsonObject);
        } catch (IOException | RestClientException e) {
            return getFailed(e.toString());
        }
    }


    /**
     * 获得subject下的最后一个version的schema,即最新的schema
     *
     * @param subject subjectName
     * @return json 成功{"succeed":true,"msg":schema json}
     */

    public static JSONObject getLastVersionBySubject(String subject) {
        try {
            Schema latestVersionSchema = restService.getLatestVersion(subject);
            return getSucceed(JSONObject.parseObject(latestVersionSchema.getSchema()));
        } catch (IOException | RestClientException e) {
            return getFailed(e.toString());
        }
    }

    /**
     * 更新subject的schema兼容性
     *
     * @param compatibility
     * @param subjectName
     * @return
     */
    public static JSONObject updateCompatibilityLevelForSubject(String compatibility, String subjectName) {
        ConfigUpdateRequest configUpdateRequest;
        try {
            configUpdateRequest = restService.updateCompatibility(compatibility, subjectName);
            String compatibilityLevel = configUpdateRequest.getCompatibilityLevel();
            return getSucceed(compatibilityLevel);
        } catch (IOException | RestClientException e) {
            return getFailed(e.toString());
        }
    }

    /**
     * 获得registry的config,目前只有compatibilityLevel
     *
     * @return
     */
    public static JSONObject getConfigRegistry() {
        OkHttpResponse response = OkHttpUtils.get(makeHttpPre(TambooConfig.KAFKA_REGISTRY_URL) + "/config");
        if (response.getCode() != 200) {
            return getFailed(response.getBody());
        }
        JSONObject config = JSONObject.parseObject(response.getBody());
        return getSucceed(config);
    }

    /**
     * 获得指定subject下的compatibilityLevel
     *
     * @param subjectName
     * @return
     */
    public static JSONObject getConfigBySubjectName(String subjectName) {
        try {
            Config config = restService.getConfig(subjectName);
            return getSucceed(new JSONObject().fluentPut("compatibilityLevel", config.getCompatibilityLevel()));
        } catch (IOException | RestClientException e) {
            return getFailed(e.toString());
        }
    }

    public static JSONObject bytearr2json(String subjectName,List<byte[]> records){
        JSONArray array = new JSONArray();
        Map<String, Object> props = new HashMap();
        props.put("schema.registry.url", makeHttpPre(TambooConfig.KAFKA_REGISTRY_URL));
        org.apache.avro.Schema.Parser parser = new org.apache.avro.Schema.Parser();
        Schema valueRestResponseSchema = null;
        try {
            valueRestResponseSchema = restService.getLatestVersion(subjectName);
        } catch (IOException | RestClientException e) {
            getSucceed(new JSONArray().fluentAdd(e.toString()));
        }
        org.apache.avro.Schema schema = parser.parse(valueRestResponseSchema.getSchema());
        KafkaAvroDeserializer valueAvroDeserializer = new KafkaAvroDeserializer();
        valueAvroDeserializer.configure(props,false);
        for (byte[] record:records){
            String deserialize;
            try {
                deserialize = valueAvroDeserializer.deserialize(subjectName, record, schema).toString();
                array.add(JSONObject.parseObject(deserialize));
            }catch (SerializationException e) {
                org.apache.avro.Schema schema2 = null;
//                try {
//                    schema2 = parser.parse(restService
//                            .getVersion(subjectName,restService.getAllVersions(subjectName).get(0)).getSchema());
//                } catch (IOException | RestClientException e1) {
//                    return getSucceed(e.toString());
//                }
//                deserialize = valueAvroDeserializer.deserialize(subjectName,record,schema2).toString();
            }
//            array.add(JSONObject.parseObject(deserialize));
        }
        return getSucceed(array);
    }

    public static void main(String[] args) {
        JSONObject schema = new JSONObject(true);
        JSONArray array = new JSONArray();
        array.add(new JSONObject().fluentPut("name", "name").fluentPut("type", "int"));
        array.add(new JSONObject().fluentPut("name", "age").fluentPut("type", "int"));
        schema.fluentPut("type", "record").fluentPut("name", "my")
                .fluentPut("fields", array);
//        System.out.println(registerSchema(schema,"testRegister2"));
//        System.out.println(addSubjectVersion(schema,"testRegister"));
//        System.out.println(getSchemaById(2).toJSONString());
//        getAllSubjects().getJSONArray("msg").forEach(System.out::println);
//        JSONObject versionsBySubject = getVersionsBySubject("jdbc3-value");
//        JSONArray msg = versionsBySubject.getJSONArray("msg");
//        System.out.println(msg.size());
//        msg.forEach(System.out::println);
//        System.out.println(getSchemaBySubjectAndVersion("testUI",2));
        System.out.println(getLastVersionBySubject("jdbc3-value"));
//        System.out.println(getConfigBySubjectName("testUI"));
//        System.out.println(getConfigRegistry());
    }
}

