package com.aimir.fep.protocol.fmp.frame.service.entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.datatype.*;
import com.aimir.fep.protocol.fmp.frame.service.Entry;



/**
 * @author kaze
 *
 * <pre>
 * &lt;complexType name="endDeviceEntry">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}entry">
 *       &lt;sequence>
 *         &lt;element name="commDevId" type="{http://server.ws.command.fep.aimir.com/}octet" minOccurs="0"/>
 *         &lt;element name="currentDemand" type="{http://server.ws.command.fep.aimir.com/}uint" minOccurs="0"/>
 *         &lt;element name="deviceDescription" type="{http://server.ws.command.fep.aimir.com/}octet" minOccurs="0"/>
 *         &lt;element name="deviceId" type="{http://server.ws.command.fep.aimir.com/}octet" minOccurs="0"/>
 *         &lt;element name="drEnable" type="{http://server.ws.command.fep.aimir.com/}bool" minOccurs="0"/>
 *         &lt;element name="drLevel" type="{http://server.ws.command.fep.aimir.com/}int" minOccurs="0"/>
 *         &lt;element name="drProgramMandatory" type="{http://server.ws.command.fep.aimir.com/}bool" minOccurs="0"/>
 *         &lt;element name="firmwareVersion" type="{http://server.ws.command.fep.aimir.com/}octet" minOccurs="0"/>
 *         &lt;element name="loadControl" type="{http://server.ws.command.fep.aimir.com/}bool" minOccurs="0"/>
 *         &lt;element name="localCapacity" type="{http://server.ws.command.fep.aimir.com/}octet" minOccurs="0"/>
 *         &lt;element name="scheduledInterval" type="{http://server.ws.command.fep.aimir.com/}int" minOccurs="0"/>
 *         &lt;element name="status" type="{http://server.ws.command.fep.aimir.com/}int" minOccurs="0"/>
 *         &lt;element name="switchStatus" type="{http://server.ws.command.fep.aimir.com/}bool" minOccurs="0"/>
 *         &lt;element name="type" type="{http://server.ws.command.fep.aimir.com/}int" minOccurs="0"/>
 *         &lt;element name="updateDate" type="{http://server.ws.command.fep.aimir.com/}timestamp" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "endDeviceEntry", propOrder = {
    "commDevId",
    "currentDemand",
    "deviceDescription",
    "deviceId",
    "drEnable",
    "drLevel",
    "drProgramMandatory",
    "firmwareVersion",
    "loadControl",
    "localCapacity",
    "scheduledInterval",
    "status",
    "switchStatus",
    "type",
    "updateDate"
})
public class endDeviceEntry extends Entry {
	private OCTET commDevId = new OCTET();
	private OCTET deviceId = new OCTET();
	private OCTET deviceDescription = new OCTET();
	private OCTET firmwareVersion = new OCTET();
	private INT drLevel = new INT();
	private BOOL drProgramMandatory = new BOOL();
	private BOOL drEnable = new BOOL();
	private OCTET localCapacity  = new OCTET();
	private BOOL loadControl = new BOOL();
	private UINT currentDemand = new UINT();
	private INT type = new INT();
	private INT scheduledInterval = new INT();
	private INT status = new INT();
	private TIMESTAMP updateDate = new TIMESTAMP();
	private BOOL switchStatus = new BOOL();
	
	/**
	 * @return the commDevId
	 */
	public OCTET getCommDevId() {
		return commDevId;
	}

	/**
	 * @param commDevId the commDevId to set
	 */
	public void setCommDevId(OCTET commDevId) {
		this.commDevId = commDevId;
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
	 * @return the deviceDescription
	 */
	public OCTET getDeviceDescription() {
		return deviceDescription;
	}

	/**
	 * @param deviceDescription the deviceDescription to set
	 */
	public void setDeviceDescription(OCTET deviceDescription) {
		this.deviceDescription = deviceDescription;
	}

	/**
	 * @return the firmwareVersion
	 */
	public OCTET getFirmwareVersion() {
		return firmwareVersion;
	}

	/**
	 * @param firmwareVersion the firmwareVersion to set
	 */
	public void setFirmwareVersion(OCTET firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
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
	 * @return the drProgramMandatory
	 */
	public BOOL getDrProgramMandatory() {
		return drProgramMandatory;
	}

	/**
	 * @param drProgramMandatory the drProgramMandatory to set
	 */
	public void setDrProgramMandatory(BOOL drProgramMandatory) {
		this.drProgramMandatory = drProgramMandatory;
	}

	/**
	 * @return the drEnable
	 */
	public BOOL getDrEnable() {
		return drEnable;
	}

	/**
	 * @param drEnable the drEnable to set
	 */
	public void setDrEnable(BOOL drEnable) {
		this.drEnable = drEnable;
	}

	/**
	 * @return the localCapacity
	 */
	public OCTET getLocalCapacity() {
		return localCapacity;
	}

	/**
	 * @param localCapacity the localCapacity to set
	 */
	public void setLocalCapacity(OCTET localCapacity) {
		this.localCapacity = localCapacity;
	}

	/**
	 * @return the loadControl
	 */
	public BOOL getLoadControl() {
		return loadControl;
	}

	/**
	 * @param loadControl the loadControl to set
	 */
	public void setLoadControl(BOOL loadControl) {
		this.loadControl = loadControl;
	}

	/**
	 * @return the currentDemand
	 */
	public UINT getCurrentDemand() {
		return currentDemand;
	}

	/**
	 * @param currentDemand the currentDemand to set
	 */
	public void setCurrentDemand(UINT currentDemand) {
		this.currentDemand = currentDemand;
	}

	/**
	 * @return the type
	 */
	public INT getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(INT type) {
		this.type = type;
	}

	/**
	 * @return the scheduledInterval
	 */
	public INT getScheduledInterval() {
		return scheduledInterval;
	}

	/**
	 * @param scheduledInterval the scheduledInterval to set
	 */
	public void setScheduledInterval(INT scheduledInterval) {
		this.scheduledInterval = scheduledInterval;
	}

	/**
	 * @return the status
	 */
	public INT getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(INT status) {
		this.status = status;
	}

	/**
	 * @return the updateDate
	 */
	public TIMESTAMP getUpdateDate() {
		return updateDate;
	}

	/**
	 * @param updateDate the updateDate to set
	 */
	public void setUpdateDate(TIMESTAMP updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * @return the switchStatus
	 */
	public BOOL getSwitchStatus() {
		return switchStatus;
	}

	/**
	 * @param switchStatus the switchStatus to set
	 */
	public void setSwitchStatus(BOOL switchStatus) {
		this.switchStatus = switchStatus;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "endDeviceEntry [commDevId=" + commDevId + ", currentDemand="
				+ currentDemand + ", deviceDescription=" + deviceDescription
				+ ", deviceId=" + deviceId + ", drEnable=" + drEnable
				+ ", drLevel=" + drLevel + ", drProgramMandatory="
				+ drProgramMandatory + ", firmwareVersion=" + firmwareVersion
				+ ", loadControl=" + loadControl + ", localCapacity="
				+ localCapacity + ", scheduledInterval=" + scheduledInterval
				+ ", status=" + status + ", switchStatus=" + switchStatus
				+ ", type=" + type + ", updateDate=" + updateDate + "]";
	}
	
}
