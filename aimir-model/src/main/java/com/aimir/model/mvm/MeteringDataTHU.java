package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

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
 * 원격검침 데이터의 메타데이터 
 *
 */

@MappedSuperclass
public abstract class MeteringDataTHU {	

	@EmbeddedId public MeteringDataTHUPk id;	

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="supplier_id", nullable=false)
	@ReferencedBy(name="name")
	private Supplier supplier;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "meter_id")
	@ColumnInfo(name="미터") 
	@ReferencedBy(name="mdsId")
	private Meter meter;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "contract_id")
    @ColumnInfo(name="계약")
    @ReferencedBy(name="contractNumber")
	private Contract contract;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "modem_id")
    @ColumnInfo(name="모뎀번호")
    @ReferencedBy(name="deviceSerial")
	private Modem modem;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "enddevice_id")
    @ColumnInfo(name="엔드 디바이스 ")
    @ReferencedBy(name="serialNumber")
	private EndDevice enddevice;
	
	@Column(length=8)
	@ColumnInfo(name="검침일자")
	private String yyyymmdd;
	
	@Column(length=6)
	@ColumnInfo(name="검침시각")
	private String hhmmss;
	
	@ColumnInfo(name="온도 검침값")
	private Double ctValue;
	
	@ColumnInfo(name="습도 검침값")
	private Double chValue;
	
	@ColumnInfo(name="밧데리 검침값")
	private Double cbValue;
	
	@ColumnInfo(name="주기")
	private Double period;
	
	@ColumnInfo(name="온도 보정값")
	private Double tu;
	
	@ColumnInfo(name="습도 보정값")
	private Double hu;
	
	@ColumnInfo(name="밧데리 보정값")
	private Double bu;
	
	@Column(length=14)
	@ColumnInfo(name="데이터 작성시간")
	private String writeDate;

	@ColumnInfo(name="검침타입 0:정기검침, 1:온디맨드, 2:실패검침 ")
	private Integer meteringType;
	
    @ColumnInfo(name="지역아이디", descr="지역 테이블의 ID나  NULL")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="location_id")
    private Location location;
    
	@Column(name="device_id",length=20)
	@ColumnInfo(name="통신장비 아이디", descr="집중기아이디 혹은 모뎀아이디 수검침일경우 장비 아이디 없기 때문에 널 허용")
	private String deviceId;

	@Column(name = "device_type")
	@ColumnInfo(name="장비타입", descr="집중기, 모뎀")
	private DeviceType deviceType;
	
	public MeteringDataTHU(){
		id = new MeteringDataTHUPk();
	}
	
	public MeteringDataTHUPk getId() {
		return id;
	}
	public void setId(MeteringDataTHUPk id) {
		this.id = id;
	}	
	
	public DeviceType getMDevType() {
		return id.getMDevType();
	}
	
	/*
	public void setMDevType(DeviceType mdevType) {
		this.id.setMDevType(mdevType);
	}
	*/
	
	public void setMDevType(Integer mdevType) {
		this.id.setMDevType(mdevType);
	}
	public String getMDevId() {
		return id.getMDevId();
	}
	public void setMDevId(String mdevId) {
		this.id.setMDevId(mdevId);
	}

	public Meter getMeter() {
		return meter;
	}
	public void setMeter(Meter meter) {
		this.meter = meter;
	}

	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	public Modem getModem() {
		return modem;
	}

	public void setModem(Modem modem) {
		this.modem = modem;
	}

	public EndDevice getEnddevice() {
		return enddevice;
	}

	public void setEnddevice(EndDevice enddevice) {
		this.enddevice = enddevice;
	}
	
	public String getYyyymmddhhmmss() {
		return this.id.getYyyymmddhhmmss();
	}
	public void setYyyymmddhhmmss(String yyyymmddhhmmss) {
		this.id.setYyyymmddhhmmss(yyyymmddhhmmss);
	}
	
	
	public String getYyyymmdd() {
		return yyyymmdd;
	}
	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}
	public String getHhmmss() {
		return hhmmss;
	}
	public void setHhmmss(String hhmmss) {
		this.hhmmss = hhmmss;
	}		
	
	public Integer getDst() {
		return id.getDst();
	}
	public void setDst(Integer dst) {
		this.id.setDst(dst);
	}
	
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}
	public Integer getMeteringType() {
		return meteringType;
	}
	public void setMeteringType(Integer meteringType) {
		this.meteringType = meteringType;
	}	
	public Double getCtValue() {
		return ctValue;
	}
	public void setCtValue(Double ctValue) {
		this.ctValue = ctValue;
	}
	
	public Double getChValue() {
		return chValue;
	}
	public void setChValue(Double chValue) {
		this.chValue = chValue;
	}
	
	public Double getCbValue() {
		return cbValue;
	}
	public void setCbValue(Double cbValue) {
		this.cbValue = cbValue;
	}
	
	public Double getPeriod() {
		return period;
	}
	public void setPeriod(Double period) {
		this.period = period;
	}
	
	public Double getTu() {
		return tu;
	}
	public void setTu(Double tu) {
		this.tu = tu;
	}
	
	public Double getHu() {
		return hu;
	}
	public void setHu(Double hu) {
		this.hu = hu;
	}
	
	public Double getBu() {
		return bu;
	}
	public void setBu(Double bu) {
		this.bu = bu;
	}

	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public DeviceType getDeviceType() {
		return deviceType;
	}
	
	/*
	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}
	*/
	
	public void setDeviceType(Integer deviceType) {
		if(DeviceType.Modem.getCode().equals(deviceType)){
			this.deviceType = DeviceType.Modem;
		}
		if(DeviceType.Meter.getCode().equals(deviceType)){
			this.deviceType = DeviceType.Meter;
		}
		if(DeviceType.EndDevice.getCode().equals(deviceType)){
			this.deviceType = DeviceType.EndDevice;
		}
	}
	   
    public void setLocation(Location location) {
        this.location = location;
    }
    public Location getLocation() {
        return location;
    }

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Supplier getSupplier() {
		return supplier;
	}

}
