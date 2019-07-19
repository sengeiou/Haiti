package com.aimir.service.system.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.PlcQualityTestDao;
import com.aimir.dao.system.PlcQualityTestDetailDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.PlcQualityTest;
import com.aimir.model.system.PlcQualityTestDetail;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.system.PlcQualityTestManager;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.TimeUtil;

@Service(value = "PlcQualityTestManager")
@Transactional
public class PlcQualityTestManagerImpl implements PlcQualityTestManager {
	
    private static Log log = LogFactory.getLog(PlcQualityTestManagerImpl.class);
    
    @Autowired
    PlcQualityTestDao plcQualityTestDao;
    
    @Autowired
    PlcQualityTestDetailDao plcQualityTestDetailDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    CmdOperationUtil cmdOperationUtil;
    
    public List<Map<String,Object>> getReadExcelAsset(String savePath, String zigName) {
    	List<Map<String,Object>> returnData = new ArrayList<Map<String,Object>>();
    	
		String[] endFileName = {".xlsx", "_complete.xlsx", "_ing.xlsx", ".xls", "_complete.xls", "_ing.xls"};
		String ext = "";
		String fullPath = "";
		Boolean result = false;
		File file = null;
		try {
			for (int i = 0; i < endFileName.length; i++) {
				String fileName = zigName+endFileName[i]; 
				fullPath = savePath + "/" + fileName;
				File tempfile = new File(fullPath);
				if(tempfile.exists() && tempfile.canRead() && tempfile.isFile()) {
					result = true;
					file = tempfile;
					ext = endFileName[i].toLowerCase();
					break;
				}
			}
	
			if(!result) {
				return returnData;
			}
		} catch(Exception e) {
			log.error(e,e);
		}

		if(ext.endsWith(".xls")) {
			returnData = readXLS(file, zigName);
		} else if(ext.endsWith(".xlsx")) {
			returnData = readXLSX(file, zigName);
		}
		

    	return returnData;
    }
    
    private List<Map<String,Object>> readXLS(File file, String zigName) {
    	List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
    	try {
            // Workbook
			HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
	        ExcelExtractor extractorXLS = new ExcelExtractor(wb);
	        extractorXLS.setFormulasNotResults(true);
	        extractorXLS.setIncludeSheetNames(false);
	
	
	        int no = 1;
	        // Getting cell contents
	        // Getting cell contents
	        for (int i = 0; i < wb.getSheetAt(0).getLastRowNum(); i=+2) {
	        	Row row = wb.getSheetAt(0).getRow(i);
	        	Row nextRow = wb.getSheetAt(0).getRow(i+1);

	        	if(i == 0) {
	        		continue;
	        	}
	        	
	            Map<String, Object> tempMap = getFileMap(row, nextRow, zigName, no);
	            if(tempMap == null) {
	            	continue;
	            } else {
	            	resultList.add(tempMap);
	            	no++;
	            }
	        } // for end : Row
    	} catch(Exception e) {
    		log.error(e,e);
    	}
    	return resultList;
    }
    
    private List<Map<String, Object>> readXLSX(File file, String zigName) {
    	List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();

    	try {
			XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
			XSSFExcelExtractor extractorXLSX = new XSSFExcelExtractor(wb);
			extractorXLSX.setFormulasNotResults(true);
			extractorXLSX.setIncludeSheetNames(false);
	
	        int no =1;
	        // Getting cell contents
	        for (int i = 0; i <= wb.getSheetAt(0).getLastRowNum(); i=i+2) {
	        	Row row = wb.getSheetAt(0).getRow(i);
	        	Row nextRow = wb.getSheetAt(0).getRow(i+1);

	        	if(i == 0) {
	        		continue;
	        	}
	        	
	            Map<String, Object> tempMap = getFileMap(row, nextRow, zigName, no);
	            if(tempMap == null) {
	            	continue;
	            } else {
	            	resultList.add(tempMap);
	            	no++;
	            }
	        } // for end : Row
	
    	} catch(Exception e) {
    		log.error(e,e);
    	}
    	return resultList;
    }
    
