package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.aimir.annotation.ColumnInfo;

/**
 * <p>
 * Copyright NuriTelecom Co.Ltd. since 2009
 * </p>
 * 
 * <p>
 * EMnV LTE Modem
 * </p>
 * 
 * 
 */
@Entity
@DiscriminatorValue("LTE")
public class LTE extends Modem {

	private static final long serialVersionUID = 1L;

	@ColumnInfo(name = "modemTime", descr = "현재시간 YYYYMMDDHHMMSS")
	@Column(name = "MODEM_TIME", length = 14)
	private String modemTime;

	@ColumnInfo(name = "rsrp", descr = "RSRP(Reference Signal Received Power)[단위 dBm]")
	@Column(name = "RSRP")
	//private int rsrp;
	private String rsrp;

	@ColumnInfo(name = "rsrq", descr = "RSRP(Reference Signal Quality Power)[단위 dBm]")
	@Column(name = "RSRQ")
	//private int rsrq;
	private String rsrq; // 추후 int형으로 바꿀것

	@ColumnInfo(name = "txPower", descr = "TX Power [단위 dBm]")
	@Column(name = "TX_POWER")
	//private int txPower;
	private String txPower; // 추후 int형으로 바꿀것

	@ColumnInfo(name = "plmn", descr = "Public Land Mobile Network")
	@Column(name = "PLMN", length = 32)
	private String plmn;

	@ColumnInfo(name = "mdPeriod", descr = "검침주기 [단위 분]")
	@Column(name = "MD_PERIOD", length = 32)
	private int mdPeriod;

	@ColumnInfo(name = "prodMaker", descr = "제작사")
	@Column(name = "PROD_MAKER")
	private String prodMaker;

	@ColumnInfo(name = "prodMakeDate", descr = "제조년월일")
	@Column(name = "PROD_MAKE_DATE")
	private String prodMakeDate;

	@ColumnInfo(name = "prodSerial", descr = "제조번호")
	@Column(name = "PROD_SERIAL")
	private String prodSerial;

	public String getModemTime() {
		return modemTime;
	}

	public void setModemTime(String modemTime) {
		this.modemTime = modemTime;
	}

	public String getRsrp() {
		return rsrp;
	}

	public void setRsrp(String rsrp) {
		this.rsrp = rsrp;
	}

	public String getRsrq() {
		return rsrq;
	}

	public void setRsrq(String rsrq) {
		this.rsrq = rsrq;
	}

	public String getTxPower() {
		return txPower;
	}

	public void setTxPower(String txPower) {
		this.txPower = txPower;
	}

	public String getPlmn() {
		return plmn;
	}

	public void setPlmn(String plmn) {
		this.plmn = plmn;
	}

	public int getMdPeriod() {
		return mdPeriod;
	}

	public void setMdPeriod(int mdPeriod) {
		this.mdPeriod = mdPeriod;
	}

	public String getProdMaker() {
		return prodMaker;
	}

	public void setProdMaker(String prodMaker) {
		this.prodMaker = prodMaker;
	}

	public String getProdMakeDate() {
		return prodMakeDate;
	}

	public void setProdMakeDate(String prodMakeDate) {
		this.prodMakeDate = prodMakeDate;
	}

	public String getProdSerial() {
		return prodSerial;
	}

	public void setProdSerial(String prodSerial) {
		this.prodSerial = prodSerial;
	}

}