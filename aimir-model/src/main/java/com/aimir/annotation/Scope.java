package com.aimir.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

@Documented
@Target({}) @Retention(RUNTIME)
public @interface Scope {
	
    /*
     * 해당 ANNOTATION은 UI에서의 데이터접근에 대한 정의와 장비쪽과의 연계에 대한 예이다.
     * 1. UI에서 신규 데이터 생성시 값을 넣어 줄 수 있다.
     *   A. UI에서 값을 조회, 수정, 삭제(초기화)가 가능  
     *     create=true, read=true, update=true, delete=true
     *   B. UI에서 값을 조회, 수정 이 가능 (DEFAULT)
     *     create=true, read=true, update=true, delete=false
     *   C. UI에서 값을 조회만 가능
     *     create=false, read=true, update=false, delete=false
     *     
     * 2. 시스템에서 데이터를 관리하는 경우
     *   A. UI에서 조회만 가능한 경우
     *     create=false, read=true, update=false, delete=false
     *   B. UI에서 접근할 필요가 없는 경우
     *     create=false, read=false, update=false, delete=false 
     */
	boolean create() default false;    // UI에서 신규 등록시 들어가야할 INPUT FIELD가 필요
	boolean read() default false;  // UI 에서데이터를 보여줄 수 있다. 
	boolean update() default false;    // UI 에서 데이터를 수정 가능
	boolean delete() default false;    // UI 에서 데이터를 삭제 가능

	boolean devicecontrol() default false; // 장비쪽에 명령을 내려 데이터를 넣을 수 있음
}