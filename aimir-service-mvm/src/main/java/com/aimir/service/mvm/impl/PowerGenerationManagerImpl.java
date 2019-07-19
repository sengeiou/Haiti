package com.aimir.service.mvm.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.WeekDay;
import com.aimir.dao.device.MeterMdisDao;
import com.aimir.dao.device.SolarPowerMeterDao;
import com.aimir.dao.device.impl.SolarPowerMeterDaoImpl;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DaySPMDao;
import com.aimir.dao.mvm.LpSPMDao;
import com.aimir.dao.mvm.MonthSPMDao;
import com.aimir.dao.mvm.impl.DaySPMDaoImpl;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.MonthSPM;
import com.aimir.service.mvm.PowerGenerationManager;
import com.aimir.util.Condition;
import com.aimir.util.TimeUtil;
import com.aimir.util.Condition.Restriction;

/**
 * 
 * 발전 관련 매니저
 * 
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 *
 */
@Service(value="PowerGenerationManager")
@Transactional(readOnly=false)
public class PowerGenerationManagerImpl implements PowerGenerationManager {

	Logger logger = Logger.getLogger(PowerGenerationManagerImpl.class);
	
	// 일별 날자 기한. 이 숫자만큼 오늘로부터 전일까지의 통계를 구한다.
	private static int PREV_DATE = 19;
	
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
	
	@Autowired LpSPMDao lpSPMDao;
	@Autowired DaySPMDao daySPMDao;
	@Autowired MonthSPMDao monthSPMDao;
	
	@Autowired DayEMDao dayEMDao;
	
	@Autowired MeterMdisDao meterMdisDao;
	@Autowired SolarPowerMeterDao solarPowerMeterDao;

	@Override
	public Map<String, Double> getGenerationInfo(MeterType meterType, String now) {
		
		Set<Condition> conditions = new HashSet<Condition>();
		String currentHourKey = "H";
		try {
			if(now == null) {
				now = TimeUtil.getCurrentTime();
			}
			currentHourKey = currentHourKey + Integer.parseInt(now.substring(8, 10));
		}
		catch (Exception e) {
			now = "19700101";
		}
		
		conditions.add(
			new Condition("id.yyyymmdd", new Object[]{ now.substring(0, 8) }, null, Restriction.EQ)
		);
		
		Map<String, Double> result = daySPMDao.getSumUsageByCondition(conditions);
		
		for (String k : result.keySet()) {
			Double d = (result.get(k) != null) ? result.get(k) : 0.0;
			result.put(k, Double.parseDouble(String.format("%.4f", d)));
		}		
		Double total = daySPMDao.getSumTotalUsageByCondition(conditions);
		
		result.put("total", total);
		result.put("current", result.get(currentHourKey));
		
		return result;
	}
	
	public List<Map<String, Object>> getGenerationValueAmountByMeters(
			MeterType meterType, Map<String, Object> condition) {
		Set<Condition> conditions = new HashSet<Condition>();
		
		if(condition.containsKey("today")) {
			String today = (condition.get("today") == null) ? "" : condition.get("today").toString();
			if(today == null || today.isEmpty()) {
				try {
					today = TimeUtil.getCurrentDay();
				}
				catch (ParseException cannot) {
					today = "19700101";
				}
			}
			conditions.add(
				new Condition("id.yyyymmdd", new Object[]{ today }, null, Restriction.EQ)
			);
		}
		conditions.add(
			new Condition("id.channel", new Object[]{ 1 }, null, Restriction.EQ)
		);
		
		// meter property로 Meter에 INNER JOIN을 건다.
		// 이때 Alias 는 meter이며 join 타입은 INNER JOIN
		conditions.add(new Condition("meter", new Object[]{"meter"}, null, Restriction.INNER_JOIN));
		
		List<Projection> fields = new LinkedList<Projection>();
		fields.add(Projections.sum("total").as("total")); 
		fields.add(Projections.groupProperty("meter.mdsId").as("mdsId"));		
		
		List<Map<String, Object>> result = 
			((SolarPowerMeterDaoImpl)solarPowerMeterDao).findByConditionsAndProjections(conditions, fields);
		
		return result;
	}
	
