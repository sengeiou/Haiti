package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
 * 검침데이터 LoadProfile Data를 Daily 별 Summary과 월별(Monthly) Total로 분류
 * MONTH테이블은 전기(EM) 가스(GM) 수도(WM) 로 테이블 명을 구분한다. 
 * 월 테이블은 해당월의 매월 1일에서 31(말일)까지 시간별데이터, 총 사용량 등을 저장한다.
 * 
 * 월별 총 사용량, 매월1일의 지침에 대한 값을 가지고 있으며 
 * 해당월의 1일부터 마지막 날까지의 사용량 정보도 가지고 있다. 
 *  주기별,일별과 마찬가지로 추상클래스이며  
 * MonthXX 클래스 형태로 상속되어 해당 테이블들이 생성된다. 
 * 
 * </pre>
 * 
 * @author goodjob
 *
 */
@MappedSuperclass
public abstract class MeteringMonth {
	
	@EmbeddedId public MonthPk id;	

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="supplier_id", nullable=false)
	@ReferencedBy(name="name")
	private Supplier supplier;
	
	@Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
	private Integer supplierId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "meter_id")
	@ColumnInfo(name="미터") 
	@ReferencedBy(name="mdsId")
	private Meter meter;
	
	@Column(name="meter_id", nullable=true, updatable=false, insertable=false)
	private Integer meterId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "contract_id")
    @ColumnInfo(name="계약")
    @ReferencedBy(name="contractNumber")
	private Contract contract;
	
	@Column(name="contract_id", nullable=true, updatable=false, insertable=false)
	private Integer contractId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "modem_id")
    @ColumnInfo(name="모뎀번호")
    @ReferencedBy(name="deviceSerial")
	private Modem modem;
	
	@Column(name="modem_id", nullable=true, updatable=false, insertable=false)
	private Integer modemId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "enddevice_id")
    @ColumnInfo(name="엔드 디바이스 ")
    @ReferencedBy(name="serialNumber")
	private EndDevice enddevice;
	
	@Column(name="enddevice_id", nullable=true, updatable=false, insertable=false)
	private Integer endDeviceId;

	@Column(length=14)
	@ColumnInfo(name="데이터 작성시간")
	private String writeDate;
	
	@ColumnInfo(name="검침타입 0:정기검침, 1:온디맨드, 2:실패검침 ")
	private Integer meteringType;
	
	@Column(name="device_id",length=20)
	@ColumnInfo(name="통신장비 아이디", descr="집중기아이디 혹은 모뎀아이디 수검침일경우 장비 아이디 없기 때문에 널 허용")
	private String deviceId;
    
	@Column(name = "device_type")
	@Enumerated(EnumType.STRING)
	@ColumnInfo(name="장비타입", descr="집중기, 모뎀")
	private DeviceType deviceType;
	
	@ColumnInfo(name="검침시작값")
	private Double baseValue;
	
	@ColumnInfo(name="검침값")
	private Double value;	
	
	@ColumnInfo(name="미터의 마지막 char 값")
	private Integer mdev_id_last;
	
	@ColumnInfo(name="count value, Day 에서 합쳐진 개수")
	private Integer c_value;
	
	@ColumnInfo(name="채널별 정의 메소드")
	private String ch_method;
	
	public MeteringMonth(){
		id = new MonthPk();
	}
	
	public MonthPk getId() {
		return id;
	}
	public void setId(MonthPk id) {
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
	
	public String getYyyymm() {
		return this.id.getYyyymm();
	}
	public void setYyyymm(String yyyymm) {
		this.id.setYyyymm(yyyymm);
	}
	
	public Integer getDst() {
		return id.getDst();
	}
	public void setDst(Integer dst) {
		this.id.setDst(dst);
	}
	
	public Integer getChannel() {
		return this.id.getChannel();
	}
	public void setChannel(Integer channel) {
		this.id.setChannel(channel);
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
	

	public Double getBaseValue() {
		return baseValue;
	}
	public void setBaseValue(Double baseValue) {
		this.baseValue = baseValue;
	}
	
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
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

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public Integer getMdev_id_last() {
		return mdev_id_last;
	}

	public void setMdev_id_last(Integer mdev_id_last) {
		this.mdev_id_last = mdev_id_last;
	}

	public Integer getC_value() {
		return c_value;
	}

	public void setC_value(Integer c_value) {
		this.c_value = c_value;
	}

	public String getCh_method() {
		return ch_method;
	}

	public void setCh_method(String ch_method) {
		this.ch_method = ch_method;
	}
}
