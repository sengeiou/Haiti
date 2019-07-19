package com.aimir.bo.system.gadget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Dashboard;
import com.aimir.model.system.DashboardGadget;
import com.aimir.model.system.DashboardGadgetPositionVO;
import com.aimir.model.system.Gadget;
import com.aimir.model.system.Operator;
import com.aimir.model.system.User;
import com.aimir.service.system.DashboardGadgetManager;
import com.aimir.service.system.DashboardManager;
import com.aimir.service.system.GadgetRoleManager;
import com.aimir.service.system.OperatorManager;

@Controller
public class DashboardGadgetManageController {
	private final Log log = LogFactory.getLog(DashboardGadgetManageController.class);
	
    @Autowired
    public DashboardManager dashboardManager;
    
    @Autowired
    public DashboardGadgetManager dashboardGadgetManager; 
    
    @Autowired
    public GadgetRoleManager gadgetRoleManager;
    
    @Autowired
    public OperatorManager operatorManager;

	@RequestMapping(value="/ajax/dashboardgadgetitems.*")
	public final ModelAndView getDashBoardItems(HttpServletRequest request, HttpServletResponse response) {
		// 구성할 가젯들을 준비한다.
		ArrayList<Object> result = new ArrayList<Object>();
		ArrayList<Dashboard> dashboardList = new ArrayList<Dashboard>();
		User user = null;
		int operatorId = 0;
		int roleId = 0;
		
//		dashboardList = (ArrayList<Dashboard>) dashboardManager.getDashboards();
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser aimirUser = (AimirUser)instance.getUserFromSession();
        Operator operator = null;
        if(aimirUser !=null && !aimirUser.isAnonymous()) {
			try {
				user = aimirUser.getOperator(new Operator());
				operatorId = user.getId();
				operator = operatorManager.getOperator(operatorId);
				log.info("RoleId[" + operator.getRoleId() + "]");
				roleId = operator.getRoleId();
			} catch (Exception e1) {
				log.warn(e1, e1);
			}
		}
        
		if (user.getShowDefaultDashboard()) {
			dashboardList = (ArrayList<Dashboard>) dashboardManager.getDashboardsByOperatorAndRole(operatorId, roleId);
		} else {
			dashboardList = (ArrayList<Dashboard>) dashboardManager.getDashboardsByOperator(operatorId);
			
			if (dashboardList.size() == 0) {
				operatorManager.getOperator(operatorId).setShowDefaultDashboard(true);
				dashboardList = (ArrayList<Dashboard>) dashboardManager.getDashboardsByRole(roleId);
			}
		}
		
		Iterator<Dashboard> it = dashboardList.iterator();
		while(it.hasNext()){
			Dashboard dashboard = (Dashboard)it.next();
			result.add(getTabItems(dashboard));
		}

		ModelAndView mav = new ModelAndView();
		mav.addObject("result", result);
		
		//2011-04-01 kskim  help link 에 사용될 언어 코드
		String language = operator.getSupplier().getLang().getCode_2letter();
		mav.addObject("language", language);
		
		mav.setViewName("jsonView");
		return mav;
	}
    
