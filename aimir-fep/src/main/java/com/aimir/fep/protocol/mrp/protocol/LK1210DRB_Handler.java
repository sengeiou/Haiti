/** 
 * @(#)LK1210DRB_Handler.java        *
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

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DataSVC;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.MeterModel;
import com.aimir.constants.CommonConstants.ModemType;
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
	
public class LK1210DRB_Handler extends MeterProtocolHandler { 

    private static Log log = LogFactory.getLog(LK1210DRB_Handler.class);
    private String meterId;
    private String modemId;
    private String groupId = "";
    private String memberId = "";
    private String mcuSwVersion = "";

    private static byte METER_TYPE =(byte)0x89;
    
    private static byte g_rxd_control = (byte) 0x00;
    private static byte g_txd_control = (byte) 0x00;
	/**
	 * Constructor.<p>
	 */
    protected long sendBytes;
    
	public LK1210DRB_Handler() {	
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
	public LK1210DRB_Handler(int meterModel) {	
		if(meterModel == MeterModel.LSIS_LK3410DRB_120.getCode()) 
			this.METER_TYPE = (byte)0x8A;
		else if(meterModel == MeterModel.LSIS_LK1210DRB_120.getCode())
			this.METER_TYPE = (byte)0x89;
	}
	
    public CommandData execute(MRPClientProtocolHandler handler,
                          IoSession session, 
                          CommandData command) throws MRPException
    {
        this.handler = handler;
        
        CommandData commandData = null;
        byte[] pbInfo = null;
        byte[] powerInfo = null;
        byte[] event = null;
        byte[] cpb = null;
        byte[] tpb = null;
        byte[] lpd = null;
        byte[] meterconf = null;
        ByteArray ba = new ByteArray();
        int nOffset = 0;
        int nCount = 96;
        String from = "";
        String cmd = command.getCmd().getValue();
        log.debug("==============LK1210DRB_Handler start cmd:"+cmd+"================");
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
                ba.append(new byte[]{METER_TYPE});
                ba.append(new byte[]{(byte)0x00});
                ba.append(new byte[]{(byte)0x00});
                ba.append(new byte[]{(byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30});//ZIGBEE ID
                ba.append(new byte[]{0x00,0x00,0x00,0x00});
                
            	g_rxd_control = 0x00;
            	g_txd_control = 0x00;
            	log.debug("kind :: "+ kind);
            	switch(kind){
                case 1:
	                tpb = lpDataRead(session,nOffset,TimeUtil.getCurrentDateUsingFormat("yyyyMMddHHmmss"),15).toByteArray();
	                ba.append(DataConstants.LP_IEIU);
	                ba.append(tpb);
	                terminate(session);
	                break;
               case 2:
	                tpb = billingDataRead(session,from,TimeUtil.getCurrentDateUsingFormat("yyyyMMddHHmmss"),15).toByteArray();
	                ba.append(DataConstants.PB_IEIU);
	                ba.append(tpb);
	                terminate(session);
	           break;
               default:
                   throw new MRPException(MRPError.ERR_INVALID_PARAM,"Invalid parameters");
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
                log.debug("meterEntryData=>"+smiValue[0].getVariable().toString()+","+
                        new OCTET(ba.toByteArray()).toHexString());
                meterDataEntry md = new meterDataEntry();
                md.setMdID(new OCTET(smiValue[0].getVariable().toString()));
                md.setMdSerial(new OCTET(this.meterId));
                md.setMdServiceType(new BYTE(DataSVC.Electricity.getCode()));//1: electric
                md.setMdTime(new TIMESTAMP(currTime));
                md.setMdType(new BYTE(ModemType.IEIU.getCode()));
                md.setMdVendor(new BYTE(6));//LSIS
                md.setMdData(new OCTET(ba.toByteArray()));
                commandData = new CommandData();
                commandData.setCnt(new WORD(1));
                commandData.setTotalLength(ba.toByteArray().length);
                commandData.append(new SMIValue(new OID("10.1.0"),new OPAQUE(md)));
            }
        }

        log.debug("==============LK1210DRB_Handler end ================");
        return commandData;
    }

    public ByteArray configureRead(IoSession session) throws MRPException
    {
    	   return null;
    }
	
	/**
	 * Read Event Log.<p>
	 * @return
	 * @throws MeterException
	 */
	public ByteArray eventlogInfo(IoSession session) throws MRPException
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
	
    protected LK1210DRB_ReceiveDataFrame read(IoSession session,LK1210DRB_RequestDataFrame frame,
    							byte bytCmd, byte bytClass ) throws MRPException
    {
        int sequence = 0;
    	byte[] baData = null;
    	byte[] baTemp = null;
    	int	nSize, nPktSize = 0;
    	int retry =3;
    	
    	try
        {
       //     IoBuffer buf = frame.getIoBuffer();
            LK1210DRB_ReceiveDataFrame rcvFrame = null; //new LK1210DRB_ReceiveDataFrame();
       //     log.debug("send class "+bytClass+", cmd "+bytCmd+"=>"+buf.getHexDump());
            int cnt=0;
            while(cnt<retry && baTemp==null){
            	IoBuffer buf = frame.getIoBuffer();
                rcvFrame = new LK1210DRB_ReceiveDataFrame();
                log.debug("send class "+bytClass+", cmd "+bytCmd+"=>"+buf.getHexDump());
            	//buf = frame.getIoBuffer();
	            session.write(buf); 
	    //        byte[] message = (byte[])handler.getMsg(session,20,160,false);
	            //byte[] message = (byte[])handler.getMessageLSIS(session,2,MeterModel.LSIS_LK1210DRB_120);
	            byte[] message = (byte[])handler.getMessage(session,2,MeterModel.LSIS_LK1210DRB_120.getCode().intValue());
	            baTemp = message;
	            
	            if(baTemp != null && baTemp.length > 0){
	            	log.debug("receive class "+bytClass+", cmd "+bytCmd+"=>"+new OCTET(baTemp).toHexString());
					nSize	= baTemp.length;
		
					if( nSize < 3 ) {
	                	rcvFrame = null;
	                	return null;
					}
					
					if( baTemp[0] != LK1210DRB_DataConstants.HDLC_FLAG ){ 
						log.debug("HDLC_FLAG error");
						rcvFrame = null;
						return null;
					}
					nPktSize	=  ((((baTemp[1] & (byte)0x07) & 0xff) << 8) | (baTemp[2] & 0xff)) + 2;
					log.debug("nPktSize = "+nPktSize);
					if( nSize < nPktSize) {
	                	rcvFrame = null;
	                	return null;
					}
					baData = new byte[nPktSize];
					System.arraycopy(baTemp, 0, baData, 0, nPktSize);
		
					if( baData[nPktSize-1] != LK1210DRB_DataConstants.HDLC_FLAG ) {
						log.debug("error baData[nPktSize-1] = "+baData[nPktSize-1]);
	                	rcvFrame = null;
	                	return null;
					}
		/*
					if( !DLMS_Calc_Fcs( baData, 1, 3 ) )	{
						//return LK1210DRB_DataConstants.CRC_ERR;
						result = nakByte();
	                	rcvFrame = null;
					}
					if( !DLMS_Calc_Fcs( baData, 1, nPktSize - 3 ) )	{
						result = nakByte();
	                	rcvFrame = null;
					}
		    */        
					switch( bytCmd )
					{
					case LK1210DRB_DataConstants.LSLOW_SNRM:
						if( nPktSize != 33 ) {
	                    	rcvFrame = null;
	                    	return null;
						}
						break;
					case LK1210DRB_DataConstants.LSLOW_DISC:
						if( nPktSize != 10 ) {
	                    	rcvFrame = null;
	                    	return null;
						}
						break;
					case LK1210DRB_DataConstants.LSLOW_LL_AUTH:
						if( nPktSize != 58 ) {
	                    	rcvFrame = null;
	                    	return null;
						}
						break;
					case LK1210DRB_DataConstants.LSLOW_READ:
						switch( bytClass )
						{
						case LK1210DRB_DataConstants.READ_METER_INFO:
							if( nPktSize != 88 ){
								log.debug("nPktSize is not 88");
		                    	rcvFrame = null;
		                    	return null;
							}
							break;
						case LK1210DRB_DataConstants.READ_CURR_BILL:
						case LK1210DRB_DataConstants.READ_LAST_BILL:
							if( nPktSize != 119 ){ 
								log.debug("nPktSize is not 119");
								rcvFrame = null;
								return null;
							}
							break;
						case LK1210DRB_DataConstants.READ_LAST_BILL_INFO:
						case LK1210DRB_DataConstants.READ_PF_INFO:
						case LK1210DRB_DataConstants.READ_LP_INFO:
							if( nPktSize != 24 ) {
								log.debug("nPktSize is not 24");
		                    	rcvFrame = null;
		                    	return null;
							}
							break;
						case LK1210DRB_DataConstants.READ_LP_READ_CONTINUE:
							if( nPktSize < 19 ) {
								log.debug("nPktSize <19");
		                    	rcvFrame = null;
		                    	return null;
							}
						default:
							break;
						}
						break;
					default:
						break;
					}
					if(rcvFrame==null)
			            cnt++;
			            else break;
	            }else{
	            	cnt++;
	            }
            }
            
            if(rcvFrame!=null && baData!=null)
            	rcvFrame.append(baData);
     //       else
     //       	throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
            
            return rcvFrame;
        }
        catch (Exception e)
        {
            log.error("Read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Data receive error");
        } 
        
    }
    
	public boolean DLMS_Calc_Fcs(byte[] baData, int npos, int len)						// HDLC frame check sequence (FCS)
	{
		char		trialfcs;
	
		trialfcs  = LK1210DRB_DataConstants.DLMS_Fcs16((char)0xffff, baData, npos, len);
		trialfcs ^= 0xffff;
	
		baData[npos+len]	= (byte)(trialfcs & 0x00ff);
		baData[(npos+len)+1]= (byte)((trialfcs >> 8) & 0x00ff);
	
		trialfcs = LK1210DRB_DataConstants.DLMS_Fcs16((char)0xffff, baData, npos, len + 2);
	
		if(trialfcs == 0xf0b8)
		    return true;
		
		return false;
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
 //       snrm(session);
  //      ll_auth(session);
    }

    public ByteArray lpDataRead(IoSession session, int nOffset, String endday, int lpCycle) throws MRPException
    {
    	ByteArray ba = new ByteArray();  
        LK1210DRB_RequestDataFrame frame = null;
        LK1210DRB_ReceiveDataFrame buf = null;
        int retry=3;
        //snrm
        try
        {
        	byte[] data = DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_SNRM);
        	
        	OCTET send_Data = new OCTET(data);
        	
            byte[] message = null;
            int cnt =0;
            while(cnt<retry && message ==null){
            	frame = new LK1210DRB_RequestDataFrame(new BYTE(),
                		send_Data, 0,0, null);
                IoBuffer buf2 = frame.getIoBuffer();
	            log.debug("send snrm=>"+buf2.getHexDump());
	            session.write(buf2); 
//	            message = (byte[])handler.getMsg(session,20,160,false);
//	            message = (byte[])handler.getMessageLSIS(session,2,MeterModel.LSIS_LK1210DRB_120);
	            message = (byte[])handler.getMessage(session,2,MeterModel.LSIS_LK1210DRB_120.getCode().intValue());
	            cnt++;
            }
            if(message != null && message.length > 0){
                log.debug("receive snrm =>"+new OCTET(message).toHexString());
                g_rxd_control	= message[6];
                log.debug("g_rxd_control" +g_rxd_control);
              //  session.write(ackByte());
            }else{
            	throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"snrm meter error");
            	// session.write(nakByte());
            }
        }
        catch (Exception e)
        {
            log.error("snrm error",e);
            throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"snrm meter error");
        } 
        
        //ll_auth
        try
        {	
        	byte[] data = DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_LL_AUTH);
        	OCTET send_Data = new OCTET(data);
            
            byte[] message = null;
            int cnt =0;
            while(cnt<retry && message ==null){
            	frame = new LK1210DRB_RequestDataFrame(new BYTE(), send_Data, 0,0, null);
                IoBuffer buf2 = frame.getIoBuffer();
	            log.debug("send ll_auth=>"+buf2.getHexDump());
	            session.write(buf2); 
	     //       message = (byte[])handler.getMsg(session,20,160,false);
//	            message = (byte[])handler.getMessageLSIS(session,2,MeterModel.LSIS_LK1210DRB_120);
	            message = (byte[])handler.getMessage(session,2,MeterModel.LSIS_LK1210DRB_120.getCode().intValue());
	            cnt++;
            }
            if(message != null && message.length > 0){
                log.debug("receive ll_auth =>"+new OCTET(message).toHexString());
            //    session.write(ackByte());
                g_rxd_control	= message[6];
                log.debug("g_rxd_control= "+g_rxd_control);
            }else{
            	throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"error to get auth.");
            //	 session.write(nakByte());
            }
            
        }
        catch (Exception e)
        {
            log.error("ll_auth error",e);
            throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"error to get auth.");
        } 
        
        //READ_METER_INFO
        try
        {
        	byte[] data = DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_READ, 
            							LK1210DRB_DataConstants.READ_METER_INFO);
        	OCTET send_Data = new OCTET(data);
            
            byte[] temp = null;
            int cnt=0;
            while(cnt<retry && buf==null){ 
            	frame = new LK1210DRB_RequestDataFrame(new BYTE(),
                		send_Data, 0,0, null);
	            buf = read(session,frame, LK1210DRB_DataConstants.LSLOW_READ, LK1210DRB_DataConstants.READ_METER_INFO);        
	            cnt++;
            }
            if(buf==null)
            	return null;

            temp = buf.encode();
            
            g_rxd_control	= temp[6];
            log.debug("g_rxd_control= "+g_rxd_control);
	        ba.append(DataUtil.arrayAppend(temp, 25, 1, temp, 0, 0)); //Meter manufacturer number
	        ba.append(DataUtil.arrayAppend(temp, 22, 1, temp, 0, 0)); //Meter Type
	        ba.append(new byte[]{(byte)0x00});
	        ba.append(DataUtil.arrayAppend(temp, 29, 7, temp, 0, 0)); //Meter ID
	        ba.append(DataUtil.arrayAppend(temp, 48, 1, temp, 0, 0)); //Meter State information : Error
	        ba.append(DataUtil.arrayAppend(temp, 51, 1, temp, 0, 0)); //Meter State information : Caution
	        ba.append(DataUtil.arrayAppend(temp, 53, 2, temp, 0, 0)); //Meter Constant
	        ba.append(DataUtil.arrayAppend(temp, 56, 2, temp, 0, 0)); //Meter Overall Transformer ratio : voltage ratio
	        ba.append(DataUtil.arrayAppend(temp, 59, 2, temp, 0, 0)); //Meter Overall Transformer ratio : current ratio
	        ba.append(DataUtil.arrayAppend(temp, 62, 1, temp, 0, 0)); //Meter Periodic self-read day
	        ba.append(DataUtil.arrayAppend(temp, 64, 1, temp, 0, 0)); //Meter Record interval for load profile
	        ba.append(DataUtil.arrayAppend(temp, 67, 6, temp, 0, 0)); //Meter Recording date of the last load profile
	        ba.append(DataUtil.arrayAppend(temp, 76, 9, temp, 0, 0)); //Meter Billing information of the last month
	        ba.append(DataUtil.arrayAppend(temp, 38, 8, temp, 0, 0)); //Meter Current date/time of meter
          
	        log.debug("READ_METER_INFO :: "+new OCTET(ba.toByteArray()).toHexString());
	        buf = null;
        }
        catch (Exception e)
        {
            log.error("Meter Info Read error",e);
            throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"Meter Info Read error");
        } 
        
        //READ LP INFO
        int to_lp		=	0;
        int from_lp	=	0;
        try{
        	//READ LP INFO
	        byte[] data = DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_READ, 
					LK1210DRB_DataConstants.READ_LP_INFO);
			OCTET send_Data = new OCTET(data);
			
              
            byte[] temp = null;
            int cnt=0;
            while(cnt<retry && buf==null){ 
            	frame = new LK1210DRB_RequestDataFrame(new BYTE(),
						send_Data, 0,0, null);
            	buf = read(session,frame, LK1210DRB_DataConstants.LSLOW_READ,LK1210DRB_DataConstants.READ_LP_INFO);        
	            cnt++;
            }
            
            if(buf==null)
            	return ba;
            temp = buf.encode();
            ByteArray baLP = new ByteArray();  
            g_rxd_control	= temp[6];
            log.debug("g_rxd_control= "+g_rxd_control);
            
            to_lp	 =   (temp[17] & 0xff)  << 24;
            to_lp	|=   (temp[18] & 0xff)<< 16;
            to_lp	|=   (temp[19] & 0xff)<< 8;
            to_lp	|=   (temp[20] & 0xff);
            ba.append(DataUtil.arrayAppend(temp, 17, 4, temp, 0, 0)); //total_lp
        //    to_lp = (int)(temp[20] & 0xff);
            log.debug("to_lp= "+to_lp);
         //   ba.append(DataConstants.LP_IEIU);
    	//	ba.append(DataUtil.arrayAppend(temp, 20, 1, temp, 0, 0));
    		
    	//	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        //    formatter.setTimeZone(TimeZone.getTimeZone(AimirModel.UTC_ID));

       //     long startDateLong = formatter.parse((startday+"000000").substring(0,14)).getTime();
       //     long endDateLong = formatter.parse((endday+"235959").substring(0,14)).getTime();

        //    log.debug("startday : "+startday);
            
       //     int nSize	 = (int)((int)((endDateLong - startDateLong)/(1000*60*60/lpCycle)) * 1.2);
       int nSize = nOffset *(24*(60/lpCycle));
            log.debug("nSize= "+nSize);
    		
            if( to_lp < nSize )
    			from_lp	= 1;
    		else
    			from_lp	= (to_lp - nSize + 1);
    		log.debug("to_lp= "+to_lp);
    		log.debug("from_lp= "+from_lp);
    		log.debug("nSize= "+nSize);
            
         //   from_lp	= 1;
        //    ba.append(new byte[]{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00});
    		byte[] countLP = new byte[2];
    		int countLPInt = to_lp-from_lp+1;
    		countLP[0] = (byte)((countLPInt & 0x0000FF00 ) >> 8);
    		countLP[1] = (byte)((countLPInt & 0x000000FF )); 
    		log.debug("countLPInt ="+countLPInt);
    		log.debug("countLP[0] ="+countLP[0]);
    		log.debug("countLP[1] ="+countLP[1]);
    		ba.append(DataUtil.arrayAppend(countLP, 0, 2, temp, 0, 0)); //countLP
    		buf = null;
    		log.debug("READ_LP_INFO :: "+new OCTET(ba.toByteArray()).toHexString());
    		//READ LP DATA
    		byte bytCmd = LK1210DRB_DataConstants.LSLOW_READ;
    		byte bytClass = LK1210DRB_DataConstants.READ_LP_READ;
    		int nCnt=0;
    		byte response_flag	= 0;
    		byte lp_continue = 0;
    		boolean		bFlag = true;
    		data = DLMS_Make_Msg(bytCmd, bytClass, true, from_lp, to_lp );
    		while( nCnt < retry ){
	    		send_Data = new OCTET(data);
				frame = new LK1210DRB_RequestDataFrame(new BYTE(),
													send_Data, 0,0, null);
	            buf = read(session,frame, bytCmd,bytClass);   

	            if(buf==null){
	            	nCnt++;
	            	continue;
	            }
	            
	            temp = buf.encode();
	            if(temp==null || temp.length<1){
	            	nCnt++;
	            	continue;
	            }
	            g_rxd_control	= temp[6];
	            log.debug("g_rxd_control= "+g_rxd_control);
	            switch(bytCmd){
	            case LK1210DRB_DataConstants.LSLOW_RR:
					switch( bytClass )
					{
					case LK1210DRB_DataConstants.READ_LP_READ:
						baLP = copy_to_lp_data(baLP, temp, bytClass, true, response_flag);

						if(temp[1] == (byte)0xa8) {
							bytCmd 		= LK1210DRB_DataConstants.LSLOW_RR;
							bytClass 	= LK1210DRB_DataConstants.READ_LP_READ;

							data = DLMS_Make_Msg( bytCmd );

							nCnt = 0;
							continue;
						}else{
							ba =move_to_lp_data( ba, baLP );
							baLP=new ByteArray();

							if( lp_continue>0 ){
								bytCmd 		= LK1210DRB_DataConstants.LSLOW_READ;
								bytClass 	= LK1210DRB_DataConstants.READ_LP_READ_CONTINUE;
		
								data = DLMS_Make_Msg(bytCmd, bytClass, bFlag, lp_continue );

								nCnt = 0;
								continue;
							}
						}
						break;
					case LK1210DRB_DataConstants.READ_LP_READ_CONTINUE:
						baLP= copy_to_lp_data( baLP, temp, bytClass, true, response_flag);

						if(temp[1] == (byte)0xa8) {
							bytCmd 		= LK1210DRB_DataConstants.LSLOW_RR;
							bytClass 	= LK1210DRB_DataConstants.READ_LP_READ_CONTINUE;

							data = DLMS_Make_Msg( bytCmd );

							nCnt = 0;
							continue;
						}else{
							
							ba = move_to_lp_data( ba, baLP );
							baLP=new ByteArray();

							if( lp_continue>0 ){
								bytCmd 		= LK1210DRB_DataConstants.LSLOW_READ;
								bytClass 	= LK1210DRB_DataConstants.READ_LP_READ_CONTINUE;

								data = DLMS_Make_Msg(bytCmd, bytClass, bFlag, lp_continue );

								nCnt = 0;
								continue;
							}
						}
						break;
					}
					break;
	            case LK1210DRB_DataConstants.LSLOW_READ :
	            	switch( bytClass )
					{
					case LK1210DRB_DataConstants.READ_LP_READ:
						
			    		lp_continue = 0;
						response_flag = temp[13];
						log.debug("response_flag :: "+response_flag);
						if(response_flag == LK1210DRB_DataConstants.GET_RESPONSE_WITH_DATABLOCK) 
							lp_continue++;
						else 
							lp_continue = 0;
			
						baLP = copy_to_lp_data( baLP, temp, LK1210DRB_DataConstants.READ_LP_READ, false, response_flag );
			
						if( temp[1] == (byte)0xa8){
							bytCmd 	= LK1210DRB_DataConstants.LSLOW_RR;
			
							data = DLMS_Make_Msg( bytCmd );
			
							nCnt = 0;
							continue;
						}else if( response_flag == LK1210DRB_DataConstants.GET_RESPONSE_WITH_DATABLOCK ){
			
							ba =move_to_lp_data( ba, baLP );
							baLP=new ByteArray();
			
							bytCmd 		= LK1210DRB_DataConstants.LSLOW_READ;
							bytClass 	= LK1210DRB_DataConstants.READ_LP_READ_CONTINUE;
			
							data = DLMS_Make_Msg( bytCmd, bytClass, bFlag, lp_continue );
			
							nCnt = 0;
							continue;
						}
			
						ba = move_to_lp_data( ba, baLP );
						baLP=new ByteArray();
						break;
					case LK1210DRB_DataConstants.READ_LP_READ_CONTINUE:
						baLP = copy_to_lp_data( baLP, temp, bytClass, false, response_flag );

						if( temp[15]>0) lp_continue = 0;
						else lp_continue++;

						if(temp[1] == (byte)0xa8){
							bytCmd 		= LK1210DRB_DataConstants.LSLOW_RR;

							data = DLMS_Make_Msg(bytCmd);

							nCnt = 0;
							continue;
						}

						ba =move_to_lp_data( ba, baLP );
						baLP=new ByteArray();

						break;
		            }
	            }
            nCnt++;
            log.debug("lp_continue :: "+lp_continue);
            
    		}
    		log.debug("READ LP :: "+new OCTET(ba.toByteArray()).toHexString());
    		 return ba;
        } catch(Exception e){
            log.error("LP read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"LP read error");
        }
    }
    
    public ByteArray billingDataRead(IoSession session, String startday, String endday, int lpCycle) throws MRPException
    {
    	ByteArray ba = new ByteArray();  
        byte[] temp = null;
        LK1210DRB_RequestDataFrame frame = null;
        LK1210DRB_ReceiveDataFrame buf = null;
        int retry=3;
        //snrm
        try
        {
        	byte[] data = DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_SNRM);
        	OCTET send_Data = new OCTET(data);
        //	OCTET send_Data = new OCTET(new byte[]{0x7E, (byte)0xA0, 0x08, 0x02, 0x03, 0x21, (byte)0x93, (byte)0x86, 0x67, 0x7E});
        	
            byte[] message = null;
            int cnt =0;
            while(cnt<retry && message ==null){
            	frame = new LK1210DRB_RequestDataFrame(new BYTE(),
                		send_Data, 0,0, null);
                IoBuffer buf2 = frame.getIoBuffer();
            	 log.debug("send snrm=>"+buf2.getHexDump());
                 session.write(buf2); 
       //          message = (byte[])handler.getMsg(session,20,160,false);
        //         message = (byte[])handler.getMessageLSIS(session,2,MeterModel.LSIS_LK1210DRB_120);
	            message = (byte[])handler.getMessage(session,2,MeterModel.LSIS_LK1210DRB_120.getCode().intValue());
	            cnt++;
            }
           
            if(message != null && message.length > 0){
                log.debug("receive snrm =>"+new OCTET(message).toHexString());
                g_rxd_control	= message[6];
                log.debug("g_rxd_control" +g_rxd_control);
              //  session.write(ackByte());
            }else{
            	throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"error to get auth.");
            	// session.write(nakByte());
            }
        }
        catch (Exception e)
        {
            log.error("snrm error",e);
            throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"snrm meter error");
            
        } 
        
        //ll_auth
        try
        {	
        	byte[] data = DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_LL_AUTH);
        	OCTET send_Data = new OCTET(data);
            
            byte[] message = null;
            int cnt =0;
            while(cnt<retry && message ==null){
            	frame = new LK1210DRB_RequestDataFrame(new BYTE(),
                		send_Data, 0,0, null);
                IoBuffer buf2 = frame.getIoBuffer();
            	log.debug("send ll_auth=>"+buf2.getHexDump());
                session.write(buf2); 
         //       message = (byte[])handler.getMsg(session,20,160,false);
//                message = (byte[])handler.getMessageLSIS(session,2,MeterModel.LSIS_LK1210DRB_120);
                message = (byte[])handler.getMessage(session,2,MeterModel.LSIS_LK1210DRB_120.getCode().intValue());
                cnt++;
            }
            if(message != null && message.length > 0){
                log.debug("receive ll_auth =>"+new OCTET(message).toHexString());
            //    session.write(ackByte());
                g_rxd_control	= message[6];
                log.debug("g_rxd_control= "+g_rxd_control);
            }else{
            	throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"error to get auth.");
            //	 session.write(nakByte());
            }
            
        }
        catch (Exception e)
        {
            log.error("ll_auth error",e);
            throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"error to get auth.");

        } 
        
        //READ_METER_INFO
        try
        {
        	byte[] data = DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_READ, 
            							LK1210DRB_DataConstants.READ_METER_INFO);
        	OCTET send_Data = new OCTET(data);
        	int cnt =0;
            while(cnt<retry && buf ==null){
	            frame = new LK1210DRB_RequestDataFrame(new BYTE(),
	            		send_Data, 0,0, null);
	            buf = read(session,frame, LK1210DRB_DataConstants.LSLOW_READ, LK1210DRB_DataConstants.READ_METER_INFO);        
	            cnt++;
            }
            if(buf==null)
            	return null;

            temp = buf.encode();
            buf=null;
            g_rxd_control	= temp[6];
            log.debug("g_rxd_control= "+g_rxd_control);
	        ba.append(DataUtil.arrayAppend(temp, 25, 1, temp, 0, 0)); //Meter manufacturer number
	        ba.append(DataUtil.arrayAppend(temp, 22, 1, temp, 0, 0)); //Meter Type
	        ba.append(new byte[]{(byte)0x00});
	        ba.append(DataUtil.arrayAppend(temp, 29, 7, temp, 0, 0)); //Meter ID
	        ba.append(DataUtil.arrayAppend(temp, 48, 1, temp, 0, 0)); //Meter State information : Error
	        ba.append(DataUtil.arrayAppend(temp, 51, 1, temp, 0, 0)); //Meter State information : Caution
	        ba.append(DataUtil.arrayAppend(temp, 53, 2, temp, 0, 0)); //Meter Constant
	        ba.append(DataUtil.arrayAppend(temp, 56, 2, temp, 0, 0)); //Meter Overall Transformer ratio : voltage ratio
	        ba.append(DataUtil.arrayAppend(temp, 59, 2, temp, 0, 0)); //Meter Overall Transformer ratio : current ratio
	        ba.append(DataUtil.arrayAppend(temp, 62, 1, temp, 0, 0)); //Meter Periodic self-read day
	        ba.append(DataUtil.arrayAppend(temp, 64, 1, temp, 0, 0)); //Meter Record interval for load profile
	        ba.append(DataUtil.arrayAppend(temp, 67, 6, temp, 0, 0)); //Meter Recording date of the last load profile
	        ba.append(DataUtil.arrayAppend(temp, 76, 9, temp, 0, 0)); //Meter Billing information of the last month
	        ba.append(DataUtil.arrayAppend(temp, 38, 8, temp, 0, 0)); //Meter Current date/time of meter
          
	        log.debug("READ_METER_INFO :: "+new OCTET(ba.toByteArray()).toHexString());
        }
        catch (Exception e)
        {
            log.error("Meter Info Read error",e);
            throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"Meter Info Read error");
            
        } 
        
        byte billingFlag	=	0;
        //READ_Bill_INFO
        try{
        	
	        byte[] data = DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_READ, 
					LK1210DRB_DataConstants.READ_LAST_BILL_INFO);
			OCTET send_Data = new OCTET(data);
			int cnt =0;
			
            while(cnt<retry && buf ==null){
				frame = new LK1210DRB_RequestDataFrame(new BYTE(),
													send_Data, 0,0, null);
	            buf = read(session,frame, LK1210DRB_DataConstants.LSLOW_READ,LK1210DRB_DataConstants.READ_LAST_BILL_INFO);   
	            cnt++;
            }
            if(buf==null)
            	return null;
            temp = buf.encode();
            buf=null;
            g_rxd_control	= temp[6];
            log.debug("g_rxd_control= "+g_rxd_control);
    		billingFlag = (byte)(temp[20] & 0xff);
    		log.debug("billingFlag = "+billingFlag);
    		ba.append(DataUtil.arrayAppend(temp, 20, 1, temp, 0, 0));
    		
    		log.debug("READ_Bill_INFO :: "+new OCTET(ba.toByteArray()).toHexString());
    		
        }catch(Exception e){
            log.error("Billing info read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Billing info read error");
            
        }
        //READ EVENT INFO
        byte countOfPF	=	0;
        try{
        	
	        byte[] data = DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_READ, 
					LK1210DRB_DataConstants.READ_PF_INFO);
			OCTET send_Data = new OCTET(data);
			int cnt =0;
            while(cnt<retry && buf ==null){
				frame = new LK1210DRB_RequestDataFrame(new BYTE(),
													send_Data, 0,0, null);
	            buf = read(session,frame, LK1210DRB_DataConstants.LSLOW_READ,LK1210DRB_DataConstants.READ_PF_INFO);   
	            cnt++;
	        }
            if(buf==null)
            	return null;
            temp = buf.encode();
            buf=null;
            g_rxd_control	= temp[6];
            log.debug("g_rxd_control= "+g_rxd_control);
            countOfPF = (byte)(temp[20] & 0xff);
            log.debug("countOfPF = "+countOfPF);
    		ba.append(DataUtil.arrayAppend(temp, 20, 1, temp, 0, 0));
    		log.debug("READ EVENT INFO :: "+new OCTET(ba.toByteArray()).toHexString());
        }catch(Exception e){
            log.error("pf info read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"pf info read error");
            
        }
        
        //READ CURRENT BILLING
        try{
        	
	        byte[] data = DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_READ, 
					LK1210DRB_DataConstants.READ_CURR_BILL);
			OCTET send_Data = new OCTET(data);
			int cnt =0;
            while(cnt<retry && buf ==null){
				frame = new LK1210DRB_RequestDataFrame(new BYTE(),
													send_Data, 0,0, null);
	            buf = read(session,frame, LK1210DRB_DataConstants.LSLOW_READ,LK1210DRB_DataConstants.READ_CURR_BILL);   
	            cnt++;
            }
            if(buf==null)
            	return null;

            temp = buf.encode();
            buf=null;
            g_rxd_control	= temp[6];
            log.debug("g_rxd_control= "+g_rxd_control);
			for( int i=0; i<3; i++)		//Tariff
			{
	            ba.append(DataUtil.arrayAppend(temp, 21+(i*32), 4, temp, 0, 0));// active energy
	            ba.append(DataUtil.arrayAppend(temp, 26+(i*32), 4, temp, 0, 0));// reactive energy
	            ba.append(DataUtil.arrayAppend(temp, 31+(i*32), 2, temp, 0, 0));// maximum active power
	            ba.append(DataUtil.arrayAppend(temp, 34+(i*32), 4, temp, 0, 0));// cumulative maximum active power
	            ba.append(DataUtil.arrayAppend(temp, 40+(i*32), 4, temp, 0, 0));// maximum active power date
	            ba.append(DataUtil.arrayAppend(temp, 45+(i*32), 3, temp, 0, 0));// maximum active power time
			}    
			
			log.debug("READ CURRENT BILLING :: "+new OCTET(ba.toByteArray()).toHexString());
			
        }catch(Exception e){
            log.error("Curr bill read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Curr Billing data read error");
            
        }
        
        //READ PREV BILLING
        if(billingFlag>0){
            try{
            	
    	        byte[] data = DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_READ, 
    					LK1210DRB_DataConstants.READ_LAST_BILL);
    			OCTET send_Data = new OCTET(data);
    			int cnt =0;
                while(cnt<retry && buf ==null){
	    			frame = new LK1210DRB_RequestDataFrame(new BYTE(),
	    												send_Data, 0,0, null);
	                buf = read(session,frame, LK1210DRB_DataConstants.LSLOW_READ,LK1210DRB_DataConstants.READ_LAST_BILL);   
	                cnt++;
                }
                if(buf==null)
                	return ba;
                
                temp = buf.encode();
                buf=null;
                g_rxd_control	= temp[6];
                log.debug("g_rxd_control= "+g_rxd_control);
    			for( int i=0; i<3; i++)		//Tariff
    			{
    	            ba.append(DataUtil.arrayAppend(temp, 21+(i*32), 4, temp, 0, 0));// active energy
    	            ba.append(DataUtil.arrayAppend(temp, 26+(i*32), 4, temp, 0, 0));// reactive energy
    	            ba.append(DataUtil.arrayAppend(temp, 31+(i*32), 2, temp, 0, 0));// maximum active power
    	            ba.append(DataUtil.arrayAppend(temp, 34+(i*32), 4, temp, 0, 0));// cumulative maximum active power
    	            ba.append(DataUtil.arrayAppend(temp, 40+(i*32), 4, temp, 0, 0));// maximum active power date
    	            ba.append(DataUtil.arrayAppend(temp, 45+(i*32), 3, temp, 0, 0));// maximum active power time
    			}
    			
    			log.debug("READ PREV BILLING :: "+new OCTET(ba.toByteArray()).toHexString());
            } catch(Exception e){
                log.error("Prev bill read error",e);
                throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Prev Billing data read error");
                
            }
        }
        
        //EVENT LOG
        if(countOfPF>0){
        	//Power Failure
        	try{
        		 byte[] data = DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_READ, 
     					LK1210DRB_DataConstants.READ_POWER_FAILURE);
     			OCTET send_Data = new OCTET(data);
     			int cnt =0;
                while(cnt<retry && buf ==null){
	     			frame = new LK1210DRB_RequestDataFrame(new BYTE(),
	     												send_Data, 0,0, null);
		            buf = read(session,frame, LK1210DRB_DataConstants.LSLOW_READ,LK1210DRB_DataConstants.READ_POWER_FAILURE);
		            cnt++;
                }
	            if(buf!=null){
	            	
	            temp = buf.encode();
	            buf=null;
	            g_rxd_control	= temp[6];
	            log.debug("g_rxd_control= "+g_rxd_control);
	            log.debug("g_txd_control= "+g_txd_control);
	            ByteArray powerFail = new ByteArray();
	            powerFail.append(DataUtil.arrayAppend(temp, 18, temp.length-21, temp, 0, 0));
	           // ba.append(DataUtil.arrayAppend(temp, 18, temp.length-21, temp, 0, 0));
	            log.debug("EVENT LOG1 :: "+new OCTET(powerFail.toByteArray()).toHexString());
	            cnt=0;
	            byte bytCmd = LK1210DRB_DataConstants.LSLOW_RR;
	            byte bytClass = LK1210DRB_DataConstants.READ_POWER_FAILURE;
	            while( cnt<retry && buf==null){
	            	if(temp[1] == (byte)0xa8){
		            	data = DLMS_Make_Msg(bytCmd );
		            	send_Data = new OCTET(data);
		            	frame = new LK1210DRB_RequestDataFrame(new BYTE(), 
				        		send_Data, 0,0, null);
						buf = read(session,frame, bytCmd, bytClass);   
						if(buf!=null){
							temp = buf.encode();
							buf=null;
				            g_rxd_control	= temp[6];
				            log.debug("g_rxd_control= "+g_rxd_control);
				            powerFail.append(DataUtil.arrayAppend(temp, 9, temp.length-12, temp, 0, 0));
				          //  break;
						}
						cnt=0;
						continue;
	            	}else{
	            		
	            		ba = plslow_add_to_pf_data(ba, powerFail);
	            	}
	            	cnt++;
				}
	            log.debug("EVENT LOG2 :: "+new OCTET(ba.toByteArray()).toHexString());
	            }else{
	            	log.debug("EVENT LOG2 :: NO DATA");
	            }
	            buf=null;
        	} catch(Exception e){
                log.error("Power Failure read error",e);
                throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Power Failure data read error");
                
            }
        	//Power Recovery
        	try{
       		 	byte[] data = DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_READ, 
    					LK1210DRB_DataConstants.READ_POWER_RESTORE);
    			OCTET send_Data = new OCTET(data);
    			int cnt =0;
                while(cnt<retry && buf ==null){
	    			frame = new LK1210DRB_RequestDataFrame(new BYTE(),
	    												send_Data, 0,0, null);
		            buf = read(session,frame, LK1210DRB_DataConstants.LSLOW_READ,LK1210DRB_DataConstants.READ_POWER_RESTORE);   
	                cnt++;
                }
	            if(buf!=null){
	            cnt=0;
	            temp = buf.encode();
	            buf=null;
	            g_rxd_control	= temp[6];
   				log.debug("g_rxd_control= "+g_rxd_control);
	            ByteArray powerRecover = new ByteArray();
	            powerRecover.append(DataUtil.arrayAppend(temp, 18, temp.length-21, temp, 0, 0));
	            log.debug("Power Recovery1 :: "+new OCTET(ba.toByteArray()).toHexString());
	            while(cnt<retry && buf==null){
	            	if(temp[1] == (byte)0xa8 ){
		            	data = DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_RR );
		            	send_Data = new OCTET(data);
						frame = new LK1210DRB_RequestDataFrame(new BYTE(), 
				        		send_Data, 0,0, null);
						buf = read(session,frame, LK1210DRB_DataConstants.LSLOW_RR,LK1210DRB_DataConstants.READ_POWER_RESTORE);   
						if(buf!=null){
							temp = buf.encode();
							buf=null;
				            g_rxd_control	= temp[6];
				            log.debug("g_rxd_control= "+g_rxd_control);
				            powerRecover.append(DataUtil.arrayAppend(temp, 9, temp.length-12, temp, 0, 0));
						}
						cnt=0;
						continue;
	            	}else{
	            		ba = plslow_add_to_pf_data(ba, powerRecover);
	            	}
	            	cnt++;
				}
	            log.debug("Power Recovery2 :: "+new OCTET(ba.toByteArray()).toHexString());
	            }else{
	            	log.debug("Power Recovery2 :: NO DATA");
	            }
	            buf=null;
        	} catch(Exception e){
               log.error("Power Recovery read error",e);
               throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Power Recovery data read error");
               
            }
        }        
        return ba;
    }
    
    protected void terminate(IoSession session) throws MRPException
    {
    	LK1210DRB_RequestDataFrame frame = null;
        try
        {	
        	byte[] data = DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_DISC);
        	OCTET send_Data = new OCTET(data);
            frame  = new LK1210DRB_RequestDataFrame(new BYTE(),
            		send_Data, 0,0, null);
            IoBuffer buf2 = frame.getIoBuffer();
            log.debug("send terminate=>"+buf2.getHexDump());
            session.write(buf2); 

//            byte[] message = (byte[])handler.getMsg(session,20,160,false);
//            byte[] message = (byte[])handler.getMessageLSIS(session,2,MeterModel.LSIS_LK1210DRB_120);
              byte[] message = (byte[])handler.getMessage(session,2,MeterModel.LSIS_LK1210DRB_120.getCode().intValue());
            if(message != null && message.length > 0){
                log.debug("receive terminate =>"+new OCTET(message).toHexString());
            //    session.write(ackByte());
            }else{
            //	 session.write(nakByte());
            //	throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"terminate error");
            }
        }
        catch (Exception e)
        {
            log.error("terminate error",e);
         //   throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"terminate error");
        }
    }
    
    public IoBuffer ackByte() throws MRPException
    {
	    byte[] data = new byte[10];

	    byte[] header = DLMS_Make_Header(LK1210DRB_DataConstants.ACK, (byte)0x08);	
		System.arraycopy(header, 0, data, 0, 9);
		data[9] 	= LK1210DRB_DataConstants.HDLC_FLAG;
		
		OCTET send_Data = new OCTET(data);
		LK1210DRB_RequestDataFrame frame = null;
		IoBuffer buf2 =null;
        try
        {	
			frame = new LK1210DRB_RequestDataFrame(new BYTE(),
	        		send_Data, 0,0, null);
	        buf2 = frame.getIoBuffer();
	        log.debug("ack byte=>"+buf2.getHexDump());
        }
        catch (Exception e)
        {
            log.error("ack error",e);
            throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"ack error");
        }
        
	    return buf2;
    }
    
    public IoBuffer nakByte() throws MRPException
    {
	    byte[] data = new byte[10];

	    byte[] header = DLMS_Make_Header(LK1210DRB_DataConstants.NACK, (byte)0x08);	
		System.arraycopy(header, 0, data, 0, 9);
		data[9] 	= LK1210DRB_DataConstants.HDLC_FLAG;
		
		OCTET send_Data = new OCTET(data);
		LK1210DRB_RequestDataFrame frame = null;
		IoBuffer buf2 =null;
        try
        {	
			frame = new LK1210DRB_RequestDataFrame(new BYTE(),
	        		send_Data, 0,0, null);
	        buf2 = frame.getIoBuffer();
	        log.debug("nack byte=>"+buf2.getHexDump());
        }
        catch (Exception e)
        {
            log.error("nack error",e);
            throw new MRPException(MRPError.ERR_CONNECTTOMETER_CLASS,"nack error");
        }
        
	    return buf2;
    }
    
    public ByteArray instRead(IoSession session) throws MRPException
    {
    	return null;
    }

    public ByteArray lpRead(IoSession session, String startday, String endday, int lpCycle) throws MRPException
    {
    	return null;
    }
    
    public byte[] DLMS_Make_Header( byte ctl_data, byte len_data)
    {
    	char fcs_data;
    	byte[] baTxMsg = new byte[9];
    	baTxMsg[0] 	= LK1210DRB_DataConstants.HDLC_FLAG;
    	baTxMsg[1] 	= LK1210DRB_DataConstants.HDLC_FRAME_FORMAT;
    	baTxMsg[2] 	= len_data;										//length
    	baTxMsg[3] 	= (LK1210DRB_DataConstants.MSAP_DEST_ADDRESS >> 8);				//MAC Sub-Layer Service Access Point (Dest Upper)
    	baTxMsg[4] 	= (byte)LK1210DRB_DataConstants.MSAP_DEST_ADDRESS;						//MAC Sub-Layer Service Access Point (Dest Lower)		
    	baTxMsg[5] 	= LK1210DRB_DataConstants.MSAP_SRC_ADDRESS;								//MAC Sub-Layer Service Access Point (Source)						
    	baTxMsg[6] 	= ctl_data;										//control

    	fcs_data	= LK1210DRB_DataConstants.DLMS_Fcs16((char)0xffff, baTxMsg, 1, 6);
    	fcs_data   ^= 0xffff;
    	baTxMsg[7] 	= (byte)(fcs_data & 0x00ff);								// HCS (LSB)
    	baTxMsg[8] 	= (byte)((fcs_data >> 8) & 0x00ff);							// HCS (MSB)
    	
    	return baTxMsg;
    }
    
    public byte[] DLMS_Make_Msg( byte bytCmd )
    {
    	int		i;
    	char 	fcs_data;
    	byte 	control;

    	byte[] baTxBuf =null;
    	byte[] header = null;
    	switch( bytCmd )
    	{
    	case LK1210DRB_DataConstants.LSLOW_SNRM:
    		baTxBuf =new byte[10];

    		header = DLMS_Make_Header(LK1210DRB_DataConstants.HDLC_CTRL_SNRM, (byte)0x08);	
    		System.arraycopy(header, 0, baTxBuf, 0, 9);
    		baTxBuf[9] 	= LK1210DRB_DataConstants.HDLC_FLAG;
    		break;
    	case LK1210DRB_DataConstants.LSLOW_LL_AUTH:
    		baTxBuf =new byte[46];
    		header = DLMS_Make_Header(LK1210DRB_DataConstants.HDLC_CTRL_I, (byte)0x2c);
    		System.arraycopy(header, 0, baTxBuf, 0, 9);
    		
    		baTxBuf[9] 		= LK1210DRB_DataConstants.LSAP_DEST_ADDRESS;		//Destination Address LSAP
    		baTxBuf[10] 	= LK1210DRB_DataConstants.LSAP_SRC_ADDRESS;			//Source Address LSAP

    		for(i=0 ; i<32 ; i++) baTxBuf[11 + i] = LK1210DRB_DataConstants.DLMS_ll_auth[i];

    		fcs_data	= LK1210DRB_DataConstants.DLMS_Fcs16((char)0xffff, baTxBuf, 1, 6);
        	fcs_data   ^= 0xffff;
    		baTxBuf[43] = (byte)(fcs_data & 0x00ff);					// FCS (LSB)
    		baTxBuf[44] = (byte)((fcs_data >> 8) & 0x00ff);			// FCS (MSB)						
    		baTxBuf[45] = LK1210DRB_DataConstants.HDLC_FLAG;
    		break;
    	case LK1210DRB_DataConstants.LSLOW_DISC:
    		baTxBuf =new byte[10];

    		header = DLMS_Make_Header(LK1210DRB_DataConstants.HDLC_CTRL_DISC, (byte)0x08);
    		System.arraycopy(header, 0, baTxBuf, 0, 9);
    		baTxBuf[9] = LK1210DRB_DataConstants.HDLC_FLAG;

    		break;
    	case LK1210DRB_DataConstants.LSLOW_RR:
    		baTxBuf =new byte[10];

    		log.debug("g_txd_control= "+g_txd_control);
    		log.debug("g_rxd_control= "+g_rxd_control);
    		control =  (byte)(g_txd_control & (byte)0xf0);
    		log.debug("control= "+control);
    		control += (byte)0x21;
    		log.debug("control= "+control);
    		if((g_rxd_control & 0x0e) == 0x0e) control = LK1210DRB_DataConstants.HDLC_CTRL_RR;

    		g_txd_control = control;
    		log.debug("g_txd_control= "+g_txd_control);
    		log.debug("g_rxd_control= "+g_rxd_control);
    		log.debug("control= "+control);
    		header =DLMS_Make_Header(control, (byte)0x08);
    		System.arraycopy(header, 0, baTxBuf, 0, 9);
    		baTxBuf[9] = LK1210DRB_DataConstants.HDLC_FLAG;

    		break;
    	}
    	return baTxBuf;
    }
    
