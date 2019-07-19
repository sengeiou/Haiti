/**
 * (@)# OTACmdController.java
 *
 * 2016. 5. 26.
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
package com.aimir.bo.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.constants.CommonConstants.FW_OTA;
import com.aimir.constants.CommonConstants.FW_STATE;
import com.aimir.constants.CommonConstants.FW_TRIGGER;
import com.aimir.constants.CommonConstants.ModemCommandType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.OTAExecuteType;
import com.aimir.constants.CommonConstants.OTATargetType;
import com.aimir.constants.CommonConstants.OTAType;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.fep.protocol.nip.command.ModemIpInformation.TargetType;
import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.Firmware;
import com.aimir.model.device.FirmwareHistory;
import com.aimir.model.device.FirmwareIssue;
import com.aimir.model.device.FirmwareIssueHistory;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Code;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.schedule.task.ThresholdAuthenticationErrorTask;
import com.aimir.service.device.AsyncCommandLogManager;
import com.aimir.service.device.FirmWareManager;
import com.aimir.service.device.MCUManager;
import com.aimir.service.device.MeterManager;
import com.aimir.service.device.ModemManager;
import com.aimir.service.device.OperationLogManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

import net.sf.json.JSONObject;

/**
 * @author simhanger
 *
 */
@Service(value = "otaCmdController")
@Controller
public class OTACmdController<V> {
	private static Logger logger = LoggerFactory.getLogger(OTACmdController.class);

	@Autowired
	MeterManager meterManager;

	@Autowired
	ModemManager modemManager;

	@Autowired
	MCUManager mcuManager;

	@Autowired
	OperationLogManager operationLogManager;

	@Autowired
	OperatorManager operatorManager;

	@Autowired
	CodeManager codeManager;

	@Autowired
	CmdOperationUtil cmdOperationUtil;

	@Autowired
	AsyncCommandLogManager asyncCommandLogManager;

	@Autowired
	CmdController<V> cmdController;

	@Autowired
	FirmWareManager firmWareManager;

	private OTAType otaType; // 진행할 OTA타입

	//	public enum OTATargetType {
	//		DCU("DCU"), DCU_KERNEL("DCU_KERNEL"), METER("METER"), MODEM("MODEM");
	//
	//		@SuppressWarnings("unused")
	//		private String targetType;
	//
	//		private OTATargetType(String type) {
	//			this.targetType = type;
	//		}
	//
	//		public static OTATargetType getItem(String value) {
	//			for (OTATargetType fc : OTATargetType.values()) {
	//				if (fc.targetType.equals(value)) {
	//					return fc;
	//				}
	//			}
	//			return null;
	//		}
	//	}

	protected boolean commandAuthCheck(String loginId, String command) {

		Operator operator = operatorManager.getOperatorByLoginId(loginId);

		Role role = operator.getRole();
		Set<Code> commands = role.getCommands();
		Code codeCommand = null;
		if (role.getCustomerRole() != null && role.getCustomerRole()) {
			return false; //고객 권한이면 
		}

		for (Iterator<Code> i = commands.iterator(); i.hasNext();) {
			codeCommand = (Code) i.next();
			if (codeCommand.getCode().equals(command))
				return true; //관리자가 아니라도 명령에 대한 권한이 있으면
		}
		return false;
	}

	@RequestMapping(value = "/gadget/device/command/cmdGetMeterFWVersion")
	public ModelAndView commandGetMeterFWVersion(@RequestParam(value = "targetType", required = true) String targetType, @RequestParam(value = "deviceId", required = true) String meterId, @RequestParam(value = "loginId", required = true) String loginId, @RequestParam(value = "isNullBypass", required = false) String isNullBypass) {

		Code targetTypeCode = null;
		Code operationCode = null;
		Supplier supplier = null;
		ResultStatus status = ResultStatus.SUCCESS;

		ModelAndView mav = new ModelAndView("jsonView");
		String rtnMessage = "";

		OTATargetType tType = OTATargetType.getItem(targetType);
		boolean useNullBypass = Boolean.parseBoolean(isNullBypass);

		logger.debug("[cmdGetMeterFWVersion] targetType={}, meterId={}, loginId={}, isNullBypass={}", targetType, meterId, loginId, useNullBypass);

		/**
		 * 1. 권한 및 파라미터 체크
		 */
		if (!commandAuthCheck(loginId, "8.1.9")) { // Get Meter F/W Version
			mav.addObject("rtnStr", "No permission");
			return mav;
		} else if (meterId == null || meterId.equals("") || tType == null || tType.equals("")) {
			mav.addObject("rtnStr", "Target null!");
			return mav;
		}

		/**
		 * 2. 유형확인
		 */
		Meter meter = null;
		Modem modem = null;
		switch (tType) {
		case DCU:
			break;
		case METER:
			meter = meterManager.getMeter(Integer.parseInt(meterId));
			modem = meter.getModem();
			supplier = modem.getSupplier();

			/*
			 * SORIA Project
			 */
			if(modem.getNameSpace() != null && modem.getNameSpace().equals("SP")) {
				if (modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.SMS) { // MBB Modem
					otaType = OTAType.METER_MBB;
				} else if (modem.getModemType() == ModemType.MMIU && (modem.getProtocolType() == Protocol.IP|| modem.getProtocolType() == Protocol.GPRS)) { // Ethernet Modem
					otaType = OTAType.METER_ETHERNET;
				} else if (modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP && (modem.getMcu() != null && useNullBypass == false)) { // DCU - RF Modem
					otaType = OTAType.METER_RF_BY_DCU;
				} else if (modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP) { // RF Modem
					otaType = OTAType.METER_RF;
				}				
			}
			/*
			 * Pakistan Project
			 */
			else if(modem.getNameSpace() != null && (modem.getNameSpace().equals("PH") || modem.getNameSpace().equals("PG"))) {
				if(modem.getModemType() == ModemType.PLCIU) {
					otaType = OTAType.METER_RF;
				}else if(modem.getModemType() == ModemType.MMIU){
					otaType = OTAType.METER_MBB;
				}
			}
			else 
			break;
		case MODEM:
			break;
		default:
			break;
		}

		logger.debug("CmdInfo otaType = {}, deviceId = {}, loginId = {}", otaType, meter.getMdsId(), loginId);

		/**
		 * 3. OTA 타입에 따라 분기
		 */
		try {
			switch (otaType) {
			case DCU:
				break;

			case METER_MBB:
				Map<String, String> asyncParamMap = new HashMap<String, String>();
				asyncParamMap.put("meterId", meter.getMdsId());

				@SuppressWarnings("unchecked")
				Map<String, String> asyncResult = cmdController.sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "cmdGetMeterFWVersion", asyncParamMap);

				if (asyncResult != null) {
					rtnMessage = "F/W version = ";
					for (String key : asyncResult.keySet()) {
						rtnMessage += asyncResult.get(key).toString() + ", ";
					}

					logger.debug("cmdGetMeterFWVersion returnValue =>> " + rtnMessage);
				} else {
					status = ResultStatus.FAIL;
					rtnMessage = "FAIL : result receive fail.";
					logger.debug("FAIL : result receive fail.");
				}

				targetTypeCode = codeManager.getCodeByCode("1.3.1.1"); // EnergyMeter
				operationCode = codeManager.getCodeByCode("8.1.9"); // Get Meter F/W Version
				break;

			case METER_RF:
			case METER_ETHERNET:
				Map<String, Object> result = cmdOperationUtil.cmdGetMeterFWVersion(meter.getMdsId(), modem.getProtocolType());
				if (result != null && 0 < result.size()) {
					if (!Boolean.valueOf((boolean) result.get("result"))) {
						status = ResultStatus.FAIL;
						rtnMessage = String.valueOf(result.get("resultValue"));
					}else{
						rtnMessage = "F/W version = " + String.valueOf(result.get("resultValue"));	
					}
					
					logger.debug("cmdGetMeterFWVersion returnValue =>> " + rtnMessage);
				} else {
					status = ResultStatus.FAIL;
					rtnMessage = "FAIL : result receive fail.";
					logger.debug("FAIL : result receive fail.");
				}

				targetTypeCode = codeManager.getCodeByCode("1.3.1.1"); // EnergyMeter
				operationCode = codeManager.getCodeByCode("8.1.9"); // Get Meter F/W Version
				break;

			case METER_RF_BY_DCU:
				break;
			default:
				break;
			}

			if (operationCode != null && operationCode.getCode() != null) {
				String opMessage = "";
				if (250 <= rtnMessage.length()) {
					opMessage = rtnMessage.substring(0, 250) + "....";
				} else {
					opMessage = rtnMessage;
				}

				try {
					operationLogManager.saveOperationLog(supplier, targetTypeCode, meter.getMdsId(), loginId, operationCode, status.getCode(), opMessage);
				} catch (Exception e) {
					logger.error("## OperationLog save error - {}", e);
				}

			} else {
				logger.error("## Operation Code is not define. please check AIMIR Code.");
			}
		} catch (Exception e) {
			logger.error("FAIL : cmdGetMeterFWVersion Fail - [" + otaType + "][" + meterId + "] - {}", e);
			rtnMessage = "FAIL : cmdGetMeterFWVersion - [" + otaType + "][" + meterId + "] - " + e.getMessage();
		}

