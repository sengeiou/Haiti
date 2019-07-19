
package com.aimir.service.system.impl.demandResponseMgmt.eventstate;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * This is an instance of a value for the information that is associated with a DR event when it is issued by the Utility.  This instance includes a number value for the information and a time parameter that defines a time slot with the DR events active period for when this value is valid.  By putting together sequences of these types there can be a schedule of values that are valid across the entire active period of the DR event.
 * 
 * <p>Java class for EventInfoValue complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EventInfoValue">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="timeOffset" type="{http://www.w3.org/2001/XMLSchema}unsignedInt"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlRootElement(name = "EventInfoValue", namespace = "http://www.openadr.org/DRAS/EventState")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventInfoValue", propOrder = {
    "value",
    "timeOffset"
})
public class EventInfoValue {

    @XmlElement(required = true)
    protected BigDecimal value;
    @XmlSchemaType(name = "unsignedInt")
    protected long timeOffset;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Gets the value of the timeOffset property.
     * 
     */
    public long getTimeOffset() {
        return timeOffset;
    }

    /**
     * Sets the value of the timeOffset property.
     * 
     */
    public void setTimeOffset(long value) {
        this.timeOffset = value;
    }

}
