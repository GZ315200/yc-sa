package com.unistack.tamboo.commons.utils;

import java.io.Serializable;

public  class Boolean implements Serializable, Comparable<Boolean> {
	public static  Boolean TRUE = new Boolean(true);

	public static  Boolean FALSE = new Boolean(false);

	private boolean value;
	private static  long serialVersionUID = -3665804199014368530L;

	public Boolean(boolean paramBoolean) {
		this.value = paramBoolean;
	}

	public Boolean(String paramString) {
		this(toBoolean(paramString));
	}

	public static boolean parseBoolean(String paramString) {
		return toBoolean(paramString);
	}

	public boolean booleanValue() {
		return this.value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public static Boolean valueOf(boolean paramBoolean) {
		return ((paramBoolean) ? TRUE : FALSE);
	}

	public static Boolean valueOf(String paramString) {
		return ((toBoolean(paramString)) ? TRUE : FALSE);
	}

	public static String toString(boolean paramBoolean) {
		return ((paramBoolean) ? "true" : "false");
	}

	public String toString() {
		return ((this.value) ? "true" : "false");
	}

	public int hashCode() {
		return ((this.value) ? 1231 : 1237);
	}

	public boolean equals(Object paramObject) {
		if (paramObject instanceof Boolean) {
			return (this.value == ((Boolean) paramObject).booleanValue());
		}
		return false;
	}

	public static boolean getBoolean(String paramString) {
		boolean bool = false;
		try {
			bool = toBoolean(System.getProperty(paramString));
		} catch (IllegalArgumentException localIllegalArgumentException) {
		} catch (NullPointerException localNullPointerException) {
		}
		return bool;
	}

	public int compareTo(Boolean paramBoolean) {
		return compare(this.value, paramBoolean.value);
	}

	public static int compare(boolean paramBoolean1, boolean paramBoolean2) {
		return ((paramBoolean1) ? 1 : (paramBoolean1 == paramBoolean2) ? 0 : -1);
	}

	private static boolean toBoolean(String paramString) {
		return ((paramString != null) && (paramString.equalsIgnoreCase("true")));
	}
}
