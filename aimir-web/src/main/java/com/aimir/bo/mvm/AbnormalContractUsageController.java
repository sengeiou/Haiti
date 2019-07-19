package com.aimir.bo.mvm;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.service.mvm.AbnormalContractUsageManager;
import com.aimir.service.system.LocationManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.TariffTypeManager;
import com.aimir.util.AbnormalContractUsageMaxMakeExcel;
import com.aimir.util.CalendarUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.VoltageLevelMaxMakeExcel;
import com.aimir.util.ZipUtils;

@Controller
public class AbnormalContractUsageController {

	@Autowired
	AbnormalContractUsageManager abnormalContractUsageManager;
	
	@Autowired
	SupplierManager supplierManager;
	
	@Autowired
	TariffTypeManager tariffTypeManager;
	
	@Autowired
	LocationManager locationManager;
    
	@RequestMapping(value="/gadget/mvm/abnormalContractUsageEmMaxGadget")
    public ModelAndView abnormalContractUsageEmMaxGadget() {
		ModelAndView mav = new ModelAndView("gadget/mvm/abnormalContractUsageEmMaxGadget");
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
    	return mav;
    }

	@RequestMapping(value="/gadget/mvm/abnormalContractUsageEmMiniGadget")
    public ModelAndView abnormalContractUsageEmMiniGadget() {
    	
		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
        AimirUser user = (AimirUser)instance.getUserFromSession(); 
        
		ModelAndView mav = new ModelAndView("gadget/mvm/abnormalContractUsageEmMiniGadget");
		mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
		mav.addObject("tariffTypes", tariffTypeManager.getTariffTypeBySupplier(MeterType.EnergyMeter.getServiceType(), user.getSupplier().getId()));
		return mav;
    }
    
	@RequestMapping(value="/gadget/mvm/getSuppliers")
	public ModelAndView getSuppliers() {
		ModelAndView mav = new ModelAndView("jsonView");
    	mav.addObject("supplierList", supplierManager.getSuppliers());
    	return mav;
	}
	
