package com.aimir.model.sgdg;

import com.aimir.model.BasePk;

public class SGDG_XAM4Pk extends BasePk {

	private static final long serialVersionUID = 802639626089625224L;
	
	String contractId;
	
	String billDate;
	
	String meteringFlag;

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getBillDate() {
		return billDate;
	}

	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	public String getMeteringFlag() {
		return meteringFlag;
	}

	public void setMeteringFlag(String meteringFlag) {
		this.meteringFlag = meteringFlag;
	}
}
