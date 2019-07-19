/**
 * (@)# TelitModuleUpgradeTask.java
 *
 * 2018. 8. 8.
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.OperationLog;
import com.aimir.model.system.Code;
import com.aimir.model.system.Location;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.schedule.util.SAPProperty;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;

/**
 * SP-982 [HES] MBB 모뎀 버전 1.5 이상에서의 기능 추가 건 중 3. Telit 모듈 업그레이드 기능(FOTA) 추가
 * 항목에의해 개발
 * 
 * @author Jiwoong Park (wll27471297)
 */
@Service
public class TelitModuleUpgradeTask extends ScheduleTask {
	private static Logger logger = LoggerFactory.getLogger(TelitModuleUpgradeTask.class);

	@Resource(name = "transactionManager")
	HibernateTransactionManager txmanager;

	@Autowired
	LocationDao locationDao;

	@Autowired
	CodeDao codeDao;

	@Autowired
	MMIUDao mmiuDao;

	@Autowired
	CmdOperationUtil cmdOperationUtil;

	@Autowired
	OperationLogDao operationLogDao;
	
	@Autowired
	AsyncCommandLogDao asyncCommandLogDao;

	@Autowired
	AsyncCommandParamDao asyncCommandParamDao;
	
	// Task Parameter
	private static String mode;
	private static String tagetLocation;
	private static String targetListFilePath;
	private static int maxThreadWorker;
	private static int timeout;
	private static int sleep;
	private Code modemOTACode;
	private Code targetTypeMMIU;

	// Command Parameter
	private static String ftpUrl;
	private static String ftpPort;
	private static String ftpDirectory;
	private static String targetFile_build4;
	private static String targetFile_build8;
	private static String username;
	private static String password;
	private static boolean async;

	private static Code DELETE;

	private static String finalModuleRevision = "20.00.403-3";
	private static Double finalFwVersion = 1.6d;

	private List<MMIU> targetList = new LinkedList<>();

	public static void main(String[] args) {
		logger.info("-----");
		logger.info("-----");
		logger.info("-----");
		logger.info("#### TelitModuleUpgradeTask start. ###");

		try {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "spring-FOTAForSORIA.xml" });
			DataUtil.setApplicationContext(ctx);

