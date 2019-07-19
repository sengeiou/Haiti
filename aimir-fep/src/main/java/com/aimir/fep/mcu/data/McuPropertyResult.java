package com.aimir.fep.mcu.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * MCU Property Result
 * @author TEN
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mcuPropertyResult", propOrder = {
	"sysId",  
    "result"
})
public class McuPropertyResult implements java.io.Serializable {

	/**
	 * auto generated 
	 */
	private static final long serialVersionUID = 394882806975722868L;

	private String sysId = null;
	private String result = null;
	
	public McuPropertyResult() {
		sysId = new String();
		result = new String();
	}
	
	public McuPropertyResult(String sysId, String result) {
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
		return "McuPropertyResult [sysId=" + sysId + ", result=" + result + "]";
	}
	
}
