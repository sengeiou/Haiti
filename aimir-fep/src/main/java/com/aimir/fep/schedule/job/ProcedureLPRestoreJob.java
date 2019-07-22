package com.aimir.fep.schedule.job;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.fep.logger.AimirThreadMapper;
import com.aimir.fep.protocol.fmp.log.ProcedureRecoveryLogger;
import com.aimir.fep.protocol.fmp.processor.Processor;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.system.MeterConfig;

public class ProcedureLPRestoreJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(ProcedureLPRestoreJob.class);

	private static boolean isRunning = false;
	
	public ProcedureLPRestoreJob() {}
	
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
	
		try {
			if(!isRunning) {
				isRunning = true;
				LPProcessor processor = new LPProcessor();
				processor.restore();
			}
		}catch(Exception e) {
			log.error(e);
		} finally {
			isRunning = false;
		}
	}
	
	public class LPProcessor extends Processor implements Runnable {

	    @Resource(name="transactionManager")
	    protected JpaTransactionManager txmanager;
		
	    @Autowired
	    private ProcedureRecoveryLogger prLogger;
	    
	    @Autowired
	    private LpEMDao lpEMDao;
	    
	    @Autowired
	    private MeterDao meterDao;
	    
	    private File file = null;
	    	    	    	    
	    public LPProcessor() throws Exception {
	    	init();
	    }
	    
	    public LPProcessor(File file) throws Exception {
	    	init();
	    	
	    	this.file = file;
	    }
	    
	    public void init() throws Exception {
	    	if(txmanager == null)
	    		txmanager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
	    	
	    	if(prLogger == null)
	    		prLogger = DataUtil.getBean(ProcedureRecoveryLogger.class);
	    	
	    	if(lpEMDao == null)
	    		lpEMDao = DataUtil.getBean(LpEMDao.class);
	    	
	    	if(meterDao == null)
	    		meterDao = DataUtil.getBean(MeterDao.class);
	    	
	    	if(txmanager == null || prLogger == null || lpEMDao == null || meterDao == null){
	    		throw new Exception("Essential parameter is null!!");
	    	}	
	    }
	    
		@Override
		public void run() {
			TransactionStatus txStatus = null;
			
			try {
				txStatus = txmanager.getTransaction(null);
				
				File copyFile = prLogger.writeLpOfRamDisk(file);
				if(copyFile != null) {
					//미터정보를 알아오자.
					Meter meter = meterDao.get(prLogger.getMeterIdByFile(file));
					if(meter != null) {
						MeterConfig meterConfig = (MeterConfig)meter.getModel().getDeviceConfig();
						
						if(meterConfig != null) {
							String mappingID = AimirThreadMapper.getInstance().getRecoveryMapperId(Thread.currentThread().getId());
							
							Map<String, Object> parameter = new HashMap<String, Object>();
							parameter.put("PROCEDURE_NAME", "LP_EXTERNAL_MERGE");
							
							parameter.put("THREAD_NUM", mappingID);
							String procedureReuslt = lpEMDao.callProcedure(parameter);
							
							log.debug("procedureReuslt:"+procedureReuslt+" | mappingID:"+mappingID+" | meter:"+meter.getMdsId());
							AimirThreadMapper.getInstance().deleteRecoveryMapperId(Thread.currentThread().getId());
							
							if(!procedureReuslt.contains("ERROR")) {
								Path path = file.toPath();
								Files.delete(path);
								
								path = copyFile.toPath();
								Files.delete(path);
							}
						} else {
							log.debug("meterConfig is null!!!");
						}
					}
				}
				
				txmanager.commit(txStatus);
			}catch(Exception e) {
				if (txmanager != null)
					txmanager.rollback(txStatus);
				log.error(e,e);				
			}
		}

		@Override
		public int processing(Object obj) throws Exception {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void restore() throws Exception {
			ExecutorService pool = Executors.newFixedThreadPool(10);
			
			if(prLogger.isReadableRecoveryObject()) {
				ArrayList<File> list = prLogger.readRecoveryObject();
							
				log.debug("# list Len : "+list.size());
				if(list != null && list.size() > 0) {
					for(File file : list){
						LPProcessor por = new LPProcessor(file);
						pool.execute(por);
					}
					
					Thread.sleep(2 * 1000);
					
					pool.shutdown();
				}
			}
		}
		
		
	}
}
