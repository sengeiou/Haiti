package com.aimir.service.mvm.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.ChangeMeterTypeName;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.WeekDay;
import com.aimir.dao.GenericDao;
import com.aimir.dao.device.MeterMdisDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayHMDao;
import com.aimir.dao.mvm.DayHUMDao;
import com.aimir.dao.mvm.DaySPMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.dao.mvm.MonthHMDao;
import com.aimir.dao.mvm.MonthHUMDao;
import com.aimir.dao.mvm.MonthSPMDao;
import com.aimir.dao.mvm.MonthWMDao;
import com.aimir.fep.meter.saver.ManualMDSaver;
import com.aimir.model.device.Meter;
import com.aimir.service.mvm.ManualMeteringManager;
import com.aimir.service.mvm.SearchMeteringDataManager;
import com.aimir.service.mvm.bean.MeteringListData;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.constants.CommonConstants.MeteringDataType;

/**
 * 수검침 매니저
 * 
 * @see com.aimir.service.mvm.ManualMeteringManager
 * 
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 */
@Service(value = "ManualMeteringManager")
@SuppressWarnings("unchecked")
@Transactional(readOnly=false)
public class ManualMeteringManagerImpl implements ManualMeteringManager {

	// 로거
	private Logger logger = Logger.getLogger(ManualMeteringManagerImpl.class);

	// 일별 날자 기한. 이 숫자만큼 오늘로부터 전일까지의 통계를 구한다.
	private static final int PREV_DATE = 19;
	
	// 날자 포맷터
	private final SimpleDateFormat yyyyMM = new SimpleDateFormat("yyyyMM");
	private final SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
	
	// 일별 문자열 배열 (영문)
	private final String[] dayOfWeek = new String[] {
        WeekDay.Sunday.getEngName(),
        WeekDay.Monday.getEngName(),
        WeekDay.Tuesday.getEngName(),
        WeekDay.Wednesday.getEngName(),
        WeekDay.Thursday.getEngName(),
        WeekDay.Friday.getEngName(),
        WeekDay.Saturday.getEngName()
    };
	
	/**
	 * Dependency Objects
	 */
	@Autowired
	private MeterMdisDao meterMdisDao;
	
	@Autowired
	private SearchMeteringDataManager searchMeteringDataManager;

	@Autowired private DayEMDao dayEMDao;	
	@Autowired private DayGMDao dayGMDao;
	@Autowired private DayWMDao dayWMDao;
	@Autowired private DayHMDao dayHMDao;
	@Autowired private DaySPMDao daySPMDao;
	
	@SuppressWarnings("unused")
	@Autowired private DayHUMDao dayHUMDao;
	
    @Autowired private MonthEMDao monthEMDao;
    @Autowired private MonthGMDao monthGMDao;
    @Autowired private MonthWMDao monthWMDao;
    @Autowired private MonthHMDao monthHMDao;
    @Autowired private MonthSPMDao monthSPMDao;
    
    @SuppressWarnings("unused")
	@Autowired private MonthHUMDao monthHUMDao;
        
    // 수동검침 값을 전송하기 위한 객체
    // @Autowired
    ManualMDSaver mMDSaver;

    private Map<String, Object> adjustCondition(Map<String, Object> params, String dateType) {
    	
    	String meterName = (String) params.get("meterType");
    	String mdsId = (String) params.get("mdsId");
    	String friendlyName = (String) params.get("friendlyName");
    	Integer supplierId = (Integer) params.get("supplierId");
    	Integer page = (Integer) params.get("page");
    	Integer limit = (Integer) params.get("limit");
    	String sdate = (String) params.get("sdate");
    	String edate = (String) params.get("edate");
    	
    	Map<String, Object> conditionMap = new HashMap<String, Object>();
        String meterType = ChangeMeterTypeName.valueOf(meterName).getCode();
        conditionMap.put("meterType", meterType);
    	conditionMap.put("mdevId", mdsId);
        conditionMap.put("friendlyName", friendlyName);
        conditionMap.put("searchDateType", dateType);
        conditionMap.put("supplierId", supplierId);
        conditionMap.put("page", page);
        conditionMap.put("limit", limit);
        conditionMap.put("meteringSF", "s");
        conditionMap.put("isManualMeter", 1);
        
    	Calendar calendar = Calendar.getInstance();
    	
    	if(sdate == null || edate == null || sdate.isEmpty() || edate.isEmpty()) {
    		if("DAILY".equalsIgnoreCase(dateType)) {
            	conditionMap.put("searchEndDate", yyyyMMdd.format(calendar.getTime()));
            	calendar.set(Calendar.MONTH, 0);
            	calendar.set(Calendar.DATE, 1);
            	conditionMap.put("searchStartDate", yyyyMMdd.format(calendar.getTime()));
            }
    		else if("MONTHLY".equalsIgnoreCase(dateType)) {
            	conditionMap.put("searchEndDate", yyyyMM.format(calendar.getTime()));
            	calendar.set(Calendar.MONTH, 0);
            	conditionMap.put("searchStartDate", yyyyMM.format(calendar.getTime()));
	        }
    	}
    	else {
    		conditionMap.put("searchStartDate", sdate);
            conditionMap.put("searchEndDate", edate);
    	}
    	return conditionMap;
    }
    
