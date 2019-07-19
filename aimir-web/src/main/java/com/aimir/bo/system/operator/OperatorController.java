/**
 * OperatorController.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.bo.system.operator;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncryptionException;
import org.owasp.esapi.errors.EnterpriseSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import com.aimir.bo.system.prepaymentMgmt.PrepaymentChargeController;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.dao.system.LoginLogDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Customer;
import com.aimir.model.system.LoginLog;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.User;
import com.aimir.service.system.CustomerManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.OperatorMakeExcel;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;
 

/**
 * OperatorController.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 6.   v1.0                
 *
 */
@Controller
public class OperatorController{

    @Autowired
    OperatorManager operatorManager;
    
    @Autowired
    CustomerManager customerManager;

    @Autowired
    RoleManager roleManager;
    
    @Autowired
    SupplierManager supplierManager;
    
    @Autowired
    LoginLogDao loginLogDao;
    
    @Autowired
    private MessageSource messageSource;
    
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    
    /**
     * 
     * @desc 사용자관리 max 가젯 불러오기
     * @param request
     * @param response
     * @return
     */
	@RequestMapping(value = "/gadget/system/operatorMgmtMax.do")
	public ModelMap operatorMgmtMax(HttpServletRequest request,
			HttpServletResponse response)
	{

		ESAPI.httpUtilities().setCurrentHTTP(request, response);

		// ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		ModelMap model = new ModelMap();

		// if there's a user in the session then use that
		AimirUser user = (AimirUser) instance.getUserFromSession();

		if (user == null || user.isAnonymous())
		{
			model.addAttribute("roleList", null);
			model.addAttribute("roleId", "");

		} else
		{
			int roleId = user.getRoleData().getId();
			int supplierId = user.getRoleData().getSupplier().getId();

			List<Role> roles = roleManager.getRoleBySupplierId(supplierId);

			model.addAttribute("roleList", roles);
			model.addAttribute("roleId", roleId);
			model.addAttribute("supplierId", supplierId);
		}
		return model;
	}

	@RequestMapping(value={"/gadget/accountSetting.do", "/gadget/accountFirstSetting.do"})
	public ModelMap  accountInfo(HttpServletRequest request, HttpServletResponse response) {

		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		    
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
		ModelMap model = new ModelMap();
		
		// if there's a user in the session then use that
		AimirUser user = (AimirUser)instance.getUserFromSession();
		   
        if(user==null || user.isAnonymous()){
        	model.addAttribute("roleList", null);
			model.addAttribute("roleId", "");
	        
        }else{
        	int operatorId = (int)user.getAccountId();
        	Operator operator = operatorManager.getOperator(operatorId);
        	int roleId = operator.getRoleId();
        	int supplierId = operator.getSupplierId();
        	String loginId = operator.getLoginId();
    		
        	model.addAttribute("operatorId", operatorId);
			model.addAttribute("roleId", roleId);
			model.addAttribute("supplierId", supplierId);
			model.addAttribute("loginId", loginId);
        }
    	return model;
	}
	
	@RequestMapping(value="/gadget/accountSetting_customer.do")
	public ModelMap  accountCustomerInfo(HttpServletRequest request, HttpServletResponse response) {

		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		    
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
		ModelMap model = new ModelMap();
		
		// if there's a user in the session then use that
		AimirUser user = (AimirUser)instance.getUserFromSession();
		   
        if(user==null || user.isAnonymous()){
        	model.addAttribute("roleList", null);
			model.addAttribute("roleId", "");
	        
        }else{
        	int operatorId = (int)user.getAccountId();
        	int roleId = user.getRoleData().getId();
        	int supplierId = user.getRoleData().getSupplier().getId();
    		
        	model.addAttribute("operatorId", operatorId);
			model.addAttribute("roleId", roleId);
			model.addAttribute("supplierId", supplierId);
        }
    	return model;
	}
	
	/*
	@RequestMapping(value="/gadget/system/operatorMgmtMax.do")
	public ModelMap  operatorList(@RequestParam("roleId") int roleId) {

		ModelMap model = new ModelMap();
		int supplierId = roleManager.getRole(roleId).getSupplier().getId();
		List<Role> roles = roleManager.getRoleBySupplierId(supplierId);
		
		model.addAttribute("roleList", roles);
		model.addAttribute("roleId", roleId);
    	return model;
	}
	 */
	
