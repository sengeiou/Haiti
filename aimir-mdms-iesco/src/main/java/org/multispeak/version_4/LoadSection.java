//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.22 at 06:41:41 PM KST 
//


package org.multispeak.version_4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import _1_release.cpsm_v4.LoadCurve;


/**
 * <p>Java class for loadSection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="loadSection">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="loadMix" type="{http://www.multispeak.org/Version_4.1_Release}eaEquipID" minOccurs="0"/>
 *         &lt;element name="loadGroup" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loadClass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loadZone" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loadDistr" type="{http://www.multispeak.org/Version_4.1_Release}loadDistr" minOccurs="0"/>
 *         &lt;element name="loadGrowth" type="{http://www.w3.org/2001/XMLSchema}float" minOccurs="0"/>
 *         &lt;element name="interruptibleType" type="{http://www.multispeak.org/Version_4.1_Release}loadInterruptibleType" minOccurs="0"/>
 *         &lt;element name="allocated" type="{http://www.multispeak.org/Version_4.1_Release}ArrayOfAllocatedLoad" minOccurs="0"/>
 *         &lt;element name="loadCurve" type="{cpsm_V4.1_Release}LoadCurve" minOccurs="0"/>
 *         &lt;element name="loadGroupID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "loadSection", propOrder = {
    "loadMix",
    "loadGroup",
    "loadClass",
    "loadZone",
    "loadDistr",
    "loadGrowth",
    "interruptibleType",
    "allocated",
    "loadCurve",
    "loadGroupID"
})
public class LoadSection {

    protected EaEquipID loadMix;
    protected String loadGroup;
    protected String loadClass;
    protected String loadZone;
    protected LoadDistr loadDistr;
    protected Float loadGrowth;
    protected LoadInterruptibleType interruptibleType;
    protected ArrayOfAllocatedLoad allocated;
    protected LoadCurve loadCurve;
    protected String loadGroupID;

    /**
     * Gets the value of the loadMix property.
     * 
     * @return
     *     possible object is
     *     {@link EaEquipID }
     *     
     */
    public EaEquipID getLoadMix() {
        return loadMix;
    }

    /**
     * Sets the value of the loadMix property.
     * 
     * @param value
     *     allowed object is
     *     {@link EaEquipID }
     *     
     */
    public void setLoadMix(EaEquipID value) {
        this.loadMix = value;
    }

    /**
     * Gets the value of the loadGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoadGroup() {
        return loadGroup;
    }

    /**
     * Sets the value of the loadGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoadGroup(String value) {
        this.loadGroup = value;
    }

    /**
     * Gets the value of the loadClass property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoadClass() {
        return loadClass;
    }

    /**
     * Sets the value of the loadClass property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoadClass(String value) {
        this.loadClass = value;
    }

    /**
     * Gets the value of the loadZone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoadZone() {
        return loadZone;
    }

    /**
     * Sets the value of the loadZone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoadZone(String value) {
        this.loadZone = value;
    }

    /**
     * Gets the value of the loadDistr property.
     * 
     * @return
     *     possible object is
     *     {@link LoadDistr }
     *     
     */
    public LoadDistr getLoadDistr() {
        return loadDistr;
    }

    /**
     * Sets the value of the loadDistr property.
     * 
     * @param value
     *     allowed object is
     *     {@link LoadDistr }
     *     
     */
    public void setLoadDistr(LoadDistr value) {
        this.loadDistr = value;
    }

    /**
     * Gets the value of the loadGrowth property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getLoadGrowth() {
        return loadGrowth;
    }

    /**
     * Sets the value of the loadGrowth property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setLoadGrowth(Float value) {
        this.loadGrowth = value;
    }

    /**
     * Gets the value of the interruptibleType property.
     * 
     * @return
     *     possible object is
     *     {@link LoadInterruptibleType }
     *     
     */
    public LoadInterruptibleType getInterruptibleType() {
        return interruptibleType;
    }

    /**
     * Sets the value of the interruptibleType property.
     * 
     * @param value
     *     allowed object is
     *     {@link LoadInterruptibleType }
     *     
     */
    public void setInterruptibleType(LoadInterruptibleType value) {
        this.interruptibleType = value;
    }

    /**
     * Gets the value of the allocated property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfAllocatedLoad }
     *     
     */
    public ArrayOfAllocatedLoad getAllocated() {
        return allocated;
    }

    /**
     * Sets the value of the allocated property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfAllocatedLoad }
     *     
     */
    public void setAllocated(ArrayOfAllocatedLoad value) {
        this.allocated = value;
    }

    /**
     * Gets the value of the loadCurve property.
     * 
     * @return
     *     possible object is
     *     {@link LoadCurve }
     *     
     */
    public LoadCurve getLoadCurve() {
        return loadCurve;
    }

    /**
     * Sets the value of the loadCurve property.
     * 
     * @param value
     *     allowed object is
     *     {@link LoadCurve }
     *     
     */
    public void setLoadCurve(LoadCurve value) {
        this.loadCurve = value;
    }

    /**
     * Gets the value of the loadGroupID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLoadGroupID() {
        return loadGroupID;
    }

    /**
     * Sets the value of the loadGroupID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLoadGroupID(String value) {
        this.loadGroupID = value;
    }

}
