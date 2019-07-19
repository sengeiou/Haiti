package com.aimir.schedule.task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.ECGBillingIntegrationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.ECGBillingIntegration;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.util.SAPProperty;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.TimeUtil;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2012</p>
 * 
 * 매달 말일 기준일의 지침값을 조회하여 BillingForamt을 생성한다.
 * 1. GEO-ID(22A) : 고객 계약 번호로 등록되어 있다.
 * 2. METERAD(10N) : Meter의 last metering value
 * 3. RESETCD(1A) : blank, reset X
 * 4. RDTYPCD(1A) : actual 0, estimate 1
 * 5. RDGDD(2N) : 일
 * 6. RDGMM(2N) : 월
 * 7. RDGYY(4N) : 년
 * 8. MTREDNO(6A) : Meter Reader Number (고객번호로 되어 있다)  
 * 9. BATCHNO(14A) : 배치번호, ECGBillingIntegration의 batchNo
 * 10. VFLG(1A) : V
 * 
 * Date          Version     Author
 * 2012. 11. 16.   v1.0       elevas       
 *
 */
public class ECGBillingIntegrationTask extends ScheduleTask {
	
	private static Log log = LogFactory.getLog(ECGBillingIntegrationTask.class);
	
	@Resource(name="transactionManager")
	HibernateTransactionManager tx;
	
	@Autowired
	ECGBillingIntegrationDao ebiDao;
	
	@Autowired
	MeterDao meterDao;
	
	@Autowired
	SupplierDao supplierDao;
	
	private boolean isNowRunning = false;
	
