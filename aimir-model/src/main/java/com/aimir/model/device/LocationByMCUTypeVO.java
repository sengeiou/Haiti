package com.aimir.model.device;


public class LocationByMCUTypeVO {

	private String name;
	private String id;
	private String mcutype;
	private String cnt;

	private String indoorCnt = "0";
	private String outdoorCnt = "0";
	private String DCUCnt = "0";
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMcutype() {
		return mcutype;
	}
	public void setMcutype(String mcutype) {
		this.mcutype = mcutype;
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
