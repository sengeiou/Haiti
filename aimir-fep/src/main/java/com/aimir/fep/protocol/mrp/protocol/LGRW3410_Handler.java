/** 
 * @(#)LGRW3410_Handler.java        *
 * Copyright (c) 2008-2009 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
package com.aimir.fep.protocol.mrp.protocol;

import java.net.Socket;
import java.text.ParseException;
import java.util.HashMap;

import com.aimir.constants.CommonConstants.MeterModel;
import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.OPAQUE;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.entry.meterDataEntry;
import com.aimir.fep.protocol.mrp.MeterProtocolHandler;
import com.aimir.fep.protocol.mrp.client.MRPClientProtocolHandler;
import com.aimir.fep.protocol.mrp.exception.MRPError;
import com.aimir.fep.protocol.mrp.exception.MRPException;
import com.aimir.fep.util.ByteArray;
import com.aimir.fep.util.DataUtil;

import com.aimir.util.TimeUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * @author Kang, Soyi
 */
	
public class LGRW3410_Handler extends MeterProtocolHandler { 

    private static Log log = LogFactory.getLog(LGRW3410_Handler.class);
    private String meterId;
    private String modemId;
    
    private String groupId = "";
    private String memberId = "";
    private String mcuSwVersion = "";

    private static int DATA_CTRL_IDX =0;
	/**
	 * Constructor.<p>
	 */
    protected long sendBytes;
    
	public LGRW3410_Handler() {	
	}

