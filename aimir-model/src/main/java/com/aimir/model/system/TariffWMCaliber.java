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
import com.aimir.model.BaseObject;


/**
 * <p>Tariff (계약종별 요금(요율) 수도 기준 미터사이즈(구경 mm) 별 기본요금 )</p>
 * 사용량 별 요금에 상관없이 수도 미터의 구경별로 기본 단가가 책정되며 <br>
 * 요금은 사이즈별 기본단가 + 사용량별 금액으로 계산됨<br>
 * 공급사별로 구경별 단가가 틀리므로 공급사 정보를 가짐<br>
 *
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "TARIFF_WM_CALIBER")
public class TariffWMCaliber extends BaseObject  {

	private static final long serialVersionUID = -4343917831220543584L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TARIFF_WM_CALIBER_SEQ")
	@SequenceGenerator(name="TARIFF_WM_CALIBER_SEQ", sequenceName="TARIFF_WM_CALIBER_SEQ", allocationSize=1) 
	@ColumnInfo(name="PK", descr="PK")
	private Integer id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="supplier_id", nullable=false)
	@ReferencedBy(name="name")
	private Supplier supplier;
	
	@Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
	private Integer supplierId;
		
	@ColumnInfo(descr="구경 (mm)")
	@Column(name="CALIBER", nullable=false, unique=true)
	private Double caliber;
	
	@Column(name="BASIC_RATE", nullable=false)
	@ColumnInfo(descr="기본 요금 (냉수)")
	private Double basicRate;
	
	@Column(name="BASIC_RATE_HOT")
	@ColumnInfo(descr="기본 요금 (온수)")
	private Double basicRateHot;
    
    @Column(name="WRITE_TIME", nullable=false, length=14)
    @ColumnInfo(name="변경일 서버시간")
    private String writeTime;

	public TariffWMCaliber() {		
	}
	public TariffWMCaliber(Integer id) {
		this.id = id;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}	

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	public Double getBasicRate() {
		return basicRate;
	}
	public void setBasicRate(Double basicRate) {
		this.basicRate = basicRate;
	}

	public Double getBasicRateHot() {
		return basicRateHot;
	}
	public void setBasicRateHot(Double basicRateHot) {
		this.basicRateHot = basicRateHot;
	}
	public Double getCaliber() {
		return caliber;
	}
	public void setCaliber(Double caliber) {
		this.caliber = caliber;
	}
	public String getWriteTime() {
		return writeTime;
	}
	public void setWriteTime(String writeTime) {
		this.writeTime = writeTime;
	}
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
