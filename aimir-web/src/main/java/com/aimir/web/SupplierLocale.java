package com.aimir.web;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.WebUtils;

/**
 * aimir-web 을 이용하는 사용자의 세션에 Locale 정보를 설정하는 클래스.
 * Spring Framework의 LocaleResolver 를 이용한다.
 * 
 * @author yuky
 *
 */
public class SupplierLocale {

	/**
	 * Language, Country 정보를 가지고 Session 의 Locale 정보를 변경한다.
	 * 변경된 Locale 정보에 따라 출력 메시지가 변경된다.
	 * 
	 * @param request 
	 * @param lang ISO 639-2 Code,  국제 언어 부호표
	 * @param country ISO 3166-1-alpha-2 code, 국제 국가별 코드표
	 */
	public static void setSessionLocale(HttpServletRequest request, String lang, String country){
		
		WebUtils.setSessionAttribute(
				request, 
				"org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE", 
				new Locale(lang, country));
	}
	
	/**
	 * <code>Locale</code> 정보를 Session 에 설정한다.
	 * 변경된 Locale 정보에 따라 출력 메시지가 변경된다.
	 * 
	 * @param request
	 * @param locale 자바 Locale 인스턴스
	 */
	public static void setSessionLocale(HttpServletRequest request, Locale locale){

		WebUtils.setSessionAttribute(
				request, 
				"org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE", 
				locale);
	}
	
	/**
	 * 세션에 저장되어 있는 <code>Locale</code> 정보를 리턴한다.
	 */
	public static Locale getSessionLocale(HttpServletRequest request){
		return (Locale)WebUtils.getSessionAttribute(request, 
				"org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE");
	}
	
}
