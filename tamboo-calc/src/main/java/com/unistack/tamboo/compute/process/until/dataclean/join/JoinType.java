package com.unistack.tamboo.compute.process.until.dataclean.join;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JoinType {
    String value() default "";
}

