package com.aimir.schedule.task;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Location;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.excel.MonthlyConsumeData;
import com.aimir.schedule.excel.MonthlyConsumeExcel;
import com.aimir.schedule.excel.RegionalConsumeData;
import com.aimir.schedule.excel.RegionalConsumeExcel;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

@Transactional
public class ECGBillingMonthlyExcelTask extends ScheduleTask {
	private static final String RESIDENTIAL = "Residential";
	private static final String NONRESIDENTIAL = "Non Residential";
	private static final String REPORT_CON_TITLE = "Prepayment Consumption and Government Subsidy Statistics";
	private static final String REPORT_SAL_TITLE = "Prepayment Sales Report By Tariff Class";
	private static final String FILE_NAME = "MonthlyReport";
	private static final String REGIONAL = "Regional ";
	private static final String SUPPLIER = "ECG";
	private static Log logger = LogFactory.getLog(ECGBillingMonthlyExcelTask.class);
	
	@Autowired
	PrepaymentLogDao prepaymentLogDao;
	
	@Autowired
	LocationDao locationDao;
	
	@Autowired
	SupplierDao supplierDao;
	
	private boolean isNowRunning = false;
	 
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(JobExecutionContext context) {
		if(isNowRunning){
			logger.info("########### ECGBillingMonthlyExcelTask is already running...");
			return;
		}
		isNowRunning = true;
		
		String currentTime = "";
		String yyyymm = "";
		Integer supplierId = new Integer(0);
		
		try {
			currentTime = TimeUtil.getCurrentTime();
			yyyymm = DateTimeUtil.getPreDay(currentTime, 20).substring(0, 6);
		} catch (Exception e) {
			e.printStackTrace();
		}
		currentTime = currentTime.substring(0, 6);
		logger.debug("yyyymm: " + yyyymm);
		
		String fileName = "(" + yyyymm + ")" + RESIDENTIAL + FILE_NAME; 
		List<PrepaymentLog> logList = prepaymentLogDao.getMonthlyConsumptionLog(currentTime, RESIDENTIAL);
		LinkedHashMap<String, Map<String, Object>> data = MonthlyConsumeData.makeExcelData(logList, RESIDENTIAL);
		
		logger.info("residential log count: "+ logList.size());
		MonthlyConsumeExcel.makeMonthlyReportExcel(REPORT_CON_TITLE, data, fileName, yyyymm, RESIDENTIAL);
		logger.info(fileName + "has created");
		
		fileName = "(" + yyyymm + ")" + NONRESIDENTIAL + FILE_NAME;
		logList = prepaymentLogDao.getMonthlyConsumptionLog(currentTime, NONRESIDENTIAL);
		data = MonthlyConsumeData.makeExcelData(logList,NONRESIDENTIAL);
		
		logger.info("nonResidential log count: "+ logList.size());
		MonthlyConsumeExcel.makeMonthlyReportExcel(REPORT_CON_TITLE, data, fileName, yyyymm, NONRESIDENTIAL);
		logger.info(fileName + "has created");
		
		
		Integer rootLocation = locationDao.getRoot().get(0);
		
		Supplier supplier = supplierDao.getSupplierByName(SUPPLIER);
		if ( supplier == null || supplier.getId() == null ) {
			supplierId = new Integer(22);
		} else {
			supplierId = supplier.getId();
		}
		
		logger.info("supplierId: " + supplierId); 
		logger.info("root location Id: " + rootLocation);
		
		fileName = "(" + yyyymm + ")" + REGIONAL + RESIDENTIAL + FILE_NAME;
		List regions = locationDao.getChildNodesInLocation(rootLocation, supplierId);
		data.clear();
		LinkedHashMap<String, Map<String, Object>> totalData = new LinkedHashMap<String, Map<String,Object>>();
		for (Object regionIdObj : regions ) {
			Integer regionId = Integer.parseInt(regionIdObj.toString());
			Location region = locationDao.get(regionId);
			String regionName = region.getName();
			List<Integer> subRegion = locationDao.getChildLocationId(regionId);
			logList = prepaymentLogDao.getMonthlyConsumptionLog(currentTime, RESIDENTIAL, subRegion);
			logger.info("Region: " + regionName);
			logger.info("regional residential log count: "+ logList.size()); 
			data.put(regionName, RegionalConsumeData.makeExcelData(logList,RESIDENTIAL));
			totalData.put(regionName, RegionalConsumeData.makeExcelData(logList,RESIDENTIAL));
			List districts = locationDao.getChildNodesInLocation(regionId, supplierId);
			
			for (Object districtObj : districts) {
			    Integer districtId = Integer.parseInt(districtObj.toString());
	            Location district = locationDao.get(districtId);
	            String districtName = district.getName();
    			List<Integer> subDistrict = locationDao.getChildLocationId(districtId);
    			logList = prepaymentLogDao.getMonthlyConsumptionLog(currentTime, RESIDENTIAL, subDistrict);
    			logger.info("District: " + districtName);
                logger.info("regional residential log count: "+ logList.size());            
                data.put(districtName, RegionalConsumeData.makeExcelData(logList,RESIDENTIAL));
			}
		}
		
		RegionalConsumeData.makeRegionalTotal(totalData, data, true);
        RegionalConsumeExcel.makeMonthlyReportExcel(REPORT_SAL_TITLE, data, fileName, yyyymm, RESIDENTIAL);
		
		data.clear();
	
		fileName = "(" + yyyymm + ")" + REGIONAL + NONRESIDENTIAL + FILE_NAME;
        data.clear();
        totalData.clear();
        for (Object regionIdObj : regions ) {
            Integer regionId = Integer.parseInt(regionIdObj.toString());
            Location region = locationDao.get(regionId);
            String regionName = region.getName();
            List<Integer> subRegion = locationDao.getChildLocationId(regionId);
            logList = prepaymentLogDao.getMonthlyConsumptionLog(currentTime, NONRESIDENTIAL, subRegion);
            logger.info("Region: " + regionName);
            logger.info("regional residential log count: "+ logList.size()); 
            data.put(regionName, RegionalConsumeData.makeExcelData(logList,NONRESIDENTIAL));
            totalData.put(regionName, RegionalConsumeData.makeExcelData(logList,NONRESIDENTIAL));
            List districts = locationDao.getChildNodesInLocation(regionId, supplierId);
            
            for (Object districtObj : districts) {
                Integer districtId = Integer.parseInt(districtObj.toString());
                Location district = locationDao.get(districtId);
                String districtName = district.getName();
                List<Integer> subDistrict = locationDao.getChildLocationId(districtId);
                logList = prepaymentLogDao.getMonthlyConsumptionLog(currentTime, NONRESIDENTIAL, subDistrict);
                logger.info("District: " + districtName);
                logger.info("regional residential log count: "+ logList.size());            
                data.put(districtName, RegionalConsumeData.makeExcelData(logList,NONRESIDENTIAL));
            }
        }
        
        RegionalConsumeData.makeRegionalTotal(totalData, data, true);
        RegionalConsumeExcel.makeMonthlyReportExcel(REPORT_SAL_TITLE, data, fileName, yyyymm, NONRESIDENTIAL);
        
        isNowRunning = false;
	}
}
