package com.aimir.fep.protocol.reversegprs;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.FilterEvent;

import com.aimir.fep.bypass.actions.CommandAction;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.UINT;
import com.aimir.fep.protocol.fmp.exception.FMPACKTimeoutException;
import com.aimir.fep.protocol.fmp.exception.FMPException;
import com.aimir.fep.protocol.fmp.exception.FMPResponseTimeoutException;
import com.aimir.fep.protocol.fmp.frame.ControlDataConstants;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.protocol.fmp.frame.service.EventData_1_2;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.AsyncCommandParam;

public class CommHandler implements IoHandler{

    private static Log log = LogFactory.getLog(CommHandler.class);
    
    private final int IDLE_TIME = Integer.parseInt(FMPProperty.getProperty(
            "protocol.idle.time","5"));
    private int retry = Integer.parseInt(FMPProperty.getProperty(
            "protocol.retry","3"));
    private final int WAIT_TIME = Integer.parseInt(FMPProperty.getProperty(
            "protocol.bypass.waittime","30"));
    private int ackTimeout = Integer.parseInt(
            FMPProperty.getProperty("protocol.ack.timeout","3"));
    private int responseTimeout = Integer.parseInt(
            FMPProperty.getProperty("protocol.response.timeout","15"));
    
    private Object ackMonitor = new Object();
    private ControlDataFrame ack = null;
    private CommandAction action = null;
    private Object resMonitor = new Object();
    private Hashtable response = new Hashtable();

    private SessionCache cache = null;
    
    public CommHandler(){

    }

    public SessionCache getSessionCache(){
    	return this.cache;
    }

    public void setSessionCache(SessionCache cache) {
    	this.cache = cache;
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        log.error(cause, cause);
        log.error(cause+"  from "+session.getRemoteAddress());
        session.closeNow();
        //log.info("!!!ExceptionCaught SessionClosed="+session.getRemoteAddress());
    	// Close connection when unexpected exception is caught.
    	//String targetId = (String) session.getAttribute("client");
    	//clients.remove(client);
    	//sessions.remove(session);
    	//session.closeNow();
    	//cache.removeSession(targetId, session);
    }   
    
    @Override
    public void sessionClosed(IoSession session) throws Exception {
    	String targetId = (String) session.getAttribute("client");
    	//clients.remove(client);
    	//sessions.remove(session);
    	
        log.info("!!!SessionClosed="+session.getRemoteAddress());
        session.removeAttribute(session.getRemoteAddress());
        CloseFuture future = session.closeNow();
        cache.removeSession(targetId, session);
	}

	@Override
	public void messageSent(IoSession arg0, Object arg1) throws Exception {
		// TODO Auto-generated method stub		
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub		
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		// TODO Auto-generated method stub		
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
        log.info(session.getRemoteAddress());
        session.write(new ControlDataFrame(ControlDataConstants.CODE_ENQ));
        session.getConfig().setIdleTime(IdleStatus.READER_IDLE, IDLE_TIME);
        this.ack = null;	
	}  
	
    /**
     * wait ACK ControlDataFrame
     */
    public void waitAck()
    {
        synchronized(ackMonitor)
        { 
            try { 
                //log.debug("ACK Wait");
                ackMonitor.wait(500); 
                //log.debug("ACK Received");
            } catch(InterruptedException ie) {ie.printStackTrace();}
        }
    }
    
    /**
     * wait util received ACK ControlDataFrame
     *
     * @param session <code>IoSession</code> session
     * @param sequence <code>int</code> wait ack sequence
     */
    public void waitAck(IoSession session, int sequence)
        throws Exception
    { 
        //log.debug("waitAck "+ sequence);
        int waitAckCnt = 0;
        while(session.isConnected())
        {
            if(ack == null)
            {
                waitAck();
                waitAckCnt++;
                if((waitAckCnt / 2) > ackTimeout)
                { 
                    throw new FMPACKTimeoutException(
                        "ACK Wait Timeout[" +ackTimeout +"]");
                }
            }
            else
            {
                int ackseq = FrameUtil.getAckSequence(this.ack);
                //log.debug("ackseq : " + ackseq);
                if(sequence == ackseq)
                {
                    setAck(null);
                    break;
                }
                else
                    setAck(null);
            }
        }
        //log.debug("finished waitAck "+ sequence);
    }
    
