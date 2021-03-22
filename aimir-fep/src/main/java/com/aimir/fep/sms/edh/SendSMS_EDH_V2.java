package com.aimir.fep.sms.edh;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;
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
@Service
public class SendSMS_EDH_V2 {
	private static Log log = LogFactory.getLog(SendSMS_EDH_V2.class);

    @Resource(name="transactionManager")
    private HibernateTransactionManager txManager;
	
    @Autowired
    private SmsServiceLogDao smsServiceLogDao; 
    
	private SMSInterface instance;
	
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
		if(instance == null) {
			instance = ConnectAPI.getInstance(url, login, password);
		}
		
		log.debug("url : " +  url + ", login : " +  login + ", password : " +  password + ", countryCode : " +  countryCode +", instance : " +instance);
	}
	
	public void send(String mobileNumber, String message) {
		init();
		
		if(instance != null) {
			String result = instance.send(mobileNumber, message);
			if(result != null) {
				log.debug("mobileNumber : " + mobileNumber +",sms result : " + result);
				saveSMSLog(mobileNumber, message, result);
			}
		}
		
		log.info("send smsLog complete");
	}

	private void saveSMSLog(String mobileNumber, String message, String result) {
		TransactionStatus txStatus = null;
		
		try {
			txStatus = txManager.getTransaction(null);
			
			SmsServiceLog smsLog = new SmsServiceLog();
			smsLog.setMsg(message);
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
		if(m == null) 
			return null;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(countryCode)
			.append(m.replaceAll("-", ""));
		
		return buffer.toString();
	}
}
