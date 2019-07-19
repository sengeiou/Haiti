package com.aimir.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import com.aimir.constants.CommonConstants.WeekDay;

public class CalendarUtil {
	
	public static String to2Digit(int str){
		DecimalFormat df = new DecimalFormat("00");
		return df.format(str);
	}
	public static String getCurrentDate(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
	    int year = cal.get(Calendar.YEAR);
	    int month = cal.get(Calendar.MONTH)+1;
	    int day = cal.get(Calendar.DAY_OF_MONTH);
	    
	    StringBuffer sb = new StringBuffer();
	    sb.append(Integer.toString(year));
	    sb.append(to2Digit(month));
	    sb.append(to2Digit(day));
	 
	    return sb.toString();
	}
	
	public static String getCurrentDateUsingFormat(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		
	    int year = cal.get(Calendar.YEAR);
	    int month = cal.get(Calendar.MONTH)+1;
	    int day = cal.get(Calendar.DAY_OF_MONTH);
	    
	    StringBuffer sb = new StringBuffer();
	    sb.append(Integer.toString(year));
	    sb.append("/");
	    sb.append(to2Digit(month));
	    sb.append("/");
	    sb.append(to2Digit(day));
	 
	    return sb.toString();
	}
	
	private static String getDate(Calendar cal){
	    int year = cal.get(Calendar.YEAR);
	    int month = cal.get(Calendar.MONTH)+1;
	    int day = cal.get(Calendar.DAY_OF_MONTH);
	    
	    StringBuffer sb = new StringBuffer();
	    sb.append(Integer.toString(year));
	    sb.append(to2Digit(month));
	    sb.append(to2Digit(day));
	    
	    return sb.toString();
	}
	
	private static String getDateUsingFormat(Calendar cal){
	    int year = cal.get(Calendar.YEAR);
	    int month = cal.get(Calendar.MONTH)+1;
	    int day = cal.get(Calendar.DAY_OF_MONTH);
	    
	    StringBuffer sb = new StringBuffer();
	    sb.append(Integer.toString(year));
	    sb.append("/");
	    sb.append(to2Digit(month));
	    sb.append("/");
	    sb.append(to2Digit(day));
	    
	    return sb.toString();
	}
	
	public static String getDateNotUsingFormat(Calendar cal){
	    int year = cal.get(Calendar.YEAR);
	    int month = cal.get(Calendar.MONTH)+1;
	    int day = cal.get(Calendar.DAY_OF_MONTH);
	    
	    StringBuffer sb = new StringBuffer();
	    sb.append(Integer.toString(year));
	    sb.append(to2Digit(month));
	    sb.append(to2Digit(day));
	    
	    return sb.toString();
	}
	
	/**
	 * 입력된날짜에 구분(년도,월,일)별로 날짜를 더하거나 뺀 결과를 조회한다.
	 * @param date
	 * @param field
	 * @param addVal
	 * @return
	 */
	public static String getDate(String date,int field,int addVal){
		int year = Integer.parseInt(date.substring(0,4));
	    int month = Integer.parseInt(date.substring(4,6))-1;
	    int day = Integer.parseInt(date.substring(6,8));
	    Calendar cal = Calendar.getInstance();
	    cal.set(year, month, day);
	    
	    cal.add(field, addVal);
	 
	    return getDate(cal);
	}
	
	/**
	 * 입력된날짜에 구분(년도,월,일)별로 날짜를 더하거나 뺀 결과를 조회한다.
	 * @param date
	 * @param field
	 * @param addVal
	 * @return
	 */
	public static String getDateUsingFormat(String date,int field,int addVal){
		int year = Integer.parseInt(date.substring(0,4));
	    int month = Integer.parseInt(date.substring(4,6))-1;
	    int day = Integer.parseInt(date.substring(6,8));
	    Calendar cal = Calendar.getInstance();
	    cal.set(year, month, day);
	    
	    cal.add(field, addVal);
	 
	    return getDateUsingFormat(cal);
	}
	
	/**
	 * 입력된날짜에 구분(년도,월,일)별로 날짜를 더하거나 뺀 결과를 조회한다.
	 * @param date
	 * @param field
	 * @param addVal
	 * @return
	 */
	public static String getDateWithoutFormat(String date,int field,int addVal){
		int year = Integer.parseInt(date.substring(0,4));
	    int month = Integer.parseInt(date.substring(4,6))-1;
	    int day = Integer.parseInt(date.substring(6,8));
	    Calendar cal = Calendar.getInstance();
	    cal.set(year, month, day);
	    
	    cal.add(field, addVal);
	 
	    return getDateNotUsingFormat(cal);
	}
	
