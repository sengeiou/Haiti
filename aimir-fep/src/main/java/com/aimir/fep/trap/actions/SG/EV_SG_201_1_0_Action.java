package com.aimir.fep.trap.actions.SG;

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
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.Modem;
import com.aimir.model.device.SubGiga;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;

/**
 * Event ID : SG_201.1.0 (Subgiga Child Information)
 * <br>Subgiga Modem
 *
 * @author elevas
 * @version $Rev: 1 $, $Date: 2014-08-28 10:00:00 +0900 $,
 */
@Component
public class EV_SG_201_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_SG_201_1_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    ModemDao modemDao;
    
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
        log.debug("EventName[eventChildInformation] "+" EventCode[" + trap.getCode()+"] Modem["+trap.getSourceId()+"]");
        
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            String commDate = trap.getTimeStamp();
            String nameSpace = "";
            if(trap.getCode().indexOf("_") > 0){
                nameSpace = trap.getCode().substring(0,2);
            }
            
            SubGiga parentModem = (SubGiga)modemDao.get(trap.getSourceId());
            
            if (parentModem == null) {
                Supplier supplier = supplierDao.getAll().get(0);
                
                parentModem = new SubGiga();                
                parentModem.setDeviceSerial(trap.getSourceId());
                parentModem.setInstallDate(DateTimeUtil.getDST(supplier.getTimezone().getName(), commDate));
                parentModem.setSupplier(supplier);
                parentModem.setModemType(ModemType.SubGiga.name());
                parentModem.setLocation(locationDao.getAll().get(0));   
                parentModem.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), commDate));
                parentModem.setProtocolVersion(trap.getProtocolVersion());
                parentModem.setProtocolType(Protocol.SMS.name());
                parentModem.setNameSpace(nameSpace);
                modemDao.add(parentModem);
            }
            
            byte[] src = Hex.encode(event.getEventAttrValue("evtChildInfoTable.hex"));
            byte[] bx = new byte[2];
            int pos = 0;
            System.arraycopy(src, pos, bx, 0, bx.length);
            pos += bx.length;
            DataUtil.convertEndian(bx);
            
            int count = DataUtil.getIntTo2Byte(bx);
            String newModemId = "";
            for (int i = 0; i < count; i++) {
                if (i != 0)
                    newModemId += ",";
                newModemId += validateModem(src, pos, nameSpace, commDate, trap.getProtocolVersion(), parentModem);
                pos += 42;
            }
                
            event.setActivatorId(trap.getSourceId());
            event.setActivatorType(TargetClass.SubGiga);
            event.append(EventUtil.makeEventAlertAttr("modemID",
                                         "java.lang.String", newModemId));
            event.append(EventUtil.makeEventAlertAttr("message",
                    "java.lang.String",
                    "Child Information "));
        }
        finally {
            if (txstatus != null) txmanager.commit(txstatus);
        }
    }

    private String validateModem(byte[] src, int pos, String nameSpace, 
            String commDate, String protocolVer, Modem p)
    {
        // index
        byte[] bx = new byte[2];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        DataUtil.convertEndian(bx);
        int idx = DataUtil.getIntTo2Byte(bx);
        log.debug("IDX[" + idx + "]");
        
        // EUI
        bx = new byte[8];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        String newModemId = Hex.decode(bx);
        log.debug("EUI[" + newModemId + "]");
        
        // reset interval
        bx = new byte[2];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        DataUtil.convertEndian(bx);
        int resetInterval = DataUtil.getIntTo2Byte(bx);
        log.debug("RESET_INTERVAL[" + resetInterval + "]");
        
        // metering interval
        bx = new byte[2];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        DataUtil.convertEndian(bx);
        int meteringInterval = DataUtil.getIntTo2Byte(bx);
        log.debug("METERING_INTERVAL[" + meteringInterval + "]");
        
        // time range
        bx = new byte[1];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        int meteringTimeRange = DataUtil.getIntToBytes(bx);
        log.debug("METERING_TIMERANGE[" + meteringTimeRange + "]");
        
        // model name
        bx = new byte[20];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        String modelName = new String(bx).trim();
        log.debug("MODEL[" + modelName + "]");
        
        // fw version
        bx = new byte[2];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        DataUtil.convertEndian(bx);
        String fwVer = (int)bx[0] + "." + (int)bx[1];
        log.debug("FW_VER[" + fwVer + "]");
        
        // build number
        bx = new byte[2];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        DataUtil.convertEndian(bx);
        String buildNumber = Integer.toString(DataUtil.getIntTo2Byte(bx));
        log.debug("BUILD_NUMBER[" + buildNumber + "]");
        
        // hw version
        bx = new byte[2];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        DataUtil.convertEndian(bx);
        String hwVer = (int)bx[0] + "." + (int)bx[1];
        log.debug("HW_VER[" + hwVer + "]");
        
        // meter  type
        bx = new byte[1];
        System.arraycopy(src, pos, bx, 0, bx.length);
        pos += bx.length;
        int meterType = DataUtil.getIntToBytes(bx);
        log.debug("METER_TYPE[" + meterType + "]");
        
        ModemType modemType = ModemType.SubGiga;
        
        DeviceModel modemModel = deviceModelDao.findByCondition("name", modelName);

        try {
            // get modem
            SubGiga modem = (SubGiga) modemDao.get(newModemId);
            Supplier supplier = supplierDao.getAll().get(0);
            
            if (modem == null) {
                modem = new SubGiga();                
                modem.setDeviceSerial(newModemId);
                modem.setInstallDate(DateTimeUtil.getDST(supplier.getTimezone().getName(), commDate));
                modem.setSupplier(supplier);
                modem.setModemType(modemType.name());
                modem.setLocation(locationDao.getAll().get(0));   
                if(modemModel != null){
                    modem.setModel(modemModel); 
                }
                modem.setFwRevision(buildNumber);
                modem.setFwVer(fwVer);
                modem.setHwVer(hwVer);
                modem.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), commDate));
                modem.setProtocolVersion(protocolVer);
                modem.setProtocolType(Protocol.SMS.name());
                modem.setNameSpace(nameSpace);
                modem.setResetInterval(resetInterval);
                modem.setMeteringInterval(meteringInterval);
                modem.setMeteringTimeRange(meteringTimeRange);
                modem.setModem(p);
                modemDao.add(modem);
            }else{
                if(modemModel != null){
                    modem.setModel(modemModel); 
                } 
                modem.setFwRevision(buildNumber);
                modem.setFwVer(fwVer);
                modem.setHwVer(hwVer);
                modem.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), commDate));
                modem.setProtocolVersion(protocolVer);
                modem.setProtocolType(Protocol.SMS.name());
                modem.setNameSpace(nameSpace);
                modem.setResetInterval(resetInterval);
                modem.setMeteringInterval(meteringInterval);
                modem.setMeteringTimeRange(meteringTimeRange);
                modem.setModem(p);
                modemDao.update(modem);
            }
        }
        catch (Exception e) {
            log.error(e, e);
        }
        
        return newModemId;
    }
}
