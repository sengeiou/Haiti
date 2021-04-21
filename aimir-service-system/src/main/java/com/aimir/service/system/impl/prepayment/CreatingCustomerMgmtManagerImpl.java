package com.aimir.service.system.impl.prepayment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TariffType;
import com.aimir.service.system.prepayment.CreatingCustomerMgmtManager;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;
import com.google.common.collect.Iterators;

/**
 * CreatingCustomerMgmtManagerImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2013. 10. 28. v1.0        문동규   Creating Customer Manager Service Impl
 * </pre>
 */
@Service(value = "creatingCustomerMgmtManager")
public class CreatingCustomerMgmtManagerImpl implements CreatingCustomerMgmtManager {
	@Resource(name="transactionManager")
    HibernateTransactionManager txManager;
	
	private int batchSize = 100;
	private static int totalSize = 0;
	private static int loopSize = 0;
	
    protected static Log logger = LogFactory.getLog(CreatingCustomerMgmtManagerImpl.class);

    @Autowired
    SupplierDao supplierDao; 

    @Autowired
    CustomerDao customerDao;

    @Autowired
    ContractDao contractDao;

    @Autowired
    LocationDao locationDao;

    @Autowired
    TariffTypeDao tariffTypeDao;

    @Autowired
    CodeDao codeDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    DeviceModelDao deviceModelDao;

