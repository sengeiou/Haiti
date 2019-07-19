package com.aimir.fep.protocol.fmp.frame.service.entry;

import javax.xml.bind.annotation.XmlTransient;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.frame.service.Entry;

/**
 * sensorInfoEntry
 * generated by MIB Tool, Do not modify
 *
 * @author Y.S Kim (sorimo@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class sensorEventEntry extends Entry {
    public TIMESTAMP sevtTime = new TIMESTAMP(7);
    public BYTE sevtType = new BYTE(1);
    public OCTET sevtStatus = new OCTET(4);

    @XmlTransient
    public TIMESTAMP getSevtTime()
    {
        return sevtTime;
    }

    public void setSevtTime(TIMESTAMP sevtTime)
    {
        this.sevtTime = sevtTime;
    }

    @XmlTransient
    public BYTE getSevtType()
    {
        return sevtType;
    }

    public void setSevtType(BYTE sevtType)
    {
        this.sevtType = sevtType;
    }

    @XmlTransient
    public OCTET getSevtStatus()
    {
        return sevtStatus;
    }

    public void setSevtStatus(OCTET sevtStatus)
    {
        this.sevtStatus = sevtStatus;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

		sb.append("CLASS["+this.getClass().getName()+"]\n");
		sb.append("sevtTime: " + sevtTime + "\n");
		sb.append("sevtType: " + sevtType + "\n");
		sb.append("sevtStatus: " + sevtStatus + "\n");

        return sb.toString();
    }
}
