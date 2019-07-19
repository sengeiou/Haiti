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

@MappedSuperclass
public abstract class MeteringDayTHU {
    
    @EmbeddedId public DayTHUPk id; 

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

    @ColumnInfo(name="지역아이디", descr="지역 테이블의 ID나  NULL")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="location_id")
    private Location location;
    
    @Column(name="location_id", nullable=true, updatable=false, insertable=false)
    private Integer locationId;
    
    @Column(name = "device_type")
    @ColumnInfo(name="장비타입", descr="집중기, 모뎀")
    private DeviceType deviceType;
    
    @ColumnInfo(name="최대값", descr="최대값")
    private Double maximumValue;
    
    @ColumnInfo(name="최소값", descr="최소값")
    private Double minimumValue;
    
    @ColumnInfo(name="평균값", descr="평균값")
    private Double avgValue;
    
    @ColumnInfo(name="검침값", descr="00~01(h)")
    private Double value_00;    
    @ColumnInfo(name="검침값", descr="01~02(h)")
    private Double value_01;
    @ColumnInfo(name="검침값", descr="02~03(h)")
    private Double value_02;
    @ColumnInfo(name="검침값", descr="03~04(h)")
    private Double value_03;
    @ColumnInfo(name="검침값", descr="04~05(h)")
    private Double value_04;
    @ColumnInfo(name="검침값", descr="05~06(h)")
    private Double value_05;
    @ColumnInfo(name="검침값", descr="06~07(h)")
    private Double value_06;
    @ColumnInfo(name="검침값", descr="07~08(h)")
    private Double value_07;
    @ColumnInfo(name="검침값", descr="08~09(h)")
    private Double value_08;
    @ColumnInfo(name="검침값", descr="09~10(h)")
    private Double value_09;
    @ColumnInfo(name="검침값", descr="10~11(h)")
    private Double value_10;
    @ColumnInfo(name="검침값", descr="11~12(h)")
    private Double value_11;
    @ColumnInfo(name="검침값", descr="12~13(h)")
    private Double value_12;
    @ColumnInfo(name="검침값", descr="13~14(h)")
    private Double value_13;
    @ColumnInfo(name="검침값", descr="14~15(h)")
    private Double value_14;
    @ColumnInfo(name="검침값", descr="15~16(h)")
    private Double value_15;
    @ColumnInfo(name="검침값", descr="16~17(h)")
    private Double value_16;
    @ColumnInfo(name="검침값", descr="17~18(h)")
    private Double value_17;
    @ColumnInfo(name="검침값", descr="18~19(h)")
    private Double value_18;
    @ColumnInfo(name="검침값", descr="19~20(h)")
    private Double value_19;
    @ColumnInfo(name="검침값", descr="20~21(h)")
    private Double value_20;
    @ColumnInfo(name="검침값", descr="21~22(h)")
    private Double value_21;
    @ColumnInfo(name="검침값", descr="22~23(h)")
    private Double value_22;
    @ColumnInfo(name="검침값", descr="23~24(h)")
    private Double value_23;
    
    @Column(name = "SEND_RESULT", length=255)
    @ColumnInfo(name="sendResult", descr="외부 연계 시스템 에 전달하는 검침값 결과, 예 15분 ,30분 데이터가 전송됬으면 '15,30' 이렇게 설정됨 ")
    private String sendResult;  
    
    public MeteringDayTHU(){
        id = new DayTHUPk();
    }
    
    public DayTHUPk getId() {
        return id;
    }
    public void setId(DayTHUPk id) {
        this.id = id;
    }
    
    public DeviceType getMDevType() {
        return id.getMDevType();
    }
    /*
    public void setMDevType(DeviceType mdevType) {
        this.id.setMDevType(mdevType);
    }*/
    
    public void setMDevType(Integer mdevType) {
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
    
    public String getYyyymmdd() {
        return this.id.getYyyymmdd();
    }
    public void setYyyymmdd(String yyyymmdd) {
        this.id.setYyyymmdd(yyyymmdd);
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

    public Double getMaximumValue() {
        return maximumValue;
    }
    public void setMaximumValue(Double maximumValue) {
        this.maximumValue = maximumValue;
    }
    
    public Double getMinimumValue() {
        return minimumValue;
    }
    public void setMinimumValue(Double minimumValue) {
        this.minimumValue = minimumValue;
    }
    
    public Double getAvgValue() {
        return avgValue;
    }
    public void setAvgValue(Double avgValue) {
        this.avgValue = avgValue;
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
