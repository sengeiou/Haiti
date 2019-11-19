package com.aimir.fep.protocol.fmp.frame.service.entry;

import javax.xml.bind.annotation.XmlTransient;

import com.aimir.fep.protocol.fmp.datatype.BYTE;
import com.aimir.fep.protocol.fmp.datatype.CHAR;
import com.aimir.fep.protocol.fmp.datatype.HEX;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.service.Entry;

/**
 * sensorPathEntry (4.4)
 * generated by MIB Tool, Do not modify
 *
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2008-05-08 13:59:15 +0900 $,
 */
public class sensorPathEntry extends Entry {

    /* 4.4.1 */
    public HEX sensorPathID = new HEX(8);

    /* 4.4.2 */
	public OCTET sensorPathSerial = new OCTET(20);

	/* 4.4.3 */
	public OCTET sensorPathModel = new OCTET(18);

	/* 4.4.4 */
	public WORD sensorPathShortID =	new WORD();
	
	/* 4.4.5 */
	public BYTE sensorPathHops = new BYTE();
	
	/* 4.4.6 */
	public OCTET sensorPathNode = new OCTET(60);
    
	/* 4.4.7 */
    public TIMESTAMP sensorPathTime = new TIMESTAMP(7); 
    
    /* 4.4.8 */
    public BYTE sensorPathLQI = new BYTE();
    
    /* 4.4.9 */
    public CHAR sensorPathRSSI = new CHAR();

    @XmlTransient
    public HEX getSensorPathID()
    {
        return sensorPathID;
    }

    public void setSensorPathID(HEX sensorPathID)
    {
        this.sensorPathID = sensorPathID;
    }

    @XmlTransient
    public OCTET getSensorPathSerial()
    {
        return sensorPathSerial;
    }

    public void setSensorPathSerial(OCTET sensorPathSerial)
    {
        this.sensorPathSerial = sensorPathSerial;
    }

    @XmlTransient
    public OCTET getSensorPathModel()
    {
        return sensorPathModel;
    }

    public void setSensorPathModel(OCTET sensorPathModel)
    {
        this.sensorPathModel = sensorPathModel;
    }

    @XmlTransient
    public WORD getSensorPathShortID()
    {
        return sensorPathShortID;
    }

    public void setSensorPathShortID(WORD sensorPathShortID)
    {
        this.sensorPathShortID = sensorPathShortID;
    }

    @XmlTransient
    public BYTE getSensorPathHops()
    {
        return sensorPathHops;
    }

    public void setSensorPathHops(BYTE sensorPathHops)
    {
        this.sensorPathHops = sensorPathHops;
    }

    @XmlTransient
    public OCTET getSensorPathNode()
    {
        return sensorPathNode;
    }

    public void setSensorPathNode(OCTET sensorPathNode)
    {
        this.sensorPathNode = sensorPathNode;
    }

    @XmlTransient
    public TIMESTAMP getSensorPathTime()
    {
        return sensorPathTime;
    }

    public void setSensorPathTime(TIMESTAMP sensorPathTime)
    {
        this.sensorPathTime = sensorPathTime;
    }

    @XmlTransient
    public BYTE getSensorPathLQI()
    {
        return sensorPathLQI;
    }

    public void setSensorPathLQI(BYTE sensorPathLQI)
    {
        this.sensorPathLQI = sensorPathLQI;
    }

    @XmlTransient
    public CHAR getSensorPathRSSI()
    {
        return sensorPathRSSI;
    }

    public void setSensorPathRSSI(CHAR sensorPathRSSI)
    {
        this.sensorPathRSSI = sensorPathRSSI;
    }

    public String toString()
    {
        StringBuffer sb = new StringBuffer();

		sb.append("CLASS["+this.getClass().getName()+"]\n");
		sb.append("sensorPathID: " + sensorPathID + "\n");
		sb.append("sensorPathSerial["+sensorPathSerial.toHexString()+"]: " + sensorPathSerial + "\n");
		sb.append("sensorPathModel["+sensorPathModel.toHexString()+"]: " + sensorPathModel + "\n");
		sb.append("sensorPathShortID: " + sensorPathShortID + "\n");
		sb.append("sensorPathHops: " + sensorPathHops + "\n");
		sb.append("sensorPathNode["+sensorPathNode.toHexString()+"]: " + sensorPathNode + "\n");
        sb.append("sensorPathTime: " + sensorPathTime + "\n");
        sb.append("sensorPathLQI: " + sensorPathLQI + "\n");
        sb.append("sensorPathRSSI: " + sensorPathRSSI + "\n");
        return sb.toString();
    }
}
