package com.unistack.tamboo.commons.utils.errors;
/**
 * 配置文件找不到异常 
 *
 */
public class ConfigNotFoundException extends RuntimeException {
	private static  long serialVersionUID = -3199832436338918089L;

	public ConfigNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigNotFoundException(String message) {
		super(message);
	}

	public ConfigNotFoundException(Throwable cause) {
		super(cause);
	}

	public ConfigNotFoundException() {
		super();
	}

}
