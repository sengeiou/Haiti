package com.aimir.bo.system.operator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.aimir.service.system.CustomerManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.RoleManager;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/gadget/system/operator/addOperator.do")
@SessionAttributes(types = Operator.class)
public class OperatorAddForm {
	
	private final Log log = LogFactory.getLog(OperatorAddForm.class);
	
	@Autowired
	OperatorManager operatorManager;
	
	@Autowired
	CustomerManager customerManager;
	
	@Autowired
	RoleManager roleManager;
	
	SimpleDateFormat formatter = new SimpleDateFormat(
	    "yyyyMMddHHmmss");
	 
	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(@RequestParam("roleId") int roleId, Model model) {
		
		Operator operator = new Operator();
		
		Role role = roleManager.getRole(roleId);
		
		operator.setRole(role);
		operator.setSupplier(role.getSupplier());
	//	ModelMap model = new ModelMap();
		model.addAttribute("roleId", roleId);
		model.addAttribute("operator",operator);

		return "/gadget/system/operator/addOperator";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(HttpServletRequest request, HttpServletResponse response, @ModelAttribute Operator operator, BindingResult result, SessionStatus status, String customerNo){
		ModelAndView mav = new ModelAndView("jsonView");
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
		
        try{
        	if(operator.getRole().getCustomerRole()) {
                Set<Condition> condition = new HashSet<Condition>();
                condition.add(new Condition("supplier.id", new Object[]{operator.getSupplier().getId()}, null, Restriction.EQ));
                condition.add(new Condition("customerNo", new Object[]{customerNo}, null, Restriction.EQ));
        		Customer customer = customerManager.customerSearchListFindSet(condition).get(0);
        		
        		if(operator.getLocation().getId() != null) {
            		customer.setLocation(operator.getLocation());
            	} else {
            		customer.setLocation(null);
            	}
        		customer.setShowDefaultDashboard(true);
        		customer.setRole(operator.getRole());
        		customer.setLoginDenied(false);
        		customer.setLoginId(operator.getLoginId());
        		customer.setPassword(instance.hashPassword(operator.getPassword(), operator.getLoginId()));
        		customer.setLastPasswordChangeTime(formatter.format(Calendar.getInstance().getTime()));
        		customerManager.update(customer);
        	} else {
        		operator.setShowDefaultDashboard(true);
            	operator.setPassword(instance.hashPassword(operator.getPassword(), operator.getLoginId()));
            	operator.setLastPasswordChangeTime(formatter.format(Calendar.getInstance().getTime()));
            	operator.setRole(operator.getRole());
            	operator.setSupplier(operator.getSupplier());
            	if(operator.getLocation().getId() != null) {
            		operator.setLocation(operator.getLocation());
            	} else {
            		operator.setLocation(null);
            	}
            	//유저 등록시 operator setLoginDenied 값 설정- false로 설정해줘야 로그인 가능
            	operator.setLoginDenied(false);
        		operatorManager.addOperator(operator);
        	}
        	mav.addObject("result", "success");
        }catch(Exception e){
        	log.error(e,e);
        	mav.addObject("result", "fail");
        }

		status.setComplete();
		
		return mav;
	}
}
