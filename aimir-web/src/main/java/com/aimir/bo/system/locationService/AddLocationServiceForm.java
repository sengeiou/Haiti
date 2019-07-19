package com.aimir.bo.system.locationService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

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
import com.aimir.model.system.Supplier;
import com.aimir.model.system.SupplyCapacityLog;
import com.aimir.model.system.SupplyType;
import com.aimir.model.system.SupplyTypeLocation;
import com.aimir.model.system.TariffType;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ContractCapacityManager;
import com.aimir.service.system.LocationManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.SupplyCapacityLogManager;
import com.aimir.service.system.SupplyTypeLocationManager;
import com.aimir.service.system.SupplyTypeManager;
import com.aimir.service.system.TariffTypeManager;
import com.aimir.util.DateTimeUtil;

@Controller
@RequestMapping("/gadget/system/supplier/addLocationService.do")
@SessionAttributes("supplyTypeLocation")
public class AddLocationServiceForm {

    @SuppressWarnings("unused")
    private final Log log = LogFactory.getLog(AddLocationServiceForm.class);

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
	TariffTypeManager tariffTypeManager;

   	@Autowired
   	SupplyCapacityLogManager supplyCapacityLogManager;
   	
   	@Autowired
   	TariffTypeManager tarifftypeManager;
   	
    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(@RequestParam("supplierId") int supplierId,
            @RequestParam("locationId") int locationId, Model model) {
        
        SupplyTypeLocation locationService = new SupplyTypeLocation();
        locationService.setSupplyType(new SupplyType());
        locationService.setLocation(locationManager.getLocation(locationId));

        List<SupplyType> supplyTypeList = supplyTypeManager.getSupplyTypeBySupplierId(supplierId);
        
        model.addAttribute(locationService);
    	Collections.sort(supplyTypeList, new Comparator<SupplyType>(){
    		
 			@Override
 			public int compare(SupplyType o1, SupplyType o2) {
 				 String firstValue =  (String) codeManager.getCode(o1.getTypeCodeId()).getName();
 				 String secondValue = (String) codeManager.getCode(o2.getTypeCodeId()).getName();
 			    return firstValue.compareToIgnoreCase(secondValue);
 			}
    	});	
    	
        model.addAttribute(supplyTypeList);

        return "/gadget/system/supplier/addLocationService";
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView addLocationService(@ModelAttribute("supplyTypeLocation") 
            SupplyTypeLocation locationService, BindingResult result) {
        
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	mav.addObject("result", "fail");
    	
    	Random oRandom = new Random();
    	int supplierid = 0;
    	int typecodeid = 0;
    	int tarriffid  = 0;
    	SupplyType supplyType = supplyTypeManager.getSupplyType(locationService.getSupplyType().getId());//공급사
 
    	supplierid = supplyType.getSupplierId();
    	typecodeid = supplyType.getTypeCodeId();

    	List<TariffType> tariffList = tariffTypeManager.getTariffTypeList(supplierid, typecodeid);
    	//Water와 Heat의 tariffType이 존재하지 않아 tariffType의 id=1로 디폴트 설정
    	//추후 Water와 Heat의 tariffType의 생성될경우 else부분 삭제할 예정.
    	if (tariffList != null && ! tariffList.isEmpty() && tariffList.get(0) != null) {
    		tarriffid = tariffList.get(0).getId();
    	}else{
    		tarriffid = 1;
    	}

    	TariffType tf = tariffTypeManager.getTariffType(tarriffid);//에너지 계약 종 
    	
    	ContractCapacity contractCapa = new ContractCapacity();
    	SupplyCapacityLog supplyCapacityLog = new SupplyCapacityLog();
    	
    	int j = oRandom.nextInt(2147483600)+1;//에너지계약번호 랜덤한
    	contractCapa.setCapacity(locationService.getContractCapacity().getCapacity());
    	
    	contractCapa.setContractNumber(Integer.toString(j));
    	contractCapa.setContractDate(DateTimeUtil
				.getCurrentDateTimeByFormat("").substring(0,8));  
  
		contractCapa.setContractTypeCode(tf);
    	contractCapacityManager.add(contractCapa);
    	locationService.setContractCapacity(contractCapa);
    	
    	int type_id = codeManager.getCode(supplyType.getTypeCode().getId()).getId();
    	log.debug("type_id :" + type_id);
    	if (!"".equals(Integer.toString(type_id)) || Integer.toString(type_id) != null) {
    		locationService.setSupplyType(supplyType);	
	    	locationServiceManager.add(locationService);
    	}
    	
    	supplyCapacityLog.setContractCapacity(contractCapa.getCapacity().toString());
		supplyCapacityLog.setContractNumber(contractCapa.getContractNumber().toString());
		supplyCapacityLog.setSupplier(supplyType.getSupplier());
		supplyCapacityLog.setSupplyType(contractCapa.getContractTypeCode().getName());
		supplyCapacityLog.setSupplyTypeLocation(locationService.getLocation().getName());
		supplyCapacityLog.setWriteDatetime(DateTimeUtil
				.getCurrentDateTimeByFormat(""));
		supplyCapacityLogManager.add(supplyCapacityLog);
		
		mav.addObject("result", "success");
    	return mav;
    }
}