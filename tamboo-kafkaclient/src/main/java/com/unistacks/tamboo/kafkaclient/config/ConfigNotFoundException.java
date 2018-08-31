package com.unistacks.tamboo.kafkaclient.config;

@Deprecated
public class ConfigNotFoundException extends Exception {
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
