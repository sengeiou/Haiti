/**
 * PrepaymentSetWSChangeInfo.java Copyright NuriTelecom Limited 2011
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
 * PrepaymentSetWSChangeInfo.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 8. 4.   v1.0        문동규   Change Energy Utility Information 모델
 *
 */

@Entity
@Table(name = "PREPAYMENT_WS_CHANGE_INFO")
public class PrepaymentSetWSChangeInfo extends BaseObject {

    private static final long serialVersionUID = -3156689093225085739L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PREPAYMENT_WS_CHANGE_INFO_SEQ")
    @SequenceGenerator(name="PREPAYMENT_WS_CHANGE_INFO_SEQ", sequenceName="PREPAYMENT_WS_CHANGE_INFO_SEQ", allocationSize=1) 
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

    @Column(name = "change_yn")
    @ColumnInfo(name="Change Energy Utility ID Y/N", descr="Y면 공급사를 변경")
    private String changeYn;

    @Column(name = "new_supplier_name")
    @ColumnInfo(name="New Utility ID", descr="Utility ID - text string")
    private String newSupplierName;

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

	public String getChangeYn() {
	    return changeYn;
	}

	public void setChangeYn(String changeYn) {
	    this.changeYn = changeYn;
	}

	public String getNewSupplierName() {
	    return newSupplierName;
	}

	public void setNewSupplierName(String newSupplierName) {
	    this.newSupplierName = newSupplierName;
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
