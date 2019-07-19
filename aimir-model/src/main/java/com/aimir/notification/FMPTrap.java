package com.aimir.notification;

import java.util.Iterator;

import org.apache.commons.codec.binary.Hex;

public class FMPTrap extends Trap {

	private static final long serialVersionUID = -8836119493951922771L;
	
	private String mcuId;
	
	public String getMcuId() {
		return mcuId;
	}
	public void setMcuId(String mcuId) {
		this.mcuId = mcuId;
	}

	private String code;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	private String sourceType;
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String srcType) {
		sourceType = srcType;
	}

	private String sourceId;
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String srcId) {
		sourceId = srcId;
	}

	private String timeStamp;
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String ts) {
		timeStamp = ts;
	}

	private VarBinds vb;
	public VarBinds getVarBinds() {
		return vb;
	}
	public void setVarBinds(VarBinds vb) {
		this.vb = vb;
	}

	private String ipAddr;
	public String getIpAddr() {
	    return ipAddr;
	}
	public void setIpAddr(String ipAddr) {
	    this.ipAddr = ipAddr;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FMPTrap [code=");
		builder.append(code);
		builder.append(", \\n ipAddr=");
		builder.append(ipAddr);
		builder.append(", \\n mcuId=");
		builder.append(mcuId);
		builder.append(", \\n sourceId=");
		builder.append(sourceId);
		builder.append(", \\n sourceType=");
		builder.append(sourceType);
		builder.append(", \\n timeStamp=");
		builder.append(timeStamp);
		builder.append(", \\n vb={");
		Object key = null;
		Object value = null;
		for (Iterator i = vb.keySet().iterator(); i.hasNext();) {
		    key = i.next();
		    value = vb.get(key);
		    
		    if (key instanceof byte[]) {
		        builder.append("\\nvar=" + Hex.encodeHexString((byte[])key) + "");
		    }
		    else 
		        builder.append("var=" + key + "=");
		    
		    if (value instanceof byte[]) {
		        builder.append("value=" + Hex.encodeHexString((byte[])value) + ",");
		    }
		    else
		        builder.append("value=" + value + ",");
		}
		builder.append("}]");
		return builder.toString();
	}
}
