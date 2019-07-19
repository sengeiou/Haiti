package com.aimir.mars.integration.metercontrol.util;

import java.util.Map;

import com.aimir.mars.integration.metercontrol.util.MeterControlConstants.ObisInfo;

import net.sf.json.JSONArray;

public class MeterControlConstants {

	public enum ErrorCode {
		Success(0, "Success"),
		Running(1, "Command is being executed"),
		InvalidParameter(2, "Invalid Parameter"),
		AuthenticationError(3, "Authentication Error"),
		ObisPermissionError(4, "OBIS Permission Error"),
		MeterNotExist(5,"The requested meter does not exist"),
		MeterNotReached(6, "The meter could not be reached"),
		TimeOut(7,"The request is time out"),
		NoResult(8,"The requested result does not exist"),
		PermissionError(9, "Permission Error"),
		UserNotExist(10, "The requested user does not exist"),
		UserAlreadyExist(11, "The requested user already exists"),
		SettingAlreadyExist(12, "The requested setting already exists"),
		SystemError(100, "System Error");
		
		private int code;
		private String message;

		ErrorCode(int code, String message)
		{
			this.code = code;
			this.message = message;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		static public ErrorCode getErrorCode(Integer code) {
			for (ErrorCode err : ErrorCode.values()) {
				if ( err.code == code) {
					return err;
				}
			}
			return null;
		}
	}
	
	public enum ResultMapKey {
		errorCode,
		errorString,
		status,
		resultValue,
		trID
	}
	
	public enum ObisInfo {
		BREAKER_MODE("BreakerMode","0.0.96.3.10.255", "70", "4", "enum"), //0,1,2,3,4,5,6,7
		BREAKER_OUTPUT_STATE("BreakerOutputState", "0.0.96.3.10.255", "70", "2", "boolean"),
		BREAKER_CONTROL_STATE("BreakerControlState","0.0.96.3.10.255", "70", "3", "enum"),//0,1,2
		HAN_CONFIG_OBJECT("HANConfigObject", "0.1.94.31.3.255","1", "2", "long-unsigned"),
		BILLING_CYCLE("BillingCycle", "1.0.99.1.0.255", "7", "4", "double-long-unsigend" ),
		POWER_QUALITY_CYCLE("PowerQualityCycle", "1.0.99.2.0.255", "7", "4", "double-long-unsigend");
		private String command;
		private String obisCode;
		private String classId;
		private String attributeNo;
		private String dataType;

		ObisInfo(String command, String obisCode, String classId, String attributeNo, String dataType ) {
			this.command = command;
			this.obisCode = obisCode;
			this.classId = classId;
			this.attributeNo = attributeNo;
			this.dataType = dataType;
		}
		public String getObisCode() {
			return obisCode;
		}

		public void setObisCode(String obisCode) {
			this.obisCode = obisCode;
		}

		public String getClassId() {
			return classId;
		}

		public void setClassId(String classId) {
			this.classId = classId;
		}

		public String getAttributeNo() {
			return attributeNo;
		}

		public void setAttributeNo(String attributeNo) {
			this.attributeNo = attributeNo;
		}
		public String getDataType() {
			return dataType;
		}
		public void setDataType(String dataType) {
			this.dataType = dataType;
		}
		public String getCommand()
		{
			return this.command;
		}
		public void setCommand(String command)
		{
			this.command = command;
		}
		public String toString() {
			return  "command="+ command + ",obis=" + obisCode + "|" + classId + "|" + attributeNo + "|" + dataType ;
		}
		
		public String makeJsonSetValue( String value)
		{
			String ret = null;
			if ( dataType.equals("enum") 
					|| dataType.equals("long-unsigned")
					|| dataType.equals("double-long-unsigend")
					|| dataType.equals("boolean")) {
				ret = "[{\"value\":\"" + value + "\"}]";
			}
			return ret;
		}
		
		public String getValueFromResult( String value)
		{
			JSONArray jsonArr = JSONArray.fromObject(value);
			
			String ret = null;
			if ( dataType.equals("enum") 
					|| dataType.equals("long-unsigned")
					|| dataType.equals("double-long-unsigend") 
					|| dataType.equals("boolean")) {
				Map<String,String> map = (Map<String, String>) jsonArr.toArray()[0];
				ret = map.get("value");
			}
			return ret;
		}
		public static ObisInfo getByObisCode(String obisCode, String classId, String attributeNo  ){
			for(ObisInfo o : ObisInfo.values()){
				if(o.getObisCode().equals(obisCode) 
						&& o.getClassId().equals(classId) 
						&& o.getAttributeNo().equals(attributeNo))
					return o;
			}
			return null;
		}
		
		public static ObisInfo getByCommand(String command) {
			String cmd = command.replace("Get", "").replace("Set", "");
			for(ObisInfo o : ObisInfo.values()){
				if(o.getCommand().equals(cmd))
					return o;
			}
			return null;
		}
	}

}
