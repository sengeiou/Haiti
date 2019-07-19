package com.aimir.service.system.impl;

import java.io.IOException;
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
import java.util.Properties;
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
import com.aimir.dao.mvm.DayHMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.dao.mvm.MonthHMDao;
import com.aimir.dao.mvm.MonthWMDao;
import com.aimir.dao.system.AverageUsageBaseDao;
import com.aimir.dao.system.AverageUsageDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.EnergySavingGoal2Dao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.device.Meter;
import com.aimir.model.system.AverageUsage;
import com.aimir.model.system.AverageUsageBase;
import com.aimir.model.system.Code;
import com.aimir.model.system.EnergySavingGoal2;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.EnergySavingGoal2Manager;
import com.aimir.util.Condition;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.TimeUtil;

@WebService(endpointInterface = "com.aimir.service.system.EnergySavingGoal2Manager")
@Service(value = "energySavingGoal2Manager")
@Transactional
public class EnergySavingGoal2ManagerImpl implements EnergySavingGoal2Manager {

	Log logger = LogFactory.getLog(EnergySavingGoal2ManagerImpl.class);

	@Autowired
	LocationDao locationDao;

	@Autowired
	EnergySavingGoal2Dao energySavingGoal2Dao;

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
	DayHMDao dayHMDao;

	@Autowired
	MonthHMDao monthHMDao;

	@Autowired
	AverageUsageDao averageUsageDao;

	@Autowired
	AverageUsageBaseDao averageUsageBaseDao;
	
	@Autowired
	SupplierDao supplierDao;

