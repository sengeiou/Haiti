package com.aimir.service.system.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.system.AverageUsageDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.EnergySavingGoal2Dao;
import com.aimir.dao.system.LocationDao;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.Meter;
import com.aimir.model.system.AverageUsage;
import com.aimir.model.system.EnergySavingGoal2;
import com.aimir.model.system.Location;
import com.aimir.service.system.KTSeochoExhibitManager;
import com.aimir.util.BemsStatisticUtil;
import com.aimir.util.CalendarUtil;
import com.aimir.util.TimeUtil;


@WebService(endpointInterface = "com.aimir.service.system.KTSeochoExhibitManager")
@Service(value = "ktSeochoExhibitManager")
@Transactional
public class KTSeochoExhibitManagerImpl implements KTSeochoExhibitManager {

	@Autowired
	DayEMDao dayemDao;

	@Autowired
	MonthEMDao monthemDao;

	@Autowired
	MeteringDayDao meteringDayDao;

	@Autowired
	AverageUsageDao averageUsageDao;

	@Autowired
	EnergySavingGoal2Dao energySavingGoal2Dao;

	@Autowired
	LocationDao locationDao;

	@Autowired
	CodeDao codeDao;

	@Autowired
	EndDeviceDao endDeviceDao;

	public List<EnergySavingGoal2> getSavingGoal(String searchDateType,
			String energyType, String startDate, Integer supplierId) {
		AverageUsage averageUsage = averageUsageDao.getAverageUsageByUsed();
		String energyTypeTemp = "";
		if ("EM".equals(energyType)) {

			energyTypeTemp = "0";
		} else if ("GM".equals(energyType)) {

			energyTypeTemp = "1";
		} else if ("WM".equals(energyType)) {

			energyTypeTemp = "2";
		}
		return energySavingGoal2Dao.getEnergySavingGoal2ListByAverageUsage(
				searchDateType, energyTypeTemp, startDate, supplierId,
				averageUsage.getId());

	}

