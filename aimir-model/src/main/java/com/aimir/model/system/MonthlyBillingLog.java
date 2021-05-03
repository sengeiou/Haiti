package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * @FileName MonthlyBillingLog.java
 * @Date 2020. 6.25
 * @author SH LIM
 * @Descr 월정산 이력 로그
 * 
 * 
 * 2019.10.03~ 
 * levy1:GovLevy, levy2:PublicLevy
 * subsidy1:UtilityRelief, subsidy2:GovSubsidy, subsidy3:NewSubsidy, subsidy4:lifeline
 * 
 */

@Entity
@Table(name="MONTHLY_BILLING_LOG")
public class MonthlyBillingLog extends BaseObject {

	private static final long serialVersionUID = 4831438137971046848L;

	@EmbeddedId public MonthlyBillingLogPk id;
	
	@ColumnInfo(descr = "미터 시리얼 번호")
    @Column(name="MDS_ID")
    private String mdsId;
    
	@ColumnInfo(descr = "LOG 생성일")
    @Column(name="WRITE_DATE")
    private String writeDate;
	
	@ColumnInfo(descr = "월정산 전, Contract Balance")
    @Column(name="BEFORE_CREDIT")
    private Double beforeCredit;
    
	@ColumnInfo(descr = "월정산 후, Contract Balance")
    @Column(name="CURRENT_CREDIT")
    private Double currentCredit;
    
	@ColumnInfo(descr = "월 사용량(kWh)")
    @Column(name="USED_CONSUMPTION")
    private Double usedConsumption;
	
	@ColumnInfo(descr = "일일정산 총 지불금액")
    @Column(name="PAID_COST")
    private Double paidCost;
	
	@ColumnInfo(descr = "추가로 가감되는 세금")
    @Column(name="ADDITIONAL_COST")
    private Double additionalCost;
	
	@ColumnInfo(descr = "월정산 금액 (PAID_COST+ADDITIONAL_COST)")
    @Column(name="MONTHLY_COST")
    private Double monthlyCost;
	
    @Column(name="SERVICE_CHARGE")
    private Double serviceCharge;
	
    @Column(name="VAT")
    private Double vat;
	
	@ColumnInfo(descr = "과금 필드")
    @Column(name="LEVY1")
    private Double levy1;
	
	@ColumnInfo(descr = "과금 필드")
    @Column(name="LEVY2")
    private Double levy2;
	
	@ColumnInfo(descr = "과금 필드")
    @Column(name="LEVY3")
    private Double levy3;
	
	@ColumnInfo(descr = "과금 필드")
    @Column(name="LEVY4")
    private Double levy4;
	
	@ColumnInfo(descr = "과금 필드")
    @Column(name="LEVY5")
    private Double levy5;
	
	@ColumnInfo(descr = "보조금(구호금) 필드")
    @Column(name="SUBSIDY1")
    private Double subsidy1;
	
	@ColumnInfo(descr = "보조금(구호금) 필드")
    @Column(name="SUBSIDY2")
    private Double subsidy2;

	@ColumnInfo(descr = "보조금(구호금) 필드")
    @Column(name="SUBSIDY3")
    private Double subsidy3;
	
	@ColumnInfo(descr = "보조금(구호금) 필드")
    @Column(name="SUBSIDY4")
    private Double subsidy4;
	
	@ColumnInfo(descr = "보조금(구호금) 필드")
    @Column(name="SUBSIDY5")
    private Double subsidy5;
	
	@ColumnInfo(descr = "Tariff Type 정보")
    @Column(name="TARIFF_TYPE")
    private String tariffType;
	
	@ColumnInfo(descr = "과금필드, 보조금(구호금) 필드에 적용된 상세정보")
    @Column(name="DESCR")
    private String descr;
	
	@ColumnInfo(descr = "해당 월에 대한 미터 채널 1의 검침걊")
	@Column(name="ACTIVEENERGYIMPORT")
	private double activeEnergyImport;
	
	@ColumnInfo(descr = "해당 월에 대한 미터 채널 2의 검침걊")
	@Column(name="ACTIVEENERGYEXPORT")
	private double activeEnergyExport;
	
    public MonthlyBillingLog() {
        id = new MonthlyBillingLogPk();
    }
    
    public MonthlyBillingLogPk getId() {
        return id;
    }

    public void setId(MonthlyBillingLogPk id) {
        this.id = id;
    }

    public void setYyyymm(String yyyymm) {
        id.setYyyymm(yyyymm);
    }
    
    public String getYyyymm() {
        return id.getYyyymm();
    }
    
	public void setContractId(Integer contractId) {
		id.setContractId(contractId);
	}

	public Integer getContractId() {
		return id.getContractId();
	}

	public String getMdsId() {
		return mdsId;
	}

