package com.aimir.fep.protocol.smsp.command.frame.sms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * RequestFrame 
 * 
 * @version     1.0  2016.07.23 
 * @author		Sung Han LIM 
 */

public class RequestFrame {
	
	private static Log log = LogFactory.getLog(RequestFrame.class);
	public static final String START_FLAG = "S";
	public static final String END_FLAG = "#";
	public static final String DELIMITER = ",";
	
	private String messageType = "0";
	private String sequence = null;
	private String hashCode = null;
	private String command = null;
	private String[] param = null;
	private StringBuffer frame = null;
	
	public RequestFrame() {
		
	}

    public RequestFrame(String messageType, String sequence, String hashCode, String command, String[] param) {
    	this.messageType = messageType;
    	this.sequence = sequence;
    	this.hashCode = hashCode;
    	this.command = command;
    	this.param = param;
    }
    
    public RequestFrame(String messageType, String sequence, String hashCode, String command, String param) {
    	this.messageType = messageType;
    	this.sequence = sequence;
    	this.hashCode = hashCode;
    	this.command = command;
    	this.param = new String[]{param};
    }
    
    public byte[] encode() throws Exception {
		if (messageType == null || messageType.equals("")) {
			messageType = "0";
		}
    	
    	frame = new StringBuffer();
    	frame.append(START_FLAG);
    	frame.append(messageType);
    	frame.append(sequence);
    	frame.append(hashCode);
    	
		if (command != null && !command.equals("")) {
			frame.append(command);
		}
		
		if (param != null && param.length > 0) {
			frame.append(DELIMITER);
			for (int i = 0; i < param.length; i++) {
				if (i != 0) {
					frame.append(DELIMITER);
				}
				frame.append(param[i]);
			}
		}
    	
		frame.append(END_FLAG);
    	log.debug("beforeEncode=["+frame.toString()+"]");
    	
    	return frame.toString().getBytes();
    }
    
	public String getMessageType() {
		return this.messageType;
	}
	
	public String getSequence() {
		return this.sequence;
	}

	public String getHashCode() {
		return this.hashCode;
	}

	public String getCommand() {
		return this.command;
	}

	public String[] getParam() {
		return this.param;
	}

}
