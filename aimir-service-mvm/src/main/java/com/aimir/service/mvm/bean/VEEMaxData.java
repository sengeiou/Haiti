package com.aimir.service.mvm.bean;

public class VEEMaxData {
	
	private String writeTime;	//발생시간
	private String deviceId;	//장비아이디
	
	private String checkItem;	//체크항목
	
	
	private String meterId;	//.
	private String eventAlertLogId; // eventAlertLog인 경우 상세정보를 가져오기 위해.
	private String table;		//어느테이블인지..
	private String contractId;	//계약번호 only number
	private String contractNo;	//계약번호 varchar
	
	//lp, day, month 인경우  lp 데이터를 가져오기 위한 pk 정보.
	private String yyyymmdd;
	private String channel;	// 
	private String mdevId;			
	private String dst;
	private String mdevType; //// MCU(0), Modem(1), Meter(2);
	
	private int total;
	
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public String getDst() {
		return dst;
	}
	public void setDst(String dst) {
		this.dst = dst;
	}
	public String getYyyymmdd() {
		return yyyymmdd;
	}
	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}
	public String getMdevId() {
		return mdevId;
	}
	public void setMdevId(String mdevId) {
		this.mdevId = mdevId;
	}
	public String getMdevType() {
		return mdevType;
	}
	public void setMdevType(String mdevType) {
		this.mdevType = mdevType;
	}
	public String getWriteTime() {
		return writeTime;
	}
	public void setWriteTime(String writeTime) {
		this.writeTime = writeTime;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getContractId() {
		return contractId;
	}
	public String getContractNo() {
		return contractNo;
	}
	public void setContractId(String contractId) {
		this.contractId = contractId;
	}
	public String getCheckItem() {
		return checkItem;
	}
	public void setCheckItem(String checkItem) {
		this.checkItem = checkItem;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getMeterId() {
		return meterId;
	}
	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}
	public String getEventAlertLogId() {
		return eventAlertLogId;
	}
	public void setEventAlertLogId(String eventAlertLogId) {
		this.eventAlertLogId = eventAlertLogId;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	
}