//  Basic Read
    public byte[] DLMS_Make_Msg(byte bytCmd, byte bytClass)
    {
    	char 	fcs_data;
    	byte 	control;
    	byte[] data = new byte[28];
    	switch( bytCmd )
    	{
    	case LK1210DRB_DataConstants.LSLOW_READ:

    		 control = Make_Hdlc_Control( bytCmd );
    		System.arraycopy(DLMS_Make_Header(control, (byte)0x1A),0,data,0,9);
    		
    		int idx =9;
            data[idx++] = LK1210DRB_DataConstants.LSAP_DEST_ADDRESS; //Destination Address LSAP	
            data[idx++] = LK1210DRB_DataConstants.LSAP_SRC_ADDRESS;  //Source Address LSAP
            data[idx++] = (byte)0x00;	 //LLC Quality
            data[idx++] = (byte)0xc0;							//GET.requeset
            data[idx++] = (byte)0x01; 						//GET.requeset.normal
            data[idx++] = (byte)0x81; 						////Invoke-id and priority
            
            System.arraycopy(LK1210DRB_DataConstants.DLMS_Read[bytClass], 0, 
            				data, idx, LK1210DRB_DataConstants.DLMS_Read[LK1210DRB_DataConstants.LSLOW_READ].length);
            idx+= LK1210DRB_DataConstants.DLMS_Read[bytClass].length;
            data[idx++] = (byte)0x00; 		 //data_index
            
            fcs_data	= LK1210DRB_DataConstants.DLMS_Fcs16((char)0xffff, data, 1, 24);
        	fcs_data   ^= 0xffff;
        	data[idx++] 	= (byte)(fcs_data & 0x00ff);								// HCS (LSB)
        	data[idx++] 	= (byte)((fcs_data >> 8) & 0x00ff);							// HCS (MSB)
        	data[idx++] = LK1210DRB_DataConstants.HDLC_FLAG;

    		break; 	
    	
    	}
    	return data;
    }
    