	public void setMdsId(String mdsId) {
		this.mdsId = mdsId;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public Double getBeforeCredit() {
		return beforeCredit;
	}

	public void setBeforeCredit(Double beforeCredit) {
		this.beforeCredit = beforeCredit;
	}

	public Double getCurrentCredit() {
		return currentCredit;
	}

	public void setCurrentCredit(Double currentCredit) {
		this.currentCredit = currentCredit;
	}

	public Double getUsedConsumption() {
		return usedConsumption;
	}

	public void setUsedConsumption(Double usedConsumption) {
		this.usedConsumption = usedConsumption;
	}

	public Double getPaidCost() {
		return paidCost;
	}

	public void setPaidCost(Double paidCost) {
		this.paidCost = paidCost;
	}

	public Double getAdditionalCost() {
		return additionalCost;
	}

	public void setAdditionalCost(Double additionalCost) {
		this.additionalCost = additionalCost;
	}

	public Double getMonthlyCost() {
		return monthlyCost;
	}

	public void setMonthlyCost(Double monthlyCost) {
		this.monthlyCost = monthlyCost;
	}

	public Double getServiceCharge() {
		return serviceCharge;
	}

	public void setServiceCharge(Double serviceCharge) {
		this.serviceCharge = serviceCharge;
	}

	public Double getVat() {
		return vat;
	}

	public void setVat(Double vat) {
		this.vat = vat;
	}
	
	public Double getLevy1() {
		return levy1;
	}

	public void setLevy1(Double levy1) {
		this.levy1 = levy1;
	}

	public Double getLevy2() {
		return levy2;
	}

	public void setLevy2(Double levy2) {
		this.levy2 = levy2;
	}

	public Double getLevy3() {
		return levy3;
	}

	public void setLevy3(Double levy3) {
		this.levy3 = levy3;
	}

	public Double getLevy4() {
		return levy4;
	}

	public void setLevy4(Double levy4) {
		this.levy4 = levy4;
	}

	public Double getLevy5() {
		return levy5;
	}

	public void setLevy5(Double levy5) {
		this.levy5 = levy5;
	}

	public Double getSubsidy1() {
		return subsidy1;
	}

	public void setSubsidy1(Double subsidy1) {
		this.subsidy1 = subsidy1;
	}

	public Double getSubsidy2() {
		return subsidy2;
	}

	public void setSubsidy2(Double subsidy2) {
		this.subsidy2 = subsidy2;
	}

	public Double getSubsidy3() {
		return subsidy3;
	}

	public void setSubsidy3(Double subsidy3) {
		this.subsidy3 = subsidy3;
	}

	public Double getSubsidy4() {
		return subsidy4;
	}

	public void setSubsidy4(Double subsidy4) {
		this.subsidy4 = subsidy4;
	}

	public Double getSubsidy5() {
		return subsidy5;
	}

	public void setSubsidy5(Double subsidy5) {
		this.subsidy5 = subsidy5;
	}

	public String getTariffType() {
		return tariffType;
	}

	public void setTariffType(String tariffType) {
		this.tariffType = tariffType;
	}
	
	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public double getActiveEnergyImport() {
		return activeEnergyImport;
	}

	public void setActiveEnergyImport(double activeEnergyImport) {
		this.activeEnergyImport = activeEnergyImport;
	}

	public double getActiveEnergyExport() {
		return activeEnergyExport;
	}

	public void setActiveEnergyExport(double activeEnergyExport) {
		this.activeEnergyExport = activeEnergyExport;
	}

	@Override
	public String toString() {
		return "MonthlyBillingLog " + toJSONString();
	}
    
	public String toJSONString() {
		StringBuffer str = new StringBuffer();

		str.append("{"
                + "' yyyymm:'" + this.id.getYyyymm()
                + "', contractId:'" + this.id.getContractId()
                + "', mdsId:'" + this.mdsId
                + "', writeDate:'" + this.writeDate
                + "', beforeCredit:'" + this.beforeCredit
                + "', currentCredit:'" + this.currentCredit
                + "', usedConsumption:'" + this.usedConsumption
                + "', paidCost:'" + this.paidCost
                + "', additionalCost:'" + this.additionalCost
                + "', monthlyCost:'" + this.monthlyCost
                + "', serviceCharge:'" + this.serviceCharge
                + "', vat:'" + this.vat
                + "', levy1:'" + this.levy1
                + "', levy2:'" + this.levy2
                + "', levy3:'" + this.levy3
                + "', levy4:'" + this.levy4
                + "', levy5:'" + this.levy5
                + "', subsidy1:'" + this.subsidy1
                + "', subsidy2:'" + this.subsidy2
                + "', subsidy3:'" + this.subsidy3
                + "', subsidy4:'" + this.subsidy4
                + "', subsidy5:'" + this.subsidy5
                + "', tariffType:'" + this.tariffType
                + "', descr:'" + this.descr
                + "', activeEnergyImport:'" + this.activeEnergyImport
                + "', activeEnergyExport:'" + this.activeEnergyExport                
                + "'}");

		return str.toString();
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
