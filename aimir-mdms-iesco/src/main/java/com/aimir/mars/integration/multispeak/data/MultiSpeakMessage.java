package com.aimir.mars.integration.multispeak.data;

import java.util.Calendar;

import org.multispeak.version_4.MultiSpeakMsgHeader;

public abstract class MultiSpeakMessage implements java.io.Serializable {

	protected Calendar requestedTime;
	protected Object object;
	protected MultiSpeakMsgHeader multiSpeakMsgHeader;

	public Calendar getRequestedTime() {
		return requestedTime;
	}
	public void setRequestedTime(Calendar requestedTime) {
		this.requestedTime = requestedTime;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public MultiSpeakMsgHeader getMultiSpeakMsgHeader() {
		return multiSpeakMsgHeader;
	}
	public void setMultiSpeakMsgHeader(MultiSpeakMsgHeader multiSpeakMsgHeader) {
		this.multiSpeakMsgHeader = multiSpeakMsgHeader;
	}

}
