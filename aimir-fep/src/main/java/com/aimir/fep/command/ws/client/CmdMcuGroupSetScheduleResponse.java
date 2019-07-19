package com.aimir.fep.command.ws.client;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.mcu.data.McuScheduleResult;

/**
 * <p>Java class for CmdMcuGetScheduleResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CmdMcuSetScheduleResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://www.w3.org/2001/XMLSchema}anyType " minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdMcuGroupSetScheduleResponse" , propOrder = {
		"_return"
})
public class CmdMcuGroupSetScheduleResponse {

	@XmlElement(name = "return")
    protected List<McuScheduleResult> _return;

	public List<McuScheduleResult> get_return() {
		return _return;
	}

	public void set_return(List<McuScheduleResult> _return) {
		this._return = _return;
	}
	
}
