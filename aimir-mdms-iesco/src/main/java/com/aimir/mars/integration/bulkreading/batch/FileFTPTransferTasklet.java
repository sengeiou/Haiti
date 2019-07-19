package com.aimir.mars.integration.bulkreading.batch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.mars.util.FTPClient;
import com.aimir.mars.util.SFTPClient;


public class FileFTPTransferTasklet implements Tasklet {

    private static Log log = LogFactory.getLog(FileFTPTransferTasklet.class);
    
    private String sourceDir;
    
    @Autowired
    SFTPClient sftpClient;
    
    @Autowired
    FTPClient ftpClient;
    
    protected static final String MDM_SAVE_PATH = MDMSProperty.getProperty("mdms.dir.root", "/home/aimir/aimiramm/aimir-files/Integraton/MDMS");
	protected static final String MDM_TMP_PATH = MDMSProperty.getProperty("mdms.dir.tmp", "/home/aimir/aimiramm/aimir-files/Integraton/MDMS/tmp");

	protected static final String MDM_CHANNEL = MDMSProperty.getProperty("mdms.transfer.channel","sftp");
	protected static final String SFTP_REMOTE_DIR = MDMSProperty.getProperty("mdms.sftp.dest.directory", "/home/aimir/integration/");
	protected static final String FTP_REMOTE_DIR = MDMSProperty.getProperty("mdms.ftp.dest.directory", "/home/aimir/integration/");

	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {
		
		try {		
			
			String SOURCE_DIR = MDM_SAVE_PATH;
			String DEST_DIR = MDM_TMP_PATH;
			
			log.debug("MDM_CHANNEL : " + MDM_CHANNEL);
			
			boolean result = false;
			if("sftp".equals(MDM_CHANNEL)) {	
				result = sftpClient.folderUpload(SOURCE_DIR, SFTP_REMOTE_DIR);			
			} else {
				result = ftpClient.folderUpload(SOURCE_DIR, FTP_REMOTE_DIR);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return RepeatStatus.FINISHED;
	}

	public String getSourceDir() {
		return sourceDir;
	}

	public void setSourceDir(String sourceDir) {
		this.sourceDir = sourceDir;
	}
}
