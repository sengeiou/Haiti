package com.aimir.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.model.system.Supplier;

public class ParameterConverter {

	/**
	 * 페이징 데이터가 start ~ limit 형식일 경우, page, amount 형식으로 바꾼다.
	 * 반환은 int 배열
	 * 
	 * @param start
	 * @param limit
	 * @return
	 */
	public static int [] adjustPagingParameter(int start, int limit) {
		limit = (limit > 1) ? limit : 10;
		int page = 1;
		try { 
			page = (start > 0) ? ((int)(start/limit)) + 1 : 1;
		}
		catch (Exception e) { 
			page = 1; 
		}
		return new int[]{ page, limit };
	}
	
	/**
	 * LocaleDate 형식의 문자열을 DB 형식의 로우 문자열로 바꾼다.
	 * 
	 * @param supplier
	 * @param localeDate
	 * @return
	 */
	public static String convertDBDate(Supplier supplier, String localeDate) {
		String country = supplier.getCountry().getCode_2letter();
		String lang    = supplier.getLang().getCode_2letter();
		String ret = "";
		try {
			if(localeDate != null && !"".equals(localeDate.trim()))
				ret = TimeLocaleUtil.getDBDate(localeDate, 8, lang, country);
		} 
		catch (Exception e) {
			ret = CalendarUtil.getCurrentDate();
		}	
		return ret;
	}
	
	/**
	 * rawDate 형식의 문자열을 지역화 문자열로 바꾼다.
	 * 
	 * @param supplier
	 * @param localeDate
	 * @return
	 */
	public static String convertLocaleDate(Supplier supplier, String localeDate) {
		String country = supplier.getCountry().getCode_2letter();
        String lang    = supplier.getLang().getCode_2letter();
        return TimeLocaleUtil.getLocaleDate(localeDate, lang, country);
	}
	
	/**
	 * yyyymmddhhmmss@yyyymmddhhmmss 형식의 문자열을 {시작일, 종료일} 의 문자열 배열로 변환한다
	 * null 이거나 부정확한 문자열이면 주어진 기본값으로 초기화한다.
	 * 
	 * @param searchDate
	 * @param type
	 * @param past
	 * @param pastType
	 * @return
	 */
	public static String [] convertAtSignStringToDate(String searchDate, DateType type, int past, DateType pastType) {
		if(searchDate == null || searchDate.isEmpty() || !searchDate.contains("@") || searchDate.length() < 2) {
			try {
				Calendar now = Calendar.getInstance();
				String edate = TimeUtil.getFormatTime(now);
				int dtype = Calendar.DATE;
				pastType = (pastType == null) ? DateType.DAILY : pastType;
				
				switch (pastType) {
					case HOURLY: {
						dtype = Calendar.HOUR;
						break;
					}
					case DAILY: {
						dtype = Calendar.DATE;
						break;
					}
					case MONTHLY: {
						dtype = Calendar.MONTH;
						break;
					}
					case WEEKLY: {
						dtype = Calendar.YEAR;
						break;
					}					
				}
				now.set(dtype, now.get(dtype) - past);
				String sdate = TimeUtil.getFormatTime(now);
				searchDate = sdate + "@" + edate;
			}
			catch (ParseException cannot) {
				searchDate = "1970010100@1970010123";
			}            
		}
		return convertAtSignStringToDate(searchDate, type);
	}
	
	/**
	 * yyyymmddhhmmss@yyyymmddhhmmss 형식의 문자열을 {시작일, 종료일} 의 문자열 배열로 변환한다
	 * null 이거나 부정확한 문자열이면 주어진 기본값으로 초기화한다.
	 * 
	 * @param searchDate
	 * @param type
	 * @param past
	 * @param pastType
	 * @return
	 */
	public static String[] convertAtSignStringToDate(String searchDate, DateType type) {
		
		String sdate = ""; 
		String edate = ""; 
		if(searchDate != null && searchDate.contains("@")) {
			String [] dates = searchDate.split("@");
			if(dates.length == 2) {
				sdate = dates[0];
				edate = dates[1];
			}
			else {
				sdate = (dates[0] != null) ? dates[0] : "1970010100";
				edate = (dates[0] != null) ? dates[0] : "1970010123";
			}
		}
		else {
			try {
				sdate = TimeUtil.getCurrentDay() + "00";
				edate = TimeUtil.getCurrentDay() + "23";
			}
			catch (ParseException cannot) {
				sdate = "1970010100";
				edate = "1970010123";
			}            
		}
		
		int maxLength = 8;
		
		switch(type) {
			case HOURLY: {
				return new String[]{ sdate, edate };
			}
	        case DAILY: case WEEKLY: case MONTHLY: case WEEKDAILY: {
	        	maxLength = 8;
	            break;
	        }
	        case SEASONAL: case YEARLY: {
	        	maxLength = 6;
	            break;
	        }
	    }
		return 
			new String[]{ sdate.substring(0, maxLength), edate.substring(0, maxLength) };
	}
	
	/*형식변환 method 찾을시 삭제할예정.*/
	public static String[] getCovertSearchDateToString(String searchDate){
		
		String [] dates = searchDate.split(". ");


		int year = Integer.parseInt("20"+dates[0]);
		int month = Integer.parseInt(dates[1]);
		int day = Integer.parseInt(dates[2]);
		
		Calendar searchNow = Calendar.getInstance();
		searchNow.set(year, (month-1),day);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String meteringTimedate = formatter.format(searchNow.getTime());


		 return new String[]{ meteringTimedate, meteringTimedate };
	}
}
