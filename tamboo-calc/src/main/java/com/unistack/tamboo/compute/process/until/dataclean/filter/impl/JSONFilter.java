package com.unistack.tamboo.compute.process.until.dataclean.filter.impl;

import com.alibaba.fastjson.JSON;
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
 */
@FilterType("json")
public class JSONFilter implements Filter {
    private static Logger LOGGER = LoggerFactory.getLogger(JSONFilter.class);
//	private static  boolean DEFAULT_DISCARD_EXISTING = false;
//	private static  boolean DEFAULT_PRESERVE_EXISTING = true;
//	private static  boolean DEFAULT_APPEND_PREFIX = false;

    private String field;
    private boolean discardExisting;
    private boolean preserveExisting;

    /**
     * 注意：设置appendPrefix=true的话，后续的filter使用字段时要带上前缀,否则将会找不到field
     * {"type":"JSON","fields":[{"field":"AA","discard_existing":"false","preserve_existing":"false","append_prefix":"true"}]}
     */
    private boolean appendPrefix;

    @Override
    public void init(JSONObject config) {
        JSONObject firstEle = config.getJSONArray("fields").getJSONObject(0);
        field = firstEle.getString("field");

        discardExisting = firstEle.getBoolean("discard_existing");
        preserveExisting = firstEle.getBoolean("preserve_existing");
        appendPrefix = firstEle.getBoolean("append_prefix");
        LOGGER.info(
                MessageFormat.format("field = {0}, discardExisting = {1}, preserveExisting = {2}, appendPrefix = {3}",
                        field, discardExisting, preserveExisting, appendPrefix));
    }

    /**
     * 将某个json格式的字段所有项都解析出来加入到源json中
     * {"A":"a-value","B":{"inner1":"v1","inner2":"v2"}}
     * =>{"A":"a-value","B":{"inner1":"v1","inner2":"v2"},"B.inner1":"v1","B.inner2":"v2"}
     *
     * @param source
     * @return
     */
    @Override
    public JSONObject filter(JSONObject source) {
        ExtractCallable excall = new ExtractCallable() {
            @Override
            public JSONObject call() throws Exception {
                return JSON.parseObject(getSource());
            }
        };

        JSONObject result = new JSONObject();
        try {
            CommonUtils.extract(source, field, discardExisting, preserveExisting, appendPrefix, excall);
            result.put("code", "200");
            result.put("msg", "");
            result.put("data", source);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", "199");
            result.put("msg", "解析异常!");
            return result;
        }
    }
}
