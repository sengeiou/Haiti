/**
 * (@)# NIBypassClientHandler.java
 *
 * 2016. 6. 1.
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

import java.util.Map;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.FilterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.logger.snowflake.SnowflakeGeneration;
import com.aimir.fep.protocol.nip.client.actions.BypassCommandAction;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class BypassClientHandler extends IoHandlerAdapter {
	private static Logger logger = LoggerFactory.getLogger(BypassClientHandler.class);
	private final int BYPASS_WAIT_TIME = Integer.parseInt(FMPProperty.getProperty("protocol.connection.timeout", "30"));
	private final int MAX_IDLE_COUNT = 3;  // 3번. 즉 1분30초동안 대기후 disconnect.
	private int idleCount = 0;

	private BypassClient bypassClient;
	private BypassCommandAction commandAction;
	
	private long sequenceLog;

	public BypassClientHandler(BypassClient bypassClient, BypassCommandAction commandAction) {
		this.bypassClient = bypassClient;
		this.commandAction = commandAction;
		this.sequenceLog = SnowflakeGeneration.getId();
	}
	
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		String tname = Thread.currentThread().getName();
		SnowflakeGeneration.setSeq(tname, sequenceLog);
		
		logger.debug("### {} session Open...", session.toString());
		session.getConfig().setWriteTimeout(BYPASS_WAIT_TIME);
		session.getConfig().setIdleTime(IdleStatus.BOTH_IDLE, BYPASS_WAIT_TIME);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		//SP-691
		SnowflakeGeneration.deleteId();
		if(session != null && session.isConnected()){
			session.closeNow();
		}
		logger.debug("### {} session Close...", session.toString());
	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		idleCount = 0;  //초기화
		
		byte[] frame = (byte[]) message;
		logger.debug("### [RECEIVE] BypassFrame [" + Hex.decode(frame) + "]");
		
		MultiSession bpSession = commandAction.getMultiSession(session);
		commandAction.executeBypass(bpSession, frame);
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		idleCount = 0;  //초기화
		
		byte[] frame = (byte[]) message;
		logger.debug("### [SENT] BypassFrame [" + Hex.decode(frame) + "]");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		logger.debug("### {} session idle...", session.toString());
		
		if(MAX_IDLE_COUNT < idleCount){
			logger.info("#### Max Idle Count({} times) over. Disconnect progress..", MAX_IDLE_COUNT);
			
			if(bypassClient != null){
				bypassClient.close();
			}			
			
			 // 3. NullBypass Close 요청
			/**
			 *  2016-08-01
			 * Modem측에서 Thread를 여러개 띄울 형편이 안되 reqNullBypassColse는 요청하지 않고 생략하는것으로 한다.
			 * => Modem쪽에서는 NullBypassPasOpen요청시 설정한 timeout시간만큼 대기하고 있다가 자동으로 close처리한다고함.
			 * 추후 필요시 주석 풀고 사용할것.
			 * 
			 * 	 Map<String, Object> rboResult = bypassClient.reqNullBypassClose();
			 *   logger.debug("Req Bypass Close - ", rboResult.toString());
			 */
			//SP-691
			if(session != null && session.isConnected()){
				session.closeNow();
			}

		}
	}
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		String tname = Thread.currentThread().getName();
		
		logger.debug("### tname : {},  SequenceLog : {}", tname, sequenceLog);
		SnowflakeGeneration.setSeq(tname, sequenceLog);
		logger.debug("### {} Session Create...", session.toString());
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		logger.error("### " +session.toString()+ " session exceptionCaught... - " + cause.getMessage(), cause);
		
		//SP-691
		if(session != null && session.isConnected()){
			session.closeNow();
			
			String tname = Thread.currentThread().getName();
			SnowflakeGeneration.deleteId();
		}
	}
	
	@Override
	public void event(IoSession session, FilterEvent event) throws Exception {
		logger.debug("Event !!! - " + event.toString());
	}
}
