package com.aimir.fep.protocol.fmp.datatype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * represent IP6ADDR Data Type
 * 
 * @author goodjob (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2014-09-10 10:00:00 +0900 $,
 * <pre>
 * &lt;complexType name="ip6addr">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}fmpVariable">
 *       &lt;sequence>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ip6addr", propOrder = {
    "value"
})
public class IP6ADDR extends FMPVariable
{
    private String value = null;

    /**
     * constructor
     */
    public IP6ADDR()
    {
    }

    /**
     * constructor
     *
     * @param value <code>String</code> value
     */
    public IP6ADDR(String value)
    {
        this.value = value;
    }

    /**
     * constructor
     *
     * @param value <code>byte[]</code> value
     */
    public IP6ADDR(byte[] value)
    {
        setValue(value);
    }

    /**
     * get value
     *
     * @return result <code>String</code>
     */
    public String getValue()
    {
        return this.value;
    }

    /**
     * set value
     *
     * @param value <code>int</code>
     */
    public void setValue(String value)
    {
        this.value = value;
    }
    /**
     * set value
     *
     * @param value <code>byte[]</code>
     */
    public void setValue(byte[] value)
    {
        StringBuffer sb = new StringBuffer();
		
	    for (int i = 0; i < 8; i++) { 
	        sb.append(Integer.toHexString(((value[i << 1] << 8) & 0xff00) 
	                | (value[(i << 1) + 1] & 0xff))); 

	        if (i < 8 - 1) { 
	            sb.append(":"); 
	        } 
	    } 
	    this.value = sb.toString();
    }

    /**
     * encode IP6ADDR Value
     *
     * @return value <code>byte[]</code> encoded byte array
     */
    public byte[] encode()
    {
        if(value == null)
            return new byte[0];
        
        String[] ips = value.split("[\\:]");
        byte[] res = new byte[16];
        int idx = 0;
        for(int i = 0 ; i < ips.length ; i++)
        {
            int ival = Integer.parseInt(ips[i]);
            res[idx++] = (byte)(ival >> 8);
            res[idx++] = (byte)ival;
        }
        
        return res;
    }

    /**
     * decode IPADDR Value
     *
     * @param buff <code>IoBuffer</code> input bytebuffer
     * @param size <code>int</code> Value length
     */
    public void decode(String ns, IoBuffer buff,int size)
    {
        if(size < 16)
        {
            byte[] bx = new byte[16];
            buff.get(bx,0,size);
            setValue(bx);
        }
        else if(size == 16)
        {
            byte[] bx = new byte[16];
            buff.get(bx,0,bx.length);
            setValue(bx);
        } else if(size > 16)
        {
            byte[] temp = new byte[size];
            buff.get(temp,0,temp.length);
            byte[] bx = new byte[16];
            System.arraycopy(temp,0,bx,0,bx.length);
            setValue(bx);
        }
    }

    public int decode(String ns, byte[] buff,int pos)
    {
        byte[] bx = new byte[16];
        System.arraycopy(buff,pos,bx,0,bx.length);
        setValue(bx);
        return bx.length;
    }

    public int decode(String ns, byte[] buff,int pos,int size)
    {
        byte[] bx = new byte[16];
        System.arraycopy(buff,pos,bx,0,bx.length);
        setValue(bx);
        return size;
    }

    /**
     * get syntax(data type)
     *
     * @return syntax <code>int</code> syntax
     */
    public int getSyntax()
    {
        return DataType.IP6ADDR;
    }

    /**
     * get java syntax
     *
     *@returnsyntax<code>String</code>
     */
    public String getJavaSyntax()
    {
        return String.class.getName();
    }
    public String getMIBName() { return "ip6Entry"; }

    /**
     * get IPADDR String Value
     *
     * @return value <code>String</code>
     */
    public String toString()
    {
        return this.value;
    }
}
