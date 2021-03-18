package com.aimir.fep.trap.actions;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

/**
 * Event ID : 201.4.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_202_4_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_202_4_0_Action.class);

    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
	SupplierDao supplierDao;

	@Autowired
	LocationDao locationDao;
	
	@Autowired
    DeviceModelDao modelDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Alert Log Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EV_202_4_0_Action : EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");
        String currentTime = TimeUtil.getCurrentTime();
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            MCU mcu = mcuDao.get(trap.getMcuId());
            
            if (mcu == null)
            {
            	mcu = new MCU();
            	mcu.setSysID(trap.getMcuId());
            	log.info("mcu[" + mcu.getSysID() + "] is not existed!!");

				/*
				 * DCU  신규 등록
				 */
				//Set Default Location            
				String defaultLocName = FMPProperty.getProperty("loc.default.name");
				if (defaultLocName != null && !"".equals(defaultLocName)) {
					if (locationDao.getLocationByName(StringUtil.toDB(defaultLocName)) != null && locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).size() > 0) {
						log.info("MCU[" + mcu.getSysID() + "] Set Default Location[" + locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).get(0).getId() + "]");
						mcu.setLocation(locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).get(0));
					} else {
						log.info("MCU[" + mcu.getSysID() + "] Default Location[" + defaultLocName + "] is Not Exist In DB, Set First Location[" + locationDao.getAll().get(0).getId() + "]");
						mcu.setLocation(locationDao.getAll().get(0));
					}
				} else {
					log.info("MCU[" + mcu.getSysID() + "] Default Location is Not Exist In Properties, Set First Location[" + locationDao.getAll().get(0).getId() + "]");
					mcu.setLocation(locationDao.getAll().get(0));
				}

				//Set Default Supplier
				String supplierName = new String(FMPProperty.getProperty("default.supplier.name").getBytes("8859_1"), "UTF-8");
				log.debug("Supplier Name[" + supplierName + "]");
				Supplier supplier = supplierName != null ? supplierDao.getSupplierByName(supplierName) : null;

				if (supplier != null && supplier.getId() != null && mcu.getSupplier() == null) {
					mcu.setSupplier(supplier);
				} else {
					log.info("MCU[" + mcu.getSysID() + "] Default Supplier is Not Exist In Properties, Set First Supplier[" + supplierDao.getAll().get(0).getId() + "]");
					mcu.setSupplier(supplierDao.getAll().get(0));
				}
				
				DeviceModel model = modelDao.findByCondition("name", "DCU-DUMMY");
                if (model != null) {
                    mcu.setDeviceModel(model);
                }

                log.debug("Event["+event+"]");
   	            String ipaddr = event.getEventAttrValue("ethIpAddr");
   	            log.debug("ipaddr ["+ipaddr+"]");
   	            
   	            if (ipaddr != null && !ipaddr.equals("") && !ipaddr.equals("0.0.0.0"))
   	            {
   	            	mcu.setIpAddr(ipaddr);
   	            }
   	            
				mcu.setInstallDate(currentTime);
				mcu.setLastCommDate(currentTime);
				mcu.setNetworkStatus(1);
				mcu.setMcuType(CommonConstants.getMcuTypeByName(McuType.DCU.name()));
				mcu.setSysLocalPort(new Integer(8000));
//				mcu.setNameSpace(FMPProperty.getProperty("default.namespace.dcu", "EDH"));
				mcu.setProtocolVersion("0102");
				mcu.setSysHwVersion("1.0");
				mcu.setSysSwVersion("1.0");
				mcuDao.add(mcu);

				/*
				 * DCU Registration Event save
				 */
				try {
					EventAlertLog eventAlertLog = new EventAlertLog();
					eventAlertLog.setStatus(EventStatus.Open);
					eventAlertLog.setOpenTime(event.getOpenTime());
					eventAlertLog.setLocation(event.getLocation());
					eventAlertLog.setSupplier(event.getSupplier());
					log.debug("########## DCU[" + mcu.getSysID() + "] Equipment Registration start ##########");
					EventUtil.sendEvent("Equipment Registration", TargetClass.DCU, mcu.getSysID(), currentTime, new String[][] {}, eventAlertLog);
					log.debug("########## DCU[" + mcu.getSysID() + "] Equipment Registration stop  ##########");
				} catch (Exception e) {
					log.error("Equipment Registration save error - " + e.getMessage(), e);
				}
            } else {
            	String ipaddr = event.getEventAttrValue("ethIpAddr");
   	            log.debug("ipaddr ["+ipaddr+"]");
   	            
   	            mcu.setLastCommDate(currentTime);
   	            if (ipaddr != null && !ipaddr.equals("") && !ipaddr.equals("0.0.0.0"))
	            {
   	            	if (mcu.getIpAddr() == null || !mcu.getIpAddr().equals(ipaddr))
   	            		mcu.setIpAddr(ipaddr);
	            }
   	            
   	            mcuDao.update(mcu);
            }
            
            /*
            log.debug("Event["+event+"]");
            String ipaddr = event.getEventAttrValue("ethIpAddr");
            if (ipaddr != null && !ipaddr.equals("") && !ipaddr.equals("0.0.0.0"))
            {
                if (mcu.getIpAddr() != null && !mcu.getIpAddr().equals(ipaddr))
                    mcu.setIpAddr(ipaddr);
                
                // mcuDao.update(mcu);
            }
            */
    
            txmanager.commit(txstatus);
            log.debug("EV_202_4_0_Action Compelte");
        }catch(Exception e) {
        	log.error(e,e);
        	txmanager.rollback(txstatus);
        }
    }
}
