package com.aimir.model.device;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

import net.sf.json.JSONSerializer;
import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 *
 * <pre>
 * DCU 라고 쓰이나 클래스명은 MCU로 명명한다.
 * Data Concentrator Unit, Master Concentrator Unit
 * Operation 범위는 (집중기 타입이 Indoor, Outdoor인 경우)
 * - Status Monitoring
 * - Self Test
 * - Remote File Access
 * - Metering Data Recovery
 * - Time Sync
 * - MCU Reset
 * </pre>
 *
 * @author 박종성(elevas)
 *
 */
@Entity
//@Indexes({
//    @Index(name="IDX_MCU_01", columnNames={"IP_ADDR"}),
//    @Index(name="IDX_MCU_02", columnNames={"IPV6_ADDR"})
//})
public class MCU extends BaseObject implements JSONString, IAuditable {

    private static final long serialVersionUID = 6386644292331049382L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MCU")
    @SequenceGenerator(name = "SEQ_MCU", sequenceName = "SEQ_MCU", allocationSize = 1)
    private Integer id;
    

    @ColumnInfo(name = "집중기 ID", view = @Scope(create = true, read = true, update = true, devicecontrol = true), descr = "")
    @Column(name = "SYS_ID", unique = true, nullable = false, length = 20)
    private String sysID;

    @ColumnInfo(name = "", descr = "")
    @Column(name = "INSTALL_DATE", length = 14)
    private String installDate;    

    @ColumnInfo(name = "", view = @Scope(create = false, read = true, update = false), descr = "")
    @Column(name = "LAST_COMM_DATE", length = 14)
    private String lastCommDate;

    @ColumnInfo(name = "소프트웨어 버젼", view = @Scope(create = true, read = true, update = true, devicecontrol = true), descr = "CODE 1.1.3 연관")
    @Column(name = "SYS_SW_VERSION")
    private String sysSwVersion;
    
    @ColumnInfo(name = "집중기 S/W Revision", view = @Scope(create = true, read = true, update = true, devicecontrol = true), descr = "")
    @Column(name = "SYS_SW_REVISION")
    private String sysSwRevision;    

    @ColumnInfo(name = "하드웨어 버젼", view = @Scope(create = true, read = true, update = true, devicecontrol = true), descr = "CODE 1.1.2 연관")
    @Column(name = "SYS_HW_VERSION")
    private String sysHwVersion;
    

    @ColumnInfo(name = "장비모델", view = @Scope(create = true, read = true, update = true), descr = "DEVICE_MODEL 테이블의 Code")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEVICEMODEL_ID")
    @ReferencedBy(name = "name")
    private DeviceModel deviceModel;

    @Column(name = "DEVICEMODEL_ID", nullable = true, updatable = false, insertable = false)
    private Integer deviceModelId;
    

    @ColumnInfo(name = "SYS_SERIAL_NUMBER")
    @Column(name = "SYS_SERIAL_NUMBER")
    private String sysSerialNumber;

    @ColumnInfo(name = "SYS_TLS_PORT")
    @Column(name = "SYS_TLS_PORT")
    private Integer sysTlsPort;
    

    @ColumnInfo(name = "집중기의 로컬 제어 포트 번호", view = @Scope(create = true, read = true, update = true, devicecontrol = true), descr = "")
    @Column(name = "SYS_LOCAL_PORT", length = 10)
    private Integer sysLocalPort;
    

    @ColumnInfo(name = "communication protocol namespace for OID mapping")
    @Column(name = "NAME_SPACE", length = 10)
    private String nameSpace;

    @ColumnInfo(descr = "바코드 정보")
    @Column(name = "GS1")
    private String gs1;

    @ColumnInfo(name = "SYS_HW_BUILD")
    @Column(name = "SYS_HW_BUILD")
    private String sysHwBuild;


    @ColumnInfo(name = "SYS_TLS_VERSION")
    @Column(name = "SYS_TLS_VERSION")
    private String sysTlsVersion;   


    @XmlTransient
    @ColumnInfo(name = "집중기 삭제 상태", descr = "코드 테이블의 ID 혹은  NULL : Code 1.1.4 참조", view = @Scope(create = true, read = true, update = true))
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MCU_STATUS")
    @ReferencedBy(name = "code")
    private Code mcuStatus;

    @XmlTransient
    @Column(name = "MCU_STATUS", nullable = true, updatable = false, insertable = false)
    private Integer mcuStatusCodeId;
    

    @ColumnInfo(name = "공급사아이디", descr = "공급사 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPPLIER_ID")
    @ReferencedBy(name = "name")
    private Supplier supplier;

    @Column(name = "SUPPLIER_ID", nullable = true, updatable = false, insertable = false)
    private Integer supplierId;

    @ColumnInfo(name = "", view = @Scope(create = true, read = true, update = true), descr = "")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOCATION_ID")
    @ReferencedBy(name = "name")
    private Location location;

    @Column(name = "LOCATION_ID", nullable = true, updatable = false, insertable = false)
    private Integer locationId;

    @ColumnInfo(name = "", descr = "")
    @Column(name = "NETWORK_STATUS", length = 10)
    private Integer networkStatus;
    
    @ColumnInfo(name = "PROTOCOL TYPE", view = @Scope(create = true, read = true, update = true), descr = "Code 4.6의 타입")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROTOCOL_TYPE")
    @ReferencedBy(name = "code")
    private Code protocolType;
    

    @ColumnInfo(name = "MCU TYPE", view = @Scope(create = true, read = true, update = true), descr = "Code 1.1.1의 타입")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MCU_TYPE")
    @ReferencedBy(name = "code")
    private Code mcuType;

    @Column(name = "MCU_TYPE", nullable = true, updatable = false, insertable = false)
    private Integer mcuTypeCodeId;

    @Column(name = "PROTOCOL_TYPE", nullable = true, updatable = false, insertable = false)
    private Integer protocolTypeCodeId;
    

    @ColumnInfo(name = "", view = @Scope(create = true, read = true, update = true), descr = "")
    @Column(name = "IP_ADDR", length = 64)
    private String ipAddr;

    @ColumnInfo(name = "", view = @Scope(create = true, read = true, update = true), descr = "")
    @Column(name = "IPV6_ADDR", length = 64)
    private String ipv6Addr;

    @ColumnInfo(name = "", view = @Scope(create = true, read = true, update = true), descr = "")
    @Column(name = "MAC_ADDR", length = 64)
    private String macAddr;


    @ColumnInfo(name = "", descr = "")
    @Column(name = "LOC_DETAIL", length = 100)
    private String locDetail;

//    @Version
//    Integer version;

    @ColumnInfo(name = "", descr = "")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "MCU_VAR_ID")
    private MCUVar mcuVar;

    @Column(name = "MCU_VAR_ID", nullable = true, updatable = false, insertable = false)
    private Long mcuVarId;

    @ColumnInfo(name = "", descr = "Indoor, Outdoor 타입의 집중기에만 코디정보가 있음 나머지에는 정보 필요없음")
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "MCU_CODI_ID")
    private MCUCodi mcuCodi;

    @Column(name = "MCU_CODI_ID", nullable = true, updatable = false, insertable = false)
    private Integer mcuCodeId;
    
    
    @ColumnInfo(name="L2 network key", view=@Scope(create=true, read=true, update=true), descr="L2 network key")
    @Column(name="NETWORK_KEY", length=256)
    private String networkKey;


    @JoinColumn(name = "parent_id")
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MCU mcu;

    @Column(name = "parent_id", nullable = true, updatable = false, insertable = false)
    private Integer parentId;

    @ColumnInfo(name = "미터아이디", descr = "미터 테이블의 ID 혹은  NULL")
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "mcu")
    private Set<Modem> modem = new HashSet<Modem>(0);


    @ColumnInfo(name = "", descr = "")
    @Column(name = "LAST_MODIFIED_DATE", length = 14)
    private String lastModifiedDate;


    @ColumnInfo(name = "", view = @Scope(create = true, read = true, update = true), descr = "")
    @Column(name = "SERVICE_ATM", length = 1)
    private Integer serviceAtm;

