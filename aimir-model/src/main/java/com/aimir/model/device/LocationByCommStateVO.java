package com.aimir.model.device;


public class LocationByCommStateVO {

	private String name;
	private String id;
	private String commState;
	private String cnt;
	
	private String twentyfourCnt = "0";
	private String fortyeightCnt = "0";
	private String normalCnt = "0";
	private String otherCnt = "0";
	
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
