package com.aimir.bo.system.auditlog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.AuditAction;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.service.system.AuditLogManager;

@Controller
public class ChangeLogController {

    protected static Log log = LogFactory.getLog(ChangeLogController.class);

    @Autowired
    AuditLogManager auditLogManager;

//	/**
//	 * method name : getChangeLogMiniGadget<b/>
//	 * method Desc :
//	 *
//	 * @return
//	 */
//	@RequestMapping(value="/gadget/device/changeLogMiniGadget")   
//    public ModelAndView getChangeLogMiniGadget() {
//	    // TODO - system 으로 변경
//    	ModelAndView mav = new ModelAndView("/gadget/device/changeLogMiniGadget");    	
//    
//    	return mav;
//    }    
//	
//	/**
//	 * method name : getChangeLogMaxGadget<b/>
//	 * method Desc :
//	 *
//	 * @return
//	 */
//	@RequestMapping(value="/gadget/device/changeLogMaxGadget")   
//    public ModelAndView getChangeLogMaxGadget() {
//	    // TODO - system 으로 변경
//    	ModelAndView mav = new ModelAndView("/gadget/device/changeLogMaxGadget");    	
//    
//    	return mav;
//    }    	

	
	   /**
     * method name : getChangeLogMiniGadget<b/>
     * method Desc :
     *
     * @return
     */
    @RequestMapping(value="/gadget/system/changeLogMiniGadget")   
    public ModelAndView getChangeLogMiniGadget() {
        // TODO - system 으로 변경
        ModelAndView mav = new ModelAndView("/gadget/system/changeLogMiniGadget");      
    
        return mav;
    }    
    
    /**
     * method name : getChangeLogMaxGadget<b/>
     * method Desc :
     *
     * @return
     */
    @RequestMapping(value="/gadget/system/changeLogMaxGadget")   
    public ModelAndView getChangeLogMaxGadget() {
        // TODO - system 으로 변경
        ModelAndView mav = new ModelAndView("/gadget/system/changeLogMaxGadget");       
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);
        return mav;
    }       

	
	
    /**
     * method name : getAuditLogRankingList<b/>
     * method Desc : ChangeLog 미니가젯의 AuditLog Ranking 리스트를 조회한다.
     *
     * @param reportName
     * @param searchStartDate
     * @param searchEndDate
     * @return
     */
    @RequestMapping(value = "/gadget/system/getAuditLogRankingList")
    public ModelAndView getAuditLogRankingList() {
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();

        // 기본 조회조건 : 현재월
        Calendar startDateCal = Calendar.getInstance();
        Calendar endDateCal = Calendar.getInstance();
        // 현재월 첫일자
        startDateCal.set(startDateCal.get(Calendar.YEAR), startDateCal.get(Calendar.MONTH), 1, 0, 0, 0);
        // 현재월 마지막 일자
        endDateCal.set(endDateCal.get(Calendar.YEAR), endDateCal.get(Calendar.MONTH), endDateCal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        endDateCal.set(Calendar.MILLISECOND, 999);

        conditionMap.put("startDate", startDateCal);
        conditionMap.put("endDate", endDateCal);
        conditionMap.put("supplierId", user.getRoleData().getSupplier().getId());

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        conditionMap.put("page", page);
        conditionMap.put("limit", limit);

        List<Map<String, Object>> result = auditLogManager.getAuditLogRankingList(conditionMap);

        mav.addObject("result", result);
        mav.addObject("totalCount", auditLogManager.getAuditLogRankingListTotalCount(conditionMap));

        return mav;
    }

    /**
     * method name : getChangeLogSelectBoxData
     * method Desc : ChangeLog 맥스가젯의 SelectBox 데이터를 가져온다.
     *
     * @return
     */
    @RequestMapping(value="/gadget/system/getChangeLogSelectBoxData")
    public ModelAndView getChangeLogSelectBoxData() {
        ModelAndView mav = new ModelAndView("jsonView");
        
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        
        for (AuditAction action : AuditAction.values()) {
            map = new HashMap<String, String>();
            map.put("id", action.name());
            map.put("name", action.name());
            list.add(map);
        }
        
        mav.addObject("action", list);
        return mav;
    }

    /**
     * method name : getAuditLogList<b/>
     * method Desc : ChangeLog 맥스가젯의 AuditLog 리스트를 조회한다.
     *
     * @param reportName
     * @param searchStartDate
     * @param searchEndDate
     * @return
     */
    @RequestMapping(value = "/gadget/system/getAuditLogList")
    public ModelAndView getAuditLogList(@RequestParam("action") String action,
            @RequestParam("equipType") String equipType,
            @RequestParam("equipName") String equipName,
            @RequestParam("propertyName") String propertyName,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("loginId") String loginId){
        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

        conditionMap.put("action", action);
        conditionMap.put("equipType", equipType);
        conditionMap.put("equipName", equipName);
        conditionMap.put("propertyName", propertyName);
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        conditionMap.put("loginId", loginId);

        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));

        conditionMap.put("page", page);
        conditionMap.put("limit", limit);

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          

        conditionMap.put("supplierId", user.getRoleData().getSupplier().getId());

        List<Map<String, Object>> result = auditLogManager.getAuditLogList(conditionMap);

        mav.addObject("result", result);
        mav.addObject("totalCount", auditLogManager.getAuditLogListTotalCount(conditionMap));

        return mav;
    }
}