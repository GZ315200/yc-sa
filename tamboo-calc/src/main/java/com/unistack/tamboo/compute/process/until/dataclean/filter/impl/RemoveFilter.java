package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import com.unistack.tamboo.compute.process.until.dataclean.filter.FilterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiejing.kane
 *
 */
@FilterType("remove")
public class RemoveFilter implements Filter {
	private static Logger LOGGER = LoggerFactory.getLogger(RemoveFilter.class);
	private List<String> fields;

	//{"type":"Remove","fields":[{"removeFiled":"目标字段#string"}]}
	@Override
	public void init(JSONObject config) {
		JSONArray fieldsArray = config.getJSONArray("fields");
		List<String> willRemoveList = new ArrayList<>();
		for(int i=0; i<fieldsArray.size(); i++){
			String removeFiled = fieldsArray.getJSONObject(i).getString("removeFiled");
			willRemoveList.add(removeFiled);
		}
		this.fields = willRemoveList;
		LOGGER.info("remove fields = " + this.fields);
	}

	@Override
	public JSONObject filter(JSONObject source) {
		for(String field : fields){
			source.remove(field);
		}

		JSONObject result = new JSONObject();
		result.put("code","200");
		result.put("msg","");
		result.put("data",source);
		return result;
	}
}