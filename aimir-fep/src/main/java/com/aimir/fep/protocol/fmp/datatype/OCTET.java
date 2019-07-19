package com.aimir.fep.protocol.fmp.datatype;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * represent OCTET Data Type
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 * <pre>
 * &lt;complexType name="octet">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}fmpNonFixedVariable">
 *       &lt;sequence>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "octet", propOrder = {
        "type"
})
public class OCTET extends FMPNonFixedVariable
{
    String  type = "java.lang.String";
    
    /**
     * constructor
     */
    public OCTET()
    {
    }

    /**
     * constructor
     *
     * @param len <code>int</code> length of byte array
     */
    public OCTET(int len)
    {
        this.value = new byte[len];
        this.len = len;
        this.isFixed = true;
    }

    /**
     * constructor
     *
     * @param len <code>int</code> length of byte array
     * @param isFixed <code>boolean</code> fixed length
     */
    public OCTET(int len, boolean isFixed)
    {
        this.value = new byte[len];
        this.len = len;
        this.isFixed = isFixed;
    }

    /**
     * constructor
     *
     * @param value <code>String</code> OCTET String
     */
    public OCTET(String value) 
    { 
        ByteArrayOutputStream bao = new ByteArrayOutputStream(); 
        byte[] strbyte = new byte[0];
        if(value != null && value.length() > 0)
            strbyte = value.getBytes();
        if(strbyte.length > 1)
            bao.write(strbyte,0,strbyte.length); 
        else if(strbyte.length == 1)
            bao.write(strbyte[0]);
        this.value = bao.toByteArray(); 
        this.len = this.value.length; 
    }

    /**
     * constructor
     *
     * @param value <code>String</code> OCTET String
     * @param len <code>int</code> length of byte array
     * @param isFixed <code>boolean</code> fixed length
     */
    public OCTET(String value, 
            int len, boolean isFixed)
    {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        byte[] strbyte = new byte[0];
        if(value != null && value.length() > 0)
            strbyte = value.getBytes();
        if(isFixed)
        {
            this.value = new byte[len];
            if(strbyte.length > 0)
            {
                System.arraycopy(strbyte,0,this.value,0,
                        strbyte.length);
            }
        } 
        else
        {
            if(strbyte.length > 1)
                bao.write(strbyte,0,strbyte.length);
            else if(strbyte.length == 1)
                bao.write(strbyte[0]);
            this.value = bao.toByteArray();
        }
        this.len = this.value.length;
        this.isFixed = isFixed;

    }


    /**
     * constructor
     *
     * @param value <code>byte[]</code> byte array
     */
    public OCTET(byte[] value) 
    { 
        this.value = value; 
        /*
        this.len = this.value.length; 
        this.isFixed = true;
        */
    }

    public OCTET(byte[] value,int len, 
            boolean isFixed)
    {
        this.value = value;
        this.len = len;
        this.isFixed = isFixed;
    }

    public OCTET(IoBuffer bytebuffer , 
            int len, boolean isFixed) throws Exception
    {
        int tmplen = bytebuffer.remaining(); 

        if(isFixed && len > tmplen)
            len = tmplen;

        if(!isFixed)
        {
            len= ((bytebuffer.get() & 0xff) << 8) 
                + ((bytebuffer.get() & 0xff) << 0);
        }
        this.value = new byte[len];
        bytebuffer.get(this.value,0,len);
        this.len = len;
        this.isFixed = isFixed;
    }

    /**
     * get OCTET 
     *
     * @return value <code>byte[]</code> OCTET 
     */
    public byte[] getValue()
    {
        return this.value;
    }

    /**
     * set OCTET Value String
     *
     * @param value <code>String</code> OCTET String
     */
    public void setValue(String value)
    {
        setValue(value.getBytes());
    }

    /**
     * set OCTET Value Byte array
     *
     * @param value <code>byte[]</code> OCTET value byte array
     */
    public void setValue(byte[] value)
    {
        int size = value.length;
        if(isFixed)
        { 
            if(len < size) 
                size =len; 
            System.arraycopy(value,0,this.value,0,size); 
        } 
        else 
        { 
            this.value = value; 
            this.len = size; 
        }
    }

    /**
     * encode OCTET Value
     *
     * @return value <code>byte[]</code> encoded byte array
     */
    public byte[] encode()
    {
        if(this.value == null || this.value.length < 1)
            return new byte[0];
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        if(isFixed)
        {
            bao.write(value,0,len);
        } 
        else 
        {
            this.len = value.length;
            byte[] bx = DataUtil.get2ByteToInt(this.len);
            DataUtil.convertEndian(bx);
            bao.write(bx,0,bx.length);
            /*
            bao.write((byte)(len >> 8));
            bao.write((byte)len);
            */
            bao.write(value,0,len);
        }

        return bao.toByteArray();
    }

