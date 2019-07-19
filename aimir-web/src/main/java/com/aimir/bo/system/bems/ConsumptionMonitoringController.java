package com.aimir.bo.system.bems;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Code;
import com.aimir.model.system.Location;
import com.aimir.model.system.Operator;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ConsumptionMonitoringEmManager;
import com.aimir.service.system.ConsumptionMonitoringGmManager;
import com.aimir.service.system.ConsumptionMonitoringWmManager;
import com.aimir.service.system.ConsumptionMonitoringHmManager;
import com.aimir.service.system.EnergySavingGoal2Manager;
import com.aimir.service.system.LocationManager;
import com.aimir.util.BemsStatisticUtil;

@Controller
public class ConsumptionMonitoringController {

	@Autowired
	ConsumptionMonitoringEmManager consumptionMonitoringEmManager;

	@Autowired
	ConsumptionMonitoringGmManager consumptionMonitoringGmManager;
	
	@Autowired
	ConsumptionMonitoringWmManager consumptionMonitoringWmManager;
	
	@Autowired
	ConsumptionMonitoringHmManager consumptionMonitoringHmManager;
	
	@Autowired
	EnergySavingGoal2Manager energySavingGoal2Manager;
	
	@Autowired
	LocationManager locationManager;
	
	@Autowired
	CodeManager codeManager;
	
