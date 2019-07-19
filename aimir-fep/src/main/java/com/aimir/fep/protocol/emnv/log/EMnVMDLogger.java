package com.aimir.fep.protocol.emnv.log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.meter.data.MDHistoryData;
import com.aimir.fep.protocol.fmp.log.MessageLogger;

public class EMnVMDLogger extends MessageLogger {
	private static Logger log = LoggerFactory.getLogger(EMnVMDLogger.class);

	/**
	 * Constructs a FileMDLogger object
	 */
	private EMnVMDLogger() throws IOException {
		super();
	}

	@Override
	public String writeObject(Serializable obj) {
		log.debug("EMnVMDLogger() Object ==> {}", obj.toString());
		try {
			if (obj instanceof MDHistoryData) {
				MDHistoryData mdhd = (MDHistoryData) obj;
				File f = null;
				f = new File(logDirName, "EMnVMDLog-" + mdhd.getMcuId() + "-" + System.currentTimeMillis() + ".log");
				ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
				os.writeObject(mdhd);
				os.close();
			}
		} catch (Exception e) {
			log.error("******** " + getClass().getName() + " write() Failed *********", e);
		}
		return null;
	}

	@Override
	public void backupObject(Serializable obj) {

		try {
			if (obj instanceof MDHistoryData) {
				MDHistoryData mdhd = (MDHistoryData) obj;
				File f = null;
				f = new File(getBackupDir(), "EMnVMDLog-" + mdhd.getMcuId() + "-" + System.currentTimeMillis() + ".log");
				ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
				os.writeObject(mdhd);
				os.close();
			}
		} catch (Exception e) {
			log.error("********" + getClass().getName() + " backup() Failed *********", e);
		}
	}
}