    /**
     * method name : saveCreatingCustomer<b/>
     * method Desc : Creating Customer Manager 가젯에서 선불고객을 저장한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
    @Transactional
    public void saveCreatingCustomer(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String customerNo = StringUtil.nullToBlank(conditionMap.get("customerNo"));
        String customerName = StringUtil.nullToBlank(conditionMap.get("customerName"));
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String telephoneNo = StringUtil.nullToBlank(conditionMap.get("telephoneNo"));
        String mobileNo = StringUtil.nullToBlank(conditionMap.get("mobileNo"));
        String email = StringUtil.nullToBlank(conditionMap.get("email"));
        String barcode = StringUtil.nullToBlank(conditionMap.get("barcode"));
//        String tariffTypeName = StringUtil.nullToBlank(conditionMap.get("tariffTypeName"));
//        String firstArrears = StringUtil.nullToBlank(conditionMap.get("firstArrears"));
//        String oldArrears = StringUtil.nullToBlank(conditionMap.get("oldArrears"));

        String dateTime = null;
        try {
            dateTime = TimeUtil.getCurrentTime();
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }

        Supplier supplier = supplierDao.get(supplierId);

        List<Location> locationList = locationDao.getParentsBySupplierId(supplierId);
        Location location = null;
        if (locationList != null && locationList.size() > 0) {
            location = locationList.get(0);
        }

        Code serviceTypeCode = codeDao.getCodeIdByCodeObject(MeterType.EnergyMeter.getServiceType());
        Code creditTypeCode = codeDao.getCodeIdByCodeObject(Code.EMERGENCY_CREDIT);
        Code statusCode = codeDao.getCodeIdByCodeObject(Code.NORMAL);
//        TariffType tariffType = tariffTypeDao.findByCondition("name", tariffTypeName);

        Customer customer = new Customer();
        customer.setCustomerNo(customerNo);
        customer.setName(customerName);
        customer.setTelephoneNo(telephoneNo);
        customer.setMobileNo(mobileNo);
        customer.setSmsYn(1);
        customer.setEmail(email);
        customer.setSupplier(supplier);
//        Customer newCustomer = customerDao.add(customer);
        Customer newCustomer = customerDao.saveOrUpdate(customer);

        Contract contract = new Contract();
        contract.setContractNumber(contractNumber);
        contract.setLocation(location);
        contract.setServiceTypeCode(serviceTypeCode);   // Energy
        contract.setCreditType(creditTypeCode);         // emergency credit
        contract.setStatus(statusCode);					// Normal
//        contract.setTariffIndex(tariffType);			// TariffType
        contract.setChargeAvailable(true);
        contract.setBarcode(barcode);
        contract.setContractDate(dateTime);
        contract.setSupplier(supplier);
        contract.setCustomer(newCustomer);
        contract.setCurrentCredit(0.0);
        contract.setCurrentArrears(0.0);
        contract.setCurrentArrears2(0.0);
        contract.setEmergencyCreditAvailable(true);
        contract.setEmergencyCreditMaxDuration(365);
        contract.setEmergencyCreditStartTime(dateTime);
        contractDao.add(contract);
//        contractDao.merge(contract);
    }

    /**
     * method name : saveBulkCreatingCustomerByExcelXLS<b/>
     * method Desc : Creating Customer Manager 가젯 Bulk Tab 에서 선불고객정보를 xls 파일로 받아서 저장한다.
     *
     * @param excel
     * @param supplierId
     * @return
     */
    @Transactional(readOnly=false)
    public Map<String, Object> saveBulkCreatingCustomerByExcelXLS(String excel, Integer supplierId) {

        Map<String, Object> result = new HashMap<String, Object>();
        List<List<Object>> errorList = new ArrayList<List<Object>>();

        Supplier supplier = supplierDao.get(supplierId);

        List<Location> locationList = locationDao.getParentsBySupplierId(supplierId);
        Location location = null;
        if (locationList != null && locationList.size() > 0) {
            location = locationList.get(0);
        }
        
        // check file
        File file = new File(excel.trim());

        // Workbook
        HSSFWorkbook wb = null;
        try {
            wb = new HSSFWorkbook(new FileInputStream(file));
        } catch (FileNotFoundException e1) {
            logger.error(e1, e1);
        } catch (IOException e1) {
            logger.error(e1, e1);
        }
        HSSFSheet sheet = wb.getSheetAt(0);

        // Getting cell contents
        List<Object> errs = null;
        String customerNo = null;
        String customerName = null;
        String contractNumber = null;
        String mobileNo = null;
        String contractDate = null;
        String userAddress1 = null;
        String userAddress2 = null;
        String userAddress3 = null;
        String meterNumber = null;
        String tariffIndexID = null;
        Double oldArrears = null;
        
        TariffType tariffType = null;
        Code serviceTypeCode = null;
        Code creditTypeCode = null;
        Code statusCode = null;

        for (Row row : sheet) {
            // header row skip
            if (row.getRowNum() == 0) {
                continue;
            }

            logger.debug("cell count : " + row.getLastCellNum());
            // Cell 개수가 맞지 않을 경우
            if (row.getLastCellNum() < 10) {
                Iterator<Cell> itrCell = row.cellIterator();
                int cidx = 0;
                errs = new ArrayList<Object>();
                while(itrCell.hasNext()) {
                    errs.add(getCellValue(itrCell.next()));
                    cidx++;
                }

                for (int i = cidx; i < 10; i++) {
                    errs.add("");
                }

                errs.add("Please input all cells");
                errorList.add(errs);
                continue;
            }

            contractNumber = getCellValue(row.getCell(0)).trim();
            contractDate = getCellValue(row.getCell(1)).trim();
            tariffIndexID = getCellValue(row.getCell(2)).trim();
            tariffType = tariffTypeDao.findByCondition("name", getCellValue(row.getCell(2)).trim());
            customerNo = getCellValue(row.getCell(3)).trim();
            customerName = getCellValue(row.getCell(4)).trim();
            userAddress1 = getCellValue(row.getCell(5)).trim();
            userAddress2 = getCellValue(row.getCell(6)).trim();
            userAddress3 = getCellValue(row.getCell(7)).trim();
            mobileNo = getCellValue(row.getCell(8)).trim();
            meterNumber = getCellValue(row.getCell(9)).trim();
            oldArrears = Double.parseDouble(getCellValue(row.getCell(10)).trim());            
            serviceTypeCode = codeDao.getCodeIdByCodeObject(MeterType.EnergyMeter.getServiceType());
            statusCode = codeDao.getCodeIdByCodeObject(Code.NORMAL);
            creditTypeCode = codeDao.getCodeIdByCodeObject(Code.EMERGENCY_CREDIT);

            // 비어있는 cell 이 있으면 에러처리
            if (contractNumber.isEmpty() || contractDate.isEmpty() || tariffIndexID.isEmpty() || customerNo.isEmpty() || customerName.isEmpty() || mobileNo.isEmpty()) {
                errorList.add(getErrorRecord(customerNo, customerName, contractNumber, mobileNo, "Please input all cells"));
                continue;
            }
            
            // contractNumber 값이 sample이면 skip
            if ("sample".equals(contractNumber)) {
                continue;
            }

            // customerNo 중복체크
            Customer chkCustomer = customerDao.findByCondition("customerNo", customerNo);
            customerDao.clear();

            if (chkCustomer != null && chkCustomer.getId() != null) {
                errorList.add(getErrorRecord(customerNo, customerName, contractNumber, mobileNo, "There is a duplicate Customer No : " + customerNo));
                continue;
            }

            // contractNumber 중복체크
            Contract chkContract = contractDao.findByCondition("contractNumber", contractNumber);
            contractDao.clear();

            if (chkContract != null && chkContract.getId() != null) {
                errorList.add(getErrorRecord(customerNo, customerName, contractNumber, mobileNo, "There is a duplicate Contract Number : " + contractNumber));
                continue;
            }
            
            Meter chkMeter = meterDao.findByCondition("mdsId", meterNumber);
            meterDao.clear();
            
            if (chkMeter != null && chkMeter.getId() != null) {
                errorList.add(getErrorRecord(customerNo, customerName, contractNumber, mobileNo, "There is a duplicate Meter Number : " + meterNumber));
                continue;
            }
            
            // Add
            String dateTime = null;
            try {
                dateTime = TimeUtil.getCurrentTime();
            } catch (ParseException e) {
                logger.error(e.getMessage(), e);
            }

            Customer customer = new Customer();
            customer.setCustomerNo(customerNo);
            customer.setName(customerName);
            customer.setAddress1(userAddress1);
            customer.setAddress2(userAddress2);
            customer.setAddress3(userAddress3);
            customer.setMobileNo(mobileNo);
            customer.setSmsYn(1);
            customer.setSupplier(supplier);
            Customer newCustomer = customerDao.add(customer);
            customerDao.flushAndClear();
            
            DeviceModel model = deviceModelDao.findByCondition("name", "I210+");
            
            Meter newMeter = new Meter();
            if (meterNumber != null) {
            	Meter meter = new Meter();
            	meter.setMdsId(meterNumber);
            	meter.setSupplier(supplier);;
            	meter.setLocation(location);
            	meter.setModel(model);
            	meter.setWriteDate(dateTime);
            	newMeter = meterDao.add(meter);
            	meterDao.flushAndClear();
            }
            
            Contract contract = new Contract();
            contract.setContractNumber(contractNumber);
            contract.setLocation(location);
            contract.setServiceTypeCode(serviceTypeCode);   // Energy
            contract.setCreditType(creditTypeCode);         // emergency credit
            contract.setStatus(statusCode);					// Normal
            contract.setTariffIndex(tariffType);			// TariffType
            contract.setContractDate(contractDate);
            contract.setSupplier(supplier);
            contract.setCustomer(newCustomer);
            contract.setMeter(newMeter);
            contract.setCurrentCredit(0.0);
            contract.setCurrentArrears(0.0);
//            contract.setFirstArrears(firstArrears);
//            contract.setOldArrears(oldArrears);
            contractDao.add(contract);
            contractDao.flushAndClear();
            
        } // for end : Row

        if (errorList.size() <= 0) {
            result.put("status", "success");
        } else {
            result.put("status", "failure");
        }

        result.put("errorList", errorList);
        result.put("errorListSize", errorList.size());

        // delete uploaded file
        file.delete();

        return result;
    }

