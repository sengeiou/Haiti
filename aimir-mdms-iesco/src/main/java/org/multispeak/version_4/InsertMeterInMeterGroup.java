//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.22 at 06:40:06 PM KST 
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
 *         &lt;element name="meterIDs" type="{http://www.multispeak.org/Version_4.1_Release}ArrayOfMeterIDNillable" minOccurs="0"/>
 *         &lt;element name="meterGroupID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "meterIDs",
    "meterGroupID"
})
@XmlRootElement(name = "InsertMeterInMeterGroup")
public class InsertMeterInMeterGroup {

    protected ArrayOfMeterIDNillable meterIDs;
    protected String meterGroupID;

    /**
     * Gets the value of the meterIDs property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMeterIDNillable }
     *     
     */
    public ArrayOfMeterIDNillable getMeterIDs() {
        return meterIDs;
    }

    /**
     * Sets the value of the meterIDs property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMeterIDNillable }
     *     
     */
    public void setMeterIDs(ArrayOfMeterIDNillable value) {
        this.meterIDs = value;
    }

    /**
     * Gets the value of the meterGroupID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeterGroupID() {
        return meterGroupID;
    }

    /**
     * Sets the value of the meterGroupID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeterGroupID(String value) {
        this.meterGroupID = value;
    }

}
