package com.aimir.bo.system.gadget;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Dashboard;
import com.aimir.model.system.Operator;
import com.aimir.model.system.User;
import com.aimir.service.system.DashboardManager;
import com.aimir.service.system.RoleManager;

@Controller
public class DashboardController {
	
    @Autowired
    public DashboardManager dashboardManager;
    
    @Autowired
    public RoleManager roleManager;
    
	private final Log log = LogFactory.getLog(DashboardController.class);

    @RequestMapping(value="/gadget/gadget_setting_max.do")
    public String getDashBoards() {
//        List<Dashboard> list = dashboardManager.getDashboards();
//        ModelAndView mav = new ModelAndView();
//        mav.setViewName("/gadget/gadget_setting_max");
//        mav.addObject("dashboard", list);       
//        return mav;
    	return "/gadget/gadget_setting_max";
    }
    
    @RequestMapping(value="/gadget/getDashboards.do")
    public ModelAndView getDashBoardList(HttpServletRequest request, HttpServletResponse response, @RequestParam("tabType") String tabType) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	List<Dashboard> dashboardList = null;
    	User user = null;
    	int userId = 0;
		int roleId = 0;
    	
    	ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser aimirUser = (AimirUser)instance.getUserFromSession();
        
        if(aimirUser !=null && !aimirUser.isAnonymous()) {
			try {
			    user = aimirUser.getOperator(new Operator());
			    userId = user.getId();
				roleId = user.getRole().getId();
			} catch (Exception e1) {
				log.warn(e1, e1);
			}
			if (tabType.equals("system")) {
				dashboardList = dashboardManager.getDashboardsByRole(roleId);
			} else if (tabType.equals("user")) {
				dashboardList = dashboardManager.getDashboardsByOperator(userId);
			}
			mav.addObject("dashboard", dashboardList);
			mav.addObject("operator", userId);
        }
        return mav;
    }
    
	//대시보드 상세정보 조회
	@RequestMapping(value="/gadget/getDashboard.do")
	public ModelAndView getDashboard(@RequestParam("dashboardId") int dashboardId) {
        Dashboard dashboard = dashboardManager.getDashboard(dashboardId);        
        
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("dashboardInfo", dashboard);        
        return mav;
	}

	//대시보드 삭제
	@RequestMapping(value="/gadget/deleteDashboard.do")
	public ModelAndView deleteDashboard(@RequestParam("dashboardId") int dashboardId) {
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("id", dashboardId+"");		
		 
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();

	    AimirUser user = (AimirUser) instance.getUserFromSession();
	        
	    Boolean dashboardAuth = user.getRoleData().getHasDashboardAuth();

	    if(dashboardAuth){
			dashboardManager.delete(dashboardId);
			mav.addObject("result", "success");
		}else{
			mav.addObject("result", "No Authorization");
		}
		
		return mav;
	}
	
	@RequestMapping(value="/gadget/addDashboard.do", method = RequestMethod.POST)
	public ModelAndView addDashboard(HttpServletRequest request, HttpServletResponse response,
	        @ModelAttribute("addForm") Dashboard dashboard, BindingResult result, SessionStatus status) {
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("result", "fail");

        ESAPI.httpUtilities().setCurrentHTTP(request, response);

        // ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();

        AimirUser user = (AimirUser) instance.getUserFromSession();
        
        Boolean dashboardAuth = user.getRoleData().getHasDashboardAuth();
        
        if (user != null && !user.isAnonymous()) {
            try {
                dashboard.setRole(user.getRoleData());
        	    if(dashboardAuth){
                	dashboardManager.add(dashboard);
                	mav.addObject("result", "success");
                }else{
                	mav.addObject("result", "No Authorization");
                }
                status.setComplete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

		return mav;
	}
	
	@RequestMapping(value="/gadget/addDashboardWithUser.do", method = RequestMethod.POST)
	public ModelAndView addDashboardWithUser(HttpServletRequest request, HttpServletResponse response, 
			@ModelAttribute("addForm") Dashboard dashboard, BindingResult result, SessionStatus status) {
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("result", "fail");
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser aimirUser = (AimirUser)instance.getUserFromSession();
        
        if(aimirUser != null && !aimirUser.isAnonymous()) {
	        try {
	        	User user = aimirUser.getOperator(new Operator());
	        	if (dashboardManager.checkDashboardCountByOperator(user.getId())) {
	        		dashboard.setOperator((Operator)user);
	        		dashboardManager.add(dashboard);
	        		status.setComplete();
	        		mav.addObject("result", "success");
	        	}
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
		return mav;
	}

	@RequestMapping(value="/gadget/updateDashboard.do", method = RequestMethod.POST)
    public ModelAndView updateDashboard(@ModelAttribute("updateForm") Dashboard dashboard, BindingResult result,
            SessionStatus status) {
	    Dashboard dashbd = dashboardManager.getDashboard(dashboard.getId());
	    
	    dashbd.setName(dashboard.getName());
	    dashbd.setOrderNo(dashboard.getOrderNo());
	    dashbd.setMaxGridX(dashboard.getMaxGridX());
	    dashbd.setMaxGridY(dashboard.getMaxGridY());
	    dashbd.setDescr(dashboard.getDescr());

	    ModelAndView mav = new ModelAndView("jsonView");
	    mav.addObject("result", "Fail");
	    
	    AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
       
	    AimirUser user = (AimirUser) instance.getUserFromSession();
        
        Boolean dashboardAuth = user.getRoleData().getHasDashboardAuth();
	    if(dashboardAuth){
	    	dashboardManager.update(dashbd);
	    	mav.addObject("result", "Success");            
	    }else{
        	mav.addObject("result", "No Authorization");
        }
	    
        status.setComplete();
        return mav;
    }
}