    /**
     * method name : saveBulkCreatingCustomerByExcelXLSX<b/>
     * method Desc : Creating Customer Manager 가젯 Bulk Tab 에서 선불고객정보를 xlsx 파일로 받아서 저장한다.
     *
     * @param excel
     * @param supplierId
     * @return
     */
    @Transactional(readOnly=false)
    public Map<String, Object> saveBulkCreatingCustomerByExcelXLSX(String excel, Integer supplierId) {
    	Date date = new Date();
    	
        Map<String, Object> result = new HashMap<String, Object>();
        List<List<Object>> errorList = new ArrayList<List<Object>>();
        
        Supplier supplier = supplierDao.get(supplierId);

        List<Location> locationList = locationDao.getParentsBySupplierId(supplierId);
        Location location = null;
        if (locationList != null && locationList.size() > 0) {
            location = locationList.get(0);
        }

        logger.debug("excel file:" + excel);

        // Workbook
        XSSFWorkbook wb = null;
        OPCPackage pkg = null;

        try {
            pkg = OPCPackage.open(excel.trim());
            wb = new XSSFWorkbook(pkg);
        } catch (FileNotFoundException e1) {
            logger.error(e1, e1);
        } catch (IOException e1) {
            logger.error(e1, e1);
        } catch (InvalidFormatException e) {
            logger.error(e, e);
        }

        XSSFSheet sheet = null;
        
        try {
        	sheet = wb.getSheetAt(0);
        	Iterator<Row> rowIterator = sheet.iterator();
        	List<Row> entities = new ArrayList<Row>();
        	
        	logger.info("Read Sheet init timestamp : "+ new Timestamp(System.currentTimeMillis()));
	    	
	    	loopSize = 0;
//			totalSize = Iterators.size(rowIterator);
			logger.info("validateSheet() start");
			
	    	// row 수 만큼
	    	while (rowIterator.hasNext()) {
	    		Row row = rowIterator.next();
	    		// header row skip
				if (row.getRowNum() == 0) {					
					continue;
		        }
	    		entities.add(row);
	    		
	    		if(entities.size() % batchSize == 0) {
	    			errorList.addAll(saveRows(entities, supplier, location));
	    			entities.clear();
	    		}
	    	}
			logger.info("entities size is under batchSize : " + Iterators.size(rowIterator));
	    	if(Iterators.size(rowIterator) <= batchSize) {
	    		errorList.addAll(saveRows(entities, supplier, location));
	    	}
	    	
	    	
//        	errorList = validateSheet(sheet, supplier, location);
        	logger.info("Read Sheet end timestamp : "+ new Timestamp(System.currentTimeMillis()));
        	
        	if (errorList.size() <= 0) {
                result.put("status", "success");
            } else {
                result.put("status", "failure");
            }
        	
        	// close OPCPackage
        	if (pkg != null) {
				pkg.close();
            }
		} catch (Exception e) {
			logger.error("saveBulkCreating() for try catch error : " + new Timestamp(date.getTime()) );
		}
        
        result.put("errorList", errorList);
        result.put("errorListSize", errorList.size());

        // delete uploaded file
//        File file = new File(excel.trim());
//        file.delete();

        return result;
    }

