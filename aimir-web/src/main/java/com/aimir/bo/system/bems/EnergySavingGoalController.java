package com.aimir.bo.system.bems;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Operator;
import com.aimir.service.system.EnergySavingGoalManager;

@Controller
public class EnergySavingGoalController {

	@Autowired
	EnergySavingGoalManager energySavingGoalManager;

	@RequestMapping(value="/gadget/bems/energySavingGoalMaxGadget")
    public ModelAndView energySavingGoalMaxGadget(HttpServletRequest request, HttpServletResponse response) {
		
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

        mav.setViewName("gadget/bems/energySavingGoalMaxGadget");
        
        return mav;
//    	return new ModelAndView("gadget/bems/energySavingGoalMaxGadget");
    }
	
	@RequestMapping(value="/gadget/bems/energySavingGoalMiniGadget")
	public ModelAndView energySavingGoalMinGadget(HttpServletRequest request, HttpServletResponse response) {
		
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

        mav.setViewName("gadget/bems/energySavingGoalMiniGadget");
        
        return mav;
//		return new ModelAndView("gadget/bems/energySavingGoalMiniGadget");
	}
	
	
	@RequestMapping(value="/gadget/bems/setEnergySavingGoal")
	public ModelAndView setEnergySavingGoal( @RequestParam("supplierId") String supplierId 
											, @RequestParam("savingGoal") String savingGoal  
											, @RequestParam("savingGoalStartDate") String savingGoalStartDate ) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("supplierId"		, supplierId );
        params.put("savingGoal"		, savingGoal );
        params.put("savingGoalStartDate"		, savingGoalStartDate );

        Map<String, Object> result = energySavingGoalManager.setEnergySavingGoal(params);
        mav.addObject("result", result.get("result"));
        
    	return mav;
	}
	
	@RequestMapping(value="/gadget/bems/setEnergyAvg")
	public ModelAndView setEnergyAvg( @RequestParam("supplierId2") String supplierId 
			, @RequestParam("years") String years  
			, @RequestParam("descr") String descr  
			, @RequestParam(value="used" , required=false) String used 
			, @RequestParam(value="avgInfoId" , required=false) String avgInfoId 
			) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("supplierId"	, supplierId );
		params.put("years"		, years );
		params.put("descr"		, descr );
		params.put("used"		, used );
		params.put("avgInfoId"	, avgInfoId );
		
		Map<String, Object> result = energySavingGoalManager.setEnergyAvg(params);
		mav.addObject("result", result.get("result"));
		
		return mav;
	}
}
