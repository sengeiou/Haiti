
package com.aimir.fep.command.ws.client;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.mcu.data.McuPropertyResult;


/**
 * <p>Java class for cmdMcuSetPropertyResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CmdMcuGroupSetPropertyResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://server.ws.command.fep.aimir.com/}anyType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdMcuGroupSetPropertyResponse", propOrder = {
        "_return"
})
public class CmdMcuGroupSetPropertyResponse {

	@XmlElement(name = "return")
    protected List<McuPropertyResult> _return;

	public List<McuPropertyResult> get_return() {
		return _return;
	}

	public void set_return(List<McuPropertyResult> _return) {
		this._return = _return;
	}
	

}