	public void setModemNumber(String modemId){
		this.modemId = modemId;
	}
    public void setGroupId(String groupId){
    	this.groupId = groupId;
    }
    public void setMemberId(String memberId){
    	this.memberId = memberId;
    }
    public void setMcuSwVersion(String mcuSwVersion){
    	this.mcuSwVersion = mcuSwVersion;
    }
    public CommandData execute(MRPClientProtocolHandler handler,
                          IoSession session, 
                          CommandData command) throws MRPException
    {
        this.handler = handler;
        
        CommandData commandData = null;
        byte[] tpb = null;
        byte[] lpd = null;
        ByteArray ba = new ByteArray();
        int nOffset = 0;
        int nCount = 96;
        String from = "";
        String cmd = command.getCmd().getValue();
        log.debug("==============LGRW3410_Handler start cmd:"+cmd+"================");
        if(cmd.equals("104.6.0")||cmd.equals("104.13.0"))
        {
            SMIValue[] smiValue = command.getSMIValue();
            
            if(smiValue != null && smiValue.length >= 2){
                int kind = ((INT)smiValue[1].getVariable()).getValue();
                
                if(smiValue.length == 4){
                    nOffset = ((INT)smiValue[2].getVariable()).getValue();
                    nCount = ((INT)smiValue[3].getVariable()).getValue();

                    try
                    {
                        from = TimeUtil.getPreDay(TimeUtil.getCurrentTime(),nOffset);
                    }
                    catch (ParseException e)
                    {

                    }
                }else{
                    try
                    {
                        from = TimeUtil.getPreDay(TimeUtil.getCurrentTime(),2);
                    }
                    catch (ParseException e)
                    {

                    }
                }

                byte[] mcuId = new byte[17];
                System.arraycopy(command.getMcuId().getBytes(), 0, mcuId, 0, command.getMcuId().length());

                ba.append(new byte[]{'N','C','5','A','1'});
                ba.append(new byte[]{0x00});
                ba.append(mcuId);
                ba.append(new byte[]{(byte)0x88});//LGRW3410
                ba.append(new byte[]{(byte)0x00});
                ba.append(new byte[]{(byte)0x00});
                ba.append(new byte[]{(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00});//ZIGBEE ID
                ba.append(new byte[]{0x00,0x00,0x00,0x00});
                
                
            	switch(kind){
	                case 1:
	                	initProcess(session);
                        try {
                            lpd = lpRead(session,nOffset,TimeUtil.getCurrentTime(),15).toByteArray();
                        } catch (ParseException e) {
                            log.warn(e);
                        }
		                if(lpd!=null){
			                ba.append(DataConstants.LP_IEIU);
			                ba.append(lpd);
		                }
		                logOff(session);
		                terminate(session);
		            break;
	                case 2:
	                	initProcess(session);
	                	tpb = billRead(session).toByteArray();
	                	if(tpb!=null){
	    	                ba.append(DataConstants.PB_IEIU);
	    	                ba.append(tpb);
	                    }
	                	logOff(session);
	                    terminate(session);
	                	break;
	                default:
	                	//////
	                  	initProcess(session);
                        try {
                            lpd = lpRead(session,kind,TimeUtil.getCurrentTime(),15).toByteArray();
                        } catch (ParseException e) {
                            log.warn(e);
                        }
		                if(lpd!=null){
			                ba.append(DataConstants.LP_IEIU);
			                ba.append(lpd);
		                }
		                logOff(session);
		                terminate(session);
	                	break;
	                	//////
	                  //  throw new MRPException(MRPError.ERR_INVALID_PARAM,"Invalid parameters");
             	}
                
            }else{
                throw new MRPException(MRPError.ERR_INVALID_PARAM,"Invalid parameters");
            }
            
            if(ba != null && ba.toByteArray().length > 0)
            {      
                String currTime = null;
                try{currTime = TimeUtil.getCurrentTime();}
                catch(Exception e){                 
                }
                log.debug("meterEntryData=>"+smiValue[0].getVariable().toString()+","+new OCTET(ba.toByteArray()).toHexString());
                meterDataEntry md = new meterDataEntry();
                md.setMdID(new OCTET(smiValue[0].getVariable().toString()));
                md.setMdSerial(new OCTET(this.meterId));
                md.setMdServiceType(new BYTE(1));//1: electric
                md.setMdTime(new TIMESTAMP(currTime));
                md.setMdType(new BYTE(13));//13:ieiu
                md.setMdVendor(new BYTE(6));//LSIS
                md.setMdData(new OCTET(ba.toByteArray()));
                commandData = new CommandData();
                commandData.setCnt(new WORD(1));
                commandData.setTotalLength(ba.toByteArray().length);
                commandData.append(new SMIValue(new OID("10.1.0"),new OPAQUE(md)));
            }
        }

        log.debug("==============LGRW3410_Handler end ================");
        return commandData;
    }

    public ByteArray configureRead(IoSession session) throws MRPException
    {
        return null;
    }
    
	/**
	 * Read Cumulative Data.<p>
	 * @return
	 * @throws MeterException
	 */
	public ByteArray currbillRead(IoSession session) throws MRPException
	{
    	return null;
    }
	
	/**
	 * Read Event Log.<p>
	 * @return
	 * @throws MeterException
	 */
	public ByteArray eventlogRead(IoSession session) throws MRPException
    {
    	return null;
    }
	
    protected LGRW3410_ReceiveDataFrame read(IoSession session,LGRW3410_RequestDataFrame frame) throws MRPException
    {
        int sequence = 0;
        try
        {
            IoBuffer buf = frame.getIoBuffer();
         //   session.write(buf); 
            LGRW3410_ReceiveDataFrame rcvFrame = new LGRW3410_ReceiveDataFrame();
            
            int retry =3;
            
            byte[] message = null ; 
            
            int cnt=0;
            while(cnt<retry &&(message==null || message.length<9)){
            	log.debug("cnt :"+cnt);
            	try{
            		if (cnt==0)
            			buf = frame.getIoBuffer();
            		else{
            			buf = frame.getIoBuffer(false);
            		}
	            	session.write(buf); 
		    //        byte[] message = (byte[])handler.getMsg(session,20,160,false);
		            message = (byte[])handler.getMessage(session,5,MeterModel.LSIS_LGRW3410.getCode().intValue());
		            cnt++;
            	}catch(Exception e){
            		cnt++;
            	}
           }
           if(message != null && message.length > 0){
            	if(message[0] == LGRW3410_DataConstants.DATA_CTRL_R_NACK){
                	 throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
                }else{
	               	if(message[0] != LGRW3410_DataConstants.DATA_CTRL_R_ACK || message[1] != LGRW3410_DataConstants.SOH)// || message[6] != LGRW3410_DataConstants.OK)
	               	{
		               	session.write(nakByte());
		               	rcvFrame = null;
		               	log.debug("SOH Error or not OK" );
	               	}else{
	               		rcvFrame.append(message);
		               log.debug("receive read =>"+new OCTET(message).toHexString());
		
	               	
		             /*  	int nLen = message.length;
		               	char crc	=	LGRW3410_DataConstants.KH_CRC16(message, 1, nLen-3);
		
		               	if ((message[nLen-2] != (byte)(crc>>8)) || (message[nLen-1] != (byte)(crc)))
		               	{
			               	//	result = nakByte();
			               	session.write(nakByte());
			               	rcvFrame = null;
			               	log.debug("CRC Error :"+ (byte)(crc>>8)+", "+(byte)crc );
		               	}
		               	*/
	               	}   
                }
            }else{
         	   throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
            }

            if(rcvFrame==null){
                throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
            }
            return rcvFrame;
        }
        catch (Exception e)
        {
            log.error("Read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
        } 
    }

    protected boolean checkRemain(byte b)
    {
        return ( b >> 7 == 0) ? true : false;
    }
    
    protected boolean isFirstFrame(byte b)
    {
        return ((b & 0x7F) >> 6 == 0) ? false : true;
    }
    
    protected boolean checkSequence(byte prev, byte current)
    {
        int prev_sequence  = (int)(prev & 0x3F);
        int current_sequence = (int)(current & 0x3F);
        return prev_sequence + 1 == current_sequence ? true : false;
    }

    public boolean checkCRC(byte[] src) throws MRPException
    {
        return false;
    }

    public void initProcess(IoSession session) throws MRPException
    {
    	try{
	        identify(session);
	    //    negociate(session);
	        logOn(session);
	        security(session);
    	}catch(Exception e)
        {
            log.error("initProcess error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"initProcess meter error");
        } 
    }

    protected boolean identify(IoSession session) throws MRPException
    {
        OCTET length = new OCTET(new byte[]{(byte)0x00, (byte) 0x01});
        OCTET send_Data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_IDENTIFY});
        BYTE control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
        DATA_CTRL_IDX = DATA_CTRL_IDX++;
        
        LGRW3410_RequestDataFrame frame 
            = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
            		send_Data, false, 0,0, null);
        LGRW3410_ReceiveDataFrame buf = null;
        
        boolean result = false;
        try
        {
        	buf = read(session,frame);    
        } catch (Exception e)
        {
            log.error("identify error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"identify meter error");
        } 
        return result;
    }
    
    protected boolean negociate(IoSession session) throws MRPException
    {
        OCTET length = new OCTET(new byte[]{(byte)0x00, (byte) 0x01});
        OCTET send_Data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_NEGOCIATE});
        BYTE control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
        DATA_CTRL_IDX = DATA_CTRL_IDX++;   
 