    /**
     * set ACK ControlDataFrame
     *
     * @param ack <code>ControlDataFrame</code> ack
     */
    public void setAck(ControlDataFrame ack)
    {
        this.ack = ack;
    }
    
    /*
    @Override
    public void messageReceived(IoSession session, Object message) {
    	log.info("received: " + message);
    	String client = (String) session.getAttribute("client");
    	
    	if (!clients.contains(client)){
            sessions.add(session);
            session.setAttribute("client", client);
            MdcInjectionFilter.setProperty(session, "client", client);
            // Allow all users
            clients.add(client);
    	}
    }
    */
    
    @Override
    public void messageReceived(IoSession session, Object message)
            throws Exception {
        log.info(message);
        if (message instanceof ControlDataFrame) {
            ControlDataFrame cdf = (ControlDataFrame)message;
            byte code = cdf.getCode();
            log.info("Control Frame Code=["+code+"]");
            if(code == ControlDataConstants.CODE_NEG)
            {
                receiveNEG(session, cdf);   
            }
            else if(code == ControlDataConstants.CODE_NEGR){
                log.debug(" NEG_R Received ");
                byte[] args = cdf.getArg().getValue();
                
                if(args[0] == ControlDataConstants.NEG_R_NOERROR){
                    log.debug(" NEG_R_NOERROR ");
                }else if(args[0] == ControlDataConstants.NEG_R_UNSUPPORTED_VERSION){
                    log.debug(" NEG_R_UNSUPPORTED_VERSION ");
                    CloseFuture future = session.closeNow();
                }else if(args[0] == ControlDataConstants.NEG_R_UNKNOWN_NAMESPACE){
                    log.debug(" NEG_R_UNKNOWN_NAMESPACE ");
                    CloseFuture future = session.closeNow();
                }else if(args[0] == ControlDataConstants.NEG_R_INVALID_SIZE){
                    log.debug(" NEG_R_INVALID_SIZE ");
                    CloseFuture future = session.closeNow();
                }else if(args[0] == ControlDataConstants.NEG_R_INVALID_COUNT){
                    log.debug(" NEG_R_INVALID_COUNT ");
                    CloseFuture future = session.closeNow();
                }            
            }
            else if(code == ControlDataConstants.CODE_ACK)
            {
                int sequence = FrameUtil.getAckSequence(ack);
                log.debug(" ACK sequence #" + sequence);
                synchronized(ackMonitor)
                {
                    ack = cdf;
                    ackMonitor.notify();
                }
            }
            else if(code == ControlDataConstants.CODE_NAK)
            {
                int seqs[] = FrameUtil.getNakSequence(cdf);
                ArrayList framelist = (ArrayList)
                    session.getAttribute("sendframes");
                for(int i = 0 ; i < seqs.length ; i++)
                {
                    byte[] mbx = (byte[])framelist.get(seqs[i]);
                    IoBuffer buffer = IoBuffer.allocate(mbx.length);
                    buffer.put(mbx,0,mbx.length);
                    buffer.flip();
                    session.write(buffer);
                    FrameUtil.waitSendFrameInterval();
                }
                ControlDataFrame wck =
                    (ControlDataFrame)session.getAttribute("wck");
                if(wck != null) {
                	session.write(wck);
                }
            }
        }
        else if (message instanceof ServiceDataFrame) {
            ServiceDataFrame sdf = (ServiceDataFrame)message;
            // 모뎀이나 미터 시리얼번호 응답이 오면 bypassService를 실행한다.
            String ns = (String)session.getAttribute("nameSpace");
            ServiceData sd = ServiceData.decode(ns, sdf, session.getRemoteAddress().toString());
            
            if (sd instanceof CommandData) {
            	
            	CommandData commandData = (CommandData)sd;
            	SMIValue[] smiValues = commandData.getSMIValue();

            	if(smiValues != null && smiValues.length >=2){            		
            		if(smiValues[0].getOid().getValue().equals("101.2.2")){
            			String modemId = smiValues[0].getVariable().toString();
            			log.info("CacheSession EUI="+modemId);
            			if(cache.getActiveSession(modemId) == null){
                			session.setAttribute("client", modemId);
                			cache.cacheSession(modemId, session);
                			log.info("CacheSession Count:"+cache.getTotalSessionCount());
                			log.info("CacheSession : "+modemId+"=["+cache.getActiveSession(modemId).getRemoteAddress());
            			}
            		}
            	}
                //try {
                //    action = (CommandAction)Class.forName("com.aimir.fep.bypass.actions.CommandAction_"+ns).newInstance();
                //    action.execute(session, (CommandData)sd);
                //}
                //catch (Exception e) {
                //    log.error(e, e);
                //}
            }
            if (sd instanceof MDData) {
                MDData data = (MDData)sd;
                try {
                    ProcessorHandler handler = DataUtil.getBean(ProcessorHandler.class);
                    handler.putServiceData(ProcessorHandler.SERVICE_MEASUREMENTDATA, data);
                }
                catch (Exception e) {
                    log.error(e,e);
                }
            }
            if (sd instanceof EventData_1_2) {
            	EventData_1_2 data = (EventData_1_2)sd;
                try {
                    ProcessorHandler handler = DataUtil.getBean(ProcessorHandler.class);
                    handler.putServiceData(ProcessorHandler.SERVICE_EVENT_1_2, data);
                }
                catch (Exception e) {
                    log.error(e,e);
                }
            }
        }
        else if (message instanceof byte[]) {
            byte[] frame = (byte[])message;
            log.debug("BypassFrame[" + Hex.decode(frame) + "]");
            if (action != null)
                action.executeBypass(frame, session);
        }
    }
    
