package com.aimir.service.system.impl;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.jws.WebService;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.dao.mvm.MonthWMDao;
import com.aimir.dao.system.AverageUsageBaseDao;
import com.aimir.dao.system.AverageUsageDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.EnergySavingGoalDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.model.device.Meter;
import com.aimir.model.system.AverageUsage;
import com.aimir.model.system.AverageUsageBase;
import com.aimir.model.system.Code;
import com.aimir.model.system.EnergySavingGoal;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.EnergySavingGoalManager;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

@WebService(endpointInterface = "com.aimir.service.system.EnergySavingGoalManager")
@Service(value="energySavingGoalManager")
@Transactional
public class EnergySavingGoalManagerImpl implements EnergySavingGoalManager {

    Log logger = LogFactory.getLog(EnergySavingGoalManagerImpl.class);

	@Autowired
	LocationDao locationDao;
	
	@Autowired
	EnergySavingGoalDao energySavingGoalDao;
	
	@Autowired
	MeterDao meterDao;
	
	@Autowired
	CodeDao codeDao;
	
	@Autowired
	DayEMDao dayEMDao;
	@Autowired
	MonthEMDao monthEMDao;

	@Autowired
	DayGMDao dayGMDao;
	@Autowired
	MonthGMDao monthGMDao;
	
	@Autowired
	DayWMDao dayWMDao;
	
	@Autowired
	MonthWMDao monthWMDao;
	
	@Autowired
	AverageUsageDao averageUsageDao;
	
	@Autowired
	AverageUsageBaseDao averageUsageBaseDao;
	
	public Map<String, Object> setEnergySavingGoal(Map<String, Object> params) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {
			
			// 사용가능한 평균값 정보가 있는지 조회하여 목표사용량 등록 가능여부를 판단.
			AverageUsage au = averageUsageDao.getAverageUsageByUsed();
			
			if( au != null ){
				System.out.println(">>>>>>>>>>>>>>>>>>>>> " + (String)params.get("supplierId"));
				System.out.println(">>>>>>>>>>>>>>>>>>>>> " + (String)params.get("savingGoal"));
				System.out.println(">>>>>>>>>>>>>>>>>>>>> " + (String)params.get("savingGoalStartDate"));
				
				String supplierId   		= StringUtils.defaultIfEmpty( (String)params.get("supplierId") , "0" );
				String savingGoal   		= StringUtils.defaultIfEmpty( (String)params.get("savingGoal") , "0" ); // 절감목표량
				String savingGoalStartDate  = StringUtils.defaultIfEmpty( (String)params.get("savingGoalStartDate") , "" ); // 기준일
				
				SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMdd");
				String savingGoalStartDateS = outFormat.format(inFormat.parse(savingGoalStartDate));
				
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				Calendar c = Calendar.getInstance();
				String createDate = dateFormat.format(c.getTime());
				
				EnergySavingGoal esg = new EnergySavingGoal();
				esg.id.setCreateDate(createDate);
				esg.id.setStartDate( savingGoalStartDateS );
				esg.setSupplier( new Supplier( Integer.parseInt( supplierId ) ) );
				esg.setSavingGoal( Double.valueOf( savingGoal ) );
				esg.setAverageUsage(au);
				
				energySavingGoalDao.saveOrUpdate(esg);
//			energySavingGoalDao.update(esg);
//			energySavingGoalDao.add(esg);
				
				
				Map<String, Object> condition1 = new HashMap<String, Object>(); 
				condition1.put("supplierId"		, supplierId);
//			returnLocation = dayEmDao.getRootLocationId( condition1 );
				
				result.put("result", "Y");
			}else {

				result.put("result", "E"); // Empty
			}
			
        } catch (ParseException e) {
            e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
			result.put("result", "N");
		}
		