	@RequestMapping(value="/ajax/dashboardgadgetitemsRole.*")
	public final ModelAndView getDashBoardItemsRole(HttpServletRequest request, HttpServletResponse response) {
    	/*
    	 * ej8486
    	 * 2010.08.17 
    	 * 1. 사용자 정의 대시보드 + 2. 표준 대시보드 (설정에 따라) 로딩
    	 */
		ArrayList<Object> result = new ArrayList<Object>();
		ArrayList<Dashboard> dashboardList = new ArrayList<Dashboard>();
		User user = null;
		int operatorId = 0;
		int roleId = 0;
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser aimirUser = (AimirUser)instance.getUserFromSession();
        Operator operator = null;
        if(aimirUser !=null && !aimirUser.isAnonymous()) {
			try {
				user = aimirUser.getOperator(new Operator());
				operatorId = user.getId();
				operator = operatorManager.getOperator(operatorId);
                log.info("RoleId[" + operator.getRoleId() + "]");
                roleId = operator.getRoleId();
			} catch (Exception e1) {
				log.warn(e1, e1);
			}
		}
        
		if (user.getShowDefaultDashboard()) {
			dashboardList = (ArrayList<Dashboard>) dashboardManager.getDashboardsByOperatorAndRole(operatorId, roleId);
		} else {
			dashboardList = (ArrayList<Dashboard>) dashboardManager.getDashboardsByOperator(operatorId);
			
			if (dashboardList.size() == 0) {
				operatorManager.getOperator(operatorId).setShowDefaultDashboard(true);
				dashboardList = (ArrayList<Dashboard>) dashboardManager.getDashboardsByRole(roleId);
			}
		}

		Iterator<Dashboard> it = dashboardList.iterator();
		while(it.hasNext()){
			Dashboard dashboard = (Dashboard)it.next();
			result.add(getTabItems(dashboard));
		}

		ModelAndView mav = new ModelAndView();
		mav.addObject("result", result);
		mav.addObject("loginId", aimirUser.getLoginId());
		
		//2011-04-01 kskim  help link 에 사용될 언어 코드
		String language = operator.getSupplier().getLang().getCode_2letter();
		mav.addObject("language", language);
		
		
		
		mav.setViewName("jsonView");
		return mav;
//		//로그인한 사용자의 operatorId(우선순위 1), role(우선순위 2)에 따라 구성할 가젯들을 준비한다.
//    	
//		ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
//		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
//		
//		AimirUser user = (AimirUser) instance.getUserFromSession();
//
//		ArrayList<Object> result = new ArrayList<Object>();
//		ArrayList<Dashboard> dashboardList = new ArrayList<Dashboard>();
//		dashboardList = (ArrayList<Dashboard>) dashboardManager.getDashboardsByOperator(Integer.parseInt(user.getAccountId()+""));
//		
//		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ operator(우선순위 1)로 검색 dashboardList.size : " + dashboardList.size());
//		
//		if(dashboardList.size() == 0){
//			dashboardList = (ArrayList<Dashboard>) dashboardManager.getDashboardsByRole(user.getRoleData().getId());
//		}
//		
//		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ role(우선순위 2)로 검색 dashboardList.size : " + dashboardList.size());
//		
//		
//		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@ 1,2 순위 경우에 해당하지 않으면 디폴트 dashboard를 가져와 출력.");
//		if(dashboardList.size() == 0){
////			dashboardList = (ArrayList<Dashboard>) dashboardManager.getDashboardsByDefault();
//		}
//		
//		Iterator<Dashboard> it = dashboardList.iterator();
//		while(it.hasNext()){
//			Dashboard dashboard = it.next();
//			result.add(getRoleTabItems(dashboard, user.getRoleData().getId().toString()));
//		}
//		
//		ModelAndView mav = new ModelAndView();
//		mav.addObject("result", result);
//		mav.addObject("loginId", user.getLoginId());
//		mav.setViewName("jsonView");
//		return mav;
		
	}
	
    @RequestMapping(value="/ajax/setItemsPosition.*")
	public final ModelAndView saveLayout(@RequestParam(value="tuid") String tabID, @RequestParam(value="data") String data) throws JSONException {
        ArrayList<DashboardGadgetPositionVO> al = new ArrayList<DashboardGadgetPositionVO>();
        JSONObject obj = JSONObject.fromObject(data);
        JSONObject obj2 = new JSONObject();

        JSONArray array = (JSONArray)obj.get("data");
        for (int i=0; i<array.size(); i++) {
            DashboardGadgetPositionVO gadget = new DashboardGadgetPositionVO();
            obj2=array.getJSONObject(i);

            gadget.setUid(obj2.getString("uid"));
            gadget.setColumnIndex(obj2.getInt("columnIndex"));
            gadget.setPosition(obj2.getInt("position"));
            
            al.add(gadget);
        }
        dashboardGadgetManager.updatePosition(al);

        ModelAndView mav = new ModelAndView();
        mav.addObject("result", "성공");
        mav.setViewName("jsonView");
        return mav;
	}
    
