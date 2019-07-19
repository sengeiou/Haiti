package com.aimir.fep.protocol.reversegprs;

import com.aimir.fep.protocol.fmp.frame.service.CommandData;

public interface ReverseGPRSAdapterMBean {
	public void start() throws Exception;
	public void stop();
	public String getName();
	public String getState();
	public int getPort();
	public CommandData cmdExecute(String targetId, CommandData cmdData) throws Exception;
}