    private List<List<Object>> validateSheetWithIterator(Iterator<Row> rowIterator, Supplier supplier, Location location) {
    	List<List<Object>> errorList = new ArrayList<List<Object>>();
		try {
			// 시트의 row
	    	List<Row> entities = new ArrayList<Row>();
	    	
	    	loopSize = 0;
			totalSize = Iterators.size(rowIterator);
			logger.info("validateSheet() start");
			
	    	// row 수 만큼
			for (int i = 0; i < totalSize; i++) {
				Row row = rowIterator.next();
	    		// header row skip
				if (row.getRowNum() == 0) {					
					continue;
		        }
	    		entities.add(row);
	    		
	    		if(entities.size() % batchSize == 0) {
	    			errorList.addAll(saveRows(entities, supplier, location));
	    			entities.clear();
	    		}
			}
			
			logger.info("entities size is under batchSize : " + Iterators.size(rowIterator));
	    	if(Iterators.size(rowIterator) <= batchSize) {
	    		errorList.addAll(saveRows(entities, supplier, location));
	    	}
	    	
		} catch (Exception e) {
			logger.error("validateSheet() for try catch error");
		}
    	
    	return errorList;
		
	}

	private List<List<Object>> saveRows(List<Row> entities, Supplier supplier, Location location) {
    	TransactionStatus txStatus = null;
    	List<List<Object>> errorList = null;
    	String NIB = null;
    	String NIC = null;
    	String customerName = null;
    	String tariffIndexID = null;
    	
        try {
        	txStatus = txManager.getTransaction(null);
        	
    		errorList = new ArrayList<List<Object>>();
    		Date date = new Date();
    		
    		for (Row row : entities) {

    			NIB = getCellValue(row.getCell(0)).trim();		// contractNumber
                String contractDate = getCellValue(row.getCell(1)).trim();
                tariffIndexID = getCellValue(row.getCell(2)).trim();
                NIC = getCellValue(row.getCell(3)).trim();		// customerNo
                customerName = getCellValue(row.getCell(4)).trim();
                String userAddress1 = getCellValue(row.getCell(5)).trim();
                String userAddress2 = getCellValue(row.getCell(6)).trim();
                String userAddress3 = getCellValue(row.getCell(7)).trim();
                String mobileNo = getCellValue(row.getCell(8)).trim();
                String meterNumber = getCellValue(row.getCell(9)).trim().isEmpty() ? null : getCellValue(row.getCell(9)).trim();
                String previousMeter = getCellValue(row.getCell(10)).trim();
                String currentArrears1 = getCellValue(row.getCell(11)).trim().isEmpty() ? null : getCellValue(row.getCell(10)).trim();            
                String currentArrears2 = getCellValue(row.getCell(12)).trim().isEmpty() ? null : getCellValue(row.getCell(11)).trim();
                String carrier = getCellValue(row.getCell(13)).trim();
                TariffType tariffType = tariffTypeDao.findByCondition("name", tariffIndexID);
                Code serviceTypeCode = codeDao.getCodeIdByCodeObject(MeterType.EnergyMeter.getServiceType());
                Code statusCode = codeDao.getCodeIdByCodeObject(Code.NORMAL);
                Code creditTypeCode = codeDao.getCodeIdByCodeObject(Code.EMERGENCY_CREDIT);
                Code meterStatusCode = codeDao.getCodeIdByCodeObject(MeterStatus.NewRegistered.getCode());
                
                if("sample".equals(NIB)) { // contractNumber 값이 sample이면 skip
                	continue;
                }
                
                // 비어있는 cell 이 있으면 에러처리
                if (NIB.isEmpty() || tariffIndexID.isEmpty() || NIC.isEmpty() || customerName.isEmpty()) {
                    errorList.add(getErrorRecord(NIC, customerName, NIB, tariffIndexID, "Please fill in the values in the cells"));
                    continue;
                }

                // Add
                String dateTime = TimeUtil.getCurrentTime();
                
                logger.info("### Customer 생성 customerNo : "+NIC);
                Customer customer = customerDao.findByCondition("customerNo", NIC);
                if(customer == null) {
                	customer = new Customer();
                	customer.setCustomerNo(NIC);
                    customer.setName(customerName);
                    customer.setAddress1(userAddress1);
                    customer.setAddress2(userAddress2);
                    customer.setAddress3(userAddress3);
                    customer.setMobileNo(mobileNo);
                    customer.setSmsYn(1);
                    customer.setSupplier(supplier);
                    customer.setCarrier(carrier);
                    customerDao.add(customer);
                }else {
                	customer.setCustomerNo(NIC);
                	customer.setName(customerName);
                	customer.setAddress1(userAddress1);
                	customer.setAddress2(userAddress2);
                	customer.setAddress3(userAddress3);
                	customer.setMobileNo(mobileNo);
                	customer.setSmsYn(1);
                	customer.setSupplier(supplier);
                	customer.setCarrier(carrier);
                	customerDao.update(customer);
                }
//                customerDao.flushAndClear();
                logger.info("### Customer 저장 : "+customer.toString());
                logger.debug("customerDao.add finished : " + new Timestamp(date.getTime()) );
                
                DeviceModel model = deviceModelDao.findByCondition("name", "I210+");
                
                Meter newMeter = new Meter();
                if (meterNumber != null && !"".equals(meterNumber)) {
                	logger.info("### newMeter 생성 meterNumber : "+meterNumber+".");
                	Meter meter = meterDao.findByCondition("mdsId", meterNumber.toString());
                	if(meter == null) {
                		errorList.add(getErrorRecord(NIC, customerName, NIB, tariffIndexID, "Unregistered meter"));
                		continue;
                	}
                	
                	logger.info("### Meter 조회 : "+meter.toString());
                	meter.setMdsId(meterNumber);
                	meter.setSupplier(supplier);;
                	meter.setLocation(location);
                	meter.setModel(model);
                	meter.setMeterStatus(meterStatusCode);
                	meter.setWriteDate(dateTime);
                	meterDao.update(meter);
//                	meterDao.flushAndClear();
                	logger.info("### Meter 저장 : "+meter.toString());
                	newMeter = meter;
                }
                logger.info("### Contract 생성 contractNumber : "+NIB);
                Contract contract = contractDao.findByCondition("contractNumber", NIB);
                if(contract == null) {
                	contract = new Contract();
                	contract.setContractNumber(NIB);
                    contract.setLocation(location);
                    contract.setServiceTypeCode(serviceTypeCode);   // Energy
                    contract.setCreditType(creditTypeCode);         // emergency credit
                    contract.setStatus(statusCode);					// Normal
                    contract.setTariffIndex(tariffType);			// TariffType
                    contract.setContractDate(contractDate);
                    contract.setChargeAvailable(true);
                    contract.setSupplier(supplier);
                    contract.setCustomer(customer);
                    contract.setCurrentCredit(contract.getCurrentCredit() == null ? 0.0 : contract.getCurrentCredit());
                    contract.setCurrentArrears(currentArrears1 == null  ?  0.0 : Double.parseDouble(currentArrears1));
                    contract.setCurrentArrears2(currentArrears2 == null  ?  0.0 : Double.parseDouble(currentArrears2));
                    contract.setPreMdsId(previousMeter);
                    contract.setEmergencyCreditAvailable(true);
                    contract.setEmergencyCreditMaxDuration(365);
                    contract.setEmergencyCreditStartTime(dateTime);
                    if (meterNumber != null && !"".equals(meterNumber)) {
                    	contract.setMeter(newMeter);
                    }
                    contractDao.add(contract);
                }else {
                	contract.setContractNumber(NIB);
                	contract.setLocation(location);
                	contract.setServiceTypeCode(serviceTypeCode);   // Energy
                	contract.setCreditType(creditTypeCode);         // emergency credit
                	contract.setStatus(statusCode);					// Normal
                	contract.setTariffIndex(tariffType);			// TariffType
                	contract.setContractDate(contractDate);
                	contract.setChargeAvailable(true);
                	contract.setSupplier(supplier);
                	contract.setCustomer(customer);
                	contract.setCurrentCredit(contract.getCurrentCredit() == null ? 0.0 : contract.getCurrentCredit());
                	contract.setCurrentArrears(currentArrears1 == null  ?  0.0 : Double.parseDouble(currentArrears1));
                	contract.setCurrentArrears2(currentArrears2 == null  ?  0.0 : Double.parseDouble(currentArrears2));
                	contract.setPreMdsId(previousMeter);
                	contract.setEmergencyCreditAvailable(true);
                	contract.setEmergencyCreditMaxDuration(365);
                	contract.setEmergencyCreditStartTime(dateTime);
                	if (meterNumber != null && !"".equals(meterNumber)) {
                		contract.setMeter(newMeter);
                	}
                	contractDao.update(contract);
                }
//                contractDao.flushAndClear();
                logger.info("### Contract 저장  : "+contract.toString());
                logger.debug("contractDao.add finished : " + new Timestamp(date.getTime()) );
                logger.info("### saveRows loop size :  " + ++loopSize +",  totalSize : "+ totalSize);
			}
    		
    		txManager.commit(txStatus);
		} catch (Exception e) {
			logger.error("saveRows - for try catch error");
			logger.error(e,e);
			errorList.add(getErrorRecord(NIC, customerName, NIB, tariffIndexID, "Please check the values"));
			if(txStatus != null) txManager.rollback(txStatus);
		}
		return errorList;
	}

