package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.Scope;
import com.aimir.constants.CommonConstants.BatteryStatus;
import com.aimir.constants.CommonConstants.ModemNetworkType;
import com.aimir.constants.CommonConstants.ModemPowerType;

/**
 * 
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>Zigbee Repeater Modem </p>
 * <pre>
 *  Operation 범위는
 *  - Status Monitoring
 *  - Event Log 읽기 
 * </pre>
 * @author goodjob
 *
 */
@Entity
@DiscriminatorValue("ZBRepeater")
public class ZBRepeater extends Modem {

    private static final long serialVersionUID = -3512093224145523336L;    

    @ColumnInfo(name="통신형태", descr="예: RFD, FFD")
    @Enumerated(EnumType.STRING)
    @Column(name="NETWORK_TYPE")
    private ModemNetworkType networkType;    

    @ColumnInfo(name="전력형태", descr="예: Solar, Line(AC/DC), Battery")
    @Enumerated(EnumType.STRING)
    @Column(name="POWER_TYPE")
    private ModemPowerType powerType;
    
    @ColumnInfo(name="배터리용량")
    @Column(name="BATTERY_CAPACITY", length=10)
    private Integer batteryCapacity; 

    @ColumnInfo(name="배터리 전압")
    @Column(name="BATTERY_VOLT")
    private Double batteryVolt;

    @ColumnInfo(name="배터리 상태", descr="Battery Status( 1: Normal, 0,2: Abnormal, 3: Replacement, 4:Unknown(default))")
    @Column(name="BATTERY_STATUS", length=10)
    @Enumerated(EnumType.STRING)
    private BatteryStatus batteryStatus; 

    @ColumnInfo(name="", descr="Wake Up Day")
    @Column(name="REPEATING_DAY", length=255)
    private String repeatingDay;

    @ColumnInfo(name="", descr="Wake Up Hour")
    @Column(name="REPEATING_HOUR", length=255)
    private String repeatingHour;

    @ColumnInfo(name="", descr="")
    @Column(name="REPEATING_SETUP_SEC", length=10)
    private Integer repeatingSetupSec;
    
    @ColumnInfo(name="", descr="AES-128 Key Data - 250 (securityKey -> linkKey)")
    @Column(name="LINK_KEY", length=32)
    private String linkKey;

