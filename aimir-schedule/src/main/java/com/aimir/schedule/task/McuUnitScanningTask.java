/**
 * (@)# UnitScanningTask.java
 *
 * 2015. 3. 2.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.schedule.task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.MCUDao;
import com.aimir.fep.command.conf.DefaultConf;
import com.aimir.fep.command.ws.client.CommandWS;
import com.aimir.fep.command.ws.client.ResponseMap;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MCU;
import com.aimir.schedule.command.CmdManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

/**
 * @author nuri
 *
 */
@Transactional
public class McuUnitScanningTask extends ScheduleTask {
	private static Log log = LogFactory.getLog(McuUnitScanningTask.class);

	private final int CORE_POOL_SIZE = 10;      // 15개 이상 생성시 OutOfMemory Erro 발생. 시스템사양에따라 조절할것.
	private final int MAXIMUM_POOL_SIZE = 20;
	private final int KEEP_ALIVE_TIME = 1;
	private final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.MINUTES;
	private final int AWAIT_TIME_OUT = 1;
	private final TimeUnit AWAIT_TIME_OUT_TIME_UNIT = TimeUnit.MINUTES;
	private boolean isNowRunning = false;
	
	List<Map<String, String>> successList = new LinkedList<Map<String, String>>();
	List<Map<String, String>> failList = new LinkedList<Map<String, String>>();
	ThreadPoolExecutor executor = null;
	
	@Resource(name="transactionManager")
	HibernateTransactionManager txManager;

	@Autowired
	MCUDao mcuDao;

	@Override
	public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### McuUnitScanning Task is already running...");
			return;
		}
		
		isNowRunning = true;
		Date startDate = new Date();
		long startTime = startDate.getTime();

		log.info("########### START UnitScanningTask - " + CalendarUtil.getDatetimeString(startDate, "yyyy-MM-dd HH:mm:ss") + " ###############");
		
		successList.clear();
		failList.clear();
		mcuUnitScanningStart();
		
		long endTime = System.currentTimeMillis();
		log.info("MCU Scanning finished - Elapse Time : " + (endTime - startTime) / 1000.0f + "s");
		
		log.info("########### END UnitScanningTask ############");
		isNowRunning = false;
	}

	private void mcuUnitScanningStart() {

		try {
			List<String> mcuPropList = getMcuProperties();
			
			Calendar cal = Calendar.getInstance();
			String currentTime = DateTimeUtil.getDateString(cal.getTime());
			cal.add(Calendar.DAY_OF_MONTH, -1);
			String beforeOneday = DateTimeUtil.getDateString(cal.getTime());
			
			TransactionStatus txStatus = txManager.getTransaction(null);
			Set<Condition> condition = new HashSet<Condition>();
			condition.add(new Condition("lastCommDate", new Object[]{beforeOneday, currentTime}, null, Restriction.BETWEEN));
			List<MCU> mcuList = mcuDao.findByConditions(condition);
			Map<String, String> mcuProtocol = new HashMap<String, String>();
			
			if (mcuList != null) {
			    for (MCU m : mcuList) {
			        mcuProtocol.put(m.getSysID(), m.getProtocolType().getName());
			    }
			}
			else {
                log.info("MCU List is null. please check your MCU list or Serach Type.");
            }
			
			txManager.commit(txStatus);

			log.info("MCU_SID Total List (" + mcuProtocol.size() + ")");

			// MCU List
			Collection<CallableTask> callList = new LinkedList<CallableTask>();

			//int count = 0;
			String sysId = null;
			for (Iterator<String> i = mcuProtocol.keySet().iterator(); i.hasNext(); ) {
			//	if(200 <= count) break;
			    sysId = i.next();
				callList.add(new CallableTask(sysId, mcuPropList, mcuProtocol.get(sysId)));
				
				//count++;
			}
			//log.info("########### DCU COUNT ==> " + count);

			// Excute Job.
			executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>());
			List<Future<Map<String, String>>> futureL = executor.invokeAll(callList, 3, TimeUnit.MINUTES);

			TimeUnit.SECONDS.sleep(5);
			for (Future<Map<String, String>> resultFuture : futureL) {
				Map<String, String> ht = resultFuture.get(AWAIT_TIME_OUT, AWAIT_TIME_OUT_TIME_UNIT); // the maximum time to wait
				if (resultFuture.isDone()) {
					if (ht.containsKey("error_create") || ht.containsKey("STATUS")) { // Scanning Fail.
						failList.add(ht);
					} else {                                                          // Scanning Success.
						successList.add(ht);
					}
				} else if (resultFuture.isCancelled()) {
					log.info("###### Future is Cancelled ==> " + ht.get("SCANNING_MCU_SID"));
					failList.add(ht);
				} else {
					Map<String, String> unknownCancelMap = new HashMap<String, String>();
					unknownCancelMap.put("ERROR", resultFuture.toString());
					failList.add(unknownCancelMap);
				}
			}				

			/**
			 * Printing
			 */
			log.info("=========== SCAN SUCCESS LIST (" + successList.size() + ") ==========");
			if (successList != null && 0 < successList.size()) {
				for (Map<String, String> sMap : successList) {
					log.info(sMap.toString()); // 파일 생성
				}
			} else {
				log.info("There is no list of successful."); // 파일 생성
			}
			log.info("========================================");

			log.info("===========  SCAN FAIL LIST (" + failList.size() + ")==========");
			if (failList != null && 0 < failList.size()) {
				for (Map<String, String> fMap : failList) {
					log.info(fMap.toString()); // 파일 생성
				}
			} else {
				log.info("There is no list of failure."); // 파일 생성
			}
			log.info("========================================");
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			if (executor != null) {
				executor.shutdown();
				try {
					if (!executor.awaitTermination(AWAIT_TIME_OUT, TimeUnit.SECONDS)) {
						executor.shutdownNow();
						if (!executor.awaitTermination(AWAIT_TIME_OUT, TimeUnit.SECONDS)) {
							log.error("Pool did not terminate");
						}
					}
				} catch (InterruptedException ie) {
					executor.shutdownNow();
					Thread.currentThread().interrupt();
				}
			}
		}
		log.info("END.");
		// System.exit(0);
	}

	@SuppressWarnings("rawtypes")
	private List<String> getMcuProperties() throws Exception {
		// MCU Property
		DefaultConf defaultConf = DefaultConf.getInstance();
		Hashtable props = defaultConf.getDefaultProperties("MCU");
		List<String> propertys = new ArrayList<String>();

		Iterator it = props.keySet().iterator();
		while (it.hasNext()) {
			propertys.add((String) it.next());
		}

		return propertys;
	}
}

