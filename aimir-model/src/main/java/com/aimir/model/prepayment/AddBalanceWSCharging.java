/**
 * AddBalanceWSCharging.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.model.prepayment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Code;

/**
 * AddBalanceWSCharging.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 6. 28   v1.0        김상연   과금 충전 모델
 * 2011. 8. 12   v1.0        문동규   클래스명 수정
 *
 */

@Entity
@Table(name = "ADD_BALANCE_WS_CHARGING")
public class AddBalanceWSCharging extends BaseObject {

	private static final long serialVersionUID = -9082722391307249191L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ADD_BALANCE_WS_CHARGING_SEQ")
	@SequenceGenerator(name="ADD_BALANCE_WS_CHARGING_SEQ", sequenceName="ADD_BALANCE_WS_CHARGING_SEQ", allocationSize=1) 
	private Integer id;
	
//	@ManyToOne
//	@JoinColumn(name = "supplier_id", nullable = false)
//	@ReferencedBy(name = "name")
//	private Supplier supplier;

	@Column(name = "supplier_name")
	@ColumnInfo(name="Utility ID", descr="공급사 아이디")
	private String supplierName;

//	@Column(name = "date_time", nullable=false)
	@Column(name = "date_time")
	@ColumnInfo(name="Date & Time", descr="충전 날짜")
	private String dateTime;

//    @ManyToOne
//    @JoinColumn(name = "contract_id", nullable = false)
//    @ReferencedBy(name = "contractNumber")
//    private Contract contract;

    @Column(name = "contract_number", nullable=false)
    @ColumnInfo(name="Contract ID", descr="고객의 계약번호")
    private String contractNumber;

//	@OneToOne
//	@JoinColumn(name = "meter_id", nullable = false)
//	@ReferencedBy(name = "mdsId")
//	private Meter meter;

    @Column(name = "mds_id", nullable=false)
    @ColumnInfo(name="Meter Serial Number", descr="미터 시리얼 번호")
    private String mdsId;

	@Column(name = "account_id", nullable=false)
	@ColumnInfo(name="Account ID", descr="충전 아이디")
	private String accountId;
	
	@Column(name = "amount", nullable=false)
	@ColumnInfo(name="Amount", descr="충전액")
	private Double amount;
	
	@Column(name = "arrears")
	@ColumnInfo(name="Arrears", descr="충전 미수금")
	private Double arrears;
	
	//@Column(name = "power_limit", nullable=false)
	@Column(name = "power_limit")	
	@ColumnInfo(name="Power Limit(kWh)", descr="전력 사용량")
	private Double powerLimit;
	
//	@ManyToOne
//	@JoinColumn(name = "tariffIndex_id", nullable = false)
//	@ReferencedBy(name = "code")
//	private TariffType tariffIndex;


   //@Column(name = "tariff_code", nullable=false)
    @Column(name = "tariff_code")
    @ColumnInfo(name="Tariff Code", descr="과금 분류 코드(요금 코드)")
    private Integer tariffCode;

	@Column(name = "source")
	@ColumnInfo(name="source", descr="충전 방식(온라인, 카드)")
	private String source;

	@Column(name = "encryption_key")
	@ColumnInfo(name="Encryption Key", descr="암호화 시 인증 키")
	private String encryptionKey;

	@Column(name = "auth_code")
	@ColumnInfo(name="Authorization Code", descr="권한 코드")
	private String authCode;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "municipalityCode_id")
	@ReferencedBy(name="code")
	@ColumnInfo(name="Municipality Code", descr="지자체 코드")
	private Code municipalityCode;
	
	@Column(name="municipalityCode_id", nullable=true, updatable=false, insertable=false)
	private Integer municipalityCodeId;
	
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

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	@XmlTransient
	public Code getMunicipalityCode() {
		return municipalityCode;
	}

	public void setMunicipalityCode(Code municipalityCode) {
		this.municipalityCode = municipalityCode;
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

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getArrears() {
		return arrears;
	}

	public void setArrears(Double arrears) {
		this.arrears = arrears;
	}

	public Double getPowerLimit() {
		return powerLimit;
	}

	public void setPowerLimit(Double powerLimit) {
		this.powerLimit = powerLimit;
	}

	public Integer getTariffCode() {
		return tariffCode;
	}

	public void setTariffCode(Integer tariffCode) {
		this.tariffCode = tariffCode;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String encryptionKey) {
		this.encryptionKey = encryptionKey;
	}

	public Integer getMunicipalityCodeId() {
        return municipalityCodeId;
    }

    public void setMunicipalityCodeId(Integer municipalityCodeId) {
        this.municipalityCodeId = municipalityCodeId;
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
