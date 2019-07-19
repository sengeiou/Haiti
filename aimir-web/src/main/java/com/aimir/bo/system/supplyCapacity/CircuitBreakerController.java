package com.aimir.bo.system.supplyCapacity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.CircuitBreakerCondition;
import com.aimir.constants.CommonConstants.CircuitBreakerStatus;
import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.CircuitBreakerLog;
import com.aimir.model.device.CircuitBreakerSetting;
import com.aimir.model.system.Role;
import com.aimir.service.system.CircuitBreakerManager;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.RoleManager;
import com.aimir.util.CommonUtils;

import edu.emory.mathcs.backport.java.util.Collections;

@Controller
public class CircuitBreakerController {

    @Autowired
    CircuitBreakerManager circuitBreakerManager;

    @Autowired
    ContractManager contractManager;

    @Autowired
    RoleManager roleManager;

	@RequestMapping(value="/gadget/system/supplyCapacityMaxGadget")
    public ModelAndView supplyCapacityMaxGadget() {
        ModelAndView mav = new ModelAndView("gadget/system/supplyCapacityMaxGadget");

        GroupType[] groupTypeArray = GroupType.values();
        List<String> groupTypes = new ArrayList<String>();

        for (GroupType groupType : groupTypeArray) {
            if (groupType == GroupType.Operator)
                continue;
            groupTypes.add(groupType.name());
        }
        Collections.sort(groupTypes);

        Map<String, String> meterStatusMap = new LinkedHashMap<String, String>();
        MeterStatus[] meterStatusArray = MeterStatus.values();

        for (MeterStatus meterStatus : meterStatusArray) {
            meterStatusMap.put(meterStatus.name(), meterStatus.name());
        }

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("groupTypes", groupTypes);
        mav.addObject("meterStatusMap", meterStatusMap);
        mav.addObject("cmdAuth", authMap.get("command")); // Command 실행권한(command = true)

        return mav;
    }
	
	@RequestMapping(value="/gadget/system/supplyCapacityMiniGadget")
	public ModelAndView supplyCapacityMiniGadget() {
	    ModelAndView mav = new ModelAndView("gadget/system/supplyCapacityMiniGadget");
	    AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
	    AimirUser user = (AimirUser) instance.getUserFromSession();

	    Role role = roleManager.getRole(user.getRoleData().getId());
	    Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

	    mav.addObject("cmdAuth", authMap.get("command"));  // Command 실행권한(command = true)

		return mav;
	}	

	@RequestMapping(value="/gadget/system/getElecSupplyCapacityMiniGridData")
	public ModelAndView getElecSupplyCapacityMiniGridData(
			
			@RequestParam("switchStatus") String switchStatus,
			@RequestParam("page") String page,
			@RequestParam("pageSize") String pageSize) {
        
        Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("switchStatus", switchStatus);
		paramMap.put("groupType", "");
		paramMap.put("target", "");
		paramMap.put("condition", "");
		paramMap.put("meterStatus", "");
		paramMap.put("page", page);
		paramMap.put("pageSize", pageSize);
		
		List<Map<String, String>> data = circuitBreakerManager.getElecSupplyCapacityGridData(paramMap);
		paramMap.put("totalCount", data.size() + "");
		List<Map<String, String>> gridData = circuitBreakerManager.getElecSupplyCapacityGridDataCount(data, paramMap);
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("totalCount", data.size() + "");
		mav.addObject("gridData", gridData);
		
		return mav;	
	}		
										  
//	@RequestMapping(value="/gadget/system/saveSupplyCapacityList")
//	public ModelAndView saveSupplayCapacityList(
//			
//			@RequestParam("meterIds") String meterIds,
//			@RequestParam("conditions") String conditions,
//			@RequestParam("targetTypes") String targetTypes,
//			@RequestParam("target") String target,
//			@RequestParam("status") String status) {
//        	
//		Map<String, String> paramMap = new HashMap<String, String>();		
//		paramMap.put("status", status);
//		
//		String[] contractIdArray = contractIds.split(",");
//		
//		for(String contractId : contractIdArray) {
//			
//			paramMap.put("contractId", contractId);
//			circuitBreakerManager.saveSupplyCapacity(paramMap);
//		}
//		
//		return null;	
//	}
	
