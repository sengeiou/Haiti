//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.22 at 06:38:23 PM KST 
//


package org.multispeak.version_4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element name="GetMeterByMeterIDResult" type="{http://www.multispeak.org/Version_4.1_Release}meters" minOccurs="0"/>
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
    "getMeterByMeterIDResult"
})
@XmlRootElement(name = "GetMeterByMeterIDResponse")
public class GetMeterByMeterIDResponse {

    @XmlElement(name = "GetMeterByMeterIDResult")
    protected Meters getMeterByMeterIDResult;

    /**
     * Gets the value of the getMeterByMeterIDResult property.
     * 
     * @return
     *     possible object is
     *     {@link Meters }
     *     
     */
    public Meters getGetMeterByMeterIDResult() {
        return getMeterByMeterIDResult;
    }

    /**
     * Sets the value of the getMeterByMeterIDResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Meters }
     *     
     */
    public void setGetMeterByMeterIDResult(Meters value) {
        this.getMeterByMeterIDResult = value;
    }

}
