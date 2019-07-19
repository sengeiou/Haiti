package com.aimir.bo.report;

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
import com.aimir.service.mvm.LpReportManager;

@Controller
public class LpReportController {

	@Autowired
	LpReportManager lpReportManager;
	
	/**
     * Mini Gadget
     */
    @RequestMapping(value="/gadget/report/lpReportMiniGadget")
    public ModelAndView loadLpReportMiniGadget() {
        return new ModelAndView("/gadget/report/lpReportMiniGadget");
    }

    /**
     * Max Gadget
     */
    @RequestMapping(value="/gadget/report/lpReportMaxGadget")
    public ModelAndView loadLpReportMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/report/lpReportMaxGadget");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();
        int operatorId = new Long(user.getAccountId()).intValue();

        mav.addObject("supplierId", supplierId);
        mav.addObject("operatorId", operatorId);
        return mav;
    }
    
    
    @RequestMapping(value="/gadget/report/getValidLpRate")
    public ModelAndView getValidLpRate(
    		@RequestParam("supplierId") Integer supplierId,
    		@RequestParam(value = "dcuName") String dcuName,
    		@RequestParam(value = "timeType") String timeType,
    		@RequestParam(value = "startDate") String startDate,
    		@RequestParam(value = "endDate") String endDate
    		) throws Exception {
    	ModelAndView mav = new ModelAndView("jsonView");
        List<Object> result	= new ArrayList<Object>();
        Map<String,Object> condition = new HashMap<String,Object>();
                
        condition.put("dcuName", dcuName);
        condition.put("timeType", timeType);
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);
        
        Map<String, Object> mResult = lpReportManager.getValidLpRate(condition);
        
        mav.addObject("calc", mResult.get("lpReport"));
        mav.addObject("message", mResult.get("ERROR").toString());
        return mav;
    }
    
    
    @RequestMapping(value="/gadget/report/getValidMeterRate")
    public ModelAndView getValidMeterRate(
    		@RequestParam("supplierId") Integer supplierId,
    		@RequestParam(value = "dcuName") String dcuName,
    		@RequestParam(value = "timeType") String timeType,
    		@RequestParam(value = "startDate") String startDate,
    		@RequestParam(value = "endDate") String endDate
    		) throws Exception {
    	ModelAndView mav = new ModelAndView("jsonView");
        List<Object> result	= new ArrayList<Object>();
        Map<String,Object> condition = new HashMap<String,Object>();
                
        condition.put("dcuName", dcuName);
        condition.put("timeType", timeType);
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);
        
        Map<String, Object> mResult = lpReportManager.getValidMeterRate(condition);
        
        mav.addObject("calc", mResult.get("meterReport"));
        mav.addObject("message", mResult.get("ERROR").toString());
        return mav;
    }



}


