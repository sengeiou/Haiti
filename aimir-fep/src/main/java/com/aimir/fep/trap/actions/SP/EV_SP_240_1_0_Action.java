package com.aimir.fep.trap.actions.SP;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.device.MCUCodiDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.SNRLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUCodi;
import com.aimir.model.device.Modem;
import com.aimir.model.device.SNRLog;
import com.aimir.model.device.SubGiga;
import com.aimir.model.system.DeviceModel;
import com.aimir.notification.FMPTrap;
import com.aimir.util.IPUtil;
/**
 * Event ID : 240.1.0 evtInstallModem
 *
 * @author Elevas Park
 * @version $Rev: 1 $, $Date: 2016-05-24 15:59:15 +0900 $,
 */
@Service
public class EV_SP_240_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_SP_240_1_0_Action.class);
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MCUCodiDao mcucodiDao;

    @Autowired
    SNRLogDao snrLogDao;
    
    @Autowired
    DeviceModelDao deviceModelDao;
    
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        // Initialize
        String mcuId = trap.getMcuId();
        String ipAddr = IPUtil.format(trap.getIpAddr());
        MCU mcu = mcuDao.get(mcuId);

        log.debug("IP[" + ipAddr + "]");
        String modemId = event.getEventAttrValue("moSPId");
        log.debug("MODEM_ID[" + modemId + "]");
        int resetTime = Integer.parseInt(event.getEventAttrValue("moSPResetTime"));
        log.debug("MODEM_RESET_TIME[" + resetTime + "]");
        String nodeKind = event.getEventAttrValue("moSPNodeKind");
        log.debug("NODE_KIND[" + nodeKind + "]");
        String fwVer =  Hex.decode(DataUtil.get2ByteToInt(Integer.parseInt(event.getEventAttrValue("moSPFwVer"))));
        fwVer = Double.parseDouble(fwVer.substring(0, 2) + "." + fwVer.substring(2, 4)) + "";
        log.debug("FW_VER[" + fwVer + "]");
        String fwBuild = event.getEventAttrValue("moSPFwBuild");
        log.debug("FW_BUILD[" + fwBuild + "]");
        String hwVer = Hex.decode(DataUtil.get2ByteToInt(Integer.parseInt(event.getEventAttrValue("moSPHwVer"))));
        hwVer = Double.parseDouble(hwVer.substring(0, 2) + "." + hwVer.substring(2, 4)) + "";
        log.debug("HW_VER[" + hwVer + "]");
        boolean status = Boolean.parseBoolean(event.getEventAttrValue("moSPStatus"));
        log.debug("STATUS[" + status + "]");
        boolean mode = Boolean.parseBoolean(event.getEventAttrValue("moSPMode"));
        log.debug("MODE[" + mode + "]");
        String lastOnLine = event.getEventAttrValue("moSPLastOnLine");
        log.debug("LAST_ON_LINE[" + lastOnLine + "]");
        String lastOffLine = event.getEventAttrValue("moSPLastOffLine");
        log.debug("LAST_OFF_LINE[" + lastOffLine + "]");
        String installDate = event.getEventAttrValue("moSPInstall");
        log.debug("INSTALL_DATE[" + installDate + "]");
        String bootLoaderVer = event.getEventAttrValue("moSPBootLoaderVer");
        if (bootLoaderVer != null && !"".equals(bootLoaderVer)) {
		    bootLoaderVer = Hex.decode(DataUtil.get2ByteToInt(Integer.parseInt(bootLoaderVer)));
            bootLoaderVer = Double.parseDouble(bootLoaderVer.substring(0, 2) + "." + bootLoaderVer.substring(2, 4)) + "";
        	log.debug("BOOTLOADER_VER[" + bootLoaderVer + "]");
        }
        
		JpaTransactionManager txManager = null;      
        TransactionStatus txStatus = null;
        try {
        	// SP-766 (NAMR-P214SR)
        	if ("NAMR-P214SR".equals(nodeKind)){
				txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
				txStatus = txManager.getTransaction(null);
				
		        // if not mcu, it's is created and installed.
		        Modem modem = modemDao.get(modemId);
		        MCU oldMcu = null;
		        if (modem == null) {
		            /*
		            try {
		                EventUtil.sendEvent("Equipment Registration",
		                                    TargetClass.MCU,
		                                    mcuId,
		                                    trap.getTimeStamp(),
		                                    new String[][] {},
		                                    event                
		                );                
		            }catch(Exception e) {
		                log.error("can't send event["+e.getMessage()+"]",e);
		            }
		            */
		            modem = new SubGiga();
	
		            modem.setDeviceSerial(modemId);
		            modem.setModemType(ModemType.SubGiga.name());
		            modem.setSupplier(mcu.getSupplier());
		            modem.setLocation(mcu.getLocation());
		            modem.setInstallDate(installDate);
		            modem.setLastLinkTime(trap.getTimeStamp());
		            modem.setMcu(mcu);
		            modem.setFwVer(fwVer);
		            modem.setFwRevision(fwBuild);
		            modem.setSwVer(fwVer);
		            modem.setHwVer(hwVer);
		            modem.setProtocolType(Protocol.IP.name());
		            modem.setNameSpace("SP");
		            modem.setProtocolVersion("0102");
		            if (bootLoaderVer != null && !"".equals(bootLoaderVer)) {
		            	modem.setBootLoaderVer(bootLoaderVer);
		            }
		            
		            List<DeviceModel> models = deviceModelDao.getDeviceModelByName(mcu.getSupplierId(), "NAMR-P214SR");//SP-862
		            if ( (models != null) && (models.size()) == 1)
		                modem.setModel(models.get(0));
		            
		            if (mcu.getMacAddr() != null && !"".equals(mcu.getMacAddr())) {
		                ((SubGiga)modem).setIpv6Address(Util.getIPv6(mcu.getIpv6Addr(), modemId));
		            }
		            modemDao.add(modem);
		        }else{
		            modem.setSupplier(mcu.getSupplier());
		            modem.setLocation(mcu.getLocation());
		            if (modem.getInstallDate() == null)
		                modem.setInstallDate(installDate);
		            modem.setLastLinkTime(trap.getTimeStamp());
		            modem.setFwVer(fwVer);
		            modem.setSwVer(fwVer);
		            modem.setFwRevision(fwBuild);
		            modem.setHwVer(hwVer);
		            modem.setNameSpace("SP");
		            modem.setProtocolVersion("0102");
		            if (bootLoaderVer != null && !"".equals(bootLoaderVer)) {
		            	modem.setBootLoaderVer(bootLoaderVer);
		            }
		            
		            List<DeviceModel> models = deviceModelDao.getDeviceModelByName(mcu.getSupplierId(), "NAMR-P214SR");//SP-862
		            if ( (models != null) && (models.size() == 1))
		                modem.setModel(models.get(0));
		            
		            if (mcu.getMacAddr() != null && !"".equals(mcu.getMacAddr())) {
		                ((SubGiga)modem).setIpv6Address(Util.getIPv6(mcu.getIpv6Addr(), modemId));
		            }
		            
		            if (modem.getMcu() == null || (modem.getMcu() != null && !modem.getMcu().getSysID().equals(mcu.getSysID()))) {
		                oldMcu = modem.getMcu();
	
		                /*
		                try{
		                	String startDate = "";
		        	        Calendar calendar = Calendar.getInstance();
		        	        calendar.setTime(new Date());
		        	        calendar.add(Calendar.HOUR, (24*-1));	        
		        	        Date before24Date = calendar.getTime();
		        	        startDate = DateTimeUtil.getDateString(before24Date);
		        	        
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				            Calendar fromCal = Calendar.getInstance();
							fromCal.setTime(sdf.parse(startDate));
							Calendar toCal = Calendar.getInstance();
							toCal = (Calendar) fromCal.clone();
							toCal.add(Calendar.DAY_OF_MONTH, +1);
							String toDate = sdf.format(toCal.getTime());
							
							Set<Meter> meters = modem.getMeter();
							if(modem.getMcu() != null && meters != null && meters.size() > 0){
								Meter meter = meters.iterator().next();
						        CommandGW gw = new CommandGW();
						        gw.cmdGetMeteringData(mcuId, meter.getMdsId(), modemId, "", startDate, toDate, null);
							}
					        
		                }catch(Exception e){
		                	log.warn(e,e);
		                }
		                */
		                
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
		                
		                modem.setMcu(mcu);
		                
		            }
		            
		            modemDao.update(modem);
		            
		            event.setActivatorIp(modem.getDeviceSerial());
		            // event.setActivatorType(TargetClass.Modem);
		            event.setSupplier(modem.getSupplier());
		            event.setLocation(modem.getLocation());
		        }
        	}else if ("NCB-S201".equals(nodeKind)){
        		// SP-766 (NCB-S201)
                try{
	        		MCUCodi mcuc = mcu.getMcuCodi();
	        		if (mcuc == null){
	        			mcuc = new MCUCodi();
	        			mcuc.setMcu(mcu);
	        		}
	    			mcuc.setCodiID(modemId);
	    			mcuc.setCodiFwVer(fwVer);
	    			mcuc.setCodiHwVer(hwVer);
	    			mcuc.setCodiFwBuild(fwBuild);

	        		mcu.setMcuCodi(mcuc);
	        		mcuDao.update_requires_new(mcu);
                }catch(Exception e){
                    log.warn(e,e);
                }
        	}
        } catch (Exception e) {
			log.error(e, e);
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
		}
        finally {
            if (txStatus != null) txManager.commit(txStatus);
        }
        
        // oldMcu가 null이 아니면 삭제 명령을 보낸다.
    
        log.debug("Modem Install Event Action Compelte");
    }
}