	/**
	 * 년도를 입력받아서 개월수를 계산한다.
	 * 현재년월의 경우에만 현재월까지 조회하고 그렇지 않으면 12
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getMonthCountOfYear(String year){
		
		String monthCount="12";

		// 입력된 년,월이 현재년,월과 동일할경우 오늘날짜기준으로 월수를 구한다.
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		if(Integer.toString(cal.get(Calendar.YEAR)).equals(year)){
			monthCount = Integer.toString(cal.get(Calendar.MONTH)+1);
		}
		
		return monthCount;
	}
	
	/**
	 * 년도,월을 입력받아서 몇주까지 있는지 계산한다.
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getWeekCountOfMonth(String year,String month){
		
		//입력된 년,월의 마지막일자
		String lastDate =	getMonthLastDate(year, month);
		
		// 입력된 년,월이 현재년,월과 동일할경우 오늘날짜기준으로 주차수를 구한다.
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		if(Integer.toString(cal.get(Calendar.YEAR)).equals(year)&&Integer.toString(cal.get(Calendar.MONTH)+1).equals(month)){
			lastDate = Integer.toString(cal.get(Calendar.DATE));
		}
		
	    StringBuffer sb = new StringBuffer();
	    sb.append(year);
	    sb.append(to2Digit(Integer.parseInt(month)));
	    sb.append(to2Digit(Integer.parseInt(lastDate)));
		return Integer.toString(getWeekOfMonth(sb.toString()));
	}
	
	
	/**
	 * 입력된 연도,월,주차에 해당하는 시작일자,종료일자를 조회한다.
	 * @param inYear
	 * @param inMonth
	 * @param inWeek
	 * @return
	 */
	public static Map<String,String> getDateWeekOfMonth(String inYear,String inMonth,String inWeek){
		Map<String,String> map = new HashMap<String,String>();
		
		int year = Integer.parseInt(inYear);
	    int month = Integer.parseInt(inMonth)-1; 
	    int day = Integer.parseInt("1");
	    Calendar cal = Calendar.getInstance();
	    cal.set(year, month, day);
	    
	    int firstDate 	= 1; 
	    int lastDate	= cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	  
		//구하고픈 주차
		int week=Integer.parseInt(inWeek);
		
		for(int i=0;i<7;i++){
			cal.set(year,month,firstDate+i);
			if(cal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY){
				break;
			}
		}
		
		int firstSaturday = cal.get(Calendar.DAY_OF_MONTH);
//		int lastWeek = getWeekOfMonth(inYear+(Integer.parseInt(inMonth)<10?"0"+inMonth:inMonth)+Integer.toString(lastDate));
		int lastWeek = getWeekOfMonth(inYear + StringUtil.frontAppendNStr('0', inMonth, 2) + Integer.toString(lastDate));
		
		int startDate=0;
		int endDate=0;
		
		//현제 cal 객체는 첫주 마지막날인 토요일로 되어있음
		if(week == lastWeek){
			//마지막주차일경우
			//마지막 전주에서 1일을 더하여 시작일 계산
			cal.add(Calendar.WEEK_OF_MONTH, week-2);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			
			startDate 	= cal.get(Calendar.DAY_OF_MONTH);
			endDate		= lastDate;
			
		}else if(week == 1){
			//첫주차일경우
			startDate	= firstDate;
			endDate		= firstSaturday;
		}else{
			cal.add(Calendar.WEEK_OF_MONTH, week-1);
			endDate 	= cal.get(Calendar.DAY_OF_MONTH);
			
			cal.add(Calendar.DAY_OF_MONTH, -6);
			startDate = cal.get(Calendar.DAY_OF_MONTH);
		}
		
		String sDate="";
		String eDate="";
		
		inMonth = to2Digit(Integer.parseInt(inMonth));
		sDate = to2Digit(startDate);
		eDate = to2Digit(endDate);
		
		String currDate = getCurrentDate();
		
		if(Integer.parseInt(inYear+inMonth+eDate)>Integer.parseInt(currDate)){
			eDate = getCurrentDate().substring(6,8);
		}
		

		map.put("startDate", inYear+inMonth+sDate);
		map.put("endDate", inYear+inMonth+eDate);
		
		return map;
	}
	
	
	/**
	 * 입력된 년,월에 해당하는 시작일자 종료일자를 구한다.
	 * @param year
	 * @param month
	 * @return
	 */
	public static Map<String,String> getDateMonth(String year,String month){
		String lastDate = getMonthLastDate(year, month);
		
	    StringBuffer startDate = new StringBuffer();
	    startDate.append(year);
	    startDate.append(to2Digit(Integer.parseInt(month)));
	    startDate.append("01");
	    
	    StringBuffer endDate = new StringBuffer();
	    endDate.append(year);
	    endDate.append(to2Digit(Integer.parseInt(month)));
	    endDate.append(lastDate);
	    
		String currDate = getCurrentDate();
		if(Integer.parseInt(year+to2Digit(Integer.parseInt(month))+lastDate)>Integer.parseInt(currDate)){
			endDate.setLength(0);
			endDate.append(getCurrentDate());
		}
	    
	    Map<String,String> map = new HashMap<String,String>();
	    map.put("startDate", startDate.toString());
	    map.put("endDate", endDate.toString());
	    
	    return map;
	}
	
