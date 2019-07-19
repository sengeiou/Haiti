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
public class KDH_ReceiveDataFrame extends ReceiveDataFrame
{
    private static Log log = LogFactory.getLog(KDH_ReceiveDataFrame.class);
    @SuppressWarnings("unused")
    public int cnt;
    @SuppressWarnings("unused")
    private ArrayList<byte[]> list = new ArrayList<byte[]>();

    /**
     * constructor
     */
    public KDH_ReceiveDataFrame()
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
        Iterator it = list.iterator();
        int idx = 0;
        while(it.hasNext()){
            byte[] temp = (byte[])it.next();
        /*    if(idx == 0){
                array.append(cutHeaderTail(temp,true)); 
            }else{
                array.append(cutHeaderTail(temp,false)); 
            }
  */
            array.append(temp);
            idx++;
        }
        return array.toByteArray();
    }
    
    protected byte[] cutHeaderTail(byte[] org, boolean isFirst )
    {
        log.debug("org =>"+new OCTET(org).toHexString());
        int length = 0;
        byte[] ret = null;
        if(org[0]==KDH_DataConstants.SOH_LONG){
	    	length |= (org[1] & 0xff);

	        if(isFirst){
	            ret = new byte[length+3];
	            System.arraycopy(org, 0, ret, 0, length);
	        }else{
	            ret = new byte[length+3];
	            System.arraycopy(org, 0, ret, 0, length);
	        }
        }else if(org[0]==KDH_DataConstants.SOH_SHORT){
        	length=2;
        	if(isFirst){
	            ret = new byte[length];
	            System.arraycopy(org, 0, ret, 0, length);
	        }else{
	            ret = new byte[length];
	            System.arraycopy(org, 0, ret, 0, length);
	        }
        }
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
    public static KDH_ReceiveDataFrame decode(byte[] b) 
        throws Exception
    {
    	KDH_ReceiveDataFrame frame = null;
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
