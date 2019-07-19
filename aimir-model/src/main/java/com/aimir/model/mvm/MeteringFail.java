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
import com.aimir.constants.CommonConstants.MeteringFailReason;
import com.aimir.model.BaseObject;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Location;


/**
 * 검침 실패한 미터들에 대한 정보를 일자별로 기록하는 클래스
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="METERING_FAIL")
public class MeteringFail extends BaseObject {

	private static final long serialVersionUID = 4112311151899106781L;
	
	@EmbeddedId public MeteringFailPk id;

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
	
	@Column(length=14)
	@ColumnInfo(name="데이터 작성시간")
	private String writeDate;
	
	@ColumnInfo(name="검침타입 0:정기검침, 1:온디맨드, 2:실패검침 ")
	private Integer meteringType;	
	
	@Column(name="device_id",length=20)
	@ColumnInfo(name="통신장비 아이디", descr="집중기아이디 혹은 모뎀아이디 수검침일경우 장비 아이디 없기 때문에 널 허용")
	private String deviceId;

    @ColumnInfo(name="지역아이디", descr="지역 테이블의 ID나  NULL")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="location_id")
    private Location location;
    
	@Column(name = "device_type")
	@ColumnInfo(name="장비타입", descr="집중기, 모뎀")
	private DeviceType deviceType;	
	
	@Column(name = "LAST_COMMDATE", length=14)
	@ColumnInfo(name="장비타입", descr="집중기, 모뎀")
	private String lastCommDate;
	
	@Column(name = "FAIL_REASON")
	@ColumnInfo(name="검침실패 이유", descr="집중기, 모뎀")
	private MeteringFailReason failReason;	
	
	
	public MeteringFail(){
		id = new MeteringFailPk();
	}

	public MeteringFailPk getId() {
		return id;
	}
	public void setId(MeteringFailPk id) {
		this.id = id;
	}
	
	public String getYyyymmdd() {
		return this.id.getYyyymmdd();
	}
	public void setYyyymmddhh(String yyyymmdd) {
		this.id.setYyyymmdd(yyyymmdd);
	}
	
	public DeviceType getMDevType() {
		return id.getMDevType();
	}
	
	public void setMDevType(String mdevType) {
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

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = DeviceType.valueOf(deviceType);
	}

	public String getLastCommDate() {
		return lastCommDate;
	}

	public void setLastCommDate(String lastCommDate) {
		this.lastCommDate = lastCommDate;
	}

	public MeteringFailReason getFailReason() {
		return failReason;
	}

	public void setFailReason(Integer failReason) {		
	    for (MeteringFailReason _failReason : MeteringFailReason.values()) {
	        if (_failReason.getCode() == failReason) {
	        	this.failReason = _failReason;
	        }
	    }
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
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }
}