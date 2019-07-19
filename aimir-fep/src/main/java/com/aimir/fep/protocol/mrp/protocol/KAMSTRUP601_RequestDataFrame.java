package com.aimir.fep.protocol.mrp.protocol;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.mrp.command.frame.RequestDataFrame;
import com.aimir.fep.util.Hex;


/**
 * RequestDataFrame
 * 
 * @author Kang, Soyi
 */
public class KAMSTRUP601_RequestDataFrame extends RequestDataFrame
{
    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(KAMSTRUP601_RequestDataFrame.class);
    public BYTE service = new BYTE();
    public BYTE addr = new BYTE();
    public BYTE command = new BYTE();
    public OCTET send_data_buffer = null;
    public int send_count;
    public int retry_flag;
    public TIMESTAMP time_of_transmission = new TIMESTAMP();
    
    private int length;

    /**
     * constructor
     */
    public KAMSTRUP601_RequestDataFrame()
    {
    }
    
    public KAMSTRUP601_RequestDataFrame(BYTE service, BYTE addr, BYTE command,
                            OCTET send_data_buffer, int send_count,
                            int retry_flag, TIMESTAMP time_of_transmission){
        this.service = service;
        this.addr = addr;
        this.command = command;
        this.send_data_buffer = send_data_buffer;
        this.send_count = send_count;
        this.retry_flag = retry_flag;
        this.time_of_transmission = time_of_transmission;
    }

    /**
     * get code
     *
     * @result code <code>byte</code> code
     */
    public BYTE getService()
    {
        return this.service;
    }

    /**
     * set code
     *
     * @param code <code>byte</code> code
     */
    public void setService(BYTE service)
    {
        this.service = service;
    }

    /**
     * get arg
     *
     * @result arg <code>OCTET</code> arg
     */
    public OCTET getDestination()
    {
        return this.destination;
    }

    /**
     * set arg
     *
     * @param arg <code>OCTET</code> arg
     */
    public void setDestination(OCTET destination)
    {
        this.destination = destination;
    }

    /**
     * decode
     *
     * @param bytebuffer <code>ByteBuffer</code> bytebuffer
     * @return frame <code>GeneralDataFrame</code> frame
     */
    public static KAMSTRUP601_RequestDataFrame decode(IoBuffer bytebuffer) 
        throws Exception
    {
    	KAMSTRUP601_RequestDataFrame frame = null;
        return frame;
    }
    
    public byte[] encode() throws Exception
    {
        byte commandValue = (byte)command.getValue();
        byte addrValue = (byte)addr.getValue();
        byte[] data = send_data_buffer.getValue();
        
        int dataLength = 2; //CRC
        
     //   if(addrValue!=null )
        	dataLength += 1; //addr
        
    //    if(commandValue!=null)
        	dataLength += 1; //COMMAND
                
        if(data!=null && data.length>0)
        	dataLength += data.length; //DATA
        
        char crc = 0;
        byte[] buf = null;
        if(data!=null && data.length>0){
        	buf = new byte[(dataLength+2)*2];	
        }

        /*
        * Add the STX and start the CRC calc.
        */
        int idx =0;
        buf[idx++]= KAMSTRUP601_DataConstants.STX_TO_METER;
    //    crc = KAMSTRUP601_DataConstants.CalculateCharacterCRC16(crc,buf[0]);
        
        /*
         * Send the addr data, computing CRC as we go.
         */
         byte[] dled = send_byte(addrValue);
         buf[idx++]= dled[0];
         if(dled.length>1){
         	buf[idx++]= dled[1];
         }
         crc = KAMSTRUP601_DataConstants.CalculateCharacterCRC16(crc,addrValue);

         /*
          * Send the command data, computing CRC as we go.
          */
          dled = send_byte(commandValue);
          buf[idx++]= dled[0];
          if(dled.length>1){
          	buf[idx++]= dled[1];
          }
          crc = KAMSTRUP601_DataConstants.CalculateCharacterCRC16(crc,commandValue);

        for (int i=0; i<data.length; i++) {
	        dled = send_byte(data[i]);
	        buf[idx++]= dled[0];
	        if(dled.length>1){
	        	buf[idx++]= dled[1];
	        }
	        crc = KAMSTRUP601_DataConstants.CalculateCharacterCRC16(crc,data[i]);
        }
        
        /*
        * Add the CRC
        */
        byte[] dleCRC = send_byte((byte)(crc>>8));
        buf[idx++]= dleCRC[0];
        if(dleCRC.length>1){
        	buf[idx++]= dleCRC[1];
        }
        
        dleCRC = send_byte((byte)crc);
        buf[idx++]= dleCRC[0];
        if(dleCRC.length>1){
        	buf[idx++]= dleCRC[1];
        }
        
        byte[] dataFinal = new byte[idx+1];
        System.arraycopy(buf, 0, dataFinal, 0, idx);
        
        /*
        * Add the ETX
        */
        dataFinal[idx] = KAMSTRUP601_DataConstants.ETX;
       
    //    log.debug("dataFinal ::: "+Hex.decode(dataFinal));
        
        return dataFinal;
    }

    /*
    * DLE stuff a single byte
    */
    public byte[] send_byte(byte d)
    {
    	byte[] b = null;
	    switch(d) {
		    case KAMSTRUP601_DataConstants.STX_FROM_METER:
		    case KAMSTRUP601_DataConstants.STX_TO_METER:
		    case KAMSTRUP601_DataConstants.ETX:
		    case KAMSTRUP601_DataConstants.ACK:
		    case KAMSTRUP601_DataConstants.DLE:
		    	b = new byte[]{KAMSTRUP601_DataConstants.DLE, (byte)(~d)};
		    break;
		    default:
		    	b = new byte[]{d};
	    }
	    return b ;
    }
    
    public IoBuffer getIoBuffer() throws Exception
    {
        byte[] b = encode();
        IoBuffer buf = IoBuffer.allocate(b.length);
        buf.put(b);
        buf.flip();
        return buf;
    }
    
    /**
     * get string
     */
    public String toString()
    {
        StringBuffer strbuf = new StringBuffer();
        strbuf.append(super.toString());
        strbuf.append("CLASS["+this.getClass().getName()+"]\n");
        strbuf.append("service : " + service + "\n");
        strbuf.append("command: " + command + "\n");
        strbuf.append("send_data_buffer: " + Hex.decode(send_data_buffer.getValue()) + "\n");
        strbuf.append("send_count : " + send_count + "\n");
        strbuf.append("retry_flag : " + retry_flag + "\n");
        strbuf.append("time_of_transmission : " + time_of_transmission + "\n");

        return strbuf.toString();
    }
}
