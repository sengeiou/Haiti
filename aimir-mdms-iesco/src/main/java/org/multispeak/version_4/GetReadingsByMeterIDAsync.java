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
 *         &lt;element name="meterReads" type="{http://www.multispeak.org/Version_4.1_Release}ArrayOfMeterReading1" minOccurs="0"/>
 *         &lt;element name="transactionID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "meterReads",
    "transactionID"
})
@XmlRootElement(name = "GetReadingsByMeterIDAsync")
public class GetReadingsByMeterIDAsync {

    @XmlElement(name="meterReads")
    protected ArrayOfMeterReading1 meterReads;
    @XmlElement(name="transactionID")
    protected String transactionID;

    /**
     * Gets the value of the meterReads property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMeterReading1 }
     *     
     */
    public ArrayOfMeterReading1 getMeterReads() {
        return meterReads;
    }

    /**
     * Sets the value of the meterReads property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMeterReading1 }
     *     
     */
    public void setMeterReads(ArrayOfMeterReading1 value) {
        this.meterReads = value;
    }

    /**
     * Gets the value of the transactionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionID() {
        return transactionID;
    }

    /**
     * Sets the value of the transactionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionID(String value) {
        this.transactionID = value;
    }

}