		return result;
	}

	public Map<String, Object> getEnergySavingGoalInfo(Map<String, Object> params) {
		
		List<Object> returnLocation = new ArrayList<Object>();
		Map<String, Object> supplierInfo = new HashMap<String, Object>(); 
		Map<String, Object> averageInfo 			= new HashMap<String, Object>(); // 평균 정보
		Map<String, Object> energyInfoNow 			= new HashMap<String, Object>(); // 당일 정보
		Map<String, Object> energyInfoBefore 		= new HashMap<String, Object>(); // 직전 정보
		Map<String, Object> energyInfobeforeYear 	= new HashMap<String, Object>(); // 전년 정보

		try {

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Calendar c = Calendar.getInstance();
//			System.out.println("##############################2010.07.07일 셋팅! TEST 가 끝나면 지울것!![getTotalUseOfSearchType]");
//			c.set(2010, 6, 7); // 2010.07.07일 셋팅! TEST 가 끝나면 지울것!!
			String currentDate = dateFormat.format(c.getTime()); // 오늘 날짜

			String locationId			= null;
			Integer[] locationChildren 	= null;
			
			String startDate  			= StringUtils.defaultIfEmpty( (String)params.get("searchStartDate") , "" );
			String endDate   			= StringUtils.defaultIfEmpty( (String)params.get("searchEndDate") , "" );
			String searchDateType   	= StringUtils.defaultIfEmpty( (String)params.get("searchDateType") , "1" );
			String supplierId   		= StringUtils.defaultIfEmpty( (String)params.get("supplierId") , "0" );
			String savingGoal   		= StringUtils.defaultIfEmpty( (String)params.get("savingGoal") , "0" ); // 절감목표량
			String savingGoalStartDate 	= StringUtils.defaultIfEmpty( (String)params.get("savingGoalStartDate") , "" ); // 기준일
			
			if( "0".equals( searchDateType ) ) {
				searchDateType = "1";
			}
			
			if("".equals(startDate)){
				startDate = currentDate;
			}
			if("".equals(endDate)){
				endDate = currentDate;
			}
			
				
			ArrayList<Location> rootLocation = (ArrayList<Location>) locationDao.getParents( Integer.parseInt( supplierId ) );
			if( rootLocation != null ){
				
				locationId = String.valueOf( rootLocation.get(0).getId() );
				
				Iterator a = rootLocation.get(0).getChildren().iterator();
				locationChildren = new Integer[rootLocation.get(0).getChildren().size()];
				int b = 0;
				while( a.hasNext() ){
					Location z = (Location) a.next();
					locationChildren[b] = z.getId(); 
					++b;
				}
				
			}
			
			supplierInfo.put("supplierId"		, supplierId);
			supplierInfo.put("locationId"		, locationId);


			int y = Integer.parseInt(startDate.substring(0,4));
			int m = Integer.parseInt(startDate.substring(4,6));
			int d = Integer.parseInt(startDate.substring(6,8));

			// 1. 주기별 사용량 조회
			// 일(시간)
			if( CommonConstants.DateType.DAILY.getCode().equals( searchDateType ) ) {

				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				
				Calendar getDay = Calendar.getInstance();
				getDay.set( y , m-1 , d );
				String dateS = formatter.format( getDay.getTime() );
//				logger.debug( "오늘 날짜 : " + dateS );

				Calendar getOldDay = Calendar.getInstance();
				getOldDay.set( y , m-1 , d );
				getOldDay.set( getOldDay.get(Calendar.YEAR) , getOldDay.get(Calendar.MONTH) , getOldDay.get(Calendar.DATE) - 1 );
				String oldStartDate = formatter.format( getOldDay.getTime() );
//				logger.debug( " 전일 날짜 : " + oldStartDate );

				Calendar getOldDayYear = Calendar.getInstance();
				getOldDayYear.set( y , m-1 , d );
				getOldDayYear.set( getOldDayYear.get(Calendar.YEAR) - 1 , getOldDayYear.get(Calendar.MONTH) , getOldDayYear.get(Calendar.DATE) );
				String getOldDayYearDate = formatter.format( getOldDayYear.getTime() );
//				logger.debug( " 전년 동일 날짜 : " + getOldDayYearDate );

				Map<String, Object> condition = new HashMap<String, Object>(); 
				condition.put("searchDateType"		, searchDateType);
				condition.put("supplierId"			, supplierId);
				condition.put("locationId"			, locationId);
				condition.put("locationChildren"	, locationChildren);
				condition.put("startDate"			, dateS );
				condition.put("endDate"				, dateS );
				condition.put("savingGoalStartDate"	, savingGoalStartDate );
				
				Map<String, Object> conditionOld = new HashMap<String, Object>(); 
				conditionOld.put("searchDateType"	, searchDateType);
				conditionOld.put("supplierId"		, supplierId);
				conditionOld.put("locationId"		, locationId);
				conditionOld.put("locationChildren"	, locationChildren);
				conditionOld.put("startDate"		, oldStartDate );
				conditionOld.put("endDate"			, oldStartDate );
				
				Map<String, Object> conditionOldYear = new HashMap<String, Object>(); 
				conditionOldYear.put("searchDateType"	, searchDateType);
				conditionOldYear.put("supplierId"		, supplierId);
				conditionOldYear.put("locationId"		, locationId);
				conditionOldYear.put("locationChildren"	, locationChildren);
				conditionOldYear.put("startDate"		, getOldDayYearDate );
				conditionOldYear.put("endDate"			, getOldDayYearDate );
				
				
				averageInfo 			= averageEnergyCo2( condition );
				energyInfoNow 			= energyCo2Day( condition , averageInfo );
				energyInfoBefore 		= energyCo2Day( conditionOld , averageInfo );
				energyInfobeforeYear 	= energyCo2Day( conditionOldYear , averageInfo );
				
			// 주
			} else if( CommonConstants.DateType.WEEKLY.getCode().equals( searchDateType ) ) {

				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				

				Calendar getWeek = Calendar.getInstance();
				getWeek.set( y , m-1 , d );
				
				getWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 해당 주의 일요일로 셋팅. 
				String weekS = formatter.format( getWeek.getTime() );
//				logger.debug( "[" + startDate + "] " + "금주 일요일 날짜 : " + weekS );
				
				getWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // 해당 주의 토요일로 셋팅. 
				String weekE = formatter.format( getWeek.getTime() );
//				logger.debug( "[" + startDate + "] " + "금주 토요일 날짜 : " + weekE );

				
				Calendar getOldWeek = Calendar.getInstance();
				getOldWeek.set( y , m-1 , d );
				
				getOldWeek.set(Calendar.WEEK_OF_YEAR, getOldWeek.get(Calendar.WEEK_OF_YEAR) - 1); // calendar를 전주로 셋팅
				getOldWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 해당 주의 일요일로 셋팅.
				String oldWeekS = formatter.format( getOldWeek.getTime() );
//				logger.debug( "[" + startDate + "] " + "전주 일요일 날짜 : " + oldWeekS );
				
				getOldWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // 해당 주의 토요일로 셋팅.
				String oldWeekE = formatter.format( getOldWeek.getTime() );
//				logger.debug( "[" + startDate + "] " + "전주 토요일 날짜 : " + oldWeekE );

				
				Calendar getOldWeekYear = Calendar.getInstance();
				getOldWeekYear.set( y , m-1 , d );
				
				getOldWeekYear.set(getOldWeekYear.get(Calendar.YEAR) - 1 ,getOldWeekYear.get(Calendar.MONTH) , getOldWeekYear.get(Calendar.DATE) );
				getOldWeekYear.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 해당 주의 일요일로 셋팅. 
				String oldWeekYearS = formatter.format( getOldWeekYear.getTime() );
//				logger.debug( "[" + startDate + "] " + "전년 동주 일요일 날짜 : " + oldWeekYearS );
				
				getOldWeekYear.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // 해당 주의 토요일로 셋팅. 
				String oldWeekYearE = formatter.format( getOldWeekYear.getTime() );
//				logger.debug( "[" + startDate + "] " + "전년 동주 토요일 날짜 : " + oldWeekYearE );


				Map<String, Object> condition = new HashMap<String, Object>(); 
				condition.put("searchDateType"		, searchDateType);
				condition.put("supplierId"			, supplierId);
				condition.put("locationId"			, locationId);
				condition.put("locationChildren"	, locationChildren);
				condition.put("startDate"			, weekS );
				condition.put("endDate"				, weekE );
				condition.put("savingGoalStartDate"	, savingGoalStartDate );
				
				Map<String, Object> conditionOld = new HashMap<String, Object>(); 
				conditionOld.put("searchDateType"	, searchDateType);
				conditionOld.put("supplierId"		, supplierId);
				conditionOld.put("locationId"		, locationId);
				conditionOld.put("locationChildren"	, locationChildren);
				conditionOld.put("startDate"		, oldWeekS );
				conditionOld.put("endDate"			, oldWeekE );
				
				Map<String, Object> conditionOldYear = new HashMap<String, Object>(); 
				conditionOldYear.put("searchDateType"	, searchDateType);
				conditionOldYear.put("supplierId"		, supplierId);
				conditionOldYear.put("locationId"		, locationId);
				conditionOldYear.put("locationChildren"	, locationChildren);
				conditionOldYear.put("startDate"		, oldWeekYearS );
				conditionOldYear.put("endDate"			, oldWeekYearE );
				
				
				averageInfo 			= averageEnergyCo2( condition );
				energyInfoNow 			= energyCo2Week( condition , averageInfo );
				energyInfoBefore 		= energyCo2Week( conditionOld , averageInfo );
				energyInfobeforeYear 	= energyCo2Week( conditionOldYear , averageInfo );
			
			// 월
			} else if( CommonConstants.DateType.MONTHLY.getCode().equals( searchDateType ) ) {

				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
				SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");

				Calendar getMonth = Calendar.getInstance();
				getMonth.set( y , m-1 , d );
				getMonth.set( getMonth.get(Calendar.YEAR) , getMonth.get(Calendar.MONTH) , 1 );
				String monthS = formatter.format( getMonth.getTime() );
				String monthS1 = formatter1.format( getMonth.getTime() );
//				logger.debug( "[" + startDate + "] " + "월 시작 날짜 : " + monthS );
				
				getMonth.set( getMonth.get(Calendar.YEAR) , getMonth.get(Calendar.MONTH) , getMonth.getActualMaximum(Calendar.DAY_OF_MONTH) );
				String monthE = formatter.format( getMonth.getTime() );
				String monthE1 = formatter1.format( getMonth.getTime() );
//				logger.debug( "[" + startDate + "] " + "월 마지막 날짜 : " + monthE );


				Calendar getOldMonth = Calendar.getInstance();
				getOldMonth.set( y , m-1 , d );
				getOldMonth.set( getOldMonth.get(Calendar.YEAR) , getOldMonth.get(Calendar.MONTH) - 1 , 1 );
				String oldMonthS = formatter.format( getOldMonth.getTime() );
//				logger.debug( "[" + startDate + "] " + "전월 시작 날짜 : " + oldMonthS );
				
				getOldMonth.set( getOldMonth.get(Calendar.YEAR) , getOldMonth.get(Calendar.MONTH) , getOldMonth.getActualMaximum(Calendar.DAY_OF_MONTH) );
				String oldMonthE = formatter.format( getOldMonth.getTime() );
//				logger.debug( "[" + startDate + "] " + "전월 마지막 날짜 : " + oldMonthE );

				
				Calendar getOldMonthYear = Calendar.getInstance();
				getOldMonthYear.set( y , m-1 , d );
				getOldMonthYear.set( getOldMonthYear.get(Calendar.YEAR) - 1 , getOldMonthYear.get(Calendar.MONTH) , 1 );
				String oldMonthYearS = formatter.format( getOldMonthYear.getTime() );
//				logger.debug( "[" + startDate + "] " + "전년 동월 시작날짜 : " + oldMonthYearS );
				
				getOldMonthYear.set( getOldMonthYear.get(Calendar.YEAR) , getOldMonthYear.get(Calendar.MONTH) , getOldMonthYear.getActualMaximum(Calendar.DAY_OF_MONTH) );
				String oldMonthYearE = formatter.format( getOldMonthYear.getTime() );
//				logger.debug( "[" + startDate + "] " + "전년 동월 마지막 날짜 : " + oldMonthYearE );


				Map<String, Object> condition = new HashMap<String, Object>(); 
				condition.put("searchDateType"		, searchDateType);
				condition.put("supplierId"			, supplierId);
				condition.put("locationId"			, locationId);
				condition.put("locationChildren"	, locationChildren);
				condition.put("startDate"			, monthS );
				condition.put("endDate"				, monthE );
				condition.put("savingGoalStartDate"	, savingGoalStartDate );
				
				Map<String, Object> condition1 = new HashMap<String, Object>(); 
				condition1.put("searchDateType"		, searchDateType);
				condition1.put("supplierId"			, supplierId);
				condition1.put("locationId"			, locationId);
				condition1.put("locationChildren"	, locationChildren);
				condition1.put("startDate"			, monthS1 );
				condition1.put("endDate"			, monthE1 );
				condition1.put("savingGoalStartDate", savingGoalStartDate );
				
				Map<String, Object> conditionOld = new HashMap<String, Object>(); 
				conditionOld.put("searchDateType"	, searchDateType);
				conditionOld.put("supplierId"		, supplierId);
				conditionOld.put("locationId"		, locationId);
				conditionOld.put("locationChildren"	, locationChildren);
				conditionOld.put("startDate"		, oldMonthS );
				conditionOld.put("endDate"			, oldMonthE );
				
				Map<String, Object> conditionOldYear = new HashMap<String, Object>(); 
				conditionOldYear.put("searchDateType"	, searchDateType);
				conditionOldYear.put("supplierId"		, supplierId);
				conditionOldYear.put("locationId"		, locationId);
				conditionOldYear.put("locationChildren"	, locationChildren);
				conditionOldYear.put("startDate"		, oldMonthYearS );
				conditionOldYear.put("endDate"			, oldMonthYearE );
				
				
				averageInfo 			= averageEnergyCo2( condition1 );
				energyInfoNow 			= energyCo2Month( condition , averageInfo );
				energyInfoBefore 		= energyCo2Month( conditionOld , averageInfo );
				energyInfobeforeYear 	= energyCo2Month( conditionOldYear , averageInfo );
			// 년간
			} else if( CommonConstants.DateType.YEARLY.getCode().equals( searchDateType ) ) {

				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
				SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
				
				Calendar getYear = Calendar.getInstance();
				getYear.set( y , 0 , 1 );
				String yearS = formatter.format( getYear.getTime() );
				String yearS1 = formatter1.format( getYear.getTime() );
//				logger.debug( "[" + startDate + "] " + "년 시작날짜 : " + yearS );
				
				getYear.set( getYear.get(Calendar.YEAR) , getYear.getActualMaximum(Calendar.MONTH) , getYear.getActualMaximum(Calendar.DAY_OF_MONTH) );
				String yearE = formatter.format( getYear.getTime() );
				String yearE1 = formatter1.format( getYear.getTime() );
//				logger.debug( "[" + startDate + "] " + "년 마지막 날짜 : " + yearE );

				
				Calendar getOldYear = Calendar.getInstance();
				getOldYear.set( y , 0 , 1 );
				getOldYear.set(getOldYear.get(Calendar.YEAR) - 1 ,getOldYear.get(Calendar.MONTH) , getOldYear.get(Calendar.DATE) );
				String oldYearS = formatter.format( getOldYear.getTime() );
//				logger.debug( "[" + startDate + "] " + "전년 시작 날짜 : " + oldYearS );

				getOldYear.set( getOldYear.get(Calendar.YEAR) , getOldYear.get(Calendar.MONTH) , getOldYear.getActualMaximum(Calendar.DAY_OF_MONTH) );
				String oldYearE = formatter.format( getOldYear.getTime() );
//				logger.debug( "[" + startDate + "] " + "전년 마지막 날짜 : " + oldYearE);

				
				Calendar getOldYearYear = Calendar.getInstance();
				getOldYearYear.set( y , 0 , 1 );
				getOldYearYear.set(getOldYearYear.get(Calendar.YEAR) - 2 ,getOldYearYear.get(Calendar.MONTH) , getOldYearYear.get(Calendar.DATE) );
				String oldYearYearS = formatter.format( getOldYearYear.getTime() );
//				logger.debug( "[" + startDate + "] " + "전전년  시작 날짜 : " + oldYearYearS );
				
				getOldYearYear.set(getOldYearYear.get(Calendar.YEAR) - 2 ,getOldYearYear.get(Calendar.MONTH) , getOldYearYear.get(Calendar.DATE) );
				String oldYearYearE = formatter.format( getOldYearYear.getTime() );
//				logger.debug( "[" + startDate + "] " + "전전년  마지막 날짜 : " + oldYearYearE );

				
				Map<String, Object> condition = new HashMap<String, Object>(); 
				condition.put("searchDateType"		, searchDateType);
				condition.put("supplierId"			, supplierId);
				condition.put("locationId"			, locationId);
				condition.put("locationChildren"	, locationChildren);
				condition.put("startDate"			, yearS );
				condition.put("endDate"				, yearE );
				condition.put("savingGoalStartDate"	, savingGoalStartDate );
				
				Map<String, Object> condition1 = new HashMap<String, Object>(); 
				condition1.put("searchDateType"		, searchDateType);
				condition1.put("supplierId"			, supplierId);
				condition1.put("locationId"			, locationId);
				condition1.put("locationChildren"	, locationChildren);
				condition1.put("startDate"			, yearS1 );
				condition1.put("endDate"			, yearE1 );
				condition1.put("savingGoalStartDate", savingGoalStartDate );
				
				Map<String, Object> conditionOld = new HashMap<String, Object>(); 
				conditionOld.put("searchDateType"	, searchDateType);
				conditionOld.put("supplierId"		, supplierId);
				conditionOld.put("locationId"		, locationId);
				conditionOld.put("locationChildren"	, locationChildren);
				conditionOld.put("startDate"		, oldYearS );
				conditionOld.put("endDate"			, oldYearE );
				
				Map<String, Object> conditionOldYear = new HashMap<String, Object>(); 
				conditionOldYear.put("searchDateType"	, searchDateType);
				conditionOldYear.put("supplierId"		, supplierId);
				conditionOldYear.put("locationId"		, locationId);
				conditionOldYear.put("locationChildren"	, locationChildren);
				conditionOldYear.put("startDate"		, oldYearYearS );
				conditionOldYear.put("endDate"			, oldYearYearE );
				
				
				averageInfo 			= averageEnergyCo2( condition1 );
				energyInfoNow 			= energyCo2Year( condition , averageInfo );
				energyInfoBefore 		= energyCo2Year( conditionOld , averageInfo );
				energyInfobeforeYear 	= energyCo2Year( conditionOldYear , averageInfo );
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
//		result.put("rootLocation"			, supplierInfo);
//		result.put("averageInfo"			, averageInfo);
//		result.put("energyInfoNow"			, energyInfoNow);
//		result.put("energyInfoBefore"		, energyInfoBefore);
//		result.put("energyInfobeforeYear"	, energyInfobeforeYear);
		
		
		ArrayList<Map<String,Object>> resultList = new ArrayList();
		resultList.add( 0 , averageInfo);
		resultList.add( 1 , energyInfoNow);
		resultList.add( 2 , energyInfoBefore);
		resultList.add( 3 , energyInfobeforeYear);

		result.put("info"			, resultList);
		return result;
	}
	
	/**
	 * 기간별 사용량 , 탄소배출량 , 절감율을 구한다.
	 * @param condition
	 * @return
	 */
	private Map<String, Object> averageEnergyCo2(Map<String, Object> condition ) {

		String gubun = "";
		Integer yearsSize = 0;
		Double energyUse = 0.0;
		Double co2Use = 0.0;
		Double saving = 0.0;
		Double savingGoal = 0.0;
		String savingGoalStartDate = "";
		
		String searchDateType		= (String)condition.get("searchDateType");
		String supplierId   		= (String)condition.get("supplierId");
		String locationId 			= (String)condition.get("locationId");
		String startDate  			= (String)condition.get("startDate");
		String endDate   			= (String)condition.get("endDate");
		
		List<EnergySavingGoal> esgList = energySavingGoalDao.getEnergySavingGoalListBystartDate( endDate , Integer.parseInt( supplierId ) ); 
		if( esgList != null && !esgList.isEmpty() ){
			
			EnergySavingGoal esg = esgList.get(0);

			if( esg.getAverageUsage() != null ){
				
				// 일(시간)
				if( CommonConstants.DateType.DAILY.getCode().equals( searchDateType ) ) {
					
					energyUse 	= esg.getAverageUsage().getAvgUsageDay();
					co2Use 		= esg.getAverageUsage().getAvgCo2Day();
					// 주
				} else if( CommonConstants.DateType.WEEKLY.getCode().equals( searchDateType ) ) {
					
					energyUse 	= esg.getAverageUsage().getAvgUsageWeek();
					co2Use 		= esg.getAverageUsage().getAvgCo2Week();
					// 월
				} else if( CommonConstants.DateType.MONTHLY.getCode().equals( searchDateType ) ) {
					
					energyUse 	= esg.getAverageUsage().getAvgUsageMonth();
					co2Use 		= esg.getAverageUsage().getAvgCo2Month();
					// 년간
				} else if( CommonConstants.DateType.YEARLY.getCode().equals( searchDateType ) ) {
					
					energyUse 	= esg.getAverageUsage().getAvgUsageYear();
					co2Use 		= esg.getAverageUsage().getAvgCo2Year();
				}
				
				
				
				List<AverageUsageBase> bases = esg.getAverageUsage().getBases();
		    	String basesString = "";
		    	
				if( !bases.isEmpty() && bases.size() > 0 ){
		    		
		    		for(int i=0; i < bases.size(); i++){
		    			
		    			String temp = bases.get(i).getId().getUsageYear();
		    			if( basesString.indexOf( temp ) < 0 ){
		    				
		    				++yearsSize;
		    				if( i > 0 ) {
		    					
		    					basesString = basesString + "," + temp;
		    				}else {
		    					
		    					basesString = basesString + temp;
		    				}
		    			}
		    		}
		    	}
				
//				yearsSize = esg.getAverageUsage().getBasesToString().split(",").length;
				
				
			} else {
				
				logger.error("energySavingGoal에AverageUsage 키값이 존재하지 않습니다!! (energySavingGoal은 반드시 AverageUsage 정보를 가지고 있어야한다.)");
			}
			
			savingGoal = esg.getSavingGoal();
			

			

			String savingGoalStartDateTemp = esg.getStartDate();

			if( savingGoalStartDateTemp.length() >= 6 ){
				
				int y = Integer.parseInt( savingGoalStartDateTemp.substring(0,4) );
				int m = Integer.parseInt( savingGoalStartDateTemp.substring(4,6) );
				int d = Integer.parseInt( savingGoalStartDateTemp.substring(6,8) );
				
				Calendar c = Calendar.getInstance();
				c.set( y , m-1 , d );
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

				savingGoalStartDate = formatter.format( c.getTime() );
			}
			

		}
		
//		// 일(시간)
//		if( CommonConstants.DateType.DAILY.getCode().equals( searchDateType ) ) {
//			
//			gubun = "일 평균";
//			// 주
//		} else if( CommonConstants.DateType.WEEKLY.getCode().equals( searchDateType ) ) {
//			
//			gubun = "주 평균";
//			// 월
//		} else if( CommonConstants.DateType.MONTHLY.getCode().equals( searchDateType ) ) {
//			
//			gubun = "월 평균";
//			// 년간
//		} else if( CommonConstants.DateType.YEARLY.getCode().equals( searchDateType ) ) {
//			
//			gubun = "년 평균";
//		}
		
		gubun = searchDateType;
		
		
		if( energyUse == null ){
			energyUse = 0.0;
		}
		if( co2Use == null ){
			co2Use = 0.0;
		}

    	NumberFormat numberFormatter = new DecimalFormat("###,###,###,###.##");

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put( "gubun"				, gubun );
		resultMap.put( "searchDateType"		, searchDateType );
		resultMap.put( "yearsSize"			, yearsSize );
		resultMap.put( "energyUse"			, numberFormatter.format( energyUse ) + "kWh" );
		resultMap.put( "co2Use"				, numberFormatter.format( co2Use ) + "g" );
		resultMap.put( "energyUseNum"		, energyUse );
		resultMap.put( "co2UseNum"			, co2Use );
		resultMap.put( "saving"				, saving );
		resultMap.put( "savingGoal"			, savingGoal );
		resultMap.put( "savingGoalStartDate", savingGoalStartDate );
		return resultMap;
	}

	/**
	 * 일 사용량 , 탄소배출량 , 절감율을 구한다.
	 * @param condition
	 * @return
	 */
	private Map<String, Object> energyCo2Day(Map<String, Object> condition , Map<String, Object> averageInfo) {
		
		String gubun = "";
		Double energyUse = 0.0;
		Double co2Use = 0.0;
		Double saving = 0.0;

		Double averageEnergyUse = 0.0;
		Double averageCo2Use = 0.0;
		
		try {
			averageEnergyUse 	= (Double) averageInfo.get("energyUseNum");
			averageCo2Use 		= (Double) averageInfo.get("co2UseNum");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		String searchDateType		= (String)condition.get("searchDateType");
		String supplierId   		= (String)condition.get("supplierId");
		String locationId 			= (String)condition.get("locationId");
		String startDate  			= (String)condition.get("startDate");
		String endDate   			= (String)condition.get("endDate");
		Integer[] locationChildren	= (Integer[]) condition.get("locationChildren");

		int y = Integer.parseInt(startDate.substring(0,4));
		int m = Integer.parseInt(startDate.substring(4,6));
		int d = Integer.parseInt(startDate.substring(6,8));

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.set( y , m-1 , d );
		

		gubun = formatter.format( c.getTime() );

		
		
		// 조회시 필요한 기준채널, 일자, 미터아이디 생성
		int channel = 1;

		Condition cdtLocationChildren = new Condition("location.id", locationChildren , null, Restriction.IN);
		
    	Double sumEm = dayMonthMaxMinAvgSum( cdtLocationChildren , "EM" , "1.3.1.1" , channel , "day" , "sum" , startDate , endDate );
    	Double sumGm = dayMonthMaxMinAvgSum( cdtLocationChildren , "WM" , "1.3.1.2" , channel , "day" , "sum" , startDate , endDate );
    	Double sumWm = dayMonthMaxMinAvgSum( cdtLocationChildren , "GM" , "1.3.1.3" , channel , "day" , "sum" , startDate , endDate );

    	energyUse = sumEm + sumGm + sumWm;

    	
		channel = 0; // 탄소배출량

    	Double sumEmCo2 = dayMonthMaxMinAvgSum( cdtLocationChildren , "EM" , "1.3.1.1" , channel , "day" , "sum" , startDate , endDate );
    	Double sumGmCo2 = dayMonthMaxMinAvgSum( cdtLocationChildren , "GM" , "1.3.1.3" , channel , "day" , "sum" , startDate , endDate );
    	Double sumWmCo2 = dayMonthMaxMinAvgSum( cdtLocationChildren , "WM" , "1.3.1.2" , channel , "day" , "sum" , startDate , endDate );
    	
    	co2Use = sumEmCo2 + sumGmCo2 + sumWmCo2;


    	// 절감율
    	saving = getSavingPercentage( averageEnergyUse.intValue()  , energyUse.intValue() );
    	
    	
    	NumberFormat numberFormatter = new DecimalFormat("###,###,###,###.##");
    	
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("gubun", gubun );
		resultMap.put("searchDateType"		, searchDateType );
		resultMap.put("energyUse", numberFormatter.format( energyUse ) + "kWh" );
		resultMap.put("co2Use", numberFormatter.format( co2Use ) + "g" );
		resultMap.put("saving", numberFormatter.format( saving ) );
		resultMap.put("savingNum", numberFormatter.format( saving ) );
		return resultMap;
	}

	/**
	 * 주간 사용량 , 탄소배출량 , 절감율을 구한다.
	 * @param condition
	 * @return
	 */
	private Map<String, Object> energyCo2Week(Map<String, Object> condition , Map<String, Object> averageInfo) {

		String gubun = "";
		Double energyUse = 0.0;
		Double co2Use = 0.0;
		Double saving = 0.0;

		Double averageEnergyUse = 0.0;
		Double averageCo2Use = 0.0;
		
		try {
			averageEnergyUse 	= (Double) averageInfo.get("energyUseNum");
			averageCo2Use 		= (Double) averageInfo.get("co2UseNum");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		String searchDateType		= (String)condition.get("searchDateType");
		String supplierId   		= (String)condition.get("supplierId");
		String locationId 			= (String)condition.get("locationId");
		String startDate  			= (String)condition.get("startDate");
		String endDate   			= (String)condition.get("endDate");
		Integer[] locationChildren	= (Integer[]) condition.get("locationChildren");

		int y = Integer.parseInt(startDate.substring(0,4));
		int m = Integer.parseInt(startDate.substring(4,6));
		int d = Integer.parseInt(startDate.substring(6,8));

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		Calendar c = Calendar.getInstance();
		c.set( y , m-1 , d );
		

		gubun = formatter.format( c.getTime() ) + " " + c.get(Calendar.WEEK_OF_MONTH) ;


		
		// 조회시 필요한 기준채널, 일자, 미터아이디 생성
		int channel = 1;

		Condition cdtLocationChildren = new Condition("location.id", locationChildren , null, Restriction.IN);
		
    	Double sumEm = dayMonthMaxMinAvgSum( cdtLocationChildren , "EM" , "1.3.1.1" , channel , "day" , "sum" , startDate , endDate );
    	Double sumGm = dayMonthMaxMinAvgSum( cdtLocationChildren , "WM" , "1.3.1.2" , channel , "day" , "sum" , startDate , endDate );
    	Double sumWm = dayMonthMaxMinAvgSum( cdtLocationChildren , "GM" , "1.3.1.3" , channel , "day" , "sum" , startDate , endDate );

    	energyUse = sumEm + sumGm + sumWm;

    	
		channel = 0; // 탄소배출량

    	Double sumEmCo2 = dayMonthMaxMinAvgSum( cdtLocationChildren , "EM" , "1.3.1.1" , channel , "day" , "sum" , startDate , endDate );
    	Double sumGmCo2 = dayMonthMaxMinAvgSum( cdtLocationChildren , "GM" , "1.3.1.3" , channel , "day" , "sum" , startDate , endDate );
    	Double sumWmCo2 = dayMonthMaxMinAvgSum( cdtLocationChildren , "WM" , "1.3.1.2" , channel , "day" , "sum" , startDate , endDate );
    	
    	co2Use = sumEmCo2 + sumGmCo2 + sumWmCo2;


    	// 절감율
    	saving = getSavingPercentage( averageEnergyUse.intValue()  , energyUse.intValue() );
    	
    	
    	

    	NumberFormat numberFormatter = new DecimalFormat("###,###,###,###.##");
    	
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("gubun", gubun );
		resultMap.put("searchDateType"		, searchDateType );
		resultMap.put("energyUse", numberFormatter.format( energyUse ) + "kWh" );
		resultMap.put("co2Use", numberFormatter.format( co2Use ) + "g");
		resultMap.put("saving", numberFormatter.format( saving ) );
		resultMap.put("savingNum", numberFormatter.format( saving ) );
		return resultMap;
	}

	/**
	 * 월간 사용량 , 탄소배출량 , 절감율을 구한다.
	 * @param condition
	 * @return
	 */
	private Map<String, Object> energyCo2Month(Map<String, Object> condition , Map<String, Object> averageInfo) {

		String gubun = "";
		Double energyUse = 0.0;
		Double co2Use = 0.0;
		Double saving = 0.0;

		Double averageEnergyUse = 0.0;
		Double averageCo2Use = 0.0;
		
		try {
			averageEnergyUse 	= (Double) averageInfo.get("energyUseNum");
			averageCo2Use 		= (Double) averageInfo.get("co2UseNum");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		String searchDateType		= (String)condition.get("searchDateType");
		String supplierId   		= (String)condition.get("supplierId");
		String locationId 			= (String)condition.get("locationId");
		String startDate  			= (String)condition.get("startDate");
		String endDate   			= (String)condition.get("endDate");
		Integer[] locationChildren	= (Integer[]) condition.get("locationChildren");

		int y = Integer.parseInt(startDate.substring(0,4));
		int m = Integer.parseInt(startDate.substring(4,6));

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
		Calendar c = Calendar.getInstance();
		c.set( y , m-1 , 1 );
		

		gubun = formatter.format( c.getTime() );
		
		

		
		// 조회시 필요한 기준채널, 일자, 미터아이디 생성
		int channel = 1;

		Condition cdtLocationChildren = new Condition("location.id", locationChildren , null, Restriction.IN);
		
    	Double sumEm = dayMonthMaxMinAvgSum( cdtLocationChildren , "EM" , "1.3.1.1" , channel , "month" , "sum" , startDate , endDate );
    	Double sumGm = dayMonthMaxMinAvgSum( cdtLocationChildren , "WM" , "1.3.1.2" , channel , "month" , "sum" , startDate , endDate );
    	Double sumWm = dayMonthMaxMinAvgSum( cdtLocationChildren , "GM" , "1.3.1.3" , channel , "month" , "sum" , startDate , endDate );

    	energyUse = sumEm + sumGm + sumWm;

    	
		channel = 0; // 탄소배출량

    	Double sumEmCo2 = dayMonthMaxMinAvgSum( cdtLocationChildren , "EM" , "1.3.1.1" , channel , "month" , "sum" , startDate , endDate );
    	Double sumGmCo2 = dayMonthMaxMinAvgSum( cdtLocationChildren , "GM" , "1.3.1.3" , channel , "month" , "sum" , startDate , endDate );
    	Double sumWmCo2 = dayMonthMaxMinAvgSum( cdtLocationChildren , "WM" , "1.3.1.2" , channel , "month" , "sum" , startDate , endDate );
    	
    	co2Use = sumEmCo2 + sumGmCo2 + sumWmCo2;


    	// 절감율
    	saving = getSavingPercentage( averageEnergyUse.intValue()  , energyUse.intValue() );
    	

    	NumberFormat numberFormatter = new DecimalFormat("###,###,###,###.##");
    	
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("gubun", gubun );
		resultMap.put("searchDateType"		, searchDateType );
		resultMap.put("energyUse", numberFormatter.format( energyUse ) + "kWh" );
		resultMap.put("co2Use", numberFormatter.format( co2Use ) + "g" );
		resultMap.put("saving", numberFormatter.format( saving ) );
		resultMap.put("savingNum", numberFormatter.format( saving ) );
		return resultMap;
	}

	/**
	 * 년간 사용량 , 탄소배출량 , 절감율을 구한다.
	 * @param condition
	 * @return
	 */
	private Map<String, Object> energyCo2Year(Map<String, Object> condition , Map<String, Object> averageInfo) {

		String gubun = "";
		Double energyUse = 0.0;
		Double co2Use = 0.0;
		Double saving = 0.0;

		Double averageEnergyUse = 0.0;
		Double averageCo2Use = 0.0;
		
		try {
			averageEnergyUse 	= (Double) averageInfo.get("energyUseNum");
			averageCo2Use 		= (Double) averageInfo.get("co2UseNum");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		String searchDateType		= (String)condition.get("searchDateType");
		String supplierId   		= (String)condition.get("supplierId");
		String locationId 			= (String)condition.get("locationId");
		String startDate  			= (String)condition.get("startDate");
		String endDate   			= (String)condition.get("endDate");
		Integer[] locationChildren	= (Integer[]) condition.get("locationChildren");
		
		int y = Integer.parseInt(startDate.substring(0,4));
		int m = Integer.parseInt( startDate.substring(4,6) );

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		Calendar c = Calendar.getInstance();
		c.set( y , m-1 , 1 );
		
		
		
		
		gubun = formatter.format( c.getTime() );
		

		// 조회시 필요한 기준채널, 일자, 미터아이디 생성
		int channel = 1;

		Condition cdtLocationChildren = new Condition("location.id", locationChildren , null, Restriction.IN);
		
    	Double sumEm = dayMonthMaxMinAvgSum( cdtLocationChildren , "EM" , "1.3.1.1" , channel , "month" , "sum" , startDate , endDate );
    	Double sumGm = dayMonthMaxMinAvgSum( cdtLocationChildren , "WM" , "1.3.1.2" , channel , "month" , "sum" , startDate , endDate );
    	Double sumWm = dayMonthMaxMinAvgSum( cdtLocationChildren , "GM" , "1.3.1.3" , channel , "month" , "sum" , startDate , endDate );

    	energyUse = sumEm + sumGm + sumWm;

    	
		channel = 0; // 탄소배출량

    	Double sumEmCo2 = dayMonthMaxMinAvgSum( cdtLocationChildren , "EM" , "1.3.1.1" , channel , "month" , "sum" , startDate , endDate );
    	Double sumGmCo2 = dayMonthMaxMinAvgSum( cdtLocationChildren , "GM" , "1.3.1.3" , channel , "month" , "sum" , startDate , endDate );
    	Double sumWmCo2 = dayMonthMaxMinAvgSum( cdtLocationChildren , "WM" , "1.3.1.2" , channel , "month" , "sum" , startDate , endDate );
    	
    	co2Use = sumEmCo2 + sumGmCo2 + sumWmCo2;


    	// 절감율
    	saving = getSavingPercentage( averageEnergyUse.intValue()  , energyUse.intValue() );
    	
    	

    	NumberFormat numberFormatter = new DecimalFormat("###,###,###,###.##");
    	
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("gubun", gubun );
		resultMap.put("searchDateType"		, searchDateType );
		resultMap.put("energyUse", numberFormatter.format( energyUse ) + "kWh" );
		resultMap.put("co2Use", numberFormatter.format( co2Use ) + "g" );
		resultMap.put("saving", numberFormatter.format( saving ) );
		resultMap.put("savingNum", numberFormatter.format( saving ) );
		return resultMap;
	}

	/**
	 * 증감율 구하기
	 * @param pre 기준값
	 * @param now 비교값
	 * @return
	 */
	public Double getSavingPercentage(int pre, int now) {
		double dPre = 0d;
		double dNow = 0d;

		if (pre == 0) {
			dPre = 1d;
		} else {
			dPre = (double) pre;
		}
		if (now == 0) {
			dNow = 1d;
		} else {
			dNow = (double) now;
		}

		double result = 0d;
		
		if(pre != 0 && now != 0) {
			result = ((dNow - dPre) / dPre) * 100d;
		}
		


		return result;
	}

	/**
	 * 조회 기간과 에너지 타입에따른 최대/최소/평균/합 수치를 반환함.
	 * @param cdtLocationChildren
	 * @param energyType : 전기 = EM , 가스  = GM , 수도 = WM
	 * @param meterTypeCode : 예)1.3.1.1 = EnergyMeter , 1.3.1.2 = WaterMeter , 1.3.1.3 = GasMeter
	 * @param channel
	 * @param dateType : day : day테이블 조회 , month : month테이블 조회
	 * @param valueType : 최대,최소,평균, 합
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private Double dayMonthMaxMinAvgSum(Condition cdtLocationChildren , String energyType, String meterTypeCode, int channel, String dateType , String valueType , String startDate, String endDate) {

		Double returnValue = 0.0;
		List<Object> sumList = null;
		

		Set<Condition> conditionSetMeters = new HashSet<Condition>();
		
		conditionSetMeters.add(cdtLocationChildren);
		
		Code meterTypeCodeObject = codeDao.getCodeIdByCodeObject( meterTypeCode );
		
		Condition conditionMeterType = new Condition("meterType.id", new Object[] { meterTypeCodeObject.getId() }, null, Restriction.EQ);
		
		conditionSetMeters.add(conditionMeterType);
		
		List<Meter> meterList = meterDao.findByConditions( conditionSetMeters );
		Integer[] meterIds = new Integer[meterList.size()];
		for(int i=0; i < meterList.size(); i++ ){
			Meter mm = meterList.get(i);
			meterIds[i] = mm.getId();
		}
		
		if( meterIds.length > 0 ){
			
			Set<Condition> conditionSetGroupFunction = new HashSet<Condition>();
			
			Condition conditionChannel = new Condition("id.channel", new Object[] { channel }, null, Restriction.EQ);
			conditionSetGroupFunction.add(conditionChannel);
			if( !startDate.isEmpty() && !endDate.isEmpty() ) {
				
				Condition cdt21 = null;
				if( "day".equals( dateType ) ){
					
					cdt21 = new Condition("id.yyyymmdd", new Object[] { startDate, endDate }, null, Restriction.BETWEEN);
				} else if( "month".equals( dateType ) ){
					
					cdt21 = new Condition("id.yyyymm", new Object[] { startDate, endDate }, null, Restriction.BETWEEN);
				}
				
				conditionSetGroupFunction.add(cdt21);
			}
			
			Condition conditionMeterIds = new Condition("meter.id", meterIds , null, Restriction.IN);
			
			conditionSetGroupFunction.add(conditionMeterIds);
			
			if( "EM".equals( energyType ) ){
				
				if( "day".equals( dateType ) ){
					
					sumList = dayEMDao.getDayEMsMaxMinAvgSum( conditionSetGroupFunction , valueType );
				} else if( "month".equals( dateType ) ){
					
					sumList = monthEMDao.getMonthEMsMaxMinAvgSum( conditionSetGroupFunction , valueType );
				}
			} else if( "GM".equals( energyType ) ){
				
				if( "day".equals( dateType ) ){
					
					sumList = dayGMDao.getDayGMsMaxMinAvgSum( conditionSetGroupFunction , valueType );
				} else if( "month".equals( dateType ) ){
					
					sumList = monthGMDao.getMonthGMsMaxMinAvgSum( conditionSetGroupFunction , valueType );
				}
			} else if( "WM".equals( energyType ) ){
				
				if( "day".equals( dateType ) ){
					
					sumList = dayWMDao.getDayWMsMaxMinAvgSum( conditionSetGroupFunction , valueType );
				} else if( "month".equals( dateType ) ){
					
					sumList = monthWMDao.getMonthWMsMaxMinAvgSum( conditionSetGroupFunction , valueType );
				}
			}
		}
		
		
		if( sumList != null && !sumList.isEmpty() && sumList.get(0) != null ){
			returnValue = (Double) sumList.get(0);
		}
		return returnValue;
	}
	

	public Map<String, Object> getEnergySavingGoalAvgYearsUsed(Map<String, Object> params) {
		
		int channel = 1;
		List<Object> yearList   	= (List<Object>) params.get("years");
		String supplierId   		= StringUtils.defaultIfEmpty( (String)params.get("supplierId") , "0" );

		Condition cdtLocationChildren = null;
			
		ArrayList<Location> rootLocation = (ArrayList<Location>) locationDao.getParents( Integer.parseInt( supplierId ) );
		if( rootLocation != null ){
			
			String locationId = String.valueOf( rootLocation.get(0).getId() );
			
			Iterator a = rootLocation.get(0).getChildren().iterator();
			Integer[] locationChildren = new Integer[rootLocation.get(0).getChildren().size()];
			int b = 0;
			while( a.hasNext() ){
				Location z = (Location) a.next();
				locationChildren[b] = z.getId(); 
				++b;
			}
			
			cdtLocationChildren = new Condition("location.id", locationChildren , null, Restriction.IN);
		}
		
		
		
		Map<String, Object> result = new HashMap<String, Object>();
		List<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();
		
		Double sumEm = 0.0; 
		Double sumGm = 0.0; 
		Double sumWm = 0.0; 

		int avgYearSize = yearList.size();
		
		if( avgYearSize > 0 ){
			
			for( int i=0; i < avgYearSize; i++ ){
				String year = yearList.get(i).toString();
				
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
				Calendar c = Calendar.getInstance();
				c.set( Integer.parseInt( year ) , 0 , 1 );
				String startDate = formatter.format( c.getTime() );
				c.set( Integer.parseInt( year ) , 11 , 1 );
				String endDate = formatter.format( c.getTime() );
				
				sumEm = sumEm + dayMonthMaxMinAvgSum( cdtLocationChildren , "EM" , "1.3.1.1" , channel , "month" , "sum" , startDate , endDate );
				sumWm = sumWm + dayMonthMaxMinAvgSum( cdtLocationChildren , "WM" , "1.3.1.2" , channel , "month" , "sum" , startDate , endDate );
				sumGm = sumGm + dayMonthMaxMinAvgSum( cdtLocationChildren , "GM" , "1.3.1.3" , channel , "month" , "sum" , startDate , endDate );
				
				
			}
			
			
			Double emAvgUsed = (Double) ObjectUtils.defaultIfNull( ( sumEm / avgYearSize ) , new Double(0.0) );
			Double gmAvgUsed = (Double) ObjectUtils.defaultIfNull( ( sumWm / avgYearSize ) , new Double(0.0) );
			Double wmAvgUsed = (Double) ObjectUtils.defaultIfNull( ( sumGm / avgYearSize ) , new Double(0.0) );
			Double totalAvgUsed = (Double) ObjectUtils.defaultIfNull( ( (sumEm + sumWm + sumGm) / avgYearSize ) , new Double(0.0) );
			
			NumberFormat numberFormatter = new DecimalFormat("###,###,###,###.##");
			
			HashMap<String,Object> used = new HashMap<String,Object>();
			used.put( "avgYear"			, String.valueOf( avgYearSize ) + (String)params.get("msgYear") );
			used.put( "emAvgUsed"		, numberFormatter.format( emAvgUsed ) + "kWh" );
			used.put( "gmAvgUsed"		, numberFormatter.format( gmAvgUsed ) + "m3" );
			used.put( "wmAvgUsed"		, numberFormatter.format( wmAvgUsed ) + "m3" );
			used.put( "totalAvgUsed"	, numberFormatter.format( totalAvgUsed ) + "TOE" );
			
			resultList.add( used );
			
			result.put( "avgYearsUsedList" , resultList );
		}
		return result;
	}
	/**
	 * 집계가능한 년도별 전기 , 가스, 수도 , 총사용량(전기+가스+수도) 리스트를 조회한다.
	 * @return
	 */
	public Map<String, Object> getEnergySavingGoalYearsUsed(Map<String, Object> params) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		
		List<HashMap<String,Object>> yearsUsedList = new ArrayList<HashMap<String,Object>>();
		
		// 1. 에너지 타입별 집계가능한 년도 추출
		List<Object> yearsEm = monthEMDao.getMonthToYears();
		List<Object> yearsGm = monthGMDao.getMonthToYears();
		List<Object> yearsWm = monthWMDao.getMonthToYears();
		
		// 2. 에너지 타입별 해당 년도의 검침값의 합 추출
		String locationId			= null;
		Integer[] locationChildren 	= null;
		
		String startDate  			= StringUtils.defaultIfEmpty( (String)params.get("searchStartDate") , "" );
		String endDate   			= StringUtils.defaultIfEmpty( (String)params.get("searchEndDate") , "" );
		String supplierId   		= StringUtils.defaultIfEmpty( (String)params.get("supplierId") , "0" );
		
		ArrayList<Location> rootLocation = (ArrayList<Location>) locationDao.getParents( Integer.parseInt( supplierId ) );
		if( rootLocation != null ){
			
			locationId = String.valueOf( rootLocation.get(0).getId() );
			
			Iterator a = rootLocation.get(0).getChildren().iterator();
			locationChildren = new Integer[rootLocation.get(0).getChildren().size()];
			int b = 0;
			while( a.hasNext() ){
				Location z = (Location) a.next();
				locationChildren[b] = z.getId(); 
				++b;
			}
			
		}

		Map<String, Object> conditionEM = new HashMap<String, Object>();
		conditionEM.put( "energyType" , "EM" );
		conditionEM.put( "locationChildren" , locationChildren );
		conditionEM.put( "years" , yearsEm );
		Map<String, Object> conditionGM = new HashMap<String, Object>();
		conditionGM.put( "energyType" , "GM" );
		conditionGM.put( "locationChildren" , locationChildren );
		conditionGM.put( "years" , yearsGm );
		Map<String, Object> conditionWM = new HashMap<String, Object>();
		conditionWM.put( "energyType" , "WM" );
		conditionWM.put( "locationChildren" , locationChildren );
		conditionWM.put( "years" , yearsWm );
		HashMap<String , Object> yearsEmSum = getMonthToYearsSum( conditionEM );
		HashMap<String , Object> yearsGmSum = getMonthToYearsSum( conditionGM );
		HashMap<String , Object> yearsWmSum = getMonthToYearsSum( conditionWM );
		
		// 3. 조회한 년도에따른 정보를 리스트 형식으로 만든다. ( 전기 , 가스 , 수도 , 총사용량(전기+가스+수도)
		yearsUsedList = getYearUsedList( yearsEm , yearsGm , yearsWm , yearsEmSum , yearsGmSum , yearsWmSum, params);
		
		result.put( "yearsUsedList" , yearsUsedList );
		return result;
	}

	/**
	 * 에너지 타입의 해당 년도별의 검침값의 합 
	 * @param condition
	 * @return
	 */
	private HashMap<String , Object> getMonthToYearsSum(Map<String, Object> condition) {
		

		HashMap<String , Object> result = new HashMap<String,Object>();
		
		String energyType		= (String)condition.get("energyType");
		List<Object> years   	= (List<Object>) condition.get("years");
		Integer[] locationChildren	= (Integer[]) condition.get("locationChildren");

		int channel = 1;
		Condition cdtLocationChildren = new Condition("location.id", locationChildren , null, Restriction.IN);
		
		for( int i=0; i < years.size(); i++ ){
			String year = years.get(i).toString();

			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
			Calendar c = Calendar.getInstance();
			c.set( Integer.parseInt( year ) , 0 , 1 );
			String startDate = formatter.format( c.getTime() );
			c.set( Integer.parseInt( year ) , 11 , 1 );
			String endDate = formatter.format( c.getTime() );
			
//			select * from code where code ='1.3.1'
//			select * from code where PARENT_ID = 182
			
//			183	1.3.1.1	EnergyMeter	EnergyMeter	1	182
//			191	1.3.1.2	WaterMeter	WaterMeter	2	182
//			197	1.3.1.3	GasMeter	GasMeter	3	182
//			198	1.3.1.4	HeatMeter	HeatMeter	4	182
//			208	1.3.1.5	VolumeCorrector	VolumeCorrector	5	182

			
			Double sum = 0.0; 
			if( "EM".equals( energyType ) ){
				
				sum= dayMonthMaxMinAvgSum( cdtLocationChildren , "EM" , "1.3.1.1" , channel , "month" , "sum" , startDate , endDate );
			} else if( "WM".equals( energyType ) ){
				
				sum= dayMonthMaxMinAvgSum( cdtLocationChildren , "WM" , "1.3.1.1" , channel , "month" , "sum" , startDate , endDate );
				
				System.out.println("======================= 검침데이터에 사용된  meter의 meter code 타입 수정 필요함!!!! ===================");
//				sum= dayMonthMaxMinAvgSum( cdtLocationChildren , "WM" , "1.3.1.2" , channel , "month" , "sum" , startDate , endDate );
			} else if( "GM".equals( energyType ) ){
				
				sum= dayMonthMaxMinAvgSum( cdtLocationChildren , "GM" , "1.3.1.1" , channel , "month" , "sum" , startDate , endDate );
				
				System.out.println("======================= 검침데이터에 사용된  meter의 meter code 타입 수정 필요함!!!! ===================");
//				sum= dayMonthMaxMinAvgSum( cdtLocationChildren , "GM" , "1.3.1.3" , channel , "month" , "sum" , startDate , endDate );
			}
			
			result.put(year	, sum);
			
		}
		
		
		return result;
	}

	/**
	 * 조회한 년도에따른 정보를 리스트 형식으로 반환한다. ( 전기 , 가스 , 수도 , 총사용량(전기+가스+수도) )
	 * @param yearsEmSum
	 * @param yearsGmSum
	 * @param yearsWmSum
	 * @param yearsWmSum2 
	 * @param yearsGmSum2 
	 * @param yearsEmSum2 
	 * @return
	 */
	private List<HashMap<String, Object>> getYearUsedList(	List<Object> yearsEm, List<Object> yearsGm, List<Object> yearsWm,
															HashMap<String , Object> yearsEmSum, HashMap<String , Object> yearsGmSum,
															HashMap<String , Object> yearsWmSum, Map<String, Object> params) {
		

		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		
		List<Object> tmpList = new ArrayList<Object>();
		
		tmpList.addAll( yearsEm );
		tmpList.addAll( yearsGm );
		tmpList.addAll( yearsWm );
		
		List<Object> yearList = new ArrayList<Object>();
		String[] arrStr = ( String[]) tmpList.toArray( new String[ tmpList.size()]);
		Arrays.sort( arrStr , String.CASE_INSENSITIVE_ORDER);

		for( int i=0; i<arrStr.length; i++){
			
			if( i == 0 ) {
				
				yearList.add( arrStr[i] );
			}
			
			if( i>0 && !arrStr[i-1].equals(arrStr[i]) ){
				
				yearList.add( arrStr[i] );
			}
		}
		System.out.println("#" + yearList.toString());
		
//		Collections.sort(list)
		
		
		for( int i=0; i < yearList.size(); i++ ){
			
			String year = yearList.get(i).toString();
			Double emUsed = (Double) ObjectUtils.defaultIfNull( yearsEmSum.get(year) , new Double(0.0) );
			Double gmUsed = (Double) ObjectUtils.defaultIfNull( yearsGmSum.get(year) , new Double(0.0) );
			Double wmUsed = (Double) ObjectUtils.defaultIfNull( yearsWmSum.get(year) , new Double(0.0) );
			Double totalUsed = emUsed + gmUsed + wmUsed;
			
			NumberFormat numberFormatter = new DecimalFormat("###,###,###,###.##");
			
			HashMap<String,Object> used = new HashMap<String,Object>();
			used.put( "year"		, year );
			used.put( "yearStr"		, year );	//
			used.put( "checked"		, new Boolean(true) );
			used.put( "emUsed"		, numberFormatter.format( emUsed ) + "kWh" );
			used.put( "gmUsed"		, numberFormatter.format( gmUsed ) + "m3" );
			used.put( "wmUsed"		, numberFormatter.format( wmUsed ) + "m3" );
			used.put( "totalUsed"	, numberFormatter.format( totalUsed ) + "TOE" );
			
			result.add( used );
		}
		
		
		return result;
	}
	
	public Map<String, Object> getEnergySavingGoalSaveInfoList(Map<String, Object> params) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		List<HashMap<String, Object>> saveInfoListObj = new ArrayList<HashMap<String, Object>>();
		
		
		List<AverageUsage> saveInfoList = averageUsageDao.getAll();
		
		if( saveInfoList != null ){

			SimpleDateFormat dateFormatter1 = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat dayFormatter = new SimpleDateFormat("yyyy-MM-dd");
			NumberFormat numFormatter = new DecimalFormat("###,###,###,###.##");
			
			for( int i=0; i<saveInfoList.size(); i++){
				
				HashMap<String,Object> info = new HashMap<String,Object>();
				String years = "";
				AverageUsage au = saveInfoList.get(i);
				info.put("avgUsageYear" , numFormatter.format( au.getAvgUsageYear() ) + "TOE" );
				try {
					info.put("createDate" , dayFormatter.format( dateFormatter1.parse( au.getCreateDate() ) ) );
				} catch (ParseException e) {
					e.printStackTrace();
				}
				info.put("descr" , au.getDescr() );
				info.put("id" , au.getId() );
				info.put("used" , au.getUsed() ? (String)params.get("msgIsUsedYes"):(String)params.get("msgIsUsedNo") );
				info.put("years" , au.getBasesToString());
				
				saveInfoListObj.add( info );
			}
			
		}
		
		result.put( "saveInfoList" , saveInfoListObj );
//		result.put( "saveInfoList" , saveInfoList );

		return result;
	}

	
	
	public Map<String, Object> getEnergySavingGoalGoalList(Map<String, Object> params) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		List<HashMap<String, Object>> goalListObj = new ArrayList<HashMap<String, Object>>();
		
		List<EnergySavingGoal> goalList = energySavingGoalDao.getAll();

		if( goalList != null ){

			SimpleDateFormat dateFormatter1 = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			NumberFormat numFormatter = new DecimalFormat("###,###,###,###.##");
			
			for( int i=0; i<goalList.size(); i++){
				
				HashMap<String,Object> info = new HashMap<String,Object>();
				EnergySavingGoal esg = goalList.get(i);
				try {
					info.put("startDate" , dateFormatter.format( dateFormatter1.parse( esg.getId().getStartDate() ) ) );
					info.put("createDate" , dateFormatter.format( dateFormatter1.parse( esg.getId().getCreateDate() ) ) );
				} catch (ParseException e) {
					e.printStackTrace();
				}
				info.put("supplier" , esg.getSupplier().getId() );
				info.put("savingGoal" , esg.getSavingGoal() + "%" );
				info.put("averageUsage" , numFormatter.format( esg.getAverageUsage().getAvgUsageYear() ) + "TOE" );
				info.put("averageUsageId" , esg.getAverageUsage().getId() );
				
				goalListObj.add( info );
			}
			
		}

		result.put( "goalList" , goalListObj );
		
		return result;
	}
	
	
	public Map<String, Object> setEnergyAvg(Map<String, Object> params){

		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
				String supplierId   = StringUtils.defaultIfEmpty( (String)params.get("supplierId") , "0" );
				String years   		= StringUtils.defaultIfEmpty( (String)params.get("years") , "" ); // 평균에 사용할 년도
				String descr   		= StringUtils.defaultIfEmpty( (String)params.get("descr") , "0" ); // 설명
				String used  		= StringUtils.defaultIfEmpty( (String)params.get("used") , "false" ); // 사용유무
				String avgInfoId  	= (String)params.get("avgInfoId");
				
				String[] yearList = StringUtils.split( years , "," ); 
				
				// 1. AverageUsage 테이블에 설명 , 사용유무 정보만 insert하고 key값을 구한다.
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
				Calendar c = Calendar.getInstance();
				String createDate = dateFormat.format(c.getTime());
				
				AverageUsage resultAu = new AverageUsage();
				Integer resultAuKey = null;
				
				AverageUsage au = new AverageUsage();
				au.setCreateDate( createDate );
				au.setDescr( descr );
				au.setUsed( Boolean.valueOf( used ) );
				if( !avgInfoId.isEmpty() ){
					
					au.setId( Integer.parseInt( avgInfoId ) );
					
					averageUsageDao.updateSql(au);
					resultAuKey = Integer.parseInt( avgInfoId );
				}else{
					
					if( au != null && au.getUsed() ){
						
						averageUsageDao.usageInitSql(au);
					}
					
					resultAu = averageUsageDao.add(au);
					resultAuKey = resultAu.getId();
				}
				
//				averageUsageDao.saveOrUpdate(au);
//				averageUsageDao.update(au);
//				averageUsageDao.add(au);
				
				// 2. 년도별 , 에너지 타입별(전기,가스,수도) 정보를 구하여 AverageUseageBase테이블에 insert 한다.
				
				String locationId			= null;
				Integer[] locationChildren 	= null;
				
				ArrayList<Location> rootLocation = (ArrayList<Location>) locationDao.getParents( Integer.parseInt( supplierId ) );
				if( rootLocation != null ){
					
					locationId = String.valueOf( rootLocation.get(0).getId() );
					
					Iterator a = rootLocation.get(0).getChildren().iterator();
					locationChildren = new Integer[rootLocation.get(0).getChildren().size()];
					int b = 0;
					while( a.hasNext() ){
						Location z = (Location) a.next();
						locationChildren[b] = z.getId(); 
						++b;
					}
					
				}

				Condition cdtLocationChildren = new Condition("location.id", locationChildren , null, Restriction.IN);
				
				if( !avgInfoId.isEmpty() ){ // update 하는경우 이전에 등록된 정보를 삭제한후 다시 insert함.

					AverageUsageBase aubDel = new AverageUsageBase();
					aubDel.setAvgUsageId(resultAuKey);
					averageUsageBaseDao.deleteAvgUsageId( aubDel );
				}
				
				Double sum = 0.0; 
				Double sumCo2 = 0.0; 
				Double sumAvg = 0.0; 
				Double sumCo2Avg = 0.0; 
				List<AverageUsageBase> bases = new ArrayList<AverageUsageBase>();

				for( int i=0; i < yearList.length; i++ ){
					String year = yearList[i].toString();

					SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
					SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy");
					Calendar c1 = Calendar.getInstance();
					c1.set( Integer.parseInt( year ) , 0 , 1 );
					String startDate = formatter.format( c1.getTime() );
					c1.set( Integer.parseInt( year ) , 11 , 1 );
					String endDate = formatter.format( c1.getTime() );
					
					Double sumEm	= dayMonthMaxMinAvgSum( cdtLocationChildren , "EM" , "1.3.1.1" , 1 , "month" , "sum" , startDate , endDate );
					Double sumEmCo2	= dayMonthMaxMinAvgSum( cdtLocationChildren , "EM" , "1.3.1.1" , 0 , "month" , "sum" , startDate , endDate );

					Double sumWm	= dayMonthMaxMinAvgSum( cdtLocationChildren , "WM" , "1.3.1.2" , 1 , "month" , "sum" , startDate , endDate );
					Double sumWmCo2 = dayMonthMaxMinAvgSum( cdtLocationChildren , "WM" , "1.3.1.2" , 0 , "month" , "sum" , startDate , endDate );

					Double sumGm	= dayMonthMaxMinAvgSum( cdtLocationChildren , "GM" , "1.3.1.3" , 1 , "month" , "sum" , startDate , endDate );
					Double sumGmCo2	= dayMonthMaxMinAvgSum( cdtLocationChildren , "GM" , "1.3.1.3" , 0 , "month" , "sum" , startDate , endDate );
					
					sum = sumEm + sumWm + sumGm;
					sumCo2 = sumEmCo2 + sumWmCo2 + sumGmCo2;
					
					AverageUsageBase aubEm = new AverageUsageBase();
					aubEm.id.setAvgUsageId( resultAuKey );
					aubEm.id.setUsageYear( formatter1.format ( c1.getTime() ) );
					aubEm.id.setSupplyType( 0 ); // 전기
					aubEm.setUsageValue(sumEm);
					aubEm.setCo2Value(sumEmCo2);
					
					AverageUsageBase aubGm = new AverageUsageBase();
					aubGm.id.setAvgUsageId( resultAuKey );
					aubGm.id.setUsageYear( formatter1.format ( c1.getTime() ) );
					aubGm.id.setSupplyType( 1 ); // 가스
					aubGm.setUsageValue(sumGm);
					aubGm.setCo2Value(sumGmCo2);
					
					AverageUsageBase aubWm = new AverageUsageBase();
					aubWm.id.setAvgUsageId( resultAuKey );
					aubWm.id.setUsageYear( formatter1.format ( c1.getTime() ) );
					aubWm.id.setSupplyType( 2 ); // 수도
					aubWm.setUsageValue(sumWm);
					aubWm.setCo2Value(sumWmCo2);
					
					
					AverageUsageBase aubE = averageUsageBaseDao.saveOrUpdate( aubEm );
					AverageUsageBase aubG = averageUsageBaseDao.saveOrUpdate( aubGm );
					AverageUsageBase aubW = averageUsageBaseDao.saveOrUpdate( aubWm );
					
					bases.add( aubE );
					bases.add( aubG );
					bases.add( aubW );
				}
				
				
				// 3. 에너지 , 년도를 합산한 평균값을 이용하여 AverageUseage에 년/월/일의 평균값을 입력한다.
				sumAvg = sum / yearList.length;
				sumCo2Avg = sumCo2 / yearList.length;
				
				Calendar c2 = Calendar.getInstance();
				c2.set(c2.get(Calendar.YEAR), 11, 31);
				
				resultAu.setAvgUsageYear( sumAvg );
				resultAu.setAvgUsageMonth( sumAvg / 12 );
				resultAu.setAvgUsageWeek( sumAvg / ( c2.getMaximum(Calendar.WEEK_OF_YEAR)-1 ) );
				resultAu.setAvgUsageDay( sumAvg / 365);
				resultAu.setAvgCo2Year( sumCo2Avg );
				resultAu.setAvgCo2Month( sumCo2Avg / 12 );
				resultAu.setAvgCo2Week( sumCo2Avg / ( c2.getMaximum(Calendar.WEEK_OF_YEAR)-1 ) );
				resultAu.setAvgCo2Day( sumCo2Avg / 365);
				if( !avgInfoId.isEmpty() ){
					
					resultAu.setId( resultAuKey );
					resultAu.setBases(bases);
//					averageUsageDao.update( resultAu );
					averageUsageDao.updateSql( resultAu );
				}else{
					
					averageUsageDao.add( resultAu );
				}
				
				
//				averageUsageDao.update( auObj );
//				averageUsageDao.saveOrUpdate( resultAu );
				
				result.put("result", "Y");
			
		}catch(Exception e){
			e.printStackTrace();
			result.put("result", "N");
		}
		
		return result;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//정렬하기
	       Vector vc = new Vector();
	       vc.addElement("01");
	       vc.addElement("03");
	       vc.addElement("01");
	       vc.addElement("02");
	       
	       Collections.sort(vc);
	       System.out.println("ComparableComparator");
	       for(int i=0 ; i< vc.size() ; i++){
	    	   System.out.println("vc.get("+i+")"+ vc.get(i));
	       }

	       
	       
	       
	       
	        Collections.sort(vc,new org.apache.commons.collections.comparators.ComparableComparator());
	       System.out.println("ComparableComparator");
	       for(int i=0 ; i< vc.size() ; i++){
	           System.out.println("vc.get("+i+")"+ vc.get(i));
	       }
	       Collections.sort(vc,new org.apache.commons.collections.comparators.NullComparator());
	       System.out.println("NullComparator");
	       for(int i=0 ; i< vc.size() ; i++){
	           System.out.println("vc.get("+i+")"+ vc.get(i));
	       }
	       Collections.sort(vc,new org.apache.commons.collections.comparators.ReverseComparator());
	       System.out.println("ReverseComparator");
	       for(int i=0 ; i< vc.size() ; i++){
	           System.out.println("vc.get("+i+")"+ vc.get(i));
	       }
		
		
		
		
		
		
		
		
		List<Object> temp = new ArrayList<Object>();
		List<Object> A = new ArrayList<Object>();
		A.add("2010");
		A.add("2008");
		A.add("2009");
		List<Object> B = new ArrayList<Object>();
		B.add("2005");
		B.add("2008");
		B.add("2001");
		List<Object> C = new ArrayList<Object>();
		C.add("2009");
		C.add("2000");
		
		temp.addAll(A);
		temp.addAll(B);
		temp.addAll(C);
		
		System.out.println(temp.toString());


		List<Object> nana = new ArrayList<Object>();
		String[] arrStr = ( String[]) temp.toArray( new String[ temp.size()]);
		Arrays.sort( arrStr , String.CASE_INSENSITIVE_ORDER);

		for( int i=0; i<arrStr.length; i++){
			
			if( i == 0 ) {
				nana.add( arrStr[i] );
			}
			if( i>0 && !arrStr[i-1].equals(arrStr[i]) ){
				
//				System.out.println(arrStr[i]);
				nana.add( arrStr[i] );
			}
		}
		System.out.println("#" + nana.toString());
		

		List<Object> temp1 = new ArrayList<Object>();
		List<HashMap> A1 = new ArrayList<HashMap>();
		A1.add( (HashMap) new HashMap().put("year", "2010"));
		A1.add( (HashMap) new HashMap().put("year", "2008"));
		A1.add( (HashMap) new HashMap().put("year", "2009"));
		List<HashMap> B1 = new ArrayList<HashMap>();
		B1.add( (HashMap) new HashMap().put("year", "2005"));
		B1.add( (HashMap) new HashMap().put("year", "2008"));
		B1.add( (HashMap) new HashMap().put("year", "2001"));
		List<HashMap> C1 = new ArrayList<HashMap>();
		C1.add( (HashMap) new HashMap().put("year", "2009"));
		C1.add( (HashMap) new HashMap().put("year", "2000"));
		

		temp1.addAll(A1);
		temp1.addAll(B1);
		temp1.addAll(C1);
		
		System.out.println(temp1.toString());
		
		
//		Collections.sort( temp1 );
		
		
		
		
	  SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
	  SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMM");

	      
	      
	  String startDate = "20100108";

	  int y = Integer.parseInt(startDate.substring(0,4));
	  int m = Integer.parseInt(startDate.substring(4,6));
	  int d = Integer.parseInt(startDate.substring(6,8));

	  System.out.println( " 현재 날짜 : " + startDate );
	  
	  
	  Calendar getOldDay = Calendar.getInstance();
	  getOldDay.set( y , m-1 , d );
	  getOldDay.set( getOldDay.get(Calendar.YEAR) , getOldDay.get(Calendar.MONTH) , getOldDay.get(Calendar.DATE) - 1 );
  	  System.out.println( "[" + startDate + "] " + "전일 날짜 : " + formatter.format( getOldDay.getTime() ));
	
	  Calendar getOldDayYear = Calendar.getInstance();
	  getOldDayYear.set( y , m-1 , d );
	  getOldDayYear.set( getOldDayYear.get(Calendar.YEAR) - 1 , getOldDayYear.get(Calendar.MONTH) , getOldDayYear.get(Calendar.DATE) );
	  System.out.println( "[" + startDate + "] " + "전년 동일 날짜 : " + formatter.format( getOldDayYear.getTime() ));

	  System.out.println( "------------------");
	  

	  Calendar getOldWeek = Calendar.getInstance();
	  getOldWeek.set( y , m-1 , d );
	  getOldWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 해당 주의 일요일로 셋팅. 
	  System.out.println( "[" + startDate + "] " + "금주 일요일 날짜 : " + formatter.format( getOldWeek.getTime() ));
	  getOldWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // 해당 주의 토요일로 셋팅. 
	  System.out.println( "[" + startDate + "] " + "금주 토요일 날짜 : " + formatter.format( getOldWeek.getTime() ));
	  
	  getOldWeek.set(Calendar.WEEK_OF_YEAR, getOldWeek.get(Calendar.WEEK_OF_YEAR) - 1); // calendar를 전주로 셋팅
	  getOldWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 해당 주의 일요일로 셋팅. 
	  System.out.println( "[" + startDate + "] " + "전주 일요일 날짜 : " + formatter.format( getOldWeek.getTime() ));
	  getOldWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // 해당 주의 토요일로 셋팅. 
	  System.out.println( "[" + startDate + "] " + "전주 토요일 날짜 : " + formatter.format( getOldWeek.getTime() ));

	  Calendar getOldWeekYear = Calendar.getInstance();
	  getOldWeekYear.set( y , m-1 , d );
	  getOldWeekYear.set(getOldWeekYear.get(Calendar.YEAR) - 1 ,getOldWeekYear.get(Calendar.MONTH) , getOldWeekYear.get(Calendar.DATE) );
	  getOldWeekYear.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 해당 주의 일요일로 셋팅. 
	  System.out.println( "[" + startDate + "] " + "전년 동주 일요일 날짜 : " + formatter.format( getOldWeekYear.getTime() ));
	  getOldWeekYear.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // 해당 주의 토요일로 셋팅. 
	  System.out.println( "[" + startDate + "] " + "전년 동주 토요일 날짜 : " + formatter.format( getOldWeekYear.getTime() ));
	  System.out.println( "------------------");


	  Calendar getOldMonth = Calendar.getInstance();
	  getOldMonth.set( y , m-1 , d );
	  getOldMonth.set( getOldMonth.get(Calendar.YEAR) , getOldMonth.get(Calendar.MONTH) - 1 , 1 );
	  System.out.println( "[" + startDate + "] " + "전월 시작 날짜 : " + formatter.format( getOldMonth.getTime() ));
	  getOldMonth.set( getOldMonth.get(Calendar.YEAR) , getOldMonth.get(Calendar.MONTH) , getOldMonth.getActualMaximum(Calendar.DAY_OF_MONTH) );
	  System.out.println( "[" + startDate + "] " + "전월 마지막 날짜 : " + formatter.format( getOldMonth.getTime() ));
	  
	  Calendar getOldMonthYear = Calendar.getInstance();
	  getOldMonthYear.set( y , m-1 , d );
	  getOldMonthYear.set( getOldMonthYear.get(Calendar.YEAR) - 1 , getOldMonthYear.get(Calendar.MONTH) , 1 );
	  System.out.println( "[" + startDate + "] " + "전년 동월 시작날짜 : " + formatter.format( getOldMonthYear.getTime() ));
	  getOldMonthYear.set( getOldMonthYear.get(Calendar.YEAR) , getOldMonthYear.get(Calendar.MONTH) , getOldMonthYear.getActualMaximum(Calendar.DAY_OF_MONTH) );
	  System.out.println( "[" + startDate + "] " + "전년 동월 마지막 날짜 : " + formatter.format( getOldMonthYear.getTime() ));
	  
	  System.out.println( "------------------");

	  Calendar getOldYear = Calendar.getInstance();
	  getOldYear.set( y , m-1 , d );
	  getOldYear.set(getOldYear.get(Calendar.YEAR) - 1 ,getOldYear.get(Calendar.MONTH) , getOldYear.get(Calendar.DATE) );
	  System.out.println( "[" + startDate + "] " + "전년 날짜 : " + formatter.format( getOldYear.getTime() ));

	  Calendar getOldYearYear = Calendar.getInstance();
	  getOldYearYear.set( y , m-1 , d );
	  getOldYearYear.set(getOldYearYear.get(Calendar.YEAR) - 2 ,getOldYearYear.get(Calendar.MONTH) , getOldYearYear.get(Calendar.DATE) );
	  System.out.println( "[" + startDate + "] " + "전전년  날짜 : " + formatter.format( getOldYearYear.getTime() ));
	  
	  System.out.println( "------------------");
	  
	  
	  
//	  Calendar getOldMonth = Calendar.getInstance();
//	  getOldMonth.set( y , m-1 , d );
//	  getOldMonth.set( y , getOldMonth.get(Calendar.MONTH) - 1 , d );
//	  System.out.println( " 전월 날짜 : " + formatter.format( getOldMonth.getTime() ));
	  
//	  getOldWeek.set(Calendar.WEEK_OF_YEAR, getOldWeek.get(Calendar.WEEK_OF_YEAR) - 1); // calendar를 전주로 셋팅
//	  System.out.println( "1 " + getOldWeek.get(Calendar.WEEK_OF_YEAR));
//	  getOldWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 해당 주의 일요일로 셋팅. 
//	  System.out.println( "2 일주일의 시작이 일요일인경우 -> 전주 일요일의 날짜 : " + formatter.format( getOldWeek.getTime() ));  // 23일
//	  getOldWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); 
//	  System.out.println( "3 일주일의 시작이 일요일인경우 -> 전주 월요일의 날짜 : " + formatter.format( getOldWeek.getTime() ));  // 29일
	  
	    
	  
	  
//	  System.out.println( getSavingPercentage( 33000 , 22000) );


	}


	@Override
	public Map<String, Object> setEnergySavingGoalByParam(String supplierId,
			Double savingGoal, String savingGoalStartDate) {
		Map<String, Object> condition = new HashMap<String, Object>();
	    condition .put("supplierId", supplierId);
	    condition .put("savingGoal", savingGoal);
	    condition .put("savingGoalStartDate", savingGoalStartDate);
	    return setEnergySavingGoal(condition);
	}


	@Override
	public Map<String, Object> getEnergySavingGoalInfoByParam(
			String searchStartDate, String searchEndDate,
			String searchDateType, String supplierId, String savingGoal,
			String savingGoalStartDate) {
		Map<String, Object> condition = new HashMap<String, Object>();
	    condition .put("searchStartDate", searchStartDate);
	    condition .put("searchEndDate", searchEndDate);
	    condition .put("searchDateType", searchDateType);
	    condition .put("supplierId", supplierId);
	    condition .put("savingGoal", savingGoal);
	    condition .put("savingGoalStartDate", savingGoalStartDate);
	    return getEnergySavingGoalInfo(condition);
	}


	@Override
	public Map<String, Object> getEnergySavingGoalYearsUsedByParam(
			String searchStartDate, String searchEndDate, String supplierId) {
		Map<String, Object> condition = new HashMap<String, Object>();
	    condition .put("searchStartDate", searchStartDate);
	    condition .put("searchEndDate", searchEndDate);
	    condition .put("supplierId", supplierId);
	    return getEnergySavingGoalYearsUsed(condition);
	}


	@Override
	public Map<String, Object> getEnergySavingGoalAvgYearsUsedByParam(
			List<Object> years, String supplierId) {
		Map<String, Object> condition = new HashMap<String, Object>();
	    condition .put("years", years);
	    condition .put("supplierId", supplierId);
	    return getEnergySavingGoalAvgYearsUsed(condition);
	}


	@Override
	public Map<String, Object> getEnergySavingGoalSaveInfoListByParam(
			Boolean msgIsUsedYes, Boolean msgIsUsedNo) {
		Map<String, Object> condition = new HashMap<String, Object>();
	    condition .put("msgIsUsedYes", msgIsUsedYes);
	    condition .put("msgIsUsedNo", msgIsUsedNo);
	    return getEnergySavingGoalSaveInfoList(condition);
	}


	@Override
	public Map<String, Object> setEnergyAvgByParam(String supplierId,
			Double years, String descr, Boolean used, Boolean avgInfoId) {
		Map<String, Object> condition = new HashMap<String, Object>();
	    condition .put("supplierId", supplierId);
	    condition .put("years", years);
	    condition .put("descr", descr);
	    condition .put("used", used);
	    condition .put("avgInfoId", avgInfoId);
	    return setEnergyAvg(condition);
	}

}
