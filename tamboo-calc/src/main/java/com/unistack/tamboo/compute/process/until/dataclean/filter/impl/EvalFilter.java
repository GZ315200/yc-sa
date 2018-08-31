package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSONObject;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import com.unistack.tamboo.compute.process.until.dataclean.filter.FilterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.text.MessageFormat;
import java.util.Map.Entry;

/**
 * @author xiejing.kane
 *
 */
@FilterType("eval")
public class EvalFilter implements Filter {
	private static Logger LOGGER = LoggerFactory.getLogger(EvalFilter.class);
	private ScriptEngine engine;
	private String field;
	private String expr;

	//{"type":"Eval","fields":[{"field":"原字段#string","expr":"表达式#string"}]}
	@Override
	public void init(JSONObject config){
		JSONObject firstEle = config.getJSONArray("fields").getJSONObject(0);
		field = firstEle.getString("field");
		expr = firstEle.getString("expr");
		ScriptEngineManager mgr = new ScriptEngineManager();
		engine = mgr.getEngineByName("JavaScript");
		LOGGER.info(MessageFormat.format("field = {0}, expr = {1}", field, expr));
	}

	@Override
	public JSONObject filter(JSONObject source) throws ScriptException {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, Object> entry : source.entrySet()){
			sb.append(entry.getKey()).append("=").append(parse(entry.getValue())).append(";");
		}
		sb.append(expr);
		source.put(field, engine.eval(sb.toString()));

		JSONObject result = new JSONObject();
		result.put("code","200");
		result.put("msg","");
		result.put("data",source);
		return result;
	}

	private String parse(Object obj) {
		try {
			return Double.parseDouble(obj.toString()) + "";
		} catch (NumberFormatException e) {
			return "'" + obj + "'";
		}
	}
}
