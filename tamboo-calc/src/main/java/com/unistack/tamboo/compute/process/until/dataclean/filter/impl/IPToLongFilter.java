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
@FilterType("iptolong")
public class IPToLongFilter implements Filter {
	private static Logger LOGGER = LoggerFactory.getLogger(IPToLongFilter.class);
	private JSONObject config;
//	private String field;
//	private String newField;


	//{"type":"IPToLong","fields":[{"field":"原始字段#string","new_field":"目标字段#string"}]}
	@Override
	public void init(JSONObject config) {
		this.config = config;
//		field = config.getString("field");
//		newField = config.getString("new_field");
		LOGGER.info("IPToLongFilter config="+config);
	}


	@Override
	public JSONObject filter(JSONObject source){
		JSONObject result = new JSONObject();
		JSONArray fieldsArray = this.config.getJSONArray("fields");
		for(int i=0;i<fieldsArray.size();i++){
			JSONObject item = fieldsArray.getJSONObject(i);
			String field = item.getString("field");
			String newField = item.getString("new_field");
			if(source.containsKey(field)){
				String ip = source.getString(field);
				if(!ip.isEmpty()){
					long targetValue ;
					try{
						targetValue = ipToLong(ip);
					}catch(Exception e){
						result.put("code","199");
						result.put("msg","ip地址异常");
						return result;
					}
					source.put(newField,targetValue);
				}
			}
		}

		result.put("code","200");
		result.put("msg","");
		result.put("data",source);
		return result;
	}



	private long ipToLong(String ip) throws Exception {
		String[] addrArray = ip.split("\\.");
		if(addrArray.length != 4){
			throw new Exception("ip地址长度必须等于4");
		}

		long num = 0;
		for (int i = 0; i < addrArray.length; i++){
			int item = -1;
			try{
				item = Integer.parseInt(addrArray[i]);
			}catch(Exception e){
				throw new Exception("ip格式不正确");
			}

			if(item <= 0 || item >= 255){
				throw new Exception("ip格式不正确");
			}

			int power = 3 - i;
			num += ((Integer.parseInt(addrArray[i]) % 256 * Math.pow(256, power)));
		}
		return num;
	}
}
