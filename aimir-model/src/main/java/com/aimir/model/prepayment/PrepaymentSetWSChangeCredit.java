/**
 * PrepaymentSetWSChangeCredit.java Copyright NuriTelecom Limited 2011
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
 * PrepaymentSetWSChangeCredit.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 27.  v1.0        문동규    Change Credit available 모델
 * 2016. 2. 23.  v1.1        lucky   Change sequence name. oracle maximum object name length: 30 
 *
 */

@Entity
@Table(name = "PREPAYMENT_WS_CHANGE_CREDIT")
public class PrepaymentSetWSChangeCredit extends BaseObject {

    private static final long serialVersionUID = -3156689093225085739L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PREPAYMENT_WS_CHANGE_CREDI_SEQ")
    @SequenceGenerator(name="PREPAYMENT_WS_CHANGE_CREDI_SEQ", sequenceName="PREPAYMENT_WS_CHANGE_CREDI_SEQ", allocationSize=1)
	private Integer id;
	
	@Column(name = "supplier_name", nullable=false)
	@ColumnInfo(name="Utility ID", descr="공급사 아이디")
	private String supplierName;

	@Column(name = "date_time", nullable=false)
	@ColumnInfo(name="Date & Time of request", descr="현재 날짜")
	private String dateTime;
	
    @Column(name = "contract_number", nullable=false)
    @ColumnInfo(name="Contract ID", descr="고객의 계약번호")
    private String contractNumber;

    @Column(name = "mds_id", nullable=false)
    @ColumnInfo(name="Meter Serial Number", descr="미터 시리얼 번호")
    private String mdsId;

    @Column(name = "change_credit_yn", nullable=false)
    @ColumnInfo(name="Y/N", descr="Change credit yes/no")
    private String changeCreditYn;

	@Column(name = "increment_yn", nullable=false)
	@ColumnInfo(name="Absolute/Incremental", descr="현재 금액에서 증가할 것인지 아니면 아예 값을 변경할 것인지")
	private String incrementYn;

	@Column(name = "credit", nullable=false)
	@ColumnInfo(name="Value", descr="금액")
	private Double credit;

	@Column(name = "encryption_key")
	@ColumnInfo(name="Encryption Key", descr="암호화 시 인증 키")
	private String encryptionKey;

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

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
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

	public String getChangeCreditYn() {
	    return changeCreditYn;
	}

	public void setChangeCreditYn(String changeCreditYn) {
	    this.changeCreditYn = changeCreditYn;
	}

	public String getIncrementYn() {
		return incrementYn;
	}

	public void setIncrementYn(String incrementYn) {
		this.incrementYn = incrementYn;
	}

	public Double getCredit() {
	    return credit;
	}

	public void setCredit(Double credit) {
	    this.credit = credit;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
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