    private Map<String, Object> getFileMap(Row row, Row nextRow, String zigName, int no) throws IOException {

    	//meterSerial과 modemSerial중 하나라도 없으면 저장하지 않음.
        if(row == null || nextRow == null) {
        	return null;
        }
    	
        Map<String, Object> returnData = new HashMap<String, Object>();

        Cell meterCell = row.getCell(0);
        Cell modemCell = nextRow.getCell(0);
        
        meterCell.setCellType(1);
        modemCell.setCellType(1);
        
        returnData.put("no",no);
        returnData.put("zigName",zigName);
        returnData.put("meterSerial", meterCell.getRichStringCellValue().getString());
        returnData.put("modemSerial", modemCell.getRichStringCellValue().getString());
        
        if (inValidList(returnData)) {
            return null;
        }
        return returnData;
    }

    public boolean inValidList(Map<String, Object> map){
        boolean meterNull = true;
        boolean modemNull = true;
        boolean bool = false;
        if(map.get("meterSerial") != null){
            if(!inValidNull(map.get("meterSerial").toString())){
            	meterNull = false;
            }
        }
        
        if(map.get("modemSerial") != null){    
            if(!inValidNull(map.get("modemSerial").toString())){
            	modemNull = false;
            }
        }
        // 둘중 하나라도 빈값이 들어오면 skip
        if(meterNull || modemNull) {
        	bool = true;
        }
        
        return bool;
    }
    
    public boolean inValidNull(String str){
        boolean bool = false;
        if (StringUtil.nullToBlank(str).isEmpty()) {
            bool = true;
        }
        return bool;
    }
    
