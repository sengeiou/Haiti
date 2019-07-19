package com.aimir.bo.system.location;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.model.system.Location;
import com.aimir.service.system.LocationManager;

@Controller
@RequestMapping("/gadget/system/supplier/updateLocation.do")
@SessionAttributes("location")
public class UpdateLocationFrom {

	@SuppressWarnings("unused")
    private final Log log = LogFactory.getLog(AddLocationForm.class);
    
    @Autowired
    LocationManager locationManager;
    
    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(@RequestParam("locationId") int locationId, Model model) {
    	Location location = locationManager.getLocation(locationId);

    	model.addAttribute(location);
        
        return "/gadget/system/supplier/updateLocation";
    }
    
    @RequestMapping(method = RequestMethod.POST)
	public ModelAndView updateLocation(@ModelAttribute Location location, BindingResult result) 
	{
		ModelAndView mav = new ModelAndView("jsonView");

		locationManager.update(location);

		mav.addObject("result", "success");
		return mav;
	}
}
