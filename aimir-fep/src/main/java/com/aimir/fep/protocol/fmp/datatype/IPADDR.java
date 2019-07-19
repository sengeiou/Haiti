package com.aimir.fep.protocol.fmp.datatype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.util.DataUtil;

/**
 * represent IPADDR Data Type
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 * <pre>
 * &lt;complexType name="ipaddr">
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
@XmlType(name = "ipaddr", propOrder = {
    "value"
})
public class IPADDR extends FMPVariable
{
    private String value = null;

    /**
     * constructor
     */
    public IPADDR()
    {
    }

    /**
     * constructor
     *
     * @param value <code>String</code> value
     */
    public IPADDR(String value)
    {
        this.value = value;
    }

    /**
     * constructor
     *
     * @param value <code>byte[]</code> value
     */
    public IPADDR(byte[] value)
    {
        this.value = DataUtil.decodeIpAddr(value);
    }

    /**
     * get value
     *
     * @return result <code>String</code>
     */
    public String getValue()
    {
        return  this.value;
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
        this.value = DataUtil.decodeIpAddr(value);
    }

    /**
     * encode IPADDR Value
     *
     * @return value <code>byte[]</code> encoded byte array
     */
    public byte[] encode()
    {
        if(value == null)
            return new byte[0];
        return DataUtil.encodeIpAddr(this.value);
    }

    /**
     * decode IPADDR Value
     *
     * @param buff <code>IoBuffer</code> input bytebuffer
     * @param size <code>int</code> Value length
     */
    public void decode(String ns, IoBuffer buff,int size)
    {
        if(size < 4)
        {
            byte[] bx = new byte[4];
            buff.get(bx,0,size);
            setValue(bx);
        }
        else if(size == 4)
        {
            byte[] bx = new byte[4];
            buff.get(bx,0,bx.length);
            setValue(bx);
        } else if(size > 4)
        {
            byte[] temp = new byte[size];
            buff.get(temp,0,temp.length);
            byte[] bx = new byte[4];
            System.arraycopy(temp,0,bx,0,bx.length);
            setValue(bx);
        }
    }

    public int decode(String ns, byte[] buff,int pos)
    {
        byte[] bx = new byte[4];
        System.arraycopy(buff,pos,bx,0,bx.length);
        setValue(bx);
        return bx.length;
    }

    public int decode(String ns, byte[] buff,int pos,int size)
    {
        byte[] bx = new byte[4];
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
        return DataType.IPADDR;
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
    public String getMIBName() { return "ipEntry"; }

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
