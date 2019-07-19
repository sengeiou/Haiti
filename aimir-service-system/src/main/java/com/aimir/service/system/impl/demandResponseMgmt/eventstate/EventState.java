
package com.aimir.service.system.impl.demandResponseMgmt.eventstate;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * This represents the state of a single DR Event.  There will be one of these senT for each DR Event that is either pending or active.  If there are no DR Events pending or active then there will be one of these sent with the EventStatus field of the simpleDRModeData set to "NONE".  Note that when the EventStatus is set to NONE then some of the elements and attributes of this entity are not applicable.  See the documentation for each element and attribute to see which are not applicable when the EventStatus is set to NONE.
 * 
 * 
 * <p>Java class for EventState complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EventState">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="simpleDRModeData" type="{http://www.openadr.org/DRAS/EventState}SimpleClientEventData"/>
 *         &lt;element name="drEventData" type="{http://www.openadr.org/DRAS/EventState}SmartClientDREventData" minOccurs="0"/>
 *         &lt;element name="customData" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="generalInfoInstances" type="{http://www.openadr.org/DRAS/EventState}GeneralInfoInstance" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="programName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="eventModNumber" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *       &lt;attribute name="eventIdentifier" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="drasClientID" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="eventStateID" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" />
 *       &lt;attribute name="schemaVersion" type="{http://www.w3.org/2001/XMLSchema}string" default="1.0" />
 *       &lt;attribute name="drasName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="testEvent" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="offLine" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EventState", propOrder = {
    "simpleDRModeData",
    "drEventData",
    "customData"
})
public class EventState {

    @XmlElement(required = true)
    protected SimpleClientEventData simpleDRModeData;
    @XmlElementRef(name = "drEventData", namespace = "http://www.openadr.org/DRAS/EventState", type = JAXBElement.class)
    protected JAXBElement<SmartClientDREventData> drEventData;
    @XmlElementRef(name = "customData", namespace = "http://www.openadr.org/DRAS/EventState", type = JAXBElement.class)
    protected JAXBElement<EventState.CustomData> customData;
    @XmlAttribute(required = true)
    protected String programName;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "unsignedInt")
    protected long eventModNumber;
    @XmlAttribute(required = true)
    protected String eventIdentifier;
    @XmlAttribute(required = true)
    protected String drasClientID;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "unsignedInt")
    protected long eventStateID;
    @XmlAttribute
    protected String schemaVersion;
    @XmlAttribute(required = true)
    protected String drasName;
    @XmlAttribute
    protected Boolean testEvent;
    @XmlAttribute
    protected Boolean offLine;

    /**
     * Gets the value of the simpleDRModeData property.
     * 
     * @return
     *     possible object is
     *     {@link SimpleClientEventData }
     *     
     */
    public SimpleClientEventData getSimpleDRModeData() {
        return simpleDRModeData;
    }

    /**
     * Sets the value of the simpleDRModeData property.
     * 
     * @param value
     *     allowed object is
     *     {@link SimpleClientEventData }
     *     
     */
    public void setSimpleDRModeData(SimpleClientEventData value) {
        this.simpleDRModeData = value;
    }

    /**
     * Gets the value of the drEventData property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link SmartClientDREventData }{@code >}
     *     
     */
    public JAXBElement<SmartClientDREventData> getDrEventData() {
        return drEventData;
    }

    /**
     * Sets the value of the drEventData property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link SmartClientDREventData }{@code >}
     *     
     */
    public void setDrEventData(JAXBElement<SmartClientDREventData> value) {
        this.drEventData = ((JAXBElement<SmartClientDREventData> ) value);
    }

    /**
     * Gets the value of the customData property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link EventState.CustomData }{@code >}
     *     
     */
    public JAXBElement<EventState.CustomData> getCustomData() {
        return customData;
    }

    /**
     * Sets the value of the customData property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link EventState.CustomData }{@code >}
     *     
     */
    public void setCustomData(JAXBElement<EventState.CustomData> value) {
        this.customData = ((JAXBElement<EventState.CustomData> ) value);
    }

    /**
     * Gets the value of the programName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProgramName() {
        return programName;
    }

    /**
     * Sets the value of the programName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProgramName(String value) {
        this.programName = value;
    }

    /**
     * Gets the value of the eventModNumber property.
     * 
     */
    public long getEventModNumber() {
        return eventModNumber;
    }

    /**
     * Sets the value of the eventModNumber property.
     * 
     */
    public void setEventModNumber(long value) {
        this.eventModNumber = value;
    }

    /**
     * Gets the value of the eventIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEventIdentifier() {
        return eventIdentifier;
    }

    /**
     * Sets the value of the eventIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventIdentifier(String value) {
        this.eventIdentifier = value;
    }

    /**
     * Gets the value of the drasClientID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDrasClientID() {
        return drasClientID;
    }

    /**
     * Sets the value of the drasClientID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDrasClientID(String value) {
        this.drasClientID = value;
    }

    /**
     * Gets the value of the eventStateID property.
     * 
     */
    public long getEventStateID() {
        return eventStateID;
    }

    /**
     * Sets the value of the eventStateID property.
     * 
     */
    public void setEventStateID(long value) {
        this.eventStateID = value;
    }

    /**
     * Gets the value of the schemaVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchemaVersion() {
        if (schemaVersion == null) {
            return "1.0";
        } else {
            return schemaVersion;
        }
    }

    /**
     * Sets the value of the schemaVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchemaVersion(String value) {
        this.schemaVersion = value;
    }

    /**
     * Gets the value of the drasName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDrasName() {
        return drasName;
    }

    /**
     * Sets the value of the drasName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDrasName(String value) {
        this.drasName = value;
    }

    /**
     * Gets the value of the testEvent property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isTestEvent() {
        return testEvent;
    }

    /**
     * Sets the value of the testEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setTestEvent(Boolean value) {
        this.testEvent = value;
    }

    /**
     * Gets the value of the offLine property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOffLine() {
        return offLine;
    }

    /**
     * Sets the value of the offLine property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOffLine(Boolean value) {
        this.offLine = value;
    }


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
     *         &lt;element name="generalInfoInstances" type="{http://www.openadr.org/DRAS/EventState}GeneralInfoInstance" maxOccurs="unbounded" minOccurs="0"/>
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
        "generalInfoInstances"
    })
    public static class CustomData {

        @XmlElement(nillable = true)
        protected List<GeneralInfoInstance> generalInfoInstances;

        /**
         * Gets the value of the generalInfoInstances property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the generalInfoInstances property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getGeneralInfoInstances().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link GeneralInfoInstance }
         * 
         * 
         */
        public List<GeneralInfoInstance> getGeneralInfoInstances() {
            if (generalInfoInstances == null) {
                generalInfoInstances = new ArrayList<GeneralInfoInstance>();
            }
            return this.generalInfoInstances;
        }

    }

}
