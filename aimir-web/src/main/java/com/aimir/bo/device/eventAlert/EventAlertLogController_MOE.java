package com.aimir.bo.device.eventAlert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.common.CommandProperty;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.service.device.EventAlertLogManager;
import com.aimir.service.device.EventAlertManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ProfileManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.CommonUtils;
import com.aimir.util.StringUtil;


@Controller
public class EventAlertLogController_MOE {
    private static Log log = LogFactory.getLog(EventAlertLogController_MOE.class);

    @Autowired
    HibernateTransactionManager transactionManager;

    @Autowired
    EventAlertLogManager eventAlertLogManager;

    @Autowired
    EventAlertManager eventAlertManager;

    @Autowired
    CodeManager codeManager;

    @Autowired
    ProfileManager profileManager;

    @Autowired
    SupplierManager supplierManager;

    @Autowired
    RoleManager roleManager;

    @RequestMapping(value = "/gadget/device/eventAlert/eventAlertLogMax_MOE")
    public ModelAndView getEventAlertLogMax(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("/gadget/device/eventAlert/eventAlertLogMax_MOE");

        String interval = CommandProperty.getProperty("event.alert.interval");

        if (StringUtil.nullToBlank(interval).isEmpty()) {
            interval = "3000";
        }
        mav.addObject("interval", interval);

        ESAPI.httpUtilities().setCurrentHTTP(request, response);

        // ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();

        AimirUser user = (AimirUser) instance.getUserFromSession();

        if (user != null && !user.isAnonymous()) {
            try {
                mav.addObject("userId", user.getOperator(new Operator()).getId());
                mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<String> eventStatusList = new ArrayList<String>();
        for (EventStatus status : EventStatus.values()) {
            if (status.name().equals("Open") || status.name().equals("Cleared")) {
                continue;
            }
            eventStatusList.add(status.name());
        }
        mav.addObject("eventStatusList", eventStatusList);

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)

        return mav;
    }

    @RequestMapping(value = "/gadget/device/eventAlert/eventAlertLogMini_MOE")
    public ModelAndView getEventAlertLogMini(HttpServletRequest request, HttpServletResponse response) {
    	//WEB Stop현상으로인해 eventAlertLogMini가젯의 RealTime기능을 제거
    	//ModelAndView mav = new ModelAndView("/gadget/device/eventAlert/eventAlertLogMiniEmpty");
    	//Mini가젯의 RealTime기능 사용시 eventAlertLogMini 로 소스 교체
        ModelAndView mav = new ModelAndView("/gadget/device/eventAlert/eventAlertLogMini_MOE");

        String interval = CommandProperty.getProperty("event.alert.interval");

        if (StringUtil.nullToBlank(interval).isEmpty()) {
            interval = "3000";
        }
        mav.addObject("interval", interval);

        DataUtil.setApplicationContext(WebApplicationContextUtils.getWebApplicationContext(request.getSession().getServletContext()));
        ESAPI.httpUtilities().setCurrentHTTP(request, response);

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        if (user != null && !user.isAnonymous()) {
            try {
                mav.addObject("userId", user.getOperator(new Operator()).getId());
                mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<String> eventStatusList = new ArrayList<String>();
        for (EventStatus status : EventStatus.values()) {
            if (status.name().equals("Open") || status.name().equals("Cleared")) {
                continue;
            }
            eventStatusList.add(status.name());
        }
        mav.addObject("eventStatusList", eventStatusList);

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)

        return mav;
    }

}