    /*
     * NEG 프레임을 수신하면 프레임 사이즈와 윈도우 개수를 저장하여 활용한다.
     * 바이패스 핸들러는 명령을 수행하기 위한 것으로 모뎀이 접속하면 어떤 명령을 수행하려 했는지
     * 비동기 내역에서 찾아야 하는데 이때 접속한 모뎀의 시리얼 번호를 먼저 가져와야 한다.
     */
    private void receiveNEG(IoSession session, ControlDataFrame cdf) 
    throws Exception
    {
        byte[] args = cdf.getArg().getValue();
        if(args != null){
            log.info("NEG[" + Hex.decode(args) + "]");
        }

        // enq 버전이 1.2 인 경우 frame size와 window size를 session에 저장한다.
        if (args[0] == 0x01 && args[1] == 0x02) {
            int frameMaxLen = DataUtil.getIntTo2Byte(new byte[] {args[3], args[2]});
            int frameWinSize = DataUtil.getIntToByte(args[4]);
            String nameSpace = new String(DataUtil.select(args, 5, 2));
            session.setAttribute("frameMaxLen", frameMaxLen);
            session.setAttribute("frameWinSize", frameWinSize);
            session.setAttribute("nameSpace", nameSpace);
            log.info("NEG V1.2 Frame Size[" + frameMaxLen + "] Window Size[" + frameWinSize + "] NameSpace["+nameSpace+"]");
            
            // NEG에 대한 응답을 보내고
            session.write(FrameUtil.getNEGR());
            // 어떤 장비인지 식별하기 위한 명령을 보낸다.
            cmdIdentifyDevice(session);
        }        
    }
    
    /*
     * 모뎀과 미터 시리얼번호를 가져오기 위한 명령
     */
    private void cmdIdentifyDevice(IoSession session) 
    throws Exception
    {
        sendCommand(session, "cmdIdentifyDevice", null);        
    }
    
