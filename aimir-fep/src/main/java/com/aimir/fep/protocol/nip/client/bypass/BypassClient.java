/**
 * (@)# NIBypassClient.java
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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.eclipse.californium.scandium.DTLSConnector;
import org.eclipse.californium.scandium.ErrorHandler;
import org.eclipse.californium.scandium.dtls.AlertMessage.AlertDescription;
import org.eclipse.californium.scandium.dtls.AlertMessage.AlertLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.ModemIFType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.bypass.BypassDevice;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.server.FMPSslContextFactory;
import com.aimir.fep.protocol.nip.CommandNIProxy;
import com.aimir.fep.protocol.nip.client.actions.BypassCommandAction;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.protocol.nip.command.NullBypassClose;
import com.aimir.fep.protocol.nip.command.NullBypassOpen;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.protocol.security.DtlsConnector;
import com.aimir.fep.util.FMPProperty;

/*
 * 1. NullBypass Open 요청 
 * 2. Bypass Command 실행 
 * 3. NullBypass Close 요청
 * 
 * @author simhanger
 *
 */
public class BypassClient implements AutoCloseable{
	private static Logger logger = LoggerFactory.getLogger(BypassClient.class);
	private String clientName;
	private int CONNECT_TIMEOUT = Integer.parseInt(FMPProperty.getProperty("protocol.connection.timeout", "30")); // seconds
	// for pana certificate, pana is greater than 1.0
	private double fwVer = Double.parseDouble(FMPProperty.getProperty("pana.modem.fw.ver"));
//	private int MODEM_NULLBYPASS_PORT = 0;
//	private int MODEM_TIMEOUT = 60;   // 초
	private byte[] FINISH_DATA_FOR_MODEM_BYPASSCLOSE = {(byte)0x2A, (byte)0x2A, (byte)0x2A, (byte)0x2A, (byte)0x2A};
	
	private IoConnector connector;
    private DTLSConnector dtlsConnector;
    private MultiSession bPSession;
	private Target target;
	private BypassCommandAction commandAction;
	private Map<String, Object> params;
	private ConnectProtocolType conType;
	private IoSession externalNISession;
	
	enum ConnectProtocolType{
		TLS, DTLS, TCP, UDP
	};

    //for Observer
	boolean isCommandAciontFinished = false;
	private Object resMonitor = new Object();
	
	public void setExternalNISession(IoSession session) {
		this.externalNISession = session;
		
	}
	