    public List<Object> getPlcQualityResult(Map<String, Object> condition) {
    	List<Object> resultList = plcQualityTestDao.getPlcQualityResult(condition);
    	List<Map<String,Object>> gridData = (List<Map<String, Object>>) resultList.get(0);
        String supplierId = StringUtil.nullToBlank( condition.get("supplierId"));
        if (supplierId.length() > 0) {
            Supplier supplier = supplierDao.get(Integer.parseInt(supplierId));
            for (Map<String, Object> data : gridData) {
                Map<String, Object> map = data;
                String completeDate = StringUtil.nullToBlank(map.get("completeDate"));
                if(!completeDate.isEmpty() && completeDate.length() > 8) {
                	completeDate = completeDate.substring(0,8);
                }
                map.put("completeDate", TimeLocaleUtil.getLocaleDate(completeDate , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
                map.put("resultCnt", data.get("successCnt")+"/"+(Long.parseLong(data.get("totalCnt").toString()) - Long.parseLong(data.get("successCnt").toString())));
            }
        }
    	
    	return resultList;
    }
    
    public List<Object> getPlcQualityDetailResult(Map<String, Object> condition) {
    	return plcQualityTestDetailDao.getPlcQualityDetailResult(condition);
    }
    
    public Map<String, Object> getSummaryInfo(Map<String, Object> condition) {
    	Map<String, Object> returnData = new HashMap<String, Object>();
		List<Map<String, Object>> data = plcQualityTestDetailDao.getSummaryInfo(condition);
		returnData.put("data",data);
		int dataCnt = data.size();
		int total = 0;
		int totalFail = 0;
		int totalSuccess = 0;
		for (int i = 0; i < dataCnt; i++) {
			Map<String,Object> map = data.get(i);
			total = total + Integer.parseInt(map.get("cnt").toString());
			if(map.get("testResult") == null || map.get("testResult").toString()=="0" || map.get("testResult").toString() == "false") {
				totalFail = totalFail + Integer.parseInt(map.get("cnt").toString());
			} else {
				totalSuccess = totalSuccess + Integer.parseInt(map.get("cnt").toString());
			}
		}
		
		Map<String, Object> temp = new HashMap<String,Object>();
		temp.put("total", total);
		temp.put("totalFail", totalFail);
		temp.put("totalSuccess", totalSuccess);
		returnData.put("summary",temp);
    	return returnData;
    }

	@Override
	public void testStart(String filePath, String[] zigName) {

		for(String zig: zigName){
			try {
				cmdOperationUtil.cmdAssembleTestStart(zig);
			} catch (Exception e) {
				log.error(e,e);
			}
		}
	    
		String testStartDate = null;
		try {
			testStartDate = TimeUtil.getCurrentTime();
		} catch (ParseException e1) {
			log.error(e1,e1);
		}
		if(zigName == null || zigName.length < 1){
			log.info("Zig List is empty");
			return;
		}	
		
		for(String zig: zigName){
			PlcQualityTest plcQualityTest = plcQualityTestDao.getInfoByZig(zig);
			if(plcQualityTest != null){

				//이미 DB에 넣었으나 재 시도하는 경우.
				Boolean flag = false;
				String ext = ".xlsx";
				String[] fileListXLSX = {zig+ext, zig+"_complete"+ext};
				File newFile = new File(filePath+"/"+zig+"_ing"+ext);
				for (int i = 0; i < fileListXLSX.length; i++) {
					File originFile = new File(filePath+"/"+fileListXLSX[i]);
					if(originFile.exists()) {
						originFile.renameTo(newFile);
						flag = true;
						break;
					}
				}
				
				if(!flag) {
					ext = ".xls";
					String[] fileListXLS = {zig+ext, zig+"_complete"+ext};
					newFile = new File(filePath+"/"+zig+"_ing"+ext);
					for (int i = 0; i < fileListXLSX.length; i++) {
						File originFile = new File(filePath+"/"+fileListXLS[i]);
						if(originFile.exists()) {
							originFile.renameTo(newFile);
							break;
						}
					}
				}
				
				//기존 지그에 추가로 입력한다.
				List<Map<String,Object>> excelData = getReadExcelAsset(filePath,zig);
				List<PlcQualityTestDetail> details = new ArrayList<PlcQualityTestDetail>();
				for(Map<String,Object> data : excelData){

					PlcQualityTestDetail detail = new PlcQualityTestDetail();
					detail.setMeterSerial((String)data.get("meterSerial"));
					detail.setModemSerial((String)data.get("modemSerial"));
					detail.setTestStartDate(testStartDate);
					details.add(detail);
				}
				for(PlcQualityTestDetail detail : details){
					detail.setPlcQualityTest(plcQualityTest);
					detail.setZigId(plcQualityTest.getId());
					
	                Set<Condition> condition = new HashSet<Condition>();
	                condition.add(new Condition("meterSerial", new Object[]{ detail.getMeterSerial() }, null, Restriction.EQ));
	                condition.add(new Condition("modemSerial", new Object[]{ detail.getModemSerial() }, null, Restriction.EQ));	                
					List<PlcQualityTestDetail> list = plcQualityTestDetailDao.findByConditions(condition);
					
					if(list == null || list.size() < 1){
						plcQualityTestDetailDao.add(detail);
					} else {
						PlcQualityTestDetail pd = list.get(0);
						plcQualityTestDetailDao.delete(pd);
						plcQualityTestDetailDao.add(detail);
					}
				}
				
				Integer totalCount = plcQualityTestDao.getCount(plcQualityTest.getId());
				
				plcQualityTest.setTestEnable(true);
				plcQualityTest.setTotalCount(totalCount);
				plcQualityTestDao.update(plcQualityTest);
				log.info("plcQualityTestDao update");
				
			}else{
				log.info("plcQualityTestDao add start");
				List<Map<String,Object>> excelData = getReadExcelAsset(filePath,zig);
				List<PlcQualityTestDetail> details = new ArrayList<PlcQualityTestDetail>();
				for(Map<String,Object> data : excelData){

					PlcQualityTestDetail detail = new PlcQualityTestDetail();
					detail.setMeterSerial((String)data.get("meterSerial"));
					detail.setModemSerial((String)data.get("modemSerial"));
					detail.setTestStartDate(testStartDate);
					details.add(detail);
				}
				try{
					plcQualityTest = new PlcQualityTest();
					plcQualityTest.setZigName(zig);
					plcQualityTest.setTestEnable(true);
					plcQualityTest.setSuccessCount(0);
					plcQualityTest.setTotalCount(excelData.size());	
					PlcQualityTest addEntity = plcQualityTestDao.add(plcQualityTest);
					log.info("plcQualityTestDao add end");					
					
					for(PlcQualityTestDetail detail : details){
						detail.setPlcQualityTest(addEntity);
						detail.setZigId(addEntity.getId());
						plcQualityTestDetailDao.add(detail);
					}
					
					//testStart를 하는 경우 뒤에 _ing를 붙여 테스트 중 상태가된 파일임을 나타낸다.
					Boolean flag = false;
					String ext = ".xlsx";
					String[] fileListXLSX = {zig+ext, zig+"_complete"+ext};
					File newFile = new File(filePath+"/"+zig+"_ing"+ext);
					for (int i = 0; i < fileListXLSX.length; i++) {
						File originFile = new File(filePath+"/"+fileListXLSX[i]);
						if(originFile.exists()) {
							originFile.renameTo(newFile);
							flag = true;
							break;
						}
					}
					
					if(!flag) {
						ext = ".xls";
						String[] fileListXLS = {zig+ext, zig+"_complete"+ext};
						newFile = new File(filePath+"/"+zig+"_ing"+ext);
						for (int i = 0; i < fileListXLSX.length; i++) {
							File originFile = new File(filePath+"/"+fileListXLS[i]);
							if(originFile.exists()) {
								originFile.renameTo(newFile);
								break;
							}
						}
					}
				}catch(Exception e){
					log.error(e,e);
				}

			}
		}
	}

	@Override
	public void testEnd(String filePath, String[] zigName) {
		
		if(zigName == null || zigName.length < 1){
			log.info("Zig List is empty");
			return;
		}
		
		for(String zig: zigName){
			PlcQualityTest plcQualityTest = plcQualityTestDao.getInfoByZig(zig);
			if(plcQualityTest != null){
				log.info("update test result start ");
				List<PlcQualityTestDetail> details = plcQualityTest.getPlcQualityTestDetails();
				int successCount = 0;
				for(PlcQualityTestDetail detail: details){
					if(detail.getTestResult()!= null && detail.getTestResult()){
						successCount++;
					}
				}
				try{
					plcQualityTest.setTestEnable(false);
					plcQualityTest.setCompleteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
					plcQualityTest.setSuccessCount(successCount);
					plcQualityTestDao.update(plcQualityTest);
					log.info("update test result end ");
					
					//완료된 파일의 경우 파일명 뒤체 _complete를 붙여 테스트가 완료된 파일임을 나타낸다.
					String[] fileList = {zig+"_ing.xlsx", zig+"_ing.xls"};
					
					for (int i = 0; i < fileList.length; i++) {
						File originFile = new File(filePath+"/"+fileList[i]);
						if(originFile.exists()) {
							File newFile = new File(filePath+"/"+zig+"_complete"+fileList[i].substring(fileList[i].lastIndexOf(".")));
							originFile.renameTo(newFile);
							break;
						}
					}
				}catch(Exception e){
					log.error(e,e);
				}
			}
		}
	}
	
	public List<Map<String,Object>> checkResult(String zigName) {
		List<Map<String,Object>> checkList = plcQualityTestDetailDao.checkResult(zigName);
		try {
			String currenTime = TimeUtil.getCurrentTime();
			for (Map<String, Object> map : checkList) {
				Boolean timeout = false;
				String testStartDate = StringUtil.nullToZero(map.get("testStartDate"));
				if(testStartDate.length() > 14) {
					testStartDate = testStartDate.substring(0,14);
				} else if (testStartDate.length() < 14) {
					for (int i = 0; i < 14 - testStartDate.length(); i++) {
						testStartDate = testStartDate + "0";
					}
				}
				String timeoutTime;
			
				timeoutTime = TimeUtil.getAddMinute(testStartDate, 10);
			
				if(Long.parseLong(currenTime) >= Long.parseLong(timeoutTime)) {
					timeout = true;
				}
				map.put("timeout", timeout);
			}
		} catch (ParseException e) {
			log.error(e,e);
		}
		return checkList;
	}
	
	public int changeNullResult(Integer zigId, String testStartDate) {
		return plcQualityTestDetailDao.changeNullResult(zigId, testStartDate);
	}
}