package com.unistack.tamboo.compute.exception;

/**
 * @author xiejing.kane
 *
 */
public class InvalidParameterException extends Exception {
	/**
	 * 
	 */
	private static  long serialVersionUID = -6689105824330447208L;

	public InvalidParameterException() {
		super();
	}

	public InvalidParameterException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidParameterException(String message) {
		super(message);
	}

	public InvalidParameterException(Throwable cause) {
		super(cause);
	}
}
