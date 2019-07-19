package com.aimir.mars.util;

import java.io.File;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

@Service
public class FTPClient {
	
	protected static Log log = LogFactory.getLog(FTPClient.class);
	
	@Resource(name="ftpChannel") 
    MessageChannel ftpChannel;
	
	private int DEFAULT_FTP_CONNECTION_TIMEOUT = 30000;
	
	public boolean upload(String fileName, String remoteDir) {
		
		boolean result = false;
		
		try {
			
			File f = new File(fileName);
			
			Message<File> message = null;
			
			// build message payload
			message = MessageBuilder.withPayload(f)
					.setHeader("remoteDir", remoteDir)
					.build();
			
			result = ftpChannel.send(message , DEFAULT_FTP_CONNECTION_TIMEOUT);
			
			log.debug("result : " + result);


		} catch (Exception e) {
			log.error("ERROR", e);
		}
		
		return result;
	}
	
	
	public boolean folderUpload(String sourceDir, String remoteDir) {
		 
		boolean result = true;
		
		try {		
			
			log.info("sourceDir : " + sourceDir);
			
			log.debug(ftpChannel);
			
			// get the list of files that needs to be transfered
			String[] filesToTransfer = FileUtil.getAllFileNames(sourceDir);
			
			Message<File> message = null;

			// iterative the files and transfer
			for (String fileName : filesToTransfer) {
				
				File f = new File(fileName);
				
				// build message payload
				message = MessageBuilder.withPayload(f)
						.setHeader("remoteDir", remoteDir)
						.build();
				
				result = ftpChannel.send(message , DEFAULT_FTP_CONNECTION_TIMEOUT);
			}
			
		} catch(Exception e) {
			result = false;
			e.printStackTrace();
		}
		
		return result;
	}
}