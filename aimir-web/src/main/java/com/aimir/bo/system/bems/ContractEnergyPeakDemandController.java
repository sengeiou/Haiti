package com.aimir.bo.system.bems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.system.Code;
import com.aimir.model.system.PeakDemandScenario;
import com.aimir.model.system.PeakDemandSetting;
import com.aimir.model.system.Operator;

import com.aimir.service.system.OperatorManager;
import com.aimir.service.device.EventAlertLogManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ContractEnergyPeakDemandManager;
import com.aimir.service.system.ContractCapacityManager;
import com.aimir.service.system.PeakDemandManager;
import com.aimir.model.system.PeakDemandLog;

@Controller
public class ContractEnergyPeakDemandController {
	
	@Autowired ContractEnergyPeakDemandManager contractEnergyPeakDemandManager;
	@Autowired EventAlertLogManager eventAlertLogManager;
	@Autowired PeakDemandManager peakDemandManager; 
	@Autowired CodeManager codeManager;
	@Autowired OperatorManager operatorManager;
	@Autowired ContractCapacityManager contractCapacityManager;
	
	private static final String [] TAG_CODES = { "20.1", "20.2" };
	private final Logger logger = Logger.getLogger(ContractEnergyPeakDemandController.class);
	
	private Operator getCurrentOperator() {
		try {
			AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
			AimirUser user = (AimirUser)instance.getUserFromSession();
			Operator o = operatorManager.getOperatorByLoginId(user.getLoginId());
			if(o == null) {
				throw new IllegalStateException("session authinfo not exists");
			}
			return o;
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	private ModelAndView handleException(Throwable t, ModelAndView mav) {
		
		t.printStackTrace();
		logger.info(t);
		
		if(t instanceof IllegalStateException) {
			mav.addObject("result", "fail").addObject("msg", "aimir.invalidUser");
		}
		else if(t instanceof IllegalArgumentException) {
			mav.addObject("result", "fail").addObject("msg", t.getMessage());
		}
		else {
			mav.addObject("result", "fail").addObject("msg", t.getMessage());
		}
		return mav;
	}
	
	private List<Map<String,Object>> getEnergyPeakDemandCombo() {
		return contractEnergyPeakDemandManager.getEnergyPeakDemandCombo();
	}
	
	@RequestMapping(value="/gadget/bems/contractEnergyPeakDemandMiniGadget")
    public ModelAndView contractEnergyPeakDemandMiniGadget() {
		ModelAndView mav = new ModelAndView("gadget/bems/contractEnergyPeakDemandMiniGadget");
		return mav.addObject("combo", getEnergyPeakDemandCombo());
	}
	
	@RequestMapping(value="/gadget/bems/contractEnergyPeakDemandMaxGadget")
    public ModelAndView contractEnergyPeakDemandMaxGadget() {
		ModelAndView mav = new ModelAndView("gadget/bems/contractEnergyPeakDemandMaxGadget");
		List<Code> fmStatus = codeManager.getChildCodes("9.4");		
		List<Code> tags = new ArrayList<Code>();
		List<Code> tag = null;		
		for(int i=0; i < TAG_CODES.length; i++) {
			tag = codeManager.getChildCodesOrder(TAG_CODES[i]);	
			mav.addObject("tags_" + (i+1), tag);
			tags.addAll(tag);
		}
		return mav
			.addObject("combo", getEnergyPeakDemandCombo())
			.addObject("tags", tags)
			.addObject("fmStatus", fmStatus)
			.addObject("pdSettings", peakDemandManager.getAllPeakDemandSettings());
	}
	
	@RequestMapping(value="/gadget/bems/getEnergyPeakDemand")
    public ModelAndView getEnergyPeakDemand(   
   		@RequestParam("contractCapacityId") String contractCapacityId) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("contractCapacityId", contractCapacityId);

        Map<String, Object> result = contractEnergyPeakDemandManager.getEnergyPeakDemand(condition);
        mav.addObject("result", result);
        
    	return mav;
    }
	
	/**
	 * FM Status 코드를 얻는다.
	 * 
	 * @return json
	 */
	@RequestMapping(value="/gadget/bems/getFMStatusCode")
	public ModelAndView getFMStatusCode() {
		ModelAndView mav = new ModelAndView("jsonView");
		List<Code> fmStatus = codeManager.getChildCodesOrder("9.4");
		return mav.addObject("fmStatus", fmStatus);
	}
	
	/**
	 * Threshhold를 업데이트한다.
	 * 
	 * @param contractCapacityId
	 * @param threshold1
	 * @param threshold2
	 * @param threshold3
	 * @return json
	 */
	@RequestMapping(value="/gadget/bems/updateThreshold")
	public ModelAndView updateThreshold( 
		@RequestParam("contractCapacityId") String contractCapacityId,
		@RequestParam("threshold1") String threshold1,
		@RequestParam("threshold2") String threshold2,
		@RequestParam("threshold3") String threshold3 ) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("contractCapacityId", contractCapacityId);
		condition.put("threshold1", threshold1);
		condition.put("threshold2", threshold2);
		condition.put("threshold3", threshold3);
		
		contractEnergyPeakDemandManager.updateThreshold(condition);
		
		return mav;
	}
	
