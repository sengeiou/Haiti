package com.aimir.schedule.task;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.CircuitBreakerCondition;
import com.aimir.dao.device.CircuitBreakerSettingDao;
import com.aimir.dao.device.EndDeviceDao;
import com.aimir.dao.device.EnergyMeterDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.mvm.DayEMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.DemandResponseEventLogDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.dao.system.HomeGroupDao;
import com.aimir.model.device.CircuitBreakerSetting;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.Modem;
import com.aimir.model.mvm.DayEM;
import com.aimir.model.system.DemandResponseEventLog;
import com.aimir.model.system.GroupMember;
import com.aimir.model.system.HomeGroup;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;
import com.aimir.util.Condition.Restriction;


@Transactional
public class LoadManagementTask {

    protected static Log log = LogFactory.getLog(LoadManagementTask.class);
    
    @Autowired
    CircuitBreakerSettingDao circuitBreakerSettingDao;
    
    @Autowired
    EndDeviceDao endDeviceDao;
    
    @Autowired
    HomeGroupDao homeGroupDao;
    
    @Autowired
    GroupMemberDao groupMemberDao;
    
    @Autowired
    DayEMDao dayDao;
    
    @Autowired
    EnergyMeterDao meterDao;
    
    @Autowired
    ContractDao contractDao;

    @Autowired
    CodeDao codeDao;
    
	@Autowired
	OperationLogDao operationLogDao;

	@Autowired
	CmdOperationUtil cmdOperationUtil;
	
    @Autowired    
    DemandResponseEventLogDao demandResponseEventLogDao;
	
	public static final int DRLEVEL_ON = 1;
	public static final int DRLEVEL_OFF = 15;

    public void excute() {

    	log.info("############################# Load Control Monitor Scheduler Start ##################################");
    	
        CircuitBreakerSetting circuitBreakerSetting 
        	= circuitBreakerSettingDao.findByCondition("condition", CircuitBreakerCondition.ExceedsThreshold);
        Integer recoverTime = 5;
        if(circuitBreakerSetting != null){
        	recoverTime = circuitBreakerSetting.getRecoveryTime();
        	if(recoverTime == null || recoverTime < 1){
        		recoverTime = 5;
        	}
        }
    	
    	
        String datetime = "";
		try {
			datetime = TimeUtil.getCurrentTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	    Set<Condition> condition = new HashSet<Condition>();
	    condition.add(new Condition("lastReadDate", new Object[]{datetime.substring(0,8)+"%"}, null, Restriction.LIKE));
	    condition.add(new Condition("usageThreshold", new Object[]{0d}, null, Restriction.GT));
	    List<EnergyMeter> meters = meterDao.findByConditions(condition);	    
	    
	    List<String> contractList = new ArrayList<String>();
	    
	    for(EnergyMeter meter : meters){
	    	String mdsId = meter.getMdsId();
	    	log.info("meterId="+mdsId);
	    	Double threshold = meter.getUsageThreshold();
		    Set<Condition> cond = new HashSet<Condition>();
		    cond.add(new Condition("id.channel", new Object[]{1}, null, Restriction.EQ));
		    cond.add(new Condition("id.yyyymmdd", new Object[]{datetime.substring(0,8)}, null, Restriction.EQ));
		    cond.add(new Condition("id.mdevId", new Object[]{mdsId}, null, Restriction.EQ));		    
		    
		    List<DayEM> days = dayDao.findByConditions(cond);
		    
		    if(days != null && days.size() > 0){
		    	DayEM day = days.get(0);
		    	if(day.getTotal() > threshold){
			    	log.info("Threshold list ="+mdsId + "," + meter.getContract().getContractNumber() + ","+day.getTotal() + "/"+threshold);
		    		contractList.add(meter.getContract().getContractNumber());
		    	}		    	
		    }
	    }
	    
	    if(contractList != null && contractList.size() > 0){
	    	for(String contractNumber : contractList ){
	    		HomeGroup homeGroup = homeGroupDao.findByCondition("name", contractNumber);
	    		if(homeGroup != null){	    			
	    			
	    			Set<GroupMember>  groupMembers = groupMemberDao.getGroupMemberById(homeGroup.getId());
	    			
	    			if(groupMembers != null && groupMembers.size() > 0){
	    				for(GroupMember groupMember : groupMembers){
	    					String memberSerial = groupMember.getMember();
	    					EndDevice endDevice = endDeviceDao.findByCondition("serialNumber", memberSerial);
	    					if(endDevice != null){
	    						Modem modem = endDevice.getModem();
	    						Boolean drTrue = endDevice.getDrProgramMandatory();
	    						
	    						if(drTrue != null && drTrue && modem != null){
	    							try {
	    								
	    								String startDateTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
										cmdOperationUtil.cmdSetEnergyLevel(modem.getMcu().getSysID(), modem.getDeviceSerial(), DRLEVEL_OFF);			    						
			    						// DR레벨 변경 구현( 집중기에 자산 상태이벤트가 구현되지 않았기 때문에 임시로 작성)
			    						endDeviceDao.updateEndDeviceDrLevel(endDevice.getId(), endDevice.getCategoryCode().getId(), DRLEVEL_OFF);

			    						
			    						Thread.sleep(recoverTime*1000);
			    						
										cmdOperationUtil.cmdSetEnergyLevel(modem.getMcu().getSysID(), modem.getDeviceSerial(), DRLEVEL_ON);			    						
			    						// DR레벨 변경 구현( 집중기에 자산 상태이벤트가 구현되지 않았기 때문에 임시로 작성)
			    						endDeviceDao.updateEndDeviceDrLevel(endDevice.getId(), endDevice.getCategoryCode().getId(), DRLEVEL_ON);			    						
			    						
			    						DemandResponseEventLog drLog = new DemandResponseEventLog();

			    	            		drLog.setOptOutStatus(CommonConstants.DemandResponseEventOptOutStatus.Initialization.getDrEventOptOutStatus()); // 초기값 설정
			    	                    
			    	        			drLog.setDrasClientId("nuritelecom.hems_001");
			    	        			drLog.setEndTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			    	        			drLog.setEventIdentifier("Direct load Control_"+contractNumber);
			    	        			drLog.setEventInfoName("LC");
			    	        			drLog.setEventInfoTypeID("LC");
			    	        			drLog.setEventModNumber("1");
			    	        			drLog.setEventStateId("1");
			    	        			drLog.setNotificationTime(startDateTime);
			    	        			drLog.setOperationModeValue("NORMAL");
			    	        			drLog.setProgramName("Direct load Control");
			    	        			drLog.setStartTime(startDateTime);
			    	        			drLog.setEventStatus("FAR");
			    	        			drLog.setYyyymmdd(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd"));			    						
			    						drLog.setOptOutStatus(CommonConstants.DemandResponseEventOptOutStatus.Completed.getDrEventOptOutStatus()); // DR Event 참여 상태를 갱신한다.
			    			        	demandResponseEventLogDao.saveOrUpdate(drLog);
			    						
									} catch (Exception e) {
										log.error(e,e);
									}

	    						}

	    					}
	    					
	    				}
	    			}
	    		}
	    	}
	    }

    	log.info("############################# Load Control Monitor Scheduler End ##################################");
    }

}