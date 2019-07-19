package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Supplier;

/**
 * 검침 성공률에 대한 SLA (Service Level Agreement)
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="METERING_SLA")
public class MeteringSLA extends BaseObject {

	private static final long serialVersionUID = 99862709132603223L;

	@EmbeddedId public MeteringSLAPk id;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="supplier_id", nullable=false, updatable=false, insertable=false)
    @ReferencedBy(name="name")
    private Supplier supplier;
	
	@Column(name="TOTAL_INSTALLED_METERS")
	@ColumnInfo(name="전체 설치 미터 수")
	private Long totalInstalledMeters;
	
	@Column(name="COMM_PERMITTED_METERS")
	@ColumnInfo(name="한번이라도 통신 성공한 미터수")
	private Long commPermittedMeters;
	
	@Column(name="PERMITTED_METERS")
	@ColumnInfo(name="통신 성공한 미터수")
	private Long permittedMeters;
	
	@Column(name="TOTAL_GATHERED_METERS")
	@ColumnInfo(name="검침 성공한 미터수")
	private Long totalGatheredMeters;
	
	@Column(name="SLA_METERS")
	@ColumnInfo(name="SLA로 등록된 미터 혹은 설치 후 5일이상 검침된 미터 혹은 계약정보가 있는 미터")
	private Long slaMeters;
	
	@Column(name="delivered_meters")
	@ColumnInfo(name="연계시스템으로 검침정보가 전달된 미터")
	private Long deliveredMeters;
	
	@Column(name="success_rate")
	@ColumnInfo(name="검침 성공률")
	private Double successRate;

	public MeteringSLA(){
		id = new MeteringSLAPk();
	}

	public MeteringSLAPk getId() {
		return id;
	}
	public void setId(MeteringSLAPk id) {
		this.id = id;
	}
	
	public String getYyyymmdd() {
		return id.getYyyymmdd();
	}
	public void setYyyymmdd(String yyyymmdd) {
		id.setYyyymmdd(yyyymmdd);
	}

    public Long getTotalInstalledMeters() {
		return totalInstalledMeters;
	}

	public void setTotalInstalledMeters(Long totalInstalledMeters) {
		this.totalInstalledMeters = totalInstalledMeters;
	}

	public Long getCommPermittedMeters() {
		return commPermittedMeters;
	}

	public void setCommPermittedMeters(Long commPermittedMeters) {
		this.commPermittedMeters = commPermittedMeters;
	}

	public Long getPermittedMeters() {
		return permittedMeters;
	}

	public void setPermittedMeters(Long permittedMeters) {
		this.permittedMeters = permittedMeters;
	}

	public Long getTotalGatheredMeters() {
		return totalGatheredMeters;
	}

	public void setTotalGatheredMeters(Long totalGatheredMeters) {
		this.totalGatheredMeters = totalGatheredMeters;
	}

	public Long getSlaMeters() {
		return slaMeters;
	}

	public void setSlaMeters(Long slaMeters) {
		this.slaMeters = slaMeters;
	}

	public Long getDeliveredMeters() {
		return deliveredMeters;
	}

	public void setDeliveredMeters(Long deliveredMeters) {
		this.deliveredMeters = deliveredMeters;
	}

	public Double getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(Double successRate) {
		this.successRate = successRate;
	}

	@XmlTransient
    public Supplier getSupplier() {
        return supplier;
    }
    public void setSupplier(Supplier supplier) {
        id.setSupplierId(supplier.getId());
        this.supplier = supplier;
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