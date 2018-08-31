package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import com.unistack.tamboo.compute.process.until.dataclean.filter.FilterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author xiejing.kane
 */
@FilterType("keep")
public class KeepFilter implements Filter {
    private static Logger LOGGER = LoggerFactory.getLogger(KeepFilter.class);
    private List<String> fields;

    //{"type":"Keep","fields":[{"keepField":"保留字段#string"}]}
    @Override
    public void init(JSONObject config) {
        List<String> willKeepList = new ArrayList<>();
        JSONArray fieldsArray = config.getJSONArray("fields");
        for (int i = 0; i < fieldsArray.size(); i++) {
            String keepName = fieldsArray.getJSONObject(i).getString("keepField");
            willKeepList.add(keepName);
        }
        this.fields = willKeepList;
        LOGGER.info("keep fields = " + this.fields);
    }

    @Override
    public JSONObject filter(JSONObject source) {
        for (Entry<String, Object> entry : Sets.newHashSet(source.entrySet())) {
            if (!fields.contains(entry.getKey())) {
                source.remove(entry.getKey());
            }
        }

        JSONObject result = new JSONObject();
        result.put("code", "200");
        result.put("msg", "");
        result.put("data", source);
        return result;
    }
}
