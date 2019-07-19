package com.aimir.mars.integration.bulkreading.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.mars.integration.bulkreading.batch.MDMSProperty;
import com.aimir.mars.util.FTPClient;
import com.aimir.mars.util.FileUtil;
import com.aimir.mars.util.SFTPClient;

@Service
public abstract class MDMDataService {
	
	protected static Log log = LogFactory.getLog(MDMDataService.class);
	
	protected static final String BATCH_STATUS_READY = "1"; // 대기
	protected static final String BATCH_STATUS_COMPLETE = "2"; // 완료
	protected static final String MDM_BATCH_MAXRESULT = MDMSProperty.getProperty("mdms.batch.maxResult", "20");
	
	protected static final String MDM_SAVE_PATH = MDMSProperty.getProperty("mdms.dir.input", "/home/aimir/aimiramm/aimir-files/Integraton/MDMS/input");
	protected static final String MDM_BACKUP_PATH = MDMSProperty.getProperty("mdms.dir.backup", "/home/aimir/aimiramm/aimir-files/Integraton/MDMS/backup");

	protected static final String MDM_CHANNEL = MDMSProperty.getProperty("mdms.transfer.channel","sftp");
	protected static final String SFTP_REMOTE_DIR = MDMSProperty.getProperty("mdms.sftp.dest.directory", "/home/aimir/integration/");
	protected static final String FTP_REMOTE_DIR = MDMSProperty.getProperty("mdms.ftp.dest.directory", "/home/aimir/integration/");
	
	protected static final String HEAD_END_ID = MDMSProperty.getProperty("hes.id","NURI001");
	
	protected MDMSHelper mdmsHelper;
	
	@Autowired
	FTPClient ftpClient;
	
	@Autowired
	SFTPClient sftpClient;
	
	public MDMDataService() {
	}
	
	public abstract void execute();
	
	public abstract void getBatchData();
	
	public abstract void makeDataList();
	
	public abstract void getMessage();
	
	public double _floor(double _double) {
		return Math.floor(_double * 1000) / 1000.0;
	}
	
	/**
	 * saveLPFile
	 * @param data
	 * @param mdmType
	 */
	public void saveLPFile(String data, String mdmType) {
		
		String filename = "input_read_" + mdmType + "_" + getCurrentTimeMilli() + ".xml";		
		String filepath = MDM_SAVE_PATH + File.separator + mdmType + File.separator + filename;
				
		try {			
	        writeFile(data, filepath);	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * saveEventFile
	 * @param data
	 * @param mdmType
	 */
	public void saveEventFile(String data, String mdmType) {
		
		String filename = "input_event_" + getCurrentTimeMilli() + ".xml";
		String filepath = MDM_SAVE_PATH + File.separator + mdmType + File.separator + filename;
				
		try {			
	        writeFile(data, filepath);	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * writeFile
	 * @param data
	 * @param filename
	 */
	public void writeFile(String data, String filename) {
			
		Path path = Paths.get(filename);
		
		try {
			
			if (!Files.exists(path)) {
				Files.createDirectories(path.getParent());			
				Files.createFile(path);
			}
		
			BufferedWriter writer = Files.newBufferedWriter(path);
			writer.write(data);
			writer.close();
			
		} catch (IOException e) {
			log.error(e, e);		
		}
	}
	
	public void ftpUpload(String mdmType) {
		
		String datetime = getCurrentTimeMilli();
		
		String SOURCE_DIR = MDM_SAVE_PATH + "/" + mdmType;
		String DEST_DIR = MDM_BACKUP_PATH + "/" + mdmType + "/" + datetime.substring(0, 4) + "/" + datetime.substring(4, 6) + "/" + datetime.substring(6, 8);
		
		boolean result = false;
		if("sftp".equals(MDM_CHANNEL)) {	
			result = sftpClient.folderUpload(SOURCE_DIR, SFTP_REMOTE_DIR);			
		} else {
			result = ftpClient.folderUpload(SOURCE_DIR, FTP_REMOTE_DIR);
		}	
		
		log.debug("FTP(SFTP) RESULT : " + result);
		
		if(result) {			
			log.debug("SOURCE_DIR : " + SOURCE_DIR);
			log.debug("DEST_DIR : " + DEST_DIR);
			// 전송 완료 후 파일 move
			FileUtil.filesMove(SOURCE_DIR, DEST_DIR);
			log.info("ftpUpload move");
		}
	}
	
	public static String getCurrentTimeMilli() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss.SSS");
        return formatter.format(new Date());
    }
}