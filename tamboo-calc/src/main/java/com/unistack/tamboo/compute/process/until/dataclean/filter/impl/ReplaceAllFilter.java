package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import com.unistack.tamboo.compute.process.until.dataclean.filter.FilterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiejing.kane
 *
 */
@FilterType("replaceall")
public class ReplaceAllFilter implements Filter {
	private static Logger LOGGER = LoggerFactory.getLogger(RenameFilter.class);
	private JSONArray configs;


	//{"type":"ReplaceAll","fields":[{"field":"目标字段#string","regex":"正则表达式","repl":"替换成的字段"}]}
	@Override
	public void init(JSONObject config) {
		configs = config.getJSONArray("fields");
	}

	@Override
	public JSONObject filter(JSONObject source) {
		for(int i=0;i<configs.size();i++){
			JSONObject item = configs.getJSONObject(i);
			String field = item.getString("field");
			String regex = item.getString("regex");
			String repl = item.getString("repl");

			if(source.containsKey(field)){
				String fieldValue = source.getString(field);
				if(null != fieldValue){
					String resultValue = fieldValue.replaceAll(regex, repl);
					source.put(field,resultValue);
				}
			}
		}

		JSONObject result = new JSONObject();
		result.put("code","200");
		result.put("msg","");
		result.put("data",source);
		return result;
	}
}
