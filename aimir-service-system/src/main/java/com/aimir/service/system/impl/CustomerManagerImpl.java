package com.aimir.service.system.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.EncryptionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.RegType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.DeviceRegistrationDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.model.device.DeviceRegLog;
import com.aimir.model.system.Customer;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.CustomerManager;
import com.aimir.service.system.RoleManager;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@WebService(endpointInterface = "com.aimir.service.system.CustomerManager")
@Service(value = "customerManager")
@Transactional
public class CustomerManagerImpl implements CustomerManager {
    
    private static Log logger = LogFactory.getLog(CustomerManagerImpl.class);
    
    @Autowired
    CustomerDao dao;
    @Autowired
    ContractDao contractDao;
    @Autowired
    CodeDao codeDao;
    @Autowired
    SupplierDao supplierDao;
    @Autowired
    CustomerDao customerDao;
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    DeviceRegistrationDao deviceRegistrationDao;

    @Autowired
    ContractChangeLogDao contractChangeLogDao;
    
    @Autowired
    OperatorDao operatorDao;
    
    @Autowired
    RoleManager roleManager;

    @SuppressWarnings("unused")
    private String ctxRoot; 
    @SuppressWarnings("unused")
    private int overLapCnt = 0;

    public void addCustomer(Customer customer) {
        dao.add(customer);
    }

    public void deleteCustomer(Customer customer) {
        dao.delete(customer);
    }

    public Customer getCustomer(Integer userId) {
        return dao.get(userId);
    }

    public List<Customer> getCustomers() {
        return dao.getAll();
    }
    
    public void update(Customer customer) {
        dao.update(customer);
    }

    public void updateCustomer(Customer customer) {
//      dao.updateCustomer(customer);
//        dao.update(customer);
        Customer curCustomer = dao.get(customer.getId());
        
        if(customer.getLoginId() == null || "".equals(customer.getLoginId()) 
                || (!customer.getLoginId().equals(curCustomer.getLoginId()))) {
            curCustomer.setIsFirstLogin(null);
            curCustomer.setLastPasswordChangeTime(null);
        }
        curCustomer.setCustomerNo(customer.getCustomerNo());
        curCustomer.setName(customer.getName());
        curCustomer.setLoginId(customer.getLoginId());
        curCustomer.setMobileNo(customer.getMobileNo());
        curCustomer.setTelephoneNo(customer.getTelephoneNo());
        curCustomer.setAddress(customer.getAddress());
        curCustomer.setAddress1(customer.getAddress1());
        curCustomer.setAddress2(customer.getAddress2());
        curCustomer.setAddress3(customer.getAddress3());
        curCustomer.setFamilyCnt(customer.getFamilyCnt());
        curCustomer.setCo2MileId(customer.getCo2MileId());
        curCustomer.setEmail(customer.getEmail());
        curCustomer.setSmsYn(customer.getSmsYn());
        curCustomer.setEmailYn(customer.getEmailYn());
        curCustomer.setDemandResponse(customer.getDemandResponse());
        curCustomer.setIdentityOrCompanyRegNo(customer.getIdentityOrCompanyRegNo());
        curCustomer.setInitials(customer.getInitials());
        curCustomer.setVatNo(customer.getVatNo());
        curCustomer.setWorkTelephone(customer.getWorkTelephone());
        curCustomer.setPostalAddressLine1(customer.getPostalAddressLine1());
        curCustomer.setPostalAddressLine2(customer.getPostalAddressLine2());
        curCustomer.setPostalSuburb(customer.getPostalSuburb());
        curCustomer.setPostalCode(customer.getPostalCode());
        curCustomer.setShowDefaultDashboard(customer.getShowDefaultDashboard());
        curCustomer.setLoginDenied(customer.getLoginDenied());
        curCustomer.setRole(customer.getRole());
        
        if((customer.getPassword() != null && !"".equals(customer.getPassword())) && !(customer.getPassword().equals(curCustomer.getPassword()))) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            curCustomer.setPassword(customer.getPassword());
            curCustomer.setLastPasswordChangeTime(formatter.format(Calendar.getInstance().getTime()));
        }
        
