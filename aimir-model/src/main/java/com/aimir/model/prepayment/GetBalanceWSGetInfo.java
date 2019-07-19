/**
 * GetBalanceWSGetInfo.java Copyright NuriTelecom Limited 2011
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
 * GetBalanceWSGetInfo.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 22.  v1.0        문동규   현재 잔액 정보 요청 모델
 *
 */

@Entity
@Table(name = "GET_BALANCE_WS_GET_INFO")
public class GetBalanceWSGetInfo extends BaseObject {

    private static final long serialVersionUID = -3678158342335488540L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="GET_BALANCE_WS_GET_INFO_SEQ")
    @SequenceGenerator(name="GET_BALANCE_WS_GET_INFO_SEQ", sequenceName="GET_BALANCE_WS_GET_INFO_SEQ", allocationSize=1) 
	private Integer id;
	
	@Column(name = "supplier_name", nullable=false)
	@ColumnInfo(name="Utility ID", descr="공급사 아이디")
	private String supplierName;

    @Column(name = "contract_number", nullable=false)
    @ColumnInfo(name="Contract ID", descr="고객의 계약번호")
    private String contractNumber;

    @Column(name = "mds_id", nullable=false)
    @ColumnInfo(name="Meter Serial Number", descr="미터 시리얼 번호")
    private String mdsId;

	@Column(name = "transaction_id")
	@ColumnInfo(name="Transaction ID", descr="처리 아이디")
	private String transactionId;
	
	@Column(name = "write_date")
	@ColumnInfo(name="Write Date", descr="처리 일자")
	private String writeDate;
	
	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

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

	public String getMdsId() {
		return mdsId;
	}

	public void setMdsId(String mdsId) {
		this.mdsId = mdsId;
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
