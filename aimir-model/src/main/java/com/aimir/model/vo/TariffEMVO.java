package com.aimir.model.vo;

/**
 * Tariff (계약종별 요금(요율) 전기 기준 클래스)
 * @author YeonKyoung Park(goodjob)
 *
 */
public class TariffEMVO {

	private Integer id;

	private String yyyymmdd; 
	
	private String tariffType;	

	private Double supplySizeMin;

	private String condition1;
	
	private Double supplySizeMax;

	private String condition2;
	
	private String supplySizeUnit;
	
	private Double serviceCharge;

	private Double adminCharge;

	private Double transmissionNetworkCharge;

	private Double distributionNetworkCharge;
	
	private Double energyDemandCharge;
	
	private Double activeEnergyCharge;	

	private Double reactiveEnergyCharge;
		
	private Double ers;
	
	private Double rateRebalancingLevy;
	
	private String season;	
	
	private String peakType;
	
	public TariffEMVO() {		
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}			

	public String getYyyymmdd() {
		return yyyymmdd;
	}

	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}

	public String getTariffType() {
		return tariffType;
	}

	public void setTariffType(String tariffType) {
		this.tariffType = tariffType;
	}
	
	public Double getSupplySizeMin() {
		return supplySizeMin;
	}
	public void setSupplySizeMin(Double supplySizeMin) {
		this.supplySizeMin = supplySizeMin;
	}
	public String getCondition1() {
		return condition1;
	}
	public void setCondition1(String condition1) {
		this.condition1 = condition1;
	}
	public Double getSupplySizeMax() {
		return supplySizeMax;
	}
	public void setSupplySizeMax(Double supplySizeMax) {
		this.supplySizeMax = supplySizeMax;
	}
	public String getCondition2() {
		return condition2;
	}
	public void setCondition2(String condition2) {
		this.condition2 = condition2;
	}
	public String getSupplySizeUnit() {
		return supplySizeUnit;
	}
	public void setSupplySizeUnit(String supplySizeUnit) {
		this.supplySizeUnit = supplySizeUnit;
	}
	public Double getServiceCharge() {
		return serviceCharge;
	}
	public void setServiceCharge(Double serviceCharge) {
		this.serviceCharge = serviceCharge;
	}
	public Double getAdminCharge() {
		return adminCharge;
	}
	public void setAdminCharge(Double adminCharge) {
		this.adminCharge = adminCharge;
	}
	
	public Double getTransmissionNetworkCharge() {
		return transmissionNetworkCharge;
	}
	public void setTransmissionNetworkCharge(Double transmissionNetworkCharge) {
		this.transmissionNetworkCharge = transmissionNetworkCharge;
	}
	public Double getDistributionNetworkCharge() {
		return distributionNetworkCharge;
	}
	public void setDistributionNetworkCharge(Double distributionNetworkCharge) {
		this.distributionNetworkCharge = distributionNetworkCharge;
	}
	public Double getEnergyDemandCharge() {
		return energyDemandCharge;
	}
	public void setEnergyDemandCharge(Double energyDemandCharge) {
		this.energyDemandCharge = energyDemandCharge;
	}
	public Double getActiveEnergyCharge() {
		return activeEnergyCharge;
	}
	public void setActiveEnergyCharge(Double activeEnergyCharge) {
		this.activeEnergyCharge = activeEnergyCharge;
	}

	public Double getReactiveEnergyCharge() {
		return reactiveEnergyCharge;
	}
	public void setReactiveEnergyCharge(Double reactiveEnergyCharge) {
		this.reactiveEnergyCharge = reactiveEnergyCharge;
	}
		
	public Double getErs() {
		return ers;
	}
	public void setErs(Double ers) {
		this.ers = ers;
	}
	
	public Double getRateRebalancingLevy() {
		return rateRebalancingLevy;
	}
	public void setRateRebalancingLevy(Double rateRebalancingLevy) {
		this.rateRebalancingLevy = rateRebalancingLevy;
	}
	
	public String getSeason() {
		return season;
	}
	public void setSeason(String season) {
		this.season = season;
	}
	public String getPeakType() {
		return peakType;
	}
	public void setPeakType(String peakType) {
		this.peakType = peakType;
	}	
}
