package com.aimir.mars.integration.metercontrol.server;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.integration.WSMeterConfigOBISDao;
import com.aimir.mars.integration.metercontrol.queue.MeterControlQueueHandler;
import com.aimir.mars.integration.metercontrol.server.data.MeterConfigureMessage;
import com.aimir.mars.integration.metercontrol.util.CmdUtil;
import com.aimir.mars.integration.metercontrol.util.MeterControlConstants.ErrorCode;
import com.aimir.mars.integration.metercontrol.util.MeterControlConstants.ObisInfo;
import com.aimir.mars.integration.metercontrol.util.MeterControlProperty;
//import com.aimir.mars.util.MarsProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.integration.WSMeterConfigOBIS;
import com.aimir.service.device.MeterManager;
import com.aimir.util.TimeUtil;


@WebService(serviceName = "MeterConfigure")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
@Service(value = "meterConfigure")
@HandlerChain(file="/config/handlers.xml")
public class MeterConfigure {
	protected static Log log = LogFactory.getLog(MeterConfigure.class);
	@Autowired
	private MeterControlQueueHandler handler;

	@Resource(name="transactionManager")
	HibernateTransactionManager txManager;

	@Autowired
	private MeterManager meterManager;

	@Resource
	private WebServiceContext wsContext;

	@Autowired
	private CmdUtil cmdUtil;

	@Autowired
	private WSMeterConfigOBISDao wsMeterConfigOBISDao;



	private static Integer _defaultSyncTimeOut = Integer.valueOf(MeterControlProperty.getProperty("meterconfig.sync.timeout", "60"));


	@WebMethod(operationName = "GetAsyncResponse")
	public @WebResult(name = "GetAsyncResponseResult") McResponse getAsyncResponse(
			@WebParam(name = "meterID") java.lang.String meterID,
			@WebParam(name = "trID") java.lang.String trID,
			@WebParam(name = "command") java.lang.String command
			)
					throws Exception {

		McResponse res = new McResponse();
		ErrorCode err = ErrorCode.Success;

		try {
			if ( meterID == null || meterID.length() == 0 ) {
				log.error("meterID is null or \"\" ");
				throw new Exception();
			}
			if ( trID == null || trID.length() < 16 ) {
				log.error("trID is null or \"\" ");
				throw new Exception();
			}
			if ( command == null || command.length() == 0 ) {
				log.error("command is null or \"\" ");
				throw new Exception();
			}
		}
		catch (Exception e) {
			err = ErrorCode.InvalidParameter;
		}
		if ( err ==  ErrorCode.Success ) {
			res = cmdUtil.getWSMeterConfigResult( meterID, trID, command);
		}
		else {
			res.setErrorCode(err.getCode());
			res.setErrorString(err.getMessage());
		}
		return res;
	}