//  LP Read
    public byte[] DLMS_Make_Msg(byte bytCmd, byte bytClass, boolean bFlag, int from_lp, int to_lp )
    {
    	char 	fcs_data;
    	byte 	control;
    	byte[] baTxBuf = new byte[47];
    	switch( bytCmd )
    	{
    	case LK1210DRB_DataConstants.LSLOW_READ:	
    		if(bFlag) control = Make_Hdlc_Control( bytCmd );
    		else control = g_txd_control;
    		log.debug("g_txd_control= "+g_txd_control);
    		System.arraycopy(DLMS_Make_Header(control, (byte)0x2D), 0, baTxBuf, 0, 8);
    		int idx =9;
    		baTxBuf[idx++] = LK1210DRB_DataConstants.LSAP_DEST_ADDRESS;			//Destination Address LSAP		
    		baTxBuf[idx++] = LK1210DRB_DataConstants.LSAP_SRC_ADDRESS;				//Source Address LSAP
    		baTxBuf[idx++] = (byte)0x00;							//LLC Quality

    		baTxBuf[idx++] = (byte)0xc0;							//GET.requeset

    		baTxBuf[idx++] = (byte)0x01; 					//GET.requeset.normal
    		baTxBuf[idx++] = (byte)0x81;					//Invoke-id and priority

    		for(int i=0 ; i<9 ; i++) 
    			baTxBuf[idx+i] =  LK1210DRB_DataConstants.DLMS_Read[bytClass][i];

    		idx +=9;
    		
    		baTxBuf[idx++] = (byte)0x01 ;					//data index
    		baTxBuf[idx++] = (byte)0x02 ;					//data index count
    		baTxBuf[idx++] = (byte)0x02 ;					//item index
    		baTxBuf[idx++] = (byte)0x04 ;					//item index count

    		baTxBuf[idx++] = (byte)0x06 ;					//first entry to retrive (double-long-unsigned)
    		baTxBuf[idx++] = (byte)(from_lp >> 24) ;
    		baTxBuf[idx++] = (byte)(from_lp >> 16) ;
    		baTxBuf[idx++] = (byte)(from_lp >>  8) ;
    		baTxBuf[idx++] = (byte)(from_lp) ;

    		baTxBuf[idx++] = (byte)0x06 ;					//end entry to retrive (double-long-unsigned)
    		baTxBuf[idx++] = (byte)(to_lp >> 24) ;
    		baTxBuf[idx++] = (byte)(to_lp >> 16) ;
    		baTxBuf[idx++] = (byte)(to_lp >>  8) ;
    		baTxBuf[idx++] = (byte)(to_lp) ;

    		baTxBuf[idx++] = (byte)0x12 ;					//index of first value to retrive (long-unsigned)
    		baTxBuf[idx++] = (byte)0x00 ;
    		baTxBuf[idx++] = (byte)0x01 ;

    		baTxBuf[idx++] = (byte)0x12 ;					//index of last value to retrive (long-unsigned)
    		baTxBuf[idx++] = (byte)0x00 ;
    		baTxBuf[idx++] = (byte)0x06 ;

    		fcs_data 	= LK1210DRB_DataConstants.DLMS_Fcs16((char)0xffff, baTxBuf, 1, 43);
    		fcs_data   ^= 0xffff;
    		baTxBuf[idx++] = (byte) (fcs_data & 0x00ff);			// FCS (LSB)
    		baTxBuf[idx++] = (byte) ((fcs_data >> 8) & 0x00ff);	// FCS (MSB)	
    		baTxBuf[idx++] = (byte) LK1210DRB_DataConstants.HDLC_FLAG;
    		break;
    	}
    	return baTxBuf;
    }
    
