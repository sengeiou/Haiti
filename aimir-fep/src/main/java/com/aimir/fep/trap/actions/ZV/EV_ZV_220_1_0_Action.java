package com.aimir.fep.trap.actions.ZV;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.ZRU;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.Condition.Restriction;

/**
 * Event ID : ZV_220.1.0 (evtInstallMeter) <br>
 * ZRU Meter
 */
@Component
public class EV_ZV_220_1_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_ZV_220_1_0_Action.class);

	@Autowired
	MCUDao mcuDao;

	@Autowired
	ModemDao modemDao;

	@Autowired
	MeterDao meterDao;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	DeviceModelDao deviceModelDao;

	@Autowired
	LocationDao locationDao;
	
	@Resource(name = "transactionManager")
	JpaTransactionManager txmanager;

	/*
	 * execute event action
	 *
	 * @param trap
	 *            - FMP Trap(Event)
	 * @param event
	 *            - Event Alert Log Data 220.1 evtInstallMeter OID 3 True Major
	 *            Install meter
	 * 
	 * 
	 *            21.1.1 meterId STRING 20 Meter ID
	 *            21.1.2 meterModel STRING 20 Meter model 
	 *            21.1.3 meterVendor STRING 3 Meter Vendor 
	 *            21.1.5 meterPhase BYTE 1 Meter phase (electricity only) 
	 *            31.3.3 moG3NodeKind STRING 20 Modem Node Kind 
	 *            31.3.5 moG3FwVer WORD 2 Modem FW Version (Major , Minor) 
	 *            31.3.6 moG3FwBuild WORD 2 Modem FW Build number 
	 *            31.3.7 moG3HwVer WORD 2 Modem HW Version
	 */
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		log.debug("EventName[evtInstallMeter] " + " EventCode[" + trap.getCode() + "] Modem[" + trap.getSourceId() + "]");

		String ipAddr = trap.getIpAddr();
		String modemSerial = trap.getSourceId();
		String meterId = event.getEventAttrValue("meterId");
		String meterModel = event.getEventAttrValue("meterModel") == null ? "" : event.getEventAttrValue("meterModel");
		String meterVendor = event.getEventAttrValue("meterVendor") == null ? "" : event.getEventAttrValue("meterVendor");
		int meterPhase = Integer.parseInt(event.getEventAttrValue("meterPhase") == null ? "0" : event.getEventAttrValue("meterPhase"));
		String moG3NodeKind = event.getEventAttrValue("moG3NodeKind") == null ? "" : event.getEventAttrValue("moG3FwVer");
		String fwVer = event.getEventAttrValue("moG3FwVer") == null ? "" : event.getEventAttrValue("moG3FwVer");
		String build = event.getEventAttrValue("moG3FwBuild") == null ? "" : event.getEventAttrValue("moG3FwBuild");
		String hwVer = event.getEventAttrValue("moG3HwVer") == null ? "" : event.getEventAttrValue("moG3HwVer");

		if (fwVer != null && !"".equals(fwVer)) {
			fwVer = DataUtil.getVersionString(Integer.parseInt(fwVer));
		}
		if (hwVer != null && !"".equals(hwVer)) {
			hwVer = DataUtil.getVersionString(Integer.parseInt(hwVer));
		}
		
		
		String locationName = FMPProperty.getProperty("loc.default.name","PE9900");
		
		List<Location> locations = locationDao.getLocationByName(locationName);
		Location defaultLocation = null;
		if(locations != null && locations.size() > 0){
			defaultLocation = locations.get(0);
        } else {
        	defaultLocation = locationDao.getAll().get(0);
        }

		log.debug("meterId[" + meterId + "]");
		log.debug("meterModel[" + meterModel + "]");
		//LSKLIR3410DR-100
		log.debug("meterVendor[" + meterVendor + "]");
		log.debug("meterPhase[" + meterPhase + "]");
		log.debug("moG3NodeKind[" + moG3NodeKind + "]");
		log.debug("moG3FwVer[" + fwVer + "]");
		log.debug("moG3FwBuild[" + build + "]");
		log.debug("moG3HwVer[" + hwVer + "]");

		// Log
		StringBuffer logBuf = new StringBuffer();
		logBuf.append("ipAddr[" + ipAddr + "] mcuId[" + trap.getMcuId() + "] meterId[" + meterId + "] meterModel[" + meterModel + "] meterVendor[" + meterVendor + "] meterPhase[" + meterPhase + "] moG3NodeKind[" + moG3NodeKind + "]");
		log.debug(logBuf.toString());

		TransactionStatus txstatus = null;

		try {
			txstatus = txmanager.getTransaction(null);
			/********************************
			 * 모뎀 등록
			 */
			MCU mcu = mcuDao.get(trap.getMcuId());
			
			mcu.setLastCommDate(TimeUtil.getCurrentTime()); // mcu last time
			mcuDao.update(mcu);
			
			//Supplier supplier = supplierDao.getAll().get(0);
			Supplier supplier = mcu.getSupplier();
			DeviceModel modemModel = deviceModelDao.findByCondition("name", "NAMR-P121SR"); // Vietnam ZigBee Modem
			ZRU modem = (ZRU) modemDao.get(modemSerial);
			Meter meter = null;
			boolean isOldModem = true;

			if (modem == null) {
				isOldModem = false;
				modem = new ZRU();
				modem.setLocation(mcu.getLocation()); // modem이 null인 경우만 location 설정
			}

			modem.setSupplier(supplier);
//			modem.setLocation(locationDao.getAll().get(0));
			
			modem.setModemType(ModemType.ZRU.name());
			if (modemModel != null) {
				modem.setModel(modemModel);
			}
			if (mcu != null) {
				modem.setMcu(mcu);
			}

			modem.setFwRevision(build);
			modem.setFwVer(fwVer);
			modem.setHwVer(hwVer);
			modem.setProtocolVersion(trap.getProtocolVersion());
			modem.setProtocolType(Protocol.ZigBee.name());
			modem.setNameSpace("ZV");
			modem.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), trap.getTimeStamp()));

			if (!isOldModem) { // 신규 모뎀 등록
				modem.setDeviceSerial(modemSerial);
				modem.setInstallDate(DateTimeUtil.getDST(supplier.getTimezone().getName(), trap.getTimeStamp()));
				modemDao.add(modem);
				log.info("Add modem=" + modemSerial);

				EventUtil.sendEvent("Equipment Installation", TargetClass.valueOf(ModemType.ZRU.name()), modemSerial, trap.getTimeStamp(), new String[][] {}, event);
			} else {
				modemDao.update(modem);
				log.info("Update modem=" + modemSerial);
				//event.getEventAlert().setName("Equipment Registration");
				//EventUtil.sendEvent("Equipment Registration", TargetClass.valueOf(ModemType.ZRU.name()), modemSerial, trap.getTimeStamp(), new String[][] {}, event);
			}

			/***********************
			 * 미터 등록
			 */
			if (meterId != null && !"".equals(meterId)) {
				
				// meter id - pattern check
				if (!Pattern.matches("ML([0-9]{10,12})", meterId)) {
                	
                	EventUtil.sendEvent("Meter Value Alarm",
                            TargetClass.valueOf(modem.getModemType().name()),
                            modem.getDeviceSerial(),
                            new String[][] {{"message", "WRONG METER_SERIAL[" + meterId + "]"}});
                	
                    throw new Exception("Meter serial[" + meterId + 
                            " pattern[" + "ML([0-9]{10,12})" + "] not matched");
                }
				
//				// meter-modem 관계 체크
//				List<Meter> meterList = meterDao.getMeterByModemId(modem.getDeviceSerial());
//	    	 	for(Meter _meter :  meterList) {
//	    	 		if(!_meter.getMdsId().equals(meterId)) { // 미터아이디가 다른 미터의 모뎀관계 삭제
//		    	 		_meter.setModem(null);
//		    	 		meterDao.update(_meter);
//	    	 		}
//	    	 	}
				
				meter = meterDao.get(meterId);

				if (meter == null) { // 신규 미터 등록
					
					/**
					 * Licence check
					 */
					log.debug("licenceCheck !! " + meterId);
					if (licenceCheck(meterId, modem.getDeviceSerial(), supplier)) {
						log.debug("licenceCheck pass - " + meterId);
						meter = new EnergyMeter();
						meter.setMdsId(meterId);
						meter.setInstallDate(DateTimeUtil.getDST(supplier.getTimezone().getName(), trap.getTimeStamp()));
						meter.setModem(modem);
						meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.NewRegistered.name()));
						
						DeviceModel deviceModel = deviceModelDao.findByCondition("name", meterModel);
						if (deviceModel != null) {
							meter.setModel(deviceModel);
						}

						meter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.EnergyMeter.name()));
						meter.setLocation(defaultLocation); // PE9900
						meter.setSupplier(supplier);
						meter.setLpInterval(30); // OPF-522 Default 30

						meterDao.add(meter);						

						EventUtil.sendEvent("Equipment Installation", TargetClass.valueOf(MeterType.EnergyMeter.name()), meterId, trap.getTimeStamp(), new String[][] {}, event);
					} else {
						return;
					}

				} else { // 이미 등록된 미터
					if (meter.getModem() == null) {
						meter.setModem(modem);
					} else {						
						Modem orgModem = meter.getModem();
						if (!modem.getDeviceSerial().equals(meter.getModem().getDeviceSerial())) { // 모뎀이 바뀐경우 새로등록
							meter.setModem(modem);
							
			                EventUtil.sendEvent("Equipment Replacement",
			                        TargetClass.valueOf(modem.getModemType().name()),
			                        modem.getDeviceSerial(),
			                        new String[][] {{"equipType", modem.getModemType().name()},
			                                        {"oldEquipID", orgModem.getDeviceSerial()},
			                                        {"newEquipID", modem.getDeviceSerial()}
			                        });
						}
					}
					
					DeviceModel deviceModel = deviceModelDao.findByCondition("name", meterModel);
					if (deviceModel != null) {
						meter.setModel(deviceModel);
					}

					meter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.EnergyMeter.name()));
