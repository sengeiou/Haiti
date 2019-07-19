package com.aimir.schedule.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.model.system.GroupMember;
import com.aimir.schedule.command.CmdOperationUtil;

/**
 * 서버에 등록된 모든 이더넷 모뎀을 조회하여 동기화를 진행한다. 
 * @author kskim
 *
 */
public class DailyTimeSynchTask extends GroupTask {

	private static Log log = LogFactory.getLog(DailyTimeSynchTask.class);
	
	@Autowired
	CmdOperationUtil cmdOperationUtil;
	
	@Override
	public void execute(JobExecutionContext context) {
		//모뎀 목록 조회.
		try {
			// 명령 전송.
			for(int i = 0;i<this.groupMembers.length;i++){
				GroupMember groupMember = this.groupMembers[i];
				String mdsid = groupMember.getMember();
				try {
					cmdOperationUtil.cmdMeterTimeSync(null, mdsid);
				} catch (Exception e) {
					log.error(e,e);
					this.addFailMember(mdsid);
					continue;
				}
			}

			if(isSuccess()){
				setSuccessResult();
			}else{
				setFailResult();
			}

		} catch (Exception e) {
			log.error(e,e);
			setFailResult(e.getMessage());
		}
		
	}
}
