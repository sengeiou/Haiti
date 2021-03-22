package com.aimir.fep.sms.edh;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.fep.sms.edh.atompark.ConnectAPI;
import com.aimir.fep.util.FMPProperty;

/**
 * @since 2021.03.18
 * @author Han seung woo
 * 
 */
@Service
public class SendSMS_EDH_V2 {
	private static Log log = LogFactory.getLog(SendSMS_EDH_V2.class);
	
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
