package com.aimir.bo.system.prepaymentMgmt;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.prepayment.SalesReportManager;
import com.aimir.util.SPASASalesReportDataMakeExcel;
import com.aimir.util.TimeUtil;
import com.aimir.util.ZipUtils;

@Controller
public class SalesReportMgmtController {
	@Autowired
	PrepaymentLogDao prepaymentLogDao;
	
	@Autowired
	SupplierDao supplierDao;
	
	@Autowired
	SalesReportManager salesReportManager;

	@Autowired
	OperatorDao operatorDao;
	
	int maxRow = 5000;
	
	@RequestMapping("/gadget/prepaymentMgmt/salesReportGadgetMax")
	public ModelAndView salesReportMgmtMax() {
		ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/salesReportGadgetMax");
		
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();
        
		 //user에서 그냥 supplier를 가져오면 이전 supplier객체를 가지고 온다.
        Supplier supplier = supplierDao.get(user.getSupplier().getId());
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("roleType", "vendor");
        params.put("supplierId", supplier.getId());

        mav.addObject("depositVendorList", operatorDao.getOperatorListByRoleType(params));
        
		return mav; 
	}
	
	@RequestMapping("/gadget/prepaymentMgmt/salesReportGadgetMini")
	public ModelAndView salesReportMgmtMini() {
		ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/salesReportGadgetMini");
		return mav; 
	}
	
	@RequestMapping("/gadget/prepaymentMgmt/addBalanceList")
	public ModelAndView getAddBalanceList(
			@RequestParam Integer supplierId,
			@RequestParam String searchDate,
			Integer page,
			Integer limit,
			@RequestParam(value = "vendorId", required = false) String vendorId) {
		ModelAndView mav = new ModelAndView("jsonView");
		Supplier supplier = supplierDao.get(supplierId);
		Map<String, Object> data = salesReportManager.getAddBalanceList(supplier, page, limit, searchDate, vendorId);
		mav.addAllObjects(data);
		return mav;
	}
	
	@RequestMapping("/gadget/prepaymentMgmt/salesExcelDownloadPopup")
	public ModelAndView downloadDailyListExcelPopUp() {
		ModelAndView mav = new ModelAndView("/gadget/prepaymentMgmt/salesExcelDownloadPopup");
		return mav;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/gadget/prepaymentMgmt/dailyListExcelMake")
	public ModelAndView makeDailyListExcelData(@RequestParam Integer supplierId, 
			@RequestParam String filePath,
			String reportType, 
			String searchDate,
			String startDate,
			String endDate,
			String dVendorId,
			String mVendorId) {
		ModelAndView mav = new ModelAndView("jsonView");
		StringBuilder fileName = new StringBuilder(reportType);
		fileName.append(TimeUtil.getCurrentTimeMilli());
		List<String> fileNameList = new ArrayList<String>();
		Map<String, Object> data = new HashMap<String, Object>();
		Supplier supplier = supplierDao.get(supplierId);
		Integer total = new Integer(0);
		
		File downDir = new File(filePath);
		
		if ( downDir.exists() ) {
			File[] files = downDir.listFiles();
			
			if (files != null) {
				for (File file : files) {
					file.delete();
				}
			}
		} else {
			downDir.mkdir();
		}
		
		if ( reportType.equals("daily")) {
			data = salesReportManager.getAddBalanceList(supplier, null, null, searchDate, dVendorId);			
			data.put("searchDate", searchDate);
			
		} else if ( reportType.equals("monthly") ) {
			data = salesReportManager.getMonthlyGridDataList(supplier, null, null, startDate, endDate, mVendorId);
			data.put("startDate", startDate);
			data.put("endDate", endDate);
		}
		total = new Integer(data.get("size").toString());
		SPASASalesReportDataMakeExcel excel = new SPASASalesReportDataMakeExcel();
		
		if ( total < maxRow ) {
			fileName.append(".xls");
			excel.writeReportExcel(data, reportType, supplier, filePath, fileName.toString());
			fileNameList.add(fileName.toString());
		} else {
			int recordIndex = 0;
			int fileIndex = 0;
			List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("data");
			List<Map<String, Object>> subList = new ArrayList<Map<String, Object>>();
			
			while ( recordIndex < total ) {
				subList = list.subList(recordIndex, recordIndex + maxRow);
				recordIndex += maxRow;
				data.put("data", subList);
				fileName.append("(").append(fileIndex++).append(").xml");
				excel.writeReportExcel(data, reportType, supplier, filePath, fileName.toString());
				fileNameList.add(fileName.toString());				
			}
		}
		
		// create zip file
		StringBuilder sbZipFile = new StringBuilder();
		sbZipFile.append(fileName).append(".zip");

		ZipUtils zutils = new ZipUtils();
		try {
			zutils.zipEntry(fileNameList, sbZipFile.toString(), filePath);
			mav.addObject("zipFileName", sbZipFile.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		mav.addObject("total", total);
        mav.addObject("filePath", filePath);
        mav.addObject("fileName", fileNameList.get(0));
        mav.addObject("fileNames", fileNameList);
		return mav;
	}
	
	@RequestMapping("/gadget/prepaymentMgmt/monthlyGridDataList")
	public ModelAndView getMonthlyGridDataList(
			@RequestParam Integer supplierId,
			@RequestParam String startDate,
			@RequestParam String endDate,
			Integer page,
			Integer limit,
			@RequestParam (value="vendorId", required=false) String vendorId) {
		ModelAndView mav = new ModelAndView("jsonView");
		Supplier supplier = supplierDao.get(supplierId);
		Map<String, Object> ret = salesReportManager.getMonthlyGridDataList(supplier, page, limit, startDate, endDate, vendorId);
		mav.addAllObjects(ret);
		return mav; 
	}
}
