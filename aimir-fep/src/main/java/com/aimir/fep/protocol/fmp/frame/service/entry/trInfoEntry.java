package com.aimir.fep.protocol.fmp.frame.service.entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.CHAR;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.service.Entry;

/**
 * trInfoEntry
 * generated by MIB Tool, Do not modify
 *
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2009-04-16 09:59:15 +0900 $,
 * <pre>
 * &lt;complexType name="trInfoEntry">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}entry">
 *       &lt;sequence>
 *         &lt;element name="triTargetID" type="{http://server.ws.command.fep.aimir.com/}hex" minOccurs="0"/>
 *         &lt;element name="triCommand" type="{http://server.ws.command.fep.aimir.com/}oid" minOccurs="0"/>
 *         &lt;element name="triID" type="{http://server.ws.command.fep.aimir.com/}word" minOccurs="0"/>
 *         &lt;element name="triOption" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="triDay" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="triInitNice" type="{http://server.ws.command.fep.aimir.com/}char" minOccurs="0"/>
 *         &lt;element name="triCurNice" type="{http://server.ws.command.fep.aimir.com/}char" minOccurs="0"/>
 *         &lt;element name="triInitTry" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="triCurTry" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="triQueue" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="triCreateTime" type="{http://server.ws.command.fep.aimir.com/}timestamp" minOccurs="0"/>
 *         &lt;element name="triLastTime" type="{http://server.ws.command.fep.aimir.com/}timestamp" minOccurs="0"/>
 *         &lt;element name="triState" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *         &lt;element name="triError" type="{http://server.ws.command.fep.aimir.com/}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "trInfoEntry", propOrder = {
    "triTargetID",
    "triCommand",
    "triID",
    "triOption",
    "triDay",
    "triInitNice",
    "triCurNice",
    "triInitTry",
    "triCurTry",
    "triQueue",
    "triCreateTime",
    "triLastTime",
    "triState",
    "triError"
})
public class trInfoEntry extends Entry {
    public HEX triTargetID = new HEX(8);
    public OID triCommand = new OID();
    public WORD triID = new WORD(2);
    public BYTE triOption = new BYTE(1);
    public BYTE triDay = new BYTE(1);
    public CHAR triInitNice = new CHAR();
    public CHAR triCurNice = new CHAR();
    public BYTE triInitTry = new BYTE(1);
    public BYTE triCurTry = new BYTE(1);
    public BYTE triQueue = new BYTE(1);
    public TIMESTAMP triCreateTime = new TIMESTAMP(7);
    public TIMESTAMP triLastTime = new TIMESTAMP(7);
    public BYTE triState = new BYTE(1);
    public INT triError = new INT();

    @XmlTransient
    public HEX getTriTargetID()
    {
        return triTargetID;
    }

    public void setTriTargetID(HEX triTargetID)
    {
        this.triTargetID = triTargetID;
    }

    @XmlTransient
    public OID getTriCommand()
    {
        return triCommand;
    }

    public void setTriCommand(OID triCommand)
    {
        this.triCommand = triCommand;
    }

    @XmlTransient
    public WORD getTriID()
    {
        return triID;
    }

    public void setTriID(WORD triID)
    {
        this.triID = triID;
    }

    @XmlTransient
    public BYTE getTriOption()
    {
        return triOption;
    }

    public void setTriOption(BYTE triOption)
    {
        this.triOption = triOption;
    }

    @XmlTransient
    public BYTE getTriDay()
    {
        return triDay;
    }

    public void setTriDay(BYTE triDay)
    {
        this.triDay = triDay;
    }

    @XmlTransient
    public CHAR getTriInitNice()
    {
        return triInitNice;
    }

    public void setTriInitNice(CHAR triInitNice)
    {
        this.triInitNice = triInitNice;
    }

    @XmlTransient
    public CHAR getTriCurNice()
    {
        return triCurNice;
    }

    public void setTriCurNice(CHAR triCurNice)
    {
        this.triCurNice = triCurNice;
    }

    @XmlTransient
    public BYTE getTriInitTry()
    {
        return triInitTry;
    }

    public void setTriInitTry(BYTE triInitTry)
    {
        this.triInitTry = triInitTry;
    }

    @XmlTransient
    public BYTE getTriCurTry()
    {
        return triCurTry;
    }

    public void setTriCurTry(BYTE triCurTry)
    {
        this.triCurTry = triCurTry;
    }

    @XmlTransient
    public BYTE getTriQueue()
    {
        return triQueue;
    }

    public void setTriQueue(BYTE triQueue)
    {
        this.triQueue = triQueue;
    }

    @XmlTransient
    public TIMESTAMP getTriCreateTime()
    {
        return triCreateTime;
    }

    public void setTriCreateTime(TIMESTAMP triCreateTime)
    {
        this.triCreateTime = triCreateTime;
    }

    @XmlTransient
    public TIMESTAMP getTriLastTime()
    {
        return triLastTime;
    }

    public void setTriLastTime(TIMESTAMP triLastTime)
    {
        this.triLastTime = triLastTime;
    }

    @XmlTransient
    public BYTE getTriState()
    {
        return triState;
    }

    public void setTriState(BYTE triState)
    {
        this.triState = triState;
    }

    @XmlTransient
    public INT getTriError()
    {
        return triError;
    }

    public void setTriError(INT triError)
    {
        this.triError = triError;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

		sb.append("CLASS["+this.getClass().getName()+"]\n");
		sb.append("triTargetID: " + triTargetID + "\n");
		sb.append("triCommand: " + triCommand + "\n");
		sb.append("triID: " + triID + "\n");
		sb.append("triOption: " + triOption + "\n");
		sb.append("triDay: " + triDay + "\n");
		sb.append("triInitNice: " + triInitNice + "\n");
		sb.append("triCurNice: " + triCurNice + "\n");
		sb.append("triInitTry: " + triInitTry + "\n");
		sb.append("triCurTry: " + triCurTry + "\n");
		sb.append("triQueue: " + triQueue + "\n");
		sb.append("triCreateTime: " + triCreateTime + "\n");
		sb.append("triLastTime: " + triLastTime + "\n");
		sb.append("triState: " + triState + "\n");
		sb.append("triError: " + triError + "\n");

        return sb.toString();
    }
}
