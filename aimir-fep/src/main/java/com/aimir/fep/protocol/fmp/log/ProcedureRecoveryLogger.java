package com.aimir.fep.protocol.fmp.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.logger.AimirThreadMapper;
import com.aimir.fep.logger.snowflake.SnowflakeGeneration;

/**
 *
 * LP에 대한 저장을 프로시저를 이용할 때 실패할 경우 해당 로그를 통해
 * 저장 후 스케줄을 통해서 복구한다.
 *
 * Created: 2019.02.12
 *
 * @author  Seung Woo Han
 * @version 0.1
 */
public class ProcedureRecoveryLogger extends ExternalTableLogger {
    private static Log log = LogFactory.getLog(ProcedureRecoveryLogger.class);
    
    
    private String backupPath = null;
    private String restorePath = null;
    
    public ProcedureRecoveryLogger() throws IOException {
        super();
        backupPath = "db/procedure";
        restorePath = logDirName;
    }
    
    public boolean isReadableRecoveryObject() {
    	File backupDir = new File(backupPath);
    	File[] files = backupDir.listFiles();
    	
    	if(files != null && files.length > 0) 
    		return true;
    	else
    		return false;
    }
    
    public synchronized ArrayList<File> readRecoveryObject() {
    	ArrayList<File> list = new ArrayList<File>();
    	File backupDir = new File(backupPath);
    	int maxNum = 100000;
    	
    	try {
    		File[] files = backupDir.listFiles();
    		int cnt = files.length > maxNum ? maxNum : files.length;
    		
    		for(int i=0; i<cnt; i++) {
    			list.add(files[i]);
    		}
    			
    	}catch(Exception e) {
    		log.error(e,e);
    	}
    	
    	return list;
    }
    
    public String getMeterIdByFile(File file) {
    	String meterId = null;
    	if(file == null)
    		return meterId;
    	
    	FileReader fileReader = null;
    	BufferedReader bufferedReader = null; 
    	
    	try {
    		fileReader = new FileReader(file);
    		bufferedReader = new BufferedReader(fileReader);
    		
    		String s = null;
    		while((s = bufferedReader.readLine()) != null) {
    			if(s.contains("|")) {
    				String[] arr = s.split("\\|");    
    				if(!arr[0].isEmpty()) {
    					meterId = arr[0];
    					break;
    				}
    			}
    		}
    		
    		fileReader.close();
    		bufferedReader.close();
    	}catch(Exception e) {
    		log.error(e,e);
    	}finally {
    		try {
    			if(fileReader != null) fileReader.close();
    			if(bufferedReader != null) bufferedReader.close();
    		}catch(Exception e) {
    			log.error(e,e);
    		}
		}
    	
    	log.debug("meterId:"+meterId);
    	return meterId;
    }
    
    public File writeLpOfRamDisk(File file) {
    	FileInputStream inputStream = null;
    	FileOutputStream outputStream = null;
    	
    	File copyFile = null;
    	try {
    		String filename = "LP_EM_TOBE_EXT_" + AimirThreadMapper.getInstance().getRecoveryMapperId(Thread.currentThread().getId());
    		copyFile = new File(restorePath, filename);
    		
    		inputStream = new FileInputStream(file);
    		outputStream = new FileOutputStream(copyFile);
    		
    		FileChannel in = inputStream.getChannel();
    		FileChannel out = outputStream.getChannel();
    		
    		long size = in.size();
    		in.transferTo(0, size, out);
    		
    		in.close();
    		out.close();
    	}catch(Exception e) {
    		log.error(e,e);
    	} finally {
    		try {
				if(inputStream != null)
					inputStream.close();
				
				if(outputStream != null)
					outputStream.close();
    		}catch(Exception e) {
    			log.error(e,e);
    		}
		}
    	
    	return copyFile;
    }
    
    //프로시저가 에러가 날 경우에 해당 파일을 스케줄러를 통해 다시
    //실행하기 위한 용도
    public void makeLPOfProcedureERR(String body) {
    	ObjectOutputStream out = null;
    	try {    		
    		File f = null;
            f = new File(backupPath, UUID.randomUUID().toString().replaceAll("-", ""));
    		Writer writer = new BufferedWriter(new OutputStreamWriter(
    				new FileOutputStream(f), "utf-8"));
    		writer.write(body);
    		writer.flush();
    		
    		writer.close();
    		
    	}catch (Exception e) {
            log.error(e,e);
        }
        finally {
            if (out != null) {
                try {
                    out.close();
                }
                catch (Exception e) {
                    log.error(e, e);
                }
            }
        }
    }
    
    

}

