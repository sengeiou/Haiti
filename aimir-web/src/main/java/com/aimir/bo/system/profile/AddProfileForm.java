package com.aimir.bo.system.profile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.EventAlert;
import com.aimir.model.system.Location;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Profile;
import com.aimir.service.device.EventAlertManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.LocationManager;
import com.aimir.service.system.ProfileManager;

@Controller
public class AddProfileForm {

    private final Log log = LogFactory.getLog(AddProfileForm.class);
	
	@Autowired
    ProfileManager profileManager;
	
	@Autowired
    EventAlertManager eventAlertManager;
	
	@Autowired
    CodeManager codeManager;
	
	@Autowired
    LocationManager locationManager;
	
	@RequestMapping(method = RequestMethod.GET)
    public String setupForm(HttpServletRequest request, HttpServletResponse response, Model model) {
		
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser user = (AimirUser)instance.getUserFromSession();
		
        Profile profile = new Profile();
        try {
			profile.setOperator((Operator)user.getOperator(new Operator()));
		} catch (Exception e) {
			e.printStackTrace();
		}
        profile.setEventAlert(new EventAlert());
        //profile.setActivatorType(new TargetClass());
        profile.setLocation(new Location());
        
        model.addAttribute(profile);
        
        return "/gadget/system/supplier/addSupplyType";
    }

	@RequestMapping(value="/gadget/system/profile/addProfile.do", method = RequestMethod.POST)
    public ModelAndView saveProfile(HttpServletRequest request, HttpServletResponse response, @ModelAttribute("profile") Profile profile, BindingResult result) {
		log.debug("AddProfileForm=====" + profile);
		ModelAndView mav = new ModelAndView("jsonView");
    	
    	Profile profileData = new Profile();
    	
    	ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser user = (AimirUser)instance.getUserFromSession();
		
        try {
        	profileData.setOperator((Operator)user.getOperator(new Operator()));
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (profile.getEventAlertType() != null) {
			profileData.setEventAlertType(profile.getEventAlertType());
		}
		
		if (profile.getSeverity() != null) {
			profileData.setSeverity(profile.getSeverity());
		}
		
		if (profile.getEventAlert().getId() != null) {
			profileData.setEventAlert(eventAlertManager.getEventAlert(profile.getEventAlert().getId()));
		}
		
		if (profile.getStatus() != null) {
			profileData.setStatus(profile.getStatus());
		}
		
		if (profile.getActivatorType() != null) {
			profileData.setActivatorType(profile.getActivatorType().name());
		}
		
		if (profile.getActivatorId() != null) {
			profileData.setActivatorId(profile.getActivatorId());
		}
		/*
		if (profile.getLocation() != null) {
			profileData.setLocation(locationManager.getLocation(profile.getLocation().getId()));
		}
        */
		if (profile.getSound() != null) {
			profileData.setSound(profile.getSound());
		} else {
			profileData.setSound(true);
		}
		if (profile.getPopup() != null) {
			profileData.setPopup(profile.getPopup());
		} else {
			profileData.setPopup(true);
		}
		if (profile.getPopupCnt() != null) {
			profileData.setPopupCnt(profile.getPopupCnt());
		} else {
			profileData.setPopupCnt(100);
		}
		
        mav.addObject("result", "fail");
        
        profileManager.addProfile(profileData);
        	
        mav.addObject("result", "success");

        return mav;
    }
}