class CallableTask implements Callable<Map<String, String>> {
	private static Log log = LogFactory.getLog(CallableTask.class);
	String mcuId;
	List<String> mcuProperties;
	String protocolType;

	public CallableTask(String mcuId, List<String> mcuProperties, String protocolType) {
		this.mcuId = mcuId;
		this.mcuProperties = mcuProperties;
		this.protocolType = protocolType;
	}

	@SuppressWarnings("unused")
	@Override
	public Map<String, String> call() throws Exception {
		log.debug("[" + mcuId + "] Scanning Starting...");

		Map<String, String> result = new LinkedHashMap<String, String>();
		result.put("SCANNING_MCU_SID", mcuId);
		HibernateTransactionManager txManager = (HibernateTransactionManager)DataUtil.getBean("transactionManager");
		TransactionStatus txStatus = null;
		try {

			/**
			 * fep tool에서 사용.
				CommandGW cgw = DataUtil.getBean(CommandGW.class);
				Map<String, String> scanResult = cgw.cmdMcuScanning(mcuId, property);
				if (scanResult == null) {
					result.put("STATUS", "SCAN_FAIL");
				} else {
					Set<String> it = scanResult.keySet();
					for (String key : it) {
						result.put(key, scanResult.get(key));
						log.info("key => [" + key + "] value => [" + scanResult.get(key) + "]");
					}
				} 
			*/

			/**
			 * could not initialize proxy - no Session 에러남. 
			 * CmdOperationUtil cmdOperationUtil = DataUtil.getBean(CmdOperationUtil.class);
			 * Map<String, String> scanResult = cmdOperationUtil.doMCUScanning(mcu);
			 */
			CommandWS gw = CmdManager.getCommandWS(protocolType);
			ResponseMap scanResult = gw.cmdStdGet1(mcuId, mcuProperties);
			List<ResponseMap.Response.Entry> entries = scanResult.getResponse().getEntry();
			if (scanResult == null) {
				result.put("STATUS", "SCAN_FAIL");
			} else {
			    txStatus = txManager.getTransaction(null);
			    MCUDao mcuDao = DataUtil.getBean(MCUDao.class);
			    MCU mcu = mcuDao.get(mcuId);
				for (ResponseMap.Response.Entry e : entries) {
					result.put((String) e.getKey(), (String) e.getValue());
					log.debug("key => [" + (String) e.getKey() + "] value => [" + (String) e.getValue() + "]");
					if (e.getValue() != null) {
	                    BeanUtils.copyProperty(mcu, (String)e.getKey(), e.getValue());
					}
				}
				mcuDao.update(mcu);
				txManager.commit(txStatus);
			}
			log.debug("Scanning result ==> " + result.toString());
		} catch (NullPointerException e) {
		    if (txStatus != null) {
		        txManager.rollback(txStatus);
		    }
			result.put("error_create", e.getMessage());
		} catch (Exception e) {
		    if (txStatus != null) {
                txManager.rollback(txStatus);
            }
			result.put("error_create", e.getMessage());
		}

		return result;
	}

}