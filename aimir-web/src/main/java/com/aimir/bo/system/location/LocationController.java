package com.aimir.bo.system.location;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.service.system.LocationManager;

@Controller
public class LocationController {

	@Autowired
    LocationManager locationManager;
	
	@RequestMapping(value="/gadget/system/location/locationSample.*")
    public String getMemoMax() {
        return "/gadget/system/location/locationSample";
    }
	
	@RequestMapping(value="/gadget/system/location/getLocations.do")
    public ModelAndView getLocations(HttpServletRequest request, HttpServletResponse response) {
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        
        ModelAndView mav = new ModelAndView("jsonView");
        
        if(user!=null && !user.isAnonymous()) {
        	int supplierId = user.getRoleData().getSupplier().getId();
        	mav.addObject("locations", locationManager.getParentsBySupplierId(supplierId));
        } else {
        	mav.addObject("locations", locationManager.getParents());
        }
        return mav;
    }

	@RequestMapping(value="/gadget/system/location/getUserLocation.do")
	public ModelAndView getUserLocations(HttpServletRequest request, HttpServletResponse response,
	        @RequestParam(value = "locationId", required = false) Integer locationId) {
	    ESAPI.httpUtilities().setCurrentHTTP(request, response);

	    // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
	    AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();

	    AimirUser user = (AimirUser)instance.getUserFromSession();

	    ModelAndView mav = new ModelAndView("jsonView");

	    if (user != null && !user.isAnonymous()) {
	        int supplierId = user.getRoleData().getSupplier().getId();
	        mav.addObject("locations", locationManager.getUserLocationBySupplierId(locationId, supplierId));
	    } else {
	        mav.addObject("locations", locationManager.getUserLocation(locationId));
	    }
	    return mav;
	}

	@RequestMapping(value="/gadget/mvm/getAllLocations.do")
    public ModelAndView getAllLocations(HttpServletRequest request, HttpServletResponse response) {
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        
		ModelAndView mav = new ModelAndView("jsonView");
		
		if(user!=null && !user.isAnonymous()) {
        	int supplierId = user.getRoleData().getSupplier().getId();
        	mav.addObject("locations", locationManager.getLocationsBySupplierId(supplierId));
		}else{
			mav.addObject("locations", locationManager.getLocations());
		}
    	return mav;
    }
	
	@RequestMapping(value="/gadget/system/location/searchLocations.do")
    public ModelAndView searchLocations(HttpServletRequest request, HttpServletResponse response,@RequestParam("keyWord") String keyWord) throws UnsupportedEncodingException {
		ESAPI.httpUtilities().setCurrentHTTP(request, response);
		  
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        
        ModelAndView mav = new ModelAndView("jsonView");
        
        keyWord = URLDecoder.decode(keyWord, "UTF-8");	//encode되어있는 keyWord를 "UTF-8"로 decode한다.

        if(user!=null && !user.isAnonymous()) {
        	int supplierId = user.getRoleData().getSupplier().getId();
        	mav.addObject("locations", locationManager.getParentsBykeyWord(supplierId,keyWord));
        	mav.addObject("keyWord",keyWord);	//decode된 keyWord를 반환.
        } else {
        	mav.addObject("locations", locationManager.getParents());
        	mav.addObject("keyWord",keyWord);
        }
        return mav;
    }
	
	@RequestMapping(value="/gadget/system/location/getLocationsName.do")
    public ModelAndView getLocationsName() {
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("locations", locationManager.getLocationsName());
        return mav;
    }
}