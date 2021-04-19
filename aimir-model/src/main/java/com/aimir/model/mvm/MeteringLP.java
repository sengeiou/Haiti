package com.aimir.model.mvm;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Contract;

import javax.persistence.*;

@MappedSuperclass
public abstract class MeteringLP {

	@EmbeddedId public LpPk id;	
	
	@Column(name="device_id",length=20)
	@ColumnInfo(name="통신장비 아이디", descr="집중기아이디 혹은 모뎀아이디 수검침일경우 장비 아이디 없기 때문에 널 허용")
	private String deviceId;
	
	@Column(name = "device_type")
	@Enumerated(EnumType.STRING)
	@ColumnInfo(name="장비타입", descr="집중기, 모뎀")
	private DeviceType deviceType;
	
	@Column(name="meteringtype", length= 1)
	@ColumnInfo(descr="검침타입 0:정기검침, 1:온디맨드, 2:실패검침  3 : 수검침")
	private Integer meteringType;	

	@Column(name="device_serial", nullable=true, updatable=false, insertable=false)
	@ColumnInfo(descr="모뎀 시리얼 번호")
	private String modemSerial;	
	
	@Column(name = "lp_status")
	@ColumnInfo(descr="energy profile에 함께 들어오는 profile status 항목을 저장")
	private String lpStatus;
	
	@Column(name="interval_yn", length=1)
	@ColumnInfo(descr="meter interval 간격으로 정상적으로 값이 들어왔다면 1, 비정상적인 시간에 들어왔다면  0")
	private Integer intervalYN;
	
	@Column(name = "value")
	@ColumnInfo(descr="검침값")
	private Double value;	
	
	@Column(name = "writedate", length = 14)
	@ColumnInfo(descr="데이터 작성시간")
	private String writeDate;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "contract_id")
    @ColumnInfo(name="계약")
	@ReferencedBy(name = "contractNumber")
	private Contract contract;	
	
	@Column(name="contract_id", nullable=true, updatable=false, insertable=false)
	private Integer contractId;
	
	@Column(name = "modem_time", length = 14)
	@ColumnInfo(descr="데이터 작성시간")
	private String modemTime;
	
	@Column(name = "dcu_time", length = 14)
	@ColumnInfo(descr="데이터 작성시간")
	private String dcuTime;
	
	@Transient
	private double lpFlag;
	
	@Transient
	private Meter meter;

	public MeteringLP(){
		id = new LpPk();
	}	
	
	public LpPk getId() {
		return id;
	}

	public void setId(LpPk id) {
		this.id = id;
	}

	public Meter getMeter() {
		return meter;
	}

	public void setMeter(Meter meter) {
		this.meter = meter;
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

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}
	
	public void setDeviceType(String deviceType) {
		this.deviceType = DeviceType.valueOf(deviceType);
	}

	public Integer getMeteringType() {
		return meteringType;
	}

	public void setMeteringType(MeteringType type) {
		this.meteringType = new Integer(type.getType());
	}
	
	public void setMeteringType(Integer meteringType) {
		this.meteringType = meteringType;
	}

	public Double getValue() {
		//return Double.parseDouble("%.6f", BigDecimal.valueOf(value).toPlainString());
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
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

	public String getModemTime() {
		return modemTime;
	}

	public void setModemTime(String modemTime) {
		this.modemTime = modemTime;
	}

	public String getDcuTime() {
		return dcuTime;
	}

	public void setDcuTime(String dcuTime) {
		this.dcuTime = dcuTime;
	}
	
	public double getLpFlag() {
		return lpFlag;
	}

	public void setLpFlag(double lpFlag) {
		this.lpFlag = lpFlag;
	}

	public void setMDevId(String mdevId) {
		this.id.setMDevId(mdevId);
	}
	
	public DeviceType getMDevType() {
		return id.getMDevType();
	}
	
	public void setMDevType(String mdevType){
		this.id.setMDevType(mdevType);
	}
	
	public String getYyyymmdd() {
		return id.getYyyymmddhhmiss().substring(0, 8);
	}
		
	public String getYyyymmddhh() {
		return id.getYyyymmddhhmiss().substring(0, 10);
	}
	
	public String getYyyymmddhhmm() {
		return id.getYyyymmddhhmiss().substring(0, 12);
	}
	
	public void setDate(String date) {
		if(date.length() == 10)
			this.id.setYyyymmddhhmiss(date+"0000");
		else if(date.length() == 12)
			this.id.setYyyymmddhhmiss(date+"00");
		else if(date.length() == 14)
			this.id.setYyyymmddhhmiss(date);
	}
		
	public String getHour() {
		return id.getYyyymmddhhmiss().substring(8, 10);
	}
	
	public String getMinute() {
		return id.getYyyymmddhhmiss().substring(10, 12);
	}
	
	public String getYyyymmddhhmiss() {
		return id.getYyyymmddhhmiss();
	}
		
	public String getMDevId() {
		return id.getMDevId();
	}
	
	public Integer getDst() {
		return id.getDst();
	}
	public void setDst(Integer dst) {
		this.id.setDst(dst);
	}
	
	public Integer getChannel() {
		return id.getChannel();
	}
	
	public void setChannel(Integer channel) {
		this.id.setChannel(channel);
	}
	
	public String getModemSerial() {
		return modemSerial;
	}

	public void setModemSerial(String modemSerial) {
		this.modemSerial = modemSerial;
	}
	
	public Integer getIntervalYN() {
		return intervalYN;
	}

	public void setIntervalYN(Integer intervalYN) {
		this.intervalYN = intervalYN;
	}

	public String getLpStatus() {
		return lpStatus;
	}

	public void setLpStatus(String lpStatus) {
		this.lpStatus = lpStatus;
	}
	
	public String getExternalTableValue() {
		StringBuilder builder = new StringBuilder();
		
		builder.append(id.getMDevId()).append("|");
		builder.append(id.getYyyymmddhhmiss()).append("|");
		builder.append(id.getChannel()).append("|");
		builder.append(id.getMDevType()).append("|");
		builder.append(getDst()).append("|");
		builder.append(getDeviceId()).append("|");
		builder.append(getDeviceType()).append("|");
		builder.append(getMeteringType()).append("|");
		
		if(modemSerial != null)
			builder.append(modemSerial).append("|");
		
		builder.append(getLpStatus()).append("|");
		builder.append(getIntervalYN()).append("|");
		builder.append(getValue()).append("|");
		builder.append(getWriteDate()).append("|");
		
		Integer conId = getContractId();
		if(conId == null)
			builder.append("|");
		else
			builder.append(getContractId()).append("|");
				
		builder.append(getModemTime()).append("|");
		builder.append(getDcuTime());
		builder.append("\n");
			
		return builder.toString();
		//return builder.toString().replaceAll("null", "");
	}
	
}
