package com.aimir.bo.mvm;

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
import com.aimir.service.mvm.MeteringSLAManager;

@Controller
public class MeteringSLAController {
	
	@Autowired
	MeteringSLAManager meteringSLAManager;
    
	@RequestMapping(value="/gadget/mvm/meteringSLAMiniGadget")
	public ModelAndView meteringSLAMiniGadget() {		
		return new ModelAndView("gadget/mvm/meteringSLAMiniGadget");        
    }
	
	@RequestMapping(value="/gadget/mvm/meteringSLAMaxGadget")
	public ModelAndView meteringSLAMaxGadget() {	
		ModelAndView mav = new ModelAndView("gadget/mvm/meteringSLAMaxGadget");
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
		return mav;        
    }
	
	
	@RequestMapping(value="/gadget/mvm/getMeteringSLASummaryGrid")
	public ModelAndView getMeteringSLASummaryGrid(@RequestParam(value="searchStartDate" ,required=false) String searchStartDate
            								    , @RequestParam(value="searchEndDate"   ,required=false) String searchEndDate
            								    , @RequestParam(value="supplierId" 	 	,required=false) String supplierId){
		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("searchStartDate" , searchStartDate);
		condition.put("searchEndDate" 	, searchEndDate);
		condition.put("supplierId" 		, supplierId);
        
    	List<Object> miniChart = meteringSLAManager.getMeteringSLASummaryGrid(condition);
    	
        ModelAndView mav = new ModelAndView("jsonView");
                
        mav.addObject("gridData",miniChart.get(0));
        
        return mav;        
    }

	@RequestMapping(value="/gadget/mvm/getMeteringSLAMiniChart")
	public ModelAndView getMeteringSLAMiniChart(@RequestParam(value="searchStartDate" ,required=false) String searchStartDate
            								   , @RequestParam(value="searchEndDate"  ,required=false) String searchEndDate
            								   , @RequestParam(value="supplierId" 	  ,required=false) String supplierId
            								   , @RequestParam(value="page" 	  	  ,required=false) String page){
		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("searchStartDate" , searchStartDate);
		condition.put("searchEndDate" 	, searchEndDate);
		condition.put("supplierId" 		, supplierId);
		
    	List<Object> miniChart = meteringSLAManager.getMeteringSLAMiniChart(condition);
    	condition.put("page" 			, page);
    	List<Object> miniChart1 = meteringSLAManager.getMeteringSLAMiniChart(condition);
    	
        ModelAndView mav = new ModelAndView("jsonView");        
        mav.addObject("chartData",miniChart1.get(0));
        mav.addObject("total",miniChart.get(1));
    
        return mav;        
    }
		
	@RequestMapping(value="/gadget/mvm/getMeteringSLAMissingData")
	public ModelAndView getMeteringSLAMissingData(@RequestParam(value="searchStartDate" ,required=false) String searchStartDate
            								 , @RequestParam(value="searchEndDate" 	 ,required=false) String searchEndDate
            								 , @RequestParam(value="supplierId"	 	 ,required=false) String supplierId
            								 , @RequestParam(value="page"	 	 ,required=false) String page){
		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("searchStartDate" , searchStartDate);
		condition.put("searchEndDate" 	, searchEndDate);
		condition.put("supplierId" 		, supplierId);
		condition.put("page" 			, page);
    	List<Object> miniChart = meteringSLAManager.getMeteringSLAMissingData(condition);
        ModelAndView mav = new ModelAndView("jsonView");
                
        mav.addObject("gridData",miniChart.get(0));
        mav.addObject("totalCnt",miniChart.get(1));
        
        return mav;        
    }
	
	
	@RequestMapping(value="/gadget/mvm/getMeteringSLAMissingDetailChart")
	public ModelAndView getMeteringSLAMissingDetailChart(@RequestParam(value="searchStartDate" ,required=false) String searchStartDate
            								           , @RequestParam(value="searchEndDate"   ,required=false) String searchEndDate
            								           , @RequestParam(value="supplierId" 	   ,required=false) String supplierId
													   , @RequestParam(value="missedDay" 	   ,required=false) String missedDay){
		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("searchStartDate" , searchStartDate);
		condition.put("searchEndDate" 	, searchEndDate);
		condition.put("supplierId" 		, supplierId);
		condition.put("missedDay" 		, missedDay);
        
    	List<Object> miniChart = meteringSLAManager.getMeteringSLAMissingDetailChart(condition);
    	
        ModelAndView mav = new ModelAndView("jsonView");
                
        mav.addObject("chartData",miniChart.get(0));
        
        return mav;        
    }
	
	
	@RequestMapping(value="/gadget/mvm/getMeteringSLAMissingDetailGrid")
	public ModelAndView getMeteringSLAMissingDetailGrid(@RequestParam(value="searchStartDate" 	,required=false) String searchStartDate
            								          , @RequestParam(value="searchEndDate" 	,required=false) String searchEndDate
            								          , @RequestParam(value="supplierId" 		,required=false) String supplierId
													  , @RequestParam(value="missedDay" 	    ,required=false) String missedDay
													  , @RequestParam(value="missedReason" 	    ,required=false) String missedReason){
		Map<String, Object> condition = new HashMap<String, Object>();
		
		condition.put("searchStartDate" , searchStartDate);
		condition.put("searchEndDate" 	, searchEndDate);
		condition.put("supplierId" 		, supplierId);
		condition.put("missedDay" 		, missedDay);
		condition.put("missedReason" 	, missedReason);
        
    	List<Object> miniChart = meteringSLAManager.getMeteringSLAMissingDetailGrid(condition);
    	
        ModelAndView mav = new ModelAndView("jsonView");
        
        mav.addObject("totalCnt",miniChart.get(0));
        mav.addObject("gridData",miniChart.get(1));
        
        return mav;        
    }
	
	
	
	
}
