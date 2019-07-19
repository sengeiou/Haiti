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
import com.aimir.model.BaseObject;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.Operator;
import com.aimir.model.system.PrepaymentLog;

/**
 * @FileName DepositHistory.java
 * @Date 2013. 6. 25.
 * @author khk
 * @ModifiedDate
 * @Descr 예치금 변경 이력
 */

@Entity
@Table(name="DEPOSIT_HISTORY")
public class DepositHistory extends BaseObject {

	private static final long serialVersionUID = 7578408308756076944L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DEPOSIT_HISTORY_SEQ")
	@SequenceGenerator(name="DEPOSIT_HISTORY_SEQ", sequenceName="DEPOSIT_HISTORY_SEQ", allocationSize=1)
	@ColumnInfo(name="History Index", descr="예치금 거래 내역 index")
	private Integer id;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operator_id")
	@ColumnInfo(name="operator", descr="vendor 즉 vending point")
	private Operator operator;

	@Column(name="operator_id", nullable=true, updatable=false, insertable=false)
	@ColumnInfo(name="operator id", descr="vendor id")
	private Integer operatorId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="loginUser_id")
	@ColumnInfo(name="loginUser", descr="chage한 사용자 정보")
	private Operator loginUser;

	@Column(name="loginUser_id", nullable=true, updatable=false, insertable=false)
	private Integer loginUserId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="customer_id")
	@ColumnInfo(name="customer", descr="고객")
	private Customer customer;
	
	@Column(name="customer_id", nullable=true, updatable=false, insertable=false)
	@ColumnInfo(name="customer id", descr="고객 id")
	private Integer customerId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="meter_id")
	@ColumnInfo(name="meter", descr="미터기")
	private Meter meter;
	
	@Column(name="meter_id", nullable=true, updatable=false, insertable=false)
	@ColumnInfo(name="meter id", descr="미터 id")
	private Integer meterId;
		
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="contract_id")
	@ColumnInfo(name="contract", descr="계약 정보")
	private Contract contract;
	
	@Column(name="contract_id", nullable=true, updatable=false, insertable=false)
	@ColumnInfo(name="contract id", descr="계약 id")
	private Integer contractId;
	
	@Column(name="change_date", length=14, nullable=true)
	@ColumnInfo(name="change date", descr="변경일")
	private String changeDate;
	
	@Column(name="charge_deposit", nullable=true)
	@ColumnInfo(name="charge deposit", descr="충전 예치금")
	private Double chargeDeposit;
	
	@Column(name="charge_credit", nullable=true)
	@ColumnInfo(name="charge credit", descr="고객 전용 충전 금액")
	private Double chargeCredit;
	
	@Column(name="deposit", columnDefinition="float default 0")
	@ColumnInfo(name="deposit", descr="거래 후 예치금")
	private Double deposit;
	
	@Column(name="commission")
	@ColumnInfo(name="commission", descr="commission")
	private Float commission;
	
	@Column(name="value")
	@ColumnInfo(name="value", descr="value")
	private Double value;
	
	@Column(name="taxRate")
	@ColumnInfo(name="taxRate", descr="세금율")
	private Float taxRate;
	
	@Column(name="tax")
	@ColumnInfo(name="tax", descr="세금")
	private Double tax;
	
	@Column(name="netValue")
	@ColumnInfo(name="netValue", descr="총금액")
	private Double netValue;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="prepaymentLog_id")
	@ColumnInfo(name="prepaymentLog", descr="고객 선불 결제 이력")
	private PrepaymentLog prepaymentLog;
	
	@Column(name="prepaymentLog_id", nullable=true, updatable=false, insertable=false)
	@ColumnInfo(name="prepaymentLog id", descr="고객 선불 결제 이력 id")
	private Long prepaymentLogId;	
	
	@ColumnInfo(descr="예치금 거래 취소 식별 FLAG")
	@Column(name="IS_CANCELED")
	private Boolean isCanceled;
	
	@ColumnInfo(descr="예치금 거래 취소 일(yyyymmddhhmmss) ")
	@Column(name="CANCEL_DATE")
	private String cancelDate;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlTransient
	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public Integer getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}
	
	@XmlTransient
	public Operator getLoginUser() {
		return loginUser;
	}

	public void setLoginUser(Operator loginUser) {
		this.loginUser = loginUser;
	}

	public Integer getLoginUserId() {
		return loginUserId;
	}

	public void setLoginUserId(Integer loginUserId) {
		this.loginUserId = loginUserId;
	}
	
	@XmlTransient
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Meter getMeter() {
		return meter;
	}

	public void setMeter(Meter meter) {
		this.meter = meter;
	}
	
	@XmlTransient
	public Integer getMeterId() {
		return meterId;
	}

	public void setMeterId(Integer meterId) {
		this.meterId = meterId;
	}

	@XmlTransient
	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public Integer getContractId() {
		return contractId;
	}

	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}

	public String getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(String changeDate) {
		this.changeDate = changeDate;
	}

	public Double getChargeDeposit() {
		return chargeDeposit;
	}

	public void setChargeDeposit(Double chargeDeposit) {
		this.chargeDeposit = chargeDeposit;
	}

	public Double getChargeCredit() {
		return chargeCredit;
	}

	public void setChargeCredit(Double chargeCredit) {
		this.chargeCredit = chargeCredit;
	}

	public Double getDeposit() {
		return deposit;
	}

	public void setDeposit(Double deposit) {
		this.deposit = deposit;
	}
	
	
	public Float getCommission() {
		return commission;
	}

	public void setCommission(Float commission) {
		this.commission = commission;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Float getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(Float taxRate) {
		this.taxRate = taxRate;
	}

	public Double getTax() {
		return tax;
	}

	public void setTax(Double tax) {
		this.tax = tax;
	}

	public Double getNetValue() {
		return netValue;
	}

	public void setNetValue(Double netValue) {
		this.netValue = netValue;
	}

	public PrepaymentLog getPrepaymentLog() {
		return prepaymentLog;
	}

	public void setPrepaymentLog(PrepaymentLog prepaymentLog) {
		this.prepaymentLog = prepaymentLog;
	}

	public Long getPrepaymentLogId() {
		return prepaymentLogId;
	}

	public void setPrepaymentLogId(Long prepaymentLogId) {
		this.prepaymentLogId = prepaymentLogId;
	}

	public Boolean getIsCanceled() {
		return isCanceled;
	}

	public void setIsCanceled(Boolean isCanceled) {
		this.isCanceled = isCanceled;
	}

	public String getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(String cancelDate) {
		this.cancelDate = cancelDate;
	}

	@Override
	public String toString() {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
}
