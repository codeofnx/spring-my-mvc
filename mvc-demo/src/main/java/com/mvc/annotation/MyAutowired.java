package com.mvc.annotation;

public @interface MyAutowired {
	boolean required() default true;
}