        LGRW3410_RequestDataFrame frame 
            = new LGRW3410_RequestDataFrame(new BYTE(), control,length,
            		send_Data, true, 0,0, null);
        boolean result = false;
        try
        {
            IoBuffer buf = frame.getIoBuffer();
            log.debug("send negociate=>"+buf.getHexDump());
            session.write(buf); 

            byte[] message = (byte[])handler.getMessage(session,5,MeterModel.LSIS_LGRW3410.getIntValue());
            if(message != null && message.length > 0){
                log.debug("receive negociate =>"+new OCTET(message).toHexString());
                if(message[0] != LGRW3410_DataConstants.DATA_CTRL_R_ACK
                		|| message[1] != LGRW3410_DataConstants.SOH){
                	log.debug("receive nack or soh error");
                	session.write(nakByte());
                }else{
                	result = true;
                }
            }else{
            	throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Reset meter error");
           }
        }
        catch (Exception e)
        {
            log.error("negociate error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"negociate meter error");
        } 
        return result;
    }
    
    protected boolean logOn(IoSession session) throws MRPException
    {
        byte[] logonData = {LGRW3410_DataConstants.DATA_COMMAND_LOGON,
        		(byte)0xf7, (byte)0xf7, 'A', 'T', 'I', ' ', ' ', 'S', 'Y', 'S', 'T', 'M'};
        OCTET length = new OCTET(new byte[]{(byte)0x00, (byte) 0x0D});
    //    BYTE control = new BYTE(DATA_CTRL[DATA_CTRL_IDX%2]);
        DATA_CTRL_IDX = DATA_CTRL_IDX++;
        BYTE control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
        DATA_CTRL_IDX = DATA_CTRL_IDX++;
        OCTET send_data = new OCTET(logonData);

        LGRW3410_RequestDataFrame frame 
            = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
                                   send_data, true, 0,0, null); 
        LGRW3410_ReceiveDataFrame buf = null;
        boolean result = false;
        try
        {
        	buf = read(session,frame);    
        }
        catch (Exception e)
        {
            log.error(e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Meter logon error");
        }
        return result;
    }
    
    protected boolean security(IoSession session) throws MRPException
    {
    /*	byte[] securityData = {LGRW3410_DataConstants.DATA_COMMAND_SECURITY,
        		 'M', '2', 'a', '1', 'r', '0', 'o', '9', 'm', '4', 'i', '3',
        		  ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
*/
 /*   	byte[] securityData = {LGRW3410_DataConstants.DATA_COMMAND_SECURITY,
			 '2', '0', '7', '6', '1', ' ', ' ', ' ', ' ', ' ', 
			 ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
*/			 
    	byte[] securityData = {LGRW3410_DataConstants.DATA_COMMAND_SECURITY,
   			 'A', 'T', 'I', ' ', 'S', 'Y', 'S', 'T', 'E', 'M',
   			 ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
 
 /*   	byte[] securityData = {LGRW3410_DataConstants.DATA_COMMAND_SECURITY,
      			 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B,
      			0x0C, 0x0D, 0x0E, 0x0F, 0x10, 0x11, 0x12, 0x13, 0x14, 0x15};
  */  	
    	OCTET length = new OCTET(new byte[]{(byte)0x00, (byte) 0x15});
    	BYTE control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
        DATA_CTRL_IDX = DATA_CTRL_IDX++;
        OCTET send_data = new OCTET(securityData);

        LGRW3410_RequestDataFrame frame 
            = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
                                   send_data, true, 0,0, null); 
        LGRW3410_ReceiveDataFrame  buf = null;
        boolean result = false;
        try
        {
        	buf = read(session,frame);  
        }
        catch (Exception e)
        {
            log.error(e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS, "Meter security error");
        }
        return result;
    }
    