	@Override
	public List<Map<String, Object>> getGenerationValueAmountByMeter(
		MeterType c, Map<String, Object> condition) {
		
		Set<Condition> conditions = new HashSet<Condition>();
		
		if(condition.containsKey("today")) {
			String today = (condition.get("today") == null) ? "" : condition.get("today").toString();
			if(today == null || today.isEmpty()) {
				try {
					today = TimeUtil.getCurrentDay();
				}
				catch (ParseException cannot) {
					today = "19700101";
				}
			}
			conditions.add(
				new Condition("id.yyyymmdd", new Object[]{ today }, null, Restriction.EQ)
			);
		}
		conditions.add(
			new Condition("id.channel", new Object[]{ 1 }, null, Restriction.EQ)
		);
		
		
		conditions.add(new Condition("meter", new Object[]{"meter"}, null, Restriction.INNER_JOIN));
		
		conditions.add(
				new Condition("total", new Object[]{}, null, Restriction.ORDERBYDESC)
			);
		
		List<Projection> fields = new LinkedList<Projection>();
		fields.add(Projections.sum("total").as("total")); 
		fields.add(Projections.groupProperty("meter.mdsId").as("mdsId"));		
		
		List<Map<String, Object>> result = 
			((DaySPMDaoImpl)daySPMDao).findByConditionsAndProjections(conditions, fields);

		
		return result.subList(0, 10);
	}
	
