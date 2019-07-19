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

import javax.servlet.http.HttpSession;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Code;
import com.aimir.service.mvm.PowerQualityManager;
import com.aimir.service.system.CodeManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.PowerInstrumentMaxMakeExcel;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.VoltageLevelMaxMakeExcel;
import com.aimir.util.ZipUtils;

@Controller
public class PowerQualityController {

	@Autowired
	CodeManager codeManager;
	@Autowired
	PowerQualityManager powerQualityManager;

	
	
	@RequestMapping(value="/gadget/mvm/powerQualityMiniGadget")
    public ModelAndView powerQualityMiniGadget(HttpSession session) 
	{
		
		//
		ModelAndView mav = new ModelAndView("/gadget/mvm/powerQualityMiniGadget");
		
		String supplierId = (String) session.getAttribute("sesSupplierId");
		mav.addObject("supplierId", supplierId);
		
    	return mav;
    }

	@RequestMapping(value="/gadget/mvm/powerQualityMaxGadget")
    public ModelAndView powerQualityMaxGadget() {
		ModelAndView mav = new ModelAndView("gadget/mvm/powerQualityMaxGadget");
		List<Object> result = powerQualityManager.getTypeViewCombo();
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
		mav.addObject("combo", result);
		return mav;
    }

	// INSERT START SP-204
	@RequestMapping(value="/gadget/mvm/powerQualityMaxGadgetSoria")
    public ModelAndView powerQualityMaxGadgetSoria() {
		ModelAndView mav = new ModelAndView("gadget/mvm/powerQualityMaxGadgetSoria");
		List<Object> result = powerQualityManager.getTypeViewCombo();
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());
		mav.addObject("combo", result);
		return mav;
    }	
	// INSERT END SP-204
	
	@RequestMapping(value="/gadget/mvm/getDeviceType")
	public ModelAndView getDeviceType() {
		ModelAndView mav = new ModelAndView("jsonView");
		List<Code> codes = codeManager.getChildCodes(Code.EQUIPMENT);
		List<Code> device = new ArrayList<Code>();
		for(Code c:codes){
			if(c.getName().equals(CommonConstants.DeviceType.MCU.name())){
				device.add(c);
			}
			else if(c.getName().equals(CommonConstants.DeviceType.Modem.name())){
				device.add(c);
			}
			else if(c.getName().equals(CommonConstants.DeviceType.Meter.name())){
				device.add(c);
			}
			else if(c.getName().equals(CommonConstants.DeviceType.EndDevice.name())){
				device.add(c);
			}
		}
		mav.addObject("deviceType", device);
		return mav;
	}
	
	@RequestMapping(value="/gadget/mvm/getDeviceTypeByComm")
	public ModelAndView getDeviceTypeByComm() {
		ModelAndView mav = new ModelAndView("jsonView");
		List<CommonConstants.DeviceType> deviceTypeList = new ArrayList<CommonConstants.DeviceType>();
		List<Map<String, Object>> returnData = new ArrayList<Map<String, Object>>();
		
		deviceTypeList.add(CommonConstants.DeviceType.EndDevice);
		deviceTypeList.add(CommonConstants.DeviceType.MCU);
		deviceTypeList.add(CommonConstants.DeviceType.Meter);
		deviceTypeList.add(CommonConstants.DeviceType.Modem);
		
		for (DeviceType deviceType : deviceTypeList) {
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("id", deviceType.getCode());
			map.put("name", deviceType.name());
			returnData.add(map);
		}
		
		mav.addObject("deviceType", returnData);
		return mav;
	}
	
	@RequestMapping(value="/gadget/mvm/getPowerQuality")
    public ModelAndView getPowerQuality(
    		@RequestParam("supplierId") String supplierId,
    		@RequestParam("deviation") String deviation,
    		@RequestParam("fromDate") String fromDate,
    		@RequestParam("toDate") String toDate,
    		@RequestParam("dateType") String dateType) {
		
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		
		// supplierId, deviation to integer
		Integer nSupplierId = 0;
		Integer nDeviation = 0;
		if(!"".equals(StringUtil.nullToBlank(supplierId))){
			nSupplierId = Integer.parseInt(supplierId);
		}
		if(!"".equals(StringUtil.nullToBlank(deviation))){
			nDeviation = Integer.parseInt(deviation);
		}

    	conditionMap.put("supplierId", nSupplierId);
    	conditionMap.put("deviation", nDeviation);
    	conditionMap.put("fromDate", fromDate);
    	conditionMap.put("toDate", toDate);
    	conditionMap.put("dateType", dateType);
    	
    	if(fromDate.length() == 0 && toDate.length() == 0) {
    		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    		
    		conditionMap.put("fromDate", df.format(new Date()));
        	conditionMap.put("toDate", df.format(new Date()));
        	conditionMap.put("dateType", "1");
    	}
    	
		ModelAndView mav = new ModelAndView("jsonView");
    	
    	Map<String, Object> result = powerQualityManager.getPowerQuality(conditionMap);
    	mav.addObject("result", result);
    	
    	return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/getPowerDetailList")
    public ModelAndView getPowerDetailList(
    		@RequestParam("supplierId") String supplierId,
    		@RequestParam("deviceType") String deviceType,
    		@RequestParam("deviceId") String deviceId,
    		@RequestParam("deviation") String deviation,
    		@RequestParam("typeView") String typeView,
    		@RequestParam("dateType") String dateType,
    		@RequestParam("page") String page,
    		@RequestParam("fromDate") String fromDate,
    		@RequestParam("toDate") String toDate) {
		
		Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("supplierId", supplierId);
    	conditionMap.put("deviceType", deviceType);
    	conditionMap.put("deviceId", deviceId);
    	conditionMap.put("deviation", deviation);
    	conditionMap.put("typeView", typeView);
    	conditionMap.put("dateType", dateType);
    	conditionMap.put("fromDate", fromDate);
    	conditionMap.put("toDate", toDate);
    	conditionMap.put("page", page);
    	
    	
		ModelAndView mav = new ModelAndView("jsonView");
    	
    	Map<String, Object> result = powerQualityManager.getPowerDetailList(conditionMap);
    	mav.addObject("total", result.get("total"));
    	mav.addObject("grid", result.get("grid"));
    	
    	return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/getPowerQualityList")
    public ModelAndView getPowerQualityList(
    		@RequestParam("supplierId") String supplierId,
    		@RequestParam("tabType") String tabType,
    		@RequestParam("selectType") String selectType,
    		@RequestParam("deviceType") Integer deviceType,
    		@RequestParam("deviation") String deviation,
    		@RequestParam("dateType") String dateType,
    		@RequestParam("page") Integer page,
    		@RequestParam("fromDate") String fromDate,
    		@RequestParam("toDate") String toDate,
    		@RequestParam("equipId") String equipId,
    		@RequestParam("vendorId") Integer vendorId,
    		@RequestParam("modelId") Integer modelId) {
		
		Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("supplierId", supplierId);
    	conditionMap.put("tabType", tabType);
    	conditionMap.put("selectType", selectType);
    	conditionMap.put("deviceType", deviceType);
    	conditionMap.put("deviation", deviation);
    	conditionMap.put("dateType", dateType);
    	conditionMap.put("fromDate", fromDate);
    	conditionMap.put("toDate", toDate);
    	conditionMap.put("equipId", equipId);
    	conditionMap.put("vendorId", vendorId);
    	conditionMap.put("modelId", modelId);
    	conditionMap.put("page", page);
    	conditionMap.put("pageSize", 20);
    	
		ModelAndView mav = new ModelAndView("jsonView");
    	
    	Map<String, Object> result = powerQualityManager.getPowerQualityList(conditionMap);
    	mav.addObject("total", result.get("total"));
    	mav.addObject("grid", result.get("grid"));
    	return mav;
    }
	
	// INSERT START SP-204
	@RequestMapping(value="/gadget/mvm/getPowerQualityListForSoria")
    public ModelAndView getPowerQualityListForSoria(
    		@RequestParam("supplierId") String supplierId,
    		@RequestParam("tabType") String tabType,
    		@RequestParam("selectType") String selectType,
    		@RequestParam("deviceType") Integer deviceType,
    		@RequestParam("deviation") String deviation,
    		@RequestParam("dateType") String dateType,
    		@RequestParam("page") Integer page,
    		@RequestParam("fromDate") String fromDate,
    		@RequestParam("toDate") String toDate,
    		@RequestParam("equipId") String equipId,
    		@RequestParam("vendorId") Integer vendorId,
    		@RequestParam("modelId") Integer modelId) {
		
		Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("supplierId", supplierId);
    	conditionMap.put("tabType", tabType);
    	conditionMap.put("selectType", selectType);
    	conditionMap.put("deviceType", deviceType);
    	conditionMap.put("deviation", deviation);
    	conditionMap.put("dateType", dateType);
    	conditionMap.put("fromDate", fromDate);
    	conditionMap.put("toDate", toDate);
    	conditionMap.put("equipId", equipId);
    	conditionMap.put("vendorId", vendorId);
    	conditionMap.put("modelId", modelId);
    	conditionMap.put("page", page);
    	conditionMap.put("pageSize", 20);
    	
		ModelAndView mav = new ModelAndView("jsonView");
    	
    	Map<String, Object> result = powerQualityManager.getPowerQualityListForSoria(conditionMap);
    	mav.addObject("total", result.get("total"));
    	mav.addObject("grid", result.get("grid"));
    	return mav;
    }
	// INSERT END SP-204
	 	
	@RequestMapping(value="/gadget/mvm/getPowerInstrumentList")
    public ModelAndView getPowerInstrumentList(
    		@RequestParam("supplierId") String supplierId,
    		@RequestParam("tabType") String tabType,
    		@RequestParam("selectType") String selectType,
    		@RequestParam("deviceType") Integer deviceType,
    		@RequestParam("deviation") String deviation,
    		@RequestParam("dateType") String dateType,
    		@RequestParam("fromDate") String fromDate,
    		@RequestParam("page") Integer page,
    		@RequestParam("toDate") String toDate,
    		@RequestParam("equipId") String equipId,
    		@RequestParam("vendorId") Integer vendorId,
    		@RequestParam("modelId") Integer modelId) {
		
		Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("supplierId", supplierId);
    	conditionMap.put("tabType", tabType);
    	conditionMap.put("selectType", selectType);
    	conditionMap.put("deviceType", deviceType);
    	conditionMap.put("deviation", deviation);
    	conditionMap.put("dateType", dateType);
    	conditionMap.put("fromDate", fromDate);
    	conditionMap.put("toDate", toDate);
    	conditionMap.put("equipId", equipId);
    	conditionMap.put("vendorId", vendorId);
    	conditionMap.put("modelId", modelId);
    	conditionMap.put("page", page);
    	conditionMap.put("pageSize", 10);
    	
		ModelAndView mav = new ModelAndView("jsonView");
    	
    	Map<String, Object> result = powerQualityManager.getPowerInstrumentList(conditionMap);
    	mav.addObject("total", result.get("total"));
    	mav.addObject("grid", result.get("grid"));
    	return mav;
    }
	
	@RequestMapping(value="/gadget/mvm/getPowerDetailListAll")
    public ModelAndView getPowerDetailListAll(
    		@RequestParam("supplierId") String supplierId,
    		@RequestParam("deviceType") String deviceType,
    		@RequestParam("deviceId") String deviceId,
    		@RequestParam("deviation") String deviation,
    		@RequestParam("typeView") String typeView,
    		@RequestParam("dateType") String dateType,
    		@RequestParam("fromDate") String fromDate,
    		@RequestParam("toDate") String toDate) {
		
		Map<String, Object> conditionMap = new HashMap<String, Object>();
    	conditionMap.put("supplierId", supplierId);
    	conditionMap.put("deviceType", deviceType);
    	conditionMap.put("deviceId", deviceId);
    	conditionMap.put("deviation", deviation);
    	conditionMap.put("typeView", typeView);
    	conditionMap.put("dateType", dateType);
    	conditionMap.put("fromDate", fromDate);
    	conditionMap.put("toDate", toDate);
    	
    	
		ModelAndView mav = new ModelAndView("jsonView");
    	
    	Map<String, Object> result = powerQualityManager.getPowerDetailList(conditionMap);
    	
    	List<Map<String,Object>> grid = (List<Map<String,Object>>) result.get("grid");
    	for (int i = 0; i < grid.size(); i++) {
			Map<String,Object> map = grid.get(i);
			map.put("decimalA", map.get("decimalA") == null ? StringUtil.nullToZero(map.get("decimalA")) : map.get("decimalA").toString().replaceAll(",", ""));
			map.put("decimalB", map.get("decimalA") == null ? StringUtil.nullToZero(map.get("decimalA")) : map.get("decimalA").toString().replaceAll(",", ""));
			map.put("decimalC", map.get("decimalA") == null ? StringUtil.nullToZero(map.get("decimalA")) : map.get("decimalA").toString().replaceAll(",", ""));
			map.put("decimalD", map.get("decimalA") == null ? StringUtil.nullToZero(map.get("decimalA")) : map.get("decimalA").toString().replaceAll(",", ""));
		}
    	
    	mav.addObject("result", result);
    	
    	return mav;
    }
	
    // 엑셀로 저장하는 페이지
	@RequestMapping(value = "/gadget/mvm/powerQualityexcelDownloadPopup")
	public ModelAndView powerQualityexcelDownloadPopup() {
		ModelAndView mav = new ModelAndView(
				"/gadget/mvm/powerQualityexcelDownloadPopup");
		return mav;
	}
	
	 //operationlog리스트를 엑셀로 저장
  	@RequestMapping(value = "/gadget/mvm/voltageLevelMaxExcelMake")
  	public ModelAndView voltageLevelMaxExcelMake(
  			@RequestParam("condition[]") 	String[] condition,
  			@RequestParam("fmtMessage[]") 	String[] fmtMessage,
  			@RequestParam("isExcel") 		Boolean isExcel,
  			@RequestParam("filePath") 		String filePath) {

  		Map<String, String> msgMap = new HashMap<String, String>();
  		List<String> fileNameList = new ArrayList<String>();
  		List<Object> list = new ArrayList<Object>();

  		StringBuilder sbFileName = new StringBuilder();
  		StringBuilder sbSplFileName = new StringBuilder();

  		boolean isLast = false;
  		Long total = 0L; // 데이터 조회건수
  		Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

  		final String logPrefix = "voltageLevelList";//9

  		ModelAndView mav = new ModelAndView("jsonView");

  		List<Object> result = null;
  		try {
  			Map<String, Object> conditionMap = new HashMap<String, Object>();
  			
  				conditionMap.put("supplierId"   , condition[0]);
  				conditionMap.put("tabType"      , condition[1]);
  				conditionMap.put("selectType"   , condition[2]);                          
  				conditionMap.put("deviceType"   , Integer.parseInt(condition[3]));
  				conditionMap.put("deviation"	, condition[4]);
  				conditionMap.put("dateType"     , condition[5]);                           
  				conditionMap.put("fromDate"     , condition[6]);
  				conditionMap.put("toDate"       , condition[7]);
  				conditionMap.put("equipId"      , condition[8]);
  				conditionMap.put("vendorId"     , Integer.parseInt(condition[9].toString()));
  				conditionMap.put("modelId"      , Integer.parseInt(condition[10].toString()));
  				conditionMap.put("isExcel"      , isExcel);
  				conditionMap.put("page"         , 0);    
  	            

 			Map<String, Object> resultMap = powerQualityManager.getPowerQualityList(conditionMap);
 
  			result = (List<Object>) resultMap.get("grid");

  			total = new Integer(result.size()).longValue();

  			mav.addObject("total", total);
  			if (total <= 0) {
  				return mav;
  			}

  			sbFileName.append(logPrefix);

  			sbFileName.append(TimeUtil.getCurrentTimeMilli());//14

  			// message 생성
  			msgMap.put("yyyymmdd"	 	, 	 fmtMessage[0]);
  			msgMap.put("deviceType"	 	, 	 fmtMessage[1]);
  			msgMap.put("deviceId"  		,    fmtMessage[2]);
  			msgMap.put("customerName"  	, 	 fmtMessage[3]);			
  			msgMap.put("volA_min"		,    fmtMessage[7]);
  			msgMap.put("volA_max"		, 	 fmtMessage[8]);
  			msgMap.put("volA_avg"   	,    fmtMessage[9]);
  			msgMap.put("vol_angleA_min" ,    fmtMessage[7]);
  			msgMap.put("vol_angleA_max" , 	 fmtMessage[8]);
  			msgMap.put("vol_angleA_avg" , 	 fmtMessage[9]);
  			msgMap.put("volB_min"		,    fmtMessage[7]);
  			msgMap.put("volB_max"		, 	 fmtMessage[8]);
  			msgMap.put("volB_avg"   	,    fmtMessage[9]);
  			msgMap.put("vol_angleB_min" ,    fmtMessage[7]);
  			msgMap.put("vol_angleB_max" , 	 fmtMessage[8]);
  			msgMap.put("vol_angleB_avg" , 	 fmtMessage[9]);
  			msgMap.put("volC_min"		,    fmtMessage[7]);
  			msgMap.put("volC_max"		, 	 fmtMessage[8]);
  			msgMap.put("volC_avg"   	,    fmtMessage[9]);
  			msgMap.put("vol_angleC_min" ,    fmtMessage[7]);
  			msgMap.put("vol_angleC_max" , 	 fmtMessage[8]);
  			msgMap.put("vol_angleC_avg" , 	 fmtMessage[9]);
  			msgMap.put("headerA1" 		,    fmtMessage[4]);
  			msgMap.put("headerA2" 		, 	 fmtMessage[10]);
  			msgMap.put("headerB1" 		, 	 fmtMessage[5]);
  			msgMap.put("headerB2" 		,    fmtMessage[11]);
  			msgMap.put("headerC1" 		, 	 fmtMessage[6]);
  			msgMap.put("headerC2" 		, 	 fmtMessage[12]);


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
  			VoltageLevelMaxMakeExcel wExcel = new VoltageLevelMaxMakeExcel();
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
  	
  	 //operationlog리스트를 엑셀로 저장
  	@RequestMapping(value = "/gadget/mvm/powerInstrumentMaxExcelMake")
  	public ModelAndView powerInstrumentMaxExcelMake(
  			@RequestParam("condition[]") 	String[] condition,
  			@RequestParam("fmtMessage[]") 	String[] fmtMessage,
  			@RequestParam("isExcel") 		Boolean isExcel,
  			@RequestParam("filePath") 		String filePath) {

  		Map<String, String> msgMap = new HashMap<String, String>();
  		List<String> fileNameList = new ArrayList<String>();
  		List<Object> list = new ArrayList<Object>();

  		StringBuilder sbFileName = new StringBuilder();
  		StringBuilder sbSplFileName = new StringBuilder();

  		boolean isLast = false;
  		Long total = 0L; // 데이터 조회건수
  		Long maxRows = 5000L; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

  		final String logPrefix = "powerInstrumentlList";//9

  		ModelAndView mav = new ModelAndView("jsonView");

  		List<Object> result = null;
  		try {
  			Map<String, Object> conditionMap = new HashMap<String, Object>();
  			
  				conditionMap.put("supplierId"   , condition[0]);
  				conditionMap.put("tabType"      , condition[1]);
  				conditionMap.put("selectType"   , condition[2]);                          
  				conditionMap.put("deviceType"   , Integer.parseInt(condition[3]));
  				conditionMap.put("deviation"	, condition[4]);
  				conditionMap.put("dateType"     , condition[5]);                           
  				conditionMap.put("fromDate"     , condition[6]);
  				conditionMap.put("toDate"       , condition[7]);
  				conditionMap.put("equipId"      , condition[8]);
  				conditionMap.put("vendorId"     , Integer.parseInt(condition[9].toString()));
  				conditionMap.put("modelId"      , Integer.parseInt(condition[10].toString()));
  				conditionMap.put("isExcel"      , isExcel);
  				conditionMap.put("page"		    , 0);    
  	            

 			Map<String, Object> resultMap = powerQualityManager.getPowerInstrumentList(conditionMap);
 
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
  			msgMap.put("deviceType"	 	, 	 fmtMessage[1]);
  			msgMap.put("deviceId"  		,    fmtMessage[2]);
  			msgMap.put("lastReadDate"  	, 	 fmtMessage[3]);			
  			msgMap.put("contractId"		,    fmtMessage[4]);
  			msgMap.put("customerName"	, 	 fmtMessage[5]);
  			msgMap.put("A"   			,    "A");
  			msgMap.put("B" 				,    "B");
  			msgMap.put("C" 				, 	 "C");
  			msgMap.put("AB" 			, 	 "AB");
  			msgMap.put("CA"				,    "CA");
  			msgMap.put("BC"				, 	 "BC");
  			msgMap.put("voltage"   		,    fmtMessage[6]);
  			msgMap.put("current" 		,    fmtMessage[7]);
  			msgMap.put("linevoltage" 	, 	 fmtMessage[8]);
   			
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
  			PowerInstrumentMaxMakeExcel wExcel = new PowerInstrumentMaxMakeExcel();
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
