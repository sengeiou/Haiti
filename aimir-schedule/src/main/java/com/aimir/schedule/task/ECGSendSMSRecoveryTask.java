package com.aimir.schedule.task;

import java.lang.reflect.Method;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.util.SAPProperty;
import com.aimir.util.DecimalUtil;

/**
 * 
 *
 * ECGSendSMSRecoveryTask.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2014. 6. 17.   v1.0       김지애       ECG에서 잔액통보 문자를 보낸 후 네트워크상의 문제로 문자가 전송되지 않은 경우를 위해 재 전송.         
 * </pre>
 */
@Transactional
public class ECGSendSMSRecoveryTask extends ScheduleTask {
	
	private static Log log = LogFactory.getLog(ECGSendSMSRecoveryTask.class);
	
    @Resource(name="transactionManager")
    HibernateTransactionManager txManager;
	
	@Autowired
	SupplierDao supplierDao;
	
    @Autowired
	CodeDao codeDao;
    
	@Autowired
	ContractDao contractDao;
	
	@Autowired
	CustomerDao customerDao;
	
	@Autowired
    MeterDao meterDao;
	
	URL url = null;
	String baseURL= null;
	String SMSGHId = null;
	String SMSGHPass = null;
	String textTargetVal = null;
	String textPeriod = null;
	private boolean isNowRunning = false;
	
    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"spring-forcrontab.xml"}); 
        DataUtil.setApplicationContext(ctx);
        
        ECGSendSMSRecoveryTask task = ctx.getBean(ECGSendSMSRecoveryTask.class);
        task.execute(null);
        System.exit(0);
    }

	@Override
    public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### ECGSendSMSRecoveryTask is already running...");
			return;
		}
		isNowRunning = true;
        log.info("########### 1. START ECGSendSMSRecoveryTask ###############");
        
        try {
            baseURL = SAPProperty.getProperty("prepay.sms.baseUrl");
            SMSGHId = SAPProperty.getProperty("prepay.sms.id"); 
            SMSGHPass = SAPProperty.getProperty("prepay.sms.pass"); 
        
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("prepayCreditId", codeDao.getCodeIdByCode("2.2.1"));
            condition.put("emergencyICreditId", codeDao.getCodeIdByCode("2.2.2"));
            condition.put("smsYn", true);
            condition.put("isRecovery", true);
            
            //sms를 받기로 설정하고 모바일번호를 가지고 있는 선불고객을 검색 _ 계약이 그룹지어 있는지 아닌지 여부를 판단후 각각 조건에 맞게 검색해온다.
            List<Map<String, Object>> contractInfo = contractDao.getContractSMSYN(condition);
            
            log.debug("contractInfoByGroup size : " + contractInfo.size());
            
            if(contractInfo.size() > 0) {
                this.sendSMSRecovery(contractInfo); 
            }
            
            // setSuccessResult();
        } catch (Exception e) {
            // setFailResult();
            log.error(e,e);
        }

        log.info("########### 1. END ECGSendSMSRecoveryTask ############");
        isNowRunning = false;
    }
	

    private void sendSMSRecovery(List<Map<String, Object>> contractInfo) {
        Supplier supplier = null;
        Double currentCredit = 0d;
        TransactionStatus txStatus = null;
        String msg = null;
        String mobileNo = null;
        
        for (int i = 0; i < contractInfo.size(); i++) {
        	Contract contract = null;
            try {
            	txStatus = txManager.getTransaction(null);
                contract = contractDao.get(Integer.parseInt(contractInfo.get(i).get("CONTRACTID").toString()));
                if (contract.getMeter() == null) {
                	txManager.commit(txStatus);
                	continue;
                }
                Meter meter = meterDao.get(contract.getMeterId());
                String mdsId =meter.getMdsId();
                supplier = supplierDao.get(contract.getSupplierId());
                DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
                
                Double targetVal = Double.parseDouble(
                        contractInfo.get(i).get("PREPAYMENTTHRESHOLD") == null ? "0" : contractInfo.get(i).get("PREPAYMENTTHRESHOLD").toString());
                currentCredit = contractInfo.get(i).get("CURRENTCREDIT") == null ? 0d : Double.parseDouble(contractInfo.get(i).get("CURRENTCREDIT").toString());
                
                
                if(contractInfo.get(i).get("GROUP_MOBILENO") == null) {
                    mobileNo = contractInfo.get(i).get("MOBILENO").toString().replace("-", "");
                } else {
                    //그룹의 경우 그룹의 대표번호로 SMS를 보낸다.
                    mobileNo = contractInfo.get(i).get("GROUP_MOBILENO").toString().replace("-", "");
                }

                if(targetVal >= currentCredit ) {
                    //targetValue값보다 현재 값이 적은 경우에 문자전송이 실패한 경우
                    log.debug("Target Value contractNumber[" + contract.getContractNumber() + "]");
                    
                    msg = targetValMsg(contractInfo.get(i), mdsId, cdf, currentCredit, targetVal);

                } else {
                    //주기적으로 보내는 문자전송이 실패했을 경우
                    msg = periodMsg(contractInfo.get(i), mdsId, cdf, currentCredit);   
                    
                }
            } catch (Exception e) {
            	txManager.commit(txStatus);
                log.error(e,e);
                continue;
            }
            
            try{
                String messageId = notificationSMS(mobileNo, msg);
                
                //재전송 후의 messageId 저장
                contractDao.updateSmsNumber(contract.getId(), messageId+":prepaySendSMS");
                txManager.commit(txStatus);
            } catch (Exception e) {
                if (txStatus != null) {
                    try {
                        txManager.rollback(txStatus);
                    }
                    catch (Exception ee) {}
                }
                log.error(e,e);
            }
        }
    }
    
    private String targetValMsg(Map<String, Object> contractInfo, String mdsId, DecimalFormat cdf, Double currentCredit, Double targetVal) {
        
        textTargetVal = "You have reached your target credit value. Kindly top up to avoid inconvenience."
                + "\n Customer Name : " + contractInfo.get("CUSTOMERNAME")
                + "\n METER ID : " + mdsId
                // spasa 전용 문자 항목 ecg에서는 contract nr를 사용하지 않음
                //+ "\n Contract nr : " + contract.getContractNumber()
                + "\n Current Credit : " +  cdf.format(currentCredit);
        
        return textTargetVal;
    }
    
    private String periodMsg(Map<String, Object> contractInfo, String mdsId, DecimalFormat cdf, Double currentCredit) {
        
        textPeriod = "Customer Name : " + contractInfo.get("CUSTOMERNAME")
                + "\n METER ID : " + mdsId
                + "\n Supply Type : " + contractInfo.get("SERVICETYPE")
                + "\n Current Credit : " +  cdf.format(currentCredit);
        
        return textPeriod;
    }
	
	private String notificationSMS(String mobileNo, String text) {
    	String messageId=null;
    	Properties prop = new Properties();
    	try {
    		prop.load(getClass().getClassLoader().getResourceAsStream("config/schedule.properties"));
    		log.info("prop load : "+prop.containsKey("smsClassPath"));

			System.out.println("########## txt = "+text+" ##########");
			
			log.info("START SMS");
			String smsClassPath = prop.getProperty("smsClassPath");
			log.info("smsClassPath : "+smsClassPath);
			SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
			Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
			messageId = (String) m.invoke(obj, mobileNo, text, prop);
			log.info("FINISHED SMS");
				
    	} catch (Exception e) {
    		log.error(e,e);
		}
    	
		return messageId;
    }
}
