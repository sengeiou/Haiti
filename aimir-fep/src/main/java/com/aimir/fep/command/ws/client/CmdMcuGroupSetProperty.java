
package com.aimir.fep.command.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for cmdMcuSetProperty complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CmdMcuGroupSetProperty">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GroupId" type="{http://www.w3.org/2001/XMLSchema}Integer" minOccurs="0"/>
 *         &lt;element name="Key" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="KeyValue" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CmdMcuGroupSetProperty", propOrder = {
    "groupId",
    "key",
    "keyValue"
})
public class CmdMcuGroupSetProperty {

    @XmlElement(name = "GroupId")
    protected Integer groupId;
    @XmlElement(name = "Key")
    protected String[] key;
    @XmlElement(name = "KeyValue")
    protected String[] keyValue;
    
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	public String[] getKey() {
		return key;
	}
	public void setKey(String[] key) {
		this.key = key;
	}
	public String[] getKeyValue() {
		return keyValue;
	}
	public void setKeyValue(String[] keyValue) {
		this.keyValue = keyValue;
	}
        
        
}