	@RequestMapping(value="/gadget/bems/consumptionMonitoringEmMaxGadget")
    public ModelAndView consumptionMonitoringEmMaxGadget(HttpServletRequest request, HttpServletResponse response) {
		
		ModelAndView mav = new ModelAndView();
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        
        if(user!=null && !user.isAnonymous()) {
	        try {
	        	mav.addObject("userId", user.getOperator(new Operator()).getId());
	        	mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
        mav.setViewName("gadget/bems/consumptionMonitoringEmMaxGadget");
        
        return mav;
//    	return new ModelAndView("gadget/bems/consumptionMonitoringEmMaxGadget");
    }

	@RequestMapping(value="/gadget/bems/consumptionMonitoringEmMiniGadget")
    public ModelAndView consumptionMonitoringEmMiniGadget(HttpServletRequest request, HttpServletResponse response) {
		
		ModelAndView mav = new ModelAndView();
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        
        if(user!=null && !user.isAnonymous()) {
	        try {
	        	mav.addObject("userId", user.getOperator(new Operator()).getId());
	        	mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
        mav.setViewName("gadget/bems/consumptionMonitoringEmMiniGadget");
        
        return mav;
//    	return new ModelAndView("gadget/bems/consumptionMonitoringEmMiniGadget");
    }
	
	@RequestMapping(value="/gadget/bems/consumptionMonitoringGmMaxGadget")
    public ModelAndView consumptionMonitoringGmMaxGadget(HttpServletRequest request, HttpServletResponse response) {
		
		ModelAndView mav = new ModelAndView();
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        
        if(user!=null && !user.isAnonymous()) {
	        try {
	        	mav.addObject("userId", user.getOperator(new Operator()).getId());
	        	mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
        mav.setViewName("gadget/bems/consumptionMonitoringGmMaxGadget");
        
        return mav;
//    	return new ModelAndView("gadget/bems/consumptionMonitoringGmMaxGadget");
    }

	@RequestMapping(value="/gadget/bems/consumptionMonitoringGmMiniGadget")
    public ModelAndView consumptionMonitoringGmMiniGadget(HttpServletRequest request, HttpServletResponse response) {
		
		ModelAndView mav = new ModelAndView();
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        
        if(user!=null && !user.isAnonymous()) {
	        try {
	        	mav.addObject("userId", user.getOperator(new Operator()).getId());
	        	mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
        mav.setViewName("gadget/bems/consumptionMonitoringGmMiniGadget");
        
        return mav;
//    	return new ModelAndView("gadget/bems/consumptionMonitoringGmMiniGadget");
    }
	
	@RequestMapping(value="/gadget/bems/consumptionMonitoringWmMaxGadget")
    public ModelAndView consumptionMonitoringWmMaxGadget(HttpServletRequest request, HttpServletResponse response) {
		
		ModelAndView mav = new ModelAndView();
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        
        if(user!=null && !user.isAnonymous()) {
	        try {
	        	mav.addObject("userId", user.getOperator(new Operator()).getId());
	        	mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
        mav.setViewName("gadget/bems/consumptionMonitoringWmMaxGadget");
        
        return mav;
//    	return new ModelAndView("gadget/bems/consumptionMonitoringWmMaxGadget");
    }

	@RequestMapping(value="/gadget/bems/consumptionMonitoringWmMiniGadget")
    public ModelAndView consumptionMonitoringWmMiniGadget(HttpServletRequest request, HttpServletResponse response) {
		
		ModelAndView mav = new ModelAndView();
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        
        if(user!=null && !user.isAnonymous()) {
	        try {
	        	mav.addObject("userId", user.getOperator(new Operator()).getId());
	        	mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
        mav.setViewName("gadget/bems/consumptionMonitoringWmMiniGadget");
        
        return mav;
//    	return new ModelAndView("gadget/bems/consumptionMonitoringWmMiniGadget");
    }
	
	@RequestMapping(value="/gadget/bems/consumptionMonitoringHmMaxGadget")
    public ModelAndView consumptionMonitoringHmMaxGadget(HttpServletRequest request, HttpServletResponse response) {
		
		ModelAndView mav = new ModelAndView();
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        
        if(user!=null && !user.isAnonymous()) {
	        try {
	        	mav.addObject("userId", user.getOperator(new Operator()).getId());
	        	mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
        mav.setViewName("gadget/bems/consumptionMonitoringHmMaxGadget");
        
        return mav;
//    	return new ModelAndView("gadget/bems/consumptionMonitoringHmMaxGadget");
    }

	@RequestMapping(value="/gadget/bems/consumptionMonitoringHmMiniGadget")
    public ModelAndView consumptionMonitoringHmMiniGadget(HttpServletRequest request, HttpServletResponse response) {
		
		ModelAndView mav = new ModelAndView();
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        
        if(user!=null && !user.isAnonymous()) {
	        try {
	        	mav.addObject("userId", user.getOperator(new Operator()).getId());
	        	mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
        mav.setViewName("gadget/bems/consumptionMonitoringHmMiniGadget");
        
        return mav;
//    	return new ModelAndView("gadget/bems/consumptionMonitoringHmMiniGadget");
    }
	
	/**
	 * supplierId를 이용하여 location의 Root id를 구한다.
	 * @param searchDateType
	 * @param supplierId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value="/gadget/bems/getRootLocationId")
    public ModelAndView getRootLocationId( @RequestParam("supplierId") String supplierId) {
		ModelAndView mav = new ModelAndView("jsonView");
		
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplierId"		, supplierId);

        Map<String, Object> result = consumptionMonitoringEmManager.getRootLocationId(condition);
        mav.addObject("rootLocation", result.get("rootLocation"));
        
    	return mav;
    }
	
	@RequestMapping(value="/gadget/bems/getTotalUseOfSearchType")
	public ModelAndView getTotalUseOfSearchType( @RequestParam("supplierId") String supplierId 
												, @RequestParam("locationId") String locationId
												, @RequestParam("searchDateType") String searchDateType
												, @RequestParam("energyType") String energyType
												, @RequestParam("meterTypeCode") String meterTypeCode) {
		ModelAndView mav = new ModelAndView("jsonView");
		
		String energyName = "EnergyMeter";
		
		if("EM".equalsIgnoreCase(energyType)){
			energyName = "EnergyMeter";
		}else if("GM".equalsIgnoreCase(energyType)){
			energyName = "GasMeter";
		}else if("WM".equalsIgnoreCase(energyType)){
			energyName = "WaterMeter";
		}else if("HM".equalsIgnoreCase(energyType)){
			energyName = "HeatMeter";
		}
	
		Code parentCode = codeManager.getCodeByName("MeterType") ;
		
		Map<String,Object> conditionMap = new HashMap<String,Object>();
		conditionMap.put("name", energyName);
		conditionMap.put("parentCodeId", parentCode.getId());
		Code code = codeManager.getCodeByCondition(conditionMap);
		
		Map<String, String> condition = new HashMap<String, String>();
		condition.put("supplierId"		, supplierId);
		condition.put("locationId"		, locationId);
		condition.put("searchDateType"	, searchDateType);
		condition.put("energyType"	, energyType);
		condition.put("meterTypeCode"	, code == null ? "1.3.1.1" : code.getCode());
		
		
		Map<String, Object> result = new HashMap<String, Object>();	
		
		if("EM".equalsIgnoreCase(energyType)){
		result = consumptionMonitoringEmManager.getTotalUseOfSearchType((HashMap<String, String>)condition);
		}else if("GM".equalsIgnoreCase(energyType)){
		result = consumptionMonitoringGmManager.getTotalUseOfSearchType(condition);
		}else if("WM".equalsIgnoreCase(energyType)){
		result = consumptionMonitoringWmManager.getTotalUseOfSearchType(condition);
		}else if("HM".equalsIgnoreCase(energyType)){
			result = consumptionMonitoringHmManager.getTotalUseOfSearchType(condition);
		}
		
		Map<String, Object> result2 = new HashMap<String, Object>();
		result2 = energySavingGoal2Manager.getAvgByUsed(condition);
		
		BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
		NumberFormat formatter = new DecimalFormat(bemsUtil.getNumberFormat());
		      
		mav.addObject("totalUse",  formatter.format((Double)result.get("totalUse") ) );
		mav.addObject("totalCo2Use",  formatter.format((Double)result.get("totalCo2Use") ) );
		mav.addObject("averageUsage", formatter.format( (Double)result2.get("averageUsage") ) );
		mav.addObject("averageCo2Usage", formatter.format( (Double)result2.get("averageCo2Usage") ) );
		
		return mav;
	}

	@RequestMapping(value="/gadget/bems/getTotalUseOfSearchTypeEm")
	public ModelAndView getTotalUseOfSearchTypeEm( @RequestParam("supplierId") String supplierId 
												, @RequestParam("locationId") String locationId
												, @RequestParam("searchDateType") String searchDateType ) {
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, String> condition = new HashMap<String, String>();
		condition.put("supplierId"		, supplierId);
		condition.put("locationId"		, locationId);
		condition.put("searchDateType"	, searchDateType);
		condition.put("energyType"	, "EM");
		condition.put("meterTypeCode"	, "1.3.1.1");
		
		
		Map<String, Object> result = new HashMap<String, Object>();		
		result = consumptionMonitoringEmManager.getTotalUseOfSearchType((HashMap<String, String>)condition);
		
		Map<String, Object> result2 = new HashMap<String, Object>();
		result2 = energySavingGoal2Manager.getDayMonthRangeAvgByUsed(condition);
		
		BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
		NumberFormat formatter = new DecimalFormat(bemsUtil.getNumberFormat());
		      
		mav.addObject("totalUse",  formatter.format((Double)result.get("totalUse") ) );
		mav.addObject("totalCo2Use",  formatter.format((Double)result.get("totalCo2Use") ) );
		mav.addObject("averageUsage", formatter.format( (Double)result2.get("averageUsage") ) );
		mav.addObject("averageCo2Usage", formatter.format( (Double)result2.get("averageCo2Usage") ) );
		
		return mav;
	}
	
	@RequestMapping(value="/gadget/bems/getTotalUseOfSearchTypeGm")
	public ModelAndView getTotalUseOfSearchTypeGm( @RequestParam("supplierId") String supplierId 
												, @RequestParam("locationId") String locationId
												, @RequestParam("searchDateType") String searchDateType ) {
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, String> condition = new HashMap<String, String>();
		condition.put("supplierId"		, supplierId);
		condition.put("locationId"		, locationId);
		condition.put("searchDateType"	, searchDateType);
		condition.put("energyType"	, "GM");
		condition.put("meterTypeCode"	, "1.3.1.3");
		
		Map<String, Object> result = new HashMap<String, Object>();		
		result = consumptionMonitoringGmManager.getTotalUseOfSearchType(condition);
		
		Map<String, Object> result2 = new HashMap<String, Object>();
		result2 = energySavingGoal2Manager.getDayMonthRangeAvgByUsed(condition);
		
		BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
		NumberFormat formatter = new DecimalFormat(bemsUtil.getNumberFormat());
		
		mav.addObject("totalUse", formatter.format( (Double)result.get("totalUse") ) );
		mav.addObject("totalCo2Use", formatter.format( (Double)result.get("totalCo2Use") ) );
		mav.addObject("averageUsage", formatter.format( (Double)result2.get("averageUsage") ) );
		mav.addObject("averageCo2Usage", formatter.format( (Double)result2.get("averageCo2Usage") ) );
		
		return mav;
	}
	
	@RequestMapping(value="/gadget/bems/getTotalUseOfSearchTypeWm")
	public ModelAndView getTotalUseOfSearchTypeWm( @RequestParam("supplierId") String supplierId 
												, @RequestParam("locationId") String locationId
												, @RequestParam("searchDateType") String searchDateType ) {
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, String> condition = new HashMap<String, String>();
		condition.put("supplierId"		, supplierId);
		condition.put("locationId"		, locationId);
		condition.put("searchDateType"	, searchDateType);
		condition.put("energyType"	, "WM");
		condition.put("meterTypeCode"	, "1.3.1.2");
		
		Map<String, Object> result = new HashMap<String, Object>();		
		result = consumptionMonitoringWmManager.getTotalUseOfSearchType(condition);
		
		Map<String, Object> result2 = new HashMap<String, Object>();
		result2 = energySavingGoal2Manager.getDayMonthRangeAvgByUsed(condition);
		
		BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
		NumberFormat formatter = new DecimalFormat(bemsUtil.getNumberFormat());
		
		mav.addObject("totalUse", formatter.format( (Double)result.get("totalUse") ) );
		mav.addObject("totalCo2Use", formatter.format( (Double)result.get("totalCo2Use") ) );
		mav.addObject("averageUsage", formatter.format( (Double)result2.get("averageUsage") ) );
		mav.addObject("averageCo2Usage", formatter.format( (Double)result2.get("averageCo2Usage") ) );
		
		return mav;
	}
	
	@RequestMapping(value="/gadget/bems/getTotalUseOfSearchTypeHm")
	public ModelAndView getTotalUseOfSearchTypeHm( @RequestParam("supplierId") String supplierId 
												, @RequestParam("locationId") String locationId
												, @RequestParam("searchDateType") String searchDateType ) {
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, String> condition = new HashMap<String, String>();
		condition.put("supplierId"		, supplierId);
		condition.put("locationId"		, locationId);
		condition.put("searchDateType"	, searchDateType);
		condition.put("energyType"	, "HM");
		condition.put("meterTypeCode"	, "1.3.1.4");
		
		
		Map<String, Object> result = new HashMap<String, Object>();		
		result = consumptionMonitoringHmManager.getTotalUseOfSearchType((HashMap<String, String>)condition);
		
		Map<String, Object> result2 = new HashMap<String, Object>();
		result2 = energySavingGoal2Manager.getDayMonthRangeAvgByUsed(condition);
		
		BemsStatisticUtil bemsUtil = new BemsStatisticUtil();
		NumberFormat formatter = new DecimalFormat(bemsUtil.getNumberFormat());
		      
		mav.addObject("totalUse",  formatter.format((Double)result.get("totalUse") ) );
		mav.addObject("totalCo2Use",  formatter.format((Double)result.get("totalCo2Use") ) );
		mav.addObject("averageUsage", formatter.format( (Double)result2.get("averageUsage") ) );
		mav.addObject("averageCo2Usage", formatter.format( (Double)result2.get("averageCo2Usage") ) );
		
		return mav;
	}
	
	@RequestMapping(value="/gadget/bems/getLocationInfo")
	public ModelAndView getLocationInfo( @RequestParam("locationId") String locationId  ) {
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, String> condition = new HashMap<String, String>();
		condition.put("locationId"		, locationId);
		
		Location result = new Location();
		
		result = locationManager.getLocation(Integer.parseInt( locationId ) );
		
		mav.addObject("name", result.getName() );
		if( result.getParent() == null ){
			
			mav.addObject("parentId", "" );
		}else{
			
			mav.addObject("parentId", ObjectUtils.defaultIfNull( result.getParent().getId() , "" ) );
		}
		
		return mav;
	}
	
	
	@RequestMapping(value="/gadget/bems/getBuildingLookUp")
	public ModelAndView getBuildingLookUp( @RequestParam("searchDateType") String searchDateType,
			@RequestParam("supplierId") String supplierId,@RequestParam("locationId") Integer locationId,
			@RequestParam("detailLocationId") Integer detailLocationId,@RequestParam("energyType") String energyType) {
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("searchDateType", searchDateType);
		condition.put("supplierId", supplierId);
		condition.put("locationId", locationId);
		condition.put("detailLocationId", detailLocationId);
		
		
		Map<String, Object> result = null;
		if("EM".equalsIgnoreCase(energyType)){
			result =consumptionMonitoringEmManager.getBuildingLookUpEm(condition);
		}else if("GM".equalsIgnoreCase(energyType)){
			result =consumptionMonitoringGmManager.getBuildingLookUpGm(condition);
		}else if("WM".equalsIgnoreCase(energyType)){
			result =consumptionMonitoringWmManager.getBuildingLookUpWm(condition);
		}else if("HM".equalsIgnoreCase(energyType)){
			result= consumptionMonitoringHmManager.getBuildingLookUpHm(condition);
		}	
		
		mav.addObject("currentDateTime", result.get("currentDateTime"));
		mav.addObject("grid", result.get("grid")); // 동별 전력/탄소 값의 합.
		mav.addObject("sumGrid", result.get("sumGrid")); // 빌딩 전체 전력사용량/탄솝출량 의
		// 최대값 ,최소값,합
		mav.addObject("sumTHGrid", result.get("sumTHGrid")); // 빌딩 전체 전력온도량/습도량 의
		// 합
		mav.addObject("returnLocation", result.get("returnLocation")); // 동정보
		mav.addObject("TM_MAX", result.get("TM_MAX"));
		mav.addObject("TM_MIN", result.get("TM_MIN"));
	
		return mav;
	}
	
	@RequestMapping(value="/gadget/bems/getBuildingLookUpMax")
	public ModelAndView getBuildingLookUpMax( @RequestParam("searchDateType") String searchDateType,
			@RequestParam("supplierId") String supplierId,@RequestParam("locationId") Integer locationId,
			@RequestParam("detailLocationId") Integer detailLocationId,@RequestParam("energyType") String energyType) {
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("searchDateType", searchDateType);
		condition.put("supplierId", supplierId);
		condition.put("locationId", locationId);
		condition.put("detailLocationId", detailLocationId);
		
		
		Map<String, Object> result = null;
		if("EM".equalsIgnoreCase(energyType)){
			result =consumptionMonitoringEmManager.getBuildingLookUpMaxEm(condition);
		}else if("GM".equalsIgnoreCase(energyType)){
			result =consumptionMonitoringGmManager.getBuildingLookUpMaxGm(condition);
		}else if("WM".equalsIgnoreCase(energyType)){
			result =consumptionMonitoringWmManager.getBuildingLookUpMaxWm(condition);
		}else if("HM".equalsIgnoreCase(energyType)){
			result =consumptionMonitoringHmManager.getBuildingLookUpMaxHm(condition);
		}		
		
		mav.addObject("currentDateTime", result.get("currentDateTime"));

		mav.addObject("myChartDataLocation", result.get("myChartDataLocation")); 
		mav.addObject("myChartDataDay", result.get("myChartDataDay"));
		mav.addObject("myChartDataDayInfo", result.get("myChartDataDayInfo"));
		mav.addObject("myChartDataWeek", result.get("myChartDataWeek"));
		mav.addObject("myChartDataWeekInfo", result.get("myChartDataWeekInfo"));
		mav.addObject("myChartDataMonth", result.get("myChartDataMonth"));
		mav.addObject("myChartDataMonthInfo", result.get("myChartDataMonthInfo"));
		mav.addObject("myChartDataQuarter", result.get("myChartDataQuarter"));
		mav.addObject("myChartDataQuarterInfo", result.get("myChartDataQuarterInfo"));
		mav.addObject("returnLocation", result.get("returnLocation")); 
		
		return mav;
	}
	
	@RequestMapping(value="/gadget/bems/thisLocationLevel")
	public ModelAndView thisLocationLevel(@RequestParam("locationId") Integer locationId) {
		ModelAndView mav = new ModelAndView("jsonView");		
		Integer level = consumptionMonitoringEmManager.thisLocationLevel(locationId);		
		
		mav.addObject("level", level); 
	
		return mav;
	}
	
}
