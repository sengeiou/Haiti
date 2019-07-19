/**
 * 
 */
package com.aimir.schedule.task;

import java.util.HashMap;
import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.ModemCommandType;
import com.aimir.constants.CommonConstants.OTAExecuteType;
import com.aimir.constants.CommonConstants.OTATargetType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.command.ws.client.CommandWS;
import com.aimir.fep.command.ws.client.ResponseMap;
import com.aimir.schedule.command.CmdManager;

/**
 * @author simhanger
 *
 */
@Service
public class CloneOnOffScheduleTask extends ScheduleTask {
	private static Logger logger = LoggerFactory.getLogger(CloneOnOffScheduleTask.class);

	@Override
	public void execute(JobExecutionContext context) {
		logger.debug("## CloneOnOffScheduleTask Start. ##");

		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
		if (jobDataMap != null && jobDataMap.containsKey("subJobData")) {

			@SuppressWarnings("unchecked")
			Map<String, Object> params = (Map<String, Object>) jobDataMap.get("subJobData");
			logger.debug(" ==> Task params=[{}]", params.toString());

			if (0 < params.size()) {
				try {
					OTATargetType otaTargetType = (OTATargetType) params.get("otaTargetType");
					OTAExecuteType otaExecuteType = (OTAExecuteType) params.get("otaExecuteType");
					String issueDate = (String) params.get("issueDate");

					boolean propagation = (boolean) params.get("propagation");
					ModemCommandType modemCommandType = (ModemCommandType) params.get("modemCommandType");
					int cloningTime = (int) params.get("cloningTime");

					Protocol fepProtocol = null;
					if (!jobDataMap.containsKey("fepProtocol") || Protocol.valueOf(jobDataMap.getString("fepProtocol")) == null) {
						fepProtocol = Protocol.IP;
					} else {
						fepProtocol = Protocol.valueOf(jobDataMap.getString("fepProtocol"));
					}

					CommandWS ws = CmdManager.getCommandWS(fepProtocol);
					Map<String, ?> result = new HashMap<String, String>();
					if (ws != null) {

						ResponseMap response = ws.cmdCloneOnOffViaSchedule(otaTargetType.name(), otaExecuteType.getValue(), issueDate, propagation, modemCommandType.name(), cloningTime);

						Map<String, Object> map = new HashMap<String, Object>();
						for (ResponseMap.Response.Entry e : response.getResponse().getEntry()) {
							map.put(e.getKey().toString(), e.getValue());
							logger.debug("Result key = " + e.getKey() + ", value = " + e.getValue());
						}
					} else {
						throw new Exception("Can't initiation WebService.");
					}

				} catch (Exception e) {
					logger.error("FEP Connection error - " + e, e);
				}
			}
		} else {
			logger.error("Error ~~ ");
		}

		logger.debug("## CloneOnOffScheduleTask End. ##");
	}

}
