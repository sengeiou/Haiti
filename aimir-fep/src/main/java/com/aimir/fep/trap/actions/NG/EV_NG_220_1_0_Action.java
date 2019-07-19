package com.aimir.fep.trap.actions.NG;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import com.aimir.dao.system.PlcQualityTestDao;
import com.aimir.dao.system.PlcQualityTestDetailDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.PLCIU;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.PlcQualityTest;
import com.aimir.model.system.PlcQualityTestDetail;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;

/**
 * Event ID : NG_220.1.0 (evtInstallMeter)
 * <br>PLC-G3 Meter
 *
 * @author goodjob
 * @version $Rev: 1 $, $Date: 2015-07-27 10:00:00 +0900 $,
 */
@Component
public class EV_NG_220_1_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_NG_220_1_0_Action.class);
    
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
    
    @Autowired
    PlcQualityTestDao plcQualityTestDao;
    
    @Autowired
    PlcQualityTestDetailDao plcQualityTestDetailDao;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(Event)
     * @param event - Event Alert Log Data
     * 220.1	evtInstallMeter		OID	3	True	Major	Install meter
     * 
     * 
     * 21.1.1	meterId			STRING	20	 Meter ID
     * 21.1.2	meterModel		STRING	20	 Meter model
     * 21.1.3	meterVendor		STRING	3	 Meter Vendor
     * 21.1.5	meterPhase		BYTE	1	 Meter phase (electricity only)
     * 31.3.3	moG3NodeKind	STRING	20	 Modem Node Kind
     * 31.3.5	moG3FwVer		WORD	2	 Modem FW Version (Major , Minor)
     * 31.3.6	moG3FwBuild		WORD	2	 Modem FW Build number
     * 31.3.7	moG3HwVer		WORD	2	 Modem HW Version
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EventName[evtInstallMeter] "+" EventCode[" + trap.getCode()+"] Modem["+trap.getSourceId()+"]");

        String ipAddr = trap.getIpAddr();
        String modemSerial = trap.getSourceId();
        String meterId = event.getEventAttrValue("meterId");
        String meterModel = event.getEventAttrValue("meterModel") == null ? "" :  event.getEventAttrValue("meterModel");
        String meterVendor = event.getEventAttrValue("meterVendor") == null ? "" :  event.getEventAttrValue("meterVendor");
        int meterPhase = Integer.parseInt(event.getEventAttrValue("meterPhase") == null ? "0" : event.getEventAttrValue("meterPhase"));
        String moG3NodeKind = event.getEventAttrValue("moG3NodeKind") == null ? "" :  event.getEventAttrValue("moG3FwVer");
        String fwVer = event.getEventAttrValue("moG3FwVer") == null ? "" : event.getEventAttrValue("moG3FwVer");
        String build = event.getEventAttrValue("moG3FwBuild")== null ? "" : event.getEventAttrValue("moG3FwBuild");
        String hwVer = event.getEventAttrValue("moG3HwVer") == null ? "" : event.getEventAttrValue("moG3HwVer");
        
        if(fwVer != null && !"".equals(fwVer)){
        	fwVer = DataUtil.getVersionString(Integer.parseInt(fwVer));
        }
        if(hwVer != null && !"".equals(hwVer)){
        	hwVer = DataUtil.getVersionString(Integer.parseInt(hwVer));
        }
        
        log.debug("meterId["+meterId+"]");
        log.debug("meterModel["+meterModel+"]");
        //LSKLIR3410DR-100
        log.debug("meterVendor["+meterVendor+"]");
        log.debug("meterPhase["+meterPhase+"]");
        log.debug("moG3NodeKind["+moG3NodeKind+"]");
        log.debug("moG3FwVer["+fwVer+"]");
        log.debug("moG3FwBuild["+build+"]");
        log.debug("moG3HwVer["+hwVer+"]");

        // Log
        StringBuffer logBuf = new StringBuffer();
        logBuf.append("ipAddr[" + ipAddr +
        		      "] mcuId[" + trap.getMcuId() +
                      "] meterId["+ meterId +
	                  "] meterModel[" + meterModel +
                      "] meterVendor[" + meterVendor +
                      "] meterPhase[" + meterPhase +
                      "] moG3NodeKind[" + moG3NodeKind + "]");
        log.debug(logBuf.toString());        
        
        try {
            
         // get modem
        	MCU mcu = mcuDao.get(trap.getMcuId());
            Supplier supplier = supplierDao.getAll().get(0);
            DeviceModel modemModel = deviceModelDao.findByCondition("name", "NAMR-C402PG");
            PLCIU modem = (PLCIU) modemDao.get(modemSerial);
            Meter meter = null;
            if (modem == null) {
                
                modem = new PLCIU();                
                modem.setDeviceSerial(modemSerial);
                modem.setInstallDate(DateTimeUtil.getDST(supplier.getTimezone().getName(), trap.getTimeStamp()));
                modem.setSupplier(supplier);
                modem.setModemType(ModemType.PLC_G3.name());
                modem.setLocation(locationDao.getAll().get(0));                
                if(modemModel != null){
                    modem.setModel(modemModel); 
                }
                if(mcu != null){
                	modem.setMcu(mcu);
                }

                modem.setFwRevision(build);
                modem.setFwVer(fwVer);
                modem.setHwVer(hwVer);
                modem.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), trap.getTimeStamp()));
                modem.setProtocolVersion(trap.getProtocolVersion());
                modem.setProtocolType(Protocol.PLC.name());
                
                modem.setNameSpace("NG");
                modem.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), trap.getTimeStamp()));
                modemDao.add(modem);
                log.info("Add modem="+modemSerial);
            } else {
                if(modemModel != null){
                    modem.setModel(modemModel); 
                } 
                if(mcu != null){
                	modem.setMcu(mcu);
                }
                modem.setFwRevision(build);
                modem.setFwVer(fwVer);
                modem.setHwVer(hwVer);
                modem.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), trap.getTimeStamp()));
                modem.setProtocolVersion(trap.getProtocolVersion());

                modem.setNameSpace("NG");
            	modemDao.update(modem);
                log.info("Update modem="+modemSerial);
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
                    meter.setInstallDate(DateTimeUtil.getDST(supplier.getTimezone().getName(), trap.getTimeStamp()));
                    meter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.EnergyMeter.name()));
                    meter.setLocation(locationDao.getAll().get(0));
                    meter.setSupplier(supplier);
                    meter.setModem(modem);
                    meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.NewRegistered.name()));
                    
                    EventUtil.sendEvent("Equipment Registration",
                            TargetClass.valueOf(MeterType.EnergyMeter.name()),
                            meterId,
                            trap.getTimeStamp(), new String[][] {},
                            event);
                }                
                
                
                //LSKLIR3410DR-100
                if(meterModel.indexOf("3410CT") >= 0){
                	DeviceModel deviceModel = deviceModelDao.findByCondition("name", "LSIQ-3PCT");
                    if(deviceModel!= null){
                        meter.setModel(deviceModel);
                    }
                }
                if(meterModel.indexOf("3405CP") >= 0){
                	DeviceModel deviceModel = deviceModelDao.findByCondition("name", "LSIQ-3PCV");
                    if(deviceModel!= null){
                        meter.setModel(deviceModel);
                    }
                }
                if(meterModel.indexOf("3410DR") >= 0){
                	DeviceModel deviceModel = deviceModelDao.findByCondition("name", "LSIQ-3P");
                    if(deviceModel!= null){
                        meter.setModel(deviceModel);
                    }
                }
                if(meterModel.indexOf("1210DR") >= 0){
                	DeviceModel deviceModel = deviceModelDao.findByCondition("name", "LSIQ-1P");
                    if(deviceModel!= null){
                        meter.setModel(deviceModel);
                    }
                }
                
                if(meter != null){

                	if(meter.getModel() == null){
                		DeviceModel deviceModel = deviceModelDao.findByCondition("name", "LSIQ-1P");
                        if(deviceModel!= null){
                            meter.setModel(deviceModel);
                        }
                	}

                    meter.setLpInterval(60);
                    meterDao.saveOrUpdate(meter);
                    log.info("Meter Install="+meterId);
                }

            }
            else log.warn("Meter of Modem[" + modemSerial + "] is NULL!");
        }
        catch (Exception e) {
        	log.error(e,e);
        }
        
        //plcAssembleTest2(trap, meterId, modemSerial, hwVer, fwVer, build);

        log.debug("evtInstallMeter Action Compelte");
    }    
    
    
    public void plcAssembleTest2(FMPTrap trap, 
    		String meterId, 
    		String modemSerial, 
    		String hwVer, 
    		String fwVer, 
    		String build){
    	
    	log.debug("meter assemble test start..");
        Set<Condition> condition = new HashSet<Condition>();
        condition.add(new Condition("plcQualityTest", new Object[] {"plcQualityTest"}, null, Restriction.ALIAS));
        condition.add(new Condition("plcQualityTest.zigName", new Object[] {trap.getMcuId()}, null, Restriction.EQ));
        condition.add(new Condition("testStartDate", new Object[]{DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd")+"%"}, null, Restriction.LIKE));
        //condition.add(new Condition("testResult", new Object[]{ modemSerial }, null, Restriction.NULL));
    	List<PlcQualityTestDetail> details = plcQualityTestDetailDao.findByConditions(condition);
    	
    	for(int i = 0; !details.isEmpty() && i < details.size(); i++){        		
    		
    		String excelString = details.get(i).getMeterSerial()+details.get(i).getModemSerial();
    		String testString = meterId+modemSerial;
    		if(excelString.equals(testString)){
    			details.get(i).setTestResult(true);
    			details.get(i).setHwVer(hwVer);
    			details.get(i).setSwVer(fwVer);
    			details.get(i).setSwBuild(build);    
    			details.get(i).setFailReason("");
    			details.get(i).setCompleteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
    			log.debug("plcQualityTestDao update [zig="+trap.getMcuId()+"] testResult=["+details.get(i).getTestResult()+"] meter/modem="+meterId+"/"+modemSerial);  

    			PlcQualityTestDetail updateEntity = details.get(i);
    			try{
    				plcQualityTestDetailDao.update(updateEntity);
    			}catch(Exception e){
    				log.error(e,e);
    			}     			
    		}
    		
    		if(modemSerial.equals(details.get(i).getModemSerial()) && !meterId.equals(details.get(i).getMeterSerial())){
    			details.get(i).setTestResult(false);
    			details.get(i).setCompleteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
    			details.get(i).setFailReason("Meter Serial Number is not match '"+meterId+"' VS '"+details.get(i).getMeterSerial()+"'");

    			PlcQualityTestDetail updateEntity = details.get(i);
    			try{
    				plcQualityTestDetailDao.update(updateEntity);
    			}catch(Exception e){
    				log.error(e,e);
    			}
    		}
    		
    		if(meterId.equals(details.get(i).getMeterSerial()) && !modemSerial.equals(details.get(i).getModemSerial())){
    			details.get(i).setTestResult(false);
    			details.get(i).setCompleteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
    			details.get(i).setFailReason("Modem Serial Number is not match '"+modemSerial+"' VS '"+details.get(i).getModemSerial()+"'");

    			PlcQualityTestDetail updateEntity = details.get(i);
    			try{
    				plcQualityTestDetailDao.update(updateEntity);
    			}catch(Exception e){
    				log.error(e,e);
    			}
    		}
    	}
    	
        condition = new HashSet<Condition>();
        condition.add(new Condition("plcQualityTest", new Object[] {"plcQualityTest"}, null, Restriction.ALIAS));
        condition.add(new Condition("plcQualityTest.zigName", new Object[] {trap.getMcuId()}, null, Restriction.EQ));
        condition.add(new Condition("testResult", new Object[]{ Boolean.TRUE }, null, Restriction.EQ));
    	List<PlcQualityTestDetail> succ = plcQualityTestDetailDao.findByConditions(condition);
    	int succCnt = 0;
    	if(succ != null && succ.size() > 0){
    		succCnt = succ.size();
    	}
    	
    	PlcQualityTest plcQualityTest = plcQualityTestDao.getInfoByZig(trap.getMcuId());
		try{
			plcQualityTest.setCompleteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			plcQualityTest.setSuccessCount(succCnt);
			plcQualityTestDao.update(plcQualityTest);
			log.info("update test result end ");
		}catch(Exception e){
			log.error(e,e);
		}
    	log.debug("plcQualityTestDao update");
    	
    }
    
    public void plcAssembleTest(FMPTrap trap, 
    		String meterId, 
    		String modemSerial, 
    		String hwVer, 
    		String fwVer, 
    		String build){

    	log.debug("meter assemble test start..");
        PlcQualityTest plcQualityTest = plcQualityTestDao.getInfoByZig(trap.getMcuId());
        //if(plcQualityTest != null && plcQualityTest.getTestEnable()!= null && plcQualityTest.getTestEnable()){
        if(plcQualityTest != null){	
        	log.debug("plcQualityTestDao test enable");
        	List<PlcQualityTestDetail> details = plcQualityTest.getPlcQualityTestDetails();

        	for(int i = 0; !details.isEmpty() && i < details.size(); i++){        		
        		
        		String excelString = details.get(i).getMeterSerial()+details.get(i).getModemSerial();
        		String testString = meterId+modemSerial;
        		if(excelString.equals(testString)){
        			details.get(i).setTestResult(true);
        			details.get(i).setHwVer(hwVer);
        			details.get(i).setSwVer(fwVer);
        			details.get(i).setSwBuild(build);    
        			log.debug("plcQualityTestDao update [zig="+trap.getMcuId()+"] testResult=["+details.get(i).getTestResult()+"] meter/modem="+meterId+"/"+modemSerial);  
        			
        			/*
        			PlcQualityTestDetail updateEntity = details.get(i);
        			try{
        				plcQualityTestDetailDao.update(updateEntity);
        			}catch(Exception e){
        				log.error(e,e);
        			}       
        			*/ 			
        		}
        		
        		if(modemSerial.equals(details.get(i).getModemSerial()) && !meterId.equals(details.get(i).getMeterSerial())){
        			details.get(i).setTestResult(false);
        			details.get(i).setFailReason("Meter Serial Number is not match '"+meterId+"' VS '"+details.get(i).getMeterSerial()+"'");
        			/*
        			PlcQualityTestDetail updateEntity = details.get(i);
        			try{
        				plcQualityTestDetailDao.update(updateEntity);
        			}catch(Exception e){
        				log.error(e,e);
        			} 
        			*/
        		}
        		
        		if(meterId.equals(details.get(i).getMeterSerial()) && !modemSerial.equals(details.get(i).getModemSerial())){
        			details.get(i).setTestResult(false);
        			details.get(i).setFailReason("Modem Serial Number is not match '"+modemSerial+"' VS '"+details.get(i).getModemSerial()+"'");
        			/*
        			PlcQualityTestDetail updateEntity = details.get(i);
        			try{
        				plcQualityTestDetailDao.update(updateEntity);
        			}catch(Exception e){
        				log.error(e,e);
        			} 
        			*/
        		}
        	}    

			log.info("update test result start ");
			int successCount = 0;
			for(PlcQualityTestDetail detail: details){
				if(detail.getTestResult()!= null && detail.getTestResult()){
					successCount++;
				}
			}
			try{
				plcQualityTest.setCompleteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
				plcQualityTest.setSuccessCount(successCount);
				plcQualityTestDao.update(plcQualityTest);
				log.info("update test result end ");
			}catch(Exception e){
				log.error(e,e);
			}
        	log.debug("plcQualityTestDao update");
        }
    }

}