    @Override
    public Integer getManualMeteringDataTotal(Map<String, Object> params) {
    	String dayType = (String) params.get("dayType");        
        Map<String, Object> conditionMap = adjustCondition(params, dayType);
        Integer result = null;
        if("DAILY".equalsIgnoreCase(dayType)) {
        	result = searchMeteringDataManager.getMeteringDataDailyDataTotalCount(conditionMap);
        }
        else if("MONTHLY".equalsIgnoreCase(dayType)) {
        	result = searchMeteringDataManager.getMeteringDataMonthlyDataTotalCount(conditionMap);
        }
		return result;
    }
    
    @Override
	public List<Map<String, Object>> getManualMeteringData(Map<String, Object> params) {
    	String dayType = (String) params.get("dayType");        
        Map<String, Object> conditionMap = adjustCondition(params, dayType);
        List<Map<String, Object>> result = null;
        if("DAILY".equalsIgnoreCase(dayType)) {
            result = searchMeteringDataManager.getMeteringDataDailyData(conditionMap);
        }
        else if("MONTHLY".equalsIgnoreCase(dayType)) {
            result = searchMeteringDataManager.getMeteringDataMonthlyData(conditionMap);
        }
		return result;
	}
    
	/**
	 * 미터 검색 파라미터를 배열로 만든다
	 * 
	 * 맵형식의 파라미터를 키:값 형태의 배열로 만듬
	 * 
	 * @param params
	 * @return
	 */
	private String[] parameterMapToStringArray(Map<String, String> params) {
		String condition [] = {
			"search_from", 
			"meter_id", 
			"first", 
			"max", 
			"friendly_name", 
			"is_manual_metering"
		};
		String key = null;
		for (int i = 0; i < condition.length; i++) {
			if(params.containsKey(condition[i])) {
				key = condition[i];				
				condition[i] = key + ":" + params.get(key);
			}
		}
		return condition;
	}
	
	@Override
	public List<MeteringListData> getManualMeteringData(
			Map<String, String> params, String dayType, String meterType, String supplierId) {
		
		String[] condition = parameterMapToStringArray(params);
		
		List<MeteringListData> meteringListDatas = null;
				
		if(dayType.equals("HOUR")) {
			meteringListDatas = (List<MeteringListData>) 
				searchMeteringDataManager.getMeteringDataHour(
					condition, meterType, supplierId
				);
        }
		else if(dayType.equals("DAY")) {
			meteringListDatas = (List<MeteringListData>) 
				searchMeteringDataManager.getMeteringDataDay(
					condition, meterType, supplierId
				);
        }
		return meteringListDatas;
	}

	@Override
	public Map<String, String> getManualMeteringDataTotal(Map<String, String> params,
			String dayType, String meterType, String supplierId) {
		String[] condition = parameterMapToStringArray(params);
		Map<String, String> total = null;
		if(dayType.equals("HOUR")) {
			total = searchMeteringDataManager.getMeteringDataHourTotal(
				condition, meterType, supplierId
			);
        }
		else if(dayType.equals("DAY")) {
			total = searchMeteringDataManager.getMeteringDataDayTotal(
				condition, meterType, supplierId
			);
        }
		return total;
	}

