/**
 * SupplierController.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.bo.system.supplier;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.SeasonDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.TOURateDao;
import com.aimir.dao.system.TariffEMDao;
import com.aimir.dao.system.TariffWMDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.fep.trap.data.IHD_RequestDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.WaterMeter;
import com.aimir.model.mvm.Season;
import com.aimir.model.system.Co2Formula;
import com.aimir.model.system.Code;
import com.aimir.model.system.GroupMember;
import com.aimir.model.system.Location;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.SupplyType;
import com.aimir.model.system.SupplyTypeLocation;
import com.aimir.model.system.TOURate;
import com.aimir.model.system.TariffEM;
import com.aimir.model.system.TariffType;
import com.aimir.model.system.TariffWM;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.device.MCUManager;
import com.aimir.service.device.MeterManager;
import com.aimir.service.device.ModemManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ContractCapacityManager;
import com.aimir.service.system.CountryManager;
import com.aimir.service.system.GroupMgmtManager;
import com.aimir.service.system.LocationManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.SupplierMgmtManager;
import com.aimir.service.system.SupplyTypeLocationManager;
import com.aimir.service.system.SupplyTypeManager;
import com.aimir.service.system.TariffTypeManager;
import com.aimir.support.FileUploadHelper;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommonUtils;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.SupplierEMDataMakeExcel;
import com.aimir.util.SupplierGMDataMakeExcel;
import com.aimir.util.SupplierMaxLocationMakeExcel;
import com.aimir.util.SupplierWMDataMakeExcel;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

/**
 * SupplierController.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 4. 11.   v1.0                
 *
 */
@Controller
public class SupplierController {
    
    private static Log log = LogFactory.getLog(SupplierController.class);

    @Autowired
    SupplierManager supplierManager;

    @Autowired
    SupplyTypeManager supplyTypeManager;
    
    @Autowired
    LocationManager locationManager;
    
    @Autowired
    SupplyTypeLocationManager locationServiceManager;
    
    @Autowired
    CountryManager countryManager;

    @Autowired
    SupplierMgmtManager supplierMgmtManager;
    
	@Autowired
	ContractCapacityManager contractCapacityManager;
	
	@Autowired
	GroupMgmtManager groupMgmtManager;
	
	@Autowired
	CodeManager codeManager;
	
	@Autowired
	TariffTypeManager tariffTypeManager;
	
	@Autowired
	TariffEMDao tariffEMDao;
	
	@Autowired
	TariffWMDao tariffWMDao;
	
	@Autowired
	CodeDao codeDao;
	
	@Autowired
	SeasonDao seasonDao;
	
	@Autowired
	TOURateDao touRateDao;
	
	@Autowired
	MeterDao meterDao;
	
	@Autowired
	MCUManager mcuManager;
	
	@Autowired
	ModemManager modemManager;

    @Autowired
    RoleManager roleManager;

	@Autowired
	CmdOperationUtil cmdOperationUtil;
	
	@Autowired
	MeterManager meterManager;

    @RequestMapping(value="/gadget/system/supplier/supplierMgmtMini")
    public ModelAndView loadSupplierMgmtMini() {
        ModelAndView mav = new ModelAndView("/gadget/system/supplier/supplierMgmtMini");
        return mav;
    }