    protected boolean securityLP(IoSession session) throws MRPException
    {
    	byte[] securityData = {LGRW3410_DataConstants.DATA_COMMAND_SECURITY,
   			 'N', 'U', 'R', 'I', 'S', 'Y', 'S', 'T', 'E', 'M',
   			 ' ', 'L', 'P', 'R', 'E', 'A', 'D', 'I', 'N', 'G'};
 
    	OCTET length = new OCTET(new byte[]{(byte)0x00, (byte) 0x15});
    	BYTE control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
        DATA_CTRL_IDX = DATA_CTRL_IDX++;
        OCTET send_data = new OCTET(securityData);

        LGRW3410_RequestDataFrame frame 
            = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
                                   send_data, true, 0,0, null); 
        LGRW3410_ReceiveDataFrame  buf = null;
        boolean result = false;
        try
        {
        	buf = read(session,frame);  
        	if(buf !=null) result = true;
        }
        catch (Exception e)
        {
            log.error(e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS, "Meter security error");
        }
        return result;
    }
    
    protected void logOff(IoSession session) throws MRPException
    {
        
        OCTET length = new OCTET(new byte[]{(byte)0x00, (byte) 0x01});
        OCTET send_Data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_LOGOFF});
        BYTE control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
        DATA_CTRL_IDX = DATA_CTRL_IDX++;    
 
        LGRW3410_RequestDataFrame frame 
            = new LGRW3410_RequestDataFrame(new BYTE(), control,length,
            		send_Data, true, 0,0, null);
        LGRW3410_ReceiveDataFrame buf =null;
        try
        {
        	buf = read(session,frame);  
    /*        IoBuffer buf = frame.getIoBuffer();
            log.debug("send logOff=>"+buf.getHexDump());
            session.write(buf); 

            byte[] message = (byte[])handler.getMessage(session,5,AimirModel.MT_MODEL_LSIS_LGRW3410);
            if(message != null && message.length > 0){
                log.debug("receive logOff =>"+new OCTET(message).toHexString());
            }else{
            }
    */   }
        catch (Exception e)
        {
            log.error("logOff error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"logOff meter error");
        } 
    }
    
    protected void terminate(IoSession session) throws MRPException
    {
        
        OCTET length = new OCTET(new byte[]{(byte)0x00, (byte) 0x01});
        OCTET send_Data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_TERMINATE});
        BYTE control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
        DATA_CTRL_IDX = DATA_CTRL_IDX++;    
 
        LGRW3410_RequestDataFrame frame 
            = new LGRW3410_RequestDataFrame(new BYTE(), control,length,
            		send_Data, true, 0,0, null);

        LGRW3410_ReceiveDataFrame buf =null;
        try
        {
        	buf = read(session,frame);  
     /*       IoBuffer buf = frame.getIoBuffer();
            log.debug("send terminate=>"+buf.getHexDump());
            session.write(buf); 

            byte[] message = (byte[])handler.getMessage(session,5,AimirModel.MT_MODEL_LSIS_LGRW3410);
            if(message != null && message.length > 0){
                log.debug("receive terminate =>"+new OCTET(message).toHexString());
            }else{
            }
  */      }
        catch (Exception e)
        {
            log.error("terminate error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"terminate meter error");
        } 
    }
    
    public IoBuffer ackByte() throws MRPException
    {
	    byte[] b = new byte[1];
	    b[0] = LGRW3410_DataConstants.DATA_CTRL_R_ACK;
	    IoBuffer buf = IoBuffer.allocate(b.length);
	    buf.put(b);
	    buf.flip();
	    return buf;
    }
    
    public IoBuffer nakByte() throws MRPException
    {
	    byte[] b = new byte[1];
	    b[0] = LGRW3410_DataConstants.DATA_CTRL_R_NACK;
	    IoBuffer buf = IoBuffer.allocate(b.length);
	    buf.put(b);
	    buf.flip();
	    return buf;
    }
    
    public ByteArray instRead(IoSession session) throws MRPException
    {
    	return null;
    }
    public ByteArray lpRead(IoSession session, String startday, String endday, int lpCycle) throws MRPException
    {
    	return null;
    }
    public ByteArray lpRead(IoSession session, int nOffset, String endday, int lpCycle) throws MRPException
    {
        log.debug("========== LoadProfile Read Start ===============");
        ByteArray ba = new ByteArray();  
        byte[] temp = null;
        LGRW3410_RequestDataFrame frame = null;
        LGRW3410_ReceiveDataFrame buf = null;
        
        try
        { 
        	//GET CONFIG
	        short table =LGRW3410_DataConstants.TABLE_CONFIG;
	        OCTET length = new OCTET(new byte[]{(byte)0x00, (byte) 0x03});
	        OCTET send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        BYTE control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
	        DATA_CTRL_IDX = DATA_CTRL_IDX++;    
	        
	        frame  = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
	            		send_data, true, 0,0, null);

            buf = read(session,frame);        
            temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 27, 8, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, 19, 4, temp, 0, 0));
           
            //GET LP
      
         	if( temp[27] == (byte)0x4C && temp[28] == (byte)0x47 ){
         		
         		if(!securityLP(session)) return ba;
         
            table =LGRW3410_DataConstants.TABLE_LP2;
	
	        length = new OCTET(new byte[]{(byte)0x00, (byte) 0x03});
	        send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
	        DATA_CTRL_IDX = DATA_CTRL_IDX++;    
	 
	        frame = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
	            		send_data, true, 0,0, null);		

	        buf = read(session,frame);
            temp = buf.encode();
            
            byte[] recentDateByte = new byte[8]; 
            System.arraycopy(temp, 8, recentDateByte, 0, 8);
            String  recentDate = parseYyyymmddhhmmss(recentDateByte);
            
            //////
    		int		nCnt=0, nSize=0, nLast_Read=0;
    		int	wLP_Cnt, wPage, wLastPage, LG_FIRST_PAGE=513;
    		byte	bytWrap, bytIdx, bytMod;
    		String	strTemp="";

    //		nSize	 = (int)((nOffset*(1440/lpCycle))*1.2);
    		nSize = 400;
    		log.debug("nSize : "+nSize);
    	//	nSize = 96;
    		bytWrap  = (byte) (temp[13] & 0x08);		//Wrap

    		wLP_Cnt  = (((int)temp[16]& 0xff) << 8) | (temp[17] & 0xff);		//LP_INDEX
    		if(nOffset>=10){
    		log.debug("wLP_Cnt 1: "+wLP_Cnt);
    		wLP_Cnt = wLP_Cnt - (nSize*nOffset)/10;
    		log.debug("bytWrap : "+bytWrap);
    		log.debug("wLP_Cnt 2: "+wLP_Cnt);
    		}
    		if( wLP_Cnt < 1 || wLP_Cnt > 9215 ) return ba;

    		wLastPage	= (((wLP_Cnt-1) / 9) * 2 + LG_FIRST_PAGE);
    		wPage		= wLastPage;
    		
    		bytIdx		= (byte) ((wLP_Cnt-1) % 9);

    		nCnt		= 	nSize / 9;
    		bytMod		=	(byte) (nSize  % 9);

    		if( (bytIdx+1) < bytMod )	nCnt++;		

    		nCnt *= 2;

    		log.debug("nCnt : "+nCnt);
    		
    		for(int i=0; i<nCnt; i++){

    			if( bytWrap>0 ){
    				//wPage = LG_DEC_PAGE(wPage);
    				wPage = (wPage == LGRW3410_DataConstants.LG_FIRST_PAGE) ? (LGRW3410_DataConstants.LG_LAST_PAGE) : (wPage-1);
    			}else{
    				if( wPage == LG_FIRST_PAGE ) break;
    				else {
    				//	wPage = LG_DEC_PAGE(wPage);
    					wPage = (wPage == LGRW3410_DataConstants.LG_FIRST_PAGE) ? (LGRW3410_DataConstants.LG_LAST_PAGE) : (wPage-1);
    				}
    			}
    		}
    		log.debug("bytIdx : "+bytIdx);
    		switch(bytIdx)
    		{
    			case 0: nLast_Read=14;
    					break;
    			case 1: nLast_Read=28;
    					break;
    			case 2: nLast_Read=42;
    					break;
    			case 3: nLast_Read=56;
    					break;
    			case 4: nLast_Read=6;
    					wLastPage+=1;
    					break;
    			case 5: nLast_Read=20;
    					wLastPage+=1;
    					break;
    			case 6: nLast_Read=34;
    					wLastPage+=1;
    					break;
    			case 7: nLast_Read=48;
    					wLastPage+=1;
    					break;
    			case 8: nLast_Read=62;
    					wLastPage+=1;
    					break;
    		}//switch()

    		nCnt = 0;


    		log.debug("wLastPage:"+wLastPage);
    		while( true )
    		{
    	        send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte)(wPage >> 8), (byte)wPage});
    	        control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
    	        DATA_CTRL_IDX = DATA_CTRL_IDX++;  
    	 
    	        frame = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
    	            		send_data, true, 0,0, null);		

    	        buf = read(session,frame);
                temp = buf.encode();
                log.debug("wPage:"+wPage);
    			if( wPage == wLastPage )
    			{
    				ba.append(DataUtil.arrayAppend(temp, 0, nLast_Read , temp, 0, 0));
    				break;
    			}
    			else if( ++nCnt == 2 )
    			{
    				ba.append(DataUtil.arrayAppend(temp, 0, 62 , temp, 0, 0));
    				nCnt	= 0;
    				wPage++;
    				continue;
    			}
    			else
    			{
    				ba.append(DataUtil.arrayAppend(temp, 0, 64 , temp, 0, 0));
    				wPage++;
    				continue;
    			}
    		}
            }else{
            
            	 table =LGRW3410_DataConstants.TABLE_LP;
            	log.debug("DATA_CTRL_IDX:"+DATA_CTRL_IDX);
     	        length = new OCTET(new byte[]{(byte)0x00, (byte) 0x03});
     	        send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
     	        control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
     	        DATA_CTRL_IDX = DATA_CTRL_IDX++;    
     	 
     	        frame = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
     	            		send_data, true, 0,0, null);		

     	        buf = read(session,frame);
                temp = buf.encode();
                 
                 //////
         		int		nCnt=0, nSize=0, nLast_Read=0;
         		int	wLP_Cnt, wPage, wLastPage;
         		int	bytWrap, bytIdx, bytMod;
         		String	strTemp="";

         		//nSize	 = (int)((endDateLong - startDateLong)/(1000*60*60*(60/lpCycle)));
         		nSize=100;
         		log.debug("nSize : "+nSize);
         		//nSize = 288;
         		
         		bytWrap  = temp[18] & 0xff;
        		wLP_Cnt  = ((int)temp[17] & 0xff) << 8;
        		wLP_Cnt |= temp[16];
        		
        		log.debug("wLP_Cnt : "+wLP_Cnt);
        		
        		if( wLP_Cnt < 1 || wLP_Cnt > 8640 ) return ba;

        		wLastPage	= (int)((wLP_Cnt-1) / 20) * 4 + 1000;
        		wPage		= wLastPage;

        		bytIdx		= (wLP_Cnt-1) % 20 ;

        		nCnt		= nSize / 20;
        		bytMod		= nSize % 20;

        		if( (bytIdx+1) < bytMod )	nCnt++;		

        		nCnt *=4;

        		for(int i=0; i<nCnt; i++){

        			if( bytWrap >0){
        				wPage = LGRW3410_DataConstants.KH_DEC_PAGE(wPage);
        			}else{
        				if( wPage == LGRW3410_DataConstants.KH_FIRST_PAGE ) break;
        				else wPage = LGRW3410_DataConstants.KH_DEC_PAGE(wPage);
        			}
        		}

        		switch(bytIdx)
        		{
        			case 0: nLast_Read=12;
        					break;
        			case 1: nLast_Read=24;
        					break;
        			case 2: nLast_Read=36;
        					break;
        			case 3: nLast_Read=48;
        					break;
        			case 4: nLast_Read=60;
        					break;
        			case 5: nLast_Read=8;
        					wLastPage+=1;
        					break;
        			case 6: nLast_Read=20;
        					wLastPage+=1;
        					break;
        			case 7: nLast_Read=32;
        					wLastPage+=1;
        					break;
        			case 8: nLast_Read=44;
        					wLastPage+=1;
        					break;
        			case 9: nLast_Read=56;
        					wLastPage+=1;
        					break;
        			case 10:nLast_Read=4;
        					wLastPage+=2;
        					break;
        			case 11:nLast_Read=16;
        					wLastPage+=2;
        					break;
        			case 12:nLast_Read=28;
        					wLastPage+=2;
        					break;
        			case 13:nLast_Read=40;
        					wLastPage+=2;
        					break;
        			case 14:nLast_Read=52;
        					wLastPage+=2;
        					break;
        			case 15:nLast_Read=64;
        					wLastPage+=2;
        					break;
        			case 16:nLast_Read=12;
        					wLastPage+=3;
        					break;
        			case 17:nLast_Read=24;
        					wLastPage+=3;
        					break;
        			case 18:nLast_Read=36;
        					wLastPage+=3;
        					break;
        			case 19:nLast_Read=48;
        					wLastPage+=3;
        					break;
        		}//switch

        		
        		nCnt = 0;
        		while( true )
        		{
        			log.debug("wLastPage : "+wLastPage);
        			log.debug("wPage : "+wPage);
        		    send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte)(wPage >> 8), (byte)wPage});
         	        control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
         	        DATA_CTRL_IDX = DATA_CTRL_IDX++;  
         	 
         	        frame = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
         	            		send_data, true, 0,0, null);		

         	        buf = read(session,frame);
                    temp = buf.encode();
                     
        			if( temp == null || temp.length<1 ) {
        				return ba;
        			}
        			
        			if( wPage == wLastPage )
        			{
        			//	baLP.BlockAdd( baRxData, 0, nLast_Read );	//LP
        				ba.append(DataUtil.arrayAppend(temp, 0, nLast_Read , temp, 0, 0));
        				break;
        			}
        			else if( ++nCnt == 4 )
        			{
        			//	baLP.BlockAdd( baRxData, 0, 48 );	//LP
        				ba.append(DataUtil.arrayAppend(temp, 0, 48 , temp, 0, 0));
        				nCnt	= 0;
        				wPage = LGRW3410_DataConstants.KH_INC_PAGE(wPage);
        				continue;
        			}
        			else
        			{
        			//	baLP.BlockAdd( baRxData, 0, 64 );	//LP
        				ba.append(DataUtil.arrayAppend(temp, 0, 64 , temp, 0, 0));
        				wPage = LGRW3410_DataConstants.KH_INC_PAGE(wPage);
        				continue;
        			}
        		}
         		
            }
        }
        catch (ParseException e)
        {
            log.error("lp read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Loadprofile read data error");
        }
        catch (Exception e)
        {
            log.error("lp read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Loadprofile read data error");
        } 
        log.debug("========== LoadProfile Read End ===============");
        return ba;
    }
    
    public ByteArray billRead(IoSession session) throws MRPException
    {
        log.debug("========== Prev Bill Read Start ===============");
        ByteArray ba = new ByteArray();
    	byte			bytDemandID, bytPreDemand;
    	short			kh_tbl;
    	String			strMsg;
    	int				nResult;
    	
        byte[] temp = null;
        LGRW3410_RequestDataFrame frame = null;
        LGRW3410_ReceiveDataFrame buf = null;
        try{
        	//GET CONFIG
	        //Meter Read #1
	        short table =LGRW3410_DataConstants.TABLE_CONFIG;
	        OCTET length = new OCTET(new byte[]{(byte)0x00, (byte) 0x03});
	        OCTET send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        BYTE control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
	        DATA_CTRL_IDX = DATA_CTRL_IDX++;    
	        
	        frame  = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
	            		send_data, true, 0,0, null);

            buf = read(session,frame);        
            temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 27, 8, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, 19, 4, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, 0, 1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, 1, 4, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, 10, 1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, 11, 1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, 12, 6, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, 18, 1, temp, 0, 0));
            ba.append(DataUtil.arrayAppend(temp, 43, 8, temp, 0, 0));
            
