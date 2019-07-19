package com.aimir.notification;

public abstract class Trap extends Notification {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4963650026540359791L;

	/*
	 * public String getType() { return super.getType()+".trap"; }
	 */
	private String protocolName;
	private String protocolVersion;
	private String nameSpace;

	public String getProtocolName() {
		return protocolName;
	}

	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
}
