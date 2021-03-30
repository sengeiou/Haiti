package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.model.BasePk;

/**
 * @FileName MonthlyBillingLog.java
 * @Date 2020. 6.25
 * @author SH LIM
 * @Descr 월정산 이력 로그 테이블 PK Class
 */

@Embeddable
public class MonthlyBillingLogPk extends BasePk {

	private static final long serialVersionUID = 4391653983694163696L;

	@Column(name="YYYYMM", nullable=false)
    private String yyyymm;
    
    @Column(name="CONTRACT_ID", nullable=false)
	private Integer contractId;	
    
    
	public String getYyyymm() {
		return yyyymm;
	}

	public void setYyyymm(String yyyymm) {
		this.yyyymm = yyyymm;
	}

	public Integer getContractId() {
		return contractId;
	}

	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}


}