package com.aimir.fep.command.ws.client;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.protocol.fmp.exception.FMPMcuException;

/**
 * <p>
 * Java class for CmdSetCoordinatorConfigure complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="cmdSetCoordinatorConfigure">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;element name="McuId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="Configurations" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;element name="ModemMode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;element name="ResetTime" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;element name="MeteringInterval" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;element name="TransmitFrequency" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;element name="CloneTerminate" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdSetCoordinatorConfigure", propOrder = {
	    "mcuId",
	    "configurations",
	    "modemMode",
	    "resetTime",
	    "meteringInterval",
	    "transmitFrequency",
	    "cloneTerminate"	    
})

public class CmdSetCoordinatorConfigure {
	@XmlElement(name = "McuId")
	protected String mcuId;	
	@XmlElement(name = "Configurations")
	protected int configurations;	
	@XmlElement(name = "ModemMode")
	protected int modemMode;	
	@XmlElement(name = "ResetTime")
	protected int resetTime;	
	@XmlElement(name = "MeteringInterval")
	protected int meteringInterval;	
	@XmlElement(name = "TransmitFrequency")
	protected int transmitFrequency;	
	@XmlElement(name = "CloneTerminate")
	protected int cloneTerminate;	
	
	public String getMcuId() {
		return mcuId;
	}
	public void setMcuId(String mcuId) {
		this.mcuId = mcuId;
	}

	public int getConfigurations() {
		return configurations;
	}
	public void setConfigurations(int configurations) {
		this.configurations = configurations;
	}

	public int getModemMode() {
		return modemMode;
	}
	public void setModemMode(int modemMode) {
		this.modemMode = modemMode;
	}

	public int getResetTime() {
		return resetTime;
	}
	public void setResetTime(int resetTime) {
		this.resetTime = resetTime;
	}

	public int getMeteringInterval() {
		return meteringInterval;
	}
	public void setMeteringInterval(int meteringInterval) {
		this.meteringInterval = meteringInterval;
	}

	public int getTransmitFrequency() {
		return transmitFrequency;
	}
	public void setTransmitFrequency(int transmitFrequency) {
		this.transmitFrequency = transmitFrequency;
	}

	public int getCloneTerminate() {
		return cloneTerminate;
	}
	public void setCloneTerminates(int cloneTerminate) {
		this.cloneTerminate = cloneTerminate;
	}
	
}
