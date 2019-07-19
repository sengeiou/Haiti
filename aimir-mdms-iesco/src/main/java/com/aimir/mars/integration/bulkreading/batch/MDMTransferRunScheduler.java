package com.aimir.mars.integration.bulkreading.batch;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MDMTransferRunScheduler {
	
	private static final Logger log = LoggerFactory.getLogger(MDMTransferRunScheduler.class);
	
	@Autowired
	private JobLauncher jobLauncher;
	
	@Resource(name="mdmsFtpTransferJob")
	private Job job;
	
	public void run() {
		
		try {
			
			String dateParam = new Date().toString();
			JobParameters param =
					new JobParametersBuilder().addString("date", dateParam).toJobParameters();
			
			log.info("## [MDMSTransferRunScheduler] dateParam : " + dateParam);
			
			JobExecution jobExecution = jobLauncher.run(job, param);
			
			log.info("## [MDMSTransferRunScheduler] Exit Status : " + jobExecution.getStatus());
			
		} catch (Exception e) {
			e.printStackTrace();
			
			log.error("## [MDMSTransferRunScheduler]", e);
		}
	}
}
