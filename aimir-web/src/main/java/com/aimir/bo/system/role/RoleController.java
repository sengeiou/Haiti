package com.aimir.bo.system.role;

import com.aimir.model.system.*;
import com.aimir.service.system.*;
import com.aimir.util.CodeTypeEditor;
import com.aimir.util.GadgetTypeEditor;
import com.aimir.util.StringUtil;
import com.aimir.util.SupplierTypeEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping("/gadget/system/user_group_max.do")
public class RoleController {
	
	@Autowired
	RoleManager roleManager;
	
	@Autowired
    SupplierManager supplierManager;
	
	@Autowired
    GadgetManager gadgetManager;
	
	@Autowired
	CodeManager codeManager;
	
	@Autowired
    OperatorManager operatorManager;

    @Autowired
    GadgetRoleManager gadgetRoleManager;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Code.class, new CodeTypeEditor());		
		binder.registerCustomEditor(Supplier.class, new SupplierTypeEditor());
		binder.registerCustomEditor(Gadget.class, new GadgetTypeEditor());
	}
	
	/*
	@RequestMapping()
	public ModelAndView welcome() {
		ModelAndView mav = new ModelAndView("gadget/system/user_group_max");
		
		//명령실행
		List<Code> parentList = codeManager.getChildCodes(Code.COMMAND);	
		Map<String, List> parent = new HashMap<String, List>();
		List<Code> childList = null;
		for ( Code code : parentList ) {
			List chilren = codeManager.getChildCodes(code.getCode());
			parent.put(code.getName(), chilren);
		}
		
		mav.addObject("parentSize", parent.size());
		mav.addObject("commandList", parent);	
		
		return mav;
	}
	*/
	
	//사용자그룹 상세정보 탭 클릭했을 시 
	@RequestMapping(params = "param=userDetailInfo")
	public ModelAndView welcome(@RequestParam("supplierId") int supplierId, @RequestParam("roleId") int roleId) {
		ModelAndView mav = new ModelAndView("gadget/system/user_group_max");
		
		//명령실행
		List<Code> parentList = codeManager.getChildCodes(Code.COMMAND);	
		Map<String, List<?>> parent = new HashMap<String, List<?>>();
		//List<Code> childList = null;
		for ( Code code : parentList ) {
			List<?> chilren = codeManager.getChildCodes(code.getCode());
//			parent.put(code.getDescr(), chilren);
			parent.put(code.getName(), chilren);
		}
		
		mav.addObject("parentSize", parent.size());
		mav.addObject("commandList", parent);	
		mav.addObject("supplierId", supplierId);
		mav.addObject("roleId", roleId);
		
		return mav;
	}
	

	/**
	 * 
	 *		//사용자그룹 추가 페이지 
	 * @return
	 */
	@RequestMapping(params = "param=addViewPage")
	public ModelAndView addViewPage() {
		ModelAndView mav = new ModelAndView("gadget/system/add_user_group_max");
		
		//명령실행
		List<Code> parentList = codeManager.getChildCodes(Code.COMMAND);	
		Map<String, List<?>> parent = new HashMap<String, List<?>>();
		//List<Code> childList = null;
		
		for ( Code code : parentList ) 
		{
			List<?> chilren = codeManager.getChildCodes(code.getCode());
			parent.put(code.getName(), chilren);
		}
		
		//공급사
		List<Supplier> supplierList = supplierManager.getSuppliers();
		
		mav.addObject("parentSize", parent.size());
		
		//커맨드 리스트 setting 부분.
		mav.addObject("commandList", parent);	
		
		
		mav.addObject("supplier", supplierList);
		return mav;
	}
	
	//사용자 그룹명
	/**
	 * 초기 화면 로딩시 상단 사용자 그룹 fetch method
	 * @param supplierId
	 * @param roleId
	 * @return
	 */
	@RequestMapping(params = "param=groups")
	public ModelAndView getRoleGroups(
			@RequestParam("supplierId") int supplierId,
			@RequestParam("roleId") int roleId)
	{
		ModelMap model = new ModelMap();
		model.addAttribute("rolegroups",	roleManager.getRoleBySupplierId(supplierId));
		
		//model.addAttribute("supplierId",	supplierId);
		
		
		return new ModelAndView("jsonView", model);
	}
	
	//내가 가지고 있는 정보
	@RequestMapping(params = "param=myRoleView")
	public ModelAndView getmyRoleView(@RequestParam(value="roleId", required=false) int roleId) {
		ModelAndView mav = new ModelAndView("jsonView");
		Role role = roleManager.getRole(roleId);		
		mav.addObject("role", role);
		
		if ( role != null ) { 
			mav.addObject("myCommands", role.getCommands());
		}
		
		return mav;
	}
	
	//전체가젯 가지고 온다.
	@RequestMapping(params = "param=gadgets")
	public ModelAndView getGadgets(
			@RequestParam("roleId") Integer roleId,
			@RequestParam("supplierId") Integer supplierId
			) 
	{
		ModelMap model = new ModelMap();
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("roleId", roleId);
		conditionMap.put("supplierId", supplierId);
		
		
		
		model.addAttribute("gadgets", gadgetManager.getGadgets2(conditionMap));
		
		//model.addAttribute("gadgets", gadgetManager.getGadgets());
		
		
		return new ModelAndView("jsonView", model);
	}

	//허용된 가젯 fetch method
	@SuppressWarnings({ "unchecked"})
	@RequestMapping(params = "param=permitedGadgets")
	public ModelAndView getPermitedGadgets(
			@RequestParam("roleId") Integer roleId,
			@RequestParam("supplierId") Integer supplierId)
	{
		ModelMap model = new ModelMap();
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("roleId", roleId);
		conditionMap.put("supplierId", supplierId);

		
		
		//허가 된 가젯 fetch
		List<Gadget> permitedGadgetList = roleManager.getGadgetListByRole2(conditionMap);
		
		model.addAttribute("permitedGadget", permitedGadgetList);
		return new ModelAndView("jsonView", model);
	}

    // 전체가젯에서 허용된 가젯을 제외한 나머지 리스트
    @RequestMapping(params = "param=remainGadgets")
    public ModelAndView getRemainGadgets(@RequestParam(value = "roleId", required = false) Integer roleId,
            @RequestParam("supplierId") Integer supplierId) {
        ModelMap model = new ModelMap();
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("roleId", roleId);
        conditionMap.put("supplierId", supplierId);

        Set<Gadget> result = roleManager.getRemainGadgetList(conditionMap);

        model.addAttribute("remainGadgets", result);
        return new ModelAndView("jsonView", model);
    }

    // 상세보기화면에서 허용된 가젯 검색
    @RequestMapping(params = "param=viewPermitedSearch")
    public ModelAndView viewPermitedSearch(@RequestParam("roleId") Integer roleId,
            @RequestParam("supplierId") Integer supplierId, HttpServletRequest request) throws Exception {
        String searchWord = new String(request.getParameter("permitedGadgetSearchName").getBytes("8859_1"), "UTF-8");
        String searchType = request.getParameter("permitedGadgetSearchType");
        ModelAndView mnv = new ModelAndView("jsonView");
        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("roleId", roleId);
        conditionMap.put("supplierId", supplierId);

        // Type = 이름으로 검색
        if ("searchName".equals(searchType)) {
            conditionMap.put("gadgetName", searchWord);
        }
        // Type = Tag 로 검색
        if ("searchTag".equals(searchType)) {
            conditionMap.put("tagName", searchWord);
        }

        Set<Gadget> permited = roleManager.getGadgetListByRole(conditionMap);

        mnv.addObject("permited", permited);
        return mnv;
    }

    // 상세보기화면에서 전체 가젯 검색
    @RequestMapping(params = "param=viewAllSearch")
    public ModelAndView viewAllSearch(@RequestParam("roleId") Integer roleId,
            @RequestParam("supplierId") Integer supplierId, HttpServletRequest request) throws Exception {
        String searchWord = new String(request.getParameter("allGadgetSearchName").getBytes("8859_1"), "UTF-8");
        String searchType = request.getParameter("allGadgetSearchType");
        ModelAndView mnv = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("roleId", roleId);
        conditionMap.put("supplierId", supplierId);

        // Type = 이름으로 검색
        if ("searchName".equals(searchType)) {
            conditionMap.put("gadgetName", searchWord);
        }
        // Type = Tag 로 검색
        if ("searchTag".equals(searchType)) {
            conditionMap.put("tagName", searchWord);
        }

        Set<Gadget> result = roleManager.getRemainGadgetList(conditionMap);

        mnv.addObject("remainGadgets", result);
        return mnv;
    }
	
	// 추가화면에서 전체가젯 검색
	@RequestMapping(params = "param=gadgetSearch")
	public ModelAndView gadgetSearch( HttpServletRequest request) throws Exception {
		String searchWord = new String(request.getParameter("allGadgetSearchName").getBytes("8859_1"),"UTF-8");		
		String searchType = request.getParameter("allGadgetSearchType");
		ModelAndView mnv = new ModelAndView("jsonView");		
		List<Gadget> gadgetList = null;
		//이름으로 검색
		if ("searchName".equals(searchType)) {
			gadgetList = roleManager.search(searchWord);
		} 
		//Tag 로 검색
		if ("searchTag".equals(searchType)) {			
		}
		mnv.addObject("gadgetList" , gadgetList);
		return mnv;		
	}	

    // 가젯 허용
    @RequestMapping(params = "param=gadgetAdd")
    public ModelAndView gadgetAdd(@RequestParam("roleId") Integer roleId, @RequestParam("supplierId") Integer supplierId,
            @RequestParam("gadgetIds") String gadgetIds) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("roleId", roleId);
        conditionMap.put("supplierId", supplierId);

        String[] gadgetIdArr = StringUtil.nullToBlank(gadgetIds).split(",");
        conditionMap.put("gadgetIds", gadgetIdArr);

        roleManager.addGadgetRole(conditionMap);

        return mav;
    }

    // 허용된 가젯 삭제
    @RequestMapping(params = "param=gadgetDel")
    public ModelAndView gadgetDel(@RequestParam("roleId") Integer roleId, @RequestParam("supplierId") Integer supplierId,
            @RequestParam("gadgetIds") String gadgetIds, SessionStatus status) {
        ModelAndView mav = new ModelAndView("jsonView");

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        conditionMap.put("roleId", roleId);
        conditionMap.put("supplierId", supplierId);

        String[] gadgetIdArr = StringUtil.nullToBlank(gadgetIds).split(",");
        conditionMap.put("gadgetIds", gadgetIdArr);

        roleManager.delGadgetRole(conditionMap);
        status.setComplete();
        return mav;
    }

  //Role 추가
  	@RequestMapping(params = "param=add")
      public ModelAndView addRole(HttpServletRequest request, @ModelAttribute("addRoleModel") Role role, SessionStatus status)
              throws Exception {
          ModelAndView mav = new ModelAndView("jsonView");
          String permitedGadgetIds = StringUtil.nullToBlank(request.getParameter("permitedGadgetIds"));
          String[] gadgetIds = permitedGadgetIds.split(",");
          String codes = StringUtil.nullToBlank(request.getParameter("codes"));
//          System.out.println(">>>>>>>>>>>>> codes : " + codes);

          try {

              // 명령실행
              if ( codes.length() > 0 ) {
                  String[] codeArray = codes.split(",");

                  for(String codeId : codeArray) {
                      Code code = codeManager.getCode(Integer.parseInt(codeId));
                      role.addCommand(code);
                  }
              } 

              Role rtnRole = roleManager.addRole(role);
              GadgetRole gadgetRole = null;
              Gadget gadget = null;
              Supplier supplier = rtnRole.getSupplier();

              for (String str : gadgetIds){
                  gadget = gadgetManager.getGadget(new Integer(str));
                  gadgetRole = new GadgetRole();
                  gadgetRole.setGadget(gadget);
                  gadgetRole.setRole(rtnRole);
                  gadgetRole.setSupplier(supplier);

                  gadgetRoleManager.add(gadgetRole);
              }
              status.setComplete();
          } catch (Exception e) {
              e.toString();
              throw new Exception("저장에 실패 하였습니다.");
          }

          mav.addObject("result", "success");
          return mav;
      }
	
	//Role 추가2
		@RequestMapping(params = "param=add2")
	    public ModelAndView addRole2(HttpServletRequest request, @ModelAttribute("addRoleModel") Role role, SessionStatus status)
	            throws Exception {

	        String permitedGadgetIds = StringUtil.nullToBlank(request.getParameter("permitedGadgetIds"));
	        String[] gadgetIds = permitedGadgetIds.split(",");
	        String codes = StringUtil.nullToBlank(request.getParameter("codes"));
//	        System.out.println(">>>>>>>>>>>>> codes : " + codes);

	        try {

	            // 명령실행
	            if ( codes.length() > 0 ) {
	                String[] codeArray = codes.split(",");

	                
	                for ( int i=0; i<codeArray.length; i++ )	
	                {
	                	String[] code2 = codeArray[i].split("add_");
	                	
	                
	                    Code code = codeManager.getCode(Integer.parseInt(code2[1]));
	                    try
						{
	                    	role.addCommand(code);
						} catch (Exception e)
						{
							// TODO: handle exception
							e.printStackTrace();
						}
	                    
	                }
	                
	                
	            } 
	            
	            Role rtnRole=null;
	            
	            //FIXME : 화면 UI상에 Customerrole 설정하는 부분이 없어서 일단 무조건 false설정처리함 
	            //role.setCustomerRole(false);
	            
            	rtnRole = roleManager.addRole(role);
	            
	            
	            GadgetRole gadgetRole = null;
	            Gadget gadget = null;
	            Supplier supplier= null;
	            
            	supplier = rtnRole.getSupplier();
	            	
	            
	            

	            for (String str : gadgetIds)
	            {
	                gadget = gadgetManager.getGadget(new Integer(str));
	                gadgetRole = new GadgetRole();
	                gadgetRole.setGadget(gadget);
	                gadgetRole.setRole(rtnRole);
	                gadgetRole.setSupplier(supplier);

	                gadgetRoleManager.add(gadgetRole);
	            }
	            status.setComplete();
	        } catch (Exception e) {
	            e.toString();
	            throw new Exception("저장에 실패 하였습니다.");
	        }

	        ModelAndView mav = new ModelAndView("jsonView");

			
	        
	        mav.addObject("result", "success");
	        return mav;
	    }

	//Role 업데이트
	@RequestMapping(params = "param=update")
    public ModelAndView updateRole(@ModelAttribute("roleModel") Role role,
    		@RequestParam(value = "maxMeters") String maxMeters,
    		SessionStatus status, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mav = new ModelAndView("jsonView");

		String codes = request.getParameter("codes") == null ? "" : request.getParameter("codes");
//        System.out.println(">>>>>>>>>>>>> codes : " + codes);

		Role entity = roleManager.getRole(role.getId());
		entity.setMaxMeters(Integer.parseInt(maxMeters));
		entity.setLoginAuthority(role.getLoginAuthority());
		entity.setHasDashboardAuth(role.getHasDashboardAuth());
		entity.setMtrAuthority(role.getMtrAuthority());
		entity.setSystemAuthority(role.getSystemAuthority());
		entity.setDescr(role.getDescr());
		entity.setCommands(null);		

		if ( codes.length() > 0 ) {

			String[] codeArray = codes.split(",");

			for(String codeId : codeArray) {

				Code code = codeManager.getCode(Integer.parseInt(codeId));
				entity.addCommand(code);
			}
		} 

		try {
			roleManager.updateRole(entity);
			status.setComplete();
		} catch ( Exception e ) {
			e.printStackTrace();
			throw new Exception("변경에 실패 하였습니다.");
		}
		
		mav.addObject("result", "success");		
		return mav;
	}
	
	//Role 삭제
	@RequestMapping(params = "param=delete")
	public ModelAndView deleteRole(@ModelAttribute("role") Role role) {
		ModelAndView mav = new ModelAndView("jsonView");		
		//Role 삭제
		roleManager.deleteRole(role);			 
        mav.addObject("result", "success");     
		return mav;
	}
	
	//그룹명 중복체크
	@RequestMapping(params = "param=overlapcheck")
	public ModelAndView getmyRoleView(@RequestParam("name") String name) {
		ModelAndView mav = new ModelAndView("jsonView");
		int count = 0;
		boolean checkYN = false;
		count = roleManager.nameOverlapCheck(name);
		if ( count == 0 ) 
			checkYN = true;
		mav.addObject("checkYN", checkYN);
		mav.addObject("count", count);
		return mav;
	}

	// Check permission -- Command Execute
	@RequestMapping(params = "param=permitCommandCheck")
	public ModelAndView permitCommandCheck(@RequestParam("loginId") String loginId,
										   @RequestParam("cmdCode") String cmdCode){
		ModelAndView mav = new ModelAndView("jsonView");

		Operator operator = operatorManager.getOperatorByLoginId(loginId);
		Role role = operator.getRole();

		Set<Code> commands = role.getCommands();
		Code codeCommand = null;

		//Defaulf result
		mav.addObject("result", "false");
		mav.addObject("message", "Youe role have no permission.");

		//Customer Role
		if (role.getCustomerRole() != null && role.getCustomerRole()) {
			mav.addObject("result", "false");
			mav.addObject("message", "Customer Role");
			return mav; // 고객 권한이면
		}

		//Command Execute
		for (Iterator<Code> i = commands.iterator(); i.hasNext();) {
			codeCommand = (Code) i.next();
			if (codeCommand.getCode().equals(cmdCode)){
				mav.addObject("result", "true");
				mav.addObject("message", codeCommand.getName());
			}
		}

		return mav;
	}
}
