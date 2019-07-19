/** 
 * @(#)ResponseFrame.java       1.0 2008-11-24 *
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelecom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */

package com.aimir.fep.protocol.mrp.command.frame.sms;

import java.util.StringTokenizer;
import java.util.ArrayList;

import com.aimir.fep.protocol.mrp.exception.MRPError;
import com.aimir.fep.protocol.mrp.exception.MRPException;


/** 
 * ResponseFrame 
 * 
 * @version     1.0 1 Nov 2008 
 * @author		YK.Park 
 */

public class ResponseFrame {

	public static final String STX = "NT";
	public static final String RESPONSE = "R";
	public static final String DELIM = ",";
	public static final String PARAMDELIM = ",";
	public static final String SUCC = "S";
	public static final String FAIL = "F";
	private String SEQ = null;
	String header = null;
	String result = null;
	String cmd = null;
	ArrayList<String> resultParam = new ArrayList<String>();
	
	private String frame = null;
	
    /**
     *  Constructor. <p>
     */ 
    public ResponseFrame() 
    {

    }

    /**
     *  Constructor. <p>
     */ 
    public ResponseFrame(String frame) 
    {
    	//Encryption encrypt = new Encryption(frame);    	
    	//this.frame = encrypt.decode();
    	this.frame = frame;
    }
    
    /**
     *  Constructor. <p>
     */ 
    public ResponseFrame(StringBuffer frameBuffer) 
    {
    	
    	//Encryption encrypt = new Encryption(frameBuffer);    	
    	//this.frame = encrypt.decode();
    	this.frame = frameBuffer.toString();
    }
    
    /**
     *  Constructor. <p>
     */ 
    public ResponseFrame(byte[] frameBuffer) 
    {
    	
    	//Encryption encrypt = new Encryption(frameBuffer);    	
    	//this.frame = encrypt.decode();
    	this.frame = new String(frameBuffer);
    }
    
    public int getSEQ()
    {

    	if(header.startsWith(STX)){
        	SEQ = header.substring(2,5);
        	try{
            	return Integer.parseInt(SEQ);
        	}catch(NumberFormatException e){
        		
        	}

    	}else{
    		return -1;
    	}
    	return -1;

    }
    
    public boolean getResult()
    {
    	if(this.result.equals(SUCC)){
    		return true;
    	}
    	return false;
    }
    
    public int getReason()
    {
    	if(this.result.equals(FAIL)){
    		if(resultParam != null && resultParam.size() > 0){
    			String ret = (String)resultParam.get(0);
    			return Integer.parseInt(ret);
    		}else{
    			return -1;
    		}
    	}
		return -1;
    }
    
    public ArrayList<String> getParam()
    {
    	return resultParam;
    }
    
    public void decode() throws MRPException {
    	
    	StringTokenizer st = new StringTokenizer(frame,DELIM);
        
    	try{
            if(st.hasMoreTokens()){
            	this.header = (String)st.nextToken();
            }
            if(st.hasMoreTokens()){
            	this.result = (String)st.nextToken();
            }
            if(st.hasMoreTokens()){
            	this.cmd = (String)st.nextToken();
            }
            //String param = null;
            //if(st.hasMoreTokens()){
            //	param = (String)st.nextToken();
            //}
            //st = new StringTokenizer(param,PARAMDELIM);
            while(st.hasMoreTokens()){
            	resultParam.add((String)st.nextToken());
            }
    	}catch(Exception e){
    		throw new MRPException(MRPError.ERR_INVALID_PARAM);
    	}

    }
    
    public String toString(){
    	StringBuffer sb = new StringBuffer();
    	sb.append("header="+this.header);
    	sb.append("seq="+getSEQ());
    	sb.append("result="+this.result);
    	sb.append("result?="+getResult());
    	sb.append("cmd="+this.cmd);
    	sb.append("reason="+getReason());
    	
    	if(resultParam != null && resultParam.size() > 0){
    		for(int i = 0; i < resultParam.size(); i++){
    			sb.append("param="+(String)resultParam.get(i));
    		}
    	}
    	return sb.toString();
    }
    
    public byte[] encode() throws MRPException {
    	
    	StringBuffer buffer = new StringBuffer();
    	buffer.append(STX);
    	buffer.append(SEQ);
    	buffer.append(RESPONSE);
    	buffer.append(DELIM);
    	buffer.append(result);
    	buffer.append(DELIM);
    	buffer.append(cmd);

    	if(resultParam != null && resultParam.size() > 0){
        	buffer.append(DELIM);
    		for(int i = 0; i < resultParam.size(); i++){
    			if(i != 0){
    				buffer.append(PARAMDELIM);
    			}
    			buffer.append((String)resultParam.get(i));
    		}
    	}
    	
    	//Encryption encrypt = new Encryption(buffer);
    	
    	return buffer.toString().getBytes();
    }
    
    public void setSEQ(int seq)
    {
    	seq = (int)(seq & 0xFF);
    	
    	if(seq > 10 && seq < 100){
    		this.SEQ = "0"+seq;
    	}else if(seq < 10){
    		this.SEQ = "00"+seq;
    	}else{
    		this.SEQ = ""+seq;
    	}
    }
    
    public void setResult(String result)
    {
    	this.result = result;
    }
    
    public void setResult(ArrayList<String> resultParam)
    {
    	this.resultParam = resultParam;
    }
}