	@Override
	public boolean updateManualMeteringData(
		String mdsId, String dayType, int supplierId, 
		String meteringDate, double meteringValue, Set<String> errSet) {
		try {
			Meter meter = meterMdisDao.get(mdsId);
			if(meter == null) {
				errSet.add("meterInvalid ## meter is invalid [" + mdsId + "]");
				return false;
			}
			if(meter.getIsManualMeter() == null || meter.getIsManualMeter() != 1) {
				errSet.add("meterNotManualMeter ## meter is not manual meter [" + mdsId + "]");
				return false;
			}
			MeteringDataType dt = MeteringDataType.Day;
			if("MONTHLY".equalsIgnoreCase(dayType)) {
				dt = MeteringDataType.Month;
			}
			boolean result = mMDSaver.saveNew(meter.getMdsId(), meteringDate, meteringValue, dt);
			logger.debug("Write ManualMetering Result >> " + result);
			return result;
		} 
		catch (Exception e){	
			logger.error(e);
			errSet.add(e.getMessage());
		}
		return false;
	}

	@Override
	public Map<String, List<Map<String, String>>> getManualMeteringStatisticsByMdsId(String mdsId, String energyType) {
		Meter meter = meterMdisDao.get(mdsId);
		Map<String, List<Map<String, String>>> result = new HashMap<String, List<Map<String, String>>>();
		if(meter == null) {
			return result;
		}
		
		List<Map<String, String>> dayLimitList = this.getDayMonthLimitDataByMeter(meter, energyType);
		List<Map<String, String>> weekList = this.getWeekDataByMeter(meter, energyType);		
		List<Map<String, String>> monthList = this.getMonthDataByMeter(meter, energyType);
		List<Map<String, String>> seasonList = this.getSeasonDataByMeter(meter, energyType);
		
		result.put("dayLimitList", dayLimitList);
		result.put("weekList", weekList);
		result.put("monthList", monthList);
		result.put("seasonList", seasonList);
		
		return result;
	}
	
