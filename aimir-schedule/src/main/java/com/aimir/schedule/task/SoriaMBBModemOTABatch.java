package com.aimir.schedule.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.OTAExecuteType;
import com.aimir.constants.CommonConstants.OTATargetType;
import com.aimir.dao.device.FirmwareDao;
import com.aimir.dao.device.FirmwareIssueDao;
import com.aimir.dao.device.FirmwareIssueHistoryDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.Firmware;
import com.aimir.model.device.FirmwareIssue;
import com.aimir.model.device.FirmwareIssueHistory;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

@Service
public class SoriaMBBModemOTABatch extends ScheduleTask {
	private static Logger logger = LoggerFactory.getLogger(SoriaMBBModemOTABatch.class);

	@Resource(name = "transactionManager")
	HibernateTransactionManager txmanager;

	@Autowired
	FirmwareDao firmwareDao;

	@Autowired
	FirmwareIssueDao firmwareIssueDao;

	@Autowired
	FirmwareIssueHistoryDao firmwareIssueHistoryDao;

	@Autowired
	CmdOperationUtil cmdOperationUtil;

	@Autowired
	CodeDao dao;

	@Autowired
	OperationLogDao operationLogDao;

	@Autowired
	LocationDao locationDao;

	@Autowired
	ModemDao modemDao;

	@Autowired
	DeviceModelDao deviceModelDao;

	static String issueDate;
	private String defaultLastLinkTimeRange = "3";
	private String defaultReTryCount = "2";
	private String defaultReTryInterval = "3";
	private final String deviceModelName = "NAMR-P117LT";
	private final String toTargetProperty = "schedule.jmxrmi";

	private static Map<String, String> argMap;

	List<String> deviceList = new ArrayList<String>();