	@RequestMapping(value="/gadget/bems/getPeakDemandthresholdConfigs")
	public ModelAndView getPeakDemandthresholdConfigs() {
		ModelAndView mav = new ModelAndView("jsonView");
		return mav.addObject("settings", peakDemandManager.getAllPeakDemandSettings());
	}
	
	/**
	 * 
	 * @param start
	 * @param limit
	 * @param supplierId
	 * @param searchDate
	 * @param locationId
	 * @param status
	 * @return
	 */
	@RequestMapping(value="/gadget/bems/getPeekDemandLogs")
	public ModelAndView getPeekDemandLogs(
		@RequestParam int start,
		@RequestParam int limit,
		@RequestParam(required=false) String supplierId,
		@RequestParam(required=false) String searchDate,
		@RequestParam(required=false) String locationId,
		@RequestParam(required=false) String status ) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, String> parameters = new HashMap<String, String>();
		
		if(supplierId != null) parameters.put("supplierId", supplierId);
		if(searchDate != null) parameters.put("searchDate", searchDate);
		if(locationId != null) parameters.put("locationId", locationId);
		if(status != null) parameters.put("status", status);
		
		Map<String, Integer> pagingVars = new HashMap<String, Integer>();
		pagingVars.put("first", start);
		pagingVars.put("max", limit);
		
		List<EventAlertLog> eventAlertLogs 
			= eventAlertLogManager.getEventAlertLogs(parameters, pagingVars);
		long count = eventAlertLogManager.getEventAlertLogCount(parameters);
		