	@RequestMapping(value="/gadget/mvm/getTariffTypes")
	public ModelAndView getTariffTypes(@RequestParam("serviceType") String serviceType 
									  ,@RequestParam("supplierId") String strSupplierId ) {
		ModelAndView mav = new ModelAndView("jsonView");
		Integer supplierId = null;
		if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
			supplierId = Integer.parseInt(strSupplierId);
		}
		mav.addObject("tariffTypes", tariffTypeManager.getTariffTypeBySupplier(serviceType, supplierId));
		return mav;
	}
	
	@RequestMapping(value="/gadget/mvm/getLocations")
	public ModelAndView getLocations(@RequestParam("supplierId") String strSupplierId) {
		ModelAndView mav = new ModelAndView("jsonView");
		//Supplier supplier = supplierManager.getSupplier(supplierId);
		//mav.addObject("locations", supplier.getLocations());
		
		Integer supplierId = null;
		if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
			supplierId = Integer.parseInt(strSupplierId);
			mav.addObject("locations", locationManager.getLocationsBySupplierId(supplierId));
		}
		else{
			mav.addObject("locations", locationManager.getLocations());
		}
		return mav;
	}
	
    @RequestMapping(value="/gadget/mvm/getAbnormalContractUsageEM")
    public ModelAndView getAbnormalContractUsageEM( @RequestParam("supplierId") String strSupplierId
    		                                       ,@RequestParam("tariffType") int tariffType
    		                                       ,@RequestParam("yyyymmdd") String yyyymmdd) {
        ModelAndView mav = new ModelAndView("jsonView");

		Integer supplierId = 0;
		if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
			supplierId = Integer.parseInt(strSupplierId);
		}
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplierId", supplierId);
        condition.put("yyyymmdd", yyyymmdd);
        condition.put("tariffType", tariffType); 
       

        Map<String, Object> result = abnormalContractUsageManager.getAbnormalContractUsageEM(condition);
        mav.addObject("total", result.get("total"));
        mav.addObject("grid", result.get("grid"));

        return mav;
    }
	
    @RequestMapping(value="/gadget/mvm/getAbnormalContractUsageEMList")
    public ModelAndView getAbnormalContractUsageEMList( @RequestParam("supplierId") String strSupplierId
	    		                                       ,@RequestParam("locationId") Integer locationId
	    		                                       ,@RequestParam("tariffType") Integer tariffType
	    		                                       ,@RequestParam("customerName") String customerName
	    		                                       ,@RequestParam("contractNo") String contractNo
	    		                                       ,@RequestParam("wattage") String wattage
	    		                                       ,@RequestParam("dateType") String dateType
	    		                                       ,@RequestParam("fromDate") String fromDate
	    		                                       ,@RequestParam("toDate") String toDate 
	    		                                       ,@RequestParam("page") Integer page
	    		                                       ,@RequestParam("pageSize") Integer pageSize) {
        ModelAndView mav = new ModelAndView("jsonView");

       
//        Integer supplierId = strSupplierId;
        Integer supplierId = 0;
		if(!"".equals(StringUtil.nullToBlank(strSupplierId))){
			supplierId = Integer.parseInt(strSupplierId);
		}	
        Map<String, Object> condition = new HashMap<String, Object>();
        condition.put("supplierId", supplierId);
        condition.put("locationId", locationId);
        condition.put("tariffType", tariffType);
        
        try {
			condition.put("customerName", URLDecoder.decode(customerName,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        condition.put("contractNo", contractNo);
        condition.put("dateType", dateType);
        condition.put("fromDate", fromDate);
        condition.put("toDate", toDate);
        condition.put("wattage", wattage);
        condition.put("page", page);
        condition.put("pageSize", pageSize);
        
        Map<String, Object> result = abnormalContractUsageManager.getAbnormalContractUsageEMList(condition);
        
        mav.addObject("total", result.get("total"));
        mav.addObject("grid", result.get("grid"));

        return mav;
    }
    
	 //operationlog리스트를 엑셀로 저장
  	@RequestMapping(value = "/gadget/mvm/abnormalContractUsageMaxExcelMake")
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

  		final String logPrefix = "abnormalContractUsageList";//9

  		ModelAndView mav = new ModelAndView("jsonView");

  		List<Object> result = null;
  		try {
  			Map<String, Object> conditionMap = new HashMap<String, Object>();
  			
  				conditionMap.put("supplierId"   , Integer.parseInt(condition[0]));
  				conditionMap.put("locationId"   , Integer.parseInt(condition[1]));
  				conditionMap.put("tariffType"   , Integer.parseInt(condition[2]));                          
  				conditionMap.put("customerName" , condition[3]);
  				conditionMap.put("contractNo"	, condition[4]);
  				conditionMap.put("wattage"      , condition[5]);                           
  				conditionMap.put("dateType"     , condition[6]);
  				conditionMap.put("fromDate"     , condition[7]);
  				conditionMap.put("toDate"       , condition[8]);
  				conditionMap.put("page"         , 0);
  				conditionMap.put("pageSize"     , 10000);

 			Map<String, Object> resultMap = abnormalContractUsageManager.getAbnormalContractUsageEMList(conditionMap);
 
  			result = (List<Object>) resultMap.get("grid");

  			total = new Integer(result.size()).longValue();

  			mav.addObject("total", total);
  			if (total <= 0) {
  				return mav;
  			}

  			sbFileName.append(logPrefix);

  			sbFileName.append(TimeUtil.getCurrentTimeMilli());//14

  			// message 생성
  			msgMap.put("no"	 			, 	 fmtMessage[0]);
  			msgMap.put("yyyymmdd"	 	, 	 fmtMessage[1]);
  			msgMap.put("customerName"  	,    fmtMessage[2]);
  			msgMap.put("contractNo"  	, 	 fmtMessage[3]);			
  			msgMap.put("tariffName"		,    fmtMessage[4]);
  			msgMap.put("contractUsage"	, 	 fmtMessage[5]);
  			msgMap.put("demandUsage"   	,    fmtMessage[6]);
  			msgMap.put("title"   		,    fmtMessage[11]);
  			
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
  			AbnormalContractUsageMaxMakeExcel wExcel = new AbnormalContractUsageMaxMakeExcel();
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