	public List<List<Object>> validateSheet(XSSFSheet sheet, Supplier supplier, Location location) {
		List<List<Object>> errorList = new ArrayList<List<Object>>();
		try {
			// 시트의 row
	    	Iterator<Row> rowIterator = sheet.iterator();
	    	List<Row> entities = new ArrayList<Row>();
	    	
	    	loopSize = 0;
//			totalSize = Iterators.size(rowIterator);
			logger.info("validateSheet() start");
			
	    	// row 수 만큼
	    	while (rowIterator.hasNext()) {
	    		Row row = rowIterator.next();
	    		// header row skip
				if (row.getRowNum() == 0) {					
					continue;
		        }
	    		entities.add(row);
	    		
	    		if(entities.size() % batchSize == 0) {
	    			errorList.addAll(saveRows(entities, supplier, location));
	    			entities.clear();
	    		}
	    	}
			logger.info("entities size is under batchSize : " + Iterators.size(rowIterator));
	    	if(Iterators.size(rowIterator) <= batchSize) {
	    		errorList.addAll(saveRows(entities, supplier, location));
	    	}
	    	
		} catch (Exception e) {
			logger.error("validateSheet() for try catch error");
		}
    	
    	return errorList;
	}
	
	public void validateCell(List<Row> entities){
		
		MultiKeyMap compareMap = new MultiKeyMap();
    	List<Object> errs = new ArrayList<Object>();
    	
    	for (Row row : entities) {
    		logger.info("row.getCell(0) "+ row.getCell(0));
    		compareMap.put(getCellValue(row.getCell(0)).trim(), getCellValue(row.getCell(9)).trim(), row);
    	}
    	
    	for (Object key : compareMap.keySet()) {
    		if(compareMap.containsKey(key)) {
		        errs.add(compareMap.get(key));
		        compareMap.remove(key);
    		}
		}
   	}

