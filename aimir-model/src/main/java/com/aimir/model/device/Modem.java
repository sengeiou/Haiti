package com.aimir.model.device;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.audit.IAuditable;
import com.aimir.constants.CommonConstants.Interface;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>AMI 네트웍 상에서 통신기능을 가지고 있는 모뎀의 정보</p>
 * <p>용도 및 통신 방식에 따라 여러가지 타입으로 분류할 수 있다.</p>
 * <pre>
 * 노드유형 
 * - 미터(전자,펄스),M-Bus,리피터,IHD,PLC,가전기기,경보장치, ACD 
 * 파워파입 
 * - 전기,배터리,솔라 
 * 통신유형 
 * - Zigbee 
 * - CDMA(GSM):이런 경우 MCU와 붙지 않는다. 
 * - PLC 
 * 망타입 
 * - Zigbee:RFD,FFD 
 * - PLC, CDMA(GSM) : 없음 
 * 노드가 펄스식 미터인 경우 
 * - 검침주기(업로드는 즉시) 
 * </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="modem">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}baseObject">
 *       &lt;sequence>
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="amiNetworkAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="amiNetworkDepth" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="commState" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="modemStatusCodeId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="currentThreshold" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="deviceSerial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fwRevision" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fwVer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gpioX" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="gpioY" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="gpioZ" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="hwVer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="idType" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="installDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="installedSiteImg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="interfaceType" type="{http://server.ws.command.fep.aimir.com/}interface" minOccurs="0"/>
 *         &lt;element name="ipAddr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastLinkTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastResetCode" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="locationId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="lpPeriod" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="macAddr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mcuId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="modelId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="nameSpace" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nodeKind" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nodeType" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="parentModemId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="powerThreshold" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="protocolVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="resetCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="rfPower" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="supplierId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="swVer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="bootLoaderVer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="zdzdIfVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "modem", propOrder = {
        "modemType",
        "protocolType",
        "meter",
        "address",
        "amiNetworkAddress",
        "amiNetworkDepth",
        "commState",
        "modemStatusCodeId",
        "currentThreshold",
        "deviceSerial",
        "fwRevision",
        "fwVer",
        "gpioX",
        "gpioY",
        "gpioZ",
        "hwVer",
        "id",
        "idType",
        "installDate",
        "installedSiteImg",
        "interfaceType",
        "ipAddr",
        "lastLinkTime",
        "lastResetCode",
        "locationId",
        "lpPeriod",
        "macAddr",
        "mcuId",
        "modelId",
        "nameSpace",
        "nodeKind",
        "nodeType",
        "parentModemId",
        "powerThreshold",
        "protocolVersion",
        "resetCount",
        "rfPower",
        "supplierId",
        "swVer",
        "bootLoaderVer",
        "zdzdIfVersion",
        "gs1",
        "po",
        "iccId",
        "manufacturedDate",
        "imei",
        "imsi",
        "simNumber",
        "phoneNumber",
 //       "rssi",
//        "lqi",
//        "etx",
        "cpuUsage",
        "memoryUsage"
})

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="MODEM",discriminatorType=DiscriminatorType.STRING)
@Table(name="MODEM")
//@Indexes({
//    @Index(name="IDX_MODEM_01", columnNames={"IP_ADDR"}),
//   @Index(name="IDX_MODEM_02", columnNames={"IPV6_ADDRESS"})
//})
public class Modem extends BaseObject implements JSONString, IAuditable { 
    
    private static final long serialVersionUID = -299021006738263346L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_MODEM")
    @SequenceGenerator(name="SEQ_MODEM", sequenceName="SEQ_MODEM", allocationSize=1)
    private Integer id;

//    @Version
//    Integer version;

    @ColumnInfo(name="모뎀시리얼", descr="사람 혹은 장비에서 올라오는 값. 반드시 있어야 하는 값이며, 중복 되는 값은 사용할 수 없음")
    @Column(name="DEVICE_SERIAL", nullable=false, unique=true)
    private String deviceSerial;    

    @ColumnInfo(name="", descr="sensor type (code:zru, zeupls, mmiu, ieiu, zeupls, zmu, ihd, acd, hmu)")
    @Enumerated(EnumType.STRING)
    @Column(name="MODEM_TYPE")
    private ModemType modemType; 

