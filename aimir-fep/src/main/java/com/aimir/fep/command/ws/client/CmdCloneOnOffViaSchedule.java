package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for CmdCloneOnOffViaSchedule complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="cmdCloneOnOffViaSchedule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;element name="OtaTargetType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="OTAExecuteType" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;element name="FirmwareId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="IssueDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="Propagation" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *       &lt;element name="ModemCommandType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="CloningTime" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>       
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdCloneOnOffViaSchedule", propOrder = { "otaTargetType", "otaExecuteType", "issueDate", "propagation", "modemCommandType", "cloningTime" })
public class CmdCloneOnOffViaSchedule {
	@XmlElement(name = "OtaTargetType")
	protected String otaTargetType;

	@XmlElement(name = "OTAExecuteType")
	protected int otaExecuteType;

	@XmlElement(name = "IssueDate")
	protected String issueDate;

	@XmlElement(name = "Propagation")
	protected boolean propagation;

	@XmlElement(name = "ModemCommandType")
	protected String modemCommandType;

	@XmlElement(name = "CloningTime")
	protected int cloningTime;

	public String getOtaTargetType() {
		return otaTargetType;
	}

	public int getOtaExecuteType() {
		return otaExecuteType;
	}

	public String getIssueDate() {
		return issueDate;
	}

	public boolean isPropagation() {
		return propagation;
	}

	public String getModemCommandType() {
		return modemCommandType;
	}

	public int getCloningTime() {
		return cloningTime;
	}

}
