package com.aimir.service.mvm.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.TOE;
import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.dao.mvm.MeteringMonthDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.dao.mvm.MonthWMDao;
import com.aimir.dao.system.AverageUsageDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.EnergySavingGoal2Dao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.ZoneDao;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.Meter;
import com.aimir.model.system.AverageUsage;
import com.aimir.model.system.Code;
import com.aimir.model.system.EnergySavingGoal2;
import com.aimir.model.system.Location;
import com.aimir.service.mvm.EmsReportManager;
import com.aimir.util.BemsStatisticUtil;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@Service(value = "emsReportManager")
public class EmsReportManagerImpl implements EmsReportManager {
	
	@Autowired
	CodeDao codeDao;

	@Autowired
	ZoneDao zoneDao;

	@Autowired
	EndDeviceDao endDeviceDao;

	@Autowired
	MeteringDayDao meteringDayDao;

	@Autowired
	DayEMDao dayEMDao;

	@Autowired
	DayGMDao dayGMDao;

	@Autowired
	DayWMDao dayWMDao;

	@Autowired
	MonthEMDao monthEMDao;

	@Autowired
	MonthGMDao monthGMDao;

	@Autowired
	MonthWMDao monthWMDao;

	@Autowired
	MeteringMonthDao meteringMonthDao;

	@Autowired
	LocationDao locationDao;

	@Autowired
	MeterDao meterDao;

	@Autowired
	EnergySavingGoal2Dao energySavingGoal2Dao;

	@Autowired
	AverageUsageDao averageUsageDao;

    private static Log logger = LogFactory.getLog(EmsReportManagerImpl.class);

	public Map<String, Object> getEmsReportInfo(Map<String, Object> params) {

		String periodType = String.valueOf(params.get("periodType"));
		String searchDate = String.valueOf(params.get("searchDate"));
		String quarter = String.valueOf(params.get("quarter"));

		String searchStartDate = "";
		String searchEndDate = "";

		String year = "";
		String weekOfYear = "";
		String month = "";
		try {
			SimpleDateFormat inFormatter = new SimpleDateFormat("yyyyMMdd");
			Date date = inFormatter.parse(searchDate);

			year = new SimpleDateFormat("yyyy").format(date);
			weekOfYear = new SimpleDateFormat("w").format(date);
			month = new SimpleDateFormat("M").format(date);
		} catch (ParseException e) {

			try {
				SimpleDateFormat inFormatter = new SimpleDateFormat("y. M. d");
				Date date = inFormatter.parse(searchDate);

				searchDate = new SimpleDateFormat("yyyyMMdd").format(date);
				year = new SimpleDateFormat("yyyy").format(date);
				weekOfYear = new SimpleDateFormat("w").format(date);
				month = new SimpleDateFormat("M").format(date);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
		}

		if (DateType.DAILY.getCode().equals(periodType)) {
			searchStartDate = searchDate;
			searchEndDate = searchDate;
		} else if (DateType.WEEKLY.getCode().equals(periodType)) {
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, Integer.parseInt(year));
			c.set(Calendar.WEEK_OF_YEAR, Integer.parseInt(weekOfYear));

			c.set(Calendar.DAY_OF_WEEK, 1);
			searchStartDate = DateTimeUtil.getShortDateString(c
					.getTimeInMillis());

			c.set(Calendar.DAY_OF_WEEK, 7);
			searchEndDate = DateTimeUtil
					.getShortDateString(c.getTimeInMillis());

		} else if (DateType.MONTHLY.getCode().equals(periodType)) {

			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, Integer.parseInt(year));
			c.set(Calendar.MONTH, Integer.parseInt(month) - 1);
			c.set(Calendar.DAY_OF_MONTH, 1);
			searchStartDate = DateTimeUtil.getShortDateString(c
					.getTimeInMillis());

			c.set(Calendar.DAY_OF_MONTH, c
					.getActualMaximum(Calendar.DAY_OF_MONTH));
			searchEndDate = DateTimeUtil
					.getShortDateString(c.getTimeInMillis());

		} else if (DateType.YEARLY.getCode().equals(periodType)) {
			searchStartDate = year + "01";
			searchEndDate = year + "12";
		} else if (DateType.QUARTERLY.getCode().equals(periodType)) {

			String smonth = CalendarUtil
					.to2Digit((Integer.parseInt(quarter) * 3) - 2);
			String emonth = CalendarUtil
					.to2Digit((Integer.parseInt(quarter) * 3));

			searchStartDate = year + smonth;
			searchEndDate = year + emonth;
		}

		params.put("searchStartDate", searchStartDate);
		params.put("searchEndDate", searchEndDate);

		Map<String, Object> energyUsage = getEnergyUsage(params);// 에너지별
		Map<String, Object> machineryUsage = getEndDeviceTypeUsage(params,
				"1.9.1.1");// 공조
		Map<String, Object> electricityUsage = getEndDeviceTypeUsage(params,
				"1.9.1.2");// 전기
		Map<String, Object> etcUsage = getEndDeviceTypeUsage(params, "1.9.1.3");// 기타

		Map<String, Object> resultData = new HashMap<String, Object>();
		resultData.put("Energy", energyUsage);
		resultData.put("Machinery", machineryUsage);
		resultData.put("Electricity", electricityUsage);
		resultData.put("Etc", etcUsage);

