package com.aimir.fep.protocol.fmp.frame.service.entry;

import javax.xml.bind.annotation.XmlTransient;

import com.aimir.fep.protocol.fmp.datatype.*;
import com.aimir.fep.protocol.fmp.frame.service.Entry;

/**
 * gmtEntry
 * generated by MIB Tool, Do not modify
 *
 * @author Y.S Kim (sorimo@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2007-10-05 15:59:15 +0900 $,
 */
public class gmtEntry extends Entry {

    public WORD gmtTimezone = 
            new WORD();
    public WORD gmtDstValue = 
            new WORD();
    public WORD gmtYear = 
            new WORD();
    public BYTE gmtMon = 
            new BYTE();
    public BYTE gmtDay = 
            new BYTE();
    public BYTE gmtHour = 
            new BYTE();
    public BYTE gmtMin = 
            new BYTE();
    public BYTE gmtSec = 
            new BYTE();

    @XmlTransient
    public WORD getGmtTimezone()
    {
        return this.gmtTimezone;
    }
    
    @XmlTransient
    public WORD getGmtDstValue()
    {
        return this.gmtDstValue;
    }
    
    @XmlTransient
    public WORD getGmtYear()
    {
        return this.gmtYear;
    }
    
    @XmlTransient
    public BYTE getGmtMon()
    {
        return this.gmtMon;
    }
    
    @XmlTransient
    public BYTE getGmtDay()
    {
        return this.gmtDay;
    }
    
    @XmlTransient
    public BYTE getGmtHour()
    {
        return this.gmtHour;
    }
    
    @XmlTransient
    public BYTE getGmtMin()
    {
        return this.gmtMin;
    }
    
    @XmlTransient
    public BYTE getGmtSec()
    {
        return this.gmtSec;
    }

    public void setGmtTimezone(WORD gmtTimezone)
    {
         this.gmtTimezone = gmtTimezone;
    }
    public void setGmtDstValue(WORD gmtDstValue)
    {
         this.gmtDstValue = gmtDstValue;
    }
    public void setGmtYear(WORD gmtYear)
    {
         this.gmtYear = gmtYear;
    }
    public void setGmtMon(BYTE gmtMon)
    {
         this.gmtMon = gmtMon;
    }
    public void setGmtDay(BYTE gmtDay)
    {
         this.gmtDay = gmtDay;
    }
    public void setGmtHour(BYTE gmtHour)
    {
         this.gmtHour = gmtHour;
    }
    public void setGmtMin(BYTE gmtMin)
    {
         this.gmtMin = gmtMin;
    }
    public void setGmtSec(BYTE gmtSec)
    {
         this.gmtSec = gmtSec;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

		sb.append("CLASS["+this.getClass().getName()+"]\n");
        sb.append("gmtTimezone: " + gmtTimezone + "\n");
        sb.append("gmtDstValue: " + gmtDstValue + "\n");
        sb.append("gmtYear: " + gmtYear + "\n");
        sb.append("gmtMon: " + gmtMon + "\n");
        sb.append("gmtDay: " + gmtDay + "\n");
        sb.append("gmtHour: " + gmtHour + "\n");
        sb.append("gmtMin: " + gmtMin + "\n");
        sb.append("gmtSec: " + gmtSec + "\n");

        return sb.toString();
    }
}
