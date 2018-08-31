package com.unistack.tamboo.mgt.controller.schema;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.mgt.service.schema.SchemaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author anning
 * @date 2018/7/27 上午9:19
 * @description: 注册中心调用
 */

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class SchemaController {

    @Autowired
    private SchemaService schemaService;

    @RequestMapping(value="/schemas",method= RequestMethod.GET)
    public JSONObject listSchemaAndVersionByPageIndex(@RequestParam(value = "fuzzyQuery",required = false) String fuzzyString,
                                                      @RequestParam("pageIndex") int pageIndex){
        if (StringUtils.isNotBlank(fuzzyString)){
            return schemaService.fuzzyQuery(fuzzyString,pageIndex);
        }
        return schemaService.getSubjectsAndVersionByPageInt(pageIndex);
    }


    @RequestMapping(value="/schemas/{name}/{version}",method= RequestMethod.GET)
    public JSONObject getSchemaByNameAndVersion(@PathVariable String name,
                                                @PathVariable int version ){
        return schemaService.getSchemaBySubjectAndVersion(name,version);
    }

    @RequestMapping(value = "/schemas",method = RequestMethod.POST)
    public JSONObject registrySchema(@RequestBody JSONObject json){
        return schemaService.registerSchema(json);
    }

    @RequestMapping(value = "/schemas/{name}/{version}",method = RequestMethod.POST)
    public JSONObject addSubjectVersion(@PathVariable String name,
                                        @PathVariable int version,
                                        @RequestBody JSONObject json){

        return null;
    }

    @RequestMapping(value="/schemas/config/{name}",method= RequestMethod.POST)
    public JSONObject updateCompatibilityLevelForSubject(@PathVariable String name,
                                                         @RequestBody JSONObject json){
        return schemaService.updateCompatibilityLevelForSubject(json,name);
    }

    @RequestMapping(value = "/records/{subjectName}",method = RequestMethod.GET)
    public JSONObject getKafkaRecord(@PathVariable String subjectName){
        return schemaService.getSomeRecordByTopicName(subjectName);
    }

}