//          SELF DEMAND DATETIME
//          Meter Read #2
            table =LGRW3410_DataConstants.TABLE_DEMANDINFO;
            send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
	        DATA_CTRL_IDX = DATA_CTRL_IDX++;  
	        
            frame = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
                                   send_data, true, 0,0, null);
            buf = read(session,frame);
            temp = buf.encode();
            ba.append(DataUtil.arrayAppend(temp, 0, 60, temp, 0, 0));
        	
//          Demand Info
//          Meter Read #2
            table =LGRW3410_DataConstants.TABLE_SELFREADING;
            send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
	        DATA_CTRL_IDX = DATA_CTRL_IDX++;  
	        
            frame = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
                                   send_data, true, 0,0, null);
            buf = read(session,frame);
            byte[] demandInfo = buf.encode();
            bytDemandID = demandInfo[19];
        	bytPreDemand= demandInfo[29];
        	
//          Meter Read #3
        	switch( bytDemandID )
        	{
        	case 0: table = 30;
        			break;
        	case 1: table = 50;
        			break;
        	case 2: table = 70;
        			break;
        	case 3: table = 90;
        			break;
        	}
        	send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
	        DATA_CTRL_IDX = DATA_CTRL_IDX++;  
	        
            frame = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
                                   send_data, true, 0,0, null);
            buf = read(session,frame);        
            byte[] energyTotal = buf.encode();

