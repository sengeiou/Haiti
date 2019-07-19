package com.aimir.mars.integration.bulkreading.batch;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.mars.integration.bulkreading.service.MDMDataLPService;

public class MDMDataLPTasklet implements Tasklet, InitializingBean {

	@Autowired
	MDMDataLPService service;
	

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		
		service.execute();
		
		return RepeatStatus.FINISHED;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}
}