/**
 * 
 */
package com.aimir.fep.protocol.nip.client.batch.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.OTAType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.FirmwareIssueHistoryDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.nip.client.batch.excutor.IBatchRunnable;
import com.aimir.fep.trap.actions.SP.EV_SP_200_64_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_65_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_66_0_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_RESULT_CODE;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.FirmwareIssueHistory;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.util.DateTimeUtil;

/**
 * @author simhanger
 *
 */
@Deprecated
public class SORIADcuOTARunnable implements IBatchRunnable {
	private static Logger logger = LoggerFactory.getLogger(SORIADcuOTARunnable.class);
	private CommandGW gw;
	private String issueDate;
	private String mcuSysId;
	private OTAType otaType;
	private HashMap<String, Object> params;
	private String newFwVersion;
	private String targetModel;
	private List<String> filterValue;

	@SuppressWarnings("unchecked")
	public SORIADcuOTARunnable(CommandGW gw, String issueDate, String mcuSysId, OTAType otaType, Map<String, Object> params, List<String> filterValue) {
		this.gw = gw;
		this.issueDate = issueDate;
		this.mcuSysId = mcuSysId;
		this.otaType = otaType;
		this.params = (HashMap<String, Object>) ((HashMap<String, Object>) params).clone();
		this.newFwVersion = params.get("fw_version").toString();
		this.targetModel = params.get("model").toString();
		this.filterValue = filterValue;
	}

