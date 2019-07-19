//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.07.22 at 06:41:41 PM KST 
//


package org.multispeak.version_4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for ZigBeeCancelMessage complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ZigBeeCancelMessage">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.multispeak.org/Version_4.1_Release}mspObject">
 *       &lt;sequence>
 *         &lt;element name="messageID" type="{http://www.w3.org/2001/XMLSchema}unsignedInt" minOccurs="0"/>
 *         &lt;element name="messageControl" type="{http://www.w3.org/2001/XMLSchema}hexBinary" minOccurs="0"/>
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
@XmlType(name = "ZigBeeCancelMessage", propOrder = {
    "messageID",
    "messageControl"
})
public class ZigBeeCancelMessage
    extends MspObject
{

    @XmlSchemaType(name = "unsignedInt")
    protected Long messageID;
    @XmlElement(type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    @XmlSchemaType(name = "hexBinary")
    protected byte[] messageControl;

    /**
     * Gets the value of the messageID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMessageID() {
        return messageID;
    }

    /**
     * Sets the value of the messageID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMessageID(Long value) {
        this.messageID = value;
    }

    /**
     * Gets the value of the messageControl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public byte[] getMessageControl() {
        return messageControl;
    }

    /**
     * Sets the value of the messageControl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageControl(byte[] value) {
        this.messageControl = value;
    }

}
