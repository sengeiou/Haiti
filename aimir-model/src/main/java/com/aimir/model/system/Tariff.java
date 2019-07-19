package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;

/**
 * Tariff (계약종별 요금(요율) 추상 클래스)
 * 
 * 계약종별 요금표 클래스 
 * 추상 클래스 이며 전기 가스 수도 등 서비스 공급타입별로 상세 클래스로 분류된다. 
 * @author YeonKyoung Park(goodjob)
 *
 */
@MappedSuperclass
public abstract class Tariff{
	
	@Column(name="yyyymmdd")
	@ColumnInfo(descr="시행 날짜, 시행날짜에 따라 요금체계가 달라진다. ")
	private String yyyymmdd; 
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="tarifftype_id", nullable=false)
	@ReferencedBy(name="name")
	private TariffType tariffType;	
	
	@Column(name="tarifftype_id", nullable=true, updatable=false, insertable=false)
	private Integer tariffTypeId;

    public String getYyyymmdd() {
		return yyyymmdd;
	}
	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}
	
	@XmlTransient
	public TariffType getTariffType() {
		return tariffType;
	}
	public void setTariffType(TariffType tariffType) {
		this.tariffType = tariffType;
	}
}
