
package com.aimir.service.system.impl.demandResponseMgmt.eventstate;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * This is an instance of the general information that is reserved for use outside the scope of the normal EventState information.
 * 
 * <p>Java class for GeneralInfoInstance complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GeneralInfoInstance">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="infoName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="infoType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="infoValues" type="{http://www.openadr.org/DRAS/EventState}GeneralInfoValue" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GeneralInfoInstance", propOrder = {
    "infoName",
    "infoType",
    "infoValues"
})
public class GeneralInfoInstance {

    @XmlElement(required = true)
    protected String infoName;
    @XmlElement(required = true)
    protected String infoType;
    @XmlElement(required = true)
    protected List<GeneralInfoValue> infoValues;

    /**
     * Gets the value of the infoName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInfoName() {
        return infoName;
    }

    /**
     * Sets the value of the infoName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInfoName(String value) {
        this.infoName = value;
    }

    /**
     * Gets the value of the infoType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInfoType() {
        return infoType;
    }

    /**
     * Sets the value of the infoType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInfoType(String value) {
        this.infoType = value;
    }

    /**
     * Gets the value of the infoValues property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the infoValues property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInfoValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GeneralInfoValue }
     * 
     * 
     */
    public List<GeneralInfoValue> getInfoValues() {
        if (infoValues == null) {
            infoValues = new ArrayList<GeneralInfoValue>();
        }
        return this.infoValues;
    }

}
