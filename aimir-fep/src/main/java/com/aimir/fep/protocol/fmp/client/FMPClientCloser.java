package com.aimir.fep.protocol.fmp.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;

public class FMPClientCloser {
	private static Log log = LogFactory.getLog(FMPClientCloser.class);

	private IoConnector connector = null;
	private IoSession session = null;
	private int closeWaitTime = Integer.parseInt(FMPProperty.getProperty("protocol.if4.closewait.timeout", "10000"));
	private boolean loopFlag = true;
	private boolean doClose = true;
	
	public FMPClientCloser(IoSession session) {
		this.session = session;
	}
		
	public FMPClientCloser(IoConnector connector, IoSession session) {
		this.connector = connector;
		this.session = session;
	}

	public void close() {
		log.debug("### Close Immediately. Start. ###");

		CloseFuture future = (session == null) ? null : session.closeNow();
		if(future != null ) future.awaitUninterruptibly();
		session = null;

		if (connector != null) {
			connector.dispose();
			connector = null;
		}

		log.debug("### Close Immediately. Finished. ###");
	}

	/**
	 * 1. Send EOT Frame <br>
	 * 2. Session close. EOT 전송후 곧바로 Close 하지 않고 DCU가 Close
	 * 하기를 대기하되 Close가 되지 않으면 최대 closeWaitTime까지 대기후 HES쪽에서 Close를 진행한다.
	 * 
	 * @param closeWaitTime
	 *            millisecond
	 */
	public void closeAfterSendEOT(int closeWaitTime) {
		this.closeWaitTime = closeWaitTime;
		closeAfterSendEOT();
	}

	/**
	 * 1. Send EOT Frame 
	 * 2. Session close. EOT 전송후 곧바로 Close 하지 않고 DCU가 Close
	 * 하기를 대기하되 Close가 되지 않으면 최대 closeWaitTime까지 대기후 HES쪽에서 Close를 진행한다.
	 * 
	 * CloseWaitTime : protocol.if4.closewait.timeout (default : 10000
	 * millisecond)
	 */
	public void closeAfterSendEOT() {
		new closer().start();
	}

	synchronized public void cancelClose() {
		loopFlag = false;
		doClose = false;
		log.debug("Set cancel close flag = " + !doClose);
	}

	/**
	 * Close Thread
	 */
	protected class closer extends Thread {
		private Object resMonitor = new Object();

		@Override
		public void run() {
			log.debug("### closeAfterSendEOT Start. ###");

			/** TEST CODE */
			//        log.debug("######### 1. Session Status : " + (session == null ? "null~!!" : session.getRemoteAddress()) + " ############");
			//    	  log.debug("	connector is null = " + (connector == null ? "true" : "false"));
			//        if(connector != null) {
			//            log.debug("	Managed Session count = " + connector.getManagedSessionCount());        	
			//        }
			//        log.debug("	Session is null = " + (session == null ? "true" : "false"));
			//        if(session != null) {
			//            log.debug("	Session is Acitve = " + session.isActive());
			//            log.debug("	Session is Connected = " + session.isConnected());
			//            log.debug("	Session is Closing = " + session.isClosing());        	
			//        }

			if (session != null && !session.isClosing()) {
				session.setAttributeIfAbsent(CommonConstants.SessionKey.SESSION_SUSTAINABLE, false);  // SP-901
				session.write(FrameUtil.getEOT());
				log.debug("######### Send EOT to [" + session.getRemoteAddress() + "]");

				try {

					long stime = System.currentTimeMillis();
					long ctime = 0;
					while ((!(boolean) session.getAttribute(CommonConstants.SessionKey.SESSION_SUSTAINABLE)) 
							&& loopFlag == true 
							&& session.isConnected()) {
						waitResponse(500);
						ctime = System.currentTimeMillis();
						if ((ctime - stime) > closeWaitTime) {
							loopFlag = false;
							log.warn("## Connection close after wait for Timeout : " + closeWaitTime / 1000 + "s");
						}
					}
				} catch (Exception e) {
					log.warn("Connection close error - " + e.getMessage(), e);
				} finally {
					if (!(boolean) session.getAttribute(CommonConstants.SessionKey.SESSION_SUSTAINABLE)) {
						log.debug("Closing....");
						CloseFuture future = session.closeNow();
						future.awaitUninterruptibly();
						session = null;

						if (connector != null) {
							connector.dispose();
							connector = null;
						}
					} else {
						log.info("Cancel session close = [" + session.getRemoteAddress() + "]");
					}
				}
			}else {
				log.debug("session == " + ((session == null) ? "null" : "not null, session.isClosing()="+session.isClosing()) );
				if (connector != null) {
					connector.dispose();
					log.debug("connector disposed.");
					connector = null;
				}
			}

			/** TEST CODE */
			//        log.debug("######### 2. Session Status : " + (session == null ? "null~!!" : session.getRemoteAddress()) + " ############");
			//    	  log.debug("	connector is null = " + (connector == null ? "true" : "false"));
			//        if(connector != null) {
			//            log.debug("	Managed Session count = " + connector.getManagedSessionCount());        	
			//        }
			//        log.debug("	Session is null = " + (session == null ? "true" : "false"));
			//        if(session != null) {
			//            log.debug("	Session is Acitve = " + session.isActive());
			//            log.debug("	Session is Connected = " + session.isConnected());
			//            log.debug("	Session is Closing = " + session.isClosing());        	
			//        }

			log.debug("### closeAfterSendEOT Finished. ###");
		}

		private void waitResponse(int waitTime) {
			synchronized (resMonitor) {
				try {
					resMonitor.wait(waitTime);
				} catch (InterruptedException e) {
					log.warn("ResMonitor Interrupt Error - " + e.getMessage(), e);
				}
			}
		}
	}

}
