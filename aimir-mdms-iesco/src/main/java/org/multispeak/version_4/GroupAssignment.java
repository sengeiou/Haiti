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


/**
 * <p>Java class for groupAssignment complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="groupAssignment">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.multispeak.org/Version_4.1_Release}mspObject">
 *       &lt;sequence>
 *         &lt;element name="workGroupID" type="{http://www.multispeak.org/Version_4.1_Release}objectRef" minOccurs="0"/>
 *         &lt;element name="taskIdentifier" type="{http://www.multispeak.org/Version_4.1_Release}multiPartIdentifier" minOccurs="0"/>
 *         &lt;element name="workSchedule" type="{http://www.multispeak.org/Version_4.1_Release}workSchedule" minOccurs="0"/>
 *         &lt;element name="constraints" type="{http://www.multispeak.org/Version_4.1_Release}constraints" minOccurs="0"/>
 *         &lt;element name="actionBy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timeToLive" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;anyAttribute/>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "groupAssignment", propOrder = {
    "workGroupID",
    "taskIdentifier",
    "workSchedule",
    "constraints",
    "actionBy",
    "timeToLive"
})
public class GroupAssignment
    extends MspObject
{

    protected ObjectRef workGroupID;
    protected MultiPartIdentifier taskIdentifier;
    protected WorkSchedule workSchedule;
    protected Constraints constraints;
    protected String actionBy;
    protected String timeToLive;

    /**
     * Gets the value of the workGroupID property.
     * 
     * @return
     *     possible object is
     *     {@link ObjectRef }
     *     
     */
    public ObjectRef getWorkGroupID() {
        return workGroupID;
    }

    /**
     * Sets the value of the workGroupID property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectRef }
     *     
     */
    public void setWorkGroupID(ObjectRef value) {
        this.workGroupID = value;
    }

    /**
     * Gets the value of the taskIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link MultiPartIdentifier }
     *     
     */
    public MultiPartIdentifier getTaskIdentifier() {
        return taskIdentifier;
    }

    /**
     * Sets the value of the taskIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link MultiPartIdentifier }
     *     
     */
    public void setTaskIdentifier(MultiPartIdentifier value) {
        this.taskIdentifier = value;
    }

    /**
     * Gets the value of the workSchedule property.
     * 
     * @return
     *     possible object is
     *     {@link WorkSchedule }
     *     
     */
    public WorkSchedule getWorkSchedule() {
        return workSchedule;
    }

    /**
     * Sets the value of the workSchedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link WorkSchedule }
     *     
     */
    public void setWorkSchedule(WorkSchedule value) {
        this.workSchedule = value;
    }

    /**
     * Gets the value of the constraints property.
     * 
     * @return
     *     possible object is
     *     {@link Constraints }
     *     
     */
    public Constraints getConstraints() {
        return constraints;
    }

    /**
     * Sets the value of the constraints property.
     * 
     * @param value
     *     allowed object is
     *     {@link Constraints }
     *     
     */
    public void setConstraints(Constraints value) {
        this.constraints = value;
    }

    /**
     * Gets the value of the actionBy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getActionBy() {
        return actionBy;
    }

    /**
     * Sets the value of the actionBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setActionBy(String value) {
        this.actionBy = value;
    }

    /**
     * Gets the value of the timeToLive property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimeToLive() {
        return timeToLive;
    }

    /**
     * Sets the value of the timeToLive property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimeToLive(String value) {
        this.timeToLive = value;
    }

}