    private CommandData command(String ns, String cmdName, List<?> params){
        MIBUtil mu = MIBUtil.getInstance(ns);
        CommandData command = new CommandData();
        command.setCmd(mu.getMIBNodeByName(cmdName).getOid());
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                Object obj = params.get(i);
                if (obj instanceof SMIValue) {
                    command.append((SMIValue) obj);
                } else if(obj instanceof AsyncCommandParam){
                    AsyncCommandParam param = (AsyncCommandParam) obj;
                    SMIValue smiValue;
                    try {
                        smiValue = DataUtil.getSMIValueByOid(ns, param.getParamType(), param.getParamValue());
                        command.append(smiValue);
                    } catch (Exception e) {
                        log.error(e,e);
                    }

                }
            }
        }
        return command;
    }
    
    public void sendCommand(IoSession session, String cmdName, List<?> params)
            throws Exception
    {
        CommandData command = null;
        try {
            String ns = (String)session.getAttribute("nameSpace");
            command = command(ns, cmdName, params);
            command.setAttr(ServiceDataConstants.C_ATTR_REQUEST);
            command.setTid(FrameUtil.getCommandTid());
            
            ServiceDataFrame frame = new ServiceDataFrame();
            long mcuId = 0L;
            frame.setMcuId(new UINT(mcuId));
            frame.setSvc(GeneralDataConstants.SVC_C);
            frame.setAttr((byte)(GeneralDataConstants.ATTR_START | GeneralDataConstants.ATTR_END));
            frame.setSvcBody(command.encode());
            session.write(frame);					
        }
        catch(Exception ex)
        {
            log.error(ex, ex);
            if(!command.getCmd().toString().equals("198.3.0"))
            {
                log.error("sendCommand failed : command["+command+"]",ex);
            } else {
                log.error("sendCommand failed : command["+command.getCmd()
                        +"]",ex);
            }
            throw ex;
        }
    }
    
    /**
     * wait util received command response data and return Response
     * 
     * @param session <code>IoSession</code> session
     * @param tid <code>int</code> command request id
     * @throws FMPException
     */
    public ServiceData getResponse(IoSession session,int tid)
        throws FMPException
    {
        String key = ""+tid;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        int waitResponseCnt = 0;
        while(session.isConnected())
        { 
            // log.debug(tid);
            if(response.containsKey(""+tid)) 
            { 
                Object obj = (ServiceData)response.get(key); 
                response.remove(key); 
                if(obj == null) 
                    continue; 
                return (ServiceData)obj; 
            } 
            else
            {
                waitResponse();
                ctime = System.currentTimeMillis();

                /** modified by D.J Park in 2006.04.10
                 * modify Measurement of Reponse time out  
                 * same monitor notified when other request 
                 * can be wait time less than response time
                waitResponseCnt++;
                if((waitResponseCnt / 2) > responseTimeout)
                {
                    throw new FMPResponseTimeoutException(" tid : "
                            + tid +" Response Timeout["
                            +responseTimeout +"]");
                }
                */
                if(((ctime - stime)/1000) > responseTimeout)
                {
                    log.debug("getResponse:: SESSION IDLE COUNT["
                            +session.getIdleCount(IdleStatus.BOTH_IDLE) 
                            +"]");
                    if(session.getIdleCount(IdleStatus.BOTH_IDLE) >= retry)
                    {
                        response.remove(key); 
                        throw new FMPResponseTimeoutException(" tid : " 
                                + tid +" Response Timeout[" 
                                +responseTimeout +"]");
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * wait util received command response data
     */
    public void waitResponse()
    {
        synchronized(resMonitor)
        { 
            try { resMonitor.wait(500); 
            } catch(InterruptedException ie) {ie.printStackTrace();}
        }
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        log.info("### Bye Bye ~ Client session closed from " + session.getRemoteAddress().toString());
        session.removeAttribute(session.getRemoteAddress());
        session.closeNow();
    }

	@Override
	public void event(IoSession session, FilterEvent event) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
