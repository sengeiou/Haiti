package com.aimir.service.mvm.bean;

public class CustomerInfo{

	private String customerName;
	private String customerNo;
    private String adress;
	private String mobileNo;
	private String telephoneNo;
    private String meterType;
    private String mcuNo;
    private String meterNo;
    private String lastTime;
    private String lastMeteringData;
    private String contractNo;
    
    private String tariffType;
    private String location;
    
    private Integer tariffId;
    
    
    public Integer getTariffId() {
		return tariffId;
	}
	public void setTariffId(Integer tariffId) {
		this.tariffId = tariffId;
	}
	public String getContractNo() {
        return contractNo;
    }
    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCustomerNo() {
        return customerNo;
    }
    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }
    public String getAdress() {
        return adress;
    }
    public void setAdress(String adress) {
        this.adress = adress;
    }
    public String getMobileNo() {
        return mobileNo;
    }
    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }
    public String getTelephoneNo() {
        return telephoneNo;
    }
    public void setTelephoneNo(String telephoneNo) {
        this.telephoneNo = telephoneNo;
    }
    public String getMeterType() {
        return meterType;
    }
    public void setMeterType(String meterType) {
        this.meterType = meterType;
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
    public String getLastTime() {
        return lastTime;
    }
    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }
    public String getLastMeteringData() {
        return lastMeteringData;
    }
    public void setLastMeteringData(String lastMeteringData) {
        this.lastMeteringData = lastMeteringData;
    }
    /**
     * @return the tariffType
     */
    public String getTariffType() {
        return tariffType;
    }
    /**
     * @param tariffType the tariffType to set
     */
    public void setTariffType(String tariffType) {
        this.tariffType = tariffType;
    }
    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }
    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }
		
}
	