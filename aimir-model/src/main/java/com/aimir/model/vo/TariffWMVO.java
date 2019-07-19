package com.aimir.model.vo;

/**
 * Tariff (계약종별 요금(요율) 수도 기준 클래스)
 * @author YeonKyoung Park(goodjob)
 *
 */
public class TariffWMVO {
	
	private Integer id;

	private String yyyymmdd; 
	
	private String tariffType;	

	private Double supplySizeMin;
	
	private Double supplySizeMax;
	
	private String supplySizeUnit;

	private String condition1;

	private String condition2;
	
	private Double usageUnitPrice;
	
	private Double shareCost;
	
	public TariffWMVO() {		
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

	public Double getUsageUnitPrice() {
		return usageUnitPrice;
	}

	public void setUsageUnitPrice(Double usageUnitPrice) {
		this.usageUnitPrice = usageUnitPrice;
	}

	public Double getShareCost() {
		return shareCost;
	}

	public void setShareCost(Double shareCost) {
		this.shareCost = shareCost;
	}

	
}