    @RequestMapping(value="/ajax/deleteItem.*")
    public final ModelAndView deleteItem(@RequestParam(value="uid") String UID) {
        dashboardGadgetManager.deleteById(Integer.parseInt(UID));

        ModelAndView mav = new ModelAndView();
        mav.addObject("result", "성공");
        mav.setViewName("jsonView");
        return mav;
    }

    @RequestMapping(value="/ajax/getIFrame.do")
    public final ModelAndView getIFrame(@RequestParam("url") String url) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("src", url);
        mav.setViewName("/gadget/iframe");
        return mav;
    }

	@RequestMapping(value="/ajax/isfullitem.*", method=RequestMethod.GET)
	public final ModelAndView getIsMaxItem(@RequestParam(value="uid", required=true) String uid, @RequestParam(value="type", required=true) String type) {
		
		DashboardGadget dashboardGadget = dashboardGadgetManager.getDashBoardGadget(Integer.parseInt(uid));
		Gadget gadget = dashboardGadget.getGadget();
		
		HashMap<String, Object> result = getMaxGadgetItems(dashboardGadget, gadget, uid, type);

		ModelAndView mav = new ModelAndView();
		mav.addObject("result", result);
		mav.setViewName("jsonView");
		return mav;
	}
	
	@RequestMapping(value="/ajax/getGadgetByDashboard.do", method=RequestMethod.POST)
	public final ModelAndView getGadgetByDashboard(@RequestParam("dashboardId") int dashboardId) {

		List<DashboardGadget> list = dashboardGadgetManager.getDashBoardGadgetsByDashboard(dashboardId);
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("gadgetList", list);
		return mav;
	}
	

	public HashMap<String, Object> getGadgetItems(Gadget gadget,String uid, String type) {
		HashMap<String, Object> gadgetItem = new HashMap<String, Object>();

		gadgetItem.put("uid", uid);
        gadgetItem.put("title", gadget.getName());

        //2011-03-30 kskim
        gadgetItem.put("gadgetCode", gadget.getGadgetCode()); // 가젯 코드로 도움말 링크
        
        
		if (type == "iframeportlet") {
            gadgetItem.put("xtype", "iframeportlet");
            gadgetItem.put("defaultSrc", gadget.getMiniUrl());
            gadgetItem.put("type", "iframeportlet");
		}
		if (type == "portlet") {
			HashMap<String, Object> al = new HashMap<String, Object>();
			al.put("url",gadget.getMiniUrl());
			al.put("scripts", true);
            gadgetItem.put("autoLoad", al);
            gadgetItem.put("type", "portlet");
		}

		gadgetItem.put("autoScroll", false);
		HashMap<String, Object> lm = new HashMap<String, Object>();
        lm.put("msg", "&nbsp;");
        gadgetItem.put("loadMask", lm);
        gadgetItem.put("height", gadget.getMiniHeight());

		return gadgetItem;
	}

	public HashMap<String, Object> getMaxGadgetItems(DashboardGadget dashboardGadget, 
													 Gadget gadget, 
													 String uid,  String type) {
		HashMap<String, Object> gadgetItem = new HashMap<String, Object>();

        gadgetItem.put("uid", uid);
        gadgetItem.put("title", gadget.getName());

      //2011-03-30 kskim
        gadgetItem.put("gadgetCode", gadget.getGadgetCode()); // 가젯 코드로 도움말 링크
        
        if (type.equals("iframeportlet")) {
            gadgetItem.put("xtype", "iframeportlet");
            gadgetItem.put("defaultSrc", gadget.getMaxUrl());
            gadgetItem.put("type", "iframeportlet");
        }
		if (type.equals("portlet")) {
			HashMap<String, Object> al = new HashMap<String, Object>();
			al.put("url",gadget.getMaxUrl());
			al.put("scripts", true);
            gadgetItem.put("autoLoad", al);
		}

        gadgetItem.put("autoScroll", false);
        HashMap<String, Object> lm = new HashMap<String, Object>();
        lm.put("msg", "&nbsp;");
        gadgetItem.put("loadMask", lm);
        gadgetItem.put("layout", dashboardGadget.getLayout());
        gadgetItem.put("height", gadget.getFullHeight());
        gadgetItem.put("collapsible", false);

		return gadgetItem;
	}
	
	public HashMap<String, Object> getRoleTabItems(Dashboard dashboard, String roleId) {
		HashMap<String, Object> tab = new HashMap<String, Object>();
		HashMap<String, Object> gadget = new HashMap<String, Object>();
		//int columnN = dashboard.getMaxGridX();
		//int rowN = dashboard.getMaxGridY();

		ArrayList<Object> items 					= new ArrayList<Object>();
		Set<DashboardGadget> dashboardGadgetList 	= (Set<DashboardGadget>) dashboard.getDashboardGargets();
		DashboardGadget dashboardGadget 			= null;
		Map<String, Object> params					= null;
		List<Map<String, Object>> gadgetRoleList 	= null;
		String dashboardGadget_gadgetId 			= "";
		String gadGetRole_gadgetId 					= "";
		
		Iterator<DashboardGadget> it = dashboardGadgetList.iterator();
		
		while(it.hasNext()){
			
			dashboardGadget_gadgetId 			= "";
			gadGetRole_gadgetId 				= "";
			
			dashboardGadget = (DashboardGadget)it.next();
			
			dashboardGadget_gadgetId = dashboardGadget.getGadget().getId().toString();
			
			params = new HashMap<String, Object>();
			
			params.put("roleId", roleId);
			params.put("gadgetId", dashboardGadget_gadgetId);
			
			/*GadgetRole 테이블에서 해당 가젯의 권한을 가지고 있는지 체크*/
			gadgetRoleList = gadgetRoleManager.getGadgetRolesList(params);
			
			if(gadgetRoleList.size() > 0){
				gadGetRole_gadgetId = ((Map<String, Object>)gadgetRoleList.get(0)).get("gadgetId").toString();
			}
			
			if(!"".equals(dashboardGadget_gadgetId) && !"".equals(gadGetRole_gadgetId) && dashboardGadget_gadgetId.equals(gadGetRole_gadgetId)){
				gadget = getGadgetItems(dashboardGadget.getGadget(),dashboardGadget.getId().toString(), "iframeportlet");
				gadget.put("columnIndex", dashboardGadget.getGridX());
				gadget.put("position", dashboardGadget.getGridY());
				items.add(gadget);
			}
			/*GadgetRole 테이블에서 해당 가젯의 권한을 가지고 있는지 체크*/
		}
		
		tab.put("title", dashboard.getName());
        tab.put("uid", dashboard.getId());
		tab.put("data", items);

		return tab;
	}
	
	public HashMap<String, Object> getTabItems(Dashboard dashboard) {
		HashMap<String, Object> tab = new HashMap<String, Object>();
		HashMap<String, Object> gadget = new HashMap<String, Object>();
		//int columnN = dashboard.getMaxGridX();
		//int rowN = dashboard.getMaxGridY();

		ArrayList<Object> items = new ArrayList<Object>();
		Set<DashboardGadget> dashboardGadgetList = (Set<DashboardGadget>) dashboard.getDashboardGargets();

		Iterator<DashboardGadget> it = dashboardGadgetList.iterator();
		while(it.hasNext()){
			DashboardGadget dashboardGadget = (DashboardGadget)it.next();
			gadget = getGadgetItems(dashboardGadget.getGadget(),dashboardGadget.getId().toString(), "iframeportlet");
			gadget.put("columnIndex", dashboardGadget.getGridX());
			gadget.put("position", dashboardGadget.getGridY());
			items.add(gadget);
		}
		
		tab.put("title", dashboard.getName());
        tab.put("uid", dashboard.getId());
		tab.put("data", items);

		return tab;
	}
}