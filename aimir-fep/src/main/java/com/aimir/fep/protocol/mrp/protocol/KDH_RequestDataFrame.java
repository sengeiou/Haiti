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
public class KDH_RequestDataFrame extends RequestDataFrame
{
 //   @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(KDH_RequestDataFrame.class);

    public OCTET send_data_buffer = null;
    public int send_count;
    public int retry_flag;
    public TIMESTAMP time_of_transmission = new TIMESTAMP();
    
    private int length;

    /**
     * constructor
     */
    public KDH_RequestDataFrame()
    {
    }
    
    public KDH_RequestDataFrame(BYTE service,
                            OCTET send_data_buffer, int send_count,
                            int retry_flag, TIMESTAMP time_of_transmission){
      
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
    
    public void setControl(BYTE control)
    {
        this.control = control;
    }

    /**
     * decode
     *
     * @param bytebuffer <code>IoBuffer</code> bytebuffer
     * @return frame <code>GeneralDataFrame</code> frame
     */
    public static KDH_RequestDataFrame decode(IoBuffer bytebuffer) 
        throws Exception
    {
    	KDH_RequestDataFrame frame = null;
        return frame;
    }
    
    public byte[] encode() throws Exception
    {
    	int dateLenth = send_data_buffer.getValue().length;
      //  log.debug("RequestDataFrame/"+toString());
        this.length = 1 //start
                    + dateLenth //DATA
                    +1 //check sum
                    +1; //stop

        byte[] buf = new byte[this.length];
        
        if(dateLenth>2)
        	buf[0] = (byte) KDH_DataConstants.SOH_LONG;
        else
        	buf[0] = (byte) KDH_DataConstants.SOH_SHORT;
        
        if(send_data_buffer != null && send_data_buffer.getValue().length > 0){
            System.arraycopy(send_data_buffer.getValue(), 0, buf, 1, dateLenth);
        }
        //It needs to edit. How to decide checksum..
        buf[length-2] = KDH_DataConstants.checkSum(buf,0,length-2);
        if(dateLenth>2)
        	buf[length-1] = (byte) KDH_DataConstants.SOH_LONG;
        else
        	buf[length-1] = (byte) KDH_DataConstants.EOH_SHORT;
        
        return buf;
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
        strbuf.append("send_data_buffer: " + Hex.decode(send_data_buffer.getValue()) + "\n");
        strbuf.append("send_count : " + send_count + "\n");
        strbuf.append("retry_flag : " + retry_flag + "\n");
        strbuf.append("time_of_transmission : " + time_of_transmission + "\n");

        return strbuf.toString();
    }
}
