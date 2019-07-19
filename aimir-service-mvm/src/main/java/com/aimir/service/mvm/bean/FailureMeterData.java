package com.aimir.service.mvm.bean;

public class FailureMeterData {
	private boolean checked;
	private String mdsId;
	private String meterId;
	private String mcuId;
	private String modemId;
	private String customerId;
	private String customerName;
	private String address;
	private String meterAddress;
	private String lastlastReadDate;
	private String failureCause;
	private String meterStatus;
	private String timeDiff;
	private String YYYYMMDD;
	
	public String getMdsId() {
		return mdsId;
	}
	public void setMdsId(String mdsId) {
		this.mdsId = mdsId;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public String getModemId() {
		return modemId;
	}
	public void setModemId(String modemId) {
		this.modemId = modemId;
	}
	public String getMeterAddress() {
		return meterAddress;
	}
	public void setMeterAddress(String meterAddress) {
		this.meterAddress = meterAddress;
	}
	public String getMeterId() {
		return meterId;
	}
	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}
	public String getMcuId() {
		return mcuId;
	}
	public void setMcuId(String mcuId) {
		this.mcuId = mcuId;
	}
	public String getCustomerId() {
		return customerId;
	}
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getLastlastReadDate() {
		return lastlastReadDate;
	}
	public void setLastlastReadDate(String lastlastReadDate) {
		this.lastlastReadDate = lastlastReadDate;
	}
	public String getFailureCause() {
		return failureCause;
	}
	public void setFailureCause(String failureCause) {
		this.failureCause = failureCause;
	}
	public void setMeterStatus (String meterStatus){
		this.meterStatus = meterStatus;
	}
	public String getMeterStatus (){
		return meterStatus;
	}
	public void setTimeDiff(String timeDiff){
		this.timeDiff = timeDiff;
	}
	public String getTimeDiff(){
		return timeDiff;
	}
	public void setYYYYMMDD(String YYYYMMDD){
		this.YYYYMMDD = YYYYMMDD;
	}
	public String getYYYYMMDD(){
		return YYYYMMDD;
	}
}
