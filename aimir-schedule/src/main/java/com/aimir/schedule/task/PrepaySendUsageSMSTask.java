package com.aimir.schedule.task;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.LanguageDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Language;
import com.aimir.model.system.Supplier;
import com.aimir.util.DecimalUtil;
import com.aimir.util.TimeUtil;

/**
 * 
 * @author jiae
 * Prepayment Customer Management 가젯에서 [Your balance Alert]에 설정된 정보대로 고객에게 사용량정보를 SMS로 전송하는 Task
 * Period의 경우 조건없이 스케줄러가 돌때 전송된다.
 *
 */
@Transactional
public class PrepaySendUsageSMSTask extends ScheduleTask {
	
	private static Log log = LogFactory.getLog(PrepaySendUsageSMSTask.class);
	
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
	
	@Autowired
	LanguageDao languageDao;
    
	Properties messageProp = null;
	
	String mobileNo = null;
	String text = null;
	private boolean isNowRunning = false;

	@Override
    public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### PrepaySendUsageSMSTask is already running...");
			return;
		}
		isNowRunning = true;
		
        log.info("########### 1. START prepayUsageSMSSend ###############");
        
        this.ContractInfo();

        log.info("########### 1. END prepayUsageSMSSend ############");
        isNowRunning = false;
    }
	
	public void ContractInfo() {
		try {
			
			String today = TimeUtil.getCurrentDay();
			String formarDay = today.substring(6,8);
			
			String[] channelArr = {"1"};
			//Preday, today 순서대로 입력
			String[] dayArr = {TimeUtil.getPreDay(today).substring(6,8), formarDay};
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("prepayCreditId", codeDao.getCodeIdByCode("2.2.1"));
			condition.put("emergencyICreditId", codeDao.getCodeIdByCode("2.2.2"));
			condition.put("smsYn", true);
			condition.put("channelArr", channelArr);
			condition.put("meterType", "Month_EM");
			condition.put("yyyymm", today.substring(0,6));
			condition.put("dayArr", dayArr);
			//sms를 받기로 설정하고 모바일번호를 가지고 있는 선불고객을 검색 _ 계약이 그룹지어 있는지 아닌지 여부를 판단후 각각 조건에 맞게 검색해온다.
			List<Map<String, Object>> contractInfoByNotGroup = contractDao.getContractUsageSMSNOTGroup(condition);
			List<Map<String, Object>> contractInfoByGroup = contractDao.getContractSMSYNWithGroup(condition);
			
			log.info("########### 1. Start Contract Not in Group ############");
			log.info("contractInfoByNotGroup size : " + contractInfoByNotGroup.size());
			if(contractInfoByNotGroup.size() > 0) {
				this.sendSMSTargetVal(contractInfoByNotGroup); 
				this.sendSMSPeriod(contractInfoByNotGroup);
			}
			log.info("########### 1. End Contract Not in Group ############");

			log.info("########### 2. Start Contract in Group ############");
			log.debug("contractInfoByGroup size : " + contractInfoByGroup.size());
			if(contractInfoByGroup.size() > 0) {
				this.sendSMSTargetVal(contractInfoByGroup);
				this.sendSMSPeriod(contractInfoByNotGroup);
			}
			log.info("########### 2. End Contract in Group ############");
		} catch (Exception e) {
			log.error(e,e);
		}
	}
	
	private Properties getMessageProp(Supplier supplier){
		try {
			if(messageProp == null){
				messageProp = new Properties();
				Language la = languageDao.get(supplier.getLangId());
				String lang = (la.getCode_2letter() == null) ? "en" : la.getCode_2letter();
		        InputStream ips = getClass().getClassLoader().getResourceAsStream("lang/message_"+ lang +".properties");
		        if(ips == null){
		        	ips = getClass().getClassLoader().getResourceAsStream("message_en.properties");	        	
		        }
		        messageProp.load(ips);			
			}			
		} catch (Exception e) {
			log.debug(e);
		}
        
        return this.messageProp;
	}
	
	/**
	 * 
	 * @param contractInfo
	 * 설정한 Target보다 현재금액이 작을 경우 SMS를 보낸다.
	 */
	public void sendSMSTargetVal(List<Map<String, Object>> contractInfo) {
		Supplier supplier = null;
		String preDayUsage = "";
		String toDayUsage = "";
		Double currentCredit = 0d;
		HttpURLConnection urlConnection = null;
		int cnt = 0;
		int totalCnt = contractInfo.size();
		for (int i = 0; i < totalCnt; i++) {
			try {
				Contract contract = contractDao.get(Integer.parseInt(contractInfo.get(i).get("CONTRACTID").toString()));
				if (contract.getMeter() == null) continue;
				Meter meter = meterDao.get(contract.getMeterId());
				String mdsId =meter.getMdsId();
				supplier = supplierDao.get(contract.getSupplierId());
				
				DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
				
				Double targetVal = Double.parseDouble(
						contractInfo.get(i).get("PREPAYMENTTHRESHOLD") == null ? "0" : contractInfo.get(i).get("PREPAYMENTTHRESHOLD").toString());
				currentCredit = contractInfo.get(i).get("CURRENTCREDIT") == null ? 0d : Double.parseDouble(contractInfo.get(i).get("CURRENTCREDIT").toString());
				preDayUsage = contractInfo.get(i).get("PREDAY") == null ? "uncertain" : mdf.format((Double)contractInfo.get(i).get("PREDAY"));
				toDayUsage = contractInfo.get(i).get("TODAY") == null ? "uncertain" : mdf.format((Double)contractInfo.get(i).get("TODAY"));
				
//				text = "Customer Name : " + contractInfo.get(i).get("CUSTOMERNAME")
//                        + "\n METER ID : " + mdsId
//                        + "\n Yesterday's Consumption : " +  preDayUsage
// 						+ "\n Today's Consumption : " + toDayUsage;
				
				text = getMessageProp(supplier).getProperty("aimir.sms.customer.name") + " : " + contractInfo.get(i).get("CUSTOMERNAME")
						+ "\n " + getMessageProp(supplier).getProperty("aimir.meterid") + " : " + mdsId
						+ "\n " + getMessageProp(supplier).getProperty("aimir.sms.consumption.yesterday") + " : " +  preDayUsage
						+ "\n " + getMessageProp(supplier).getProperty("aimir.sms.consumption.today") + " : " +  toDayUsage;
				
				
				if(contractInfo.get(i).get("GROUP_MOBILENO") == null) {
					mobileNo = contractInfo.get(i).get("MOBILENO").toString().replace("-", "");
				} else {
					//그룹의 경우 그룹의 대표번호로 SMS를 보낸다.
					mobileNo = contractInfo.get(i).get("GROUP_MOBILENO").toString().replace("-", "");
				}
				
				if(targetVal >= currentCredit ) {
					log.debug("Target Value contractNumber[" + contract.getContractNumber() + "]");
					log.debug("text messae : " + text);

					Properties prop = new Properties();
	    			prop.load(getClass().getClassLoader().getResourceAsStream("config/schedule.properties"));
	    			
					String smsClassPath = prop.getProperty("smsClassPath");
					SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
					
					Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
					String messageId = (String) m.invoke(obj, mobileNo, text, prop);
					
					if(!"".equals(messageId)) {
						contractDao.updateSmsNumber(contract.getId(), messageId+":prepaySendSMS");
					}
				}
				 
			} catch (Exception e) {
				log.error(e,e);
			} finally {
				if(urlConnection != null && urlConnection.getURL() != null)
					urlConnection.disconnect();
				contractDao.flushAndClear();
				log.info(" ##### TargetVal [" + ++cnt + " / " + totalCnt + "] #####");
			}
		}
	}
	
	/**
	 * 
	 * @param contractInfo
	 * SMS를 보낸다.
	 */
	public void sendSMSPeriod(List<Map<String, Object>> contractInfo) {
		SimpleDateFormat sd = new SimpleDateFormat("yyyyMMddHHmmss");
		String today = sd.format(new Date());
		Supplier supplier = null;
		String preDayUsage = "";
		String toDayUsage = "";
		Contract contract = null;
		int cnt = 0;
		int totalCnt = contractInfo.size();
		
		for (int i = 0; i < totalCnt; i++) {
			try {
				contract = contractDao.get(Integer.parseInt(contractInfo.get(i).get("CONTRACTID").toString()));
				
				if (contract.getMeter() == null) continue;
				
				supplier = supplierDao.get(contract.getSupplierId());
				
				Meter meter = meterDao.get(contract.getMeterId());
				String mdsId =meter.getMdsId();
				
				DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
				
				preDayUsage = contractInfo.get(i).get("PREDAY") == null ? "uncertain" : mdf.format((Double)contractInfo.get(i).get("PREDAY"));
				toDayUsage = contractInfo.get(i).get("TODAY") == null ? "uncertain" : mdf.format((Double)contractInfo.get(i).get("TODAY"));

//				text = "Customer Name : " + contractInfo.get(i).get("CUSTOMERNAME")
//				        + "\n METER ID : " + mdsId
//                        + "\n Yesterday's Consumption : " +  preDayUsage
// 						+ "\n Today's Consumption : " + toDayUsage;

				text = getMessageProp(supplier).getProperty("aimir.sms.customer.name") + " : " + contractInfo.get(i).get("CUSTOMERNAME")
						+ "\n " + getMessageProp(supplier).getProperty("aimir.meterid") + " : " + mdsId
						+ "\n " + getMessageProp(supplier).getProperty("aimir.sms.consumption.yesterday") + " : " +  preDayUsage
						+ "\n " + getMessageProp(supplier).getProperty("aimir.sms.consumption.today") + " : " +  toDayUsage;

				if(contractInfo.get(i).get("GROUP_MOBILENO") == null) {
					mobileNo = contractInfo.get(i).get("MOBILENO").toString().replace("-", "");
				} else {
					//그룹의 경우 그룹의 대표번호로 SMS를 보낸다.
					mobileNo = contractInfo.get(i).get("GROUP_MOBILENO").toString().replace("-", "");
				}

				String messageId = notificationSMS(contract);
				contractDao.updateSmsNumber(contract.getId(), messageId);
				
			} catch (Exception e) {
				log.error(e,e);
			} finally {
				log.info(" ##### SMSPeriod [" + ++cnt + " / " + totalCnt + "] #####");
			}
			
		}
	}
	

	
	/**
	 * SMS 보내는 부분
	 * 
	 * @param contract
	 * @param today
	 * @return
	 * @throws Exception
	 */
	private String notificationSMS(Contract contract) throws Exception {
		Properties prop = new Properties();
		prop.load(getClass().getClassLoader().getResourceAsStream("config/schedule.properties"));
		
		String smsClassPath = prop.getProperty("smsClassPath");
		SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
		
		Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
		String messageId = (String) m.invoke(obj, mobileNo, text, prop);
		
		if(!"".equals(messageId)) {
			messageId=messageId + ":prepaySendSMS";
		}
		
		return messageId;
	}
}
