package com.aimir.mars.integration.metercontrol.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.integration.WSMeterConfigLogDao;
import com.aimir.dao.integration.WSMeterConfigResultDao;
import com.aimir.fep.protocol.smsp.SMSConstants;
import com.aimir.mars.integration.metercontrol.server.data.MeterControlMessage;
import com.aimir.mars.integration.metercontrol.util.CmdUtil;
import com.aimir.mars.integration.metercontrol.util.MeterControlConstants.ErrorCode;
import com.aimir.mars.integration.metercontrol.util.MeterControlConstants.ObisInfo;
import com.aimir.mars.integration.metercontrol.util.MeterControlProperty;
import com.aimir.model.integration.WSMeterConfigLog;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.TimeUtil;

@Service
//@Transactional
public class MeterConfigureService extends AbstractService {
	@Resource(name="transactionManager")
	HibernateTransactionManager txManager;

	private static Log log = LogFactory
			.getLog(MeterConfigureService.class);

	final static DecimalFormat dformat = new DecimalFormat("#0.000000");


	@Autowired
	private CmdUtil	cmdUtil;

	@Autowired
	private WSMeterConfigLogDao wsMeterconfigLogDao;

	@Autowired
	private WSMeterConfigResultDao wsMeterconfigResultDao;

	@Autowired
	private CmdOperationUtil cmdOperationUtil;

	private static Integer  _smsTimeOut = Integer.valueOf(MeterControlProperty.getProperty("meterconfig.sms.timeout", "120"));

	public enum ResultCheck {
		GetBreakerMode("GET_VALUE", "Success", "value"),
		SetBreakerMode("SET_VALUE", "Success", ""),
		GetBreakerOutputState("GET_DISCONNECT_CONTROL","Success", "value"),
		GetBreakerControlState("GET_VALUE","Success", "value"),
		GetHANConfigObject("GET_VALUE","Success","value"),
		SetHANConfigObject("SET_VALUE","Success",""),
		GetBillingCycle("GET_PROFILE_PERIOD", "Success","period(sec)"),
		SetBillingCycle("SET_PROFILE_PERIOD","Success",""),
		GetPowerQualityCycle("GET_PROFILE_PERIOD", "Success","period(sec)"),
		SetPowerQualityCycle("SET_PROFILE_PERIOD","Success","");


		private String resultStepParamValue; //{"paramType":"RESULT_STEP","paramValue":"GET_PROFILE_PERIOD"}
		private String resultValueParamValue; // {"paramType":"RESULT_VALUE","paramValue":"Success"}
		private String resultParamType;  // result Value ex {"paramType":"value","paramValue":"1136"}

		ResultCheck(String resultStepParamValue,String resultValueParamValue,String resultParamType)
		{
			this.resultParamType = resultParamType;
			this.resultStepParamValue = resultStepParamValue;
			this.resultValueParamValue = resultValueParamValue;
		}

		public String getResultStepParamValue() {
			return resultStepParamValue;
		}

		public void setResultStepParamValue(String resultStepParamValue) {
			this.resultStepParamValue = resultStepParamValue;
		}

		public String getResultValueParamValue() {
			return resultValueParamValue;
		}

		public void setResultValueParamValue(String resultValueParamValue) {
			this.resultValueParamValue = resultValueParamValue;
		}

		public String getResultParamType() {
			return resultParamType;
		}

		public void setResultParamType(String resultParamType) {
			this.resultParamType = resultParamType;
		}
		public static ResultCheck getResultCheck(String command){
			for(ResultCheck o : ResultCheck.values()){
				if(o.name().equals(command))
					return o;
			}
			return null;
		}
	}
	@Override
	public void execute(MeterControlMessage message) throws Exception {
		String trId = message.getTrId();
		String meterId = message.getMeterId();
		String command = message.getCommand();
		List<Map<String, String>> result = new ArrayList<Map<String, String>>() ;
		Map<String,Object> map = getMapFromMCLog(meterId, trId);
		try {
			if ( (boolean)map.get("AsyncCommand")) {
				result= (List<Map<String, String>>) cmdUtil.sendSmsForCmdServer(
						(String)map.get("deviceSerial"),  SMSConstants.MESSAGE_TYPE.REQ_NON_ACK.getTypeCode(), SMSConstants.COMMAND_TYPE.NI.getTypeCode(), 
						(String)map.get("cmd"), (Map<String,String>)map.get("paramMap"), _smsTimeOut*1000); 
			}
			else {
				result = dlmsGetSet((String) map.get("cmd"),  
						(String)map.get("deviceSerial"),
						(Protocol)map.get("protocolType"),
						(Map<String,String>)map.get("paramMap") ) ;
			}
		}catch (Exception e) {
			log.error(e,e);

		}
		saveResult((String)map.get("wsCmdName"), meterId, trId, command, result);

	}