	@RequestMapping(value="/gadget/system/operatorMgmtMini.do")
	public ModelMap  operatorMiniList(HttpServletRequest request, HttpServletResponse response) {
				
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
    	ModelMap model = new ModelMap();
		
        // if there's a user in the session then use that
        AimirUser user = (AimirUser)instance.getUserFromSession();
   //	  AimirUser user = (AimirUser)instance.getCurrentUser();
        if(user==null || user.isAnonymous()){
        	model.addAttribute("roleList", null);
			model.addAttribute("roleId", "");
	        
        }else{
        	int roleId = user.getRoleData().getId();
        	int supplierId = user.getSupplier().getId();
    		
			List<Role> roles = roleManager.getRoleBySupplierId(supplierId);
			model.addAttribute("roleList", roles);
			model.addAttribute("roleId", roleId);
        }
        
    	return model;
	}
	
	
	@RequestMapping(value = "/gadget/system/operator/detailOperator.do")
	public ModelMap getOperatorInfo(@RequestParam("operatorId") int operatorId, @RequestParam("roleId") int roleId)
	{
		ModelMap mav = new ModelMap();
		Operator operator = new Operator();
		Role role = roleManager.getRole(roleId);
		Boolean customerRole = role.getCustomerRole() == null ? false : role.getCustomerRole();
		String customerNo = "";
		if(customerRole) {
			Customer customer = customerManager.getCustomerForUser(operatorId);
			customerNo = customer.getCustomerNo();
			operator.setId(customer.getId());
			operator.setLoginId(customer.getLoginId());
			operator.setLoginDenied(customer.getLoginDenied());
			operator.setName(customer.getName());
			operator.setAliasName(customer.getAliasName());
			operator.setEmail(customer.getEmail());
			operator.setTelNo(customer.getTelNo());
			operator.setLastPasswordChangeTimeLocale(customer.getLastPasswordChangeTimeLocale());
			operator.setLocation(customer.getLocation());
			operator.setUseLocation(customer.getUseLocation());
			operator.setRole(customer.getRole());
			operator.setSupplier(customer.getSupplier());
			operator.setShowDefaultDashboard(customer.getShowDefaultDashboard());
		} else {
			operator = operatorManager.getOperator(operatorId);
		}
		
		mav.addAttribute("operator", operator);
		mav.addAttribute("customerNo", customerNo);
		
		return mav;
	}
    
    @RequestMapping(value="/gadget/system/operator/detailPersonalOperator.do")
    public ModelMap getOperatorPersonalInfo(@RequestParam("operatorId") int operatorId, @RequestParam("loginId") String loginId) {
        User loginUser = operatorManager.getUser(operatorId, loginId);
        ModelMap model = new ModelMap();
        model.addAttribute("operator", loginUser);
        return model;
    }
    
    @RequestMapping(value="/gadget/system/membership/membershipModify.do")
    public ModelMap getCustomerAccountModify(@RequestParam("operatorId") int operatorId) {
    	
    	Operator operator = operatorManager.getOperator(operatorId);
    	String pucNumber = operator.getPucNumber();
    	String changePucNumber;
    	String pucNumber1;
    	
    	try {
    		
    		changePucNumber = ESAPI.encryptor().decrypt(pucNumber);
    		pucNumber1 = changePucNumber.substring(0, 6);
		} catch (EncryptionException e) {

			changePucNumber = null;
			pucNumber1 = null;
			
			e.printStackTrace();
		}
    		
    	ModelMap modelMap = new ModelMap();
    	
    	modelMap.addAttribute(operator);
    	modelMap.addAttribute("pucNumber1", pucNumber1);

    	return modelMap;
    }
    
    @RequestMapping(value="/gadget/system/membership/membershipDelete.do")
    public ModelMap getCustomerAccountDelete(@RequestParam("operatorId") int operatorId) {
        return new ModelMap(operatorManager.getOperator(operatorId));
    }

    @RequestMapping(value = "/gadget/system/operator/getOperatorList.do")
    @Deprecated
   	public ModelAndView getOperatorList(@RequestParam("roleId") int roleId)
   	{
   		 ModelMap modelMap = new ModelMap();
   		 modelMap.addAttribute("gridDatas", operatorManager.getOperatorsByRole(roleId));
   		 return new ModelAndView("jsonView", modelMap);
   	}