    @RequestMapping(value="/gadget/system/supplier/supplierMgmtMax")
    public ModelAndView loadSupplierMgmtMax() {
        ModelAndView mav = new ModelAndView("/gadget/system/supplier/supplierMgmtMax");
        
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());

        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)
        mav.addObject("supplierId", user.getSupplier().getId());
        return mav;
    }

    @RequestMapping(value="/gadget/system/supplier/getSuppliers.do")
    public ModelAndView getSuppliers(HttpServletRequest request, HttpServletResponse response) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	try {
    		ESAPI.httpUtilities().setCurrentHTTP(request, response);
    		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        	
            AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
            AimirUser user = (AimirUser)instance.getUserFromSession();
            String notUse = " ";
            
			if (user != null && !user.isAnonymous()) {
            	int supplierId = user.getRoleData().getSupplier().getId();
            	Supplier supplier = supplierManager.getSupplier(supplierId);

				if (supplier.getLicenceUse() == 1 ) {
					mav.addObject("licenceUse", supplier.getLicenceUse());
        			mav.addObject("totalLicenceCount", supplier.getLicenceMeterCount());
        			mav.addObject("registeredLicenceCount", meterManager.getTotalMeterCount());
        			mav.addObject("availableLicenceCount", supplier.getLicenceMeterCount() - meterManager.getTotalMeterCount());
            	} else {
            		mav.addObject("licenceUse", supplier.getLicenceUse());
            		mav.addObject("totalLicenceCount", notUse);
        			mav.addObject("registeredLicenceCount", notUse);
        			mav.addObject("availableLicenceCount", notUse);
            	}
            	
//            	mav.addObject("suppliers", supplierManager.getSuppliers());
            	mav.addObject("supplier", supplier);
            } else {
            	mav.addObject("suppliers", supplierManager.getSuppliers());
            }
		} catch (Exception e) {
			log.error(e, e);
		}
    	
        return mav;
    }
    
    @RequestMapping(value="/gadget/system/supplier/getAppliedTariffDate.do") 
    public ModelAndView getAppliedTariffDate(String supplierType, String yyyymmdd, Integer supplierId) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	String date = supplierMgmtManager.getAppliedTariffDate(supplierType, yyyymmdd, supplierId);
    	mav.addObject("date", date);
    	return mav;
    }
    
    @RequestMapping(value="/gadget/system/supplier/getSupplier.do")
    public ModelAndView getSupplier(@RequestParam("supplierId") int supplierId) {
        Supplier supplier = supplierManager.getSupplier(supplierId);
        
        DecimalFormat mdFormat = DecimalUtil.getDecimalFormat(supplier.getMd());
        
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("supplier", supplier);
        List<Object> resultList = new ArrayList<Object>();
        Set<SupplyType> supplyTypes =  supplier.getSupplyTypes(); 
        SupplyType st = null;
        int count=0;
        Map<String,Object>  typemap= null; 
        for (Iterator<SupplyType> i = supplyTypes.iterator(); i.hasNext(); ) {
        	st = i.next();
        	typemap = new HashMap<String, Object>();
        	typemap.put("billDate", st.getBillDate());
            typemap.put("supplier", st.getSupplierId());
            typemap.put("type", st.getTypeCode().getDescr());
            typemap.put("typeCode", st.getTypeCode().getCode());
            typemap.put("co2emissions", mdFormat.format(st.getCo2Formula().getCo2emissions()));
            typemap.put("co2unitUsage", mdFormat.format(st.getCo2Formula().getUnitUsage())+" "+
            		st.getCo2Formula().getUnit());
            typemap.put("supplyType", st.getCo2Formula().getSupplyTypeCodeId());
            resultList.add(count,typemap);
        }
        
        Set<SupplyType> supplyTypesSetFormat =  supplier.getSupplyTypes(); 
        for (SupplyType supplyType : supplyTypes) {
        	Co2Formula co2Formula = supplyType.getCo2Formula();
        	co2Formula.setCo2emissions(co2Formula.getCo2emissions());
        	co2Formula.setUnitUsage(co2Formula.getUnitUsage());
			supplyType.setCo2Formula(co2Formula);
			supplyTypesSetFormat.add(supplyType);
		}

        mav.addObject("supplyTypes", supplyTypesSetFormat);
        mav.addObject("minisupplyTypes", resultList);
        
        String notUse = " ";
        if (supplier.getLicenceUse() == 1 ) {
			mav.addObject("licenceUse", supplier.getLicenceUse());
			mav.addObject("totalLicenceCount", supplier.getLicenceMeterCount());
			mav.addObject("registeredLicenceCount", meterManager.getTotalMeterCount());
			mav.addObject("availableLicenceCount", supplier.getLicenceMeterCount() - meterManager.getTotalMeterCount());
		} else {
			mav.addObject("licenceUse", supplier.getLicenceUse());
			mav.addObject("totalLicenceCount", notUse);
			mav.addObject("registeredLicenceCount", notUse);
			mav.addObject("availableLicenceCount", notUse);
		}
        
        return mav;
    }
    
    @RequestMapping("/gadget/system/supplier/deleteSupplier.do")
    public ModelAndView deleteSupplier(@RequestParam("supplierId") int supplierId) {
		ModelAndView mav = new ModelAndView("jsonView");
		supplierManager.delete(supplierId);
		
		mav.addObject("result", "success");
		return mav;
	}
    
    @RequestMapping(value="/gadget/system/supplier/getSupplyType.do")
    public ModelMap getSupplyType(@RequestParam("supplierId") int supplierId) {
        return new ModelMap(supplierManager.getSupplier(supplierId));
    }
    
    @RequestMapping("/gadget/system/supplier/deleteSupplyType.do")
    public ModelAndView deleteSupplyType(@RequestParam("supplyTypeId") int supplyTypeId) {
		ModelAndView mav = new ModelAndView("jsonView");
		supplyTypeManager.delete(supplyTypeId);
		
		mav.addObject("result", "success");
		return mav;
	}
    
    @RequestMapping(value="/gadget/system/supplier/getLocations.do")
    public ModelAndView getLocations(@RequestParam("supplierId") int supplierId) {
//        ModelMap model = new ModelMap("locationlist", locationManager.getParentsBySupplierId(supplierId));
        
//        return new ModelAndView("jsonView", model);
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("locationlist", locationManager.getParentsBySupplierId(supplierId));
        //System.out.println(locationManager.getParentsBySupplierId(supplierId));
        return mav;
    }
    
    @RequestMapping(value="/gadget/system/supplier/getLocation.do")
    public ModelAndView getLocation(@RequestParam("locationId") int locationId) {
        Location location = locationManager.getLocation(locationId);
        
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("location", location);
        mav.addObject("locationServices", location.getSupplyTypeLocations());
        
        return mav;
    }
    
    @RequestMapping(value="/gadget/system/supplier/deleteLocation.do")
    public ModelAndView deleteLocation(@RequestParam("locationId") int locationId) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	locationManager.delete(locationId);
        
        mav.addObject("result", "success");
		return mav;
    }
    

    @RequestMapping(value="/gadget/system/supplier/deleteLocationService.do")
    public ModelAndView deleteLocationService(@RequestParam("locationServiceId") int locationServiceId) {
		ModelAndView mav = new ModelAndView("jsonView");
		
		SupplyTypeLocation supplyTypeLocation = locationServiceManager.get(locationServiceId);
		locationServiceManager.delete(locationServiceId);
		
		int contract_id  = supplyTypeLocation.getContractCapacityId();
		if(!"".equals(Integer.toString(contract_id)) || Integer.toString(contract_id) != null){
			contractCapacityManager.delete(contract_id);
		}
	
		mav.addObject("result", "success");
		return mav;
	}
    
    /**
     * Tariff 정보
     * @param supplierId
     * @return
     */
    @RequestMapping(value="/gadget/system/supplier/getTariffType.do")
    public ModelAndView getSTSTariffType(String serviceType, Integer supplierId) {
		ModelAndView mav = new ModelAndView("jsonView");
		
		List<TariffType> tariffType = tariffTypeManager.getTariffTypeBySupplier(serviceType, supplierId);
		
		List<Map<String,Object>> tariffSTSType = new ArrayList<Map<String,Object>>();
		for (int i = 0; i < tariffType.size(); i++) {
			Map<String,Object> tempMap = new HashMap<String,Object>();
			tempMap.put("name", tariffType.get(i).getName());
			tempMap.put("id", tariffType.get(i).getId());
			tariffSTSType.add(tempMap);
		}
		
		mav.addObject("result", tariffSTSType);
		return mav;
	}
    
	@RequestMapping(value = "/gadget/system/supplier/getCount.do")
	public ModelAndView getCount() {
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("count", supplierManager.getCount().toString());
		return mav;
	}
    
    @RequestMapping(value="/gadget/system/supplier/getSupplierTypes.do")
    public ModelAndView getSupplierTypes() {
    	 
    	// ESAPI.setAuthenticator((Authenticator) new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();
		
    	List<SupplyType> supplyTypes = supplyTypeManager.getSupplyTypeBySupplierId(user.getSupplier().getId());
    	
    	Collections.sort(supplyTypes, new Comparator<SupplyType>(){
    		
 			@Override
 			public int compare(SupplyType o1, SupplyType o2) {
 				 String firstValue =  (String) codeManager.getCode(o1.getTypeCodeId()).getName();
 				 String secondValue = (String) codeManager.getCode(o2.getTypeCodeId()).getName();
 			    return firstValue.compareToIgnoreCase(secondValue);
 			}
 
         });
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("supplyTypes", supplyTypes);
        return mav;
    }
    
    @RequestMapping(value="/gadget/system/supplier/getYyyymmddList.do")
    public ModelAndView getYyyymmddList(
    		@RequestParam("supplierType") String supplierType,
    		@RequestParam(value="supplierId", required=false) Integer supplierId) {
    	List<Object> yyyymmddList = supplierMgmtManager.getYyyymmddList(supplierType, supplierId);
        
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("yyyymmddList", yyyymmddList);
        return mav;
    }
    
	@SuppressWarnings("rawtypes")
    @RequestMapping(value="/gadget/system/supplier/getRecentYyyymmddList.do")
    public ModelAndView getRecentYyyymmddList(
    		@RequestParam("supplierType") String supplierType,
    		@RequestParam(value="supplierId", required=false) Integer supplierId) {
    	List<Object> yyyymmddList = supplierMgmtManager.getYyyymmddList(supplierType, supplierId);
    	
    	Integer temp	= 0;
    	Integer MaxYyyymmdd 	= 0;
    	
    	for(Object yyyymmdd : yyyymmddList){
    		
    		temp = StringUtil.nullToBlank(((HashMap)yyyymmdd).get("yyyymmdd").toString()) == ""?0:Integer.parseInt(StringUtil.nullToBlank(((HashMap)yyyymmdd).get("yyyymmdd").toString()));
    		
    		if(MaxYyyymmdd < temp){
    			MaxYyyymmdd = temp;
    		}
    	}
        
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("MaxYyyymmdd", MaxYyyymmdd);
        return mav;
    }

    @RequestMapping(value="/gadget/system/supplier/supplierMaxExportPopup")
    public ModelAndView supplierMaxExportPopup(String fileType) {      
    	String popupName= "";
    	if("Electricity".equals(fileType)) {
    		popupName = "supplierElectricityExportExcelPopup";
    	} else if ("Gas".equals(fileType)) {
    		popupName = "supplierGasExportExcelPopup";
    	} else if ("Water".equals(fileType)) {
    		popupName = "supplierWaterExportExcelPopup";
    	} else if ("WaterCaliber".equals(fileType)) {
    		popupName = "supplierWaterCaliberExportExcelPopup";
    	}
    	
        ModelAndView mav = new ModelAndView("/gadget/system/supplier/"+popupName);
        return mav;
    }
    
	@SuppressWarnings({"unchecked", "rawtypes"})
    @RequestMapping(value="/gadget/system/supplier/supplierExcelMake")
    public ModelAndView supplierExcelMake(
    		@RequestParam("supplierId") 		String supplierId,
    		@RequestParam("fileType") 			String fileType,
    		@RequestParam("filePath") 			String filePath,
    		@RequestParam("head[]") 			String[] head,
    		@RequestParam("yyyymmdd") 			String yyyymmdd,
    		@RequestParam(value="tariffType", required=false) 		String tariffType
    		) {
    	
	     ModelAndView mav = new ModelAndView("jsonView");     
		 Map<String, String> titleMap = new HashMap<String, String>();
		 
		 List<String> fileNameList = new ArrayList();
		 List<Map<String, Object>> listEM = new ArrayList();
		 List<Map<String, Object>> listGM = new ArrayList();
		 List<Map<String, Object>> listWM = new ArrayList();
		 List<Map<String, Object>> listWMCaliber = new ArrayList();
		 
		 List<Map<String, Object>> resultWMCaliber = null;
		 List<Map<String, Object>> resultWM = null;
		 List<Map<String, Object>> resultGM = null;
		 List<Map<String, Object>> resultEM = null;
		 
		 Map<String, Object> list = new HashMap<String, Object>();
		
		 StringBuilder sbFileName = new StringBuilder();
		 StringBuilder sbSplFileName = new StringBuilder();
		 
		 final String ListData = "Supplier"; 
		
		 boolean isLast = false;
		 Long total = 0L;        // 데이터 조회건수
		 Long maxRows = 5000L;   // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수
		 
		 Map<String, Object> conditionMap = new HashMap<String, Object>();  
		 conditionMap.put("supplierId", supplierId);
		 conditionMap.put("supplyTypeName", fileType);
		 conditionMap.put("filePath", filePath);
		 conditionMap.put("yyyymmdd", yyyymmdd);
		 conditionMap.put("tariffType", tariffType);
		 
		 list = supplierMgmtManager.getChargeMgmtList(conditionMap);

		 try {
			 if("Electricity".equals(fileType)) {
				 resultEM = (List<Map<String, Object>>) list.get("grid");
				 total = new Integer(resultEM.size()).longValue();
			 } else if ("Gas".equals(fileType)) {
				 resultGM = (List<Map<String, Object>>) list.get("grid");
				 total = new Integer(resultGM.size()).longValue();
			 } else if ("Water".equals(fileType)) {
				 resultWM = (List<Map<String, Object>>) list.get("grid2");
				 total = new Integer(resultWM.size()).longValue();
			 } else if ("WaterCaliber".equals(fileType)) {
				 resultWMCaliber = (List<Map<String, Object>>) list.get("grid1");
				 total = new Integer(resultWMCaliber.size()).longValue();
			 }
			 
			 mav.addObject("total", total);
			 if (total <= 0) {
				 return mav;
			 }
			 sbFileName.append("Tariff_"+fileType);
			 
			// message 생성
			 if("Electricity".equals(fileType)) {
				 titleMap.put("date", 						head[0]);
				 titleMap.put("tariff",						head[1]);
				 titleMap.put("supplySize",   				head[2]);
				 titleMap.put("serviceCharge",				head[3]);
				 titleMap.put("govLevy",  					head[4]);
				 titleMap.put("publicLevy", 				head[5]);
				 titleMap.put("vat",						head[6]);
				 titleMap.put("activeEnergyCharge",			head[7]);
				 titleMap.put("lifeLineSubsidy",			head[8]);
				 titleMap.put("govSubsidy",					head[9]);
				 titleMap.put("additionalSubsidy",			head[10]);
				 titleMap.put("utilityRelief",				head[11]);
			 } else if ("Gas".equals(fileType)) {
				 titleMap.put("tariffType",					head[0]);
				 titleMap.put("season",						head[1]);
				 titleMap.put("basicRate",					head[2]);
				 titleMap.put("usageUnitPrice",				head[3]);
				 titleMap.put("salePrice",					head[4]);
				 titleMap.put("adjustmentFactor",			head[5]);
			 } else if ("Water".equals(fileType)) {
				 titleMap.put("date", 						head[0]);
				 titleMap.put("tariff",						head[1]);
				 titleMap.put("supplySize",   				head[2]);
				 titleMap.put("serviceCharge",				head[3]);
				 titleMap.put("transmissionNetworkCharge",  head[4]);
				 titleMap.put("distributionNetworkCharge", 	head[5]);
				 titleMap.put("energy",						head[6]);
				 titleMap.put("activeEnergyCharge",			head[7]);
				 titleMap.put("reactiveEnergyCharge",		head[8]);
				 titleMap.put("adminCharge",				head[9]);
				 titleMap.put("rate",						head[10]);
				 titleMap.put("maxDemand",					head[11]);
				 titleMap.put("season",						head[12]);
				 titleMap.put("tou",						head[13]);
				 titleMap.put("hour",						head[14]);
			 } else if ("WaterCaliber".equals(fileType)) {
				 titleMap.put("caliber", 					head[0]);
				 titleMap.put("basicRate", 					head[1]);
				 titleMap.put("basicRateHot", 				head[2]);
				 titleMap.put("supplier", 					head[3]);
			 } 

		        
		     // check download dir
		        File downDir = new File(filePath);
		        
		        if (downDir.exists()) {
		            File[] files = downDir.listFiles();
		            
		            if (files != null) {
		                String filename = null;
		                String deleteDate;
		                
		                deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -10);	// 10일 이전 일자
		                boolean isDel = false;
		                
		                for (File file : files) {
		                	
		                    filename = file.getName();
		                    isDel = false;
		                    
		                 // 파일길이 : 22이상, 확장자 : xls|zip
		                    if (filename.length() > 22 && (filename.endsWith("xls") || filename.endsWith("zip"))) {
		                        // 10일 지난 파일들 삭제
		                        if (filename.startsWith(ListData) && filename.substring(3, 11).compareTo(deleteDate) < 0) {
		                            isDel = true;
		                        } 
		
		                        if (isDel) {
		                            file.delete();
		                        }
		                    }
		                    filename = null;
		                }
		            }
		        } else {
		            // directory 가 없으면 생성
		            downDir.mkdir();
		        }  
		        
		     // create excel file
		        int cnt = 1;
	            int idx = 0;
	            int fnum = 0;
	            int splCnt = 0;
	            
	            SupplierEMDataMakeExcel wExcelEM = new SupplierEMDataMakeExcel();
                SupplierGMDataMakeExcel wExcelGM = new SupplierGMDataMakeExcel();
                SupplierWMDataMakeExcel wExcelWM = new SupplierWMDataMakeExcel();
	            
	            if (total <= maxRows) {
	                sbSplFileName = new StringBuilder();
	                sbSplFileName.append(sbFileName);
	                sbSplFileName.append(".xls");
	                
	                if("Electricity".equals(fileType)) {
			        	wExcelEM.writeExportExcelEM(resultEM, titleMap, isLast, filePath, sbSplFileName.toString());
					 } else if ("Gas".equals(fileType)) {
						 wExcelGM.writeExportExcelGM(resultGM, titleMap, isLast, filePath, sbSplFileName.toString());
					 } else if ("Water".equals(fileType)) {
						  wExcelWM.writeReportExcel(resultWM, titleMap, isLast, filePath, sbSplFileName.toString());
					 } else if ("WaterCaliber".equals(fileType)) {
						 wExcelWM.writeReportExcelCaliber(resultWMCaliber, titleMap, isLast, filePath, sbSplFileName.toString());
					 }
	                
	                fileNameList.add(sbSplFileName.toString());
	            } else {
	                for (int i = 0; i < total; i++) {
	                    if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
	                        sbSplFileName = new StringBuilder();
	                        sbSplFileName.append(sbFileName);
	                        sbSplFileName.append('(').append(++fnum).append(").xls");
	
	                        if("Electricity".equals(fileType)) {
	                        	listEM = resultEM.subList(idx, (i + 1));
		                        wExcelEM.writeExportExcelEM(listEM, titleMap, isLast, filePath, sbSplFileName.toString());
	    					 } else if ("Gas".equals(fileType)) {
	    						 listGM = resultGM.subList(idx, (i + 1));
	 	                        wExcelGM.writeExportExcelGM(listGM, titleMap, isLast, filePath, sbSplFileName.toString());
	    					 } else if ("Water".equals(fileType)) {
	    						 listWM = resultWM.subList(idx, (i + 1));
	 	                         wExcelWM.writeReportExcel(listWM, titleMap, isLast, filePath, sbSplFileName.toString());
	    					 } else if ("WaterCaliber".equals(fileType)) {
	    						 listWMCaliber = resultWMCaliber.subList(idx, (i + 1));
	 	                         wExcelWM.writeReportExcelCaliber(listWMCaliber, titleMap, isLast, filePath, sbSplFileName.toString());
	    					 }
	                        
	                        fileNameList.add(sbSplFileName.toString());
	                        
	                        if("Electricity".equals(fileType)) {
	                        	listEM = null;
	    					 } else if ("Gas".equals(fileType)) {
	    						 listGM = null;
	    					 } else if ("Water".equals(fileType)) {
	    						 listWM = null;
	    					 } else if ("WaterCaliber".equals(fileType)) {
	    						 listWMCaliber = null;
	    					 }
	                        
	                        splCnt = cnt;
	                        cnt = 0;
	                        idx = (i + 1);
	                    }
	                    cnt++;
	                }
	            }
	            
	           // create zip file
	            StringBuilder sbZipFile = new StringBuilder();
	            sbZipFile.append(sbFileName).append(".zip");
	
	            ZipUtils zutils = new ZipUtils();
				zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);
	
	
	            // return object
	            mav.addObject("filePath", filePath);
	            mav.addObject("fileName", fileNameList.get(0));
	            mav.addObject("zipFileName", sbZipFile.toString());
	            mav.addObject("fileNames", fileNameList);
	        
	        } catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
		 return mav;
        
    }
	
	@RequestMapping(value="/gadget/system/supplier/getTempFileName") 
	public ModelAndView getTempFileName(HttpServletRequest request, HttpServletResponse response)
					throws ServletRequestBindingException, IOException {
		
		String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");

		MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest)request;
		MultipartFile multipartFile = multiReq.getFile("userfile");

		String filename = multipartFile.getOriginalFilename();
		if (filename == null || "".equals(filename))
			return null;
				
		String tempPath = contextRoot+"temp";
		
		if (!FileUploadHelper.exists(tempPath)) {
			File savedir = new File(tempPath);
			savedir.mkdir();
		}
		File uFile = new File(FileUploadHelper.makePath(tempPath, filename));
		
		if(FileUploadHelper.exists(FileUploadHelper.makePath(tempPath, filename))){
			
			if(FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(tempPath, filename))){
		
				multipartFile.transferTo(uFile);
			}
		}
		else multipartFile.transferTo(uFile);
		
		String filePath = tempPath+"/"+filename;

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("filePath", filePath);
		
		return mav;
	}

	@RequestMapping(value="/gadget/system/supplier/insertEMData") 
	public ModelAndView insertEMData(@RequestParam("serviceCharge") String serviceCharge,
			@RequestParam("adminCharge") String adminCharge,
			@RequestParam("transmissionNetworkCharge") String transmissionNetworkCharge,
			@RequestParam("distributionNetworkCharge") String distributionNetworkCharge,
			@RequestParam("energyDemandCharge") String energyDemandCharge,
			@RequestParam("activeEnergyCharge") String activeEnergyCharge,
			@RequestParam("reactiveEnergyCharge") String reactiveEnergyCharge,
			@RequestParam("rateRebalancingLevy") String rateRebalancingLevy,
			@RequestParam("maxDemand") String maxDemand,
			@RequestParam("tariffType") String tariffType,
			@RequestParam("season") String season,
			@RequestParam("yyyymmdd") String yyyymmdd,
			@RequestParam("supplyTypeName") String supplyTypeName) {

		Map<String, Object> conditionMap = new HashMap<String, Object>();
		
		String[] tariffTypeArr = tariffType.split(",");
		int dataCount = tariffTypeArr.length;
		
		conditionMap.put("tariffType", tariffTypeArr);
		conditionMap.put("serviceCharge", serviceCharge.split(",",dataCount));
		conditionMap.put("adminCharge", adminCharge.split(",",dataCount));
		conditionMap.put("transmissionNetworkCharge", transmissionNetworkCharge.split(",",dataCount));
		conditionMap.put("distributionNetworkCharge", distributionNetworkCharge.split(",",dataCount));
		conditionMap.put("energyDemandCharge", energyDemandCharge.split(",",dataCount));
		conditionMap.put("activeEnergyCharge", activeEnergyCharge.split(",",dataCount));
		conditionMap.put("reactiveEnergyCharge", reactiveEnergyCharge.split(",",dataCount));
		conditionMap.put("rateRebalancingLevy", rateRebalancingLevy.split(",",dataCount));
		conditionMap.put("maxDemand", maxDemand.split(",",dataCount));
		conditionMap.put("season", season.split(",",dataCount));
		conditionMap.put("yyyymmdd", yyyymmdd.split(",",dataCount));
		conditionMap.put("supplyTypeName", supplyTypeName);

		int resultCnt = supplierMgmtManager.insertEMData(conditionMap);

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("result", resultCnt);
		
		return mav;
	}
	
	@RequestMapping(value="/gadget/system/supplier/insertGMData") 
	public ModelAndView insertGMData(@RequestParam("basicRate") String basicRate,
			@RequestParam("usageUnitPrice") String usageUnitPrice,
			@RequestParam("salePrice") String salePrice,
			@RequestParam("adjustmentFactor") String adjustmentFactor,
			@RequestParam("tariffType") String tariffType,
			@RequestParam("season") String season,
			@RequestParam("yyyymmdd") String yyyymmdd,
			@RequestParam("supplyTypeName") String supplyTypeName) {

		Map<String, Object> conditionMap = new HashMap<String, Object>();
		
		String[] tariffTypeArr = tariffType.split(",");
		int dataCount = tariffTypeArr.length;
		
		conditionMap.put("tariffType", tariffTypeArr);
		conditionMap.put("usageUnitPrice", usageUnitPrice.split(",",dataCount));
		conditionMap.put("salePrice", salePrice.split(",",dataCount));
		conditionMap.put("adjustmentFactor", adjustmentFactor.split(",",dataCount));
		conditionMap.put("season", season.split(",",dataCount));
		conditionMap.put("yyyymmdd", yyyymmdd.split(",",dataCount));
		conditionMap.put("supplyTypeName", supplyTypeName);

		int resultCnt = supplierMgmtManager.insertGMData(conditionMap);

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("result", resultCnt);
		
		return mav;
	}
	
	@RequestMapping(value="/gadget/system/supplier/insertWMData") 
	public ModelAndView insertWMData(@RequestParam("caliber") String caliber,
			@RequestParam("basicRate") String basicRate,
			@RequestParam("basicRateHot") String basicRateHot,
			@RequestParam("supplierName") String supplierName,
			@RequestParam("writeTime") String writeTime,
			
			@RequestParam("usageUnitPrice") String usageUnitPrice,
			@RequestParam("share") String share,
			@RequestParam("tariffType") String tariffType,
			@RequestParam("yyyymmdd") String yyyymmdd,
			@RequestParam("supplyTypeName") String supplyTypeName) {

		Map<String, Object> conditionMap = new HashMap<String, Object>();
		
		String[] tariffTypeArr = tariffType.split(",");
		String[] caliberArr = caliber.split(",");
		int dataCountWM = tariffTypeArr.length;
		int dataCountCaliber = caliberArr.length;
		
		conditionMap.put("caliber", caliberArr);
		conditionMap.put("basicRate", basicRate.split(",", dataCountCaliber));
		conditionMap.put("basicRateHot", basicRateHot.split(",", dataCountCaliber));
		conditionMap.put("supplierName", supplierName.split(",", dataCountCaliber));
		conditionMap.put("writeTime", writeTime.split(",", dataCountCaliber));
		
		conditionMap.put("usageUnitPrice", usageUnitPrice.split(",", dataCountWM));
		conditionMap.put("share", share.split(",", dataCountWM));
		conditionMap.put("tariffType", tariffTypeArr);
		conditionMap.put("yyyymmdd", yyyymmdd.split(",", dataCountWM));
		
		conditionMap.put("supplyTypeName", supplyTypeName);

		int resultCnt = supplierMgmtManager.insertWMData(conditionMap);

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("result", resultCnt);
		
		return mav;
	}
	
    /**
     * Tariff정보 변경시 IHD에 보내는 업데이트 정보
     * @param mdsId
     * @return
     */
    @SuppressWarnings({"unused", "unchecked", "rawtypes"})
    @RequestMapping(value="/gadget/system/supplier/sendIHDTariffMessage")
    public ModelAndView sendIHDTariffMessage() {
    	Map<String, Object> conditionMap = new HashMap<String, Object>();
    	String result = "";
    	int cntSuccess = 0;
    	int cntFail = 0;
    	ModelAndView mav = new ModelAndView("jsonView");
        conditionMap.put("groupType", "IHD");
        List<Map<String, Object>> failList = new ArrayList();
        Map<String, Object> failMeter = new HashMap<String, Object>();
        
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer operatorId = null;

        if(user != null && !user.isAnonymous()) {
            try {
                operatorId = user.getOperator(new Operator()).getId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        conditionMap.put("operatorId", operatorId);

        //IHD Type의 그룹을 조회
        List<Map<String, Object>> group = groupMgmtManager.getGroupComboDataByType(conditionMap);
        
    	ResultStatus status = ResultStatus.FAIL;
    	List<String> mdsIdList = new ArrayList();
    	List<List<String>> mdsIdListbyGroup = new ArrayList();
    	GroupMember groupMemeber = null;
    	
    	int groupId = 0;
    	for (int i = 0; i < group.size(); i++) {
    		groupId = (Integer) group.get(i).get("id");
    		Iterator<GroupMember> it = groupMgmtManager.getGroupMemberById(groupId).iterator();  		
    		while(it.hasNext()) {
    			groupMemeber = it.next();
    			mdsIdList.add(groupMemeber.getMember().toString());
    		}
    		if(mdsIdList.size() > 0) {
    			mdsIdListbyGroup.add(mdsIdList);
    			mdsIdList = new ArrayList();
    		}
		}
    	
    	String mdsId = null;
    	Meter meter_EM = null;
		Meter meter_WM = null;
		Modem modem = null;
		MCU mcu = null;
		String rtnStr = "";
    	for (int i = 0; i < mdsIdListbyGroup.size(); i++) { //그룹
    		for (int j = 0; j < mdsIdListbyGroup.get(i).size(); j++) { //멤버
	    		mdsId = mdsIdListbyGroup.get(i).get(j).trim();
	    		Meter tmpMeter = meterDao.get(mdsId);
	    		//IHD Modem인 경우
	    		if(tmpMeter == null) {
	    			modem = modemManager.getModem(mdsId);
	    			mcu = mcuManager.getMCU(modem.getMcuId());
	    			continue;
	    		}
	    		
	    		if(tmpMeter != null && tmpMeter.getMeterType().getCode().equals("1.3.1.1")){ // EnergyMeter
					meter_EM = new EnergyMeter();
					meter_EM = tmpMeter;
				} else if(tmpMeter != null && tmpMeter.getMeterType().getCode().equals("1.3.1.2")){ // WaterMeter
					meter_WM = new WaterMeter();
					meter_WM = tmpMeter;
				}
    		}
    		
			List<TariffEM> tariffEMList = null;
			int tariffEMSize = 0;
			List<TariffWM> tariffWMList = null;
			int tariffWMSize = 0;
			
			if(meter_EM != null && meter_EM.getContract() != null){
				tariffEMList = tariffEMDao.getNewestTariff(meter_EM.getContract());
				tariffEMSize = tariffEMList.size();
			}
			
			if(meter_WM != null && meter_WM.getContract() != null){
				tariffWMList = tariffWMDao.getNewestTariff(meter_WM.getContract());
				tariffWMSize = tariffWMList.size();
			}
			rtnStr = "";
			Date date = new Date();
			Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
			rtnStr += getTypeFrame("01", formatter.format(date));
			
			if(tariffEMSize > 0){
				if(Code.POSTPAY.equals(meter_EM.getContract().getCreditType().getCode())) {
					if(tariffEMList.get(0) != null && tariffEMList.get(0).getTariffType() != null){
						rtnStr += getTypeFrame("02", tariffEMList.get(0).getTariffType().getName());
						
						if(((EnergyMeter)meter_EM).getMeterElementCodeId() != null) {
							rtnStr += getTypeFrame("03", codeDao.get(((EnergyMeter)meter_EM).getMeterElementCodeId()).getName());
						}
						if(tariffEMList.get(0).getActiveEnergyCharge() != null && !"".equals(tariffEMList.get(0).getActiveEnergyCharge())) {
							rtnStr += getTypeFrame("04", StringUtil.nullToBlank(tariffEMList.get(0).getActiveEnergyCharge()));
						}
						if(tariffEMList.get(0).getRateRebalancingLevy() != null && !"".equals(tariffEMList.get(0).getRateRebalancingLevy())) {
							rtnStr += getTypeFrame("05", StringUtil.nullToBlank(tariffEMList.get(0).getRateRebalancingLevy()));
						}
					}
					rtnStr += getTypeFrame("13", "Electricity");
					
					for(int cnt=0; cnt<tariffEMSize; cnt++){
						if(tariffEMList.get(cnt) != null && tariffEMList.get(cnt).getTariffType() != null){
							if(tariffEMList.get(cnt).getSeason() != null){
								if(tariffEMList.get(cnt).getSeason() != null) {
									rtnStr += getTypeFrame("06", StringUtil.nullToBlank(tariffEMList.get(cnt).getSeason().getName()));
								}
								if(tariffEMList.get(cnt).getPeakType() != null) {
									rtnStr += getTypeFrame("07", StringUtil.nullToBlank(tariffEMList.get(cnt).getPeakType().name()));						
								}
								//TOU 생성
								TOURate touRateEM = touRateDao.getTOURate(tariffEMList.get(cnt).getTariffType().getId(), tariffEMList.get(cnt).getSeason().getId(), tariffEMList.get(cnt).getPeakType());
								if(!(touRateEM == null || "".equals(touRateEM))) {
									rtnStr += getTypeFrame("08", StringUtil.nullToBlank(touRateEM.getStartTime()));
									rtnStr += getTypeFrame("09", StringUtil.nullToBlank(touRateEM.getEndTime()));
								}
							}
						}
						
					}
					
					//4계절 정보를 모두 보냄
					List<Season> season = seasonDao.getSeasons();
					for (int k = 0; k < season.size(); k++) {
						rtnStr += getTypeFrame("10", season.get(k).getName().toString());
						rtnStr += getTypeFrame("11", season.get(k).getSmonth().toString()+season.get(k).getSday().toString());
						rtnStr += getTypeFrame("12", season.get(k).getEmonth().toString()+season.get(k).getEday().toString());
					}
				}
				//선불고객일때
				else if(Code.PREPAYMENT.equals(meter_EM.getContract().getCreditType().getCode()) 
						|| Code.EMERGENCY_CREDIT.equals(meter_EM.getContract().getCreditType().getCode())) {
					for(int cnt=0; cnt<tariffEMSize; cnt++){
						rtnStr += getTypeFrame("02", tariffEMList.get(0).getTariffType().getName());
						
						if(((EnergyMeter)meter_EM).getMeterElementCodeId() != null) {
							rtnStr += getTypeFrame("03", codeDao.get(((EnergyMeter)meter_EM).getMeterElementCodeId()).getName());
						}
						if(tariffEMList.get(cnt).getActiveEnergyCharge() != null && !"".equals(tariffEMList.get(cnt).getActiveEnergyCharge())) {
							rtnStr += getTypeFrame("04", StringUtil.nullToBlank(tariffEMList.get(0).getActiveEnergyCharge()));
						}
						if(tariffEMList.get(cnt).getRateRebalancingLevy() != null && !"".equals(tariffEMList.get(cnt).getRateRebalancingLevy())) {
							rtnStr += getTypeFrame("05", StringUtil.nullToBlank(tariffEMList.get(0).getRateRebalancingLevy()));
						}
						
						rtnStr += getTypeFrame("13", "Electricity");
	//					//Block요금제 내려보냄
						if(tariffEMList.get(cnt).getSupplySizeMin() == null) {
							rtnStr += getTypeFrame("15", "0");
						} else {
							rtnStr += getTypeFrame("15", tariffEMList.get(cnt).getSupplySizeMin().toString());
						}
						if(tariffEMList.get(cnt).getSupplySizeMax() == null) {
							//tariffEMList의 마지막이 null일경우 0으로 보냄
							rtnStr += getTypeFrame("16", "0");
						} else {
							rtnStr += getTypeFrame("16", tariffEMList.get(cnt).getSupplySizeMax().toString());
						}
						
						
					}
				}
		}
			
			
			//WM
	    	if(Code.PREPAYMENT.equals(meter_WM.getContract().getCreditType().getCode()) 
	    			|| Code.EMERGENCY_CREDIT.equals(meter_WM.getContract().getCreditType().getCode())) {
				if(tariffWMSize > 0){
					for(int cnt=0; cnt<tariffWMSize; cnt++){
						if(tariffWMList.get(cnt) != null && tariffWMList.get(cnt).getTariffType() != null){
							rtnStr += getTypeFrame("02", tariffWMList.get(cnt).getTariffType().getName());
							
							if(tariffWMList.get(cnt).getUsageUnitPrice() != null) {
								rtnStr += getTypeFrame("04", StringUtil.nullToBlank(tariffWMList.get(cnt).getUsageUnitPrice()));
							} 
							if(tariffWMList.get(cnt).getShareCost() != null) {
								rtnStr += getTypeFrame("05", StringUtil.nullToBlank(tariffWMList.get(cnt).getShareCost()));
							}
							rtnStr += getTypeFrame("13", "Water");
							
							//Block요금제 내려보냄
							if(tariffWMList.get(cnt).getSupplySizeMin() == null) {
								rtnStr += getTypeFrame("15", "0");
							} else {
								rtnStr += getTypeFrame("15", tariffWMList.get(cnt).getSupplySizeMin().toString());
							}
							if(tariffWMList.get(cnt).getSupplySizeMax() == null) {
								//tariffEMList의 마지막이 null일경우 0으로 보냄
								rtnStr += getTypeFrame("16", "0");
							} else {
								rtnStr += getTypeFrame("16", tariffWMList.get(cnt).getSupplySizeMax().toString());
							}
						}
					}
				}
			}
			
			
			 
	    	try {
	    		IHD_RequestDataFrame rf = new IHD_RequestDataFrame();
				cmdOperationUtil.cmdSendIHDData(mcu.getSysID(), modem.getDeviceSerial(), rf.getBytes("53","49","31",rtnStr));
				status = ResultStatus.SUCCESS;
				result = status.name();
				cntSuccess++;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				cntFail++;
				result = e.getMessage();
				e.printStackTrace();
			
    		}
    	}
		mav.addObject("cntSuccess", cntSuccess);
		mav.addObject("cntFail", cntFail);
		
		return mav;
    }
        
    /**
     * CustomerInfosMessage 변경시 IHD에 보내는 업데이트 정보
     * @param mdsId
     * @return
     */
    @SuppressWarnings({"unused", "unchecked", "rawtypes"})
    @RequestMapping(value="/gadget/system/supplier/sendIHDCustomerInfosMessage")
    public ModelAndView sendIHDCustomerInfosMessage() {
    	Map<String, Object> conditionMap = new HashMap<String, Object>();
    	String result = "";
    	int cntSuccess = 0;
    	int cntFail = 0;
    	ModelAndView mav = new ModelAndView("jsonView");
        conditionMap.put("groupType", "IHD");
        List<Map<String, Object>> failList = new ArrayList();
        Map<String, Object> failMeter = new HashMap<String, Object>();
        
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer operatorId = null;

        if(user != null && !user.isAnonymous()) {
            try {
                operatorId = user.getOperator(new Operator()).getId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        conditionMap.put("operatorId", operatorId);

        List<Map<String, Object>> group = groupMgmtManager.getGroupComboDataByType(conditionMap);
        
    	ResultStatus status = ResultStatus.FAIL;
    	
    	List<List<String>> mdsIdListbyGroup = new ArrayList();
    	GroupMember groupMemeber = null;
    	
    	int groupId = 0;
    	for (int i = 0; i < group.size(); i++) {
    		List<String> mdsIdList = new ArrayList();
    		groupId = (Integer) group.get(i).get("id");
    		Iterator<GroupMember> it = groupMgmtManager.getGroupMemberById(groupId).iterator();  		
    		while(it.hasNext()) {
    			groupMemeber = it.next();
    			mdsIdList.add(groupMemeber.getMember().toString());
    		}
    		if(0 < mdsIdList.size()) {
    			mdsIdListbyGroup.add(mdsIdList);
    			mdsIdList = new ArrayList();
    		}
		}
    	
    	String mdsId = null;
    	Meter meter_EM = null;
		Meter meter_WM = null;
		Modem modem = null;
		MCU mcu = null;
		
    	for (int i = 0; i < mdsIdListbyGroup.size(); i++) {
    		for (int j = 0; j < mdsIdListbyGroup.get(i).size(); j++) {
	    		mdsId = mdsIdListbyGroup.get(i).get(j).trim();
	    		Meter tmpMeter = meterDao.get(mdsId);
	    		//IHD Modem인 경우
	    		if(tmpMeter == null) {
	    			modem = modemManager.getModem(mdsId);
	    			mcu = mcuManager.getMCU(modem.getMcuId());
	    			continue;
	    		}
		    	
	    		if(tmpMeter != null && tmpMeter.getMeterType().getCode().equals("1.3.1.1")){ // EnergyMeter
					meter_EM = new EnergyMeter();
					meter_EM = tmpMeter;
				} else if(tmpMeter != null && tmpMeter.getMeterType().getCode().equals("1.3.1.2")){ // WaterMeter
					meter_WM = new WaterMeter();
					meter_WM = tmpMeter;
				}
    		}	
			String rtnStr = "";
			Supplier supplier 	= modem.getSupplier();
			
			rtnStr += getTypeFrame("01", "BillingDeterminants");	 //고정값
			
			Date date = new Date();
			Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
			rtnStr += getTypeFrame("02", formatter.format(date));
			
			rtnStr += getTypeFrame("03", "Rand");
			
			if(supplier != null && supplier.getLang() != null) {
				rtnStr += getTypeFrame("04", supplier.getLang().getName());
			}
			if(supplier != null && supplier.getMd() != null) {
				rtnStr += getTypeFrame("05", supplier.getMd().getPattern());
			}
			if(supplier != null && supplier.getCd() != null) {
				rtnStr += getTypeFrame("06", supplier.getCd().getPattern());
			}
			/*
			 * EM 	
			 */		
			if(meter_EM != null){
			
				rtnStr += getTypeFrame("10", "Electricity");
				
				rtnStr += getTypeFrame("11", "kWh");
				
	  		if(((EnergyMeter)meter_EM).getMeterElementCodeId() != null) {
					rtnStr += getTypeFrame("12", codeDao.get(((EnergyMeter)meter_EM).getMeterElementCodeId()).getName());
				}
				if(meter_EM.getContract() != null ){
					if(meter_EM.getContract().getCreditType() != null) {
						rtnStr += getTypeFrame("13", StringUtil.nullToBlank(meter_EM.getContract().getCreditType().getCode()));
					}
					if(meter_EM.getContract().getBillDate() != null) {
						rtnStr += getTypeFrame("14", StringUtil.nullToBlank(meter_EM.getContract().getBillDate()));
					}
					if(meter_EM.getContract().getContractDemand() != null) {
						rtnStr += getTypeFrame("15", StringUtil.nullToBlank(new BigDecimal(meter_EM.getContract().getContractDemand())));
					}
					if(meter_EM.getContract().getTariffIndex() != null) {
						rtnStr += getTypeFrame("16", StringUtil.nullToBlank(meter_EM.getContract().getTariffIndex().getCode()));
					}
				}
			}
			
			/*
			 * WM 	
			 */
			if(meter_WM != null){
				rtnStr += getTypeFrame("20", "Water");
				
				rtnStr += getTypeFrame("21", "m3");
	  		if(meter_WM.getContract() != null){
					if(meter_WM.getContract().getCreditType() != null) {
						rtnStr += getTypeFrame("22", StringUtil.nullToBlank(meter_WM.getContract().getCreditType().getCode()));
					}
					
					if(meter_WM.getContract().getBillDate() != null) {
						rtnStr += getTypeFrame("23", StringUtil.nullToBlank(meter_WM.getContract().getBillDate()));
					}
					
					if(meter_WM.getContract().getContractDemand() != null) {
						rtnStr += getTypeFrame("24", StringUtil.nullToBlank(new BigDecimal(meter_WM.getContract().getContractDemand())));
					}
					
					if(meter_WM.getContract().getTariffIndex() != null) {
						rtnStr += getTypeFrame("25", StringUtil.nullToBlank(meter_WM.getContract().getTariffIndex().getCode()));
					}
				}
			}
			
    	
	    	try {
	    		IHD_RequestDataFrame rf = new IHD_RequestDataFrame();
				cmdOperationUtil.cmdSendIHDData(mcu.getSysID(), modem.getDeviceSerial(), rf.getBytes("53","49","30",rtnStr));
				status = ResultStatus.SUCCESS;
				result = status.name();
				cntSuccess++;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				cntFail++;
				result = e.getMessage();
				e.printStackTrace();
			}
    	}
    	
		mav.addObject("cntSuccess", cntSuccess);
		mav.addObject("cntFail", cntFail);
		
		return mav;
    }
    
    @RequestMapping("/gadget/system/supplier/getTariffGrid") 
    public ModelAndView getTariffGrid(
    		@RequestParam("supplierId") 		String supplierId,
    		@RequestParam("fileType") 			String fileType,
    		@RequestParam("yyyymmdd") 			String yyyymmdd,
    		@RequestParam("tariffType") 			String tariffType
    		) {
		List<Map<String, Object>> resultWMCaliber = null; // WaterCaliber
		List<Map<String, Object>> resultWM = null; // Water
		List<Map<String, Object>> resultGM = null; // Gas
		List<Map<String, Object>> resultEM = null; // Electricity

		Long total = 0L;        // 데이터 조회건수
		
    	Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("supplierId", supplierId);
		conditionMap.put("supplyTypeName", fileType);
		conditionMap.put("yyyymmdd", yyyymmdd);
		conditionMap.put("tariffType", tariffType);
    	Map<String, Object> list = new HashMap<String, Object>();
    	ModelAndView mav = new ModelAndView("jsonView");
    	list = supplierMgmtManager.getChargeMgmtList(conditionMap);

    	if("Electricity".equals(fileType)) {
			 resultEM = (List<Map<String, Object>>) list.get("grid");
			 total = new Integer(resultEM.size()).longValue();
			 mav.addObject("GridData", resultEM);
		 } else if ("Gas".equals(fileType)) {
			 resultGM = (List<Map<String, Object>>) list.get("grid");
			 total = new Integer(resultGM.size()).longValue();
			 mav.addObject("GridData", resultGM);
		 } else if ("Water".equals(fileType)) {
			 resultWM = (List<Map<String, Object>>) list.get("grid2");
			 total = new Integer(resultWM.size()).longValue();
			 mav.addObject("GridData", resultWM);
		 } else if ("WaterCaliber".equals(fileType)) {
			 resultWMCaliber = (List<Map<String, Object>>) list.get("grid1");
			 total = new Integer(resultWMCaliber.size()).longValue();
			 mav.addObject("GridData", resultWMCaliber);
		 }
		 
		 mav.addObject("total", total);
		 if (total <= 0) {
			 return mav;
		 }
		 
    	return mav;
    }

    
    @RequestMapping("/gadget/system/supplier/updateTariffTable") 
    public ModelAndView updateTariffTable(String date, String data) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	String result = supplierMgmtManager.updateTariffEMTable(date, data);
    	mav.addObject("result", result);
    	return mav;
    }
    
    @RequestMapping("/gadget/system/supplier/updateTariffWMTable") 
    public ModelAndView updateTariffWMTable(String date, String data) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	String result = supplierMgmtManager.updateTariffWMTable(date, data);
    	mav.addObject("result", result);
    	return mav;
    }
    
    @RequestMapping("/gadget/system/supplier/updateTaxRate.do")
    public ModelAndView updateTaxRate(@RequestParam Integer supplierId, @RequestParam Float taxRate) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	Supplier supplier = supplierManager.getSupplier(supplierId);
    	String result = null;
    	
    	try {
    		supplier.setTaxRate(taxRate);
    		supplierManager.update(supplier);
    		result = "success";
    		mav.addObject("result", result);
    	} catch (Exception e) {
    		e.printStackTrace();
    		result = "fail";
    		mav.addObject("result", result);
    	} 
    	return mav;
    }
    
    @RequestMapping("/gadget/system/supplier/updateCommissionRate.do")
    public ModelAndView updateCommissionRate(@RequestParam Integer supplierId, @RequestParam Float commissionRate) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	Supplier supplier = supplierManager.getSupplier(supplierId);
    	String result = null;
    	
    	try {
    		supplier.setCommissionRate(commissionRate);
    		supplierManager.update(supplier);
    		result = "success";
    		mav.addObject("result", result);
    	} catch (Exception e) {
    		e.printStackTrace();
    		result = "fail";
    		mav.addObject("result", result);
    	} 
    	return mav;
    }    

    @RequestMapping(value = "/gadget/system/supplier/supplierLocationExportExcelPopup")
    public ModelAndView supplierMaxLocationExportPopup(String fileType) {
        ModelAndView mav = new ModelAndView("/gadget/system/supplier/supplierLocationExportExcelPopup");
        return mav;
    }

    @RequestMapping(value = "/gadget/system/supplier/supplierMaxLocationExcelMake")
    public ModelAndView supplierMaxLocationExcelMake(@RequestParam("supplierId") Integer supplierId,
            @RequestParam("header") String header,
            @RequestParam("filePath") String filePath) {
        Map<String, String> msgMap = new HashMap<String, String>();
        List<String> fileNameList = new ArrayList<String>();
        List<Object> list = new ArrayList<Object>();

        StringBuilder sbFileName = new StringBuilder();
        StringBuilder sbSplFileName = new StringBuilder();

        Long total = 0L; // 데이터 조회건수
        Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수
        Integer maxColCnt = 0;

        final String excelPrefix = "Location";  // 8

        ModelAndView mav = new ModelAndView("jsonView");

        List<Object> result = null;
        try {
            result = locationManager.getAllLocationsForExcel(supplierId);
            maxColCnt = (Integer)result.get(0);
            result.remove(0);
            total = new Integer(result.size()).longValue();
            mav.addObject("total", total);

            if (total <= 0) {
                return mav;
            }

            sbFileName.append(excelPrefix);
            sbFileName.append(TimeUtil.getCurrentTimeMilli());  // 14

            // message 생성
            msgMap.put("location", header);

            // check download dir
            File downDir = new File(filePath);

            if (downDir.exists()) {
                File[] files = downDir.listFiles();

                if (files != null) {
                    String filename = null;
                    String deleteDate;

                    deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -10); // 10일 이전
                                                                                                             // 일자(yyyyMMdd)
                    int prefixLen = excelPrefix.length();
                    List<File> deleteFileList = new ArrayList<File>();

                    for (File file : files) {
                        filename = file.getName();

                        // 파일길이 : 파일명 이상, 확장자 : xls|zip
                        if (filename.length() > (prefixLen + 14) && (filename.endsWith("xls") || filename.endsWith("zip"))) {
                            // 10일 지난 파일 삭제리스트에 추가
                            if (filename.startsWith(excelPrefix)
                                    && filename.substring(prefixLen, (prefixLen + 8)).compareTo(deleteDate) < 0) {
                                deleteFileList.add(file);
                            }
                        }
                        filename = null;
                    }

                    for (File file : deleteFileList) {
                        file.delete();
                    }
                }
            } else {
                // directory 가 없으면 생성
                downDir.mkdir();
            }

            // create excel file
            SupplierMaxLocationMakeExcel wExcel = new SupplierMaxLocationMakeExcel();
            int cnt = 1;
            int idx = 0;
            int fnum = 0;
            int splCnt = 0;

            if (total <= maxRows) {
                sbSplFileName = new StringBuilder();
                sbSplFileName.append(sbFileName);
                sbSplFileName.append(".xls");
                wExcel.writeReportExcel(result, msgMap, filePath, sbSplFileName.toString(), maxColCnt);
                fileNameList.add(sbSplFileName.toString());
            } else {
                for (int i = 0; i < total; i++) {
                    if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
                        sbSplFileName = new StringBuilder();
                        sbSplFileName.append(sbFileName);
                        sbSplFileName.append('(').append(++fnum).append(").xls");

                        list = result.subList(idx, (i + 1));

                        wExcel.writeReportExcel(list, msgMap, filePath, sbSplFileName.toString(), maxColCnt);
                        fileNameList.add(sbSplFileName.toString());
                        list = null;
                        splCnt = cnt;
                        cnt = 0;
                        idx = (i + 1);
                    }
                    cnt++;
                }
            }

            // create zip file
            StringBuilder sbZipFile = new StringBuilder();
            sbZipFile.append(sbFileName).append(".zip");

            ZipUtils zutils = new ZipUtils();
            zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);

            // return object
            mav.addObject("filePath", filePath);
            mav.addObject("fileName", fileNameList.get(0));
            mav.addObject("zipFileName", sbZipFile.toString());
            mav.addObject("fileNames", fileNameList);
        } catch(ParseException e) {
            e.printStackTrace();
            log.error(e.toString(), e);
        } catch(Exception e) {
            e.printStackTrace();
            log.error(e.toString(), e);
        }
        return mav;
    }

	/**
	 * getTypeFrame
	 * 
	 * @param type
	 * @param data
	 * @return DATA필드의 Type 프레임 리턴(Type(1), TypeLength(1), Data(가변))
	 */
	private String getTypeFrame(String type, String data){
		
		if(data.length()<1){
			return "";
		}
		String returnStr 	= "";
		byte[] dataBytes 	= data.getBytes();
		String dataSize		= String.format("%02X", DataUtil.getByteToInt(dataBytes.length));
		
		
		returnStr += type;
		returnStr += dataSize;
		returnStr += Hex.decode(dataBytes);
		
		return returnStr.replaceAll(" ", "");
	}
}