	public static Map<String, String> getDateFullMonth(String year, String month) {
		String lastDate = getMonthLastDate(year, month);
		
	    StringBuffer startDate = new StringBuffer();
	    startDate.append(year);
	    startDate.append(to2Digit(Integer.parseInt(month)));
	    startDate.append("01");
	    
	    StringBuffer endDate = new StringBuffer();
	    endDate.append(year);
	    endDate.append(to2Digit(Integer.parseInt(month)));
	    endDate.append(lastDate);
	    
	    Map<String,String> map = new HashMap<String,String>();
	    map.put("startDate", startDate.toString());
	    map.put("endDate", endDate.toString());
	    
	    return map;		
	}
	
	/**
	 * 입력된 연도,월,주차,요일에 해당하는 일자를 조회한다.
	 * @param inYear
	 * @param inMonth
	 * @param inWeek
	 * @return
	 */
	public static String getDateWeekDayOfWeek(String inYear,String inMonth,String inWeek,String inWeekDay){
		Map<String,String> map = getDateWeekOfMonth(inYear,inMonth,inWeek);
		
		String weekStartDate = (String)map.get("startDate");
		
		int syear = Integer.parseInt(weekStartDate.substring(0,4));
	    int smonth = Integer.parseInt(weekStartDate.substring(4,6))-1;
	    int sday = Integer.parseInt(weekStartDate.substring(6,8));
	    Calendar sCal = Calendar.getInstance();
	    sCal.set(syear, smonth, sday);
	    
	    for(int i=0;i<7;i++){
	    	if(sCal.get(Calendar.DAY_OF_WEEK)== Integer.parseInt(inWeekDay)){
	    		break;
	    	}else{
	    		sCal.add(Calendar.DAY_OF_MONTH, 1);
	    	}
	    }
	    
	    return getDateNotUsingFormat(sCal);
	    
	}
   
	/**
	 * 입력된 연도,월,주차에 해당하는 요일수를 구한다.
	 * @param inYear
	 * @param inMonth
	 * @param inWeek
	 * @return
	 */
	public static Map<String,String> getWeekDayOfWeek(String inYear,String inMonth,String inWeek){
		Map<String,String> map = getDateWeekOfMonth(inYear,inMonth,inWeek);
		
		String weekStartDate = (String)map.get("startDate");
		String weekEndDate = (String)map.get("endDate");
		
		
		int syear = Integer.parseInt(weekStartDate.substring(0,4));
	    int smonth = Integer.parseInt(weekStartDate.substring(4,6))-1;
	    int sday = Integer.parseInt(weekStartDate.substring(6,8));
	    Calendar sCal = Calendar.getInstance();
	    sCal.set(syear, smonth, sday);
	    
	    int eyear = Integer.parseInt(weekEndDate.substring(0,4));
	    int emonth = Integer.parseInt(weekEndDate.substring(4,6))-1;
	    int eday = Integer.parseInt(weekEndDate.substring(6,8));
	    Calendar eCal = Calendar.getInstance();
	    eCal.set(eyear, emonth, eday);
	    
	    int startWeek = sCal.get(Calendar.DAY_OF_WEEK);
	    int endWeek = eCal.get(Calendar.DAY_OF_WEEK);
	    
	    Map<String,String> resultMap = new HashMap<String,String>();
	    resultMap.put("startWeek", Integer.toString(startWeek));
	    resultMap.put("endWeek", Integer.toString(endWeek));
	    
	    return resultMap;
	}

