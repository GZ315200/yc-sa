package com.unistack.tamboo.compute.process.until.dataclean.filter;

import com.alibaba.fastjson.JSONObject;

/**
 * @author xiejing.kane
 *
 */
public interface Filter {

	void init(JSONObject config) throws Exception;

	JSONObject filter(JSONObject source) throws Exception;
}
