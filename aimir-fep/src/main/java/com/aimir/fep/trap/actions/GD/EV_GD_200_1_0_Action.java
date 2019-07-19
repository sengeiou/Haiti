package com.aimir.fep.trap.actions.GD;

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
import com.aimir.dao.device.ChangeLogDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/**
 * Event ID : GD_200.0.0 (GPRS Modem Install)
 * <br>GPRS Modem
 *
 * @author goodjob
 * @version $Rev: 1 $, $Date: 2014-08-28 10:00:00 +0900 $,
 */
@Component
public class EV_GD_200_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_GD_200_1_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MeterDao meterDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    CommandGW commandGW;
    
    @Autowired
    ChangeLogDao clDao;
    
    @Autowired
    DeviceModelDao deviceModelDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    LocationDao locationDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(GPRS Modem Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EventName[eventModemInstall] "+" EventCode[" + trap.getCode()+"] Modem["+trap.getSourceId()+"]");
        String currentTime = TimeUtil.getCurrentTimeMilli();
        String commDate = trap.getTimeStamp();
        String newModemId = event.getEventAttrValue("EUI");
        String installedDate = event.getEventAttrValue("Time");
        
        if(installedDate == null || "".equals(installedDate) ||!installedDate.subSequence(0, 4).equals(TimeUtil.getCurrentTimeMilli().substring(0, 4))){
        	installedDate = currentTime;
        }

        String nameSpace = "";
        if(trap.getCode().indexOf("_") > 0){
        	nameSpace = trap.getCode().substring(0,2);
        }
        int resetInterval = Integer.parseInt(event.getEventAttrValue("ResetInterval"));
        int meteringInterval = Integer.parseInt(event.getEventAttrValue("MeteringInterval"));        
        String serverIp = event.getEventAttrValue("ServerIP");
        String serverPort = event.getEventAttrValue("ServerPort");        
        String apnAddress = event.getEventAttrValue("APNAddress");
        String apnId = event.getEventAttrValue("APNID");
        String apnPassword = event.getEventAttrValue("APNPassword");
        String modelName = event.getEventAttrValue("ModelName");        
        String fwVer = event.getEventAttrValue("FwVer");
        String buildNumber = event.getEventAttrValue("BuildNumber");
        String hwVer = event.getEventAttrValue("HwVer");
        String simNumber = event.getEventAttrValue("SimIMSI");
        String phoneNumber = event.getEventAttrValue("PhoneNumber");
        String meterId = event.getEventAttrValue("MeterID");
        //int commStatus = Integer.parseInt((event.getEventAttrValue("MeterCommStatus") == null) ? "0" : (event.getEventAttrValue("MeterCommStatus")));   

        MeterType meterType = MeterType.EnergyMeter;
        ModemType modemType = ModemType.MMIU;
        
        DeviceModel modemModel = deviceModelDao.findByCondition("name", modelName);
        if(fwVer != null && !"".equals(fwVer)){
        	fwVer = DataUtil.getVersionString(Integer.parseInt(fwVer));
        }
        if(hwVer != null && !"".equals(hwVer)){
        	hwVer = DataUtil.getVersionString(Integer.parseInt(hwVer));
        }
        
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
         // get modem
            Supplier supplier = supplierDao.getAll().get(0);
            
            MMIU modem = (MMIU) modemDao.get(newModemId);
            Meter meter = null;
            if (modem == null) {
                
                modem = new MMIU();                
                modem.setDeviceSerial(newModemId);
                modem.setInstallDate(DateTimeUtil.getDST(supplier.getTimezone().getName(), installedDate));
                modem.setSupplier(supplier);
                modem.setModemType(modemType.name());
                modem.setLocation(locationDao.getAll().get(0));                
                if(modemModel != null){
                    modem.setModel(modemModel); 
                }
                if (phoneNumber != null && !"".equals(phoneNumber))
                    modem.setPhoneNumber(phoneNumber);
                modem.setSimNumber(simNumber);
                modem.setFwRevision(buildNumber);
                modem.setFwVer(fwVer);
                modem.setHwVer(hwVer);
                modem.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), commDate));
                modem.setProtocolVersion(trap.getProtocolVersion());
                if(serverPort.equals("8888")){ //reversegprs port
                	modem.setProtocolType(Protocol.REVERSEGPRS.name());
                }else{
                	modem.setProtocolType(Protocol.SMS.name());
                }
                
                modem.setNameSpace(nameSpace);
                modem.setResetInterval(resetInterval);
                modem.setMeteringInterval(meteringInterval);
                modem.setApnAddress(apnAddress);
                modem.setApnId(apnId);
                modem.setApnPassword(apnPassword);
                modemDao.add(modem);
                log.info("Add modem="+newModemId);
            } else {
                if(modemModel != null){
                    modem.setModel(modemModel); 
                } 
                if (phoneNumber != null && !"".equals(phoneNumber))
                    modem.setPhoneNumber(phoneNumber);
                modem.setSimNumber(simNumber);
                modem.setFwRevision(buildNumber);
                modem.setFwVer(fwVer);
                modem.setHwVer(hwVer);
                modem.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), commDate));
                modem.setProtocolVersion(trap.getProtocolVersion());
                if(serverPort.equals("8888")){ //reversegprs port
                	modem.setProtocolType(Protocol.REVERSEGPRS.name());
                }else{
                	modem.setProtocolType(Protocol.SMS.name());
                }
                modem.setNameSpace(nameSpace);
                modem.setResetInterval(resetInterval);
                modem.setMeteringInterval(meteringInterval);
                modem.setApnAddress(apnAddress);
                modem.setApnId(apnId);
                modem.setApnPassword(apnPassword);
            	modemDao.update(modem);
                log.info("Update modem="+newModemId);
            }
            
            if (meterId != null && !"".equals(meterId)) {

                meter = meterDao.get(meterId);
                
                if (meter != null && !meter.getMdsId().equals(meterId)) {
                    meter.setModem(null);
                }
                
                // 미터의 모뎀과 입력받은 모뎀이 다르면 관계를 생성한다.
                meter = meterDao.get(meterId);
                if (meter != null) {
                	if(meter.getModem() != null){
                        if (!modem.getDeviceSerial().equals(meter.getModem().getDeviceSerial())) {
                            meter.setModem(modem);
                        }
                	}else{
                    	meter.setModem(modem);
                	}

                }
                else {

                    meter = new EnergyMeter();                    
                    meter.setMdsId(meterId);
                    meter.setInstallDate(DateTimeUtil.getDST(supplier.getTimezone().getName(), installedDate));
                    meter.setMeterType(CommonConstants.getMeterTypeByName(meterType.name()));
                    meter.setLocation(locationDao.getAll().get(0));
                    meter.setSupplier(supplier);
                    meter.setModem(modem);
                    meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.NewRegistered.name()));
                    EventUtil.sendEvent("Equipment Registration",
                            TargetClass.valueOf(meterType.name()),
                            meterId,
                            currentTime, new String[][] {},
                            event);
                }                
                
                if(meter != null){

                	if(meter.getModel() == null){
                        DeviceModel meterModel = deviceModelDao.findByCondition("name", "LSIQ-1P");
                        if(meterModel != null){
                            meter.setModel(meterModel);
                        }
                	}

                    meter.setLpInterval(60);
                    meterDao.saveOrUpdate(meter);
                    log.info("Meter Install="+meterId);
                }

            }
            else log.warn("Meter of Modem[" + newModemId + "] is NULL!");

            event.setActivatorId(newModemId);
            event.setActivatorType(TargetClass.Modem);
            event.append(EventUtil.makeEventAlertAttr("modemID",
                                                 "java.lang.String", newModemId));
            event.append(EventUtil.makeEventAlertAttr("message",
                                                 "java.lang.String",
                                                 "Modem is connected with a Meter[" + modelName + "," + meterId + "]"));
            
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
        	log.error(e,e);
        	if (txstatus != null) txmanager.rollback(txstatus);
            throw e;
        }
    }

}
