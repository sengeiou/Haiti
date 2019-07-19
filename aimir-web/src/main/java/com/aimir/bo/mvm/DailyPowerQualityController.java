package com.aimir.bo.mvm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.dao.mvm.PowerQualityDao;
import com.aimir.service.mvm.PowerQualityManager;

@Controller
public class DailyPowerQualityController {
	
	@Autowired
	PowerQualityDao powerQualityDao;
	
	@Autowired
	PowerQualityManager powerQualityManager;
	
	@RequestMapping("/gadget/mvm/dailyPowerQualityMaxGadget")
	ModelAndView dailyPowerQuality() {
		ModelAndView mav = new ModelAndView("/gadget/mvm/dailyPowerQualityMaxGadget");
		return mav;
	}
	
	@RequestMapping("/gadget/mvm/dailyPowerQualityMiniGadget")
	ModelAndView dailyPowerQualityMini() {
		ModelAndView mav =new ModelAndView("/gadget/mvm/dailyPowerQualityMiniGadget");
		return mav;
	}

	@RequestMapping("/gadget/mvm/dailyPowerQualityChartData")
	ModelAndView dailyPowerQualityChartData(String date, 
			Double sag,
			Double swell) {
		ModelAndView mav= new ModelAndView("jsonView");
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("date", date);
		condition.put("sag", sag);
		condition.put("swell", swell);
		
		List<Map<String, Object>> data = powerQualityDao.getPowerQualityChartData(condition);
		mav.addObject("data", data);
		return mav;
	}
	
	@RequestMapping("/gadget/mvm/dailyPowerQualityData")
	ModelAndView dailyPowerQualityData(String date, 
			Double sag, 
			Double swell,
			Double vol,
			Integer supplierId,
			Integer page,
			Integer limit) {
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("supplierId", supplierId);
		condition.put("date", date);
		condition.put("sag", sag);
		condition.put("swell", swell);
		condition.put("vol", vol);
		condition.put("page", page);
		condition.put("limit", limit);
		
		Map<String, Object> result = powerQualityManager.getDailyPowerQualityData(condition);
		mav.addAllObjects(result);
		return mav;
	}
}
