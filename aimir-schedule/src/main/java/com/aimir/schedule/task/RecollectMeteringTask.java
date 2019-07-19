package com.aimir.schedule.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;

import com.aimir.fep.util.GroupInfo;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

@Service
@Transactional
public class RecollectMeteringTask 
{
    private static Log log = LogFactory.getLog(RecollectMeteringTask.class);
    
  
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    MeterDao meterDao;
    
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
		
    	
    	Map<String, Object> params = new HashMap<String, Object>();    		
		
		params.put("channel", 1);
		params.put("searchStartDate",TFDate.substring(0,8) );
		params.put("searchEndDate", TFDate.substring(0,8));
		params.put("dateType", CommonConstants.DateType.HOURLY.getCode());

    	List<Object> gaps = meterDao.getMissingMeters(params);
    	Set<String> mcuList = new HashSet<String>();
    	Hashtable<String,Set<Modem>> mcuModem = new Hashtable<String,Set<Modem>>();
    	if(gaps != null && gaps.size() > 0){
        	for(Object obj : gaps) {
        		HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
        		mcuList.add((String) resultMap.get("deviceNo"));        		
        	}
        	
        	for(String mcu : mcuList) {
        		mcuModem.put(mcu, new HashSet<Modem>());
        	}
        	
        	for(Object obj : gaps) {
        		HashMap<String,Object> resultMap = (HashMap<String, Object>) obj;
        		String meterId = (String) resultMap.get("mdsId");
        		String mcuId =  (String) resultMap.get("deviceNo");
        		if(mcuModem.containsKey(mcuId)){
        			Set set = (Set) mcuModem.get(mcuId);
        			Meter meter = meterDao.get(meterId);
        			Modem modem = meter.getModem();
        			set.add(modem);
        			mcuModem.put(mcuId, set);
        		}
        	}
    	}
    	
        try{

        	/*
            for (String mcuSysId : mcuList) {            	
            	Set modems = mcuModem.get(mcuSysId);
            	MCU mcu = mcuDao.get(mcuSysId);
                (new RecollectThread(mcu, modems)).start();
            }
            */

    		Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("mcu.id", new Object[]{mcuDao.get("2010").getId()}, null, Restriction.EQ));
        	Set<Modem> modemList = (Set<Modem>) modemDao.findByConditions(condition);
        	//(new RecollectThread(mcuDao.get("2010"), modemList)).start();
        }
        catch (Exception e)
        {
            log.error("RecollectMeteringTask" + e.getMessage(), e);
        }
        log.info("Executing task  ending at " + new Date());
    }
    
    class RecollectThread extends Thread {
    	
        private MCU mcu;
        
        private Set<Modem> list;
        
        RecollectThread(MCU mcu, Set<Modem> modems) {
            this.mcu = mcu;
            this.list = modems;
        }
        
        public void run() {
            
            String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);
            
            try{
            	GroupInfo[] groupInfo = cmdOperationUtil.cmdGroupInfo(mcu.getSysID());
                for(int k = 0; k < groupInfo.length; k++) {
                	String gName = groupInfo[k].getGroupName();
                	int gKey = groupInfo[k].getGroupKey();
                	log.debug("mcu["+2010+"] groupName="+gName);
                	//if(!gName.endsWith(today)){
                		cmdOperationUtil.cmdGroupDelete(mcu.getSysID(), gKey);
                	//}
                }
            }catch(Exception e){
            	log.error(e,e);
            	System.err.println(e);
            }

            if(list != null && list.size() > 0){

                String groupName = mcu.getSysID()+"_Recollect_"+today;
                int groupKey=0;
                int ONEDAY = 1;                
                try{
                    groupKey= cmdOperationUtil.cmdGroupAdd(mcu.getSysID(), groupName);
                }catch(Exception e){
                	log.error(e,e);
                }

                try{                	
                	int i = 0;
                    for (Modem modem : list) {                        
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
                	cmdOperationUtil.cmdGroupAsyncCall(mcu.getSysID(), groupKey, "cmdOnDemandMeter", 
                			(byte)TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT.getCode()|(byte)TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode(), ONEDAY, 0, 3);
                }catch(Exception e){
                	log.error(e,e);
                }

            }else{
            	log.info("mcu["+mcu.getSysID()+"] has no modem!!");
            }
        }
    }
}
