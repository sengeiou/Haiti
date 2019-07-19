package com.aimir.fep.protocol.smsp.client.sms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;

import com.aimir.fep.protocol.smsp.command.frame.sms.RequestFrame;

/**
 * SMS_Client
 * 
 * @version 1.0 2016.07.23
 * @author Sung Han LIM
 */

public class SMS_Client {
	private static Log logger = LogFactory.getLog(SMS_Client.class);
	private static TimeFormatter timeFormatter = new AbsoluteTimeFormatter();

	public String execute(HashMap<String, Object> condition, List<String> paramList, String cmdMap) throws Exception {

		String commandName = condition.containsKey("commandName") ? condition.get("commandName").toString() : "";
		String messageType = condition.containsKey("messageType") ? condition.get("messageType").toString() : "";
		String euiId = condition.containsKey("euiId") ? condition.get("euiId").toString() : "";
		String mobliePhNum = (condition.containsKey("mobliePhNum") && condition.get("mobliePhNum") != null) ? condition.get("mobliePhNum").toString() : "";
		String commandCode = condition.containsKey("commandCode") ? condition.get("commandCode").toString() : "";
		String hashCode = condition.containsKey("hashCode") ? condition.get("hashCode").toString() : "";

		String[] param = null;
		String sequence = null;

		if (paramList != null) {
			param = new String[paramList.size()];
			param = paramList.toArray(param);
		}

		// Sequence 생성 로직 (S)
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
		sequence = dateFormat.format(calendar.getTime());
		// Sequence 생성 로직 (E)

		RequestFrame reqFrame = new RequestFrame(messageType, sequence, hashCode, commandCode, param);
		SMS_Requester sms_Requester = SMS_Requester.newInstance();

		return sms_Requester.sendSMS(sequence, commandName, commandCode, euiId, mobliePhNum, reqFrame.encode(), cmdMap);
	}
}
