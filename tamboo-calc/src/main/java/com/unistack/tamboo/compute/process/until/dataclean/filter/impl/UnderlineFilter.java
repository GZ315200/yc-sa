package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import com.unistack.tamboo.compute.process.until.dataclean.filter.FilterType;
import com.unistack.tamboo.compute.process.until.dataclean.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * 将key由驼峰表达式替换成下划线表达式
 * <p>
 * gameId -> game_id
 * 
 * @author xiejing.kane
 *
 */
@FilterType("underline")
public class UnderlineFilter implements Filter {
	private static Logger LOGGER = LoggerFactory.getLogger(UnderlineFilter.class);
	private List<String> fields;

	//{"type":"Underline","fields":[{"UnderlineField":"字段名#string"}]}
	@Override
	public void init(JSONObject config){
		JSONArray fieldsArray = config.getJSONArray("fields");
		List<String> willUnderline = new ArrayList<>();
		for(int i=0; i<fieldsArray.size(); i++){
			String name = fieldsArray.getJSONObject(i).getString("UnderlineField");
			willUnderline.add(name);
		}
		this.fields = willUnderline;
		LOGGER.info("underline fields = " + this.fields);
	}

	@Override
	public JSONObject filter(JSONObject source) {
		for (Entry<String, Object> entry : Sets.newHashSet(source.entrySet())) {
			String key = entry.getKey();
			if (fields.contains(key) || fields.contains("*")) {
				source.put(CommonUtils.camel2Underline(key), source.remove(key));
			}
		}

		JSONObject result = new JSONObject();
		result.put("code","200");
		result.put("msg","");
		result.put("data",source);
		return result;
	}
}
