package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import com.unistack.tamboo.compute.process.until.dataclean.filter.FilterType;
import com.unistack.tamboo.compute.process.until.dataclean.util.CommonUtils;
import com.unistack.tamboo.compute.process.until.dataclean.util.ExtractCallable;
import io.thekraken.grok.api.Grok;
import io.thekraken.grok.api.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

/**
 * @author xiejing.kane
 *
 */
@FilterType("grok")
public class GrokFilter implements Filter {
	private static Logger LOGGER = LoggerFactory.getLogger(GrokFilter.class);
	private static  boolean DEFAULT_DISCARD_EXISTING = false;
	private static  boolean DEFAULT_PRESERVE_EXISTING = true;
	private static  boolean DEFAULT_APPEND_PREFIX = false;

	private String field;
	private boolean discardExisting;
	private boolean preserveExisting;
	/**
	 * 注意：设置appendPrefix=true的话，后续的filter使用字段时要带上前缀，否则将会找不到field
	 */
	private boolean appendPrefix;
	private Grok grok;

	@Override
	public void init(JSONObject config) throws Exception {
		field = config.getString("fields");
		discardExisting = (boolean) config.getOrDefault("discard_existing", DEFAULT_DISCARD_EXISTING);
		preserveExisting = (boolean) config.getOrDefault("preserve_existing", DEFAULT_PRESERVE_EXISTING);
		appendPrefix = (boolean) config.getOrDefault("append_prefix", DEFAULT_APPEND_PREFIX);
		LOGGER.info(MessageFormat.format(
				"field = {0}, discardExisting = {1}, preserveExisting = {2}, appendPrefix = {3}, patterns = {4}", field,
				discardExisting, preserveExisting, appendPrefix, config.getJSONObject("patterns")));
		initGrok(config);
	}

	private void initGrok(JSONObject config) throws Exception {
		String name = config.getString("entry");
		JSONObject patterns = config.getJSONObject("patterns");
		grok = CommonUtils.initGrok(name, patterns, null);
		LOGGER.info(MessageFormat.format("entry = {0}, patterns = {1}", name, patterns));
	}

	@Override
	public JSONObject filter(JSONObject source){
		ExtractCallable extCall = new ExtractCallable() {
			@Override
			public JSONObject call() throws Exception {
				Match gm = grok.match(getSource());
				gm.captures();
				JSONObject json = JSON.parseObject(gm.toJson());
				LOGGER.trace("decode result = " + json);
				return json;
			}
		};

		JSONObject result = new JSONObject();
		JSONObject data;
		try{
			data = CommonUtils.extract(source, field, discardExisting, preserveExisting, appendPrefix, extCall);
		}catch(Exception e){
			e.printStackTrace();
			result.put("code","199");
			result.put("msg","grok解析异常!");
			return  result;
		}

		result.put("code","200");
		result.put("msg","");
		result.put("data",data);
		return result;
	}
}
