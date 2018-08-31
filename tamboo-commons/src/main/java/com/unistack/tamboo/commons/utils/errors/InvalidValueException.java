package com.unistack.tamboo.commons.utils.errors;
/**
 * 无效值异常 
 *
 */
public class InvalidValueException extends RuntimeException {

	private static  long serialVersionUID = 6473000620584069678L;

	public InvalidValueException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidValueException(String message) {
		super(message);
	}

	public InvalidValueException(Throwable cause) {
		super(cause);
	}

	public InvalidValueException() {
		super();
	}
}
