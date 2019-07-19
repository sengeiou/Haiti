package com.aimir.fep.meter.parser.MBusTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;

public class Control implements java.io.Serializable{
	private static Log log = LogFactory.getLog(Control.class);
	private byte[] rawData = null;
	private int control=0;
	private String controlName="";
	private String controlDescription="";

	/**
	 * @param data
	 */
	public Control(byte[] data) {
		rawData=data;
		control = DataUtil.getIntToBytes(rawData);
		if(control==0x40){
			controlName="SND_NKE";
			controlDescription="Initialiation of Slave";

		}
		else if(control==0x53 || control==0x73){
			controlName="SND_UD";
			controlDescription="Send User Data to Slave";
		}
		else if(control==0x5B || control==0x7B){
			controlName="REQ_UD2";
			controlDescription="Request for Class 2 Data";
		}
		else if(control==0x5A || control==0x7A){
			controlName="REQ_UD1";
			controlDescription="Request for Class1 Data";
		}
		else if(control==0x08 || control==0x18 || control==0x28 || control==0x38){
			controlName="RSP_UD";
			controlDescription="Data Transfer from Slave to Master after Request";
		}
	}

	/**
	 * @return
	 */
	public int getControl() {
		return control;
	}

	/**
	 * @param control
	 */
	public void setControl(int control) {
		this.control = control;
	}

	/**
	 * @return
	 */
	public String getControlName() {
		return controlName;
	}

	/**
	 * @param controlName
	 */
	public void setControlName(String controlName) {
		this.controlName = controlName;
	}

	/**
	 * @return
	 */
	public String getControlDescription() {
		return controlDescription;
	}

	/**
	 * @param controlDescription
	 */
	public void setControlDescription(String controlDescription) {
		this.controlDescription = controlDescription;
	}

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation
	 * of this object.
	 */
	public String toString()
	{
	    final String TAB = "    ";

	    StringBuffer retValue = new StringBuffer();

	    retValue.append("Control ( ")
	        .append(super.toString()).append(TAB)
	        .append("control = ").append(this.control).append(TAB)
	        .append("controlName = ").append(this.controlName).append(TAB)
	        .append("controlDescription = ").append(this.controlDescription).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
