package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;

/**
 * Tariff (계약종별 요금(요율) 수도 기준 클래스)
 * Tariff 클래스를 상속 받아 수도 관련 요금표 관련 정보를 추가하였다.
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "TARIFF_WM")
public class TariffWM extends Tariff{

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TARIFF_WM_SEQ")
	@SequenceGenerator(name="TARIFF_WM_SEQ", sequenceName="TARIFF_WM_SEQ", allocationSize=1) 
	@ColumnInfo(name="PK", descr="PK")
	private Integer id;
		
	@Column(name="supply_size_min")
	@ColumnInfo(descr="공급 사이즈 최소 ")
	private Double supplySizeMin;
	
	@Column(name="condition1")
	@ColumnInfo(descr="공급 사이즈 최소에 대한 조건 ")
	private String condition1;
	
	@Column(name="supply_size_max")
	@ColumnInfo(descr="공급 사이즈 최대")
	private Double supplySizeMax;
	
	@Column(name="condition2")
	@ColumnInfo(descr="공급 사이즈 최대에 대한 조건 ")
	private String condition2;
	
	@Column(name="supply_size_unit")
	@ColumnInfo(descr="공급 사이즈 단위 전기의 경우 kva")
	private String supplySizeUnit;
	
	@ColumnInfo(descr="단가, 사용량에 따른 사용 단가")
	@Column(name="USAGE_UNIT_PRICE")
	private Double usageUnitPrice;
	
	@ColumnInfo(descr="물 이용 부담금 톤당 부담금 ")
	@Column(name="share_cost")
	private Double shareCost;
	
	public TariffWM() {		
	}
	public TariffWM(Integer id) {
		this.id = id;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
