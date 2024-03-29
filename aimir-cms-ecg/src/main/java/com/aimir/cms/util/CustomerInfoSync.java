package com.aimir.cms.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.cms.dao.CustEntDao;
import com.aimir.cms.dao.DebtEntDao;
import com.aimir.cms.dao.MeterEntDao;
import com.aimir.cms.dao.ServPointDao;
import com.aimir.cms.model.CustEnt;
import com.aimir.cms.model.DebtEnt;
import com.aimir.cms.model.MeterEnt;
import com.aimir.cms.model.ServPoint;
import com.aimir.cms.model.TariffEnt;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.BillingBlockTariffDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.BillingBlockTariff;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;

public class CustomerInfoSync {
    private static Log log = LogFactory.getLog(CustomerInfoSync.class);
    
    private static ApplicationContext ctx;
    
    public static void main(String[] args) {
        String filename = args[0];
        ctx = new ClassPathXmlApplicationContext(new String[]{"/spring.xml"}); 
        CustomerInfoSync sync = new CustomerInfoSync();
        sync.run(filename);
        sync.createCustomer();
        // sync.insertCD001002003(filename);
        // sync.insertCD004(filename);
    }
    
    public void insertCD004(String filename) {
        InputStream in = null;
        HSSFWorkbook wb = null;
        try {
            in = new FileInputStream(filename);
            wb = new HSSFWorkbook(new POIFSFileSystem(in));
            
            Sheet sheet1 = wb.getSheetAt(0);
            int i_mdsid = 0;
            int i_hhmmss = 1;
            
            int lastRowNum = sheet1.getLastRowNum()+1;
            Cell c_mdsid = null;
            Cell c_hhmmss = null;
            
            String s_mdsid = null;
            String s_hhmmss = null;
            
            Row row = null;
            for (int i = 1; i < lastRowNum; i++) {
                row = sheet1.getRow(i);
                c_mdsid = row.getCell(i_mdsid);
                c_hhmmss = row.getCell(i_hhmmss);
                
                s_mdsid = c_mdsid.getRichStringCellValue().getString();
                s_hhmmss = c_hhmmss.getRichStringCellValue().getString();
                
                log.info("MDS_ID[" + s_mdsid + "] HHMMSS[" + s_hhmmss + "]");
                
                insertCD004(s_mdsid, s_hhmmss);
            }
        }
        catch (Exception e) {
            log.error(e, e);
        }
        finally {
            try {
                if (wb != null) wb.close();
            }
            catch (Exception e) {}
            try {
                if (in != null) in.close();
            }
            catch (Exception e) {}
        }
    }
    
    private void insertCD004(String mdsid, String hhmmss) {
        BillingBlockTariffDao bbtDao = ctx.getBean(BillingBlockTariffDao.class);
        HibernateTransactionManager txmanager = ctx.getBean(HibernateTransactionManager.class);
        DataSource ds = ctx.getBean(DataSource.class);
        TransactionStatus txstatus = null;
        Connection con = null;
        PreparedStatement maxid_stmt = null;
        PreparedStatement g_cd004 = null;
        PreparedStatement i_cd004 = null;
        ResultSet g_rs = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("id.yyyymmdd", new Object[]{"20150731"}, null, Restriction.EQ));
            condition.add(new Condition("id.hhmmss", new Object[]{hhmmss}, null, Restriction.EQ));
            condition.add(new Condition("id.mdevId", new Object[]{mdsid}, null, Restriction.EQ));
            condition.add(new Condition("writeDate", new Object[]{"20150803155540"}, null, Restriction.LE));
            
