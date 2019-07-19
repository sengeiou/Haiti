package com.aimir.service.mvm.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MnmCalendarUtil {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	

	/**
	 * @Method Name : getWeekDayToDateList
	 * @Date        : 2010. 3. 29.
	 * @Method 설명    : 조회시작일자를 구해서 조회종료일자안의 선택된 요일에 대한 일자들을 구한다.(요일별조회시 사용)
	 * @param args
	 *  String beginDate : 조회시작일자
	 *  String endDate   : 조회종료일자
	 *  String weekDay   : 조회하고자 하는 요일(0: 일요일, 1:월요일~ 6:토요일)
	 */
	public List<String> getWeekDayToDateList(String beginDate, String endDate, String weekDay) {
		//Calendar cld = GregorianCalendar.getInstance();
		List<String> result = new ArrayList<String>();
		
		Calendar cld = Calendar.getInstance();
		
		int inputWeekDay = Integer.parseInt(weekDay); // 조회조건의 요일
		
		// Calendar객체에 시작일자 세잍
		int yyyy = Integer.parseInt(beginDate.substring(0, 4));
		cld.set(Calendar.YEAR, yyyy);
		int mm = Integer.parseInt(beginDate.substring(4, 6));
		cld.set(Calendar.MONTH, mm-1);
		int dd = Integer.parseInt(beginDate.substring(6, 8));
		cld.set(Calendar.DAY_OF_MONTH, dd);
		// 시작일자의 요일		
		int beginWeekDay = cld.get(Calendar.DAY_OF_WEEK)-1;// 0:일요일, 1:월요일 ~ 6:토요일
		
		int addDay = 0;
		if(beginWeekDay < inputWeekDay) { 
			// 입력된 요일이 조회시작일자의 요일보다 크다면 조회시작일다음에 나오는 해당요일의 일자를 찾음
			// 예 입력된 요일은 수요일인데 현재 일자에 해당하는 요일은 월요일이라면 2일의 차이를 더한 일자를 기준으로  7일씩 더해서 일자 추출 
			addDay = (inputWeekDay - beginWeekDay) ; 
			cld.add(Calendar.DAY_OF_MONTH, addDay);
		}
		else if(beginWeekDay > inputWeekDay) { // 입력된 요일이 조회시작일보다 작다면   차이에서 +7일 해줌
			addDay = (inputWeekDay - beginWeekDay) +7;
			cld.add(Calendar.DAY_OF_MONTH, addDay);
		}
		else {//입력된 요일과 조회시작일자의 요일이 동일하면 조회시작일자에서 7일씩 더해서 해당 요일에 해당하는 일자들을 구함
			cld.add(Calendar.DAY_OF_MONTH, addDay);// 0을 더함
		}
		
		
		int idx = Integer.parseInt(sdf.format(cld.getTime()));
		int intEndDate = Integer.parseInt(endDate);
		for (; idx <= intEndDate;) {
			result.add(sdf.format(cld.getTime()));
			cld.add(Calendar.DAY_OF_MONTH, 7);
			idx = Integer.parseInt(sdf.format(cld.getTime()));
			
		}
		return result;
		
	}
	
	
	/**
	 * @Method Name : getDateToWeekDateList
	 * @Date        : 2010. 3. 29.
	 * @Method 설명    : 조회시작일자를 구해서 해당주의 일요일에 해당하는 일자와 토요일에 해당하는 일자를 구함(주별조회)
	 * @param args
	 *  String beginDate : 조회시작일자
	 *  String endDate   : 조회종료일자
	 *  String weekDay   : 조회하고자 하는 요일(0: 일요일, 1:월요일~ 6:토요일)
	 */
	public List<String> getDateToWeekDateList(String strDate) {
		List<String> result = new ArrayList<String>();
		Calendar cld = Calendar.getInstance();
		cld.get(Calendar.WEEK_OF_YEAR);
		
		int firstWeekDay =0;
		int inputWeekDay =0;
		
		String beginDate ="";
		String endDate ="";
		strDate="20100301";
		
		
		// Calendar객체에 일자세팅
		int yyyy = Integer.parseInt(strDate.substring(0, 4));
		cld.set(Calendar.YEAR, yyyy);
		int mm = Integer.parseInt(strDate.substring(4, 6));
		cld.set(Calendar.MONTH, mm-1);
		int dd = Integer.parseInt(strDate.substring(6, 8));
		cld.set(Calendar.DAY_OF_MONTH, dd);
		
		
		inputWeekDay = cld.get(Calendar.DAY_OF_WEEK)-1;// 0:일요일, 1:월요일 ~ 6:토요일
		
		// 해당일자가 포함된 주의 일요일 데이터
		cld.add(Calendar.DAY_OF_MONTH, firstWeekDay -inputWeekDay );
		beginDate=sdf.format(cld.getTime());
		
		//해당일자 포함된 주의 토요일데이터
		cld.add(Calendar.DAY_OF_MONTH, 6 );// 일요일로 변경된 일자를 토요일로 변경
		endDate=sdf.format(cld.getTime());
		
		result.add(beginDate);//조회시작일
		result.add(endDate);//조회종료일
		return result;
		
	}
	
	/**
	 * @Method Name : getWeekDayToDateList
	 * @Date        : 2010. 3. 29.
	 * @Method 설명    : 조회월과 월의 주차를 이용해서 조회하고자하는 기간(일요일~토요일)을 추출한다.(주별조회)
	 * @param args
	 *  String stdYyMonth : 조회년월(YYYYMM)
	 *  Int weekNum       : 조회하고자하는 주
	 */
	public List<String> getWeekNumToDateList(String stdYyMonth, int weekNum) {
		List<String> result = new ArrayList<String>();
		
		Calendar cld = Calendar.getInstance();
		
		// Calendar객체에 시작일자 세잍
		int yyyy = Integer.parseInt(stdYyMonth.substring(0, 4));
		cld.set(Calendar.YEAR, yyyy);
		int mm = Integer.parseInt(stdYyMonth.substring(4, 6));
		cld.set(Calendar.MONTH, mm-1);
		int dd = (7 * weekNum) -6;
		cld.set(Calendar.DAY_OF_MONTH, dd);
		
		int firstWeekDay =0;//그 주에 해당하는 일요일
		int inputWeekDay = cld.get(Calendar.DAY_OF_WEEK)-1;//계산된 일자의 주
		
		cld.add(Calendar.DAY_OF_MONTH, firstWeekDay -inputWeekDay );
		result.add(sdf.format(cld.getTime()));
		
		cld.add(Calendar.DAY_OF_MONTH, 6 );
		result.add(sdf.format(cld.getTime()));
		
		return result;
		
	}
	
	
}