	public Map<String, Object> setEnergySavingGoal2(Map<String, Object> params) {

		Map<String, Object> result = new HashMap<String, Object>();
		try {

			// 사용가능한 평균값 정보가 있는지 조회하여 목표사용량 등록 가능여부를 판단.
			AverageUsage au = averageUsageDao.getAverageUsageByUsed();

			if (au != null) {

				String supplierId = StringUtils.defaultIfEmpty((String) params
						.get("supplierId"), "0");
				String energyType = StringUtils.defaultIfEmpty((String) params
						.get("energyType"), ""); // 에너지 타입 ( 전기 : 0 , 가스 : 1 ,
													// 수도 : 2 )
				String savingGoalDateType = StringUtils.defaultIfEmpty(
						(String) params.get("savingGoalDateType"), ""); // 주기 타입
																		// ( 1 :
																		// 일, 3
																		// : 주,
																		// 4 :
																		// 월, 8
																		// : 년 )
				String savingGoalStartDate = StringUtils.defaultIfEmpty(
						(String) params.get("savingGoalStartDate"), ""); // 기준일
				String savingGoal = StringUtils.defaultIfEmpty((String) params
						.get("savingGoal"), "0"); // 절감목표량
 
				
				Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
				
				if (!energyType.isEmpty() && !savingGoalDateType.isEmpty()) {

					SimpleDateFormat inFormat = new SimpleDateFormat(
							"yyyy-MM-dd");
					SimpleDateFormat outFormat = new SimpleDateFormat(
							"yyyyMMdd");
					String savingGoalStartDateS = "";
					
					try {
						
						outFormat.parse(savingGoalStartDate);
						savingGoalStartDateS = outFormat.format(outFormat
								.parse(savingGoalStartDate));
						
					
					} catch (ParseException pe) {
						
						
						savingGoalStartDateS=  TimeLocaleUtil.getDBDate(savingGoalStartDate, 8, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
						
					}

//					System.out.println("########savingGoalStartDateS:"+savingGoalStartDateS);
					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyyMMdd");
					Calendar c = Calendar.getInstance();
					String createDate = dateFormat.format(c.getTime());

					EnergySavingGoal2 esg = new EnergySavingGoal2();
					esg.id.setCreateDate(createDate);
					esg.id.setStartDate(savingGoalStartDateS);
					esg.setSupplier(new Supplier(Integer.parseInt(supplierId)));
					esg.setSavingGoal(Double.valueOf(savingGoal));
					esg.setAverageUsage(au);

					esg.id.setSupplyType(Integer.parseInt(energyType));
					if (CommonConstants.DateType.DAILY.getCode().equals(
							savingGoalDateType)) {

						esg.id.setDateType(CommonConstants.DateType.DAILY);
					} else if (CommonConstants.DateType.WEEKLY.getCode()
							.equals(savingGoalDateType)) {

						esg.id.setDateType(CommonConstants.DateType.WEEKLY);
					} else if (CommonConstants.DateType.MONTHLY.getCode()
							.equals(savingGoalDateType)) {

						esg.id.setDateType(CommonConstants.DateType.MONTHLY);
					} else if (CommonConstants.DateType.YEARLY.getCode()
							.equals(savingGoalDateType)) {

						esg.id.setDateType(CommonConstants.DateType.YEARLY);
					}

					energySavingGoal2Dao.saveOrUpdate(esg);

					result.put("result", "Y");
				} else {

					logger.warn("energyType or savingGoalDateType is empty!!");
					result.put("result", "E");
				}
			} else {

				result.put("result", "E"); // Empty
			}

		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", "N");
		}

		return result;
	}

	public Map<String, Object> getEnergySavingGoal2Info(
			Map<String, Object> params) {

		if (params.get("requestType") != null) {
			System.out.println();
		}

		List<Object> returnLocation = new ArrayList<Object>();
		Map<String, Object> supplierInfo = new HashMap<String, Object>();
		Map<String, Object> averageInfo = new HashMap<String, Object>(); // 평균
																			// 정보
		Map<String, Object> energyInfoNow = new HashMap<String, Object>(); // 당일
																			// 정보
		Map<String, Object> energyInfoBefore = new HashMap<String, Object>(); // 직전
																				// 정보
		Map<String, Object> energyInfobeforeYear = new HashMap<String, Object>(); // 전년
																					// 정보

		try {

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Calendar c = Calendar.getInstance();
			// System.out.println("##############################2010.07.07일 셋팅! TEST 가 끝나면 지울것!![getTotalUseOfSearchType]");
			// c.set(2010, 6, 7); // 2010.07.07일 셋팅! TEST 가 끝나면 지울것!!
			String currentDate = dateFormat.format(c.getTime()); // 오늘 날짜

			String locationId = null;
			Integer[] locationChildren = null;

			String startDate = StringUtils.defaultIfEmpty((String) params
					.get("searchStartDate"), "");
			String endDate = StringUtils.defaultIfEmpty((String) params
					.get("searchEndDate"), "");
			String searchDateType = StringUtils.defaultIfEmpty((String) params
					.get("searchDateType"), "1");// 주기 타입 ( 1 : 일, 3 : 주, 4 : 월,
													// 8 : 년 )
			String supplierId = StringUtils.defaultIfEmpty((String) params
					.get("supplierId"), "0");
			String energyType = StringUtils.defaultIfEmpty((String) params
					.get("energyType"), ""); // 에너지 타입 ( 전기 : 0 , 가스 : 1 , 수도 :
												// 2 )
			String savingGoalStartDate = StringUtils.defaultIfEmpty(
					(String) params.get("savingGoalStartDate"), ""); // 기준일
			String savingGoal = StringUtils.defaultIfEmpty((String) params
					.get("savingGoal"), "0"); // 절감목표량
			String meterTypeCode = "";

			String requestType = StringUtils.defaultIfEmpty((String) params
					.get("requestType"), ""); // request to Flex

			String supplyType = "";
			if ("0".equals(energyType)) {

				supplyType = "EM";
				meterTypeCode = "1.3.1.1";
			} else if ("1".equals(energyType)) {

				supplyType = "GM";
				meterTypeCode = "1.3.1.3";
			} else if ("2".equals(energyType)) {

				supplyType = "WM";
				meterTypeCode = "1.3.1.2";
			}else if ("3".equals(energyType)) {

				supplyType = "HM";
				meterTypeCode = "1.3.1.4";
			}

			if ("0".equals(searchDateType)) {
				searchDateType = "1";
			}

			if ("".equals(startDate)) {
				startDate = currentDate;
			}
			if ("".equals(endDate)) {
				endDate = currentDate;
			}

			ArrayList<Location> rootLocation = (ArrayList<Location>) locationDao
					.getParents(Integer.parseInt(supplierId));
			if (rootLocation != null) {

				locationId = String.valueOf(rootLocation.get(0).getId());

				Iterator a = rootLocation.get(0).getChildren().iterator();
				locationChildren = new Integer[rootLocation.get(0)
						.getChildren().size()];
				int b = 0;
				while (a.hasNext()) {
					Location z = (Location) a.next();
					locationChildren[b] = z.getId();
					++b;
				}

			}

			supplierInfo.put("supplierId", supplierId);
			supplierInfo.put("locationId", locationId);

			int y = 0;
			int m = 0;
			int d = 0;
			
			try {
				y = Integer.parseInt(startDate.substring(0, 4));
				m = Integer.parseInt(startDate.substring(4, 6));
				d = Integer.parseInt(startDate.substring(6, 8));
			} catch(Exception e ) {
				/**
				 * Exception
				 * 가끔 (yy. M. d) 형식으로 날짜 데이터가 넘어왔을 경우, (yyyyMMdd) 형식으로 변환.
				 */
//				e.printStackTrace();				
				SimpleDateFormat dfInput = new SimpleDateFormat("yy. M. d");
				SimpleDateFormat dfOutput = new SimpleDateFormat("yyyyMMdd");
				startDate = dfOutput.format(dfInput.parse(startDate));
				
				y = Integer.parseInt(startDate.substring(0, 4));
				m = Integer.parseInt(startDate.substring(4, 6));
				d = Integer.parseInt(startDate.substring(6, 8));
			}

			// 1. 주기별 사용량 조회
			// 일(시간)
			if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {

				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

				Calendar getDay = Calendar.getInstance();
				getDay.set(y, m - 1, d);
				String dateS = formatter.format(getDay.getTime());
				// logger.debug( "오늘 날짜 : " + dateS );

				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("searchDateType", searchDateType);
				condition.put("energyType", supplyType);
				condition.put("meterTypeCode", meterTypeCode);
				condition.put("supplierId", supplierId);
				condition.put("locationId", locationId);
				condition.put("locationChildren", locationChildren);
				condition.put("startDate", dateS);
				condition.put("endDate", dateS);
				condition.put("savingGoalStartDate", savingGoalStartDate);
				condition.put("requestType", requestType);

				averageInfo = averageEnergy(condition);
				energyInfoNow = energyDay(condition, averageInfo);

				// 주
			} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
					searchDateType)) {

				// String startEnd[] = getThisWeekStartEndDay( y , m , d );
				//				
				// String weekS = startEnd[0];
				// String weekE = startEnd[1];

				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("searchDateType", searchDateType);
				condition.put("energyType", supplyType);
				condition.put("meterTypeCode", meterTypeCode);
				condition.put("supplierId", supplierId);
				condition.put("locationId", locationId);
				condition.put("locationChildren", locationChildren);
				condition.put("startDate", startDate);
				condition.put("endDate", endDate);
				condition.put("savingGoalStartDate", savingGoalStartDate);
				condition.put("requestType", requestType);

				averageInfo = averageEnergy(condition);
				energyInfoNow = energyWeek(condition, averageInfo);

				// 월
			} else if (CommonConstants.DateType.MONTHLY.getCode().equals(
					searchDateType)) {

				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
				SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");

				Calendar getMonth = Calendar.getInstance();
				getMonth.set(y, m - 1, d);
				getMonth.set(getMonth.get(Calendar.YEAR), getMonth
						.get(Calendar.MONTH), 1);
				String monthS = formatter.format(getMonth.getTime());
				String monthS1 = formatter1.format(getMonth.getTime());
				// logger.debug( "[" + startDate + "] " + "월 시작 날짜 : " + monthS
				// );

				getMonth.set(getMonth.get(Calendar.YEAR), getMonth
						.get(Calendar.MONTH), getMonth
						.getActualMaximum(Calendar.DAY_OF_MONTH));
				String monthE = formatter.format(getMonth.getTime());
				String monthE1 = formatter1.format(getMonth.getTime());
				// logger.debug( "[" + startDate + "] " + "월 마지막 날짜 : " + monthE
				// );

				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("searchDateType", searchDateType);
				condition.put("energyType", supplyType);
				condition.put("meterTypeCode", meterTypeCode);
				condition.put("supplierId", supplierId);
				condition.put("locationId", locationId);
				condition.put("locationChildren", locationChildren);
				condition.put("startDate", monthS);
				condition.put("endDate", monthE);
				condition.put("savingGoalStartDate", savingGoalStartDate);
				condition.put("requestType", requestType);

				Map<String, Object> condition1 = new HashMap<String, Object>();
				condition1.put("searchDateType", searchDateType);
				condition1.put("energyType", supplyType);
				condition1.put("meterTypeCode", meterTypeCode);
				condition1.put("supplierId", supplierId);
				condition1.put("locationId", locationId);
				condition1.put("locationChildren", locationChildren);
				condition1.put("startDate", monthS1);
				condition1.put("endDate", monthE1);
				condition1.put("savingGoalStartDate", savingGoalStartDate);
				condition1.put("requestType", requestType);

				averageInfo = averageEnergy(condition1);
				energyInfoNow = energyMonth(condition, averageInfo);
				// 년간
			} else if (CommonConstants.DateType.YEARLY.getCode().equals(
					searchDateType)) {

				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
				SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");

				Calendar getYear = Calendar.getInstance();
				getYear.set(y, 0, 1);
				String yearS = formatter.format(getYear.getTime());
				String yearS1 = formatter1.format(getYear.getTime());
				// logger.debug( "[" + startDate + "] " + "년 시작날짜 : " + yearS );

				getYear.set(getYear.get(Calendar.YEAR), getYear
						.getActualMaximum(Calendar.MONTH), getYear
						.getActualMaximum(Calendar.DAY_OF_MONTH));
				String yearE = formatter.format(getYear.getTime());
				String yearE1 = formatter1.format(getYear.getTime());
				// logger.debug( "[" + startDate + "] " + "년 마지막 날짜 : " + yearE
				// );

				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("searchDateType", searchDateType);
				condition.put("energyType", supplyType);
				condition.put("meterTypeCode", meterTypeCode);
				condition.put("supplierId", supplierId);
				condition.put("locationId", locationId);
				condition.put("locationChildren", locationChildren);
				condition.put("startDate", yearS);
				condition.put("endDate", yearE);
				condition.put("savingGoalStartDate", savingGoalStartDate);
				condition.put("requestType", requestType);

				Map<String, Object> condition1 = new HashMap<String, Object>();
				condition1.put("searchDateType", searchDateType);
				condition1.put("energyType", supplyType);
				condition1.put("meterTypeCode", meterTypeCode);
				condition1.put("supplierId", supplierId);
				condition1.put("locationId", locationId);
				condition1.put("locationChildren", locationChildren);
				condition1.put("startDate", yearS1);
				condition1.put("endDate", yearE1);
				condition1.put("savingGoalStartDate", savingGoalStartDate);
				condition1.put("requestType", requestType);

				averageInfo = averageEnergy(condition1);
				energyInfoNow = energyYear(condition, averageInfo);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Object> result = new HashMap<String, Object>();
		// result.put("rootLocation" , supplierInfo);
		// result.put("averageInfo" , averageInfo);
		// result.put("energyInfoNow" , energyInfoNow);
		// result.put("energyInfoBefore" , energyInfoBefore);
		// result.put("energyInfobeforeYear" , energyInfobeforeYear);

		List<Map<String, Object>> resultListTemp = new ArrayList<Map<String, Object>>(); 
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

		HashMap<String, Object> h1 = new HashMap<String, Object>();
		h1.put("gubun", averageInfo.get("energyAvgYearCount")
				+ (String) params.get("msgAvgYear"));
		h1.put("usage", averageInfo.get("energyAvgUse"));

		HashMap<String, Object> h2 = new HashMap<String, Object>();
		h2.put("gubun", (String) params.get("msgGoal"));
		h2.put("usage", averageInfo.get("energyGoalUse"));

		HashMap<String, Object> h3 = new HashMap<String, Object>();
		h3.put("gubun", (String) params.get("msgPrediction"));
		h3.put("usage", averageInfo.get("energyForecastUse"));

		HashMap<String, Object> h4 = new HashMap<String, Object>();
		h4.put("gubun", energyInfoNow.get("gubun"));
		h4.put("usage", energyInfoNow.get("energyUse"));

		resultListTemp.add(0, h4);
		resultListTemp.add(1, h3);
		resultListTemp.add(2, h2);
		resultListTemp.add(3, h1);

		Map<String, Object> chartMap = new HashMap<String, Object>();
		chartMap.put("chartInfo", resultListTemp);

		resultList.add(0, averageInfo);
		resultList.add(1, energyInfoNow);
		resultList.add(2, chartMap);

		result.put("info", resultList);
		result.put("energyType", params.get("energyType"));
		return result;
	}

	/**
	 * 입력받은 주의 시작일이나 종료일이 다른 월에 걸치는 경우 걸치지 않도록 처리하여 시작일과 종료일을 반환
	 * 
	 * @param y
	 * @param m
	 * @param d
	 * @return
	 */
	private String[] getThisWeekStartEndDay(int y, int m, int d) {

		String startEnd[] = new String[2];

		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

		Calendar getWeek = Calendar.getInstance();
		getWeek.set(y, m - 1, d);
		String weekSTemp = formatter1.format(getWeek.getTime());
		getWeek.getTime();
		getWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 해당 주의 일요일로 셋팅.

		// 입력받은 주의 시작일이나 종료일이 다른 월에 걸치는 경우 걸치지 않도록 처리!!
		String weekS = "";
		if (weekSTemp.equals(formatter1.format(getWeek.getTime()))) {

			weekS = formatter.format(getWeek.getTime());
		} else {

			for (int i = 0; i < 7; i++) {
				getWeek.getTime();
				getWeek.set(Calendar.DATE,
						getWeek.get(Calendar.DAY_OF_MONTH) + 1);
				String sss = formatter1.format(getWeek.getTime());
				if (weekSTemp.equals(sss)) {

					weekS = formatter.format(getWeek.getTime());
					break;
				}
			}
		}

		String weekE = "";
		getWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // 해당 주의 토요일로 셋팅.

		if (weekSTemp.equals(formatter1.format(getWeek.getTime()))) {

			weekE = formatter.format(getWeek.getTime());
		} else {

			for (int k = 0; k < 7; k++) {
				getWeek.getTime();
				getWeek.set(Calendar.DATE,
						getWeek.get(Calendar.DAY_OF_MONTH) - 1);

				if (weekSTemp.equals(formatter1.format(getWeek.getTime()))) {

					weekE = formatter.format(getWeek.getTime());
					break;
				}
			}
		}

		weekE = formatter.format(getWeek.getTime());

		startEnd[0] = weekS;
		startEnd[1] = weekE;

		return startEnd;
	}

	/**
	 * 기간 및 에너지 타입에 따른 평균 사용량 , 목표사용량 , 예상 사용량 정보를 반환한다.
	 * 
	 * @param condition
	 * @return
	 */
	private Map<String, Object> averageEnergy(Map<String, Object> condition) {

		Double energyAvgUse = 0.0; // 평균 사용량
		int energyAvgYearCount = 0; // 평균에 추출에 사용된 년수
		Double savingGoal = 0.0; // 절감 목표율
		Double energyGoalUse = 0.0; // 절감 목표량
		Double energyForecastUse = 0.0; // 예상 절감 목표량

		String searchDateType = (String) condition.get("searchDateType"); // 주기
																			// 타입
																			// (
																			// 1
																			// :
																			// 일,
																			// 3
																			// :
																			// 주,
																			// 4
																			// :
																			// 월,
																			// 8
																			// :
																			// 년
																			// )
		String energyType = (String) condition.get("energyType"); // 에너지 타입 ( 전기
																	// : EM , 가스
																	// : GM , 수도
																	// : WM )
		String meterTypeCode = (String) condition.get("meterTypeCode");
		String supplierId = (String) condition.get("supplierId");
		String locationId = (String) condition.get("locationId");
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");

		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("bems_charge.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String patternNum = prop.getProperty("pattern.format.usage");
		
		DecimalFormat df = new DecimalFormat(patternNum);

		String energyTypeTemp = "";
		if ("EM".equals(energyType)) {

			energyTypeTemp = "0";
		} else if ("GM".equals(energyType)) {

			energyTypeTemp = "1";
		} else if ("WM".equals(energyType)) {

			energyTypeTemp = "2";
		} else if ("HM".equals(energyType)) {

			energyTypeTemp = "3";
		}

		List<EnergySavingGoal2> esgList = null;

		AverageUsage averageUsage = averageUsageDao.getAverageUsageByUsed();

		if (((String) condition.get("requestType")).equals("LeftTop")) {
			esgList = energySavingGoal2Dao.getEnergySavingGoal2ListByStartDate(
					searchDateType, energyTypeTemp, endDate, Integer
							.parseInt(supplierId));
		} else {
			esgList = energySavingGoal2Dao
			.getEnergySavingGoal2ListByAverageUsage(searchDateType,
					energyTypeTemp, endDate, Integer
							.parseInt(supplierId), averageUsage.getId());
		}

		if (esgList != null && !esgList.isEmpty()) {

			EnergySavingGoal2 esg = esgList.get(0);
			String years = esg.getAverageUsage().getBasesToString();
			String[] yearList = StringUtils.split(years, ",");

			if (esg.getAverageUsage() != null && !StringUtils.isEmpty(years)
					&& years.length() >= 4) {

				energyAvgYearCount = yearList.length;

				int channel = 1; // 탄소배출량 : 0 , 사용량 : 1

				// 일(시간)
				if (CommonConstants.DateType.DAILY.getCode().equals(
						searchDateType)) {

					energyAvgUse = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, channel, "day",
							startDate, endDate);
					energyForecastUse = dayForecastSum(condition,"day");
					// 주
				} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
						searchDateType)) {

					energyAvgUse = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, channel, "day",
							startDate, endDate);
					energyForecastUse = weekForecastSum(condition,"week");
					// 월
				} else if (CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)) {

					energyAvgUse = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, channel, "month",
							startDate, endDate);
					energyForecastUse = monthForecastSum(condition,"month");
					// 년간
				} else if (CommonConstants.DateType.YEARLY.getCode().equals(
						searchDateType)) {

					energyAvgUse = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, channel, "month",
							startDate, endDate);
					energyForecastUse = yearForecastSum(condition,"year");
				}
			} else {

				logger
						.error("energySavingGoal2에AverageUsage 키값이 존재하지 않습니다!! (energySavingGoal2은 반드시 AverageUsage 정보를 가지고 있어야한다.)");
			}

			// 절감 목표율
			savingGoal = esg.getSavingGoal();

			// 목표 사용량.
			energyGoalUse = energyAvgUse * ((100 - savingGoal) * 0.01);

			// 예상 사용량.
			energyForecastUse = energyForecastUse;

		} else {
			String years = averageUsage.getBasesToString();
			String[] yearList = StringUtils.split(years, ",");

			if (averageUsage != null && !StringUtils.isEmpty(years)
					&& years.length() >= 4) {

				energyAvgYearCount = yearList.length;

				int channel = 1; // 탄소배출량 : 0 , 사용량 : 1

				// 일(시간)
				if (CommonConstants.DateType.DAILY.getCode().equals(
						searchDateType)) {

					energyAvgUse = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, channel, "day",
							startDate, endDate);
					// 주
				} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
						searchDateType)) {

					energyAvgUse = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, channel, "day",
							startDate, endDate);
					// 월
				} else if (CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)) {

					energyAvgUse = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, channel, "month",
							startDate, endDate);
					// 년간
				} else if (CommonConstants.DateType.YEARLY.getCode().equals(
						searchDateType)) {

					energyAvgUse = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, channel, "month",
							startDate, endDate);
				}
			} else {

				logger
						.error("energySavingGoal2에AverageUsage 키값이 존재하지 않습니다!! (energySavingGoal2은 반드시 AverageUsage 정보를 가지고 있어야한다.)");
			}
			
			esgList = energySavingGoal2Dao.getEnergySavingGoal2ListByStartDate(
					searchDateType, energyTypeTemp, endDate, Integer
							.parseInt(supplierId));
			if (esgList != null && !esgList.isEmpty()) {
				EnergySavingGoal2 esg = esgList.get(0);
				
				// 절감 목표율
				savingGoal = esg.getSavingGoal();

				// 목표 사용량.
				energyGoalUse = energyAvgUse * ((100 - savingGoal) * 0.01);

				// 예상 사용량.
				energyForecastUse = 0.0;
			} else {
				// 절감 목표율
				savingGoal = 0.0;

				// 목표 사용량.
				energyGoalUse = 0.0;

				// 예상 사용량.
				energyForecastUse = 0.0;
			}

			// // 절감율
			// saving = getSavingPercentage( energyAvgUse , energyUse.intValue()
			// );
		}

		if (((String) condition.get("requestType")).equals("LeftBottom")
				|| (((String) condition.get("requestType")).equals("LeftTop") && energyAvgYearCount == 0)) {
			energyAvgYearCount = averageUsageBaseDao.getSetYearsbyId(
					averageUsage.getId()).size();
		}

		if (energyAvgUse == null || energyAvgUse == 0.0) {
			energyAvgUse = 0.0;
		}

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("energyAvgYearCount", energyAvgYearCount); // 평균에 추출에 사용된
																	// 년수
		resultMap.put("energyAvgUse", Double.parseDouble(df.format(energyAvgUse))); // 평균 사용량
		resultMap.put("energyGoalUse", Double.parseDouble(df.format(energyGoalUse))); // 목표 사용량.
		resultMap.put("energyForecastUse", Double.parseDouble(df.format(energyForecastUse))); // 예상 사용량.
		resultMap.put("savingGoal", savingGoal); // 절감 목표율
		resultMap.put("searchDateType", searchDateType); // // 주기 타입 ( 1 : 일, 3
															// : 주, 4 : 월, 8 : 년
															// )
		return resultMap;
	}

	/**
	 * 에너지 타입에 따른 startDate 와 endDate의 월/일 정보가 동일한 년도들 이용하여 평균값을 추출함.
	 * 
	 * @param supplierId
	 *            : supplierId를 이용하여 최상위 로케이션 을 부모로 하는 로케이션 정보를 추출하여 meter정보를
	 *            추출하기 위한 조건식 생성을 위해 사용됨.
	 * @param yearList
	 * @param energyType
	 *            : 전기 : EM , 가스 : GM , 수도 : WM 열량 : HM
	 * @param meterTypeCode
	 * @param channel
	 *            : 탄소배출량 : 0 , 사용량 : 1
	 * @param dayMonth
	 *            : day검침량 테이블 조회 , month검침량 테이블조회
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private Double dayMonthRangeAvg(String supplierId, String[] yearList,
			String energyType, String meterTypeCode, int channel,
			String dayMonth, String startDate, String endDate) {

		Double returnValue = 0.0;

		Condition cdtLocationChildren = null;

		ArrayList<Location> rootLocation = (ArrayList<Location>) locationDao
				.getParents(Integer.parseInt(supplierId));
		if (rootLocation != null) {

			// String locationId = String.valueOf( rootLocation.get(0).getId()
			// );

			Iterator a = rootLocation.get(0).getChildren().iterator();
			Integer[] locationChildren = new Integer[rootLocation.get(0)
					.getChildren().size()];
			int b = 0;
			while (a.hasNext()) {
				Location z = (Location) a.next();
				locationChildren[b] = z.getId();
				++b;
			}

			cdtLocationChildren = new Condition("location.id",
					locationChildren, null, Restriction.IN);
		}

		int sY = Integer.parseInt(startDate.substring(0, 4));
		int sM = Integer.parseInt(startDate.substring(4, 6));
		int sD = Integer.parseInt(startDate.substring(6, 8));

		int eY = Integer.parseInt(endDate.substring(0, 4));
		int eM = Integer.parseInt(endDate.substring(4, 6));
		int eD = Integer.parseInt(endDate.substring(6, 8));

		Double sum = 0.0;

		int avgYearSize = yearList.length;

		if (avgYearSize > 0) {

			for (int i = 0; i < avgYearSize; i++) {

				String year = yearList[i].toString();

				String startDateTemp = "";
				String endDateTemp = "";
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMM");
				Calendar c = Calendar.getInstance();

				c.set(Integer.parseInt(year), sM - 1, sD);
				if ("day".equals(dayMonth)) {

					startDateTemp = formatter.format(c.getTime());
				} else if ("month".equals(dayMonth)) {

					startDateTemp = formatter1.format(c.getTime());
				}

				c.set(Integer.parseInt(year), eM - 1, eD);
				if ("day".equals(dayMonth)) {

					endDateTemp = formatter.format(c.getTime());
				} else if ("month".equals(dayMonth)) {

					endDateTemp = formatter1.format(c.getTime());
				}

				sum = sum
						+ dayMonthMaxMinAvgSum(cdtLocationChildren, energyType,
								meterTypeCode, channel, dayMonth, "sum",
								startDateTemp, endDateTemp);

			}

			returnValue = (Double) ObjectUtils.defaultIfNull(
					(sum / avgYearSize), new Double(0.0));

		}

		return returnValue;
	}

	/**
	 * 일 사용량 , 절감율을 구한다.
	 * 
	 * @param condition
	 * @return
	 */
	private Map<String, Object> energyDay(Map<String, Object> condition,
			Map<String, Object> averageInfo) {

		String gubun = "";
		String gubun2 = "";
		String gubun3 = "";
		Double energyUse = 0.0; // 에너지 사용량
		Double saving = 0.0;

		Double averageEnergyUse = 0.0;

		try {
			averageEnergyUse = (Double) averageInfo.get("energyAvgUse");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		String meterTypeCode = (String) condition.get("meterTypeCode");
		String energyType = (String) condition.get("energyType");
		String searchDateType = (String) condition.get("searchDateType");
		String supplierId = (String) condition.get("supplierId");
		String locationId = (String) condition.get("locationId");
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");
		Integer[] locationChildren = (Integer[]) condition
				.get("locationChildren");
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("bems_charge.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String patternNum = prop.getProperty("pattern.format.usage");		
		DecimalFormat df = new DecimalFormat(patternNum);

		int y = Integer.parseInt(startDate.substring(0, 4));
		int m = Integer.parseInt(startDate.substring(4, 6));
		int d = Integer.parseInt(startDate.substring(6, 8));

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Calendar c = Calendar.getInstance();
		c.set(y, m - 1, d);

		gubun = TimeLocaleUtil.getLocaleDate(formatter.format(c.getTime()), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());

		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {

			SimpleDateFormat formatter2 = new SimpleDateFormat("MM-dd");
			//gubun2 = formatter2.format(c.getTime());
			gubun2 = TimeLocaleUtil.getLocaleDate(formatter.format(c.getTime()), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
			// 주
		}

		int channel = 1;

		Condition cdtLocationChildren = new Condition("location.id",
				locationChildren, null, Restriction.IN);

		energyUse = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType,
				meterTypeCode, channel, "day", "sum", startDate, endDate);

		// 절감율
		saving = getSavingPercentage(averageEnergyUse.intValue(), energyUse
				.intValue());

		String startDateTemp = "";
		String endDateTemp = "";
		String startLocaleDateTemp = "";
		String endLocaleDateTemp = "";
		try {

			SimpleDateFormat yyyyMMddStr = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat yyyyMMddStr1 = new SimpleDateFormat("yyyy/MM/dd");
			
			startLocaleDateTemp = TimeLocaleUtil.getLocaleDate(yyyyMMddStr.parse(startDate),8, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
			endLocaleDateTemp = TimeLocaleUtil.getLocaleDate(yyyyMMddStr.parse(endDate),8, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
			
			startDateTemp = yyyyMMddStr1.format(yyyyMMddStr.parse(startDate));
			endDateTemp = yyyyMMddStr1.format(yyyyMMddStr.parse(endDate));
		} catch (ParseException e) {
			startLocaleDateTemp = startDate;
			endLocaleDateTemp = endDate;
			
			startDateTemp = startDate;
			endDateTemp = endDate;
		}

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("energyUse", Double.valueOf(df.format(energyUse))); // 에너지 사용량
		resultMap.put("saving", saving); // 절감율
		resultMap.put("gubun", gubun);
		resultMap.put("gubun2", gubun2);
		resultMap.put("gubun3", gubun3);
		resultMap.put("startDate", startDateTemp);
		resultMap.put("endDate", endDateTemp);
		resultMap.put("startLocaleDate", startLocaleDateTemp);
		resultMap.put("endLocaleDate", endLocaleDateTemp);
		return resultMap;
	}

	/**
	 * 주간 사용량 , 절감율을 구한다.
	 * 
	 * @param condition
	 * @return
	 */
	private Map<String, Object> energyWeek(Map<String, Object> condition,
			Map<String, Object> averageInfo) {

		String gubun = "";
		String gubun2 = "";
		String gubun3 = "";
		Double energyUse = 0.0;
		Double saving = 0.0;

		Double averageEnergyUse = 0.0;

		try {
			averageEnergyUse = (Double) averageInfo.get("energyAvgUse");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		String meterTypeCode = (String) condition.get("meterTypeCode");
		String energyType = (String) condition.get("energyType");
		String searchDateType = (String) condition.get("searchDateType");
		String supplierId = (String) condition.get("supplierId");
		String locationId = (String) condition.get("locationId");
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");
		Integer[] locationChildren = (Integer[]) condition
				.get("locationChildren");
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("bems_charge.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String patternNum = prop.getProperty("pattern.format.usage");		
		DecimalFormat df = new DecimalFormat(patternNum);

		int y = Integer.parseInt(startDate.substring(0, 4));
		int m = Integer.parseInt(startDate.substring(4, 6));
		int d = Integer.parseInt(startDate.substring(6, 8));

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
		Calendar c = Calendar.getInstance();
		c.set(y, m - 1, d);
		//String yyyymmdd = TimeLocaleUtil.getLocaleDate(formatter.format(c.getTime()), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
		gubun = TimeLocaleUtil.getLocaleYearMonth(formatter.format(c.getTime()), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter())+" "+ c.get(Calendar.WEEK_OF_MONTH);
//		gubun = formatter.format(c.getTime()) + " "
//				+ c.get(Calendar.WEEK_OF_MONTH);

		if (CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)) {

			SimpleDateFormat formatter3 = new SimpleDateFormat("MM-W");
			gubun2 = TimeLocaleUtil.getLocaleYearMonth(formatter.format(c.getTime()), supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter())+" "+ c.get(Calendar.WEEK_OF_MONTH);
			//gubun2 = formatter3.format(c.getTime());

			SimpleDateFormat formatter4 = new SimpleDateFormat("yyyy w");
			//gubun3 = TimeLocaleUtil.getLocaleYear(formatter.format(c.getTime()),8, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter())+" "+ c.get(Calendar.WEEK_OF_YEAR);
			gubun3 = formatter4.format(c.getTime());
		}

		int channel = 1;

		Condition cdtLocationChildren = new Condition("location.id",
				locationChildren, null, Restriction.IN);

		energyUse = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType,
				meterTypeCode, channel, "day", "sum", startDate, endDate);

		// 절감율
		saving = getSavingPercentage(averageEnergyUse.intValue(), energyUse
				.intValue());

		String startDateTemp = "";
		String endDateTemp = "";
		String startLocaleDateTemp = "";
		String endLocaleDateTemp = "";
		try {

			SimpleDateFormat yyyyMMddStr = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat yyyyMMddStr1 = new SimpleDateFormat("yyyy/MM/dd");
			
			startLocaleDateTemp = TimeLocaleUtil.getLocaleDate(yyyyMMddStr.parse(startDate),8, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
			endLocaleDateTemp = TimeLocaleUtil.getLocaleDate(yyyyMMddStr.parse(endDate),8, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
			
			startDateTemp = yyyyMMddStr1.format(yyyyMMddStr.parse(startDate));
			endDateTemp = yyyyMMddStr1.format(yyyyMMddStr.parse(endDate));

		} catch (ParseException e) {
			startLocaleDateTemp = startDate;
			endLocaleDateTemp = endDate;
			
			startDateTemp = startDate;
			endDateTemp = endDate;
		}

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("energyUse", Double.valueOf(df.format(energyUse))); // 에너지 사용량
		resultMap.put("saving", saving); // 절감율
		resultMap.put("gubun", gubun);
		resultMap.put("gubun2", gubun2);
		resultMap.put("gubun3", gubun3);
		resultMap.put("startDate", startDateTemp);
		resultMap.put("endDate", endDateTemp);
		resultMap.put("startLocaleDate", startLocaleDateTemp);
		resultMap.put("endLocaleDate", endLocaleDateTemp);
		return resultMap;
	}

	/**
	 * 월간 사용량 , 절감율을 구한다.
	 * 
	 * @param condition
	 * @return
	 */
	private Map<String, Object> energyMonth(Map<String, Object> condition,
			Map<String, Object> averageInfo) {

		String gubun = "";
		String gubun2 = "";
		String gubun3 = "";
		Double energyUse = 0.0;
		Double saving = 0.0;

		Double averageEnergyUse = 0.0;

		try {
			averageEnergyUse = (Double) averageInfo.get("energyAvgUse");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		String meterTypeCode = (String) condition.get("meterTypeCode");
		String energyType = (String) condition.get("energyType");
		String searchDateType = (String) condition.get("searchDateType");
		String supplierId = (String) condition.get("supplierId");
		String locationId = (String) condition.get("locationId");
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");
		Integer[] locationChildren = (Integer[]) condition
				.get("locationChildren");
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("bems_charge.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String patternNum = prop.getProperty("pattern.format.usage");		
		DecimalFormat df = new DecimalFormat(patternNum);

		int y = Integer.parseInt(startDate.substring(0, 4));
		int m = Integer.parseInt(startDate.substring(4, 6));

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
		Calendar c = Calendar.getInstance();
		c.set(y, m - 1, 1);

		gubun = TimeLocaleUtil.getLocaleYearMonth(formatter.format(c.getTime()),supplier.getLang().getCode_2letter(),supplier.getCountry().getCode_2letter());

		if (CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)) {

			SimpleDateFormat formatter4 = new SimpleDateFormat("MM");
			gubun2 = formatter4.format(c.getTime());
		}
		int channel = 1;

		Condition cdtLocationChildren = new Condition("location.id",
				locationChildren, null, Restriction.IN);

		energyUse = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType,
				meterTypeCode, channel, "month", "sum", startDate, endDate);

		// 절감율
		saving = getSavingPercentage(averageEnergyUse.intValue(), energyUse
				.intValue());

		Calendar tempC = Calendar.getInstance();
		tempC.set(y, m - 1, 1);
		tempC.getTime();
		tempC.set(Calendar.DATE, tempC.getActualMaximum(Calendar.DAY_OF_MONTH));

		String startDateTemp = "";
		String endDateTemp = "";
		String startLocaleDateTemp = "";
		String endLocaleDateTemp = "";
		try {

			SimpleDateFormat yyyyMMddStr = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat yyyyMMddStr1 = new SimpleDateFormat("yyyy/MM/dd");
			
			startLocaleDateTemp = TimeLocaleUtil.getLocaleDate(yyyyMMddStr.parse(startDate+"01"),8, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());			
			endLocaleDateTemp = TimeLocaleUtil.getLocaleDate(tempC.getTime(),8, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
			
			startDateTemp = yyyyMMddStr1.format(yyyyMMddStr.parse(startDate
					+ "01"));
			endDateTemp = yyyyMMddStr1.format(tempC.getTime());
		} catch (ParseException e) {
			startLocaleDateTemp = startDate;
			endLocaleDateTemp = endDate;
			
			startDateTemp = startDate;
			endDateTemp = endDate;
		}

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("energyUse", Double.valueOf(df.format(energyUse))); // 에너지 사용량
		resultMap.put("saving", saving); // 절감율
		resultMap.put("gubun", gubun);
		resultMap.put("gubun2", gubun2);
		resultMap.put("gubun3", gubun3);
		resultMap.put("startDate", startDateTemp);
		resultMap.put("endDate", endDateTemp);
		resultMap.put("startLocaleDate", startLocaleDateTemp);
		resultMap.put("endLocaleDate", endLocaleDateTemp);
		return resultMap;
	}

	/**
	 * 년간 사용량 , 절감율을 구한다.
	 * 
	 * @param condition
	 * @return
	 */
	private Map<String, Object> energyYear(Map<String, Object> condition,
			Map<String, Object> averageInfo) {

		String gubun = "";
		String gubun2 = "";
		String gubun3 = "";
		Double energyUse = 0.0;
		Double saving = 0.0;

		Double averageEnergyUse = 0.0;

		try {
			averageEnergyUse = (Double) averageInfo.get("energyAvgUse");
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		String meterTypeCode = (String) condition.get("meterTypeCode");
		String energyType = (String) condition.get("energyType");
		String searchDateType = (String) condition.get("searchDateType");
		String supplierId = (String) condition.get("supplierId");
		String locationId = (String) condition.get("locationId");
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");
		Integer[] locationChildren = (Integer[]) condition
				.get("locationChildren");
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("bems_charge.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String patternNum = prop.getProperty("pattern.format.usage");		
		DecimalFormat df = new DecimalFormat(patternNum);

		int y = Integer.parseInt(startDate.substring(0, 4));
		int m = Integer.parseInt(startDate.substring(4, 6));

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		Calendar c = Calendar.getInstance();
		c.set(y, m - 1, 1);

		gubun = formatter.format(c.getTime());

		if (CommonConstants.DateType.YEARLY.getCode().equals(searchDateType)) {

			SimpleDateFormat formatter5 = new SimpleDateFormat("yyyy");
			gubun2 = formatter5.format(c.getTime());
		}

		int channel = 1;

		Condition cdtLocationChildren = new Condition("location.id",
				locationChildren, null, Restriction.IN);

		energyUse = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType,
				meterTypeCode, channel, "month", "sum", startDate, endDate);

		// 절감율
		saving = getSavingPercentage(averageEnergyUse.intValue(), energyUse
				.intValue());

		Calendar tempC = Calendar.getInstance();
		tempC.set(y, 11, 1);
		tempC.getTime();
		tempC.set(Calendar.DATE, tempC.getActualMaximum(Calendar.DAY_OF_MONTH));

		String startDateTemp = "";
		String endDateTemp = "";		
		String startLocaleDateTemp = "";
		String endLocaleDateTemp = "";
		try {

			SimpleDateFormat yyyyMMddStr = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat yyyyMMddStr1 = new SimpleDateFormat("yyyy/MM/dd");
			
			startLocaleDateTemp = TimeLocaleUtil.getLocaleDate(yyyyMMddStr.parse(startDate+"01"),8, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
			endLocaleDateTemp = TimeLocaleUtil.getLocaleDate(tempC.getTime(),8, supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
			startDateTemp = yyyyMMddStr1.format(yyyyMMddStr.parse(startDate
					+ "01"));
			endDateTemp = yyyyMMddStr1.format(tempC.getTime());
		} catch (ParseException e) {
			startLocaleDateTemp = startDate;
			endLocaleDateTemp = endDate;
			
			startDateTemp = startDate;
			endDateTemp = endDate;
		}

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("energyUse", Double.valueOf(df.format(energyUse))); // 에너지 사용량
		resultMap.put("saving", saving); // 절감율
		resultMap.put("gubun", gubun);
		resultMap.put("gubun2", gubun2);
		resultMap.put("gubun3", gubun3);
		resultMap.put("startDate", startDateTemp);
		resultMap.put("endDate", endDateTemp);
		resultMap.put("startLocaleDate", startLocaleDateTemp);
		resultMap.put("endLocaleDate", endLocaleDateTemp);
		return resultMap;
	}

	/**
	 * 증감율 구하기
	 * 
	 * @param pre
	 *            기준값
	 * @param now
	 *            비교값
	 * @return
	 */
	private Double getSavingPercentage(int pre, int now) {
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

		if (pre != 0 && now != 0) {
			result = ((dNow - dPre) / dPre) * 100d;
		}

		return result;
	}

	/**
	 * 조회 기간과 에너지 타입에따른 최대/최소/평균/합 수치를 반환함.
	 * 
	 * @param cdtLocationChildren
	 * @param energyType
	 *            : 전기 = EM , 가스 = GM , 수도 = WM 열량 = HM
	 * @param meterTypeCode
	 *            : 예)1.3.1.1 = EnergyMeter , 1.3.1.2 = WaterMeter , 1.3.1.3 =
	 *            GasMeter 1.3.1.4 = HeatMeter
	 * @param channel
	 * @param dateType
	 *            : day : day테이블 조회 , month : month테이블 조회
	 * @param valueType
	 *            : 최대,최소,평균, 합
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private Double dayMonthMaxMinAvgSum(Condition cdtLocationChildren,
			String energyType, String meterTypeCode, int channel,
			String dateType, String valueType, String startDate, String endDate) {

		Double returnValue = 0.0;
		List<Object> sumList = null;

		Set<Condition> conditionSetMeters = new HashSet<Condition>();

		conditionSetMeters.add(cdtLocationChildren);

		Code meterTypeCodeObject = codeDao.getCodeIdByCodeObject(meterTypeCode);

		Condition conditionMeterType = new Condition("meterType.id",
				new Object[] { meterTypeCodeObject.getId() }, null,
				Restriction.EQ);
		conditionSetMeters.add(conditionMeterType);

		List<Meter> meterList = meterDao.findByConditions(conditionSetMeters);
		Integer[] meterIds = new Integer[meterList.size()];
		for (int i = 0; i < meterList.size(); i++) {
			Meter mm = meterList.get(i);
			meterIds[i] = mm.getId();
		}

		if (meterIds.length > 0) {

			Set<Condition> conditionSetGroupFunction = new HashSet<Condition>();

			Condition conditionChannel = new Condition("id.channel",
					new Object[] { channel }, null, Restriction.EQ);
			conditionSetGroupFunction.add(conditionChannel);
			if (!startDate.isEmpty() && !endDate.isEmpty()) {

				Condition cdt21 = null;
				if ("day".equals(dateType)) {

					cdt21 = new Condition("id.yyyymmdd", new Object[] {
							startDate, endDate }, null, Restriction.BETWEEN);
				} else if ("month".equals(dateType)) {

					cdt21 = new Condition("id.yyyymm", new Object[] {
							startDate, endDate }, null, Restriction.BETWEEN);
				}

				conditionSetGroupFunction.add(cdt21);
			}

			Condition conditionMeterIds = new Condition("meter.id", meterIds,
					null, Restriction.IN);

			conditionSetGroupFunction.add(conditionMeterIds);

			if ("EM".equals(energyType)) {

				if ("day".equals(dateType)) {

					sumList = dayEMDao.getDayEMsMaxMinAvgSum(
							conditionSetGroupFunction, valueType);
				} else if ("month".equals(dateType)) {

					sumList = monthEMDao.getMonthEMsMaxMinAvgSum(
							conditionSetGroupFunction, valueType);
				}
			} else if ("GM".equals(energyType)) {

				if ("day".equals(dateType)) {

					sumList = dayGMDao.getDayGMsMaxMinAvgSum(
							conditionSetGroupFunction, valueType);
				} else if ("month".equals(dateType)) {

					sumList = monthGMDao.getMonthGMsMaxMinAvgSum(
							conditionSetGroupFunction, valueType);
				}
			} else if ("WM".equals(energyType)) {

				if ("day".equals(dateType)) {

					sumList = dayWMDao.getDayWMsMaxMinAvgSum(
							conditionSetGroupFunction, valueType);
				} else if ("month".equals(dateType)) {

					sumList = monthWMDao.getMonthWMsMaxMinAvgSum(
							conditionSetGroupFunction, valueType);
				}
			}  else if ("HM".equals(energyType)) {

				if ("day".equals(dateType)) {

					sumList = dayHMDao.getDayHMsMaxMinAvgSum(
							conditionSetGroupFunction, valueType);
				} else if ("month".equals(dateType)) {

					sumList = monthHMDao.getMonthHMsMaxMinAvgSum(
							conditionSetGroupFunction, valueType);
				}
			} 
		}

		if (sumList != null && !sumList.isEmpty() && sumList.get(0) != null) {
			returnValue = (Double) sumList.get(0);
		}
		return returnValue;
	}

	public Map<String, Object> getEnergySavingGoal2AvgYearsUsed(
			Map<String, Object> params) {

		int channel = 1;
		List<Object> yearList = (List<Object>) params.get("years");
		String supplierId = StringUtils.defaultIfEmpty((String) params
				.get("supplierId"), "0");

		Condition cdtLocationChildren = null;

		ArrayList<Location> rootLocation = (ArrayList<Location>) locationDao
				.getParents(Integer.parseInt(supplierId));
		if (rootLocation != null) {

			String locationId = String.valueOf(rootLocation.get(0).getId());

			Iterator a = rootLocation.get(0).getChildren().iterator();
			Integer[] locationChildren = new Integer[rootLocation.get(0)
					.getChildren().size()];
			int b = 0;
			while (a.hasNext()) {
				Location z = (Location) a.next();
				locationChildren[b] = z.getId();
				++b;
			}

			cdtLocationChildren = new Condition("location.id",
					locationChildren, null, Restriction.IN);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		List<HashMap<String, Object>> resultList = new ArrayList<HashMap<String, Object>>();

		Double sumEm = 0.0;
		Double sumGm = 0.0;
		Double sumWm = 0.0;
		Double sumHm = 0.0;

		int avgYearSize = yearList.size();
		logger.debug("avgYearSize:" + avgYearSize);
		if (avgYearSize > 0) {

			for (int i = 0; i < avgYearSize; i++) {
				String year = yearList.get(i).toString();

				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
				Calendar c = Calendar.getInstance();
				c.set(Integer.parseInt(year), 0, 1);
				String startDate = formatter.format(c.getTime());
				c.set(Integer.parseInt(year), 11, 1);
				String endDate = formatter.format(c.getTime());

				sumEm = sumEm
						+ dayMonthMaxMinAvgSum(cdtLocationChildren, "EM",
								"1.3.1.1", channel, "month", "sum", startDate,
								endDate);
				sumWm = sumWm
						+ dayMonthMaxMinAvgSum(cdtLocationChildren, "WM",
								"1.3.1.2", channel, "month", "sum", startDate,
								endDate);
				sumGm = sumGm
						+ dayMonthMaxMinAvgSum(cdtLocationChildren, "GM",
								"1.3.1.3", channel, "month", "sum", startDate,
								endDate);		
				sumHm = sumHm
						+ dayMonthMaxMinAvgSum(cdtLocationChildren, "HM",
								"1.3.1.4", channel, "month", "sum", startDate,
								endDate);

			}

			logger.debug("sumEm:" + sumEm);
			Double emAvgUsed = (Double) ObjectUtils.defaultIfNull(
					(sumEm / avgYearSize), new Double(0.0));
			Double gmAvgUsed = (Double) ObjectUtils.defaultIfNull(
					(sumGm / avgYearSize), new Double(0.0));
			Double wmAvgUsed = (Double) ObjectUtils.defaultIfNull(
					(sumWm / avgYearSize), new Double(0.0));
			Double hmAvgUsed = (Double) ObjectUtils.defaultIfNull(
					(sumHm / avgYearSize), new Double(0.0));
			
			Double totalAvgUsed = (Double) ObjectUtils.defaultIfNull(((sumEm
					+ sumWm + sumGm + sumHm) / avgYearSize), new Double(0.0));

			NumberFormat numberFormatter = new DecimalFormat(
					"###,###,###,###.##");

			HashMap<String, Object> used = new HashMap<String, Object>();
			used.put("avgYear", String.valueOf(avgYearSize));
			used.put("avgYearStr", String.valueOf(avgYearSize)
					+ (String) params.get("msgYear"));
			used.put("emAvgUsed", numberFormatter.format(emAvgUsed) + " kWh");
			used.put("gmAvgUsed", numberFormatter.format(gmAvgUsed) + " ㎥");
			used.put("wmAvgUsed", numberFormatter.format(wmAvgUsed) + " ㎥");
			used.put("hmAvgUsed", numberFormatter.format(hmAvgUsed) + " ㎥");
			used.put("totalAvgUsed", numberFormatter.format(totalAvgUsed)
					+ " TOE");
			
			logger.debug("used:" + used);
			resultList.add(used);
			logger.debug("used:" + used);
			result.put("avgYearsUsedList", resultList);
		}
		return result;
	}

	/**
	 * 집계가능한 년도별 전기 , 가스, 수도 , 총사용량(전기+가스+수도+열량) 리스트를 조회한다.
	 * 
	 * @return
	 */
	public Map<String, Object> getEnergySavingGoal2YearsUsed(
			Map<String, Object> params) {

		Map<String, Object> result = new HashMap<String, Object>();

		List<HashMap<String, Object>> yearsUsedList = new ArrayList<HashMap<String, Object>>();

		// 1. 에너지 타입별 집계가능한 년도 추출
		List<Object> yearsEm = monthEMDao.getMonthToYears();
		List<Object> yearsGm = monthGMDao.getMonthToYears();
		List<Object> yearsWm = monthWMDao.getMonthToYears();
		List<Object> yearsHm = monthHMDao.getMonthToYears();

		// 2. 에너지 타입별 해당 년도의 검침값의 합 추출
		String locationId = null;
		Integer[] locationChildren = null;

		String startDate = StringUtils.defaultIfEmpty((String) params
				.get("searchStartDate"), "");
		String endDate = StringUtils.defaultIfEmpty((String) params
				.get("searchEndDate"), "");
		String supplierId = StringUtils.defaultIfEmpty((String) params
				.get("supplierId"), "0");

		ArrayList<Location> rootLocation = (ArrayList<Location>) locationDao
				.getParents(Integer.parseInt(supplierId));
		if (rootLocation != null) {

			locationId = String.valueOf(rootLocation.get(0).getId());

			Iterator a = rootLocation.get(0).getChildren().iterator();
			locationChildren = new Integer[rootLocation.get(0).getChildren()
					.size()];
			int b = 0;
			while (a.hasNext()) {
				Location z = (Location) a.next();
				locationChildren[b] = z.getId();
				++b;
			}

		}

		Map<String, Object> conditionEM = new HashMap<String, Object>();
		conditionEM.put("energyType", "EM");
		conditionEM.put("locationChildren", locationChildren);
		conditionEM.put("years", yearsEm);
		
		Map<String, Object> conditionGM = new HashMap<String, Object>();
		conditionGM.put("energyType", "GM");
		conditionGM.put("locationChildren", locationChildren);
		conditionGM.put("years", yearsGm);
		
		Map<String, Object> conditionWM = new HashMap<String, Object>();
		conditionWM.put("energyType", "WM");
		conditionWM.put("locationChildren", locationChildren);
		conditionWM.put("years", yearsWm);
		
		Map<String, Object> conditionHM = new HashMap<String, Object>();
		conditionHM.put("energyType", "HM");
		conditionHM.put("locationChildren", locationChildren);
		conditionHM.put("years", yearsWm);
		
		HashMap<String, Object> yearsEmSum = getMonthToYearsSum(conditionEM);
		HashMap<String, Object> yearsGmSum = getMonthToYearsSum(conditionGM);
		HashMap<String, Object> yearsWmSum = getMonthToYearsSum(conditionWM);
		HashMap<String, Object> yearsHmSum = getMonthToYearsSum(conditionHM);
		
		logger.debug("yearsUsedList:" + yearsUsedList);
		// 3. 조회한 년도에따른 정보를 리스트 형식으로 만든다. ( 전기 , 가스 , 수도 , 총사용량(전기+가스+수도)
		yearsUsedList = getYearUsedList(yearsEm, yearsGm, yearsWm, yearsHm, yearsEmSum,
				yearsGmSum, yearsWmSum, yearsHmSum);

		result.put("yearsUsedList", yearsUsedList);
		return result;
	}

	/**
	 * 에너지 타입의 해당 년도별의 검침값의 합
	 * 
	 * @param condition
	 * @return
	 */
	private HashMap<String, Object> getMonthToYearsSum(
			Map<String, Object> condition) {

		HashMap<String, Object> result = new HashMap<String, Object>();

		String energyType = (String) condition.get("energyType");
		List<Object> years = (List<Object>) condition.get("years");
		Integer[] locationChildren = (Integer[]) condition
				.get("locationChildren");

		int channel = 1;
		Condition cdtLocationChildren = new Condition("location.id",
				locationChildren, null, Restriction.IN);

		for (int i = 0; i < years.size(); i++) {
			String year = years.get(i).toString();

			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
			Calendar c = Calendar.getInstance();
			c.set(Integer.parseInt(year), 0, 1);
			String startDate = formatter.format(c.getTime());
			c.set(Integer.parseInt(year), 11, 1);
			String endDate = formatter.format(c.getTime());

			Double sum = 0.0;
			if ("EM".equals(energyType)) {

				sum = dayMonthMaxMinAvgSum(cdtLocationChildren, "EM",
						"1.3.1.1", channel, "month", "sum", startDate, endDate);
			} else if ("GM".equals(energyType)) {

				sum = dayMonthMaxMinAvgSum(cdtLocationChildren, "GM",
						"1.3.1.3", channel, "month", "sum", startDate, endDate);
			} else if ("WM".equals(energyType)) {

				sum = dayMonthMaxMinAvgSum(cdtLocationChildren, "WM",
						"1.3.1.2", channel, "month", "sum", startDate, endDate);
			} else if ("HM".equals(energyType)) {

				sum = dayMonthMaxMinAvgSum(cdtLocationChildren, "HM",
						"1.3.1.4", channel, "month", "sum", startDate, endDate);
			}

			result.put(year, sum);

		}

		return result;
	}

	/**
	 * 조회한 년도에따른 정보를 리스트 형식으로 반환한다. ( 전기 , 가스 , 수도 , 총사용량(전기+가스+수도) )
	 * 
	 * @param yearsEmSum
	 * @param yearsGmSum
	 * @param yearsWmSum
	 * @param yearsHmSum
	 * @param yearsWmSum2
	 * @param yearsGmSum2
	 * @param yearsEmSum2
	 * @param yearsHmSum2
	 * @return
	 */
	private List<HashMap<String, Object>> getYearUsedList(List<Object> yearsEm,
			List<Object> yearsGm, List<Object> yearsWm, List<Object> yearsHm,
			HashMap<String, Object> yearsEmSum,
			HashMap<String, Object> yearsGmSum,
			HashMap<String, Object> yearsWmSum,
			HashMap<String, Object> yearsHmSum) {

		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		List<Object> tmpList = new ArrayList<Object>();

		tmpList.addAll(yearsEm);
		tmpList.addAll(yearsGm);
		tmpList.addAll(yearsWm);

		List<Object> yearList = new ArrayList<Object>();
		String[] arrStr = (String[]) tmpList
				.toArray(new String[tmpList.size()]);
		Arrays.sort(arrStr, String.CASE_INSENSITIVE_ORDER);

		for (int i = 0; i < arrStr.length; i++) {

			if (i == 0) {

				yearList.add(arrStr[i]);
			}

			if (i > 0 && !arrStr[i - 1].equals(arrStr[i])) {

				yearList.add(arrStr[i]);
			}
		}
		System.out.println("#" + yearList.toString());

		// Collections.sort(list)

		for (int i = 0; i < yearList.size(); i++) {

			String year = yearList.get(i).toString();
			Double emUsed = (Double) ObjectUtils.defaultIfNull(yearsEmSum
					.get(year), new Double(0.0));
			Double gmUsed = (Double) ObjectUtils.defaultIfNull(yearsGmSum
					.get(year), new Double(0.0));
			Double wmUsed = (Double) ObjectUtils.defaultIfNull(yearsWmSum
					.get(year), new Double(0.0));
			Double hmUsed = (Double) ObjectUtils.defaultIfNull(yearsHmSum
					.get(year), new Double(0.0));
			
			Double totalUsed = emUsed + gmUsed + wmUsed + hmUsed;

			NumberFormat numberFormatter = new DecimalFormat(
					"###,###,###,###.##");

			HashMap<String, Object> used = new HashMap<String, Object>();
			used.put("year", year);
			used.put("checked", new Boolean(true));
			used.put("emUsed", numberFormatter.format(emUsed) + " kWh");
			used.put("gmUsed", numberFormatter.format(gmUsed) + " ㎥");
			used.put("wmUsed", numberFormatter.format(wmUsed) + " ㎥");
			used.put("hmUsed", numberFormatter.format(hmUsed) + " ㎥");
			used.put("totalUsed", numberFormatter.format(totalUsed) + " TOE");

			result.add(used);
		}

		return result;
	}

	public Map<String, Object> getEnergySavingGoal2SaveInfoList(
			Map<String, Object> params) {

		Map<String, Object> result = new HashMap<String, Object>();
		List<HashMap<String, Object>> saveInfoListObj = new ArrayList<HashMap<String, Object>>();

		String supplierId = StringUtils.defaultIfEmpty((String) params
				.get("supplierId"), "0");
		
		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		List<AverageUsage> saveInfoList = averageUsageDao.getAll();

		if (saveInfoList != null) {

			SimpleDateFormat dateFormatter1 = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat dayFormatter = new SimpleDateFormat("yyyy-MM-dd");
			NumberFormat numFormatter = new DecimalFormat("###,###,###,###.##");

			for (int i = 0; i < saveInfoList.size(); i++) {

				HashMap<String, Object> info = new HashMap<String, Object>();
				String years = "";
				AverageUsage au = saveInfoList.get(i);
				info.put("avgUsageYear", numFormatter.format(au
						.getAvgUsageYear())
						+ "TOE");
				try {
					info.put("createDate",TimeLocaleUtil.getLocaleDate(dateFormatter1.parse(au.getCreateDate()),8,supplier.getLang().getCode_2letter(),supplier.getCountry().getCode_2letter()));
//					info.put("createDate", dayFormatter.format(dateFormatter1
//							.parse(au.getCreateDate())));
				} catch (Exception e) {
					info.put("createDate",au.getCreateDate());
					e.printStackTrace();
				}
				info.put("descr", au.getDescr());
				info.put("id", au.getId());
				info.put("used", au.getUsed() ? (String) params
						.get("msgIsUsedYes") : (String) params
						.get("msgIsUsedNo"));
				info.put("years", au.getBasesToString());

				saveInfoListObj.add(info);
			}

		}
		logger.debug("saveInfoListObj:" + saveInfoListObj);
		result.put("saveInfoList", saveInfoListObj);
		// result.put( "saveInfoList" , saveInfoList );

		return result;
	}

	public Map<String, Object> getEnergySavingGoal2GoalList(
			Map<String, Object> params) {

		String supplierId = (String) params.get("supplierId");
		String energyType = (String) params.get("energyType");
		String avgInfoId = (String) params.get("avgInfoId");
		String allView = (String) params.get("allView");

		Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
		logger.debug("getEnergySavingGoal2GoalList params:" + params);
		Map<String, Object> result = new HashMap<String, Object>();
		List<HashMap<String, Object>> goalListObj = new ArrayList<HashMap<String, Object>>();

		List<EnergySavingGoal2> goalList = null;
		if ("Y".equals(allView) && !avgInfoId.isEmpty()) {

			goalList = energySavingGoal2Dao.getEnergySavingGoal2ListByAvg(
					supplierId, energyType, avgInfoId, allView);
		} else {

			goalList = energySavingGoal2Dao.getAll();
		}
		logger.debug("getEnergySavingGoal2GoalList goalList:" + goalList);
		if (goalList != null) {

			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
//			SimpleDateFormat dateFormatter1 = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat dateFormatter2 = new SimpleDateFormat("yyyy-w");
			SimpleDateFormat dateFormatter3 = new SimpleDateFormat("yyyy. M");
			SimpleDateFormat dateFormatter4 = new SimpleDateFormat("yyyy");
			NumberFormat numFormatter = new DecimalFormat("###,###,###,###.##");

			for (int i = 0; i < goalList.size(); i++) {

				HashMap<String, Object> info = new HashMap<String, Object>();
				EnergySavingGoal2 esg = goalList.get(i);

				try {
					info.put("createDate", TimeLocaleUtil.getLocaleDate(dateFormatter.parse(esg.getId().getCreateDate()),8,supplier.getLang().getCode_2letter(),supplier.getCountry().getCode_2letter()));
//					info.put("createDate", dateFormatter1.format(dateFormatter
//							.parse(esg.getId().getCreateDate())));
				} catch (ParseException e) {
					e.printStackTrace();
					info.put("createDate", esg.getId().getCreateDate());
				}
				info.put("supplier", esg.getSupplier().getId());
				info.put("savingGoal", esg.getSavingGoal() + " %");
				info.put("averageUsage", numFormatter.format(esg
						.getAverageUsage().getAvgUsageYear())
						+ " TOE");
				info.put("averageUsageId", esg.getAverageUsage().getId());

				String startDate = esg.getId().getStartDate();
				String dateType = "";
				if ("DAILY".equals(esg.getDateType().toString())) {
					dateType = (String) params.get("msgDaily");
					try {
						info.put("startDate", TimeLocaleUtil.getLocaleDate(dateFormatter.parse(esg.getId().getStartDate()),8,supplier.getLang().getCode_2letter(),supplier.getCountry().getCode_2letter()));
//						info.put("startDate", dateFormatter1
//								.format(dateFormatter.parse(esg.getId()
//										.getStartDate())));
					} catch (Exception e) {
						e.printStackTrace();
						info.put("startDate", esg.getId().getStartDate());
					}
				} else if ("WEEKLY".equals(esg.getDateType().toString())) {
					dateType = (String) params.get("msgWeekly");
					try {
						
						
						info.put("startDate", dateFormatter2
								.format(dateFormatter.parse(esg.getId()
										.getStartDate()))
								+ " " + (String) params.get("msgWeek"));
					} catch (ParseException e) {
						e.printStackTrace();
						info.put("startDate", esg.getId().getStartDate());
					}
				} else if ("MONTHLY".equals(esg.getDateType().toString())) {
					dateType = (String) params.get("msgMonthly");
					try {
						
//						info.put("startDate", TimeLocaleUtil.getLocaleMonth(dateFormatter.format(esg.getId().getStartDate()),8,supplier.getLang().getCode_2letter(),supplier.getCountry().getCode_2letter())+(String) params.get("msgMonth"));
						info.put("startDate", dateFormatter3
								.format(dateFormatter.parse(esg.getId()
										.getStartDate()))
								+ " " + (String) params.get("msgMonth"));
					} catch (Exception e) {
						e.printStackTrace();
						info.put("startDate", esg.getId().getStartDate());
					}
				} else if ("YEARLY".equals(esg.getDateType().toString())) {
					dateType = (String) params.get("msgYearly");
					try {
						
						info.put("startDate", dateFormatter4
								.format(dateFormatter.parse(esg.getId()
										.getStartDate()))
								+ " " + (String) params.get("msgYear"));
					} catch (ParseException e) {
						e.printStackTrace();
						info.put("startDate", esg.getId().getStartDate());
					}
				}
				String supplyType = "";
				if (0 == esg.getSupplyType()) {

					supplyType = (String) params.get("msgElectricity");
				} else if (1 == esg.getSupplyType()) {

					supplyType = (String) params.get("msgGas");
				} else if (2 == esg.getSupplyType()) {

					supplyType = (String) params.get("msgWater");
				} else if (3 == esg.getSupplyType()) {

					supplyType = (String) params.get("msgHeat");
				}

				info.put("supplyTypeMsg", supplyType);
				info.put("dateTypeMsg", dateType);
				info.put("supplyType", esg.getSupplyType());
				info.put("dateType", esg.getDateType().toString());

				goalListObj.add(info);
			}

		}
		logger.debug("getEnergySavingGoal2GoalList goalListObj:" + goalListObj);
		result.put("goalList", goalListObj);

		return result;
	}

	public Map<String, Object> getEnergySavingGoal2GoalList2(
			Map<String, Object> params) {

		String searchDateType = StringUtils.defaultIfEmpty((String) params
				.get("searchDateType"), "1");// 주기 타입 ( 1 : 일, 3 : 주, 4 : 월, 8 :
												// 년 )
		String startDate = StringUtils.defaultIfEmpty((String) params
				.get("searchStartDate"), "");
		String endDate = StringUtils.defaultIfEmpty((String) params
				.get("searchEndDate"), "");
		String supplierId = StringUtils.defaultIfEmpty((String) params
				.get("supplierId"), "");
		String energyType = StringUtils.defaultIfEmpty((String) params
				.get("energyType"), "0"); // 에너지 타입 ( 전기 : 0 , 가스 : 1 , 수도 : 2 )

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Calendar c = Calendar.getInstance();
		// System.out.println("##############################2010.07.07일 셋팅! TEST 가 끝나면 지울것!![getTotalUseOfSearchType]");
		// c.set(2010, 6, 7); // 2010.07.07일 셋팅! TEST 가 끝나면 지울것!!
		String currentDate = dateFormat.format(c.getTime()); // 오늘 날짜

		if ("".equals(startDate)) {
			startDate = currentDate;
		}
		if ("".equals(endDate)) {
			endDate = currentDate;
		}

		if ("0".equals(searchDateType)) {
			searchDateType = "1";
		}

		Map<String, Object> result = new HashMap<String, Object>();
		List<HashMap<String, Object>> goalListObj = new ArrayList<HashMap<String, Object>>();

		int y = Integer.parseInt(startDate.substring(0, 4));
		int m = Integer.parseInt(startDate.substring(4, 6));
		int d = Integer.parseInt(startDate.substring(6, 8));

		int ey = Integer.parseInt(endDate.substring(0, 4));
		int em = Integer.parseInt(endDate.substring(4, 6));
		int ed = Integer.parseInt(endDate.substring(6, 8));
		logger.debug("getEnergySavingGoal2GoalList2 params:" + params);

		// 1. 주기별 사용량 조회
		// 일(시간)
		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {

			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
			Calendar getDay = Calendar.getInstance();
			getDay.set(y, m - 1, d);

			for (int i = getDay.getActualMaximum(Calendar.DAY_OF_MONTH); i > 0; i--) {

				getDay.set(getDay.get(Calendar.YEAR), getDay
						.get(Calendar.MONTH), i);
				String monthDayS = formatter1.format(getDay.getTime());

				if (getDay.getTimeInMillis() > c.getTimeInMillis()) {
					continue;
				}
				Map<String, Object> paramsTemp = new HashMap<String, Object>();
				paramsTemp.put("searchDateType", CommonConstants.DateType.DAILY
						.getCode());
				paramsTemp.put("supplierId", supplierId);
				paramsTemp.put("energyType", energyType);
				paramsTemp.put("searchStartDate", monthDayS);
				paramsTemp.put("searchEndDate", monthDayS);

				HashMap<String, Object> dayInfoMap = (HashMap<String, Object>) getEnergySavingGoal2Info(paramsTemp);

				goalListObj.add(getEnergySavingGoal2GoalList2Item(dayInfoMap));
			}

			// 주
		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
				searchDateType)) {

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
			Calendar getWeek = Calendar.getInstance();
			getWeek.set(ey, em - 1, ed);

			if (em == 12 && getWeek.get(Calendar.WEEK_OF_YEAR) == 1) {
				getWeek.set(Calendar.WEEK_OF_YEAR, 52);
				getWeek.set(Calendar.YEAR, ey);

				System.out.println(getWeek.getTime().toString());
			}

			for (int k = getWeek.get(Calendar.WEEK_OF_YEAR); k > 0; k--) {

				getWeek.set(Calendar.WEEK_OF_YEAR, k);

				String weekS = "";
				String weekE = "";

				getWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				if (getWeek.get(Calendar.YEAR) == y) {
					weekS = formatter1.format(getWeek.getTime());
				} else {
					Calendar ti = Calendar.getInstance();
					ti.set(y, 0, 1);
					weekS = formatter1.format(ti.getTime());
				}

				getWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
				if (k != 52) {
					weekE = formatter1.format(getWeek.getTime());
				} else {
					Calendar lastDate = Calendar.getInstance();
					lastDate.set(ey, em - 1, ed);
					weekE = formatter1.format(lastDate.getTime());
				}

				Map<String, Object> paramsTemp = new HashMap<String, Object>();
				paramsTemp.put("searchDateType",
						CommonConstants.DateType.WEEKLY.getCode());
				paramsTemp.put("supplierId", supplierId);
				paramsTemp.put("energyType", energyType);
				paramsTemp.put("searchStartDate", weekS);
				paramsTemp.put("searchEndDate", weekE);

				HashMap<String, Object> dayInfoMap = (HashMap<String, Object>) getEnergySavingGoal2Info(paramsTemp);

				goalListObj.add(getEnergySavingGoal2GoalList2Item(dayInfoMap));

			}

			// 월
		} else if (CommonConstants.DateType.MONTHLY.getCode().equals(
				searchDateType)) {

			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
			Calendar getMonth = Calendar.getInstance();
			getMonth.set(ey, em - 1, ed);

			for (int i = getMonth.get(Calendar.MONTH); i >= 0; i--) {
				getMonth.set(getMonth.get(Calendar.YEAR), i, 1);
				String monthS = formatter1.format(getMonth.getTime());
				// logger.debug( "[" + startDate + "] " + "월 시작 날짜 : " + monthS
				// );

				/*
				 * if(getMonth.get(Calendar.MONTH) !=
				 * Calendar.getInstance().get(Calendar.MONTH)) { getMonth.set(
				 * Calendar.DATE ,
				 * getMonth.getActualMaximum(Calendar.DAY_OF_MONTH) ); } else {
				 * getMonth.set( Calendar.DATE ,
				 * Calendar.getInstance().get(Calendar.DATE) ); }
				 */
				getMonth.set(Calendar.DATE, getMonth
						.getActualMaximum(Calendar.DAY_OF_MONTH));
				String monthE = formatter1.format(getMonth.getTime());
				// logger.debug( "[" + startDate + "] " + "월 마지막 날짜 : " + monthE
				// );

				Map<String, Object> paramsTemp = new HashMap<String, Object>();
				paramsTemp.put("searchDateType",
						CommonConstants.DateType.MONTHLY.getCode());
				paramsTemp.put("supplierId", supplierId);
				paramsTemp.put("energyType", energyType);
				paramsTemp.put("searchStartDate", monthS);
				paramsTemp.put("searchEndDate", monthE);

				HashMap<String, Object> dayInfoMap = (HashMap<String, Object>) getEnergySavingGoal2Info(paramsTemp);

				goalListObj.add(getEnergySavingGoal2GoalList2Item(dayInfoMap));
			}

			// 년간
		} else if (CommonConstants.DateType.YEARLY.getCode().equals(
				searchDateType)) {

			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
			Calendar getYear = Calendar.getInstance();
			getYear.set(y, 0, 1);

			for (int i = y; i > y - 10; i--) {

				getYear.set(i, 0, 1);
				String monthS = formatter1.format(getYear.getTime());
				// logger.debug( "[" + startDate + "] " + "월 시작 날짜 : " + monthS
				// );

				getYear.set(Calendar.MONTH, getYear
						.getActualMaximum(Calendar.MONTH));
				getYear.getTime();
				getYear.set(Calendar.DATE, getYear
						.getActualMaximum(Calendar.DAY_OF_MONTH));
				String monthE = formatter1.format(getYear.getTime());
				// logger.debug( "[" + startDate + "] " + "월 마지막 날짜 : " + monthE
				// );

				Map<String, Object> paramsTemp = new HashMap<String, Object>();
				paramsTemp.put("searchDateType",
						CommonConstants.DateType.YEARLY.getCode());
				paramsTemp.put("supplierId", supplierId);
				paramsTemp.put("energyType", energyType);
				paramsTemp.put("searchStartDate", monthS);
				paramsTemp.put("searchEndDate", monthE);

				HashMap<String, Object> dayInfoMap = (HashMap<String, Object>) getEnergySavingGoal2Info(paramsTemp);

				goalListObj.add(getEnergySavingGoal2GoalList2Item(dayInfoMap));
			}
		}

		logger
				.debug("getEnergySavingGoal2GoalList2 goalListObj:"
						+ goalListObj);

		result.put("goalList", goalListObj);

		return result;
	}

	private HashMap<String, Object> getEnergySavingGoal2GoalList2Item(
			HashMap<String, Object> dayInfoMap) {

		HashMap<String, Object> returnMap = null;
		
		String energyType = StringUtils.defaultIfEmpty((String) dayInfoMap
				.get("energyType"), "0"); // 에너지 타입 ( 전기 : 0 , 가스 : 1 , 수도 : 2 , 열량 : 3 )

		NumberFormat numFormatter = new DecimalFormat("###,###,###,###.##");

		List<Map<String, Object>> resultList = (List<Map<String, Object>>) dayInfoMap
				.get("info");

		HashMap<String, Object> averageInfo = (HashMap<String, Object>) resultList
				.get(0);
		HashMap<String, Object> energyInfoNow = (HashMap<String, Object>) resultList
				.get(1);
		HashMap<String, Object> chartMap = (HashMap<String, Object>) resultList
				.get(2);

		returnMap = new HashMap<String, Object>();

		returnMap.put("startDate", energyInfoNow.get("startDate")); // 시작 일자
		returnMap.put("endDate", energyInfoNow.get("endDate")); // 종료 일자
		returnMap.put("startLocaleDate", energyInfoNow.get("startLocaleDate")); // 시작 일자 by Locale
		returnMap.put("endLocaleDate", energyInfoNow.get("endLocaleDate")); // 종료 일자 by Locale
		returnMap.put("gubun", energyInfoNow.get("gubun")); // 일자
		returnMap.put("gubun2", energyInfoNow.get("gubun2")); // 일자
		returnMap.put("gubun3", energyInfoNow.get("gubun3")); // 일자
		returnMap.put("energyAvgYearCount", averageInfo
				.get("energyAvgYearCount")); // 평균에 추출에 사용된 년수
		
		if(energyType.equals("0")) {
			returnMap.put("energyAvgUse", numFormatter.format(averageInfo
					.get("energyAvgUse"))
					+ " kWh"); // 평균 사용량
			returnMap.put("savingGoal", averageInfo.get("savingGoal") + " %"); // 목표율
			returnMap.put("energyGoalUse", numFormatter.format(averageInfo
					.get("energyGoalUse"))
					+ " kWh"); // 목표량
			returnMap.put("energyUse", numFormatter.format(energyInfoNow
					.get("energyUse"))
					+ " kWh"); // 사용량
		} else {
			returnMap.put("energyAvgUse", numFormatter.format(averageInfo
					.get("energyAvgUse"))
					+ " ㎥"); // 평균 사용량
			returnMap.put("savingGoal", averageInfo.get("savingGoal") + " %"); // 목표율
			returnMap.put("energyGoalUse", numFormatter.format(averageInfo
					.get("energyGoalUse"))
					+ " ㎥"); // 목표량
			returnMap.put("energyUse", numFormatter.format(energyInfoNow
					.get("energyUse"))
					+ " ㎥"); // 사용량
		}
		
		returnMap.put("saving", numFormatter
				.format(energyInfoNow.get("saving"))); // 절감율
		returnMap.put("searchDateType", averageInfo.get("searchDateType")); // 주기
																			// 타입
																			// (
																			// 1
																			// :
																			// 일,
																			// 3
																			// :
																			// 주,
																			// 4
																			// :
																			// 월,
																			// 8
																			// :
																			// 년
																			// )

		return returnMap;
	}

	public Map<String, Object> setEnergyAvg2(Map<String, Object> params) {

		Map<String, Object> result = new HashMap<String, Object>();

		try {
			String supplierId = StringUtils.defaultIfEmpty((String) params
					.get("supplierId"), "0");
			String years = StringUtils.defaultIfEmpty((String) params
					.get("years"), ""); // 평균에 사용할 년도
			String descr = StringUtils.defaultIfEmpty((String) params
					.get("descr"), "0"); // 설명
			String used = StringUtils.defaultIfEmpty((String) params
					.get("used"), "false"); // 사용유무
			String avgInfoId = (String) params.get("avgInfoId");

			String[] yearList = StringUtils.split(years, ",");

			// 1. AverageUsage 테이블에 설명 , 사용유무 정보만 insert하고 key값을 구한다.
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Calendar c = Calendar.getInstance();
			String createDate = dateFormat.format(c.getTime());

			AverageUsage resultAu = new AverageUsage();
			Integer resultAuKey = null;

			AverageUsage au = new AverageUsage();
			au.setCreateDate(createDate);
			au.setDescr(descr);
			au.setUsed(Boolean.valueOf(used));
			if (!avgInfoId.isEmpty()) {

				au.setId(Integer.parseInt(avgInfoId));

				averageUsageDao.updateSql(au);
				resultAuKey = Integer.parseInt(avgInfoId);
			} else {

				if (au != null && au.getUsed()) {

					averageUsageDao.usageInitSql(au);
				}

				resultAu = averageUsageDao.add(au);
				resultAuKey = resultAu.getId();
			}

			// averageUsageDao.saveOrUpdate(au);
			// averageUsageDao.update(au);
			// averageUsageDao.add(au);

			// 2. 년도별 , 에너지 타입별(전기,가스,수도) 정보를 구하여 AverageUseageBase테이블에 insert
			// 한다.

			String locationId = null;
			Integer[] locationChildren = null;

			ArrayList<Location> rootLocation = (ArrayList<Location>) locationDao
					.getParents(Integer.parseInt(supplierId));
			if (rootLocation != null) {

				locationId = String.valueOf(rootLocation.get(0).getId());

				Iterator a = rootLocation.get(0).getChildren().iterator();
				locationChildren = new Integer[rootLocation.get(0)
						.getChildren().size()];
				int b = 0;
				while (a.hasNext()) {
					Location z = (Location) a.next();
					locationChildren[b] = z.getId();
					++b;
				}

			}

			Condition cdtLocationChildren = new Condition("location.id",
					locationChildren, null, Restriction.IN);

			if (!avgInfoId.isEmpty()) { // update 하는경우 이전에 등록된 정보를 삭제한후 다시
										// insert함.

				AverageUsageBase aubDel = new AverageUsageBase();
				aubDel.setAvgUsageId(resultAuKey);
				averageUsageBaseDao.deleteAvgUsageId(aubDel);
			}

			Double sum = 0.0;
			Double sumCo2 = 0.0;
			Double sumAvg = 0.0;
			Double sumCo2Avg = 0.0;
			List<AverageUsageBase> bases = new ArrayList<AverageUsageBase>();

			for (int i = 0; i < yearList.length; i++) {
				String year = yearList[i].toString();

				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
				SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy");
				Calendar c1 = Calendar.getInstance();
				c1.set(Integer.parseInt(year), 0, 1);
				String startDate = formatter.format(c1.getTime());
				c1.set(Integer.parseInt(year), 11, 1);
				String endDate = formatter.format(c1.getTime());

				Double sumEm = dayMonthMaxMinAvgSum(cdtLocationChildren, "EM",
						"1.3.1.1", 1, "month", "sum", startDate, endDate);
				Double sumEmCo2 = dayMonthMaxMinAvgSum(cdtLocationChildren,
						"EM", "1.3.1.1", 0, "month", "sum", startDate, endDate);

				Double sumWm = dayMonthMaxMinAvgSum(cdtLocationChildren, "WM",
						"1.3.1.2", 1, "month", "sum", startDate, endDate);
				Double sumWmCo2 = dayMonthMaxMinAvgSum(cdtLocationChildren,
						"WM", "1.3.1.2", 0, "month", "sum", startDate, endDate);

				Double sumGm = dayMonthMaxMinAvgSum(cdtLocationChildren, "GM",
						"1.3.1.3", 1, "month", "sum", startDate, endDate);
				Double sumGmCo2 = dayMonthMaxMinAvgSum(cdtLocationChildren,
						"GM", "1.3.1.3", 0, "month", "sum", startDate, endDate);
				
				Double sumHm = dayMonthMaxMinAvgSum(cdtLocationChildren, "HM",
						"1.3.1.4", 1, "month", "sum", startDate, endDate);
				Double sumHmCo2 = dayMonthMaxMinAvgSum(cdtLocationChildren,
						"HM", "1.3.1.4", 0, "month", "sum", startDate, endDate);

				sum = sumEm + sumWm + sumGm + sumHm;
				sumCo2 = sumEmCo2 + sumWmCo2 + sumGmCo2 + sumHmCo2;

				AverageUsageBase aubEm = new AverageUsageBase();
				aubEm.id.setAvgUsageId(resultAuKey);
				aubEm.id.setUsageYear(formatter1.format(c1.getTime()));
				aubEm.id.setSupplyType(0); // 전기
				aubEm.setUsageValue(sumEm);
				aubEm.setCo2Value(sumEmCo2);

				AverageUsageBase aubGm = new AverageUsageBase();
				aubGm.id.setAvgUsageId(resultAuKey);
				aubGm.id.setUsageYear(formatter1.format(c1.getTime()));
				aubGm.id.setSupplyType(1); // 가스
				aubGm.setUsageValue(sumGm);
				aubGm.setCo2Value(sumGmCo2);

				AverageUsageBase aubWm = new AverageUsageBase();
				aubWm.id.setAvgUsageId(resultAuKey);
				aubWm.id.setUsageYear(formatter1.format(c1.getTime()));
				aubWm.id.setSupplyType(2); // 수도
				aubWm.setUsageValue(sumWm);
				aubWm.setCo2Value(sumWmCo2);
				
				AverageUsageBase aubHm = new AverageUsageBase();
				aubHm.id.setAvgUsageId(resultAuKey);
				aubHm.id.setUsageYear(formatter1.format(c1.getTime()));
				aubHm.id.setSupplyType(3); // 열량
				aubHm.setUsageValue(sumHm);
				aubHm.setCo2Value(sumHmCo2);

				AverageUsageBase aubE = averageUsageBaseDao.saveOrUpdate(aubEm);
				AverageUsageBase aubG = averageUsageBaseDao.saveOrUpdate(aubGm);
				AverageUsageBase aubW = averageUsageBaseDao.saveOrUpdate(aubWm);
				AverageUsageBase aubH = averageUsageBaseDao.saveOrUpdate(aubHm);

				bases.add(aubE);
				bases.add(aubG);
				bases.add(aubW);
				bases.add(aubH);
			}

			// 3. 에너지 , 년도를 합산한 평균값을 이용하여 AverageUseage에 년/월/일의 평균값을 입력한다.
			sumAvg = sum / yearList.length;
			sumCo2Avg = sumCo2 / yearList.length;

			Calendar c2 = Calendar.getInstance();
			c2.set(c2.get(Calendar.YEAR), 11, 31);

			resultAu.setAvgUsageYear(sumAvg);
			resultAu.setAvgUsageMonth(sumAvg / 12);
			resultAu.setAvgUsageWeek(sumAvg
					/ (c2.getMaximum(Calendar.WEEK_OF_YEAR) - 1));
			resultAu.setAvgUsageDay(sumAvg / 365);
			resultAu.setAvgCo2Year(sumCo2Avg);
			resultAu.setAvgCo2Month(sumCo2Avg / 12);
			resultAu.setAvgCo2Week(sumCo2Avg
					/ (c2.getMaximum(Calendar.WEEK_OF_YEAR) - 1));
			resultAu.setAvgCo2Day(sumCo2Avg / 365);
			if (!avgInfoId.isEmpty()) {

				resultAu.setId(resultAuKey);
				resultAu.setBases(bases);
				// averageUsageDao.update( resultAu );
				averageUsageDao.updateSql(resultAu);
			} else {

				averageUsageDao.add(resultAu);
			}

			// averageUsageDao.update( auObj );
			// averageUsageDao.saveOrUpdate( resultAu );

			result.put("result", "Y");

		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", "N");
		}

		return result;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// 정렬하기
		Vector vc = new Vector();
		vc.addElement("01");
		vc.addElement("03");
		vc.addElement("01");
		vc.addElement("02");

		Collections.sort(vc);
		System.out.println("ComparableComparator");
		for (int i = 0; i < vc.size(); i++) {
			System.out.println("vc.get(" + i + ")" + vc.get(i));
		}

		Collections
				.sort(
						vc,
						new org.apache.commons.collections.comparators.ComparableComparator());
		System.out.println("ComparableComparator");
		for (int i = 0; i < vc.size(); i++) {
			System.out.println("vc.get(" + i + ")" + vc.get(i));
		}
		Collections
				.sort(
						vc,
						new org.apache.commons.collections.comparators.NullComparator());
		System.out.println("NullComparator");
		for (int i = 0; i < vc.size(); i++) {
			System.out.println("vc.get(" + i + ")" + vc.get(i));
		}
		Collections
				.sort(
						vc,
						new org.apache.commons.collections.comparators.ReverseComparator());
		System.out.println("ReverseComparator");
		for (int i = 0; i < vc.size(); i++) {
			System.out.println("vc.get(" + i + ")" + vc.get(i));
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
		String[] arrStr = (String[]) temp.toArray(new String[temp.size()]);
		Arrays.sort(arrStr, String.CASE_INSENSITIVE_ORDER);

		for (int i = 0; i < arrStr.length; i++) {

			if (i == 0) {
				nana.add(arrStr[i]);
			}
			if (i > 0 && !arrStr[i - 1].equals(arrStr[i])) {

				// System.out.println(arrStr[i]);
				nana.add(arrStr[i]);
			}
		}
		System.out.println("#" + nana.toString());

		List<Object> temp1 = new ArrayList<Object>();
		List<HashMap> A1 = new ArrayList<HashMap>();
		A1.add((HashMap) new HashMap().put("year", "2010"));
		A1.add((HashMap) new HashMap().put("year", "2008"));
		A1.add((HashMap) new HashMap().put("year", "2009"));
		List<HashMap> B1 = new ArrayList<HashMap>();
		B1.add((HashMap) new HashMap().put("year", "2005"));
		B1.add((HashMap) new HashMap().put("year", "2008"));
		B1.add((HashMap) new HashMap().put("year", "2001"));
		List<HashMap> C1 = new ArrayList<HashMap>();
		C1.add((HashMap) new HashMap().put("year", "2009"));
		C1.add((HashMap) new HashMap().put("year", "2000"));

		temp1.addAll(A1);
		temp1.addAll(B1);
		temp1.addAll(C1);

		System.out.println(temp1.toString());

		// Collections.sort( temp1 );

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMM");

		String startDate = "20100108";

		int y = Integer.parseInt(startDate.substring(0, 4));
		int m = Integer.parseInt(startDate.substring(4, 6));
		int d = Integer.parseInt(startDate.substring(6, 8));

		System.out.println(" 현재 날짜 : " + startDate);

		Calendar getOldDay = Calendar.getInstance();
		getOldDay.set(y, m - 1, d);
		getOldDay.set(getOldDay.get(Calendar.YEAR), getOldDay
				.get(Calendar.MONTH), getOldDay.get(Calendar.DATE) - 1);
		System.out.println("[" + startDate + "] " + "전일 날짜 : "
				+ formatter.format(getOldDay.getTime()));

		Calendar getOldDayYear = Calendar.getInstance();
		getOldDayYear.set(y, m - 1, d);
		getOldDayYear.set(getOldDayYear.get(Calendar.YEAR) - 1, getOldDayYear
				.get(Calendar.MONTH), getOldDayYear.get(Calendar.DATE));
		System.out.println("[" + startDate + "] " + "전년 동일 날짜 : "
				+ formatter.format(getOldDayYear.getTime()));

		System.out.println("------------------");

		Calendar getOldWeek = Calendar.getInstance();
		getOldWeek.set(y, m - 1, d);
		getOldWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 해당 주의 일요일로 셋팅.
		System.out.println("[" + startDate + "] " + "금주 일요일 날짜 : "
				+ formatter.format(getOldWeek.getTime()));
		getOldWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // 해당 주의 토요일로
																	// 셋팅.
		System.out.println("[" + startDate + "] " + "금주 토요일 날짜 : "
				+ formatter.format(getOldWeek.getTime()));

		getOldWeek.set(Calendar.WEEK_OF_YEAR, getOldWeek
				.get(Calendar.WEEK_OF_YEAR) - 1); // calendar를 전주로 셋팅
		getOldWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 해당 주의 일요일로 셋팅.
		System.out.println("[" + startDate + "] " + "전주 일요일 날짜 : "
				+ formatter.format(getOldWeek.getTime()));
		getOldWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // 해당 주의 토요일로
																	// 셋팅.
		System.out.println("[" + startDate + "] " + "전주 토요일 날짜 : "
				+ formatter.format(getOldWeek.getTime()));

		Calendar getOldWeekYear = Calendar.getInstance();
		getOldWeekYear.set(y, m - 1, d);
		getOldWeekYear.set(getOldWeekYear.get(Calendar.YEAR) - 1,
				getOldWeekYear.get(Calendar.MONTH), getOldWeekYear
						.get(Calendar.DATE));
		getOldWeekYear.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 해당 주의 일요일로
																	// 셋팅.
		System.out.println("[" + startDate + "] " + "전년 동주 일요일 날짜 : "
				+ formatter.format(getOldWeekYear.getTime()));
		getOldWeekYear.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // 해당 주의
																		// 토요일로
																		// 셋팅.
		System.out.println("[" + startDate + "] " + "전년 동주 토요일 날짜 : "
				+ formatter.format(getOldWeekYear.getTime()));
		System.out.println("------------------");

		Calendar getOldMonth = Calendar.getInstance();
		getOldMonth.set(y, m - 1, d);
		getOldMonth.set(getOldMonth.get(Calendar.YEAR), getOldMonth
				.get(Calendar.MONTH) - 1, 1);
		System.out.println("[" + startDate + "] " + "전월 시작 날짜 : "
				+ formatter.format(getOldMonth.getTime()));
		getOldMonth.set(getOldMonth.get(Calendar.YEAR), getOldMonth
				.get(Calendar.MONTH), getOldMonth
				.getActualMaximum(Calendar.DAY_OF_MONTH));
		System.out.println("[" + startDate + "] " + "전월 마지막 날짜 : "
				+ formatter.format(getOldMonth.getTime()));

		Calendar getOldMonthYear = Calendar.getInstance();
		getOldMonthYear.set(y, m - 1, d);
		getOldMonthYear.set(getOldMonthYear.get(Calendar.YEAR) - 1,
				getOldMonthYear.get(Calendar.MONTH), 1);
		System.out.println("[" + startDate + "] " + "전년 동월 시작날짜 : "
				+ formatter.format(getOldMonthYear.getTime()));
		getOldMonthYear.set(getOldMonthYear.get(Calendar.YEAR), getOldMonthYear
				.get(Calendar.MONTH), getOldMonthYear
				.getActualMaximum(Calendar.DAY_OF_MONTH));
		System.out.println("[" + startDate + "] " + "전년 동월 마지막 날짜 : "
				+ formatter.format(getOldMonthYear.getTime()));

		System.out.println("------------------");

		Calendar getOldYear = Calendar.getInstance();
		getOldYear.set(y, m - 1, d);
		getOldYear.set(getOldYear.get(Calendar.YEAR) - 1, getOldYear
				.get(Calendar.MONTH), getOldYear.get(Calendar.DATE));
		System.out.println("[" + startDate + "] " + "전년 날짜 : "
				+ formatter.format(getOldYear.getTime()));

		Calendar getOldYearYear = Calendar.getInstance();
		getOldYearYear.set(y, m - 1, d);
		getOldYearYear.set(getOldYearYear.get(Calendar.YEAR) - 2,
				getOldYearYear.get(Calendar.MONTH), getOldYearYear
						.get(Calendar.DATE));
		System.out.println("[" + startDate + "] " + "전전년  날짜 : "
				+ formatter.format(getOldYearYear.getTime()));

		System.out.println("------------------");

		Double energyGoalUse = 0.0;
		Double energyAvgUse = 30.0;
		Double savingGoal = 10.0;
		energyGoalUse = energyAvgUse * ((100 - savingGoal) * 0.01);

		System.out.println(energyGoalUse);

		SimpleDateFormat formatter11 = new SimpleDateFormat("yyyyMMdd");
		Calendar getMonth1 = Calendar.getInstance();
		getMonth1.set(y, 0, 1);

		for (int i = 0; i < getMonth1.getActualMaximum(Calendar.WEEK_OF_YEAR) + 1; i++) {

			Calendar getWeek = Calendar.getInstance();
			getWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);

			getWeek.set(Calendar.WEEK_OF_YEAR, i + 1);

			System.out.print("[" + i + "] "
					+ formatter11.format(getWeek.getTime()));
			getWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // 해당 주의 토요일로
																	// 셋팅.
			System.out.println(" ~ " + formatter11.format(getWeek.getTime()));
		}

		Calendar getMonth = Calendar.getInstance();
		getMonth.set(2010, 0, 1);

		for (int i = 0; i < getMonth.getActualMaximum(Calendar.MONTH) + 1; i++) {

			getMonth.set(getMonth.get(Calendar.YEAR), i, 1);
			String monthS = formatter.format(getMonth.getTime());
			// logger.debug( "[" + startDate + "] " + "월 시작 날짜 : " + monthS );

			getMonth.set(Calendar.DATE, getMonth
					.getActualMaximum(Calendar.DAY_OF_MONTH));
			String monthE = formatter.format(getMonth.getTime());
			// logger.debug( "[" + startDate + "] " + "월 마지막 날짜 : " + monthE );

			System.out.println(monthS + "~" + monthE);
		}

		Calendar getMonthY = Calendar.getInstance();
		getMonthY.set(y, 0, 1);

		for (int i = y; i > y - 10; i--) {

			getMonthY.set(i, 0, 1);
			String monthS = formatter.format(getMonthY.getTime());
			// logger.debug( "[" + startDate + "] " + "월 시작 날짜 : " + monthS );

			getMonthY.set(Calendar.DATE, getMonthY
					.getActualMaximum(Calendar.DAY_OF_MONTH));
			String monthE = formatter.format(getMonthY.getTime());
			// logger.debug( "[" + startDate + "] " + "월 마지막 날짜 : " + monthE );
			System.out.println(monthS + " , " + monthE);
		}

		Calendar getWeek = Calendar.getInstance();
		getWeek.set(y, 0, 1);
		getWeek.getTime();
		getWeek.set(Calendar.WEEK_OF_YEAR, 1);
		// getWeek.getTime();
		// getWeek.set( Calendar.DAY_OF_WEEK, Calendar.SUNDAY );

		String ss = formatter.format(getWeek.getTime());

		getWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // 해당 주의 토요일로 셋팅.

		String ss2 = formatter.format(getWeek.getTime());

		System.out.println(ss + " ~ " + ss2);

	}

	public Map<String, Object> getDayMonthRangeAvg(Map<String, String> condition) {

		Double energyAvgUse = 0.0; // 평균 사용량
		Double energyAvgCo2 = 0.0; // 평균 Co2

		try {

			String supplierId = StringUtils.defaultIfEmpty((String) condition
					.get("supplierId"), "0");
			String searchDateType = StringUtils.defaultIfEmpty(
					(String) condition.get("searchDateType"),
					CommonConstants.DateType.DAILY.getCode());
			Integer locationId = Integer.parseInt(StringUtils.defaultIfEmpty(
					(String) condition.get("locationId"), "0"));
			String energyType = (String) condition.get("energyType");
			String meterTypeCode = (String) condition.get("meterTypeCode");

			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			Calendar c = Calendar.getInstance();

			String startDate = "";
			String endDate = "";

			String energyTypeTemp = "";
			if ("EM".equals(energyType)) {

				energyTypeTemp = "0";
			} else if ("GM".equals(energyType)) {

				energyTypeTemp = "1";
			} else if ("WM".equals(energyType)) {

				energyTypeTemp = "2";
			} else if ("HM".equals(energyType)) {

				energyTypeTemp = "3";
			}

			if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {
				startDate = formatter.format(c.getTime());
				endDate = formatter.format(c.getTime());
			} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
					searchDateType)) {
				c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				startDate = formatter.format(c.getTime());
				c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
				endDate = formatter.format(c.getTime());
			} else if (CommonConstants.DateType.MONTHLY.getCode().equals(
					searchDateType)) {
				c.set(Calendar.DATE, c.getActualMinimum(Calendar.DAY_OF_MONTH));
				startDate = formatter.format(c.getTime());
				c.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
				endDate = formatter.format(c.getTime());
			} else if (CommonConstants.DateType.QUARTERLY.getCode().equals(
					searchDateType)) {
				c.set(c.get(Calendar.YEAR), c.getActualMinimum(Calendar.MONTH),
						c.getActualMinimum(Calendar.DAY_OF_MONTH));
				startDate = formatter.format(c.getTime());
				c.set(c.get(Calendar.YEAR), c.getActualMaximum(Calendar.MONTH),
						c.getActualMaximum(Calendar.DAY_OF_MONTH));
				endDate = formatter.format(c.getTime());
			}

			List<EnergySavingGoal2> esgList = null;
			esgList = energySavingGoal2Dao
					.getEnergySavingGoal2ListByAverageUsage(searchDateType,
							energyTypeTemp, endDate, Integer
									.parseInt(supplierId), averageUsageDao
									.getAverageUsageByUsed().getId());

			if (esgList != null && !esgList.isEmpty()) {

				EnergySavingGoal2 esg = esgList.get(0);
				String years = esg.getAverageUsage().getBasesToString();
				String[] yearList = StringUtils.split(years, ",");

				if (CommonConstants.DateType.DAILY.getCode().equals(
						searchDateType)) {
					energyAvgUse = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, 1, "day", startDate,
							endDate);
					energyAvgCo2 = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, 0, "day", startDate,
							endDate);
				} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
						searchDateType)) {
					energyAvgUse = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, 1, "day", startDate,
							endDate);
					energyAvgCo2 = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, 0, "day", startDate,
							endDate);
				} else if (CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)) {
					energyAvgUse = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, 1, "month", startDate,
							endDate);
					energyAvgCo2 = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, 0, "month", startDate,
							endDate);
				} else if (CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) {
					energyAvgUse = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, 1, "month", startDate,
							endDate);
					energyAvgCo2 = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, 0, "month", startDate,
							endDate);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("averageUsage", energyAvgUse);
		result.put("averageCo2Usage", energyAvgCo2);

		return result;
	}
	
	public Map<String, Object> getDayMonthRangeAvgByUsed(Map<String, String> condition) {

		Double energyAvgUse = 0.0; // 평균 사용량
		Double energyAvgCo2 = 0.0; // 평균 Co2

		try {

			String supplierId = StringUtils.defaultIfEmpty((String) condition
					.get("supplierId"), "0");
			String searchDateType = StringUtils.defaultIfEmpty(
					(String) condition.get("searchDateType"),
					CommonConstants.DateType.DAILY.getCode());
			String energyType = (String) condition.get("energyType");
			String meterTypeCode = (String) condition.get("meterTypeCode");

			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			Calendar c = Calendar.getInstance();

			String startDate = "";
			String endDate = "";
			if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {
				startDate = formatter.format(c.getTime());
				endDate = formatter.format(c.getTime());
			} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
					searchDateType)) {
				c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
				startDate = formatter.format(c.getTime());
				c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
				endDate = formatter.format(c.getTime());
			} else if (CommonConstants.DateType.MONTHLY.getCode().equals(
					searchDateType)) {
				c.set(Calendar.DATE, c.getActualMinimum(Calendar.DAY_OF_MONTH));
				startDate = formatter.format(c.getTime());
				c.set(Calendar.DATE, c.getActualMaximum(Calendar.DAY_OF_MONTH));
				endDate = formatter.format(c.getTime());
			} else if (CommonConstants.DateType.QUARTERLY.getCode().equals(
					searchDateType)) {
				c.set(c.get(Calendar.YEAR), c.getActualMinimum(Calendar.MONTH),
						c.getActualMinimum(Calendar.DAY_OF_MONTH));
				startDate = formatter.format(c.getTime());
				c.set(c.get(Calendar.YEAR), c.getActualMaximum(Calendar.MONTH),
						c.getActualMaximum(Calendar.DAY_OF_MONTH));
				endDate = formatter.format(c.getTime());
			}
			
			AverageUsage avgUsage = averageUsageDao.getAverageUsageByUsed();
			
			if(avgUsage != null) {
				String years = avgUsage.getBasesToString();
				String[] yearList = StringUtils.split(years, ",");
	
				if (CommonConstants.DateType.DAILY.getCode().equals(
						searchDateType)) {
					energyAvgUse = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, 1, "day", startDate,
							endDate);
					energyAvgCo2 = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, 0, "day", startDate,
							endDate);
				} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
						searchDateType)) {
					energyAvgUse = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, 1, "day", startDate,
							endDate);
					energyAvgCo2 = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, 0, "day", startDate,
							endDate);
				} else if (CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)) {
					energyAvgUse = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, 1, "month", startDate,
							endDate);
					energyAvgCo2 = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, 0, "month", startDate,
							endDate);
				} else if (CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) {
					energyAvgUse = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, 1, "month", startDate,
							endDate);
					energyAvgCo2 = dayMonthRangeAvg(supplierId, yearList,
							energyType, meterTypeCode, 0, "month", startDate,
							endDate);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("averageUsage", energyAvgUse);
		result.put("averageCo2Usage", energyAvgCo2);

		return result;
	}
	
	public Map<String, Object> getAvgByUsed(Map<String, String> condition) {

		Double energyAvgUse = 0.0; // 평균 사용량
		Double energyAvgCo2 = 0.0; // 평균 Co2

		try {

			String supplierId = StringUtils.defaultIfEmpty((String) condition
					.get("supplierId"), "0");
			String searchDateType = StringUtils.defaultIfEmpty(
					(String) condition.get("searchDateType"),
					CommonConstants.DateType.DAILY.getCode());
			String energyType = (String) condition.get("energyType");
			String meterTypeCode = (String) condition.get("meterTypeCode");
			String locationId = (String) condition.get("locationId");

			AverageUsage avgUsage = averageUsageDao.getAverageUsageByUsed();
			
			if(avgUsage != null) {
				String years = avgUsage.getBasesToString();
				String[] yearList = StringUtils.split(years, ",");
	
				if (CommonConstants.DateType.DAILY.getCode().equals(
						searchDateType)) {
					energyAvgUse = getAvgUsage(supplierId, searchDateType,
							energyType, meterTypeCode, 1, "day", locationId);
					energyAvgCo2 = getAvgUsage(supplierId, searchDateType,
							energyType, meterTypeCode, 0, "day", locationId);
				} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
						searchDateType)) {
					energyAvgUse = getAvgUsage(supplierId, searchDateType,
							energyType, meterTypeCode, 1, "day", locationId);
					energyAvgCo2 = getAvgUsage(supplierId, searchDateType,
							energyType, meterTypeCode, 0, "day", locationId);
				} else if (CommonConstants.DateType.MONTHLY.getCode().equals(
						searchDateType)) {
					energyAvgUse = getAvgUsage(supplierId, searchDateType,
							energyType, meterTypeCode, 1, "month", locationId);
					energyAvgCo2 = getAvgUsage(supplierId, searchDateType,
							energyType, meterTypeCode, 0, "month", locationId);
				} else if (CommonConstants.DateType.QUARTERLY.getCode().equals(
						searchDateType)) {
					energyAvgUse = getAvgUsage(supplierId, searchDateType,
							energyType, meterTypeCode, 1, "month", locationId);
					energyAvgCo2 = getAvgUsage(supplierId, searchDateType,
							energyType, meterTypeCode, 0, "month", locationId);
				}
			}
		} catch (Exception e) {
			logger.error(e,e);
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("averageUsage", energyAvgUse);
		result.put("averageCo2Usage", energyAvgCo2);

		return result;
	}
	
	/**
	 * 에너지 타입에 따른 전체데이터의 평균값을 추출함.
	 * 
	 * @param supplierId
	 *            : supplierId를 이용하여 최상위 로케이션 을 부모로 하는 로케이션 정보를 추출하여 meter정보를
	 *            추출하기 위한 조건식 생성을 위해 사용됨.
	 * @param searchDateType
	 * @param energyType
	 *            : 전기 : EM , 가스 : GM , 수도 : WM 열량 : HM
	 * @param meterTypeCode
	 * @param channel
	 *            : 탄소배출량 : 0 , 사용량 : 1
	 * @param dayMonth
	 *            : day검침량 테이블 조회 , month검침량 테이블조회
	 * @return
	 */
	private Double getAvgUsage(String supplierId, String searchDateType,
			String energyType, String meterTypeCode, int channel, String dayMonth, String locationId) {

		Double returnValue = 0.0;

		Condition cdtLocationChildren = null;
		Set<Condition> condition = new HashSet<Condition>();
		
		Condition conditionSupplierId = new Condition("supplierId", new Object[] { Integer.parseInt(supplierId) }, null,Restriction.EQ);
		Condition conditionlocationId = new Condition("id", new Object[] { Integer.parseInt(locationId) }, null,Restriction.EQ);
		condition.add(conditionSupplierId);
		condition.add(conditionlocationId);
		List<Location> parentLocationList = locationDao.findByConditions(condition);

		if (parentLocationList.size() > 0) {
			Location parentLocation = parentLocationList.get(0);
			Iterator a = parentLocation.getChildren().iterator();
			Integer[] locationChildren = new Integer[parentLocation.getChildren().size()];
			int b = 0;
			while (a.hasNext()) {
				Location z = (Location) a.next();
				locationChildren[b] = z.getId();
				++b;
			}

			cdtLocationChildren = new Condition("location.id",
					locationChildren, null, Restriction.IN);
		}
		
		Double dataCountByType = new Double(0.0);
		Double sum = new Double(0.0);
		
		if(CommonConstants.DateType.DAILY.getCode().equals(searchDateType)
				|| CommonConstants.DateType.MONTHLY.getCode().equals(searchDateType)) {
			dataCountByType = getUsageCountByType(cdtLocationChildren, energyType,
					meterTypeCode, channel, dayMonth, "groupby", searchDateType);
	
			sum = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType, meterTypeCode, channel, dayMonth, "sum", "", "");
			
		} else if(CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)) {
			dataCountByType = getUsageCountByType(cdtLocationChildren, energyType,
					meterTypeCode, channel, dayMonth, "groupby", searchDateType);
			sum = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType, meterTypeCode, channel, dayMonth, "sum", "", "");
			
		}  else if(CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {
			dataCountByType = getUsageCountByType(cdtLocationChildren, energyType,
					meterTypeCode, channel, dayMonth, "groupby", searchDateType);
			sum = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType, meterTypeCode, channel, dayMonth, "sum", "", "");
			
		}
		
		
		if(dataCountByType == new Double(0.0) || dataCountByType == 0) {
			returnValue =  new Double(0.0);
		} else {
			returnValue = (Double) ObjectUtils.defaultIfNull(
					(sum / dataCountByType), new Double(0.0));
		}

		return returnValue;
	}
	
	/**
	 * 에너지 타입에따라 전체데이터의 최대/최소/평균/합 수치를 반환함.
	 * 
	 * @param cdtLocationChildren
	 * @param energyType
	 *            : 전기 = EM , 가스 = GM , 수도 = WM 열량 = HM
	 * @param meterTypeCode
	 *            : 예)1.3.1.1 = EnergyMeter , 1.3.1.2 = WaterMeter , 1.3.1.3 =
	 *            GasMeter 1.3.1.4 = HeatMeter
	 * @param channel
	 * @param dateType
	 *            : day : day테이블 조회 , month : month테이블 조회
	 * @param valueType
	 *            : 최대,최소,평균, 합, 그룹
	 * @return
	 */
	private Double getUsageCountByType(Condition cdtLocationChildren,
			String energyType, String meterTypeCode, int channel,
			String dateType, String valueType, String searchDateType) {

		Double returnValue = 0.0;
		List<Object> groupData = null;

		Set<Condition> conditionSetMeters = new HashSet<Condition>();

		conditionSetMeters.add(cdtLocationChildren);

		Code meterTypeCodeObject = codeDao.getCodeIdByCodeObject(meterTypeCode);

		Condition conditionMeterType = new Condition("meterType.id",
				new Object[] { meterTypeCodeObject.getId() }, null,
				Restriction.EQ);
		conditionSetMeters.add(conditionMeterType);

		List<Meter> meterList = meterDao.findByConditions(conditionSetMeters);
		Integer[] meterIds = new Integer[meterList.size()];
		for (int i = 0; i < meterList.size(); i++) {
			Meter mm = meterList.get(i);
			meterIds[i] = mm.getId();
		}

		if (meterIds.length > 0) {

			Set<Condition> conditionSetGroupFunction = new HashSet<Condition>();

			Condition conditionChannel = new Condition("id.channel",
					new Object[] { channel }, null, Restriction.EQ);
			conditionSetGroupFunction.add(conditionChannel);

			Condition conditionMeterIds = new Condition("meter.id", meterIds,
					null, Restriction.IN);

			conditionSetGroupFunction.add(conditionMeterIds);
			
			if ("EM".equals(energyType)) {

				if ("day".equals(dateType)) {

					if(CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)) {
						String minDate = null;
						List<Object> minDateList = dayEMDao.getDayEMsMaxMinAvgSum(
								conditionSetGroupFunction, "minDate");
						if(minDateList.size() > 0) {
							minDate = (String) minDateList.get(0);
						}
						
						returnValue = setWeeklyCount(minDate);
						
					} else {
						groupData = dayEMDao.getDayEMsCount(
								conditionSetGroupFunction, valueType);
					}
				} else if ("month".equals(dateType)) {

					if(CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {
						String minDate = null;
						int quarter = 0;
						List<Object> minDateList = monthEMDao.getMonthEMsMaxMinAvgSum(
								conditionSetGroupFunction, "minDate");
						if(minDateList.size() > 0) {
							minDate = (String) minDateList.get(0);
						}
						
						returnValue = setQuarterCount(minDate);
						
					} else {
						groupData = monthEMDao.getMonthEMsCount(
								conditionSetGroupFunction, valueType);
						
					}
					
				}
			} else if ("GM".equals(energyType)) {

				if ("day".equals(dateType)) {

					if(CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)) {
						String minDate = null;
						List<Object> minDateList = dayGMDao.getDayGMsMaxMinAvgSum(
								conditionSetGroupFunction, "minDate");
						if(minDateList.size() > 0) {
							minDate = (String) minDateList.get(0);
						}
						
						returnValue = setWeeklyCount(minDate);
						
					} else {
						groupData = dayGMDao.getDayGMsCount(
								conditionSetGroupFunction, valueType);
					}
					
				} else if ("month".equals(dateType)) {

					if(CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {
						String minDate = null;
						int quarter = 0;
						List<Object> minDateList = monthGMDao.getMonthGMsMaxMinAvgSum(
								conditionSetGroupFunction, "minDate");
						if(minDateList.size() > 0) {
							minDate = (String) minDateList.get(0);
						}
						
						returnValue = setQuarterCount(minDate);

					} else {
						groupData = monthGMDao.getMonthGMsCount(
								conditionSetGroupFunction, valueType);
						
					}
				}
			} else if ("WM".equals(energyType)) {

				if ("day".equals(dateType)) {

					if(CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)) {
						String minDate = null;
						List<Object> minDateList = dayWMDao.getDayWMsMaxMinAvgSum(
								conditionSetGroupFunction, "minDate");
						if(minDateList.size() > 0) {
							minDate = (String) minDateList.get(0);
						}
						
						returnValue = setWeeklyCount(minDate);
						
					} else {
						groupData = dayWMDao.getDayWMsCount(
								conditionSetGroupFunction, valueType);
					}
					
				} else if ("month".equals(dateType)) {

					if(CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {
						String minDate = null;
						List<Object> minDateList = monthWMDao.getMonthWMsMaxMinAvgSum(
								conditionSetGroupFunction, "minDate");
						if(minDateList.size() > 0) {
							minDate = (String) minDateList.get(0);
						}
						
						returnValue = setQuarterCount(minDate);
						
					} else {
						groupData = monthWMDao.getMonthWMsCount(
								conditionSetGroupFunction, valueType);
					}
				}
			}  else if ("HM".equals(energyType)) {

				if ("day".equals(dateType)) {

					if(CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType)) {
						String minDate = null;
						List<Object> minDateList = dayHMDao.getDayHMsMaxMinAvgSum(
								conditionSetGroupFunction, "minDate");
						if(minDateList.size() > 0) {
							minDate = (String) minDateList.get(0);
						}
						
						returnValue = setWeeklyCount(minDate);
						
					} else {
						groupData = dayHMDao.getDayHMsCount(
								conditionSetGroupFunction, valueType);
					}
					
				} else if ("month".equals(dateType)) {
					
					if(CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {
						String minDate = null;
						List<Object> minDateList = monthHMDao.getMonthHMsMaxMinAvgSum(
								conditionSetGroupFunction, "minDate");
						if(minDateList.size() > 0) {
							minDate = (String) minDateList.get(0);
						}
						
						returnValue = setQuarterCount(minDate);
						
					} else {
						groupData = monthHMDao.getMonthHMsCount(
								conditionSetGroupFunction, valueType);
					}
				}
			} 
		}

		if(!CommonConstants.DateType.WEEKLY.getCode().equals(searchDateType) && !CommonConstants.DateType.QUARTERLY.getCode().equals(searchDateType)) {
			returnValue = Double.parseDouble(groupData.get(0).toString());
		}
		
		return returnValue;
	}
	
	/**
	 * @method name : setWeeklyCount
	 * @method desc : 최초날짜 값을 받아 최초날짜부터 현재 시간까지의 주기 수를 구한다.
	 * 
	 * @param minDate
	 * @return
	 */
	private Double setWeeklyCount(String minDate) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmss");
		
		Calendar minDateC = Calendar.getInstance();
		minDateC.set(Integer.parseInt(minDate.substring(0,4)), 
				Integer.parseInt(minDate.substring(4,6))-1, Integer.parseInt(minDate.substring(6,8)));
		//첫 번째 주의 마지막 날짜 
		minDateC.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); 
		String endDateByFirstWeek = formatter2.format(minDateC.getTime());
		double durationDay = 0;
		try {
			String firstDate = TimeUtil.getAddedDay(endDateByFirstWeek,1);
			
			Calendar today = Calendar.getInstance();
			String endDate = formatter.format(today.getTime());
		
			durationDay = TimeUtil.getDayDuration(firstDate, endDate)+1;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		double returnValue = Math.ceil(durationDay/7.0) + 1;
		
		return returnValue;
	}
	
	/**
	 * @method name : setQuarterCount
	 * @method desc : 최초날짜 값을 받아 최초날짜부터 현재 시간까지의 분기 수를 구한다.
	 * 
	 * @param minDate
	 * @return
	 */
	private Double setQuarterCount(String minDate) {
		double returnValue = 0;
		int quarter = (int)Math.ceil(Double.parseDouble(minDate.substring(4,6))/3.0);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmss");
		
		Calendar lastDateByFirstQuarter = Calendar.getInstance();
		lastDateByFirstQuarter.set(Integer.parseInt(minDate.substring(0, 4)), Integer.parseInt(String.valueOf(quarter*3))-1, 1);
		try {
			String firstQuarterDate = null;
		
			firstQuarterDate = TimeUtil.getAddedMonth(formatter2.format(lastDateByFirstQuarter.getTime()), 1);
			Calendar today = Calendar.getInstance();
			String endQuarterDate = formatter.format(today.getTime());
			
			int endMonth = Integer.parseInt(endQuarterDate.substring(0,4))*12 + Integer.parseInt(endQuarterDate.substring(4,6));
			int firstMonth = Integer.parseInt(firstQuarterDate.substring(0,4))*12 + Integer.parseInt(firstQuarterDate.substring(4,6));
			 
			int gap = endMonth - firstMonth;
			
			returnValue = Math.ceil((double)gap/3.0)+1;
		
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return returnValue;
	}

	@Override
	public Map<String, Object> setEnergySavingGoal2ByParam(String supplierId,
			String energyType, String savingGoalDateType,
			String savingGoalStartDate, Double savingGoal) {
		Map<String, Object> condition = new HashMap<String, Object>();
	    condition .put("supplierId", supplierId);
	    condition .put("energyType", energyType);
	    condition .put("savingGoalDateType", savingGoalDateType);
	    condition .put("savingGoalStartDate", savingGoalStartDate);
	    condition .put("savingGoal", savingGoal);
	    return setEnergySavingGoal2(condition);
	}
	

	private Double dayForecastSum(Map<String, Object> condition, String dateType) {

		Double R = 0.0;  //사용 비율 
		Double energyBeforeUse = 0.0; // 에너지 이전 사용량
		Double energyCurrentUse = 0.0; // 에너지 현재 사용량
		Double energyNextUse = 0.0; // 에너지 현재 사용량
		Double energyForeCastUse = 0.0; // 에너지 현재 사용량
		
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");
		int supplierId = Integer.parseInt((String) condition.get("supplierId"));
		String searchDate = endDate;
	

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String energyType = (String) condition.get("energyType"); 

		String energyCode = "";
		int hour = 0;
		
		energyType = (String) condition.get("energyType"); 
		Integer[] locationChildren = (Integer[]) condition
				.get("locationChildren");
		Condition cdtLocationChildren = new Condition("location.id",
				locationChildren, null, Restriction.IN);
		
		Properties prop = new Properties();
		
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("bems_charge.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String patternNum = prop.getProperty("pattern.format.usage");		
		DecimalFormat df = new DecimalFormat(patternNum);
		
//		logger.debug("#### 일별 ####" );
//		logger.debug("검색 날짜 : startDate : "+ startDate + ", endDate : "+ endDate);
//		logger.debug("date Type : "+ dateType);
//		logger.debug("energy Type : "+ energyType);
		
		//현재까지 사용량 
		if("EM".equals(energyType)){	
			energyCode = "1.3.1.1";	
		}else if("GM".equals(energyType)){
			energyCode ="1.3.1.3";	
		}else if("WM".equals(energyType)){
			energyCode ="1.3.1.2";
		}else if("HM".equals(energyType)){
			energyCode = "1.3.1.4";	
		}
		
		energyCurrentUse = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType ,energyCode , 1, "day", "sum", startDate, endDate);	

		Map<String, Object> dataMap =  getSearchDate (searchDate,dateType,1);
		startDate = (String) dataMap.get("startDate");
		endDate = (String) dataMap.get("endDate");
		hour   = (Integer) dataMap.get("value"); 
		
		energyBeforeUse = getEnergyUse(supplierId, dateType, energyType, startDate, endDate, 0, hour);
		
		if(	energyBeforeUse == 0.0){
			
			dataMap =  getSearchDate (searchDate,dateType,2);
			startDate = (String) dataMap.get("startDate");
			endDate = (String) dataMap.get("endDate");
			hour   = (Integer) dataMap.get("value");
	
			energyBeforeUse = getEnergyUse(supplierId, dateType, energyType,startDate, endDate, 0, hour);
			
			if(energyBeforeUse == 0.0){
			
				dataMap =  getSearchDate (searchDate,dateType,3);
				startDate = (String) dataMap.get("startDate");
				endDate = (String) dataMap.get("endDate");
				hour   = (Integer) dataMap.get("value");
				
				energyBeforeUse = getEnergyUse(supplierId, dateType, energyType,startDate, endDate, 0, hour);
				
			}
		}
		
		if(!(hour == 23)){
			energyNextUse = getEnergyUse(supplierId, dateType, energyType,startDate, endDate, hour+1, 23);
		}else {
			energyNextUse = 0.0;
		}
	
		R = (energyBeforeUse == 0.0) ? 0.0 : (energyCurrentUse/energyBeforeUse);
		energyForeCastUse = energyCurrentUse +(R*energyNextUse);		
		energyForeCastUse = Double.valueOf(df.format(energyForeCastUse)); // 에너지 사용량
		
		logger.debug(" energyCurrentUse : "+Double.valueOf(df.format(energyCurrentUse)));
		logger.debug(" energyBeforeUse : " + energyBeforeUse);
		logger.debug(" R : " + R);
		logger.debug(" energyNextUse : "+energyNextUse);
		logger.debug(" energyForeCastUse : "+energyForeCastUse);
		
		return energyForeCastUse;
	}
	
	private Double weekForecastSum(Map<String, Object> condition, String dateType) {

		Double R = 0.0;  //사용 비율 
		Double energyBeforeUse = 0.0; // 에너지 이전 사용량
		Double energyCurrentUse = 0.0; // 에너지 현재 사용량
		Double energyNextUse = 0.0; // 에너지 현재 사용량
		Double energyForeCastUse = 0.0; // 에너지 현재 사용량

		String startDate = (String)condition.get("startDate");
		String endDate = (String)condition.get("endDate");
		String nextDate = "";
		String lastDate = "";
		String searchDate = "";
		
		String energyCode = "";
	
		searchDate = endDate ; //마지막 날짜를 기준
		String energyType = (String) condition.get("energyType");  //EM/GM/WM/HM
		
		Integer[] locationChildren = (Integer[]) condition
				.get("locationChildren");
		Condition cdtLocationChildren = new Condition("location.id",
				locationChildren, null, Restriction.IN);
		
		Properties prop = new Properties();
		
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("bems_charge.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String patternNum = prop.getProperty("pattern.format.usage");		
		DecimalFormat df = new DecimalFormat(patternNum);
		
//		logger.debug("#### 주별 ####" );
//		logger.debug("검색 날짜 : startDate : "+ startDate + ", endDate : "+ endDate);
//		logger.debug("date Type : "+ dateType);
//		logger.debug("energy Type : "+ energyType);
//		
		//현재까지 사용량 
		if("EM".equals(energyType)){	
			energyCode = "1.3.1.1";	
		}else if("GM".equals(energyType)){
			energyCode ="1.3.1.3";	
		}else if("WM".equals(energyType)){
			energyCode ="1.3.1.2";
		}else if("HM".equals(energyType)){
			energyCode = "1.3.1.4";	
		}
		
		energyCurrentUse = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType ,energyCode , 1, "day", "sum", startDate, endDate);	
	
		Map<String, Object> dataMap =  getSearchDate (searchDate,dateType,1); //작년 동월 동주 
		startDate 	= (String) dataMap.get("startDate");
		endDate 	= (String) dataMap.get("endDate");
		
		energyBeforeUse = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType ,energyCode , 1, "day", "sum", startDate, endDate);		
		if(	energyBeforeUse == 0.0){
	
			dataMap =  getSearchDate (searchDate,dateType,2); //금년 전월 동주
			startDate 	= (String) dataMap.get("startDate");
			endDate 	= (String) dataMap.get("endDate");
			
			energyBeforeUse = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType ,energyCode , 1, "day", "sum", startDate, endDate);		
			
			if(energyBeforeUse == 0.0){

				dataMap =  getSearchDate (searchDate,dateType,3); //금년 동전주
				startDate 	= (String) dataMap.get("startDate");
				endDate 	= (String) dataMap.get("endDate");
				
				energyBeforeUse = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType ,energyCode , 1, "day", "sum", startDate, endDate);		
				
			}
		}
		
		nextDate  		= (String) dataMap.get("nextDate");  //검색 이후의 날짜
		lastDate   		= (String) dataMap.get("lastDate");  //주의 마지막 날짜
		logger.debug(" 주별  nextDate : "+ nextDate +", lastDate : "+ lastDate);
		
		if(Integer.parseInt(nextDate) <= Integer.parseInt(lastDate)){
			energyNextUse = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType ,energyCode , 1, "day", "sum", nextDate, lastDate);		
		}else {
			energyNextUse = 0.0; //
		}

		R = (energyBeforeUse == 0.0) ? 0.0 : (energyCurrentUse/energyBeforeUse);
		energyForeCastUse = energyCurrentUse +(R*energyNextUse);
		energyForeCastUse = Double.valueOf(df.format(energyForeCastUse)); // 에너지 사용량

		logger.debug("#### 주별 ####" );
		logger.debug(" energyCurrentUse : "+Double.valueOf(df.format(energyCurrentUse)));
		logger.debug(" energyBeforeUse : " + energyBeforeUse);
		logger.debug(" R : " + R);
		logger.debug(" energyNextUse : "+energyNextUse);
		logger.debug(" energyForeCastUse : "+energyForeCastUse);
		
		return energyForeCastUse;
	}
	
	private Double monthForecastSum(Map<String, Object> condition, String dateType) {

		Double R = 0.0;  //사용 비율 
		Double energyBeforeUse = 0.0; // 에너지 이전 사용량
		Double energyCurrentUse = 0.0; // 에너지 현재 사용량
		Double energyNextUse = 0.0; // 에너지 현재 사용량
		Double energyForeCastUse = 0.0; // 에너지 현재 사용량
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMM");
		String startDate = (String)condition.get("startDate");
		String endDate = (String)condition.get("endDate");
		int supplierId = Integer.parseInt((String) condition.get("supplierId"));
		String searchDate = startDate;
			
		int comparelastDay = 0; //비교 월의 마지막 날짜 
		
		int sY =  Integer.parseInt(startDate.substring(0, 4));
		int sM = Integer.parseInt(startDate.substring(4, 6));
		int sD = Integer.parseInt(startDate.substring(6, 8));
		c.set(sY,sM-1,sD);
		startDate = formatter1.format(c.getTime());
		
		int eY =  Integer.parseInt(endDate.substring(0, 4));
		int eM = Integer.parseInt(endDate.substring(4, 6));
		int eD = Integer.parseInt(endDate.substring(6, 8));
		c.set(eY,eM-1,eD);
		endDate = formatter1.format(c.getTime());

		String energyType = (String) condition.get("energyType");  //EM/GM/WM/HM
		String energyCode = "";
		
		int day = 0; //날짜
		
		Integer[] locationChildren = (Integer[]) condition
				.get("locationChildren");
		Condition cdtLocationChildren = new Condition("location.id",
				locationChildren, null, Restriction.IN);
		
		Properties prop = new Properties();
		
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("bems_charge.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String patternNum = prop.getProperty("pattern.format.usage");		
		DecimalFormat df = new DecimalFormat(patternNum);
		
		logger.debug("#### 월별 ####" );
		logger.debug("검색 날짜 : startDate : "+ startDate + ", endDate : "+ endDate);
//		logger.debug("date Type : "+ dateType);
//		logger.debug("energy Type : "+ energyType);
		
		//현재까지 사용량 
		if("EM".equals(energyType)){	
			energyCode = "1.3.1.1";	
		}else if("GM".equals(energyType)){
			energyCode ="1.3.1.3";	
		}else if("WM".equals(energyType)){
			energyCode ="1.3.1.2";
		}else if("HM".equals(energyType)){
			energyCode = "1.3.1.4";	
		}
				
		energyCurrentUse = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType ,energyCode , 1, "month", "sum", startDate, endDate);	
	
		Map<String, Object> dataMap =  getSearchDate (searchDate,dateType,1); //작년 동월

		startDate 		= (String) dataMap.get("startDate");
		endDate 		= (String) dataMap.get("endDate");
		day   			= (Integer) dataMap.get("value");  //현재 날짜
		comparelastDay 	= (Integer)dataMap.get("lastDay"); 
		         
		energyBeforeUse =  getEnergyUse(supplierId,dateType,energyType,startDate, endDate, 1, day);//1일부터 동일 날짜까지의 사용량
			
		if(energyBeforeUse == 0.0){
			
			dataMap =  getSearchDate (searchDate,dateType,2); //금년 전월
			startDate 	= (String) dataMap.get("startDate");
			endDate 	= (String) dataMap.get("endDate");
			day   		= (Integer) dataMap.get("value"); //이전날짜
			comparelastDay = (Integer)dataMap.get("lastDay");
			
			energyBeforeUse =  getEnergyUse(supplierId,dateType,energyType,startDate, endDate, 1, day);

		}

		if(!(day == comparelastDay)){
			energyNextUse = getEnergyUse(supplierId,dateType,energyType,startDate, endDate, day+1, comparelastDay); //동일날짜 이후부터 월의 마지막일까지 사용량
		}else {
			energyNextUse = 0.0; //
		}

		R = (energyBeforeUse == 0.0) ? 0.0 : (energyCurrentUse/energyBeforeUse);
		energyForeCastUse = energyCurrentUse +(R*energyNextUse);
		energyForeCastUse = Double.valueOf(df.format(energyForeCastUse)); // 에너지 사용량
		
		logger.debug("#### 월별 ####" );
		logger.debug(" energyCurrentUse : "+Double.valueOf(df.format(energyCurrentUse)));
		logger.debug(" energyBeforeUse : " + energyBeforeUse);
		logger.debug(" R : " + R);
		logger.debug(" energyNextUse : "+energyNextUse);
		logger.debug(" energyForeCastUse : "+energyForeCastUse);
		
		return energyForeCastUse;
	}
	
	private Double yearForecastSum(Map<String, Object> condition, String dateType) {

		Double R = 0.0;  //사용 비율 
		Double energyBeforeUse = 0.0; // 에너지 이전 사용량
		Double energyCurrentUse = 0.0; // 에너지 현재 사용량
		Double energyNextUse = 0.0; // 에너지 현재 사용량
		Double energyForeCastUse = 0.0; // 에너지 현재 사용량
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
		String startDate = (String)condition.get("startDate");
		String endDate = (String)condition.get("endDate");
		String nextDate = "";
		String lastDate = "";
		String searchDate = startDate;
		String energyCode = "";
	
		int eY =  Integer.parseInt(endDate.substring(0, 4));
		int eM = Integer.parseInt(endDate.substring(4, 6));
		int eD = Integer.parseInt(endDate.substring(6, 8));
		c.set(eY,eM-1,eD);
		endDate = formatter.format(c.getTime());

		String energyType = (String) condition.get("energyType");  //EM/GM/WM/HM

		int month = 0; //월
		
		Integer[] locationChildren = (Integer[]) condition
				.get("locationChildren");
		Condition cdtLocationChildren = new Condition("location.id",
				locationChildren, null, Restriction.IN);
		
		Properties prop = new Properties();
		
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("bems_charge.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String patternNum = prop.getProperty("pattern.format.usage");		
		DecimalFormat df = new DecimalFormat(patternNum);

//		logger.debug("#### 연별 ####" );
//		logger.debug("검색 날짜 : startDate : "+ startDate + ", endDate : "+ endDate);
//		logger.debug("date Type : "+ dateType);
//		logger.debug("energy Type : "+ energyType);
		
		if("EM".equals(energyType)){	
			energyCode = "1.3.1.1";	
		}else if("GM".equals(energyType)){
			energyCode ="1.3.1.3";	
		}else if("WM".equals(energyType)){
			energyCode ="1.3.1.2";
		}else if("HM".equals(energyType)){
			energyCode = "1.3.1.4";	
		}
		
		energyCurrentUse = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType ,energyCode , 1, "month", "sum", startDate, endDate);	
	
		Map<String, Object> dataMap =  getSearchDate (searchDate,dateType,1); //작년

		startDate 			= (String) dataMap.get("startDate");
		endDate 			= (String) dataMap.get("endDate");
		nextDate 			= (String) dataMap.get("nextDate");
		lastDate 			= (String) dataMap.get("lastDate");
		
		energyBeforeUse = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType ,energyCode , 1, "month", "sum", startDate, endDate);	
		
		if(!(energyBeforeUse == 0.0)){
		
			energyNextUse = dayMonthMaxMinAvgSum(cdtLocationChildren, energyType ,energyCode , 1, "month", "sum", nextDate, lastDate);	
			R = energyCurrentUse/energyBeforeUse;
			energyForeCastUse = energyCurrentUse +(R*energyNextUse);
			
		}else{
			energyForeCastUse = 0.0;
			
		}

		energyForeCastUse = Double.valueOf(df.format(energyForeCastUse)); // 에너지 사용량
		
		logger.debug("#### 연별 ####" );
		logger.debug(" energyCurrentUse : "+Double.valueOf(df.format(energyCurrentUse)));
		logger.debug(" energyBeforeUse : " + energyBeforeUse);
		logger.debug(" R : " + R);
		logger.debug(" energyNextUse : "+energyNextUse);
		logger.debug(" energyForeCastUse : "+energyForeCastUse);
		
		return energyForeCastUse;
	}
	
	private Double getEnergyUse(int supplierId, String dateType, String energyType, String startDate, String endDate, int startValue, int endValue){
		
		Double energyUse= 0.0;
		
		if("day".equals(dateType)){
			energyUse = dayMonthValueSum(supplierId, energyType,
					"day", startDate, endDate, startValue, endValue);
		}else if("month".equals(dateType)) {
			energyUse = dayMonthValueSum(supplierId, energyType,
					"month", startDate, endDate, startValue, endValue);
		}

		return energyUse;
		
	}
	
	private Double dayMonthValueSum(int supplierId, String energyType, String dateType,
			String startDate, String endDate, int startValue, int endValue) {
		Double returnValue = 0.0;
		List<Object> forecastUseList = null;
		
		if("EM".equals(energyType)){ 
			if("day".equals(dateType)){
				forecastUseList =  dayEMDao.getConsumptionEmValueSum(supplierId,startDate,endDate,startValue, endValue);
			}else if("month".equals(dateType)){
				forecastUseList =  monthEMDao.getConsumptionEmValueSum(supplierId,startDate,endDate,startValue, endValue);
			}
		
		}else if("GM".equals(energyType)){
			if("day".equals(dateType)){
				forecastUseList =  dayGMDao.getConsumptionGmValueSum(supplierId,startDate,endDate,startValue, endValue);
			}else if("month".equals(dateType)){
				forecastUseList =  monthGMDao.getConsumptionGmValueSum(supplierId,startDate,endDate,startValue, endValue);
			}
		
		}else if("WM".equals(energyType)){
			if("day".equals(dateType)){
				forecastUseList =  dayWMDao.getConsumptionWmValueSum(supplierId,startDate,endDate,startValue, endValue);
			}else if("month".equals(dateType)){
				forecastUseList =  monthWMDao.getConsumptionWmValueSum(supplierId,startDate,endDate,startValue, endValue);
			}
		
		}else if("HM".equals(energyType)){
			if("day".equals(dateType)){
				forecastUseList =  dayHMDao.getConsumptionHmValueSum(supplierId,startDate,endDate,startValue, endValue);
			}else if("month".equals(dateType)){
				forecastUseList =  monthHMDao.getConsumptionHmValueSum(supplierId,startDate,endDate,startValue, endValue);
			}
		}
		
		if (forecastUseList != null && !forecastUseList.isEmpty() && forecastUseList.get(0) != null) {
			HashMap<String, Object> temp = (HashMap<String, Object>) forecastUseList.get(0);
			returnValue = Double.parseDouble(
					ObjectUtils.defaultIfNull(temp.get("VALUE_SUM"), "0").toString());
		}
	
		return returnValue;
		
	}
	
	private Map<String, Object> getSearchDate(String sDate, String dateType, int beforeDateType){
		
		Calendar c = Calendar.getInstance();
		Calendar c1 = Calendar.getInstance();
		
		String startDate = "";
		String endDate = "";
		String nextDate = "";
		String lastDate = "";
		String searchDate = "";
		String currentDate 	= "";
		
		int week  = 0;
		int yoil  = 0;
		int value  = 0;

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMM");
		
		int year = Integer.parseInt(sDate.substring(0, 4));
		int month = Integer.parseInt(sDate.substring(4, 6));
		int day = Integer.parseInt(sDate.substring(6, 8));
		int lastday = 0;

		c.set(year,month-1,day); //현재 날짜
		
		if("day".equals(dateType) || "week".equals(dateType)){
			searchDate 	= formatter.format(c.getTime());
			currentDate = formatter.format(c1.getTime());
		}else{
			searchDate 	= formatter1.format(c.getTime());
			currentDate = formatter1.format(c1.getTime());
		}
		
		week = c.get(Calendar.WEEK_OF_MONTH);//주
		yoil = c.get(Calendar.DAY_OF_WEEK); //요일
		
		//일일 사용량
		if("day".equals(dateType)){
		
			if(beforeDateType == 1){
				year = year-1;   //전년도 동일(전년도 동월 몇주차  x요일)
			}else if(beforeDateType == 2){
				month = month-1; //동년 전월 동일(동년 전월 몇주차 x요일)
			}else if(beforeDateType == 3){
				week = week-1;   //동년 동월 전주 동일 
			}
			
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, month-1);
			c.set(Calendar.WEEK_OF_MONTH, week);
			c.set(Calendar.DAY_OF_WEEK, yoil);
			
		}else if("week".equals(dateType)){
			
			if(beforeDateType == 1){
				year = year-1;   //전년도 동일(전년도 동월 몇주차)
			}else if(beforeDateType == 2){
				
				month = month-1; //동년 전월 동일(동년 전월 몇주차)
			}else if(beforeDateType == 3){
				week = week-1;   //동년 동월 전주 동일 
			}
		
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, month-1);
			c.set(Calendar.WEEK_OF_MONTH, week);
		
		}else if("month".equals(dateType)){

			if(beforeDateType == 1){
				year = year-1;   //전년도 동일(전년도 동월)
			}else if(beforeDateType == 2){
				month = month-1; //동년 전월 동일(동년 전월)
				
			}
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, month-1);
			
		}else if("year".equals(dateType)) {
			
			if(beforeDateType == 1){
				year = year-1;   //전년도 
			}
			c.set(Calendar.YEAR, year);
			c.set(Calendar.MONTH, month-1);
		}
	
		logger.debug("비교 Date  " +  formatter.format(c.getTime()));
		
		if("day".equals(dateType)) {
			
			startDate = formatter.format(c.getTime());
			endDate = formatter.format(c.getTime()); 
			value = Integer.parseInt(searchDate) < Integer.parseInt(currentDate) ? 23 :c.get(Calendar.HOUR_OF_DAY); //시간
			
		}else if("week".equals(dateType)){
			
			endDate = formatter.format(c.getTime());
			int eY =  Integer.parseInt(endDate.substring(0, 4));
			int eM = Integer.parseInt(endDate.substring(4, 6));
			int eD = Integer.parseInt(endDate.substring(6, 8));
			
			String weekS[] = getThisWeekStartEndDayS(eY,eM,eD);
			startDate = weekS[0];
			endDate   = (weekS[1].equals(endDate)) ? weekS[1] : endDate ; 
		
			int lY =  Integer.parseInt(endDate.substring(0, 4));
			int lM = Integer.parseInt(endDate.substring(4, 6));
			int lD = Integer.parseInt(endDate.substring(6, 8));
			
			c.set(lY,lM-1,lD+1);
			nextDate = formatter.format(c.getTime());
			lastDate = weekS[1];
			
		}else if("month".equals(dateType)){
					
			startDate = formatter1.format(c.getTime());
			endDate = formatter1.format(c.getTime());

			lastday = c.getActualMaximum(Calendar.DAY_OF_MONTH); //월의 마지막 날짜.
			value = Integer.parseInt(searchDate) < Integer.parseInt(currentDate) ? lastday : c1.get(Calendar.DAY_OF_MONTH); ;
		
		}else {
			value = c1.get(Calendar.MONTH); //금월날짜
			
			startDate = formatter1.format(c.getTime());
			
			c.set(Calendar.MONTH, value);
			endDate =  formatter1.format(c.getTime());
			
			c.set(Calendar.MONTH,value+1);
			nextDate =  formatter1.format(c.getTime());

			c.set(year,11,31);
			lastDate = formatter1.format(c.getTime());
			
		}
		
		logger.debug("반환될 startDate : " +startDate +", endDate : "+ endDate +", value : "+ value );
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("startDate", startDate);
		result.put("endDate", endDate);
		result.put("value", value);
		result.put("nextDate", nextDate);
		result.put("lastDate", lastDate);
		result.put("lastDay", lastday);
	    return result;
		
	}

	private String[] getThisWeekStartEndDayS(int y, int m, int d) {

		String startEnd[] = new String[2];

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

		
		Calendar getWeek = Calendar.getInstance();
		Calendar getCurrent = Calendar.getInstance();
		getWeek.set(y, m - 1, d);
		
		String currentDate  = formatter.format(getCurrent.getTime());
		logger.debug("currentDate : " + currentDate );
		getWeek.getTime();
		getWeek.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY); // 해당 주의 일요일로 셋팅.

		String weekS =  formatter.format(getWeek.getTime());
	
		getWeek.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); // 해당 주의 토요일로 셋팅.
		String weekE = formatter.format(getWeek.getTime());
	
		startEnd[0] = weekS;
		startEnd[1] = Integer.parseInt(weekE) >= Integer.parseInt(currentDate) ? currentDate : weekE;

		return startEnd;
	}
	
	private Double DoubleUtil(double use,double zero){
		if("".equals(Double.toString(use))){
			use = zero;
		}
		return use;
	}

}
