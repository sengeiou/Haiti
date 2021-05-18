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
import com.aimir.dao.device.MCUCodiDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUCodi;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

/**
 * Event ID : 202.6.0 Processing Class
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
@Component
public class EV_202_6_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_202_6_0_Action.class);
    
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    MCUCodiDao mcuCodiDao;
    
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
        log.debug("EV_202_6_0_Action : EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");
        String currentTime = TimeUtil.getCurrentTime();
        TransactionStatus txstatus = null;
        
        try {
            txstatus = txmanager.getTransaction(null);
            
            MCU mcu = mcuDao.get(trap.getMcuId());
            
            if (mcu == null)
            {
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
				
				DeviceModel model = modelDao.findByCondition("name", "NZC I211");
                if (model != null) {
                    mcu.setDeviceModel(model);
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
            
            }
            //Code mcuType = mcu.getMcuType();
    
            // Update MCU Mobile IP        
            //String ipAddr = event.getEventAttrValue("ipAddr");
            String ipAddr = event.getEventAttrValue("ethIpAddr");
            log.debug("Mobile Ipaddr=["+ipAddr+"]");
    
            if (ipAddr != null && !ipAddr.equals("") && !ipAddr.equals("0.0.0.0"))
            {
                mcu.setIpAddr(ipAddr);
            }
            
            mcu.setNetworkStatus(1);
            mcu.setLastCommDate(currentTime);
            processingCodi(trap, event, mcu);
            
           // MOINSTANCE mmp = IUtil.makeDummyMO("MCUMobilePort");
            String mobileId = null;
    
            try
            {
                mobileId = trap.getVarBinds().get("2.104.1").toString();
            }
            catch (Exception e) {}
    
            if (mobileId == null)
            {
                log.debug("no mobile id in event");
                return;
            }
            //MOINSTANCE[] mobiles = IUtil.getMIOLocal().EnumerateAssociationInstances(
            //        "MCUHasMobilePort",mcu.getName(),"hasMobilePort",
            //        "MCUMobilePort");
    
            //if (mobiles == null || mobiles.length == 0)
            //{
            //    log.debug("no mobile associated by "+mcu.getName());
            //    return;
            //}
    
           // for (int i = 0; i < mobiles.length; i++)
            //{
            //    tmpId = mobiles[i].getPropertyValueString("id");
            //    if (mobileId.equals(tmpId))
            //    {
            //        mmp.setName(mobiles[i].getName());
            //    }
            //}
    
            log.debug("mobile Id=["+mobileId+"]");
            //mmp.addProperty(new MOPROPERTY("portState", "1"));
            //mmp.addProperty(new MOPROPERTY("packetIpAddr", ipAddr));
             mcuDao.update(mcu);
            log.debug("Mobile Keepalive Event Action Compelte");
            txmanager.commit(txstatus);
        } catch (Exception e) {
        	if(txstatus != null) 
        		 txmanager.rollback(txstatus);
        	
        	throw e;
		}
    }

    
    private void processingCodi(FMPTrap trap,EventAlertLog event, MCU mcu)
        throws Exception {
        // Update Codi
    	String codiId = null;
        try
        {
        	codiId = trap.getVarBinds().get("3.3.3").toString();
        }catch (Exception e) {}

        if (codiId == null)
        {
            log.debug("no codi id in event");
            return;
        }
        
        MCUCodi mcuCodi = mcuCodiDao.findByCondition("codiId", codiId); //TODO CHECK 

        if (mcuCodi == null)
        {
            log.debug("no codi associated by "+mcu.getSysID());
            return;
        }

        //TODO 집중기와 코디간의 연관관계 설정
        //TODO CHANGE CODI PROPERTY
        /*
        String sinkState = event.getEventAttrValue("portState");
        String sinkNeighborNode = event.getEventAttrValue("neighborNodeCnt");

        log.debug("sinkState=["+sinkState+"], sinkNeighborNode=["+
                sinkNeighborNode+"]");
        if (sinkState != null && !sinkState.equals(""))
        {
            mo.addProperty(new MOPROPERTY("portState", sinkState));
        }

        if (sinkNeighborNode != null && !sinkNeighborNode.equals(""))
        {
            mo.addProperty(new MOPROPERTY("neighborNodeCnt",sinkNeighborNode));
        }
        */
    }
}
