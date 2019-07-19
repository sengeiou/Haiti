/**
 * 
 */
package com.aimir.fep.tool;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.concurrent.Executors;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.FilterEvent;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.aimir.fep.protocol.nip.client.bypass.BypassClientEncoder;
import com.aimir.fep.protocol.nip.client.bypass.HSWClientDecoder;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.DateTimeUtil;

/**
 * @author simhanger
 *
 */
@Service
public class DLMSGetSetByUDP {
	private static Logger logger = LoggerFactory.getLogger(DLMSGetSetByUDP.class);
	private Object resMonitor = new Object();
	private IoConnector connector;
	private IoSession session;
	private String result;

	private void init(int connectionTimeout) {
		connector = new NioDatagramConnector();
		//connector.getSessionConfig().setMinReadBufferSize(20);
		
		connector.setConnectTimeoutMillis(connectionTimeout * 1000L);
		connector.getFilterChain().addLast("logger", new LoggingFilter());		
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ProtocolCodecFactory() {
			public ProtocolDecoder getDecoder(IoSession session) throws Exception {
				return new HSWClientDecoder();
			}

			public ProtocolEncoder getEncoder(IoSession session) throws Exception {
				return new BypassClientEncoder();
			}
		}));
		connector.getFilterChain().addLast("Executor", new ExecutorFilter(Executors.newCachedThreadPool()));
		connector.setHandler(new IoHandler() {
			@Override
			public void sessionCreated(IoSession session) throws Exception {
				logger.debug("### [SESSION CREATE] - {}", session.toString());
			}

			@Override
			public void sessionOpened(IoSession session) throws Exception {
				logger.debug("### [SESSION OPEN] - {}", session.toString());
			}

			@Override
			public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
				logger.debug("### [SESSION IDLE] - {}", session.toString());
			}

			@Override
			public void inputClosed(IoSession session) throws Exception {
				logger.debug("### [SESSION INPUT_CLOSE] - {}", session.toString());
				if (session != null && session.isConnected()) {
					session.closeNow();
				}
			}

			@Override
			public void messageSent(IoSession session, Object message) throws Exception {
				logger.debug("### [SESSION SENT] - {}", session.toString());
				logger.debug("### [SESSION SENT] - {}", Hex.decode((byte[]) message));
			}

			@Override
			public void sessionClosed(IoSession session) throws Exception {
				logger.debug("### [SESSION CLOSED] - {}", session.toString());
			}

			@Override
			public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
				logger.error("### [SESSION EXCEPTION] - {}", session.toString());
				logger.error("### [SESSION EXCEPTION] Cause - " + cause.getMessage(), cause);
				if (session != null && session.isConnected()) {
					session.closeNow();
				}
			}

			@Override
			public void messageReceived(IoSession session, Object message) throws Exception {
				logger.error("### [SESSION RECEIVED] - {}", session.toString());
				logger.debug("### [SESSION RECEIVED] - {}", Hex.decode((byte[]) message));

				result = Hex.decode((byte[]) message);
			}
			
			@Override
			public void event(IoSession session, FilterEvent event) throws Exception {
				logger.debug("### [SESSION EVENT]] - {}", event.toString());
			}
		});

		logger.info("Connector init...");
	}

	/*
	 * Connection.
	 * @param destIp
	 * @param destPort
	 * @param srcPort
	 */
	private void connect(String destIp, int destPort, int srcPort) {
		ConnectFuture future = null;
		if (srcPort < 0) {
			future = connector.connect(new InetSocketAddress(destIp, destPort));	
		} else {
			future = connector.connect(new InetSocketAddress(destIp, destPort), new InetSocketAddress(srcPort));
		}

		future.awaitUninterruptibly();
		if (!future.isConnected()) {
			logger.warn("Connector Connection fail..");
			close();
		}
		session = future.getSession();

		logger.debug("Connector Connection success. DestIp={}, DestPort={}, SrcPort={}", destIp, destPort, srcPort);
	}

	private String getResult(int resultCheckTimeout) {
		String checkData = "Cannot received data.";
		long stime = System.currentTimeMillis();
		long ctime = 0;
		int tempCount = 0;
		while (session.isConnected()) {
			if (result != null) {
				checkData = result;
				break;
			} else {
				waitResponse(500);
				ctime = System.currentTimeMillis();
				//logger.debug("======>>> count = " + ++tempCount + ", resultCheckTimeout=" + resultCheckTimeout + ", (ctime - stime) / 1000 = " + ((ctime - stime) / 1000));
				if (((ctime - stime) / 1000) > resultCheckTimeout) {
					checkData = "Response Timeout : " + resultCheckTimeout;
					break;
				}
			}
		}

		logger.debug("End while.");
		return checkData;
	}

	public void waitResponse(int waitTime) {
		synchronized (resMonitor) {
			try {
				resMonitor.wait(waitTime);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}

	/**
	 * Connector close.
	 */
	private void close() {
		if (connector != null) {
			connector.dispose();
			connector = null;
		}
		logger.debug("### Connector close. ##");
	}

	private void send(byte[] sendData) {
		session.write(sendData);
		logger.debug("Send Data write..");
	}

	/*
	 * args Param list
	 * 1. destIp   : 대상 IP
	 * 2. destPort : 대상 Port
	 * 3. srcPort  : Connector Port. 메시지 수신을 위해 열어놓고자하는 UDP서버의 Port
	 * 4. connectionTimeout : Connector의 Connection time out
	 * 5. resultCheckTimeout : 결과값 올때까지 기다리는 시간.
	 * 6. sendByteData : 전송하고자 하는 바이트데이터
	 * @param args
	 */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();

		logger.info("-----");
		logger.info("-----");
		logger.info("-----");
		logger.info("#### DLMSGetSet Task start. ###");
		logger.info("args[] => {}", Arrays.deepToString(args));
		String sendByteData;
		
		String destIp = null;
		int destPort = -1;
		int srcPort = -1;
		int connectionTimeout = -1;
		int resultCheckTimeout = -1;
		sendByteData = null;

		try {
			if (args == null || args.length < 5 || args[0] == null || args[1] == null || args[2] == null || args[3] == null || args[4] == null || args[5] == null) {
				logger.error("Invalid parameters. please check parameters.");
				return;
			}

			destIp = args[0];
			destPort = Integer.parseInt(args[1]);
			srcPort = Integer.parseInt(args[2]);
			connectionTimeout = Integer.parseInt(args[3]);
			resultCheckTimeout = Integer.parseInt(args[4]);
			sendByteData = args[5];
			logger.info("Received Parameters : destIp={}, destPort={}, srcPort={}, connectionTimeout={}, resultCheckTimeout={}, sendByteData={}", destIp, destPort, srcPort, connectionTimeout, resultCheckTimeout, sendByteData);

			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-DLMSGetSet.xml" });
			DataUtil.setApplicationContext(ctx);

			DLMSGetSetByUDP task = (DLMSGetSetByUDP) ctx.getBean(DLMSGetSetByUDP.class);
			task.init(connectionTimeout);
			task.connect(destIp, destPort, srcPort);
			
			Thread.sleep(3000);  
			logger.info("watting 3sec for session open....");
			task.send(DataUtil.readByteString(sendByteData));
			
			String receivedData = task.getResult(resultCheckTimeout);
			logger.info("### Result Data = {}", receivedData);
			
			task.close();
			
			
			logger.info("DLMSGetSet Process finished.");
		} catch (Exception e) {
			logger.error("DLMSGetSet excute error - " + e, e);
		} finally {
			logger.info("#### DLMSGetSet Task finished - Elapse Time : {} ###", DateTimeUtil.getElapseTimeToString(System.currentTimeMillis() - startTime));
			System.exit(0);
		}
	}

}
