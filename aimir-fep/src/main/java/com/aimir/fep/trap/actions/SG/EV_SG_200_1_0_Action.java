package com.aimir.fep.trap.actions.SG;

import java.util.StringTokenizer;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.ChangeLogDao;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.SubGiga;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/**
 * Event ID : SG_200.1.0 (Subgiga Parent Modem Install)
 * <br>Subgiga Modem
 *
 * @author elevas
 * @version $Rev: 1 $, $Date: 2014-08-28 10:00:00 +0900 $,
 */
@Component
public class EV_SG_200_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_SG_200_1_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    EventAlertDao eaDao;
    
    @Autowired
    ModemDao modemDao;
    
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
        
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            String currentTime = TimeUtil.getCurrentTimeMilli();
            String commDate = trap.getTimeStamp();
            String newModemId = event.getEventAttrValue("evtEUI");
            String installedDate = event.getEventAttrValue("evtTime");
            
            if(installedDate == null || "".equals(installedDate) ||!installedDate.subSequence(0, 4).equals(TimeUtil.getCurrentTimeMilli().substring(0, 4))){
            	installedDate = currentTime;
            }
    
            String nameSpace = "";
            if(trap.getCode().indexOf("_") > 0){
            	nameSpace = trap.getCode().substring(0,2);
            }
            
            int resetInterval = Integer.parseInt(event.getEventAttrValue("evtResetInterval"));
            int meteringInterval = Integer.parseInt(event.getEventAttrValue("evtMeteringInterval"));     
            int meteringTimeRange = Integer.parseInt(event.getEventAttrValue("evtMeteringTimeRange"));
            String serverIp = event.getEventAttrValue("evtServerIP");
            String serverPort = event.getEventAttrValue("evtServerPort");        
            String apnAddress = event.getEventAttrValue("evtAPNAddress");
            String apnId = event.getEventAttrValue("evtAPNID");
            String apnPassword = event.getEventAttrValue("evtAPNPassword");
            String modelName = event.getEventAttrValue("evtModelName");        
            String fwVer = event.getEventAttrValue("evtFwVer");
            String buildNumber = event.getEventAttrValue("evtBuildNumber");
            String hwVer = event.getEventAttrValue("evtHwVer");
            String simNumber = event.getEventAttrValue("evtSimIMSI");     
            String phoneNumber = event.getEventAttrValue("evtSimNumberID");   
            String strFrequency = event.getEventAttrValue("evtFrequency");
            String strBandwidth = event.getEventAttrValue("evtBandwidth");
            int panId = Integer.parseInt(event.getEventAttrValue("evtPanID"));
            Long rfPower = Long.parseLong(event.getEventAttrValue("evtRfPower"));
            // int rfPower = Integer.parseInt(event.getEventAttrValue("evtRfPower"));
            //int commStatus = Integer.parseInt((event.getEventAttrValue("evtMeterCommStatus") == null) ? "0" : (event.getEventAttrValue("evtMeterCommStatus")));   
    
            if(fwVer != null && !"".equals(fwVer)){
            	fwVer = DataUtil.getVersionString(Integer.parseInt(fwVer));
            }
            if(hwVer != null && !"".equals(hwVer)){
            	hwVer = DataUtil.getVersionString(Integer.parseInt(hwVer));
            }
            
            ModemType modemType = ModemType.SubGiga;
            
            DeviceModel modemModel = deviceModelDao.findByCondition("name", modelName);

            // 단위가 같이 오기 때문에 정수만 추출한다.
            StringTokenizer st = new StringTokenizer(strFrequency, " ");
            int frequency = Integer.parseInt(st.nextToken());
            st = new StringTokenizer(strBandwidth, " ");
            int bandwidth = Integer.parseInt(st.nextToken());
            
            // get modem
            SubGiga modem = (SubGiga) modemDao.get(newModemId);
            Supplier supplier = supplierDao.getAll().get(0);
            
            if (modem == null) {
                modem = new SubGiga();                
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
                modem.setProtocolType(Protocol.SMS.name());
                modem.setNameSpace(nameSpace);
                modem.setRfPower(rfPower);
                modem.setFrequency(frequency);
                modem.setBandWidth(bandwidth);
                modem.setPanId(panId);
                modem.setResetInterval(resetInterval);
                modem.setMeteringInterval(meteringInterval);
                modem.setMeteringTimeRange(meteringTimeRange);
                modem.setApnAddress(apnAddress);
                modem.setApnId(apnId);
                modem.setApnPassword(apnPassword);
                modem.setIpAddr(serverIp);
                modem.setListenPort(Integer.parseInt(serverPort));
                modemDao.add(modem);
            }else{
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
                modem.setProtocolType(Protocol.SMS.name());
                modem.setNameSpace(nameSpace);
                modem.setRfPower(rfPower);
                modem.setFrequency(frequency);
                modem.setBandWidth(bandwidth);
                modem.setPanId(panId);
                modem.setResetInterval(resetInterval);
                modem.setMeteringInterval(meteringInterval);
                modem.setMeteringTimeRange(meteringTimeRange);
                modem.setApnAddress(apnAddress);
                modem.setApnId(apnId);
                modem.setApnPassword(apnPassword);
                modem.setIpAddr(serverIp);
                modem.setListenPort(Integer.parseInt(serverPort));
            	modemDao.update(modem);
            }
            
            EventAlert ea = eaDao.findByCondition("name", "Equipment Registration");
            if (ea != null)
                event.setEventAlert(ea);
            
            event.setActivatorId(newModemId);
            event.setActivatorType(TargetClass.SubGiga);
            event.append(EventUtil.makeEventAlertAttr("modemID",
                                                 "java.lang.String", newModemId));
        }
        catch (Exception e) {
        	log.error(e,e);
            throw e;
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }

}