	public Map<String, Object> getEnergyDataByLocation(String locationId) {

		List<Location> locList = locationDao.getChildren(Integer
				.parseInt(locationId));
		String date = CalendarUtil.getCurrentDate();

		String dailyStartDate = "";
		String dailyEndDate = "";

		String preDailyStartDate = "";
		String preDailyEndDate = "";

		String weeklyStartDate = "";
		String weeklyEndDate = "";

		String preWeeklyStartDate = "";
		String preWeeklyEndDate = "";

		String monthlyStartDate = "";
		String monthlyEndDate = "";

		String preMonthlyStartDate = "";
		String preMonthlyEndDate = "";

		Map<String, Object> params = new HashMap<String, Object>();

		dailyStartDate = date;
		dailyEndDate = date;

		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		Map<String, Object> total = new HashMap<String, Object>();
		for (Location loc : locList) {
			Map<String, Object> tmp = new HashMap<String, Object>();

			Map<String, Object> lightDaily = getEndDeviceLightUsage(
					loc.getId(), dailyStartDate, dailyEndDate,
					CommonConstants.DateType.HOURLY.getCode());
			Map<String, Object> heatDaily = getEndDeviceHeatUsage(loc.getId(),
					dailyStartDate, dailyEndDate,
					CommonConstants.DateType.HOURLY.getCode());

			preDailyEndDate = CalendarUtil.getDateWithoutFormat(date,
					Calendar.DATE, -7);
			Map<String, Object> preLightDaily = getEndDeviceLightUsage(loc
					.getId(), dailyStartDate, preDailyEndDate,
					CommonConstants.DateType.HOURLY.getCode());
			Map<String, Object> preHeatDaily = getEndDeviceHeatUsage(loc
					.getId(), dailyStartDate, preDailyEndDate,
					CommonConstants.DateType.HOURLY.getCode());

			Calendar c = Calendar.getInstance();
			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
			c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			weeklyStartDate = formatter1.format(c.getTime());
			c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
			weeklyEndDate = formatter1.format(c.getTime());

			Map<String, Object> lightWeekly = getEndDeviceLightUsage(loc
					.getId(), weeklyStartDate, weeklyEndDate,
					CommonConstants.DateType.DAILY.getCode());
			Map<String, Object> heatWeekly = getEndDeviceHeatUsage(loc.getId(),
					weeklyStartDate, weeklyEndDate,
					CommonConstants.DateType.DAILY.getCode());

			preWeeklyStartDate = CalendarUtil.getDateWithoutFormat(
					weeklyStartDate, Calendar.DATE, -7);
			preWeeklyEndDate = CalendarUtil.getDateWithoutFormat(weeklyEndDate,
					Calendar.DATE, -7);

			params.put("startDate", preWeeklyStartDate);
			params.put("endDate", preWeeklyEndDate);
			// Map<String, Object> preWeekly =
			// getExihibitionPeriodUsageForEMUsage(params);

			Map<String, Object> preLightWeekly = getEndDeviceLightUsage(loc
					.getId(), preWeeklyStartDate, preWeeklyEndDate,
					CommonConstants.DateType.DAILY.getCode());
			Map<String, Object> preHeatWeekly = getEndDeviceHeatUsage(loc
					.getId(), preWeeklyStartDate, preWeeklyEndDate,
					CommonConstants.DateType.DAILY.getCode());

			monthlyStartDate = date.substring(0, 6);
			monthlyEndDate = date.substring(0, 6);
			params.put("startDate", monthlyStartDate);
			params.put("endDate", monthlyEndDate);
			params
					.put("periodType", CommonConstants.DateType.MONTHLY
							.getCode());
			// Map<String, Object> monthly =
			// getExihibitionPeriodUsageForEMUsage(params);
			Map<String, Object> lightMonthly = getEndDeviceLightUsage(loc
					.getId(), monthlyStartDate, monthlyEndDate,
					CommonConstants.DateType.MONTHLY.getCode());
			Map<String, Object> heatMonthly = getEndDeviceHeatUsage(
					loc.getId(), monthlyStartDate, monthlyEndDate,
					CommonConstants.DateType.MONTHLY.getCode());

			preMonthlyStartDate = CalendarUtil.getDateWithoutFormat(date,
					Calendar.MONTH, -1).substring(0, 6);
			preMonthlyEndDate = CalendarUtil.getDateWithoutFormat(date,
					Calendar.MONTH, -1).substring(0, 6);

			Map<String, Object> preLightMonthly = getEndDeviceLightUsage(loc
					.getId(), preMonthlyStartDate, preMonthlyEndDate,
					CommonConstants.DateType.MONTHLY.getCode());
			Map<String, Object> preHeatMonthly = getEndDeviceHeatUsage(loc
					.getId(), preMonthlyStartDate, preMonthlyEndDate,
					CommonConstants.DateType.MONTHLY.getCode());

			
//			System.out.println(loc.getName()+":lightDaily:"+lightDaily);
//			System.out.println(loc.getName()+":preLightDaily:"+preLightDaily);
			tmp.put("LIGHTDAILY", lightDaily);
			tmp.put("PRELIGHTDAILY", preLightDaily);

			//System.out.println(loc.getName()+":lightWeekly:"+lightWeekly);
			//System.out.println(loc.getName()+":preLightWeekly:"+preLightWeekly);
			tmp.put("LIGHTWEEKLY", lightWeekly);
			tmp.put("PRELIGHTWEEKLY", preLightWeekly);
			
			//System.out.println(loc.getName()+":lightMonthly:"+lightMonthly);
			//System.out.println(loc.getName()+":preLightMonthly:"+preLightMonthly);
			tmp.put("LIGHTMONTHLY", lightMonthly);
			tmp.put("PRELIGHTMONTHLY", preLightMonthly);
			
           
			tmp.put("HEATDAILY", heatDaily);
			tmp.put("PREHEATDAILY", preHeatDaily);

           	tmp.put("HEATWEEKLY", heatWeekly);
			tmp.put("PREHEATWEEKLY", preHeatWeekly);

			tmp.put("HEATMONTHLY", heatMonthly);
			tmp.put("PREHEATMONTHLY", preHeatMonthly);
			
						
			tmp.put("WEEKLY", getWeekEmUsage(weeklyStartDate, weeklyEndDate,loc.getId(),"CUR"));
			tmp.put("PREWEEKLY", getWeekEmUsage(preWeeklyStartDate, preWeeklyEndDate,loc.getId(),"PRE"));
			
			total.put(loc.getName(), tmp);
		}

		List<Map<String, Object>> preDailyRanking = getRankDaily(locationId,CalendarUtil.getDateWithoutFormat(date,
				Calendar.DATE, -2),CalendarUtil.getDateWithoutFormat(date,
						Calendar.DATE, -2),CalendarUtil.getDateWithoutFormat(date,
								Calendar.DATE, -1),CalendarUtil.getDateWithoutFormat(date,
										Calendar.DATE, -1),CommonConstants.DateType.HOURLY.getCode());
		List<Map<String, Object>> dailyRanking = getRankDaily(locationId,CalendarUtil.getDateWithoutFormat(date,
				Calendar.DATE, -1),CalendarUtil.getDateWithoutFormat(date,
						Calendar.DATE, -1),date,date,CommonConstants.DateType.HOURLY.getCode());
		setRankingCurrent(dailyRanking,getRankingPre(preDailyRanking),total,CommonConstants.DateType.HOURLY.getCode());
		List<Map<String, Object>> preWeeklyRanking = getRankDaily(locationId,CalendarUtil.getDateWithoutFormat(
				preWeeklyStartDate, Calendar.DATE, -7),CalendarUtil.getDateWithoutFormat(
						preWeeklyEndDate, Calendar.DATE, -1),preWeeklyStartDate,preWeeklyEndDate,CommonConstants.DateType.DAILY.getCode());
	
		List<Map<String, Object>> weeklyRanking = getRankDaily(locationId,preWeeklyStartDate,preWeeklyEndDate,weeklyStartDate,weeklyEndDate,CommonConstants.DateType.DAILY.getCode());
		setRankingCurrent(weeklyRanking,getRankingPre(preWeeklyRanking),total,CommonConstants.DateType.DAILY.getCode());
		

		List<Map<String, Object>> preMonthlyRanking = getRankMonthly(locationId,CalendarUtil.getDateWithoutFormat(
				date, Calendar.MONTH, -2),CalendarUtil.getDateWithoutFormat(
						date, Calendar.MONTH, -1));
		List<Map<String, Object>> monthlyRanking = getRankMonthly(locationId,CalendarUtil.getDateWithoutFormat(
				date, Calendar.MONTH, -1),date);
		
		setRankingCurrent(monthlyRanking,getRankingPre(preMonthlyRanking),total,CommonConstants.DateType.MONTHLY.getCode());
		Map<String,Object> retMap= new HashMap<String,Object>();
		retMap.put("daily",dailyRanking);
		retMap.put("weekly",weeklyRanking);
		retMap.put("monthly",monthlyRanking);
	
		return retMap;
	}

	private Map<String, Object> getRankingPre(List<Map<String, Object>> rankList){
		int ranking = 1;
		Map<String, Object> rankMap = new HashMap<String, Object>();
		for (Map<String, Object> rank : rankList) {
//			System.out.println("location name:" + rank.get("NAME") + ":rank:"
//					+ ranking);
			rankMap.put((String) rank.get("NAME"), ranking);
			ranking++;
		}
		return rankMap;
	}
	
