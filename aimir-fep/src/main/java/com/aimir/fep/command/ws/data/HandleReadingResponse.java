package com.aimir.fep.command.ws.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Response to a HandleReadingRequest
 * 
 * <p>
 * Java class for HandleReadingResponse complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="HandleReadingResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="handledDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */
@XmlType(name = "HandleReadingResponse", namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReadingCallback", propOrder = { "handledDate" })
@XmlAccessorType(XmlAccessType.FIELD)
public class HandleReadingResponse {

	@XmlElement(nillable = false, required = true, namespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReadingCallback")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar handledDate;

	/**
	 * Gets the value of the handledDate property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getHandledDate() {
		return handledDate;
	}

	/**
	 * Sets the value of the handledDate property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setHandledDate(XMLGregorianCalendar value) {
		this.handledDate = value;
	}
}