//  LP Continue Read
    public byte[] DLMS_Make_Msg(byte bytCmd, byte bytClass, boolean bFlag, byte lp_continue )
    {
    	char 	fcs_data;
    	byte 	control;
    	byte[] baTxBuf = new byte[22];
    	switch( bytCmd )
    	{
    	case LK1210DRB_DataConstants.LSLOW_READ:	

    		if(bFlag) control = Make_Hdlc_Control( bytCmd );
    		else control = g_txd_control;
    		log.debug("g_txd_control= "+g_txd_control);
    		System.arraycopy(DLMS_Make_Header(control, (byte) 0x14), 0, baTxBuf, 0, 8);
    		int idx =9;
    		baTxBuf[idx++] = LK1210DRB_DataConstants.LSAP_DEST_ADDRESS;			//Destination Address LSAP		
    		baTxBuf[idx++] = LK1210DRB_DataConstants.LSAP_SRC_ADDRESS;				//Source Address LSAP
    		baTxBuf[idx++] = (byte)0x00;							//LLC Quality

    		baTxBuf[idx++] = (byte)0xc0;							//GET.requeset

    		baTxBuf[idx++] = (byte)0x02;					//GET.requeset.DataBlock
    		baTxBuf[idx++] = (byte)0x81;					//Invoke-id and priority

    		baTxBuf[idx++] = (byte)0x00;
    		baTxBuf[idx++] = (byte)0x00;
    		baTxBuf[idx++] = (byte)0x00;
    		baTxBuf[idx++] = (byte)lp_continue;			//Data block Count

    		fcs_data 	= LK1210DRB_DataConstants.DLMS_Fcs16((char)0xffff, baTxBuf, 1, 18);
    		fcs_data   ^= 0xffff;
    		baTxBuf[idx++] = (byte) (fcs_data & 0x00ff);			// FCS (LSB)
    		baTxBuf[idx++] = (byte) ((fcs_data >> 8) & 0x00ff);	// FCS (MSB)	
    		baTxBuf[idx++] = (byte) LK1210DRB_DataConstants.HDLC_FLAG;

    		break;
    	}
    	return baTxBuf;
    }
    
    public ByteArray copy_to_lp_data(ByteArray baData1, byte[] baData2, byte bytClass, boolean rr_flag, byte response_flag )
    {
    	log.debug("copy_to_lp_data ");
    	log.debug("baData2.length() : "+baData2.length);
		log.debug("baData1.length() : "+baData1.toByteArray().length);
    	
		if(rr_flag) {
    		baData1.append(DataUtil.arrayAppend(baData2, 9, baData2.length-12 , baData2, 0, 0));
    	}
    	else 
    	{
    		if( bytClass == LK1210DRB_DataConstants.READ_LP_READ ){
    			switch( response_flag ){
    			case LK1210DRB_DataConstants.GET_RESPONSE_NORMAL:
    				baData1.append(DataUtil.arrayAppend(baData2, 20, baData2.length - 23 , baData2, 0, 0));
    				break;
    			case LK1210DRB_DataConstants.GET_RESPONSE_WITH_DATABLOCK:
    				baData1.append(DataUtil.arrayAppend(baData2, 28, baData2.length - 31 , baData2, 0, 0));
    				break;
    			}
    		}else{
    			if(baData2.length>27)
    			baData1.append(DataUtil.arrayAppend(baData2, 24, baData2.length - 27 , baData2, 0, 0));
    		}
    	}
   // 	log.debug("copy_to_lp_data :: "+new OCTET(baData1.toByteArray()).toHexString());
    	return baData1;
    }

    public ByteArray move_to_lp_data( ByteArray baData1, ByteArray  baData2 )
    {
    	log.debug("move_to_lp_data ");
    	byte		nCnt;
    	byte[] baLPArray = baData2.toByteArray();
    	nCnt	=  (byte)(baLPArray.length/31);

    	for( int i=0; i < nCnt; i++ ){
    		baData1.append(DataUtil.arrayAppend(baLPArray,(i*31)+ 3, 2, baLPArray, 0, 0));//Active Energy
    		baData1.append(DataUtil.arrayAppend(baLPArray,(i*31)+ 6, 2, baLPArray, 0, 0));//Reactive Energy
    		baData1.append(DataUtil.arrayAppend(baLPArray,(i*31)+16, 4, baLPArray, 0, 0));//LP Date
    		baData1.append(DataUtil.arrayAppend(baLPArray,(i*31)+21, 3, baLPArray, 0, 0));//LP Time
    		baData1.append(DataUtil.arrayAppend(baLPArray,(i*31)+30, 1, baLPArray, 0, 0));//Error Status
    	}
    //	log.debug("move_to_lp_data :: "+new OCTET(baData1.toByteArray()).toHexString());
    	return baData1;
    }
    
    public ByteArray billInfo(IoSession session) throws MRPException
    {
        log.debug("========== Prev Bill Info Start ===============");

    	ByteArray ba = new ByteArray();  
        byte[] temp = null;
        LK1210DRB_RequestDataFrame frame = null;
        LK1210DRB_ReceiveDataFrame buf = null;
        
    	BYTE length = new BYTE((byte) 0x1A);
   //     byte[] data = new byte[19];
        OCTET send_Data = new OCTET(DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_READ, 
        		LK1210DRB_DataConstants.READ_LAST_BILL_INFO));
        BYTE control = new BYTE(Make_Hdlc_Control( LK1210DRB_DataConstants.LSLOW_READ ));
         
   //     frame = new LK1210DRB_RequestDataFrame(new BYTE(), control, length,
   //     		send_Data, 0,0, null);
        try{
            buf = read(session,frame, LK1210DRB_DataConstants.LSLOW_READ,LK1210DRB_DataConstants.READ_LAST_BILL_INFO);   
            if(buf==null)
            	return null;
            temp = buf.encode();
            g_rxd_control	= temp[6];
            log.debug("g_rxd_control= "+g_rxd_control);
    		int nFlag	=	1;
    		nFlag = temp[20];
    		
    		 ba.append(DataUtil.arrayAppend(temp, 17, 1, temp, 0, 0));
    		
        }catch(Exception e){
            log.error("prev bill read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Billing data read error");
        }
        log.debug("========== Prev Bill Info End ===============");
        return ba;
    }
    
	/**
	 * Read Cumulative Data.<p>
	 * @return
	 * @throws MeterException
	 */
	public ByteArray currbillRead(IoSession session) throws MRPException
	{

        log.debug("========== Curr Bill Read Start ===============");

    	ByteArray ba = new ByteArray();  
        byte[] temp = null;
        LK1210DRB_RequestDataFrame frame = null;
        LK1210DRB_ReceiveDataFrame buf = null;
        
    	BYTE length = new BYTE((byte) 0x1A);
   //     byte[] data = new byte[19];
        OCTET send_Data = new OCTET(DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_READ, 
        		LK1210DRB_DataConstants.READ_CURR_BILL));
        BYTE control = new BYTE(Make_Hdlc_Control( LK1210DRB_DataConstants.LSLOW_READ ));
         
    //    frame = new LK1210DRB_RequestDataFrame(new BYTE(), control, length,
     //   		send_Data, 0,0, null);
        try{
            buf = read(session,frame, LK1210DRB_DataConstants.LSLOW_READ,LK1210DRB_DataConstants.READ_CURR_BILL);   
            if(buf==null)
            	return null;
            temp = buf.encode();
            g_rxd_control	= temp[6];
            log.debug("g_rxd_control= "+g_rxd_control);
			for( int i=0; i<3; i++)		//Tariff
			{
	            ba.append(DataUtil.arrayAppend(temp, 21+(i*32), 4, temp, 0, 0));// active energy
	            ba.append(DataUtil.arrayAppend(temp, 26+(i*32), 4, temp, 0, 0));// reactive energy
	            ba.append(DataUtil.arrayAppend(temp, 31+(i*32), 2, temp, 0, 0));// maximum active power
	            ba.append(DataUtil.arrayAppend(temp, 34+(i*32), 4, temp, 0, 0));// cumulative maximum active power
	            ba.append(DataUtil.arrayAppend(temp, 40+(i*32), 4, temp, 0, 0));// maximum active power date
	            ba.append(DataUtil.arrayAppend(temp, 45+(i*32), 3, temp, 0, 0));// maximum active power time
			}
        }catch(Exception e){
            log.error("Curr bill read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Billing data read error");
        }
        log.debug("========== Curr Bill Read End ===============");
        return ba;
    }
	
    public ByteArray billRead(IoSession session) throws MRPException
    {
        log.debug("========== Prev Bill Read Start ===============");

    	ByteArray ba = new ByteArray();  
        byte[] temp = null;
        LK1210DRB_RequestDataFrame frame = null;
        LK1210DRB_ReceiveDataFrame buf = null;
        
    	BYTE length = new BYTE((byte) 0x1A);
   //     byte[] data = new byte[19];
        OCTET send_Data = new OCTET(DLMS_Make_Msg(LK1210DRB_DataConstants.LSLOW_READ, 
        		LK1210DRB_DataConstants.READ_LAST_BILL));
        BYTE control = new BYTE(Make_Hdlc_Control( LK1210DRB_DataConstants.LSLOW_READ ));
         
    //    frame = new LK1210DRB_RequestDataFrame(new BYTE(), control, length,
    //    		send_Data, 0,0, null);
        try{
            buf = read(session,frame, LK1210DRB_DataConstants.LSLOW_READ,LK1210DRB_DataConstants.READ_LAST_BILL);   
            if(buf==null)
            	return null;
            temp = buf.encode();
            g_rxd_control	= temp[6];
            log.debug("g_rxd_control= "+g_rxd_control);
			for( int i=0; i<3; i++)		//Tariff
			{
	            ba.append(DataUtil.arrayAppend(temp, 21+(i*32), 4, temp, 0, 0));// active energy
	            ba.append(DataUtil.arrayAppend(temp, 26+(i*32), 4, temp, 0, 0));// reactive energy
	            ba.append(DataUtil.arrayAppend(temp, 31+(i*32), 2, temp, 0, 0));// maximum active power
	            ba.append(DataUtil.arrayAppend(temp, 34+(i*32), 4, temp, 0, 0));// cumulative maximum active power
	            ba.append(DataUtil.arrayAppend(temp, 40+(i*32), 4, temp, 0, 0));// maximum active power date
	            ba.append(DataUtil.arrayAppend(temp, 45+(i*32), 3, temp, 0, 0));// maximum active power time
			}
        }catch(Exception e){
            log.error("prev bill read error",e);
            throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Billing data read error");
        }
        log.debug("========== Prev Bill Read End ===============");
        return ba;
    }
    
    public byte Make_Hdlc_Control( byte bytCmd )
    {
    	byte	control;
    	byte	rrr;
    	byte	sss;

    	sss = (byte)(((g_rxd_control & (byte)0xe0)& 0xff) >> 4);
    	rrr = (byte)(((g_rxd_control & (byte)0x0e)& 0xff) << 4);

    	if( bytCmd == LK1210DRB_DataConstants.LSLOW_RR ){
    		sss = 0x00; 
    		control =(byte)(( rrr + (byte)0x20 ) + (byte)0x10 + sss + 1);
    	}else{
    		control =(byte)(( rrr + (byte)0x20 ) + (byte)0x10 + sss);
    	}

    	g_txd_control = (byte)control;
    	
    	log.debug("Make_Hdlc_Control:: g_txd_control ="+g_txd_control);
    	return control;
    }

