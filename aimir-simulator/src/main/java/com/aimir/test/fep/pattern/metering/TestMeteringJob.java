package com.aimir.test.fep.pattern.metering;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.fep.util.DataUtil;

public class TestMeteringJob extends QuartzJobBean {
	
    private static Log log = LogFactory.getLog(TestMeteringJob.class);
    static {
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
    }
    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {

        try {
        	//DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
            //TestKamstrup test = DataUtil.getBean(TestKamstrup.class);
        	TestNamjun test =  DataUtil.getBean(TestNamjun.class);
            JobDetail jobDetail = context.getJobDetail();
            JobDataMap  map = jobDetail.getJobDataMap();
            
            
            //map.getString(arg0);
           
            
            String[] args = null;
            test.main(args);
        }
        catch (Exception e) {
            log.error(e);
        }
    }
}
