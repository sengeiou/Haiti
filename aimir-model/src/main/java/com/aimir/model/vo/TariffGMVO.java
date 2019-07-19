package com.aimir.model.vo;

/**
 * Tariff (계약종별 요금(요율) 가스 기준 클래스)
 * @author YeonKyoung Park(goodjob)
 *
 */
public class TariffGMVO {

	private Integer id;

	private String yyyymmdd; 
	
	private String tariffType;	
	
	private String season;	

	private Double basicRate;

	private Double usageUnitPrice;

	private Double adjustmentFactor;

	private Double salePrice;
	
	public TariffGMVO() {		
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
	
	public String getSeason() {
		return season;
	}
	public void setSeason(String season) {
		this.season = season;
	}

	public Double getBasicRate() {
		return basicRate;
	}

	public void setBasicRate(Double basicRate) {
		this.basicRate = basicRate;
	}

	public Double getUsageUnitPrice() {
		return usageUnitPrice;
	}

	public void setUsageUnitPrice(Double usageUnitPrice) {
		this.usageUnitPrice = usageUnitPrice;
	}

	public Double getAdjustmentFactor() {
		return adjustmentFactor;
	}

	public void setAdjustmentFactor(Double adjustmentFactor) {
		this.adjustmentFactor = adjustmentFactor;
	}

	public Double getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Double salePrice) {
		this.salePrice = salePrice;
	}	
}
