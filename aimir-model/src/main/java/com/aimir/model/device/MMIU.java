package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.Scope;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p> Modem Meter Interface Unit - mobile type modem (2g, 3g, LTE, 4g, 5g) or P2P modem</p>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@DiscriminatorValue("MMIU")
public class MMIU extends Modem {

    private static final long serialVersionUID = -8856182359061451639L;    
    
    @ColumnInfo(name="ipv6Address", descr="IPv6 Modem Ipv6 Address")
    @Column(name="IPV6_ADDRESS", length=64)
    private String ipv6Address;

    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=false), descr="")
    @Column(name="ERROR_STATUS", length=10)
    private Integer errorStatus;
    
    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="Metering Schedule Day (Mask 4 Bytes)")
    @Column(name="METERING_DAY", length=255)
    private String meteringDay;

    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="Metering Schedule Hour (Mask 12 Bytes)")
    @Column(name="METERING_HOUR", length=255)
    private String meteringHour;
    
    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true),descr="모뎀이 올려주는 lp데이터가 오늘부터 몇일 전의 데이터인지 정의")
    @Column(name="LP_CHOICE", length=2)
    private Integer lpChoice;

    @ColumnInfo(name="APN address")
    @Column(name="APN_ADDRESS", length=100)
    private String apnAddress;
    
    @ColumnInfo(name="APN Id")
    @Column(name="APN_ID", length=30)
    private String apnId;
    
    @ColumnInfo(name="APN Password")
    @Column(name="APN_PASSWORD", length=30)
    private String apnPassword;
    
    @ColumnInfo(name="Reset Interval")
    @Column(name="RESET_INTERVAL")
    private Integer resetInterval;
    
    @ColumnInfo(name="Metering Interval")
    @Column(name="METERING_INTERVAL")
    private Integer meteringInterval;
    
    @ColumnInfo(name="mobileNetworkType", descr="mobileNetworkType (2g,3g,4g)")
    @Column(name="MOBILE_NETWORK_TYPE", length=20)
    private String mobileNetworkType;
    
    @ColumnInfo(name="frequency", descr="Frequency  주파수(ex: 868mhz)")
    @Column(name="FREQUENCY")
    private Integer frequency;
    
	@ColumnInfo(name = "txPower", descr = "TX Power [단위 dBm]")
	@Column(name = "TX_POWER")
	//private int txPower;
	private String txPower; 
	
    @ColumnInfo(name="cellId", descr="base station id")
    @Column(name="CELL_ID")
    private String cellId;

    // SP-982
    @ColumnInfo(name="moduleVersion", descr="Version of Telit Module")
    @Column(name="MODULE_VERSION", length=20)
    private String moduleVersion;
    
    @ColumnInfo(name="moduleRevision", descr="Revision of Telit Module")
    @Column(name="MODULE_REVISION", length=20)
    private String moduleRevision;
    
    public String getModuleRevision() {
		return moduleRevision;
	}

	public void setModuleRevision(String moduleRevision) {
		this.moduleRevision = moduleRevision;
	}

	public Integer getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(Integer errorStatus) {
        this.errorStatus = errorStatus;
    }    

	public String getMeteringDay() {
		return meteringDay;
	}

	public String getModuleVersion() {
		return moduleVersion;
	}

	public void setModuleVersion(String moduleVersion) {
		this.moduleVersion = moduleVersion;
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
	
	public Integer getLpChoice() {
		return lpChoice;
	}

	public void setLpChoice(Integer lpChoice) {
		this.lpChoice = lpChoice;
	}

    public String getApnAddress() {
        return apnAddress;
    }

    public void setApnAddress(String apnAddress) {
        this.apnAddress = apnAddress;
    }

    public String getApnId() {
        return apnId;
    }

    public void setApnId(String apnId) {
        this.apnId = apnId;
    }

    public String getApnPassword() {
        return apnPassword;
    }

    public void setApnPassword(String apnPassword) {
        this.apnPassword = apnPassword;
    }

    public Integer getResetInterval() {
        return resetInterval;
    }

    public void setResetInterval(Integer resetInterval) {
        this.resetInterval = resetInterval;
    }

    public Integer getMeteringInterval() {
        return meteringInterval;
    }

    public void setMeteringInterval(Integer meteringInterval) {
        this.meteringInterval = meteringInterval;
    } 
    
    public String getIpv6Address() {
        return ipv6Address;
    }

    public void setIpv6Address(String ipv6Address) {
        this.ipv6Address = ipv6Address;
    }

	public String getMobileNetworkType() {
		return mobileNetworkType;
	}

	public void setMobileNetworkType(String mobileNetworkType) {
		this.mobileNetworkType = mobileNetworkType;
	}

	public Integer getFrequency() {
		return frequency;
	}

	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}

	public String getTxPower() {
		return txPower;
	}

	public void setTxPower(String txPower) {
		this.txPower = txPower;
	}

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}
	
	
	
}