package com.aimir.fep.protocol.nip.server;

import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.logger.snowflake.SnowflakeGeneration;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.protocol.nip.client.actions.NICommandActionHandlerAdaptor;
import com.aimir.fep.protocol.nip.client.actions.BypassCommandAction;
import com.aimir.fep.protocol.nip.client.actions.NICommandAction;
import com.aimir.fep.protocol.nip.client.actions.NI_MBB_Action_SP;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.protocol.nip.command.MBBModuleInformation;
import com.aimir.fep.protocol.nip.common.GeneralDataFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.fep.protocol.nip.frame.NetworkStatus;
import com.aimir.fep.protocol.nip.frame.NetworkStatusEthernet;
import com.aimir.fep.protocol.nip.frame.NetworkStatusMBB;
import com.aimir.fep.protocol.nip.frame.NetworkStatusSub1GhzForSORIA;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NetworkType;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.OndemandType;
import com.aimir.fep.protocol.nip.frame.NIFrameConstants;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameControl_Ack;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.FrameControl_Pending;
import com.aimir.fep.protocol.nip.frame.payload.AlarmEvent;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.protocol.nip.frame.payload.Firmware;
import com.aimir.fep.protocol.nip.frame.payload.MeterEvent;
import com.aimir.fep.protocol.nip.frame.payload.MeterEvent.MeterEventFrame;
import com.aimir.fep.protocol.nip.frame.payload.Command.Attribute;
import com.aimir.fep.protocol.nip.frame.payload.Command.Attribute.Data;
import com.aimir.fep.protocol.nip.frame.payload.MeteringData;
import com.aimir.fep.protocol.security.OacServerApi;
import com.aimir.fep.trap.actions.SP.EV_SP_220_2_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_240_2_0_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Message;
import com.aimir.fep.util.threshold.CheckThreshold;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.SubGiga;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.IPUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.Condition.Restriction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.FilterEvent;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * {@link NiProtocolHandler}).
 *
 * @author DJ Kim
 * @version $Rev: 1 $, $Date: 2016-05-21 15:59:15 +0900 $,
 */
@Component
public class NiProtocolHandler extends NICommandActionHandlerAdaptor implements IoHandler //extends IoHandlerAdapter
{
    private static Log log = LogFactory.getLog(NiProtocolHandler.class);
    private boolean isCmdAdapter = false;
	private int readidleTime = Integer.parseInt(FMPProperty.getProperty("protocol.ni.idle.readtime", "30"));
	private int writeidleTime = Integer.parseInt(FMPProperty.getProperty("protocol.ni.idle.writetime", "120"));
	
    private int responseTimeout = Integer.parseInt(FMPProperty.getProperty("protocol.ni.response.timeout", "180"));
    private int writeTimeout = Integer.parseInt(FMPProperty.getProperty("protocol.ni.write.timeout", "180")) * 1000;
    private Object resMonitor = new Object();
//    private Hashtable<Long, IoBuffer> response = new Hashtable<Long, IoBuffer>();
    private Hashtable<Long, Object> response = new Hashtable<Long, Object>();
    private ProcessorHandler processorHandler;
    
    private long sequenceLog = 0L;
    
    public NiProtocolHandler() throws Exception { }
    
    public NiProtocolHandler(boolean isCmdAdapter, String handlerName) throws Exception {
    	setHandlerName(handlerName);
    	this.isCmdAdapter = isCmdAdapter;
    	this.sequenceLog = SnowflakeGeneration.getId();
    	
    	log.debug("############### New NiProtocolHandler : HandlerName=" + getHandlerName() + " ##############");
    }
    
