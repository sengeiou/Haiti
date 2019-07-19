package com.aimir.model.device;



public class MCUTypeByCommStateVO {

	private String mcuType;
	private String mcuTypeId;
	private String mcuTypeCode;
	private String commState;
	private String cnt;
	
	private String twentyfourCnt = "0";
	private String fortyeightCnt = "0";
	private String normalCnt = "0";
	private String otherCnt = "0";
	
	public String getMcuType() {
		return mcuType;
	}
	public void setMcuType(String mcuType) {
		this.mcuType = mcuType;
	}
	public String getMcuTypeId() {
		return mcuTypeId;
	}
	public void setMcuTypeId(String mcuTypeId) {
		this.mcuTypeId = mcuTypeId;
	}
	public String getMcuTypeCode() {
		return mcuTypeCode;
	}
	public void setMcuTypeCode(String mcuTypeCode) {
		this.mcuTypeCode = mcuTypeCode;
	}
	public String getCommState() {
		return commState;
	}
	public void setCommState(String commState) {
		this.commState = commState;
	}
	public String getCnt() {
		return cnt;
	}
	public void setCnt(String cnt) {
		this.cnt = cnt;
	}
	public String getTwentyfourCnt() {
		return twentyfourCnt;
	}
	public void setTwentyfourCnt(String twentyfourCnt) {
		this.twentyfourCnt = twentyfourCnt;
	}
	public String getFortyeightCnt() {
		return fortyeightCnt;
	}
	public void setFortyeightCnt(String fortyeightCnt) {
		this.fortyeightCnt = fortyeightCnt;
	}
	public String getNormalCnt() {
		return normalCnt;
	}
	public void setNormalCnt(String normalCnt) {
		this.normalCnt = normalCnt;
	}
	public String getOtherCnt() {
		return otherCnt;
	}
	public void setOtherCnt(String otherCnt) {
		this.otherCnt = otherCnt;
	}	
}