	/**
     * method name : sendCertificationSMS<b/>
     * method Desc : Creating Customer Manager 가젯에서 휴대폰번호를 인증한다.
     *
     * @param conditionMap
     * @return
     */
    public Boolean sendCertificationSMS(Map<String, Object> conditionMap) {
        Boolean result = false;
        String mobileNo = StringUtil.nullToBlank(conditionMap.get("mobileNo"));
	    String text = "This message is for ensurance for admission.";
	    Properties prop = new Properties();
	    if ( mobileNo != null ) {
		    try {
		    	prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
		    	String smsClassPath = prop.getProperty("smsClassPath");
				Object obj = Class.forName(smsClassPath).newInstance();
				
				Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class);
				m.invoke(obj, mobileNo, text);
		    				
			} catch (Exception e) {
				result = false;
				e.printStackTrace();
			}				
		}    

        return result;
    }

    /**
     * method name : getCellValue<b/>
     * method Desc :
     *
     * @param cell
     * @return
     */
    private String getCellValue(Cell cell) {
    	if (cell == null) {
    		return "";
    	}

        String value = null;

        switch(cell.getCellType()) {
            case Cell.CELL_TYPE_BLANK:
                value = "";
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                value = Boolean.toString(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_ERROR:
                value = "";
                break;
            case Cell.CELL_TYPE_FORMULA:
                value = cell.getCellFormula();
                break;
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue().toString();
                } else {
                    Long roundVal = Math.round(cell.getNumericCellValue());
                    Double doubleVal = cell.getNumericCellValue();
                    if (doubleVal.equals(roundVal.doubleValue())) {
                        value = String.valueOf(roundVal);
                    } else {
                        value = String.valueOf(doubleVal);
                    }
                }
                break;
            case Cell.CELL_TYPE_STRING:
                value = cell.getRichStringCellValue().getString();
                break;
        }
        return value;
    }

    /**
     * method name : getErrorRecord<b/>
     * method Desc :
     *
     * @param customerNo
     * @param customerName
     * @param contractNumber
     * @param mobileNo
     * @param errMsg
     * @return
     */
    private List<Object> getErrorRecord(String customerNo, String customerName, String contractNumber, String tariffIndexID, String errMsg) {
        List<Object> errs = new ArrayList<Object>();
        errs.add(customerNo);
        errs.add(customerName);
        errs.add(contractNumber);
        errs.add(tariffIndexID);
        errs.add(errMsg);
        return errs;
    }
}