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
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>Zigbee Extention Unit for Pulse Metering (Gas,Water 타입의 미터와 인터페이스)</p>
 * 
 * <pre>
 * Metering Day, MeteringHour 설정은 ZEUPLS만 설정한다.
 * 
 * Operation 범위는
 * - Status Monitoring
 * - Unit Scanning
 * - Event Log 읽기
 * - LP Log 읽기
 * 
 * Date          Version     Author   Description
 * 2016. 2. 23.  v1.1        lucky   Change number length. 43->38 oracle maximum number length: 38
 * 
 * </pre>
 *
 */
@Entity
@DiscriminatorValue("ZEUPLS")
public class ZEUPLS extends Modem {

    private static final long serialVersionUID = -7528683306075222615L;
    
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
    private Double batteryCapacity; 

    @ColumnInfo(name="배터리 전압")
    @Column(name="BATTERY_VOLT", length=5)
    private Double batteryVolt;

    @ColumnInfo(name="배터리 상태", descr="Battery Status( 1: Normal, 0,2: Abnormal, 3: Replacement, 4:Unknown(default))")
    @Column(name="BATTERY_STATUS", length=10)
    @Enumerated(EnumType.STRING)
    private BatteryStatus batteryStatus;  
    
    @ColumnInfo(name="배터리전압 offset")
    @Column(name="VOLT_OFFSET", length=10)
    private Double voltOffset;

    @ColumnInfo(name="", descr="")
    @Column(name="AUTO_TRAP_FLAG")
    private Boolean autoTrapFlag;

    @ColumnInfo(name="", descr="")
    @Column(name="TRAP_DATE", length=10)
    private Integer trapDate;

    @ColumnInfo(name="", descr="")
    @Column(name="TRAP_HOUR", length=10)
    private Integer trapHour;

    @ColumnInfo(name="", descr="")
    @Column(name="TRAP_Minute", length=10)
    private Integer trapMinute;

    @ColumnInfo(name="", descr="")
    @Column(name="TRAP_SECOND", length=10)
    private Integer trapSecond;

    @ColumnInfo(name="", descr="")
    @Column(name="LQI", length=38)
    private Integer LQI;

    @ColumnInfo(name="", descr="")
    @Column(name="RSSI", length=10)
    private Integer rssi;

    @ColumnInfo(name="", descr="")
    @Column(name="RESET_REASON", length=10)
    private Integer resetReason;

    @ColumnInfo(name="", descr="")
    @Column(name="PERMIT_MODE", length=10)
    private Integer permitMode;

