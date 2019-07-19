//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.22 at 06:41:41 PM KST 
//


package _1_release.cpsm_v4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Bay complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Bay">
 *   &lt;complexContent>
 *     &lt;extension base="{cpsm_V4.1_Release}EquipmentContainer">
 *       &lt;sequence>
 *         &lt;element name="voltageLevelID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="breakerConfiguration" type="{cpsm_V4.1_Release}BreakerConfiguration" minOccurs="0"/>
 *         &lt;element name="busbarConfiguration" type="{cpsm_V4.1_Release}BusbarConfiguration" minOccurs="0"/>
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
@XmlType(name = "Bay", propOrder = {
    "voltageLevelID",
    "breakerConfiguration",
    "busbarConfiguration"
})
public class Bay
    extends EquipmentContainer
{

    protected String voltageLevelID;
    protected BreakerConfiguration breakerConfiguration;
    protected BusbarConfiguration busbarConfiguration;

    /**
     * Gets the value of the voltageLevelID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVoltageLevelID() {
        return voltageLevelID;
    }

    /**
     * Sets the value of the voltageLevelID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVoltageLevelID(String value) {
        this.voltageLevelID = value;
    }

    /**
     * Gets the value of the breakerConfiguration property.
     * 
     * @return
     *     possible object is
     *     {@link BreakerConfiguration }
     *     
     */
    public BreakerConfiguration getBreakerConfiguration() {
        return breakerConfiguration;
    }

    /**
     * Sets the value of the breakerConfiguration property.
     * 
     * @param value
     *     allowed object is
     *     {@link BreakerConfiguration }
     *     
     */
    public void setBreakerConfiguration(BreakerConfiguration value) {
        this.breakerConfiguration = value;
    }

    /**
     * Gets the value of the busbarConfiguration property.
     * 
     * @return
     *     possible object is
     *     {@link BusbarConfiguration }
     *     
     */
    public BusbarConfiguration getBusbarConfiguration() {
        return busbarConfiguration;
    }

    /**
     * Sets the value of the busbarConfiguration property.
     * 
     * @param value
     *     allowed object is
     *     {@link BusbarConfiguration }
     *     
     */
    public void setBusbarConfiguration(BusbarConfiguration value) {
        this.busbarConfiguration = value;
    }

}
