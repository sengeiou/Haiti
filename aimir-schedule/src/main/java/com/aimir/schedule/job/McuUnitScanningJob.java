/**
 * (@)# UnitScanningJob.java
 *
 * 2015. 3. 2.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;
import com.aimir.schedule.task.McuUnitScanningTask;

/**
 * @author nuri
 *
 */
public class McuUnitScanningJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(McuUnitScanningJob.class);
	private McuUnitScanningTask task;

	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		task = DataUtil.getBean(McuUnitScanningTask.class);

		if (task == null) {
			log.error("@@@@@@ McuUnitScanningTask is null @@@@@@");
		}

		task.executeTask(context);
	}

}
