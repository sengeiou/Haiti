package com.aimir.bo.device.firmware;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.common.CommandProperty;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.FirmwareDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.DeviceVendorDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.fep.util.CRCUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Firmware;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.FirmWareManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Controller
public class FirmwareOperatorController {
	private Logger logger = Logger.getLogger(FirmwareOperatorController.class);

	@Autowired
	FirmwareDao firmwareDao;
	
	@Autowired
	DeviceVendorDao deviceVendorDao;
	
	@Autowired
	DeviceModelDao deviceModelDao;
	
	@Autowired
	CodeManager codeManager;
	
	@Autowired
	FirmWareManager firmwareManager;
	
	@Autowired
	SupplierManager supplierManager;
	
	@Autowired 
	LocationDao locationDao;
	
	// 임시 파일 정보 저장을 위한 변수
	private String finalFilePath_ = null;
	private byte[] fileBinary_ = null;
	private String[] filePath_ = null;
	private String fileName_ = null;
	private String ext_ = null;
	private String checkSum_ = null;
	private Object crc_ = null;
	private String fwVersion;
	
	/**
     * Mini Gadget
     */
    @RequestMapping(value="/gadget/device/firmware/firmwareMiniGadget")
    public ModelAndView loadfirmwareMiniGadget() {
        return new ModelAndView("/gadget/device/firmware/firmwareMiniGadget");
    }

    /**
     * Max Gadget
     */
    @RequestMapping(value="/gadget/device/firmware/firmwareMaxGadget")
    public ModelAndView loadfirmwareMaxGadget() {
        ModelAndView mav = new ModelAndView("/gadget/device/firmware/firmwareMaxGadget");
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        int supplierId = user.getRoleData().getSupplier().getId();
    

        mav.addObject("supplierId", supplierId);
        return mav;
    }
    
    /**
     * Max Gadget --> Item Add Popup
     */
    @RequestMapping(value="/gadget/device/firmware/firmwareAddPopup")
    public ModelAndView loadfirmwareAddPopup() {
    	ModelAndView mav = new ModelAndView("/gadget/device/firmware/firmwareAddPopup");
    	//파라미터는 win.obj를 통해 전달    	
    	return mav;
    }
    
    @RequestMapping(value="/gadget/device/firmware/deviceListPopup")
    public ModelAndView deviceListPopup() {
    	ModelAndView mav = new ModelAndView("/gadget/device/firmware/deviceListPopup");
    	//파라미터는 win.obj를 통해 전달    	
    	return mav;
    }

    @RequestMapping(value="/gadget/device/firmware/deviceListPopup2")
    public ModelAndView deviceListPopup2() {
    	ModelAndView mav = new ModelAndView("/gadget/device/firmware/deviceListPopup2");
    	//파라미터는 win.obj를 통해 전달    	
    	return mav;
    }
    
    @RequestMapping(value="/gadget/device/firmware/getFirmwareFileList")
	public ModelAndView getFirmwareFileList(
			@RequestParam(value = "supplierId") String supplierId,
			@RequestParam(value = "equip_kind") String equip_kind,
			@RequestParam(value = "fileName") String fileName,
			@RequestParam(value = "modelName") String modelName,
			@RequestParam(value = "modelId") String modelId,
			@RequestParam(value = "fwVer") String fwVer
			) throws Exception {
    	
    	ModelAndView mav = new ModelAndView("jsonView");
    	List<Object> result	= new ArrayList<Object>();
        Map<String,Object> condition = new HashMap<String,Object>();
        
        Supplier supplier = supplierManager.getSupplier(Integer.parseInt(supplierId));
        String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
    	condition.put("supplierId", supplierId);
        condition.put("equip_kind", equip_kind);
        condition.put("fileName", fileName);
        condition.put("modelName", modelName);
        condition.put("modelId", modelId);
        condition.put("fwVer", fwVer);
        
        List<Object> resultList = firmwareManager.getFirmwareFileList(condition);
         
		int dataListLen = 0;
		if (resultList != null) {
			dataListLen = resultList.size();
		}

		for (int i = 0; i < dataListLen; i++) {
			HashMap returnResult = new HashMap();
			Object[] resultData = (Object[]) resultList.get(i);

			/* 참고 : resultData[]
			 * FirmwareDaoImpl.java - getFirmwareFileList */
			returnResult.put("no", i+1);
			returnResult.put("modelname", resultData[0]);
			returnResult.put("firmwareId", resultData[1]);
			returnResult.put("creator", resultData[5]);
			returnResult.put("hwver", resultData[7]);
			returnResult.put("fwver", resultData[8]);
			returnResult.put("fwrev", resultData[9]);
			returnResult.put("creationdate", TimeLocaleUtil.getLocaleDate(resultData[10].toString(), lang, country));
			returnResult.put("filename", resultData[11]);
			returnResult.put("modelId", resultData[12]);
			returnResult.put("checkSum", resultData[13]);
			returnResult.put("crc", resultData[14]);
			returnResult.put("imageKey", resultData[15]);
			returnResult.put("filePath", resultData[16]);
			returnResult.put("fileUrlPath", resultData[17]);
			returnResult.put("fId", resultData[18]);
			
			// 서버 파일 체크 - Java NIO.2 적용
			String file_path = (String) resultData[16];
			Path path = Paths.get(file_path);
			
			boolean pathExists = Files.exists(path, new LinkOption[]{ LinkOption.NOFOLLOW_LINKS});
        	
			if(pathExists) {
				returnResult.put("fileExists", "Normal");		// OTA 사용 가능
			} else {
				// DB에는 파일 경로가 저장되어 있는데, Server에는 해당 파일이 존재하지 않을 경우
				// DB에 저장되어있는 경로 정보를 삭제한다.
//				Map<String,Object> conditionMap = new HashMap<String,Object>();
//				String command = "updateFileStatus";
//				String firmwareId = (String) resultData[1];
//				
//				conditionMap.put("firmwareId", firmwareId);
//				conditionMap.put("command", command);
//				
//				firmwareManager.updateFirmWareFile(conditionMap);
//				
				returnResult.put("fileExists", "No File");	// OTA 사용 불가능
			}
        	
        	result.add(returnResult);
		}
		
		mav.addObject("rtnStr", result);
        return mav;
    }
    
