package com.aimir.bo.system.supplier;

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
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.model.system.Country;
import com.aimir.model.system.DecimalPattern;
import com.aimir.model.system.Language;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TimeZone;
import com.aimir.service.system.CountryManager;
import com.aimir.service.system.LanguageManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.TimeZoneManager;

@Controller
@RequestMapping("/gadget/system/supplier/addSupplier.do")
@SessionAttributes(value = {"supplier", "countryList"})
public class AddSupplierForm {
    
    @SuppressWarnings("unused")
    private final Log log = LogFactory.getLog(AddSupplierForm.class);

    @Autowired
    SupplierManager supplierManager;
    
    @Autowired
    CountryManager countryManager;
    
    @Autowired
    LanguageManager languageManager;
    
    @Autowired
    TimeZoneManager timezoneManager;
    
    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(Model model) {
        Supplier supplier = new Supplier();
        supplier.setCountry(new Country());
        supplier.setLang(new Language());
        supplier.setTimezone(new TimeZone());
        supplier.setMd(new DecimalPattern());
        supplier.setCd(new DecimalPattern());
        
        List<Country> countryList = countryManager.getCountries();
        List<Language> languageList = languageManager.getLanguaes();
        List<TimeZone> timeZoneList = timezoneManager.getTimeZones();
        
        model.addAttribute(supplier);
        model.addAttribute(countryList);
        model.addAttribute(languageList);
        model.addAttribute(timeZoneList);
        
        return "/gadget/system/supplier/addSupplier";
    }

//    private void onBindAndValidate(Supplier supplier, Errors errors) {
//		if(!StringUtils.hasLength(supplier.getName())) {
//			errors.rejectValue("name", "required", "required");
//		} else {
//			if(supplierManager.getSupplierByName(supplier.getName()) != null) {
//				errors.rejectValue("name", "duplicated", "duplicated");
//			}
//		}
//	}
    
    @RequestMapping(method = RequestMethod.POST)
	public ModelAndView addSupplier(@ModelAttribute Supplier supplier, BindingResult result) 
	{
		ModelAndView mav = new ModelAndView("jsonView");
		
//		onBindAndValidate(supplier, result);
//		if (result.hasErrors()) {
//			mav.addObject("result", "failure");
//			mav.addObject("errors", result);
//			return mav;
//		}
		if (supplier.getName() == "" || supplier.getName() == null) {
			mav.addObject("result", "fail");
			return mav;
		}
			
		supplier.setCountry(countryManager.get(supplier.getCountry().getId()));
		supplier.setLang(languageManager.get(supplier.getLang().getId()));
		supplier.setTimezone(timezoneManager.get(supplier.getTimezone().getId()));
		mav.addObject("result", "fail");
		supplierManager.add(supplier);
		mav.addObject("result", "success");
		return mav;
	}
}