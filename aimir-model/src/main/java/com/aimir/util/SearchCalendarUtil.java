package com.aimir.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SearchCalendarUtil {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	/**
	 * @Method Name : getWeekDayToDateList
	 * @Date        : 2010. 3. 29.
	 * @Method 설명    : 조회년월과 월의 주차, 요일을 가져와서 조회하고자 하는 일자를 리턴(요일별조회)
	 * @param args
	 *  String stdYearMonth : 조회년월(YYYYMM)
	 *  String weekNum      : 조회월의 요일(1~5)
	 *  String weekDay      : 조회하고자 하는 요일(0: 일요일, 1:월요일~ 6:토요일)
	 * @return yyyymmdd
	 */
	public String getWeekDayToDateList(String stdYearMonth, String weekNum, String weekDay) {
		String result ="";
		
		Calendar cld = Calendar.getInstance();
		
		// Calendar객체에 시작일자 세잍
		int yyyy = Integer.parseInt(stdYearMonth.substring(0, 4));
		cld.set(Calendar.YEAR, yyyy);
		int mm = Integer.parseInt(stdYearMonth.substring(4, 6));
		cld.set(Calendar.MONTH, mm-1);
		int dd = (7 * Integer.parseInt(weekNum)) -6;
		cld.set(Calendar.DAY_OF_MONTH, dd);
		
		int inputWeekDay = cld.get(Calendar.DAY_OF_WEEK)-1;//계산된 일자의 주
		
		cld.add(Calendar.DAY_OF_MONTH, (Integer.parseInt(weekDay) -inputWeekDay) );
		result = sdf.format(cld.getTime());
		
		return result;
		
	}
	
	
	/**
	 * @Method Name : getWeekNumToDateList
	 * @Date        : 2010. 3. 29.
	 * @Method 설명    : 조회월과 월의 주차를 이용해서 조회하고자하는 기간(일요일~토요일)을 추출한다.(주별조회)
	 * @param args
	 *  String stdYyMonth : 조회년월(YYYYMM)
	 *  String weekNum    : 조회하고자하는 주
	 * @return yyyymmdd list
	 * history 		: 해당월의 일자만 나오도록 수정
	 */
	public List<String> getWeekNumToDateList(String stdYyMonth, String weekNum) {
		List<String> result = new ArrayList<String>();
		
		int compWeekNum = Integer.parseInt(weekNum); 
		
		Calendar cld = Calendar.getInstance();
		int totWeekNum = getDateTotalWeekNum(stdYyMonth);
		
		// Calendar객체에 시작일자 세팅
		int yyyy = Integer.parseInt(stdYyMonth.substring(0, 4));
		cld.set(Calendar.YEAR, yyyy);
		int mm = Integer.parseInt(stdYyMonth.substring(4, 6));
		cld.set(Calendar.MONTH, mm-1);
		int dd = (7 * Integer.parseInt(weekNum)) -6;
		cld.set(Calendar.DAY_OF_MONTH, dd);
		
		int firstWeekDay =0;//그 주에 해당하는 일요일
		int inputWeekDay = cld.get(Calendar.DAY_OF_WEEK)-1;//계산된 일자의 주
		
		cld.add(Calendar.DAY_OF_MONTH, firstWeekDay -inputWeekDay );
		if(compWeekNum == 1) { // 첫재주일때
			result.add(stdYyMonth+"01");
		}
		else {
			result.add(sdf.format(cld.getTime()));
		}
		
		cld.add(Calendar.DAY_OF_MONTH, 6 );
		if(compWeekNum == totWeekNum ) {// 해당주가 마지막 주일때
			result.add(stdYyMonth+"31");
		}
		else {
			result.add(sdf.format(cld.getTime()));
		}
		
		return result;
		
	}
	
	
	
	/**
	 * @Method Name : getWeekNumToDateList
	 * @Date        : 2010. 3. 29.
	 * @Method 설명    : 해당일의 요일을 추출한다.(요일별조회)
	 * @param args
	 *  String stdYyMonth : 조회년월일(stdYyMmDd)
	 * @return int (1:일요일, 2:월요일 ~ 7:토요일)
	 */
	public int getDateTodayWeekNum(String stdYyMmDd) {
		int result = 1;//일요일
		
		Calendar cld = Calendar.getInstance();
		
		// Calendar객체에 시작일자 세팅
		int yyyy = Integer.parseInt(stdYyMmDd.substring(0, 4));
		cld.set(Calendar.YEAR, yyyy);
		int mm = Integer.parseInt(stdYyMmDd.substring(4, 6));
		cld.set(Calendar.MONTH, mm-1);
		int dd = Integer.parseInt(stdYyMmDd.substring(6, 8));;
		cld.set(Calendar.DAY_OF_MONTH, dd);
		
		result = cld.get(Calendar.DAY_OF_WEEK);// 1:일요일, 2:월요일 ~ 3:토요일
		//System.out.println("입력일자가 현재 무슨 요일인지?["+inputWeekDay+"]");
		
		return result;
		
	}
	
	/**
	 * @Method Name : getWeekNumToDateList
	 * @Date        : 2010. 3. 29.
	 * @Method 설명    : 해당월의 총 주차수를 구함
	 * @param args
	 *  String stdYyMonth : 조회년월(stdYyMm)
	 * @return int 해당월의 주차수
	 */
	public int getDateTotalWeekNum(String stdYyMm) {
		int result = 0;//0주
		
		Calendar cld = Calendar.getInstance();
		
		// Calendar객체에 시작일자 세팅
		int yyyy = Integer.parseInt(stdYyMm.substring(0, 4));
		cld.set(Calendar.YEAR, yyyy);
		int mm = Integer.parseInt(stdYyMm.substring(4, 6));
		cld.set(Calendar.MONTH, mm-1);
		cld.set(Calendar.DAY_OF_MONTH, 1);
		
		cld.set(Calendar.DAY_OF_MONTH, cld.getActualMaximum ( Calendar.DAY_OF_MONTH ));
		
		result = cld.get(Calendar.WEEK_OF_MONTH);
		
		return result;
		
	}
	
	
	/**
	 * @Method Name : getMonthToBeginDateEndDate
	 * @Date        : 2010. 3. 29.
	 * @Method 설명    : 해당월의 주차별 조회 시작일자, 종료일자를 구함
	 * @param args
	 *  String stdYyMonth : 조회년월(stdYyMm)
	 * @return List
	 */
	public List<String> getMonthToBeginDateEndDate(String stdYyMm) {
		
		
		List<String> result = new ArrayList<String>();
		
		Calendar firstCld = Calendar.getInstance();
		Calendar lastCld = Calendar.getInstance();
		
		String beginDate ="";
		String endDate ="";
		
		
		// Calendar객체에 시작일자 세팅
		int yyyy = Integer.parseInt(stdYyMm.substring(0, 4));
		int mm = Integer.parseInt(stdYyMm.substring(4, 6));
		
		
		firstCld.set(Calendar.YEAR, yyyy);
		firstCld.set(Calendar.MONTH, mm-1);
		firstCld.set(Calendar.DAY_OF_MONTH, 1);
		
		lastCld.set(Calendar.YEAR, yyyy);
		lastCld.set(Calendar.MONTH, mm-1);
		lastCld.set(Calendar.DAY_OF_MONTH, 1);

		int firstDayWeekDay = firstCld.get(Calendar.DAY_OF_WEEK);// 1일의 요일
		String firstDay = sdf.format(firstCld.getTime());
		
		lastCld.set(Calendar.DAY_OF_MONTH, lastCld.getActualMaximum ( Calendar.DAY_OF_MONTH ));
		String lastDay = sdf.format(lastCld.getTime());
		int lastDayWeekDay = lastCld.get(Calendar.DAY_OF_WEEK);// 마지막일
		
		
		int totNum = lastCld.get(Calendar.WEEK_OF_MONTH);//해당월의 총 주차수
		
		for(int i=0;i<totNum;i++) {
			
			if(i == 0) { // 첫주에서 시작일 처리
				beginDate =firstDay;
				firstCld.add(Calendar.DAY_OF_MONTH, 7 -firstDayWeekDay );  //1일이 속한 토요일
				endDate = sdf.format(firstCld.getTime());
				
			} else if(i == (totNum -1)) {
				lastCld.add(Calendar.DAY_OF_MONTH, -lastDayWeekDay +1 );  //마지막일이 속한 일요일
				beginDate = sdf.format(lastCld.getTime());
				endDate = lastDay;
			} else {
				firstCld.add(Calendar.DAY_OF_MONTH, 1);
				
				beginDate=sdf.format(firstCld.getTime());
				firstCld.add(Calendar.DAY_OF_MONTH, 6 );
				
				endDate=sdf.format(firstCld.getTime());
			}
			result.add((String)beginDate+endDate);		
		}		
		return result;		
	}	
	
	/**
	 * @Method Name : getWeekNumToDateList
	 * @Date        : 2010. 3. 29.
	 * @Method 설명    : 조회년도와 Season을 가져와서 해당기간에 대한 시작일과 종료일을 구한다(Season별 조회)
	 * @param args
	 *  String stdYyMonth : 조회년월(YYYYMM)
	 *  String weekNum    : 조회하고자하는 주
	 * @return yyyymm list
	 */
	public List<String> getSeasonToDateList(String stdYyyy, String stdSeason) {
		List<String> result = new ArrayList<String>();
		//Calendar cld = Calendar.getInstance();
		
		//미구현
		
		return result;
	}
	
	/**
	 * @Method 설명 : 종료일에서 시작일을 뺀 일수 구해오기
	 * @param startDt
	 * @param endDt
	 * @return
	 */
	public static int getDayDiff(String fromDate, String toDate) {	  
	        
		if(fromDate.length() < 8) return -1;
		if(toDate.length() < 8) return -1;
		
		int year1  = Integer.parseInt(fromDate.substring(0,4));
		int month1 = Integer.parseInt(fromDate.substring(4,6)) - 1;
		int day1   = Integer.parseInt(fromDate.substring(6,8));
		
		int year2  = Integer.parseInt(toDate.substring(0,4));
		int month2 = Integer.parseInt(toDate.substring(4,6)) - 1;
		int day2   = Integer.parseInt(toDate.substring(6,8));
		
		Calendar c1 = Calendar.getInstance(); 
		Calendar c2 = Calendar.getInstance(); 
		
		c1.set(year1, month1, day1); 
		c2.set(year2, month2, day2); 
		
		long d1 = c1.getTime().getTime();
		long d2 = c2.getTime().getTime();
		int days =(int)((d2-d1)/(1000*60*60*24)); 
		
		return days; 
	}
}
