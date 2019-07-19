package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <pre>
 * 가스,수도,열량(TOU를 적용한) 빌링 메타 데이터(전기 제외)
 * 과금 
 * 
 * 빌링사에서 정보가 오거나 공급사의 과금기준일로 스케줄을 실행하여 생성 
 * - 과금기준(일시) 
 * - 납기일 
 * - 사용량 
 * - 금액 
 * - 납부여부 
 * 
 *
 * Date          Version     Author   Description
 * -              V1.0       YeonKyoung Park(goodjob)         신규작성 
 * 2011. 4. 25.   v1.1       eunmiae  Co2배출량 추가          
 * </pre>
 */

@MappedSuperclass
public abstract class Billing {	

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

	@ColumnInfo(descr="사용량")
	@Column(name="consum_usage")
	private Double usage;
	
	@ColumnInfo(descr=" 누적 지침 사용량")
	private Double value;
	
	@ColumnInfo(descr="실제 사용요금 ")
	private Double bill;	
	
	@ColumnInfo(descr="Demand Response or 요금 목표 절갑에 따른 절감액 잔여 마일리지")
	private Double remainingmiles;	
	
	@ColumnInfo(descr="Demand Response or 요금 목표 절갑에 따른 절감액 신규축적 마일리지")
	private Double newMiles;
	
	@ColumnInfo(descr="Demand Response or 요금 목표 절갑에 따른 절감액 사용한 마일리지")
	private Double usingMiles;
	
	@ColumnInfo(descr="탄소 요금 절감에 따른 마일리지")
	private Double co2Miles;
	
	@ColumnInfo(descr="기타 할인요금 독립 유공자 등등")
	private Double discountedRates;
	
	@ColumnInfo(descr="TV 수신료 등의 부가 사용요금")
	private Double additionalCosts;	
	
	@Column(name = "SEND_RESULT", length=255)
	@ColumnInfo(name="sendResult", descr="외부 연계 시스템 에 전달하는 검침값 결과  전송성공 true, 실패false ")
	private Boolean sendResult;

	/* 2011. 4. 25 v1.1 Co2배출량 추가 ADD START eunmiae */
	@Column(name = "CO2_EMISSIONS", length=10)
	@ColumnInfo(name="co2Emissions", descr="Co2 배출량")	
	private Double co2Emissions;

	@Column(name = "USAGE_READ_FROM_DATE", length=14)
	@ColumnInfo(descr="빌링에 정보를 등록하기 위해서 사용량 정보를 읽은 시작 날짜(YYYYMMDDHHMMSS)")
	private String usageReadFromDate;

	@Column(name = "USAGE_READ_TO_DATE", length=14)
	@ColumnInfo(descr="빌링에 정보를 등록하기 위해서 사용량 정보를 읽은 종료 날짜(YYYYMMDDHHMMSS)")
	private String usageReadToDate;

	public String getUsageReadFromDate() {
		return usageReadFromDate;
	}

	public void setUsageReadFromDate(String usageReadFromDate) {
		this.usageReadFromDate = usageReadFromDate;
	}

	public String getUsageReadToDate() {
		return usageReadToDate;
	}

	public void setUsageReadToDate(String usageReadToDate) {
		this.usageReadToDate = usageReadToDate;
	}

	public Double getCo2Emissions() {
		return co2Emissions;
	}

	public void setCo2Emissions(Double co2Emissions) {
		this.co2Emissions = co2Emissions;
	}
	/* 2011. 4. 25 v1.1 Co2배출량  추가 ADD END eunmiae */

	public Billing(){
		id = new BillingPk();
	}

	public void setId(BillingPk id) {
		this.id = id;
	}
	
	public DeviceType getMDevType() {
		return id.getMDevType();
	}
//	public void setDeviceType(String mdevType) {
//		id.setMDevType(mdevType);
//	}
	
	public void setMDevType(String mdevType){
		this.id.setMDevType(mdevType);
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

	@XmlTransient
	public Meter getMeter() {
		return meter;
	}
	public void setMeter(Meter meter) {
		this.meter = meter;
	}

	@XmlTransient
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	@XmlTransient
	public Modem getModem() {
		return modem;
	}

	public void setModem(Modem modem) {
		this.modem = modem;
	}

	@XmlTransient
	public EndDevice getEnddevice() {
		return enddevice;
	}

	public void setEnddevice(EndDevice enddevice) {
		this.enddevice = enddevice;
	}
	
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public Double getUsage() {
		return usage;
	}

	public void setUsage(Double usage) {
		this.usage = usage;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Double getBill() {
		return bill;
	}

	public void setBill(Double bill) {
		this.bill = bill;
	}

	public Double getRemainingmiles() {
		return remainingmiles;
	}

	public void setRemainingmiles(Double remainingmiles) {
		this.remainingmiles = remainingmiles;
	}

	public Double getNewMiles() {
		return newMiles;
	}

	public void setNewMiles(Double newMiles) {
		this.newMiles = newMiles;
	}

	public Double getUsingMiles() {
		return usingMiles;
	}

	public void setUsingMiles(Double usingMiles) {
		this.usingMiles = usingMiles;
	}

	public Double getCo2Miles() {
		return co2Miles;
	}

	public void setCo2Miles(Double co2Miles) {
		this.co2Miles = co2Miles;
	}

	public Double getDiscountedRates() {
		return discountedRates;
	}

	public void setDiscountedRates(Double discountedRates) {
		this.discountedRates = discountedRates;
	}

	public Double getAdditionalCosts() {
		return additionalCosts;
	}

	public void setAdditionalCosts(Double additionalCosts) {
		this.additionalCosts = additionalCosts;
	}

	public Boolean getSendResult() {
		return sendResult;
	}

	public void setSendResult(Boolean sendResult) {
		this.sendResult = sendResult;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@XmlTransient
	public Location getLocation() {
		return location;
	}

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getMeterId() {
        return meterId;
    }

    public void setMeterId(Integer meterId) {
        this.meterId = meterId;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public Integer getModemId() {
        return modemId;
    }

    public void setModemId(Integer modemId) {
        this.modemId = modemId;
    }

    public Integer getEndDeviceId() {
        return endDeviceId;
    }

    public void setEndDeviceId(Integer endDeviceId) {
        this.endDeviceId = endDeviceId;
    }

}
