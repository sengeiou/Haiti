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
import com.aimir.service.system.SupplierManager;

@Controller
@RequestMapping("/gadget/system/supplier/addLocation.do")
@SessionAttributes("location")
public class AddLocationForm {
    @SuppressWarnings("unused")
    private final Log log = LogFactory.getLog(AddLocationForm.class);
    
    @Autowired
    LocationManager locationManager;
    
    @Autowired
    SupplierManager supplierManager;
    
    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(@RequestParam("supplierId") int supplierId, 
    		@RequestParam("parentId") int parentId, Model model) {
    	
        Location location = new Location();
        
        Location parent = new Location();
        // 기본값 : parentId = -1
        if (parentId > 0) {
        	parent = locationManager.getLocation(parentId);
        }
        
        location.setParent(parent);
        location.setSupplier(supplierManager.getSupplier(supplierId));
        
        model.addAttribute(location);
        
        return "/gadget/system/supplier/addLocation";
    }
    
    @RequestMapping(method = RequestMethod.POST)
	public ModelAndView addLocation(@ModelAttribute Location location, BindingResult result) 
	{
		ModelAndView mav = new ModelAndView("jsonView");
		
		if (location.getParent().getId() != null) {
			int parentId = location.getParent().getId();
			location.setParent(locationManager.getLocation(parentId));
		} else {
			location.setParent(null);
		}
		
		locationManager.add(location);

		mav.addObject("result", "success");
		return mav;
	}
}
