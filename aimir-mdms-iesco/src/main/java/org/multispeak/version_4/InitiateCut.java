//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.22 at 06:40:55 PM KST 
//


package org.multispeak.version_4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="newCut" type="{http://www.multispeak.org/Version_4.1_Release}switchingDeviceBank" minOccurs="0"/>
 *         &lt;element name="eventTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "newCut",
    "eventTime"
})
@XmlRootElement(name = "InitiateCut")
public class InitiateCut {

    protected SwitchingDeviceBank newCut;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar eventTime;

    /**
     * Gets the value of the newCut property.
     * 
     * @return
     *     possible object is
     *     {@link SwitchingDeviceBank }
     *     
     */
    public SwitchingDeviceBank getNewCut() {
        return newCut;
    }

    /**
     * Sets the value of the newCut property.
     * 
     * @param value
     *     allowed object is
     *     {@link SwitchingDeviceBank }
     *     
     */
    public void setNewCut(SwitchingDeviceBank value) {
        this.newCut = value;
    }

    /**
     * Gets the value of the eventTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEventTime() {
        return eventTime;
    }

    /**
     * Sets the value of the eventTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEventTime(XMLGregorianCalendar value) {
        this.eventTime = value;
    }

}
