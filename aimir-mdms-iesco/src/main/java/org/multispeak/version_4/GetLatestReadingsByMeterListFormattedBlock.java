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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="formattedBlockTemplateName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fieldName" type="{http://www.multispeak.org/Version_4.1_Release}ArrayOfString" minOccurs="0"/>
 *         &lt;element name="lastReceived" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "startDate",
    "endDate",
    "formattedBlockTemplateName",
    "fieldName",
    "lastReceived"
})
@XmlRootElement(name = "GetLatestReadingsByMeterListFormattedBlock")
public class GetLatestReadingsByMeterListFormattedBlock {

    protected ArrayOfMeterIDNillable meterIDs;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar startDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar endDate;
    protected String formattedBlockTemplateName;
    protected ArrayOfString fieldName;
    protected String lastReceived;

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
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStartDate(XMLGregorianCalendar value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setEndDate(XMLGregorianCalendar value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the formattedBlockTemplateName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFormattedBlockTemplateName() {
        return formattedBlockTemplateName;
    }

    /**
     * Sets the value of the formattedBlockTemplateName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFormattedBlockTemplateName(String value) {
        this.formattedBlockTemplateName = value;
    }

    /**
     * Gets the value of the fieldName property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getFieldName() {
        return fieldName;
    }

    /**
     * Sets the value of the fieldName property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setFieldName(ArrayOfString value) {
        this.fieldName = value;
    }

    /**
     * Gets the value of the lastReceived property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastReceived() {
        return lastReceived;
    }

    /**
     * Sets the value of the lastReceived property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastReceived(String value) {
        this.lastReceived = value;
    }

}
