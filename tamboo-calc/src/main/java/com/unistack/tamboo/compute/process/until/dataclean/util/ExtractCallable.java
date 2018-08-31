package com.unistack.tamboo.compute.process.until.dataclean.util;

import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.Callable;

/**
 * @author xiejing.kane
 *
 */
public abstract class ExtractCallable implements Callable<JSONObject> {
	private String source;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
}
