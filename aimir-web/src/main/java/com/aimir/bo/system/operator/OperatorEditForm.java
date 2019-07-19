package com.aimir.bo.system.operator;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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
import com.aimir.service.system.CustomerManager;
import com.aimir.service.system.LocationManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.DateTimeUtil;

@Controller
@RequestMapping("/gadget/system/operator/updateOperator.do")
@SessionAttributes(types = Operator.class)
public class OperatorEditForm {

	private final Log log = LogFactory.getLog(OperatorEditForm.class);

	@Autowired
	OperatorManager operatorManager;

	@Autowired
	CustomerManager customerManager;

	@Autowired
	RoleManager roleManager;

	@Autowired
	SupplierManager supplierManager;

	@Autowired
	LocationManager locationManager;
	
	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelMap setupForm(@RequestParam("operatorId") int operatorId, @RequestParam("customerRole") Boolean customerRole) {
		Operator operator = new Operator();
		String customerNo = "";
		if(customerRole) {
			Customer customer = customerManager.getCustomer(operatorId);
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
		
		ModelMap model = new ModelMap();
		model.addAttribute("roleId", operator.getRole().getId());
		model.addAttribute("operatorId", operator.getId());
		model.addAttribute("customerNo", customerNo);
		model.addAttribute("operator",operator);

		return model;
		
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public ModelAndView processSubmit(String customerNo, HttpServletRequest request, HttpServletResponse response, @ModelAttribute Operator operator, 
			BindingResult result, SessionStatus status) throws ParseException, Exception {
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        Integer operatorId = null;
        try{
        	if(operator.getRole().getCustomerRole()) {
        		Customer customer = customerManager.getCustomer(operator.getId());
        		
        		customer.setAliasName(operator.getAliasName());
        		customer.setName(operator.getName());
        		customer.setEmail(operator.getEmail());
        		customer.setTelNo(operator.getTelNo());
        		customer.setDeniedReason(operator.getDeniedReason());
        		customer.setLoginDenied(operator.getLoginDenied());
        		customer.setUseLocation(operator.getUseLocation());
        		
        		if(operator.getPassword().length() != 0 || !operator.getPassword().isEmpty()) {
            		String newPassword = instance.hashPassword(operator.getPassword(), operator.getLoginId());
            		customer.setPassword(newPassword);
            		customer.setLastPasswordChangeTime(DateTimeUtil.getCurrentDateTimeByFormat(""));// update time
            	}
            	if(operator.getLocationId() != null){
            		customer.setLocation(locationManager.getLocation(operator.getLocationId()));
            	} else {
            		customer.setLocation(null);
            	}	
        		customerManager.update(customer);
        		
        	} else {
        		if(operator.getPassword().length() != 0 || !operator.getPassword().isEmpty()) {
            		String newPassword = instance.hashPassword(operator.getPassword(), operator.getLoginId());
            		operator.setPassword(newPassword);
            		operator.setLastPasswordChangeTime(DateTimeUtil.getCurrentDateTimeByFormat(""));// update time
            	}
            	if(operator.getLocationId() != null){
            		operator.setLocation(locationManager.getLocation(operator.getLocationId()));
            	} else {
            		operator.setLocation(null);
            	}	
            	
            	// role
            	operator.setRole(roleManager.getRole(operator.getRoleId())); 
            	
                operatorId = operator.getId();
                operatorManager.updateOperatorInfo(operator);
        	}
        }catch(EncryptionException e){
        	log.error(e,e);
        }

		status.setComplete();
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("result", "success");
		mav.addObject("operatorId", operatorId);
		return mav;
	}
	
}