package com.aimir.bo.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.service.device.BatteryLogManager;

@Controller
public class BatteryLogController {

	@Autowired
	BatteryLogManager batteryLogManager;
	
	@RequestMapping(value="/gadget/device/batteryLogMiniGadget")
    public ModelAndView batteryLogMiniGadget() {
		ModelAndView mav = new ModelAndView("gadget/device/batteryLogMiniGadget");
		List<Object> result = batteryLogManager.getModemTypeCombo();
		mav.addObject("combo", result);
		return mav;
    }

	@RequestMapping(value="/gadget/device/batteryLogMaxGadget")
    public ModelAndView batteryLogMaxGadget() {
		ModelAndView mav = new ModelAndView("gadget/device/batteryLogMaxGadget");
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
		List<Object> modemType = batteryLogManager.getModemTypeCombo();
		mav.addObject("modemType", modemType);
		List<Map<String, Object>> powerType = batteryLogManager.getPowerTypeCombo();
		mav.addObject("powerType", powerType);
//		List<Object> batteryStatus = batteryLogManager.getBatteryStatusCombo();
//		mav.addObject("batteryStatus", batteryStatus);
		return mav;
    }
	
	// MeterMini Gadget 조회 / Chart,Grid 동일 데이터 사용
	@RequestMapping(value="/gadget/device/getBatteryLog")
	public ModelAndView getBatteryLog(@RequestParam(value="supplierId")   String supplierId
											, @RequestParam(value="modemType") String modemType
											, @RequestParam(value="modemTypeName") String modemTypeName) {
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
        condition.put("supplierId"   , supplierId);
        condition.put("modemType" , modemType);
        condition.put("modemTypeName" , modemTypeName);
        condition.put("modemId" , null);
        condition.put("powerType" , null);
        condition.put("meterLocation" , null);
        condition.put("batteryStatus" , null);
        condition.put("batteryVoltSign" , null);
        condition.put("batteryVolt" , null);
        condition.put("operatingDaySign" , null);
        condition.put("operatingDay" , null);
        
    	ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("result", batteryLogManager.getBatteryLog(condition));
        
        return mav;
    }
	
	@RequestMapping(value="/gadget/device/getBatteryVoltageGridData")
	public ModelAndView getBatteryVoltageData(@RequestParam(value="supplierId") String supplierId
												, @RequestParam(value="modemId") String modemId
												, @RequestParam(value="modemType") String modemType
												, @RequestParam(value="modemTypeName") String modemTypeName
												, @RequestParam(value="powerType") String powerType
												, @RequestParam(value="meterLocation") String meterLocation
												, @RequestParam(value="batteryStatus") String batteryStatus
												, @RequestParam(value="batteryVoltSign") String batteryVoltSign
												, @RequestParam(value="batteryVolt") String batteryVolt
												, @RequestParam(value="operatingDaySign") String operatingDaySign
												, @RequestParam(value="operatingDay") String operatingDay
												, @RequestParam(value="page") Integer page
												, @RequestParam(value="pageSize") Integer pageSize) {
		
		Map<String, Object> condition = new HashMap<String, Object>();
		Map<String, Object> tocondition = new HashMap<String, Object>();
		
        condition.put("supplierId"   , supplierId);
        condition.put("modemType" , modemType);
        condition.put("modemTypeName" , modemTypeName);
        condition.put("modemId" , modemId);
        condition.put("powerType" , powerType);
        condition.put("meterLocation" , meterLocation);
        condition.put("batteryStatus" , batteryStatus);
        condition.put("batteryVoltSign" , batteryVoltSign);
        condition.put("batteryVolt" , batteryVolt);
        condition.put("operatingDaySign" , operatingDaySign);
        condition.put("operatingDay" , operatingDay);
        condition.put("page" , page);
        condition.put("pageSize" , pageSize);
        
        tocondition.put("supplierId"   , supplierId);
        tocondition.put("modemType" , modemType);
        tocondition.put("modemTypeName" , modemTypeName);
        tocondition.put("modemId" , modemId);
        tocondition.put("powerType" , powerType);
        tocondition.put("meterLocation" , meterLocation);
        tocondition.put("batteryStatus" , batteryStatus);
        tocondition.put("batteryVoltSign" , batteryVoltSign);
        tocondition.put("batteryVolt" , batteryVolt);
        tocondition.put("operatingDaySign" , operatingDaySign);
        tocondition.put("operatingDay" , operatingDay);
        tocondition.put("page" , page);
        tocondition.put("pageSize" , pageSize);
       
       
    	ModelAndView mav = new ModelAndView("jsonView");

    	Map<String, Object> totalcount = batteryLogManager.getBatteryLogListTotalCount(tocondition); 
        mav.addObject("griddata", batteryLogManager.getBatteryVoltageLogList(condition));
        mav.addObject("total",totalcount.get("total"));
        
        return mav;
		
	}
	@RequestMapping(value="/gadget/device/getBatteryLogList")
	public ModelAndView getBatteryLogList(@RequestParam(value="supplierId")   String supplierId
											, @RequestParam(value="modemType") String modemType
											, @RequestParam(value="modemTypeName") String modemTypeName
											, @RequestParam(value="modemId") String modemId
											, @RequestParam(value="powerType") String powerType
											, @RequestParam(value="meterLocation") String meterLocation
											, @RequestParam(value="batteryStatus") String batteryStatus
											, @RequestParam(value="batteryVoltSign") String batteryVoltSign
											, @RequestParam(value="batteryVolt") String batteryVolt
											, @RequestParam(value="operatingDaySign") String operatingDaySign
											, @RequestParam(value="operatingDay") String operatingDay) {
		
		Map<String, Object> condition = new HashMap<String, Object>();
		
        condition.put("supplierId"   , supplierId);
        condition.put("modemType" , modemType);
        condition.put("modemTypeName" , modemTypeName);
        condition.put("modemId" , modemId);
        condition.put("powerType" , powerType);
        condition.put("meterLocation" , meterLocation);
        condition.put("batteryStatus" , batteryStatus);
        condition.put("batteryVoltSign" , batteryVoltSign);
        condition.put("batteryVolt" , batteryVolt);
        condition.put("operatingDaySign" , operatingDaySign);
        condition.put("operatingDay" , operatingDay);
        condition.put("page" , 0);
    
    	ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("result", batteryLogManager.getBatteryLogList(condition));
        
        return mav;
    }
	