    @XmlTransient
    @ColumnInfo(name="모뎀아이디", descr="모뎀 테이블의 ID 혹은  NULL : 재귀호출을 위함")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="MODEM_ID")
    @ReferencedBy(name="deviceSerial")
    private Modem modem;
    
    @Column(name="MODEM_ID", nullable=true, updatable=false, insertable=false)
    private Integer parentModemId;

    @ColumnInfo(name="미터아이디", descr="미터 테이블의 ID 혹은  NULL")
    @OneToMany(fetch=FetchType.LAZY, mappedBy="modem")
    private Set<Meter> meter = new HashSet<Meter>(0);

    @XmlTransient
    @ColumnInfo(name="MCU 아이디", descr="MCU 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch=FetchType.LAZY)    
    @JoinColumn(name="MCU_ID")
    @ReferencedBy(name="sysID")
    private MCU mcu;
    
    @Column(name="MCU_ID", nullable=true, updatable=false, insertable=false)
    private Integer mcuId;

    @XmlTransient
    @ColumnInfo(name="공급사아이디", descr="공급사 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="SUPPLIER_ID")
    @ReferencedBy(name="name")
    private Supplier supplier;
    
    @Column(name="SUPPLIER_ID", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;

    @XmlTransient
    @ColumnInfo(name="모뎀 모델", descr="미터 제조사 모델의 ID 혹은  NULL")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="DEVICEMODEL_ID")
    @ReferencedBy(name="name")
    private DeviceModel model;
    
    @Column(name="DEVICEMODEL_ID", nullable=true, updatable=false, insertable=false)
    private Integer modelId;

    @ColumnInfo(name="노드타입", descr="Modem에 연결된 Node의 타입(전기,가스,수도,열량,고압,PLC)")
    @Column(name="NODE_TYPE", length=5)
    private Integer nodeType;

    @ColumnInfo(name="", descr="model name (Modem에 연결된 Node의 종류를 기술-GE-SM-XXX, AIDON-55X0, NAMR-H101MG 등), 배포 시 model 값으로 등록 됨.")
    @Column(name="NODE_KIND", length=20)
    private String nodeKind;
    
    @ColumnInfo(name="", descr="Zigbee Interface Version")
    @Column(name="ZDZD_IF_VERSION", length=20)
    private String zdzdIfVersion;
   
    @ColumnInfo(name="", descr="protocol type (CDMA,GSM,GPRS,PSTN,LAN,ZigBee,WiMAX,Serial,PLC,Bluetooth,SMS)")
    @Enumerated(EnumType.STRING)
    @Column(name="PROTOCOL_TYPE")
    private Protocol protocolType;

    @ColumnInfo(name="소프트웨어 버젼", descr="")
    @Column(name="SW_VER", length=10)
    private String swVer;

    @ColumnInfo(name="하드웨어 버젼", descr="")
    @Column(name="HW_VER", length=10)
    private String hwVer;

    @ColumnInfo(name="펌웨어버전", descr="")
    @Column(name="FW_VER", length=10)
    private String fwVer;
    
    @ColumnInfo(name="펌웨어 리비젼", descr="")
    @Column(name="FW_REVISION", length=20)
    private String fwRevision;

    @ColumnInfo(name="통신상태", descr="Code")
    @Column(name="COMM_STATE", length=10)
    private Integer commState;

    /**
     * 모뎀상태에 따라 상태값이 다르므로 코드를 참조한다.
     * <br>유형에 따른 미터 상태가 없으면 Code 1.2.7 을 이용한다.
     */
    @XmlTransient
    @ColumnInfo(name="모뎀 삭제 상태", descr="코드 테이블의 ID 혹은  NULL : Code 1.2.7 참조", view=@Scope(create=true, read=true, update=true))
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="MODEM_STATUS")
    @ReferencedBy(name="code")
    private Code modemStatus;
    
    @Column(name="MODEM_STATUS", nullable=true, updatable=false, insertable=false)
    private Integer modemStatusCodeId;

    @ColumnInfo(name="프로토콜 버젼")
    @Column(name="PROTOCOL_VERSION", length=10)
    private String protocolVersion;  //protocol Version

    @ColumnInfo(name="설치일자", descr="YYYYMMDDHHMMSS")
    @Column(name="INSTALL_DATE", length=14)
    private String installDate;

    @ColumnInfo(name="마지막 통신 일자", descr="YYYYMMDDHHMMSS")
    @Column(name="LAST_LINK_TIME", length=14)
    private String lastLinkTime;  
    
    @ColumnInfo(name="무선 신호 세기(dBm) Signal Strength 모뎀 타입이 MMIU, IEIU인 경우에는 dbm으로 그외는V로 표시한다.PLC는 해당사항 없음")
    @Column(name="RF_POWER")
    private Long rfPower;    

    @ColumnInfo(name="아이피")
    @Column(name="IP_ADDR", length=64)
    private String ipAddr;

    @ColumnInfo(name="맥 어드레스")
    @Column(name="MAC_ADDR", length=20)
    private String macAddr;    

    @ColumnInfo(name="GPS X")
    @Column(name="GPIOX", length=10)
    private Double gpioX;

    @ColumnInfo(name="GPS Y")
    @Column(name="GPIOY", length=10)
    private Double gpioY;

    @ColumnInfo(name="GPS Z")
    @Column(name="GPIOZ", length=10)
    private Double gpioZ;   

    @ColumnInfo(name="설치된 이미지")
    @Column(name="INSTALLED_SITE_IMG", length=100)
    private String installedSiteImg;    

    @ColumnInfo(name="주소")
    @Column(name="ADDRESS", length=255)
    private String address;

    @ColumnInfo(name="전력 임계치")
    @Column(name="POWER_THRESHOLD")
    private Double powerThreshold;

    @ColumnInfo(name="기존 임계치")
    @Column(name="CURRENT_THRESHOLD")
    private Double currentThreshold;
    
    @ColumnInfo(name="", descr="Reset Count")
    @Column(name="RESET_COUNT", length=10)
    private Integer resetCount;

    @ColumnInfo(name="", descr="last reset code(reset reason)")
    @Column(name="LAST_RESET_CODE", length=10)
    private Integer lastResetCode;   
    
    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="모뎀이 LP 저장하는 주기  0: No LP, 1: 60분 2: 30분 4: 15분")
    @Column(name="LP_PERIOD", length=2)
    private Integer lpPeriod;
    
    @ColumnInfo(name="interface", descr="modem interface type (IF4, AMU, etc)")
    @Enumerated(EnumType.STRING)
    @Column(name="INTERFACE_TYPE")
    private Interface interfaceType;

    @XmlTransient
    @ColumnInfo(name="",view=@Scope(create=true, read=true, update=true),descr="")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="LOCATION_ID")
    @ReferencedBy(name="name")
    private Location location;
    
    @Column(name="LOCATION_ID", nullable=true, updatable=false, insertable=false)
    private Integer locationId;
    
    @ColumnInfo(name="deviceIdType", descr="Id type (IPv4, IPv6, MAC, EUI64, SignedNumber, UnsignedNumber, ASCII, BCD, Byte Stream)")
    @Column(name="ID_TYPE")
    private Integer idType;
    
    @ColumnInfo(name="AMI Virtual Network Address Depth")
    @Column(name="AMI_NETWORK_DEPTH", length=2)
    private Integer amiNetworkDepth;  //AMI Virtual Network Depth
    
    @ColumnInfo(name="AMI Virtual Network Address")
    @Column(name="AMI_NETWORK_ADDRESS", length=128)
    private String amiNetworkAddress;  //AMI Virtual Network Address
    
    @ColumnInfo(name="communication protocol namespace for OID mapping")
    @Column(name="NAME_SPACE", length=10)
    private String nameSpace;

    @ColumnInfo(descr="바코드 정보")
    @Column(name="GS1", length=20)
	private String gs1;
    
    @ColumnInfo(name="Purchase Order")
    @Column(name="PO", length=20)
    private String po;
    
    @ColumnInfo(name="ICC ID")
    @Column(name="ICC_ID", length=36)
    private String iccId;
    
    @ColumnInfo(name="제조일자", view=@Scope(create=true, read=true, update=true), descr="제조일자")
    @Column(name="MANUFACTURED_DATE", length=8)
    private String manufacturedDate;
    
    @ColumnInfo(name="IMEI")
    @Column(name="IMEI", length=36)
    private String imei;    
    
    @ColumnInfo(name="IMSI")
    @Column(name="IMSI", length=36)
    private String imsi;
    
    @ColumnInfo(name="심카드 번호", view=@Scope(create=true, read=true, update=true))
    @Column(name="SIM_NUMBER",length=24)
    private String simNumber;
    
    @ColumnInfo(name="전화번호", view=@Scope(create=true, read=true, update=true))
    @Column(name="PHONE_NUMBER", length=20)
    private String phoneNumber;

    @ColumnInfo(name="Cpu Usage")
    @Column(name="CPU_USAGE", length=20)
    private Integer cpuUsage;

    @ColumnInfo(name="Memory Usage")
    @Column(name="MEMORY_USAGE", length=20)
    private Integer memoryUsage;
    
    @ColumnInfo(name="Boot Loader Version")
    @Column(name="BOOTLOADER_VER", length=100)
    private String bootLoaderVer;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ModemType getModemType() {
        return modemType;
    }

    public void setModemType(String modemType) {
        this.modemType = ModemType.valueOf(modemType);
    }
    

    public void setMeter(Set<Meter> meter) {
        this.meter = meter;
    }

    @XmlTransient
    public Set<Meter> getMeter() {
        return meter;
    }

    public String getDeviceSerial() {
        return deviceSerial;
    }

    public void setDeviceSerial(String deviceSerial) {
        this.deviceSerial = deviceSerial;
    }

    @XmlTransient
    public Modem getModem() {
        return modem;
    }

    public void setModem(Modem modem) {
        this.modem = modem;
    }

    @XmlTransient
    public MCU getMcu() {
        return mcu;
    }

    public void setMcu(MCU mcu) {
        this.mcu = mcu;
    }

    @XmlTransient
    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Integer getNodeType() {
        return nodeType;
    }

    public void setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeKind() {
        return nodeKind;
    }

    public void setNodeKind(String nodeKind) {
        this.nodeKind = nodeKind;
    }

    public String getZdzdIfVersion() {
        return zdzdIfVersion;
    }

    public void setZdzdIfVersion(String zdzdIfVersion) {
        this.zdzdIfVersion = zdzdIfVersion;
    }

    public Protocol getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = Protocol.valueOf(protocolType);
    }

    public String getSwVer() {
        return swVer;
    }

    public void setSwVer(String swVer) {
        this.swVer = swVer;
    }

    public String getHwVer() {
        return hwVer;
    }

    public void setHwVer(String hwVer) {
        this.hwVer = hwVer;
    }

    public String getFwRevision() {
        return fwRevision;
    }

    public void setFwRevision(String fwRevision) {
        this.fwRevision = fwRevision;
    }

    public String getFwVer() {
        return fwVer;
    }

    public void setFwVer(String fwVer) {
        this.fwVer = fwVer;
    }

    public void setResetCount(Integer resetCount) {
        this.resetCount = resetCount;
    }

    public void setLastResetCode(Integer lastResetCode) {
        this.lastResetCode = lastResetCode;
    }

    public Integer getCommState() {
        return commState;
    }

    public void setCommState(Integer commState) {
        this.commState = commState;
    }
    
    @XmlTransient
    public Code getModemStatus() {
        return modemStatus;
    }

    public void setModemStatus(Code modemStatus) {
        this.modemStatus = modemStatus;
    }

    public Integer getModemStatusCodeId() {
        return modemStatusCodeId;
    }

    public void setModemStatusCodeId(Integer modemStatusCodeId) {
        this.modemStatusCodeId = modemStatusCodeId;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getInstallDate() {
        return installDate;
    }

    public void setInstallDate(String installDate) {
        this.installDate = installDate;
    }

    public String getLastLinkTime() {
        return lastLinkTime;
    }

    public void setLastLinkTime(String lastLinkTime) {
        this.lastLinkTime = lastLinkTime;
    }

    public Long getRfPower() {
        return rfPower;
    }

    public void setRfPower(Long rfPower) {
        this.rfPower = rfPower;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public Double getGpioX() {
        return gpioX;
    }

    public void setGpioX(Double gpioX) {
        this.gpioX = gpioX;
    }

    public Double getGpioY() {
        return gpioY;
    }

    public void setGpioY(Double gpioY) {
        this.gpioY = gpioY;
    }

    public Double getGpioZ() {
        return gpioZ;
    }

    public void setGpioZ(Double gpioZ) {
        this.gpioZ = gpioZ;
    }

    public String getInstalledSiteImg() {
        return installedSiteImg;
    }

    public void setInstalledSiteImg(String installedSiteImg) {
        this.installedSiteImg = installedSiteImg;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getPowerThreshold() {
        return powerThreshold;
    }

    public void setPowerThreshold(Double powerThreshold) {
        this.powerThreshold = powerThreshold;
    }

    public Double getCurrentThreshold() {
        return currentThreshold;
    }

    public void setCurrentThreshold(Double currentThreshold) {
        this.currentThreshold = currentThreshold;
    }

    public Integer getResetCount() {
        return resetCount;
    }

    public Integer getLastResetCode() {
        return lastResetCode;
    }

    public Integer getLpPeriod() {
        return lpPeriod;
    }

    public void setLpPeriod(Integer lpPeriod) {
        this.lpPeriod = lpPeriod;
    }
    
    public Interface getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(Interface interfaceType) {
        this.interfaceType = interfaceType;
    }

    @XmlTransient
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getParentModemId() {
        return parentModemId;
    }

    public void setParentModemId(Integer parentModemId) {
        this.parentModemId = parentModemId;
    }

    public Integer getMcuId() {
        return mcuId;
    }

    public void setMcuId(Integer mcuId) {
        this.mcuId = mcuId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getAmiNetworkDepth() {
        return amiNetworkDepth;
    }

    public void setAmiNetworkDepth(Integer amiNetworkDepth) {
        this.amiNetworkDepth = amiNetworkDepth;
    }

    public String getAmiNetworkAddress() {
        return amiNetworkAddress;
    }

    public void setAmiNetworkAddress(String amiNetworkAddress) {
        this.amiNetworkAddress = amiNetworkAddress;
    }   

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }
    
    public String getGs1() {
        return gs1;
    }

    public void setGs1(String gs1) {
        this.gs1 = gs1;
    }
    
    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }
    
    public String getIccId() {
        return iccId;
    }

    public void setIccId(String iccId) {
        this.iccId = iccId;
    }
    
    public String getManufacturedDate() {
        return manufacturedDate;
    }

    public void setManufacturedDate(String manufacturedDate) {
        this.manufacturedDate = manufacturedDate;
    }
    
    public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getSimNumber() {
		return simNumber;
	}

	public void setSimNumber(String simNumber) {
		this.simNumber = simNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
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

	public String getBootLoaderVer() {
		return bootLoaderVer;
	}

	public void setBootLoaderVer(String bootLoaderVer) {
		this.bootLoaderVer = bootLoaderVer;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result
                + ((commState == null) ? 0 : commState.hashCode());
        result = prime
                * result
                + ((currentThreshold == null) ? 0 : currentThreshold.hashCode());
        result = prime * result
                + ((deviceSerial == null) ? 0 : deviceSerial.hashCode());
        result = prime * result + ((gpioX == null) ? 0 : gpioX.hashCode());
        result = prime * result + ((gpioY == null) ? 0 : gpioY.hashCode());
        result = prime * result + ((gpioZ == null) ? 0 : gpioZ.hashCode());
        result = prime * result + ((hwVer == null) ? 0 : hwVer.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((installDate == null) ? 0 : installDate.hashCode());
        result = prime
                * result
                + ((installedSiteImg == null) ? 0 : installedSiteImg.hashCode());
        result = prime * result + ((ipAddr == null) ? 0 : ipAddr.hashCode());
        result = prime * result
                + ((lastLinkTime == null) ? 0 : lastLinkTime.hashCode());
        result = prime * result
                + ((lastResetCode == null) ? 0 : lastResetCode.hashCode());
        result = prime * result + ((macAddr == null) ? 0 : macAddr.hashCode());
        result = prime * result + ((mcu == null) ? 0 : mcu.hashCode());
        // result = prime * result + ((modem == null) ? 0 : modem.hashCode());
        result = prime * result
                + ((modemType == null) ? 0 : modemType.hashCode());
        result = prime * result
                + ((nodeType == null) ? 0 : nodeType.hashCode());
        result = prime * result
                + ((powerThreshold == null) ? 0 : powerThreshold.hashCode());
        result = prime * result
                + ((protocolType == null) ? 0 : protocolType.hashCode());
        result = prime * result
                + ((protocolVersion == null) ? 0 : protocolVersion.hashCode());
        result = prime * result
                + ((resetCount == null) ? 0 : resetCount.hashCode());
        result = prime * result + ((rfPower == null) ? 0 : rfPower.hashCode());
        result = prime * result
                + ((supplier == null) ? 0 : supplier.hashCode());
        result = prime * result + ((fwVer == null)? 0 : fwVer.hashCode());
        result = prime * result
                + ((fwRevision == null) ? 0 : fwRevision.hashCode());
        result = prime * result + ((swVer == null) ? 0 : swVer.hashCode());
        result = prime * result + ((bootLoaderVer == null) ? 0 : bootLoaderVer.hashCode());
//        result = prime * result + ((version == null) ? 0 : version.hashCode());
        result = prime * result + ((interfaceType == null) ? 0 : interfaceType.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((amiNetworkAddress == null) ? 0 : amiNetworkAddress.hashCode());
        result = prime * result + ((amiNetworkDepth == null) ? 0 : amiNetworkDepth.hashCode());
        result = prime * result + ((nameSpace == null) ? 0 : nameSpace.hashCode());
        result = prime * result + ((gs1 == null) ? 0 : gs1.hashCode());
        result = prime * result + ((po == null) ? 0 : po.hashCode());
        result = prime * result + ((iccId == null) ? 0 : iccId.hashCode());
        result = prime * result + ((manufacturedDate == null) ? 0 : manufacturedDate.hashCode());
        result = prime * result + ((imei == null) ? 0 : imei.hashCode());
        result = prime * result + ((imsi == null) ? 0 : imsi.hashCode());
        result = prime * result + ((simNumber == null) ? 0 : simNumber.hashCode());
        result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
        //result = prime * result + (( lqi == null) ? 0 :  lqi.hashCode());
       // result = prime * result + ((etx == null) ? 0 : etx.hashCode());
        result = prime * result + ((cpuUsage == null) ? 0 : cpuUsage.hashCode());
        result = prime * result + ((memoryUsage == null) ? 0 : memoryUsage.hashCode());

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        Modem other = (Modem) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (commState == null) {
            if (other.commState != null)
                return false;
        } else if (!commState.equals(other.commState))
            return false;
        if (currentThreshold == null) {
            if (other.currentThreshold != null)
                return false;
        } else if (!currentThreshold.equals(other.currentThreshold))
            return false;
        if (deviceSerial == null) {
            if (other.deviceSerial != null)
                return false;
        } else if (!deviceSerial.equals(other.deviceSerial))
            return false;
        if (gpioX == null) {
            if (other.gpioX != null)
                return false;
        } else if (!gpioX.equals(other.gpioX))
            return false;
        if (gpioY == null) {
            if (other.gpioY != null)
                return false;
        } else if (!gpioY.equals(other.gpioY))
            return false;
        if (gpioZ == null) {
            if (other.gpioZ != null)
                return false;
        } else if (!gpioZ.equals(other.gpioZ))
            return false;
        if (hwVer == null) {
            if (other.hwVer != null)
                return false;
        } else if (!hwVer.equals(other.hwVer))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (installDate == null) {
            if (other.installDate != null)
                return false;
        } else if (!installDate.equals(other.installDate))
            return false;
        if (installedSiteImg == null) {
            if (other.installedSiteImg != null)
                return false;
        } else if (!installedSiteImg.equals(other.installedSiteImg))
            return false;
        if (ipAddr == null) {
            if (other.ipAddr != null)
                return false;
        } else if (!ipAddr.equals(other.ipAddr))
            return false;
        if (lastLinkTime == null) {
            if (other.lastLinkTime != null)
                return false;
        } else if (!lastLinkTime.equals(other.lastLinkTime))
            return false;
        if (lastResetCode == null) {
            if (other.lastResetCode != null)
                return false;
        } else if (!lastResetCode.equals(other.lastResetCode))
            return false;
        if (macAddr == null) {
            if (other.macAddr != null)
                return false;
        } else if (!macAddr.equals(other.macAddr))
            return false;
        if (mcu == null) {
            if (other.mcu != null)
                return false;
        } else if (!mcu.equals(other.mcu))
            return false;
        if (modem == null) {
            if (other.modem != null)
                return false;
        } else if (!modem.equals(other.modem))
            return false;
        if (modemType == null) {
            if (other.modemType != null)
                return false;
        } else if (!modemType.equals(other.modemType))
            return false;
        if (nodeType == null) {
            if (other.nodeType != null)
                return false;
        } else if (!nodeType.equals(other.nodeType))
            return false;
        if (powerThreshold == null) {
            if (other.powerThreshold != null)
                return false;
        } else if (!powerThreshold.equals(other.powerThreshold))
            return false;
        if (protocolType == null) {
            if (other.protocolType != null)
                return false;
        } else if (!protocolType.equals(other.protocolType))
            return false;
        if (protocolVersion == null) {
            if (other.protocolVersion != null)
                return false;
        } else if (!protocolVersion.equals(other.protocolVersion))
            return false;
        if (resetCount == null) {
            if (other.resetCount != null)
                return false;
        } else if (!resetCount.equals(other.resetCount))
            return false;
        if (rfPower == null) {
            if (other.rfPower != null)
                return false;
        } else if (!rfPower.equals(other.rfPower))
            return false;
        if (supplier == null) {
            if (other.supplier != null)
                return false;
        } else if (!supplier.equals(other.supplier))
            return false;
        if (fwVer == null) {
            if (other.fwVer != null)
                return false;
        } else if (!fwVer.equals(other.fwVer))
        if (fwRevision == null) {
            if (other.fwRevision != null)
                return false;
        } else if (!fwRevision.equals(other.fwRevision))
            return false;
        if (swVer == null) {
            if (other.swVer != null)
                return false;
        } else if (!swVer.equals(other.swVer))
            return false;
//        if (version == null) {
//            if (other.version != null)
//                return false;
//        } else if (!version.equals(other.version))
//            return false;
        if (interfaceType == null) {
            if (other.interfaceType != null)
                return false;
        } else if (!interfaceType.equals(other.interfaceType))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;        
        if (amiNetworkAddress == null) {
            if (other.amiNetworkAddress != null)
                return false;
        } else if (!amiNetworkAddress.equals(other.amiNetworkAddress))
            return false;
        if (amiNetworkDepth == null) {
            if (other.amiNetworkDepth != null)
                return false;
        } else if (!amiNetworkDepth.equals(other.amiNetworkDepth))
            return false;
        if (nameSpace == null) {
            if (other.nameSpace != null)
                return false;
        } else if (!nameSpace.equals(other.nameSpace))
            return false;
        if (bootLoaderVer == null) {
            if (other.bootLoaderVer != null)
                return false;
        } else if (!bootLoaderVer.equals(other.bootLoaderVer))
            return false;
        if (gs1 == null) {
            if (other.gs1 != null)
                return false;
        } else if (!gs1.equals(other.gs1))
            return false;
        if (po == null) {
            if (other.po != null)
                return false;
        } else if (!po.equals(other.po))
            return false;
        if (iccId == null) {
            if (other.iccId != null)
                return false;
        } else if (!iccId.equals(other.iccId))
            return false;
        if (manufacturedDate == null) {
            if (other.manufacturedDate != null)
                return false;
        } else if (!manufacturedDate.equals(other.manufacturedDate))
            return false;
        if (imei == null) {
            if (other.imei != null)
                return false;
        } else if (!imei.equals(other.imei))
            return false;
        if (imsi == null) {
            if (other.imsi != null)
                return false;
        } else if (!imsi.equals(other.imsi))
            return false;
        if (simNumber == null) {
            if (other.simNumber != null)
                return false;
        } else if (!simNumber.equals(other.simNumber))
            return false;
        if (phoneNumber == null) {
            if (other.phoneNumber != null)
                return false;
        } else if (!phoneNumber.equals(other.phoneNumber))
            return false;
/*        if (lqi == null) {
        	if (other. lqi != null)
        		return false;
        } else if (! lqi.equals(other. lqi))
        	return false;*/
/*        if (etx == null) {
        	if (other.etx != null)
        		return false;
        } else if (!etx.equals(other.etx))
        	return false;*/
        if (cpuUsage == null) {
        	if (other.cpuUsage != null)
        		return false;
        } else if (!cpuUsage.equals(other.cpuUsage))
        	return false;
        if (memoryUsage == null) {
        	if (other.memoryUsage != null)
        		return false;
        } else if (!memoryUsage.equals(other.memoryUsage))
        	return false;
        
        return true;
    }

//    @Override
//    public String toString() {
//     	return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
//    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Modem [id=");
		builder.append(id);
		builder.append(", deviceSerial=");
		builder.append(deviceSerial);
		builder.append(", modem=");
		builder.append(modem == null ? "" : modem.getDeviceSerial());
		builder.append(", parentModemId=");
		builder.append(parentModemId);
		builder.append(", mcu=");
		builder.append(mcu == null ? "" : mcu.getSysID());
		builder.append(", mcuId=");
		builder.append(mcuId);
		builder.append(", supplier=");
		builder.append(supplier);
		builder.append(", supplierId=");
		builder.append(supplierId);
		builder.append(", model=");
		builder.append(model);
		builder.append(", modelId=");
		builder.append(modelId);
		builder.append(", nodeType=");
		builder.append(nodeType);
		builder.append(", nodeKind=");
		builder.append(nodeKind);
		builder.append(", zdzdIfVersion=");
		builder.append(zdzdIfVersion);
		builder.append(", protocolType=");
		builder.append(protocolType);
		builder.append(", swVer=");
		builder.append(swVer);
		builder.append(", hwVer=");
		builder.append(hwVer);
		builder.append(", fwVer=");
		builder.append(fwVer);
		builder.append(", fwRevision=");
		builder.append(fwRevision);
		builder.append(", commState=");
		builder.append(commState);
		builder.append(", modemStatus=");
		builder.append(modemStatus);
		builder.append(", modemStatusCodeId=");
		builder.append(modemStatusCodeId);
		builder.append(", protocolVersion=");
		builder.append(protocolVersion);
		builder.append(", installDate=");
		builder.append(installDate);
		builder.append(", lastLinkTime=");
		builder.append(lastLinkTime);
		builder.append(", rfPower=");
		builder.append(rfPower);
		builder.append(", ipAddr=");
		builder.append(ipAddr);
		builder.append(", macAddr=");
		builder.append(macAddr);
		builder.append(", gpioX=");
		builder.append(gpioX);
		builder.append(", gpioY=");
		builder.append(gpioY);
		builder.append(", gpioZ=");
		builder.append(gpioZ);
		builder.append(", installedSiteImg=");
		builder.append(installedSiteImg);
		builder.append(", address=");
		builder.append(address);
		builder.append(", powerThreshold=");
		builder.append(powerThreshold);
		builder.append(", currentThreshold=");
		builder.append(currentThreshold);
		builder.append(", resetCount=");
		builder.append(resetCount);
		builder.append(", lastResetCode=");
		builder.append(lastResetCode);
		builder.append(", modemType=");
		builder.append(modemType == null ? "" : modemType.name());
		builder.append(", lpPeriod=");
		builder.append(lpPeriod);
		builder.append(", interfaceType=");
		builder.append(interfaceType);
		builder.append(", location=");
		builder.append(location == null ? "" : location.getName());
		builder.append(", locationId=");
		builder.append(locationId);
		builder.append(", idType=");
		builder.append(idType);
		builder.append(", amiNetworkDepth=");
		builder.append(amiNetworkDepth);
		builder.append(", amiNetworkAddress=");
		builder.append(amiNetworkAddress);
		builder.append(", nameSpace=");
		builder.append(nameSpace);
		builder.append(", gs1=");
		builder.append(gs1);
		builder.append(", po=");
		builder.append(po);
		builder.append(", iccId=");
		builder.append(iccId);
		builder.append(", manufacturedDate=");
		builder.append(manufacturedDate);
		builder.append(", imei=");
		builder.append(imei);
		builder.append(", imsi=");
		builder.append(imsi);
		builder.append(", simNumber=");
		builder.append(simNumber);
		builder.append(", phoneNumber=");
		builder.append(phoneNumber);
		builder.append(", cpuUsage=");
		builder.append(cpuUsage);
		builder.append(", memoryUsage=");
		builder.append(memoryUsage);
		builder.append(", bootLoaderVer=");
		builder.append(bootLoaderVer);
		builder.append("]");
		return builder.toString();
	}

    public void setModel(DeviceModel model) {
        this.model = model;
    }
	
	@XmlTransient
    public DeviceModel getModel() {
        return model;
    }

    public Integer getIdType() {
        return idType;
    }

    public void setIdType(Integer idType) {
        this.idType = idType;
    }

    @Override
    public String toJSONString() {
        JSONStringer js = null;
        
        try {
            js = new JSONStringer();
            js.object().key("address").value(address)
                       .key("commState").value(commState)
                       .key("currentThreshold").value(currentThreshold)
                       .key("deviceSerial").value(deviceSerial)
                       .key("gpioX").value(gpioX)
                       .key("gpioY").value(gpioY)
                       .key("gpioZ").value(gpioZ)
                       .key("hwVer").value(hwVer)
                       .key("id").value(id)
                       .key("installDate").value(installDate)
                       .key("installedSiteImg").value(installedSiteImg)
                       .key("ipAddr").value(ipAddr)
                       .key("lastLinkTime").value(lastLinkTime)
                       .key("lastResetCode").value(lastResetCode)
                       .key("macAddr").value(macAddr)
                       .key("mcu").value(mcu ==null ? "":mcu.getSysID())
                       .key("modem").value(modem ==null ? "":modem.getId())
                       .key("modemType").value(modemType==null ? "":modemType.name())
                       .key("nodeType").value(nodeType)
                       .key("powerThreshold").value(powerThreshold)
                       .key("protocolType").value(protocolType)
                       .key("protocolVersion").value(protocolVersion)
                       .key("resetCount").value(resetCount)
                       .key("rfPower").value(rfPower)
                       .key("supplier").value(supplier.getId())
                       .key("fwVer").value(fwVer)
                       .key("fwRevision").value(fwRevision)
                       .key("swVer").value(swVer)
                       .key("bootLoaderVer").value(bootLoaderVer)
//                     .key("version").value(version)
                       .key("location").value(location ==null ? "" :location.getId())
                       .key("amiNetworkAddress").value(amiNetworkAddress)
                       .key("amiNetworkDepth").value(amiNetworkDepth)                      
                       .key("nameSpace").value(nameSpace)
                       .key("gs1").value(gs1)
                       .key("idType").value(idType)
                       .key("po").value(po)
                       .key("iccId").value(iccId)
                       .key("manufacturedDate").value(manufacturedDate)
            		   .key("imei").value(imei)
            		   .key("imsi").value(imsi)
            		   .key("simNumber").value(simNumber)
            		   .key("phoneNumber").value(phoneNumber)
//            		   .key("rssi").value(rssi)
 //           		   .key("lqi").value(lqi)
 //           		   .key("etx").value(etx)
            		   .key("cpuUsage").value(cpuUsage)
            		   .key("memoryUsage").value(memoryUsage) ;
            
            js.endObject();
        } catch (Exception e) {
            System.out.println(e);
        }
        return js.toString();
    }
    
    @Override
    public String getInstanceName() {
        return this.getDeviceSerial();
    }
}