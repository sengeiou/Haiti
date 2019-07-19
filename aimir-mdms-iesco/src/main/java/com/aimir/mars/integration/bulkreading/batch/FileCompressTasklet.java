package com.aimir.mars.integration.bulkreading.batch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public class FileCompressTasklet implements Tasklet, InitializingBean {

	private static Log log = LogFactory.getLog(FileCompressTasklet.class);
    private Resource csrcResource;
    private Resource cdestResource;
    
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
    	// get the directory 
        File dir = csrcResource.getFile();
        if(!dir.exists()){
            dir.mkdir();	
        }
        File dest = cdestResource.getFile();
        
        File[] fileList = dir.listFiles();
        
        if(fileList == null || fileList.length == 0){
        	log.debug("No file to compress");
        	return RepeatStatus.FINISHED;
        }
        String zipName = "zip";
        String filePath = dest.getAbsolutePath()+"/arcv_"+TimeUtil.getCurrentTimeMilli();
        if(!dest.exists()){
        	dest.mkdir();
        }
        
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        
        String sourceFile = null;
        int count = 0;
        
        try {
            count = fileList.length;

            fos = new FileOutputStream(filePath + "."+ zipName);
            zos = new ZipOutputStream(fos);
            
            for (int i = 0 ; i < count ; i++) {
                sourceFile = fileList[i].getAbsolutePath();
                zipEntry(sourceFile, zos);
            }
        } finally {
            if (zos != null) {
                zos.close();
            }
            if (fos != null) {
                fos.close();
            }
        }

        return RepeatStatus.FINISHED;
    }


    public void setCsrcResource(Resource csrcResource) {
        this.csrcResource = csrcResource;
    }
    
    public void setCdestResource(Resource cdestResource) {
        this.cdestResource = cdestResource;
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
    public void zipEntry(String sourceFile, String zipName) throws Exception {

        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        try {
            fos = new FileOutputStream(zipName);
            zos = new ZipOutputStream(fos);
            
            zipEntry(sourceFile, zos);
        } catch (Exception e) {
        	log.error(e,e);
            e.printStackTrace();
        } finally {
            if (fos != null) {
                fos.close();
            }
            if (zos != null) {
                zos.close();
            }
        }
    }
    
    private void zipEntry(String sourceFile, ZipOutputStream zos) throws Exception {

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        
        String zipEntryName = null;

        try {
            File file = new File(sourceFile);
            zipEntryName = file.getName();

            fis = new FileInputStream(sourceFile);
            bis = new BufferedInputStream(fis);

            ZipEntry zentry = new ZipEntry(zipEntryName);
            zentry.setTime(file.lastModified());
//            zentry.setCompressedSize(COMPRESSION_LEVEL);
            zentry.setCompressedSize(file.length());
            zos.putNextEntry(zentry);

            byte[] buffer = new byte[4096];
            int cnt = 0;

            while ((cnt = bis.read(buffer)) != -1) {
                zos.write(buffer, 0, cnt);
            }
            zos.closeEntry();
        } catch (Exception e) {
        	log.error(e,e);
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
            if (bis != null) {
                bis.close();
            }
        }
               
    }

}