	@RequestMapping(value="/gadget/device/getBatteryLogDetailList")
	public ModelAndView getBatteryLogDetailList(@RequestParam(value="supplierId")   String supplierId
											, @RequestParam(value="modemType") String modemType
											, @RequestParam(value="modemId") String modemId
											, @RequestParam(value="dateType") String dateType
											, @RequestParam(value="fromDate") String fromDate
											, @RequestParam(value="toDate") String toDate) {
		
		Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplierId"   , supplierId);
        condition.put("modemType" , modemType);
        condition.put("modemId" , modemId);
        condition.put("dateType" , dateType);
        condition.put("fromDate" , fromDate);
        condition.put("toDate" , toDate);
        condition.put("searchType" , "search");
        condition.put("page" , 0);

		ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("result",batteryLogManager.getBatteryLogDetailList(condition));
        return mav;
		
	}
	
	@RequestMapping(value="/gadget/device/getBatteryLogDetailDataList")
	public ModelAndView getBatteryLogDetailDataList(@RequestParam(value="supplierId")   String supplierId
											, @RequestParam(value="modemType") String modemType
											, @RequestParam(value="modemId") String modemId
											, @RequestParam(value="dateType") String dateType
											, @RequestParam(value="fromDate") String fromDate
											, @RequestParam(value="toDate") String toDate) {
		
		Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplierId"   , supplierId);
        condition.put("modemType" , modemType);
        condition.put("modemId" , modemId);
        condition.put("dateType" , dateType);
        condition.put("fromDate" , fromDate);
        condition.put("toDate" , toDate);
        condition.put("searchType" , "search");
        condition.put("page" , 0);
        
		Map<String, Object> tocondition = new HashMap<String, Object>();
		tocondition.put("supplierId"   , supplierId);
		tocondition.put("modemType" , modemType);
		tocondition.put("modemId" , modemId);
		tocondition.put("dateType" , dateType);
		tocondition.put("fromDate" , fromDate);
		tocondition.put("toDate" , toDate);
		tocondition.put("searchType" , "search");
		tocondition.put("page" , 0);
		
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> resultList = batteryLogManager.getBatteryLogDetailList(condition); 
        mav.addObject("griddata", resultList.get("grid"));
        Map<String, Object> totalcount = batteryLogManager.getBatteryLogDetailListTotalCount(tocondition); 
        mav.addObject("total",totalcount.get("total"));

        return mav;
		
	}
	@RequestMapping(value="/gadget/device/getBatteryLogNavigatePage")
	public ModelAndView getBatteryLogNavigatePage(@RequestParam(value="supplierId")   String supplierId
											, @RequestParam(value="modemType") String modemType
											, @RequestParam(value="modemId") String modemId
											, @RequestParam(value="dateType") String dateType
											, @RequestParam(value="fromDate") String fromDate
											, @RequestParam(value="toDate") String toDate
											, @RequestParam(value="page") String page
											, @RequestParam(value="pageSize", required=false) String pageSize) {

		Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplierId", supplierId);
        condition.put("modemType", modemType);
        condition.put("modemId", modemId);
        condition.put("dateType", dateType);
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("searchType", "page");
        condition.put("page", Integer.parseInt(page));
        if (pageSize != null) {
            condition.put("pageSize", Integer.parseInt(pageSize));
        }

		ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("result", batteryLogManager.getBatteryLogDetailList(condition));

        return mav;
	}
}