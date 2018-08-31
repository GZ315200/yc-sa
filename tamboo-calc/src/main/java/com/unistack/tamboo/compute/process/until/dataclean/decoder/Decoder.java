package com.unistack.tamboo.compute.process.until.dataclean.decoder;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.Context;

/**
 * @author xiejing.kane
 *
 */
public interface Decoder {
	void init(Context context) throws Exception;

	JSONObject decode(String source) throws Exception;
}