package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.mvm.Season;


/**
 * Tariff (계약종별 요금(요율) 가스 기준 클래스)
 * Tariff 클래스를 상속 받아 가스 관련 요금표 관련 정보를 추가하였다.
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "TARIFF_GM")
public class TariffGM extends Tariff{

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TARIFF_GM_SEQ")
	@SequenceGenerator(name="TARIFF_GM_SEQ", sequenceName="TARIFF_GM_SEQ", allocationSize=1)
	@ColumnInfo(name="PK", descr="PK")
	private Integer id;
	
	@Column(name="BASIC_RATE")
	@ColumnInfo(descr="기본 요금")
	private Double basicRate;
	
	@Column(name="USAGE_UNIT_PRICE")
	@ColumnInfo(descr="단가, 사용량에 따른 사용 단가")
	private Double usageUnitPrice;

	@ColumnInfo(descr="보정계수(조정률)")
	@Column(name="ADJUSTMENT_FACTOR")
	private Double adjustmentFactor;
	
	@Column(name="SALE_PRICE")
	@ColumnInfo(descr="판매가")
	private Double salePrice;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "season_id")
	@ReferencedBy(name="name")
	private Season season;
	
	@Column(name="season_id", nullable=true, updatable=false, insertable=false)
	private Integer seasonId;
	
	public TariffGM() {		
	}
	public TariffGM(Integer id) {
		this.id = id;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public Double getSalePrice() {
		return salePrice;
	}
	public void setSalePrice(Double salePrice) {
		this.salePrice = salePrice;
	}
	
	@XmlTransient
	public Season getSeason() {
		return season;
	}
	public void setSeason(Season season) {
		this.season = season;
	}	

	public Double getAdjustmentFactor() {
		return adjustmentFactor;
	}
	public void setAdjustmentFactor(Double adjustmentFactor) {
		this.adjustmentFactor = adjustmentFactor;
	}
    public Integer getSeasonId() {
        return seasonId;
    }
    public void setSeasonId(Integer seasonId) {
        this.seasonId = seasonId;
    }
}
