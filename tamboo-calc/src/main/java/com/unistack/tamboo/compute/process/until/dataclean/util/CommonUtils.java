package com.unistack.tamboo.compute.process.until.dataclean.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.unistack.tamboo.compute.process.until.dataclean.decoder.DecoderType;
import com.unistack.tamboo.compute.process.until.dataclean.filter.FilterType;
import io.thekraken.grok.api.Grok;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.security.InvalidParameterException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.unistack.tamboo.compute.process.until.dataclean.Constants.*;

/**
 * @author xiejing.kane
 *
 */
public class CommonUtils {
	private static  Logger LOGGER = LoggerFactory.getLogger(CommonUtils.class);
	private static  SAXReader SAX_READER = new SAXReader();


	public static JSONObject generateFailure(boolean recordFailure, Throwable throwable, Object source) {
		JSONObject result = new JSONObject();
		if (recordFailure) {
			JSONObject failure = new JSONObject();
			failure.put(RES_THROWABLE, throwable);
			failure.put(RES_SOURCE, source);
			result.put(RES_FAILURE, failure);
		}
		return result;
	}

	/**
	 * 驼峰转下划线
	 *
	 * @param camel
	 * @return
	 */
	public static String camel2Underline(String camel) {
		Pattern pattern = Pattern.compile("[A-Z]");
		Matcher matcher = pattern.matcher(camel);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String w = matcher.group().trim();
			matcher.appendReplacement(sb, "_" + w.toLowerCase());
		}
		matcher.appendTail(sb);
		if (sb.charAt(0) == '_') {
			sb.delete(0, 1);
		}
		return sb.toString();
	}

	public static Map<String, String> getAnnotatedFilters() throws Exception {
		Map<String, String> annotatedFilters = Maps.newHashMap();
		for (Entry<Annotation, String> entry : getAnnotatedClasses(FilterType.class).entrySet()) {
			annotatedFilters.put(((FilterType) entry.getKey()).value(), entry.getValue());
		}
		return annotatedFilters;
	}

	public static Map<String, String> getAnnotatedDecoders() throws Exception {
		Map<String, String> annotatedDecoders = Maps.newHashMap();
		for (Entry<Annotation, String> entry : getAnnotatedClasses(DecoderType.class).entrySet()) {
			annotatedDecoders.put(((DecoderType) entry.getKey()).value(), entry.getValue());
		}
		return annotatedDecoders;
	}

	private static Map<Annotation, String> getAnnotatedClasses(Class<? extends Annotation> annotationClass)
			throws Exception {
		Map<Annotation, String> annotatedClasses = Maps.newHashMap();
		Reflections reflections = new Reflections(annotationClass.getPackage().getName() + ".impl");
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(annotationClass);
		for (Class<?> clazz : annotated) {
			Annotation annotation = clazz.getAnnotation(annotationClass);
			if (annotatedClasses.containsKey(annotation)) {
				throw new Exception(
						MessageFormat.format("Duplicated declaration of Annotation: {0}, classes = [{1}, {2}]",
								annotation, annotatedClasses.get(annotation), clazz.getName()));
			}
			annotatedClasses.put(annotation, clazz.getName());
		}
		return annotatedClasses;
	}

	public static Grok initGrok(String name, JSONObject patterns, String patternFile) throws Exception {
		Grok grok = new Grok();
		// load default patterns, which will be overriden by custom patterns
		grok.addPatternFromReader(
				new InputStreamReader(CommonUtils.class.getClassLoader().getResourceAsStream("default_patterns")));
		if (patternFile != null) {
			grok.addPatternFromFile(patternFile);
		}
		// grok_patterns优先级高于grok_patterns_file
		if (patterns != null) {
			for (Entry<String, Object> entry : patterns.entrySet()) {
				grok.addPattern(entry.getKey(), entry.getValue().toString());
			}
		}

		if (grok.getPatterns().isEmpty()) {
			throw new InvalidParameterException(
					MessageFormat.format("Either {0} or {1} is required!", GROK_PATTERNS_FILE, GROK_PATTERNS));
		}
		LOGGER.info("init Grok " + name + ", patterns = " + grok.getPatterns());
		grok.compile("%{" + name + "}", true);
		return grok;
	}

	public static JSONObject extract(JSONObject source, String field, boolean discardExisting, boolean preserveExisting,
                                     boolean appendPrefix, ExtractCallable callable) throws Exception {
		JSONObject result = discardExisting ? new JSONObject() : source;
		if (!source.containsKey(field)) {
			return result;
		}
		callable.setSource(source.getString(field));
		JSONObject value = callable.call();
		for (Entry<String, Object> entry : value.entrySet()) {
			if (preserveExisting && result.containsKey(entry.getKey())) {
				continue;
			} else {
				if (appendPrefix) {
					result.put(field + "." + entry.getKey(), entry.getValue());
				} else {
					result.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return result;
	}


//	/**
//	 * 把xml转换为Document对象<br/>
//	 * @author hero.li
//	 * @param xmlStr
//	 * @return
//	 * @throws DataFormatErrorException
//	 */
//	public static Document getDocFromXmlStr(String xmlStr) throws DataFormatErrorException {
//		try {
//			Document doc = SAX_READER.read(new ByteArrayInputStream(xmlStr.getBytes("UTF-8")));
//			return doc;
//		} catch (UnsupportedEncodingException e){
//			e.printStackTrace();
//			//正常情况下是不应该出现这种情况的
//			throw new IllegalArgumentException("字符编码异常!");
//		} catch (DocumentException e) {
//			e.printStackTrace();
//			throw new DataFormatErrorException("字符串不是标准的xml格式!");
//		}
//	}

	/**
	 * @author hero.li
	 * 递归获取Element元素对应的JSONObject<br/>
	 * @param e  Element元素实例 <br/>
	 * @return    e 对应的JSONObject实例 <br/>
	 */
	public static JSONObject getJSONObjectFromElement(Element e){
		if(null == e){
			throw new IllegalArgumentException("元素为null");
		}

		JSONObject result = new JSONObject();
		if(e.isTextOnly()){
			result.put(e.getName(),e.getData().toString());
			return result;
		}

		Iterator<Element> itr = e.elementIterator();
		while(itr.hasNext()){
			Element e1 = itr.next();
			boolean isLeaf = e1.isTextOnly();
			if(isLeaf){
				result.put(e1.getName(),e1.getData().toString());
			}else{
				JSONObject o1 = getJSONObjectFromElement(e1);
				result.put(e1.getName(),o1);
			}
		}
		return result;
	}

}
