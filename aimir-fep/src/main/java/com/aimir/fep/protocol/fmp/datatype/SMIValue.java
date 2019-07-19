package com.aimir.fep.protocol.fmp.datatype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

import com.aimir.fep.util.ByteArray;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.MIBUtil;


/**
 * represent SMIValue Data Type
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 * <pre>
 * &lt;complexType name="smiValue">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}fmpVariable">
 *       &lt;sequence>
 *         &lt;element name="oid" type="{http://server.ws.command.fep.aimir.com/}oid" minOccurs="0"/>
 *         &lt;element name="variable" type="{http://server.ws.command.fep.aimir.com/}fmpVariable" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "smiValue", propOrder = {
    "oid",
    "variable"
})
public class SMIValue extends FMPVariable
{
    private static Log log = LogFactory.getLog(SMIValue.class);
    
    private OID oid = null;
    private FMPVariable variable = null;

    /**
     * constructor
     */
    public SMIValue()
    {
    }

    /**
     * constructor
     *
     * @param oid <code>OID</code> oid
     */
    public SMIValue(OID oid)
    {
        this.oid = oid;
    }

    /**
     * constructor
     *
     * @param oid <code>OID</code> oid
     * @param variable <code>FMPVariable</code> variable
     */
    public SMIValue(OID oid, FMPVariable variable)
    {
        this.oid = oid;
        this.variable = variable;
    }

    /**
     * get oid
     *
     * @return oid <code>OID</code> oid
     */ 
    public OID getOid()
    {
        return this.oid;
    }

    /**
     * set oid
     *
     * @param oid <code>OID</code> oid
     */ 
    public void setOid(OID oid)
    {
        this.oid = oid;
    }

    /**
     * get variable
     *
     * @return variable <code>FMPVariable</code> variable
     */ 
    public FMPVariable getVariable()
    {
        return this.variable;
    }

    /**
     * set variable
     *
     * @param variable <code>FMPVariable</code> variable
     */ 
    public void setVariable(FMPVariable variable)
    {
        this.variable = variable;
    }

    /**
     * encode SMIValue Value
     *
     * @return value <code>byte[]</code> encoded byte array
     */
    public byte[] encode()
    {
        if(this.oid == null)
            return new byte[0];
        ByteArray ba = new ByteArray();
        byte[] bval = oid.encode();
        byte[] lval = null;
        ba.append(bval);
        int len = 0;
        if(this.variable == null)
        {
            /*
            ba.append(DataUtil.get2ByteToInt(len));
            */
            lval = DataUtil.get2ByteToInt(len);
            DataUtil.convertEndian(lval);
            ba.append(lval);
        }
        else
        {
            if(this.variable instanceof FMPNonFixedVariable)
                bval = 
                    ((FMPNonFixedVariable)this.variable).encode(true);
            else
                bval = this.variable.encode();
            len = bval.length;
            /*
            ba.append(DataUtil.get2ByteToInt(len));
            */
            lval = DataUtil.get2ByteToInt(len);
            DataUtil.convertEndian(lval);
            ba.append(lval);
            if(len > 0)
                ba.append(bval);
        }

        return ba.toByteArray();
    }

    public byte[] getVariableByte(){
        byte[] bval = null;
        
        if(this.variable instanceof FMPNonFixedVariable){
            bval = ((FMPNonFixedVariable)this.variable).encode(true);
        } else{
            bval = this.variable.encode();
        }
        
        return bval;
    }
    
    /**
     * decode SMIValue Value
     *
     * @param buff <code>IoBuffer</code> input bytebuffer
     * @param size <code>int</code> Value length
     */
    public void decode(String ns, IoBuffer buff,int size)
    {
        this.oid = new OID();
        oid.decode(ns,buff,3);
        byte[] lval = new byte[2];
        lval[0]=buff.get();
        lval[1]=buff.get();
        DataUtil.convertEndian(lval);
        int len = DataUtil.getIntTo2Byte(lval);
        /*
        int len = DataUtil.getIntTo2Byte(buff.get(),buff.get());
        */
        if(len == 0)
            return;
        // TO DO : find type of data in MIB
        if(!DataUtil.isEntryOid(ns, this.oid))
        {
            log.debug("get FMPVariableObject");
            this.variable = DataUtil.getFMPVariableObject(ns, this.oid);
            variable.decode(ns,buff,len);
        } else
        {
            log.debug("create FMPVariableObject");
            String clsName = DataUtil.getMIBClassName(
                    ns, this.oid.toString());
            if(clsName == null)
            {
                int pos = buff.position();
                buff.position(pos+len);
                return;
            }
            this.variable = new OPAQUE(clsName);
            variable.decode(ns,buff,len);
        }
    }

    public int decode(String ns,byte[] buff,int pos)
    {
        int bpos = pos;
        this.oid = new OID();
        bpos+=oid.decode(ns,buff,bpos);
        byte[] lval = new byte[2];
        lval[0]=buff[bpos];
        bpos++;
        lval[1]=buff[bpos];
        bpos++;
        DataUtil.convertEndian(lval);
        int len = DataUtil.getIntTo2Byte(lval);
        
        MIBUtil mu = MIBUtil.getInstance(ns);
        
        if(len == 0)
            return (bpos - pos);
        // TO DO : find type of data in MIB
        log.debug("NS[" + ns + "] OID[" + oid.value + "]");
        if(!DataUtil.isEntryOid(ns,this.oid))
        {
            this.variable = DataUtil.getFMPVariableObject(ns,this.oid);
            bpos+=variable.decode(ns,buff,bpos,len);
            
            log.debug("[GET FMPVariableObject] SMIValue OID["+this.oid+
                    "] OID_NAME[" + mu.getName(oid.toString()) + "] VALUE[" + variable.toString() + "]");
        } else
        {
            String clsName = DataUtil.getMIBClassName(
                    ns,this.oid.toString());
            log.debug(" make class name : " + clsName);
            if(clsName == null)
            {
                bpos+=len;
                return (bpos - pos);
            }
            this.variable = new OPAQUE(clsName);
            bpos+=variable.decode(ns,buff,bpos,len);
            
            log.debug("[CREATE FMPVariableObject] SMIValue OID["+this.oid+
                    "] OID_NAME[" + mu.getName(oid.toString()) + "] VALUE[" + variable.toString() + "]");
        }
        
        return (bpos-pos);
    }

    public int decode(String ns,byte[] buff,int pos,int size)
    {
        int bpos = pos;
        this.oid = new OID();
        bpos+=oid.decode(ns,buff,bpos);
        byte[] lval = new byte[2];
        lval[0]=buff[bpos];
        bpos++;
        lval[1]=buff[bpos];
        bpos++;
        DataUtil.convertEndian(lval);
        int len = DataUtil.getIntTo2Byte(lval);
        if(len == 0)
            return (bpos - pos);
        // TO DO : find type of data in MIB
        if(!DataUtil.isEntryOid(ns,this.oid))
        {
            log.debug("get FMPVariableObject");
            
            this.variable = DataUtil.getFMPVariableObject(ns,this.oid);
            bpos+=variable.decode(ns,buff,bpos,len);
        } else
        {
            log.debug("create FMPVariableObject");
            
            String clsName = DataUtil.getMIBClassName(
                    ns,this.oid.toString());
            if(clsName == null)
            {
                bpos+=len;
                return (bpos - pos);
            }
            this.variable = new OPAQUE(clsName);
            bpos+=variable.decode(ns,buff,bpos,len);
        }
        return (bpos-pos);
    }

    /**
     * get syntax(data type)
     *
     * @return syntax <code>int</code> syntax
     */
    public int getSyntax()
    {
        return DataType.SMIVALUE;
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
    public String getMIBName() { return "smiEntry"; }

    /**
     * get INT String Value
     *
     * @return value <code>String</code>
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        if(variable instanceof OCTET)
        {
            sb.append("OID["+oid+"] VARIABLE["
                    +Hex.decode(((OCTET)variable).getValue())
                    +"]");
        }
        else
        {
            if (variable == null)
                sb.append("OID[" + oid + "] VARRIABLE[NULL]");
            else
                sb.append("OID["+oid+"] VARIABLE["+variable.toString()+"]");
        }
        return sb.toString();
    }
}
