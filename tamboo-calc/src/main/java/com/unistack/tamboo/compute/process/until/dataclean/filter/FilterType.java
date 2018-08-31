package com.unistack.tamboo.compute.process.until.dataclean.filter;

import java.lang.annotation.*;

/**
 * @author xiejing.kane
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FilterType {
	String value() default "";
}
