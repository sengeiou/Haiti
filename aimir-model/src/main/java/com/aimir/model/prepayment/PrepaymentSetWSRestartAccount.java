/**
 * PrepaymentSetWSRestartAccount.java Copyright NuriTelecom Limited 2011
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
 * PrepaymentSetWSRestartAccount.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 27.  v1.0        문동규    Restart Accounting 모델
 * 2016. 2. 23.  v1.1        lucky   Change sequence name. oracle maximum object name length: 30 
 *
 */

@Entity
@Table(name = "PREPAYMENT_WS_RESTART_ACCOUNT")
public class PrepaymentSetWSRestartAccount extends BaseObject {

    private static final long serialVersionUID = -2854975321692836254L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PREPAYMENT_WS_RESTART_ACCO_SEQ")
    @SequenceGenerator(name="PREPAYMENT_WS_RESTART_ACCO_SEQ", sequenceName="PREPAYMENT_WS_RESTART_ACCO_SEQ", allocationSize=1) 
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

    @Column(name = "restart_yn", nullable=false)
    @ColumnInfo(name="Y/N", descr="계정을 다시 시작할지 여부")
    private String restartYn;

	@Column(name = "zero_emergency_yn")
	@ColumnInfo(name="Zero credit & Em Cr Y/N", descr="잔액을 0으로 하고 Emergency Credit 여부 설정")
	private String zeroEmergencyYn;

	@Column(name = "rebilling_yn")
	@ColumnInfo(name="Restart Billing Y/N", descr="과금 재시작")
	private String rebillingYn;

	@Column(name = "zero_credit_yn")
	@ColumnInfo(name="Zero Debts Y/N", descr="잔액을 0으로 함")
	private String zeroCreditYn;

	@Column(name = "encryption_key", nullable=false)
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

	public String getRestartYn() {
	    return restartYn;
	}

	public void setRestartYn(String restartYn) {
	    this.restartYn = restartYn;
	}

	public String getZeroEmergencyYn() {
		return zeroEmergencyYn;
	}

	public void setZeroEmergencyYn(String zeroEmergencyYn) {
		this.zeroEmergencyYn = zeroEmergencyYn;
	}

	public String getRebillingYn() {
	    return rebillingYn;
	}

	public void setRebillingYn(String rebillingYn) {
	    this.rebillingYn = rebillingYn;
	}

	public String getZeroCreditYn() {
	    return zeroCreditYn;
	}

	public void setZeroCreditYn(String zeroCreditYn) {
	    this.zeroCreditYn = zeroCreditYn;
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
