package com.aimir.model.mvm;

public class DataGaps {
	
	private String successCnt = "0";
	private String failCnt = "0";
	private String cnt = "0";
	private String width = "0";
	private String name;
	private String mdsId;
	private String lastReadDate;	
	private String lpInterval;

	public String getLpInterval() {
		return lpInterval;
	}
	public void setLpInterval(String lpInterval) {
		this.lpInterval = lpInterval;
	}
	public String getMdsId() {
		return mdsId;
	}
	public void setMdsId(String mdsId) {
		this.mdsId = mdsId;
	}
	public String getLastReadDate() {
		return lastReadDate;
	}
	public void setLastReadDate(String lastReadDate) {
		this.lastReadDate = lastReadDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSuccessCnt() {
		return successCnt;
	}
	public void setSuccessCnt(String successCnt) {
		this.successCnt = successCnt;
	}
	public String getFailCnt() {
		return failCnt;
	}
	public void setFailCnt(String failCnt) {
		this.failCnt = failCnt;
	}
	public String getCnt() {
		return cnt;
	}
	public void setCnt(String cnt) {
		this.cnt = cnt;
	}		
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}		
}
