package com.aimir.service.mvm.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.DefaultChannel;
import com.aimir.constants.CommonConstants.KGOE;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.mvm.DayGMDao;
import com.aimir.dao.mvm.DayWMDao;
import com.aimir.dao.mvm.MeteringDayDao;
import com.aimir.dao.mvm.MeteringMonthDao;
import com.aimir.dao.mvm.MonthEMDao;
import com.aimir.dao.mvm.MonthGMDao;
import com.aimir.dao.mvm.MonthWMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.ZoneDao;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.Zone;
import com.aimir.service.mvm.FacilityUsageMonitoringManager;
import com.aimir.util.BemsStatisticUtil;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@Service(value="facilityUsageMonitoringManager")
public class FacilityUsageMonitoringManagerImpl implements FacilityUsageMonitoringManager{	
	Log logger = LogFactory.getLog(getClass());
	
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
	SupplierDao supplierDao;
	
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> getFacilityUsageByPeriod(Map<String,Object> params){
		logger.info(params);
		String codeId = StringUtil.nullToBlank(params.get("codeId"));
		String periodType = (String)params.get("periodType");
		
		String lastUsageYn = StringUtil.nullToBlank(params.get("lastUsageYn"));
		String searchDate = StringUtil.nullToBlank(params.get("searchDate"));
		
		// 현재일자 yyyyMMdd
		String today 		= searchDate == "" ? CalendarUtil.getCurrentDate() : searchDate;
		String yesterday 	= CalendarUtil.getDateWithoutFormat(today, Calendar.DAY_OF_MONTH, -1);
		
		String currYear 	= today.substring(0, 4);
		String currMonth	= today.substring(4, 6);
		String currDate		= today.substring(6, 8);
		
		String lastYear		= Integer.toString(Integer.parseInt(currYear) - 1);
		
		
		Calendar c = Calendar.getInstance();
		c.setFirstDayOfWeek(Calendar.SUNDAY);

		if ( "" != searchDate ) {
			c.set(Integer.parseInt(currYear), 
					Integer.parseInt(currMonth) - 1, 
					Integer.parseInt(currDate));
			c.add(Calendar.DATE, -(c.get(Calendar.DAY_OF_WEEK))+1);
		}
		SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMdd");
//		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		String currWeekStartDate = formatter1.format(c.getTime());
//		c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY); 
		c.add(Calendar.DATE, 6);
		String currWeekEndDate = formatter1.format(c.getTime());

//		Map<String,String> currWeekDate = CalendarUtil.getDateWeekOfMonth(currYear, currMonth, Integer.toString(CalendarUtil.getWeekOfMonth(today)));
//		String currWeekStartDate = currWeekDate.get("startDate");
//		String currWeekEndDate = CalendarUtil.getDateWithoutFormat(currWeekStartDate, Calendar.DAY_OF_MONTH, 6);
		
		String lastWeekStartDate = CalendarUtil.getDateWithoutFormat(currWeekStartDate, Calendar.DAY_OF_MONTH, -7);
		String lastWeekEndDate = CalendarUtil.getDateWithoutFormat(currWeekStartDate, Calendar.DAY_OF_MONTH, -1);
		
		Map<String,Object> paramMap = new HashMap<String,Object>();
		// 전일,전주,전년도 월,전년도 분기 조회일경우
		// 조회기준일을 각각 전일,주,년,월로 설정한다.
		if(CommonConstants.YesNo.Yes.getCode().equals(lastUsageYn)){
			paramMap.put("today", yesterday);
			paramMap.put("currYear", lastYear);
			paramMap.put("currMonth", currMonth);
			paramMap.put("lastYear", lastYear);
			paramMap.put("currWeekStartDate", lastWeekStartDate);
			paramMap.put("currWeekEndDate", lastWeekEndDate);
			paramMap.put("periodType", periodType);
		}else{
			paramMap.put("today", today);
			paramMap.put("currYear", currYear);
			paramMap.put("currMonth", currMonth);
			paramMap.put("currWeekStartDate", currWeekStartDate);
			paramMap.put("currWeekEndDate", currWeekEndDate);
			paramMap.put("periodType", periodType);
		}
		
		// 리턴 객체
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		try{
		List<Object> periodUsageList = new ArrayList<Object>();
		List<Object> facilityUsageList = new ArrayList<Object>();
		Map<String,Object> totalUsageMap = new HashMap<String,Object>();
		
		
		// 최상위 로케이션일경우 EndDevice 최상위 분류목록을 조회하고
		// 특정 EndDevice 분류코드가 있을경우 하위 분류목록을 조회한다.
		List<Code> endDeviceDstcdList = null;
		if("".equals(codeId)){
			endDeviceDstcdList = codeDao.getChildCodes("1.9.1");
		}else{
			endDeviceDstcdList = codeDao.getChildren(Integer.parseInt(codeId));
		}
		
		//최하위 EndDevice 분류여부
		boolean leafCodeYn = false;
		if(endDeviceDstcdList==null||endDeviceDstcdList.size()<1){
			
			Code tmp = new Code();
			tmp.setId(Integer.parseInt(codeId));
			
			endDeviceDstcdList = new ArrayList<Code>();
			endDeviceDstcdList.add(tmp);
			
			leafCodeYn = true;
		}
		
		List<Object> tmpList = null;
		Map<String,Object> currMap = null; 
		Map<String,Object> totalMap = null;
		Map<String,Object> facilityUsageMap = null;
		BigDecimal totalUsage = new BigDecimal(0);
		BigDecimal totalToe = new BigDecimal(0);
		BigDecimal co2totalUsage = new BigDecimal(0);
		
		boolean isZero = true;	// facilityUsageList 데이터가 모두 0이면, PieChart가 표현이 안되는 경우를 제어하기 위한 변수
		for(Code code:endDeviceDstcdList){
			tmpList = new ArrayList<Object>();
			//paramMap.put("codeId", code.getId());
			// 입력된 EndDevice 분류의 최하위 코드ID 목록조회
			List<Integer> categories = codeDao.getLeafCode(code.getId());
			// 최하위 EndDevice 분류에 해당하는 EndDevice 목록조회
			List<EndDevice> endDeviceList = endDeviceDao.getEndDevicesByCategories(categories);
			
			tmpList = getPeriodUsage(paramMap,endDeviceList);
			
			BigDecimal emGmWmHmTotal = new BigDecimal(0);
			BigDecimal emTotal = new BigDecimal(0);
			BigDecimal gmTotal = new BigDecimal(0);
			BigDecimal wmTotal = new BigDecimal(0);
			BigDecimal hmTotal = new BigDecimal(0);
			
			BigDecimal co2Total = new BigDecimal(0);
			if(periodUsageList.size()<1){
				periodUsageList = tmpList;
				
				for(Object tmpObj:tmpList){
					currMap = new HashMap<String,Object>();
					currMap = (Map<String,Object>)tmpObj;
					
					BigDecimal currEmUsage = new BigDecimal(currMap.get("EmUsage") == null ? 0 :
						 DecimalUtil.ConvertNumberToDouble(currMap.get("EmUsage")));
					BigDecimal currGmUsage = new BigDecimal(currMap.get("GmUsage") == null ? 0 :
						 DecimalUtil.ConvertNumberToDouble(currMap.get("GmUsage")));
					BigDecimal currWmUsage = new BigDecimal(currMap.get("WmUsage") == null ? 0 :
						 DecimalUtil.ConvertNumberToDouble(currMap.get("WmUsage")));
					BigDecimal currHmUsage = new BigDecimal(currMap.get("HmUsage") == null ? 0 :
						 DecimalUtil.ConvertNumberToDouble(currMap.get("HmUsage")));
					
					emGmWmHmTotal = emGmWmHmTotal.add(currEmUsage).add(currGmUsage).add(currWmUsage).add(currHmUsage);
					emTotal = emTotal.add(currEmUsage);
					gmTotal = gmTotal.add(currGmUsage);
					wmTotal = wmTotal.add(currWmUsage);
					hmTotal = hmTotal.add(currHmUsage);
					
					co2Total = co2Total.add((BigDecimal)currMap.get("co2Usage"));
				}
				
			}else{
				
				int i=0;
				for(Object tmpObj:tmpList){
					currMap = new HashMap<String,Object>();
					currMap = (Map<String,Object>)tmpObj;
					
					totalMap = new HashMap<String,Object>();
					totalMap = (Map<String,Object>)periodUsageList.get(i);
					
					BigDecimal prevEmUsage = new BigDecimal(totalMap.get("EmUsage") == null ? 0 :
						 DecimalUtil.ConvertNumberToDouble(totalMap.get("EmUsage")));
					BigDecimal currEmUsage =  new BigDecimal(currMap.get("EmUsage") == null ? 0 :
						 DecimalUtil.ConvertNumberToDouble(currMap.get("EmUsage")));
					BigDecimal prevGmUsage =  new BigDecimal(totalMap.get("GmUsage") == null ? 0 :
						 DecimalUtil.ConvertNumberToDouble(totalMap.get("GmUsage")));
					BigDecimal currGmUsage =  new BigDecimal(currMap.get("GmUsage") == null ? 0 :
						 DecimalUtil.ConvertNumberToDouble(currMap.get("GmUsage")));
					BigDecimal prevWmUsage =  new BigDecimal(totalMap.get("WmUsage") == null ? 0 :
						 DecimalUtil.ConvertNumberToDouble(totalMap.get("WmUsage")));
					BigDecimal currWmUsage =  new BigDecimal(currMap.get("WmUsage") == null ? 0 :
						 DecimalUtil.ConvertNumberToDouble(currMap.get("WmUsage")));
					BigDecimal prevHmUsage =  new BigDecimal(totalMap.get("HmUsage") == null ? 0 :
						 DecimalUtil.ConvertNumberToDouble(totalMap.get("HmUsage")));
					BigDecimal currHmUsage =  new BigDecimal(currMap.get("HmUsage") == null ? 0 :
						 DecimalUtil.ConvertNumberToDouble(currMap.get("HmUsage")));
					BigDecimal prevco2Usage = new BigDecimal(totalMap.get("co2Usage") == null ? 0 :
						 DecimalUtil.ConvertNumberToDouble(totalMap.get("co2Usage")));
					BigDecimal currco2Usage =  new BigDecimal(currMap.get("co2Usage") == null ? 0 :
						 DecimalUtil.ConvertNumberToDouble(currMap.get("co2Usage")));
					
					// 기간별 사용량 데이터
					totalMap.put("EmUsage", prevEmUsage.add(currEmUsage));
					totalMap.put("GmUsage", prevGmUsage.add(currGmUsage));
					totalMap.put("WmUsage", prevWmUsage.add(currWmUsage));
					totalMap.put("HmUsage", prevHmUsage.add(currHmUsage));
					totalMap.put("co2Usage", prevco2Usage.add(currco2Usage));
					
					emGmWmHmTotal = emGmWmHmTotal.add(currEmUsage).add(currGmUsage).add(currWmUsage).add(currHmUsage);
					emTotal = emTotal.add(currEmUsage);
					gmTotal = gmTotal.add(currGmUsage);
					wmTotal = wmTotal.add(currWmUsage);
					hmTotal = hmTotal.add(currHmUsage);
					co2Total = co2Total.add((BigDecimal)currMap.get("co2Usage"));
					
					i++;
				}
			}
			

			BigDecimal emgmwmhmToe = emTotal.multiply(new BigDecimal(KGOE.Energy.getValue()))
					.add(gmTotal.multiply(new BigDecimal(KGOE.GasLng.getValue())))
					.add(wmTotal.multiply(new BigDecimal(KGOE.Water.getValue())))
					.add(hmTotal.multiply(new BigDecimal(KGOE.Heat.getValue())));
			
			// 설비분류별 사용량 데이터
			facilityUsageMap = new HashMap<String,Object>();
			facilityUsageMap.put("name", code.getName());
			facilityUsageMap.put("value", emgmwmhmToe.setScale(3,BigDecimal.ROUND_DOWN));
			facilityUsageMap.put("toe", emgmwmhmToe.setScale(3,BigDecimal.ROUND_DOWN));
			facilityUsageList.add(facilityUsageMap);
			
			// 총사용량 데이터
			totalUsage = totalUsage.add(emGmWmHmTotal);
			totalToe  = totalToe.add(emgmwmhmToe);
			co2totalUsage = co2totalUsage.add(co2Total);
			
			BigDecimal tempZero = new BigDecimal("0.00");				
			if(emGmWmHmTotal.compareTo(tempZero) != 0) {
				isZero = false;
			}
		}
		
		
		if(leafCodeYn){
		
			facilityUsageList.clear();
			
//			facilityUsageMap = new HashMap<String,Object>();
//			facilityUsageMap.put("name", "");
//			facilityUsageMap.put("value", 1);
//			facilityUsageMap.put("toe", 1);
//			facilityUsageList.add(facilityUsageMap);
		}
		

		if(isZero) {
			facilityUsageList.clear();
			
//			facilityUsageMap = new HashMap<String,Object>();
//			facilityUsageMap.put("name", "");
//			facilityUsageMap.put("value", 1);
//			facilityUsageMap.put("toe", 1);
//			facilityUsageList.add(facilityUsageMap);
		}
	
		// 요금 계산 && TOE 환산
		BigDecimal emTotal = new BigDecimal(0);
		BigDecimal gmTotal = new BigDecimal(0);
		BigDecimal wmTotal = new BigDecimal(0);
		BigDecimal hmTotal = new BigDecimal(0);
		
		for(Object tmpObj:periodUsageList){
			currMap = (Map<String,Object>)tmpObj;
			
			BigDecimal currEmUsage = new BigDecimal(currMap.get("EmUsage") == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currMap.get("EmUsage")));
			BigDecimal currGmUsage = new BigDecimal(currMap.get("GmUsage") == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currMap.get("GmUsage")));
			BigDecimal currWmUsage = new BigDecimal(currMap.get("WmUsage") == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currMap.get("WmUsage")));
			BigDecimal currHmUsage = new BigDecimal(currMap.get("HmUsage") == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currMap.get("HmUsage")));
			
			currMap.put("EmToe", new BigDecimal(currEmUsage == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currEmUsage)* KGOE.Energy.getValue()));
			currMap.put("GmToe", new BigDecimal(currGmUsage == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currGmUsage)* KGOE.GasLng.getValue()));
			currMap.put("WmToe", new BigDecimal(currWmUsage == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currWmUsage)* KGOE.Water.getValue()));
			currMap.put("HmToe", new BigDecimal(currHmUsage == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currHmUsage)* KGOE.Heat.getValue()));

			emTotal = emTotal.add(currEmUsage);
			gmTotal = gmTotal.add(currGmUsage);
			wmTotal = wmTotal.add(currWmUsage);
			hmTotal = hmTotal.add(currHmUsage);
		}
		
		Map<String,Object> emParams = new HashMap<String,Object>();
		Map<String,Object> gmParams = new HashMap<String,Object>();
		Map<String,Object> wmParams = new HashMap<String,Object>();
		Map<String,Object> hmParams = new HashMap<String,Object>();
		
		String dateType=DateType.MONTHLY.getCode();
		Integer period=0;
		if(periodType.equals(DateType.DAILY.getCode())){
			dateType = DateType.DAILY.getCode();
			period = 1;
		}else if(periodType.equals(DateType.WEEKLY.getCode())){
			dateType = DateType.DAILY.getCode();
			period = 7;
		}else if(periodType.equals(DateType.MONTHLY.getCode())){
			dateType = DateType.MONTHLY.getCode();
			period = 12;
		}else if(periodType.equals(DateType.QUARTERLY.getCode())){
			dateType = DateType.MONTHLY.getCode();
			period = 12;
		}
		
		emParams.put("serviceType",MeterType.EnergyMeter.getServiceType());
		emParams.put("dateType",dateType);
		emParams.put("usage",emTotal.doubleValue());
		emParams.put("period",period);
		
		gmParams.put("serviceType",MeterType.GasMeter.getServiceType());
		gmParams.put("dateType",dateType);
		gmParams.put("usage",gmTotal.doubleValue());
		gmParams.put("period",period);
		
		wmParams.put("serviceType",MeterType.WaterMeter.getServiceType());
		wmParams.put("dateType",dateType);
		wmParams.put("usage",wmTotal.doubleValue());
		wmParams.put("period",period);
		
		hmParams.put("serviceType",MeterType.HeatMeter.getServiceType());
		hmParams.put("dateType",dateType);
		hmParams.put("usage",hmTotal.doubleValue());
		hmParams.put("period",period);
		
		Double emCharge = 0.0;
		Double gmCharge = 0.0;
		Double wmCharge = 0.0;
		Double hmCharge = 0.0;
		Double totalCharge = 0.0;
		
		emCharge = new BemsStatisticUtil().getUsageCharge(emParams);
		gmCharge = new BemsStatisticUtil().getUsageCharge(gmParams);
		wmCharge = new BemsStatisticUtil().getUsageCharge(wmParams);
		hmCharge = new BemsStatisticUtil().getUsageCharge(hmParams);
		
		totalCharge = totalCharge + Math.round(emCharge + gmCharge + wmCharge+ hmCharge);
		
		
		
		// 현재시간,요일,월,분기까지의 검침데이터중에서
		// 최소,최대검침값을 가지는 구간을 설정한다.
		String min = "";
		String max = "";
		BigDecimal minVal = new BigDecimal(0);
		BigDecimal maxVal = new BigDecimal(0);
		BigDecimal minToe = new BigDecimal(0);
		BigDecimal maxToe = new BigDecimal(0);
		
		int i=0;
		for(Object tmpObj:periodUsageList){
			currMap = new HashMap<String,Object>();
			currMap = (Map<String,Object>)tmpObj;
			
			BigDecimal currEmUsage = new BigDecimal(currMap.get("EmUsage") == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currMap.get("EmUsage")));
			BigDecimal currGmUsage = new BigDecimal(currMap.get("GmUsage") == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currMap.get("GmUsage")));
			BigDecimal currWmUsage = new BigDecimal(currMap.get("WmUsage") == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currMap.get("WmUsage")));
			BigDecimal currHmUsage = new BigDecimal(currMap.get("HmUsage") == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currMap.get("HmUsage")));
			BigDecimal currEmToe = new BigDecimal(currMap.get("EmToe") == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currMap.get("EmToe")));
			BigDecimal currGmToe =  new BigDecimal(currMap.get("GmToe") == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currMap.get("GmToe")));
			BigDecimal currWmToe =  new BigDecimal(currMap.get("WmToe") == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currMap.get("WmToe")));
			BigDecimal currHmToe =  new BigDecimal(currMap.get("HmToe") == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currMap.get("HmToe")));
			
			BigDecimal emGmWmHmTotal = new BigDecimal(0);
			emGmWmHmTotal = emGmWmHmTotal.add(currEmUsage).add(currGmUsage).add(currWmUsage).add(currHmUsage);
			
			BigDecimal emGmWmHmToe = new BigDecimal(0);
			emGmWmHmToe = emGmWmHmToe.add(currEmToe).add(currGmToe).add(currWmToe).add(currHmToe);
			
			if(i==0){
				min = (String)currMap.get("xField");
				max = (String)currMap.get("xField");
				minVal = emGmWmHmTotal;
				maxVal = emGmWmHmTotal;
				minToe = emGmWmHmToe;
				maxToe = emGmWmHmToe;
			}
			if(emGmWmHmTotal.compareTo(minVal)<0){
				min = (String)currMap.get("xField");
				minVal = emGmWmHmTotal;
				minToe = emGmWmHmToe;
			}
			if(emGmWmHmTotal.compareTo(maxVal)>0){
				max = (String)currMap.get("xField");
				maxVal = emGmWmHmTotal;
				maxToe = emGmWmHmToe;
			}
			
			if(CommonConstants.YesNo.Yes.getCode().equals(currMap.get("isCurrent"))){
				break;
			}
			
			i++;
		}
		
		String currDateTime = formatter.format(new Date());
		resultMap.put("currentDateTime", currDateTime);
		
		totalUsageMap.put("totalUsage", totalUsage.setScale(3,BigDecimal.ROUND_DOWN));
		totalUsageMap.put("totalToe", totalToe.setScale(3,BigDecimal.ROUND_DOWN));
		totalUsageMap.put("totalCharge", totalCharge);
		totalUsageMap.put("co2Usage", co2totalUsage.setScale(3,BigDecimal.ROUND_DOWN));
		totalUsageMap.put("minUsage", minVal.setScale(3,BigDecimal.ROUND_DOWN));
		totalUsageMap.put("maxUsage", maxVal.setScale(3,BigDecimal.ROUND_DOWN));
		totalUsageMap.put("minToe", minToe.setScale(3,BigDecimal.ROUND_DOWN));
		totalUsageMap.put("maxToe", maxToe.setScale(3,BigDecimal.ROUND_DOWN));
		totalUsageMap.put("minPeriod", min);
		totalUsageMap.put("maxPeriod", max);
		
		resultMap.put("periodUsageList", periodUsageList);
		resultMap.put("facilityUsageList", facilityUsageList);
		resultMap.put("totalUsageMap", totalUsageMap);
		resultMap.put("maxToe", maxToe);
		}catch(Exception e){
			e.printStackTrace();
			
		}
		return resultMap;
		
		
	}
	
	@SuppressWarnings("unchecked")
	private List<Object> getPeriodUsage(Map<String,Object> params,List<EndDevice> endDeviceList) throws Exception{
		logger.info (params);
		
		if ( endDeviceList.isEmpty() ) {
			return new ArrayList<Object>();
		}
		
		String periodType = (String)params.get("periodType");
		
		String paramWeekStartDate = (String)params.get("currWeekStartDate");
		String paramYear = (String)params.get("currYear");
		String paramMonth = (String)params.get("currMonth");
		String paramDay = (String)params.get("today");
		String paramYearMonth = paramYear + paramMonth;
		
		
		// 실제 현제일자로써 기간구분별로 최소값,최대값을 찾아내기위해서
		// 현재시간,요일,월,분기까지만 대상으로 한다.
		String today 		= CalendarUtil.getCurrentDate();
		String currYearMonth	= today.substring(0, 6);
		String currMonth	= today.substring(4, 6);
		String currHour 	= TimeUtil.getCurrentTimeMilli().substring(8, 10);
		String currQuater	=  Integer.toString((Integer.parseInt(currMonth) - 1)/3 + 1);
		
		
		// 검침데이터와 조인을 하기위한 IN 조건 내용 조립
		// 1.EndDevice ID 목록
		// 2.EndDevice 의 Meter ID 목록
		// 3.EndDevice 의 Modem ID 목록,EndDevice 의 Meter 의 Modem ID 목록
		List<Integer> endDeviceId = new ArrayList<Integer>();
		List<Integer> modemId = new ArrayList<Integer>();
		List<Integer> meterId = new ArrayList<Integer>();
		
		Supplier supplier = null;
		
		for(EndDevice endDevice:endDeviceList){
		    if (supplier == null) {
		        supplier = endDevice.getSupplier();		        
		    }
			endDeviceId.add(endDevice.getId());
			
			if(endDevice.getModem()!=null){
				modemId.add(endDevice.getModem().getId());
			}
			
			if(endDevice.getMeters()!=null){
				for(Meter meter:endDevice.getMeters()){
					meterId.add(meter.getId());
					if(meter.getModem()!=null){
						modemId.add(meter.getModem().getId());
					}
				}
			}
		}
		
		// IN 문이 OR 조건으로 걸리는데 내용이없을경우 쿼리 오류발생하므로
		// -1 을 설정해준다.
		if(endDeviceId.size()<1){
			endDeviceId.add(-1);
		}
		if(modemId.size()<1){
			modemId.add(-1);
		}
		if(meterId.size()<1){
			meterId.add(-1);
		}
		
		params.put("endDeviceId", endDeviceId);
		params.put("modemId", modemId);
		params.put("meterId", meterId);
		
		
		List<Object> resultList = new ArrayList<Object>();
		
		Map<String,Object> tmp = null;
		if(CommonConstants.DateType.DAILY.getCode().equals(periodType)){
			// MeteringDay 호출
			
			Map<String,Object> emparams = new HashMap<String,Object>();
			emparams.put("dst", 0);
			emparams.put("channel", DefaultChannel.Usage.getCode());
			emparams.put("meterType", CommonConstants.MeterType.EnergyMeter.getDayClassName());
			List<Object> emList = meteringDayDao.getUsageForEndDevicesByDay(params,emparams);
			
			Map<String,Object> gmparams = new HashMap<String,Object>();
			gmparams.put("dst", 0);
			gmparams.put("channel", DefaultChannel.Usage.getCode());
			gmparams.put("meterType", CommonConstants.MeterType.GasMeter.getDayClassName());
			List<Object> gmList = meteringDayDao.getUsageForEndDevicesByDay(params,gmparams);
			
			Map<String,Object> wmparams = new HashMap<String,Object>();
			wmparams.put("dst", 0);
			wmparams.put("channel", DefaultChannel.Usage.getCode());
			wmparams.put("meterType", CommonConstants.MeterType.WaterMeter.getDayClassName());
			List<Object> wmList = meteringDayDao.getUsageForEndDevicesByDay(params,wmparams);
			
			Map<String,Object> hmparams = new HashMap<String,Object>();
			hmparams.put("dst", 0);
			hmparams.put("channel", DefaultChannel.Usage.getCode());
			hmparams.put("meterType", CommonConstants.MeterType.HeatMeter.getDayClassName());
			List<Object> hmList = meteringDayDao.getUsageForEndDevicesByDay(params,hmparams);
			
			Map<String,Object> emco2params = new HashMap<String,Object>();
			emco2params.put("dst", 0);
			emco2params.put("channel", DefaultChannel.Co2.getCode());
			emco2params.put("meterType", CommonConstants.MeterType.EnergyMeter.getDayClassName());
			List<Object> emCo2List = meteringDayDao.getUsageForEndDevicesByDay(params,emco2params);
			
			Map<String,Object> gmco2params = new HashMap<String,Object>();
			gmco2params.put("dst", 0);
			gmco2params.put("channel", DefaultChannel.Co2.getCode());
			gmco2params.put("meterType", CommonConstants.MeterType.GasMeter.getDayClassName());
			List<Object> gmCo2List = meteringDayDao.getUsageForEndDevicesByDay(params,gmco2params);
			
			Map<String,Object> wmco2params = new HashMap<String,Object>();
			wmco2params.put("dst", 0);
			wmco2params.put("channel", DefaultChannel.Co2.getCode());
			wmco2params.put("meterType", CommonConstants.MeterType.WaterMeter.getDayClassName());
			List<Object> wmCo2List = meteringDayDao.getUsageForEndDevicesByDay(params,wmco2params);
			
			Map<String,Object> hmco2params = new HashMap<String,Object>();
			hmco2params.put("dst", 0);
			hmco2params.put("channel", DefaultChannel.Co2.getCode());
			hmco2params.put("meterType", CommonConstants.MeterType.HeatMeter.getDayClassName());
			List<Object> hmCo2List = meteringDayDao.getUsageForEndDevicesByDay(params,hmco2params);
			
			Map<String,Object> emMap = new HashMap<String,Object>();
			Map<String,Object> gmMap = new HashMap<String,Object>();
			Map<String,Object> wmMap = new HashMap<String,Object>();
			Map<String,Object> hmMap = new HashMap<String,Object>();
			
			Map<String,Object> emCo2Map = new HashMap<String,Object>();
			Map<String,Object> gmCo2Map = new HashMap<String,Object>();
			Map<String,Object> wmCo2Map = new HashMap<String,Object>();
			Map<String,Object> hmCo2Map = new HashMap<String,Object>();
			
			if(emList!=null&&emList.size()>0){
				emMap = (Map<String,Object>)emList.get(0);
			}
			if(gmList!=null&&gmList.size()>0){
				gmMap = (Map<String,Object>)gmList.get(0);
			}
			if(wmList!=null&&wmList.size()>0){
				wmMap = (Map<String,Object>)wmList.get(0);
			}
			if(hmList!=null&&hmList.size()>0){
				hmMap = (Map<String,Object>)hmList.get(0);
			}
			
			if(emCo2List!=null&&emCo2List.size()>0){
				emCo2Map = (Map<String,Object>)emCo2List.get(0);
			}
			if(gmCo2List!=null&&gmCo2List.size()>0){
				gmCo2Map = (Map<String,Object>)gmCo2List.get(0);
			}
			if(wmCo2List!=null&&wmCo2List.size()>0){
				wmCo2Map = (Map<String,Object>)wmCo2List.get(0);
			}
			if(hmCo2List!=null&&hmCo2List.size()>0){
				hmCo2Map = (Map<String,Object>)hmCo2List.get(0);
			}
			
			String hh="";
			for(int i=0;i<24;i++){
				hh = TimeUtil.to2Digit(i);
				tmp = new HashMap<String,Object>();
				tmp.put("xField", hh);
				tmp.put("EmUsage", new BigDecimal(emMap.get("value"+hh)==null?0:(Double)emMap.get("value"+hh)));
				tmp.put("GmUsage", new BigDecimal(gmMap.get("value"+hh)==null?0:(Double)gmMap.get("value"+hh)));
				tmp.put("WmUsage", new BigDecimal(wmMap.get("value"+hh)==null?0:(Double)wmMap.get("value"+hh)));
				tmp.put("HmUsage", new BigDecimal(hmMap.get("value"+hh)==null?0:(Double)hmMap.get("value"+hh)));
				tmp.put("co2Usage",		 new BigDecimal(emCo2Map.get("value"+hh)==null?0:(Double)emCo2Map.get("value"+hh))
								.add(new BigDecimal(gmCo2Map.get("value"+hh)==null?0:(Double)gmCo2Map.get("value"+hh))
								.add(new BigDecimal(wmCo2Map.get("value"+hh)==null?0:(Double)wmCo2Map.get("value"+hh))
								.add(new BigDecimal(hmCo2Map.get("value"+hh)==null?0:(Double)hmCo2Map.get("value"+hh))))));
				
				if(paramDay.equals(today)){
					if(hh.equals(currHour)){
						tmp.put("isCurrent", CommonConstants.YesNo.Yes.getCode());
					}else if(i>Integer.parseInt(currHour)){
						tmp.put("EmUsage", new BigDecimal(0));
						tmp.put("GmUsage", new BigDecimal(0));
						tmp.put("WmUsage", new BigDecimal(0));
						tmp.put("HmUsage", new BigDecimal(0));
						tmp.put("co2Usage",new BigDecimal(0));
					}
				}
				
				resultList.add(tmp);
			}
			
		}else if(CommonConstants.DateType.WEEKLY.getCode().equals(periodType)){
			
			Map<String,Object> emparams = new HashMap<String,Object>();
			emparams.put("dst", 0);
			emparams.put("channel", DefaultChannel.Usage.getCode());
			emparams.put("meterType", CommonConstants.MeterType.EnergyMeter.getDayClassName());
			
			List<Object> emList = meteringDayDao.getUsageForEndDevicesByWeek(params,emparams);
			
			Map<String,Object> gmparams = new HashMap<String,Object>();
			gmparams.put("dst", 0);
			gmparams.put("channel", DefaultChannel.Usage.getCode());
			gmparams.put("meterType", CommonConstants.MeterType.GasMeter.getDayClassName());
			
			List<Object> gmList = meteringDayDao.getUsageForEndDevicesByWeek(params,gmparams);
			
			Map<String,Object> wmparams = new HashMap<String,Object>();
			wmparams.put("dst", 0);
			wmparams.put("channel", DefaultChannel.Usage.getCode());
			wmparams.put("meterType", CommonConstants.MeterType.WaterMeter.getDayClassName());
			
			List<Object> wmList = meteringDayDao.getUsageForEndDevicesByWeek(params,wmparams);
			
			Map<String,Object> hmparams = new HashMap<String,Object>();
			hmparams.put("dst", 0);
			hmparams.put("channel", DefaultChannel.Usage.getCode());
			hmparams.put("meterType", CommonConstants.MeterType.HeatMeter.getDayClassName());
			
			List<Object> hmList = meteringDayDao.getUsageForEndDevicesByWeek(params,hmparams);
			
			Map<String,Object> emco2params = new HashMap<String,Object>();
			emco2params.put("dst", 0);
			emco2params.put("channel", DefaultChannel.Co2.getCode());
			emco2params.put("meterType", CommonConstants.MeterType.EnergyMeter.getDayClassName());
			List<Object> emCo2List = meteringDayDao.getUsageForEndDevicesByWeek(params,emco2params);
			
			Map<String,Object> gmco2params = new HashMap<String,Object>();
			gmco2params.put("dst", 0);
			gmco2params.put("channel", DefaultChannel.Co2.getCode());
			gmco2params.put("meterType", CommonConstants.MeterType.GasMeter.getDayClassName());
			List<Object> gmCo2List = meteringDayDao.getUsageForEndDevicesByWeek(params,gmco2params);
			
			Map<String,Object> wmco2params = new HashMap<String,Object>();
			wmco2params.put("dst", 0);
			wmco2params.put("channel", DefaultChannel.Co2.getCode());
			wmco2params.put("meterType", CommonConstants.MeterType.WaterMeter.getDayClassName());
			List<Object> wmCo2List = meteringDayDao.getUsageForEndDevicesByWeek(params,wmco2params);
			
			Map<String,Object> hmco2params = new HashMap<String,Object>();
			hmco2params.put("dst", 0);
			hmco2params.put("channel", DefaultChannel.Co2.getCode());
			hmco2params.put("meterType", CommonConstants.MeterType.HeatMeter.getDayClassName());
			List<Object> hmCo2List = meteringDayDao.getUsageForEndDevicesByWeek(params,hmco2params);
			
			
			Map<String,Object> emMap = new HashMap<String,Object>();
			Map<String,Object> gmMap = new HashMap<String,Object>();
			Map<String,Object> wmMap = new HashMap<String,Object>();
			Map<String,Object> hmMap = new HashMap<String,Object>();
			
			Map<String,Object> emCo2Map = new HashMap<String,Object>();
			Map<String,Object> gmCo2Map = new HashMap<String,Object>();
			Map<String,Object> wmCo2Map = new HashMap<String,Object>();
			Map<String,Object> hmCo2Map = new HashMap<String,Object>();
			
			for(Object obj:emList){
				Map<String,Object> emtmp = new HashMap<String,Object>();
				emtmp = (Map<String,Object>)obj;
				emMap.put((String)emtmp.get("yyyymmdd"), emtmp.get("total"));
			}
			
			for(Object obj:gmList){
				Map<String,Object> gmtmp = new HashMap<String,Object>();
				gmtmp = (Map<String,Object>)obj;
				gmMap.put((String)gmtmp.get("yyyymmdd"), gmtmp.get("total"));
			}
			
			for(Object obj:wmList){
				Map<String,Object> wmtmp = new HashMap<String,Object>();
				wmtmp = (Map<String,Object>)obj;
				wmMap.put((String)wmtmp.get("yyyymmdd"), wmtmp.get("total"));
			}
			
			for(Object obj:hmList){
				Map<String,Object> hmtmp = new HashMap<String,Object>();
				hmtmp = (Map<String,Object>)obj;
				hmMap.put((String)hmtmp.get("yyyymmdd"), hmtmp.get("total"));
			}
			
			for(Object obj:emCo2List){
				Map<String,Object> emco2tmp = new HashMap<String,Object>();
				emco2tmp = (Map<String,Object>)obj;
				emCo2Map.put((String)emco2tmp.get("yyyymmdd"), emco2tmp.get("total"));
			}
			
			for(Object obj:gmCo2List){
				Map<String,Object> gmco2tmp = new HashMap<String,Object>();
				gmco2tmp = (Map<String,Object>)obj;
				gmCo2Map.put((String)gmco2tmp.get("yyyymmdd"), gmco2tmp.get("total"));
			}
			
			for(Object obj:wmCo2List){
				Map<String,Object> wmco2tmp = new HashMap<String,Object>();
				wmco2tmp = (Map<String,Object>)obj;
				wmCo2Map.put((String)wmco2tmp.get("yyyymmdd"), wmco2tmp.get("total"));
			}
			
			for(Object obj:hmCo2List){
				Map<String,Object> hmco2tmp = new HashMap<String,Object>();
				hmco2tmp = (Map<String,Object>)obj;
				hmCo2Map.put((String)hmco2tmp.get("yyyymmdd"), hmco2tmp.get("total"));
			}
		
			for(int i=0;i<7;i++){
				String yyyymmdd = CalendarUtil.getDateWithoutFormat(paramWeekStartDate,Calendar.DATE, i);
				
				int year = Integer.parseInt(yyyymmdd.substring(0, 4));
				int month = Integer.parseInt(yyyymmdd.substring(4, 6));
				int date = Integer.parseInt(yyyymmdd.substring(6, 8));
				
				tmp = new HashMap<String,Object>();
				tmp.put("yyyymmdd", yyyymmdd);
				tmp.put("xField", CalendarUtil.getWeekDay(supplier.getLang().getCode_2letter(), year, month, date));
				tmp.put("EmUsage", new BigDecimal(emMap.get(yyyymmdd)==null?0:(Double)emMap.get(yyyymmdd)));
				tmp.put("GmUsage", new BigDecimal(gmMap.get(yyyymmdd)==null?0:(Double)gmMap.get(yyyymmdd)));
				tmp.put("WmUsage", new BigDecimal(wmMap.get(yyyymmdd)==null?0:(Double)wmMap.get(yyyymmdd)));
				tmp.put("HmUsage", new BigDecimal(hmMap.get(yyyymmdd)==null?0:(Double)hmMap.get(yyyymmdd)));
				tmp.put("co2Usage",		 new BigDecimal(emCo2Map.get(yyyymmdd)==null?0:(Double)emCo2Map.get(yyyymmdd))
								.add(new BigDecimal(gmCo2Map.get(yyyymmdd)==null?0:(Double)gmCo2Map.get(yyyymmdd))
								.add(new BigDecimal(wmCo2Map.get(yyyymmdd)==null?0:(Double)wmCo2Map.get(yyyymmdd))
								.add(new BigDecimal(hmCo2Map.get(yyyymmdd)==null?0:(Double)hmCo2Map.get(yyyymmdd))))));
				
				if(yyyymmdd.equals(today)){
					tmp.put("isCurrent", CommonConstants.YesNo.Yes.getCode());
				}else if(Integer.parseInt(yyyymmdd)>Integer.parseInt(today)){
					tmp.put("EmUsage", new BigDecimal(0));
					tmp.put("GmUsage", new BigDecimal(0));
					tmp.put("WmUsage", new BigDecimal(0));
					tmp.put("HmUsage", new BigDecimal(0));
					tmp.put("co2Usage",new BigDecimal(0));
				}
				
				resultList.add(tmp);
			}
			
		}else if(CommonConstants.DateType.MONTHLY.getCode().equals(periodType)){
			Map<String,Object> emparams = new HashMap<String,Object>();
			emparams.put("dst", 0);
			emparams.put("channel", DefaultChannel.Usage.getCode());
			emparams.put("meterType", CommonConstants.MeterType.EnergyMeter.getMonthClassName());
			List<Object> emList = meteringDayDao.getUsageForEndDevicesByMonth(params,emparams);
			
			Map<String,Object> gmparams = new HashMap<String,Object>();
			gmparams.put("dst", 0);
			gmparams.put("channel", DefaultChannel.Usage.getCode());
			gmparams.put("meterType", CommonConstants.MeterType.GasMeter.getMonthClassName());
			List<Object> gmList = meteringDayDao.getUsageForEndDevicesByMonth(params,gmparams);
			
			Map<String,Object> wmparams = new HashMap<String,Object>();
			wmparams.put("dst", 0);
			wmparams.put("channel", DefaultChannel.Usage.getCode());
			wmparams.put("meterType", CommonConstants.MeterType.WaterMeter.getMonthClassName());
			List<Object> wmList = meteringDayDao.getUsageForEndDevicesByMonth(params,wmparams);
			
			Map<String,Object> hmparams = new HashMap<String,Object>();
			hmparams.put("dst", 0);
			hmparams.put("channel", DefaultChannel.Usage.getCode());
			hmparams.put("meterType", CommonConstants.MeterType.HeatMeter.getMonthClassName());
			List<Object> hmList = meteringDayDao.getUsageForEndDevicesByMonth(params,hmparams);
			
			Map<String,Object> emco2params = new HashMap<String,Object>();
			emco2params.put("dst", 0);
			emco2params.put("channel", DefaultChannel.Co2.getCode());
			emco2params.put("meterType", CommonConstants.MeterType.EnergyMeter.getMonthClassName());
			List<Object> emCo2List = meteringDayDao.getUsageForEndDevicesByMonth(params,emco2params);
			
			Map<String,Object> gmco2params = new HashMap<String,Object>();
			gmco2params.put("dst", 0);
			gmco2params.put("channel", DefaultChannel.Co2.getCode());
			gmco2params.put("meterType", CommonConstants.MeterType.GasMeter.getMonthClassName());
			List<Object> gmCo2List = meteringDayDao.getUsageForEndDevicesByMonth(params,gmco2params);
			
			Map<String,Object> wmco2params = new HashMap<String,Object>();
			wmco2params.put("dst", 0);
			wmco2params.put("channel", DefaultChannel.Co2.getCode());
			wmco2params.put("meterType", CommonConstants.MeterType.WaterMeter.getMonthClassName());
			List<Object> wmCo2List = meteringDayDao.getUsageForEndDevicesByMonth(params,wmco2params);
			
			Map<String,Object> hmco2params = new HashMap<String,Object>();
			hmco2params.put("dst", 0);
			hmco2params.put("channel", DefaultChannel.Co2.getCode());
			hmco2params.put("meterType", CommonConstants.MeterType.HeatMeter.getMonthClassName());
			List<Object> hmCo2List = meteringDayDao.getUsageForEndDevicesByMonth(params,hmco2params);
					
			Map<String,Object> emMap = new HashMap<String,Object>();
			Map<String,Object> gmMap = new HashMap<String,Object>();
			Map<String,Object> wmMap = new HashMap<String,Object>();
			Map<String,Object> hmMap = new HashMap<String,Object>();
			
			Map<String,Object> emCo2Map = new HashMap<String,Object>();
			Map<String,Object> gmCo2Map = new HashMap<String,Object>();
			Map<String,Object> wmCo2Map = new HashMap<String,Object>();
			Map<String,Object> hmCo2Map = new HashMap<String,Object>();
			
			for(Object obj:emList){
				Map<String,Object> emtmp = (Map<String,Object>)obj;
				emMap.put((String)emtmp.get("yyyymm"), emtmp.get("total"));
			}
			
			for(Object obj:gmList){
				Map<String,Object> gmtmp = (Map<String,Object>)obj;
				gmMap.put((String)gmtmp.get("yyyymm"), gmtmp.get("total"));
			}
			
			for(Object obj:wmList){
				Map<String,Object> wmtmp = (Map<String,Object>)obj;
				wmMap.put((String)wmtmp.get("yyyymm"), wmtmp.get("total"));
			}
			
			for(Object obj:hmList){
				Map<String,Object> hmtmp = (Map<String,Object>)obj;
				hmMap.put((String)hmtmp.get("yyyymm"), hmtmp.get("total"));
			}
			
			for(Object obj:emCo2List){
				Map<String,Object> emco2tmp = (Map<String,Object>)obj;
				emCo2Map.put((String)emco2tmp.get("yyyymm"), emco2tmp.get("total"));
			}
			
			for(Object obj:gmCo2List){
				Map<String,Object> gmco2tmp = (Map<String,Object>)obj;
				gmCo2Map.put((String)gmco2tmp.get("yyyymm"), gmco2tmp.get("total"));
			}
			
			for(Object obj:wmCo2List){
				Map<String,Object> wmco2tmp = (Map<String,Object>)obj;
				wmCo2Map.put((String)wmco2tmp.get("yyyymm"), wmco2tmp.get("total"));
			}
			
			for(Object obj:hmCo2List){
				Map<String,Object> hmco2tmp = (Map<String,Object>)obj;
				hmCo2Map.put((String)hmco2tmp.get("yyyymm"), hmco2tmp.get("total"));
			}
			
			for(int i=1;i<13;i++){
				String mm = TimeUtil.to2Digit(i);
				String yyyymm = paramYear+mm;
				tmp = new HashMap<String,Object>();
				tmp.put("xField", Integer.toString(i));
				tmp.put("EmUsage", new BigDecimal(emMap.get(yyyymm)==null?0:(Double)emMap.get(yyyymm)));
				tmp.put("GmUsage", new BigDecimal(gmMap.get(yyyymm)==null?0:(Double)gmMap.get(yyyymm)));
				tmp.put("WmUsage", new BigDecimal(wmMap.get(yyyymm)==null?0:(Double)wmMap.get(yyyymm)));
				tmp.put("HmUsage", new BigDecimal(hmMap.get(yyyymm)==null?0:(Double)hmMap.get(yyyymm)));
				tmp.put("co2Usage",		 new BigDecimal(emCo2Map.get(yyyymm)==null?0:(Double)emCo2Map.get(yyyymm))
								.add(new BigDecimal(gmCo2Map.get(yyyymm)==null?0:(Double)gmCo2Map.get(yyyymm))
								.add(new BigDecimal(wmCo2Map.get(yyyymm)==null?0:(Double)wmCo2Map.get(yyyymm))
								.add(new BigDecimal(hmCo2Map.get(yyyymm)==null?0:(Double)hmCo2Map.get(yyyymm))))));
				
				if(mm.equals(currMonth)){
					tmp.put("isCurrent", CommonConstants.YesNo.Yes.getCode());
				}
				
				resultList.add(tmp);
			}
		}else if(CommonConstants.DateType.QUARTERLY.getCode().equals(periodType)){
			Map<String,Object> emparams = new HashMap<String,Object>();
			emparams.put("dst", 0);
			emparams.put("channel", DefaultChannel.Usage.getCode());
			emparams.put("meterType", CommonConstants.MeterType.EnergyMeter.getMonthClassName());
			List<Object> emList = meteringDayDao.getUsageForEndDevicesByMonth(params,emparams);
			
			Map<String,Object> gmparams = new HashMap<String,Object>();
			gmparams.put("dst", 0);
			gmparams.put("channel", DefaultChannel.Usage.getCode());
			gmparams.put("meterType", CommonConstants.MeterType.GasMeter.getMonthClassName());
			List<Object> gmList = meteringDayDao.getUsageForEndDevicesByMonth(params,gmparams);
			
			Map<String,Object> wmparams = new HashMap<String,Object>();
			wmparams.put("dst", 0);
			wmparams.put("channel", DefaultChannel.Usage.getCode());
			wmparams.put("meterType", CommonConstants.MeterType.WaterMeter.getMonthClassName());
			List<Object> wmList = meteringDayDao.getUsageForEndDevicesByMonth(params,wmparams);
			
			Map<String,Object> hmparams = new HashMap<String,Object>();
			hmparams.put("dst", 0);
			hmparams.put("channel", DefaultChannel.Usage.getCode());
			hmparams.put("meterType", CommonConstants.MeterType.HeatMeter.getMonthClassName());
			List<Object> hmList = meteringDayDao.getUsageForEndDevicesByMonth(params,hmparams);
			
			Map<String,Object> emco2params = new HashMap<String,Object>();
			emco2params.put("dst", 0);
			emco2params.put("channel", DefaultChannel.Co2.getCode());
			emco2params.put("meterType", CommonConstants.MeterType.EnergyMeter.getMonthClassName());
			List<Object> emCo2List = meteringDayDao.getUsageForEndDevicesByMonth(params,emco2params);
			
			Map<String,Object> gmco2params = new HashMap<String,Object>();
			gmco2params.put("dst", 0);
			gmco2params.put("channel", DefaultChannel.Co2.getCode());
			gmco2params.put("meterType", CommonConstants.MeterType.GasMeter.getMonthClassName());
			List<Object> gmCo2List = meteringDayDao.getUsageForEndDevicesByMonth(params,gmco2params);
			
			Map<String,Object> wmco2params = new HashMap<String,Object>();
			wmco2params.put("dst", 0);
			wmco2params.put("channel", DefaultChannel.Co2.getCode());
			wmco2params.put("meterType", CommonConstants.MeterType.WaterMeter.getMonthClassName());
			List<Object> wmCo2List = meteringDayDao.getUsageForEndDevicesByMonth(params,wmco2params);
			
			Map<String,Object> hmco2params = new HashMap<String,Object>();
			hmco2params.put("dst", 0);
			hmco2params.put("channel", DefaultChannel.Co2.getCode());
			hmco2params.put("meterType", CommonConstants.MeterType.HeatMeter.getMonthClassName());
			List<Object> hmCo2List = meteringDayDao.getUsageForEndDevicesByMonth(params,hmco2params);
			
			Map<String,Object> emMap = new HashMap<String,Object>();
			Map<String,Object> gmMap = new HashMap<String,Object>();
			Map<String,Object> wmMap = new HashMap<String,Object>();
			Map<String,Object> hmMap = new HashMap<String,Object>();
			
			Map<String,Object> emCo2Map = new HashMap<String,Object>();
			Map<String,Object> gmCo2Map = new HashMap<String,Object>();
			Map<String,Object> wmCo2Map = new HashMap<String,Object>();
			Map<String,Object> hmCo2Map = new HashMap<String,Object>();
			
			for(Object obj:emList){
				Map<String,Object> emtmp = (Map<String,Object>)obj;
				emMap.put((String)emtmp.get("yyyymm"), emtmp.get("total"));
			}
			
			for(Object obj:gmList){
				Map<String,Object> gmtmp = (Map<String,Object>)obj;
				gmMap.put((String)gmtmp.get("yyyymm"), gmtmp.get("total"));
			}
			
			for(Object obj:wmList){
				Map<String,Object> wmtmp = (Map<String,Object>)obj;
				wmMap.put((String)wmtmp.get("yyyymm"), wmtmp.get("total"));
			}
			
			for(Object obj:hmList){
				Map<String,Object> hmtmp = (Map<String,Object>)obj;
				hmMap.put((String)hmtmp.get("yyyymm"), hmtmp.get("total"));
			}
			
			for(Object obj:emCo2List){
				Map<String,Object> emco2tmp = (Map<String,Object>)obj;
				emCo2Map.put((String)emco2tmp.get("yyyymm"), emco2tmp.get("total"));
			}
			
			for(Object obj:gmCo2List){
				Map<String,Object> gmco2tmp = (Map<String,Object>)obj;
				gmCo2Map.put((String)gmco2tmp.get("yyyymm"), gmco2tmp.get("total"));
			}
			
			for(Object obj:wmCo2List){
				Map<String,Object> wmco2tmp = (Map<String,Object>)obj;
				wmCo2Map.put((String)wmco2tmp.get("yyyymm"), wmco2tmp.get("total"));
			}
			
			for(Object obj:hmCo2List){
				Map<String,Object> hmco2tmp = (Map<String,Object>)obj;
				hmCo2Map.put((String)hmco2tmp.get("yyyymm"), hmco2tmp.get("total"));
			}
			
			List<Object> monthList = new ArrayList<Object>(); 
			for(int i=1;i<13;i++){
				String mm = TimeUtil.to2Digit(i);
				String yyyymm = paramYear+mm;
				tmp = new HashMap<String,Object>();
				tmp.put("xField", i);
				tmp.put("EmUsage", new BigDecimal(emMap.get(yyyymm)==null?0:(Double)emMap.get(yyyymm)));
				tmp.put("GmUsage", new BigDecimal(gmMap.get(yyyymm)==null?0:(Double)gmMap.get(yyyymm)));
				tmp.put("WmUsage", new BigDecimal(wmMap.get(yyyymm)==null?0:(Double)wmMap.get(yyyymm)));
				tmp.put("HmUsage", new BigDecimal(hmMap.get(yyyymm)==null?0:(Double)hmMap.get(yyyymm)));
				tmp.put("co2Usage",		 new BigDecimal(emCo2Map.get(yyyymm)==null?0:(Double)emCo2Map.get(yyyymm))
								.add(new BigDecimal(gmCo2Map.get(yyyymm)==null?0:(Double)gmCo2Map.get(yyyymm))
								.add(new BigDecimal(wmCo2Map.get(yyyymm)==null?0:(Double)wmCo2Map.get(yyyymm))
								.add(new BigDecimal(hmCo2Map.get(yyyymm)==null?0:(Double)hmCo2Map.get(yyyymm))))));
				monthList.add(tmp);
			}
			
			for(int i=0;i<4;i++){
				BigDecimal emUsage = new BigDecimal(0);
				BigDecimal gmUsage = new BigDecimal(0);
				BigDecimal wmUsage = new BigDecimal(0);
				BigDecimal hmUsage = new BigDecimal(0);
				BigDecimal co2Usage = new BigDecimal(0);
				for(int j=i*3;j<i*3+3;j++){
					Map<String,Object> monthtmp = (Map<String,Object>)monthList.get(j);
					emUsage = emUsage.add((BigDecimal)monthtmp.get("EmUsage"));
					gmUsage = gmUsage.add((BigDecimal)monthtmp.get("GmUsage"));
					wmUsage = wmUsage.add((BigDecimal)monthtmp.get("WmUsage"));
					hmUsage = hmUsage.add((BigDecimal)monthtmp.get("HmUsage"));
					co2Usage = co2Usage.add((BigDecimal)monthtmp.get("co2Usage"));
				}
				
				String quater = Integer.toString(i+1) ;
				Map<String,Object> quatermap = new HashMap<String,Object>();
				String xField = paramYear != null ? paramYear + "/" + quater + "Q": quater + "Q";
				
				quatermap.put("xField", xField);
				quatermap.put("EmUsage", emUsage);
				quatermap.put("GmUsage", gmUsage);
				quatermap.put("WmUsage", wmUsage);
				quatermap.put("HmUsage", hmUsage);
				quatermap.put("co2Usage", co2Usage);
				
				if(paramYearMonth.equals(currYearMonth)){
					
					if(quater.equals(currQuater)){
						quatermap.put("isCurrent", CommonConstants.YesNo.Yes.getCode());
					}else if(Integer.parseInt(quater)>Integer.parseInt(currQuater)){
						tmp.put("EmUsage", new BigDecimal(0));
						tmp.put("GmUsage", new BigDecimal(0));
						tmp.put("WmUsage", new BigDecimal(0));
						tmp.put("HmUsage", new BigDecimal(0));
						tmp.put("co2Usage",new BigDecimal(0));
					}
				}
				
				resultList.add(quatermap);
			}

		}

		// EndDevice 분류에 해당하는 EndDevice Id 목록을 조회한후
		// 각각의 모뎀Id, 미터Id, 미터의모뎀Id 를 조회하고
		// 검침데이터와 모든 ID를 or 조건으로 걸어서 검침데이터를 전기,가스,수도 별로 조회한다.
		// 시간or요일or달or분기,전기검침량,가스검침량,수도검침량,
			
		return resultList;
	}
	
    @SuppressWarnings("unchecked")
    public Map<String,Object> getFacilityUsageAllPeriod(Map<String,Object> params) {
        logger.info(params);
        String codeId = StringUtil.nullToBlank(params.get("codeId"));
        String searchDate = (String) params.get("searchDate");
        // 일별데이터조회
        Map<String,Object> dayResultMap = null;
        Map<String,Object> lastDayResultMap = null;
        List<Object> dayResultList = null;
        List<Object> lastDayResultList = null;

        Map<String,Object> dayParam = new HashMap<String,Object>();
        dayParam.put("searchDate", searchDate);
        dayParam.put("codeId", codeId);
        dayParam.put("periodType", CommonConstants.DateType.DAILY.getCode());
        dayResultMap = getFacilityUsageByPeriod(dayParam);
        dayResultList = (List<Object>)dayResultMap.get("periodUsageList");

        Map<String,Object> lastDayParam = new HashMap<String,Object>();
        lastDayParam.put("searchDate", searchDate);
        lastDayParam.put("codeId", codeId);
        lastDayParam.put("periodType", CommonConstants.DateType.DAILY.getCode());
        lastDayParam.put("lastUsageYn", CommonConstants.YesNo.Yes.getCode());
        lastDayResultMap = getFacilityUsageByPeriod(lastDayParam);
        lastDayResultList = (List<Object>)lastDayResultMap.get("periodUsageList");

//      for(int i=0;i<24;i++){
//          Map<String,Object> dayhh = (Map<String,Object>)dayResultList.get(i);
//          Map<String,Object> lastdayhh = (Map<String,Object>)lastDayResultList.get(i);
//
//          dayhh.put("lastEmUsage", lastdayhh.get("EmUsage"));
//          dayhh.put("lastGmUsage", lastdayhh.get("GmUsage"));
//          dayhh.put("lastWmUsage", lastdayhh.get("WmUsage"));
//          dayhh.put("lastHmUsage", lastdayhh.get("HmUsage"));
//          dayhh.put("lastco2Usage", lastdayhh.get("co2Usage"));
//
//          dayhh.put("lastEmToe", lastdayhh.get("EmToe"));
//          dayhh.put("lastGmToe", lastdayhh.get("GmToe"));
//          dayhh.put("lastWmToe", lastdayhh.get("WmToe"));
//          dayhh.put("lastHmToe", lastdayhh.get("HmToe"));
//      }

        if (dayResultList != null && dayResultList.size() > 0 && lastDayResultList != null && lastDayResultList.size() > 0) {
            Map<String,Object> dayhh = null;
            Map<String,Object> lastdayhh = null;

            for (int i = 0; i < 24; i++) {
                dayhh = (Map<String,Object>)dayResultList.get(i);
                lastdayhh = (Map<String,Object>)lastDayResultList.get(i);

                dayhh.put("lastEmUsage", lastdayhh.get("EmUsage"));
                dayhh.put("lastGmUsage", lastdayhh.get("GmUsage"));
                dayhh.put("lastWmUsage", lastdayhh.get("WmUsage"));
                dayhh.put("lastHmUsage", lastdayhh.get("HmUsage"));
                dayhh.put("lastco2Usage", lastdayhh.get("co2Usage"));

                dayhh.put("lastEmToe", lastdayhh.get("EmToe"));
                dayhh.put("lastGmToe", lastdayhh.get("GmToe"));
                dayhh.put("lastWmToe", lastdayhh.get("WmToe"));
                dayhh.put("lastHmToe", lastdayhh.get("HmToe"));
            }
        }

        if (((BigDecimal)dayResultMap.get("maxToe")).compareTo((BigDecimal)lastDayResultMap.get("maxToe")) > 0) {
            dayResultMap.put("maximum", (BigDecimal)dayResultMap.get("maxToe"));
        } else {
            dayResultMap.put("maximum", (BigDecimal)lastDayResultMap.get("maxToe"));
        }

        // 주별데이터조회
        Map<String,Object> weekResultMap = null;
        Map<String,Object> lastWeekResultMap = null;
        List<Object> weekResultList = null;
        List<Object> lastWeekResultList = null;

        Map<String,Object> weekParam = new HashMap<String,Object>();
        weekParam.put("searchDate", searchDate);
        weekParam.put("codeId", codeId);
        weekParam.put("periodType", CommonConstants.DateType.WEEKLY.getCode());
        weekResultMap = getFacilityUsageByPeriod(weekParam);
        weekResultList = (List<Object>)weekResultMap.get("periodUsageList");

        Map<String,Object> lastWeekParam = new HashMap<String,Object>();
        lastWeekParam.put("searchDate", searchDate);
        lastWeekParam.put("codeId", codeId);
        lastWeekParam.put("periodType", CommonConstants.DateType.WEEKLY.getCode());
        lastWeekParam.put("lastUsageYn", CommonConstants.YesNo.Yes.getCode());
        lastWeekResultMap = getFacilityUsageByPeriod(lastWeekParam);
        lastWeekResultList = (List<Object>)lastWeekResultMap.get("periodUsageList");

//      for(int i=0;i<7;i++){
//          Map<String,Object> weekday = (Map<String,Object>)weekResultList.get(i);
//          Map<String,Object> lastWeekday = (Map<String,Object>)lastWeekResultList.get(i);
//
//          weekday.put("lastEmUsage", lastWeekday.get("EmUsage"));
//          weekday.put("lastGmUsage", lastWeekday.get("GmUsage"));
//          weekday.put("lastWmUsage", lastWeekday.get("WmUsage"));
//          weekday.put("lastHmUsage", lastWeekday.get("HmUsage"));
//          weekday.put("lastco2Usage", lastWeekday.get("co2Usage"));
//
//          weekday.put("lastEmToe", lastWeekday.get("EmToe"));
//          weekday.put("lastGmToe", lastWeekday.get("GmToe"));
//          weekday.put("lastWmToe", lastWeekday.get("WmToe"));
//          weekday.put("lastHmToe", lastWeekday.get("HmToe"));
//      }

        if (weekResultList != null && weekResultList.size() > 0 && lastWeekResultList != null && lastWeekResultList.size() > 0) {
            Map<String,Object> weekday = null;
            Map<String,Object> lastWeekday = null;
            for (int i = 0; i < 7; i++) {
                weekday = (Map<String,Object>)weekResultList.get(i);
                lastWeekday = (Map<String,Object>)lastWeekResultList.get(i);

                weekday.put("lastEmUsage", lastWeekday.get("EmUsage"));
                weekday.put("lastGmUsage", lastWeekday.get("GmUsage"));
                weekday.put("lastWmUsage", lastWeekday.get("WmUsage"));
                weekday.put("lastHmUsage", lastWeekday.get("HmUsage"));
                weekday.put("lastco2Usage", lastWeekday.get("co2Usage"));

                weekday.put("lastEmToe", lastWeekday.get("EmToe"));
                weekday.put("lastGmToe", lastWeekday.get("GmToe"));
                weekday.put("lastWmToe", lastWeekday.get("WmToe"));
                weekday.put("lastHmToe", lastWeekday.get("HmToe"));
            }
        }

        if (((BigDecimal)weekResultMap.get("maxToe")).compareTo((BigDecimal)lastWeekResultMap.get("maxToe")) > 0) {
            weekResultMap.put("maximum", (BigDecimal)weekResultMap.get("maxToe"));
        } else {
            weekResultMap.put("maximum", (BigDecimal)lastWeekResultMap.get("maxToe"));
        }

        // 월별데이터조회
        Map<String,Object> monthResultMap = null;
        Map<String,Object> lastMonthResultMap = null;
        List<Object> monthResultList = null;
        List<Object> lastMonthResultList = null;

        Map<String,Object> monthParam = new HashMap<String,Object>();
        monthParam.put("searchDate", searchDate);
        monthParam.put("codeId", codeId);
        monthParam.put("periodType", CommonConstants.DateType.MONTHLY.getCode());
        monthResultMap = getFacilityUsageByPeriod(monthParam);
        monthResultList = (List<Object>)monthResultMap.get("periodUsageList");

        Map<String,Object> lastMonthParam = new HashMap<String,Object>();
        lastMonthParam.put("searchDate", searchDate);
        lastMonthParam.put("codeId", codeId);
        lastMonthParam.put("periodType", CommonConstants.DateType.MONTHLY.getCode());
        lastMonthParam.put("lastUsageYn", CommonConstants.YesNo.Yes.getCode());
        lastMonthResultMap = getFacilityUsageByPeriod(lastMonthParam);
        lastMonthResultList = (List<Object>)lastMonthResultMap.get("periodUsageList");

//      for(int i=0;i<12;i++){
//          Map<String,Object> yearMonth = (Map<String,Object>)monthResultList.get(i);
//          Map<String,Object> lastYearMonth = (Map<String,Object>)lastMonthResultList.get(i);
//
//          yearMonth.put("lastEmUsage", lastYearMonth.get("EmUsage"));
//          yearMonth.put("lastGmUsage", lastYearMonth.get("GmUsage"));
//          yearMonth.put("lastWmUsage", lastYearMonth.get("WmUsage"));
//          yearMonth.put("lastHmUsage", lastYearMonth.get("HmUsage"));
//          yearMonth.put("lastco2Usage", lastYearMonth.get("co2Usage"));
//
//          yearMonth.put("lastEmToe", lastYearMonth.get("EmToe"));
//          yearMonth.put("lastGmToe", lastYearMonth.get("GmToe"));
//          yearMonth.put("lastWmToe", lastYearMonth.get("WmToe"));
//          yearMonth.put("lastHmToe", lastYearMonth.get("HmToe"));
//      }

        if (monthResultList != null && monthResultList.size() > 0 && lastMonthResultList != null && lastMonthResultList.size() > 0) {
            for (int i = 0; i < 12; i++) {
                Map<String,Object> yearMonth = (Map<String,Object>)monthResultList.get(i);
                Map<String,Object> lastYearMonth = (Map<String,Object>)lastMonthResultList.get(i);

                yearMonth.put("lastEmUsage", lastYearMonth.get("EmUsage"));
                yearMonth.put("lastGmUsage", lastYearMonth.get("GmUsage"));
                yearMonth.put("lastWmUsage", lastYearMonth.get("WmUsage"));
                yearMonth.put("lastHmUsage", lastYearMonth.get("HmUsage"));
                yearMonth.put("lastco2Usage", lastYearMonth.get("co2Usage"));

                yearMonth.put("lastEmToe", lastYearMonth.get("EmToe"));
                yearMonth.put("lastGmToe", lastYearMonth.get("GmToe"));
                yearMonth.put("lastWmToe", lastYearMonth.get("WmToe"));
                yearMonth.put("lastHmToe", lastYearMonth.get("HmToe"));
            }
        }

        if (((BigDecimal)monthResultMap.get("maxToe")).compareTo((BigDecimal)lastMonthResultMap.get("maxToe")) > 0) {
            monthResultMap.put("maximum", (BigDecimal)monthResultMap.get("maxToe"));
        } else {
            monthResultMap.put("maximum", (BigDecimal)lastMonthResultMap.get("maxToe"));
        }

        // 분기별데이터조회
        Map<String,Object> quaterResultMap = null;
        Map<String,Object> lastQuaterResultMap = null;
        List<Object> quaterResultList = null;
        List<Object> lastQuaterResultList = null;

        Map<String,Object> quaterParam = new HashMap<String,Object>();
        quaterParam.put("searchDate", searchDate);
        quaterParam.put("codeId", codeId);
        quaterParam.put("periodType", CommonConstants.DateType.QUARTERLY.getCode());
        quaterResultMap = getFacilityUsageByPeriod(quaterParam);
        quaterResultList = (List<Object>)quaterResultMap.get("periodUsageList");

        Map<String,Object> lastQuaterParam = new HashMap<String,Object>();
        lastQuaterParam.put("searchDate", searchDate);
        lastQuaterParam.put("codeId", codeId);
        lastQuaterParam.put("periodType", CommonConstants.DateType.QUARTERLY.getCode());
        lastQuaterParam.put("lastUsageYn", CommonConstants.YesNo.Yes.getCode());
        lastQuaterResultMap = getFacilityUsageByPeriod(lastQuaterParam);
        lastQuaterResultList = (List<Object>)lastQuaterResultMap.get("periodUsageList");

//      for(int i=0;i<4;i++){
//          Map<String,Object> yearQuater = (Map<String,Object>)quaterResultList.get(i);
//          Map<String,Object> lastYearQuater = (Map<String,Object>)lastQuaterResultList.get(i);
//
//          yearQuater.put("lastEmUsage", lastYearQuater.get("EmUsage"));
//          yearQuater.put("lastGmUsage", lastYearQuater.get("GmUsage"));
//          yearQuater.put("lastWmUsage", lastYearQuater.get("WmUsage"));
//          yearQuater.put("lastHmUsage", lastYearQuater.get("HmUsage"));
//          yearQuater.put("lastco2Usage", lastYearQuater.get("co2Usage"));
//
//          yearQuater.put("lastEmToe", lastYearQuater.get("EmToe"));
//          yearQuater.put("lastGmToe", lastYearQuater.get("GmToe"));
//          yearQuater.put("lastWmToe", lastYearQuater.get("WmToe"));
//          yearQuater.put("lastHmToe", lastYearQuater.get("HmToe"));
//      }

        if (quaterResultList != null && quaterResultList.size() > 0 && lastQuaterResultList != null && lastQuaterResultList.size() > 0) {
            for (int i = 0; i < 4; i++) {
                Map<String,Object> yearQuater = (Map<String,Object>)quaterResultList.get(i);
                Map<String,Object> lastYearQuater = (Map<String,Object>)lastQuaterResultList.get(i);

                yearQuater.put("lastEmUsage", lastYearQuater.get("EmUsage"));
                yearQuater.put("lastGmUsage", lastYearQuater.get("GmUsage"));
                yearQuater.put("lastWmUsage", lastYearQuater.get("WmUsage"));
                yearQuater.put("lastHmUsage", lastYearQuater.get("HmUsage"));
                yearQuater.put("lastco2Usage", lastYearQuater.get("co2Usage"));

                yearQuater.put("lastEmToe", lastYearQuater.get("EmToe"));
                yearQuater.put("lastGmToe", lastYearQuater.get("GmToe"));
                yearQuater.put("lastWmToe", lastYearQuater.get("WmToe"));
                yearQuater.put("lastHmToe", lastYearQuater.get("HmToe"));
            }
        }

        if (((BigDecimal)quaterResultMap.get("maxToe")).compareTo((BigDecimal)lastQuaterResultMap.get("maxToe")) > 0) {
            quaterResultMap.put("maximum", (BigDecimal)quaterResultMap.get("maxToe"));
        } else {
            quaterResultMap.put("maximum", (BigDecimal)lastQuaterResultMap.get("maxToe"));
        }

        // 현재시간의 설비별 사용량,총사용량 조회
        Map<String,Object> currHourParam = new HashMap<String,Object>();
        currHourParam.put("codeId", codeId);
        currHourParam.put("periodType", CommonConstants.DateType.DAILY.getCode());
//      Map<String,Object> currHourResultMap = getFacilityUsageByPeriod(currHourParam);
        lastDayParam.put("lastUsageYn", "");
        Map<String,Object> currHourResultMap = getFacilityUsageByPeriod(lastDayParam);

        // Result Data 조립
        Map<String,Object> resultMap = new HashMap<String,Object>();

        String currDateTime = formatter.format(new Date());
        resultMap.put("currentDateTime", currDateTime);

        resultMap.put("dayUsage", dayResultMap);
        resultMap.put("weekUsage", weekResultMap);
        resultMap.put("monthUsage", monthResultMap);
        resultMap.put("quaterUsage", quaterResultMap);
        resultMap.put("currHourUsage", currHourResultMap);

        return resultMap;
    }

	@SuppressWarnings({ "unchecked", "unused" })
	public Map<String,Object> getFacilityUsageCurrentTime(Map<String,Object> params){
		
		// 리턴 객체
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try{
		String codeId = StringUtil.nullToBlank(params.get("codeId"));
		String periodType = (String)params.get("periodType");
		
		// 현재일자 yyyyMMdd
		String today 	= CalendarUtil.getCurrentDate();
		String currHour = TimeUtil.getCurrentTimeMilli().substring(8, 10);
		
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("today", today);
		paramMap.put("periodType", periodType);
		
		
		
		List<Object> facilityUsageList = new ArrayList<Object>();
		Map<String,Object> totalUsageMap = new HashMap<String,Object>();
		
		
		// 최상위 로케이션일경우 EndDevice 최상위 분류목록을 조회하고
		// 특정 EndDevice 분류코드가 있을경우 하위 분류목록을 조회한다.
		List<Code> endDeviceDstcdList = null;
		if("".equals(codeId)){
			endDeviceDstcdList = codeDao.getChildCodes("1.9.1");
		}else{
			endDeviceDstcdList = codeDao.getChildren(Integer.parseInt(codeId));
		}
		
		//최하위 EndDevice 분류여부
		boolean leafCodeYn = false;
		if(endDeviceDstcdList==null||endDeviceDstcdList.size()<1){
			
			Code tmp = new Code();
			tmp.setId(Integer.parseInt(codeId));
			
			endDeviceDstcdList = new ArrayList<Code>();
			endDeviceDstcdList.add(tmp);
			
			leafCodeYn = true;
		}
		
		List<Object> tmpList = null;
		Map<String,Object> currMap = null; 
		Map<String,Object> facilityUsageMap = null;
		BigDecimal totalUsage = new BigDecimal(0);
		BigDecimal totalToe = new BigDecimal(0);
		BigDecimal co2totalUsage = new BigDecimal(0);
		
		BigDecimal emTotal=new BigDecimal(0);
		BigDecimal gmTotal=new BigDecimal(0);
		BigDecimal wmTotal=new BigDecimal(0);
		BigDecimal hmTotal=new BigDecimal(0);
		
		for(Code code:endDeviceDstcdList){
			tmpList = new ArrayList<Object>();
			//paramMap.put("codeId", code.getId());
			// 입력된 EndDevice 분류의 최하위 코드ID 목록조회
			List<Integer> categories = codeDao.getLeafCode(code.getId());
			// 최하위 EndDevice 분류에 해당하는 EndDevice 목록조회
			List<EndDevice> endDeviceList = endDeviceDao.getEndDevicesByCategories(categories);
			
			tmpList = getPeriodUsage(paramMap,endDeviceList);

			BigDecimal emGmWmHmTotal = new BigDecimal(0);
			BigDecimal emgmwmhmToe = new BigDecimal(0);
			BigDecimal co2Total = new BigDecimal(0);
				
			currMap = (Map<String,Object>)tmpList.get(Integer.parseInt(currHour));
			
			BigDecimal currEmUsage = new BigDecimal(currMap.get("EmUsage") == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currMap.get("EmUsage")));
			BigDecimal currGmUsage = new BigDecimal(currMap.get("GmUsage") == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currMap.get("GmUsage")));
			BigDecimal currWmUsage = new BigDecimal(currMap.get("WmUsage") == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currMap.get("WmUsage")));
			BigDecimal currHmUsage = new BigDecimal(currMap.get("HmUsage") == null ? 0 :
				 DecimalUtil.ConvertNumberToDouble(currMap.get("HmUsage")));
			
			emTotal = emTotal.add(currEmUsage);
			gmTotal = gmTotal.add(currGmUsage);
			wmTotal = wmTotal.add(currWmUsage);
			hmTotal = hmTotal.add(currHmUsage);
			
			emGmWmHmTotal = emGmWmHmTotal.add(currEmUsage).add(currGmUsage).add(currWmUsage).add(currHmUsage);
			emgmwmhmToe = currEmUsage.multiply(new BigDecimal(KGOE.Energy.getValue()))
			.add(currGmUsage.multiply(new BigDecimal(KGOE.GasLng.getValue())))
			.add(currWmUsage.multiply(new BigDecimal(KGOE.Water.getValue())))
			.add(currHmUsage.multiply(new BigDecimal(KGOE.Heat.getValue())));
			
			co2Total = co2Total.add((BigDecimal)currMap.get("co2Usage"));
			
			// 설비분류별 사용량 데이터
			facilityUsageMap = new HashMap<String,Object>();
			facilityUsageMap.put("name", code.getName());
			facilityUsageMap.put("value", emGmWmHmTotal.setScale(3,BigDecimal.ROUND_DOWN));
			facilityUsageMap.put("toe", emgmwmhmToe.setScale(3,BigDecimal.ROUND_DOWN));
			facilityUsageList.add(facilityUsageMap);
			
			// 총사용량 데이터
			totalUsage = totalUsage.add(emGmWmHmTotal);
			totalToe = totalToe.add(emgmwmhmToe);
			co2totalUsage = co2totalUsage.add(co2Total);
		}
		
		boolean isZero = true;	// facilityUsageList 데이터가 모두 0이면, PieChart가 표현이 안되는 경우를 제어하기 위한 변수
	
		if(isZero) {
			facilityUsageList.clear();
			
			facilityUsageMap = new HashMap<String,Object>();
			facilityUsageMap.put("name", "");
			facilityUsageMap.put("value", 1);
			facilityUsageMap.put("toe", 1);
			facilityUsageList.add(facilityUsageMap);
		}
		
		Map<String,Object> emParams = new HashMap<String,Object>();
		Map<String,Object> gmParams = new HashMap<String,Object>();
		Map<String,Object> wmParams = new HashMap<String,Object>();
		Map<String,Object> hmParams = new HashMap<String,Object>();
		
		String dateType=DateType.DAILY.getCode();
		Integer period=1;
				
		emParams.put("serviceType",MeterType.EnergyMeter.getServiceType());
		emParams.put("dateType",dateType);
		emParams.put("usage",emTotal.doubleValue());
		emParams.put("period",period);
		
		gmParams.put("serviceType",MeterType.GasMeter.getServiceType());
		gmParams.put("dateType",dateType);
		gmParams.put("usage",gmTotal.doubleValue());
		gmParams.put("period",period);
		
		wmParams.put("serviceType",MeterType.WaterMeter.getServiceType());
		wmParams.put("dateType",dateType);
		wmParams.put("usage",wmTotal.doubleValue());
		wmParams.put("period",period);
		
		hmParams.put("serviceType",MeterType.HeatMeter.getServiceType());
		hmParams.put("dateType",dateType);
		hmParams.put("usage",hmTotal.doubleValue());
		hmParams.put("period",period);
		
		Double emCharge = 0.0;
		Double gmCharge = 0.0;
		Double wmCharge = 0.0;
		Double hmCharge = 0.0;
		Double totalCharge = 0.0;
		
		emCharge = new BemsStatisticUtil().getUsageCharge(emParams);
		gmCharge = new BemsStatisticUtil().getUsageCharge(gmParams);
		wmCharge = new BemsStatisticUtil().getUsageCharge(wmParams);
		hmCharge = new BemsStatisticUtil().getUsageCharge(hmParams);
		
		totalCharge = totalCharge + Math.round(emCharge + gmCharge + wmCharge + hmCharge);
		
		totalUsageMap.put("totalUsage", totalUsage.setScale(3,BigDecimal.ROUND_DOWN));
		totalUsageMap.put("totalToe", totalToe.setScale(3,BigDecimal.ROUND_DOWN));
		totalUsageMap.put("totalCharge", totalCharge);
		totalUsageMap.put("co2Usage", co2totalUsage.setScale(3,BigDecimal.ROUND_DOWN));
		
		resultMap.put("facilityUsageList", facilityUsageList);
		resultMap.put("totalUsageMap", totalUsageMap);
		}catch(Exception e){
			e.printStackTrace();
		}
		return resultMap;
	}

    @SuppressWarnings("unchecked")
    public Map<String, Object> getZoneUsageByPeriod(Map<String, Object> params) {
        logger.info(params);
        // 리턴 객체
        Map<String, Object> resultMap = new HashMap<String, Object>();

        try {
            String zoneId = StringUtil.nullToBlank(params.get("zoneId"));
            String periodType = StringUtil.nullToBlank(params.get("periodType"));

            String lastUsageYn = StringUtil.nullToBlank(params.get("lastUsageYn"));

            // 현재일자 yyyyMMdd
            String startDate = StringUtil.nullToBlank(params.get("startDate"));
            String yesterday = CalendarUtil.getDateWithoutFormat(startDate, Calendar.DAY_OF_MONTH, -1);
            String lastMonthDate = CalendarUtil.getDateWithoutFormat(startDate, Calendar.MONTH, -1);

            String currYear = startDate.substring(0, 4);
            String currMonth = startDate.substring(4, 6);
            String lastMonth = lastMonthDate.substring(4, 6);
            String lastYear = Integer.toString(Integer.parseInt(currYear) - 1);

            Map<String, String> currWeekDate = CalendarUtil.getDateWeekOfMonth(currYear, currMonth,
                    Integer.toString(CalendarUtil.getWeekOfMonth(startDate)));
            String currWeekStartDate = currWeekDate.get("startDate");
            String currWeekEndDate = CalendarUtil.getDateWithoutFormat(currWeekStartDate, Calendar.DAY_OF_MONTH, 6);

            String lastWeekStartDate = CalendarUtil.getDateWithoutFormat(currWeekStartDate, Calendar.DAY_OF_MONTH, -7);
            String lastWeekEndDate = CalendarUtil.getDateWithoutFormat(currWeekStartDate, Calendar.DAY_OF_MONTH, -1);

            Map<String, Object> paramMap = new HashMap<String, Object>();
            // 전일,전주,전년도 월,전년도 분기 조회일경우
            // 조회기준일을 각각 전일,주,년,월로 설정한다.
            if (CommonConstants.YesNo.Yes.getCode().equals(lastUsageYn)) {
                paramMap.put("today", yesterday);
                paramMap.put("currYear", lastYear);
                paramMap.put("currMonth", lastMonth);
                paramMap.put("currWeekStartDate", lastWeekStartDate);
                paramMap.put("currWeekEndDate", lastWeekEndDate);
                paramMap.put("periodType", periodType);
            } else {
                paramMap.put("today", startDate);
                paramMap.put("currYear", currYear);
                paramMap.put("currMonth", currMonth);
                paramMap.put("currWeekStartDate", currWeekStartDate);
                paramMap.put("currWeekEndDate", currWeekEndDate);
                paramMap.put("periodType", periodType);
            }

            List<Object> periodUsageList = new ArrayList<Object>();
            List<Object> facilityUsageList = new ArrayList<Object>();
            Map<String, Object> totalUsageMap = new HashMap<String, Object>();

            // 최상위 로케이션일경우 EndDevice 최상위 분류목록을 조회하고
            // 특정 EndDevice 분류코드가 있을경우 하위 분류목록을 조회한다.
            List<Zone> endDeviceDstcdList = new ArrayList<Zone>();
            Integer rootzoneId = 0;
            if ("".equals(zoneId)) {
                List<Zone> list = zoneDao.getParents();
                if (list != null && !list.isEmpty()) {
                    rootzoneId = list.get(0).getId();
                    zoneId = rootzoneId.toString();
                    endDeviceDstcdList = zoneDao.getChildren(rootzoneId);
                }
            } else {
                endDeviceDstcdList = zoneDao.getChildren(Integer.parseInt(zoneId));
            }

            // 최하위 Zone 여부
            boolean leafZoneYn = false;
            if (endDeviceDstcdList == null || endDeviceDstcdList.isEmpty()) {
                if (!zoneId.isEmpty()) {
                    Zone tmp = new Zone();
                    tmp.setId(Integer.parseInt(zoneId));

                    endDeviceDstcdList = new ArrayList<Zone>();
                    endDeviceDstcdList.add(tmp);

                    leafZoneYn = true;
                } else {
                    endDeviceDstcdList = new ArrayList<Zone>();
                }
            }

            List<Object> tmpList = null;
            Map<String, Object> currMap = null;
            Map<String, Object> totalMap = null;
            Map<String, Object> facilityUsageMap = null;
            BigDecimal totalUsage = new BigDecimal(0);
            BigDecimal totalToe = new BigDecimal(0);
            BigDecimal co2totalUsage = new BigDecimal(0);

            boolean isZero = true; // facilityUsageList 데이터가 모두 0이면, PieChart가 표현이 안되는 경우를 제어하기 위한 변수
            for (Zone zone : endDeviceDstcdList) {
                tmpList = new ArrayList<Object>();
                // paramMap.put("codeId", code.getId());
                // 입력된 EndDevice 분류의 최하위 코드ID 목록조회
                List<Integer> zones = zoneDao.getLeafZoneId(zone.getId());
                // 최하위 EndDevice 분류에 해당하는 EndDevice 목록조회
                List<EndDevice> endDeviceList = endDeviceDao.getEndDevicesByzones(zones);

                // zone에 포함된 endDevice가 없는 경우
                if (endDeviceList == null || endDeviceList.isEmpty()) {
                    continue;
                }

                tmpList = getPeriodUsage(paramMap, endDeviceList);

                BigDecimal emGmWmHmTotal = new BigDecimal(0);
                BigDecimal emTotal = new BigDecimal(0);
                BigDecimal gmTotal = new BigDecimal(0);
                BigDecimal wmTotal = new BigDecimal(0);
                BigDecimal hmTotal = new BigDecimal(0);
                BigDecimal co2Total = new BigDecimal(0);

                if (periodUsageList.isEmpty()) {
                    periodUsageList = tmpList;

                    for (Object tmpObj : tmpList) {
                        currMap = new HashMap<String, Object>();
                        currMap = (Map<String, Object>) tmpObj;

                        BigDecimal currEmUsage = new BigDecimal(currMap.get("EmUsage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(currMap.get("EmUsage")));
                        BigDecimal currGmUsage = new BigDecimal(currMap.get("GmUsage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(currMap.get("GmUsage")));
                        BigDecimal currWmUsage = new BigDecimal(currMap.get("WmUsage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(currMap.get("WmUsage")));
                        BigDecimal currHmUsage = new BigDecimal(currMap.get("HmUsage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(currMap.get("HmUsage")));

                        emGmWmHmTotal = emGmWmHmTotal.add(currEmUsage).add(currGmUsage).add(currWmUsage).add(currHmUsage);
                        emTotal = emTotal.add(currEmUsage);
                        gmTotal = gmTotal.add(currGmUsage);
                        wmTotal = wmTotal.add(currWmUsage);
                        hmTotal = hmTotal.add(currHmUsage);

                        co2Total = co2Total.add((BigDecimal) currMap.get("co2Usage"));

                    }

                } else {

                    int i = 0;
                    for (Object tmpObj : tmpList) {
                        currMap = new HashMap<String, Object>();
                        currMap = (Map<String, Object>) tmpObj;

                        totalMap = new HashMap<String, Object>();
                        totalMap = (Map<String, Object>) periodUsageList.get(i);

                        BigDecimal prevEmUsage = new BigDecimal(totalMap.get("EmUsage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(totalMap.get("EmUsage")));
                        BigDecimal currEmUsage = new BigDecimal(currMap.get("EmUsage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(currMap.get("EmUsage")));
                        BigDecimal prevGmUsage = new BigDecimal(totalMap.get("GmUsage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(totalMap.get("GmUsage")));
                        BigDecimal currGmUsage = new BigDecimal(currMap.get("GmUsage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(currMap.get("GmUsage")));
                        BigDecimal prevWmUsage = new BigDecimal(totalMap.get("WmUsage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(totalMap.get("WmUsage")));
                        BigDecimal currWmUsage = new BigDecimal(currMap.get("WmUsage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(currMap.get("WmUsage")));
                        BigDecimal prevHmUsage = new BigDecimal(totalMap.get("HmUsage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(totalMap.get("HmUsage")));
                        BigDecimal currHmUsage = new BigDecimal(currMap.get("HmUsage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(currMap.get("HmUsage")));
                        BigDecimal prevco2Usage = new BigDecimal(totalMap.get("co2Usage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(totalMap.get("co2Usage")));
                        BigDecimal currco2Usage = new BigDecimal(currMap.get("co2Usage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(currMap.get("co2Usage")));

                        // 기간별 사용량 데이터
                        totalMap.put("EmUsage", prevEmUsage.add(currEmUsage));
                        totalMap.put("GmUsage", prevGmUsage.add(currGmUsage));
                        totalMap.put("WmUsage", prevWmUsage.add(currWmUsage));
                        totalMap.put("HmUsage", prevHmUsage.add(currHmUsage));
                        totalMap.put("co2Usage", prevco2Usage.add(currco2Usage));

                        emGmWmHmTotal = emGmWmHmTotal.add(currEmUsage).add(currGmUsage).add(currWmUsage).add(currHmUsage);
                        emTotal = emTotal.add(currEmUsage);
                        gmTotal = gmTotal.add(currGmUsage);
                        wmTotal = wmTotal.add(currWmUsage);
                        hmTotal = hmTotal.add(currHmUsage);
                        co2Total = co2Total.add((BigDecimal) currMap.get("co2Usage"));

                        i++;
                    }
                }

                BigDecimal emgmwmhmToe = emTotal.multiply(new BigDecimal(KGOE.Energy.getValue()))
                        .add(gmTotal.multiply(new BigDecimal(KGOE.GasLng.getValue())))
                        .add(wmTotal.multiply(new BigDecimal(KGOE.Water.getValue())))
                        .add(hmTotal.multiply(new BigDecimal(KGOE.Heat.getValue())));

                // 설비분류별 사용량 데이터
                facilityUsageMap = new HashMap<String, Object>();
                facilityUsageMap.put("name", zone.getName());
                facilityUsageMap.put("value", emGmWmHmTotal.setScale(3, BigDecimal.ROUND_DOWN));
                facilityUsageMap.put("toe", emgmwmhmToe.setScale(3, BigDecimal.ROUND_DOWN));
                facilityUsageList.add(facilityUsageMap);

                // 총사용량 데이터
                totalUsage = totalUsage.add(emGmWmHmTotal);
                totalToe = totalToe.add(emgmwmhmToe);
                co2totalUsage = co2totalUsage.add(co2Total);

                BigDecimal tempZero = new BigDecimal("0.00");
                if (emgmwmhmToe.compareTo(tempZero) != 0) {
                    isZero = false;
                }
            }

            if (leafZoneYn) {
                // 입력된 EndDevice 분류의 최하위 코드ID 목록조회
                Zone tmp = zoneDao.get(Integer.parseInt(zoneId));
                List<Integer> zones = new ArrayList<Integer>();
                zones.add(tmp.getId());

                // 최하위 EndDevice 분류에 해당하는 EndDevice 목록조회
                List<EndDevice> endDeviceList = endDeviceDao.getEndDevicesByzones(zones);

                // 설비별사용량 리스트객체 초기화
                facilityUsageList = new ArrayList<Object>();
                for (EndDevice endDevice : endDeviceList) {

                    // zone에 포함된 endDevice가 없는 경우
                    if (endDeviceList == null || endDeviceList.isEmpty()) {
                        continue;
                    }

                    List<EndDevice> tmpEndDeviceList = new ArrayList<EndDevice>();
                    tmpEndDeviceList.add(endDevice);

                    tmpList = getPeriodUsage(paramMap, tmpEndDeviceList);

                    BigDecimal emGmWmHmTotal = new BigDecimal(0);
                    BigDecimal emTotal = new BigDecimal(0);
                    BigDecimal gmTotal = new BigDecimal(0);
                    BigDecimal wmTotal = new BigDecimal(0);
                    BigDecimal hmTotal = new BigDecimal(0);

                    for (Object tmpObj : tmpList) {
                        currMap = new HashMap<String, Object>();
                        currMap = (Map<String, Object>) tmpObj;

                        BigDecimal currEmUsage = new BigDecimal(currMap.get("EmUsage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(currMap.get("EmUsage")));
                        BigDecimal currGmUsage = new BigDecimal(currMap.get("GmUsage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(currMap.get("GmUsage")));
                        BigDecimal currWmUsage = new BigDecimal(currMap.get("WmUsage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(currMap.get("WmUsage")));
                        BigDecimal currHmUsage = new BigDecimal(currMap.get("HmUsage") == null ? 0
                                : DecimalUtil.ConvertNumberToDouble(currMap.get("HmUsage")));

                        emGmWmHmTotal = emGmWmHmTotal.add(currEmUsage).add(currGmUsage).add(currWmUsage).add(currHmUsage);
                        emTotal = emTotal.add(currEmUsage);
                        gmTotal = gmTotal.add(currGmUsage);
                        wmTotal = wmTotal.add(currWmUsage);
                        hmTotal = hmTotal.add(currHmUsage);
                    }

                    BigDecimal emgmwmhmToe = emTotal.multiply(new BigDecimal(KGOE.Energy.getValue()))
                            .add(gmTotal.multiply(new BigDecimal(KGOE.GasLng.getValue())))
                            .add(wmTotal.multiply(new BigDecimal(KGOE.Water.getValue())))
                            .add(hmTotal.multiply(new BigDecimal(KGOE.Heat.getValue())));

                    // 설비분류별 사용량 데이터
                    facilityUsageMap = new HashMap<String, Object>();
                    facilityUsageMap.put("name", endDevice.getSerialNumber());
                    facilityUsageMap.put("value", emGmWmHmTotal.setScale(3, BigDecimal.ROUND_DOWN));
                    facilityUsageMap.put("toe", emgmwmhmToe.setScale(3, BigDecimal.ROUND_DOWN));
                    facilityUsageList.add(facilityUsageMap);
                }
            }

            if (isZero) {
                facilityUsageList.clear();

                facilityUsageMap = new HashMap<String, Object>();
                facilityUsageMap.put("name", "");
                facilityUsageMap.put("value", 1);
                facilityUsageMap.put("toe", 1);
                facilityUsageList.add(facilityUsageMap);
            }

            // 요금 계산 && TOE 환산
            BigDecimal emTotal = new BigDecimal(0);
            BigDecimal gmTotal = new BigDecimal(0);
            BigDecimal wmTotal = new BigDecimal(0);
            BigDecimal hmTotal = new BigDecimal(0);

            for (Object tmpObj : periodUsageList) {
                currMap = (Map<String, Object>) tmpObj;

                BigDecimal currEmUsage = new BigDecimal(currMap.get("EmUsage") == null ? 0
                        : DecimalUtil.ConvertNumberToDouble(currMap.get("EmUsage")));
                BigDecimal currGmUsage = new BigDecimal(currMap.get("GmUsage") == null ? 0
                        : DecimalUtil.ConvertNumberToDouble(currMap.get("GmUsage")));
                BigDecimal currWmUsage = new BigDecimal(currMap.get("WmUsage") == null ? 0
                        : DecimalUtil.ConvertNumberToDouble(currMap.get("WmUsage")));
                BigDecimal currHmUsage = new BigDecimal(currMap.get("HmUsage") == null ? 0
                        : DecimalUtil.ConvertNumberToDouble(currMap.get("HmUsage")));

                currMap.put("EmToe", new BigDecimal(currEmUsage == null ? 0 : DecimalUtil.ConvertNumberToDouble(currEmUsage)
                        * KGOE.Energy.getValue()));
                currMap.put("GmToe", new BigDecimal(currGmUsage == null ? 0 : DecimalUtil.ConvertNumberToDouble(currGmUsage)
                        * KGOE.GasLng.getValue()));
                currMap.put("WmToe", new BigDecimal(currWmUsage == null ? 0 : DecimalUtil.ConvertNumberToDouble(currWmUsage)
                        * KGOE.Water.getValue()));
                currMap.put("HmToe", new BigDecimal(currHmUsage == null ? 0 : DecimalUtil.ConvertNumberToDouble(currHmUsage)
                        * KGOE.Heat.getValue()));

                emTotal = emTotal.add(currEmUsage);
                gmTotal = gmTotal.add(currGmUsage);
                wmTotal = wmTotal.add(currWmUsage);
                hmTotal = hmTotal.add(currHmUsage);
            }

            Map<String, Object> emParams = new HashMap<String, Object>();
            Map<String, Object> gmParams = new HashMap<String, Object>();
            Map<String, Object> wmParams = new HashMap<String, Object>();
            Map<String, Object> hmParams = new HashMap<String, Object>();

            String dateType = DateType.MONTHLY.getCode();
            Integer period = 0;
            if (periodType.equals(DateType.DAILY.getCode())) {
                dateType = DateType.DAILY.getCode();
                period = 1;
            } else if (periodType.equals(DateType.WEEKLY.getCode())) {
                dateType = DateType.DAILY.getCode();
                period = 7;
            } else if (periodType.equals(DateType.MONTHLY.getCode())) {
                dateType = DateType.MONTHLY.getCode();
                period = 12;
            } else if (periodType.equals(DateType.QUARTERLY.getCode())) {
                dateType = DateType.MONTHLY.getCode();
                period = 12;
            }

            emParams.put("serviceType", MeterType.EnergyMeter.getServiceType());
            emParams.put("dateType", dateType);
            emParams.put("usage", emTotal.doubleValue());
            emParams.put("period", period);

            gmParams.put("serviceType", MeterType.GasMeter.getServiceType());
            gmParams.put("dateType", dateType);
            gmParams.put("usage", gmTotal.doubleValue());
            gmParams.put("period", period);

            wmParams.put("serviceType", MeterType.WaterMeter.getServiceType());
            wmParams.put("dateType", dateType);
            wmParams.put("usage", wmTotal.doubleValue());
            wmParams.put("period", period);

            hmParams.put("serviceType", MeterType.HeatMeter.getServiceType());
            hmParams.put("dateType", dateType);
            hmParams.put("usage", hmTotal.doubleValue());
            hmParams.put("period", period);

            Double emCharge = 0.0;
            Double gmCharge = 0.0;
            Double wmCharge = 0.0;
            Double hmCharge = 0.0;
            Double totalCharge = 0.0;

            emCharge = new BemsStatisticUtil().getUsageCharge(emParams);
            gmCharge = new BemsStatisticUtil().getUsageCharge(gmParams);
            wmCharge = new BemsStatisticUtil().getUsageCharge(wmParams);
            hmCharge = new BemsStatisticUtil().getUsageCharge(hmParams);

            totalCharge = totalCharge + Math.round(emCharge + gmCharge + wmCharge + hmCharge);

            // 최소,최대 구간 설정
            String min = "";
            String max = "";
            BigDecimal minVal = new BigDecimal(0);
            BigDecimal maxVal = new BigDecimal(0);
            BigDecimal minToe = new BigDecimal(0);
            BigDecimal maxToe = new BigDecimal(0);

            BigDecimal emallTotal = new BigDecimal(0);
            BigDecimal gmallTotal = new BigDecimal(0);
            BigDecimal wmallTotal = new BigDecimal(0);
            BigDecimal hmallTotal = new BigDecimal(0);

            int i = 0;
            for (Object tmpObj : periodUsageList) {
                currMap = new HashMap<String, Object>();
                currMap = (Map<String, Object>) tmpObj;

                BigDecimal currEmUsage = new BigDecimal(currMap.get("EmUsage") == null ? 0
                        : DecimalUtil.ConvertNumberToDouble(currMap.get("EmUsage")));
                BigDecimal currGmUsage = new BigDecimal(currMap.get("GmUsage") == null ? 0
                        : DecimalUtil.ConvertNumberToDouble(currMap.get("GmUsage")));
                BigDecimal currWmUsage = new BigDecimal(currMap.get("WmUsage") == null ? 0
                        : DecimalUtil.ConvertNumberToDouble(currMap.get("WmUsage")));
                BigDecimal currHmUsage = new BigDecimal(currMap.get("HmUsage") == null ? 0
                        : DecimalUtil.ConvertNumberToDouble(currMap.get("HmUsage")));
                BigDecimal currEmToe = new BigDecimal(currMap.get("EmToe") == null ? 0
                        : DecimalUtil.ConvertNumberToDouble(currMap.get("EmToe")));
                BigDecimal currGmToe = new BigDecimal(currMap.get("GmToe") == null ? 0
                        : DecimalUtil.ConvertNumberToDouble(currMap.get("GmToe")));
                BigDecimal currWmToe = new BigDecimal(currMap.get("WmToe") == null ? 0
                        : DecimalUtil.ConvertNumberToDouble(currMap.get("WmToe")));
                BigDecimal currHmToe = new BigDecimal(currMap.get("HmToe") == null ? 0
                        : DecimalUtil.ConvertNumberToDouble(currMap.get("HmToe")));

                BigDecimal emGmWmHmTotal = new BigDecimal(0);
                emGmWmHmTotal = emGmWmHmTotal.add(currEmUsage).add(currGmUsage).add(currWmUsage).add(currHmUsage);

                emallTotal = emallTotal.add(currEmUsage);
                gmallTotal = gmallTotal.add(currGmUsage);
                wmallTotal = wmallTotal.add(currWmUsage);
                hmallTotal = hmallTotal.add(currHmUsage);

                BigDecimal emGmWmHmToe = new BigDecimal(0);
                emGmWmHmToe = emGmWmHmToe.add(currEmToe).add(currGmToe).add(currWmToe).add(currHmToe);

                if (i == 0) {
                    min = (String) currMap.get("xField");
                    max = (String) currMap.get("xField");
                    minVal = emGmWmHmTotal;
                    maxVal = emGmWmHmTotal;
                    minToe = emGmWmHmToe;
                    maxToe = emGmWmHmToe;
                }
                if (emGmWmHmTotal.compareTo(minVal) < 0) {
                    min = (String) currMap.get("xField");
                    minVal = emGmWmHmTotal;
                    minToe = emGmWmHmToe;
                }
                if (emGmWmHmTotal.compareTo(maxVal) > 0) {
                    max = (String) currMap.get("xField");
                    maxVal = emGmWmHmTotal;
                    maxToe = emGmWmHmToe;
                }

                if (CommonConstants.YesNo.Yes.getCode().equals(currMap.get("isCurrent"))) {
                    break;
                }
                i++;
            }

            String currDateTime = formatter.format(new Date());
            resultMap.put("currentDateTime", currDateTime);

            totalUsageMap.put("totalUsage", totalUsage.setScale(3, BigDecimal.ROUND_DOWN));
            totalUsageMap.put("totalToe", totalToe.setScale(3, BigDecimal.ROUND_DOWN));
            totalUsageMap.put("totalCharge", totalCharge);
            totalUsageMap.put("co2Usage", co2totalUsage.setScale(3, BigDecimal.ROUND_DOWN));
            totalUsageMap.put("minUsage", minVal.setScale(3, BigDecimal.ROUND_DOWN));
            totalUsageMap.put("maxUsage", maxVal.setScale(3, BigDecimal.ROUND_DOWN));
            totalUsageMap.put("minToe", minToe.setScale(3, BigDecimal.ROUND_DOWN));
            totalUsageMap.put("maxToe", maxToe.setScale(3, BigDecimal.ROUND_DOWN));
            totalUsageMap.put("minPeriod", min);
            totalUsageMap.put("maxPeriod", max);
            totalUsageMap.put("emallTotal", emallTotal.setScale(3, BigDecimal.ROUND_DOWN));
            totalUsageMap.put("gmallTotal", gmallTotal.setScale(3, BigDecimal.ROUND_DOWN));
            totalUsageMap.put("wmallTotal", wmallTotal.setScale(3, BigDecimal.ROUND_DOWN));
            totalUsageMap.put("hmallTotal", hmallTotal.setScale(3, BigDecimal.ROUND_DOWN));

            resultMap.put("periodUsageList", periodUsageList);
            resultMap.put("facilityUsageList", facilityUsageList);
            resultMap.put("totalUsageMap", totalUsageMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }
	
	public Map<String,Object> getZoneUsageAllPeriod(Map<String,Object> params){
		logger.info("params: " + params);
		
		String zoneId = StringUtil.nullToBlank(params.get("zoneId"));
		String startDate = StringUtil.nullToBlank(params.get("startDate"));
		
		String today = CalendarUtil.getCurrentDate();
		
		
		// Result Data 조립
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		
		// 일별데이터조회
		Map<String,Object> dayResultMap = null;
		Map<String,Object> lastDayResultMap = null;		
		
		Map<String,Object> dayParam = new HashMap<String,Object>();
		dayParam.put("zoneId", zoneId);
		dayParam.put("periodType", CommonConstants.DateType.DAILY.getCode());
		dayParam.put("startDate",startDate);
		dayResultMap = getZoneUsageByPeriod(dayParam);		
		
		
		Map<String,Object> lastDayParam = new HashMap<String,Object>();
		lastDayParam.put("zoneId", zoneId);
		lastDayParam.put("periodType", CommonConstants.DateType.DAILY.getCode());
		lastDayParam.put("lastUsageYn", CommonConstants.YesNo.Yes.getCode());
		lastDayParam.put("startDate",startDate);
		lastDayResultMap = getZoneUsageByPeriod(lastDayParam);
		
		// 주별데이터조회
		Map<String,Object> weekResultMap = null;
		Map<String,Object> lastWeekResultMap = null;
		
		Map<String,Object> weekParam = new HashMap<String,Object>();
		weekParam.put("zoneId", zoneId);
		weekParam.put("periodType", CommonConstants.DateType.WEEKLY.getCode());
		weekParam.put("startDate", startDate);
		weekResultMap = getZoneUsageByPeriod(weekParam);
		
		Map<String,Object> lastWeekParam = new HashMap<String,Object>();
		lastWeekParam.put("zoneId", zoneId);
		lastWeekParam.put("periodType", CommonConstants.DateType.WEEKLY.getCode());
		lastWeekParam.put("lastUsageYn", CommonConstants.YesNo.Yes.getCode());
		lastWeekParam.put("startDate", startDate);
		lastWeekResultMap = getZoneUsageByPeriod(lastWeekParam);
		
		// 월별데이터조회
		Map<String,Object> monthResultMap = null;
		Map<String,Object> lastMonthResultMap = null;
		
		Map<String,Object> monthParam = new HashMap<String,Object>();
		monthParam.put("zoneId", zoneId);
		monthParam.put("startDate", startDate);
		monthParam.put("periodType", CommonConstants.DateType.MONTHLY.getCode());
		monthResultMap = getZoneUsageByPeriod(monthParam);
		
		Map<String,Object> lastMonthParam = new HashMap<String,Object>();
		lastMonthParam.put("zoneId", zoneId);
		lastMonthParam.put("startDate",startDate);
		lastMonthParam.put("periodType", CommonConstants.DateType.MONTHLY.getCode());
		lastMonthParam.put("lastUsageYn", CommonConstants.YesNo.Yes.getCode());
		lastMonthResultMap = getZoneUsageByPeriod(lastMonthParam);
		
		// 분기별데이터조회
		Map<String,Object> quaterResultMap = null;
		Map<String,Object> lastQuaterResultMap = null;
		
		Map<String,Object> quaterParam = new HashMap<String,Object>();
		quaterParam.put("zoneId", zoneId);
		quaterParam.put("startDate", startDate);
		quaterParam.put("periodType", CommonConstants.DateType.QUARTERLY.getCode());
		quaterResultMap = getZoneUsageByPeriod(quaterParam);
		
		Map<String,Object> lastQuaterParam = new HashMap<String,Object>();
		lastQuaterParam.put("zoneId", zoneId);
		lastQuaterParam.put("startDate", startDate);
		lastQuaterParam.put("periodType", CommonConstants.DateType.QUARTERLY.getCode());
		lastQuaterParam.put("lastUsageYn", CommonConstants.YesNo.Yes.getCode());
		lastQuaterResultMap = getZoneUsageByPeriod(lastQuaterParam);
		
		// 현재시간의 설비별 사용량,총사용량 조회
		Map<String,Object> currHourParam = new HashMap<String,Object>();
		currHourParam.put("zoneId", zoneId);
		currHourParam.put("startDate", today);
		currHourParam.put("periodType", CommonConstants.DateType.DAILY.getCode());
		Map<String,Object> currHourResultMap = getZoneUsageCurrentTime(currHourParam);
		
		String currDateTime = formatter.format(new Date());
		
		resultMap.put("currentDateTime", currDateTime);
		
		resultMap.put("dayUsage", dayResultMap);
		resultMap.put("lastDayUsage", lastDayResultMap);
		resultMap.put("weekUsage", weekResultMap);
		resultMap.put("lastWeekUsage", lastWeekResultMap);
		resultMap.put("monthUsage", monthResultMap);
		resultMap.put("lastMonthUsage", lastMonthResultMap);
		resultMap.put("quaterUsage", quaterResultMap);
		resultMap.put("lastQuaterUsage", lastQuaterResultMap);
		resultMap.put("currHourUsage", currHourResultMap);
		
		return resultMap;
	}

    @SuppressWarnings("unchecked")
    public Map<String, Object> getZoneUsageCurrentTime(Map<String, Object> params) {

        // 리턴 객체
        Map<String, Object> resultMap = new HashMap<String, Object>();

        try {
            String zoneId = StringUtil.nullToBlank(params.get("zoneId"));
            String periodType = (String) params.get("periodType");

            // 현재일자 yyyyMMdd
            String today = CalendarUtil.getCurrentDate();

            String currHour = TimeUtil.getCurrentTimeMilli().substring(8, 10);

            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("today", today);
            paramMap.put("periodType", periodType);

            List<Object> facilityUsageList = new ArrayList<Object>();
            Map<String, Object> totalUsageMap = new HashMap<String, Object>();

            // 최상위 로케이션일경우 EndDevice 최상위 분류목록을 조회하고
            // 특정 EndDevice 분류코드가 있을경우 하위 분류목록을 조회한다.
            List<Zone> endDeviceDstcdList = null;
            Integer rootzoneId = 0;
            if ("".equals(zoneId)) {
                List<Zone> list = zoneDao.getParents();

                if (list != null && !list.isEmpty()) {
                    rootzoneId = list.get(0).getId();
                    zoneId = rootzoneId.toString();
                    endDeviceDstcdList = zoneDao.getChildren(rootzoneId);
                }
            } else {
                endDeviceDstcdList = zoneDao.getChildren(Integer.parseInt(zoneId));
            }

            // 최하위 Zone 여부
            boolean leafZoneYn = false;
            if (endDeviceDstcdList == null || endDeviceDstcdList.isEmpty()) {
                if (!zoneId.isEmpty()) {
                    Zone tmp = new Zone();
                    tmp.setId(Integer.parseInt(zoneId));

                    endDeviceDstcdList = new ArrayList<Zone>();
                    endDeviceDstcdList.add(tmp);

                    leafZoneYn = true;
                } else {
                    endDeviceDstcdList = new ArrayList<Zone>();
                }
            }

            List<Object> tmpList = null;
            Map<String, Object> currMap = null;
            Map<String, Object> facilityUsageMap = null;
            BigDecimal totalUsage = new BigDecimal(0);
            BigDecimal totalToe = new BigDecimal(0);
            BigDecimal co2totalUsage = new BigDecimal(0);

            BigDecimal emTotal = new BigDecimal(0);
            BigDecimal gmTotal = new BigDecimal(0);
            BigDecimal wmTotal = new BigDecimal(0);
            BigDecimal hmTotal = new BigDecimal(0);

            boolean isZero = true; // facilityUsageList 데이터가 모두 0이면, PieChart가 표현이 안되는 경우를 제어하기 위한 변수

            for (Zone zone : endDeviceDstcdList) {
                tmpList = new ArrayList<Object>();
                // paramMap.put("codeId", code.getId());
                // 입력된 EndDevice 분류의 최하위 코드ID 목록조회
                List<Integer> zones = zoneDao.getLeafZoneId(zone.getId());
                // 최하위 EndDevice 분류에 해당하는 EndDevice 목록조회
                List<EndDevice> endDeviceList = endDeviceDao.getEndDevicesByzones(zones);

                tmpList = getPeriodUsage(paramMap, endDeviceList);

                BigDecimal emGmWmHmTotal = new BigDecimal(0);
                BigDecimal emgmwmhmToe = new BigDecimal(0);
                BigDecimal co2Total = new BigDecimal(0);

                if (!tmpList.isEmpty()) {
                    currMap = (Map<String, Object>) tmpList.get(Integer.parseInt(currHour));

                    BigDecimal currEmUsage = new BigDecimal(currMap.get("EmUsage") == null ? 0
                            : DecimalUtil.ConvertNumberToDouble(currMap.get("EmUsage")));
                    BigDecimal currGmUsage = new BigDecimal(currMap.get("GmUsage") == null ? 0
                            : DecimalUtil.ConvertNumberToDouble(currMap.get("GmUsage")));
                    BigDecimal currWmUsage = new BigDecimal(currMap.get("WmUsage") == null ? 0
                            : DecimalUtil.ConvertNumberToDouble(currMap.get("WmUsage")));
                    BigDecimal currHmUsage = new BigDecimal(currMap.get("HmUsage") == null ? 0
                            : DecimalUtil.ConvertNumberToDouble(currMap.get("HmUsage")));

                    emTotal = emTotal.add(currEmUsage);
                    gmTotal = gmTotal.add(currGmUsage);
                    wmTotal = wmTotal.add(currWmUsage);
                    hmTotal = hmTotal.add(currHmUsage);

                    emGmWmHmTotal = emGmWmHmTotal.add(currEmUsage).add(currGmUsage).add(currWmUsage).add(currHmUsage);
                    emgmwmhmToe = currEmUsage.multiply(new BigDecimal(KGOE.Energy.getValue()))
                            .add(currGmUsage.multiply(new BigDecimal(KGOE.GasLng.getValue())))
                            .add(currHmUsage.multiply(new BigDecimal(KGOE.Heat.getValue())))
                            .add(currWmUsage.multiply(new BigDecimal(KGOE.Water.getValue())));

                    co2Total = co2Total.add((BigDecimal) currMap.get("co2Usage"));
                }

                // 설비분류별 사용량 데이터
                facilityUsageMap = new HashMap<String, Object>();
                facilityUsageMap.put("name", zone.getName());
                facilityUsageMap.put("value", emGmWmHmTotal.setScale(3, BigDecimal.ROUND_DOWN));
                facilityUsageMap.put("toe", emgmwmhmToe.setScale(3, BigDecimal.ROUND_DOWN));
                facilityUsageList.add(facilityUsageMap);

                BigDecimal tempZero = new BigDecimal("0.00");
                if (emgmwmhmToe.compareTo(tempZero) != 0) {
                    isZero = false;
                }

                // 총사용량 데이터
                totalUsage = totalUsage.add(emGmWmHmTotal);
                totalToe = totalToe.add(emgmwmhmToe);
                co2totalUsage = co2totalUsage.add(co2Total);
            }

            if (leafZoneYn) {
                // 입력된 EndDevice 분류의 최하위 코드ID 목록조회
                Zone tmp = zoneDao.get(Integer.parseInt(zoneId));
                List<Integer> zones = new ArrayList<Integer>();
                zones.add(tmp.getId());

                // 최하위 EndDevice 분류에 해당하는 EndDevice 목록조회
                List<EndDevice> endDeviceList = endDeviceDao.getEndDevicesByzones(zones);

                // 설비별사용량 리스트객체 초기화
                facilityUsageList = new ArrayList<Object>();
                for (EndDevice endDevice : endDeviceList) {

                    List<EndDevice> tmpEndDeviceList = new ArrayList<EndDevice>();
                    tmpEndDeviceList.add(endDevice);

                    tmpList = getPeriodUsage(paramMap, tmpEndDeviceList);

                    BigDecimal emGmWmHmTotal = new BigDecimal(0);
                    BigDecimal emgmwmhmToe = new BigDecimal(0);

                    currMap = (Map<String, Object>) tmpList.get(Integer.parseInt(currHour));

                    BigDecimal currEmUsage = new BigDecimal(currMap.get("EmUsage") == null ? 0
                            : DecimalUtil.ConvertNumberToDouble(currMap.get("EmUsage")));
                    BigDecimal currGmUsage = new BigDecimal(currMap.get("GmUsage") == null ? 0
                            : DecimalUtil.ConvertNumberToDouble(currMap.get("GmUsage")));
                    BigDecimal currWmUsage = new BigDecimal(currMap.get("WmUsage") == null ? 0
                            : DecimalUtil.ConvertNumberToDouble(currMap.get("WmUsage")));
                    BigDecimal currHmUsage = new BigDecimal(currMap.get("HmUsage") == null ? 0
                            : DecimalUtil.ConvertNumberToDouble(currMap.get("HmUsage")));

                    emGmWmHmTotal = emGmWmHmTotal.add(currEmUsage).add(currGmUsage).add(currWmUsage).add(currHmUsage);
                    emgmwmhmToe = currEmUsage.multiply(new BigDecimal(KGOE.Energy.getValue()))
                            .add(currGmUsage.multiply(new BigDecimal(KGOE.GasLng.getValue())))
                            .add(currWmUsage.multiply(new BigDecimal(KGOE.Water.getValue())))
                            .add(currHmUsage.multiply(new BigDecimal(KGOE.Heat.getValue())));

                    // 설비분류별 사용량 데이터
                    facilityUsageMap = new HashMap<String, Object>();
                    facilityUsageMap.put("name", endDevice.getSerialNumber());
                    facilityUsageMap.put("value", emGmWmHmTotal.setScale(3, BigDecimal.ROUND_DOWN));
                    facilityUsageMap.put("toe", emgmwmhmToe.setScale(3, BigDecimal.ROUND_DOWN));
                    facilityUsageList.add(facilityUsageMap);
                }
            }

            if (isZero) {
                facilityUsageList.clear();

                facilityUsageMap = new HashMap<String, Object>();
                facilityUsageMap.put("name", "no data");
                facilityUsageMap.put("value", 0);
                facilityUsageMap.put("toe", 1);
                facilityUsageList.add(facilityUsageMap);
            }

            Map<String, Object> emParams = new HashMap<String, Object>();
            Map<String, Object> gmParams = new HashMap<String, Object>();
            Map<String, Object> wmParams = new HashMap<String, Object>();
            Map<String, Object> hmParams = new HashMap<String, Object>();

            String dateType = DateType.DAILY.getCode();
            Integer period = 1;

            emParams.put("serviceType", MeterType.EnergyMeter.getServiceType());
            emParams.put("dateType", dateType);
            emParams.put("usage", emTotal.doubleValue());
            emParams.put("period", period);

            gmParams.put("serviceType", MeterType.GasMeter.getServiceType());
            gmParams.put("dateType", dateType);
            gmParams.put("usage", gmTotal.doubleValue());
            gmParams.put("period", period);

            wmParams.put("serviceType", MeterType.WaterMeter.getServiceType());
            wmParams.put("dateType", dateType);
            wmParams.put("usage", wmTotal.doubleValue());
            wmParams.put("period", period);

            hmParams.put("serviceType", MeterType.HeatMeter.getServiceType());
            hmParams.put("dateType", dateType);
            hmParams.put("usage", hmTotal.doubleValue());
            hmParams.put("period", period);

            Double emCharge = 0.0;
            Double gmCharge = 0.0;
            Double wmCharge = 0.0;
            Double hmCharge = 0.0;
            Double totalCharge = 0.0;

            emCharge = new BemsStatisticUtil().getUsageCharge(emParams);
            gmCharge = new BemsStatisticUtil().getUsageCharge(gmParams);
            wmCharge = new BemsStatisticUtil().getUsageCharge(wmParams);
            hmCharge = new BemsStatisticUtil().getUsageCharge(hmParams);

            totalCharge = totalCharge + Math.round(emCharge + gmCharge + wmCharge + hmCharge);

            totalUsageMap.put("totalUsage", totalUsage.setScale(3, BigDecimal.ROUND_DOWN));
            totalUsageMap.put("totalToe", totalToe.setScale(3, BigDecimal.ROUND_DOWN));
            totalUsageMap.put("totalCharge", totalCharge);
            totalUsageMap.put("co2Usage", co2totalUsage.setScale(3, BigDecimal.ROUND_DOWN));

            resultMap.put("facilityUsageList", facilityUsageList);
            resultMap.put("totalUsageMap", totalUsageMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }
}