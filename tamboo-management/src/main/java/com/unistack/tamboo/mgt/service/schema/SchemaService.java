package com.unistack.tamboo.mgt.service.schema;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.commons.utils.SchemaUtils;
import com.unistack.tamboo.mgt.dao.collect.TopicInfoDao;
import com.unistack.tamboo.mgt.helper.TopicHelper;
import com.unistack.tamboo.mgt.model.collect.TopicInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.unistack.tamboo.commons.utils.SchemaUtils.getFailed;
import static com.unistack.tamboo.commons.utils.SchemaUtils.getSucceed;
import static java.util.stream.Collectors.toList;

/**
 * @author anning
 * @date 2018/7/27 上午11:28
 * @description: schema service
 */
@Service
public class SchemaService {

    @Autowired
    private TopicHelper topicHelper;

    @Autowired
    private TopicInfoDao topicInfoDao;

    /**
     * 获得所有subject及其最新的schema version
     * @param pageIndex
     * @return
     */
    public JSONObject getSubjectsAndVersionByPageInt(int pageIndex){
        JSONObject getAllResult = SchemaUtils.getAllSubjects();
        if (!getAllResult.getBoolean("succeed")) {
            return getAllResult;
        }
        List<String> allSubjects = getAllResult.getObject("msg", ArrayList.class);
        return getSubjectsAndVersionByPage(allSubjects,pageIndex);
    }


    /**
     * 获得指定subject及其最新的schema version
     *
     * @return {"isSucceed":true,"msg":JSONArray}
     */
    public JSONObject getSubjectsAndVersionByPage(List<String> collect,int pageIndex) {
        List<String> allSubjects = collect.stream().filter(x -> x.endsWith("-value")).collect(toList());
        JSONArray array = new JSONArray();
        int pageSize = 10;
        if (pageIndex < 1) {
            pageIndex = 1;
        }
        int size = allSubjects.size();
        int startSize;
        int endSize;
        Collections.sort(allSubjects);
        if (pageSize * (pageIndex - 1) > pageSize * (size / pageSize)) {
            startSize = pageSize * (size / pageSize);
        } else {
            startSize = pageSize * (pageIndex - 1);
        }
        if (pageSize * pageIndex - 1 >= size) {
            endSize = size;
        } else {
            endSize = pageSize * pageIndex;
        }
        List<String> firstPageList = allSubjects.subList(startSize, endSize);
        for (String subject : firstPageList) {
            ArrayList allVersions = SchemaUtils.getVersionsBySubject(subject).getObject("msg", ArrayList.class);
            //TODO 返回值应该加上allVersions 2,3 实际没有v.1
            array.add(new JSONObject().fluentPut("name", subject).fluentPut("version", allVersions.get(allVersions.size() - 1)));
        }
        JSONObject data = new JSONObject(true);

        data.put("data", array);
        data.put("pagination", new JSONObject().fluentPut("total", size).fluentPut("current", pageIndex).fluentPut("size", pageSize));
        JSONObject succeed = getSucceed(data);
        return succeed;
    }

    /**
     * 模糊查询
     * @param fuzzyString
     * @return json
     */
    public JSONObject fuzzyQuery(String fuzzyString,int pageIndex) {
        JSONObject getAllResult = SchemaUtils.getAllSubjects();
        if (!getAllResult.getBoolean("succeed")) {
            return getAllResult;
        }
        List<String> allSubjects = getAllResult.getObject("msg", ArrayList.class);
        List<String> fuzzyList = allSubjects.stream().filter(subjectName ->
                subjectName.contains(fuzzyString)
        ).collect(toList());
        return getSubjectsAndVersionByPage(fuzzyList,pageIndex);
    }


    /**
     * 通过subjectName 和version获得schema
     *
     * @param subject subject名称
     * @param version 版本
     * @return json
     */
    public JSONObject getSchemaBySubjectAndVersion(String subject, int version) {
        JSONObject schemaBySubjectAndVersion = SchemaUtils.getSchemaBySubjectAndVersion(subject, version);
        if (!schemaBySubjectAndVersion.getBoolean("succeed")) {
            return schemaBySubjectAndVersion;
        }
        JSONObject configBySubjectName = getConfigBySubjectName(subject);
        if (configBySubjectName.getBoolean("succeed")) {
            schemaBySubjectAndVersion.getJSONObject("msg").put("compatibilityLevel", configBySubjectName.getJSONObject("msg").getString("compatibilityLevel"));
        }
        return schemaBySubjectAndVersion;
    }

    /**
     * 注册一个schema
     *
     * @param json {"subjectName":"subject名称","schema":"json"}
     * @return
     */
    public JSONObject registerSchema(JSONObject json) {
        String subjectName = json.getString("subjectName");
        JSONObject schema = json.getJSONObject("schema");
        return SchemaUtils.registerSchema(schema, subjectName);
    }

    /**
     * 更新subject的schema兼容性
     *
     * @param compatibility
     * @param subjectName
     * @return
     */
    public JSONObject updateCompatibilityLevelForSubject(JSONObject compatibility, String subjectName) {
        String compatibility1 = compatibility.getString("compatibilityLevel");
        SchemaUtils.CompatibilityLevel compatibilityByType = SchemaUtils.CompatibilityLevel.getCompatibilityByType(compatibility1);
        if (compatibilityByType != null) {
            return SchemaUtils.updateCompatibilityLevelForSubject(compatibilityByType.type, subjectName);
        }
        return getFailed("错误的兼容性等级!");
    }

    public JSONObject getConfigBySubjectName(String subjectName) {
        JSONObject configBySubjectName = SchemaUtils.getConfigBySubjectName(subjectName);
        if (configBySubjectName.getBoolean("succeed")) {
            return configBySubjectName;
        }
        return SchemaUtils.getConfigRegistry();
    }

    /**
     * 根据时间戳 获得topic中的数据
     * @param subjectName
     * @return
     */
    public JSONObject getSomeRecordByTopicName(String subjectName){
        String topicName = subjectName.substring(0,subjectName.lastIndexOf("-"));
        TopicInfo topicInfoByTopicName = topicInfoDao.getTopicInfoByTopicName(topicName);
        if (topicInfoByTopicName==null){
            return SchemaUtils.getSucceed(new JSONArray().fluentAdd("未找到subject对应的topic!"));
        }
        List<byte[]> records = topicHelper.getRecords(topicName,2);
        return SchemaUtils.bytearr2json(subjectName, records);
    }

}
