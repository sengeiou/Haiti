/**
 * (@)# MultiSession.java
 *
 * 2016. 7. 24.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.protocol.nip.client.multisession;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.mina.core.session.IoSession;
import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.scandium.DTLSConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.bypass.BypassDevice;
import com.aimir.fep.protocol.nip.client.bypass.BypassClient;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class MultiSession {
	private static Logger logger = LoggerFactory.getLogger(MultiSession.class);
	
	private Object sessionId;
	private IoSession ioSession;
	private InetSocketAddress peer;
	private DTLSConnector dtlsConnector;
	private BypassDevice bypassDevice;
	private BypassClient bypassClient;
	private Map<String, Object> attribute = new HashMap<String, Object>();

	public MultiSession(IoSession session) {
		this.sessionId = session.getId();
		this.ioSession = session;
	}
	
	public MultiSession(InetSocketAddress peer, DTLSConnector dtlsConnector) {
		this.sessionId = peer;
		this.peer = peer;
		this.dtlsConnector = dtlsConnector;
	}

	public BypassDevice getBypassDevice() {
		return bypassDevice;
	}

	public void setBypassDevice(BypassDevice bypassDevice) {
		this.bypassDevice = bypassDevice;
	}

	public BypassClient getBypassClient() {
		return bypassClient;
	}

	public void setBypassClient(BypassClient bypassClient) {
		this.bypassClient = bypassClient;
	}

	public boolean containsKey(String key){
		return attribute.containsKey(key);
	}
	
	public Object getAttribute(String key) {
		return attribute.get(key);
	}

	public void setAttribute(String key, Object value) {
		attribute.put(key, value);
	}
	
	public void removeAttribute(String key){
		attribute.remove(key);
	}
	
	public Object getSessionId() {
		return sessionId;
	}

	public boolean isConnected() {
		if ((ioSession != null && ioSession.isConnected()) || (dtlsConnector != null && dtlsConnector.isRunning())) {
			return true;
		}
		return false;
	}

	public void write(byte[] data) {
		if (ioSession != null) {
			ioSession.write(data);

			logger.debug("## [MultiSession][TCP] Write = {}", Hex.decode(data));
			//logger.debug("## [MultiSession][TCP] Write = {}", Hex.getHexDump(data));
			
			//logger.debug("## [MultiSession] Write = {}", Hex.decode(data));
		} else {
			RawData sndRawData = new RawData(data, peer);
			dtlsConnector.send(sndRawData);
			
			//logger.debug("## [MultiSession][DTLS] Write = {}", Hex.decode(data));
			//logger.debug("## [MultiSession] Write = {}byte", data.length);
			
			logger.debug("## [MultiSession][UDP] Write = {}", Hex.decode(data));
			//logger.debug("## [MultiSession][UDP] Write = {}", Hex.getHexDump(data));
		}
	}

	public void destroy() {
		logger.debug("MultiSession destroy step start");
		if (ioSession != null && ioSession.isConnected()) {
			logger.debug("MultiSession [IoSession] destroy step 1");
			ioSession.closeNow();
			logger.debug("MultiSession [IoSession] destroy step 2");
		} else if (dtlsConnector != null && dtlsConnector.isRunning()) {
			logger.debug("MultiSession [DTLS] destroy step 1");
			dtlsConnector.close(peer);
			logger.debug("MultiSession [DTLS] destroy step 2");
		}
		logger.debug("MultiSession destroy step end");
		logger.debug("### MultiSession closed.");
	}
	
	public String getRemoteAddress(){
		if(ioSession != null){
			return ioSession.getRemoteAddress().toString();
		}else if(peer != null){
			return peer.getHostName();
		}
		return "";
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
	}

}