			TelitModuleUpgradeTask task = (TelitModuleUpgradeTask) ctx.getBean(TelitModuleUpgradeTask.class);
			task.execute(null);

		} catch (Exception e) {
			logger.error("TelitModuleUpgradeTask excute error - " + e, e);
		} finally {
			logger.info("#### TelitModuleUpgradeTask Task finished. ###");

			System.exit(0);
		}
	}

	@Override
	public void execute(JobExecutionContext context) {
		TransactionStatus txstatus = null;
		txstatus = txmanager.getTransaction(null);

		try {
			// Set Delete Code
			DELETE = codeDao.getCodeIdByCodeObject("1.3.3.9");
			modemOTACode = codeDao.getCodeIdByCodeObject("8.2.6");
			targetTypeMMIU = codeDao.getCodeIdByCodeObject("7.13.16");

			mode = SAPProperty.getProperty("modem.mbb.fota.mode", "FILE");
			targetListFilePath = SAPProperty.getProperty("modem.mbb.fota.targets.filepath", "fotaTargets.txt");
			maxThreadWorker = Integer.parseInt(SAPProperty.getProperty("modem.mbb.fota.maxworker", "10"));
			timeout = Integer.parseInt(SAPProperty.getProperty("modem.mbb.fota.timeout", "3600"));
			sleep = Integer.parseInt(SAPProperty.getProperty("modem.mbb.fota.sleep", "60000"));
			tagetLocation = SAPProperty.getProperty("modem.mbb.fota.location");
			ftpUrl = SAPProperty.getProperty("modem.mbb.fota.ftp.url","10.40.200.47");
			ftpPort = SAPProperty.getProperty("modem.mbb.fota.ftp.port","21");
			ftpDirectory = SAPProperty.getProperty("modem.mbb.fota.ftp.directory","fw/temp");
			targetFile_build4 = SAPProperty.getProperty("modem.mbb.fota.ftp.targetfile.build4", "20.00.402.4_20.00.403.3_UpdPkg_LE910_EU_V2_1G_20.00.402_20.00.403.bin");
			targetFile_build8 = SAPProperty.getProperty("modem.mbb.fota.ftp.targetfile.build8", "UpdPkg_LE910_EU_V2_1G_20.00.402.8_20.00.403.3.bin");
			username = SAPProperty.getProperty("modem.mbb.fota.ftp.username","aimirftp");
			password = SAPProperty.getProperty("modem.mbb.fota.ftp.password","aimiramm");
			String asyncProp = SAPProperty.getProperty("modem.mbb.fota.async","false");
			async = (asyncProp.toLowerCase().equals("true")) ? true : false;
			
			// Get Target List By Mode
			if (mode.equals("DSO"))
				targetList = getTargetListFromDso();
			else if (mode.equals("FILE"))
				targetList = getTargetListFromFile();

			logger.info("mode : {}", mode);
			logger.info("targetListFilePath : {}", targetListFilePath);
			logger.info("maxThreadWorker : {}", maxThreadWorker);
			logger.info("timeout : {}", timeout);
			logger.info("sleep : {}", sleep);
			logger.info("tagetLocation : {}", tagetLocation);
			logger.info("ftpUrl : {}", ftpUrl);
			logger.info("ftpPort : {}", ftpPort);
			logger.info("ftpDirectory : {}", ftpDirectory);
			logger.info("targetFile_build4 : {}", targetFile_build4);
			logger.info("targetFile_build8 : {}", targetFile_build8);
			logger.info("username : {}", username);
			logger.info("password : {}", password);
			logger.info("async : {}", async);
			logger.info("targetList : {}", targetList);

			// Check TargetList
			if (targetList == null || (targetList.size() <= 0)) {
				logger.error("There is no Device. Please check agin.");
				return;
			}

			commandFOTAStart();

		} catch (Exception e) {
			if (txstatus != null)
				txmanager.rollback(txstatus);
			logger.error("Task Excute transaction error - " + e, e);
			return;
		}
		if (txstatus != null)
			txmanager.commit(txstatus);
	}

	@SuppressWarnings("unused")
	private List<MMIU> getTargetListFromFile() {
		List<String> targetList = null;
		List<MMIU> targetListMMIUObj = new ArrayList<>();

		// Get device serial list from file
		InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(targetListFilePath);
		if (fileInputStream != null) {
			targetList = new LinkedList<String>();

			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(fileInputStream);
			while (scanner.hasNextLine()) {
				String target = scanner.nextLine().trim();
				if (!target.equals("")) {
					targetList.add(target);
				}
			}
			logger.debug("Target List({}) ===> {}", targetList.size(), targetList.toString());
		} else {
			logger.debug("[{}] file not found", targetListFilePath);
		}

		// Get MMIU object list
		// Default filter : ModemType == MMIU && FWVersion >= 1.6
		MMIU modem = null;
		for (String deviceSerial : targetList) {
			modem = mmiuDao.findByCondition("deviceSerial", deviceSerial);

			// Filter (S)
			if (modem == null) {
				logger.error("DeviceSerial[{}] is not exist.", deviceSerial);
				continue;
			}
			if (modem.getModemStatus().equals(DELETE)) {
				logger.error("DeviceSerial[{}] is deleted.", deviceSerial);
				continue;
			}
			if (!modem.getModemType().equals(ModemType.MMIU)) {
				logger.error("DeviceSerial[{}]'s type is not MMIU.", deviceSerial);
				continue;
			}
			if (modem.getFwVer() == null || "".equals(modem.getFwVer())) {
				logger.error("DeviceSerial[{}]'s firmware version is null");
				continue;
			}
			if (Double.parseDouble(modem.getFwVer()) < finalFwVersion) {
				logger.error("DeviceSerial[{}]'s firmware version is not over 1.6. It's {}", deviceSerial,
						modem.getFwVer());
				continue;
			}
			if (modem.getModuleRevision().equals(finalModuleRevision)) {
				logger.error("DeviceSerial[{}]'s module version is already upgraded. It's {}", deviceSerial,
						modem.getModuleRevision());
				continue;
			}
			// Filter (E)

			logger.debug("MMIU[" + modem.getDeviceSerial() + "]");
			targetListMMIUObj.add(modem);
		}

		return targetListMMIUObj;
	}

	private List<MMIU> getTargetListFromDso() {
		List<MMIU> targetList = null;
		Location loc = locationDao.findByCondition("name", tagetLocation);

		Set<Condition> condition = new LinkedHashSet<Condition>();
		condition.add(new Condition("locationId", new Object[] { loc.getId() }, null, Restriction.EQ));
		condition.add(new Condition("modemType", new Object[] { ModemType.MMIU }, null, Restriction.EQ));
		// condition.add(new Condition("fwVersion", new Object[]{ finalFwVersion },
		// null, Restriction.GE));
		condition.add(new Condition("moduleRevision", new Object[] { finalModuleRevision }, null, Restriction.NEQ));
		condition.add(new Condition("modemStatus", new Object[] { DELETE }, null, Restriction.NEQ));

		targetList = mmiuDao.findByConditions(condition);

		return targetList;
	}

	private void saveAsyncFOTA(MMIU target) {
		try {
			// Make trId
			long trId = System.currentTimeMillis();
			AsyncCommandLog asyncCommandLog = new AsyncCommandLog();
			
			// AsyncCommandLog 생성
			asyncCommandLog.setTrId(trId);
			asyncCommandLog.setCommand("cmdFOTA");
			asyncCommandLog.setTrOption(TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode());
			asyncCommandLog.setDeviceType(ModemType.MMIU.name());
			asyncCommandLog.setMcuId(target.getDeviceSerial());
			asyncCommandLog.setDeviceId(target.getDeviceSerial());
			asyncCommandLog.setOperator(OperatorType.OPERATOR.name());
			asyncCommandLog.setRequestTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			asyncCommandLog.setCreateTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			asyncCommandLog.setLastTime(null);
			asyncCommandLog.setInitTry(3);
			asyncCommandLog.setInitNice(0); // 우선순위
			asyncCommandLog.setDay(0); // 보관일수
			asyncCommandLog.setState(TR_STATE.Waiting.getCode()); // 0x01
			asyncCommandLogDao.add(asyncCommandLog);
			logger.debug(asyncCommandLog.toJSONString());
			
			// AsyncCommandLog에 대한 Param 생성
			int num = 0;
			String targetFile = "";
			if(target.getModuleRevision().equals("20.00.402-4")) {
				targetFile = targetFile_build4;
			}else if(target.getModuleRevision().equals("20.00.402-8")) {
				targetFile = targetFile_build8;
			}
			AsyncCommandParam asyncCommandParam = new AsyncCommandParam();
			asyncCommandParam.setParamType("CommandCode");
			asyncCommandParam.setParamValue(COMMAND_TYPE.NI.getTypeCode());
			asyncCommandParam.setTrType("CommandCode");
			asyncCommandParam.setMcuId(target.getDeviceSerial());
			asyncCommandParam.setNum(num++);
			asyncCommandParam.setTrId(trId);
	        asyncCommandParamDao.add(asyncCommandParam);
			logger.debug(asyncCommandParam.toJSONString());
			
			asyncCommandParam = new AsyncCommandParam();
			asyncCommandParam.setParamType("deviceSerial");
			asyncCommandParam.setParamValue(target.getDeviceSerial());
			asyncCommandParam.setTrType("CMD");
			asyncCommandParam.setMcuId(target.getDeviceSerial());
			asyncCommandParam.setNum(num++);
			asyncCommandParam.setTrId(trId);
	        asyncCommandParamDao.add(asyncCommandParam);
			logger.debug(asyncCommandParam.toJSONString());
	        
	        asyncCommandParam = new AsyncCommandParam();
			asyncCommandParam.setParamType("ftpUrl");
			asyncCommandParam.setParamValue(ftpUrl);
			asyncCommandParam.setTrType("CMD");
			asyncCommandParam.setMcuId(target.getDeviceSerial());
			asyncCommandParam.setNum(num++);
			asyncCommandParam.setTrId(trId);
	        asyncCommandParamDao.add(asyncCommandParam);
			logger.debug(asyncCommandParam.toJSONString());
	        
	        asyncCommandParam = new AsyncCommandParam();
			asyncCommandParam.setParamType("ftpPort");
			asyncCommandParam.setParamValue(ftpPort);
			asyncCommandParam.setTrType("CMD");
			asyncCommandParam.setMcuId(target.getDeviceSerial());
			asyncCommandParam.setNum(num++);
			asyncCommandParam.setTrId(trId);
	        asyncCommandParamDao.add(asyncCommandParam);
			logger.debug(asyncCommandParam.toJSONString());
	        
	        asyncCommandParam = new AsyncCommandParam();
			asyncCommandParam.setParamType("ftpDirectory");
			asyncCommandParam.setParamValue(ftpDirectory);
			asyncCommandParam.setTrType("CMD");
			asyncCommandParam.setMcuId(target.getDeviceSerial());
			asyncCommandParam.setNum(num++);
			asyncCommandParam.setTrId(trId);
	        asyncCommandParamDao.add(asyncCommandParam);
			logger.debug(asyncCommandParam.toJSONString());
	        
	        asyncCommandParam = new AsyncCommandParam();
			asyncCommandParam.setParamType("targetFile");
			asyncCommandParam.setParamValue(targetFile);
			asyncCommandParam.setTrType("CMD");
			asyncCommandParam.setMcuId(target.getDeviceSerial());
			asyncCommandParam.setNum(num++);
			asyncCommandParam.setTrId(trId);
	        asyncCommandParamDao.add(asyncCommandParam);
			logger.debug(asyncCommandParam.toJSONString());
	        
	        asyncCommandParam = new AsyncCommandParam();
			asyncCommandParam.setParamType("username");
			asyncCommandParam.setParamValue(username);
			asyncCommandParam.setTrType("CMD");
			asyncCommandParam.setMcuId(target.getDeviceSerial());
			asyncCommandParam.setNum(num++);
			asyncCommandParam.setTrId(trId);
	        asyncCommandParamDao.add(asyncCommandParam);
			logger.debug(asyncCommandParam.toJSONString());
	        
	        asyncCommandParam = new AsyncCommandParam();
			asyncCommandParam.setParamType("password");
			asyncCommandParam.setParamValue(password);
			asyncCommandParam.setTrType("CMD");
			asyncCommandParam.setMcuId(target.getDeviceSerial());
			asyncCommandParam.setNum(num++);
			asyncCommandParam.setTrId(trId);
	        asyncCommandParamDao.add(asyncCommandParam);
			logger.debug(asyncCommandParam.toJSONString());
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
        
	}
	private void commandFOTAStart() {
		try {
			logger.debug("maxThreadWorker[" + maxThreadWorker + "] >  targetList.size()[" + targetList.size() + "] : "
					+ (maxThreadWorker > targetList.size()));
			int threadPoolSize = maxThreadWorker > targetList.size() ? targetList.size() : maxThreadWorker;
			logger.debug("Thread Pool Size : " + threadPoolSize);
			ExecutorService pool = Executors.newFixedThreadPool(threadPoolSize);
			FotaThread threads[] = new FotaThread[targetList.size()];
			int i = 0;
			logger.debug("ASYNC [" + async + "]");
			for (MMIU target : targetList) {
				if(async) {
					saveAsyncFOTA(target);
				}else {
					logger.debug("Create Thread to FOTA MODEM[" + target.getDeviceSerial() + "]");
					threads[i] = new FotaThread(target);
					pool.execute(threads[i]);
					i++;
					Thread.sleep(sleep);
				}
			}
			logger.info("ExecutorService for FOTA shutdown.");
			pool.shutdown();
			logger.info("ExecutorService for FOTA awaitTermination. [" + timeout + "]sec");
			if (!pool.awaitTermination(timeout, TimeUnit.SECONDS))
				pool.shutdownNow();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected class FotaThread extends Thread {
		private MMIU modem;

		public FotaThread(MMIU modem) {
			this.modem = modem;
		}

		@Override
		public void run() {
			logger.info("FotaThread ThreadID[" + Thread.currentThread().getName() + "] RUN!! MODEM["
					+ modem.getDeviceSerial() + "]");

			TransactionStatus txstatus = null;
			txstatus = txmanager.getTransaction(null);
			
			ResultStatus resultStatus = ResultStatus.FAIL;
			OperationLog log = new OperationLog();
			log.setOperatorType(OperatorType.SYSTEM.getCode());
			log.setOperationCommandCode(modemOTACode);
			log.setYyyymmdd(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd"));
			log.setHhmmss(DateTimeUtil.getCurrentDateTimeByFormat("HHmmss"));
			log.setYyyymmddhhmmss(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			log.setDescription("Telit Upgrade Operation Log by TelitModuleUpgradeTask.");
			log.setResultSrc("");
			log.setTargetName(modem.getDeviceSerial());
			log.setTargetTypeCode(targetTypeMMIU);
			log.setUserId("admin");
			log.setSupplier(modem.getSupplier());
			
			String targetFile = "";
			try {
				if(modem.getModuleRevision() == null || "".equals(modem.getModuleRevision())) {
					logger.warn("current modem module revision is empty ");
					return;
				}

				if(modem.getModuleRevision().equals("20.00.402-4")) {
					targetFile = targetFile_build4;
				}else if(modem.getModuleRevision().equals("20.00.402-8")) {
					targetFile = targetFile_build8;
				}else {
					logger.warn("current modem module revision is not target "+ modem.getModuleRevision());
					return;
				}
				Map<String, Object> map = new HashMap<String, Object>();
				
				map = cmdOperationUtil.cmdFOTA(modem, ftpUrl, ftpPort, ftpDirectory, targetFile, username, password);
				logger.debug("map : " + map);
				if (map != null && !map.isEmpty() && map.get("ftpUrl").toString().equals(ftpUrl) && map.get("ftpPort").toString().equals(ftpPort)
						&& map.get("ftpDirectory").toString().equals(ftpDirectory) && map.get("targetFile").toString().equals(targetFile)
						&& map.get("username").toString().equals(username) && map.get("password").toString().equals(password)) {
					resultStatus = ResultStatus.SUCCESS;
				}
			} catch (Exception e) {
				e.getStackTrace();
			}finally {
				logger.debug("OperationLog : " + log);
				log.setErrorReason("FOTA Request - " + resultStatus.name());
				log.setStatus(resultStatus.getCode());
				operationLogDao.add(log);
				if (txstatus != null)
					txmanager.commit(txstatus);
				logger.info("FotaThread ThreadID[" + Thread.currentThread().getName() + "] END!! MODEM["+ modem.getDeviceSerial() + "]");
			}
			return;
		}
	}

}
