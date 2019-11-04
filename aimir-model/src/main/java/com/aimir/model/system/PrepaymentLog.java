package com.aimir.model.system;

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

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.model.BaseObject;
import com.aimir.model.prepayment.VendorCasher;

/**
 * 선불내역
 *  - 선불정보는 Contract 테이블에 갱신되며, 갱신될때마다 PrepaymentLog에 기록된다.
 *  
 *  @author 강소이
 */
@Entity
@Table(name="PREPAYMENTLOG")
//@Indexes({
//        @Index(name="IDX_PREPAYMENTLOG_01", columnNames={"LASTTOKENDATE"}),
//        @Index(name="IDX_PREPAYMENTLOG_02", columnNames={"CONTRACT_ID", "LASTTOKENDATE"})
//})
public class PrepaymentLog extends BaseObject implements JSONString{

	private static final long serialVersionUID = 4218162564823781897L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_PREPAYMENTLOG")
	@SequenceGenerator(name="SEQ_PREPAYMENTLOG", sequenceName="SEQ_PREPAYMENTLOG", allocationSize=1) 
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="customer_id", nullable=false)
	@ReferencedBy(name="customerNo")
	private Customer customer;
	
	@Column(name="customer_id", nullable=true, updatable=false, insertable=false)
	private Integer customerId;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="vendorCasher_id", nullable=true)
	private VendorCasher vendorCasher;
	
	@Column(name="vendorCasher_id", nullable=true, updatable=false, insertable=false)
	private Integer vendorCasherId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="contract_id", nullable=false)
	@ReferencedBy(name="contractNumber")
	private Contract contract;
	
	@Column(name="contract_id", nullable=true, updatable=false, insertable=false)
	private Integer contractId;
	
	private String keyNum;			//지불이 선불일 경우 카드키넘버
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "keytype_id")
	@ReferencedBy(name="code")
	private Code keyType;			//지불=선불 카드타입
	
	@Column(name="keytype_id", nullable=true, updatable=false, insertable=false)
	private Integer keyTypeCodeId;
	
	private Double chargedCredit;		//충전한 금액

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operator_id", nullable=true)
	@ReferencedBy(name="loginId")
	private Operator operator;
	
	@Column(name="operator_id", nullable=true, updatable=false, insertable=false)
	private Integer operatorId;		// 결제자 접속 id
	
	@Column(length=14, nullable=false)
	@ColumnInfo(descr="마지막 충전한 시간")
	private String lastTokenDate;	//마지막 충전한 시간
	
	@Column(length=40)
	private String lastTokenId;		//충전 세션키
	private Integer emergencyCreditAvailable;	//
	
	private String descr;		//비고
	
	@Column(name = "power_limit")
	@ColumnInfo(name="Power Limit(kWh)", descr="전력 사용량")
	private Double powerLimit;
	
	@ColumnInfo(descr="충전전 잔액")
	@Column(name="PRE_BALANCE")
	private Double preBalance;
	
	@ColumnInfo(descr="충전후 총 잔액")
	private Double balance;
	
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
	
	@ColumnInfo(descr="사용한 요금")
	@Column(name = "used_Cost")
	private Double usedCost;
	
	@ColumnInfo(descr="사용한 사용량")
	@Column(name = "used_Consumption")
	private Double usedConsumption;
	
	@ColumnInfo(descr="결제전 미수금")
	@Column(name="PRE_ARREARS")
	private Double preArrears;
	
	@ColumnInfo(descr="결제후 미수금")
	@Column(name="ARREARS")
	private Double arrears;

	private Double chargedArrears;
	
	@ColumnInfo(descr="")
	@Column(name="INIT_CREDIT", columnDefinition="FLOAT default 0")
	private Double initCredit;
	
	@ColumnInfo(descr="월간 총사용 금액(ECG)")
	private Double monthlyTotalAmount;
	
	@ColumnInfo(descr="월간 총납부 금액(ECG)")
	private Double monthlyPaidAmount;
	
	@ColumnInfo(descr="월간 서비스 비용(ECG)")
	private Double monthlyServiceCharge;
	
	@ColumnInfo(descr="월간 공공제 세금(ECG)")
	private Double publicLevy;
	
	@ColumnInfo(descr="월간 정부 세금(ECG)")
	private Double govLevy;
	
	@ColumnInfo(descr="월간 vat(ECG)")
	private Double vat;
	
	@ColumnInfo(descr="월간 vat on subsidy(ECG)")
	private Double vatOnSubsidy;
	
	@ColumnInfo(descr="월간 subsidy(ECG)")
	private Double subsidy;
	
	@ColumnInfo(descr="월간 lifeline subsidy(ECG)")
	private Double lifeLineSubsidy;
	
	@ColumnInfo(descr="월간 추가 subsidy(ECG)")
	private Double additionalSubsidy;
	
	@ColumnInfo(descr="마지막 충전일로부터 경과일")
	@Column(name="DAYS_FROM_CHARGE")
	private Integer daysFromCharge;
	
	@ColumnInfo(descr="충전 당시 분할납부 상황 ")
	@Column(name="PARTPAYINFO")
	private String partpayInfo;
	
	@ColumnInfo(descr="거래 취소 식별 FLAG")
	@Column(name="IS_CANCELED")
	private Boolean isCanceled;
	
	@ColumnInfo(descr="거래 취소 일(yyyymmddhhmmss) ")
	@Column(name="CANCEL_DATE")
	private String cancelDate;
	
	@ColumnInfo(descr="최종 계산한 LP 시간")
	@Column(name="LAST_LP_TIME")
	private String lastLpTime;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "location_id")
	@ReferencedBy(name="name")
	private Location location;		//공급지역
	
	@Column(name="location_id", nullable=true, updatable=false, insertable=false)
	private Integer locationId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "tariffIndex_id")
	@ReferencedBy(name="code")
	private TariffType tariffIndex;		//계약종별entity
	
	@Column(name="tariffIndex_id", nullable=true, updatable=false, insertable=false)
	private Integer tariffIndexId;
	
	@ColumnInfo(name="pay_type_code", view=@Scope(create=true, read=true, update=true), descr="Code 17 타입")
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="pay_type_code")
    @ReferencedBy(name="code")
    private Code payType;	// cash or Check
	
	@Column(name="pay_type_code", nullable=true, updatable=false, insertable=false)
	private Integer payTypeCodeId;
	
	@ColumnInfo(descr="취소 사유")
	@Column(name="CANCEL_REASON")
	private String cancelReason;
	
	@ColumnInfo(descr="debtEnt의 참조키")
    @Column(name="DEBT_REF", length=50)
    private String debtRef;
	
	@ColumnInfo(descr="체크카드번호")
    @Column(name="CHEQUE_NO", length=25)
    private String chequeNo;
	
	@ColumnInfo(descr="bankOfficeCode")
    @Column(name="BANK_OFFICE_CODE")
    private Integer bankOfficeCode;
	
	@ColumnInfo(descr="utilityRelief")
	@Column(name="utilityRelief")
	private Double utilityRelief;
	    
	@Column(name="ACTIVE_ENERGY_IMPORT")
	private Double activeEnergyImport;
	    
	@Column(name="ACTIVE_ENERGY_EXPORT")
	private Double activeEnergyExport;
	
	public Double getUsedCost() {
		return usedCost;
	}
	public void setUsedCost(Double usedCost) {
		this.usedCost = usedCost;
	}
	public Double getUsedConsumption() {
		return usedConsumption;
	}
	public void setUsedConsumption(Double usedConsumption) {
		this.usedConsumption = usedConsumption;
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

	public Double getPreBalance() {
		return preBalance;
	}
	public void setPreBalance(Double preBalance) {
		this.preBalance = preBalance;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@XmlTransient
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	@XmlTransient
	public VendorCasher getVendorCasher() {
		return vendorCasher;
	}
	public void setVendorCasher(VendorCasher vendorCasher) {
		this.vendorCasher = vendorCasher;
	}
	@XmlTransient
	public Integer getVendorCasherId() {
		return vendorCasherId;
	}
	public void setVendorCasherId(Integer vendorCasherId) {
		this.vendorCasherId = vendorCasherId;
	}
	@XmlTransient
	public Contract getContract() {
		return contract;
	}
	public String getCancelReason() {
		return cancelReason;
	}
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	public String getKeyNum() {
		return keyNum;
	}
	public void setKeyNum(String keyNum) {
		this.keyNum = keyNum;
	}
	
	@XmlTransient
	public Code getKeyType() {
		return keyType;
	}
	public void setKeyType(Code keyType) {
		this.keyType = keyType;
	}
	public Double getChargedCredit() {
		return chargedCredit;
	}
	public void setChargedCredit(Double chargedCredit) {
		this.chargedCredit = chargedCredit;
	}
	public String getLastTokenDate() {
		return lastTokenDate;
	}
	public void setLastTokenDate(String lastTokenDate) {
		this.lastTokenDate = lastTokenDate;
	}
	public String getLastTokenId() {
		return lastTokenId;
	}
	public void setLastTokenId(String lastTokenId) {
		this.lastTokenId = lastTokenId;
	}
	public Integer getEmergencyCreditAvailable() {
		return emergencyCreditAvailable;
	}
	public void setEmergencyCreditAvailable(Integer emergencyCreditAvailable) {
		this.emergencyCreditAvailable = emergencyCreditAvailable;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	
	public Integer getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
    public Integer getContractId() {
        return contractId;
    }
    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }
    public Integer getKeyTypeCodeId() {
        return keyTypeCodeId;
    }
    public void setKeyTypeCodeId(Integer keyTypeCodeId) {
        this.keyTypeCodeId = keyTypeCodeId;
    }
    public Integer getMunicipalityCodeId() {
        return municipalityCodeId;
    }
    public void setMunicipalityCodeId(Integer municipalityCodeId) {
        this.municipalityCodeId = municipalityCodeId;
    }
    public Double getPreArrears() {
		return preArrears;
	}
	public void setPreArrears(Double preArrears) {
		this.preArrears = preArrears;
	}
	public Double getArrears() {
		return arrears;
	}
	public void setArrears(Double arrears) {
		this.arrears = arrears;
	}
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
	public Double getChargedArrears() {
		return chargedArrears;
	}
	public void setChargedArrears(Double chargedArrears) {
		this.chargedArrears = chargedArrears;
	}
	public String toString()
	{
	    return "PrepaymentLog "+toJSONString();
	}
	
	public void setPowerLimit(Double powerLimit) {
		this.powerLimit = powerLimit;
	}
	public Double getPowerLimit() {
		return powerLimit;
	}
	
	public Double getInitCredit() {
		return initCredit;
	}
	public void setInitCredit(Double initCredit) {
		this.initCredit = initCredit;
	}

	public Double getMonthlyTotalAmount() {
		return monthlyTotalAmount;
	}
	public void setMonthlyTotalAmount(Double monthlyTotalAmount) {
		this.monthlyTotalAmount = monthlyTotalAmount;
	}
	public Double getMonthlyPaidAmount() {
		return monthlyPaidAmount;
	}
	public void setMonthlyPaidAmount(Double monthlyPaidAmount) {
		this.monthlyPaidAmount = monthlyPaidAmount;
	}
	public Double getMonthlyServiceCharge() {
		return monthlyServiceCharge;
	}
	public void setMonthlyServiceCharge(Double monthlyServiceCharge) {
		this.monthlyServiceCharge = monthlyServiceCharge;
	}
	public Double getPublicLevy() {
		return publicLevy;
	}
	public void setPublicLevy(Double publicLevy) {
		this.publicLevy = publicLevy;
	}
	public Double getGovLevy() {
		return govLevy;
	}
	public void setGovLevy(Double govLevy) {
		this.govLevy = govLevy;
	}
	public Double getVat() {
		return vat;
	}
	public void setVat(Double vat) {
		this.vat = vat;
	}
	public Double getVatOnSubsidy() {
		return vatOnSubsidy;
	}
	public void setVatOnSubsidy(Double vatOnSubsidy) {
		this.vatOnSubsidy = vatOnSubsidy;
	}
	public Double getSubsidy() {
		return subsidy;
	}
	public void setSubsidy(Double subsidy) {
		this.subsidy = subsidy;
	}
	public Double getLifeLineSubsidy() {
		return lifeLineSubsidy;
	}
	public void setLifeLineSubsidy(Double lifeLineSubsidy) {
		this.lifeLineSubsidy = lifeLineSubsidy;
	}
	public Double getAdditionalSubsidy() {
		return additionalSubsidy;
	}
	public void setAdditionalSubsidy(Double additionalSubsidy) {
		this.additionalSubsidy = additionalSubsidy;
	}
	public Integer getDaysFromCharge() {
		return daysFromCharge;
	}
	public void setDaysFromCharge(Integer daysFromCharge) {
		this.daysFromCharge = daysFromCharge;
	}
	public String getPartpayInfo() {
		return partpayInfo;
	}
	public void setPartpayInfo(String partpayInfo) {
		this.partpayInfo = partpayInfo;
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
	
	public String getLastLpTime() {
        return lastLpTime;
    }
    public void setLastLpTime(String lastLpTime) {
        this.lastLpTime = lastLpTime;
    }
    @XmlTransient
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public Integer getLocationId() {
		return locationId;
	}
	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}
	@XmlTransient
	public TariffType getTariffIndex() {
		return tariffIndex;
	}
	public void setTariffIndex(TariffType tariffIndex) {
		this.tariffIndex = tariffIndex;
	}
	public Integer getTariffIndexId() {
		return tariffIndexId;
	}
	public void setTariffIndexId(Integer tariffIndexId) {
		this.tariffIndexId = tariffIndexId;
	}
	
	public Code getPayType() {
		return payType;
	}
	public void setPayType(Code payType) {
		this.payType = payType;
	}
	public Integer getPayTypeCodeId() {
		return payTypeCodeId;
	}
	public void setPayTypeCodeId(Integer payTypeCodeId) {
		this.payTypeCodeId = payTypeCodeId;
	}
	
	public String getDebtRef() {
        return debtRef;
    }
    public void setDebtRef(String debtRef) {
        this.debtRef = debtRef;
    }
    
    public String getChequeNo() {
        return chequeNo;
    }
    public void setChequeNo(String chequeNo) {
        this.chequeNo = chequeNo;
    }
    
    public Integer getBankOfficeCode() {
        return bankOfficeCode;
    }
    public void setBankOfficeCode(Integer bankOfficeCode) {
        this.bankOfficeCode = bankOfficeCode;
    }
    
    public Double getUtilityRelief() {
        return utilityRelief;
    }
    public void setUtilityRelief(Double utilityRelief) {
        this.utilityRelief = utilityRelief;
    }
    public Double getActiveEnergyImport() {
        return activeEnergyImport;
    }
    public void setActiveEnergyImport(Double activeEnergyImport) {
        this.activeEnergyImport = activeEnergyImport;
    }
    public Double getActiveEnergyExport() {
        return activeEnergyExport;
    }
    public void setActiveEnergyExport(Double activeEnergyExport) {
        this.activeEnergyExport = activeEnergyExport;
    }
    public String toJSONString() {

		String retValue = "";

	    retValue = "{"
	        + "id:'" + this.id 
	        + "',customer:'" + ((this.customer == null)? "null" : this.customer.getId()) 
	        + "',contract:'" + ((this.contract == null)? "null" : this.contract.getId()) 
		    + "',keyNum:'" + this.keyNum
		    + "',keyType:'" + ((this.keyType == null)? "null" : this.keyType.getId())
		    + "',chargedCredit:'" + this.chargedCredit
		    + "',lastTokenDate:'" + this.lastTokenDate
		    + "',lastTokenId:'" + this.lastTokenId
		    + "',emergencyCreditAvailable:'" + this.emergencyCreditAvailable
		    + "',descr:'" + this.descr
		    + "',balance:'" + this.balance
		    + "',authCode:'" + this.authCode
		    + "',municipalityCode:'" + this.municipalityCode
		    + "',lastLpTime:'" + this.lastLpTime
		    + "',location:'" + ((this.location == null)? "null" : this.location.getName()) 
		    + "',tariffType:'" + ((this.tariffIndex == null)? "null" : this.tariffIndex.getName())
		    + "',payType:'" + ((this.payType == null)? "null" : this.payType.getName())
		    + "',debtRef:'" + this.debtRef
		    + "',chequeNo:'" + this.chequeNo
		    + "',bankOfficeCode:'" + this.bankOfficeCode
		    + "',utilityRelief:'" + this.utilityRelief
            + "',activeEnergyImport:'" + this.activeEnergyImport
            + "',activeEnergyExport:'" + this.activeEnergyExport
	        + "'}";

	    return retValue;
	}
    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }
}
