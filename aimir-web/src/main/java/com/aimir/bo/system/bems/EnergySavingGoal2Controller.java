package com.aimir.bo.system.bems;

import java.io.UnsupportedEncodingException;
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
import com.aimir.service.system.EnergySavingGoal2Manager;

import flex.messaging.util.URLDecoder;

@Controller
public class EnergySavingGoal2Controller {

	@Autowired
	EnergySavingGoal2Manager energySavingGoal2Manager;

	@RequestMapping(value="/gadget/bems/energySavingGoal2EmMaxGadget")
    public ModelAndView energySavingGoal2EmMaxGadget(HttpServletRequest request, HttpServletResponse response) {
		
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

        mav.setViewName("gadget/bems/energySavingGoal2EmMaxGadget");
        
        return mav;
//    	return new ModelAndView("gadget/bems/energySavingGoal2EmMaxGadget");
    }
	
	@RequestMapping(value="/gadget/bems/energySavingGoal2EmMiniGadget")
	public ModelAndView energySavingGoal2EmMinGadget(HttpServletRequest request, HttpServletResponse response) {
		
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

        mav.setViewName("gadget/bems/energySavingGoal2EmMiniGadget");
        
        return mav;
//		return new ModelAndView("gadget/bems/energySavingGoal2EmMiniGadget");
	}

	
	@RequestMapping(value="/gadget/bems/energySavingGoal2GmMaxGadget")
	public ModelAndView energySavingGoal2GmMaxGadget(HttpServletRequest request, HttpServletResponse response) {
		
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

        mav.setViewName("gadget/bems/energySavingGoal2GmMaxGadget");
        
        return mav;
//		return new ModelAndView("gadget/bems/energySavingGoal2GmMaxGadget");
	}
	
	@RequestMapping(value="/gadget/bems/energySavingGoal2GmMiniGadget")
	public ModelAndView energySavingGoal2GmMinGadget(HttpServletRequest request, HttpServletResponse response) {
		
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

        mav.setViewName("gadget/bems/energySavingGoal2GmMiniGadget");
        
        return mav;
//		return new ModelAndView("gadget/bems/energySavingGoal2GmMiniGadget");
	}
	
	
	
	@RequestMapping(value="/gadget/bems/energySavingGoal2WmMaxGadget")
	public ModelAndView energySavingGoal2WmMaxGadget(HttpServletRequest request, HttpServletResponse response) {
		
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

        mav.setViewName("gadget/bems/energySavingGoal2WmMaxGadget");
        
        return mav;
//		return new ModelAndView("gadget/bems/energySavingGoal2WmMaxGadget");
	}
	
	@RequestMapping(value="/gadget/bems/energySavingGoal2WmMiniGadget")
	public ModelAndView energySavingGoal2WmMinGadget(HttpServletRequest request, HttpServletResponse response) {
		
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

        mav.setViewName("gadget/bems/energySavingGoal2WmMiniGadget");
        
        return mav;
//		return new ModelAndView("gadget/bems/energySavingGoal2WmMiniGadget");
	}
	
	@RequestMapping(value="/gadget/bems/energySavingGoal2HmMaxGadget")
    public ModelAndView energySavingGoal2HmMaxGadget(HttpServletRequest request, HttpServletResponse response) {
		
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

        mav.setViewName("gadget/bems/energySavingGoal2HmMaxGadget");
        
        return mav;
//    	return new ModelAndView("gadget/bems/energySavingGoal2EmMaxGadget");
    }
	
	@RequestMapping(value="/gadget/bems/energySavingGoal2HmMiniGadget")
	public ModelAndView energySavingGoal2HmMinGadget(HttpServletRequest request, HttpServletResponse response) {
		
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

        mav.setViewName("gadget/bems/energySavingGoal2HmMiniGadget");
        
        return mav;
//		return new ModelAndView("gadget/bems/energySavingGoal2EmMiniGadget");
	}
	
	@RequestMapping(value="/gadget/bems/setEnergySavingGoal2Em")
	public ModelAndView setEnergySavingGoal2Em( @RequestParam("supplierId") String supplierId 
											, @RequestParam("savingGoalTarget") String savingGoal  
											, @RequestParam("savingGoalDateType") String savingGoalDateType  
											, @RequestParam("savingGoalStartDate") String savingGoalStartDate ) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("supplierId"		, supplierId );
        params.put("energyType"		, "0" ); //전기 : 0 , 가스 : 1 , 수도 : 2
        params.put("savingGoal"		, savingGoal );
        params.put("savingGoalDateType"		, savingGoalDateType );
        params.put("savingGoalStartDate"		, savingGoalStartDate );

        Map<String, Object> result = energySavingGoal2Manager.setEnergySavingGoal2(params);
        mav.addObject("result", result.get("result"));
        
