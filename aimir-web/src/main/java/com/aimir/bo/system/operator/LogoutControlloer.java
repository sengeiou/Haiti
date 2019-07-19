package com.aimir.bo.system.operator;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.service.system.CustomerManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.RoleManager;

/**
 * 로그아웃 시 이력 저장하는 로직
 * 현재 사용자가 로그인 했을 때 기록된 로그인이력에 로그아웃 시간을 추가한다.
 * @author yuky
 *
 */
@Controller
public class LogoutControlloer {
	private final Log log = LogFactory.getLog(LoginController.class);
	
	@Autowired
    OperatorManager userLoginManager;
    
    @Autowired
    CustomerManager customerLoginManager;
    
    @Autowired
    RoleManager userRoleManager;
    
    AimirUser user;
    Map<String, Object> condition;
    
    //@RequestMapping(method = RequestMethod.GET)
    @RequestMapping("/admin/logout.do")
    protected ModelAndView logout(HttpServletRequest request, HttpServletResponse response)
	throws Exception {
    	log.debug("============ Admin LOG OUT START =====================");
    	ModelAndView mav = new ModelAndView("jsonView");
    	logoutProcess();        
        mav.setViewName("/admin/logout");
        return mav;
    }
    
    @RequestMapping("/customer/logout.do")
    protected ModelAndView customerLogout(HttpServletRequest request, HttpServletResponse response)
	throws Exception {
    	log.debug("============ customer LOG OUT START =====================");
    	ModelAndView mav = new ModelAndView("jsonView");
    	logoutProcess();
    	mav.setViewName("/customer/logout");
        return mav;
    }
    
    private void logoutProcess(){
    	
    	HttpSession session = ESAPI.currentRequest().getSession();
    	
    	Long genId = (Long)session.getAttribute("generatedId");
    	log.debug("getUsernameParameterName " + ESAPI.securityConfiguration().getUsernameParameterName());
    	
    	if(genId != null){
    		condition = new HashMap();
        	condition.put("generatedId", genId);
        	log.debug("==========Generated ID : " + genId);
        	
        	String id = null;
        	/*id = ESAPI.currentRequest().getParameter(
        			ESAPI.securityConfiguration().getUsernameParameterName());*/

           	userLoginManager.addLogoutLog(condition);
           	log.debug("===============LOGOUT WRITE===============");
    	}else{
        	
    	}
        
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        // LOG OUT
        if(user != null){
        	user.logout();
        }
        
        
    }
}
