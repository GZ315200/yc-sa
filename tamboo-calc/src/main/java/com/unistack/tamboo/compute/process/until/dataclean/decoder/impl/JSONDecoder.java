package com.unistack.tamboo.compute.process.until.dataclean.decoder.impl;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.Context;
import com.unistack.tamboo.compute.process.until.dataclean.decoder.Decoder;
import com.unistack.tamboo.compute.process.until.dataclean.decoder.DecoderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.InvalidParameterException;

/**
 * @author xiejing.kane
 *
 */
@DecoderType("json")
public class JSONDecoder implements Decoder {
	private static  Logger LOGGER = LoggerFactory.getLogger(JSONDecoder.class);

	@Override
	public void init(Context decoderContext) throws InvalidParameterException {
	}

	@Override
	public JSONObject decode(String source) {
		JSONObject json = JSONObject.parseObject(source);
		LOGGER.trace("decode result = " + json);
		return json;
	}

}
