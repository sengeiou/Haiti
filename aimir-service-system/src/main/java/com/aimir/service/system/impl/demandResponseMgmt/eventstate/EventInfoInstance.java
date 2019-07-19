
package com.aimir.service.system.impl.demandResponseMgmt.eventstate;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * This represents an instance of information that is associated with a DR event.  An instance of such information represents a specific type of information that was defiend to exist for DR events when the program was first set up.  For example a DR program my have bben set up to use real-time pricing as the values that are sent when DR events are issued.  Furthermore each EventInfoInstance may have a schedule of values that are valid across the entire period for then the DR event is active.  Therefore a single EventInfoInstance may have multiple values each or which are valid during a different time period.
 * 
 * <p>Java class for EventInfoInstance complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EventInfoInstance">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventInfoTypeID" type="{http://www.openadr.org/DRAS/EventState}EventInfoTypeID"/>
 *         &lt;element name="eventInfoName">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="eventInfoValues" type="{http://www.openadr.org/DRAS/EventState}EventInfoValue" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "EventInfoInstances", namespace = "http://www.openadr.org/DRAS/EventState")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventInfoInstance", namespace = "http://www.openadr.org/DRAS/EventState", propOrder = {
    "eventInfoTypeID",
    "eventInfoName",
    "eventInfoValues"
})
public class EventInfoInstance {

    @XmlElement(required = true)
    protected EventInfoTypeID eventInfoTypeID;
    @XmlElement(required = true)
    protected String eventInfoName;
    @XmlElement(required = true)
    protected List<EventInfoValue> eventInfoValues;

    /**
     * Gets the value of the eventInfoTypeID property.
     * 
     * @return
     *     possible object is
     *     {@link EventInfoTypeID }
     *     
     */
    public EventInfoTypeID getEventInfoTypeID() {
        return eventInfoTypeID;
    }

    /**
     * Sets the value of the eventInfoTypeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link EventInfoTypeID }
     *     
     */
    public void setEventInfoTypeID(EventInfoTypeID value) {
        this.eventInfoTypeID = value;
    }

    /**
     * Gets the value of the eventInfoName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventInfoName() {
        return eventInfoName;
    }

    /**
     * Sets the value of the eventInfoName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventInfoName(String value) {
        this.eventInfoName = value;
    }

    /**
     * Gets the value of the eventInfoValues property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the eventInfoValues property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEventInfoValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link EventInfoValue }
     * 
     * 
     */
    public List<EventInfoValue> getEventInfoValues() {
        if (eventInfoValues == null) {
            eventInfoValues = new ArrayList<EventInfoValue>();
        }
        return this.eventInfoValues;
    }

}
