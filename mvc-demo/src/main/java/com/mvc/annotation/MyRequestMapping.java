package com.mvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestMapping {
	String name() default "";
	String value() default "";
	String path() default "";
	String[] method() default {};
	String[] params() default {};
	String[] headers() default {};
	String[] consumes() default {};
	String[] produces() default {};
}
