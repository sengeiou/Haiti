package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;
/**
 * Block Tariff용 빌링 데이타 저장 객체
 * @author elevas
 */
@Entity
@Table(name = "BILLING_BLOCK_TARIFF")
@Indexes(value={
        @Index(name="IDX_BILLING_BLOCK_TARIFF_01", columnNames={"mdev_Type", "mdev_Id", "yyyymmdd"}),
        @Index(name="IDX_BILLING_BLOCK_TARIFF_02", columnNames={"CONTRACT_ID", "WRITEDATE"})})
public class BillingBlockTariff {
    @EmbeddedId public BillingPk id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="supplier_id", nullable=false)
    @ReferencedBy(name="name")
    private Supplier supplier;  
    
    @Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;
    
    @ColumnInfo(name="지역아이디", descr="지역 테이블의 ID나  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="location_id")
    @ReferencedBy(name="name")
    private Location location;
    
    @Column(name="location_id", nullable=true, updatable=false, insertable=false)
    private Integer locationId;
    
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enddevice_id")
    @ColumnInfo(name="엔드 디바이스 ")
    @ReferencedBy(name="serialNumber")
    private EndDevice enddevice;
    
    @Column(name="enddevice_id", nullable=true, updatable=false, insertable=false)
    private Integer endDeviceId;
    
    @Column(length=14)
    @ColumnInfo(name="데이터 작성시간")
    private String writeDate;
    
    @ColumnInfo(descr="누적요금")
    private Double accumulateBill;
    
    @ColumnInfo(descr="block 요금 계산을 위해 월단위 누적 사용량")
    private Double accumulateUsage;

    @ColumnInfo(descr="사용량")
    private Double usage;
    
    @ColumnInfo(descr="실제 사용요금 ")
    private Double bill;
    
    @ColumnInfo(descr="차감 후의 남은 balance")
    private Double balance;
    
    @ColumnInfo(descr="LP의 value, Import와 Export값의 합이 들어간다.")
    private double activeEnergy;
    
    @ColumnInfo(descr="LP의 value, Import")
    private double activeEnergyImport;
    
    @ColumnInfo(descr="LP의 value, Export")
    private double activeEnergyExport;
    
    @ColumnInfo(descr="LP의 reactive 합산")
    private double reactiveEnergy;
    
    @ColumnInfo(descr="LP의 value, Import")
    private double reactiveEnergyImport;
    
    @ColumnInfo(descr="LP의 value, Export")
    private double reactiveEnergyExport;
    
    public BillingBlockTariff() {
        id = new BillingPk();
    }
    
    public BillingPk getId() {
        return id;
    }

    public void setId(BillingPk id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

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

    public EndDevice getEnddevice() {
        return enddevice;
    }

    public void setEnddevice(EndDevice enddevice) {
        this.enddevice = enddevice;
    }

    public Integer getEndDeviceId() {
        return endDeviceId;
    }

    public void setEndDeviceId(Integer endDeviceId) {
        this.endDeviceId = endDeviceId;
    }

    public String getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }

    public Double getAccumulateBill() {
        return accumulateBill;
    }

    public void setAccumulateBill(Double accumulateBill) {
        this.accumulateBill = accumulateBill;
    }

    public Double getAccumulateUsage() {
        return accumulateUsage;
    }

    public void setAccumulateUsage(Double accumulateUsage) {
        this.accumulateUsage = accumulateUsage;
    }
    
    public DeviceType getMDevType() {
        return id.getMDevType();
    }
    
    public void setMDevType(String mdevType) {
        id.setMDevType(mdevType);
    }
    
    public String getMDevId() {
        return id.getMDevId();
    }
    
    public void setMDevId(String mdevId) {
        id.setMDevId(mdevId);
    }
    
    public String getYyyymmdd() {
        return id.getYyyymmdd();
    }
    
    public void setYyyymmdd(String yyyymmdd) {
        id.setYyyymmdd(yyyymmdd);
    }
    
    public String getHhmmss() {
        return id.getHhmmss();
    }
    
    public void setHhmmss(String hhmmss) {
        id.setHhmmss(hhmmss);
    }

    public Double getBill() {
        return bill;
    }

    public void setBill(Double bill) {
        this.bill = bill;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
    
    public Double getUsage() {
        return usage;
    }

    public void setUsage(Double usage) {
        this.usage = usage;
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

    public double getReactiveEnergyImport() {
        return reactiveEnergyImport;
    }

    public void setReactiveEnergyImport(double reactiveEnergyImport) {
        this.reactiveEnergyImport = reactiveEnergyImport;
    }

    public double getReactiveEnergyExport() {
        return reactiveEnergyExport;
    }

    public void setReactiveEnergyExport(double reactiveEnergyExport) {
        this.reactiveEnergyExport = reactiveEnergyExport;
    }

    public double getActiveEnergy() {
        return activeEnergy;
    }

    public void setActiveEnergy(double activeEnergy) {
        this.activeEnergy = activeEnergy;
    }

    public double getReactiveEnergy() {
        return reactiveEnergy;
    }

    public void setReativeEnergy(double reactiveEnergy) {
        this.reactiveEnergy = reactiveEnergy;
    }

    public String toJSONString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{");
        buf.append("mdevType:'" + this.id.getMDevType());
        buf.append("',mdevId:'" + this.id.getMDevId());
        buf.append("',yyyymmdd:'" + this.id.getYyyymmdd());
        buf.append("',hhmmss:'" + this.id.getHhmmss());
        buf.append("',accumulateBill:'" + this.getAccumulateBill());
        buf.append("',accumulateUsage:'" + this.getAccumulateUsage());
        buf.append("',activeEnergy:'" + this.getActiveEnergy());
        buf.append("',activeEnergyImport:'" + this.getActiveEnergyImport());
        buf.append("',activeEnergyExport:'" + this.getActiveEnergyExport());
        buf.append("',reactiveEnergy:'" + this.getReactiveEnergy());
        buf.append("',reactiveEnergyImport:'" + this.getReactiveEnergyImport());
        buf.append("',reactiveEnergyExport:'" + this.getReactiveEnergyExport());
        buf.append("',writeDate:'" + this.getWriteDate());
        buf.append("'}");
     
        return buf.toString();
    }
    
    public String toString() {
        return toJSONString();
    }
}