	private Map<String,Object>  getMapFromMCLog(String meterId,  String trId) throws Exception
	{
		HashMap<String,Object> retMap = new HashMap<String, Object>();
		TransactionStatus txstatus = null;
		try {
			txstatus = txManager.getTransaction(null);
			WSMeterConfigLog mclog = wsMeterconfigLogDao.getByAsyncTrId( meterId, trId, null);
			Map<String,Object> paramMap = new HashMap<String,Object>();

			String command = mclog.getCommand();
			String paramType = "";
			String cmd = "";
			if ( command.startsWith("Get")) {
				paramType = "paramGet";
				cmd = "cmdMeterParamGet";
			}
			else if (command.startsWith("Set")){
				paramType = "paramSet";
				cmd = "cmdMeterParamSet";
			}
			String obisCode = mclog.getObisCode();
			String classId = mclog.getClassId();
			String attributeNo = mclog.getAttributeNo();
			String dataType = ObisInfo.getByCommand(mclog.getCommand()).getDataType();
			String accessRight = "RW";
			String value = mclog.getParameter();
			if ( value == null )
				value = "";
			paramMap.put(paramType, obisCode+"|"+classId+"|"+attributeNo+"|"+accessRight+"|"+dataType+"|"+value);
			retMap.put("paramMap", paramMap);
			retMap.put("wsCmdName",mclog.getCommand());

			retMap.put("cmd", cmd);
			retMap.put("paramType", paramType);
			retMap.put("meterId", mclog.getDeviceId());
			retMap.put("deviceSerial", mclog.getModemId());
			retMap.put("protocolType", mclog.getProtocolType());
			retMap.put("modemType", mclog.getModemType());
			if ( mclog.getModemType() ==  ModemType.MMIU && mclog.getProtocolType() == Protocol.SMS ) {
				retMap.put("AsyncCommand", true);
			}
			else {
				retMap.put("AsyncCommand", false);
			}
			String currentTime = TimeUtil.getCurrentTime();
			mclog.setUpdateDate(currentTime);
			mclog.setState(TR_STATE.Running.getCode());
			txManager.commit(txstatus);
		}catch  (Exception e) {
			log.error("Error" + e, e);
			if (txstatus != null&& !txstatus.isCompleted())
				txManager.rollback(txstatus);
			throw new Exception(ErrorCode.SystemError.name());
		}
		return retMap;
	}

	public List<Map<String, String>> dlmsGetSet(String cmd, 
			String deviceSerial, Protocol protocolType,  Map<String,String> paramMap ) 
	{	
		log.info( "deviceSerial: " + deviceSerial + " cmd: " + cmd + " protocolType: " + protocolType );

		List<Map<String, Object>> result = null;
		List<Map<String, String>> ret = new ArrayList<Map<String,String>>();
		try {
			if ("cmdMeterParamGet".equals(cmd)) {
				result = cmdOperationUtil.cmdMeterParamGet(deviceSerial,
						paramMap.get("paramGet"), protocolType );
			} else if ("cmdMeterParamSet".equals(cmd))  {
				result = cmdOperationUtil.cmdMeterParamSet(deviceSerial,
						paramMap.get("paramSet"), protocolType);
			} else if ("cmdMeterParamAct".equals(cmd))  {
				result = cmdOperationUtil.cmdMeterParamAct(deviceSerial,
						paramMap.get("paramAct"),protocolType);
			}
		} catch (Exception e) {
			log.error(e, e);
		}
		if ( result != null && result.size() > 0) {
			for(Map<String,Object> entry : result) {
				HashMap<String, String> map = new HashMap<String,String>();
				for ( String key : entry.keySet()) {
					map.put(key,  String.valueOf(entry.get(key)));
				}
				ret.add(map);
			}
		}
		return ret;
	}

