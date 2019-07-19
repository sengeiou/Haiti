package com.aimir.fep.protocol.snmp;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.NetworkInfoLogDao;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.protocol.nip.frame.NIFrameConstants;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.SnmpMibUtil;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.NetworkInfoLog;
import com.aimir.util.DateTimeUtil;

import org.snmp4j.PDU;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;

@Component
public class SnmpProtocolHandler extends IoHandlerAdapter{

    private static Log logger = LogFactory.getLog(SnmpProtocolHandler.class);

    private int sessionIdleTime = Integer.parseInt(FMPProperty.getProperty("protocol.idle.time", "5"));
    private int enqTimeout = Integer.parseInt(FMPProperty.getProperty("protocol.enq.timeout", "10"));
    private int retry = Integer.parseInt(FMPProperty.getProperty("protocol.retry", "3"));

    SnmpMibUtil mu = null;
    
	@Autowired
	private ProcessorHandler processorHandler;
    
	@SuppressWarnings("unused")
	private void putServiceData(String serviceType, Serializable data) {
		try {
			processorHandler.putServiceData(serviceType, data);
		} catch (Exception e) {
			logger.error("Error putServiceData", e);
		}
	}

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        session.getConfig().setWriteTimeout(enqTimeout);
        session.getConfig().setIdleTime(IdleStatus.READER_IDLE, sessionIdleTime);

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date d = new Date(session.getLastWriterIdleTime());
        logger.info("### sessionOpened : " + session.getRemoteAddress() + ", lastWriteIdleTime : " + sf.format(d));
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        Iterator<Object> keys = session.getAttributeKeys().iterator();
        while (keys.hasNext()) {
            Object key = keys.next();
            logger.info("key=["+key+"], value=["+session.getAttribute(key)+"]");
            session.removeAttribute(key);
        }

