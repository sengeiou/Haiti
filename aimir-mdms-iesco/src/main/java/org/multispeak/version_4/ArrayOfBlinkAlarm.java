//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.22 at 06:40:55 PM KST 
//


package org.multispeak.version_4;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfBlinkAlarm complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfBlinkAlarm">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="blinkAlarm" type="{http://www.multispeak.org/Version_4.1_Release}blinkAlarm" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfBlinkAlarm", propOrder = {
    "blinkAlarm"
})
public class ArrayOfBlinkAlarm {

    @XmlElement(nillable = true)
    protected List<BlinkAlarm> blinkAlarm;

    /**
     * Gets the value of the blinkAlarm property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the blinkAlarm property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBlinkAlarm().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BlinkAlarm }
     * 
     * 
     */
    public List<BlinkAlarm> getBlinkAlarm() {
        if (blinkAlarm == null) {
            blinkAlarm = new ArrayList<BlinkAlarm>();
        }
        return this.blinkAlarm;
    }

}
