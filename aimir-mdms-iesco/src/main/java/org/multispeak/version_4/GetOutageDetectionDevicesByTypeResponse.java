//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.22 at 06:41:41 PM KST 
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
 *         &lt;element name="GetOutageDetectionDevicesByTypeResult" type="{http://www.multispeak.org/Version_4.1_Release}ArrayOfOutageDetectionDevice" minOccurs="0"/>
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
    "getOutageDetectionDevicesByTypeResult"
})
@XmlRootElement(name = "GetOutageDetectionDevicesByTypeResponse")
public class GetOutageDetectionDevicesByTypeResponse {

    @XmlElement(name = "GetOutageDetectionDevicesByTypeResult")
    protected ArrayOfOutageDetectionDevice getOutageDetectionDevicesByTypeResult;

    /**
     * Gets the value of the getOutageDetectionDevicesByTypeResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfOutageDetectionDevice }
     *     
     */
    public ArrayOfOutageDetectionDevice getGetOutageDetectionDevicesByTypeResult() {
        return getOutageDetectionDevicesByTypeResult;
    }

    /**
     * Sets the value of the getOutageDetectionDevicesByTypeResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfOutageDetectionDevice }
     *     
     */
    public void setGetOutageDetectionDevicesByTypeResult(ArrayOfOutageDetectionDevice value) {
        this.getOutageDetectionDevicesByTypeResult = value;
    }

}