//          Meter Read #4
    		// Active Cummulate Rate A
            switch( bytDemandID )
        	{
        	case 0: table = 31;
        			break;
        	case 1: table = 51;
        			break;
        	case 2: table = 71;
        			break;
        	case 3: table = 91;
        			break;
        	}
            send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
	        DATA_CTRL_IDX = DATA_CTRL_IDX++;  
	        
            frame = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
                                   send_data, true, 0,0, null);
            buf = read(session,frame);        
            byte[] cummulateRateA = buf.encode();

//          Meter Read #5
            switch( bytDemandID )
        	{
        	case 0: table = 32;
        			break;
        	case 1: table = 52;
        			break;
        	case 2: table = 72;
        			break;
        	case 3: table = 92;
        			break;
        	}
            send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
	        DATA_CTRL_IDX = DATA_CTRL_IDX++;  
	        
            frame = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
                                   send_data, true, 0,0, null);
            buf = read(session,frame);        
            byte[] cummulateRateB = buf.encode();
  
//          Meter Read #6
            switch( bytDemandID )
        	{
        	case 0: table = 33;
        			break;
        	case 1: table = 53;
        			break;
        	case 2: table = 73;
        			break;
        	case 3: table = 93;
        			break;
        	}
            send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
	        DATA_CTRL_IDX = DATA_CTRL_IDX++;  
	        
            frame = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
                                   send_data, true, 0,0, null);
            buf = read(session,frame);        
            byte[] cummulateRateC = buf.encode();
            
//          Meter Read #7
            switch( bytDemandID )
        	{
        	case 0: table = 36;
        			break;
        	case 1: table = 56;
        			break;
        	case 2: table = 76;
        			break;
        	case 3: table = 96;
        			break;
        	}
            send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
	        DATA_CTRL_IDX = DATA_CTRL_IDX++;  
	        
            frame = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
                                   send_data, true, 0,0, null);
            buf = read(session,frame);        
            byte[] maxPowerRateA = buf.encode();

