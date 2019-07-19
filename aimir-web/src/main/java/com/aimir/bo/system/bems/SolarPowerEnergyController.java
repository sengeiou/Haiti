package com.aimir.bo.system.bems;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.device.MeterController;
import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ChangeMeterTypeName;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.model.device.SolarPowerMeter;
import com.aimir.model.system.Code;
import com.aimir.service.device.MeterManager;
import com.aimir.service.mvm.PowerGenerationManager;
import com.aimir.service.mvm.SearchMeteringDataManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.KmaWeatherUtil;
import com.aimir.util.ParameterConverter;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.KmaWeatherUtil.WeatherLocation;
import com.aimir.util.Weather;

/**
 * 태양광 에너지 컨트롤러
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 */
@Controller
public class SolarPowerEnergyController {

	Logger logger = Logger.getLogger(SolarPowerEnergyController.class);

	@Autowired
	SupplierManager supplierManager;
	
	@Autowired
	MeterManager meterManager;
	
	@Autowired
	SearchMeteringDataManager searchMeteringDataManager;
	
	@Autowired
	PowerGenerationManager powerGenerationManager;
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	MeterController meterController;
	
	/**
	 * 예외 처리 핸들러
	 * 
	 * @param t 예외
	 * @param mav ModelAndView
	 * @return ModelAndView
	 */
	private ModelAndView handleException(Throwable t, ModelAndView mav) {
		
		t.printStackTrace();
		logger.info(t);
		
		if(t instanceof IllegalArgumentException) {
			mav.addObject("result", "fail").addObject("msg", t.getMessage());
		}
		else {
			mav.addObject("result", "fail").addObject("msg", t.getMessage());
		}
		return mav;
	}
	
	/**
	 * SolarPower Hello World~
	 * 
	 * @param data
	 * @return
	 */
	@RequestMapping(value="/gadget/bems/test")
	public ModelAndView SolarPowerControllerTest(Locale locale, @RequestBody String data) {
		return new ModelAndView("jsonView")
			.addObject("Locale", locale.getLanguage())
			.addObject("HelloMessage", messageSource.getMessage("aimir.hems.label.savingGoalHistory", null, locale))
			.addObject("requestBody", data);
	}
	
	/**
	 * Mini 가젯 HTML을 생성한다.
	 * 
	 * @return jstl ModelAndView
	 */
	@RequestMapping(value="/gadget/bems/SolarPowerMini.do")
	public ModelAndView SolarPowerMonitoringMini() {
		return new ModelAndView("gadget/bems/solarPowerMonitoringMini");
	}
	
	/**
	 * Max 가젯 HTML을 생성한다.
	 * 
	 * @return jstl ModelAndView
	 */
	@RequestMapping(value="/gadget/bems/SolarPowerMax.do")
	public ModelAndView SolarPowerMonitoringMax() {
		Code solarPowerMeterCode = CommonConstants.getMeterTypeByName(SolarPowerMeter.class.getSimpleName());
		return new ModelAndView("gadget/bems/solarPowerMonitoringMax")
			.addObject("meterCode", solarPowerMeterCode.toJSONString());
	}
	
	
	