	/**
	 * 시즌별 검침 및 탄소배출 통계를 구한다.
	 * 
	 * @param meter 미터 도메인
	 * @param energyType 미터타입
	 * @return 통계값을 문자열 페어로 갖는 리스트 객체
	 */
	private List<Map<String, String>> getSeasonDataByMeter(Meter meter, String energyType) {
		Map<String, Object> condition = new HashMap<String, Object>();
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), 0, 1);
		condition.put("startDate", yyyyMM.format(calendar.getTime()));
		calendar.set(calendar.get(Calendar.YEAR), 11, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		condition.put("endDate", yyyyMM.format(calendar.getTime()));
		condition.put("meterId", meter.getId());
		
		List<Object> raw = null;
		String dataPrefix = "";
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		if("EM".equalsIgnoreCase(energyType) || "EnergyMeter".equalsIgnoreCase(energyType)) {
			dataPrefix = "EM";
			raw = monthEMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.SEASONAL);
		}
		else if("GM".equalsIgnoreCase(energyType) || "GasMeter".equalsIgnoreCase(energyType)) {
			dataPrefix = "GM";
			raw = monthGMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.SEASONAL);
		} 
		else if("WM".equalsIgnoreCase(energyType) || "WaterMeter".equalsIgnoreCase(energyType)) {
			dataPrefix = "WM";
			raw = monthWMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.SEASONAL);
		}	
		else if("HM".equalsIgnoreCase(energyType) || "HeatMeter".equalsIgnoreCase(energyType)) {
			dataPrefix = "HM";
			raw = monthHMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.SEASONAL);
		}
		else if("SPM".equalsIgnoreCase(energyType) || "SolarPowerMeter".equalsIgnoreCase(energyType)) {
			dataPrefix = "SPM";
			raw = monthSPMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.MONTHLY);
		}
		if(raw == null) {
			return result;
		}
		
		double emTotalTemp = 0;
		double co2TotalTemp = 0;
		int q = 0;
		
		Map<String, HashMap<String, String>> set = new HashMap<String, HashMap<String, String>>();
		for (int i = 0; i < raw.size(); i++) {
			HashMap<String, Object> temp = (HashMap<String, Object>) raw.get(i);
			String setDate = temp.get("YYYYMM").toString();
			int m1 = Integer.parseInt(setDate.substring(4, 6));
			if (1 == (m1 % 3)) {
				emTotalTemp = 0;
				co2TotalTemp = 0;
			}
			emTotalTemp = 
				emTotalTemp + 
				Double.parseDouble(
					ObjectUtils.defaultIfNull(temp.get(dataPrefix + "_TOTAL"), "0").toString()
				);
			co2TotalTemp = 
				co2TotalTemp + 
				Double.parseDouble(
					ObjectUtils.defaultIfNull(temp.get("CO2_TOTAL"), "0").toString()
				);
			if (0 == (m1 % 3) || (i + 1) == raw.size()) {
				q = m1 / 3;
				if ((0 != (m1 % 3)) && (i + 1) == raw.size()) {
					q = m1 / 3 + 1;
				}
				HashMap<String, String> setTemp = new HashMap<String, String>();
				setTemp.put("MYDATE", q + "Q");
				setTemp.put(dataPrefix + "SUM", String.valueOf(emTotalTemp));
				setTemp.put("CO2SUM", String.valueOf(co2TotalTemp));
				set.put(q + "Q", setTemp);
			}
		}
		for (int k = 1; k < 5; k++) {
			if (!set.containsKey(k + "Q")) {
				HashMap<String, String> setTemp = new HashMap<String, String>();
				setTemp.put("MYDATE", k + "Q");
				setTemp.put(dataPrefix + "SUM", "0");
				setTemp.put("CO2SUM", "0");
				setTemp.put("ORDER", String.valueOf(k));
				set.put(k + "Q", setTemp);
			} 
			result.add(k - 1, (HashMap<String, String>) set.get(k + "Q"));
		}
		return result;
	}

	/**
	 * 월간 검침 및 탄소배출 통계를 구한다.
	 * 
	 * @param meter 미터 도메인
	 * @param energyType 미터타입
	 * @return 통계값을 문자열 페어로 갖는 리스트 객체
	 */
	private List<Map<String, String>> getMonthDataByMeter(Meter meter, String energyType) {
		Map<String, Object> condition = new HashMap<String, Object>();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), 0, 1);
		condition.put("startDate", yyyyMM.format(calendar.getTime()));
		calendar.set(calendar.get(Calendar.YEAR), 11, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		condition.put("endDate", yyyyMM.format(calendar.getTime()));
		condition.put("meterId", meter.getId());
		
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		List<Object> raw = null;
		String dataPrefix = "";
		if("EM".equalsIgnoreCase(energyType) || "EnergyMeter".equalsIgnoreCase(energyType)) {
			dataPrefix = "EM";
			raw = monthEMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.MONTHLY);
		}
		else if("GM".equalsIgnoreCase(energyType) || "GasMeter".equalsIgnoreCase(energyType)) {
			dataPrefix = "GM";
			raw = monthGMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.MONTHLY);
		} 
		else if("WM".equalsIgnoreCase(energyType) || "WaterMeter".equalsIgnoreCase(energyType)) {
			dataPrefix = "WM";
			raw = monthWMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.MONTHLY);
		}	
		else if("HM".equalsIgnoreCase(energyType) || "HeatMeter".equalsIgnoreCase(energyType)) {
			dataPrefix = "HM";
			raw = monthHMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.MONTHLY);
		}
		else if("SPM".equalsIgnoreCase(energyType) || "SolarPowerMeter".equalsIgnoreCase(energyType)) {
			dataPrefix = "SPM";
			raw = monthSPMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.MONTHLY);
		}
		
		if(raw == null) {
			return result;
		}
		HashMap<String, HashMap<String, String>> addMonth = new HashMap<String, HashMap<String, String>>();
		for (int i = 0; i < raw.size(); i++) {
			HashMap<String, String> temp = (HashMap<String, String>) raw.get(i);
			
			Object mDate = temp.get("YYYYMM");
			String setDate = mDate.toString();
			int	m1 = Integer.parseInt(setDate.substring(4, 6));

			HashMap<String, String> setTemp = new HashMap<String, String>();
			setTemp.put("MYDATE", String.valueOf(m1));
			setTemp.put(dataPrefix + "SUM", String.valueOf(temp.get(dataPrefix + "_TOTAL")));
			setTemp.put("CO2SUM", String.valueOf(temp.get("CO2_TOTAL")));
			addMonth.put(m1 + "", setTemp);
		}
		if (12 > raw.size()) {
			for (int k = 0; k < 12; k++) {
				if (!addMonth.containsKey(k + 1 + "")) {
					HashMap<String, String> setTemp = new HashMap<String, String>();
					setTemp.put("MYDATE", String.valueOf((k + 1)));
					setTemp.put(dataPrefix + "SUM", "0");
					setTemp.put("CO2SUM", "0");
					setTemp.put("ORDER", String.valueOf(k + 1));
					result.add(k, setTemp);
				} else {
					HashMap<String, String> setTemp = (HashMap<String, String>) addMonth.get(k + 1 + "");
					setTemp.put("ORDER", String.valueOf(k + 1));
					result.add(k, setTemp);
				}
			}
		}
		return result;
	}

	/**
	 * yyyyMMdd 포맷의 날자 데이터에서 현재 요일을 구한다.
	 * @param yyyymmdd
	 * @return
	 */
	private int getWeekOfDay(String yyyymmdd) {
		try {
			 Calendar cal = Calendar.getInstance();
			 cal.setTime(yyyyMMdd.parse(yyyymmdd));
			 return cal.get(Calendar.DAY_OF_WEEK);
		}
		catch(Exception e) {
			return 0;
		}
	}

	/**
	 * 주간 검침 및 탄소배출 통계를 구한다.
	 * 
	 * @param meter 미터 도메인
	 * @param energyType 미터타입
	 * @return 통계값을 문자열 페어로 갖는 리스트 객체
	 */
	private List<Map<String, String>> getWeekDataByMeter(Meter meter, String energyType) {
		
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		Map<String, Object> condition = new HashMap<String, Object>();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		condition.put("startDate", yyyyMMdd.format(calendar.getTime()));
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		condition.put("endDate", yyyyMMdd.format(calendar.getTime()));
		condition.put("meterId", meter.getId());
		
		String dataPrefix = "";
		List<Object> raw = null;
		if("EM".equalsIgnoreCase(energyType) || "EnergyMeter".equalsIgnoreCase(energyType)) {
			dataPrefix = "EM";
			raw = dayEMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.WEEKLY);
		}
		else if("GM".equalsIgnoreCase(energyType) || "GasMeter".equalsIgnoreCase(energyType)) {
			dataPrefix = "GM";
			raw = dayGMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.WEEKLY);
		} 
		else if("WM".equalsIgnoreCase(energyType) || "WaterMeter".equalsIgnoreCase(energyType)) {
			dataPrefix = "WM";
			raw = dayWMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.WEEKLY);
		}	
		else if("HM".equalsIgnoreCase(energyType) || "HeatMeter".equalsIgnoreCase(energyType)) {
			dataPrefix = "HM";
			raw = dayHMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.WEEKLY);
		}
		else if("SPM".equalsIgnoreCase(energyType) || "SolarPowerMeter".equalsIgnoreCase(energyType)) {
			dataPrefix = "SPM";
			raw = daySPMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.WEEKLY);
		}
		if(raw == null) {
			raw = new ArrayList<Object>();
		}
		
		// 모든 요일을 초기화한다.
		for(int week = 0; week < 7; week++) {
			Map<String, String> setTemp = new HashMap<String, String>();
			setTemp.put("MYDATE", dayOfWeek[week]);
			setTemp.put(dataPrefix + "SUM", "0");
			setTemp.put("CO2SUM", "0");
			setTemp.put("ORDER", String.valueOf(week + 1));
			result.add(setTemp);
		}
		
		// 실제 데이터와 해당 요일을 바꿔 넣는다.
		int size = raw.size();
		int week, index;
		for(int i = 0; i < size; i++) {
			Map<String, Object> temp = (HashMap<String, Object>) raw.get(i);
			week = getWeekOfDay(temp.get("YYYYMMDD").toString());
			index = week - 1;			
			Map<String, String> setTemp = result.remove(index);
			//setTemp.clear();
			setTemp.put("MYDATE", dayOfWeek[index]);
			setTemp.put(dataPrefix + "SUM", String.valueOf(temp.get(dataPrefix + "_TOTAL") + ""));
			setTemp.put("CO2SUM", temp.get("CO2_TOTAL") + "");
			setTemp.put("ORDER", String.valueOf(week));
			result.add(index, setTemp);
		}
		return result;
	}

	/**
	 * 현재일로부터 지정된 일만큼의 전일까지의 사용량 및 탄소배출량을 구한다.
	 * 
	 * 지정된 날짜만큼 뺀 날을 계산하고 그 뒤 데이터를 가져온다.
	 * 계산한 날자가 현재달과 다를 경우 전월부터로 계산한다.
	 * 
	 * 가령 현재가 10일이고 20일전의 날자부터 데이터를 가져온다고 가정하면,
	 * 20일 전부터 그 달의 마지막 일까지 루프,
	 * 그 뒤 현재 달부터 오늘까지 루프를 돌며 get_value_XX 의 메소드를 호출하여 값을 구한다.
	 * 
	 * @param meter
	 * @param energyType
	 *  
	 * @return 결과 맵
	 * 
	 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
	 */
	private List<Map<String, String>> getDayMonthLimitDataByMeter(Meter meter, String energyType) {
		if("EM".equalsIgnoreCase(energyType) || "EnergyMeter".equalsIgnoreCase(energyType)) {
			return getGenericDayMonthLimitDataByMeter(meter, "EM", monthEMDao);
		}
		else if("GM".equalsIgnoreCase(energyType) || "GasMeter".equalsIgnoreCase(energyType)) {
			return getGenericDayMonthLimitDataByMeter(meter, "GM", monthGMDao);
		} 
		else if("WM".equalsIgnoreCase(energyType) || "WaterMeter".equalsIgnoreCase(energyType)) {
			return getGenericDayMonthLimitDataByMeter(meter, "WM", monthWMDao);
		}	
		else if("HM".equalsIgnoreCase(energyType) || "HeatMeter".equalsIgnoreCase(energyType)) {
			return getGenericDayMonthLimitDataByMeter(meter, "HM", monthHMDao);
		}
		else if("SPM".equalsIgnoreCase(energyType) || "SolarPowerMeter".equalsIgnoreCase(energyType)) {
			return getGenericDayMonthLimitDataByMeter(meter, "SPM", monthSPMDao);
		}
		else {
			return new ArrayList<Map<String,String>>();
		}
	}

	/**
	 * 실제 일별통계를 구하는 메서드
	 * T 제네릭스는 MonthXX 형식의 도메인 객체이다.
	 * 
	 * @param meter 미터객체
	 * @param prefix 데이터 키 프리픽스
	 * @param dao 실제 데이터 억세스 객체.
	 * @return 결과 값
	 */
	private <T> List<Map<String, String>> getGenericDayMonthLimitDataByMeter(
		Meter meter, String prefix, GenericDao<T, Integer> dao) {
		
		List<Map<String, String>> result = new ArrayList<Map<String,String>>(); 
		
		Condition usageCondition = new Condition(
			"id.channel",
 			new Object[]{ DefaultChannel.Usage.getCode() }, null,
 			Restriction.EQ
 		);
		
		Condition co2Condition = new Condition(
			"id.channel",
 			new Object[]{ DefaultChannel.Co2.getCode() }, null,
 			Restriction.EQ
 		);
		
		Set<Condition> condition = new LinkedHashSet<Condition>();
		
		boolean checkPrevMonth = false;
		String prevYyyyMMdd = "";		
		int prevYear = 0;
		int prevMonth = 0;		
		int prevStart = 0;
		int prevEnd = 0;
		int currentMonth = 0;
		int currentStart = 0;
		int currentEnd = 0;
		
		Calendar calendar = Calendar.getInstance();
		String endDate = yyyyMM.format(calendar.getTime());
		currentMonth = calendar.get(Calendar.MONTH) + 1;
		currentEnd = calendar.get(Calendar.DATE) + 1;		
		
		calendar.add(Calendar.DATE, -PREV_DATE);
		String startDate = yyyyMM.format(calendar.getTime());
		prevMonth = calendar.get(Calendar.MONTH) + 1;	
		if(currentMonth != prevMonth) {
			checkPrevMonth = true;
			prevStart = calendar.get(Calendar.DATE);
			prevEnd = calendar.getActualMaximum(Calendar.DATE) + 1;
			prevYear = calendar.get(Calendar.YEAR);
			currentStart = 1;
			prevYyyyMMdd = 
				String.valueOf(prevYear) + StringUtils.leftPad(String.valueOf(prevMonth), 2, "0");
		}	
		else {
			checkPrevMonth = false;
			currentStart = calendar.get(Calendar.DATE);
		}
				
		condition.add(
			new Condition(
				"meter.id", 
				new Object[]{meter.getId()}, null, 
				Restriction.EQ
			)
		);
   	 	condition.add(
   	 		new Condition(
   	 			"id.yyyymm", 
   	 			new Object[]{ startDate, endDate }, null, 
   	 			Restriction.BETWEEN)
   	 	);
   	 	condition.add(usageCondition);
   	 	 
   	 	List<T> monthUsage = (List<T>) dao.findByConditions(condition);
		condition.remove(usageCondition);
		condition.add(co2Condition);
		List<T> monthCo2 = (List<T>) dao.findByConditions(condition);
   	 	
		Map<String, String> item = null;
		int ORDER = 1;
		String td = "";
		if(checkPrevMonth) {			
			for(int i = prevStart; i < prevEnd; i++) {
				item = new HashMap<String, String>();
				item.put(prefix + "SUM", "0");
				item.put("CO2SUM", "0");
				for (T u : monthUsage) {
					td = getValueAtBean(u, "yyyymm");
					if(td != null && prevYyyyMMdd.equalsIgnoreCase(td)){
						item.put(prefix + "SUM", getMonthDataByDayString(u, i));
					}
				}
				for (T c : monthCo2) {
					td = getValueAtBean(c, "yyyymm");
					if(td != null && prevYyyyMMdd.equalsIgnoreCase(td)){
						item.put("CO2SUM", getMonthDataByDayString(c, i));
					}
				}
				item.put("MYDATE", String.valueOf(i));
				result.add(item);
				item.put("ORDER", String.valueOf(ORDER++));
			}
		}
		for(int i = currentStart; i < currentEnd; i++) {
			item = new HashMap<String, String>();
			item.put(prefix + "SUM", "0");
			item.put("CO2SUM", "0");
			for (T u : monthUsage) {
				item.put(prefix + "SUM", getMonthDataByDayString(u, i));
			}
			for (T c : monthCo2) {
				item.put("CO2SUM", getMonthDataByDayString(c, i));
			}
			item.put("MYDATE", String.valueOf(i));
			result.add(item);
			item.put("ORDER", String.valueOf(ORDER++));
		}
		return result;
	}
	
	/**
	 * 해당 검침 엔티티에서 value_XX 형식의 메소드를 호출하고 결과를 문자열로 반환한다.
	 * 
	 * @param <T> 검침결과 객체 클래스 제네릭스 타입
	 * 
	 * @param month 검침결과 객체
	 * @param value 호출할 메소드 포스트픽스
	 * @return 호출결과 값
	 */
	private <T> String getMonthDataByDayString(T month, int value) {
		String setTime = StringUtils.leftPad(String.valueOf(value), 2, "0");
		String val = (String)ObjectUtils.defaultIfNull(
			getValueAtBean(month, "value_" + setTime), "0"
		);
        return val;
	}
	
	private Set<Condition> buildParameters(Map<String, Object> parameters, boolean isPaging) {
		Set<Condition> conditions = new LinkedHashSet<Condition>();
		
		String mdsId = (String) parameters.get("meter_id");
		String meterName = (String) parameters.get("friendly_name");
		String searchFrom = (String) parameters.get("search_from");
		Integer isManualMeter = 1;		
		Integer supplierId = (Integer) parameters.get("supplierId");
		Integer first = (Integer) parameters.get("first");
		Integer max = (Integer) parameters.get("max");
		
		conditions.add(new Condition("s.id",new Object[]{supplierId},null,Restriction.EQ));
		conditions.add(new Condition("id.channel",new Object[]{1},null,Restriction.EQ));
		conditions.add(new Condition("meter",new Object[]{"m"},null,Restriction.ALIAS));
		conditions.add(new Condition("supplier",new Object[]{"s"},null,Restriction.ALIAS));
        
		if(mdsId != null) {
			conditions.add(
				new Condition("m.mdsId", new Object[]{mdsId}, null, Restriction.EQ)
			);
		}	
		if(meterName != null) {
			conditions.add(
				new Condition("m.friendlyName", new Object[]{meterName}, null, Restriction.EQ)
			);
		}
		if(isManualMeter != null) {
			conditions.add(
				new Condition("m.isManualMeter", new Object[]{isManualMeter}, null, Restriction.EQ)
			);
		}
		if(searchFrom != null && !searchFrom.isEmpty()){
			if(searchFrom.contains("@")) {
				String [] dates = searchFrom.split("@");
				if(dates.length == 2) {
					String startDate = dates[0];
					String endDate = dates[1];
					conditions.add(
						new Condition(
							"id.yyyymmdd", 
							new Object[]{startDate, endDate}, 
							null, Restriction.BETWEEN
						)
					);
				}
			}
		}
		
		if(isPaging) {
			if(first != null) {
				conditions.add(
					new Condition("", new Object[]{first}, null, Restriction.FIRST)
				);
			}
			else {
				conditions.add(
					new Condition("", new Object[]{0}, null, Restriction.FIRST)
				);
			}
			if(max != null) {
				conditions.add(
					new Condition("", new Object[]{max}, null, Restriction.MAX)
				);
			}
			else {
				conditions.add(
					new Condition("", new Object[]{10}, null, Restriction.MAX)
				);
			}
		}
		
		conditions.add(
			new Condition("id.yyyymmdd", null, null, Restriction.ORDERBYDESC)
		);
		
		return conditions;
	}
	
	@Override
	public List<?> getManualMetering(Map<String, Object> parameters) {
		
		String meterType = (String) parameters.get("meterType");
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		
		logger.debug("meterType["+meterType+"]");		
		Set<Condition> conditions = buildParameters(parameters, true);
		if("EM".equalsIgnoreCase(meterType) || "EnergyMeter".equalsIgnoreCase(meterType)) {
			return dayEMDao.findByConditions(conditions);
		}
		else if("GM".equalsIgnoreCase(meterType) || "GasMeter".equalsIgnoreCase(meterType)) {
			return dayGMDao.findByConditions(conditions);
		}
		else if("WM".equalsIgnoreCase(meterType) || "WaterMeter".equalsIgnoreCase(meterType)) {
			return dayWMDao.findByConditions(conditions);
		}
		else if("HM".equalsIgnoreCase(meterType) || "HeatMeter".equalsIgnoreCase(meterType)) {
			return dayHMDao.findByConditions(conditions);
		}
		else if("SPM".equalsIgnoreCase(meterType) || "SolarPowerMeter".equalsIgnoreCase(meterType)) {
			return daySPMDao.findByConditions(conditions);
		}			
		return result;
	}

	@Override
	public Long getManualMeteringTotal(Map<String, Object> parameters) {
		String meterType = (String) parameters.get("meterType");
		Set<Condition> conditions = buildParameters(parameters, false);
		List<Object> r = null;
		GenericDao<?, Integer> dao = null;
		if("EM".equalsIgnoreCase(meterType) || "EnergyMeter".equalsIgnoreCase(meterType)) {
			dao = dayEMDao;
		}
		else if("GM".equalsIgnoreCase(meterType) || "GasMeter".equalsIgnoreCase(meterType)) {
			dao = dayGMDao;
		}
		else if("WM".equalsIgnoreCase(meterType) || "WaterMeter".equalsIgnoreCase(meterType)) {
			dao = dayWMDao;
		}
		else if("HM".equalsIgnoreCase(meterType) || "HeatMeter".equalsIgnoreCase(meterType)) {
			dao = dayHMDao;
		}
		else if("SPM".equalsIgnoreCase(meterType) || "SolarPowerMeter".equalsIgnoreCase(meterType)) {
			dao = daySPMDao;			
		}
		r = dao.findTotalCountByConditions(conditions);
		if(r != null && r.size() > 0) {
			Object o = r.get(0);
			if(o != null) {
				return (Long) o;
			}
		}		
		return 0L;
	}
	
	/**
	 * 자바빈 규약을 가진 객체 안의 프로퍼티를 Reflection (apache commons beanutil) 을 통해 얻는다.
	 * 
	 * @param bean 객체
	 * @param prop 프로퍼티 이름
	 * @return 프로퍼티 값
	 */
	private <T> String getValueAtBean(T bean, String prop) {
		String val = null;
        try {
        	val = BeanUtils.getProperty(bean, prop);
		} 
        catch (Exception ignore) {}
        return val;
	}
}
