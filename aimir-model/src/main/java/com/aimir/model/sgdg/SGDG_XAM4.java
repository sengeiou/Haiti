package com.aimir.model.sgdg;

import com.aimir.model.BaseObject;

public class SGDG_XAM4 extends BaseObject{

	private static final long serialVersionUID = -2886365032395906273L;
	
	public SGDG_XAM4Pk id;
	
	private String meteringDate;
	
	private String realMeteringDate;
	
	private String gaugeNumber;
	
	private Double maxLD;
	
	private Double middleLD;
	
	private Double middleLD2;
	
	private Double middleLDS;
	
	private Double middleLDR;
	
	private Double miniLD;
	
	private String meteringMethodCode;

	private String meteringResultCode;

	private String meteringRegDate;
	
	private String meteringUpdateDate;
	
	private Double gnrAverage;
	
	private Double ngtAverage;
	
	private String genDate;
	
	private String genId;
	
	public SGDG_XAM4Pk getId() {
		return id;
	}

	public void setId(SGDG_XAM4Pk id) {
		this.id = id;
	}

	public String getContractId() {
		return id.getContractId();
	}
	
	public void setContractId(String contractId) {
		id.setContractId(contractId);
	}
	
	public String getBillDate() {
		return id.getBillDate();
	}
	
	public void setBillDate(String billDate) {
		id.setBillDate(billDate);
	}
	
	public String getMeteringFlag() {
		return id.getMeteringFlag();
	}
	
	public void setMeteringFlag(String meteringFlag) {
		id.setMeteringFlag(meteringFlag);
	}
	
	public String getMeteringDate() {
		return meteringDate;
	}

	public void setMeteringDate(String meteringDate) {
		this.meteringDate = meteringDate;
	}

	public String getRealMeteringDate() {
		return realMeteringDate;
	}

	public void setRealMeteringDate(String realMeteringDate) {
		this.realMeteringDate = realMeteringDate;
	}

	public String getGaugeNumber() {
		return gaugeNumber;
	}

	public void setGaugeNumber(String gaugeNumber) {
		this.gaugeNumber = gaugeNumber;
	}

	public Double getMaxLD() {
		return maxLD;
	}

	public void setMaxLD(Double maxLD) {
		this.maxLD = maxLD;
	}

	public Double getMiddleLD() {
		return middleLD;
	}

	public void setMiddleLD(Double middleLD) {
		this.middleLD = middleLD;
	}

	public Double getMiddleLD2() {
		return middleLD2;
	}

	public void setMiddleLD2(Double middleLD2) {
		this.middleLD2 = middleLD2;
	}

	public Double getMiddleLDS() {
		return middleLDS;
	}

	public void setMiddleLDS(Double middleLDS) {
		this.middleLDS = middleLDS;
	}

	public Double getMiddleLDR() {
		return middleLDR;
	}

	public void setMiddleLDR(Double middleLDR) {
		this.middleLDR = middleLDR;
	}

	public Double getMiniLD() {
		return miniLD;
	}

	public void setMiniLD(Double miniLD) {
		this.miniLD = miniLD;
	}

	public String getMeteringMethodCode() {
		return meteringMethodCode;
	}

	public void setMeteringMethodCode(String meteringMethodCode) {
		this.meteringMethodCode = meteringMethodCode;
	}

	public String getMeteringResultCode() {
		return meteringResultCode;
	}

	public void setMeteringResultCode(String meteringResultCode) {
		this.meteringResultCode = meteringResultCode;
	}

	public String getMeteringRegDate() {
		return meteringRegDate;
	}

	public void setMeteringRegDate(String meteringRegDate) {
		this.meteringRegDate = meteringRegDate;
	}

	public String getMeteringUpdateDate() {
		return meteringUpdateDate;
	}

	public void setMeteringUpdateDate(String meteringUpdateDate) {
		this.meteringUpdateDate = meteringUpdateDate;
	}

	public Double getGnrAverage() {
		return gnrAverage;
	}

	public void setGnrAverage(Double gnrAverage) {
		this.gnrAverage = gnrAverage;
	}

	public Double getNgtAverage() {
		return ngtAverage;
	}

	public void setNgtAverage(Double ngtAverage) {
		this.ngtAverage = ngtAverage;
	}

	public String getGenDate() {
		return genDate;
	}

	public void setGenDate(String genDate) {
		this.genDate = genDate;
	}

	public String getGenId() {
		return genId;
	}

	public void setGenId(String genId) {
		this.genId = genId;
	}

	public SGDG_XAM4() {
		this.id = new SGDG_XAM4Pk(); 
	}
	
	@Override
	public String toString() {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
	
	public String toJSONString() {
		String retValue = "";
		retValue += "{"
		+" contractId:" + id.getContractId() + ","
		+" billDate:" + id.getBillDate() + ","
		+" meteringFlag:" + id.getMeteringFlag() + ","
		+" meteringDate:" + meteringDate + ","
		+" realMeteringDate:" + realMeteringDate + ","
		+" gaugeNumber:" + gaugeNumber + ","
		+" maxLD:" + maxLD + "," 
		+" middleLD:" + middleLD + "," 
		+" middleLD2:" + middleLD2 + ","
		+" miniLD:" + miniLD + ","
		+" meteringMethodCode:" + meteringMethodCode + ","
		+" meteringResultCode:" + meteringResultCode + ","
		+" meteringRegDate:" + meteringRegDate + ","
		+" meterginUpdateDate:" + meteringUpdateDate + ","
		+" gnrAverage:" + gnrAverage + ","
		+" ngtAverage:" + ngtAverage + ","
		+" genDate:" + genDate + ","
		+" genId:" + genId + " }";
		return retValue;
	}
}
