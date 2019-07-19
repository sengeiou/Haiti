package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.model.BasePk;

/**
 * MeteringSLA class의 Primary Key 정보를 정의한 클래스
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Embeddable
public class MeteringSLAPk extends BasePk {

	private static final long serialVersionUID = -6537516512751335215L;

	@Column(name="supplier_id", nullable=false)
    private Integer supplierId;
	
	@Column(name="YYYYMMDD", length=8, nullable=false)
	private String yyyymmdd;

	public String getYyyymmdd() {
		return yyyymmdd;
	}
	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}
	
	public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

}