	@RequestMapping(value="/gadget/system/saveSupplyCapacity")
	public ModelAndView saveSupplayCapacity(			

			@RequestParam("meterId") String meterId,
			@RequestParam("condition") String condition,
			@RequestParam("targetType") String targetType,
			@RequestParam("target") String target,
			@RequestParam("status") String status) {
		
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("meterId", meterId);
		paramMap.put("condition", condition);
		paramMap.put("targetType", targetType);
		paramMap.put("target", target);
		paramMap.put("status", status);
		
		CircuitBreakerStatus circuitStatus = CircuitBreakerStatus.valueOf(status);
		CircuitBreakerCondition circuitCondition = CircuitBreakerCondition.valueOf(condition);
		
		circuitBreakerManager.saveSupplyCapacity(circuitStatus, targetType, circuitCondition , Integer.parseInt(meterId));
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("", "");
		
		return mav;		
	}
	
	@RequestMapping(value="/gadget/system/saveCircuitBreakerSettingPrepayment")
	public ModelAndView saveCircuitBreakerSettingPrepayment(			
			
		@ModelAttribute("prepaymentForm") CircuitBreakerSetting setting) {
		setting.setCondition(CircuitBreakerCondition.Prepayment);
		circuitBreakerManager.saveCircuitBreakerSetting(setting);
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("result", "success");
		
		return mav;	
	}		
	
	@RequestMapping(value="/gadget/system/saveCircuitBreakerSettingExceedsThreshold")
	public ModelAndView saveCircuitBreakerSettingExceedsThreshold(			
			
		@ModelAttribute("exceedsThresholdForm") CircuitBreakerSetting setting) {
		setting.setCondition(CircuitBreakerCondition.ExceedsThreshold);
		circuitBreakerManager.saveCircuitBreakerSetting(setting);
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("result", "success");
		
		return mav;	
	}
	
	@RequestMapping(value="/gadget/system/saveCircuitBreakerSettingEmergency")
	public ModelAndView saveCircuitBreakerSettingEmergency(			
			
		@ModelAttribute("emergencyForm") CircuitBreakerSetting setting) {
		setting.setCondition(CircuitBreakerCondition.Emergency);
		circuitBreakerManager.saveCircuitBreakerSetting(setting);
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("result", "success");
		
		return mav;	
	}

	@RequestMapping(value="/gadget/system/getCircuitBreakerSetting")
	public ModelAndView getCircuitBreakerSetting() {

		CircuitBreakerSetting prepayment = circuitBreakerManager.getCircuitBreakerSetting(CircuitBreakerCondition.Prepayment);
		CircuitBreakerSetting exceedsThreshold = circuitBreakerManager.getCircuitBreakerSetting(CircuitBreakerCondition.ExceedsThreshold);
		CircuitBreakerSetting emergency = circuitBreakerManager.getCircuitBreakerSetting(CircuitBreakerCondition.Emergency);
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("prepayment", prepayment);
		mav.addObject("exceedsThreshold", exceedsThreshold);
		mav.addObject("emergency", emergency);
		
		return mav;	
	}		
	
	@RequestMapping(value="/gadget/system/getCircuitBreakerLogGridData")
	public ModelAndView getCircuitBreakerLogGridData(

			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("switchStatus") String switchStatus,
			@RequestParam("groupType") String groupType,
			@RequestParam("target") String target,
			@RequestParam("page") String page,
			@RequestParam("pageSize") String pageSize) {

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("switchStatus", switchStatus);
		paramMap.put("groupType", groupType);
		paramMap.put("target", target);
		paramMap.put("page", page);
		paramMap.put("pageSize", pageSize);
		
		Long totalCount = circuitBreakerManager.getCircuitBreakerLogGridDataCount(paramMap);
		List<CircuitBreakerLog> gridData = circuitBreakerManager.getCircuitBreakerLogGridData(paramMap);
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("totalCount", totalCount);
		mav.addObject("gridData", gridData);
		
		return mav;	
	}			
	
	
	@RequestMapping(value="/gadget/system/getCircuitBreakerLogChartData")
	public ModelAndView getCircuitBreakerLogChartData(

			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("switchStatus") String switchStatus,
			@RequestParam("groupType") String groupType,
			@RequestParam("target") String target) {

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("startDate", startDate);
		paramMap.put("endDate", endDate);
		paramMap.put("switchStatus", switchStatus);
		paramMap.put("groupType", groupType);
		paramMap.put("target", target);
		
		List<Map<String, String>> chartData = circuitBreakerManager.getCircuitBreakerLogChartData(paramMap);
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("chartData", chartData);
		
		return mav;	
	}			
	