	@WebMethod(operationName = "GetBreakerMode")
	public @WebResult(name = "GetBreakerModeResult") McResponse getBreakerMode(
			@WebParam(name = "meterID") java.lang.String meterID,
			@WebParam(name = "async") java.lang.Boolean async,
			@WebParam(name = "timeout") java.lang.Integer timeout
			)
					throws Exception {

		String commandName = "GetBreakerMode";
		log.info(commandName + " Start: meterID=" + meterID + ",async=" + async + "timeout=" + timeout );
		McResponse res = new McResponse();

		try {
			String requestTime = TimeUtil.getCurrentTime();
			ObisInfo obis = ObisInfo.getByCommand(commandName);
			String valueStr = "";

			// Check Parameter
			res = checkParameter(requestTime,meterID, commandName , obis,  null , async, timeout);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}

			res = checkOBISPermission( requestTime, meterID, commandName, null);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			// Check User, Save WS_METERCONFIG_LOG and PutQueue
			res = SaveAndPutQueue(requestTime, meterID, valueStr, async,  commandName, obis , timeout );
		} catch (Exception e) {
			log.error(e,e);
			ErrorCode err = ErrorCode.SystemError;
			res.setErrorCode(err.getCode());
			res.setErrorString(err.getMessage());
		}
		return res;
	}

	@WebMethod(operationName = "GetBreakerOutputState")
	public @WebResult(name = "GetBreakerOutputStateResult")  McResponse  getBreakerOutputState(
			@WebParam(name = "meterID") java.lang.String meterID,
			@WebParam(name = "async") java.lang.Boolean async,
			@WebParam(name = "timeout") java.lang.Integer timeout
			)
					throws Exception {

		String commandName = "GetBreakerOutputState";
		log.info(commandName + " Start: meterID=" + meterID + ",async=" + async + "timeout=" + timeout );
		McResponse res = new McResponse();
		try {
			String requestTime = TimeUtil.getCurrentTime();
			ObisInfo obis = ObisInfo.getByCommand(commandName);
			String valueStr = "";

			// Check Parameter
			res = checkParameter(requestTime,meterID, commandName , obis, null , async, timeout);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			res = checkOBISPermission( requestTime, meterID, commandName, null);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			// Check User, Save WS_METERCONFIG_LOG and PutQueue
			res = SaveAndPutQueue(requestTime,meterID, valueStr, async,  commandName, obis , timeout);
		} catch (Exception e) {
			log.error(e,e);
			ErrorCode err = ErrorCode.SystemError;
			res.setErrorCode(err.getCode());
			res.setErrorString(err.getMessage());
		}
		return res;
	}

	@WebMethod(operationName = "GetBreakerControlState")
	public @WebResult(name = "GetBreakerControlStateResult")  McResponse  getBreakerControlState(
			@WebParam(name = "meterID") java.lang.String meterID,
			@WebParam(name = "async") java.lang.Boolean async,
			@WebParam(name = "timeout") java.lang.Integer timeout
			)
					throws Exception {

		String commandName = "GetBreakerControlState";
		log.info(commandName + " Start: meterID=" + meterID + ",async=" + async + "timeout=" + timeout );
		

		String valueStr = "";
		McResponse res = new McResponse();
		try {
			String requestTime = TimeUtil.getCurrentTime();
			ObisInfo obis = ObisInfo.getByCommand(commandName);

			// Check Parameter
			res = checkParameter(requestTime,meterID, commandName , obis, null , async, timeout);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			res = checkOBISPermission( requestTime, meterID, commandName, null);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			// Check User, Save WS_METERCONFIG_LOG and PutQueue
			res = SaveAndPutQueue(requestTime,meterID, valueStr, async,  commandName, obis , timeout);
		} catch (Exception e) {
			log.error(e,e);
			ErrorCode err = ErrorCode.SystemError;
			res.setErrorCode(err.getCode());
			res.setErrorString(err.getMessage());
		}
		return res;
	}
	@WebMethod(operationName = "GetHANConfigObject")
	public @WebResult(name = "GetHANConfigObjectResult") McResponse getHANConfigObject(
			@WebParam(name = "meterID") java.lang.String meterID,
			@WebParam(name = "async") java.lang.Boolean async,
			@WebParam(name = "timeout") java.lang.Integer timeout
			)
					throws Exception {

		String commandName = "GetHANConfigObject";
		log.info(commandName + " Start: meterID=" + meterID + ",async=" + async + "timeout=" + timeout );
		
		String valueStr = "";

		McResponse res = new McResponse();
		try {
			String requestTime = TimeUtil.getCurrentTime();
			ObisInfo obis = ObisInfo.getByCommand(commandName);
			// Check Parameter
			res = checkParameter(requestTime,meterID, commandName , obis, null, async, timeout );
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			res = checkOBISPermission( requestTime, meterID, commandName, null);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			// Check User, Save WS_METERCONFIG_LOG and PutQueue
			res = SaveAndPutQueue(requestTime,meterID, valueStr, async,  commandName, obis, timeout );
		} catch (Exception e) {
			log.error(e,e);
			ErrorCode err = ErrorCode.SystemError;
			res.setErrorCode(err.getCode());
			res.setErrorString(err.getMessage());
		}
		return res;
	}


	@WebMethod(operationName = "GetBillingCycle")
	public @WebResult(name = "GetBillingCycleResult") McResponse getBillingCycle(
			@WebParam(name = "meterID") java.lang.String meterID,
			@WebParam(name = "async") java.lang.Boolean async,
			@WebParam(name = "timeout") java.lang.Integer timeout
			)
					throws Exception {
		String commandName = "GetBillingCycle";
		log.info(commandName + " Start: meterID=" + meterID + ",async=" + async + "timeout=" + timeout );

		String valueStr = "";

		McResponse res = new McResponse();
		try {
			String requestTime = TimeUtil.getCurrentTime();
			ObisInfo obis = ObisInfo.getByCommand(commandName);
			// Check Parameter
			res = checkParameter(requestTime,meterID, commandName , obis, null , async, timeout);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			res = checkOBISPermission( requestTime, meterID, commandName, null);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			// Check User, Save WS_METERCONFIG_LOG and PutQueue
			res = SaveAndPutQueue(requestTime,meterID, valueStr, async,  commandName, obis , timeout);
		} catch (Exception e) {
			log.error(e,e);
			ErrorCode err = ErrorCode.SystemError;
			res.setErrorCode(err.getCode());
			res.setErrorString(err.getMessage());
		}
		return res;
	}

	@WebMethod(operationName = "GetPowerQualityCycle")
	public @WebResult(name = "GetPowerQualityCycleResult") McResponse getPowerQualityCycle(
			@WebParam(name = "meterID") java.lang.String meterID,
			@WebParam(name = "async") java.lang.Boolean async,
			@WebParam(name = "timeout") java.lang.Integer timeout
			)
					throws Exception {

		String commandName = "GetPowerQualityCycle";
		log.info(commandName + " Start: meterID=" + meterID + ",async=" + async + "timeout=" + timeout );
		
		String valueStr = "";

		McResponse res = new McResponse();
		try {
			String requestTime = TimeUtil.getCurrentTime();
			ObisInfo obis = ObisInfo.getByCommand(commandName);
			// Check Parameter
			res = checkParameter(requestTime,meterID, commandName , obis, null , async, timeout);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			res = checkOBISPermission( requestTime, meterID, commandName, null);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			// Check User, Save WS_METERCONFIG_LOG and PutQueue
			res = SaveAndPutQueue(requestTime,meterID, valueStr, async,  commandName, obis , timeout);
		} catch (Exception e) {
			log.error(e,e);
			ErrorCode err = ErrorCode.SystemError;
			res.setErrorCode(err.getCode());
			res.setErrorString(err.getMessage());
		}
		return res;
	}


	@WebMethod(operationName = "SetBreakerMode")
	public @WebResult(name = "SetBreakerModeResult") McResponse setBreakerMode(
			@WebParam(name = "meterID") java.lang.String meterID,
			@WebParam(name = "setValue") java.lang.String setValue,
			@WebParam(name = "async") java.lang.Boolean async,
			@WebParam(name = "timeout") java.lang.Integer timeout
			)
					throws Exception {

		String commandName = "SetBreakerMode";
		log.info(commandName + " Start: meterID=" + meterID + ",setValue=" + setValue + ",async=" + async + "timeout=" + timeout );
		

		McResponse res = new McResponse();
		try {
			String requestTime = TimeUtil.getCurrentTime();
			ObisInfo obis = ObisInfo.getByCommand(commandName);
			// Check Parameter
			res = checkParameter(requestTime,meterID, commandName , obis, setValue , async, timeout);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			res = checkOBISPermission( requestTime, meterID, commandName, setValue);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			// Check User, Save WS_METERCONFIG_LOG and PutQueue
			res = SaveAndPutQueue(requestTime,meterID, setValue, async,  commandName, obis , timeout);
		} catch (Exception e) {
			log.error(e,e);
			ErrorCode err = ErrorCode.SystemError;
			res.setErrorCode(err.getCode());
			res.setErrorString(err.getMessage());
		}
		return res;
	}

	@WebMethod(operationName = "SetHANConfigObject")
	public @WebResult(name = "SetHANConfigObjectResult") McResponse setHANConfigObject(
			@WebParam(name = "meterID") java.lang.String meterID,
			@WebParam(name = "setValue") java.lang.String setValue,
			@WebParam(name = "async") java.lang.Boolean async,
			@WebParam(name = "timeout") java.lang.Integer timeout
			)
					throws Exception {
		String commandName = "SetHANConfigObject";
		log.info(commandName + " Start: meterID=" + meterID + ",setValue=" + setValue + ",async=" + async + "timeout=" + timeout );

		McResponse res = new McResponse();
		try {
			String requestTime = TimeUtil.getCurrentTime();
			ObisInfo obis = ObisInfo.getByCommand(commandName);
			// Check Parameter
			res = checkParameter(requestTime,meterID, commandName , obis, setValue, async, timeout);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			res =checkOBISPermission( requestTime, meterID, commandName, setValue);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			// Check User, Save WS_METERCONFIG_LOG and PutQueue
			res = SaveAndPutQueue(requestTime,meterID, setValue, async,  commandName, obis , timeout);
		} catch (Exception e) {
			log.error(e,e);
			ErrorCode err = ErrorCode.SystemError;
			res.setErrorCode(err.getCode());
			res.setErrorString(err.getMessage());
		}
		return res;
	}

	@WebMethod(operationName = "SetBillingCycle")
	public @WebResult(name = "SetBillingCycleResult") McResponse setBillingCycle(
			@WebParam(name = "meterID") java.lang.String meterID,
			@WebParam(name = "setValue") java.lang.String setValue,
			@WebParam(name = "async") java.lang.Boolean async,
			@WebParam(name = "timeout") java.lang.Integer timeout
			)
					throws Exception {
		String commandName = "SetBillingCycle";
		log.info(commandName + " Start: meterID=" + meterID + ",setValue=" + setValue + ",async=" + async + "timeout=" + timeout );
		McResponse res = new McResponse();

		try {
			String requestTime = TimeUtil.getCurrentTime();
			ObisInfo obis = ObisInfo.getByCommand(commandName);

			// Check Parameter
			res = checkParameter(requestTime,meterID, commandName , obis, setValue , async, timeout);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			res = checkOBISPermission( requestTime, meterID, commandName, setValue);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			// Check User, Save WS_METERCONFIG_LOG and PutQueue
			res = SaveAndPutQueue(requestTime,meterID, setValue, async,  commandName, obis , timeout);
		} catch (Exception e) {
			log.error(e,e);
			ErrorCode err = ErrorCode.SystemError;
			res.setErrorCode(err.getCode());
			res.setErrorString(err.getMessage());
		}
		return res;
	}

	@WebMethod(operationName = "SetPowerQualityCycle")
	public @WebResult(name = "SetPowerQualityCycleResult") McResponse setPowerQualityCycle(
			@WebParam(name = "meterID") java.lang.String meterID,
			@WebParam(name = "setValue") java.lang.String setValue,
			@WebParam(name = "async") java.lang.Boolean async,
			@WebParam(name = "timeout") java.lang.Integer timeout
			)
					throws Exception {

		String commandName = "SetPowerQualityCycle";
		log.info(commandName + " Start: meterID=" + meterID + ",setValue=" + setValue + ",async=" + async + "timeout=" + timeout );
		McResponse res = new McResponse();
		try {
			String requestTime = TimeUtil.getCurrentTime();
			ObisInfo obis = ObisInfo.getByCommand(commandName);
			// Check Parameter
			res = checkParameter(requestTime,meterID, commandName , obis, setValue, async, timeout );
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			res = checkOBISPermission( requestTime, meterID, commandName, setValue);
			if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
				return res;
			}
			// Check User, Save WS_METERCONFIG_LOG and PutQueue
			res = SaveAndPutQueue(requestTime,meterID, setValue, async,  commandName, obis , timeout);
		} catch (Exception e) {
			log.error(e,e);
			ErrorCode err = ErrorCode.SystemError;
			res.setErrorCode(err.getCode());
			res.setErrorString(err.getMessage());
		}
		return res;
	}

	/**
	 * SP-995,996
	 * @param mdsId
	 * @return
	 */
	private Map<String,Object> getDeviceInfo(String mdsId) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		String deviceType = null;

		Meter meter = meterManager.getMeter(mdsId);
		if ( meter == null) {
			retMap.put("error", ErrorCode.MeterNotExist);
			return retMap;
		}
		Modem modem = meter.getModem();
		if (  modem == null || modem.getModemType() == null || modem.getProtocolType()== null ) {
			retMap.put("error", ErrorCode.MeterNotReached);
			return retMap;
		}

		retMap.put("deviceSerial", modem.getDeviceSerial());
		retMap.put("modemType", modem.getModemType());
		retMap.put("protocolType", modem.getProtocolType());
		if (modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.SMS  &&
				modem.getPhoneNumber() != null ) {
			deviceType = "MBB";
		} else if ( modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.GPRS) { 
			deviceType = "GPRS";
		} else if (modem.getModemType() == ModemType.MMIU && modem.getProtocolType() == Protocol.IP) { // Ethernet Modem
			deviceType = "Ethernet";
		} else if (modem.getModemType() == ModemType.SubGiga && modem.getProtocolType() == Protocol.IP) { // RF Modem
			deviceType = "RF";
		}else { // Not Supported
			retMap.put("error", ErrorCode.MeterNotReached);
			return retMap;
		}
		retMap.put("deviceType", deviceType);
		return retMap;
	}

	/**
	 * SP-995,996
	 * @param meterID
	 * @param setValue
	 * @param async
	 * @param commandName
	 * @param obis
	 * @param timeout
	 * @return
	 */
	McResponse SaveAndPutQueue(String requestDate, String meterID, String setValue, Boolean async,  String commandName, ObisInfo obis, Integer timeout ){
		log.info( "Start - meterId: " + meterID + " setValue: " + setValue + " async: " + async +  "commandName: " + commandName +  "ObisInfo:" + obis);

		Map<String,Object>  deviceInfoMap = null;
		ErrorCode err = ErrorCode.Success;
		String trId = null;
		boolean isSync = true;
		McResponse res = new McResponse();

		String currentTime = null;
		int  syncTimeOut = _defaultSyncTimeOut;

		if ( async != null &&  async) {
			isSync = false;	
		}

		if ( timeout != null) {
			syncTimeOut = timeout;
		}
		String username = (String) wsContext.getMessageContext().get("USERNAME");    	
		//		String password = (String) wsContext.getMessageContext().get("PASSWORD");

		String setValueStr = null;

		TransactionStatus txStatus = null;
		try {
			txStatus = txManager.getTransaction(null);

			currentTime = TimeUtil.getCurrentTime();
			if ( setValue != null && setValue.length() > 0) {
				setValueStr = obis.makeJsonSetValue(setValue);
			}
			// Check Meter
			deviceInfoMap = getDeviceInfo(meterID);
			if ( deviceInfoMap.get("error") != null ) {
				err = (ErrorCode) deviceInfoMap.get("error") ;
				res.setErrorString( err.getMessage());
				res.setErrorCode(err.getCode());
				throw new Exception(err.getMessage());
			}

			// save WS_METERCONFIG_LOG
			trId = cmdUtil.createWsMeterconfigLog(meterID, 
					(String)deviceInfoMap.get("deviceSerial"),
					(ModemType)deviceInfoMap.get("modemType"),
					(Protocol)deviceInfoMap.get("protocolType"),
					(String)deviceInfoMap.get("deviceType"),
					obis.getAttributeNo(),
					obis.getClassId(),
					obis.getObisCode(),
					username, TR_STATE.Waiting.getCode(),
					null,
					commandName, setValueStr,
					requestDate,
					currentTime, 
					null);
			txManager.commit(txStatus);
		}
		catch (Exception e) {
			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				}
				catch (Exception ee) {}
			}
			if ( err == ErrorCode.Success) {
				// transaction error
				err = ErrorCode.SystemError;
			}
			log.error(e, e);
		}

		if ( trId == null && err == ErrorCode.Success  )
			err = ErrorCode.SystemError;
		if (  err != ErrorCode.Success ) { // Error Occurred
			res.setErrorString( err.getMessage());
			res.setErrorCode(err.getCode());
			cmdUtil.createWsMeterconfigLog(meterID, 
					(deviceInfoMap == null || deviceInfoMap.get("deviceSerial") == null ) ? "" : (String)deviceInfoMap.get("deviceSerial"),
							(deviceInfoMap == null || deviceInfoMap.get("modemType") == null )? null : (ModemType)deviceInfoMap.get("modemType"),
									(deviceInfoMap == null || deviceInfoMap.get("protocolType") == null) ? null :(Protocol) deviceInfoMap.get("protocolType"), "",
											obis.getAttributeNo(),obis.getClassId(),obis.getObisCode(),
											username, TR_STATE.Terminate.getCode(),err.getCode(),
											commandName,setValue,
											requestDate, currentTime, currentTime);
			return res;
		}


		// Put Queue
		try {
			///////
			MeterConfigureMessage message = new MeterConfigureMessage();
			message.setTrId(trId);
			message.setMeterId(meterID);
			message.setSync(isSync);
			message.setCommand(commandName);

			//message.setMultiSpeakMsgHeader(multiSpeakMsgHeader.value);
			message.setRequestedTime(Calendar.getInstance());
			handler.putServiceData(MeterControlQueueHandler.MC_MESSAGE, message);
		}
		catch ( Exception e) {
			if ( err == ErrorCode.Success) {
				err = ErrorCode.SystemError;
			}
			res.setErrorString( err.getMessage());
			res.setErrorCode(err.getCode());
			return res;
		}

		// Return or Wait 
		if ( !isSync ) {
			err = ErrorCode.Success;
			res.setErrorString( err.getMessage());
			res.setErrorCode(err.getCode());
			res.setTrID(trId);
			return res;
		}
		else {
			res = cmdUtil.waitAndGetWSMeterConfigResult(meterID, trId, commandName,  syncTimeOut*1000);
		}
		log.info( "Finish - meterId: " + meterID + " trId: " + trId + " async: " + async +  "commandName: " + commandName +  "ObisInfo:" + obis);
		return res;
	}

	private boolean checkObisValue(ObisInfo obis, String value)
	{
		long lval = 0;
		boolean ret = true;
		try {
			// check by obis datatype
			if ( obis.getDataType().equals("enum")) {
				lval  =  Long.parseLong(value);
				if ( lval < 0  ) {
					ret = false;
				}
			}
			else if ( obis.getDataType().equals("boolean")) {

			}
			else if ( obis.getDataType().equals("long-unsigned")) {
				lval =  Long.parseLong(value);
				if ( lval < 0  || lval > 0xFFFFL  ) {
					ret = false;
				}
			}
			else if ( obis.getDataType().equals("double-long-unsigend")) {
				lval =  Long.parseLong(value);
				if ( lval < 0 || lval > 0xFFFFFFFFL ) {
					ret = false;
				}
			}

			// check by each OBIS 
			switch ( obis ) {
				case BREAKER_MODE:
					if ( lval > 7 )
						ret = false;
					break;
				default:
					break;
			}
		}catch (Exception e){
			ret = false;
		}
		return ret;
	}

	private McResponse checkParameter(String requestDate, String meterID, String cmd ,ObisInfo obis, String setValue, Boolean sync, Integer timeout ){

		ErrorCode err = ErrorCode.Success;
		McResponse result  = new McResponse();
		String username = (String) wsContext.getMessageContext().get("USERNAME");  

		// Check Parameter
		if ( meterID == null || meterID.length() == 0 ) {
			err = ErrorCode.InvalidParameter;
		}
		Long val;
		String currentTime = null;
		try {
			currentTime = TimeUtil.getCurrentTime();
			// check timeout
			if ( timeout != null && timeout <= 0  ) {
				throw new Exception();
			}
			// check setValue
			if ( cmd.startsWith("Set") ) {
				if ( setValue == null || setValue.length() == 0 ||
						checkObisValue(obis, setValue ) == false ) { 
					throw new Exception();
				}
			}	
		}
		catch (Exception e) {
			err = ErrorCode.InvalidParameter;	
		}
		if ( err != ErrorCode.Success) {
			cmdUtil.createWsMeterconfigLog(meterID,"",null,null, "",
					obis.getAttributeNo(),obis.getClassId(),obis.getObisCode(),
					username, TR_STATE.Terminate.getCode(),err.getCode(),
					cmd,setValue,
					requestDate,currentTime, currentTime);
		}
		result.setErrorString(err.getMessage());
		result.setErrorCode(err.getCode());
		return result;
	}


	private McResponse checkOBISPermission(String requestDate, String meterId, String cmd, String setValue ){

		ErrorCode err = ErrorCode.Success;
		//Map<String,String> rtnMap = new HashMap<String,String>();
		McResponse res = new McResponse();
		String username = (String) wsContext.getMessageContext().get("USERNAME");  		
		String currentTime = null;
		ObisInfo obisInfo = null;
		TransactionStatus txStatus = null;
		try {
			currentTime = TimeUtil.getCurrentTime();

			obisInfo = ObisInfo.getByCommand(cmd);
			String   permission = "";
			if (cmd.startsWith("Get")) {
				permission = "R";
			} else if (cmd.startsWith("Set")) {
				permission = "W";
			}
			if (obisInfo == null || permission == null) {
				err = ErrorCode.InvalidParameter;
			}															

			if (err == ErrorCode.Success) {
				txStatus = txManager.getTransaction(null);		
				WSMeterConfigOBIS mcOBIS = wsMeterConfigOBISDao.get(username, 
						obisInfo.getObisCode(), obisInfo.getClassId(), obisInfo.getAttributeNo());
				if(mcOBIS==null) {
					err = ErrorCode.ObisPermissionError;
				}
				else {
					if (!mcOBIS.getPermission().contains(permission)) {
						err = ErrorCode.ObisPermissionError;
					}
				}

				txManager.commit(txStatus);
			}
		}
		catch (Exception e) {
			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				}
				catch (Exception ee) {}
			}
			err = ErrorCode.SystemError;	
		}

		if ( err != ErrorCode.Success) {
			res.setErrorString(err.getMessage());
			res.setErrorCode(err.getCode());
			cmdUtil.createWsMeterconfigLog(meterId,"",null,null, "",
					obisInfo == null ? "" : obisInfo.getAttributeNo(),
					obisInfo == null ? "" : obisInfo.getClassId(), 
					obisInfo == null ? "" : obisInfo.getObisCode(),
					username, TR_STATE.Terminate.getCode(),err.getCode(),
					cmd,setValue,
					requestDate, currentTime, currentTime);
		}
		return res;
	}	

}