	@Override
	public void execute(JobExecutionContext context) {
		String locationName = argMap.get("location");
		String targetFWVersion = null;
		if(argMap.get("targetFWVersion") != null && !argMap.get("targetFWVersion").equals("")) {
			targetFWVersion = argMap.get("targetFWVersion");			
		}
		String lastLinkTimeRange = StringUtil.nullEmtpyToString(argMap.get("lastLinkTimeRange"), defaultLastLinkTimeRange);
		String fwFileName = argMap.get("fwFileName");
		String fwFileVersion = argMap.get("fwFileVersion");
		String reTryCount = StringUtil.nullEmtpyToString(argMap.get("reTryCount"), defaultReTryCount);
		String reTryInterval = StringUtil.nullEmtpyToString(argMap.get("reTryInterval"), defaultReTryInterval);

		TransactionStatus txstatus = null;
		txstatus = txmanager.getTransaction(null);

		try {
			/** DeviceModel 정보 추출 */
			DeviceModel modemModel = deviceModelDao.findByCondition("name", deviceModelName);
			if (modemModel != null) {
				logger.debug("### DeviceModel info => {}", modemModel.toString());
			} else {
				logger.error("Unknown DeviceModel. please check DeviceModel file name.");
				return;
			}

			/** Firmware Validation 및 ID 추출 */
			String firmwareId = "";
			Set<Condition> firmwareConditions = new HashSet<Condition>();
			firmwareConditions.add(new Condition("fileName", new Object[] { fwFileName }, null, Restriction.EQ));
			firmwareConditions.add(new Condition("fwVersion", new Object[] { fwFileVersion }, null, Restriction.EQ));
			firmwareConditions.add(new Condition("equipModel", new Object[] { deviceModelName }, null, Restriction.EQ));
			List<Firmware> firmwareList = firmwareDao.findByConditions(firmwareConditions);
			if (firmwareList != null && 0 < firmwareList.size()) {
				firmwareId = firmwareList.get(0).getId().toString();
				logger.debug("### Firmware info => {}", firmwareList.get(0).toString());
			} else {
				logger.error("Unknown Firmware. please check firmware file name.");
				return;
			}

			/** Location Validation 및 ID 추출 */
			Integer locationId = null;
			List<Location> locList = locationDao.getLocationByName(locationName);
			if (locList != null && 0 < locList.size()) {
				Location location = locList.get(0);
				locationId = location.getId();
				logger.debug("### Location info => {}", location.toString());
			} else {
				logger.error("Can't find location name. please check location nanme.");
				return;
			}

			/** Last Comm date range 설정 */
			String lastCommStartDate = DateTimeUtil.calcDate(Calendar.DATE, (Integer.parseInt(lastLinkTimeRange) * -1), "yyyyMMdd") + "000000";
			String lastCommEndDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd") + "235959";
			logger.debug("### lastLinkTimeRange info => lastCommStartDate={}, lastCommEndDate={}", lastCommStartDate, lastCommEndDate);

			/** Target Device List 추출 */
			Set<Condition> modemCondistions = new HashSet<Condition>();
			modemCondistions.add(new Condition("locationId", new Object[] { locationId }, null, Restriction.EQ));
			if (targetFWVersion != null && !targetFWVersion.equals("")) {
				modemCondistions.add(new Condition("fwVer", new Object[] { targetFWVersion }, null, Restriction.EQ));
			}
			modemCondistions.add(new Condition("phoneNumber", null, null, Restriction.NOTNULL));
			modemCondistions.add(new Condition("fwVer", null, null, Restriction.NOTNULL));
			modemCondistions.add(new Condition("fwVer", new Object[] { fwFileVersion }, null, Restriction.NEQ));
			modemCondistions.add(new Condition("lastLinkTime", new Object[] { lastCommStartDate, lastCommEndDate }, null, Restriction.BETWEEN));
			modemCondistions.add(new Condition("modemStatus", new Object[] { "code" }, null, Restriction.ALIAS));
			modemCondistions.add(new Condition("code.name", new Object[] { "BreakDown" }, null, Restriction.NEQ));
			modemCondistions.add(new Condition("code.name", new Object[] { "Delete" }, null, Restriction.NEQ));
			modemCondistions.add(new Condition("code.name", new Object[] { "Repair" }, null, Restriction.NEQ));
			modemCondistions.add(new Condition("model", null, null, Restriction.NOTNULL));
			modemCondistions.add(new Condition("model", new Object[] { "deviceModel" }, null, Restriction.ALIAS));
			modemCondistions.add(new Condition("deviceModel.name", new Object[] { deviceModelName }, null, Restriction.EQ));
			List<Modem> modemList = modemDao.findByConditions(modemCondistions);
			
			logger.debug("params ==>> locationId={}, targetFWVersion={}, lastCommStartDate={}, lastCommEndDate={}, deviceModelName={}"
					, locationId, targetFWVersion, lastCommStartDate, lastCommEndDate, deviceModelName);
			
			List<String> deviceList = new ArrayList<String>();
			for (Modem modemId : modemList) {
				deviceList.add(modemId.getDeviceSerial());
			}

			if (deviceList != null && 0 < deviceList.size()) {
				logger.debug("### DeviceList size => {}, List={}", deviceList.size(), deviceList.toString());
			} else {
				logger.error("Can't find Target device list. please check search parameters.");
				return;
			}

			/** 기타 파라미터 설정 */
			boolean useAsyncChannel = true;
			OTAExecuteType otaExecuteType = OTAExecuteType.EACH_BY_HES;
			OTATargetType targetType = OTATargetType.MODEM;
			DeviceType deviceType = DeviceType.Modem;
			boolean isImmediately = true;
			Code operationCode = getCodeByCode("8.2.6"); // Modem OTA

			logger.info("SoriaMBBModemOTABatch params. locationId={}, firmwareId={}, issueDate={}, useAsyncChannel={}, otaExecuteType={}" + ", otaRetryCount={}, otaRetryCycle={}, targetType={}, deviceType={}, isImmediately={}, operationCode={}", locationId, firmwareId, issueDate, useAsyncChannel, otaExecuteType, Integer.parseInt(reTryCount), Integer.parseInt(reTryInterval), targetType, deviceType,
					isImmediately, operationCode);
			logger.info("SoriaOTAImprovSchedule params. size={}, deviceList={}", deviceList.size(), deviceList.toString());

			soriaOTAImprovScheduleExecute(locationId.toString(), firmwareId, issueDate, useAsyncChannel, deviceList, otaExecuteType, Integer.parseInt(reTryCount), Integer.parseInt(reTryInterval), targetType, deviceType, isImmediately, operationCode);
		} catch (Exception e) {
			if (txstatus != null) {
				txmanager.rollback(txstatus);
			}
			logger.error("Task Excute transaction error - " + e, e);
			return;
		}

		if (txstatus != null) {
			txmanager.commit(txstatus);
		}
	}

