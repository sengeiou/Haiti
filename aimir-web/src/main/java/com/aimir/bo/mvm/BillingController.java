package com.aimir.bo.mvm;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.TariffType;
import com.aimir.service.mvm.BillingManager;
import com.aimir.service.system.LocationManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.TariffTypeManager;
import com.aimir.util.BillingMaxMakeExcel;
import com.aimir.util.CalendarUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

@Controller
public class BillingController {

	@Autowired
	TariffTypeManager tariffTypeManager;
	
	@Autowired
	LocationManager locationManager;
	
	@Autowired
	BillingManager billingManager;
	
	@Autowired
	SupplierManager supplierManager;
	
    @RequestMapping(value="/gadget/mvm/elecBillingMiniGadget")    
    public ModelAndView getElecBillingMiniGadget() {
    	return new ModelAndView("/gadget/mvm/elecBillingMiniGadget");
	}
    
    
    
    @RequestMapping(value="/gadget/mvm/gasBillingMiniGadget")    
    public ModelAndView getGasBingMiniGadget() {
    	return new ModelAndView("/gadget/mvm/gasBillingMiniGadget");
	}
    
    @RequestMapping(value="/gadget/mvm/waterBillingMiniGadget")    
    public ModelAndView getWaterBillingMiniGadget() {
    	return new ModelAndView("/gadget/mvm/waterBillingMiniGadget");
	}
    
    @RequestMapping(value="/gadget/mvm/heatBillingMiniGadget")    
    public ModelAndView getHeatBillingMiniGadget() {
    	return new ModelAndView("/gadget/mvm/heatBillingMiniGadget");
	}    
    
    @RequestMapping(value="/gadget/mvm/elecBillingMaxGadget")    
    public ModelAndView getElecBillingMaxGadget() {
    	
    	ModelAndView mav = new ModelAndView("gadget/mvm/elecBillingMaxGadget");
		
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();        

        List<TariffType> tariffTypes = tariffTypeManager.getTariffTypeBySupplier(MeterType.EnergyMeter.getServiceType(), user.getSupplier().getId());
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
        mav.addObject("tariffTypes",tariffTypes);    	
    	return mav;
	}   
    
