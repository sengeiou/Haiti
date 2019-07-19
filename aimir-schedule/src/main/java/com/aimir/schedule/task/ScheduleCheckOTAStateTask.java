package com.aimir.schedule.task;

import java.text.SimpleDateFormat;
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
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.FirmwareHistoryDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;

import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.GroupInfo;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Modem;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;


/*
 * Modem을 위한 ScheduleCheckOTAStateTask을 만든 이유.
 * MCU의 경우 배포 완료 되면 장비가 바로 리셋이 되며 경과 시간은 5분정도가 걸린다. 즉 OTA Event에서 5분정도의 delay time을 가진 후 결과를 업데이트 하면 됨.
 * 하지만 Modem의 경우 바로 리셋이 되는것이 아니라 하루 또는 (정기검침) 그 이후에 배포가 반영되었다고 나온다. 
 * 그럴경우 성공 실패는 OTA Event 에서 바로 업데이트 가능하겠지만 F/W version은 반영이 안된다. 그래서 history정보에서 성공 된값을 
 * 가지고 온 후 uniscanning을 해서 version이 틀린 값 또는 같은 값의 결과 상태 값을 다시 업데이트 하는것이다.
 * 아래 로직에서 좀더 성공 실패 여부의 경우 의 수를 체크 한  후 추가 작업 할 필요가 있음..
 * */
@Service
@Transactional
public class ScheduleCheckOTAStateTask 
{
    private static final String Modem = null;

	private static Log log = LogFactory.getLog(SensorUnitScanningTask.class);
    
     @Autowired
    FirmwareHistoryDao firmwareHistoryDao;
    
	@Autowired
	ModemDao modemDao;
	
	@Autowired
	CmdOperationUtil cmdOperationUtil;
	
