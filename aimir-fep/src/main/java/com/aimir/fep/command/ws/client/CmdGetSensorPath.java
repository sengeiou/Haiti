package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.model.device.MMIU;

/**
 * <p>
 * Java class for cmdGetSensorPath complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 *
 * <pre>
 * &lt;complexType name="cmdGetSensorPath">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *       &lt;element name="ParserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>      
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdGetSensorPath", propOrder = { 
		"mcuId",
		"parserName" 
})
public class CmdGetSensorPath {
    @XmlElement(name = "McuId")
    protected String mcuId;
	@XmlElement(name = "ParserName")
	protected String parserName;
	
	/**
     * Gets the value of the mcuId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMcuId() {
        return mcuId;
    }

    /**
     * Sets the value of the mcuId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMcuId(String value) {
        this.mcuId = value;
    }

    /**
     * Gets the value of the parserName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParserName() {
        return parserName;
    }

    /**
     * Sets the value of the parserName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParserName(String value) {
        this.parserName = value;
    }

}