	// 빌링 배치 처리 결과
	ECGBillingIntegration ebi = null;
	
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-forcrontab.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        ECGBillingIntegrationTask task = ctx.getBean(ECGBillingIntegrationTask.class);
        task.execute(null);
        System.exit(0);
    }
    
	@Override
	public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### ECGBillingIntegrationTask is already running...");
			return;
		}
		isNowRunning = true;
		
	    log.info("########### START BILLING PROCESS ###############");
	    makeBillingFile();
	    
	    Boolean isSend = Boolean.parseBoolean(SAPProperty.getProperty("sap.send.file"));
	    
	    if (isSend) {
    	    try {
    	        log.info("########### SEND BILLING FILE ##############");
        	    // 위에서 생성한 파일을 FTP로 전송한다.
        	    sendFile();
        	    log.info("########### END SENDING ##############");
    	    }
    	    catch (Exception e) {
    	        log.error(e);
    	    }
	    }
	    else log.info("########## SEND OPTION FALSE ###########");
	    
	    log.info("########### END BILLING PROCESS ############");
	    isNowRunning = false;
	    
	}//execute end
	
	private void makeBillingFile() {
	    // 파일 경로를 가져온다.
	    String filePath = SAPProperty.getProperty("sap.ftp.inboundFilePath");
	    
	    // 파일당 처리 건수를 가져온다.
	    int countPerFile = Integer.parseInt(SAPProperty.getProperty("sap.file.row.count"));
	    
	    BufferedWriter out = null;
	    TransactionStatus txStatus = null;
	    try {
	        txStatus = tx.getTransaction(null);
	        
            // 공급사 정보를 가져온다. ECG
            Supplier supplier = supplierDao.getSupplierByName(SAPProperty.getProperty("sap.supplier.name"));
            String datetime = TimeUtil.getCurrentTime();
            String prefix = makePrefix(supplier.getLang().getName());
            
            // 미터 전체 건수를 가져온다.
            int meterCount = getTotalMeterCount(datetime, supplier.getId());
            log.debug("TOTAL_METER_COUNT[" + meterCount + "]");
            
            // 미터의 last read date가 금일 날짜에 해당하는 last metering value를 가져온다.
            List<Meter> meterList = getMeterList(datetime, supplier.getId());
            log.debug("METER_READING_COUNT[" + meterList.size() + "]");

            // ECGBillingIntegration을 생성하고 batchNo를 얻어온다.
            ebi = createBilling(datetime, meterCount);
            
            // Contract 정보를 가져온다. GEO-CODE
            Contract contract = null;
            int processCount = 0;
            String row = null;
            int fileCount = 0;
            String filename = null;
            
            for (Meter meter : meterList) {
                contract = meter.getContract();
                
                // Contract 정보가 없으면 skip한다.
                if (contract == null || contract.getContractNumber() == null) {
                    log.warn("METER[" + meter.getMdsId() + "] doesn't have GEO-CODE");
                    continue;
                }
                
                if (contract.getContractNumber().length() != 22) {
                    log.warn("METER[" + meter.getMdsId() + "] has wrong GEO-CODE[" + contract.getContractNumber() + "]");
                    continue;
                }
                    
                row = makeBillingRow(contract.getContractNumber(),
                        meter.getLastMeteringValue(), ebi.getMeterReadingDate(), 
                        meter.getMdsId(), ebi.getBatchNo());
                
                // 파일을 변경한다.
                if (processCount % countPerFile == 0) {
                    fileCount++;
                    
                    filename = prefix + String.format("%02d", fileCount)+".txt";
                    
                    // 생성하기 전에 열린 것이 있으면 닫는다.
                    if (out != null) out.close();
                    out = new BufferedWriter(new FileWriter(new File(filePath, filename)));
                }
                processCount++;
                
                // filename에 row를 작성한다.
                out.write(row);
                out.newLine();
            }
            
            ebi.setTotalReadingCount(processCount);
            
            ebiDao.update(ebi);
            
            tx.commit(txStatus);
        }
        catch (Exception e) {
            log.error(e);
            if (txStatus != null) tx.rollback(txStatus);
        }
	    finally {
	        try {
	            if (out != null) out.close();
	        }
	        catch (Exception e) {}
	    }
	}
	
	private String makePrefix(String language) {
	    Calendar cal = Calendar.getInstance();
        String prefix = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT,
                new Locale("English")).toUpperCase()+"_BG";
        
        return prefix;
	}
	
	private List<Meter> getMeterList(String datetime, int supplierId) {
	    Set<Condition> cond = new HashSet<Condition>();
        cond.add(new Condition("lastReadDate", new Object[]{datetime.substring(0,8)+"%"}, null, Restriction.LIKE));
        cond.add(new Condition("supplier.id", new Object[]{supplierId}, null, Restriction.EQ));
        
        return meterDao.findByConditions(cond);
	}
	
	private int getTotalMeterCount(String datetime, int supplierId) {
	    Map<String, Object> params = new HashMap<String, Object>();
        params.put("searchStartDate", datetime);
        params.put("supplierId", supplierId);
        params.put("meterType", "Meter");
        return meterDao.getMeterCount(params);
	}
	
	private ECGBillingIntegration createBilling(String datetime, int meterCount) {
	    // ECGBillingIntegration을 생성하고 batchNo를 얻어온다.
        ECGBillingIntegration ebi = new ECGBillingIntegration();
        ebi.setWriteDate(datetime);
        ebi.setMeterReadingDate(datetime.substring(0, 8));
        ebi.setTotalMeterCount(meterCount);
        
        return ebiDao.add(ebi);
	}
	
	private String makeBillingRow(String geoCode, double mtread, String readdate, String mtredno, int batchno) {
	    StringBuffer billRow = new StringBuffer();
	    billRow.append(geoCode.replace("-", ""));
	    billRow.append(String.format("%010d", (int)mtread));
	    billRow.append(" ");
	    billRow.append("0");
	    billRow.append(readdate.substring(6,8));
	    billRow.append(readdate.substring(4,6));
	    billRow.append(readdate.substring(0,4));
	    billRow.append(mtredno.substring(mtredno.length()-6));
	    billRow.append(String.format("%013d", batchno));
	    billRow.append(" ");
	    billRow.append("V");
	    
	    return billRow.toString();
	}
	
	private void sendFile() throws NumberFormatException, SocketException, IOException  {
	    String id = SAPProperty.getProperty("sap.ftp.username", "");
        String password = SAPProperty.getProperty("sap.ftp.password", "");
        String url = SAPProperty.getProperty("sap.ftp.url", "");
        String port = SAPProperty.getProperty("sap.ftp.port", "");
        
        //컨넥션
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("UTF-8");

        // 접속 및 로그인
        ftp.connect(url, Integer.parseInt(port));

        if( !ftp.login(id, password) ) {
            log.error("LOGIN FAILED!!!");
            ftp.disconnect();
            ebi.setSendResult(false);
            ebiDao.update(ebi);
            return;
        }
        
        FileInputStream fis = null;
        
        StringBuffer sfile = new StringBuffer("S:");
        StringBuffer ffile = new StringBuffer("F:");

        File filePath = new File(SAPProperty.getProperty("sap.ftp.inboundFilePath"));
        String backupPath = SAPProperty.getProperty("sap.ftp.inboundFileBackupPath");
        
        for (File f : filePath.listFiles()) {
            // 디렉토리는 처리하지 않는다.
            if (f.isDirectory()) continue;
            
            try {
                fis = new FileInputStream(f);

                if( !ftp.storeFile(f.getName(), fis) ) {
                    log.debug("UPLOAD FAILED!!");
                    ffile.append(f.getName()+",");
                }
                else {
                    sfile.append(f.getName()+",");
                    // backup 디렉토리로 백업한다.
                    f.renameTo(new File(backupPath, f.getName()));
                }
            }
            catch(FileNotFoundException e) {
                log.debug("Not Found file, File Path : " + f.getName());
                ffile.append(f.getName()+",");
            }
            finally {
                if(fis != null) {
                    fis.close();
                }
            }
        }
        
        ftp.logout();
        
        if (ffile.length() > 0) ebi.setSendResult(false);
        else ebi.setSendResult(true);
        
        ebiDao.update(ebi);
	}
}