    @RequestMapping(value="/gadget/mvm/gasBillingMaxGadget")    
    public ModelAndView getGasBillingMaxGadget() {
    	
    	ModelAndView mav = new ModelAndView("gadget/mvm/gasBillingMaxGadget");
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();        
        
        List<TariffType> tariffTypes = tariffTypeManager.getTariffTypeBySupplier(MeterType.GasMeter.getServiceType(), user.getSupplier().getId());
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId()); 
        mav.addObject("tariffTypes",tariffTypes);    	
    	return mav;
	}
    
    @RequestMapping(value="/gadget/mvm/waterBillingMaxGadget")    
    public ModelAndView getWaterBillingMaxGadget() {
    	
    	ModelAndView mav = new ModelAndView("gadget/mvm/waterBillingMaxGadget");
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();        
        
        List<TariffType> tariffTypes = tariffTypeManager.getTariffTypeBySupplier(MeterType.WaterMeter.getServiceType(), user.getSupplier().getId());
    	    	
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId()); 
        mav.addObject("tariffTypes",tariffTypes);    	
    	return mav;
	}
    
    @RequestMapping(value="/gadget/mvm/heatBillingMaxGadget")    
    public ModelAndView getHeatBillingMaxGadget() {
    	
    	ModelAndView mav = new ModelAndView("gadget/mvm/heatBillingMaxGadget");
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();        
        
        List<TariffType> tariffTypes = tariffTypeManager.getTariffTypeBySupplier(MeterType.HeatMeter.getServiceType(), user.getSupplier().getId());
    	    	
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId()); 
        mav.addObject("tariffTypes",tariffTypes);    	
    	return mav;
	}    
    
    @RequestMapping(value="/gadget/mvm/getBillingChartData")    
    public ModelAndView getElecBillingChartData(
    		
		@RequestParam("startDate") String startDate,
		@RequestParam("endDate") String endDate,
		@RequestParam("chartType") String chartType,
		@RequestParam("searchDateType") String searchDateType,    		
		@RequestParam("locationIds") String locationIds,
		@RequestParam("serviceType") String serviceType) {
    	
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        String supplierId = user.getRoleData().getSupplier().getId() + "";
        
    	Map<String, String> conditionMap = new HashMap<String, String>();
    	conditionMap.put("startDate", startDate);
    	conditionMap.put("endDate", endDate);
    	conditionMap.put("chartType", chartType);
    	conditionMap.put("searchDateType", searchDateType);    	
    	conditionMap.put("locationIds", locationIds);
    	conditionMap.put("supplierId", supplierId);
    	conditionMap.put("serviceType", serviceType);
    	
    	return new ModelAndView("jsonView", new ModelMap("chartDatas", billingManager.getElecBillingChartData(conditionMap)));
    }
    
    
    /**
     * 
     * @DESC: PIE CHART DATA FETCH ACTION AT billling data full gadget 
     * @param startDate
     * @param endDate
     * @param chartType
     * @param searchDateType
     * @param locationIds
     * @param serviceType
     * @return
     */
    @RequestMapping(value="/gadget/mvm/getBillingFusionChartData")    
    public ModelAndView getElecBillingFusionChartData(    		
		@RequestParam("startDate") String startDate,
		@RequestParam("endDate") String endDate,
		@RequestParam("chartType") String chartType,
		@RequestParam("searchDateType") String searchDateType,    		
		@RequestParam("locationIds") String locationIds,
		@RequestParam("serviceType") String serviceType) {
    	
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession();
        String supplierId = user.getRoleData().getSupplier().getId() + "";
        
    	Map<String, String> conditionMap = new HashMap<String, String>();
    	conditionMap.put("startDate", startDate);
    	conditionMap.put("endDate", endDate);
    	conditionMap.put("chartType", chartType);
    	conditionMap.put("searchDateType", searchDateType);    	
    	conditionMap.put("locationIds", locationIds);
    	conditionMap.put("supplierId", supplierId);
    	conditionMap.put("serviceType", serviceType);
    	
    	if(startDate.length() == 0 && endDate.length() == 0) {
    		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    		
    		conditionMap.put("startDate", df.format(new Date()));
        	conditionMap.put("endDate", df.format(new Date()));
        	conditionMap.put("searchDateType", "1");
    	}
    	
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	
    	
    	//fusion PIE chart data datA fetch 
    	List<Map<String, String>> elecbillingchartdata = billingManager.getElecBillingChartData(conditionMap);    	
    	
    	
    	mav.addObject("gridDatas", elecbillingchartdata);
    	
    	return mav;
    }
    
    /**
     * @DESC : 지역별 전기 사용량 빌링 그리드 DATA FETCH ACTION
     * @param startDate
     * @param endDate
     * @param searchDateType
     * @param locationIds
     * @param serviceType
     * @return
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping(value="/gadget/mvm/getLocationBillingGridData")    
    public ModelAndView getElecLocationBillingGridData(
    	
		@RequestParam("startDate") String startDate,
		@RequestParam("endDate") String endDate,
		@RequestParam("searchDateType") String searchDateType,    
		
		//로케이션 id
		@RequestParam("locationIds") String locationIds,
		@RequestParam("serviceType") String serviceType) 
    
    	{
	
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
	    AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
	    AimirUser user = (AimirUser)instance.getUserFromSession();
	    String supplierId = user.getRoleData().getSupplier().getId() + "";
	    
	    String isAnonymous = "true";
	    if(user != null && !user.isAnonymous()) 
	    	isAnonymous = "false";
	    	    
		Map<String, String> conditionMap = new HashMap<String, String>();
		conditionMap.put("startDate", startDate);
		conditionMap.put("endDate", endDate);
		conditionMap.put("searchDateType", searchDateType);    	
		conditionMap.put("locationIds", locationIds);
		conditionMap.put("supplierId", supplierId);
		conditionMap.put("isAnonymous", isAnonymous);
		conditionMap.put("serviceType", serviceType);
		
		if(startDate.length() == 0 && endDate.length() == 0) {
    		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    		
    		conditionMap.put("startDate", df.format(new Date()));
        	conditionMap.put("endDate", df.format(new Date()));
        	conditionMap.put("searchDateType", "1");
    	}
		
		ModelMap modelMap = new ModelMap();
		
		//flex Grid data fetch and set		
		List eleclocationbillinggriddata= billingManager.getElecLocationBillingGridData(conditionMap);
		

		modelMap.addAttribute("gridDatas",eleclocationbillinggriddata );
		return new ModelAndView("jsonView", modelMap);
		
		
    }    

    @RequestMapping(value="/gadget/mvm/getCustomerBillingGridData")    
    public ModelAndView getElecCustomerBillingGridData(
    	
		@RequestParam("startDate") String startDate,
		@RequestParam("endDate") String endDate,
		@RequestParam("searchDateType") String searchDateType,   
		@RequestParam("locationIds") String locationIds,    		
		@RequestParam("tariffIndex") String tariffIndex,
		@RequestParam("customerName") String customerName,
		@RequestParam("contractNo") String contractNo,
		@RequestParam("meterName") String meterName,
		@RequestParam("supplierId") String supplierId,
		@RequestParam("page") String page,
		@RequestParam("pageSize") String pageSize,
		@RequestParam("serviceType") String serviceType) {

		Map<String, Object> conditionMap = new HashMap<String, Object>();
		conditionMap.put("startDate", startDate);
		conditionMap.put("endDate", endDate);
		conditionMap.put("searchDateType", searchDateType);    	
		conditionMap.put("locationIds", locationIds);
		conditionMap.put("tariffIndex", tariffIndex);
		conditionMap.put("customerName", customerName);
		conditionMap.put("contractNo", contractNo);
		conditionMap.put("meterName", meterName);
		conditionMap.put("page", page);
		conditionMap.put("pageSize", pageSize);
		conditionMap.put("serviceType", serviceType);
		conditionMap.put("supplierId", supplierId);
				
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute("gridDatas", billingManager.getCustomerBillingGridData(conditionMap));
		modelMap.addAttribute("total", billingManager.getElecCustomerBillingGridDataCount(conditionMap));
		
		return new ModelAndView("jsonView", modelMap);
    }  
    
	 //operationlog리스트를 엑셀로 저장
  	@RequestMapping(value = "/gadget/mvm/billingMaxExcelMake")
  	public ModelAndView voltageLevelMaxExcelMake(
  			@RequestParam("condition[]") 	String[] condition,
  			@RequestParam("fmtMessage[]") 	String[] fmtMessage,
  			@RequestParam("filePath") 		String filePath) {

  		Map<String, String> msgMap = new HashMap<String, String>();
  		List<String> fileNameList = new ArrayList<String>();
  		List<Object> list = new ArrayList<Object>();

  		StringBuilder sbFileName = new StringBuilder();
  		StringBuilder sbSplFileName = new StringBuilder();

  		boolean isLast = false;
  		Long total = 0L; // 데이터 조회건수
  		Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

  		final String logPrefix = "billingList";//9

  		ModelAndView mav = new ModelAndView("jsonView");

  		List<Object> result = null;
  		try {
  			Map<String, Object> conditionMap = new HashMap<String, Object>();
  			
  				conditionMap.put("startDate"    	, condition[0]);
  				conditionMap.put("endDate"   		, condition[1]);
  				conditionMap.put("searchDateType"   , condition[2]);                          
  				conditionMap.put("locationIds" 		, condition[3]);
  				conditionMap.put("tariffIndex"		, condition[4]);
  				conditionMap.put("customerName"     , condition[5]);                           
  				conditionMap.put("contractNo"     	, condition[6]);
  				conditionMap.put("meterName"     	, condition[7]);
  				conditionMap.put("serviceType"      , condition[8]);
  				conditionMap.put("supplierId"       , condition[9]);
  				conditionMap.put("page"         	, "0");
  				conditionMap.put("pageSize"     	, "10000");

 			List resultMap = billingManager.getCustomerBillingGridData(conditionMap);
 
  			result = resultMap;

  			total = new Integer(result.size()).longValue();

  			mav.addObject("total", total);
  			if (total <= 0) {
  				return mav;
  			}

  			sbFileName.append(logPrefix);

  			sbFileName.append(TimeUtil.getCurrentTimeMilli());//14

  			// message 생성
  			msgMap.put("rownum"	 			, 	 fmtMessage[0]);
  			msgMap.put("yyyymmdd"	 		, 	 fmtMessage[1]);
  			msgMap.put("customerName"  		,    fmtMessage[2]);
  			msgMap.put("contractNo"  		, 	 fmtMessage[3]);			
  			msgMap.put("meterName"			,    fmtMessage[4]);
  			msgMap.put("total"				, 	 fmtMessage[5]);
  			msgMap.put("usage"   			,    fmtMessage[6]);
  			msgMap.put("max"   				,    fmtMessage[7]);
  			msgMap.put("usageCharge"   		,    fmtMessage[8]);
  			msgMap.put("title"   			,    fmtMessage[9]);
  			
  			// check download dir
  			File downDir = new File(filePath);

  			if (downDir.exists()) {
  				File[] files = downDir.listFiles();

  				if (files != null) {
  					String filename = null;
  					String deleteDate;

  					deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(),
  							Calendar.DAY_OF_MONTH, -10); // 10일 이전 일자
  					boolean isDel = false;

  					for (File file : files) {

  						filename = file.getName();
  						isDel = false;

  						// 파일길이 : 26이상, 확장자 : xls|zip
  						if (filename.length() > 29
  								&& (filename.endsWith("xls") || filename
  										.endsWith("zip"))) {
  							// 10일 지난 파일들 삭제
  							if (filename.startsWith(logPrefix)
  									&& filename.substring(9, 17).compareTo(
  											deleteDate) < 0) {
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
  			BillingMaxMakeExcel wExcel = new BillingMaxMakeExcel();
  			int cnt = 1;
  			int idx = 0;
  			int fnum = 0;
  			int splCnt = 0;

  			if (total <= maxRows) {
  				sbSplFileName = new StringBuilder();
  				sbSplFileName.append(sbFileName);
  				sbSplFileName.append(".xls");
  				wExcel.writeReportExcel(result, msgMap, isLast, filePath,
  						sbSplFileName.toString());
  				fileNameList.add(sbSplFileName.toString());
  			} else {
  				for (int i = 0; i < total; i++) {
  					if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
  						sbSplFileName = new StringBuilder();
  						sbSplFileName.append(sbFileName);
  						sbSplFileName.append('(').append(++fnum)
  								.append(").xls");

  						list = result.subList(idx, (i + 1));

  						wExcel.writeReportExcel(list, msgMap, isLast, filePath,
  								sbSplFileName.toString());
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

  		} catch (ParseException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		} catch (Exception e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  		return mav;
  	}
}