            List<BillingBlockTariff> list = bbtDao.findByConditions(condition);
            if (list != null && list.size() == 1) {
                BillingBlockTariff bbt = list.get(0);
                
                String ts = bbt.getWriteDate();
                String num_apa = mdsid;
                String co_marca = "MC013";
                String cust_name = "";
                String customer_number = ""; 
                if (bbt.getContract() != null && bbt.getContract().getCustomer() != null) {
                    cust_name = bbt.getContract().getCustomer().getName();
                    customer_number = bbt.getContract().getCustomer().getCustomerNo();
                }
                String service_point_no = "";
                int cod_unicom = -1;
                if (bbt.getContract() != null) {
                    service_point_no = bbt.getContract().getServicePointId();
                    if (bbt.getContract().getCashPoint() != null && bbt.getContract().getCashPoint() > 0)
                        cod_unicom = bbt.getContract().getCashPoint();
                }
                String co_concepto = "CD004";
                double csmo_fact = bbt.getAccumulateUsage();
                int cycle = 7;
                Date cycle_date = new Date(DateTimeUtil.getDateFromYYYYMMDD("20150731").getTime());
                double imp_concepto = bbt.getAccumulateBill();
                
                con = ds.getConnection();
                g_cd004 = con.prepareStatement("select transaction_id from prep_trans_ext@CMS_LINK where"
                        + " num_apa=? and cycle_date = to_date(?, 'YYYYMMDD')");
                g_cd004.setString(1, mdsid);
                g_cd004.setString(2,  "20150731");
                g_rs = g_cd004.executeQuery();
                
                long txId = 0;
                if (g_rs.next()) {
                    txId = g_rs.getInt(1);
                    if (txId > 0) {
                        // update
                        /*
                        i_cd004 = con.prepareStatement("update prep_trans_ext@CMS_LINK set csmo_fact = ?, imp_concepto = ?"
                                + " where transaction_id = ?");
                        i_cd004.setDouble(1, csmo_fact);
                        i_cd004.setDouble(2, imp_concepto);
                        i_cd004.setLong(3, txId);
                        
                        i_cd004.close();
                        
                        log.info("UPDATE TS[" + ts + "] NUM_APA[" + num_apa + "] CUST_NAME[" + cust_name 
                                + "] CUSTOMER_NUMBER[" + customer_number + "] SERVICE_POINT_NO[" + service_point_no
                                + "] RECPT_NO[" + co_concepto+txId + "] CO_CONCEPTO[" + co_concepto 
                                + "] CSMO_FACT[" + csmo_fact + "] CYBLE[" + cycle + "] CYCLE_DATE[" + cycle_date 
                                + "] IMP_CONCEPTO[" + imp_concepto + "] COD_UNICOM[" + cod_unicom + "]");
                                */
                    }
                }
                
                if (txId == 0 && service_point_no != null && !"".equals(service_point_no)) {
                    // insert
                    maxid_stmt = con.prepareStatement("select nvl(max(transaction_id), 0)+1 from prep_trans_ext@CMS_Link where co_concepto=?");
                    maxid_stmt.setString(1, co_concepto);
                    g_rs = maxid_stmt.executeQuery();
                    
                    if (g_rs.next()) {
                        txId = g_rs.getLong(1);
                        // insert
                        i_cd004 = con.prepareStatement("insert into prep_trans_ext@CMS_LINK "
                                + " (ts, transaction_id, num_apa, co_marca, cust_name, customer_number, service_point_no,"
                                + " recpt_no, co_concepto, csmo_fact, cycle, cycle_date, imp_concepto, cod_unicom, co_sistema, exported)"
                                + " values(?,?,?,?,?,?,?,?,?,?,?,to_date(?, 'YYYYMMDD'),?,?,?,?)");
                        i_cd004.setDate(1,  new Date(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(ts).getTime()));
                        i_cd004.setLong(2,  txId);
                        i_cd004.setString(3, num_apa);
                        i_cd004.setString(4, co_marca);
                        i_cd004.setString(5, cust_name);
                        i_cd004.setString(6, customer_number);
                        i_cd004.setString(7,  service_point_no);
                        i_cd004.setString(8, co_concepto+txId);
                        i_cd004.setString(9, co_concepto);
                        i_cd004.setDouble(10,  csmo_fact);
                        i_cd004.setInt(11, cycle);
                        i_cd004.setString(12, "20150731");
                        i_cd004.setDouble(13, imp_concepto);
                        i_cd004.setInt(14, cod_unicom);
                        i_cd004.setString(15, "SE003");
                        i_cd004.setInt(16, 0);
                        
                        boolean result = i_cd004.execute();
                        log.info("INSERT TS[" + ts + "] NUM_APA[" + num_apa + "] CUST_NAME[" + cust_name 
                                + "] CUSTOMER_NUMBER[" + customer_number + "] SERVICE_POINT_NO[" + service_point_no
                                + "] RECPT_NO[" + co_concepto+txId + "] CO_CONCEPTO[" + co_concepto 
                                + "] CSMO_FACT[" + csmo_fact + "] CYBLE[" + cycle + "] CYCLE_DATE[" + cycle_date 
                                + "] IMP_CONCEPTO[" + imp_concepto + "] COD_UNICOM[" + cod_unicom + "]");
                        log.info("Result[" + result + "]");
                    }
                }
            }
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
        }
        finally {
            if (maxid_stmt != null) try {
                maxid_stmt.close();
            }
            catch (Exception e) {};
            if (g_cd004 != null) try {
                g_cd004.close();
            }
            catch (Exception e) {}
            if (i_cd004 != null) try {
                i_cd004.close();
            }
            catch (Exception e) {}
            if (con != null) try {
                con.close();
            }
            catch (Exception e) {}
        }
    }

    public void insertCD001002003(String filename) {
        InputStream in = null;
        HSSFWorkbook wb = null;
        try {
            in = new FileInputStream(filename);
            wb = new HSSFWorkbook(new POIFSFileSystem(in));
            
            
            Sheet sheet1 = wb.getSheetAt(0);
            int i_ts = 0;
            int i_num_apa = 1;
            int i_co_marca = 2;
            int i_cust_name = 3;
            int i_customer_number = 4;
            int i_service_point_no = 5;
            int i_vendor_id = 6;
            int i_recpt_no = 7;
            int i_pmethod = 8;
            int i_co_concepto = 9;
            int i_imp_concepto = 10;
            int i_cod_unicom = 11;
            int i_operator_name = 12;
            
            int lastRowNum = sheet1.getLastRowNum()+1;
            Cell c_ts = null;
            Cell c_num_apa = null;
            Cell c_cust_name = null;
            Cell c_customer_number = null;
            Cell c_service_point_no = null;
            Cell c_vendor_id = null;
            Cell c_recpt_no = null;
            Cell c_pmethod = null;
            Cell c_co_concepto = null;
            Cell c_imp_concepto = null;
            Cell c_cod_unicom = null;
            Cell c_operator_name = null;
            
            String s_ts = null;
            String s_num_apa = null;
            String s_cust_name = null;
            String s_customer_number = null;
            String s_service_point_no = null;
            String s_vendor_id = null;
            long l_recpt_no = 0l;
            String s_pmethod = null;
            String s_co_concepto = null;
            double d_imp_concepto = 0.0;
            int s_cod_unicom = -1;
            String s_operator_name = null;
            
            Row row = null;
            DataSource ds = ctx.getBean(DataSource.class);
            log.info("ROW_NUM[" + lastRowNum + "]");
            for (int i = 1; i < lastRowNum; i++) {
                row = sheet1.getRow(i);
                c_ts = row.getCell(i_ts);
                c_num_apa = row.getCell(i_num_apa);
                c_cust_name = row.getCell(i_cust_name);
                c_customer_number = row.getCell(i_customer_number);
                c_service_point_no = row.getCell(i_service_point_no);
                c_vendor_id = row.getCell(i_vendor_id);
                c_recpt_no = row.getCell(i_recpt_no);
                c_pmethod = row.getCell(i_pmethod);
                c_co_concepto = row.getCell(i_co_concepto);
                c_imp_concepto = row.getCell(i_imp_concepto);
                c_cod_unicom = row.getCell(i_cod_unicom);
                c_operator_name = row.getCell(i_operator_name);
                
                if (c_ts != null) {
                    s_ts = c_ts.getRichStringCellValue().getString();
                    log.info(s_ts);
                }
                if (c_num_apa != null)
                    s_num_apa = c_num_apa.getRichStringCellValue().getString();
                if (c_cust_name != null)
                    s_cust_name = c_cust_name.getRichStringCellValue().getString();
                if (c_customer_number != null)
                    s_customer_number = c_customer_number.getRichStringCellValue().getString();
                if (c_service_point_no != null)
                    s_service_point_no = c_service_point_no.getRichStringCellValue().getString();
                if (c_vendor_id != null)
                    s_vendor_id = c_vendor_id.getRichStringCellValue().getString();
                if (c_recpt_no != null)
                    l_recpt_no = new Double(c_recpt_no.getNumericCellValue()).longValue();
                if (c_pmethod != null)
                    s_pmethod = c_pmethod.getRichStringCellValue().getString();
                if (c_co_concepto != null)
                    s_co_concepto = c_co_concepto.getRichStringCellValue().getString();
                if (c_imp_concepto != null)
                    d_imp_concepto = c_imp_concepto.getNumericCellValue();
                if (c_cod_unicom != null)
                    s_cod_unicom = new Double(c_cod_unicom.getNumericCellValue()).intValue();
                if (c_operator_name != null)
                    s_operator_name = c_operator_name.getRichStringCellValue().getString();
                
                log.info("TS[" + s_ts + "] NUM_APA[" + s_num_apa + "] CUST_NAME[" + s_cust_name 
                       + "] CUSTOMER_NUMBER[" + s_customer_number + "] SERVICE_POINT_NO[" + s_service_point_no
                       + "] VENDOR_ID[" + s_vendor_id + "] RECPT_NO[" + l_recpt_no + "] PMETHOD[" + s_pmethod
                       + "] CO_CONCEPTO[" + s_co_concepto + "] IMP_CONCEPTO[" + d_imp_concepto 
                       + "] COD_UNICOM[" + s_cod_unicom + "] OPERATOR_NAME[" + s_operator_name + "]");
                insertCD001002003(ds, s_ts, s_num_apa, s_cust_name, s_customer_number, s_service_point_no, s_vendor_id,
                       l_recpt_no, s_pmethod, s_co_concepto, d_imp_concepto, s_cod_unicom, s_operator_name);
            }
        }
        catch (Exception e) {
            log.error(e, e);
        }
        finally {
            try {
                if (wb != null) wb.close();
            }
            catch (Exception e) {}
            try {
                if (in != null) in.close();
            }
            catch (Exception e) {}
        }
    }
    public void insertCD001002003(DataSource ds,
            String ts,
            String num_apa,
            String cust_name,
            String customer_number,
            String service_point_no,
            String vendor_id,
            long recpt_no,
            String pmethod,
            String co_concepto,
            double imp_concepto,
            int cod_unicom,
            String operator_name
            ) {
        log.info("TS[" + ts + "] NUM_APA[" + num_apa + "] CUST_NAME[" + cust_name 
                + "] CUSTOMER_NUMBER[" + customer_number + "] SERVICE_POINT_NO[" + service_point_no
                + "] VENDOR_ID[" + vendor_id + "] RECPT_NO[" + recpt_no + "] PMETHOD[" + pmethod
                + "] CO_CONCEPTO[" + co_concepto + "] IMP_CONCEPTO[" + imp_concepto 
                + "] COD_UNICOM[" + cod_unicom + "] OPERATOR_NAME[" + operator_name + "]");
        
        HibernateTransactionManager txmanager = ctx.getBean(HibernateTransactionManager.class);
        
        Connection con = null;
        PreparedStatement maxTxId = null;
        PreparedStatement insStmt = null;
        ResultSet rs = null;
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            con = ds.getConnection();
            maxTxId = con.prepareStatement("select nvl(max(transaction_id), 0)+1 from prep_trans_ext@CMS_Link where co_concepto=?");
            
            maxTxId.setString(1, co_concepto);
            rs = maxTxId.executeQuery();
            
            long txid = 0l;
            if (rs.next()) {
                txid = rs.getLong(1);
            }
            
            if (txid > 0) {
                insStmt = con.prepareStatement("insert into prep_trans_ext@CMS_LINK"
                        + "(ts, transaction_id, num_apa, co_marca, cust_name, customer_number, service_point_no,"
                        + "vendor_id, recpt_no, pmethod, co_concepto, imp_concepto, cod_unicom, operator_name, co_sistema, exported)"
                        + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                insStmt.setDate(1, new Date(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(ts).getTime()));
                insStmt.setLong(2,  txid);
                insStmt.setString(3, num_apa);
                insStmt.setString(4, "MC013");
                insStmt.setString(5, cust_name);
                insStmt.setString(6, customer_number);
                insStmt.setString(7, service_point_no);
                insStmt.setString(8, vendor_id);
                insStmt.setLong(9, recpt_no);
                insStmt.setString(10, pmethod);
                insStmt.setString(11, co_concepto);
                insStmt.setDouble(12, imp_concepto);
                insStmt.setInt(13, cod_unicom);
                insStmt.setString(14, operator_name);
                insStmt.setString(15, "SE003");
                insStmt.setInt(16, 0);
                
                boolean result = insStmt.execute();
                
                log.info("INSERT RESULT[" + result + "]");
            }
            else {
                log.warn("TxID is 0");
            }
            
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            log.error(e, e);
            if (txstatus != null) txmanager.rollback(txstatus);
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (Exception e) {}
            }
            if (maxTxId != null) {
                try {
                    maxTxId.close();
                }
                catch (Exception e) {}
            }
            if (insStmt != null) {
                try {
                    insStmt.close();
                }
                catch (Exception e) {}
            }
            if (con != null) {
                try {
                    con.close();
                }
                catch (Exception e) {}
            }
        }
    }
    public void run(String filename) {
        InputStream in = null;
        HSSFWorkbook wb = null;
        try {
            in = new FileInputStream(filename);
            wb = new HSSFWorkbook(new POIFSFileSystem(in));

            String text = null;
            Sheet sheet1 = wb.getSheetAt(0);
            Row header = sheet1.getRow(0);
            int idx_geocode = 0;
            int idx_blocker_indicator = 0;
            int idx_nis_rad = 0;
            int idx_cod_cli = 0;
            int idx_customer_id = 0;
            int idx_bill_no = 0;
            int idx_debt_type = 0;
            int idx_meter_serial = 0;
            int idx_customer_no = 0;
            int idx_old_arrears = 0;
            int idx_id_type = 0;
            int idx_id_no = 0;
            int idx_make = 0;
            int idx_model = 0;
            int idx_tariffCode = 0;
            int idx_tariffGroup = 0;
            
            for (Cell cell : header) {
                text = cell.getRichStringCellValue().getString();
                log.info("CELL[" + text + "]");
                if (text.equalsIgnoreCase("METER_GEO_CODE"))
                    idx_geocode = cell.getColumnIndex();
                if (text.equalsIgnoreCase("NIS_RAD"))
                    idx_nis_rad = cell.getColumnIndex();
                if (text.equalsIgnoreCase("COD_CLI"))
                    idx_cod_cli = cell.getColumnIndex();
                if (text.equalsIgnoreCase("CUSTOMER_ID"))
                    idx_customer_id = cell.getColumnIndex();
                if (text.equalsIgnoreCase("BILL_NO"))
                    idx_bill_no = cell.getColumnIndex();
                if (text.equalsIgnoreCase("DEBT_TYPE"))
                    idx_debt_type = cell.getColumnIndex();
                if (text.equalsIgnoreCase("BLOCKER_INDICATOR"))
                    idx_blocker_indicator = cell.getColumnIndex();
                if (text.equalsIgnoreCase("METER_SERIAL_NUMBER"))
                    idx_meter_serial = cell.getColumnIndex();
                if (text.equalsIgnoreCase("OLD_POSTPAID_ACCOUNT_NUMBER"))
                    idx_customer_no = cell.getColumnIndex();
                if (text.equalsIgnoreCase("OLD_ARREARS"))
                    idx_old_arrears = cell.getColumnIndex();
                if (text.equalsIgnoreCase("COD_CLI"))
                    idx_id_no = cell.getColumnIndex();
                if (text.equalsIgnoreCase("MAKE"))
                    idx_make = cell.getColumnIndex();
                if (text.equalsIgnoreCase("MODEL"))
                    idx_model = cell.getColumnIndex();
                if (text.equalsIgnoreCase("TARIFFCODE"))
                    idx_tariffCode = cell.getColumnIndex();
                if (text.equalsIgnoreCase("TARIFFGROUP"))
                    idx_tariffGroup = cell.getColumnIndex();
            }
            
            log.info("GEO_CODE[" + idx_geocode + "] NIS_RAD[" + idx_nis_rad 
                    + "] COD_CLI[" + idx_cod_cli + "] CUSTOMER_ID[" + idx_customer_id
                    + "] BILL_NO[" + idx_bill_no + "] DEBT_TYPE[" + idx_debt_type
                    + "] BLOCKER_INDICATOR[" + idx_blocker_indicator 
                    + "] METER_SERIAL_NUMBER[" + idx_meter_serial 
                    + "] OLD_CUSTOMER_NO[" + idx_customer_no
                    + "] OLD_ARREARS[" + idx_old_arrears 
                    + "] ID_NO[" + idx_id_no
                    + "] MAKE[" + idx_make
                    + "] MODEL[" + idx_model
                    + "] TARIFFCODE[" + idx_tariffCode
                    + "] TARIFFGROUP[" + idx_tariffGroup
                    + "]");
            
            int lastRowNum = sheet1.getLastRowNum()+1;
            Cell geocode = null;
            Cell nisRad = null;
            Cell codCli = null;
            Cell customerId = null;
            Cell billNo = null;
            Cell debtType = null;
            Cell blocker = null;
            Cell meterId = null;
            Cell oldCustomerNo = null;
            Cell oldArrears = null;
            Cell idNo = null;
            Cell make = null;
            Cell model = null;
            Cell tariffCode = null;
            Cell tariffGroup = null;
            
            String s_geocode = null;
            String s_nisRad = null;
            String s_codCli = null;
            String s_customerId = null;
            String s_billNo = null;
            String s_debtType = null;
            int s_blocker = 0;
            String s_meterId = null;
            String s_oldCustomerNo = null;
            String s_oldArrears = null;
            String s_idNo = null;
            String s_make = null;
            String s_model = null;
            String s_tariffCode = null;
            int s_tariffGroup = 0;
            
            Row row = null;
            for (int i = 1; i < lastRowNum; i++) {
                row = sheet1.getRow(i);
                geocode = row.getCell(idx_geocode);
                nisRad = row.getCell(idx_nis_rad);
                codCli = row.getCell(idx_cod_cli);
                customerId = row.getCell(idx_customer_id);
                billNo = row.getCell(idx_bill_no);
                debtType = row.getCell(idx_debt_type);
                blocker = row.getCell(idx_blocker_indicator);
                meterId = row.getCell(idx_meter_serial);
                oldCustomerNo = row.getCell(idx_customer_no);
                oldArrears = row.getCell(idx_old_arrears);
                idNo = row.getCell(idx_id_no);
                make = row.getCell(idx_make);
                model = row.getCell(idx_model);
                tariffCode = row.getCell(idx_tariffCode);
                tariffGroup = row.getCell(idx_tariffGroup);
                
                if (geocode != null)
                    s_geocode = geocode.getRichStringCellValue().getString();
                // if (nisRad != null)
                //    s_nisRad = nisRad.getRichStringCellValue().getString();
                // if (codCli != null)
                //    s_codCli = codCli.getRichStringCellValue().getString();
                if (customerId != null)
                    s_customerId = customerId.getRichStringCellValue().getString();
                if (billNo != null)
                    s_billNo = billNo.getRichStringCellValue().getString();
                if (debtType != null)
                    s_debtType = debtType.getRichStringCellValue().getString();
                if (blocker != null)
                    s_blocker = (int)blocker.getNumericCellValue();
                if (meterId != null)
                    s_meterId = meterId.getRichStringCellValue().getString();
                if (oldCustomerNo != null)
                    s_oldCustomerNo = oldCustomerNo.getRichStringCellValue().getString();
                if (oldArrears != null)
                    s_oldArrears = oldArrears.getRichStringCellValue().getString();
                if (idNo != null)
                    s_idNo = (int)idNo.getNumericCellValue() + "";
                if (make != null)
                    s_make = make.getRichStringCellValue().getString();
                if (model != null)
                    s_model = model.getRichStringCellValue().getString();
                if (tariffCode != null)
                    s_tariffCode = tariffCode.getRichStringCellValue().getString();
                if (tariffGroup != null)
                    s_tariffGroup = (int)tariffGroup.getNumericCellValue();
                
                log.info("ROW_NUM[" + i+ "] GEO_CODE[" + s_geocode + "] NIS_RAD[" + s_nisRad  
                        + "] COD_CLI[" + s_codCli + "] CUSTOMER_ID[" + s_customerId 
                        + "] BILL_NO[" + s_billNo + "] DEBT_TYPE[" + s_debtType
                        + "] BLOCKER_INDICATOR[" + s_blocker 
                        + "] METER_SERIAL_NUMBER[" + s_meterId
                        + "] OLD_CUSTOMER_NO[" + s_oldCustomerNo
                        + "] OLD_ARREARS[" + s_oldArrears 
                        + "] ID_NO[" + s_idNo
                        + "] MAKE[" + s_make
                        + "] MODEL[" + s_model
                        + "] TARIFF_CODE[" + s_tariffCode
                        + "] TARIFF_GROUP[" + s_tariffGroup
                        + "]");
                
                // updateCustomerId(s_meterId, s_oldCustomerNo);
                String[] mdsIds = new String[]{"141110183", "141118935",
                        "14316156", "14316732", "141111333", "141113767",
                        "141108146", "141114559", "14312422", "14315978",
                        "19065710", "141108333", "14315740", "141111552",
                        "14316347", "141110988", "14316728", "141104558"};
                
                boolean twice = false;
                for (String m : mdsIds) {
                    if (m.equals(s_meterId)) {
                        twice = true;
                        break;
                    }
                }
                
                if (!twice) {
                    updateCustomer(s_geocode, s_customerId, s_customerId, s_blocker, s_meterId);
                    createCustomer(s_customerId, s_geocode, s_idNo,
                            s_meterId, s_tariffCode, s_tariffGroup, s_make, s_model);
                    addDebt(s_customerId, s_billNo, s_debtType, s_oldArrears);
                }
                // updateIdNo(s_customerId, s_idNo);
            }
        }
        catch(Exception e) {
            log.error(e, e);
        }
        finally {
            try {
                if (wb != null) wb.close();
            }
            catch (IOException e) {}
        }
    }
    public void createCustomer() {
        String customerId = "200260599-01,200261396-01,200261399-01,200262618-01,200262627-01,200262709-01,200263288-01"
                + ",200263453-01,200263488-01,200263542-01,200264131-01,200264303-01,200264914-01,200264830-01,200264957-01"
                + ",200258097-01,200256513-01,200257139-01,200257144-01,200259390-01,200259429-01,200261202-01,200253647-01"
                + ",200253648-01,200260444-01,200260516-01,200256264-01,200260990-01,200250089-01,200250114-01,200249867-01"
                + ",200255189-01,200250256-01,200256067-01,200256130-01,200248267-01,200256152-01,200254388-01,200255939-01"
                + ",200259101-01,200259102-01,200258953-01,200254947-01,200258620-01,200258668-01,200254094-01,200253862-01"
                + ",200258790-01,200249400-01,200247736-01,200249290-01,200252579-01,200252640-01,200249410-01,200249426-01"
                + ",200246824-01,200246825-01,200246836-01,200251797-01,200251325-01,200251400-01,200250931-01,200252108-01"
                + ",200251581-01,200250503-01,200252247-01,200268286-01,200265696-01,200267187-01,200265275-01,200265557-01"
                + ",200265595-01,200267569-01,200268160-01,200247169-01,200248063-01,200248940-01,200248967-01,200248849-01"
                + ",200267595-01,200265187-01";
        StringTokenizer st = new StringTokenizer(customerId, ",");
        while (st.hasMoreTokens()) {
            // createCustomer(st.nextToken());
        }
    }
    
    private void createCustomer(String customerId, String geoCode, String idNo,
            String meterId, String tariffCode, int tariffGroup, String make, String model) 
    throws Exception {
        log.info("CUSTOMER_ID[" + customerId + "]");
        HibernateTransactionManager txmanager = (HibernateTransactionManager)ctx.getBean("transactionManager");
        CustomerDao customerDao = ctx.getBean(CustomerDao.class);
        ContractDao contractDao = ctx.getBean(ContractDao.class);
        CustEntDao custDao = ctx.getBean(CustEntDao.class);
        ServPointDao servPointDao = ctx.getBean(ServPointDao.class);
        MeterEntDao meterEntDao = ctx.getBean(MeterEntDao.class);
        
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            Contract contract = contractDao.findByCondition("servicePointId", customerId);
            
            if (contract.getCustomer() != null) {
                Customer _customer = contract.getCustomer();
                Customer customer = new Customer();
                customer.setCustomerNo(customerId);
                customer.setName(_customer.getName());
                customer.setAliasName(_customer.getAliasName());
                customer.setAddress(_customer.getAddress());
                customer.setAddress1(_customer.getAddress1());
                customer.setAddress2(_customer.getAddress2());
                customer.setAddress3(_customer.getAddress3());
                customer.setEmail(_customer.getEmail());
                customer.setEmailYn(_customer.getEmailYn());
                customer.setTelephoneNo(_customer.getTelephoneNo());
                customer.setTelNo(_customer.getTelNo());
                customer.setWorkTelephone(_customer.getWorkTelephone());
                customer.setMobileNo(_customer.getMobileNo());
                customer.setMobileNumber(_customer.getMobileNumber());
                customer.setSmsYn(_customer.getSmsYn());
                customer.setLocation(_customer.getLocation());
                customer.setSupplier(_customer.getSupplier());
                
                // customerDao.add(customer);
                
                // contract.setCustomer(customer);
                // contractDao.update(contract);
                
                CustEnt custEnt = custDao.findByCondition("customerId", customerId);
                if (custEnt == null) {
                    custEnt = new CustEnt();
                    custEnt.setCustomerId(customerId);
                    custEnt.setSurname(customer.getName());
                    custEnt.setOtherNames(customer.getAliasName());
                    custEnt.setAddress1(customer.getAddress());
                    custEnt.setAddress2(customer.getAddress1());
                    custEnt.setAddress3(customer.getAddress3());
                    custEnt.setEmail(customer.getEmail());
                    custEnt.setExist(true);
                    custEnt.setTelephone1(customer.getMobileNo());
                    custEnt.setTelephone2(customer.getTelNo());
                    custEnt.setTelephone3(customer.getWorkTelephone());
                    custEnt.setIdNo(idNo);
                    custEnt.setTaxRefNo(idNo);
                    custEnt.setIdType("TD000");
                    
                    custDao.add(custEnt);
                }
                
                MeterEnt meterEnt = meterEntDao.findByCondition("id.meterSerialNo", contract.getMeter().getMdsId());
                if (meterEnt == null) {
                    meterEnt = new MeterEnt();
                    meterEnt.setMake(make);
                    meterEnt.setMeterSerialNo(meterId);
                    meterEnt.setModel(model);
                    meterEnt.setWriteDate(DateTimeUtil.getDateString(new java.util.Date()));
                    
                    meterEntDao.add(meterEnt);
                }
                
                ServPoint servPoint = servPointDao.findByCondition("servPointId", customerId);
                    
                if (servPoint != null) {
                    custEnt.setServPoint(servPoint);
                    servPoint.setCustEnt(custEnt);
                    
                    if (contract.getMeter() != null)
                        servPoint.setMeterSerialNo(contract.getMeter().getMdsId());
                    
                    servPointDao.update(servPoint);
                    custDao.update(custEnt);
                    
                    log.info("CUSTOMER_ID[" + customer.getCustomerNo() + 
                            "] SERVICE_POINT_ID[" + custEnt.getServPoint().getServPointId() +
                            "] CUST_ENT[" + custEnt.getCustomerId() + "]");
                }
                else {
                    servPoint = new ServPoint();
                    servPoint.setAddress1(customer.getAddress());
                    servPoint.setAddress2(customer.getAddress1());
                    servPoint.setAddress3(customer.getAddress3());
                    servPoint.setBlockFlag(!contract.getChargeAvailable());
                    servPoint.setBlockReason("");
                    servPoint.setCustEnt(custEnt);
                    servPoint.setExist(true);
                    servPoint.setGeoCode(geoCode);
                    servPoint.setMeter(meterEnt);
                    servPoint.setMeterSerialNo(contract.getMeter().getMdsId());
                    servPoint.setServPointId(contract.getServicePointId());
                    
                    TariffEnt tariff = new TariffEnt();
                    tariff.setTariffCode(tariffCode);
                    tariff.setTariffGroup(tariffGroup);
                    servPoint.setTariff(tariff);
                    servPoint.setWriteDate(DateTimeUtil.getDateString(new java.util.Date()));
                    
                    servPointDao.add(servPoint);
                    
                    custEnt.setServPoint(servPoint);
                    custDao.update(custEnt);
                }
            }
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
            throw e;
        }
    }
    
    private void updateIdNo(String customerId, String idNo) {
        HibernateTransactionManager txmanager = (HibernateTransactionManager)ctx.getBean("transactionManager");
        CustEntDao custEntDao = ctx.getBean(CustEntDao.class);
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            CustEnt custEnt = custEntDao.findByCondition("customerId", customerId);
            if (custEnt != null) {
                custEnt.setIdNo(idNo);
                custEnt.setTaxRefNo(idNo);
            }
        }
        catch (Exception e) {
            log.warn("CUSTOMERID[" + customerId + "] ID_NO[" + idNo + "]");
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
    
    private void updateLocation() {
        HibernateTransactionManager txmanager = (HibernateTransactionManager)ctx.getBean("transactionManager");
        ContractDao contractDao = ctx.getBean(ContractDao.class);
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            List<Contract> contracts = contractDao.getAll();
            txmanager.commit(txstatus);
            
            for (Contract c : contracts) {
                updateLocation(c);
            }
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
        }
    }
    
    private void updateLocation(Contract c) {
        HibernateTransactionManager txmanager = (HibernateTransactionManager)ctx.getBean("transactionManager");
        ContractDao contractDao = ctx.getBean(ContractDao.class);
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Contract _c = contractDao.get(c.getId());
            if (_c.getMeter() != null && _c.getLocation() != null)
                _c.getMeter().setLocation(_c.getLocation());
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
        }
    }
    
    private void updateCustomer(String geocode, String customerId, String servicePointId, int blocker, String meterId)
    throws Exception
    {
        HibernateTransactionManager txmanager = (HibernateTransactionManager)ctx.getBean("transactionManager");
        MeterDao meterDao = ctx.getBean(MeterDao.class);
        ContractDao contractDao = ctx.getBean(ContractDao.class);
        
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Meter meter = meterDao.get(meterId);
            
            Contract contract = meter.getContract();
            Customer customer = null;
            
            if (contract != null) {
                customer = contract.getCustomer();
                if (customer != null)
                    customer.setCustomerNo(customerId);
                
                Set<Condition> conditions = new HashSet<Condition>();
                conditions.add(new Condition("contractNumber", new Object[]{"%"+geocode+"%"}, null, Restriction.LIKE));
                List<Contract> contracts = contractDao.findByConditions(conditions);
                log.info("CONTRACT[" + geocode + "] SIZE[" + contracts.size() + "]");
                if (contracts != null && contracts.size() == 0)
                    contract.setContractNumber(geocode);
                else {
                    if (!contract.getContractNumber().contains(geocode))
                        contract.setContractNumber(geocode+(contracts.size()));
                }
                contract.setServicePointId(servicePointId);
                contract.setChargeAvailable(blocker == 1? false:true);
            }
            else {
                log.warn("GEO_CODE[" + geocode + "] SERVICE_POINT_ID[" + servicePointId + "] METER_ID[" + meterId + "] not exist");
            }
            /*
            if (contracts == null || contracts.size() == 0) {
                MeterDao meterDao = ctx.getBean(MeterDao.class);
                Meter meter = meterDao.get(meterId);
                
                if (meter != null && meter.getContract() != null) {
                    Contract c = meter.getContract();
                    if (c != null) {
                        c.setContractNumber(geocode);
                        c.setServicePointId(servicePointId);
                        c.setChargeAvailable(blocker == 1? false:true);
                    
                        Customer customer = c.getCustomer();
                        if (customer != null) {
                            if (!customer.getCustomerNo().equals(customerId))
                                customer.setCustomerNo(customerId);
                        }
                    }
                }
                else {
                    log.warn("GEO_CODE[" + geocode + "] SERVICE_POINT_ID[" + servicePointId + "] METER_ID[" + meterId + "] not exist");
                }
            }
            */
            /*
            else if (contracts.size() > 1) {
                Customer customer = null;
                Meter meter = null;
                for (Contract c : contracts) {
                    customer = c.getCustomer();
                    meter = c.getMeter();
                    log.warn("GEO_CODE[" + c.getContractNumber() + 
                            "] CUSTOMER_NO[" + customer != null? customer.getCustomerNo():"" + 
                            "] METER_ID[" + meter != null? meter.getMdsId():"" + "]");
                    
                    if (meter != null) {
                        if (meter.getMdsId().equals(meterId)) {
                            c.setServicePointId(servicePointId);
                            c.setChargeAvailable(blocker == 1? false:true);
                            
                            if (customer != null) {
                                if (!customer.getCustomerNo().equals(customerId))
                                    customer.setCustomerNo(customerId);
                            }
                        }
                    }
                }
            }
            else {
                Contract contract = contracts.get(0);
                Customer customer = contract.getCustomer();
                
                if (customer != null && !customer.getCustomerNo().equalsIgnoreCase(customerId))
                    customer.setCustomerNo(customerId);
                
                contract.setServicePointId(servicePointId);
                contract.setChargeAvailable(blocker == 1? false:true);
            }
            */
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
    
    private void updateCustomerId(String meterId, String oldCustomerNo)
    throws Exception
    {
        HibernateTransactionManager txmanager = (HibernateTransactionManager)ctx.getBean("transactionManager");
        ContractDao contractDao = ctx.getBean(ContractDao.class);
        CustomerDao customerDao = ctx.getBean(CustomerDao.class);
        
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("meter", new Object[]{"m"}, null, Restriction.ALIAS));
            condition.add(new Condition("m.mdsId", new Object[]{meterId}, null, Restriction.EQ));
            List<Contract> contracts = contractDao.findByConditions(condition);
            
            if (contracts.size() > 1) {
                Customer customer = null;
                Meter meter = null;
                for (Contract c : contracts) {
                    customer = c.getCustomer();
                    meter = c.getMeter();
                    log.warn("GEO_CODE[" + c.getContractNumber() + 
                            "] CUSTOMER_NO[" + customer != null? customer.getCustomerNo():"" + 
                            "] METER_ID[" + meter != null? meter.getMdsId():"" + "]");
                }
            }
            else if (contracts.size() == 1){
                Contract contract = contracts.get(0);
                Customer customer = contract.getCustomer();
                
                if (customer == null) return;
                
                log.info("GEO_CODE[" + contract.getContractNumber() + "] CUSTOMER_NO[" + customer.getCustomerNo() + "]");
                if (oldCustomerNo != null && customer != null && !customer.getCustomerNo().equalsIgnoreCase(oldCustomerNo)) {
                    Customer _customer = customerDao.findByCondition("contractNumber", oldCustomerNo);
                    if (_customer == null)
                        customer.setCustomerNo(oldCustomerNo);
                }
            }
        }
        catch (Exception e) {
            log.warn(e, e);
        }
        finally {
            try {
                if (txstatus != null) txmanager.commit(txstatus);
            }
            catch (Exception e) {
                if (txstatus != null) txmanager.rollback(txstatus);
            }
        }
    }

    private void addDebt(String customerId, String billNo, String debtType, String oldArrears)
    throws Exception
    {
        if (billNo == null || billNo.equals(""))
            return;
        
        HibernateTransactionManager txmanager = (HibernateTransactionManager)ctx.getBean("transactionManager");
        DebtEntDao debtDao = ctx.getBean(DebtEntDao.class);
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("id.customerId", new Object[]{customerId}, null, Restriction.EQ));
            condition.add(new Condition("id.debtRef", new Object[]{billNo}, null, Restriction.EQ));
            
            List<DebtEnt> debts = debtDao.findByConditions(condition);
            if (debts == null || debts.size() == 0) {
                DebtEnt debt = new DebtEnt();
                debt.setCustomerId(customerId);
                debt.setDebtRef(billNo);
                debt.setDebtStatus("ACTIVE");
                debt.setDebtType(debtType);
                if (oldArrears != null && !"".equals(oldArrears))
                    debt.setDebtAmount(Double.parseDouble(oldArrears));
                
                log.info(debt.toString());
                debtDao.add(debt);
            }
            else if (debts.size() == 1) {
                DebtEnt debt = debts.get(0);
                debt.setDebtStatus("ACTIVE");
                debt.setDebtType(debtType);
                if (oldArrears != null && !"".equals(oldArrears))
                    debt.setDebtAmount(Double.parseDouble(oldArrears));
                
                log.info(debt.toString());
                debtDao.update(debt);
            }
        }
        catch (Exception e) {
            log.warn("SERVICE_POINT_ID[" + customerId + "] CUSTOMER_ID["
        + customerId + "] BILL_NO[" + billNo + " DEBT_TYPE[" + debtType 
        + "] OLD_ARREARS[" + oldArrears + "] fail to create");
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }
}
