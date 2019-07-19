package com.aimir.fep.trap.actions.PH;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.SNRLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.protocol.fmp.datatype.FMPVariable;
import com.aimir.fep.protocol.fmp.datatype.OPAQUE;
import com.aimir.fep.protocol.fmp.frame.service.entry.modemSPNMSEntry;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUCodi;
import com.aimir.model.device.Modem;
import com.aimir.model.device.SNRLog;
import com.aimir.model.device.SubGiga;
import com.aimir.notification.FMPTrap;
import com.aimir.notification.VarBinds;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

/**
 * Event ID : EV_PH_240.3.0 (DCU NMS Info)
 * 
 * 240.3 This event has modemSPNSMEntry
 *
 * @author elevas
 * @version $Rev: 1 $, $Date: 2016-12-29 10:00:00 +0900 $,
 */
@Component
public class EV_PH_240_3_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_PH_240_3_0_Action.class);

	@Resource(name = "transactionManager")
	JpaTransactionManager txmanager;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	MCUDao mcuDao;

	@Autowired
	ModemDao modemDao;
	
	@Autowired
	SNRLogDao snrLogDao;

	/**
	 * execute event action
	 *
	 * @param trap
	 *            - FMP Trap(Modem Tamper Event)
	 * @param event
	 *            - Event Alert Log Data
	 */
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		String openTime = DateTimeUtil.getCurrentDateTimeByFormat(null);
		log.debug("EV_PH_240_3_0_Action : EventName[evtNMSInfo] " + 
		" EventCode[" + trap.getCode() + "] MCU[" + trap.getMcuId() + 
		"] TargetClass[" + event.getActivatorType() + "] openTime[" + openTime + "]");

		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(
			        new DefaultTransactionDefinition(TransactionDefinition.ISOLATION_READ_UNCOMMITTED));

			MCU mcu = mcuDao.get(trap.getSourceId());
			if (mcu == null) {
				log.debug("no mcu intance exist mcu[" + trap.getMcuId() + "]");
				return;
			}
			log.debug("EV_PH_240_3_0_Action : event[" + event.toString() + "]");

			/*
			 * modemSPNSMEntry is opaqueEntry, it can be one or more.
			 * opaqueEntry must be taken as opaque.
			 */	
			List<modemSPNMSEntry> entries = new ArrayList<modemSPNMSEntry>();
			
	        VarBinds vb = trap.getVarBinds();
	        Iterator<?> iter = vb.keySet().iterator();
	        FMPVariable variable = null;

	        String oid = null;
	        Object obj = null;
	        while(iter.hasNext())
	        {
	            oid = (String)iter.next();
	            obj = vb.get(oid);
	            if (obj instanceof FMPVariable) {
	                variable = (FMPVariable)obj;
	                if (variable != null && variable.getJavaSyntax() != null && !"null".equals(variable.getJavaSyntax())) {

	                    if (variable instanceof OPAQUE) {
	            			OPAQUE mdv = (OPAQUE) variable;	  
	            			modemSPNMSEntry val =(modemSPNMSEntry)mdv.getValue();
	            			entries.add(val);
	                    }
	                }
	            }
	        }
	        
	        for(modemSPNMSEntry value : entries){
	        	
				String moSPId = value.getMoSPId().toHexString();
				String moSPParentNodeId = value.getMoSPParentNodeId().toHexString();

				int moSPRssi = value.getMoSPRssi().getValue();
				if (moSPRssi > 127) {
					moSPRssi = (256 - moSPRssi) * -1;
				}

				int moSPLQI = value.getMoSPLQI().getValue();
				int moSP_Etx = value.getMoSPEtx().getValue();
				String moSPEtx = "0x" + Integer.toHexString(moSP_Etx);
				
				int moSPCpuUsage = value.getMoSPCpuUsage().getValue();
				int moSPMemoryUsage = value.getMoSPMemoryUsage().getValue();
				long moSPTxDataPacketSize = value.getMoSPTxDataPacketSize().getValue();
				int moSPHopCount = value.getMoSPHopCount().getValue();
				
				Modem modem = modemDao.get(moSPId);
				
				if (modem != null) {
					ModemType targetModemType = modem.getModemType();
					
					if(targetModemType != null) {
						if (targetModemType.equals(ModemType.SubGiga)) {
							SubGiga subGigaModem = null;
							
							try {
								subGigaModem = (SubGiga) modem;
								subGigaModem.setMcu(mcu); // 모뎀이 바라보고있는 MCU를 갱신한다.
								
					            if (modem.getMcu() == null || (modem.getMcu() != null && !modem.getMcu().getSysID().equals(mcu.getSysID()))) {
					                
					                try{
					                    SNRLog snrLog = new SNRLog();            
					                    snrLog.setDcuid(mcu.getSysID());
					                    snrLog.setDeviceId(modem.getDeviceSerial());
					                    snrLog.setDeviceType(modem.getModemType().name());
					                    snrLog.setYyyymmdd(trap.getTimeStamp().substring(0,8));
					                    snrLog.setHhmmss(trap.getTimeStamp().substring(8,14));
					                    snrLogDao.add(snrLog);
					                }catch(Exception e){
					                    log.warn(e,e);
					                }
					                
					            }
								
							} catch (Exception e) {
								log.error("DEVICE_SERIAL [" + moSPId + "] MODEM AND MODEM_TYPE is mismatch.");
							}
							
							if (subGigaModem != null) {
								
								if(moSPParentNodeId != null && !"".equals(moSPParentNodeId)) {
									Set<Condition> condition = new HashSet<Condition>();
									condition.add(new Condition("modemType",
											new Object[] { ModemType.SubGiga }, null, Restriction.EQ));
									condition.add(new Condition("deviceSerial",
											new Object[] { moSPParentNodeId }, null, Restriction.EQ));
									List<Modem> modemParent = modemDao.findByConditions(condition);
									if(modemParent != null && !modemParent.isEmpty()) {
										subGigaModem.setModem(modemParent.get(0));
									}else {
										
										MCUCodi mcuc = mcu.getMcuCodi();
										if(mcuc != null && mcuc.getCodiID().equals(moSPParentNodeId)) {
											log.debug("[SKIP] No matching ParentNodeId. but it connected direct DCU");
											subGigaModem.setModem(null);	
											moSPHopCount = 1; //direct connected modem SP-1039
										}
									}

								}else{
									subGigaModem.setModem(null); //patch
									moSPHopCount = 0;  //lost path Isolated modem SP-1039
								}
								
								log.debug("=== DATA LOG (S) ===");
								log.debug("### SubGiga ###");
								log.debug("getMoSPId : " + moSPId);
								log.debug("moSPParentNodeId : " + moSPParentNodeId);
								log.debug("moSPRssi : " + moSPRssi);
								log.debug("moSPLQI : " + moSPLQI);
								log.debug("moSPEtx : " + moSPEtx);
								log.debug("moSPCpuUsage : " + moSPCpuUsage);
								log.debug("moSPMemoryUsage : " + moSPMemoryUsage);
								log.debug("moSPTxDataPacketSize : " + moSPTxDataPacketSize);
								log.debug("moSPHopCount : " + moSPHopCount);
								log.debug("=== DATA LOG (E) ===");
								
								subGigaModem.setRssi(moSPRssi);
								// set - LQI
								subGigaModem.setLqi(moSPLQI);
								// set - Etx
								subGigaModem.setEtx(moSP_Etx);
								// set - CpuUsage
								subGigaModem.setCpuUsage(moSPCpuUsage);
								// set - MemoryUsage
								subGigaModem.setMemoryUsage(moSPMemoryUsage);
								// set - Hop count
								subGigaModem.setRfPower(moSPTxDataPacketSize);
								subGigaModem.setHopsToBaseStation(moSPHopCount);
								modemDao.update(subGigaModem);

							}
						}
					}
				}
	        }

		} catch (Exception ex) {
			log.error(ex, ex);
		} finally {
			if (txstatus != null)
				txmanager.commit(txstatus);
		}

		log.debug("Modem Tamper Action Compelte");
	}

}
