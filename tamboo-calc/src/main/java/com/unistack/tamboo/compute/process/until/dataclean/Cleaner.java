package com.unistack.tamboo.compute.process.until.dataclean;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.unistack.tamboo.compute.exception.InvalidParameterException;
import com.unistack.tamboo.compute.process.until.dataclean.decoder.Decoder;
import com.unistack.tamboo.compute.process.until.dataclean.filter.Filter;
import com.unistack.tamboo.compute.process.until.dataclean.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.unistack.tamboo.compute.process.until.dataclean.Constants.*;

/**
 * @author xiejing.kane
 */
public class Cleaner {
    private static Logger LOGGER = LoggerFactory.getLogger(Cleaner.class);

    private Decoder decoder;
    private List<Filter> filterList;
    private Context context;

    private Cleaner(Context context) throws Exception {
        this.context = context;
        validate();
        initDecoder();
        initFilterList();
    }

    private void initFilterList() throws Exception {
        filterList = Lists.newArrayList();
        if (!context.containsKey(FILTERS)) {
            // FILTER is optional
            return;
        }
        Map<String, String> annotated = CommonUtils.getAnnotatedFilters();
        for (Object obj : context.getJSONArray(FILTERS)) {
            JSONObject json = (JSONObject) obj;
            String type = json.getString(TYPE);
            JSONObject filterConfig = json.getJSONObject(FILTER_PARAMS);
            // 如果根据filter type找不到相应的注解，则把filter type当做class name解析
            String className = annotated.getOrDefault(type, type);
            Filter filter;
            try {
                filter = (Filter) Class.forName(className).newInstance();
                filter.init(filterConfig);
                filterList.add(filter);
            } catch (Exception e) {
                LOGGER.error("Failed to create filter: " + className, e);
                throw new InvalidParameterException(e);
            }
        }
    }

    private void initDecoder() throws Exception {
        Context decoderContext = context.getSubContext(DECODER);
        String type = decoderContext.getString(TYPE, "json");
        Map<String, String> annotated = CommonUtils.getAnnotatedDecoders();
        // 如果根据decoder type找不到相应的注解，则把decoder type当做class name解析
        String className = annotated.getOrDefault(type, type);
        try {
            decoder = (Decoder) Class.forName(className).newInstance();
            decoder.init(decoderContext);
        } catch (Exception e) {
            LOGGER.error("Failed to create decoder: " + className, e);
            throw new InvalidParameterException(e);
        }
    }

    public Result process(String source) {
        JSONObject data;
        try {
            data = decoder.decode(source);
        } catch (Exception e) {
            LOGGER.error("Failed to decode: " + source + ", decoder = " + decoder.getClass().getSimpleName(), e);
            return new Result(source, null, e);
        }
        for (Filter filter : filterList) {
            try {
                data = filter.filter(data);
                if (data == null || data.isEmpty()) {
                    // 被过滤掉的数据,非异常
                    return new Result(null, source);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to filter: " + source + ", filter = " + filter.getClass().getSimpleName(), e);
                return new Result(source, data, e);
            }
        }
        return new Result(data, source);
    }

    private void validate() {
        // to do
    }

    public static Cleaner create(JSONObject json) throws Exception {
        Cleaner formatter = new Cleaner(new Context(json));
        return formatter;
    }

    public static Cleaner create(Properties props) throws Exception {
        Cleaner formatter = new Cleaner(new Context(props));
        return formatter;
    }
}
