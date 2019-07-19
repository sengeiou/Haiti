package com.aimir.fep.protocol.mrp.protocol;


import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.mrp.command.frame.ReceiveDataFrame;
import com.aimir.fep.util.ByteArray;

/**
 * RequestDataFrame
 * 
 * @author Kang, Soyi
 */
public class LGRW3410_ReceiveDataFrame extends ReceiveDataFrame
{
    private static Log log = LogFactory.getLog(LGRW3410_ReceiveDataFrame.class);
    public int cnt;
    private ArrayList<byte[]> list = new ArrayList<byte[]>();

    /**
     * constructor
     */
    public LGRW3410_ReceiveDataFrame()
    {
    }
    

    public void append(byte[] b)
    {
        log.debug("append =>"+new OCTET(b).toHexString());
        list.add(b);
        this.cnt++;
    }

    public byte[] encode() throws Exception
    {
        ByteArray array = new ByteArray();
        Iterator<byte[]> it = list.iterator();
        int idx = 10;
        while(it.hasNext()){
        	log.debug("idx ="+idx);
            byte[] temp = (byte[])it.next();
            array.append(cutHeaderTail(temp)); 
            idx++;
        }
        return array.toByteArray();
    }
    
    protected byte[] cutHeaderTail(byte[] org)
    {
        log.debug("org =>"+new OCTET(org).toHexString());
        byte[] ret = null;
        int lenth = org.length-12;
        ret = new byte[lenth];
        System.arraycopy(org, 10, ret, 0, lenth);
    
        if(ret!=null)
        	log.debug("org =>"+new OCTET(ret).toHexString());
        return ret;
    }
    
    /**
     * decode
     *
     * @param bytebuffer <code>ByteBuffer</code> bytebuffer
     * @return frame <code>GeneralDataFrame</code> frame
     */
    public static LGRW3410_ReceiveDataFrame decode(byte[] b) 
        throws Exception
    {
    	LGRW3410_ReceiveDataFrame frame = null;
        return frame;
    }

    /**
     * get string
     */
    public String toString()
    {
        StringBuffer strbuf = new StringBuffer();
        try
        {        
            strbuf.append("CLASS["+this.getClass().getName()+"]\n");
            strbuf.append("service : " + new OCTET(encode()).toHexString() + "\n");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return strbuf.toString();
    }
}