    @RequestMapping(value = "/gadget/system/operator/getOperatorListByRole.do")
    public ModelAndView getOperatorListByRole(@RequestParam("roleId") Integer roleId,
            @RequestParam("limit") Integer limit,
            @RequestParam("page") Integer page) {
        ModelMap modelMap = new ModelMap();
        
        Role role = roleManager.getRole(roleId);
        
        Boolean isCustomerRole = role.getCustomerRole();

        if(isCustomerRole) {
	        Map<String, Object> conditionMap = new HashMap<String, Object>();
	        conditionMap.put("roleId", roleId);
	        conditionMap.put("limit", limit);
	        conditionMap.put("page", page);
	        modelMap.addAttribute("gridDatas", customerManager.getCustomerListByRole(conditionMap));
	        modelMap.addAttribute("totalCount", customerManager.getCustomerListByRoleTotalCount(conditionMap));
        } else {
	        Map<String, Object> conditionMap = new HashMap<String, Object>();
	        conditionMap.put("roleId", roleId);
	        conditionMap.put("limit", limit);
	        conditionMap.put("page", page);
	        modelMap.addAttribute("gridDatas", operatorManager.getOperatorListByRole(conditionMap));
	        modelMap.addAttribute("totalCount", operatorManager.getOperatorListByRoleTotalCount(conditionMap));
        }
        return new ModelAndView("jsonView", modelMap);
    }

    @RequestMapping("/gadget/system/operator/deleteOperator.do")
    public ModelAndView deleteOperator(@RequestParam("operatorId") int operatorId, @RequestParam("customerRole") Boolean customerRole,
    		HttpServletRequest request, HttpServletResponse response) throws Exception {     
    	ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("id", operatorId);
		
		//로그부터 삭제
		LoginLog loginLog = loginLogDao.findByCondition("operator.id", operatorId);
		if(loginLog != null)
		    loginLogDao.deleteById(loginLog.getId());
		try{
			if(customerRole) {  
				Customer customer = customerManager.getCustomer(operatorId);
                customer.setPassword(null);
                customer.setRole(null);
                customer.setShowDefaultDashboard(null);
                customer.setLoginDenied(null);
                customer.setLastPasswordChangeTime(null);
                customer.setLoginId(null);
				customerManager.updateCustomer(customer);
			} else {
				Operator operator = operatorManager.getOperator(operatorId);
				operatorManager.deleteOperator(operator);
			}
			
			mav.addObject("result", "success");
		}catch(Exception e){
			ESAPI.httpUtilities().setCurrentHTTP(request, response);
			// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
			AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
			AimirUser user = (AimirUser)instance.getUserFromSession();
			
			String country = user.getSupplier().getCountry().getCode_2letter();
			String lang    = user.getSupplier().getLang().getCode_2letter();
			Locale locale = new Locale(lang, country);
			
			String errorMessage = this.messageSource.getMessage("aimir.alert.cant.used.delete", null,"You can not delete the user who has used the AiMiR.", locale);
			
			mav.addObject("result", errorMessage);
		}
		
		
		return mav;
    }
    
	//중복체크
	@RequestMapping(value="/gadget/system/operator/checkDuplicateLoginId.do")
	public ModelAndView checkDuplicateLoginId(@RequestParam("loginId") String loginId) {
		ModelAndView mav = new ModelAndView("jsonView");		

		boolean check = false;	
		
        int count = customerManager.loginIdOverlapCheck(loginId, null);
        
        if (count == 0) {
            check = operatorManager.checkDuplicateLoginId(loginId);
        }
        
		mav.addObject("dupCheck", check);
		return mav;
	}
	
	//비번체크
	@RequestMapping(value="/gadget/system/operator/checkPassword.do")
	public ModelAndView checkPassword(HttpServletRequest request, HttpServletResponse response) {
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
	    
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
		
		String loginId = ESAPI.currentRequest().getParameter( "loginId");
		String passwd = ESAPI.currentRequest().getParameter( "oldPassword");

		ModelAndView mav = new ModelAndView("jsonView");
		String password = null;
		boolean check = false;
		
		try{
			password = instance.hashPassword(passwd, loginId);
		}catch(EnterpriseSecurityException e){
    		
			mav.addObject("pwCheck", check);
    	}
		
		if(password!=null)
			check = operatorManager.checkPassword(loginId, password);	
		
		mav.addObject("pwCheck", check);
		return mav;
	}

