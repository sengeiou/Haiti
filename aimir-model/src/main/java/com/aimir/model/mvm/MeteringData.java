package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <pre>
 * 원격검침 데이터의 메타데이터 
 * 검침 시점의 현재 검침값을 기록하는 클래스
 * 해당일의 검침실패 성공기준을 판단하기 위해 필요한 테이블 , 검침데이터 전송 시 마지막 누적값(지침 사용량) 정보를 가진다.  전기(EM) 가스(GM) 수도(WM) 로 클래스명을 구분한다.
 * </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 */

@MappedSuperclass
public abstract class MeteringData {	

	@EmbeddedId public MeteringDataPk id;
	
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
	
	@ColumnInfo(name="검침값")
	private Double value;	
	
	@ColumnInfo(name="channel1")
	private Double ch1;
	@ColumnInfo(name="channel2")
	private Double ch2;
	@ColumnInfo(name="channel3")
	private Double ch3;
	@ColumnInfo(name="channel4")
	private Double ch4;
	@ColumnInfo(name="channel5")
	private Double ch5;
	@ColumnInfo(name="channel6")
	private Double ch6;
	@ColumnInfo(name="channel7")
	private Double ch7;
	
	@Column(length=14)
	@ColumnInfo(name="데이터 작성시간")
	private String writeDate;

	@ColumnInfo(name="검침타입 0:정기검침, 1:온디맨드, 2:실패검침 ")
	private Integer meteringType;
	
    @ColumnInfo(name="지역아이디", descr="지역 테이블의 ID나  NULL")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="location_id")
    @ReferencedBy(name="name")
    private Location location;
    
	@Column(name="device_id",length=20)
	@ColumnInfo(name="통신장비 아이디", descr="집중기아이디 혹은 모뎀아이디 수검침일경우 장비 아이디 없기 때문에 널 허용")
	private String deviceId;

	@Column(name = "device_type")
	@Enumerated(EnumType.STRING)
	@ColumnInfo(name="장비타입", descr="집중기, 모뎀(고압모뎀은 모뎀이 직접 업로드)")
	private DeviceType deviceType;
	
	public MeteringData(){
		id = new MeteringDataPk();
	}
	
	public MeteringDataPk getId() {
		return id;
	}
	public void setId(MeteringDataPk id) {
		this.id = id;
	}	
	
	public DeviceType getMDevType() {
		return id.getMDevType();
	}

	public void setMDevType(String mdevType){
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
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}	

	public Double getCh1() {
		return ch1;
	}

	public void setCh1(Double ch1) {
		this.ch1 = ch1;
	}

	public Double getCh2() {
		return ch2;
	}

	public void setCh2(Double ch2) {
		this.ch2 = ch2;
	}

	public Double getCh3() {
		return ch3;
	}

	public void setCh3(Double ch3) {
		this.ch3 = ch3;
	}

	public Double getCh4() {
		return ch4;
	}

	public void setCh4(Double ch4) {
		this.ch4 = ch4;
	}

	public Double getCh5() {
		return ch5;
	}

	public void setCh5(Double ch5) {
		this.ch5 = ch5;
	}

	public Double getCh6() {
		return ch6;
	}

	public void setCh6(Double ch6) {
		this.ch6 = ch6;
	}

	public Double getCh7() {
		return ch7;
	}

	public void setCh7(Double ch7) {
		this.ch7 = ch7;
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
	public void setDeviceType(String deviceType) {
		this.deviceType = DeviceType.valueOf(deviceType);
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