/*
    @ColumnInfo(name="",descr="")
    @Enumerated(EnumType.STRING)
    @Column(name="MCU_TYPE")
    private MCUType mcuType;
 */


    @ColumnInfo(name = "경도", view = @Scope(create = true, read = true, update = true), descr = "")
    @Column(name = "GPIOX")
    private Double gpioX;

    @ColumnInfo(name = "위도", view = @Scope(create = true, read = true, update = true), descr = "")
    @Column(name = "GPIOY")
    private Double gpioY;

    @ColumnInfo(name = "해발", view = @Scope(create = true, read = true, update = true), descr = "")
    @Column(name = "GPIOZ")
    private Double gpioZ;

    @ColumnInfo(name = "", view = @Scope(create = false, read = true, update = false), descr = "")
    @Column(name = "POWER_STATE", length = 10)
    private Integer powerState;

    @ColumnInfo(name = "", view = @Scope(create = false, read = true, update = false), descr = "")
    @Column(name = "LOW_BATTREY_FLAG", length = 1)
    private Integer lowBatteryFlag;

    @ColumnInfo(name = "", view = @Scope(create = false, read = true, update = false), descr = "")
    @Column(name = "MOBILE_USAGE_FLAG", length = 1)
    private Integer mobileUsageFlag;

    @ColumnInfo(name = "", view = @Scope(create = false, read = true, update = false), descr = "")
    @Column(name = "BATTERY_CAPACITY", length = 10)
    private Integer batteryCapacity;

    @ColumnInfo(name = "", view = @Scope(create = false, read = true, update = false), descr = "")
    @Column(name = "LAST_TIME_SYNC_DATE", length = 14)
    private String lastTimeSyncDate;

    @ColumnInfo(name = "", view = @Scope(create = false, read = true, update = false), descr = "")
    @Column(name = "LAST_SW_UPDATE_DATE", length = 14)
    private String lastswUpdateDate;

    @ColumnInfo(name = "", view = @Scope(create = false, read = true, update = true), descr = "")
    @Column(name = "UPDATE_SERVER_PORT", length = 10)
    private Integer updateServerPort;

    @ColumnInfo(name = "", view = @Scope(create = false, read = true, update = true), descr = "")
    @Column(name = "FW_STATE", length = 10)
    private Integer fwState;


    @ColumnInfo(name = "집중기 유형", view = @Scope(create = false, read = false, update = false, devicecontrol = true), descr = "3=Indoor MCU, 4=Outdoor MCU, 7=DCU : CODE 1.1.1 동일)")
    @Column(name = "SYS_TYPE", length = 10)
    private Integer sysType;

    @ColumnInfo(name = "집중기 명칭", view = @Scope(create = false, read = true, update = true, devicecontrol = true), descr = "")
    @Column(name = "SYS_NAME", length = 100)
    private String sysName;

    @ColumnInfo(name = "집중기 설명", view = @Scope(create = false, read = true, update = true, devicecontrol = true), descr = "")
    @Column(name = "SYS_DESCR", length = 255)
    private String sysDescr;

    @ColumnInfo(name = "집중기 위치", view = @Scope(create = false, read = true, update = true, devicecontrol = true), descr = "")
    @Column(name = "SYS_LOCATION", length = 100)
    private String sysLocation;

    @ColumnInfo(name = "집중기 담당자", view = @Scope(create = false, read = true, update = true, devicecontrol = true), descr = "")
    @Column(name = "SYS_CONTACT", length = 100)
    private String sysContact;


    @ColumnInfo(name = "모바일 전화번호", view = @Scope(create = true, read = true, update = true, devicecontrol = true), descr = "protocolType 연관")
    @Column(name = "SYS_PHONE_NUMBER", length = 16)
    private String sysPhoneNumber;

    @ColumnInfo(name = "이더넷 유형", view = @Scope(create = true, read = false, update = false, devicecontrol = true), descr = "0=LAN, 1=DHCP, 2=PPPoE, 3=PPP : protocolType 연관")
    @Column(name = "SYS_ETHER_TYPE", length = 2)
    private Integer sysEtherType;

    @ColumnInfo(name = "모바일 유형", view = @Scope(create = true, read = false, update = false, devicecontrol = true), descr = "0=DISABLE, 1=GSM, 2=CDMA, 3=PSTN : protocolType 연관")
    @Column(name = "SYS_MOBILE_TYPE", length = 2)
    private Integer sysMobileType;

    @ColumnInfo(name = "모바일 접속 유형", view = @Scope(create = true, read = false, update = false, devicecontrol = true), descr = "0=CSD, 1=Packet, 2=Always On : protocolType 연관")
    @Column(name = "SYS_MOBILE_MODE", length = 2)
    private Integer sysMobileMode;

    @ColumnInfo(name = "부팅후 경과된 시간", view = @Scope(create = false, read = false, update = false, devicecontrol = true), descr = "")
    @Column(name = "SYS_UPTIME", length = 14)
    private String sysUpTime;

    @ColumnInfo(name = "시스템 시간", view = @Scope(create = false, read = true, update = false, devicecontrol = true), descr = "")
    @Column(name = "SYS_TIME", length = 14)
    private String sysTime;

    @ColumnInfo(name = "집중기 현재 온도", view = @Scope(create = false, read = true, update = false, devicecontrol = true), descr = "")
    @Column(name = "SYS_CUR_TEMP", length = 10)
    private Integer sysCurTemp;

    @ColumnInfo(name = "집중기 최소 온도", view = @Scope(create = true, read = true, update = true, devicecontrol = true), descr = "")
    @Column(name = "SYS_MIN_TEMP", length = 10)
    private Integer sysMinTemp;

    @ColumnInfo(name = "집중기 최대 온도", view = @Scope(create = true, read = true, update = true, devicecontrol = true), descr = "")
    @Column(name = "SYS_MAX_TEMP", length = 10)
    private Integer sysMaxTemp;

    @ColumnInfo(name = "FEP 서버 주소", view = @Scope(create = true, read = true, update = true, devicecontrol = true), descr = "")
    @Column(name = "SYS_SERVER", length = 25)
    private String sysServer;

    @ColumnInfo(name = "FEP 서버 포트", view = @Scope(create = true, read = true, update = true, devicecontrol = true), descr = "")
    @Column(name = "SYS_SERVER_PORT", length = 10)
    private Integer sysServerPort;

    @ColumnInfo(name = "FEP 서버 알람 포트 번호", view = @Scope(create = true, read = true, update = true, devicecontrol = true), descr = "")
    @Column(name = "SYS_SERVER_ALARM_PORT", length = 10)
    private Integer sysServerAlarmPort;

    @ColumnInfo(name = "집중기의 보안 포트 번호", view = @Scope(create = true, read = true, update = true, devicecontrol = true), descr = "")
    @Column(name = "SYS_SECURE_PORT", length = 10)
    private Integer sysSecurePort;

    @ColumnInfo(name = "집중기의 상태", view = @Scope(create = false, read = true, update = false, devicecontrol = true), descr = "0=비정상, 1=정상")
    @Column(name = "SYS_STATE", length = 2)
    private Integer sysState;

    @ColumnInfo(name = "집중기 제조사", view = @Scope(create = false, read = false, update = true, devicecontrol = true), descr = "deviceModel 연관")
    @Column(name = "SYS_VENDOR", length = 100)
    private String sysVendor;

    @ColumnInfo(name = "집중기 모델명", view = @Scope(create = false, read = false, update = true, devicecontrol = true), descr = "deviceModel 연관")
    @Column(name = "SYS_MODEL", length = 100)
    private String sysModel;

    @ColumnInfo(name = "모바일 제조사", view = @Scope(create = false, read = true, update = false, devicecontrol = true), descr = "")
    @Column(name = "SYS_MOBILE_VENDOR", length = 10)
    private Integer sysMobileVendor;

    @ColumnInfo(name = "모바일 APN Name", view = @Scope(create = true, read = true, update = false, devicecontrol = true), descr = "")
    @Column(name = "SYS_MOBILE_ACCESS_POINT_NAME", length = 64)
    private String sysMobileAccessPointName;

    @ColumnInfo(name = "집중기 리셋 사유", view = @Scope(create = false, read = false, update = false, devicecontrol = true), descr = "0:Unknown, 1:Command, 2:Firmware Upgrade, 3:Fixed Reset, 4:Watchdog, 5:Low Battery")
    @Column(name = "SYS_RESET_REASON", length = 10)
    private Integer sysResetReason;

    @ColumnInfo(name = "시스템 기동 후 Join 된 Node 수", view = @Scope(create = false, read = false, update = false, devicecontrol = true), descr = "")
    @Column(name = "SYS_JOIN_NODE_COUNT", length = 10)
    private Integer sysJoinNodeCount;

    @ColumnInfo(name = "시스템의 Timezone", view = @Scope(create = false, read = false, update = false, devicecontrol = true), descr = "단위 분")
    @Column(name = "SYS_TIME_ZONE", length = 10)
    private Integer sysTimeZone;

    @ColumnInfo(name = "시스템의 운영 모드", view = @Scope(create = false, read = false, update = false, devicecontrol = true), descr = "0:Normal, 1:Test")
    @Column(name = "SYS_OP_MODE", length = 10)
    private Integer sysOpMode;

    @ColumnInfo(name = "시스템의 전원 방식", view = @Scope(create = false, read = false, update = false, devicecontrol = true), descr = "")
    @Column(name = "SYS_POWER_TYPE", length = 2)
    private Integer sysPowerType;

    @ColumnInfo(name = "시스템 상태", view = @Scope(create = false, read = false, update = false, devicecontrol = true), descr = "sysState가 0 일 때 Error Mask가 Setting 되어 있음")
    @Column(name = "SYS_STATE_MASK", length = 10)
    private Integer sysStateMask;

    @ColumnInfo(name = "communication protocol version('IF4', 'TNG')")
    @Column(name = "PROTOCOL_VERSION", length = 20)
    private String protocolVersion; // protocol Version


    @ColumnInfo(name = "AMI Virtual Network Address")
    @Column(name = "AMI_NETWORK_ADDRESS", length = 128)
    private String amiNetworkAddress; // AMI Virtual Network Address


    @ColumnInfo(name = "", descr = "MCU 테이블의 ID 혹은  NULL : 재귀호출을 위함")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mcu", fetch = FetchType.LAZY)
    // @JoinColumn(name="PARENT_MCU_ID")
    private List<MCU> childMcus = new ArrayList<MCU>(0);

    @ColumnInfo(name = "", view = @Scope(create = true, read = true, update = true), descr = "")
    @OneToMany
    @JoinColumn(name = "MCU_ID")
    @OrderBy("id")
    private List<MCUInstallImg> mcuInstallImgs = new ArrayList<MCUInstallImg>(0);


    @Transient
    private String selectedField = "true";

    @ColumnInfo(name="Purchase Order")
    @Column(name="po")
    private String po;
    
    @ColumnInfo(name="IMEI")
    @Column(name="IMEI")
    private String imei;
    
    @ColumnInfo(name="심카드 번호")
    @Column(name="SIM_NUMBER",length=24)
    private String simNumber;

    @ColumnInfo(name="ICC ID")
    @Column(name="ICC_ID")
    private String iccId;
    
    @ColumnInfo(name="제조일자", view=@Scope(create=true, read=true, update=true), descr="제조일자")
    @Column(name="MANUFACTURED_DATE", length=8)
    private String manufacturedDate;

    
    @ColumnInfo(name="L2 network key index", view=@Scope(create=true, read=true, update=true), descr="L2 network index")
    @Column(name="NETWORK_KEY_IDX")
    private Integer networkKeyIdx;
    

    @ColumnInfo(name = "AMI Virtual Network Address V6")
    @Column(name = "AMI_NETWORK_ADDRESS_V6", length = 128)
    private String amiNetworkAddressV6; // AMI Virtual Network Address
    

    @ColumnInfo(name = "AMI Virtual Network Address Depth")
    @Column(name = "AMI_NETWORK_DEPTH", length = 2)
    private Integer amiNetworkDepth; // AMI Virtual Network Depth
    

    public void setSelectedField(String selectedField) {
        this.selectedField = selectedField;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

//  public Integer getVersion() {
//      return version;
//  }
//
//  public void setVersion(Integer version) {
//      this.version = version;
//  }

    @XmlTransient
    public List<MCU> getChildMcus() {
        return childMcus;
    }

    public void setChildMcus(List<MCU> childMcus) {
        this.childMcus = childMcus;
    }

    @XmlTransient
    public List<MCUInstallImg> getMcuInstallImgs() {
        return mcuInstallImgs;
    }

    public void setMcuInstallImgs(List<MCUInstallImg> mcuInstallImgs) {
        this.mcuInstallImgs = mcuInstallImgs;
    }

    @XmlTransient
    public MCUVar getMcuVar() {
        return mcuVar;
    }

    public void setMcuVar(MCUVar mcuVar) {
        this.mcuVar = mcuVar;
    }

    @XmlTransient
    public MCUCodi getMcuCodi() {
        return mcuCodi;
    }

    public void setMcuCodi(MCUCodi mcuCodi) {
        this.mcuCodi = mcuCodi;
    }

    @XmlTransient
    public DeviceModel getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(DeviceModel deviceModel) {
        this.deviceModel = deviceModel;
    }

    @XmlTransient
    public MCU getMcu() {
        return mcu;
    }

    public void setMcu(MCU mcu) {
        this.mcu = mcu;
    }

    @XmlTransient
    public Set<Modem> getModem() {
        return modem;
    }

    public void setModem(Set<Modem> modem) {
        this.modem = modem;
    }

    @XmlTransient
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getSysDescr() {
        return sysDescr;
    }

    public void setSysDescr(String sysDescr) {
        this.sysDescr = sysDescr;
    }

    public String getSysID() {
        return sysID;
    }

    public void setSysID(String sysID) {
        this.sysID = sysID;
    }

    public String getSysLocation() {
        return sysLocation;
    }

    public void setSysLocation(String sysLocation) {
        this.sysLocation = sysLocation;
    }

    public String getSysVendor() {
        return sysVendor;
    }

    public void setSysVendor(String sysVendor) {
        this.sysVendor = sysVendor;
    }

    public String getSysModel() {
        return sysModel;
    }

    public void setSysModel(String sysModel) {
        this.sysModel = sysModel;
    }

    public String getSysContact() {
        return sysContact;
    }

    public void setSysContact(String sysContact) {
        this.sysContact = sysContact;
    }

    public Integer getNetworkStatus() {
        return networkStatus;
    }

    public void setNetworkStatus(Integer networkStatus) {
        this.networkStatus = networkStatus;
    }

    public String getInstallDate() {
        return installDate;
    }

    public void setInstallDate(String installDate) {
        this.installDate = installDate;
    }

    public String getLocDetail() {
        return locDetail;
    }

    public void setLocDetail(String locDetail) {
        this.locDetail = locDetail;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getIpv6Addr() {
        return ipv6Addr;
    }

    public void setIpv6Addr(String ipv6Addr) {
        this.ipv6Addr = ipv6Addr;
    }

    public String getMacAddr() {
        return macAddr;
    }

    public void setMacAddr(String macAddr) {
        this.macAddr = macAddr;
    }

    public String getSysUpTime() {
        return sysUpTime;
    }

    public void setSysUpTime(String sysUpTime) {
        this.sysUpTime = sysUpTime;
    }

    public Integer getServiceAtm() {
        return serviceAtm;
    }

    public void setServiceAtm(Integer serviceAtm) {
        this.serviceAtm = serviceAtm;
    }

    @XmlTransient
    public Code getMcuType() {
        return mcuType;
    }

    public void setMcuType(Code mcuType) {
        this.mcuType = mcuType;
    }

    @XmlTransient
    public Code getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(Code protocolType) {
        this.protocolType = protocolType;
    }

    public String getSysPhoneNumber() {
        return sysPhoneNumber;
    }

    public void setSysPhoneNumber(String sysPhoneNumber) {
        this.sysPhoneNumber = sysPhoneNumber;
    }

    public Integer getPowerState() {
        return powerState;
    }

    public void setPowerState(Integer powerState) {
        this.powerState = powerState;
    }

    public Integer getLowBatteryFlag() {
        return lowBatteryFlag;
    }

    public void setLowBatteryFlag(Integer lowBatteryFlag) {
        this.lowBatteryFlag = lowBatteryFlag;
    }

    public Integer getMobileUsageFlag() {
        return mobileUsageFlag;
    }

    public void setMobileUsageFlag(Integer mobileUsageFlag) {
        this.mobileUsageFlag = mobileUsageFlag;
    }

    public Integer getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(Integer batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public String getLastTimeSyncDate() {
        return lastTimeSyncDate;
    }

    public void setLastTimeSyncDate(String lastTimeSyncDate) {
        this.lastTimeSyncDate = lastTimeSyncDate;
    }

    public String getLastswUpdateDate() {
        return lastswUpdateDate;
    }

    public void setLastswUpdateDate(String lastswUpdateDate) {
        this.lastswUpdateDate = lastswUpdateDate;
    }

    public String getLastCommDate() {
        return lastCommDate;
    }

    public void setLastCommDate(String lastCommDate) {
        this.lastCommDate = lastCommDate;
    }

    public String getSysMobileAccessPointName() {
        return sysMobileAccessPointName;
    }

    public void setSysMobileAccessPointName(String sysMobileAccessPointName) {
        this.sysMobileAccessPointName = sysMobileAccessPointName;
    }

    public Integer getUpdateServerPort() {
        return updateServerPort;
    }

    public void setUpdateServerPort(Integer updateServerPort) {
        this.updateServerPort = updateServerPort;
    }

    public Integer getFwState() {
        return fwState;
    }

    public void setFwState(Integer fwState) {
        this.fwState = fwState;
    }

    public Integer getSysResetReason() {
        return sysResetReason;
    }

    public void setSysResetReason(Integer sysResetReason) {
        this.sysResetReason = sysResetReason;
    }

    public Integer getSysJoinNodeCount() {
        return sysJoinNodeCount;
    }

    public void setSysJoinNodeCount(Integer sysJoinNodeCount) {
        this.sysJoinNodeCount = sysJoinNodeCount;
    }

    public Integer getSysTimeZone() {
        return sysTimeZone;
    }

    public void setSysTimeZone(Integer sysTimeZone) {
        this.sysTimeZone = sysTimeZone;
    }

    public Integer getSysOpMode() {
        return sysOpMode;
    }

    public void setSysOpMode(Integer sysOpMode) {
        this.sysOpMode = sysOpMode;
    }

    public String getSelectedField() {
        return selectedField;
    }

    public Integer getSysType() {
        return sysType;
    }

    public void setSysType(Integer sysType) {
        this.sysType = sysType;
    }

    public Integer getSysMaxTemp() {
        return sysMaxTemp;
    }

    public void setSysMaxTemp(Integer sysMaxTemp) {
        this.sysMaxTemp = sysMaxTemp;
    }

    public Integer getSysMinTemp() {
        return sysMinTemp;
    }

    public void setSysMinTemp(Integer sysMinTemp) {
        this.sysMinTemp = sysMinTemp;
    }

    public Integer getSysCurTemp() {
        return sysCurTemp;
    }

    public void setSysCurTemp(Integer sysCurTemp) {
        this.sysCurTemp = sysCurTemp;
    }

    public Integer getSysSecurePort() {
        return sysSecurePort;
    }

    public void setSysSecurePort(Integer sysSecurePort) {
        this.sysSecurePort = sysSecurePort;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public String getAmiNetworkAddress() {
        return amiNetworkAddress;
    }

    public void setAmiNetworkAddress(String amiNetworkAddress) {
        this.amiNetworkAddress = amiNetworkAddress;
    }

    public String getAmiNetworkAddressV6() {
        return amiNetworkAddressV6;
    }

    public void setAmiNetworkAddressV6(String amiNetworkAddressV6) {
        this.amiNetworkAddressV6 = amiNetworkAddressV6;
    }

    public Integer getAmiNetworkDepth() {
        return amiNetworkDepth;
    }

    public void setAmiNetworkDepth(Integer amiNetworkDepth) {
        this.amiNetworkDepth = amiNetworkDepth;
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

    public String getNetworkKey() {
		return networkKey;
	}

	public void setNetworkKey(String networkKey) {
		this.networkKey = networkKey;
	}

	public Integer getNetworkKeyIdx() {
		return networkKeyIdx;
	}

	public void setNetworkKeyIdx(Integer networkKeyIdx) {
		this.networkKeyIdx = networkKeyIdx;
	}

	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        if (getClass() != obj.getClass())
            return false;
        MCU other = (MCU) obj;
        if (sysContact == null) {
            if (other.sysContact != null)
                return false;
        } else if (!sysContact.equals(other.sysContact))
            return false;
        if (batteryCapacity == null) {
            if (other.batteryCapacity != null)
                return false;
        } else if (!batteryCapacity.equals(other.batteryCapacity))
            return false;
        if (childMcus == null) {
            if (other.childMcus != null)
                return false;
        } else if (!childMcus.equals(other.childMcus))
            return false;
        if (deviceModel == null) {
            if (other.deviceModel != null)
                return false;
        } else if (!deviceModel.equals(other.deviceModel))
            return false;
        if (fwState == null) {
            if (other.fwState != null)
                return false;
        } else if (!fwState.equals(other.fwState))
            return false;
        if (sysHwVersion == null) {
            if (other.sysHwVersion != null)
                return false;
        } else if (!sysHwVersion.equals(other.sysHwVersion))
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
        if (ipAddr == null) {
            if (other.ipAddr != null)
                return false;
        } else if (!ipAddr.equals(other.ipAddr))
            return false;
        if (ipv6Addr == null) {
            if (other.ipv6Addr != null)
                return false;
        } else if (!ipv6Addr.equals(other.ipv6Addr))
            return false;
        if (macAddr == null) {
            if (other.macAddr != null)
                return false;
        } else if (!macAddr.equals(other.macAddr))
            return false;
        if (lastCommDate == null) {
            if (other.lastCommDate != null)
                return false;
        } else if (!lastCommDate.equals(other.lastCommDate))
            return false;
        if (lastModifiedDate == null) {
            if (other.lastModifiedDate != null)
                return false;
        } else if (!lastModifiedDate.equals(other.lastModifiedDate))
            return false;
        if (lastTimeSyncDate == null) {
            if (other.lastTimeSyncDate != null)
                return false;
        } else if (!lastTimeSyncDate.equals(other.lastTimeSyncDate))
            return false;
        if (lastswUpdateDate == null) {
            if (other.lastswUpdateDate != null)
                return false;
        } else if (!lastswUpdateDate.equals(other.lastswUpdateDate))
            return false;
        if (sysLocalPort == null) {
            if (other.sysLocalPort != null)
                return false;
        } else if (!sysLocalPort.equals(other.sysLocalPort))
            return false;
        if (locDetail == null) {
            if (other.locDetail != null)
                return false;
        } else if (!locDetail.equals(other.locDetail))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (lowBatteryFlag == null) {
            if (other.lowBatteryFlag != null)
                return false;
        } else if (!lowBatteryFlag.equals(other.lowBatteryFlag))
            return false;
        if (mcu == null) {
            if (other.mcu != null)
                return false;
        } else if (!mcu.equals(other.mcu))
            return false;
        if (mcuCodi == null) {
            if (other.mcuCodi != null)
                return false;
        } else if (!mcuCodi.equals(other.mcuCodi))
            return false;
        if (mcuInstallImgs == null) {
            if (other.mcuInstallImgs != null)
                return false;
        } else if (!mcuInstallImgs.equals(other.mcuInstallImgs))
            return false;
        if (mcuType == null) {
            if (other.mcuType != null)
                return false;
        } else if (!mcuType.equals(other.mcuType))
            return false;
        if (mcuVar == null) {
            if (other.mcuVar != null)
                return false;
        } else if (!mcuVar.equals(other.mcuVar))
            return false;
        if (mobileUsageFlag == null) {
            if (other.mobileUsageFlag != null)
                return false;
        } else if (!mobileUsageFlag.equals(other.mobileUsageFlag))
            return false;
        if (modem == null) {
            if (other.modem != null)
                return false;
        } else if (!modem.equals(other.modem))
            return false;
        if (networkStatus == null) {
            if (other.networkStatus != null)
                return false;
        } else if (!networkStatus.equals(other.networkStatus))
            return false;
        if (powerState == null) {
            if (other.powerState != null)
                return false;
        } else if (!powerState.equals(other.powerState))
            return false;
        if (protocolType == null) {
            if (other.protocolType != null)
                return false;
        } else if (!protocolType.equals(other.protocolType))
            return false;
        if (selectedField == null) {
            if (other.selectedField != null)
                return false;
        } else if (!selectedField.equals(other.selectedField))
            return false;
        if (!serviceAtm.equals(other.serviceAtm))
            return false;
        if (sysSwVersion == null) {
            if (other.sysSwVersion != null)
                return false;
        } else if (!sysSwVersion.equals(other.sysSwVersion))
            return false;
        if (sysDescr == null) {
            if (other.sysDescr != null)
                return false;
        } else if (!sysDescr.equals(other.sysDescr))
            return false;
        if (sysJoinNodeCount == null) {
            if (other.sysJoinNodeCount != null)
                return false;
        } else if (!sysJoinNodeCount.equals(other.sysJoinNodeCount))
            return false;
        if (sysLocation == null) {
            if (other.sysLocation != null)
                return false;
        } else if (!sysLocation.equals(other.sysLocation))
            return false;
        if (sysMobileAccessPointName == null) {
            if (other.sysMobileAccessPointName != null)
                return false;
        } else if (!sysMobileAccessPointName
                .equals(other.sysMobileAccessPointName))
            return false;
        if (sysName == null) {
            if (other.sysName != null)
                return false;
        } else if (!sysName.equals(other.sysName))
            return false;
        if (sysOpMode == null) {
            if (other.sysOpMode != null)
                return false;
        } else if (!sysOpMode.equals(other.sysOpMode))
            return false;
        if (sysPhoneNumber == null) {
            if (other.sysPhoneNumber != null)
                return false;
        } else if (!sysPhoneNumber.equals(other.sysPhoneNumber))
            return false;
        if (sysResetReason == null) {
            if (other.sysResetReason != null)
                return false;
        } else if (!sysResetReason.equals(other.sysResetReason))
            return false;
        if (sysSwRevision == null) {
            if (other.sysSwRevision != null)
                return false;
        } else if (!sysSwRevision.equals(other.sysSwRevision))
            return false;
        if (sysTimeZone == null) {
            if (other.sysTimeZone != null)
                return false;
        } else if (!sysTimeZone.equals(other.sysTimeZone))
            return false;
        if (sysUpTime == null) {
            if (other.sysUpTime != null)
                return false;
        } else if (!sysUpTime.equals(other.sysUpTime))
            return false;
        if (updateServerPort == null) {
            if (other.updateServerPort != null)
                return false;
        } else if (!updateServerPort.equals(other.updateServerPort))
            return false;
//      if (version == null) {
//          if (other.version != null)
//              return false;
//      } else if (!version.equals(other.version))
//          return false;
        if (sysType == null) {
            if (other.sysType != null)
                return false;
        } else if (!sysType.equals(other.sysType))
            return false;
        if (sysMinTemp == null) {
            if (other.sysMinTemp != null)
                return false;
        } else if (!sysMinTemp.equals(other.sysMinTemp))
            return false;
        if (sysMaxTemp == null) {
            if (other.sysMaxTemp != null)
                return false;
        } else if (!sysMaxTemp.equals(other.sysMaxTemp))
            return false;
        if (sysCurTemp == null) {
            if (other.sysCurTemp != null)
                return false;
        } else if (!sysCurTemp.equals(other.sysCurTemp))
            return false;
        if (protocolVersion == null) {
            if (other.protocolVersion != null)
                return false;
        } else if (!protocolVersion.equals(other.protocolVersion))
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
        if (gs1 == null) {
            if (other.gs1 != null)
                return false;
        } else if (!gs1.equals(other.gs1))
            return false;
        if (sysHwBuild == null) {
            if (other.sysHwBuild != null)
                return false;
        } else if (!sysHwBuild.equals(other.sysHwBuild))
            return false;
        if (sysSerialNumber == null) {
            if (other.sysSerialNumber != null)
                return false;
        } else if (!sysSerialNumber.equals(other.sysSerialNumber))
            return false;
        if (sysTlsVersion == null) {
            if (other.sysTlsVersion != null)
                return false;
        } else if (!sysTlsVersion.equals(other.sysTlsVersion))
            return false;
        if (sysTlsPort == null) {
            if (other.sysTlsPort != null)
                return false;
        } else if (!sysTlsPort.equals(other.sysTlsPort))
            return false;
        if (networkKey == null) {
            if (other.networkKey != null)
                return false;
        } else if (!networkKey.equals(other.networkKey))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;

        result = prime * result + ((batteryCapacity == null) ? 0 : batteryCapacity.hashCode());
        result = prime * result + ((childMcus == null) ? 0 : childMcus.hashCode());
        result = prime * result + ((deviceModel == null) ? 0 : deviceModel.hashCode());
        result = prime * result + ((fwState == null) ? 0 : fwState.hashCode());

        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((installDate == null) ? 0 : installDate.hashCode());
        result = prime * result + ((ipAddr == null) ? 0 : ipAddr.hashCode());
        result = prime * result + ((ipv6Addr == null) ? 0 : ipv6Addr.hashCode());
        result = prime * result + ((macAddr == null) ? 0 : macAddr.hashCode());
        result = prime * result + ((lastCommDate == null) ? 0 : lastCommDate.hashCode());
        result = prime * result + ((lastModifiedDate == null) ? 0 : lastModifiedDate.hashCode());
        result = prime * result + ((lastTimeSyncDate == null) ? 0 : lastTimeSyncDate.hashCode());
        result = prime * result + ((lastswUpdateDate == null) ? 0 : lastswUpdateDate.hashCode());

        result = prime * result + ((locDetail == null) ? 0 : locDetail.hashCode());
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((lowBatteryFlag == null) ? 0 : lowBatteryFlag.hashCode());
        result = prime * result + ((mcu == null) ? 0 : mcu.hashCode());
        result = prime * result + ((mcuCodi == null) ? 0 : mcuCodi.hashCode());
        result = prime * result + ((mcuInstallImgs == null) ? 0 : mcuInstallImgs.hashCode());
        result = prime * result + ((mcuType == null) ? 0 : mcuType.hashCode());
        result = prime * result + ((mcuVar == null) ? 0 : mcuVar.hashCode());
        result = prime * result + ((mobileUsageFlag == null) ? 0 : mobileUsageFlag.hashCode());
//        result = prime * result + ((modem == null) ? 0 : modem.hashCode());
        result = prime * result + ((networkStatus == null) ? 0 : networkStatus.hashCode());
        result = prime * result + ((powerState == null) ? 0 : powerState.hashCode());
        result = prime * result + ((protocolType == null) ? 0 : protocolType.hashCode());
        result = prime * result + ((selectedField == null) ? 0 : selectedField.hashCode());
        result = prime * result + ((serviceAtm == null) ? 0 : serviceAtm.hashCode());
        result = prime * result + ((updateServerPort == null) ? 0 : updateServerPort.hashCode());
//        result = prime * result + ((version == null) ? 0 : version.hashCode());

        result = prime * result + ((sysID == null) ? 0 : sysID.hashCode());
        result = prime * result + ((sysType == null) ? 0 : sysType.hashCode());
        result = prime * result + ((sysName == null) ? 0 : sysName.hashCode());
        result = prime * result + ((sysDescr == null) ? 0 : sysDescr.hashCode());
        result = prime * result + ((sysLocation == null) ? 0 : sysLocation.hashCode());
        result = prime * result + ((sysContact == null) ? 0 : sysContact.hashCode());
        result = prime * result + ((sysHwVersion == null) ? 0 : sysHwVersion.hashCode());
        result = prime * result + ((sysSwVersion == null) ? 0 : sysSwVersion.hashCode());
        result = prime * result + ((sysPhoneNumber == null) ? 0 : sysPhoneNumber.hashCode());
        result = prime * result + ((sysEtherType == null) ? 0 : sysEtherType.hashCode());
        result = prime * result + ((sysMobileType == null) ? 0 : sysMobileType.hashCode());
        result = prime * result + ((sysMobileMode == null) ? 0 : sysMobileMode.hashCode());
        result = prime * result + ((sysUpTime == null) ? 0 : sysUpTime.hashCode());
        result = prime * result + ((sysTime == null) ? 0 : sysTime.hashCode());
        result = prime * result + ((sysCurTemp == null) ? 0 : sysCurTemp.hashCode());
        result = prime * result + ((sysMinTemp == null) ? 0 : sysMinTemp.hashCode());
        result = prime * result + ((sysMaxTemp == null) ? 0 : sysMaxTemp.hashCode());
        result = prime * result + ((sysServer == null) ? 0 : sysServer.hashCode());
        result = prime * result + ((sysServerPort == null) ? 0 : sysServerPort.hashCode());
        result = prime * result + ((sysServerAlarmPort == null) ? 0 : sysServerAlarmPort.hashCode());
        result = prime * result + ((sysLocalPort == null) ? 0 : sysLocalPort.hashCode());
        result = prime * result + ((sysState == null) ? 0 : sysState.hashCode());
        result = prime * result + ((sysVendor == null) ? 0 : sysVendor.hashCode());
        result = prime * result + ((sysModel == null) ? 0 : sysModel.hashCode());
        result = prime * result + ((sysMobileVendor == null) ? 0 : sysMobileVendor.hashCode());
        result = prime * result + ((sysMobileAccessPointName == null) ? 0 : sysMobileAccessPointName.hashCode());
        result = prime * result + ((sysSwRevision == null) ? 0 : sysSwRevision.hashCode());
        result = prime * result + ((sysResetReason == null) ? 0 : sysResetReason.hashCode());
        result = prime * result + ((sysJoinNodeCount == null) ? 0 : sysJoinNodeCount.hashCode());
        result = prime * result + ((sysTimeZone == null) ? 0 : sysTimeZone.hashCode());
        result = prime * result + ((sysOpMode == null) ? 0 : sysOpMode.hashCode());
        result = prime * result + ((sysPowerType == null) ? 0 : sysPowerType.hashCode());
        result = prime * result + ((sysStateMask == null) ? 0 : sysStateMask.hashCode());
        result = prime * result + ((protocolVersion == null) ? 0 : protocolVersion.hashCode());
        result = prime * result + ((amiNetworkAddress == null) ? 0 : amiNetworkAddress.hashCode());
        result = prime * result + ((amiNetworkAddressV6 == null) ? 0 : amiNetworkAddressV6.hashCode());
        result = prime * result + ((amiNetworkDepth == null) ? 0 : amiNetworkDepth.hashCode());
        result = prime * result + ((nameSpace == null) ? 0 : nameSpace.hashCode());
        result = prime * result + ((gs1 == null) ? 0 : gs1.hashCode());
        result = prime * result + ((sysHwBuild == null) ? 0 : sysHwBuild.hashCode());
        result = prime * result + ((sysSerialNumber == null) ? 0 : sysSerialNumber.hashCode());
        return result;
     }

//  @Override
//  public String toString() {
//      return "MCU [id=" + id + ", batteryCapacity=" + batteryCapacity
//              + ", fwState=" + fwState + ", installDate=" + installDate + ", ipAddr=" + ipAddr
//              + ", lastCommDate=" + lastCommDate + ", lastModifiedDate=" + lastModifiedDate
//              + ", lastTimeSyncDate=" + lastTimeSyncDate + ", lastswUpdateDate=" + lastswUpdateDate
//              + ", locDetail=" + locDetail + ", location=" + location + ", lowBatteryFlag=" + lowBatteryFlag
//              + ", mcu=" + mcu + ", childMcus=" + childMcus
//              + ", mcuCodi=" + mcuCodi + ", mcuInstallImgs=" + mcuInstallImgs
//              + ", mcuType=" + mcuType + ", mcuVar=" + mcuVar
//              + ", mobileUsageFlag=" + mobileUsageFlag + ", modem=" + modem + ", networkStatus=" + networkStatus
//              + ", powerState=" + powerState + ", protocolType=" + protocolType
//              + ", selectedField=" + selectedField + ", serviceAtm=" + serviceAtm
//              + ", updateServerPort=" + updateServerPort
//              + ", sysID=" + sysID + ", sysType=" + sysType + ", sysName=" + sysName
//              + ", sysDescr=" + sysDescr + ", sysLocation=" + sysLocation
//              + ", sysContact=" + sysContact + ", sysHwVersion=" + sysHwVersion
//              + ", sysSwVersion=" + sysSwVersion + ", sysPhoneNumber=" + sysPhoneNumber
//              + ", sysEtherType=" + sysEtherType + ", sysMobileType=" + sysMobileType
//              + ", sysMobileMode=" + sysMobileMode + ", sysUpTime=" + sysUpTime
//              + ", sysTime=" + sysTime + ", sysCurTemp=" + sysCurTemp
//              + ", sysMinTemp=" + sysMinTemp + ", sysMaxTemp=" + sysMaxTemp
//              + ", sysServer=" + sysServer + ", sysServerPort=" + sysServerPort
//              + ", sysServerAlarmPort=" + sysServerAlarmPort + ", sysLocalPort=" + sysLocalPort
//              + ", sysState=" + sysState + ", sysVendor=" + sysVendor
//              + ", sysModel=" + sysModel + ", sysMobileVendor=" + sysMobileVendor
//              + ", sysMobileAccessPointName=" + sysMobileAccessPointName + ", sysSwRevision=" + sysSwRevision
//              + ", sysResetReason=" + sysResetReason + ", sysJoinNodeCount=" + sysJoinNodeCount
//              + ", sysTimeZone=" + sysTimeZone + ", sysOpMode=" + sysOpMode
//              + ", sysPowerType=" + sysPowerType + ", sysStateMask=" + sysStateMask + "]";
//  }

    @Override
    public String toString() {
        return "MCU "+toJSONString();

    }

    public void setGpioX(Double gpioX) {
        this.gpioX = gpioX;
    }

    public Double getGpioX() {
        return gpioX;
    }

    public void setGpioY(Double gpioY) {
        this.gpioY = gpioY;
    }

    public Double getGpioY() {
        return gpioY;
    }

    public void setGpioZ(Double gpioZ) {
        this.gpioZ = gpioZ;
    }

    public Double getGpioZ() {
        return gpioZ;
    }

    public String getSysHwVersion() {
        return sysHwVersion;
    }

    public void setSysHwVersion(String sysHwVersion) {
        this.sysHwVersion = sysHwVersion;
    }

    public String getSysSwVersion() {
        return sysSwVersion;
    }

    public void setSysSwVersion(String sysSwVersion) {
        this.sysSwVersion = sysSwVersion;
    }

    public Integer getSysEtherType() {
        return sysEtherType;
    }

    public void setSysEtherType(Integer sysEtherType) {
        this.sysEtherType = sysEtherType;
    }

    public Integer getSysMobileType() {
        return sysMobileType;
    }

    public void setSysMobileType(Integer sysMobileType) {
        this.sysMobileType = sysMobileType;
    }

    public Integer getSysMobileMode() {
        return sysMobileMode;
    }

    public void setSysMobileMode(Integer sysMobileMode) {
        this.sysMobileMode = sysMobileMode;
    }

    public String getSysTime() {
        return sysTime;
    }

    public void setSysTime(String sysTime) {
        this.sysTime = sysTime;
    }

    public String getSysServer() {
        return sysServer;
    }

    public void setSysServer(String sysServer) {
        this.sysServer = sysServer;
    }

    public Integer getSysServerPort() {
        return sysServerPort;
    }

    public void setSysServerPort(Integer sysServerPort) {
        this.sysServerPort = sysServerPort;
    }

    public Integer getSysServerAlarmPort() {
        return sysServerAlarmPort;
    }

    public void setSysServerAlarmPort(Integer sysServerAlarmPort) {
        this.sysServerAlarmPort = sysServerAlarmPort;
    }

    public Integer getSysLocalPort() {
        return sysLocalPort;
    }

    public void setSysLocalPort(Integer sysLocalPort) {
        this.sysLocalPort = sysLocalPort;
    }

    public Integer getSysState() {
        return sysState;
    }

    public void setSysState(Integer sysState) {
        this.sysState = sysState;
    }

    public Integer getSysMobileVendor() {
        return sysMobileVendor;
    }

    public void setSysMobileVendor(Integer sysMobileVendor) {
        this.sysMobileVendor = sysMobileVendor;
    }

    public String getSysSwRevision() {
        return sysSwRevision;
    }

    public void setSysSwRevision(String sysSwRevision) {
        this.sysSwRevision = sysSwRevision;
    }

    public Integer getSysPowerType() {
        return sysPowerType;
    }

    public void setSysPowerType(Integer sysPowerType) {
        this.sysPowerType = sysPowerType;
    }

    public Integer getSysStateMask() {
        return sysStateMask;
    }

    public void setSysStateMask(Integer sysStateMask) {
        this.sysStateMask = sysStateMask;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @XmlTransient
    public Supplier getSupplier() {
        return supplier;
    }

    public Long getMcuVarId() {
        return mcuVarId;
    }

    public void setMcuVarId(Long mcuVarId) {
        this.mcuVarId = mcuVarId;
    }

    public Integer getMcuCodeId() {
        return mcuCodeId;
    }

    public void setMcuCodeId(Integer mcuCodeId) {
        this.mcuCodeId = mcuCodeId;
    }

    public Integer getDeviceModelId() {
        return deviceModelId;
    }

    public void setDeviceModelId(Integer deviceModelId) {
        this.deviceModelId = deviceModelId;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getMcuTypeCodeId() {
        return mcuTypeCodeId;
    }

    public void setMcuTypeCodeId(Integer mcuTypeCodeId) {
        this.mcuTypeCodeId = mcuTypeCodeId;
    }

    public Integer getProtocolTypeCodeId() {
        return protocolTypeCodeId;
    }

    public void setProtocolTypeCodeId(Integer protocolTypeCodeId) {
        this.protocolTypeCodeId = protocolTypeCodeId;
    }

    @XmlTransient
    public Code getMcuStatus() {
        return mcuStatus;
    }

    public void setMcuStatus(Code mcuStatus) {
        this.mcuStatus = mcuStatus;
    }

    @XmlTransient
    public Integer getMcuStatusCodeId() {
        return mcuStatusCodeId;
    }

    public void setMcuStatusCodeId(Integer mcuStatusCodeId) {
        this.mcuStatusCodeId = mcuStatusCodeId;
    }

    public String getSysHwBuild() {
        return sysHwBuild;
    }

    public void setSysHwBuild(String sysHwBuild) {
        this.sysHwBuild = sysHwBuild;
    }

    public String getSysSerialNumber() {
        return sysSerialNumber;
    }

    public void setSysSerialNumber(String sysSerialNumber) {
        this.sysSerialNumber = sysSerialNumber;
    }

    public Integer getSysTlsPort() {
        return sysTlsPort;
    }

    public void setSysTlsPort(int sysTlsPort) {
        this.sysTlsPort = sysTlsPort;
    }

    public String getSysTlsVersion() {
        return sysTlsVersion;
    }

    public void setSysTlsVersion(String sysTlsVersion) {
        this.sysTlsVersion = sysTlsVersion;
    }
    
    public String getPo() {
        return po;
    }

    public void setPo(String po) {
        this.po = po;
    }
    
    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }
    
    public String getSimNumber() {
        return simNumber;
    }

    public void setSimNumber(String simNumber) {
        this.simNumber = simNumber;
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

    @Override
    public String toJSONString() {
        JSONStringer js = null;

        try {
            js = new JSONStringer();
            js.object().key("id").value(id)
                .key("batteryCapacity").value(batteryCapacity)
                .key("fwState").value(fwState)
                .key("installDate").value(installDate)
                .key("ipAddr").value(ipAddr)
                .key("ipv6Addr").value(ipv6Addr)
                .key("macAddr").value(macAddr)
                .key("lastCommDate").value(lastCommDate== null? "":lastCommDate)
                .key("lastModifiedDate").value(lastModifiedDate)
                .key("lastTimeSyncDate").value(lastTimeSyncDate)
                .key("lastswUpdateDate").value(lastswUpdateDate)
                .key("locDetail").value(locDetail)
                .key("location").value(location ==null ? "":JSONSerializer.toJSON(location.toJSONString()))
                .key("lowBatteryFlag").value(lowBatteryFlag)
                .key("mcu").value(mcu ==null ? "":JSONSerializer.toJSON(mcu.toJSONString()))
                .key("mcuInstallImgs").value(mcuInstallImgs)
                .key("mcuType").value(mcuType)
                .key("mcuVar").value(mcuVar)
                .key("mobileUsageFlag").value(mobileUsageFlag)
                .key("networkStatus").value(networkStatus)
                .key("powerState").value(powerState)
                .key("protocolType").value(protocolType)
                .key("selectedField").value(selectedField)
                .key("serviceAtm").value(serviceAtm)
                .key("updateServerPort").value(updateServerPort)
                .key("sysID").value(sysID)
                .key("sysType").value(sysType)
                .key("sysName").value(sysName)
                .key("sysDescr").value(sysDescr)
                .key("sysLocation").value(sysLocation)
                .key("sysContact").value(sysContact)
                .key("sysHwVersion").value(sysHwVersion)
                .key("sysSwVersion").value(sysSwVersion)
                .key("sysPhoneNumber").value(sysPhoneNumber)
                .key("sysEtherType").value(sysEtherType)
                .key("sysMobileType").value(sysMobileType)
                .key("sysMobileMode").value(sysMobileMode)
                .key("sysUpTime").value(sysUpTime)
                .key("sysTime").value(sysTime)
                .key("sysCurTemp").value(sysCurTemp)
                .key("sysMinTemp").value(sysMinTemp)
                .key("sysMaxTemp").value(sysMaxTemp)
                .key("sysServer").value(sysServer)
                .key("sysServerPort").value(sysServerPort)
                .key("sysServerAlarmPort").value(sysServerAlarmPort)
                .key("sysLocalPort").value(sysLocalPort)
                .key("sysState").value(sysState)
                .key("sysVendor").value(sysVendor)
                .key("sysModel").value(sysModel)
                .key("sysMobileVendor").value(sysMobileVendor)
                .key("sysMobileAccessPointName").value(sysMobileAccessPointName)
                .key("sysSwRevision").value(sysSwRevision)
                .key("sysResetReason").value(sysResetReason)
                .key("sysJoinNodeCount").value(sysJoinNodeCount)
                .key("sysTimeZone").value(sysTimeZone)
                .key("sysOpMode").value(sysOpMode)
                .key("sysPowerType").value(sysPowerType)
                .key("sysStateMask").value(sysStateMask)
                .key("protocolVersion").value(protocolVersion)
                .key("amiNetworkAddress").value(amiNetworkAddress)
                .key("amiNetworkAddressV6").value(amiNetworkAddressV6)
                .key("amiNetworkDepth").value(amiNetworkDepth)
                .key("nameSpace").value(nameSpace)
                .key("gs1").value(gs1)
                .key("mcuCodi").value(mcuCodi ==null ? "":JSONSerializer.toJSON(mcuCodi.toJSONString()))
                .key("sysHwBuild").value(sysHwBuild)
                .key("sysSerialNumber").value(sysSerialNumber)
                .key("sysTlsPort").value(sysTlsPort)
                .key("sysTlsVersion").value(sysTlsVersion)
                .key("po").value(po)
                .key("imei").value(imei)
                .key("simNumber").value(simNumber)
                .key("iccId").value(iccId)
                .key("manufacturedDate").value(manufacturedDate)
                .key("networkKey").value(networkKey)
                /*
                .key("codiChannel").value((mcuCodi==null ? "null": mcuCodi.getCodiChannel()))
                .key("codiEnableEncrypt").value((mcuCodi==null ? "null": mcuCodi.getCodiEnableEncrypt()))
                .key("codiExtPanId").value((mcuCodi==null ? "null": mcuCodi.getCodiExtPanId()))
                .key("codiFwBuild").value((mcuCodi==null ? "null": mcuCodi.getCodiFwBuild()))
                .key("codiFwVer").value((mcuCodi==null ? "null": mcuCodi.getCodiFwVer()))
                .key("codiHwVer").value((mcuCodi==null ? "null": mcuCodi.getCodiHwVer()))
                .key("codiIndex").value((mcuCodi==null ? "null": mcuCodi.getCodiIndex()))
                .key("codiLinkKey").value((mcuCodi==null ? "null": mcuCodi.getCodiLinkKey()))
                .key("codiMask").value((mcuCodi==null ? "null": mcuCodi.getCodiMask()))
                .key("codiMulticastHops").value((mcuCodi==null ? "null": mcuCodi.getCodiMulticastHops()))
                .key("codiNetworkKey").value((mcuCodi==null ? "null": mcuCodi.getCodiNetworkKey()))
                .key("codiPanID").value((mcuCodi==null ? "null": mcuCodi.getCodiPanID()))
                .key("codiPermit").value((mcuCodi==null ? "null": mcuCodi.getCodiPermit()))
                .key("codiResetKind").value((mcuCodi==null ? "null": mcuCodi.getCodiResetKind()))
                .key("codiRfPower").value((mcuCodi==null ? "null": mcuCodi.getCodiRfPower()))
                .key("codiRouteDiscovery").value((mcuCodi==null ? "null": mcuCodi.getCodiRouteDiscovery()))
                .key("codiShortID").value((mcuCodi==null ? "null": mcuCodi.getCodiShortID()))
                .key("codiString").value((mcuCodi==null ? "null": mcuCodi.getCodiString()))
                .key("codiTxPowerMode").value((mcuCodi==null ? "null": mcuCodi.getCodiTxPowerMode()))
                .key("codiType").value((mcuCodi==null ? "null": mcuCodi.getCodiType()))
                .key("codiZAIfVer").value((mcuCodi==null ? "null": mcuCodi.getCodiZAIfVer()))
                .key("codiZZIfVer").value((mcuCodi==null ? "null": mcuCodi.getCodiZZIfVer()))
                .key("codiBindID").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiBinding()==null ? "null":mcuCodi.getMcuCodiBinding().getCodiBindID())))
                .key("codiBindIndex").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiBinding()==null ? "null":mcuCodi.getMcuCodiBinding().getCodiBindIndex())))
                .key("codiBindLocal").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiBinding()==null ? "null":mcuCodi.getMcuCodiBinding().getCodiBindLocal())))
                .key("codiBindRemote").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiBinding()==null ? "null":mcuCodi.getMcuCodiBinding().getCodiBindRemote())))
                .key("codiLastHeard").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiBinding()==null ? "null":mcuCodi.getMcuCodiBinding().getCodiLastHeard())))
                .key("codiBindingId").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiBinding()==null ? "null":mcuCodi.getMcuCodiBinding().getId())))
                .key("codiBaudRate").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiDevice()==null ? "null":mcuCodi.getMcuCodiDevice().getCodiBaudRate())))
                .key("codiDataBit").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiDevice()==null ? "null":mcuCodi.getMcuCodiDevice().getCodiDataBit())))
                .key("codiDevice").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiDevice()==null ? "null":mcuCodi.getMcuCodiDevice().getCodiDataBit())))
                .key("codiDeviceCodiID").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiDevice()==null ? "null":mcuCodi.getMcuCodiDevice().getCodiID())))
                .key("codiDevicecodiIndex").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiDevice()==null ? "null":mcuCodi.getMcuCodiDevice().getCodiIndex())))
                .key("codiParityBit").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiDevice()==null ? "null":mcuCodi.getMcuCodiDevice().getCodiParityBit())))
                .key("codiRtsCts").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiDevice()==null ? "null":mcuCodi.getMcuCodiDevice().getCodiRtsCts())))
                .key("codiStopBit").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiDevice()==null ? "null":mcuCodi.getMcuCodiDevice().getCodiStopBit())))
                .key("codiDeviceId").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiDevice()==null ? "null":mcuCodi.getMcuCodiDevice().getId())))
                .key("codiAddressTableSize").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiMemory()==null ? "null":mcuCodi.getMcuCodiMemory().getCodiAddressTableSize())))
                .key("codiMemoryCodiID").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiMemory()==null ? "null":mcuCodi.getMcuCodiMemory().getCodiID())))
                .key("codiMemoryCodiIndex").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiMemory()==null ? "null":mcuCodi.getMcuCodiMemory().getCodiIndex())))
                .key("codiKeyTableSize").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiMemory()==null ? "null":mcuCodi.getMcuCodiMemory().getCodiKeyTableSize())))
                .key("codiMaxChildren").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiMemory()==null ? "null":mcuCodi.getMcuCodiMemory().getCodiMaxChildren())))
                .key("codiMaxHops").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiMemory()==null ? "null":mcuCodi.getMcuCodiMemory().getCodiMaxHops())))
                .key("codiNeighborTableSize").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiMemory()==null ? "null":mcuCodi.getMcuCodiMemory().getCodiNeighborTableSize())))
                .key("codiPacketBufferCount").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiMemory()==null ? "null":mcuCodi.getMcuCodiMemory().getCodiPacketBufferCount())))
                .key("codiRouteTableSize").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiMemory()==null ? "null":mcuCodi.getMcuCodiMemory().getCodiRouteTableSize())))
                .key("codiSoftwareVersion").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiMemory()==null ? "null":mcuCodi.getMcuCodiMemory().getCodiSoftwareVersion())))
                .key("codiSourceRouteTableSize").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiMemory()==null ? "null":mcuCodi.getMcuCodiMemory().getCodiSourceRouteTableSize())))
                .key("codiWholeAddressTableSize").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiMemory()==null ? "null":mcuCodi.getMcuCodiMemory().getCodiWholeAddressTableSize())))
                .key("codiMemoryId").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiMemory()==null ? "null":mcuCodi.getMcuCodiMemory().getId())))
                .key("codiNeighborAge").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiNeighbor()==null ? "null":mcuCodi.getMcuCodiNeighbor().getCodiNeighborAge())))
                .key("codiNeighborNeighborId").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiNeighbor()==null ? "null":mcuCodi.getMcuCodiNeighbor().getCodiNeighborId())))
                .key("codiNeighborInCost").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiNeighbor()==null ? "null":mcuCodi.getMcuCodiNeighbor().getCodiNeighborInCost())))
                .key("codiNeighborIndex").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiNeighbor()==null ? "null":mcuCodi.getMcuCodiNeighbor().getCodiNeighborIndex())))
                .key("codiNeighborLqi").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiNeighbor()==null ? "null":mcuCodi.getMcuCodiNeighbor().getCodiNeighborLqi())))
                .key("codiNeighborOutCost").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiNeighbor()==null ? "null":mcuCodi.getMcuCodiNeighbor().getCodiNeighborOutCost())))
                .key("codiNeighborShortId").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiNeighbor()==null ? "null":mcuCodi.getMcuCodiNeighbor().getCodiNeighborShortId())))
                .key("codiNeighborId").value((mcuCodi==null ? "null": (mcuCodi.getMcuCodiNeighbor()==null ? "null":mcuCodi.getMcuCodiNeighbor().getId())))
                */
                .key("childMcus").array();
            if(childMcus != null){
                 for(MCU childMcu:childMcus){
                      js.value(JSONSerializer.toJSON(childMcu.toJSONString()));
                 }
            }

            js.endArray();

            js.key("modem").array();

            Iterator<Modem> it = this.modem.iterator();
            if(it.hasNext()) {
                while(it.hasNext()) {
                    Modem m = (Modem) it.next();
                    js.value(JSONSerializer.toJSON(m.toJSONString()));
                }
                js.endArray();
            } else {
                js.endArray();
            }


            js.endObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return js.toString();
    }

    @Override
    public String getInstanceName() {
        return this.getSysID();
    }
}