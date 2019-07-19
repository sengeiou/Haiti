package com.aimir.schedule.job;
 
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.schedule.task.CellPlanningTask;

public class CellPlanningJob extends QuartzJobBean
{
	private CellPlanningTask cellPlanningTask;

	public void setCellPlanningTask(CellPlanningTask cellPlanningTask) {
				
		this.cellPlanningTask = cellPlanningTask;
	}

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
		
		cellPlanningTask.excute();
	}
}