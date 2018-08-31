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
@FilterType("rename")
public class RenameFilter implements Filter {
	private static Logger LOGGER = LoggerFactory.getLogger(RenameFilter.class);
	private JSONArray fields;

	@Override
	public void init(JSONObject config) {
		fields = config.getJSONArray("fields");
		LOGGER.info("rename fields = " + fields);
	}


	/**
	 * {"type": "Rename",
	 *  "fields":[{"old":"原始字段","new":"新字段"}]
	 * }
	 * @param source
	 * @return
	 */
	@Override
	public JSONObject filter(JSONObject source){
//		for (Entry<String, Object> entry : fields.entrySet()) {
//			String oldName = entry.getKey();
//			String newName = entry.getValue().toString();
//			if (source.containsKey(oldName)) {
//				source.put(newName, source.remove(oldName));
//			}
//		}

		//JSONArray fields = this.fields.getJSONArray("fields");
		for(int i=0;i<fields.size();i++){
			JSONObject item = fields.getJSONObject(i);
			String oldName = item.getString("old");
			String newName = item.getString("new");
			source.put(newName,source.get(oldName));
			source.remove(oldName);
		}

		JSONObject result = new JSONObject();
		result.put("code","200");
		result.put("msg","");
		result.put("data",source);
		return source;
	}

}
