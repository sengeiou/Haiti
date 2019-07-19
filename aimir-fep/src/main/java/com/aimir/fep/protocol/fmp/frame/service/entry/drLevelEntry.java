package com.aimir.fep.protocol.fmp.frame.service.entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.datatype.*;
import com.aimir.fep.protocol.fmp.frame.service.Entry;


/**
 * @author kaze
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="drLevelEntry">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}entry">
 *       &lt;sequence>
 *         &lt;element name="commdevId" type="{http://server.ws.command.fep.aimir.com/}octet" minOccurs="0"/>
 *         &lt;element name="deviceId" type="{http://server.ws.command.fep.aimir.com/}octet" minOccurs="0"/>
 *         &lt;element name="drLevel" type="{http://server.ws.command.fep.aimir.com/}int" minOccurs="0"/>
 *         &lt;element name="drProgramName" type="{http://server.ws.command.fep.aimir.com/}octet" minOccurs="0"/>
 *         &lt;element name="duration" type="{http://server.ws.command.fep.aimir.com/}int" minOccurs="0"/>
 *         &lt;element name="incentive" type="{http://server.ws.command.fep.aimir.com/}octet" minOccurs="0"/>
 *         &lt;element name="status" type="{http://server.ws.command.fep.aimir.com/}byte" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "drLevelEntry", propOrder = {
    "commdevId",
    "deviceId",
    "drLevel",
    "drProgramName",
    "duration",
    "incentive",
    "status"
})
public class drLevelEntry extends Entry {
	private OCTET commdevId = new OCTET();
	private OCTET deviceId = new OCTET();
	private INT drLevel = new INT();
	private OCTET drProgramName = new OCTET();
	private INT duration = new INT();
	private OCTET incentive = new OCTET();
	private BYTE status = new BYTE();
	
	/**
	 * @return the commdevId
	 */
	public OCTET getCommdevId() {
		return commdevId;
	}

	/**
	 * @param commdevId the commdevId to set
	 */
	public void setCommdevId(OCTET commdevId) {
		this.commdevId = commdevId;
	}

	/**
	 * @return the deviceId
	 */
	public OCTET getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(OCTET deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @return the drLevel
	 */
	public INT getDrLevel() {
		return drLevel;
	}

	/**
	 * @param drLevel the drLevel to set
	 */
	public void setDrLevel(INT drLevel) {
		this.drLevel = drLevel;
	}

	/**
	 * @return the drProgramName
	 */
	public OCTET getDrProgramName() {
		return drProgramName;
	}

	/**
	 * @param drProgramName the drProgramName to set
	 */
	public void setDrProgramName(OCTET drProgramName) {
		this.drProgramName = drProgramName;
	}

	/**
	 * @return the duration
	 */
	public INT getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(INT duration) {
		this.duration = duration;
	}

	/**
	 * @return the incentive
	 */
	public OCTET getIncentive() {
		return incentive;
	}

	/**
	 * @param incentive the incentive to set
	 */
	public void setIncentive(OCTET incentive) {
		this.incentive = incentive;
	}

	/**
	 * @return the status
	 */
	public BYTE getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(BYTE status) {
		this.status = status;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "drLevelEntry [commdevId=" + commdevId + ", deviceId="
				+ deviceId + ", drLevel=" + drLevel + ", drProgramName="
				+ drProgramName + ", duration=" + duration + ", incentive="
				+ incentive + ", status=" + status + "]";
	}
	
}
