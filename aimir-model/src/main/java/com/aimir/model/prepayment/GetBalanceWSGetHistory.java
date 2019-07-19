/**
 * GetBalanceWSGetHistory.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.model.prepayment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * GetBalanceWSGetHistory.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 22.  v1.0        문동규   잔액 충전 내역 조회 모델
 *
 */

@Entity
@Table(name = "GET_BALANCE_WS_GET_HISTORY")
public class GetBalanceWSGetHistory extends BaseObject {

    private static final long serialVersionUID = 223056115028460372L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="GET_BALANCE_WS_GET_HISTORY_SEQ")
    @SequenceGenerator(name="GET_BALANCE_WS_GET_HISTORY_SEQ", sequenceName="GET_BALANCE_WS_GET_HISTORY_SEQ", allocationSize=1) 
	private Integer id;
	
	@Column(name = "supplier_name", nullable=false)
	@ColumnInfo(name="Utility ID", descr="공급사 아이디")
	private String supplierName;

    @Column(name = "contract_number", nullable=false)
    @ColumnInfo(name="Contract ID", descr="고객의 계약번호")
    private String contractNumber;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	@Override
	public boolean equals(Object o) {
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return null;
	}
}