	/**
	 * 초기화
	 * 
	 * @param target
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public BypassClient(Target target) throws Exception {
		this.target = target;
		
    	UUID uuid = UUID.randomUUID();
    	this.clientName = uuid.toString();
    	logger.debug("## Create BypassClient. Name = " + clientName);

		/**
		 * 1. Command Action Setting
		 */
		try {
			commandAction = (BypassCommandAction) Class.forName("com.aimir.fep.protocol.nip.client.actions.BypassCommandAction_" + target.getNameSpace()).newInstance();
		} catch (Exception e) {
			throw new Exception("BypassCommandAction Execute Error.", e);
		}
	}
	
	public BypassCommandAction getBypassCommandCommandAction(){
		return commandAction;
	}

	public void setParams(Map<String, Object> params) {
		System.out.println("########################### 여기 넘어왔어 " + params.toString());
		this.params = params;
	}
	
	/**
	 * MBB/Ethernet Modme
	 *  : TLS(NioSocketConnector) + Null Bypass
	 * RF Modem
	 *  : DTLS(DtlsConnector) + Null Bypass or UDP(NioDatagramConnector) + NUll Bypass 
	 * @throws Exception 
	 */
	private void initConnector() throws Exception{
		initConnector(false);
	}
	
	/**
	 * MBB/Ethernet Modme
	 *  : TLS(NioSocketConnector) + NI Bypass
	 * RF Modem
	 *  : DTLS(DtlsConnector) + NI Bypass Only. 
	 *    UDP + NI Bypass (can't use)
	 * PLC Modem
	 *  : UDP
	 * @param useNiBypass
	 * @throws Exception 
	 */
	private void initConnector(boolean useNiBypass) throws Exception{
		/**
		 * 2. Bypass Connector create
		 */		
		/*
		 * If using TLS - MBB Modem, Ethernet Modem
		 */
		if (target.getReceiverType().equals("MMIU") || target.getTargetType() == McuType.MMIU) {
			connector = new NioSocketConnector();
			if(Boolean.parseBoolean(FMPProperty.getProperty("protocol.ssl.use"))){
				conType = ConnectProtocolType.TLS;				
			}else{
				conType = ConnectProtocolType.TCP;
			}
		} else if (target.getReceiverType().equals("SubGiga")) {
			/*
			 * If using DTLS - RF Modem
			 */
			if (target.getProtocol() == Protocol.IP) {
				
				// DTLS
				if(Boolean.parseBoolean(FMPProperty.getProperty("soria.protocol.modem.rf.dtls.use", "true"))){
					conType = ConnectProtocolType.DTLS;
					
                	InetSocketAddress address = new InetSocketAddress(FMPProperty.getProperty("fep.ipv6.addr"), 0);
					
                	if (Double.parseDouble(target.getFwVer()) >= fwVer){
                	    dtlsConnector = DtlsConnector.newDtlsClientConnector(address, true);
                	} else{
                	    dtlsConnector = DtlsConnector.newDtlsClientConnector(address, false);
                	}
                    logger.debug("[{}] DtlsConnector create is {} - Host={}", conType, dtlsConnector == null ? "fail" : "success", address.getHostName());
                    
                    dtlsConnector.setRawDataReceiver(new BypassDTLSClientHandler(this, dtlsConnector, commandAction));
                    dtlsConnector.setErrorHandler(new ErrorHandler() {
                        @Override
                        public void onError(InetSocketAddress peerAddress, AlertLevel level, AlertDescription description) {
                        	logger.error("BypassClient DTLSConnector Alert.Level[" + level.toString() + " DESCR[" + description.getDescription() + "] Peer[" + peerAddress.getHostName() + "]");
                        }                
                    });
				}
				// UDP
				else{
					conType = ConnectProtocolType.UDP;	
					connector = new NioDatagramConnector();
					logger.debug("[{}] UDPConnector create is {}", conType, connector == null ? "fail" : "success");
				}
				
				
				
				
				
				
//				// Use NIBypass or Use DTLS
//				if(useNiBypass || Boolean.parseBoolean(FMPProperty.getProperty("soria.protocol.modem.rf.dtls.use", "true"))){
//                	conType = ConnectProtocolType.DTLS;
//                	InetSocketAddress address = new InetSocketAddress(FMPProperty.getProperty("fep.ipv6.addr"), 0);
//					
//                	if (Double.parseDouble(target.getFwVer()) >= fwVer){
//                	    dtlsConnector = DtlsConnector.newDtlsClientConnector(address, true);
//                	} else{
//                	    dtlsConnector = DtlsConnector.newDtlsClientConnector(address, false);
//                	}
//                    logger.debug("[{}] DtlsConnector create is {} - Host={}", conType, dtlsConnector == null ? "fail" : "success", address.getHostName());
//                    
//                    dtlsConnector.setRawDataReceiver(new BypassDTLSClientHandler(this, dtlsConnector, commandAction));
//                    dtlsConnector.setErrorHandler(new ErrorHandler() {
//                        @Override
//                        public void onError(InetSocketAddress peerAddress, AlertLevel level, AlertDescription description) {
//                        	logger.error("BypassClient DTLSConnector Alert.Level[" + level.toString() + " DESCR[" + description.getDescription() + "] Peer[" + peerAddress.getHostName() + "]");
//                        }                
//                        
//                    });
//				}
//				// Use UDP + NullBypass
//				else{
//                	connector = new NioDatagramConnector();	            
//                	conType = ConnectProtocolType.UDP;					
//				}
			}else{
				logger.error("Unknown protocol type for SubGiga - ModemId=" + target.getModemId() + ", MeterId=" + target.getMeterId());
			}
		} else if (target.getReceiverType().equals("PLCIU")) {
			conType = ConnectProtocolType.UDP;	
			connector = new NioDatagramConnector();
			logger.debug("[{}] UDPConnector create is {}", conType, connector == null ? "fail" : "success");
		}
		
		/*
		 *  If using TLS or UDP
		 */
		logger.debug("### Connection Type = {}", conType.name());
		if((conType == ConnectProtocolType.TLS || conType == ConnectProtocolType.TCP || conType == ConnectProtocolType.UDP) && connector != null){
			if(conType == ConnectProtocolType.TLS){
				FMPSslContextFactory.setSslFilter(connector);				
			}
			connector.setConnectTimeoutMillis(CONNECT_TIMEOUT * 1000);
			connector.getFilterChain().addLast("logger", new LoggingFilter());
	        //connector.getFilterChain().addLast("Executor", new ExecutorFilter(Executors.newCachedThreadPool()));    // 이거 적용하면 안됨 !!!!!!  Please don't delete this line.
			connector.getFilterChain().addLast("codecFilter", new ProtocolCodecFilter(new ProtocolCodecFactory() {
				public ProtocolDecoder getDecoder(IoSession session) throws Exception {
					return new BypassClientDecoder();
				}

				public ProtocolEncoder getEncoder(IoSession session) throws Exception {
					return new BypassClientEncoder();
				}
			}));
			connector.setHandler(new BypassClientHandler(this, commandAction));			
		}

		/**
		 * 3. Bypass Port setting
		 */
		try {
			int TARGET_MODEM_PORT = 0;
			// for NI Bypass
			if(useNiBypass){
				// MBB Modem, Ethernet Modem
				if (conType == ConnectProtocolType.TLS || conType == ConnectProtocolType.TCP) {
					TARGET_MODEM_PORT = Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.port.tls.server", "8004"));  // TLS Port
				} 
				// RF Modem - DTLS
				else if (conType == ConnectProtocolType.DTLS) {
					TARGET_MODEM_PORT = Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.port.dtls.server", "8006")); // DTLS Port
				}
				// RF Modem - UDP
				else if(conType == ConnectProtocolType.UDP){
					TARGET_MODEM_PORT = Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.port.udp.server", "65333")); // UDP Port
				}
			}
			// for Null Bypass
			else{
				// If using UDP - RF Modem
				if(conType == ConnectProtocolType.UDP){  
					TARGET_MODEM_PORT = Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.port.nullbypass.udp", "8901"));
				}else{ 
					/**
					 * soria.protocol.modem.port.nullbypass 는 원래 8008번포트를 써야하는데 SORIA에 신청이 안되어 있어서 8900번으로 쓰고있음.
					 */
					TARGET_MODEM_PORT = Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.port.nullbypass", "8008"));	
				}
				
			}
			target.setPort(TARGET_MODEM_PORT);
			target.setTimeout(Integer.parseInt(FMPProperty.getProperty("soria.protocol.modem.port.nullbypass.timeout", "60")));
		} catch (Exception e) {
			logger.debug("FMPProperty value error - " + e, e);
		}

		logger.debug("## Target Modem Type = {}, Protocol Type = {}, isUseNIBypass = {}, Connector = {}, Target Modem port = {}, ModemTimeOut = {}"
				, target.getReceiverType(), conType, useNiBypass, (dtlsConnector != null ? dtlsConnector.getClass().getName() : connector.getClass().getName()), target.getPort(), target.getTimeout());
	}
	
	
	

	/**
	 * Bypass Client 실행
	 * 
	 * @param command
	 * @return
	 */
	public BypassResult excute(final String command) {
		BypassResult executeResult = null;

		if (bPSession != null && bPSession.isConnected()){
			throw new IllegalStateException("Already connected. Disconnect first.");
		}
		
		try {
			// Connector 초기화
			initConnector();
			
			Map<String, Object> rboResult = null;
			HashMap<String, Object> param = new HashMap<String, Object>();
			int MODEM_NULLBYPASS_PORT = target.getPort();
			
			// 1. NullBypass Open 요청
			if(conType == ConnectProtocolType.UDP){  // If using UDP - RF Modem
				rboResult = new HashMap<String, Object>();
				rboResult.put("result", true);
				param.put("port", MODEM_NULLBYPASS_PORT);
			}else{  // IF using DTLS-RF Modem , TLS-Ethernet, TLS-MBB
				param.put("port", MODEM_NULLBYPASS_PORT);
				//param.put("timeout", target.getTimeout() * 1000);
				param.put("timeout", 60 * 1000);
				logger.debug("## reqNullBypassOpen Param = {}", param.toString());
				
				rboResult = reqNullBypassOpen(param);	
			}
			
			// 2. Bypass Command 실행
			if (rboResult != null && Boolean.valueOf(String.valueOf(rboResult.get("result"))) == true) {
				logger.debug("[{}] BypassClient Connection 준비... Connector = {}, DtlsConnector = {}"
						, conType, (connector == null ? "null" : "ok"), (dtlsConnector == null ? "null" : "ok"));
				
				BypassDevice bd = new BypassDevice();
				bd.setModemId(target.getModemId());
				bd.setMeterId(target.getMeterId());
				bd.setArgMap((HashMap<String, Object>) params);
				
				// RF Modem이용하는 경우
			    if ((target.getReceiverType().equals("SubGiga") || target.getReceiverType().equals("PLCIU")) && target.getProtocol() == Protocol.IP) {
			    	bd.setSendDelayTime(2000);
			    	logger.debug("Set send delay time = {}", bd.getSendDelayTime()); 	
				}
				
				// delay fo modem.
				Thread.sleep(3000);  
				
				// For DTLS
				if(conType == ConnectProtocolType.DTLS && dtlsConnector != null){
					dtlsConnector.start();
					logger.debug("[{}] BypassClient Connection start ~~ !! Ip={}, NullBypassPort={}, Param={}"
							, conType, target.getIpv6Addr(), MODEM_NULLBYPASS_PORT, param.toString());
					
					if(dtlsConnector.isRunning()){
						logger.debug("[{}] BypassClient Connection success..", conType.name());
						
						InetSocketAddress connectPeerAddr = new InetSocketAddress(target.getIpAddr(), MODEM_NULLBYPASS_PORT);
						bPSession = commandAction.setMultiSession(connectPeerAddr, dtlsConnector);
					}else{
						logger.debug("[{}] BypassClient Connection fail.. ㅜㅜ", conType.name());
						close();
						return new BypassResult(command, false, "Connection Fail.");
					}
				}
				// For Using TCP or TLS or UDP
				else if(connector != null){
					ConnectFuture future = connector.connect(new InetSocketAddress(target.getIpAddr(), MODEM_NULLBYPASS_PORT));
					
					//ConnectFuture future = connector.connect(new InetSocketAddress(target.getIpAddr(), target.getPort()), new InetSocketAddress(9001));
					//logger.debug("######### local port set static 9001 #####");
					
					
//					ConnectFuture future = connector.connect(new InetSocketAddress(target.getIpv6Addr(), MODEM_NULLBYPASS_PORT), 
//							new InetSocketAddress(InetAddress.getByName(FMPProperty.getProperty("fep.ipv6.addr")), 0));
					logger.debug("{}:{} [{}] BypassClient Connection start ~ : Param = {}", target.getIpAddr(), MODEM_NULLBYPASS_PORT, param.toString(), conType);
					
					future.awaitUninterruptibly();
					if (!future.isConnected()) {
						logger.debug("[{}] BypassClient Connection fail. ㅜㅜ", conType.name());
						close();
						return new BypassResult(command, false, "Connection Fail.");
					}
					logger.debug("[{}] BypassClient Connection success.", conType.name());
					
					IoSession session = future.getSession();
					bPSession = commandAction.setMultiSession(session);
				} else{
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("result", false);
					result.put("resultValue", "BypassClient connection Fail.");
					
					return new BypassResult(command, false, result);
				}
				
				bPSession.setBypassDevice(bd);
				bPSession.setBypassClient(this);
				bPSession.setAttribute("target", target);
				
				logger.debug("#### Target Info = {}", target.toString());
				commandAction.execute(bPSession, command);
				
			} else if(rboResult != null){ 
				return new BypassResult(command, false, rboResult);
			} else {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("result", false);
				result.put("resultValue", "Bypass Tunnel Open Fail. - Unknown reason.");
				
				return new BypassResult(command, false, result);
			}

			// 3. Result return.			
			try {
				executeResult = commandAction.getBypassResult(bPSession, commandFinishResponseTime(command));
			} catch (Exception e) {
				logger.error("GetBypassResult Exception - {}", e);
			}finally {
				//자원해제
				close();	
				
                /**
                 *  2016-08-01
                 * Modem측에서 Thread를 여러개 띄울 형편이 안되 reqNullBypassColse는 요청하지 않고 생략하는것으로 한다.
                 * => Modem쪽에서는 NullBypassPasOpen요청시 설정한 timeout시간만큼 대기하고 있다가 자동으로 close처리한다고함.
                 *    추후 필요시 주석 풀고 사용할것.
                 *     
                 * reqNullBypassClose();
                 */
                
                /**
                 * 2016-08-16
                 * Modem측 타임아웃 끝나는 시간동안 재실행 방지를위해 대기해주는 로직 추가해주기로함.
                 */
                //logger.debug("wating {}s for modem closing...", MODEM_TIMEOUT * 1000);
                //Thread.sleep(MODEM_TIMEOUT * 1000);
                
                /**
                 * Bypass close 요청을 따로 하지 않고 close할때 종료패킷 보내는것으로 대체함
                 * 2016.09.13
                 */
				
			}
			
			logger.debug("#### Result bypassResult value ==> {}", executeResult == null ? "NULL ㅠㅠ" : executeResult.toString());
			
			if (executeResult == null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("result", false);
				result.put("resultValue", "Request - Result is null");
				
				executeResult = new BypassResult(command, false, result);
			}
		} catch (Exception e) {
			logger.error("Bypass Client Excute Fail - {}", e);
            
			try {
				close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}   
            
			/**
			 *  2016-08-01
			 * Modem측에서 Thread를 여러개 띄울 형편이 안되 reqNullBypassColse는 요청하지 않고 생략하는것으로 한다.
			 * => Modem쪽에서는 NullBypassPasOpen요청시 설정한 timeout시간만큼 대기하고 있다가 자동으로 close처리한다고함.
			 * 추후 필요시 주석 풀고 사용할것.
			 * 
			 * reqNullBypassClose();
			 */
			
            /**
             * 2016-08-16
             * Modem측 타임아웃 끝나는 시간동안 재실행 방지를위해 대기해주는 로직 추가해주기로함.
             */
            //logger.debug("wating {}s for modem closing...", MODEM_TIMEOUT * 1000);
            //Thread.sleep(MODEM_TIMEOUT * 1000);
            
            /**
             * Bypass close 요청을 따로 하지 않고 close할때 종료패킷 보내는것으로 대체함
             * 2016.09.13
             */
			
			return new BypassResult(command, false, "Bypass Client Exception Fail." + e.getMessage());
		}

		return executeResult;
	}
	
	/**
	 * Bypass Client (use NiBypass)
	 * SP-519
	 * @param command
	 * @return
	 */
	public BypassResult excuteNiBypass(final String command) {
		BypassResult executeResult = null;

		if (bPSession != null && bPSession.isConnected()){
			throw new IllegalStateException("Already connected. Disconnect first.");
		}
		
		try {
			
			BypassDevice bd = new BypassDevice();
			bd.setModemId(target.getModemId());
			bd.setMeterId(target.getMeterId());
			bd.setArgMap((HashMap<String, Object>) params);

			if(externalNISession != null){
				logger.debug("excuteNiBypass Start. using externalNISession.");
				if(externalNISession.isActive() && externalNISession.isConnected()){
					bPSession = commandAction.setMultiSession(externalNISession);
					
					//ret = (NullBypassOpen) commandNIProxy.execute(target, NIAttributeId.NullBypassOpen, param, externalNISession);					
				}else{
					throw new Exception("NI Session is disconnected.");
				}
			}else{
				logger.debug("excuteNiBypass Start. usein New NI Session.");
				// Connector Initializing
				initConnector(true);
					
				// RF Modem이용하는 경우
				if (target.getReceiverType().equals("SubGiga") && target.getProtocol() == Protocol.IP) {
					bd.setSendDelayTime(2000);
					logger.debug("Set send delay time = {}", bd.getSendDelayTime()); 	
				}

				// delay fo modem.
				Thread.sleep(3000);  

				// For DTLS
				if(conType == ConnectProtocolType.DTLS && dtlsConnector != null){
					dtlsConnector.start();
					logger.debug("[{}] BypassClient Connection start ~~ !! Ip={}, NiBypassPort={}", conType, target.getIpv6Addr(), target.getPort());

					if(dtlsConnector.isRunning()){
						logger.debug("[{}] BypassClient Connection success.. Ip={}, NiBypassPort={}", conType, target.getIpv6Addr(), target.getPort());

						InetSocketAddress connectPeerAddr = new InetSocketAddress(target.getIpAddr(), target.getPort());
						bPSession = commandAction.setMultiSession(connectPeerAddr, dtlsConnector);
					}else{
						logger.debug("[{}] BypassClient Connection fail.. Ip={}, NiBypassPort={}", conType, target.getIpv6Addr(), target.getPort());
						close();
						return new BypassResult(command, false, "Connection Fail.");
					}
				}
				// For Using TCP or TLS or UDP
				else if(connector != null){
					ConnectFuture future = connector.connect(new InetSocketAddress(target.getIpAddr(), target.getPort()));
					
					//					ConnectFuture future = connector.connect(new InetSocketAddress(target.getIpv6Addr(), MODEM_NULLBYPASS_PORT), 
					//							new InetSocketAddress(InetAddress.getByName(FMPProperty.getProperty("fep.ipv6.addr")), 0));
					logger.debug("{}:{} [{}] BypassClient Connection start ~ ", target.getIpAddr(), target.getPort(),  conType);

					future.awaitUninterruptibly();
					if (!future.isConnected()) {
						logger.debug("{}:{} [{}] BypassClient Connection fail.", target.getIpAddr(), target.getPort(),  conType);
						close();
						return new BypassResult(command, false, "Connection Fail.");
					}
					logger.debug("{}:{} [{}] BypassClient Connection success.", target.getIpAddr(), target.getPort(),  conType);

					IoSession session = future.getSession();
					bPSession = commandAction.setMultiSession(session);
				} else{
					Map<String, Object> result = new HashMap<String, Object>();
					result.put("result", false);
					result.put("resultValue", "BypassClient connection Fail.");

					return new BypassResult(command, false, result);
				}				
			}
			
			bPSession.setBypassDevice(bd);
			bPSession.setBypassClient(this);
			bPSession.setAttribute("target", target);
			bPSession.setAttribute("UseNiBypass", "true"); 

			logger.debug("#### Target Info = {}", target.toString());
			commandAction.execute(bPSession, command);

			// Result return.
			try {
				executeResult = commandAction.getBypassResult(bPSession, commandFinishResponseTime(command));
			} catch (Exception e) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("result", false);
				result.put("resultValue", e.getMessage());
				executeResult = new BypassResult(command, false, result);
				
				logger.error("GetBypassResult Exception - " + e.getMessage(), e);				
			}finally {
				//자원해제
				close();	
				
                /**
                 *  2016-08-01
                 * Modem측에서 Thread를 여러개 띄울 형편이 안되 reqNullBypassColse는 요청하지 않고 생략하는것으로 한다.
                 * => Modem쪽에서는 NullBypassPasOpen요청시 설정한 timeout시간만큼 대기하고 있다가 자동으로 close처리한다고함.
                 *    추후 필요시 주석 풀고 사용할것.
                 *     
                 * reqNullBypassClose();
                 */
                
                /**
                 * 2016-08-16
                 * Modem측 타임아웃 끝나는 시간동안 재실행 방지를위해 대기해주는 로직 추가해주기로함.
                 */
                //logger.debug("wating {}s for modem closing...", MODEM_TIMEOUT * 1000);
                //Thread.sleep(MODEM_TIMEOUT * 1000);
                
                /**
                 * Bypass close 요청을 따로 하지 않고 close할때 종료패킷 보내는것으로 대체함
                 * 2016.09.13
                 */
				
			}
			
			logger.debug("#### Result bypassResult value ==> {}", executeResult == null ? "NULL ㅠㅠ" : executeResult.toString());
			
			if (executeResult == null) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("result", false);
				result.put("resultValue", "Request - No Result");
				
				executeResult = new BypassResult(command, false, result);
			}
		} catch (Exception e) {
			logger.error("Bypass Client Excute Fail - " + e.getMessage(), e);
            
			try {
				close();
			} catch (Exception e1) {
				logger.error("BypassClient Close error - " + e.getMessage(), e);
			}   
            
			/**
			 *  2016-08-01
			 * Modem측에서 Thread를 여러개 띄울 형편이 안되 reqNullBypassColse는 요청하지 않고 생략하는것으로 한다.
			 * => Modem쪽에서는 NullBypassPasOpen요청시 설정한 timeout시간만큼 대기하고 있다가 자동으로 close처리한다고함.
			 * 추후 필요시 주석 풀고 사용할것.
			 * 
			 * reqNullBypassClose();
			 */
			
            /**
             * 2016-08-16
             * Modem측 타임아웃 끝나는 시간동안 재실행 방지를위해 대기해주는 로직 추가해주기로함.
             */
            //logger.debug("wating {}s for modem closing...", MODEM_TIMEOUT * 1000);
            //Thread.sleep(MODEM_TIMEOUT * 1000);
            
            /**
             * Bypass close 요청을 따로 하지 않고 close할때 종료패킷 보내는것으로 대체함
             * 2016.09.13
             */
			
			return new BypassResult(command, false, "Bypass Client Excute Fail - " + e.getMessage());
		}

		return executeResult;
	}
	
	/**
     * Bypass Client 실행
     * 
     * @param command
     * @return
     */
    public BypassResult executeMBB(final String command) {
        BypassResult executeResult = null;

        if (bPSession != null && bPSession.isConnected()){
            throw new IllegalStateException("Already connected. Disconnect first.");
        }
        
        try {
            logger.debug("[{}] BypassClient Connection 준비... Connector = {}, TlsConnector = {}"
                , conType, (connector == null ? "null" : "ok"));
            
			// Connector 초기화
			initConnector();
            
            BypassDevice bd = new BypassDevice();
            bd.setModemId(target.getModemId());
            bd.setMeterId(target.getMeterId());
            bd.setArgMap((HashMap<String, Object>) params);
            
            // delay fo modem.
            Thread.sleep(3000);  
            
            if(connector != null){
                ConnectFuture future = connector.connect(new InetSocketAddress(target.getIpAddr(), target.getPort()));
            //                  ConnectFuture future = connector.connect(new InetSocketAddress(target.getIpv6Addr(), MODEM_NULLBYPASS_PORT), 
            //                          new InetSocketAddress(InetAddress.getByName(FMPProperty.getProperty("fep.ipv6.addr")), 0));
            logger.debug("{}:{} [{}] BypassClient Connection start ~ : Param = {}", target.getIpAddr(), target.getPort(), conType);
            
            future.awaitUninterruptibly();
            if (!future.isConnected()) {
                logger.debug("[{}] BypassClient Connection fail. ㅜㅜ", conType.name());
                close();
                return new BypassResult(command, false, "Connection Fail.");
            }
            logger.debug("[{}] BypassClient Connection success.", conType.name());
                
                IoSession session = future.getSession();
                bPSession = commandAction.setMultiSession(session);
            } 
            bPSession.setBypassDevice(bd);
            bPSession.setBypassClient(this);
            bPSession.setAttribute("target", target);
            
            logger.debug("#### Target Info = {}", target.toString());
            commandAction.execute(bPSession, command);
                
            // Result return.
            try {
            	executeResult = commandAction.getBypassResult(bPSession, commandFinishResponseTime(command));
            } catch (Exception e) {
                logger.error("GetBypassResult Exception - {}", e);
            }finally {
                //자원해제
                close();    
                
                /**
                 *  2016-08-01
                 * Modem측에서 Thread를 여러개 띄울 형편이 안되 reqNullBypassColse는 요청하지 않고 생략하는것으로 한다.
                 * => Modem쪽에서는 NullBypassPasOpen요청시 설정한 timeout시간만큼 대기하고 있다가 자동으로 close처리한다고함.
                 *    추후 필요시 주석 풀고 사용할것.
                 *     
                 * reqNullBypassClose();
                 */
                
                /**
                 * 2016-08-16
                 * Modem측 타임아웃 끝나는 시간동안 재실행 방지를위해 대기해주는 로직 추가해주기로함.
                 */
                //logger.debug("wating {}s for modem closing...", MODEM_TIMEOUT * 1000);
                //Thread.sleep(MODEM_TIMEOUT * 1000);
                
                /**
                 * Bypass close 요청을 따로 하지 않고 close할때 종료패킷 보내는것으로 대체함
                 * 2016.09.13
                 */
            }
            
            logger.debug("#### Result bypassResult value ==> {}", executeResult == null ? "NULL ㅠㅠ" : executeResult.toString());
            
            if (executeResult == null) {
                Map<String, Object> result = new HashMap<String, Object>();
                result.put("result", false);
                result.put("resultValue", "Request - No Result");
                
                executeResult = new BypassResult(command, false, result);
            }
        } catch (Exception e) {
            //자원해제
            try {
				close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}    
            
            /**
             *  2016-08-01
             * Modem측에서 Thread를 여러개 띄울 형편이 안되 reqNullBypassColse는 요청하지 않고 생략하는것으로 한다.
             * => Modem쪽에서는 NullBypassPasOpen요청시 설정한 timeout시간만큼 대기하고 있다가 자동으로 close처리한다고함.
             *    추후 필요시 주석 풀고 사용할것.
             *     
             * reqNullBypassClose();
             */
            
            /**
             * 2016-08-16
             * Modem측 타임아웃 끝나는 시간동안 재실행 방지를위해 대기해주는 로직 추가해주기로함.
             */
            //logger.debug("wating {}s for modem closing...", MODEM_TIMEOUT * 1000);
            //Thread.sleep(MODEM_TIMEOUT * 1000);
            
            /**
             * Bypass close 요청을 따로 하지 않고 close할때 종료패킷 보내는것으로 대체함
             * 2016.09.13
             */
            
            return new BypassResult(command, false, "Bypass Client Excute Fail.");
        }

        return executeResult;
    }

	/**
	 * 1. NullBypass Open 요청 Command 목록은
	 * com.aimir.fep.protocol.nip.frame.GeneralFrame.AttributeList 와 동일. =>
	 * AttributeList.NullBypassOpen.name().equals("NullBypassOpen") == true
	 * 
	 * @return
	 */
	private Map<String, Object> reqNullBypassOpen(HashMap<String, Object> param) {
		Map<String, Object> result = new HashMap<String, Object>();
		int modemStatus = -1;

		try {
			/*
			 * Command 목록은 com.aimir.fep.protocol.nip.frame.GeneralFrame.AttributeList 와 동일.
			 * => AttributeList.NullBypassOpen.name().equals("NullBypassOpen") == true
			 */
			CommandNIProxy commandNIProxy = new CommandNIProxy();
			NullBypassOpen ret = null;
			if(externalNISession != null){
				if(externalNISession.isActive() && externalNISession.isConnected()){
					ret = (NullBypassOpen) commandNIProxy.execute(target, NIAttributeId.NullBypassOpen, param, externalNISession);					
				}else{
					throw new Exception("NI Session is disconnected.");
				}
			}else{
				ret = (NullBypassOpen) commandNIProxy.execute(target, NIAttributeId.NullBypassOpen, param, null);				
			}

			if(ret != null){
				modemStatus = Integer.parseInt(String.valueOf(ret.getStatus()));
				logger.debug("####### reqNullBypassOpen req param ==>>> {}", param.toString());
				logger.debug("####### reqNullBypassOpen response ==>>> {}", modemStatus);
				
				if (modemStatus == 0) { // 널 바이패스 가능 상태
					result.put("result", true);
					result.put("resultValue", "Success.");
				}else if(modemStatus == -1){ 
					result.put("result", false);
					result.put("resultValue", "NI NullBypass Open request Failuer - Modem Connection fail.");
				} else {
					result.put("result", false);
					result.put("resultValue", "NI NullBypass Open request is refused - Modem Busy.");
				} 				
			}else{
				logger.error("NI NullBypass Open Request Error- can not connet to modem");
				result.put("result", false);
				result.put("resultValue", "NI NullBypass Open Request Error - can not connet to modem");
			}

		} catch (Exception e) {
			logger.error("Request NI NullBypass Open Error - " + e, e);
			result.put("result", false);
			result.put("resultValue", "NI NullBypass Open Request Error");
		}

		logger.debug("[requestNINullBypassOpen] {}", result.toString());

		return result;
	}
	
	/*
	 * Total latency for command 
	 */
	private int commandFinishResponseTime(String command) throws Exception{
		int responseTimeout = 180;
		String modemType = "";
		/** cmdMeterOTAStart */
		if (command.equals("cmdMeterOTAStart")) {
			/* RF Modem */
		    if (target.getReceiverType().equals("SubGiga") && target.getProtocol() == Protocol.IP) {
		    	responseTimeout = Integer.parseInt(FMPProperty.getProperty("ota.firmware.meter.waiting.time.rf", "60"));
		    	modemType = ModemIFType.RF.name();
			}
		    /* Ethernet Modem */
		    else if(target.getReceiverType().equals("MMIU") && target.getProtocol() == Protocol.IP){
		    	responseTimeout = Integer.parseInt(FMPProperty.getProperty("ota.firmware.meter.waiting.time.ethernet", "40"));
		    	modemType = ModemIFType.Ethernet.name();
			}
		    /* MBB Modem */
		    else if(target.getReceiverType().equals("MMIU") && (target.getProtocol() == Protocol.SMS || target.getProtocol() == Protocol.GPRS)){
		    	responseTimeout = Integer.parseInt(FMPProperty.getProperty("ota.firmware.meter.waiting.time.mbb", "40"));
		    	modemType = ModemIFType.MBB.name();
			}else{
				throw new Exception("Unknown Modem type.");
			}
		}
//		/** cmdMeterOTAStart */
//		if else(){
//			
//		}
//		/** cmdMeterOTAStart */
//		if else(){
//			
//		}
//		/** cmdMeterOTAStart */
//		if else(){
//			
//		}
		/** Defalut Command */
		else{
			responseTimeout = Integer.parseInt(FMPProperty.getProperty("protocol.bypass.response.timeout", "180"));
		}
		
		logger.debug("Finish Response Time set by command. Command={}, ModemType={}, Timeout={}", command, modemType, responseTimeout);
		return responseTimeout;
	}

	/**
	 * 3. NullBypass Close 요청
	 */
	public Map<String, Object> reqNullBypassClose() {
		Map<String, Object> result = new HashMap<String, Object>();
		
		try {
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("port", target.getPort());

			logger.debug("## reqNullBypassClose Param = {}", param.toString());
			CommandNIProxy commandNIProxy = new CommandNIProxy();
			NullBypassClose ret = (NullBypassClose) commandNIProxy.execute(target, NIAttributeId.NullBypassClose, param, null);
			int modemStatus = Integer.parseInt(String.valueOf(ret.getStatus()));

			if (modemStatus == 0) { // 널 바이패스 종료됨
				result.put("result", true);
				result.put("resultValue", "Success");
			} else if (modemStatus == 1) { // 타임아웃.
				result.put("result", false);
				result.put("resultValue", "Request NI NullBypass Close Fail - Timeout");
			} else if (modemStatus == 2) { // Port Mismatch
				result.put("result", false);
				result.put("resultValue", "Request NI NullBypass Close Fail - Port Mismatch");
			}
		} catch (Exception e) {
			logger.error("Request NI NullBypass Close Error - {}", e);
		}

		logger.debug("[reqBypassClose] {}", result.toString());
		return result;
	}

	@Override
	public void close() throws Exception {
		 
		/*
		 * 아래 데이터를 보내면 모뎀측에서 파이패스 종료로 인식하고 bypass통로를 close하게된다.
		 * 2016-09.13 추가
		 */
		if((bPSession != null) && bPSession.isConnected()){
			logger.debug("### FINISH_DATA_FOR_MODEM_BYPASSCLOSE = {}", FINISH_DATA_FOR_MODEM_BYPASSCLOSE);
			bPSession.write(FINISH_DATA_FOR_MODEM_BYPASSCLOSE);			
		}
		
		if(bPSession != null){
			logger.debug("## BypassClient Destroy start~!!");
			bPSession.destroy();
			logger.debug("## BypassClient Destroy end  ~!!");
		}
		
		if(connector != null){
			connector.dispose();
			connector = null;
		}
		
		if (dtlsConnector != null && dtlsConnector.isRunning()) {
			dtlsConnector.destroy();
			dtlsConnector = null;
		}
		
		logger.debug("### BypassClient Resource released. ##");
	}
}