    /**
     * encode OCTET Value
     *
     * @param iscompact <code>boolean</code>
     * @return value <code>byte[]</code> encoded byte array
     */
    public byte[] encode(boolean isCompact)
    {
        this.len = value.length;
        if(this.len < 1)
            return new byte[0];
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        if(isFixed)
        {
            bao.write(value,0,len);
        } 
        else 
        {
            if(!isCompact)
            {
                this.len = value.length;
                byte[] bx = DataUtil.get2ByteToInt(this.len);
                DataUtil.convertEndian(bx);
                bao.write(bx,0,bx.length);
                /*
                bao.write((byte)(len >> 8));
                bao.write((byte)len);
                */
            }
            bao.write(value,0,len);
        }

        return bao.toByteArray();
    }

    /**
     * decode OCTET Value
     *
     * @param bytebuffer <code>IoBuffer</code> input bytebuffer
     * @param pos <code>int</code> position of ByteBuffer
     * @param length <code>int</code> value length
     * @param isFixed <code>boolean</code> isFixed 
     */
    public void decode(String ns, IoBuffer bytebuffer ,int pos, int length, 
            boolean isFixed) throws Exception
    {
        bytebuffer.position(pos);
        int tmplen = bytebuffer.remaining(); 

        if(isFixed && length > tmplen)
            length = tmplen;
        else if(!isFixed && length > (tmplen -2))
            length = tmplen - 2;

        if(isFixed)
        {
            this.value = new byte[length];
            bytebuffer.get(this.value,0,length);
        }
        else
        {
            /*
            int size = ((bytebuffer.get() & 0xff) << 8) 
                + ((bytebuffer.get() & 0xff) << 0);
            */
            byte[] lval = new byte[2];
            lval[0]=bytebuffer.get();
            lval[1]=bytebuffer.get();
            DataUtil.convertEndian(lval);
            int size = DataUtil.getIntTo2Byte(lval);
            this.value = new byte[size];
            bytebuffer.get(this.value,0,this.value.length);
            length = size;
        }
        this.len = length;
        this.isFixed = isFixed;
    }

    /**
     * decode OCTET Value
     *
     * @param bytebuffer <code>IoBuffer</code> input bytebuffer
     * @param size <code>int</code> value length
     */
    public void decode(String ns, IoBuffer buff,int size)
    {
        byte[] bx = new byte[size];
        buff.get(bx,0,bx.length);
        setValue(bx);
    }

    /**
     * decode OCTET Value
     *
     * @param bytebuffer <code>IoBuffer</code> input bytebuffer
     */
    public void decode(String ns, IoBuffer buff)
    {
        try {
            if(isFixed && (this.value != null) 
                    && (this.value.length > 0)) 
                buff.get(this.value,0,this.value.length); 
            else 
                decode(ns,buff,buff.position(),this.len,this.isFixed);
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public int decode(String ns, byte[] buff,int pos)
    { 
        int tlen = 0;
        if(isFixed)
        {
            System.arraycopy(buff,pos,this.value,0,
                    this.value.length); 
            tlen = this.value.length;
        }
        else
        {
            byte[] bx = new byte[2];
            System.arraycopy(buff,pos,bx,0,bx.length);
            DataUtil.convertEndian(bx);
            int size = DataUtil.getIntTo2Byte(bx);
            this.value = new byte[size];
            System.arraycopy(buff,pos+bx.length,this.value,0,
                    this.value.length); 
            tlen = bx.length+this.value.length;
        }
        return tlen;
    }

    @Override
    public int decode(String ns, byte[] buff,int pos,int size)
    {
        this.value = new byte[size];
        System.arraycopy(buff,pos,this.value,0,this.value.length); 
        return size;
    }

    /**
     * get syntax(data type)
     *
     * @return syntax <code>int</code> syntax
     */
    public int getSyntax()
    {
        return DataType.STRING;
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
    public String getMIBName() { return "streamEntry"; }

    /**
     * get OCTET String Value
     *
     * @return value <code>String</code>
     */
    public String toString()
    {
        if(value == null)
            return ""+this.value;
        return new String(this.value).trim();
    }

    public String toHexString()
    {
        if(value == null)
            return ""+this.value;
        return Hex.decode(this.value);
    }
}