	@Override
	public void run() {
		OTA_UPGRADE_RESULT_CODE resultCode = null;
		boolean executeResult = false;
		String exceptionMessage = null;
		String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
//		ModemDao modemDao = DataUtil.getBean(ModemDao.class);
//		MeterDao meterDao = DataUtil.getBean(MeterDao.class);

		/*
		 * Clone On 관련 Parameter
		 * - Clone 시도 시간: 0이 아닌 경우 Clone On
		 * - 15분 단위 카운트 수: 96 *15 = 1440, 24시간
		 * - 0으로 설정 시, 사용하지 않음
		 * - range : 20 ~ 96
		 */
		int cloneCount = Integer.parseInt(FMPProperty.getProperty("ota.firmware.coordinator.clone.count", "96"));
		MCU mcu = null;
		
		try {
			logger.debug("Excute [START] IssueDate={}, OTAType={}, OTATypeCode={}, mcuSysId={}, params={}, filterValue={}, cloneCount={}"
					, issueDate, otaType, otaType.getTypeCode(), mcuSysId, params.toString(),(filterValue == null ? "null" : filterValue.toString()), cloneCount);
			
			logger.debug("#### Job parameters ####");
			logger.debug("# fw_crc={}", params.get("fw_crc"));
			logger.debug("# fw_version={}", newFwVersion);
			logger.debug("# image_identifier={}", params.get("image_identifier"));
			logger.debug("# checkSum={}", params.get("checkSum"));
			logger.debug("# take_over={}", params.get("take_over"));
			logger.debug("# model={}", params.get("model"));
			logger.debug("# fwFileName={}", params.get("fwFileName"));
			logger.debug("# fw_path={}", params.get("fw_path"));
			logger.debug("");
			
			/*
			 * FW Version Check.
			 */
			boolean versionCheck = true;
			MCUDao mcuDao = DataUtil.getBean(MCUDao.class);
			mcu = mcuDao.get(mcuSysId);
			if(otaType == OTAType.DCU){
				String oldFWVersion = mcu.getSysSwVersion();
				logger.debug("DCU={}, FW Version. OldVersion=({}) ==> NewVersion=({})", mcuSysId, oldFWVersion, newFwVersion);
				
				if(oldFWVersion.equals(newFwVersion)){
					logger.info("### [SKIP] This DCU is already same Firmware version. DCU ID = {}, Firmware Version = {}", mcuSysId, newFwVersion);
					
					exceptionMessage = "[SKIP] This DCU is already same Firmware version.";
					resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_SAME_VERSION;
					versionCheck = false;
				}
			}else if(otaType == OTAType.DCU_COORDINATOR){
				if(mcu.getMcuCodi() != null){
					String oldFWVersion = mcu.getMcuCodi().getCodiFwVer();
					logger.debug("Coordinator={}, FW Version. OldVersion=({}) ==> NewVersion=({})", mcu.getMcuCodi().getCodiID(), oldFWVersion, newFwVersion);
					
					if(oldFWVersion.equals(newFwVersion)){
						logger.info("### [SKIP] This Coordinator is already same Firmware version. DCU ID = {}, Firmware Version = {}", mcuSysId, newFwVersion);
						exceptionMessage = "[SKIP] This Coordinator is already same Firmware version.";
						resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_SAME_VERSION;
						versionCheck = false;
					}					
				}else{
					resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_EXECUTE_FAIL;
					exceptionMessage = "Can't found Coordinator.";
//					throw new Exception("Can't found Coordinator.");
				}
			}
			
			if (versionCheck){
				/************************
				 * For Test code
				 */
				//				Hashtable resultTable = null;
				//				for(int i=1; i<6; i++){
				//					logger.debug(i + ". Execute [{}] ==> mcuSysId=[{}], otaType=[{}], imageIdentifier=[{}], fwPath=[{}], checkSum=[{}], filterValue=[{}]", 
				//							"SORIADCUOTARunnable", mcuSysId, otaType, imageIdentifier, fwPath, checkSum, filterValue);			
				//					Thread.sleep(5000);
				//				}
				/************************/
				Hashtable<?, ?> resultTable = null;

				switch (otaType) {
				case DCU:
					String oldFWVersion = mcu.getSysSwVersion();
					logger.debug("FW Version. OldVersion=({}) ==> NewVersion=({})", oldFWVersion, newFwVersion);
					if (otaType == OTAType.DCU && oldFWVersion.equals(newFwVersion)) {
						logger.info("### [SKIP] This DCU is already same Firmware version. DCU ID = {}, Firmware Version = {}", mcuSysId, newFwVersion);
						
					}
				case DCU_KERNEL:
				case DCU_COORDINATOR:
				case METER_RF_BY_DCU:
				case MODEM_RF_BY_DCU:
					resultTable = gw.cmdReqToDCUNodeUpgrade(mcuSysId, otaType, params.get("image_identifier").toString(), params.get("fw_path").toString(), params.get("checkSum").toString(), filterValue);
					logger.debug("Result = " + resultTable.toString());
					
					// FirmwareIssueHistory에 Request ID 설정
					if(resultTable != null){
						String requestId = (String) resultTable.get("uintEntry");
						updateReqId(issueDate, requestId);
						logger.debug("requestId={}, issueDate={}", requestId, issueDate);
					}
					break;
				case METER_RF_BY_THIRD_PARTY_COORDINATOR:
					/** 이벤트는 저장않함.  DCU에서 올림 63, 64, 65, 66 */
					logger.debug("MeterModel={}, TargetModel={}, ControlCode={}, filterValue= null", targetModel, params.get("image_identifier"), 0);
					
					resultTable = gw.cmdReqImagePropagate(mcuSysId, otaType.getTypeCode(), 0, params.get("image_identifier").toString(), params.get("fw_path").toString(), params.get("checkSum").toString(), newFwVersion, (String) params.get("image_identifier"), cloneCount, null, null);
					logger.debug("Result = " + resultTable.toString());
					
					// FirmwareIssueHistory에 Request ID 설정
					if(resultTable != null){
						String requestId = (String) resultTable.get("uintEntry");
						updateReqId(issueDate, requestId);
						logger.debug("requestId={}, issueDate={}", requestId, issueDate);
					}
					break;
				case MODEM_RF_BY_THIRD_PARTY_COORDINATOR:
					/** 이벤트는 저장않함.  DCU에서 올림 63, 64, 65, 66 */
					logger.debug("TargetModel={}, ControlCode={}, filterValue= null", targetModel, 0);
					
					resultTable = gw.cmdReqImagePropagate(mcuSysId, otaType.getTypeCode(), 0, params.get("image_identifier").toString(), params.get("fw_path").toString(), params.get("checkSum").toString(), newFwVersion, targetModel, cloneCount, null, null); // "NAMR-P214SR" for use third party RF Modem ota
					logger.debug("Result = " + resultTable.toString());

					// FirmwareIssueHistory에 Request ID 설정
					if(resultTable != null){
						String requestId = (String) resultTable.get("uintEntry");
						updateReqId(issueDate, requestId);
						logger.debug("requestId={}, issueDate={}", requestId, issueDate);
					}
					break;
				case DCU_COORDINATOR_THIRD_PARTY_COORDINATOR:
				case METER_RF_BY_THIRD_PARTY_MODEM:
				case MODEM_RF_BY_THIRD_PARTY_MODEM:
					throw new Exception("This OTA Type is not support in HES.");
				default:
					break;
				}
				

				if (resultTable != null && resultTable.size() > 0) {
					executeResult = true;
					logger.debug("cmdReqToDCUNodeUpgrade Result = " + resultTable.toString());
				} else {
					resultCode = OTA_UPGRADE_RESULT_CODE.OTAERR_EXECUTE_FAIL;
					logger.debug("cmdReqToDCUNodeUpgrade Result is null");
					throw new Exception("cmdReqToDCUNodeUpgrade Result is null");
				}
			}

			logger.debug("Excute [END] IssueDate={}, OTAType={}, OTATypeCode={}, mcuSysId={}, params={}, filterValue={}, cloneCount={}"
					, issueDate, otaType, otaType.getTypeCode(), mcuSysId, params.toString(),(filterValue == null ? "null" : filterValue.toString()), cloneCount);
			
		} catch (Exception e) {
			logger.error("Excute DCU OTA error - " + e.toString(), e);
			exceptionMessage = e.toString();
		}

		/*
		 * 실패시 종료 Event저장
		 */
		if (!executeResult) {
			openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
			String errMsg = null;
			EV_SP_200_66_0_Action action66 = new EV_SP_200_66_0_Action();
			
			if (otaType == OTAType.METER_RF_BY_THIRD_PARTY_COORDINATOR) {
				errMsg = (exceptionMessage == null ? "Meter OTA Error by Thirdparty" : "Meter OTA Error by Thirdparty - " + exceptionMessage);
				
				action66.makeEvent(TargetClass.DCU, mcuSysId, TargetClass.EnergyMeter, openTime, resultCode, errMsg, "HES", mcu.getLocation());
				action66.updateOTAHistory(mcuSysId, DeviceType.Meter, openTime, resultCode, errMsg);
			} else if (otaType == OTAType.MODEM_RF_BY_THIRD_PARTY_COORDINATOR) {
				errMsg = (exceptionMessage == null ? "Modem OTA Error by Thirdparty" : "Modem OTA Error by Thirdparty - " + exceptionMessage);
				
				action66.makeEvent(TargetClass.DCU, mcuSysId, TargetClass.SubGiga, openTime, resultCode, errMsg, "HES", mcu.getLocation());
				action66.updateOTAHistory(mcuSysId, DeviceType.Modem, openTime, resultCode, errMsg);
			} else if(otaType == OTAType.DCU || otaType == OTAType.DCU_COORDINATOR || otaType == OTAType.DCU_KERNEL){
				errMsg = (exceptionMessage == null ? "Unknown Error" : exceptionMessage);
				
				for (String targetId : filterValue) {
					action66.makeEvent(TargetClass.DCU, targetId, TargetClass.DCU, openTime, resultCode, errMsg, "HES", mcu.getLocation());
					action66.updateOTAHistory(targetId, DeviceType.MCU, openTime, resultCode, errMsg);
				}				
			}
			
			logger.debug("Execute Result fail. event message = {}", errMsg);
		}
	}

