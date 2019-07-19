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

@MappedSuperclass
public abstract class MeteringLPTHU {
	
	@EmbeddedId public LpPk id;	

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
	
	@Column(length=8)
	@ColumnInfo(name="검침일자")
	private String yyyymmdd;
	
	@Column(name="hh", length=2)
	@ColumnInfo(name="검침 시")
	private String hour;
	
	@Column(length=14)
	@ColumnInfo(descr="데이터 작성시간")
	private String writeDate;
	
	@ColumnInfo(descr="검침타입 0:정기검침, 1:온디맨드, 2:실패검침 ")
	private Integer meteringType;
	
	@Column(name="device_id",length=20)
	@ColumnInfo(name="통신장비 아이디", descr="집중기아이디 혹은 모뎀아이디 수검침일경우 장비 아이디 없기 때문에 널 허용")
	private String deviceId;
	
    @ColumnInfo(name="지역아이디", descr="지역 테이블의 ID나  NULL")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="location_id")
    private Location location;
    
    @Column(name="location_id", nullable=true, updatable=false, insertable=false)
    private Integer locationId;

	@Column(name = "device_type")
	@ColumnInfo(name="장비타입", descr="집중기, 모뎀")
	private DeviceType deviceType;
	

	@ColumnInfo(name="검침값", descr="00~01(mm)")
	private Double value_00;	
	@ColumnInfo(name="검침값", descr="01~02(mm)")
	private Double value_01;
	@ColumnInfo(name="검침값", descr="02~03(mm)")
	private Double value_02;
	@ColumnInfo(name="검침값", descr="03~04(mm)")
	private Double value_03;
	@ColumnInfo(name="검침값", descr="04~05(mm)")
	private Double value_04;
	@ColumnInfo(name="검침값", descr="05~06(mm)")
	private Double value_05;
	@ColumnInfo(name="검침값", descr="06~07(mm)")
	private Double value_06;
	@ColumnInfo(name="검침값", descr="07~08(mm)")
	private Double value_07;
	@ColumnInfo(name="검침값", descr="08~09(mm)")
	private Double value_08;
	@ColumnInfo(name="검침값", descr="09~10(mm)")
	private Double value_09;
	@ColumnInfo(name="검침값", descr="10~11(mm)")
	private Double value_10;
	@ColumnInfo(name="검침값", descr="11~12(mm)")
	private Double value_11;
	@ColumnInfo(name="검침값", descr="12~13(mm)")
	private Double value_12;
	@ColumnInfo(name="검침값", descr="13~14(mm)")
	private Double value_13;
	@ColumnInfo(name="검침값", descr="14~15(mm)")
	private Double value_14;
	@ColumnInfo(name="검침값", descr="15~16(mm)")
	private Double value_15;
	@ColumnInfo(name="검침값", descr="16~17(mm)")
	private Double value_16;
	@ColumnInfo(name="검침값", descr="17~18(mm)")
	private Double value_17;
	@ColumnInfo(name="검침값", descr="18~19(mm)")
	private Double value_18;
	@ColumnInfo(name="검침값", descr="19~20(mm)")
	private Double value_19;
	@ColumnInfo(name="검침값", descr="20~21(mm)")
	private Double value_20;
	@ColumnInfo(name="검침값", descr="21~22(mm)")
	private Double value_21;
	@ColumnInfo(name="검침값", descr="22~23(mm)")
	private Double value_22;
	@ColumnInfo(name="검침값", descr="23~24(mm)")
	private Double value_23;
	@ColumnInfo(name="검침값", descr="24~25(mm)")
	private Double value_24;
	@ColumnInfo(name="검침값", descr="25~26(mm)")
	private Double value_25;
	@ColumnInfo(name="검침값", descr="26~27(mm)")
	private Double value_26;
	@ColumnInfo(name="검침값", descr="27~28(mm)")
	private Double value_27;
	@ColumnInfo(name="검침값", descr="28~29(mm)")
	private Double value_28;
	@ColumnInfo(name="검침값", descr="29~30(mm)")
	private Double value_29;
	@ColumnInfo(name="검침값", descr="30~31(mm)")
	private Double value_30;
	@ColumnInfo(name="검침값", descr="31~32(mm)")
	private Double value_31;
	@ColumnInfo(name="검침값", descr="32~33(mm)")
	private Double value_32;
	@ColumnInfo(name="검침값", descr="33~34(mm)")
	private Double value_33;
	@ColumnInfo(name="검침값", descr="34~35(mm)")
	private Double value_34;
	@ColumnInfo(name="검침값", descr="35~36(mm)")
	private Double value_35;
	@ColumnInfo(name="검침값", descr="36~37(mm)")
	private Double value_36;
	@ColumnInfo(name="검침값", descr="37~38(mm)")
	private Double value_37;
	@ColumnInfo(name="검침값", descr="38~39(mm)")
	private Double value_38;
	@ColumnInfo(name="검침값", descr="39~40(mm)")
	private Double value_39;
	@ColumnInfo(name="검침값", descr="40~41(mm)")
	private Double value_40;		
	@ColumnInfo(name="검침값", descr="41~42(mm)")
	private Double value_41;
	@ColumnInfo(name="검침값", descr="42~43(mm)")
	private Double value_42;
	@ColumnInfo(name="검침값", descr="43~44(mm)")
	private Double value_43;
	@ColumnInfo(name="검침값", descr="44~45(mm)")
	private Double value_44;
	@ColumnInfo(name="검침값", descr="45~46(mm)")
	private Double value_45;
	@ColumnInfo(name="검침값", descr="46~47(mm)")
	private Double value_46;
	@ColumnInfo(name="검침값", descr="47~48(mm)")
	private Double value_47;
	@ColumnInfo(name="검침값", descr="48~49(mm)")
	private Double value_48;
	@ColumnInfo(name="검침값", descr="49~50(mm)")
	private Double value_49;
	@ColumnInfo(name="검침값", descr="50~51(mm)")
	private Double value_50;
	@ColumnInfo(name="검침값", descr="51~52(mm)")
	private Double value_51;
	@ColumnInfo(name="검침값", descr="52~53(mm)")
	private Double value_52;
	@ColumnInfo(name="검침값", descr="53~54(mm)")
	private Double value_53;
	@ColumnInfo(name="검침값", descr="54~55(mm)")
	private Double value_54;
	@ColumnInfo(name="검침값", descr="55~56(mm)")
	private Double value_55;
	@ColumnInfo(name="검침값", descr="56~57(mm)")
	private Double value_56;
	@ColumnInfo(name="검침값", descr="57~58(mm)")
	private Double value_57;
	@ColumnInfo(name="검침값", descr="58~59(mm)")
	private Double value_58;
	@ColumnInfo(name="검침값", descr="59~60(mm)")
	private Double value_59;
	
	@Column(name = "SEND_RESULT", length=255)
	@ColumnInfo(name="sendResult", descr="외부 연계 시스템 에 전달하는 검침값 결과, 예 15분 ,30분 데이터가 전송됬으면 '15,30' 이렇게 설정됨 ")
	private String sendResult;	
	
	public LpPk getId() {
		return id;
	}
	
	public void setId(LpPk id) {
		this.id = id;
	}
	
	public MeteringLPTHU(){
		id = new LpPk();
	}	
	
	public DeviceType getMDevType() {
		return id.getMDevType();
	}
	/*
	public void setMDevType(DeviceType mdevType) {
		this.id.setMDevType(mdevType);
	}
	*/
	/*public void setMDevType(Integer mdevType) {
		if(DeviceType.Modem.getCode().equals(mdevType)){
			this.id.setMDevType(DeviceType.Modem);
		}
		if(DeviceType.Meter.getCode().equals(mdevType)){
			this.id.setMDevType(DeviceType.Meter);
		}
		if(DeviceType.EndDevice.getCode().equals(mdevType)){
			this.id.setMDevType(DeviceType.EndDevice);
		}		
	}*/
	public void setMDevType(String mdevType){
		if(DeviceType.Modem.equals(DeviceType.valueOf(mdevType))){
			this.id.setMDevType(mdevType);
		}
		if(DeviceType.Meter.equals(DeviceType.valueOf(mdevType))){
			this.id.setMDevType(mdevType);
		}
		if(DeviceType.EndDevice.equals(DeviceType.valueOf(mdevType))){
			this.id.setMDevType(mdevType);
		}
	}
	/*public void setMDevType(String mdevType){
		if(DeviceType.Modem.equals(other))
	}*/
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
	
	public String getYyyymmddhh() {
		return this.id.getYyyymmddhh();
	}
	public void setYyyymmddhh(String yyyymmddhh) {
		this.id.setYyyymmddhh(yyyymmddhh);
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
	
	public String getYyyymmdd() {
		return yyyymmdd;
	}
	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
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

	public String getHour() {
		return hour;
	}
	public void setHour(String hour) {
		this.hour = hour;
	}

	
	
	public Double getValue_00() {
		return value_00;
	}
	public void setValue_00(Double value_00) {
		this.value_00 = value_00;
	}
	public Double getValue_01() {
		return value_01;
	}
	public void setValue_01(Double value_01) {
		this.value_01 = value_01;
	}
	public Double getValue_02() {
		return value_02;
	}
	public void setValue_02(Double value_02) {
		this.value_02 = value_02;
	}
	public Double getValue_03() {
		return value_03;
	}
	public void setValue_03(Double value_03) {
		this.value_03 = value_03;
	}
	public Double getValue_04() {
		return value_04;
	}
	public void setValue_04(Double value_04) {
		this.value_04 = value_04;
	}
	public Double getValue_05() {
		return value_05;
	}
	public void setValue_05(Double value_05) {
		this.value_05 = value_05;
	}
	public Double getValue_06() {
		return value_06;
	}
	public void setValue_06(Double value_06) {
		this.value_06 = value_06;
	}
	public Double getValue_07() {
		return value_07;
	}
	public void setValue_07(Double value_07) {
		this.value_07 = value_07;
	}
	public Double getValue_08() {
		return value_08;
	}
	public void setValue_08(Double value_08) {
		this.value_08 = value_08;
	}
	public Double getValue_09() {
		return value_09;
	}
	public void setValue_09(Double value_09) {
		this.value_09 = value_09;
	}
	public Double getValue_10() {
		return value_10;
	}
	public void setValue_10(Double value_10) {
		this.value_10 = value_10;
	}
	public Double getValue_11() {
		return value_11;
	}
	public void setValue_11(Double value_11) {
		this.value_11 = value_11;
	}
	public Double getValue_12() {
		return value_12;
	}
	public void setValue_12(Double value_12) {
		this.value_12 = value_12;
	}
	public Double getValue_13() {
		return value_13;
	}
	public void setValue_13(Double value_13) {
		this.value_13 = value_13;
	}
	public Double getValue_14() {
		return value_14;
	}
	public void setValue_14(Double value_14) {
		this.value_14 = value_14;
	}
	public Double getValue_15() {
		return value_15;
	}
	public void setValue_15(Double value_15) {
		this.value_15 = value_15;
	}
	public Double getValue_16() {
		return value_16;
	}
	public void setValue_16(Double value_16) {
		this.value_16 = value_16;
	}
	public Double getValue_17() {
		return value_17;
	}
	public void setValue_17(Double value_17) {
		this.value_17 = value_17;
	}
	public Double getValue_18() {
		return value_18;
	}
	public void setValue_18(Double value_18) {
		this.value_18 = value_18;
	}
	public Double getValue_19() {
		return value_19;
	}
	public void setValue_19(Double value_19) {
		this.value_19 = value_19;
	}
	public Double getValue_20() {
		return value_20;
	}
	public void setValue_20(Double value_20) {
		this.value_20 = value_20;
	}
	public Double getValue_21() {
		return value_21;
	}
	public void setValue_21(Double value_21) {
		this.value_21 = value_21;
	}
	public Double getValue_22() {
		return value_22;
	}
	public void setValue_22(Double value_22) {
		this.value_22 = value_22;
	}
	public Double getValue_23() {
		return value_23;
	}
	public void setValue_23(Double value_23) {
		this.value_23 = value_23;
	}
	public Double getValue_24() {
		return value_24;
	}
	public void setValue_24(Double value_24) {
		this.value_24 = value_24;
	}
	public Double getValue_25() {
		return value_25;
	}
	public void setValue_25(Double value_25) {
		this.value_25 = value_25;
	}
	public Double getValue_26() {
		return value_26;
	}
	public void setValue_26(Double value_26) {
		this.value_26 = value_26;
	}
	public Double getValue_27() {
		return value_27;
	}
	public void setValue_27(Double value_27) {
		this.value_27 = value_27;
	}
	public Double getValue_28() {
		return value_28;
	}
	public void setValue_28(Double value_28) {
		this.value_28 = value_28;
	}
	public Double getValue_29() {
		return value_29;
	}
	public void setValue_29(Double value_29) {
		this.value_29 = value_29;
	}
	public Double getValue_30() {
		return value_30;
	}
	public void setValue_30(Double value_30) {
		this.value_30 = value_30;
	}
	public Double getValue_31() {
		return value_31;
	}
	public void setValue_31(Double value_31) {
		this.value_31 = value_31;
	}
	public Double getValue_32() {
		return value_32;
	}
	public void setValue_32(Double value_32) {
		this.value_32 = value_32;
	}
	public Double getValue_33() {
		return value_33;
	}
	public void setValue_33(Double value_33) {
		this.value_33 = value_33;
	}
	public Double getValue_34() {
		return value_34;
	}
	public void setValue_34(Double value_34) {
		this.value_34 = value_34;
	}
	public Double getValue_35() {
		return value_35;
	}
	public void setValue_35(Double value_35) {
		this.value_35 = value_35;
	}
	public Double getValue_36() {
		return value_36;
	}
	public void setValue_36(Double value_36) {
		this.value_36 = value_36;
	}
	public Double getValue_37() {
		return value_37;
	}
	public void setValue_37(Double value_37) {
		this.value_37 = value_37;
	}
	public Double getValue_38() {
		return value_38;
	}
	public void setValue_38(Double value_38) {
		this.value_38 = value_38;
	}
	public Double getValue_39() {
		return value_39;
	}
	public void setValue_39(Double value_39) {
		this.value_39 = value_39;
	}
	public Double getValue_40() {
		return value_40;
	}
	public void setValue_40(Double value_40) {
		this.value_40 = value_40;
	}
	public Double getValue_41() {
		return value_41;
	}
	public void setValue_41(Double value_41) {
		this.value_41 = value_41;
	}
	public Double getValue_42() {
		return value_42;
	}
	public void setValue_42(Double value_42) {
		this.value_42 = value_42;
	}
	public Double getValue_43() {
		return value_43;
	}
	public void setValue_43(Double value_43) {
		this.value_43 = value_43;
	}
	public Double getValue_44() {
		return value_44;
	}
	public void setValue_44(Double value_44) {
		this.value_44 = value_44;
	}
	public Double getValue_45() {
		return value_45;
	}
	public void setValue_45(Double value_45) {
		this.value_45 = value_45;
	}
	public Double getValue_46() {
		return value_46;
	}
	public void setValue_46(Double value_46) {
		this.value_46 = value_46;
	}
	public Double getValue_47() {
		return value_47;
	}
	public void setValue_47(Double value_47) {
		this.value_47 = value_47;
	}
	public Double getValue_48() {
		return value_48;
	}
	public void setValue_48(Double value_48) {
		this.value_48 = value_48;
	}
	public Double getValue_49() {
		return value_49;
	}
	public void setValue_49(Double value_49) {
		this.value_49 = value_49;
	}
	public Double getValue_50() {
		return value_50;
	}
	public void setValue_50(Double value_50) {
		this.value_50 = value_50;
	}
	public Double getValue_51() {
		return value_51;
	}
	public void setValue_51(Double value_51) {
		this.value_51 = value_51;
	}
	public Double getValue_52() {
		return value_52;
	}
	public void setValue_52(Double value_52) {
		this.value_52 = value_52;
	}
	public Double getValue_53() {
		return value_53;
	}
	public void setValue_53(Double value_53) {
		this.value_53 = value_53;
	}
	public Double getValue_54() {
		return value_54;
	}
	public void setValue_54(Double value_54) {
		this.value_54 = value_54;
	}
	public Double getValue_55() {
		return value_55;
	}
	public void setValue_55(Double value_55) {
		this.value_55 = value_55;
	}
	public Double getValue_56() {
		return value_56;
	}
	public void setValue_56(Double value_56) {
		this.value_56 = value_56;
	}
	public Double getValue_57() {
		return value_57;
	}
	public void setValue_57(Double value_57) {
		this.value_57 = value_57;
	}
	public Double getValue_58() {
		return value_58;
	}
	public void setValue_58(Double value_58) {
		this.value_58 = value_58;
	}
	public Double getValue_59() {
		return value_59;
	}
	public void setValue_59(Double value_59) {
		this.value_59 = value_59;
	}
	
    public String getSendResult() {
		return sendResult;
	}

	public void setSendResult(String sendResult) {
		this.sendResult = sendResult;
	}

	public void setLocation(Location location) {
        this.location = location;
    }
	
	@XmlTransient
    public Location getLocation() {
        return location;
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

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
		
}