		return mav
			.addObject("eventAlertLogs", eventAlertLogs)
			.addObject("totalCount", count);
	}
	
	@RequestMapping(value="/gadget/bems/getDRLogs")
	public ModelAndView getDRLogs(
		@RequestParam int start,
		@RequestParam int limit,
		@RequestParam(required=false) String supplierId,
		@RequestParam(required=false) String searchDate,
		@RequestParam(required=false) String scenario,
		@RequestParam(required=false) String result) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, String> parameters = new HashMap<String, String>();
		
		if(supplierId != null) parameters.put("supplierId", supplierId);
		if(searchDate != null) parameters.put("searchDate", searchDate);
		if(scenario != null) parameters.put("scenario", scenario);
		if(result != null) parameters.put("result", result);
		
		Map<String, Integer> pagingVars = new HashMap<String, Integer>();
		pagingVars.put("first", start);
		pagingVars.put("max", limit);
		
		List<PeakDemandLog> peakDemandLogs 
			= peakDemandManager.getPeakDemandLogs(parameters, pagingVars);
		long totalcount 
			= peakDemandManager.getTotalPeakDemandLog(parameters);
		
		return mav
			.addObject("peakDemandLogs", peakDemandLogs)
			.addObject("totalCount", totalcount);
	}
	
	@RequestMapping(value="/gadget/bems/getDRScenarios")
	public ModelAndView getDRScenarios(
		@RequestParam int start,
		@RequestParam int limit,
		@RequestParam(required=false) String supplierId,
		@RequestParam(required=false) String name,
		@RequestParam(required=false) String contractCapacity) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, String> parameters = new HashMap<String, String>();
		if(supplierId != null) parameters.put("supplierId", supplierId);
		if(contractCapacity != null) parameters.put("contractCapacity", contractCapacity);
		if(name != null) parameters.put("name", name);
		
		Map<String, Integer> pagingVars = new HashMap<String, Integer>();
		pagingVars.put("first", start);
		pagingVars.put("max", limit);
		
		List<PeakDemandScenario> peakDemandScenarios 
			= peakDemandManager.getPeakDemandScenarios(parameters, pagingVars);
		long totalcount 
			= peakDemandManager.getTotalPeakDemandScenario(parameters);
		
		return mav
			.addObject("peakDemandScenarios", peakDemandScenarios)
			.addObject("totalcount", totalcount);
	}
	
	/**
	 * DR 시나리오를 업데이트한다.
	 * @param scenarioId
	 * @param threshold
	 * @param level
	 * @return
	 */
	@RequestMapping(value="/gadget/bems/configDRScenario")
	public ModelAndView configDRScenario(
		@RequestParam(required=false) String scenarioId,
		@RequestParam String supplierId,
		@RequestParam String isAction,		
		@RequestParam String level ) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, String> params = new HashMap<String, String>();
		
		try {
			Operator operator = getCurrentOperator();			
			
			params.put("scenarioId", scenarioId);		
			params.put("supplierId", supplierId);
			params.put("level", level);
			params.put("isAction", isAction);
			
			PeakDemandSetting setting = 
				peakDemandManager.applyPeakDemandConfiguration(params, operator);
			
			mav.addObject("result", "success").addObject("setting", setting);
		}
		catch (Exception ex) {
			mav = handleException(ex, mav);
		}
		return mav;
	}
	
	@RequestMapping(value="/gadget/bems/addDRScenario")
	public ModelAndView addDRScenario(
		Locale locale,
		@RequestParam String scenarioName,
		@RequestParam String contractLocation,
		@RequestParam(required=false) String description,
		@RequestParam String tags) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, String> params = new HashMap<String, String>();
		
		try {
			if(scenarioName == null || tags == null) {
				throw new IllegalArgumentException("aimir.form.required.DRScenarioInvalid");
			}
			params.put("scenarioName", scenarioName);
			params.put("contractLocation", contractLocation);
			params.put("description", description);
			params.put("target", tags);
			
			Operator operator = getCurrentOperator();
			PeakDemandScenario scenario 
				= peakDemandManager.addPeakDemandScenario(params, operator);
			
			mav.addObject("result", "success");
			mav.addObject("scenario", scenario);
		}
		catch (Exception ex) {
			mav = handleException(ex, mav);
		}
		return mav;
	}
	
	@RequestMapping(value="/gadget/bems/modifyDRScenario")
	public ModelAndView modifyDRScenario(
		Locale locale,
		@RequestParam String scenarioId,
		@RequestParam String scenarioName,
		@RequestParam String contractLocation,
		@RequestParam(required=false) String description,
		@RequestParam(required=false) String tags) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("scenarioId", scenarioId);
		params.put("name", scenarioName);
		params.put("contractLocation", contractLocation);
		params.put("description", description);
		params.put("target", tags);
		
		try {
			PeakDemandScenario peakDemandScenario = 
				peakDemandManager.modifyPeakDemandScenario(params);
			if(peakDemandScenario != null) {
				mav.addObject("result", "success");
				mav.addObject("peakDemandScenario", peakDemandScenario);
			}
			else {
				mav.addObject("result", "fail");
				mav.addObject("result", "aimir.msg.updatefail");
			}
		}
		catch (IllegalArgumentException e) {
			mav = handleException(e, mav);
		}
		return mav;
	}
	
	@RequestMapping(value="/gadget/bems/deleteDRScenario")
	public ModelAndView deleteDRScenario(@RequestParam Integer scenario) {
		ModelAndView mav = new ModelAndView("jsonView");
		try {
			Integer peakDemandScenarioId = peakDemandManager.deletePeakDemandScenario(scenario);
			if(peakDemandScenarioId == null) {
				mav.addObject("result", "fail");
				mav.addObject("result", "aimir.msg.deleteFail");
			}
			else {
				mav.addObject("result", "success");
				mav.addObject("deleteId", peakDemandScenarioId);
			}
		}
		catch (IllegalArgumentException e) {
			mav = handleException(e, mav);
		}
		return mav;
	}
}