//  void plslow_add_to_pf_data(void)
    public ByteArray plslow_add_to_pf_data(ByteArray ba, ByteArray eventBa)	//MD0412
    {
    	int	pf_cnt=0;
    	byte[] temp = eventBa.toByteArray();
//    	pf_cnt	= meter_pf_cnt;	//MD0412
    	pf_cnt	= temp.length / 19;

    /*  MD0412 End		*/
    	for( int i=0; i < pf_cnt; i++ ){
    		ba.append(DataUtil.arrayAppend(temp, 4+(i*19), 4, temp, 0, 0));
    		ba.append(DataUtil.arrayAppend(temp, 9+(i*19), 3, temp, 0, 0));
    	}
    	return ba;	//MD0412
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
		if(len != 6)
		throw new Exception("YYYYMMDDHHMMSS LEN ERROR : "+len);

		int idx = 0;
		short year = (byte) (b[idx++] << 8);
		year += b[idx++];

		int mm = b[idx++];
		int dd = b[idx++];
		int hh = b[idx++];
		int MM = b[idx++];
		int ss = 0;
		
		StringBuffer ret = new StringBuffer();
		
		ret.append(frontAppendNStr('0',Integer.toString(year),4));
		ret.append(frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(frontAppendNStr('0',Integer.toString(dd),2));
		ret.append(frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(frontAppendNStr('0',Integer.toString(MM),2));
		ret.append(frontAppendNStr('0',Integer.toString(ss),2));
		
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
