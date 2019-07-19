/**
 * SupplyControlWSInterrupt.java Copyright NuriTelecom Limited 2011
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
 * SupplyControlWSInterrupt.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 7. 20.  v1.0        문동규   공급 차단 모델
 * 2016. 2. 23.  v1.1        lucky   Change sequence name. oracle maximum object name length: 30 
 *
 */

@Entity
@Table(name = "SUPPLY_CONTROL_WS_INTERRUPT")
public class SupplyControlWSInterrupt extends BaseObject {

    private static final long serialVersionUID = -4946852875662153318L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SUPPLY_CONTROL_WS_INTERRUP_SEQ")
    @SequenceGenerator(name="SUPPLY_CONTROL_WS_INTERRUP_SEQ", sequenceName="SUPPLY_CONTROL_WS_INTERRUP_SEQ", allocationSize=1) 
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
    @ColumnInfo(name="Date & Time for interruption", descr="시작 날짜")
    private String applyDateTime;

	@Column(name = "power_delay", nullable=false)
	@ColumnInfo(name="Delay kWh", descr="차단에 도달하는 kWh")
	private Double powerDelay;
	
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

	public Double getPowerDelay() {
		return powerDelay;
	}

	public void setPowerDelay(Double powerDelay) {
		this.powerDelay = powerDelay;
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
