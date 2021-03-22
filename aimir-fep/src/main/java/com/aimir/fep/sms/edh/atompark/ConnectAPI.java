package com.aimir.fep.sms.edh.atompark;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.sms.edh.SMSInterface;

public class ConnectAPI implements SMSInterface {
	private static Log log = LogFactory.getLog(ConnectAPI.class);
	private static ConnectAPI instance;
	
	private String url;
	private String login;
	private String password;
	private RequestBuilder requestBuilder;
	
	public ConnectAPI(String url, String login, String password) {
		this.url = url;
		this.login = login;
		this.password = password;
		
		requestBuilder = new RequestBuilder(url);
	}
	
	public static ConnectAPI getInstance(String url, String login, String password) {
		if(instance == null) {
			synchronized (ConnectAPI.class) {
				if(instance == null) {
					instance = new ConnectAPI(url, login, password);
				}
			}
		}
		
		return instance;
	}
	
	@Override
	public String send(String mobileNumber, String message) {
		String messageID = "id1";
		String variables = "";
		String sender = "Haiti";
		
        String request="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"; 
        request=request.concat("<SMS>");     
        request=request.concat("<operations>");
        	request=request.concat("<operation>SEND</operation>");
        request=request.concat("</operations>");
        request=request.concat("<authentification>");
	        request=request.concat("<username>"+this.login+"</username>"); 
	        request=request.concat("<password>"+this.password+"</password>");
        request=request.concat("</authentification>");
        request=request.concat("<message>");
        	request=request.concat("<sender>"+sender+"</sender>"); //"15771212"아닌 15771212전송할 경우 도착하지 않았다.
        	request=request.concat("<text>"+message+"</text>");
        request=request.concat("</message>");
        request=request.concat("<numbers>");
	        request=request.concat("<number");
		        request=request.concat(" messageID=\""+messageID+"\""); 	//phone.getIdMessage()
		        //request=request.concat(" variables=\""+variables+"\""); 	//phone.getVariable()
		        request=request.concat(">");
		        request=request.concat(mobileNumber);						//phone.getPhone()
	        request=request.concat("</number>");
        request=request.concat("</numbers>");
        request=request.concat("</SMS>");
        
        log.debug("request : " + request);        
        
        return requestBuilder.doXMLQuery(request);
	}
	
}
