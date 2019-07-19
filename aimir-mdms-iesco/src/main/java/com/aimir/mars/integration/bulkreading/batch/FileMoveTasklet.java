package com.aimir.mars.integration.bulkreading.batch;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;

import com.aimir.util.TimeUtil;

/**
 * File move task 
 * @author goodjob
 *
 */
public class FileMoveTasklet implements Tasklet, InitializingBean {

    private Resource msrcResource;
    private Resource mdestResource;
    
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    	// get the directory 
        File dir = msrcResource.getFile();
        File dest = mdestResource.getFile();
        
        if(!dest.exists()){
        	dest.mkdir();
        }
        
        String currDay = TimeUtil.getCurrentDay();
        File currDest = new File(dest.getAbsolutePath()+File.separator+currDay);        

        if(!currDest.exists()){
        	currDest.mkdir();
        }
        
        if( dir != null ) {

            File[] files = dir.listFiles();
        
            if( files != null){
            	
            	for (int i = 0; i < files.length; i++) {
                	FileUtils.moveFileToDirectory(files[i], currDest, false);
            	}            	
            }
        }
        return RepeatStatus.FINISHED;
    }


    public void setMsrcResource(Resource msrcResource) {
        this.msrcResource = msrcResource;
    }
    
    public void setMdestResource(Resource mdestResource) {
        this.mdestResource = mdestResource;
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub		
	}

}