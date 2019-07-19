package com.aimir.schedule.job;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 검침데이터의 유효성 여부를 주기적으로 체크하여 상태 플래그에 표시
 * @author goodjob
 *
 */
public class DataValidationJob extends AimirJob
{
    private static Log log = LogFactory.getLog(DataValidationJob.class);
    
    private static final String description = "com.aimir.schedule.job.DataValidationJob";
    
    private static final String[] paramList = null;
    private static final String[] paramListDescription = null;
    private static final boolean[] paramListRequired = null;
    private static final String[] paramListDefault = null;
    
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException
    {
		//TODO FIND VALIDATION TARGET
		//TODO SET FLAG METERING DATA (IF NOT VALIDATE, SET FLAG NOT VALIDATE)
    }
    
    public String getDescription()
    {
        return description;
    }

    public String[] getParamList()
    {
        return paramList;
    }

    public String[] getParamListDescription()
    {
        return paramListDescription;
    }

    public String[] getParamListDefault()
    {
        return paramListDefault;
    }

    public boolean[] getParamListRequired()
    {
        return paramListRequired;
    }

}