	//비번체크
	/**
	 * @desc: 로긴 로그 그리드 fetch method
	 * @param roleId
	 * @param loginId
	 * @param ipAddr
	 * @param login
	 * @param logOut
	 * @param loginFail
	 * @param searchStartDate
	 * @param searchEndDate
	 * @param curPage
	 * @return
	 */
	@RequestMapping(value="/gadget/system/operator/getLoginLogGrid.do")
	public ModelAndView getLoginLogGrid(@RequestParam(value="roleId" 	,required=false) String roleId
			                          , @RequestParam(value="loginId" 	,required=false) String loginId
			                          , @RequestParam(value="ipAddr" 	,required=false) String ipAddr
			                                                                             
			                          , @RequestParam(value="login" 	,required=false) boolean login
			                          , @RequestParam(value="logOut" 	,required=false) boolean logOut
			                          , @RequestParam(value="loginFail" ,required=false) boolean loginFail
			                          
			                          , @RequestParam(value="searchStartDate" ,required=false) String searchStartDate
			                          , @RequestParam(value="searchEndDate"   ,required=false) String searchEndDate
			                          
			                          , @RequestParam(value="curPage"   ,required=false) String curPage) {
		
		
		if(curPage == null) {
	    	HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
	    	curPage = request.getParameter("page");
    	}
		
		Map<String, Object> condition = new HashMap<String, Object>();        		
		
				
		condition.put("roleId",      roleId       );
		
		Role role = roleManager.getRole(Integer.parseInt(roleId));
		condition.put("roleName",      role.getName()       );
		
		condition.put("loginId",    loginId       );
		condition.put("ipAddr",		ipAddr        );
		
		condition.put("login",      login         );
		condition.put("logOut",     logOut        );
		condition.put("loginFail",  loginFail     );
		
		condition.put("searchStartDate",     searchStartDate   );
		condition.put("searchEndDate",  	 searchEndDate     );
		
		condition.put("curPage",     curPage      );
		
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();          
        
        int supplierId = user.getRoleData().getSupplier().getId();
        condition.put("supplierId", supplierId);

		List<Object> loginLogGrid = operatorManager.getLoginLogGrid(condition);
		
		
		ModelAndView mav = new ModelAndView("jsonView");
		
		mav.addObject("totalCnt" , loginLogGrid.get(0));
        mav.addObject("gridData" , loginLogGrid.get(1));
		
		return mav;
	}
	
    @RequestMapping(method = RequestMethod.GET, value="/gadget/system/operator/changeLocale.do")
    public ModelAndView changeLocale(String locale, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mav = new ModelAndView("/result");

        Locale setlocale = new Locale(locale);
        WebUtils.setSessionAttribute(request,"org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE", setlocale);
        return mav;
    }

    /**
     * update password
     * @param operatorId
     * @param loginId
     * @param passwd
     * @return
     */
	@RequestMapping(value = "/gadget/system/operator/updatePassword.do")
	public ModelAndView updatePassword(@RequestParam("userId") int userId, @RequestParam("loginId") String loginId, @RequestParam("password") String passwd)
	{
		AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
		String password = null;
		try{
			//Hash the password.
			password = instance.hashPassword(passwd, loginId);
		}catch(EnterpriseSecurityException e){
			Log log = LogFactory.getLog(PrepaymentChargeController.class);
			log.debug(e,e);	
    	}

        Operator preOperator = operatorManager.getOperator(userId);
        preOperator.setPassword(password);
        preOperator.setIsFirstLogin(false);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        preOperator.setLastPasswordChangeTime(formatter.format(Calendar.getInstance().getTime()));
        operatorManager.updateOperator(preOperator);
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("result", "success");
		mav.addObject("operatorId", userId);
		mav.addObject("loginId", loginId);
		return mav;
	}
    
    /**
     * @desc 유저관리(오퍼레이터) 엑셀 export 팝업
     * 
     * @return
     */
    //gadget/system/operator/operatorExcelDownloadPopup
    @RequestMapping(value = "/gadget/system/operator/operatorExcelDownloadPopup")
	public ModelAndView operatorExcelDownloadPopup()
	{
		// ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();
		Integer supplierId = user.getRoleData().getSupplier().getId();

		

		ModelAndView mav = new ModelAndView("/gadget/system/operator/operatorExcelDownloadPopup");
		
		mav.addObject("supplierId", supplierId);
		
		

		return mav;
	}
    
