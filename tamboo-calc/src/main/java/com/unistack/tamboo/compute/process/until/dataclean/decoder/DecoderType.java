package com.unistack.tamboo.compute.process.until.dataclean.decoder;

import java.lang.annotation.*;

/**
 * @author xiejing.kane
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecoderType {
	String value() default "";
}
