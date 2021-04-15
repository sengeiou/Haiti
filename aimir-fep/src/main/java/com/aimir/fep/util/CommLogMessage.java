package com.aimir.fep.util;

import java.io.Serializable;

import com.aimir.fep.logger.snowflake.SnowflakeGeneration;
import com.aimir.model.device.CommLog;

public class CommLogMessage implements Serializable{
	
	private CommLog commLog;
	private String sequenceLog;
	
	public CommLogMessage(CommLog commLog) {
		this.commLog = commLog;
		sequenceLog = SnowflakeGeneration.getId();
	}

	public CommLog getCommLog() {
		return commLog;
	}

	public void setCommLog(CommLog commLog) {
		this.commLog = commLog;
	}

	public String getSequenceLog() {
		return sequenceLog;
	}

	public void setSequenceLog(String sequenceLog) {
		this.sequenceLog = sequenceLog;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("sequenceLog : " + sequenceLog);
		builder.append(", " + commLog.toString());
		
		return builder.toString();
	}
	
	
}
