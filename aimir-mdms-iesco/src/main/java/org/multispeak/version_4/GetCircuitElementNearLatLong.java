//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.22 at 06:40:55 PM KST 
//


package org.multispeak.version_4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="latitude" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="longitude" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="tolerance" type="{http://www.multispeak.org/Version_4.1_Release}lengthUnitValue" minOccurs="0"/>
 *         &lt;element name="numCEs" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "latitude",
    "longitude",
    "tolerance",
    "numCEs"
})
@XmlRootElement(name = "GetCircuitElementNearLatLong")
public class GetCircuitElementNearLatLong {

    protected double latitude;
    protected double longitude;
    protected LengthUnitValue tolerance;
    protected int numCEs;

    /**
     * Gets the value of the latitude property.
     * 
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the value of the latitude property.
     * 
     */
    public void setLatitude(double value) {
        this.latitude = value;
    }

    /**
     * Gets the value of the longitude property.
     * 
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the value of the longitude property.
     * 
     */
    public void setLongitude(double value) {
        this.longitude = value;
    }

    /**
     * Gets the value of the tolerance property.
     * 
     * @return
     *     possible object is
     *     {@link LengthUnitValue }
     *     
     */
    public LengthUnitValue getTolerance() {
        return tolerance;
    }

    /**
     * Sets the value of the tolerance property.
     * 
     * @param value
     *     allowed object is
     *     {@link LengthUnitValue }
     *     
     */
    public void setTolerance(LengthUnitValue value) {
        this.tolerance = value;
    }

    /**
     * Gets the value of the numCEs property.
     * 
     */
    public int getNumCEs() {
        return numCEs;
    }

    /**
     * Sets the value of the numCEs property.
     * 
     */
    public void setNumCEs(int value) {
        this.numCEs = value;
    }

}