	/**
	 * 입력한 날짜의 해당월의 마지막 날짜를 구한다.
	 */
	public static String getMonthLastDate(String inYear,String inMonth){
		int lastDate = 31;

		int year = Integer.parseInt(inYear);
	    int month = Integer.parseInt(inMonth)-1;
	    int day = Integer.parseInt("1");
	    Calendar cal = Calendar.getInstance();
	    cal.set(year, month, day);

	    lastDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

		return Integer.toString(lastDate);
	}
	
	
	/**
	 * 입력된 날자의 주차 구하기
	 * @param date
	 * @return
	 */
	public static int getWeekOfMonth(String date) {
	    int year = Integer.parseInt(date.substring(0,4));
	    int month = Integer.parseInt(date.substring(4,6))-1;
	    int day = Integer.parseInt(date.substring(6,8));
	    Calendar destDate = Calendar.getInstance();
	    destDate.set(year, month, day);
	    return destDate.get(Calendar.WEEK_OF_MONTH); 
	  }
	
	/**
	 * 입력된 년,월에 해당하는 분기의 시작일자 종료일자를 구한다.
	 * @param year
	 * @param month
	 * @param quarter : 2분기 = 6.0 , 3분기 = 4.0 , 4분기 = 3.0
	 * @return
	 */
	public static Map<String,String> getMonthQuarter(String year,String month,double quarter ){
		
		int quarterMonth = (int) Math.ceil( Integer.parseInt( month ) / quarter ); // 입력 받은 날이 속하는 분기
		
		int startMonth = (int) (quarter * ( quarterMonth - 1 ) ) + 1;
		int endMonth = (int) (quarter * quarterMonth);

		
		String lastDate = getMonthLastDate(year, to2Digit(endMonth));
		
	    StringBuffer startDate = new StringBuffer();
	    startDate.append(year);
	    startDate.append(to2Digit(startMonth));
	    startDate.append("01");
	    
	    StringBuffer endDate = new StringBuffer();
	    endDate.append(year);
	    endDate.append(to2Digit(endMonth));
	    endDate.append(lastDate);
	    
		String currDate = getCurrentDate();
		if(Integer.parseInt(year+to2Digit(Integer.parseInt(month))+lastDate)>Integer.parseInt(currDate)){
			endDate.setLength(0);
			endDate.append(getCurrentDate());
		}
	    
	    Map<String,String> map = new HashMap<String,String>();
	    map.put("startDate", startDate.toString());
	    map.put("endDate", endDate.toString());
	    
	    return map;
	}
	
	/**
	 * 날짜로 요일가져오기
	 * @param y
	 * @param m
	 * @param d
	 * @return
	 */
	public static String getWeekDay(String lang, int y,int m,int d) {
		
	    String[] dayOfWeek = null;
        if (lang.equals("ko"))
            dayOfWeek = new String[]{
                WeekDay.Sunday.getKorName(),
                WeekDay.Monday.getKorName(),
                WeekDay.Tuesday.getKorName(),
                WeekDay.Wednesday.getKorName(),
                WeekDay.Thursday.getKorName(),
                WeekDay.Friday.getKorName(),
                WeekDay.Saturday.getKorName()
                };
        else
            dayOfWeek = new String[]{
                WeekDay.Sunday.getEngName(),
                WeekDay.Monday.getEngName(),
                WeekDay.Tuesday.getEngName(),
                WeekDay.Wednesday.getEngName(),
                WeekDay.Thursday.getEngName(),
                WeekDay.Friday.getEngName(),
                WeekDay.Saturday.getEngName()
        };
        Calendar cal=new GregorianCalendar();
        cal.set(Calendar.YEAR, y);
        cal.set(Calendar.MONTH, m-1);
        cal.set(Calendar.DAY_OF_MONTH, d);

        String weekday=dayOfWeek[cal.get(Calendar.DAY_OF_WEEK)-1];
        
        return weekday;
	 }
	
	/**
	 * method name : getDatetimeString
	 * method Desc : 지정한 Date를 지정한 포멧으로 날짜문자열을 반환한다.
	 *
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getDatetimeString(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
}
