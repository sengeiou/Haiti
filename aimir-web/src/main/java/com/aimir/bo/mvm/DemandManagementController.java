package com.aimir.bo.mvm;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.service.mvm.DemandManagementManager;
import com.aimir.util.TimeUtil;

@Controller
public class DemandManagementController {

	@Autowired
	DemandManagementManager demandManagementManager;
    
	@RequestMapping(value="/gadget/mvm/demandManagementMax")
    public ModelAndView demandManagementMax() {
    	return new ModelAndView("gadget/mvm/demandManagementMax");
    }

	@RequestMapping(value="/gadget/mvm/demandManagementMini")
    public ModelAndView demandManagementMini() {
    	return new ModelAndView("gadget/mvm/demandManagementMini");
    }
	
	@RequestMapping(value="/gadget/mvm/getDemandManagement")    
    public ModelAndView getDemandManagement(    		
		@RequestParam("meterType") String meterType,
		@RequestParam("supplierId") String supplierId,
		@RequestParam("locationId") String locationId,
		@RequestParam("tariffType") String tariffType,    		
		@RequestParam("dateType") String dateType,
		@RequestParam("startDate") String startDate,
		@RequestParam("endDate") String endDate,
		@RequestParam("season") String season) {    	

    	try {
            if (startDate.isEmpty()) {
            	startDate = TimeUtil.getCurrentDay();
            	endDate = TimeUtil.getCurrentDay();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
		
    	Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("meterType", meterType);
    	conditionMap.put("supplierId", supplierId);
    	conditionMap.put("locationId", locationId);
    	conditionMap.put("tariffType", tariffType);
    	conditionMap.put("dateType", dateType);
    	conditionMap.put("startDate", startDate);
    	conditionMap.put("endDate", endDate);
    	conditionMap.put("season", season);
    	
    	ModelAndView mav = new ModelAndView("jsonView");
    	mav.addObject("result", demandManagementManager.getDemandManagement(conditionMap));
    	
    	return mav;
    }
	
	@SuppressWarnings({ "rawtypes" })
	@RequestMapping(value="/gadget/mvm/getDemandManagementList")    
    public ModelAndView getDemandManagementList(    		
		@RequestParam("meterType") String meterType,
		@RequestParam("supplierId") String supplierId,
		@RequestParam("locationId") String locationId,
		@RequestParam("tariffType") String tariffType,    		
		@RequestParam("dateType") String dateType,
		@RequestParam("startDate") String startDate,
		@RequestParam("endDate") String endDate,
		@RequestParam("season") String season) 
	{    	
        
    	Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("meterType", meterType);
    	conditionMap.put("supplierId", supplierId);
    	conditionMap.put("locationId", locationId);
    	conditionMap.put("tariffType", tariffType);
    	conditionMap.put("dateType", dateType);
    	
    	conditionMap.put("startDate", startDate);
    	conditionMap.put("endDate", endDate);
    	
    	
    	conditionMap.put("season", season);
    	conditionMap.put("searchType", "search");
    	conditionMap.put("page", 0);
    	
    	ModelAndView mav = new ModelAndView("jsonView");
    	List demandManagementGridList=new ArrayList();
    	
    	Map<String, Object> demandmanagementlistMap= demandManagementManager.getDemandManagementList(conditionMap);
    	
    	mav.addObject("result", demandmanagementlistMap);
    	
    	
    	demandManagementGridList= (ArrayList) demandmanagementlistMap.get("grid");
    	
    	int totalCnt = demandManagementGridList.size();
    	
    	mav.addObject("demandManagementGridList", demandManagementGridList);
    	mav.addObject("totalCnt", totalCnt);
    	
    	
    	return mav;
    }	
	
}
