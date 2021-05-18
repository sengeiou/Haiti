package com.aimir.schedule.exception;

public class DailyBillingException extends Exception {

	private String errCode;
	private String descr;
	
	private String billing_yyyymmddhh;
	private Double billing_activeEnergy;
	
	private String prev_yyyymmddhh;
	private Double prev_activeEnergy;
	
	private String lastBillingDate;
	
	public DailyBillingException(String errCode, String descr) {
		super(descr);
		
		this.errCode = errCode;
		this.descr = descr;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getPrev_yyyymmddhh() {
		return prev_yyyymmddhh;
	}

	public void setPrev_yyyymmddhh(String prev_yyyymmddhh) {
		this.prev_yyyymmddhh = prev_yyyymmddhh;
	}

	public Double getPrev_activeEnergy() {
		return prev_activeEnergy;
	}

	public void setPrev_activeEnergy(Double prev_activeEnergy) {
		this.prev_activeEnergy = prev_activeEnergy;
	}

	public String getLastBillingDate() {
		return lastBillingDate;
	}

	public void setLastBillingDate(String lastBillingDate) {
		this.lastBillingDate = lastBillingDate;
	}

	public String getBilling_yyyymmddhh() {
		return billing_yyyymmddhh;
	}

	public void setBilling_yyyymmddhh(String billing_yyyymmddhh) {
		this.billing_yyyymmddhh = billing_yyyymmddhh;
	}

	public Double getBilling_activeEnergy() {
		return billing_activeEnergy;
	}

	public void setBilling_activeEnergy(Double billing_activeEnergy) {
		this.billing_activeEnergy = billing_activeEnergy;
	}
	
}
