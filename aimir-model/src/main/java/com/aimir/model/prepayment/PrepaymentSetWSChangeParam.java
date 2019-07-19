/**
 * PrepaymentSetWSChangeParam.java Copyright NuriTelecom Limited 2011
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
 * PrepaymentSetWSChangeParam.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 27.  v1.0        문동규   Change Emergency Credit parameter 모델
 *
 */

@Entity
@Table(name = "PREPAYMENT_WS_CHANGE_PARAM")
public class PrepaymentSetWSChangeParam extends BaseObject {

    private static final long serialVersionUID = -3156689093225085739L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PREPAYMENT_WS_CHANGE_PARAM_SEQ")
    @SequenceGenerator(name="PREPAYMENT_WS_CHANGE_PARAM_SEQ", sequenceName="PREPAYMENT_WS_CHANGE_PARAM_SEQ", allocationSize=1) 
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

    @Column(name = "emergency_yn", nullable=false)
    @ColumnInfo(name="Y/N", descr="Emergency Credit 할지 여부")
    private String emergencyYn;

	@Column(name = "emergency_auto_yn", nullable=false)
	@ColumnInfo(name="Auto/manual selection", descr="자동으로 전횐되게 할 것인지 수동으로 전환되게 할 것인지")
	private String emergencyAutoYn;

	@Column(name = "max_duration")
	@ColumnInfo(name="Maximum Emergency Credit available", descr="Emergency Credit 모드의 최대 기간 (날짜수)")
	private Integer maxDuration;

	@Column(name = "device_id")
	@ColumnInfo(name="Device ID", descr="인증 장비 아이디")
	private String deviceId;

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

	public String getEmergencyYn() {
	    return emergencyYn;
	}

	public void setEmergencyYn(String emergencyYn) {
	    this.emergencyYn = emergencyYn;
	}

	public String getEmergencyAutoYn() {
		return emergencyAutoYn;
	}

	public void setEmergencyAutoYn(String emergencyAutoYn) {
		this.emergencyAutoYn = emergencyAutoYn;
	}

	public Integer getMaxDuration() {
	    return maxDuration;
	}

	public void setMaxDuration(Integer maxDuration) {
	    this.maxDuration = maxDuration;
	}

	public String getDeviceId() {
	    return deviceId;
	}

	public void setDeviceId(String deviceId) {
	    this.deviceId = deviceId;
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