	void saveResult(String wsCmdName, String meterId, String trId, String command, List<Map<String,String>> dlmsResultList)
	{
		boolean resultStep = false;
		boolean resultValue = false;
		String saveResultValue = null;
		TR_STATE state = TR_STATE.Terminate;
		ErrorCode errCode  = ErrorCode.MeterNotReached;

		ResultCheck rc = ResultCheck.getResultCheck(wsCmdName);
		if ( rc == null ) {
			log.error("Invalid command Name:" + wsCmdName);
			saveWSMeterConfigResult(trId, meterId, command, state, errCode , saveResultValue);
			return;
		}
		
		// dlmsResultList is null or empty, communication error(MeterNotReached)
		if ( dlmsResultList == null || dlmsResultList.size() == 0 ) {
			saveWSMeterConfigResult(trId, meterId, command, state, errCode , saveResultValue);
			return;
		}
		
		// check result and get resultValue
		for(Map<String,String> entry : dlmsResultList) {
			String paramType = entry.get("paramType");
			String paramValue = entry.get("paramValue");
			
			if ( paramType == null || paramValue == null )
				continue;
			
			if ( paramType.equals("RESULT_VALUE") ) {
				if ( rc.getResultValueParamValue().equals(paramValue)){
					resultValue = true;
				}
			}
			else if (paramType.equals("RESULT_STEP") ) {
				if ( rc.getResultStepParamValue().equals(paramValue)) {
					resultStep = true;
				}
			}
			else if (paramType.equals(rc.getResultParamType())){
				String cmdResultValue = paramValue;
				if ( wsCmdName.equals(ResultCheck.GetBreakerOutputState.name()) ) {
					// paramValue = Connected (true) or Disconnected(false)
					if ( paramValue.contains("true")) {
						cmdResultValue = "true"; 
					}
					else if ( paramValue.contains("false")) {
						cmdResultValue = "false";
					}
				}
				
				// put result map and add array
				Map<String,String> _result = new HashMap<String,String>();
				_result.put("value", cmdResultValue);
				ArrayList<Map<String,String>> _map = new ArrayList<Map<String,String>>();
				_map.add(_result);

				ObjectMapper om = new ObjectMapper();

				try {
					saveResultValue = om.writeValueAsString(_map);
				} catch (Exception e) {
					log.error("Exception comes from appendBypassParam : "+ e, e);
				} 
			}
		}

		// if  SetCommand , CommandResultValue is not required
		if ( wsCmdName.startsWith("Set") || rc.getResultParamType() == null || "".equals(rc.getResultParamType())){ 
			if ( resultValue && resultStep ) {
				state = TR_STATE.Success;
				errCode = ErrorCode.Success;
			}
			else {
				state = TR_STATE.Terminate;
				errCode = ErrorCode.MeterNotReached;
			}
		}
		else { // GetCommand
			if ( resultValue && resultStep && saveResultValue != null) {
				state = TR_STATE.Success;
				errCode = ErrorCode.Success;
			}
			else {
				state = TR_STATE.Terminate;
				errCode = ErrorCode.MeterNotReached;
			}
		}
		saveWSMeterConfigResult(trId, meterId, command, state, errCode , saveResultValue);
	}

	private void saveWSMeterConfigResult(String trId, String meterId, String command, TR_STATE state, ErrorCode errCode, String resultValue) {
		log.debug("save WS_METERCONFIG_LOG & WS_METERCONFIG_RESULT: meterId["+ meterId + "] trId[" + trId + "] command [ " + command + "] state[" + state + "] errCode[" + errCode + "] resultValue[" + resultValue + "]" );
		TransactionStatus txstatus = null;
		try {
			DefaultTransactionDefinition  transdef = new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
			transdef.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
			txstatus = txManager.getTransaction(transdef);

			WSMeterConfigLog wsLog = wsMeterconfigLogDao.getByAsyncTrId(meterId, trId, command);
			String currentTime = TimeUtil.getCurrentTime();

			if ( wsLog.getState() != TR_STATE.Terminate.getCode() ) {
				wsLog.setState(state.getCode());
				wsLog.setUpdateDate(currentTime);
				wsLog.setErrorCode(errCode.getCode());
			}


			if ( resultValue != null ) {
				wsMeterconfigResultDao.addByAsyncTrId(meterId, trId, resultValue, command);
			}

			txManager.commit(txstatus);
		}catch  (Exception e) {
			log.error("Error" + e, e);
			if (txstatus != null&& !txstatus.isCompleted())
				txManager.rollback(txstatus);
		}

	}
}
