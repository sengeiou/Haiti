package com.aimir.service.system.impl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.WeekDay;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayHUMDao;
import com.aimir.dao.mvm.DayTMDao;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.dao.mvm.MonthHUMDao;
import com.aimir.dao.mvm.MonthTMDao;
import com.aimir.dao.system.AverageUsageBaseDao;
import com.aimir.dao.system.AverageUsageDao;
import com.aimir.dao.system.EnergySavingGoalDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.AverageUsageBase;
import com.aimir.model.system.EnergySavingGoal;
import com.aimir.model.system.Language;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.ConsumptionMonitoringGmManager;
import com.aimir.util.BemsStatisticUtil;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DecimalUtil;

@WebService(endpointInterface = "com.aimir.service.system.ConsumptionMonitoringGmManager")
@Service(value = "consumptionMonitoringGmManager")
public class ConsumptionMonitoringGmManagerImpl implements
		ConsumptionMonitoringGmManager {

	Log logger = LogFactory.getLog(ConsumptionMonitoringGmManagerImpl.class);

	@Autowired
	DayGMDao dayGmDao;

	@Autowired
	MonthGMDao monthGMDao;

	@Autowired
	DayTMDao dayTMDao;
	@Autowired
	MonthTMDao monthTMDao;
	@Autowired
	DayHUMDao dayHUMDao;
	@Autowired
	MonthHUMDao monthHUMDao;
	@Autowired
	LocationDao locationDao;
	@Autowired
	EnergySavingGoalDao energySavingGoalDao;
	@Autowired
	AverageUsageBaseDao averageUsageBaseDao;
	@Autowired
	AverageUsageDao averageUsageDao;

	@Autowired
	MeteringDayDao meteringDayDao;

	@Autowired
	SupplierDao supplierDao;
	
	/*
	 * supplierId 와 주기 정보를 이용하여 현 시점을 기준으로 기간내의 사용량과 탄소배출량을 반환함.
	 */
	@SuppressWarnings("unchecked")
    public Map<String, Object> getTotalUseOfSearchType(
			Map<String, String> condition) {

		List<Object> returnEnergyGrid = new ArrayList<Object>();
		List<Object> returnCo2Grid = new ArrayList<Object>();
		List<Object> returnEnergyGrid2 = new ArrayList<Object>();
		List<Object> returnCo2Grid2 = new ArrayList<Object>();
		List<String> dayList = new ArrayList<String>();
		List<String> weekList = new ArrayList<String>();
		List<String> weekList2 = new ArrayList<String>();

		Double averageUsage = 0.0;
		Double averageCo2Usage = 0.0;
		String searchDateType = StringUtils.defaultIfEmpty((String) condition
				.get("searchDateType"), CommonConstants.DateType.DAILY
				.getCode());
		String startWday = "";
		String endWday = "";
		try {

			String supplierId = StringUtils.defaultIfEmpty((String) condition
					.get("supplierId"), "0");
			Integer locationId = Integer.parseInt(StringUtils.defaultIfEmpty(
					(String) condition.get("locationId"), "0"));

			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMM");
			SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat formatter3 = new SimpleDateFormat("dd");
			Calendar c = Calendar.getInstance();
			// System.out.println("##############################2010.07.07일 셋팅! TEST 가 끝나면 지울것!![getTotalUseOfSearchType]");
			// c.set(2010, 9, 15); // 2010.05.19일 셋팅! TEST 가 끝나면 지울것!!

			Map<String, Object> conditionDay = new HashMap<String, Object>();
			conditionDay.put("searchDateType", CommonConstants.DateType.DAILY
					.getCode());
			conditionDay.put("supplierId", supplierId);
			conditionDay.put("locationId", locationId);
			conditionDay.put("startDate", formatter1.format(c.getTime()));
			conditionDay.put("endDate", null);
			conditionDay.put("channel", DefaultChannel.Usage.getCode());
			conditionDay.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthTableName());

			dayList.add(formatter3.format(c.getTime()));

			Map<String, Object> conditionWeek = new HashMap<String, Object>();
			conditionWeek.put("searchDateType", CommonConstants.DateType.WEEKLY
					.getCode());
			conditionWeek.put("supplierId", supplierId);
			conditionWeek.put("locationId", locationId);
			c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			startWday = formatter2.format(c.getTime());
			conditionWeek.put("startDate", formatter1.format(c.getTime()));
			c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			endWday = formatter2.format(c.getTime());
			conditionWeek.put("endDate", formatter1.format(c.getTime()));
			conditionWeek.put("channel", DefaultChannel.Usage.getCode());
			conditionWeek.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthTableName());
			String weekday = "";

			for (int i = 0; i < 7; i++) {
				weekday = CalendarUtil.getDateWithoutFormat(startWday,
						Calendar.DATE, i);
				if (weekday.startsWith(startWday)) {
					weekList.add(weekday.substring(6, 8));
				} else {
					weekList2.add(weekday.substring(6, 8));
				}
			}

			Map<String, Object> conditionMonth = new HashMap<String, Object>();
			conditionMonth.put("searchDateType",
					CommonConstants.DateType.MONTHLY.getCode());
			conditionMonth.put("supplierId", supplierId);
			conditionMonth.put("locationId", locationId);
			// c.set(c.get(Calendar.YEAR), 0, 1);
			conditionMonth.put("startDate", formatter1.format(c.getTime()));
			// c.set(c.get(Calendar.YEAR), 11, 1);
			// conditionMonth.put("endDate", formatter1.format(c.getTime()));
			conditionMonth.put("channel", DefaultChannel.Usage.getCode());
			conditionMonth.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthTableName());

			Map<String, Object> conditionYear = new HashMap<String, Object>();
			conditionYear.put("searchDateType",
					CommonConstants.DateType.QUARTERLY.getCode());
			conditionYear.put("supplierId", supplierId);
			conditionYear.put("locationId", locationId);
			c.set(c.get(Calendar.YEAR), 0, 1);
			conditionYear.put("startDate", formatter1.format(c.getTime()));
			c.set(c.get(Calendar.YEAR), 11, 1);
			conditionYear.put("endDate", formatter1.format(c.getTime()));
			conditionYear.put("channel", DefaultChannel.Usage.getCode());
			conditionYear.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthTableName());

			// 년간 평균사용량 추출
			Double yearAverageUsage = 0.0;
			Double yearAverageCo2Usage = 0.0;
			List<EnergySavingGoal> esgList = energySavingGoalDao
					.getEnergySavingGoalListBystartDate((String) conditionDay
							.get("startDate"), Integer.parseInt(supplierId));
			if (esgList != null && !esgList.isEmpty()) {

				EnergySavingGoal esg = esgList.get(0);

				List<AverageUsageBase> averageUsageBaseList = averageUsageBaseDao
						.getAverageUsageBaseListBystartDate(esg
								.getAverageUsage().getId(), new Integer(0),
								String.valueOf(c.get(Calendar.YEAR)));

				if (averageUsageBaseList != null
						&& !averageUsageBaseList.isEmpty()) {
					yearAverageUsage = averageUsageBaseList.get(0)
							.getUsageValue();
					yearAverageCo2Usage = averageUsageBaseList.get(0)
							.getCo2Value();
				}
			}
			Calendar c2 = Calendar.getInstance();
			c2.set(c2.get(Calendar.YEAR), 11, 31);

			if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {

				// 일간 빌딩 total 전력사용량/ total 탄소배출량
				returnEnergyGrid = monthGMDao
						.getConsumptionGmCo2MonthSearchDayTypeTotal2(conditionDay);
				conditionDay.put("channel", DefaultChannel.Co2.getCode());
				returnCo2Grid = monthGMDao
						.getConsumptionGmCo2MonthSearchDayTypeTotal2(conditionDay);
				// returnGrid = dayEmDao.getConsumptionEmCo2SearchDayTypeTotal(
				// conditionDay );
				if (yearAverageUsage > 0) {

					averageUsage = yearAverageUsage / 365;
				}
				if (yearAverageCo2Usage > 0) {

					averageCo2Usage = yearAverageCo2Usage / 365;
				}
			} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
					searchDateType)) {

				// 주간
				if (!startWday.equals(endWday)) {
					returnEnergyGrid = monthGMDao
							.getConsumptionGmCo2MonthSearchDayTypeTotal2(conditionWeek);

					returnEnergyGrid2 = monthGMDao
							.getConsumptionGmCo2MonthSearchDayTypeTotal2(conditionWeek);
					conditionWeek.put("channel", DefaultChannel.Co2.getCode());

					returnCo2Grid = monthGMDao
							.getConsumptionGmCo2MonthSearchDayTypeTotal2(conditionWeek);
					returnCo2Grid2 = monthGMDao
							.getConsumptionGmCo2MonthSearchDayTypeTotal2(conditionWeek);

				} else {
					returnEnergyGrid = monthGMDao
							.getConsumptionGmCo2MonthSearchDayTypeTotal2(conditionWeek);
					conditionWeek.put("channel", DefaultChannel.Co2.getCode());
					returnCo2Grid = monthGMDao
							.getConsumptionGmCo2MonthSearchDayTypeTotal2(conditionWeek);
				}
				if (yearAverageUsage > 0) {

					averageUsage = yearAverageUsage
							/ (c2.getMaximum(Calendar.WEEK_OF_YEAR) - 1);
				}
				if (yearAverageCo2Usage > 0) {

					averageCo2Usage = yearAverageCo2Usage
							/ (c2.getMaximum(Calendar.WEEK_OF_YEAR) - 1);
				}
			} else if (CommonConstants.DateType.MONTHLY.getCode().equals(
					searchDateType)) {

				// 월
				returnEnergyGrid = monthGMDao
						.getConsumptionGmCo2MonthSearchDayTypeTotal2(conditionMonth);

				conditionMonth.put("channel", DefaultChannel.Co2.getCode());
				returnCo2Grid = monthGMDao
						.getConsumptionGmCo2MonthSearchDayTypeTotal2(conditionMonth);

				if (yearAverageUsage > 0) {

					// averageUsage = yearAverageUsage / (
					// c2.getMaximum(Calendar.WEEK_OF_YEAR)-1 );
					averageUsage = yearAverageUsage / 12;
				}
				if (yearAverageCo2Usage > 0) {

					// averageCo2Usage = yearAverageCo2Usage / (
					// c2.getMaximum(Calendar.WEEK_OF_YEAR)-1 );
					averageCo2Usage = yearAverageCo2Usage / 12;
				}
			} else if (CommonConstants.DateType.QUARTERLY.getCode().equals(
					searchDateType)) {

				// 분기
				returnEnergyGrid = monthGMDao
						.getConsumptionGmCo2MonthSearchDayTypeTotal2(conditionYear);
				conditionYear.put("channel", DefaultChannel.Co2.getCode());
				returnCo2Grid = monthGMDao
						.getConsumptionGmCo2MonthSearchDayTypeTotal2(conditionYear);
				if (yearAverageUsage > 0) {

					averageUsage = yearAverageUsage / 4; // 4분기
					// averageUsage = yearAverageUsage / (
					// c2.getMaximum(Calendar.MONTH)+1 );
				}
				if (yearAverageCo2Usage > 0) {

					averageCo2Usage = yearAverageCo2Usage / 4; // 4분기
					// averageCo2Usage = yearAverageCo2Usage / (
					// c2.getMaximum(Calendar.MONTH)+1 );
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		double total = 0;
		double co2Total = 0;
		if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {
			if (!returnEnergyGrid.isEmpty()) {

				Map<String, Object> a = new HashMap<String, Object>();
				a = (Map<String, Object>) returnEnergyGrid.get(0);
				if (a.get("VALUE_" + dayList.get(0)) != null) {

					total = DecimalUtil.ConvertNumberToDouble(a.get("VALUE_" + dayList.get(0)));
				}

			}

			if (!returnCo2Grid.isEmpty()) {

				Map<String, Object> a = new HashMap<String, Object>();
				a = (Map<String, Object>) returnCo2Grid.get(0);
				if (a.get("VALUE_" + dayList.get(0)) != null) {

					co2Total = DecimalUtil.ConvertNumberToDouble(a.get("VALUE_" + dayList.get(0)));
				}

			}

		} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
				searchDateType)) {

			if (!returnEnergyGrid.isEmpty()) {
				Map<String, Object> a = (Map<String, Object>) returnEnergyGrid
						.get(0);

				for (String w : weekList) {

					if (a.get("VALUE_" + w) != null) {

						total = total + DecimalUtil.ConvertNumberToDouble(a.get("VALUE_" + w));
					}
				}
			}

			if (!returnCo2Grid.isEmpty()) {
				Map<String, Object> a = (Map<String, Object>) returnCo2Grid
						.get(0);

				for (String w : weekList) {

					if (a.get("VALUE_" + w) != null) {

						co2Total = co2Total + DecimalUtil.ConvertNumberToDouble(a.get("VALUE_" + w));
					}
				}
			}

			if (!returnEnergyGrid2.isEmpty()) {
				Map<String, Object> a = (Map<String, Object>) returnEnergyGrid2
						.get(0);

				for (String w : weekList2) {

					if (a.get("VALUE_" + w) != null) {

						total = total + DecimalUtil.ConvertNumberToDouble(a.get("VALUE_" + w));
					}
				}
			}

			if (!returnCo2Grid.isEmpty()) {
				Map<String, Object> a = (Map<String, Object>) returnCo2Grid2
						.get(0);

				for (String w : weekList2) {

					if (a.get("VALUE_" + w) != null) {

						co2Total = co2Total + DecimalUtil.ConvertNumberToDouble(a.get("VALUE_" + w));
					}
				}
			}

		} else if (CommonConstants.DateType.MONTHLY.getCode().equals(
				searchDateType)) {
			if (!returnEnergyGrid.isEmpty()) {

				Map<String, Object> a = new HashMap<String, Object>();
				a = (Map<String, Object>) returnEnergyGrid.get(0);
				if (a.get("TOTAL") != null) {

					total = DecimalUtil.ConvertNumberToDouble(a.get("TOTAL"));
				}

			}

			if (!returnCo2Grid.isEmpty()) {

				Map<String, Object> a = new HashMap<String, Object>();
				a = (Map<String, Object>) returnCo2Grid.get(0);
				if (a.get("TOTAL") != null) {

					co2Total = DecimalUtil.ConvertNumberToDouble(a.get("TOTAL"));
				}

			}

		} else if (CommonConstants.DateType.QUARTERLY.getCode().equals(
				searchDateType)) {
			if (!returnEnergyGrid.isEmpty()) {

				Map<String, Object> a = new HashMap<String, Object>();
				a = (Map<String, Object>) returnEnergyGrid.get(0);
				if (a.get("TOTAL") != null) {

					total = DecimalUtil.ConvertNumberToDouble(a.get("TOTAL"));
				}

			}

			if (!returnCo2Grid.isEmpty()) {

				Map<String, Object> a = new HashMap<String, Object>();
				a = (Map<String, Object>) returnCo2Grid.get(0);
				if (a.get("TOTAL") != null) {

					co2Total = DecimalUtil.ConvertNumberToDouble(a.get("TOTAL"));
				}

			}

		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("totalUse", total);
		result.put("totalCo2Use", co2Total);
		result.put("averageUsage", averageUsage);
		result.put("averageCo2Usage", averageCo2Usage);
		System.out.println("%%%%%%%%%%:" + result);
		return result;
	}

	/*
	 * supplierId를 이용하여 location테이블에서 Root 키를 가져옴. 검침량 조회시 필요함.
	 */
	public Map<String, Object> getRootLocationId(Map<String, Object> condition) {

		List<Object> returnLocation = new ArrayList<Object>();

		try {

			String supplierId = StringUtils.defaultIfEmpty((String) condition
					.get("supplierId"), "0");

			Map<String, Object> condition1 = new HashMap<String, Object>();
			condition1.put("supplierId", supplierId);

			returnLocation = dayGmDao.getRootLocationId(condition1);

		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("rootLocation", returnLocation);

		return result;
	}

	/*
	 * 빌딩(동/층) 가스 사용량 Max 페이지
	 */
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	public Map<String, Object> getBuildingLookUpMaxGm(Map<String, Object> condition) {

		List<Object> returnEnergyGrid = new ArrayList<Object>();
		List<Object> returnCo2Grid = new ArrayList<Object>();

		// 가스사용량/탄소배출량
		List<HashMap<String, Object>> returnGmCo2LocationDay = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> returnGmCo2LocationDayInfo = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> returnGmCo2LocationWeek = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> returnGmCo2LocationWeekInfo = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> returnGmCo2LocationMonth = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> returnGmCo2LocationMonthInfo = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> returnGmCo2LocationQuarter = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> returnGmCo2LocationQuarterInfo = new ArrayList<HashMap<String, Object>>();

		List<Object> returnLocation = new ArrayList<Object>();

		try {
			String searchDateType = (String) condition.get("searchDateType");
			Integer supplierId = Integer.parseInt(StringUtils.defaultIfEmpty(
					(String) condition.get("supplierId"), "0"));
			Integer locationId = (Integer) ObjectUtils.defaultIfNull(condition
					.get("locationId"), 0);// location 그룹 이동 selectbox에서 선택한
			// locationId
			Integer detailLocationId = (Integer) ObjectUtils.defaultIfNull(
					condition.get("detailLocationId"), 0); // 마우스로 해당 동/층/호를
			// 클릭했을때의 해당
			// locationId

			if (0 != supplierId && 0 == locationId && 0 == detailLocationId) {

				HashMap initLocationIdMap = (HashMap) getRootLocationId(condition);
				ArrayList a = (ArrayList) initLocationIdMap.get("rootLocation");
				HashMap b = (HashMap) a.get(0);
				locationId = DecimalUtil.ConvertNumberToInteger(b.get("ID"));
			}

			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMM");
			SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");
			Calendar c = Calendar.getInstance();
			Calendar monthCalendar = Calendar.getInstance();
			Integer detailLocationIdTemp = 0;
			if (0 < detailLocationId) {// bar차트에서 특정 bar를 클릭한경우.

				detailLocationIdTemp = detailLocationId;
			} else {

				detailLocationIdTemp = locationId;
			}

			Map<String, Object> conditionDayLocation = new HashMap<String, Object>();
			conditionDayLocation.put("searchDateType",
					CommonConstants.DateType.DAILY.getCode());
			conditionDayLocation.put("detailLocationId", detailLocationId);
			conditionDayLocation.put("supplierId", supplierId);
			conditionDayLocation.put("startDate", formatter2
					.format(c.getTime()));
			conditionDayLocation.put("endDate", formatter2.format(c.getTime()));
			conditionDayLocation.put("channel", DefaultChannel.Usage.getCode());
			conditionDayLocation.put("meterType",
					CommonConstants.MeterType.GasMeter.getDayTableName());

			Map<String, Object> conditionDay = new HashMap<String, Object>();
			conditionDay.put("searchDateType", CommonConstants.DateType.DAILY
					.getCode());
			conditionDay.put("detailLocationId", detailLocationId);
			conditionDay.put("supplierId", supplierId);
			conditionDay.put("locationId", detailLocationIdTemp);
			conditionDay.put("startDate", formatter2.format(c.getTime()));
			conditionDay.put("endDate", formatter2.format(c.getTime()));

			Map<String, Object> conditionWeek = new HashMap<String, Object>();
			conditionWeek.put("searchDateType", CommonConstants.DateType.WEEKLY
					.getCode());
			conditionWeek.put("detailLocationId", detailLocationId);
			conditionWeek.put("supplierId", supplierId);
			conditionWeek.put("locationId", detailLocationIdTemp);
			c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			conditionWeek.put("startDate", formatter2.format(c.getTime()));
			c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			conditionWeek.put("endDate", formatter2.format(c.getTime()));

			Map<String, Object> conditionMonth = new HashMap<String, Object>();
			conditionMonth.put("searchDateType",
					CommonConstants.DateType.MONTHLY.getCode());
			conditionMonth.put("detailLocationId", detailLocationId);
			conditionMonth.put("supplierId", supplierId);
			conditionMonth.put("locationId", detailLocationIdTemp);

			monthCalendar.set(monthCalendar.get(Calendar.YEAR), 0, 1);
			conditionMonth.put("startDate", formatter1.format(monthCalendar
					.getTime()));
			monthCalendar.set(monthCalendar.get(Calendar.YEAR), 11,
					monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			conditionMonth.put("endDate", formatter1.format(monthCalendar
					.getTime()));

			Map<String, Object> conditionYear = new HashMap<String, Object>();
			conditionYear.put("searchDateType",
					CommonConstants.DateType.QUARTERLY.getCode());
			conditionYear.put("detailLocationId", detailLocationId);
			conditionYear.put("supplierId", supplierId);
			conditionYear.put("locationId", detailLocationIdTemp);
			monthCalendar.set(monthCalendar.get(Calendar.YEAR), 0, 1);
			conditionYear.put("startDate", formatter1.format(monthCalendar
					.getTime()));
			monthCalendar.set(monthCalendar.get(Calendar.YEAR), 11,
					monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			conditionYear.put("endDate", formatter1.format(monthCalendar
					.getTime()));

			// 0. location 리스트 조회
			returnLocation = thisLocationsList(condition);

			// 1. 동 그룹/층 그룹/호 그룹 에 대한 사용량 조회
			// location 정보가 층 레벨까지 내려간 경우 층에서 다시 하위 레벨은 존재하지 않으므로 상위 location
			// list가 보여지도록함.
			Integer locationIdTemp = 0;
			if (3 == thisLocationLevel(locationId)) {

				Location location = locationDao.get(locationId);

				if (location != null) {

					locationIdTemp = location.getParent().getId();
				}
			} else {

				locationIdTemp = locationId;
			}

			// Max 가젯에서는 주기 관련 선택 UI가 없어서 일 주기를 보여주게 하였음.
			conditionDayLocation.put("locationId", locationIdTemp);

			returnEnergyGrid = dayGmDao
					.getConsumptionGmCo2DayMonitoringParentId(conditionDayLocation);
			conditionDayLocation.put("channel", DefaultChannel.Co2.getCode());
			returnCo2Grid = dayGmDao
					.getConsumptionGmCo2DayMonitoringParentId(conditionDayLocation);
			// 2. 주기별 사용량 조회
			// 일(시간)
			returnGmCo2LocationDay = gmCo2MonitoringSumMinMaxDay(conditionDay);
			returnGmCo2LocationDayInfo = gmCo2MonitoringSumMinMaxDayInfo(conditionDay);
			// 주
			returnGmCo2LocationWeek = gmCo2MonitoringSumMinMaxWeek(conditionWeek);
			returnGmCo2LocationWeekInfo = gmCo2MonitoringSumMinMaxWeekInfo(conditionWeek);
			// 월
			returnGmCo2LocationMonth = gmCo2MonitoringSumMinMaxMonth(conditionMonth);
			returnGmCo2LocationMonthInfo = gmCo2MonitoringSumMinMaxMonthInfo(conditionMonth);
			// 분기
			returnGmCo2LocationQuarter = gmCo2MonitoringSumMinMaxQuarter(conditionYear);
			returnGmCo2LocationQuarterInfo = gmCo2MonitoringSumMinMaxQuarterInfo(conditionYear);

		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Object> retGrid = new ArrayList<Object>();
		Map<String, Object> map = new HashMap<String, Object>();

		for (int i = 0; i < returnEnergyGrid.size(); i++) {
			Map<String, Object> tmp = (Map<String, Object>) returnEnergyGrid
					.get(i);
			if (map.containsKey((String) tmp.get("NAME"))) {
				mapSum((Map<String, Object>) map.get((String) tmp.get("NAME")),
						tmp, "TOTAL");
			} else {
				map.put((String) tmp.get("NAME"), tmp);
				retGrid.add(tmp);
			}

		}

		Map<String, Object> result = new HashMap<String, Object>();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String currDateTime = formatter.format(new Date());
		result.put("currentDateTime", currDateTime);

		result.put("myChartDataLocation", retGrid); // 동별 가스/탄소 값의 합.
		result.put("myChartDataDay", returnGmCo2LocationDay);
		result.put("myChartDataDayInfo", returnGmCo2LocationDayInfo);
		result.put("myChartDataWeek", returnGmCo2LocationWeek);
		result.put("myChartDataWeekInfo", returnGmCo2LocationWeekInfo);
		result.put("myChartDataMonth", returnGmCo2LocationMonth);
		result.put("myChartDataMonthInfo", returnGmCo2LocationMonthInfo);
		result.put("myChartDataQuarter", returnGmCo2LocationQuarter);
		result.put("myChartDataQuarterInfo", returnGmCo2LocationQuarterInfo);
		result.put("returnLocation", returnLocation); // 동정보

		return result;
	}

	/*
	 * 빌딩(동/층) 가스 사용량 Min 페이지
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getBuildingLookUpGm(Map<String, Object> condition) {

		List<Object> returnLocation = new ArrayList<Object>();

		List<Object> returnEnergyGrid = new ArrayList<Object>();
		List<Object> returnCo2Grid = new ArrayList<Object>();

		// 가스사용량/탄소배출량
		List<HashMap<String, Object>> returnGmCo2LocationSumGrid = new ArrayList<HashMap<String, Object>>();

		// 온습도 temperature and humidity
		List<HashMap<String, Object>> returnTmHmLocationSumGrid = new ArrayList<HashMap<String, Object>>();

		try {

			String searchDateType = (String) condition.get("searchDateType");
			Integer supplierId = Integer.parseInt(StringUtils.defaultIfEmpty(
					(String) condition.get("supplierId"), "0"));
			Integer locationId = (Integer) ObjectUtils.defaultIfNull(condition
					.get("locationId"), 0);// location 그룹 이동 selectbox에서 선택한
			// locationId
			Integer detailLocationId = (Integer) ObjectUtils.defaultIfNull(
					condition.get("detailLocationId"), 0); // 마우스로 해당 동/층/호를
			// 클릭했을때의 해당
			// locationId

			if (0 != supplierId && 0 == locationId && 0 == detailLocationId) {

				HashMap initLocationIdMap = (HashMap) getRootLocationId(condition);
				ArrayList a = (ArrayList) initLocationIdMap.get("rootLocation");
				HashMap b = (HashMap) a.get(0);
				locationId = Integer.parseInt(b.get("ID").toString());
			}

			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMM");
			SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMdd");
			Calendar c = Calendar.getInstance();
			Calendar monthCalendar = Calendar.getInstance();
			System.out
					.println("##############################2010.07.07일 셋팅! TEST 가 끝나면 지울것!![getBuildingLookUpGm]");
			// c.set(2010, 6, 7); // 2010.07.07일 셋팅! TEST 가 끝나면 지울것!!

			Map<String, Object> conditionDay = new HashMap<String, Object>();
			conditionDay.put("searchDateType", CommonConstants.DateType.DAILY
					.getCode());
			conditionDay.put("detailLocationId", detailLocationId);
			conditionDay.put("supplierId", supplierId);
			// conditionDay.put("locationId" , locationId);
			conditionDay.put("startDate", formatter2.format(c.getTime()));
			conditionDay.put("endDate", formatter2.format(c.getTime()));

			conditionDay.put("channel", DefaultChannel.Usage.getCode());
			conditionDay.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayTableName());

			Map<String, Object> conditionWeek = new HashMap<String, Object>();
			conditionWeek.put("searchDateType", CommonConstants.DateType.WEEKLY
					.getCode());
			conditionWeek.put("detailLocationId", detailLocationId);
			conditionWeek.put("supplierId", supplierId);
			// conditionWeek.put("locationId" , locationId);
			c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			conditionWeek.put("startDate", formatter2.format(c.getTime()));
			c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			conditionWeek.put("endDate", formatter2.format(c.getTime()));
			conditionWeek.put("channel", DefaultChannel.Usage.getCode());
			conditionWeek.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayTableName());

			Map<String, Object> conditionMonth = new HashMap<String, Object>();
			conditionMonth.put("searchDateType",
					CommonConstants.DateType.MONTHLY.getCode());
			conditionMonth.put("detailLocationId", detailLocationId);
			conditionMonth.put("supplierId", supplierId);
			// conditionMonth.put("locationId" , locationId);
			// c.set(c.get(Calendar.YEAR), 0, 1);
			// conditionMonth.put("startDate", formatter1.format(c.getTime()));
			// c.set(c.get(Calendar.YEAR), 11, c
			// .getActualMaximum(Calendar.DAY_OF_MONTH));
			monthCalendar.set(monthCalendar.get(Calendar.YEAR), 0, 1);
			conditionMonth.put("startDate", formatter1.format(monthCalendar
					.getTime()));
			monthCalendar.set(monthCalendar.get(Calendar.YEAR), 11,
					monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			conditionMonth.put("endDate", formatter1.format(monthCalendar
					.getTime()));
			conditionMonth.put("channel", DefaultChannel.Usage.getCode());
			conditionMonth.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthTableName());

			Map<String, Object> conditionYear = new HashMap<String, Object>();
			conditionYear.put("searchDateType",
					CommonConstants.DateType.QUARTERLY.getCode());
			conditionYear.put("detailLocationId", detailLocationId);
			conditionYear.put("supplierId", supplierId);
			// conditionYear.put("locationId" , locationId);
			monthCalendar.set(monthCalendar.get(Calendar.YEAR), 0, 1);
			conditionYear.put("startDate", formatter1.format(monthCalendar
					.getTime()));
			monthCalendar.set(monthCalendar.get(Calendar.YEAR), 11,
					monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			conditionYear.put("endDate", formatter1.format(monthCalendar
					.getTime()));
			conditionYear.put("channel", DefaultChannel.Usage.getCode());
			conditionYear.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthTableName());

			// 0. location 리스트 조회
			returnLocation = thisLocationsList(condition);

			// 1. 동 그룹/층 그룹/호 그룹 에 대한 사용량 조회
			// location 정보가 층 레벨까지 내려간 경우 층에서 다시 하위 레벨은 존재하지 않으므로 상위 location
			// list가 보여지도록함.
			Integer locationIdTemp = 0;
			if (3 == thisLocationLevel(locationId)) {

				Location location = locationDao.get(locationId);

				if (location != null) {

					locationIdTemp = location.getParent().getId();
				}
			} else {

				locationIdTemp = locationId;
			}

			if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {

				conditionDay.put("locationId", locationIdTemp);
				returnEnergyGrid = dayGmDao
						.getConsumptionGmCo2DayMonitoringParentId(conditionDay);
				conditionDay.put("channel", DefaultChannel.Co2.getCode());
				returnCo2Grid = dayGmDao
						.getConsumptionGmCo2DayMonitoringParentId(conditionDay);

			} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
					searchDateType)) {

				conditionWeek.put("locationId", locationIdTemp);
				returnEnergyGrid = dayGmDao
						.getConsumptionGmCo2DayMonitoringParentId(conditionWeek);
				conditionWeek.put("channel", DefaultChannel.Co2.getCode());
				returnCo2Grid = dayGmDao
						.getConsumptionGmCo2DayMonitoringParentId(conditionWeek);

			} else if (CommonConstants.DateType.MONTHLY.getCode().equals(
					searchDateType)) {

				conditionMonth.put("locationId", locationIdTemp);
				returnEnergyGrid = monthGMDao
						.getConsumptionGmCo2MonthMonitoringParentId(conditionMonth);
				conditionMonth.put("channel", DefaultChannel.Co2.getCode());
				returnCo2Grid = monthGMDao
						.getConsumptionGmCo2MonthMonitoringParentId(conditionMonth);
			} else if (CommonConstants.DateType.QUARTERLY.getCode().equals(
					searchDateType)) {

				conditionYear.put("locationId", locationIdTemp);
				returnEnergyGrid = monthGMDao
						.getConsumptionGmCo2MonthMonitoringParentId(conditionYear);
				conditionYear.put("channel", DefaultChannel.Co2.getCode());
				returnCo2Grid = monthGMDao
						.getConsumptionGmCo2MonthMonitoringParentId(conditionYear);
			} else {

				conditionDay.put("locationId", locationIdTemp);
				returnEnergyGrid = dayGmDao
						.getConsumptionGmCo2DayMonitoringParentId(conditionDay);
				conditionDay.put("channel", DefaultChannel.Co2.getCode());
				returnCo2Grid = dayGmDao
						.getConsumptionGmCo2DayMonitoringParentId(conditionDay);
			}

			// 2. 주기별 사용량 조회
			Integer detailLocationIdTemp = 0;
			if (0 < detailLocationId) {// bar차트에서 특정 bar를 클릭한경우.

				detailLocationIdTemp = detailLocationId;
			} else {

				detailLocationIdTemp = locationId;
			}
			// 일(시간)
			if (CommonConstants.DateType.DAILY.getCode().equals(searchDateType)) {

				conditionDay.put("locationId", detailLocationIdTemp);

				// 빌딩 전체 가스사용량/탄솝출량 의 최대 ,최소,합
				returnGmCo2LocationSumGrid = gmCo2MonitoringSumMinMaxDay(conditionDay);

				// 빌딩 전체 온도 / 습도 의 시간별 변화량.
				returnTmHmLocationSumGrid = tmHmMonitoringMinMaxDay(conditionDay);

				// 주
			} else if (CommonConstants.DateType.WEEKLY.getCode().equals(
					searchDateType)) {

				conditionWeek.put("locationId", detailLocationIdTemp);

				// 빌딩 전체 가스사용량/탄솝출량 의 최대 ,최소,합
				returnGmCo2LocationSumGrid = gmCo2MonitoringSumMinMaxWeek(conditionWeek);

				// 빌딩 전체 온도 / 습도 의 최대 ,최소
				returnTmHmLocationSumGrid = tmHmMonitoringMinMaxWeek(conditionWeek);

				// 월
			} else if (CommonConstants.DateType.MONTHLY.getCode().equals(
					searchDateType)) {

				conditionMonth.put("locationId", detailLocationIdTemp);

				// 빌딩 전체 가스사용량/탄솝출량 의 최대 ,최소,합
				returnGmCo2LocationSumGrid = gmCo2MonitoringSumMinMaxMonth(conditionMonth);

				// 빌딩 전체 온도 / 습도 의 최대 ,최소
				returnTmHmLocationSumGrid = tmHmMonitoringMinMaxMonth(conditionMonth);
				// 분기
			} else if (CommonConstants.DateType.QUARTERLY.getCode().equals(
					searchDateType)) {

				conditionYear.put("locationId", detailLocationIdTemp);

				// 빌딩 전체 가스사용량/탄솝출량 의 최대 ,최소,합
				returnGmCo2LocationSumGrid = gmCo2MonitoringSumMinMaxQuarter(conditionYear);

				// 빌딩 전체 온도 / 습도 의 최대 ,최소
				returnTmHmLocationSumGrid = tmHmMonitoringMinMaxQuarter(conditionYear);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Object> retGrid = new ArrayList<Object>();

		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < returnEnergyGrid.size(); i++) {
			Map<String, Object> tmp = (Map<String, Object>) returnEnergyGrid
					.get(i);
			if (map.containsKey((String) tmp.get("NAME"))) {
				mapSum((Map<String, Object>) map.get((String) tmp.get("NAME")),
						tmp, "TOTAL");
			} else {
				map.put((String) tmp.get("NAME"), tmp);
				retGrid.add(tmp);
			}

		}
		for (int i = 0; i < returnCo2Grid.size(); i++) {
			Map<String, Object> tmp = (Map<String, Object>) returnCo2Grid
					.get(i);
			if (map.containsKey((String) tmp.get("NAME"))) {
				mapSum((Map<String, Object>) map.get((String) tmp.get("NAME")),
						tmp, "CO2_TOTAL");
			} else {
				map.put((String) tmp.get("NAME"), tmp);
				retGrid.add(tmp);
			}

		}

		Map<String, Object> result = new HashMap<String, Object>();
		if (!returnTmHmLocationSumGrid.isEmpty()) {
			HashMap<String, Object> tmMinMax = returnTmHmLocationSumGrid
					.get(returnTmHmLocationSumGrid.size() - 1);
			result.put("TM_MAX", tmMinMax.get("TM_MAX"));
			result.put("TM_MIN", tmMinMax.get("TM_MIN"));
		} else {
			result.put("TM_MAX", 0);
			result.put("TM_MIN", 0);
		}

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String currDateTime = formatter.format(new Date());
		result.put("currentDateTime", currDateTime);

		result.put("grid", retGrid); // 동별 가스/탄소 값의 합.
		result.put("sumGrid", returnGmCo2LocationSumGrid); // 빌딩 전체 가스사용량/탄솝출량 의
		// 최대값 ,최소값,합
		result.put("sumTHGrid", returnTmHmLocationSumGrid); // 빌딩 전체 가스온도량/습도량 의
		// 합
		result.put("returnLocation", returnLocation); // 동정보

		return result;
	}

	private void mapSum(Map<String, Object> map1, Map<String, Object> map2,
			String key) {
		Double total1 = DecimalUtil.ConvertNumberToDouble(map1.get("TOTAL"));

		Double total2 = DecimalUtil.ConvertNumberToDouble(map2.get("TOTAL"));

		map1.put(key, total1 + total2);

	}

	/**
	 * 빌딩의 동/층정보 리스트 . 건물 / 동 / 층
	 * 
	 * @param condition
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    private List<Object> thisLocationsList(Map<String, Object> condition) {

		List<Object> returnLocation = new ArrayList<Object>();

		Integer supplierId = Integer.parseInt(StringUtils.defaultIfEmpty(
				(String) condition.get("supplierId"), "0"));
		Integer locationId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("locationId"), 0);// location 그룹 이동 selectbox에서 선택한
		// locationId
		Integer detailLocationId = (Integer) ObjectUtils.defaultIfNull(
				condition.get("detailLocationId"), 0); // 마우스로 해당 동/층/호를 클릭했을때의
		// 해당 locationId

		if (0 != supplierId && 0 == locationId && 0 == detailLocationId) {

			HashMap initLocationIdMap = (HashMap) this
					.getRootLocationId(condition);
			ArrayList a = (ArrayList) initLocationIdMap.get("rootLocation");
			HashMap b = (HashMap) a.get(0);
			locationId = Integer.parseInt(b.get("ID").toString());
		}

		Map<String, Object> locationParentKeyMap = new HashMap<String, Object>();
		locationParentKeyMap = this.getRootLocationId(condition);

		Integer locationRootId = 0;
		if (!locationParentKeyMap.isEmpty()) {

			ArrayList a = (ArrayList) locationParentKeyMap.get("rootLocation");
			HashMap b = (HashMap) a.get(0);
			locationRootId = Integer.parseInt(b.get("ID").toString());		
		}

		// 1. 최상위 레벨의 정보는 무조건 노출시킴.
		Location rootLocation = locationDao.get(locationRootId);

		HashMap rootLocationInfo = new HashMap();

		rootLocationInfo.put("label", rootLocation.getName());
		rootLocationInfo.put("data", rootLocation.getId());

		returnLocation.add(rootLocationInfo);

		// 2. 빌딩의 동/층정보 리스트 추가
		List<Location> childrenLocationTemp = new ArrayList<Location>();
		int thisLocationLevel = thisLocationLevel(locationId);

		if (0 == thisLocationLevel) {// 빌딩 레벨이면

			// 동 리스트 추가
			childrenLocationTemp = locationDao.getChildren(locationId);

		} else if (1 == thisLocationLevel) {// 동 레벨이면

			Location thisLocation = locationDao.get(locationId);

			HashMap thisLocationInfo = new HashMap();
			thisLocationInfo.put("label", thisLocation.getName());
			thisLocationInfo.put("data", thisLocation.getId());
			// 동정보 추가
			returnLocation.add(thisLocationInfo);
			// 층 리스트 추가
			childrenLocationTemp = locationDao.getChildren(locationId);

		} else if (2 == thisLocationLevel) {// 층 레벨이면

			// 층정보 추가
			Location thisLocation1 = locationDao.get(locationId);

			HashMap thisLocationInfo1 = new HashMap();
			thisLocationInfo1.put("label", thisLocation1.getParent().getName());
			thisLocationInfo1.put("data", thisLocation1.getParent().getId());

			returnLocation.add(thisLocationInfo1);

			// 호 리스트 추가
			childrenLocationTemp = locationDao.getChildren(thisLocation1
					.getParent().getId());

		} else if (3 == thisLocationLevel) { // 호(room) 레벨이면

			// 층 리스트 추가
			childrenLocationTemp = locationDao.getChildren(locationDao.get(
					locationId).getParent().getId());
		}

		for (int i = 0; i < childrenLocationTemp.size(); i++) {

			Location l = childrenLocationTemp.get(i);

			HashMap a = new HashMap();
			a.put("label", l.getName());
			a.put("data", l.getId());

			returnLocation.add(a);
		}

		return returnLocation;
	}

	/**
	 * 현재의 키정보를 이용하여 해당 id의 레벨이 층보다 하위레벨의 정보인지 첵크.
	 * 
	 * @param id
	 *            : 조회할 키
	 * @return
	 */
	@SuppressWarnings("unused")
    private boolean myParentIsRoot(int id) {

		boolean returnBool = false;

		Location level1 = locationDao.get(id);
		if (level1 != null) {

			if (level1.getParent() == null) {

				returnBool = true;
			} else if ((level1.getParent()).getParent() == null) {

				returnBool = true;
			}
		}

		return returnBool;
	}

	/**
	 * 현재의 키정보를 이용하여 해당 id의 레벨을 반환.
	 * 
	 * @param id
	 *            : 조회할 키
	 * @return
	 */
	public int thisLocationLevel(int locationId) {

		int thisLevel = 0;

		Location level1 = locationDao.get(locationId);

		if (null == level1.getParent()) {

			thisLevel = 0;
		} else if (null == (level1.getParent()).getParent()) {

			thisLevel = 1;
		} else if (null == ((level1.getParent()).getParent()).getParent()) {

			thisLevel = 2;
		}

		return thisLevel;
	}

	@SuppressWarnings("unused")
    public int thisLocationLevelMap(Map<String, Object> condition) {

		Integer supplierId = Integer.parseInt(StringUtils.defaultIfEmpty(
				(String) condition.get("supplierId"), "0"));
		Integer locationId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("locationId"), 0);

		// if( 0 == locationId ) {
		//			
		// ArrayList<Location> rootLocations = (ArrayList<Location>)
		// locationDao.getParents(supplierId);
		//			
		// if( rootLocations != null ){
		// Location rootLocation = rootLocations.get(0);
		// locationId = rootLocation.getId();
		// }
		// }

		int thisLevel = 0;

		Location level1 = locationDao.get(locationId);

		if (null == level1.getParent()) {

			thisLevel = 0;
		} else if (null == (level1.getParent()).getParent()) {

			thisLevel = 1;
		} else if (null == ((level1.getParent()).getParent()).getParent()) {

			thisLevel = 2;
		}

		return thisLevel;
	}

	/**
	 * 일(시간) 빌딩 전체 : 가스사용량/탄솝출량 사용량 정보 금일 일사용량 , 평균요금 , 최소사용량 , 최대사용량
	 * 
	 * @param condition
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
    private List<HashMap<String, Object>> gmCo2MonitoringSumMinMaxDayInfo(
			Map<String, Object> condition) {

		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		double total = 0; // 일 사용량
		double avgCharge = 0; // 평균요금
		double sumCharge = 0; // 요금 합
		int minUseTime = 0; // 최소사용량 시간
		int maxUseTime = 0; // 최대 사용량 시간
		double minUse = 0; // 최소사용량
		double maxUse = 0; // 최대 사용량

		String searchDateType = (String) condition.get("searchDateType"); // 일 ,
		// 주
		// ,
		// 월
		// ,
		// 분기
		Integer supplierId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("supplierId"), 0);
		Integer locationId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("locationId"), 0);// location 그룹 이동 selectbox에서 선택한
		// locationId
		Integer detailLocationId = (Integer) ObjectUtils.defaultIfNull(
				condition.get("detailLocationId"), 0); // 마우스로 해당 동/층/호를 클릭했을때의
		// 해당 locationId
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");

		if (0 < detailLocationId) {

			locationId = detailLocationId;
		}

		Map<String, Object> conditionDayInfo = new HashMap<String, Object>(); // 전일
		// 정보
		// 셋팅
		conditionDayInfo.put("searchDateType", searchDateType);
		conditionDayInfo.put("supplierId", supplierId);
		conditionDayInfo.put("locationId", locationId);
		conditionDayInfo.put("startDate", startDate);
		conditionDayInfo.put("endDate", endDate);

		List<Object> returnGmCo2LocationSumGridTemp = new ArrayList<Object>();

		/*************************
		 * @author cmyang 2010. 10. 20
		 * 
		 *         선택된 Node에 검침 데이터가 없으면, Children Node 조회로 수정 (기존 Children
		 *         Node가 있으면 무조건 Children Node의 검침 데이터를 조회)
		 */
		returnGmCo2LocationSumGridTemp = dayGmDao
				.getConsumptionGmCo2MonitoringSumMinMaxLocationId(conditionDayInfo);
		if (returnGmCo2LocationSumGridTemp.isEmpty()) {
			Set<Location> childrenLocationSet = locationDao.get(locationId)
					.getChildren();

			if (childrenLocationSet != null && !childrenLocationSet.isEmpty()) {
				returnGmCo2LocationSumGridTemp = dayGmDao
						.getConsumptionGmCo2MonitoringSumMinMaxPrentId(conditionDayInfo);
			}
		}
		/*************************/

		if (!returnGmCo2LocationSumGridTemp.isEmpty()) {

			for (int i = 0; i < returnGmCo2LocationSumGridTemp.size(); i++) {

				HashMap<String, Object> temp = (HashMap<String, Object>) returnGmCo2LocationSumGridTemp
						.get(i);

				for (int k = 0; k < 24; k++) {

					String setTime = StringUtils.leftPad(String.valueOf(k), 2,
							"0");
					double check = Double.valueOf(ObjectUtils.defaultIfNull(
							temp.get("GM_SUM_" + setTime), "0").toString());

					if (k == 0) {
						minUse = check;
						minUseTime = k;
					}

					if (maxUse < check) {
						maxUse = check;
						maxUseTime = k;
					} else if (minUse > check) {
						minUse = check;
						minUseTime = k;
					}
					BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
					Map<String, Object> chargeMap = new HashMap<String, Object>();
					chargeMap.put("serviceType", MeterType.GasMeter
							.getServiceType());
					chargeMap.put("dateType", DateType.DAILY.getCode());
					chargeMap.put("usage", check);
					chargeMap.put("period", 1);

					sumCharge = sumCharge + bemsUtil.getUsageCharge(chargeMap); // 사용요금
					// sumCharge = sumCharge + 사용요금; // 사용요금 작업 필요함.
				}
				avgCharge = sumCharge / 24;

				NumberFormat formatter = new DecimalFormat("###,###,###,###.##");

				HashMap<String, Object> setTemp = new HashMap<String, Object>();

				double totalValue = DecimalUtil.ConvertNumberToDouble(temp.get("GM_TOTAL"));
				setTemp.put("INFOTOTAL", formatter.format(totalValue) + " ㎥");
				setTemp.put("INFOCO2TOTAL", null);
				setTemp.put("INFOCAVGCHARGE", formatter.format(avgCharge)); // 원
				setTemp.put("INFOMAXUSETIME", maxUseTime); // 시
				setTemp.put("INFOMAXUSE", formatter.format(maxUse) + " ㎥");
				setTemp.put("INFOMINUSETIME", minUseTime);
				setTemp.put("INFOMINUSE", formatter.format(minUse) + " ㎥");

				result.add(i, setTemp);
			}
		}

		return result;

	}

	/**
	 * 일(시간) 빌딩 전체 : 가스사용량/탄솝출량 의 최대 ,최소,합 금일 / 전일 데이터 조회
	 * 
	 * @param condition
	 * @return
	 */
	@SuppressWarnings({ "unused", "unchecked" })
    private List<HashMap<String, Object>> gmCo2MonitoringSumMinMaxDay(Map<String, Object> condition) {

		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		String searchDateType = (String) condition.get("searchDateType"); // 일 ,
		// 주
		// ,
		// 월
		// ,
		// 분기
		Integer supplierId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("supplierId"), 0);
		Integer locationId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("locationId"), 0);// location 그룹 이동 selectbox에서 선택한
		// locationId
		Integer detailLocationId = (Integer) ObjectUtils.defaultIfNull(
				condition.get("detailLocationId"), 0); // 마우스로 해당 동/층/호를 클릭했을때의
		// 해당 locationId
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");

		if (0 < detailLocationId) {
			locationId = detailLocationId;
		}

		int y = Integer.parseInt(startDate.substring(0, 4));
		int m = Integer.parseInt(startDate.substring(4, 6));
		int d = Integer.parseInt(startDate.substring(6, 8));

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

		Calendar getOldDay = Calendar.getInstance();
		getOldDay.set(y, m - 1, d);
		getOldDay.set(y, m - 1, getOldDay.get(Calendar.DATE) - 1);
		String oldStartDate = formatter.format(getOldDay.getTime());
		// System.out.println( " 전일 날짜 : " + oldStartDate );

		Map<String, Object> conditionDay = new HashMap<String, Object>(); // 전일
		// 정보
		// 셋팅
		conditionDay.put("searchDateType", searchDateType);
		conditionDay.put("supplierId", supplierId);
		conditionDay.put("locationId", locationId);
		conditionDay.put("startDate", startDate);
		conditionDay.put("endDate", startDate);

		Map<String, Object> conditionOldDay = new HashMap<String, Object>(); // 전일
		// 정보
		// 셋팅
		conditionOldDay.put("searchDateType", searchDateType);
		conditionOldDay.put("supplierId", supplierId);
		conditionOldDay.put("locationId", locationId);
		conditionOldDay.put("startDate", oldStartDate);
		conditionOldDay.put("endDate", oldStartDate);

		List<Object> returnGmCo2LocationSumGridTemp = new ArrayList<Object>();
		List<Object> returnGmCo2LocationSumGridTempOld = new ArrayList<Object>();

		/*************************
		 * @author cmyang 2010. 10. 20
		 * 
		 *         선택된 Node에 검침 데이터가 없으면, Children Node 조회로 수정 (기존 Children
		 *         Node가 있으면 무조건 Children Node의 검침 데이터를 조회)
		 */
		List<Integer> parent = locationDao.getParentId(detailLocationId);

		if (!parent.isEmpty()) {
			returnGmCo2LocationSumGridTemp = dayGmDao
					.getConsumptionGmCo2MonitoringSumMinMaxLocationId(conditionDay);
			returnGmCo2LocationSumGridTempOld = dayGmDao
					.getConsumptionGmCo2MonitoringSumMinMaxLocationId(conditionOldDay);
		}
		if (returnGmCo2LocationSumGridTemp.isEmpty()) {
			Set<Location> childrenLocationSet = locationDao.get(locationId)
					.getChildren();

			if (childrenLocationSet != null && !childrenLocationSet.isEmpty()) {
				returnGmCo2LocationSumGridTemp = dayGmDao
						.getConsumptionGmCo2MonitoringSumMinMaxPrentId(conditionDay);
				returnGmCo2LocationSumGridTempOld = dayGmDao
						.getConsumptionGmCo2MonitoringSumMinMaxPrentId(conditionOldDay);
			}
		}
		/*************************/

		if (!returnGmCo2LocationSumGridTemp.isEmpty()) {
			for (int i = 0; i < returnGmCo2LocationSumGridTemp.size(); i++) {
				HashMap<String, Object> temp = (HashMap<String, Object>) returnGmCo2LocationSumGridTemp
						.get(i);
				HashMap<String, Object> temp2 = new HashMap<String, Object>();
				if (!returnGmCo2LocationSumGridTempOld.isEmpty()) {

					temp2 = (HashMap<String, Object>) returnGmCo2LocationSumGridTempOld
							.get(i);
				}
				for (int k = 0; k < 24; k++) {
					String setTime = StringUtils.leftPad(String.valueOf(k), 2,
							"0");

					HashMap<String, Object> setTemp = new HashMap<String, Object>();
					setTemp.put("MYDATE", String.valueOf(k));
					setTemp.put("GMMIN", temp.get("GM_MIN_" + setTime));
					setTemp.put("GMMAX", temp.get("GM_MAX_" + setTime));
					setTemp.put("GMSUM", temp.get("GM_SUM_" + setTime));
					setTemp.put("CO2MIN", temp.get("CO2_MIN_" + setTime));
					setTemp.put("CO2MAX", temp.get("CO2_MAX_" + setTime));
					setTemp.put("CO2SUM", temp.get("CO2_SUM_" + setTime));

					if (!temp2.isEmpty()) {

						setTemp.put("OLDGMMIN", temp2.get("GM_MIN_" + setTime));
						setTemp.put("OLDGMMAX", temp2.get("GM_MAX_" + setTime));
						setTemp.put("OLDGMSUM", temp2.get("GM_SUM_" + setTime));
						setTemp.put("OLDCO2MIN", temp2
								.get("CO2_MIN_" + setTime));
						setTemp.put("OLDCO2MAX", temp2
								.get("CO2_MAX_" + setTime));
						setTemp.put("OLDCO2SUM", temp2
								.get("CO2_SUM_" + setTime));
					} else {

						setTemp.put("OLDGMMIN", null);
						setTemp.put("OLDGMMAX", null);
						setTemp.put("OLDGMSUM", null);
						setTemp.put("OLDCO2MIN", null);
						setTemp.put("OLDCO2MAX", null);
						setTemp.put("OLDCO2SUM", null);

					}

					result.add(k, setTemp);
				}
			}
		} else {
            HashMap<String, Object> setTemp = null;
            for (int k = 0; k < 24; k++) {
                setTemp = new HashMap<String, Object>();
                setTemp.put("MYDATE", String.valueOf(k));
                setTemp.put("GMMIN", 0);
                setTemp.put("GMMAX", 0);
                setTemp.put("GMSUM", 0);
                setTemp.put("CO2MIN", 0);
                setTemp.put("CO2MAX", 0);
                setTemp.put("CO2SUM", 0);
                setTemp.put("OLDGMMIN", 0);
                setTemp.put("OLDGMMAX", 0);
                setTemp.put("OLDGMSUM", 0);
                setTemp.put("OLDCO2MIN", 0);
                setTemp.put("OLDCO2MAX", 0);
                setTemp.put("OLDCO2SUM", 0);

                result.add(k, setTemp);
            }
		}

		return result;
	}

	/**
	 * 일(시간) 빌딩 전체 : 온도 값 건물전체에대한 온도 수치는 나타낼수 없어서 건물의 첫번째동에대한 온도정보를 보여주는것으로
	 * 대체함.(협의된 내용임.)
	 * 
	 * @param condition
	 * @return
	 */
	@SuppressWarnings("unchecked")
    protected List<HashMap<String, Object>> tmHmMonitoringMinMaxDay(Map<String, Object> condition) {

		String searchDateType = (String) condition.get("searchDateType"); // 일 ,
		// 주
		// ,
		// 월
		// ,
		// 분기
		Integer supplierId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("supplierId"), 0);
		Integer locationId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("locationId"), 0);// location 그룹 이동 selectbox에서 선택한
		// locationId
		Integer detailLocationId = (Integer) ObjectUtils.defaultIfNull(
				condition.get("detailLocationId"), 0); // 마우스로 해당 동/층/호를 클릭했을때의
		// 해당 locationId
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");

		if (0 < detailLocationId) {
			locationId = detailLocationId;
		}

		Map<String, Object> conditionDay = new HashMap<String, Object>();
		conditionDay.put("searchDateType", searchDateType);
		conditionDay.put("supplierId", supplierId);
		conditionDay.put("startDate", startDate);
		conditionDay.put("endDate", endDate);

		// 건물 전체에대한 온도 정보 표현시에는 첫번째 빌딩에대한 온도정보를 나타내도록 한다.
		if (0 == thisLocationLevel(locationId)) {

			if (0 == locationDao.getChildren(locationId).size()) {
				conditionDay.put("locationId", locationId);
			} else {
				Location targetLocation = locationDao.getChildren(locationId)
						.get(0);
				conditionDay.put("locationId", targetLocation.getId());
			}
		} else {
			conditionDay.put("locationId", locationId);
		}

		List<Object> locationList = meteringDayDao
				.getTemperatureHumidityLocation("DAY_TM");

		if (!locationList.isEmpty() && locationList.size() != 0) {
			Map<String, Object> map = (Map<String, Object>) locationList.get(0);

			conditionDay.put("locationId", DecimalUtil.ConvertNumberToInteger(map.get("LOCATIONID")));
		} else {
			conditionDay.put("locationId", locationId);
		}
		
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		List<Object> returnTMLocationTemp = dayTMDao
				.getConsumptionTmMonitoring(conditionDay);
		List<Object> returnHUMLocationTemp = dayHUMDao
				.getConsumptionHumMonitoring(conditionDay);
		if (!returnTMLocationTemp.isEmpty()) {

			for (int i = 0; i < returnTMLocationTemp.size(); i++) {

				HashMap<String, Object> tempTm = (HashMap<String, Object>) returnTMLocationTemp
						.get(i);
				HashMap<String, Object> tempHum = new HashMap<String, Object>();
				if (!returnHUMLocationTemp.isEmpty()) {
					tempHum = (HashMap<String, Object>) returnHUMLocationTemp
							.get(i);
				}
				for (int k = 0; k < 24; k++) {
					String setTime = StringUtils.leftPad(String.valueOf(k), 2,
							"0");

					HashMap<String, Object> setTemp = new HashMap<String, Object>();
					setTemp.put("MYDATE", String.valueOf(k));
					setTemp.put("TM", ObjectUtils.defaultIfNull(tempTm.get("TM_" + setTime),0));
					setTemp.put("TM_MAX", tempTm.get("TM_MAXVALUE"));
					setTemp.put("TM_MIN", tempTm.get("TM_MINVALUE"));
					setTemp.put("HUM", ObjectUtils.defaultIfNull(tempHum.get("HUM_" + setTime),0));

					result.add(k, setTemp);
				}
			}
		} else {
            HashMap<String, Object> setTemp = null;
            for (int k = 0; k < 24; k++) {
                setTemp = new HashMap<String, Object>();

                setTemp.put("MYDATE", String.valueOf(k));
                setTemp.put("TM", 0);
                setTemp.put("TM_MAX", 0);
                setTemp.put("TM_MIN", 0);
                setTemp.put("HUM", 0);

                result.add(k, setTemp);
            }
		}
		return result;
	}

	/**
	 * 일주일 : 빌딩 전체 가스사용량/탄솝출량 의 합 기본정보 금주 주간 사용량 합 , 평균사용요금 , 최소사용량 , 최대사용량.
	 * 
	 * @param condition
	 * @return
	 */
	@SuppressWarnings("unchecked")
    private List<HashMap<String, Object>> gmCo2MonitoringSumMinMaxWeekInfo(
			Map<String, Object> condition) {

		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		double total = 0; // 주간 사용량
		double avgCharge = 0; // 평균요금
		double sumCharge = 0; // 요금 합
		int minUseTime = 0; // 최소사용량 시간
		int maxUseTime = 0; // 최대 사용량 시간
		double minUse = 0; // 최소사용량
		double maxUse = 0; // 최대 사용량

		String searchDateType = (String) condition.get("searchDateType"); // 일 ,
		// 주
		// ,
		// 월
		// ,
		// 분기
		Integer supplierId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("supplierId"), 0);
		Integer locationId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("locationId"), 0);// location 그룹 이동 selectbox에서 선택한
		// locationId
		Integer detailLocationId = (Integer) ObjectUtils.defaultIfNull(
				condition.get("detailLocationId"), 0); // 마우스로 해당 동/층/호를 클릭했을때의
		// 해당 locationId
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");

		if (0 < detailLocationId) {// bar차트에서 특정 bar를 클릭한경우.

			locationId = detailLocationId;
		}

		Supplier supplier = supplierDao.get(supplierId);
		Language lang = supplier.getLang();
		
		Map<String, Object> conditionWeek = new HashMap<String, Object>(); // 전일
		// 정보
		// 셋팅
		conditionWeek.put("searchDateType", searchDateType);
		conditionWeek.put("supplierId", supplierId);
		conditionWeek.put("locationId", locationId);
		conditionWeek.put("startDate", startDate);
		conditionWeek.put("endDate", endDate);

		// List<Object> returnGmCo2LocationSumGridTemp =
		// dayGmDao.getConsumptionGmCo2WeekMonitoring( condition );
		List<Object> returnGmCo2LocationSumGridTemp = new ArrayList<Object>();

		/*************************
		 * @author cmyang 2010. 10. 20
		 * 
		 *         선택된 Node에 검침 데이터가 없으면, Children Node 조회로 수정 (기존 Children
		 *         Node가 있으면 무조건 Children Node의 검침 데이터를 조회)
		 */
		returnGmCo2LocationSumGridTemp = dayGmDao
				.getConsumptionGmCo2WeekMonitoringLocationId(conditionWeek);
		if (returnGmCo2LocationSumGridTemp.isEmpty()) {
			Set<Location> childrenLocationSet = locationDao.get(locationId)
					.getChildren();

			if (childrenLocationSet != null && !childrenLocationSet.isEmpty()) {
				returnGmCo2LocationSumGridTemp = dayGmDao
						.getConsumptionGmCo2WeekMonitoringParentId(conditionWeek);
			}
		}
		/*************************/

		if (!returnGmCo2LocationSumGridTemp.isEmpty()) {

			double check = 0.0;

			ArrayList<String> dayOfWeek = new ArrayList<String>();
			if (lang.getCode_2letter().equals("ko")) {
				dayOfWeek.add(0, WeekDay.Sunday.getKorName());
				dayOfWeek.add(1, WeekDay.Monday.getKorName());
				dayOfWeek.add(2, WeekDay.Tuesday.getKorName());
				dayOfWeek.add(3, WeekDay.Wednesday.getKorName());
				dayOfWeek.add(4, WeekDay.Thursday.getKorName());
				dayOfWeek.add(5, WeekDay.Friday.getKorName());
				dayOfWeek.add(6, WeekDay.Saturday.getKorName());
			} else {
				dayOfWeek.add(0, WeekDay.Sunday.getEngName());
				dayOfWeek.add(1, WeekDay.Monday.getEngName());
				dayOfWeek.add(2, WeekDay.Tuesday.getEngName());
				dayOfWeek.add(3, WeekDay.Wednesday.getEngName());
				dayOfWeek.add(4, WeekDay.Thursday.getEngName());
				dayOfWeek.add(5, WeekDay.Friday.getEngName());
				dayOfWeek.add(6, WeekDay.Saturday.getEngName());
			}

			for (int i = 0; i < 7; i++) {

				int k = 0;
				String cpstartWeek = CalendarUtil.getDateWithoutFormat(
						startDate, Calendar.DATE, i);
				String setDate = null;
				boolean isSearch = false;
				HashMap<String, Object> temp = new HashMap<String, Object>();

				for (int j = 0; j < returnGmCo2LocationSumGridTemp.size(); j++) {
					temp = (HashMap<String, Object>) returnGmCo2LocationSumGridTemp
							.get(j);
					setDate = temp.get("YYYYMMDD").toString();

					if (cpstartWeek.contains(setDate)) {
						isSearch = true;
						k = j;
					} else {
						continue;
					}
				}

				temp = (HashMap<String, Object>) returnGmCo2LocationSumGridTemp
						.get(k);
				
				if (isSearch) {

					check = Double.valueOf(ObjectUtils.defaultIfNull(
							temp.get("GM_TOTAL"), "0").toString());
				} else {
					check = 0.0;
				}

				if (i == 0) {
					minUse = check;
					minUseTime = i;
				}

				total = total + check;
				BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
				Map<String, Object> chargeMap = new HashMap<String, Object>();
				chargeMap.put("serviceType",
						MeterType.GasMeter.getServiceType());
				chargeMap.put("dateType", DateType.DAILY.getCode());
				chargeMap.put("usage", check);
				chargeMap.put("period", 7);

				sumCharge = sumCharge + bemsUtil.getUsageCharge(chargeMap); // 사용요금

				avgCharge = sumCharge / returnGmCo2LocationSumGridTemp.size();

				if (maxUse < check) {
					maxUse = check;
					maxUseTime = i;
				} else if (minUse > check) {
					minUse = check;
					minUseTime = i;
				}
			}
			
			NumberFormat formatter = new DecimalFormat("###,###,###,###.##");

			HashMap<String, Object> setTemp = new HashMap<String, Object>();

			setTemp.put("INFOTOTAL", formatter.format(total) + " kWh");
			setTemp.put("INFOCO2TOTAL", null);
			setTemp.put("INFOCAVGCHARGE", formatter.format(avgCharge)); // 원
			setTemp.put("INFOMAXUSETIME", dayOfWeek.get(maxUseTime)); // 요일
			setTemp.put("INFOMAXUSE", formatter.format(maxUse) + " kWh");
			setTemp.put("INFOMINUSETIME", dayOfWeek.get(minUseTime));
			setTemp.put("INFOMINUSE", formatter.format(minUse) + " kWh");

			result.add(0, setTemp);
		
		}

		return result;

	}

	/**
	 * 일주일 : 빌딩 전체 가스사용량/탄솝출량 의 합 금주 / 전주 데이터 조회
	 * 
	 * @param condition
	 * @return
	 */
	@SuppressWarnings("unchecked")
    private List<HashMap<String, Object>> gmCo2MonitoringSumMinMaxWeek(Map<String, Object> condition) {

		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		String searchDateType = (String) condition.get("searchDateType"); // 일 ,
		// 주
		// ,
		// 월
		// ,
		// 분기
		Integer supplierId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("supplierId"), 0);
		Integer locationId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("locationId"), 0);// location 그룹 이동 selectbox에서 선택한
		// locationId
		Integer detailLocationId = (Integer) ObjectUtils.defaultIfNull(
				condition.get("detailLocationId"), 0); // 마우스로 해당 동/층/호를 클릭했을때의
		// 해당 locationId
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");

		if (0 < detailLocationId) {// bar차트에서 특정 bar를 클릭한경우.
			locationId = detailLocationId;
		}

		Supplier supplier = supplierDao.get(supplierId);
		Language lang = supplier.getLang();
		
		String oldStartDate = CalendarUtil.getDateWithoutFormat(startDate,
				Calendar.DATE, -7);

		String oldEndDate = CalendarUtil.getDateWithoutFormat(endDate,
				Calendar.DATE, -7);

		Map<String, Object> conditionWeek = new HashMap<String, Object>(); // 전일
		// 정보
		// 셋팅
		conditionWeek.put("searchDateType", searchDateType);
		conditionWeek.put("supplierId", supplierId);
		conditionWeek.put("locationId", locationId);
		conditionWeek.put("startDate", startDate);
		conditionWeek.put("endDate", endDate);

		Map<String, Object> conditionOldWeek = new HashMap<String, Object>(); // 전일
		// 정보
		// 셋팅
		conditionOldWeek.put("searchDateType", searchDateType);
		conditionOldWeek.put("supplierId", supplierId);
		conditionOldWeek.put("locationId", locationId);
		conditionOldWeek.put("startDate", oldStartDate);
		conditionOldWeek.put("endDate", oldEndDate);

		List<Object> returnGmCo2LocationSumGridWeekTemp = new ArrayList<Object>();
		List<Object> returnGmCo2LocationSumGridWeekOldTemp = new ArrayList<Object>();

		/*************************
		 * @author cmyang 2010. 10. 20
		 * 
		 *         선택된 Node에 검침 데이터가 없으면, Children Node 조회로 수정 (기존 Children
		 *         Node가 있으면 무조건 Children Node의 검침 데이터를 조회)
		 */
		List<Integer> parent = locationDao.getParentId(detailLocationId);

		if (!parent.isEmpty()) {
			returnGmCo2LocationSumGridWeekTemp = dayGmDao
					.getConsumptionGmCo2WeekMonitoringLocationId(conditionWeek);
			returnGmCo2LocationSumGridWeekOldTemp = dayGmDao
					.getConsumptionGmCo2WeekMonitoringLocationId(conditionOldWeek);
		}

		if (returnGmCo2LocationSumGridWeekTemp.isEmpty()) {
			Set<Location> childrenLocationSet = locationDao.get(locationId)
					.getChildren();

			if (childrenLocationSet != null && !childrenLocationSet.isEmpty()) {
				returnGmCo2LocationSumGridWeekTemp = dayGmDao
						.getConsumptionGmCo2WeekMonitoringParentId(conditionWeek);
				returnGmCo2LocationSumGridWeekOldTemp = dayGmDao
						.getConsumptionGmCo2WeekMonitoringParentId(conditionOldWeek);
			}
		}
		/*************************/

		if (!returnGmCo2LocationSumGridWeekTemp.isEmpty()) {

            for (int i = 0; i < 7; i++) {
                HashMap<String, Object> setTemp = new HashMap<String, Object>();  
				String cpstartWeek = CalendarUtil.getDateWithoutFormat(
						startDate, Calendar.DATE, i);
				String setDate = null;
				boolean isSearch = false;
				
				HashMap<String, Object> temp = new HashMap<String, Object>();
				HashMap<String, Object> temp2 = new HashMap<String, Object>();
				
				int k = 0 ;
				
				for (int j = 0; j < returnGmCo2LocationSumGridWeekTemp.size(); j++) {
					temp = (HashMap<String, Object>) returnGmCo2LocationSumGridWeekTemp
							.get(j);
					
					setDate = temp.get("YYYYMMDD").toString();

					if (cpstartWeek.contains(setDate)) {
						isSearch = true;
						k= j;
					}else{
						continue;
					}
				}
				
				temp = (HashMap<String, Object>) returnGmCo2LocationSumGridWeekTemp
						.get(k);
				if (!returnGmCo2LocationSumGridWeekOldTemp.isEmpty()
						&& i < returnGmCo2LocationSumGridWeekOldTemp.size()) {

					temp2 = (HashMap<String, Object>) returnGmCo2LocationSumGridWeekOldTemp
							.get(i);
				}
				
				if (isSearch) {
					
					int y1 = Integer.parseInt(cpstartWeek.substring(0, 4));
					int m1 = Integer.parseInt(cpstartWeek.substring(4, 6));
					int d1 = Integer.parseInt(cpstartWeek.substring(6, 8));

					String dayWeek = CalendarUtil.getWeekDay(
							lang.getCode_2letter(), y1, m1, d1);
					setTemp.put("yyyymmdd", cpstartWeek);
					setTemp.put("MYDATE", dayWeek);
					setTemp.put("GMSUM", temp.get("GM_TOTAL"));
					setTemp.put("CO2SUM", temp.get("CO2_TOTAL"));
					if (!temp2.isEmpty()) {

						setTemp.put("OLDGMSUM", temp2.get("GM_TOTAL"));
						setTemp.put("OLDCO2SUM", temp2.get("CO2_TOTAL"));
					} else {

						setTemp.put("OLDGMSUM", "0");
						setTemp.put("OLDCO2SUM", "0");
					}
										
				} else {
					
					int y1 = Integer.parseInt(cpstartWeek.substring(0, 4));
					int m1 = Integer.parseInt(cpstartWeek.substring(4, 6));
					int d1 = Integer.parseInt(cpstartWeek.substring(6, 8));

					String dayWeek = CalendarUtil.getWeekDay(
							lang.getCode_2letter(), y1, m1, d1);
					setTemp.put("yyyymmdd", cpstartWeek);
					setTemp.put("MYDATE", dayWeek);
					setTemp.put("GMSUM", "0");
					setTemp.put("CO2SUM", "0");
					if (!temp2.isEmpty()) {

						setTemp.put("OLDGMSUM", temp2.get("GM_TOTAL"));
						setTemp.put("OLDCO2SUM", temp2.get("CO2_TOTAL"));
					} else {

						setTemp.put("OLDGMSUM", "0");
						setTemp.put("OLDCO2SUM", "0");
					}
				}
				
				result.add(i, setTemp);
            }
		} else {
            HashMap<String, Object> setTemp = null;  
		    for (int i = 0; i < 7; i++) {
                setTemp = new HashMap<String, Object>();  
                String cpstartWeek = CalendarUtil.getDateWithoutFormat(startDate, Calendar.DATE, i);

                int y1 = Integer.parseInt(cpstartWeek.substring(0, 4));
                int m1 = Integer.parseInt(cpstartWeek.substring(4, 6));
                int d1 = Integer.parseInt(cpstartWeek.substring(6, 8));
                String dayWeek = CalendarUtil.getWeekDay(lang.getCode_2letter(), y1, m1, d1);

                setTemp.put("yyyymmdd", cpstartWeek);
                setTemp.put("MYDATE", dayWeek);
                setTemp.put("GMSUM", 0);
                setTemp.put("CO2SUM", 0);
                setTemp.put("OLDGMSUM", 0);
                setTemp.put("OLDCO2SUM", 0);

                result.add(i, setTemp);
            }
		}

		return result;
	}

	/**
	 * 일주일 : 빌딩 전체 온도 의 최대 / 최소
	 * 
	 * @param condition
	 * @return
	 */
	@SuppressWarnings("unchecked")
    protected List<HashMap<String, Object>> tmHmMonitoringMinMaxWeek(Map<String, Object> condition) {

		String searchDateType = (String) condition.get("searchDateType"); // 일 ,
		// 주
		// ,
		// 월
		// ,
		// 분기
		Integer supplierId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("supplierId"), 0);
		Integer locationId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("locationId"), 0);// location 그룹 이동 selectbox에서 선택한
		// locationId
		Integer detailLocationId = (Integer) ObjectUtils.defaultIfNull(
				condition.get("detailLocationId"), 0); // 마우스로 해당 동/층/호를 클릭했을때의
		// 해당 locationId
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");

		if (0 < detailLocationId) {// bar차트에서 특정 bar를 클릭한경우.

			locationId = detailLocationId;
		}

		Supplier supplier = supplierDao.get(supplierId);
		Language lang = supplier.getLang();
		
		Map<String, Object> conditionWeek = new HashMap<String, Object>();
		conditionWeek.put("searchDateType", searchDateType);
		conditionWeek.put("supplierId", supplierId);
		conditionWeek.put("startDate", startDate);
		conditionWeek.put("endDate", endDate);

		// 건물 전체에대한 온도 정보 표현시에는 첫번째 빌딩에대한 온도정보를 나타내도록 한다.
		if (0 == thisLocationLevel(locationId)) {

			if (0 == locationDao.getChildren(locationId).size()) {
				conditionWeek.put("locationId", locationId);
			} else {
				Location targetLocation = locationDao.getChildren(locationId)
						.get(0);
				conditionWeek.put("locationId", targetLocation.getId());
			}
		} else {
			conditionWeek.put("locationId", locationId);
		}

		List<Object> locationList = meteringDayDao
				.getTemperatureHumidityLocation("DAY_TM");
		
		if(!locationList.isEmpty() && locationList.size()!=0){
			
			Map<String, Object> map = (Map<String, Object>) locationList.get(0);

			conditionWeek.put("locationId", DecimalUtil.ConvertNumberToInteger(map.get("LOCATIONID")));
		}else{
			conditionWeek.put("locationId", locationId);
		}
		
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		List<Object> returnTMLocationTemp = dayTMDao
				.getConsumptionTmMonitoring(conditionWeek);
		List<Object> returnHUMLocationTemp = dayHUMDao
				.getConsumptionHumMonitoring(conditionWeek);

        String[] dayOfWeek = null;
        if (lang.getCode_2letter().equals("ko")) {
            dayOfWeek = new String[]{
                WeekDay.Sunday.getKorName(),
                WeekDay.Monday.getKorName(),
                WeekDay.Tuesday.getKorName(),
                WeekDay.Wednesday.getKorName(),
                WeekDay.Thursday.getKorName(),
                WeekDay.Friday.getKorName(),
                WeekDay.Saturday.getKorName()
                };
        } else {
            dayOfWeek = new String[]{
                WeekDay.Sunday.getEngName(),
                WeekDay.Monday.getEngName(),
                WeekDay.Tuesday.getEngName(),
                WeekDay.Wednesday.getEngName(),
                WeekDay.Thursday.getEngName(),
                WeekDay.Friday.getEngName(),
                WeekDay.Saturday.getEngName()
            };
        }

		if (!returnTMLocationTemp.isEmpty()) {
			Double tmMax = 0d;
			Double tmMin = 0d;
			Map<String, Object> weekMap = new HashMap<String, Object>();

			for (int i = 0; i < returnTMLocationTemp.size(); i++) {

				HashMap<String, Object> tempTm = (HashMap<String, Object>) returnTMLocationTemp
						.get(i);
				HashMap<String, Object> tempHum = new HashMap<String, Object>();
				if (!returnHUMLocationTemp.isEmpty()) {
					tempHum = (HashMap<String, Object>) returnHUMLocationTemp
							.get(i);
				}
				String setDate = tempTm.get("YYYYMMDD").toString();

				int y1 = Integer.parseInt(setDate.substring(0, 4));
				int m1 = Integer.parseInt(setDate.substring(4, 6));
				int d1 = Integer.parseInt(setDate.substring(6, 8));

				String dayWeek = CalendarUtil.getWeekDay(lang.getCode_2letter(), y1, m1, d1);
				HashMap<String, Object> setTemp = new HashMap<String, Object>();
				setTemp.put("MYDATE", dayWeek);
				// setTemp.put( "TMAVGVALUE" , tempTm.get("TM_AVGVALUE") );
				setTemp.put("TMMAXVALUE", tempTm.get("TM_MAXVALUE"));
				setTemp.put("TMMINVALUE", tempTm.get("TM_MINVALUE"));
				// setTemp.put( "HUMAVGVALUE" , tempTm.get("HUM_AVGVALUE") );
				setTemp.put("HUMMAXVALUE", tempHum.get("HUM_MAXVALUE"));
				setTemp.put("HUMMINVALUE", tempHum.get("HUM_MINVALUE"));

				Double tmMaxTemp = DecimalUtil.ConvertNumberToDouble(tempTm.get("TM_MAXVALUE"));
				Double tmMinTemp = DecimalUtil.ConvertNumberToDouble(tempTm.get("TM_MINVALUE"));

				if (tmMax < tmMaxTemp) {
					tmMax = tmMaxTemp;
				}

				if (tmMin > tmMinTemp) {
					tmMin = tmMinTemp;
				}

				setTemp.put("TM_MAX", tmMax);
				setTemp.put("TM_MIN", tmMin);
				weekMap.put(dayWeek, setTemp);
				// result.add(i, setTemp);
			}

			for (int k = 0; k < 7; k++) {

				if (weekMap.containsKey(dayOfWeek[k])) {
					result.add(k, (HashMap<String, Object>) weekMap
							.get(dayOfWeek[k]));
				} else {
					HashMap<String, Object> setTemp = new HashMap<String, Object>();
					setTemp.put("MYDATE", dayOfWeek[k]);
					// setTemp.put( "TMAVGVALUE" , "0" );
					setTemp.put("TMMAXVALUE", "0");
					setTemp.put("TMMINVALUE", "0");
					// setTemp.put( "HUMAVGVALUE" , "0" );
					setTemp.put("HUMMAXVALUE", "0");
					setTemp.put("HUMMINVALUE", "0");
					setTemp.put("TM_MAX", tmMax);
					setTemp.put("TM_MIN", tmMin);
					result.add(k, setTemp);
					// result.add(returnTMLocationTemp.size() + k, setTemp);
				}
			}
		} else {
            HashMap<String, Object> setTemp = null;
            for (int k = 0; k < 7; k++) {
                setTemp = new HashMap<String, Object>();

                setTemp.put("MYDATE", dayOfWeek[k]);
                setTemp.put("TMMAXVALUE", 0);
                setTemp.put("TMMINVALUE", 0);
                setTemp.put("HUMMAXVALUE", 0);
                setTemp.put("HUMMINVALUE", 0);
                setTemp.put("TM_MAX", 0);
                setTemp.put("TM_MIN", 0);
                result.add(k, setTemp);
            }
		}
		return result;
	}

	/**
	 * 월별 가스사용량/탄솝출량 기본정보 월 사용량 , 평균요금 , 최소사용량 , 최대사용량
	 * 
	 * @param condition
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
    private List<HashMap<String, Object>> gmCo2MonitoringSumMinMaxMonthInfo(
			Map<String, Object> condition) {

		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		double total = 0; // 주간 사용량
		double avgCharge = 0; // 평균요금
		double sumCharge = 0; // 요금 합
		int minUseTime = 0; // 최소사용량 시간
		int maxUseTime = 0; // 최대 사용량 시간
		double minUse = 0; // 최소사용량
		double maxUse = 0; // 최대 사용량

		double[] minUseArr = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		int[] minUseTimeArr = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		String searchDateType = (String) condition.get("searchDateType"); // 일 ,
		// 주
		// ,
		// 월
		// ,
		// 분기
		Integer supplierId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("supplierId"), 0);
		Integer locationId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("locationId"), 0);// location 그룹 이동 selectbox에서 선택한
		// locationId
		Integer detailLocationId = (Integer) ObjectUtils.defaultIfNull(
				condition.get("detailLocationId"), 0); // 마우스로 해당 동/층/호를 클릭했을때의
		// 해당 locationId
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");

		if (0 < detailLocationId) {// bar차트에서 특정 bar를 클릭한경우.

			locationId = detailLocationId;
		}

		Map<String, Object> conditionMonthInfo = new HashMap<String, Object>(); // 전일
		// 정보
		// 셋팅
		conditionMonthInfo.put("searchDateType", searchDateType);
		conditionMonthInfo.put("supplierId", supplierId);
		conditionMonthInfo.put("locationId", locationId);
		conditionMonthInfo.put("startDate", startDate);
		conditionMonthInfo.put("endDate", endDate);

		List<Object> returnGmCo2LocationSumGridTemp = new ArrayList<Object>();

		/*************************
		 * @author cmyang 2010. 10. 20
		 * 
		 *         선택된 Node에 검침 데이터가 없으면, Children Node 조회로 수정 (기존 Children
		 *         Node가 있으면 무조건 Children Node의 검침 데이터를 조회)
		 */
		returnGmCo2LocationSumGridTemp = monthGMDao
				.getConsumptionGmCo2MonitoringLocationId(conditionMonthInfo);
		if (returnGmCo2LocationSumGridTemp.isEmpty()) {
			Set<Location> childrenLocationSet = locationDao.get(locationId)
					.getChildren();

			if (childrenLocationSet != null && !childrenLocationSet.isEmpty()) {
				returnGmCo2LocationSumGridTemp = monthGMDao
						.getConsumptionGmCo2MonitoringParentId(conditionMonthInfo);
			}
		}
		/*************************/

		if (!returnGmCo2LocationSumGridTemp.isEmpty()) {

			for (int i = 0; i < returnGmCo2LocationSumGridTemp.size(); i++) {

				HashMap<String, Object> temp = (HashMap<String, Object>) returnGmCo2LocationSumGridTemp
						.get(i);

				double check = Double.valueOf(ObjectUtils.defaultIfNull(
						temp.get("GM_TOTAL"), "0").toString());

				String setDate = temp.get("YYYYMM").toString();
				int y1 = Integer.parseInt(setDate.substring(0, 4));
				int m1 = Integer.parseInt(setDate.substring(4, 6));
				minUseArr[m1 - 1] = check;
				if (i == 0) {
					minUse = check;
					minUseTime = m1;

				}

				total = total + check;
				BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
				Map<String, Object> chargeMap = new HashMap<String, Object>();
				chargeMap.put("serviceType", MeterType.GasMeter
						.getServiceType());
				chargeMap.put("dateType", DateType.MONTHLY.getCode());
				chargeMap.put("usage", check);
				chargeMap.put("period", 1);

				sumCharge = sumCharge + bemsUtil.getUsageCharge(chargeMap); // 사용요금

				if (maxUse < check) {
					maxUse = check;
					maxUseTime = m1;
				} else if (minUse > check) {
					minUse = check;
					minUseTime = m1;
				}

			}
			avgCharge = sumCharge / returnGmCo2LocationSumGridTemp.size();

			for (int i = 0; i < 12; i++) {
				if (minUse > minUseArr[i]) {
					minUse = minUseArr[i];
					minUseTime = minUseTimeArr[i];
				}

			}
			NumberFormat formatter = new DecimalFormat("###,###,###,###.##");
			HashMap<String, Object> setTemp = new HashMap<String, Object>();

			setTemp.put("INFOTOTAL", formatter.format(total) + " ㎥");
			setTemp.put("INFOCO2TOTAL", null);
			setTemp.put("INFOCAVGCHARGE", formatter.format(avgCharge)); // 원
			setTemp.put("INFOMAXUSETIME", maxUseTime); // 월
			setTemp.put("INFOMAXUSE", formatter.format(maxUse) + " ㎥");
			setTemp.put("INFOMINUSETIME", minUseTime); // 월
			setTemp.put("INFOMINUSE", formatter.format(minUse) + " ㎥");

			result.add(setTemp);

		}

		return result;

	}

	/**
	 * 월별 빌딩 전체 가스사용량/탄솝출량 의 합
	 * 
	 * @param condition
	 * @return
	 */
	@SuppressWarnings({ "unused", "unchecked" })
    private List<HashMap<String, Object>> gmCo2MonitoringSumMinMaxMonth(Map<String, Object> condition) {

		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		String searchDateType = (String) condition.get("searchDateType"); // 일 ,
		// 주
		// ,
		// 월
		// ,
		// 분기
		Integer supplierId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("supplierId"), 0);
		Integer locationId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("locationId"), 0);// location 그룹 이동 selectbox에서 선택한
		// locationId
		Integer detailLocationId = (Integer) ObjectUtils.defaultIfNull(
				condition.get("detailLocationId"), 0); // 마우스로 해당 동/층/호를 클릭했을때의
		// 해당 locationId
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");

		if (0 < detailLocationId) {// bar차트에서 특정 bar를 클릭한경우.
			locationId = detailLocationId;
		}

		int y = Integer.parseInt(startDate.substring(0, 4));
		int m = Integer.parseInt(startDate.substring(4, 6));

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");

		Calendar getOldYear = Calendar.getInstance();
		getOldYear.set(y, m - 1, 1);

		getOldYear.set(getOldYear.get(Calendar.YEAR) - 1, 0, 1);
		String oldStartDate = formatter.format(getOldYear.getTime());
		// System.out.println( "전년도 시작 날짜 : " + oldStartDate );

		getOldYear.set(getOldYear.get(Calendar.YEAR), 11, 31);
		String oldEndDate = formatter.format(getOldYear.getTime());
		// System.out.println( "전년도 마지막 날짜 : " + oldEndDate );

		Map<String, Object> conditionYear = new HashMap<String, Object>(); // 전일
		// 정보
		// 셋팅
		conditionYear.put("searchDateType", searchDateType);
		conditionYear.put("supplierId", supplierId);
		conditionYear.put("locationId", locationId);
		conditionYear.put("startDate", startDate);
		conditionYear.put("endDate", endDate);

		Map<String, Object> conditionOldYear = new HashMap<String, Object>(); // 전일
		// 정보
		// 셋팅
		conditionOldYear.put("searchDateType", searchDateType);
		conditionOldYear.put("supplierId", supplierId);
		conditionOldYear.put("locationId", locationId);
		conditionOldYear.put("startDate", oldStartDate);
		conditionOldYear.put("endDate", oldEndDate);

		List<Object> returnGmCo2LocationSumGridTemp = new ArrayList<Object>();
		List<Object> returnGmCo2LocationSumGridOldTemp = new ArrayList<Object>();

		/*************************
		 * @author cmyang 2010. 10. 20
		 * 
		 *         선택된 Node에 검침 데이터가 없으면, Children Node 조회로 수정 (기존 Children
		 *         Node가 있으면 무조건 Children Node의 검침 데이터를 조회)
		 */
		List<Integer> parent = locationDao.getParentId(detailLocationId);

		if (!parent.isEmpty()) {
			returnGmCo2LocationSumGridTemp = monthGMDao
					.getConsumptionGmCo2MonitoringLocationId(conditionYear);
			returnGmCo2LocationSumGridOldTemp = monthGMDao
					.getConsumptionGmCo2MonitoringLocationId(conditionOldYear);
		}
		if (returnGmCo2LocationSumGridTemp.isEmpty()) {
			Set<Location> childrenLocationSet = locationDao.get(locationId)
					.getChildren();

			if (childrenLocationSet != null && !childrenLocationSet.isEmpty()) {
				returnGmCo2LocationSumGridTemp = monthGMDao
						.getConsumptionGmCo2MonitoringParentId(conditionYear);
				returnGmCo2LocationSumGridOldTemp = monthGMDao
						.getConsumptionGmCo2MonitoringParentId(conditionOldYear);
			}
		}

		HashMap<String, HashMap<String, Object>> addMonth = new HashMap<String, HashMap<String, Object>>();
		HashMap<String, HashMap<String, Object>> addOldMonth = new HashMap<String, HashMap<String, Object>>();
		if (!returnGmCo2LocationSumGridTemp.isEmpty()) {

			for (int i = 0; i < returnGmCo2LocationSumGridTemp.size(); i++) {
				HashMap<String, Object> temp = (HashMap<String, Object>) returnGmCo2LocationSumGridTemp
						.get(i);

				String setDate = temp.get("YYYYMM").toString();

				int y1 = Integer.parseInt(setDate.substring(0, 4));
				int m1 = Integer.parseInt(setDate.substring(4, 6));

				HashMap<String, Object> setTemp = new HashMap<String, Object>();
				setTemp.put("MYDATE", m1);
				setTemp.put("GMSUM", temp.get("GM_TOTAL"));
				setTemp.put("CO2SUM", temp.get("CO2_TOTAL"));
				addMonth.put(m1 + "", setTemp);

			}

			for (int i = 0; i < returnGmCo2LocationSumGridOldTemp.size(); i++) {

				HashMap<String, Object> temp = (HashMap<String, Object>) returnGmCo2LocationSumGridOldTemp
						.get(i);

				String setDate = temp.get("YYYYMM").toString();

				int y1 = Integer.parseInt(setDate.substring(0, 4));
				int m1 = Integer.parseInt(setDate.substring(4, 6));

				HashMap<String, Object> setTemp = new HashMap<String, Object>();
				setTemp.put("MYDATE", m1);
				setTemp.put("GMSUM", temp.get("GM_TOTAL"));
				setTemp.put("CO2SUM", temp.get("CO2_TOTAL"));
				addOldMonth.put(m1 + "", setTemp);

			}

			for (int k = 0; k < 12; k++) {
				if (!addMonth.containsKey(k + 1 + "")) {
					HashMap<String, Object> setTemp = new HashMap<String, Object>();
					HashMap<String, Object> temp = new HashMap<String, Object>();
					setTemp.put("MYDATE", k + 1);
					setTemp.put("GMSUM", "0");
					setTemp.put("CO2SUM", "0");

					HashMap<String, Object> setOldTemp = (HashMap<String, Object>) addOldMonth
							.get(k + 1 + "");
					
					if (setOldTemp != null) {
						setTemp.put("OLDGMSUM", setOldTemp.get("GMSUM"));
						setTemp.put("OLDCO2SUM", setOldTemp.get("CO2SUM"));
					}else{
						setTemp.put("OLDGMSUM", "0");
						setTemp.put("OLDCO2SUM", "0");
					}

					result.add(k, setTemp);
				} else {
					HashMap<String, Object> setTemp = (HashMap<String, Object>) addMonth
							.get(k + 1 + "");
					HashMap<String, Object> setOldTemp = (HashMap<String, Object>) addOldMonth
							.get(k + 1 + "");
					
					if (setOldTemp != null) {
						setTemp.put("OLDGMSUM", setOldTemp.get("GMSUM"));
						setTemp.put("OLDCO2SUM", setOldTemp.get("CO2SUM"));
					}else{
						setTemp.put("OLDGMSUM", "0");
						setTemp.put("OLDCO2SUM", "0");
					}
					
					result.add(k, setTemp);

				}
			}
		} else {
            HashMap<String, Object> setTemp = null;
		    for (int k = 0; k < 12; k++) {
                setTemp = new HashMap<String, Object>();

                setTemp.put("MYDATE", k + 1);
                setTemp.put("GMSUM", 0);
                setTemp.put("CO2SUM", 0);
                setTemp.put("OLDGMSUM", 0);
                setTemp.put("OLDCO2SUM", 0);

                result.add(k, setTemp);
            }
		}

		return result;
	}

	/**
	 * 월별 빌딩 전체 온도/습도 의 최대 / 최소
	 * 
	 * @param condition
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
    protected List<HashMap<String, Object>> tmHmMonitoringMinMaxMonth(Map<String, Object> condition) {

		String searchDateType = (String) condition.get("searchDateType"); // 일 ,
		// 주
		// ,
		// 월
		// ,
		// 분기
		Integer supplierId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("supplierId"), 0);
		Integer locationId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("locationId"), 0);// location 그룹 이동 selectbox에서 선택한
		// locationId
		Integer detailLocationId = (Integer) ObjectUtils.defaultIfNull(
				condition.get("detailLocationId"), 0); // 마우스로 해당 동/층/호를 클릭했을때의
		// 해당 locationId
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");

		if (0 < detailLocationId) {// bar차트에서 특정 bar를 클릭한경우.
			locationId = detailLocationId;
		}

		Map<String, Object> conditionMonth = new HashMap<String, Object>();
		conditionMonth.put("searchDateType", searchDateType);
		conditionMonth.put("supplierId", supplierId);
		conditionMonth.put("startDate", startDate);
		conditionMonth.put("endDate", endDate);

		// 건물 전체에대한 온도 정보 표현시에는 첫번째 빌딩에대한 온도정보를 나타내도록 한다.
		if (0 == thisLocationLevel(locationId)) {

			if (0 == locationDao.getChildren(locationId).size()) {
				conditionMonth.put("locationId", locationId);
			} else {
				Location targetLocation = locationDao.getChildren(locationId)
						.get(0);
				conditionMonth.put("locationId", targetLocation.getId());
			}
		} else {
			conditionMonth.put("locationId", locationId);
		}

		List<Object> locationList = meteringDayDao
				.getTemperatureHumidityLocation("DAY_TM");

		if (!locationList.isEmpty() && locationList.size() != 0) {
			Map<String, Object> map = (Map<String, Object>) locationList.get(0);

			conditionMonth.put("locationId", map.get("LOCATIONID"));
		} else {
			conditionMonth.put("locationId", locationId);
		}
		
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		List<Object> returnTmLocationSumGridTemp = monthTMDao
				.getConsumptionTmMonitoring(conditionMonth);
		List<Object> returnHumLocationSumGridTemp = monthHUMDao
				.getConsumptionHumMonitoring(conditionMonth);

		if (!returnTmLocationSumGridTemp.isEmpty()) {
			Double tmMax = 0d;
			Double tmMin = 0d;
			HashMap<Integer, Object> monthTemp = new HashMap<Integer, Object>();
			for (int i = 0; i < returnTmLocationSumGridTemp.size(); i++) {

				HashMap<String, Object> tempTm = (HashMap<String, Object>) returnTmLocationSumGridTemp
						.get(i);
				HashMap<String, Object> tempHum = (HashMap<String, Object>) returnHumLocationSumGridTemp
						.get(i);

				String setDate = tempTm.get("YYYYMM").toString();

				int y = Integer.parseInt(setDate.substring(0, 4));
				int m = Integer.parseInt(setDate.substring(4, 6));

				HashMap<String, Object> setTemp = new HashMap<String, Object>();
				setTemp.put("MYDATE", m);
				// setTemp.put( "TMAVGVALUE" , tempTm.get("TM_AVGVALUE") );
				setTemp.put("TMMAXVALUE", tempTm.get("TM_MAXVALUE"));
				setTemp.put("TMMINVALUE", tempTm.get("TM_MINVALUE"));
				// setTemp.put( "HUMAVGVALUE" , tempTm.get("HUM_AVGVALUE") );
				setTemp.put("HUMMAXVALUE", tempHum.get("HUM_MAXVALUE"));
				setTemp.put("HUMMINVALUE", tempHum.get("HUM_MINVALUE"));

				Double tmMaxTemp = DecimalUtil.ConvertNumberToDouble(tempTm.get("TM_MAXVALUE"));
				Double tmMinTemp = DecimalUtil.ConvertNumberToDouble(tempTm.get("TM_MINVALUE"));

				if (tmMax < tmMaxTemp) {
					tmMax = tmMaxTemp;
				}

				if (tmMin > tmMinTemp) {
					tmMin = tmMinTemp;
				}

				setTemp.put("TM_MAX", tmMax);
				setTemp.put("TM_MIN", tmMin);

				monthTemp.put(m, setTemp);
				// result.add(setTemp);
			}

			for (int k = 0; k < 12; k++) {

				if (monthTemp.containsKey(k + 1)) {
					result.add(k, (HashMap<String, Object>) monthTemp
							.get(k + 1));
				} else {
					HashMap<String, Object> setTemp = new HashMap<String, Object>();
					setTemp.put("MYDATE", k + 1);
					// setTemp.put( "TMAVGVALUE" , 0 );
					setTemp.put("TMMAXVALUE", 0);
					setTemp.put("TMMINVALUE", 0);
					setTemp.put("TM_MAX", tmMax);
					setTemp.put("TM_MIN", tmMin);
					// setTemp.put( "HUMAVGVALUE" , 0 );
					setTemp.put("HUMMAXVALUE", 0);
					setTemp.put("HUMMINVALUE", 0);

					result.add(k, setTemp);
				}
			}
		} else {
            HashMap<String, Object> setTemp = null;
            for (int k = 0; k < 12; k++) {
                setTemp = new HashMap<String, Object>();

                setTemp.put("MYDATE", k + 1);
                setTemp.put("TMMAXVALUE", 0);
                setTemp.put("TMMINVALUE", 0);
                setTemp.put("TM_MAX", 0);
                setTemp.put("TM_MIN", 0);
                setTemp.put("HUMMAXVALUE", 0);
                setTemp.put("HUMMINVALUE", 0);

                result.add(k, setTemp);
            }
		}

		return result;
	}

	/**
	 * 분기별 가스사용량/탄솝출량 기본정보 분기 사용량 , 평균 요금 , 최소사용량 , 최대 사용량
	 * 
	 * @param condition
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
    private List<HashMap<String, Object>> gmCo2MonitoringSumMinMaxQuarterInfo(
			Map<String, Object> condition) {

		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		double emTotalTemp = 0;
		double co2TotalTemp = 0;
		int q = 0;

		double total = 0; // 주간 사용량
		double avgCharge = 0; // 평균요금
		double sumCharge = 0; // 요금 합
		int minUseTime = 0; // 최소사용량 시간
		int maxUseTime = 0; // 최대 사용량 시간
		double minUse = 0; // 최소사용량
		double maxUse = 0; // 최대 사용량

		int Q1minUseTime = 1; // 최소사용량 시간
		int Q2minUseTime = 2; // 최소사용량 시간
		int Q3minUseTime = 3; // 최소사용량 시간
		int Q4minUseTime = 4; // 최소사용량 시간
		double Q1minUse = 0; // 최소사용량
		double Q2minUse = 0; // 최소사용량
		double Q3minUse = 0; // 최소사용량
		double Q4minUse = 0; // 최소사용량

		String searchDateType = (String) condition.get("searchDateType"); // 일 ,
		// 주
		// ,
		// 월
		// ,
		// 분기
		Integer supplierId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("supplierId"), 0);
		Integer locationId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("locationId"), 0);// location 그룹 이동 selectbox에서 선택한
		// locationId
		Integer detailLocationId = (Integer) ObjectUtils.defaultIfNull(
				condition.get("detailLocationId"), 0); // 마우스로 해당 동/층/호를 클릭했을때의
		// 해당 locationId
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");

		if (0 < detailLocationId) {// bar차트에서 특정 bar를 클릭한경우.

			locationId = detailLocationId;
		}

		Map<String, Object> conditionQuarter = new HashMap<String, Object>();
		conditionQuarter.put("searchDateType", searchDateType);
		conditionQuarter.put("supplierId", supplierId);
		conditionQuarter.put("locationId", locationId);
		conditionQuarter.put("startDate", startDate);
		conditionQuarter.put("endDate", endDate);

		List<Object> returnGmCo2LocationSumGridTemp = new ArrayList<Object>();

		/*************************
		 * @author cmyang 2010. 10. 20
		 * 
		 *         선택된 Node에 검침 데이터가 없으면, Children Node 조회로 수정 (기존 Children
		 *         Node가 있으면 무조건 Children Node의 검침 데이터를 조회)
		 */
		returnGmCo2LocationSumGridTemp = monthGMDao
				.getConsumptionGmCo2MonitoringLocationId(conditionQuarter);
		if (returnGmCo2LocationSumGridTemp.isEmpty()) {
			Set<Location> childrenLocationSet = locationDao.get(locationId)
					.getChildren();

			if (childrenLocationSet != null && !childrenLocationSet.isEmpty()) {
				returnGmCo2LocationSumGridTemp = monthGMDao
						.getConsumptionGmCo2MonitoringParentId(conditionQuarter);
			}
		}
		/*************************/

		if (!returnGmCo2LocationSumGridTemp.isEmpty()) {

			for (int i = 0; i < returnGmCo2LocationSumGridTemp.size(); i++) {

				HashMap<String, Object> temp = (HashMap<String, Object>) returnGmCo2LocationSumGridTemp
						.get(i);
				double check = Double.valueOf(ObjectUtils.defaultIfNull(
						temp.get("GM_TOTAL"), "0").toString());

				String setDate = temp.get("YYYYMM").toString();

				int y1 = Integer.parseInt(setDate.substring(0, 4));
				int m1 = Integer.parseInt(setDate.substring(4, 6));

				if (1 == (m1 % 3)) {

					emTotalTemp = 0;
					co2TotalTemp = 0;
				}

				emTotalTemp = emTotalTemp
						+ Double.parseDouble(ObjectUtils.defaultIfNull(
								temp.get("GM_TOTAL"), "0").toString());
				co2TotalTemp = co2TotalTemp
						+ Double.parseDouble(ObjectUtils.defaultIfNull(
								temp.get("CO2_TOTAL"), "0").toString());

				if (0 == (m1 % 3)
						|| (i + 1) == returnGmCo2LocationSumGridTemp.size()) {

					q = m1 / 3;
					minUse = emTotalTemp;
					if ((0 != (m1 % 3))
							&& (i + 1) == returnGmCo2LocationSumGridTemp.size())
						q = q + 1;

					if ((m1 % 3) == 0) {
						if (q == 1) {
							Q1minUseTime = q; // 최소사용량 시간
							Q1minUse = emTotalTemp; // 최소사용량
						} else if (q == 2) {
							Q2minUseTime = q; // 최소사용량 시간
							Q2minUse = emTotalTemp; // 최소사용량
						} else if (q == 3) {

							Q3minUseTime = q; // 최소사용량 시간
							Q3minUse = emTotalTemp; // 최소사용량
						} else if (q == 4) {

							Q4minUseTime = q; // 최소사용량 시간
							Q4minUse = emTotalTemp; // 최소사용량
						}
						minUse = emTotalTemp;
						minUseTime = q;
					}

					// q++;

					if (maxUse < emTotalTemp) {
						maxUse = emTotalTemp;
						maxUseTime = q;
					} else if (minUse > emTotalTemp) {
						minUse = emTotalTemp;
						minUseTime = q;
					}

					total = total + emTotalTemp;
					BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
					Map<String, Object> chargeMap = new HashMap<String, Object>();
					chargeMap.put("serviceType", MeterType.GasMeter
							.getServiceType());
					chargeMap.put("dateType", DateType.MONTHLY.getCode());
					chargeMap.put("usage", check);
					chargeMap.put("period", 3);

					sumCharge = sumCharge + bemsUtil.getUsageCharge(chargeMap); // 사용요금
					// sumCharge = sumCharge + 사용요금;

				}
			}
			avgCharge = sumCharge / q;
		}

		if (minUse > Q1minUse) {
			minUse = Q1minUse;
			minUseTime = Q1minUseTime;
		}

		if (minUse > Q2minUse) {
			minUse = Q2minUse;
			minUseTime = Q2minUseTime;
		}

		if (minUse > Q3minUse) {
			minUse = Q3minUse;
			minUseTime = Q3minUseTime;
		}

		if (minUse > Q4minUse) {
			minUse = Q4minUse;
			minUseTime = Q4minUseTime;
		}

		NumberFormat formatter = new DecimalFormat("###,###,###,###.##");
		HashMap<String, Object> setTemp = new HashMap<String, Object>();

		setTemp.put("INFOTOTAL", formatter.format(total) + " ㎥");
		setTemp.put("INFOCO2TOTAL", null);
		setTemp.put("INFOCAVGCHARGE", formatter.format(avgCharge)); // 원
		setTemp.put("INFOMAXUSETIME", maxUseTime); // 분기
		setTemp.put("INFOMAXUSE", formatter.format(maxUse) + " ㎥");
		setTemp.put("INFOMINUSETIME", minUseTime); // 분기
		setTemp.put("INFOMINUSE", formatter.format(minUse) + " ㎥");

		result.add(0, setTemp);

		return result;
	}

	/**
	 * 분기별 빌딩 전체 가스사용량/탄솝출량 의 합
	 * 
	 * @param condition
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
    private List<HashMap<String, Object>> gmCo2MonitoringSumMinMaxQuarter(Map<String, Object> condition) {

		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		String searchDateType = (String) condition.get("searchDateType"); // 일 ,
		// 주
		// ,
		// 월
		// ,
		// 분기
		Integer supplierId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("supplierId"), 0);
		Integer locationId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("locationId"), 0);// location 그룹 이동 selectbox에서 선택한
		// locationId
		Integer detailLocationId = (Integer) ObjectUtils.defaultIfNull(
				condition.get("detailLocationId"), 0); // 마우스로 해당 동/층/호를 클릭했을때의
		// 해당 locationId
		String startDate = (String) condition.get("startDate");
		String endDate = (String) condition.get("endDate");
		String year = startDate.substring(0, 4);
		
		if (0 < detailLocationId) {// bar차트에서 특정 bar를 클릭한경우.
			locationId = detailLocationId;
		}

		int y = Integer.parseInt(startDate.substring(0, 4));
		int m = Integer.parseInt(startDate.substring(4, 6));

		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");

		Calendar getOldYear = Calendar.getInstance();
		getOldYear.set(y, m - 1, 1);

		getOldYear.set(getOldYear.get(Calendar.YEAR) - 1, 0, 1);
		String oldStartDate = formatter.format(getOldYear.getTime());
		// System.out.println( "전년도 시작 날짜 : " + oldStartDate );

		getOldYear.set(getOldYear.get(Calendar.YEAR), 11, 31);
		String oldEndDate = formatter.format(getOldYear.getTime());
		// System.out.println( "전년도 마지막 날짜 : " + oldEndDate );

		Map<String, Object> conditionYear = new HashMap<String, Object>(); // 전일
		// 정보
		// 셋팅
		conditionYear.put("searchDateType", searchDateType);
		conditionYear.put("supplierId", supplierId);
		conditionYear.put("locationId", locationId);
		conditionYear.put("startDate", startDate);
		conditionYear.put("endDate", endDate);

		Map<String, Object> conditionOldYear = new HashMap<String, Object>(); // 전일
		// 정보
		// 셋팅
		conditionOldYear.put("searchDateType", searchDateType);
		conditionOldYear.put("supplierId", supplierId);
		conditionOldYear.put("locationId", locationId);
		conditionOldYear.put("startDate", oldStartDate);
		conditionOldYear.put("endDate", oldEndDate);

		double emTotalTemp = 0;
		double co2TotalTemp = 0;
		double emTotalTemp1 = 0;
		double co2TotalTemp1 = 0;
		int q = 0;

		List<Object> returnGmCo2LocationSumGridTemp = new ArrayList<Object>();
		List<Object> returnGmCo2LocationSumGridOldTemp = new ArrayList<Object>();

		/*************************
		 * @author cmyang 2010. 10. 20
		 * 
		 *         선택된 Node에 검침 데이터가 없으면, Children Node 조회로 수정 (기존 Children
		 *         Node가 있으면 무조건 Children Node의 검침 데이터를 조회)
		 */
		List<Integer> parent = locationDao.getParentId(detailLocationId);

		if (!parent.isEmpty()) {
			returnGmCo2LocationSumGridTemp = monthGMDao
					.getConsumptionGmCo2MonitoringLocationId(conditionYear);
			returnGmCo2LocationSumGridOldTemp = monthGMDao
					.getConsumptionGmCo2MonitoringLocationId(conditionOldYear);
		}
		if (returnGmCo2LocationSumGridTemp.isEmpty()) {
			Set<Location> childrenLocationSet = locationDao.get(locationId)
					.getChildren();

			if (childrenLocationSet != null && !childrenLocationSet.isEmpty()) {
				returnGmCo2LocationSumGridTemp = monthGMDao
						.getConsumptionGmCo2MonitoringParentId(conditionYear);
				returnGmCo2LocationSumGridOldTemp = monthGMDao
						.getConsumptionGmCo2MonitoringParentId(conditionOldYear);
			}
		}

		if (!returnGmCo2LocationSumGridTemp.isEmpty()) {

			Map<String, HashMap<String, Object>> set = new HashMap<String, HashMap<String, Object>>();
			Map<String, HashMap<String, Object>> setOld = new HashMap<String, HashMap<String, Object>>();
			for (int i = 0; i < returnGmCo2LocationSumGridTemp.size(); i++) {

				HashMap<String, Object> temp = (HashMap<String, Object>) returnGmCo2LocationSumGridTemp
						.get(i);

				String setDate = temp.get("YYYYMM").toString();

				int y1 = Integer.parseInt(setDate.substring(0, 4));
				int m1 = Integer.parseInt(setDate.substring(4, 6));

				if (1 == (m1 % 3)) {

					emTotalTemp = 0;
					co2TotalTemp = 0;

				}

				emTotalTemp = emTotalTemp
						+ Double.parseDouble(ObjectUtils.defaultIfNull(
								temp.get("GM_TOTAL"), "0").toString());
				co2TotalTemp = co2TotalTemp
						+ Double.parseDouble(ObjectUtils.defaultIfNull(
								temp.get("CO2_TOTAL"), "0").toString());

				if (0 == (m1 % 3)
						|| (i + 1) == returnGmCo2LocationSumGridTemp.size()) {

					q = m1 / 3;
					if ((0 != (m1 % 3))
							&& (i + 1) == returnGmCo2LocationSumGridTemp.size()) {
						q = m1 / 3 + 1;
					}

					HashMap<String, Object> setTemp = new HashMap<String, Object>();
					setTemp.put("yyyy", year);
					setTemp.put("MYDATE", q + "Q");
					setTemp.put("GMSUM", emTotalTemp);
					setTemp.put("CO2SUM", co2TotalTemp);

					set.put(q + "Q", setTemp);
					// result.add( q - 1 , setTemp );
				}
			}

			for (int i = 0; i < returnGmCo2LocationSumGridOldTemp.size(); i++) {

				HashMap<String, Object> temp = (HashMap<String, Object>) returnGmCo2LocationSumGridOldTemp
						.get(i);

				String setDate = temp.get("YYYYMM").toString();

				int y1 = Integer.parseInt(setDate.substring(0, 4));
				int m1 = Integer.parseInt(setDate.substring(4, 6));

				if (1 == (m1 % 3)) {

					emTotalTemp1 = 0;
					co2TotalTemp1 = 0;
				}

				emTotalTemp1 = emTotalTemp1
						+ Double.parseDouble(ObjectUtils.defaultIfNull(
								temp.get("GM_TOTAL"), "0").toString());
				co2TotalTemp1 = co2TotalTemp1
						+ Double.parseDouble(ObjectUtils.defaultIfNull(
								temp.get("CO2_TOTAL"), "0").toString());

				if (0 == (m1 % 3)
						|| (i + 1) == returnGmCo2LocationSumGridOldTemp.size()) {

					q = m1 / 3;
					if ((0 != (m1 % 3))
							&& (i + 1) == returnGmCo2LocationSumGridOldTemp
									.size()) {
						q = m1 / 3 + 1;
					}

					HashMap<String, Object> setTemp = new HashMap<String, Object>();
					setTemp.put("yyyy", year);
					setTemp.put("MYDATE", q + "Q");
					setTemp.put("GMSUM", emTotalTemp1);
					setTemp.put("CO2SUM", co2TotalTemp1);

					setOld.put(q + "Q", setTemp);
					// result.add( q - 1 , setTemp );
				}
			}

			for (int k = 1; k < 5; k++) {

				if (!set.containsKey(k + "Q")) {
					HashMap<String, Object> setTemp = new HashMap<String, Object>();
					HashMap<String, Object> setOldTemp = (HashMap<String, Object>) setOld
							.get(k + "Q");
					setTemp.put("yyyy", year);
					setTemp.put("MYDATE", k + "Q");
					setTemp.put("GMSUM", "0");
					setTemp.put("CO2SUM", "0");

					if (setOldTemp != null) {
						setTemp.put("OLDGMSUM", setOldTemp.get("GMSUM"));
						setTemp.put("OLDCO2SUM", setOldTemp.get("CO2SUM"));
					} else {
						setTemp.put("OLDGMSUM", "0");
						setTemp.put("OLDCO2SUM", "0");
					}

					set.put(k + "Q", setTemp);
				} else {
					HashMap<String, Object> setTemp = (HashMap<String, Object>) set
							.get(k + "Q");
					HashMap<String, Object> setOldTemp = (HashMap<String, Object>) setOld
							.get(k + "Q");
					setTemp.put("yyyy",year);
					if (setOldTemp != null) {
						setTemp.put("OLDGMSUM", setOldTemp.get("GMSUM"));
						setTemp.put("OLDCO2SUM", setOldTemp.get("CO2SUM"));
					} else {
						setTemp.put("OLDGMSUM", "0");
						setTemp.put("OLDCO2SUM", "0");
					}
				}

				result.add(k - 1, (HashMap<String, Object>) set.get(k + "Q"));
			}
		} else {
            HashMap<String, Object> setTemp = null;
		    for (int k = 0; k < 4; k++) {
                setTemp = new HashMap<String, Object>();

                setTemp.put("yyyy", year);
                setTemp.put("MYDATE", (k+1) + "Q");
                setTemp.put("GMSUM", 0);
                setTemp.put("CO2SUM", 0);
                setTemp.put("OLDGMSUM", 0);
                setTemp.put("OLDCO2SUM", 0);

                result.add(k, setTemp);
            }
		}

		return result;
	}

	/**
	 * 분기별 빌딩 전체 온도/습도 의 최대 /최소
	 * 
	 * @param condition
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "unused" })
    protected List<HashMap<String, Object>> tmHmMonitoringMinMaxQuarter(Map<String, Object> condition) {

		String searchDateType = ObjectUtils.defaultIfNull(
				condition.get("searchDateType"),
				CommonConstants.DateType.DAILY.getCode()).toString(); // 일 , 주 ,
		// 월 ,
		// 분기
		Integer supplierId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("supplierId"), 0);
		Integer locationId = (Integer) ObjectUtils.defaultIfNull(condition
				.get("locationId"), 0);
		String startDate = ObjectUtils.defaultIfNull(
				condition.get("startDate"), "").toString();
		String endDate = ObjectUtils
				.defaultIfNull(condition.get("endDate"), "").toString();

		Map<String, Object> conditionQuarter = new HashMap<String, Object>();
		conditionQuarter.put("searchDateType", searchDateType);
		conditionQuarter.put("supplierId", supplierId);
		conditionQuarter.put("startDate", startDate);
		conditionQuarter.put("endDate", endDate);

		// 건물 전체에대한 온도 정보 표현시에는 첫번째 빌딩에대한 온도정보를 나타내도록 한다.
		if (0 == thisLocationLevel(locationId)) {

			if (0 == locationDao.getChildren(locationId).size()) {
				conditionQuarter.put("locationId", locationId);
			} else {
				Location targetLocation = locationDao.getChildren(locationId)
						.get(0);
				conditionQuarter.put("locationId", targetLocation.getId());
			}
		} else {
			conditionQuarter.put("locationId", locationId);
		}

		List<Object> locationList = meteringDayDao
				.getTemperatureHumidityLocation("DAY_TM");
		
		if (!locationList.isEmpty() && locationList.size() != 0) {
			Map<String, Object> map = (Map<String, Object>) locationList.get(0);

			conditionQuarter.put("locationId", map.get("LOCATIONID"));
		} else {
			conditionQuarter.put("locationId", locationId);
		}
		
		List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();

		int q = 0;

		double minUseTm = 0; // 최소사용량
		double maxUseTm = 0; // 최대 사용량
		double minUseHum = 0; // 습도 최소사용량
		double maxUseHum = 0; // 습도 최대 사용량

		List<Object> returnTmLocationGridTemp = monthTMDao
				.getConsumptionTmMonitoring(conditionQuarter);
		List<Object> returnHummLocationGridTemp = monthHUMDao
				.getConsumptionHumMonitoring(conditionQuarter);

		if (!returnTmLocationGridTemp.isEmpty()) {

			int tmSize = returnTmLocationGridTemp.size();
			Map<String, HashMap<String, Object>> set = new HashMap<String, HashMap<String, Object>>();
			for (int i = 0; i < tmSize; i++) {

				HashMap<String, Object> tempTm = (HashMap<String, Object>) returnTmLocationGridTemp
						.get(i);
				HashMap<String, Object> tempHum = (HashMap<String, Object>) returnHummLocationGridTemp
						.get(i);

				String setDate = tempTm.get("YYYYMM").toString();

				int y = Integer.parseInt(setDate.substring(0, 4));
				int m = Integer.parseInt(setDate.substring(4, 6));

				double checkMinUseTm = Double.valueOf(ObjectUtils
						.defaultIfNull(tempTm.get("TM_MINVALUE"), "0")
						.toString());
				double checkMaxUseTm = Double.valueOf(ObjectUtils
						.defaultIfNull(tempTm.get("TM_MAXVALUE"), "0")
						.toString());

				double checkMinUseHum = Double.valueOf(ObjectUtils
						.defaultIfNull(tempHum.get("HUM_MINVALUE"), "0")
						.toString());
				double checkMaxUseHum = Double.valueOf(ObjectUtils
						.defaultIfNull(tempHum.get("HUM_MAXVALUE"), "0")
						.toString());

				if (i == 0) {

					minUseTm = checkMinUseTm;
					maxUseTm = checkMaxUseTm;

					minUseHum = checkMinUseHum;
					maxUseHum = checkMaxUseHum;
				}

				if (minUseTm > checkMinUseTm) {
					minUseTm = checkMinUseTm;
				} else if (maxUseTm < checkMaxUseTm) {
					maxUseTm = checkMaxUseTm;
				}

				if (minUseHum > checkMinUseHum) {
					minUseHum = checkMinUseHum;
				} else if (maxUseHum < checkMaxUseHum) {
					maxUseHum = checkMaxUseHum;
				}
				if (0 == (m % 3) || (i + 1) == tmSize) {
					q = m / 3;
					if ((0 != (m % 3)) && (i + 1) == tmSize) {
						q = m / 3 + 1;
					}
					HashMap<String, Object> setTemp = new HashMap<String, Object>();
					setTemp.put("MYDATE", q + "Q");
					setTemp.put("TMMAXVALUE", maxUseTm);
					setTemp.put("TMMINVALUE", minUseTm);
					setTemp.put("HUMMAXVALUE", maxUseHum);
					setTemp.put("HUMMINVALUE", minUseHum);
					setTemp.put("TM_MAX", maxUseTm);
					setTemp.put("TM_MIN", minUseTm);

					set.put(q + "Q", setTemp);
				}

			}
			for (int k = 1; k < 5; k++) {

				if (!set.containsKey(k + "Q")) {
					HashMap<String, Object> setTemp = new HashMap<String, Object>();
					setTemp.put("MYDATE", k + "Q");
					setTemp.put("TMMAXVALUE", 0);
					setTemp.put("TMMINVALUE", 0);
					setTemp.put("HUMMAXVALUE", 0);
					setTemp.put("HUMMINVALUE", 0);
					setTemp.put("TM_MAX", maxUseTm);
					setTemp.put("TM_MIN", minUseTm);
					set.put(k + "Q", setTemp);
				}

				result.add(k - 1, (HashMap<String, Object>) set.get(k + "Q"));
			}
		} else {
            HashMap<String, Object> setTemp = null;
            for (int k = 0; k < 4; k++) {
                setTemp = new HashMap<String, Object>();

                setTemp.put("MYDATE", (k+1) + "Q");
                setTemp.put("TMMAXVALUE", 0);
                setTemp.put("TMMINVALUE", 0);
                setTemp.put("HUMMAXVALUE", 0);
                setTemp.put("HUMMINVALUE", 0);
                setTemp.put("TM_MAX", 0);
                setTemp.put("TM_MIN", 0);

                result.add(k, setTemp);
            }
		}

		return result;
	}

	@Override
	public Map<String, Object> getBuildingLookUpGmByParam(
			String searchDateType, String supplierId, Integer detailLocationId) {
		Map<String, Object> condition = new HashMap<String, Object>();
	    condition .put("searchDateType", searchDateType);
	    condition .put("supplierId", supplierId);
	    condition .put("detailLocationId", detailLocationId);
	    return getBuildingLookUpGm(condition);
	}

	@Override
	public Map<String, Object> getBuildingLookUpMaxGmByParam(
			String searchDateType, String supplierId, Integer detailLocationId) {
		Map<String, Object> condition = new HashMap<String, Object>();
	    condition .put("searchDateType", searchDateType);
	    condition .put("supplierId", supplierId);
	    condition .put("detailLocationId", detailLocationId);
	    return getBuildingLookUpMaxGm(condition);
	}

	@Override
	public Map<String, Object> getTotalUseOfSearchTypeByParam(
			String searchDateType, String supplierId, Integer locationId) {
		Map<String, String> condition = new HashMap<String, String>();
	    condition .put("searchDateType", searchDateType);
	    condition .put("supplierId", supplierId.toString());
	    condition .put("locationId", locationId.toString());
	    return getTotalUseOfSearchType(condition);
	}

}
