package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.aimir.annotation.ColumnInfo;


/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>PLC Interface Unit (PLC- Power Line Carrier 방식의 통신인터페이스를 가진 모뎀)</p>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@DiscriminatorValue("PLCIU")
public class PLCIU extends Modem {

    private static final long serialVersionUID = 6195190002090836902L;    
    
    @ColumnInfo(name="ipv6Address", descr="IPv6 Modem Ipv6 Address")
    @Column(name="IPV6_ADDRESS", length=64)
    private String ipv6Address;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_DESCR", length=250)
    private String sysDescr;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_OBJECT_ID", length=25)
    private String sysObjectId;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_UPTIME", length=14)
    private String sysUptime;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_CONTACT", length=25)
    private String sysContact;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_NAME", length=25)
    private String sysName;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_LOCATION", length=25)
    private String sysLocation;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_SERVICE", length=10)
    private Integer sysService;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_NODE_TYPE", length=10)
    private Integer sysNodeType;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_FW_VERSION", length=25)
    private String sysFwVersion;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_RESET", length=10)
    private Integer sysReset;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_FACTORY_RESET", length=10)
    private Integer sysFactoryReset;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_STATUS", length=10)
    private String sysStatus;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_RTS_CTSENABLE")
    private Boolean sysRtsCtsEnable;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_SERIAL_RATE", length=10)
    private Integer sysSerialRate;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_SERIAL_WORDBIT", length=10)
    private Integer sysSerialWordBit;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_SERIAL_STOP_BIT", length=10)
    private Integer sysSerialStopBit;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_SERIAL_PARITY_TYPE", length=10)
    private Integer sysSerialParityType;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_USE_DHCP", length=10)
    private Integer sysUseDhcp;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_IP_ADDR", length=16)
    private String sysIpAddr;

    @ColumnInfo(name="", descr="")
    @Column(name="SYS_PORT", length=10)
    private String sysPort;


    public String getSysDescr() {
        return sysDescr;
    }

    public void setSysDescr(String sysDescr) {
        this.sysDescr = sysDescr;
    }

    public String getSysObjectId() {
        return sysObjectId;
    }

    public void setSysObjectId(String sysObjectId) {
        this.sysObjectId = sysObjectId;
    }

    public String getSysUptime() {
        return sysUptime;
    }

    public void setSysUptime(String sysUptime) {
        this.sysUptime = sysUptime;
    }

    public String getSysContact() {
        return sysContact;
    }

    public void setSysContact(String sysContact) {
        this.sysContact = sysContact;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getSysLocation() {
        return sysLocation;
    }

    public void setSysLocation(String sysLocation) {
        this.sysLocation = sysLocation;
    }

    public Integer getSysService() {
        return sysService;
    }

    public void setSysService(Integer sysService) {
        this.sysService = sysService;
    }

    public Integer getSysNodeType() {
        return sysNodeType;
    }

    public void setSysNodeType(Integer sysNodeType) {
        this.sysNodeType = sysNodeType;
    }

    public String getSysFwVersion() {
        return sysFwVersion;
    }

    public void setSysFwVersion(String sysFwVersion) {
        this.sysFwVersion = sysFwVersion;
    }

    public Integer getSysReset() {
        return sysReset;
    }

    public void setSysReset(Integer sysReset) {
        this.sysReset = sysReset;
    }

    public Integer getSysFactoryReset() {
        return sysFactoryReset;
    }

    public void setSysFactoryReset(Integer sysFactoryReset) {
        this.sysFactoryReset = sysFactoryReset;
    }

    public String getSysStatus() {
        return sysStatus;
    }

    public void setSysStatus(String sysStatus) {
        this.sysStatus = sysStatus;
    }

    public Boolean getSysRtsCtsEnable() {
        return sysRtsCtsEnable;
    }

    public void setSysRtsCtsEnable(Boolean sysRtsCtsEnable) {
        this.sysRtsCtsEnable = sysRtsCtsEnable;
    }

    public Integer getSysSerialRate() {
        return sysSerialRate;
    }

    public void setSysSerialRate(Integer sysSerialRate) {
        this.sysSerialRate = sysSerialRate;
    }

    public Integer getSysSerialWordBit() {
        return sysSerialWordBit;
    }

    public void setSysSerialWordBit(Integer sysSerialWordBit) {
        this.sysSerialWordBit = sysSerialWordBit;
    }

    public Integer getSysSerialStopBit() {
        return sysSerialStopBit;
    }

    public void setSysSerialStopBit(Integer sysSerialStopBit) {
        this.sysSerialStopBit = sysSerialStopBit;
    }

    public Integer getSysSerialParityType() {
        return sysSerialParityType;
    }

    public void setSysSerialParityType(Integer sysSerialParityType) {
        this.sysSerialParityType = sysSerialParityType;
    }

    public Integer getSysUseDhcp() {
        return sysUseDhcp;
    }

    public void setSysUseDhcp(Integer sysUseDhcp) {
        this.sysUseDhcp = sysUseDhcp;
    }

    public String getSysIpAddr() {
        return sysIpAddr;
    }

    public void setSysIpAddr(String sysIpAddr) {
        this.sysIpAddr = sysIpAddr;
    }

    public String getSysPort() {
        return sysPort;
    }

    public void setSysPort(String sysPort) {
        this.sysPort = sysPort;
    }
    
    public String getIpv6Address() {
        return ipv6Address;
    }

    public void setIpv6Address(String ipv6Address) {
        this.ipv6Address = ipv6Address;
    }
}