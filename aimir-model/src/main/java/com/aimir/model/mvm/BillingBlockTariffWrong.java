package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Contract;

@Entity
@Table(name = "BILLING_BLOCK_TARIFF_WRONG")
public class BillingBlockTariffWrong {

	@EmbeddedId public BillingWrongPk id;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meter_id")
    @ColumnInfo(name="미터") 
    @ReferencedBy(name="mdsId")
    private Meter meter;
    
    @Column(name="meter_id", nullable=true, updatable=false, insertable=false)
    private Integer meterId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    @ColumnInfo(name="계약")
    @ReferencedBy(name="contractNumber")
    private Contract contract;
    
    @Column(name="contract_id", nullable=true, updatable=false, insertable=false)
    private Integer contractId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modem_id")
    @ColumnInfo(name="모뎀번호")
    @ReferencedBy(name="deviceSerial")
    private Modem modem;
    
    @Column(name="modem_id", nullable=true, updatable=false, insertable=false)
    private Integer modemId;
	
    @ColumnInfo(descr="일일정산을 계산하는 LP의 시간, LP_EM의 시간일수도 있으나 장시간 미통신 미터의 LP가 올라온다면 중간 LP의 연산 날짜 일 수 도 있음")
    private String yyyymmddhh;
    
    @ColumnInfo(descr="마지막 LP로 부터 계산된 누적 사용량")
    private double activeEnergy;
    
    @ColumnInfo(descr="LP의 value, Import")
    private double activeEnergyImport;
    
    @ColumnInfo(descr="LP의 value, Export")
    private double activeEnergyExport;
    
    @Column(name="prev_yyyymmddhh")
    @ColumnInfo(descr="이전 또는 계산된 일일정산 시간")
    private String prevyyyymmddhh;
    
    @Column(name="prev_activeenergy")
    @ColumnInfo(descr="이전 또는 계산된 일일정산 사용량")
    private double prevActiveEnergy;
    
    @Column(name="prev_activeenergyimport")
    @ColumnInfo(descr="이전 또는 계산된 일일정산 import value")
    private double prevActiveEnergyImport;
    
    @Column(name="prev_activeenergyexport")
    @ColumnInfo(descr="이전 또는 계산된 일일정산 export value")
    private double prevActiveEnergyExport;
    
    @ColumnInfo(descr="설명")
    private String descr;
        
    @Column(length=14)
    @ColumnInfo(name="데이터 작성시간")
    private String writeDate;
    
    @Column(length=14)
    @ColumnInfo(name="마지막 빌링 시간. 잘못된 정산인데 데이터가 있는 경우 추가적으로 저장하지 않는다 때문에 lastBillingDate 값을 업데이트 한다.")
    private String lastBillingDate;

    @ColumnInfo(name="빌링이 진행되지 않은 일자")
    private Integer intervalDay;
    
    @Column(length=14)
    @ColumnInfo(name="문제가 해결된 시간. 잘못된 정산이 해결되어 정상적으로 해결된 시간")
    private String complateDate;
    
	public BillingWrongPk getId() {
		return id;
	}

	public void setId(BillingWrongPk id) {
		this.id = id;
	}
	
	public void setCode(String code) {
		if(id == null)
			id = new BillingWrongPk();
			
		this.id.setCode(code);
	}
	
	public void setMDevId(String mdevId) {
		if(id == null)
			id = new BillingWrongPk();
		
		this.id.setMDevId(mdevId);
	}
		
	public void setYyyymmdd(String yyyymmdd) {
		if(id == null)
			id = new BillingWrongPk();
		
		this.id.setYyyymmdd(yyyymmdd);
	}

	public Meter getMeter() {
		return meter;
	}

	public void setMeter(Meter meter) {
		this.meter = meter;
	}

	public Integer getMeterId() {
		return meterId;
	}

	public void setMeterId(Integer meterId) {
		this.meterId = meterId;
	}

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

	public Modem getModem() {
		return modem;
	}

	public void setModem(Modem modem) {
		this.modem = modem;
	}

	public Integer getModemId() {
		return modemId;
	}

	public void setModemId(Integer modemId) {
		this.modemId = modemId;
	}

	public String getYyyymmddhh() {
		return yyyymmddhh;
	}

	public void setYyyymmddhh(String yyyymmddhh) {
		this.yyyymmddhh = yyyymmddhh;
	}

	public double getActiveEnergy() {
		return activeEnergy;
	}

	public void setActiveEnergy(double activeEnergy) {
		this.activeEnergy = activeEnergy;
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
	
	public String getPrevyyyymmddhh() {
		return prevyyyymmddhh;
	}

	public void setPrevyyyymmddhh(String prevyyyymmddhh) {
		this.prevyyyymmddhh = prevyyyymmddhh;
	}

	public double getPrevActiveEnergy() {
		return prevActiveEnergy;
	}

	public void setPrevActiveEnergy(double prevActiveEnergy) {
		this.prevActiveEnergy = prevActiveEnergy;
	}

	public double getPrevActiveEnergyImport() {
		return prevActiveEnergyImport;
	}

	public void setPrevActiveEnergyImport(double prevActiveEnergyImport) {
		this.prevActiveEnergyImport = prevActiveEnergyImport;
	}

	public double getPrevActiveEnergyExport() {
		return prevActiveEnergyExport;
	}

	public void setPrevActiveEnergyExport(double prevActiveEnergyExport) {
		this.prevActiveEnergyExport = prevActiveEnergyExport;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public String getLastBillingDate() {
		return lastBillingDate;
	}

	public void setLastBillingDate(String lastBillingDate) {
		this.lastBillingDate = lastBillingDate;
	}

	public Integer getIntervalDay() {
		return intervalDay;
	}

	public void setIntervalDay(Integer intervalDay) {
		this.intervalDay = intervalDay;
	}

	public String getComplateDate() {
		return complateDate;
	}

	public void setComplateDate(String complateDate) {
		this.complateDate = complateDate;
	}   
	
}