	private void setRankingCurrent(List<Map<String, Object>> rankList,Map<String, Object> preRank,Map<String, Object> total,String periodType){
		int ranking = 1;
		
		for (Map<String, Object> rank : rankList) {
			rank.put("RANK",ranking);
			rank.put("PRERANK",preRank.get((String)rank.get("NAME")));
			if(periodType.equals(CommonConstants.DateType.HOURLY.getCode())){
		     Map<String,Object> lightDaily = (Map<String, Object>)((Map<String, Object>)total.get((String)rank.get("NAME"))).get("LIGHTDAILY");
		     Map<String,Object> heatDaily = (Map<String, Object>)((Map<String, Object>)total.get((String)rank.get("NAME"))).get("HEATDAILY");
		     
		     
		     Map<String,Object> preLightDaily = (Map<String, Object>)((Map<String, Object>)total.get((String)rank.get("NAME"))).get("PRELIGHTDAILY");
		     Map<String,Object> preHeatDaily = (Map<String, Object>)((Map<String, Object>)total.get((String)rank.get("NAME"))).get("PREHEATDAILY");
			 rank.put("LIGHT",lightDaily);
			 rank.put("HEAT",heatDaily);
			 rank.put("PRELIGHT",preLightDaily);
			 rank.put("PREHEAT",preHeatDaily);			 
			 
			 Map<String,Object> daily = new HashMap<String,Object>();
			 Map<String,Object> preDaily = new HashMap<String,Object>();
			 for(int i=0;i<24;i++){
				 String hh = TimeUtil.to2Digit(i);
				 daily.put("EMUSAGE_"+hh,rank.get("CURVALUE"+hh));
				 preDaily.put("EMUSAGE_"+hh,rank.get("PREVALUE"+hh));
			 }
//			 System.out.println("lightDaily:::"+lightDaily);
//			 System.out.println("preLightDaily:::"+preLightDaily);
			 rank.put("EXPLIGHT",BemsStatisticUtil.getDailyExpect(preLightDaily,lightDaily,"EM"));
			 rank.put("EXPHEAT",BemsStatisticUtil.getDailyExpect(preHeatDaily,heatDaily,"EM"));			 
			 rank.put("EXP",BemsStatisticUtil.getDailyExpect(preDaily,daily,"EM"));
			 //System.out.println("EXP::::::::::"+BemsStatisticUtil.getDailyExpect(preDaily,daily,"EM"));
			}else if(periodType.equals(CommonConstants.DateType.DAILY.getCode())){
				
				 Map<String,Object> lightWeekly = (Map<String, Object>)((Map<String, Object>)total.get((String)rank.get("NAME"))).get("LIGHTWEEKLY");
			     Map<String,Object> heatWeekly = (Map<String, Object>)((Map<String, Object>)total.get((String)rank.get("NAME"))).get("HEATWEEKLY");
			     
			     Map<String,Object> preLightWeekly = (Map<String, Object>)((Map<String, Object>)total.get((String)rank.get("NAME"))).get("PRELIGHTWEEKLY");
			     Map<String,Object> preHeatWeekly = (Map<String, Object>)((Map<String, Object>)total.get((String)rank.get("NAME"))).get("PREHEATWEEKLY");	
				
			 rank.put("LIGHT",lightWeekly);
			 rank.put("HEAT",heatWeekly);
			 rank.put("PRELIGHT",preLightWeekly);
			 rank.put("PREHEAT",preHeatWeekly);
//			 System.out.println("preLightWeekly:"+preLightWeekly);
//			 System.out.println("lightWeekly:"+lightWeekly);
//			 System.out.println("BemsStatisticUtil.getWeeklyExpect(preLightWeekly,lightWeekly,):"+BemsStatisticUtil.getWeeklyExpect(preLightWeekly,lightWeekly,"EM"));
			 rank.put("EXPLIGHT",BemsStatisticUtil.getWeeklyExpect(preLightWeekly,lightWeekly,"EM"));
			 rank.put("EXPHEAT",BemsStatisticUtil.getWeeklyExpect(preHeatWeekly,heatWeekly,"EM"));	
			 Map<String, Object> curExp = (Map<String, Object>)((Map<String, Object>)total.get((String)rank.get("NAME"))).get("WEEKLY");
			 Map<String, Object> preExp = (Map<String, Object>)((Map<String, Object>)total.get((String)rank.get("NAME"))).get("PREWEEKLY");
			 
			
			
			 Map<String,Object> weekly = new HashMap<String,Object>();
			 Map<String,Object> preWeekly = new HashMap<String,Object>();
			 for(int i=1;i<8;i++){
				 String hh = TimeUtil.to2Digit(i);
				 rank.put("CURVALUE"+hh,curExp.get("CURUSAGE"+hh));
				 rank.put("PREVALUE"+hh,preExp.get("PREUSAGE"+hh));
				 weekly.put("EMUSAGE_"+hh,curExp.get("CURUSAGE"+hh));
				 preWeekly.put("EMUSAGE_"+hh,preExp.get("PREUSAGE"+hh));
			 }
			 weekly.put("VALUECNT", curExp.get("VALUECNT"));
			 preWeekly.put("VALUECNT", preExp.get("VALUECNT"));
			 rank.put("EXP",BemsStatisticUtil.getWeeklyExpect(preWeekly,weekly,"EM"));
			}else if(periodType.equals(CommonConstants.DateType.MONTHLY.getCode())){
				
				Map<String,Object> lightMonthly = (Map<String, Object>)((Map<String, Object>)total.get((String)rank.get("NAME"))).get("LIGHTMONTHLY");
			     Map<String,Object> heatMonthly = (Map<String, Object>)((Map<String, Object>)total.get((String)rank.get("NAME"))).get("HEATMONTHLY");
			     
			     Map<String,Object> preLightMonthly = (Map<String, Object>)((Map<String, Object>)total.get((String)rank.get("NAME"))).get("PRELIGHTMONTHLY");
			     Map<String,Object> preHeatMonthly = (Map<String, Object>)((Map<String, Object>)total.get((String)rank.get("NAME"))).get("PREHEATMONTHLY");	
			 rank.put("LIGHT",lightMonthly);
		     rank.put("HEAT",heatMonthly);
		     rank.put("PRELIGHT",preLightMonthly);
			 rank.put("PREHEAT",preHeatMonthly);
			 
			 Map<String,Object> monthly = new HashMap<String,Object>();
			 Map<String,Object> preMonthly = new HashMap<String,Object>();
			 for(int i=1;i<32;i++){
				 String hh = TimeUtil.to2Digit(i);
				 monthly.put("EMUSAGE_"+hh,rank.get("CURVALUE"+hh));
				 preMonthly.put("EMUSAGE_"+hh,rank.get("PREVALUE"+hh));
			 }
			
			 rank.put("EXPLIGHT",BemsStatisticUtil.getMonthlyExpect(preLightMonthly,lightMonthly,"EM"));
			 rank.put("EXPHEAT",BemsStatisticUtil.getMonthlyExpect(preHeatMonthly,heatMonthly,"EM"));	
			 rank.put("EXP",BemsStatisticUtil.getMonthlyExpect(preMonthly,monthly,"EM"));
			}
			ranking++;
		}
	}
	private List<Map<String, Object>> getRankDaily(String locationId,String preStartDate,String preEndDate,String startDate,String endDate,String periodType) {
		Map<String, Object> rankingParamsDay = new HashMap<String, Object>();
		
		rankingParamsDay.put("preStartDate", preStartDate);
		rankingParamsDay.put("preEndDate", preEndDate);
		rankingParamsDay.put("startDate", startDate);
		rankingParamsDay.put("endDate", endDate);
		rankingParamsDay.put("locationId", Integer.parseInt(locationId));
		rankingParamsDay.put("periodType", periodType);
		List<Map<String, Object>> preDailyRanking = meteringDayDao
				.getBemsFloorUsageReductRankingDay(rankingParamsDay);

		return preDailyRanking;
	}
	