		return resultData;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private Map<String, Object> getEnergyUsage(Map<String, Object> params) {

		List<EndDevice> endDeviceList = new ArrayList<EndDevice>();
		List<Code> endDeviceDstcdList = codeDao.getChildCodes("1.9.1");
		for (Code code : endDeviceDstcdList) {
			// 입력된 EndDevice 분류의 최하위 코드ID 목록조회
			List<Integer> categories = codeDao.getLeafCode(code.getId());
			// 최하위 EndDevice 분류에 해당하는 EndDevice 목록조회
			endDeviceList.addAll(endDeviceDao
					.getEndDevicesByCategories(categories));
		}

		// Map - xField,EmUsage,GmUsage,WmUsage,co2Usage
		// 기간별로 전기,가스,수도별 각각 단위의 사용량과
		List<Object> periodUsageList = getPeriodUsage(params, endDeviceList);

		BigDecimal emGmWmTotal = new BigDecimal(0);
		BigDecimal emTotal = new BigDecimal(0);
		BigDecimal gmTotal = new BigDecimal(0);
		BigDecimal wmTotal = new BigDecimal(0);
		BigDecimal preEmTotal = new BigDecimal(0);
		BigDecimal preGmTotal = new BigDecimal(0);
		BigDecimal preWmTotal = new BigDecimal(0);
		BigDecimal lastYearEmTotal = new BigDecimal(0);
		BigDecimal lastYearGmTotal = new BigDecimal(0);
		BigDecimal lastYearWmTotal = new BigDecimal(0);
		BigDecimal emCo2Total = new BigDecimal(0);
		BigDecimal gmCo2Total = new BigDecimal(0);
		BigDecimal wmCo2Total = new BigDecimal(0);

		// 현재시간,요일,월,분기까지의 검침데이터중에서
		// 최대검침값을 가지는 구간을 설정한다.
		String emMinPeriod = "";
		String emMaxPeriod = "";
		String gmMinPeriod = "";
		String gmMaxPeriod = "";
		String wmMinPeriod = "";
		String wmMaxPeriod = "";

		BigDecimal emMinVal = new BigDecimal(0);
		BigDecimal emMaxVal = new BigDecimal(0);
		BigDecimal gmMinVal = new BigDecimal(0);
		BigDecimal gmMaxVal = new BigDecimal(0);
		BigDecimal wmMinVal = new BigDecimal(0);
		BigDecimal wmMaxVal = new BigDecimal(0);

		Map<String, Object> usageMap = null;
		boolean fastYn = false;
		int i = 0;
		for (Object tmpObj : periodUsageList) {
			usageMap = (Map<String, Object>) tmpObj;

			BigDecimal emUsage = (BigDecimal) usageMap.get("EmUsage");
			BigDecimal gmUsage = (BigDecimal) usageMap.get("GmUsage");
			BigDecimal wmUsage = (BigDecimal) usageMap.get("WmUsage");
			BigDecimal preEmUsage = (BigDecimal) usageMap.get("PreEmUsage");
			BigDecimal preGmUsage = (BigDecimal) usageMap.get("PreGmUsage");
			BigDecimal preWmUsage = (BigDecimal) usageMap.get("PreWmUsage");
			BigDecimal lastYearEmUsage = (BigDecimal) usageMap.get("LastYearEmUsage");
			BigDecimal lastYearGmUsage = (BigDecimal) usageMap.get("LastYearGmUsage");
			BigDecimal lastYearWmUsage = (BigDecimal) usageMap.get("LastYearWmUsage");
			BigDecimal emCo2usage = (BigDecimal) usageMap.get("EmCo2Usage");
			BigDecimal gmCo2usage = (BigDecimal) usageMap.get("GmCo2Usage");
			BigDecimal wmCo2usage = (BigDecimal) usageMap.get("WmCo2Usage");

			usageMap.put("EmToe", emUsage.multiply(
					new BigDecimal(TOE.Energy.getValue())).setScale(3,
					BigDecimal.ROUND_DOWN).toString());
			usageMap.put("GmToe", gmUsage.multiply(
					new BigDecimal(TOE.GasLng.getValue())).setScale(3,
					BigDecimal.ROUND_DOWN).toString());
			usageMap.put("WmToe", wmUsage.multiply(
					new BigDecimal(TOE.Water.getValue())).setScale(3,
					BigDecimal.ROUND_DOWN).toString());
			
			emTotal = emTotal.add(emUsage);
			gmTotal = gmTotal.add(gmUsage);
			wmTotal = wmTotal.add(wmUsage);
			preEmTotal = preEmTotal.add(preEmUsage);
			preGmTotal = preGmTotal.add(preGmUsage);
			preWmTotal = preWmTotal.add(preWmUsage);
			lastYearEmTotal = lastYearEmTotal.add(lastYearEmUsage);
			lastYearGmTotal = lastYearGmTotal.add(lastYearGmUsage);
			lastYearWmTotal = lastYearWmTotal.add(lastYearWmUsage);
			emCo2Total = emCo2Total.add(emCo2usage);
			gmCo2Total = gmCo2Total.add(gmCo2usage);
			wmCo2Total = wmCo2Total.add(wmCo2usage);

			// 전기,가스,수도별 Peak 구간 구하기
			String xField = (String) usageMap.get("xField");
			if (!fastYn) {
				if (i == 0) {
					emMinPeriod = xField;
					emMaxPeriod = xField;
					gmMinPeriod = xField;
					gmMaxPeriod = xField;
					wmMinPeriod = xField;
					wmMaxPeriod = xField;

					emMinVal = emUsage;
					emMaxVal = emUsage;
					gmMinVal = gmUsage;
					gmMaxVal = gmUsage;
					wmMinVal = wmUsage;
					wmMaxVal = wmUsage;
				} else {

					if (emUsage.compareTo(emMinVal) < 0) {
						emMinPeriod = xField;
						emMinVal = emUsage;
					}
					if (emUsage.compareTo(emMaxVal) > 0) {
						emMaxPeriod = xField;
						emMaxVal = emUsage;
					}

					if (gmUsage.compareTo(gmMinVal) < 0) {
						gmMinPeriod = xField;
						gmMinVal = gmUsage;
					}
					if (gmUsage.compareTo(gmMaxVal) > 0) {
						gmMaxPeriod = xField;
						gmMaxVal = gmUsage;
					}

					if (wmUsage.compareTo(wmMinVal) < 0) {
						wmMinPeriod = xField;
						wmMinVal = wmUsage;
					}
					if (wmUsage.compareTo(wmMaxVal) > 0) {
						wmMaxPeriod = xField;
						wmMaxVal = wmUsage;
					}
				}
				if (CommonConstants.YesNo.Yes.getCode().equals(
						usageMap.get("isCurrent"))) {
					fastYn = true;
				}
				i++;
			}
		}

		// 구간 전체의 전기,가스,수도 TOE 값
		BigDecimal emTotalToe = emTotal.multiply(
				new BigDecimal(TOE.Energy.getValue())).setScale(3,
				BigDecimal.ROUND_DOWN);
		BigDecimal gmTotalToe = gmTotal.multiply(
				new BigDecimal(TOE.GasLng.getValue())).setScale(3,
				BigDecimal.ROUND_DOWN);
		BigDecimal wmTotalToe = wmTotal.multiply(
				new BigDecimal(TOE.Water.getValue())).setScale(3,
				BigDecimal.ROUND_DOWN);

		// /////////////////////////////////////
		// 사용량(TOE) 기준 전기,가스,수도 비율
		BigDecimal totalToe = new BigDecimal(0);
		totalToe = totalToe.add(emTotalToe).add(gmTotalToe).add(wmTotalToe);

		BigDecimal emRate = new BigDecimal(0);
		BigDecimal gmRate = new BigDecimal(0);
		BigDecimal wmRate = new BigDecimal(0);
		// 100% 맞춰주기위해서 차이값을 wmRate에 더함
		if (totalToe.compareTo(new BigDecimal(0)) != 0) {

			emRate = emTotalToe.divide(totalToe, MathContext.DECIMAL32)
					.multiply(new BigDecimal(100)).setScale(1,
							BigDecimal.ROUND_DOWN);
			gmRate = gmTotalToe.divide(totalToe, MathContext.DECIMAL32)
					.multiply(new BigDecimal(100)).setScale(1,
							BigDecimal.ROUND_DOWN);
			wmRate = wmTotalToe.divide(totalToe, MathContext.DECIMAL32)
					.multiply(new BigDecimal(100)).setScale(1,
							BigDecimal.ROUND_DOWN);
			// 100% 맞춰주기위해서 차이값을 wmRate에 더함
			wmRate = wmRate.add(new BigDecimal(100).subtract(emRate).subtract(
					gmRate).subtract(wmRate)); 
		}
		
		Double preEmRate = new BigDecimal(getSavingPercentage(preEmTotal.intValue() , emTotal.intValue())).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();		
		Double preGmRate = new BigDecimal(getSavingPercentage(preGmTotal.intValue() , gmTotal.intValue())).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();		
		Double preWmRate = new BigDecimal(getSavingPercentage(preWmTotal.intValue() , wmTotal.intValue())).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
		
		Double lastYearEmRate = new BigDecimal(getSavingPercentage(lastYearEmTotal.intValue() , emTotal.intValue())).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();		
		Double lastYearGmRate = new BigDecimal(getSavingPercentage(lastYearGmTotal.intValue() , gmTotal.intValue())).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();		
		Double lastYearWmRate = new BigDecimal(getSavingPercentage(lastYearWmTotal.intValue() , wmTotal.intValue())).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();

		// /////////////////////////////////////
		// 탄소배출량 기준 전기,가스,수도 비율
		BigDecimal totalCo2 = new BigDecimal(0);
		totalCo2 = totalCo2.add(emCo2Total).add(gmCo2Total).add(wmCo2Total);

		BigDecimal emCo2Rate = new BigDecimal(0);
		BigDecimal gmCo2Rate = new BigDecimal(0);
		BigDecimal wmCo2Rate = new BigDecimal(0);
		if (totalCo2.compareTo(new BigDecimal(0)) != 0) {
			emCo2Rate = emCo2Total.divide(totalCo2, MathContext.DECIMAL32)
					.multiply(new BigDecimal(100)).setScale(1,
							BigDecimal.ROUND_DOWN);
			gmCo2Rate = gmCo2Total.divide(totalCo2, MathContext.DECIMAL32)
					.multiply(new BigDecimal(100)).setScale(1,
							BigDecimal.ROUND_DOWN);
			wmCo2Rate = wmCo2Total.divide(totalCo2, MathContext.DECIMAL32)
					.multiply(new BigDecimal(100)).setScale(1,
							BigDecimal.ROUND_DOWN);
			// 100% 맞춰주기위해서 차이값을 wmRate에 더함
			wmCo2Rate = wmCo2Rate.add(new BigDecimal(100).subtract(emCo2Rate)
					.subtract(gmCo2Rate).subtract(wmCo2Rate));
		}

		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream(
					"bems_charge.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		String patternNum = prop.getProperty("pattern.format.usageNumber");
		DecimalFormat df = new DecimalFormat(patternNum);

		List<Object> pieChartDataList = new ArrayList<Object>();

		// 파이차트 데이터 - 전기,가스,수도별 TOE
		Map<String, Object> pieChartDataEm = new HashMap<String, Object>();
		pieChartDataEm.put("type", "Electricity");
		pieChartDataEm.put("usage", emTotalToe.toString());
		pieChartDataList.add(pieChartDataEm);

		Map<String, Object> pieChartDataGm = new HashMap<String, Object>();
		pieChartDataGm.put("type", "Gas");
		pieChartDataGm.put("usage", gmTotalToe.toString());
		pieChartDataList.add(pieChartDataGm);

		Map<String, Object> pieChartDataWm = new HashMap<String, Object>();
		pieChartDataWm.put("type", "Water");
		pieChartDataWm.put("usage", wmTotalToe.toString());
		pieChartDataList.add(pieChartDataWm);

		String temp = df.format(emTotal.doubleValue());

		// 그리드 데이터 - 전기,가스,수도별 상세 사용량정보
		List<Object> gridDataList = new ArrayList<Object>();
		Map<String, Object> emGridData = new HashMap<String, Object>();
		emGridData.put("type", "Electricity");
		emGridData.put("usage", df.format(emTotal.doubleValue()) + " kWh");
		emGridData.put("preRate", preEmRate.toString() + " %");// 전일,전주,전분기,전년도 대비 %
		emGridData.put("lastYearRate", lastYearEmRate.toString() + " %"); // 전년동일,전년동주,전년동분기,전전년도 대비 %
		emGridData.put("usageRate", emRate.toString() + " %");
		emGridData.put("peakUsage", df.format(emMaxVal.doubleValue()) + " kWh");
		emGridData.put("peakPeriod", emMaxPeriod);
		emGridData.put("toe", df.format(emTotalToe.doubleValue()) + " TOE");
		emGridData.put("co2", df.format(emCo2Total.doubleValue()) + " kg");
		emGridData.put("co2rate", emCo2Rate.toString() + " %");

		Map<String, Object> gmGridData = new HashMap<String, Object>();
		gmGridData.put("type", "Gas");
		gmGridData.put("usage", df.format(gmTotal.doubleValue()) + " ㎥");
		gmGridData.put("preRate", preGmRate.toString() + " %");// 전일,전주,전분기,전년도 대비 %
		gmGridData.put("lastYearRate", lastYearGmRate.toString() + " %"); // 전년동일,전년동주,전년동분기,전전년도 대비 %
		gmGridData.put("usageRate", gmRate.toString() + " %");
		gmGridData.put("peakUsage", df.format(gmMaxVal.doubleValue()) + " ㎥");
		gmGridData.put("peakPeriod", gmMaxPeriod);
		gmGridData.put("toe", df.format(gmTotalToe.doubleValue()) + " TOE");
		gmGridData.put("co2", df.format(gmCo2Total.doubleValue()) + " kg");
		gmGridData.put("co2rate", gmCo2Rate.toString() + " %");

		Map<String, Object> wmGridData = new HashMap<String, Object>();
		wmGridData.put("type", "Water");
		wmGridData.put("usage", df.format(wmTotal.doubleValue()) + " ㎥");
		wmGridData.put("preRate", preWmRate.toString() + " %");// 전일,전주,전분기,전년도 대비 %
		wmGridData.put("lastYearRate", lastYearWmRate.toString() + " %"); // 전년동일,전년동주,전년동분기,전전년도 대비 %
		wmGridData.put("usageRate", wmRate.toString() + " %");
		wmGridData.put("peakUsage", df.format(wmMaxVal.doubleValue()) + " ㎥");
		wmGridData.put("peakPeriod", wmMaxPeriod);
		wmGridData.put("toe", df.format(wmTotalToe.doubleValue()) + " TOE");
		wmGridData.put("co2", df.format(wmCo2Total.doubleValue()) + " kg");
		wmGridData.put("co2rate", wmCo2Rate.toString() + " %");

		gridDataList.add(emGridData);
		gridDataList.add(gmGridData);
		gridDataList.add(wmGridData);

		// 결과 데이터 조립
		Map<String, Object> resultData = new HashMap<String, Object>();
		resultData.put("columnChartData", periodUsageList);
		resultData.put("pieChartData", pieChartDataList);
		resultData.put("gridData", gridDataList);

		return resultData;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private Map<String, Object> getEndDeviceTypeUsage(
			Map<String, Object> params, String endDeviceTypeCode) {

		List<EndDevice> endDeviceList = null;
		List<Code> endDeviceDstcdList = codeDao
				.getChildCodes(endDeviceTypeCode);

		BigDecimal totalToe = new BigDecimal(0);
		BigDecimal totalCo2 = new BigDecimal(0);

		// 공조하위의 항목(열원,열반송,동력)들이 Map 으로 쌓인다.
		List<Object> machineryList = new ArrayList<Object>();
		Map<String, Object> machineryMap = new HashMap<String, Object>();
		for (Code code : endDeviceDstcdList) {
			// 입력된 EndDevice 분류의 최하위 코드ID 목록조회
			List<Integer> categories = codeDao.getLeafCode(code.getId());
			// 최하위 EndDevice 분류에 해당하는 EndDevice 목록조회
			endDeviceList = endDeviceDao.getEndDevicesByCategories(categories);

			// Map - xField,EmUsage,GmUsage,WmUsage,co2Usage
			// 기간별로 전기,가스,수도별 각각 단위의 사용량과
			List<Object> periodUsageList = getPeriodUsage(params, endDeviceList);

			BigDecimal emGmWmTotalToe = new BigDecimal(0);
			BigDecimal emTotal = new BigDecimal(0);
			BigDecimal emGmWmTotalCo2 = new BigDecimal(0);
			BigDecimal preTotal = new BigDecimal(0);
			BigDecimal lastYearTotal = new BigDecimal(0);

			// 현재시간,요일,월,분기까지의 검침데이터중에서
			// 최대검침값을 가지는 구간을 설정한다.
			String minPeriod = "";
			String maxPeriod = "";

			BigDecimal minVal = new BigDecimal(0);
			BigDecimal maxVal = new BigDecimal(0);

			Map<String, Object> usageMap = null;
			boolean fastYn = false;
			int i = 0;
			for (Object tmpObj : periodUsageList) {
				usageMap = (Map<String, Object>) tmpObj;

				BigDecimal emUsage = (BigDecimal) usageMap.get("EmUsage");
				BigDecimal gmUsage = (BigDecimal) usageMap.get("GmUsage");
				BigDecimal wmUsage = (BigDecimal) usageMap.get("WmUsage");
				
				BigDecimal preEmUsage = (BigDecimal) usageMap.get("PreEmUsage");
				BigDecimal preGmUsage = (BigDecimal) usageMap.get("PreGmUsage");
				BigDecimal preWmUsage = (BigDecimal) usageMap.get("PreWmUsage");
				
				BigDecimal lastYearEmUsage = (BigDecimal) usageMap.get("LastYearEmUsage");
				BigDecimal lastYearGmUsage = (BigDecimal) usageMap.get("LastYearGmUsage");
				BigDecimal lastYearWmUsage = (BigDecimal) usageMap.get("LastYearWmUsage");
				
				BigDecimal emCo2usage = (BigDecimal) usageMap.get("EmCo2Usage");
				BigDecimal gmCo2usage = (BigDecimal) usageMap.get("GmCo2Usage");
				BigDecimal wmCo2usage = (BigDecimal) usageMap.get("WmCo2Usage");

				BigDecimal emgmwmToe = emUsage.multiply(
						new BigDecimal(TOE.Energy.getValue())).add(
						gmUsage.multiply(new BigDecimal(TOE.GasLng.getValue()))
								.add(
										wmUsage.multiply(new BigDecimal(
												TOE.Water.getValue())))
								.setScale(3, BigDecimal.ROUND_DOWN));

				if (emgmwmToe.compareTo(new BigDecimal(0)) != 1) {
					emgmwmToe = new BigDecimal(emgmwmToe.intValue());
				}

				usageMap.put("EmGmWmToe", emgmwmToe.setScale(3,
						BigDecimal.ROUND_DOWN));

				emGmWmTotalToe = emGmWmTotalToe.add(emgmwmToe);
				emGmWmTotalCo2 = emGmWmTotalCo2.add(emCo2usage).add(gmCo2usage)
						.add(wmCo2usage);

				emTotal = emTotal.add(emUsage);
				preTotal = preTotal.add(preEmUsage);
				lastYearTotal = lastYearTotal.add(lastYearEmUsage);
				// 전기+가스+수도 Peak 구간 구하기
				String xField = (String) usageMap.get("xField");
				if (!fastYn) {
					if (i == 0) {
						minPeriod = xField;
						maxPeriod = xField;

						minVal = emgmwmToe;
						maxVal = emgmwmToe;

					} else {

						if (emgmwmToe.compareTo(minVal) < 0) {
							minPeriod = xField;
							minVal = emgmwmToe;
						}
						if (emgmwmToe.compareTo(maxVal) > 0) {
							maxPeriod = xField;
							maxVal = emgmwmToe;
						}
					}
					if (CommonConstants.YesNo.Yes.getCode().equals(
							usageMap.get("isCurrent"))) {
						fastYn = true;
					}
					i++;
				}
			}

			// 열원,열반송,동력 전체 Toe,Co2
			totalToe = totalToe.add(emGmWmTotalToe);
			totalCo2 = totalCo2.add(emGmWmTotalCo2);

			machineryMap = new HashMap<String, Object>();
			machineryMap.put("MachineryTypeCode", code.getCode());
			machineryMap.put("MachineryType", code.getDescr());
			machineryMap.put("PeriodUsage", periodUsageList);
			machineryMap.put("TotalToe", emGmWmTotalToe.setScale(3,
					BigDecimal.ROUND_DOWN));
			machineryMap.put("TotalCo2", emGmWmTotalCo2.setScale(3,
					BigDecimal.ROUND_DOWN));
			machineryMap.put("MaxPeriod", maxPeriod);
			machineryMap.put("MaxToe", maxVal
					.setScale(3, BigDecimal.ROUND_DOWN));
			machineryMap.put("EmUsage", emTotal.setScale(3,
					BigDecimal.ROUND_DOWN));
			machineryMap.put("PreEmUsage", preTotal.setScale(3,
					BigDecimal.ROUND_DOWN));
			machineryMap.put("LastYearEmUsage", lastYearTotal.setScale(3,
					BigDecimal.ROUND_DOWN));

			machineryList.add(machineryMap);
		}

		// 기간별 차트 데이터
		List<Object> periodUsageList = new ArrayList<Object>();
		List<Object> pieChartDataList = new ArrayList<Object>();
		Map<String, Object> pieChartData = new HashMap<String, Object>();
		List<Object> gridDataList = new ArrayList<Object>();
		for (Object obj : machineryList) {
			Map<String, Object> machinery = (Map<String, Object>) obj;
			String _machineryType = (String) machinery.get("MachineryType");
			String _machineryTypeCode = (String) machinery
					.get("MachineryTypeCode");
			List<Object> _tmpPeriodList = (List<Object>) machinery
					.get("PeriodUsage");
			BigDecimal _toe = (BigDecimal) machinery.get("TotalToe");
			BigDecimal _co2 = (BigDecimal) machinery.get("TotalCo2");
			String _maxPeriod = (String) machinery.get("MaxPeriod");
			BigDecimal _maxToe = (BigDecimal) machinery.get("MaxToe");
			BigDecimal _emUsage = (BigDecimal) machinery.get("EmUsage");
			BigDecimal _preUsage = (BigDecimal) machinery.get("PreEmUsage");
			BigDecimal _lastYearUsage = (BigDecimal) machinery.get("LastYearEmUsage");

			// 기간별 차트 데이터 조립
			Map<String, Object> periodItem = new HashMap<String, Object>();
			Map<String, Object> period = null;
			if (periodUsageList.size() < 1) {
				for (Object periodObj : _tmpPeriodList) {
					period = (Map<String, Object>) periodObj;
					periodItem = new HashMap<String, Object>();
					periodItem.put("xField", period.get("xField"));
					periodItem.put(_machineryTypeCode, period.get("EmGmWmToe")
							.toString());
					periodUsageList.add(periodItem);
				}
			} else {
				int i = 0;
				for (Object periodObj : _tmpPeriodList) {
					period = new HashMap<String, Object>();
					period = (Map<String, Object>) periodObj;
					((Map<String, Object>) periodUsageList.get(i)).put(
							_machineryTypeCode, period.get("EmGmWmToe")
									.toString());
					i++;
				}
			}

			// 파이차트 데이터 조립
			pieChartData = new HashMap<String, Object>();
			pieChartData.put("type", _machineryType);
			pieChartData.put("usage", _toe.toString());
			pieChartDataList.add(pieChartData);

			// /////////////////////////////////////
			// 사용량(TOE) 기준 비율
			BigDecimal toeRate = new BigDecimal(0);
			if (totalToe.compareTo(new BigDecimal(0)) != 0) {
				toeRate = _toe.divide(totalToe, MathContext.DECIMAL32)
						.multiply(new BigDecimal(100)).setScale(1,
								BigDecimal.ROUND_DOWN);
			}

			// /////////////////////////////////////
			// 탄소배출량 기준 비율
			BigDecimal co2Rate = new BigDecimal(0);
			if (totalCo2.compareTo(new BigDecimal(0)) != 0) {
				co2Rate = _co2.divide(totalCo2, MathContext.DECIMAL32)
						.multiply(new BigDecimal(100)).setScale(1,
								BigDecimal.ROUND_DOWN);
			}

			Properties prop = new Properties();
			try {
				prop.load(getClass().getClassLoader().getResourceAsStream(
						"bems_charge.properties"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			String patternNum = prop.getProperty("pattern.format.usageNumber");
			DecimalFormat df = new DecimalFormat(patternNum);

			Map<String, Object> gridData = new HashMap<String, Object>();
			gridData.put("type", _machineryType);
			gridData.put("usage", df.format(_toe.doubleValue()) + " TOE");
			gridData.put("emUsage", df.format(_emUsage.doubleValue()) + " kWh");
			gridData.put("preRate", df.format(getSavingPercentage(_preUsage.intValue(), _emUsage.intValue())) + " %" );// 전일,전주,전분기,전년도 대비 %
			gridData.put("lastYearRate", df.format(getSavingPercentage(_lastYearUsage.intValue(), _emUsage.intValue())) + " %"); // 전년동일,전년동주,전년동분기,전전년도 대비 %
			gridData.put("usageRate", toeRate.toString() + " %");
			gridData
					.put("peakUsage", df.format(_maxToe.doubleValue()) + " TOE");
			gridData.put("peakPeriod", _maxPeriod);
			gridData.put("co2", df.format(_co2.doubleValue()) + " kg");
			gridData.put("co2rate", co2Rate.toString() + " %");

			gridDataList.add(gridData);
		}

		// 결과 데이터 조립
		Map<String, Object> resultData = new HashMap<String, Object>();
		resultData.put("columnChartData", periodUsageList);
		resultData.put("pieChartData", pieChartDataList);
		resultData.put("gridData", gridDataList);

		return resultData;
	}
	
	public List<Map<String, Object>> getYearlyUsageStatisticReport( String yyyy, int locationId ) {
		BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<String> yearMonth = new ArrayList<String>();

		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("serviceType", MeterType.EnergyMeter.getServiceType());
		condition.put("dateType", DateType.MONTHLY.getCode());
		condition.put("period",1);
				
		for ( int i = 1 ; i < 13 ; i++ ) {
			String month = "";
			
			if (i < 10) {
				month = "0" + i;
			} else {
				month = "" + i;
			}
			yearMonth.add( yyyy + month );
		}
		
		list = monthEMDao.getYearlyUsageTotal(yearMonth, locationId);
		
		for ( int i = 0 ; i < 12 ; i++ ) {
			boolean exist = false;
			
			for (Map<String, Object> row: list) {

				if ( row.get("YYYYMM").equals((String)yearMonth.get(i)) ) {
					exist = true;
					break;
				}
			}
			if ( !exist ) {
				Map<String, Object> row = new HashMap<String, Object>();
				row.put("YYYYMM", (String)yearMonth.get(i));
				row.put("TOTAL", new Double("0"));
				list.add(i, row);
			}
			
			Map<String, Object> row = list.get(i);
//			condition.put("usage",(Double)row.get("TOTAL"));
			condition.put("usage",DecimalUtil.ConvertNumberToDouble(row.get("TOTAL")));
			
			row.put("charge", bemsUtil.getUsageCharge(condition));
		}
		return list;
	}

    public List<Map<String, Object>> getEnergyUsageInfo(String yyyymm, String energyType) {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		int curYear = Integer.parseInt(yyyymm.substring(0, 4));
		int curMonth = Integer.parseInt(yyyymm.substring(4));

        String lastYyyyMM = "" + (curYear - 1) + CalendarUtil.to2Digit(curMonth);

		Properties prop = new Properties();

		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("bems_Meter.properties"));
		} catch (IOException e) {
		    logger.error(e.getMessage(), e);
			e.printStackTrace();
		}

		String energyTypeProp = energyType.substring(5,7)+".meterid";
		String[] strArr = prop.getProperty(energyTypeProp).split(",");
		Map<String, Object> data = null;
		Double currUsage = null;
		Double lastUsage = null;
		Meter meter = null;

		for (int i = 0; i < strArr.length; i++) {
			meter = meterDao.get(strArr[i]);

			if (meter == null) {
			    continue;
			}

			data = new HashMap<String, Object> ();
            currUsage = getEndDeviceTotalUsageByMonth(yyyymm, energyType, meter.getId(), meter);
            lastUsage = getEndDeviceTotalUsageByMonth(lastYyyyMM, energyType, meter.getId(), meter);

			data.put("energyType", energyType);

            if (StringUtil.nullToBlank(meter.getFriendlyName()).isEmpty()) {
                data.put("endDeviceName", (meter.getEndDevice() != null) ? meter.getEndDevice().getFriendlyName() : "");
            } else {
                data.put("endDeviceName", meter.getFriendlyName());
            }

			data.put("currUsage", currUsage);
			data.put("lastUsage", lastUsage);

			result.add(data);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
    private Double getEndDeviceTotalUsageByMonth(String yyyymm, String energyType, Integer endDeviceId, Meter meter ) {
		Double value = (double) 0;

		List<Integer> meterIdList = new ArrayList<Integer>();
		List<Integer> modemIdList = new ArrayList<Integer>();

		Map<String, Object> params2 = new HashMap<String, Object>();
		params2.put("channel", CommonConstants.DefaultChannel.Usage.getCode());
		params2.put("dst", 0);
		params2.put("meterType", energyType);

		meterIdList.add(meter.getId());
		modemIdList.add(meter.getModemId());	

		if ( meterIdList.isEmpty() ) {
			meterIdList.add(-1);
		}
		
		if ( modemIdList.isEmpty()) {
			modemIdList.add(-1);
		}
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("modemId", modemIdList);
		params.put("meterId", meterIdList);
		params.put("searchStartDate", yyyymm);
		params.put("searchEndDate", yyyymm);
		
		List<Object> list = meteringDayDao.getUsageForEndDevicesByMonthPeriodReport(params, params2);
		
		if ( !list.isEmpty() ) {
			Object tmp = list.get(0);
			Map<String, Object> data = (Map<String, Object>)tmp;
			value = DecimalUtil.ConvertNumberToDouble(data.get("total"));
		}
		return value; 
	}

	@SuppressWarnings({ "unchecked", "unused" })
	private List<Object> getPeriodUsage(Map<String, Object> params,
			List<EndDevice> endDeviceList) {

		String periodType = String.valueOf(params.get("periodType"));

		String searchStartDate = String.valueOf(params.get("searchStartDate"));
		String searchEndDate = String.valueOf(params.get("searchEndDate"));

		// 실제 현제일자로써 기간구분별로 최소값,최대값을 찾아내기위해서
		// 현재시간,요일,월,분기까지만 대상으로 한다.
		String today = CalendarUtil.getCurrentDate();
		String currYearMonth = today.substring(0, 6);

		String currMonth = today.substring(4, 6);
		String currHour = TimeUtil.getCurrentTimeMilli().substring(8, 10);
		String currQuater = Integer
				.toString((Integer.parseInt(currMonth) - 1) / 3 + 1);

		// 검침데이터와 조인을 하기위한 IN 조건 내용 조립
		// 1.EndDevice ID 목록
		// 2.EndDevice 의 Meter ID 목록
		// 3.EndDevice 의 Modem ID 목록,EndDevice 의 Meter 의 Modem ID 목록
		List<Integer> endDeviceId = new ArrayList<Integer>();
		List<Integer> modemId = new ArrayList<Integer>();
		List<Integer> meterId = new ArrayList<Integer>();

		for (EndDevice endDevice : endDeviceList) {
			endDeviceId.add(endDevice.getId());

			if (endDevice.getModem() != null) {
				modemId.add(endDevice.getModem().getId());
			}

			if (endDevice.getMeters() != null) {
				for (Meter meter : endDevice.getMeters()) {
					meterId.add(meter.getId());
					if (meter.getModem() != null) {
						modemId.add(meter.getModem().getId());
					}
				}
			}
		}

		// IN 문이 OR 조건으로 걸리는데 내용이없을경우 쿼리 오류발생하므로
		// -1 을 설정해준다.
		if (endDeviceId.size() < 1) {
			endDeviceId.add(-1);
		}
		if (modemId.size() < 1) {
			modemId.add(-1);
		}
		if (meterId.size() < 1) {
			meterId.add(-1);
		}

		params.put("endDeviceId", endDeviceId);
		params.put("modemId", modemId);
		params.put("meterId", meterId);

		List<Object> resultList = new ArrayList<Object>();

		Map<String, Object> tmp = null;
		if (CommonConstants.DateType.DAILY.getCode().equals(periodType)) {
			// MeteringDay 호출
			
			Map<String, Object> preParams = new HashMap<String, Object>();
			preParams.putAll(params);
			preParams.put("searchStartDate", this.getPreDay(false, (String)params.get("searchStartDate")));
			
			Map<String, Object> lastYearParams = new HashMap<String, Object>();
			lastYearParams.putAll(params);
			lastYearParams.put("searchStartDate", this.getPreDay(true, (String)params.get("searchStartDate")));	

			Map<String, Object> emparams = new HashMap<String, Object>();
			emparams.put("dst", 0);
			emparams.put("channel", DefaultChannel.Usage.getCode());
			emparams.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayClassName());
			List<Object> emList = meteringDayDao
					.getUsageForEndDevicesBySearchDate(params, emparams);
			
			List<Object> preEmList = meteringDayDao
					.getUsageForEndDevicesBySearchDate(preParams, emparams);
			
			List<Object> lastYearEmList = meteringDayDao
					.getUsageForEndDevicesBySearchDate(lastYearParams, emparams);

			Map<String, Object> gmparams = new HashMap<String, Object>();
			gmparams.put("dst", 0);
			gmparams.put("channel", DefaultChannel.Usage.getCode());
			gmparams.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayClassName());
			List<Object> gmList = meteringDayDao
					.getUsageForEndDevicesBySearchDate(params, gmparams);
			
			List<Object> preGmList = meteringDayDao
					.getUsageForEndDevicesBySearchDate(preParams, gmparams);
	
			List<Object> lastYearGmList = meteringDayDao
					.getUsageForEndDevicesBySearchDate(lastYearParams, gmparams);

			Map<String, Object> wmparams = new HashMap<String, Object>();
			wmparams.put("dst", 0);
			wmparams.put("channel", DefaultChannel.Usage.getCode());
			wmparams.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayClassName());
			List<Object> wmList = meteringDayDao
					.getUsageForEndDevicesBySearchDate(params, wmparams);
			
			List<Object> preWmList = meteringDayDao
					.getUsageForEndDevicesBySearchDate(preParams, wmparams);

			List<Object> lastYearWmList = meteringDayDao
					.getUsageForEndDevicesBySearchDate(lastYearParams, wmparams);

			Map<String, Object> emco2params = new HashMap<String, Object>();
			emco2params.put("dst", 0);
			emco2params.put("channel", DefaultChannel.Co2.getCode());
			emco2params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayClassName());
			List<Object> emCo2List = meteringDayDao
					.getUsageForEndDevicesBySearchDate(params, emco2params);

			Map<String, Object> gmco2params = new HashMap<String, Object>();
			gmco2params.put("dst", 0);
			gmco2params.put("channel", DefaultChannel.Co2.getCode());
			gmco2params.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayClassName());
			List<Object> gmCo2List = meteringDayDao
					.getUsageForEndDevicesBySearchDate(params, gmco2params);

			Map<String, Object> wmco2params = new HashMap<String, Object>();
			wmco2params.put("dst", 0);
			wmco2params.put("channel", DefaultChannel.Co2.getCode());
			wmco2params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayClassName());
			List<Object> wmCo2List = meteringDayDao
					.getUsageForEndDevicesBySearchDate(params, wmco2params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> gmMap = new HashMap<String, Object>();
			Map<String, Object> wmMap = new HashMap<String, Object>();
			
			Map<String, Object> preEmMap = new HashMap<String, Object>();
			Map<String, Object> preGmMap = new HashMap<String, Object>();
			Map<String, Object> preWmMap = new HashMap<String, Object>();
			
			Map<String, Object> lastYearEmMap = new HashMap<String, Object>();
			Map<String, Object> lastYearGmMap = new HashMap<String, Object>();
			Map<String, Object> lastYearWmMap = new HashMap<String, Object>();

			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			Map<String, Object> gmCo2Map = new HashMap<String, Object>();
			Map<String, Object> wmCo2Map = new HashMap<String, Object>();

			if (emList != null && emList.size() > 0) {
				emMap = (Map<String, Object>) emList.get(0);
			}
			if (gmList != null && gmList.size() > 0) {
				gmMap = (Map<String, Object>) gmList.get(0);
			}
			if (wmList != null && wmList.size() > 0) {
				wmMap = (Map<String, Object>) wmList.get(0);
			}
			
			if (preEmList != null && preEmList.size() > 0) {
				preEmMap = (Map<String, Object>) preEmList.get(0);
			}
			if (preGmList != null && preGmList.size() > 0) {
				preGmMap = (Map<String, Object>) preGmList.get(0);
			}
			if (preWmList != null && preWmList.size() > 0) {
				preWmMap = (Map<String, Object>) preWmList.get(0);
			}
			
			if (lastYearEmList != null && lastYearEmList.size() > 0) {
				lastYearEmMap = (Map<String, Object>) lastYearEmList.get(0);
			}
			if (lastYearGmList != null && lastYearGmList.size() > 0) {
				lastYearGmMap = (Map<String, Object>) lastYearGmList.get(0);
			}
			if (lastYearWmList != null && lastYearWmList.size() > 0) {
				lastYearWmMap = (Map<String, Object>) lastYearWmList.get(0);
			}

			if (emCo2List != null && emCo2List.size() > 0) {
				emCo2Map = (Map<String, Object>) emCo2List.get(0);
			}
			if (gmCo2List != null && gmCo2List.size() > 0) {
				gmCo2Map = (Map<String, Object>) gmCo2List.get(0);
			}
			if (wmCo2List != null && wmCo2List.size() > 0) {
				wmCo2Map = (Map<String, Object>) wmCo2List.get(0);
			}

			String hh = "";
			for (int i = 0; i < 24; i++) {
				hh = TimeUtil.to2Digit(i);
				tmp = new HashMap<String, Object>();
				tmp.put("xField", hh);
				tmp.put("EmUsage", new BigDecimal(
						emMap.get("value" + hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(emMap
								.get("value" + hh))).setScale(
						3, BigDecimal.ROUND_DOWN));
				tmp.put("GmUsage", new BigDecimal(
						gmMap.get("value" + hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(gmMap
								.get("value" + hh))).setScale(
						3, BigDecimal.ROUND_DOWN));
				tmp.put("WmUsage", new BigDecimal(
						wmMap.get("value" + hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(wmMap
								.get("value" + hh))).setScale(
						3, BigDecimal.ROUND_DOWN));

				tmp.put("PreEmUsage", new BigDecimal(
						preEmMap.get("value" + hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(preEmMap
								.get("value" + hh))).setScale(
						3, BigDecimal.ROUND_DOWN));
				tmp.put("PreGmUsage", new BigDecimal(
						preGmMap.get("value" + hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(preGmMap
								.get("value" + hh))).setScale(
						3, BigDecimal.ROUND_DOWN));
				tmp.put("PreWmUsage", new BigDecimal(
						preWmMap.get("value" + hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(preWmMap
								.get("value" + hh))).setScale(
						3, BigDecimal.ROUND_DOWN));
				
				tmp.put("LastYearEmUsage", new BigDecimal(
						lastYearEmMap.get("value" + hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(lastYearEmMap
								.get("value" + hh))).setScale(
						3, BigDecimal.ROUND_DOWN));
				tmp.put("LastYearGmUsage", new BigDecimal(
						lastYearGmMap.get("value" + hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(lastYearGmMap
								.get("value" + hh))).setScale(
						3, BigDecimal.ROUND_DOWN));
				tmp.put("LastYearWmUsage", new BigDecimal(
						lastYearWmMap.get("value" + hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(lastYearWmMap
								.get("value" + hh))).setScale(
						3, BigDecimal.ROUND_DOWN));

				tmp.put("EmCo2Usage", new BigDecimal(
						emCo2Map.get("value" + hh) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(emCo2Map.get("value" + hh)))
						.setScale(3, BigDecimal.ROUND_DOWN));
				tmp.put("GmCo2Usage", new BigDecimal(
						gmCo2Map.get("value" + hh) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(gmCo2Map.get("value" + hh)))
						.setScale(3, BigDecimal.ROUND_DOWN));
				tmp.put("WmCo2Usage", new BigDecimal(
						wmCo2Map.get("value" + hh) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(wmCo2Map.get("value" + hh)))
						.setScale(3, BigDecimal.ROUND_DOWN));

				if (searchStartDate.equals(today)) {
					if (hh.equals(currHour)) {
						tmp.put("isCurrent", CommonConstants.YesNo.Yes
								.getCode());
					} else if (i > Integer.parseInt(currHour)) {
						tmp.put("EmUsage", new BigDecimal(0));
						tmp.put("GmUsage", new BigDecimal(0));
						tmp.put("WmUsage", new BigDecimal(0));
						tmp.put("EmCo2Usage", new BigDecimal(0));
						tmp.put("GmCo2Usage", new BigDecimal(0));
						tmp.put("WmCo2Usage", new BigDecimal(0));
					}
				}

				resultList.add(tmp);
			}

		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(periodType)
				|| CommonConstants.DateType.MONTHLY.getCode()
						.equals(periodType)) {
			
			Map<String, Object> preParams = new HashMap<String, Object>();
			Map<String, Object> lastYearParams = new HashMap<String, Object>();
			
			if(CommonConstants.DateType.WEEKLY.getCode().equals(periodType)) {			
				preParams.putAll(params);
				preParams.put("searchStartDate", this.getPreWeek(false, true, (String)params.get("searchStartDate")));
				preParams.put("searchEndDate", this.getPreWeek(false, false, (String)params.get("searchStartDate")));
				
				lastYearParams.putAll(params);
				lastYearParams.put("searchStartDate", this.getPreWeek(true, true, (String)params.get("searchStartDate")));
				lastYearParams.put("searchEndDate", this.getPreWeek(true, false, (String)params.get("searchStartDate")));
			} else {
				preParams.putAll(params);
				preParams.put("searchStartDate", this.getPreMonth(false, true, (String)params.get("searchStartDate")));
				preParams.put("searchEndDate", this.getPreMonth(false, false, (String)params.get("searchStartDate")));
				
				lastYearParams.putAll(params);
				lastYearParams.put("searchStartDate", this.getPreMonth(true, true, (String)params.get("searchStartDate")));
				lastYearParams.put("searchEndDate", this.getPreMonth(true, false, (String)params.get("searchStartDate")));
			}

			Map<String, Object> emparams = new HashMap<String, Object>();
			emparams.put("dst", 0);
			emparams.put("channel", DefaultChannel.Usage.getCode());
			emparams.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayClassName());
			List<Object> emList = meteringDayDao
					.getUsageForEndDevicesByDayPeriod(params, emparams);
			
			List<Object> preEmList = meteringDayDao
					.getUsageForEndDevicesByDayPeriod(preParams, emparams);
	
			List<Object> lastYearEmList = meteringDayDao
					.getUsageForEndDevicesByDayPeriod(lastYearParams, emparams);

			Map<String, Object> gmparams = new HashMap<String, Object>();
			gmparams.put("dst", 0);
			gmparams.put("channel", DefaultChannel.Usage.getCode());
			gmparams.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayClassName());
			List<Object> gmList = meteringDayDao
					.getUsageForEndDevicesByDayPeriod(params, gmparams);
			
			List<Object> preGmList = meteringDayDao
					.getUsageForEndDevicesByDayPeriod(preParams, gmparams);
		
			List<Object> lastYearGmList = meteringDayDao
					.getUsageForEndDevicesByDayPeriod(lastYearParams, gmparams);

			Map<String, Object> wmparams = new HashMap<String, Object>();
			wmparams.put("dst", 0);
			wmparams.put("channel", DefaultChannel.Usage.getCode());
			wmparams.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayClassName());
			List<Object> wmList = meteringDayDao
					.getUsageForEndDevicesByDayPeriod(params, wmparams);
			
			List<Object> preWmList = meteringDayDao
					.getUsageForEndDevicesByDayPeriod(preParams, wmparams);
		
			List<Object> lastYearWmList = meteringDayDao
					.getUsageForEndDevicesByDayPeriod(lastYearParams, wmparams);

			Map<String, Object> emco2params = new HashMap<String, Object>();
			emco2params.put("dst", 0);
			emco2params.put("channel", DefaultChannel.Co2.getCode());
			emco2params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayClassName());
			List<Object> emCo2List = meteringDayDao
					.getUsageForEndDevicesByDayPeriod(params, emco2params);

			Map<String, Object> gmco2params = new HashMap<String, Object>();
			gmco2params.put("dst", 0);
			gmco2params.put("channel", DefaultChannel.Co2.getCode());
			gmco2params.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayClassName());
			List<Object> gmCo2List = meteringDayDao
					.getUsageForEndDevicesByDayPeriod(params, gmco2params);

			Map<String, Object> wmco2params = new HashMap<String, Object>();
			wmco2params.put("dst", 0);
			wmco2params.put("channel", DefaultChannel.Co2.getCode());
			wmco2params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayClassName());
			List<Object> wmCo2List = meteringDayDao
					.getUsageForEndDevicesByDayPeriod(params, wmco2params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> gmMap = new HashMap<String, Object>();
			Map<String, Object> wmMap = new HashMap<String, Object>();
			
			Map<String, Object> preEmMap = new HashMap<String, Object>();
			Map<String, Object> preGmMap = new HashMap<String, Object>();
			Map<String, Object> preWmMap = new HashMap<String, Object>();
			
			Map<String, Object> lastYearEmMap = new HashMap<String, Object>();
			Map<String, Object> lastYearGmMap = new HashMap<String, Object>();
			Map<String, Object> lastYearWmMap = new HashMap<String, Object>();

			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			Map<String, Object> gmCo2Map = new HashMap<String, Object>();
			Map<String, Object> wmCo2Map = new HashMap<String, Object>();

			for (Object obj : emList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				emMap.put((String) emtmp.get("yyyymmdd"), emtmp.get("total"));
			}
			
			for (Object obj : preEmList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				preEmMap.put((String) emtmp.get("yyyymmdd"), emtmp.get("total"));
			}
			
			for (Object obj : lastYearEmList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				lastYearEmMap.put((String) emtmp.get("yyyymmdd"), emtmp.get("total"));
			}

			for (Object obj : gmList) {
				Map<String, Object> gmtmp = new HashMap<String, Object>();
				gmtmp = (Map<String, Object>) obj;
				gmMap.put((String) gmtmp.get("yyyymmdd"), gmtmp.get("total"));
			}
			
			for (Object obj : preGmList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				preGmMap.put((String) emtmp.get("yyyymmdd"), emtmp.get("total"));
			}
			
			for (Object obj : lastYearGmList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				lastYearGmMap.put((String) emtmp.get("yyyymmdd"), emtmp.get("total"));
			}

			for (Object obj : wmList) {
				Map<String, Object> wmtmp = new HashMap<String, Object>();
				wmtmp = (Map<String, Object>) obj;
				wmMap.put((String) wmtmp.get("yyyymmdd"), wmtmp.get("total"));
			}
			
			for (Object obj : preWmList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				preWmMap.put((String) emtmp.get("yyyymmdd"), emtmp.get("total"));
			}
			
			for (Object obj : lastYearWmList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				lastYearWmMap.put((String) emtmp.get("yyyymmdd"), emtmp.get("total"));
			}

			for (Object obj : emCo2List) {
				Map<String, Object> emco2tmp = new HashMap<String, Object>();
				emco2tmp = (Map<String, Object>) obj;
				emCo2Map.put((String) emco2tmp.get("yyyymmdd"), emco2tmp
						.get("total"));
			}

			for (Object obj : gmCo2List) {
				Map<String, Object> gmco2tmp = new HashMap<String, Object>();
				gmco2tmp = (Map<String, Object>) obj;
				gmCo2Map.put((String) gmco2tmp.get("yyyymmdd"), gmco2tmp
						.get("total"));
			}

			for (Object obj : wmCo2List) {
				Map<String, Object> wmco2tmp = new HashMap<String, Object>();
				wmco2tmp = (Map<String, Object>) obj;
				wmCo2Map.put((String) wmco2tmp.get("yyyymmdd"), wmco2tmp
						.get("total"));
			}

			int istart = Integer.parseInt(searchStartDate);
			int iend = Integer.parseInt(searchEndDate);
			
			int preStart = Integer.parseInt((String)preParams.get("searchStartDate"));
			int lastStart = Integer.parseInt((String)lastYearParams.get("searchStartDate"));
			
			for (int i = istart; i <= iend; i = Integer
					.parseInt(CalendarUtil.getDateWithoutFormat(Integer
							.toString(i), Calendar.DATE, 1))) {
				String yyyymmdd = Integer.toString(i);
				String pre_yyyymmdd = Integer.toString(preStart);
				String last_yyyymmdd = Integer.toString(lastStart);

				tmp = new HashMap<String, Object>();
				tmp.put("xField", yyyymmdd);
				tmp.put("EmUsage", new BigDecimal(
						emMap.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(emMap
								.get(yyyymmdd))).setScale(BigDecimal.ROUND_DOWN,
						3));
				tmp.put("GmUsage", new BigDecimal(
						gmMap.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(gmMap
								.get(yyyymmdd))).setScale(BigDecimal.ROUND_DOWN,
						3));
				tmp.put("WmUsage", new BigDecimal(
						wmMap.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(wmMap
								.get(yyyymmdd))).setScale(BigDecimal.ROUND_DOWN,
						3));
				
				tmp.put("PreEmUsage", new BigDecimal(
						preEmMap.get(pre_yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(preEmMap
								.get(pre_yyyymmdd))).setScale(BigDecimal.ROUND_DOWN, 3));
				tmp.put("PreGmUsage", new BigDecimal(
						preGmMap.get(pre_yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(preGmMap
								.get(pre_yyyymmdd))).setScale(BigDecimal.ROUND_DOWN, 3));
				tmp.put("PreWmUsage", new BigDecimal(
						preWmMap.get(pre_yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(preWmMap
								.get(pre_yyyymmdd))).setScale(BigDecimal.ROUND_DOWN, 3));
				
				tmp.put("LastYearEmUsage", new BigDecimal(
						lastYearEmMap.get(last_yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(lastYearEmMap
								.get(last_yyyymmdd))).setScale(BigDecimal.ROUND_DOWN, 3));
				tmp.put("LastYearGmUsage", new BigDecimal(
						lastYearGmMap.get(last_yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(lastYearGmMap
								.get(last_yyyymmdd))).setScale(BigDecimal.ROUND_DOWN, 3));
				tmp.put("LastYearWmUsage", new BigDecimal(
						lastYearWmMap.get(last_yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(lastYearWmMap
								.get(last_yyyymmdd))).setScale(BigDecimal.ROUND_DOWN, 3));
				
				tmp.put("EmCo2Usage", new BigDecimal(
						emCo2Map.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(emCo2Map
								.get(yyyymmdd))).setScale(BigDecimal.ROUND_DOWN,
						3));
				tmp.put("GmCo2Usage", new BigDecimal(
						gmCo2Map.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(gmCo2Map
								.get(yyyymmdd))).setScale(BigDecimal.ROUND_DOWN,
						3));
				tmp.put("WmCo2Usage", new BigDecimal(
						wmCo2Map.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(wmCo2Map
								.get(yyyymmdd))).setScale(BigDecimal.ROUND_DOWN,
						3));

				if (yyyymmdd.equals(today)) {
					tmp.put("isCurrent", CommonConstants.YesNo.Yes.getCode());
				} else if (Integer.parseInt(yyyymmdd) > Integer.parseInt(today)) {
					tmp.put("EmUsage", new BigDecimal(0));
					tmp.put("GmUsage", new BigDecimal(0));
					tmp.put("WmUsage", new BigDecimal(0));
					tmp.put("EmCo2Usage", new BigDecimal(0));
					tmp.put("GmCo2Usage", new BigDecimal(0));
					tmp.put("WmCo2Usage", new BigDecimal(0));
				}

				resultList.add(tmp);
				
				preStart = Integer.parseInt(CalendarUtil.getDateWithoutFormat(Integer.toString(preStart), Calendar.DATE, 1));
				lastStart = Integer.parseInt(CalendarUtil.getDateWithoutFormat(Integer.toString(lastStart), Calendar.DATE, 1));
			}

		} else if (CommonConstants.DateType.QUARTERLY.getCode().equals(
				periodType)
				|| CommonConstants.DateType.YEARLY.getCode().equals(periodType)) {
			
			Map<String, Object> preParams = new HashMap<String, Object>();
			Map<String, Object> lastYearParams = new HashMap<String, Object>();
			
			if(CommonConstants.DateType.QUARTERLY.getCode().equals(periodType)) {			
				preParams.putAll(params);
				preParams.put("searchStartDate", this.getPreQuarter(false, true, (String)params.get("searchStartDate"), Integer.parseInt((String)params.get("quarter"))));
				preParams.put("searchEndDate", this.getPreQuarter(false, false, (String)params.get("searchStartDate"), Integer.parseInt((String)params.get("quarter"))));
				
				lastYearParams.putAll(params);
				lastYearParams.put("searchStartDate", this.getPreQuarter(true, true, (String)params.get("searchStartDate"), Integer.parseInt((String)params.get("quarter"))));
				lastYearParams.put("searchEndDate", this.getPreQuarter(true, false, (String)params.get("searchStartDate"), Integer.parseInt((String)params.get("quarter"))));
			} else {
				preParams.putAll(params);
				preParams.put("searchStartDate", this.getPreYear(true, (String)params.get("searchStartDate")));
				preParams.put("searchEndDate", this.getPreYear(false, (String)params.get("searchStartDate")));
				
				lastYearParams.putAll(params);
				lastYearParams.put("searchStartDate", this.getPreYear(true, (String)params.get("searchStartDate")));
				lastYearParams.put("searchEndDate", this.getPreYear(false, (String)params.get("searchStartDate")));
			}
			
			Map<String, Object> emparams = new HashMap<String, Object>();
			emparams.put("dst", 0);
			emparams.put("channel", DefaultChannel.Usage.getCode());
			emparams.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthClassName());
			List<Object> emList = meteringDayDao
					.getUsageForEndDevicesByMonthPeriod(params, emparams);
			
			List<Object> preEmList = meteringDayDao
					.getUsageForEndDevicesByMonthPeriod(preParams, emparams);
			
			List<Object> lastYearEmList = meteringDayDao
					.getUsageForEndDevicesByMonthPeriod(lastYearParams, emparams);

			Map<String, Object> gmparams = new HashMap<String, Object>();
			gmparams.put("dst", 0);
			gmparams.put("channel", DefaultChannel.Usage.getCode());
			gmparams.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthClassName());
			List<Object> gmList = meteringDayDao
					.getUsageForEndDevicesByMonthPeriod(params, gmparams);
			
			List<Object> preGmList = meteringDayDao
					.getUsageForEndDevicesByMonthPeriod(preParams, gmparams);
			
			List<Object> lastYearGmList = meteringDayDao
					.getUsageForEndDevicesByMonthPeriod(lastYearParams, gmparams);

			Map<String, Object> wmparams = new HashMap<String, Object>();
			wmparams.put("dst", 0);
			wmparams.put("channel", DefaultChannel.Usage.getCode());
			wmparams.put("meterType", CommonConstants.MeterType.WaterMeter
					.getMonthClassName());
			List<Object> wmList = meteringDayDao
					.getUsageForEndDevicesByMonthPeriod(params, wmparams);
			
			List<Object> preWmList = meteringDayDao
					.getUsageForEndDevicesByMonthPeriod(preParams, wmparams);
			
			List<Object> lastYearWmList = meteringDayDao
					.getUsageForEndDevicesByMonthPeriod(lastYearParams, wmparams);

			Map<String, Object> emco2params = new HashMap<String, Object>();
			emco2params.put("dst", 0);
			emco2params.put("channel", DefaultChannel.Co2.getCode());
			emco2params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthClassName());
			List<Object> emCo2List = meteringDayDao
					.getUsageForEndDevicesByMonthPeriod(params, emco2params);

			Map<String, Object> gmco2params = new HashMap<String, Object>();
			gmco2params.put("dst", 0);
			gmco2params.put("channel", DefaultChannel.Co2.getCode());
			gmco2params.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthClassName());
			List<Object> gmCo2List = meteringDayDao
					.getUsageForEndDevicesByMonthPeriod(params, gmco2params);

			Map<String, Object> wmco2params = new HashMap<String, Object>();
			wmco2params.put("dst", 0);
			wmco2params.put("channel", DefaultChannel.Co2.getCode());
			wmco2params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getMonthClassName());
			List<Object> wmCo2List = meteringDayDao
					.getUsageForEndDevicesByMonthPeriod(params, wmco2params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> gmMap = new HashMap<String, Object>();
			Map<String, Object> wmMap = new HashMap<String, Object>();
			
			Map<String, Object> preEmMap = new HashMap<String, Object>();
			Map<String, Object> preGmMap = new HashMap<String, Object>();
			Map<String, Object> preWmMap = new HashMap<String, Object>();
			
			Map<String, Object> lastYearEmMap = new HashMap<String, Object>();
			Map<String, Object> lastYearGmMap = new HashMap<String, Object>();
			Map<String, Object> lastYearWmMap = new HashMap<String, Object>();

			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			Map<String, Object> gmCo2Map = new HashMap<String, Object>();
			Map<String, Object> wmCo2Map = new HashMap<String, Object>();

			for (Object obj : emList) {
				Map<String, Object> emtmp = (Map<String, Object>) obj;
				emMap.put((String) emtmp.get("yyyymm"), emtmp.get("total"));
			}
			
			for (Object obj : preEmList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				preEmMap.put((String) emtmp.get("yyyymmdd"), emtmp.get("total"));
			}
			
			for (Object obj : lastYearEmList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				lastYearEmMap.put((String) emtmp.get("yyyymmdd"), emtmp.get("total"));
			}

			for (Object obj : gmList) {
				Map<String, Object> gmtmp = (Map<String, Object>) obj;
				gmMap.put((String) gmtmp.get("yyyymm"), gmtmp.get("total"));
			}
			
			for (Object obj : preGmList) {
				Map<String, Object> gmtmp = (Map<String, Object>) obj;
				preEmMap.put((String) gmtmp.get("yyyymmdd"), gmtmp.get("total"));
			}
			
			for (Object obj : lastYearGmList) {
				Map<String, Object> gmtmp = (Map<String, Object>) obj;
				lastYearGmMap.put((String) gmtmp.get("yyyymmdd"), gmtmp.get("total"));
			}

			for (Object obj : wmList) {
				Map<String, Object> wmtmp = (Map<String, Object>) obj;
				wmMap.put((String) wmtmp.get("yyyymm"), wmtmp.get("total"));
			}
			
			for (Object obj : preWmList) {
				Map<String, Object> wmtmp = (Map<String, Object>) obj;
				preWmMap.put((String) wmtmp.get("yyyymmdd"), wmtmp.get("total"));
			}
			
			for (Object obj : lastYearWmList) {
				Map<String, Object> wmtmp = (Map<String, Object>) obj;
				lastYearWmMap.put((String) wmtmp.get("yyyymmdd"), wmtmp.get("total"));
			}

			for (Object obj : emCo2List) {
				Map<String, Object> emco2tmp = (Map<String, Object>) obj;
				emCo2Map.put((String) emco2tmp.get("yyyymm"), emco2tmp
						.get("total"));
			}

			for (Object obj : gmCo2List) {
				Map<String, Object> gmco2tmp = (Map<String, Object>) obj;
				gmCo2Map.put((String) gmco2tmp.get("yyyymm"), gmco2tmp
						.get("total"));
			}

			for (Object obj : wmCo2List) {
				Map<String, Object> wmco2tmp = (Map<String, Object>) obj;
				wmCo2Map.put((String) wmco2tmp.get("yyyymm"), wmco2tmp
						.get("total"));
			}

			int istart = Integer.parseInt(searchStartDate);
			int iend = Integer.parseInt(searchEndDate);
			
			int preStart = Integer.parseInt((String)preParams.get("searchStartDate"));
			for (int i = istart; i <= iend; i = Integer.parseInt(CalendarUtil
					.getDateWithoutFormat(Integer.toString(i) + "01",
							Calendar.MONTH, 1).substring(0, 6))) {

				String yyyymm = Integer.toString(i);
				String pre_yyyymm = Integer.toString(preStart);
				tmp = new HashMap<String, Object>();
				tmp.put("xField", yyyymm);
				tmp.put("EmUsage", new BigDecimal(emMap.get(yyyymm) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(emMap.get(yyyymm))).setScale(
						3, BigDecimal.ROUND_DOWN));
				tmp.put("GmUsage", new BigDecimal(gmMap.get(yyyymm) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(gmMap.get(yyyymm))).setScale(
						3, BigDecimal.ROUND_DOWN));
				tmp.put("WmUsage", new BigDecimal(wmMap.get(yyyymm) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(wmMap.get(yyyymm))).setScale(
						3, BigDecimal.ROUND_DOWN));
				
				tmp.put("PreEmUsage", new BigDecimal(
						preEmMap.get(pre_yyyymm) == null ? 0 : DecimalUtil.ConvertNumberToDouble(preEmMap
								.get(pre_yyyymm))).setScale(BigDecimal.ROUND_DOWN, 3));
				tmp.put("PreGmUsage", new BigDecimal(
						preGmMap.get(pre_yyyymm) == null ? 0 : DecimalUtil.ConvertNumberToDouble(preGmMap
								.get(pre_yyyymm))).setScale(BigDecimal.ROUND_DOWN, 3));
				tmp.put("PreWmUsage", new BigDecimal(
						preWmMap.get(pre_yyyymm) == null ? 0 : DecimalUtil.ConvertNumberToDouble(preWmMap
								.get(pre_yyyymm))).setScale(BigDecimal.ROUND_DOWN, 3));
				
				tmp.put("LastYearEmUsage", new BigDecimal(
						lastYearEmMap.get(pre_yyyymm) == null ? 0 : DecimalUtil.ConvertNumberToDouble(lastYearEmMap
								.get(pre_yyyymm))).setScale(BigDecimal.ROUND_DOWN, 3));
				tmp.put("LastYearGmUsage", new BigDecimal(
						lastYearGmMap.get(pre_yyyymm) == null ? 0 : DecimalUtil.ConvertNumberToDouble(lastYearGmMap
								.get(pre_yyyymm))).setScale(BigDecimal.ROUND_DOWN, 3));
				tmp.put("LastYearWmUsage", new BigDecimal(
						lastYearWmMap.get(pre_yyyymm) == null ? 0 : DecimalUtil.ConvertNumberToDouble(lastYearWmMap
								.get(pre_yyyymm))).setScale(BigDecimal.ROUND_DOWN, 3));
				
				tmp.put("EmCo2Usage", new BigDecimal(
						emCo2Map.get(yyyymm) == null ? 0 : DecimalUtil.ConvertNumberToDouble(emCo2Map
								.get(yyyymm)))
						.setScale(3, BigDecimal.ROUND_DOWN));
				tmp.put("GmCo2Usage", new BigDecimal(
						gmCo2Map.get(yyyymm) == null ? 0 : DecimalUtil.ConvertNumberToDouble(gmCo2Map
								.get(yyyymm)))
						.setScale(3, BigDecimal.ROUND_DOWN));
				tmp.put("WmCo2Usage", new BigDecimal(
						wmCo2Map.get(yyyymm) == null ? 0 : DecimalUtil.ConvertNumberToDouble(wmCo2Map
								.get(yyyymm)))
						.setScale(3, BigDecimal.ROUND_DOWN));

				if (yyyymm.equals(currYearMonth)) {
					tmp.put("isCurrent", CommonConstants.YesNo.Yes.getCode());
				}
				resultList.add(tmp);
				
				preStart = Integer.parseInt(CalendarUtil.getDateWithoutFormat(Integer.toString(preStart) + "01", Calendar.MONTH, 1).substring(0, 6));
			}
		}

		return resultList;
	}

	public Map<String, Object> getEnergySavingReportInfo(
			Map<String, Object> params) {
		
		Map<String, Object> monthlyEnergySavingInfo = getMonthlyEnergySavingInfo(params);
		
		Map<String, Object> yearlyEnergySavingInfo = getYearlyEnergySavingInfo(params);

		Map<String, Object> resultData = new HashMap<String, Object>();
		
		monthlyEnergySavingInfo.put("supplierId", params.get("supplierId"));
		yearlyEnergySavingInfo.put("supplierId", params.get("supplierId"));
		
		resultData.put("Monthly", monthlyEnergySavingInfo);
		resultData.put("Yearly", yearlyEnergySavingInfo);

		return resultData;
	}

	public Map<String, Object> getZoneUsageInfo(
			Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
		
        resultMap.put("zoneTotal", dayEMDao.getDayEmsLocationUsage(params)); 
		resultMap.put("total", dayEMDao.getTotalDayEmsLocationUsage(params));
		
		return resultMap;
	}
	
	public Map<String, Object> getLocationUsageInfo(
			Map<String, Object> params) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
		
        resultMap.put("locationTotal", dayEMDao.getDayEmsZoneUsage(params)); 
		resultMap.put("total", dayEMDao.getTotalDayEmsZoneUsage(params));
		
		return resultMap;
	}

	public Map<String, Object> getMonthlyEnergySavingInfo(
			Map<String, Object> params) {

		String[] searchYear = { StringUtils.defaultIfEmpty(String
				.valueOf(params.get("searchYear")), "0") };
		String supplierId = StringUtils.defaultIfEmpty(String.valueOf(params
				.get("supplierId")), "0");

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String yyyymmdd = df.format(Calendar.getInstance().getTime());

		double emMonthlyGoal = 0;
		double wmMonthlyGoal = 0;
		double gmMonthlyGoal = 0;

		List<Object> emMonthlyReport = new ArrayList<Object>();
		List<Object> wmMonthlyReport = new ArrayList<Object>();
		List<Object> gmMonthlyReport = new ArrayList<Object>();

		AverageUsage avgUsage = averageUsageDao.getAverageUsageByUsed();
		if (avgUsage != null) {
			String years = avgUsage.getBasesToString();
			String[] yearList = StringUtils.split(years, ",");

			/** 전기 **/
			List<EnergySavingGoal2> emGoalList = null;
			emGoalList = energySavingGoal2Dao
					.getEnergySavingGoal2ListByStartDate(CommonConstants.DateType.MONTHLY.getCode(), "0", yyyymmdd,
							Integer.parseInt(supplierId));
			if (emGoalList != null && emGoalList.size() > 0) {
				emMonthlyGoal = emGoalList.get(0).getSavingGoal();
			}

			List<Object> emTotalUsage = monthEMDao
					.getEnergySavingReportMonthlyData(yearList, 1, getMeterIds(
							supplierId, "1.3.1.1")); // 전기 평균관리 총 월별 사용량
			List<Object> emTotalCo2 = monthEMDao
					.getEnergySavingReportMonthlyData(yearList, 0, getMeterIds(
							supplierId, "1.3.1.1")); // 전기 평균관리 총 월별 탄소배출량

			List<Object> emSearchUsage = monthEMDao
					.getEnergySavingReportMonthlyData(searchYear, 1,
							getMeterIds(supplierId, "1.3.1.1")); // 전기 월별 사용량
			List<Object> emSearchCo2 = monthEMDao
					.getEnergySavingReportMonthlyData(searchYear, 0,
							getMeterIds(supplierId, "1.3.1.1")); // 전기 월별 탄소배출량

			emMonthlyReport = getMonthlyEnergySavingDataSet("EM", emMonthlyGoal,
					searchYear[0], yearList, emTotalUsage, emTotalCo2,
					emSearchUsage, emSearchCo2);

			/** 수도 **/
			List<EnergySavingGoal2> wmGoalList = null;
			wmGoalList = energySavingGoal2Dao
					.getEnergySavingGoal2ListByStartDate(CommonConstants.DateType.MONTHLY.getCode(), "2", yyyymmdd,
							Integer.parseInt(supplierId));
			if (wmGoalList != null && wmGoalList.size() > 0) {
				wmMonthlyGoal = wmGoalList.get(0).getSavingGoal();
			}

			List<Object> wmTotalUsage = monthWMDao
					.getEnergySavingReportMonthlyData(yearList, 1, getMeterIds(
							supplierId, "1.3.1.2")); // 수도 평균관리 총 월별 사용량
			List<Object> wmTotalCo2 = monthWMDao
					.getEnergySavingReportMonthlyData(yearList, 0, getMeterIds(
							supplierId, "1.3.1.2")); // 수도 평균관리 총 월별 탄소배출량

			List<Object> wmSearchUsage = monthWMDao
					.getEnergySavingReportMonthlyData(searchYear, 1,
							getMeterIds(supplierId, "1.3.1.2")); // 수도 월별 사용량
			List<Object> wmSearchCo2 = monthWMDao
					.getEnergySavingReportMonthlyData(searchYear, 0,
							getMeterIds(supplierId, "1.3.1.2")); // 수도 월별 탄소배출량

			wmMonthlyReport = getMonthlyEnergySavingDataSet("WM", wmMonthlyGoal,
					searchYear[0], yearList, wmTotalUsage, wmTotalCo2,
					wmSearchUsage, wmSearchCo2);

			/** 가스 **/
			List<EnergySavingGoal2> gmGoalList = null;
			gmGoalList = energySavingGoal2Dao
					.getEnergySavingGoal2ListByStartDate(CommonConstants.DateType.MONTHLY.getCode(), "1", yyyymmdd,
							Integer.parseInt(supplierId));
			if (gmGoalList != null && gmGoalList.size() > 0) {
				gmMonthlyGoal = gmGoalList.get(0).getSavingGoal();
			}

			List<Object> gmTotalUsage = monthGMDao
					.getEnergySavingReportMonthlyData(yearList, 1, getMeterIds(
							supplierId, "1.3.1.3")); // 가스 평균관리 총 월별 사용량
			List<Object> gmTotalCo2 = monthGMDao
					.getEnergySavingReportMonthlyData(yearList, 0, getMeterIds(
							supplierId, "1.3.1.3")); // 가스 평균관리 총 월별 탄소배출량

			List<Object> gmSearchUsage = monthGMDao
					.getEnergySavingReportMonthlyData(searchYear, 1,
							getMeterIds(supplierId, "1.3.1.3")); // 가스 월별 사용량
			List<Object> gmSearchCo2 = monthGMDao
					.getEnergySavingReportMonthlyData(searchYear, 0,
							getMeterIds(supplierId, "1.3.1.3")); // 가스 월별 탄소배출량

			gmMonthlyReport = getMonthlyEnergySavingDataSet("GM", gmMonthlyGoal,
					searchYear[0], yearList, gmTotalUsage, gmTotalCo2,
					gmSearchUsage, gmSearchCo2);

		}

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("emMonthlyReport", emMonthlyReport);
		resultMap.put("wmMonthlyReport", wmMonthlyReport);
		resultMap.put("gmMonthlyReport", gmMonthlyReport);

		return resultMap;
	}

	public Map<String, Object> getYearlyEnergySavingInfo(
			Map<String, Object> params) {

		String[] searchYear = { StringUtils.defaultIfEmpty(String
				.valueOf(params.get("searchYear")), "0") };
		String supplierId = StringUtils.defaultIfEmpty(String.valueOf(params
				.get("supplierId")), "0");

		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String yyyymmdd = df.format(Calendar.getInstance().getTime());

		double emYearlyGoal = 0;
		double wmYearlyGoal = 0;
		double gmYearlyGoal = 0;

		List<Object> emYearlyReport = new ArrayList<Object>();
		List<Object> wmYearlyReport = new ArrayList<Object>();
		List<Object> gmYearlyReport = new ArrayList<Object>();

		AverageUsage avgUsage = averageUsageDao.getAverageUsageByUsed();
		if (avgUsage != null) {
			String years = avgUsage.getBasesToString();
			String[] yearList = StringUtils.split(years, ",");

			/** 전기 **/
			List<EnergySavingGoal2> emGoalList = null;
			emGoalList = energySavingGoal2Dao
					.getEnergySavingGoal2ListByStartDate(CommonConstants.DateType.YEARLY.getCode(), "0", yyyymmdd,
							Integer.parseInt(supplierId));
			if (emGoalList != null && emGoalList.size() > 0) {
				emYearlyGoal = emGoalList.get(0).getSavingGoal();
			}

			List<Object> emTotalUsage = monthEMDao
					.getEnergySavingReportYearlyData(yearList, 1, getMeterIds(
							supplierId, "1.3.1.1")); // 전기 평균관리 총 월별 사용량
			List<Object> emTotalCo2 = monthEMDao
					.getEnergySavingReportYearlyData(yearList, 0, getMeterIds(
							supplierId, "1.3.1.1")); // 전기 평균관리 총 월별 탄소배출량

			List<Object> emSearchUsage = monthEMDao
					.getEnergySavingReportYearlyData(searchYear, 1,
							getMeterIds(supplierId, "1.3.1.1")); // 전기 월별 사용량
			List<Object> emSearchCo2 = monthEMDao
					.getEnergySavingReportYearlyData(searchYear, 0,
							getMeterIds(supplierId, "1.3.1.1")); // 전기 월별 탄소배출량

			emYearlyReport = getYearlyEnergySavingDataSet("EM", emYearlyGoal,
					searchYear[0], yearList, emTotalUsage, emTotalCo2,
					emSearchUsage, emSearchCo2);

			/** 수도 **/
			List<EnergySavingGoal2> wmGoalList = null;
			wmGoalList = energySavingGoal2Dao
					.getEnergySavingGoal2ListByStartDate(CommonConstants.DateType.YEARLY.getCode(), "2", yyyymmdd,
							Integer.parseInt(supplierId));
			if (wmGoalList != null && wmGoalList.size() > 0) {
				wmYearlyGoal = wmGoalList.get(0).getSavingGoal();
			}

			List<Object> wmTotalUsage = monthWMDao
					.getEnergySavingReportMonthlyData(yearList, 1, getMeterIds(
							supplierId, "1.3.1.2")); // 수도 평균관리 총 월별 사용량
			List<Object> wmTotalCo2 = monthWMDao
					.getEnergySavingReportMonthlyData(yearList, 0, getMeterIds(
							supplierId, "1.3.1.2")); // 수도  평균관리 총 월별 탄소배출량

			List<Object> wmSearchUsage = monthWMDao
					.getEnergySavingReportMonthlyData(searchYear, 1,
							getMeterIds(supplierId, "1.3.1.2")); // 수도  월별 사용량
			List<Object> wmSearchCo2 = monthWMDao
					.getEnergySavingReportMonthlyData(searchYear, 0,
							getMeterIds(supplierId, "1.3.1.2")); // 수도  월별 탄소배출량

			wmYearlyReport = getYearlyEnergySavingDataSet("WM", wmYearlyGoal,
					searchYear[0], yearList, wmTotalUsage, wmTotalCo2,
					wmSearchUsage, wmSearchCo2);

			/** 가스 **/
			List<EnergySavingGoal2> gmGoalList = null;
			gmGoalList = energySavingGoal2Dao
					.getEnergySavingGoal2ListByStartDate(CommonConstants.DateType.YEARLY.getCode(), "1", yyyymmdd,
							Integer.parseInt(supplierId));
			if (gmGoalList != null && gmGoalList.size() > 0) {
				gmYearlyGoal = gmGoalList.get(0).getSavingGoal();
			}

			List<Object> gmTotalUsage = monthGMDao
					.getEnergySavingReportMonthlyData(yearList, 1, getMeterIds(
							supplierId, "1.3.1.3")); // 가스  평균관리 총 월별 사용량
			List<Object> gmTotalCo2 = monthGMDao
					.getEnergySavingReportMonthlyData(yearList, 0, getMeterIds(
							supplierId, "1.3.1.3")); // 가스  평균관리 총 월별 탄소배출량

			List<Object> gmSearchUsage = monthGMDao
					.getEnergySavingReportMonthlyData(searchYear, 1,
							getMeterIds(supplierId, "1.3.1.3")); // 가스  월별 사용량
			List<Object> gmSearchCo2 = monthGMDao
					.getEnergySavingReportMonthlyData(searchYear, 0,
							getMeterIds(supplierId, "1.3.1.3")); // 가스  월별 탄소배출량

			gmYearlyReport = getYearlyEnergySavingDataSet("GM", gmYearlyGoal,
					searchYear[0], yearList, gmTotalUsage, gmTotalCo2,
					gmSearchUsage, gmSearchCo2);

		}

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("emYearlyReport", emYearlyReport);
		resultMap.put("wmYearlyReport", wmYearlyReport);
		resultMap.put("gmYearlyReport", gmYearlyReport);

		return resultMap;
	}

	@SuppressWarnings({ "rawtypes", "unused" })
    private Integer[] getMeterIds(String supplierId, String meterType) {
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

		Set<Condition> conditionSetMeters = new HashSet<Condition>();
		conditionSetMeters.add(cdtLocationChildren);

		Code meterTypeCodeObject = codeDao.getCodeIdByCodeObject(meterType); // 코드
																				// em/wm/gm
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

		return meterIds;
	}

	@SuppressWarnings("unchecked")
    private List<Object> getMonthlyEnergySavingDataSet(String energyType, double goal,
			String searchYear, String[] yearList, List<Object> totalUsageList,
			List<Object> totalCo2List, List<Object> searchUsageList,
			List<Object> searchCo2List) {

		DecimalFormat df = new DecimalFormat("00");

		List<Object> dataSetList = new ArrayList<Object>();
		Map<String, Object> dataSet = null;
		Map<String, Object> tempMap = null;
		for (int i = 1; i <= 12; i++) {
			double goalUsage = 0;
			double goalToe = 0;
			double usage = 0;
			double toe = 0;
			double avgCo2 = 0;
			double co2 = 0;

			dataSet = new HashMap<String, Object>();
			dataSet.put("month", searchYear + df.format(i) + "");
			dataSet.put("goal", String.valueOf(goal));

			for (Object totalUsage : totalUsageList) {
				tempMap = (Map<String, Object>) totalUsage;
				if (String.valueOf(tempMap.get("MM")).equals(df.format(i))) {
					Double total = DecimalUtil.ConvertNumberToDouble(tempMap.get("TOTAL")) / yearList.length;
					goalUsage = total - (total / 100) * goal;
					if(energyType.equals("EM")) goalToe = goalUsage * TOE.Energy.getValue();
					else if(energyType.equals("WM")) goalToe = goalUsage * TOE.Water.getValue();
					else if(energyType.equals("GM")) goalToe = goalUsage * TOE.GasLng.getValue();
					
				}
			}

			for (Object searchUsage : searchUsageList) {
				tempMap = (Map<String, Object>) searchUsage;
				if (String.valueOf(tempMap.get("MM")).equals(df.format(i))) {
					usage = DecimalUtil.ConvertNumberToDouble(tempMap.get("TOTAL"));
					toe = usage * TOE.Energy.getValue();
					if(energyType.equals("EM")) toe = usage * TOE.Energy.getValue();
					else if(energyType.equals("WM")) toe = usage * TOE.Water.getValue();
					else if(energyType.equals("GM")) toe = usage * TOE.GasLng.getValue();
				}
			}

			for (Object totalCo2 : totalCo2List) {
				tempMap = (Map<String, Object>) totalCo2;
				if (String.valueOf(tempMap.get("MM")).equals(df.format(i))) {
					avgCo2 = DecimalUtil.ConvertNumberToDouble(tempMap.get("TOTAL")) / yearList.length;
				}
			}

			for (Object searchCo2 : searchCo2List) {
				tempMap = (Map<String, Object>) searchCo2;
				if (String.valueOf(tempMap.get("MM")).equals(df.format(i))) {
					co2 = DecimalUtil.ConvertNumberToDouble(tempMap.get("TOTAL"));
				}
			}

			dataSet.put("goalUsage", new BigDecimal(goalUsage).setScale(
					3, BigDecimal.ROUND_DOWN).toString());
			dataSet.put("goalToe", new BigDecimal(goalToe).setScale(
					3, BigDecimal.ROUND_DOWN).toString());
			dataSet.put("usage", new BigDecimal(usage).setScale(
					3, BigDecimal.ROUND_DOWN).toString());
			dataSet.put("toe", new BigDecimal(toe).setScale(
					3, BigDecimal.ROUND_DOWN).toString());
			dataSet.put("effectedUsage", new BigDecimal((goalUsage - usage)
					* -1).setScale(3, BigDecimal.ROUND_DOWN).toString());
			dataSet.put("effectedToe", new BigDecimal((goalToe - toe) * -1)
					.setScale(3, BigDecimal.ROUND_DOWN).toString());
			dataSet.put("reduceUsageRate", new BigDecimal(getSavingPercentage(
					(int) goalUsage, (int) usage)).setScale(
					3, BigDecimal.ROUND_DOWN).toString());
			dataSet.put("reduceCo2", new BigDecimal((avgCo2 - co2) * -1)
					.setScale(3, BigDecimal.ROUND_DOWN).toString());

			dataSetList.add(dataSet);
		}

		return dataSetList;
	}

	@SuppressWarnings("unchecked")
    private List<Object> getYearlyEnergySavingDataSet(String energyType, double goal,
			String searchYear, String[] yearList, List<Object> totalUsageList,
			List<Object> totalCo2List, List<Object> searchUsageList,
			List<Object> searchCo2List) {

		List<Object> dataSetList = new ArrayList<Object>();
		Map<String, Object> dataSet = null;
		Map<String, Object> tempMap = null;
		
		double avgUsage = 0;
		double goalUsage = 0;
		double goalToe = 0;
		double usage = 0;
		double toe = 0;
		double avgCo2 = 0;
		double co2 = 0;

		dataSet = new HashMap<String, Object>();
		dataSet.put("year", searchYear);
		dataSet.put("goal", String.valueOf(goal));
		dataSet.put("period", String.valueOf(yearList.length));

		for (Object totalUsage : totalUsageList) {
			tempMap = (Map<String, Object>) totalUsage;
			avgUsage = DecimalUtil.ConvertNumberToDouble(tempMap.get("TOTAL")) / yearList.length;
			goalUsage = avgUsage - (avgUsage / 100) * goal;
			if(energyType.equals("EM")) goalToe = goalUsage * TOE.Energy.getValue();
			else if(energyType.equals("WM")) goalToe = goalUsage * TOE.Water.getValue();
			else if(energyType.equals("GM")) goalToe = goalUsage * TOE.GasLng.getValue();
			
		}

		for (Object searchUsage : searchUsageList) {
			tempMap = (Map<String, Object>) searchUsage;
			usage = DecimalUtil.ConvertNumberToDouble(tempMap.get("TOTAL"));
			if(energyType.equals("EM")) toe = usage * TOE.Energy.getValue();
			else if(energyType.equals("WM")) toe = usage * TOE.Water.getValue();
			else if(energyType.equals("GM")) toe = usage * TOE.GasLng.getValue();
		}

		for (Object totalCo2 : totalCo2List) {
			tempMap = (Map<String, Object>) totalCo2;
			avgCo2 = DecimalUtil.ConvertNumberToDouble(tempMap.get("TOTAL")) / yearList.length;
		}

		for (Object searchCo2 : searchCo2List) {
			tempMap = (Map<String, Object>) searchCo2;
			co2 = DecimalUtil.ConvertNumberToDouble(tempMap.get("TOTAL"));
		}

		dataSet.put("avgUsage", new BigDecimal(avgUsage).setScale(
				3, BigDecimal.ROUND_DOWN).toString());
		dataSet.put("goalUsage", new BigDecimal(goalUsage).setScale(
				3, BigDecimal.ROUND_DOWN).toString());
		dataSet.put("goalToe", new BigDecimal(goalToe).setScale(
				3, BigDecimal.ROUND_DOWN).toString());
		dataSet.put("usage", new BigDecimal(usage).setScale(
				3, BigDecimal.ROUND_DOWN).toString());
		dataSet.put("toe", new BigDecimal(toe).setScale(
				3, BigDecimal.ROUND_DOWN).toString());
		dataSet.put("effectedUsage", new BigDecimal((goalUsage - usage)
				* -1).setScale(3, BigDecimal.ROUND_DOWN).toString());
		dataSet.put("effectedToe", new BigDecimal((goalToe - toe) * -1)
				.setScale(3, BigDecimal.ROUND_DOWN).toString());
		dataSet.put("reduceUsageRate", new BigDecimal(getSavingPercentage(
				(int) goalUsage, (int) usage)).setScale(
				3, BigDecimal.ROUND_DOWN).toString());
		dataSet.put("reduceCo2", new BigDecimal((avgCo2 - co2) * -1)
				.setScale(3, BigDecimal.ROUND_DOWN).toString());

		dataSetList.add(dataSet);

		return dataSetList;
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
	
	private String getPreDay(boolean preYear, String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Calendar ti = Calendar.getInstance();
		try {
			ti.setTime(df.parse(date));
			
			if(preYear) {
				ti.add(Calendar.YEAR, -1);
			} else {
				ti.add(Calendar.DATE, -1);				
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return df.format(ti.getTime());		
	}
	
	private String getPreWeek(boolean preYear, boolean startWeekDay, String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Calendar ti = Calendar.getInstance();
		try {
			ti.setTime(df.parse(date));
			int weekOfYear = ti.get(Calendar.WEEK_OF_YEAR);
			
			if(preYear) {
				ti.add(Calendar.YEAR, -1);
				ti.set(Calendar.WEEK_OF_YEAR, weekOfYear);
			} else {
				ti.set(Calendar.WEEK_OF_YEAR, weekOfYear - 1);
			}
			
			if(startWeekDay) {
				ti.set(Calendar.DAY_OF_WEEK, 1);
			} else {
				ti.set(Calendar.DAY_OF_WEEK, 7);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return df.format(ti.getTime());
	}
	
	private String getPreMonth(boolean preYear, boolean startMonthDay, String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Calendar ti = Calendar.getInstance();
		try {
			ti.setTime(df.parse(date));
			
			if(preYear) {
				ti.add(Calendar.YEAR, -1);			
			} else {
				ti.add(Calendar.MONTH, -1);
			}
			
			if(startMonthDay) {
				ti.set(Calendar.DAY_OF_MONTH, 1);
			} else {
				ti.set(Calendar.DAY_OF_MONTH, ti.getActualMaximum(Calendar.DAY_OF_MONTH));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return df.format(ti.getTime());
	}
	
	private String getPreQuarter(boolean preYear, boolean startMonthDay, String date, int quarter) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
		Calendar ti = Calendar.getInstance();
		try {
			ti.setTime(df.parse(date));
			int qt = quarter;
			
			if(preYear) {
				ti.add(Calendar.YEAR, -1);
			} else {
				if(qt != 1) qt = qt - 1;
				else {
					ti.add(Calendar.YEAR, -1);
					qt = 4;
				}
			}
			
			if(startMonthDay) {
				ti.set(Calendar.MONTH, (qt * 3) - 3);
			} else {
				ti.set(Calendar.MONTH, (qt * 3) - 1);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return df.format(ti.getTime());
	}
	
	private String getPreYear(boolean startYearMonth, String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
		Calendar ti = Calendar.getInstance();
		try {
			ti.setTime(df.parse(date));
			ti.add(Calendar.YEAR, -1);
			if(startYearMonth) {				
				ti.set(Calendar.MONTH, 0);
			} else {
				ti.set(Calendar.MONTH, 11);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return df.format(ti.getTime());
	}
	
}
