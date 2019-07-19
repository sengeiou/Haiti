package com.aimir.bo.system.supplier;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import com.aimir.model.system.Country;
import com.aimir.model.system.DecimalPattern;
import com.aimir.model.system.Language;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TimeZone;
import com.aimir.service.device.MeterManager;
import com.aimir.service.system.CountryManager;
import com.aimir.service.system.LanguageManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.TimeZoneManager;
import com.aimir.web.SupplierLocale;

@Controller
@RequestMapping("/gadget/system/supplier/updateSupplier.do")
@SessionAttributes("supplier")
public class UpdateSupplierForm {

    private final Log log = LogFactory.getLog(UpdateSupplierForm.class);

    @Autowired
    SupplierManager supplierManager;
    
    @Autowired
    CountryManager countryManager;
    
    @Autowired
    LanguageManager languageManager;
    
    @Autowired
    TimeZoneManager timezoneManager;
    
    @Autowired
	MeterManager meterManager;
    
    @RequestMapping(method = RequestMethod.GET)
    public String getSupplier(@RequestParam("supplierId") int supplierId, Model model) {

    	Supplier supplier = supplierManager.getSupplier(supplierId);

    	List<Country> countryList = countryManager.getCountries();
        List<Language> languageList = languageManager.getLanguaes();
        List<TimeZone> timezoneList = timezoneManager.getTimeZones();
        
        if (supplier.getMd() == null) {
        	supplier.setMd(new DecimalPattern());
        }
        
        if (supplier.getCd() == null) {
        	supplier.setCd(new DecimalPattern());
        }
        
        // Licence Count Check (S) 
 		if (supplier.getLicenceUse() == 1) {
 			model.addAttribute("licenceUse", supplier.getLicenceUse());
 			model.addAttribute("totalLicenceCount", supplier.getLicenceMeterCount());
 			model.addAttribute("registeredLicenceCount", meterManager.getTotalMeterCount());
 			model.addAttribute("availableLicenceCount", supplier.getLicenceMeterCount() - meterManager.getTotalMeterCount());
 		} else {
 			model.addAttribute("licenceUse", supplier.getLicenceUse());
 			model.addAttribute("totalLicenceCount", " ");
 			model.addAttribute("registeredLicenceCount", " ");
 			model.addAttribute("availableLicenceCount", " ");
 		}
 		// Licence Count Check (E)
        
        model.addAttribute(supplier);
        model.addAttribute(countryList);
        model.addAttribute(languageList);
        model.addAttribute(timezoneList);
        
        return "/gadget/system/supplier/updateSupplier";
        
    }
     
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView updateSupplier(@ModelAttribute Supplier supplier, BindingResult result, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("jsonView"); 
		
		try {
			supplier.setCountry(countryManager.get(supplier.getCountry().getId()));
			supplier.setLang(languageManager.get(supplier.getLang().getId()));
			supplier.setTimezone(timezoneManager.get(supplier.getTimezone().getId()));
			supplier.setSysDatePattern(supplier.getSysDatePattern());
			supplier.setName(supplier.getName().replace("'", "\\'"));
	        supplier.setDescr(supplier.getDescr().replace("'", "\\'"));
	        supplier.setAddress(supplier.getAddress().replace("'", "\\'"));
			
			if(supplier.getLicenceUse() == 1) {
				supplier.setLicenceUse(1);
			} else {
				supplier.setLicenceUse(0);
			}
			
			supplierManager.update(supplier);
			
			log.debug("UpdateSupplierForm START ================================================");
			log.debug("Lang[" + supplier.getLang().getCode_2letter()+"], " + "Country["+ supplier.getCountry().getCode_2letter()+"]");
			
			SupplierLocale.setSessionLocale(
					request, 
					supplier.getLang().getCode_2letter(), 
					supplier.getCountry().getCode_2letter());
			
		}catch(Exception e){
			log.error("==========Supplier Update failed==============",e);
		}
		
		// 업데이트 성공 시 Session 의 Locale Resolver 의 정보도 변경
		/*WebUtils.setSessionAttribute(
				request, 
				"org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE", 
				new Locale(supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
		System.out.println("UpdateSupplierForm END   ================================================");
		*/
		
		mav.addObject("result", "success");
		return mav;
	}
}
