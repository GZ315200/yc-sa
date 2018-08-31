package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import com.unistack.tamboo.compute.process.until.dataclean.filter.FilterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hero.Li
 *
 */
@FilterType("date")
public class DateFilter implements Filter {
	private static Logger LOGGER = LoggerFactory.getLogger(RenameFilter.class);
	private String field;

	private ThreadLocal<SimpleDateFormat> sourceSdf;
	private ThreadLocal<SimpleDateFormat> targetSdf;

	//{"type":"DateFilter","fields":[{"field":"原始字段#string","source":"原始格式#string","target":"目标格式#string"}]}
	@Override
	public void init(JSONObject config) {
		JSONObject firstEle = config.getJSONArray("fields").getJSONObject(0);

		field = firstEle.getString("field");
		String sourceFormat = firstEle.getString("source");
		String targetFormat = firstEle.getString("target");
		sourceSdf = new ThreadLocal<SimpleDateFormat>() {
			@Override
			protected SimpleDateFormat initialValue() {
				return new SimpleDateFormat(sourceFormat);
			}
		};
		targetSdf = new ThreadLocal<SimpleDateFormat>() {
			@Override
			protected SimpleDateFormat initialValue() {
				return new SimpleDateFormat(targetFormat);
			}
		};
		LOGGER.info(MessageFormat.format("field = {0}, sourceFormat = {1}, targetFormat = {2}", field, sourceFormat,
				targetFormat));
	}

	@Override
	public JSONObject filter(JSONObject source){
		JSONObject result = new JSONObject();
		if(source.containsKey(field)){
			try{
				Date sourceDate = sourceSdf.get().parse(source.getString(field));
				String targetDate = targetSdf.get().format(sourceDate);
				source.put(field, targetDate);
			}catch(ParseException e){
				result.put("code","199");
				result.put("msg","日期格式解析异常,源格式/目标格式不正确");
				return result;
			}
		}

		result.put("code","200");
		result.put("msg","");
		result.put("data",source);
		return result;
	}
}
