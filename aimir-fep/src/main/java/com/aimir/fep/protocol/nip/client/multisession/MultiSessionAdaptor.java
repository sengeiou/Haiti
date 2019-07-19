package com.aimir.fep.protocol.nip.client.multisession;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;
import org.eclipse.californium.scandium.DTLSConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.tool.notiplug.NotiGeneratorForSingleObserver;

public abstract class MultiSessionAdaptor extends NotiGeneratorForSingleObserver {
//public abstract class MultiSessionAdaptor {
	private static Logger logger = LoggerFactory.getLogger(MultiSessionAdaptor.class);
	private ConcurrentHashMap<Object, MultiSession> multiSessionMap = new ConcurrentHashMap<Object, MultiSession>();

	/**
	 * 
	 * @param session
	 *            - IoSession
	 * @return
	 */
	public boolean containsMultiSession(IoSession session) {
		return multiSessionMap.containsKey(session.getId());
	}

	/**
	 * 
	 * @param peer-
	 *            InetSocketAddress
	 * @return
	 */
	public boolean containsMultiSession(InetSocketAddress peer) {
		return multiSessionMap.containsKey(peer);
	}

	/**
	 * 
	 * @param session
	 *            - IoSession
	 * @return
	 */
	public MultiSession getMultiSession(IoSession session) {
		MultiSession mSession = multiSessionMap.get(session.getId());
		logger.debug("found MultiSession(TCP) = [{}], Total MultiSession size = {}", mSession == null ? "null!!!" : mSession.getSessionId().toString(), multiSessionMap.size());
		return mSession;
	}

	/**
	 * 
	 * @param peer
	 *            - InetSocketAddress
	 * @return
	 */
	public MultiSession getMultiSession(InetSocketAddress peer) {
		MultiSession mSession = multiSessionMap.get(peer);
		logger.debug("found MultiSession(UDP) = [{}], Total MultiSession size = {}", mSession == null ? "null!!!" : mSession.getSessionId().toString(), multiSessionMap.size());
		return mSession;
	}

	/**
	 * 
	 * @param session
	 *            - IoSession
	 * @return
	 */
	public MultiSession setMultiSession(IoSession session) {
		MultiSession mSession = new MultiSession(session);
		multiSessionMap.put(mSession.getSessionId(), mSession);
		logger.debug("Add IoSession = [ID={}]{}, total session = {}", session.getId(), session.getRemoteAddress(), multiSessionMap.size());

		return mSession;
	}

	/**
	 * 
	 * @param peer
	 *            - InetSocketAddress
	 * @param connector
	 *            - DTLSConnector
	 * @return
	 */
	public MultiSession setMultiSession(InetSocketAddress peer, DTLSConnector connector) {
		MultiSession mSession = new MultiSession(peer, connector);
		multiSessionMap.put(mSession.getSessionId(), mSession);
		logger.debug("Add DTLS Session = {}, total session = {}", peer.toString(), multiSessionMap.size());

		return mSession;
	}

	/**
	 * 
	 * @param sessionId
	 *            - IoSession
	 */
	public void deleteMultiSession(IoSession session) {
		logger.info("###A MultiSession[ID={}]{} delete Start, total session = {}", session.getId(), session.getRemoteAddress(), multiSessionMap.size());
		multiSessionMap.remove(session.getId());
		getMultiSession(session).destroy();
		logger.info("###A MultiSession[ID={}]{} delete Complete, total session = {}", session.getId(), session.getRemoteAddress(), multiSessionMap.size());
	}

	/**
	 * 
	 * @param peer
	 *            - InetSocketAddress
	 */
	public void deleteMultiSession(InetSocketAddress peer) {
		logger.info("###B MultiSession[{}] delete Start, total session = {}", String.valueOf(peer), multiSessionMap.size());
		multiSessionMap.remove(peer);
		getMultiSession(peer).destroy();
		logger.info("###B MultiSession[{}] delete Complete, total session = {}", String.valueOf(peer), multiSessionMap.size());
	}

	/**
	 * 
	 * @param mSession
	 *            - MultiSession
	 */
	public void deleteMultiSession(MultiSession mSession) {
		logger.info("###C MultiSession[{}] delete Start, total session = {}", String.valueOf(mSession.getSessionId()), multiSessionMap.size());

		multiSessionMap.remove(mSession.getSessionId());
		mSession.destroy();

		logger.info("###C MultiSession[{}] delete Complete, total session = {}", String.valueOf(mSession.getSessionId()), multiSessionMap.size());
	}
	
	public void printMultiSessionRemoteAddress(){
		Iterator<Object> it = multiSessionMap.keySet().iterator();
		StringBuilder sb = new StringBuilder();
		while(it.hasNext()){
			sb.append(multiSessionMap.get(it.next()).getRemoteAddress());
		}
		logger.debug("Total MultiSession size = {}, MultiSession Address = [{}]", multiSessionMap.size(), sb.toString());
	}
}