//					meter.setLocation(mcu.getLocation()); // mcu location 정보 변경
					
					if(meter.getSupplierId() == null || !meter.getSupplierId().equals(supplier.getId())) {
						meter.setSupplier(supplier);
					}					
					// meter.setLpInterval(60); 검침정보 업로드 시 업데이트 되므로 제거

					meterDao.update(meter);
					//EventUtil.sendEvent("Equipment Registration", TargetClass.valueOf(MeterType.EnergyMeter.name()), meterId, trap.getTimeStamp(), new String[][] {}, event);
				}

				log.info("Meter Install=" + meterId);

			} else {
				log.warn("Meter of Modem[" + modemSerial + "] is NULL!");
			}
		} catch (Exception e) {
			log.error("evtInstallMeter error - " + e, e);		
		} finally {
			if (txstatus != null) {
				txmanager.commit(txstatus);
			}
		}

		log.debug("evtInstallMeter Action Compelte");
	}

	private boolean licenceCheck(String meterSerial, String modemSerial, Supplier supplier) {
		log.debug("#### meterSerial=" + meterSerial + ", modemSerial=" + modemSerial + ", supplier=" + supplier.getName());
		boolean isPass = true;
		
    	int limitedCount = supplier.getLicenceMeterCount();
    	String eventAlertName = "Excessive Number of Device Registration";
    	String activatorType = TargetClass.EnergyMeter.name();
    	String activatorId = " ";
    	
    	if (supplier.getLicenceUse() == 1) {
    		int currentMeterCount = meterDao.getTotalMeterCount();
    		log.debug("## licenceCheck - LicenceUse=" + supplier.getLicenceUse() + ", currentMeterCoun=" + currentMeterCount);
    		
			if (!(currentMeterCount < limitedCount)) {
				isPass = false;
				EventUtil.sendEvent(eventAlertName, activatorType, activatorId, supplier.getId());
				
				log.warn("##################################################################################");
				log.warn("Excessive Number of Device Registration. (limited quantity : " + limitedCount + ") - Meter=" + meterSerial + ", Modem=" + modemSerial);				
				log.warn("##################################################################################");
			} 
		}
		
		//return supplier.getLicenceUse();
    	return isPass;
    }

}
