package com.aimir.service.system.impl.prepayment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TariffType;
import com.aimir.service.system.prepayment.CreatingCustomerMgmtManager;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

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
        Code creditTypeCode = codeDao.getCodeIdByCodeObject(Code.PREPAYMENT);

        Customer customer = new Customer();
        customer.setCustomerNo(customerNo);
        customer.setName(customerName);
        customer.setTelephoneNo(telephoneNo);
        customer.setMobileNo(mobileNo);
        customer.setSmsYn(1);
        customer.setEmail(email);
        customer.setSupplier(supplier);
        Customer newCustomer = customerDao.add(customer);

        Contract contract = new Contract();
        contract.setContractNumber(contractNumber);
        contract.setLocation(location);
        contract.setServiceTypeCode(serviceTypeCode);   // Energy
        contract.setCreditType(creditTypeCode);         // prepay
        contract.setBarcode(barcode);
        contract.setContractDate(dateTime);
        contract.setSupplier(supplier);
        contract.setCustomer(newCustomer);
        contractDao.add(contract);
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

        TariffType tariffType = tariffTypeDao.findByCondition("name", "Residential");   // Default Tariff Type : Residential
        Code serviceTypeCode = codeDao.getCodeIdByCodeObject(MeterType.EnergyMeter.getServiceType());   // Default Service Type : EnergyMeter
        Code creditTypeCode = codeDao.getCodeIdByCodeObject(Code.PREPAYMENT);           // Default Credit Type : Prepayment

        // check file
        File file = new File(excel.trim()); // jhkim trim() 추가

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

        for (Row row : sheet) {
            // header row skip
            if (row.getRowNum() == 0) {
                continue;
            }

            logger.debug("cell count : " + row.getLastCellNum());
            // Cell 개수가 맞지 않을 경우
            if (row.getLastCellNum() < 4) {
                Iterator<Cell> itrCell = row.cellIterator();
                int cidx = 0;
                errs = new ArrayList<Object>();
                while(itrCell.hasNext()) {
                    errs.add(getCellValue(itrCell.next()));
                    cidx++;
                }

                for (int i = cidx; i < 4; i++) {
                    errs.add("");
                }

                errs.add("Please input all cells");
                errorList.add(errs);
                continue;
            }

            customerNo = getCellValue(row.getCell(0)).trim();
            customerName = getCellValue(row.getCell(1)).trim();
            contractNumber = getCellValue(row.getCell(2)).trim();
            mobileNo = getCellValue(row.getCell(3)).trim();

            // cell 이 모두 비어있으면 skip
            if (customerNo.isEmpty() && customerName.isEmpty() && contractNumber.isEmpty() && mobileNo.isEmpty()) {
                continue;
            }

            // 비어있는 cell 이 있으면 에러처리
            if (customerNo.isEmpty() || customerName.isEmpty() || contractNumber.isEmpty()) {
                errorList.add(getErrorRecord(customerNo, customerName, contractNumber, mobileNo, "Please input all cells"));
                continue;
            }

            // customerNo 중복체크
            Customer chkCustomer = customerDao.findByCondition("customerNo", customerNo);
            customerDao.clear();

            if (chkCustomer != null && chkCustomer.getId() != null) {
                errorList.add(getErrorRecord(customerNo, customerName, contractNumber, mobileNo, "There is a duplicate Customer No."));
                continue;
            }

            // contractNumber 중복체크
            Contract chkContract = contractDao.findByCondition("contractNumber", contractNumber);
            contractDao.clear();

            if (chkContract != null && chkContract.getId() != null) {
                errorList.add(getErrorRecord(customerNo, customerName, contractNumber, mobileNo, "There is a duplicate Contract Number."));
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
            
            customer.setMobileNo(mobileNo);
            customer.setSmsYn(1);
            customer.setSupplier(supplier);
            Customer newCustomer = customerDao.add(customer);
            customerDao.flushAndClear();

            Contract contract = new Contract();
            contract.setContractNumber(contractNumber);
            contract.setLocation(location);
            contract.setServiceTypeCode(serviceTypeCode);   // Energy
            contract.setCreditType(creditTypeCode);         // prepay
            contract.setContractDate(dateTime);
            contract.setSupplier(supplier);
            contract.setCustomer(newCustomer);
            contract.setTariffIndex(tariffType);
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

        Map<String, Object> result = new HashMap<String, Object>();
        List<List<Object>> errorList = new ArrayList<List<Object>>();

        Supplier supplier = supplierDao.get(supplierId);

        List<Location> locationList = locationDao.getParentsBySupplierId(supplierId);
        Location location = null;
        if (locationList != null && locationList.size() > 0) {
            location = locationList.get(0);
        }

        TariffType tariffType = tariffTypeDao.findByCondition("name", "Residential");   // Default Tariff Type : Residential
        Code serviceTypeCode = codeDao.getCodeIdByCodeObject(MeterType.EnergyMeter.getServiceType());
        Code creditTypeCode = codeDao.getCodeIdByCodeObject(Code.PREPAYMENT);

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

        XSSFSheet sheet = wb.getSheetAt(0);

        // Getting cell contents
        List<Object> errs = null;
        String customerNo = null;
        String customerName = null;
        String contractNumber = null;
        String mobileNo = null;

        for (Row row : sheet) {
            // header row skip
            if (row.getRowNum() == 0) {
                continue;
            }

            // cell 개수가 안맞을 경우
            if (row.getLastCellNum() < 4) {
                Iterator<Cell> itrCell = row.cellIterator();
                errs = new ArrayList<Object>();
                while(itrCell.hasNext()) {
                    errs.add(getCellValue(itrCell.next()));
                }
                errs.add("Please input all cells");
                errorList.add(errs);
                continue;
            }

            customerNo = getCellValue(row.getCell(0)).trim();
            customerName = getCellValue(row.getCell(1)).trim();
            contractNumber = getCellValue(row.getCell(2)).trim();
            mobileNo = getCellValue(row.getCell(3)).trim();

            // cell 이 모두 비어있으면 skip
            if (customerNo.isEmpty() && customerName.isEmpty() && contractNumber.isEmpty() && mobileNo.isEmpty()) {
                continue;
            }

            // 비어있는 cell 이 있으면 에러처리
            if (customerNo.isEmpty() || customerName.isEmpty() || contractNumber.isEmpty() || mobileNo.isEmpty()) {
                errorList.add(getErrorRecord(customerNo, customerName, contractNumber, mobileNo, "Please input all cells"));
                continue;
            }

            // customerNo 중복체크
            Customer chkCustomer = customerDao.findByCondition("customerNo", customerNo);
            customerDao.clear();

            if (chkCustomer != null && chkCustomer.getId() != null) {
                errorList.add(getErrorRecord(customerNo, customerName, contractNumber, mobileNo, "There is a duplicate Customer No."));
                continue;
            }

            // contractNumber 중복체크
            Contract chkContract = contractDao.findByCondition("contractNumber", contractNumber);
            contractDao.clear();

            if (chkContract != null && chkContract.getId() != null) {
                errorList.add(getErrorRecord(customerNo, customerName, contractNumber, mobileNo, "There is a duplicate Contract Number."));
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
            customer.setMobileNo(mobileNo);
            customer.setSmsYn(1);
            customer.setSupplier(supplier);
            Customer newCustomer = customerDao.add(customer);
            customerDao.flushAndClear();

            Contract contract = new Contract();
            contract.setContractNumber(contractNumber);
            contract.setLocation(location);
            contract.setServiceTypeCode(serviceTypeCode);   // Energy
            contract.setCreditType(creditTypeCode);         // prepay
            contract.setContractDate(dateTime);
            contract.setSupplier(supplier);
            contract.setCustomer(newCustomer);
            contract.setTariffIndex(tariffType);
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

        // close OPCPackage
        try {
            if (pkg != null) {
                pkg.close();
            }
        } catch (IOException e) {
            logger.error(e, e);
        }

        // delete uploaded file
        File file = new File(excel.trim());
        file.delete();

        return result;
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
    private List<Object> getErrorRecord(String customerNo, String customerName, String contractNumber, String mobileNo, String errMsg) {
        List<Object> errs = new ArrayList<Object>();
        errs.add(customerNo);
        errs.add(customerName);
        errs.add(contractNumber);
        errs.add(mobileNo);
        errs.add(errMsg);
        return errs;
    }
}