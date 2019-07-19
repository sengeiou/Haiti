package com.aimir.fep.trap.actions.SP;

import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MCUVarDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUVar;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.IPUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

/**
 * Event ID : 200.1.0 eventDcuInstall
 *
 * @author Elevas Park
 * @version $Rev: 1 $, $Date: 2016-05-23 15:59:15 +0900 $,
 */
@Service
public class EV_SP_200_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_SP_200_1_0_Action.class);
    
    enum SYS_TYPE_1st {
        WAN_IPv4(0x80),
        WAN_IPv6(0x40),
        LTE(0x02),
        Mobile_2G(0x01);
        
        private int code;
        
        SYS_TYPE_1st(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
    }
    
    enum SYS_TYPE_2nd {
        SubGiga_6LoWPAN(0x01);
        
        private int code;
        
        SYS_TYPE_2nd(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
    }
    
    enum SYS_TYPE_3rd {
        ZB_SEPv1(0x80),
        ZB_SEPv2(0x40),
        ZB_AIMIR(0x20),
        RS485(0x08),
        BB_PLC_KS_X_4600_1(0x04),
        NB_Prime_PLC(0x02),
        NB_G3_PLC(0x01);
        
        private int code;
        
        SYS_TYPE_3rd(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
    }
    
    enum SYS_TYPE_4th {
        Rural(0x02),
        Urban(0x00),
        Indoor(0x01),
        Outdoor(0x00);
        
        private int code;
        
        SYS_TYPE_4th(int code) {
            this.code = code;
        }
        
        public int getCode() {
            return this.code;
        }
    }
    
    @Autowired
    MCUDao mcuDao;    
    
    @Autowired
    MCUVarDao mcuVarDao;    
    
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    DeviceModelDao modelDao;
    
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

        MCU mcu = null;
        // Initialize
        String mcuId = trap.getMcuId();
        String ipAddr = IPUtil.format(trap.getIpAddr());

        log.debug("IP[" + ipAddr + "]");
        String sysLocation = event.getEventAttrValue("sysLocation");
        log.debug("SYS_Location[" + sysLocation + "]");
        String sysContact = event.getEventAttrValue("sysContact");
        log.debug("SYS_Contact[" + sysContact + "]");
        String sysModel = event.getEventAttrValue("sysModel");
        log.debug("SYS_Model[" + sysModel + "]");
        String fwBuild = event.getEventAttrValue("sysSwBuild");
        log.debug("FW_BUILD[" + fwBuild + "]");
        
        String sysHwVersion = Hex.decode(DataUtil.get2ByteToInt(Integer.parseInt(event.getEventAttrValue("sysHwVersion"))));
        sysHwVersion = Double.parseDouble(sysHwVersion.substring(0, 2) + "." + sysHwVersion.substring(2, 4)) + "";
        log.debug("SYS_HW_VERSION[" + sysHwVersion + "]");
        String sysHwBuild = event.getEventAttrValue("sysHwBuild");
        log.debug("SYS_HW_BUILD[" + sysHwBuild + "]");
        String sysSwVersion = Hex.decode(DataUtil.get2ByteToInt(Integer.parseInt(event.getEventAttrValue("sysSwVersion"))));
        sysSwVersion = Double.parseDouble(sysSwVersion.substring(0, 2) + "." + sysSwVersion.substring(2, 4)) + "";
        log.debug("SYS_SW_VERSION[" + sysSwVersion + "]");
        String sysSwRevision = event.getEventAttrValue("sysSwRevision");
        log.debug("SYS_SW_REVISION[" + sysSwRevision + "]");
        String sysPort = event.getEventAttrValue("sysPort");
        log.debug("SYS_PORT[" + sysPort + "]");
        String sysSerialNumber = event.getEventAttrValue("sysSerialNumber");
        log.debug("SYS_SERIALNUMBER[" + sysSerialNumber + "]");
        String secTLSPort = event.getEventAttrValue("secTLSPort");
        log.debug("SEC_TLS_PORT[" + secTLSPort + "]");
        String secTLSVersion = event.getEventAttrValue("secTLSVersion");
        if (secTLSVersion != null && !"".equals(secTLSVersion)) {
            secTLSVersion = Hex.decode(new byte[]{DataUtil.getByteToInt(Integer.parseInt(secTLSVersion))});
            secTLSVersion = Double.parseDouble(secTLSVersion.substring(0, 1) + "." + secTLSVersion.substring(1, 2)) + "";
        }
        
        log.debug("SEC_TLS_Version[" + secTLSVersion + "]");
        String macAddr = event.getEventAttrValue("ntwEthPhy");
        if (macAddr != null && !"".equals(macAddr)) {
            macAddr = macAddr.replaceAll(":", "");
            
            if (macAddr.length() == 12) {
                macAddr = macAddr.toUpperCase();
                macAddr = macAddr.substring(0, 2) + "-" + macAddr.substring(2, 4) + "-" + macAddr.substring(4, 6) + "-" +
                          macAddr.substring(6, 8) + "-" + macAddr.substring(8, 10) + "-" + macAddr.substring(10, 12);
            }
        }
        
        log.debug("MAC_ADDR[" + macAddr + "]");
        // public ipv4
        String publicIpV4 = event.getEventAttrValue("stringEntry");
        String publicIpV6 = event.getEventAttrValue("stringEntry.16");
        
        String privateIpV4 = event.getEventAttrValue("stringEntry.17");
        String privateIpV6 = event.getEventAttrValue("stringEntry.18");
        log.debug("IPv4[" + privateIpV4 + "] IPv6[" + privateIpV6 + "]");
        if (privateIpV6 == null || "".equals(privateIpV6))
            privateIpV6 = ipAddr;
        
        log.debug("WAN_IP_ADDRv4[" + publicIpV4 + "] WAN_IP_ADDRv6[" + publicIpV6 + "]");
        
        byte[] sysType = DataUtil.get4ByteToInt(Long.parseLong(event.getEventAttrValue("sysType")));
        for (SYS_TYPE_1st s : SYS_TYPE_1st.values()) {
            if ((s.getCode() & sysType[0]) != 0) log.debug("SYS_TYPE_1st[" + s + "]");
        }
        for (SYS_TYPE_2nd s : SYS_TYPE_2nd.values()) {
            if ((s.getCode() & sysType[1]) != 0) log.debug("SYS_TYPE_2nd[" + s + "]");
        }
        for (SYS_TYPE_3rd s : SYS_TYPE_3rd.values()) {
            if ((s.getCode() & sysType[2]) != 0) log.debug("SYS_TYPE_3rd[" + s + "]");
        }
        
        McuType mcuTypeEnum = McuType.Indoor;
        
        for (SYS_TYPE_4th s : SYS_TYPE_4th.values()) {
            if ((s.getCode() & sysType[3]) != 0) {
                log.debug("SYS_TYPE_4th[" + s + "]");
                if (s == SYS_TYPE_4th.Outdoor) mcuTypeEnum = McuType.Outdoor;
            }
        }
        
        String sysName = event.getEventAttrValue("sysName");

        /*
         * 2016.09.18 added
         */
        String protocolType = Protocol.LAN.name();
        String _ntwType = event.getEventAttrValue("ntwType");
        if (_ntwType != null && !"".equals(_ntwType)) {
            byte ntwType = Byte.parseByte(_ntwType);
            if (ntwType == 0x03)
                protocolType = Protocol.GPRS.name();
        }

        // make Target
        Hashtable<String, String> target = new Hashtable<String, String>();
        target.put("id",mcuId);
        target.put("protocolType",protocolType);
        target.put("listenPort",sysPort);
        target.put("ipAddr",ipAddr);

        if (event.getActivatorType() != TargetClass.DCU) {
            log.warn("Activator Type is not DCU");
            return;
        }
        
        mcu = new MCU();

        mcu.setSysID(mcuId);
        mcu.setMcuType(CommonConstants.getMcuTypeByName(mcuTypeEnum.name()));
        mcu.setNetworkStatus(1);
        mcu.setSysName(sysName);
        mcu.setInstallDate(TimeUtil.getCurrentTime());
        mcu.setLastCommDate(TimeUtil.getCurrentTime());
        mcu.setSysLocalPort(Integer.parseInt(sysPort));
        mcu.setProtocolType(CommonConstants.getProtocolByName(protocolType+""));
        mcu.setSysHwVersion(sysHwVersion);
        mcu.setSysHwBuild(sysHwBuild);
        mcu.setSysSwVersion(sysSwVersion);
        mcu.setSysSwRevision(sysSwRevision);
        mcu.setSysContact(sysContact);
        mcu.setSysTlsPort(Integer.parseInt(secTLSPort));
        mcu.setSysTlsVersion(secTLSVersion);
        mcu.setSysContact(sysContact);
        mcu.setSysSerialNumber(sysSerialNumber);
        mcu.setNameSpace("SP");
        mcu.setMacAddr(macAddr);
        mcu.setAmiNetworkAddress(IPUtil.format(publicIpV4));
        mcu.setAmiNetworkAddressV6(IPUtil.format(publicIpV6));
        mcu.setIpAddr(IPUtil.format(privateIpV4.toUpperCase()));
        mcu.setIpv6Addr(IPUtil.format(privateIpV6.toUpperCase()));
        
        if (mcu.getMacAddr() != null && !"".equals(mcu.getMacAddr())) {
            mcu.setMacAddr(mcu.getMacAddr().replaceAll(":", ""));
            //mcu.setIpv6Addr(Util.getIPv6(DeviceType.MCU, mcu.getMacAddr(), null));
        }
        
		JpaTransactionManager txManager = null;      
        TransactionStatus txStatus = null;
        try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);
            // if not mcu, it's is created and installed.
            MCU existMcu = mcuDao.get(mcu.getSysID());        
            if (existMcu == null) {
                log.info("mcu["+mcu.getSysID()+"] is not existed!!");
                //Set Default Location            
                String defaultLocName = FMPProperty.getProperty("loc.default.name");            
                if(defaultLocName != null && !"".equals(defaultLocName)){               
                    if(locationDao.getLocationByName(StringUtil.toDB(defaultLocName))!=null && locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).size()>0) {
                        log.info("MCU["+mcu.getSysID()+"] Set Default Location["+locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).get(0).getId()+"]");
                        mcu.setLocation(locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).get(0));
                    }else {
                        log.info("MCU["+mcu.getSysID()+"] Default Location["+defaultLocName+"] is Not Exist In DB, Set First Location["+locationDao.getAll().get(0).getId()+"]");
                        mcu.setLocation(locationDao.getAll().get(0));   
                    }
                }else{
                    log.info("MCU["+mcu.getSysID()+"] Default Location is Not Exist In Properties, Set First Location["+locationDao.getAll().get(0).getId()+"]");
                    mcu.setLocation(locationDao.getAll().get(0));  
                }                       
                
                //Set Default Supplier
                String supplierName = new String(FMPProperty.getProperty("default.supplier.name").getBytes("8859_1"), "UTF-8");
                log.debug("Supplier Name[" + supplierName + "]");
                Supplier supplier = supplierName !=null ? supplierDao.getSupplierByName(supplierName):null;
                
                if(supplier !=null && supplier.getId() != null && mcu.getSupplier()==null) {
                    mcu.setSupplier(supplier);
                }else {
                    log.info("MCU["+mcu.getSysID()+"] Default Supplier is Not Exist In Properties, Set First Supplier["+supplierDao.getAll().get(0).getId()+"]");
                    mcu.setSupplier(supplierDao.getAll().get(0));
                }
    
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
                Code status = codeDao.findByCondition("code", CommonConstants.McuStatus.Normal.getCode());
                if (status != null)
                    mcu.setMcuStatus(status);
                
                // get model
                if (sysModel != null)
                    sysModel = sysModel.replace("_", "-");
                
                DeviceModel model = modelDao.findByCondition("name", sysModel);
                if (model != null)
                    mcu.setDeviceModel(model);
                
                event.setLocation(mcu.getLocation());
                
                mcuDao.add(mcu);
            }else{          
                log.info("mcu["+existMcu.getSysID()+"] is existed!! - Location Id:"+existMcu.getLocation().getId()+" Location Name:"+existMcu.getLocation().getName());         
                existMcu.setIpAddr(mcu.getIpAddr());
                existMcu.setSysLocalPort(mcu.getSysLocalPort());
                existMcu.setProtocolType(CommonConstants.getProtocolByName(protocolType+""));
                existMcu.setSysHwVersion(sysHwVersion);
                existMcu.setSysHwBuild(sysHwBuild);
                existMcu.setSysSwVersion(sysSwVersion);
                existMcu.setSysSwRevision(sysSwRevision);
                existMcu.setLastCommDate(TimeUtil.getCurrentTime());
                existMcu.setMacAddr(mcu.getMacAddr());
                existMcu.setIpv6Addr(mcu.getIpv6Addr());
                existMcu.setAmiNetworkAddress(mcu.getAmiNetworkAddress());
                existMcu.setAmiNetworkAddressV6(mcu.getAmiNetworkAddressV6());
                
                Code status = codeDao.findByCondition("code", CommonConstants.McuStatus.Normal.getCode());
                if (status != null)
                    existMcu.setMcuStatus(status);
                
                if (existMcu.getInstallDate() == null || "".equals(existMcu.getInstallDate()))
                    existMcu.setInstallDate(TimeUtil.getCurrentTime());
                // 업데이트를 호출하지 않더라도 갱신이 된다.
                
                event.setLocation(existMcu.getLocation());
                
                mcuDao.update(existMcu);
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
    
        event.setActivatorIp(ipAddr);

        // Get MCU Infomation
        try {
        	
            if (Boolean.parseBoolean(FMPProperty.getProperty("autoset.upload.schedule.enable","false"))) {
            	int min = 40, max = 54;
    	        CommandGW gw = new CommandGW();        
                Map<String, Object> result = gw.cmdMcuGetSchedule(mcuId, "upload");
                String defaultUploadCondition = "55 * * * *";
                
                if(result != null && result.size() > 0){
                	String upload_suspend = (String)result.get("upload_suspend");
                	String upload_condition = (String)result.get("upload_condition");
                	String upload_task = (String)result.get("upload_task");
                	
                	if(upload_condition.indexOf(defaultUploadCondition) >= 0){
                		
                		Random rand = new Random();
                		int seq = rand.nextInt(max - min + 1) + min;
                		upload_task = seq+" * * * *";
                		String[][] scheduleparam = new String[1][4];
                		scheduleparam[0][0] = "upload";
                		scheduleparam[0][1] = upload_suspend;
                		scheduleparam[0][2] = upload_condition;
                		scheduleparam[0][3] = upload_task;

                		try{
                    		gw.cmdMcuSetSchedule_(mcuId, scheduleparam);
                		}catch(Exception e){
                			log.warn("Can't set mcu schedule via CommandGW",e);
                			upload_condition = null;
                		}
                	}
                	
                    if(upload_condition != null && !"".equals(upload_condition)){
                    	                	
                        MCUVar mcuVar = mcu.getMcuVar();
                        if(mcuVar != null){
                        	mcuVar.setVarUploadTime(upload_condition);
                        	mcuVarDao.update(mcuVar);
                        }else{
                        	mcuVar = new MCUVar();
                        	mcuVar.setVarUploadTime(upload_condition);
                        	mcu.setMcuVar(mcuVar);
                        	mcuDao.update(mcu);
                        }
                    }

                }
            }

        }
        catch (Exception e) {
            log.warn("Can't set mcu schedule", e);
        }

        log.debug("MCU Install Event Action Compelte");

    }
}
