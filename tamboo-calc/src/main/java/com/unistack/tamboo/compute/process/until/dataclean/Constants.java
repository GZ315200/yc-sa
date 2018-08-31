package com.unistack.tamboo.compute.process.until.dataclean;

/**
 * @author xiejing.kane
 *
 */
public class Constants {
	public static  String DECODER = "decoder";
	public static  String FILTERS = "filters";
	public static  String HANDLER = "handler";
	public static  String TYPE = "type";

	/* grok */
	public static  String GROK_PATTERNS = "grok_patterns";
	public static  String GROK_PATTERNS_FILE = "grok_patterns_file";
	public static  String GROK_ENTRY = "grok_entry";

	/* filter */
	public static  String FILTER_PARAMS = "params";
	public static  String DISCARD_RECORD_ON_ERROR = "discard_record_on_error";
	public static  boolean DEFAULT_DISCARD_RECORD_ON_ERROR = true;

	/* dynamic filter */
	public static  String CODE = "code";
	public static  String CODE_FILE = "code_file";
	public static  String IMPORT = "import";
	public  static String TEMPLATE_CLASS_NAME = "_Template_";
	public  static String TEMPLATE_METHOD_NAME = "_execute_";
	public  static String TEMPLATE_FILE = "dynamic_filter.template";

	/* response */
	public static  String RES_FAILURE = "#FAILURE#";
	public static  String RES_THROWABLE = "throwable";
	public static  String RES_SOURCE = "source";
}