    /**
     * 
     * @desc  excel make method
     * 
     * @param supplierId
     * @param tabType
     * @param search_from
     * @param svcTypeCode_input
     * @param protocolCode_input
     * @param senderId
     * @param receiverId
     * @param hourlyStartDate
     * @param hourlyEndDate
     * @param hourlyStartHourCombo_input
     * @param hourlyEndHourCombo_input
     * @param periodType_input
     * @param periodStartDate
     * @param periodEndDate
     * @param weeklyYearCombo_input
     * @param weeklyMonthCombo_input
     * @param weeklyWeekCombo_input
     * @param monthlyYearCombo_input
     * @param monthlyMonthCombo_input
     * @param msg_time
     * @param msg_datatype
     * @param msg_protocol
     * @param msg_sender
     * @param msg_receiver
     * @param msg_sendbytes
     * @param msg_receivebytes
     * @param msg_result
     * @param msg_totalcommtime
     * @param filePath
     * @return
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping(value="gadget/system/operator/operatorExcelMake")
    public ModelAndView operatorExcelMake (
    		
    		@RequestParam("supplierId") String supplierId,
    		@RequestParam("tabType") String tabType,
    		@RequestParam("search_from") String search_from,
    		@RequestParam("dailyStartDate") String dailyStartDate,
    		@RequestParam("periodType_input") String periodType_input,
    		@RequestParam("periodStartDate") String periodStartDate,
    		@RequestParam("periodEndDate") String periodEndDate,
    		@RequestParam("weeklyYearCombo_input") String weeklyYearCombo_input,
    		@RequestParam("weeklyMonthCombo_input") String weeklyMonthCombo_input,
    		@RequestParam("weeklyWeekCombo_input") String weeklyWeekCombo_input,
    		@RequestParam("monthlyYearCombo_input") String monthlyYearCombo_input,
    		@RequestParam("monthlyMonthCombo_input") String monthlyMonthCombo_input,
    		@RequestParam("loginStatusCheckedValue") String loginStatusCheckedValue,
    		@RequestParam("roleId") String roleId,
    		
    		@RequestParam("loginLogLoginId") String loginLogLoginId,
    		@RequestParam("loginLogIpAddr") String loginLogIpAddr,
    		
    		
    		@RequestParam("msg_number") String msg_number,
    		@RequestParam("msg_userid") String msg_userid,
    		@RequestParam("msg_username") String msg_username,
    		@RequestParam("msg_usergroup") String msg_usergroup,
    		@RequestParam("msg_ipaddress") String msg_ipaddress,
    		@RequestParam("msg_loginhour") String msg_loginhour,
    		@RequestParam("msg_logouthour") String msg_logouthour,
    		@RequestParam("msg_status") String msg_status,
    		@RequestParam("filePath") String filePath
    		
    		) 
	{
		
        ModelAndView mav = new ModelAndView("jsonView");        
        List result = null;
        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
//        List<MeteringListData> list = new ArrayList<MeteringListData>();
        List<Object> list = new ArrayList<Object> ();

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        boolean isLast = false;
        Long total = 0L;        // 데이터 조회건수
        Long maxRows = 5000L;   // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

        final String dayWeekPrefix = "OperatorLoginLogDataDayWeek";    //19글자
        final String seasonPrefix  = "OperatorLoginLogDataSeason";     //18글자
        final String yearPrefix    = "OperatorLoginLogDataYear";       //16글자
        final String dailyPrefix    = "operatorLoginLogDataHourly";       //16글자
        final String hourPrefix    = "operatorLoginLogDataHourly";       //16글자
        final String dayPrefix     = "operatorLoginLogDataDay";        //15글자
        final String weekPrefix    = "operatorLoginLogDataWeek";       //16글자
        final String monthPrefix   = "operatorLoginLogDataMonth";      //17글자

        Map<String, Object> conditionMap = new HashMap<String, Object>();
        
    
        
        conditionMap.put("loginStatusCheckedValue",loginStatusCheckedValue );
        conditionMap.put("tabType",tabType );
        conditionMap.put("search_from",search_from );
        conditionMap.put("supplierId",supplierId );

    	conditionMap.put("dailyStartDate", dailyStartDate);
    	
    	conditionMap.put("periodType_input", periodType_input);
    	conditionMap.put("periodStartDate", periodStartDate);
    	conditionMap.put("periodEndDate", periodEndDate);
    	
    	conditionMap.put("weeklyYearCombo_input", weeklyYearCombo_input);
    	conditionMap.put("weeklyMonthCombo_input", weeklyMonthCombo_input);
    	conditionMap.put("weeklyWeekCombo_input", weeklyWeekCombo_input);
    	
    	conditionMap.put("monthlyYearCombo_input", monthlyYearCombo_input);
    	conditionMap.put("monthlyMonthCombo_input", monthlyMonthCombo_input);
    	conditionMap.put("roleId", roleId);

    	/*String loginId           = StringUtil.nullToBlank(condition.get("loginId"));   
		String ipAddr            = StringUtil.nullToBlank(condition.get("ipAddr"));*/
    	
