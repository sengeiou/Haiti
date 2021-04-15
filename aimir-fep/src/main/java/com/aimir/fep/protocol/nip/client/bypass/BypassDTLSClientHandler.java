/**
 * (@)# BypassDTLSClientHandler.java
 *
 * 2016. 7. 23.
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
package com.aimir.fep.protocol.nip.client.bypass;

import java.net.InetSocketAddress;
import java.util.logging.Level;

import org.eclipse.californium.elements.RawData;
import org.eclipse.californium.elements.RawDataChannel;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.ScandiumLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.logger.snowflake.SnowflakeGeneration;
import com.aimir.fep.protocol.nip.client.actions.BypassCommandAction;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class BypassDTLSClientHandler implements RawDataChannel {
	private static Logger logger = LoggerFactory.getLogger(BypassDTLSClientHandler.class);

	private BypassClient bypassClient;
	private DTLSConnector dtlsConnector;
	private BypassCommandAction commandAction;
	private String sequenceLog;
	
	static {
		ScandiumLogger.initialize();
		ScandiumLogger.setLevel(Level.ALL);
	}

	public BypassDTLSClientHandler(BypassClient bypassClient, DTLSConnector dtlsConnector, BypassCommandAction commandAction) {
		this.bypassClient = bypassClient;
		this.dtlsConnector = dtlsConnector;
		this.commandAction = commandAction;
		this.sequenceLog = SnowflakeGeneration.getId();
	}
	
	@Override
	public void receiveData(RawData raw) {
		SnowflakeGeneration.setSeq(sequenceLog);
		
		InetSocketAddress peer = raw.getInetSocketAddress();
		logger.debug("[RECEIVE] From ={}", peer);

		byte[] frame = raw.getBytes();
		logger.debug("### [ReceiveData] BypassFrame [" + Hex.decode(frame) + "]");

		//BypassSession session = commandAction.getBypassSession(dtlsConnector.getSessionByAddress(peer));
		MultiSession session = commandAction.getMultiSession(peer);

		if (session != null) {
			try {
				commandAction.executeBypass(session, frame);
			} catch (Exception e) {
				logger.error("[{}]Bypass Action Commnad Excute error[Peer={}] - {}", commandAction.getClass().getSimpleName(), peer, e.toString());
			}
		} else {
			logger.error("Bypass Session lost...!!");
			try {
				bypassClient.close();
			} catch (Exception e) {
				logger.error("Closing Error - {}", e);
			}
		}

	}

}
