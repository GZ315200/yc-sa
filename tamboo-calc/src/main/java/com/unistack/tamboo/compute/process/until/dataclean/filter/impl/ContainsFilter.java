package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContainsFilter implements Filter{
    private static Logger LOGGER = LoggerFactory.getLogger(ContainsFilter.class);
    private String rule;
    private String value;


    //{"rule":"A.B.C","value":"*And*"}
    @Override
    public void init(JSONObject config) throws Exception {
        this.rule = config.getString("rule");
        this.value = config.getString("value");
    }

    @Override
    public JSONObject filter(JSONObject source) throws Exception {
        String[] rules = rule.split("\\.");

        JSONObject tempData = source;
        for(int i=0;i<rules.length;i++){
            String ruleItem = rules[i];
            if(i == rules.length-1){
                String resultValue = tempData.getString(ruleItem);
                if(value.startsWith("*") && value.endsWith("*")){
                    String t = value.replace("*", "");
                    return (!StringUtils.isBlank(resultValue) && resultValue.contains(t)) ? source : null;
                }else if(value.startsWith("*")){
                    String t = value.replace("*", "");
                    return (!StringUtils.isBlank(resultValue) && resultValue.endsWith(t)) ? source : null;
                }else if(value.endsWith("*")){
                    String t = value.replace("*", "");
                    return (!StringUtils.isBlank(resultValue) && resultValue.startsWith(t)) ? source : null;
                }else if(value.contains("*")){
                    String[] tt = value.split("\\*");
                    return (!StringUtils.isBlank(resultValue) && resultValue.startsWith(tt[0]) && resultValue.endsWith(tt[1])) ? source : null;
                }else{
                    return (!StringUtils.isBlank(resultValue) && resultValue.equals(value)) ? source : null;
                }
            }else{
                tempData = tempData.getJSONObject(ruleItem);
                if(null == tempData){
                    LOGGER.info("SPARK:数据ContainsFilter该json不符合拆分规则,rule="+rule+"  原始json："+source);
                    return null;
                }

            }
        }
        return null;
    }
}