    @ColumnInfo(name="", descr="")
    @Column(name="PERMIT_STATE", length=10)
    private Integer permitState;

    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="알람데이터를 보낼 것인지 여부")
    @Column(name="ALARM_FLAG", length=10)
    private Integer alarmFlag;

    @ColumnInfo(name="", descr="")
    @Column(name="ALARM_MASK", length=10)
    private Integer alarmMask;
    
    @ColumnInfo(name="", descr="AES-128 Key Data - 250 (securityKey -> linkKey)")
    @Column(name="LINK_KEY", length=32)
    private String linkKey;

    @ColumnInfo(name="", descr="AES-128 Key Data")
    @Column(name="NETWORK_KEY", length=32)
    private String networkKey;
    
    @ColumnInfo(name="", descr="")
    @Column(name="NEED_JOIN_SET")
    private Boolean needJoinSet;//
    
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

    @ColumnInfo(name="", descr="0:Auto Scan And Join, 1:Channel, PanID Manually")
    @Column(name="MANUAL_ENABLE", length=1)
    private Boolean manualEnable;

    @ColumnInfo(name="", descr="0 : AES-128 Key Disable, 1:AES-128 Key Enable")
    @Column(name="SECURITY_ENABLE", length=1)
    private Boolean securityEnable;    

    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="Metering Schedule Day (Mask 4 Bytes) MSB->LSB")
    @Column(name="METERING_DAY", length=255)
    private String meteringDay;

    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="Metering Schedule Hour (Mask 12 Bytes) MSB->LSB")
    @Column(name="METERING_HOUR", length=255)
    private String meteringHour;

    @ColumnInfo(name="", descr="Periodically Reset Time By a Day.")
    @Column(name="FIXED_RESET", length=5)
    private String fixedReset;    
    
    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="모뎀이 올려주는 lp데이터가 오늘부터 몇일 전의 데이터인지 정의, 0~39 즉 현재부터 과거시점까지 최대 40일치의 데이터를 가져올 수 있음")
    @Column(name="LP_CHOICE", length=2)
    private Integer lpChoice;
    
    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="오퍼레이팅 데이 모뎀이 동작한 누적시간(일수) ")
    @Column(name="OPERATING_DAY")
    private Integer operatingDay;
    
    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="실제 동작 유효시간(초) ")
    @Column(name="ACTIVE_TIME")
    private Integer activeTime;

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

	public Double getBatteryCapacity() {
		return batteryCapacity;
	}

	public void setBatteryCapacity(Double batteryCapacity) {
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
		System.out.println("batteryStatus : " + batteryStatus);
		this.batteryStatus = BatteryStatus.valueOf(batteryStatus);
		
		System.out.println("batteryStatus.valueOf : " + BatteryStatus.valueOf(batteryStatus));
		System.out.println("this.batteryStatus : " + this.batteryStatus.getStatus());
		System.out.println("this.batteryStatus : " + this.batteryStatus.name());
	}

	public void setVoltOffset(double voltOffset) {
        this.voltOffset = voltOffset;
    }
    
    public Double getVoltOffset() {
        return voltOffset;
    }
    
	public Boolean getAutoTrapFlag() {
		return autoTrapFlag;
	}

	public void setAutoTrapFlag(Boolean autoTrapFlag) {
		this.autoTrapFlag = autoTrapFlag;
	}

	public Integer getTrapDate() {
		return trapDate;
	}

	public void setTrapDate(Integer trapDate) {
		this.trapDate = trapDate;
	}

	public Integer getTrapHour() {
		return trapHour;
	}

	public void setTrapHour(Integer trapHour) {
		this.trapHour = trapHour;
	}

	public Integer getTrapMinute() {
		return trapMinute;
	}

	public void setTrapMinute(Integer trapMinute) {
		this.trapMinute = trapMinute;
	}

	public Integer getTrapSecond() {
		return trapSecond;
	}

	public void setTrapSecond(Integer trapSecond) {
		this.trapSecond = trapSecond;
	}

	public Integer getLQI() {
		return LQI;
	}

	public void setLQI(Integer lqi) {
		LQI = lqi;
	}

	public Integer getRssi() {
		return rssi;
	}

	public void setRssi(Integer rssi) {
		this.rssi = rssi;
	}

	public Integer getResetReason() {
		return resetReason;
	}

	public void setResetReason(Integer resetReason) {
		this.resetReason = resetReason;
	}

	public Integer getPermitMode() {
		return permitMode;
	}

	public void setPermitMode(Integer permitMode) {
		this.permitMode = permitMode;
	}

	public Integer getPermitState() {
		return permitState;
	}

	public void setPermitState(Integer permitState) {
		this.permitState = permitState;
	}

	public Integer getAlarmFlag() {
		return alarmFlag;
	}

	public void setAlarmFlag(Integer alarmFlag) {
		this.alarmFlag = alarmFlag;
	}

	public Integer getAlarmMask() {
		return alarmMask;
	}

	public void setAlarmMask(Integer alarmMask) {
		this.alarmMask = alarmMask;
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

	public Boolean getNeedJoinSet() {
		return needJoinSet;
	}

	public void setNeedJoinSet(Boolean needJoinSet) {
		this.needJoinSet = needJoinSet;
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

	public Integer getLpChoice() {
		return lpChoice;
	}

	public void setLpChoice(Integer lpChoice) {
		this.lpChoice = lpChoice;
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

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((LQI == null) ? 0 : LQI.hashCode());
        result = prime * result
                + ((alarmFlag == null) ? 0 : alarmFlag.hashCode());
        result = prime * result
                + ((alarmMask == null) ? 0 : alarmMask.hashCode());
        result = prime * result
                + ((autoTrapFlag == null) ? 0 : autoTrapFlag.hashCode());
        result = prime * result
                + ((batteryCapacity == null) ? 0 : batteryCapacity.hashCode());
        result = prime * result
                + ((batteryStatus == null) ? 0 : batteryStatus.hashCode());
        result = prime * result
                + ((batteryVolt == null) ? 0 : batteryVolt.hashCode());
        result = prime * result
                + ((channelId == null) ? 0 : channelId.hashCode());
        result = prime * result
                + ((extPanId == null) ? 0 : extPanId.hashCode());
        result = prime * result
                + ((fixedReset == null) ? 0 : fixedReset.hashCode());
        result = prime * result + ((linkKey == null) ? 0 : linkKey.hashCode());
        result = prime * result
                + ((lpChoice == null) ? 0 : lpChoice.hashCode());
        result = prime * result
                + ((manualEnable == null) ? 0 : manualEnable.hashCode());
        result = prime * result
                + ((meteringDay == null) ? 0 : meteringDay.hashCode());
        result = prime * result
                + ((meteringHour == null) ? 0 : meteringHour.hashCode());
        result = prime * result
                + ((needJoinSet == null) ? 0 : needJoinSet.hashCode());
        result = prime * result
                + ((networkKey == null) ? 0 : networkKey.hashCode());
        result = prime * result
                + ((networkType == null) ? 0 : networkType.hashCode());
        result = prime * result + ((panId == null) ? 0 : panId.hashCode());
        result = prime * result
                + ((permitMode == null) ? 0 : permitMode.hashCode());
        result = prime * result
                + ((permitState == null) ? 0 : permitState.hashCode());
        result = prime * result
                + ((powerType == null) ? 0 : powerType.hashCode());
        result = prime * result
                + ((resetReason == null) ? 0 : resetReason.hashCode());
        result = prime * result + ((rssi == null) ? 0 : rssi.hashCode());
        result = prime * result
                + ((securityEnable == null) ? 0 : securityEnable.hashCode());
        result = prime * result
                + ((solarADV == null) ? 0 : solarADV.hashCode());
        result = prime * result
                + ((solarBDCV == null) ? 0 : solarBDCV.hashCode());
        result = prime * result
                + ((solarChgBV == null) ? 0 : solarChgBV.hashCode());
        result = prime * result
                + ((testFlag == null) ? 0 : testFlag.hashCode());
        result = prime * result
                + ((trapDate == null) ? 0 : trapDate.hashCode());
        result = prime * result
                + ((trapHour == null) ? 0 : trapHour.hashCode());
        result = prime * result
                + ((trapMinute == null) ? 0 : trapMinute.hashCode());
        result = prime * result
                + ((trapSecond == null) ? 0 : trapSecond.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ZEUPLS other = (ZEUPLS) obj;
        if (LQI == null) {
            if (other.LQI != null)
                return false;
        } else if (!LQI.equals(other.LQI))
            return false;
        if (alarmFlag == null) {
            if (other.alarmFlag != null)
                return false;
        } else if (!alarmFlag.equals(other.alarmFlag))
            return false;
        if (alarmMask == null) {
            if (other.alarmMask != null)
                return false;
        } else if (!alarmMask.equals(other.alarmMask))
            return false;
        if (autoTrapFlag == null) {
            if (other.autoTrapFlag != null)
                return false;
        } else if (!autoTrapFlag.equals(other.autoTrapFlag))
            return false;
        if (batteryCapacity == null) {
            if (other.batteryCapacity != null)
                return false;
        } else if (!batteryCapacity.equals(other.batteryCapacity))
            return false;
        if (batteryStatus == null) {
            if (other.batteryStatus != null)
                return false;
        } else if (!batteryStatus.equals(other.batteryStatus))
            return false;
        if (batteryVolt == null) {
            if (other.batteryVolt != null)
                return false;
        } else if (!batteryVolt.equals(other.batteryVolt))
            return false;
        if (voltOffset == null) {
            if (other.voltOffset != null)
                return false;
        } else if (!voltOffset.equals(other.voltOffset))
            return false;
        if (channelId == null) {
            if (other.channelId != null)
                return false;
        } else if (!channelId.equals(other.channelId))
            return false;
        if (extPanId == null) {
            if (other.extPanId != null)
                return false;
        } else if (!extPanId.equals(other.extPanId))
            return false;
        if (fixedReset == null) {
            if (other.fixedReset != null)
                return false;
        } else if (!fixedReset.equals(other.fixedReset))
            return false;
        if (linkKey == null) {
            if (other.linkKey != null)
                return false;
        } else if (!linkKey.equals(other.linkKey))
            return false;
        if (lpChoice == null) {
            if (other.lpChoice != null)
                return false;
        } else if (!lpChoice.equals(other.lpChoice))
            return false;
        if (manualEnable == null) {
            if (other.manualEnable != null)
                return false;
        } else if (!manualEnable.equals(other.manualEnable))
            return false;
        if (meteringDay == null) {
            if (other.meteringDay != null)
                return false;
        } else if (!meteringDay.equals(other.meteringDay))
            return false;
        if (meteringHour == null) {
            if (other.meteringHour != null)
                return false;
        } else if (!meteringHour.equals(other.meteringHour))
            return false;
        if (needJoinSet == null) {
            if (other.needJoinSet != null)
                return false;
        } else if (!needJoinSet.equals(other.needJoinSet))
            return false;
        if (networkKey == null) {
            if (other.networkKey != null)
                return false;
        } else if (!networkKey.equals(other.networkKey))
            return false;
        if (networkType == null) {
            if (other.networkType != null)
                return false;
        } else if (!networkType.equals(other.networkType))
            return false;
        if (panId == null) {
            if (other.panId != null)
                return false;
        } else if (!panId.equals(other.panId))
            return false;
        if (permitMode == null) {
            if (other.permitMode != null)
                return false;
        } else if (!permitMode.equals(other.permitMode))
            return false;
        if (permitState == null) {
            if (other.permitState != null)
                return false;
        } else if (!permitState.equals(other.permitState))
            return false;
        if (powerType == null) {
            if (other.powerType != null)
                return false;
        } else if (!powerType.equals(other.powerType))
            return false;
        if (resetReason == null) {
            if (other.resetReason != null)
                return false;
        } else if (!resetReason.equals(other.resetReason))
            return false;
        if (rssi == null) {
            if (other.rssi != null)
                return false;
        } else if (!rssi.equals(other.rssi))
            return false;
        if (securityEnable == null) {
            if (other.securityEnable != null)
                return false;
        } else if (!securityEnable.equals(other.securityEnable))
            return false;
        if (solarADV == null) {
            if (other.solarADV != null)
                return false;
        } else if (!solarADV.equals(other.solarADV))
            return false;
        if (solarBDCV == null) {
            if (other.solarBDCV != null)
                return false;
        } else if (!solarBDCV.equals(other.solarBDCV))
            return false;
        if (solarChgBV == null) {
            if (other.solarChgBV != null)
                return false;
        } else if (!solarChgBV.equals(other.solarChgBV))
            return false;
        if (testFlag == null) {
            if (other.testFlag != null)
                return false;
        } else if (!testFlag.equals(other.testFlag))
            return false;
        if (trapDate == null) {
            if (other.trapDate != null)
                return false;
        } else if (!trapDate.equals(other.trapDate))
            return false;
        if (trapHour == null) {
            if (other.trapHour != null)
                return false;
        } else if (!trapHour.equals(other.trapHour))
            return false;
        if (trapMinute == null) {
            if (other.trapMinute != null)
                return false;
        } else if (!trapMinute.equals(other.trapMinute))
            return false;
        if (trapSecond == null) {
            if (other.trapSecond != null)
                return false;
        } else if (!trapSecond.equals(other.trapSecond))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ZEUPLS [LQI=" + LQI + ", alarmFlag=" + alarmFlag
                + ", alarmMask=" + alarmMask + ", autoTrapFlag=" + autoTrapFlag
                + ", batteryCapacity=" + batteryCapacity + ", batteryStatus=" + batteryStatus
                + ", batteryVolt=" + batteryVolt + ", voltOffset=" + voltOffset
                + ", channelId=" + channelId + ", extPanId=" + extPanId
                + ", fixedReset=" + fixedReset + ", linkKey=" + linkKey
                + ", lpChoice=" + lpChoice + ", manualEnable=" + manualEnable
                + ", meteringDay=" + meteringDay + ", meteringHour=" + meteringHour
                + ", needJoinSet=" + needJoinSet + ", networkKey=" + networkKey
                + ", networkType=" + networkType + ", panId=" + panId
                + ", permitMode=" + permitMode + ", permitState=" + permitState
                + ", powerType=" + powerType + ", resetReason=" + resetReason
                + ", rssi=" + rssi + ", securityEnable=" + securityEnable
                + ", solarADV=" + solarADV + ", solarBDCV=" + solarBDCV
                + ", solarChgBV=" + solarChgBV + ", testFlag=" + testFlag
                + ", trapDate=" + trapDate + ", trapHour=" + trapHour
                + ", trapMinute=" + trapMinute + ", trapSecond=" + trapSecond + "]";
    }
}