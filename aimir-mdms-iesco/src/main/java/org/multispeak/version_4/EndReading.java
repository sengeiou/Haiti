//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.22 at 06:40:06 PM KST 
//


package org.multispeak.version_4;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for endReading complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="endReading">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="channelIndex" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *         &lt;element name="reading" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="readingDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "endReading", propOrder = {
    "channelIndex",
    "reading",
    "readingDate"
})
public class EndReading {

    protected BigInteger channelIndex;
    protected Float reading;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar readingDate;

    /**
     * Gets the value of the channelIndex property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getChannelIndex() {
        return channelIndex;
    }

    /**
     * Sets the value of the channelIndex property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setChannelIndex(BigInteger value) {
        this.channelIndex = value;
    }

    /**
     * Gets the value of the reading property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getReading() {
        return reading;
    }

    /**
     * Sets the value of the reading property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setReading(Float value) {
        this.reading = value;
    }

    /**
     * Gets the value of the readingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getReadingDate() {
        return readingDate;
    }

    /**
     * Sets the value of the readingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setReadingDate(XMLGregorianCalendar value) {
        this.readingDate = value;
    }

}
