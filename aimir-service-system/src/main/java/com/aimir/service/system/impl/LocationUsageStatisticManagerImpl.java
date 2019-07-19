package com.aimir.service.system.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.KGOE;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.PeakType;
import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.dao.mvm.MonthWMDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractCapacityDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.DeviceVendorDao;
import com.aimir.dao.system.EnergySavingGoalDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffGMDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.dao.system.TariffWMDao;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.Meter;
import com.aimir.model.system.AverageUsageBase;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.EnergySavingGoal;
import com.aimir.model.system.Language;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffGM;
import com.aimir.model.system.TariffType;
import com.aimir.model.system.TariffWM;
import com.aimir.service.system.LocationUsageStatisticManager;
import com.aimir.util.BemsStatisticUtil;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;


@WebService(endpointInterface = "com.aimir.service.system.LocationUsageStatisticManager")
@Service(value = "locationUsageStatisticManager")
@Transactional
public class LocationUsageStatisticManagerImpl implements
		LocationUsageStatisticManager {
	Log log = LogFactory.getLog(getClass());
	
	@Autowired
	ContractCapacityDao contractCapacityDao;

	@Autowired
	DayEMDao dayemDao;

	@Autowired
	DayGMDao daygmDao;

	@Autowired
	DayWMDao daywmDao;

	@Autowired
	MonthEMDao monthemDao;

	@Autowired
	MonthGMDao monthgmDao;

	@Autowired
	MonthWMDao monthwmDao;

	@Autowired
	MeteringDayDao meteringDayDao;

	@Autowired
	MeterDao meterDao;

	@Autowired
	LocationDao locationDao;

	@Autowired
	DeviceModelDao deviceModelDao;

	@Autowired
	DeviceVendorDao deviceVendorDao;

	@Autowired
	CodeDao codeDao;

	@Autowired
	TariffTypeDao tariffTypeDao;

	@Autowired
	TariffEMDao tariffEmDao;

	@Autowired
	TariffGMDao tariffGmDao;

	@Autowired
	TariffWMDao tariffWmDao;

	@Autowired
	SeasonDao seasonDao;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	EnergySavingGoalDao energySavingGoalDao;

	@Autowired
	EndDeviceDao endDeviceDao;

	private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
	
	public BigDecimal getPercent(Object usage, Object goal) {
		BigDecimal bUsage = null;
		if (usage instanceof BigDecimal) {
			bUsage = ((BigDecimal) usage).setScale(BigDecimal.ROUND_DOWN, 2);
		} else {
			bUsage = new BigDecimal((Integer) usage);
		}
		BigDecimal bGoal = null;
		if (goal instanceof BigDecimal) {
			bGoal = ((BigDecimal) goal).setScale(BigDecimal.ROUND_DOWN, 2);
		} else {
			bGoal = new BigDecimal((Integer) goal);
		}

		BigDecimal zero = new BigDecimal(0);
		BigDecimal maxPercent = new BigDecimal(100);

		if (zero.compareTo(bUsage) >= 0) {
			return zero;
		}

		if (zero.compareTo(bGoal) >= 0) {
			return maxPercent;
		}

		BigDecimal percent = bUsage.divide(bGoal, MathContext.DECIMAL32)
				.setScale(BigDecimal.ROUND_DOWN, 2).multiply(maxPercent);
		// System.out.println("percent.intValue():::"+percent.intValue());
		if (percent.intValue() > 200) {
			return new BigDecimal(172);
		}
		if (percent.intValue() <= 200 && percent.intValue() > 175) {
			return new BigDecimal(160);
		}

		if (percent.intValue() <= 175 && percent.intValue() > 150) {
			return new BigDecimal(145);
		}

		if (percent.intValue() <= 150 && percent.intValue() > 125) {
			return new BigDecimal(130);
		}

		if (percent.intValue() <= 125 && percent.intValue() > 100) {
			return new BigDecimal(110);
		}

		return percent;
	}

	public Map<String, Object> getPercent(Object usage, Object goal,
			Object forecast) {
		BigDecimal bUsage = ((BigDecimal) usage).setScale(
				BigDecimal.ROUND_DOWN, 2);
		BigDecimal bGoal = ((BigDecimal) goal).setScale(BigDecimal.ROUND_DOWN,
				2);
		BigDecimal bForecast = ((BigDecimal) forecast).setScale(
				BigDecimal.ROUND_DOWN, 2);

		BigDecimal max = new BigDecimal(0);
		BigDecimal maxPercent = new BigDecimal(100);
		BigDecimal scale = new BigDecimal(0.8);
		max = bUsage;

		if (max.compareTo(bGoal) <= 0) {
			max = bGoal;
		}
		if (max.compareTo(bForecast) <= 0) {
			max = bForecast;
		}
		Map<String, Object> retMap = new HashMap<String, Object>();

		if (max.intValue() == 0) {
			retMap.put("usagePercent", max);
			retMap.put("goalPercent", max);
			retMap.put("forecastPercent", max);
			return retMap;
		}

		BigDecimal usagePercent = bUsage.divide(max, MathContext.DECIMAL32)
				.setScale(BigDecimal.ROUND_DOWN, 2).multiply(maxPercent);
		BigDecimal goalPercent = bGoal.divide(max, MathContext.DECIMAL32)
				.setScale(BigDecimal.ROUND_DOWN, 2).multiply(maxPercent);
		BigDecimal forecastPercent = bForecast.divide(max,
				MathContext.DECIMAL32).setScale(BigDecimal.ROUND_DOWN, 2)
				.multiply(maxPercent);

		retMap.put("usagePercent", usagePercent);
		retMap.put("goalPercent", goalPercent);
		retMap.put("forecastPercent", forecastPercent);
		return retMap;
	}

	public Map<String, Object> getExbitionUsage(int loc) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();

		try {
			String date = CalendarUtil.getCurrentDate();

			// int locationId = locationDao.getRoot().get(0);
			// List<Integer> locIdList =
			// locationDao.getChildLocationId(locationId);

			params.put("locationId", loc);
			params.put("root", true);
			params.put("endDate", date);
			params.put("periodType", CommonConstants.DateType.HOURLY.getCode());

			String dailyStartDate = CalendarUtil.getDateWithoutFormat(date,
					Calendar.DATE, -7);

			params.put("startDate", dailyStartDate);

			Map<String, Object> daily = getExihibitionPeriodUsage(params);

			params.put("periodType", CommonConstants.DateType.DAILY.getCode());

			Map<String, Object> weekly = getExihibitionPeriodUsage(params);

			params
					.put("periodType", CommonConstants.DateType.MONTHLY
							.getCode());
			params.put("startDate", date.substring(0, 6));
			params.put("endDate", date.substring(0, 6));
			Map<String, Object> monthly = getExihibitionPeriodUsage(params);
			List<EnergySavingGoal> energyGoalList = energySavingGoalDao
					.getEnergySavingGoalListBystartDate(date, 11);
			EnergySavingGoal energyGoal = energyGoalList.get(0);

			List<AverageUsageBase> baseList = energyGoal.getAverageUsage()
					.getBases();
			BigDecimal emGoal = null;
			BigDecimal gmGoal = null;
			BigDecimal wmGoal = null;
			BigDecimal hmGoal = null;
			
			BigDecimal dailyDivide = new BigDecimal(365);
			BigDecimal weeklyDivide = new BigDecimal(52);
			BigDecimal monthlyDivide = new BigDecimal(12);
			
			for (AverageUsageBase base : baseList) {
				Integer supplyType = base.getSupplyType();
				if (supplyType == 0) {
					emGoal = new BigDecimal(base.getUsageValue()).setScale(
							BigDecimal.ROUND_DOWN, 2);
				} else if (supplyType == 1) {
					gmGoal = new BigDecimal(base.getUsageValue()).setScale(
							BigDecimal.ROUND_DOWN, 2);
				} else if (supplyType == 2) {
					wmGoal = new BigDecimal(base.getUsageValue()).setScale(
							BigDecimal.ROUND_DOWN, 2);
				}  else if (supplyType == 3) {
					hmGoal = new BigDecimal(base.getUsageValue()).setScale(
							BigDecimal.ROUND_DOWN, 2);
				}
			}
			BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
			
			Map<String, Object> chargeMap = new HashMap<String, Object>();
			
			daily.put("EmGoal", emGoal.divide(dailyDivide,
					MathContext.DECIMAL32));
			chargeMap.put("serviceType", MeterType.EnergyMeter.getServiceType());
			chargeMap.put("dateType", DateType.DAILY.getCode());
			chargeMap.put("usage", ((BigDecimal) daily.get("EmGoal")).doubleValue());
			chargeMap.put("period", 1);
			daily.put("EmGoalBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());
			
			daily.put("GmGoal", gmGoal.divide(dailyDivide,
					MathContext.DECIMAL32));
			chargeMap.put("serviceType", MeterType.GasMeter.getServiceType());
			chargeMap.put("usage", ((BigDecimal) daily.get("GmGoal"))
					.doubleValue());
			daily.put("GmGoalBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());
			
			daily.put("WmGoal", wmGoal.divide(dailyDivide,
					MathContext.DECIMAL32));
			chargeMap.put("serviceType", MeterType.WaterMeter.getServiceType());
			chargeMap.put("usage", ((BigDecimal) daily.get("WmGoal"))
					.doubleValue());
			daily.put("WmGoalBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());
			
			daily.put("HmGoal", hmGoal.divide(dailyDivide,
					MathContext.DECIMAL32));
			chargeMap.put("serviceType", MeterType.HeatMeter.getServiceType());
			chargeMap.put("usage", ((BigDecimal) daily.get("HmGoal"))
					.doubleValue());
			daily.put("HmGoalBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());
			
			BigDecimal forecastScale = new BigDecimal(0.9);
			
			daily.put("EmForecast", emGoal.divide(dailyDivide,
					MathContext.DECIMAL32).multiply(forecastScale).setScale(
					BigDecimal.ROUND_DOWN, 2));
			daily.put("GmForecast", gmGoal.divide(dailyDivide,
					MathContext.DECIMAL32).multiply(forecastScale).setScale(
					BigDecimal.ROUND_DOWN, 2));
			daily.put("WmForecast", wmGoal.divide(dailyDivide,
					MathContext.DECIMAL32).multiply(forecastScale).setScale(
					BigDecimal.ROUND_DOWN, 2));
			daily.put("HmForecast", hmGoal.divide(dailyDivide,
					MathContext.DECIMAL32).multiply(forecastScale).setScale(
					BigDecimal.ROUND_DOWN, 2));
			
			daily.put("TotalForecast", daily.get("TotalUsage"));
			daily.put("TotalGoal", emGoal.divide(dailyDivide,MathContext.DECIMAL32).add(
				gmGoal.divide(dailyDivide, MathContext.DECIMAL32).add(
					wmGoal.divide(dailyDivide,MathContext.DECIMAL32).add(
							hmGoal.divide(dailyDivide,MathContext.DECIMAL32)))));

			Map<String, Object> dailyPercentMap = getPercent(daily
					.get("TotalUsage"), daily.get("TotalGoal"), daily
					.get("TotalForecast"));

			daily.put("TotalUsagePercent", dailyPercentMap.get("usagePercent"));
			daily.put("TotalGoalPercent", dailyPercentMap.get("goalPercent"));
			daily.put("TotalForecastPercent", dailyPercentMap
					.get("forecastPercent"));

			Map<String, Object> dailyEmPercentMap = getPercent(daily
					.get("EmUsage"), daily.get("EmGoal"), daily
					.get("EmForecast"));
			daily.put("EmUsagePercent", dailyEmPercentMap.get("usagePercent"));
			daily.put("EmGoalPercent", dailyEmPercentMap.get("goalPercent"));
			daily.put("EmForecastPercent", dailyEmPercentMap
					.get("forecastPercent"));

			Map<String, Object> dailyGmPercentMap = getPercent(daily
					.get("GmUsage"), daily.get("GmGoal"), daily
					.get("GmForecast"));
			daily.put("GmUsagePercent", dailyGmPercentMap.get("usagePercent"));
			daily.put("GmGoalPercent", dailyGmPercentMap.get("goalPercent"));
			daily.put("GmForecastPercent", dailyGmPercentMap
					.get("forecastPercent"));

			Map<String, Object> dailyWmPercentMap = getPercent(daily
					.get("WmUsage"), daily.get("WmGoal"), daily
					.get("WmForecast"));
			daily.put("WmUsagePercent", dailyWmPercentMap.get("usagePercent"));
			daily.put("WmGoalPercent", dailyWmPercentMap.get("goalPercent"));
			daily.put("WmForecastPercent", dailyWmPercentMap
					.get("forecastPercent"));
			
			Map<String, Object> dailyHmPercentMap = getPercent(daily
					.get("HmUsage"), daily.get("HmGoal"), daily
					.get("HmForecast"));
			daily.put("HmUsagePercent", dailyHmPercentMap.get("usagePercent"));
			daily.put("HmGoalPercent", dailyHmPercentMap.get("goalPercent"));
			daily.put("HmForecastPercent", dailyHmPercentMap
					.get("forecastPercent"));

			Map<String, Object> dailyToePercentMap = getPercent(daily
					.get("EmToe"), daily.get("WmToe"), daily.get("GmToe"));
			daily.put("EmToePercent", dailyToePercentMap.get("usagePercent"));
			daily.put("WmToePercent", dailyToePercentMap.get("goalPercent"));
			daily.put("GmToePercent", dailyToePercentMap.get("forecastPercent"));

			weekly.put("EmGoal", emGoal.divide(weeklyDivide,
					MathContext.DECIMAL32));

			chargeMap.put("serviceType", MeterType.EnergyMeter.getServiceType());
			chargeMap.put("dateType", DateType.DAILY.getCode());
			chargeMap.put("usage", ((BigDecimal) weekly.get("EmGoal")).doubleValue());
			chargeMap.put("period", 7);
			weekly.put("EmGoalBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());

			weekly.put("GmGoal", gmGoal.divide(weeklyDivide,
					MathContext.DECIMAL32));
			chargeMap.put("serviceType", MeterType.GasMeter.getServiceType());
			chargeMap.put("usage", ((BigDecimal) weekly.get("GmGoal"))
					.doubleValue());
			weekly.put("GmGoalBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());
			
			weekly.put("WmGoal", wmGoal.divide(weeklyDivide,
					MathContext.DECIMAL32));
			chargeMap.put("serviceType", MeterType.WaterMeter.getServiceType());
			chargeMap.put("usage", ((BigDecimal) weekly.get("WmGoal"))
					.doubleValue());
			weekly.put("WmGoalBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());
			
			weekly.put("HmGoal", hmGoal.divide(weeklyDivide,
					MathContext.DECIMAL32));
			chargeMap.put("serviceType", MeterType.HeatMeter.getServiceType());
			chargeMap.put("usage", ((BigDecimal) weekly.get("HmGoal"))
					.doubleValue());
			weekly.put("HmGoalBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());
			
			weekly.put("EmForecast", emGoal.divide(weeklyDivide,
					MathContext.DECIMAL32).multiply(forecastScale).setScale(
					BigDecimal.ROUND_DOWN, 2));
			weekly.put("GmForecast", gmGoal.divide(weeklyDivide,
					MathContext.DECIMAL32).multiply(forecastScale).setScale(
					BigDecimal.ROUND_DOWN, 2));
			weekly.put("WmForecast", wmGoal.divide(weeklyDivide,
					MathContext.DECIMAL32).multiply(forecastScale).setScale(
					BigDecimal.ROUND_DOWN, 2));
			weekly.put("HmForecast", hmGoal.divide(weeklyDivide,
					MathContext.DECIMAL32).multiply(forecastScale).setScale(
					BigDecimal.ROUND_DOWN, 2));
			
			weekly.put("TotalForecast", weekly.get("TotalUsage"));

			weekly.put("TotalGoal", emGoal.divide(weeklyDivide,MathContext.DECIMAL32)
					.add(gmGoal.divide(weeklyDivide, MathContext.DECIMAL32)
						.add(wmGoal.divide(weeklyDivide,MathContext.DECIMAL32)
								.add(hmGoal.divide(weeklyDivide,MathContext.DECIMAL32)))));

			Map<String, Object> weeklyPercentMap = getPercent(weekly.get("TotalUsage"), weekly.get("TotalGoal"), 
					weekly.get("TotalForecast"));
			weekly.put("TotalUsagePercent", weeklyPercentMap.get("usagePercent"));
			weekly.put("TotalGoalPercent", weeklyPercentMap.get("goalPercent"));
			weekly.put("TotalForecastPercent", weeklyPercentMap.get("forecastPercent"));
			
			Map<String, Object> weeklyEmPercentMap = getPercent(weekly.get("EmUsage"), weekly.get("EmGoal"), 
					weekly.get("EmForecast"));
			weekly.put("EmUsagePercent", weeklyEmPercentMap.get("usagePercent"));
			weekly.put("EmGoalPercent", weeklyEmPercentMap.get("goalPercent"));
			weekly.put("EmForecastPercent", weeklyEmPercentMap.get("forecastPercent"));

			Map<String, Object> weeklyGmPercentMap = getPercent(weekly.get("GmUsage"), 
					weekly.get("GmGoal"), weekly.get("GmForecast"));
			weekly.put("GmUsagePercent", weeklyGmPercentMap.get("usagePercent"));
			weekly.put("GmGoalPercent", weeklyGmPercentMap.get("goalPercent"));
			weekly.put("GmForecastPercent", weeklyGmPercentMap.get("forecastPercent"));

			Map<String, Object> weeklyWmPercentMap = getPercent(weekly.get("WmUsage"),
					weekly.get("WmGoal"), weekly.get("WmForecast"));
			weekly.put("WmUsagePercent", weeklyWmPercentMap.get("usagePercent"));
			weekly.put("WmGoalPercent", weeklyWmPercentMap.get("goalPercent"));
			weekly.put("WmForecastPercent", weeklyWmPercentMap.get("forecastPercent"));
			
			Map<String, Object> weeklyHmPercentMap = getPercent(weekly.get("HmUsage"), 
					weekly.get("HmGoal"), weekly.get("HmForecast"));
			weekly.put("HmUsagePercent", weeklyHmPercentMap.get("usagePercent"));
			weekly.put("HmGoalPercent", weeklyHmPercentMap.get("goalPercent"));
			weekly.put("HmForecastPercent", weeklyHmPercentMap.get("forecastPercent"));

			Map<String, Object> weeklyToePercentMap = getPercent(weekly.get("EmToe"), 
					weekly.get("WmToe"), weekly.get("GmToe"));
			weekly.put("EmToePercent", weeklyToePercentMap.get("usagePercent"));
			weekly.put("WmToePercent", weeklyToePercentMap.get("goalPercent"));
			weekly.put("GmToePercent", weeklyToePercentMap.get("forecastPercent"));

			monthly.put("EmGoal", emGoal.divide(monthlyDivide,
					MathContext.DECIMAL32));
			chargeMap.put("serviceType", MeterType.EnergyMeter.getServiceType());
			chargeMap.put("dateType", DateType.MONTHLY.getCode());
			chargeMap.put("usage", ((BigDecimal) monthly.get("EmGoal")).doubleValue());
			chargeMap.put("period", 1);
			monthly.put("EmGoalBillUsage", new BigDecimal((Double) bemsUtil.getUsageCharge(chargeMap)).intValue());
			
			monthly.put("GmGoal", gmGoal.divide(monthlyDivide,MathContext.DECIMAL32));
			chargeMap.put("serviceType", MeterType.GasMeter.getServiceType());
			chargeMap.put("usage", ((BigDecimal) monthly.get("GmGoal")).doubleValue());
			monthly.put("GmGoalBillUsage", new BigDecimal((Double) bemsUtil.getUsageCharge(chargeMap)).intValue());
			
			monthly.put("WmGoal", wmGoal.divide(monthlyDivide,MathContext.DECIMAL32));
			chargeMap.put("serviceType", MeterType.WaterMeter.getServiceType());
			chargeMap.put("usage", ((BigDecimal) monthly.get("WmGoal")).doubleValue());
			monthly.put("WmGoalBillUsage", new BigDecimal((Double) bemsUtil.getUsageCharge(chargeMap)).intValue());
			
			monthly.put("HmGoal", hmGoal.divide(monthlyDivide,MathContext.DECIMAL32));
			chargeMap.put("serviceType", MeterType.HeatMeter.getServiceType());
			chargeMap.put("usage", ((BigDecimal) monthly.get("HmGoal")).doubleValue());
			monthly.put("HmGoalBillUsage", new BigDecimal((Double) bemsUtil.getUsageCharge(chargeMap)).intValue());
			
			monthly.put("EmForecast", emGoal.divide(monthlyDivide,
					MathContext.DECIMAL32).multiply(forecastScale).setScale(
					BigDecimal.ROUND_DOWN, 2));
			monthly.put("GmForecast", gmGoal.divide(monthlyDivide,
					MathContext.DECIMAL32).multiply(forecastScale).setScale(
					BigDecimal.ROUND_DOWN, 2));
			monthly.put("WmForecast", wmGoal.divide(monthlyDivide,
					MathContext.DECIMAL32).multiply(forecastScale).setScale(
					BigDecimal.ROUND_DOWN, 2));
			monthly.put("HmForecast", hmGoal.divide(monthlyDivide,
					MathContext.DECIMAL32).multiply(forecastScale).setScale(
					BigDecimal.ROUND_DOWN, 2));
			
			monthly.put("TotalGoal", emGoal.divide(monthlyDivide,MathContext.DECIMAL32).add(
					gmGoal.divide(monthlyDivide, MathContext.DECIMAL32).add(
						wmGoal.divide(monthlyDivide,MathContext.DECIMAL32).add(
								hmGoal.divide(monthlyDivide,MathContext.DECIMAL32)))));
			monthly.put("TotalForecast", monthly.get("TotalUsage"));

			Map<String, Object> monthlyPercentMap = getPercent(monthly
					.get("TotalUsage"), monthly.get("TotalGoal"), monthly
					.get("TotalForecast"));

			monthly.put("TotalUsagePercent", monthlyPercentMap
					.get("usagePercent"));
			monthly.put("TotalGoalPercent", monthlyPercentMap
					.get("goalPercent"));
			monthly.put("TotalForecastPercent", monthlyPercentMap
					.get("forecastPercent"));

			Map<String, Object> monthlyEmPercentMap = getPercent(monthly.get("EmUsage"), 
					monthly.get("EmGoal"), monthly.get("EmForecast"));
			monthly.put("EmUsagePercent", monthlyEmPercentMap.get("usagePercent"));
			monthly.put("EmGoalPercent", monthlyEmPercentMap.get("goalPercent"));
			monthly.put("EmForecastPercent", monthlyEmPercentMap.get("forecastPercent"));

			Map<String, Object> monthlyGmPercentMap = getPercent(monthly.get("GmUsage"), 
					monthly.get("GmGoal"), monthly.get("GmForecast"));
			monthly.put("GmUsagePercent", monthlyGmPercentMap.get("usagePercent"));
			monthly.put("GmGoalPercent", monthlyGmPercentMap.get("goalPercent"));
			monthly.put("GmForecastPercent", monthlyGmPercentMap.get("forecastPercent"));

			Map<String, Object> monthlyWmPercentMap = getPercent(monthly.get("WmUsage"), 
					monthly.get("WmGoal"), monthly.get("WmForecast"));
			monthly.put("WmUsagePercent", monthlyWmPercentMap.get("usagePercent"));
			monthly.put("WmGoalPercent", monthlyWmPercentMap.get("goalPercent"));
			monthly.put("WmForecastPercent", monthlyWmPercentMap.get("forecastPercent"));
			
			Map<String, Object> monthlyHmPercentMap = getPercent(monthly.get("HmUsage"),
					monthly.get("HmGoal"), monthly.get("HmForecast"));
			monthly.put("HmUsagePercent", monthlyHmPercentMap.get("usagePercent"));
			monthly.put("HmGoalPercent", monthlyHmPercentMap.get("goalPercent"));
			monthly.put("HmForecastPercent", monthlyHmPercentMap.get("forecastPercent"));
			
			Map<String, Object> monthlyToePercentMap = getPercent(monthly
					.get("EmToe"), monthly.get("WmToe"), monthly.get("GmToe"));
			monthly.put("EmToePercent", monthlyToePercentMap.get("usagePercent"));
			monthly.put("WmToePercent", monthlyToePercentMap.get("goalPercent"));
			monthly.put("GmToePercent", monthlyToePercentMap.get("forecastPercent"));
	

			retMap.put("daily", daily);
			retMap.put("weekly", weekly);
			retMap.put("monthly", monthly);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return retMap;
	}

	public Map<String, Object> getLocationExbitionUsage() {
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> total = new HashMap<String, Object>();

		Map<String, Object> params = new HashMap<String, Object>();

		try {
			String date = CalendarUtil.getCurrentDate();
			// date ="20100521";;
			int locationId = locationDao.getRoot().get(0);
			params.put("locationId", locationId);
			params.put("root", true);
			params.put("endDate", date);

			total = getLocationExbitionUsage(params);

			Location rootLocation = locationDao.get(locationId);

			total.put("name", rootLocation.getName());
			Set<Location> childLocation = rootLocation.getChildren();
			params.put("root", false);
			int i = 0;

			Iterator<Location> childIterator = childLocation.iterator();

			while (childIterator.hasNext()) {
				Location child = childIterator.next();
				params.put("endDate", date);
				Map<String, Object> position = new HashMap<String, Object>();
				params.put("locationId", child.getId());
				position = getLocationExbitionUsage(params);
				position.put("name", child.getName());

				retMap.put("position" + i, position);
				Set<Location> subChildLocation = child.getChildren();
				Iterator<Location> subChildIterator = subChildLocation
						.iterator();

				// System.out.println("position"+i+"child.getName():"+child.getName());
				while (subChildIterator.hasNext()) {
					i++;
					Location subChild = subChildIterator.next();
					params.put("endDate", date);
					Map<String, Object> subPosition = new HashMap<String, Object>();
					params.put("locationId", subChild.getId());
					subPosition = getLocationExbitionUsage(params);
					subPosition.put("name", subChild.getName());
					retMap.put("position" + i, subPosition);

				}

				i++;
			}

			retMap.put("total", total);
			// System.out.println("retMap result:" + retMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retMap;
	}

	public Map<String, Object> getLocationExbitionUsage(
			Map<String, Object> params) {
		Map<String, Object> retMap = new HashMap<String, Object>();

		try {
			String date = (String) params.get("endDate");
			params.put("periodType", CommonConstants.DateType.HOURLY.getCode());
			String dailyStartDate = CalendarUtil.getDateWithoutFormat(date,
					Calendar.DATE, -7);
			
			params.put("startDate", dailyStartDate);
			Map<String, Object> daily = getLocationExihibitionPeriodUsage(params);
			
			params.put("periodType", CommonConstants.DateType.DAILY.getCode());
			Map<String, Object> weekly = getLocationExihibitionPeriodUsage(params);
			
			params.put("periodType", CommonConstants.DateType.MONTHLY.getCode());
			Map<String, Object> monthly = getLocationExihibitionPeriodUsage(params);

			List<EnergySavingGoal> energyGoalList = energySavingGoalDao
					.getEnergySavingGoalListBystartDate(date, 5);
			EnergySavingGoal energyGoal = null;
			List<AverageUsageBase> baseList = new ArrayList<AverageUsageBase>();
			if (energyGoalList.size() > 0) {
				energyGoal = energyGoalList.get(0);
				baseList = energyGoal.getAverageUsage().getBases();
			}

			BigDecimal emGoal = new BigDecimal(0);

			BigDecimal dailyDivide = new BigDecimal(365);
			BigDecimal weeklyDivide = new BigDecimal(52);
			BigDecimal monthlyDivide = new BigDecimal(12);

			for (AverageUsageBase base : baseList) {
				Integer supplyType = base.getSupplyType();
				if (supplyType == 0) {
					emGoal = new BigDecimal(base.getUsageValue()).setScale(
							BigDecimal.ROUND_DOWN, 2);
				}
			}

			BigDecimal dilyEmGoal = emGoal.divide(dailyDivide,
					MathContext.DECIMAL32);
			BigDecimal weeklyEmGoal = emGoal.divide(weeklyDivide,
					MathContext.DECIMAL32);
			BigDecimal monthlyEmGoal = emGoal.divide(monthlyDivide,
					MathContext.DECIMAL32);

			daily.put("percent", getPercent(daily.get("EmUsage"), dilyEmGoal));
			weekly.put("percent", getPercent(weekly.get("EmUsage"),weeklyEmGoal));
			monthly.put("percent", getPercent(monthly.get("EmUsage"),monthlyEmGoal));

			BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
			Map<String, Object> chargeMap = new HashMap<String, Object>();
			chargeMap.put("serviceType", MeterType.EnergyMeter.getServiceType());
			chargeMap.put("dateType", DateType.DAILY.getCode());
			chargeMap.put("usage", dilyEmGoal.doubleValue());
			chargeMap.put("period", 1);

			daily.put("EmGoal", dilyEmGoal.intValue());
			daily.put("EmGoalBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());

			// chargeMap.put("dateType", DateType.WEEKLY.getCode());
			chargeMap.put("usage", weeklyEmGoal.doubleValue());
			chargeMap.put("period", 7);

			weekly.put("EmGoal", weeklyEmGoal.intValue());
			weekly.put("EmGoalBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());

			chargeMap.put("dateType", DateType.MONTHLY.getCode());
			chargeMap.put("usage", monthlyEmGoal.doubleValue());
			chargeMap.put("period", 1);

			monthly.put("EmGoal", monthlyEmGoal.intValue());
			monthly.put("EmGoalBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());

			// daily.put("percent",getPercent(new BigDecimal(10),new
			// BigDecimal(100)));
			// weekly.put("percent",getPercent(new BigDecimal(100),new
			// BigDecimal(10)));
			// monthly.put("percent",getPercent(new BigDecimal(50),new
			// BigDecimal(80)));

			retMap.put("daily", daily);
			retMap.put("weekly", weekly);
			retMap.put("monthly", monthly);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return retMap;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getLocationExihibitionPeriodUsage(
			Map<String, Object> params) {

		String periodType = (String) params.get("periodType");

		BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
		params.put("dst", 0);
		Map<String, Object> tmp = null;
		String oriEndDate = (String) params.get("endDate");
		String oriStartDate = (String) params.get("startDate");

		if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> emList = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("channel", DefaultChannel.Co2.getCode());
			List<Object> emCo2List = meteringDayDao
					.getUsageForLocationByDay(params);
			String preDate = CalendarUtil.getDateWithoutFormat(oriEndDate,
					Calendar.DATE, -1);
			params.put("endDate", preDate);

			List<Object> preEmList = meteringDayDao
					.getUsageForLocationByDay(params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> preEmMap = new HashMap<String, Object>();
			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			if (emList != null && emList.size() > 0) {
				emMap = (Map<String, Object>) emList.get(0);
			}

			if (preEmList != null && preEmList.size() > 0) {
				preEmMap = (Map<String, Object>) preEmList.get(0);
			}

			if (emCo2List != null && emCo2List.size() > 0) {
				emCo2Map = (Map<String, Object>) emCo2List.get(0);
			}

			tmp = new HashMap<String, Object>();

			BigDecimal emUsage = new BigDecimal(emMap.get("TOTAL") == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emMap.get("TOTAL"))).setScale(
					BigDecimal.ROUND_DOWN, 2);
			tmp.put("EmUsage", emUsage.intValue());

			tmp.put("preEmUsage", new BigDecimal(
					preEmMap.get("TOTAL") == null ? 0 : DecimalUtil.ConvertNumberToDouble(preEmMap
							.get("TOTAL"))).setScale(BigDecimal.ROUND_DOWN, 2)
					.intValue());

			Map<String, Object> chargeMap = new HashMap<String, Object>();
			chargeMap
					.put("serviceType", MeterType.EnergyMeter.getServiceType());
			chargeMap.put("dateType", DateType.DAILY.getCode());
			chargeMap.put("usage", emUsage.doubleValue());
			chargeMap.put("period", 1);
			tmp.put("EmBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());
			tmp.put("Co2Usage", new BigDecimal(
					emCo2Map.get("TOTAL") == null ? 0 : DecimalUtil.ConvertNumberToDouble(emCo2Map
							.get("TOTAL"))).intValue());

		} else if (CommonConstants.DateType.DAILY.getCode().equals(periodType)) {

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());

			List<Object> emList = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("channel", DefaultChannel.Co2.getCode());

			List<Object> emCo2List = meteringDayDao
					.getUsageForLocationByDay(params);

			String preStartDate = CalendarUtil.getDateWithoutFormat(oriEndDate,
					Calendar.DATE, -14);

			String preEndDate = CalendarUtil.getDateWithoutFormat(oriEndDate,
					Calendar.DATE, -7);

			params.put("startDate", preStartDate);
			params.put("endDate", preEndDate);

			List<Object> preEmList = meteringDayDao
					.getUsageForLocationByDay(params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> preEmMap = new HashMap<String, Object>();
			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			for (Object obj : emList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				emMap.put((String) emtmp.get("YYYYMMDD"), emtmp.get("TOTAL"));
			}

			for (Object obj : preEmList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				preEmMap
						.put((String) emtmp.get("YYYYMMDD"), emtmp.get("TOTAL"));
			}

			for (Object obj : emCo2List) {
				Map<String, Object> emco2tmp = new HashMap<String, Object>();
				emco2tmp = (Map<String, Object>) obj;
				emCo2Map.put((String) emco2tmp.get("YYYYMMDD"), emco2tmp
						.get("TOTAL"));
			}

			BigDecimal emVal = new BigDecimal(0);
			BigDecimal preEmVal = new BigDecimal(0);
			BigDecimal co2Val = new BigDecimal(0);

			for (int i = 0; i < 7; i++) {
				String yyyymmdd = CalendarUtil.getDateWithoutFormat(
						oriStartDate, Calendar.DATE, i);

				tmp = new HashMap<String, Object>();

				emVal = emVal.add(new BigDecimal(
						emMap.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(emMap
								.get(yyyymmdd))).setScale(BigDecimal.ROUND_DOWN,
						2));

				preEmVal = preEmVal.add(new BigDecimal(
						preEmMap.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(preEmMap
								.get(yyyymmdd))).setScale(BigDecimal.ROUND_DOWN,
						2));

				co2Val = co2Val.add(new BigDecimal(
						emCo2Map.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(emCo2Map
								.get(yyyymmdd))));

			}

			Map<String, Object> chargeMap = new HashMap<String, Object>();
			chargeMap
					.put("serviceType", MeterType.EnergyMeter.getServiceType());
			chargeMap.put("dateType", DateType.DAILY.getCode());
			chargeMap.put("usage", emVal.doubleValue());
			chargeMap.put("period", 7);
			tmp.put("EmBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());
			tmp.put("EmUsage", emVal.intValue());
			tmp.put("preEmUsage", preEmVal.intValue());
			tmp.put("Co2Usage", co2Val.intValue());

		} else if (CommonConstants.DateType.MONTHLY.getCode()
				.equals(periodType)) {

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthTableName());
			params.put("periodType", CommonConstants.DateType.HOURLY.getCode());

			String yyyymm = oriEndDate.substring(0, 6);
			params.put("startDate", yyyymm);
			params.put("endDate", yyyymm);

			List<Object> emList = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("channel", DefaultChannel.Co2.getCode());

			List<Object> emCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);

			String preEndDate = CalendarUtil.getDateWithoutFormat(oriEndDate,
					Calendar.MONTH, -1);

			params.put("startDate", preEndDate.substring(0, 6));
			params.put("endDate", preEndDate.substring(0, 6));
			List<Object> preEmList = meteringDayDao
					.getUsageForLocationByMonth(params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> preEmMap = new HashMap<String, Object>();
			Map<String, Object> emCo2Map = new HashMap<String, Object>();

			for (Object obj : emList) {
				Map<String, Object> emtmp = (Map<String, Object>) obj;
				emMap.put((String) emtmp.get("YYYYMM"), emtmp.get("TOTAL"));
			}

			for (Object obj : preEmList) {
				Map<String, Object> emtmp = (Map<String, Object>) obj;
				preEmMap.put((String) emtmp.get("YYYYMM"), emtmp.get("TOTAL"));
			}

			for (Object obj : emCo2List) {
				Map<String, Object> emco2tmp = (Map<String, Object>) obj;
				emCo2Map.put((String) emco2tmp.get("YYYYMM"), emco2tmp
						.get("TOTAL"));
			}
			tmp = new HashMap<String, Object>();

			BigDecimal emUsage = new BigDecimal(emMap.get(yyyymm) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emMap.get(yyyymm))).setScale(
					BigDecimal.ROUND_DOWN, 2);
			tmp.put("EmUsage", emUsage.intValue());

			tmp.put("preEmUsage", new BigDecimal(
					preEmMap.get(yyyymm) == null ? 0 : DecimalUtil.ConvertNumberToDouble(preEmMap
							.get(yyyymm))).setScale(BigDecimal.ROUND_DOWN, 2)
					.intValue());

			Map<String, Object> chargeMap = new HashMap<String, Object>();
			chargeMap
					.put("serviceType", MeterType.EnergyMeter.getServiceType());
			chargeMap.put("dateType", DateType.MONTHLY.getCode());
			chargeMap.put("usage", emUsage.doubleValue());
			chargeMap.put("period", 1);
			tmp.put("EmBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());
			tmp.put("Co2Usage", new BigDecimal(emCo2Map.get(yyyymm) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emCo2Map.get(yyyymm))).intValue());

		}
		return tmp;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getExihibitionPeriodUsage(
			Map<String, Object> params) {

		String periodType = (String) params.get("periodType");
		// String billPrefix = "Billing";
		BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
		params.put("dst", 0);
		Map<String, Object> tmp = null;
		if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> emList = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayTableName());
			List<Object> gmList = meteringDayDao
					.getUsageForLocationByDay(params);


			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayTableName());
			List<Object> wmList = meteringDayDao
					.getUsageForLocationByDay(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getDayTableName());
			List<Object> hmList = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("channel", DefaultChannel.Co2.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> emCo2List = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayTableName());
			List<Object> gmCo2List = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayTableName());
			List<Object> wmCo2List = meteringDayDao
					.getUsageForLocationByDay(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getDayTableName());
			List<Object> hmCo2List = meteringDayDao
					.getUsageForLocationByDay(params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> gmMap = new HashMap<String, Object>();
			Map<String, Object> wmMap = new HashMap<String, Object>();
			Map<String, Object> hmMap = new HashMap<String, Object>();

			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			Map<String, Object> gmCo2Map = new HashMap<String, Object>();
			Map<String, Object> wmCo2Map = new HashMap<String, Object>();
			Map<String, Object> hmCo2Map = new HashMap<String, Object>();

			if (emList != null && emList.size() > 0) {
				emMap = (Map<String, Object>) emList.get(0);
			}
			if (gmList != null && gmList.size() > 0) {
				gmMap = (Map<String, Object>) gmList.get(0);
			}
			if (wmList != null && wmList.size() > 0) {
				wmMap = (Map<String, Object>) wmList.get(0);
			}
			if (hmList != null && hmList.size() > 0) {
				hmMap = (Map<String, Object>) hmList.get(0);
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
			if (hmCo2List != null && hmCo2List.size() > 0) {
				hmCo2Map = (Map<String, Object>) hmCo2List.get(0);
			}

			tmp = new HashMap<String, Object>();

			tmp.put("EmUsage", new BigDecimal(emMap.get("TOTAL") == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emMap.get("TOTAL"))).setScale(
					BigDecimal.ROUND_DOWN, 2));
			tmp.put("GmUsage", new BigDecimal(gmMap.get("TOTAL") == null ? 0
					: DecimalUtil.ConvertNumberToDouble(gmMap.get("TOTAL"))).setScale(
					BigDecimal.ROUND_DOWN, 2));
			tmp.put("WmUsage", new BigDecimal(wmMap.get("TOTAL") == null ? 0
					: DecimalUtil.ConvertNumberToDouble(wmMap.get("TOTAL"))).setScale(
					BigDecimal.ROUND_DOWN, 2));
			tmp.put("HmUsage", new BigDecimal(hmMap.get("TOTAL") == null ? 0
					: DecimalUtil.ConvertNumberToDouble(hmMap.get("TOTAL"))).setScale(
					BigDecimal.ROUND_DOWN, 2));

			Map<String, Object> chargeMap = new HashMap<String, Object>();
			chargeMap
					.put("serviceType", MeterType.EnergyMeter.getServiceType());
			chargeMap.put("dateType", DateType.DAILY.getCode());
			chargeMap.put("usage", ((BigDecimal) tmp.get("EmUsage"))
					.doubleValue());
			chargeMap.put("period", 1);
			tmp.put("EmBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)));
			tmp.put("EmGoalBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());
			
			chargeMap.put("serviceType", MeterType.GasMeter.getServiceType());
			chargeMap.put("usage", ((BigDecimal) tmp.get("GmUsage"))
					.doubleValue());
			tmp.put("GmBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)));
			tmp.put("GmGoalBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());
			
			chargeMap.put("serviceType", MeterType.WaterMeter.getServiceType());
			chargeMap.put("usage", ((BigDecimal) tmp.get("WmUsage"))
					.doubleValue());
			tmp.put("WmBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)));
			tmp.put("WmGoalBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());
			
			chargeMap.put("serviceType", MeterType.HeatMeter.getServiceType());
			chargeMap.put("usage", ((BigDecimal) tmp.get("HmUsage"))
					.doubleValue());
			tmp.put("HmBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)));
			tmp.put("HmGoalBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)).intValue());
			
			tmp.put("Co2Usage", new BigDecimal(emMap.get("TOTAL") == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emMap.get("TOTAL")) * 0.424).add(
					new BigDecimal(gmMap.get("TOTAL") == null ? 0: DecimalUtil.ConvertNumberToDouble(gmMap.get("TOTAL")) * 9.95).add(
							new BigDecimal(wmMap.get("TOTAL") == null ? 0: DecimalUtil.ConvertNumberToDouble(wmMap.get("TOTAL")) * 0.332).add(
									new BigDecimal(hmMap.get("TOTAL") == null ? 0: DecimalUtil.ConvertNumberToDouble(hmMap.get("TOTAL")) * 0.424))))
					.setScale(BigDecimal.ROUND_DOWN, 2));

			tmp.put("TotalUsage", new BigDecimal(emMap.get("TOTAL") == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emMap.get("TOTAL")) * KGOE.Energy.getValue()).add(
					new BigDecimal(gmMap.get("TOTAL") == null ? 0: DecimalUtil.ConvertNumberToDouble(gmMap.get("TOTAL"))* KGOE.GasLng.getValue()).add(
							new BigDecimal(wmMap.get("TOTAL") == null ? 0: DecimalUtil.ConvertNumberToDouble(wmMap.get("TOTAL"))* KGOE.Water.getValue()).add(
									new BigDecimal(hmMap.get("TOTAL") == null ? 0: DecimalUtil.ConvertNumberToDouble(hmMap.get("TOTAL"))* KGOE.Heat.getValue()))))
							.setScale(BigDecimal.ROUND_DOWN, 2));

			tmp.put("EmToe", new BigDecimal(emMap.get("TOTAL") == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emMap.get("TOTAL")) * KGOE.Energy.getValue())
					.setScale(BigDecimal.ROUND_DOWN, 2));

			tmp.put("GmToe", new BigDecimal(gmMap.get("TOTAL") == null ? 0
					: DecimalUtil.ConvertNumberToDouble(gmMap.get("TOTAL")) * KGOE.GasLng.getValue())
					.setScale(BigDecimal.ROUND_DOWN, 2));

			tmp.put("WmToe", new BigDecimal(wmMap.get("TOTAL") == null ? 0
					: DecimalUtil.ConvertNumberToDouble(wmMap.get("TOTAL")) * KGOE.Water.getValue())
					.setScale(BigDecimal.ROUND_DOWN, 2));
			
			tmp.put("HmToe", new BigDecimal(hmMap.get("TOTAL") == null ? 0
					: DecimalUtil.ConvertNumberToDouble(hmMap.get("TOTAL")) * KGOE.Heat.getValue())
					.setScale(BigDecimal.ROUND_DOWN, 2));

		} else if (CommonConstants.DateType.DAILY.getCode().equals(periodType)) {

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> emList = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayTableName());
			List<Object> gmList = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayTableName());
			List<Object> wmList = meteringDayDao
					.getUsageForLocationByDay(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getDayTableName());
			List<Object> hmList = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("channel", DefaultChannel.Co2.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> emCo2List = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayTableName());
			List<Object> gmCo2List = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayTableName());
			List<Object> wmCo2List = meteringDayDao
					.getUsageForLocationByDay(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getDayTableName());
			List<Object> hmCo2List = meteringDayDao
					.getUsageForLocationByDay(params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> gmMap = new HashMap<String, Object>();
			Map<String, Object> wmMap = new HashMap<String, Object>();
			Map<String, Object> hmMap = new HashMap<String, Object>();

			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			Map<String, Object> gmCo2Map = new HashMap<String, Object>();
			Map<String, Object> wmCo2Map = new HashMap<String, Object>();
			Map<String, Object> hmCo2Map = new HashMap<String, Object>();

			for (Object obj : emList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				emMap.put((String) emtmp.get("YYYYMMDD"), emtmp.get("TOTAL"));
			}

			for (Object obj : gmList) {
				Map<String, Object> gmtmp = new HashMap<String, Object>();
				gmtmp = (Map<String, Object>) obj;
				gmMap.put((String) gmtmp.get("YYYYMMDD"), gmtmp.get("TOTAL"));
			}

			for (Object obj : wmList) {
				Map<String, Object> wmtmp = new HashMap<String, Object>();
				wmtmp = (Map<String, Object>) obj;
				wmMap.put((String) wmtmp.get("YYYYMMDD"), wmtmp.get("TOTAL"));
			}

			for (Object obj : hmList) {
				Map<String, Object> hmtmp = new HashMap<String, Object>();
				hmtmp = (Map<String, Object>) obj;
				hmMap.put((String) hmtmp.get("YYYYMMDD"), hmtmp.get("TOTAL"));
			}
			
			for (Object obj : emCo2List) {
				Map<String, Object> emco2tmp = new HashMap<String, Object>();
				emco2tmp = (Map<String, Object>) obj;
				emCo2Map.put((String) emco2tmp.get("YYYYMMDD"), emco2tmp
						.get("TOTAL"));

			}

			for (Object obj : gmCo2List) {
				Map<String, Object> gmco2tmp = new HashMap<String, Object>();
				gmco2tmp = (Map<String, Object>) obj;
				gmCo2Map.put((String) gmco2tmp.get("YYYYMMDD"), gmco2tmp
						.get("TOTAL"));
			}

			for (Object obj : wmCo2List) {
				Map<String, Object> wmco2tmp = new HashMap<String, Object>();
				wmco2tmp = (Map<String, Object>) obj;
				wmCo2Map.put((String) wmco2tmp.get("YYYYMMDD"), wmco2tmp
						.get("TOTAL"));
			}

			for (Object obj : hmCo2List) {
				Map<String, Object> hmco2tmp = new HashMap<String, Object>();
				hmco2tmp = (Map<String, Object>) obj;
				hmCo2Map.put((String) hmco2tmp.get("YYYYMMDD"), hmco2tmp
						.get("TOTAL"));
			}
			
			BigDecimal emVal = new BigDecimal(0);
			BigDecimal gmVal = new BigDecimal(0);
			BigDecimal wmVal = new BigDecimal(0);
			BigDecimal hmVal = new BigDecimal(0);
			BigDecimal emBillVal = new BigDecimal(0);
			BigDecimal gmBillVal = new BigDecimal(0);
			BigDecimal wmBillVal = new BigDecimal(0);
			BigDecimal hmBillVal = new BigDecimal(0);
			BigDecimal co2Val = new BigDecimal(0);
			tmp = new HashMap<String, Object>();
			for (int i = 0; i < 7; i++) {
				String yyyymmdd = CalendarUtil.getDateWithoutFormat(params
						.get("startDate")
						+ "", Calendar.DATE, i);		

				emVal = emVal.add(new BigDecimal(
						emMap.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(emMap
								.get(yyyymmdd))).setScale(BigDecimal.ROUND_DOWN,
						2));
				gmVal = gmVal.add(new BigDecimal(
						gmMap.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(gmMap
								.get(yyyymmdd))).setScale(BigDecimal.ROUND_DOWN,
						2));
				wmVal = wmVal.add(new BigDecimal(
						wmMap.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(wmMap
								.get(yyyymmdd))).setScale(BigDecimal.ROUND_DOWN,
						2));
				hmVal = hmVal.add(new BigDecimal(
						hmMap.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(hmMap
								.get(yyyymmdd))).setScale(BigDecimal.ROUND_DOWN,
						2));

				co2Val = co2Val.add(new BigDecimal(
						emCo2Map.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(emCo2Map.get(yyyymmdd))).add(
						new BigDecimal(gmCo2Map.get(yyyymmdd) == null ? 0: DecimalUtil.ConvertNumberToDouble(gmCo2Map.get(yyyymmdd))).add(
								new BigDecimal(wmCo2Map.get(yyyymmdd) == null ? 0: DecimalUtil.ConvertNumberToDouble(wmCo2Map.get(yyyymmdd))).add(
										new BigDecimal(hmCo2Map.get(yyyymmdd) == null ? 0: DecimalUtil.ConvertNumberToDouble(hmCo2Map.get(yyyymmdd))))))
						.setScale(BigDecimal.ROUND_DOWN, 2));

			}

			Map<String, Object> chargeMap = new HashMap<String, Object>();
			chargeMap
					.put("serviceType", MeterType.EnergyMeter.getServiceType());
			chargeMap.put("dateType", DateType.DAILY.getCode());
			chargeMap.put("usage", emVal.doubleValue());
			chargeMap.put("period", 7);
			tmp.put("EmBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)));
			
			chargeMap.put("serviceType", MeterType.GasMeter.getServiceType());
			chargeMap.put("usage", gmVal.doubleValue());
			tmp.put("GmBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)));

			chargeMap.put("serviceType", MeterType.WaterMeter.getServiceType());
			chargeMap.put("usage", wmVal.doubleValue());
			tmp.put("WmBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)));

			chargeMap.put("serviceType", MeterType.HeatMeter.getServiceType());
			chargeMap.put("usage", hmVal.doubleValue());
			tmp.put("HmBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)));
			
			tmp.put("EmUsage", emVal);
			tmp.put("GmUsage", gmVal);
			tmp.put("WmUsage", wmVal);
			tmp.put("HmUsage", hmVal);
			
			BigDecimal eToe = emVal.multiply(new BigDecimal(KGOE.Energy
					.getValue()));
			BigDecimal gToe = gmVal.multiply(new BigDecimal(KGOE.GasLng
					.getValue()));
			BigDecimal wToe = wmVal.multiply(new BigDecimal(KGOE.Water
					.getValue()));
			BigDecimal hToe = hmVal.multiply(new BigDecimal(KGOE.Heat
					.getValue()));

			BigDecimal eCo2 = emVal.multiply(new BigDecimal(0.424));
			BigDecimal gCo2 = gmVal.multiply(new BigDecimal(9.95));
			BigDecimal wCo2 = wmVal.multiply(new BigDecimal(0.323));
			BigDecimal hCo2 = hmVal.multiply(new BigDecimal(0.424));
			
			tmp.put("EmToe", eToe.setScale(BigDecimal.ROUND_DOWN, 2));

			tmp.put("GmToe", gToe.setScale(BigDecimal.ROUND_DOWN, 2));

			tmp.put("WmToe", wToe.setScale(BigDecimal.ROUND_DOWN, 2));
			
			tmp.put("HmToe", hToe.setScale(BigDecimal.ROUND_DOWN, 2));
			
			tmp.put("TotalUsage", eToe.add(gToe.add(wToe.add(hToe))).setScale(
					BigDecimal.ROUND_DOWN, 2));
			
//			tmp.put("WmUsage", wmVal);
			tmp.put("Co2Usage", eCo2.add(gCo2.add(wCo2.add(hCo2))).setScale(
					BigDecimal.ROUND_DOWN, 2));

		} else if (CommonConstants.DateType.MONTHLY.getCode()
				.equals(periodType)) {
			String yyyymm = (String) params.get("endDate");
			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthTableName());
			List<Object> emList = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthTableName());
			List<Object> gmList = meteringDayDao
					.getUsageForLocationByMonth(params);
			
			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getMonthTableName());
			List<Object> wmList = meteringDayDao
					.getUsageForLocationByMonth(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getMonthTableName());
			List<Object> hmList = meteringDayDao
					.getUsageForLocationByMonth(params);
			// params
			// .put("meterType", billPrefix
			// + CommonConstants.MeterType.EnergyMeter
			// .getMonthClassName());
			// List<Object> emBillList = meteringDayDao
			// .getUsageForLocationByMonth(params);
			//
			// params.put("meterType", billPrefix
			// + CommonConstants.MeterType.GasMeter.getMonthClassName());
			// List<Object> gmBillList = meteringDayDao
			// .getUsageForLocationByMonth(params);
			//
			// params.put("meterType", billPrefix
			// + CommonConstants.MeterType.WaterMeter.getMonthClassName());
			// List<Object> wmBillList = meteringDayDao
			// .getUsageForLocationByMonth(params);

			params.put("channel", DefaultChannel.Co2.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthTableName());
			List<Object> emCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthTableName());
			List<Object> gmCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getMonthTableName());
			List<Object> wmCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getMonthTableName());
			List<Object> hmCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> gmMap = new HashMap<String, Object>();
			Map<String, Object> wmMap = new HashMap<String, Object>();
			Map<String, Object> hmMap = new HashMap<String, Object>();

			// Map<String, Object> emBillMap = new HashMap<String, Object>();
			// Map<String, Object> gmBillMap = new HashMap<String, Object>();
			// Map<String, Object> wmBillMap = new HashMap<String, Object>();

			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			Map<String, Object> gmCo2Map = new HashMap<String, Object>();
			Map<String, Object> wmCo2Map = new HashMap<String, Object>();
			Map<String, Object> hmCo2Map = new HashMap<String, Object>();

			for (Object obj : emList) {
				Map<String, Object> emtmp = (Map<String, Object>) obj;
				emMap.put((String) emtmp.get("YYYYMM"), emtmp.get("TOTAL"));
			}

			for (Object obj : gmList) {
				Map<String, Object> gmtmp = (Map<String, Object>) obj;
				gmMap.put((String) gmtmp.get("YYYYMM"), gmtmp.get("TOTAL"));
			}

			for (Object obj : wmList) {
				Map<String, Object> wmtmp = (Map<String, Object>) obj;
				wmMap.put((String) wmtmp.get("YYYYMM"), wmtmp.get("TOTAL"));
			}
			
			for (Object obj : hmList) {
				Map<String, Object> hmtmp = (Map<String, Object>) obj;
				hmMap.put((String) hmtmp.get("YYYYMM"), hmtmp.get("TOTAL"));
			}

			// for (Object obj : emBillList) {
			// Map<String, Object> emBilltmp = (Map<String, Object>) obj;
			// emBillMap.put((String) emBilltmp.get("yyyymm"), emBilltmp
			// .get("total"));
			// }
			//
			// for (Object obj : gmBillList) {
			// Map<String, Object> gmBilltmp = (Map<String, Object>) obj;
			// gmBillMap.put((String) gmBilltmp.get("yyyymm"), gmBilltmp
			// .get("total"));
			// }
			//
			// for (Object obj : wmBillList) {
			// Map<String, Object> wmBilltmp = (Map<String, Object>) obj;
			// wmBillMap.put((String) wmBilltmp.get("yyyymm"), wmBilltmp
			// .get("total"));
			// }

			for (Object obj : emCo2List) {
				Map<String, Object> emco2tmp = (Map<String, Object>) obj;
				emCo2Map.put((String) emco2tmp.get("YYYYMM"), emco2tmp
						.get("TOTAL"));
			}

			for (Object obj : gmCo2List) {
				Map<String, Object> gmco2tmp = (Map<String, Object>) obj;
				gmCo2Map.put((String) gmco2tmp.get("YYYYMM"), gmco2tmp
						.get("TOTAL"));
			}

			for (Object obj : wmCo2List) {
				Map<String, Object> wmco2tmp = (Map<String, Object>) obj;
				wmCo2Map.put((String) wmco2tmp.get("YYYYMM"), wmco2tmp
						.get("TOTAL"));
			}
			
			for (Object obj : hmCo2List) {
				Map<String, Object> hmco2tmp = (Map<String, Object>) obj;
				hmCo2Map.put((String) hmco2tmp.get("YYYYMM"), hmco2tmp
						.get("TOTAL"));
			}

			tmp = new HashMap<String, Object>();

			tmp.put("EmUsage", new BigDecimal(emMap.get(yyyymm) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emMap.get(yyyymm))).setScale(
					BigDecimal.ROUND_DOWN, 2));
			tmp.put("GmUsage", new BigDecimal(gmMap.get(yyyymm) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(gmMap.get(yyyymm))).setScale(
					BigDecimal.ROUND_DOWN, 2));
			tmp.put("WmUsage", new BigDecimal(wmMap.get(yyyymm) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(wmMap.get(yyyymm))).setScale(
					BigDecimal.ROUND_DOWN, 2));
			
			tmp.put("HmUsage", new BigDecimal(hmMap.get(yyyymm) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(hmMap.get(yyyymm))).setScale(
					BigDecimal.ROUND_DOWN, 2));
			
			Map<String, Object> chargeMap = new HashMap<String, Object>();
			chargeMap
					.put("serviceType", MeterType.EnergyMeter.getServiceType());
			chargeMap.put("dateType", DateType.MONTHLY.getCode());
			chargeMap.put("usage", ((BigDecimal) tmp.get("EmUsage"))
					.doubleValue());
			chargeMap.put("period", 1);
			tmp.put("EmBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)));
			chargeMap.put("serviceType", MeterType.GasMeter.getServiceType());
			chargeMap.put("usage", ((BigDecimal) tmp.get("GmUsage"))
					.doubleValue());
			tmp.put("GmBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)));

			chargeMap.put("serviceType", MeterType.WaterMeter.getServiceType());
			chargeMap.put("usage", ((BigDecimal) tmp.get("WmUsage"))
					.doubleValue());
			tmp.put("WmBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)));
			
			chargeMap.put("serviceType", MeterType.HeatMeter.getServiceType());
			chargeMap.put("usage", ((BigDecimal) tmp.get("HmUsage"))
					.doubleValue());
			tmp.put("HmBillUsage", new BigDecimal((Double) bemsUtil
					.getUsageCharge(chargeMap)));
			// tmp.put("EmBillUsage", new BigDecimal(
			// emBillMap.get("total") == null ? 0 : (Double) emBillMap
			// .get("total")).setScale(BigDecimal.ROUND_DOWN, 2));
			// tmp.put("GmBillUsage", new BigDecimal(
			// gmBillMap.get("total") == null ? 0 : (Double) gmBillMap
			// .get("total")).setScale(BigDecimal.ROUND_DOWN, 2));
			// tmp.put("WmBillUsage", new BigDecimal(
			// wmBillMap.get("total") == null ? 0 : (Double) wmBillMap
			// .get("total")).setScale(BigDecimal.ROUND_DOWN, 2));

			// tmp.put("Co2Usage", new BigDecimal(
			// emCo2Map.get("TOTAL") == null ? 0 : (Double) emCo2Map
			// .get("TOTAL")).add(
			// new BigDecimal(gmCo2Map.get("TOTAL") == null ? 0
			// : (Double) gmCo2Map.get("TOTAL"))
			// .add(new BigDecimal(
			// wmCo2Map.get("TOTAL") == null ? 0
			// : (Double) wmCo2Map.get("TOTAL"))))
			// .setScale(BigDecimal.ROUND_DOWN, 2));

			tmp.put("Co2Usage", new BigDecimal(emMap.get(yyyymm) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emMap.get(yyyymm)) * 0.424).add(
					new BigDecimal(gmMap.get(yyyymm) == null ? 0
							: DecimalUtil.ConvertNumberToDouble(gmMap.get(yyyymm)) * 9.95)
							.add(new BigDecimal(wmMap.get(yyyymm) == null ? 0
									: DecimalUtil.ConvertNumberToDouble(wmMap.get(yyyymm)) * 0.323)
								.add(new BigDecimal(hmMap.get(yyyymm) == null ? 0
									: DecimalUtil.ConvertNumberToDouble(hmMap.get(yyyymm)) * 0.424))))
					.setScale(BigDecimal.ROUND_DOWN, 2));

			tmp.put("EmToe", new BigDecimal(emMap.get(yyyymm) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emMap.get(yyyymm)) * KGOE.Energy.getValue())
					.setScale(BigDecimal.ROUND_DOWN, 2));

			tmp.put("GmToe", new BigDecimal(gmMap.get(yyyymm) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(gmMap.get(yyyymm)) * KGOE.GasLng.getValue())
					.setScale(BigDecimal.ROUND_DOWN, 2));

			tmp.put("WmToe", new BigDecimal(wmMap.get(yyyymm) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(wmMap.get(yyyymm)) * KGOE.Water.getValue())
					.setScale(BigDecimal.ROUND_DOWN, 2));

			tmp.put("HmToe", new BigDecimal(hmMap.get(yyyymm) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(hmMap.get(yyyymm)) * KGOE.Heat.getValue())
					.setScale(BigDecimal.ROUND_DOWN, 2));

			
			tmp.put("TotalUsage", new BigDecimal(emMap.get(yyyymm) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emMap.get(yyyymm)) * KGOE.Energy.getValue()).add(
					new BigDecimal(gmMap.get(yyyymm) == null ? 0
							: DecimalUtil.ConvertNumberToDouble(gmMap.get(yyyymm))
									* KGOE.GasLng.getValue())
							.add(new BigDecimal(wmMap.get(yyyymm) == null ? 0
									: DecimalUtil.ConvertNumberToDouble(wmMap.get(yyyymm))
											* KGOE.Water.getValue())
							.add(new BigDecimal(hmMap.get(yyyymm) == null ? 0
									: DecimalUtil.ConvertNumberToDouble(hmMap.get(yyyymm))
											* KGOE.Heat.getValue())))).setScale(
					BigDecimal.ROUND_DOWN, 2));

		}
		return tmp;
	}

	public Integer getTariffTypeId(String serviceType, int supplierId) {
		List<TariffType> tariffTypeList = tariffTypeDao
				.getTariffTypeBySupplier(serviceType, supplierId);

		TariffType tariffType = tariffTypeList.get(0);
		return tariffType.getId();
	}

	public Map<String, Object> getTariff(Integer tariffTypeId,
			String serviceType) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		List<TariffEM> tariffEMList = new ArrayList<TariffEM>();
		List<TariffGM> tariffGMList = new ArrayList<TariffGM>();
		List<TariffWM> tariffWMList = new ArrayList<TariffWM>();

		Set<Condition> conditions = new HashSet<Condition>(0);

		conditions.add(new Condition("tariffType.id",
				new Object[] { tariffTypeId }, null, Restriction.EQ));
		
		conditions.add(new Condition("yyyymmdd", new Object[] {}, null,
				Restriction.ORDERBY));

		if (serviceType.equals(CommonConstants.MeterType.EnergyMeter
				.getServiceType())) {
			tariffEMList = tariffEmDao.findByConditions(conditions);
			resultMap.put("tariffList", tariffEMList);
		} else if (serviceType.equals(CommonConstants.MeterType.GasMeter
				.getServiceType())) {
			tariffGMList = tariffGmDao.findByConditions(conditions);
			resultMap.put("tariffList", tariffGMList);
		} else if (serviceType.equals(CommonConstants.MeterType.WaterMeter
				.getServiceType())) {
			tariffWMList = tariffWmDao.findByConditions(conditions);
			resultMap.put("tariffList", tariffWMList);
		}

		return resultMap;

	}

	public BigDecimal getBillUsage(Map<String, Object> tariffList,
			String serviceType, String date, BigDecimal usage, int seasonId,
			PeakType peakType) {

		BigDecimal billUsage = new BigDecimal(0);
		BigDecimal zero = new BigDecimal(0);
		List<TariffEM> tariffEMList = new ArrayList<TariffEM>();
		List<TariffGM> tariffGMList = new ArrayList<TariffGM>();
		List<TariffWM> tariffWMList = new ArrayList<TariffWM>();
		int day = Integer.parseInt(date);

		if (serviceType.equals(CommonConstants.MeterType.EnergyMeter
				.getServiceType())) {
			tariffEMList = (List<TariffEM>) tariffList.get("tariffList");

			for (int i = 0; i < tariffEMList.size(); i++) {
				TariffEM tariff = tariffEMList.get(i);
				int tariffDay = Integer.parseInt(tariff.getYyyymmdd());
				if (tariffDay > day) {
					if (tariff.getSeason() != null) {
						if (seasonId == tariff.getSeason().getId()) {
							if (tariff.getPeakType() != null) {
								if (peakType == tariff.getPeakType()) {

									billUsage = usage
											.multiply(
													new BigDecimal(
															tariff
																	.getActiveEnergyCharge()))
											.add(
													usage
															.multiply(new BigDecimal(
																	tariff
																			.getEnergyDemandCharge())));
									break;
								}

							}
						} else {

							billUsage = usage.multiply(
									new BigDecimal(tariff
											.getActiveEnergyCharge())).add(
									usage.multiply(new BigDecimal(tariff
											.getEnergyDemandCharge())));
							break;

						}
					}
				} else {

					billUsage = usage.multiply(
							new BigDecimal(tariff.getActiveEnergyCharge()))
							.add(
									usage.multiply(new BigDecimal(tariff
											.getEnergyDemandCharge())));
					break;

				}
			}

		} else if (serviceType.equals(CommonConstants.MeterType.GasMeter
				.getServiceType())) {
			tariffGMList = (List<TariffGM>) tariffList.get("tariffList");

			for (int i = 0; i < tariffGMList.size(); i++) {
				TariffGM tariff = tariffGMList.get(i);
				int tariffDay = Integer.parseInt(tariff.getYyyymmdd());
				if (tariffDay > day) {
					if (tariff.getSeason() != null) {
						if (seasonId == tariff.getSeason().getId()) {
							billUsage = usage.multiply(new BigDecimal(tariff
									.getUsageUnitPrice()));
							break;
						}
					} else {
						billUsage = usage.multiply(new BigDecimal(tariff
								.getUsageUnitPrice()));
						break;
					}
				}
			}

		} else if (serviceType.equals(CommonConstants.MeterType.WaterMeter
				.getServiceType())) {
			tariffWMList = (List<TariffWM>) tariffList.get("tariffList");
			for (int i = 0; i < tariffWMList.size(); i++) {
				TariffWM tariff = tariffWMList.get(i);
				int tariffDay = Integer.parseInt(tariff.getYyyymmdd());
				if (tariffDay > day) {
					if (tariff.getCondition1().endsWith("<")) {
						BigDecimal supplySizeMin = new BigDecimal(tariff
								.getSupplySizeMin());
						if (supplySizeMin.compareTo(usage) > 0
								|| usage.compareTo(zero) == 0) {
							billUsage = usage.multiply(new BigDecimal(tariff
									.getUsageUnitPrice()));
							break;
						}
					}
				}
			}
		}

		return billUsage;

	}

	public Map<String, Object> getEfficiencyUsage(Map<String, Object> params) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			Integer supplierId = (Integer) params.get("supplierId");
			Supplier supplier = supplierDao.get(supplierId);
			Double area = supplier.getArea();
			List<Object> co2List = new ArrayList<Object>();
			Map<String, Object> effiency = getEfficiencyLocationUsage(params,
					new BigDecimal(area));

			BigDecimal emUsage = (BigDecimal) effiency.get("EmUsage");
			co2List = (List<Object>) effiency.get("co2List");

			BigDecimal grade1 = new BigDecimal(300);
			BigDecimal grade2 = new BigDecimal(350);
			BigDecimal grade3 = new BigDecimal(400);
			BigDecimal grade4 = new BigDecimal(450);
			BigDecimal grade5 = new BigDecimal(500);
			Integer grade = 0;
			if (emUsage.compareTo(grade1) < 0) {
				grade = 1;
			} else if (emUsage.compareTo(grade2) < 0) {
				grade = 2;
			} else if (emUsage.compareTo(grade3) < 0) {
				grade = 3;
			} else if (emUsage.compareTo(grade4) < 0) {
				grade = 4;
			} else if (emUsage.compareTo(grade5) < 0) {
				grade = 5;
			} else if (emUsage.compareTo(grade5) >= 0) {
				grade = 5;
			}
			retMap.put("Grade", grade);
			retMap.put("EmUsage", emUsage);

			retMap.put("co2List", co2List);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retMap;
	}

	private String convertDate(int supplierId, String date) {
		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();

		return TimeLocaleUtil.getDBDate(date, 8, lang, country);
	}

	public Map<String, Object> getSearchLocationChartData(
			Map<String, Object> params) {

		String periodType = (String) params.get("periodType");
		String date = convertDate((Integer) params.get("supplierId"),
				(String) params.get("date"));

		// 리턴 객체
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			Map<String, String> currMap = this.calerdarTerm(periodType, date);

			params.put("startDate", currMap.get("startDate"));
			params.put("endDate", currMap.get("endDate"));
			List<Object> currUsage = getLocationUsage(params);

			BigDecimal minVal = new BigDecimal(0);
			BigDecimal maxVal = new BigDecimal(0);
			BigDecimal co2MinVal = new BigDecimal(0);
			BigDecimal co2MaxVal = new BigDecimal(0);
			Map<String, Object> currUsageMap = null;

			for (int i = 0; i < currUsage.size(); i++) {

				currUsageMap = new HashMap<String, Object>();
				currUsageMap = (Map<String, Object>) currUsage.get(i);

				BigDecimal currEmUsage = (BigDecimal) currUsageMap
						.get("EmUsage");
				BigDecimal currGmUsage = (BigDecimal) currUsageMap
						.get("GmUsage");
				BigDecimal currWmUsage = (BigDecimal) currUsageMap
						.get("WmUsage");
				BigDecimal currHmUsage = (BigDecimal) currUsageMap
						.get("HmUsage");
				
				BigDecimal currCo2Usage = (BigDecimal) currUsageMap
						.get("Co2Usage");

				BigDecimal emGmWmHmTotal = new BigDecimal(0);
				emGmWmHmTotal = emGmWmHmTotal.add(currEmUsage).add(currGmUsage)
						.add(currWmUsage).add(currHmUsage);

				if (emGmWmHmTotal.compareTo(minVal) < 0) {
					minVal = emGmWmHmTotal;
				}
				if (emGmWmHmTotal.compareTo(maxVal) > 0) {
					maxVal = emGmWmHmTotal;
				}

				if (currCo2Usage.compareTo(co2MinVal) < 0) {
					co2MinVal = currCo2Usage;
				}
				if (currCo2Usage.compareTo(co2MaxVal) > 0) {
					co2MaxVal = currCo2Usage;
				}
			}
			
			Map<String, Object> totalUsageMap = new HashMap<String, Object>();

			totalUsageMap.put("minVal", minVal);
			totalUsageMap.put("maxVal", maxVal);
			totalUsageMap.put("co2MinVal", co2MinVal);
			totalUsageMap.put("co2MaxVal", co2MaxVal);
			resultMap.put("currUsageList", currUsage);
			resultMap.put("totalUsageMap", totalUsageMap);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	public Map<String, Object> getSearchCompareLocationChartData(
			Map<String, Object> params) {
		// 리턴 객체
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			String periodType = (String) params.get("periodType");
			String date = convertDate((Integer) params.get("supplierId"),
					(String) params.get("date"));
			boolean compare = (Boolean) params.get("compare");

			Map<String, String> currMap = this.calerdarTerm(periodType, date);

			if(!CommonConstants.DateType.YEARLY.getCode().equals(periodType)){
				params.put("startDate", currMap.get("startDate"));
				params.put("endDate", currMap.get("endDate"));
			}else{
				params.put("startDate", currMap.get("startDate").substring(0, 6));
				params.put("endDate", currMap.get("endDate").substring(0, 6));
			}

			List<Object> currUsage = getLocationUsage(params);

			Map<String, String> preMap = this.compareCalendarTerm(periodType,
					date, compare);

			if(!CommonConstants.DateType.YEARLY.getCode().equals(periodType)){
				params.put("startDate", preMap.get("startDate"));
				params.put("endDate", preMap.get("endDate"));
			}else{
				params.put("startDate", preMap.get("startDate").substring(0, 6));
				params.put("endDate", preMap.get("endDate").substring(0, 6));
			}

			List<Object> preUsage = getLocationUsage(params);

			// System.out.println("################ pre params:" + params);
			BigDecimal minVal = new BigDecimal(0);
			BigDecimal maxVal = new BigDecimal(0);
			BigDecimal co2MinVal = new BigDecimal(0);
			BigDecimal co2MaxVal = new BigDecimal(0);
			Map<String, Object> currUsageMap = null;
			Map<String, Object> preUsageMap = null;
			for (int i = 0; i < currUsage.size(); i++) {

				currUsageMap = new HashMap<String, Object>();
				currUsageMap = (Map<String, Object>) currUsage.get(i);

				BigDecimal currEmUsage = (BigDecimal) currUsageMap
						.get("EmKGOE");
				BigDecimal currGmUsage = (BigDecimal) currUsageMap
						.get("GmKGOE");
				BigDecimal currWmUsage = (BigDecimal) currUsageMap
						.get("WmKGOE");
				BigDecimal currHmUsage = (BigDecimal) currUsageMap
						.get("HmKGOE");
				BigDecimal currCo2Usage = (BigDecimal) currUsageMap
						.get("Co2Usage");

				BigDecimal emGmWmHmTotal = new BigDecimal(0);
				emGmWmHmTotal = emGmWmHmTotal.add(currEmUsage).add(currGmUsage)
						.add(currWmUsage).add(currHmUsage);
				
				if (emGmWmHmTotal.compareTo(minVal) < 0) {
					minVal = emGmWmHmTotal;
				}
				if (emGmWmHmTotal.compareTo(maxVal) > 0) {
					maxVal = emGmWmHmTotal;
				}

				if (currCo2Usage.compareTo(co2MinVal) < 0) {
					co2MinVal = currCo2Usage;
				}
				if (currCo2Usage.compareTo(co2MaxVal) > 0) {
					co2MaxVal = currCo2Usage;
				}

				preUsageMap = new HashMap<String, Object>();
				preUsageMap = (Map<String, Object>) preUsage.get(i);
				preUsageMap.put("xField", currUsageMap.get("xField"));
				BigDecimal preEmUsage = (BigDecimal) preUsageMap.get("EmKGOE");
				BigDecimal preGmUsage = (BigDecimal) preUsageMap.get("GmKGOE");
				BigDecimal preWmUsage = (BigDecimal) preUsageMap.get("WmKGOE");
				BigDecimal preHmUsage = (BigDecimal) preUsageMap.get("HmKGOE");
				BigDecimal preCo2Usage = (BigDecimal) preUsageMap
						.get("Co2Usage");

				BigDecimal preEmGmWmHmTotal = new BigDecimal(0);
				preEmGmWmHmTotal = preEmGmWmHmTotal.add(preEmUsage).add(preGmUsage)
						.add(preWmUsage).add(preHmUsage);

				if (preEmGmWmHmTotal.compareTo(minVal) < 0) {
					minVal = preEmGmWmHmTotal;
				}
				if (preEmGmWmHmTotal.compareTo(maxVal) > 0) {
					maxVal = preEmGmWmHmTotal;
				}

				if (preCo2Usage.compareTo(co2MinVal) < 0) {
					co2MinVal = preCo2Usage;
				}
				if (preCo2Usage.compareTo(co2MaxVal) > 0) {
					co2MaxVal = preCo2Usage;
				}

			}
			Map<String, Object> totalUsageMap = new HashMap<String, Object>();

			totalUsageMap.put("minVal", minVal);
			totalUsageMap.put("maxVal", maxVal);
			totalUsageMap.put("co2MinVal", co2MinVal);
			totalUsageMap.put("co2MaxVal", co2MaxVal);
			resultMap.put("currUsageList", currUsage);
			resultMap.put("preUsageList", preUsage);
			resultMap.put("totalUsageMap", totalUsageMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getSearchChartData(Map<String, Object> params) {

		String periodType = (String) params.get("periodType");
		Integer supplierId = (Integer) params.get("supplierId");
		String date = convertDate( supplierId,
				(String) params.get("date"));
		int locationId = (Integer) params.get("locationId");

		Language lang = supplierDao.get(supplierId).getLang();
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, String> currMap = this.calerdarTerm(periodType, date);
		Map<String, Object> meterIdMap = this.getMeterId(locationId);
		try {
			// params.put("root", false);
			params.put("startDate", currMap.get("startDate"));
			params.put("endDate", currMap.get("endDate"));
			params.put("tLocationId", (Integer) meterIdMap.get("tLocationId"));
			params.put("inMeterId", (Integer) meterIdMap.get("inMeterId"));
			params.put("outMeterId", (Integer) meterIdMap.get("outMeterId"));
			params.put("supplierId", (Integer) supplierId);
			
			List<Object> locationList = meteringDayDao.getTemperatureHumidityLocation("DAY_TM");
			if (locationList.size() > 0) {
				Map<String, Object> map = (Map<String, Object>) locationList.get(0);
				params.put("tLocationId", DecimalUtil.ConvertNumberToInteger(map.get("LOCATIONID")));
			} else {
			    params.put("tLocationId", -1);
			}
			List<Object> currUsage = getPeriodUsage(params,lang.getCode_2letter());
				
			BigDecimal minVal = new BigDecimal(0);
			BigDecimal maxVal = new BigDecimal(0);
			
			BigDecimal billMinVal = new BigDecimal(0);
			BigDecimal billMaxVal = new BigDecimal(0);
			
			BigDecimal co2MinVal = new BigDecimal(0);
			BigDecimal co2MaxVal = new BigDecimal(0);
			
			BigDecimal tmMinVal = new BigDecimal(100);
			BigDecimal tmMaxVal = new BigDecimal(0);
			BigDecimal humMinVal = new BigDecimal(100);
			BigDecimal humMaxVal = new BigDecimal(0);
			
			Map<String, Object> currUsageMap = null;

			String tmKey0 = "";
			String tmKey1 = "";
			String tmKey2 = "";
			String tmKey3 = "";

			String humKey0 = "";
			String humKey1 = "";
			String humKey2 = "";
			String humKey3 = "";

			if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {

				tmKey0 = "TmInUsage";
				tmKey1 = "TmInUsage";
				tmKey2 = "TmOutUsage";
				tmKey3 = "TmOutUsage";

				humKey0 = "HumInUsage";
				humKey1 = "HumInUsage";
				humKey2 = "HumOutUsage";
				humKey3 = "HumOutUsage";

			} else {
				tmKey0 = "TmInMaxUsage";
				tmKey1 = "TmInMinUsage";
				tmKey2 = "TmOutMaxUsage";
				tmKey3 = "TmOutMinUsage";

				humKey0 = "HumInMaxUsage";
				humKey1 = "HumInMinUsage";
				humKey2 = "HumOutMaxUsage";
				humKey3 = "HumOutMinUsage";
			}
			
			for (int i = 0; i < currUsage.size(); i++) {

				currUsageMap = new HashMap<String, Object>();
				currUsageMap = (Map<String, Object>) currUsage.get(i);

				BigDecimal currEmUsage = (BigDecimal) currUsageMap
						.get("EmUsage");
				BigDecimal currGmUsage = (BigDecimal) currUsageMap
						.get("GmUsage");
				BigDecimal currWmUsage = (BigDecimal) currUsageMap
						.get("WmUsage");
				BigDecimal currHmUsage = (BigDecimal) currUsageMap
						.get("HmUsage");
				
				BigDecimal currEmBillUsage = (BigDecimal) currUsageMap
						.get("EmBillUsage");
				BigDecimal currGmBillUsage = (BigDecimal) currUsageMap
						.get("GmBillUsage");
				BigDecimal currWmBillUsage = (BigDecimal) currUsageMap
						.get("WmBillUsage");
				BigDecimal currHmBillUsage = (BigDecimal) currUsageMap
						.get("HmBillUsage");
				
				BigDecimal currTmInMaxUsage = (BigDecimal) currUsageMap
						.get(tmKey0);
				BigDecimal currTmInMinUsage = (BigDecimal) currUsageMap
						.get(tmKey1);
				BigDecimal currTmOutMaxUsage = (BigDecimal) currUsageMap
						.get(tmKey2);
				BigDecimal currTmOutMinUsage = (BigDecimal) currUsageMap
						.get(tmKey3);

				BigDecimal currHumInMaxUsage = (BigDecimal) currUsageMap
						.get(humKey0);
				BigDecimal currHumInMinUsage = (BigDecimal) currUsageMap
						.get(humKey1);

				BigDecimal currHumOutMaxUsage = (BigDecimal) currUsageMap
						.get(humKey2);
				BigDecimal currHumOutMinUsage = (BigDecimal) currUsageMap
						.get(humKey3);

				BigDecimal currCo2Usage = (BigDecimal) currUsageMap
						.get("Co2Usage");

				BigDecimal emGmWmHmTotal = new BigDecimal(0);
				emGmWmHmTotal = emGmWmHmTotal.add(currEmUsage).add(currGmUsage)
						.add(currWmUsage).add(currHmUsage);

				BigDecimal emGmWmHmBillTotal = new BigDecimal(0);
				emGmWmHmBillTotal = emGmWmHmBillTotal.add(currEmBillUsage).add(
						currGmBillUsage).add(currWmBillUsage).add(currHmBillUsage);

				if (tmMinVal.compareTo(currTmInMinUsage) > 0) {
					tmMinVal = currTmInMinUsage;
				}

				if (tmMaxVal.compareTo(currTmInMaxUsage) < 0) {
					tmMaxVal = currTmInMaxUsage;
				}

				if (tmMinVal.compareTo(currTmOutMinUsage) > 0) {
					tmMinVal = currTmOutMinUsage;
				}

				if (tmMaxVal.compareTo(currTmOutMaxUsage) < 0) {
					tmMaxVal = currTmOutMaxUsage;
				}

				if (humMaxVal.compareTo(currHumInMaxUsage) < 0) {
					humMaxVal = currHumInMaxUsage;
				}

				if (humMinVal.compareTo(currHumInMinUsage) > 0) {
					humMinVal = currHumInMinUsage;
				}
				
				if (humMaxVal.compareTo(currHumOutMaxUsage) < 0) {
					humMaxVal = currHumOutMaxUsage;
				}

				if (humMinVal.compareTo(currHumOutMinUsage) > 0) {
					humMinVal = currHumOutMinUsage;
				}

				//System.out.println("emGmWmTotal:"+emGmWmTotal+":maxVal:"+maxVal+":compare:"+emGmWmTotal.compareTo(maxVal)); 
				if (emGmWmHmTotal.compareTo(maxVal) > 0) {
					maxVal = emGmWmHmTotal;
				}
				if (emGmWmHmTotal.compareTo(minVal) < 0) {
					minVal = emGmWmHmTotal;
				}
				if (emGmWmHmBillTotal.compareTo(billMaxVal) > 0) {
					billMaxVal = emGmWmHmBillTotal;
				}
				if (emGmWmHmBillTotal.compareTo(billMinVal) < 0) {
					billMinVal = emGmWmHmBillTotal;
				}

				if (currCo2Usage.compareTo(co2MinVal) > 0) {
					co2MinVal = currCo2Usage;
				}
				if (currCo2Usage.compareTo(co2MaxVal) < 0) {
					co2MaxVal = currCo2Usage;
				}
			}
			Map<String, Object> totalUsageMap = new HashMap<String, Object>();

			totalUsageMap.put("minVal", minVal);
			totalUsageMap.put("maxVal", maxVal);
			totalUsageMap.put("billMinVal", billMinVal);
			totalUsageMap.put("billMaxVal", billMaxVal);
			totalUsageMap.put("co2MinVal", co2MinVal);
			totalUsageMap.put("co2MaxVal", co2MaxVal);

			totalUsageMap.put("tmMinVal", tmMinVal);
			totalUsageMap.put("tmMaxVal", tmMaxVal);
			totalUsageMap.put("humMinVal", humMinVal);
			totalUsageMap.put("humMaxVal", humMaxVal);
			
			resultMap.put("currUsageList", currUsage);
			resultMap.put("totalUsageMap", totalUsageMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	@SuppressWarnings("unchecked")
    public Map<String, Object> getSearchCompareChartData(
			Map<String, Object> params) {
		// 리턴 객체
		Map<String, Object> resultMap = new HashMap<String, Object>();
		BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
		
		try {
			String periodType = (String) params.get("periodType");
			// String date = (String) params.get("date");
			Integer supplierId = (Integer) params.get("supplierId");
			String date = convertDate(supplierId,
					(String) params.get("date"));
			boolean compare = (Boolean) params.get("compare");
			int locationId = (Integer) params.get("locationId");
			
			Supplier supplier = supplierDao.get(supplierId);
			Language lang = supplier.getLang();
			DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());
			
			Map<String, String> currMap = this.calerdarTerm(periodType, date);

			Map<String, Object> meterIdMap = this.getMeterId(locationId);
			params.put("supplierId", (Integer) supplierId);
			params.put("tLocationId", (Integer) meterIdMap.get("tLocationId"));
			params.put("inMeterId", (Integer) meterIdMap.get("inMeterId"));
			params.put("outMeterId", (Integer) meterIdMap.get("outMeterId"));
			List<Object> locationList = meteringDayDao.getTemperatureHumidityLocation("DAY_TM");
			Map<String, Object> map = null;

			if (locationList != null && locationList.size() > 0) {
			    map = (Map<String, Object>) locationList.get(0);
	            params.put("tLocationId", DecimalUtil.ConvertNumberToInteger(map.get("LOCATIONID")));
			} else {
			    map = new HashMap<String, Object>();
	            params.put("tLocationId", -1);
			}

			params.put("startDate", currMap.get("startDate"));
			params.put("endDate", currMap.get("endDate"));

			// System.out.println("curr params:" + params);
			List<Object> currUsage = getPeriodUsage(params, lang.getCode_2letter());

			Map<String, String> preMap = this.compareCalendarTerm(periodType,
					date, compare);

			params.put("startDate", preMap.get("startDate"));
			params.put("endDate", preMap.get("endDate"));
			
			// System.out.println("pre params:" + params);
			List<Object> preUsage = getPeriodUsage(params, lang.getCode_2letter());

			BigDecimal minVal = new BigDecimal(20);
			BigDecimal maxVal = new BigDecimal(0);

			BigDecimal billMinVal = new BigDecimal(20);
			BigDecimal billMaxVal = new BigDecimal(0);
			BigDecimal co2MinVal = new BigDecimal(20);
			BigDecimal co2MaxVal = new BigDecimal(0);

			BigDecimal tmMinVal = new BigDecimal(20);
			BigDecimal tmMaxVal = new BigDecimal(0);
			BigDecimal humMinVal = new BigDecimal(20);
			BigDecimal humMaxVal = new BigDecimal(0);

			Map<String, Object> currUsageMap = null;
			Map<String, Object> preUsageMap = null;

			String tmKey0 = "";
			String tmKey1 = "";
			String tmKey2 = "";
			String tmKey3 = "";

			String humKey0 = "";
			String humKey1 = "";
			String humKey2 = "";
			String humKey3 = "";

			if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {
				tmKey0 = "TmInUsage";
				tmKey1 = "TmInUsage";
				tmKey2 = "TmOutUsage";
				tmKey3 = "TmOutUsage";

				humKey0 = "HumInUsage";
				humKey1 = "HumInUsage";
				humKey2 = "HumOutUsage";
				humKey3 = "HumOutUsage";

			} else {
				tmKey0 = "TmInMaxUsage";
				tmKey1 = "TmInMinUsage";
				tmKey2 = "TmOutMaxUsage";
				tmKey3 = "TmOutMinUsage";

				humKey0 = "HumInMaxUsage";
				humKey1 = "HumInMinUsage";
				humKey2 = "HumOutMaxUsage";
				humKey3 = "HumOutMinUsage";
			}
			for (int i = 0; i < currUsage.size(); i++) {

				currUsageMap = new HashMap<String, Object>();
				currUsageMap = (Map<String, Object>) currUsage.get(i);

				preUsageMap = new HashMap<String, Object>();
				preUsageMap = (Map<String, Object>) preUsage.get(i);

				preUsageMap.put("xField", currUsageMap.get("xField"));
				BigDecimal currEmUsage = (BigDecimal) currUsageMap
						.get("EmKGOE");
				BigDecimal currGmUsage = (BigDecimal) currUsageMap
						.get("GmKGOE");
				BigDecimal currWmUsage = (BigDecimal) currUsageMap
						.get("WmKGOE");
				BigDecimal currHmUsage = (BigDecimal) currUsageMap
						.get("HmKGOE");
				
				BigDecimal currEmBillUsage = (BigDecimal) currUsageMap
						.get("EmBillUsage");
				BigDecimal currGmBillUsage = (BigDecimal) currUsageMap
						.get("GmBillUsage");
				BigDecimal currWmBillUsage = (BigDecimal) currUsageMap
						.get("WmBillUsage");
				BigDecimal currHmBillUsage = (BigDecimal) currUsageMap
						.get("HmBillUsage");

				BigDecimal currCo2Usage = (BigDecimal) currUsageMap
						.get("Co2Usage");

				BigDecimal preEmUsage = (BigDecimal) preUsageMap.get("EmKGOE");
				BigDecimal preGmUsage = (BigDecimal) preUsageMap.get("GmKGOE");
				BigDecimal preWmUsage = (BigDecimal) preUsageMap.get("WmKGOE");
				BigDecimal preHmUsage = (BigDecimal) preUsageMap.get("HmKGOE");

				BigDecimal preEmBillUsage = (BigDecimal) preUsageMap
						.get("EmBillUsage");
				BigDecimal preGmBillUsage = (BigDecimal) preUsageMap
						.get("GmBillUsage");
				BigDecimal preWmBillUsage = (BigDecimal) preUsageMap
						.get("WmBillUsage");
				BigDecimal preHmBillUsage = (BigDecimal) preUsageMap
						.get("HmBillUsage");

				BigDecimal preCo2Usage = (BigDecimal) preUsageMap
						.get("Co2Usage");

				BigDecimal currTmInMaxUsage = (BigDecimal) currUsageMap
						.get(tmKey0);
				BigDecimal currTmInMinUsage = (BigDecimal) currUsageMap
						.get(tmKey1);

				BigDecimal currTmOutMaxUsage = (BigDecimal) currUsageMap
						.get(tmKey2);
				BigDecimal currTmOutMinUsage = (BigDecimal) currUsageMap
						.get(tmKey3);

				BigDecimal currHumInMaxUsage = (BigDecimal) currUsageMap
						.get(humKey0);
				BigDecimal currHumInMinUsage = (BigDecimal) currUsageMap
						.get(humKey1);

				BigDecimal currHumOutMaxUsage = (BigDecimal) currUsageMap
						.get(humKey2);
				BigDecimal currHumOutMinUsage = (BigDecimal) currUsageMap
						.get(humKey3);

				BigDecimal preTmInMaxUsage = (BigDecimal) preUsageMap
						.get(tmKey0);
				BigDecimal preTmInMinUsage = (BigDecimal) preUsageMap
						.get(tmKey1);

				BigDecimal preTmOutMaxUsage = (BigDecimal) preUsageMap
						.get(tmKey2);
				BigDecimal preTmOutMinUsage = (BigDecimal) preUsageMap
						.get(tmKey3);

				BigDecimal preHumInMaxUsage = (BigDecimal) preUsageMap
						.get(humKey0);
				BigDecimal preHumInMinUsage = (BigDecimal) preUsageMap
						.get(humKey1);

				BigDecimal preHumOutMaxUsage = (BigDecimal) preUsageMap
						.get(humKey2);
				BigDecimal preHumOutMinUsage = (BigDecimal) preUsageMap
						.get(humKey3);

				BigDecimal emGmWmHmTotal = new BigDecimal(0);
				emGmWmHmTotal = emGmWmHmTotal.add(currEmUsage).add(currGmUsage)
						.add(currWmUsage).add(currHmUsage);
				
				BigDecimal emGmWmHmBillTotal = new BigDecimal(0);
				emGmWmHmBillTotal = emGmWmHmBillTotal.add(currEmBillUsage).add(
						currGmBillUsage).add(currWmBillUsage).add(currHmBillUsage);

				BigDecimal preEmGmWmHmTotal = new BigDecimal(0);
				preEmGmWmHmTotal = preEmGmWmHmTotal.add(preEmUsage).add(preGmUsage)
						.add(preWmUsage).add(preHmUsage);

				BigDecimal preEmGmWmHmBillTotal = new BigDecimal(0);
				preEmGmWmHmBillTotal = preEmGmWmHmBillTotal.add(preEmBillUsage)
						.add(preGmBillUsage).add(preWmBillUsage).add(preHmBillUsage);

				if (emGmWmHmTotal.compareTo(minVal) < 0) {
					minVal = emGmWmHmTotal;
				}
				if (emGmWmHmTotal.compareTo(maxVal) > 0) {
					maxVal = emGmWmHmTotal;
				}

				if (preEmGmWmHmTotal.compareTo(minVal) < 0) {
					minVal = preEmGmWmHmTotal;
				}
				if (preEmGmWmHmTotal.compareTo(maxVal) > 0) {
					maxVal = preEmGmWmHmTotal;
				}
				
				currUsageMap.put("EmGmWmHmTotal", emGmWmHmTotal);
				currUsageMap.put("EmGmWmHmGridTotal", df.format(emGmWmHmTotal));
				currUsageMap.put("usageReduct", getReduct(preEmGmWmHmTotal,
						emGmWmHmTotal));
				BigDecimal co2Usage = (BigDecimal) currUsageMap.get("Co2Usage");
				
				currUsageMap.put("Co2GridUsage", df.format(co2Usage));
				
				if (emGmWmHmBillTotal.compareTo(billMinVal) < 0) {
					billMinVal = emGmWmHmBillTotal;
				}
				if (emGmWmHmBillTotal.compareTo(billMaxVal) > 0) {
					billMaxVal = emGmWmHmBillTotal;
				}

				if (preEmGmWmHmBillTotal.compareTo(billMinVal) < 0) {
					billMinVal = preEmGmWmHmBillTotal;
				}
				if (preEmGmWmHmBillTotal.compareTo(billMaxVal) > 0) {
					billMaxVal = preEmGmWmHmBillTotal;
				}

				int billReduct = getReduct(preEmGmWmHmBillTotal, emGmWmHmBillTotal);
				
				currUsageMap.put("EmGmWmHmBillTotal", emGmWmHmBillTotal);
				currUsageMap.put("usageBillReduct", billReduct);
				
				if (preEmGmWmHmTotal.compareTo(minVal) < 0) {
					minVal = preEmGmWmHmTotal;
				}
				if (preEmGmWmHmTotal.compareTo(maxVal) > 0) {
					maxVal = preEmGmWmHmTotal;
				}

				if (currCo2Usage.compareTo(co2MinVal) < 0) {
					co2MinVal = currCo2Usage;
				}
				if (currCo2Usage.compareTo(co2MaxVal) > 0) {
					co2MaxVal = currCo2Usage;
				}

				if (preCo2Usage.compareTo(co2MinVal) < 0) {
					co2MinVal = preCo2Usage;
				}
				if (preCo2Usage.compareTo(co2MaxVal) > 0) {
					co2MaxVal = preCo2Usage;
				}
				currUsageMap.put("usageCo2Reduct", getReduct(preCo2Usage,
						currCo2Usage));


				if (tmMaxVal.compareTo(currTmInMaxUsage) < 0) {
					tmMaxVal = currTmInMaxUsage;
				}

				if (tmMinVal.compareTo(currTmInMinUsage) > 0) {
					tmMinVal = currTmInMinUsage;
				}

				if (tmMaxVal.compareTo(currTmOutMaxUsage) < 0) {
					tmMaxVal = currTmOutMaxUsage;
				}

				if (tmMinVal.compareTo(currTmOutMinUsage) > 0) {
					tmMinVal = currTmOutMinUsage;
				}

				if (humMaxVal.compareTo(currHumInMaxUsage) > 0) {
					humMaxVal = currHumInMaxUsage;
				}

				if (humMinVal.compareTo(currHumInMinUsage) < 0) {
					humMinVal = currHumInMinUsage;
				}
				if (humMaxVal.compareTo(currHumOutMaxUsage) > 0) {
					humMaxVal = currHumOutMaxUsage;
				}

				if (humMinVal.compareTo(currHumOutMinUsage) < 0) {
					humMinVal = currHumOutMinUsage;
				}			
			}
		
			Map<String, Object> totalUsageMap = new HashMap<String, Object>();

			totalUsageMap.put("minVal", minVal);
			totalUsageMap.put("maxVal", maxVal);
			totalUsageMap.put("billMinVal", billMinVal);
			totalUsageMap.put("billMaxVal", billMaxVal);
			totalUsageMap.put("co2MinVal", co2MinVal);
			totalUsageMap.put("co2MaxVal", co2MaxVal);

			totalUsageMap.put("tmMinVal", tmMinVal);
			totalUsageMap.put("tmMaxVal", tmMaxVal);
			totalUsageMap.put("humMinVal", humMinVal);
			totalUsageMap.put("humMaxVal", humMaxVal);
			resultMap.put("currUsageList", currUsage);
			resultMap.put("preUsageList", preUsage);
			resultMap.put("totalUsageMap", totalUsageMap);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	private int getReduct(BigDecimal pre, BigDecimal curr) {
		BigDecimal reduct = new BigDecimal(0);
		if (pre.compareTo(reduct) == 0 && curr.compareTo(reduct) == 0) {
			return 0;
		}
		if (curr.compareTo(reduct) == 0) {
			return 0;
		}
		if (pre.compareTo(reduct) == 0) {
			return 100;
		}
		int result = ((pre.subtract(curr)).divide(pre, MathContext.DECIMAL32)
				.multiply(new BigDecimal(100))).intValue();

		return result;
	}

	private Map<String, Object> getEfficiencyLocationUsage(
			Map<String, Object> params, BigDecimal area) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		List<Object> resultList = new ArrayList<Object>();
		String date = (String) params.get("date");
		String yyyy = date.substring(0, 4);
		String emStartDate = CalendarUtil.getDateWithoutFormat(yyyy + "0101",
				Calendar.YEAR, -1);
		String co2StartDate = CalendarUtil.getDateWithoutFormat(yyyy + "0101",
				Calendar.YEAR, -4);
		String emEndDate = CalendarUtil.getDateWithoutFormat(yyyy + "1231",
				Calendar.YEAR, -1);
		String co2EndDate = yyyy + "1231";
		params.put("startDate", emStartDate);
		params.put("endDate", emEndDate);
		params.put("dst", 0);

		params.put("channel", DefaultChannel.Usage.getCode());
		params.put("meterType", CommonConstants.MeterType.EnergyMeter
				.getMonthTableName());
		List<Object> emList = meteringDayDao
				.getUsageForSubLocationByMonth(params);

		params.put("meterType", CommonConstants.MeterType.GasMeter
				.getMonthTableName());
		List<Object> gmList = meteringDayDao
				.getUsageForSubLocationByMonth(params);

		params.put("meterType", CommonConstants.MeterType.WaterMeter
				.getMonthTableName());
		List<Object> wmList = meteringDayDao
				.getUsageForSubLocationByMonth(params);
		
		params.put("meterType", CommonConstants.MeterType.HeatMeter
				.getMonthTableName());
		List<Object> hmList = meteringDayDao
				.getUsageForSubLocationByMonth(params);

		params.put("startDate", co2StartDate);
		params.put("endDate", co2EndDate);
		params.put("channel", DefaultChannel.Co2.getCode());
		params.put("meterType", CommonConstants.MeterType.EnergyMeter
				.getMonthTableName());
		params.put("root", true);
		params.put("periodType", CommonConstants.DateType.MONTHLY.getCode());

		List<Object> emCo2List = meteringDayDao
				.getUsageForLocationByMonth(params);

		params.put("meterType", CommonConstants.MeterType.GasMeter
				.getMonthTableName());
		List<Object> gmCo2List = meteringDayDao
				.getUsageForLocationByMonth(params);

		params.put("meterType", CommonConstants.MeterType.WaterMeter
				.getMonthTableName());
		List<Object> wmCo2List = meteringDayDao
				.getUsageForLocationByMonth(params);
		
		params.put("meterType", CommonConstants.MeterType.HeatMeter
				.getMonthTableName());
		List<Object> hmCo2List = meteringDayDao
				.getUsageForLocationByMonth(params);
		// System.out.println("co2 params:" + params);
		BigDecimal emVal = new BigDecimal(0);
		BigDecimal gmVal = new BigDecimal(0);
		BigDecimal wmVal = new BigDecimal(0);
		BigDecimal hmVal = new BigDecimal(0);
		
		for (Object obj : emList) {

			Map<String, Object> emtmp = new HashMap<String, Object>();
			emtmp = (Map<String, Object>) obj;

			emVal = emVal.add(new BigDecimal(emtmp.get("TOTAL") == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emtmp.get("TOTAL"))).setScale(
					BigDecimal.ROUND_DOWN, 2));
		}

		for (Object obj : gmList) {
			Map<String, Object> gmtmp = new HashMap<String, Object>();
			gmtmp = (Map<String, Object>) obj;

			gmVal = gmVal.add(new BigDecimal(gmtmp.get("TOTAL") == null ? 0
					: DecimalUtil.ConvertNumberToDouble(gmtmp.get("TOTAL"))).setScale(
					BigDecimal.ROUND_DOWN, 2));
		}

		for (Object obj : wmList) {
			Map<String, Object> wmtmp = new HashMap<String, Object>();
			wmtmp = (Map<String, Object>) obj;

			wmVal = wmVal.add(new BigDecimal(wmtmp.get("TOTAL") == null ? 0
					: DecimalUtil.ConvertNumberToDouble(wmtmp.get("TOTAL"))).setScale(
					BigDecimal.ROUND_DOWN, 2));
		}
		
		for (Object obj : hmList) {
			Map<String, Object> hmtmp = new HashMap<String, Object>();
			hmtmp = (Map<String, Object>) obj;

			hmVal = hmVal.add(new BigDecimal(hmtmp.get("TOTAL") == null ? 0
					: DecimalUtil.ConvertNumberToDouble(hmtmp.get("TOTAL"))).setScale(
					BigDecimal.ROUND_DOWN, 2));
		}

		Map<String, Object> emCo2Map = new HashMap<String, Object>();
		Map<String, Object> gmCo2Map = new HashMap<String, Object>();
		Map<String, Object> wmCo2Map = new HashMap<String, Object>();
		Map<String, Object> hmCo2Map = new HashMap<String, Object>();
		
		for (Object obj : emCo2List) {
			Map<String, Object> emco2tmp = new HashMap<String, Object>();
			emco2tmp = (Map<String, Object>) obj;

			emCo2Map
					.put((String) emco2tmp.get("YYYYMM"), emco2tmp.get("TOTAL"));
		}

		for (Object obj : gmCo2List) {
			Map<String, Object> gmco2tmp = new HashMap<String, Object>();
			gmco2tmp = (Map<String, Object>) obj;

			gmCo2Map
					.put((String) gmco2tmp.get("YYYYMM"), gmco2tmp.get("TOTAL"));
		}

		for (Object obj : wmCo2List) {
			Map<String, Object> wmco2tmp = new HashMap<String, Object>();
			wmco2tmp = (Map<String, Object>) obj;
			wmCo2Map
					.put((String) wmco2tmp.get("YYYYMM"), wmco2tmp.get("TOTAL"));
		}
		
		for (Object obj : hmCo2List) {
			Map<String, Object> hmco2tmp = new HashMap<String, Object>();
			hmco2tmp = (Map<String, Object>) obj;
			hmCo2Map
					.put((String) hmco2tmp.get("YYYYMM"), hmco2tmp.get("TOTAL"));
		}

		List<Object> monthList = new ArrayList<Object>();

		for (int i = 0; i < 60; i++) {
			Map<String, Object> tmp = new HashMap<String, Object>();
			String yyyymmdd = CalendarUtil.getDateWithoutFormat(co2StartDate,
					Calendar.MONTH, i);
			String yyyymm = yyyymmdd.substring(0, 6);

			tmp.put("Co2Usage", new BigDecimal(emCo2Map.get(yyyymm) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emCo2Map.get(yyyymm))).add(
					new BigDecimal(gmCo2Map.get(yyyymm) == null ? 0: DecimalUtil.ConvertNumberToDouble(gmCo2Map.get(yyyymm))).add(
							new BigDecimal(wmCo2Map.get(yyyymm) == null ? 0: DecimalUtil.ConvertNumberToDouble(wmCo2Map.get(yyyymm))).add(
									new BigDecimal(hmCo2Map.get(yyyymm) == null ? 0: DecimalUtil.ConvertNumberToDouble(hmCo2Map.get(yyyymm)))	)))
										.setScale(BigDecimal.ROUND_DOWN, 2));

			tmp.put("date", yyyymmdd.substring(0, 4));
			monthList.add(tmp);
		}

		for (int i = 0; i < 5; i++) {

			BigDecimal co2Usage = new BigDecimal(0);

			String year = "";
			for (int j = i * 12; j < i * 12 + 12; j++) {
				Map<String, Object> monthtmp = (Map<String, Object>) monthList
						.get(j);

				co2Usage = co2Usage.add((BigDecimal) monthtmp.get("Co2Usage"))
						.divide(area, MathContext.DECIMAL32).setScale(
								BigDecimal.ROUND_DOWN, 2);
				year = (String) monthtmp.get("date");

			}

			Map<String, Object> quatermap = new HashMap<String, Object>();
			quatermap.put("xField", year);
			quatermap.put("Co2Usage", co2Usage);

			resultList.add(quatermap);
		}

		retMap.put("co2List", resultList);
		retMap.put("EmUsage", emVal.divide(area, MathContext.DECIMAL32));
		return retMap;
	}

	@SuppressWarnings("unchecked")
	private List<Object> getLocationUsage(Map<String, Object> params) {

		String periodType = (String) params.get("periodType");

		List<Object> resultList = new ArrayList<Object>();

		params.put("dst", 0);
		Map<String, Object> tmp = null;

		if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)
				|| CommonConstants.DateType.DAILY.getCode().equals(periodType)) {

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> emList = meteringDayDao
					.getUsageForSubLocationByDay(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayTableName());
			List<Object> gmList = meteringDayDao
					.getUsageForSubLocationByDay(params);

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayTableName());
			List<Object> wmList = meteringDayDao
					.getUsageForSubLocationByDay(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getDayTableName());
			List<Object> hmList = meteringDayDao
					.getUsageForSubLocationByDay(params);

			params.put("channel", DefaultChannel.Co2.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> emCo2List = meteringDayDao
					.getUsageForSubLocationByDay(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayTableName());
			List<Object> gmCo2List = meteringDayDao
					.getUsageForSubLocationByDay(params);

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayTableName());
			List<Object> wmCo2List = meteringDayDao
					.getUsageForSubLocationByDay(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getDayTableName());
			List<Object> hmCo2List = meteringDayDao
					.getUsageForSubLocationByDay(params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> gmMap = new HashMap<String, Object>();
			Map<String, Object> wmMap = new HashMap<String, Object>();
			Map<String, Object> hmMap = new HashMap<String, Object>();

			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			Map<String, Object> gmCo2Map = new HashMap<String, Object>();
			Map<String, Object> wmCo2Map = new HashMap<String, Object>();
			Map<String, Object> hmCo2Map = new HashMap<String, Object>();

			List<String> n1 = new ArrayList();
			for (Object obj : emList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				n1.add((String) emtmp.get("NAME"));
				emMap.put((String) emtmp.get("NAME"), emtmp.get("TOTAL"));
			}

			List<String> n2 = new ArrayList();
			for (Object obj : gmList) {
				Map<String, Object> gmtmp = new HashMap<String, Object>();
				gmtmp = (Map<String, Object>) obj;
				n2.add((String) gmtmp.get("NAME"));
				gmMap.put((String) gmtmp.get("NAME"), gmtmp.get("TOTAL"));
			}
			
			List<String> n3 = new ArrayList();
			for (Object obj : wmList) {
				Map<String, Object> wmtmp = new HashMap<String, Object>();
				wmtmp = (Map<String, Object>) obj;
				n3.add((String) wmtmp.get("NAME"));
				wmMap.put((String) wmtmp.get("NAME"), wmtmp.get("TOTAL"));
			}

			List<String> n4 = new ArrayList();
			for (Object obj : hmList) {
				Map<String, Object> hmtmp = new HashMap<String, Object>();
				hmtmp = (Map<String, Object>) obj;
				n4.add((String) hmtmp.get("NAME"));
				hmMap.put((String) hmtmp.get("NAME"), hmtmp.get("TOTAL"));
			}
			
			for (Object obj : emCo2List) {
				Map<String, Object> emco2tmp = new HashMap<String, Object>();
				emco2tmp = (Map<String, Object>) obj;

				emCo2Map.put((String) emco2tmp.get("NAME"), emco2tmp
						.get("TOTAL"));
			}

			for (Object obj : gmCo2List) {
				Map<String, Object> gmco2tmp = new HashMap<String, Object>();
				gmco2tmp = (Map<String, Object>) obj;

				gmCo2Map.put((String) gmco2tmp.get("NAME"), gmco2tmp
						.get("TOTAL"));
			}

			for (Object obj : wmCo2List) {
				Map<String, Object> wmco2tmp = new HashMap<String, Object>();
				wmco2tmp = (Map<String, Object>) obj;
				wmCo2Map.put((String) wmco2tmp.get("NAME"), wmco2tmp
						.get("TOTAL"));
			}

			for (Object obj : hmCo2List) {
				Map<String, Object> hmco2tmp = new HashMap<String, Object>();
				hmco2tmp = (Map<String, Object>) obj;
				hmCo2Map.put((String) hmco2tmp.get("NAME"), hmco2tmp
						.get("TOTAL"));
			}
			
			List<String> nameList = new ArrayList();
			if (n1.size() > 0) {
				nameList = n1;
			} else if (n2.size() > 0) {
				nameList = n2;
			} else if (n3.size() > 0) {
				nameList = n3;
			} else if (n4.size() > 0) {
				nameList = n4;
			}

			// System.out.println("nameList:"+nameList);
			for (String name : nameList) {

				tmp = new HashMap<String, Object>();
				tmp.put("xField", name);
				tmp.put("EmKGOE", new BigDecimal(emMap.get(name) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(emMap.get(name)) * KGOE.Energy.getValue()));
				tmp.put("EmUsage", new BigDecimal(emMap.get(name) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(emMap.get(name))));
				
				tmp.put("GmKGOE", new BigDecimal(gmMap.get(name) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(gmMap.get(name))));
				
				tmp.put("GmUsage", new BigDecimal(gmMap.get(name) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(gmMap.get(name)) * KGOE.GasLng.getValue()));
				
				tmp.put("WmKGOE", new BigDecimal(wmMap.get(name) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(wmMap.get(name)) * KGOE.Water.getValue()));		
				tmp.put("WmUsage", new BigDecimal(wmMap.get(name) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(wmMap.get(name))));
				
				tmp.put("HmKGOE", new BigDecimal(hmMap.get(name) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(hmMap.get(name)) * KGOE.Heat.getValue()));		
				tmp.put("HmUsage", new BigDecimal(hmMap.get(name) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(hmMap.get(name))));
				
				tmp.put("Co2Usage", new BigDecimal(
					emCo2Map.get(name) == null ? 0 : DecimalUtil.ConvertNumberToDouble(emCo2Map.get(name)))
						.add(new BigDecimal(gmCo2Map.get(name) == null ? 0: DecimalUtil.ConvertNumberToDouble(gmCo2Map.get(name)))
							.add(new BigDecimal(wmCo2Map.get(name) == null ? 0: DecimalUtil.ConvertNumberToDouble(wmCo2Map.get(name)))
								.add(new BigDecimal(hmCo2Map.get(name) == null ? 0: DecimalUtil.ConvertNumberToDouble(hmCo2Map.get(name)))))));
				resultList.add(tmp);
			}

		} else {

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthTableName());
			List<Object> emList = meteringDayDao
					.getUsageForSubLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthTableName());
			List<Object> gmList = meteringDayDao
					.getUsageForSubLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getMonthTableName());
			List<Object> wmList = meteringDayDao
					.getUsageForSubLocationByMonth(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getMonthTableName());
			List<Object> hmList = meteringDayDao
					.getUsageForSubLocationByMonth(params);

			params.put("channel", DefaultChannel.Co2.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthTableName());
			List<Object> emCo2List = meteringDayDao
					.getUsageForSubLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthTableName());
			List<Object> gmCo2List = meteringDayDao
					.getUsageForSubLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getMonthTableName());
			List<Object> wmCo2List = meteringDayDao
					.getUsageForSubLocationByMonth(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getMonthTableName());
			List<Object> hmCo2List = meteringDayDao
					.getUsageForSubLocationByMonth(params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> gmMap = new HashMap<String, Object>();
			Map<String, Object> wmMap = new HashMap<String, Object>();
			Map<String, Object> hmMap = new HashMap<String, Object>();

			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			Map<String, Object> gmCo2Map = new HashMap<String, Object>();
			Map<String, Object> wmCo2Map = new HashMap<String, Object>();
			Map<String, Object> hmCo2Map = new HashMap<String, Object>();

			List<String> n1 = new ArrayList();
			for (Object obj : emList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				n1.add((String) emtmp.get("NAME"));
				emMap.put((String) emtmp.get("NAME"), emtmp.get("TOTAL"));
			}

			List<String> n2 = new ArrayList();
			for (Object obj : gmList) {
				Map<String, Object> gmtmp = new HashMap<String, Object>();
				gmtmp = (Map<String, Object>) obj;
				n2.add((String) gmtmp.get("NAME"));
				gmMap.put((String) gmtmp.get("NAME"), gmtmp.get("TOTAL"));
			}
			
			List<String> n3 = new ArrayList();
			for (Object obj : wmList) {
				Map<String, Object> wmtmp = new HashMap<String, Object>();
				wmtmp = (Map<String, Object>) obj;
				n3.add((String) wmtmp.get("NAME"));
				wmMap.put((String) wmtmp.get("NAME"), wmtmp.get("TOTAL"));
			}
			
			List<String> n4 = new ArrayList();
			for (Object obj : hmList) {
				Map<String, Object> hmtmp = new HashMap<String, Object>();
				hmtmp = (Map<String, Object>) obj;
				n4.add((String) hmtmp.get("NAME"));
				hmMap.put((String) hmtmp.get("NAME"), hmtmp.get("TOTAL"));
			}

			for (Object obj : emCo2List) {
				Map<String, Object> emco2tmp = new HashMap<String, Object>();
				emco2tmp = (Map<String, Object>) obj;

				emCo2Map.put((String) emco2tmp.get("NAME"), emco2tmp
						.get("TOTAL"));
			}

			for (Object obj : gmCo2List) {
				Map<String, Object> gmco2tmp = new HashMap<String, Object>();
				gmco2tmp = (Map<String, Object>) obj;

				gmCo2Map.put((String) gmco2tmp.get("NAME"), gmco2tmp
						.get("TOTAL"));
			}

			for (Object obj : wmCo2List) {
				Map<String, Object> wmco2tmp = new HashMap<String, Object>();
				wmco2tmp = (Map<String, Object>) obj;
				wmCo2Map.put((String) wmco2tmp.get("NAME"), wmco2tmp
						.get("TOTAL"));
			}
			
			for (Object obj : hmCo2List) {
				Map<String, Object> hmco2tmp = new HashMap<String, Object>();
				hmco2tmp = (Map<String, Object>) obj;
				hmCo2Map.put((String) hmco2tmp.get("NAME"), hmco2tmp
						.get("TOTAL"));
			}

			List<String> nameList = new ArrayList();
			if (n1.size() > 0) {
				nameList = n1;
			} else if (n2.size() > 0) {
				nameList = n2;
			} else if (n3.size() > 0) {
				nameList = n3;
			} else if (n4.size() > 0) {
				nameList = n4;
			}

			// System.out.println("nameList:"+nameList);
			for (String name : nameList) {

				tmp = new HashMap<String, Object>();
				tmp.put("xField", name);
				
				tmp.put("EmKGOE", new BigDecimal(emMap.get(name) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(emMap.get(name)) * KGOE.Energy.getValue()));
				tmp.put("EmUsage", new BigDecimal(emMap.get(name) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(emMap.get(name))));
				
				tmp.put("GmKGOE", new BigDecimal(gmMap.get(name) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(gmMap.get(name)) * KGOE.GasLng.getValue()));
				tmp.put("GmUsage", new BigDecimal(gmMap.get(name) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(gmMap.get(name))));
				
				tmp.put("WmKGOE", new BigDecimal(wmMap.get(name) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(wmMap.get(name)) * KGOE.Water.getValue()));
				tmp.put("WmUsage", new BigDecimal(wmMap.get(name) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(wmMap.get(name))));
				
				tmp.put("HmKGOE", new BigDecimal(hmMap.get(name) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(hmMap.get(name)) * KGOE.Heat.getValue()));
				tmp.put("HmUsage", new BigDecimal(hmMap.get(name) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(hmMap.get(name))));
				
				tmp.put("Co2Usage", new BigDecimal(
					emCo2Map.get(name) == null ? 0 : DecimalUtil.ConvertNumberToDouble(emCo2Map.get(name))).add(
						new BigDecimal(gmCo2Map.get(name) == null ? 0: DecimalUtil.ConvertNumberToDouble(gmCo2Map.get(name))).add(
							new BigDecimal(wmCo2Map.get(name) == null ? 0: DecimalUtil.ConvertNumberToDouble(wmCo2Map.get(name))).add(
									new BigDecimal(hmCo2Map.get(name) == null ? 0: DecimalUtil.ConvertNumberToDouble(hmCo2Map.get(name)))))));
				resultList.add(tmp);
			}

		}

		return resultList;
	}

	
	/**
	 * 특정 EndDevice와 특정 검색 기준일로
	 * MeteringDayDao.getUsageForEndDevicesByDay 메소드의 params 파라미터 생성
	 * 
	 * @param endDevice
	 * @param yyyymmdd 검색 기준일
	 * @return Map
	 */
	private Map<String, Object> getDayEmByEndDeviceParam( EndDevice endDevice, String yyyymmdd ) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Integer> endDeviceId = new ArrayList<Integer>();
		List<Integer> modemId = new ArrayList<Integer>();
		List<Integer> meterId = new ArrayList<Integer>();
		
		endDeviceId.add(endDevice.getId());
		
		if ( endDevice.getModemId() != null ) {
			modemId.add(endDevice.getModemId());
		}
		
		if ( endDevice.getMeters() != null ) {
			for ( Meter meter : endDevice.getMeters() ) {
				if ( meter.getId() != null ) {
					meterId.add(meter.getId());
				}
			}
		}
				
		if ( modemId.isEmpty() ) {
			modemId.add(-1);
		}
		
		if ( meterId.isEmpty() ) {
			meterId.add(-1);
		}
		
		result.put("endDeviceId", endDeviceId); 
		result.put("modemId", modemId); // 특정 endDevice와 연결된 modem Id목록
		result.put("meterId", meterId); // 특정 endDevice와 연결된 meter Id목록
		result.put("today", yyyymmdd);	// 검색기준일
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private List<Object> getPeriodUsage(Map<String, Object> params, String lang) {

		// convertLocation(params);
		log.info(params);
		BemsStatisticUtil bemsUtil = new BemsStatisticUtil();

		String periodType = (String) params.get("periodType");
		Integer supplierId = (Integer) params.get("supplierId");
		
		DecimalFormat df = DecimalUtil.getDecimalFormat(supplierDao.get(supplierId).getMd());
		
		// String billPrefix = "Billing";
		String temperatureDayMeterType = "DAY_TM";
		String temperatureMonthMeterType = "MONTH_TM";

		String humidityDayMeterType = "DAY_HUM";
		String humidityMonthMeterType = "MONTH_HUM";

		List<Object> resultList = new ArrayList<Object>();
		params.put("dst", 0);
		Map<String, Object> tmp = null;

		boolean isEmEmpty = false;
		boolean isGmEmpty = false;
		boolean isWmEmpty = false;
		boolean isHmEmpty = false;
		
		if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {

			params.put("channel", DefaultChannel.Usage.getCode());
			
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> emList = meteringDayDao
					.getUsageForLocationByDay(params);

			if (emList.isEmpty() && !(Boolean) params.get("root")) {
				params.put("root", true);
				emList = meteringDayDao.getUsageForLocationByDay(params);
				params.put("root", false);
				isEmEmpty = true;
			}
			
			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayTableName());
			List<Object> gmList = meteringDayDao
					.getUsageForLocationByDay(params);

			if (gmList.isEmpty() && !(Boolean) params.get("root")) {
				params.put("root", true);
				gmList = meteringDayDao.getUsageForLocationByDay(params);
				params.put("root", false);
				isGmEmpty = true;
			}

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayTableName());
			List<Object> wmList = meteringDayDao
					.getUsageForLocationByDay(params);

			if (wmList.isEmpty() && !(Boolean) params.get("root")) {
				params.put("root", true);
				wmList = meteringDayDao.getUsageForLocationByDay(params);
				params.put("root", false);
				isWmEmpty = true;
			}

			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getDayTableName());
			List<Object> hmList = meteringDayDao
					.getUsageForLocationByDay(params);

			if (hmList.isEmpty() && !(Boolean) params.get("root")) {
				params.put("root", true);
				hmList = meteringDayDao.getUsageForLocationByDay(params);
				params.put("root", false);
				isHmEmpty = true;
			}
			
			params.put("meterType", temperatureDayMeterType);
			
			List<Object> tmInList = meteringDayDao
					.getTemperatureHumidityForLocationByDay(params);

			params.put("meterId", params.get("outMeterId"));
			List<Object> tmOutList = meteringDayDao
					.getTemperatureHumidityForLocationByDay(params);

			params.put("meterType", humidityDayMeterType);
			params.put("meterId", params.get("inMeterId"));
			List<Object> humInList = meteringDayDao
					.getTemperatureHumidityForLocationByDay(params);

			params.put("meterId", params.get("outMeterId"));
			List<Object> humOutList = meteringDayDao
					.getTemperatureHumidityForLocationByDay(params);

			params.put("channel", DefaultChannel.Co2.getCode());
			
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());

			List<Object> emCo2List = null;
			emCo2List = meteringDayDao.getUsageForLocationByDay(params);
			if (isEmEmpty && !(Boolean) params.get("root")) {
				params.put("root", true);
				emCo2List = meteringDayDao.getUsageForLocationByDay(params);
				params.put("root", false);
			}

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayTableName());
			
			List<Object> gmCo2List = null;
			gmCo2List = meteringDayDao.getUsageForLocationByDay(params);
			if (isGmEmpty && !(Boolean) params.get("root")) {
				params.put("root", true);
				gmCo2List = meteringDayDao.getUsageForLocationByDay(params);
				params.put("root", false);
			}

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayTableName());
			
			List<Object> wmCo2List = null;
			wmCo2List = meteringDayDao.getUsageForLocationByDay(params);
			if (isWmEmpty && !(Boolean) params.get("root")) {
				params.put("root", true);
				wmCo2List = meteringDayDao.getUsageForLocationByDay(params);
				params.put("root", false);
			}
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getDayTableName());
			
			List<Object> hmCo2List = null;
			hmCo2List = meteringDayDao.getUsageForLocationByDay(params);
			if (isHmEmpty && !(Boolean) params.get("root")) {
				params.put("root", true);
				hmCo2List = meteringDayDao.getUsageForLocationByDay(params);
				params.put("root", false);
			}

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> gmMap = new HashMap<String, Object>();
			Map<String, Object> wmMap = new HashMap<String, Object>();
			Map<String, Object> hmMap = new HashMap<String, Object>();

			Map<String, Object> tmInMaxMap = new HashMap<String, Object>();
			Map<String, Object> tmOutMaxMap = new HashMap<String, Object>();
			Map<String, Object> humInMaxMap = new HashMap<String, Object>();
			Map<String, Object> humOutMaxMap = new HashMap<String, Object>();

			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			Map<String, Object> gmCo2Map = new HashMap<String, Object>();
			Map<String, Object> wmCo2Map = new HashMap<String, Object>();
			Map<String, Object> hmCo2Map = new HashMap<String, Object>();

			if (emList != null && emList.size() > 0) {
				emMap = (Map<String, Object>) emList.get(0);
			}
			if (gmList != null && gmList.size() > 0) {
				gmMap = (Map<String, Object>) gmList.get(0);
			}
			if (wmList != null && wmList.size() > 0) {
				wmMap = (Map<String, Object>) wmList.get(0);
			}
			if (hmList != null && hmList.size() > 0) {
				hmMap = (Map<String, Object>) hmList.get(0);
			}


			if (tmInList != null && tmInList.size() > 0) {
				tmInMaxMap = (Map<String, Object>) tmInList.get(0);
			}
			if (tmOutList != null && tmOutList.size() > 0) {
				tmOutMaxMap = (Map<String, Object>) tmOutList.get(0);
			}

			if (humInList != null && humInList.size() > 0) {
				humInMaxMap = (Map<String, Object>) humInList.get(0);
			}

			if (humOutList != null && humOutList.size() > 0) {
				humOutMaxMap = (Map<String, Object>) humOutList.get(0);
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
			if (hmCo2List != null && hmCo2List.size() > 0) {
				hmCo2Map = (Map<String, Object>) hmCo2List.get(0);
			}

			BigDecimal eUsage = new BigDecimal(0);
			BigDecimal gUsage = new BigDecimal(0);
			BigDecimal wUsage = new BigDecimal(0);
			BigDecimal hUsage = new BigDecimal(0);
			
			String hh = "";
			for (int i = 0; i < 24; i++) {
				hh = TimeUtil.to2Digit(i);
				
				tmp = new HashMap<String, Object>();
				tmp.put("xField", hh);
				eUsage = new BigDecimal(emMap.get("VALUE" + hh) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(emMap.get("VALUE" + hh)));
				
				
				eUsage= eUsage;				
				tmp.put("EmUsage", eUsage);
				tmp.put("EmKGOE", new BigDecimal(
						emMap.get("VALUE" + hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(emMap
								.get("VALUE" + hh))
								* KGOE.Energy.getValue()));
				
				gUsage = new BigDecimal(gmMap.get("VALUE" + hh) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(gmMap.get("VALUE" + hh)));
				tmp.put("GmUsage", gUsage);
				tmp.put("GmKGOE", new BigDecimal(
						gmMap.get("VALUE" + hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(gmMap
								.get("VALUE" + hh))
								* KGOE.GasLng.getValue()));
				
				wUsage = new BigDecimal(wmMap.get("VALUE" + hh) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(wmMap.get("VALUE" + hh)));		
				tmp.put("WmUsage", wUsage);
				tmp.put("WmKGOE", new BigDecimal(
						wmMap.get("VALUE" + hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(wmMap
								.get("VALUE" + hh))
								* KGOE.Water.getValue()));
				
				hUsage = new BigDecimal(hmMap.get("VALUE" + hh) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(hmMap.get("VALUE" + hh)));				
				tmp.put("HmUsage", hUsage);
				tmp.put("HmKGOE", new BigDecimal(
						hmMap.get("VALUE" + hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(hmMap
								.get("VALUE" + hh))
								* KGOE.Heat.getValue()));
				
				Map<String, Object> chargeMap = new HashMap<String, Object>();
				
				chargeMap.put("serviceType", MeterType.EnergyMeter
						.getServiceType());
				chargeMap.put("dateType", DateType.DAILY.getCode());
				chargeMap.put("usage", eUsage.doubleValue());
				chargeMap.put("period", 1);
				tmp.put("EmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));
				
				chargeMap.put("serviceType", MeterType.GasMeter
						.getServiceType());
				chargeMap.put("usage", gUsage.doubleValue());
				tmp.put("GmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));

				chargeMap.put("serviceType", MeterType.WaterMeter
						.getServiceType());
				chargeMap.put("usage", wUsage.doubleValue());
				tmp.put("WmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));

				chargeMap.put("serviceType", MeterType.HeatMeter
						.getServiceType());
				chargeMap.put("usage", hUsage.doubleValue());
				tmp.put("HmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));
			
				tmp.put("TmInMaxUsage", null);
				tmp.put("TmOutMaxUsage", null);
				tmp.put("TmInMinUsage", null);
				tmp.put("TmOutMinUsage", null);
				tmp.put("TmOutUsage", new BigDecimal(tmOutMaxMap.get("VALUE"
						+ hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(tmOutMaxMap.get("VALUE"
						+ hh))));
				
				tmp.put("TmGridOutUsage", new BigDecimal(tmOutMaxMap.get("VALUE"
						+ hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(tmOutMaxMap.get("VALUE"
						+ hh))));
				
				tmp.put("TmInUsage", new BigDecimal(tmInMaxMap 
						.get("VALUE" + hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(tmInMaxMap
						.get("VALUE" + hh))));
				
				
				tmp.put("TmGridInUsage", new BigDecimal(tmInMaxMap
						.get("VALUE" + hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(tmInMaxMap
						.get("VALUE" + hh))));
				
				tmp.put("HumOutUsage", new BigDecimal(humOutMaxMap.get("VALUE"
						+ hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(humOutMaxMap.get("VALUE"
						+ hh))));
				
				tmp.put("HumGridOutUsage", new BigDecimal(humOutMaxMap.get("VALUE"
						+ hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(humOutMaxMap.get("VALUE"
						+ hh))));  

				tmp.put("HumInMaxUsage", null);
				tmp.put("HumOutMaxUsage", null);
				tmp.put("HumInMinUsage", null);
				tmp.put("HumOutMinUsage", null);

				tmp.put("HumInUsage", new BigDecimal(humInMaxMap.get("VALUE"
						+ hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(humInMaxMap.get("VALUE"
						+ hh))));
				tmp.put("HumOutUsage", new BigDecimal(humInMaxMap.get("VALUE"
						+ hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(humInMaxMap.get("VALUE"
						+ hh))));
				tmp.put("HumGridInUsage", new BigDecimal(humInMaxMap.get("VALUE"
						+ hh) == null ? 0 : DecimalUtil.ConvertNumberToDouble(humInMaxMap.get("VALUE"
						+ hh))));

				tmp.put("Co2Usage", new BigDecimal(emCo2Map.get("VALUE" + hh) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emCo2Map.get("VALUE" + hh))).add(
						new BigDecimal(gmCo2Map.get("VALUE" + hh) == null ? 0
							: DecimalUtil.ConvertNumberToDouble(gmCo2Map.get("VALUE" + hh))).add(
									new BigDecimal(wmCo2Map.get("VALUE" + hh) == null ? 0
										: DecimalUtil.ConvertNumberToDouble(wmCo2Map.get("VALUE"+ hh))).add(
											new BigDecimal(hmCo2Map.get("VALUE" + hh) == null ? 0
												: DecimalUtil.ConvertNumberToDouble(hmCo2Map.get("VALUE" + hh)))))));
				resultList.add(tmp);
			}

		} else if (CommonConstants.DateType.DAILY.getCode().equals(periodType)) {

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> emList = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayTableName());
			List<Object> gmList = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayTableName());
			List<Object> wmList = meteringDayDao
					.getUsageForLocationByDay(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getDayTableName());
			List<Object> hmList = meteringDayDao
					.getUsageForLocationByDay(params);
			
			params.put("meterType", temperatureDayMeterType);

			List<Object> tmInList = meteringDayDao
					.getTemperatureHumidityForLocationByDay(params);

			params.put("meterId", params.get("outMeterId"));
			List<Object> tmOutList = meteringDayDao
					.getTemperatureHumidityForLocationByDay(params);

			params.put("meterType", humidityDayMeterType);
			params.put("meterId", params.get("inMeterId"));
			List<Object> humInList = meteringDayDao
					.getTemperatureHumidityForLocationByDay(params);

			params.put("meterId", params.get("outMeterId"));
			List<Object> humOutList = meteringDayDao
					.getTemperatureHumidityForLocationByDay(params);

			params.put("channel", DefaultChannel.Co2.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> emCo2List = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayTableName());
			List<Object> gmCo2List = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayTableName());
			List<Object> wmCo2List = meteringDayDao
					.getUsageForLocationByDay(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getDayTableName());
			List<Object> hmCo2List = meteringDayDao
					.getUsageForLocationByDay(params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> gmMap = new HashMap<String, Object>();
			Map<String, Object> wmMap = new HashMap<String, Object>();
			Map<String, Object> hmMap = new HashMap<String, Object>();

			Map<String, Object> tmInMaxMap = new HashMap<String, Object>();
			Map<String, Object> tmInMinMap = new HashMap<String, Object>();

			Map<String, Object> tmOutMaxMap = new HashMap<String, Object>();
			Map<String, Object> tmOutMinMap = new HashMap<String, Object>();
			Map<String, Object> humInMaxMap = new HashMap<String, Object>();
			Map<String, Object> humInMinMap = new HashMap<String, Object>();

			Map<String, Object> humOutMaxMap = new HashMap<String, Object>();
			Map<String, Object> humOutMinMap = new HashMap<String, Object>();

			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			Map<String, Object> gmCo2Map = new HashMap<String, Object>();
			Map<String, Object> wmCo2Map = new HashMap<String, Object>();
			Map<String, Object> hmCo2Map = new HashMap<String, Object>();

			for (Object obj : emList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				emMap.put((String) emtmp.get("YYYYMMDD"), emtmp.get("TOTAL"));
			}

			for (Object obj : gmList) {
				Map<String, Object> gmtmp = new HashMap<String, Object>();
				gmtmp = (Map<String, Object>) obj;
				gmMap.put((String) gmtmp.get("YYYYMMDD"), gmtmp.get("TOTAL"));
			}

			for (Object obj : wmList) {
				Map<String, Object> wmtmp = new HashMap<String, Object>();
				wmtmp = (Map<String, Object>) obj;
				wmMap.put((String) wmtmp.get("YYYYMMDD"), wmtmp.get("TOTAL"));
			}

			for (Object obj : hmList) {
				Map<String, Object> hmtmp = new HashMap<String, Object>();
				hmtmp = (Map<String, Object>) obj;
				hmMap.put((String) hmtmp.get("YYYYMMDD"), hmtmp.get("TOTAL"));
			}
           
			for (Object obj : tmInList) {
				Map<String, Object> tmtmp = new HashMap<String, Object>();
				tmtmp = (Map<String, Object>) obj;
				tmInMaxMap.put((String) tmtmp.get("YYYYMMDD"), tmtmp
						.get("MAXIMUMVALUE"));
				tmInMinMap.put((String) tmtmp.get("YYYYMMDD"), tmtmp
						.get("MINIMUMVALUE"));
			}
			
			for (Object obj : tmOutList) {
				Map<String, Object> tmtmp = new HashMap<String, Object>();
				tmtmp = (Map<String, Object>) obj;
				tmOutMaxMap.put((String) tmtmp.get("YYYYMMDD"), tmtmp
						.get("MAXIMUMVALUE"));
				tmOutMinMap.put((String) tmtmp.get("YYYYMMDD"), tmtmp
						.get("MINIMUMVALUE"));
			}

			
			for (Object obj : humInList) {
				Map<String, Object> humtmp = new HashMap<String, Object>();
				humtmp = (Map<String, Object>) obj;
				humInMaxMap.put((String) humtmp.get("YYYYMMDD"), humtmp
						.get("MAXIMUMVALUE"));
				humInMinMap.put((String) humtmp.get("YYYYMMDD"), humtmp
						.get("MINIMUMVALUE"));
			}

			for (Object obj : humOutList) {
				Map<String, Object> humtmp = new HashMap<String, Object>();
				humtmp = (Map<String, Object>) obj;
				humOutMaxMap.put((String) humtmp.get("YYYYMMDD"), humtmp
						.get("MAXIMUMVALUE"));
				humOutMinMap.put((String) humtmp.get("YYYYMMDD"), humtmp
						.get("MINIMUMVALUE"));
			}

			for (Object obj : emCo2List) {
				Map<String, Object> emco2tmp = new HashMap<String, Object>();
				emco2tmp = (Map<String, Object>) obj;
				emCo2Map.put((String) emco2tmp.get("YYYYMMDD"), emco2tmp
						.get("TOTAL"));
			}

			for (Object obj : gmCo2List) {
				Map<String, Object> gmco2tmp = new HashMap<String, Object>();
				gmco2tmp = (Map<String, Object>) obj;
				gmCo2Map.put((String) gmco2tmp.get("YYYYMMDD"), gmco2tmp
						.get("TOTAL"));
			}

			for (Object obj : wmCo2List) {
				Map<String, Object> wmco2tmp = new HashMap<String, Object>();
				wmco2tmp = (Map<String, Object>) obj;
				wmCo2Map.put((String) wmco2tmp.get("YYYYMMDD"), wmco2tmp
						.get("TOTAL"));
			}
			
			for (Object obj : hmCo2List) {
				Map<String, Object> hmco2tmp = new HashMap<String, Object>();
				hmco2tmp = (Map<String, Object>) obj;
				hmCo2Map.put((String) hmco2tmp.get("YYYYMMDD"), hmco2tmp
						.get("TOTAL"));
			}

			BigDecimal eUsage = new BigDecimal(0);
			BigDecimal gUsage = new BigDecimal(0);
			BigDecimal wUsage = new BigDecimal(0);
			BigDecimal hUsage = new BigDecimal(0);
			
			for (int i = 0; i < 7; i++) {

				String yyyymmdd = CalendarUtil.getDateWithoutFormat(params
						.get("startDate")
						+ "", Calendar.DATE, i);

				tmp = new HashMap<String, Object>();
				tmp.put("yyyymmdd", yyyymmdd);
				tmp.put("xField", CalendarUtil.getWeekDay(lang, 
				        Integer.parseInt(yyyymmdd.substring(0, 4)),
				        Integer.parseInt(yyyymmdd.substring(4, 6)),
				        Integer.parseInt(yyyymmdd.substring(6, 8))));
				
				eUsage = new BigDecimal(emMap.get(yyyymmdd) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(emMap.get(yyyymmdd)));
				tmp.put("EmUsage", eUsage);
				tmp.put("EmKGOE", new BigDecimal(
						emMap.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(emMap
								.get(yyyymmdd))
								* KGOE.Energy.getValue()));
				
				gUsage = new BigDecimal(gmMap.get(yyyymmdd) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(gmMap.get(yyyymmdd)));
				tmp.put("GmUsage", gUsage);
				tmp.put("GmKGOE", new BigDecimal(
						gmMap.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(gmMap
								.get(yyyymmdd))
								* KGOE.GasLng.getValue()));

				wUsage = new BigDecimal(wmMap.get(yyyymmdd) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(wmMap.get(yyyymmdd)));
				tmp.put("WmUsage", wUsage);
				tmp.put("WmKGOE", new BigDecimal(
						wmMap.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(wmMap
								.get(yyyymmdd))
								* KGOE.Water.getValue()));
				
				hUsage = new BigDecimal(hmMap.get(yyyymmdd) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(hmMap.get(yyyymmdd)));	
				tmp.put("HmUsage", hUsage);
				tmp.put("HmKGOE", new BigDecimal(
						hmMap.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(hmMap
								.get(yyyymmdd))
								* KGOE.Heat.getValue()));
				
				Map<String, Object> chargeMap = new HashMap<String, Object>();
				
				chargeMap.put("serviceType", MeterType.EnergyMeter
						.getServiceType());
				chargeMap.put("dateType", DateType.DAILY.getCode());
				chargeMap.put("usage", eUsage.doubleValue());
				chargeMap.put("period", 1);
				tmp.put("EmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));
				
				chargeMap.put("serviceType", MeterType.GasMeter
						.getServiceType());
				chargeMap.put("usage", gUsage.doubleValue());
				tmp.put("GmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));

				chargeMap.put("serviceType", MeterType.WaterMeter
						.getServiceType());
				chargeMap.put("usage", wUsage.doubleValue());
				tmp.put("WmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));
			
				chargeMap.put("serviceType", MeterType.HeatMeter
						.getServiceType());
				chargeMap.put("usage", hUsage.doubleValue());
				tmp.put("HmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));
			
				tmp.put("TmInMaxUsage", new BigDecimal(
						tmInMaxMap.get(yyyymmdd) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(tmInMaxMap.get(yyyymmdd))));
				tmp.put("TmInMinUsage", new BigDecimal(
						tmInMinMap.get(yyyymmdd) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(tmInMinMap.get(yyyymmdd))));

				tmp.put("TmGridInUsage", (tmp.get("TmInMaxUsage") + "/" + tmp
						.get("TmInMinUsage")));

				tmp.put("TmOutMaxUsage", new BigDecimal(tmOutMaxMap
						.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(tmOutMaxMap
						.get(yyyymmdd))));
				tmp.put("TmOutMinUsage", new BigDecimal(tmOutMinMap
						.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(tmOutMinMap
						.get(yyyymmdd))));
				tmp.put("TmGridOutUsage", (tmp.get("TmOutMaxUsage") + "/" + tmp
						.get("TmOutMinUsage")));
				tmp.put("HumInMaxUsage", new BigDecimal(humInMaxMap
						.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(humInMaxMap
						.get(yyyymmdd))));
				tmp.put("HumInMinUsage", new BigDecimal(humInMinMap
						.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(humInMinMap
						.get(yyyymmdd))));

				tmp.put("HumGridInUsage", (tmp.get("HumInMaxUsage") + "/" + tmp
						.get("HumInMinUsage")));
				tmp.put("HumOutMaxUsage", new BigDecimal(humOutMaxMap
						.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(humOutMaxMap
						.get(yyyymmdd))));
				tmp.put("HumOutMinUsage", new BigDecimal(humOutMinMap
						.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(humOutMinMap
						.get(yyyymmdd))));
				tmp.put("HumGridOutUsage", (tmp.get("HumOutMaxUsage") + "/" + tmp
						.get("HumOutMinUsage")));

				tmp.put("Co2Usage", new BigDecimal(
					emCo2Map.get(yyyymmdd) == null ? 0 : DecimalUtil.ConvertNumberToDouble(emCo2Map.get(yyyymmdd))).add(
						new BigDecimal(gmCo2Map.get(yyyymmdd) == null ? 0
							: DecimalUtil.ConvertNumberToDouble(gmCo2Map.get(yyyymmdd))).add(
								new BigDecimal(wmCo2Map.get(yyyymmdd) == null ? 0
									: DecimalUtil.ConvertNumberToDouble(wmCo2Map.get(yyyymmdd))).add(
										new BigDecimal(hmCo2Map.get(yyyymmdd) == null ? 0
											: DecimalUtil.ConvertNumberToDouble(hmCo2Map.get(yyyymmdd)))))));
				resultList.add(tmp);
			}

		} else if (CommonConstants.DateType.MONTHLY.getCode()
				.equals(periodType)) {

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthTableName());
			List<Object> emList = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthTableName());
			List<Object> gmList = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getMonthTableName());
			List<Object> wmList = meteringDayDao
					.getUsageForLocationByMonth(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getMonthTableName());
			List<Object> hmList = meteringDayDao
					.getUsageForLocationByMonth(params);
		
			params.put("meterType", temperatureMonthMeterType);
			params.put("meterId", params.get("inMeterId"));
			
			List<Object> tmInList = meteringDayDao
					.getTemperatureHumidityForLocationByMonth(params);
			
			params.put("meterId", params.get("outMeterId"));
			
			List<Object> tmOutList = meteringDayDao
					.getTemperatureHumidityForLocationByMonth(params);

			params.put("meterType", humidityMonthMeterType);
			params.put("meterId", params.get("inMeterId"));
			
			List<Object> humInList = meteringDayDao
					.getTemperatureHumidityForLocationByMonth(params);

			params.put("meterId", params.get("outMeterId"));
			
			List<Object> humOutList = meteringDayDao
					.getTemperatureHumidityForLocationByMonth(params);
			
			params.put("channel", DefaultChannel.Co2.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthTableName());
			List<Object> emCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthTableName());
			List<Object> gmCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getMonthTableName());
			List<Object> wmCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getMonthTableName());
			List<Object> hmCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> gmMap = new HashMap<String, Object>();
			Map<String, Object> wmMap = new HashMap<String, Object>();
			Map<String, Object> hmMap = new HashMap<String, Object>();

			Map<String, Object> tmInMaxMap = new HashMap<String, Object>();
			Map<String, Object> tmInMinMap = new HashMap<String, Object>();
			Map<String, Object> tmOutMaxMap = new HashMap<String, Object>();
			Map<String, Object> tmOutMinMap = new HashMap<String, Object>();

			Map<String, Object> humInMaxMap = new HashMap<String, Object>();
			Map<String, Object> humInMinMap = new HashMap<String, Object>();

			Map<String, Object> humOutMaxMap = new HashMap<String, Object>();
			Map<String, Object> humOutMinMap = new HashMap<String, Object>();

			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			Map<String, Object> gmCo2Map = new HashMap<String, Object>();
			Map<String, Object> wmCo2Map = new HashMap<String, Object>();
			Map<String, Object> hmCo2Map = new HashMap<String, Object>();

			for (Object obj : emList) {
				Map<String, Object> emtmp = (Map<String, Object>) obj;
				emMap.put((String) emtmp.get("YYYYMM"), emtmp.get("TOTAL"));
			}

			for (Object obj : gmList) {
				Map<String, Object> gmtmp = (Map<String, Object>) obj;
				gmMap.put((String) gmtmp.get("YYYYMM"), gmtmp.get("TOTAL"));
			}

			for (Object obj : wmList) {
				Map<String, Object> wmtmp = (Map<String, Object>) obj;
				wmMap.put((String) wmtmp.get("YYYYMM"), wmtmp.get("TOTAL"));
			}
			
			for (Object obj : hmList) {
				Map<String, Object> hmtmp = (Map<String, Object>) obj;
				hmMap.put((String) hmtmp.get("YYYYMM"), hmtmp.get("TOTAL"));
			}

			for (Object obj : emCo2List) {
				Map<String, Object> emco2tmp = (Map<String, Object>) obj;
				emCo2Map.put((String) emco2tmp.get("YYYYMM"), emco2tmp
						.get("TOTAL"));
			}
			
			for (Object obj : gmCo2List) {
				Map<String, Object> gmco2tmp = (Map<String, Object>) obj;
				gmCo2Map.put((String) gmco2tmp.get("YYYYMM"), gmco2tmp
						.get("TOTAL"));
			}

			for (Object obj : wmCo2List) {
				Map<String, Object> wmco2tmp = (Map<String, Object>) obj;
				wmCo2Map.put((String) wmco2tmp.get("YYYYMM"), wmco2tmp
						.get("TOTAL"));
			}
			
			for (Object obj : hmCo2List) {
				Map<String, Object> hmco2tmp = (Map<String, Object>) obj;
				hmCo2Map.put((String) hmco2tmp.get("YYYYMM"), hmco2tmp
						.get("TOTAL"));
			}
			
			for (Object obj : tmInList) {
				Map<String, Object> tmtmp = new HashMap<String, Object>();
				tmtmp = (Map<String, Object>) obj;
				tmInMaxMap.put((String) tmtmp.get("YYYYMM"), tmtmp
						.get("MAXIMUMVALUE"));
				tmInMinMap.put((String) tmtmp.get("YYYYMM"), tmtmp
						.get("MINIMUMVALUE"));
			}
			
			for (Object obj : tmOutList) {
				
				Map<String, Object> tmtmp = new HashMap<String, Object>();
				tmtmp = (Map<String, Object>) obj;
				tmOutMaxMap.put((String) tmtmp.get("YYYYMM"), tmtmp
						.get("MAXIMUMVALUE"));
				tmOutMinMap.put((String) tmtmp.get("YYYYMM"), tmtmp
						.get("MINIMUMVALUE"));
			}
			
			for (Object obj : humInList) {
				Map<String, Object> humtmp = new HashMap<String, Object>();
				humtmp = (Map<String, Object>) obj;
				humInMaxMap.put((String) humtmp.get("YYYYMM"), humtmp
						.get("MAXIMUMVALUE"));
				humInMinMap.put((String) humtmp.get("YYYYMM"), humtmp
						.get("MINIMUMVALUE"));
			}
			for (Object obj : humOutList) {
				Map<String, Object> humtmp = new HashMap<String, Object>();
				humtmp = (Map<String, Object>) obj;
				humOutMaxMap.put((String) humtmp.get("YYYYMM"), humtmp
						.get("MAXIMUMVALUE"));
				humOutMinMap.put((String) humtmp.get("YYYYMM"), humtmp
						.get("MINIMUMVALUE"));
			}
			
			
			BigDecimal eUsage = new BigDecimal(0);
			BigDecimal gUsage = new BigDecimal(0);
			BigDecimal wUsage = new BigDecimal(0);
			BigDecimal hUsage = new BigDecimal(0);
			
			for (int i = 0; i < 12; i++) {

				String yyyymm = CalendarUtil.getDateWithoutFormat(params
						.get("startDate")
						+ "01", Calendar.MONTH, i);
				
				tmp = new HashMap<String, Object>();
				
				yyyymm = yyyymm.substring(0,6);
				String xField = "" + StringUtil.getDigitOnly(yyyymm.substring(4,6));
				tmp.put("yyyymm", yyyymm);
				tmp.put("xField", xField);

				eUsage = new BigDecimal(emMap.get(yyyymm) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(emMap.get(yyyymm)));				
				tmp.put("EmUsage", eUsage);
				tmp.put("EmKGOE", new BigDecimal(emMap.get(yyyymm) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(emMap.get(yyyymm)) * KGOE.Energy.getValue()));

				gUsage = new BigDecimal(gmMap.get(yyyymm) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(gmMap.get(yyyymm))); 				
				tmp.put("GmUsage", gUsage);
				tmp.put("GmKGOE", new BigDecimal(gmMap.get(yyyymm) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(gmMap.get(yyyymm)) * KGOE.GasLng.getValue()));

				wUsage = new BigDecimal(wmMap.get(yyyymm) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(wmMap.get(yyyymm)));
				tmp.put("WmUsage", wUsage);				
				tmp.put("WmKGOE", new BigDecimal(wmMap.get(yyyymm) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(wmMap.get(yyyymm)) * KGOE.Water.getValue()));
				
				hUsage = new BigDecimal(hmMap.get(yyyymm) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(hmMap.get(yyyymm)));
				tmp.put("HmUsage", hUsage);				
				tmp.put("HmKGOE", new BigDecimal(hmMap.get(yyyymm) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(hmMap.get(yyyymm)) * KGOE.Heat.getValue()));
				
				Map<String, Object> chargeMap = new HashMap<String, Object>();
				chargeMap.put("serviceType", MeterType.EnergyMeter
						.getServiceType());
				chargeMap.put("dateType", DateType.MONTHLY.getCode());
				chargeMap.put("usage", eUsage.doubleValue());
				chargeMap.put("period", 1);
				tmp.put("EmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));
				
				chargeMap.put("serviceType", MeterType.GasMeter
						.getServiceType());
				chargeMap.put("usage", gUsage.doubleValue());
				tmp.put("GmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));

				chargeMap.put("serviceType", MeterType.WaterMeter
						.getServiceType());
				chargeMap.put("usage", wUsage.doubleValue());
				tmp.put("WmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));
				
				chargeMap.put("serviceType", MeterType.HeatMeter
						.getServiceType());
				chargeMap.put("usage", hUsage.doubleValue());
				tmp.put("HmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));
			
				tmp.put("TmInMaxUsage", new BigDecimal(
						tmInMaxMap.get(yyyymm) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(tmInMaxMap.get(yyyymm))));
				tmp.put("TmInMinUsage", new BigDecimal(
						tmInMinMap.get(yyyymm) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(tmInMinMap.get(yyyymm))));
				tmp.put("TmGridInUsage", (tmp.get("TmInMaxUsage") + "/" + tmp
						.get("TmInMinUsage")));
				tmp.put("TmOutMaxUsage", new BigDecimal(
						tmOutMaxMap.get(yyyymm) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(tmOutMaxMap.get(yyyymm))));
				tmp.put("TmOutMinUsage", new BigDecimal(
						tmOutMinMap.get(yyyymm) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(tmOutMinMap.get(yyyymm))));
				tmp.put("TmGridOutUsage", (tmp.get("TmOutMaxUsage") + "/" + tmp
						.get("TmOutMinUsage")));
				tmp.put("HumInMaxUsage", new BigDecimal(
						humInMaxMap.get(yyyymm) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(humInMaxMap.get(yyyymm))));
				tmp.put("HumInMinUsage", new BigDecimal(
						humInMinMap.get(yyyymm) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(humInMinMap.get(yyyymm))));
				tmp.put("HumGridInUsage", (tmp.get("HumInMaxUsage") + "/" + tmp
						.get("HumInMinUsage")));
				tmp.put("HumOutMaxUsage", new BigDecimal(humOutMaxMap
						.get(yyyymm) == null ? 0 : DecimalUtil.ConvertNumberToDouble(humOutMaxMap
						.get(yyyymm))));
				tmp.put("HumOutMinUsage", new BigDecimal(humOutMinMap
						.get(yyyymm) == null ? 0 : DecimalUtil.ConvertNumberToDouble(humOutMinMap
						.get(yyyymm))));
				tmp.put("HumGridOutUsage", (tmp.get("HumOutMaxUsage") + "/" + tmp
						.get("HumOutMinUsage")));
				tmp.put("Co2Usage", new BigDecimal(emCo2Map.get(yyyymm) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emCo2Map.get(yyyymm))).add(
						new BigDecimal(gmCo2Map.get(yyyymm) == null ? 0
							: DecimalUtil.ConvertNumberToDouble(gmCo2Map.get(yyyymm))).add(
									new BigDecimal(wmCo2Map.get(yyyymm) == null ? 0
										: DecimalUtil.ConvertNumberToDouble(wmCo2Map.get(yyyymm))).add(
												new BigDecimal(hmCo2Map.get(yyyymm) == null ? 0
														: DecimalUtil.ConvertNumberToDouble(hmCo2Map.get(yyyymm)))))));

				resultList.add(tmp);
			}
		} else if (CommonConstants.DateType.QUARTERLY.getCode().equals(
				periodType)) {

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthTableName());
			List<Object> emList = meteringDayDao
					.getUsageForLocationByMonth(params);
			// System.out.println("$$$$$$$$$:" + emList);
			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthTableName());
			List<Object> gmList = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getMonthTableName());
			List<Object> wmList = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getMonthTableName());
			List<Object> hmList = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("meterType", temperatureMonthMeterType);
			params.put("meterId", params.get("inMeterId"));
			List<Object> tmInList = meteringDayDao
					.getTemperatureHumidityForLocationByMonth(params);

			params.put("meterId", params.get("outMeterId"));
			List<Object> tmOutList = meteringDayDao
					.getTemperatureHumidityForLocationByMonth(params);

			params.put("meterType", humidityMonthMeterType);
			params.put("meterId", params.get("inMeterId"));
			List<Object> humInList = meteringDayDao
					.getTemperatureHumidityForLocationByMonth(params);

			params.put("meterId", params.get("outMeterId"));
			List<Object> humOutList = meteringDayDao
					.getTemperatureHumidityForLocationByMonth(params);

			params.put("channel", DefaultChannel.Co2.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthTableName());
			List<Object> emCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthTableName());
			List<Object> gmCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getMonthTableName());
			List<Object> wmCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getMonthTableName());
			List<Object> hmCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> gmMap = new HashMap<String, Object>();
			Map<String, Object> wmMap = new HashMap<String, Object>();
			Map<String, Object> hmMap = new HashMap<String, Object>();

			Map<String, Object> tmInMaxMap = new HashMap<String, Object>();
			Map<String, Object> tmInMinMap = new HashMap<String, Object>();
			Map<String, Object> tmOutMaxMap = new HashMap<String, Object>();
			Map<String, Object> tmOutMinMap = new HashMap<String, Object>();
			Map<String, Object> humInMaxMap = new HashMap<String, Object>();
			Map<String, Object> humInMinMap = new HashMap<String, Object>();
			Map<String, Object> humOutMaxMap = new HashMap<String, Object>();
			Map<String, Object> humOutMinMap = new HashMap<String, Object>();

			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			Map<String, Object> gmCo2Map = new HashMap<String, Object>();
			Map<String, Object> wmCo2Map = new HashMap<String, Object>();
			Map<String, Object> hmCo2Map = new HashMap<String, Object>();

			for (Object obj : emList) {
				Map<String, Object> emtmp = (Map<String, Object>) obj;
				emMap.put((String) emtmp.get("YYYYMM"), emtmp.get("TOTAL"));
			}

			for (Object obj : gmList) {
				Map<String, Object> gmtmp = (Map<String, Object>) obj;
				gmMap.put((String) gmtmp.get("YYYYMM"), gmtmp.get("TOTAL"));
			}

			for (Object obj : wmList) {
				Map<String, Object> wmtmp = (Map<String, Object>) obj;
				wmMap.put((String) wmtmp.get("YYYYMM"), wmtmp.get("TOTAL"));
			}
			
			for (Object obj : hmList) {
				Map<String, Object> hmtmp = (Map<String, Object>) obj;
				hmMap.put((String) hmtmp.get("YYYYMM"), hmtmp.get("TOTAL"));
			}


			for (Object obj : tmInList) {
				Map<String, Object> tmtmp = new HashMap<String, Object>();
				tmtmp = (Map<String, Object>) obj;
				tmInMaxMap.put((String) tmtmp.get("YYYYMM"), tmtmp
						.get("MAXIMUMVALUE"));
				tmInMinMap.put((String) tmtmp.get("YYYYMM"), tmtmp
						.get("MINIMUMVALUE"));
			}

			for (Object obj : tmOutList) {
				Map<String, Object> tmtmp = new HashMap<String, Object>();
				tmtmp = (Map<String, Object>) obj;
				tmOutMaxMap.put((String) tmtmp.get("YYYYMM"), tmtmp
						.get("MAXIMUMVALUE"));
				tmOutMinMap.put((String) tmtmp.get("YYYYMM"), tmtmp
						.get("MINIMUMVALUE"));
			}

			for (Object obj : humInList) {
				Map<String, Object> humtmp = new HashMap<String, Object>();
				humtmp = (Map<String, Object>) obj;
				humInMaxMap.put((String) humtmp.get("YYYYMM"), humtmp
						.get("MAXIMUMVALUE"));
				humInMinMap.put((String) humtmp.get("YYYYMM"), humtmp
						.get("MINIMUMVALUE"));
			}

			for (Object obj : humOutList) {
				Map<String, Object> humtmp = new HashMap<String, Object>();
				humtmp = (Map<String, Object>) obj;
				humOutMaxMap.put((String) humtmp.get("YYYYMM"), humtmp
						.get("MAXIMUMVALUE"));
				humOutMinMap.put((String) humtmp.get("YYYYMM"), humtmp
						.get("MINIMUMVALUE"));
			}

			for (Object obj : emCo2List) {
				Map<String, Object> emco2tmp = (Map<String, Object>) obj;
				emCo2Map.put((String) emco2tmp.get("YYYYMM"), emco2tmp
						.get("TOTAL"));
			}

			for (Object obj : gmCo2List) {
				Map<String, Object> gmco2tmp = (Map<String, Object>) obj;
				gmCo2Map.put((String) gmco2tmp.get("YYYYMM"), gmco2tmp
						.get("TOTAL"));
			}

			for (Object obj : wmCo2List) {
				Map<String, Object> wmco2tmp = (Map<String, Object>) obj;
				wmCo2Map.put((String) wmco2tmp.get("YYYYMM"), wmco2tmp
						.get("TOTAL"));
			}
			
			for (Object obj : hmCo2List) {
				Map<String, Object> hmco2tmp = (Map<String, Object>) obj;
				hmCo2Map.put((String) hmco2tmp.get("YYYYMM"), hmco2tmp
						.get("TOTAL"));
			}
		
			List<Object> monthList = new ArrayList<Object>();
			for (int i = 0; i < 12; i++) {
				String yyyymm = CalendarUtil.getDateWithoutFormat(params
						.get("startDate")
						+ "0101", Calendar.MONTH, i);
				yyyymm = yyyymm.substring(0, 6);
				tmp = new HashMap<String, Object>();
				tmp.put("xField", i);
				tmp.put("EmUsage", new BigDecimal(emMap.get(yyyymm) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(emMap.get(yyyymm))));
				tmp.put("GmUsage", new BigDecimal(gmMap.get(yyyymm) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(gmMap.get(yyyymm))));
				tmp.put("WmUsage", new BigDecimal(wmMap.get(yyyymm) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(wmMap.get(yyyymm))));
				tmp.put("HmUsage", new BigDecimal(hmMap.get(yyyymm) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(hmMap.get(yyyymm))));
				
				Map<String, Object> chargeMap = new HashMap<String, Object>();
				chargeMap.put("serviceType", MeterType.EnergyMeter
						.getServiceType());
				chargeMap.put("dateType", DateType.MONTHLY.getCode());
				chargeMap.put("usage", ((BigDecimal) tmp.get("EmUsage"))
						.doubleValue());
				chargeMap.put("period", 1);
				tmp.put("EmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));
				
				chargeMap.put("serviceType", MeterType.GasMeter
						.getServiceType());
				chargeMap.put("usage", ((BigDecimal) tmp.get("GmUsage"))
						.doubleValue());
				tmp.put("GmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));

				chargeMap.put("serviceType", MeterType.WaterMeter
						.getServiceType());
				chargeMap.put("usage", ((BigDecimal) tmp.get("WmUsage"))
						.doubleValue());
				tmp.put("WmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));
				
				chargeMap.put("serviceType", MeterType.HeatMeter
						.getServiceType());
				chargeMap.put("usage", ((BigDecimal) tmp.get("HmUsage"))
						.doubleValue());
				tmp.put("HmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));

				tmp.put("TmInMaxUsage", new BigDecimal(
						tmInMaxMap.get(yyyymm) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(tmInMaxMap.get(yyyymm))));
				tmp.put("TmInMinUsage", new BigDecimal(
						tmInMinMap.get(yyyymm) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(tmInMinMap.get(yyyymm))));

				tmp.put("TmOutMaxUsage", new BigDecimal(
						tmOutMaxMap.get(yyyymm) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(tmOutMaxMap.get(yyyymm))));
				tmp.put("TmOutMinUsage", new BigDecimal(
						tmOutMinMap.get(yyyymm) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(tmOutMinMap.get(yyyymm))));

				tmp.put("HumInMaxUsage", new BigDecimal(
						humInMaxMap.get(yyyymm) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(humInMaxMap.get(yyyymm))));
				tmp.put("HumInMinUsage", new BigDecimal(
						humInMinMap.get(yyyymm) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(humInMinMap.get(yyyymm))));

				tmp.put("HumOutMaxUsage", new BigDecimal(humOutMaxMap
						.get(yyyymm) == null ? 0 : DecimalUtil.ConvertNumberToDouble(humOutMaxMap
						.get(yyyymm))));
				tmp.put("HumOutMinUsage", new BigDecimal(humOutMinMap
						.get(yyyymm) == null ? 0 : DecimalUtil.ConvertNumberToDouble(humOutMinMap
						.get(yyyymm))));

				tmp.put("Co2Usage",new BigDecimal(emCo2Map.get(yyyymm) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emCo2Map.get(yyyymm))).add(
						new BigDecimal(gmCo2Map.get(yyyymm) == null ? 0
							: DecimalUtil.ConvertNumberToDouble(gmCo2Map.get(yyyymm))).add(
									new BigDecimal(wmCo2Map.get(yyyymm) == null ? 0
										: DecimalUtil.ConvertNumberToDouble(wmCo2Map.get(yyyymm))).add(
												new BigDecimal(hmCo2Map.get(yyyymm) == null ? 0
														: DecimalUtil.ConvertNumberToDouble(hmCo2Map.get(yyyymm)))))));
				tmp.put("date", yyyymm);
				monthList.add(tmp);
			}

			for (int i = 0; i < 4; i++) {
				BigDecimal emUsage = new BigDecimal(0);
				BigDecimal gmUsage = new BigDecimal(0);
				BigDecimal wmUsage = new BigDecimal(0);
				BigDecimal hmUsage = new BigDecimal(0);

				BigDecimal emBillUsage = new BigDecimal(0);
				BigDecimal gmBillUsage = new BigDecimal(0);
				BigDecimal wmBillUsage = new BigDecimal(0);
				BigDecimal hmBillUsage = new BigDecimal(0);

				BigDecimal tmInMaxUsage = new BigDecimal(0);
				BigDecimal tmInMinUsage = new BigDecimal(100);

				BigDecimal tmOutMaxUsage = new BigDecimal(0);
				BigDecimal tmOutMinUsage = new BigDecimal(100);

				BigDecimal humInMaxUsage = new BigDecimal(0);
				BigDecimal humInMinUsage = new BigDecimal(100);

				BigDecimal humOutMaxUsage = new BigDecimal(0);
				BigDecimal humOutMinUsage = new BigDecimal(100);
				BigDecimal co2Usage = new BigDecimal(0);
				String date = "";

				for (int j = i * 3; j < i * 3 + 3; j++) {
					Map<String, Object> monthtmp = (Map<String, Object>) monthList
							.get(j);
					date = (String) monthtmp.get("date");
					emUsage = emUsage.add((BigDecimal) monthtmp.get("EmUsage"));
					gmUsage = gmUsage.add((BigDecimal) monthtmp.get("GmUsage"));
					wmUsage = wmUsage.add((BigDecimal) monthtmp.get("WmUsage"));
					hmUsage = hmUsage.add((BigDecimal) monthtmp.get("HmUsage"));

					emBillUsage = emBillUsage.add((BigDecimal) monthtmp
							.get("EmBillUsage"));
					gmBillUsage = gmBillUsage.add((BigDecimal) monthtmp
							.get("GmBillUsage"));
					wmBillUsage = wmBillUsage.add((BigDecimal) monthtmp
							.get("WmBillUsage"));
					hmBillUsage = hmBillUsage.add((BigDecimal) monthtmp
							.get("HmBillUsage"));

					BigDecimal inmax = (BigDecimal) monthtmp
							.get("TmInMaxUsage");
					BigDecimal inmin = (BigDecimal) monthtmp
							.get("TmInMinUsage");

					if (tmInMaxUsage.compareTo(inmax) < 0) {
						tmInMaxUsage = inmax;
					}

					if (tmInMinUsage.compareTo(inmin) > 0) {
						tmInMinUsage = inmin;
					}

					BigDecimal outmax = (BigDecimal) monthtmp
							.get("TmOutMaxUsage");
					BigDecimal outmin = (BigDecimal) monthtmp
							.get("TmOutMinUsage");

					if (tmOutMaxUsage.compareTo(outmax) < 0) {
						tmOutMaxUsage = outmax;
					}

					if (tmOutMinUsage.compareTo(outmin) > 0) {
						tmOutMinUsage = outmin;
					}

					BigDecimal hInmax = (BigDecimal) monthtmp
							.get("HumInMaxUsage");
					BigDecimal hInmin = (BigDecimal) monthtmp
							.get("HumInMinUsage");
					if (humInMaxUsage.compareTo(hInmax) < 0) {
						humInMaxUsage = hInmax;
					}

					if (humInMinUsage.compareTo(hInmin) > 0) {
						if (humInMinUsage.intValue() == 100) {
							humInMinUsage = hInmin;
						} else {
							if (hInmin.intValue() > 0) {
								humInMinUsage = hInmin;
							}
						}

					}

					BigDecimal hOutmax = (BigDecimal) monthtmp
							.get("HumOutMaxUsage");
					BigDecimal hOutmin = (BigDecimal) monthtmp
							.get("HumOutMinUsage");

					if (humOutMaxUsage.compareTo(hOutmax) < 0) {
						humOutMaxUsage = hOutmax;
					}

					if (humOutMinUsage.compareTo(hOutmin) > 0) {

						if (humOutMinUsage.intValue() == 100) {
							humOutMinUsage = hOutmin;
						} else {

							if (hOutmin.intValue() > 0) {
								humOutMinUsage = hOutmin;
							}
						}
					}

					co2Usage = co2Usage.add((BigDecimal) monthtmp
							.get("Co2Usage"));
				}

				Map<String, Object> quatermap = new HashMap<String, Object>();

				// System.out.println("emBillUsage:" + emBillUsage);
				quatermap.put("yyyy", date.substring(0, 4));
				quatermap.put("xField", setYearQuaterLable(date) + "Q");
				quatermap.put("EmUsage", emUsage);
				quatermap.put("EmKGOE", new BigDecimal(emUsage == null? 0: DecimalUtil.ConvertNumberToDouble(emUsage)*
						KGOE.Energy.getValue()));
				
				quatermap.put("GmUsage", gmUsage);				
				quatermap.put("GmKGOE", new BigDecimal(gmUsage == null? 0: DecimalUtil.ConvertNumberToDouble(gmUsage)*
						KGOE.GasLng.getValue()));
				
				quatermap.put("WmUsage", wmUsage);
				quatermap.put("WmKGOE", new BigDecimal(wmUsage == null? 0: DecimalUtil.ConvertNumberToDouble(wmUsage)*
						KGOE.Water.getValue()));
				
				quatermap.put("HmUsage", hmUsage);
				quatermap.put("HmKGOE", new BigDecimal(hmUsage == null? 0: DecimalUtil.ConvertNumberToDouble(hmUsage)*
						KGOE.Heat.getValue()));
				
				quatermap.put("EmBillUsage", emBillUsage);
				quatermap.put("GmBillUsage", gmBillUsage);
				quatermap.put("WmBillUsage", wmBillUsage);
				quatermap.put("HmBillUsage", hmBillUsage);
				quatermap.put("TmInMaxUsage", tmInMaxUsage);
				quatermap.put("TmInMinUsage", tmInMinUsage);
				quatermap.put("TmGridInUsage", tmInMaxUsage + "/" + tmInMinUsage);
				quatermap.put("TmOutMaxUsage", tmOutMaxUsage);
				quatermap.put("TmOutMinUsage", tmOutMinUsage);
				quatermap
						.put("TmGridOutUsage", tmOutMaxUsage + "/" + tmOutMinUsage);
				quatermap.put("HumInMaxUsage", humInMaxUsage);
				quatermap.put("HumInMinUsage", humInMinUsage);
				quatermap
						.put("HumGridInUsage", humInMaxUsage + "/" + humInMinUsage);
				quatermap.put("HumOutMaxUsage", humOutMaxUsage);
				quatermap.put("HumOutMinUsage", humOutMinUsage);
				quatermap.put("HumGridOutUsage", humOutMaxUsage + "/"
						+ humOutMinUsage);
//				quatermap.put("WmUsage", wmUsage);

				quatermap.put("Co2Usage", co2Usage);
				//System.out.println("quatermap:" + quatermap);
				resultList.add(quatermap);
			}

		} else if (CommonConstants.DateType.YEARLY.getCode().equals(periodType)) {

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthTableName());
			List<Object> emList = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthTableName());
			List<Object> gmList = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getMonthTableName());
			List<Object> wmList = meteringDayDao
					.getUsageForLocationByMonth(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getMonthTableName());
			List<Object> hmList = meteringDayDao
					.getUsageForLocationByMonth(params);


			params.put("meterType", temperatureMonthMeterType);
			params.put("meterId", params.get("inMeterId"));
			List<Object> tmInList = meteringDayDao
					.getTemperatureHumidityForLocationByMonth(params);
			params.put("meterId", params.get("outMeterId"));
			List<Object> tmOutList = meteringDayDao
					.getTemperatureHumidityForLocationByMonth(params);
			params.put("meterType", humidityMonthMeterType);
			params.put("meterId", params.get("inMeterId"));
			List<Object> humInList = meteringDayDao
					.getTemperatureHumidityForLocationByMonth(params);

			params.put("meterId", params.get("outMeterId"));
			List<Object> humOutList = meteringDayDao
					.getTemperatureHumidityForLocationByMonth(params);
			params.put("channel", DefaultChannel.Co2.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthTableName());
			List<Object> emCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getMonthTableName());
			List<Object> gmCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);

			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getMonthTableName());
			List<Object> wmCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);
			
			params.put("meterType", CommonConstants.MeterType.HeatMeter
					.getMonthTableName());
			List<Object> hmCo2List = meteringDayDao
					.getUsageForLocationByMonth(params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> gmMap = new HashMap<String, Object>();
			Map<String, Object> wmMap = new HashMap<String, Object>();
			Map<String, Object> hmMap = new HashMap<String, Object>();

			Map<String, Object> tmInMaxMap = new HashMap<String, Object>();
			Map<String, Object> tmInMinMap = new HashMap<String, Object>();

			Map<String, Object> tmOutMaxMap = new HashMap<String, Object>();
			Map<String, Object> tmOutMinMap = new HashMap<String, Object>();

			Map<String, Object> humInMaxMap = new HashMap<String, Object>();
			Map<String, Object> humInMinMap = new HashMap<String, Object>();

			Map<String, Object> humOutMaxMap = new HashMap<String, Object>();
			Map<String, Object> humOutMinMap = new HashMap<String, Object>();

			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			Map<String, Object> gmCo2Map = new HashMap<String, Object>();
			Map<String, Object> wmCo2Map = new HashMap<String, Object>();
			Map<String, Object> hmCo2Map = new HashMap<String, Object>();
			
			String emyyyy  = "1111";
			Double emtotal = 0.0;
			for (Object obj : emList) {
				Map<String, Object> emtmp = (Map<String, Object>) obj;
				
				String yyyy = ((String)emtmp.get("YYYYMM")).substring(0, 4);
				Double total = DecimalUtil.ConvertNumberToDouble(emtmp.get("TOTAL"));
				if(!emyyyy.equals(yyyy)){
					emyyyy = yyyy;
					emtotal = 0.0;
				}
				emtotal = emtotal+total;
				emMap.put(emyyyy, emtotal);
			}
			
			String gmyyyy  = "1111";
			Double gmtotal = 0.0;
			for (Object obj : gmList) {
				Map<String,Object> gmtmp = (Map<String,Object>)obj;
				String yyyy = ((String)gmtmp.get("YYYYMM")).substring(0, 4);
				Double total = DecimalUtil.ConvertNumberToDouble(gmtmp.get("TOTAL"));
				if(!gmyyyy.equals(yyyy)){
					gmyyyy = yyyy;
					gmtotal = 0.0;
				}
				gmtotal = gmtotal+total;
				gmMap.put(gmyyyy, gmtotal);
			}

			String wmyyyy  = "1111";
			Double wmtotal = 0.0;
			for (Object obj : wmList) {
				Map<String,Object> wmtmp = (Map<String,Object>)obj;
				String yyyy = ((String)wmtmp.get("YYYYMM")).substring(0, 4);
				Double total = DecimalUtil.ConvertNumberToDouble(wmtmp.get("TOTAL"));
				if(!wmyyyy.equals(yyyy)){
					wmyyyy = yyyy;
					wmtotal = 0.0;
				}
				wmtotal = wmtotal+total;
				wmMap.put(wmyyyy, wmtotal);
			}

			String hmyyyy  = "1111";
			Double hmtotal = 0.0;
			for (Object obj : hmList) {
				Map<String,Object> hmtmp = (Map<String,Object>)obj;
				String yyyy = ((String)hmtmp.get("YYYYMM")).substring(0, 4);
				Double total = DecimalUtil.ConvertNumberToDouble(hmtmp.get("TOTAL"));
				if(!hmyyyy.equals(yyyy)){
					hmyyyy = yyyy;
					hmtotal = 0.0;
				}
				hmtotal = hmtotal+total;
				hmMap.put(hmyyyy, hmtotal);
			}
			
			String tmInyyyy  = "1111";
			Double tmInMax = 0.0;
			Double tmInMin = 0.0;
			for (Object obj : tmInList) {
				Map<String, Object> tmtmp = new HashMap<String, Object>();
				tmtmp = (Map<String, Object>) obj;
				String yyyy = ((String)tmtmp.get("YYYYMM")).substring(0, 4);
				Double maxvaluecheck = DecimalUtil.ConvertNumberToDouble(tmtmp.get("MAXIMUMVALUE"));
				Double minvaluecheck = DecimalUtil.ConvertNumberToDouble(tmtmp.get("MINIMUMVALUE"));
				
				if(!tmInyyyy.equals(yyyy)){
					tmInyyyy = yyyy;
					tmInMax = maxvaluecheck;
					tmInMin = minvaluecheck;
				}
				
				if(tmInMin > minvaluecheck){
					tmInMin = minvaluecheck;
				}else if(tmInMax < maxvaluecheck){
					tmInMax = maxvaluecheck;
				}

				tmInMaxMap.put(tmInyyyy, tmInMax);
				tmInMinMap.put(tmInyyyy, tmInMin);
			}

			String tmOutyyyy  = "1111";
			Double tmOutMax = 0.0;
			Double tmOutMin = 0.0;
			for (Object obj : tmOutList) {
				Map<String, Object> tmtmp = new HashMap<String, Object>();
				tmtmp = (Map<String, Object>) obj;
				
				String yyyy = ((String)tmtmp.get("YYYYMM")).substring(0, 4);
				Double maxvaluecheck = DecimalUtil.ConvertNumberToDouble(tmtmp.get("MAXIMUMVALUE"));
				Double minvaluecheck = DecimalUtil.ConvertNumberToDouble(tmtmp.get("MINIMUMVALUE"));
				
				if(!tmOutyyyy.equals(yyyy)){
					tmOutyyyy = yyyy;
					tmOutMax = maxvaluecheck;
					tmOutMin = minvaluecheck;
				}
				
				if(tmOutMin > minvaluecheck){
					tmOutMin = minvaluecheck;
				}else if(tmOutMax < maxvaluecheck){
					tmOutMax = maxvaluecheck;
				}
				
				tmOutMaxMap.put(tmOutyyyy, tmOutMax);
				tmOutMinMap.put(tmOutyyyy, tmOutMin);
			}

			String humInyyyy  = "1111";
			Double humInMax = 0.0;
			Double humInMin = 0.0;
			for (Object obj : humInList) {
				Map<String, Object> humtmp = new HashMap<String, Object>();
				humtmp = (Map<String, Object>) obj;
				
				String yyyy = ((String)humtmp.get("YYYYMM")).substring(0, 4);
				Double maxvaluecheck = DecimalUtil.ConvertNumberToDouble(humtmp.get("MAXIMUMVALUE"));
				Double minvaluecheck = DecimalUtil.ConvertNumberToDouble(humtmp.get("MINIMUMVALUE"));
				if(!humInyyyy.equals(yyyy)){
					humInyyyy = yyyy;
					humInMax = maxvaluecheck;
					humInMin = minvaluecheck;
				}
				
				if(humInMin > minvaluecheck){
					humInMin = minvaluecheck;
				}else if(humInMax < maxvaluecheck){
					humInMax = maxvaluecheck;
				}
				
				humInMaxMap.put(humInyyyy, humInMax);
				humInMinMap.put(humInyyyy, humInMin);
			}

			String humOutyyyy  = "1111";
			Double humOutMax = 0.0;
			Double humOutMin = 0.0;
			for (Object obj : humOutList) {
				Map<String, Object> humtmp = new HashMap<String, Object>();
				humtmp = (Map<String, Object>) obj;

				String yyyy = ((String)humtmp.get("YYYYMM")).substring(0, 4);
				Double maxvaluecheck = DecimalUtil.ConvertNumberToDouble(humtmp.get("MAXIMUMVALUE"));
				Double minvaluecheck = DecimalUtil.ConvertNumberToDouble(humtmp.get("MINIMUMVALUE"));
				if(!humOutyyyy.equals(yyyy)){
					humOutyyyy = yyyy;
					humOutMax = maxvaluecheck;
					humOutMin = minvaluecheck;
				}
				
				if(humOutMin > minvaluecheck){
					humOutMin = minvaluecheck;
				}else if(humOutMax < maxvaluecheck){
					humOutMax = maxvaluecheck;
				}
				
				humOutMaxMap.put(humOutyyyy, humOutMax);
				humOutMinMap.put(humOutyyyy, humOutMin);
			}

			String emco2yyyy  = "1111";
			Double emco2total = 0.0;
			for (Object obj : emCo2List) {
				Map<String, Object> emco2tmp = (Map<String, Object>) obj;
				String yyyy = ((String)emco2tmp.get("YYYYMM")).substring(0, 4);
				Double total = DecimalUtil.ConvertNumberToDouble(emco2tmp.get("TOTAL"));
				if(!emco2yyyy.equals(yyyy)){
					emco2yyyy  = yyyy;
					emco2total = 0.0;
				}
				emco2total = emco2total+total;
				emCo2Map.put(emco2yyyy, emco2total);
			}

			String gmco2yyyy  = "1111";
			Double gmco2total = 0.0;
			for (Object obj : gmCo2List) {
				Map<String, Object> gmco2tmp = (Map<String, Object>) obj;
				String yyyy = ((String)gmco2tmp.get("YYYYMM")).substring(0, 4);
				Double total = DecimalUtil.ConvertNumberToDouble(gmco2tmp.get("TOTAL"));
				if(!gmco2yyyy.equals(yyyy)){
					gmco2yyyy  = yyyy;
					gmco2total = 0.0;
				}
				gmco2total = gmco2total+total;
				gmCo2Map.put(gmco2yyyy, gmco2total);
			}

			String wmco2yyyy  = "1111";
			Double wmco2total = 0.0;
			for (Object obj : wmCo2List) {
				Map<String, Object> wmco2tmp = (Map<String, Object>) obj;
				String yyyy = ((String)wmco2tmp.get("YYYYMM")).substring(0, 4);
				Double total = DecimalUtil.ConvertNumberToDouble(wmco2tmp.get("TOTAL"));
				if(!wmco2yyyy.equals(yyyy)){
					wmco2yyyy  = yyyy;
					wmco2total = 0.0;
				}
				wmco2total = wmco2total+total;
				wmCo2Map.put(wmco2yyyy, wmco2total);
			}
			
			String hmco2yyyy  = "1111";
			Double hmco2total = 0.0;
			for (Object obj : hmCo2List) {
				Map<String, Object> hmco2tmp = (Map<String, Object>) obj;
				String yyyy = ((String)hmco2tmp.get("YYYYMM")).substring(0, 4);
				Double total = DecimalUtil.ConvertNumberToDouble(hmco2tmp.get("TOTAL"));
				if(!hmco2yyyy.equals(yyyy)){
					hmco2yyyy  = yyyy;
					hmco2total = 0.0;
				}
				hmco2total = hmco2total+total;
				hmCo2Map.put(hmco2yyyy, hmco2total);
			}
			
			String startyyyy = (String)params.get("startDate");
			
			int y = Integer.parseInt(startyyyy.substring(0, 4));
    		int m = Integer.parseInt(startyyyy.substring(4, 6));

    		Calendar getstartYear = Calendar.getInstance();
    		getstartYear.set(y, m-1, 1);
    		startyyyy = formatter.format(getstartYear.getTime());
			List<Object> yearList = new ArrayList<Object>();
			
			for (int i = 1; i < 5; i++) {

				String year = startyyyy.substring(0, 4);
				
				tmp = new HashMap<String, Object>();
				tmp.put("xField", year);
				tmp.put("EmUsage", new BigDecimal(emMap.get(year) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(emMap.get(year))));
				tmp.put("GmUsage", new BigDecimal(gmMap.get(year) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(gmMap.get(year))));
				tmp.put("WmUsage", new BigDecimal(wmMap.get(year) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(wmMap.get(year))));
				tmp.put("HmUsage", new BigDecimal(hmMap.get(year) == null ? 0
						: DecimalUtil.ConvertNumberToDouble(hmMap.get(year))));
				
				Map<String, Object> chargeMap = new HashMap<String, Object>();
				
				chargeMap.put("serviceType", MeterType.EnergyMeter
						.getServiceType());
				chargeMap.put("dateType", DateType.YEARLY.getCode());
				chargeMap.put("usage", ((BigDecimal) tmp.get("EmUsage"))
						.doubleValue());
				chargeMap.put("period", 1);
				tmp.put("EmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));
				
				chargeMap.put("serviceType", MeterType.GasMeter
						.getServiceType());
				chargeMap.put("usage", ((BigDecimal) tmp.get("GmUsage"))
						.doubleValue());		
				tmp.put("GmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));
				
				chargeMap.put("serviceType", MeterType.WaterMeter
						.getServiceType());
				chargeMap.put("usage", ((BigDecimal) tmp.get("WmUsage"))
						.doubleValue());
				tmp.put("WmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));
				
				chargeMap.put("serviceType", MeterType.HeatMeter
						.getServiceType());
				chargeMap.put("usage", ((BigDecimal) tmp.get("HmUsage"))
						.doubleValue());
				tmp.put("HmBillUsage", new BigDecimal((Double) bemsUtil
						.getUsageCharge(chargeMap)));

				tmp.put("TmInMaxUsage", new BigDecimal(
						tmInMaxMap.get(year) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(tmInMaxMap.get(year))));
				tmp.put("TmInMinUsage", new BigDecimal(
						tmInMinMap.get(year) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(tmInMinMap.get(year))));

				tmp.put("TmOutMaxUsage", new BigDecimal(
						tmOutMaxMap.get(year) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(tmOutMaxMap.get(year))));
				tmp.put("TmOutMinUsage", new BigDecimal(
						tmOutMinMap.get(year) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(tmOutMinMap.get(year))));

				tmp.put("HumInMaxUsage", new BigDecimal(
						humInMaxMap.get(year) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(humInMaxMap.get(year))));
				tmp.put("HumInMinUsage", new BigDecimal(
						humInMinMap.get(year) == null ? 0
								: DecimalUtil.ConvertNumberToDouble(humInMinMap.get(year))));
				
				tmp.put("HumOutMaxUsage", new BigDecimal(humOutMaxMap
						.get(year) == null ? 0 : DecimalUtil.ConvertNumberToDouble(humOutMaxMap
						.get(year))));
				tmp.put("HumOutMinUsage", new BigDecimal(humOutMinMap
						.get(year) == null ? 0 : DecimalUtil.ConvertNumberToDouble(humOutMinMap
						.get(year))));
				
				tmp.put("Co2Usage",new BigDecimal(emCo2Map.get(year) == null ? 0
					: DecimalUtil.ConvertNumberToDouble(emCo2Map.get(year))).add(
						new BigDecimal(gmCo2Map.get(year) == null ? 0
							: DecimalUtil.ConvertNumberToDouble(gmCo2Map.get(year))).add(
									new BigDecimal(wmCo2Map.get(year) == null ? 0
										: DecimalUtil.ConvertNumberToDouble(wmCo2Map.get(year))).add(
												new BigDecimal(hmCo2Map.get(year) == null ? 0
														: DecimalUtil.ConvertNumberToDouble(hmCo2Map.get(year)))))));

				tmp.put("date", year);			
				yearList.add(tmp);
				getstartYear.set(getstartYear.get(Calendar.YEAR)+1, 0, 1);
				startyyyy = formatter.format(getstartYear.getTime());
			}

			

			String date = "";

			for (int j = 0; j < yearList.size(); j++) {
				
				BigDecimal emUsage = new BigDecimal(0);
				BigDecimal gmUsage = new BigDecimal(0);
				BigDecimal wmUsage = new BigDecimal(0);
				BigDecimal hmUsage = new BigDecimal(0);

				BigDecimal emBillUsage = new BigDecimal(0);
				BigDecimal gmBillUsage = new BigDecimal(0);
				BigDecimal wmBillUsage = new BigDecimal(0);
				BigDecimal hmBillUsage = new BigDecimal(0);

				BigDecimal tmInMaxUsage = new BigDecimal(0);
				BigDecimal tmInMinUsage = new BigDecimal(0);

				BigDecimal tmOutMaxUsage = new BigDecimal(0);
				BigDecimal tmOutMinUsage = new BigDecimal(0);

				BigDecimal humInMaxUsage = new BigDecimal(0);
				BigDecimal humInMinUsage = new BigDecimal(0);

				BigDecimal humOutMaxUsage = new BigDecimal(0);
				BigDecimal humOutMinUsage = new BigDecimal(0);
				BigDecimal co2Usage = new BigDecimal(0);
				
				Map<String, Object> yeartmp = (Map<String, Object>) yearList
						.get(j);
				date = (String) yeartmp.get("date");
				emUsage = (BigDecimal) yeartmp.get("EmUsage");
				gmUsage = (BigDecimal) yeartmp.get("GmUsage");
				wmUsage = (BigDecimal) yeartmp.get("WmUsage");
				hmUsage = (BigDecimal) yeartmp.get("HmUsage");
				emBillUsage = (BigDecimal) yeartmp.get("EmBillUsage");
				gmBillUsage = (BigDecimal) yeartmp.get("GmBillUsage");
				wmBillUsage = (BigDecimal) yeartmp.get("WmBillUsage");
				hmBillUsage = (BigDecimal) yeartmp.get("HmBillUsage");

				tmInMaxUsage = (BigDecimal) yeartmp
						.get("TmInMaxUsage");
				tmInMinUsage = (BigDecimal) yeartmp
						.get("TmInMinUsage");

				tmOutMaxUsage = (BigDecimal) yeartmp
						.get("TmOutMaxUsage");
				tmOutMinUsage = (BigDecimal) yeartmp
						.get("TmOutMinUsage");


				humInMaxUsage = (BigDecimal) yeartmp
						.get("HumInMaxUsage");
				humInMinUsage = (BigDecimal) yeartmp
						.get("HumInMinUsage");


				humOutMaxUsage = (BigDecimal) yeartmp
						.get("HumOutMaxUsage");
				humOutMinUsage = (BigDecimal) yeartmp
						.get("HumOutMinUsage");


				co2Usage = (BigDecimal) yeartmp
						.get("Co2Usage");
			

			Map<String, Object> yearmap = new HashMap<String, Object>();
			
			yearmap.put("xField", date.substring(0, 4) + "년");

			yearmap.put("EmUsage", emUsage);
			
			yearmap.put("EmKGOE", new BigDecimal(DecimalUtil.ConvertNumberToDouble(emUsage)* KGOE.Energy.getValue()));
				
			yearmap.put("GmUsage", gmUsage);
			yearmap.put("GmKGOE", new BigDecimal(DecimalUtil.ConvertNumberToDouble(gmUsage)* KGOE.GasLng.getValue()));
			
			yearmap.put("WmUsage", wmUsage);
			yearmap.put("WmKGOE", new BigDecimal(DecimalUtil.ConvertNumberToDouble(wmUsage)* KGOE.Water.getValue()));
			
			yearmap.put("HmUsage", hmUsage);
			yearmap.put("HmKGOE", new BigDecimal(DecimalUtil.ConvertNumberToDouble(hmUsage)* KGOE.Heat.getValue()));
			
			yearmap.put("EmBillUsage", emBillUsage);
			yearmap.put("GmBillUsage", gmBillUsage);
			yearmap.put("WmBillUsage", wmBillUsage);
			yearmap.put("HmBillUsage", hmBillUsage);
				
			yearmap.put("TmInMaxUsage", tmInMaxUsage);
			yearmap.put("TmInMinUsage", tmInMinUsage);
			yearmap.put("TmGridInUsage", tmInMaxUsage + "/" + tmInMinUsage);
			yearmap.put("TmOutMaxUsage", tmOutMaxUsage);
			yearmap.put("TmOutMinUsage", tmOutMinUsage);
			yearmap
					.put("TmGridOutUsage", tmOutMaxUsage + "/" + tmOutMinUsage);
			yearmap.put("HumInMaxUsage", humInMaxUsage);
			yearmap.put("HumInMinUsage", humInMinUsage);
			yearmap
					.put("HumGridInUsage", humInMaxUsage + "/" + humInMinUsage);
			yearmap.put("HumOutMaxUsage", humOutMaxUsage);
			yearmap.put("HumOutMinUsage", humOutMinUsage);
			yearmap.put("HumGridOutUsage", humOutMaxUsage + "/"
					+ humOutMinUsage);
			yearmap.put("Co2Usage", co2Usage);

			resultList.add(yearmap);
			}
		}
		return resultList;
	}

	/*
	 * 분기,10년 검색조건을 챠트 라벨로 변환 ,검색 조건 및 챠트 라벨에 사용
	 */
	public String setYearQuaterLable(String date) {
		String yyyymm = "";
		String year = date.substring(0, 4);
		String month = date.substring(4, 6);
		String setMonth = "";
		if ("03".equals(month)) {
			setMonth = "/1";
		} else if ("06".equals(month)) {
			setMonth = "/2";
		} else if ("09".equals(month)) {
			setMonth = "/3";
		} else if ("12".equals(month)) {
			setMonth = "/4";
		}
		yyyymm = year + setMonth;

		return yyyymm;
	}

	/*
	 * 지정일의 2: 일주일 전일자,3: 지정월의 1년 전월 시작 일자,4: 분기 별 시작일자 끝일자,5:10년 전의 시작월,끝월 검색
	 * 조건 및 챠트 라벨에 사용
	 */
	public Map<String, String> calerdarTerm(String searchDateType, String yyyymm) {
		Map<String, String> term = new HashMap<String, String>();
		int y = Integer.parseInt(yyyymm.substring(0, 4));
		int m = Integer.parseInt(yyyymm.substring(4, 6));
		if (CommonConstants.DateType.HOURLY.getCode().equals(searchDateType)) {
			String startDay = CalendarUtil.getDateWithoutFormat(yyyymm,
					Calendar.DATE, -1);

			term.put("startDate", startDay);
			term.put("endDate", yyyymm);

		} else if (CommonConstants.DateType.DAILY.getCode().equals(
				searchDateType)) {
			String startDay = CalendarUtil.getDateWithoutFormat(yyyymm,
					Calendar.DATE, -6);

			term.put("startDate", startDay);
			term.put("endDate", yyyymm);
		} else if (CommonConstants.DateType.MONTHLY.getCode().equals(
				searchDateType)) {
			String startMonth = CalendarUtil.getDateWithoutFormat(
					yyyymm + "01", Calendar.MONTH, -11);
			/*String startMonth = CalendarUtil.getDateWithoutFormat(
					yyyymm + "01", Calendar.MONTH, -11);*/
			// String endMonth = CalendarUtil.getDateWithoutFormat(yyyymm +
			// "01",
			// Calendar.MONTH, -1);

			term.put("startDate", startMonth.substring(0, 6));
			term.put("endDate", yyyymm.substring(0, 6));
		} else if (CommonConstants.DateType.QUARTERLY.getCode().equals(
				searchDateType)) {

			if (m >= 1 && m <= 3) {
				term.put("startDate", (y - 1) + "04");
				term.put("endDate", y + "03");

			} else if (m >= 4 && m <= 6) {
				term.put("startDate", (y - 1) + "07");
				term.put("endDate", y + "06");

			} else if (m >= 7 && m <= 9) {
				term.put("startDate", (y - 1) + "10");
				term.put("endDate", y + "09");

			} else if (m >= 10 && m <= 12) {
				term.put("startDate", y + "01");
				term.put("endDate", y + "12");

			}

		} else if (CommonConstants.DateType.YEARLY.getCode().equals(
				searchDateType)) {
			String startDate = CalendarUtil.getDateWithoutFormat(yyyymm,
					Calendar.YEAR, -3);
			 String endDate = CalendarUtil.getDateWithoutFormat(yyyymm,
			 Calendar.YEAR, 0);
			term.put("startDate", startDate.substring(0, 4)+"0101");
			term.put("endDate", yyyymm.substring(0, 4)+"1231");

		}
		return term;
	}

	public Map<String, String> compareCalendarTerm(String searchDateType,
			String date, boolean compare) {
		Map<String, String> term = new HashMap<String, String>();
		int y = Integer.parseInt(date.substring(0, 4));
		int m = Integer.parseInt(date.substring(4, 6));
		if (CommonConstants.DateType.HOURLY.getCode().equals(searchDateType)) {

			String endDay = "";
			if (compare) {
				endDay = CalendarUtil.getDateWithoutFormat(date, Calendar.YEAR,
						-1);
			} else {
				endDay = CalendarUtil.getDateWithoutFormat(date, Calendar.DATE,
						-1);
			}
			term.put("startDate", endDay);
			term.put("endDate", endDay);

		} else if (CommonConstants.DateType.DAILY.getCode().equals(
				searchDateType)) {

			String startDay = "";
			String endDay = "";
			if (compare) {

				int week = CalendarUtil.getWeekOfMonth(date);
				int d = Integer.parseInt(date.substring(6, 8));

				endDay = CalendarUtil.getDateWeekDayOfWeek(y - 1 + "", date
						.substring(4, 6), week + "", getWeekDay(y, m, d) + "");
				startDay = CalendarUtil.getDateWithoutFormat(endDay,
						Calendar.DATE, -7);

			} else {
				startDay = CalendarUtil.getDateWithoutFormat(date,
						Calendar.DATE, -13);
				endDay = CalendarUtil.getDateWithoutFormat(date, Calendar.DATE,
						-7);

			}
			term.put("startDate", startDay);
			term.put("endDate", endDay);
		} else if (CommonConstants.DateType.MONTHLY.getCode().equals(
				searchDateType)) {

			String startMonth = CalendarUtil.getDateWithoutFormat(date,
					Calendar.MONTH, -23);
			String endMonth = CalendarUtil.getDateWithoutFormat(date,
					Calendar.MONTH, -12);

			term.put("startDate", startMonth.substring(0, 6));
			term.put("endDate", endMonth.substring(0, 6));

		} else if (CommonConstants.DateType.QUARTERLY.getCode().equals(
				searchDateType)) {

			if (m >= 1 && m <= 3) {

				term.put("startDate", (y - 2) + "04");
				term.put("endDate", (y - 1) + "03");

			} else if (m >= 4 && m <= 6) {

				term.put("startDate", (y - 2) + "07");
				term.put("endDate", (y - 1) + "06");

			} else if (m >= 7 && m <= 9) {

				term.put("startDate", (y - 2) + "10");
				term.put("endDate", (y - 1) + "09");

			} else if (m >= 10 && m <= 12) {

				term.put("startDate", (y - 1) + "1");
				term.put("endDate", (y - 1) + "12");

			}

		} else if (CommonConstants.DateType.YEARLY.getCode().equals(
				searchDateType)) {
			String startDate = CalendarUtil.getDateWithoutFormat(date,
					Calendar.YEAR, -3);
			 String endDate = CalendarUtil.getDateWithoutFormat(date,
			 Calendar.YEAR, 0);
			term.put("startDate", startDate.substring(0, 4)+"0101");
			term.put("endDate", endDate.substring(0, 4)+"1231");

		}
		return term;
	}

	/**
	 * 날짜로 요일가져오기
	 * 
	 * @param y
	 * @param m
	 * @param d
	 * @return
	 */
	public static int getWeekDay(int y, int m, int d) {

		Calendar cal = new GregorianCalendar();
		cal.set(Calendar.YEAR, y);
		cal.set(Calendar.MONTH, m - 1);
		cal.set(Calendar.DAY_OF_MONTH, d);

		return cal.get(Calendar.DAY_OF_WEEK);
	}

	private List<Meter> getMeterLocation(int locationId, int inner) {
		int modelId = 0;
		List<DeviceModel> deviceModel = null;
		if (inner == CommonConstants.OutdoorIndoorType.Indoor
				.getOutdoorIndoorType()) {
			deviceModel = getDeviceModel("HeatMeter", "NURITelecom");
		} else {
			deviceModel = getDeviceModel("WaterMeter", "NURITelecom");
		}

		if (deviceModel != null && deviceModel.size() > 0) {
			modelId = deviceModel.get(0).getId();
		} else {
			return new ArrayList<Meter>();
		}
		Set<Condition> endDeviceCondition = new HashSet<Condition>(0);
		endDeviceCondition.add(new Condition("location.id",
				new Object[] { locationId }, null, Restriction.EQ));
		endDeviceCondition.add(new Condition("model.id",
				new Object[] { modelId }, null, Restriction.EQ));

		return meterDao.findByConditions(endDeviceCondition);
	}

	private List<DeviceModel> getDeviceModel(String codeName, String vendorName) {
		Code code = getCode(codeName);
		DeviceVendor deviceVendor = getDeviceVendor(vendorName);
		if (deviceVendor != null)
			return deviceModelDao.getDeviceModels(deviceVendor.getId(), code
					.getId());
		else
			return null;

	}

	private DeviceVendor getDeviceVendor(String name) {
		Set<Condition> condition = new HashSet<Condition>(0);
		condition.add(new Condition("name", new Object[] { name }, null,
				Restriction.EQ));
		return deviceVendorDao.findByCondition("name", "NURITelecom");
	}

	private Code getCode(String name) {
		return codeDao.getCodeByName(name);
	}

	private Map<String, Object> locationIdWithEndDevice(int locationId,
			int inner, boolean searchParent) {

		Map<String, Object> idMap = new HashMap<String, Object>();
		idMap.put("locationId", -1);
		idMap.put("meterId", -1);
		Location location = locationDao.get(locationId);

		List<Meter> innerMeter = null;
		try {
			innerMeter = getMeterLocation(locationId, inner);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (innerMeter.size() > 0) {

			idMap.put("locationId", locationId);
			idMap.put("meterId", innerMeter.get(0).getId());

			return idMap;
		} else {
			if (!searchParent) {
				return idMap;
			}
			if (location != null) {
				if (location.getParent() != null) {
					locationIdWithEndDevice(location.getParent().getId(),
							inner, searchParent);
				} else {
					return idMap;
				}
			} else {
				return idMap;
			}
		}
		return idMap;
	}

	public Map<String, Object> getMeterId(int location) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		try {
			Map<String, Object> inMap = locationIdWithEndDevice(location,
					CommonConstants.OutdoorIndoorType.Indoor
							.getOutdoorIndoorType(), true);
			Map<String, Object> outMap = new HashMap<String, Object>();
			retMap.put("tLocationId", -1);
			retMap.put("inMeterId", -1);
			retMap.put("outMeterId", -1);
			int locationId = (Integer) inMap.get("locationId");
			int outLocationId = -1;
			if (locationId != -1) {
				outMap = locationIdWithEndDevice(locationId,
						CommonConstants.OutdoorIndoorType.Outdoor
								.getOutdoorIndoorType(), false);
				outLocationId = (Integer) outMap.get("locationId");

				retMap.put("tLocationId", outLocationId);
				retMap.put("inMeterId", inMap.get("meterId"));
				retMap.put("outMeterId", outMap.get("meterId"));

			} else {
				outMap = locationIdWithEndDevice(location,
						CommonConstants.OutdoorIndoorType.Outdoor
								.getOutdoorIndoorType(), true);
				outLocationId = (Integer) outMap.get("locationId");
				if (outLocationId != -1) {
					retMap.put("tLocationId", outLocationId);
					retMap.put("inMeterId", -1);
					retMap.put("outMeterId", outMap.get("meterId"));

				} else {
					retMap.put("tLocationId", -1);
					retMap.put("inMeterId", -1);
					retMap.put("outMeterId", -1);

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return retMap;
	}

	@Override
	public Map<String, Object> getSearchChartDataByParam(String periodType,
			Integer supplierId, String date, Integer locationId) {
		Map<String, Object> condition = new HashMap<String, Object>();
	    condition .put("periodType", periodType);
	    condition .put("supplierId", supplierId);
	    condition .put("date", date);
	    condition .put("locationId", locationId);
	    return getSearchChartData(condition);
	}

	@Override
	public Map<String, Object> getSearchCompareChartDataByParam(
			String periodType, Integer supplierId, String date,
			Boolean compare, Integer locationId) {
		Map<String, Object> condition = new HashMap<String, Object>();
	    condition .put("periodType", periodType);
	    condition .put("supplierId", supplierId);
	    condition .put("date", date);
	    condition .put("compare", compare);
	    condition .put("locationId", locationId);
	    return getSearchCompareChartData(condition);
	}



}
