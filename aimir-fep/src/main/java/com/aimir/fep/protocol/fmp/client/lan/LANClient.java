package com.aimir.fep.protocol.fmp.client.lan;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.client.FMPClientCloser;
import com.aimir.fep.protocol.fmp.client.FMPClientProtocolHandler;
import com.aimir.fep.protocol.fmp.client.FMPClientProtocolProvider;
import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.frame.ControlDataConstants;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.ErrorCode;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.AlarmData;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.DFData;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.RMDData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.protocol.fmp.server.FMPSslContextFactory;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.CommLog;
import com.aimir.util.TimeUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

/**
 * MCU TCP Packet Client
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class LANClient implements Client
{
    private Log log = LogFactory.getLog(LANClient.class);
    private LANTarget target = null;
    private int CONNECT_TIMEOUT =
        Integer.parseInt(FMPProperty.getProperty("protocol.connection.timeout", 30+"")); // seconds
    private IoConnector connector = null;
    private FMPClientProtocolProvider provider = null;
    private IoSession session = null;
    private long MAX_MCUID = 4294967295L;
    private ProcessorHandler logProcessor = DataUtil.getBean(ProcessorHandler.class);
    private Integer activatorType = new Integer(
            FMPProperty.getProperty("protocol.system.FEP","1"));
    private Integer targetType = new Integer(
            FMPProperty.getProperty("protocol.system.MCU","2"));
    private Integer protocolType = new Integer(
            FMPProperty.getProperty("protocol.type.LAN", "5"));
    
    private int closeWaitTime = Integer.parseInt(FMPProperty.getProperty("protocol.if4.closewait.timeout", "10000"));
    private Object resMonitor = new Object();
    
    
    /**
     * constructor
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public LANClient() throws GeneralSecurityException, IOException
    {
    }

    /**
     * constructor
     * @param target <code>TcpTarget</code> target
     * @throws IOException
     * @throws GeneralSecurityException
     */
    public LANClient(LANTarget target) throws GeneralSecurityException, IOException
    {
        this.target = target;
    }

    /**
     * initialize
     * create IoProtocolConnector
     * create FMPClientProtocolProvider
     * @throws IOException
     * @throws GeneralSecurityException
     */
    private void init() throws GeneralSecurityException, IOException
    {
        connector = new NioSocketConnector();
        connector.setDefaultLocalAddress(
                new InetSocketAddress(InetAddress.getByName(FMPProperty.getProperty("fep.ipv6.addr")), 0));
        FMPSslContextFactory.setSslFilter(connector);
        provider = new FMPClientProtocolProvider();
        if (!connector.getFilterChain().contains(getClass().getName()))
            connector.getFilterChain().addLast(getClass().getName(),
                    new ProtocolCodecFilter(provider.getCodecFactory()));
        connector.setHandler(provider.getHandler());
    }

    public synchronized void connect(boolean meterConnect) throws Exception
    {
    	connect();
    }

    /**
     * connect to Target and wait ENQ
     *
     * @throws Exception
     */
    public synchronized void connect() throws Exception
    {
        init();
        
        if(session != null && session.isConnected())
            return;

        for( ;; )
        {
            try
            {
                connector.setConnectTimeoutMillis(CONNECT_TIMEOUT*1000);
                ConnectFuture future = connector.connect(new InetSocketAddress(
                        target.getIpAddr(),
                        target.getPort()));
                future.awaitUninterruptibly();

                if (!future.isConnected()) {
                    //throw new Exception("not yet");
                	throw new Exception(ErrorCode.getMessage(ErrorCode.MCU_CANNOT_CONNECT));
                }

                session = future.getSession();
                session.getConfig().setWriteTimeout(CONNECT_TIMEOUT);
                session.getConfig().setIdleTime(IdleStatus.READER_IDLE,
                        CONNECT_TIMEOUT);
                session.setAttribute("nameSpace", target.getNameSpace() != null ? target.getNameSpace() : null);                
                log.debug("SESSION CONNECTED[" + session.isConnected() + "], nameSpace = [" + target.getNameSpace() + "]");

                break;
            }
            catch( Exception e )
            {
                /*
                try { LookupUtil.updateMCUNetworkStatus(
                        target.getMcuId(),"0");
                }catch(Exception ex){log.error(ex,ex);}
                */
                log.error( "Failed to connect. host["
                        + target.getIpAddr()+"] port["
                        + target.getPort()+"]",e );
                throw e;
            }
        }

        if(session == null)
            throw new Exception("Failed to connect. host["
                        + target.getIpAddr()+"] port["
                        + target.getPort()+"]");

        FMPClientProtocolHandler handler =
            (FMPClientProtocolHandler)session.getHandler();
        handler.setResponseTimeout(target.getTimeout());
        log.debug("Handler timeout set["+ target.getTimeout() + "]");
        try
        {
            //handler.waitENQ();
        	handler.waitIF4Nego(session);
        }catch(Exception ex)
        {
            close();
            throw ex;
        }
    }

    private void saveCommLog(CommLog commLog)
    {
        try
        {
            if(this.logProcessor != null)
                this.logProcessor.putServiceData(ProcessorHandler.LOG_COMMLOG, commLog);
            else
                log.warn("Log Processor not registered");
        } catch(Exception ex)
        {
            log.error("save Communication Log failed",ex);
        }
    }

    public void setLogProcessor(ProcessorHandler logProcessor)
    {
        this.logProcessor = logProcessor;
    }

    /**
     * set Target
     *
     * @param target <code>TcpTarget</code> target
     */
    public void setTarget(Target target) throws Exception
    {
        if(!(target instanceof LANTarget))
            throw new Exception("not supported target");
        this.target = (LANTarget)target;
    }

    /**
     * request command data to Target and response
     *
     * @param command <code>CommandData</code> request command
     * @return response <code>ServiceData</code> response
     * @throws Exception
     */
    public CommandData sendCommand(CommandData command)
        throws Exception
    {
        try {
            //ProtocolSession session = connect();
            if(session == null || !session.isConnected())
                connect();
            CommLog commLog = new CommLog();
            ServiceDataFrame frame = new ServiceDataFrame();
            frame.setSvc(GeneralDataConstants.SVC_C);
            long mcuId = Long.parseLong(target.getTargetId());
            frame.setMcuId(new UINT(mcuId));
            if(mcuId > MAX_MCUID)
                throw new Exception("mcuId is too Big: max["
                        + MAX_MCUID+"]");
            command.setAttr(ServiceDataConstants.C_ATTR_REQUEST);
            command.setTid(FrameUtil.getCommandTid());
            frame.setSvcBody(command.encode());

            
            FMPClientProtocolHandler handler =
                (FMPClientProtocolHandler)session.getHandler();
            ServiceData sd;
            String startTime = TimeUtil.getCurrentTime();
            commLog.setStartDate(startTime.substring(0,8));
            commLog.setStartTime(startTime.substring(8,14));
            commLog.setStartDateTime(startTime);
            commLog.setProtocolCode(CommonConstants.getProtocol(protocolType+""));
            commLog.setSenderTypeCode(CommonConstants.getSenderReceiver(activatorType+""));
            commLog.setSenderId(DataUtil.getFepIdString());
            commLog.setReceiverTypeCode(CommonConstants.getSenderReceiverByName(target.getReceiverType()));
            commLog.setReceiverId(target.getReceiverId());
            commLog.setOperationCode(MIBUtil.getInstance().getName(
                        command.getCmd().toString()));
            long s = System.currentTimeMillis();
            commLog.setSendBytes(send(frame));
            sd = handler.getResponse(session,command.getTid().getValue());
            long e = System.currentTimeMillis();
            if(sd == null)
                return null;
            log.debug("Received Response TID : "
                    + ((CommandData)sd).getTid());
            commLog.setEndTime(TimeUtil.getCurrentTime());
            commLog.setRcvBytes(sd.getTotalLength());
            commLog.setUnconPressedRcvBytes(sd.getTotalLength());
            commLog.setTotalCommTime((int)(e-s));
            commLog.setSvcTypeCode(CommonConstants.getHeaderSvc("C"));
            if(((CommandData)sd).getErrCode().getValue() > 0)
            {
                commLog.setCommResult(0);
                commLog.setDescr(ErrorCode.getMessage(((CommandData)sd).getErrCode().getValue()));
            }
            else
            {
                commLog.setCommResult(1);
            }
            log.info(commLog.toString());
            //commonLogger.sendCommLog(commLog);
            saveCommLog(commLog);
            return (CommandData)sd;
        }
        catch (Exception e)
        {
            if(!command.getCmd().toString().equals("198.3.0"))
            {
                log.error("sendCommand failed : command["+command+"]",e);
            } else {
                log.error("sendCommand failed : command["+command.getCmd()
                        +"]",e);
            }
            throw e;
        }
        finally
        {
            close();
        }
    }

    /**
     * send Alarm to Target
     *
     * @param alarm <code>AlarmData</code> send alarm
     * @throws Exception
     */
    public void sendAlarm(AlarmData alarm) throws Exception
    {
		try {
			// ProtocolSession session = connect();
			if (session == null || !session.isConnected())
				connect();
			ServiceDataFrame frame = new ServiceDataFrame();
			frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
			frame.setAttrByte(GeneralDataConstants.ATTR_START);
			frame.setAttrByte(GeneralDataConstants.ATTR_END);
			long mcuId = Long.parseLong(target.getTargetId());
			if (mcuId > MAX_MCUID)
				throw new Exception("mcuId is too Big: max[" + MAX_MCUID + "]");
			frame.setMcuId(new UINT(mcuId));
			frame.setSvcBody(alarm.encode());
			// log.debug("alarm size : " + alarm.encode().length);
			// frame.setAttrByte(GeneralDataConstants.ATTR_COMPRESS);
			frame.setSvc(GeneralDataConstants.SVC_A);

			send(frame);

			log.info("sendAlarm : finished");
		} catch (Exception ex) {
			throw ex;
		} finally {
			close();
		}
    }

    /**
     * send Event to Target
     *
     * @param event <code>EventData</code> event alarm
     * @throws Exception
     */
    public void sendEvent(EventData event) throws Exception
    {
		try {
			// ProtocolSession session = connect();
			if (session == null || !session.isConnected())
				connect();
			ServiceDataFrame frame = new ServiceDataFrame();
			frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
			frame.setAttrByte(GeneralDataConstants.ATTR_START);
			frame.setAttrByte(GeneralDataConstants.ATTR_END);
			long mcuId = Long.parseLong(target.getTargetId());
			if (mcuId > MAX_MCUID)
				throw new Exception("mcuId is too Big: max[" + MAX_MCUID + "]");
			frame.setMcuId(new UINT(mcuId));
			frame.setSvcBody(event.encode());
			// log.debug("event size : " + event.encode().length);
			// frame.setAttrByte(GeneralDataConstants.ATTR_COMPRESS);
			frame.setSvc(GeneralDataConstants.SVC_E);

			send(frame);

			log.info("sendEvent : finished");
		} catch (Exception ex) {
			throw ex;
		} finally {
			close();
		}
    }

    /**
     * send Measurement Data to Target
     *
     * @param md <code>MDData</code> Measurement Data
     * @throws Exception
     */
    public void sendMD(MDData md) throws Exception
    {
		try {
			// ProtocolSession session = connect();
			if (session == null || !session.isConnected())
				connect();
			ServiceDataFrame frame = new ServiceDataFrame();
			frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
			frame.setAttrByte(GeneralDataConstants.ATTR_START);
			frame.setAttrByte(GeneralDataConstants.ATTR_END);
			long mcuId = Long.parseLong(target.getTargetId());
			frame.setMcuId(new UINT(mcuId));
			if (mcuId > MAX_MCUID)
				throw new Exception("mcuId is too Big: max[" + MAX_MCUID + "]");
			frame.setSvcBody(md.encode());
			// frame.setAttrByte(GeneralDataConstants.ATTR_COMPRESS);
			frame.setSvc(GeneralDataConstants.SVC_M);

			send(frame);

			log.info("sendMD : finished");
		} catch (Exception ex) {
			throw ex;
		} finally {
			close();
		}
    }
    
    public void sendMDWithCompress(MDData md) throws Exception
    {
		try {
			if (session == null || !session.isConnected())
				connect();
			ServiceDataFrame frame = new ServiceDataFrame();
			frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
			frame.setAttrByte(GeneralDataConstants.ATTR_START);
			frame.setAttrByte(GeneralDataConstants.ATTR_END);
			long mcuId = Long.parseLong(target.getTargetId());
			frame.setMcuId(new UINT(mcuId));
			if (mcuId > MAX_MCUID)
				throw new Exception("mcuId is too Big: max[" + MAX_MCUID + "]");
			frame.setSvcBody(md.encode());
			frame.setAttrByte(GeneralDataConstants.ATTR_COMPRESS);
			frame.setSvc(GeneralDataConstants.SVC_M);
			sendWithCompress(frame);

			log.info("sendMD : finished");
		} catch (Exception ex) {
			throw ex;
		} finally {
			close();
		}
    }
    

    
    public void sendDFWithCompress(DFData df) throws Exception
    {
		try {
			if (session == null || !session.isConnected())
				connect();
			ServiceDataFrame frame = new ServiceDataFrame();
			frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
			frame.setAttrByte(GeneralDataConstants.ATTR_START);
			frame.setAttrByte(GeneralDataConstants.ATTR_END);
			long mcuId = Long.parseLong(target.getTargetId());
			frame.setMcuId(new UINT(mcuId));
			if (mcuId > MAX_MCUID)
				throw new Exception("mcuId is too Big: max[" + MAX_MCUID + "]");
			frame.setSvcBody(df.encode());
			frame.setAttrByte(GeneralDataConstants.ATTR_COMPRESS);
			frame.setSvc(GeneralDataConstants.SVC_D);
			sendWithCompress(frame);

			log.info("sendMD : finished");
		} catch (Exception ex) {
			throw ex;
		} finally {
			close();
		}
    }
    
    
    /**
     * send R Measurement Data to Target 
     *
     * @param md <code>RMDData</code>R Measurement Data
     * @throws Exception
     */
    public void sendRMD(RMDData rmd) throws Exception
    {
        try {
            //ProtocolSession session = connect();
            if(session == null || !session.isConnected())
                connect();
            ServiceDataFrame frame = new ServiceDataFrame();
            frame.setAttrByte(GeneralDataConstants.ATTR_ACK);
            frame.setAttrByte(GeneralDataConstants.ATTR_START);
            frame.setAttrByte(GeneralDataConstants.ATTR_END);
            long mcuId = Long.parseLong(target.getTargetId());
            frame.setMcuId(new UINT(mcuId));
            if(mcuId > MAX_MCUID) 
                throw new Exception("mcuId is too Big: max["
                        + MAX_MCUID+"]");
            frame.setSvcBody(rmd.encode());
            frame.setSvc(GeneralDataConstants.SVC_R);
    
            send(frame);
    
            log.info("sendRMD : finished");
        } catch (Exception ex) {
            throw ex;
        } finally {
            close();
        }
    }

    /**
     * close TCPClient session
     */
    public void close()
    {
        close(false);
    }

    /**
     * close TCPClient session and wait completed
     *
     * @param immediately <code>boolean</code> immediately
     */
    public void close(boolean immediately) {
    	if(immediately)
    		new FMPClientCloser(connector, session).close();
    	else
    		new FMPClientCloser(connector, session).closeAfterSendEOT();
    }
    
    /**
     * @deprecated
     * @param immediately
     */
    public void close_org(boolean immediately)
    {
        log.debug("### LAN Client Close Start. ###");

        /** TEST CODE */
//        log.debug("######### 1. Session Status : " + (session == null ? "null~!!" : session.getRemoteAddress()) + " ############");
//    	log.debug("	connector is null = " + (connector == null ? "true" : "false"));
//        if(connector != null) {
//            log.debug("	Managed Session count = " + connector.getManagedSessionCount());        	
//        }
//        log.debug("	Session is null = " + (session == null ? "true" : "false"));
//        if(session != null) {
//            log.debug("	Session is Acitve = " + session.isActive());
//            log.debug("	Session is Connected = " + session.isConnected());
//            log.debug("	Session is Closing = " + session.isClosing());        	
//        }
        
        /*
         *	EOT 전송후 곧바로 Close 하지 않고 DCU가 Close 하기를 대기하되 
         *  Close가 되지 않으면 최대 closeWaitTime까지 대기후 HES쪽에서 Close를 진행한다.
         */
        if(session != null && !session.isClosing()) {
            session.write(FrameUtil.getEOT());
            log.debug("######### Send EOT to [" + session.getRemoteAddress() + "]");
            
            try {
            	boolean flag = true;
            	long stime = System.currentTimeMillis();            	
            	long ctime = 0;
	            while(flag == true && session.isConnected()){
        			waitResponse(500);
                    ctime = System.currentTimeMillis();
					if((ctime - stime) > closeWaitTime)
                    {
						flag = false;
						log.warn("## Connection close after wait for a Timeout : " + closeWaitTime / 1000 + "s");
                    }
	            }
            } catch (Exception e) {
    			log.warn("Connection close error - " + e.getMessage(), e);
			} finally {
	            CloseFuture future = session.closeNow();
	            future.awaitUninterruptibly();
	            session = null;
	            
	            if(connector != null) {
	                connector.dispose();
	                connector = null;
	            }
			}
        }
        
        /** TEST CODE */
//        log.debug("######### 2. Session Status : " + (session == null ? "null~!!" : session.getRemoteAddress()) + " ############");
//    	log.debug("	connector is null = " + (connector == null ? "true" : "false"));
//        if(connector != null) {
//            log.debug("	Managed Session count = " + connector.getManagedSessionCount());        	
//        }
//        log.debug("	Session is null = " + (session == null ? "true" : "false"));
//        if(session != null) {
//            log.debug("	Session is Acitve = " + session.isActive());
//            log.debug("	Session is Connected = " + session.isConnected());
//            log.debug("	Session is Closing = " + session.isClosing());        	
//        }
        
        log.debug("### LAN Client Close Finished. ###");
    }
    
    public void waitResponse(int waitTime)
    {
        synchronized(resMonitor)
        { 
            try { 
            	resMonitor.wait(waitTime);
            } catch(InterruptedException e) {
            	log.warn("ResMonitor Interrupt Error - " + e.getMessage(), e);
            }
        }
    }

    /**
     * check whether connected or not
     *
     * @return connected <code>boolean</code>
     */
    public boolean isConnected()
    {
        if(session == null)
            return false;
        return session.isConnected();
    }

    /**
     * wait ACK
     */
    private void waitAck(IoSession session, int sequence)
        throws Exception
    {
        log.debug("waitACK.SEQ:" + sequence);
        FMPClientProtocolHandler handler =
           (FMPClientProtocolHandler)session.getHandler();
        handler.waitAck(session,sequence);
    }

    /**
     * send GeneralDataFrame to Target
     *
     * @param frame <code>GeneralDataFrame</code>
    private synchronized void send(GeneralDataFrame frame)
        throws Exception
    {
        byte[] bx = frame.encode();
        byte[] mbx = null;
        ByteBuffer buf = null;
        ArrayList framelist = FrameUtil.makeMultiEncodedFrame(bx);
        session.setAttribute("sendframes",framelist);
        int lastIdx = framelist.size() - 1;
        mbx = (byte[])framelist.get(lastIdx);
        int lastSequence = DataUtil.getIntToByte(mbx[1]);
        boolean isSetLastSequence = false;
        if((lastIdx / GeneralDataConstants.FRAME_MAX_SEQ) < 1)
        {
            session.setAttribute("lastSequence",
                    new Integer(lastSequence));
            isSetLastSequence = true;
        }
        Iterator iter = framelist.iterator();
        int cnt = 0;
        int seq = 0;
        ControlDataFrame wck = null;
        while(iter.hasNext())
        {
            if(!isSetLastSequence &&
                    ((lastIdx - cnt) /
                     GeneralDataConstants.FRAME_MAX_SEQ) < 1)
            {
                session.setAttribute("lastSequence",
                        new Integer(lastSequence));
                isSetLastSequence = true;
            }
            mbx = (byte[])iter.next();
            seq = DataUtil.getIntToByte(mbx[1]);
            buf = ByteBuffer.allocate(mbx.length);
            buf.put(mbx,0,mbx.length);
            buf.flip();
            session.write(buf);
            FrameUtil.waitSendFrameInterval();
            if(((cnt+1) % GeneralDataConstants.FRAME_WINSIZE)== 0)
            {
                log.debug("WCK : start : " + (seq -
                    GeneralDataConstants.FRAME_WINSIZE + 1)
                        +"end : " + seq);
                wck =FrameUtil.getWCK((seq-
                            GeneralDataConstants.FRAME_WINSIZE + 1),
                        seq);
                session.write(wck);
                session.setAttribute("wck",wck);
                waitAck(session,cnt);
            }
            cnt++;
        }
        if(frame.getSvc() != GeneralDataConstants.SVC_C)
        {
            if((cnt % GeneralDataConstants.FRAME_WINSIZE) != 0)
            {
                if(cnt > 1)
                {
                    log.debug("WCK : start : " + (seq -(seq%
                        GeneralDataConstants.FRAME_WINSIZE))
                            + "end : " + seq);
                    wck =FrameUtil.getWCK(seq-(seq%
                                GeneralDataConstants.FRAME_WINSIZE)
                            ,seq);
                    session.write(wck);
                    session.setAttribute("wck",wck);
                }
                waitAck(session,cnt-1);
            }
        }
        FrameUtil.waitSendFrameInterval();
        session.removeAttribute("wck");
    }
    */
    /**
     * send GeneralDataFrame to Target
     *
     * @param frame <code>GeneralDataFrame</code>
     */
    private synchronized int send(GeneralDataFrame frame)
        throws Exception
    {
    	log.info("### LanClient Send");
        int sendBytes = 0;
        //byte[] bx = frame.encode();
        byte[] bx = frame.encodeWithCompress();
        
        byte[] mbx = null;
        IoBuffer buf = null;
        
        ArrayList<?> framelist = null;
        if(frame.isAttrByte(GeneralDataConstants.ATTR_COMPRESS)){
            framelist = FrameUtil.makeMultiEncodedFrame(bx, session, true, frame.getUnCompressedLength());        	
        }else{
            framelist = FrameUtil.makeMultiEncodedFrame(bx, session);
        }

        session.setAttribute("sendframes",framelist); 
        log.info("### Multi frameList size["+framelist.size()+"]");
        
        int lastIdx = framelist.size() - 1;
        mbx = (byte[])framelist.get(lastIdx);
        int lastSequence = DataUtil.getIntToByte(mbx[1]);
        boolean isSetLastSequence = false;
        if((lastIdx / GeneralDataConstants.FRAME_MAX_SEQ) < 1)
        {
            session.setAttribute("lastSequence",
                    new Integer(lastSequence));
            isSetLastSequence = true;
        }
        Iterator<?> iter = framelist.iterator();
        int cnt = 0;
        int seq = 0;
        ControlDataFrame wck = null;
        while(iter.hasNext())
        {
            if(!isSetLastSequence &&
                    ((lastIdx - cnt) /
                     GeneralDataConstants.FRAME_MAX_SEQ) < 1)
            {
                session.setAttribute("lastSequence",
                        new Integer(lastSequence));
                isSetLastSequence = true;
            }
            mbx = (byte[])iter.next();
            sendBytes+=mbx.length;
            seq = DataUtil.getIntToByte(mbx[1]);
            buf = IoBuffer.allocate(mbx.length);
            buf.put(mbx,0,mbx.length);
            buf.flip();
            log.info(buf.getHexDump());
            session.write(buf);
            // FrameUtil.waitSendFrameInterval();
            if(((cnt+1) % GeneralDataConstants.FRAME_WINSIZE)== 0)
            {
                log.debug("WCK : start : " + (seq -
                    GeneralDataConstants.FRAME_WINSIZE + 1)
                        +"end : " + seq);
                wck =FrameUtil.getWCK((seq-
                            GeneralDataConstants.FRAME_WINSIZE + 1),
                        seq);
                sendBytes+=GeneralDataConstants.HEADER_LEN
                    + GeneralDataConstants.TAIL_LEN
                    + wck.getArg().getValue().length;
                session.write(wck);
                session.setAttribute("wck",wck);
                waitAck(session,cnt);
            }
            cnt++;
        }
        //if(frame.getSvc() != GeneralDataConstants.SVC_C)
        //{
        if((cnt % GeneralDataConstants.FRAME_WINSIZE) != 0)
        {
            if(cnt > 1)
            {
                log.debug("WCK : start : " + (seq -(seq%
                                GeneralDataConstants.FRAME_WINSIZE))
                        + "end : " + seq);
                wck =FrameUtil.getWCK(seq-(seq%
                            GeneralDataConstants.FRAME_WINSIZE) ,seq);
                sendBytes+=GeneralDataConstants.HEADER_LEN
                    + GeneralDataConstants.TAIL_LEN
                    + wck.getArg().getValue().length;
                session.write(wck);
                session.setAttribute("wck",wck);
                waitAck(session,cnt-1);
            }
        }
        //}
        //FrameUtil.waitSendFrameInterval();
        session.removeAttribute("wck");
        return sendBytes;
    }
    
    /**
     * send GeneralDataFrame to Target
     *
     * @param frame <code>GeneralDataFrame</code>
     */
    private synchronized int sendWithCompress(GeneralDataFrame frame)
        throws Exception
    {
    	log.info("### LanClient Send");
        int sendBytes = 0;
        byte[] bx = frame.encodeWithCompress();
        byte[] mbx = null;
        IoBuffer buf = null;
        
        ArrayList<?> framelist = FrameUtil.makeMultiEncodedFrame(bx, session);
        log.info("### Multi frameList size[" + framelist.size() + "]");
        session.setAttribute("sendframes",framelist);
        int lastIdx = framelist.size() - 1;
        mbx = (byte[])framelist.get(lastIdx);
        int lastSequence = DataUtil.getIntToByte(mbx[1]);
        boolean isSetLastSequence = false;
        if((lastIdx / GeneralDataConstants.FRAME_MAX_SEQ) < 1)
        {
            session.setAttribute("lastSequence",
                    Integer.valueOf(lastSequence));
            isSetLastSequence = true;
        }
        Iterator<?> iter = framelist.iterator();
        int cnt = 0;
        int seq = 0;
        ControlDataFrame wck = null;
        while(iter.hasNext())
        {
            if(!isSetLastSequence  && 
                    ((lastIdx - cnt) /
                     GeneralDataConstants.FRAME_MAX_SEQ) < 1)
            {
                session.setAttribute("lastSequence",
                        Integer.valueOf(lastSequence));
                isSetLastSequence = true;
            }
            mbx = (byte[])iter.next();
            sendBytes += mbx.length;
            seq = DataUtil.getIntToByte(mbx[1]);
            buf = IoBuffer.allocate(mbx.length);
            buf.put(mbx,0,mbx.length);
            buf.flip();
            session.write(buf);
            //FrameUtil.waitSendFrameInterval();
            if(((cnt + 1) % GeneralDataConstants.FRAME_WINSIZE) ==  0)
            {
                log.debug("WCK : start : " + (seq -
                    GeneralDataConstants.FRAME_WINSIZE + 1)
                         + "end : " + seq);
                wck = FrameUtil.getWCK((seq - 
                            GeneralDataConstants.FRAME_WINSIZE + 1),
                        seq);
                sendBytes += GeneralDataConstants.HEADER_LEN
                    + GeneralDataConstants.TAIL_LEN
                    + wck.getArg().getValue().length;
                session.write(wck);
                session.setAttribute("wck",wck);
                waitAck(session,cnt);
            }
            cnt++;
        }

        if((cnt % GeneralDataConstants.FRAME_WINSIZE) != 0)
        {
            if(cnt > 1)
            {
                log.debug("WCK : start : " + (seq - (seq % 
                                GeneralDataConstants.FRAME_WINSIZE))
                        + "end : " + seq);
                wck = FrameUtil.getWCK(seq - (seq % 
                            GeneralDataConstants.FRAME_WINSIZE) ,seq);
                sendBytes += GeneralDataConstants.HEADER_LEN
                    + GeneralDataConstants.TAIL_LEN
                    + wck.getArg().getValue().length;
                session.write(wck);
                session.setAttribute("wck",wck);
                waitAck(session,cnt - 1);
            }
        }
        //}
        //FrameUtil.waitSendFrameInterval();
        session.removeAttribute("wck");
        return sendBytes;
    }
}