    @ColumnInfo(name="", descr="AES-128 Key Data")
    @Column(name="NETWORK_KEY", length=32)
    private String networkKey;
    
    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="Flag For a QC Test Program")
    @Column(name="TEST_FLAG")
    private Boolean testFlag;    

    @ColumnInfo(name="solarADV")
    @Column(name="SOLAR_ADV", length=38)
    private Double solarADV;

    @ColumnInfo(name="solarChgBv")
    @Column(name="SOLAR_CHG_BV", length=38)
    private Double solarChgBV;

    @ColumnInfo(name="solarBDCV")
    @Column(name="SOLAR_BDCV", length=38)
    private Double solarBDCV;
    
    @ColumnInfo(name="", descr="Channel Id")
    @Column(name="CHANNEL_ID", length=10)
    private Integer channelId;

    @ColumnInfo(name="", descr="Pan ID")
    @Column(name="PAN_ID", length=10)
    private Integer panId;
 
    @ColumnInfo(name="", descr="8 Bytes")
    @Column(name="EXTPAN_ID", length=16)
    private String extPanId;    

    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="Metering Schedule Day (Mask 4 Bytes)")
    @Column(name="METERING_DAY", length=255)
    private String meteringDay;

    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="Metering Schedule Hour (Mask 12 Bytes)")
    @Column(name="METERING_HOUR", length=255)
    private String meteringHour;

    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="Periodically Reset Time By a Day.")
    @Column(name="FIXED_RESET", length=5)
    private String fixedReset;    

    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="0:Auto Scan And Join, 1:Channel, PanID Manually")
    @Column(name="MANUAL_ENABLE")
    private Boolean manualEnable;

    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="0 : AES-128 Key Disable, 1:AES-128 Key Enable")
    @Column(name="SECURITY_ENABLE")
    private Boolean securityEnable;
    
    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="오퍼레이팅 데이 모뎀이 동작한 누적시간(일수) ")
    @Column(name="OPERATING_DAY")
    private Integer operatingDay;
    
    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="실제 동작 유효시간(초) ")
    @Column(name="ACTIVE_TIME")
    private Integer activeTime;
    
    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="모뎀이 올려주는 lp데이터가 오늘부터 몇일 전의 데이터인지 정의, 0~39 즉 현재부터 과거시점까지 최대 40일치의 데이터를 가져올 수 있음")
    @Column(name="LP_CHOICE", length=2)
    private Integer lpChoice;

	public ModemNetworkType getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = ModemNetworkType.valueOf(networkType);
	}

	public ModemPowerType getPowerType() {
		return powerType;
	}

	public void setPowerType(String powerType) {
		this.powerType = ModemPowerType.valueOf(powerType);
	}

	public Integer getBatteryCapacity() {
		return batteryCapacity;
	}

	public void setBatteryCapacity(Integer batteryCapacity) {
		this.batteryCapacity = batteryCapacity;
	}

	public Double getBatteryVolt() {
		return batteryVolt;
	}

	public void setBatteryVolt(Double batteryVolt) {
		this.batteryVolt = batteryVolt;
	}

	public BatteryStatus getBatteryStatus() {
		return batteryStatus;
	}

	public void setBatteryStatus(String batteryStatus) {
		this.batteryStatus = BatteryStatus.valueOf(batteryStatus);
	}

	public String getRepeatingDay() {
		return repeatingDay;
	}

	public void setRepeatingDay(String repeatingDay) {
		this.repeatingDay = repeatingDay;
	}

	public String getRepeatingHour() {
		return repeatingHour;
	}

	public void setRepeatingHour(String repeatingHour) {
		this.repeatingHour = repeatingHour;
	}

	public Integer getRepeatingSetupSec() {
		return repeatingSetupSec;
	}

	public void setRepeatingSetupSec(Integer repeatingSetupSec) {
		this.repeatingSetupSec = repeatingSetupSec;
	}

	public String getLinkKey() {
		return linkKey;
	}

	public void setLinkKey(String linkKey) {
		this.linkKey = linkKey;
	}

	public String getNetworkKey() {
		return networkKey;
	}

	public void setNetworkKey(String networkKey) {
		this.networkKey = networkKey;
	}

	public Boolean getTestFlag() {
		return testFlag;
	}

	public void setTestFlag(Boolean testFlag) {
		this.testFlag = testFlag;
	}

	public Double getSolarADV() {
		return solarADV;
	}

	public void setSolarADV(Double solarADV) {
		this.solarADV = solarADV;
	}

	public Double getSolarChgBV() {
		return solarChgBV;
	}

	public void setSolarChgBV(Double solarChgBV) {
		this.solarChgBV = solarChgBV;
	}

	public Double getSolarBDCV() {
		return solarBDCV;
	}

	public void setSolarBDCV(Double solarBDCV) {
		this.solarBDCV = solarBDCV;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	public Integer getPanId() {
		return panId;
	}

	public void setPanId(Integer panId) {
		this.panId = panId;
	}

	public String getExtPanId() {
		return extPanId;
	}

	public void setExtPanId(String extPanId) {
		this.extPanId = extPanId;
	}

	public String getMeteringDay() {
		return meteringDay;
	}

	public void setMeteringDay(String meteringDay) {
		this.meteringDay = meteringDay;
	}

	public String getMeteringHour() {
		return meteringHour;
	}

	public void setMeteringHour(String meteringHour) {
		this.meteringHour = meteringHour;
	}

	public String getFixedReset() {
		return fixedReset;
	}

	public void setFixedReset(String fixedReset) {
		this.fixedReset = fixedReset;
	}

	public Boolean getManualEnable() {
		return manualEnable;
	}

	public void setManualEnable(Boolean manualEnable) {
		this.manualEnable = manualEnable;
	}

	public Boolean getSecurityEnable() {
		return securityEnable;
	}

	public void setSecurityEnable(Boolean securityEnable) {
		this.securityEnable = securityEnable;
	}

	public Integer getOperatingDay() {
		return operatingDay;
	}

	public void setOperatingDay(Integer operatingDay) {
		this.operatingDay = operatingDay;
	}

	public Integer getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(Integer activeTime) {
		this.activeTime = activeTime;
	}

	public Integer getLpChoice() {
		return lpChoice;
	}

	public void setLpChoice(Integer lpChoice) {
		this.lpChoice = lpChoice;
	}
  

}