package com.aimir.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TariffType;
import com.aimir.util.DateTimeUtil;

/**
 * ECG의 고객 정보를 텍스트 파일로 등록할 때 사용함.
 * @author elevas
 *
 */
public class EcgCbis {
    private static Log log = LogFactory.getLog(EcgCbis.class);
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"/applicationContext-hibernate.xml"}); 
        
        String filename = "D:\\Downloads\\04210017nw.txt"; // args[0];
        String supplierName = "ECG"; // args[1];
        
        File file = new File(filename);
        
        BufferedReader reader = null;
        JpaTransactionManager txManager = (JpaTransactionManager)ctx.getBean("transactionManager");
        TransactionStatus txStatus = null;
        try {
            txStatus = txManager.getTransaction(null);
            
            reader = new BufferedReader(new FileReader(file));
            EcgCbis cbis = new EcgCbis();
            String row = null;
            while ((row= reader.readLine()) != null) {
                if (row.startsWith("03-04"))
                    cbis.mapping(ctx, row, supplierName);
            }
            
            txManager.commit(txStatus);
        }
        catch (Exception e) {
            log.error(e, e);
        }
        finally {
            try {
                if (reader != null) reader.close();
            }
            catch (IOException e) {}
            try {
                if (txStatus != null) txManager.rollback(txStatus);
            }
            catch (Exception e) {}
        }
    }

    private void mapping(ApplicationContext ctx, String row, String supplierName) {
        String geocode;
        String customerNo;
        String c;
        String customerName;
        String address1;
        String address2;
        String oldMeterNo;
        String tc;
        
        int pos = 0;
        geocode = row.substring(pos, pos+22).trim().replaceAll("-", "");
        log.info("GEO-CODE[" + geocode + "]");
        pos += 23;
        customerNo = row.substring(pos, pos+12).trim().replaceAll("-", "");
        log.info("CUSTOMER_NO[" + customerNo + "]");
        pos += 13;
        c = row.substring(pos, pos+1).trim();
        pos += 2;
        customerName = row.substring(pos, pos+25).trim();
        log.info("CUSTOMER_NAME[" + customerName + "]");
        pos += 26;
        address1 = row.substring(pos, pos+25).trim();
        log.info("ADDRESS1[" + address1+ "]");
        pos += 26;
        address2 = row.substring(pos, pos+25).trim();
        log.info("ADDRESS2[" + address2 + "]");
        pos += 26;
        oldMeterNo = row.substring(pos, pos+10).trim();
        log.info("OLD_METER_NO[" + oldMeterNo + "]");
        pos += 11;
        tc = row.substring(pos, pos+2);
        log.info("TC[" + tc + "]");
        
        SupplierDao supplierDao = ctx.getBean(SupplierDao.class);
        Supplier supplier = supplierDao.findByCondition("name", supplierName);
        
        if (supplier == null) return;
        
        Customer customer = makeCustomer(ctx, supplier, customerNo, customerName, address1, address2);
        Contract contract = makeContract(ctx, customer, geocode, tc);
        
        Meter meter = contract.getMeter();
        if (meter != null) {
            meter.setInstallProperty(oldMeterNo);
            
            MeterDao meterDao = ctx.getBean(MeterDao.class);
            meterDao.update(meter);
        }
    }
    
    private Customer makeCustomer(ApplicationContext ctx, Supplier supplier, 
            String customerNo, String name, String address1, String address2) {
        CustomerDao customerDao = ctx.getBean(CustomerDao.class);
        Customer customer = customerDao.findByCondition("customerNo", "ECG_"+customerNo);
        
        if (customer == null) {
            LocationDao locationDao = ctx.getBean(LocationDao.class);
            Location location = locationDao.findByCondition("name", "Accra");
            
            customer = new Customer();
            customer.setCustomerNo("ECG_" + customerNo);
            customer.setName(name);
            customer.setAddress(address1);
            customer.setAddress1(address2);
            customer.setSupplier(supplier);
            customer.setLocation(location);
            
            customerDao.add(customer);
        }
        else {
            if (!customer.getName().equals(name))
                customer.setName(name);
            if (!customer.getAddress().equals(address1))
                customer.setAddress(address1);
            if (!customer.getAddress1().equals(address2))
                customer.setAddress1(address2);
            
            customerDao.update(customer);
        }
        
        return customer;
    }
    
    private Contract makeContract(ApplicationContext ctx, 
            Customer customer, String geocode, String tc) {
        ContractDao contractDao = ctx.getBean(ContractDao.class);
        Contract contract = contractDao.findByCondition("contractNumber", geocode);
        String contractDate = DateTimeUtil.getDateString(new Date());
        
        if (contract == null) {
            contract = new Contract();
            contract.setCustomer(customer);
            contract.setLocation(customer.getLocation());
            contract.setContractNumber(geocode);
            contract.setSupplier(customer.getSupplier());
            contract.setCurrentArrears(10.0);
            contract.setCurrentCredit(10.0);
            contract.setPrepaymentThreshold(10);
            contract.setChargeAvailable(true);
            contract.setContractDate(contractDate);
            contract.setPrepayStartTime(contractDate);
            
            CodeDao codeDao = ctx.getBean(CodeDao.class);
            Code serviceType = codeDao.findByCondition("code", "3.1");
            Code creditType = codeDao.findByCondition("code", "2.2.1");
            
            contract.setServiceTypeCode(serviceType);
            contract.setCreditType(creditType);
            
            TariffTypeDao tariffDao = ctx.getBean(TariffTypeDao.class);
            TariffType tariffType = null;
            
            if (tc.equals("11")) tariffType = tariffDao.findByCondition("name", "Residential");
            else if (tc.equals("21")) tariffType = tariffDao.findByCondition("name", "Non Residential");
            
            contract.setTariffIndex(tariffType);
            
            contractDao.add(contract);
        }
        else {
            TariffTypeDao tariffDao = ctx.getBean(TariffTypeDao.class);
            TariffType tariffType = contract.getTariffIndex();
            boolean needUpdate = false;
            
            if (contract.getContractDate() == null || "".equals(contract.getContractDate())) {
                contract.setContractDate(contractDate);
                needUpdate =true;
            }
            
            if (contract.getPrepayStartTime() == null || "".equals(contract.getPrepayStartTime())) {
                contract.setPrepayStartTime(contractDate);
                needUpdate =true;
            }
            
            if (tariffType == null) {
                if (tc.equals("11")) tariffType = tariffDao.findByCondition("name", "Residential");
                else if (tc.equals("21")) tariffType = tariffDao.findByCondition("name", "Non Residential");
                
                contract.setTariffIndex(tariffType);
                needUpdate =true;
            }
            else {
                if (tariffType.getName().equals("Residential") && tc.equals("21")) {
                    tariffType = tariffDao.findByCondition("name", "Non Residential");
                    contract.setTariffIndex(tariffType);
                    needUpdate =true;
                }
                else if (tariffType.getName().equals("Non Residential") && tc.equals("11")) {
                    tariffType = tariffDao.findByCondition("name", "Residential");
                    contract.setTariffIndex(tariffType);
                    needUpdate =true;
                }
            }
            
            if (needUpdate) contractDao.update(contract);
        }
        
        return contract;
    }
}
