//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.22 at 06:40:55 PM KST 
//


package org.multispeak.version_4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for circuitElementAndDistance complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="circuitElementAndDistance">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="circuitElement" type="{http://www.multispeak.org/Version_4.1_Release}circuitElement" minOccurs="0"/>
 *         &lt;element name="distance" type="{http://www.multispeak.org/Version_4.1_Release}lengthUnitValue" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "circuitElementAndDistance", propOrder = {
    "circuitElement",
    "distance"
})
public class CircuitElementAndDistance {

    protected CircuitElement circuitElement;
    protected LengthUnitValue distance;

    /**
     * Gets the value of the circuitElement property.
     * 
     * @return
     *     possible object is
     *     {@link CircuitElement }
     *     
     */
    public CircuitElement getCircuitElement() {
        return circuitElement;
    }

    /**
     * Sets the value of the circuitElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link CircuitElement }
     *     
     */
    public void setCircuitElement(CircuitElement value) {
        this.circuitElement = value;
    }

    /**
     * Gets the value of the distance property.
     * 
     * @return
     *     possible object is
     *     {@link LengthUnitValue }
     *     
     */
    public LengthUnitValue getDistance() {
        return distance;
    }

    /**
     * Sets the value of the distance property.
     * 
     * @param value
     *     allowed object is
     *     {@link LengthUnitValue }
     *     
     */
    public void setDistance(LengthUnitValue value) {
        this.distance = value;
    }

}