        logger.info("### Bye Bye ~ Client session closed from=" + session.getRemoteAddress().toString());
        session.closeNow();
        logger.info("### this Session is being closed or closed? = ["+session.isClosing()+"], Session isConnected = ["+session.isConnected()+"], Session isBothIdle = ["+session.isBothIdle()+"]");
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        int idleCount = session.getIdleCount(IdleStatus.READER_IDLE);
        logger.info("### Session = ["+session.getRemoteAddress()+"], IDLE COUNT=["+idleCount+"]");
        if (idleCount >= retry) {
            session.write(SnmpConstants.SnmpActionType.UNKNOWN);
        }
        //SP-890
        if(session != null && session.isConnected()) {
            session.closeNow();
        }       
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        logger.info("### [MESSAGE_RECEIVED] from=["+session.getRemoteAddress().toString()+"]  SessionId=["+session.getId()+"] ###");
        if(mu == null){
            mu = SnmpMibUtil.getInstance("snmpv2c");
        }
        // PDU 처리
        try {
            if(message instanceof PDU) {
                PDU pdu = (PDU)message;
                Map<String,Object> receiveMap = new HashMap<String,Object>();
                
                if(pdu != null){                                        
                    int pduType = pdu.getType();
                    logger.info("### [PDU] "+pdu.toString() );
                    if(pduType == PDU.TRAP) {
                        //TRAP 정보를 receiveMap에 저장
                        Vector vector = new Vector(5,2);
                        vector = pdu.getVariableBindings();                        
                        
                        int vSize = vector.size();                        
                        logger.info("SIZE OF Variables" + vSize);                        
                        for(int i=0; i<vSize; i++){
                            VariableBinding vb = (VariableBinding) vector.get(i);
                            OID oid = vb.getOid();
                            String oidStr = oid.toString();
                            String trapName = mu.getName(oidStr);
                                                       
                            if(trapName!=null){
                            	logger.info("MIB Information of ["+ trapName + "]" +vb.getVariable());
                                receiveMap.put(trapName, vb.getVariable());
                                String varStr = vb.getVariable().toString();
                                String varName = mu.getName(varStr);
                                receiveMap.put(varStr, varName);
                            }else{
                                logger.info("MIB Information of ["+ oid.toString() + "] is Null");
                            }

                        }

                        //Sender를 맵에 저장                        
                        String senderIp = session.getRemoteAddress().toString();
                        // session에서 얻은 정보가 /0.0.0.0:162 형태로 있어서 ip만 남도록 가공함
                        senderIp = senderIp.substring(1, senderIp.lastIndexOf(":"));
                        receiveMap.put("sender", senderIp);
                        receiveMap.put("activatorIp", senderIp);                        
                       
                        //StartTime
                        receiveMap.put("startTime", session.getAttribute("startTime").toString());
                        
                        //이벤트 저장 함수  (실패면 세션 닫음)  
                        sendTrapEvent(receiveMap);
                        session.closeNow();
                    }else {
                        // 트랩이 아닌 PDU
                    	logger.info("PDU type is not trap. The data will not be saved.");
                    	session.closeNow();
                    }
                }
            }
        } catch (Exception ex){
            logger.error("Error in SnmpProtocolHandler", ex);
        }

    }
    
    // 전달받은 맵에서 트랩 종류를 구분하여, 이벤트로 전송
    public boolean sendTrapEvent(Map _receiveMap) throws Exception{
    	HashMap<String,Object> saveMap = (HashMap<String,Object>)_receiveMap;
    	OID trapOid = null;
    	String oidStr = null;
    	String trapName = null;
    	String sender = null;
    	String message = null;
    	TargetClass target = TargetClass.FEP;
    	
    	NetworkInfoLog nlog = null;
    	// 사전 협의된 Trap OID가 있는지 확인
    	if(saveMap.containsKey("SnmpTrapOID")){
    		trapOid = (OID)saveMap.get("SnmpTrapOID");
    		oidStr = trapOid.toString();
    		
    		if(oidStr.contains("1.3.6.1.4.1.3204.31.10.1")){
    			// trapFromMeter, 미터시리얼을 포함하고있음
    			if(saveMap.containsKey("trapMeterSerial")){
    				sender = saveMap.get("trapMeterSerial").toString();
    				trapName = saveMap.get(oidStr).toString();
    				target = TargetClass.SubGiga;
    				logger.info("Trap From Meter ["+ sender +"], OID ["+ oidStr +"]");
    			}else{
    				// 미터시리얼이 없으면 에러 처리
    				return false;
    			}    			    			
    		}else if(oidStr.contains("1.3.6.1.4.1.3204.31.10.2")){
    			// trapFromMT, sender의 IP를 이용하여 이벤트 저장
    			sender = saveMap.get("sender").toString();
    			trapName = saveMap.get(oidStr).toString();
    			target = TargetClass.Modem;
    			logger.info("Trap From Meter-Terminal ["+ sender +"], OID ["+ oidStr +"]");
    			String eui = "";
    			String cpuUsage = "";
    			String memoryUsage = "";
    			String totalTxSize = "";
    			int network = 0;
    			String rssi = "";
    			String lqi = "";
    			String etx = "";
    			String clock = "";
    			String currentNetworkStatus = "";
    			String imei = "";
    			String lastConnectionStatus = "";

    			/*
    		    trapFromMT	.1.3.6.1.4.1.3204.31.10.2
    		    mtEui64	.1.3.6.1.4.1.3204.31.10.2.1
    		    mtCpuUsage	.1.3.6.1.4.1.3204.31.10.2.2
    		    mtMemoryUsage	.1.3.6.1.4.1.3204.31.10.2.3
    		    mtTotalTxSize	.1.3.6.1.4.1.3204.31.10.2.4
    		    mtNetwork	.1.3.6.1.4.1.3204.31.10.2.5
    		    mtParentNodeId	.1.3.6.1.4.1.3204.31.10.2.6
    		    mtRSSI	.1.3.6.1.4.1.3204.31.10.2.7
    		    mtLQI	.1.3.6.1.4.1.3204.31.10.2.8
    		    mtETX	.1.3.6.1.4.1.3204.31.10.2.9
    		    mtClock	.1.3.6.1.4.1.3204.31.10.2.10
    		    mtCurrentNetworkStatus	.1.3.6.1.4.1.3204.31.10.2.11
    		    mtIMEI	.1.3.6.1.4.1.3204.31.10.2.12
    		    mtLastConnectionStatus	.1.3.6.1.4.1.3204.31.10.2.13
    		    */
    			nlog = new NetworkInfoLog();
    			
    			nlog.setIpAddr(sender);
    			if(saveMap.containsKey("mtEui64")){
    				eui = saveMap.get("mtEui64").toString().replaceAll("-", "");
    				eui = eui.replaceAll(":", "");
    				eui = eui.toUpperCase();
    				logger.info("mtEui64=["+eui+"]");
    				nlog.setTargetNode(eui);
    				nlog.setCommand("trapFromMT");
    				nlog.setDateTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddhhmmss"));
    			}
    			if(saveMap.containsKey("mtCpuUsage")){
    				cpuUsage = saveMap.get("mtCpuUsage").toString();
    				nlog.setCpuUsage(Integer.parseInt(cpuUsage));
    			}
    			if(saveMap.containsKey("mtMemoryUsage")){
    				memoryUsage = saveMap.get("mtMemoryUsage").toString();    
    				nlog.setMemoryUsage(Integer.parseInt(memoryUsage));
    			}
    			if(saveMap.containsKey("mtTotalTxSize")){
    				totalTxSize = saveMap.get("mtTotalTxSize").toString();
    				nlog.setTotalTxSize(totalTxSize);
    			}
    			if(saveMap.containsKey("mtNetwork")){
    				network = Integer.parseInt(saveMap.get("mtNetwork").toString());
    				nlog.setMobileNetworkType(NIFrameConstants.getMobileNetworkType((byte)network));
    			}
    			if(saveMap.containsKey("mtRSSI")){
    				rssi = saveMap.get("mtRSSI").toString();
    				nlog.setRssi(Double.parseDouble(rssi));
    			}
    			if(saveMap.containsKey("mtLQI")){
    				lqi = saveMap.get("mtLQI").toString();
    				nlog.setLqi(Double.parseDouble(lqi));
    			}
    			if(saveMap.containsKey("mtETX")){
    				etx = saveMap.get("mtETX").toString();
    				nlog.setEtx(Double.parseDouble(etx));
    			}
    			if(saveMap.containsKey("mtClock")){
    				clock = saveMap.get("mtClock").toString();
    				
    				if(clock.getBytes().length == 8) {
    					nlog.setMtClock(getDate8Byte(clock.getBytes()));
    				}else if(clock.getBytes().length > 8) {
    					clock = clock.replaceAll("-", "");
    					clock = clock.replaceAll(":", "");
    					if(clock.length() == 16) {
    						nlog.setMtClock(getDate8Byte(Hex.encode(clock)));
    					}
    				}    				
    			}
    			if(saveMap.containsKey("mtCurrentNetworkStatus")){
    				currentNetworkStatus = saveMap.get("mtCurrentNetworkStatus").toString();
    				currentNetworkStatus = currentNetworkStatus.replaceAll("OK", "");
    				currentNetworkStatus = currentNetworkStatus.replaceAll("#RFSTS: ", "");
    				currentNetworkStatus = currentNetworkStatus.replaceAll("\"", "");
    				currentNetworkStatus = currentNetworkStatus.trim();
    				
    				String[] splitVal = currentNetworkStatus.split(",");
    				
    				if(splitVal != null && splitVal.length >= 14) {

    					//SP-1017
    					String freqmobileType = getMobileNetworkTypeByFrequency(splitVal[1]);
    					
    					if(!"".equals(freqmobileType) && !freqmobileType.equals(nlog.getMobileNetworkType())) {
							nlog.setMobileNetworkType(freqmobileType);
							logger.info("Different mobile type with frequency, current mobile network=["+nlog.getMobileNetworkType()+"], frequency=["+splitVal[1]+"], real network=["+freqmobileType+"]");
    					}
    					
    					if(nlog.getMobileNetworkType().equals("2G")) {
        					nlog.setCellId(splitVal[9]);
        					nlog.setFrequency(splitVal[1]);
        					nlog.setCurrentNetworkStatus(currentNetworkStatus);
        					if(splitVal[5] != null && !"".equals(splitVal[5])) {
        		  				nlog.setTxPower(Double.parseDouble(splitVal[5]));
        					}         
         					if(splitVal[2] != null && !"".equals(splitVal[2])) {
        		  				nlog.setRssi(Double.parseDouble(splitVal[2]));
        					} 
               				nlog.setImsi(splitVal[10]);
    					}
    					if(nlog.getMobileNetworkType().equals("3G")) {
    	    				///#RFSTS: "242 01",10737,266,-3.5,-84,-81,5141,01,,64,19,0,2,,D0A736,"240090000168031","Com4",3,3,3,1,3,3,1,1
    	    				
        					nlog.setCellId(splitVal[14]);
        					nlog.setFrequency(splitVal[1]);
        					nlog.setCurrentNetworkStatus(currentNetworkStatus);
        					if(splitVal[8] != null && !"".equals(splitVal[8])) {
        		  				nlog.setTxPower(Double.parseDouble(splitVal[8]));
        					}         
         					if(splitVal[5] != null && !"".equals(splitVal[5])) {
        		  				nlog.setRssi(Double.parseDouble(splitVal[5]));
        					} 
            				nlog.setImsi(splitVal[15]);
    					}
    					if(nlog.getMobileNetworkType().equals("4G")) {
    						// Information of [mtCurrentNetworkStatus] (V2)
    						//#RFSTS: "242 02",1650,-76,-45,-6.0,09c6,FF,0,0,19,1,"20F1515","242090000388938","Com4",3,3,720000,10800

    						//V2: <PLMN>,<EARFCN>,<RSRP>,<RSSI>,<RSRQ>,<TAC>,<RAC>,[<TXPWR>],<DRX>,<MM>,<RRC>,<CID>,<IMSI>,[<NetNameAsc>],<SD>,<ABND>,<T3402>,<T3412>
    						//EUG의 경우 아래와 같이 옵니다.
    						//EUG: <PLMN>,<EARFCN>,<RSRP>,<RSSI>,<RSRQ>,<TAC>,[<TXPWR>],<DRX>,<MM>,<RRC>,<CID>,<IMSI>,[<NetNameAsc>],<SD>,<ABND>,<SINR>
           					nlog.setCurrentNetworkStatus(currentNetworkStatus);
    						nlog.setFrequency(splitVal[1]);
 
    						if(splitVal[2] != null && !"".equals(splitVal[2])) {
        		  				nlog.setRssi(Double.parseDouble(splitVal[2]));
        					} 
    						
    						if(splitVal.length <= 16) {

             					if(splitVal[6] != null && !"".equals(splitVal[6])) {
            		  				nlog.setTxPower(Double.parseDouble(splitVal[6]));
            					}         
        						nlog.setCellId(splitVal[10]);
                				nlog.setImsi(splitVal[11]);
        					}else {
             					if(splitVal[7] != null && !"".equals(splitVal[7])) {
            		  				nlog.setTxPower(Double.parseDouble(splitVal[7]));
            					} 
        						nlog.setCellId(splitVal[11]);
                				nlog.setImsi(splitVal[12]);
        					}
    					}
      				
    				}

    				/*
    				(GSM network)
    				#RFSTS:<PLMN>,<ARFCN>,<RSSI>,<LAC>,<RAC>,<TXPWR>,<MM>,
    				<RR>,<NOM>,<CID>,<IMSI>,<NetNameAsc>,<SD>,<ABND>
    				
    				(WCDMA network)
    				#RFSTS:
    				[<PLMN>],<UARFCN>,<PSC>,<Ec/Io>,<RSCP>, RSSI>,[<LAC>],
    				[<RAC>],<TXPWR>,<DRX>,<MM>,<RRC>,<NOM>,<BLER>,<CID>,<IMSI>,
    				<NetNameAsc>,<SD>,<nAST>[,<nUARFCN><nPSC>,<nEc/Io>]
    						
    				(LTE network)
    						#RFSTS:
    						<PLMN>,<EARFCN>,<RSRP>,<RSSI>,<RSRQ>,<TAC>,<RAC>,[<TXPWR>],<
    						DRX>,<MM
    						>,<RRC>,<CID>,<IMSI>,[<NetNameAsc>],<SD>,<ABND>,<T3402>,<T3412>	
    
    				<PLMN> - Country code and operator code(MCC, MNC)
    				<ARFCN> - GSM Assigned Radio Channel
    				<RSSI> - Received Signal Strength Indication
    				<LAC> - Localization Area Code
    				<RAC> - Routing Area Code
    				<TXPWR> - Tx Power
    				<MM> - Mobility Management State (NOT AVAILABLE)
    				<RR> - Radio Resource State (NOT AVAILABLE) <NOM> - Network Operator
    				Mode
    				<CID> - Cell ID
    				<IMSI> - International Mobile Subscriber Identity
    				<NetNameAsc> - Operator name
    				<SD> - Service Domain
    				0 - No Service
    				1 - CS only
    				2 - PS only
    				3 - CS+PS
    				<ABND> - Active Band
    				1 - GSM 850
    				2 - GSM 900
    				3 - DCS 1800
    				4 - PCS 1900
    				*/
    			}
    			if(saveMap.containsKey("mtIMEI")){
    				imei = saveMap.get("mtIMEI").toString();
    				imei = imei.replaceAll("#", "");
    				imei = imei.replaceAll("CGSN", "");
    				imei = imei.replaceAll(":", "");
    				imei = imei.replaceAll(";", "");
    				imei = imei.replaceAll("AT", "");
    				imei = imei.replaceAll(" ", "");
       				imei = imei.replaceAll("OK", "");
       				imei = imei.trim();
       				nlog.setImei(imei);
    			}
    			if(saveMap.containsKey("mtLastConnectionStatus")){
    				lastConnectionStatus = saveMap.get("mtLastConnectionStatus").toString();
    				lastConnectionStatus = lastConnectionStatus.replaceAll("OK", "");
    				lastConnectionStatus = lastConnectionStatus.trim();
    				nlog.setLastConnectionStatus(lastConnectionStatus);
    			}
    		}
    		else{
    			logger.error("["+oidStr+"] is not a valid oid.");
    			return false;
    		}
    	}else{
    		// SNMP TRAP OID가 없음
    		logger.error("saveMap has no SnmpTrapOID item.");
    		return false;
    	}    	
    	
    	//logger.info("### [MAP] {}", saveMap.toString());

    	//이벤트 전송
    	if(trapName == null || sender == null){
    		logger.error("# trapName or Sender is null. Manager can not save the trap event.");
    		return false;
    	}else{
    		/*
    		EventAlertLog event = new EventAlertLog();
            event.setStatus(EventStatus.Open);
            event.setActivatorIp(saveMap.get("activatorIp").toString());
            
			EventUtil.sendEvent("Snmp Trap Event", target, sender,
			        saveMap.get("startTime").toString(),
			        new String[][] {{"message", message},{"trapname", trapName}}, event);
			*/
    		
    		doTrap(nlog);
			
    	}        
        
        return true;
    }
    
    private void doTrap(NetworkInfoLog nlog) throws Exception {

        JpaTransactionManager txmanager = DataUtil.getBean(JpaTransactionManager.class);
        NetworkInfoLogDao nilDao = DataUtil.getBean(NetworkInfoLogDao.class);
        MMIUDao modemDao = DataUtil.getBean(MMIUDao.class);
        
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);

            nilDao.add(nlog);              
            logger.info("save NetworkInfoLog="+nlog.getTargetNode());      
            
            try {           	

                MMIU modem = modemDao.findByCondition("deviceSerial", nlog.getTargetNode());

                if(modem != null) {
    				modem.setLastLinkTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddhhmmss"));
                	modem.setCpuUsage(nlog.getCpuUsage());
    				modem.setMemoryUsage(nlog.getMemoryUsage());
    				modem.setMobileNetworkType(nlog.getMobileNetworkType());
    				modem.setImei(nlog.getImei());
    				modem.setImsi(nlog.getImsi());
    				modem.setCellId(nlog.getCellId());
    				if(nlog.getTotalTxSize() != null && !"".equals(nlog.getTotalTxSize())) {
    					modem.setRfPower(Long.parseLong(nlog.getTotalTxSize()));	
    				}    							
    	            
    	            //if(nlog.getTxPower() != null) {
    	            //	DecimalFormat df = new DecimalFormat("#.00");
        			//	modem.setTxPower(df.format(nlog.getTxPower().doubleValue()));
    	            //}
    	            if(nlog.getRssi() != null) {
    	            	DecimalFormat df = new DecimalFormat("#.00");
        				modem.setTxPower(df.format(nlog.getRssi().doubleValue()));
    	            }
    	            if(nlog.getFrequency() != null) {
    	            	modem.setFrequency(Integer.parseInt(nlog.getFrequency()));
    	            }
    				
    	            if(nlog.getMobileNetworkType() != null && !"".equals(nlog.getMobileNetworkType())) {
    	            	modemDao.update(modem);
    	            }
        			
                    logger.info("update modem="+nlog.getTargetNode());
                   
                }
            }catch(Exception e) {logger.error(e,e);}
            
            txmanager.commit(txstatus);
            
        }catch (Exception e) {
        	logger.error(e,e);
            if (txstatus != null) txmanager.rollback(txstatus);
        }
    }
    

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        logger.debug("MessageSent "+ session.getLocalAddress() +"/"+session.getRemoteAddress()+"/"+ message);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        cause.printStackTrace();
        //logger.error(cause);
        session.closeNow();
        logger.info(cause.getMessage(), cause);
    }
    
    public static String getDate8Byte(byte[] data) throws Exception {
    	String yyyy="";
    	String MM="";
    	String dd="";
    	String hh="";
    	String mm="";
    	String ss="";
    	if(data.length!=8) {
    		throw new Exception("Date's length is invalid.");
    	}else {
    		yyyy = String.valueOf(DataUtil.getIntTo2Byte(DataUtil.select(data, 0, 2)));
    		MM = String.format("%02d", data[2] & 0xFF );
    		dd = String.format("%02d", data[3] & 0xFF);
    		hh = String.format("%02d", data[5] & 0xFF);
    		mm = String.format("%02d", data[6] & 0xFF);    
    		ss = String.format("%02d", data[7] & 0xFF);   
    	}
    	return yyyy + MM + dd + hh + mm + ss;
    }    
    
    
    /*
	*2G 채널범위 *
	 0 ~ 124

	  - 512 ~ 885

	  - 975 ~ 1023

	*3G 채널 범위*

	  - 10562 ~ 10838

	*4G 채널 범위*

	  - 1200 ~ 1949

	  - 2750 ~ 3449

	  - 6150 ~ 6449 
     */	  
	//SP-1017
    public static String getMobileNetworkTypeByFrequency(String freq) {
    	
    	int frequency = 0;
    	
    	if(freq == null || "".equals(freq)) {
    		return "";
    	}
    	
    	try {
    		frequency = Integer.parseInt(freq);
    	}catch(NumberFormatException e) {
    		logger.warn(e, e);
    		return "";
    	}catch(Exception e) {
    		logger.warn(e, e);
    		return "";
    	}
    	
    	if(frequency < 1024) {
    		return "2G";
    	}else if(frequency >= 1200 && frequency < 6500) {
    		return "4G";
    	}else if(frequency >= 10562 && frequency < 10839) {
    		return "3G";
    	}else {
    		return "";
    	}    	
    }
}