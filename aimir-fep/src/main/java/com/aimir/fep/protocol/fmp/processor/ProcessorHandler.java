package com.aimir.fep.protocol.fmp.processor;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.kafka.outbound.KafkaProducerMessageHandler;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.Interface;
import com.aimir.fep.logger.snowflake.SnowflakeGeneration;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.log.MDLogger;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.CommLog;
import com.aimir.util.DateTimeUtil;

/**
 * Processor Handler
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
@Component
public class ProcessorHandler
{
    private static Log log = LogFactory.getLog(ProcessorHandler.class);
    public static String SERVICE_DATA = "ServiceData";
    public static String SERVICE_COMMAND = FMPProperty.getProperty("ServiceData.CommandData");
    public static String SERVICE_ALARM = FMPProperty.getProperty("ServiceData.AlarmData");
    public static String SERVICE_EVENT = FMPProperty.getProperty("ServiceData.EventData");
    public static String SERVICE_EVENT_1_2 = FMPProperty.getProperty("ServiceData.EventData_1_2");
    public static String SERVICE_FILE = FMPProperty.getProperty("ServiceData.FileData");
    public static String SERVICE_MEASUREMENTDATA = FMPProperty.getProperty("ServiceData.MDData");
    public static String SERVICE_NEWMEASUREMENTDATA = FMPProperty.getProperty("ServiceData.NDData");
    public static String SERVICE_DATAFILEDATA = FMPProperty.getProperty("ServiceData.DFData");
    public static String SERVICE_PLC = FMPProperty.getProperty("ServiceData.PLCData");
    public static String LOG_COMMLOG = FMPProperty.getProperty("LogData.CommLogData");
    public static String SERVICE_AMU_EVENT = FMPProperty.getProperty("ServiceData.AMUEventData");
    public static String SERVICE_AMU_METERING = FMPProperty.getProperty("ServiceData.AMUMDData");
    public static String SERVICE_REAL_MEASUREMENTDATA = FMPProperty.getProperty("ServiceData.RMDData");
    
    public static String SERVICE_EMNV_METERING = FMPProperty.getProperty("ServiceData.EMNVMDData");
    public static String SERVICE_EMNV_COMMAND = FMPProperty.getProperty("ServiceData.EMNVCommandData");

    @Autowired
    private JmsTemplate jmsTemplate;
    
    @Autowired
    private KafkaProducer producer;

    private KafkaTemplate<Integer, com.aimir.fep.util.Message> toKafka = null;
    
    public void putServiceData(String serviceType, final Serializable data) throws Exception
    {
        // 2017.03.24 SP-629
        boolean enableMDFilelog = Boolean.parseBoolean(FMPProperty.getProperty("protocol.md.filelog.enable"));
        
        if (data instanceof com.aimir.fep.util.Message) {
            if (toKafka == null) {
                KafkaProducerMessageHandler<Integer, com.aimir.fep.util.Message> handler = 
                        (KafkaProducerMessageHandler<Integer, com.aimir.fep.util.Message>)producer.handler();
                toKafka = 
                        (KafkaTemplate<Integer, com.aimir.fep.util.Message>)handler.getKafkaTemplate();
            }
            
            com.aimir.fep.util.Message msg = (com.aimir.fep.util.Message)data;
            log.debug("Put Data to KafkaQueue ==> [" + serviceType + "][" + msg.toString() + "]");
            
            if (serviceType == ProcessorHandler.SERVICE_MEASUREMENTDATA && enableMDFilelog) { //ServiceData.MDData
                MDData sd = new MDData();
                sd.setCnt(new WORD(1));
                // byte[] b = new byte[msg.getData().length - 2];
                // System.arraycopy(msg.getData(), 2, b, 0, b.length);
                sd.setMdData(msg.getData());
                sd.setNS(msg.getNameSpace());
                sd.setIpAddr(msg.getSenderIp());
                sd.setMcuId(msg.getSenderId());
                
                MDLogger mdlog = new MDLogger();
                String filename = mdlog.writeObject(sd);
                msg.setFilename(filename);
                log.debug("Put MDData Filename[" + filename + "] to "+ serviceType);
            }
            
            msg.setSequenceLog(SnowflakeGeneration.getId());
            toKafka.send(serviceType, msg);
            /*
            CommLog commLog = makeCommLog((com.aimir.fep.util.Message)data, serviceType);
            CommLogger commLogger = DataUtil.getBean(CommLogger.class);
            commLogger.sendLog(commLog);
            */
        }
        else {
            log.debug("Try to Put Data to ActiveMQueue ==> [" + serviceType + "][" + data.getClass().getName() + "]");
            
            if (serviceType.equals(ProcessorHandler.SERVICE_MEASUREMENTDATA) && enableMDFilelog) {
                MDLogger mdlog = new MDLogger();
                final String filename = mdlog.writeObject(data);
                log.debug("Wrote MDData to LogFile[" + filename + "]");
                jmsTemplate.send(serviceType, new MessageCreator() {
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(filename);
                    }
                });
                log.debug("Put MDData Filename[" + filename + "] to "+ serviceType);
            }
            else {
                jmsTemplate.send(serviceType, new MessageCreator() {
                    public Message createMessage(Session session) throws JMSException {
                        return session.createObjectMessage(data);
                    }
                });
                log.debug("Put Data to "+ serviceType);
            }
        }
    }
    
    private CommLog makeCommLog(com.aimir.fep.util.Message msg, String serviceType) {
        CommLog commLog = new CommLog();
        
        // commLog.setNameSpace(msg.getNameSpace());
        // commLog.setData(frame.encode());
        // commLog.setDataType("ServiceData");
        commLog.setSenderIp(msg.getSenderIp());
        commLog.setSenderId(msg.getSenderId());
        commLog.setReceiverId(msg.getReceiverId());
        commLog.setReceiverTypeCode(CommonConstants.getSenderReceiver("1")); // FEP
        commLog.setSendBytes((int)msg.getSendBytes()); //ENQ+ACK
        commLog.setRcvBytes((int)msg.getRcvBytes());//included EOT that received
        commLog.setStartDateTime(msg.getStartDateTime());
        commLog.setStartDate(msg.getStartDateTime().substring(0,8));
        commLog.setStartTime(msg.getStartDateTime().substring(8,14));
        commLog.setEndTime(msg.getEndDateTime());
        commLog.setInterfaceCode(CommonConstants.getInterface(Interface.IF4.name()));
        commLog.setOperationCode(serviceType);
        // Communication Success
        commLog.setCommResult(1);
        log.debug("startTime["+commLog.getStartTime()+"] endTime["+commLog.getEndTime()+"]");
        try {
            long startLongTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(commLog.getStartDateTime()).getTime();
            long endLongTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(commLog.getEndTime()).getTime();
            
            if(endLongTime - startLongTime > 0) {
                commLog.setTotalCommTime((int)(endLongTime - startLongTime));
            }
            else {
                commLog.setTotalCommTime(0);
            }
        }
        catch (Exception e) {
            log.warn(e);
        }
        return commLog;
    }
}
