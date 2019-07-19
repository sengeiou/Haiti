package com.aimir.bo.system.gadget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.dao.system.DashboardDao;
import com.aimir.dao.system.DashboardGadgetDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Dashboard;
import com.aimir.model.system.DashboardGadget;
import com.aimir.model.system.DashboardGadgetVO;
import com.aimir.model.system.Gadget;
import com.aimir.service.system.DashboardGadgetManager;
import com.aimir.service.system.GadgetManager;
import com.aimir.service.system.RoleManager;


@Controller
public class GadgetController {

    @Autowired
    protected GadgetManager gadgetManager;
    
    @Autowired
    protected DashboardGadgetManager dashboardGadgetManager;
    
    @Autowired
    protected DashboardGadgetDao dashboardGadgetDao;
    
    @Autowired
    protected DashboardDao dashboardDao;
    
    @Autowired
    protected RoleManager roleManager;
    /**
     * <p>Searches for all gadget and returns them in a 
     * <code>List</code>.</p>
     * 
     * <p>Expected HTTP GET and request '/gadget/search'.</p>
     */
    @RequestMapping(method=RequestMethod.GET)
    public List<Gadget> gatGadget() {
        return gadgetManager.getGadgets();
    }
    
    @RequestMapping("/gadget.*")
    public String execute(ModelMap model) {
        model.addAttribute("getGadget", gadgetManager.getGadgets());
        return "getGadget";
    }
    
    @SuppressWarnings("unchecked")
	@RequestMapping(value="gadget/system/getGadgetSetting")
    public ModelAndView getGadget(@RequestParam("roleId") Integer roleId,
			@RequestParam("supplierId") Integer supplierId)
	{
		ModelMap model = new ModelMap();
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("roleId", roleId);
		conditionMap.put("supplierId", supplierId);
		
		//허가 된 가젯 fetch
		List<Gadget> gadgetList = roleManager.getGadgetListByRole2(conditionMap);
		
		model.addAttribute("gridData", gadgetList);
		return new ModelAndView("jsonView", model);
    }
    
    @RequestMapping(value="gadget/system/getGadgetByDashboard")
    public ModelAndView getDashBoardGadget(@RequestParam("dashboardId") int dashboardId){
    	  ModelAndView mav = new ModelAndView("jsonView");
    	  
    	  List<DashboardGadgetVO> gadgetList = dashboardGadgetManager.getGadgetsByDashboard(dashboardId);
    	  
    	  mav.addObject("gadgetList", gadgetList);
    	 
    	  return mav;
    }
    
    /*가젯 조회,추가,삭제,수정*/
    //가젯 추가
    @RequestMapping(value="/gadget/system/addGadget.do", method = RequestMethod.POST)
    public ModelAndView addGadget(@RequestParam("gadgetId") int gadgetId, @RequestParam("dashboardId") int dashboardId){
    	Dashboard dashboard = dashboardDao.get(dashboardId);
    	
    	ModelAndView mav = new ModelAndView("jsonView");    	

    	List<DashboardGadgetVO> gadgetList = dashboardGadgetManager.getGadgetsByDashboard(dashboardId);
    	AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        
	    AimirUser user = (AimirUser) instance.getUserFromSession();
        
        Boolean dashboardAuth = user.getRoleData().getHasDashboardAuth();
	   
    	//가젯 중복 확인
    	boolean check = true;
    	String resultMsg = "Fail";
		for(DashboardGadgetVO dg : gadgetList){
			if((dg.getGadget_id() == gadgetId)){
				check = false;
				resultMsg = "Gadget Already exists.";
			}
		}
		
		//가젯 갯수 확인
		int dbSize = gadgetList.size();
		int gridMaxX = dashboard.getMaxGridX();
		int gridMaxY = dashboard.getMaxGridY();
		
		if(dbSize > gridMaxX*gridMaxY){
			check = false;
			resultMsg = "Exceed Maximum number of Gadget.";
		}
		
		if(!dashboardAuth){
			check = false;
			resultMsg = "No Authorization";
		}
		
		if(check){
			dashboardGadgetManager.add(gadgetId, dashboardId);
			mav.addObject("result", "success");
		}else{
			mav.addObject("result", resultMsg);
		}
		return mav;
    }
    
    //가젯 삭제
    @RequestMapping(value="/gadget/system/removeGadget.do", method = RequestMethod.POST)
    public ModelAndView removeGadget(@RequestParam("dashboardGadgetId") int dashboardGadgetId){
    	
    	AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        
	    AimirUser user = (AimirUser) instance.getUserFromSession();
        
        Boolean dashboardAuth = user.getRoleData().getHasDashboardAuth();
        
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	if(dashboardAuth){
    		dashboardGadgetManager.deleteById(dashboardGadgetId);
    		mav.addObject("result", "success");
    	}else{
    		mav.addObject("result", "No Authorization");
    	}
    	
        return mav;
    }
    
    //가젯 조회
    @RequestMapping(value="gadget/system/searchGadget")
    public ModelAndView searchGadget(@RequestParam("gadgetName") String gadgetName, @RequestParam("roleId") Integer roleId){
    	  ModelAndView mav = new ModelAndView("jsonView");
    	
    	  List<Gadget> gadgetList = gadgetManager.searchGadgetList(gadgetName, roleId);
    	  mav.addObject("gadgetList", gadgetList);
    	  
    	  return mav;
    }
    
    //가젯 수정
    @RequestMapping(value="/gadget/system/updateGadget.do", method = RequestMethod.POST)
    public ModelAndView updateGadget(@ModelAttribute("gadgetInfoEditForm") Gadget gadget, BindingResult result,
            SessionStatus status) {
	    Gadget gadgetid = gadgetManager.getGadget(gadget.getId());
	    
	    gadgetid.setName(gadget.getName());
	    gadgetid.setFullHeight(gadget.getFullHeight());
	    gadgetid.setMiniHeight(gadget.getMiniHeight());
	    gadgetid.setDescr(gadget.getDescr());
	    
	    AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
	       
	    AimirUser user = (AimirUser) instance.getUserFromSession();
        
        Boolean dashboardAuth = user.getRoleData().getHasDashboardAuth();

        ModelAndView mav = new ModelAndView("jsonView");
	   
        if(dashboardAuth){
	    	gadgetManager.update(gadgetid);
	    	mav.addObject("result", "success");
	    }else{
    		mav.addObject("result", "No Authorization");
    	}
        
        status.setComplete();
        return mav;
    }
}