	/*
	 * @param args
	 * 
	 * params
	 *  1. location
	 *    - 대상 모뎀의 Location.
	 *    - 옵션 : 필수
	 *  2. targetFWVersion
	 *    - 대상 모뎀의 펌웨어버전
	 *    - 옵션 : 선택 (default : all)
	 *  3. lastLinkTimeRange
	 *    - 대상 모뎀의 통신시간 범위. (Day)
	 *      EX) 1 : 1일 이내 통신이력있는 대상
	 *          2 : 2일 이내 통신이력있는 대상
	 *          3 : 3일 이내 통신이력있는 대상
	 *    - 옵션 : 선택 (default : 3)
	 *  4. fwFileName  
	 *    - 적용할 펌웨어 파일명
	 *    - 옵션 : 필수
	 *  5. fwFileVersion
	 *    - 적용할 펌웨어 파일 버전
	 *    - 옵션 : 필수
	 *  6. reTryCount
	 *    - 재시도 횟수
	 *    - 옵션 : 선택 (default : 2)
	 *  7. reTryInterval
	 *    - 재시도 간격 (Time)
	 *      ex) 1 : 1 시간 간격으로 실행
	 *          2 : 2 시간 간격으로 실행
	 *          3 : 3 시간 간격으로 실행
	 *    - 옵션 : 선택 (default : 3)
	 */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		issueDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

		logger.info("-----");
		logger.info("-----");
		logger.info("-----");
		logger.info("#### SoriaMBBModemOTABatch Task start. issueDate - {} ###", issueDate);
		logger.info("args[] => {}", Arrays.deepToString(args));

