package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "LP_EM_EXTERNAL")
public class LpEmExternal {

	@EmbeddedId public LpExPk id;
	
	@Column(name = "MDEV_TYPE")
	private String mdevType;
	
	@Column(name = "DST")
	private Integer dst;
	
	@Column(name = "DEVICE_ID")
	private String deviceId;
	
	@Column(name = "DEVICE_TYPE")
	private String deviceType;
	
	@Column(name = "METERINGTYPE")
	private Integer meteringtype;
	
	@Column(name = "DEVICE_SERIAL")
	private String deviceSerial;
	
	@Column(name = "LP_STATUS")
	private String lpStatus;
	
	@Column(name = "INTERVAL_YN")
	private int intervalYn;
	
	@Column(name = "VALUE")
	private double value;
	
	@Column(name = "WRITEDATE")
	private String writedate;
	
	@Column(name = "CONTRACT_ID")
	private Integer contractId;
	
	@Column(name = "MODEM_TIME")
	private String modemTime;
	
	@Column(name = "DCU_TIME")
	private String dcuTime;

	public LpEmExternal() {
		id = new LpExPk();
	}

	public LpExPk getId() {
		return id;
	}


	public void setId(LpExPk id) {
		this.id = id;
	}


	public String getMdevType() {
		return mdevType;
	}


	public void setMdevType(String mdevType) {
		this.mdevType = mdevType;
	}


	public Integer getDst() {
		return dst;
	}


	public void setDst(Integer dst) {
		this.dst = dst;
	}


	public String getDeviceId() {
		return deviceId;
	}


	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}


	public String getDeviceType() {
		return deviceType;
	}


	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}


	public Integer getMeteringtype() {
		return meteringtype;
	}


	public void setMeteringtype(Integer meteringtype) {
		this.meteringtype = meteringtype;
	}


	public String getDeviceSerial() {
		return deviceSerial;
	}


	public void setDeviceSerial(String deviceSerial) {
		this.deviceSerial = deviceSerial;
	}


	public String getLpStatus() {
		return lpStatus;
	}


	public void setLpStatus(String lpStatus) {
		this.lpStatus = lpStatus;
	}


	public int getIntervalYn() {
		return intervalYn;
	}


	public void setIntervalYn(int intervalYn) {
		this.intervalYn = intervalYn;
	}


	public double getValue() {
		return value;
	}


	public void setValue(double value) {
		this.value = value;
	}


	public String getWritedate() {
		return writedate;
	}


	public void setWritedate(String writedate) {
		this.writedate = writedate;
	}


	public Integer getContractId() {
		return contractId;
	}


	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}


	public String getModemTime() {
		return modemTime;
	}


	public void setModemTime(String modemTime) {
		this.modemTime = modemTime;
	}


	public String getDcuTime() {
		return dcuTime;
	}


	public void setDcuTime(String dcuTime) {
		this.dcuTime = dcuTime;
	}

	public void setMdevId(String mdevId) {
		id.mdevId = mdevId;
	}

	public void setChannel(Integer channel) {
		id.channel = channel;
	}

	public void setYyyymmddhhmiss(String yyyymmddhhmiss) {
		id.yyyymmddhhmiss = yyyymmddhhmiss;
	}	
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(id.getMdevId()).append("|")
			.append(id.getYyyymmddhhmiss()).append("|")
			.append(id.getChannel()).append("|")
			.append(getMdevType()).append("|")
			.append(getDst()).append("|")
			.append(getDeviceId()).append("|")
			.append(getDeviceType()).append("|")
			.append(getMeteringtype()).append("|")
			.append(getDeviceSerial()).append("|")
			.append(getLpStatus()).append("|")
			.append(getIntervalYn()).append("|")
			.append(getValue()).append("|")
			.append(getWritedate()).append("|")
			.append(getContractId()).append("|")
			.append(getModemTime()).append("|")
			.append(getDcuTime()).append(".");
		
		return buffer.toString();
	}	
	
	
}