	@Override
	public Map<String, List<Map<String, String>>> getStatistics(MeterType meterType, Map<String, Object> condition) {
		Map<String, List<Map<String, String>>> result = new HashMap<String, List<Map<String, String>>>();
	
		Meter meter = null;
		if(condition != null && condition.containsKey("meterId")) {
			Integer meterId = (Integer) condition.get("meterId");
			if(meterId != null) {
				meter = solarPowerMeterDao.get(meterId);
			}
		}
		
		List<Map<String, String>> dayList = new ArrayList<Map<String,String>>();
		List<Map<String, String>> weekList = new ArrayList<Map<String,String>>();	
		List<Map<String, String>> monthList = new ArrayList<Map<String,String>>();
		List<Map<String, String>> seasonList = new ArrayList<Map<String,String>>();
		
		//if(meter != null) {
			dayList = this.getDayStatistics(meter, condition);
			weekList = this.getWeekStatistics(meter, condition);		
			monthList = this.getMonthStatistics(meter, condition);
			seasonList = this.getSeasonStatistics(meter, condition);
		//}
		
		result.put("dayList", dayList);
		result.put("weekList", weekList);
		result.put("monthList", monthList);
		result.put("seasonList", seasonList);
		
		return result;
	}

	
	@SuppressWarnings("unchecked")
	private List<Map<String, String>> getSeasonStatistics(Meter meter, Map<String, Object> condition) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), 0, 1);
		condition.put("startDate", yyyyMM.format(calendar.getTime()));
		calendar.set(calendar.get(Calendar.YEAR), 11, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		condition.put("endDate", yyyyMM.format(calendar.getTime()));
		if(meter != null) {
			condition.put("meterId", meter.getId());
		}
		
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		List<Object> raw = monthSPMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.MONTHLY);;
		String dataPrefix = "SPM";
		
		double emTotalTemp = 0;
		int q = 0;
		
		Map<String, HashMap<String, String>> set = new HashMap<String, HashMap<String, String>>();
		for (int i = 0; i < raw.size(); i++) {
			HashMap<String, Object> temp = (HashMap<String, Object>) raw.get(i);
			String setDate = temp.get("YYYYMM").toString();
			int m1 = Integer.parseInt(setDate.substring(4, 6));
			if (1 == (m1 % 3)) {
				emTotalTemp = 0;
			}
			emTotalTemp = 
				emTotalTemp + 
				Double.parseDouble(
					ObjectUtils.defaultIfNull(temp.get(dataPrefix + "_TOTAL"), "0").toString()
				);
			if (0 == (m1 % 3) || (i + 1) == raw.size()) {
				q = m1 / 3;
				if ((0 != (m1 % 3)) && (i + 1) == raw.size()) {
					q = m1 / 3 + 1;
				}
				HashMap<String, String> setTemp = new HashMap<String, String>();
				setTemp.put("MYDATE", q + "Q");
				setTemp.put(dataPrefix + "SUM", String.valueOf(emTotalTemp));
				set.put(q + "Q", setTemp);
			}
		}
		for (int k = 1; k < 5; k++) {
			if (!set.containsKey(k + "Q")) {
				HashMap<String, String> setTemp = new HashMap<String, String>();
				setTemp.put("MYDATE", k + "Q");
				setTemp.put(dataPrefix + "SUM", "0");
				setTemp.put("ORDER", String.valueOf(k));
				set.put(k + "Q", setTemp);
			} 
			result.add(k - 1, (HashMap<String, String>) set.get(k + "Q"));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, String>> getMonthStatistics(Meter meter, Map<String, Object> condition) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), 0, 1);
		condition.put("startDate", yyyyMM.format(calendar.getTime()));
		calendar.set(calendar.get(Calendar.YEAR), 11, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		condition.put("endDate", yyyyMM.format(calendar.getTime()));
		if(meter != null) {
			condition.put("meterId", meter.getId());
		}
		
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		List<Object> raw = null;
		String dataPrefix = "SPM";
		raw = monthSPMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.MONTHLY);
		
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
			addMonth.put(m1 + "", setTemp);
		}
		if (12 > raw.size()) {
			for (int k = 0; k < 12; k++) {
				if (!addMonth.containsKey(k + 1 + "")) {
					HashMap<String, String> setTemp = new HashMap<String, String>();
					setTemp.put("MYDATE", String.valueOf((k + 1)));
					setTemp.put(dataPrefix + "SUM", "0");
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

	@SuppressWarnings("unchecked")
	private List<Map<String, String>> getWeekStatistics(Meter meter, Map<String, Object> condition) {
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		condition.put("startDate", yyyyMMdd.format(calendar.getTime()));
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		condition.put("endDate", yyyyMMdd.format(calendar.getTime()));
		if(meter != null) {
			condition.put("meterId", meter.getId());
		}
		
		String dataPrefix = "SPM";
		List<Object> raw = daySPMDao.getConsumptionEmCo2ManualMonitoring(condition, DateType.WEEKLY);
		if(raw == null) {
			raw = new ArrayList<Object>();
		}
		
		// 모든 요일을 초기화한다.
		for(int week = 0; week < 7; week++) {
			Map<String, String> setTemp = new HashMap<String, String>();
			setTemp.put("MYDATE", dayOfWeek[week]);
			setTemp.put(dataPrefix + "SUM", "0");
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
			setTemp.put("ORDER", String.valueOf(week));
			result.add(index, setTemp);
		}
		return result;
	}

	private List<Map<String, String>> getDayStatistics(Meter meter, Map<String, Object> condition) {
		
		String prefix = "SPM";
		List<Map<String, String>> result = new ArrayList<Map<String,String>>(); 
		Set<Condition> restrict = new HashSet<Condition>();
		
		Integer prev = PREV_DATE;
		if(condition.containsKey("dayLimit")){
			prev = (Integer) condition.get("dayLimit");
		}	
		
		Condition usageCondition = new Condition(
			"id.channel",
 			new Object[]{ DefaultChannel.Usage.getCode() }, null,
 			Restriction.EQ
 		);
		
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
		
		calendar.add(Calendar.DATE, -prev);
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
		
		if(meter != null) {
			restrict.add(
				new Condition(
					"meter.id", 
					new Object[]{meter.getId()}, null, 
					Restriction.EQ
				)
			);
		}
		restrict.add(
   	 		new Condition(
   	 			"id.yyyymm", 
   	 			new Object[]{ startDate, endDate }, null, 
   	 			Restriction.BETWEEN)
   	 	);
		restrict.add(usageCondition);
   	 	 
   	 	List<MonthSPM> monthUsage = monthSPMDao.findByConditions(restrict);
   	 	
		Map<String, String> item = null;
		int ORDER = 1;
		String td = "";
		if(checkPrevMonth) {
			Double presum = 0.0;
			for(int i = prevStart; i < prevEnd; i++) {
				item = new HashMap<String, String>();
				item.put(prefix + "SUM", "0");
				for (MonthSPM u : monthUsage) {
					td = getValueAtBean(u, "yyyymm");
					if(td != null && prevYyyyMMdd.equalsIgnoreCase(td)){
						presum += getMonthDataByDayString(u, i);
						item.put(prefix + "SUM", String.valueOf(presum));
					}
				}
				item.put("MYDATE", String.valueOf(i));
				result.add(item);
				item.put("ORDER", String.valueOf(ORDER++));
			}
		}
		Double cursum = 0.0;
		for(int i = currentStart; i < currentEnd; i++) {
			item = new HashMap<String, String>();
			item.put(prefix + "SUM", "0");
			for (MonthSPM u : monthUsage) {
				cursum += getMonthDataByDayString(u, i);
				item.put(prefix + "SUM", String.valueOf(cursum));
			}
			item.put("MYDATE", String.valueOf(i));
			result.add(item);
			item.put("ORDER", String.valueOf(ORDER++));
		}
		return result;
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
	
	/**
	 * 해당 검침 엔티티에서 value_XX 형식의 메소드를 호출하고 결과를 문자열로 반환한다.
	 * 
	 * @param <T> 검침결과 객체 클래스 제네릭스 타입
	 * 
	 * @param month 검침결과 객체
	 * @param value 호출할 메소드 포스트픽스
	 * @return 호출결과 값
	 */
	private <T> Double getMonthDataByDayString(T month, int value) {
		String setTime = StringUtils.leftPad(String.valueOf(value), 2, "0");
		String val = (String)ObjectUtils.defaultIfNull(
			getValueAtBean(month, "value_" + setTime), "0"
		);
        return Double.parseDouble(val);
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
}