	@RequestMapping(value="/gadget/system/getElecSupplyCapacityGridData")
	public ModelAndView getElecSupplyCapacityGridData(

			@RequestParam("switchStatus") String switchStatus,
			@RequestParam("groupType") String groupType,
			@RequestParam("target") String target,
			@RequestParam("condition") String condition,
			@RequestParam("meterStatus") String meterStatus,
			@RequestParam("page") String page,
			@RequestParam("pageSize") String pageSize) {

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("switchStatus", switchStatus);
		paramMap.put("groupType", groupType);
		paramMap.put("target", target);
		paramMap.put("condition", condition);
		paramMap.put("meterStatus", meterStatus);
		paramMap.put("page", page);
		paramMap.put("pageSize", pageSize);
		
		List<Map<String, String>> data = circuitBreakerManager.getElecSupplyCapacityGridData(paramMap);
		paramMap.put("totalCount", data.size() + "");
		List<Map<String, String>> gridData = circuitBreakerManager.getElecSupplyCapacityGridDataCount(data, paramMap);
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("totalCount", data.size() + "");
		mav.addObject("gridData", gridData);
		
		return mav;	
	}				
	
	@RequestMapping(value="/gadget/system/updateElecSupplyCapacity")
	public ModelAndView updateElecSupplyCapacity(
			
			@RequestParam("meterIds") String meterIds,
			@RequestParam("targetTypes") String targetTypes,
			@RequestParam("targets") String targets,
			@RequestParam("status") String status,
			@RequestParam("conditions") String conditions) {
        
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		Map<String, String> resultMap = null;
		Map<String, String> paramMap = new HashMap<String, String>();		
		paramMap.put("status", status);
		
		String[] meterIdArray = meterIds.split(",");
		String[] targetTypeArray = targetTypes.split(",");
		String[] targetArray = targets.split(",");
		String[] conditionArray = conditions.split(",");
		
		for(int i = 0, size = meterIdArray.length ; i < size; i++) {

			paramMap.put("meterId", meterIdArray[i]);
			paramMap.put("targetType", targetTypeArray[i]);
			paramMap.put("target", targetArray[i]);
			paramMap.put("condition", conditionArray[i]);
			CircuitBreakerStatus circuitStatus = CircuitBreakerStatus.valueOf(status);
			CircuitBreakerCondition circuitCondition = CircuitBreakerCondition.valueOf(conditionArray[i]);
			circuitBreakerManager.saveSupplyCapacity(circuitStatus, targetTypeArray[i], circuitCondition, Integer.parseInt(meterIdArray[i]));
			
			resultMap = new HashMap<String, String>();
			resultMap.put("id", meterIdArray[i]);
			resultMap.put("block", status);
			result.add(resultMap);
		}
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("result", result);
		
		return mav;	
	}	
	
	@RequestMapping(value="/gadget/system/getEmergencyElecBlockGroupTypeGridData")
	public ModelAndView geEmergencyElecBlockGroupTypeGridData(

			@RequestParam("groupType") String groupType,
			@RequestParam("target") String target,
			@RequestParam("page") String page,
			@RequestParam("pageSize") String pageSize,
			@RequestParam("dateFlag") String dateFlag) {

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("groupType", groupType);
		paramMap.put("target", target);
		paramMap.put("page", page);
		paramMap.put("pageSize", pageSize);
		paramMap.put("dateFlag", dateFlag);
				
		List<Map<String, String>> data = circuitBreakerManager.getEmergencyElecSupplyCapacityGridData(paramMap);
		paramMap.put("totalCount", data.size() + "");
		List<Map<String, String>> gridData = circuitBreakerManager.getEmergencyElecSupplyCapacityGridDataCount(data, paramMap);
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("totalCount", data.size() + "");
		mav.addObject("gridData", gridData);
		
		return mav;	
	}			
}
