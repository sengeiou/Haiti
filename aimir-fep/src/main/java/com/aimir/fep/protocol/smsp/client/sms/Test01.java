package com.aimir.fep.protocol.smsp.client.sms;

import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.DataCodings;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.DeliveryReceipt;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.MessageType;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.Session;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.InvalidDeliveryReceiptException;
import org.jsmpp.util.TimeFormatter;

public class Test01 {
	private static TimeFormatter timeFormatter = new AbsoluteTimeFormatter();
	private static final String SMSC_SERVER = "smsc1.com4.no";
	private static final Integer SMSC_PORT = 9000;
	private static final String SMSC_USERNAME = "validerams";
	private static final String SMSC_PASSWORD = "U91nDBr";
	private static final String DEFAULT_SYSTEMTYPE = "cp";
	private static final String DEFAULT_SERVICETYPE = "CMT";
	private static final String DEFAULT_SOURCEADDRESS = "1616";

	public static void main(String[] args) {
		String MSISDN = "47580014006945";
    	String contents = "S0mRl9mo4bVEJhkDM8kQ3Rhr0muRI=03#";
    	byte[] sendMessage = contents.getBytes();
    	
    	String smscServer = "smsc1.com4.no";
		String smscPort = "9000"; 
		String smscUserName = "validerams"; 
		String smscPassword = "U91nDBr"; 
		String hesPhonenumber  = "47580014013024";
        SMPPSession session = new SMPPSession();
        
        // session.setMessageReceiverListener(new Test01_Listener());

        try {
        	session.connectAndBind(smscServer, Integer.parseInt(smscPort), new BindParameter(BindType.BIND_TRX, smscUserName, smscPassword, "cp",
                    TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
        } catch (IOException e) {
            System.err.println("Failed connect and bind to host");
            e.printStackTrace();
        }

		// send Message
		try {
			// set RegisteredDelivery
			final RegisteredDelivery registeredDelivery = new RegisteredDelivery();
			registeredDelivery.setSMSCDeliveryReceipt(SMSCDeliveryReceipt.SUCCESS_FAILURE);

			String messageId = session.submitShortMessage("CMT", TypeOfNumber.UNKNOWN,
                    NumberingPlanIndicator.ISDN, hesPhonenumber, TypeOfNumber.INTERNATIONAL,
                    NumberingPlanIndicator.ISDN, MSISDN, new ESMClass(), (byte) 0, (byte) 1,
                    timeFormatter.format(new Date()), null, new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE),
                    (byte) 0, DataCodings.ZERO, (byte) 0, sendMessage);

			System.out.println("Message submitted, message_id is " + messageId);
		} catch (PDUException e) {
			// Invalid PDU parameter
			System.out.println("Invalid PDU parameter");
			e.printStackTrace();
		} catch (ResponseTimeoutException e) {
			// Response timeout
			System.out.println("Response timeout");
			e.printStackTrace();
		} catch (InvalidResponseException e) {
			// Invalid response
			System.out.println("Receive invalid respose");
			e.printStackTrace();
		} catch (NegativeResponseException e) {
			// Receiving negative response (non-zero command_status)
			System.out.println("Receive negative response");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO error occur");
			e.printStackTrace();
		}

		try {
			Thread.sleep(20000); // wait 50 second
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// unbind(disconnect)
		session.unbindAndClose();
		System.out.println("finish!");

	}
}
