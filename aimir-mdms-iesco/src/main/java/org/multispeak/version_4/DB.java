//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.22 at 06:40:06 PM KST 
//


package org.multispeak.version_4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for dB complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="dB">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="chs" type="{http://www.multispeak.org/Version_4.1_Release}ArrayOfCH" minOccurs="0"/>
 *         &lt;element name="cS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dB", propOrder = {
    "chs",
    "cs"
})
public class DB {

    protected ArrayOfCH chs;
    @XmlElement(name = "cS")
    protected String cs;

    /**
     * Gets the value of the chs property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCH }
     *     
     */
    public ArrayOfCH getChs() {
        return chs;
    }

    /**
     * Sets the value of the chs property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCH }
     *     
     */
    public void setChs(ArrayOfCH value) {
        this.chs = value;
    }

    /**
     * Gets the value of the cs property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCS() {
        return cs;
    }

    /**
     * Sets the value of the cs property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCS(String value) {
        this.cs = value;
    }

}