	private List<Map<String, Object>> getRankMonthly(String locationId,String startDate,String endDate) {
		Map<String, Object> rankingParamsMonth = new HashMap<String, Object>();

		rankingParamsMonth.put("startDate", startDate.substring(0, 6));
		rankingParamsMonth.put("endDate", endDate.substring(0, 6));
		rankingParamsMonth.put("locationId", Integer.parseInt(locationId));
		List<Map<String, Object>> monthlyRanking = meteringDayDao
				.getBemsFloorUsageReductRankingMonth(rankingParamsMonth);

		return monthlyRanking;
	}

	private Map<String, Object> getEndDeviceLightUsage(Integer locationId,
			String startDate, String endDate, String periodType) {
//		Set<Condition> conditions0 = new HashSet<Condition>(0);
//		conditions0.add(new Condition("location.id",
//				new Object[] { locationId }, null, Restriction.EQ));
//		conditions0.add(new Condition("categoryCode.id", new Object[] { codeDao
//				.getCodeIdByCode("1.9.1.2.1") }, null, Restriction.EQ));
		List<Integer> categoryIdList = new ArrayList<Integer>();
		categoryIdList.add(codeDao.getCodeIdByCode("1.9.1.2.1"));
		List<EndDevice> lightEndDeviceList = getEndDeviceByParentLocation(locationId,categoryIdList);

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("locationId", locationId);
		params.put("root", false);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("periodType", periodType);
		// Map<String, Object> daily =
		// getExihibitionPeriodUsageForEMUsage(params);

		Map<String, Object> endDeviceUsage = null;
		try {
			endDeviceUsage = getPeriodUsage(params, lightEndDeviceList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return endDeviceUsage;
	}
	
	public List<EndDevice> getEndDeviceByParentLocation(Integer locationId,List<Integer> categoryIdList) {

		
		List<Integer> locationIdList = locationDao.getChildLocationId(locationId);
		
		List<EndDevice> endDeviceList = endDeviceDao.getEndDevicesByParentLocations(locationIdList, categoryIdList);

		return endDeviceList;
	}

	private Map<String, Object> getEndDeviceHeatUsage(Integer locationId,
			String startDate, String endDate, String periodType) {

//		Set<Condition> conditions1 = new HashSet<Condition>(0);
//		conditions1.add(new Condition("location.id",
//				new Object[] { locationId }, null, Restriction.EQ));
//		conditions1.add(new Condition("categoryCode.id", new Object[] { codeDao
//				.getCodeIdByCode("1.9.1.1.1") }, null, Restriction.EQ));
//		List<EndDevice> heatEndDeviceList0 = endDeviceDao
//				.findByConditions(conditions1);
//
//		Set<Condition> conditions2 = new HashSet<Condition>(0);
//		conditions2.add(new Condition("location.id",
//				new Object[] { locationId }, null, Restriction.EQ));
//		conditions2.add(new Condition("categoryCode.id", new Object[] { codeDao
//				.getCodeIdByCode("1.9.1.1.2") }, null, Restriction.EQ));
//
//		List<EndDevice> heatEndDeviceList1 = endDeviceDao
//				.findByConditions(conditions1);
//
//		for (EndDevice endDevice : heatEndDeviceList1) {
//			heatEndDeviceList0.add(endDevice);
//		}

		List<Integer> categoryIdList = new ArrayList<Integer>();
		categoryIdList.add(codeDao.getCodeIdByCode("1.9.1.1.1"));
		categoryIdList.add(codeDao.getCodeIdByCode("1.9.1.1.2"));
		List<EndDevice> heatEndDeviceList = getEndDeviceByParentLocation(locationId,categoryIdList);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("locationId", locationId);
		params.put("root", false);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("periodType", periodType);
		// Map<String, Object> daily =
		// getExihibitionPeriodUsageForEMUsage(params);

		Map<String, Object> endDeviceUsage = null;
		try {
			endDeviceUsage = getPeriodUsage(params, heatEndDeviceList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return endDeviceUsage;
	}
	private Map<String, Object> getWeekEmUsage(String startDate,String endDate,Integer locationId,String cur) {

		
		Map<String, Object> params = new HashMap<String,Object>();
		params.put("dst", 0);
		params.put("periodType", CommonConstants.DateType.DAILY.getCode());
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("root", false);
		params.put("locationId", locationId);
		params.put("channel", DefaultChannel.Usage.getCode());
		params.put("meterType", CommonConstants.MeterType.EnergyMeter
				.getDayTableName());	
		
		Map<String, Object> tmp = new HashMap<String, Object>();
		
		List<Object> emList = meteringDayDao
				.getUsageForLocationByDay(params);

		Map<String, Object> emMap = new HashMap<String, Object>();

		for (Object obj : emList) {
			Map<String, Object> emtmp = new HashMap<String, Object>();
			emtmp = (Map<String, Object>) obj;
			emMap.put((String) emtmp.get("YYYYMMDD"), emtmp.get("TOTAL"));
		}

		BigDecimal emVal = new BigDecimal(0);

		
		for (int i = 1; i < 8; i++) {
			String yyyymmdd = CalendarUtil.getDateWithoutFormat(params
					.get("startDate")
					+ "", Calendar.DATE, i-1);
			String hh = TimeUtil.to2Digit(i);

			emVal = emVal.add(new BigDecimal(
					emMap.get(yyyymmdd) == null ? 0 : (Double) emMap
							.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
					2));
            if(emMap.get(yyyymmdd) != null){
            	tmp.put(cur+"USAGE" + hh, new BigDecimal((Double) emMap
						.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
								2));
            	
            }else{
            	
            	tmp.put(cur+"USAGE" + hh, new BigDecimal(0));
            }
			

		}	
		
		//System.out.println("tmp########:"+tmp);
		return tmp;
	}
	
  /*
	@SuppressWarnings("unchecked")
	private Map<String, Object> getExihibitionPeriodUsageForEMUsage(
			Map<String, Object> params) {

		String periodType = (String) params.get("periodType");
		// String billPrefix = "Billing";

		params.put("dst", 0);
		Map<String, Object> tmp = null;
		if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> emList = meteringDayDao
					.getUsageForLocationByDay(params);

			Map<String, Object> emMap = new HashMap<String, Object>();

			if (emList != null && emList.size() > 0) {
				emMap = (Map<String, Object>) emList.get(0);
			}

			tmp = new HashMap<String, Object>();

			tmp.put("EMUSAGE", new BigDecimal(emMap.get("TOTAL") == null ? 0
					: (Double) emMap.get("TOTAL")).setScale(
					BigDecimal.ROUND_DOWN, 2));

			for (int i = 0; i < 24; i++) {
				String hh = TimeUtil.to2Digit(i);
				tmp.put("EMUSAGE_" + hh, emMap.get("VALUE" + hh));
			}

		} else if (CommonConstants.DateType.DAILY.getCode().equals(periodType)) {

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> emList = meteringDayDao
					.getUsageForLocationByDay(params);

			Map<String, Object> emMap = new HashMap<String, Object>();

			for (Object obj : emList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				emMap.put((String) emtmp.get("YYYYMMDD"), emtmp.get("TOTAL"));
			}

			BigDecimal emVal = new BigDecimal(0);

			tmp = new HashMap<String, Object>();
			for (int i = 1; i < 8; i++) {
				String yyyymmdd = CalendarUtil.getDateWithoutFormat(params
						.get("startDate")
						+ "", Calendar.DATE, i);
				String hh = TimeUtil.to2Digit(i);

				emVal = emVal.add(new BigDecimal(
						emMap.get(yyyymmdd) == null ? 0 : (Double) emMap
								.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
						2));

				tmp.put("EMUSAGE_" + hh, emMap.get(yyyymmdd));

			}

			tmp.put("EMUSAGE", emVal);

		} else if (CommonConstants.DateType.MONTHLY.getCode()
				.equals(periodType)) {
			String yyyymm = (String) params.get("endDate");
			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthTableName());
			List<Object> emList = meteringDayDao
					.getUsageForLocationByMonth(params);

			Map<String, Object> emMap = new HashMap<String, Object>();

			for (Object obj : emList) {
				Map<String, Object> emtmp = (Map<String, Object>) obj;
				emMap.put((String) emtmp.get("YYYYMM"), emtmp.get("TOTAL"));
				for (int i = 1; i < 32; i++) {
					String hh = TimeUtil.to2Digit(i);

					emMap.put("VALUE" + hh, emtmp.get("VALUE" + hh));
				}
			}

			tmp = new HashMap<String, Object>();

			tmp.put("EMUSAGE", new BigDecimal(emMap.get(yyyymm) == null ? 0
					: (Double) emMap.get(yyyymm)).setScale(
					BigDecimal.ROUND_DOWN, 2));

			for (int i = 1; i < 32; i++) {
				String hh = TimeUtil.to2Digit(i);
				tmp.put("EMUSAGE_" + hh, emMap.get("VALUE" + hh));

			}

		}
		return tmp;
	}
*/
	@SuppressWarnings("unchecked")
	private Map<String, Object> getPeriodUsage(Map<String, Object> params,
			List<EndDevice> endDeviceList) throws Exception {

		String periodType = (String) params.get("periodType");

		String startDate = (String) params.get("startDate");
		String endDate = (String) params.get("endDate");

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

		Map<String, Object> tmp = null;
		if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {
			// MeteringDay 호출

			Map<String, Object> emparams = new HashMap<String, Object>();
			emparams.put("dst", 0);
			emparams.put("channel", DefaultChannel.Usage.getCode());
			emparams.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayClassName());

			params.put("today", endDate);

			// System.out.println("getUsageForEndDevicesByDay:"+params);
			List<Object> emList = meteringDayDao.getUsageForEndDevicesByDay(
					params, emparams);

			Map<String, Object> emMap = new HashMap<String, Object>();

			if (emList != null && emList.size() > 0) {
				emMap = (Map<String, Object>) emList.get(0);
			}
			tmp = new HashMap<String, Object>();
			tmp.put("EMUSAGE", new BigDecimal(emMap.get("total") == null ? 0
					: (Double) emMap.get("total")).setScale(
					BigDecimal.ROUND_DOWN, 3));
			String hh = "";
			
			for (int i = 0; i < 24; i++) {
				hh = TimeUtil.to2Digit(i);

				if(emMap.get("value" + hh) != null){
					tmp.put("EMUSAGE_" + hh,new BigDecimal(
							 (Double) emMap
									.get("value" + hh)).setScale(
							BigDecimal.ROUND_DOWN, 3));
				}else{
					
					tmp.put("EMUSAGE_" + hh,new BigDecimal(0));
				}
			
			}
			
		} else if (CommonConstants.DateType.DAILY.getCode().equals(periodType)) {

			Map<String, Object> emparams = new HashMap<String, Object>();
			emparams.put("dst", 0);
			emparams.put("channel", DefaultChannel.Usage.getCode());
			emparams.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayClassName());

			params.put("currWeekStartDate", startDate);
			params.put("currWeekEndDate", endDate);

			List<Object> emList = meteringDayDao.getUsageForEndDevicesByWeek(
					params, emparams);
			//System.out.println("getUsageForEndDevicesByWeek emList:"+emList);
			Map<String, Object> emMap = new HashMap<String, Object>();

			for (Object obj : emList) {
				Map<String, Object> emtmp = new HashMap<String, Object>();
				emtmp = (Map<String, Object>) obj;
				emMap.put((String) emtmp.get("yyyymmdd"), emtmp.get("total"));
			}
			tmp = new HashMap<String, Object>();
			//System.out.println("params:"+params);
			//System.out.println("emMap:"+emMap);
			BigDecimal emVal = new BigDecimal(0);
		
			for (int i = 1; i < 8; i++) {
				String hh = TimeUtil.to2Digit(i);
				String yyyymmdd = CalendarUtil.getDateWithoutFormat(startDate,
						Calendar.DATE, i-1);
				//System.out.println("yyyymmdd:"+yyyymmdd);
				emVal = emVal.add(new BigDecimal(
						emMap.get(yyyymmdd) == null ? 0 : (Double) emMap
								.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
						3));
				
				//System.out.println("emMap.get(yyyymmdd):"+emMap.get(yyyymmdd));
                if(emMap.get(yyyymmdd) != null){
                	tmp.put("EMUSAGE_" + hh,new BigDecimal( (Double) emMap
    								.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
    						3));
                }else{
                	
                	tmp.put("EMUSAGE_" + hh, new BigDecimal(0));
                }
				
			}
			tmp.put("EMUSAGE", emVal);
			
			//System.out.println("tmp:::::::::"+tmp);
		} else if (CommonConstants.DateType.MONTHLY.getCode()
				.equals(periodType)) {
			Map<String, Object> emparams = new HashMap<String, Object>();
			emparams.put("dst", 0);
			emparams.put("channel", DefaultChannel.Usage.getCode());
			emparams.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getMonthClassName());

			params.put("searchStartDate", startDate);
			params.put("searchEndDate", endDate);

			List<Object> emList = meteringDayDao
					.getUsageForEndDevicesByMonthPeriod(params, emparams);

			Map<String, Object> emMap = new HashMap<String, Object>();
           
			for (Object obj : emList) {
				Map<String, Object> emtmp = (Map<String, Object>) obj;
				emMap.put((String) emtmp.get("yyyymm"), emtmp.get("total"));
				for (int i = 1; i < 32; i++) {
					String hh = TimeUtil.to2Digit(i);

					emMap.put("value" + hh, emtmp.get("value" + hh));
				}
			}
			
			tmp = new HashMap<String, Object>();
			tmp.put("EMUSAGE", new BigDecimal(emMap.get(endDate) == null ? 0
					: (Double) emMap.get(endDate)).setScale(
					BigDecimal.ROUND_DOWN, 3));
			
			for (int i = 1; i < 32; i++) {
				String mm = TimeUtil.to2Digit(i);

				if(emMap.get("value" + mm) != null){
					tmp.put("EMUSAGE_" + mm, new BigDecimal( (Double) emMap
									.get("value" + mm)).setScale(
							BigDecimal.ROUND_DOWN, 3));
				}else{
					
                	tmp.put("EMUSAGE_" + mm, new BigDecimal(0));
				}
				
			}
		
		}

		// EndDevice 분류에 해당하는 EndDevice Id 목록을 조회한후
		// 각각의 모뎀Id, 미터Id, 미터의모뎀Id 를 조회하고
		// 검침데이터와 모든 ID를 or 조건으로 걸어서 검침데이터를 전기,가스,수도 별로 조회한다.
		// 시간or요일or달or분기,전기검침량,가스검침량,수도검침량,

		return tmp;
	}

	public Map<String, Object> getEnergyData(String locationId) {
		String date = CalendarUtil.getCurrentDate();
		BigDecimal zero = new BigDecimal(0);

		String startDate = "";
		String endDate = "";

		Map<String, Object> params = new HashMap<String, Object>();

		startDate = date;
		endDate = date;

		params.put("locationId", Integer.parseInt(locationId));
		params.put("root", true);
		params.put("endDate", endDate);
		params.put("periodType", CommonConstants.DateType.HOURLY.getCode());
		Map<String, Object> daily = getExihibitionPeriodUsage(params);
		endDate = CalendarUtil.getDateWithoutFormat(date, Calendar.YEAR, -1);

		params.put("root", false);
		params.put("endDate", endDate);
		Map<String, Object> preDaily = getExihibitionPeriodUsage(params);
		 
		BigDecimal emDaily = (BigDecimal)preDaily.get("EMUSAGE");
		
		if(emDaily.compareTo(zero)==0){
			endDate = CalendarUtil.getDateWithoutFormat(date, Calendar.DATE, -7);
			params.put("endDate", endDate);
			//System.out.println("date params:"+params);
			preDaily = getExihibitionPeriodUsage(params);
		}
		//System.out.println("(emDaily.compareTo(zero)==0):"+(emDaily.compareTo(zero)==0));
		//System.out.println("preDaily:"+preDaily);
		Calendar c = Calendar.getInstance();
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		String weeklyStartDate = formatter1.format(c.getTime());
		c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		String weeklyEndDate = formatter1.format(c.getTime());

		params.put("root", true);
		params.put("startDate", weeklyStartDate);
		params.put("endDate", weeklyEndDate);
		params.put("periodType", CommonConstants.DateType.DAILY.getCode());
		Map<String, Object> weekly = getExihibitionPeriodUsage(params);

		startDate = CalendarUtil.getDateWithoutFormat(weeklyStartDate, Calendar.YEAR,
				-1);
		endDate = CalendarUtil.getDateWithoutFormat(weeklyEndDate, Calendar.YEAR, -1);
		
		params.put("root", false);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		Map<String, Object> preWeekly = getExihibitionPeriodUsage(params);

        BigDecimal emWeekly = (BigDecimal)preWeekly.get("EMUSAGE");
		
		if(emWeekly.compareTo(zero)==0){
			startDate = CalendarUtil.getDateWithoutFormat(weeklyStartDate, Calendar.DATE,
					-7);
			endDate = CalendarUtil.getDateWithoutFormat(weeklyEndDate, Calendar.DATE, -7);
			params.put("startDate", startDate);
			params.put("endDate", endDate);
			//System.out.println("Weekly params:"+params);
			preWeekly = getExihibitionPeriodUsage(params);
		}
		//System.out.println("(emWeekly.compareTo(zero)==0):"+(emWeekly.compareTo(zero)==0));
		//System.out.println("preWeekly:"+preWeekly);
		startDate = date.substring(0, 6);
		endDate = date.substring(0, 6);
		
		params.put("root", true);
		params.put("startDate", startDate);
		params.put("endDate", endDate);
		params.put("periodType", CommonConstants.DateType.MONTHLY.getCode());
		Map<String, Object> monthly = getExihibitionPeriodUsage(params);

		startDate = CalendarUtil.getDateWithoutFormat(date, Calendar.YEAR, -1)
				.substring(0, 6);
		endDate = CalendarUtil.getDateWithoutFormat(date, Calendar.YEAR, -1)
				.substring(0, 6);

		params.put("startDate", startDate);
		params.put("endDate", endDate);

		params.put("root", false);
		Map<String, Object> preMonthly = getExihibitionPeriodUsage(params);
		
		BigDecimal emMonthly = (BigDecimal)preMonthly.get("EMUSAGE");
		
		if(emMonthly.compareTo(zero)==0){
			startDate = CalendarUtil.getDateWithoutFormat(date, Calendar.MONTH,
					-1).substring(0, 6);
			endDate = CalendarUtil.getDateWithoutFormat(date, Calendar.MONTH, -1).substring(0, 6);
			params.put("startDate", startDate);
			params.put("endDate", endDate);
			
			//System.out.println("Monthly params:"+params);
			preMonthly = getExihibitionPeriodUsage(params);
		}
		//System.out.println("(emMonthly.compareTo(zero)==0):"+(emMonthly.compareTo(zero)==0));
		//System.out.println("preMonthly:"+preMonthly);

		Map<String, Object> retMap = new HashMap<String, Object>();

		retMap.put("DAILY", daily);
		retMap.put("WEEKLY", weekly);
		retMap.put("MONTHLY", monthly);

		retMap.put("PREDAILY", preDaily);
		retMap.put("PREWEEKLY", preWeekly);
		retMap.put("PREMONTHLY", preMonthly);

		return retMap;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getExihibitionPeriodUsage(
			Map<String, Object> params) {

		String periodType = (String) params.get("periodType");
		// String billPrefix = "Billing";

		
		//System.out.println("getExihibitionPeriodUsage params:"+params);
		params.put("dst", 0);
		Map<String, Object> tmp = null;
		if (CommonConstants.DateType.HOURLY.getCode().equals(periodType)) {

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> emList = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("channel", DefaultChannel.Co2.getCode());
			params.put("meterType", CommonConstants.MeterType.EnergyMeter
					.getDayTableName());
			List<Object> emCo2List = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayTableName());
			List<Object> wmList = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("channel", DefaultChannel.Co2.getCode());
			params.put("meterType", CommonConstants.MeterType.WaterMeter
					.getDayTableName());
			List<Object> wmCo2List = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("channel", DefaultChannel.Usage.getCode());
			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayTableName());
			List<Object> gmList = meteringDayDao
					.getUsageForLocationByDay(params);

			params.put("channel", DefaultChannel.Co2.getCode());
			params.put("meterType", CommonConstants.MeterType.GasMeter
					.getDayTableName());
			List<Object> gmCo2List = meteringDayDao
					.getUsageForLocationByDay(params);

			Map<String, Object> emMap = new HashMap<String, Object>();

			Map<String, Object> emCo2Map = new HashMap<String, Object>();

			if (emList != null && emList.size() > 0) {
				emMap = (Map<String, Object>) emList.get(0);
			}

			if (emCo2List != null && emCo2List.size() > 0) {
				emCo2Map = (Map<String, Object>) emCo2List.get(0);
			}

			tmp = new HashMap<String, Object>();

			tmp.put("EMUSAGE", new BigDecimal(emMap.get("TOTAL") == null ? 0
					: (Double) emMap.get("TOTAL")).setScale(
					BigDecimal.ROUND_DOWN, 2));

			tmp.put("EMCO2USAGE", new BigDecimal(
					emCo2Map.get("TOTAL") == null ? 0 : (Double) emCo2Map
							.get("TOTAL")).setScale(BigDecimal.ROUND_DOWN, 2));

			for (int i = 0; i < 24; i++) {
				String hh = TimeUtil.to2Digit(i);
				tmp.put("EMUSAGE_" + hh, new BigDecimal(emMap.get("VALUE" + hh)==null ? 0:(Double)emMap.get("VALUE" + hh)));
			}

			Map<String, Object> wmMap = new HashMap<String, Object>();

			Map<String, Object> wmCo2Map = new HashMap<String, Object>();

			if (wmList != null && wmList.size() > 0) {
				wmMap = (Map<String, Object>) wmList.get(0);
			}

			if (wmCo2List != null && wmCo2List.size() > 0) {
				wmCo2Map = (Map<String, Object>) wmCo2List.get(0);
			}

			tmp.put("WMUSAGE", new BigDecimal(wmMap.get("TOTAL") == null ? 0
					: (Double) wmMap.get("TOTAL")).setScale(
					BigDecimal.ROUND_DOWN, 2));

			tmp.put("WMCO2USAGE", new BigDecimal(
					wmCo2Map.get("TOTAL") == null ? 0 : (Double) wmCo2Map
							.get("TOTAL")).setScale(BigDecimal.ROUND_DOWN, 2));
			for (int i = 0; i < 24; i++) {
				String hh = TimeUtil.to2Digit(i);
				tmp.put("WMUSAGE_" + hh,new BigDecimal(wmMap.get("VALUE" + hh)==null ? 0:(Double)wmMap.get("VALUE" + hh)));
				//tmp.put("WMUSAGE_" + hh, wmMap.get("VALUE" + hh));
			}
			Map<String, Object> gmMap = new HashMap<String, Object>();

			Map<String, Object> gmCo2Map = new HashMap<String, Object>();

			if (gmList != null && gmList.size() > 0) {
				gmMap = (Map<String, Object>) gmList.get(0);
			}

			if (wmCo2List != null && wmCo2List.size() > 0) {
				gmCo2Map = (Map<String, Object>) gmCo2List.get(0);
			}

			tmp.put("GMUSAGE", new BigDecimal(gmMap.get("TOTAL") == null ? 0
					: (Double) gmMap.get("TOTAL")).setScale(
					BigDecimal.ROUND_DOWN, 2));

			tmp.put("GMCO2USAGE", new BigDecimal(
					gmCo2Map.get("TOTAL") == null ? 0 : (Double) gmCo2Map
							.get("TOTAL")).setScale(BigDecimal.ROUND_DOWN, 2));
			for (int i = 0; i < 24; i++) {
				String hh = TimeUtil.to2Digit(i);
				tmp.put("GMUSAGE_" + hh,new BigDecimal(gmMap.get("VALUE" + hh)==null ? 0:(Double)gmMap.get("VALUE" + hh)));
				//tmp.put("GMUSAGE_" + hh, gmMap.get("VALUE" + hh));
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

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> gmMap = new HashMap<String, Object>();
			Map<String, Object> wmMap = new HashMap<String, Object>();

			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			Map<String, Object> gmCo2Map = new HashMap<String, Object>();
			Map<String, Object> wmCo2Map = new HashMap<String, Object>();

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

			BigDecimal emVal = new BigDecimal(0);
			BigDecimal gmVal = new BigDecimal(0);
			BigDecimal wmVal = new BigDecimal(0);

			BigDecimal emCo2Val = new BigDecimal(0);
			BigDecimal gmCo2Val = new BigDecimal(0);
			BigDecimal wmCo2Val = new BigDecimal(0);
			tmp = new HashMap<String, Object>();
			//System.out.println("params:::"+params);
			//System.out.println("emMap:::"+emMap);
			
			for (int i = 1; i < 8; i++) {
				String yyyymmdd = CalendarUtil.getDateWithoutFormat(params
						.get("startDate")
						+ "", Calendar.DATE, i-1);
				String hh = TimeUtil.to2Digit(i);
				//System.out.println("yyyymmdd:::"+yyyymmdd+":"+emMap.get(yyyymmdd));
				emVal = emVal.add(new BigDecimal(
						emMap.get(yyyymmdd) == null ? 0 : (Double) emMap
								.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
						2));
				gmVal = gmVal.add(new BigDecimal(
						gmMap.get(yyyymmdd) == null ? 0 : (Double) gmMap
								.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
						2));
				wmVal = wmVal.add(new BigDecimal(
						wmMap.get(yyyymmdd) == null ? 0 : (Double) wmMap
								.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
						2));

				emCo2Val = emCo2Val.add(new BigDecimal(
						emCo2Map.get(yyyymmdd) == null ? 0 : (Double) emCo2Map
								.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
						2));
				gmCo2Val = gmCo2Val.add(new BigDecimal(
						gmCo2Map.get(yyyymmdd) == null ? 0 : (Double) gmCo2Map
								.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
						2));
				wmCo2Val = wmCo2Val.add(new BigDecimal(
						wmCo2Map.get(yyyymmdd) == null ? 0 : (Double) wmCo2Map
								.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,
						2));

				tmp.put("EMUSAGE_" + hh, new BigDecimal(emMap.get(yyyymmdd)==null ? 0:(Double)emMap.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,2));
				tmp.put("WMUSAGE_" + hh, new BigDecimal(wmMap.get(yyyymmdd)==null ? 0:(Double)wmMap.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,2));
				tmp.put("GMUSAGE_" + hh, new BigDecimal(gmMap.get(yyyymmdd)==null ? 0:(Double)gmMap.get(yyyymmdd)).setScale(BigDecimal.ROUND_DOWN,2));

			}

			tmp.put("EMUSAGE", emVal);
			tmp.put("EMCO2USAGE", emCo2Val);
			tmp.put("WMUSAGE", wmVal);
			tmp.put("WMCO2USAGE", wmCo2Val);

			tmp.put("GMUSAGE", gmVal);
			tmp.put("GMCO2USAGE", gmCo2Val);

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

			Map<String, Object> emMap = new HashMap<String, Object>();
			Map<String, Object> gmMap = new HashMap<String, Object>();
			Map<String, Object> wmMap = new HashMap<String, Object>();

			Map<String, Object> emCo2Map = new HashMap<String, Object>();
			Map<String, Object> gmCo2Map = new HashMap<String, Object>();
			Map<String, Object> wmCo2Map = new HashMap<String, Object>();

			for (Object obj : emList) {
				Map<String, Object> emtmp = (Map<String, Object>) obj;
				emMap.put((String) emtmp.get("YYYYMM"), emtmp.get("TOTAL"));
				for (int i = 1; i < 32; i++) {
					String hh = TimeUtil.to2Digit(i);
					emMap.put("VALUE" + hh,new BigDecimal(emtmp.get("VALUE" + hh)==null ? 0:(Double)emtmp.get("VALUE" + hh)));
					//emMap.put("VALUE" + hh, emtmp.get("VALUE" + hh));
				}
			}

			//System.out.println("gmList:"+gmList);
			for (Object obj : gmList) {
				Map<String, Object> gmtmp = (Map<String, Object>) obj;
				gmMap.put((String) gmtmp.get("YYYYMM"), gmtmp.get("TOTAL"));
				for (int i = 1; i < 32; i++) {
					String hh = TimeUtil.to2Digit(i);
					gmMap.put("VALUE" + hh,new BigDecimal(gmtmp.get("VALUE" + hh)==null ? 0:(Double)gmtmp.get("VALUE" + hh)));
					//gmMap.put("VALUE" + hh, gmtmp.get("VALUE" + hh));
				}
			}

			for (Object obj : wmList) {
				Map<String, Object> wmtmp = (Map<String, Object>) obj;
				wmMap.put((String) wmtmp.get("YYYYMM"), wmtmp.get("TOTAL"));
				for (int i = 1; i < 32; i++) {
					String hh = TimeUtil.to2Digit(i);
					wmMap.put("VALUE" + hh,new BigDecimal(wmtmp.get("VALUE" + hh)==null ? 0:(Double)wmtmp.get("VALUE" + hh)));
					//wmMap.put("VALUE" + hh, wmtmp.get("VALUE" + hh));
				}
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

			tmp = new HashMap<String, Object>();

			tmp.put("EMUSAGE", new BigDecimal(emMap.get(yyyymm) == null ? 0
					: (Double) emMap.get(yyyymm)).setScale(
					BigDecimal.ROUND_DOWN, 2));
			tmp.put("GMUSAGE", new BigDecimal(gmMap.get(yyyymm) == null ? 0
					: (Double) gmMap.get(yyyymm)).setScale(
					BigDecimal.ROUND_DOWN, 2));
			tmp.put("WMUSAGE", new BigDecimal(wmMap.get(yyyymm) == null ? 0
					: (Double) wmMap.get(yyyymm)).setScale(
					BigDecimal.ROUND_DOWN, 2));

			tmp.put("EMCO2USAGE", new BigDecimal(
					emCo2Map.get(yyyymm) == null ? 0 : (Double) emCo2Map
							.get(yyyymm)).setScale(BigDecimal.ROUND_DOWN, 2));
			tmp.put("GMCO2USAGE", new BigDecimal(
					gmCo2Map.get(yyyymm) == null ? 0 : (Double) gmCo2Map
							.get(yyyymm)).setScale(BigDecimal.ROUND_DOWN, 2));
			tmp.put("WMCO2USAGE", new BigDecimal(
					wmCo2Map.get(yyyymm) == null ? 0 : (Double) wmCo2Map
							.get(yyyymm)).setScale(BigDecimal.ROUND_DOWN, 2));

			for (int i = 1; i < 32; i++) {
				String hh = TimeUtil.to2Digit(i);
				
				tmp.put("EMUSAGE_" + hh, emMap.get("VALUE" + hh));
				tmp.put("WMUSAGE_" + hh, wmMap.get("VALUE" + hh));
				tmp.put("GMUSAGE_" + hh, gmMap.get("VALUE" + hh));
			}

		}
		return tmp;
	}

}
