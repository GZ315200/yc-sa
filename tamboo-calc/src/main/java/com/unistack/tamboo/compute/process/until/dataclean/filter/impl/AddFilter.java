package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import com.unistack.tamboo.compute.process.until.dataclean.filter.FilterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chinaLi
 *
 */
@FilterType("add")
public class AddFilter implements Filter {
	private static Logger LOGGER = LoggerFactory.getLogger(AddFilter.class);
	private JSONArray fields;

	//{"type":"Add","fields":[{"addKey":"新增键","addValue":"新增值#string","preserve_existing":"覆盖原值#boolean"}]}
	@Override
	public void init(JSONObject config){
		fields = config.getJSONArray("fields");
		LOGGER.info("add fields = " + fields);
	}

	/**
	 * 添加清洗过滤器
	 * @param source 源数据
	 * @return
	 * {"code":"错误码","msg":"描述","data":{}}
	 * code:200代表争正确，其余代表失败
	 * msg: 错误描述，     当code==200时为空
	 * data:清洗后的数据   当code!=200时，为空
	 */
	@Override
	public JSONObject filter(JSONObject source){
		for(int i=0;i<fields.size();i++){
			JSONObject item = fields.getJSONObject(i);
			String addKey = item.getString("addKey");
			String addValue = item.getString("addValue");
			Boolean preserveExisting = item.getBoolean("preserve_existing");


			if(source.containsKey(addKey)){
				if(preserveExisting)
					source.put(addKey,addValue);
			}else{
				source.put(addKey,item.get("addValue"));
			}
		}

		JSONObject result = new JSONObject();
		result.put("code","200");
		result.put("msg","");
		result.put("data",source);
		return result;
	}
}