	/**
	 * 현재 날씨를 기상청에서 얻어온다.
	 * 
	 * @return json ModelAndView
	 */
	@RequestMapping(value="/gadget/bems/getCurrentWeather.do")
	public ModelAndView getCurrentWeather(String location) {
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			WeatherLocation wl = WeatherLocation.getByLocation(location);
			Weather weather = KmaWeatherUtil.getWeather(wl);
			return mav.addObject("weather", weather);
		}
		catch (Exception e) {
			handleException(e, mav);
			return mav;
		}
	}
	
	/**
	 * 현재 제주 중문동의 날씨를 기상청에서 얻어온다.
	 * 
	 * @see this.getCurrentWeather
	 * 
	 * @return json ModelAndView
	 */
	@RequestMapping(value="/gadget/bems/getCurrentJejuJungMunWeather.do")
	public ModelAndView getCurrentJejuJungMunWeather() {
		return getCurrentWeather(WeatherLocation.JungMunDong.getName());
	}
	
	/**
	 * 발전 정보를 얻는다.
	 * current key value는 금일 현재 발전량,
	 * accumated key value는 금일 누적 발전량,
	 * hourly key value는 금일 시간별 발전량이다.
	 * 
	 * 모든 값은 5자리까지의 Double 형이다.
	 * 
	 * @return json ModelAndView
	 */
	@RequestMapping(value="/gadget/bems/getGenerationInfo.do")
	public ModelAndView getGenerationInfo() {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Double> info = 
			powerGenerationManager.getGenerationInfo(MeterType.SolarPowerMeter, null);
		
		return mav.addObject("current", info.remove("current"))
			.addObject("accumated", info.remove("total"))
			.addObject("hourly", info);
	}
	
	/**
	 * 인버터별 발전량을 얻는다.
	 * 
	 * @param today 검색일. 주어지지 않으면 오늘로 고정
	 * @return json ModelAndView
	 * 
	 */
	@RequestMapping(value="/gadget/bems/generationValueByInverter.do")
	public ModelAndView generationValueByInverter(
		@RequestParam int supplierId,
		@RequestParam(required=false) String today) {
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> condition = new HashMap<String, Object>();
		if(today == null || today.isEmpty()) {
			try {
				today = TimeUtil.getCurrentDay();
			}
			catch (ParseException cannot) {
				today = "19700101";
			}
		}
		condition.put("today", today);
		
		List<Map<String, Object>> result = 
			powerGenerationManager.getGenerationValueAmountByMeter(MeterType.SolarPowerMeter, condition);
		
		return mav.addObject("value", result)
			.addObject(
				"today", 
				ParameterConverter.convertLocaleDate(supplierManager.getSupplier(supplierId), today));
	}
	
	/**
	 * 인버터 정보를 얻는다.
	 * Generics를 사용하여 태양열미터 Class를 파라미터로 줘야 한다.
	 * 
	 * @param supplierId
	 * @param inverterId
	 * @return json ModelAndView
	 */
	@RequestMapping(value="/gadget/bems/getInverters.do")
	public ModelAndView getInverters(
		@RequestParam Integer supplierId,
		@RequestParam(required=false) String inverterId ) {
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("mdsId", inverterId);
		condition.put("supplierId", supplierId);
		condition.put("tag", "inverter");
		
		List<SolarPowerMeter> sm = meterManager.getSpecificMeterList(
			condition, SolarPowerMeter.class
		);
		
		return mav.addObject("inverters", sm);
	}
	
	/**
	 * 태양열 발전량 검침 결과 데이터 얻기
	 * MvmMaxController의 메서드를 그대로 가져와 파라미터 몇개만 삭제하여 구현
	 * 
	 * @param supplierId 공급자 아이디
	 * @param start 시작 인덱스
	 * @param limit 시작 인덱스부터 읽을 데이터 양
	 * @param mdsId 미터 아이디
	 * @param meteringSF 검침 성공 여부
	 * @param searchDateType 날자 검색 타입
	 * @param searchDate 날자 검색일
	 * @param meterName 미터 이름
	 * 
	 * @return json ModelAndView
	 */
	@RequestMapping(value="/gadget/bems/getElectricGenerationAmounts.do")
	public ModelAndView getElectricGenerationAmounts(
		@RequestParam Integer supplierId,		
		@RequestParam(required=false) Integer start,
		@RequestParam(required=false) Integer limit,
		@RequestParam(required=false) String inverterId,
		@RequestParam(required=false) String meteringSF,
		@RequestParam(required=false) String searchDateType,
		@RequestParam(required=false) String searchDate,
		@RequestParam(required=false) String inverterName,
		@RequestParam(required=false) Integer isDetail) {
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		
		conditionMap.put("supplierId", supplierId);
		conditionMap.put("meteringSF", (meteringSF == null) ? "s" : meteringSF);
        conditionMap.put("meterType", ChangeMeterTypeName.SPM.getCode());
        conditionMap.put("tlbType", MeterType.SolarPowerMeter.getLpClassName());
        
        if(inverterId != null && !inverterId.isEmpty()) {
        	conditionMap.put("mdevId", inverterId);
        }
        if(inverterName != null && !inverterName.isEmpty()) {
        	conditionMap.put("friendlyName", inverterName);
        }
       
        if(StringUtil.nullToBlank(searchDateType).isEmpty()){
			searchDateType = "DAILY";
		}
        
        DateType dateType = DateType.valueOf(searchDateType.toUpperCase());
        
        String dates [] = null;
        
        if(isDetail == null || isDetail != 1){
        	dates = ParameterConverter.convertAtSignStringToDate(searchDate, dateType, 1, DateType.MONTHLY);	  
        }else if(isDetail == 1){
        	dates = ParameterConverter.getCovertSearchDateToString(searchDate);	
        	conditionMap.put("searchStartHour","00");
        	conditionMap.put("searchEndHour","23");
        }
    	conditionMap.put("searchStartDate", dates[0]);
    	conditionMap.put("searchEndDate", dates[1]);
        int p[] = ParameterConverter.adjustPagingParameter(
        	((start == null) ? 0 : start), ((limit == null) ? 10 : limit)
        );
        
		conditionMap.put("page", p[0]);
		conditionMap.put("limit", p[1]);		
		
		List<Map<String, Object>> result = null;
        Integer totalCount = null;
        
		switch(dateType) {
	        case HOURLY:
	            result = searchMeteringDataManager.getMeteringDataHourlyData(conditionMap);
	            totalCount = searchMeteringDataManager.getMeteringDataHourlyDataTotalCount(conditionMap);
	            break;
	        case DAILY:
	            result = searchMeteringDataManager.getMeteringDataDailyData(conditionMap);
	            totalCount = searchMeteringDataManager.getMeteringDataDailyDataTotalCount(conditionMap);
	            break;
	        case WEEKLY:
	            result = searchMeteringDataManager.getMeteringDataWeeklyData(conditionMap);
	            totalCount = searchMeteringDataManager.getMeteringDataWeeklyDataTotalCount(conditionMap);
	            break;
	        case MONTHLY:
	            result = searchMeteringDataManager.getMeteringDataMonthlyData(conditionMap);
	            totalCount = searchMeteringDataManager.getMeteringDataMonthlyDataTotalCount(conditionMap);
	            break;
	        case WEEKDAILY:
	            result = searchMeteringDataManager.getMeteringDataWeekDailyData(conditionMap);
	            totalCount = searchMeteringDataManager.getMeteringDataWeekDailyDataTotalCount(conditionMap);
	            break;
	        case SEASONAL:
	            result = searchMeteringDataManager.getMeteringDataSeasonalData(conditionMap);
	            totalCount = searchMeteringDataManager.getMeteringDataSeasonalDataTotalCount(conditionMap);
	            break;
	        case YEARLY:
	            result = searchMeteringDataManager.getMeteringDataYearlyData(conditionMap);
	            totalCount = searchMeteringDataManager.getMeteringDataYearlyDataTotalCount(conditionMap);
	            break;
	    }
	
	    mav.addObject("result", result);
	    mav.addObject("totalCount", totalCount);
		return mav;
	}

	/**
	 * 
	 * 새 인버터를 얻는다.
	 * MeterController의 메서드를 빌려 썼다.
	 * 
	 * @param solarPowerMeter 태양열 미터 폼 Value
	 * @return json ModelAndView
	 */
	@RequestMapping(value="/gadget/bems/addNewInverter.do")
	public ModelAndView addNewInverter(
		@ModelAttribute("meterInfoFormEdit") SolarPowerMeter solarPowerMeter) {
		return meterController.insertSolarPowerMeter(solarPowerMeter);
	}
	
	/**
	 * 인버터 발전량 통계를 계산하여 얻는다.
	 * 
	 * @return json ModelAndView
	 */
	@RequestMapping(value="/gadget/bems/getElectricGenerationStatistics.do")
	public ModelAndView getElectricGenerationStatistics(
		@RequestParam("supplierId") String supplierId,
		@RequestParam(required=false) Integer inverterId,
		@RequestParam(required=false) String searchDate) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
		if(inverterId != null) {
			condition.put("meterId", inverterId);
		}
		
		condition.put("supplierId", supplierId);
		condition.put("searchDate", searchDate);
		condition.put("dayLimit", 19);
		
		Map<String, List<Map<String, String>>> result = 
			powerGenerationManager.getStatistics(MeterType.SolarPowerMeter, condition);
		
		return mav.addObject("result", result);
	}
}