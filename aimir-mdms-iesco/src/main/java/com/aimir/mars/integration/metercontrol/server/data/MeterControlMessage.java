package com.aimir.mars.integration.metercontrol.server.data;

import java.util.Calendar;

import org.multispeak.version_4.MultiSpeakMsgHeader;

public abstract class MeterControlMessage implements java.io.Serializable {


	protected Calendar requestedTime;
	protected Object object;
	protected String meterId;
	protected boolean sync;
	protected String trId;
	protected String command;
//	protected MultiSpeakMsgHeader multiSpeakMsgHeader;

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
	
	public void setTrId(String trId)
	{
		this.trId = trId;
	}
	public String getTrId()
	{
		return this.trId;
	}
	public String getMeterId() {
		return meterId;
	}
	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}
	public boolean isSync() {
		return sync;
	}
	public void setSync(boolean sync) {
		this.sync = sync;
	}
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	
//	public MultiSpeakMsgHeader getMultiSpeakMsgHeader() {
//		return multiSpeakMsgHeader;
//	}
//	public void setMultiSpeakMsgHeader(MultiSpeakMsgHeader multiSpeakMsgHeader) {
//		this.multiSpeakMsgHeader = multiSpeakMsgHeader;
//	}

}