	@Override
	public String getName() {
		return this.mcuSysId;
	}

	@Override
	public void printResult(String title, ResultStatus status, String desc) {
		logger.info(title + "," + status.name() + "," + desc);
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = ((IBatchRunnable) obj).getName().equals(getName());
		logger.debug("[Equals Check] ThisObj=[{}], ParamObj=[{}], is equals?=[{}]", getName(), ((IBatchRunnable) obj).getName(), result);

		return result;
	}
	
	/**
	 * FirmwareIssueHistory에 Request ID 설정
	 * @param issueDate
	 * @param requestId
	 * @throws Exception
	 */
	private void updateReqId(String issueDate, String requestId) throws Exception {
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		FirmwareIssueHistoryDao firmwareIssueHistoryDao = DataUtil.getBean(FirmwareIssueHistoryDao.class);
		
		if (issueDate == null || issueDate.equals("") || requestId == null || requestId.equals("")){
			throw new Exception("Unknown params. issueDate=" + issueDate + ", requestId=" + requestId);
		}else{
			try {
				txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
				txStatus = txManager.getTransaction(null);
				
				List<FirmwareIssueHistory> firmwareIssueHistoryList = firmwareIssueHistoryDao.getRetryTargetList(issueDate);
				for(FirmwareIssueHistory list : firmwareIssueHistoryList){
					list.setRequestId(requestId);
					logger.debug("DeviceType={}, DeviceId={}, updateReqId={}", list.getDeviceType(), list.getDeviceId(), requestId);
				}
				
				txManager.commit(txStatus);
				
			} catch (Exception e) {
				logger.error("Update RequestId Error - " + e, e);
				if (txStatus != null) {
					txManager.rollback(txStatus);
				}
			}
		}
	}

}
