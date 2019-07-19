package com.aimir.service.mvm.bean;

public class MeteringListData{

	private String checked;
    private int no;
	
    private String customerNo;
	private String customerName;
	private String meteringTime;
	private String meteringData;
	private String beforData;
	private String co2;
	private String mcuNo;
	private String meterNo;
	private String locationName;
	private String detailView;
	private String contractNo;  
	private String friendlyName;
	private String meterType;
	private String baseValue;
	
	private boolean isManual;
	
	public String getChecked() {
        return checked;
    }
    public void setChecked(String checked) {
        this.checked = checked;
    }
    public int getNo() {
        return no;
    }
    public void setNo(int no) {
        this.no = no;
    }
    public String getCustomerNo() {
        return customerNo;
    }
    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getMeteringTime() {
        return meteringTime;
    }
    public void setMeteringTime(String meteringTime) {
        this.meteringTime = meteringTime;
    }
    public String getMeteringData() {
        return meteringData;
    }
    public void setMeteringData(String meteringData) {
        this.meteringData = meteringData;
    }
    public String getBeforData() {
        return beforData;
    }
    public void setBeforData(String beforData) {
        this.beforData = beforData;
    }
    public String getCo2() {
        return co2;
    }
    public void setCo2(String co2) {
        this.co2 = co2;
    }
    public String getMcuNo() {
        return mcuNo;
    }
    public void setMcuNo(String mcuNo) {
        this.mcuNo = mcuNo;
    }
    public String getMeterNo() {
        return meterNo;
    }
    public void setMeterNo(String meterNo) {
        this.meterNo = meterNo;
    }
    public String getLocationName() {
        return locationName;
    }
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    public String getDetailView() {
        return detailView;
    }
    public void setDetailView(String detailView) {
        this.detailView = detailView;
    }
    
    public String getContractNo() {
		return contractNo;
	}
	
	public void setcontractNo(String contractNo) {
        this.contractNo = contractNo;
    }
	public String getFriendlyName() {
		return friendlyName;
	}
	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}
	public String getMeterType() {
		return meterType;
	}
	public void setMeterType(String meterType) {
		this.meterType = meterType;
	}
	public boolean getIsManual() {
		return isManual;
	}
	public void setIsManual(boolean isManual) {
		this.isManual = isManual;
	}
	public String getBaseValue() {
		return baseValue;
	}
	public void setBaseValue(String baseValue) {
		this.baseValue = baseValue;
	}
}
	