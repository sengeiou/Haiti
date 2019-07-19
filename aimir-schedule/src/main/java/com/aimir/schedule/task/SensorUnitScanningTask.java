package com.aimir.schedule.task;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;

import com.aimir.fep.util.GroupInfo;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Modem;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

@Service
@Transactional
public class SensorUnitScanningTask 
{
    private static Log log = LogFactory.getLog(SensorUnitScanningTask.class);
    
  
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    CmdOperationUtil cmdOperationUtil;

	public void execute() {

		Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
		String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
        
		/*
		Code indoorType = CommonConstants.getMcuTypeByName(McuType.Indoor.name());
		Code outdoorType = CommonConstants.getMcuTypeByName(McuType.Outdoor.name());
		Set<Condition> condition = new HashSet<Condition>();
        condition.add(new Condition("lastCommDate", new Object[]{TFDate}, null, Restriction.GE));
        condition.add(new Condition("mcuType.id", new Object[]{indoorType.getId(), outdoorType.getId()}, null, Restriction.IN));
        condition.add(new Condition("sysHwVersion", new Object[]{}, null, Restriction.NOTNULL));
        condition.add(new Condition("sysSwVersion", new Object[]{}, null, Restriction.NOTNULL));
        List<MCU> list = mcuDao.findByConditions(condition);
		*/
        try{
        	/*
            for (MCU mcu : list) {
                (new UnitScanningThread(mcu)).start();
            }
            */
        	//(new UnitScanningThread(mcuDao.get("2010"))).start();
        }
        catch (Exception e)
        {
            log.error("SensorUnitScanningTask" + e.getMessage(), e);
        }
        log.info("Executing task  ending at " + new Date());
    }
    
    class UnitScanningThread extends Thread {
        private MCU mcu;
        
        UnitScanningThread(MCU mcu) {
            this.mcu = mcu;
        }
        
        public void run() {
        	Set<Condition>set = new HashSet<Condition>();
        	set.add(new Condition("mcu.id",new Object[]{mcu.getId()},null,Restriction.EQ));
        	set.add(new Condition("hwVer",new Object[]{null},null,Restriction.NULL));
            List<Modem> list = modemDao.findByConditions(set);
            
            String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
            
            try{
            	GroupInfo[] groupInfo = cmdOperationUtil.cmdGroupInfo(mcu.getSysID());
                for(int k = 0; k < groupInfo.length; k++) {
                	String gName = groupInfo[k].getGroupName();
                	int gKey = groupInfo[k].getGroupKey();
                	log.debug("mcu["+2010+"] groupName="+gName+" groupKey="+gKey);
                	//if(!gName.endsWith(today)){
                		cmdOperationUtil.cmdGroupDelete(mcu.getSysID(), gKey);
                	//}
                }
            }catch(Exception e){
            	log.error(e,e);
            	System.err.println(e);
            }

            if(list != null && list.size() > 0){

                String groupName = mcu.getSysID()+"_UnitScan_"+today;
                int groupKey=0;
                int ONEDAY = 1;

                try{
                    groupKey = cmdOperationUtil.cmdGroupAdd(mcu.getSysID(), groupName);                    
                }catch(Exception e){
                	log.error(e,e);
                }

                try{                	
                    for (int i = 0; i < list.size(); i++) {
                    	Modem modem = list.get(i);                        
                        cmdOperationUtil.cmdGroupAddMember(mcu.getSysID(), groupKey, modem.getDeviceSerial());
                    }                	
                }catch(Exception e){
                	log.error(e,e);
                }
                try{
                	GroupInfo[] groupInfo = cmdOperationUtil.cmdGroupInfo(mcu.getSysID());
                    for(int k = 0; groupInfo != null && k < groupInfo.length; k++) {
                    	String gName = groupInfo[k].getGroupName();
                    	log.debug(gName);
                    }
                }catch(Exception e){
                	log.error(e,e);
                }

                try{
                	cmdOperationUtil.cmdGroupAsyncCall(mcu.getSysID(), groupKey, "cmdGetSensorROM", 
                			(byte)TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT.getCode()|(byte)TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode(), ONEDAY, 0, 3);
                	//cmdOperationUtil.cmdGroupAsyncCall(mcu.getSysID(), groupName, "cmdGetSensorROM", 
                	//		(byte)TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT.getCode(), ONEDAY, 0, 3);
                }catch(Exception e){
                	log.error(e,e);
                }

            }else{
            	log.info("mcu["+mcu.getSysID()+"] has no modem!!");
            }
  
            
            
            
            /*
            Modem modem = null;
            for(int i = 0; i < list.size(); i++){
            	modem = list.get(i);
                
                log.debug("start sensor join unit scanning,"+modem.getDeviceSerial());
                
                try {
                    if(modem.getModemType().equals(ModemType.ZRU))
                    {
                        CmdOperationUtil.doZRUScanning(modem,1,"schedule");
                    }
                    else if(modem.getModemType().equals(ModemType.ZMU))
                    {
                        CmdOperationUtil.doZMUScanning((ZMU)modem,1,"schedule");
                    }
                    else if(modem.getModemType().equals(ModemType.ZEUPLS))
                    {
                        CmdOperationUtil.doZEUPLSScanning((ZEUPLS)modem,1,"schedule");
                    }
                    else
                    {
                        CmdOperationUtil.doZRUScanning(modem,1,"schedule");
                    }
                    
                    //setUnitScanningResult(modem.getDeviceSerial(), 1);
                }
                catch (Exception e) {
                    try {
                        //setUnitScanningResult(modem.getDeviceSerial(), 0);
                    }
                    catch (Exception ee) {
                        log.error(ee);
                    }
                }
            }
            */ 
        }
    }
}