    	return mav;
	}
	
	@RequestMapping(value="/gadget/bems/setEnergySavingGoal2Gm")
	public ModelAndView setEnergySavingGoal2Gm( @RequestParam("supplierId") String supplierId 
			, @RequestParam("savingGoalTarget") String savingGoal  
			, @RequestParam("savingGoalDateType") String savingGoalDateType  
			, @RequestParam("savingGoalStartDate") String savingGoalStartDate ) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("supplierId"		, supplierId );
        params.put("energyType"		, "1" ); //전기 : 0 , 가스 : 1 , 수도 : 2
		params.put("savingGoal"		, savingGoal );
		params.put("savingGoalDateType"		, savingGoalDateType );
		params.put("savingGoalStartDate"		, savingGoalStartDate );
		
		Map<String, Object> result = energySavingGoal2Manager.setEnergySavingGoal2(params);
		mav.addObject("result", result.get("result"));
		
		return mav;
	}
	
	@RequestMapping(value="/gadget/bems/setEnergySavingGoal2Wm")
	public ModelAndView setEnergySavingGoal2Wm( @RequestParam("supplierId") String supplierId 
			, @RequestParam("savingGoalTarget") String savingGoal  
			, @RequestParam("savingGoalDateType") String savingGoalDateType  
			, @RequestParam("savingGoalStartDate") String savingGoalStartDate ) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("supplierId"		, supplierId );
		params.put("energyType"		, "2" ); //전기 : 0 , 가스 : 1 , 수도 : 2
		params.put("savingGoal"		, savingGoal );
		params.put("savingGoalDateType"		, savingGoalDateType );
		params.put("savingGoalStartDate"		, savingGoalStartDate );
		
		Map<String, Object> result = energySavingGoal2Manager.setEnergySavingGoal2(params);
		mav.addObject("result", result.get("result"));
		
		return mav;
	}
	
	@RequestMapping(value="/gadget/bems/setEnergySavingGoal2Hm")
	public ModelAndView setEnergySavingGoal2Hm( @RequestParam("supplierId") String supplierId 
											, @RequestParam("savingGoalTarget") String savingGoal  
											, @RequestParam("savingGoalDateType") String savingGoalDateType  
											, @RequestParam("savingGoalStartDate") String savingGoalStartDate ) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("supplierId"		, supplierId );
        params.put("energyType"		, "3" ); //전기 : 0 , 가스 : 1 , 수도 : 2 열량 : 3
        params.put("savingGoal"		, savingGoal );
        params.put("savingGoalDateType"		, savingGoalDateType );
        params.put("savingGoalStartDate"		, savingGoalStartDate );

        Map<String, Object> result = energySavingGoal2Manager.setEnergySavingGoal2(params);
        mav.addObject("result", result.get("result"));
        
    	return mav;
	}
	
	@RequestMapping(value="/gadget/bems/setEnergyAvg2")
	public ModelAndView setEnergyAvg2( @RequestParam("supplierId2") String supplierId 
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
		
		Map<String, Object> result = energySavingGoal2Manager.setEnergyAvg2(params);
		mav.addObject("result", result.get("result"));
		
		return mav;
	}
	
	@RequestMapping(value="gadget/bems/getEnergySavingGoal2Info")
	public ModelAndView getBuildingLookUpEm( @RequestParam(value="searchStartDate",required=false) String searchStartDate,
			@RequestParam(value="searchEndDate",required=false) String searchEndDate,
			@RequestParam("searchDateType") String searchDateType,
			@RequestParam("supplierId") String supplierId,
			@RequestParam("energyType") String energyType,
			@RequestParam(value="savingGoalStartDate" ,required=false) String savingGoalStartDate,
			@RequestParam(value="savingGoal",required=false) String savingGoal,
			@RequestParam(value="requestType",required=false) String requestType,
			@RequestParam("msgAvgYear") String msgAvgYear,
			@RequestParam("msgGoal") String msgGoal,
			@RequestParam("msgPrediction") String msgPrediction) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String,Object> condition = new HashMap<String,Object>();
		condition.put("searchStartDate", searchStartDate);
		condition.put("searchEndDate", searchEndDate);
		condition.put("searchDateType", searchDateType);
		condition.put("supplierId", supplierId);
		condition.put("energyType", energyType);
		condition.put("savingGoalStartDate", savingGoalStartDate);
		condition.put("savingGoal", savingGoal);
		condition.put("requestType", requestType);
		try {
			condition.put("msgAvgYear",URLDecoder.decode(msgAvgYear,"UTF-8") );
			condition.put("msgGoal", URLDecoder.decode(msgGoal,"UTF-8"));
			condition.put("msgPrediction", URLDecoder.decode(msgPrediction,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Map<String, Object> result = energySavingGoal2Manager.getEnergySavingGoal2Info(condition);
		
		mav.addObject("info", result.get("info"));
		mav.addObject("energyType", result.get("energyType"));
		
		System.out.println(mav);
		return mav;
	}
	
}
