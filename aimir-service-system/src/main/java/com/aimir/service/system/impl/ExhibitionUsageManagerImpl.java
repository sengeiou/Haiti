package com.aimir.service.system.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.MeteringDataDao;
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
import com.aimir.service.system.ExhibitionUsageManager;
import com.aimir.util.BemsStatisticUtil;
import com.aimir.util.CalendarUtil;

@WebService(endpointInterface = "com.aimir.service.system.ExhibitionUsageManager")
@Service(value = "exhibitionUsageManager")
@Transactional
public class ExhibitionUsageManagerImpl implements
		ExhibitionUsageManager {
	Log logger = LogFactory.getLog(ExhibitionUsageManagerImpl.class);
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
    MeteringDataDao meteringDataDao;

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

	public static void main(String args[]) {
		System.out.println(getPercent(170, 100));
	}
	public static BigDecimal getPercent(Object usage, Object goal) {
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
		// System.out.println("bUsage:::"+bUsage+":bGoal:"+bGoal+":percent:"+percent);
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


	public Map<String, Object> getLocationExbitionUsage(int goal,int channel0,int channel1,int channel2) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			String date =  CalendarUtil.getCurrentDate();
			params.put("endDate", date);
			params.put("periodType", CommonConstants.DateType.HOURLY.getCode());
			String dailyStartDate = CalendarUtil.getDateWithoutFormat(date,
					Calendar.DATE, -7);

			params.put("startDate", dailyStartDate);
			Map<String, Object> daily = getLocationExihibitionPeriodUsage(params,channel0,channel1,channel2);

			params.put("periodType", CommonConstants.DateType.DAILY.getCode());
			Map<String, Object> weekly = getLocationExihibitionPeriodUsage(params,channel0,channel1,channel2);

			params
					.put("periodType", CommonConstants.DateType.MONTHLY
							.getCode());
			Map<String, Object> monthly = getLocationExihibitionPeriodUsage(params,channel0,channel1,channel2);

			daily.put("EmGoal", new BigDecimal(goal));
			daily.put("Em2Goal",new BigDecimal(goal));
			daily.put("Co2Goal",  new BigDecimal(goal));

			daily.put("EmPercent", getPercent(daily.get("EmUsage"), new BigDecimal(goal)));
			daily.put("Em2Percent", getPercent(daily.get("Em2Usage"), new BigDecimal(goal)));
			daily.put("Co2Percent", getPercent(daily.get("Co2Usage"), new BigDecimal(goal)));


			weekly.put("EmGoal", new BigDecimal(goal*7));
			weekly.put("Em2Goal",new BigDecimal(goal*7));
			weekly.put("Co2Goal",  new BigDecimal(goal*7));

			weekly.put("EmPercent", getPercent(weekly.get("EmUsage"), new BigDecimal(goal*7)));
			weekly.put("Em2Percent", getPercent(weekly.get("Em2Usage"), new BigDecimal(goal*7)));
			weekly.put("Co2Percent", getPercent(weekly.get("Co2Usage"), new BigDecimal(goal*7)));


			monthly.put("EmGoal", new BigDecimal(goal*30));
			monthly.put("Em2Goal",new BigDecimal(goal*30));
			monthly.put("Co2Goal",  new BigDecimal(goal*30));

			monthly.put("EmPercent", getPercent(monthly.get("EmUsage"), new BigDecimal(goal*30)));
			monthly.put("Em2Percent", getPercent(monthly.get("Em2Usage"), new BigDecimal(goal*30)));
			monthly.put("Co2Percent", getPercent(monthly.get("Co2Usage"), new BigDecimal(goal*30)));

			retMap.put("daily", daily);
			retMap.put("weekly", weekly);
			retMap.put("monthly", monthly);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return retMap;
	}

	public Map<String, Object> getLocationExbitionUsageJeju(int forwardGoal, int reverseGoal,  int wmGoal, int co2Goal, int meteringGoal){
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			String date =  CalendarUtil.getCurrentDate();

			params.put("periodType", CommonConstants.DateType.HOURLY.getCode());
			String dailyStartDate = CalendarUtil.getDateWithoutFormat(date,
					Calendar.DATE, -7);
			params.put("startDate", dailyStartDate);
			params.put("endDate", date);
			Map<String, Object> daily = getLocationExihibitionPeriodUsageJeju(params);
			logger.debug("daily forward["+daily.get("forwardUsage")+"] reverse["+daily.get("reverseUsage")+"] wm["+daily.get("wmUsage")+"] co2["+daily.get("co2Usage")+"] metering["+daily.get("meteringCnt")+"]");
			logger.debug("daily Before forward["+daily.get("beforeForwardUsage")+"] reverse["+daily.get("beforeReverseUsage")+"] wm["+daily.get("beforeWmUsage")+"] co2["+daily.get("beforeCo2Usage")+"] metering["+daily.get("beforeMeteringCnt")+"]");

			params.put("periodType", CommonConstants.DateType.DAILY.getCode());
			params.put("startDate", dailyStartDate);
			params.put("endDate", date);
			Map<String, Object> weekly = getLocationExihibitionPeriodUsageJeju(params);
			logger.debug("weekly forward["+weekly.get("forwardUsage")+"] reverse["+weekly.get("reverseUsage")+"] wm["+weekly.get("wmUsage")+"] co2["+weekly.get("co2Usage")+"] metering["+weekly.get("meteringCnt")+"]");
			logger.debug("weekly Before forward["+weekly.get("beforeForwardUsage")+"] reverse["+weekly.get("beforeReverseUsage")+"] wm["+weekly.get("beforeWmUsage")+"] co2["+weekly.get("beforeCo2Usage")+"] metering["+weekly.get("beforeMeteringCnt")+"]");

			params.put("periodType", CommonConstants.DateType.MONTHLY.getCode());
			params.put("startDate", dailyStartDate);
			params.put("endDate", date);
			Map<String, Object> monthly = getLocationExihibitionPeriodUsageJeju(params);
			logger.debug("monthly forward["+monthly.get("forwardUsage")+"] reverse["+monthly.get("reverseUsage")+"] wm["+monthly.get("wmUsage")+"] co2["+monthly.get("co2Usage")+"] metering["+monthly.get("meteringCnt")+"]");
			logger.debug("monthly Before forward["+monthly.get("beforeForwardUsage")+"] reverse["+monthly.get("beforeReverseUsage")+"] wm["+monthly.get("beforeWmUsage")+"] co2["+monthly.get("beforeCo2Usage")+"] metering["+monthly.get("beforeMeteringCnt")+"]");

			daily.put("forwardGoal", new BigDecimal(forwardGoal));
			daily.put("reverseGoal", new BigDecimal(reverseGoal));
			daily.put("wmGoal",new BigDecimal(wmGoal));
			daily.put("co2Goal",  new BigDecimal(co2Goal));
			daily.put("meteringGoal",  new BigDecimal(meteringGoal));

			daily.put("forwardPercent", getPercent((daily.get("forwardUsage")!=null ? daily.get("forwardUsage"):0), new BigDecimal(forwardGoal)));
			daily.put("reversePercent", getPercent((daily.get("reverseUsage")!=null ? daily.get("reverseUsage"):0), new BigDecimal(reverseGoal)));
			daily.put("wmPercent", getPercent((daily.get("wmUsage")!=null ? daily.get("wmUsage"):0), new BigDecimal(wmGoal)));
			daily.put("co2Percent", getPercent((daily.get("co2Usage")!=null ? daily.get("co2Usage"):0), new BigDecimal(co2Goal)));
			daily.put("meteringPercent", getPercent((daily.get("meteringCnt")!=null ? Integer.parseInt((String)daily.get("meteringCnt")):0), new BigDecimal(meteringGoal)));

			daily.put("beforeForwardPercent", getPercent((daily.get("beforeForwardUsage")!=null ? daily.get("beforeForwardUsage"):0), new BigDecimal(forwardGoal)));
			daily.put("beforeReversePercent", getPercent((daily.get("beforeReverseUsage")!=null ? daily.get("beforeReverseUsage"):0), new BigDecimal(reverseGoal)));
			daily.put("beforeWmPercent", getPercent((daily.get("beforeWmUsage")!=null ? daily.get("beforeWmUsage"):0), new BigDecimal(wmGoal)));
			daily.put("beforeCo2Percent", getPercent((daily.get("beforeCo2Usage")!=null ? daily.get("beforeCo2Usage"):0), new BigDecimal(co2Goal)));
			daily.put("beforeMeteringPercent", getPercent((daily.get("beforeMeteringCnt")!=null ? Integer.parseInt((String)daily.get("beforeMeteringCnt")):0), new BigDecimal(meteringGoal)));

			weekly.put("forwardGoal", new BigDecimal(forwardGoal*7));
			weekly.put("reverseGoal",new BigDecimal(reverseGoal*7));
			weekly.put("wmGoal",  new BigDecimal(wmGoal*7));
			weekly.put("co2Goal",  new BigDecimal(co2Goal*7));
			weekly.put("meteringGoal",  new BigDecimal(meteringGoal));

			weekly.put("forwardPercent", getPercent((weekly.get("forwardUsage")!=null ? weekly.get("forwardUsage"):0), new BigDecimal(forwardGoal*7)));
			weekly.put("reversePercent", getPercent((weekly.get("reverseUsage")!=null ? weekly.get("reverseUsage"):0), new BigDecimal(reverseGoal*7)));
			weekly.put("wmPercent", getPercent((weekly.get("wmUsage")!=null ? weekly.get("wmUsage"):0), new BigDecimal(wmGoal*7)));
			weekly.put("co2Percent", getPercent((weekly.get("co2Usage")!=null ? weekly.get("co2Usage"):0), new BigDecimal(co2Goal*7)));
			weekly.put("meteringPercent", getPercent((weekly.get("meteringCnt")!=null ? Integer.parseInt((String)weekly.get("meteringCnt")):0), new BigDecimal(meteringGoal)));

			weekly.put("beforeForwardPercent", getPercent((weekly.get("beforeForwardUsage")!=null ? weekly.get("beforeForwardUsage"):0), new BigDecimal(forwardGoal*7)));
			weekly.put("beforeReversePercent", getPercent((weekly.get("beforeReverseUsage")!=null ? weekly.get("beforeReverseUsage"):0), new BigDecimal(reverseGoal*7)));
			weekly.put("beforeWmPercent", getPercent((weekly.get("beforeWmUsage")!=null ? weekly.get("beforeWmUsage"):0), new BigDecimal(wmGoal*7)));
			weekly.put("beforeCo2Percent", getPercent((weekly.get("beforeCo2Usage")!=null ? weekly.get("beforeCo2Usage"):0), new BigDecimal(co2Goal*7)));
			weekly.put("beforeMeteringPercent", getPercent((weekly.get("beforeMeteringCnt")!=null ? Integer.parseInt((String)weekly.get("beforeMeteringCnt")):0), new BigDecimal(meteringGoal)));

			monthly.put("forwardGoal", new BigDecimal(forwardGoal*30));
			monthly.put("reverseGoal",new BigDecimal(reverseGoal*30));
			monthly.put("wmGoal",  new BigDecimal(wmGoal*30));
			monthly.put("co2Goal",  new BigDecimal(co2Goal*30));
			monthly.put("meteringGoal",  new BigDecimal(meteringGoal));

			monthly.put("forwardPercent", getPercent((monthly.get("forwardUsage")!=null ? monthly.get("forwardUsage"):0), new BigDecimal(forwardGoal*30)));
			monthly.put("reversePercent", getPercent((monthly.get("reverseUsage")!=null ? monthly.get("reverseUsage"):0), new BigDecimal(reverseGoal*30)));
			monthly.put("wmPercent", getPercent((monthly.get("wmUsage")!=null ? monthly.get("wmUsage"):0), new BigDecimal(wmGoal*30)));
			monthly.put("co2Percent", getPercent((monthly.get("co2Usage")!=null ? monthly.get("co2Usage"):0), new BigDecimal(co2Goal*30)));
			monthly.put("meteringPercent", getPercent((monthly.get("meteringCnt")!=null ? Integer.parseInt((String)monthly.get("meteringCnt")):0), new BigDecimal(meteringGoal)));

			monthly.put("beforeForwardPercent", getPercent((monthly.get("beforeForwardUsage")!=null ? monthly.get("beforeForwardUsage"):0), new BigDecimal(forwardGoal*30)));
			monthly.put("beforeReversePercent", getPercent((monthly.get("beforeReverseUsage")!=null ? monthly.get("beforeReverseUsage"):0), new BigDecimal(reverseGoal*30)));
			monthly.put("beforeWmPercent", getPercent((monthly.get("beforeWmUsage")!=null ? monthly.get("beforeWmUsage"):0), new BigDecimal(wmGoal*30)));
			monthly.put("beforeCo2Percent", getPercent((monthly.get("beforeCo2Usage")!=null ? monthly.get("beforeCo2Usage"):0), new BigDecimal(co2Goal*30)));
			monthly.put("beforeMeteringPercent", getPercent((monthly.get("beforeMeteringCnt")!=null ? Integer.parseInt((String)monthly.get("beforeMeteringCnt")):0), new BigDecimal(meteringGoal)));

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
			Map<String, Object> params,int channel0,int channel1,int channel2) {

		String periodType = (String) params.get("periodType");

		BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
		params.put("dst", 0);
		Map<String, Object> tmp = null;
		String oriEndDate = (String) params.get("endDate");

		if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {

			params.put("channel", channel0);
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> emList = meteringDayDao.getUsageForExhibitionTotalByDay(params);

			params.put("channel", channel1);
			List<Object> emCo2List = meteringDayDao
					.getUsageForExhibitionTotalByDay(params);

			params.put("channel", channel2);
			List<Object> em2List = meteringDayDao
					.getUsageForExhibitionTotalByDay(params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> em2Map = new HashMap<String, Object>();
			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			if (emList != null && emList.size() > 0) {
				emMap = (Map<String, Object>) emList.get(0);
			}

			if (em2List != null && em2List.size() > 0) {
				em2Map = (Map<String, Object>) em2List.get(0);
			}

			if (emCo2List != null && emCo2List.size() > 0) {
				emCo2Map = (Map<String, Object>) emCo2List.get(0);
			}

			tmp = new HashMap<String, Object>();

			BigDecimal emUsage = new BigDecimal(emMap.get("TOTAL") == null ? 0
					: (Double) emMap.get("TOTAL")).setScale(
					BigDecimal.ROUND_DOWN, 2);
			tmp.put("EmUsage", emUsage.intValue());

			tmp.put("Em2Usage", new BigDecimal(
					em2Map.get("TOTAL") == null ? 0 : (Double) em2Map
							.get("TOTAL")).setScale(BigDecimal.ROUND_DOWN, 2)
					.intValue());

			tmp.put("Co2Usage", new BigDecimal(
					emCo2Map.get("TOTAL") == null ? 0 : (Double) emCo2Map
							.get("TOTAL")).intValue());

		} else if (CommonConstants.DateType.DAILY.getCode().equals(periodType)) {

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> emList = meteringDayDao
					.getUsageForExhibitionTotalByDay(params);

			params.put("channel", DefaultChannel.Co2.getCode());

			List<Object> emCo2List = meteringDayDao
					.getUsageForExhibitionTotalByDay(params);


			params.put("channel", 2);
			List<Object> em2List = meteringDayDao
					.getUsageForExhibitionTotalByDay(params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> em2Map = new HashMap<String, Object>();
			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			for (Object obj : emList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				emMap.put((String) emtmp.get("YYYYMMDD"), emtmp.get("TOTAL"));
			}

			for (Object obj : em2List) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				em2Map
						.put((String) emtmp.get("YYYYMMDD"), emtmp.get("TOTAL"));
			}

			for (Object obj : emCo2List) {
				Map<String, Object> emco2tmp = new HashMap<String, Object>();
				emco2tmp = (Map<String, Object>) obj;
				emCo2Map.put((String) emco2tmp.get("YYYYMMDD"), emco2tmp
						.get("TOTAL"));
			}

			BigDecimal emVal = new BigDecimal(0);
			BigDecimal em2Val = new BigDecimal(0);
			BigDecimal co2Val = new BigDecimal(0);

			for (int i = 0; i < 7; i++) {
				String yyyymmdd = CalendarUtil.getDateWithoutFormat(params
						.get("startDate")
						+ "", Calendar.DATE, i);

				tmp = new HashMap<String, Object>();

				emVal = emVal.add(new BigDecimal(
						emMap.get(yyyymmdd) == null ? 0 : (Double) emMap
								.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
						2));

				em2Val = em2Val.add(new BigDecimal(
						em2Map.get(yyyymmdd) == null ? 0 : (Double) em2Map
								.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
						2));

				co2Val = co2Val.add(new BigDecimal(
						emCo2Map.get(yyyymmdd) == null ? 0 : (Double) emCo2Map
								.get(yyyymmdd)));
			}

			tmp.put("EmUsage", emVal.intValue());
			tmp.put("Em2Usage", em2Val.intValue());
			tmp.put("Co2Usage", co2Val.intValue());
		} else if (CommonConstants.DateType.MONTHLY.getCode()
				.equals(periodType)) {


			String yyyymm = oriEndDate.substring(0, 6);
			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthTableName());
			params.put("periodType", CommonConstants.DateType.HOURLY.getCode());
			params.put("startDate", yyyymm);
			params.put("endDate", yyyymm);
			List<Object> emList = meteringDayDao
					.getUsageForExhibitionTotalByMonth(params);

			params.put("channel", DefaultChannel.Co2.getCode());

			List<Object> emCo2List = meteringDayDao
					.getUsageForExhibitionTotalByMonth(params);

			params.put("channel", 2);
			List<Object> Em2List = meteringDayDao
					.getUsageForExhibitionTotalByMonth(params);

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> em2Map = new HashMap<String, Object>();
			Map<String, Object> emCo2Map = new HashMap<String, Object>();

			for (Object obj : emList) {
				Map<String, Object> emtmp = (Map<String, Object>) obj;
				emMap.put((String) emtmp.get("YYYYMM"), emtmp.get("TOTAL"));
			}

			for (Object obj : Em2List) {
				Map<String, Object> emtmp = (Map<String, Object>) obj;
				em2Map.put((String) emtmp.get("YYYYMM"), emtmp.get("TOTAL"));
			}

			for (Object obj : emCo2List) {
				Map<String, Object> emco2tmp = (Map<String, Object>) obj;
				emCo2Map.put((String) emco2tmp.get("YYYYMM"), emco2tmp
						.get("TOTAL"));
			}
			tmp = new HashMap<String, Object>();


			BigDecimal emUsage = new BigDecimal(emMap.get(yyyymm) == null ? 0
					: (Double) emMap.get(yyyymm)).setScale(
					BigDecimal.ROUND_DOWN, 2);
			tmp.put("EmUsage", emUsage.intValue());

			tmp.put("Em2Usage", new BigDecimal(
					em2Map.get(yyyymm) == null ? 0 : (Double) em2Map
							.get(yyyymm)).setScale(BigDecimal.ROUND_DOWN, 2)
					.intValue());

			tmp.put("Co2Usage", new BigDecimal(
					emCo2Map.get(yyyymm) == null ? 0 : (Double) emCo2Map
							.get(yyyymm)).intValue());
		}
		return tmp;
	}


	@SuppressWarnings("unchecked")
	private Map<String, Object> getLocationExihibitionPeriodUsageJeju(
			Map<String, Object> params) {

		String periodType = (String) params.get("periodType");

		BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
		params.put("dst", 0);
		Map<String, Object> tmp = null;
		String oriEndDate = (String) params.get("endDate");
		logger.debug("oriEndDate: "+oriEndDate);

		if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {
			//------------
			// 일별 현재 데이타
			//------------
			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> forwardList = meteringDayDao.getUsageForExhibitionTotalByDay(params);

			params.put("channel", DefaultChannel.Co2.getCode());
			List<Object> co2List = meteringDayDao
					.getUsageForExhibitionTotalByDay(params);

			params.put("channel", 2);
			List<Object> reverseList = meteringDayDao
					.getUsageForExhibitionTotalByDay(params);

			Map<String, Object> forwardMap = new HashMap<String, Object>();
			Map<String, Object> reverseMap = new HashMap<String, Object>();
			Map<String, Object> co2Map = new HashMap<String, Object>();
			if (forwardList != null && forwardList.size() > 0) {
				forwardMap = (Map<String, Object>) forwardList.get(0);
			}

			if (reverseList != null && reverseList.size() > 0) {
				reverseMap = (Map<String, Object>) reverseList.get(0);
			}

			if (co2List != null && co2List.size() > 0) {
				co2Map = (Map<String, Object>) co2List.get(0);
			}

			tmp = new HashMap<String, Object>();

			BigDecimal forwardUsage = new BigDecimal(forwardMap.get("TOTAL") == null ? 0
					: (Double) forwardMap.get("TOTAL")).setScale(
					BigDecimal.ROUND_DOWN, 2);
			tmp.put("forwardUsage", forwardUsage.intValue());
			tmp.put("reverseUsage", new BigDecimal(
					reverseMap.get("TOTAL") == null ? 0 : (Double) reverseMap
							.get("TOTAL")).setScale(BigDecimal.ROUND_DOWN, 2)
					.intValue());
			tmp.put("co2Usage", new BigDecimal(
					co2Map.get("TOTAL") == null ? 0 : (Double) co2Map
							.get("TOTAL")).intValue());

			Map<String, Object> meteringParams = new HashMap<String, Object>();
			meteringParams.put("supplierId", 22);//한전
			meteringParams.put("meterType", "EnergyMeter");
			meteringParams.put("locationId", "8");//송당리 and 송당리
			meteringParams.put("locationId2", "112");//행원리
			meteringParams.put("searchDataType", 1);//1:일별, 3:주별, 4:월별
			meteringParams.put("searchStartDate", params.get("endDate"));
			meteringParams.put("searchEndDate", params.get("endDate"));

			//Map<String, Object> totalData = meteringDataDao.getTotalCountByLocation(meteringParams);
			//String totalCount = (String)totalData.get(5);
			String successCnt = meteringDataDao.getSuccessCountByLocationJeju(meteringParams);
			tmp.put("meteringCnt", successCnt);

			logger.info("Daily Current forwardUsage["+forwardUsage+"], reverseUsage["+new BigDecimal(
					reverseMap.get("TOTAL") == null ? 0 : (Double) reverseMap
							.get("TOTAL")).setScale(BigDecimal.ROUND_DOWN, 2)
					.intValue()+"], co2Usage["+new BigDecimal(
							co2Map.get("TOTAL") == null ? 0 : (Double) co2Map
									.get("TOTAL")).intValue()+"] meteringCnt["+successCnt+"]");
			//--------------
			// 일별 이전 데이타
			//--------------
			params.put("endDate", CalendarUtil.getDateWithoutFormat(CalendarUtil.getCurrentDate(),Calendar.DATE, -1));
			params.put("periodType", CommonConstants.DateType.HOURLY.getCode());

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> beforeForwardList = meteringDayDao.getUsageForExhibitionTotalByDay(params);

			params.put("channel", DefaultChannel.Co2.getCode());
			List<Object> beforeCo2List = meteringDayDao.getUsageForExhibitionTotalByDay(params);

			params.put("channel", 2);
			List<Object> beforeReverseList = meteringDayDao.getUsageForExhibitionTotalByDay(params);

			Map<String, Object> beforeForwardMap = new HashMap<String, Object>();
			Map<String, Object> beforeReverseMap = new HashMap<String, Object>();
			Map<String, Object> beforeCo2Map = new HashMap<String, Object>();
			if (beforeForwardList != null && beforeForwardList.size() > 0) {
				beforeForwardMap = (Map<String, Object>) beforeForwardList.get(0);
			}

			if (beforeReverseList != null && beforeReverseList.size() > 0) {
				beforeReverseMap = (Map<String, Object>) beforeReverseList.get(0);
			}

			if (beforeCo2List != null && beforeCo2List.size() > 0) {
				beforeCo2Map = (Map<String, Object>) beforeCo2List.get(0);
			}

			BigDecimal beforeForwardUsage = new BigDecimal(beforeForwardMap.get("TOTAL") == null ? 0
					: (Double) beforeForwardMap.get("TOTAL")).setScale(
					BigDecimal.ROUND_DOWN, 2);
			tmp.put("beforeForwardUsage", beforeForwardUsage.intValue());

			tmp.put("beforeReverseUsage", new BigDecimal(
					beforeReverseMap.get("TOTAL") == null ? 0 : (Double) beforeReverseMap
							.get("TOTAL")).setScale(BigDecimal.ROUND_DOWN, 2)
					.intValue());

			tmp.put("beforeCo2Usage", new BigDecimal(
					beforeCo2Map.get("TOTAL") == null ? 0 : (Double) beforeCo2Map
							.get("TOTAL")).intValue());

			meteringParams = new HashMap<String, Object>();
			meteringParams.put("supplierId", 22);//한전
			meteringParams.put("meterType", "EnergyMeter");
			meteringParams.put("locationId", "8");//송당리
			meteringParams.put("locationId2", "112");//행원리
			meteringParams.put("searchDataType", 1);//1:일별, 3:주별, 4:월별
			meteringParams.put("searchStartDate", params.get("endDate"));
			meteringParams.put("searchEndDate", params.get("endDate"));

			//totalData = meteringDataDao.getTotalCountByLocation(meteringParams);
			//totalCount = (String)totalData.get(5);
			successCnt = meteringDataDao.getSuccessCountByLocationJeju(meteringParams);
			tmp.put("beforeMeteringCnt", successCnt);

			logger.info("Daily Before forwardUsage["+beforeForwardUsage+"], reverseUsage["+new BigDecimal(
					beforeReverseMap.get("TOTAL") == null ? 0 : (Double) beforeReverseMap
							.get("TOTAL")).setScale(BigDecimal.ROUND_DOWN, 2)
					.intValue()+"], co2Usage["+new BigDecimal(
							beforeCo2Map.get("TOTAL") == null ? 0 : (Double) beforeCo2Map
									.get("TOTAL")).intValue()+"] meteringCnt["+successCnt+"]");

		} else if (CommonConstants.DateType.DAILY.getCode().equals(periodType)) {
			//------------------
			// 주별 현재 데이타
			//------------------
			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> forwardList = meteringDayDao
					.getUsageForExhibitionTotalByDay(params);

			params.put("channel", DefaultChannel.Co2.getCode());

			List<Object> co2List = meteringDayDao
					.getUsageForExhibitionTotalByDay(params);


			params.put("channel", 2);
			List<Object> reverseList = meteringDayDao
					.getUsageForExhibitionTotalByDay(params);

			Map<String, Object> forwardMap = new HashMap<String, Object>();
			Map<String, Object> reverseMap = new HashMap<String, Object>();
			Map<String, Object> co2Map = new HashMap<String, Object>();
			for (Object obj : forwardList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				forwardMap.put((String) emtmp.get("YYYYMMDD"), emtmp.get("TOTAL"));
			}

			for (Object obj : reverseList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				reverseMap
						.put((String) emtmp.get("YYYYMMDD"), emtmp.get("TOTAL"));
			}

			for (Object obj : co2List) {
				Map<String, Object> emco2tmp = new HashMap<String, Object>();
				emco2tmp = (Map<String, Object>) obj;
				co2Map.put((String) emco2tmp.get("YYYYMMDD"), emco2tmp
						.get("TOTAL"));
			}

			BigDecimal forwardVal = new BigDecimal(0);
			BigDecimal reverseVal = new BigDecimal(0);
			BigDecimal co2Val = new BigDecimal(0);

			for (int i = 0; i < 7; i++) {
				String yyyymmdd = CalendarUtil.getDateWithoutFormat(params
						.get("startDate")
						+ "", Calendar.DATE, i);
				logger.info("current weekly["+yyyymmdd+"]");
				tmp = new HashMap<String, Object>();

				forwardVal = forwardVal.add(new BigDecimal(
						forwardMap.get(yyyymmdd) == null ? 0 : (Double) forwardMap
								.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
						2));

				reverseVal = reverseVal.add(new BigDecimal(
						reverseMap.get(yyyymmdd) == null ? 0 : (Double) reverseMap
								.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
						2));

				co2Val = co2Val.add(new BigDecimal(
						co2Map.get(yyyymmdd) == null ? 0 : (Double) co2Map
								.get(yyyymmdd)));
			}

			tmp.put("forwardUsage", forwardVal.intValue());
			tmp.put("reverseUsage", reverseVal.intValue());
			tmp.put("co2Usage", co2Val.intValue());


			Map<String, Object> meteringParams = new HashMap<String, Object>();
			meteringParams.put("supplierId", 22);//한전
			meteringParams.put("meterType", "EnergyMeter");
			meteringParams.put("locationId", "8");//송당리
			meteringParams.put("locationId2", "112");//행원리
			meteringParams.put("searchDataType", 3);//1:일별, 3:주별, 4:월별
			meteringParams.put("searchStartDate", params.get("startDate"));
			meteringParams.put("searchEndDate", params.get("endDate"));

			//Map<String, Object> totalData = meteringDataDao.getTotalCountByLocation(meteringParams);
			//String totalCount = (String)totalData.get(5);
			String meteringCnt = meteringDataDao.getSuccessCountByLocationJeju(meteringParams);
			tmp.put("meteringCnt", meteringCnt);

			logger.info("Weekly Current forwardUsage["+forwardVal.intValue()+"], reverseUsage["+reverseVal.intValue()+"], co2Usage["+co2Val.intValue()+"] meteringCnt["+meteringCnt+"]");

			//--------------------
			// 주별 이전
			//--------------------
			params.put("startDate", CalendarUtil.getDateWithoutFormat(CalendarUtil.getCurrentDate(),Calendar.DATE, -14));
			params.put("endDate", CalendarUtil.getDateWithoutFormat(CalendarUtil.getCurrentDate(),Calendar.DATE, -7));
			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> beforeForwardList = meteringDayDao
					.getUsageForExhibitionTotalByDay(params);

			params.put("channel", DefaultChannel.Co2.getCode());

			List<Object> beforeCo2List = meteringDayDao
					.getUsageForExhibitionTotalByDay(params);


			params.put("channel", 2);
			List<Object> beforeReverseList = meteringDayDao
					.getUsageForExhibitionTotalByDay(params);

			Map<String, Object> beforeForwardMap = new HashMap<String, Object>();
			Map<String, Object> beforeReverseMap = new HashMap<String, Object>();
			Map<String, Object> beforeCo2Map = new HashMap<String, Object>();
			for (Object obj : beforeForwardList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				beforeForwardMap.put((String) emtmp.get("YYYYMMDD"), emtmp.get("TOTAL"));
			}

			for (Object obj : beforeReverseList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				beforeReverseMap
						.put((String) emtmp.get("YYYYMMDD"), emtmp.get("TOTAL"));
			}

			for (Object obj : beforeCo2List) {
				Map<String, Object> emco2tmp = new HashMap<String, Object>();
				emco2tmp = (Map<String, Object>) obj;
				beforeCo2Map.put((String) emco2tmp.get("YYYYMMDD"), emco2tmp
						.get("TOTAL"));
			}

			BigDecimal beforeForwardVal = new BigDecimal(0);
			BigDecimal beforeReverseVal = new BigDecimal(0);
			BigDecimal beforeCo2Val = new BigDecimal(0);

			for (int i = 0; i < 7; i++) {
				String yyyymmdd = CalendarUtil.getDateWithoutFormat(params
						.get("startDate")
						+ "", Calendar.DATE, i);
				logger.info("before weekly["+yyyymmdd+"]");

				beforeForwardVal = beforeForwardVal.add(new BigDecimal(
						beforeForwardMap.get(yyyymmdd) == null ? 0 : (Double) beforeForwardMap
								.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
						2));

				beforeReverseVal = beforeReverseVal.add(new BigDecimal(
						beforeReverseMap.get(yyyymmdd) == null ? 0 : (Double) beforeReverseMap
								.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
						2));

				beforeCo2Val = beforeCo2Val.add(new BigDecimal(
						beforeCo2Map.get(yyyymmdd) == null ? 0 : (Double) beforeCo2Map
								.get(yyyymmdd)));
			}

			tmp.put("beforeForwardUsage", beforeForwardVal.intValue());
			tmp.put("beforeReverseUsage", beforeReverseVal.intValue());
			tmp.put("beforeCo2Usage", beforeCo2Val.intValue());

			meteringParams = new HashMap<String, Object>();
			meteringParams.put("supplierId", 22);//한전
			meteringParams.put("meterType", "EnergyMeter");
			meteringParams.put("locationId", "8");//송당리
			meteringParams.put("locationId2", "112");//행원리
			meteringParams.put("searchDataType", 3);//1:일별, 3:주별, 4:월별
			meteringParams.put("searchStartDate", params.get("startDate"));
			meteringParams.put("searchEndDate", params.get("endDate"));

			//Map<String, Object> totalData = meteringDataDao.getTotalCountByLocation(meteringParams);
			//String totalCount = (String)totalData.get(5);
			meteringCnt = meteringDataDao.getSuccessCountByLocationJeju(meteringParams);
			tmp.put("beforeMeteringCnt", meteringCnt);
			logger.info("Weekly Before forwardUsage["+beforeForwardVal.intValue()+"], reverseUsage["+beforeReverseVal.intValue()+"], co2Usage["+beforeCo2Val.intValue()+"] meteringCnt["+meteringCnt+"]");
		} else if (CommonConstants.DateType.MONTHLY.getCode()
				.equals(periodType)) {

			//--------------------
			// 월별 현재
			//--------------------
			String yyyymm = oriEndDate.substring(0, 6);
			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthTableName());
			params.put("periodType", CommonConstants.DateType.HOURLY.getCode());
			params.put("startDate", yyyymm);
			params.put("endDate", yyyymm);
			List<Object> forwardList = meteringDayDao
					.getUsageForExhibitionTotalByMonth(params);

			params.put("channel", DefaultChannel.Co2.getCode());

			List<Object> co2List = meteringDayDao
					.getUsageForExhibitionTotalByMonth(params);

			params.put("channel", 2);
			List<Object> reverseList = meteringDayDao
					.getUsageForExhibitionTotalByMonth(params);

			Map<String, Object> forwardMap = new HashMap<String, Object>();
			Map<String, Object> reverseMap = new HashMap<String, Object>();
			Map<String, Object> co2Map = new HashMap<String, Object>();

			for (Object obj : forwardList) {
				Map<String, Object> emtmp = (Map<String, Object>) obj;
				forwardMap.put((String) emtmp.get("YYYYMM"), emtmp.get("TOTAL"));
			}

			for (Object obj : reverseList) {
				Map<String, Object> emtmp = (Map<String, Object>) obj;
				reverseMap.put((String) emtmp.get("YYYYMM"), emtmp.get("TOTAL"));
			}

			for (Object obj : co2List) {
				Map<String, Object> emco2tmp = (Map<String, Object>) obj;
				co2Map.put((String) emco2tmp.get("YYYYMM"), emco2tmp
						.get("TOTAL"));
			}
			tmp = new HashMap<String, Object>();


			BigDecimal forwardUsage = new BigDecimal(forwardMap.get(yyyymm) == null ? 0
					: (Double) forwardMap.get(yyyymm)).setScale(
					BigDecimal.ROUND_DOWN, 2);
			tmp.put("forwardUsage", forwardUsage.intValue());

			tmp.put("reverseUsage", new BigDecimal(
					reverseMap.get(yyyymm) == null ? 0 : (Double) reverseMap
							.get(yyyymm)).setScale(BigDecimal.ROUND_DOWN, 2)
					.intValue());

			tmp.put("co2Usage", new BigDecimal(
					co2Map.get(yyyymm) == null ? 0 : (Double) co2Map
							.get(yyyymm)).intValue());

			Map<String, Object> meteringParams = new HashMap<String, Object>();
			meteringParams.put("supplierId", 22);//한전
			meteringParams.put("meterType", "EnergyMeter");
			meteringParams.put("locationId", "8");//송당리
			meteringParams.put("locationId2", "112");//행원리
			meteringParams.put("searchDataType", 4);//1:일별, 3:주별, 4:월별
			meteringParams.put("searchStartDate", params.get("startDate")+"01");
			meteringParams.put("searchEndDate", oriEndDate.substring(0,8));

			//Map<String, Object> totalData = meteringDataDao.getTotalCountByLocation(meteringParams);
			//String totalCount = (String)totalData.get(5);
			String meteringCnt = meteringDataDao.getSuccessCountByLocationJeju(meteringParams);
			tmp.put("meteringCnt", meteringCnt);

			logger.info("Monthly["+yyyymm+"] Current forwardUsage["+forwardUsage.intValue()+"], reverseUsage["+ new BigDecimal(
					reverseMap.get(yyyymm) == null ? 0 : (Double) reverseMap
							.get(yyyymm)).setScale(BigDecimal.ROUND_DOWN, 2)
					.intValue()+"], co2Usage["+new BigDecimal(
							co2Map.get(yyyymm) == null ? 0 : (Double) co2Map
									.get(yyyymm)).intValue()+"] meteringCnt["+meteringCnt+"]");

			//--------------------
			// 월별 이전
			//--------------------
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MONTH, -1);
			yyyymm = CalendarUtil.getDateNotUsingFormat(cal).substring(0, 6);
			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthTableName());
			params.put("periodType", CommonConstants.DateType.HOURLY.getCode());
			//이전달 구하는 공식 추가
			params.put("startDate", yyyymm);
			params.put("endDate", yyyymm);
			List<Object> beforeForwardList = meteringDayDao
					.getUsageForExhibitionTotalByMonth(params);

			params.put("channel", DefaultChannel.Co2.getCode());

			List<Object> beforeCo2List = meteringDayDao
					.getUsageForExhibitionTotalByMonth(params);

			params.put("channel", 2);
			List<Object> beforeReverseList = meteringDayDao
					.getUsageForExhibitionTotalByMonth(params);

			Map<String, Object> beforeForwardMap = new HashMap<String, Object>();
			Map<String, Object> beforeReverseMap = new HashMap<String, Object>();
			Map<String, Object> beforeCo2Map = new HashMap<String, Object>();

			for (Object obj : beforeForwardList) {
				Map<String, Object> emtmp = (Map<String, Object>) obj;
				beforeForwardMap.put((String) emtmp.get("YYYYMM"), emtmp.get("TOTAL"));
			}

			for (Object obj : beforeReverseList) {
				Map<String, Object> emtmp = (Map<String, Object>) obj;
				beforeReverseMap.put((String) emtmp.get("YYYYMM"), emtmp.get("TOTAL"));
			}

			for (Object obj : beforeCo2List) {
				Map<String, Object> emco2tmp = (Map<String, Object>) obj;
				beforeCo2Map.put((String) emco2tmp.get("YYYYMM"), emco2tmp
						.get("TOTAL"));
			}

			BigDecimal beforeForwardUsage = new BigDecimal(beforeForwardMap.get(yyyymm) == null ? 0
					: (Double) beforeForwardMap.get(yyyymm)).setScale(
					BigDecimal.ROUND_DOWN, 2);
			tmp.put("beforeForwardUsage", beforeForwardUsage.intValue());

			tmp.put("beforeReverseUsage", new BigDecimal(
					beforeReverseMap.get(yyyymm) == null ? 0 : (Double) beforeReverseMap
							.get(yyyymm)).setScale(BigDecimal.ROUND_DOWN, 2)
					.intValue());

			tmp.put("beforeCo2Usage", new BigDecimal(
					beforeCo2Map.get(yyyymm) == null ? 0 : (Double) beforeCo2Map
							.get(yyyymm)).intValue());

			meteringParams = new HashMap<String, Object>();
			meteringParams.put("supplierId", 22);//한전
			meteringParams.put("meterType", "EnergyMeter");
			meteringParams.put("locationId", "8");//송당리
			meteringParams.put("locationId2", "112");//행원리
			meteringParams.put("searchDataType", 4);//1:일별, 3:주별, 4:월별
			meteringParams.put("searchStartDate", params.get("startDate")+"01");
			meteringParams.put("searchEndDate", params.get("endDate")+"31");

			//Map<String, Object> totalData = meteringDataDao.getTotalCountByLocation(meteringParams);
			//String totalCount = (String)totalData.get(5);
			meteringCnt = meteringDataDao.getSuccessCountByLocationJeju(meteringParams);
			tmp.put("meteringCnt", meteringCnt);

			logger.info("Monthly["+yyyymm+"] Before forwardUsage["+beforeForwardUsage.intValue()+"], reverseUsage["+ new BigDecimal(
					beforeReverseMap.get(yyyymm) == null ? 0 : (Double) beforeReverseMap
							.get(yyyymm)).setScale(BigDecimal.ROUND_DOWN, 2)
					.intValue()+"], co2Usage["+new BigDecimal(
							beforeCo2Map.get(yyyymm) == null ? 0 : (Double) beforeCo2Map
									.get(yyyymm)).intValue()+"] meteringCnt["+meteringCnt+"]");
		}
		return tmp;
	}





}