	public void execute() {
		System.out.println("ScheduleCheckOTAStateTask start..");
		log.debug("ScheduleCheckOTAStateTask start..");
		/*
		 * 2.0의  nuri.aimir.service.cm.fw.ejb.impl.ScheduleCheckOTAState.java 파일 참조하였음.
		 * 
		 * */
		Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
//		String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");
		double limitation = 1.0;
		
        try{
        	log.debug(" firmwareHistoryDao.getScheduleCheckOTAState before = ");
        	List<Object> otaStateList = firmwareHistoryDao.getScheduleCheckOTAState("Modem");
        	// 여기서 날짜를 가지고 오는데 날짜가 오래 지난것은 error로 업데이트 하는 작업을 하여 야 함. 
        	log.debug("otaStateList.size() = "+otaStateList.size());
 		   for (Object obj : otaStateList) {
		       Object[] objs = (Object[])obj;
 		       String equipKind = String.valueOf(objs[4]);
 		       String equipType = String.valueOf(objs[6]);
 		       String equipId =  String.valueOf(objs[3]);
 		       Double timegap = new Double(String.valueOf(objs[23]));
 		       //String equip_kind = String.valueOf(objs[4]);
 		       String deviceSerial = String.valueOf(objs[3]);//mcu일경우 mcu_id 값이 들어가 있다.
 		       String prev_equipid = "";
 		       String tmpArm = String.valueOf(objs[16]);
    		   String isArm = tmpArm.equals("0")?"false":"true";
    		   String triggerHw = String.valueOf(objs[18]);
    		   String triggerFw = String.valueOf(objs[19]);
    		   String triggerBuild = String.valueOf(objs[21]);
    		   String triggerId = String.valueOf(objs[2]);
    		   String enddate =  String.valueOf(objs[22]);
    		   
    		   log.debug(" equipKind     ="+ equipKind     );
    		   log.debug(" equipType 		="+ equipType 	  );
    		   log.debug(" equipId 			="+ equipId 		  );
    		   log.debug(" timegap 			="+ timegap 		  );
    		   log.debug(" deviceSerial 	="+ deviceSerial  );
    		   log.debug(" prev_equipid 	="+ prev_equipid  );
    		   log.debug(" tmpArm 				="+ tmpArm 			  );
    		   log.debug(" isArm 				="+ isArm 			  );
    		   log.debug(" triggerHw			="+ triggerHw		  );
    		   log.debug(" triggerFw 		="+ triggerFw 	  );
    		   log.debug(" triggerBuild 	="+ triggerBuild  );
    		   log.debug(" triggerId 		="+ triggerId 	  );
    		   log.debug(" enddate 			="+ enddate 		  );

    		   
    		   boolean result = false;
 		       
    		   log.debug("timegap.doubleValue() > limitation  ");
    		   
 		       if(timegap.doubleValue() > limitation){
                   String modemHW = "";
                   String modemFW = "";
                   String modemBuild = "";
                   String armHW = "";
                   String armFW = "";
                   String armBuild = "";
                 
                   try{
                	   String  success = "";
            		   //스켄 후 성공 여부 확인 하여 모뎀 정보를 가지고 온다
            		   //가지고 온 modem 버젼 정보와 trigger정보와 비교하여 성공 여부를 판가름 한다.
                	   //2.0에서는 select * from mi_zru where id =?
            		   Modem mdm = modemDao.findByCondition("deviceSerial", deviceSerial);
            		   String m_id = String.valueOf(mdm.getId());
            		   String m_type =  mdm.getModemType().name();
            		   //serviceType 1:NMS 2:MTR 으로 구분이 되나 2번으로 고정 시키며 3.0버전에서 필요가 없으므로 추후 제거 필요, operator도 마찬가지임
            		   //schedule에서 실행 되는 것이기 때문에 administrator 로 고정 시켰음.
            		   log.debug(" m_id :"+m_id );
            		   log.debug(" m_type :"+m_type );
            		   if (mdm.getMcu() != null) {
                		   log.debug(" mdm.getMcu().getSysID() :"+mdm.getMcu().getSysID() );
                		   log.debug(" m_type :"+m_type );
                		   success= cmdOperationUtil.doSensorScanning(mdm.getMcu().getSysID(), m_id, m_type, 2, "administrator");
                		   log.debug(" success = "+success );
                		   if(success.equals("success")){
                    		   modemHW = mdm.getHwVer()==null?"": mdm.getHwVer();
                               modemFW = mdm.getFwVer()==null?"": mdm.getFwVer();
                               modemBuild = mdm.getFwRevision()==null?"":mdm.getFwRevision();
                               //triggerHw = otaStateList 에서 가지고 온 값
       	                    //if(isArm==null || isArm.equals("false")|| isArm.equals("0")){
                   		   //2.0은 모뎀 종류마다 테이블이 나뉘어저 있기때문에  아래 주석처럼armFwVersion 을 따로 가지고 왔지만(select * from MI_ZEUMBUS)
                   		   //3.0에서는  그렇지 않음.
       	                    if(triggerHw.equals(modemHW) && triggerFw.equals(modemFW) && triggerBuild.equals(modemBuild)){
       	                    	log.debug(" if triggerHw.equals(modemHW) case" );
       	                    	StringBuffer buff = new StringBuffer();
       	                    	buff.append("UPDATE FIRMWARE_HISTORY SET TRIGGER_STEP = 4,TRIGGER_STATE=0 WHERE tr_id = "+triggerId+" \n");
       	                    	firmwareHistoryDao.updateFirmHistoryBySchedule(buff.toString());
       	                    	result=true;
       	                    }else{
       	                    	log.debug(" if !triggerHw.equals(modemHW) case" );
       	                    	StringBuffer buff = new StringBuffer();
       	                    	buff.append("UPDATE FIRMWARE_HISTORY SET ota_state = 1 WHERE tr_id = "+triggerId+" \n");
       	                    	firmwareHistoryDao.updateFirmHistoryBySchedule(buff.toString());
       	                    }
       	                   log.debug("Scheduler Message [ modemHW : " + modemHW + " modemFW : " + modemFW + " modemBuild : " + modemBuild+" ]");
       	                   log.debug("TargetClass.valueOf(mdm.getModemType().name()) = "+TargetClass.valueOf(mdm.getModemType().name()));
       	                   log.debug("mdm.getMcu().getSysID()  = "+mdm.getMcu().getSysID());
       	                   
       	                   /* EventUtil.sendEvent("OTAResult",
       	                    		TargetClass.valueOf(mdm.getModemType().name()),
       	                    		mdm.getMcu().getSysID(),
       	                    		 new String[][] {
                                           {"message",
                                               equipType +"[" + equipId +
                                               "] Trigger ID[" + triggerId + "] OTA Trigger Update ["+result+"]"}
       	                    			},null
       	                           );*/
                		   }
            		   }
            		   else {
            		       log.warn("Modem[" + deviceSerial + "] don't have mcu");
            		   }
                   }catch(Exception e){
                	   log.error(e);
                   }                                                 
 		       }//if limit End 		
	           if(enddate == null || enddate.equals("null")){
	        	   log.debug("if enddate is null" );
	        	    String format = "yyyyMMddHHmmss";
	                SimpleDateFormat sdf = new SimpleDateFormat(format);
	                Calendar cal = Calendar.getInstance();
	                String dateStr = sdf.format(cal.getTime());
	        		StringBuffer buff = new StringBuffer();
	            	buff.append("UPDATE FIRMWARE_TRIGGER SET END_DATE='"+dateStr+ "' WHERE ID = "+triggerId+" \n");
	            	firmwareHistoryDao.updateFirmHistoryBySchedule(buff.toString());
	            }
 		   }//End for
 		   
        }
        catch (Exception e)
        {
            log.error("SensorUnitScanningTask" + e.getMessage(), e);
        }
        log.info("Executing task  ending at " + new Date());
		System.out.println("Executing task  ending at " + new Date());
    }       
}
