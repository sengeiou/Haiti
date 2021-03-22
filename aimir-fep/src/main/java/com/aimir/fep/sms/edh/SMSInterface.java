package com.aimir.fep.sms.edh;

public interface SMSInterface {

	public String send(String sendId, String mobileNumber, String message);
}