        dao.update(curCustomer);
    }
    
    public Customer getCustomersByLoginId(String loginId){
        return dao.getCustomersByLoginId(loginId);
    }

    public int idOverlapCheck(String customerNo) {
        return dao.idOverlapCheck(customerNo);
    }    
    
    public int loginIdOverlapCheck(String loginId, String customerNo) {
        return dao.loginIdOverlapCheck(loginId, customerNo);
    }

	/**
	 * method name : checkCustomerNoLoginMapping
     * method Desc : 입력받은 CustomerNumber가 다른 customer의 Login아이디와 매핑되어있는지 체크한다.
     * 
	 * @param customerNo
	 * @return
	 */
    public Map<String, String> checkCustomerNoLoginMapping(String customerNo) {
    	List<Map<String, String>> resultData = dao.checkCustomerNoLoginMapping(customerNo);
    	Map<String, String> returnMap = new HashMap<String, String>();
    	if(resultData.size() > 0) {
    		returnMap = resultData.get(0);
    	}
    	return returnMap;
    }

    public String createNewCustomerNumber() {
    	StringBuffer customerNumber = new StringBuffer("324");
    	customerNumber.append(dao.getNextId());
    	return customerNumber.toString();
    }
    
    public List<Customer> customerSearchList(String customerNo, String name, String first, String max) {        
        Set<Condition> set = new HashSet<Condition>();      
        Condition condition1 = new Condition("customerNo",new Object[]{"%"+customerNo+"%"},null,Restriction.LIKE);
        Condition condition2 = new Condition("name",new Object[]{"%"+name+"%"},null,Restriction.LIKE);
        Condition condition3 = new Condition(null,new Object[]{Integer.parseInt(first)},null,Restriction.FIRST);
        Condition condition4 = new Condition(null,new Object[]{Integer.parseInt(max)},null,Restriction.MAX);
        
        set.add(condition1);
        set.add(condition2);
        set.add(condition3);
        set.add(condition4);
        
        List<Customer> customerList = dao.customerSearchList(set);
        return customerList;
    }

    public Map<String,String> customerSearchListCount(String customerNo, String name) {
        Map<String,String> result = new HashMap<String,String>();        
        result.put("total", dao.customerSearchListCount(customerNo, name)+"");
        return result;  
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCustomerContractInfo(Map<String,Object> params) {

        Map<String, Object> result = new HashMap<String, Object>();
        String type = (String)params.get("type");
        String strSupplierId = (String)params.get("supplierId");

        Supplier supplier = supplierDao.get(Integer.parseInt(strSupplierId));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        Integer supplierId = 0;      
        if (!"".equals(StringUtil.nullToBlank(strSupplierId))) {
            supplierId = Integer.parseInt(strSupplierId);
        }
        params.put("supplierId", supplierId);
        params.put("today", TimeUtil.getCurrentTimeMilli());
        params.put("type", type);

        if ("".equals(StringUtil.nullToBlank(type))) {
            List<Object> totalGrid = contractDao.getContractCountByStatusCode(params);
            List<Object> todayGrid = contractDao.getContractCountForToday(params);

            Map<String, Object> chart = getChartData(totalGrid, todayGrid, supplierId);

            try {
                chart.put("today", TimeLocaleUtil.getLocaleDate(TimeUtil.getCurrentDay(), lang, country));
            } catch (ParseException e) {
                chart.put("today", TimeUtil.getCurrentTimeMilli());
            }

            result.put("chart", chart);
        }

        List<Object> grid = contractDao.getContractCountByTariffType(params);
        
        int gridSize = grid.size();
        DecimalFormat df = DecimalUtil.getMDStyle(supplier.getMd());
        
        for (int i = 0; i < gridSize; i++) {
			Map<String, Object> map = (Map<String, Object>) grid.get(i);
			map.put("tariffCount", map.get("tariffCount") == null? "" : df.format(map.get("tariffCount")));
			grid.remove(i);
			grid.add(i, map);
		}
        
        result.put("grid", grid);

        return result; 
    }
    
    public Map<String, Object> getCustomerContractInfoByparam(String supplierId, String yyyymmdd, String serviceType) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("supplierId", supplierId);
        params.put("yyyyMMdd", yyyymmdd);
        params.put("serviceType", serviceType);
        return getCustomerContractInfo(params);
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> getChartData(List<Object> totalGrid, List<Object> todayGrid, Integer supplierId) {

        long total = 0;
        long value = 0;
        String status = null;
        Supplier supplier = supplierDao.get(supplierId);
        
        DecimalFormat df = DecimalUtil.getMDStyle(supplier.getMd());
        
        Map<String, Object> row = null;
        Map<String, Object> chart = new HashMap<String, Object>();
        
        for(Object obj:totalGrid){
            row = (Map<String, Object>)obj;
            status = (String)row.get("statusCode");
            value  = (Long)row.get("statusCount");
            total += value;
            
            
            if(status.equals(CommonConstants.ContractStatus.NORMAL.getCode())){
                chart.put("normal", value);
                chart.put("normalFormat", df.format(value));
            }
            else if(status.equals(CommonConstants.ContractStatus.SUSPENDED.getCode())){
                chart.put("suspended", value);
                chart.put("suspendedFormat", df.format(value));
            }
            else if(status.equals(CommonConstants.ContractStatus.PAUSE.getCode())){
                chart.put("pause", value);
                chart.put("pauseFormat", df.format(value));
            }
            else if(status.equals(CommonConstants.ContractStatus.STOP.getCode())){
                chart.put("stop", value);
                chart.put("stopFormat", df.format(value));
            }
            else if(status.equals(CommonConstants.ContractStatus.CANCEL.getCode())){
                chart.put("cancel", value);
                chart.put("cancelFormat", df.format(value));
            }
            else {
                chart.put("unknown", value);
                chart.put("unknownFormat", df.format(value));
            }
        }
        if(!chart.containsKey("normal")){
            chart.put("normal", 0);
            chart.put("normalFormat", 0);
        }
        if(!chart.containsKey("suspended")){
            chart.put("suspended", 0);
            chart.put("suspendedFormat", 0);
        }
        if(!chart.containsKey("pause")){
            chart.put("pause", 0);
            chart.put("pauseFormat", 0);
        }
        if(!chart.containsKey("stop")){
            chart.put("stop", 0);
            chart.put("stopFormat", 0);
        }
        if(!chart.containsKey("cancel")){
            chart.put("cancel", 0);
            chart.put("cancelFormat", 0);
        }
        if(!chart.containsKey("unknown")){
            chart.put("unknown", 0);
            chart.put("unknownFormat", 0);
        }
        
        chart.put("totalCountFormat", df.format(total));
        
        // flex 화면에서 Login한 사용자 포맷으로 형변환
        chart.put("today", TimeUtil.getCurrentTimeMilli());     


        for(Object obj:todayGrid){
            row = (Map<String, Object>)obj;
            
            value = 0;
            if(row.containsKey(CommonConstants.ContractStatus.NORMAL.getCode())){
                chart.put("todayNormal", (Long)row.get(CommonConstants.ContractStatus.NORMAL.getCode()));
            }
            if(row.containsKey(CommonConstants.ContractStatus.CANCEL.getCode())){
                chart.put("todayCancel", (Long)row.get(CommonConstants.ContractStatus.CANCEL.getCode()));
            }
        }
        if(!chart.containsKey("todayNormal")){
            chart.put("todayNormal", 0);
        }
        if(!chart.containsKey("todayCancel")){
            chart.put("todayCancel", 0);
        }

        return chart; 
    }

//    public void customerDelete(Integer id) {
//        dao.deleteById(id);     
//    }

    public void customerDelete(Integer customerId) {
        contractChangeLogDao.contractLogAllDelete(customerId);
        contractDao.contractAllDelete(customerId);
        dao.deleteById(customerId);
    }

    public Integer getTotalCustomer(Map<String, Object> conditionMap) {
        String locationId = StringUtil.nullToBlank(conditionMap.get("location"));
        List<Integer> locationIdList = null;

        if (!locationId.isEmpty()) {
            locationIdList = locationDao.getChildLocationId(Integer.valueOf(locationId));
            locationIdList.add(Integer.valueOf(locationId));
            conditionMap.put("locationIdList", locationIdList);
        }

        return dao.getTotalCustomer(conditionMap);
    }

    public Integer getTotalCustomerByParam(String customerName,String location,
            String mdsId,String address,String serviceType,String supplierId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("customerName", customerName);
        params.put("location", location);
        params.put("mdsId", mdsId);
        params.put("address", address);
        params.put("serviceType", serviceType);
        params.put("supplierId", supplierId);
        return getTotalCustomer(params);
    }
    
    public String getTitleName(String excel, String ext) {
        StringBuffer sb = new StringBuffer();
        
        try {
            // check file
            File file = new File(excel.trim());
            Row titles = null;

            if("xls".equals(ext)){
                // Workbook
                HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));
                titles = (wb.getSheetAt(0)).getRow(0);
            }
            else if("xlsx".equals(ext)){
                // Workbook
                XSSFWorkbook wb = new XSSFWorkbook(excel.trim());   
                titles = (wb.getSheetAt(0)).getRow(0);
            }

            for( Cell cell : titles ) {
                
                if(cell.getColumnIndex() > 0 ) sb.append(',');
                
                sb.append( cell.getRichStringCellValue().getString() );
            }
            
        }catch(IOException ie){
            ie.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return sb.toString();
    }

    /** 
     * XLS file Read
     * @param filePath
     * @return 엑셀데이터 반환
     */
    public Map<String, Object> readExcelXLS(String excel) {

        Map<String, Object> result = new HashMap<String, Object>();

        try {
            ctxRoot = excel.substring(0, excel.lastIndexOf("/"));

            // check file
            File file = new File(excel.trim()); // jhkim trim() 추가

            // Workbook
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));

            // Text Extraction
            ExcelExtractor extractor = new ExcelExtractor(wb);
            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);

            // Excel 전체 출력
            logger.debug(extractor.getText());

            List<Object> resultList = new ArrayList<Object>();
            Row titles = null;

            // Getting cell contents
            for (Row row : wb.getSheetAt(0)) {

                if (row.getRowNum() == 0) {
                    titles = row;
                    continue;
                }
                Map<String, Object> tempMap = getFileMap(titles, row);
                if (tempMap == null) {
                    continue;
                } else {
                    resultList.add(tempMap);
                }
            } // for end : Row

            result.put("file", resultList);
        } catch(IOException ie) {
            ie.printStackTrace();
            logger.error(ie.toString(), ie);
        } catch(Exception e) {
            e.printStackTrace();
            logger.error(e.toString(), e);
        }

        return result;
    }
    
    /** 
     * XLSX file Read
     * @param filePath
     * @return 엑셀데이터 반환
     */
    public Map<String, Object> readExcelXLSX(String excel) {

        Map<String, Object> result = new HashMap<String, Object>();

        try {
            ctxRoot = excel.substring(0, excel.lastIndexOf("/"));

            // check file
            File file = new File(excel.trim());
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                throw new IOException(excel.trim());
            }

            // Workbook
            XSSFWorkbook wb = new XSSFWorkbook(excel.trim());

            // Text Extraction
            XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);
            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);

            // Excel 전체 출력
            logger.debug(extractor.getText());

            List<Object> resultList = new ArrayList<Object>();

            Row titles = null;

            // Getting cell contents
            for (Row row : wb.getSheetAt(0)) {

                if (row.getRowNum() == 0) {
                    titles = row;
                    continue;
                }

                Map<String, Object> tempMap = getFileMap(titles, row);
                if (tempMap == null) {
                    continue;
                } else {
                    resultList.add(tempMap);
                }

            } // for end : Row

            result.put("file", resultList);
        } catch(IOException ie) {
            ie.printStackTrace();
            logger.error(ie.toString(), ie);
        } catch(Exception e) {
            e.printStackTrace();
            logger.error(e.toString(), e);
        }

        return result;
    }

    /** 
     * XLS file Save
     * @param filePath
     * @return 엑셀데이터 등록 결과 반환
     */
    @SuppressWarnings("unused")
    @Transactional(readOnly=false)
    public Map<String, Object> saveExcelXLS(String excel, int supplierId) {

        Map<String, Object> result = new HashMap<String, Object>();

        try {
            ctxRoot = excel.substring(0, excel.lastIndexOf("/"));

            // check file
            File file = new File(excel.trim()); // jhkim trim() 추가

            // Workbook
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));

            // Text Extraction
            ExcelExtractor extractor = new ExcelExtractor(wb);
            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);

            // Excel 전체 출력
            logger.debug(extractor.getText());

            List<Object> resultList = new ArrayList<Object>();
            List<String> failCustomerNo = new ArrayList<String>();
            
            
            int totCnt = 0;
            int overLapCnt = 0;

            Row titles = null;
            Supplier supplier = supplierDao.get(supplierId);
            // Getting cell contents
            for (Row row : wb.getSheetAt(0)) {

                if (row.getRowNum() == 0) {
                    titles = row;
                    continue;
                }

                if (listNullCheck(titles, row) != null) {
                    totCnt++;
                    ResultStatus insertResult = ResultStatus.SUCCESS;
                    Map<String, Object> map = getCustomerList(titles, row);

                    if (map == null) {
                        overLapCnt++;
                        
                        failCustomerNo.add(row.getCell(1).getRichStringCellValue().getString());                        
                        continue;
                    }

                    Boolean isUpdate = (Boolean)map.get("isUpdate");
                    Customer customer = (Customer)map.get("customer");

                    try {
                        if (isUpdate) {
                            String[] sarr = {customer.getCustomerNo()};
                            Customer updCustomer = customerDao.getCustomersByCustomerNo(sarr).get(0);

                            mergeExcelCustomer(customer, updCustomer);                            
                            customerDao.update(updCustomer);
                        } else {
                            customerDao.add(customer);
                        }
                    } catch(Exception e) {
                        insertResult = ResultStatus.FAIL;
                        logger.error(e.toString(), e);
                        throw new Exception("저장에 실패 하였습니다.");
                    } finally {
                        if (!isUpdate) {
                            Map<String, Object> logData = new HashMap<String, Object>();
                            // 로그 저장
                            logData.put("deviceType", TargetClass.Customer);
                            logData.put("deviceName", customer.getCustomerNo());
                            logData.put("deviceModel", null);
                            logData.put("resultStatus", insertResult);
                            logData.put("regType", RegType.Bulk);
                            logData.put("supplier", supplier);
                            insertDeviceRegLog(logData);
                        }
                    }
                }
            } // for end : Row

            if(totCnt != (totCnt - overLapCnt)){
            	result.put("resultMsg", "Total: " + totCnt + ", Success: " + (totCnt - overLapCnt) + ", Please check log.");
            	logger.info("############# Fail Customer Number ##########");
            	for(String no : failCustomerNo){
                	logger.info(no);            		
            	}
            	logger.info("#############################################");            	
            }else{
            	result.put("resultMsg", "Total: " + totCnt + ", Success: " + (totCnt - overLapCnt));
            }
        } catch(IOException ie) {
            ie.printStackTrace();
            logger.error(ie.toString(), ie);
        } catch(Exception e) {
            e.printStackTrace();
            logger.error(e.toString(), e);
        }

        return result;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertDeviceRegLog(Map<String, Object> insertData) {
        // DeviceLog 객체 생성
        DeviceRegLog deviceRegLog = new DeviceRegLog();

        try {
            deviceRegLog.setCreateDate(TimeUtil.getCurrentTime());
        } catch(ParseException e) {
            e.printStackTrace();
        }
        deviceRegLog.setDeviceType((TargetClass)insertData.get("deviceType"));
        deviceRegLog.setDeviceName((String)insertData.get("deviceName"));
        deviceRegLog.setDeviceModel((DeviceModel)insertData.get("deviceModel"));
        deviceRegLog.setResult((ResultStatus)insertData.get("resultStatus"));
        deviceRegLog.setRegType((RegType)insertData.get("regType"));
        if ((Supplier)insertData.get("supplier") != null) {
            deviceRegLog.setSupplier((Supplier)insertData.get("supplier"));
        }

        // DeviceLog 등록
        deviceRegistrationDao.add(deviceRegLog);
        deviceRegistrationDao.flushAndClear();
    }
    
    /** 
     * XLSX file Save
     * @param filePath
     * @return 엑셀데이터 등록 결과 반환
     */
    
    @SuppressWarnings("unused")
    @Transactional(readOnly=false)
    public Map<String, Object> saveExcelXLSX(String excel, int supplierId) {

        Map<String, Object> result = new HashMap<String, Object>();

        try {
            ctxRoot = excel.substring(0, excel.lastIndexOf("/"));

            // check file
            File file = new File(excel.trim());
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                throw new IOException(excel.trim());
            }

            // Workbook
            XSSFWorkbook wb = new XSSFWorkbook(excel.trim());

            // Text Extraction
            XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);
            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);

            // Excel 전체 출력
            logger.debug(extractor.getText());

            List<Object> resultList = new ArrayList<Object>();
            List<String> failCustomerNo = new ArrayList<String>();
            int totCnt = 0;
            int overLapCnt = 0;

            Row titles = null;
            Supplier supplier = supplierDao.get(supplierId);
            // Getting cell contents
            for (Row row : wb.getSheetAt(0)) {

                if (row.getRowNum() == 0) {
                    titles = row;
                    continue;
                }
                if (listNullCheck(titles, row) != null) {
                    totCnt++;
                    ResultStatus insertResult = ResultStatus.SUCCESS;
                    Map<String, Object> map = getCustomerList(titles, row);

                    if (map == null) {
                        overLapCnt++;
                        
                        failCustomerNo.add(row.getCell(1).getRichStringCellValue().getString());      
                        continue;
                    }

                    Boolean isUpdate = (Boolean)map.get("isUpdate");
                    Customer customer = (Customer)map.get("customer");

                    try {
                        if (isUpdate) {
                            String[] sarr = {customer.getCustomerNo()};
                            Customer updCustomer = customerDao.getCustomersByCustomerNo(sarr).get(0);

                            mergeExcelCustomer(customer, updCustomer);
                            customerDao.update(updCustomer);
                        } else {
                            customerDao.add(customer);
                        }
                    } catch(Exception e) {
                        insertResult = ResultStatus.FAIL;
                        logger.error(e.toString(), e);
                        throw new Exception("저장에 실패 하였습니다.");
                    } finally {
                        if (!isUpdate) {
                            Map<String, Object> logData = new HashMap<String, Object>();
                            // 로그 저장
                            logData.put("deviceType", TargetClass.Customer);
                            logData.put("deviceName", customer.getCustomerNo());
                            logData.put("deviceModel", null);
                            logData.put("resultStatus", insertResult);
                            logData.put("regType", RegType.Bulk);
                            logData.put("supplier", supplier);

                            insertDeviceRegLog(logData);
                        }
                    }
                }
            } // for end : Row

            if(totCnt != (totCnt - overLapCnt)){
            	result.put("resultMsg", "Total: " + totCnt + ", Success: " + (totCnt - overLapCnt) + ", Please check log.");
            	logger.info("############# Fail Customer Number ##########");
            	for(String no : failCustomerNo){
                	logger.info(no);            		
            	}
            	logger.info("#############################################");            	
            }else{
            	result.put("resultMsg", "Total: " + totCnt + ", Success: " + (totCnt - overLapCnt));
            }
        } catch(IOException ie) {
            ie.printStackTrace();
            logger.error(ie.toString(), ie);
        } catch(Exception e) {
            e.printStackTrace();
            logger.error(e.toString(), e);
        }

        return result;
    }

