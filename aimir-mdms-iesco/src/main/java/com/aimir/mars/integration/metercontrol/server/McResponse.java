package com.aimir.mars.integration.metercontrol.server;

public class McResponse {
	String errorString;
	int	errorCode = 0;
	String trID;
	String resultValue;
	

	public String getErrorString() {
		return errorString;
	}
	public void setErrorString(String errorString) {
		this.errorString = errorString;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getResultValue() {
		return resultValue;
	}
	public void setResultValue(String resultValue) {
		this.resultValue = resultValue;
	}
	public String getTrID() {
		return trID;
	}
	public void setTrID(String trID) {
		this.trID = trID;
	}
	
}
