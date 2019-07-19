
package com.aimir.service.system.impl.demandResponseMgmt.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import  com.aimir.service.system.impl.demandResponseMgmt.eventstate.EventStateConfirmation;


/**
 * <p>Java class for SetEventStateConfirmation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetEventStateConfirmation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventStateConfirmation" type="{http://www.openadr.org/DRAS/EventState}EventStateConfirmation"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SetEventStateConfirmation", namespace="http://www.openadr.org/DRAS/EventState/", propOrder = {
    "eventStateConfirmation"
})
public class SetEventStateConfirmation {

    @XmlElement(required = true)
    protected EventStateConfirmation eventStateConfirmation;

    /**
     * Gets the value of the eventStateConfirmation property.
     * 
     * @return
     *     possible object is
     *     {@link EventStateConfirmation }
     *     
     */
    public EventStateConfirmation getEventStateConfirmation() {
        return eventStateConfirmation;
    }

    /**
     * Sets the value of the eventStateConfirmation property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventStateConfirmation }
     *     
     */
    public void setEventStateConfirmation(EventStateConfirmation value) {
        this.eventStateConfirmation = value;
    }

}
