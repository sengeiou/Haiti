package com.aimir.bo.system.supplyType;

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

import com.aimir.model.system.Co2Formula;
import com.aimir.model.system.SupplyType;
import com.aimir.service.system.Co2FormulaManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.SupplyTypeManager;

@Controller
@RequestMapping("/gadget/system/supplier/updateSupplyType.do")
@SessionAttributes("supplyType")
public class UpdateSupplyTypeForm {

    @SuppressWarnings("unused")
    private final Log log = LogFactory.getLog(UpdateSupplyTypeForm.class);

    @Autowired
    SupplyTypeManager supplyTypeManager;
    
    @Autowired
    SupplierManager supplierManager;
    
    @Autowired
    CodeManager codeManager;
    
    @Autowired
    Co2FormulaManager co2FormulaManager;

    @RequestMapping(method = RequestMethod.GET)
    public String getSupplyType(@RequestParam("supplyTypeId") int supplyTypeId, Model model) {
    	SupplyType supplyType = supplyTypeManager.getSupplyType(supplyTypeId);
        model.addAttribute(supplyType);
        
        return "/gadget/system/supplier/updateSupplyType";
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView updateSupplierType(@ModelAttribute("supplyType") SupplyType supplyType, BindingResult result) {
    	ModelAndView mav = new ModelAndView("jsonView");
		
    	mav.addObject("result", "fail");

    	supplyType.setTypeCode(codeManager.getCode(supplyType.getTypeCode().getId()));
    	Co2Formula co2Formula = supplyType.getCo2Formula();
    	co2Formula.setCo2factor((co2Formula.getCo2emissions()/100));
    	co2Formula.setName(co2Formula.getName());
    	co2FormulaManager.update(co2Formula);
    	
    	supplyType.setCo2Formula(co2Formula);
		supplyTypeManager.update(supplyType);
		
			
		mav.addObject("result", "success");

		return mav;
    }
}