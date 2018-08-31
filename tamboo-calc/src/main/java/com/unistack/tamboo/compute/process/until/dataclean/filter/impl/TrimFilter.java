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
@FilterType("trim")
public class TrimFilter implements Filter {
	private static Logger LOGGER = LoggerFactory.getLogger(TrimFilter.class);
	private List<String> fields;

	@Override
	public void init(JSONObject config){
		List<String> willTrimList = new ArrayList<>();
		JSONArray fieldsArray = config.getJSONArray("fields");
		for(int i =0;i<fieldsArray.size();i++){
			String trimField = fieldsArray.getJSONObject(i).getString("trimField");
			willTrimList.add(trimField);
		}
		this.fields = willTrimList;
		LOGGER.info("trim fields = " + this.fields);
	}

	@Override
	public JSONObject filter(JSONObject source){
		for (String field : fields){
			if (source.containsKey(field)){
				source.put(field, source.getString(field).trim());
			}
		}

		JSONObject result = new JSONObject();
		result.put("code","200");
		result.put("msg","");
		result.put("data",source);
		return result;
	}
}
