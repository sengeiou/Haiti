/**
 * ChangeCreditTypeWSChange.java Copyright NuriTelecom Limited 2011
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
 * ChangeCreditTypeWSChange.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 21.  v1.0        문동규   고객의 과금방식 변경 모델
 *
 */

@Entity
@Table(name = "CHANGE_CREDIT_TYPE_WS")
public class ChangeCreditTypeWSChange extends BaseObject {

    private static final long serialVersionUID = -3156689093225085739L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CHANGE_CREDIT_TYPE_WS_SEQ")
    @SequenceGenerator(name="CHANGE_CREDIT_TYPE_WS_SEQ", sequenceName="CHANGE_CREDIT_TYPE_WS_SEQ", allocationSize=1) 
	private Integer id;
	
	@Column(name = "supplier_name", nullable=false)
	@ColumnInfo(name="Utility ID", descr="공급사 아이디")
	private String supplierName;

	@Column(name = "date_time", nullable=false)
	@ColumnInfo(name="Date & Time", descr="현재 날짜")
	private String dateTime;
	
    @Column(name = "contract_number", nullable=false)
    @ColumnInfo(name="Contract ID", descr="고객의 계약번호")
    private String contractNumber;

    @Column(name = "mds_id", nullable=false)
    @ColumnInfo(name="Meter Serial Number", descr="미터 시리얼 번호")
    private String mdsId;

    @Column(name = "apply_date_time", nullable=false)
    @ColumnInfo(name="Date & Time of implementation", descr="적용 날짜")
    private String applyDateTime;

	@Column(name = "payment_mode", nullable=false)
	@ColumnInfo(name="Payment Mode", descr="현재 과금 모드(선불, 후불)")
	private String paymentMode;

	@Column(name = "disconn_func", nullable=false)
	@ColumnInfo(name="Disconnection functionality", descr="enable, disable or no change")
	private String disconnFunc;

	@Column(name = "credit_display_func", nullable=false)
	@ColumnInfo(name="Credit display functionality", descr="enable, disable or no change")
	private String creditDisplayFunc;

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

	public String getApplyDateTime() {
	    return applyDateTime;
	}

	public void setApplyDateTime(String applyDateTime) {
	    this.applyDateTime = applyDateTime;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getDisconnFunc() {
	    return disconnFunc;
	}

	public void setDisconnFunc(String disconnFunc) {
	    this.disconnFunc = disconnFunc;
	}

	public String getCreditDisplayFunc() {
	    return creditDisplayFunc;
	}

	public void setCreditDisplayFunc(String creditDisplayFunc) {
	    this.creditDisplayFunc = creditDisplayFunc;
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
