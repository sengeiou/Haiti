package com.aimir.bo.device;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.constants.CommonConstants.RegType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.DeviceRegistrationManager;
import com.aimir.service.device.MeterManager;
import com.aimir.service.device.ModemManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.RoleManager;
import com.aimir.support.AimirFilePath;
import com.aimir.support.FileUploadHelper;
import com.aimir.util.CalendarUtil;
import com.aimir.util.CommonUtils;
import com.aimir.util.DecimalUtil;
import com.aimir.util.EnergyBalanceMonitoringReportExcel;
import com.aimir.util.FirmwareMakeExcel;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;


@Controller
public class DeviceRegistrationController {

	private static Log logger = LogFactory.getLog(DeviceRegistrationController.class);

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	DeviceRegistrationManager deviceRegistrationManager;

	@Autowired
	AimirFilePath aimirFilePath;

	@Autowired
	RoleManager roleManager;

	@Autowired
	CodeManager codeManager;
	
    @Autowired
    ModemManager modemManager;

    @Autowired
    MeterManager meterManager;
	

	@RequestMapping(value = "/gadget/device/deviceRegistrationMiniGadget")
	public ModelAndView deviceRegistrationMiniGadget() {
		return new ModelAndView("gadget/device/deviceRegistrationMiniGadget");
	}

	@RequestMapping(value = "/gadget/device/deviceRegistrationMaxGadget")
	public ModelAndView deviceRegistrationMaxGadget() {
		ModelAndView mav = new ModelAndView("gadget/device/deviceRegistrationMaxGadget");
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();

		Role role = roleManager.getRole(user.getRoleData().getId());
		Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

		mav.addObject("editAuth", authMap.get("cud")); // 수정권한(write/command =
														// true)
		return mav;
	}

	// MeterMini Gadget 조회 / Chart,Grid 동일 데이터 사용
	@RequestMapping(value = "/gadget/device/getDeviceRegMiniChart")
	public ModelAndView getDeviceRegMiniChart(@RequestParam(value = "viewType", required = false) String viewType,
			@RequestParam(value = "supplierId", required = false) String supplierId) {

		Map<String, Object> condition = new HashMap<String, Object>();

		condition.put("viewType", viewType);
		// condition.put("supplierId" , supplierId);

		// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();
		int supplier = user.getRoleData().getSupplier().getId();
		condition.put("supplierId", supplier + "");
		List<Object> miniChart = deviceRegistrationManager.getMiniChart(condition);

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("chartData", miniChart.get(0));

		return mav;
	}

	@RequestMapping(value = "/gadget/device/getAssetMiniChart")
	public ModelAndView getDeviceRegMiniChart() {

		Map<String, String> condition = new HashMap<String, String>();

		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();
		int supplier = user.getRoleData().getSupplier().getId();
		condition.put("supplierId", supplier + "");
		Map<String, Object> miniChart = deviceRegistrationManager.getAssetMiniChart(condition);

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("chartData", miniChart);

		return mav;
	}

	@RequestMapping(value = "/gadget/device/getVendorListBySubDeviceType")
	public ModelAndView getVendorListBySubDeviceType(
			@RequestParam(value = "deviceType", required = false) String deviceType,
			@RequestParam(value = "subDeviceType", required = false) String subDeviceType) {

		Map<String, Object> condition = new HashMap<String, Object>();

		condition.put("deviceType", deviceType);
		condition.put("subDeviceType", subDeviceType);

		List<DeviceVendor> rtnData = deviceRegistrationManager.getVendorListBySubDeviceType(condition);

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("deviceVendor", rtnData);

		return mav;
	}

