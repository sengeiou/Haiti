package com.aimir.schedule.task;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.GroupDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.system.GroupMember;

/**
 * Send SMS and bulk SMS to the all prepaid customer or the specific group.
 * 
 * @author MieUn
 *
 */
public class SendSMSTask extends ScheduleTask {

	private static Log log = LogFactory.getLog(PrepaySendSMSSpasaTask.class);

	@Autowired
	ContractDao contractDao;

	@Autowired
	CustomerDao customerDao;

	@Autowired
    MeterDao meterDao;
	
	@Autowired
	private GroupDao groupDao;
	
	@Autowired
	private GroupMemberDao groupMemberDao;

	private boolean isNowRunning = false;
	
	HashMap<String, String> messageMap; // 보낼 메시지 내용
	String defaultMessage = "This is a message from the AiMiR system." ;

	@Override
    public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### SendSMSTask is already running...");
			return;
		}
		isNowRunning = true;
		
        log.info("@@@@@@ Send SMS to customer Task Start @@@@@@");

        try {
        	Object groupObj = context.getTrigger().getJobDataMap().get("group");
        	List<Map<String, Object>> target = null;

        	// for the all prepaid customer
        	if(groupObj == null) {
        		target = this.getPrepaidCustomerForAll();
        	}else {// for specific group
        		String groupType = context.getTrigger().getJobDataMap().get("groupType").toString();
        		if(!"Meter".equals(groupType)){
        			log.error("no support group type " + groupType);
        			setFailResult("does not support group type");
        			return;
        		}
        		// 그룹일 경우 해당 그룹의 선불고객 정보를 취득한다.
        		target = this.getPrepaidCustomerForGroup(Integer.valueOf((String)groupObj));
        	}

        	this.sendBulkSMS(target, this.getMessage(context.getTrigger()));

        } catch (Exception e) {
            log.error(e,e);
        } finally {
            log.info("@@@@@@ Send SMS to customer Task End @@@@@@");
        }

        log.info("@@@@@@ Send SMS to customer Task End @@@@@@");
        
        isNowRunning = false;
    }

	/**
	 * Get prepaid customer list to send sms
	 * @return  List<Map<String, Object>> : prepaid customer list
	 */
	private List<Map<String, Object>> getPrepaidCustomerForAll(){
		Map<String, Object> condition = new HashMap<String, Object>();
		
		// SMS 수신 가능 조건 설정
		condition.put("smsYn", true);
		List<Map<String, Object>> result = contractDao.getPrepaidCustomerListForSMS(condition);
		return result;
	}

	/**
	 * 그룹에 포함된 선불고객 정보 취득
	 * Get preaid customer information of the specific group
	 * @return
	 */
	private  List<Map<String, Object>> getPrepaidCustomerForGroup(Integer groupId){
		List<Map<String, Object>> toList = new ArrayList<Map<String, Object>>();
		
		// 그룹 맴버 취득
		GroupMember[] groupMembers = this.getMeterGroupMembers(groupId);
		 //그룹에 포함된 미터 목록
    	for(GroupMember groupMember : groupMembers){
    		String memberId = groupMember.getMember();

			// 검색조건 설정 map 정의
			Map<String, Object> condition = new HashMap<String, Object>();
			// SMS 수신 가능 조건 설정
			condition.put("smsYn", true);
			condition.put("mdsId", memberId);
			Map<String, Object> map = new HashMap<String, Object>();
			// get mobile number to send sms
			String mobileNo = this.getPrepaidCustomerByMeter(condition);
			if(mobileNo.isEmpty()) continue;
			map.put("MOBILENO", mobileNo);
			toList.add(map);
    	}
    	return toList;
	}

	/**
	 * Task Management화면에서 입력한 메시지를 취득한다.
	 * 복수개 입력시 리스트에 담아서 저장함.
	 * @param trigger
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<String> getMessage(Trigger trigger){

		JobDataMap jobDataMap = trigger.getJobDataMap();  	
    	List<String> msgList = new ArrayList<String>();
    	
		if(jobDataMap != null && jobDataMap.containsKey("subJobData")){
			messageMap = (HashMap<String, String>) jobDataMap.get("subJobData");

			for(String key : messageMap.keySet()){
				String message = messageMap.get(Integer.parseInt(key));
				msgList.add(message);
			}
		}else{
			msgList.add(defaultMessage);
		}
		return msgList;
	}

	/**
	 * Get Group members.
	 * @param groupID Group ID
	 * @return
	 */
	private GroupMember[] getMeterGroupMembers(Integer groupID) {
		GroupMember[] groupMember = new GroupMember[]{};

		Set<GroupMember> members = groupMemberDao.getGroupMemberById(groupID);
		if(members==null || members.size()==0){
			log.debug("no group member");
			setFailResult("no group member");
			return groupMember;
		}else{
			groupMember = members.toArray(new GroupMember[0]);
		}

		return groupMember;
	}
	
	/**
	 * Get the mobile number of prepaid customers mobile number by meter ID. 
	 * 미터번호로 선불 고객의 핸드폰 번호를 조회한다.
	 * @param condition 미터번호 검색 조건
	 * @return 미터번호에 의해 조회된 선불고객의 핸드폰 번호
	 */
	public String getPrepaidCustomerByMeter(Map<String, Object> condition){

		List<Map<String, Object>> result = contractDao.getPrepaidCustomerListForSMS(condition);

		return result == null || result.size() == 0 ? "" :  result.get(0).get("MOBILENO").toString();
	}

	/**
	 * 싱글 SMS 전송한다.
	 * @param mobileNo 수신자 핸드폰 번호
	 * @param message  메시지
	 * @return
	 */
	public boolean sendSMS(String mobileNo, String message) {
		
		Properties prop = new Properties();
		try{
			prop.load(getClass().getClassLoader().getResourceAsStream("config/schedule.properties"));
	
			String smsClassPath = prop.getProperty("smsClassPath");
			SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
	
			Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
			String messageId = (String) m.invoke(obj, mobileNo.replace("-", ""), URLDecoder.decode(message, "UTF-8"), prop);

			log.debug("### Send message ID : " +messageId);
		}catch(Exception e){
			log.error(e, e);
		}
		
//      TODO 저장 여부 고려(AiMIR에서 별도의 SMS 이력을 관리해야 하는건 아닌지)		
//		if(!"".equals(messageId)) {
//			contract.setSmsNumber(messageId + ":prepaySendSMS");
//		}
		return true;
	}
	
	/**
	 * Send Bulk SMS
	 * 1. SMS 전송 대상이 복수일 경우 대상수만큼 반복하여 SMS를 전송한다.
	 * 2. 메시지가 한개 이상일 경우는 메시지 개수만큼 반복하여 SMS를 전송한다.
	 * @param toList mobile number list
	 * @param msgList  message list to send
	 * @return 
	 */
	public boolean sendBulkSMS(List<Map<String, Object>> toList, List<String> msgList) {
		if(toList == null || toList.size() ==0){
			log.debug("no target list to send sms");
			return false;
		}
		Properties prop = new Properties();
		String messageId = "";
		try{		
			/*prop.load(getClass().getClassLoader().getResourceAsStream("config/schedule.properties"));
			String smsClassPath = prop.getProperty("smsClassPath");

			for(Map<String, ?> target : toList){
				String mobileNo = (String)target.get("MOBILENO").toString().replace("-", "");
				for(String message : msgList) {// 한개 이상의 메시지 전송을 위해서 리스트에서 꺼내서 전송
					SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
					Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
					//messageId = (String) m.invoke(obj, mobileNo, URLDecoder.decode(message, "UTF-8"), prop);
					log.debug("### Send message ID : " +messageId + ", mobile Number : " + mobileNo + ", message : " + message +" ###");
				}
			}*/

			prop.load(getClass().getClassLoader().getResourceAsStream("config/schedule.properties"));
	
			String smsClassPath = prop.getProperty("smsClassPath");
			SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
			Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);

			for(Map<String, ?> target : toList){
				String mobileNo = (String)target.get("MOBILENO").toString().replace("-", "");

				for(String message : msgList) {// 한개 이상의 메시지 전송을 위해서 리스트에서 꺼내서 전송
					messageId = (String) m.invoke(obj, mobileNo, URLDecoder.decode(message, "UTF-8"), prop);
					log.debug("### Send message ID : " +messageId + ", mobile Number : " + mobileNo + " ###");
				}
			}
			
		}catch(Exception e){
			log.error(e, e);
			return false;
		}

//      TODO 저장 여부 고려(AiMIR에서 별도의 SMS 이력을 관리해야 하는건 아닌지)
//		if(!"".equals(messageId)) {
//			contract.setSmsNumber(messageId + ":prepaySendSMS");
//		}
		return true;
	}
}
