package com.aimir.fep.protocol.smcp;

import java.io.Serializable;
import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.FrameUtil;
import com.aimir.model.device.CommLog;
import com.aimir.model.system.Code;
import com.aimir.util.TimeUtil;

/**
 * {@link SMCPHandler} implementation of SCMP Protocol
 *
 * @author goodjob (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2014-09-30 10:00:00 +0900 $,
 */
@Component
public class SMCPHandler extends IoHandlerAdapter
{
    private static Log log = LogFactory.getLog(SMCPHandler.class);

    private Object ackMonitor = new Object();

    private int idleTime = Integer.parseInt(FMPProperty.getProperty(
                "protocol.idle.time","5"));
    private int retry = Integer.parseInt(FMPProperty.getProperty(
                "protocol.retry","3"));
    private int ackTimeout = Integer.parseInt(
            FMPProperty.getProperty("protocol.ack.timeout","3"));
    private CommLog commLog = new CommLog();
    private String startTime = null;
    private String endTime = null;
    private long   startLongTime = 0;
    private long   endLongTime = 0;
    private Integer activatorType = new Integer(
            FMPProperty.getProperty("protocol.system.MCU","2"));
    private Integer targetType = new Integer(
            FMPProperty.getProperty("protocol.system.FEP","1"));

    private Integer protocolType = new Integer(FMPProperty.getProperty("protocol.type.default","3"));
    
    @Autowired
    private ProcessorHandler processorHandler;
    
    private void putServiceData(String serviceType, Serializable data) {
        try {
            processorHandler.putServiceData(serviceType, data);
        }
        catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void exceptionCaught(IoSession session,
            Throwable cause )
    {
        // Close connection when unexpected exception is caught.
        log.error(cause, cause);
        log.error(cause+"  from "+session.getRemoteAddress());
        session.closeNow();
    }
    

 
    /**
     * inherited method from ProtocolHandlerAdapter
     * handling GeneralDataFrame
     *
     * @param session <code>ProtocolSession</code> session
     * @param message <code>Object</code> decoded GeneralDataFrame
     */
    public void messageReceived(IoSession session, Object message )
    {
        try
        {
            log.info("###### Message [ " + message.getClass().getName() + "]");

        }catch(Exception ex)
        {
            log.error(getProtoName()
                    + " FMPProtocolHandler::messageReceived "
                    + " failed" ,ex);
        }
    }

    /*
     * Save Request Frame
     * @see org.apache.mina.core.service.IoHandlerAdapter#messageSent(org.apache.mina.core.session.IoSession, java.lang.Object)
     */
    public void messageSent(IoSession session, Object message) throws Exception {
        log.debug("[Start] MessageSent");
        try
        {

        }catch(Exception ex)
        {
            log.error(getProtoName()
                    + " FMPProtocolHandler::MessageSent "
                    + " failed" ,ex);
        }
        log.debug("[End] MessageSent");
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionIdle(IoSession session, IdleStatus status)
    throws Exception
    {
        log.debug(getProtoName()+" IDLE COUNT : "
                + session.getIdleCount(IdleStatus.READER_IDLE));
        if(session.getIdleCount(IdleStatus.READER_IDLE) >= retry)
        {
            session.write(FrameUtil.getEOT());
            FrameUtil.waitAfterSendFrame();
            session.closeNow();
        }
    }

    /**
     * inherited method from ProtocolHandlerAdapter
     */
    public void sessionOpened(IoSession session)
    {
        log.info("sessionOpened : "
                + session.getRemoteAddress());
        startLongTime = System.currentTimeMillis();
        try {
            startTime = TimeUtil.getCurrentTime();
        }
        catch (ParseException e) {
            log.warn(e);
        }

        session.getConfig().setIdleTime(IdleStatus.READER_IDLE,
                idleTime);
    }

    public void sessionClosed(IoSession session)
    {
        synchronized(ackMonitor)
        {
            ackMonitor.notify();
        }
        session.removeAttribute("");
        session.removeAttribute("sendframes");
        log.info(getProtoName()+" Session Closed : "
                + session.getRemoteAddress());
    }

    private void saveCommLog(CommLog obj)
    {
        String serviceType = ProcessorHandler.LOG_COMMLOG;
        try {
            putServiceData(serviceType, obj);
        }
        catch (Exception e) {
            log.error(e);
        }
    }

    /**
     * set Protocol Type(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     * @param protocolType <code>Integer</code> Protocol Type
     */
    public void setProtocolType(Integer protocolType)
    {
        this.protocolType = protocolType;
    }

    /**
     * get Protocol Type(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     * @return protocolType <code>Integer</code> Protocol Type
     */
    public Integer getProtocolType()
    {
        return this.protocolType;
    }

    /**
     * get Protocol Type String(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     * @return protocolType <code>String</code> Protocol Type
     */
    public String getProtoName()
    {
        int proto = this.protocolType.intValue();
        Code code = CommonConstants.getProtocol(proto+"");
        return "[" + code.getName() + "]";
    }
}
