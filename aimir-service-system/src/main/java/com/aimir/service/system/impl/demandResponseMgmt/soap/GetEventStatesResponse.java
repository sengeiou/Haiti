
package com.aimir.service.system.impl.demandResponseMgmt.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.aimir.service.system.impl.demandResponseMgmt.eventstate.ListOfEventStates;


/**
 * <p>Java class for GetEventStatesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetEventStatesResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="returnValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="eventStates" type="{http://www.openadr.org/DRAS/EventState}ListOfEventStates"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetEventStatesResponse", namespace="http://www.openadr.org/DRAS/EventState/", propOrder = {
    "returnValue",
    "eventStates"
})
public class GetEventStatesResponse {

    @XmlElement(required = true)
    protected String returnValue;
    @XmlElement(required = true)
    protected ListOfEventStates eventStates;

    /**
     * Gets the value of the returnValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnValue() {
        return returnValue;
    }

    /**
     * Sets the value of the returnValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnValue(String value) {
        this.returnValue = value;
    }

    /**
     * Gets the value of the eventStates property.
     * 
     * @return
     *     possible object is
     *     {@link ListOfEventStates }
     *     
     */
    public ListOfEventStates getEventStates() {
        return eventStates;
    }

    /**
     * Sets the value of the eventStates property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListOfEventStates }
     *     
     */
    public void setEventStates(ListOfEventStates value) {
        this.eventStates = value;
    }

}