    @RequestMapping(value="/gadget/device/firmware/getFirmwareIssueList")
	public ModelAndView getFirmwareIssueList(
			@RequestParam(value = "supplierId") String supplierId,
			@RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "fileName") String fileName,
			@RequestParam(value = "modelName") String modelName,
			@RequestParam(value = "fwVer") String fwVer,
			@RequestParam(value = "targetType") String targetType,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("commandType") String commandType
			) throws Exception {
    	
    	ModelAndView mav = new ModelAndView("jsonView");
    	List<Object> result	= new ArrayList<Object>();
        Map<String,Object> condition = new HashMap<String,Object>();
        
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        int idx = 1;
        int totalCount = 0;
        
        Supplier supplier = supplierManager.getSupplier(Integer.parseInt(supplierId));
        String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		
    	condition.put("supplierId", supplierId);
        condition.put("locationId", locationId);
        condition.put("fileName", fileName);
        condition.put("modelName", modelName);
        condition.put("fwVer", fwVer);
        condition.put("equipKind", targetType.toLowerCase());
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);
        condition.put("page", page);
        condition.put("limit", limit);
        condition.put("isTotal", "true");
        condition.put("commandType", commandType);

        // Total Count 정보를 받아오는 로직(S)
        List<Object> totalCountList = firmwareManager.getFirmwareIssueList(condition);
        totalCount = Integer.parseInt(totalCountList.get(0).toString());
        // Total Count 정보를 받아오는 로직(E)
        
        condition.put("isTotal", "false");
        List<Object> resultList = firmwareManager.getFirmwareIssueList(condition);
        List<Location> locationRoot = locationDao.getRootLocationList();
        
        int dataListLen = 0;
        if (resultList != null) {
			dataListLen = resultList.size();
		}
        
		for (int i = 0; i < dataListLen; i++) {
			HashMap<String, Object> returnResult = new HashMap<String, Object>();
			Object[] resultData = (Object[]) resultList.get(i);
			String locationName = null; 
			
			// location id로 location name을 조회하는 로직(S)
			for (Location loc : locationRoot) {
				if (loc.getId().equals(Integer.parseInt(resultData[0].toString()))) {
					locationName = loc.getName();
				}
			}
			// location id로 location name을 조회하는 로직(E)
			
			returnResult.put("no", (((page-1) * limit) + idx));
			returnResult.put("locationName", locationName);
			returnResult.put("location", resultData[0]);
			returnResult.put("fileName", resultData[1]);
			returnResult.put("issueDate", resultData[2]);
			returnResult.put("issueDateFormat", TimeLocaleUtil.getLocaleDate(resultData[2].toString(), lang, country));
			returnResult.put("model", resultData[3]);
			returnResult.put("fwVer", resultData[4]);
			returnResult.put("targetType", resultData[5].toString().toUpperCase());
			returnResult.put("totalCount", resultData[6]);
			returnResult.put("step1Count", resultData[7]);
			returnResult.put("step2Count", resultData[8]);
			returnResult.put("step3Count", resultData[9]);
			returnResult.put("step4Count", resultData[10]);
			returnResult.put("step5Count", resultData[11]);
			returnResult.put("step6Count", resultData[12]);
			returnResult.put("step7Count", resultData[13]);
			returnResult.put("fwId", resultData[14]);
			returnResult.put("executeType", resultData[16]);
			returnResult.put("commandType", resultData[17]);
			
			result.add(returnResult);
			idx++;
		}
		
		mav.addObject("rtnStr", result);
		mav.addObject("totalCount", totalCount);
		
        return mav;
    }
    
    @RequestMapping(value="/gadget/device/firmware/getFirmwareIssueHistoryList")
	public ModelAndView getFirmwareIssueHistoryList(
			@RequestParam(value = "supplierId") String supplierId,
			@RequestParam(value = "firmwareId") String firmwareId,
			@RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "fileName") String fileName,
			@RequestParam(value = "modelName") String modelName,
			@RequestParam(value = "fwVer") String fwVer,
			@RequestParam(value = "targetType") String targetType,
			@RequestParam(value = "step") String step,
			@RequestParam(value = "targetId") String targetId,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("issueDate") String issueDate
			) throws Exception {
    	
    	ModelAndView mav = new ModelAndView("jsonView");
    	List<Object> result	= new ArrayList<Object>();
    	List<Object> result2	= new ArrayList<Object>();
        Map<String,Object> condition = new HashMap<String,Object>();
        
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        int page = Integer.parseInt(request.getParameter("page"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        int idx = 1;
        int totalCount = 0;
        
        Supplier supplier = supplierManager.getSupplier(Integer.parseInt(supplierId));
        String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		
    	condition.put("supplierId", supplierId);
    	condition.put("firmwareId", firmwareId);
        condition.put("locationId", locationId);
        condition.put("fileName", fileName);
        condition.put("modelName", modelName);
        condition.put("fwVer", fwVer);
        condition.put("equipKind", targetType.toLowerCase());
        condition.put("step", step);
        condition.put("deviceId", targetId);
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);
        condition.put("issueDate", issueDate);
        condition.put("page", page);
        condition.put("limit", limit);
        condition.put("isTotal", "true");
        
        // Total Count 정보를 받아오는 로직(S)
        List<Object> totalCountList = firmwareManager.getFirmwareIssueHistoryList(condition);
        totalCount = Integer.parseInt(totalCountList.get(0).toString());
        // Total Count 정보를 받아오는 로직(E)
        
        condition.put("isTotal", "false");
        List<Object> resultList = firmwareManager.getFirmwareIssueHistoryList(condition);
        List<Location> locationRoot = locationDao.getRootLocationList();
        
		int dataListLen = 0;
		if (resultList != null) {
			dataListLen = resultList.size();
		}
		
		for (int i = 0; i < dataListLen; i++) {
			HashMap returnResult = new HashMap();
			Object[] resultData = (Object[]) resultList.get(i);
			String locationName = null; 
			
			// location id로 location name을 조회하는 로직(S)
			for (Location loc : locationRoot) {
				if (loc.getId().equals(Integer.parseInt(resultData[0].toString()))) {
					locationName = loc.getName();
				}
			}
			// location id로 location name을 조회하는 로직(E)
			
			returnResult.put("no", (((page-1) * limit) + idx));
			returnResult.put("locationName", locationName);
			returnResult.put("location", resultData[0]);
			returnResult.put("fileName", resultData[1]);
			returnResult.put("issueDate", resultData[2]);
			returnResult.put("issueDateFormat", TimeLocaleUtil.getLocaleDate(resultData[2].toString(), lang, country));
			returnResult.put("model", resultData[3]);
			returnResult.put("fwVer", resultData[4]);
			returnResult.put("targetType", resultData[5].toString().toUpperCase());
			returnResult.put("targetId", resultData[6]);
			returnResult.put("step", resultData[7]);
			if (resultData[8] != null) {
				returnResult.put("updateDate", TimeLocaleUtil.getLocaleDate(resultData[8].toString(), lang, country));
			}
			returnResult.put("resultStatus", resultData[9]);
			
			result.add(returnResult);
			idx++;
		}
		
		mav.addObject("rtnStr", result);
		mav.addObject("totalCount", totalCount);
		
        return mav;
    }
    
    @RequestMapping(value="/gadget/device/firmware/getFirmwareIssueHistoryListAll")
	public ModelAndView getFirmwareIssueHistoryListAll(
			@RequestParam(value = "supplierId") String supplierId,
			@RequestParam(value = "firmwareId") String firmwareId,
			@RequestParam(value = "locationId") String locationId,
			@RequestParam(value = "fileName") String fileName,
			@RequestParam(value = "modelName") String modelName,
			@RequestParam(value = "fwVer") String fwVer,
			@RequestParam(value = "targetType") String targetType,
			@RequestParam(value = "step") String step,
			@RequestParam(value = "targetId") String targetId,
			@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate,
			@RequestParam("issueDate") String issueDate
			) throws Exception {
    	
    	ModelAndView mav = new ModelAndView("jsonView");
    	List<Object> result	= new ArrayList<Object>();
    	List<Object> result2	= new ArrayList<Object>();
        Map<String,Object> condition = new HashMap<String,Object>();
        
        HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
        int page = 1;
        int limit = 100000000;
        int idx = 1;
        int totalCount = 0;
        
        Supplier supplier = supplierManager.getSupplier(Integer.parseInt(supplierId));
        String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		
    	condition.put("supplierId", supplierId);
    	condition.put("firmwareId", firmwareId);
        condition.put("locationId", locationId);
        condition.put("fileName", fileName);
        condition.put("modelName", modelName);
        condition.put("fwVer", fwVer);
        condition.put("equipKind", targetType.toLowerCase());
        condition.put("step", step);
        condition.put("deviceId", targetId);
        condition.put("startDate", startDate);
        condition.put("endDate", endDate);
        condition.put("issueDate", issueDate);
        condition.put("page", page);
        condition.put("limit", limit);
        condition.put("isTotal", "true");
        
        // Total Count 정보를 받아오는 로직(S)
        List<Object> totalCountList = firmwareManager.getFirmwareIssueHistoryList(condition);
        totalCount = Integer.parseInt(totalCountList.get(0).toString());
        // Total Count 정보를 받아오는 로직(E)
        
        condition.put("isTotal", "false");
        List<Object> resultList = firmwareManager.getFirmwareIssueHistoryList(condition);
        List<Location> locationRoot = locationDao.getRootLocationList();
        
		int dataListLen = 0;
		if (resultList != null) {
			dataListLen = resultList.size();
		}
		
		for (int i = 0; i < dataListLen; i++) {
			HashMap returnResult = new HashMap();
			String retryAlltarget="";
			Object[] resultData = (Object[]) resultList.get(i);
			String locationName = null; 
			
			// location id로 location name을 조회하는 로직(S)
			for (Location loc : locationRoot) {
				if (loc.getId().equals(Integer.parseInt(resultData[0].toString()))) {
					locationName = loc.getName();
				}
			}
			// location id로 location name을 조회하는 로직(E)
			retryAlltarget = resultData[0]+","+resultData[5].toString().toUpperCase()+","+resultData[6]+","+resultData[2];
			result2.add(retryAlltarget);
			idx++;
		}
		mav.addObject("rtnStr", result2);
		mav.addObject("totalCount", totalCount);
		
        return mav;
    }
    
    /*
     * OTA 실행
     */
    @RequestMapping(value="/gadget/device/firmware/otaRun")
    public ModelAndView otaRun(@RequestParam(value="eId") String vendorName) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
        Map<String,Object> condition = new HashMap<String,Object>();
        
        
        //put grid data
        // ..
                
        mav.addObject("result", "Result of ota-run");
        return mav;
    }
    
    /*
     * 펌웨어 아이템 추가
     */
    // Firmware Add - 'OK' 버튼 클릭했을 때 실행되는 메소드
    @RequestMapping(value="/gadget/device/firmware/addNewFirmwareItem")
    public ModelAndView addNewFirmwareItem(
    								HttpServletRequest request, HttpServletResponse response,
    								@RequestParam(value="vendorid") int vendorId,
    								@RequestParam(value="modelid") int modelId,	
    								@RequestParam(value="fwversion") String fwVersion,
    								@RequestParam(value="parameter") String parameter,
    								@RequestParam(value="devicetype") String deviceType,
    								@RequestParam(value="loginId") String loginId,
    								@RequestParam(value="overwrite") Boolean overwrite) throws Exception {
    	
    	ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");

		byte[] fileBinary = null;
		boolean checkDuplication = false;
		String errorReason = "";
		String fwDownURL = null;
		String finalFilePath = null;		
		String[] filePath = null;
		String ext = null;
		String fileName = null;
		
		fileBinary = fileBinary_;
		filePath = filePath_;
		fileName = fileName_;
		ext = ext_;
		//String fileNameWithFwVersion = fwVersion + fileName;
		String fileNameWithFwVersion = fileName;
		
		// 제조사명 정보를 가져온다.
 		DeviceVendor deviceVendor = deviceVendorDao.get(vendorId);
 		String vendorName = deviceVendor.getName();
 		
 		// 모델명 정보를 가져온다.
 		DeviceModel deviceModel =  deviceModelDao.get(modelId);
 		String modelName = deviceModel.getName();
 		
		String osName = System.getProperty("os.name");
		String contextRoot = "";

		if (osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0) {
			//fwDownURL = "http://" + request.getLocalAddr() + ":" + request.getLocalPort() + request.getContextPath() + "/" + "fw/" + deviceType + "/" + vendorName + "/" + modelName + "/" + fileNameWithFwVersion + "." + ext;
			fwDownURL = "fw/" + deviceType + "/" + vendorName + "/" + modelName + "/" + fwVersion + "/" + fileName + "." + ext;
			contextRoot = new HttpServletRequestWrapper(request).getRealPath("/") + "fw/" + deviceType + "/" + vendorName + "/" + modelName+ "/" + fwVersion;
		} else {
			// linux일 경우 server.xml에 "/firmware-file" Context path로 지정해야 한다.
			// ex) <Context path="/firmware-file" docBase="/home/aimir/aimiramm/"/>
			
			/* SORIA Sample : http://10.40.201.46:8081/ota/fw/jdbc.properties */
			//fwDownURL = "http://" + request.getLocalAddr() + ":" + request.getLocalPort() + "/firmware-file" + "/" + "fw/" + deviceType + "/" + vendorName + "/" + modelName + "/" + fileNameWithFwVersion + "." + ext;
			fwDownURL = "fw/" + deviceType + "/" + vendorName + "/" + modelName + "/" + fwVersion + "/" + fileName + "." + ext;
			contextRoot = CommandProperty.getProperty("firmware.dir") + "/" + "fw/" + deviceType + "/" + vendorName + "/" + modelName + "/" + fwVersion;
        }
		
		logger.info(String.format("Download firmware file url -%s", fwDownURL));

		StringBuilder firmwareDir = new StringBuilder();
		firmwareDir.append(contextRoot);
		firmwareDir.append("/");
		firmwareDir.append(fileNameWithFwVersion);
		firmwareDir.append(".");
		firmwareDir.append(ext);
		finalFilePath = firmwareDir.toString();
		
		if(overwrite) { // SP-967
			File file = new File(finalFilePath);
			file.delete();
		}else {
			/** 파일명 중복  Check Logic (S) */
			checkDuplication = checkDuplication(contextRoot, fileName, ext);
			
			if(checkDuplication){ // 해당 경로에 이미 같은명의 파일이 있는 경우
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", "Already same file exist. Please use different file.");
				return mav;
			}
			/** 파일명 중복  Check Logic (E) */
		}
		
		
		// DB에 데이터 저장 로직 (S)
        try {
        	Map<String,Object> condition = new HashMap<String,Object>();
     		String currentDay = TimeUtil.getCurrentDay();
     		String currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
     		String creationDate = currentDay + currentTime;
    	    
     		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
            AimirUser user = (AimirUser)instance.getUserFromSession();
            int supplierId = user.getRoleData().getSupplier().getId();
    	    
            condition.put("fw_crc", crc_);
    	    condition.put("vendorId", vendorId);
            condition.put("modelId", modelId);
            condition.put("fwVersion", fwVersion);
            condition.put("deviceType", deviceType);
            condition.put("loginId", loginId);
            condition.put("fileName", fileName);
            condition.put("fileNameWithFwVersion", fileNameWithFwVersion);
            condition.put("creationDate", creationDate);
            condition.put("supplierId", supplierId);
            condition.put("checkSum", checkSum_);
            condition.put("finalFilePath", finalFilePath);
            condition.put("fwDownURL", fwDownURL); 
    		
    		// Target Type이 'meter'일 경우에만 Image Key를 저장한다.
    		if(deviceType.equals("mcu") || deviceType.equals("mcu-kernel") || deviceType.equals("modem") || deviceType.equals("dcu-coordinate")){
    			condition.put("parameter", null);
    		}  else if(deviceType.equals("meter")){
    			condition.put("parameter", parameter);
    		}
            
            // Database data setter
			Firmware firmware = firmwareManager.findByFileUrlPath(fwDownURL);
    		if(overwrite && firmware != null) { // SP-967
        		condition.put("fileStatus", " ");
        		condition.put("command", "updateFileStatus");
    	        condition.put("firmwareId", firmware.getFirmwareId());
        		firmwareManager.updateFirmWareFile(condition);
    		}else {
                firmwareManager.addFirmWareFile(condition);
    		}
            status = ResultStatus.SUCCESS;
		} catch (Exception e) {
			logger.error(e, e);
			mav.addObject("status", ResultStatus.FAIL);
			mav.addObject("rtnStr", "The same firmware version already exists.");
			
	        return mav;
		}
        // DB에 데이터 저장 로직 (E)

        // 서버에 파일 저장 로직 (S)
        if(status.name().equals("SUCCESS")){
        	finalFilePath = makeFirmwareDirectoryForSORIA(contextRoot, fileNameWithFwVersion, ext, true);
        	logger.info(String.format("Save firmware file - %s",finalFilePath));
    		
    		FileOutputStream foutStream = new FileOutputStream(finalFilePath, true);
    		foutStream.write(fileBinary);			
    		foutStream.flush();
    		foutStream.close();
    		
    		status = ResultStatus.SUCCESS;
        } else {
        	status = ResultStatus.FAIL;
        	mav.addObject("rtnStr", "Fail to add the firmware file on the server.");
        }
        // 서버에 파일 저장 로직 (E)
        
	    mav.addObject("status", status.name());
        return mav;
    }
    
    /*
     * 펌웨어 아이템 수정
     */
    @RequestMapping(value="/gadget/device/firmware/updateFirmwareItem")
    public ModelAndView updateFirmwareItem(
    		@RequestParam(value="vendorid") int vendorId,
			@RequestParam(value="modelid") int modelId,	
			@RequestParam(value="fwversion") String fwVersion,
			@RequestParam(value="firmwareId") String firmwareId
    		) throws Exception {
    	
    	ModelAndView mav = new ModelAndView("jsonView");
    	Map<String,Object> condition = new HashMap<String,Object>();
    	ResultStatus status = ResultStatus.FAIL;
        
    	try {
    		condition.put("vendorId", vendorId);
        	condition.put("modelId", modelId);
        	condition.put("fwVersion", fwVersion);
        	condition.put("firmwareId", firmwareId);	// 현재 선택된 fimrware 테이블의 Id
        	
        	// Database data setter
            firmwareManager.updateFirmWareFile(condition);
		} catch (Exception e) {
			e.printStackTrace();
			mav.addObject("status", status.name());
			mav.addObject("rtnStr", "Fail to update the firmware file.");
	        return mav;
		}
    	
    	status = ResultStatus.SUCCESS;
	    mav.addObject("status", status.name());
	    
        return mav;
    }
    
    /*
     * 펌웨어 아이템 삭제
     */
    @RequestMapping(value="/gadget/device/firmware/deleteFirmwareItem")
    public ModelAndView deleteFirmwareItem(@RequestParam(value="firmwareId") String firmwareId) throws Exception {
    	
    	ModelAndView mav = new ModelAndView("jsonView");
    	ResultStatus status = ResultStatus.FAIL;
    	
    	Firmware firmware = firmwareDao.getByFirmwareId(firmwareId);
    	String filePath = firmware.getFilePath();
    	String rtnStr = null;
    	
    	try {
        	// 서버에 저장되어있는 파일 삭제
        	// Java NIO.2 적용
            Path path = Paths.get(filePath);
            boolean pathExists = Files.exists(path, new LinkOption[]{ LinkOption.NOFOLLOW_LINKS});
            if(pathExists) { // 파일이 실제로 있는지 확인. SP-967
               	Files.delete(path);
            }
            
            // DB에 저장된 Firmware File 정보삭제
//        	firmwareManager.deleteFirmware(firmwareId);
            firmwareManager.deleteFirmwareLogical(firmwareId); // SP-967
        	status = ResultStatus.SUCCESS;
		} catch (Exception e) {
			logger.error(e, e);
			status = ResultStatus.FAIL;
			rtnStr = "Fail to delete the firmware file.";
		}

	    mav.addObject("status", status.name());
	    mav.addObject("rtnStr", rtnStr);
        return mav;
    }
    
    /**
	 * 파일 업로드 모듈 (UploadPanel에서 Select버튼을 통해 파일을 선택했을 때)
	 */
    @RequestMapping(value="/gadget/device/firmware/getTempFileName")
    public ModelAndView getTempFileName(
    	HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, IOException {
    	
    	
    	ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		String errorReason = "";
			
		String finalFilePath = null;		
		byte[] fileBinary = null;
		String[] filePath = null;
		String fileName = null;
		String ext = null;
		String checkSum = null;
		String fwVersion = null;
		try {
			MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) request;
			MultipartFile multipartFile = multiReq.getFile("fileform");
			
			File file = convert(multipartFile);
			checkSum = FirmWareHelper.getFileMD5Sum(file);
			
			String fileFullName = multipartFile.getOriginalFilename();
			fileBinary = multipartFile.getBytes();
			
			int fwVersionIndexStart = fileFullName.lastIndexOf("_");
			String temp = fileFullName.substring(0,  fileFullName.lastIndexOf("_"));
			fileName = temp.substring(0,temp.lastIndexOf('.'));
			ext = fileFullName.substring(temp.lastIndexOf('.')+1, fwVersionIndexStart);
			fwVersion = fileFullName.substring(fwVersionIndexStart+1, fileFullName.length());
			/*System.out.println(fileFullName);
			System.out.println(fileName);
			System.out.println(ext);
			System.out.println(fwVersion);*/
		} catch (Exception e) {
			mav.addObject("status", status.name());
			mav.addObject("rtnStr", "Invalid parameter");
			e.printStackTrace();
			return mav;
		}
	    
	    setFileInfo(/*fwDownURL,*/ finalFilePath, fileBinary, filePath, fileName, ext, checkSum, fwVersion);
	    
        return mav;
    }
    
    @RequestMapping(value="/gadget/device/firmware/getTempFileName_dcu")
    public ModelAndView getTempFileName_dcu(
    	HttpServletRequest request, HttpServletResponse response) throws ServletRequestBindingException, IOException {
    	
    	
    	ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		String errorReason = "";
			
		String finalFilePath = null;		
		byte[] fileBinary = null;
		String[] filePath = null;
		String fileName = null;
		String ext = null;
		String checkSum = null;
		String fwVersion = null;
		try {
			MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) request;
			MultipartFile multipartFile = multiReq.getFile("fileform");
			
			File file = convert(multipartFile);
			checkSum = FirmWareHelper.getFileMD5Sum(file);
			
			String fileFullName = multipartFile.getOriginalFilename();
			fileBinary = multipartFile.getBytes();
			
			int fwVersionIndexStart = fileFullName.lastIndexOf("_");
			
			/*if(targetDeviceType == 'dcu' || targetDeviceType == 'dcu-kernel'){
            	var str = file.substring(0, file.lastIndexOf('_'));
            	realExt = file.substring( str.lastIndexOf('.')+1, fwVersionIndexStart);
            	fwVersion = file.substring(fwVersionIndexStart+1, file.lastIndexOf('.'));
            }*/
			
			String temp = fileFullName.substring(0,  fileFullName.lastIndexOf("_"));
			fileName = temp.substring(0,temp.lastIndexOf('.'));
			ext = fileFullName.substring(temp.lastIndexOf('.')+1, fwVersionIndexStart);
			fwVersion = fileFullName.substring(fwVersionIndexStart+1, fileFullName.length());
			
			/*System.out.println(fileFullName);
			System.out.println(fileName);
			System.out.println(ext);
			System.out.println(fwVersion);*/
		} catch (Exception e) {
			mav.addObject("status", status.name());
			mav.addObject("rtnStr", "Invalid parameter");
			e.printStackTrace();
			return mav;
		}
	    
	    setFileInfo(/*fwDownURL,*/ finalFilePath, fileBinary, filePath, fileName, ext, checkSum, fwVersion);
	    
        return mav;
    }
    
    // Convert 'MultipartFile' to 'File'
	public File convert(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		convFile.createNewFile();
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}
    
    public void setFileInfo(String finalFilePath, byte[] fileBinary, String[] filePath, String fileName, String ext, String checkSum, String fwVersion){
    	byte[] imgCrc16 = CRCUtil.Calculate_ZigBee_Crc(fileBinary, (char) 0x0000);
    	DataUtil.convertEndian(imgCrc16);
    	
    	this.crc_ = Hex.decode(imgCrc16);
    	this.finalFilePath_ = finalFilePath;
    	this.fileBinary_ = fileBinary;
    	this.filePath_ = filePath;
    	this.fileName_ = fileName;
        this.ext_ = ext;               
        this.fwVersion = fwVersion;  
        
    	// 임시로 받은(저장완료하기 전) checkSum 데이터를 전역변수 checkSum_에 임시 저장한다.
    	this.checkSum_ = checkSum;
    }
    
    @RequestMapping(value="/gadget/device/firmware/getTempFileInfo")
    public ModelAndView getTempFileInfo() {
    	List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
        Map<String,Object> dataMap = new HashMap<String,Object>();
    	
		ModelAndView mav = new ModelAndView("jsonView");
		
		mav.addObject("filePath", filePath_);
		mav.addObject("fileName", fileName_);
		mav.addObject("ext", ext_);
		mav.addObject("finalFilePath", finalFilePath_);
		mav.addObject("crc", crc_);
		mav.addObject("fwVersion", fwVersion);
		
		return mav;
    }
    
    private boolean checkDuplication(String homePath, String fileName, String ext) {
    	boolean result = false;
    	
    	File file = null;
		StringBuilder firmwareDir = new StringBuilder();
		firmwareDir.append(homePath);

		file = new File(firmwareDir.toString());
		if (!file.exists()) {
			file.mkdirs();
		}
		
		firmwareDir.append("/");
		firmwareDir.append(fileName);
		firmwareDir.append(".");
		firmwareDir.append(ext);

		file = new File(firmwareDir.toString());

		if (file.exists()) {
			result = true;
		}
		
		return result;
	}
    
    
    private String makeFirmwareDirectoryForSORIA(String homePath, String fileName, String ext, boolean deletable) {
		File file = null;
		StringBuilder firmwareDir = new StringBuilder();
		firmwareDir.append(homePath);
		file = new File(firmwareDir.toString());
		if (!file.exists()) {
			file.mkdirs();
		}
		firmwareDir.append("/");
		firmwareDir.append(fileName);
		firmwareDir.append(".");
		firmwareDir.append(ext);

		file = new File(firmwareDir.toString());

		boolean result = false;
		if (deletable && file.exists()) {
			result = file.delete();
		} else {
			result = true;
		}

		if (!result) {
			//새로운 이름 규칙은 기존 이름+(n) 방식이다.
			if (fileName.matches(".*\\([0-9]*\\)")) {
				//기존 파일 이름이 중복 규칙에 의해 만들어진 파일 명이라면 숫자를 증가시켜 이름을 다시 만든다.
				int number = Integer.valueOf(fileName.replaceAll(".*\\(([0-9]*)\\)", "$1"));
				fileName = fileName.replaceAll("(.*)\\([0-9]*\\)", String.format("$1(%d)", number++));
			} else {
				// 파일 이름에 중복 이름 규칙을 적용한다.
				fileName = String.format("%s(0)", fileName);
			}

			//중복되는지 제귀하여 확인한다.
			return makeFirmwareDirectoryForSORIA(homePath, fileName, ext, deletable);
		}
		return file.getPath();
	}
    
    @RequestMapping(value="/gadget/device/firmware/getFirmwareSearchLocationName")
    public ModelAndView getFirmwareSearchLocationName() throws Exception {
    	
		ModelAndView mav = new ModelAndView("jsonView");

		// User 계정이 Admin이고, DSO 정보 입력란에 아무것도 입력하지 않고 검색한 경우 - default값으로 location table의 첫번째 인덱스 데이터를 반환
		List<Location> locationRoot = locationDao.getRootLocationList();
		
		mav.addObject("locationName", locationRoot.get(0).getName());
		mav.addObject("locationId", locationRoot.get(0).getId());
        return mav;
    }
    
    
	/*@RequestMapping(value="/gadget/device/firmware/getTempFileName")
    public ModelAndView getTempFileName(HttpServletRequest request, HttpServletResponse response)
                    throws ServletRequestBindingException, IOException {
		
		String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");
        		
        MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest)request;
        MultipartFile multipartFile = multiReq.getFile("userfile");
        
        String upFile = (String) multiReq.getParameter("upfile");
        if(upFile == null || "".equals(upFile))
        	return null;
        
        String oldname = multipartFile.getOriginalFilename();
        String filename = "";
        if (oldname == null || "".equals(oldname)){
        	return null;
        }else{
        	// 필요한 경우 파일 이름 변경 ("date"_original name.xls)        	
        	filename = "20160525".concat("_".concat(oldname));             	
        }
            
        String tempPath = contextRoot+"temp/firmware/upload";
        if (!FileUploadHelper.exists(tempPath)) {
            File savedir = new File(tempPath);
            savedir.mkdir();
        }
        
        // 90일이 지난 file 모두 삭제
        File savedir = new File(tempPath);
        if(savedir.length()>0){
        	File[] files = savedir.listFiles();
        	
        	if(files != null){
        		String savename = null;
                String deleteDate = null;
                
                try { 
                	deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -90); 
                } catch (ParseException pe){
                	pe.printStackTrace();
                }
                boolean isDel = false;
                for (File file : files) {
                	savename = file.getName();
                    isDel = false;

                    // 파일길이 : 10이상, 확장자 : ebl
                    if (savename.length() > 10 && (savename.endsWith("ebl") || savename.endsWith("ebls"))) {
                        // 90일 지난 파일들 삭제
                    	int startPosition = 0; //savename.lastIndexOf(".")-12;
                    	int lastPosition = 8; //savename.lastIndexOf(".")-6;
                        if (savename.contains("fw") && savename.substring(startPosition, lastPosition).compareTo(deleteDate) < 0) {
                            isDel = true;
                        }
                        if (isDel) {
                            file.delete();
                        }
                    }
                    savename = null;
                }
        	}
        }
        
        
        File uFile = new File(FileUploadHelper.makePath(tempPath, filename));

        if(FileUploadHelper.exists(FileUploadHelper.makePath(tempPath, filename))){

            if(FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(tempPath, filename))){

                multipartFile.transferTo(uFile);
            }
        }
        else multipartFile.transferTo(uFile);

        String filePath = tempPath+"/"+filename;

        String ext = filePath.substring(filePath.lastIndexOf(".")+1).trim();
        
		        
		ModelAndView mav = new ModelAndView("gadget/device//firmware/firmwareBulkFile");
        //mav.addObject("tempFileName", filePath);
        //mav.addObject("newFileName", filename);
        mav.addObject("result", "result of select");
        

        return mav;
    }*/
	
}
