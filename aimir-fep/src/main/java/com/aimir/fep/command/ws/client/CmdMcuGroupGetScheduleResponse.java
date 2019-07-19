package com.aimir.fep.command.ws.client;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.mcu.data.ScheduleData;


/**
 * <p>Java class for cmdSendEventByFepResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="cmdMcuGroupGetScheduleResponse">
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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "cmdMcuGroupGetScheduleResponse", propOrder = {
		"_return"
})
public class CmdMcuGroupGetScheduleResponse {

	@XmlElement(name = "return")
    protected List<ScheduleData> _return;

	public List<ScheduleData> get_return() {
		return _return;
	}

	public void set_return(List<ScheduleData> _return) {
		this._return = _return;
	}


}