package com.aimir.bo.system.operator;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncryptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.model.system.Customer;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.User;
import com.aimir.service.system.CustomerManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;

@Controller
@RequestMapping("/gadget/system/operator/updatePersonalOperator.do")
public class OperatorPersonalEditForm {

	private final Log log = LogFactory.getLog(OperatorEditForm.class);

	@Autowired
	OperatorManager operatorManager;
	@Autowired
    CustomerManager customerManager;
	@Autowired
	RoleManager roleManager;
	@Autowired
	SupplierManager supplierManager;
	
	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelMap setupForm(@RequestParam("operatorId") int operatorId, @RequestParam("loginId") String loginId) {
	    User loginUser = operatorManager.getUser(operatorId, loginId);
		
		ModelMap model = new ModelMap();
		model.addAttribute("roleId", loginUser.getRole().getId());
		model.addAttribute("operatorId", loginUser.getId());
		model.addAttribute("operator",loginUser);
		model.addAttribute("loginId",loginId);

		return model;
		
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(HttpServletRequest request, HttpServletResponse response, @ModelAttribute Operator operator, BindingResult result, SessionStatus status) {
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
		
     //   String oldPassword = operatorManager.getOperator(operator.getId()).getPassword();
		
        try{
        	String newPassword = instance.hashPassword(operator.getPassword(), operator.getLoginId());
        	operator.setPassword(newPassword);
        	operator.setIsFirstLogin(false);
        	
        	// 	if(oldPassword != newPassword){
       // 		operator.setPassword(newPassword);
       // 		operator.setLastPasswordChangeTime(formatter.format(Calendar.getInstance().getTime()));
       // 	}
        	
      /*  	if(checkPasswordChange(operator.getId(), newPassword)){
        		operator.setPassword(newPassword);
        	    operator.setLastPasswordChangeTime(formatter.format(Calendar.getInstance().getTime()));
     	}
          */ 		
        }catch(EncryptionException e){
        	log.error(e,e);
        }
        
        int userId = operator.getId();
        ModelAndView mav = new ModelAndView("jsonView");
        
        try {
	        Role role = roleManager.getRole(operator.getRoleId());
	        
	        if(role.getSystemAuthority().equals("w")) {
		        if(role.getCustomerRole() == true) {
		            Customer customer = customerManager.getCustomer(userId);
		            customer.setName(operator.getName());
		            customer.setAliasName(operator.getAliasName());
		            customer.setPassword(operator.getPassword());
		            customer.setEmail(operator.getEmail());
		            customer.setTelNo(operator.getTelNo());
		            customer.setShowDefaultDashboard(operator.getShowDefaultDashboard());
		            customer.setIsFirstLogin(false);
		            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		            customer.setLastPasswordChangeTime(formatter.format(Calendar.getInstance().getTime()));
		            customerManager.update(customer);
		            mav.addObject("result", "success");
		        } else {
		            Operator preOperator = operatorManager.getOperator(userId);
		            preOperator.setName(operator.getName());
		            preOperator.setAliasName(operator.getAliasName());
		            preOperator.setPassword(operator.getPassword());
		            preOperator.setEmail(operator.getEmail());
		            preOperator.setTelNo(operator.getTelNo());
		            preOperator.setShowDefaultDashboard(operator.getShowDefaultDashboard());
		            preOperator.setIsFirstLogin(operator.getIsFirstLogin());
		            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		            preOperator.setLastPasswordChangeTime(formatter.format(Calendar.getInstance().getTime()));
		            operatorManager.updateOperator(preOperator);
		            mav.addObject("result", "success");
		        }
	        }else {
	        	mav.addObject("result", "nopermission");
	        }
		
		status.setComplete();
		
        }catch (Exception e) {
        	mav.addObject("result", "fail");
        	log.debug(e,e);
		}
		
		mav.addObject("operatorId", userId);
		mav.addObject("loginId", operator.getLoginId());
		return mav;
	}
	
}