	// MeterMini Gadget 조회 / Chart,Grid 동일 데이터 사용
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/getDeviceRegLog")
	public ModelAndView getDeviceRegLog(@RequestParam(value = "deviceType", required = false) String deviceType,
			@RequestParam(value = "subDeviceType", required = false) String subDeviceType,
			@RequestParam(value = "vendor", required = false) String vendor,
			@RequestParam(value = "model", required = false) String model,
			@RequestParam(value = "deviceID", required = false) String deviceID,
			@RequestParam(value = "regType", required = false) String regType,
			@RequestParam(value = "regResult", required = false) String regResult,
			@RequestParam(value = "message", required = false) String message,
			@RequestParam(value = "searchStartDate", required = false) String searchStartDate // jhkim
			, @RequestParam(value = "searchEndDate", required = false) String searchEndDate,
			@RequestParam(value = "supplierId", required = false) String supplierId) {

		Map<String, Object> condition = new HashMap<String, Object>();

		condition.put("deviceType", deviceType);
		condition.put("subDeviceType", subDeviceType);
		condition.put("vendor", vendor);
		condition.put("model", model);
		condition.put("deviceID", deviceID);
		condition.put("regType", regType);
		condition.put("regResult", regResult);
		condition.put("message", message);

		if (StringUtil.nullToBlank(searchStartDate).isEmpty() && StringUtil.nullToBlank(searchEndDate).isEmpty()) {
			try {
				searchStartDate = TimeUtil.getCurrentDay();
				searchEndDate = TimeUtil.getCurrentDay();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		condition.put("searchStartDate", searchStartDate); // jhkim
		condition.put("searchEndDate", searchEndDate);
		condition.put("supplierId", supplierId);

		List<Object> logGrid = deviceRegistrationManager.getDeviceRegLog(condition);

		// jhkim
		List<Object> gridList = (List<Object>) logGrid.get(1);

		if (supplierId != null && supplierId.length() > 0) {
			Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
			String lang = supplier.getLang().getCode_2letter();
			String country = supplier.getCountry().getCode_2letter();

			for (Object data : gridList) {
				Map<String, Object> mapData = (Map<String, Object>) data;

				mapData.put("installdate", TimeLocaleUtil
						.getLocaleDate(StringUtil.nullToBlank(mapData.get("installdate")), lang, country));
				mapData.put("no", DecimalUtil.getMDStyle(supplier.getMd()).format(mapData.get("no")));
			}
		}

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("totalCnt", logGrid.get(0));
		mav.addObject("gridData", logGrid.get(1));

		return mav;
	}

	@RequestMapping(value = "/gadget/device/getTempFileName")
	public ModelAndView getTempFileName(HttpServletRequest request, HttpServletResponse response)
			throws ServletRequestBindingException, IOException {
		String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");

		MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multiReq.getFile("userfile");

		String filename = multipartFile.getOriginalFilename();
		if (filename == null || "".equals(filename))
			return null;

		String tempPath = contextRoot + "temp";

		if (!FileUploadHelper.exists(tempPath)) {
			File savedir = new File(tempPath);
			savedir.mkdir();
		}
		File uFile = new File(FileUploadHelper.makePath(tempPath, filename));

		if (FileUploadHelper.exists(FileUploadHelper.makePath(tempPath, filename))) {

			if (FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(tempPath, filename))) {

				multipartFile.transferTo(uFile);
			}
		} else
			multipartFile.transferTo(uFile);

		String filePath = tempPath + "/" + filename;

		String ext = filePath.substring(filePath.lastIndexOf(".") + 1).trim();

		ModelAndView mav = new ModelAndView("gadget/device/deviceBulkFile");
		mav.addObject("tempFileName", filePath);
		mav.addObject("titleName", deviceRegistrationManager.getTitleName(filePath, ext));

		return mav;
	}
	
	@RequestMapping(value = "/gadget/device/getTempFileName2")
	public ModelAndView getTempFileName2(HttpServletRequest request, HttpServletResponse response)
			throws ServletRequestBindingException, IOException {
		String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");

		MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multiReq.getFile("excelSearchBtn");

		String filename = multipartFile.getOriginalFilename();
		if (filename == null || "".equals(filename))
			return null;

		String tempPath = contextRoot + "temp";

		if (!FileUploadHelper.exists(tempPath)) {
			File savedir = new File(tempPath);
			savedir.mkdir();
		}
		File uFile = new File(FileUploadHelper.makePath(tempPath, filename));

		if (FileUploadHelper.exists(FileUploadHelper.makePath(tempPath, filename))) {

			if (FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(tempPath, filename))) {

				multipartFile.transferTo(uFile);
			}
		} else
			multipartFile.transferTo(uFile);

		String filePath = tempPath + "/" + filename;

		String ext = filePath.substring(filePath.lastIndexOf(".") + 1).trim();

		ModelAndView mav = new ModelAndView("gadget/device/deviceBulkFile");
		mav.addObject("tempFileName", filePath);
		mav.addObject("titleName", deviceRegistrationManager.getTitleName(filePath, ext));

		return mav;
	}