		conditionMap.put("loginId", loginLogLoginId);
    	conditionMap.put("ipAddr", loginLogIpAddr);		

        DateType dateType = null;    
        String searchDateType= tabType; 
        
        /*HOURLY("0"),           *//** 시간별 *//*
        DAILY("1"),            *//** 일별 *//*
        PERIOD("2"),           *//** 기간별 *//*
        WEEKLY("3"),           *//** 주별 */
       
        if ( tabType.equals("day"))
        	searchDateType="1";
        if ( tabType.equals("period"))
        	searchDateType="2";
        if ( tabType.equals("week"))
        	searchDateType="3";
        if ( tabType.equals("month"))
        	searchDateType="4";     
        
        for (DateType obj : DateType.values())
        {
            if (obj.getCode().equals(searchDateType)) {
                dateType = obj;
                break;
            }
        }

        conditionMap.put("period", dateType.toString());
        
        
        
        switch(dateType) 
        {
            case DAILY:

            	result = operatorManager.getLoginLogGrid2(conditionMap);
                
                
                sbFileName.append(hourPrefix);
                break;
            case PERIOD:

            	result = operatorManager.getLoginLogGrid2(conditionMap);
                sbFileName.append(dayPrefix);
                break;
            case WEEKLY:
            	
            	//List<Object> loginLogGrid = 

                result = operatorManager.getLoginLogGrid2(conditionMap);
                sbFileName.append(weekPrefix);
                break;
            case MONTHLY:

            	result = operatorManager.getLoginLogGrid2(conditionMap);
                sbFileName.append(monthPrefix);
                break;

        }

        //가져온 데이터의 총갯수를 계산.
        total = new Integer(result.size()).longValue();
        
        mav.addObject("total", total);
        
        if (total <= 0) 
        {
            return mav;
        }

        sbFileName.append(TimeUtil.getCurrentTimeMilli());
   
     /*   
    	@RequestParam("msg_number") String msg_number,
		@RequestParam("msg_userid") String msg_userid,
		@RequestParam("msg_username") String msg_username,
		@RequestParam("msg_usergroup") String msg_usergroup,
		@RequestParam("msg_ipaddress") String msg_ipaddress,
		@RequestParam("msg_loginhour") String msg_loginhour,
		@RequestParam("msg_logouthour") String msg_logouthour,
		@RequestParam("msg_status") String msg_status*/

        // message 생성(파라매터에서 날라온 값으로 설정해준다)
        msgMap.put("msg_number", msg_number);
    	msgMap.put("msg_userid", msg_userid);
    	msgMap.put("msg_username", msg_username);
    	msgMap.put("msg_usergroup", msg_usergroup);
    	msgMap.put("msg_ipaddress", msg_ipaddress);
    	msgMap.put("msg_loginhour", msg_loginhour);
    	msgMap.put("msg_logouthour", msg_logouthour);
    	msgMap.put("msg_status", msg_status);

        // check download dir
        // check download dir
        // check download dir
        // check download dir
        
    	File downDir = new File(filePath);