		try {
			argMap = new Hashtable<String, String>();
			for (String arg : args) {
				if (arg.contains("=")) {
					String splitStrs[] = arg.split("=");
					if(splitStrs != null && splitStrs.length == 2) {
						argMap.put(splitStrs[0], splitStrs[1]);						
					}
				}
			}
			
			if (argMap == null || argMap.isEmpty() || !argMap.containsKey("location") || !argMap.containsKey("fwFileName") || !argMap.containsKey("fwFileVersion")
					|| argMap.get("location").equals("")|| argMap.get("fwFileName").equals("")|| argMap.get("fwFileVersion").equals("")) {
				logger.info("Invalid parameters ==> {}", args == null ? "Null~!!" : argMap.toString() );
				return;
			}

			logger.info("SoriaMBBModemOTABatch params. location={}, targetFWVersion={}, lastLinkTimeRange={}, fwFileName={}, fwFileVersion={}, reTryCount={}, reTryInterval={}"
					, argMap.get("location"), argMap.get("targetFWVersion"), argMap.get("lastLinkTimeRange"), argMap.get("fwFileName"), argMap.get("fwFileVersion"), argMap.get("reTryCount"), argMap.get("reTryInterval"));

			
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "spring-SoriaMBBModemOTABatch.xml" });
			DataUtil.setApplicationContext(ctx);

			SoriaMBBModemOTABatch task = (SoriaMBBModemOTABatch) ctx.getBean(SoriaMBBModemOTABatch.class);
			task.execute(null);
		} catch (Exception e) {
			logger.error("SoriaMBBModemOTABatch excute error - " + e, e);
		} finally {
			logger.info("#### SoriaMBBModemOTABatch Task finished - Elapse Time : {} ###", DateTimeUtil.getElapseTimeToString(System.currentTimeMillis() - startTime));
			System.exit(0);
		}
	}

	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public void addFirmwareIssue(FirmwareIssue firmwareIssue) {
		firmwareIssueDao.saveOrUpdate(firmwareIssue);
		firmwareIssueDao.flushAndClear();
	}

	@Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
	public void addFirmwareIssueHistory(FirmwareIssueHistory firmwareIssueHistory) {
		firmwareIssueHistoryDao.saveOrUpdate(firmwareIssueHistory);
		firmwareIssueHistoryDao.flushAndClear();
	}

	public Code getCodeByCode(String code) {
		return dao.getCodeIdByCodeObject(code);
	}

	public void soriaOTAImprovScheduleExecute(String locationId, String firmwareId, String issueDate, boolean useAsyncChannel, List<String> deviceList, OTAExecuteType otaExecuteType, int otaRetryCount, int otaRetryCycle, OTATargetType targetType, DeviceType deviceType, boolean isImmediately, Code operationCode) {
		FirmwareIssue firmwareIssue = new FirmwareIssue();
		firmwareIssue.setLocationId(Integer.valueOf(locationId));
		firmwareIssue.setFirmwareId(Long.parseLong(firmwareId));
		firmwareIssue.setIssueDate(issueDate);
		if (useAsyncChannel) {
			firmwareIssue.setName("SCHEDULE_OTA_ASYNC");
		} else {
			firmwareIssue.setName("SCHEDULE_OTA_SMS");
		}

		firmwareIssue.setTotalCount(deviceList.size());

		if (otaExecuteType != null) {
			firmwareIssue.setOtaExecuteType(otaExecuteType.getValue());
		}

		firmwareIssue.setOtaRetryCount(otaRetryCount);
		firmwareIssue.setOtaRetryCycle(otaRetryCycle);

		addFirmwareIssue(firmwareIssue);
		logger.debug("### Save FirmwareIssue ==> {}", firmwareIssue.toString());

		try {
			Map<String, Object> otaExcuteResult = new HashMap<>();

			try {
				/*
				 * Each target OTA FirmwareIssueHistory save and OTA execute.
				 */
				if (deviceList != null && 0 < deviceList.size()) {
					for (String dId : deviceList) {
						FirmwareIssueHistory firmwareIssueHistory = new FirmwareIssueHistory();
						firmwareIssueHistory.setDeviceId(dId);
						firmwareIssueHistory.setDeviceType(deviceType);
						firmwareIssueHistory.setLocationId(Integer.valueOf(locationId));
						firmwareIssueHistory.setFirmwareId(Long.parseLong(firmwareId));
						firmwareIssueHistory.setIssueDate(issueDate);
						firmwareIssueHistory.setUpdateDate(isImmediately == true ? issueDate : DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
						addFirmwareIssueHistory(firmwareIssueHistory);

						logger.debug("### Save FirmwareIssueHistory ==> {}", firmwareIssueHistory.toString());
					}

					/*
					 * otaExcuteResult value
					 * 1. result
					 * 2. resultValue
					 */
					otaExcuteResult = cmdOperationUtil.cmdMultiFirmwareOTAImprov(locationId, targetType, isImmediately, firmwareId, issueDate, otaExecuteType, otaRetryCount, otaRetryCycle, useAsyncChannel, toTargetProperty);
					logger.debug("MultiFirmwareOTA excute Result = [{}]", otaExcuteResult);
				}
			} catch (Exception e) {
				logger.error("MultiFirmwareOTA Excute Exception - Target type = [" + targetType + "] Device = [" + deviceList + "]", e);
			}
		} catch (Exception e) {
			logger.error("FAIL : OTA Fail - {}", e);
		}
	}

}