		/**
		 * 4. 결과 리턴
		 */
		mav.addObject("rtnStr", rtnMessage);
		return mav;
	}

	/**
	 * Multi Type OTA Start.
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@RequestMapping(value = "/gadget/device/command/cmdOTAStart")
	public ModelAndView commandOTAStart(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Code targetTypeCode = null;
		Code operationCode = null;
		Supplier supplier = null;
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("rtnResultStatus", false);
		
		/*
		 * Received Parameter
		 * 
		 * 1. loginId:admin
		 * 2. target:[{"locationId":"5","deviceIdList":["87433"]},{"locationId":"2883","deviceIdList":["783","778"]}]
		 * 3. targetType:DCU  // DCU, DCU_KERNEL, DCU_COORDINATE, METER, MODEM
		 * 4. fId:5785        //  Firmware id
		 * 5. otaExecuteType:  // 0(CLONE_OTA), 1(EACH_BY_DCU), 2(EACH_BY_HES)
		 * 6. otaExecuteTime:201710121150
		 * 7. isImmediately:false   // 즉시 실행여부
		 * 8. otaRetryCount:2
		 * 9. otaRetryCycle:3
		 * 10. otaViaUploadChannel:false   // MBB의 경우만 해당되며 검침정보 Upload시 맺어진 커넥션을 이용하는 방식

		/*
		 * Return values
		 * 
		 * 1. rtnResultStatus
		 * 2. rtnResultInfo
		 * 3. rtnStr
		 */
		String loginId = request.getParameter("loginId");
		String target = request.getParameter("target");
		OTATargetType targetType = OTATargetType.getItem(request.getParameter("targetType"));
		String firmwareId = request.getParameter("fId");
		OTAExecuteType otaExecuteType = OTAExecuteType.getItem(request.getParameter("otaExecuteType"));
		String otaExecuteTime = request.getParameter("otaExecuteTime");
		boolean isImmediately = Boolean.parseBoolean((request.getParameter("isImmediately") == null) ? "true" : request.getParameter("isImmediately"));
		int otaRetryCount = (request.getParameter("otaRetryCount") == null || request.getParameter("otaRetryCount").equals("")) ? 0 : Integer.parseInt(request.getParameter("otaRetryCount"));
		int otaRetryCycle = (request.getParameter("otaRetryCycle") == null || request.getParameter("otaRetryCycle").equals("")) ? 0 : Integer.parseInt(request.getParameter("otaRetryCycle"));
		boolean useAsyncChannel = Boolean.parseBoolean((request.getParameter("otaViaUploadChannel") == null) ? "false" : request.getParameter("otaViaUploadChannel"));
		boolean isDowngrade = false;
		String firmwarefileVer ="";
		
		DeviceType deviceType = null;
		
		if (target == null || target.equals("") || targetType == null || targetType.equals("")) {
			mav.addObject("rtnStr", "Target null!");
			return mav;
		}
		
		Firmware firmware = firmWareManager.getById(Integer.parseInt(firmwareId));
		
		if (firmware == null) {
			throw new Exception("Unknown Firmware selected.");
		}
		logger.debug("Firmware Info => {}", firmware.toString());
		
		firmwarefileVer = firmware.getFwVersion();
		
		ObjectMapper om = new ObjectMapper();
		List<Map<String, Object>> targetList = om.readValue(target, new TypeReference<List<Map<String, Object>>>(){});
		
		// Location 별로 수행
		for(int i=0; i<targetList.size(); i++){
			Map<String, Object> map = targetList.get(i);
			
			String locationId = (String)map.get("locationId");
			List<String> deviceList = (List<String>)map.get("deviceIdList");
			
			logger.debug("##############################################");
			logger.debug("Parameter : loginId={}, targetType={}, locationId={}, firmwareId={}, OTAExecuteType={}, otaExecuteTime={}, isImmediately={}, otaRetryCount={}, otaRetryCycle={}, deviceIdSize=[{}], useAsyncChannel=[{}]"
					, loginId, targetType, locationId, firmwareId, otaExecuteType, otaExecuteTime, isImmediately, otaRetryCount, otaRetryCycle, deviceList.size(), useAsyncChannel);
			
			/*
			 *  유형확인
			 */
			MCU mcu = null;
			Meter meter = null;
			Modem modem = null;
			String targetModel = null;
	
			switch (targetType) {
			case DCU:
				if (!commandAuthCheck(loginId, "8.3.5")) {
					mav.addObject("rtnStr", "No permission");
					return mav;
				}
				mcu = mcuManager.getMCU(deviceList.get(0));
				if(Double.parseDouble(mcu.getSysSwVersion()) > Double.parseDouble(firmwarefileVer))
					isDowngrade = true;
				else
					isDowngrade = false;
				supplier = mcu.getSupplier();
				targetModel = mcu.getDeviceModel().getName();
	
				operationCode = codeManager.getCodeByCode("8.3.5"); // MCU OTA
				targetTypeCode = codeManager.getCodeByCode("1.1"); // DCU
	
				deviceType = DeviceType.MCU;
	
				break;
			 /*
			 case DCU_KERNEL:
				if (!commandAuthCheck(loginId, "8.3.8")) {
					mav.addObject("rtnStr", "No permission");
					return mav;
				}
				mcu = mcuManager.getMCU(deviceList.get(0));
				if(Double.parseDouble(mcu.getSysSwVersion()) > Double.parseDouble(firmwarefileVer))
					isDowngrade = true;
				else
					isDowngrade = false;
				supplier = mcu.getSupplier();
				targetModel = mcu.getDeviceModel().getName();
	
				operationCode = codeManager.getCodeByCode("8.3.8"); // MCU-KERNEL OTA
				targetTypeCode = codeManager.getCodeByCode("1.1"); // DCU
	
				deviceType = DeviceType.MCU;
				break;
			case DCU_COORDINATE:
				if (!commandAuthCheck(loginId, "8.3.9")) {
					mav.addObject("rtnStr", "No permission");
					return mav;
				}
				mcu = mcuManager.getMCU(deviceList.get(0));
				if(Double.parseDouble(mcu.getSysSwVersion()) > Double.parseDouble(firmwarefileVer))
					isDowngrade = true;
				else
					isDowngrade = false;
				supplier = mcu.getSupplier();
				targetModel = mcu.getDeviceModel().getName();
				
				operationCode = codeManager.getCodeByCode("8.3.9"); // DCU_COORDINATE OTA
				targetTypeCode = codeManager.getCodeByCode("1.1"); // DCU
	
				deviceType = DeviceType.MCU;
				break;
			*/
			case METER:
				if (!commandAuthCheck(loginId, "8.1.8")) {
					mav.addObject("rtnStr", "No permission");
					return mav;
				}
				meter = meterManager.getMeter(deviceList.get(0));
				if(Double.parseDouble(meter.getSwVersion()) > Double.parseDouble(firmwarefileVer))
					isDowngrade = true;
				else
					isDowngrade = false;
				modem = meter.getModem();
				supplier = modem.getSupplier();
				
				targetModel = meter.getModel().getName();
				operationCode = codeManager.getCodeByCode("8.1.8"); // Meter OTA
				targetTypeCode = codeManager.getCodeByCode("1.3"); // Meter
	
				deviceType = DeviceType.Meter;
				break;
			case MODEM:
				if (!commandAuthCheck(loginId, "8.2.3")) {
					mav.addObject("rtnStr", "No permission");
					return mav;
				}
				modem = modemManager.getModem(deviceList.get(0));
				if(Double.parseDouble(modem.getFwVer()) > Double.parseDouble(firmwarefileVer))
					isDowngrade = true;
				else
					isDowngrade = false;
				supplier = modem.getSupplier();
				targetModel = modem.getModel().getName();
				
				operationCode = codeManager.getCodeByCode("8.2.3"); // Modem OTA			
				targetTypeCode = codeManager.getCodeByCode("1.2"); // Modem
	
				deviceType = DeviceType.Modem;
				break;
			default:
				break;
			}
			
			if(isDowngrade==true && (targetType==OTATargetType.MODEM || targetType==OTATargetType.METER) &&  
					!(otaExecuteType == OTAExecuteType.EACH_BY_HES || otaExecuteType == OTAExecuteType.EACH_BY_DCU)){
				mav.addObject("rtnResultStatus", false);
				mav.addObject("rtnStr", "[Downgrade] Only Execute Type(By DCU/HES) is available"  );
				return mav;
			}
			
			//SP-943
			if(otaExecuteType == OTAExecuteType.CLONE_OTA && (targetType == OTATargetType.MODEM || targetType == OTATargetType.METER)) {
				MCU otaMcu = null;
				for(String dId : deviceList){
					if(targetType == OTATargetType.MODEM) {
						otaMcu = modemManager.getModem(dId).getMcu();
					} else {
						otaMcu = meterManager.getMeter(dId).getModem().getMcu();
					}
					
					if(otaMcu != null) {
						Float fwVersionByMcu = Float.parseFloat(otaMcu.getSysSwVersion());
						Float fwVersionByCodi = otaMcu.getMcuCodi() == null ? 0f : Float.parseFloat(otaMcu.getMcuCodi().getCodiFwVer());
						
						if(fwVersionByCodi == null || fwVersionByCodi.equals("")) {
							mav.addObject("rtnResultStatus", false);
							mav.addObject("rtnStr", "Invalid Coordinator version");
							return mav;
						}
						
						Properties prop = new Properties();
						prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
						String conditionStr = prop.getProperty("clone.ota.dcu.codiversion", null);
						if(conditionStr != null && !conditionStr.equals("")) {
							List<Map<String, Object>> conditionList = new ObjectMapper().readValue(conditionStr, new TypeReference<List<Map<String, Object>>>(){});
							
							if(conditionList.size() > 0) {
								
								for(Map<String, Object> item : conditionList) {
									Float fVersionByMcu = Float.parseFloat(item.get("dcu").toString());
									Float fVersionByCodi = 0f;
									String[] codiVersionArray = item.get("codi").toString().replaceAll("\\[", "").replaceAll("\\]", "").trim().split(",");
									
									if(codiVersionArray.length > 1) {
										fVersionByCodi = Float.parseFloat(codiVersionArray[0]);
										for(String var : codiVersionArray) {
											if(fVersionByCodi > Float.parseFloat(var)) {
												fVersionByCodi = Float.parseFloat(var);
											}
										}
									} else {
										fVersionByCodi = Float.parseFloat(codiVersionArray[0]);
									}
									
									if(fwVersionByCodi >= fVersionByCodi) {
										if(fwVersionByMcu < fVersionByMcu) {
											mav.addObject("rtnResultStatus", false);
											mav.addObject("rtnStr", "MCU SysId:"+otaMcu.getSysID()+", MCU version not matched, MCU version should be upgrad to " + fVersionByMcu); 
											return mav;
										}
									}else {									
										mav.addObject("rtnResultStatus", false);
										mav.addObject("rtnStr", "MCU SysId:"+otaMcu.getSysID()+", Coordinator version not matched, Coordinator version should be upgrade to " + fVersionByCodi);
										return mav;
									}
								}
							}							
						}
					} else {
						mav.addObject("rtnResultStatus", false);
						mav.addObject("rtnStr", "MCU Empty");
						return mav;
					}
				}
			}
	
			/*
			 * issueDate 설정. Locaion별로 IssueDate가 동일하지않도록 시간을 조정함. 
			 */
			String issueDate = null;
			if(isImmediately == true || otaExecuteTime == null || otaExecuteTime.equals("")){
				issueDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
				Thread.sleep(1000);
			}else{
				Calendar cal = DateTimeUtil.getCalendar(otaExecuteTime);
				cal.add(Calendar.SECOND, 1);
				otaExecuteTime = DateTimeUtil.getFormatTime(cal);
				
				issueDate = otaExecuteTime;
			}
			
			/*
			 * Clone OTA를 위한 DCU list 추출
			 */
			List<String> dcuList = null;
			if(deviceList != null && 0 < deviceList.size()){
				/*
				 * Clone OTA의 경우 실제로 OTA Target은 Coordiantor이지만 DCU ID로 이벤트기록및 추적하도록한다.
				 */
				if(otaExecuteType == OTAExecuteType.CLONE_OTA && targetType.equals(OTATargetType.METER)){
					dcuList = new ArrayList<String>();
					for(String dId : deviceList){
						meter = meterManager.getMeter(dId);
						modem = meter.getModem();
						String dcuId = modem.getMcu().getSysID();
						if(!dcuList.contains(dcuId)){
							dcuList.add(modem.getMcu().getSysID());									
						}
					}
				}else if(otaExecuteType == OTAExecuteType.CLONE_OTA && targetType.equals(OTATargetType.MODEM)){
					dcuList = new ArrayList<String>();
					for(String dId : deviceList){
						modem = modemManager.getModem(dId);
						String dcuId = modem.getMcu().getSysID();
						if(!dcuList.contains(dcuId)){
							dcuList.add(modem.getMcu().getSysID());									
						}
					}
				}
			}
			
			
			/*
			 * Group OTA FirmwareIssue save.
			 */
			FirmwareIssue firmwareIssue = new FirmwareIssue();
			firmwareIssue.setLocationId(Integer.valueOf(locationId));
			firmwareIssue.setFirmwareId(Long.parseLong(firmwareId));
			firmwareIssue.setIssueDate(issueDate);
			if(useAsyncChannel){
				firmwareIssue.setName("GROUP_OTA_ASYNC");				
			}else{
				firmwareIssue.setName("GROUP_OTA");
			}
			
			if(dcuList != null){  // Clone OTA인경우
				firmwareIssue.setTotalCount(dcuList.size());
			}else{
				firmwareIssue.setTotalCount(deviceList.size());				
			}

			if(otaExecuteType != null){
				firmwareIssue.setOtaExecuteType(otaExecuteType.getValue());				
			}

			firmwareIssue.setOtaRetryCount(otaRetryCount);
			firmwareIssue.setOtaRetryCycle(otaRetryCycle);
			
			// SP-957 MODEM_OTA(0) 
			if(otaExecuteType == OTAExecuteType.CLONE_OTA && targetType.equals(OTATargetType.MODEM))
				firmwareIssue.setCommandType(0);
			
			firmWareManager.addFirmwareIssue(firmwareIssue);
			logger.debug("### Save FirmwareIssue ==> {}", firmwareIssue.toString());
			
			try {
				Map<String, Object> otaExcuteResult = new HashMap<>();
				
				try {
					/*
					 * Each target OTA FirmwareIssueHistory save and OTA execute.
					 */
					if (deviceList != null && 0 < deviceList.size()) {
						
						/*
						 * Clone OTA의 경우 실제로 OTA Target은 Coordiantor이지만 DCU ID로 이벤트기록및 추적하도록한다.
						 */
						if(otaExecuteType == OTAExecuteType.CLONE_OTA && (targetType.equals(OTATargetType.METER) || targetType.equals(OTATargetType.MODEM) )){
							deviceList = dcuList;
						}
						
						/*
						 * Clone OTA가 아닌경우
						 */
						for (String dId : deviceList) {
							FirmwareIssueHistory firmwareIssueHistory = new FirmwareIssueHistory();
							firmwareIssueHistory.setDeviceId(dId);
							firmwareIssueHistory.setDeviceType(deviceType);
							firmwareIssueHistory.setLocationId(Integer.valueOf(locationId));
							firmwareIssueHistory.setFirmwareId(Long.parseLong(firmwareId));
							firmwareIssueHistory.setIssueDate(issueDate);
							firmwareIssueHistory.setUpdateDate(isImmediately == true ? issueDate : DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
							
							
							if(otaExecuteType == OTAExecuteType.CLONE_OTA) {
								firmwareIssueHistory.setDcuId(dId);	
							}else if(otaExecuteType == OTAExecuteType.EACH_BY_DCU) {
								if(targetType == OTATargetType.MODEM) {
									firmwareIssueHistory.setDcuId(modemManager.getModem(dId).getMcu().getSysID());
								} else if(targetType == OTATargetType.METER) {
									firmwareIssueHistory.setDcuId(meterManager.getMeter(dId).getModem().getMcu().getSysID());
								} else if(targetType == OTATargetType.DCU_COORDINATE || targetType == OTATargetType.DCU_KERNEL) {
									firmwareIssueHistory.setDcuId(dId);
								}
							}
							
							firmWareManager.addFirmwareIssueHistory(firmwareIssueHistory);
							
							logger.debug("### Save FirmwareIssueHistory ==> {}", firmwareIssueHistory.toString());
						}
	
						/*
			 			 * otaExcuteResult value
						 * 1. result
						 * 2. resultValue
						 */
						otaExcuteResult = cmdOperationUtil.cmdMultiFirmwareOTAImprov(locationId, targetType, isImmediately, firmwareId, issueDate, otaExecuteType, otaRetryCount, otaRetryCycle, useAsyncChannel);
						logger.debug("MultiFirmwareOTA excute Result = [{}]", otaExcuteResult.toString());
					}			
				} catch (Exception e) {
					logger.error("MultiFirmwareOTA Excute Exception - Target type = [" + targetType + "] Device = [" + deviceList + "]", e);
				} 
				
				// OTA Excute Result value
				String resultValue = String.valueOf(otaExcuteResult.get("resultValue"));
				
				/*
				 * Operation log save 
				 */
				if (operationCode != null && operationCode.getCode() != null) {
					String opMessage = "";
					if (250 <= resultValue.length()) {
						opMessage = resultValue.substring(0, 250) + "....";
					} else {
						opMessage = resultValue;
					}
	
					try {
				        ResultStatus status = ResultStatus.FAIL;
						if(Boolean.parseBoolean(String.valueOf(otaExcuteResult.get("result"))) == true){
							status = ResultStatus.SUCCESS;
						}
						
						for(String dId : deviceList){
							operationLogManager.saveOperationLog(supplier, targetTypeCode, dId, loginId, operationCode, status.getCode(), opMessage);						
						}
					} catch (Exception e) {
						logger.error("## OperationLog save error - " + e.getMessage(), e);
					}
	
				} else {
					logger.error("## Operation Code is not define. please check AIMIR Code.");
				}
				
				// 추가 정보 add
				/*Map<String, String> rInfo = new HashMap<>();
				rInfo.put("targetType", targetType.name());
				rInfo.put("locationId", locationId);
				rInfo.put("firmwareId", firmwareId);
				rInfo.put("otaExecuteType", otaExecuteType.name());
				rInfo.put("isImmediately", String.valueOf(isImmediately));
				rInfo.put("issueDate", otaExecuteTime);
				rInfo.put("otaRetryCount", String.valueOf(otaRetryCount));
				rInfo.put("otaRetryCycle", String.valueOf(otaRetryCycle));
				rInfo.put("targetModel", targetModel);
				rInfo.put("fileName", firmware.getFileName());
				rInfo.put("fwVersion", firmware.getFwVersion());*/
				
				/*mav.addObject("rtnResultStatus", Boolean.valueOf(String.valueOf(otaExcuteResult.get("result"))));
				mav.addObject("rtnResultInfo", rInfo);
				mav.addObject("rtnStr", resultValue);*/
			} catch (Exception e) {
				logger.error("FAIL : OTA Fail - [{}] - {}", otaType, e);
				
				mav.addObject("rtnResultStatus", false);
				mav.addObject("rtnStr", "Fail - " + e.getMessage());
				return mav;
			}
		}
		

		mav.addObject("rtnStr", "Request Successfully");
		return mav;
	}
	
	/**
	 * SP-957
	 * Clone on/off Start.
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@RequestMapping(value = "/gadget/device/command/cmdCloneOnOffStart")
	public ModelAndView commandCloneOnOffStart(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Code targetTypeCode = null;
		Code operationCode = null;
		Supplier supplier = null;
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("rtnResultStatus", false);
		
		/*
		 * Received Parameter
		 * 
		 * 1. loginId:admin
		 * 2. target:[{"locationId":"5","deviceIdList":["87433"]},{"locationId":"2883","deviceIdList":["783","778"]}]
		 * 3. targetType:DCU  // DCU, DCU_KERNEL, DCU_COORDINATE, METER, MODEM
		 * 4. fId:5785        //  Firmware id
		 * 5. otaExecuteType:  // 1(EACH_BY_DCU), 2(EACH_BY_HES)
		 * 6. otaExecuteTime:201710121150
		 * 7. isImmediately:false   // 즉시 실행여부
		 * 8. otaRetryCount:2
		 * 9. otaRetryCycle:3
		 * 11. propagation:true   // Default checked
		 * 12. cloneonoff   // cloneon: true, cloneoff: false
		 * 13. cloningTime   // 클론 실행 시간 (5~24 사이의 값)

		/*
		 * Return values
		 * 
		 * 1. rtnResultStatus
		 * 2. rtnResultInfo
		 * 3. rtnStr
		 */
		String loginId = request.getParameter("loginId");
		String target = request.getParameter("target");
		String deviceTargetType = request.getParameter("targetType");
		OTATargetType targetType = OTATargetType.getItem(request.getParameter("targetType"));
		String firmwareId = request.getParameter("fId");
		OTAExecuteType otaExecuteType = OTAExecuteType.getItem(request.getParameter("otaExecuteType"));
		String otaExecuteTime = request.getParameter("otaExecuteTime");
		boolean isImmediately = Boolean.parseBoolean((request.getParameter("isImmediately") == null) ? "true" : request.getParameter("isImmediately"));
		int otaRetryCount = (request.getParameter("otaRetryCount") == null || request.getParameter("otaRetryCount").equals("")) ? 0 : Integer.parseInt(request.getParameter("otaRetryCount"));
		int otaRetryCycle = (request.getParameter("otaRetryCycle") == null || request.getParameter("otaRetryCycle").equals("")) ? 0 : Integer.parseInt(request.getParameter("otaRetryCycle"));
		/* SP-957 Clone on,off */
		boolean propagation = Boolean.parseBoolean((request.getParameter("propagation") == null) ? "true" : request.getParameter("propagation"));
		int cloningTime = (request.getParameter("cloningTime") == null || request.getParameter("cloningTime").equals("")) ? 0 : Integer.parseInt(request.getParameter("cloningTime"));
		ModemCommandType commandType = ModemCommandType.getItem(request.getParameter("commandType"));

		DeviceType deviceType = null;
		
		if(deviceTargetType.equals("meter"))
			targetType =OTATargetType.METER ;
		if(deviceTargetType.equals("modem"))
			targetType = OTATargetType.MODEM;
		
		if (target == null || target.equals("") || targetType == null || targetType.equals("") 
			|| targetType == OTATargetType.DCU 
			|| targetType == OTATargetType.DCU_KERNEL
			|| targetType == OTATargetType.DCU_COORDINATE) {

			mav.addObject("rtnStr", "Target null!");
			return mav;
		}
		
		Firmware firmware = firmWareManager.getById(Integer.parseInt(firmwareId));
		if (firmware == null) {
			throw new Exception("Unknown Firmware selected.");
		}
		logger.debug("Firmware Info => {}", firmware.toString());
		
		ObjectMapper om = new ObjectMapper();
		List<Map<String, Object>> targetList = om.readValue(target, new TypeReference<List<Map<String, Object>>>(){});
		
		// Location 별로 수행
		for(int i=0; i<targetList.size(); i++){
			Map<String, Object> map = targetList.get(i);
			
			String locationId = (String)map.get("locationId");
			List<String> deviceList = (List<String>)map.get("deviceIdList");
			
			logger.debug("##############################################");
			logger.debug("Parameter : loginId={}, targetType={}, locationId={}, firmwareId={}, OTAExecuteType={}, otaExecuteTime={}"
					+ ", isImmediately={}, otaRetryCount={}, otaRetryCycle={}, deviceIdSize=[{}], propagation={}, cloningTime={}, commandType={}"
					, loginId, targetType, locationId, firmwareId, otaExecuteType, otaExecuteTime
					, isImmediately, otaRetryCount, otaRetryCycle, deviceList.size(), propagation, cloningTime, commandType);
			
			/*
			 *  유형확인
			 */
			MCU mcu = null;
			Meter meter = null;
			Modem modem = null;
			String targetModel = null;
			
			switch (targetType) {
			case MODEM:
				if (!commandAuthCheck(loginId, "8.2.12")) {
					mav.addObject("rtnStr", "No permission");
					return mav;
				}
				
				modem = modemManager.getModem(deviceList.get(0));
				supplier = modem.getSupplier();
				targetModel = modem.getModel().getName();
				targetTypeCode = codeManager.getCodeByCode("1.2"); // Modem
				operationCode = codeManager.getCodeByCode("8.2.12"); // Modem Clone on, off
				deviceType = DeviceType.Modem;
				break;
			case METER:
				if (!commandAuthCheck(loginId, "8.1.15")) {
					mav.addObject("rtnStr", "No permission");
					return mav;
				}
				
				meter = meterManager.getMeter(deviceList.get(0));
				modem = meter.getModem();
				supplier = modem.getSupplier();
				targetModel = meter.getModel().getName();
				targetTypeCode = codeManager.getCodeByCode("1.3"); // Meter
				operationCode = codeManager.getCodeByCode("8.1.15"); // Meter Clone on, off
				deviceType = DeviceType.Meter;
				break;				
			default:
				break;
			}
			
			/*
			 * issueDate 설정. Locaion별로 IssueDate가 동일하지않도록 시간을 조정함. 
			 */
			String issueDate = null;
			if(isImmediately == true || otaExecuteTime == null || otaExecuteTime.equals("")){
				issueDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
				Thread.sleep(1000);
			}else{
				Calendar cal = DateTimeUtil.getCalendar(otaExecuteTime);
				cal.add(Calendar.SECOND, 1);
				otaExecuteTime = DateTimeUtil.getFormatTime(cal);
				
				issueDate = otaExecuteTime;
			}
			
			/*
			 * Group OTA FirmwareIssue save.
			 */
			FirmwareIssue firmwareIssue = new FirmwareIssue();
			firmwareIssue.setLocationId(Integer.valueOf(locationId));
			firmwareIssue.setFirmwareId(Long.parseLong(firmwareId));
			firmwareIssue.setIssueDate(issueDate);
			firmwareIssue.setName(commandType.name());				
			firmwareIssue.setTotalCount(deviceList.size());				
			if(otaExecuteType != null){
				firmwareIssue.setOtaExecuteType(otaExecuteType.getValue());				
			}
			firmwareIssue.setOtaRetryCount(otaRetryCount);
			firmwareIssue.setOtaRetryCycle(otaRetryCycle);
	
			// Modem Command Type  추가 CLONE_ON(1), CLONE_OFF(2)
			if(commandType != null){
				if(commandType == ModemCommandType.CLONE_ON) {
					firmwareIssue.setCommandType(1);
				} else if(commandType == ModemCommandType.CLONE_OFF) {
					firmwareIssue.setCommandType(2);
				}
			}
			
			firmWareManager.addFirmwareIssue(firmwareIssue);
			logger.debug("### Save FirmwareIssue ==> {}", firmwareIssue.toString());
			
			try {
				Map<String, Object> otaExcuteResult = new HashMap<>();
				try {
					/*
					 * Each target Clone on,off FirmwareIssueHistory save and Clone on,off execute.
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
							firmWareManager.addFirmwareIssueHistory(firmwareIssueHistory);
							
							logger.debug("### Save FirmwareIssueHistory ==> {}", firmwareIssueHistory.toString());
						}
						
						/*
						 * Clone on,off ExcuteResult value
						 * 1. result
						 * 2. resultValue
						 */
						otaExcuteResult = cmdOperationUtil.commandCloneOnOffStart(locationId, targetType, isImmediately, issueDate, otaExecuteType, otaRetryCount, otaRetryCycle, propagation, commandType, cloningTime);
						logger.debug("Command Clone On/Off excute Result = [{}]", otaExcuteResult.toString());
					}			
				} catch (Exception e) {
					logger.error("Command Clone On/Off Excute Exception - Target type = [" + targetType + "] Device = [" + deviceList + "]", e);
				} 
				
				// Clone on,off Excute Result value
				String resultValue = String.valueOf(otaExcuteResult.get("resultValue"));
				
				/*
				 * Operation log save 
				 */
				if (operationCode != null && operationCode.getCode() != null) {
					String opMessage = "";
					if (250 <= resultValue.length()) {
						opMessage = resultValue.substring(0, 250) + "....";
					} else {
						opMessage = resultValue;
					}
					
					try {
						ResultStatus status = ResultStatus.FAIL;
						if(Boolean.parseBoolean(String.valueOf(otaExcuteResult.get("result"))) == true){
							status = ResultStatus.SUCCESS;
						}
						
						for(String dId : deviceList){
							operationLogManager.saveOperationLog(supplier, targetTypeCode, dId, loginId, operationCode, status.getCode(), opMessage);						
						}
					} catch (Exception e) {
						logger.error("## OperationLog save error - " + e.getMessage(), e);
					}
					
				} else {
					logger.error("## Operation Code is not define. please check AIMIR Code.");
				}
				
				// 추가 정보 add
				/*Map<String, String> rInfo = new HashMap<>();
				rInfo.put("targetType", targetType.name());
				rInfo.put("locationId", locationId);
				rInfo.put("firmwareId", firmwareId);
				rInfo.put("otaExecuteType", otaExecuteType.name());
				rInfo.put("isImmediately", String.valueOf(isImmediately));
				rInfo.put("issueDate", otaExecuteTime);
				rInfo.put("otaRetryCount", String.valueOf(otaRetryCount));
				rInfo.put("otaRetryCycle", String.valueOf(otaRetryCycle));
				rInfo.put("targetModel", targetModel);
				rInfo.put("fileName", firmware.getFileName());
				rInfo.put("fwVersion", firmware.getFwVersion());*/
				
				/*mav.addObject("rtnResultStatus", Boolean.valueOf(String.valueOf(otaExcuteResult.get("result"))));
				mav.addObject("rtnResultInfo", rInfo);
				mav.addObject("rtnStr", resultValue);*/
			} catch (Exception e) {
				logger.error("FAIL : OTA Fail - [{}] - {}", otaType, e);
				
				mav.addObject("rtnResultStatus", false);
				mav.addObject("rtnStr", "Fail - " + e.getMessage());
				return mav;
			}
		}
		
		
		mav.addObject("rtnStr", "Request Successfully");
		return mav;
	}

	@RequestMapping(value = "/gadget/device/command/cmdOTARetryStart")
	public ModelAndView commandOTARetryStart(HttpServletRequest request, HttpServletResponse response) throws Exception {

		ModelAndView mav = new ModelAndView("jsonView");
		ResultStatus status = ResultStatus.FAIL;

		int selectedDeviceCount = Integer.parseInt(request.getParameter("selectedDeviceCount"));
		String firmwareDataJsonString = request.getParameter("firmwareDataJsonString");
		String loginId = request.getParameter("loginId");

		logger.info("selectedDeviceCount : " + selectedDeviceCount);
		logger.info("firmwareDataJsonString : " + firmwareDataJsonString);

		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> map = new HashMap<String, String>();
			Map<String, String> firmwareDataMap = new HashMap<String, String>();
			ArrayList<Map<String, String>> firmwareDataList = new ArrayList<Map<String, String>>();
			String json;

			// 두개 이상 device에 OTA Retry를 시도한 경우 
			if (selectedDeviceCount != 1) {
				List<String> jsonStringList = new ArrayList<String>(Arrays.asList(firmwareDataJsonString.split("/")));

				for (int index = 0; index < selectedDeviceCount; index++) {
					json = jsonStringList.get(index);

					// convert JSON string to Map
					map = mapper.readValue(json, new TypeReference<Map<String, String>>() {
					});
					firmwareDataMap.put("firmwareId", map.get("firmwareId"));
					firmwareDataMap.put("locationId", map.get("location"));
					firmwareDataMap.put("targetType", map.get("targetType"));
					firmwareDataMap.put("targetId", map.get("targetId"));
					firmwareDataMap.put("issueDate", map.get("issueDate"));

					firmwareDataList.add(firmwareDataMap);
				}

				System.out.println(firmwareDataList);

			} else {
				json = firmwareDataJsonString;

				// convert JSON string to Map
				map = mapper.readValue(json, new TypeReference<Map<String, String>>() {
				});
				firmwareDataMap.put("firmwareId", map.get("firmwareId"));
				firmwareDataMap.put("locationId", map.get("location"));
				firmwareDataMap.put("targetType", map.get("targetType"));
				firmwareDataMap.put("targetId", map.get("targetId"));
				firmwareDataMap.put("issueDate", map.get("issueDate"));

				firmwareDataList.add(firmwareDataMap);

				System.out.println(firmwareDataList);
			}

			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
			logger.error("commandOTARetryStart -" + e, e);
		}

		// TODO 

		mav.addObject("rtnStr", status.name());
		return mav;
	}

	/**
	 * firmware_history 테이블에 OTA history를 기록합니다.
	 * 
	 * @param mcu
	 * @param equipId
	 * @param equipKind
	 * @param equipModel
	 * @param equipType
	 * @param equipVendor
	 * @param errorCode
	 * @param otaState
	 * @param otaStep
	 * @param triggerState
	 * @param triggerStep
	 * @param trId
	 * @param param
	 * @throws Exception
	 */
	private void setFirmwareIssueHistory(MCU mcu, String equipId, String equipKind, String equipModel, String equipType, String equipVendor, String errorCode, FW_STATE otaState, FW_OTA otaStep, TR_STATE triggerState, FW_TRIGGER triggerStep, Long trId, Map<String, Object> param // "equip_kind"(ex)Modem 대소문자 주의) , "equip_id", "arm"
	) throws Exception {

		FirmwareHistory firmwareHistory = new FirmwareHistory();

		if (mcu != null)
			firmwareHistory.setMcu(mcu);
		firmwareHistory.setEquipId(equipId);
		firmwareHistory.setEquipKind(equipKind);
		firmwareHistory.setEquipModel(equipModel);
		firmwareHistory.setEquipType(equipType);
		firmwareHistory.setEquipVendor(equipVendor);
		firmwareHistory.setErrorCode(errorCode);
		firmwareHistory.setOtaState(otaState);
		firmwareHistory.setOtaStep(otaStep);
		firmwareHistory.setTriggerState(triggerState);
		firmwareHistory.setTriggerStep(triggerStep);
		firmwareHistory.setTrId(trId);

		firmWareManager.insertFirmHistory(firmwareHistory, param);
	}

	/**
	 * F/W File 저장 로직
	 * 
	 * @param request
	 * @throws IOException
	 */
	//	private void fwFileSave(MultipartHttpServletRequest multiReq, Map<String, String> paramMap) throws IOException {
	//		MultipartFile multipartFile = multiReq.getFile("otaFile");
	//
	//		byte[] fileBinary = multipartFile.getBytes();
	//		String filePath = multipartFile.getOriginalFilename();
	//		String[] fileInfo = filePath.split(",");
	//		String prodName = fileInfo[0]; // 제조사
	//		String modelName = fileInfo[1]; // 모델명
	//		String fileFullName = fileInfo[2]; // 파일명.확장자명
	//		String fileName = fileFullName.substring(0, fileFullName.lastIndexOf(".")); // File명 = FW버전
	//		String ext = fileFullName.substring(fileFullName.lastIndexOf(".") + 1); // 확장자명
	//		byte[] imgCrc16 = CRCUtil.Calculate_ZigBee_Crc(fileBinary, (char) 0x0000);
	//
	//		//파일 저장
	//		String osName = System.getProperty("os.name");
	//		String homePath = "";
	//		if (osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0) {
	//			homePath = CommandProperty.getProperty("soria.firmware.window.dir");
	//		} else {
	//			homePath = CommandProperty.getProperty("soria.firmware.dir");
	//		}
	//		String finalFilePath = makeFirmwareDirectory(homePath, otaType + "/" + prodName + "/" + modelName + "/", fileName, ext, true);
	//
	//		paramMap.put("fw_path", finalFilePath); // 서버에 저장된 파일경로
	//		paramMap.put("fw_size", Long.toString(multipartFile.getSize())); // 파일사이즈
	//		paramMap.put("fw_crc", Hex.decode(imgCrc16)); // CRC16
	//		paramMap.put("fw_model_name", modelName); // Model Name
	//		paramMap.put("fw_version", fileName); // File명 = FW버전
	//
	//		Path fPath = Paths.get(finalFilePath);
	//		Files.write(fPath, fileBinary);
	//
	//		logger.info("### [{}] Firmware file Save complete : {}({}byte) - {}", otaType, fileFullName, fileBinary.length, paramMap.toString());
	//	}

	/**
	 * 경로 생성
	 * 
	 * @param homePath
	 * @param subPath
	 * @param fileName
	 * @param ext
	 * @param deletable
	 * @return
	 */
	//	private String makeFirmwareDirectory(String homePath, String subPath, String fileName, String ext, boolean deletable) {
	//		File file = null;
	//		StringBuilder firmwareDir = new StringBuilder();
	//		firmwareDir.append(homePath);
	//		firmwareDir.append("/");
	//		firmwareDir.append(subPath);
	//
	//		file = new File(firmwareDir.toString());
	//		if (!file.exists()) {
	//			file.mkdirs();
	//		}
	//		firmwareDir.append("/");
	//		firmwareDir.append(fileName);
	//		firmwareDir.append(".");
	//		firmwareDir.append(ext);
	//
	//		file = new File(firmwareDir.toString());
	//
	//		boolean result = false;
	//		if (deletable && file.exists()) {
	//			result = file.delete();
	//		} else {
	//			result = true;
	//		}
	//
	//		if (!result) {
	//			//새로운 이름 규칙은 기존 이름+(n) 방식이다.
	//			if (fileName.matches(".*\\([0-9]*\\)")) {
	//				//기존 파일 이름이 중복 규칙에 의해 만들어진 파일 명이라면 숫자를 증가시켜 이름을 다시 만든다.
	//				int number = Integer.valueOf(fileName.replaceAll(".*\\(([0-9]*)\\)", "$1"));
	//				fileName = fileName.replaceAll("(.*)\\([0-9]*\\)", String.format("$1(%d)", number++));
	//			} else {
	//				// 파일 이름에 중복 이름 규칙을 적용한다.
	//				fileName = String.format("%s(0)", fileName);
	//			}
	//
	//			//중복되는지 제귀하여 확인한다.
	//			return makeFirmwareDirectory(homePath, subPath, fileName, ext, deletable);
	//		}
	//		return file.getPath();
	//	}

	private void saveAsyncCommandForSORIA(String deviceSerial, Long trId, String cmd, Map<String, String> param, String currentTime) throws Exception {
		AsyncCommandLog asyncCommandLog = new AsyncCommandLog();
		asyncCommandLog.setTrId(trId);
		asyncCommandLog.setMcuId(deviceSerial);
		//asyncCommandLog.setDeviceType(McuType.MMIU.name());
		asyncCommandLog.setDeviceType(otaType.name());
		asyncCommandLog.setDeviceId(deviceSerial);
		asyncCommandLog.setCommand(cmd);
		asyncCommandLog.setTrOption(TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode());
		asyncCommandLog.setState(1);
		asyncCommandLog.setOperator(OperatorType.OPERATOR.name());
		asyncCommandLog.setCreateTime(currentTime);
		asyncCommandLog.setRequestTime(currentTime);
		asyncCommandLog.setLastTime(null);
		asyncCommandLogManager.add(asyncCommandLog);

		Integer num = 0;
		if (param != null && param.size() > 0) {
			//parameter가 존재할 경우.
			Integer maxNum = asyncCommandLogManager.getParamMaxNum(deviceSerial, trId);

			if (maxNum != null) {
				num = maxNum + 1;
			}

			Iterator<String> iter = param.keySet().iterator();
			while (iter.hasNext()) {
				String key = iter.next();

				AsyncCommandParam asyncCommandParam = new AsyncCommandParam();
				asyncCommandParam.setMcuId(deviceSerial);
				asyncCommandParam.setNum(num);
				asyncCommandParam.setParamType(key);
				asyncCommandParam.setParamValue((String) param.get(key));
				asyncCommandParam.setTrId(trId);

				asyncCommandLogManager.addParam(asyncCommandParam);
				num += 1;
			}
		}
	}

	private String sendSMSForSORIA(String oid, String trnxId, String mobileNo, String command) {
		String result = "";

		try {
			int seq = new Random().nextInt(100) & 0xFF;

			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
			String smsClassPath = prop.getProperty("soria.smsClassPath");
			String serverIp = prop.getProperty("soria.server.sms.serverIpAddr") == null ? "" : prop.getProperty("soria.server.sms.serverIpAddr").trim();
			String serverPort = prop.getProperty("soria.server.sms.serverPort") == null ? "" : prop.getProperty("soria.server.sms.serverPort").trim();

			if ("".equals(serverIp) || "".equals(serverPort)) {
				result = "error";
				logger.debug("========>>> [{}] Message Send Error: Invalid Ip Address or port!", command);
			} else {
				String smsMsg = cmdMsg((byte) seq, oid, serverIp.replaceAll("\\.", ","), serverPort);
				SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
				Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
				//result = (String) m.invoke(obj, mobileNo.replace("-", "").trim(), smsMsg, prop);
				result = "success";
			}
		} catch (Exception e) {
			logger.error("sendSMSForSORIA Error - {}", e);
		}
		return result;
	}

	private String cmdMsg(byte seq, String oid, String ip, String port) {
		int sequence = (int) (seq & 0xFF);
		String smsMsg = "NT,";
		if (sequence >= 10 && sequence < 100) {
			smsMsg += "0" + sequence;
		} else if (sequence < 10) {
			smsMsg += "00" + sequence;
		} else {
			smsMsg += "" + sequence;
		}
		smsMsg += ",Q,B," + oid + "," + ip + "," + port;

		return smsMsg;
	}

	
}