//          Meter Read #8
            switch( bytDemandID )
        	{
        	case 0: table = 37;
        			break;
        	case 1: table = 57;
        			break;
        	case 2: table = 77;
        			break;
        	case 3: table = 97;
        			break;
        	}
            send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
	        DATA_CTRL_IDX = DATA_CTRL_IDX++;  
	        
            frame = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
                                   send_data, true, 0,0, null);
            buf = read(session,frame);        
            byte[] maxPowerRateB = buf.encode();

//          Meter Read #9
            switch( bytDemandID )
        	{
        	case 0: table = 38;
        			break;
        	case 1: table = 58;
        			break;
        	case 2: table = 78;
        			break;
        	case 3: table = 98;
        			break;
        	}
            send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
	        DATA_CTRL_IDX = DATA_CTRL_IDX++;  
	        
            frame = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
                                   send_data, true, 0,0, null);
            buf = read(session,frame);        
            byte[] maxPowerRateC = buf.encode();
     
//          Meter Read #10
            switch( bytDemandID )
        	{
        	case 0: table = 41;
        			break;
        	case 1: table = 61;
        			break;
        	case 2: table = 81;
        			break;
        	case 3: table = 101;
        			break;
        	}
            send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
	        DATA_CTRL_IDX = DATA_CTRL_IDX++;  
	        
            frame = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
                                   send_data, true, 0,0, null);
            buf = read(session,frame);        
            byte[] prevCummEnergyA = buf.encode();

//          Meter Read #11
            switch( bytDemandID )
        	{
        	case 0: table = 42;
        			break;
        	case 1: table = 62;
        			break;
        	case 2: table = 82;
        			break;
        	case 3: table = 102;
        			break;
        	}
            send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
	        DATA_CTRL_IDX = DATA_CTRL_IDX++;  
	        
            frame = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
                                   send_data, true, 0,0, null);
            buf = read(session,frame);        
            byte[] prevCummEnergyB = buf.encode();

//          Meter Read #12
            switch( bytDemandID )
        	{
        	case 0: table = 43;
        			break;
        	case 1: table = 63;
        			break;
        	case 2: table = 83;
        			break;
        	case 3: table = 103;
        			break;
        	}
            send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        control = new BYTE(LGRW3410_DataConstants.DATA_CTRL[DATA_CTRL_IDX%2]);
	        DATA_CTRL_IDX = DATA_CTRL_IDX++;  
	        
            frame = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
                                   send_data, true, 0,0, null);
            buf = read(session,frame);        
            byte[] prevCummEnergyC = buf.encode();

            //Active Total Energy
            ba.append(DataUtil.arrayAppend(energyTotal, 0, 4, energyTotal, 0, 0));
            
            //Active Cummulate Rate A
            ba.append(DataUtil.arrayAppend(cummulateRateA, 0, 4, cummulateRateA, 0, 0));
            //Active maxPower Rate A
            ba.append(DataUtil.arrayAppend(maxPowerRateA, 0, 2, maxPowerRateA, 0, 0));
            // Active maxPower Date Time Rate A
            ba.append(DataUtil.arrayAppend(maxPowerRateA, 2, 8, maxPowerRateA, 0, 0));
            // Active prevCumm Rate A
            ba.append(DataUtil.arrayAppend(prevCummEnergyA, 0, 4, prevCummEnergyA, 0, 0));
            
            // Active Cummulate Rate B
            ba.append(DataUtil.arrayAppend(cummulateRateB, 0, 4, cummulateRateB, 0, 0));
            //Active maxPower Rate B
            ba.append(DataUtil.arrayAppend(maxPowerRateB, 0, 2, maxPowerRateB, 0, 0));
            // Active maxPower Date Time Rate B
            ba.append(DataUtil.arrayAppend(maxPowerRateB, 2, 8, maxPowerRateB, 0, 0));
            // Active prevCumm Rate B
            ba.append(DataUtil.arrayAppend(prevCummEnergyB, 0, 4, prevCummEnergyB, 0, 0));
            
