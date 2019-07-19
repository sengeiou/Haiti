package com.aimir.schedule.job;
 
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.aimir.schedule.task.MeteringSLATask;
 
public class MeteringSLAJob extends MeteringSLAAnalysisJob
{
	private MeteringSLATask slaTask;

	public void setMeteringSLATask(MeteringSLATask slaTask) {
				
		this.slaTask = slaTask;
	}

	protected void executeInternal(JobExecutionContext context)
	throws JobExecutionException {
		slaTask.excute();
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getParamList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getParamListDefault() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getParamListDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean[] getParamListRequired() {
		// TODO Auto-generated method stub
		return null;
	}
}