	@RequestMapping(value = "/gadget/device/getTempShipmentFileName")
	public ModelAndView getTempShipmentFileName(HttpServletRequest request, HttpServletResponse response)
			throws ServletRequestBindingException, IOException {
		MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multiReq.getFile("userfile");

		String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");
		String filename = multipartFile.getOriginalFilename();
		String tempPath = contextRoot + "temp";

		if (filename == null || "".equals(filename)) {
			return null;
		}

		if (!FileUploadHelper.exists(tempPath)) {
			File savedir = new File(tempPath);
			savedir.mkdir();
		}

		File uFile = new File(FileUploadHelper.makePath(tempPath, filename));

		if (FileUploadHelper.exists(FileUploadHelper.makePath(tempPath, filename))) {
			if (FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(tempPath, filename))) {
				multipartFile.transferTo(uFile);
			}
		} else {
			multipartFile.transferTo(uFile);
		}

		String filePath = tempPath + "/" + filename;
		String ext = filePath.substring(filePath.lastIndexOf(".") + 1).trim();

		ModelAndView mav = new ModelAndView("gadget/device/deviceShipmentFile");

		mav.addObject("tempFileName", filePath);
		mav.addObject("titleName", deviceRegistrationManager.getTitleName(filePath, ext));

		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/updateShipmentFile")
	public ModelAndView updateShipmentFile(@RequestParam("supplierId") int supplierId,
			@RequestParam("filePath") String filePath, @RequestParam("fileType") String fileType,
			@RequestParam("detailType") String detailType) {

		ModelAndView mav = new ModelAndView("jsonView");
		String resultMsg = null;
		List device = null;
		List<Integer> excelLineList = new ArrayList();

		try {
			File file = new File(filePath.trim());

			if (!file.exists() || !file.isFile() || !file.canRead()) {
				throw new IOException(filePath);
			}

			Map<String, Object> result = null;
			Map<String, Object> dataMap = null;
			ArrayList dataList = null;
			String dataContents = null;
			String[] tempFileName = filePath.split("/");
			String fileName = tempFileName[1];

			if (!"".equals(StringUtils.defaultIfEmpty(filePath, ""))) {
				String ext = filePath.substring(filePath.lastIndexOf(".") + 1).trim();
				if ("xls".equals(ext)) {
					result = deviceRegistrationManager.readShipmentExcelXLS(filePath, fileType, supplierId, detailType);
				} else if ("xlsx".equals(ext)) {
					result = deviceRegistrationManager.readShipmentExcelXLSX(filePath, fileType, supplierId,
							detailType);
				}
			}

			resultMsg = result.get("resultMsg").toString();
			device = (List) result.get("device");
			excelLineList = (List) result.get("excelLineList");
		} catch (Exception e) {
			logger.error(e, e);
		}

		mav.addObject("resultMsg", resultMsg);
		mav.addObject("deviceList", device);
		mav.addObject("excelLineList", excelLineList);
		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/updateMsisdnFile")
	public ModelAndView updateMsisdnFile(@RequestParam("supplierId") int supplierId,
			@RequestParam("filePath") String filePath) {

		ModelAndView mav = new ModelAndView("jsonView");
		String resultMsg = null;
		List device = null;
		List<Integer> excelLineList = new ArrayList();

		try {
			File file = new File(filePath.trim());

			if (!file.exists() || !file.isFile() || !file.canRead()) {
				throw new IOException(filePath);
			}

			Map<String, Object> result = null;
			Map<String, Object> dataMap = null;
			ArrayList dataList = null;
			String dataContents = null;
			String[] tempFileName = filePath.split("/");
			String fileName = tempFileName[1];

			String fileType = "MSISDN";
			String detailType = null;

			if (!"".equals(StringUtils.defaultIfEmpty(filePath, ""))) {
				String ext = filePath.substring(filePath.lastIndexOf(".") + 1).trim();
				if ("xls".equals(ext)) {
					result = deviceRegistrationManager.readShipmentExcelXLS(filePath, fileType, supplierId, detailType);
				} else if ("xlsx".equals(ext)) {
					result = deviceRegistrationManager.readShipmentExcelXLSX(filePath, fileType, supplierId,
							detailType);
				}
			}

			resultMsg = result.get("resultMsg").toString();
			device = (List) result.get("device");
			excelLineList = (List) result.get("excelLineList");
		} catch (Exception e) {
			logger.error(e, e);
		}

		mav.addObject("resultMsg", resultMsg);
		mav.addObject("deviceList", device);
		mav.addObject("excelLineList", excelLineList);

		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/updateSimCardFile")
	public ModelAndView updateSimCardFile(@RequestParam("supplierId") int supplierId,
			@RequestParam("filePath") String filePath) {

		ModelAndView mav = new ModelAndView("jsonView");
		String resultMsg = null;
		List device = null;
		List<Integer> excelLineList = new ArrayList();

		try {
			File file = new File(filePath.trim());

			if (!file.exists() || !file.isFile() || !file.canRead()) {
				throw new IOException(filePath);
			}

			Map<String, Object> result = null;
			Map<String, Object> dataMap = null;
			ArrayList dataList = null;
			String dataContents = null;
			String[] tempFileName = filePath.split("/");
			String fileName = tempFileName[1];

			String fileType = "SimCard";
			String detailType = null;

			if (!"".equals(StringUtils.defaultIfEmpty(filePath, ""))) {
				String ext = filePath.substring(filePath.lastIndexOf(".") + 1).trim();
				if ("xls".equals(ext)) {
					result = deviceRegistrationManager.readShipmentExcelXLS(filePath, fileType, supplierId, detailType);
				} else if ("xlsx".equals(ext)) {
					result = deviceRegistrationManager.readShipmentExcelXLSX(filePath, fileType, supplierId,
							detailType);
				}
			}

			resultMsg = result.get("resultMsg").toString();
			device = (List) result.get("device");
			excelLineList = (List) result.get("excelLineList");
		} catch (Exception e) {
			logger.error(e, e);
		}

		mav.addObject("resultMsg", resultMsg);
		mav.addObject("deviceList", device);
		mav.addObject("excelLineList", excelLineList);

		return mav;
	}
	
	@RequestMapping(value = "/gadget/device/shipmentFileImportPopup")
	public ModelAndView shipmentFileImportPopup() {
		ModelAndView mav = new ModelAndView("/gadget/device/shipmentFileImportPopup");
		return mav;
	}

	@RequestMapping(value = "/gadget/device/shipmentMsisdnFileImportPopup")
	public ModelAndView shipmentMsisdnFileImportPopup() {
		ModelAndView mav = new ModelAndView("/gadget/device/shipmentMsisdnFileImportPopup");
		return mav;
	}
	
	@RequestMapping(value = "/gadget/device/shipmentSimCardFileImportPopup")
	public ModelAndView shipmentSimCardFileImportPopup() {
		ModelAndView mav = new ModelAndView("/gadget/device/shipmentSimCardFileImportPopup");
		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/insertDeviceBulkFile")
	public ModelAndView insertDeviceBulkFile(@RequestParam("filePath") String filePath,
			@RequestParam("fileType") String fileType, @RequestParam("detailType") String detailType,
			@RequestParam("supplierId") int supplierId) {

		String resultMsg = null;
		try {
			File file = new File(filePath.trim());

			if (!file.exists() || !file.isFile() || !file.canRead()) {
				throw new IOException(filePath);
			}

			Map<String, Object> result = null;

			if (!"".equals(StringUtils.defaultIfEmpty(filePath, ""))) {
				String ext = filePath.substring(filePath.lastIndexOf(".") + 1).trim();
				if ("xls".equals(ext)) {
					result = deviceRegistrationManager.readExcelXLS(filePath, fileType, supplierId, detailType);
				} else if ("xlsx".equals(ext)) {
					result = deviceRegistrationManager.readExcelXLSX(filePath, fileType, supplierId, detailType);
				}
			}

			if (result != null) {
				// int cnt = 0;
				// resultList = (List<Object>)result.get("file");
				// headerList = (List<Object>)result.get("header");
				// index = (List<String>)result.get("index");
				// resultMsg = result.get("resultMsg").toString();
				// Map<String, Object> resultMap = null;
				//
				// for (Object obj : (List<Object>)result.get("device")) {
				// if (obj != null)
				// resultMap = (Map<String, Object>)resultList.get(cnt++);
				// if (deviceRegistrationManager.insertDevice(obj, fileType,
				// detailType) != null) {
				// resultMap.put("Status", "Success");
				// } else {
				// resultMap.put("Status", "Failure");
				// }
				// }

				resultMsg = result.get("resultMsg").toString();
				// Map<String, Object> resultMap = null;

				// for (Object obj : (List<Object>)result.get("updateDevice")) {
				// deviceRegistrationManager.updateDevice(obj, fileType,
				// detailType);
				//// deviceRegistrationManager.insertDevice(obj, fileType,
				// detailType);
				// }
				deviceRegistrationManager.updateDevice((List<Object>) result.get("updateDevice"), fileType, detailType);

				for (Object obj : (List<Object>) result.get("device")) {
					// if (obj != null)
					// resultMap = (Map<String, Object>)resultList.get(cnt++);
					// if (deviceRegistrationManager.insertDevice(obj, fileType,
					// detailType) != null) {
					// resultMap.put("Status", "Success");
					// } else {
					// resultMap.put("Status", "Failure");
					// }
					deviceRegistrationManager.insertDevice(obj, fileType, detailType);
				}

				// File Copy & Temp File Delete
				FileUploadHelper.removeExistingFile(filePath);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error(e.toString(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString(), e);
		}

		// List<Object> result = new ArrayList();
		// for (int i = 0; i < resultList.size(); i++) {
		// List<Object> tmpList = new ArrayList();
		// Map<String, Object> tmpMap = new HashMap<String, Object>();
		// tmpMap = (Map<String, Object>)resultList.get(i);
		// for (int j = 0; j < headerList.size(); j++) {
		// tmpList.add(tmpMap.get(headerList.get(j)) == null ? "" :
		// tmpMap.get(headerList.get(j)));
		// }
		// result.add(tmpList);
		// }

		ModelAndView mav = new ModelAndView("jsonView");
		// mav.addObject("resultList", result);
		// mav.addObject("headerList", headerList);
		// mav.addObject("index", index);
		mav.addObject("resultMsg", resultMsg);
		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/getDeviceBulkFile")
	public ModelAndView getDeviceBulkFile(@RequestParam("filePath") String filePath,
			@RequestParam("fileType") String fileType, @RequestParam("detailType") String detailType,
			@RequestParam("supplierId") int supplierId) {

		List<Object> resultList = null;
		List<Object> headerList = null;
		int index = 0;

		try {
			File file = new File(filePath.trim());

			if (!file.exists() || !file.isFile() || !file.canRead()) {
				throw new IOException(filePath);
			}

			Map<String, Object> result = null;

			if (!"".equals(StringUtils.defaultIfEmpty(filePath, ""))) {
				String ext = filePath.substring(filePath.lastIndexOf(".") + 1).trim();
				if ("xls".equals(ext)) {
					result = deviceRegistrationManager.readOnlyExcelXLS(filePath, fileType, supplierId, detailType);
				} else if ("xlsx".equals(ext)) {
					result = deviceRegistrationManager.readOnlyExcelXLSX(filePath, fileType, supplierId, detailType);
				}
			}

			if (result != null) {
				// int cnt = 0;
				resultList = (List<Object>) result.get("file");
				headerList = (List<Object>) result.get("header");
				if (result.get("index") != null) {
					index = Integer.parseInt(result.get("index").toString());
				}

				// File Copy & Temp File Delete
				FileUploadHelper.removeExistingFile(filePath);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error(e.toString(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString(), e);
		}
		// 데이터 추출

		List<Object> result = new ArrayList();
		for (int i = 0; i < resultList.size(); i++) {
			List<Object> tmpList = new ArrayList();
			Map<String, Object> tmpMap = new HashMap<String, Object>();
			tmpMap = (Map<String, Object>) resultList.get(i);
			for (int j = 0; j < headerList.size(); j++) {
				tmpList.add(tmpMap.get(headerList.get(j)));
			}
			result.add(tmpList);
		}

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("resultList", result);
		mav.addObject("headerList", headerList);
		mav.addObject("index", index);

		return mav;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/getSearchDeviceId")
	public ModelAndView getSearchDeviceId(@RequestParam("filePath") String filePath,
			@RequestParam("targetDeviceType") String targetDeviceType,
			@RequestParam("modelId") String modelId,
			@RequestParam("supplierId") String supplierId,
			@RequestParam("sType") String sType
			)
			 {

		String resultList = "";
		String invalidSerial ="";
		String count ="";
		int index = 0;
		Object versionList = null;

		try {
			File file = new File(filePath.trim());

			if (!file.exists() || !file.isFile() || !file.canRead()) {
				throw new IOException(filePath);
			}

			Map<String, Object> result = null;

			if (!"".equals(StringUtils.defaultIfEmpty(filePath, ""))) {
				String ext = filePath.substring(filePath.lastIndexOf(".") + 1).trim();
				if ("xls".equals(ext)) {
					//result = deviceRegistrationManager.readOnlyExcelXLS(filePath, fileType, supplierId, detailType);
				} else if ("xlsx".equals(ext)) {
					result = deviceRegistrationManager.readDeviceIdExcelXLSX(filePath,targetDeviceType,modelId,sType,supplierId);
				}
			}

			if (result != null) {
				// int cnt = 0;
				versionList = result.get("versionList");
				resultList =  (String)result.get("file");
				invalidSerial = (String)result.get("invalidSerial");
				count = (String)result.get("count");
				
				
				// File Copy & Temp File Delete
				boolean bool = FileUploadHelper.removeExistingFile(filePath);
				System.out.println(bool);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error(e.toString(), e);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString(), e);
		}
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("versionList", versionList);
		mav.addObject("result", resultList);
		mav.addObject("invalidSerial", invalidSerial);
		mav.addObject("count", count);

		return mav;
	}

	@RequestMapping(value = "/gadget/device/getShipmentImportHistory")
	public ModelAndView getShipmentImportHistory(
			@RequestParam(value = "supplierId", required = false) Integer supplierId,
			@RequestParam(value = "targetType", required = false) String targetType,
			@RequestParam(value = "targetName", required = false) String targetName,
			@RequestParam(value = "fileName", required = false) String fileName,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate) throws Exception {

		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> condition = new HashMap<String, Object>();

		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		String regType = RegType.Shipment.toString();

		if (targetName.equals(CommonConstants.ShipmentTargetType.EthernetModem.getName())
				|| targetName.equals(CommonConstants.ShipmentTargetType.EthernetConverter.getName())
				|| targetName.equals(CommonConstants.ShipmentTargetType.MBBModem.getName())
				|| targetName.equals(CommonConstants.ShipmentTargetType.RFModem.getName())) {
			targetName = targetName.replaceAll("-", "");      // 특수문자 '-' 제거
			targetName = targetName.replaceAll("\\p{Z}", ""); // 모든 공백 제거
		}

		condition.put("supplierId", supplierId);
		condition.put("targetType", targetType);
		condition.put("detailType", targetName);
		condition.put("fileName", fileName);
		condition.put("startDate", startDate);
		condition.put("endDate", endDate);
		condition.put("regType", regType);

		Integer totalCount = deviceRegistrationManager.getShipmentImportHistoryTotalCount(condition);
		List<Object> logGrid = deviceRegistrationManager.getShipmentImportHistory(condition);
		List<Object> gridList = (List<Object>) logGrid.get(0);

		for (Object data : gridList) {
			Map<String, Object> mapData = (Map<String, Object>) data;

			mapData.put("importDate",
					TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(mapData.get("importDate")), lang, country));
		}

		mav.addObject("totalCnt", totalCount);
		mav.addObject("result", gridList);

		return mav;
	}
	
    @RequestMapping(value="/gadget/device/firmwareExcelDownloadPopup")
    public ModelAndView firmwareExcelDownloadPopup() {      
        ModelAndView mav = new ModelAndView("/gadget/device/firmware/firmwareExcelDownloadPopup");
        return mav;
    }
	
	@RequestMapping(value = "/gadget/device/getFirmwareExportExcel")
	public ModelAndView getFirmwareExportExcel(@RequestParam("supplierId") Integer supplierId,
			@RequestParam(value = "deviceId", required = false) String deviceId,
			@RequestParam(value = "modelId", required = false) String modelId,
			@RequestParam(value = "fwVersion", required = false) String fwVersion,
			@RequestParam(value = "targetDeviceType", required = false) String targetDeviceType,
			@RequestParam("headerMsg[]") String[] headerMsg,
			@RequestParam(value = "filePath", required = false) String filePath) throws Exception {

        ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> condition = new HashMap<String, Object>();
		List<Map<String, Object>> deviceList = new ArrayList();
		
		Supplier supplier = supplierDao.get(supplierId);
		String country = supplier.getCountry().getCode_2letter();
		String lang = supplier.getLang().getCode_2letter();
		String regType = RegType.Shipment.toString();

		condition.put("supplierId", supplierId);
		condition.put("targetDeviceType", targetDeviceType);
		condition.put("deviceId", deviceId);
		condition.put("fwVersion", fwVersion);
		condition.put("targetDeviceType", targetDeviceType);
		condition.put("modelId", modelId);
		
		Map<String, String> msgMap = new HashMap<String, String>();
		
		List<String> headerList = Arrays.asList(headerMsg);
		// message 생성
		msgMap.put("deviceId", headerMsg[0]);
		msgMap.put("deviceIdParent", headerMsg[1]);
		msgMap.put("fmversionParent", headerMsg[2]);
		msgMap.put("title", headerMsg[3]);
		
		if(targetDeviceType.equals("meter")) {
			deviceList = meterManager.getParentDevice(condition);
		}else if(targetDeviceType.equals("modem")){
			deviceList = modemManager.getParentDevice(condition);
		}
        int total = deviceList.size();

        mav.addObject("total", total);
        if (total <= 0) {
            return mav;
        }
        
		try {
			mav.addObject("total", total);

			StringBuilder sbFileName = new StringBuilder();
			StringBuilder sbSplFileName = new StringBuilder();
			List<String> fileNameList = new ArrayList();
			List<Map<String, Object>> list = new ArrayList();
			int maxRows = 5000; // excel sheet 하나에 보여줄 수 있는 최대 데이터 row 수

			sbFileName.append(headerMsg[3] + "_");
			sbFileName.append(TimeUtil.getCurrentTimeMilli());

			File downDir = new File(filePath);

			if (downDir.exists()) {
				File[] files = downDir.listFiles();

				if (files != null) {
					String deleteDate = CalendarUtil.getDate(TimeUtil.getCurrentDay(), Calendar.DAY_OF_MONTH, -10);

					for (File file : files) {
						String filename = file.getName();
						// 확장자 : xls|zip
						if (filename.endsWith("xls") || filename.endsWith("zip")) {
							// 10일 지난 파일들 삭제
							if (filename.startsWith(headerMsg[3] + "_") && filename.substring(filename.length() - 18, filename.length() - 10).compareTo(deleteDate) < 0) {
								logger.debug("File Delete for last 10days ==> " + filename);
								file.delete();
							}
						}
					}
				}
			} else {
				downDir.mkdir();
			}

			// create excel file
			FirmwareMakeExcel wExcel = new FirmwareMakeExcel();
			int cnt = 1;
			int idx = 0;
			int fnum = 0;
			int splCnt = 0;

			if (total <= maxRows) {
				sbSplFileName = new StringBuilder();
				sbSplFileName.append(sbFileName);
				sbSplFileName.append(".xls");
				wExcel.writeReportExcel(deviceList, msgMap, filePath, sbSplFileName.toString(), supplier);
				fileNameList.add(sbSplFileName.toString());
			} else {
				for (int i = 0; i < total; i++) {
					if ((splCnt * fnum + cnt) == total || cnt == maxRows) {
						sbSplFileName = new StringBuilder();
						sbSplFileName.append(sbFileName);
						sbSplFileName.append('(').append(++fnum).append(").xls");
						list = deviceList.subList(idx, (i + 1));
						wExcel.writeReportExcel(list, msgMap, filePath, sbSplFileName.toString(), supplier);
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
			logger.debug(e,e);
		} catch (Exception e) {
			logger.debug(e,e);
		}

	return mav;
	}
}