//          Active Cummulate Rate C
            ba.append(DataUtil.arrayAppend(cummulateRateC, 0, 4, cummulateRateC, 0, 0));
            //Active maxPower Rate C
            ba.append(DataUtil.arrayAppend(maxPowerRateC, 0, 2, maxPowerRateC, 0, 0));
            // Active maxPower Date Time Rate C
            ba.append(DataUtil.arrayAppend(maxPowerRateC, 2, 8, maxPowerRateC, 0, 0));
            // Active prevCumm Rate C
            ba.append(DataUtil.arrayAppend(prevCummEnergyC, 0, 4, prevCummEnergyC, 0, 0));
            
            //Reactive Total Energy
            ba.append(DataUtil.arrayAppend(energyTotal, 4, 4, energyTotal, 0, 0));
            
        	// Active Cummulate Rate A
            ba.append(DataUtil.arrayAppend(cummulateRateA, 4, 4, cummulateRateA, 0, 0));
            // Reactive maxPower Rate A
            ba.append(DataUtil.arrayAppend(maxPowerRateA, 10, 2, maxPowerRateA, 0, 0));
            // Reactive maxPower Date Time Rate A
            ba.append(DataUtil.arrayAppend(maxPowerRateA, 12, 8, maxPowerRateA, 0, 0));
            // Reactive prevCumm Rate A
            ba.append(DataUtil.arrayAppend(prevCummEnergyA, 4, 4, prevCummEnergyA, 0, 0));	
            
            // Active Cummulate Rate B
            ba.append(DataUtil.arrayAppend(cummulateRateB, 4, 4, cummulateRateB, 0, 0));
            // Reactive maxPower Rate B
            ba.append(DataUtil.arrayAppend(maxPowerRateB, 10, 2, cummulateRateB, 0, 0));
            // Reactive maxPower Date Time Rate B
            ba.append(DataUtil.arrayAppend(maxPowerRateB, 12, 8, cummulateRateB, 0, 0));
            // Reactive prevCumm Rate B
            ba.append(DataUtil.arrayAppend(prevCummEnergyB, 4, 4, cummulateRateB, 0, 0));	
            
            // Active Cummulate Rate C
            ba.append(DataUtil.arrayAppend(cummulateRateC, 4, 4, cummulateRateC, 0, 0));
            // Reactive maxPower Rate C
            ba.append(DataUtil.arrayAppend(maxPowerRateC, 10, 2, cummulateRateC, 0, 0));
            // Reactive maxPower Date Time Rate C
            ba.append(DataUtil.arrayAppend(maxPowerRateC, 12, 8, cummulateRateC, 0, 0));
            // Reactive prevCumm Rate C
            ba.append(DataUtil.arrayAppend(prevCummEnergyC, 4, 4, cummulateRateC, 0, 0));	
            
        }catch(Exception e){
            log.error("prev bill read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Billing data read error");
        }
        log.debug("========== Prev Bill Read End ===============");
        return ba;
    }
    
    public ByteArray getPowerFail(IoSession session, String startday, String endday, int lpCycle, byte seqNo) throws MRPException
    {
        log.debug("========== getPowerFail Read Start ===============");
        ByteArray ba = new ByteArray();     
        try
        {
	        
	        short table =LGRW3410_DataConstants.TABLE_POWERFAIL;
	
	        OCTET length = new OCTET(new byte[]{(byte)0x00, (byte) 0x03});
	        OCTET send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        BYTE control = new BYTE(seqNo);     
	 
	        LGRW3410_RequestDataFrame frame 
	            = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
	            		send_data, true, 0,0, null);	
        }catch (Exception e){
	    	log.error("PowerFail read error",e);
	    	throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Loadprofile read data error");
	    } 
	    log.debug("========== getPowerFail Read End ===============");
	    return ba;
    }
    
    public ByteArray getPowerRecover(IoSession session, String startday, String endday, int lpCycle, byte seqNo) throws MRPException
    {
        log.debug("========== getPowerRecover Read Start ===============");
        ByteArray ba = new ByteArray();   
        try
        {
	        short table =LGRW3410_DataConstants.TABLE_POWERRECOVER;
	
	        OCTET length = new OCTET(new byte[]{(byte)0x00, (byte) 0x03});
	        OCTET send_data = new OCTET(new byte[]{(byte) LGRW3410_DataConstants.DATA_COMMAND_READ, (byte) (table >> 8), (byte)table});
	        BYTE control = new BYTE(seqNo);     
	 
	        LGRW3410_RequestDataFrame frame 
	            = new LGRW3410_RequestDataFrame(new BYTE(), control, length,
	            		send_data, true, 0,0, null);	
        }catch (Exception e){
	    	log.error("PowerRecover read error",e);
	    	throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Loadprofile read data error");
	    }
	    log.debug("========== getPowerRecover Read End ===============");
	    return ba;
    }

    public ByteArray pqRead(IoSession session) throws MRPException
    {
        return null;
    }

    public void quit() throws MRPException
    {
        
    }

    public HashMap timeCheck(IoSession session) throws MRPException
    {
        return null;
    }

    public HashMap timeSync(IoSession session, int timethreshold) throws MRPException
    {
        return null;
    }
    
    public long getSendBytes() throws MRPException{
        return this.sendBytes;// session.getWrittenBytes()
    }
    
    private String parseYyyymmddhhmmss(byte[] b)
	throws Exception {

		int len = b.length;
		if(len != 8)
		throw new Exception("YYYYMMDDHHMMSS LEN ERROR : "+len);

//		int idx = 0;
		int year = (((int)b[0] & 0xff) << 8) | (b[1]&0xff);
		
		int mm = b[2];
		int dd = b[3];
		int hh = b[4];
		int MM = b[5];
		int ss = b[6];
		
		StringBuffer ret = new StringBuffer();
		
		ret.append(frontAppendNStr('0',""+year,4));
		ret.append(frontAppendNStr('0',""+mm,2));
		ret.append(frontAppendNStr('0',""+dd,2));
		ret.append(frontAppendNStr('0',""+hh,2));
		ret.append(frontAppendNStr('0',""+MM,2));
		ret.append(frontAppendNStr('0',""+ss,2));
		
		log.debug(" eventTime :"+ ret.toString());
		return ret.toString();
	}

	/**
	 * @param append source of String
	 * @param str	to append  
	 * @param length
	 * @return
	 */
	public static String frontAppendNStr(char append, String str, int length)
	{
		StringBuffer b = new StringBuffer("");

		try {
			if(str.length() < length)
			{
			   for(int i = 0; i < length-str.length() ; i++)
				   b.append(append);
			   b.append(str);
			}
			else
			{
				b.append(str);
			}
		} catch(Exception e) {
			log.error("Util.frontAppendNStr : " +e.getMessage());
		}
		return b.toString();
	}
	
    public static int LG_INC_PAGE(int page){
    	return (page == LGRW3410_DataConstants.LG_LAST_PAGE) ? (LGRW3410_DataConstants.LG_FIRST_PAGE) : (page++);
    }
    
    public static int LG_DEC_PAGE(int page){
    	return (page == LGRW3410_DataConstants.LG_FIRST_PAGE) ? (LGRW3410_DataConstants.LG_LAST_PAGE) : (page--);
    }

	@Override
	public void setGroupNumber(String groupNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMemberNumber(String memberNumber) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setModemId(String modemId) {
		// TODO Auto-generated method stub
		
	}

    @Override
    public CommandData execute(Socket socket, CommandData command)
            throws MRPException {
        // TODO Auto-generated method stub
        return null;
    }

}