        if (downDir.exists()) {
            File[] files = downDir.listFiles();

            if (files != null) {
                String filename = null;
                String deleteDate = null;

                try {
                    deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -10);    // 10일 이전 일자
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                boolean isDel = false;

                for (File file : files) {
                    filename = file.getName();
                    isDel = false;

                    // 파일길이 : 22이상, 확장자 : xls|zip
                    if (filename.length() > 22 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
                        // 10일 지난 파일들 삭제
                        if (filename.startsWith(hourPrefix) && filename.substring(16, 24).compareTo(deleteDate) < 0) {
                            isDel = true;
                        } else if (filename.startsWith(dayPrefix) && filename.substring(15, 23).compareTo(deleteDate) < 0) {
                            isDel = true;
                        } else if (filename.startsWith(dayWeekPrefix) && filename.substring(19, 27).compareTo(deleteDate) < 0) {
                            isDel = true;
                        }else if (filename.startsWith(weekPrefix) && filename.substring(16, 24).compareTo(deleteDate) < 0) {
                            isDel = true;
                        } else if (filename.startsWith(monthPrefix) && filename.substring(17, 25).compareTo(deleteDate) < 0) {
                            isDel = true;
                        }else if (filename.startsWith(seasonPrefix) && filename.substring(18, 26).compareTo(deleteDate) < 0) {
                            isDel = true;
                        } else if (filename.startsWith(yearPrefix) && filename.substring(16, 24).compareTo(deleteDate) < 0) {
                            isDel = true;
                        }

                        if (isDel) {
                            file.delete();
                        }
                    }
                    filename = null;
                }
            }
        } else {
            // directory 가 없으면 생성
           // downDir.mkdir();
        }  

        
        
        //////////// 수정해야되는 부분
        // create excel file
        
        // HSSFWorkbook lib을 사용해서 excel 파일로 만들어 주는 부분.
        OperatorMakeExcel wExcel = new OperatorMakeExcel();
        
        
        
        int cnt = 1;
        int idx = 0;
        int fnum = 0;
        int splCnt = 0;

        if (total <= maxRows) 
        {
            sbSplFileName = new StringBuilder();
            sbSplFileName.append(sbFileName);
            sbSplFileName.append(".xls");
            
            
            
            
        /*    conditionMap.put("dailyStartDate", dailyStartDate);
        	
        	conditionMap.put("periodType_input", periodType_input);
        	conditionMap.put("periodStartDate", periodStartDate);
        	conditionMap.put("periodEndDate", periodEndDate);
        	
        	conditionMap.put("weeklyYearCombo_input", weeklyYearCombo_input);
        	conditionMap.put("weeklyMonthCombo_input", weeklyMonthCombo_input);
        	conditionMap.put("weeklyWeekCombo_input", weeklyWeekCombo_input);
        	
        	
        	conditionMap.put("monthlyYearCombo_input", monthlyYearCombo_input);
        	conditionMap.put("monthlyMonthCombo_input", monthlyMonthCombo_input);*/
            
            String searchTerm = "";
            
            /**
             * excel export  searchTerm setting
             */
            
            switch(dateType) 
            {
                case DAILY:
                	
                	searchTerm = dailyStartDate;
                	
                    break;
                case PERIOD:
                	
                	searchTerm= periodStartDate + "~" + periodEndDate;
                	
                    break;
                case WEEKLY:
                	
                	searchTerm = weeklyYearCombo_input + "/" + weeklyMonthCombo_input + "/" + weeklyWeekCombo_input + "주";

                    break;
                case MONTHLY:

                	searchTerm= monthlyYearCombo_input + "/" + monthlyMonthCombo_input ;
                	break;

            }
            
            
            
            //엑셀 형테로 만들어준다..excel export 형태로 시트를 만들어준다.
            wExcel.writeReportExcel(result, msgMap, isLast, filePath, sbSplFileName.toString(), dateType.toString(), searchTerm);
            
            
            
            
            
            fileNameList.add(sbSplFileName.toString());
        } 
        else 
        {
            for (int i = 0; i < total; i++) 
            {
                if ((splCnt * fnum + cnt) == total || cnt == maxRows)
                {
                    sbSplFileName = new StringBuilder();
                    sbSplFileName.append(sbFileName);
                    sbSplFileName.append('(').append(++fnum).append(").xls");

                    list = result.subList(idx, (i + 1));

                  //  wExcel.writeReportExcel(list, msgMap, isLast, filePath, sbSplFileName.toString());
                    fileNameList.add(sbSplFileName.toString());
                    list = null;
                    splCnt = cnt;
                    cnt = 0;
                    idx = (i + 1);
                }
                cnt++;
            }
        }

        // create zip file
        StringBuilder sbZipFile = new StringBuilder();
        sbZipFile.append(sbFileName).append(".zip");

        ZipUtils zutils = new ZipUtils();
        try
        {
            zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        // return object
        mav.addObject("filePath", filePath);
        mav.addObject("fileName", fileNameList.get(0));
        mav.addObject("zipFileName", sbZipFile.toString());
        mav.addObject("fileNames", fileNameList);
        
        return mav;
    }
    
    

}