	private void putServiceData(IoSession session, String serviceType, Serializable data) {
        try {
            if (processorHandler == null) processorHandler = DataUtil.getBean(ProcessorHandler.class);
            
            if (!Boolean.parseBoolean(FMPProperty.getProperty("kafka.enable")))
                processorHandler.putServiceData(serviceType, data);
            else
                processorHandler.putServiceData(serviceType, makeCommLog(session, (MDData)data));
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
	
	private Message makeCommLog(IoSession session, MDData data) throws Exception {
        String nameSpace = (String)session.getAttribute("nameSpace");
        String ipaddr = session.getRemoteAddress().toString();
        ipaddr = ipaddr.substring(ipaddr.indexOf("/")+1, ipaddr.lastIndexOf(":"));
        
        //통신 로그 저장 로직
        Message commLog = new Message();
        commLog.setNameSpace(nameSpace == null? "":nameSpace);
        commLog.setData(data.getMdData());
        commLog.setDataType(ProcessorHandler.SERVICE_MEASUREMENTDATA);
        commLog.setSenderIp(ipaddr);
        commLog.setSenderId(data.getMcuId().toString());
        commLog.setReceiverId(DataUtil.getFepIdString());
        commLog.setSendBytes(session.getWrittenBytes());
        commLog.setRcvBytes(session.getReadBytes());
        commLog.setStartDateTime(DateTimeUtil.getDateString(session.getCreationTime()));
        commLog.setEndDateTime(DateTimeUtil.getDateString(session.getLastWriteTime()));
        log.debug("startTime["+commLog.getStartDateTime()+"] endTime["+commLog.getEndDateTime()+"]");
        log.debug("startLongTime["+session.getCreationTime()+"] endLongTime["+session.getLastWriteTime()+"]");
        if(session.getLastWriteTime() - session.getCreationTime() > 0) {
            commLog.setTotalCommTime((int)(session.getLastWriteTime() - session.getCreationTime()));
        }
        else {
            commLog.setTotalCommTime(0);
        }
        log.debug(data.getNetworkType() +" "+ commLog.toString());
        if (data.getNetworkType() == NetworkType.MBB) {
            commLog.setProtocolType(Protocol.GPRS);
        }
        else {
            commLog.setProtocolType(Protocol.IP);
        }
        
        return commLog;
    }
    
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
    	String sessionRemoteAddress = null;
        try {
        	sessionRemoteAddress = (session.getRemoteAddress() != null ? session.getRemoteAddress().toString() : "Unknown. Already disconnected"); 
            log.debug("HandlerName=[" + getHandlerName() + "][NISV][EXCEPTION]"+session.getId() + ", RemoteAddress = " + sessionRemoteAddress);
            log.debug("HandlerName=[" + getHandlerName() + "]" + (cause != null ? cause.getMessage() : "Throwable object is null."));
            log.debug("HandlerName=[" + getHandlerName() + "]" + (cause.getMessage() != null ? cause.getMessage() : cause.toString()));
            log.debug("HandlerName=[" + getHandlerName() + "]Session information => " + session.toString());            
            
            if (cause != null && cause.getMessage() != null && cause.getMessage().contains("SSL handshake failed")) {
                // Security Alarm
                /*
                 * Seurity alarm has to be done with threshold */
                try {
                    EventUtil.sendEvent("Security Alarm"
                    		, TargetClass.Modem
                    		, (sessionRemoteAddress.equals("Unknown. Already disconnected") ? sessionRemoteAddress : IPUtil.formatTrim(session.getRemoteAddress().toString()))
                    		, new String[][] {{"message", "Uncertificated Access"}});
                }
                catch (Exception e) {
                    log.error(e, e);
                }
                // INSERT START SP-193
                CheckThreshold.updateCount(sessionRemoteAddress, ThresholdName.AUTHENTICATION_ERROR);
                // INSERT END SP-193  
            }
            else if (cause.getMessage().contains("Connection reset by peer")) {
                log.warn("HandlerName=[" + getHandlerName() + "]" + cause.getMessage());
            }
            else {
                // INSERT START SP-193
                CheckThreshold.updateCount(sessionRemoteAddress, ThresholdName.INVALID_PACKET);
                // INSERT END SP-193        
            }
        }
        finally {
        	log.debug("HandlerName=[" + getHandlerName() + "][code trace] sessionId=" + session.getId() + ", exceptionCaught call1=" + sessionRemoteAddress);
			//SP-890
            Thread.sleep(Integer.parseInt(FMPProperty.getProperty("protocol.ni.closewait1.timeout", "500")));
        	if(session != null && session.isConnected()) {
                session.closeNow();
        	}

            //log.debug("HandlerName=[" + getHandlerName() + "][code trace] sessionId=" + session.getId() + ", exceptionCaught call2=" + session.getRemoteAddress());
        }
    }
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception
    {
        log.debug((isCmdAdapter == true ? "[FEP-COMMAND-SERVER] ": "") + "############## HandlerName=[" + getHandlerName() + "] NI MessageReceived : " + message.toString() + ", RemoteAddress=" + session.getRemoteAddress());
        byte bx[] = null;

        if (message instanceof IoBuffer) {
            log.debug("HandlerName=[" + getHandlerName() + "][NISV][RECEIVE]"+session.getId());
            IoBuffer buffer = (IoBuffer) message;
            bx = buffer.array();
            log.debug("HandlerName=[" + getHandlerName() + "][messageReceived]"+Hex.decode(((IoBuffer)message).array()));    // 51F804005300000B1211111111110C52000000000009820001C10100020000A1DE
            GeneralFrame gframe = new GeneralFrame();
            GeneralDataFrame data = new GeneralDataFrame();
             
            Map<Integer, byte[]> multiFrame = (Map<Integer, byte[]>)session.getAttribute("multiFrame");
            multiFrame = gframe.decode(multiFrame, bx);
            
           // log.debug("[messageReceived]"+Hex.decode(((IoBuffer)message).array()));
            
            byte[] ackData = null;
            if (gframe.fcAck == FrameControl_Ack.Ack) {
                ackData = gframe.ack(null);
                //requesting
                WriteFuture future = session.write(ackData);
                log.debug("HandlerName=[" + getHandlerName() + "]send ACK Data:"+ Hex.decode(ackData));
                if(!future.awaitUninterruptibly(Long.parseLong(
                        FMPProperty.getProperty(
                                "protocol.waittime.after.send.frame","1000")))) {

                }
            }
            else if (gframe.fcAck == FrameControl_Ack.CRC) {
                byte[] crc = (byte[])session.getAttribute("crc");
                log.debug("CRC[" + Hex.decode(crc) + "]");
                ackData = gframe.ack(crc);
                //requesting
                WriteFuture future = session.write(ackData);
                log.debug("HandlerName=[" + getHandlerName() + "]send ACK Data:"+ Hex.decode(ackData));
                if(!future.awaitUninterruptibly(Long.parseLong(
                        FMPProperty.getProperty(
                                "protocol.waittime.after.send.frame","1000")))) {

                }
                log.debug("HandlerName=[" + getHandlerName() + "]send CRC ACK Data:"+ Hex.decode(ackData));
            }
            
            // if frame is multi, continue to get last frame
            if (gframe.fcPending == FrameControl_Pending.MultiFrame) {
                session.setAttribute("multiFrame", multiFrame);
                log.debug("MULTI_SIZE[" + multiFrame.size() + "]");
                return;
            }
            else {
                multiFrame = null;
            }
            
            String nodeId = Hex.decode(gframe.getSrcAddress());
            String ipAddr = IPUtil.formatTrim(session.getRemoteAddress().toString());
             
            
            //request Ack On
            NICommandAction commandAction = null;
            MultiSession mSession = null;
            switch (gframe.foType) {
            
                case Ack:
                	commandAction = getNICommandAction(session);
                    if(commandAction != null && commandAction.isUseAck() == true){
                        MultiSession bpSession = commandAction.getMultiSession(session);
                        commandAction.executeAck(bpSession, gframe);
                    }else{
                        byte[] sendData = data.make(gframe,null);
                        //requesting
                        session.write(sendData);
                        log.debug("HandlerName=[" + getHandlerName() + "][ACK]sendData : " + sendData);                        
                    }
                    break;
                case Command:
                    // packet is modem information response for command
                    log.debug("HandlerName=[" + getHandlerName() + "][NIPT]:isCmdAdapter[" + isCmdAdapter + "]");
                    if (isCmdAdapter
                            && gframe._commandType == CommandType.Trap
                            && gframe._commandFlow == CommandFlow.Trap) {

                    	
                    	log.debug("##################### Async Command by SMS Execute Start~!! ##############################");
                        log.debug("HandlerName=[" + getHandlerName() + "][NIPT]:isCmdAdapter");
                        commandAction = getNICommandAction(session);
                        
                        NI_MBB_Action_SP niMbbAction = new NI_MBB_Action_SP();
                        niMbbAction.executeAction(session, gframe);
                        log.debug("##################### Async Command by SMS Execute End~!! ##############################");
                    }
                    else if (!isCmdAdapter 
                            && gframe._commandType == CommandType.Trap 
                            && gframe._commandFlow == CommandFlow.Trap) {
                        // 모뎀 미터 정보 생성
                        Command command = (Command)gframe.getPayload();
                        
                        log.debug("HandlerName=[" + getHandlerName() + "][NISV][MODEMINFO] RemoteAddress=" + session.getRemoteAddress()+"/"+session.getLocalAddress());
                        doTrap(nodeId, ipAddr, command);
                        
                        log.debug("##################### Async Command by Upload Channel Execute Start~!! ##############################");
                        //FIRMWARE Update
                        //boolean mbbOTAOnUploadChannel = Boolean.parseBoolean(FMPProperty.getProperty("protocol.ni.allow.mbbotaonuploadchannel", "false"));
                        //if(mbbOTAOnUploadChannel){
                        
                       	session.setAttribute("asyncCommandByUploadChannel", "true"); //SP-892
                            // No longer use mbbOTAOnUploadChannel property because Firmware Management Gadget always use mbbOTAOnUploadChannel option.
                        	NI_MBB_Action_SP niMbbAction = new NI_MBB_Action_SP();
                            niMbbAction.executeAction(session, gframe);
                        //}
                        log.debug("##################### Async Command by Upload Channel Execute Stop~!! ##############################");                            
                    }
                    else {
                        // There is only one command request from modem. MeterSharedKey
                        if (gframe._commandFlow == CommandFlow.Request) {
                            Command command = (Command)gframe.getPayload();
                            doCommand(session, gframe, command);
                            
							//SP-879
                            //Thread.sleep(Integer.parseInt(FMPProperty.getProperty("protocol.ni.closewait.timeout", "2000")));
                            log.debug("## HandlerName=[" + getHandlerName() + "]Command closeNow call start");
                        	if(session != null && session.isConnected()) {
                                session.closeNow();
                        	}
                            log.debug("## HandlerName=[" + getHandlerName() + "]Command closeNow call end");
                        }
                    }
                    
                    //response.put(session.getId(), (IoBuffer) message);
                    response.put(session.getId(), gframe);
                    
                    break;
                case AlarmEvent:
                    log.debug("NI AlarmEvent Received ~!! []" + gframe.toString());
                    log.debug("HandlerName=[" + getHandlerName() + "][NISV][ALARMEVENT] RemoteAddress=" + session.getRemoteAddress()+"/"+session.getLocalAddress());
                    AlarmEvent alarmEvt = (AlarmEvent)gframe.getPayload();
                    makeModemEvent(nodeId, alarmEvt);
                    
					//SP-890
                    Thread.sleep(Integer.parseInt(FMPProperty.getProperty("protocol.ni.closewait1.timeout", "500")));
                	if(session != null && session.isConnected()) {
                        session.closeNow();
                	}
                    break;
                case MeterEvent:
                    log.debug("NI MeterEvent Received ~!! []" + gframe.toString());
                    log.debug("HandlerName=[" + getHandlerName() + "][NISV][METEREVENT] RemoteAddress=" + session.getRemoteAddress()+"/"+session.getLocalAddress());
                    MeterEvent meterEvt = (MeterEvent)gframe.getPayload();
                    makeMeterEvent(nodeId, meterEvt);
                    
					//SP-890
                    Thread.sleep(Integer.parseInt(FMPProperty.getProperty("protocol.ni.closewait1.timeout", "500")));
                	if(session != null && session.isConnected()) {
                        session.closeNow();
                	}
                    break;
                case Metering :
                    // get a metering and then in queue
                    /*Object meteringObj = session.getAttribute("metering");
                    ByteArrayOutputStream meteringOut = null;
                    if (meteringObj == null)
                        meteringOut = new ByteArrayOutputStream();
                    else
                        meteringOut = (ByteArrayOutputStream)meteringObj;
                    
                    // 무조건 바이트어레이로 만든다.
                    meteringOut.write(((MeteringData)gframe.getPayload()).getData());
                    session.setAttribute("metering", meteringOut);
                    
                    if (gframe.getFcPending() == FrameControl_Pending.LastFrame) {
                        ((MeteringData)gframe.getPayload()).decode(meteringOut.toByteArray());
                        meteringOut.close();
                        session.removeAttribute("metering");
                        
                        MDData mdData = ((MeteringData)gframe.getPayload()).getMDData();
                        mdData.setNetworkType(gframe.getNetworkType());
                        mdData.setIpAddr(session.getRemoteAddress().toString());
                        mdData.setMcuId(nodeId);
                        mdData.setNetworkType(gframe.getNetworkType());
                        putServiceData(session, ProcessorHandler.SERVICE_MEASUREMENTDATA, mdData);
                    }
                    else {
                        log.debug("METERING_DATA_PENDING");
                    }*/
                    log.debug("HandlerName=[" + getHandlerName() + "][NISV][METERING] RemoteAddress=" + session.getRemoteAddress()+"/"+session.getLocalAddress());
                	if(((MeteringData)gframe.getPayload()).getMDData() != null) {
                        MDData mdData = ((MeteringData)gframe.getPayload()).getMDData();
                        mdData.setNetworkType(gframe.getNetworkType());
                        mdData.setIpAddr(session.getRemoteAddress().toString());
                        mdData.setMcuId(nodeId);
                        mdData.setNetworkType(gframe.getNetworkType());
                        putServiceData(session, ProcessorHandler.SERVICE_MEASUREMENTDATA, mdData);
                	}else {
                		log.info("["+nodeId+"]MDData lengh is 0");
                	}

                    // SP-918
                    log.debug("##################### Async Command by Metering Channel Execute Start~!! ##############################");
                    List<String> commandFilter = new ArrayList<String>();
                    String filterProp = FMPProperty.getProperty("async.command.filter","cmdGetROMRead");// 실행하고자 하는 Command의 경우 Filter 로 지정.  
                    StringTokenizer st = new StringTokenizer(filterProp, ",");
                    while (st.hasMoreTokens()) {
                    	commandFilter.add(st.nextToken().trim());
                    }
                   	session.setAttribute("asyncCommandByUploadChannel", "true"); //SP-892
                	NI_MBB_Action_SP niMbbAction = new NI_MBB_Action_SP();
                    niMbbAction.executeAction(session, gframe, commandFilter);                        
                    log.debug("##################### Async Command by Metering Channel Execute Stop~!! ##############################"); 
                    
					//SP-890
                    Thread.sleep(Integer.parseInt(FMPProperty.getProperty("protocol.ni.closewait1.timeout", "500")));
                	if(session != null && session.isConnected()) {
                        session.closeNow();
                	}
                    break;
                case Firmware :
                	commandAction = getNICommandAction(session);
                	mSession = commandAction.getMultiSession(session);
                    if(mSession != null){
                        try {
                            commandAction.executeTransaction(mSession, gframe);                            
                        } catch (Exception e) {
                            Firmware firmwareFrame = (Firmware) gframe.getPayload();
                            String command = firmwareFrame.get_upgradeCommand().name();
                            log.error("HandlerName=[" + getHandlerName() + "][" + commandAction.getClass().getSimpleName() + 
                                    "] Command Action Excute error [" + command + "][Session=" + session.getRemoteAddress() + "] - " + e.toString());
                            
            				// Mulit session close
                            log.debug("## HandlerName=[" + getHandlerName() + "]closeMultiSession 호출1");
                            closeMultiSession(session);
                            log.debug("## HandlerName=[" + getHandlerName() + "]closeMultiSession 호출2");
                        }
                    }else{
                    	log.error("HandlerName=[" + getHandlerName() + "]Can not found MultiSession.");
                    	throw new Exception("Can not found MultiSession.");
                    }

                    break;
                case Bypass :
                	BypassCommandAction bypassCommandAction = getBypassCommandAction(session);
                	mSession = bypassCommandAction.getMultiSession(session);                	

                	log.debug("### Bypass Frame. Multisession is " + mSession == null ? "Null~!" : "Not Null~!" + " ###");
                	
                    if(mSession != null){
                        try {
                        	bypassCommandAction.executeBypass(mSession, bx);  
                        } catch (Exception e) {
                            log.error("HandlerName=[" + getHandlerName() + "][" + bypassCommandAction.getClass().getSimpleName() + 
                                    "] Command Action Excute error][Session=" + session.getRemoteAddress() + "] - " + e.toString());
                            
            				// Mulit session close
                            log.debug("## HandlerName=[" + getHandlerName() + "]closeMultiSession 호출1");
                            closeMultiSession(session);
                            log.debug("## HandlerName=[" + getHandlerName() + "]closeMultiSession 호출2");
                        }
                    }else{
                    	log.error("HandlerName=[" + getHandlerName() + "]Can not found MultiSession.");
                    	throw new Exception("Can not found MultiSession.");
                    }

                	break;
			default:
				break;
            }
            
            NetworkStatus ns = gframe.getNetworkStatus();
            
            JpaTransactionManager txmanager = DataUtil.getBean(JpaTransactionManager.class);
            ModemDao modemDao = DataUtil.getBean(ModemDao.class);
            TransactionStatus txstatus = null;
            
            try {
                txstatus = txmanager.getTransaction(null);
                
                Modem node = modemDao.get(nodeId);
                
                if (node == null) {
                    String supplierName = new String(FMPProperty.getProperty("default.supplier.name").getBytes("8859_1"), "UTF-8");
                    log.debug("Supplier Name[" + supplierName + "]");
                    SupplierDao supplierDao = DataUtil.getBean(SupplierDao.class);
                    Supplier supplier = supplierName !=null ? supplierDao.getSupplierByName(supplierName):null;
                    
                    // TLS 통신 모뎀 (MBB, Ethernet) 은 RF (SubGiga) 용 모뎀은 없음.
                    if (ns instanceof NetworkStatusSub1GhzForSORIA) {
                        node = new SubGiga();
                        if (IPUtil.checkIPv6(ipAddr))
                            ((SubGiga)node).setIpv6Address(ipAddr);
                        else
                            node.setIpAddr(ipAddr);
                        node.setProtocolType(Protocol.IP.name());
                        node.setModemType(ModemType.SubGiga.name());
                        
                        String parentNodeId = ((NetworkStatusSub1GhzForSORIA)ns).getParentNode();
                        
						if(parentNodeId != null && !"".equals(parentNodeId)) {
							Set<Condition> condition = new HashSet<Condition>();
							condition.add(new Condition("modemType",
									new Object[] { ModemType.SubGiga }, null, Restriction.EQ));
							condition.add(new Condition("deviceSerial",
									new Object[] { parentNodeId }, null, Restriction.EQ));
							List<Modem> modemParent = modemDao.findByConditions(condition);
							if(modemParent != null && !modemParent.isEmpty()) {
								node.setModem(modemParent.get(0));
							}else {
								log.debug("[SKIP] No matching ParentNodeId.");
							}
						}	
                        
                        // INSERT START SP-316
                        int rssi = (int)(((NetworkStatusSub1GhzForSORIA)ns).getRssi());
                        if (node != null) {
                            ((SubGiga)node).setRssi(rssi);
                        }
                        // INSERT END SP-316                                  
                    }
                    else if (ns instanceof NetworkStatusMBB) {
                        node = new MMIU();
                        if (IPUtil.checkIPv6(ipAddr))
                            ((MMIU)node).setIpv6Address(ipAddr);
                        else
                            node.setIpAddr(ipAddr);

                        //If modem do a command trap (doTrap), modem information is already registered and the correct protocol type can be found 
                        //through the onDemandType attribute.
                        //It is unlikely that the logic will be called.
                        //node.setProtocolType(Protocol.SMS.name()); 
                        node.setModemType(ModemType.MMIU.name());
                        node.setCpuUsage(((NetworkStatusMBB) ns).getCpu() & 0xFF);
                        node.setMemoryUsage(((NetworkStatusMBB) ns).getMemory() & 0xFF);
                        ((MMIU)node).setMobileNetworkType(NIFrameConstants.getMobileNetworkType(((NetworkStatusMBB) ns).getNetwork()));
                    }
                    else if (ns instanceof NetworkStatusEthernet) {
                        node = new MMIU();
                        if (IPUtil.checkIPv6(ipAddr))
                            ((MMIU)node).setIpv6Address(ipAddr);
                        else
                            node.setIpAddr(ipAddr);
                        node.setProtocolType(Protocol.IP.name());
                        node.setModemType(ModemType.MMIU.name());
                    }
                    
                    String defaultLocName = FMPProperty.getProperty("loc.default.name");
                    LocationDao locDao = DataUtil.getBean(LocationDao.class);
                    CodeDao codeDao = DataUtil.getBean(CodeDao.class);
                    
                    if(defaultLocName != null && !"".equals(defaultLocName)){               
                        if(locDao.getLocationByName(StringUtil.toDB(defaultLocName))!=null 
                                && locDao.getLocationByName(StringUtil.toDB(defaultLocName)).size()>0) {
                            node.setLocation(locDao.getLocationByName(StringUtil.toDB(defaultLocName)).get(0));
                        }else {
                            node.setLocation(locDao.getAll().get(0));   
                        }
                    }
                    
                    node.setDeviceSerial(nodeId);
                    node.setSupplier(supplier);
                    node.setInstallDate(DateTimeUtil.getDateString(new Date()));
                    node.setLastLinkTime(DateTimeUtil.getDateString(new Date()));
                    node.setProtocolVersion("0102");
                    node.setNameSpace("SP");
                    node.setModemStatus(codeDao.findByCondition("code", "1.2.7.3"));
                    modemDao.add(node);
                    
                    // equipment install event
                    EventUtil.sendEvent("Equipment Installation",
                            TargetClass.valueOf(node.getModemType().name()),
                            node.getDeviceSerial(),
                            new String[][] {});
                }
                else {
                    boolean isUpdated = false;
    
                    log.debug("MODEM_STATUS : " + node.getModemStatus().getName());
                    if (node.getModemStatus() == null || (!node.getModemStatus().getName().equals("Normal") && !node.getModemStatus().getName().equals("Delete"))) {
                        CodeDao codeDao = DataUtil.getBean(CodeDao.class);
                        node.setModemStatus(codeDao.findByCondition("code", "1.2.7.3"));
                        isUpdated = true;
                    }
                    log.debug("MODEM_STATUS_CHANGED : " + node.getModemStatus().getName());
                    
                    String lastLinkTime = DateTimeUtil.getDateString(new Date());
                    if (node.getLastLinkTime() == null || 
                            !node.getLastLinkTime().substring(0, 12).equals(lastLinkTime.substring(0, 12))) {
                        isUpdated = true;
                        node.setLastLinkTime(lastLinkTime);
                    }
                    
                    if (ns instanceof NetworkStatusSub1GhzForSORIA) {
                        String parentNodeId = ((NetworkStatusSub1GhzForSORIA)ns).getParentNode();
                        
						if(parentNodeId != null && !"".equals(parentNodeId)) {
							Set<Condition> condition = new HashSet<Condition>();
							condition.add(new Condition("modemType",
									new Object[] { ModemType.SubGiga }, null, Restriction.EQ));
							condition.add(new Condition("deviceSerial",
									new Object[] { parentNodeId }, null, Restriction.EQ));
							List<Modem> modemParent = modemDao.findByConditions(condition);
							if(modemParent != null && !modemParent.isEmpty()) {
								node.setModem(modemParent.get(0));
							}else {
								log.debug("[SKIP] No matching ParentNodeId.");
							}
						}
                        
                        // INSERT START SP-316
                        int rssi = (int)(((NetworkStatusSub1GhzForSORIA)ns).getRssi());
                        if (!((SubGiga)node).getRssi().equals(rssi)) {
                            isUpdated = true;
                            ((SubGiga)node).setRssi(rssi);
                        }
                        // INSERT END SP-316                    
                        
                        if (IPUtil.checkIPv6(ipAddr)) {
                            if (((SubGiga)node).getIpv6Address() != null && 
                                    !((SubGiga)node).getIpv6Address().equals(ipAddr)) {
                                isUpdated = true;
                                ((SubGiga)node).setIpv6Address(ipAddr);
                            }
                        }
                        else {
                            if (node.getIpAddr() != null && 
                                    !node.getIpAddr().equals(ipAddr)) {
                                isUpdated = true;
                                node.setIpAddr(ipAddr);
                            }
                        }
                    }
                    else if (ns instanceof NetworkStatusMBB) {
                        if (IPUtil.checkIPv6(ipAddr)) {
                            if(((MMIU)node).getIpv6Address() != null && 
                                    !((MMIU)node).getIpv6Address().equals(ipAddr)){
                                isUpdated = true;
                                ((MMIU)node).setIpv6Address(ipAddr);
                            }
                        }
                        else {
                            if(((MMIU)node).getIpAddr() != null &&
                                    !((MMIU)node).getIpAddr().equals(ipAddr)){
                                isUpdated = true;
                                ((MMIU)node).setIpAddr(ipAddr);
                            }
                        }
                        
                        // Do not update  ,SP-962, 964, 970, this networkstatus information, can't know what protocol type is
                        //if (!node.getProtocolType().equals(Protocol.SMS.name())) {
                        //    isUpdated = true;
                        //    node.setProtocolType(Protocol.SMS.name());
                        //}
                    }
                    else if (ns instanceof NetworkStatusEthernet) {
                        if (IPUtil.checkIPv6(ipAddr)) {
                            if(((MMIU)node).getIpv6Address() != null && 
                                    !((MMIU)node).getIpv6Address().equals(ipAddr)){
                                isUpdated = true;
                                ((MMIU)node).setIpv6Address(ipAddr);
                            }
                        }
                        else {
                            if(((MMIU)node).getIpAddr() != null &&
                                    !((MMIU)node).getIpAddr().equals(ipAddr)){
                                isUpdated = true;
                                ((MMIU)node).setIpAddr(ipAddr);
                            }
                        }
                        
                        if (!node.getProtocolType().equals(Protocol.IP)) {
                            isUpdated = true;
                            node.setProtocolType(Protocol.IP.name());
                        }
                    }
                    
                    if (isUpdated) {
                        modemDao.update(node);
                    }
                }
                // CommLog
                txmanager.commit(txstatus);
            }
            catch (Exception e) {
                if (txstatus != null) txmanager.rollback(txstatus);
            }
        }
    }  //~messageReceived
    
    private void makeModemEvent(String nodeId, AlarmEvent alarmEvt) {
        EV_SP_240_2_0_Action action_240 = DataUtil.getBean(EV_SP_240_2_0_Action.class);
        JpaTransactionManager txmanager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            for (int i = 0; i < alarmEvt.getCount(); i++) {
                action_240.makeModemEvent(nodeId, alarmEvt.getTime()[i], 
                        alarmEvt.getAlarmId()[i].getCode(), 
                        Hex.decode(DataUtil.get4ByteToInt(alarmEvt.getPayload()[i])));
            }
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            log.error(e, e);
            
            if (txstatus != null) txmanager.rollback(txstatus);
        }
    }
    
