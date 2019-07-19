package com.aimir.mars.integration.bulkreading.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.mars.integration.bulkreading.dao.MDMBatchDataDao;

@Service
@Transactional
public class MDMBatchDataService {
	
	private static final Logger log = LoggerFactory.getLogger(MDMBatchDataService.class);
	
	@Resource(name="transactionManager")
    HibernateTransactionManager txmanager;
	
	@Autowired
	MDMBatchDataDao mdmBatchDataDao;
	
	public void execute() {
		
		try {
			String arrCommand[] = new String[]{"procBatchLpEM","procBatchBillingDayEM","procBatchBillingMonthEM","procBatchMetereventLog","procBatchDeleteData"};
			
        	int threadPoolSize = 5;
        	int _timeout = 300;
        	
        	log.debug("Thread Pool Size : " + threadPoolSize);
        	ExecutorService pool = Executors.newFixedThreadPool(threadPoolSize);
        	batchThread threads[] = new batchThread[arrCommand.length];
		
			for(int i = 0; i < arrCommand.length; i++) {			
				threads[i] = new batchThread(arrCommand[i]);
				pool.execute(threads[i]);	
				
				Thread.sleep(1000); // 1초 sleep
			}
			
			log.info("ExecutorService for mcu shutdown.");
			pool.shutdown();
			log.info("ExecutorService for mcu awaitTermination. [" + _timeout + "]sec");
			if(!pool.awaitTermination(_timeout, TimeUnit.SECONDS)) pool.shutdownNow();
			
		} catch (Exception e) {
			log.error("## [BATCH EXECUTE] error ", e);
		}		
	}	
	
	protected class batchThread extends Thread {
		
		String command = "";
		
		public batchThread(String command) {
			this.command = command;
		}
		
		@Override
    	public void run() {
			
			try {    			
    			log.debug("ThreadID["+ Thread.currentThread().getName() + "] command [" + command + "]");    			
    			
    			if("procBatchLpEM".equals(command)) procBatchLpEM();
    			else if("procBatchBillingDayEM".equals(command)) procBatchBillingDayEM();
    			else if("procBatchBillingMonthEM".equals(command)) procBatchBillingMonthEM();    			
    			else if("procBatchMetereventLog".equals(command)) procBatchMetereventLog();    	
    			else if("procBatchDeleteData".equals(command)) procBatchDeleteData();
    			
			} catch (Exception e) {
				log.error("error", e);				
				return;
			}			
    		return;
		}
	}
	
	public void procBatchLpEM() {
		TransactionStatus txstatus = null;
		try {
			log.info("## [BATCH LpEM] START");
			
			txstatus = txmanager.getTransaction(null);		
			mdmBatchDataDao.procBatchLpEM();			
			if (txstatus != null && !txstatus.isCompleted()) txmanager.commit(txstatus);
			
			log.info("## [BATCH LpEM] END");
		} catch (Exception e) {
			if (txstatus != null) txmanager.rollback(txstatus);
			log.error("## [BATCH LpEM] error ", e);
		}	
	}
	
	public void procBatchBillingDayEM() {
		TransactionStatus txstatus = null;
		try {	
			log.info("##  [BATCH BillingDayEM] START");
			
			txstatus = txmanager.getTransaction(null);		
			mdmBatchDataDao.procBatchBillingDayEM();			
			if (txstatus != null && !txstatus.isCompleted()) txmanager.commit(txstatus);
			
			log.info("##  [BATCH BillingDayEM] END");
		} catch (Exception e) {
			log.error("##  [BATCH BillingDayEM] error ", e);
		}	
	}
	
	public void procBatchBillingMonthEM() {
		TransactionStatus txstatus = null;
		try {	
			log.info("##  [BATCH BillingMonthEM] START");
			
			txstatus = txmanager.getTransaction(null);		
			mdmBatchDataDao.procBatchBillingMonthEM();			
			if (txstatus != null && !txstatus.isCompleted()) txmanager.commit(txstatus);
			
			log.info("##  [BATCH BillingMonthEM] END");
		} catch (Exception e) {
			if (txstatus != null) txmanager.rollback(txstatus);
			log.error("##  [BATCH BillingMonthEM] error ", e);
		}	
	}	
	
	public void procBatchMetereventLog() {
		TransactionStatus txstatus = null;
		try {	
			log.info("##  [BATCH MetereventLog] START");
			
			txstatus = txmanager.getTransaction(null);		
			mdmBatchDataDao.procBatchMetereventLog();			
			if (txstatus != null && !txstatus.isCompleted()) txmanager.commit(txstatus);
			
			log.info("##  [BATCH MetereventLog] END");
		} catch (Exception e) {
			if (txstatus != null) txmanager.rollback(txstatus);
			log.error("##  [BATCH MetereventLog] error ", e);
		}	
	}
	
	public void procBatchDeleteData() {
		TransactionStatus txstatus = null;
		try {	
			log.info("##  [BATCH DELETE DATA] START");
			
			txstatus = txmanager.getTransaction(null);		
			mdmBatchDataDao.procBatchDeleteData();			
			if (txstatus != null && !txstatus.isCompleted()) txmanager.commit(txstatus);
			
			log.info("##  [BATCH DELETE DATA] END");
		} catch (Exception e) {
			if (txstatus != null) txmanager.rollback(txstatus);
			log.error("##  [BATCH DELETE DATA] error ", e);
		}	
	}
}