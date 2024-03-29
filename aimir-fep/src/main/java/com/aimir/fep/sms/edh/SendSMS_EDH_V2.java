package com.aimir.fep.sms.edh;

import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.system.SmsServiceLogDao;
import com.aimir.fep.sms.edh.atompark.ConnectAPI;
import com.aimir.model.system.SmsServiceLog;
import com.aimir.util.TimeUtil;

/**
 * @since 2021.03.18
 * @author Han seung woo
 * 
 */
public class SendSMS_EDH_V2 {
	private static Log log = LogFactory.getLog(SendSMS_EDH_V2.class);

    @Resource(name="transactionManager")
    private HibernateTransactionManager txManager;
	
    @Autowired
    private SmsServiceLogDao smsServiceLogDao; 
    
	private SMSInterface apiInterface;
	
	private String url;
	private String login;
	private String password;
	private String countryCode = null;
		
	public SendSMS_EDH_V2(String url, String login, String password, String countryCode) {
		this.url = url;
		this.login = login;
		this.password = password;
		this.countryCode = countryCode;
		
		init();
	}
	
	private void init() {
		if(apiInterface == null) {
			if(url != null) {
				if(url.contains("atompark")) {
					apiInterface = new ConnectAPI(url, login, password);		
				}
			}
		}
		
		log.debug("url : " +  url + ", login : " +  login + ", password : " +  password + ", countryCode : " +  countryCode +", instance : " +apiInterface);
	} 
	
	public void send(String mobileNumber, String message) {
		init();
		
		mobileNumber = getMobileNumber(mobileNumber);
		
		String sendId = UUID.randomUUID().toString().replaceAll("-", "").substring(0,8);
		if(apiInterface != null) {
			String result = apiInterface.send(sendId, mobileNumber, message);
			if(result != null) {
				log.debug("mobileNumber : " + mobileNumber +",sms result : " + result);
				saveSMSLog(sendId, mobileNumber, message, result);
			}
		}
		
		log.info("send smsLog complete");
	}

	private void saveSMSLog(String sendId, String mobileNumber, String message, String result) {
		TransactionStatus txStatus = null;
		
		try {
			txStatus = txManager.getTransaction(null);
			
			SmsServiceLog smsLog = new SmsServiceLog();
			smsLog.setMsg(message);
			smsLog.setSendNo(sendId);
			smsLog.setReceiveNo(mobileNumber);
			smsLog.setSendTime(TimeUtil.getCurrentTime());
			smsLog.setResult(result);
			
			smsServiceLogDao.add(smsLog);
			
			txManager.commit(txStatus);
		}catch(Exception e) {
			log.error(e,e);
			if (txStatus != null) 
				txManager.rollback(txStatus);
		}
		
		log.info("saved smsLog complete");
	}
	
	private String getMobileNumber(String m) {
		if(m == null || m.length() != 12)  {
			log.info("mobile number is Invalid!! mobile number : " + m);
			return null;
		}
		
		if(!m.startsWith(countryCode)) {
			log.info("mobile number not startWith | mobile nubmer : " + m +",  countryCode : " +countryCode);
			return null;
		}
		/*
		StringBuffer buffer = new StringBuffer();
		if(m.startsWith("010")) {
			buffer.append("+").append(countryCode).append(m.substring(1, m.length()));
		}else if(m.startsWith(countryCode)) {
			buffer.append("+").append(m);
		}
		
		return buffer.toString().replaceAll("-", "");
		*/
		return m.replaceAll("-", "");
	}
}
