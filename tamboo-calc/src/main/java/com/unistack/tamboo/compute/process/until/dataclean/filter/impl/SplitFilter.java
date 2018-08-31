package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import com.unistack.tamboo.compute.process.until.dataclean.filter.FilterType;
import com.unistack.tamboo.compute.process.until.dataclean.util.CommonUtils;
import com.unistack.tamboo.compute.process.until.dataclean.util.ExtractCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.MessageFormat;

/**
 * @author xiejing.kane
 *
 */
@FilterType("split")
public class SplitFilter implements Filter {
	private static Logger LOGGER = LoggerFactory.getLogger(SplitFilter.class);
//	private static  boolean DEFAULT_DISCARD_EXISTING = false;
//	private static  boolean DEFAULT_PRESERVE_EXISTING = true;
//	private static  boolean DEFAULT_APPEND_PREFIX = false;

	private String field;
	private boolean discardExisting;
	private boolean preserveExisting;
	private String delimiter;
	private String assigner;

	/**
	 * 注意：设置appendPrefix=true的话，后续的filter使用字段时要带上前缀，否则将会找不到field
	 */
	private boolean appendPrefix;

	//{"type":"Split","fields":[{"field": "目标字段#string","discard_existing":"忽略原始值#boolean","preserve_existing":"保留原数据#boolean","append_prefix":"添加前缀#boolean","delimiter": "分隔符#string","assigner":"二次分割符#string"}]}
	@Override
	public void init(JSONObject config){
		JSONObject firstEle = config.getJSONArray("fields").getJSONObject(0);

		field = firstEle.getString("field");
		discardExisting  = firstEle.getBoolean("discard_existing");
		preserveExisting = firstEle.getBoolean("preserve_existing");
		appendPrefix 	 = firstEle.getBoolean("append_prefix");
		delimiter = firstEle.getString("delimiter");
		assigner = firstEle.getString("assigner");
		LOGGER.info(
				MessageFormat.format("field = {0}, discardExisting = {1}, preserveExisting = {2}, appendPrefix = {3}",
						field, discardExisting, preserveExisting, appendPrefix));
	}

	@Override
	public JSONObject filter(JSONObject source){
		ExtractCallable excall = new ExtractCallable() {
			@Override
			public JSONObject call() throws Exception {
				JSONObject result = new JSONObject();
				for (String segment : getSource().split(delimiter)) {
					String[] keyValue = segment.split(assigner);
					result.put(keyValue[0], keyValue[1]);
				}
				return result;
			}
		};

		JSONObject result = new JSONObject();
		try {
			JSONObject extract = CommonUtils.extract(source, field, discardExisting, preserveExisting, appendPrefix, excall);
			result.put("code","200");
			result.put("msg","");
			result.put("data",extract);
			return result;
		}catch(Exception e){
			e.printStackTrace();
			result.put("code","199");
			result.put("msg","拆分异常");
			return result;
		}

	}
}
