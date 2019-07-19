package com.aimir.fep.util.sms;

import java.net.URLEncoder;
import java.util.Properties;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.mrp.exception.MRPException;

public class SendSMSSpasa implements SendSMS {
private static Log log = LogFactory.getLog(SendSMSSpasa.class);
	
	public String send(String mobileNo, String msg, Properties prop) throws MRPException { 
		log.debug("<<<<<<<<<<<< Send SMS in Spasa >>>>>>>>>>>"); 
		
		HttpClient client = new HttpClient();
		
		String baseURL = prop.getProperty("prepay.sms.baseUrl");
		String username = prop.getProperty("prepay.sms.id");
		String pass = prop.getProperty("prepay.sms.pass");
		String proxyServer = prop.getProperty("prepay.sms.proxy.server");
		int proxyPort = Integer.parseInt(prop.getProperty("prepay.sms.proxy.port"));
		
		GetMethod method = null;
		HostConfiguration proxy = null;
		String messageId = "fail";
		try {
	//		sampleURL : https://www.xml2sms.gsm.co.za/send?username=x&password=y&number=27825551234&message=This+is+a+test";
			String sendURL = baseURL+"username="+username+"&password="+pass+
			        "&number="+URLEncoder.encode(mobileNo, "utf-8")+"&message="+
			        URLEncoder.encode(msg, "utf-8");
			log.info("sendURL["+sendURL+"]");
			//proxy 설정
			proxy = new HostConfiguration();
            proxy.setProxy(proxyServer, proxyPort);
            
            //Timeout 설정
            HttpConnectionManagerParams managerParam = new HttpConnectionManagerParams();
			HttpConnectionManager manager = new SimpleHttpConnectionManager();
			managerParam.setConnectionTimeout(10*1000);
			client.setHttpConnectionManager(manager);
			
			method = new GetMethod(sendURL);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
			int status  = client.executeMethod(proxy, method);
			
			
			//URL에서 보내온 메세지 읽는 부분 : 
//			return message sample : "<message type="sent" key="221263072" tonumber="27828902283" message="Testing XML 1" timesent="02-Dec-2013 13:25:39" timedelivered="02-Dec-2013 13:25:41" delivered="1" status="0" statusdescription="Delivered" />"
			String body = method.getResponseBodyAsString();

			if(body != null && !body.isEmpty()) {
				//messaeId만 저장하기 위함
				if(body.contains("key")) {
					messageId = body.substring(body.indexOf("key"), body.indexOf("\"", body.indexOf("key")+5));
				}
				messageId = messageId.replace("key=\"", "");
			}

			log.info("Status[" + status + "] Msg[" + msg + "]");
			if(status == 200) {
				log.debug("messageId : "+messageId);
				//return messageId;
			}else {
				messageId="fail";
			}
		}
		catch (Exception e) {
			log.warn(e,e);
		} finally {
			if(method != null)method.releaseConnection();
		}
		
		return messageId;
    }
}
