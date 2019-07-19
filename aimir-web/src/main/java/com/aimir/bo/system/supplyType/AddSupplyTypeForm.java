package com.aimir.bo.system.supplyType;

import java.util.List;

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
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.SupplyType;
import com.aimir.service.system.Co2FormulaManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.SupplyTypeManager;

@Controller
@RequestMapping("/gadget/system/supplier/addSupplyType.do")
@SessionAttributes("supplyType")
public class AddSupplyTypeForm {

    @SuppressWarnings("unused")
    private final Log log = LogFactory.getLog(AddSupplyTypeForm.class);

    @Autowired
    SupplyTypeManager supplyTypeManager;
    
    @Autowired
    SupplierManager supplierManager;
    
    @Autowired
    CodeManager codeManager;
    
    @Autowired
    Co2FormulaManager co2FormulaManager;

    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(@RequestParam("supplierId") int supplierId, Model model) {
        Supplier supplier = supplierManager.getSupplier(supplierId);
        
        String parentCode = codeManager.getCodeByName("Energy").getCode();
        List<Code> codeList = codeManager.getChildCodes(parentCode);
        model.addAttribute(codeList);

        SupplyType supplyType = new SupplyType();
        supplyType.setTypeCode(new Code());
        supplyType.setSupplier(supplier);
        
        model.addAttribute(supplyType);
        
        return "/gadget/system/supplier/addSupplyType";
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView addSupplierType(@ModelAttribute("supplyType") SupplyType supplyType, BindingResult result) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	Co2Formula co2Formula = co2FormulaManager.getCo2FormulaBySupplyType(supplyType.getTypeCode().getId());
    	if(co2Formula == null){
    		// CO2 계산식이 없으면 에러
    		mav.addObject("supplyType", "");    		
    		mav.addObject("result", "fmerror");
    		return mav;
    	}
    	    	
    	if (supplyTypeManager.checkSupplyType(supplyType.getSupplier().getId(), 
    											codeManager.getCode(supplyType.getTypeCode().getId()).getId())) {
    		// 로직 순서 변경
    		String co2Name = supplyType.getCo2Formula().getName();
        	if(co2Name != null && !"".equals(co2Name)){
        		co2Formula.setName(co2Name);
        	}
        	supplyType.setCo2Formula(co2Formula);
    		
    		supplyType.setTypeCode(codeManager.getCode(supplyType.getTypeCode().getId()));
        	supplyTypeManager.add(supplyType);
        	
        	mav.addObject("result", "success");
    	} else {
    		// SupplyType이 반환 타입으로 명시되어야 함.
    		mav.addObject("supplyType", "");
    		mav.addObject("result", "fail");
    	}
    	return mav;
    }
}