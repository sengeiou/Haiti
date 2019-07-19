package com.aimir.fep.mcu.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * MCU Schedule Result
 * @author TEN
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mcuScheduleResult", propOrder = {
	"sysId",  
    "result"
})
public class McuScheduleResult implements java.io.Serializable  {

	/**
	 * auto generated
	 */
	private static final long serialVersionUID = -2250551954021130653L;
	
	private String sysId = null;
	private String result = null;	
	
	public McuScheduleResult() {
		sysId = new String();
		result = new String();
	}
	
	public McuScheduleResult(String sysId, String result) {
		this.sysId = sysId;
		this.result = result;
	}
	
	public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "mcuScheduleResult [sysId=" + sysId + ", result=" + result + "]";
	}
	
	
}
