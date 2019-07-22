package com.aimir.fep.protocol.fmp.log;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.File;

import com.aimir.fep.meter.data.MDHistoryData;
import com.aimir.fep.protocol.fmp.frame.service.MDData;

/**
 * ExternalTableLogger.java
 *
 * Logger which logs to a file
 *
 * Created: 2019.01.28
 *
 * @author  seungwoo.Han
 * @version 0.1
 */
public class ExternalTableLogger extends MessageLogger {
    private static Log log = LogFactory.getLog(ExternalTableLogger.class);
    
    /**
     * Constructs a FileMDLogger object
     */
    public ExternalTableLogger() throws IOException {
        super();
        // 2017.03.24 SP-629
        logDirName = "/mnt";
    }

    public String writeObject(Serializable obj, String path) {
    	return null;
    }
    
    public boolean getExistFile(String fileName) {
    	try {
    		File f = new File(logDirName, fileName);
    		
    		if(f.exists()) {
    			return true;
    		}
    		
    		return false;
    	}catch(Exception e) {
    		log.error(e,e);
    	}
    	
    	return false;
    }
    
    public void writeObject(String fileName, String body) {
    	ObjectOutputStream out = null;
    	try {    		
    		File f = null;
            f = new File(logDirName, fileName);
    		Writer writer = new BufferedWriter(new OutputStreamWriter(
    				new FileOutputStream(f), "utf-8"));
    		writer.write(body);
    		writer.flush();
    		
    		writer.close();
    		
    	}catch (Exception e) {
            log.error("********" + getClass().getName()+ " write() Failed *********",e);
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
    
    @Override
    public String writeObject(Serializable obj) {
        ObjectOutputStream out = null;
        try
        {
            String mcuId = null;
            // 2017.03.24 SP-629
            if (obj instanceof MDData) {
                MDData mdData = (MDData)obj;
                mcuId = mdData.getMcuId();
            }
            else if (obj instanceof MDHistoryData) {
                MDHistoryData mdhd = (MDHistoryData)obj;
                mcuId = mdhd.getMcuId();
            }
            
            if(mcuId == null || "".equals(mcuId)) {
            	mcuId = "127.0.0.1";
            }
            
            if (mcuId != null) {
                File f = null;
                f = new File(logDirName,"MDLog-" + mcuId + "-"
                        +UUID.randomUUID()+".log");
                out = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(f)));
                out.writeObject(obj);
                out.reset();
                return f.getAbsolutePath();
            }
            else {
                log.warn("Serializable is not MDData or MDHistory");
            }
        }
        catch (Exception e) {
            log.error("********" + getClass().getName()
                    + " write() Failed *********",e);
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
        return null;
    }

    public void deleteFile(String fileName) {
    	try {    		
    		File f = null;
            f = new File(logDirName, fileName);
    		
            if(f != null) {
	            Path file = f.toPath();
	            log.debug("file path:"+file.toString());
	            Files.delete(file);
            }
    		
    	}catch (Exception e) {
            log.error("********" + getClass().getName()+ " write() Failed *********",e);
        }
    }
    
    @Override
    public void backupObject(Serializable obj) {

        try
        {
            if (obj instanceof MDHistoryData) {
                MDHistoryData mdhd = (MDHistoryData)obj;
                File f = null;
                f = new File(getBackupDir(),"MDLog-" + mdhd.getMcuId() + "-"
                        +System.currentTimeMillis()+".log");
                ObjectOutputStream os = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(f)));
                os.writeObject(mdhd);
                os.reset();
                os.close();
            }
        }
        catch (Exception e) {
            log.error("********" + getClass().getName() + " backup() Failed *********", e);
        }
    }
}

