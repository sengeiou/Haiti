package com.aimir.schedule.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.dao.system.GroupDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.model.system.AimirGroup;
import com.aimir.model.system.GroupMember;

/**
 * Group 정보를 필요로 하는 Task에 대한 중복 기능을 구현한다. 
 * @author kskim
 *
 */
@Transactional
public abstract class GroupTask extends ScheduleTask {
	protected static Log log = LogFactory.getLog(GroupTask.class);
	
	@Autowired
	private GroupDao groupDao;
	
	@Autowired
	private GroupMemberDao groupMemberDao;
	
	protected GroupMember[] groupMembers = null;
	
	protected List<String> failMembers = null;

	abstract public void execute(JobExecutionContext context);

	@Override
	public void executeTask(JobExecutionContext context) {
		Integer groupID = Integer.parseInt(context.getTrigger().getJobDataMap().get("group").toString());
		
		if(!initGroup(groupID))
			return;
		
		super.executeTask(context);
	}
	
	
	/**
	 * Group정보를 초기화.
	 * @param groupID
	 */
	private boolean initGroup(Integer groupID) {
		Set<GroupMember> members = null;
		
		AimirGroup aimirGroup = groupDao.get(groupID);
		
		if(aimirGroup==null){
			log.debug("can not found group : " + groupID);
			setFailResult("can not found group");
			return false;
		}
		
		
		GroupType _type = aimirGroup.getGroupType();
		
		
		switch(_type){
		case Meter:
			members = groupMemberDao.getGroupMemberById(groupID);
			break;
		case Location:
			members = groupMemberDao.getMeterSerialsByLocation(groupID);
			break;
		default:
			log.debug("does not support group type : " + groupID);
			setFailResult("does not support group type");
			break;
		}
		
		if(members==null || members.size()==0){
			log.debug("no group member");
			setFailResult("no group member");
			return false;
		}else{
			groupMembers = members.toArray(new GroupMember[0]);
		}
		
		failMembers = new ArrayList<String>();
		
		return true;
	}

	/**
	 * Group단위 Task 는 멤버별 성공/실패 가 발생된다. 때문에 실패 목록을 따로 구성할 수 있다.
	 * @param memberID
	 */
	protected synchronized void addFailMember(String memberID){
		this.failMembers.add(memberID);
	}

	/**
	 * 실행 멤버들에 대한 실패 메시지를 생성한다.
	 * @return
	 */
	synchronized protected String getFailMessage(){
		if(this.groupMembers == null)
			return "";
		
		return String.format(
				"fail(%d/%d) %s",
				this.failMembers.size(), this.groupMembers.length, this.failMembers);
	}
	
	/**
	 * 성공/실패 상태.
	 * @return
	 */
	synchronized protected boolean isSuccess(){
		if(this.failMembers.size()==0)
			return true;
		return false;
	}
	
	@Override
	public void setFailResult() {
		
		String failMessage = getFailMessage();
		
		try{
		String n1 = failMessage.replaceFirst(".*([0-9])/([0-9]).*", "$1");
    	String n2 = failMessage.replaceFirst(".*([0-9])/([0-9]).*", "$2");
    	
    		int i1 = Integer.parseInt(n1);
    		int i2 = Integer.parseInt(n2);
    		
    		if(i1>i2){
    			System.out.println("왜");
    		}
    	
		}catch(Exception e){
			
		}
		
		setFailResult(getFailMessage());
	}
}
