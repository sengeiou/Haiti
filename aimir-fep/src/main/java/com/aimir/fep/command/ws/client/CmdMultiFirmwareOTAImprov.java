package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for CmdMultiFirmwareOTAImprov complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="cmdMultiFirmwareOTAImprov">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;element name="OTATargetType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="FirmwareId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="IssueDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;element name="OTAExecuteType" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;element name="UseAsyncChannel" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>       
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdMultiFirmwareOTAImprov", propOrder = { "otaTargetType", "firmwareId", "issueDate", "otaExecuteType", "useAsyncChannel" })
public class CmdMultiFirmwareOTAImprov {
	@XmlElement(name = "OTATargetType")
	protected String otaTargetType;

	@XmlElement(name = "FirmwareId")
	protected String firmwareId;

	@XmlElement(name = "IssueDate")
	protected String issueDate;

	@XmlElement(name = "OTAExecuteType")
	protected int otaExecuteType;

	@XmlElement(name = "UseAsyncChannel")
	protected boolean useAsyncChannel;

	/**
	 * Gets the value of the otaTargetType property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getOtaTargetType() {
		return otaTargetType;
	}

	/**
	 * Gets the value of the firmwareId property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getFirmwareId() {
		return firmwareId;
	}

	/**
	 * Gets the value of the issueDate property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getIssueDate() {
		return issueDate;
	}

	/**
	 * Gets the value of the otaExecuteType property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public int getOtaExecuteType() {
		return otaExecuteType;
	}

	/**
	 * Gets the value of the useAsyncChannel property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public boolean isUseAsyncChannel() {
		return useAsyncChannel;
	}
}
