package com.aimir.fep.protocol.fmp.log;

import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.io.IOException;
import java.io.File;

import com.aimir.fep.meter.data.NDHistoryData;

/**
 * FileNDLogger.java
 *
 * Logger which logs to a file
 *
 * Created: Thu Dec 09 15:51:37 1999
 *
 * @author  Dongjin Park
 * @version 0.1
 */
public class NDLogger extends MessageLogger { 
    /**
     * Constructs a FileNDLogger object
     */ 
    private NDLogger() throws IOException { 
        super();
    } 

    @Override
    public String writeObject(Serializable obj) {

        try 
        { 
            if (obj instanceof NDHistoryData) {
                NDHistoryData ndhd = (NDHistoryData)obj;
                File f = null;
                f = new File(logDirName,"NDLog-" + ndhd.getMcuId() + "-"
                        +System.currentTimeMillis()+".log");
                ObjectOutputStream os = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(f)));
                os.writeObject(ndhd);
                os.reset();
                os.close();
            }
        } catch (Exception e) {     
            log.error("********" + getClass().getName() 
                    + " write() Failed *********",e); 
        } 
        return null;
    }

    @Override
    public void backupObject(Serializable obj) {

        try
        {
            if (obj instanceof NDHistoryData) {
                NDHistoryData ndhd = (NDHistoryData)obj;
                File f = null;
                f = new File(getBackupDir(),"NDLog-" + ndhd.getMcuId() + "-"
                        +System.currentTimeMillis()+".log");
                ObjectOutputStream os = new ObjectOutputStream(
                    new BufferedOutputStream(new FileOutputStream(f)));
                os.writeObject(ndhd);
                os.reset();
                os.close();
            }
        }
        catch (Exception e) {
            log.error("********" + getClass().getName() + " backup() Failed *********", e);
        }
    }
}

