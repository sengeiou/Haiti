package com.aimir.bo.mvm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.service.mvm.SAPIntegrationManager;

@Controller
public class SAPIntegrationController {
	
	@Autowired
	SAPIntegrationManager sapIntegrationManager; 
	
	@RequestMapping(value="/gadget/mvm/sapIntegrationLogMini")    
    public ModelAndView getSapIntegrationLogMini() {
    	return new ModelAndView("/gadget/mvm/sapIntegrationLogMini");
	}
	
	@RequestMapping(value="/gadget/mvm/sapIntegrationLogMax")    
    public ModelAndView getSapIntegrationLogMax() {
    	return new ModelAndView("/gadget/mvm/sapIntegrationLogMax");
	}
	
	/**
     * SAP Integration Log 가젯의 Outbound Log Grid에 필요한 데이터 검색
     * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@RequestMapping(value="/gadget/mvm/getOutBoundGridData")
	public ModelAndView getOutBoundGridData(
			@RequestParam("startDate")	String startDate,
			@RequestParam("endDate") 	String endDate) {
		
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();
        
    	HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
		String page = request.getParameter("page");
		String limit = request.getParameter("limit");
    	
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("page", page);
		condition.put("limit", limit);
		condition.put("startDate", startDate);
		condition.put("endDate", endDate);
		condition.put("supplierId", supplierId);
		
		int i = 0;
		List<Object> gridData = sapIntegrationManager.getOutBoundGridData(condition);
		mav.addObject("outGridData", gridData.get(0));
		mav.addObject("outGridTotalCnt", (Integer)gridData.get(1));
		mav.addObject("no", i+(Integer.parseInt(page)*Integer.parseInt(limit)));
		
		return mav;
	}
	
	/**
	 * SAP Integration Log 가젯의 Inbound Log Grid에 필요한 데이터 검색
	 * 
	 * @param outboundFileName
	 * @param outboundDate
	 * @return
	 */
	@RequestMapping(value="/gadget/mvm/getInBoundGridData")
	public ModelAndView getInBoundGridData(
			@RequestParam("outboundFileName")	String outboundFileName,
			@RequestParam("outboundDate")		String outboundDate) {
		
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();
    	
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("outboundFileName", outboundFileName);
		condition.put("outboundDate", outboundDate);
		condition.put("supplierId", supplierId);
		
		List<Object> gridData = sapIntegrationManager.getInBoundGridData(condition);
		mav.addObject("inGridData", gridData.get(0));
		mav.addObject("inboundTotalCnt_Total", gridData.get(1));
		mav.addObject("inboundMeterCnt_Total", gridData.get(2));
		
		return mav;
	}
	
	/**
	 * SAP Integration Log 가젯의 Error Log Grid에 필요한 데이터 검색
	 * 
	 * @param outboundFileName
	 * @param outboundDate
	 * @return
	 */
	@RequestMapping(value="/gadget/mvm/getErrorLogGridData")
	public ModelAndView getErrorLogGridData(
			@RequestParam("outboundFileName")	String outboundFileName,
			@RequestParam("outboundDate")		String outboundDate) {

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();
    	
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("outboundFileName", outboundFileName);
		condition.put("outboundDate", outboundDate);
		condition.put("supplierId", supplierId);
		
		List<Object> gridData = sapIntegrationManager.getErrorLogGridData(condition);
		mav.addObject("errorGridData", gridData.get(0));
		mav.addObject("errorGridTotalCnt", gridData.get(1));
		
		return mav;
	}
}
