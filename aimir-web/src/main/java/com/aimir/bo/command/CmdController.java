package com.aimir.bo.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.common.CommandProperty;
import com.aimir.bo.common.CommonController;
import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.CircuitBreakerCondition;
import com.aimir.constants.CommonConstants.CircuitBreakerStatus;
import com.aimir.constants.CommonConstants.CommandType;
import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.MeterProgramKind;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemIFType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.dao.device.EventAlertLogDao;
import com.aimir.dao.device.MMIUDao;
import com.aimir.dao.device.MeterEventLogDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.dao.system.HomeGroupDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.esapi.AimirUser;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.DLMS_CLASS_ATTR;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.modem.EventLog;
import com.aimir.fep.modem.LPData;
import com.aimir.fep.protocol.coap.frame.CoapBrowserDecoder;
import com.aimir.fep.protocol.fmp.client.sms.SMSClient;
import com.aimir.fep.protocol.mrp.command.frame.sms.RequestFrame;
import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_NAME;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.protocol.smsp.SMSConstants.MESSAGE_TYPE;
import com.aimir.fep.protocol.smsp.command.frame.sms.ResponseFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.GroupTypeInfo;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUVar;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.PLCIU;
import com.aimir.model.device.SubGiga;
import com.aimir.model.device.ZBRepeater;
import com.aimir.model.device.ZEUPLS;
import com.aimir.model.device.ZMU;
import com.aimir.model.device.ZRU;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.HomeGroup;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.device.AsyncCommandLogManager;
import com.aimir.service.device.FirmWareManager;
import com.aimir.service.device.MCUManager;
import com.aimir.service.device.MbusSlaveIOModuleManager;
import com.aimir.service.device.MeterCtrlManager;
import com.aimir.service.device.MeterManager;
import com.aimir.service.device.MeterTimeManager;
import com.aimir.service.device.ModemManager;
import com.aimir.service.device.OperationLogManager;
import com.aimir.service.mvm.DataGapsManager;
import com.aimir.service.system.CircuitBreakerManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.ContractManager;
import com.aimir.service.system.GroupMgmtManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service(value = "cmdController")
@Controller
public class CmdController<V> {

	private static Log log = LogFactory.getLog(CmdController.class);
	private Object resMonitor = new Object();

	@Autowired
	ModemDao modemDao;
	
	@Autowired
	MMIUDao mmiuDao;
	
	@Autowired
	com.aimir.dao.device.MeterDao meterDao;
	
	@Autowired
	EventAlertLogDao eventAlertLogDao;
	
	@Autowired
	HibernateTransactionManager transactionManager;

	@Autowired
	MeterManager meterManager;

	@Autowired
	ModemManager modemManager;

	@Autowired
	MCUManager mcuManager;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	CodeDao codeDao;

	@Autowired
	MeterTimeManager meterTimeManager;

	@Autowired
	OperationLogManager operationLogManager;

	@Autowired
	OperatorManager operatorManager;

	@Autowired
	CodeManager codeManager;

	@Autowired
	DataGapsManager dataGapsManager;

	@Autowired
	CmdOperationUtil cmdOperationUtil;

	@Autowired
	MeterCtrlManager meterCtrlManager;

	@Autowired
	FirmWareManager firmWareManager;

	@Autowired
	HomeGroupDao homeGroupDao;

	@Autowired
	GroupMemberDao groupMemberDao;

	@Autowired
	GroupMgmtManager groupMgmtManager;

	@Autowired
	CircuitBreakerManager circuitBreakerManager;

	@Autowired
	ContractManager contractManager;

	@Autowired
	AsyncCommandLogManager asyncCommandLogManager;

	@Autowired
	AsyncCommandParamDao asyncCommandParamDao;

	@Autowired
	MeterEventLogDao meterEventLogDao;
	
    @Autowired
    MbusSlaveIOModuleManager mbusSlaveIOModuleManager;
	// final static ApplicationContext appCtx =
	// ContextLoader.getCurrentWebApplicationContext();

	protected boolean commandAuthCheck(String loginId, CommandType cmdType, String command) {

		Operator operator = operatorManager.getOperatorByLoginId(loginId);
		if(operator==null){
			return false; // wrong id
		}

		Role role = operator.getRole();
		Set<Code> commands = role.getCommands();
		Code codeCommand = null;
		if (role.getCustomerRole() != null && role.getCustomerRole()) {
			return false; // 고객 권한이면
		}

//      DELETE START SP-198
//		if (role.getMtrAuthority().equals("c")) {
//			return true; // 관리자면
//		}
//      DELETE END   SP-198
		
		for (Iterator<Code> i = commands.iterator(); i.hasNext();) {
			codeCommand = (Code) i.next();
			if (codeCommand.getCode().equals(command))
				return true; // 관리자가 아니라도 명령에 대한 권한이 있으면
		}

		/*
		 * if (CommandType.DeviceRead.equals(cmdType)) { String commandAuth =
		 * role.getMtrAuthority(); log.debug("commandAuth:"+commandAuth); if
		 * (!commandAuth.equals("c")) {// command 허용권한 체크 return false; } for
		 * (Iterator<Code> i = commands.iterator(); i.hasNext();) { codeCommand
		 * = (Code) i.next(); if (codeCommand.getCode().equals(command)) return
		 * true; } }
		 * 
		 * if (CommandType.DeviceWrite.equals(cmdType)) { String commandAuth =
		 * role.getSystemAuthority(); log.debug("commandAuth:"+commandAuth); if
		 * (!commandAuth.equals("c")) {// command 허용권한 체크
		 * log.debug("commandAuth:"+commandAuth); return false; } log.debug(
		 * "command size :"+commands.size()); for (Iterator<Code> i =
		 * commands.iterator(); i.hasNext();) {
		 * log.debug("commandAuth:"+commandAuth); codeCommand = (Code) i.next();
		 * log.debug("code: "+codeCommand.getCode()); if
		 * (codeCommand.getCode().equals(command)) return true; } }
		 */

		return false;
	}

	/**
	 * 집중기 그룹 추가 명령
	 * 
	 * @param target
	 *            - 그룹명
	 * @param mcuId
	 *            - 집중기 SysID
	 * @param loginId
	 *            - 등록하는 사용자 아이디
	 * @return
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/gadget/device/command/cmdGroupAdd")
	public ModelAndView cmdGroupAdd(@RequestParam(value = "mcuId", required = true) String mcuId,
			@RequestParam(value = "groupName", required = true) String groupName,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		int groupKey = -1;

		// TODO 허용 권한 체크에서 command 목록 사이즈를 0으로 가져오는 문제가 발생해 일단 막음
		/*
		 * if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.1")) {//
		 * TODO // opcode mav.addObject("rtnStr", "No permission");
		 * mav.addObject("status", status); return mav; }
		 */

		if (mcuId == null || "".equals(mcuId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			mav.addObject("status", status);
			return mav;
		}

		String rtnStr = "";

		MCU mcu = mcuManager.getMCU(mcuId);
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			groupKey = cmdOperationUtil.cmdGroupAdd(mcuId, groupName);
			status = ResultStatus.SUCCESS;
			rtnStr = status.name();
		} catch (Exception e) {
			rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
		}

		Code operationCode = codeManager.getCodeByCode("8.1.1");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), status.name());
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		mav.addObject("groupKey", groupKey);
		return mav;
	}

	/**
	 * 집중기에 등록한 그룹 삭제 명령
	 * 
	 * @param target
	 *            - 그룹명
	 * @param mcuId
	 *            - 집중기 SysID
	 * @param loginId
	 *            - 등록하는 사용자 아이디
	 * @return
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/gadget/device/command/cmdGroupDelete")
	public ModelAndView cmdGroupDelete(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "mcuId", required = false) String mcuId,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.1")) {// TODO
			// opcode
			mav.addObject("rtnStr", "No permission");
			mav.addObject("detail", "");
			return mav;
		}

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			mav.addObject("detail", "");
			return mav;
		}

		String rtnStr = "";

		MCU mcu = mcuManager.getMCU(mcuId);
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			cmdOperationUtil.cmdGroupDelete(mcuId, Integer.valueOf(target));
			status = ResultStatus.SUCCESS;
			rtnStr = status.name();
		} catch (Exception e) {
			rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
		}

		Code operationCode = codeManager.getCodeByCode("8.1.1");// TODO opcode
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), status.name());
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	/**
	 * 집중기 그룹의 멤버 추가
	 * 
	 * @param target
	 *            - 그룹명
	 * @param mcuId
	 *            - 집중기 SysID
	 * @param modem
	 *            - 모뎀 시리얼 번호
	 * @param loginId
	 *            - 등록하는 사용자 아이디
	 * @return
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/gadget/device/command/cmdGroupAddMember")
	public ModelAndView cmdGroupAddMember(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "mcuId", required = false) String mcuId,
			@RequestParam(value = "modem", required = false) String modem,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.1")) {// TODO
			// opcode
			mav.addObject("rtnStr", "No permission");
			mav.addObject("detail", "");
			return mav;
		}

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			mav.addObject("detail", "");
			return mav;
		}

		String rtnStr = "";

		MCU mcu = mcuManager.getMCU(mcuId);
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			cmdOperationUtil.cmdGroupAddMember(mcuId, Integer.valueOf(target), modem);
			status = ResultStatus.SUCCESS;
			rtnStr = status.name();
		} catch (Exception e) {
			rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
		}

		Code operationCode = codeManager.getCodeByCode("8.1.1");// TODO opcode
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), status.name());
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	/**
	 * 집중기 그룹의 멤버 삭제
	 * 
	 * @param target
	 *            - 그룹명
	 * @param mcuId
	 *            - 집중기 SysID
	 * @param modem
	 *            - 모뎀 시리얼 번호
	 * @param loginId
	 *            - 등록하는 사용자 아이디
	 * @return
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/gadget/device/command/cmdGroupDeleteMember")
	public ModelAndView cmdGroupDeleteMember(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "mcuId", required = false) String mcuId,
			@RequestParam(value = "modem", required = false) String modem,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.1")) {// TODO
			// opcode
			mav.addObject("rtnStr", "No permission");
			mav.addObject("detail", "");
			return mav;
		}

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			mav.addObject("detail", "");
			return mav;
		}

		String rtnStr = "";

		MCU mcu = mcuManager.getMCU(mcuId);
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			cmdOperationUtil.cmdGroupDeleteMember(mcuId, Integer.valueOf(target), modem);
			status = ResultStatus.SUCCESS;
			rtnStr = status.name();
		} catch (Exception e) {
			rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
		}

		Code operationCode = codeManager.getCodeByCode("8.1.1");// TODO opcode
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), status.name());
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	/**
	 * 집중기 그룹별 명령 호출
	 * 
	 * @param target
	 *            - 그룹명
	 * @param mcuId
	 *            - 집중기 SysID
	 * @param command
	 *            - 명령 코드
	 * @param loginId
	 *            - 등록하는 사용자 아이디
	 * @return
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/gadget/device/command/cmdGroupAsyncCall")
	public ModelAndView cmdGroupAsyncCall(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "mcuId", required = false) String mcuId,
			@RequestParam(value = "command", required = false) String command,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.1")) {// TODO
			// opcode
			mav.addObject("rtnStr", "No permission");
			mav.addObject("detail", "");
			return mav;
		}

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			mav.addObject("detail", "");
			return mav;
		}

		String rtnStr = "";

		MCU mcu = mcuManager.getMCU(mcuId);
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			cmdOperationUtil.cmdGroupAsyncCall(mcuId, Integer.valueOf(target), command,
					TR_OPTION.ASYNC_OPT_RETURN_DATA_EVT.getCode(), 0, 0, 1);
			status = ResultStatus.SUCCESS;
			rtnStr = status.name();
		} catch (Exception e) {
			rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
		}

		Code operationCode = codeManager.getCodeByCode("8.1.1");// TODO opcode
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), status.name());
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

    /**
     * Hex ObisCode => x.x.x.x.x.x 
     * 
     * @param obisCode
     * @return
     */
    private String convertObis(String obisCode) {
    	String returnData = "";
    	if(obisCode.length() == 12) {
    		byte[] obisCodeArr = Hex.encode(obisCode);
    		obisCode="";
    		for (int i = 0; i < obisCodeArr.length; i++) {
    			if(i == 0) {
    				obisCode += DataUtil.getIntToByte(obisCodeArr[i]);
    			} else {
    				obisCode += "."+DataUtil.getIntToByte(obisCodeArr[i]);
    			}
			}
    		returnData = obisCode;
    	} else {
    		returnData = "Wrong Obis";
    	}
    	
    	return returnData;
    }	

    private Map<String,String> eventLogValueByRange(String fromDate, String toDate) throws Exception {
    	Map<String,String> valueMap = new HashMap<String,String>();
    	
    	String clockObis = this.convertObis(OBIS.CLOCK.getCode());
		String option="1";	//option 0 is offset, option 1 is range_descriptor(date). but not yet implement offset.
		
    	valueMap.put("clockObis", clockObis);
		valueMap.put("option", option);
		Calendar fromCal = null;
		if (fromDate != null && !fromDate.equals("")) {
			fromCal = DateTimeUtil.getCalendar(fromDate);
		} else {
			fromCal = Calendar.getInstance();
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		fromDate = formatter.format(fromCal.getTime());
		valueMap.put("fYear", fromDate.substring(0,4));
		valueMap.put("fMonth", fromDate.substring(4,6));
		valueMap.put("fDayOfMonth", fromDate.substring(6,8));
		valueMap.put("fDayOfWeek", String.valueOf(fromCal.get(Calendar.DAY_OF_WEEK)));
		valueMap.put("fHh", fromDate.substring(8,10));
		valueMap.put("fMm", fromDate.substring(10,12));
		valueMap.put("fSs", fromDate.substring(12,14));
		
		Calendar toCal = null;
		if (toDate != null && !toDate.equals("")) {
			toCal = DateTimeUtil.getCalendar(toDate);
		} else {
			toCal = Calendar.getInstance();
		}
		toDate = formatter.format(toCal.getTime());
		
		valueMap.put("tYear", toDate.substring(0,4));
		valueMap.put("tMonth", toDate.substring(4,6));
		valueMap.put("tDayOfMonth", toDate.substring(6,8));
		valueMap.put("tDayOfWeek", String.valueOf(toCal.get(Calendar.DAY_OF_WEEK)));
		valueMap.put("tHh", toDate.substring(8,10));
		valueMap.put("tMm", toDate.substring(10,12));
		valueMap.put("tSs", toDate.substring(12,14));

		return valueMap;
    }

    private String meterParamMapToJSON(Map map) {
        StringBuffer rStr = new StringBuffer();
        Iterator<String> keys = map.keySet().iterator();
        String keyVal = null;
        rStr.append("[{");
        while (keys.hasNext()) {
            keyVal = (String) keys.next();
            rStr.append("\""+keyVal+"\":");
            rStr.append("\""+map.get(keyVal)+"\"");
            if (keys.hasNext()) {
                rStr.append(",");
            }
        }
        rStr.append("}]");
        return rStr.toString();
    }
	
    private ModelAndView onDemandMeterBypassMBB(String mdsId, String fromDate, String toDate, String loginId) {
		ModelAndView mav = new ModelAndView("jsonView");
		ResultStatus status = ResultStatus.FAIL;
		Meter meter = null;
		String cmd = "cmdMeterParamGet";
        String detailInfo = "";
        String rtnStr = "";
        
        JSONArray jsonArr = null;
        try{
    		if (loginId != null ){
    			if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.1")) {
    				throw new Exception("No permission");
    			}
    		}
    		
    		meter = meterManager.getMeter(mdsId);
            int modemPort = 0;
			if ( meter.getModemPort() != null ){
				modemPort = meter.getModemPort().intValue();
			}
			if ( modemPort > 5){
				throw new Exception("ModemPort:" + modemPort + " is not Support");
			}    		
    		
			Map<String,String> paramMap = new HashMap<String,String>();
			if (modemPort==0) {
		    	log.debug("cmdGetLoadProfile ["+ mdsId + "][" + modemPort +  "]["  +  fromDate + "][" +toDate +"]");

		    	String obisCode = this.convertObis(OBIS.ENERGY_LOAD_PROFILE.getCode());
				int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
				int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();
				
				Map<String,String> valueMap = eventLogValueByRange(fromDate,toDate);
				String value = meterParamMapToJSON(valueMap);
				
				log.debug("obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);

				//paramGet
    			paramMap.put("paramGet", obisCode+"|"+classId+"|"+attrId+"|null|null|"+value);				
			}
			else {
		    	log.debug("cmdGetLoadProfile ["+ mdsId + "][" + modemPort +  "]["  +  fromDate + "][" +toDate +"]");
				
		    	String obisCode = this.convertObis(OBIS.MBUSMASTER_LOAD_PROFILE.getCode());
				int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
				int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();
				
				Map<String,String> valueMap = eventLogValueByRange(fromDate,toDate);
				String value = meterParamMapToJSON(valueMap);
				
				log.debug("obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);

    			//paramGet
    			paramMap.put("paramGet", obisCode+"|"+classId+"|"+attrId+"|null|null|"+value);
			}
			paramMap.put("option", "ondemandmbb");				

				        
    		Map<String,Object> map = new HashMap<String,Object>();
    		try{        		
            	if(meter != null && meter.getModem() != null) {
        			Modem modem = meter.getModem();
        			if(modem.getModemType() == ModemType.MMIU && (modem.getProtocolType() == Protocol.SMS 
        					|| modem.getProtocolType() == Protocol.IP 
        					|| modem.getProtocolType() == Protocol.GPRS)) {
            			Map<String,Object> condition = new HashMap<String,Object>();
	            		condition.put("modemId", meter.getModemId());
	            		condition.put("modemType", ModemType.MMIU.toString());
	            		MMIU mmiu = (MMIU)modemManager.getModemByType(condition);
	            		
	            		map.put("meterId", mdsId);
	            		map.put("modemType", meter.getModem().getModemType().name());
	            		map.put("protocolType", meter.getModem().getProtocolType());
	            		map.put("modem", mmiu);
            		}
            	} else {
            		rtnStr = "FAIL : Target ID null!";
            	}
    		}catch(Exception e) {
    			log.warn(e,e);
        		rtnStr = "FAIL : Target ID null!";
    		}
	        
        	try{
        		if(map.get("modemType") == ModemType.MMIU.name() && map.get("protocolType") == Protocol.SMS) {
        			MMIU mmiu = (MMIU)map.get("modem");
	        		
	        		String mobileNo = mmiu.getPhoneNumber();
	            	if (mobileNo == null || "".equals(mobileNo)) {
	            		log.warn(String.format("[" + cmd + "] Phone number is empty"));
	            		rtnStr = "FAIL : Phone number is empty!";
	        		}            	
	            	else if (!Protocol.SMS.equals(mmiu.getProtocolType())) {
	            		log.warn(String.format("[" + cmd + "] Invalid ProtocolType"));
	            		rtnStr = "FAIL : Invalid ProtocolType!";
	    			}
	            	else {	            	
						Long trId = System.currentTimeMillis();
						Map<String, String> result;
						String cmdResult = "";
						
						result = sendSmsForCmdServer(mmiu, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), cmd, paramMap); 						
						if(result != null){
								cmdResult = "Success";
								
								status = ResultStatus.SUCCESS;
								detailInfo = result.get("detail").toString();
				                log.info("detailInfo[" + detailInfo + "]");
				                Map tmpMap = parseDetailMessageForMBB(
	                                    detailInfo);
				        		detailInfo = makeHTML(tmpMap);
				                log.info("detailInfo(converted)[" + detailInfo + "]");
				                ///////////////////////
				                if (meter.getModem() != null) {
				                	meter.getModem().setCommState(1);
				                }
						}else{
								log.debug("SMS Fail");
								//cmdResult="Failed to get the resopone. See the Async Command History.";
								cmdResult="Check the Async_Command_History.";								
						}						
	
						rtnStr = cmdResult;
	            	}
				} 
			} catch (Exception e) {
				log.error(e, e);
				rtnStr = "Check the Async_Command_History";
			}

		} catch (Exception e) {
			log.error(e, e);
			rtnStr = "FAIL : " + e.getMessage();
		}

        if ( meter != null ){
    		Code operationCode = codeManager.getCodeByCode("8.1.10");
    		if (operationCode != null) {
    			operationLogManager.saveOperationLog(meter.getSupplier(), 
    					meter.getMeterType(), meter.getMdsId(), loginId,
						operationCode, status.getCode(), status.name());
    		}

        }
	
		mav.addObject("status", status.name());
		mav.addObject("meterId", meter.getMdsId());
		mav.addObject("detail", detailInfo);        
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}
    private Map parseDetailMessageForMBB(String detail) {
        Map map = new LinkedHashMap<String,String>();
        BufferedReader br = new BufferedReader(new StringReader(detail));
        String b = null;
        try {
            b = br.readLine();
            while(b!=null) {
                if(!b.trim().equals("")) {
                    String[] c = b.split(":");
                    if(c.length>1) {
                        String [] d = b.split(" ");
                        String tempKey = d[0] +" " + d[1] + " " + d[2] + " " + d[3] + " " + d[4];
                        String tempValue = b.replaceAll(tempKey, "").trim();
                        map.put(tempKey,tempValue);
                    } else {
                        if(b.startsWith("Cumulative active energy -import")) {
                            map.put("Cumulative active energy -import", b.replaceAll("Cumulative active energy -import", "").trim());
                        } else if(b.startsWith("Cumulative active energy -export")) {
                            map.put("Cumulative active energy -export", b.replaceAll("Cumulative active energy -export", "").trim());
                        } else if(b.startsWith("Cumulative reactive energy -import")) {
                            map.put("Cumulative reactive energy -import", b.replaceAll("Cumulative reactive energy -import", "").trim());
                        } else if(b.startsWith("Cumulative reactive energy -export")) {
                            map.put("Cumulative reactive energy -export", b.replaceAll("Cumulative reactive energy -export", "").trim());
                        }
                    }
                }
                b = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
    private ModelAndView romReadBypassMBB(String mdsId, String fromDate, String toDate, String loginId) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	ResultStatus status = ResultStatus.FAIL;
    	Meter meter = null;
    	String cmd = "cmdGetROMRead";

    	meter = meterManager.getMeter(mdsId);
    	Modem modem = meter.getModem();

    	if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.1")) {
    		mav.addObject("rtnStr", "No permission");
    		mav.addObject("status", status.name());
    		mav.addObject("detail", "");
    		return mav;
    	}

    	if ( modem == null ){
    		mav.addObject("rtnStr", "Target ID null!");
    		mav.addObject("status", status.name());
    		mav.addObject("detail", "");
    		return mav;
    	}

    	try{
    		if( ((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
    			Map<String, String> asyncResult = new HashMap<String, String>();
    			Map<String, String> paramMap = new HashMap<String, String>();
    			
    	        fromDate = (fromDate == null || "".equals(fromDate)) ? TimeUtil
    	                .getCurrentDay()
    	                : fromDate;
    	        toDate = (toDate == null || "".equals(toDate)) ? TimeUtil
    	                .getCurrentDay()
    	                : toDate;
    			
    	                
    			paramMap.put("meterId", mdsId);
    			paramMap.put("fromDate",fromDate);
    			paramMap.put("toDate", toDate);
    			
       			asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), cmd, paramMap);	

    			if(asyncResult != null){
    				status = ResultStatus.SUCCESS;
    			}else{
    				log.debug("SMS Fail");
    				mav.addObject("rtnStr", "Check the results in Async History tab");
    				mav.addObject("status", status.name());
    				mav.addObject("detail", "");
    				return mav;
    			}
    			// SUCCESS
    			for (String key : asyncResult.keySet()) {
	                if ( key.equals("detail") ){
		                //// Convert escape char
	                	String value = asyncResult.get(key).toString() ;
		        		ObjectMapper mapper = new ObjectMapper();
		        		String json = "{\"key\":\""+ value + "\"}";
		        		Map<String, Object> tmpMap = new HashMap<String, Object>();
		        		tmpMap = mapper.readValue(json, new TypeReference<Map<String, String>>(){});
		            	log.debug("detail[" +(String)tmpMap.get("key") +"]" );
		            	mav.addObject(key, (String)tmpMap.get("key"));
	                }
	                else {
	                	mav.addObject(key, asyncResult.get(key).toString());
	                }
    			}
    			mav.addObject("status", status.name());
    			return mav;
    		}
    		else {
    			mav.addObject("rtnStr", "Invalid Type!");
    			mav.addObject("status", status.name());
    			mav.addObject("detail", "");
    			return mav;
    		}
    	}catch(Exception e){
    		log.debug(e,e);
    		mav.addObject("rtnStr", "Check the results in Async History tab");
    		mav.addObject("status", status.name());
    		return mav;
    	}
    }

//    // -> INSERT START 2016/09/13 SP-117
//    private void updateMeterStatusCutOff(Meter meter) {
//		String rtnStr = "";
//        TransactionStatus txStatus = null;
//		DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
//		txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
//        
//        try {
//        	txStatus = transactionManager.getTransaction(txDefine);
//            
//            meter = meterDao.get(meter.getMdsId());
//            meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()));
//            
//            Contract contract = meter.getContract();
//            if(contract != null && (contract.getStatus() == null
//                    || contract.getStatus().getCode().equals(CommonConstants.ContractStatus.NORMAL.getCode()))) {
//                Code pauseCode = CommonConstants.getContractStatus(CommonConstants.ContractStatus.PAUSE.getCode());
//                contract.setStatus(pauseCode);
//            }
//            
//            transactionManager.commit(txStatus);
//        }
//        catch (Exception e) {
//			rtnStr = e.getMessage();
//            if (txStatus != null) transactionManager.rollback(txStatus);
//        }
//    }
//    
//    protected void updateMeterStatusNormal(Meter meter) {
//		String rtnStr = "";
//        TransactionStatus txStatus = null;
//		DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
//		txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
//
//		try {
//        	txStatus = transactionManager.getTransaction(txDefine);
//            
//            meter = meterDao.get(meter.getMdsId());
//            meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));
//            
//            Contract contract = meter.getContract();
//            if(contract != null && (contract.getStatus() == null 
//                    || contract.getStatus().getCode().equals(CommonConstants.ContractStatus.PAUSE.getCode()))) {
//                Code normalCode = CommonConstants.getContractStatus(CommonConstants.ContractStatus.NORMAL.getCode());
//                contract.setStatus(normalCode);
//            }
//            transactionManager.commit(txStatus);
//        }
//        catch (Exception e) {
//			rtnStr = e.getMessage();
//            if (txStatus != null) transactionManager.rollback(txStatus);
//        }
//    }
//    // <- INSERT END   2016/09/13 SP-117
	
	/**
	 * 미터 온디맨드 명령 (모든 미터 타입에 보편적으로 적용)
	 * 
	 * @param target
	 *            - 미터아이디(미터시리얼번호)
	 * @param loginId
	 *            - 사용자 아이디
	 * @param fromDate
	 *            - 온디맨드 요청 시작시간 (yyyymmdd)
	 * @param toDate
	 *            - 온디맨드 요청 종료 시간 (yyyymmdd)
	 * @return
	 */
	// UPDATE SP-179
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/gadget/device/command/cmdOnDemand")
//	public ModelAndView cmdOnDemand(@RequestParam(value = "target", required = false) String target,
//	@RequestParam(value = "loginId", required = false) String loginId,
//	@RequestParam(value = "fromDate", required = false) String fromDate,
//	@RequestParam(value = "toDate", required = false) String toDate) {
	public ModelAndView cmdOnDemand(@RequestParam(value = "target", required = false) String target, 
	@RequestParam(value = "loginId", required = false) String loginId, 
	@RequestParam(value = "fromDate", required = false) String fromDate, 
	@RequestParam(value = "toDate", required = false) String toDate, 
	@RequestParam(value = "type", required = false) String type) {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");

		String checkCode = null;
		if ( "MCU".equals(type)) {
			checkCode = "8.1.1";
		}
		else if ( "MODEM".equals(type)){
			checkCode = "8.1.2";
		}
		else if ( "METER".equals(type)){
			checkCode = "8.1.3";
		}
		else {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Invalid Type!");
			mav.addObject("detail", "");
			return mav;
		}

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, checkCode)) {
			mav.addObject("rtnStr", "No permission");
			mav.addObject("detail", "");
			return mav;
		}

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			mav.addObject("detail", "");
			return mav;
		}

		Map<?, ?> result = null;
		String rtnStr = "";
		String detail = "";
		Meter meter = meterManager.getMeter(Integer.parseInt(target));
		//Modem modem = modemManager.getModem(meter.getModemId());
		Modem modem = meter.getModem();
		Supplier supplier = meter.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		boolean isSMSModem = isSMSModem(modem);

		try {
			// 프로토콜확인
			String nOption = "";
			if (modem != null) {
				//modem = modemManager.getModem(modem.getId());
       			if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) {
				//if( (modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS) ) {
					if (fromDate != null && toDate != null) {
						if (type == null ||  "METER".equals(type) ) {
							mav = this.onDemandMeterBypassMBB(meter.getMdsId(), fromDate, toDate, loginId);
							log.info("send sms command : onDemandMeterBypassMBB");
						}
						else if ( "MODEM".equals(type)){
							mav = this.romReadBypassMBB(meter.getMdsId(), fromDate, toDate, loginId);
							log.info("send sms command : romReadBypassMBB");
						}
					} else {
						mav = this.onDemandMeterBypassMBB(meter.getMdsId(), fromDate, toDate, loginId);
	       				log.info("send sms command : onDemandMeterBypassMBB");
					}

					return mav;
//				}
//				else if (modem.getProtocolType() == Protocol.SMS || modem.getProtocolType() == Protocol.GPRS
//						|| modem.getProtocolType() == Protocol.GSM || modem.getProtocolType() == Protocol.CDMA) {
//					log.info("send sms command : " + RequestFrame.CMD_ONDEMAND);
//
//					int seq = new Random().nextInt(100) & 0xFF;
//					if (fromDate != null && toDate != null) {
//						// SMS명령어 전송
//						cmdOperationUtil.cmdSendSMS(modem.getDeviceSerial(), RequestFrame.CMD_ONDEMAND,
//								String.valueOf(seq), RequestFrame.BG, fromDate, toDate);
//					} else {
//						// SMS명령어 전송
//						cmdOperationUtil.cmdSendSMS(modem.getDeviceSerial(), RequestFrame.CMD_ONDEMAND,
//								String.valueOf(seq), RequestFrame.BG);
//					}
//
//					log.info("end send sms command");
//
//					rtnStr = "SUCCESS : Send SMS Command(OnDemand).";
//
//					if (isGPRSModem) {
//						// 상태가 바뀌는 시간을 기다려주기 위해 30초 sleep
//						Thread.sleep(30000);
//						Integer lastStatus = asyncCommandLogManager.getCmdStatus(modem.getDeviceSerial(),
//								"cmdOndemandMetering");
//						if (TR_STATE.Success.getCode() != lastStatus) {
//							status = ResultStatus.FAIL;
//							rtnStr = "FAIL : Communication Error(Ondemand)";
//						}
//					}
				} else {
					if (fromDate != null && toDate != null) {
						// UPDATE SP-179
						//result = cmdOperationUtil.doOnDemand(meter, 0, "admin", nOption, fromDate, toDate);
						if (type == null ||  "METER".equals(type) ) {
							result = cmdOperationUtil.doOnDemand(meter, 0, "admin", nOption, fromDate, toDate);
						} else if ("MCU".equals(type)){
							// UPDATE START SP-633
//							result = cmdOperationUtil.cmdGetMeteringData(meter, 0, "admin", nOption, fromDate, toDate);
							result = cmdOperationUtil.cmdGetMeteringData(meter, 0, "admin", nOption, fromDate, toDate, null);
							// UPDATE END SP-633
						}else if ("MODEM".equals(type)){
							// UPDATE START SP-759
							if (modem.getModemType() == ModemType.MMIU && 
									(modem.getProtocolType() == Protocol.IP	|| modem.getProtocolType() == Protocol.GPRS)){
								result = cmdOperationUtil.cmdGetROMRead(meter, 0, "admin", nOption, fromDate, toDate);
							}else{
							// UPDATE START SP-632
//							result = cmdOperationUtil.cmdGetROMRead(meter, 0, "admin", nOption, fromDate, toDate);
								result = cmdOperationUtil.cmdDmdNiGetRomRead(meter, 0, "admin", fromDate, toDate);
							// UPDATE END SP-632
							}
							// UPDATE END SP-759
						}
					} else {
						result = cmdOperationUtil.doOnDemand(meter, 0, "admin", nOption, "", "");
					}
				}
			}else{
				log.info("no modem object");
                detail = "No Modem Object";
			}

			if (result != null && !isSMSModem) {
				rtnStr = (String) result.get("result");
				detail = (String) result.get("detail");
				if(rtnStr.equals("Success")) status = ResultStatus.SUCCESS; // SP-792
				else status = ResultStatus.FAIL;
			}

			Code operationCode = null;
            if ("MCU".equals(type)) {
                operationCode = codeManager.getCodeByCode("8.1.1");
            } else if ("MODEM".equals(type)) {
                operationCode = codeManager.getCodeByCode("8.1.2");
            } else if ("METER".equals(type)) {
                operationCode = codeManager.getCodeByCode("8.1.3");
            } else {
                operationCode = codeManager.getCodeByCode("8.1.1");
            }
			if (operationCode != null) {
				operationLogManager.saveOperationLog(supplier, meter.getMeterType(), meter.getMdsId(), loginId,
						operationCode, status.getCode(), rtnStr);
			}
		} catch (Exception e) {
			rtnStr = e.getMessage();
		}
		mav.addObject("status", status.name());
		mav.addObject("meterId", meter.getMdsId());
		mav.addObject("rtnStr", rtnStr);
		mav.addObject("detail", detail);
		return mav;
	}
    /**
     * 미터 온디맨드 명령 (그룹 커맨드)
     * 미터가젯 그룹커맨드에서 meterId가 아니라 meterMDS를 보낼수밖에 없어서 작성
     * Get the meterId using by meterMds
     */
    @SuppressWarnings("static-access")
    @RequestMapping(value = "/gadget/device/command/cmdGrpOnDemand")
    public ModelAndView cmdGrpOnDemand(@RequestParam(value = "meterMds", required = false) String meterMds,
                                    @RequestParam(value = "loginId", required = false) String loginId,
                                    @RequestParam(value = "fromDate", required = false) String fromDate,
                                    @RequestParam(value = "toDate", required = false) String toDate,
                                    @RequestParam(value = "type", required = false) String type){

        ResultStatus status = ResultStatus.FAIL;
        ModelAndView mav = new ModelAndView("jsonView");

        if(meterMds == null || "".equals(meterMds)){
            status = ResultStatus.INVALID_PARAMETER;
            mav.addObject("rtnStr", "Target ID is null!");
            mav.addObject("detail", status.name());
            return mav;
        }

        Meter meter = meterManager.getMeter(meterMds);
        if(meter == null){
            status = ResultStatus.INVALID_PARAMETER;
            mav.addObject("rtnStr", "Target meter is null!");
            mav.addObject("detail", status.name());
            return mav;
        }

        // call 'cmdOnDemand' function
        try{
            String meterId = meter.getId().toString();
            mav = this.cmdOnDemand(meterId,loginId,fromDate,toDate,type);
        }catch(Exception e){
            log.error(e,e);
            status = ResultStatus.FAIL;
            mav.addObject("rtnStr", "Ondemand Failure");
            mav.addObject("detail", status.name());
        }

        return mav;
    }

	/**
	 * 미터 온디맨드 명령 (모든 미터 타입에 보편적으로 적용)
	 * 
	 * @param target
	 *            - 미터아이디(미터시리얼번호)
	 * @param loginId
	 *            - 사용자 아이디
	 * @param fromDate
	 *            - 온디맨드 요청 시작시간 (yyyymmdd)
	 * @param toDate
	 *            - 온디맨드 요청 종료 시간 (yyyymmdd)
	 * @param nOption
	 *            - 온디맨드 option(All, Billiing, Event)
	 * @return
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/gadget/device/command/cmdOnDemandWithOption")
	public ModelAndView cmdOnDemandWithOption(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate,
			@RequestParam(value = "nOption", required = false) String nOption) {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.1")) {
			mav.addObject("rtnStr", "No permission");
			mav.addObject("detail", "");
			return mav;
		}

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			mav.addObject("detail", "");
			return mav;
		}

		Map<?, ?> result = null;
		String rtnStr = "";
		String detail = "";
		Meter meter = meterManager.getMeter(Integer.parseInt(target));
		Supplier supplier = meter.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		try {
			// 모뎀확인
			Modem modem = meter.getModem();
			if (modem != null) {
				modem = modemManager.getModem(modem.getId());
				if (modem.getProtocolType() == Protocol.SMS || modem.getProtocolType() == Protocol.GPRS
						|| modem.getProtocolType() == Protocol.GSM || modem.getProtocolType() == Protocol.CDMA) {
					log.info("send sms command : " + RequestFrame.CMD_ONDEMAND);

					if (fromDate != null && toDate != null) {
						// SMS명령어 전송
						cmdOperationUtil.cmdSendSMS(modem.getDeviceSerial(), RequestFrame.CMD_ONDEMAND,
								String.valueOf(SMSClient.getSEQ()), RequestFrame.BG, fromDate, toDate);
					} else {
						// SMS명령어 전송
						cmdOperationUtil.cmdSendSMS(modem.getDeviceSerial(), RequestFrame.CMD_ONDEMAND,
								String.valueOf(SMSClient.getSEQ()), RequestFrame.BG);
					}

					log.info("end send sms command");
					Map<String, String> res = new HashMap<String, String>();
					res.put("result", "SUCCESS : Send SMS Command(OnDemand).");
					result = res;
				} else {
					if (fromDate != null && toDate != null) {
						result = cmdOperationUtil.doOnDemand(meter, 0, "admin", nOption, fromDate, toDate);
					} else {
						result = cmdOperationUtil.doOnDemand(meter, 0, "admin", nOption, "", "");
					}
				}
			}

			if (result != null) {
				status = ResultStatus.SUCCESS;
				rtnStr = (String) result.get("result");
				detail = (String) result.get("detail");
			}

			Code operationCode = codeManager.getCodeByCode("8.1.1");
			if (operationCode != null) {
				operationLogManager.saveOperationLog(supplier, meter.getMeterType(), meter.getMdsId(), loginId,
						operationCode, status.getCode(), rtnStr);
			}
		} catch (Exception e) {
			rtnStr = e.getMessage();
		}
		mav.addObject("status", status.name());
		mav.addObject("meterId", meter.getMdsId());
		mav.addObject("rtnStr", rtnStr);
		mav.addObject("detail", detail);
		return mav;
	}

	/**
	 * 누락 데이터 재검침
	 * 
	 * @param searchStartDate
	 *            - 검침시작날짜
	 * @param searchEndDate
	 *            - 검침종료날짜
	 * @param searchDateType
	 *            - 검침데이터 타입
	 * @param meterType
	 *            - 미터 타입
	 * @param supplierId
	 *            - 공급자 아이디
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	@RequestMapping(value = "/gadget/device/command/cmdOnDemandRecollect")
	public ModelAndView cmdOnDemandRecollect(@RequestParam("searchStartDate") String searchStartDate,
			@RequestParam("searchEndDate") String searchEndDate, @RequestParam("searchDateType") String searchDateType,
			@RequestParam("meterType") String meterType, @RequestParam("supplierId") String supplierId) {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("searchStartDate", searchStartDate);
		params.put("searchEndDate", searchEndDate);
		params.put("searchDateType", searchDateType);
		params.put("meterType", meterType);
		params.put("supplierId", supplierId);

		List<Object> resultList = dataGapsManager.getLpMissingMeters(params);
		boolean tempBool = false;
		for (Object obj : resultList) {
			if (!tempBool) {
				tempBool = true;
				continue;
			}
			for (Object obj2 : (List<Object>) obj) {
				HashMap<String, Object> resultMap = (HashMap<String, Object>) obj2;
				// 미터 아이디로 미터 객체 생성
				Meter meter = meterManager.getMeter(Integer.parseInt(resultMap.get("meterId").toString()));

				String fromDate = searchStartDate + "0000"; // 검침 시작 시각 및 분 하드코딩
				String toDate = searchStartDate + "2359";

				try {
					if (fromDate != null && toDate != null) {
						cmdOperationUtil.doOnDemand(meter, 0, "admin", "", fromDate, toDate);
					} else {
						cmdOperationUtil.doOnDemand(meter, 0, "admin", "", "", "");
					}
				} catch (Exception e) {
					e.getMessage();
				}
			}
		}

		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("result", resultList);

		return mav;
	}

	/**
	 * 누락 데이터 재검침
	 * 
	 * @param searchStartDate
	 *            - 검침시작날짜
	 * @param searchEndDate
	 *            - 검침종료날짜
	 * @param searchDateType
	 *            - 검침데이터 타입
	 * @param meterType
	 *            - 미터 타입
	 * @param supplierId
	 *            - 공급자 아이디
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdOnDemandMeteringFailureRecollect")
	public ModelAndView cmdOnDemandMeteringFailureRecollect(@RequestParam("searchStartDate") String searchStartDate,
			@RequestParam("searchEndDate") String searchEndDate, @RequestParam("searchDateType") String searchDateType,
			@RequestParam("meterType") String meterType, @RequestParam("supplierId") String supplierId,
			@RequestParam("meterIdStr") String meterIdStr) {

		String fromDate = searchStartDate + "0000"; // 검침 시작 시각 및 분 하드코딩
		String toDate = searchStartDate + "2359";
		String[] array = meterIdStr.split(":");
		for (int i = 1; i < array.length; i++) {
			Meter meter = meterManager.getMeter(array[i]);

			try {
				if (fromDate != null && toDate != null) {
					cmdOperationUtil.doOnDemand(meter, 0, "admin", "", fromDate, toDate);
				} else {
					cmdOperationUtil.doOnDemand(meter, 0, "admin", "", "", "");
				}
			} catch (Exception e) {
				e.getMessage();
			}

		}

		ModelAndView mav = new ModelAndView("jsonView");
		// mav.addObject("result", resultList );

		return mav;
	}

	/**
	 * String 날짜 정보를 포멧을 변경한다.
	 * 
	 * @param srcFormat
	 *            기존 날짜 포멧
	 * @param destFormat
	 *            변경할 날짜 포멧
	 * @param dateTime
	 *            날짜 데이터
	 * @return
	 */
	private String convertTimeFormat(SimpleDateFormat destFormat, String dateTime, String modelName) {

		SimpleDateFormat srcFormat = null;
		final SimpleDateFormat srcFormat1 = new SimpleDateFormat("yyMMddHHmmss");
		final SimpleDateFormat srcFormat2 = new SimpleDateFormat("yyyyMMddHHmmss");
		final SimpleDateFormat srcFormat3 = new SimpleDateFormat("yyyyMMddHHmm");

		if ((dateTime.length() == 12) && ("NJC 130820A".equals(modelName) || "NJC 130821A".equals(modelName))) {
			srcFormat = srcFormat3;
		} else if (dateTime.length() == 12) {
			srcFormat = srcFormat1;
		} else if (dateTime.length() == 14) {
			srcFormat = srcFormat2;
		}

		if (srcFormat == null || destFormat == null)
			return dateTime;

		try {
			Date date = srcFormat.parse(dateTime);
			return destFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return dateTime;
	}

	// INSERT START SP-279
    private ModelAndView cmdMeterTimeSyncMBB(String mdsId, String loginId) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	ResultStatus status = ResultStatus.FAIL;
    	Meter meter = null;
    	String cmd = "cmdSyncTime";

    	meter = meterManager.getMeter(mdsId);
    	Modem modem = meter.getModem();

    	if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.7")) {
    		mav.addObject("rtnStr", "No permission");
    		mav.addObject("status", status.name());
    		mav.addObject("detail", "");
    		return mav;
    	}

    	if ( modem == null ){
    		mav.addObject("rtnStr", "Target ID null!");
    		mav.addObject("status", status.name());
    		mav.addObject("detail", "");
    		return mav;
    	}

    	try{
    		if( ((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
    			Map<String, String> asyncResult = new HashMap<String, String>();
    			Map<String, String> paramMap = new HashMap<String, String>();

				String obisCode = this.convertObis(OBIS.CLOCK.getCode());
				int classId = DLMS_CLASS.CLOCK.getClazz();
				int attrId = DLMS_CLASS_ATTR.CLOCK_ATTR02.getAttr();
				String accessRight = "RW";
				String dataType = "octet-string";

    			//paramSet
    			paramMap.put("paramSet", obisCode+"|"+classId+"|"+attrId+"|"+accessRight+"|"+dataType+"|");
    			
    			paramMap.put("meterId", mdsId);
				paramMap.put("option", "synctime");

       			asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), cmd, paramMap);	

    			if(asyncResult != null){
    				mav.addObject("rtnStr", "Success");
    				status = ResultStatus.SUCCESS;
    			}else{
    				log.debug("SMS Fail");
    				mav.addObject("rtnStr", "Check the results in Async History tab");
    				mav.addObject("status", status.name());
    				mav.addObject("detail", "");
    				return mav;
    			}
    			// SUCCESS
    			for (String key : asyncResult.keySet()) {
                	mav.addObject(key, asyncResult.get(key).toString());
    			}
    			mav.addObject("status", status.name());
    			return mav;
    		}
    		else {
    			mav.addObject("rtnStr", "Invalid Type!");
    			mav.addObject("status", status.name());
    			mav.addObject("detail", "");
    			return mav;
    		}
    	}catch(Exception e){
    		log.debug(e,e);
    		mav.addObject("rtnStr", "Check the results in Async History tab");
    		mav.addObject("status", status.name());
    		return mav;
    	}
    }	
    // INSERT END SP-279
    
	/**
	 * 미터 시간 동기화 명령 - 집중기에 호출
	 * 
	 * @param target
	 *            - 미터아이디
	 * @param mcuId
	 *            - 집중기 아이디
	 * @param loginId
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdMeterTimeSync")
	public ModelAndView cmdMeterTimeSync(HttpServletResponse response, HttpServletRequest request,
			@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "mcuId", required = false) String mcuId,
			@RequestParam(value = "loginId", required = true) String loginId) {

		AimirUser user = CommonController.getAimirUser(response, request);
		Supplier userSupplier = user.getSupplier();
		String lang = userSupplier.getLang().getCode_2letter();
		String country = userSupplier.getCountry().getCode_2letter();

		ResultStatus status = ResultStatus.FAIL;
		Operator operator = null;
		ModelAndView mav = new ModelAndView("jsonView");

		// INSERT START SP-279
		Meter meter = meterManager.getMeter(Integer.parseInt(target));
		Modem modem = modemManager.getModem(meter.getModemId());
		Supplier supplier = meter.getSupplier();
		
		if (modem != null) {
   			if((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) {
   				mav = cmdMeterTimeSyncMBB(meter.getMdsId(), loginId);
   				try {
   	   				if ((mav.getModel().containsKey("status")) &&
   	   						("SUCCESS".equals(mav.getModel().get("status").toString()))) {
   	   					status = ResultStatus.SUCCESS;
   	   				}
   					Code operationCode = codeManager.getCodeByCode("8.1.5"); //Meter Time sync
   					Code meterTypeCode = codeManager.getCode(meter.getMeterTypeCodeId());
   					if (operationCode != null) {
   						operationLogManager.saveOperationLog(supplier, meterTypeCode, meter.getMdsId(), loginId, operationCode,
   								status.getCode(), status.name());
   					}
   				} catch (Exception e) {
   					log.error(e, e);
   				}   				
   				
   				return mav;
   			}
		}
   		// INSERT END SP-279
		
		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.5")) {//Meter Time sync
			mav.addObject("rtnStr", "No permission");
			return mav;
		}

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}
		String rtnStr = "";
		// DELETE START SP-279 (Move to upper)		
//		Meter meter = meterManager.getMeter(Integer.parseInt(target));
//		Modem modem = modemManager.getModem(meter.getModemId());
//		Supplier supplier = meter.getSupplier();
		// DELETE START SP-279		
		if (supplier == null) {
			operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		boolean isSMSModem = isSMSModem(modem);

		String afterTime = "";

		try {
			Map<String, Object> resultMap = cmdOperationUtil.syncTime(mcuId, meter.getMdsId());

			status = ResultStatus.SUCCESS;
			Object[] values = resultMap.values().toArray(new Object[0]);
			for (Object o : values) {
//				rtnStr += (String) o + " \n";	// DELETE SP-279

				if (((String) o).contains("failReason")) {
					status = ResultStatus.FAIL;
					rtnStr = (String) o; // INSERT SP-279
					break;
				}
				// INSERT START SP-279
				if (((String) o).contains("RESULT_VALUE")) {
					rtnStr = "Success";
				}
				// INSERT END SP-279

				if (isSMSModem) {
					if (((String) o).contains("SUCCESS")) {
						// 상태가 바뀌는 시간을 기다려주기 위해 60초 sleep
						Thread.sleep(60000);
						Integer lastStatus = asyncCommandLogManager.getCmdStatus(modem.getDeviceSerial(),
								"cmdSetMeterTime");
						if (TR_STATE.Success.getCode() != lastStatus) {
							status = ResultStatus.FAIL;
							rtnStr = "FAIL : Communication Error(MeterTimeSync)\n";
						} else {
							status = ResultStatus.SUCCESS;

							/**
							 * Transaction ID가 FEP에서 생성되는 방식이어서 정확한 Transaction
							 * ID를 알수가 없다. 그래서 마지막 Transaction번호의 Parameter를
							 * 가져오는 방식으로 구현되었으며 이는 다른 값을 가져올 확률이 있다.
							 */
							List<AsyncCommandParam> acplist = asyncCommandLogManager
									.getCmdParamsByTrnxId(modem.getDeviceSerial(), null);
							if (acplist == null || acplist.size() <= 0) {
								rtnStr = "RESULT_METER_TIME=Empty~!!";
							} else {
								rtnStr += "Result = ";
								for (AsyncCommandParam param : acplist) {
									rtnStr += param.getParamType().equals("RESULT_METER_TIME") ? param.getParamValue()
											: "" + "\n";
								}
							}
							log.debug("cmdSetMeterTime returnValue =>> " + rtnStr);
						}
						break;
					} else {
						status = ResultStatus.FAIL;
					}
				}
			}

			if (meter.getModel() != null && meter.getModel().getName().indexOf("LS") >= 0) {
				String retForm = "[" + "{\"name\":\"" + "afterTime" + "\",\"value\":\"";
				// [{"name":"afterTime","value":"20150604113221"},{"name":"Result","value":"SUCCESS"},{"name":"diff","value":"0"}]
				try {
					if (rtnStr.indexOf(retForm) >= 0) {
						int idx = retForm.indexOf(retForm);
						String time = rtnStr.substring(idx + retForm.length(), idx + retForm.length() + 14);
						String fTime = TimeUtil.getFormatTime(time, lang, country);
						rtnStr = "Meter time is synchronized – New date and time: " + fTime + ".";
					}
				} catch (Exception e) {
					log.error(e, e);
				}
			}

		} catch (Exception e) {
			rtnStr = e.getMessage();
			log.error(e, e);
		}

		try {
			Code operationCode = codeManager.getCodeByCode("8.1.5"); // Meter Time Sync
			Code meterTypeCode = codeManager.getCode(meter.getMeterTypeCodeId());
			if (operationCode != null) {
				operationLogManager.saveOperationLog(supplier, meterTypeCode, meter.getMdsId(), loginId, operationCode,
						status.getCode(), status.name());
			}
		} catch (Exception e) {
			log.error(e, e);
		}
		mav.addObject("status", status.name());
		mav.addObject("meterId", meter.getMdsId());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	/**
	 * 미터 종류에 상관없이 CircuitBreaker(Releay Switch) 상태 가져오기 미터 모델에 따라 다른 커맨드로 분기
	 * 
	 * @param target
	 * @param mcuId
	 * @param loginId
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdRemoteGetStatus")
	public ModelAndView cmdRemoteGetStatus(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "mcuId", required = false) String mcuId,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.2")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}
		String rtnStr = "";
		// MCU mcu = mcuManager.getMCU(Integer.parseInt(mcuId));
		Meter meter = meterManager.getMeter(Integer.parseInt(target));
		//Modem modem = modemManager.getModem(meter.getModemId());
		Modem modem = meter.getModem();
		Supplier supplier = meter.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		boolean isSMSModem = isSMSModem(modem);

		try {
			status = ResultStatus.SUCCESS;
			
			Map<String, Object> resultMap = null;
			// MBB(SMS)
			// -> UPDATE START 2016/09/14 SP-117
			//if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
			if( ((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
			// <- UPDATE END   2016/09/14 SP-117
					// Map<String, String> asyncResult = new HashMap<String, String>();
				rtnStr = "Check the results in Async History tab"; // INSERT 2016/09/21 SP-117
				mav.addObject("jsonString", rtnStr); // INSERT 2016/09/21 SP-117
				try{ 
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("mcuId", mcuId);
					paramMap.put("meterId", meter.getMdsId());

					// -> UPDATE START 2016/09/12 SP-117
					// resultMap = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "relayValveStatus", paramMap);
					// Map<String, Object> resultMap2 = null; // DELETE 2016/09/20 SP-117
					String param    = null;
					String obisCode = this.convertObis(OBIS.RELAY_STATUS.getCode());
					int    classId  = DLMS_CLASS.RELAY_CLASS.getClazz();
					int    attrId   = DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr();
					param           = obisCode+"|"+classId+"|"+attrId+"|RO|Boolean|";
					
					paramMap.put("paramGet", param);
					paramMap.put("option"  , "relaystatusall");
					// -> UPDATE START 2016/09/20 SP-117
					// resultMap2 = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "cmdMeterParamGet", paramMap);

					// if( (Boolean)resultMap2.get("RelayStatus") == true ) {
	                //    resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Connected );
	                //    updateMeterStatusNormal(meter);
	                // }
	                // else {
	                //    resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Disconnected );
	                //    updateMeterStatusCutOff(meter);
	                // }
	           	
	            	// Set [LoadControlStatus]
	                // if( (Integer)resultMap2.get("LoadControlStatus") == CONTROL_STATE.Disconnected.ordinal() ) {
	                //    // updateMeterStatusCutOff(meter);
					//	resultMap.put( "LoadControlStatus", CONTROL_STATE.Disconnected );
	                // }
	                // else if( (Integer)resultMap2.get("LoadControlStatus") == CONTROL_STATE.Connected.ordinal() ) {
	                //    // updateMeterStatusNormal(meter);
	                //    resultMap.put( "LoadControlStatus", CONTROL_STATE.Connected );
	                // }
	                // else if( (Integer)resultMap2.get("LoadControlStatus") == CONTROL_STATE.ReadyForReconnection.ordinal() ) {
	                //	resultMap.put( "LoadControlStatus", CONTROL_STATE.ReadyForReconnection );
	                // }
	                
	                // // Set [LoadControlMode]
	                // resultMap.put( "LoadControlMode", resultMap2.get("LoadControlMode") );
					resultMap = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "cmdMeterParamGet", paramMap);
	                // -> UPDATE START 2016/09/20 SP-117
					// <- UPDATE START 2016/09/12 SP-117
						 
					if(resultMap != null){
						status = ResultStatus.SUCCESS;
					}else{
						log.debug("SMS Fail");
						// mav.addObject("rtnStr", "Check the results in Async History tab"); // DELETE 2016/09/21 SP-117
						status = ResultStatus.FAIL;
					}
				}catch(Exception e){
					log.debug("SMS Fail");
					mav.addObject("rtnStr", "Check the results in Async History tab");
					status = ResultStatus.FAIL;
				}

		    // RF or Ethernet	
			}else{ 

				resultMap = cmdOperationUtil.relayValveStatus(mcuId, meter.getMdsId());

			// } // DELETE 2016/09/21 SP-117
			
			String loadControlStatusString = "";
			String loadControlModeString = "";
			String relayStatusString = "";
			String failReasonString = "";
			try {
				String responseJson = (String)resultMap.get("Response");
				log.debug("Reponse:" + responseJson);
				JsonParser jsonParser = new JsonParser();
			    JsonElement element =  jsonParser.parse(responseJson);
			    if ( element.isJsonArray()){
			    	for (JsonElement e : element.getAsJsonArray()) {
			    		JsonObject jobj = e.getAsJsonObject();
			    		if ( jobj.get("name") != null  ){
				    		String name = jobj.get("name").getAsString();
				    		if ( "failReason".equals(name)){
				    			failReasonString = jobj.get("value").getAsString();
				    		}
				    		else if ( "Relay Status".equals(name)){
								relayStatusString = jobj.get("value").getAsString();
							}
				    		else if ( "LoadControlStatus".equals(name)){
								loadControlStatusString = jobj.get("value").getAsString();
							}
				    		else if ( "LoadControlMode".equals(name)){
				    			loadControlModeString = jobj.get("value").getAsString();
							}
				    		
			    		}
			    	}
			    }
			    else {
			    	log.debug(element.getClass());
			    }
			}catch (Exception e){
				log.debug(e,e);
			}
			Object[] values = resultMap.values().toArray(new Object[0]);

			for (Object o : values) {
				log.debug((String)o);
				//rtnStr += (String) o + " \n";
				rtnStr = loadControlStatusString;

				if (((String) o).contains("failReason")) {
					status = ResultStatus.FAIL;
					rtnStr = "FAIL : " + failReasonString;
					break;
				}

				if (isSMSModem) {
					if (((String) o).contains("SUCCESS")) {
						// 상태가 바뀌는 시간을 기다려주기 위해 60초 sleep
						Thread.sleep(60000);
						Integer lastStatus = asyncCommandLogManager.getCmdStatus(modem.getDeviceSerial(),
								"cmdRelayStatus");
						if (TR_STATE.Success.getCode() != lastStatus) {
							status = ResultStatus.FAIL;
							rtnStr = "FAIL : Communication Error(RelayStatus)\n";
						} else {
							status = ResultStatus.SUCCESS;

							/**
							 * Transaction ID가 FEP에서 생성되는 방식이어서 정확한 Transaction
							 * ID를 알수가 없다. 그래서 마지막 Transaction번호의 Parameter를
							 * 가져오는 방식으로 구현되었으며 이는 다른 값을 가져올 확률이 있다.
							 */
							List<AsyncCommandParam> acplist = asyncCommandLogManager
									.getCmdParamsByTrnxId(modem.getDeviceSerial(), null);
							if (acplist == null || acplist.size() <= 0) {
								rtnStr = "RESULT_STATUS=Empty~!!";
							} else {
								rtnStr += "Result = ";
								for (AsyncCommandParam param : acplist) {
									rtnStr += param.getParamType().equals("RESULT_STATUS") ? param.getParamValue()
											: "" + "\n";
								}
							}
							log.debug("cmdRelayStatus returnValue =>> " + rtnStr);
						}
						break;
					} else {
						status = ResultStatus.FAIL;
					}
				}
			}

			if (meter.getModel() != null && meter.getModel().getName().indexOf("LS") >= 0) {
				String open = "[" + "{\"name\":\"" + "LoadControlStatus" + "\",\"value\":\"" + "OPEN" + "\"}" + "]";
				String close = "[" + "{\"name\":\"" + "LoadControlStatus" + "\",\"value\":\"" + "CLOSE" + "\"}" + "]";
				if (rtnStr.indexOf(open) >= 0) {
					rtnStr = "Internal relay is OPEN.";
				} else if (rtnStr.indexOf(close) >= 0) {
					rtnStr = "Internal relay is CLOSED.";
				}
			}else if(status != ResultStatus.FAIL && relayStatusString.length() > 0 && loadControlModeString.length() > 0 && loadControlStatusString.length() > 0) {
				rtnStr = "Relay Status = " + relayStatusString + ", Load Control Status = " + loadControlStatusString + ", Load Control Mode = " + loadControlModeString;
			}else { // SP-792
				status = ResultStatus.FAIL;
				rtnStr = resultMap.toString();
			}

			} // INSERT 2016/09/21 SP-117
			
		} catch (Exception e) {
			log.error(e, e);
			rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
		}

		/**
		 * 2014.07.03 simhanger Relay Status : Relay Switch로 남겨지던 로그를 Relay
		 * Status로 남겨지도록 수정함
		 * 
		 * Code operationCode = codeManager.getCodeByCode("8.1.4");
		 */
		Code operationCode = codeManager.getCodeByCode("8.1.2");

		Code meterTypeCode = codeManager.getCode(meter.getMeterTypeCodeId());
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, meterTypeCode, meter.getMdsId(), loginId, operationCode,
					status.getCode(), status.name());
		}
		// meterManager.updateMeter(meter);
		mav.addObject("status", status.name());
		mav.addObject("meterId", meter.getMdsId());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	private Boolean isSMSModem(Modem modem) {
		boolean isSMSModem = false;

		if (modem != null && ModemType.MMIU.equals(modem.getModemType()) && Protocol.SMS.equals(modem.getProtocolType())
				&& "0102".equals(modem.getProtocolVersion())) {
			isSMSModem = true;
		}

		return isSMSModem;
	}

	/**
	 * 미터 종류에 상관없이 CircuitBreaker(Releay Switch) 공급차단 미터 모델에 따라 다른 커맨드로 분기
	 * 
	 * @param target
	 * @param mcuId
	 * @param loginId
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdRemotePowerOff")
	public ModelAndView cmdRemotePowerOff(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "mcuId", required = false) String mcuId,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");
		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.4")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}
		String rtnStr = "";
		Meter meter = meterManager.getMeter(Integer.parseInt(target));
		//Modem modem = modemManager.getModem(meter.getModemId());
		Modem modem = meter.getModem();
		Supplier supplier = meter.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		boolean isSMSModem = isSMSModem(modem);

		try {
			status = ResultStatus.SUCCESS;
			Map<String, Object> resultMap = null;
			// MBB(SMS)
			// -> UPDATE START 2016/09/14 SP-117
			//if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
			if( ((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
			// <- UPDATE END   2016/09/14 SP-117
					// Map<String, String> asyncResult = new HashMap<String, String>();
				rtnStr = "Check the results in Async History tab"; // INSERT 2016/09/21 SP-117
				mav.addObject("jsonString", rtnStr); // INSERT 2016/09/21 SP-117
				try{ 
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("mcuId", mcuId);
					paramMap.put("meterId", meter.getMdsId());

					// -> UPDATE START 2016/09/12 SP-117
					// resultMap = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "relayValveOff", paramMap);
					String param    = null;
					String obisCode = this.convertObis(OBIS.RELAY_STATUS.getCode());
					int    classId  = DLMS_CLASS.RELAY_CLASS.getClazz();
					int    attrId   = DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr();
					param           = obisCode+"|"+classId+"|"+attrId+"|ACTION|Boolean|false";
					
					paramMap.put("paramAct", param);
					resultMap = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "cmdMeterParamAct", paramMap);
					// <- UPDATE START 2016/09/12 SP-117

					if(resultMap != null){
						// -> DELETE START 2016/09/20 SP-117
						// // -> INSERT START 2016/09/12 SP-117
						// if( (Boolean)resultMap.get("value") == true ) {
		                //    resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Connected );
		                // }
		                // else {
		                //    resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Disconnected );
		                //    updateMeterStatusCutOff( meter );
		                // }
						// // <- INSERT END   2016/09/12 SP-117
						// <- DELETE END   2016/09/20 SP-117
						status = ResultStatus.SUCCESS;
					}else{
						log.debug("SMS Fail");
						// mav.addObject("rtnStr", "Check the results in Async History tab"); // DELETE 2016/09/21 SP-117
						status = ResultStatus.FAIL;
					}
				}catch(Exception e){
					log.debug("SMS Fail");
					mav.addObject("rtnStr", "Check the results in Async History tab");
					status = ResultStatus.FAIL;
				}

		    // RF or Ethernet	
			}else{ 
				resultMap = cmdOperationUtil.relayValveOff(mcuId, meter.getMdsId());
			// } // DELETE 2016/09/21 SP-117

			String loadControlStatusString = "";
			String relayStatusString = "";
			String failReasonString = "";
			String cmdStatusString = "";
			try {
				String responseJson = (String)resultMap.get("Response");
				log.debug("Reponse:" + responseJson);
				JsonParser jsonParser = new JsonParser();
			    JsonElement element =  jsonParser.parse(responseJson);
			    if ( element.isJsonArray()){
			    	for (JsonElement e : element.getAsJsonArray()) {
			    		JsonObject jobj = e.getAsJsonObject();
			    		if ( jobj.get("name") != null  ){
				    		String name = jobj.get("name").getAsString();
				    		if ( "failReason".equals(name)){
				    			failReasonString = jobj.get("value").getAsString();
				    		}
				    		else if ( "Relay Status".equals(name)){
								relayStatusString = jobj.get("value").getAsString();
							}
				    		else if ( "LoadControlStatus".equals(name)){
								loadControlStatusString = jobj.get("value").getAsString();
							}
				    		else if ( "RESULT_VALUE".equals(name)){
				    			cmdStatusString = jobj.get("value").getAsString();
				    		}
			    		}
			    	}
			    }
			    else {
			    	log.debug(element.getClass());
			    }
			}catch (Exception e){
				log.debug(e,e);
			}
			Object[] values = resultMap.values().toArray(new Object[0]);
			for (Object o : values) {
				log.debug((String)o);
				//rtnStr += (String) o + " \n";
				rtnStr = loadControlStatusString;

				if (((String) o).contains("failReason")) {
					status = ResultStatus.FAIL;
					rtnStr = "FAIL : " + failReasonString;
					break;
				}

				if (isSMSModem) {
					if (((String) o).contains("SUCCESS")) {
						// 상태가 바뀌는 시간을 기다려주기 위해 60초 sleep
						Thread.sleep(60000);
						Integer lastStatus = asyncCommandLogManager.getCmdStatus(modem.getDeviceSerial(),
								"cmdRelayDisconnect");
						if (TR_STATE.Success.getCode() != lastStatus) {
							status = ResultStatus.FAIL;
							rtnStr = "FAIL : Communication Error(RelayOff)\n";
						} else {
							status = ResultStatus.SUCCESS;

							/**
							 * Transaction ID가 FEP에서 생성되는 방식이어서 정확한 Transaction
							 * ID를 알수가 없다. 그래서 마지막 Transaction번호의 Parameter를
							 * 가져오는 방식으로 구현되었으며 이는 다른 값을 가져올 확률이 있다.
							 */
							List<AsyncCommandParam> acplist = asyncCommandLogManager
									.getCmdParamsByTrnxId(modem.getDeviceSerial(), null);
							if (acplist == null || acplist.size() <= 0) {
								rtnStr = "RESULT_DISCONNECT=Empty~!!";
							} else {
								rtnStr += "Result = ";
								for (AsyncCommandParam param : acplist) {
									rtnStr += param.getParamType().equals("RESULT_DISCONNECT") ? param.getParamValue()
											: "" + "\n";
								}
							}
							log.debug("cmdRelayDisconnect returnValue =>> " + rtnStr);
						}
						break;
					} else {
						status = ResultStatus.FAIL;
					}
				}
			}

			if (meter.getModel() != null && meter.getModel().getName().indexOf("LS") >= 0) {
				String open = "{\"name\":\"" + "LoadControlStatus" + "\",\"value\":\"" + "OPEN" + "\"}";
				String close = "{\"name\":\"" + "LoadControlStatus" + "\",\"value\":\"" + "CLOSE" + "\"}";
				if (rtnStr.indexOf(open) >= 0) {
					rtnStr = "Energy supply to the customer is INTERRUPTED!";
				} else if (rtnStr.indexOf(close) >= 0) {
					rtnStr = "Internal relay is CLOSED.";
				}
			}else if(status != ResultStatus.FAIL) {
				rtnStr = "Command Status = " + cmdStatusString + ", Relay Status = " + relayStatusString ;
			}

			} // INSERT 2016/09/21 SP-117
			
		} catch (Exception e) {
			rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
			log.error(e, e);
		}

		/**
		 * 2014.07.03 simhanger Relay Off : Relay Switch로 남겨지던 로그를 Relay Off로
		 * 남겨지도록 수정함
		 * 
		 * Code operationCode = codeManager.getCodeByCode("8.1.4");
		 */
		Code operationCode = codeManager.getCodeByCode("8.1.4");
		Code meterTypeCode = codeManager.getCode(meter.getMeterTypeCodeId());
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, meterTypeCode, meter.getMdsId(), loginId, operationCode,
					status.getCode(), status.name());
		}

		try {
			Code meterType = codeDao.getCodeByName("EnergyMeter");
			if (meter.getMeterType() == meterType && status == ResultStatus.SUCCESS) {
				circuitBreakerManager.saveSupplyCapacity(CircuitBreakerStatus.Deactivation, GroupType.Meter.name(),
						CircuitBreakerCondition.Emergency, meter.getId());
			}
		} catch (Exception e) {
			log.warn(e, e);
		}

		// meterManager.updateMeter(meter);
		mav.addObject("status", status.name());
		mav.addObject("meterId", meter.getMdsId());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}
	/**
	 * @MethodName getMcuIdFromMdsId
	 * @Date 2013. 9. 11.
	 * @param mdsId
	 * @return
	 * @Modified
	 * @Description MdsId로부터 MCU의 sys_id 정보를 가지고 온다
	 */
	@RequestMapping("/gadget/device/command/getMcuIdFromMdsId")
	public ModelAndView getMcuIdFromMdsId(@RequestParam String mdsId) {
		ModelAndView mav = new ModelAndView("jsonView");
		String mcuId = meterManager.getMcuIdFromMdsId(mdsId);

		if (mcuId == null || mcuId.equals("")) {
			mav.addObject("result", "failed: meter is not connected with MCU");
		} else {
			mav.addObject("result", "success");
			mav.addObject("mcuId", mcuId);
		}

		return mav;
	}

	/**
	 * 미터 종류에 상관없이 CircuitBreaker(Releay Switch) 공급차단재개 미터 모델에 따라 다른 커맨드로 분기
	 * 
	 * @param target
	 * @param mcuId
	 * @param loginId
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdRemotePowerOn")
	public ModelAndView cmdRemotePowerOn(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "mcuId", required = false) String mcuId,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");
		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.3")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}
		Map<?, ?> result = null;
		String rtnStr = "";
		Integer relayStatus = null;
		Meter meter = meterManager.getMeter(Integer.parseInt(target));
		Modem modem = modemManager.getModem(meter.getModemId());
//		Modem modem = meter.getModem();
		Contract contract = contractManager.getContractByMeterId(meter.getId());
		Supplier supplier = meter.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		boolean isSMSModem = isSMSModem(modem);

		try {
			status = ResultStatus.SUCCESS;
			Map<String, Object> resultMap = null;
			// MBB(SMS)
			// -> UPDATE START 2016/09/14 SP-117
			//if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
			if( ((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
			// <- UPDATE END   2016/09/14 SP-117
					// Map<String, String> asyncResult = new HashMap<String, String>();
				rtnStr = "Check the results in Async History tab"; // INSERT 2016/09/21 SP-117
				mav.addObject("jsonString", rtnStr); // INSERT 2016/09/21 SP-117
				try{ 
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("mcuId", mcuId);
					paramMap.put("meterId", meter.getMdsId());

					// -> UPDATE START 2016/09/12 SP-117
					// resultMap = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "relayValveOn", paramMap);
					String param    = null;
					String obisCode = this.convertObis(OBIS.RELAY_STATUS.getCode());
					int    classId  = DLMS_CLASS.RELAY_CLASS.getClazz();
					int    attrId   = DLMS_CLASS_ATTR.REGISTER_ATTR02.getAttr();
					param           = obisCode+"|"+classId+"|"+attrId+"|ACTION|Boolean|true";
					
					paramMap.put("paramAct", param);
					resultMap = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "cmdMeterParamAct", paramMap);
					// <- UPDATE START 2016/09/12 SP-117
						 
					if(resultMap != null){
						// <- INSERT START 2016/09/20 SP-117
						// // -> INSERT START 2016/09/12 SP-117
						// if( (Boolean)resultMap.get("value") == true ) {
		                //     resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Connected );
	                    //     updateMeterStatusNormal( meter );
		                // }
		                // else {
		                //     resultMap.put( "Relay Status", RELAY_STATUS_KAIFA.Disconnected );
		                // }
						// // <- INSERT END   2016/09/12 SP-117
						// -> INSERT END   2016/09/20 SP-117
						status = ResultStatus.SUCCESS;
					}else{
						log.debug("SMS Fail");
						// mav.addObject("rtnStr", "Check the results in Async History tab"); // DELETE 2016/09/21 SP-117
						status = ResultStatus.FAIL;
					}
				}catch(Exception e){
					log.debug("SMS Fail");
					mav.addObject("rtnStr", "Check the results in Async History tab");
					status = ResultStatus.FAIL;
				}
			// RF or Ethernet	
			}else{
				String meterTypeName = meter.getMeterType().getName();
				//EnergyMeter
				if(MeterType.getByServiceType("3.1").name().equals(meterTypeName)){
					cmdOperationUtil.cmdSetEnergyLevel(mcuId, Integer.toString(modem.getId()), 1);		//Relay on : 1
					Thread.sleep(60000);	// Relay control 1분 대기 
					relayStatus = cmdOperationUtil.cmdGetEnergyLevel(mcuId, Integer.toString(modem.getId()));	//Relay 상태값 조회
					
					if (relayStatus != null && relayStatus != 0) {		//리턴값이 0라면 Energy Level값 얻는 것을 실패
						status = ResultStatus.SUCCESS;
						rtnStr = "SUCCESS : " + relayStatus;
					}else{
						log.debug("EnergyMeter Relay On Fail");
						status = ResultStatus.FAIL;
						rtnStr = "FAIL : " + relayStatus;
					}
				//WaterMeter
				}else if(MeterType.getByServiceType("3.2").name().equals(meterTypeName)){					
					String modemId= Integer.toString(modem.getId());
	//				resultMap = cmdOperationUtil.relayValveOn(mcuId, meter.getMdsId());
					resultMap = cmdOperationUtil.cmdKDValveControl(mcuId, Integer.toString(modem.getId()), 0);
	
					String loadControlStatusString = "";
					String relayStatusString = "";
					String failReasonString = "";
					String cmdStatusString = "";
					try {
						String responseJson = (String)resultMap.get("Response");
						log.debug("Reponse:" + responseJson);
						JsonParser jsonParser = new JsonParser();
					    JsonElement element =  jsonParser.parse(responseJson);
					    if ( element.isJsonArray()){
					    	for (JsonElement e : element.getAsJsonArray()) {
					    		JsonObject jobj = e.getAsJsonObject();
					    		if ( jobj.get("name") != null  ){
						    		String name = jobj.get("name").getAsString();
						    		if ( "failReason".equals(name)){
						    			failReasonString = jobj.get("value").getAsString();
						    		}
						    		else if ( "Relay Status".equals(name)){
										relayStatusString = jobj.get("value").getAsString();
									}
						    		else if ( "meterStatus".equals(name)){
										relayStatusString = jobj.get("value").getAsString();
									}
						    		else if ( "LoadControlStatus".equals(name)){
										loadControlStatusString = jobj.get("value").getAsString();
									}
						    		else if ( "RESULT_VALUE".equals(name)){
										cmdStatusString  = jobj.get("value").getAsString();
									}
					    		}
					    	}
					    }
					    else {
					    	log.debug(element.getClass());
					    }
					}catch (Exception e){
						log.debug(e,e);
					}
					Object[] values = resultMap.values().toArray(new Object[0]);
	
					for (Object o : values) {
						log.debug((String)o);
						//rtnStr += (String) o + " \n";
						rtnStr = loadControlStatusString;
	
						if (((String) o).contains("failReason")) {
							status = ResultStatus.FAIL;
							rtnStr = "FAIL : " + failReasonString;
							break;
						}
	
						if (isSMSModem) {
							if (((String) o).contains("SUCCESS")) {
								// 상태가 바뀌는 시간을 기다려주기 위해 60초 sleep
								Thread.sleep(60000);
								Integer lastStatus = asyncCommandLogManager.getCmdStatus(modem.getDeviceSerial(),
										"cmdRelayReconnect");
								if (TR_STATE.Success.getCode() != lastStatus) {
									status = ResultStatus.FAIL;
									rtnStr = "FAIL : Communication Error(RelayOn)\n";
								} else {
									status = ResultStatus.SUCCESS;
	
									/**
									 * Transaction ID가 FEP에서 생성되는 방식이어서 정확한 Transaction
									 * ID를 알수가 없다. 그래서 마지막 Transaction번호의 Parameter를
									 * 가져오는 방식으로 구현되었으며 이는 다른 값을 가져올 확률이 있다.
									 */
									List<AsyncCommandParam> acplist = asyncCommandLogManager
											.getCmdParamsByTrnxId(modem.getDeviceSerial(), null);
									if (acplist == null || acplist.size() <= 0) {
										rtnStr = "RESULT_RECONNECT=Empty~!!";
									} else {
										rtnStr += "Result = ";
										for (AsyncCommandParam param : acplist) {
											rtnStr += param.getParamType().equals("RESULT_RECONNECT") ? param.getParamValue()
													: "" + "\n";
										}
									}
									log.debug("cmdRelayReconnect returnValue =>> " + rtnStr);
								}
								break;
							} else {
								status = ResultStatus.FAIL;
							}
						}
					}
	
					if (meter.getModel() != null && meter.getModel().getName().indexOf("LS") >= 0) {
						String open = "{\"name\":\"" + "LoadControlStatus" + "\",\"value\":\"" + "OPEN" + "\"}";
						String close = "{\"name\":\"" + "LoadControlStatus" + "\",\"value\":\"" + "CLOSE" + "\"}";
						if (rtnStr.indexOf(open) >= 0) {
							rtnStr = "Internal relay is OPEN.";
						} else if (rtnStr.indexOf(close) >= 0) {
							rtnStr = "Energy supply to the customer is RESUMED!";
						}
					}else if(status != ResultStatus.FAIL) {
						rtnStr = "Command Status = " + cmdStatusString + ", Relay Status = " + relayStatusString ;
					}
				}			
		} // INSERT 2016/09/21 SP-117

		} catch (Exception e) {
			log.error(e, e);
			rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
		}

		/**
		 * 2014.07.03 simhanger Relay On : Relay Switch로 남겨지던 로그를 Relay On으로
		 * 남겨지도록 수정함
		 * 
		 * Code operationCode = codeManager.getCodeByCode("8.1.4");
		 */
		Code operationCode = codeManager.getCodeByCode("8.1.3");
		Code meterTypeCode = codeManager.getCode(meter.getMeterTypeCodeId());
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, meterTypeCode, meter.getMdsId(), loginId, operationCode,
					status.getCode(), status.name());
		}
		meterManager.updateMeter(meter);
		mav.addObject("status", status.name());
		mav.addObject("meterId", meter.getMdsId());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	/**
	 * 미터 종류에 상관없이 CircuitBreaker(Releay Switch) 공급차단재개 미터 모델에 따라 다른 커맨드로 분기
	 * 
	 * @param target
	 * @param mcuId
	 * @param loginId
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdRemotePowerActivate")
	public ModelAndView cmdRemotePowerActivate(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "mcuId", required = false) String mcuId,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");
		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.4")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}
		Map<?, ?> result = null;
		String rtnStr = "";
		Meter meter = meterManager.getMeter(Integer.parseInt(target));
		Supplier supplier = meter.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			status = ResultStatus.SUCCESS;

			Map<String, Object> resultMap = cmdOperationUtil.relayValveActivate(mcuId, meter.getMdsId());

			Object[] values = resultMap.values().toArray(new Object[0]);

			for (Object o : values) {
				rtnStr += (String) o + " ";

				if (((String) o).contains("failReason")) {
					status = ResultStatus.FAIL;
					break;
				}
			}
		} catch (Exception e) {
			rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
		}

		Code operationCode = codeManager.getCodeByCode("8.1.4");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, meter.getMeterType(), meter.getMdsId(), loginId,
					operationCode, status.getCode(), status.name());
		}
		mav.addObject("status", status.name());
		mav.addObject("meterId", meter.getMdsId());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	/**
	 * 집중기 리셋(초기화)
	 * 
	 * @param target
	 *            - 집중기 아이디(mcu sysId)
	 * @param loginId
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdMcuReset")
	public ModelAndView cmdMcuReset(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		MCU mcu = null;
		String errorReason = "";
		String rtnStr = "";
		try {
			if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.3.3")) {
				rtnStr = "No permission";
				mav.addObject("rtnStr", rtnStr);
				return mav;
			}
			if (target == null || "".equals(target)) {
				status = ResultStatus.INVALID_PARAMETER;
				rtnStr = "Target ID null!";
				mav.addObject("rtnStr", rtnStr);
				return mav;
			}
			mcu = mcuManager.getMCU(Integer.parseInt(target));
			cmdOperationUtil.mcuReset(mcu.getSysID());
			rtnStr = "DONE";
			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
			errorReason = e.toString();
			log.debug("errorReason: " + errorReason);
			rtnStr = "FAIL";
			status = ResultStatus.FAIL;
		}

		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		Code operationCode = codeManager.getCodeByCode("8.3.3"); // DCU Reset
		if (operationCode != null) {
			if(rtnStr.equals("FAIL")) rtnStr += " ] " + errorReason;
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), rtnStr);
		}
		
		mav.addObject("status", status.name());
		mav.addObject("errorReason", errorReason);
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	/**
	 * 집중기 시간 설정 - 서버시간 기준
	 * 
	 * @param target
	 * @param loginId
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdMcuSetTime")
	public ModelAndView cmdMcuSetTime(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		MCU mcu = null;
		String errorReason = "";
		try {
			if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.3.8")) {
				mav.addObject("rtnStr", "No permission");
				return mav;
			}
			if (target == null || "".equals(target)) {
				status = ResultStatus.INVALID_PARAMETER;
				mav.addObject("rtnStr", "Target ID null!");
				return mav;
			}
			mcu = mcuManager.getMCU(Integer.parseInt(target));
			String time = TimeUtil.getCurrentTime();
			cmdOperationUtil.cmdMcuSetTime(mcu.getSysID(), time);
			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
			errorReason = e.toString();
			log.debug("errorReason: " + errorReason);
			status = ResultStatus.FAIL;
		}

		Code operationCode = codeManager.getCodeByCode("8.3.8");
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : errorReason);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", errorReason);
		return mav;
	}

	/**
	 * MCU Diagnosis
	 * 
	 * @param target
	 * @param loginId
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdMcuDiagnosis")
	public ModelAndView cmdMcuDiagnosis(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		MCU mcu = null;
		String errorReason = "";
		try {

			if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.3.8")) {
				mav.addObject("rtnStr", "No permission");
				return mav;
			}
			if (target == null || "".equals(target)) {
				status = ResultStatus.INVALID_PARAMETER;
				mav.addObject("rtnStr", "Target ID null!");
				return mav;
			}

			mcu = mcuManager.getMCU(Integer.parseInt(target));
			Hashtable resultTable = cmdOperationUtil.getMCUDiagnosis(mcu.getSysID());
			if (resultTable != null && resultTable.size() > 0) {
				Iterator<String> keys = resultTable.keySet().iterator();
				String keyVal = null;
				while (keys.hasNext()) {
					keyVal = (String) keys.next();
					mav.addObject(keyVal, resultTable.get(keyVal));
				}
				status = ResultStatus.SUCCESS;
			} else {
				status = ResultStatus.FAIL;
				log.debug("resultTable Is Null");
			}
		} catch (Exception e) {
			log.error(e, e);
			errorReason = e.toString();
			log.debug("errorReason: " + errorReason);
			status = ResultStatus.FAIL;
		}
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		Code operationCode = codeManager.getCodeByCode("8.3.8");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : errorReason);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", errorReason);
		return mav;
	}

	/**
	 * MCU Status Monitoring
	 * 
	 * @param target
	 * @param loginId
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/getMCUStatus")
	public ModelAndView getMCUStatus(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		MCU mcu = null;
		Supplier supplier = null;
		String errorReason = "";
		StringBuffer jsonString = new StringBuffer("");
		try {

			if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.3.4")) { //DCU Status Monitoring
				mav.addObject("rtnStr", "No permission");
				return mav;
			}
			if (target == null || "".equals(target)) {
				status = ResultStatus.INVALID_PARAMETER;
				mav.addObject("rtnStr", "Target ID null!");
				return mav;
			}

			log.debug("target : " + target);
			mcu = mcuManager.getMCU(Integer.parseInt(target));
			supplier = mcu.getSupplier();
			Map resultTable = cmdOperationUtil.getMCUStatus(mcu.getSysID());
			if (resultTable != null && resultTable.size() > 0) {
				Iterator<String> keys = resultTable.keySet().iterator();
				String keyVal = null;
				while (keys.hasNext()) {
					keyVal = (String) keys.next();
					mav.addObject(keyVal, resultTable.get(keyVal));

					if (keyVal.equals("gpioPowerFail")) {
						String val = (String) resultTable.get(keyVal);
						jsonString.append(
								"Power State" + ": " + (val != null && val == "0" ? "Normal" : "Power Fail") + "\n");
					}
					if (keyVal.equals("gpioLowBattery")) {
						String val = (String) resultTable.get(keyVal);
						jsonString.append(
								"Low Battery" + ": " + (val != null && val == "1" ? "Normal" : "Low Battery") + "\n");
					}
					if (keyVal.equals("flashTotalSize")) {
						String val = (String) resultTable.get(keyVal);
						val = Math.round(Integer.parseInt(val) / 1024 * 10) / 10 + "KB";
						jsonString.append("Flash Total Size(KB)" + ": " + val + "\n");
					}
					if (keyVal.equals("flashUseSize")) {
						String val = (String) resultTable.get(keyVal);
						val = Math.round(Integer.parseInt(val) / 1024 * 10) / 10 + "KB";
						jsonString.append("Flash Use Size(KB)" + ": " + val + "\n");
					}
					if (keyVal.equals("memTotalSize")) {
						String val = (String) resultTable.get(keyVal);
						val = Math.round(Integer.parseInt(val) / 1024 / 1000 * 10) / 10 + "MB";
						jsonString.append("Memory Total Size" + ": " + val + "\n");
					}
					if (keyVal.equals("memUseSize")) {
						String val = (String) resultTable.get(keyVal);
						val = Math.round(Integer.parseInt(val) / 1024 / 1000 * 10) / 10 + "MB";
						jsonString.append("Memory Use Size" + ": " + val + "\n");
					}
					if (keyVal.equals("sysCurTemp")) {
						String val = (String) resultTable.get(keyVal);
						if (val.length() > 2) {
							val = val.substring(0, 2) + "." + val.substring(2);
						}
						jsonString.append("Current Temp." + ": " + val + "\n");
					}
					
					if (keyVal.equals("sinkNeighborNode")) {
						jsonString.append("Sink Neighbor Node" + ": " + resultTable.get(keyVal) + "\n");
					}
					if (keyVal.equals("sinkState")) {
						String state = (String) resultTable.get(keyVal);
						jsonString.append(
								"Sink State" + ": " + state != null && state == "1" ? "Normal" : "Abnormal" + "\n");
					}
					
					//Pakistan DCU 데이터 수집항목  
					if (keyVal.equals("sysID")) {
						jsonString.append("DCU ID" + ": " + resultTable.get(keyVal) + "\n");
					}
					if (keyVal.equals("sysType")) {
						jsonString.append("DCU Type" + ": " + resultTable.get(keyVal) + "\n");
					}
					if (keyVal.equals("sysName")) {
						jsonString.append("DCU Name" + ": " + resultTable.get(keyVal) + "\n");
					}
					if (keyVal.equals("sysModel")) {
						jsonString.append("DCU HW Model Name" + ": " + resultTable.get(keyVal) + "\n");
					}
					if (keyVal.equals("sysHwVersion")) {
						jsonString.append("DCU HW Version" + ": " + resultTable.get(keyVal) + "\n");
					}
					if (keyVal.equals("sysHwBuild")) {
						jsonString.append("DCU HW Build Number" + ": " + resultTable.get(keyVal) + "\n");
					}
					if (keyVal.equals("sysSwVersion")) {
						jsonString.append("DCU SW Version" + ": " + resultTable.get(keyVal) + "\n");
					}
					if (keyVal.equals("sysSwBuild")) {
						jsonString.append("SW Build Number" + ": " + resultTable.get(keyVal) + "\n");
					}
					if (keyVal.equals("sysPort")) {
						jsonString.append("DCU Listen port number" + ": " + resultTable.get(keyVal) + "\n");
					}
					if (keyVal.equals("sysSerialNumber")) {
						jsonString.append("DCU Serial Number (Unique Value)" + ": " + resultTable.get(keyVal) + "\n");
					}
					if (keyVal.equals("sysUptime")) {
						jsonString.append("Sytem Uptimes(secs)" + ": " + resultTable.get(keyVal) + "\n");
					}
					if (keyVal.equals("sysTime")) {
						String val = (String) resultTable.get(keyVal);
						jsonString.append("DCU Current Time" + ": " + TimeLocaleUtil.getLocaleDate(val,
								supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()) + "\n");
					}
					if (keyVal.equals("sysTimeZone")) {
						jsonString.append("DCU Timezone (+720 ~ -720)" + ": " + resultTable.get(keyVal) + "\n");
					}
					if (keyVal.equals("sysTemperature")) {
						String val = (String) resultTable.get(keyVal);
						val = (Integer.parseInt(val) / 100) + "";
						jsonString.append("Current Temp." + ": " + resultTable.get(keyVal) + "\n");
					}
					if (keyVal.equals("sysState")) {
						String state = (String) resultTable.get(keyVal);
						jsonString.append("System State" + ": " + (state != null && state == "0" ? "Normal" : "Abnormal") + "\n");
					}
					if (keyVal.equals("sysNetworkUptime")) {
						jsonString.append("Sytem Network Uptimes(secs)" + ": " + resultTable.get(keyVal) + "\n");
					}
					
					

				}
				status = ResultStatus.SUCCESS;
			} else {
				status = ResultStatus.FAIL;
				log.debug("resultTable Is Null");
			}
		} catch (Exception e) {
			log.error(e, e);
			e.printStackTrace();
			errorReason = e.toString();
			log.debug("errorReason: " + errorReason);
			status = ResultStatus.FAIL;
		}

		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		Code operationCode = codeManager.getCodeByCode("8.3.4"); //DCU Status Monitoring
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : errorReason);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", errorReason);
		mav.addObject("jsonString", jsonString.toString());
		return mav;
	}
	@RequestMapping(value = "/gadget/device/command/getMCUStatus_PKS")
	public ModelAndView getMCUStatus_PKS(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		MCU mcu = null;
		Supplier supplier = null;
		String errorReason = "";
		StringBuffer jsonString = new StringBuffer("");
		try {

			if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.3.4")) { //DCU Status Monitoring
				mav.addObject("rtnStr", "No permission");
				return mav;
			}
			if (target == null || "".equals(target)) {
				status = ResultStatus.INVALID_PARAMETER;
				mav.addObject("rtnStr", "Target ID null!");
				return mav;
			}

			log.debug("target : " + target);
			mcu = mcuManager.getMCU(Integer.parseInt(target));
			supplier = mcu.getSupplier();
			Map resultTable = cmdOperationUtil.getMCUStatus(mcu.getSysID());
			if (resultTable != null && resultTable.size() > 0) {
				Iterator<String> keys = resultTable.keySet().iterator();
				String keyVal = null;
				while (keys.hasNext()) {
					keyVal = (String) keys.next();
					mav.addObject(keyVal, resultTable.get(keyVal));
					if (keyVal.equals("sysTime")) {
						String val = (String) resultTable.get(keyVal);
						mav.addObject(keyVal, TimeLocaleUtil.getLocaleDate(val,	supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()) + "\n");
					}
					if (keyVal.equals("sysState")) {
						String state = (String) resultTable.get(keyVal);
						mav.addObject(keyVal, (state != null && state == "0" ? "Normal" : "Abnormal"));
					}
				}
				status = ResultStatus.SUCCESS;
			} else {
				status = ResultStatus.FAIL;
				log.debug("resultTable Is Null");
			}
		} catch (Exception e) {
			log.error(e, e);
			e.printStackTrace();
			errorReason = e.toString();
			log.debug("errorReason: " + errorReason);
			status = ResultStatus.FAIL;
		}

		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		Code operationCode = codeManager.getCodeByCode("8.3.4"); //DCU Status Monitoring
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : errorReason);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", errorReason);
		mav.addObject("jsonString", jsonString.toString());
		return mav;
	}
	
	@RequestMapping(value = "/gadget/device/command/cmdMcuSetGMT")
	public ModelAndView cmdMcuSetGMT(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.3.8")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		try {
			long gmtTime = cmdOperationUtil.cmdMcuSetGMT(mcu.getSysID());

			status = ResultStatus.SUCCESS;

			StringBuffer rStr = new StringBuffer();
			rStr.append("[");

			rStr.append("{\"name\":\"");
			rStr.append("mcuId");
			rStr.append("\",\"value\":\"");
			rStr.append(mcu.getSysID());
			rStr.append("\"}");
			rStr.append(",");
			rStr.append("{\"name\":\"");
			rStr.append("gmtTime");
			rStr.append("\",\"value\":\"");
			rStr.append(TimeUtil.getDateUsingFormat(gmtTime, "yyyy-MM-dd HH:mm:ss"));
			rStr.append("\"}");
			rStr.append("]");
			rtnStr = rStr.toString();

		} catch (Exception e) {
			rtnStr = e.getMessage();
		}

		Code operationCode = codeManager.getCodeByCode("8.3.8");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdMcuScanning")
	public ModelAndView cmdMcuScanning(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.3.2")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		StringBuffer jsonString = new StringBuffer("");
		MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		try {
			Hashtable resultTable = cmdOperationUtil.doMCUScanning(mcu);
			if (resultTable != null && resultTable.size() > 0) {
				Iterator<String> keys = resultTable.keySet().iterator();
				String keyVal = null;
				while (keys.hasNext()) {
					keyVal = (String) keys.next();
					mav.addObject(keyVal, resultTable.get(keyVal));
					String titleName = keyVal.replaceAll("sys", "");
					jsonString.append(titleName + ": " + resultTable.get(keyVal) + "\n");
				}
				status = ResultStatus.SUCCESS;
			} else {
				status = ResultStatus.FAIL;
				log.debug("resultTable Is Null");
			}

		} catch (Exception e) {
			rtnStr = e.toString();
			log.error(e, e);
		}

		Code operationCode = codeManager.getCodeByCode("8.3.2");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		mav.addObject("jsonString", jsonString.toString());
		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdMcuPing")
	public ModelAndView cmdMcuPing(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "packetSize", required = false) int packetSize,
			@RequestParam(value = "count", required = false) int count) {
		
		String currentDay = "";
		String currentTime = "";
		String lastCommDate = "";

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.3.1")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		String cmdResult = "";

		StringBuffer jsonString = new StringBuffer("");

		MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			cmdResult = cmdOperationUtil.doMCUPing(mcu, packetSize, count);
			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.toString();
			log.error(e, e);
		}

		Code mcuStatusNormal = codeDao.findByCondition("code", "1.1.4.1");
		Code mcuStatusFail = codeDao.findByCondition("code", "1.1.4.5");

		if (cmdResult.equals("FAIL")) {
			mcu.setMcuStatus(mcuStatusFail);
			mcuManager.updateMCU(mcu);
		} else if (cmdResult.equals("NO-IP")) {
			mcu.setMcuStatus(mcuStatusFail);
			mcuManager.updateMCU(mcu);
			cmdResult = "This command is not supported. Because there is no ip address.";
		} else if (cmdResult.equals("FEP-DOWN")) {
			mcu.setMcuStatus(mcuStatusFail);
			mcuManager.updateMCU(mcu);
			cmdResult = "FEP-Server is down.";
		} else if (cmdResult.contains("FAIL")) { // IPv4, IPv6 모두 COAP-Ping 실행시, 결과가 둘 중 하나라도 FAIL일 경우
			mcu.setMcuStatus(mcuStatusFail);
			mcuManager.updateMCU(mcu);
		} else if (cmdResult.equals("LOSS-ALL")) {
			mcu.setMcuStatus(mcuStatusFail);
			mcuManager.updateMCU(mcu);
			cmdResult = "FAIL - 100% packet loss.";
		} else {
			try {
				currentDay = TimeUtil.getCurrentDay();
				currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
				lastCommDate = currentDay + currentTime;

				mcu.setMcuStatus(mcuStatusNormal);
				mcu.setLastCommDate(lastCommDate);
				mcuManager.updateMCU(mcu);

			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		mav.addObject("jsonString", cmdResult);
		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdMcuCOAPPing")
	public ModelAndView cmdMcuCOAPPing(
			@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) {
		ModelAndView mav = new ModelAndView("jsonView");
		String currentDay = "";
		String currentTime = "";
		String lastCommDate = "";
		String cmdResult = "";

		MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			cmdResult = cmdOperationUtil.doMcuCOAPPing(mcu);
		} catch (Exception e) {
			cmdResult = "FAIL";
			e.printStackTrace();
		}

		Code mcuStatusNormal = codeDao.findByCondition("code", "1.1.4.1");
		Code mcuStatusFail = codeDao.findByCondition("code", "1.1.4.5");
		if (cmdResult.equals("FAIL")) {
			mcu.setMcuStatus(mcuStatusFail);
			mcuManager.updateMCU(mcu);
		} else if (cmdResult.equals("NO-IP")) { 
			mcu.setMcuStatus(mcuStatusFail);
			mcuManager.updateMCU(mcu);
			
			cmdResult = "This command is not supported. Because there is no ip address.";
		} else if (cmdResult.equals("FEP-DOWN")) { 
			mcu.setMcuStatus(mcuStatusFail);
			mcuManager.updateMCU(mcu);
			
			cmdResult = "FEP-Server is down.";
		} else if (cmdResult.contains("FAIL")) { // IPv4, IPv6 모두 COAP-Ping 실행 시, 결과가 둘 중 하나라도 FAIL일 경우
			mcu.setMcuStatus(mcuStatusFail);
			mcuManager.updateMCU(mcu);
		} else if (cmdResult.contains("Timeout")) {
			mcu.setMcuStatus(mcuStatusFail);
			mcuManager.updateMCU(mcu);
		} else {
			try {
				currentDay = TimeUtil.getCurrentDay();
				currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
				lastCommDate = currentDay + currentTime;

				mcu.setMcuStatus(mcuStatusNormal);
				mcu.setLastCommDate(lastCommDate);
				mcuManager.updateMCU(mcu);
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		mav.addObject("jsonString", cmdResult);
		
		return mav;
	}

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/gadget/device/command/cmdMcuTraceroute")
    public ModelAndView cmdMcuTraceroute(
            @RequestParam(value = "target", required = false) String target,
            @RequestParam(value = "loginId", required = false) String loginId,
            @RequestParam(value = "hopCount", required = false) String hopCount) {
    	
        ResultStatus status = ResultStatus.FAIL;
        ModelAndView mav = new ModelAndView("jsonView");
        String rtnStr = null;
        String cmdResult = null;

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			cmdResult = cmdOperationUtil.doMCUTraceroute(mcu, hopCount);
			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.toString();
			log.error(e, e);
		}

		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		mav.addObject("jsonString", cmdResult);
		return mav;
    }

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdCODIPing")
	public ModelAndView cmdCODIPing(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "packetSize", required = false) int packetSize,
			@RequestParam(value = "count", required = false) int count) throws Exception {
		String currentDay = "";
		String currentTime = "";
		String lastCommDate = "";

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");
		
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		String cmdResult = "";

		if ("-".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Coordinator does not exist.");
			return mav;
		}
		Modem modem = modemManager.getModem(target);
		if (modem == null) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Coordinator does not exist.");
			return mav;
		}
		
		Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		try {
			cmdResult = cmdOperationUtil.doModemPing(modem, packetSize, count);
			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.toString();
			log.error(e, e);
		}

		Code modemStatusNormal = codeDao.findByCondition("code", "1.2.7.3");
		Code modemStatusFail = codeDao.findByCondition("code", "1.2.7.6");

		if (cmdResult.equals("FAIL")) {
			modem.setModemStatus(modemStatusFail);
			modemManager.updateModem(modem);
		} else if (cmdResult.equals("NO-IP")) { 
			modem.setModemStatus(modemStatusFail);
			modemManager.updateModem(modem);			
			cmdResult = "This command is not supported.Because there is no ip address.";
		} else if (cmdResult.equals("FEP-DOWN")) {
			modem.setModemStatus(modemStatusFail);
			modemManager.updateModem(modem);			
			cmdResult = "FEP-Server is down.";
		} else if (cmdResult.contains("FAIL")) { // IPv4, IPv6 모두 COAP-Ping 실행시, 결과가 둘 중 하나라도 FAIL일 경우
			modem.setModemStatus(modemStatusFail);
			modemManager.updateModem(modem);
		} else if (cmdResult.equals("LOSS-ALL")) {
			modem.setModemStatus(modemStatusFail);
			modemManager.updateModem(modem);
			cmdResult = "FAIL - 100% packet loss.";
		} else if (cmdResult.equals("NOT-SUPPORT")) {
			modem.setModemStatus(modemStatusFail);
			modemManager.updateModem(modem);
			cmdResult = "Does not support the SMS service to ping.";
		} else {
			try {
				currentDay = TimeUtil.getCurrentDay();
				currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
				lastCommDate = currentDay + currentTime;

				modem.setModemStatus(modemStatusNormal);
				modem.setLastLinkTime(lastCommDate);
				modemManager.updateModem(modem);
			} catch (ParseException e) {
				log.error("Exception occurred in cmdModemPing : " + e,e);
			}
		}
		
		
		Code operationCode = codeManager.getCodeByCode("8.2.8"); // Modem Information
		Code targetTypeCode = codeManager.getCodeByCode("1.2"); // Modem
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, targetTypeCode, modem.getDeviceSerial(),
					loginId, operationCode, status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}

		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		mav.addObject("jsonString", cmdResult);
		return mav;
	}    
    
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdModemPing")
	public ModelAndView cmdModemPing(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "packetSize", required = false) int packetSize,
			@RequestParam(value = "count", required = false) int count,
			@RequestParam(value = "device", required = false) String device) throws Exception {
		String currentDay = "";
		String currentTime = "";
		String lastCommDate = "";

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.1")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		String cmdResult = "";

		StringBuffer jsonString = new StringBuffer("");
		Modem modem = modemManager.getModem(Integer.parseInt(target));

		Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		try {
			cmdResult = cmdOperationUtil.doModemPing(modem, packetSize, count);
			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.toString();
			log.error(e, e);
		}

		Code modemStatusNormal = codeDao.findByCondition("code", "1.2.7.3");
		Code modemStatusFail = codeDao.findByCondition("code", "1.2.7.6");
		Code meterStatusNormal = codeDao.findByCondition("code", "1.3.3.1");
		Code meterStatusFail = codeDao.findByCondition("code", "1.3.3.14");

		if (cmdResult.equals("FAIL")) {
			if (device.toString().equals("modem")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
			} else { // meter에서 Ping 요청, 실패 시

				Meter meter = null;
				
				if(!modem.getMeter().isEmpty()) {
	                for (Meter m : modem.getMeter()) {
	                    if (m.getModemPort() == null || m.getModemPort() == 0) {
	                        meter = m;
	                        break;
	                    }
	                }
					
	                if(meter != null) {
						Code meterStatus = meter.getMeterStatus();
						if (meterStatus.getCode().equals("1.3.3.5") || meterStatus.getCode().equals("1.3.3.9")) {
							log.info("Meter 상태가 '정전' 또는 '철거' 상태일 때, DB에 데이터 적용 안함");
						} else {
							meter.setMeterStatus(meterStatusFail);
						}
						meterManager.updateMeter(meter);
	                }
				}
			}
		} else if (cmdResult.equals("NO-IP")) { 
			if (device.toString().equals("modem")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
			} else { // meter에서 Ping 요청, 실패 시
				
				Meter meter = null;
				
				if(!modem.getMeter().isEmpty()) {
	                for (Meter m : modem.getMeter()) {
	                    if (m.getModemPort() == null || m.getModemPort() == 0) {
	                        meter = m;
	                        break;
	                    }
	                }
					
	                if(meter != null) {
						Code meterStatus = meter.getMeterStatus();
						if (meterStatus.getCode().equals("1.3.3.5") || meterStatus.getCode().equals("1.3.3.9")) {
							log.info("Meter 상태가 '정전' 또는 '철거' 상태일 때, DB에 데이터 적용 안함");
						} else {
							meter.setMeterStatus(meterStatusFail);
						}
						meterManager.updateMeter(meter);
	                }
				}
			}
			
			cmdResult = "This command is not supported. Because there is no ip address.";
		} else if (cmdResult.equals("FEP-DOWN")) {
			if (device.toString().equals("modem")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
			} else { // meter에서 Ping 요청, 실패 시
				Meter meter = null;
				
				if(!modem.getMeter().isEmpty()) {
	                for (Meter m : modem.getMeter()) {
	                    if (m.getModemPort() == null || m.getModemPort() == 0) {
	                        meter = m;
	                        break;
	                    }
	                }
					
	                if(meter != null) {
						Code meterStatus = meter.getMeterStatus();
						if (meterStatus.getCode().equals("1.3.3.5") || meterStatus.getCode().equals("1.3.3.9")) {
							log.info("Meter 상태가 '정전' 또는 '철거' 상태일 때, DB에 데이터 적용 안함");
						} else {
							meter.setMeterStatus(meterStatusFail);
						}
						meterManager.updateMeter(meter);
	                }
				}
			}
			
			cmdResult = "FEP-Server is down.";
		} else if (cmdResult.contains("FAIL")) { // IPv4, IPv6 모두 COAP-Ping 실행시, 결과가 둘 중 하나라도 FAIL일 경우
			if (device.toString().equals("modem")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
			} else {
				Meter meter = null;
				
				if(!modem.getMeter().isEmpty()) {
	                for (Meter m : modem.getMeter()) {
	                    if (m.getModemPort() == null || m.getModemPort() == 0) {
	                        meter = m;
	                        break;
	                    }
	                }
					
	                if(meter != null) {
						Code meterStatus = meter.getMeterStatus();
						if (meterStatus.getCode().equals("1.3.3.5") || meterStatus.getCode().equals("1.3.3.9")) {
							log.info("Meter 상태가 '정전' 또는 '철거' 상태일 때, DB에 데이터 적용 안함");
						} else {
							meter.setMeterStatus(meterStatusFail);
						}
						meterManager.updateMeter(meter);
	                }
				}
			}
		} else if (cmdResult.equals("LOSS-ALL")) {
			if (device.toString().equals("modem")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
			} else {
				Meter meter = null;
				
				if(!modem.getMeter().isEmpty()) {
	                for (Meter m : modem.getMeter()) {
	                    if (m.getModemPort() == null || m.getModemPort() == 0) {
	                        meter = m;
	                        break;
	                    }
	                }
					
	                if(meter != null) {
						Code meterStatus = meter.getMeterStatus();
						if (meterStatus.getCode().equals("1.3.3.5") || meterStatus.getCode().equals("1.3.3.9")) {
							log.info("Meter 상태가 '정전' 또는 '철거' 상태일 때, DB에 데이터 적용 안함");
						} else {
							meter.setMeterStatus(meterStatusFail);
						}
						meterManager.updateMeter(meter);
	                }
				}
			}
			
			//cmdResult = "FAIL - 100% packet loss.";
			cmdResult = "Ping FAIL";
		} else if (cmdResult.equals("NOT-SUPPORT")) {
			modem.setModemStatus(modemStatusFail);
			modemManager.updateModem(modem);
			cmdResult = "Does not support the SMS service to ping.";
		} else {
			try {
				currentDay = TimeUtil.getCurrentDay();
				currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
				lastCommDate = currentDay + currentTime;
				if (device.toString().equals("modem")) {
					modem.setModemStatus(modemStatusNormal);
					modem.setLastLinkTime(lastCommDate);
					modemManager.updateModem(modem);
				} else { // meter에서 Ping 요청, 성공 시
					
					Meter meter = null;
					
					if(!modem.getMeter().isEmpty()) {
		                for (Meter m : modem.getMeter()) {
		                    if (m.getModemPort() == null || m.getModemPort() == 0) {
		                        meter = m;
		                        break;
		                    }
		                }
						
		                if(meter != null) {
							Code meterStatus = meter.getMeterStatus();
							if (meterStatus.getCode().equals("1.3.3.5") || meterStatus.getCode().equals("1.3.3.9")) {
								log.info("Meter 상태가 '정전' 또는 '철거' 상태일 때, DB에 데이터 적용 안함");
							} else {
								meter.setMeterStatus(meterStatusNormal);
								meter.setLastReadDate(lastCommDate);
							}
							meterManager.updateMeter(meter);
		                }
					}
				}
			} catch (ParseException e) {
				log.error("Exception occurred in cmdModemPing : " + e,e);
			}
		}
		
		
		Code operationCode = codeManager.getCodeByCode("8.2.1"); // Modem Ping
		Code targetTypeCode = codeManager.getCodeByCode("1.2"); // Modem
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, targetTypeCode, modem.getDeviceSerial(),
					loginId, operationCode, status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		

		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		mav.addObject("jsonString", cmdResult);
		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdModemCOAPPing")
	public ModelAndView cmdModemCOAPPing(
			@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "device", required = false) String device) throws Exception {
		log.debug("[cmdModemCOAPPing] " + " target: " +  target);
		ModelAndView mav = new ModelAndView("jsonView");
		String currentDay = "";
		String currentTime = "";
		String lastCommDate = "";
		String cmdResult = "";
		String ipv4 = "";
		String ipv6 = "";
		String type ="";

		Modem modem = modemManager.getModem(Integer.parseInt(target));
		String deviceSerial = modem.getDeviceSerial();
		Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		// SubGiga/MMIU별 IP 할당 (S)
		if (modem.getModemType() == ModemType.SubGiga) {
			SubGiga subGigaModem = (SubGiga) modem;
			ipv4 = subGigaModem.getIpAddr();
			ipv6 = subGigaModem.getIpv6Address();
			type = ModemIFType.RF.name();
		} else if ((modem.getModemType() == ModemType.MMIU)) {
			MMIU mmiuModem = (MMIU) modem;
			ipv4 = mmiuModem.getIpAddr();
			ipv6 = mmiuModem.getIpv6Address();
			//type = mmiuModem.getFwVer();
			// INSERT START SP-828
			if (modem.getProtocolType() == Protocol.IP) {
				type = ModemIFType.Ethernet.name();
			}
			else if (modem.getProtocolType() == Protocol.GPRS) {
				type = ModemIFType.MBB.name();
			}
			// INSERT END SP-828
		}
		// SubGiga/MMIU별 IP 할당 (E)
		
		// MBB(SMS)
		if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){	
			try{ 
				Map<String, String> result;
				
				/**
				 * if you have to send command using NI Protocol. you have to set "SMSConstants.PROTOCOL_TYPE.NI"
  				 * if you have to send command using COAP Protocol. you have to set "SMSConstants.PROTOCOL_TYPE.COAP"	
				 */
				
				result = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.COAP.getTypeCode(), "coapPing", null);
			 
				if(result != null)
					cmdResult =  result.get("RESULT").toString();
				else{
					log.debug("SMS Fail");
					cmdResult="Check the results in Async History tab";
				}
			}catch(Exception e){
				log.debug("SMS Fail");
				e.printStackTrace();
				cmdResult="FAIL";
			}
		}
		else{ //RF or Ethernet
				try {
					cmdResult = cmdOperationUtil.doModemCOAPPing(modem, ipv4, ipv6, type);
				} catch (Exception e) {
					e.printStackTrace();
					cmdResult="FAIL";
				}
			}
		Code modemStatusNormal = codeDao.findByCondition("code", "1.2.7.3");
		Code modemStatusFail = codeDao.findByCondition("code", "1.2.7.6");
		Code meterStatusNormal = codeDao.findByCondition("code", "1.3.3.1");
		Code meterStatusFail = codeDao.findByCondition("code", "1.3.3.14");

		if (cmdResult.equals("FAIL")) {
			if (device.toString().equals("modem")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				cmdResult = "FAIL";
			} else { // meter에서 Ping 요청, 실패 시

				cmdResult = "FAIL";
				Meter meter = null;
				
				if(!modem.getMeter().isEmpty()) {
	                for (Meter m : modem.getMeter()) {
	                    if (m.getModemPort() == null || m.getModemPort() == 0) {
	                        meter = m;
	                        break;
	                    }
	                }					
	                if(meter != null) {
						Code meterStatus = meter.getMeterStatus();
						if (meterStatus.getCode().equals("1.3.3.5") || meterStatus.getCode().equals("1.3.3.9")) {
							log.info("Meter 상태가 '정전' 또는 '철거' 상태일 때, DB에 데이터 적용 안함");
						} else {
							meter.setMeterStatus(meterStatusFail);
						}
						meterManager.updateMeter(meter);
	                }
				}
			}
		} else if (cmdResult.equals("NO-IP")) { 
			if (device.toString().equals("modem")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
			} else { // meter에서 Ping 요청, 실패 시
				Meter meter = null;
				
				if(!modem.getMeter().isEmpty()) {
	                for (Meter m : modem.getMeter()) {
	                    if (m.getModemPort() == null || m.getModemPort() == 0) {
	                        meter = m;
	                        break;
	                    }
	                }					
	                if(meter != null) {
						Code meterStatus = meter.getMeterStatus();
						if (meterStatus.getCode().equals("1.3.3.5") || meterStatus.getCode().equals("1.3.3.9")) {
							log.info("Meter 상태가 '정전' 또는 '철거' 상태일 때, DB에 데이터 적용 안함");
						} else {
							meter.setMeterStatus(meterStatusFail);
						}
						meterManager.updateMeter(meter);
	                }
				}
			}
			
			cmdResult = "This command is not supported. Because there is no ip address.";
		} else if (cmdResult.equals("FEP-DOWN")) { 
			if (device.toString().equals("modem")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
			} else { // meter에서 Ping 요청, 실패 시
				Meter meter = null;
				
				if(!modem.getMeter().isEmpty()) {
	                for (Meter m : modem.getMeter()) {
	                    if (m.getModemPort() == null || m.getModemPort() == 0) {
	                        meter = m;
	                        break;
	                    }
	                }					
	                if(meter != null) {
						Code meterStatus = meter.getMeterStatus();
						if (meterStatus.getCode().equals("1.3.3.5") || meterStatus.getCode().equals("1.3.3.9")) {
							log.info("Meter 상태가 '정전' 또는 '철거' 상태일 때, DB에 데이터 적용 안함");
						} else {
							meter.setMeterStatus(meterStatusFail);
						}
						meterManager.updateMeter(meter);
	                }
				}
			}
			
			cmdResult = "FEP-Server is down.";
		} else if (cmdResult.contains("FAIL")) { // IPv4, IPv6 모두 COAP-Ping 실행 시, 결과가 둘 중 하나라도 FAIL일 경우
			if (device.toString().equals("modem")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
			} else { // meter에서 Ping 요청, 실패 시
				Meter meter = null;
				
				if(!modem.getMeter().isEmpty()) {
	                for (Meter m : modem.getMeter()) {
	                    if (m.getModemPort() == null || m.getModemPort() == 0) {
	                        meter = m;
	                        break;
	                    }
	                }					
	                if(meter != null) {
						Code meterStatus = meter.getMeterStatus();
						if (meterStatus.getCode().equals("1.3.3.5") || meterStatus.getCode().equals("1.3.3.9")) {
							log.info("Meter 상태가 '정전' 또는 '철거' 상태일 때, DB에 데이터 적용 안함");
						} else {
							meter.setMeterStatus(meterStatusFail);
						}
						meterManager.updateMeter(meter);
	                }
				}
			}
		} else if (cmdResult.contains("Timeout")) {
			if (device.toString().equals("modem")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
			} else { // meter에서 Ping 요청, Timeout 시
				Meter meter = null;
				
				if(!modem.getMeter().isEmpty()) {
	                for (Meter m : modem.getMeter()) {
	                    if (m.getModemPort() == null || m.getModemPort() == 0) {
	                        meter = m;
	                        break;
	                    }
	                }					
	                if(meter != null) {
						Code meterStatus = meter.getMeterStatus();
						if (meterStatus.getCode().equals("1.3.3.5") || meterStatus.getCode().equals("1.3.3.9")) {
							log.info("Meter 상태가 '정전' 또는 '철거' 상태일 때, DB에 데이터 적용 안함");
						} else {
							meter.setMeterStatus(meterStatusFail);
						}
						meterManager.updateMeter(meter);
	                }
				}
			}
		} else {
			try {
				currentDay = TimeUtil.getCurrentDay();
				currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
				lastCommDate = currentDay + currentTime;
				if (device.toString().equals("modem")) {
					modem.setModemStatus(modemStatusNormal);
					modem.setLastLinkTime(lastCommDate);
					modemManager.updateModem(modem);
				} else { // meter에서 COAP-Ping 요청, 성공 시
					Meter meter = null;
					
					if(!modem.getMeter().isEmpty()) {
		                for (Meter m : modem.getMeter()) {
		                    if (m.getModemPort() == null || m.getModemPort() == 0) {
		                        meter = m;
		                        break;
		                    }
		                }
						
		                if(meter != null) {
							Code meterStatus = meter.getMeterStatus();
							if (meterStatus.getCode().equals("1.3.3.5") || meterStatus.getCode().equals("1.3.3.9")) {
								log.info("Meter 상태가 '정전' 또는 '철거' 상태일 때, DB에 데이터 적용 안함");
							} else {
								meter.setMeterStatus(meterStatusNormal);
								meter.setLastReadDate(lastCommDate);
							}
							meterManager.updateMeter(meter);
		                }
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		mav.addObject("jsonString", cmdResult);
		return mav;
	}

	public Map<String,Object> sendSms(Map<String, Object> condition, List<String> paramList, String cmdMap) throws Exception {
		Map<String, Object> resultMap = new HashMap<String,Object>();
		String euiId = condition.get("euiId").toString();
		String messageId = cmdOperationUtil.sendSMS(condition, paramList, cmdMap);
		String commandCode =  condition.get("commandCode").toString();
		
		// 결과처리 로직 (S)
		String rtnMessage = null;
		// MBB Modem으로 전송하는 SMS 명령이
		// 55(set up environment For NI),56(~~CoAP),57(~~SNMP)일 경우
		// Async_command_Result 조회를 하지않고, message id만 55, 56, 57 명령 처리 로직으로 넘겨준다.
		if (    commandCode.equals(COMMAND_TYPE.NI.getTypeCode())
			    || commandCode.equals(COMMAND_TYPE.COAP.getTypeCode())
                || commandCode.equals(COMMAND_TYPE.SNMP.getTypeCode()) ) {
			if (messageId.equals("FAIL")) {
				resultMap.put("messageId", "F");
			} else if (messageId.equals("FAIL-CONNECT")) {
				resultMap.put("messageId", "CF");
			} else {
				resultMap.put("messageId", messageId);
			}
		} else {
			if (messageId.equals("FAIL")) {
				resultMap.put("messageType", "F");
			} else if (messageId.equals("FAIL-CONNECT")) {
				resultMap.put("messageType", "CF");
			} else {
				// 5초 간격 20초 동안 AsyncCommandResult 테이블 조회 로직 (S)
				int time = 0;
				int interver = 5000;		// 5 second
				int period  = 20000;		// 20 second

				while (time != period) {
					waitResponse(interver);
					time += interver;
					
					if (rtnMessage != null) {
						break;
					} else {
						try {
							rtnMessage = asyncCommandLogManager.getCmdResults(euiId, Long.parseLong(messageId));
						} catch (Exception e) {
							rtnMessage = null;
						}
					}
				}
				// 5초 간격 20초 동안 AsyncCommandResult 테이블 조회 로직 (E)
				
				if (rtnMessage == null) {
					resultMap.put("messageType", "F");
					return resultMap;
				}
				
				ResponseFrame responseFrame = new ResponseFrame();
				resultMap = responseFrame.decode(rtnMessage);

			}
		}
		// 결과처리 로직 (E)
		
		return resultMap;
	}
	
	public void waitResponse(int waitTime) {
		synchronized (resMonitor) {
			try {
				resMonitor.wait(waitTime);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/restartCommStack")
	public ModelAndView restartCommStack(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) throws Exception {
		
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> resultMap = new HashMap<String,Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		List<String> paramList = new ArrayList<String>();

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}
		
		Modem modem = modemManager.getModem(Integer.parseInt(target));
		Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		// MBB (MMIU / SMS)
		if (((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS))) {
			String commandName = COMMAND_NAME.RESTART_COMM_STACK.getCmdName();
			String messageType = MESSAGE_TYPE.REQ_ACK.getTypeCode();
			String commandCode = COMMAND_TYPE.RESTART_COMM_STACK.getTypeCode();
			MMIU mmiuModem = (MMIU) modem;
			String mobliePhNum = mmiuModem.getPhoneNumber();
			String euiId = modem.getDeviceSerial();
			
			if (mobliePhNum == null || euiId == null) {
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", "There is no EUI ID/Phone Number.");
				return mav;
			}
			
			condition.put("commandName", commandName);
			condition.put("messageType", messageType);
			condition.put("mobliePhNum", mobliePhNum);
			condition.put("euiId", euiId);
			condition.put("commandCode", commandCode);
			
			resultMap = sendSms(condition, paramList, null);
			
			// SMS 응답 결과 처리 로직(S)
			Code modemStatusNormal = codeDao.findByCondition("code", "1.2.7.3");
			Code modemStatusFail = codeDao.findByCondition("code", "1.2.7.6");
			String response_messageType = resultMap.get("messageType").toString();
			String errorMsg;
			
			if (response_messageType.equals("F")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Send SMS Fail.\n";
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}

			if (response_messageType.equals("CF")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Failed connect and bind to host.\n";
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
			
			if (response_messageType.equals("2")) {
				String currentDay = TimeUtil.getCurrentDay();
				String currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
				String lastCommDate = currentDay + currentTime;
				
				modem.setModemStatus(modemStatusNormal);
				modem.setLastLinkTime(lastCommDate);
				modemManager.updateModem(modem);
				
				status = ResultStatus.SUCCESS;
			} else if (response_messageType.equals("3")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = resultMap.get("errorCode").toString();
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
	    	// SMS 응답 결과 처리 로직(E)
		}
		
		mav.addObject("status", status.name());
		return mav;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/restartCommModem")
	public ModelAndView restartCommModem(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) throws Exception {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> resultMap = new HashMap<String,Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		List<String> paramList = new ArrayList<String>();

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}
		
		Modem modem = modemManager.getModem(Integer.parseInt(target));
		Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		// MBB (MMIU / SMS)
		if (((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS))) {
			String commandName = COMMAND_NAME.RESTART_COMM_MODEM.getCmdName();
			String messageType = MESSAGE_TYPE.REQ_ACK.getTypeCode();
			String commandCode = COMMAND_TYPE.RESTART_COMM_MODEM.getTypeCode();
			MMIU mmiuModem = (MMIU) modem;
			String mobliePhNum = mmiuModem.getPhoneNumber();
			String euiId = modem.getDeviceSerial();
			
			if (mobliePhNum == null || euiId == null) {
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", "There is no EUI ID/Phone Number.");
				return mav;
			}
			
			condition.put("commandName", commandName);
			condition.put("messageType", messageType);
			condition.put("mobliePhNum", mobliePhNum);
			condition.put("euiId", euiId);
			condition.put("commandCode", commandCode);

			resultMap = sendSms(condition, paramList, null);
			
			// SMS 응답 결과 처리 로직(S)
			Code modemStatusNormal = codeDao.findByCondition("code", "1.2.7.3");
			Code modemStatusFail = codeDao.findByCondition("code", "1.2.7.6");
			String response_messageType = resultMap.get("messageType").toString();
			String errorMsg;
			
			if (response_messageType.equals("F")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Send SMS Fail.\n";
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}

			if (response_messageType.equals("CF")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Failed connect and bind to host.\n";
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
			
			if (response_messageType.equals("2")) {
				String currentDay = TimeUtil.getCurrentDay();
				String currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
				String lastCommDate = currentDay + currentTime;
				
				modem.setModemStatus(modemStatusNormal);
				modem.setLastLinkTime(lastCommDate);
				modemManager.updateModem(modem);
				
				status = ResultStatus.SUCCESS;
			} else if (response_messageType.equals("3")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = resultMap.get("errorCode").toString();
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
	    	// SMS 응답 결과 처리 로직(E)
		}
		
		mav.addObject("status", status.name());
		return mav;
			
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/setAPN")
	public ModelAndView setAPN(
			@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "apnName", required = false) String apnName,
			@RequestParam(value = "apnId", required = false) String apnId,
			@RequestParam(value = "apnPassword", required = false) String apnPassword) throws Exception {
		
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> resultMap = new HashMap<String,Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		List<String> paramList = new ArrayList<String>();
		String rtnStr = null;
		
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}
		
		Modem modem = modemManager.getModem(Integer.parseInt(target));
		Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		// MBB (MMIU / SMS)
		if (((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS))) {
			String commandName = COMMAND_NAME.CHANGE_APN.getCmdName();
			String messageType = MESSAGE_TYPE.REQ_ACK.getTypeCode();
			String commandCode = COMMAND_TYPE.CHANGE_APN.getTypeCode();
			MMIU mmiuModem = (MMIU) modem;
			String mobliePhNum = mmiuModem.getPhoneNumber();
			String euiId = modem.getDeviceSerial();
			
			if (mobliePhNum == null || euiId == null) {
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", "There is no EUI ID/Phone Number.");
				return mav;
			}
			
			condition.put("commandName", commandName);
			condition.put("messageType", messageType);
			condition.put("mobliePhNum", mobliePhNum);
			condition.put("euiId", euiId);
			condition.put("commandCode", commandCode);
			
			paramList.add(apnName);
			paramList.add(apnId);
			paramList.add(apnPassword);
			resultMap = sendSms(condition, paramList, null);
			
			// SMS 응답 결과 처리 로직(S)
			Code modemStatusNormal = codeDao.findByCondition("code", "1.2.7.3");
			Code modemStatusFail = codeDao.findByCondition("code", "1.2.7.6");
			String response_messageType = resultMap.get("messageType").toString();
			String errorMsg;
			
			if (response_messageType.equals("F")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Send SMS Fail.\n";
				mav.addObject("status", status.name()); 
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}

			if (response_messageType.equals("CF")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Failed connect and bind to host.\n";
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
			
			if (response_messageType.equals("2")) {
				String currentDay = TimeUtil.getCurrentDay();
				String currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
				String lastCommDate = currentDay + currentTime;
				
				modem.setModemStatus(modemStatusNormal);
				modem.setLastLinkTime(lastCommDate);
				modemManager.updateModem(modem);
				
				status = ResultStatus.SUCCESS;
			} else if (response_messageType.equals("3")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = resultMap.get("errorCode").toString();
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
	    	// SMS 응답 결과 처리 로직(E)
		}
		
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/doWatchdog")
	public ModelAndView doWatchdog(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) throws Exception {
		
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> resultMap = new HashMap<String,Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		List<String> paramList = new ArrayList<String>();

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}
		
		Modem modem = modemManager.getModem(Integer.parseInt(target));
		Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		// MBB (MMIU / SMS)
		if (((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS))) {
			String commandName = COMMAND_NAME.WATCHDOG.getCmdName();
			String messageType = MESSAGE_TYPE.REQ_ACK.getTypeCode();
			String commandCode = COMMAND_TYPE.WATCHDOG.getTypeCode();
			MMIU mmiuModem = (MMIU) modem;
			String mobliePhNum = mmiuModem.getPhoneNumber();
			String euiId = modem.getDeviceSerial();
			
			if (mobliePhNum == null || euiId == null) {
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", "There is no EUI ID/Phone Number.");
				return mav;
			}
			
			condition.put("commandName", commandName);
			condition.put("messageType", messageType);
			condition.put("mobliePhNum", mobliePhNum);
			condition.put("euiId", euiId);
			condition.put("commandCode", commandCode);
			
			resultMap = sendSms(condition, paramList, null);
			
			// SMS 응답 결과 처리 로직(S)
			Code modemStatusNormal = codeDao.findByCondition("code", "1.2.7.3");
			Code modemStatusFail = codeDao.findByCondition("code", "1.2.7.6");
			String response_messageType = resultMap.get("messageType").toString();
			String errorMsg;
			
			if (response_messageType.equals("F")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Send SMS Fail.\n";
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}

			if (response_messageType.equals("CF")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Failed connect and bind to host.\n";
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
			
			if (response_messageType.equals("2")) {
				String currentDay = TimeUtil.getCurrentDay();
				String currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
				String lastCommDate = currentDay + currentTime;
				
				modem.setModemStatus(modemStatusNormal);
				modem.setLastLinkTime(lastCommDate);
				modemManager.updateModem(modem);
				
				status = ResultStatus.SUCCESS;
			} else if (response_messageType.equals("3")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = resultMap.get("errorCode").toString();
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
	    	// SMS 응답 결과 처리 로직(E)
		}
		
		mav.addObject("status", status.name());
		return mav;
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/getAccessTechnology")
	public ModelAndView getAccessTechnology(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) throws Exception {
		
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		
		Map<String, Object> resultMap = new HashMap<String,Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		List<String> paramList = new ArrayList<String>();
		String rtnStr = null;

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}
		
		Modem modem = modemManager.getModem(Integer.parseInt(target));
		Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		// MBB (MMIU / SMS)
		if (((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS))) {
			String commandName = COMMAND_NAME.ACCESS_TECHNOLOGY.getCmdName();
			String messageType = MESSAGE_TYPE.REQ_ACK.getTypeCode();
			String commandCode = COMMAND_TYPE.ACCESS_TECHNOLOGY.getTypeCode();
			MMIU mmiuModem = (MMIU) modem;
			String mobliePhNum = mmiuModem.getPhoneNumber();
			String euiId = modem.getDeviceSerial();
			
			if (mobliePhNum == null || euiId == null) {
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", "There is no EUI ID/Phone Number.");
				return mav;
			}
			
			condition.put("commandName", commandName);
			condition.put("messageType", messageType);
			condition.put("mobliePhNum", mobliePhNum);
			condition.put("euiId", euiId);
			condition.put("commandCode", commandCode);
			
			resultMap = sendSms(condition, paramList, null);
			
			// SMS 응답 결과 처리 로직(S)
			Code modemStatusNormal = codeDao.findByCondition("code", "1.2.7.3");
			Code modemStatusFail = codeDao.findByCondition("code", "1.2.7.6");
			String response_messageType = resultMap.get("messageType").toString();
			String errorMsg;
			
			if (response_messageType.equals("F")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Send SMS Fail.\n";
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}

			if (response_messageType.equals("CF")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Failed connect and bind to host.\n";
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
			
			if (response_messageType.equals("2")) {
				String currentDay = TimeUtil.getCurrentDay();
				String currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
				String lastCommDate = currentDay + currentTime;
				
				modem.setModemStatus(modemStatusNormal);
				modem.setLastLinkTime(lastCommDate);
				modemManager.updateModem(modem);
				
				List<String> response_paramList = (List<String>) resultMap.get("paramList");
				String rtnData = response_paramList.get(0);
				
				if (rtnData.equals("0")) {
					rtnData = "GSM";
				} else if (rtnData.equals("1")) {
					rtnData = "GSM Compact";
				} else if (rtnData.equals("2")) {
					rtnData = "UTRAN";
				} else if (rtnData.equals("3")) {
					rtnData = "GSM w/EGPRS";
				} else if (rtnData.equals("4")) {
					rtnData = "UTRAN w/HSDPA";
				} else if (rtnData.equals("5")) {
					rtnData = "UTRAN w/HSUPA";
				} else if (rtnData.equals("6")) {
					rtnData = "UTRAN w/HSDPA and HSUPA";
				} else if (rtnData.equals("7")) {
					rtnData = "E-UTRAN";
				}
				
				rtnStr = "<b>Access Technology: </b>" + rtnData;
				status = ResultStatus.SUCCESS;
			} else if (response_messageType.equals("3")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = resultMap.get("errorCode").toString();
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
	    	// SMS 응답 결과 처리 로직(E)
		}
		
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/getRSSI")
	public ModelAndView getRSSI(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) throws Exception {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> resultMap = new HashMap<String,Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		List<String> paramList = new ArrayList<String>();
		String rtnStr = null;

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}
		
		Modem modem = modemManager.getModem(Integer.parseInt(target));
		Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		// MBB (MMIU / SMS)
		if (((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS))) {
			String commandName = COMMAND_NAME.RSSI.getCmdName();
			String messageType = MESSAGE_TYPE.REQ_ACK.getTypeCode();
			String commandCode = COMMAND_TYPE.RSSI.getTypeCode();
			MMIU mmiuModem = (MMIU) modem;
			String mobliePhNum = mmiuModem.getPhoneNumber();
			String euiId = modem.getDeviceSerial();
			
			if (mobliePhNum == null || euiId == null) {
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", "There is no EUI ID/Phone Number.");
				return mav;
			}
			
			condition.put("commandName", commandName);
			condition.put("messageType", messageType);
			condition.put("mobliePhNum", mobliePhNum);
			condition.put("euiId", euiId);
			condition.put("commandCode", commandCode);
			
			resultMap = sendSms(condition, paramList, null);
			
			// SMS 응답 결과 처리 로직(S)
			Code modemStatusNormal = codeDao.findByCondition("code", "1.2.7.3");
			Code modemStatusFail = codeDao.findByCondition("code", "1.2.7.6");
			String response_messageType = resultMap.get("messageType").toString();
			String errorMsg;
			
			if (response_messageType.equals("F")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Send SMS Fail.\n";
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}

			if (response_messageType.equals("CF")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Failed connect and bind to host.\n";
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
			
			if (response_messageType.equals("2")) {
				String currentDay = TimeUtil.getCurrentDay();
				String currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
				String lastCommDate = currentDay + currentTime;
				
				modem.setModemStatus(modemStatusNormal);
				modem.setLastLinkTime(lastCommDate);
				modemManager.updateModem(modem);
				
				List<String> response_paramList = (List<String>) resultMap.get("paramList");
				
				String rtnData = response_paramList.get(0);
				String rssiSign = rtnData.substring(0, 1);
				String rssiValue = rtnData.substring(1, rtnData.length());
				
				rssiValue = rssiSign + rssiValue;
				
				rtnStr = "<b>RSSI: </b>" + rssiValue;
				status = ResultStatus.SUCCESS;
			} else if (response_messageType.equals("3")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = resultMap.get("errorCode").toString();
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
	    	// SMS 응답 결과 처리 로직(E)
		}
		
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/getUseddAPN")
	public ModelAndView getUseddAPN(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) throws Exception {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> resultMap = new HashMap<String,Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		List<String> paramList = new ArrayList<String>();
		String rtnStr = null;

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}
		
		Modem modem = modemManager.getModem(Integer.parseInt(target));
		Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		// MBB (MMIU / SMS)
		if (((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS))) {
			String commandName = COMMAND_NAME.APN_USED.getCmdName();
			String messageType = MESSAGE_TYPE.REQ_ACK.getTypeCode();
			String commandCode = COMMAND_TYPE.APN_USED.getTypeCode();
			MMIU mmiuModem = (MMIU) modem;
			String mobliePhNum = mmiuModem.getPhoneNumber();
			String euiId = modem.getDeviceSerial();
			
			if (mobliePhNum == null || euiId == null) {
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", "There is no EUI ID/Phone Number.");
				return mav;
			}
			
			condition.put("commandName", commandName);
			condition.put("messageType", messageType);
			condition.put("mobliePhNum", mobliePhNum);
			condition.put("euiId", euiId);
			condition.put("commandCode", commandCode);
			
			resultMap = sendSms(condition, paramList, null);
			
			// SMS 응답 결과 처리 로직(S)
			Code modemStatusNormal = codeDao.findByCondition("code", "1.2.7.3");
			Code modemStatusFail = codeDao.findByCondition("code", "1.2.7.6");
			String response_messageType = resultMap.get("messageType").toString();
			String errorMsg;
			
			if (response_messageType.equals("F")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Send SMS Fail.\n";
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}

			if (response_messageType.equals("CF")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Failed connect and bind to host.\n";
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
			
			if (response_messageType.equals("2")) {
				String currentDay = TimeUtil.getCurrentDay();
				String currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
				String lastCommDate = currentDay + currentTime;
				String apnName = null;
				String apnId = null;
				String apnPw = null;
				
				modem.setModemStatus(modemStatusNormal);
				modem.setLastLinkTime(lastCommDate);
				modemManager.updateModem(modem);
				
				List<String> response_paramList = (List<String>) resultMap.get("paramList");
				try {
					apnName = response_paramList.get(0);					
				} catch (Exception e) {
					apnName = "";
				}
				
				try {
					apnId = response_paramList.get(1);					
				} catch (Exception e) {
					apnId = "";
				}
				
				try {
					apnPw = response_paramList.get(2);					
				} catch (Exception e) {
					apnPw = "";
				}
				
				status = ResultStatus.SUCCESS;
				
				mav.addObject("status", status.name());
				mav.addObject("apnName", apnName);
				mav.addObject("apnId", apnId);
				mav.addObject("apnPw", apnPw);
				return mav;
				
			}  else if (response_messageType.equals("3")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = resultMap.get("errorCode").toString();
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
	    	// SMS 응답 결과 처리 로직(E)
		}
		
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
		
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/getIpAddress")
	public ModelAndView getIpAddress(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) throws Exception {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> resultMap = new HashMap<String,Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		List<String> paramList = new ArrayList<String>();
		String rtnStr = null;

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}
		
		Modem modem = modemManager.getModem(Integer.parseInt(target));
		Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		// MBB (MMIU / SMS)
		if (((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS))) {
			String commandName = COMMAND_NAME.IP_ADDRESS.getCmdName();
			String messageType = MESSAGE_TYPE.REQ_ACK.getTypeCode();
			String commandCode = COMMAND_TYPE.IP_ADDRESS.getTypeCode();
			MMIU mmiuModem = (MMIU) modem;
			String mobliePhNum = mmiuModem.getPhoneNumber();
			String euiId = modem.getDeviceSerial();
			
			if (mobliePhNum == null || euiId == null) {
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", "There is no EUI ID/Phone Number.");
				return mav;
			}
			
			condition.put("commandName", commandName);
			condition.put("messageType", messageType);
			condition.put("mobliePhNum", mobliePhNum);
			condition.put("euiId", euiId);
			condition.put("commandCode", commandCode);
			
			resultMap = sendSms(condition, paramList, null);
			
			// SMS 응답 결과 처리 로직(S)
			Code modemStatusNormal = codeDao.findByCondition("code", "1.2.7.3");
			Code modemStatusFail = codeDao.findByCondition("code", "1.2.7.6");
			String response_messageType = resultMap.get("messageType").toString();
			String errorMsg;
			
			if (response_messageType.equals("F")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Send SMS Fail.\n";
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}

			if (response_messageType.equals("CF")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Failed connect and bind to host.\n";
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
			
			if (response_messageType.equals("2")) {
				String currentDay = TimeUtil.getCurrentDay();
				String currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
				String lastCommDate = currentDay + currentTime;
				
				modem.setModemStatus(modemStatusNormal);
				modem.setLastLinkTime(lastCommDate);
				modemManager.updateModem(modem);
				
				List<String> response_paramList = (List<String>) resultMap.get("paramList");
				
				rtnStr = "<b>IP address: </b>" + response_paramList.get(0);
				status = ResultStatus.SUCCESS;
			} else if (response_messageType.equals("3")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = resultMap.get("errorCode").toString();
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
	    	// SMS 응답 결과 처리 로직(E)
		}
		
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	} 
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/setInitModem")
	public ModelAndView setInitModem(
			@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) throws Exception {
		
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> resultMap = new HashMap<String,Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		List<String> paramList = new ArrayList<String>();
		String rtnStr = null;

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}
		
		Modem modem = modemManager.getModem(Integer.parseInt(target));
		Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		// MBB (MMIU / SMS)
		if (((modem.getModemType() == ModemType.MMIU) && (modem.getProtocolType() == Protocol.SMS))) {
			String commandName = COMMAND_NAME.SET_INIT_MODEM.getCmdName();
			String messageType = MESSAGE_TYPE.REQ_ACK.getTypeCode();
			String commandCode = COMMAND_TYPE.SET_INIT_MODEM.getTypeCode();
			MMIU mmiuModem = (MMIU) modem;
			String mobliePhNum = mmiuModem.getPhoneNumber();
			String euiId = modem.getDeviceSerial();
			
			if (mobliePhNum == null || euiId == null) {
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", "There is no EUI ID/Phone Number.");
				return mav;
			}
			
			condition.put("commandName", commandName);
			condition.put("messageType", messageType);
			condition.put("mobliePhNum", mobliePhNum);
			condition.put("euiId", euiId);
			condition.put("commandCode", commandCode);
			
			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
			String serverIp = prop.getProperty("smpp.hes.fep.server") == null ? "" : prop.getProperty("smpp.hes.fep.server").trim();
			String serverPort = prop.getProperty("soria.modem.tls.port") == null ? "" : prop.getProperty("soria.modem.tls.port").trim();
			
			paramList.add(serverIp);
			paramList.add(serverPort);
			
			resultMap = sendSms(condition, paramList, null);
			
			// SMS 응답 결과 처리 로직(S)
			Code modemStatusNormal = codeDao.findByCondition("code", "1.2.7.3");
			Code modemStatusFail = codeDao.findByCondition("code", "1.2.7.6");
			String response_messageType = resultMap.get("messageType").toString();
			String errorMsg;
			
			if (response_messageType.equals("F")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Send SMS Fail.\n";
				mav.addObject("status", status.name()); 
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}

			if (response_messageType.equals("CF")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = "FAIL : [" + euiId + "] Failed connect and bind to host.\n";
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
			
			if (response_messageType.equals("2")) {
				String currentDay = TimeUtil.getCurrentDay();
				String currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
				String lastCommDate = currentDay + currentTime;
				
				modem.setModemStatus(modemStatusNormal);
				modem.setLastLinkTime(lastCommDate);
				modemManager.updateModem(modem);
				
				rtnStr = "SUCCESS - Setup Information : ip [" + serverIp + "], port [" + serverPort + "]";
				status = ResultStatus.SUCCESS;
			} else if (response_messageType.equals("3")) {
				modem.setModemStatus(modemStatusFail);
				modemManager.updateModem(modem);
				
				errorMsg = resultMap.get("errorCode").toString();
				mav.addObject("status", status.name());
				mav.addObject("rtnStr", errorMsg);
				return mav;
			}
	    	// SMS 응답 결과 처리 로직(E)
		}
		
		mav.addObject("status", status.name()); 
		mav.addObject("rtnStr", rtnStr);
		
		return mav;
	}
	
	/**
	 * GetInfo (CoAP)
	 **/
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdModemCoAP")
	public ModelAndView cmdModemCoAP(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "device", required = false) String device,
			@RequestParam(value = "chkCPU", required = false) String chkCPU,  
			@RequestParam(value = "chkMemory", required = false) String chkMemory,
			@RequestParam(value = "chkTxSize", required = false) String chkTxSize,
			@RequestParam(value = "chkRSSI", required = false) String chkRSSI,
			@RequestParam(value = "chkLQI", required = false) String chkLQI,
			@RequestParam(value = "chkETX", required = false) String chkETX,
			@RequestParam(value = "chkLastCommDate", required = false) String chkLastCommDate,
			@RequestParam(value = "chkNodeId", required = false) String chkNodeId) throws ParseException {
		log.debug("[cmdModemCoAP] " + " target: " +  target);
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Code modemStatusNormal = codeDao.findByCondition("code", "1.2.7.3"); // Normal
		Code modemStatusFail = codeDao.findByCondition("code", "1.2.7.6"); 	 // CommError
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.8")) { 	 // Modem Information
			mav.addObject("rtnStr", "No permission");
			log.info("No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			log.info("Target ID null!");
			return mav;
		}

		Modem modem = modemManager.getModem(Integer.parseInt(target));
		Map result = new HashMap<String, String>();
		String rtnStr = "";
		// 현재 날짜 구하기
		String currentDay = TimeUtil.getCurrentDay();
		String currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
		String lastCommDate = currentDay + currentTime;
		/*String ipv4ForMBB=""; 
		String ipv6ForMBB="";*/
		
		Supplier supplier = modem.getSupplier();		
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		// Date Format을 위한 설정
		log.debug("date format(s)");
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		log.debug("date format(e)");
		DecimalFormat df = DecimalUtil.getIntegerDecimalFormat(supplier.getMd());
		
		try {
			// MBB(SMS)
	        if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){   
	            try{ 
	                Map<String, String> asyncResult;
	                asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.COAP.getTypeCode() , "coapGetInfo", null);
	             
	                if(asyncResult != null)
	                	result =  asyncResult;
	                else{
	                	log.debug("SMS Fail");
	                    rtnStr = "Check the results in Async History tab\n";
						throw new Exception(rtnStr);
	                }
	             
	            }catch(Exception e){
	            	log.debug("SMS Fail");
	            	rtnStr = "Check the results in Async History tab\n";
					throw new Exception(rtnStr, e);
	            }
	        }else // RF or Ethernet
				result = cmdOperationUtil.coapGetInfo(modem,"","");
			
			rtnStr = ""; // 출력 메세지
			// IPv4
			if (result.containsKey("CPU") && chkCPU.equals("true"))
				rtnStr += " CPU Usage: " + result.get("CPU") + "%\n";
			if (result.containsKey("Memory") && chkMemory.equals("true"))
				rtnStr += " Memory Usage: " + result.get("Memory") + "%\n";
			if (result.containsKey("TxSize") && chkTxSize.equals("true"))
				rtnStr += " Total TX Size: " + df.format(DecimalUtil.ConvertNumberToInteger(result.get("TxSize"))) + " Byte\n";
			if (result.containsKey("RSSI") && (result.get("type").equals(ModemIFType.RF.name()) || result.get("type").equals(ModemIFType.Ethernet.name())) && chkRSSI.equals("true"))
				rtnStr += " RSSI: " + result.get("RSSI")+"\n";
			/*else if (result.containsKey("RSSI")&& chkRSSI.equals("true"))
				rtnStr += " RSSI: - (mbb/ethernet)\n";*/
			if (result.containsKey("LQI") && result.get("type").equals(ModemIFType.RF.name()) && chkLQI.equals("true"))
				rtnStr += " LQI: " + result.get("LQI")+"\n";
			/*else if (result.containsKey("LQI") && chkLQI.equals("true"))
				rtnStr += " LQI: - (mbb/ethernet)\n";*/
			if (result.containsKey("ETX") && result.get("type").equals(ModemIFType.RF.name()) && chkETX.equals("true")){
				if(Integer.parseInt(result.get("ETX").toString()) >= 100)
					rtnStr += " ETX: " + "0x"+result.get("ETX")+"\n";
				else
					rtnStr += "ETX: " +result.get("ETX") + "\n";
			}
			/*else if (result.containsKey("ETX") && chkETX.equals("true"))
				rtnStr += " ETX: - (mbb/ethernet)\n";*/
			if (result.containsKey("LastCommDate") && chkLastCommDate.equals("true")){
				if(result.get("LastCommDate") != null)
					rtnStr += " Last Metering Communication Time: "+ TimeLocaleUtil.getLocaleDate(result.get("LastCommDate").toString(), lang, country)+"\n";
				else
					rtnStr += " Last Metering Communication Time: - \n";}
			if (result.containsKey("ParentId") && result.get("type").equals(ModemIFType.RF.name()) && chkNodeId.equals("true")){ 
				if(result.get("ParentId") != null)
					rtnStr += " Parent Node EUI Id: " + result.get("ParentId") + "\n";
				else
					rtnStr += " Parent Node EUI Id: -\n";
				}
			/*else if (result.containsKey("ParentId") && chkNodeId.equals("true"))
				rtnStr += " Parent Node EUI Id: - (mbb/ethernet) \n";*/
			
			// IPv6
			if (result.containsKey("CPU6") && chkCPU.equals("true"))
				rtnStr += " CPU Usage: " + result.get("CPU6") + "%\n";
			if (result.containsKey("Memory6") && chkMemory.equals("true"))
				rtnStr += " Memory Usage: " + result.get("Memory6") + "%\n";
			if (result.containsKey("TxSize6") && chkTxSize.equals("true"))
				rtnStr += " Total TX Size: " + df.format(DecimalUtil.ConvertNumberToInteger(result.get("TxSize6"))) + " Byte\n";
			if (result.containsKey("RSSI6") && (result.get("type").equals(ModemIFType.RF.name()) || result.get("type").equals(ModemIFType.Ethernet.name())) && chkRSSI.equals("true"))
				rtnStr += " RSSI: " + result.get("RSSI6")+"\n";
			/*else if (result.containsKey("RSSI6") && chkRSSI.equals("true"))
				rtnStr += " RSSI: - (mbb/ethernet)\n";*/
			if (result.containsKey("LQI6") && result.get("type").equals(ModemIFType.RF.name()) && chkLQI.equals("true"))
				rtnStr += " LQI: " + result.get("LQI6")+"\n";
			/*else if (result.containsKey("LQI6") && chkLQI.equals("true"))
				rtnStr += " LQI: - (mbb/ethernet)\n";*/
			if (result.containsKey("ETX6") && result.get("type").equals(ModemIFType.RF.name()) && chkETX.equals("true")){
				if(Integer.parseInt(result.get("ETX6").toString()) >= 100)
					rtnStr += " ETX: " + "0x"+result.get("ETX6")+"\n";
				else
					rtnStr += "ETX: " +result.get("ETX6") + "\n";
			}
			/*else if (result.containsKey("ETX6") && chkETX.equals("true"))
				rtnStr += " ETX: - (mbb/ethernet)\n";*/
			if (result.containsKey("LastCommDate6") && chkLastCommDate.equals("true")){
				if(result.get("LastCommDate6") != null)
					rtnStr += " Last Metering Communication Time: "+ TimeLocaleUtil.getLocaleDate(result.get("LastCommDate6").toString(), lang, country)+"\n";
				else
					rtnStr += " Last Metering Communication Time: - \n";}
			if (result.containsKey("ParentId6") && result.get("type").equals(ModemIFType.RF.name()) && chkNodeId.equals("true")){
				if(result.get("ParentId6") != null)
					rtnStr += " Parent Node EUI Id: " + result.get("ParentId6") + "\n";
				else
					rtnStr += " Parent Node EUI Id: -\n"; 
				}
			/*else if (result.containsKey("ParentId6") && chkNodeId.equals("true")) 
				rtnStr += " Parent Node EUI Id: - (mbb/ethernet) \n";*/

			Modem modems = modemManager.getModem(Integer.parseInt(target));
			if (result.containsKey("TxSize")) 
				modems.setRfPower(Long.parseLong(result.get("TxSize").toString()));
				// modems.setRfPower(Integer.parseInt(result.get("TxSize").toString()));
			if (result.containsKey("TxSize6"))
				modems.setRfPower(Long.parseLong(result.get("TxSize6").toString()));
				// modems.setRfPower(Integer.parseInt(result.get("TxSize6").toString()));

			modems.setModemStatus(modemStatusNormal); // 성공하면 'Normal'로 DB에 저장
			modems.setLastLinkTime(lastCommDate); // 마지막통신날짜를 현재시간으로 DB에 저장
			
			if (result.containsKey("ParentId") && result.get("type").equals(ModemIFType.RF.name()) && chkNodeId.equals("true")) {
				Modem chkParentId = modemManager.getModem(result.get("ParentId").toString());
				if(chkParentId!=null)
					modems.setParentModemId(modemManager.getModem(result.get("ParentId").toString()).getId());
			}
			if (result.containsKey("ParentId6") && result.get("type").equals(ModemIFType.RF.name()) && chkNodeId.equals("true")) {
				Modem chkParentId6 = modemManager.getModem(result.get("ParentId6").toString());
				if(chkParentId6!=null)
					modems.setParentModemId(modemManager.getModem(result.get("ParentId6").toString()).getId());
			}
			if (modem.getModemType() == ModemType.SubGiga){
				SubGiga subGigaModem = (SubGiga) modems;
				if (result.containsKey("RSSI")&& result.get("type").equals(ModemIFType.RF.name()) && chkRSSI.equals("true")) 
					subGigaModem.setRssi(Integer.parseInt(result.get("RSSI").toString()));
				if (result.containsKey("RSSI6")&& result.get("type").equals(ModemIFType.RF.name()) && chkRSSI.equals("true"))
					subGigaModem.setRssi(Integer.parseInt(result.get("RSSI6").toString()));
				if (result.containsKey("LQI")&& result.get("type").equals(ModemIFType.RF.name()) && chkLQI.equals("true")) 
					subGigaModem.setLqi(Integer.parseInt(result.get("LQI").toString()));
				if (result.containsKey("LQI6")&& result.get("type").equals(ModemIFType.RF.name()) && chkLQI.equals("true"))
					subGigaModem.setLqi(Integer.parseInt(result.get("LQI6").toString()));
				if (result.containsKey("ETX")&& result.get("type").equals(ModemIFType.RF.name()) && chkETX.equals("true")) 
					subGigaModem.setEtx(Integer.parseInt(result.get("ETX").toString()));
				if (result.containsKey("ETX6")&& result.get("type").equals(ModemIFType.RF.name()) && chkETX.equals("true"))
					subGigaModem.setEtx(Integer.parseInt(result.get("ETX6").toString()));

				modemManager.updateModemSubGiga(subGigaModem);
			}			
			if (result.containsKey("CPU")&& chkCPU.equals("true")) 
				modems.setCpuUsage(Integer.parseInt(result.get("CPU").toString()));
			if (result.containsKey("CPU6")&& chkCPU.equals("true"))
				modems.setCpuUsage(Integer.parseInt(result.get("CPU6").toString()));
			if (result.containsKey("Memory") && chkMemory.equals("true")) 
				modems.setMemoryUsage(Integer.parseInt(result.get("Memory").toString()));
			if (result.containsKey("Memory6") && chkMemory.equals("true"))
				modems.setMemoryUsage(Integer.parseInt(result.get("Memory6").toString()));

			modemManager.updateModem(modems); // 모뎀 상태 변경
			status = ResultStatus.SUCCESS;
		} catch (NullPointerException e) {
			rtnStr = " Fail to get modem information, check your network connection ";
			Modem modemf = modemManager.getModem(Integer.parseInt(target));
			modemf.setModemStatus(modemStatusFail); // 실패하면 'CommError'로 DB에 저장
			modemManager.updateModem(modemf); // 모뎀 상태 변경
		} catch (Exception e) {
			rtnStr = e.toString();
			log.error(e, e);
			status = ResultStatus.FAIL;
			Modem modemf = modemManager.getModem(Integer.parseInt(target));
			modemf.setModemStatus(modemStatusFail); // 실패하면 'CommError'로 DB에 저장
			modemManager.updateModem(modemf); // 모뎀 상태 변경
		}
		
		// Operation Log
		Code operationCode = codeManager.getCodeByCode("8.2.8"); // Modem Information
		Code targetTypeCode = codeManager.getCodeByCode("1.2"); // Modem
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, targetTypeCode, modem.getDeviceSerial(),
					loginId, operationCode, status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		
		
		mav.addObject("status", status);
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}
	
	
	/**
	 * NMS에서 사용하는 GetInfo (CoAP) 
	 **/
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdNMSCoAP")
	public ModelAndView cmdNMSCoAP(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "device", required = false) String device,
			@RequestParam(value = "chkRSSI", required = false) String chkRSSI, 
			@RequestParam(value = "chkLQI", required = false) String chkLQI,
			@RequestParam(value = "chkETX", required = false) String chkETX,
			@RequestParam(value = "chkLastCommDate", required = false) String chkLastCommDate,
			@RequestParam(value = "chkCPU", required = false) String chkCPU,
			@RequestParam(value = "chkMemory", required = false) String chkMemory,
			@RequestParam(value = "chkTxSize", required = false) String chkTxSize) throws ParseException {
		log.debug("[cmdNMSCoAP] " + " target: " +  target);
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Code modemStatusNormal = codeDao.findByCondition("code", "1.2.7.3"); // Normal
		Code modemStatusFail = codeDao.findByCondition("code", "1.2.7.6");   // CommError

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.8")) {   // Modem Information
			mav.addObject("rtnStr", "No permission");
			log.info("No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			log.info("Target ID null!");
			return mav;
		}

		Modem modem = modemManager.getModem(Integer.parseInt(target));
		
		Map result = new HashMap<String, String>();
		String rtnStr = "";
		// 현재 날짜 구하기
		String currentDay = TimeUtil.getCurrentDay();
		String currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
		String lastCommDate = currentDay + currentTime;
		Supplier supplier = modem.getSupplier();
		
		DecimalFormat df = DecimalUtil.getIntegerDecimalFormat(supplier.getMd());
		
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		// Date Format을 위한 설정
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		
		try {
			// MBB(SMS)
	        if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){   
	            try{ 
	                Map<String, String> asyncResult;
	                asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.COAP.getTypeCode(), "coapGetInfo", null);
	             
	                if(asyncResult != null)
	                	result =  asyncResult;
	                else{
	                    log.debug("SMS Fail");
	                    rtnStr = "Check the results in Async History tab\n";
						throw new Exception(rtnStr);
	                }
	             
	            }catch(Exception e){
	            	e.printStackTrace();
	            	log.debug("SMS Fail");
	            	rtnStr = "Check the results in Async History tab";
					throw new Exception(rtnStr);
	            }
	        }else // RF or Ethernet
				result = cmdOperationUtil.coapGetInfo(modem,"","");

			rtnStr = ""; 
			// IPv4
			if (result.containsKey("RSSI") && (result.get("type").equals(ModemIFType.RF.name()) || result.get("type").equals(ModemIFType.Ethernet.name())) && chkRSSI.equals("true")) 
				rtnStr += "<b>RSSI : </b>" + result.get("RSSI");
			/*else if (result.containsKey("RSSI") && chkRSSI.equals("true"))
				rtnStr += "<b>RSSI : </b>- (mbb/ethernet)";*/
			if (result.containsKey("LQI") && result.get("type").equals(ModemIFType.RF.name()) && chkLQI.equals("true"))
				rtnStr += "<br/> <b>LQI : </b>" + result.get("LQI");
			/*else if (result.containsKey("LQI") && chkLQI.equals("true"))
				rtnStr += "<br/> <b>LQI : </b>- (mbb/ethernet)";*/
			if (result.containsKey("ETX") && result.get("type").equals(ModemIFType.RF.name()) && chkETX.equals("true")){
				if(Integer.parseInt(result.get("ETX").toString()) >= 100)
					rtnStr += "<br/>  <b>ETX : </b>" + "0x"+result.get("ETX");
				else
					rtnStr += "<br/>  <b>ETX : </b>" +result.get("ETX");
			}
			/*else if (result.containsKey("ETX") && chkETX.equals("true"))
				rtnStr += "<br/>  <b>ETX : </b>- (mbb/ethernet)";*/
			if (result.containsKey("LastCommDate") && chkLastCommDate.equals("true"))
				rtnStr += "<br/>  <b>Last Metering Communication Time : </b>" + TimeLocaleUtil.getLocaleDate(result.get("LastCommDate").toString(), lang, country);
			if (result.containsKey("CPU") && chkCPU.equals("true"))
				rtnStr += "<br/>  <b>CPU Usage : </b>" + result.get("CPU") + "%";
			if (result.containsKey("Memory") && chkMemory.equals("true"))
				rtnStr += "<br/>  <b>Memory Usage : </b>" + result.get("Memory") + "%";
			if (result.containsKey("TxSize") && chkTxSize.equals("true"))
				rtnStr += "<br/>  <b>Total TX Size : </b>" + df.format(DecimalUtil.ConvertNumberToInteger(result.get("TxSize"))) + " Byte";

			// IPv6
			if (result.containsKey("RSSI6") && (result.get("type").equals(ModemIFType.RF.name()) || result.get("type").equals(ModemIFType.Ethernet.name())) && chkRSSI.equals("true"))
				rtnStr += "<b>(IPv6)</b><br/><b>RSSI : </b>" + result.get("RSSI6");
			/*else if (result.containsKey("RSSI6") && chkRSSI.equals("true"))
				rtnStr += "<b>(IPv6)</b><br/><b>RSSI : </b> - (mbb/ethernet)";*/
			if (result.containsKey("LQI6") && result.get("type").equals(ModemIFType.RF.name()) && chkLQI.equals("true"))
				rtnStr += "<br/> <b>LQI : </b>" + result.get("LQI6");
			/*else if (result.containsKey("LQI6") && chkLQI.equals("true"))
				rtnStr += "<br/> <b>LQI : </b> - (mbb/ethernet)";*/
			if (result.containsKey("ETX6") && result.get("type").equals(ModemIFType.RF.name()) && chkETX.equals("true")){
				if(Integer.parseInt(result.get("ETX6").toString()) >= 100)
					rtnStr += "<br/>  <b>ETX : </b>" + "0x"+result.get("ETX6");
				else
					rtnStr += "<br/>  <b>ETX : </b>" +result.get("ETX6");
			}
			/*else if (result.containsKey("ETX6") && chkETX.equals("true"))
				rtnStr += "<br/>  <b>ETX : </b>- (mbb/ethernet)";*/
			if (result.containsKey("LastCommDate6") && chkLastCommDate.equals("true"))
				rtnStr += "<br/>  <b>Last Metering Communication Time : </b>" + TimeLocaleUtil.getLocaleDate(result.get("LastCommDate6").toString(), lang, country);
			if (result.containsKey("CPU6") && chkCPU.equals("true"))
				rtnStr += "<br/>  <b>CPU Usage : </b>" + result.get("CPU6") + "%";
			if (result.containsKey("Memory6") && chkMemory.equals("true"))
				rtnStr += "<br/>  <b>Memory Usage : </b>" + result.get("Memory6") + "%";
			if (result.containsKey("TxSize6") && chkTxSize.equals("true"))
				rtnStr += "<br/>  <b>Total TX Size : </b>" + df.format(DecimalUtil.ConvertNumberToInteger(result.get("TxSize6"))) + " Byte";

			Modem modems = modemManager.getModem(Integer.parseInt(target));
			
			if (result.containsKey("TxSize"))
				modems.setRfPower(Long.parseLong(result.get("TxSize").toString()));
				// modems.setRfPower(Integer.parseInt(result.get("TxSize").toString()));
			if (result.containsKey("TxSize6"))
				modems.setRfPower(Long.parseLong(result.get("TxSize6").toString()));
				// modems.setRfPower(Integer.parseInt(result.get("TxSize6").toString()));

			modems.setModemStatus(modemStatusNormal); // 성공하면 'Normal'로 DB에 저장
			modems.setLastLinkTime(lastCommDate); // 마지막통신날짜를 현재시간으로 DB에 저장
			
			if (modem.getModemType() == ModemType.SubGiga){
				SubGiga subGigaModem = (SubGiga) modems;
				if (result.containsKey("RSSI")&& result.get("type").equals(ModemIFType.RF.name()) && chkRSSI.equals("true")) 
					subGigaModem.setRssi(Integer.parseInt(result.get("RSSI").toString()));
				if (result.containsKey("RSSI6")&& result.get("type").equals(ModemIFType.RF.name()) && chkRSSI.equals("true"))
					subGigaModem.setRssi(Integer.parseInt(result.get("RSSI6").toString()));
				if (result.containsKey("LQI")&& result.get("type").equals(ModemIFType.RF.name()) && chkLQI.equals("true")) 
					subGigaModem.setLqi(Integer.parseInt(result.get("LQI").toString()));
				if (result.containsKey("LQI6")&& result.get("type").equals(ModemIFType.RF.name()) && chkLQI.equals("true"))
					subGigaModem.setLqi(Integer.parseInt(result.get("LQI6").toString()));
				if (result.containsKey("ETX")&& result.get("type").equals(ModemIFType.RF.name()) && chkETX.equals("true")) 
					subGigaModem.setEtx(Integer.parseInt(result.get("ETX").toString()));
				if (result.containsKey("ETX6")&& result.get("type").equals(ModemIFType.RF.name()) && chkETX.equals("true"))
					subGigaModem.setEtx(Integer.parseInt(result.get("ETX6").toString()));

				modemManager.updateModemSubGiga(subGigaModem);
			}			
			
			if (result.containsKey("CPU")&& chkCPU.equals("true")) 
				modems.setCpuUsage(Integer.parseInt(result.get("CPU").toString()));
			if (result.containsKey("CPU6")&& chkCPU.equals("true"))
				modems.setCpuUsage(Integer.parseInt(result.get("CPU6").toString()));
			if (result.containsKey("Memory") && chkMemory.equals("true")) 
				modems.setMemoryUsage(Integer.parseInt(result.get("Memory").toString()));
			if (result.containsKey("Memory6") && chkMemory.equals("true"))
				modems.setMemoryUsage(Integer.parseInt(result.get("Memory6").toString()));
			
			modemManager.updateModem(modems); // 모뎀 상태 변경
			status = ResultStatus.SUCCESS;
		} catch (NullPointerException e) {
			rtnStr = " Fail to get modem information, check your network connection ";
			Modem modemf = modemManager.getModem(Integer.parseInt(target));
			modemf.setModemStatus(modemStatusFail); // 실패하면 'CommError'로 DB에 저장
			modemManager.updateModem(modemf); // 모뎀 상태 변경
			status = ResultStatus.FAIL;
		} catch (Exception e) {
			rtnStr = e.toString();
			log.error(e, e);
			Modem modemf = modemManager.getModem(Integer.parseInt(target));
			modemf.setModemStatus(modemStatusFail); // 실패하면 'CommError'로 DB에 저장
			modemManager.updateModem(modemf); // 모뎀 상태 변경
			status = ResultStatus.FAIL;
		}

		mav.addObject("status", status);
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}
	
	/**
	 * resetModem (CoAP)
	 **/
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdResetModem")
	public ModelAndView cmdResetModem(
			@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) throws ParseException {
		log.debug("[cmdResetModem] " + " target: " +  target);
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Code modemStatusNormal = codeDao.findByCondition("code", "1.2.7.3"); // Normal
		Code modemStatusFail = codeDao.findByCondition("code", "1.2.7.6"); // CommError
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.11")) {
			mav.addObject("rtnStr", "No permission");
			log.info("No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			log.info("Target ID null!");
			return mav;
		}

		Modem modem = modemManager.getModem(Integer.parseInt(target));
		Map result = new HashMap<String, String>();
		String rtnStr = "";
		// 현재 날짜 구하기
		String currentDay = TimeUtil.getCurrentDay();
		String currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
		String lastCommDate = currentDay + currentTime;
		Supplier supplier = modem.getSupplier();

		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
	
		try {
			// MBB(SMS)
	        if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){   
	            try{ 
	                Map<String, String> asyncResult;
	                asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.COAP.getTypeCode(), "modemReset", null);
	             
	                if(asyncResult != null)
	                	result =  asyncResult;
	                else{
	                    log.debug("SMS Fail");
	                    rtnStr = "Check the results in Async History tab\n";
						throw new Exception(rtnStr);
	                }
	             
	            }catch(Exception e){
	            	e.printStackTrace();
	            	log.debug("SMS Fail");
                    rtnStr = "Check the results in Async History tab\n";
					throw new Exception(rtnStr);
	            }
			}else // RF or Ethernet
				result = cmdOperationUtil.ModemReset(modem,"","");
	        
			rtnStr = ""; // 출력 메세지
			
			Modem modems = modemManager.getModem(Integer.parseInt(target));
			modems.setModemStatus(modemStatusNormal); // 성공하면 'Normal'로 DB에 저장
			modems.setLastLinkTime(lastCommDate); // 마지막통신날짜를 현재시간으로 DB에 저장

			modemManager.updateModem(modems); // 모뎀 상태 변경
			status = ResultStatus.SUCCESS;
		} catch (NullPointerException e) {
			rtnStr = " Fail to send a reset command, check your network connection ";
			Modem modemf = modemManager.getModem(Integer.parseInt(target));
			modemf.setModemStatus(modemStatusFail); // 실패하면 'CommError'로 DB에 저장
			modemManager.updateModem(modemf); // 모뎀 상태 변경
		} catch (Exception e) {
			rtnStr = e.toString();
			log.error(e, e);
			status = ResultStatus.FAIL;
			Modem modemf = modemManager.getModem(Integer.parseInt(target));
			modemf.setModemStatus(modemStatusFail); // 실패하면 'CommError'로 DB에 저장
			modemManager.updateModem(modemf); // 모뎀 상태 변경
		}
		
		// Operation Log
		Code operationCode = codeManager.getCodeByCode("8.2.11"); // Modem Reset(COAP)
		Code targetTypeCode = codeManager.getCodeByCode("1.2"); // Modem
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, targetTypeCode, modem.getDeviceSerial(),
					loginId, operationCode, status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		
		mav.addObject("status", status);
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}
	
	/**
	 * coapBrowser  For Modem
	 **/
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/coapBrowser")
	public ModelAndView coapBrowser(
			@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "uri", required = false) String uri,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "config", required = false) String config
			) throws ParseException {
		log.debug("[coapBrowser] " + " target: " +  target);
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.8")) {
			mav.addObject("rtnStr", "No permission");
			log.info("No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			log.info("Target ID null!");
			return mav;
		}

		Modem modem = modemManager.getModem(Integer.parseInt(target));
		Map result = new HashMap<String, String>();
		String rtnStr = "";
	
		try {
				// MBB (SMS)
		    if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){   
		         try{ 
		        	 	Map<String, String> asyncResult;
		                Map<String, String> paramMap = new HashMap<String, String>();
		                paramMap.put("uri", uri);
						paramMap.put("query", query);
						paramMap.put("config", config);
		                asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.COAP.getTypeCode(), "coapBrowser", paramMap);
		                if(asyncResult != null){
		                	rtnStr = asyncResult.get("result").toString();
		                	if(config.equals("GET")){
			                	String detail="\n\n\n#Decoding Data\n";
			            		try{
			            			detail += CoapBrowserDecoder.decode(uri,rtnStr);
			            		}catch(Exception e){
			            			detail += "Please refer to 'Command List'.";
			            		}
			            		rtnStr = "#Raw Data\n" + rtnStr + detail;
		                	}
		                }else{
		                	log.debug("SMS Fail");
		                    rtnStr = "Check the results in Async History tab\n";
							throw new Exception(rtnStr);
		                }
		            }catch(Exception e){
		            	e.printStackTrace();
		            	log.debug("SMS Fail");
	                    rtnStr = "Check the results in Async History tab";
						throw new Exception(rtnStr);
		            }
			}else{ // RF or Ethernet
					result = cmdOperationUtil.coapBrowser(modem, "", "", uri, query, config);
					rtnStr = ""; // 출력 메세지
					rtnStr = result.get("result").toString();
					status = ResultStatus.SUCCESS;
						if(config.equals("GET")){
						String detail="\n\n\n#Decoding Data\n";
						try{
							detail += CoapBrowserDecoder.decode(uri,rtnStr);
						}catch(Exception e){
							detail += "Please refer to 'Command List'";
						}
						rtnStr = "#Raw Data\n" + rtnStr + detail;
					}
				}
		} catch (NullPointerException e) {
			rtnStr = "FAIL\n";
			status = ResultStatus.FAIL;
		} catch (Exception e) {
			rtnStr = e.toString();
			log.error(e, e);
			status = ResultStatus.FAIL;
		}
		
		mav.addObject("status", status);
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}
	
	/**
	 * coapBrowser For DCU
	 **/
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/coapBrowserForDCU")
	public ModelAndView coapBrowserForDCU(
			@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "uri", required = false) String uri,
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "config", required = false) String config
			) throws ParseException {
		log.debug("[coapBrowserForDCU] " + " target: " +  target);
		MCU dcu = mcuManager.getMCU(Integer.parseInt(target));
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.8")) {
			mav.addObject("rtnStr", "No permission");
			log.info("No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			log.info("Target ID null!");
			return mav;
		}
		Map result = new HashMap<String, String>();
		String rtnStr = "";
		try {
			result = cmdOperationUtil.coapBrowserForDCU(dcu, uri, query, config);
			rtnStr = ""; // 출력 메세지
			rtnStr = result.get("result").toString();
			status = ResultStatus.SUCCESS;
			
			if(config.equals("GET")){
				String detail="\n\n\n#Decoding Data\n";
				try{
					detail += CoapBrowserDecoder.decode(uri,rtnStr);
				}catch(Exception e){
					detail += "Please refer to 'Command List'.";
				}
				rtnStr = "#Raw Data\n" + rtnStr + detail;
			}
		} catch (NullPointerException e) {
			rtnStr = " Fail to send a reset command, check your network connection ";
			status = ResultStatus.FAIL;
		} catch (Exception e) {
			rtnStr = e.toString();
			log.error(e, e);
			status = ResultStatus.FAIL;
		}
		mav.addObject("status", status);
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdEventTime")
	public ModelAndView cmdEventTime(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "device", required = false) String device) throws ParseException {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		String rtnStr;

		try {
			MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
			Supplier supplier = mcu.getSupplier();
			Integer supplierID = supplier.getId();
			String timeForEventLocale = getMcuEventTime(mcu, supplierID);

			rtnStr = "<b>Last Event Time: </b>" + timeForEventLocale;
			status = ResultStatus.SUCCESS;

		} catch (Exception e) {
			e.printStackTrace();
			rtnStr = "<b>Last Event Time: </b>" + "-";
			status = ResultStatus.FAIL;
		}

		mav.addObject("status", status);
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/getSignalQuality")
	public ModelAndView getSignalQuality (
			@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) throws ParseException {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String,Object>();
		
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.3.5")){
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		// IF4 
		// CSQ Value - oid = 2.7.12
		try {
			result = cmdOperationUtil.cmdMcuStdGet(mcu.getSysID(), "2.7.12");
			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
			rtnStr = e.getMessage();
			log.error(e, e);
		}
		
		if(result.containsKey("cmdResult")){
			rtnStr = result.get("cmdResult").toString();
			mav.addObject("csq", result.get("2.7.12").toString() == null ? "" : result.get("2.7.12").toString());
		}
		
		mav.addObject("status", status);
		mav.addObject("rtnStr", rtnStr);
		
		return mav;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/getNMSInformation")
	public ModelAndView getNMSInformation (
			@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) throws ParseException {

		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String,Object>();
		
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.3.10")){
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		
		if (target == null || "".equals(target)) {
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		Code mcuStatusNormal = codeDao.findByCondition("code", "1.1.4.1");
		Code mcuStatusFail = codeDao.findByCondition("code", "1.1.4.5");
		String currentDay = "";
		String currentTime = "";
		String lastCommDate = "";
		
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		// IF4 
		try {
			result = cmdOperationUtil.cmdGetMcuNMSInformation(mcu.getSysID());
		} catch (Exception e) {
			rtnStr = e.getMessage();
			log.error(e, e);
			
			mcu.setMcuStatus(mcuStatusFail);
			mcuManager.updateMCU(mcu);
			
			mav.addObject("rtnStr", rtnStr);
			return mav;
		}
		
		if (result.containsKey("cmdResult")) {
			rtnStr = result.get("cmdResult").toString();
			if (rtnStr.contains("FAIL")) {
				mcu.setMcuStatus(mcuStatusFail);
				mcuManager.updateMCU(mcu);
			} else {
				currentDay = TimeUtil.getCurrentDay();
				currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
				lastCommDate = currentDay + currentTime;

				mcu.setMcuStatus(mcuStatusNormal);
				mcu.setLastCommDate(lastCommDate);
				mcuManager.updateMCU(mcu);
			}
		}
		
		mav.addObject("rtnStr", rtnStr);
		
		return mav;
	}
	
	public String getMcuEventTime(MCU mcu, int supplierID) {
		HashMap<String, Object> condition = new HashMap<String, Object>();
		String activator_type = "MCU";
		String activatorId = mcu.getSysID();
		String timeForEvent;
		String timeForEventLocale;

		condition.put("activator_type", activator_type);
		condition.put("activatorId", activatorId);

		List McuLogType_Result = eventAlertLogDao.getMcuLogType(condition);
		timeForEvent = (String) McuLogType_Result.get(0);

		Supplier supplier = supplierDao.get(supplierID);
		timeForEventLocale = TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(timeForEvent),
				supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());

		return timeForEventLocale;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdCountHops")
	public ModelAndView cmdCountHops(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "device", required = false) String device) throws ParseException {

		ModelAndView mav = new ModelAndView("jsonView");

		int countHops;	// MCU와 Modem 사이의 default Hop
		
		try {
			countHops = 1;
			Meter meter = meterManager.getMeter(Integer.parseInt(target));
			String mdsID = meter.getMdsId();
			Supplier supplier = meter.getSupplier();
			Integer supplierID = supplier.getId();
			
			Modem modem = meter.getModem();
			Modem parentModem = modem.getModem();
			
			if(parentModem != null) {
				countHops++;
			}
		} catch (Exception e) {
			e.printStackTrace();
			countHops = 0;
		}

		mav.addObject("rtnStr", countHops);
		return mav;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdCoordinate")
	public ModelAndView cmdCoordinate(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "device", required = false) String device) throws ParseException {
		ModelAndView mav = new ModelAndView("jsonView");

		String gpioX = null;
		String gpioY = null;
		String gpioZ = null;
		
		if(device.equals("MCU")){
			MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
			gpioX = mcu.getGpioX().toString();
			gpioY = mcu.getGpioY().toString();
			gpioZ = mcu.getGpioZ().toString();
		} else if(device.equals("Modem")){
			Modem modem = modemManager.getModem(Integer.parseInt(target));
			gpioX = modem.getGpioX().toString();
			gpioY = modem.getGpioY().toString();
			gpioZ = modem.getGpioZ().toString();
		} else {	// Meter
			Meter meter = meterManager.getMeter(Integer.parseInt(target));
			gpioX = meter.getGpioX().toString();
			gpioY = meter.getGpioY().toString();
			gpioZ = meter.getGpioZ().toString();
		}
		
		mav.addObject("gpioX", gpioX);
		mav.addObject("gpioY", gpioY);
		mav.addObject("gpioZ", gpioZ);
		return mav;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdDevieceStatus")
	public ModelAndView cmdDevieceStatus(
			@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "device", required = false) String device) throws ParseException {
		
		ModelAndView mav = new ModelAndView("jsonView");
		String deviceStatus = "";
		Code code = null;
		
		if(device.equals("MCU")){
			MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
			
			if (mcu.getMcuStatus() == null) {
				deviceStatus = "Unkown";
			} else {
				code = mcu.getMcuStatus();
				deviceStatus = code.getName();
			}
		} else if(device.equals("Modem")){
			Modem modem = modemManager.getModem(Integer.parseInt(target));
			
			if (modem.getModemStatus() == null) {
				deviceStatus = "Unkown";
			} else {
				code = modem.getModemStatus();
				deviceStatus = code.getName();
			}
		} else {	// Meter
			Meter meter = meterManager.getMeter(Integer.parseInt(target));
			
			if (meter.getMeterStatus() == null) {
				deviceStatus = "Unkown";
			} else {
				code = meter.getMeterStatus();
				deviceStatus = code.getName();
			}
		}
		
		mav.addObject("deviceStatus", deviceStatus);
		return mav;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdCountMetersRelativeMcu")
	public ModelAndView cmdCountMetersRelativeMcu(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "device", required = false) String device) throws ParseException {

		ModelAndView mav = new ModelAndView("jsonView");

		// 선택된 mcu에 물려있는 meter의 수를 저장하는 변수
		int countMCUWithRelativeMeter = 0;

		try {
			MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
			List<Modem> modems = (List<Modem>) modemDao.getModemHavingMCU(mcu.getId());

			// modems.size : mcu에 물려있는 modem의 개수
			for (int i = 0; i < modems.size(); i++) {
				Modem modem = modems.get(i);

				List<Meter> meters = (List<Meter>) meterDao.getMeterHavingModem(modem.getId());

				// meters.size : modem에 물려있는 meter의 개수
				for (int j = 0; j < meters.size(); j++) {
					// Meter meter = meters.get(j);
					countMCUWithRelativeMeter++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			countMCUWithRelativeMeter = 0;
		}

		mav.addObject("rtnStr", countMCUWithRelativeMeter);
		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdModemTraceroute")
	public ModelAndView cmdModemTraceroute(
			@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "hopCount", required = false) String hopCount) {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.2")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		String cmdResult = "";
		StringBuffer jsonString = new StringBuffer("");

		// MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Modem modem = modemManager.getModem(Integer.parseInt(target));
		Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		try {
			// traceroute 결과, 변수 cmdResult에 대입
			cmdResult = cmdOperationUtil.doModemTraceroute(modem, hopCount);
			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.toString();
			log.error(e, e);
		}
		
		
		Code operationCode = codeManager.getCodeByCode("8.2.2"); // Modem TraceRoute
		Code targetTypeCode = codeManager.getCodeByCode("1.2"); // Modem
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, targetTypeCode, modem.getDeviceSerial(),
					loginId, operationCode, status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}

		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		mav.addObject("jsonString", cmdResult);
		return mav;
	}

	/**
	 * Modem Type이 MMIU이고 Kamstrup 미터에 내장타입인 모뎀에 대한 모뎀 설정 명령
	 * 
	 * @param target
	 *            - 모뎀 아이디
	 * @param loginId
	 * @param param
	 *            - 파라미터(명령 코드 및 인자)
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdGPRSModem")
	public ModelAndView cmdGPRSModem(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "param", required = false) String param) {

		Operator operator = operatorManager.getOperatorByLoginId(loginId);

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.3.2")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		Modem modem = modemManager.getModem(target);
		Meter meter = modem.getMeter().iterator().next();
		Supplier supplier = meter.getSupplier();
		if (supplier == null) {
			supplier = operator.getSupplier();
		}
		try {
			rtnStr = MapToJSON(cmdOperationUtil.doOnDemand(meter, 0, "admin", "", "", ""));
			status = ResultStatus.SUCCESS;

		} catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.getMessage();
		}

		Code operationCode = codeManager.getCodeByCode("8.3.2");
		Code mcuType = null;
		if (modem.getModemType().equals(ModemType.MMIU)) {
			mcuType = codeManager.getCodeByName(McuType.MMIU.name());
		}

		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcuType, modem.getDeviceSerial(), loginId, operationCode,
					status.getCode(), rtnStr);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	public String makeHTML(Map table) {
        StringBuffer html = new StringBuffer();
        html.append("<html>");
        html.append("<form name='f' method='post' style='display:none'>");
        html.append("<textarea name='excelData'></textarea></form>");
        html.append("<link href=/aimir-web/css/style.css rel=stylesheet type=text/css>");
        html.append("<ul><span style='float:right'><li><em class='am_button' style='margin-right: 50px;'><a href='javascript:openExcelReport2();'>excel</a></em></li></span></ul>");
        html.append("<table  border=1 cellpadding=0 cellspacing=0 bordercolor=#FFFFFF border=1 width=100% id=ondemandTable>");
        html.append("<caption style='text-align: center;font-size: 20px;'><b>Ondemand Result</b></caption>");
        html.append("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");

		Iterator keys = table.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String val = (String) table.get(key);
            html.append("<tr>");
            html.append("<td height='auto' width='50%' align=left style=\"word-break:break-all\"><b>").append(key).append("</b></td>");
            html.append("<td height='auto' align=left style=\"word-break:break-all\">").append(val).append("</td>");
	        html.append("</tr>");
		}

		html.append("</table>");
		html.append("</html>");
		return html.toString();
	}

	public String MapToJSON(Hashtable<String, String> table) throws Exception {
		StringBuffer rStr = new StringBuffer();
		Iterator<String> keys = table.keySet().iterator();
		String keyVal = null;
		rStr.append("[");
		while (keys.hasNext()) {
			keyVal = (String) keys.next();
			rStr.append("{\"name\":\"");
			rStr.append(keyVal);
			rStr.append("\",\"value\":\"");
			rStr.append(table.get(keyVal));
			rStr.append("\"}");
			if (keys.hasNext()) {
				rStr.append(",");
			}
		}
		rStr.append("]");
		return rStr.toString();
	}

	@SuppressWarnings("unchecked")
	public String MapToJSON(String[] array) throws Exception {
		StringBuffer rStr = new StringBuffer();
		rStr.append("[");
		for (int i = 0; array != null && i < array.length; i++) {
			rStr.append("{\"name\":\"");
			rStr.append("key[" + i + "]");
			rStr.append("\",\"value\":\"");
			rStr.append(array[i]);
			rStr.append("\"}");
		}
		rStr.append("]");
		return rStr.toString();
	}

	@SuppressWarnings("unchecked")
	public String MapToJSON(Map map) throws Exception {
		StringBuffer rStr = new StringBuffer();
		Iterator<String> keys = map.keySet().iterator();
		String keyVal = null;
		rStr.append("[");
		while (keys.hasNext()) {
			keyVal = (String) keys.next();
			rStr.append("{\"name\":\"");
			rStr.append(keyVal);
			rStr.append("\",\"value\":\"");
			rStr.append(map.get(keyVal));
			rStr.append("\"}");
			if (keys.hasNext()) {
				rStr.append(",");
			}
		}
		rStr.append("]");
		return rStr.toString();
	}

	@RequestMapping(value = "/gadget/device/saveSchedule")
	public ModelAndView cmdMCUSaveSchedule(@RequestParam("varMeterDayMask") String varMeterDayMask,
			@RequestParam("varEventReadDayMask") String varEventReadDayMask,
			@RequestParam("varMeterTimesyncDayMask") String varMeterTimesyncDayMask,
			@RequestParam("varRecoveryDayMask") String varRecoveryDayMask,

			@RequestParam("varMeterHourMask") String varMeterHourMask,
			@RequestParam("varEventReadHourMask") String varEventReadHourMask,
			@RequestParam("varMeterTimesyncHourMask") String varMeterTimesyncHourMask,
			@RequestParam("varMeterUploadCycle") String varMeterUploadCycle,
			@RequestParam("varRecoveryHourMask") String varRecoveryHourMask,

			@RequestParam("varMeterStartMin") String varMeterStartMin,
			@RequestParam("varMeteringPeriod") String varMeteringPeriod,
			@RequestParam("varMeteringRetry") String varMeteringRetry,

			@RequestParam("varEnableReadMeterEvent") String varEnableReadMeterEvent,

			@RequestParam("varEnableMeterTimesync") String varEnableMeterTimesync,

			@RequestParam("varEnableAutoUpload") String varEnableAutoUpload,
			@RequestParam("varMeterUploadCycleType") String varMeterUploadCycleType,
			@RequestParam("varMeterUploadStartHour") String varMeterUploadStartHour,
			@RequestParam("varMeterUploadStartMin") String varMeterUploadStartMin,
			@RequestParam("varMeterUploadTryTime") String varMeterUploadTryTime,

			@RequestParam("varMeterUploadRetry") String varMeterUploadRetry,

			@RequestParam("varEnableRecovery") String varEnableRecovery,
			@RequestParam("varRecoveryStartMin") String varRecoveryStartMin,
			@RequestParam("varRecoveryPeriod") String varRecoveryPeriod,
			@RequestParam("varRecoveryRetry") String varRecoveryRetry, @RequestParam("mcuId") int mcuId,
			@RequestParam(value = "loginId", required = false) String loginId) {

		MCUVar mcuVar = null;
		MCU mcu = mcuManager.getMCU(mcuId);
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		String target = mcuId + "";
		if (mcu.getMcuVar() != null) {
			mcuVar = mcu.getMcuVar();
		} else {
			mcuVar = new MCUVar();
			mcuVar.setVarAlamLogSaveDay(0);
			mcuVar.setVarAutoResetCheckIntegererval(0);
			mcuVar.setVarAutoTimesyncIntegereval(0);
			mcuVar.setVarDataSaveDay(0);
			mcuVar.setVarDefaultGeMeteringOPtion(0);
			mcuVar.setVarEnableAutoRegister(false);
			mcuVar.setVarEnableAutoRegister(false);
			mcuVar.setVarEnableAutoSinkReset(false);
			mcuVar.setVarEnableAutoTimesync(false);
			mcuVar.setVarEnableAutoUpload(false);
			mcuVar.setVarEnableCmdHistLog(false);
			mcuVar.setVarEnableCommLog(false);
			mcuVar.setVarEnableDebugLog(false);
			mcuVar.setVarEnableGarbageCleaning(false);
			mcuVar.setVarEnableGpsTimesync(false);
			mcuVar.setVarEnableHeater(false);
			mcuVar.setVarEnableHighRam(false);
			mcuVar.setVarEnableKeepAlive(false);
			mcuVar.setVarEnableMask(0);
			mcuVar.setVarEnableMemoryCheck(false);
			mcuVar.setVarEnableMeterCheck(false);
			mcuVar.setVarEnableMeterErroeRecovery(false);
			mcuVar.setVarEnableMeterTimesync(false);
			mcuVar.setVarEnableMobileStaticLog(false);
			mcuVar.setVarEnableMonitoring(false);
			mcuVar.setVarEnableReadMeterEvent(false);
			mcuVar.setVarEnableRecovery(false);
			mcuVar.setVarEnableSecurity(false);
			mcuVar.setVarEnableTimeBroadcast(false);
			mcuVar.setVarEventAlertLevel(0);
			mcuVar.setVarEventLogSaveDay(0);
			mcuVar.setVarEventReadDayMask(0);
			mcuVar.setVarEventReadHourMask(0);
			mcuVar.setVarFlashCriticalRate(0);
			mcuVar.setVarHeterOffTemp(0);
			mcuVar.setVarHeterOnTemp(0);
			mcuVar.setVarKeepAlaiveIntegererval(0);
			mcuVar.setVarMaxAlamLogSize(0);
			mcuVar.setVarMaxCmdLogSize(0);
			mcuVar.setVarMaxCommLogSize(0);
			mcuVar.setVarMaxEventLogSize(0);
			mcuVar.setVarMaxMeterLogSize(0);
			mcuVar.setVarMaxMobileLogSize(0);
			mcuVar.setVarMaxUpgradeLogSize(0);
			mcuVar.setVarMaxUploadLogSize(0);
			mcuVar.setVarMemoryCriticalRate(0);
			mcuVar.setVarMeterDayMask(0);
			mcuVar.setVarMeterHourMask(0);
			mcuVar.setVarMeterIssueDate(0);
			mcuVar.setVarMeterStartMin(0);
			mcuVar.setVarMeterStrategy(0);
			mcuVar.setVarMeterTimesyncDayMask(0);
			mcuVar.setVarMeterTimesyncHourMask(0);
			mcuVar.setVarMeterUploadCycle(0);
			mcuVar.setVarMeterUploadCycleType(0);
			mcuVar.setVarMeterUploadRetry(0);
			mcuVar.setVarMeterUploadStartHour(0);
			mcuVar.setVarMeterUploadStartMin(0);
			mcuVar.setVarMeterUploadTryTime(0);
			mcuVar.setVarMeteringPeriod(0);
			mcuVar.setVarMeteringRetry(0);
			mcuVar.setVarMobileLiveCheckIntegererval(0);
			mcuVar.setVarMobileLiveCheckIntegererval(0);
			mcuVar.setVarMobileLogSaveDay(0);
			mcuVar.setVarNapcGroupIntegererval(0);
			mcuVar.setVarNapcRetruClear(0);
			mcuVar.setVarNapcRetry(false);
			mcuVar.setVarNapcRetryHour(0);
			mcuVar.setVarNapcRetryStartSecond(0);
			mcuVar.setVarRecoveryDayMask(0);
			mcuVar.setVarRecoveryHourMask(0);
			mcuVar.setVarRecoveryPeriod(0);
			mcuVar.setVarRecoveryRetry(0);
			mcuVar.setVarRecoveryStartMin(0);
			mcuVar.setVarScanningStrategy(0);
			mcuVar.setVarSendDelay(0);
			mcuVar.setVarSensorCleaningThreshold(0);
			mcuVar.setVarSensorKeepAlive(0);
			mcuVar.setVarSensorLimit(0);
			mcuVar.setVarSensorMeterSaveCount(0);
			mcuVar.setVarSinkAckWaitTime(0);
			mcuVar.setVarSinkLedTurnOffIntegererval(0);
			mcuVar.setVarSinkPollingIntegererval(0);
			mcuVar.setVarSinkResetDelay(0);
			mcuVar.setVarSinkResetIntegererval(0);
			mcuVar.setVarStatusMonitorTime(0);
			mcuVar.setVarSysMeteringThreadCount(0);
			mcuVar.setVarSysPowerOffDelay(0);
			mcuVar.setVarSysTempMonIntegererval(0);
			mcuVar.setVarTimeBoardcastIntegererval(0);
			mcuVar.setVarTimeSyncStrategy(0);
			mcuVar.setVarTimesyncLogSaveDay(0);
			mcuVar.setVarTimesyncLogSize(0);
			mcuVar.setVarTimesyncThreshold(0);
			mcuVar.setVarTransactionSaveDay(0);
			mcuVar.setVarUpgradeLogSaveDay(0);
			mcuVar.setVarUploadLogSaveDay(0);
			mcuVar.setVarValueMask(0);
		}

		mcuVar.setVarMeterDayMask(Integer.parseInt(varMeterDayMask, 2));
		mcuVar.setVarEventReadDayMask(Integer.parseInt(varEventReadDayMask, 2));
		mcuVar.setVarMeterTimesyncDayMask(Integer.parseInt(varMeterTimesyncDayMask, 2));
		mcuVar.setVarRecoveryDayMask(Integer.parseInt(varRecoveryDayMask, 2));

		mcuVar.setVarMeterHourMask(Integer.parseInt(varMeterHourMask, 2));
		mcuVar.setVarEventReadHourMask(Integer.parseInt(varEventReadHourMask, 2));
		mcuVar.setVarMeterTimesyncHourMask(Integer.parseInt(varMeterTimesyncHourMask, 2));
		mcuVar.setVarRecoveryHourMask(Integer.parseInt(varRecoveryHourMask, 2));

		mcuVar.setVarMeterStartMin(spaceToZero(varMeterStartMin));
		mcuVar.setVarMeteringPeriod(spaceToZero(varMeteringPeriod));
		mcuVar.setVarMeteringRetry(spaceToZero(varMeteringRetry));

		mcuVar.setVarEnableReadMeterEvent(Boolean.valueOf(varEnableReadMeterEvent));

		mcuVar.setVarEnableMeterTimesync(Boolean.valueOf(varEnableMeterTimesync));

		mcuVar.setVarEnableAutoUpload(Boolean.valueOf(varEnableAutoUpload));
		mcuVar.setVarMeterUploadCycleType(spaceToZero(varMeterUploadCycleType));
		mcuVar.setVarMeterUploadStartHour(spaceToZero(varMeterUploadStartHour));
		mcuVar.setVarMeterUploadStartMin(spaceToZero(varMeterUploadStartMin));
		mcuVar.setVarMeterUploadTryTime(spaceToZero(varMeterUploadTryTime));
		mcuVar.setVarMeterUploadRetry(spaceToZero(varMeterUploadRetry));

		mcuVar.setVarEnableRecovery(Boolean.valueOf(varEnableRecovery));
		mcuVar.setVarRecoveryStartMin(spaceToZero(varRecoveryStartMin));
		mcuVar.setVarRecoveryPeriod(spaceToZero(varRecoveryPeriod));
		mcuVar.setVarRecoveryRetry(spaceToZero(varRecoveryRetry));

		if ("3".equals(varMeterUploadCycleType)) {
			mcuVar.setVarMeterUploadCycle(Integer.parseInt(varMeterUploadCycle));
		} else if (!"1".equals(varMeterUploadCycleType)) {
			mcuVar.setVarMeterUploadCycle(Integer.parseInt(varMeterUploadCycle, 2));
		}

		Hashtable props = new Hashtable<String, String>();
		// Metering Schedule
		props.put("varMeterDayMask", Integer.parseInt(varMeterDayMask, 2));
		props.put("varMeterHourMask", Integer.parseInt(varMeterHourMask, 2));
		props.put("varMeterStartMin", varMeterStartMin);
		props.put("varMeteringPeriod", varMeteringPeriod);
		props.put("varMeteringRetry", varMeteringRetry);
		// Meter Event Log Reading Schedule
		props.put("varEnableReadMeterEvent", varEnableReadMeterEvent);
		props.put("varEventReadDayMask", Integer.parseInt(varEventReadDayMask, 2));
		props.put("varEventReadHourMask", Integer.parseInt(varEventReadHourMask, 2));
		// Meter Time Sync schedule
		// hashtable.put("VarEnableMeterTimesync", varEnableMeterTimesync);
		props.put("varMeterTimesyncDayMask", Integer.parseInt(varMeterTimesyncDayMask, 2));
		props.put("varMeterTimesyncHourMask", Integer.parseInt(varMeterTimesyncHourMask, 2));
		// Metering Data Upload
		props.put("varEnableAutoUpload", varEnableAutoUpload);
		props.put("varMeterUploadStartHour", varMeterUploadStartHour);
		props.put("varMeterUploadCycleType", varMeterUploadCycleType);
		if ("3".equals(varMeterUploadCycleType)) {
			props.put("varMeterUploadCycle", varMeterUploadCycle);
		} else if (!"1".equals(varMeterUploadCycleType)) {
			props.put("varMeterUploadCycle", Integer.parseInt(varMeterUploadCycle, 2));
		}
		props.put("varMeterUploadStartMin", varMeterUploadStartMin);
		props.put("varUploadTryTime", varMeterUploadTryTime);
		props.put("varMeterUploadRetry", varMeterUploadRetry);
		// Recovery Schedule
		props.put("varEnableRecovery", varEnableRecovery);
		props.put("varRecoveryDayMask", Integer.parseInt(varRecoveryDayMask, 2));
		props.put("varRecoveryHourMask", Integer.parseInt(varRecoveryHourMask, 2));
		props.put("varRecoveryStartMin", varRecoveryStartMin);
		props.put("varRecoveryPeriod", varRecoveryPeriod);
		props.put("varRecoveryRetry", varRecoveryRetry);

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.3.6")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";

		try {

			Hashtable resultTable = cmdOperationUtil.doMCUSaveSchedule(mcu, props);
			if (resultTable != null && resultTable.size() > 0) {
				Iterator<String> keys = resultTable.keySet().iterator();
				String keyVal = null;
				while (keys.hasNext()) {
					keyVal = (String) keys.next();
					mav.addObject(keyVal, resultTable.get(keyVal));
				}
				status = ResultStatus.SUCCESS;

				mcu.setMcuVar(mcuVar);
				mcuManager.updateMCU(mcu);
			} else {
				status = ResultStatus.FAIL;
				log.debug("resultTable Is Null");
			}

		} catch (Exception e) {
			rtnStr = e.toString();
			log.error(e, e);
		}

		Code operationCode = codeManager.getCodeByCode("8.3.6");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}

		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	/**
	 * 
	 * 집중기에서 스케줄 정보를 가져온다.
	 * 
	 * @param mcuId
	 * @param loginId
	 * 
	 */

	@RequestMapping(value = "/gadget/device/command/importSchedule")
	public ModelAndView importSchedule(@RequestParam(value = "mcuId", required = true) String mcuId,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ModelAndView mav = new ModelAndView("jsonView");
		ResultStatus status = ResultStatus.FAIL;

		MCU mcu = mcuManager.getMCU(Integer.parseInt(mcuId));

		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		String rtnStr = null;

		try {
			Map resultTable = cmdOperationUtil.doMCUImportSchedule(mcu);

			String toBinary = null;
			int toBinarySize = 0;

			if (resultTable != null && resultTable.size() > 0) {
				Iterator<String> keys = resultTable.keySet().iterator();
				String keyVal = null;
				while (keys.hasNext()) {
					keyVal = (String) keys.next();
					if (keyVal.contains("Mask")) {
						toBinary = Integer.toBinaryString(Integer.parseInt(resultTable.get(keyVal).toString()));
						toBinarySize = toBinary.length();
						int maxSize = 31;
						if (keyVal.contains("Hour")) {
							maxSize = 24;
						}
						for (int i = toBinarySize; i < maxSize; i++) {
							toBinary = "0" + toBinary;
						}
						mav.addObject(keyVal, toBinary);
					} else {
						if ("varMeterUploadCycle".equals(keyVal)) {

							switch (Integer.parseInt(resultTable.get("varMeterUploadCycleType").toString())) {
							case 1:
								mav.addObject("varMeterUploadCycleHourly", "0000000000000000000000000");
								mav.addObject("varMeterUploadCycleDaily", "00000000000000000000000000000000");
								break;

							case 2:
								toBinary = Integer.toBinaryString(Integer.parseInt(resultTable.get(keyVal).toString()));
								toBinarySize = toBinary.length();
								for (int i = toBinarySize; i < 31; i++) {
									toBinary = "0" + toBinary;
								}
								mav.addObject("varMeterUploadCycleDaily", toBinary);
								mav.addObject("varMeterUploadCycleHourly", "0000000000000000000000000");
								break;

							case 3:
								toBinary = resultTable.get(keyVal).toString();
								mav.addObject("varMeterUploadCycleWeekly", toBinary);
								mav.addObject("varMeterUploadCycleHourly", "0000000000000000000000000");
								mav.addObject("varMeterUploadCycleDaily", "00000000000000000000000000000000");
								break;

							default:
								toBinary = Integer.toBinaryString(Integer.parseInt(resultTable.get(keyVal).toString()));
								toBinarySize = toBinary.length();
								for (int i = toBinarySize; i < 24; i++) {
									toBinary = "0" + toBinary;
								}
								mav.addObject("varMeterUploadCycleHourly", toBinary);
								mav.addObject("varMeterUploadCycleDaily", "00000000000000000000000000000000");
								break;
							}

						}
						mav.addObject(keyVal, resultTable.get(keyVal));
					}
				}
				status = ResultStatus.SUCCESS;
			} else {
				status = ResultStatus.FAIL;
				log.debug("resultTable Is Null");
			}

		} catch (Exception e) {
			rtnStr = e.toString();
			log.error(e, e);
			status = ResultStatus.FAIL;
			e.printStackTrace();
			// TODO: handle exception
		}

		Code operationCode = codeManager.getCodeByCode("8.3.6");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}

		mav.addObject("status", status);
		return mav;
	}

	private int spaceToZero(String inVal) {

		if ("".equals(inVal))
			return 0;
		return Integer.parseInt(inVal);
	}

	/**
	 * 모뎀의 LP 로그 취득(모뎀타입이 리피터, ZEUPLS, ZRU, MBUS)인 경우 취득 가능 모뎀이 검침데이터를 저장하고
	 * 있는경우에 해당 day lp days ex) day 1 ==> current day ex) day 2 ==> current day,
	 * yesterday
	 * 
	 * @param mcuId
	 * @param modemId
	 * @param day
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdGetLPLog")
	public ModelAndView cmdGetLPLog(@RequestParam(value = "supplierId", required = false) String supplierId,
			@RequestParam(value = "mcuId", required = false) String mcuId,
			@RequestParam(value = "modemId", required = false) String modemId,
			@RequestParam(value = "loginId", required = false) String loginId) {

		Operator operator = operatorManager.getOperatorByLoginId(loginId);

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.6")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}

		if (modemId == null || "".equals(modemId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "modemId is null!");
			return mav;
		}

		String rtnStr = "";

		Modem modem = modemManager.getModem(Integer.parseInt(modemId));
		Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			supplier = operator.getSupplier();
		}
		Set<Meter> meterSet = modem.getMeter();
		Integer meterId = -1;

		Meter meter = null;

		Iterator<Meter> keys = meterSet.iterator();

		while (keys.hasNext()) {
			meter = (Meter) keys.next();
			break;
		}

		if (meter != null) {
			meterId = meter.getId();
		}

		LPData[] lpData = null;
		try {
			lpData = cmdOperationUtil.getSensorLPLog(mcuId, modem.getDeviceSerial(), 2);
			status = ResultStatus.SUCCESS;

			for (LPData data : lpData) {

				List<Integer> lpList = new ArrayList<Integer>();
				mav.addObject("lpDate", data.getLpDate());
				mav.addObject("basePulse", data.getBasePulse());
				mav.addObject("period", data.getPeriod());
				mav.addObject("pointer", data.getPointer());
				for (int lp : data.getLp()) {
					lpList.add(lp);
				}
				mav.addObject("lp", lpList);
			}

		} catch (Exception e) {
			e.printStackTrace();
			status = ResultStatus.FAIL;
			rtnStr = e.getMessage();
		}

		if (meterId > 0 && getModemTypeCheck(modem)) {
			if (getMeterLpMissing(supplierId, meterId)) {
				for (LPData lp : lpData) {
					String result = cmdSensorLPLogRecovery(meter.getMdsId(), mcuId, modem.getDeviceSerial(), lp);
					if (result.startsWith("fail")) {
						status = ResultStatus.FAIL;
						rtnStr = "recovery fail";
					} else {
						status = ResultStatus.SUCCESS;
					}
				}
			}
		}

		Code operationCode = codeManager.getCodeByCode("8.2.6");

		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, modem.getMcu().getMcuType(), modem.getDeviceSerial(),
					loginId, operationCode, status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	private boolean getModemTypeCheck(Modem modem) {
		if (modem.getModemType() == ModemType.ZEUPLS) {
			return true;
		}
		log.debug("Modem Type Check: FALSE");
		return false;
	}

	private boolean getMeterLpMissing(String supplierId, Integer meterId) {

		Map<String, Object> params = new HashMap<String, Object>();

		String searchStartDate = "";
		String searchEndDate = "";
		try {
			searchEndDate = TimeUtil.getCurrentDay();
			searchStartDate = CalendarUtil.getDateWithoutFormat(searchEndDate, Calendar.DATE, -1);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		params.put("searchStartDate", searchStartDate);
		params.put("searchEndDate", searchEndDate);
		params.put("searchDateType", CommonConstants.DateType.HOURLY.getCode());
		params.put("supplierId", supplierId);
		params.put("meterType", CommonConstants.MeterType.EnergyMeter);
		params.put("channel", 1);
		params.put("meterId", meterId);
		params.put("deviceType", CommonConstants.DeviceType.Meter.name());

		System.out.println("params:" + params);
		List<Object> dataGapResult = dataGapsManager.getLpMissingCount(params);
		System.out.println("dataGapResult:" + dataGapResult);
		Integer missingCount = 0;
		for (Object result : dataGapResult) {
			HashMap<String, Object> res = (HashMap<String, Object>) result;
			missingCount = (Integer) res.get("missingCount");
			if (missingCount > 0) {
				return true;
			}
		}

		log.debug("Meter lp missing:" + missingCount);
		return false;
	}

	private String cmdSensorLPLogRecovery(String mdsId, String dcuNo, String modemNo, LPData lpData) {
		String result = "fail";
		try {
			int period = 0;
			if (lpData.getPeriod() == 1) {
				period = 60;
			} else if (lpData.getPeriod() == 2) {
				period = 30;
			} else if (lpData.getPeriod() == 4) {
				period = 15;
			}
			Long base = lpData.getBasePulse();

			result = cmdOperationUtil.cmdSensorLPLogRecovery(mdsId, dcuNo, modemNo, base.doubleValue(), period,
					lpData.getLp());

		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		log.debug("Sensor LP Log Recovery:" + result);
		return result;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdGetModemInfoNew")
	public ModelAndView cmdGetModemInfoNew(@RequestParam(value = "mcuId", required = false) String mcuId,
			@RequestParam(value = "modemId", required = false) String modemId,
			@RequestParam(value = "modemType", required = false) String modemType,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.2")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (modemId == null || "".equals(modemId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		Modem modem = modemManager.getModem(Integer.parseInt(modemId));
		Operator operator = operatorManager.getOperatorByLoginId(loginId);
		Supplier supplier = null;
		if (supplier == null) {
			supplier = operator.getSupplier();
		}
		Map<String, Object> result = null;
		try {

			result = cmdOperationUtil.cmdGetModemInfoNew(modem.getDeviceSerial(), mcuId);
			log.debug("result:" + result);
			Integer commState = (Integer) result.get("commState");
			String fwRevision = String.valueOf(result.get("fwRevision"));
			String fwVer = String.valueOf(result.get("fwVer"));
			String hwVer = String.valueOf(result.get("hwVer"));
			String lastLinkTime = String.valueOf(result.get("lastLinkTime"));
			String nodeKind = String.valueOf(result.get("nodeKind"));

			mav.addObject("sensorId", result.get("deviceSerial"));
			mav.addObject("commState", commState);
			mav.addObject("fwRevision", fwRevision);
			mav.addObject("fwVer", fwVer);
			mav.addObject("hwVer", hwVer);
			mav.addObject("lastLinkTime", TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(lastLinkTime),
					supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter()));
			mav.addObject("nodeKind", nodeKind);

			modem.setCommState(commState);
			modem.setFwRevision(fwRevision);
			modem.setFwVer(fwVer);
			modem.setHwVer(hwVer);
			modem.setLastLinkTime(lastLinkTime);
			modem.setNodeKind(nodeKind);
			modemManager.update(modem);
			status = ResultStatus.SUCCESS;

		} catch (Exception e) {
			rtnStr = e.toString();
			log.error(e, e);
		}

		Code operationCode = codeManager.getCodeByCode("8.2.2");
		if (operationCode != null && modem != null) {

			operationLogManager.saveOperationLog(supplier, modem.getMcu().getMcuType(), modem.getDeviceSerial(),
					loginId, operationCode, status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		mav.addObject("status", status.name());
		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdSensorScanning")
	public ModelAndView cmdSensorScanning(@RequestParam(value = "mcuId", required = false) String mcuId,
			@RequestParam(value = "modemId", required = false) String modemId,
			@RequestParam(value = "modemType", required = false) String modemType,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.2")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (modemId == null || "".equals(modemId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		Modem modem = null;
		// modemManager.getModem(Integer.parseInt(modemId));
		Operator operator = operatorManager.getOperatorByLoginId(loginId);
		Supplier supplier = null;
		if (supplier == null) {
			supplier = operator.getSupplier();
		}
		String result = "";
		try {

			result = cmdOperationUtil.doSensorScanning(mcuId, modemId, modemType, 0, operator.getName());
			log.debug("result:" + result);
			if ("success".equals(result)) {
				status = ResultStatus.SUCCESS;
				if ("ZBRepeater".equals(modemType)) {
					ZBRepeater repeater = (ZBRepeater) modemManager.getModem(Integer.parseInt(modemId));

					String installTimeSet = TimeLocaleUtil.getLocaleDate(
							StringUtil.nullToBlank(repeater.getInstallDate()), supplier.getLang().getCode_2letter(),
							supplier.getCountry().getCode_2letter());

					mav.addObject("installDate", repeater.getInstallDate() == null ? "" : installTimeSet);
					mav.addObject("linkKey", repeater.getLinkKey() == null ? "" : repeater.getLinkKey());
					mav.addObject("networkKey", repeater.getNetworkKey() == null ? "" : repeater.getNetworkKey());
					mav.addObject("extPanId", repeater.getExtPanId() == null ? "" : repeater.getExtPanId());
					mav.addObject("channelId", repeater.getChannelId() == null ? "" : repeater.getChannelId());
					mav.addObject("manualEnable", repeater.getManualEnable() == null ? "" : repeater.getManualEnable());
					mav.addObject("panId", repeater.getPanId() == null ? "" : repeater.getPanId());
					mav.addObject("securityEnable",
							repeater.getSecurityEnable() == null ? "" : repeater.getSecurityEnable());
					mav.addObject("hwVer", repeater.getHwVer() == null ? "" : repeater.getHwVer());
					mav.addObject("nodeKind", repeater.getNodeKind() == null ? "" : repeater.getNodeKind());
					mav.addObject("protocolVersion",
							repeater.getProtocolVersion() == null ? "" : repeater.getProtocolVersion());
					mav.addObject("resetCount", repeater.getResetCount() == null ? "" : repeater.getResetCount());
					mav.addObject("lastResetCode",
							repeater.getLastResetCode() == null ? "" : repeater.getLastResetCode());
					mav.addObject("swVer", repeater.getSwVer() == null ? "" : repeater.getSwVer());
					mav.addObject("fwVer", repeater.getFwVer() == null ? "" : repeater.getFwVer());
					mav.addObject("fwRevision", repeater.getFwRevision() == null ? "" : repeater.getFwRevision());
					mav.addObject("zdzdIfVersion",
							repeater.getZdzdIfVersion() == null ? "" : repeater.getZdzdIfVersion());
					mav.addObject("solarADV", repeater.getSolarADV() == null ? "" : repeater.getSolarADV());
					mav.addObject("solarChgBV", repeater.getSolarChgBV() == null ? "" : repeater.getSolarChgBV());
					mav.addObject("solarBDCV", repeater.getSolarBDCV() == null ? "" : repeater.getSolarBDCV());
					mav.addObject("testFlag", repeater.getTestFlag() == null ? "" : repeater.getTestFlag());
					mav.addObject("fixedReset", repeater.getFixedReset() == null ? "" : repeater.getFixedReset());
					mav.addObject("meteringDay", repeater.getMeteringDay() == null ? "" : repeater.getMeteringDay());
					mav.addObject("meteringHour", repeater.getMeteringHour() == null ? "" : repeater.getMeteringHour());
					mav.addObject("lpChoice", repeater.getLpChoice() == null ? "" : repeater.getLpChoice());
				} else if ("ZMU".equals(modemType)) {
					ZMU zmu = (ZMU) modemManager.getModem(Integer.parseInt(modemId));

					zmu.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat(null));
					mav.addObject("linkKey", zmu.getLinkKey() == null ? "" : zmu.getLinkKey());
					mav.addObject("networkKey", zmu.getNetworkKey() == null ? "" : zmu.getNetworkKey());
					mav.addObject("extPanId", zmu.getExtPanId() == null ? "" : zmu.getExtPanId());
					mav.addObject("channelId", zmu.getChannelId() == null ? "" : zmu.getChannelId());
					mav.addObject("manualEnable", zmu.getManualEnable() == null ? "" : zmu.getManualEnable());
					mav.addObject("panId", zmu.getPanId() == null ? "" : zmu.getPanId());
					mav.addObject("securityEnable", zmu.getSecurityEnable() == null ? "" : zmu.getSecurityEnable());
					mav.addObject("hwVer", zmu.getHwVer() == null ? "" : zmu.getHwVer());
					mav.addObject("nodeKind", zmu.getNodeKind() == null ? "" : zmu.getNodeKind());
					mav.addObject("protocolVersion", zmu.getProtocolVersion() == null ? "" : zmu.getProtocolVersion());
					mav.addObject("resetCount", zmu.getResetCount() == null ? "" : zmu.getResetCount());
					mav.addObject("lastResetCode", zmu.getLastResetCode() == null ? "" : zmu.getLastResetCode());
					mav.addObject("swVer", zmu.getSwVer() == null ? "" : zmu.getSwVer());
					mav.addObject("fwVer", zmu.getFwVer() == null ? "" : zmu.getFwVer());
					mav.addObject("fwRevision", zmu.getFwRevision() == null ? "" : zmu.getFwRevision());
					mav.addObject("zdzdIfVersion", zmu.getZdzdIfVersion() == null ? "" : zmu.getZdzdIfVersion());
				} else if ("ZEUPLS".equals(modemType)) {
					ZEUPLS zeupls = (ZEUPLS) modemManager.getModem(Integer.parseInt(modemId));

					String installTimeSet = TimeLocaleUtil.getLocaleDate(
							StringUtil.nullToBlank(zeupls.getInstallDate()), supplier.getLang().getCode_2letter(),
							supplier.getCountry().getCode_2letter());

					mav.addObject("installDate", zeupls.getInstallDate() == null ? "" : installTimeSet);
					mav.addObject("linkKey", zeupls.getLinkKey() == null ? "" : zeupls.getLinkKey());
					mav.addObject("networkKey", zeupls.getNetworkKey() == null ? "" : zeupls.getNetworkKey());
					mav.addObject("extPanId", zeupls.getExtPanId() == null ? "" : zeupls.getExtPanId());
					mav.addObject("channelId", zeupls.getChannelId() == null ? "" : zeupls.getChannelId());
					mav.addObject("manualEnable", zeupls.getManualEnable() == null ? "" : zeupls.getManualEnable());
					mav.addObject("panId", zeupls.getPanId() == null ? "" : zeupls.getPanId());
					mav.addObject("securityEnable",
							zeupls.getSecurityEnable() == null ? "" : zeupls.getSecurityEnable());
					mav.addObject("hwVer", zeupls.getHwVer() == null ? "" : zeupls.getHwVer());
					mav.addObject("nodeKind", zeupls.getNodeKind() == null ? "" : zeupls.getNodeKind());
					mav.addObject("protocolVersion",
							zeupls.getProtocolVersion() == null ? "" : zeupls.getProtocolVersion());
					mav.addObject("resetCount", zeupls.getResetCount() == null ? "" : zeupls.getResetCount());
					mav.addObject("lastResetCode", zeupls.getLastResetCode() == null ? "" : zeupls.getLastResetCode());
					mav.addObject("swVer", zeupls.getSwVer() == null ? "" : zeupls.getSwVer());
					mav.addObject("fwVer", zeupls.getFwVer() == null ? "" : zeupls.getFwVer());
					mav.addObject("fwRevision", zeupls.getFwRevision() == null ? "" : zeupls.getFwRevision());
					mav.addObject("zdzdIfVersion", zeupls.getZdzdIfVersion() == null ? "" : zeupls.getZdzdIfVersion());
					mav.addObject("solarADV", zeupls.getSolarADV() == null ? "" : zeupls.getSolarADV());
					mav.addObject("solarChgBV", zeupls.getSolarChgBV() == null ? "" : zeupls.getSolarChgBV());
					mav.addObject("solarBDCV", zeupls.getSolarBDCV() == null ? "" : zeupls.getSolarBDCV());
					mav.addObject("testFlag", zeupls.getTestFlag() == null ? "" : zeupls.getTestFlag());
					mav.addObject("fixedReset", zeupls.getFixedReset() == null ? "" : zeupls.getFixedReset());
					mav.addObject("meteringDay", zeupls.getMeteringDay() == null ? "" : zeupls.getMeteringDay());
					mav.addObject("meteringHour", zeupls.getMeteringHour() == null ? "" : zeupls.getMeteringHour());
					mav.addObject("lpChoice", zeupls.getLpChoice() == null ? "" : zeupls.getLpChoice());
				} else if ("ZRU".equals(modemType)) {
					ZRU zru = (ZRU) modemManager.getModem(Integer.parseInt(modemId));

					String installTimeSet = TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(zru.getInstallDate()),
							supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());

					mav.addObject("installDate", zru.getInstallDate() == null ? "" : installTimeSet);
					mav.addObject("linkKey", zru.getLinkKey() == null ? "" : zru.getLinkKey());
					mav.addObject("networkKey", zru.getNetworkKey() == null ? "" : zru.getNetworkKey());
					mav.addObject("extPanId", zru.getExtPanId() == null ? "" : zru.getExtPanId());
					mav.addObject("channelId", zru.getChannelId() == null ? "" : zru.getChannelId());
					mav.addObject("manualEnable", zru.getManualEnable() == null ? "" : zru.getManualEnable());
					mav.addObject("panId", zru.getPanId() == null ? "" : zru.getPanId());
					mav.addObject("securityEnable", zru.getSecurityEnable() == null ? "" : zru.getSecurityEnable());
					mav.addObject("hwVer", zru.getHwVer() == null ? "" : zru.getHwVer());
					mav.addObject("nodeKind", zru.getNodeKind() == null ? "" : zru.getNodeKind());
					mav.addObject("protocolVersion", zru.getProtocolVersion() == null ? "" : zru.getProtocolVersion());
					mav.addObject("resetCount", zru.getResetCount() == null ? "" : zru.getResetCount());
					mav.addObject("lastResetCode", zru.getLastResetCode() == null ? "" : zru.getLastResetCode());
					mav.addObject("swVer", zru.getSwVer() == null ? "" : zru.getSwVer());
					mav.addObject("fwVer", zru.getFwVer() == null ? "" : zru.getFwVer());
					mav.addObject("fwRevision", zru.getFwRevision() == null ? "" : zru.getFwRevision());
					mav.addObject("zdzdIfVersion", zru.getZdzdIfVersion() == null ? "" : zru.getZdzdIfVersion());
					mav.addObject("testFlag", zru.getTestFlag() == null ? "" : zru.getTestFlag());
					mav.addObject("fixedReset", zru.getFixedReset() == null ? "" : zru.getFixedReset());
					mav.addObject("meteringDay", zru.getMeteringDay() == null ? "" : zru.getMeteringDay());
					mav.addObject("meteringHour", zru.getMeteringHour() == null ? "" : zru.getMeteringHour());
					mav.addObject("lpChoice", zru.getLpChoice() == null ? "" : zru.getLpChoice());
				} else if ("SubGiga".equals(modemType)) {

					SubGiga subGiga = (SubGiga) modemManager.getModem(Integer.parseInt(modemId));

					String installTimeSet = TimeLocaleUtil.getLocaleDate(
							StringUtil.nullToBlank(subGiga.getInstallDate()), supplier.getLang().getCode_2letter(),
							supplier.getCountry().getCode_2letter());

					mav.addObject("installDate", subGiga.getInstallDate() == null ? "" : installTimeSet);
					mav.addObject("baseStationAddress",
							subGiga.getBaseStationAddress() == null ? "" : subGiga.getBaseStationAddress());
					mav.addObject("securityKey", subGiga.getSecurityKey() == null ? "" : subGiga.getSecurityKey());
					mav.addObject("bandWidth", subGiga.getBandWidth() == null ? "" : subGiga.getBandWidth());
					mav.addObject("frequency", subGiga.getFrequency() == null ? "" : subGiga.getFrequency());
					mav.addObject("Ipv6Address", subGiga.getIpv6Address() == null ? "" : subGiga.getIpv6Address());
					mav.addObject("hwVer", subGiga.getHwVer() == null ? "" : subGiga.getHwVer());
					mav.addObject("nodeKind", subGiga.getNodeKind() == null ? "" : subGiga.getNodeKind());
					mav.addObject("protocolVersion",
							subGiga.getProtocolVersion() == null ? "" : subGiga.getProtocolVersion());
					mav.addObject("swVer", subGiga.getSwVer() == null ? "" : subGiga.getSwVer());
					mav.addObject("fwVer", subGiga.getFwVer() == null ? "" : subGiga.getFwVer());
					mav.addObject("fwRevision", subGiga.getFwRevision() == null ? "" : subGiga.getFwRevision());
//					mav.addObject("lqi", subGiga.getFwRevision() == null ? "" : subGiga.getLqi());
//					mav.addObject("etx", subGiga.getFwRevision() == null ? "" : subGiga.getEtx());
//					mav.addObject("cpuUsage", subGiga.getFwRevision() == null ? "" : subGiga.getCpuUsage());
//					mav.addObject("memoryUsage", subGiga.getFwRevision() == null ? "" : subGiga.getMemoryUsage());
				} else if ("PLCIU".equals(modemType)) {

					PLCIU plciu = (PLCIU) modemManager.getModem(Integer.parseInt(modemId));

					String installTimeSet = TimeLocaleUtil.getLocaleDate(StringUtil.nullToBlank(plciu.getInstallDate()),
							supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());

					mav.addObject("installDate", plciu.getInstallDate() == null ? "" : installTimeSet);
					mav.addObject("hwVer", plciu.getHwVer() == null ? "" : plciu.getHwVer());
					mav.addObject("nodeKind", plciu.getNodeKind() == null ? "" : plciu.getNodeKind());
					mav.addObject("protocolVersion",
							plciu.getProtocolVersion() == null ? "" : plciu.getProtocolVersion());
					mav.addObject("swVer", plciu.getSwVer() == null ? "" : plciu.getSwVer());
					mav.addObject("fwVer", plciu.getFwVer() == null ? "" : plciu.getFwVer());
					mav.addObject("fwRevision", plciu.getFwRevision() == null ? "" : plciu.getFwRevision());
				}

				// log.debug("modem:"+modem);
			} else {
				status = ResultStatus.FAIL;
				log.debug("resultTable Is Null");
			}

		} catch (Exception e) {
			rtnStr = e.toString();
			log.error(e, e);
		}

		Code operationCode = codeManager.getCodeByCode("8.2.2");
		if (operationCode != null && modem != null) {

			operationLogManager.saveOperationLog(supplier, modem.getMcu().getMcuType(), modem.getDeviceSerial(),
					loginId, operationCode, status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		mav.addObject("status", status.name());
		// mav.addObject("model", modem);
		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdSensorStatus")
	public ModelAndView cmdSensorStatus(@RequestParam(value = "mcuId", required = false) String mcuId,
			@RequestParam(value = "modemId", required = false) String modemId,
			@RequestParam(value = "modemType", required = false) String modemType,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.3")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (mcuId == null || "".equals(mcuId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		if (modemId == null || "".equals(modemId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "modem ID null!");
			return mav;
		}

		String rtnStr = "";

		// modemManager.getModem(Integer.parseInt(modemId));
		Operator operator = operatorManager.getOperatorByLoginId(loginId);

		MCU mcu = mcuManager.getMCU(mcuId);
		Modem modem = modemManager.getModem(Integer.parseInt(modemId));
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			supplier = operator.getSupplier();
		}

		Modem resultModem = null;

		String result = "";
		try {

			resultModem = cmdOperationUtil.getSensorStatus(mcu, modem, 0, operator.getName());
			// log.debug("result:"+result);
			if (resultModem.getCommState() == 1) {
				status = ResultStatus.SUCCESS;
				modemManager.updateModem(modem);
				mav.addObject("fwVer", modem.getFwVer() == null ? "" : modem.getFwVer());
				mav.addObject("hwVer", modem.getHwVer() == null ? "" : modem.getHwVer());
				mav.addObject("nodeKind", modem.getNodeKind() == null ? "" : modem.getNodeKind());
				mav.addObject("protocolVersion", modem.getProtocolVersion() == null ? "" : modem.getProtocolVersion());
				mav.addObject("resetCount", modem.getResetCount() == null ? "" : modem.getResetCount());
				mav.addObject("lastResetCode", modem.getLastResetCode() == null ? "" : modem.getLastResetCode());
				mav.addObject("commState", "1");

			} else {
				status = ResultStatus.FAIL;
				log.debug("resultTable Is Null");
			}

		} catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.toString();
			log.error(e, e);
		}

		Code operationCode = codeManager.getCodeByCode("8.2.3");
		if (operationCode != null && modem != null) {

			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), modem.getDeviceSerial(), loginId,
					operationCode, status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		mav.addObject("status", status.name());
		// mav.addObject("model", modem);
		return mav;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdSensorEventLog")
	public ModelAndView cmdSensorEventLog(@RequestParam(value = "mcuId", required = false) String mcuId,
			@RequestParam(value = "modemId", required = false) String modemId,
			@RequestParam(value = "modemType", required = false) String modemType,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.5")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (mcuId == null || "".equals(mcuId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		if (modemId == null || "".equals(modemId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "modem ID null!");
			return mav;
		}

		String rtnStr = "";

		Operator operator = operatorManager.getOperatorByLoginId(loginId);

		MCU mcu = mcuManager.getMCU(mcuId);
		Modem modem = modemManager.getModem(Integer.parseInt(modemId));
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			supplier = operator.getSupplier();
		}

		Modem resultModem = null;

		Map result = null;
		try {

			result = cmdOperationUtil.getSensorEventLog(mcu, modem, 10, 0, operator.getName());
			// log.debug("result:"+result);
			if ("Success".equalsIgnoreCase((String) result.get("result"))) {
				status = ResultStatus.SUCCESS;
				rtnStr = result.get("result").toString();
				List<EventLog> list = (List<EventLog>) result.get("eventLog");
				List<Map<String, Object>> log = new ArrayList<Map<String, Object>>();

				for (EventLog el : list) {
					Map<String, Object> msg = new HashMap<String, Object>();
					msg.put("gmtTime", el.getGmtTime() + "");
					msg.put("firmwareBuild", el.getFirmwareBuild() + "");
					msg.put("firmwareVersion", el.getFirmwareVersion() + "");
					msg.put("eventMsg", el.getEventMsg() + "");
					msg.put("eventDescr", el.getEventDescr() + "");
					msg.put("eventStatus", el.getEventStatus() + "");
					msg.put("eventType", el.getEventType() + "");
					log.add(msg);

				}
				// mav.addObject("eventLog",log );
				mav.addObject("eventLog", list);

			} else {
				status = ResultStatus.FAIL;
				log.debug("resultTable Is Null");
			}

		} catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.toString();
			log.error(e, e);
		}

		Code operationCode = codeManager.getCodeByCode("8.2.5");
		if (operationCode != null && modem != null) {

			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), modem.getDeviceSerial(), loginId,
					operationCode, status.getCode(), rtnStr);
		}
		mav.addObject("status", status.name());

		return mav;
	}

	// jhkim MeterTimeManager Threshold Command
	@RequestMapping(value = "/gadget/device/setMeterTimeThreshold")
	public ModelAndView setMeterTimeThreshold(@RequestParam(value = "threshold", required = false) String threshold,
			@RequestParam(value = "mcuList", required = false) String mcuList) {

		ModelAndView mav = new ModelAndView("jsonView");
		String[] mcuIdList = mcuList.split(",");

		Integer mcuIdListLen = 0;
		int inThresholdTemp = Integer.parseInt(threshold);

		if (mcuIdList != null)
			mcuIdListLen = mcuIdList.length;

		Integer count = 0;
		for (int i = 0; i < mcuIdList.length; i++) {
			MCU mcu = mcuManager.getMCU(mcuIdList[i]);
			MCUVar mcuvar = null;
			mcuvar = mcu.getMcuVar();
			if (mcuvar == null) {
				mcuvar = new MCUVar();
			}

			mcuvar.setVarTimesyncThreshold(inThresholdTemp);

			mcu.setMcuVar(mcuvar);

			try {
				cmdOperationUtil.updateMcuConfiguration(mcu);
				count++;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		String rtnStr = count.toString(); // + "개의 mcu를 설정하였습니다.";
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	// jhkim MeterTimeManager MeterTimesync Command
	@RequestMapping(value = "/gadget/device/setMeterTimesync")
	public ModelAndView setMeterTimesync(@RequestParam(value = "mdsId", required = false) String mdsId,
			@RequestParam(value = "sysId", required = false) String sysId,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.7")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}

		Integer count = 0;
		MCU mcu = mcuManager.getMCU(sysId);
		Meter meter = meterManager.getMeter(mdsId);

		if (mcu == null) {
			mav.addObject("rtnStr", "Not found MCU");
			mav.addObject("status", ResultStatus.INVALID_PARAMETER);
			mav.addObject("detail", "");
			return mav;
		}

		if (meter == null) {
			mav.addObject("rtnStr", "Not found Meter");
			mav.addObject("status", ResultStatus.INVALID_PARAMETER);
			mav.addObject("detail", "");
			return mav;
		}
		ResultStatus status = ResultStatus.FAIL;
		String rtnStr = "";

		Supplier supplier = meter.getSupplier();

		try {
			try {
				Map<String, Object> resultMap = cmdOperationUtil.syncTime(sysId, mdsId);

				status = ResultStatus.SUCCESS;
				Object[] values = resultMap.values().toArray(new Object[0]);
				for (Object o : values) {
					rtnStr += (String) o + " ";

					if (((String) o).contains("failReason")) {
						status = ResultStatus.FAIL;
						break;
					}
				}

			} catch (Exception e) {
				rtnStr = e.getMessage();
				log.error(e, e);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			status = ResultStatus.FAIL;
			rtnStr = e.getMessage();
			log.error(e, e);
		}

		Code operationCode = codeManager.getCodeByCode("8.1.7");
		Code meterTypeCode = codeManager.getCode(meter.getMeterTypeCodeId());
		if (operationCode != null && operationCode.getId() != null) {
			operationLogManager.saveOperationLog(supplier, meterTypeCode, meter.getMdsId(), loginId, operationCode,
					status.getCode(), status.name());
		}

		mav.addObject("rtnStr", rtnStr);
		mav.addObject("status", status);

		return mav;
	}

	/**
	 * TOUCalendar 설정 명령.<br>
	 * 설정 정보는 TouProfile에서 읽어온다.
	 * 
	 * @param meterSerial
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdTOUCalendar")
	public ModelAndView cmdTOUCalendar(@RequestParam(value = "meterSerial", required = true) String meterSerial,
			@RequestParam(value = "loginId") String loginId) {
		return cmdMeterProgram(meterSerial, loginId, MeterProgramKind.TOUCalendar);
	}

	/**
	 * TOUCalendar 설정 명령.<br>
	 * 설정 정보는 TouProfile에서 읽어온다.
	 * 
	 * @param meterSerial
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdSummerTime")
	public ModelAndView cmdSummerTime(@RequestParam(value = "meterSerial", required = true) String meterSerial,
			@RequestParam(value = "loginId") String loginId) {
		return cmdMeterProgram(meterSerial, loginId, MeterProgramKind.DaySavingTime);
	}

	/**
	 * DisplayItemSetting 설정 명령.
	 * 
	 * @param meterSerial
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdDisplayItemSetting")
	public ModelAndView cmdDisplayItemSetting(@RequestParam(value = "meterSerial", required = true) String meterSerial,
			@RequestParam(value = "loginId") String loginId) {
		return cmdMeterProgram(meterSerial, loginId, MeterProgramKind.DisplayItemSetting);
	}

	public ModelAndView cmdMeterProgram(String meterSerial, String loginId, MeterProgramKind kind) {
		ModelAndView mav = new ModelAndView("jsonView");
		ResultStatus status = ResultStatus.FAIL;

		try {

			// 명령 권한 확인
			if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.21")) {
				// opcode
				mav.addObject("rtnStr", "No permission");
				return mav;
			}

			Meter meter = this.meterManager.getMeter(meterSerial);
			Modem modem = meter.getModem();
			DeviceModel model = modem.getModel();
			String modelName = model.getName();
			if (modelName.equals("NAMR-P114GP_MX2")) {
				// bypass mode
				cmdOperationUtil.cmdBypassMeterProgram(meterSerial, kind);

				status = ResultStatus.SUCCESS;
				mav.addObject("rtnStr", "Waiting on completion. (The results can be found at MeterProgramLog)");
			} else {
				cmdOperationUtil.cmdMeterProgram(meterSerial, kind);
				mav.addObject("rtnStr", "Success");
				status = ResultStatus.SUCCESS;
			}
		} catch (Exception e) {
			log.error(e, e);
			mav.addObject("rtnStr", e.getMessage());
		}

		mav.addObject("status", status.name());
		return mav;
	}

	/**
	 * 단일모뎀 모뎀 펌웨어 업데이트(MEA 태국 준비용으로 제작됨)
	 * <p>
	 * kskim
	 * <p>
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/smsFirmwareUpdate")
	public ModelAndView smsFirmwareUpdate(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView view = new ModelAndView("jsonView");
		ResultStatus status = ResultStatus.FAIL;

		byte[] fileBinary = null;
		String filePath = null;
		String loginId = null;
		String modemId = null;
		String ext = null;

		Modem modem = null;
		try {

			MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) request;
			MultipartFile multipartFile = multiReq.getFile("userfile");

			loginId = request.getParameter("loginId");
			modemId = request.getParameter("modemId");
			ext = request.getParameter("ext");

			if (loginId == null || modemId == null || ext == null)
				throw new Exception();

			modem = modemManager.getModem(Integer.valueOf(modemId));
			if (modem == null)
				throw new Exception();

			fileBinary = multipartFile.getBytes();
			filePath = multipartFile.getOriginalFilename().split("\\.")[0];

		} catch (Exception e) {
			view.addObject("rtnStr", "Invalid parameter");
		}

		// 파일 저장
		String osName = System.getProperty("os.name");
		String homePath = "";
		if (osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0) {
			homePath = CommandProperty.getProperty("firmware.window.dir");
		} else {
			homePath = CommandProperty.getProperty("firmware.dir");
		}

		String finalFilePath = makeFirmwareDirectory(homePath, filePath, ext, true);

		try {
			log.info(String.format("Save firmware file - %s", finalFilePath));
			FileOutputStream foutStream = new FileOutputStream(finalFilePath, true);

			foutStream.write(fileBinary);
			foutStream.flush();
			foutStream.close();

			status = ResultStatus.SUCCESS;
			view.addObject("rtnStr", finalFilePath);
			view.addObject("rtnStr", "Finished");
		} catch (Exception e) {
			log.error(e);
			view.addObject("rtnStr", "Save firmware file error");
		}

		// TODO : SMS명령
		try {
			String modemSerial = modem.getDeviceSerial();
			// FIXME : SMS명령 파라미터 받을수 있는 메소드 추가해야함.
			cmdOperationUtil.cmdSmsFirmwareUpdate(modemSerial, filePath);
			// cmdOperationUtil.cmdUpdateModemFirmware(null,modemSerial,fileName);
			status = ResultStatus.SUCCESS;
			view.addObject("rtnStr", "Finished");
		} catch (Exception e) {
			log.error(e);
			view.addObject("rtnStr", "Send SMS fail.");
		}

		view.addObject("status", status.name());
		return view;
	}

	/**
	 * 겹치지 않는 파일 Path 를 만들어 리턴한다. 포함된 디렉토리는 자동 생성된다.
	 * 
	 * @param homePath
	 *            홈 디렉토리
	 * @param fileName
	 *            파일명(확장자 제외)
	 * @param ext
	 *            파일 확장자('.' 제외)
	 * @param deletable
	 *            중복된 파일 삭제
	 * @return
	 */
	private String makeFirmwareDirectory(String homePath, String fileName, String ext, boolean deletable) {
		File file = null;
		StringBuilder firmwareDir = new StringBuilder();
		firmwareDir.append(homePath);
		firmwareDir.append("/");
		firmwareDir.append(fileName);

		file = new File(firmwareDir.toString());
		if (!file.exists()) {
			file.mkdirs();
		}
		firmwareDir.append("/");
		firmwareDir.append(fileName);
		firmwareDir.append(".");
		firmwareDir.append(ext);

		file = new File(firmwareDir.toString());

		boolean result = false;
		if (deletable && file.exists()) {
			result = file.delete();
		} else {
			result = true;
		}

		if (!result) {
			// 새로운 이름 규칙은 기존 이름+(n) 방식이다.
			if (fileName.matches(".*\\([0-9]*\\)")) {
				// 기존 파일 이름이 중복 규칙에 의해 만들어진 파일 명이라면 숫자를 증가시켜 이름을 다시 만든다.
				int number = Integer.valueOf(fileName.replaceAll(".*\\(([0-9]*)\\)", "$1"));
				fileName = fileName.replaceAll("(.*)\\([0-9]*\\)", String.format("$1(%d)", number++));
			} else {
				// 파일 이름에 중복 이름 규칙을 적용한다.
				fileName = String.format("%s(0)", fileName);
			}

			// 중복되는지 제귀하여 확인한다.
			return makeFirmwareDirectory(homePath, fileName, ext, deletable);
		}
		return file.getPath();
	}

	private String makeFirmwareDirectoryForEMnV(String homePath, String subPath, String fileName, String ext,
			boolean deletable) {
		File file = null;
		StringBuilder firmwareDir = new StringBuilder();
		firmwareDir.append(homePath);
		firmwareDir.append("/");
		firmwareDir.append(subPath);

		file = new File(firmwareDir.toString());
		if (!file.exists()) {
			file.mkdirs();
		}
		firmwareDir.append("/");
		firmwareDir.append(fileName);
		firmwareDir.append(".");
		firmwareDir.append(ext);

		file = new File(firmwareDir.toString());

		boolean result = false;
		if (deletable && file.exists()) {
			result = file.delete();
		} else {
			result = true;
		}

		if (!result) {
			// 새로운 이름 규칙은 기존 이름+(n) 방식이다.
			if (fileName.matches(".*\\([0-9]*\\)")) {
				// 기존 파일 이름이 중복 규칙에 의해 만들어진 파일 명이라면 숫자를 증가시켜 이름을 다시 만든다.
				int number = Integer.valueOf(fileName.replaceAll(".*\\(([0-9]*)\\)", "$1"));
				fileName = fileName.replaceAll("(.*)\\([0-9]*\\)", String.format("$1(%d)", number++));
			} else {
				// 파일 이름에 중복 이름 규칙을 적용한다.
				fileName = String.format("%s(0)", fileName);
			}

			// 중복되는지 제귀하여 확인한다.
			return makeFirmwareDirectoryForEMnV(homePath, subPath, fileName, ext, deletable);
		}
		return file.getPath();
	}

	/*
	*//**
		 * 켈린더 정보 파일을 업로드 하여 미터 켈릭더를 설정한다. <br>
		 * 1 <br>
		 * author kskim.
		 * 
		 * @param meterSerial
		 * @param fileType
		 * @param request
		 * @param response
		 * @return
		 * @throws ServletRequestBindingException
		 * @throws IOException
		 */
	/*
	 * @RequestMapping(value="/gadget/device/upfTOUCalendar") public
	 * ModelAndView upfTOUCalendar(HttpServletRequest request,
	 * HttpServletResponse response) throws ServletRequestBindingException,
	 * IOException {
	 * 
	 * MultipartHttpServletRequest multiReq =
	 * (MultipartHttpServletRequest)request; MultipartFile multipartFile =
	 * multiReq.getFile("userfile");
	 * 
	 * ModelAndView mav = new ModelAndView("jsonView");
	 * 
	 * String meterSerial = request.getParameter("meterSerial"); String fileType
	 * = request.getParameter("fileType");
	 * 
	 * if(meterSerial==null){ mav.addObject("rtnStr","Can not found Meter ID");
	 * }
	 * 
	 * if(fileType==null){ mav.addObject("rtnStr","Can not found File Type"); }
	 * 
	 * 
	 * byte[] fileBinary = multipartFile.getBytes();
	 * 
	 * try{ cmdOperationUtil.cmdSetTOUCalendar(meterSerial, fileType,
	 * fileBinary); mav.addObject("rtnStr","sucess"); } catch(Exception e){
	 * log.error(e, e); if(e.getCause() instanceof
	 * java.io.NotSerializableException){ mav.addObject("rtnStr",
	 * "Object is not Serializable"); }else{
	 * mav.addObject("rtnStr",e.getCause().getLocalizedMessage()); } } return
	 * mav; }
	 */

	@RequestMapping(value = "/gadget/device/command/cmdDemandReset")
	public ModelAndView cmdDemandReset(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.18")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = null;

		Meter meter = meterManager.getMeter(Integer.parseInt(target));
		Supplier supplier = meter.getSupplier();

		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			cmdOperationUtil.cmdDemandReset(Integer.parseInt(target));
			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.toString();
			log.error(e, e);
		}

		Code operationCode = codeManager.getCodeByCode("8.1.18");

		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, meter.getMeterType(), meter.getMdsId(), loginId,
					operationCode, status.getCode(), rtnStr);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr != null ? rtnStr : "Success");
		return mav;
	}

	/**
	 * CODI에 IHD를 등록
	 * 
	 * @param groupId
	 * @param sensorId
	 * @param loginId
	 * @return
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/gadget/device/command/cmdSetIHDTable")
	public ModelAndView cmdSetIHDTable(@RequestParam(value = "groupId", required = true) String groupId,
			@RequestParam(value = "sensorId", required = true) String sensorId,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");

		// TODO 허용 권한 체크에서 command 목록 사이즈를 0으로 가져오는 문제가 발생해 일단 막음
		/*
		 * if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.1")) {//
		 * TODO // opcode mav.addObject("rtnStr", "No permission");
		 * mav.addObject("status", status); return mav; }
		 */
		String mcuId = "";
		if (groupId == null || "".equals(groupId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			mav.addObject("status", status);
			return mav;
		} else {
			HomeGroup hg = homeGroupDao.get(Integer.parseInt(groupId));
			mcuId = hg.getHomeGroupMcu().getSysID();
		}

		String rtnStr = "";

		MCU mcu = mcuManager.getMCU(mcuId);
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			cmdOperationUtil.cmdSetIHDTable(mcuId, sensorId);
			status = ResultStatus.SUCCESS;
			rtnStr = status.name();

		} catch (Exception e) {
			log.error(e, e);
			rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
		}

		Code operationCode = codeManager.getCodeByCode("8.1.1");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), status.name());
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	/**
	 * CODI에 등록된 IHD를 삭제
	 * 
	 * @param groupId
	 * @param sensorId
	 * @param loginId
	 * @return
	 */
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/gadget/device/command/cmdDelIHDTable")
	public ModelAndView cmdDelIHDTable(@RequestParam(value = "groupId", required = true) String groupId,
			@RequestParam(value = "sensorId", required = true) String sensorId,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");

		// TODO 허용 권한 체크에서 command 목록 사이즈를 0으로 가져오는 문제가 발생해 일단 막음
		/*
		 * if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.1")) {//
		 * TODO // opcode mav.addObject("rtnStr", "No permission");
		 * mav.addObject("status", status); return mav; }
		 */
		String mcuId = "";
		if (groupId == null || "".equals(groupId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			mav.addObject("status", status);
			return mav;
		} else {
			HomeGroup hg = homeGroupDao.get(Integer.parseInt(groupId));
			mcuId = hg.getHomeGroupMcu().getSysID();
		}

		String rtnStr = "";

		MCU mcu = mcuManager.getMCU(mcuId);
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			cmdOperationUtil.cmdDelIHDTable(mcuId, sensorId);
			status = ResultStatus.SUCCESS;
			rtnStr = status.name();
		} catch (Exception e) {
			log.error(e, e);
			rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
		}

		Code operationCode = codeManager.getCodeByCode("8.1.1");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), status.name());
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	/**
	 * 집중기 그룹의 멤버를 groupType과 함께 추가 (OID 102.70사용)
	 * 
	 * @param groupType
	 * @param groupName
	 * @param modemArray
	 * @param loginId
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdUpdateGroup")
	public ModelAndView cmdUpdateGroup(@RequestParam(value = "groupType", required = true) String groupType,
			@RequestParam(value = "groupName", required = true) String groupName,
			@RequestParam(value = "groupId", required = true) Integer groupId,
			@RequestParam(value = "modemArray[]", required = true) String[] modemArray,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");

		String mcuId = "";
		if (groupName == null || "".equals(groupName)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			mav.addObject("status", status);
			return mav;
		} else {
			HomeGroup hg = homeGroupDao.getHomeGroup(groupName);
			mcuId = hg.getHomeGroupMcu().getSysID();
		}

		String rtnStr = "";

		MCU mcu = mcuManager.getMCU(mcuId);
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		if (modemArray == null) {
			modemArray = new String[0];
		}

		TransactionStatus txStatus = null;
		DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
		txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);

		try {
			txStatus = transactionManager.getTransaction(txDefine);

			cmdOperationUtil.cmdUpdateGroup(mcuId, groupType, groupName, modemArray);

			List<String> modemList = new ArrayList<String>();
			for (int i = 0; i < modemArray.length; i++) {
				modemList.add(modemArray[i]);
			}

			groupMemberDao.updateMCURegirationList(true, modemList, groupId);
			groupMemberDao.updateMCURegirationList(null, modemList, groupId);

			status = ResultStatus.SUCCESS;
			rtnStr = status.name();

			transactionManager.commit(txStatus);
		} catch (Exception e) {
			log.error(e, e);
			rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
			if (txStatus != null)
				transactionManager.rollback(txStatus);
		}

		Code operationCode = codeManager.getCodeByCode("8.1.1");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, codeDao.get(mcu.getMcuTypeCodeId()), mcu.getSysID(), loginId,
					operationCode, status.getCode(), status.name());
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	/**
	 * 집중기 그룹의 멤버를 지정된 ID로 삭제 (OID 102.71사용)
	 * 
	 * @param groupType
	 * @param groupName
	 * @param modemArray
	 * @param loginId
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdDeleteGroup")
	public ModelAndView cmdDeleteGroup(@RequestParam(value = "groupType", required = true) String groupType,
			@RequestParam(value = "groupName", required = true) String groupName,
			@RequestParam(value = "groupId", required = true) Integer groupId,
			@RequestParam(value = "modemArray[]", required = true) String[] modemArray,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");

		String mcuId = "";
		if (groupName == null || "".equals(groupName)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			mav.addObject("status", status);
			return mav;
		} else {
			HomeGroup hg = homeGroupDao.getHomeGroup(groupName);
			mcuId = hg.getHomeGroupMcu().getSysID();
		}

		String rtnStr = "";

		MCU mcu = mcuManager.getMCU(mcuId);
		Supplier supplier = supplierDao.get(mcu.getSupplierId());
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		if (modemArray == null) {
			modemArray = new String[0];
		}

		TransactionStatus txStatus = null;
		DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
		txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);

		try {
			txStatus = transactionManager.getTransaction(txDefine);
			List<String> modemList = new ArrayList<String>();

			for (int i = 0; i < modemArray.length; i++) {
				modemList.add(modemArray[i]);
			}

			groupMemberDao.updateMCURegirationList(false, modemList, groupId);
			groupMemberDao.updateMCURegirationList(null, modemList, groupId);

			cmdOperationUtil.cmdDeleteGroup(mcuId, groupType, groupName, modemArray);
			status = ResultStatus.SUCCESS;
			rtnStr = status.name();

			transactionManager.commit(txStatus);

		} catch (Exception e) {
			log.error(e, e);
			rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
			if (txStatus != null)
				transactionManager.rollback(txStatus);
		}

		Code operationCode = codeManager.getCodeByCode("8.1.1");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, codeDao.get(mcu.getMcuTypeCodeId()), mcu.getSysID(), loginId,
					operationCode, status.getCode(), status.name());
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	/**
	 * 집중기에 (OID 102.70)으로 등록된 그룹정보를 가져온다.(OID 102.72사용)
	 * 
	 * 
	 * @param mcuId
	 * @param loginId
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdGetGroup")
	public ModelAndView cmdGetGroup(@RequestParam(value = "mcuId", required = true) String mcuId,
			@RequestParam(value = "groupType", required = false) String groupType,
			@RequestParam(value = "groupName", required = false) String groupName,
			@RequestParam(value = "sensorId", required = true) String sensorId,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;
		String result = "close";
		ModelAndView mav = new ModelAndView("jsonView");

		MCU mcu = mcuManager.getMCU(mcuId);

		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {

			int groupId = 0;
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			conditionMap.put("groupName", groupName);

			List<Map<String, Object>> group = groupMgmtManager.dupCheckGroupName(conditionMap);
			if (group != null && group.size() > 0) {
				groupId = Integer.parseInt(group.get(0).get("groupId").toString());
			}

			List<GroupTypeInfo> groupInfoList = null;
			List<String> member = new ArrayList<String>();

			groupInfoList = cmdOperationUtil.cmdGetGroup(mcuId, groupType, groupName, sensorId);

			String equalsSensorId = "";
			// sensorId의 첫글자가 0이면 16자리를 채우기위해 쓰여지는 0은 뒤에 붙는다.
			if ("0".equals(sensorId.substring(0, 1))) {
				equalsSensorId = sensorId;
			}

			// 집중기에서 그룹을 지을때 보통 eui64를 가지고 짓기때문에 16자리로 자리수가 지정되어있음
			if (sensorId.length() < 16) {
				for (int i = 0; i < 16 - (sensorId.length()); i++) {
					equalsSensorId += "0";
				}
			}
			// sensorId의 첫글자가 0이 아니면 16자리를 채우기위해 쓰여지는 0은 앞에 붙는다.
			if (!("0".equals(sensorId.substring(0, 1)))) {
				equalsSensorId += sensorId;
			}

			if (groupInfoList == null || groupInfoList.size() < 1) {
				member.add(sensorId);
				groupMemberDao.updateMCURegirationList(false, member, groupId);
				groupMemberDao.updateMCURegirationList(null, member, groupId);
				result = "close";
			} else {
				for (int i = 0; i < groupInfoList.size(); i++) {
					if (equalsSensorId.equals(groupInfoList.get(i).getMember())) {
						member.add(sensorId);
						groupMemberDao.updateMCURegirationList(true, member, groupId);
						groupMemberDao.updateMCURegirationList(null, member, groupId);
						result = "registration";
					}
				}
			}

			status = ResultStatus.SUCCESS;

		} catch (Exception e) {
			log.error(e, e);
			status = ResultStatus.FAIL;
		}

		Code operationCode = codeManager.getCodeByCode("8.1.1");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, codeDao.get(mcu.getMcuTypeCodeId()), mcu.getSysID(), loginId,
					operationCode, status.getCode(), status.name());
		}

		mav.addObject("result", result);
		mav.addObject("status", status);
		return mav;

	}

	/**
	 * 집중기에 등록된 모든 모뎀정보를 가져온다.
	 * 
	 * 
	 * @param mcuId
	 * @param loginId
	 * @return
	 */
	@Transactional(readOnly = false)
	@RequestMapping(value = "/gadget/device/command/mcuSensorScan")
	public ModelAndView mcuSensorScan(@RequestParam(value = "target") String target,
			@RequestParam(value = "loginId") String loginId) {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		String errorReason = "No Error";

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		MCU mcu = mcuManager.getMCU(Integer.parseInt(target));

		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			List<Map<String, Object>> modemInfoList = cmdOperationUtil.findSensorInfo(mcu);

			for (Map<String, Object> map : modemInfoList) {
				Modem modem = modemManager.getModem((String) map.get("deviceSerial"));
				if (modem != null) {
					modem.setCommState(Integer.parseInt(map.get("commState").toString()));
					modem.setMcu(mcu);
					modem.setFwVer(String.valueOf(map.get("fwVer")));
					modem.setFwRevision(String.valueOf(map.get("fwRevision")));
					modem.setHwVer(String.valueOf(map.get("hwVer")));
					modem.setLastLinkTime(String.valueOf(map.get("lastLinkTime")));
					modem.setNodeKind(String.valueOf(map.get("nodeKind")));

					modemManager.update(modem);
				}
			}

			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
			log.error(e, e);
			errorReason = e.toString();
			status = ResultStatus.FAIL;
		}

		mav.addObject("status", status);
		mav.addObject("rtnStr", errorReason);
		return mav;
	}

	@RequestMapping(value = "/gadget/device/command/dlmsGetSet")
	public ModelAndView dlmsGetSet(String cmd, String parameter, String mdsId, String modelName, String loginId) {
		ModelAndView mav = new ModelAndView("jsonView");
		List<Map<String, Object>> modemTempList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> modemList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rtnStrList = new ArrayList<Map<String, Object>>();
		ResultStatus status = ResultStatus.FAIL;
		Meter meter = null;
		
        JSONArray jsonArr = null;
        try{
    		if (loginId != null ){
    			if ( ("cmdMeterParamSet".equals(cmd) || "cmdMeterParamAct".equals(cmd)) &&
    				!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.7")) { //OBIS Set
    				throw new Exception("No permission");
    			}
    			else if ("cmdMeterParamGet".equals(cmd) && 
    					!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.6")) { //OBIS Get
    				throw new Exception("No permission");
    			}
    		}
	        if(parameter == null || parameter.isEmpty()) {
	        	jsonArr = new JSONArray();
	        } else {
	        	jsonArr = JSONArray.fromObject(parameter);
	        }
	        
	        List<Map<String,String>> paramList = new ArrayList<Map<String,String>>();
			Map<String,String> paramMap = new HashMap<String,String>();
	        if(jsonArr.size() > 0) {
	    		Object[] tempJson = jsonArr.toArray();
	    		for (int i = 0; i < tempJson.length; i++) {
	    			Map<String,Object> jsonMap = (Map<String,Object>)tempJson[i];
	    			String obisCode = jsonMap.get("OBISCODE").toString();
	    			String classId = jsonMap.get("CLASSID").toString();
	    			String attributeNo = jsonMap.get("ATTRIBUTENO") == null ? "" : jsonMap.get("ATTRIBUTENO").toString();
	    			String dataType = jsonMap.get("DATATYPE") == null ? "" : jsonMap.get("DATATYPE").toString();
	    			String accessRight = jsonMap.get("ACCESSRIGHT") == null ? "" : jsonMap.get("ACCESSRIGHT").toString();
	    			String value = jsonMap.get("VALUE") == null ? "" : jsonMap.get("VALUE").toString();
	    			
	    			//cmd종류: cmdMeterParamGet, cmdMeterParamSet
	    			String paramType = "";
	    			if(i==0) {
	    				paramType = cmd.replace("cmdMeterP", "p");
	    			}else {
	    				paramType = cmd.replace("cmdMeterP", "p")+i;
	    			}
	    			paramMap.put(paramType, obisCode+"|"+classId+"|"+attributeNo+"|"+accessRight+"|"+dataType+"|"+value);
	    			paramList.add(paramMap);
	        	}
	        }
	        
    		try{
        		meter = meterManager.getMeter(mdsId);
            	if(meter != null && meter.getModem() != null) {
            		if(meter.getModel() != null && !meter.getModel().getName().equals(modelName)) {
            			Map<String,Object> tempMap = new HashMap<String,Object>();
                		tempMap.put("meterId", mdsId);
                		tempMap.put("rtnStr", "FAIL : Model is Not " + modelName);
                		rtnStrList.add(tempMap);
            		} else {
            			Modem modem = meter.getModem();
            			if(modem.getModemType() == ModemType.MMIU && (modem.getProtocolType() == Protocol.SMS 
            					|| modem.getProtocolType() == Protocol.IP 
            					|| modem.getProtocolType() == Protocol.GPRS)) {
                			Map<String,Object> condition = new HashMap<String,Object>();
    	            		condition.put("modemId", meter.getModemId());
    	            		condition.put("modemType", ModemType.MMIU.toString());
    	            		MMIU mmiu = (MMIU)modemManager.getModemByType(condition);
    	            		
    	            		Map<String,Object> tempMap = new HashMap<String,Object>();
    	            		tempMap.put("meterId", mdsId);
    	            		tempMap.put("modemType", meter.getModem().getModemType().name());
    	            		tempMap.put("protocolType", meter.getModem().getProtocolType());
    	            		tempMap.put("modem", mmiu);
    	            		modemTempList.add(tempMap);
            			} else if(modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP ) {
            				Map<String,Object> tempMap = new HashMap<String,Object>();
		            		tempMap.put("meterId", mdsId);
		            		tempMap.put("modemType", meter.getModem().getModemType());
		            		tempMap.put("protocolType", meter.getModem().getProtocolType());
		            		tempMap.put("modem", meter.getModem());
		            		modemTempList.add(tempMap);
            			} else if((modem.getModemType() == ModemType.PLC_G3 || modem.getModemType() == ModemType.PLC_PRIME || modem.getModemType() == ModemType.PLC_HD || modem.getModemType() == ModemType.PLCIU) 
            					&& modem.getProtocolType() == Protocol.IP ) {
            				Map<String,Object> tempMap = new HashMap<String,Object>();
		            		tempMap.put("meterId", mdsId);
		            		tempMap.put("modemType", meter.getModem().getModemType());
		            		tempMap.put("protocolType", meter.getModem().getProtocolType());
		            		tempMap.put("modem", meter.getModem());
		            		modemTempList.add(tempMap);
		            		
                		} else {
                			Map<String,Object> tempMap = new HashMap<String,Object>();
		            		tempMap.put("meterId", mdsId);
		            		tempMap.put("rtnStr", "FAIL : Target ID null!");
		            		rtnStrList.add(tempMap);
                		}
            		}
            	} else {
            		Map<String,Object> tempMap = new HashMap<String,Object>();
            		tempMap.put("meterId", mdsId);
            		tempMap.put("rtnStr", "FAIL : Target ID null!");
            		rtnStrList.add(tempMap);
            	}
            	log.info("\\\\\\\\\\\\\\\\modemTempList = \\\\\\\\\\\\\n "+ modemTempList);
       
    		}catch(Exception e) {
    			log.warn(e,e);
    			Map<String,Object> tempMap = new HashMap<String,Object>();
        		tempMap.put("meterId", mdsId);
        		tempMap.put("rtnStr", "FAIL : Target ID null!");
        		rtnStrList.add(tempMap);
    		}
	        
	        for (int j = 0; j < modemTempList.size(); j++) {
	        	Map<String,Object> map = null;
	        	try{
	        		map = modemTempList.get(j);
	        		
	        		if(map.get("modemType") == ModemType.MMIU.name() && map.get("protocolType") == Protocol.SMS) {
	        			MMIU mmiu = (MMIU)map.get("modem");
		        		
		        		String mobileNo = mmiu.getPhoneNumber();
		            	if (mobileNo == null || "".equals(mobileNo)) {
		            		log.warn(String.format("[" + cmd + "] Phone number is empty"));
		            		Map<String,Object> tempMap = new HashMap<String,Object>();
		            		tempMap.put("meterId", map.get("meterId"));
		            		tempMap.put("rtnStr", "FAIL : Phone number is empty!");
		            		rtnStrList.add(tempMap);
		            		continue;
		        		}
		            	
		            	if (!Protocol.SMS.equals(mmiu.getProtocolType())) {
		            		log.warn(String.format("[" + cmd + "] Invalid ProtocolType"));
		            		Map<String,Object> tempMap = new HashMap<String,Object>();
		            		tempMap.put("meterId", map.get("meterId"));
		            		tempMap.put("rtnStr", "FAIL : Invalid ProtocolType!");
		            		rtnStrList.add(tempMap);
		            		continue;
		    			}
		            	
						
						 // 서버에서 모뎀으로 SMS를 보낸뒤 모뎀이 서버에 접속하여 수행해야할 Command가 무엇인지
						 //* 구분할 방법이 따로 없기 때문에 Transaction ID를 사용하여 구분하도록 한다.
						 
						Long trId = System.currentTimeMillis();
						Map<String, String> result;
						String cmdResult = "";
						
						//* 비동기 명령 저장 : SMS발송보다 먼저 저장하려 했으나 변경-> 메시지발송결과를 받으면 명령을 저장.						 
						//saveAsyncCommandList(mmiu.getDeviceSerial(), trId, cmd, paramList, TimeUtil.getCurrentTime());
						//map.put("trId", trId);
						modemList.add(map);

						//String messageId = sendSMSForMOE("244.0.0", trId + "", mmiu.getDeviceSerial(), cmd);
						result = sendSmsForCmdServer(mmiu, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), cmd, paramMap); 
						
						if(result != null){
								cmdResult =  result.get("RESULT").toString();
								//check result 0911
								log.debug("### MMIU - CMDRESULT : " + cmdResult);						
							}else{
								log.debug("SMS Fail");
								//cmdResult="Failed to get the resopone. See the Async Command History.";
								cmdResult="Check the Async_Command_History.";
						}						

						// MMIU모뎀의 결과값은 나중에 한번에 가져온다.
						Map<String, Object> tempMap = new HashMap<String, Object>();
						tempMap.put("meterId", map.get("meterId"));
						tempMap.put("rtnStr", cmdResult);
						//tempMap.put("trId", trId);
						rtnStrList.add(tempMap);
					} else {
					
						List<Map<String, Object>> result = null;
						Modem modem = (Modem) map.get("modem");
						if ("cmdMeterParamGet".equals(cmd)) {
							result = cmdOperationUtil.cmdMeterParamGet(modem.getDeviceSerial(),
									paramList.get(0).get("paramGet"), modem.getProtocolType());
						} else if ("cmdMeterParamSet".equals(cmd))  {
							result = cmdOperationUtil.cmdMeterParamSet(modem.getDeviceSerial(),
									paramList.get(0).get("paramSet"), modem.getProtocolType());
						} else if ("cmdMeterParamAct".equals(cmd))  {
							result = cmdOperationUtil.cmdMeterParamAct(modem.getDeviceSerial(),
									paramList.get(0).get("paramAct"), modem.getProtocolType());
						}

						if (result != null) {
							Map<String, Object> tempMap = new HashMap<String, Object>();
							tempMap.put("meterId", map.get("meterId"));
							tempMap.put("rtnStr", "DONE! ");
							tempMap.put("viewMsg", result);
							rtnStrList.add(tempMap);
							status = ResultStatus.SUCCESS;
						}
					}

				} catch (Exception e) {
					log.error(e, e);
					Map<String, Object> tempMap = new HashMap<String, Object>();
					tempMap.put("meterId", map.get("meterId"));
					//tempMap.put("rtnStr", "FAIL : " + e.getMessage());
					tempMap.put("rtnStr", "Check the Async_Command_History");
					rtnStrList.add(tempMap);
					continue;
				}
			}

		} catch (Exception e) {
			log.error(e, e);
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("meterId", "Unknow Error");
			tempMap.put("rtnStr", "FAIL : " + e.getMessage());
			rtnStrList.add(tempMap);
		}

        if ( meter != null ){
    		Code operationCode = "cmdMeterParamSet".equals(cmd) ? 
    				codeManager.getCodeByCode("8.1.7") : codeManager.getCodeByCode("8.1.6");
    		if (operationCode != null) {
    			operationLogManager.saveOperationLog(meter.getSupplier(), 
    					meter.getMeterType(), meter.getMdsId(), loginId,
						operationCode, status.getCode(), status.name());
    		}

        }
		mav.addObject("rtnStrList", rtnStrList);
		return mav;
	}

	@RequestMapping(value = "/gadget/device/command/dlmsGetLog")
	public ModelAndView dlmsGetLog(
			@RequestParam(value = "cmd", required = true) String cmd,
			@RequestParam(value = "mdsId", required = true) String mdsId,
			@RequestParam(value = "fromDate", required = true) String fromDate,
			@RequestParam(value = "toDate", required = true) String toDate,
			@RequestParam(value = "modelName", required = false) String modelName,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ModelAndView mav = new ModelAndView("jsonView");
		List<Map<String, Object>> rtnStrList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> result = null;
		Meter meter = meterManager.getMeter(mdsId);
		Modem modem = null;
		ResultStatus status = ResultStatus.FAIL;
		int modemId = meter.getModemId();
		
		

		try {
    		if ( loginId != null  && !commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.6")) { //OBIS Get
    			throw new Exception("No permission");
    		}
			if (meter == null || (modem = meter.getModem()) == null) {
				Map<String,Object> tempMap = new HashMap<String,Object>();
				tempMap.put("meterId", mdsId);
				tempMap.put("rtnStr", "FAIL : Target ID is null!");
				rtnStrList.add(tempMap);
			}
			else if (modelName != null && 
					meter.getModel() != null && 
					!meter.getModel().getName().equals(modelName)){
	 			Map<String,Object> tempMap = new HashMap<String,Object>();
        		tempMap.put("meterId", mdsId);
        		tempMap.put("rtnStr", "FAIL : Model is Not " + modelName);
        		rtnStrList.add(tempMap);
    		} 
			else {
				if( modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.SMS){					
					try{ //Inside Try (S)
						Map<String,Object> condition = new HashMap<String,Object>();
	            		condition.put("modemId", meter.getModemId());
	            		condition.put("modemType", ModemType.MMIU.toString());
	            		MMIU mmiu = (MMIU)modemManager.getModemByType(condition);
						
						String mobileNo = mmiu.getPhoneNumber();
						if(  mobileNo != null || !("".equals(mobileNo))  ){
							Map<String,String> paramMap = new HashMap<String,String>();
							// Same as CommandGW (Each Log Commands)
							CommandGW cgw = new CommandGW();
							Map<String,String> valueMap = cgw.eventLogValueByRange(fromDate,toDate);
							String value =  cgw.meterParamMapToJSON(valueMap);
							Map<String, String> smsResult;
							
							String param = "";
							int classId = DLMS_CLASS.PROFILE_GENERIC.getClazz();
							int attrId = DLMS_CLASS_ATTR.PROFILE_GENERIC_ATTR02.getAttr();							
							// SET Async_Command_Param
							if ( cmd.equals("cmdGetStandardEventLog")){
								String obisCode = cgw.convertObis(OBIS.STANDARD_EVENT.getCode());							
								log.debug("(cmdGetStandardEventLog)obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);
								param = obisCode+"|"+classId+"|"+attrId+"|null|null|"+value;
							}
							else if ( cmd.equals("cmdGetTamperingLog")){
								String obisCode = cgw.convertObis(OBIS.TAMPER_EVENT.getCode());																		
								log.debug("(cmdGetTamperingLog)obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);
								param = obisCode+"|"+classId+"|"+attrId+"|null|null|"+value;
							}
							else if ( cmd.equals("cmdGetPowerFailureLog")){
								String obisCode = cgw.convertObis(OBIS.POWERFAILURE_LOG.getCode());
																				
								log.debug("(cmdGetPowerFailureLog)obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);
								param = obisCode+"|"+classId+"|"+attrId+"|null|null|"+value;					
							}
							else if ( cmd.equals("cmdGetControlLog")){
								String obisCode = cgw.convertObis(OBIS.CONTROL_LOG.getCode());
								log.debug("(cmdGetControlLog)obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);
								param = obisCode+"|"+classId+"|"+attrId+"|null|null|"+value;	
							}
							else if ( cmd.equals("cmdGetPQLog")){
								String obisCode = cgw.convertObis(OBIS.POWER_QUALITY_LOG.getCode());
								log.debug("(cmdGetPQLog)obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);
								param = obisCode+"|"+classId+"|"+attrId+"|null|null|"+value;
							}
							else if ( cmd.equals("cmdGetFWUpgradeLog")){
								String obisCode = cgw.convertObis(OBIS.FIRMWARE_UPGRADE_LOG.getCode());
								log.debug("(cmdGetFWUpgradeLog)obisCode => " + obisCode + ", classId => " + classId + ", attributeId => " + attrId);
								param = obisCode+"|"+classId+"|"+attrId+"|null|null|"+value;						
							}
																	
							// Send SMS to MBB Modem -> Succeed to Sending -> Save the AsyncCommandLog
							paramMap.put("paramGet", param);
							smsResult = sendSmsForCmdServer(mmiu, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "cmdMeterParamGet", paramMap);
							
							// Check the Result
							if (smsResult != null) {
								Map<String, Object> tempMap = new HashMap<String, Object>();
								tempMap.put("meterId", mdsId);
								tempMap.put("rtnStr", "DONE! ");
								tempMap.put("viewMsg", smsResult);
								rtnStrList.add(tempMap);
								status = ResultStatus.SUCCESS;
							}
							else {
								log.error("cmd[" + cmd + "] is not support");
								Map<String, Object> tempMap = new HashMap<String, Object>();
								tempMap.put("meterId", mdsId);
								tempMap.put("rtnStr", "FAIL : invalid cmd [" + cmd + "]");
								rtnStrList.add(tempMap);
							}
						}else{
							// No mobile number in modem object
							log.warn(String.format("[" + cmd + "] Phone number is empty"));
							Map<String,Object> tempMap = new HashMap<String,Object>();
							tempMap.put("meterId", mdsId);
			        		tempMap.put("rtnStr", "FAIL : Phone number is empty");
			        		rtnStrList.add(tempMap);
						}
					}catch (Exception e) {
						log.error(e, e);
						Map<String, Object> tempMap = new HashMap<String, Object>();
						tempMap.put("meterId", mdsId);
						tempMap.put("rtnStr", "FAIL : " + e.getMessage());
						rtnStrList.add(tempMap);
					}								
					// ~Inside Try (E)
				}
				else if( modem.getModemType() == ModemType.MMIU || modem.getModemType() == ModemType.SubGiga ){
					try{			
						if ( cmd.equals("cmdGetStandardEventLog")){
							result = cmdOperationUtil.cmdGetStandardEventLog(modem.getDeviceSerial(),fromDate, toDate, modem.getProtocolType());
						}
						else if ( cmd.equals("cmdGetTamperingLog")){
							result = cmdOperationUtil.cmdGetTamperingLog(modem.getDeviceSerial(),fromDate, toDate, modem.getProtocolType());		
						}
						else if ( cmd.equals("cmdGetPowerFailureLog")){
							result = cmdOperationUtil.cmdGetPowerFailureLog(modem.getDeviceSerial(),fromDate, toDate, modem.getProtocolType());							
						}
						else if ( cmd.equals("cmdGetControlLog")){
							result = cmdOperationUtil.cmdGetControlLog(modem.getDeviceSerial(),fromDate, toDate, modem.getProtocolType());		
						}
						else if ( cmd.equals("cmdGetPQLog")){
							result = cmdOperationUtil.cmdGetPQLog(modem.getDeviceSerial(),fromDate, toDate, modem.getProtocolType());		
						}
						else if ( cmd.equals("cmdGetFWUpgradeLog")){
							result = cmdOperationUtil.cmdGetFWUpgradeLog(modem.getDeviceSerial(),fromDate, toDate, modem.getProtocolType());							
						}

						if (result != null) {
							Map<String, Object> tempMap = new HashMap<String, Object>();
							tempMap.put("meterId", mdsId);
							tempMap.put("rtnStr", "DONE! ");
							tempMap.put("viewMsg", result);
							rtnStrList.add(tempMap);
							status = ResultStatus.SUCCESS;
						}
						else {
							log.error("cmd[" + cmd + "] is not support");
							Map<String, Object> tempMap = new HashMap<String, Object>();
							tempMap.put("meterId", mdsId);
							tempMap.put("rtnStr", "FAIL : invalid cmd [" + cmd + "]");
							rtnStrList.add(tempMap);
						}
					} catch (Exception e) {
						log.error(e, e);
						Map<String, Object> tempMap = new HashMap<String, Object>();
						tempMap.put("meterId", mdsId);
						tempMap.put("rtnStr", "FAIL : " + e.getMessage());
						rtnStrList.add(tempMap);
					}
				}
				
				else {
					Map<String,Object> tempMap = new HashMap<String,Object>();
					tempMap.put("meterId", mdsId);
					tempMap.put("rtnStr", "FAIL : Target ID null!");
					rtnStrList.add(tempMap);
				}
			}
		} catch (Exception e) {
			log.error(e, e);
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("meterId", mdsId);
			tempMap.put("rtnStr", "FAIL : " + e.getMessage());
			rtnStrList.add(tempMap);
		}
        if ( meter != null ){
    		Code operationCode = codeManager.getCodeByCode("8.1.6"); // OBIS Get
    		if (operationCode != null) {
    			operationLogManager.saveOperationLog(meter.getSupplier(), 
    					meter.getMeterType(), meter.getMdsId(), loginId,
						operationCode, status.getCode(), status.name());
    		}

        }
		mav.addObject("rtnStrList", rtnStrList);
		return mav;
	}
		
	
	@RequestMapping(value = "/gadget/device/command/getAsyncLog")
	public ModelAndView getAsyncLog(String meterInfoArr) {
		// meterInfo에는 mdsId와 trId가 들어있다.
		ModelAndView mav = new ModelAndView("jsonView");
		List<Map<String, Object>> rtnStrList = new ArrayList<Map<String, Object>>();

		JSONArray jsonArr = null;
		try {
			if (meterInfoArr == null || meterInfoArr.isEmpty()) {
				jsonArr = new JSONArray();
			} else {
				jsonArr = JSONArray.fromObject(meterInfoArr);
			}

			List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();
			Map<String, Object> paramMap = new HashMap<String, Object>();
			if (jsonArr.size() > 0) {
				Object[] tempJson = jsonArr.toArray();
				for (int i = 0; i < tempJson.length; i++) {
					Map<String, Object> jsonMap = (Map<String, Object>) tempJson[i];
					String mdsId = jsonMap.get("meterId").toString();
					String trId = jsonMap.get("trId").toString();
					String recordId = jsonMap.get("recordId").toString();
					paramMap.put("mdsId", mdsId);
					paramMap.put("trId", trId);
					paramMap.put("recordId", recordId);
					paramList.add(paramMap);
				}
			}

			try {
				// 응답을 기다리기 위한 sleep
				if (paramList.size() > 0) {
					Thread.sleep(40000);
					for (int i = 0; i < paramList.size(); i++) {
						Map<String, Object> resultMap = paramList.get(i);

						String mdsId = (String) resultMap.get("mdsId");
						Meter meter = meterManager.getMeter(mdsId);
						Integer lastResult = asyncCommandLogManager.getCmdStatusByTrId(
								meter.getModem().getDeviceSerial(),
								Long.parseLong(String.valueOf(resultMap.get("trId"))));

						if (TR_STATE.Success.getCode() != lastResult) {
							Map<String, Object> tempMap = new HashMap<String, Object>();
							tempMap.put("meterId", resultMap.get("meterId"));
							tempMap.put("rtnStr", "FAIL : Send SMS Fail (Communication Error)");
							tempMap.put("recordId", resultMap.get("recordId"));
							rtnStrList.add(tempMap);
						} else {
							List<Map<String, Object>> viewList = new ArrayList<Map<String, Object>>();
							Map<String, Object> viewMap = new HashMap<String, Object>();
							Long trId = Long.parseLong(String.valueOf(resultMap.get("trId")));
							List<AsyncCommandResult> acplist = asyncCommandLogManager
									.getCmdResults(meter.getModem().getDeviceSerial(), trId, null);
							if (acplist == null || acplist.size() <= 0) {
								viewMap.put("meterId", resultMap.get("meterId"));
								viewMap.put("paramType", "Result");
								viewMap.put("paramValue", "result data is Empty");
								viewList.add(viewMap);
							} else {
								for (int j = 0; j < acplist.size(); j++) {
									AsyncCommandResult param = acplist.get(j);
									viewMap = new HashMap<String, Object>();
									viewMap.put("meterId", mdsId);
									viewMap.put("paramType", param.getResultType());
									viewMap.put("paramValue", param.getResultValue());
									viewList.add(viewMap);
								}
							}

							Map<String, Object> tempMap = new HashMap<String, Object>();
							tempMap.put("meterId", resultMap.get("meterId"));
							tempMap.put("recordId", resultMap.get("recordId"));
							tempMap.put("rtnStr", "DONE : Send SMS Command.");
							tempMap.put("viewMsg", viewList);
							rtnStrList.add(tempMap);
						}

					}
				}

			} catch (Exception e) {
				log.error(e, e);
			}
		} catch (Exception e) {
			log.error(e, e);
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("meterId", "Unknow Error");
			tempMap.put("rtnStr", "FAIL : " + e.getMessage());
			rtnStrList.add(tempMap);
		}

		mav.addObject("rtnStrList", rtnStrList);
		return mav;
	}

	/**
	 * 
	 * ECG G2 SMS 연동을 위한 SMS 임시 연동
	 * 
	 * @param
	 * @param
	 * @return
	 */
	@Transactional(readOnly = false)
	@RequestMapping(value = "/gadget/device/command/cmdLine")
	public ModelAndView cmdLine(HttpServletRequest request, HttpServletResponse response) {

		ModelAndView mav = new ModelAndView("jsonView");
		ResultStatus status = ResultStatus.FAIL;

		String finalFilePath = null;

		String loginId = null;
		String modemId = null;
		String cmd = null;
		String[] param = null;

		Boolean isOTA = (request instanceof MultipartHttpServletRequest);
		try {
			if (isOTA) {
				MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) request;
				MultipartFile multipartFile = multiReq.getFile("otaFile");

				byte[] fileBinary = multipartFile.getBytes();
				String filePath = multipartFile.getOriginalFilename();
				String fileName = filePath.substring(0, filePath.lastIndexOf("."));
				String ext = filePath.substring(filePath.lastIndexOf(".") + 1);

				// 파일 저장
				String homePath = CommandProperty.getProperty("firmware.dir");
				finalFilePath = makeFirmwareDirectory(homePath, fileName, ext, true);

				log.info(String.format("Save firmware file - %s", finalFilePath));
				FileOutputStream foutStream = new FileOutputStream(finalFilePath, true);
				param = new String[1];
				param[0] = finalFilePath;
				foutStream.write(fileBinary);
				foutStream.flush();
				foutStream.close();
				log.info(String.format("firmware file Save complete"));
			}

			loginId = request.getParameter("loginId");
			modemId = request.getParameter("modemId");
			cmd = request.getParameter("cmd");

			String[] cmdList = cmd.split("/");
			cmd = cmdList[0];

			if (!(cmd.equals("cmdResetModem") || cmd.equals("cmdFactorySetting")
					|| cmd.equals("cmdReadModemConfiguration") || cmd.equals("cmdIdentifyDevice")
					|| cmd.equals("cmdSetTime") || cmd.equals("cmdSetModemResetInterval")
					|| cmd.equals("cmdSetMeteringInterval") || cmd.equals("cmdServerIpPort") || cmd.equals("cmdSetApn")
					|| cmd.equals("cmdOTAStart") || cmd.equals("cmdSetMeterTime"))) {
				mav.addObject("rtnStr", "InValid Command.");
				return mav;
			}

			for (int i = 1; i < cmdList.length; i++) {
				if (i == 1)
					param = new String[cmdList.length - 1];
				param[i - 1] = cmdList[i];
			}

			if (modemId == null || "".equals(modemId)) {
				status = ResultStatus.INVALID_PARAMETER;
				mav.addObject("rtnStr", "FAIL : Target ID null!");
				return mav;
			}

			Modem modem = modemManager.getModem(Integer.parseInt(modemId));
			MMIU mmiu = (MMIU) modem;
			String mobileNo = mmiu.getPhoneNumber();

			if (mobileNo == null || "".equals(mobileNo)) {
				mav.addObject("rtnStr", "FAIL : Phone number is empty!");
				return mav;
			}

			if (!Protocol.SMS.equals(mmiu.getProtocolType())) {
				mav.addObject("rtnStr", "FAIL : Invalid ProtocolType!");
				return mav;
			}

			if (!"0102".equals(mmiu.getProtocolVersion())) {
				mav.addObject("rtnStr", "FAIL : Invalid ProtoclVersion!");
				return mav;
			}

			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
			String ipAddr = prop.getProperty("GG.sms.ipAddr") == null ? "" : prop.getProperty("GG.sms.ipAddr").trim();
			String port = prop.getProperty("GG.sms.port") == null ? "" : prop.getProperty("GG.sms.port").trim();
			String smsClassPath = prop.getProperty("smsClassPath");

			if ("".equals(ipAddr) || "".equals(port)) {
				mav.addObject("rtnStr", "FAIL : Invalid Ip Address or port!");
				return mav;
			}

			int seq = new Random().nextInt(100) & 0xFF;
			String oid = null;
			String messageId = null;
			if ("cmdServerIpPort".equals(cmd)) {
				oid = "103.1.2";
				String smsMsg = cmdMsg((byte) seq, oid, ipAddr.replaceAll("\\.", ","), port);

				SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();

				Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
				messageId = (String) m.invoke(obj, mobileNo.replace("-", "").trim(), smsMsg, prop);
				if ("fail".equals(messageId)) {
					mav.addObject("rtnStr", "FAIL : Send SMS Fail");
				} else {
					mav.addObject("rtnStr", "SUCCESS : Send SMS Command(" + cmd + ").");
				}
			} else {
				oid = "244.0.0";
				String smsMsg = cmdMsg((byte) seq, oid, ipAddr.replaceAll("\\.", ","), port);

				SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();

				Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
				messageId = (String) m.invoke(obj, mobileNo.replace("-", "").trim(), smsMsg, prop);

				if ("fail".equals(messageId)) {
					mav.addObject("rtnStr", "FAIL : Send SMS Fail");
					log.debug("SMS Send Fail");
				} else {
					mav.addObject("rtnStr", "SUCCESS : Send SMS Command(" + cmd + ").");
					log.debug("SMS Send Success");
					saveAsyncCommand(modem.getDeviceSerial(), cmd, param, TimeUtil.getCurrentTime());

					Integer lastStatus = null;
					if ("cmdOTAStart".equals(cmd)) {
						// OTA의 경우 5분의 delay
						Thread.sleep(300000);
						lastStatus = asyncCommandLogManager.getCmdStatus(modem.getDeviceSerial(), "cmdOTAStart");
					} else {
						// 상태가 바뀌는 시간을 기다려주기 위해 30초 sleep
						Thread.sleep(30000);
						lastStatus = asyncCommandLogManager.getCmdStatus(modem.getDeviceSerial(), cmd);
					}

					if (TR_STATE.Success.getCode() != lastStatus) {
						mav.addObject("rtnStr", "FAIL : Communication Error(" + cmd + ")");
						log.debug("Fail : Command Regult[" + cmd + "]");
					} else {
						log.debug("Success : Command Regult[" + cmd + "]");
					}
				}
			}

		} catch (Exception e) {
			log.error(e, e);
			mav.addObject("rtnStr", "FAIL : Send SMS Fail.");
			return mav;
		}

		return mav;

	}

/*	*//**
	 * MOE GPRS 모뎀을 위한 SMS전송
	 * 
	 * @param request
	 * @param response
	 * @return
	 *//*
	@Transactional(readOnly = false)
	@RequestMapping(value = "/gadget/device/command/cmdLineMOE")
	public ModelAndView cmdLineMOE(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mav = new ModelAndView("jsonView");
		String finalFilePath = null;
		String modemId = request.getParameter("modemId");
		String cmd = request.getParameter("cmd");
		String loginId = request.getParameter("loginId");

		Map<String, String> paramMap = null;
		// String[] param = null;

		Boolean isOTA = (request instanceof MultipartHttpServletRequest);
		log.debug("CmdInfo modemId = " + modemId + ", cmd = " + cmd + ", loginId = " + loginId + ",  isOTA = " + isOTA);
		String rtnMessage = "";
		String[] cmdList = cmd.split("/");
		cmd = cmdList[0];

		if (!(cmd.equals("cmdResetModem") || cmd.equals("cmdUploadMeteringData") || cmd.equals("cmdFactorySetting")
				|| cmd.equals("cmdReadModemConfiguration") || cmd.equals("cmdIdentifyDevice")
				|| cmd.equals("cmdSetTime") || cmd.equals("cmdSetModemResetInterval")
				|| cmd.equals("cmdSetMeteringInterval") || cmd.equals("cmdSetServerIpPort") || cmd.equals("cmdSetApn")
				|| cmd.equals("cmdOTAStart") || cmd.equals("cmdCurrentValuesMetering")
				|| cmd.equals("cmdGetMeterStatus") || cmd.equals("cmdReadModemEventLog")

				|| cmd.equals("cmdMeterOTAStart") || cmd.equals("cmdGetMeterFWVersion") || cmd.equals("cmdAlarmReset")
				|| cmd.equals("cmdGetBillingCycle") || cmd.equals("cmdSetBillingCycle") || cmd.equals("cmdTOUSet")
				|| cmd.equals("cmdDemandPeriod"))) {
			rtnMessage = "FAIL : InValid Command.";
		} else if (modemId == null || "".equals(modemId)) {
			rtnMessage = "FAIL : Target ID null!";
		} else {
			Modem modem = modemManager.getModem(Integer.parseInt(modemId));
			MMIU mmiu = (MMIU) modem;
			String modemDeviceSerial = mmiu.getDeviceSerial();

			if (modemDeviceSerial == null || "".equals(modemDeviceSerial)) {
				rtnMessage = "FAIL : Phone number is empty!";
			} else if (!Protocol.SMS.equals(mmiu.getProtocolType())) {
				rtnMessage = "FAIL : Invalid ProtocolType!";
			} else if (!"0102".equals(mmiu.getProtocolVersion())) {
				rtnMessage = "FAIL : Invalid ProtoclVersion!";
			} else {
				try {
					Supplier supplier = modem.getSupplier();
					Code targetTypeCode = codeDao.getCodeIdByCodeObject("1.2.1.11"); // MMIU
					Code operationCode = null;
					if (cmd.equals("cmdResetModem")) {
						if (cmdList.length == 2 && !cmdList[1].equals("")) {
							paramMap = new HashMap<String, String>();
							paramMap.put("oid", "244.0.0"); // GPRS Connect :
															// OID - 244.0.0
							paramMap.put("delay_time", cmdList[1]); // Delay
																	// Time (s),
																	// 0 => 즉시
							operationCode = codeDao.getCodeIdByCodeObject("8.2.1"); // Reset
																					// Modem
						} else {
							log.warn(String.format("[" + cmd + "] Invalid Parameter"));
							rtnMessage = "FAIL : Invalid Parameter!";
							mav.addObject("rtnStr", rtnMessage);
							return mav;
						}
					} else if (cmd.equals("cmdUploadMeteringData")) {
						paramMap = new HashMap<String, String>();
						paramMap.put("oid", "244.0.0"); // GPRS Connect : OID -
														// 244.0.0
						operationCode = codeDao.getCodeIdByCodeObject("8.2.27"); // Upload
																					// Metering
																					// Data
					} else if (cmd.equals("cmdFactorySetting")) {
						paramMap = new HashMap<String, String>();
						paramMap.put("oid", "244.0.0"); // GPRS Connect : OID -
														// 244.0.0
						paramMap.put("code", "0314"); // 공장초기화 Ox0314
						operationCode = codeDao.getCodeIdByCodeObject("8.2.28"); // Factory
																					// Setting
					} else if (cmd.equals("cmdReadModemConfiguration")) {
						paramMap = new HashMap<String, String>();
						paramMap.put("oid", "244.0.0"); // GPRS Connect : OID -
														// 244.0.0
						operationCode = codeDao.getCodeIdByCodeObject("8.2.8"); // Read
																				// Modem
																				// Configuration
					}
					// Iraq MOE 프로젝트에서는 시스템적으로 미터,모뎀식별용으로만 사용. Command로 제공하지 않음.
					
					 * else if (cmd.equals("cmdIdentifyDevice")) { paramMap =
					 * new HashMap<String, String>(); paramMap.put("oid",
					 * "244.0.0"); // GPRS Connect : OID - 244.0.0 operationCode
					 * = codeDao.getCodeIdByCodeObject("8.2.29"); // Identify
					 * Device }
					 
					else if (cmd.equals("cmdSetTime")) {
						paramMap = new HashMap<String, String>();
						paramMap.put("oid", "244.0.0"); // GPRS Connect : OID -
														// 244.0.0

						if (cmdList.length == 2 && !cmdList[1].equals("")) {
							paramMap.put("time", cmdList[1]); // Time
						}

						operationCode = codeDao.getCodeIdByCodeObject("8.2.30"); // Set
																					// Time
					} else if (cmd.equals("cmdSetModemResetInterval")) {
						if (cmdList.length == 2 && !cmdList[1].equals("")) {
							paramMap = new HashMap<String, String>();
							paramMap.put("oid", "244.0.0"); // GPRS Connect :
															// OID - 244.0.0
							paramMap.put("interval", cmdList[1]); // Interval
																	// (m)
							operationCode = codeDao.getCodeIdByCodeObject("8.2.21"); // Set
																						// Modem
																						// Reset
																						// Interval
						} else {
							log.warn(String.format("[" + cmd + "] Invalid Parameter"));
							rtnMessage = "FAIL : Invalid Parameter!";
							mav.addObject("rtnStr", rtnMessage);
							return mav;
						}
					} else if (cmd.equals("cmdSetMeteringInterval")) {
						if (cmdList.length == 2 && !cmdList[1].equals("")) {
							paramMap = new HashMap<String, String>();
							paramMap.put("oid", "244.0.0"); // GPRS Connect :
															// OID - 244.0.0
							paramMap.put("interval", cmdList[1]); // Interval
																	// (m)
							operationCode = codeDao.getCodeIdByCodeObject("8.2.19"); // Set
																						// Metering
																						// Interval
						} else {
							log.warn(String.format("[" + cmd + "] Invalid Parameter"));
							rtnMessage = "FAIL : Invalid Parameter!";
							mav.addObject("rtnStr", rtnMessage);
							return mav;
						}
					} else if ("cmdSetServerIpPort".equals(cmd)) {
						if (cmdList.length == 3 && !cmdList[1].equals("") && !cmdList[2].equals("")) {
							paramMap = new HashMap<String, String>();
							paramMap.put("oid", "103.1.2"); // OID - 103.1.2
							paramMap.put("server_ip", cmdList[1]); // 서버 IP
							paramMap.put("server_port", cmdList[2]); // 서버 Port

							operationCode = codeDao.getCodeIdByCodeObject("8.2.26"); // Server
																						// IP,Port
																						// Change
						} else {
							log.warn(String.format("[" + cmd + "] Invalid Parameter"));
							rtnMessage = "FAIL : Invalid Parameter!";
							mav.addObject("rtnStr", rtnMessage);
							return mav;
						}
					} else if (cmd.equals("cmdSetApn")) {
						if (cmdList.length == 4 && !cmdList[1].equals("") && !cmdList[2].equals("")
								&& !cmdList[3].equals("")) {
							paramMap = new HashMap<String, String>();
							paramMap.put("oid", "244.0.0"); // GPRS Connect :
															// OID - 244.0.0
							paramMap.put("apn_address", cmdList[1]); // APN
																		// Address
							paramMap.put("apn_id", cmdList[2]); // APN ID
							paramMap.put("apn_password", cmdList[2]); // APN
																		// Password

							operationCode = codeDao.getCodeIdByCodeObject("8.2.31"); // Set
																						// APN
						} else {
							log.warn(String.format("[" + cmd + "] Invalid Parameter"));
							rtnMessage = "FAIL : Invalid Parameter!";
							mav.addObject("rtnStr", rtnMessage);
							return mav;
						}
					} else if (cmd.equals("cmdOTAStart") && isOTA) {
						MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) request;
						MultipartFile multipartFile = multiReq.getFile("otaFile");

						String otaType = "MODEM";
						byte[] fileBinary = multipartFile.getBytes();
						String filePath = multipartFile.getOriginalFilename();
						String[] fileInfo = filePath.split(",");
						String prodName = fileInfo[0];
						String modemModel = fileInfo[1];
						String fwVersion = fileInfo[2];
						String fileName = fwVersion.substring(0, fwVersion.lastIndexOf("."));
						String ext = fwVersion.substring(fwVersion.lastIndexOf(".") + 1);

						byte[] imgCrc16 = CRCUtil.Calculate_ZigBee_Crc(fileBinary, (char) 0x0000);

						// 파일 저장
						String osName = System.getProperty("os.name");
						String homePath = "";
						if (osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0) {
							homePath = CommandProperty.getProperty("moe.firmware.window.dir");
						} else {
							homePath = CommandProperty.getProperty("moe.firmware.dir");
						}
						finalFilePath = makeFirmwareDirectoryForEMnV(homePath,
								otaType + "/" + prodName + "/" + modemModel + "/", fileName, ext, true);

						paramMap = new HashMap<String, String>();
						paramMap.put("oid", "244.0.0"); // GPRS Connect : OID -
														// 244.0.0
						paramMap.put("fw_path", finalFilePath); // 서버에 저장된 파일경로
						paramMap.put("fw_size", Long.toString(multipartFile.getSize())); // 파일사이즈
						paramMap.put("fw_crc", Hex.decode(imgCrc16)); // CRC16
						paramMap.put("fw_model_name", modemModel); // Model Name
						paramMap.put("fw_version", fileName); // 버전

						log.info(String.format("[Save firmware]" + " filePath=" + paramMap.get("fw_path") + " fileSize="
								+ paramMap.get("fw_size") + " fileName=" + paramMap.get("fw_version") + " FW_CRC16="
								+ paramMap.get("fw_crc")));

						FileOutputStream foutStream = new FileOutputStream(finalFilePath, true);
						foutStream.write(fileBinary);
						foutStream.flush();
						foutStream.close();
						log.info(String.format("Modem firmware file Save complete."));
						operationCode = codeDao.getCodeIdByCodeObject("8.2.13"); // OTA
					} else if (cmd.equals("cmdMeterOTAStart") && isOTA) {
						MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) request;
						MultipartFile multipartFile = multiReq.getFile("otaFile");
						String otaType = "METER";

						byte[] fileBinary = multipartFile.getBytes();
						String filePath = multipartFile.getOriginalFilename();
						String[] fileInfo = filePath.split(",");
						String prodName = fileInfo[0];
						String meterModel = fileInfo[1];
						String fwVersion = fileInfo[2];
						String fileName = fwVersion.substring(0, fwVersion.lastIndexOf("."));
						String ext = fwVersion.substring(fwVersion.lastIndexOf(".") + 1);

						byte[] imgCrc16 = CRCUtil.Calculate_ZigBee_Crc(fileBinary, (char) 0x0000);

						// 파일 저장
						String osName = System.getProperty("os.name");
						String homePath = "";
						if (osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0) {
							homePath = CommandProperty.getProperty("moe.firmware.window.dir");
						} else {
							homePath = CommandProperty.getProperty("moe.firmware.dir");
						}
						finalFilePath = makeFirmwareDirectoryForEMnV(homePath,
								otaType + "/" + prodName + "/" + meterModel + "/", fileName, ext, true);

						paramMap = new HashMap<String, String>();
						paramMap.put("oid", "244.0.0"); // GPRS Connect : OID -
														// 244.0.0
						paramMap.put("fw_path", finalFilePath); // 서버에 저장된 파일경로
						paramMap.put("fw_size", Long.toString(multipartFile.getSize())); // 파일사이즈
						paramMap.put("fw_crc", Hex.decode(imgCrc16)); // CRC16
						paramMap.put("fw_model_name", meterModel); // Model Name
						paramMap.put("fw_version", fileName); // 버전
						if (cmdList.length == 2 && !cmdList[1].equals("")) {
							paramMap.put("take_over", cmdList[1].equals("true") ? "true" : "false");
						}

						log.info("[Save firmware]" + paramMap.toString());

						FileOutputStream foutStream = new FileOutputStream(finalFilePath, true);
						foutStream.write(fileBinary);
						foutStream.flush();
						foutStream.close();
						log.info(String.format("Meter firmware file Save complete."));
						operationCode = codeDao.getCodeIdByCodeObject("8.2.13"); // OTA
					} else if (cmd.equals("cmdCurrentValuesMetering")) {
						paramMap = new HashMap<String, String>();
						paramMap.put("oid", "244.0.0"); // GPRS Connect : OID -
														// 244.0.0
						operationCode = codeDao.getCodeIdByCodeObject("8.2.33"); // Current
																					// Values
																					// Metering
					} else if (cmd.equals("cmdGetMeterStatus")) {
						paramMap = new HashMap<String, String>();
						paramMap.put("oid", "244.0.0"); // GPRS Connect : OID -
														// 244.0.0
						operationCode = codeDao.getCodeIdByCodeObject("8.2.34"); // Get
																					// Meter
																					// Status
					} else if (cmd.equals("cmdReadModemEventLog")) {
						if (cmdList.length == 2 && !cmdList[1].equals("")) {
							paramMap = new HashMap<String, String>();
							paramMap.put("oid", "244.0.0"); // GPRS Connect :
															// OID - 244.0.0
							paramMap.put("read_cnt", cmdList[1]); // Read count

							operationCode = codeDao.getCodeIdByCodeObject("8.2.5"); // Read
																					// Event
																					// Log
						} else {
							log.warn(String.format("[" + cmd + "] Invalid Parameter"));
							rtnMessage = "FAIL : Invalid Parameter!";
							mav.addObject("rtnStr", rtnMessage);
							return mav;
						}
					} else if (cmd.equals("cmdGetMeterFWVersion")) {
						paramMap = new HashMap<String, String>();
						paramMap.put("oid", "244.0.0"); // GPRS Connect : OID -
														// 244.0.0
						operationCode = codeDao.getCodeIdByCodeObject("8.1.35"); // Get
																					// Meter
																					// F/W
																					// Version
					} else if (cmd.equals("cmdAlarmReset")) {
						paramMap = new HashMap<String, String>();
						paramMap.put("oid", "244.0.0"); // GPRS Connect : OID -
														// 244.0.0
						operationCode = codeDao.getCodeIdByCodeObject("8.1.36"); // Alarm
																					// Reset
					} else if (cmd.equals("cmdGetBillingCycle")) {
						paramMap = new HashMap<String, String>();
						paramMap.put("oid", "244.0.0"); // GPRS Connect : OID -
														// 244.0.0
						operationCode = codeDao.getCodeIdByCodeObject("8.1.37"); // Billing
																					// Cycle
																					// Setting
					} else if (cmd.equals("cmdSetBillingCycle")) {
						if (cmdList.length == 3 && !cmdList[1].equals("") && !cmdList[2].equals("")) {
							paramMap = new HashMap<String, String>();
							paramMap.put("oid", "244.0.0"); // GPRS Connect :
															// OID - 244.0.0
							paramMap.put("time", cmdList[1]); // Time
							paramMap.put("day", cmdList[2]); // day

							boolean temp = false;
							try {
								SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
								Date date = sdf.parse(cmdList[1]);
								int day = Integer.parseInt(cmdList[2]);
							} catch (Exception e) {
								log.warn(String.format(
										"[" + cmd + "] Invalid Time or Day.[" + cmdList[1] + "][" + cmdList[2] + "]"));
								rtnMessage = "FAIL : Invalid Time or Day!";
								mav.addObject("rtnStr", rtnMessage);
								return mav;
							}

							int day = Integer.parseInt(cmdList[2]);
							if (day < 1 || 31 < day) {
								log.warn(String.format("[" + cmd + "] Invalid Day [" + cmdList[2] + "]"));
								rtnMessage = "FAIL : Invalid  Day!";
								mav.addObject("rtnStr", rtnMessage);
								return mav;
							}

							operationCode = codeDao.getCodeIdByCodeObject("8.1.37"); // Billing
																						// Cycle
																						// Setting
						} else {
							log.warn(String.format("[" + cmd + "] Invalid Parameter"));
							rtnMessage = "FAIL : Invalid Parameter!";
							mav.addObject("rtnStr", rtnMessage);
							return mav;
						}
					} else if (cmd.equals("cmdTOUSet")) {
						MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) request;
						String calendarNamePassive = request.getParameter("calendarNamePassive");
						String startingDate = request.getParameter("startingDate"); // yyyyMMddHHmmss
						String otaType = "TOU";

						paramMap = new HashMap<String, String>();

						SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

						Iterator<String> it = multiReq.getFileNames();
						while (it.hasNext()) {
							String key = it.next();
							MultipartFile mFile = multiReq.getFile(key);

							byte[] fileBinary = mFile.getBytes();
							String filePath = mFile.getOriginalFilename();

							
							 * Season Profile File : key = seasonFile Week
							 * Profile File : key = weekFile Day Profile File :
							 * key = dayFile - 파일명 : 제조사,미터모델,Profile타입.dat -
							 * ex) LS,LSIQ-3PP,SEASON.dat
							 
							String[] fileInfo = filePath.split(",");
							String prodName = fileInfo[0];
							String meterModel = fileInfo[1];
							String profileType = fileInfo[2];
							String fileName = profileType.substring(0, profileType.lastIndexOf("."));
							String ext = "xml";

							// 파일 저장
							String osName = System.getProperty("os.name");
							String homePath = "";
							if (osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0) {
								homePath = CommandProperty.getProperty("moe.firmware.window.dir");
							} else {
								homePath = CommandProperty.getProperty("moe.firmware.dir");
							}
							finalFilePath = makeFirmwareDirectoryForEMnV(homePath,
									otaType + "/" + prodName + "/" + meterModel + "/", fileName, ext, true);

							paramMap.put(key, finalFilePath); // 서버에 저장된 파일경로
							paramMap.put("meter_model", meterModel); // Model
																		// Name

							Path fPath = Paths.get(finalFilePath);
							Files.write(fPath, fileBinary);
							log.info("### " + String.format("[" + key + "] file(" + fileBinary.length
									+ "byte) Save complete - " + paramMap.toString()));

							
							 * Validation
							 
							try {
								Source xsdSource = new StreamSource(getClass().getClassLoader()
										.getResourceAsStream("xsd/moe_tou/TOU_" + key + ".xsd"));
								Source xmlSource = new StreamSource(fPath.toFile());

								Schema schema = factory.newSchema(xsdSource);
								Validator validator = schema.newValidator();

								validator.validate(xmlSource);
								System.out.println(key + " is valid.");
							} catch (SAXException e) {
								Files.deleteIfExists(fPath);
								log.warn(String.format("[" + cmd + "]" + key + " is not found - " + e.getMessage()));
								rtnMessage = key + " is not found";
								mav.addObject("rtnStr", rtnMessage);
								return mav;
							} catch (IOException e) {
								Files.deleteIfExists(fPath);
								log.warn(String.format("[" + cmd + "] is not valid - " + e.getMessage()));
								rtnMessage = key + " is not valid because - " + e.getMessage();
								mav.addObject("rtnStr", rtnMessage);
								return mav;
							}
						}

						paramMap.put("oid", "244.0.0"); // GPRS Connect : OID -
														// 244.0.0
						paramMap.put("calendarNamePassive", calendarNamePassive);
						paramMap.put("startingDate", startingDate);

						operationCode = codeDao.getCodeIdByCodeObject("8.1.38"); // Meter
																					// TOU
																					// Setting
					} else if (cmd.equals("cmdDemandPeriod")) {
						if (cmdList.length == 3 && !cmdList[1].equals("") && !cmdList[2].equals("")) {
							paramMap = new HashMap<String, String>();
							paramMap.put("oid", "244.0.0"); // GPRS Connect :
															// OID - 244.0.0
							paramMap.put("period", cmdList[1]); // Period
							paramMap.put("numberOfPeriod", cmdList[2]); // Number
																		// of
																		// period
							operationCode = codeDao.getCodeIdByCodeObject("8.1.39"); // Meter
																						// Demand
																						// Period
																						// Setting
						} else {
							log.warn(String.format("[" + cmd + "] Invalid Parameter"));
							rtnMessage = "FAIL : Invalid Parameter!";
							mav.addObject("rtnStr", rtnMessage);
							return mav;
						}
					}

					else {
						throw new Exception("Unknown Command.");
					}

					log.info("[" + cmd + "] param = " + paramMap.toString());

					
					 * 서버에서 모뎀으로 SMS를 보낸뒤 모뎀이 서버에 접속하여 수행해야할 Command가 무엇인지 구분할
					 * 방법이 따로 없기 때문에 Transaction ID를 사용하여 구분하도록 한다.
					 
					Long maxTrId = asyncCommandLogManager.getMaxTrId(modem.getDeviceSerial());
					String trnxId;
					if (maxTrId != null) {
						trnxId = String.format("%08d", maxTrId.intValue() + 1);
					} else {
						trnxId = "00000001";
					}

					
					 * 비동기 명령 저장 : SMS발송보다 먼저 저장함.
					 
					saveAsyncCommandForMOE(modem.getDeviceSerial(), Long.parseLong(trnxId), cmd, paramMap,
							TimeUtil.getCurrentTime());

					
					 * SMS 발송
					 
					String messageId = sendSMSForMOE(paramMap.get("oid"), trnxId, modemDeviceSerial, cmd);

					
					 * 결과 처리
					 
					ResultStatus status = ResultStatus.SUCCESS;
					if (messageId.equals("fail")) {
						status = ResultStatus.FAIL;
						log.debug("FAIL : [" + modemDeviceSerial + "]Send SMS Fail.");
						rtnMessage = "FAIL : [" + modemDeviceSerial + "]Send SMS Fail.\n";
					} else if (messageId.equals("error")) {
						status = ResultStatus.FAIL;
						log.debug("FAIL : Invalid Ip Address or Port!");
						rtnMessage = "FAIL : Invalid Ip Address or Port!\n";
					} else {
						// 상태가 바뀌는 시간을 기다려주기 위해 sleep
						// Thread.sleep(cmd.equals("cmdOTAStart") ? 600000 :
						// 120000);
						Thread.sleep(210000);
						Integer lastStatus = asyncCommandLogManager.getCmdStatus(modem.getDeviceSerial(), cmd);

						if (TR_STATE.Success.getCode() != lastStatus) {
							log.debug("FAIL : [" + modemDeviceSerial + "] Communication Error(" + cmd
									+ ") but Send SMS Success.");
							rtnMessage = "FAIL : [" + modemDeviceSerial + "] Communication Error(" + cmd
									+ ") but Send SMS Success.\n";
						} else {
							log.debug("Success : [" + modemDeviceSerial + "] Command Result[" + cmd + "]");
							rtnMessage = "Success : [" + modemDeviceSerial + "] Command Result(" + cmd
									+ ") and Send SMS Success.\n";

							if (cmd.equals("cmdResetModem")) {
								rtnMessage = "SUCCESS_UNKNOWN";
							} else if (cmd.equals("cmdUploadMeteringData")) {
								rtnMessage = "SUCCESS_UNKNOWN";
							} else if (cmd.equals("cmdFactorySetting")) {
								rtnMessage = "SUCCESS_UNKNOWN";
							} else if (cmd.equals("cmdReadModemConfiguration")) {
								rtnMessage = "SUCCESS_UNKNOWN";
							}
							// Iraq MOE프로젝트에서는 시스템적으로 미터,모뎀 식별용으로만 사용. 사용자에게
							// Command제공하지 않음.
							
							 * else if (cmd.equals("cmdIdentifyDevice")) {
							 * List<AsyncCommandParam> acplist =
							 * asyncCommandLogManager.getCmdParams(modem.
							 * getDeviceSerial(), Long.parseLong(trnxId), null);
							 * if (acplist == null || acplist.size() <= 0) {
							 * rtnMessage = "RESULT=Empty~!!"; } else {
							 * StringBuilder sb = new StringBuilder(); for
							 * (AsyncCommandParam param : acplist) { if
							 * (param.getParamType().equals("eui")) { sb.append(
							 * " RESULT_EUI=" + param.getParamValue()); } else
							 * if (param.getParamType().equals("time")) {
							 * sb.append(" RESULT_TIME=" +
							 * param.getParamValue()); } } rtnMessage =
							 * sb.toString(); } log.debug(
							 * "cmdIdentifyDevice returnValue =>> " +
							 * rtnMessage); }
							 
							else if (cmd.equals("cmdSetTime")) {
								rtnMessage = "SUCCESS_UNKNOWN";
							} else if (cmd.equals("cmdSetModemResetInterval")) {
								rtnMessage = "SUCCESS_UNKNOWN";
							} else if (cmd.equals("cmdSetMeteringInterval")) {
								rtnMessage = "SUCCESS_UNKNOWN";
							} else if ("cmdSetServerIpPort".equals(cmd)) {
								rtnMessage = "SUCCESS_UNKNOWN";
							} else if (cmd.equals("cmdSetApn")) {
								rtnMessage = "SUCCESS_UNKNOWN";
							} else if (cmd.equals("cmdOTAStart") && isOTA) {
								// List<AsyncCommandParam> acplist =
								// asyncCommandLogManager.getCmdParams(modem.getDeviceSerial(),
								// Long.parseLong(trnxId), null);
								// if (acplist == null || acplist.size() <= 0) {
								// rtnMessage = "RESULT_OTA=Empty~!!";
								// } else {
								// rtnMessage += "Result = ";
								//
								// String result = "";
								// String start = "";
								// String end = "";
								// String elapse = "";
								//
								// for(AsyncCommandParam param : acplist){
								// result =
								// param.getParamType().equals("RESULT_OTA") ?
								// param.getParamValue() : "" + "\n";
								// start=
								// param.getParamType().equals("RESULT_OTA_START")
								// ? "Start =" + param.getParamValue() : "";
								// end =
								// param.getParamType().equals("RESULT_OTA_END")
								// ? " Stop =" + param.getParamValue() : "";
								// elapse =
								// param.getParamType().equals("RESULT_OTA_ELAPSE")
								// ? " ElStop =" + param.getParamValue() : "" +
								// "\n";
								// }
								//
								// rtnMessage += result + start + end + elapse;
								// }
								
								 * OTA시간이 오래걸려서 아래처럼 처리함.
								 
								rtnMessage = "RESULT_OTA = Proceeding";

								log.debug("cmdOTAStart returnValue =>> " + rtnMessage);
							} else if (cmd.equals("cmdCurrentValuesMetering")) {
								rtnMessage = "SUCCESS_UNKNOWN"; // 검침서버로 업로드
							} else if (cmd.equals("cmdGetMeterStatus")) {
								List<AsyncCommandParam> acplist = asyncCommandLogManager
										.getCmdParams(modem.getDeviceSerial(), Long.parseLong(trnxId), null);
								if (acplist == null || acplist.size() <= 0) {
									rtnMessage = "RESULT_STATUS=Empty~!!";
								} else {
									rtnMessage += "Result = ";
									for (AsyncCommandParam param : acplist) {
										rtnMessage += param.getParamType().equals("RESULT_STATUS")
												? param.getParamValue() : "" + "\n";
										rtnMessage += param.getParamType().equals("RESULT_STATUS_STRING")
												? "Meter Status => " + param.getParamValue() : "";
									}
								}
								log.debug("cmdGetMeterStatus returnValue =>> " + rtnMessage);
							} else if (cmd.equals("cmdReadModemEventLog")) {
								List<AsyncCommandParam> acplist = asyncCommandLogManager
										.getCmdParams(modem.getDeviceSerial(), Long.parseLong(trnxId), null);
								if (acplist == null || acplist.size() <= 0) {
									rtnMessage = "RESULT_EVENT_LOG_COUNT=Empty~!!";
								} else {
									rtnMessage += "Result = ";
									for (AsyncCommandParam param : acplist) {
										rtnMessage += param.getParamType().equals("RESULT_EVENT_LOG_COUNT")
												? param.getParamValue() : "" + "\n";
									}
								}
								log.debug("cmdReadModemEventLog returnValue =>> " + rtnMessage);
							} else if (cmd.equals("cmdMeterOTAStart") && isOTA) {
								rtnMessage = "RESULT_OTA = Proceeding";
								log.debug("cmdMeterOTAStart returnValue =>> " + rtnMessage);
							}

							
							 * 
							 * 
							 * 위에 AsyncCommandParam 방식으로 되어 있던거
							 * AsyncCommandResult 방식으로 모두 바꿀것. 위에
							 * AsyncCommandParam 방식으로 되어 있던거 AsyncCommandResult
							 * 방식으로 모두 바꿀것. 위에 AsyncCommandParam 방식으로 되어 있던거
							 * AsyncCommandResult 방식으로 모두 바꿀것. 위에
							 * AsyncCommandParam 방식으로 되어 있던거 AsyncCommandResult
							 * 방식으로 모두 바꿀것. 위에 AsyncCommandParam 방식으로 되어 있던거
							 * AsyncCommandResult 방식으로 모두 바꿀것.
							 * 
							 * 
							 * 
							 
							else if (cmd.equals("cmdGetMeterFWVersion")) {
								List<AsyncCommandResult> acplist = asyncCommandLogManager
										.getCmdResults(modem.getDeviceSerial(), Long.parseLong(trnxId), null);
								if (acplist == null || acplist.size() <= 0) {
									rtnMessage = "RESULT_STATUS=Empty~!!";
								} else {
									rtnMessage += "Result = ";
									for (AsyncCommandResult param : acplist) {
										rtnMessage += param.getResultType().equals("RESULT_VALUE")
												? "Meter F/W Version => " + param.getResultValue() : "" + "\n";
									}
								}
								log.debug("cmdGetMeterFWVersion returnValue =>> " + rtnMessage);
							} else if (cmd.equals("cmdAlarmReset")) {
								List<AsyncCommandResult> acplist = asyncCommandLogManager
										.getCmdResults(modem.getDeviceSerial(), Long.parseLong(trnxId), null);
								if (acplist == null || acplist.size() <= 0) {
									rtnMessage = "RESULT_STATUS=Empty~!!";
								} else {
									rtnMessage += "Result = ";
									for (AsyncCommandResult param : acplist) {
										rtnMessage += param.getResultType().equals("RESULT_VALUE")
												? "Meter Alarm Reset => " + param.getResultValue() : "" + "\n";
									}
								}
								log.debug("cmdAlarmReset returnValue =>> " + rtnMessage);
							} else if (cmd.equals("cmdGetBillingCycle")) {
								List<AsyncCommandResult> acplist = asyncCommandLogManager
										.getCmdResults(modem.getDeviceSerial(), Long.parseLong(trnxId), null);
								if (acplist == null || acplist.size() <= 0) {
									rtnMessage = "RESULT_STATUS=Empty~!!";
								} else {
									rtnMessage += "Result = ";
									for (AsyncCommandResult param : acplist) {
										rtnMessage += param.getResultType().equals("RESULT_VALUE")
												? "Get Meter Billing Cycle => " + param.getResultValue() : "" + "\n";
									}
								}
								log.debug("cmdGetBillingCycle returnValue =>> " + rtnMessage);
							} else if (cmd.equals("cmdSetBillingCycle")) {
								List<AsyncCommandResult> acplist = asyncCommandLogManager
										.getCmdResults(modem.getDeviceSerial(), Long.parseLong(trnxId), null);
								if (acplist == null || acplist.size() <= 0) {
									rtnMessage = "RESULT_STATUS=Empty~!!";
								} else {
									rtnMessage += "Result = ";
									for (AsyncCommandResult param : acplist) {
										rtnMessage += param.getResultType().equals("RESULT_VALUE")
												? "Set Meter Billing Cycle => " + param.getResultValue() : "" + "\n";
									}
								}
								log.debug("cmdSetBillingCycle returnValue =>> " + rtnMessage);
							} else if (cmd.equals("cmdTOUSet")) {
								List<AsyncCommandResult> acplist = asyncCommandLogManager
										.getCmdResults(modem.getDeviceSerial(), Long.parseLong(trnxId), null);
								if (acplist == null || acplist.size() <= 0) {
									rtnMessage = "RESULT_STATUS=Empty~!!";
								} else {
									rtnMessage += "Result = ";
									for (AsyncCommandResult param : acplist) {
										rtnMessage += param.getResultType().equals("RESULT_VALUE")
												? "Meter TOU Set => " + param.getResultValue() : "" + "\n";
									}
								}
								log.debug("cmdTOUSet returnValue =>> " + rtnMessage);
							} else if (cmd.equals("cmdDemandPeriod")) {
								List<AsyncCommandResult> acplist = asyncCommandLogManager
										.getCmdResults(modem.getDeviceSerial(), Long.parseLong(trnxId), null);
								if (acplist == null || acplist.size() <= 0) {
									rtnMessage = "RESULT_STATUS=Empty~!!";
								} else {
									rtnMessage += "Result = ";
									for (AsyncCommandResult param : acplist) {
										rtnMessage += param.getResultType().equals("RESULT_VALUE")
												? "Meter Demand Period => " + param.getResultValue() : "" + "\n";
									}
								}
								log.debug("cmdDemandPeriod returnValue =>> " + rtnMessage);
							} else {
								throw new Exception("Unknown Command.");
							}
						}
					}

					if (operationCode != null) {
						String opMessage = "";
						if (250 <= rtnMessage.length()) {
							opMessage = rtnMessage.substring(0, 250) + "....";
						} else {
							opMessage = rtnMessage;
						}
						operationLogManager.saveOperationLog(supplier, targetTypeCode, modemDeviceSerial, loginId,
								operationCode, status.getCode(), opMessage);
					}

				} catch (Exception e) {
					log.error("FAIL : Send SMS Fail - [" + cmd + "][" + modemDeviceSerial + "]", e);
					rtnMessage = "FAIL : Send SMS Fail - [" + cmd + "][" + modemDeviceSerial + "]";
				}
			}

		}

		mav.addObject("rtnStr", rtnMessage);
		return mav;

	}*/

	/**
	 * get modem event log
	 * 
	 * @param mdsId
	 *            != deviceSerial
	 * @param loginId
	 *            for validation of permission
	 * @param logCount
	 *            수집할 로그의 개수 지정(최신으로부터 개수)
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "gadget/device/command/cmdModemEventLog")
	public ModelAndView cmdModemEventLog(@RequestParam("mdsId") String mdsId,
			@RequestParam(value = "loginId") String loginId,
			@RequestParam(value = "count", required = false) Integer logCount) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String, Object>();

		// 로그인 계정의 커맨드 권한 체크 (8.2.4=Get Modem Log)
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.4")) {
            Code requiredCode = codeManager.getCodeByCode("8.2.4");
            mav.addObject("rtnStr", "Your role have no permission ["+requiredCode.getName()+"]");
			return mav;
		}
		// 커맨드 전송할 모뎀의 아이디 null 여부
		if (mdsId == null || "".equals(mdsId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} else {
			// 모뎀 조회는 CommandGW에서 한번에 실행
			// WS명령도 고려하여 조회를 공통순서 한번만 하도록 합니다
		}
		// 요청할 모뎀로그의 개수(기본값 1개)
		int lc = 1;
		if (logCount != null) {
			lc = logCount;
		}

		// 호출
		String rtnStr = "";
		Modem modem = modemManager.getModem(Integer.parseInt(mdsId));
		
		// Supplier
		Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		// MBB(SMS)
		if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){   
			Map<String, String> asyncResult = new HashMap<String, String>();
			try{ 
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("mdsId", mdsId);
                paramMap.put("count", lc+"");
                asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "ModemEventLog", paramMap);
             
                if(asyncResult != null){
                	status = ResultStatus.SUCCESS;
                }else{
                    log.debug("SMS Fail");
                    rtnStr = "SMS Fail\n";
                    status = ResultStatus.FAIL; 
                    mav.addObject("rtnStr", rtnStr);
                }
             
            }catch(Exception e){
            	e.printStackTrace();
            	log.debug("SMS Fail");
            	rtnStr = "SMS Fail\n";
            	status = ResultStatus.FAIL; 
            	mav.addObject("rtnStr", rtnStr);
            }
			for (String key : asyncResult.keySet()) {
				mav.addObject(key, asyncResult.get(key).toString());
				if(key.contains("NI_RESULT")){
					mav.addObject("eventLogs", asyncResult.get(key).toString());
				}
			}
			
			//Operation Log
			Code operationCode = codeManager.getCodeByCode("8.2.4"); // Get Modem Log
			Code targetTypeCode = codeManager.getCodeByCode("1.2"); // Modem
			if (operationCode != null) {
				operationLogManager.saveOperationLog(supplier, targetTypeCode, modem.getDeviceSerial(),
						loginId, operationCode, status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
			}
			
					
			mav.addObject("status", status.name());
			return mav;
		}else{  // RF or Ethernet
			try {
				result = cmdOperationUtil.cmdModemEventLog(mdsId, lc);
				if (result.containsKey("eventLogs")) {
					status = ResultStatus.SUCCESS;
				}
			} catch (Exception e) {
				log.error(e, e);
				status = ResultStatus.FAIL;
				rtnStr = e.getMessage();
				mav.addObject("rtnStr", rtnStr);
			}
			for (String key : result.keySet()) {
				mav.addObject(key, result.get(key).toString());
			}
			
			//Operation Log
			Code operationCode = codeManager.getCodeByCode("8.2.4"); // Get Modem Log
			Code targetTypeCode = codeManager.getCodeByCode("1.2"); // Modem
			if (operationCode != null) {
				operationLogManager.saveOperationLog(supplier, targetTypeCode, modem.getDeviceSerial(),
						loginId, operationCode, status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
			}
			
			
			mav.addObject("status", status.name());
			return mav;
		}
	}

	/**
	 * meter baud rate [get/set]
	 * 
	 * @param mdsId
	 *            modem.id
	 * @return
	 */

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "gadget/device/command/cmdMeterBaudRate")
	public ModelAndView cmdMeterBaudRate(@RequestParam(value = "mdsId") String mdsId,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "requestType") String requestType,
			@RequestParam(value = "requestValue", required = false) Integer requestValue) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String, Object>();

		// 로그인 계정의 커맨드 권한 체크
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.7")) {
			Code requiredCode = codeManager.getCodeByCode("8.2.7");
			mav.addObject("rtnStr", "Your role have no permission ["+requiredCode.getName()+"]");
			return mav;
		}
		// 커맨드 전송할 모뎀 아이디의 null 여부조회
		if (mdsId == null || "".equals(mdsId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} else {
			// 모뎀 조회는 CommandGW에서 한번에 실행
			// WS명령도 고려하여 조회를 공통순서 한번만 하도록 합니다
		}

		// 호출
		String rtnStr = "";
		int rateValue = requestValue == null ? 0 : requestValue;
		Modem modem = modemManager.getModem(Integer.parseInt(mdsId));
		
		// Supplier
		Supplier supplier = modem.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		// MBB (SMS)
		if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){   
			 Map<String, String> asyncResult = new HashMap<String, String>();
			try{ 
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("mdsId", mdsId);
                paramMap.put("requestType", requestType);
				paramMap.put("baudRate", rateValue+""); // UPDATE SP-733

                String logName = "MeterBaud_GET";
                if(requestType.contains("SET")){
                    logName = "MeterBaud";
                }
                asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(),
                        SMSConstants.COMMAND_TYPE.NI.getTypeCode(), logName, paramMap);
             
                if(asyncResult != null){
                	status = ResultStatus.SUCCESS;
                }else{
                    log.debug("SMS Fail");
                    rtnStr = "SMS Fail\n";
                    status = ResultStatus.FAIL;
                    mav.addObject("rtnStr", rtnStr);
                }
            }catch(Exception e){
            	log.error("SMS Fail : " + e,e);
            	rtnStr = "SMS Fail\n";
            	status = ResultStatus.FAIL;
            	mav.addObject("rtnStr", rtnStr);
            }
            for (String key : asyncResult.keySet()) {
				mav.addObject(key, asyncResult.get(key).toString());
                if(key.contains("NI_RESULT")){
                    mav.addObject("baudRate", asyncResult.get(key).toString());
                }
			}
            
            //Operation Log
			Code operationCode = codeManager.getCodeByCode("8.2.7"); // Set Baud Rate
			Code targetTypeCode = codeManager.getCodeByCode("1.2"); // Modem
			if (operationCode != null) {
				operationLogManager.saveOperationLog(supplier, targetTypeCode, modem.getDeviceSerial(),
						loginId, operationCode, status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
			}
            
            mav.addObject("status", status.name());
            return mav;
		}else{  // RF or Ethernet
			try {
				result = cmdOperationUtil.cmdMeterBaudRate(mdsId, requestType, rateValue);
				if (result.containsKey("baudRate")) {
					status = ResultStatus.SUCCESS;
				}
			} catch (Exception e) {
				log.error(e, e);
				status = ResultStatus.FAIL;
				rtnStr = e.getMessage();
				mav.addObject("rtnStr", rtnStr);
			}
			for (String key : result.keySet()) {
				mav.addObject(key, result.get(key).toString());
			}
			
			//Operation Log
			Code operationCode = codeManager.getCodeByCode("8.2.7"); // Set Baud Rate
			Code targetTypeCode = codeManager.getCodeByCode("1.2"); // Modem
			if (operationCode != null) {
				operationLogManager.saveOperationLog(supplier, targetTypeCode, modem.getDeviceSerial(),
						loginId, operationCode, status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
			}
			
			
			mav.addObject("status", status.name());
			return mav;
		}
	}
	
	/**
	 * 개발 중
	 * Under Construction 
	 * Real Time Metering [set]
	 * 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "gadget/device/command/cmdRealTimeMetering")
	public ModelAndView cmdRealTimeMetering(
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "mdsId") String mdsId,
			@RequestParam(value = "interval") Integer interval,
			@RequestParam(value = "duration") Integer duration){
		log.debug("## NI command - Real Time Metering ["+mdsId+"],["+interval+"],["+duration+"]");
		
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String, Object>();

		// 로그인 계정의 커맨드 권한 체크
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.7")) {
			Code requiredCode = codeManager.getCodeByCode("8.2.7");
			mav.addObject("rtnStr", "Your role have no permission ["+requiredCode.getName()+"]");
			return mav;
		}
		
		// 커맨드 전송할 모뎀 아이디의 null 여부조회
		if (mdsId == null || "".equals(mdsId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} else {
		}
		
		// 호출
		String rtnStr = "";
		Meter meter = meterManager.getMeter(Integer.parseInt(mdsId));
		mdsId = meter.getMdsId();
		Modem modem = meter.getModem();
		// mdsId = modem.getId().toString();
		
		// MBB (SMS)
		if( modem != null && modem.getProtocolType() == Protocol.SMS ){   
			Map<String, String> asyncResult = new HashMap<String, String>();
			try{ 
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("mdsId", mdsId);
                paramMap.put("interval", interval+"");
				paramMap.put("duration", duration+"");
                asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "RealTimeMetering", paramMap);
                
                if(asyncResult != null){
                	status = ResultStatus.SUCCESS;
                }else{
                    log.debug("SMS Fail");
                    rtnStr = "SMS Fail\n";
                    status = ResultStatus.FAIL;
                    mav.addObject("rtnStr", rtnStr);
                }
            }catch(Exception e){
            	log.error("SMS Fail : " + e,e);
            	rtnStr = "SMS Fail\n";
            	status = ResultStatus.FAIL;
            	mav.addObject("rtnStr", rtnStr);
            }
            for (String key : asyncResult.keySet()) {
				mav.addObject(key, asyncResult.get(key).toString());
				if(key.contains("NI_RESULT")){
					mav.addObject("result", asyncResult.get(key).toString());
				}
			}
            mav.addObject("status", status.name());
            return mav;
		}else{  // RF or Ethernet
			try {
				result = cmdOperationUtil.cmdRealTimeMetering(mdsId, interval, duration);
				if (result.containsKey("result")) {
					if(result.get("result").equals("0")) // SUCCESS
						status = ResultStatus.SUCCESS;
				}
			} catch (Exception e) {
				log.error(e, e);
				status = ResultStatus.FAIL;
				rtnStr = e.getMessage();
				mav.addObject("rtnStr", rtnStr);
			}
			for (String key : result.keySet()) {
				mav.addObject(key, result.get(key).toString());
			}
			mav.addObject("status", status.name());
			return mav;
		}
	}
	
	/**
	 * NI : Set SNMP status of MODEM (enable or disable)
     * It is supported by MBB and ETHERNET only. (RF : return error)
	 * @param mdsId  modem.id(Integer)
	 * @param requestType  SET or GET
	 * @param requestValue   ENABLE(1) or DISABLE(0)
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "gadget/device/command/cmdModemSnmpTrap")
	public ModelAndView cmdModemSnmpTrap(@RequestParam(value = "mdsId") String mdsId,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "requestType") String requestType,
			@RequestParam(value = "trapStatus", required = false) String requestValue) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String, Object>();

		// 로그인 계정의 커맨드 권한 체크(Modem SNMP enable/disable 8.2.5)
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.5")) {
			Code requiredCode = codeManager.getCodeByCode("8.2.5");
			mav.addObject("rtnStr", "Your role have no permission ["+requiredCode.getName()+"]");
			return mav;
		}
		// 커맨드 전송할 모뎀 아이디의 null 여부조회
		if (mdsId == null || "".equals(mdsId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} else {
			// 모뎀 조회는 CommandGW에서 한번에 실행
			// WS명령도 고려하여 조회를 공통순서 한번만 하도록 합니다
		}

		// 호출
		String rtnStr = "";
		String trapStatus = requestValue == null ? "0" : requestValue;
		Modem modem = modemManager.getModem(Integer.parseInt(mdsId));
		
		// MBB (SMS)
		if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){   
			 Map<String, String> asyncResult = new HashMap<String, String>();
			try{ 
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("mdsId", mdsId);
                paramMap.put("requestType", requestType);
                // Key Name is Important
				paramMap.put("trapStatus", trapStatus+"");
				
				String logName = "SnmpTrapOnOff_GET";
				if(requestType.contains("SET")){
					logName = "SnmpTrapOnOff";
				}
                asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), 
                		SMSConstants.COMMAND_TYPE.NI.getTypeCode(), logName, paramMap);
             
                if(asyncResult != null){
                	status = ResultStatus.SUCCESS;
                }else{
                    log.debug("SMS Fail");
                    rtnStr = "SMS Fail\n";
                    status = ResultStatus.FAIL;
                    mav.addObject("rtnStr", rtnStr);
                }
            }catch(Exception e){
            	log.error("SMS FAIL : " + e,e);
            	rtnStr = "SMS Fail\n";
            	status = ResultStatus.FAIL;
            	mav.addObject("rtnStr", rtnStr);
            }
            for (String key : asyncResult.keySet()) {
				mav.addObject(key, asyncResult.get(key).toString());
				if(key.contains("NI_RESULT")){
					mav.addObject("trapStatus", asyncResult.get(key).toString());
				}
			}
			mav.addObject("rtnStr", "Check the Async-Command-History");
            mav.addObject("status", status.name());
            return mav;
		}else if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.IP 
				|| modem.getProtocolType() == Protocol.GPRS)) ){
            // ETHERNET (IP)
			try {
				result = cmdOperationUtil.cmdModemSnmpTrap(mdsId, requestType, trapStatus);
				if (result.containsKey("trapStatus")) {
					status = ResultStatus.SUCCESS;
				}
			} catch (Exception e) {
				log.error(e, e);
				status = ResultStatus.FAIL;
				rtnStr = e.getMessage();
				mav.addObject("rtnStr", rtnStr);
			}
			for (String key : result.keySet()) {
				mav.addObject(key, result.get(key).toString());
				if(key.contains("NI_RESULT")){
					mav.addObject("trapStatus", result.get(key).toString());
				}
			}
			mav.addObject("status", status.name());
			return mav;
		}else{
            // RF is not support the snmp_trap_on_off command
            status = ResultStatus.SUCCESS;
            mav.addObject("status", status.name());
            mav.addObject("rtnStr", "SNMP status of RF modem follows DCU's configuration.");
            return mav;
        }
	}

	/**
	 * NI : Request SNMP Open to MBB-MODEM (161 port)
	 * It is supported by MBB only.
	 * @param mdsId  modem.id(Integer)
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "gadget/device/command/cmdSnmpDaemon")
	public ModelAndView cmdSnmpDaemon(@RequestParam(value = "modemId") String mdsId,
                                      @RequestParam(value = "loginId", required = false) String loginId) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String, Object>();

		// 로그인 계정의 커맨드 권한 체크(Modem SNMP enable/disable 8.2.5)
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.5")) {
			Code requiredCode = codeManager.getCodeByCode("8.2.5");
			mav.addObject("rtnStr", "Your role have no permission ["+requiredCode.getName()+"]");
			return mav;
		}
		// 커맨드 전송할 모뎀 아이디의 null 여부조회
		if (mdsId == null || "".equals(mdsId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} else {
			// 모뎀 조회는 CommandGW에서 한번에 실행
			// WS명령도 고려하여 조회를 공통순서 한번만 하도록 합니다
		}

		// 호출
		String rtnStr = "";
		Modem modem = modemManager.getModem(Integer.parseInt(mdsId));

		// MBB (SMS)
		if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
			Map<String, String> asyncResult = new HashMap<String, String>();
			try{
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("mdsId", mdsId);

				String logName = "SnmpDaemon";
				asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(),
						SMSConstants.COMMAND_TYPE.SNMP.getTypeCode(), logName, paramMap);

				if(asyncResult != null){
					status = ResultStatus.SUCCESS;
				}else{
					log.debug("SMS Fail");
					rtnStr = "SMS Fail\n";
					status = ResultStatus.FAIL;
					mav.addObject("rtnStr", rtnStr);
				}
			}catch(Exception e){
				log.error("SMS FAIL : " + e,e);
				rtnStr = "SMS Fail\n";
				status = ResultStatus.FAIL;
				mav.addObject("rtnStr", rtnStr);
			}
			for (String key : asyncResult.keySet()) {
				mav.addObject(key, asyncResult.get(key).toString());
			}
			mav.addObject("rtnStr", "Check the Async-Command-History");
			mav.addObject("status", status.name());
			return mav;

		}else{
			// RF & Ethernet is not support the snmp open command
			status = ResultStatus.SUCCESS;
			mav.addObject("status", status.name());
			mav.addObject("rtnStr", "Only MBB is possible to set the SNMP Daemon");
			return mav;
		}
	}

	/**
	 * 외주개발용 집중기에 별도정의된 프로토콜 메시지를 작성하여 전송
	 *
	 * @param mcuId
	 * @param streamData
	 * @param loginId
	 * @return mav
	 */
	@RequestMapping(value = "/gadget/device/command/cmdExternalCommand")
	public ModelAndView cmdExternalCommand(@RequestParam(value = "mcuId", required = false) String mcuId,
			@RequestParam(value = "streamData", required = false) String streamData,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		// 로그인 계정의 커맨드 권한 체크
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.4")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		// 커맨드 전송할 집중기 조회
		if (mcuId == null || "".equals(mcuId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(mcu id) is null!");
			return mav;
		}
		String rtnStr = "";
		MCU mcu = mcuManager.getMCU(Integer.parseInt(mcuId));
		// 공급사 조회
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {

			byte[] generalStream = streamData.getBytes();
			cmdOperationUtil.cmdExtCommand(mcuId, generalStream);

			status = ResultStatus.SUCCESS;

		} catch (Exception e) {
			log.error(e, e);
			rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
		}

		// Operation Log 잠시 대기
		// Code operationCode = codeManager.getCodeByCode("8.1.8");
		//
		// Code deviceTypeCode = codeManager.getCode(mcu.getMcuTypeCodeId());
		// if (operationCode != null) {
		// operationLogManager.saveOperationLog(supplier, deviceTypeCode,
		// meter.getMdsId(), loginId, operationCode, status.getCode(),
		// status.name());
		// }

		mav.addObject("status", status.name());
		// mav.addObject("meterId", meter.getMdsId());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}

	private void saveAsyncCommand(String deviceSerial, String cmd, String[] param, String currentTime) {
		AsyncCommandLog asyncCommandLog = new AsyncCommandLog();
		long trId = System.currentTimeMillis();
		asyncCommandLog.setTrId(trId);
		asyncCommandLog.setMcuId(deviceSerial);
		asyncCommandLog.setDeviceType(McuType.MMIU.name());
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
		if (param != null && param.length > 0) {
			// parameter가 존재할 경우.
			Integer maxNum = asyncCommandLogManager.getParamMaxNum(deviceSerial, trId);
			if (maxNum != null)
				num = maxNum + 1;

			for (int i = 0; i < param.length; i++) {
				AsyncCommandParam asyncCommandParam = new AsyncCommandParam();
				asyncCommandParam.setMcuId(deviceSerial);
				asyncCommandParam.setNum(num);
				asyncCommandParam.setParamType("String");
				asyncCommandParam.setParamValue(param[i]);
				asyncCommandParam.setTrId(trId);

				asyncCommandLogManager.addParam(asyncCommandParam);
				num += 1;
			}
		}
	}

	private void saveAsyncCommandList(String deviceSerial, long trId, String cmd, List<Map<String, String>> paramList,
			String currentTime) {
		AsyncCommandLog asyncCommandLog = new AsyncCommandLog();
		asyncCommandLog.setTrId(trId);
		asyncCommandLog.setMcuId(deviceSerial);
		asyncCommandLog.setDeviceType(McuType.MMIU.name());
		asyncCommandLog.setDeviceId(deviceSerial);
		asyncCommandLog.setCommand(cmd);
		asyncCommandLog.setTrOption(TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode());
		asyncCommandLog.setState(TR_STATE.Waiting.getCode());
		asyncCommandLog.setOperator(OperatorType.OPERATOR.name());
		asyncCommandLog.setCreateTime(currentTime);
		asyncCommandLog.setRequestTime(currentTime);
		asyncCommandLog.setLastTime(null);
		asyncCommandLogManager.add(asyncCommandLog);
		Integer num = 0;
		if (paramList != null && paramList.size() > 0) {
			// parameter가 존재할 경우.
			Integer maxNum = asyncCommandLogManager.getParamMaxNum(deviceSerial, trId);
			if (maxNum != null)
				num = maxNum + 1;

			for (int i = 0; i < paramList.size(); i++) {
				Map<String, String> param = paramList.get(i);
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
	}

	private void saveAsyncCommandForMOE(String deviceSerial, Long trId, String cmd, Map<String, String> param,
			String currentTime) throws Exception {
		AsyncCommandLog asyncCommandLog = new AsyncCommandLog();
		asyncCommandLog.setTrId(trId);
		asyncCommandLog.setMcuId(deviceSerial);
		asyncCommandLog.setDeviceType(McuType.MMIU.name());
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
			// parameter가 존재할 경우.
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


	private String sendSMSForMOE(String oid, String trnxId, String mobileNo, String command) {
		String result = "";

		try {
			int seq = new Random().nextInt(100) & 0xFF;

			Properties prop = new Properties();
			prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
			String smsClassPath = prop.getProperty("moe.smsClassPath");
			String serverIp = prop.getProperty("moe.server.sms.serverIpAddr") == null ? ""
					: prop.getProperty("moe.server.sms.serverIpAddr").trim();
			String serverPort = prop.getProperty("moe.server.sms.serverPort") == null ? ""
					: prop.getProperty("moe.server.sms.serverPort").trim();

			if ("".equals(serverIp) || "".equals(serverPort)) {
				result = "error";
				log.debug("========>>> [{" + command + "}] Message Send Error: Invalid Ip Address or port!");
			} else {
				String smsMsg = cmdMsg((byte) seq, oid, serverIp.replaceAll("\\.", ","), serverPort);
				SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
				Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
				// result = (String) m.invoke(obj, mobileNo.replace("-",
				// "").trim(), smsMsg, prop);
				result = "success";
			}
		} catch (Exception e) {
			log.debug(e);
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

	@RequestMapping(value = "/gadget/device/command/cmdMcuGetLog")
	public ModelAndView cmdMcuGetLog(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "count", required = false) int count) throws ParseException {

		ResultStatus status = ResultStatus.FAIL;
		Code mcuStatusNormal = codeDao.findByCondition("code", "1.1.4.1"); // Normal
		Code mcuStatusCommError = codeDao.findByCondition("code", "1.1.4.5"); // CommError
		String currentDay = TimeUtil.getCurrentDay();
		String currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
		String lastCommDate = currentDay + currentTime;
		ModelAndView mav = new ModelAndView("jsonView");

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.3.5")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		String cmdResult = "";

		MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		String lang = supplier.getLang().getCode_2letter();
		String country = supplier.getCountry().getCode_2letter();
		String msg = "";
		String result = "";
		String temp[] = { "", "" };
		try {
			cmdResult = cmdOperationUtil.cmdMCUGetLog(mcu.getSysID(), count);
			// cmdResult =
			// "evtConfigChanged(200.29.0),20161010101010,6D65746572696E67010C002F0032202A2F35202A202A202A206D65746572696E67206F7074696F6E3D37372C6F66667365743D302C636F756E743D32";
			status = ResultStatus.SUCCESS;
			mcu.setMcuStatus(mcuStatusNormal);
			mcu.setLastCommDate(lastCommDate);
			mcuManager.updateMCU(mcu); //모뎀상태 변경(Normal, LastCommTime)
			/*StringTokenizer str = new StringTokenizer(cmdResult, "\n");
			while (str.hasMoreTokens()) {
				StringTokenizer str2 = new StringTokenizer(str.nextToken(), ",");
				while (str2.hasMoreTokens()) {
					temp[0] = str2.nextToken();
					result += TimeLocaleUtil.getLocaleDate(str2.nextToken(), lang, country);
					if (str2.hasMoreTokens()) {
						temp[1] = str2.nextToken();
						byte[] bytes = new java.math.BigInteger(temp[1], 16).toByteArray();
						msg += new String(bytes);
						result += ",  " + temp[0] + ",  " + msg + "\n";
						msg="";
					} else
						result += ",  " + temp[0] + " \n";
				}
			}*/
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			mcu.setMcuStatus(mcuStatusCommError);
			mcuManager.updateMCU(mcu); //모뎀상태 변경(CommError)
			rtnStr = e.getMessage();
			log.error(e, e);
		}

		Code operationCode = codeManager.getCodeByCode("8.3.5");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		mav.addObject("jsonString", cmdResult);
		return mav;
	}

	/* SP-809 */
	@RequestMapping(value = "/gadget/device/command/cmdForceUpload")
	public ModelAndView cmdForceUpload(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "serverName", required = false) String serverName,
			@RequestParam(value = "dataCount", required = false) int dataCount,
			@RequestParam(value = "fromDate", required = false) String fromDate,
			@RequestParam(value = "toDate", required = false) String toDate) throws ParseException {

		Code mcuStatusNormal = codeDao.findByCondition("code", "1.1.4.1"); // Normal
		Code mcuStatusCommError = codeDao.findByCondition("code", "1.1.4.5"); // CommError
		String currentDay = TimeUtil.getCurrentDay();
		String currentTime = new java.text.SimpleDateFormat("HHmmss").format(new java.util.Date());
		String lastCommDate = currentDay + currentTime;
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		String tmpFromDateStr = "";
		String tmpToDateStr = "";

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.3.13")) {
			mav.addObject("rtnStr", "No permission");
			return mav;
		}

		tmpFromDateStr = fromDate.substring(0,4) + fromDate.substring(5,7) +fromDate.substring(8,10);
		tmpToDateStr = toDate.substring(0,4) + toDate.substring(5,7) +toDate.substring(8,10);

		if (fromDate == null || "".equals(fromDate)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "FromDate null!");
			return mav;
		}
		if (toDate == null || "".equals(toDate)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "ToDate null!");
			return mav;
		}
		String rtnStr = "";
		String cmdResult = "";

		MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			cmdResult = cmdOperationUtil.cmdForceUpload(mcu.getSysID(), serverName, dataCount, tmpFromDateStr, tmpToDateStr);
			status = ResultStatus.SUCCESS;
			mcu.setMcuStatus(mcuStatusNormal);
			mcu.setLastCommDate(lastCommDate);
			mcuManager.updateMCU(mcu); //모뎀상태 변경(Normal, LastCommTime)
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			mcu.setMcuStatus(mcuStatusCommError);
			mcuManager.updateMCU(mcu); //모뎀상태 변경(CommError)
			rtnStr = e.getMessage();
			log.error(e, e);
		}

		Code operationCode = codeManager.getCodeByCode("8.3.13");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		mav.addObject("rtnStr", cmdResult);

		return mav;
	}

	// Get DCU Schedule with SYSID(String)
	@RequestMapping(value = "/gadget/device/command/cmdMcuGetSchedule2")
	public ModelAndView cmdMcuGetSchedule2(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId){
		
		ModelAndView mav = new ModelAndView("jsonView");
		MCU mcu = mcuManager.getMCU(target);
		if(mcu == null){
			mav.addObject("rtnStr", "Unregistered DCU Serial");
			return mav;
		}
		String mcuId = mcu.getId().toString();				
		
		return cmdMcuGetSchedule(mcuId, loginId);
	}
	
	@RequestMapping(value = "/gadget/device/command/cmdMcuGetSchedule")
	public ModelAndView cmdMcuGetSchedule(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String,Object>();
		// retry interval을 요청할 두번째 커맨드 결과 맵
		Map<String, Object> result2 = new HashMap<String,Object>();
		
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.3.2")){
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			result = cmdOperationUtil.cmdMCUGetSchedule(mcu.getSysID(), "");			
			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.getMessage();
			log.error(e, e);
		}
		
		//get retry interval 커맨드 
		try {		
			result2 = cmdOperationUtil.cmdMcuGetProperty(mcu.getSysID(), "network.retry.default");			
		} catch (Exception e) {			
			rtnStr = e.getMessage();
			log.error(e, e);
		}
		if(result2.containsKey("cmdResult")){
			// mcuGetProperty에서 받은 결과값에서 필요한 키(network.retry.defautl)에 할당된 값을 mav에 입력
			mav.addObject("retryIntervalTime", result2.get("cmdResult").toString() == null ? "" : result2.get("cmdResult").toString());
			mav.addObject("retry_condition", result2.get("network.retry.default").toString() == null ? "" : result2.get("network.retry.default").toString());
		}

		Code operationCode = codeManager.getCodeByCode("8.3.2");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);

		
		mav.addObject("metering_name",		result.containsKey("metering_name") != true ? "" : result.get("metering_name").toString());
		mav.addObject("metering_suspend", 	result.containsKey("metering_suspend") != true ? "" : result.get("metering_suspend"));
		mav.addObject("metering_condition",	result.containsKey("metering_condition") != true ? "" : result.get("metering_condition").toString());
		mav.addObject("metering_task",		result.containsKey("metering_task") != true ? "" : result.get("metering_task").toString());

		mav.addObject("recovery_name",		result.containsKey("recovery_name") != true ? "" : result.get("recovery_name").toString());
		mav.addObject("recovery_suspend", 	result.containsKey("recovery_suspend") != true ? "" : result.get("recovery_suspend"));
		mav.addObject("recovery_condition",	result.containsKey("recovery_condition") != true ? "" : result.get("recovery_condition").toString());
		mav.addObject("recovery_task",		result.containsKey("recovery_task") != true ? "" : result.get("recovery_task").toString());

		mav.addObject("upgrade_name",		result.containsKey("upgrade_name") != true ? "" : result.get("upgrade_name").toString());
		mav.addObject("upgrade_suspend",	result.containsKey("upgrade_suspend") != true ? "" : result.get("upgrade_suspend"));
		mav.addObject("upgrade_condition",	result.containsKey("upgrade_condition") != true ? "" : result.get("upgrade_condition").toString());
		mav.addObject("upgrade_task",		result.containsKey("upgrade_task") != true ? "" : result.get("upgrade_task").toString());

		mav.addObject("upload_name",		result.containsKey("upload_name") != true ? "" : result.get("upload_name").toString());
		mav.addObject("upload_suspend", 	result.containsKey("upload_suspend") != true ? "" : result.get("upload_suspend"));
		mav.addObject("upload_condition",	result.containsKey("upload_condition") != true ? "" : result.get("upload_condition").toString());
		mav.addObject("upload_task",		result.containsKey("upload_task") != true ? "" : result.get("upload_task").toString());

		return mav;
	}		
	
	@RequestMapping(value = "/gadget/device/command/cmdMcuSetSchedule")
	public ModelAndView cmdMcuSetSchedule(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "meteringsuspend", required = false) boolean meteringsuspend,
			@RequestParam(value = "meteringcondition", required = false) String meteringcondition,
			@RequestParam(value = "meteringtask", required = false) String meteringtask,
			@RequestParam(value = "recoverysuspend", required = false) boolean recoverysuspend,
			@RequestParam(value = "recoverycondition", required = false) String recoverycondition,
			@RequestParam(value = "recoverytask", required = false) String recoverytask,
			@RequestParam(value = "upgradesuspend", required = false) boolean upgradesuspend,
			@RequestParam(value = "upgradecondition", required = false) String upgradecondition,
			@RequestParam(value = "upgradetask", required = false) String upgradetask,
			@RequestParam(value = "uploadsuspend", required = false) boolean uploadsuspend,
			@RequestParam(value = "uploadcondition", required = false) String uploadcondition,
			@RequestParam(value = "uploadtask", required = false) String uploadtask,
			@RequestParam(value = "retrycondition", required = false) String retrycondition
			) {
		
		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");
		
		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.3.3")){
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			List<ArrayList<String>> scheduleList = new ArrayList<ArrayList<String>>();
			ArrayList<String> schedule = new ArrayList<String>();
			
			if(!meteringtask.isEmpty() || !meteringcondition.isEmpty()){
				schedule.add("metering");
				if (meteringsuspend == true) {
					schedule.add("1");
				}else {
					schedule.add("0");
				}
				schedule.add(meteringcondition);
				schedule.add(meteringtask);			
				scheduleList.add(schedule);
			} 
			
			if(!recoverycondition.isEmpty() || !recoverytask.isEmpty()){
				schedule = new ArrayList<String>();
				schedule.add("recovery");
				if (recoverysuspend == true) {
					schedule.add("1");
				}else {
					schedule.add("0");
				}
				schedule.add(recoverycondition);
				schedule.add(recoverytask);			
				scheduleList.add(schedule);	
			}
				
			if(!upgradecondition.isEmpty() || !upgradetask.isEmpty()){
				schedule = new ArrayList<String>();
				schedule.add("upgrade");
				if (upgradesuspend == true) {
					schedule.add("1");
				}else {
					schedule.add("0");
				}
				schedule.add(upgradecondition);
				schedule.add(upgradetask);			
				scheduleList.add(schedule);	
			}
								
			if(!uploadcondition.isEmpty() || !uploadtask.isEmpty()){
				schedule = new ArrayList<String>();
				schedule.add("upload");
				if (uploadsuspend == true) {
					schedule.add("1");
				}else {
					schedule.add("0");
				}
				schedule.add(uploadcondition);
				schedule.add(uploadtask);			
				scheduleList.add(schedule);
			}
												
			if(scheduleList.size() < 1){
				status = ResultStatus.FAIL;
				rtnStr = "No Item to set schedule.";
			}else{
				cmdOperationUtil.cmdMcuSetSchedule(mcu.getSysID(), scheduleList);
				status = ResultStatus.SUCCESS;
			}			
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.getMessage();
			log.error(e, e);
		}
		// set Mcu Retry Interval 커맨드 전송 (세팅에 필요한 커맨드가 서로 다름)
		try{						
			String[] cmdKeys = new String[]{"network.retry.default"};
			String[] cmdKeyValues = new String[]{retrycondition};
			if(!retrycondition.isEmpty()){
				cmdOperationUtil.cmdMcuSetProperty(mcu.getSysID(), cmdKeys, cmdKeyValues);
			}else{
				// 결과메시지는 스케줄 커맨드의 것을 따름.
			}			
		} catch (Exception e) {			
			log.error(e, e);
			
		}

		Code operationCode = codeManager.getCodeByCode("8.3.3");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		
		return mav;
	}
	
	@RequestMapping(value = "/gadget/device/command/cmdDcuGroupSetSchedule")
	public ModelAndView cmdDcuGroupSetSchedule(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "meteringcondition", required = false) String meteringcondition,
			@RequestParam(value = "meteringsuspend", required = false) boolean meteringsuspend,
			@RequestParam(value = "meteringtask", required = false) String meteringtask,
			@RequestParam(value = "recoverycondition", required = false) String recoverycondition,
			@RequestParam(value = "recoverysuspend", required = false) boolean recoverysuspend,
			@RequestParam(value = "recoverytask", required = false) String recoverytask,
			@RequestParam(value = "uploadcondition", required = false) String uploadcondition,
			@RequestParam(value = "uploadsuspend", required = false) boolean uploadsuspend,
			@RequestParam(value = "uploadtask", required = false) String uploadtask,
			@RequestParam(value = "retrycondition", required = false) String retrycondition
			) {
		
		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");
		
		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.3.3")){
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		// Retrieve the DCU with SYS_ID
		MCU mcu = mcuManager.getMCU(target);
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		
		// Default Configuration (17-03-07)
		if(meteringtask == null || recoverytask == null){
			meteringtask = "metering option=1, offset=0, count=25, async=true, mode=poll, filter-poll=1";
			recoverytask = "metering option=2, offset=0, count=0, async=true, mode=poll, filter-poll=2";
			uploadtask = "upload";
		}

		try {
			List<ArrayList<String>> scheduleList = new ArrayList<ArrayList<String>>();
			ArrayList<String> schedule = new ArrayList<String>();
			
			if(!meteringtask.isEmpty() || !meteringcondition.isEmpty()){
				schedule.add("metering");
				if (meteringsuspend == true) {
					schedule.add("1");
				}else {
					schedule.add("0");
				}
				// crontab check 
				//...
				
				schedule.add(meteringcondition);
				schedule.add(meteringtask);			
				scheduleList.add(schedule);
			} 
			
			if(!recoverycondition.isEmpty() || !recoverytask.isEmpty()){
				schedule = new ArrayList<String>();
				schedule.add("recovery");
				if (recoverysuspend == true) {
					schedule.add("1");
				}else {
					schedule.add("0");
				}
				// crontab check
				//...
				
				schedule.add(recoverycondition);
				schedule.add(recoverytask);			
				scheduleList.add(schedule);	
			}
				
			if(!uploadcondition.isEmpty() || !uploadtask.isEmpty()){
				schedule = new ArrayList<String>();
				schedule.add("upload");
				if (uploadsuspend == true) {
					schedule.add("1");
				}else {
					schedule.add("0");
				}
				// crontab check
				// ...
				
				schedule.add(uploadcondition);
				schedule.add(uploadtask);			
				scheduleList.add(schedule);
			}
												
			if(scheduleList.size() < 1){
				status = ResultStatus.FAIL;
				rtnStr = "No Item to set schedule.";
			}else{
				cmdOperationUtil.cmdMcuSetSchedule(mcu.getSysID(), scheduleList);
				status = ResultStatus.SUCCESS;
			}			
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.getMessage();
			log.error(e, e);
		}
		// set Mcu Retry Interval 커맨드 전송 (세팅에 필요한 커맨드가 서로 다름)
		try{						
			String[] cmdKeys = new String[]{"network.retry.default"};
			String[] cmdKeyValues = new String[]{retrycondition};
			if(!retrycondition.isEmpty()){
				cmdOperationUtil.cmdMcuSetProperty(mcu.getSysID(), cmdKeys, cmdKeyValues);
			}else{
				// 결과메시지는 스케줄 커맨드의 것을 따름.
			}			
		} catch (Exception e) {			
			log.error(e, e);
			
		}

		Code operationCode = codeManager.getCodeByCode("8.3.3");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		
		return mav;
	}
	
	
	/**
	 * APN [get/set]
	 * 
	 * @param mdsId
	 *  
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdGetApn")
	public ModelAndView cmdGetApn(@RequestParam(value = "mdsId") String mdsId,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "requestType") String requestType ) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String, Object>();

		/*
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.4")) {
			mav.addObject("rtnStr", "Your role have no permission");
			return mav;
		}
		*/
		// check modemId
		if (mdsId == null || "".equals(mdsId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} else {
			// 모뎀 조회는 CommandGW에서 한번에 실행
			// WS명령도 고려하여 조회를 공통순서 한번만 하도록 합니다
		}

		// call command 
		String rtnStr = "";
		try {
			result = cmdOperationUtil.cmdGetApn(mdsId);
			if (result.containsKey("apn")) {
				status = ResultStatus.SUCCESS;
			}
		} catch (Exception e) {
			log.error(e, e);
			status = ResultStatus.FAIL;
			rtnStr = e.getMessage();
			mav.addObject("rtnStr", rtnStr);
		}
		for (String key : result.keySet()) {
			mav.addObject(key, result.get(key).toString());
		}
		mav.addObject("status", status.name());
		return mav;
	}

	/**
	 * retry count command [get/set]
	 * modemId, loginId, requestType, requestValue
	 * @return
	 */
	@RequestMapping(value = "gadget/device/command/cmdRetryCount")
	public ModelAndView cmdRetryCount(@RequestParam(value = "modemId", required = false) String modemId,
									  @RequestParam(value = "loginId") String loginId,
									  @RequestParam(value = "requestType") String requestType,
									  @RequestParam(value = "requestValue", required = false) Integer requestValue) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String, Object>();

		// Permission of command operation
		if (requestType.equals("GET") && !commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.2")) {
            Code requiredCode = codeManager.getCodeByCode("8.2.2");
            mav.addObject("rtnStr", "Your role have no permission. ["+requiredCode.getName()+"]");
            return mav;
		}else if(requestType.equals("SET") && !commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.3")){
            Code requiredCode = codeManager.getCodeByCode("8.2.3");
            mav.addObject("rtnStr", "Your role have no permission. ["+requiredCode.getName()+"]");
            return mav;
        }
		// Null check of modem Id (Mandatory option)
		if (modemId == null || "".equals(modemId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} else {
			// Modem object will be checked at CommandGW.java
			// Because of another operation is comming from WebService(SOAP)
		}

		// call function
		String rtnStr = "";
		int retryValue = requestValue == null ? 0 : requestValue;
		// load the modem
		Modem modem = modemManager.getModem(Integer.parseInt(modemId));
		// convert a proxy to the data
		Hibernate.initialize(modem);
		if (modem instanceof HibernateProxy) {
			modem = (Modem) ((HibernateProxy) modem).getHibernateLazyInitializer()
					.getImplementation();
		}
		// MBB
		if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){ 
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("modemId", modemId);
            paramMap.put("requestType", requestType);
            paramMap.put("retryCount", retryValue+"");
            try{

            	if(requestType.equals("GET"))
            		result = sendSmsForCmdServer(modem,
                            SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(),
                            SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "RetryCount_GET", paramMap);
            	else
            		result = sendSmsForCmdServer(modem,
                            SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(),
                            SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "RetryCount", paramMap);
            	/*if (result.containsKey("retryCount")) {
					status = ResultStatus.SUCCESS;
				}*/
            	if(result != null){
					status = ResultStatus.SUCCESS;
				}else{
                    rtnStr = "Fail to send a SMS";
                    status = ResultStatus.FAIL;
                    mav.addObject("rtnStr", rtnStr);
				}

            } catch (Exception e) {
				log.error(e, e);
				status = ResultStatus.FAIL;
				rtnStr = e.getMessage();
				mav.addObject("rtnStr", rtnStr);
			}
       }else{
		// RF, ETH
			try {
				result = cmdOperationUtil.cmdRetryCount(modemId, requestType, retryValue);
				if (result.containsKey("retryCount")) {
					status = ResultStatus.SUCCESS;
				}
			} catch (Exception e) {
				log.error(e, e);
				status = ResultStatus.FAIL;
				rtnStr = e.getMessage();
				mav.addObject("rtnStr", rtnStr);
			}
		}
		for (String key : result.keySet()) {
			mav.addObject(key, result.get(key).toString());
			if(key.contains("NI_RESULT")){
				mav.addObject("retryCount", result.get(key).toString());
			}
		}

		mav.addObject("status", status.name());
		return mav;
	}
    /**
     * GroupCommand -> retry count command [set]
     * meterId, loginId, requestValue
     * @return
     */
    @RequestMapping(value = "gadget/device/command/cmdGroupRetryCount")
    public ModelAndView cmdGroupRetryCount(@RequestParam(value = "mdsId") String mdsId,
                                           @RequestParam(value = "loginId") String loginId,
                                           @RequestParam(value = "requestValue") Integer requestValue) {
        ResultStatus status = ResultStatus.FAIL;
        ModelAndView mav = new ModelAndView("jsonView");
        String modemId = null;

        // Permission of command operation (8.2.3=modem schedule set)
        // --skip

        // Null check of meter Id (Mandatory option)
        if (mdsId == null || "".equals(mdsId)) {
            status = ResultStatus.INVALID_PARAMETER;
            mav.addObject("rtnStr", "Target ID(meter id) is null!");
            mav.addObject("status", status.name());
            return mav;
        } else {
            // find the modem which connected with meter
            Meter tMeter = meterManager.getMeter(mdsId);
            modemId = tMeter.getModemId().toString();
        }
        if (modemId == null){
            status = ResultStatus.INVALID_PARAMETER;
            mav.addObject("rtnStr", "Target Modem is null!");
            mav.addObject("status", status.name());
            return mav;
        }

        // call "cmdRetryCount" function
        String rtnStr = "";
        int retryValue = requestValue == null ? 0 : requestValue;
        try {
            mav = this.cmdRetryCount(modemId,loginId,"SET",retryValue);
        } catch (Exception e) {
            log.error(e, e);
            status = ResultStatus.FAIL;
            rtnStr = e.getMessage();
            mav.addObject("rtnStr", rtnStr);
        }
        return mav;
    }


	/**
	 * Set Enable or Disable of DCU's SNMP-TRAP Uploading
	 * @param mcuId
	 * @param requestType set/get
	 * @param requestValue 1(Enable), 0(Disable)
	 * @return
	 */
	@RequestMapping(value = "gadget/device/command/cmdDcuSnmpEnableDisable")
    public ModelAndView cmdDcuSnmpEnableDisable(@RequestParam(value="mcuId") String mcuId,
                                                @RequestParam(value="loginId") String loginId,
                                                @RequestParam(value="requestType") String requestType,
                                                @RequestParam(value="snmpStatus") String requestValue){
        ResultStatus status = ResultStatus.FAIL;

        ModelAndView mav = new ModelAndView("jsonView");
        Map<String, Object> result = new HashMap<String,Object>();

        // Permission of command operation
        if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.3.6")){
            Code requiredCode = codeManager.getCodeByCode("8.3.6");
            mav.addObject("rtnStr", "Your role have no permission. ["+requiredCode.getName()+"]");
            return mav;
        }
        if (mcuId == null || "".equals(mcuId)) {
            status = ResultStatus.INVALID_PARAMETER;
            mav.addObject("rtnStr", "Target ID is null!");
            return mav;
        }

        String rtnStr = " Disable(0), Enable(1)";
        MCU mcu = mcuManager.getMCU(Integer.parseInt(mcuId));
        Supplier supplier = mcu.getSupplier();
        if (supplier == null) {
            Operator operator = operatorManager.getOperatorByLoginId(loginId);
            supplier = operator.getSupplier();
        }

        String propKey = "snmp.trap.enable.default";
        try {
            if(requestType.contains("SET")){
                //SET
                result = cmdOperationUtil.cmdMcuSetProperty(mcu.getSysID(), new String[]{propKey}, new String[]{requestValue});
            }else{
                //GET
                result = cmdOperationUtil.cmdMcuGetProperty(mcu.getSysID(), propKey);
            }
        } catch (Exception e) {
            status = ResultStatus.FAIL;
            rtnStr = e.getMessage();
            log.error(e, e);
        }

        if(result.containsKey("cmdResult")){
            status = ResultStatus.SUCCESS;
            mav.addObject("cmdResult", result.get("cmdResult").toString() == null ? "" : result.get("cmdResult").toString());
			mav.addObject("snmpStatus", "No Error");
			if(requestType.contains("GET")){
				mav.addObject("snmpStatus", result.get(propKey).toString() == null ? "" : result.get(propKey).toString());
			}
		}else{
            mav.addObject("cmdResult", "No Result");
        }

        Code operationCode = codeManager.getCodeByCode("8.3.6");
        if (operationCode != null) {
            operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
                    status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
        }
        mav.addObject("status", status.name());
        mav.addObject("rtnStr", rtnStr);

        return mav;
    }


	/**
	 * NI : metering interval command [get/set]
	 * modemId, loginId, requestType, requestValue
	 * @return
	 */
	@RequestMapping(value = "gadget/device/command/cmdMeteringInterval")
	@SuppressWarnings("unchecked")
	public ModelAndView cmdMeteringInterval(@RequestParam(value = "modemId") String modemId,
									  @RequestParam(value = "loginId") String loginId,
									  @RequestParam(value = "requestType") String requestType,
									  @RequestParam(value = "requestValue", required = false) Integer requestValue) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String, Object>();

		// Permission of command operation
		if (requestType.equals("GET") && !commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.2")){
			// 8.2.2 == modem schedule get
			Code requiredCode = codeManager.getCodeByCode("8.2.2");
			mav.addObject("rtnStr", "Your role have no permission. ["+requiredCode.getName()+"]");
			return mav;
		}else if(requestType.equals("SET") && !commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.3")){
			// 8.2.3 == modem schedule set
			Code requiredCode = codeManager.getCodeByCode("8.2.3");
            mav.addObject("rtnStr", "Your role have no permission. ["+requiredCode.getName()+"]");
            return mav;
        }
		// Null check of modem Id (Mandatory option)
		if (modemId == null || "".equals(modemId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} else {
			// Modem object will be checked at CommandGW.java
			// Because of another operation is comming from WebService(SOAP)
		}

		// call function
		String rtnStr = "";
		int intervalValue = requestValue == null ? 3600 : requestValue;
		// load the modem
		Modem modem = modemManager.getModem(Integer.parseInt(modemId));
		// convert a proxy to the data
		Hibernate.initialize(modem);
		if (modem instanceof HibernateProxy) {
			modem = (Modem) ((HibernateProxy) modem).getHibernateLazyInitializer()
					.getImplementation();
		}

        // MBB(SMS)
		if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
            Map<String, String> asyncResult = new HashMap<String, String>();
            try {
                Map<String, String> paramMap = new HashMap<String, String>();
                paramMap.put("modemId", modemId);
                paramMap.put("requestType", requestType);
                paramMap.put("interval", intervalValue+"");

                 String logName = "MeteringInterval_GET";
                 if(requestType.contains("SET")){
                     logName = "MeteringInterval";
                 }

                 asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(),
                         SMSConstants.COMMAND_TYPE.NI.getTypeCode(), logName, paramMap);

                 if(asyncResult != null){
                     status = ResultStatus.SUCCESS;
                 }else{
                     log.debug("SMS Fail");
                     rtnStr = "SMS Fail\n";
                     status = ResultStatus.FAIL;
                     mav.addObject("rtnStr", rtnStr);
                 }
             } catch (Exception e) {
                log.error("SMS Fail : " + e,e);
                rtnStr = "SMS Fail\n";
				status = ResultStatus.FAIL;
                mav.addObject("rtnStr", "Check the results in Async History tab");
			}
            for (String key : asyncResult.keySet()) {
                mav.addObject(key, asyncResult.get(key).toString());
                if(key.contains("NI_RESULT")){
                    mav.addObject("cmdResult", asyncResult.get(key).toString());
                }
            }
		}else{
            // RF or Ethernet
			try {

				result = cmdOperationUtil.cmdMeteringInterval(modemId, requestType, intervalValue);
				if (result.containsKey("interval")) {
					status = ResultStatus.SUCCESS;
				}
			} catch (Exception e) {
				log.error(e, e);
				status = ResultStatus.FAIL;
				rtnStr = e.getMessage();
				mav.addObject("rtnStr", rtnStr);
			}
            for (String key : result.keySet()) {
                mav.addObject(key, result.get(key).toString());
            }
		}

		mav.addObject("status", status.name());
		return mav;
	}
	/**
	 * GroupCommand -> metering interval command [set]
     * mdsId(meter serial), loginId, requestValue
     * @return
	 */
    @RequestMapping(value = "gadget/device/command/cmdGroupMeteringInterval")
    public ModelAndView cmdGroupMeteringInterval(@RequestParam(value = "mdsId") String mdsId,
                                            @RequestParam(value = "loginId") String loginId,
                                            @RequestParam(value = "requestValue") Integer requestValue) {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
        String modemId = null;

        // Null check of meter Id (Mandatory option)
        if (mdsId == null || "".equals(mdsId)) {
            status = ResultStatus.INVALID_PARAMETER;
            mav.addObject("rtnStr", "Target ID(modem id) is null!");
			mav.addObject("status", status.name());
            return mav;
        } else {
            // find the modem which connected with meter
            Meter tMeter = meterManager.getMeter(mdsId);
            modemId = tMeter.getModemId().toString();
        }
        if (modemId == null){
            status = ResultStatus.INVALID_PARAMETER;
            mav.addObject("rtnStr", "Target Modem is null!");
			mav.addObject("status", status.name());
            return mav;
        }

        // Call 'Metering Interval' function
        String rtnStr = "";
        int intervalValue = requestValue == null ? 3600 : requestValue;
        try {
            //result = cmdOperationUtil.cmdMeteringInterval(modemId, "SET", intervalValue);
			mav = this.cmdMeteringInterval(modemId, loginId, "SET", intervalValue);
            /*if (result.containsKey("interval")) {
                status = ResultStatus.SUCCESS;
            }*/
        } catch (Exception e) {
            log.error(e, e);
            status = ResultStatus.FAIL;
            rtnStr = e.getMessage();
            mav.addObject("rtnStr", rtnStr);
        }
        /*for (String key : result.keySet()) {
            mav.addObject(key, result.get(key).toString());
        }
        */
        return mav;
    }

	/**
	 * Modem Reset Time [set]
	 * modemId, loginId, requestType, requestValue
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "gadget/device/command/cmdModemResetTime")
	public ModelAndView cmdModemResetTime(@RequestParam(value = "modemId") String modemId,
									  @RequestParam(value = "loginId") String loginId,
									  @RequestParam(value = "requestType") String requestType,
									  @RequestParam(value = "resetTime") Integer resetTime) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String, Object>();

//		// Permission of command operation
//		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.4")) {
//			mav.addObject("rtnStr", "Your role have no permission");
//			return mav;
//		}
		// Null check of modem Id (Mandatory option)
		if (modemId == null || "".equals(modemId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} else {
			// Modem object will be checked at CommandGW.java
			// Because of another operation is comming from WebService(SOAP)
		}

		// call function
		String rtnStr = "";
		Modem modem = modemManager.getModem(Integer.parseInt(modemId));
		
		// MBB(SMS)
		if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
				Map<String, String> asyncResult = new HashMap<String, String>();
			try{ 
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("modemId", modemId);
				paramMap.put("requestType", requestType);
				paramMap.put("resetTime", resetTime+"");
				asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "ModemResetTime", paramMap);
					 
				if(asyncResult != null){
					status = ResultStatus.SUCCESS;
				}else{
					log.debug("SMS Fail");
					mav.addObject("rtnStr", "Check the results in Async History tab");
					status = ResultStatus.FAIL;
				}
			}catch(Exception e){
				log.debug("SMS Fail");
				mav.addObject("rtnStr", "Check the results in Async History tab");
				status = ResultStatus.FAIL;
			}
			for (String key : asyncResult.keySet()) {
				mav.addObject(key, asyncResult.get(key).toString());
				if(key.contains("NI_RESULT")){
					mav.addObject("status", asyncResult.get(key).toString());
				}
			}
			mav.addObject("status", status.name());
			return mav;
	    // RF or Ethernet	
		}else{ 
			try {
				result = cmdOperationUtil.cmdModemResetTime(modemId, requestType, resetTime);
				if (result.containsKey("status")) {
					status = ResultStatus.SUCCESS;
				}
			} catch (Exception e) {
				log.error(e, e);
				status = ResultStatus.FAIL;
				rtnStr = e.getMessage();
				mav.addObject("rtnStr", rtnStr);
			}
			for (String key : result.keySet()) {
				mav.addObject(key, result.get(key).toString());
			}
			mav.addObject("status", status.name());
			return mav;
		}
	}

	/**
	 * NI Protocol :  Modem Mode [get/set]
	 * modemId, loginId, requestType, mode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "gadget/device/command/cmdModemMode")
	public ModelAndView cmdModemMode(@RequestParam(value = "modemId") String modemId,
									  @RequestParam(value = "loginId") String loginId,
									  @RequestParam(value = "requestType") String requestType,
									  @RequestParam(value = "mode") Integer mode) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String, Object>();

//		// Permission of command operation
//		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.4")) {
//			mav.addObject("rtnStr", "Your role have no permission");
//			return mav;
//		}
		// Null check of modem Id (Mandatory option)
		if (modemId == null || "".equals(modemId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} else {
			// Modem object will be checked at CommandGW.java
			// Because of another operation is comming from WebService(SOAP)
		}

		// call function
		String rtnStr = "";
		Modem modem = modemManager.getModem(Integer.parseInt(modemId));
		
		// MBB(SMS)
		if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
				Map<String, String> asyncResult = new HashMap<String, String>();
			try{ 
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("modemId", modemId);
				paramMap.put("requestType", requestType);
				paramMap.put("mode", mode+"");
				if ( "GET".equals(requestType)){
					asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "ModemMode_GET", paramMap);	
				}
				else {
					asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "ModemMode", paramMap);
				} 
				if(asyncResult != null){
					status = ResultStatus.SUCCESS;
				}else{
					log.debug("SMS Fail");
					mav.addObject("rtnStr", "Check the results in Async History tab");
					status = ResultStatus.FAIL;
				}
			}catch(Exception e){
				log.debug("SMS Fail");
				mav.addObject("rtnStr", "Check the results in Async History tab");
				status = ResultStatus.FAIL;
			}
			for (String key : asyncResult.keySet()) {
				mav.addObject(key, asyncResult.get(key).toString());
				if(key.contains("NI_RESULT")){
					mav.addObject("modemMode", asyncResult.get(key).toString());
				}
			}
			mav.addObject("status", status.name());
			return mav;
	    // RF or Ethernet	
		}else{
			try {
				result = cmdOperationUtil.cmdModemMode(modemId, requestType, mode);
				if (result.containsKey("modemMode")) {
					status = ResultStatus.SUCCESS;
				}
			} catch (Exception e) {
				log.error(e, e);
				status = ResultStatus.FAIL;
				rtnStr = e.getMessage();
				mav.addObject("rtnStr", rtnStr);
			}
			for (String key : result.keySet()) {
				mav.addObject(key, result.get(key).toString());
			}
			mav.addObject("status", status.name());
			return mav;
			}
	}
	/**
	 * NI Protocol : SNMP Server IPv6/Port  [get/set]
	 * modemId, loginId, requestType, type, ipAddress, port
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "gadget/device/command/cmdSnmpServerIpv6Port")
	public ModelAndView cmdSnmpServerIpv6Port(@RequestParam(value = "modemId") String modemId,
									  @RequestParam(value = "loginId") String loginId,
									  @RequestParam(value = "requestType") String requestType,
									  @RequestParam(value = "type") Integer type,
									  @RequestParam(value = "ipAddress") String ipAddress,
									  @RequestParam(value = "port") Integer port
									  ) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String, Object>();

//		// Permission of command operation
//		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.4")) {
//			mav.addObject("rtnStr", "Your role have no permission");
//			return mav;
//		}
		// Null check of modem Id (Mandatory option)
		if (modemId == null || "".equals(modemId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} else {
			// Modem object will be checked at CommandGW.java
			// Because of another operation is comming from WebService(SOAP)
		}

		// call function
		String rtnStr = "";

		//
		String ipAddrfmt = "";
		String portfmt = "";
		
		
		try {
			if ( requestType.equals("SET")){
				StringBuffer ipAddrBuf = new StringBuffer();
				String ip[] = null;
				if ( type == 0 ) {
					ip = ipAddress.split("\\.", 4);
					for ( int i = 0; i < ip.length; i++){
						byte b[] = new byte[1];
						b[0] = (byte)(Integer.parseInt(ip[i]) & 0xff );
						ipAddrBuf.append(Hex.decode(b));
					}
				}
				else if ( type == 1 ){ // ipv6
					ip = ipAddress.split("\\:", 8);
					for ( int i = 0; i < ip.length; i++){
						byte b[] = DataUtil.get2ByteToInt((Integer.parseInt(ip[i],16) & 0xffff ));
						ipAddrBuf.append(Hex.decode(b));
					}
				}
				if ( ip == null ){
					throw new Exception("Invalid ipAddress");
				}

				ipAddrfmt = ipAddrBuf.toString();
				portfmt = Hex.decode(DataUtil.get2ByteToInt(port));
			}
			
			Modem modem = modemManager.getModem(Integer.parseInt(modemId));
			
			// MBB(SMS)
			if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
					Map<String, String> asyncResult = new HashMap<String, String>();
				try{ 
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("modemId", modemId);
					paramMap.put("requestType", requestType);
					paramMap.put("type", type+"");
					paramMap.put("ipAddrfmt", ipAddrfmt);
					paramMap.put("portfmt", portfmt);
					asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "ModemPortInformation", paramMap);
						 
					if(asyncResult != null){
						status = ResultStatus.SUCCESS;
					}else{
						log.debug("SMS Fail");
						mav.addObject("rtnStr", "Check the results in Async History tab");
						status = ResultStatus.FAIL;
					}
				}catch(Exception e){
					log.debug("SMS Fail");
					mav.addObject("rtnStr", "Check the results in Async History tab");
					status = ResultStatus.FAIL;
				}
				for (String key : asyncResult.keySet()) {
					mav.addObject(key, asyncResult.get(key).toString());
					if(key.contains("NI_RESULT")){
						mav.addObject("SnmpSeverIpPort", asyncResult.get(key).toString());
					}
				}
				mav.addObject("status", status.name());
				return mav;
		    // RF or Ethernet	
			}else{
				result = cmdOperationUtil.cmdSnmpServerIpv6Port(modemId, requestType, type, ipAddrfmt, portfmt);
				if (result.containsKey("SnmpSeverIpPort")) {
					status = ResultStatus.SUCCESS;
				}
			}
		} catch (Exception e) {
			log.error(e, e);
			status = ResultStatus.FAIL;
			rtnStr = e.getMessage();
			mav.addObject("rtnStr", rtnStr);
		}
		for (String key : result.keySet()) {
			mav.addObject(key, result.get(key).toString());
		}
		mav.addObject("status", status.name());
		return mav;
	}
	/**
	 * NI Protocol : SNMP Server IPv6/Port  [get/set]
	 * modemId, loginId, requestType, type, ipAddress, port
	 * @return
	 */
	@RequestMapping(value = "gadget/device/command/cmdModemIpInformation")
	public ModelAndView cmdModemIpInformation(@RequestParam(value = "modemId") String modemId,
									  @RequestParam(value = "loginId") String loginId,
									  @RequestParam(value = "requestType") String requestType,
									  @RequestParam(value = "targetType") Integer targetType,
									  @RequestParam(value = "ipType") Integer ipType,
									  @RequestParam(value = "ipAddress") String ipAddress
									  ) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String, Object>();

//		// Permission of command operation
//		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.4")) {
//			mav.addObject("rtnStr", "Your role have no permission");
//			return mav;
//		}
		// Null check of modem Id (Mandatory option)
		if (modemId == null || "".equals(modemId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} else {
			// Modem object will be checked at CommandGW.java
			// Because of another operation is comming from WebService(SOAP)
		}

		// call function
		String rtnStr = "";

		//
		String ipAddrfmt = "";
		String portfmt = "";
		try {
			if ( requestType.equals("SET")){
				StringBuffer ipAddrBuf = new StringBuffer();
				String ip[] = null;
				if ( ipType == 0 ) {
					ip = ipAddress.split("\\.", 4);
					for ( int i = 0; i < ip.length; i++){
						byte b[] = new byte[1];
						b[0] = (byte)(Integer.parseInt(ip[i]) & 0xff );
						ipAddrBuf.append(Hex.decode(b));
					}
				}
				else if ( ipType == 1 ){ // ipv6
					ip = ipAddress.split("\\:", 8);
					for ( int i = 0; i < ip.length; i++){
						if ( "".equals(ip[i])){
							ip[i] = "0";
						}
							
						byte b[] = DataUtil.get2ByteToInt((Integer.parseInt(ip[i],16) & 0xffff ));
						ipAddrBuf.append(Hex.decode(b));
					}
				}
				ipAddrfmt = ipAddrBuf.toString();
				if ( ip == null ){
					throw new Exception("Invalid ipAddress");
				}
			}
			Modem modem = modemManager.getModem(Integer.parseInt(modemId));
			
			// MBB(SMS)
			if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
					Map<String, String> asyncResult = new HashMap<String, String>();
				try{ 
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("modemId", modemId);
					paramMap.put("requestType", requestType);
					paramMap.put("targetType", targetType+"");
					paramMap.put("ipType", ipType+"");
					paramMap.put("ipAddress", ipAddrfmt);
					if ( "GET".equals(requestType )){
						asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "ModemIpInformation_GET", paramMap);					
					}else {
						asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "ModemIpInformation", paramMap);
					}
					if(asyncResult != null){
						status = ResultStatus.SUCCESS;
					}else{
						log.debug("SMS Fail");
						mav.addObject("rtnStr", "Check the results in Async History tab");
						status = ResultStatus.FAIL;
					}
				}catch(Exception e){
					log.debug("SMS Fail");
					mav.addObject("rtnStr", "Check the results in Async History tab");
					status = ResultStatus.FAIL;
				}
				for (String key : asyncResult.keySet()) {
					mav.addObject(key, asyncResult.get(key).toString());
					if(key.contains("NI_RESULT")){
						mav.addObject("ModemIpInformation", asyncResult.get(key).toString());
					}
				}
				mav.addObject("status", status.name());
				return mav;
		    // RF or Ethernet	
			}else{
				result = cmdOperationUtil.cmdModemIpInformation(modemId, requestType, targetType,ipType, ipAddrfmt);
				if (result.containsKey("ModemIpInformation")) {
					status = ResultStatus.SUCCESS;
				}
			}
		} catch (Exception e) {
			log.error(e, e);
			status = ResultStatus.FAIL;
			rtnStr = e.getMessage();
			mav.addObject("rtnStr", rtnStr);
		}
		for (String key : result.keySet()) {
			mav.addObject(key, result.get(key).toString());
		}
		mav.addObject("status", status.name());
		return mav;
	}
	
	/**
	 * NI Protocol : SNMP Server IPv6/Port  [get/set]
	 * modemId, loginId, requestType, type, ipAddress, port
	 * @return
	 */
	@RequestMapping(value = "gadget/device/command/cmdModemPortInformation")
	public ModelAndView cmdModemPortInformation(@RequestParam(value = "modemId") String modemId,
									  @RequestParam(value = "loginId") String loginId,
									  @RequestParam(value = "requestType") String requestType,
									  @RequestParam(value = "targetType") Integer targetType,
									  @RequestParam(value = "port") Integer port
									  ) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String, Object>();

//		// Permission of command operation
//		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.4")) {
//			mav.addObject("rtnStr", "Your role have no permission");
//			return mav;
//		}
		// Null check of modem Id (Mandatory option)
		if (modemId == null || "".equals(modemId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} else {
			// Modem object will be checked at CommandGW.java
			// Because of another operation is comming from WebService(SOAP)
		}

		// call function
		String rtnStr = "";

		//
		String ipAddrfmt = "";
		String portfmt = "";
		try {
			if ( requestType.equals("SET")){
				portfmt = Hex.decode(DataUtil.get2ByteToInt(port));
			}
			
			Modem modem = modemManager.getModem(Integer.parseInt(modemId));
			
			// MBB(SMS)
			if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
					Map<String, String> asyncResult = new HashMap<String, String>();
				try{ 
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("modemId", modemId);
					paramMap.put("requestType", requestType);
					paramMap.put("targetType", targetType+"");
					paramMap.put("port", portfmt);
					if ( "GET".equals(requestType)){
						asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "ModemPortInformation_GET", paramMap);
					}
					else {
						asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "ModemPortInformation", paramMap);
					}
						 
					if(asyncResult != null){
						status = ResultStatus.SUCCESS;
					}else{
						log.debug("SMS Fail");
						mav.addObject("rtnStr", "Check the results in Async History tab");
						status = ResultStatus.FAIL;
					}
				}catch(Exception e){
					log.debug("SMS Fail");
					mav.addObject("rtnStr", "Check the results in Async History tab");
					status = ResultStatus.FAIL;
				}
				for (String key : asyncResult.keySet()) {
					mav.addObject(key, asyncResult.get(key).toString());
					if(key.contains("NI_RESULT")){
						mav.addObject("ModemPortInformation", asyncResult.get(key).toString());
					}
				}
				mav.addObject("status", status.name());
				return mav;
		    // RF or Ethernet	
			}else{
				result = cmdOperationUtil.cmdModemPortInformation(modemId, requestType, targetType,portfmt);
				if (result.containsKey("ModemPortInformation")) {
					status = ResultStatus.SUCCESS;
				}
			}
		} catch (Exception e) {
			log.error(e, e);
			status = ResultStatus.FAIL;
			rtnStr = e.getMessage();
			mav.addObject("rtnStr", rtnStr);
		}
		for (String key : result.keySet()) {
			mav.addObject(key, result.get(key).toString());
		}
		mav.addObject("status", status.name());
		return mav;
	}
	/**
	 * NI Protocol : Aralm/Event Command ON_OFF [get/set]
	 * modemId, loginId, requestType, mode
	 * @return
	 */
	@RequestMapping(value = "gadget/device/command/cmdAlarmEventCommandOnOff")
	public ModelAndView cmdAlarmEventCommandOnOff(@RequestParam(value = "modemId") String modemId,
									  @RequestParam(value = "loginId") String loginId,
									  @RequestParam(value = "requestType") String requestType,
									  @RequestParam(value = "count") Integer count,
									  @RequestParam(value = "cmds") String cmds) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String, Object>();

//		// Permission of command operation
//		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.4")) {
//			mav.addObject("rtnStr", "Your role have no permission");
//			return mav;
//		}
		// Null check of modem Id (Mandatory option)
		if (modemId == null || "".equals(modemId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} else {
			// Modem object will be checked at CommandGW.java
			// Because of another operation is comming from WebService(SOAP)
		}

		// call function
		String rtnStr = "";
		Modem modem = modemManager.getModem(Integer.parseInt(modemId));
		
		// MBB(SMS)
		if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
				Map<String, String> asyncResult = new HashMap<String, String>();
			try{ 
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("modemId", modemId);
				paramMap.put("requestType", requestType);
				paramMap.put("count", count+"");
				paramMap.put("cmds", cmds);
				if ( "GET".equals(requestType)){
					asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "Alarm_EventCommandON_OFF_GET", paramMap);
				}
				else {
					asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "Alarm_EventCommandON_OFF", paramMap);
				}
				if(asyncResult != null){
					status = ResultStatus.SUCCESS;
				}else{
					log.debug("SMS Fail");
					mav.addObject("rtnStr", "Check the results in Async History tab");
					status = ResultStatus.FAIL;
				}
			}catch(Exception e){
				log.debug("SMS Fail");
				mav.addObject("rtnStr", "Check the results in Async History tab");
				status = ResultStatus.FAIL;
			}
			for (String key : asyncResult.keySet()) {
				mav.addObject(key, asyncResult.get(key).toString());
				if(key.contains("NI_RESULT")){
					mav.addObject("AlarmEventCommand", asyncResult.get(key).toString());
				}
			}
			mav.addObject("status", status.name());
			return mav;
	    // RF or Ethernet	
		}else{
			try {
				result = cmdOperationUtil.cmdAlarmEventCommandOnOff(modemId, requestType, count, cmds);
				if (result.containsKey("AlarmEventCommand")) {
					status = ResultStatus.SUCCESS;
				}
			} catch (Exception e) {
				log.error(e, e);
				status = ResultStatus.FAIL;
				rtnStr = e.getMessage();
				mav.addObject("rtnStr", rtnStr);
			}
			for (String key : result.keySet()) {
				mav.addObject(key, result.get(key).toString());
			}
			mav.addObject("status", status.name());
			return mav;
			}
	}
	
	/**
	 * NI Protocol : Transmit Frequency [get/set]
	 * modemId, loginId, requestType, second
	 * @return
	 */
	@RequestMapping(value = "gadget/device/command/cmdTransmitFrequency")
	public ModelAndView cmdTransmitFrequency(@RequestParam(value = "modemId", required = false) String modemId,
									  @RequestParam(value = "loginId") String loginId,
									  @RequestParam(value = "requestType") String requestType,
									  @RequestParam(value = "second", required = false) Integer second) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String, Object>();

		// Permission of command operation
		if (requestType.equals("GET") && !commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.2")){
			Code requiredCode = codeManager.getCodeByCode("8.2.2");
			mav.addObject("rtnStr", "Your role have no permission. ["+requiredCode.getName()+"]");
			return mav;
		}else if(requestType.equals("SET") && !commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.3")){
			Code requiredCode = codeManager.getCodeByCode("8.2.3");
			mav.addObject("rtnStr", "Your role have no permission. ["+requiredCode.getName()+"]");
			return mav;
		}

		// Null check of modem Id (Mandatory option)
		if (modemId == null || "".equals(modemId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} else {
			// Modem object will be checked at CommandGW.java
			// Because of another operation is comming from WebService(SOAP)
		}

		// call function
		String rtnStr = "";
        int frequencyValue = second == null ? 3600 : second;
        // load the modem
		Modem modem = modemManager.getModem(Integer.parseInt(modemId));
        // convert a proxy to the data
        Hibernate.initialize(modem);
        if (modem instanceof HibernateProxy) {
            modem = (Modem) ((HibernateProxy) modem).getHibernateLazyInitializer()
                    .getImplementation();
        }

		// MBB(SMS)
		if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
				Map<String, String> asyncResult = new HashMap<String, String>();
			try{ 
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("modemId", modemId);
				paramMap.put("requestType", requestType);
				paramMap.put("second", frequencyValue+"");
				if ( "GET".equals(requestType)){
					asyncResult = sendSmsForCmdServer(modem,
                            SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(),
                            SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "TransmitFrequency_GET", paramMap);
				}
				else {
					asyncResult = sendSmsForCmdServer(modem,
                            SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(),
                            SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "TransmitFrequency", paramMap);
				}
					 
				if(asyncResult != null){
					status = ResultStatus.SUCCESS;
				}else{
                    rtnStr = "Fail to send a SMS";
                    status = ResultStatus.FAIL;
                    mav.addObject("rtnStr", rtnStr);
				}

			}catch(Exception e){
                log.error(e, e);
                status = ResultStatus.FAIL;
                rtnStr = e.getMessage();
                mav.addObject("rtnStr", rtnStr);
			}
			for (String key : asyncResult.keySet()) {
				mav.addObject(key, asyncResult.get(key).toString());
				if(key.contains("NI_RESULT")){
					mav.addObject("transmitFrequency", asyncResult.get(key).toString());
				}
			}
		}else{
        // RF or Ethernet
			try {
				result = cmdOperationUtil.cmdTransmitFrequency(modemId, requestType, frequencyValue);
				if (result.containsKey("transmitFrequency")) {
					status = ResultStatus.SUCCESS;
				}
			} catch (Exception e) {
				log.error(e, e);
				status = ResultStatus.FAIL;
				rtnStr = e.getMessage();
				mav.addObject("rtnStr", rtnStr);
			}
			for (String key : result.keySet()) {
				mav.addObject(key, result.get(key).toString());
			}

		}

        mav.addObject("status", status.name());
        return mav;
	} //~ single target
    /**
     * GroupCommand -> transmit frequency command [set]
     * meterId, loginId, requestValue
     * @return
     */
    @RequestMapping(value = "gadget/device/command/cmdGroupTransmitFrequency")
    public ModelAndView cmdGroupTransmitFrequency(@RequestParam(value = "mdsId") String mdsId,
                                                 @RequestParam(value = "loginId") String loginId,
                                                 @RequestParam(value = "requestValue") Integer requestValue) {

        ResultStatus status = ResultStatus.FAIL;
        ModelAndView mav = new ModelAndView("jsonView");
        String modemId = null;

        // Permission of command operation (8.2.3=modem schedule set)
        // --skip

        // Null check of meter Id (Mandatory option)
        if (mdsId == null || "".equals(mdsId)) {
            status = ResultStatus.INVALID_PARAMETER;
            mav.addObject("rtnStr", "Target ID(modem id) is null!");
            mav.addObject("status", status.name());
            return mav;
        } else {
            // find the modem which connected with meter
            Meter tMeter = meterManager.getMeter(mdsId);
            modemId = tMeter.getModemId().toString();
        }
        if (modemId == null){
            status = ResultStatus.INVALID_PARAMETER;
            mav.addObject("rtnStr", "Target Modem is null!");
            mav.addObject("status", status.name());
            return mav;
        }

        // call "cmdTransmitFrequency" function
        String rtnStr = "";
        int intervalValue = requestValue == null ? 3600 : requestValue;
        try {
            mav = this.cmdTransmitFrequency(modemId,loginId,"SET",intervalValue);
            /*result = cmdOperationUtil.cmdTransmitFrequency(modemId, "SET", intervalValue);
            if (result.containsKey("interval")) {
                status = ResultStatus.SUCCESS;
            }*/
        } catch (Exception e) {
            log.error(e, e);
            status = ResultStatus.FAIL;
            rtnStr = e.getMessage();
            mav.addObject("rtnStr", rtnStr);
        }
        /*for (String key : result.keySet()) {
            mav.addObject(key, result.get(key).toString());
        }
        mav.addObject("status", status.name());*/
        return mav;
    }

	
    @RequestMapping(value = "/gadget/device/command/checkIpAddress")
    public ModelAndView checkIpAddress(
    		@RequestParam(value = "ipv4", required = false) String ipv4,
            @RequestParam(value = "ipv6", required = false) String ipv6) {

    	ResultStatus status = ResultStatus.FAIL;
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	Pattern pattern;
    	String regexIPv4 = "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$";
    	String regexIPv6 = "^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]).){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$";
    	String rtnStr = "";
        
    	// ipv4, ipv6 데이터가 모두 없을 경우 - IP 유효성 Check 생략
    	if((ipv4.equals(null) || ipv4.equals("") || ipv4.equals(" ")) 
    		&& (ipv6.equals(null) || ipv6.equals("") || ipv6.equals(" "))) {
    		status = ResultStatus.SUCCESS;
    		mav.addObject("status", status.name());
    		return mav;
    	}

    	// ipv4 데이터가 있을 경우
		if (!(ipv4.equals(null) || ipv4.equals("") || ipv4.equals(" "))) {
			pattern = Pattern.compile(regexIPv4);
			if (pattern.matcher(ipv4).matches() == true) {
				status = ResultStatus.SUCCESS;
			} else {
				status = ResultStatus.FAIL;
				rtnStr = "Does not fit on the IPv4 Address type. ";
				
		    	mav.addObject("status", status.name());
		        mav.addObject("rtnStr", rtnStr);

		        return mav;
			}
		}
    	
    	// ipv6 데이터가 있을 경우
    	if (!(ipv6.equals(null) || ipv6.equals("") || ipv6.equals(" "))) {
			pattern = Pattern.compile(regexIPv6);
			if (pattern.matcher(ipv6).matches() == true) {
				status = ResultStatus.SUCCESS;
			} else {
				status = ResultStatus.FAIL;
				rtnStr = "Does not fit on the IPv6 Address type. ";
				
		    	mav.addObject("status", status.name());
		        mav.addObject("rtnStr", rtnStr);

		        return mav;
			}
		}
    	
    	mav.addObject("status", status.name());
        mav.addObject("rtnStr", rtnStr);

        return mav;
    }
    
    
    /*
     * SMS를 보내고  Async테이블에서 결과를 가져오는 함수 입니다.
     */
    public Map sendSmsForCmdServer(Modem modem, String messageType, String commandCode
    							   ,String commandName, Map<String, String> paramMap) throws Exception{
    	log.debug("[sendSmsAndGetResult] " + " messageType: " + messageType + 
    	        " commandCode: " + commandCode + " commandName: " + commandName);

		/*
		 * 서버에서 모뎀으로 SMS를 보낸뒤 모뎀이 서버에 접속하여 수행해야할 Command가 무엇인지
		 * 구분할 방법이 따로 없기 때문에 Transaction ID를 사용하여 구분하도록 한다.
		 */
		/*Long maxTrId = asyncCommandLogManager.getMaxTrId(modem.getDeviceSerial());
		String trnxId;
		if (maxTrId != null) {
			trnxId = String.format("%08d", maxTrId.intValue() + 1);
		} else {
			trnxId = "00000001";
		}*/
		
		/*
		 * 비동기 명령 저장 : SMS발송보다 먼저 저장함.
		 */
		//saveAsyncCommandForSORIA(modem.getDeviceSerial(), Long.parseLong(trnxId), commandName, paramMap, TimeUtil.getCurrentTime());
		
		/*
		 * SMS 발송
		 */
		Map<String, Object> resultMap = new HashMap<String,Object>();
		Map<String, Object> condition = new HashMap<String, Object>();
		String mobliePhNum = null;
		String euiId = null;
		
		if(modem.getModemType().equals(ModemType.MMIU)){			
			//MMIU mmiuModem = (MMIU) modem;
			MMIU mmiuModem = mmiuDao.get(modem.getId());
			mobliePhNum = mmiuModem.getPhoneNumber();
			euiId = modem.getDeviceSerial();
		
			condition.put("messageType", messageType);
			condition.put("mobliePhNum", mobliePhNum);
			condition.put("euiId", euiId);
			condition.put("commandCode", commandCode);
			condition.put("commandName", commandName);
			
			List<String> paramListForSMS  = new ArrayList<String>();
			Properties prop = new Properties();
	        prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
	        String serverIp = prop.getProperty("smpp.hes.fep.server") == null ? "" : prop.getProperty("smpp.hes.fep.server").trim();
	        String serverPort = prop.getProperty("soria.modem.tls.port") == null ? "" : prop.getProperty("soria.modem.tls.port").trim();
	        String authPort = prop.getProperty("smpp.auth.port") == null ? "" : prop.getProperty("smpp.auth.port").trim();
	        
	        int loopCountMax = Integer.parseInt(prop.getProperty("smpp.command.response.timeout.loopcount", "6"));
	        int loopSleep = Integer.parseInt(prop.getProperty("smpp.command.response.timeout.loopsleep.sec", "10")) * 1000;
	        
	        log.debug("Send SMS Param Properties. ServerIp="+serverIp+", ServerPort="+serverPort+", AuthPort="+authPort+", LoopCountMax="+loopCountMax+", loopSleep=" + loopSleep);
	        
	        paramListForSMS.add(serverIp);
	        paramListForSMS.add(serverPort);
	        paramListForSMS.add(authPort);
	        
	        // modem이 Fep에 붙었을 때 실행할 command의 param들을 json String으로 넘겨줌
	        String cmdMap = null;
	        ObjectMapper om = new ObjectMapper();
	        if(paramMap != null)
	        	cmdMap = om.writeValueAsString(paramMap);
	      
			log.debug("Send SMS euiId: " + euiId + ", mobliePhNum: " +mobliePhNum+ ", commandName: " + commandName + ", cmdMap " + cmdMap);
			resultMap = sendSms(condition, paramListForSMS, cmdMap);  // Send SMS!
			//String response_messageType = resultMap.get("messageType").toString();
			String response_messageId = resultMap.get("messageId")== null ? "F" : resultMap.get("messageId").toString();
			/*
			 * 결과 처리
			 */
			if (response_messageId.equals("F") || response_messageId.equals("CF")) { // Fail
				log.debug(response_messageId);
				return null;
			} else {
			    //TODO loopcount , sleep time을 프로퍼티로 지정할수 있게.....
				int loopCount = 0;
                Integer lastStatus = null;
                while(loopCount < loopCountMax) {
                    lastStatus = asyncCommandLogManager.getCmdStatus(modem.getDeviceSerial(), commandName);
                    log.debug("#### Response Result Status Check ~!! ==> " + TR_STATE.valueOf(lastStatus).name());
                    if (TR_STATE.Success.getCode() == lastStatus) {
                        break;
                    }
                    loopCount++;
                    Thread.sleep(loopSleep);
                }

				if (TR_STATE.Success.getCode() != lastStatus) {
					log.debug("FAIL : Communication Error but Send SMS Success.  "+ euiId + "  " + commandName);
					return null;
				} else { 
					ObjectMapper mapper = new ObjectMapper();
					List<AsyncCommandResult> asyncResult = asyncCommandLogManager.getCmdResults(modem.getDeviceSerial(), Long.parseLong(response_messageId),commandName); //ASYNC_COMMAND_RESULT에서 결과 값을 가져옴
					if (asyncResult == null || asyncResult.size() <= 0) {
						log.debug("#### Response Fail. Send SMS but fail to execute. EUI_ID=" + euiId + ", CommandName=" + commandName);
						return null;
					} else { // Success
						String resultStr="";
						for(int i = 0 ; i < asyncResult.size() ; i ++){
							resultStr += asyncResult.get(i).getResultValue();
						}
						Map<String, String> map = mapper.readValue(resultStr , new TypeReference<Map<String, String>>(){});
						log.debug("#### Response Success. Result ==> " + map.toString());
						
						return map; // 맴 형식으로 결과 리턴
					}
				}
			}			
		}else{
			log.error("Type Missmatch. this modem is not MMIU Type modem.");
			throw new Exception("Type Missmatch. this modem is not MMIU Type modem.");
		}
		
		
		

	}
	/**
	 * SP-278
	 * @param mdsId == meter.Id
	 * @param thresholdNormal
	 * @param loginId
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/cmdLimitPowerUsage")
	public ModelAndView cmdLimitPowerUsage(@RequestParam(value = "mdsId", required = true) String mdsId,
			@RequestParam(value = "thresholdNormal", required = true) Integer  thresholdNormal,
			@RequestParam(value = "loginId", required = false) String loginId) {

		ModelAndView mav = new ModelAndView("jsonView");
		ResultStatus status = ResultStatus.FAIL;
		Meter meter = null;
		String rtnStr = "Fail";
		
        JSONArray jsonArr = null;
        try{
			// Permission of command operation (8.1.12=Limit Power Usage)
    		if (loginId == null || !commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.12") ){
                Code requiredCode = codeManager.getCodeByCode("8.1.12");
                mav.addObject("rtnStr", "Your role have no permission ["+requiredCode.getName()+"]");
                return mav;
   			}
            // Target check
            meter = meterManager.getMeter(Integer.parseInt(mdsId));
        	if(meter == null && meter.getModem() == null) {
                status = ResultStatus.INVALID_PARAMETER;
                mav.addObject("rtnStr", "Target(modem) is null!");
                return mav;
        	}
        	Modem modem = meter.getModem();

            // Param
	        List<Map<String,String>> paramList = new ArrayList<Map<String,String>>();
			Map<String,String> paramMap = new HashMap<String,String>();
			
	    	String obisCode = this.convertObis(OBIS.LIMITER_INFO.getCode());
			int classId = DLMS_CLASS.LIMITER_CLASS.getClazz();
			int attrId = DLMS_CLASS_ATTR.LIMIT_ATTR04.getAttr();
			String accessRight = "RW";
			String dataType = "double-long-unsigned";
			JSONArray list = new JSONArray();
			JSONObject json = new JSONObject();
			json.put("value", thresholdNormal.toString());
			list.add(json);
			String value = list.toString();

			String cmd = "cmdMeterParamSet";
			byte[] rawdata = null;
			String param = obisCode+"|"+classId+"|"+attrId+"|"+accessRight+"|"+dataType+"|"+value;
			String paramType = "paramSet";
			
			paramMap.put(paramType, param);

            // MBB (SMS)
			if((modem.getModemType() == ModemType.MMIU )&& (modem.getProtocolType() == Protocol.SMS )) {
				Map<String,Object> condition = new HashMap<String,Object>();
				condition.put("modemId", meter.getModemId());
				condition.put("modemType", ModemType.MMIU.toString());
				MMIU mmiu = (MMIU)modemManager.getModemByType(condition);
       		
        		String mobileNo = mmiu.getPhoneNumber();
            	if (mobileNo == null || "".equals(mobileNo)) {
            		log.warn("[" + mdsId + "]Phone number is empty");
            		throw new Exception("Phone number is empty!");
        		}
            	
            	if (!Protocol.SMS.equals(mmiu.getProtocolType())) {
            		log.warn(String.format("[" + mdsId + "] Invalid ProtocolType"));
            		throw new Exception("Invalid ProtocolType!");
    			}
				Long trId = System.currentTimeMillis();
				Map<String, String> result;
				String cmdResult = "";
				result = sendSmsForCmdServer(mmiu, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(),
                        SMSConstants.COMMAND_TYPE.NI.getTypeCode(), cmd, paramMap);
				
				cmdResult="Check the Async_Command_History.";
				if(result != null){
					if ( result.get("RESULT_VALUE") != null ){
						cmdResult = result.get("RESULT_VALUE").toString();
						if ( "Success".equalsIgnoreCase(result.get("RESULT_VALUE"))){
							status = ResultStatus.SUCCESS;
						}
					}
					//check result 0911
					log.debug("### MMIU - CMDRESULT : " + cmdResult);						
				}else{
					log.debug("SMS Fail");
					//cmdResult="Failed to get the resopone. See the Async Command History.";
					cmdResult="Check the Async_Command_History.";
				}						
				rtnStr = cmdResult;	
			} else  {
            // RF(DTLS), ETHERNET(TLS)
				List<Map<String, Object>> result = null;
				result = cmdOperationUtil.cmdMeterParamSet(modem.getDeviceSerial(),
						param, modem.getProtocolType());

				if (result != null) {
					for ( Map<String, Object> entry : result ){
						log.debug("paramType=" + (String) entry.get("paramType") + ",paramValue=" + (String) entry.get("paramType"));
						if ( "RESULT_VALUE".equalsIgnoreCase((String) entry.get("paramType")) || "resultValue".equalsIgnoreCase((String) entry.get("paramType")) ){
							rtnStr = (String) entry.get("paramValue");
							if ( "Success".equalsIgnoreCase(rtnStr)){
								status = ResultStatus.SUCCESS;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e, e);
			rtnStr = "Fail : " + e.getMessage();
		}

        if ( meter != null ){
    		Code operationCode = codeManager.getCodeByCode("8.1.12");
    		if (operationCode != null) {
    			operationLogManager.saveOperationLog(meter.getSupplier(), 
    					meter.getMeterType(), meter.getMdsId(), loginId,
						operationCode, status.getCode(), status.name());
    		}
        }
        mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		return mav;
	}
    /**
     * GroupCommand -> limit power usage command [bypass set]
     * meterMds, loginId, requestValue
     * @return
     */
    @RequestMapping(value = "gadget/device/command/cmdGroupLimitPowerUsage")
    public ModelAndView cmdGroupLimitPowerUsage(@RequestParam(value = "meterMds") String meterMds,
                                           @RequestParam(value = "loginId") String loginId,
                                           @RequestParam(value = "thresholdNormal") Integer requestValue) {
        ResultStatus status = ResultStatus.FAIL;
        ModelAndView mav = new ModelAndView("jsonView");
        String meterId = null;

        // Permission of command operation (8.1.12=Limit Power Usage)
        // --skip

        // Null check of modem Id (Mandatory option)
        if (meterMds == null || "".equals(meterMds)) {
            status = ResultStatus.INVALID_PARAMETER;
            mav.addObject("rtnStr", "Target ID(Meter serial) is null!");
            mav.addObject("status", status.name());
            return mav;
        } else {
            // find the meter by meter serial
            Meter tMeter = meterManager.getMeter(meterMds);
            meterId = tMeter.getId().toString();
        }
        if (meterId == null){
            status = ResultStatus.INVALID_PARAMETER;
            mav.addObject("rtnStr", "Target Meter is null!");
            mav.addObject("status", status.name());
            return mav;
        }

        // Null check of requestValue
		if(requestValue == null){
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Request value is null!");
			mav.addObject("status", status.name());
			return mav;
		}

        // call "cmdLimitPowerUsage" function
        String rtnStr = "";
        try {
            mav = this.cmdLimitPowerUsage(meterId,requestValue,loginId);
        } catch (Exception e) {
            log.error(e, e);
            status = ResultStatus.FAIL;
            rtnStr = e.getMessage();
            mav.addObject("rtnStr", rtnStr);
        }
        return mav;
    }

    // INSERT START SP-681
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdExecNiCommand")
	public ModelAndView cmdExecNiCommand(
			@RequestParam(value = "modemId") String modemId,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "requestType") String requestType,
			@RequestParam(value = "attrID") String attrID,
			@RequestParam(value = "attrParam") String attrParam
			) throws ParseException {

		log.debug("[cmdExecNiCommand] " + " modemId: " +  modemId);
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		// Permission of command operation
//		if (requestType.equals("GET") && !commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.2")){
//			Code requiredCode = codeManager.getCodeByCode("8.2.2");
//			mav.addObject("rtnStr", "Your role have no permission. ["+requiredCode.getName()+"]");
//			return mav;
//		}else if(requestType.equals("SET") && !commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.3")){
//			Code requiredCode = codeManager.getCodeByCode("8.2.3");
//			mav.addObject("rtnStr", "Your role have no permission. ["+requiredCode.getName()+"]");
//			return mav;
//		}

		// Null check of modem Id (Mandatory option)
		if (modemId == null || "".equals(modemId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} 

		// call function
		String rtnStr = "";
		Modem modem = modemManager.getModem(Integer.parseInt(modemId));
		
		// MBB(SMS)
		if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
				Map<String, String> asyncResult = new HashMap<String, String>();
			try{ 
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put("modemId", modemId);
				paramMap.put("requestType", requestType);
				paramMap.put("attrID", attrID);
				paramMap.put("attrParam", attrParam);
				// INSERT START SP-575 MBB
				asyncResult = sendSmsForCmdServer(modem, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), "cmdExecNI", paramMap);
				// INSERT END SP-575 MBB
				if(asyncResult != null){
					status = ResultStatus.SUCCESS;
				}else{
					log.debug("SMS Fail");
					mav.addObject("rtnStr", "Check the results in Async History tab");
					status = ResultStatus.FAIL;
				}
			}catch(Exception e){
				log.debug("SMS Fail");
				mav.addObject("rtnStr", "Check the results in Async History tab");
				status = ResultStatus.FAIL;
			}
			for (String key : asyncResult.keySet()) {
				mav.addObject(key, asyncResult.get(key).toString());
				if(key.contains("NI_RESULT")){
					mav.addObject("modemMode", asyncResult.get(key).toString());
				}
			}
			mav.addObject("status", status.name());
			return mav;
	    // Ethernet
		}else if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.IP  
				|| modem.getProtocolType() == Protocol.GPRS)) ){
			// INSERT START SP-575
			Map<String, Object> result = new HashMap<String, Object>();
			try {
				result = cmdOperationUtil.cmdGeneralNiCommand(modemId, requestType, attrID, attrParam);
				rtnStr = result.get("rtnStr").toString();
				status = ResultStatus.SUCCESS;
			} catch (Exception e) {
				log.error(e, e);
				status = ResultStatus.FAIL;
				rtnStr = e.getMessage();
				mav.addObject("rtnStr", rtnStr);
			}
			for (String key : result.keySet()) {
				mav.addObject(key, result.get(key).toString());
			}
			mav.addObject("status", status.name());
			return mav;
			// INSERT END SP-575
	    // RF
		}else{
			Map<String, Object> result = new HashMap<String, Object>();
			try {
				result = cmdOperationUtil.cmdExecDmdNiCommand(modemId, requestType, attrID, attrParam);
				rtnStr = result.get("rtnStr").toString();
				status = ResultStatus.SUCCESS;
			} catch (Exception e) {
				log.error(e, e);
				status = ResultStatus.FAIL;
				rtnStr = e.getMessage();
				mav.addObject("rtnStr", rtnStr);
			}
			for (String key : result.keySet()) {
				mav.addObject(key, result.get(key).toString());
			}
			mav.addObject("status", status.name());
			return mav;
		}

	}
	 // INSERT END SP-681
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gadget/device/command/cmdExecCODINiCommand")
	public ModelAndView cmdExecCODINiCommand(
			@RequestParam(value = "codiId") String codiId,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "requestType") String requestType,
			@RequestParam(value = "attrID") String attrID,
			@RequestParam(value = "attrParam") String attrParam
			) throws ParseException {

		log.debug("[cmdExecCODINiCommand] " + " codiId: " +  codiId);
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");

		// Null check of modem Id (Mandatory option)
		if (codiId == null || "".equals(codiId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(coordinator id) is null!");
			return mav;
		} 

		// call function
		String rtnStr = "";
		if ("-".equals(codiId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Coordinator does not exist.");
			return mav;
		}
		Modem modem = modemManager.getModem(codiId);
		if (modem == null) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Coordinator does not exist.");
			return mav;
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = cmdOperationUtil.cmdExecDmdNiCommand(codiId, requestType, attrID, attrParam);
			rtnStr = result.get("rtnStr").toString();
			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
			log.error(e, e);
			status = ResultStatus.FAIL;
			rtnStr = e.getMessage();
			mav.addObject("rtnStr", rtnStr);
		}
		for (String key : result.keySet()) {
			mav.addObject(key, result.get(key).toString());
		}
		mav.addObject("status", status.name());
		return mav;

	}

	/**
	 * SP-572
	 * @param modemId
	 * @param loginId
	 * @param count
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "gadget/device/command/cmdSetCloneOnOff")
	public ModelAndView cmdSetCloneOnOff(@RequestParam(value = "modemId") String modemId,
									  @RequestParam(value = "loginId") String loginId,
									  @RequestParam(value = "count") Integer count) {
		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String, Object>();

		// Permission of command operation
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.2.6")) {
			mav.addObject("rtnStr", "Your role have no permission");
			return mav;
		}
		// Null check of modem Id (Mandatory option)
		if (modemId == null || "".equals(modemId)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID(modem id) is null!");
			return mav;
		} else {
			// Modem object will be checked at CommandGW.java
			// Because of another operation is comming from WebService(SOAP)
		}

		// call function
		String rtnStr = "";
		Modem modem = modemManager.getModem(Integer.parseInt(modemId));
		
		// MBB(SMS)
		if( ((modem instanceof MMIU) && (modem.getProtocolType() == Protocol.SMS)) ){
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target Modem is not support CloneOTA");
			return mav;
		}else{
			try {
				result = cmdOperationUtil.setCloneOnOff(modemId, count);
				if (result.containsKey("cmdResult")) {
					rtnStr += result.get("cmdResult") + "\n";
					status = ResultStatus.SUCCESS;
				} else {
					rtnStr += "communication error \n";
				}
				mav.addObject("rtnStr", rtnStr);
			} catch (Exception e) {
				log.error(e, e);
				status = ResultStatus.FAIL;
				rtnStr = e.getMessage();
				mav.addObject("rtnStr", rtnStr);
			}
			for (String key : result.keySet()) {
				mav.addObject(key, result.get(key).toString());
			}
			
			mav.addObject("status", status.name());
			return mav;
		}
	}
	
	@RequestMapping(value = "/gadget/device/command/cmdMcuGetSchedule_")
	public ModelAndView cmdMcuGetSchedule_(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId) {
		
		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");
		Map<String, Object> result = new HashMap<String,Object>();
		// retry interval을 요청할 두번째 커맨드 결과 맵
		Map<String, Object> result2 = new HashMap<String,Object>();
		
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.3.6")){
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			result = cmdOperationUtil.cmdMCUGetSchedule_(mcu.getSysID(), "");			
			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.getMessage();
			log.error(e, e);
		}
		
		//get retry interval 커맨드 
		try {		
			result2 = cmdOperationUtil.cmdMcuGetProperty(mcu.getSysID(), "network.retry.default");			
		} catch (Exception e) {			
			rtnStr = e.getMessage();
			log.error(e, e);
		}
		if(result2.containsKey("cmdResult")){
			// mcuGetProperty에서 받은 결과값에서 필요한 키(network.retry.defautl)에 할당된 값을 mav에 입력
			mav.addObject("retryIntervalTime", result2.get("cmdResult").toString() == null ? "" : result2.get("cmdResult").toString());
			mav.addObject("retry_condition", result2.get("network.retry.default").toString() == null ? "" : result2.get("network.retry.default").toString());
		}

		Code operationCode = codeManager.getCodeByCode("8.3.6");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		// schedule test
		mav.addObject("Schedule", result);

		return mav;
	}		
	
	@RequestMapping(value = "/gadget/device/command/cmdMcuSetSchedule_")
	public ModelAndView cmdMcuSetSchedule_(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "scheduleNameArr", required = false) String[] scheduleNameArr,
			@RequestParam(value = "scheduleConditionArr", required = false) String[] scheduleConditionArr,
			@RequestParam(value = "scheduleTaskArr", required = false) String[] scheduleTaskArr,
			@RequestParam(value = "scheduleSuspendArr", required = false) String[] scheduleSuspendArr,
			@RequestParam(value = "retrycondition", required = false) String retrycondition
			) {
		
		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");
		
		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.3.7")){
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			List<ArrayList<String>> scheduleList = new ArrayList<ArrayList<String>>();
			ArrayList<String> schedule = new ArrayList<String>();
			
			for(int  i=0; i < scheduleNameArr.length; i++){
				schedule = new ArrayList<String>();
				schedule.add(scheduleNameArr[i]);
				if(scheduleSuspendArr[i].equals("true"))
					schedule.add("1");
				else
					schedule.add("0");
				schedule.add(scheduleConditionArr[i]);
				schedule.add(scheduleTaskArr[i]);
				scheduleList.add(schedule);
			}
			
			cmdOperationUtil.cmdMcuSetSchedule_(mcu.getSysID(), scheduleList);
			status = ResultStatus.SUCCESS;
				
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.getMessage();
			log.error(e, e);
		}
		// set Mcu Retry Interval 커맨드 전송 (세팅에 필요한 커맨드가 서로 다름)
		try{						
			String[] cmdKeys = new String[]{"network.retry.default"};
			String[] cmdKeyValues = new String[]{retrycondition};
			if(!retrycondition.isEmpty()){
				cmdOperationUtil.cmdMcuSetProperty(mcu.getSysID(), cmdKeys, cmdKeyValues);
			}else{
				// 결과메시지는 스케줄 커맨드의 것을 따름.
			}			
		} catch (Exception e) {			
			log.error(e, e);
			
		}

		Code operationCode = codeManager.getCodeByCode("8.3.7");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		
		return mav;
	}
	
	@RequestMapping(value = "/gadget/device/command/cmdMcuDeleteSchedule")
	public ModelAndView cmdMcuDeleteSchedule(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "scheduleName", required = false) String scheduleName
			) {
		
		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");
		System.out.println("Schedule Name: " + scheduleName);
		
		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.3.8")){
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			cmdOperationUtil.cmdMcuDeleteSchedule(mcu.getSysID(), scheduleName);
			status = ResultStatus.SUCCESS;
				
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.getMessage();
			log.error(e, e);
		}
		Code operationCode = codeManager.getCodeByCode("8.3.8");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		
		return mav;
	}
	
	@RequestMapping(value = "/gadget/device/command/cmdMcuExecuteSchedule")
	public ModelAndView cmdMcuExecuteSchedule(@RequestParam(value = "target", required = false) String target,
			@RequestParam(value = "loginId", required = false) String loginId,
			@RequestParam(value = "scheduleName", required = false) String scheduleName
			) {
		
		ResultStatus status = ResultStatus.FAIL;

		ModelAndView mav = new ModelAndView("jsonView");
		System.out.println("Schedule Name: " + scheduleName);
		
		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.3.9")){
			mav.addObject("rtnStr", "No permission");
			return mav;
		}
		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			return mav;
		}

		String rtnStr = "";
		MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			cmdOperationUtil.cmdMcuExecuteSchedule(mcu.getSysID(), scheduleName);
			status = ResultStatus.SUCCESS;
				
		} catch (Exception e) {
			status = ResultStatus.FAIL;
			rtnStr = e.getMessage();
			log.error(e, e);
		}
		Code operationCode = codeManager.getCodeByCode("8.3.9");
		if (operationCode != null) {
			operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode,
					status.getCode(), "SUCCESS".equals(status.name()) ? status.name() : rtnStr);
		}
		mav.addObject("status", status.name());
		mav.addObject("rtnStr", rtnStr);
		
		return mav;
	}
	/**
	 * SP-929 Net station Monitoring
	 * @param cmd
	 * @param mdsId
	 * @param parameter
	 * @param modelName
	 * @param loginId
	 * @return
	 */
	@RequestMapping(value = "/gadget/device/command/mbusSlaveIOScheduleGetSet")
	public ModelAndView mbusSlaveIOScheduleGetSet(
			@RequestParam(value = "cmd", required = true) String cmd,
			@RequestParam(value = "mdsId", required = true) String mdsId,
			@RequestParam(value = "parameter", required = true) String parameter,
			@RequestParam(value = "modelName", required = true) String modelName,
			@RequestParam(value = "loginId", required = false) String loginId) {
		ModelAndView mav = new ModelAndView("jsonView");
		List<Map<String, Object>> modemTempList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> modemList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rtnStrList = new ArrayList<Map<String, Object>>();
		ResultStatus status = ResultStatus.FAIL;
		ArrayList<String> execTimeArray = new ArrayList<String>();
		
		// for error returm
		Map<String,Object> errTempMap = new HashMap<String,Object>();
		errTempMap.put("meterId", mdsId);
		errTempMap.put("executionTimes", execTimeArray);
		errTempMap.put("viewMsg","");
		
		Meter meter = null;
		//		List<Map<String, Object>> result = null;

		JSONArray jsonArr = null;
		try{
			if (loginId != null ){
				if ( ("cmdMeterParamSet".equals(cmd) || "cmdMeterParamAct".equals(cmd)) &&
						!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.7")) {
					throw new Exception("No permission");
				}
				else if ("cmdMeterParamGet".equals(cmd) && 
						!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.6")) {
					throw new Exception("No permission");
				}
			}
			if(parameter == null || parameter.isEmpty()) {
				jsonArr = new JSONArray();
			} else {
				jsonArr = JSONArray.fromObject(parameter);
			}

			List<Map<String,String>> paramList = new ArrayList<Map<String,String>>();
			Map<String,String> paramMap = new HashMap<String,String>();
			String OBIS = "";
			if(jsonArr.size() > 0) {
				Object[] tempJson = jsonArr.toArray();
				for (int i = 0; i < tempJson.length; i++) {
					Map<String,Object> jsonMap = (Map<String,Object>)tempJson[i];
					String obisCode = jsonMap.get("OBISCODE").toString();
					String classId = jsonMap.get("CLASSID").toString();
					String attributeNo = jsonMap.get("ATTRIBUTENO") == null ? "" : jsonMap.get("ATTRIBUTENO").toString();
					String dataType = jsonMap.get("DATATYPE") == null ? "" : jsonMap.get("DATATYPE").toString();
					String accessRight = jsonMap.get("ACCESSRIGHT") == null ? "" : jsonMap.get("ACCESSRIGHT").toString();
					String value = jsonMap.get("VALUE") == null ? "" : jsonMap.get("VALUE").toString();
					JSONArray jsonValueArr = null;
					if (value == null || value.isEmpty() || "null".equals(value)) {
						jsonValueArr = new JSONArray();
					} else {
						jsonValueArr = JSONArray.fromObject(value);
					}
					if ( cmd.equals("cmdMeterParamSet")){
						JSONArray  newArr = new JSONArray();
						for(int j = 0; j < jsonValueArr.size(); j++){
							JSONArray tmpArray = JSONArray.fromObject(jsonValueArr.get(j));
							JSONArray elementArray = new JSONArray();
							String day  = tmpArray.getString(0).substring(0,8);
							String time = tmpArray.getString(0).substring(8,14);

							String ww = "FF";
							try {
								if ( !day.contains("F")){
									Calendar tCal = DateTimeUtil.getCalendar(day + "000000");
									ww = String.format("%02d",tCal.get(Calendar.DAY_OF_WEEK));
								}
							}catch (Exception e){
								e.printStackTrace();
							}
							elementArray.add(day + ww + time+"00");
							newArr.add(elementArray);
						}
						value = newArr.toString();
					}

					// Need to exchange value!!!!
					String paramType = "";
					OBIS = obisCode;
					if(i==0) {
						paramType = cmd.replace("cmdMeterP", "p");
					}else {
						paramType = cmd.replace("cmdMeterP", "p")+i;
					}
					paramMap.put(paramType, obisCode+"|"+classId+"|"+attributeNo+"|"+accessRight+"|"+dataType+"|"+value);
					paramList.add(paramMap);
				}
			}
			try{
				meter = meterManager.getMeter(mdsId);
				if(meter != null && meter.getModem() != null) {
					if(meter.getModel() != null && !meter.getModel().getName().equals(modelName)) {
						Map<String,Object> tempMap = new HashMap<String,Object>();
						tempMap.put("meterId", mdsId);
						tempMap.put("rtnStr", "FAIL : Model is Not " + modelName);
						rtnStrList.add(tempMap);
					} else {
						Modem modem = meter.getModem();
						if(modem.getModemType() == ModemType.MMIU && 
								(modem.getProtocolType() == Protocol.SMS ||
								 modem.getProtocolType() == Protocol.GPRS ||
								 modem.getProtocolType() == Protocol.IP)) {
							Map<String,Object> condition = new HashMap<String,Object>();
							condition.put("modemId", meter.getModemId());
							condition.put("modemType", ModemType.MMIU.toString());
							MMIU mmiu = (MMIU)modemManager.getModemByType(condition);

							Map<String,Object> tempMap = new HashMap<String,Object>();
							tempMap.put("meterId", mdsId);
							tempMap.put("modemType", meter.getModem().getModemType().name());
							tempMap.put("protocolType", meter.getModem().getProtocolType());
							tempMap.put("modem", mmiu);
							modemTempList.add(tempMap);
						} else if(modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP) {
							Map<String,Object> tempMap = new HashMap<String,Object>();
							tempMap.put("meterId", mdsId);
							tempMap.put("modemType", meter.getModem().getModemType());
							tempMap.put("protocolType", meter.getModem().getProtocolType());
							tempMap.put("modem", meter.getModem());
							modemTempList.add(tempMap);
						} else {
							Map<String,Object> tempMap = new HashMap<String,Object>();
							tempMap.put("meterId", mdsId);
							tempMap.put("rtnStr", "FAIL : Target ID null!");
							tempMap.put("executionTimes", execTimeArray);
							rtnStrList.add(tempMap);
						}
					}
				} else {
					Map<String,Object> tempMap = new HashMap<String,Object>();
					errTempMap.put("rtnStr", "FAIL : Target ID null!");
					rtnStrList.add(errTempMap);
				}
			}catch(Exception e) {
				log.warn(e,e);				
				errTempMap.put("rtnStr", "FAIL :" + e.getMessage());
				rtnStrList.add(errTempMap);
			}

			for (int j = 0; j < modemTempList.size(); j++) {
				boolean async = false;
				Map<String,Object> map = null;
				try{
					map = modemTempList.get(j);

					if(map.get("modemType") == ModemType.MMIU.name() && map.get("protocolType") == Protocol.SMS) {
						
						MMIU mmiu = (MMIU)map.get("modem");

						String mobileNo = mmiu.getPhoneNumber();
						if (mobileNo == null || "".equals(mobileNo)) {
							log.warn(String.format("[" + cmd + "] Phone number is empty"));
							errTempMap.put("rtnStr", "FAIL : Phone number is empty!");
							rtnStrList.add(errTempMap);
							continue;
						}

						if (!Protocol.SMS.equals(mmiu.getProtocolType())) {
							log.warn(String.format("[" + cmd + "] Invalid ProtocolType"));
							errTempMap.put("rtnStr", "FAIL : Invalid ProtocolType!");
							rtnStrList.add(errTempMap);
							continue;
						}

						Long trId = System.currentTimeMillis();
						Map<String, String> result;
						String cmdResult = "";
						
						modemList.add(map);

						result = sendSmsForCmdServer(mmiu, SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), cmd, paramMap); 

						if(result != null){
							cmdResult =  "DONE! ";
							for(String key : result.keySet()){
							    if (key.indexOf("Execution Time") > 0){
							    	execTimeArray.add(result.get(key));
							    }
							}
							Collections.sort(execTimeArray);
						}else{
							log.debug("SMS Fail");
							cmdResult="Check the Async_Command_History.";
						}                       

						Map<String, Object> tempMap = new HashMap<String, Object>();
						tempMap.put("executionTimes", execTimeArray);
						tempMap.put("meterId", map.get("meterId"));
						tempMap.put("rtnStr", cmdResult);
						tempMap.put("viewMsg", result == null ? "" : result);
						rtnStrList.add(tempMap);
					} else {

						List<Map<String, Object>> result = null;
						Modem modem = (Modem) map.get("modem");
						if ("cmdMeterParamGet".equals(cmd)) {
							result = cmdOperationUtil.cmdMeterParamGet(modem.getDeviceSerial(),
									paramList.get(0).get("paramGet"), modem.getProtocolType());
						} else if ("cmdMeterParamSet".equals(cmd))  {
							result = cmdOperationUtil.cmdMeterParamSet(modem.getDeviceSerial(),
									paramList.get(0).get("paramSet"), modem.getProtocolType());
						}

						Map<String, Object> tempMap = new HashMap<String, Object>();

						if (result != null) {
							int size = 0;
							for (Map<String,Object> remap : result){
								String type = (String)remap.get("paramType");
								Object value = remap.get("paramValue");
								if ( type.indexOf("Execution Time") > 0){
									execTimeArray.add((String)value);
								}
							}
							Collections.sort(execTimeArray);
							tempMap.put("executionTimes", execTimeArray);
							tempMap.put("meterId", map.get("meterId"));
							tempMap.put("rtnStr", "DONE! ");
							tempMap.put("viewMsg", result);
							rtnStrList.add(tempMap);
							status = ResultStatus.SUCCESS;
						}
					}
				} catch (Exception e) {
					log.error(e, e);
					errTempMap.put("error",  e.getMessage());
					if ( async )
						errTempMap.put("rtnStr", "Check the Async_Command_History.");
					else 
						errTempMap.put("rtnStr",  "FAIL :" + e.getMessage());
					rtnStrList.add(errTempMap);
					continue;
				}
			}
		} catch (Exception e) {
			log.error(e, e);
			errTempMap.put("rtnStr", "FAIL : " + e.getMessage());
			rtnStrList.add(errTempMap);
		}

		if ( meter != null ){
			Code operationCode = "cmdMeterParamSet".equals(cmd) ? 
					codeManager.getCodeByCode("8.1.7") : codeManager.getCodeByCode("8.1.6");
					if (operationCode != null) {
						operationLogManager.saveOperationLog(meter.getSupplier(), 
								meter.getMeterType(), meter.getMdsId(), loginId,
								operationCode, status.getCode(), status.name());
					}

		}
		mav.addObject("rtnStrList", rtnStrList);

		return mav;
	}
  /////
	List<Map<String,Object>> dummyResult (){
			Map<String,Object> tempMap = new HashMap<String,Object>();
			List<Map<String,Object>>  tmpList  = new ArrayList<Map<String,Object>>();
			for ( int i = 0; i < 7; i++ ){
				HashMap<String,Object> tmpHash = new HashMap<String,Object>();
				if ( i == 0 ){
					tmpHash.put("paramType","RESULT_STEP");
					tmpHash.put("paramValue","GET_PROFILE_BUFFER");
				}
				else if ( i == 1 ){
					tmpHash.put("paramType","RESULT_VALUE");
					tmpHash.put("paramValue","Success");
				}
				else if ( i == 2){
					tmpHash.put("paramType","DATA_SIZE");
					tmpHash.put("paramValue",4);		
				}
				else if ( i == 3){
					tmpHash.put("paramType","[1] Execution Time");
					tmpHash.put("paramValue","FFFF-FF-FF FF:00:00");	
					                       // YYYYMMDDWWhhmmssHS	
				}
				else if ( i == 4){
					tmpHash.put("paramType","[2] Execution Time");
					tmpHash.put("paramValue","FFFF-FF-FF FF:15:00");		
				}
				else if ( i == 5){
					tmpHash.put("paramType","[3] Execution Time");
					tmpHash.put("paramValue","FFFF-FF-FF FF:30:00");		
				}
				else if ( i == 6){
					tmpHash.put("paramType","[4] Execution Time");
					tmpHash.put("paramValue","FFFF-FF-FF FF:45:00");		
				}
				tmpList.add(tmpHash);
			}
			return tmpList;
		}
	  
    @RequestMapping(value="/gadget/device/command/cmdGetTariffForPKS.do")
    public ModelAndView cmdGetTariffForPKS(
    		@RequestParam(value = "target", required = false) String target, 
			@RequestParam(value = "mcuId", required = false) String mcuId, 
			@RequestParam(value = "cmd", required = false) String cmd, 
			@RequestParam(value = "loginId", required = false) String loginId) {
    	
    	ResultStatus status = ResultStatus.FAIL;
		String rtnStr = "";
		String detail = "";
		ModelAndView mav = new ModelAndView("jsonView");
		
		
		if (!commandAuthCheck(loginId, CommandType.DeviceRead, "8.1.10")) {// Get Tariff
			// opcode
			mav.addObject("rtnStr", "No permission");
			mav.addObject("detail", "");
			return mav;
		}

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			mav.addObject("detail", "");
			return mav;
		}
		
		//Code for Test
		mav.addObject("rtnStr", "New feature:'Tariff Get/Set' will be added.");
		mav.addObject("detail", "");
		return mav;
		
		/*
		Meter meter = meterManager.getMeter(Integer.parseInt(target));
		try {
			Map<String, Object> resultMap = cmdOperationUtil.cmdGetTariffForPKS(mcuId, meter.getMdsId());
			if(resultMap.containsKey("status") && resultMap.get("status").equals("success")) {
				status = ResultStatus.SUCCESS;
				detail = (String) resultMap.get("detail");
			}else if(resultMap.containsKey("status") && resultMap.get("status").equals("fail")){
				status = ResultStatus.FAIL;
			}
		} catch (Exception e) {
			log.error("cmdGetTariffForPKS error - " + e.getMessage(), e);
			rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
		}
		
		mav.addObject("status", status.name());
		mav.addObject("meterId", meter.getMdsId());
		mav.addObject("rtnStr", status.name());
		mav.addObject("detail", detail);
		
		
		return mav;
		*/
	}
    
    @RequestMapping(value="/gadget/device/command/cmdSetTariffForPKS.do")
    public ModelAndView cmdSetTariffForPKS(
    		@RequestParam(value = "target", required = false) String target, 
			@RequestParam(value = "mcuId", required = false) String mcuId, 
			@RequestParam(value = "cmd", required = false) String cmd, 
			@RequestParam(value = "tariffTypeCode", required = false) String tariffTypeCode, 
			@RequestParam(value = "loginId", required = false) String loginId) {
    	
    	ResultStatus status = ResultStatus.FAIL;
		String rtnStr = "";
		String detail = "";
    	
		ModelAndView mav = new ModelAndView("jsonView");
		
		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.11")) {// Set Tariff
			// opcode
			mav.addObject("rtnStr", "No permission");
			mav.addObject("detail", "");
			return mav;
		}

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			mav.addObject("detail", "");
			return mav;
		}
		//Code for Test
				mav.addObject("rtnStr", "New feature:'Tariff Get/Set' will be added.");
				mav.addObject("detail", "");
				return mav;
				
		/*

		Meter meter = meterManager.getMeter(Integer.parseInt(target));
		try {
			Map<String, Object> resultMap = cmdOperationUtil.cmdSetTariffForPKS(mcuId, meter.getMdsId(), tariffTypeCode);
			if(resultMap.containsKey("status") && resultMap.get("status").equals("success")) {
				status = ResultStatus.SUCCESS;
				rtnStr = (String) resultMap.get("rtnStr");
				detail = (String) resultMap.get("detail");
			}else if(resultMap.containsKey("status") && resultMap.get("status").equals("fail")){
				status = ResultStatus.FAIL;
				rtnStr = (String) resultMap.get("rtnStr");
			}
		} catch (Exception e) {
			log.error("cmdSetTariffForPKS error - " + e.getMessage(), e);
			rtnStr = e.getMessage();
			status = ResultStatus.FAIL;
		}
		
		mav.addObject("status", status.name());
		mav.addObject("meterId", meter.getMdsId());
		mav.addObject("rtnStr", status.name());
		mav.addObject("detail", detail);
		return mav;
		*/
	}
    
	// OPF-846 
	@SuppressWarnings("static-access")
	@RequestMapping(value = "/gadget/device/command/cmdOnDemand_pks")
	public ModelAndView cmdOnDemand_PKS(@RequestParam(value = "target", required = false) String target, 
	@RequestParam(value = "loginId", required = false) String loginId, 
	@RequestParam(value = "fromDate", required = false) String fromDate, 
	@RequestParam(value = "toDate", required = false) String toDate, 
	@RequestParam(value = "type", required = false) String type) {

		ResultStatus status = ResultStatus.FAIL;
		ModelAndView mav = new ModelAndView("jsonView");

		String checkCode = "8.1.1";

		if (!commandAuthCheck(loginId, CommandType.DeviceRead, checkCode)) {
			mav.addObject("rtnStr", "No permission");
			mav.addObject("detail", "");
			return mav;
		}

		if (target == null || "".equals(target)) {
			status = ResultStatus.INVALID_PARAMETER;
			mav.addObject("rtnStr", "Target ID null!");
			mav.addObject("detail", "");
			return mav;
		}

		Map<?, ?> result = null;
		String rtnStr = "";
		String detail = "";
		Meter meter = meterManager.getMeter(Integer.parseInt(target));
		Modem modem = meter.getModem();
		Supplier supplier = meter.getSupplier();
		if (supplier == null) {
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}

		try {
			String nOption = "F";
			if (modem != null) {
				if (fromDate != null && toDate != null) {
					result = cmdOperationUtil.cmdGetMeteringData(meter, 0, "admin", nOption, fromDate, toDate, null);
				} else {
					result = cmdOperationUtil.cmdGetMeteringData(meter, 0, "admin", nOption, fromDate, toDate, null);
				}
			}else{
				log.info("no modem object");
                detail = "No Modem Object";
			}

			rtnStr = (String) result.get("result");
			detail = (String) result.get("detail");
			if(rtnStr.equals("Success")) status = ResultStatus.SUCCESS; // SP-792
			else status = ResultStatus.FAIL;

			Code operationCode = codeManager.getCodeByCode("8.1.1");
			if (operationCode != null) {
				operationLogManager.saveOperationLog(supplier, meter.getMeterType(), meter.getMdsId(), loginId,
						operationCode, status.getCode(), rtnStr);
			}
		} catch (Exception e) {
			rtnStr = e.getMessage();
		}
		mav.addObject("status", status.name());
		mav.addObject("meterId", meter.getMdsId());
		mav.addObject("rtnStr", rtnStr);
		mav.addObject("detail", detail);
		return mav;
	}
}