package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.aimir.annotation.ColumnInfo;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>SubGiga Modem</p> 
 * 
 * <pre>
 * Operation 범위는 미정
 * </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@DiscriminatorValue("SubGiga")
public class SubGiga extends Modem {

    private static final long serialVersionUID = -3721786480232359971L;
    
    @ColumnInfo(name="BASE_STATION_ADDRESS", descr="Base Station Address")
    @Column(name="LINK_KEY", length=64)
    private String baseStationAddress;
    
    @ColumnInfo(name="ipv6Address", descr="IPv6 Modem Ipv6 Address")
    @Column(name="IPV6_ADDRESS", length=64)
    private String ipv6Address;
    
    @ColumnInfo(name="securityKey", descr="Modem ieee 802.15.4 Security Key")
    @Column(name="SECURITY_KEY", length=64)
    private String securityKey;
    
    @ColumnInfo(name="hopsToBaseStation", descr="Base Station Hop count ")
    @Column(name="HOPS_TO_BASESTATION")
    private Integer hopsToBaseStation;
    
    @ColumnInfo(name="frequency", descr="Frequency  주파수(ex: 868mhz)")
    @Column(name="FREQUENCY")
    private Integer frequency;
    
    @ColumnInfo(name="bandWidth", descr="Bandwidth communication speed(ex: 200kbps)")
    @Column(name="BAND_WIDTH")
    private Integer bandWidth;
    
    @ColumnInfo(name="port", descr="tcp/udp 통신 포트")
    @Column(name="listen_port")
    private Integer listenPort;
    
    @ColumnInfo(name="panId")
    @Column(name="PAN_ID")
    private Integer panId;
    
    @ColumnInfo(name="Reset Interval")
    @Column(name="RESET_INTERVAL")
    private Integer resetInterval;
    
    @ColumnInfo(name="Metering Interval")
    @Column(name="METERING_INTERVAL")
    private Integer meteringInterval;
    
    @ColumnInfo(name="Metering Time Range")
    @Column(name="METERING_TIME_RANGE")
    private Integer meteringTimeRange;
    
    @ColumnInfo(name="APN address")
    @Column(name="APN_ADDRESS")
    private String apnAddress;
    
    @ColumnInfo(name="APN Id")
    @Column(name="APN_ID")
    private String apnId;
    
    @ColumnInfo(name="APN Password")
    @Column(name="APN_PASSWORD")
    private String apnPassword;
    
   @ColumnInfo(name="RSSI")
    @Column(name="RSSI", length=20)
    private Integer rssi;
    
    @ColumnInfo(name="LQI")
    @Column(name="LQI", length=20)
    private Integer lqi;

    @ColumnInfo(name="ETX")
    @Column(name="ETX", length=20)
    private Integer etx;
/*
    @ColumnInfo(name="Cpu Usage")
    @Column(name="Cpu_Usage", length=20)
    private Integer cpuUsage;

    @ColumnInfo(name="Memory Usage")
    @Column(name="Memory_Usage", length=20)
    private Integer memoryUsage;*/
    
    public String getBaseStationAddress() {
        return baseStationAddress;
    }

    public void setBaseStationAddress(String baseStationAddress) {
        this.baseStationAddress = baseStationAddress;
    }

    public String getIpv6Address() {
        return ipv6Address;
    }

    public void setIpv6Address(String ipv6Address) {
        this.ipv6Address = ipv6Address;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    public Integer getHopsToBaseStation() {
        return hopsToBaseStation;
    }

    public void setHopsToBaseStation(Integer hopsToBaseStation) {
        this.hopsToBaseStation = hopsToBaseStation;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Integer getBandWidth() {
        return bandWidth;
    }

    public void setBandWidth(Integer bandWidth) {
        this.bandWidth = bandWidth;
    }

    public Integer getListenPort() {
        return listenPort;
    }

    public void setListenPort(Integer listenPort) {
        this.listenPort = listenPort;
    }

    public Integer getPanId() {
        return panId;
    }

    public void setPanId(Integer panId) {
        this.panId = panId;
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

    public Integer getMeteringTimeRange() {
        return meteringTimeRange;
    }

    public void setMeteringTimeRange(Integer meteringTimeRange) {
        this.meteringTimeRange = meteringTimeRange;
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

    public Integer getRssi() {
        return rssi;
    }

    public void setRssi(Integer rssi) {
        this.rssi = rssi;
    }

	public Integer getLqi() {
		return lqi;
	}

	public void setLqi(Integer lqi) {
		this.lqi = lqi;
	}

	public Integer getEtx() {
		return etx;
	}

	public void setEtx(Integer etx) {
		this.etx = etx;
	}
/*
	public Integer getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(Integer cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public Integer getMemoryUsage() {
		return memoryUsage;
	}

	public void setMemoryUsage(Integer memoryUsage) {
		this.memoryUsage = memoryUsage;
	}
    */
}