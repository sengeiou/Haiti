package com.aimir.model.device;


public class CommStateByMCUTypeVO {

	private String commState;
	private String mcuType;
	private String cnt;

	private String indoorCnt = "0";
	private String outdoorCnt = "0";
	private String DCUCnt = "0";
	
	public String getCommState() {
		return commState;
	}
	public void setCommState(String commState) {
		this.commState = commState;
	}
	public String getMcuType() {
		return mcuType;
	}
	public void setMcuType(String mcuType) {
		this.mcuType = mcuType;
	}
	public String getCnt() {
		return cnt;
	}
	public void setCnt(String cnt) {
		this.cnt = cnt;
	}

	public String getIndoorCnt() {
		return indoorCnt;
	}
	public void setIndoorCnt(String indoorCnt) {
		this.indoorCnt = indoorCnt;
	}

	public String getOutdoorCnt() {
		return outdoorCnt;
	}
	public void setOutdoorCnt(String outdoorCnt) {
		this.outdoorCnt = outdoorCnt;
	}
	
	public String getDCUCnt() {
		return DCUCnt;
	}
	public void setDCUCnt(String dCUCnt) {
		DCUCnt = dCUCnt;
	}	
}
