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
 * <p>Java class for worker complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="worker">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.multispeak.org/Version_4.1_Release}mspPerson">
 *       &lt;sequence>
 *         &lt;element name="isEmplyee" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="skillsList" type="{http://www.multispeak.org/Version_4.1_Release}ArrayOfSkillIDs" minOccurs="0"/>
 *         &lt;element name="skills" type="{http://www.multispeak.org/Version_4.1_Release}ArrayOfSkill" minOccurs="0"/>
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
@XmlType(name = "worker", propOrder = {
    "isEmplyee",
    "skillsList",
    "skills"
})
public class Worker
    extends MspPerson
{

    protected Boolean isEmplyee;
    protected ArrayOfSkillIDs skillsList;
    protected ArrayOfSkill skills;

    /**
     * Gets the value of the isEmplyee property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsEmplyee() {
        return isEmplyee;
    }

    /**
     * Sets the value of the isEmplyee property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsEmplyee(Boolean value) {
        this.isEmplyee = value;
    }

    /**
     * Gets the value of the skillsList property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSkillIDs }
     *     
     */
    public ArrayOfSkillIDs getSkillsList() {
        return skillsList;
    }

    /**
     * Sets the value of the skillsList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSkillIDs }
     *     
     */
    public void setSkillsList(ArrayOfSkillIDs value) {
        this.skillsList = value;
    }

    /**
     * Gets the value of the skills property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSkill }
     *     
     */
    public ArrayOfSkill getSkills() {
        return skills;
    }

    /**
     * Sets the value of the skills property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSkill }
     *     
     */
    public void setSkills(ArrayOfSkill value) {
        this.skills = value;
    }

}