    private void makeMeterEvent(String nodeId, MeterEvent meterEvt) throws Exception {
        EV_SP_220_2_0_Action action_220 = DataUtil.getBean(EV_SP_220_2_0_Action.class);
        JpaTransactionManager txmanager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            for (MeterEventFrame mef : meterEvt.getMeterEventFrame()) {
                action_220.doAlarm(nodeId, mef.getValue());
            }
            
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            log.error(e, e);
            
            if (txstatus != null) txmanager.rollback(txstatus);
        }
    }
    
    private void doTrap(String modemId, String ipAddr, Command cmd) throws Exception {
        Attribute attr = cmd.getAttribute();
        Modem modem = null;
        Meter meter = null;
        
        String supplierName = new String(FMPProperty.getProperty("default.supplier.name").getBytes("8859_1"), "UTF-8");
        log.debug("Supplier Name[" + supplierName + "]");
        
        JpaTransactionManager txmanager = DataUtil.getBean(JpaTransactionManager.class);
        SupplierDao supplierDao = DataUtil.getBean(SupplierDao.class);
        Supplier supplier = supplierName !=null ? supplierDao.getSupplierByName(supplierName):null;
        
        String defaultLocName = FMPProperty.getProperty("loc.default.name");
        LocationDao locDao = DataUtil.getBean(LocationDao.class);
        Location loc = null;
        TransactionStatus txstatus = null;
        String mbbModuleType = null;
        String mbbModuleRevision = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            if(defaultLocName != null && !"".equals(defaultLocName)){    
                loc = locDao.getLocationByName(StringUtil.toDB(defaultLocName)).get(0);
            }
            else {
                loc = locDao.getAll().get(0);
            }
            
            NIAttributeId attrId = null;           
            
            // modem table add/update 로 인해 루프를 두번 처리한다.
            // 첫번째 루프에서 ondemandType에 대한 속성을 먼저 꺼내고 
            // 다음 루프문에서 modem 정보를 add/update한다.
            OndemandType mode = OndemandType.SMSMode; //set default sms
            for(Data d : attr.getData()) {
            	attrId = NIAttributeId.getItem(d.getId());
            	if(attrId == NIAttributeId.OndemandType) {
            		log.debug("NIAttributeId.OndemandType="+Hex.decode(d.getValue()));
                    byte[] value = d.getValue();
                    int pos = 0;
                    byte[] b = new byte[1];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    byte ondemandType = b[0];                    
                    mode = OndemandType.getMode(ondemandType);
                } 
            }
            
            for(Data d : attr.getData()) {
            	attrId = NIAttributeId.getItem(d.getId());
            	if(attrId == NIAttributeId.MBBModuleInformation) {
            		log.debug("NIAttributeId.MBBModuleInformation="+Hex.decode(d.getValue()));
                    byte[] value = d.getValue();
                    MBBModuleInformation frameInfo = new MBBModuleInformation();
                    frameInfo.decode(value);
                    mbbModuleType = frameInfo.getModuleVersion();            
                    mbbModuleRevision = frameInfo.getModuleRevision();
                    frameInfo = null;
                } 
            }            
            
            for (Data d : attr.getData()) {
                attrId = NIAttributeId.getItem(d.getId());
                log.debug("## NIAttributeId = " + attrId.name());
                if (attrId == NIAttributeId.ModemInformation) {
                    byte[] value = d.getValue();
                    
                    int pos = 0;
                    byte[] b = new byte[8];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    log.debug("MODEM_ID[" + Hex.decode(b) + "]");
                    
                    b = new byte[1];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    byte modemType = b[0];
                    log.debug("MODEM_TYPE[" + modemType + "]");
                    
                    b = new byte[1];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    int resetTime = DataUtil.getIntToBytes(b);
                    log.debug("RESET_TIME[" + resetTime + "]");
                    
                    b = new byte[20];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    String nodeKind = new String(b).trim();
                    log.debug("NODE_KIND[" + nodeKind + "]");
                    
                    b = new byte[2];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    String swVer = Hex.decode(b);
                    swVer = Double.parseDouble(swVer.substring(0, 2) + "." + swVer.substring(2, 4)) + "";
                    log.debug("SW_VER[" + swVer + "]");
                    
                    b = new byte[2];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    String buildNo = DataUtil.getIntTo2Byte(b)+"";
                    log.debug("BUILD_NUM[" + buildNo + "]");
                    
                    b = new byte[2];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    String hwVer = Hex.decode(b);
                    hwVer = Double.parseDouble(hwVer.substring(0, 2) + "." + hwVer.substring(2, 4)) + "";
                    log.debug("HW_VER[" + hwVer + "]");
                    
                    b = new byte[1];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    byte modemStatus = b[0];
                    log.debug("STATUS[" + modemStatus + "]");
                    
                    b = new byte[1];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    byte modemMode = b[0];
                    log.debug("MODE[" + modemMode + "]");
                    
                    ModemDao modemDao = DataUtil.getBean(ModemDao.class);
                    modem = modemDao.get(modemId);
                    
                    // SP-862
                    DeviceModelDao  deviceModelDao = DataUtil.getBean(DeviceModelDao.class);
                    DeviceModel deviceModel = null;
                    List<DeviceModel> models = deviceModelDao.getDeviceModelByName(supplier.getId(), nodeKind);
                    if ( models != null && models.size() == 1 ){
                        deviceModel = models.get(0);
                    }

                    if (modem != null) {
                        boolean isUpdated = false;
                        if (modem.getFwVer() == null || !modem.getFwVer().equals(swVer)) {
                            isUpdated = true;
                            modem.setFwVer(swVer);
                        }
                        if (modem.getSwVer() == null || !modem.getSwVer().equals(swVer)) {
                            isUpdated = true;
                            modem.setSwVer(swVer);
                        }
                        if (modem.getFwRevision() == null || !modem.getFwRevision().equals(buildNo)) {
                            isUpdated = true;
                            modem.setFwRevision(buildNo);
                        }
                        if (modem.getHwVer() == null || !modem.getHwVer().equals(hwVer)) {
                            isUpdated = true;
                            modem.setHwVer(hwVer);
                        }
                        if (modem.getNodeKind() == null || !modem.getNodeKind().equals(nodeKind)) {
                            isUpdated = true;
                            modem.setNodeKind(nodeKind);
                        }
                        if (modem.getResetCount() == null || modem.getResetCount() != resetTime) {
                            isUpdated = true;
                            modem.setResetCount(resetTime);
                        }
                        if (modem.getNameSpace() == null || "".equals(modem.getNameSpace())) {
                            isUpdated = true;
                            modem.setNameSpace("SP");
                        }
                        if (modem.getProtocolVersion() == null || "".equals(modem.getProtocolVersion())) {
                            isUpdated = true;
                            modem.setProtocolVersion("0102");
                        }

                        log.debug("MODEM_STATUS : " + modem.getModemStatus().getName());
                        if (modem.getModemStatus() == null || (!modem.getModemStatus().getName().equals("Normal") && !modem.getModemStatus().getName().equals("Delete"))) {
                            CodeDao codeDao = DataUtil.getBean(CodeDao.class);
                            modem.setModemStatus(codeDao.findByCondition("code", "1.2.7.3"));
                            isUpdated = true;
                        }
                        log.debug("MODEM_STATUS_CHANGE : " + modem.getModemStatus().getName());
                        
                        if (modemType == 0x20 || modemType == 0x021) {
                            if (modem.getProtocolType() == null || 
                                    (modem.getProtocolType() != null && 
                                    !modem.getProtocolType().equals(Protocol.IP))) {
                                isUpdated = true;
                                modem.setProtocolType(Protocol.IP.name());
                                modem.setModemType(ModemType.SubGiga.name());
                            }
                            
                            if (IPUtil.checkIPv6(ipAddr)) {
                                if (((SubGiga)modem).getIpv6Address() == null || 
                                        (!((SubGiga)modem).getIpv6Address().equals(ipAddr))) {
                                    isUpdated = true;
                                    ((SubGiga)modem).setIpv6Address(ipAddr);
                                }
                            }
                            else {
                                if (modem.getIpAddr() == null || !modem.getIpAddr().equals(ipAddr)) {
                                    isUpdated = true;
                                    modem.setIpAddr(ipAddr);
                                }
                            }
                        }
                        // MBB
                        else if (modemType == 0x22) {
                        	
                        	Protocol orgProtocol = modem.getProtocolType() == null ? null : modem.getProtocolType();

                            if(mode.equals(OndemandType.SMSMode)) {
                                modem.setProtocolType(Protocol.SMS.name());
                                modem.setModemType(ModemType.MMIU.name());
                            }else if(mode.equals(OndemandType.DirectMode)) {
                                modem.setProtocolType(Protocol.GPRS.name());
                                modem.setModemType(ModemType.MMIU.name());
                            }                            

                			if (Double.parseDouble(modem.getFwVer()) >= 1.5) {
                				 modem.setProtocolType(Protocol.GPRS.name());
                 			}
                			
                  			if(mbbModuleType != null && !"".equals(mbbModuleType)) {
                 				 ((MMIU)modem).setModuleVersion(mbbModuleType);
                 			}
             			
                 			if(mbbModuleRevision != null && !"".equals(mbbModuleRevision)) {
                 				((MMIU)modem).setModuleRevision(mbbModuleRevision);
                 			}
                        	
                            if (orgProtocol == null || !orgProtocol.equals(modem.getProtocolType())) {
                                isUpdated = true;
                            }
                            
                            if (IPUtil.checkIPv6(ipAddr)) {
                                if (((MMIU)modem).getIpv6Address() == null || 
                                        (!((MMIU)modem).getIpv6Address().equals(ipAddr))) {
                                    isUpdated = true;
                                    ((MMIU)modem).setIpv6Address(ipAddr);
                                }
                            }
                            else {
                                if (modem.getIpAddr() == null || !modem.getIpAddr().equals(ipAddr)) {
                                    isUpdated = true;
                                    modem.setIpAddr(ipAddr);
                                }
                            }
                            
                        }
                        // Ethernet
                        else if (modemType == 0x23) {
                            if (modem.getProtocolType() == null || 
                                    (modem.getProtocolType() != null &&
                                    !modem.getProtocolType().equals(Protocol.IP))) {
                                isUpdated = true;
                                modem.setProtocolType(Protocol.IP.name());
                                modem.setModemType(ModemType.MMIU.name());
                            }
                            
                            if (IPUtil.checkIPv6(ipAddr)) {
                                if (((MMIU)modem).getIpv6Address() == null || 
                                        (!((MMIU)modem).getIpv6Address().equals(ipAddr))) {
                                    isUpdated = true;
                                    ((MMIU)modem).setIpv6Address(ipAddr);
                                }
                            }
                            else {
                                if (modem.getIpAddr() == null || !modem.getIpAddr().equals(ipAddr)) {
                                    isUpdated = true;
                                    modem.setIpAddr(ipAddr);
                                }
                            }
                        }
                        
                        if ( deviceModel != null ){ // SP-862
                            if ( modem.getModel() == null || !modem.getModel().getId().equals(deviceModel.getId())){
                                isUpdated = true;
                                modem.setModel(deviceModel);
                            }
                        }
                        String lastLinkTime = DateTimeUtil.getDateString(new Date());
                        if (modem.getLastLinkTime() == null || 
                                !modem.getLastLinkTime().substring(0, 12).equals(lastLinkTime.substring(0, 12))) {
                            isUpdated = true;
                            modem.setLastLinkTime(lastLinkTime);
                        }
                        
                        if (isUpdated) modemDao.update(modem);
                    }
                    else {
                        if (modemType == 0x20 || modemType == 0x021) {
                            modem = new SubGiga();
                            modem.setProtocolType(Protocol.IP.name());
                            modem.setModemType(ModemType.SubGiga.name());
                            
                            if (IPUtil.checkIPv6(ipAddr))
                                ((SubGiga)modem).setIpv6Address(ipAddr);
                            else
                                modem.setIpAddr(ipAddr);
                        }
                        // MBB
                        else if (modemType == 0x22) {
                            modem = new MMIU();

                            if(mode.equals(OndemandType.SMSMode)) {
                                modem.setProtocolType(Protocol.SMS.name());
                            }else if(mode.equals(OndemandType.DirectMode)) {
                                modem.setProtocolType(Protocol.GPRS.name());
                            }  
                  			if (Double.parseDouble(swVer) >= 1.5) {
               				    modem.setProtocolType(Protocol.GPRS.name());
                			}

                            modem.setModemType(ModemType.MMIU.name());
                            
                            if (ipAddr != null && ipAddr.contains(":"))
                                ((MMIU)modem).setIpv6Address(ipAddr);
                            else
                                modem.setIpAddr(ipAddr);
                            
                  			if(mbbModuleType != null && !"".equals(mbbModuleType)) {
                  				((MMIU)modem).setModuleVersion(mbbModuleType);
                  			}
              			
                  			if(mbbModuleRevision != null && !"".equals(mbbModuleRevision)) {
                  				((MMIU)modem).setModuleRevision(mbbModuleRevision);
                  			}
                            
                        }
                        // Ethernet
                        else if (modemType == 0x23) {
                            modem = new MMIU();
                            modem.setProtocolType(Protocol.IP.name());
                            modem.setModemType(ModemType.MMIU.name());
                            
                            if (ipAddr != null && ipAddr.contains(":"))
                                ((MMIU)modem).setIpv6Address(ipAddr);
                            else
                                modem.setIpAddr(ipAddr);
                        }
                        DeviceModelDao modelDao = DataUtil.getBean(DeviceModelDao.class);
                        DeviceModel model = modelDao.findByCondition("name", nodeKind);
                        if (model != null) modem.setModel(model);
                        
                        modem.setDeviceSerial(modemId);
                        modem.setFwVer(swVer);
                        modem.setSwVer(swVer);
                        modem.setFwRevision(buildNo);
                        modem.setHwVer(hwVer);
                        modem.setSupplier(supplier);
                        modem.setLocation(loc);
                        modem.setInstallDate(DateTimeUtil.getDateString(new Date()));
                        modem.setLastLinkTime(DateTimeUtil.getDateString(new Date()));
                        modem.setNameSpace("SP");
                        modem.setProtocolVersion("0102");
                        
                        CodeDao codeDao = DataUtil.getBean(CodeDao.class);
                        modem.setModemStatus(codeDao.findByCondition("code", "1.2.7.3"));
                        
                        modemDao.add(modem);
                        try {
                            EventUtil.sendEvent("Equipment Installation",
                                    TargetClass.valueOf(modem.getModemType().name()),
                                    modem.getDeviceSerial(),
                                    new String[][] {});
                        }
                        catch (Exception e) {
                            log.error(e, e);
                        }
                    }
                }else if (attrId == NIAttributeId.MeterInformation) {
                    byte[] value = d.getValue();
                    
                    int pos = 0;
                    byte[] b = new byte[1];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    byte meterCommStatus = b[0];
                    log.debug("METER_COMM_STATUS[" + meterCommStatus + "]");
                    
                    b = new byte[1];
                    System.arraycopy(value, pos, b, 0, b.length);
                    pos += b.length;
                    
                    int meterCnt = DataUtil.getIntToBytes(b);
                    log.debug("METER_COUNT[" + meterCnt + "]");
                    
                    b = new byte[20];
                    // 485인경우 여러개의 미터가 붙을 수 있지만 소리아는 한개
                    String meterId = null;
                    for (int i = 0; i < meterCnt; i++) {
                        System.arraycopy(value, pos, b, 0, b.length);
                        pos += b.length;
                        
                        meterId = new String(b).trim();
                    }
                    MeterDao meterDao = DataUtil.getBean(MeterDao.class);
                    meter = meterDao.get(meterId);
                    
                    if (meter == null) {
                        meter = new EnergyMeter();
                        meter.setMdsId(meterId);
                        meter.setSupplier(supplier);
                        meter.setLocation(loc);
                        meter.setMeterType(CommonConstants.getMeterTypeByName("EnergyMeter"));
                        
                        CodeDao codeDao = DataUtil.getBean(CodeDao.class);
                        meter.setMeterStatus(codeDao.findByCondition("code", "1.3.3.8")); // New Registered
                        meterDao.add(meter);
                        
                        try {
                        	
                        	//before transaction commit, it can't read meter data synchronously
                            EventUtil.sendEvent("Equipment Installation",
                                    TargetClass.valueOf("EnergyMeter"),
                                    meterId,
                                    new String[][] {});
                        }
                        catch (Exception e) {
                            log.error(e, e);
                        }
                    }
                }
            }
            
            validateRelation(meter, modem);
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            if (txstatus != null) txmanager.rollback(txstatus);
        }
    }
    
    private void validateRelation(Meter meter, Modem modem) throws Exception
    {
        if (meter != null) {
            ModemDao modemDao = DataUtil.getBean(ModemDao.class);
            MeterDao meterDao = DataUtil.getBean(MeterDao.class);
            
            Modem orgModem = meter.getModem();
    
//          if (orgModem == null || !orgModem.getDeviceSerial().equals(modem.getDeviceSerial())) {
            if (orgModem == null) {
                // TODO meterDao.update(meter);
                
                if (modem.getModemType() == ModemType.ZEU_MBus || modem.getModemType() == ModemType.SubGiga) {
                    Set<Meter> m = modem.getMeter();
                    if (m == null) {
                        m = new HashSet<Meter>();
                    }
                    m.add(meter);
                    modem.setMeter(m);
                }
                else {
                    for (Meter _m : modem.getMeter()) {
                    	// UPDATE START SP-917
//                        if (_m != null) _m.setModem(null);
                        if (_m != null) {
                        	_m.setModem(null);
                        	meterDao.update(_m);
                        }
                    	// UPDATE END SP-917
                    }
                    
                    Set<Meter> m = new HashSet<Meter>();
                    m.add(meter);
                    modem.setMeter(m);
                    
/*                    // 모뎀 교체로 처리한다.
                    if (orgModem != null) {
                        orgModem.setMeter(null);
                        
                        EventUtil.sendEvent("Equipment Replacement",
                                TargetClass.valueOf(modem.getModemType().name()),
                                modem.getDeviceSerial(),
                                new String[][] {{"equipType", modem.getModemType().name()},
                                                {"oldEquipID", orgModem.getDeviceSerial()},
                                                {"newEquipID", modem.getDeviceSerial()}
                                });
                    }
*/                }
                
                meter.setModem(modem);
                // DELETE START SP-917
                // modemDao.update(modem);
                // DELETE END SP-917
                meterDao.update(meter);
            }else if (!orgModem.getDeviceSerial().equals(modem.getDeviceSerial())) {
                // SP-825
            	ArrayList<Integer> meterIdList = new ArrayList<Integer>();
                for (Meter m : modem.getMeter()) {
                	meterIdList.add(m.getId());
                }

                modem.setMeter(orgModem.getMeter());
                // DELETE START SP-917
                // modemDao.update_requires_new(modem);
                // DELETE END SP-917
                for (Meter m : modem.getMeter()) {
                    m.setModem(modem);
                    meterDao.update_requires_new(m);
                 }

                for (Integer m : meterIdList) {
                	// UPDATE START SP-917  
                    // meterDao.updateModemIdNull(m);
                	Meter updMeter = meterDao.get(m);
                	updMeter.setModem(null);
                	meterDao.update_requires_new(updMeter);
                	// UPDATE END SP-917  
                }
                
                EventUtil.sendEvent("Equipment Replacement",
                        TargetClass.valueOf(modem.getModemType().name()),
                        modem.getDeviceSerial(),
                        new String[][] {{"equipType", modem.getModemType().name()},
                                        {"oldEquipID", orgModem.getDeviceSerial()},
                                        {"newEquipID", modem.getDeviceSerial()}
                        });

                // DELETE START SP-917
                // meter.setModem(modem);
                // modemDao.update(modem);
                // meterDao.update(meter);
                // DELETE END SP-917
            }
            // TODO 관계 검증
        }
    }
    
    public void doCommand(IoSession session, GeneralFrame gframe, Command cmd) throws Exception {
        Attribute attr = cmd.getAttribute();
        NIAttributeId attrId = null;
        for (Data d : attr.getData()) {
            attrId = NIAttributeId.getItem(d.getId());
            if (attrId == NIAttributeId.MeterSharedKey) {
                byte[] value = d.getValue();
                
                int pos = 0;
                byte[] b = new byte[1];
                System.arraycopy(value, pos, b, 0, b.length);
                pos += b.length;
                log.debug("ReqeustInfo[" + DataUtil.getIntToBytes(b) + "]");
                
                b = new byte[8];
                System.arraycopy(value, pos, b, 0, b.length);
                pos += b.length;
                log.debug("ModemEUI[" + Hex.decode(b) + "]");
                String modemId = Hex.decode(b);
                
                b = new byte[1];
                System.arraycopy(value, pos, b, 0, b.length);
                pos += b.length;
                int len = DataUtil.getIntToBytes(b);
                log.debug("Len[" + len + "]");
                
                b = new byte[len];
                System.arraycopy(value, pos, b, 0, b.length);
                pos += b.length;
                String meterId = new String(b);
                log.debug("MeterId[" + meterId + "]");
                
                OacServerApi api  = new OacServerApi();
                HashMap<String,String> sharedKey = api.getMeterSharedKey(modemId, meterId);
                if ( sharedKey != null ){
                    String masterKey = sharedKey.get("MasterKey");
                    String unicastKey = sharedKey.get("UnicastKey");
                    String multicastKey = sharedKey.get("MulticastKey");
                    String authKey = sharedKey.get("AuthenticationKey");
                    
                    byte[] cmdFrame = gframe.msKeyFrame(cmd.getFrameTid(), masterKey, unicastKey, multicastKey, authKey);
                    log.debug("HandlerName=[" + getHandlerName() + "][NISV][GETMETERKEY] RemoteAddress=" + session.getRemoteAddress()+"/"+session.getLocalAddress());
                    //SP-819
                    WriteFuture future = session.write(cmdFrame);
                    if(!future.awaitUninterruptibly(Long.parseLong(
                            FMPProperty.getProperty(
                            		"protocol.ni.closewait.timeout","5000")))) {
                    }
                }
            }
            else if (attrId == NIAttributeId.ModemTime) {
                session.write(gframe.modemTimeFrame(cmd.getFrameTid()));
            }
            else if (attrId == NIAttributeId.ModemTXPower) {
                session.write(gframe.txPowerFrame(cmd.getFrameTid()));
            }
        }
    }    
    
    public GeneralFrame getResponse(IoSession session, long tid)
            throws Exception
    {
        long key = tid;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        //int waitResponseCnt = 0;
        GeneralFrame obj = null;
        
        while(session.isConnected())
        { 
            if(response.containsKey(key)) 
            { 
                obj = (GeneralFrame) response.get(key); 
                response.remove(key); 
                if(obj == null) 
                    continue;
                log.debug("HandlerName=[" + getHandlerName() + "] getResponse success.");
                return obj; 
            } 
            else
            {
                waitResponse();
                ctime = System.currentTimeMillis();
                if(((ctime - stime)/1000) > responseTimeout)
                {
                    log.debug("HandlerName=[" + getHandlerName() + "]getResponse:: SESSION IDLE COUNT["+session.getIdleCount(IdleStatus.BOTH_IDLE)+"]");
                    response.remove(key); 
                    throw new Exception("[NICL][TID : " + key +"],[Response Timeout:"+responseTimeout +"]");
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
    public void messageSent(IoSession session, Object message) 
           throws Exception {
       if (message instanceof IoBuffer) {
            IoBuffer buffer = (IoBuffer) message;
            SocketAddress remoteAddress = session.getRemoteAddress();
            log.debug("HandlerName=[" + getHandlerName() + "][NISV][messageSent][SessionID=" + session.getId() + "]:"+remoteAddress+","+Hex.decode(buffer.array()));
        }
    }
    
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        log.debug("HandlerName=[" + getHandlerName() + "][NISV][CLOSED]"+session.getId() + ",  sessionClosed call. RemoteAddress=" + session.getRemoteAddress()+"/"+session.getLocalAddress());
		//SP-691
        session.closeNow();
        SnowflakeGeneration.deleteId();
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
    	String tname = Thread.currentThread().getName();
    	SnowflakeGeneration.setSeq(tname, sequenceLog);
        log.debug("HandlerName=[" + getHandlerName() + "][NISV][CREATE]"+session.getId() + "[isCmdAdapter : " + isCmdAdapter +"], RemoteAddress[" + session.getRemoteAddress() + "]");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
            throws Exception {
 
        log.debug(" READER IDLE TIME : " + session.getConfig().getReaderIdleTime());
        log.debug(" IDLE COUNT : "
                    + session.getIdleCount(IdleStatus.READER_IDLE));
        //SP-890
        if(session != null && session.isConnected()) {
            session.closeNow();
        }
        log.debug("HandlerName=[" + getHandlerName() + "][NISV][IDLE]"+session.getId() + "] RemoteAddress=" + session.getRemoteAddress()+"/"+session.getLocalAddress());
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
       String tname = Thread.currentThread().getName();
       if(isCmdAdapter) { //HES to Device
    	   SnowflakeGeneration.setSeq(tname, sequenceLog);
       }
       else { //Device to HES
    	   SnowflakeGeneration.getId(tname);
       }
        
       log.debug("HandlerName=[" + getHandlerName() + "][NISV][OPEN]"+session.getId() + "[isCmdAdapter : " + isCmdAdapter +"], RemoteAddress[" + session.getRemoteAddress() + "]"+"/"+session.getLocalAddress());
       session.getConfig().setWriteTimeout(writeTimeout);
       //SP-890
       session.getConfig().setIdleTime(IdleStatus.READER_IDLE,
               readidleTime);
       session.getConfig().setIdleTime(IdleStatus.WRITER_IDLE,
               writeidleTime);
       // isCmdAdapter is true, ask modem who it is (modem information)
       /*if (isCmdAdapter) {
           log.debug("[NISV][OPEN]"+session.getId() + "[isCmdAdapter]");
       }*/
    }
    
	@Override
	public void inputClosed(IoSession session) throws Exception {
		log.debug("HandlerName=[" + getHandlerName() + "][NISV][INPUT_CLOSE] sessionId=" + session.getId() + ", inputClosed call1=" + session.getRemoteAddress()+"/"+session.getLocalAddress());
        session.closeNow();        
		log.debug("HandlerName=[" + getHandlerName() + "][NISV][INPUT_CLOSE] sessionId=" + session.getId() + ", inputClosed call2=" + session.getRemoteAddress()+"/"+session.getLocalAddress());
		SnowflakeGeneration.deleteId();
	}

	@Override
	public void event(IoSession session, FilterEvent event) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
