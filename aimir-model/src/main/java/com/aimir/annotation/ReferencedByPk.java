package com.aimir.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Data 객체 참조 필드명 지정 (기초데이터 입력시)
 * @author YeonKyoung Park(goodjob)
 * 
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ReferencedByPk {

	String name() default "";
	String descr() default "";
}

