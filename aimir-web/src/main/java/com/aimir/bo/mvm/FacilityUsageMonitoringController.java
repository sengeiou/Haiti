package com.aimir.bo.mvm;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FacilityUsageMonitoringController {
	@RequestMapping(value="/gadget/bems/facilityUsageMonitoringMiniGadget")
	public ModelAndView facilityUsageMonitoringMiniGadget() {		
		ModelAndView mav = new ModelAndView("/gadget/bems/facilityUsageMonitoringMiniGadget");
		return mav;
	}
	@RequestMapping(value="/gadget/bems/facilityUsageMonitoringMaxGadget")
	public ModelAndView facilityUsageMonitoringMaxGadget() {		
		ModelAndView mav = new ModelAndView("/gadget/bems/facilityUsageMonitoringMaxGadget");
		return mav;
	}
	
	@RequestMapping(value="/gadget/bems/zoneUsageMonitoringMiniGadget")
	public ModelAndView zoneUsageMonitoringMiniGadget() {		
		ModelAndView mav = new ModelAndView("/gadget/bems/zoneUsageMonitoringMiniGadget");
		return mav;
	}
	@RequestMapping(value="/gadget/bems/zoneUsageMonitoringMaxGadget")
	public ModelAndView zoneUsageMonitoringMaxGadget() {		
		ModelAndView mav = new ModelAndView("/gadget/bems/zoneUsageMonitoringMaxGadget");
		return mav;
	}
}