//    private Customer getCustomerList(Row titles, Row row) {
//
//        String colName = null;
//        String colValue = null;
//        int nullCnt = 0;
//        Customer customer = new Customer();
//
//        for (Cell cell : row) {
//
//            colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString();
//
//            switch (cell.getCellType()) {
//                case Cell.CELL_TYPE_STRING:
//                    colValue = cell.getRichStringCellValue().getString();
//                    break;
//
//                case Cell.CELL_TYPE_NUMERIC:
//                    if (DateUtil.isCellDateFormatted(cell)) {
//                        colValue = cell.getDateCellValue().toString();
//                    } else {
//                        Long roundVal = Math.round(cell.getNumericCellValue());
//                        Double doubleVal = cell.getNumericCellValue();
//                        if (doubleVal.equals(roundVal.doubleValue())) {
//                            colValue = String.valueOf(roundVal);
//                        } else {
//                            colValue = String.valueOf(doubleVal);
//                        }
//                    }
//                    break;
//
//                case Cell.CELL_TYPE_BOOLEAN:
//                    colValue = String.valueOf(cell.getBooleanCellValue());
//                    break;
//
//                case Cell.CELL_TYPE_FORMULA:
//                    colValue = cell.getCellFormula();
//                    break;
//
//                default:
//                    colValue = "";
//            }
//
//            if (colName.equals("supplier")) {
//                Supplier supplier = supplierDao.getSupplierByName(colValue.toString());
//                if (supplier.getId() != null) {
//                    customer.setSupplier(supplier);
//                } else {
//                    return null;
//                }
//                nullCnt++;
//            } else if (colName.equals("customerNo")) { // 고객번호 중복 및 Null 체크
//                if (idOverlapCheck(colValue) == 0) {
//                    customer.setCustomerNo(colValue);
//                } else {
//                    return null;
//                }
//                nullCnt++;
//            } else if (colName.equals("name")) { // Null 체크
//                customer.setName(colValue);
//                nullCnt++;
//            }
//            // Address Start
//            else if (colName.equals("address")) { // ZipCode
//                customer.setAddress(colValue);
//                nullCnt++;
//            } else if (colName.equals("address1")) { // Street No, Street Name
//                customer.setAddress1(colValue);
//                nullCnt++;
//            } else if (colName.equals("address2")) { // Address Line 3, Flat/Unit Reference
//                customer.setAddress2(colValue);
//            } else if (colName.equals("address3")) { // Eft Numnber
//                customer.setAddress3(colValue);
//            }
//            // Address End
//            else if (colName.equals("email")) { // E-Mail 형식 체크, E-Mail이 널이 아닐경우만 체크하도록 수정
//                if (colValue.length() > 0 && !isValidEmail(colValue.toString()))
//                    return null;
//                customer.setEmail(colValue);
//            } else if (colName.equals("emailYn")) { // 0,1 형식 체크
//                if (!isValidYn(colValue))
//                    return null;
//                customer.setEmailYn(Integer.parseInt(colValue));
//            } else if (colName.equals("telephoneNo")) {
//                customer.setTelephoneNo(colValue);
//            } else if (colName.equals("mobileNo")) {
//                customer.setMobileNo(colValue);
//            } else if (colName.equals("smsYn")) { // 0,1 형식 체크
//                if (!isValidYn(colValue))
//                    return null;
//                customer.setSmsYn(Integer.parseInt(colValue));
//            } else if (colName.equals("demandResponse")) { // 0,1 형식 체크
//                if (!isValidYn(colValue))
//                    return null;
//                customer.setDemandResponse(Boolean.parseBoolean(colValue));
//            } /*
//               * else if (colName.equals("customTypeCode")){
//               * customer.setCustomTypeCode(codeDao.getCodesByName(colValue).get(0)); }
//               */
//            // 남아공 추가 요구사항 필드
//            else if (colName.equals("identityOrCompanyRegNo")) {
//                customer.setIdentityOrCompanyRegNo(colValue);
//                // nullCnt++;
//            } else if (colName.equals("initials")) {
//                customer.setInitials(colValue);
//            } else if (colName.equals("vatNo")) {
//                customer.setVatNo(colValue);
//            } else if (colName.equals("workTelephone")) {
//                customer.setWorkTelephone(colValue);
//            } else if (colName.equals("postalAddressLine1")) {
//                customer.setPostalAddressLine1(colValue);
//            } else if (colName.equals("postalAddressLine2")) {
//                customer.setPostalAddressLine2(colValue);
//            } else if (colName.equals("postalSuburb")) {
//                customer.setPostalSuburb(colValue);
//            } else if (colName.equals("postalCode")) {
//                customer.setPostalCode(colValue);
//            }
//        }
//        if (nullCnt != 5) {
//            return null; // 고객번호, 이름, 주소, 주소1, identityOrCompanyRegNo, supplier 의 값이 없을경우 null리턴
//        }
//        return customer;
//    }

    /**
     * method name : getCustomerList<b/>
     * method Desc : Customer Bulk 등록하기 위해 Excel 의 한 Row 를 Customer 객체로 변환한다.
     *
     * @param titles
     * @param row
     * @return
     */
    private Map<String, Object> getCustomerList(Row titles, Row row) {
        Map<String, Object> map = new HashMap<String, Object>();
        String colName = null;
        String colValue = null;
        boolean isUpdate = false;
        int nullCnt = 0;    // not null field count
        Customer customer = new Customer();
        
        
        for (Cell cell : row) {
        	if(titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")){
        		break;
        	}
            colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();

            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    colValue = cell.getRichStringCellValue().getString();
                    break;

                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        colValue = cell.getDateCellValue().toString();
                    } else {
                        Long roundVal = Math.round(cell.getNumericCellValue());
                        Double doubleVal = cell.getNumericCellValue();
                        if (doubleVal.equals(roundVal.doubleValue())) {
                            colValue = String.valueOf(roundVal);
                        } else {
                            colValue = String.valueOf(doubleVal);
                        }
                    }
                    break;

                case Cell.CELL_TYPE_BOOLEAN:
                    colValue = String.valueOf(cell.getBooleanCellValue());
                    break;

                case Cell.CELL_TYPE_FORMULA:
                    colValue = cell.getCellFormula();
                    break;

                default:
                    colValue = "";
            }
            
            if(colValue != null) colValue = colValue.trim();

            if (colName.equals("supplier")) {
                Supplier supplier = supplierDao.getSupplierByName(colValue.toString());
                if (supplier.getId() != null) {
                    customer.setSupplier(supplier);
                } else {                	
                    return null;
                }
                nullCnt++;
            } else if (colName.equals("customerNo")) { // 고객번호 중복 및 Null 체크
                if (StringUtil.nullToBlank(colValue).isEmpty()) {
                    return null;
                } else if (idOverlapCheck(colValue) > 0) {
                    isUpdate = true;
                }
                customer.setCustomerNo(colValue);
                nullCnt++;
            } else if (colName.equals("name")) { // Null 체크
                customer.setName(colValue);
                nullCnt++;
            }
            // Address Start
            else if (colName.equals("address")) { // ZipCode
                customer.setAddress(colValue);
                nullCnt++;
            } else if (colName.equals("address1")) { // Street No, Street Name
                customer.setAddress1(colValue);
                nullCnt++;
            } else if (colName.equals("address2")) { // Address Line 3, Flat/Unit Reference
                customer.setAddress2(colValue);
                nullCnt++;
            } else if (colName.equals("address3")) { // Eft Numnber
                customer.setAddress3(colValue);
            }
            // Address End
            else if (colName.equals("email")) { // E-Mail 형식 체크, E-Mail이 널이 아닐경우만 체크하도록 수정
                if (colValue.length() > 0) {
                    if (!isValidEmail(colValue.toString())) {
                        return null;
                    } else {
                        customer.setEmail(colValue);
                    }
                }
            } else if (colName.equals("emailYn")) { // 0,1 형식 체크
            	if (colValue.length() > 0) {
                    if (!isValidYn(colValue)) {
                        return null;
                    } else {
                        customer.setEmailYn(Integer.parseInt(convertValidYn(colValue)));
                    }            		
            	}
            } else if (colName.equals("telephoneNo")) {
                customer.setTelephoneNo(colValue);
            } else if (colName.equals("mobileNo")) {
                customer.setMobileNo(colValue);
            } else if (colName.equals("smsYn")) { // 0,1 형식 체크
            	if (colValue.length() > 0) {
                    if (!isValidYn(colValue)) {
                        return null;
                    } else {
                        customer.setSmsYn(Integer.parseInt(convertValidYn(colValue)));
                    }            		
            	}
            } else if (colName.equals("demandResponse")) { // 0,1 형식 체크
            	if (colValue.length() > 0) {
                    if (!isValidYn(colValue)) {
                        return null;
                    } else {
                        customer.setDemandResponse(Boolean.parseBoolean(convertValidYn(colValue)));
                    }            		
            	}
            }
            // 남아공 추가 요구사항 필드
            else if (colName.equals("identityOrCompanyRegNo")) {
                customer.setIdentityOrCompanyRegNo(colValue);
            } else if (colName.equals("initials")) {
                customer.setInitials(colValue);
            } else if (colName.equals("vatNo")) {
                customer.setVatNo(colValue);
            } else if (colName.equals("workTelephone")) {
                customer.setWorkTelephone(colValue);
            } else if (colName.equals("postalAddressLine1")) {
                customer.setPostalAddressLine1(colValue);
            } else if (colName.equals("postalAddressLine2")) {
                customer.setPostalAddressLine2(colValue);
            } else if (colName.equals("postalSuburb")) {
                customer.setPostalSuburb(colValue);
            } else if (colName.equals("postalCode")) {
                customer.setPostalCode(colValue);
            }
            
            // 로그인 아이디 추가 기능
            else if("loginId".equals(colName)){
            	customer.setLoginId(colValue.trim());
            } else if("passwd".equals(colName)){
            	customer.setPassword(colValue.trim());
            	
            	customer.setRole(roleManager.getRoleByName("customer"));
                customer.setShowDefaultDashboard(true);
                customer.setLoginDenied(false);
            }
        }
        
        /*
         * 필수항목 체크
         */
        if (nullCnt != 6) {
            return null; // 공급사, 고객번호, 이름, 주소, 주소1, 주소2 의 값이 없을경우 null리턴
        }
        
        /*
         * 중복 로그인아이디 체크
         */
        if(customer.getLoginId() != null && !customer.getLoginId().equals("")){   
        	if(customer.getCustomerNo() == null || customer.getCustomerNo().equals("")){  // 로그인아이디 추가시 고객번호가 없으면 실패
        		return null;  // 고객번호 없으면 null
        	}else {
        		// 중복아이디 체크
        		int count = dao.loginIdOverlapCheck(customer.getLoginId(), customer.getCustomerNo()); // 0이상이면 중복
                boolean isNotDupId = operatorDao.checkDuplicateLoginId(customer.getLoginId()); // false면 중복
                
                if(0 < count || !isNotDupId){
                	logger.info("[" + customer.getLoginId() + "] is already exists. please check Login ID.");
                	return null;     // 중복 로그인 아이디가 있으면 null
                }        		
        	}
        }
        
        /*
         * 비밀번호 암호화
         */
        if(customer.getPassword() != "" && customer.getPassword() != null){
            try {
                AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
				customer.setPassword(instance.hashPassword(customer.getPassword(), customer.getLoginId()));				
			} catch (EncryptionException e) {
				logger.debug("[" + customer.getLoginId() + "] Password Encryption Error - " + e.getMessage());
			}
        }

        map.put("isUpdate", isUpdate);
        map.put("customer", customer);
        return map;
    }

    private Map<String, Object> getFileMap(Row titles, Row row) throws IOException {

        Map<String, Object> returnData = new HashMap<String, Object>();
        String colName = null;
        String colValue = null;
        String status = "Success";
        int nullCnt = 0;    // not null count

        String customerNo = "";
        String loginId = "";
        String passwd = "";
        
        for (Cell cell : row) {
            cell.setCellType(1);
            if(titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")){
            	break;
            }
            colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();
            colValue = cell.getRichStringCellValue().getString().trim();
            boolean bool = inValidNull(colValue);

            if ("customerNo".equals(colName)) { // customerNo 중복 체크
//                if (idOverlapCheck(colValue) > 0) {
//                    status = "Failure";
//                    if (colValue.length() > 0)
//                        colValue = colValue + " (!)";
//                }
//                if (!bool)
//                    nullCnt++; // Null check
                if (!bool) {
                    nullCnt++; // Null check

                    if (status == "Success" && idOverlapCheck(colValue) > 0) {
                        status = "Update";
                    }
                    
                    customerNo = colValue;
                }
            } else if (colName.equals("supplier")) {
//                if (colValue.toString() == null || colValue.toString() == "") {
//                    status = "Failure";
//                } else {
//                    Supplier tmpSupplier = supplierDao.findByCondition("name", colValue.toString());
//
//                    if (tmpSupplier == null) {
//                        status = "Failure";
//                        if (!("".equals(colValue)))
//                            colValue = colValue + " (!)";
//                    }
//                }
//                if (!bool)
//                    nullCnt++;
                if (!bool) {
                    nullCnt++;
                    Supplier tmpSupplier = supplierDao.findByCondition("name", colValue.toString());

                    if (tmpSupplier == null) {
                        status = "Failure";
                        colValue = colValue + " (!)";
                    }
                }
            } else if ("name".equals(colName)) {
                if (!bool) {
                    nullCnt++; // Null check
                }
            } else if ("address2".equals(colName)) {
                if (!bool) {
                    nullCnt++; // Null check                  
                }
            } else if ("address1".equals(colName)) {
                if (!bool) {
                    nullCnt++; // Null check
                }
            } else if ("address".equals(colName)) {
                if (!bool) {
                    nullCnt++; // Null check
                }
            } else if ("email".equals(colName)) {
                // email에 값이 입력되어있을 때만 체크를 실시하도록 수정 - by eunmiae
                if (colValue.length() > 0 && !isValidEmail(colValue.toString())) {
                    status = "Failure";
                    if (colValue.length() > 0) {
                        colValue = colValue + " (!)";
                    }
                }
            } else if ("emailYn".equals(colName)) {
            	if(colValue.equals("")){
            		colValue = "N";
            	}else{
                    if (!isValidYn(colValue)) {
                        status = "Failure";
                        if (colValue.length() > 0) {
                            colValue = colValue + " (!)";
                        }
                    } else {
                        if (colValue.equals("0")) {
                            colValue = "N";
                        } else {
                            colValue = "Y";
                        }
                    }            		
            	}
            } else if ("smsYn".equals(colName)) {
            	if(colValue.equals("")){
            		colValue = "N";
            	}else{
                    if (!isValidYn(colValue)) {
                        status = "Failure";
                        if (colValue.length() > 0) {
                            colValue = colValue + " (!)";
                        }
                    } else {
                        if (colValue.equals("0")) {
                            colValue = "N";
                        } else {
                            colValue = "Y";
                        }
                    }            		
            	}
            } else if ("demandResponse".equals(colName)) {
            	if(colValue.equals("")){
            		colValue = "N";
            	}else{
                    if (!isValidYn(colValue)) {
                        status = "Failure";
                        if (colValue.length() > 0) {
                            colValue = colValue + " (!)";
                        }
                    } else {
                        if (colValue.equals("0")) {
                            colValue = "N";
                        } else {
                            colValue = "Y";
                        }
                    }            		
            	}

            } else if("loginId".equals(colName)){
            	loginId = colValue.trim();
            } else if("passwd".equals(colName)){
            	passwd = colValue.trim();
            } 
            /*
              * else if("customTypeCode".equals(colName)){ Code tmpCode = codeDao.findByCondition("code", colValue.toString());
              * if(tmpCode != null){ colValue = tmpCode.getName(); }else{ status = "Failure"; if(colValue.length() > 0) colValue
              * = colValue+" (!)"; }
              * 
              * }else if("identityOrCompanyRegNo".equals(colName)){ if (!bool) nullCnt++; //Null check
              * 
              * }
              */

            returnData.put(colName, colValue);
        } // for end : Cell
        
        /*
         * 필수항목 체크
         */
        if (nullCnt != 6) {
            status = "Failure"; // customerNo, name, supplier, address, address1, address2 값이 null일경우 실패
        }
        
        /*
         * 로그인 체크
         */
        if(!loginId.equals("")){ 
        	if(customerNo.equals("")){  // 로그인아이디 추가시 고객번호가 없으면 실패
        		status = "Failure";    
        	}else {
        		// 중복아이디 체크
        		int count = dao.loginIdOverlapCheck(loginId, customerNo); // 0이상이면 중복
                boolean isNotDupId = operatorDao.checkDuplicateLoginId(loginId); // false면 중복
                
                if(0 < count || !isNotDupId){
                	status = "Failure";
                	returnData.put("loginId", loginId = loginId + " (!)");
                }        		
        	}
        }
        
        returnData.put("Status", status);
        if (inValidList(returnData)) {
            return null;
        }
        return returnData;
    }

    private Map<String,Object> listNullCheck(Row titles, Row row) throws IOException{
        
        Map<String,Object> returnData = new HashMap<String,Object>();
        String colName  = null;
        String colValue = null;
                
        for( Cell cell : row ) {
            cell.setCellType(1);
            if(titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")){
            	continue;
            }
            colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString();
            colValue = cell.getRichStringCellValue().getString();           
            returnData.put(colName, colValue);          
        } // for end : Cell     
        if(inValidList(returnData)) return null;
        return returnData;
    }

    /**
     * Customer Insert
     * @param Customer
     * @return Customer 등록 결과
     */
    public Customer insertCustomer(Customer obj) {
        Customer result = null;
        
        try {
                result = customerDao.add(obj);
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * 이메일 패턴 검사
     * @param email
     * @return  email 형식이면 true 반환
     */
    public static boolean isValidEmail(String email) {
        String mailPtrn = "[0-9a-zA-Z]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(mailPtrn);
        Matcher m = p.matcher(email);
        return m.matches();
    }

//    /**
//     * 값이 0또는1 검사
//     * @param Yn
//     * @return  0또는1일경우 true 반환
//     */
//    public boolean isValidYn(String Yn) {
//        boolean bool = false;
//        if (Yn.equals("0")) {
//            bool = true;
//        } else if (Yn.equals("1")) {
//            bool = true;
//        }
//        return bool;
//    }

    /**
     * 값이 0또는1 검사
     * 
     * @param Yn
     * @return  0/1 or Y/N 일 경우 true 반환
     */
    public boolean isValidYn(String Yn) {
        boolean bool = false;
        if (Yn.equals("0") || Yn.equals("1") || Yn.toUpperCase().equals("Y") || Yn.toUpperCase().equals("N")) {
            bool = true;
        }
        return bool;
    }

    /**
     * 0/1 or Y/N 값을 유효한 0/1 로 변환
     * 
     * @param Yn
     * @return
     */
    private String convertValidYn(String Yn) {
        if (Yn.equals("0") || Yn.toUpperCase().equals("N")) {
            return "0";
        } else {
            return "1";
        }
    }

    /**
     * 값이 없는 고객 리스트 검사
     * 
     * @param map
     * @return 빈 리스트일경우 true 리턴
     */
    public boolean inValidList(Map<String, Object> map){
        boolean bool = true;
        if(map.get("customerNo") != null){
            if(!inValidNull(map.get("customerNo").toString())){
                bool = false;
            }
        }
        
        if(map.get("name") != null){    
            if(!inValidNull(map.get("name").toString())){
                bool = false;
            }
        }
        
        if(map.get("address") != null){
            if(!inValidNull(map.get("address").toString())){
                bool = false;
            }
        }
        
        if(map.get("address1") != null){
            if(!inValidNull(map.get("address1").toString())){
                bool = false;
            }
        }
        
        if(map.get("address2") != null){
            if(!inValidNull(map.get("address2").toString())){
                bool = false;
            }       
        }
        
        if(map.get("email") != null){
            if(!inValidNull(map.get("email").toString())){
                bool = false;
            }
        }
        
        if(map.get("emailYn") != null){
            if(!inValidNull(map.get("emailYn").toString())){
                bool = false;
            }
        }
        
        if(map.get("telephoneNo") != null){
            if(!inValidNull(map.get("telephoneNo").toString())){
                bool = false;
            }
        }
        
        if(map.get("mobileNo") != null){
            if(!inValidNull(map.get("mobileNo").toString())){
                bool = false;
            }
        }
        
        if(map.get("smsYn") != null){
            if(!inValidNull(map.get("smsYn").toString())){
                bool = false;
            }
        }
        
        if(map.get("demandResponse") != null){
            if(!inValidNull(map.get("demandResponse").toString())){
                bool = false;
            }
        }
        
        if(map.get("loginId") != null){
            if(!inValidNull(map.get("loginId").toString())){
                bool = false;
            }
        }
        
        if(map.get("passwd") != null){
            if(!inValidNull(map.get("passwd").toString())){
                bool = false;
            }
        }
        
//      if(map.get("customTypeCode") != null){
//          if(!inValidNull(map.get("customTypeCode").toString())){
//              bool = false;
//          }
//      }
        return bool;
    }
    
    /**
     * 공백 및 null 검사
     * @param str
     * @return null, "" 일경우 true 반환
     */
    public boolean inValidNull(String str){
        boolean bool = false;
        if (StringUtil.nullToBlank(str).isEmpty()) {
            bool = true;
        }
        return bool;
    }

    public List<Customer> customerSearchListFindSet(Set<Condition> set) {
        return dao.customerSearchList(set);
    }
    
    /**
     * method name : getCustomerListByRole<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getCustomerListByRole(Map<String, Object> conditionMap) {
        List<Map<String, Object>> operatorList = dao.getCustomerListByRole(conditionMap, false);

        return operatorList;
    }

    /**
     * method name : getCustomerListByRoleTotalCount<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public Integer getCustomerListByRoleTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = dao.getCustomerListByRole(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }
    
    @SuppressWarnings("static-access")
    public Customer getCustomerForUser(Integer userId) {
        Customer customer = dao.get(userId);
        String lastPasswordChangeTime = null;
        if(customer != null)
            lastPasswordChangeTime = customer.getLastPasswordChangeTime();

        Locale locale;
        
        if(customer != null) {
            locale = new Locale(
                    customer.getSupplier().getLang().getCode_2letter(),
                    customer.getSupplier().getCountry().getCode_2letter());
            
            if (locale != null) {
                
                DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
                
                // 타임 로케일 유틸
                TimeLocaleUtil tlu = new TimeLocaleUtil(
                        locale, 
                        dfs.getGroupingSeparator()+"", 
                        dfs.getDecimalSeparator()+"");
                
                // 비밀번호 변경 시간에 로케일 적용
                lastPasswordChangeTime = tlu.getLocaleDate(
                        lastPasswordChangeTime, 
                        locale.getLanguage(), 
                        locale.getCountry());
                
                // Transient 변수에 비밀번호 변경 시간 저장
                customer.setLastPasswordChangeTimeLocale(lastPasswordChangeTime);
            }
        }
        return customer;
    }

    /**
     * method name : mergeExcelCustomer<b/>
     * method Desc : Customer Bulk 등록 시 Update 이면 입력값이 있는 경우 기존값을 Update 한다.
     *
     * @param customer 입력 데이터
     * @param updCustomer 기존 데이터
     * @return
     */
    private Customer mergeExcelCustomer(Customer customer, Customer updCustomer) {
        updCustomer.setSupplier(customer.getSupplier());
        updCustomer.setCustomerNo(customer.getCustomerNo());
        updCustomer.setName(customer.getName());
        updCustomer.setAddress(customer.getAddress());
        updCustomer.setAddress1(customer.getAddress1());
        
        if (!StringUtil.nullToBlank(customer.getAddress2()).isEmpty()) {
            updCustomer.setAddress2(customer.getAddress2());
        }
        
        if (!StringUtil.nullToBlank(customer.getAddress3()).isEmpty()) {
            updCustomer.setAddress3(customer.getAddress3());
        }

        if (!StringUtil.nullToBlank(customer.getEmail()).isEmpty()) {
            updCustomer.setEmail(customer.getEmail());
        }

        if (!StringUtil.nullToBlank(customer.getIdentityOrCompanyRegNo()).isEmpty()) {
            updCustomer.setIdentityOrCompanyRegNo(customer.getIdentityOrCompanyRegNo());
        }

        if (customer.getEmailYn() != null) {
            updCustomer.setEmailYn(customer.getEmailYn());
        }

        if (!StringUtil.nullToBlank(customer.getTelephoneNo()).isEmpty()) {
            updCustomer.setTelephoneNo(customer.getTelephoneNo());
        }

        if (!StringUtil.nullToBlank(customer.getMobileNo()).isEmpty()) {
            updCustomer.setMobileNo(customer.getMobileNo());
        }

        if (customer.getSmsYn() != null) {
            updCustomer.setSmsYn(customer.getSmsYn());
        }

        if (customer.getDemandResponse() != null) {
            updCustomer.setDemandResponse(customer.getDemandResponse());
        }

        if (!StringUtil.nullToBlank(customer.getLoginId()).isEmpty()) {
            updCustomer.setLoginId(customer.getLoginId());
        }
        
        if (!StringUtil.nullToBlank(customer.getPassword()).isEmpty()) {
            updCustomer.setPassword(customer.getPassword());
        }        
        
        if (!StringUtil.nullToBlank(customer.getInitials()).isEmpty()) {
            updCustomer.setInitials(customer.getInitials());
        }

        if (!StringUtil.nullToBlank(customer.getVatNo()).isEmpty()) {
            updCustomer.setVatNo(customer.getVatNo());
        }

        if (!StringUtil.nullToBlank(customer.getWorkTelephone()).isEmpty()) {
            updCustomer.setWorkTelephone(customer.getWorkTelephone());
        }

        if (!StringUtil.nullToBlank(customer.getPostalAddressLine1()).isEmpty()) {
            updCustomer.setPostalAddressLine1(customer.getPostalAddressLine1());
        }

        if (!StringUtil.nullToBlank(customer.getPostalAddressLine2()).isEmpty()) {
            updCustomer.setPostalAddressLine2(customer.getPostalAddressLine2());
        }

        if (!StringUtil.nullToBlank(customer.getPostalSuburb()).isEmpty()) {
            updCustomer.setPostalSuburb(customer.getPostalSuburb());
        }

        if (!StringUtil.nullToBlank(customer.getPostalCode()).isEmpty()) {
            updCustomer.setPostalCode(customer.getPostalCode());
        }

        return updCustomer;
    }
}