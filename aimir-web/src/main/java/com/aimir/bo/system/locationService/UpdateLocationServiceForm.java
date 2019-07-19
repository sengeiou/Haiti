package com.aimir.bo.system.locationService;

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

import com.aimir.model.system.ContractCapacity;
import com.aimir.model.system.SupplyCapacityLog;
import com.aimir.model.system.SupplyTypeLocation;
import com.aimir.model.system.SupplyType;
import com.aimir.model.system.TariffType;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ContractCapacityManager;
import com.aimir.service.system.LocationManager;
import com.aimir.service.system.SupplyCapacityLogManager;
import com.aimir.service.system.SupplyTypeLocationManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.SupplyTypeManager;
import com.aimir.util.DateTimeUtil;

@Controller
@RequestMapping("/gadget/system/supplier/updateLocationService.do")
@SessionAttributes("supplyTypeLocation")
public class UpdateLocationServiceForm {
    @SuppressWarnings("unused")
    private final Log log = LogFactory.getLog(UpdateLocationServiceForm.class);

    @Autowired
    SupplyTypeLocationManager locationServiceManager;
    
    @Autowired
    LocationManager locationManager;
    
    @Autowired
    SupplierManager supplierManager;
    
    @Autowired
    SupplyTypeManager supplyTypeManager;
    
    @Autowired
    CodeManager codeManager;

    @Autowired
	ContractCapacityManager contractCapacityManager;
    
	@Autowired
	SupplyCapacityLogManager supplyCapacityLogManager;
	
    @RequestMapping(method = RequestMethod.GET)
    public String getLocationService(@RequestParam("locationServiceId") int locationServiceId,
    								 Model model) {
    	    	
    	SupplyTypeLocation locationService = locationServiceManager.get(locationServiceId);
    
    	model.addAttribute(locationService);
   
        return "/gadget/system/supplier/updateLocationService";
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView updateSupplyType(@ModelAttribute("supplyTypeLocation") SupplyTypeLocation locationService, 
    										 BindingResult result) {
  	
    	
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	SupplyCapacityLog supplyCapacityLog = new SupplyCapacityLog();
    	mav.addObject("result", "fail");
    
    	SupplyType supplyType = supplyTypeManager.getSupplyType(locationService.getSupplyType().getId());
      	ContractCapacity contractCapacity = locationService.getContractCapacity();
      	
      	contractCapacityManager.update(contractCapacity);
		supplyTypeManager.update(supplyType);
		
		locationService.setSupplyType(supplyType);
		locationService.setContractCapacity(contractCapacity);
    	locationServiceManager.update(locationService);
		      	
    	supplyCapacityLog.setContractCapacity(contractCapacity.getCapacity().toString());
		supplyCapacityLog.setContractNumber(contractCapacity.getContractNumber().toString());
		supplyCapacityLog.setSupplier(supplyType.getSupplier());
		supplyCapacityLog.setSupplyType(contractCapacity.getContractTypeCode().getName());
		supplyCapacityLog.setSupplyTypeLocation(locationService.getLocation().getName());
		supplyCapacityLog.setWriteDatetime(DateTimeUtil
				.getCurrentDateTimeByFormat(""));
		
		
		supplyCapacityLogManager.add(supplyCapacityLog);

    	mav.addObject("result", "success");

    	return mav;
    }
}
