package com.aimir.model.device;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.audit.IAuditable;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.SimpleSignalLevel;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.Zone;


/**
 * 
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>EndDevice.java Description </p>
 * End Device (Appliance Control Device에 연결되어 스스로가 아닌 ACD에 의해 전력 차단이나 사용량 측정이 되는 장치) <br>
 * 워터펌프, 가전기기 에어콘 등등 <br>
 * <pre>
 * 가전기기 또는 설비(공조, 냉온항습기, 조명, 화재, 보안 등)는 ACD 장치와 붙거나 PLC 모뎀과 통신하여 기기정보획득이나 제어가 가능해질 전망이다. 
 * 각 종 설비는 그 자체적으로 에너지 사용량 정보와 통신 프로토콜을 가지고 있을 수 있다. FMS를 통해서 그러한 정보를 수집할 수도 있다. 
 * </pre>
 * 
 * <pre>
 * Date          Version     Author   Description 
 * -              V1.0       -         신규작성 
 * 2011. 5. 04.   v1.1       eunmiae   [HEMS] 항목추가 : 그룹명, 이미지파일명         
 * 2011. 5. 09.   v1.2       eunmiae   [HEMS] 항목명 변경 : endDeviceGroupName -> homeDeviceGroupName로 변경 
 * 2011. 5. 25.   v1.3       eunmiae   [HEMS] 항목명 변경 : categoryCode의 데이터 내용 변경
 * </pre>
 * 
 */
@Entity
public class EndDevice implements JSONString, IAuditable {

	private static final long serialVersionUID = -5284044128690521501L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ENDDEVICE_SEQ")
    @SequenceGenerator(name="ENDDEVICE_SEQ", sequenceName="ENDDEVICE_SEQ", allocationSize=1) 
	private Integer id;

//    @Version
//    Integer version;
    
    @ColumnInfo(name="공급사아이디", descr="공급사 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUPPLIER_ID")
    @ReferencedBy(name="name")
    private Supplier supplier;
    
    @Column(name="SUPPLIER_ID", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;
    
    @ColumnInfo(name="모뎀아이디", descr="장치 모뎀 테이블의 ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="MODEM_ID")
    @ReferencedBy(name="deviceSerial")
    private Modem modem;
    
    @Column(name="MODEM_ID", nullable=true, updatable=false, insertable=false)
    private Integer modemId;
    
    @ColumnInfo(name="지역아이디", descr="지역 테이블의 ID나  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="LOCATION_ID")
	@ReferencedBy(name="name")
	private Location location;
    
    @Column(name="LOCATION_ID", nullable=true, updatable=false, insertable=false)
    private Integer locationId;
    
    @ColumnInfo(name="Zone아이디", descr="Zone 테이블의 ID나  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ZONE_ID")
	@ReferencedBy(name="name")
	private Zone zone;
    
    @Column(name="ZONE_ID", nullable=true, updatable=false, insertable=false)
    private Integer zoneId;

	@ColumnInfo(name="UUID", descr="Universally Unique IDentifier")
    @Column(name="UUID", unique=true)
    private	String uuid;
    
    @ColumnInfo(name="UPC", descr="(Universal Product Code) bar code 12 characters")
    @Column(name="UPC")
    private	String upc;

    @ColumnInfo(name="categoryCode", descr="EndDevice 유형(스마트 콘센트, 일반 가전, 스마트 가전등)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="category_id")
    @ReferencedBy(name="code")
    private Code categoryCode;
    
    @Column(name="category_id", nullable=true, updatable=false, insertable=false)
    private Integer categoryCodeId;
    
    @ColumnInfo(name="controllerCode", descr="제어 장비 분류코드 ACD,BACnet,FMS 등등")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="controller_id")
    @ReferencedBy(name="code")
    private Code controllerCode;
    
    @Column(name="controller_id", nullable=true, updatable=false, insertable=false)
    private Integer controllerCodeId;
    
    @ColumnInfo(name="friendlyName", descr="IHD에서 지정한 별명")
    @Column(name="friendly_Name")
	private	String friendlyName;
    
    @ColumnInfo(name="모뎀 모델", descr="미터 제조사 모델의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="devicemodel_id")
    @ReferencedBy(name="name")
    private DeviceModel model;
    
    @Column(name="devicemodel_id", nullable=true, updatable=false, insertable=false)
    private Integer deviceModelId;

	@ColumnInfo(name="manufacturer", descr="설비 기기 제조사")
    @Column(name="manufacturer")
    private	String manufacturer;			

    @ColumnInfo(name="manufacturer", descr="설비 기기 모델명")
    @Column(name="MODEL_NAME")
	private	String modelName;
	
    @ColumnInfo(name="manufacturer", descr="설비 기기 제조사")
    @Column(name="MODEL_NUMBER")
    private	String modelNumber;
    
    @ColumnInfo(name="serialNumber", descr="장치 시리얼 번호")
    @Column(name="SERIAL_NUMBER")
    private	String serialNumber;
    
    @ColumnInfo(name="powerConsumption", descr="소비전력  한시간당 소비되는 전력")
    @Column(name="POWER_CONSUMPTION")
    private Double powerConsumption; 
    
    @ColumnInfo(name="energyEfficiency", descr="에너지 소비 효율 1등급 ~5등급 등")
    @Column(name="ENERGY_EFFICIENCY")
    private Integer energyEfficiency;
    
    @ColumnInfo(name="statusCode", descr="전력 차단 상태")
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id")
	@ReferencedBy(name="code")
    private Code statusCode;
    
    @Column(name="status_id", nullable=true, updatable=false, insertable=false)
    private Integer statusCodeId;
    
    @ColumnInfo(name="macAddr", descr="맥 어드레스")
    @Column(name="MAC_ADDR")
    private	String macAddr;	
    
    @ColumnInfo(name="", descr="protocol type (CDMA,GSM,GPRS,PSTN,LAN,ZigBee,WiMAX,Serial,PLC,Bluetooth,SMS)")
    @Enumerated(EnumType.STRING)
    @Column(name="PROTOCOL_TYPE")
    private Protocol protocolType;
    
    @ColumnInfo(name="설치일자", descr="YYYYMMDDHHMMSS")
    @Column(name="INSTALL_DATE", length=14)
    private String installDate;
    
    @ColumnInfo(name="제조일자", descr="YYYYMMDDHHMMSS")
    @Column(name="MANUFACTURE_DATE", length=14)
    private String manufactureDate;
    
    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name = "enddevice_id")
    @ColumnInfo(name="미터")
    private Set<Meter> meters = new HashSet<Meter>(0); 

	private Code energyType;

	private Boolean loadControl;

	private Integer drLevel;

	private Boolean drProgramMandatory; // DR 프로그램이필수인지여부 (사용자의동의)

	private SimpleSignalLevel simpleSignalLevel;

    /* 2011. 5. 04 v1.1 HEMS 제품관리 항목 추가 ADD START eunmiae */
    /* 2011. 5. 09 v1.2 HEMS 제품관리 항목 추가 UPDATE START eunmiae */	
    @ColumnInfo(name="homeDeviceGroupName", descr="댁내 EndDevice의 그룹명 (room1, room2 ~ room10)")
    @Column(name="HOMEDEVICE_GROUP_NAME", length=10)
    private String homeDeviceGroupName;
    /* 2011. 5. 09 v1.2 HEMS 제품관리 항목 추가 UPDATE END eunmiae */

	@ColumnInfo(name="Home Device의 이미지 파일명")
    @Column(name="HOMEDEVICE_IMG_FILENAME", length=250)
    private String homeDeviceImgFilename;

    @ColumnInfo(name="installStatus", descr="EndDevice(Smart Concent)의 인스톨 상태")
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "installStatus_id")
	@ReferencedBy(name="code")
	private Code installStatus;
    
    @Column(name="installStatus_id", nullable=true, updatable=false, insertable=false)
    private Integer installStatusCodeId;

    public Code getInstallStatus() {
		return installStatus;
	}

	public void setInstallStatus(Code installStatus) {
		this.installStatus = installStatus;
	}

	public String getHomeDeviceGroupName() {
		return homeDeviceGroupName;
	}

	public void setHomeDeviceGroupName(String homeDeviceGroupName) {
		this.homeDeviceGroupName = homeDeviceGroupName;
	}

	public String getHomeDeviceImgFilename() {
		return homeDeviceImgFilename;
	}

	public void setHomeDeviceImgFilename(String homeDeviceImgFilename) {
		this.homeDeviceImgFilename = homeDeviceImgFilename;
	}

    /* 2011. 5. 04 v1.1 HEMS 제품관리관리를 위한 항목 추가 ADD END eunmiae */
    
	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    @XmlTransient
    public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@XmlTransient
	public Modem getModem() {
		return modem;
	}

	public void setModem(Modem modem) {
		this.modem = modem;
	}
	    
	@XmlTransient
    public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	
	@XmlTransient
	public Zone getZone() {
		return zone;
	}

	public void setZone(Zone zone) {
		this.zone = zone;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUpc() {
		return upc;
	}

	public void setUpc(String upc) {
		this.upc = upc;
	}

	@XmlTransient
	public Code getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(Code categoryCode) {
		this.categoryCode = categoryCode;
	}
	
	@XmlTransient
	public Code getControllerCode() {
		return controllerCode;
	}

	public void setControllerCode(Code controllerCode) {
		this.controllerCode = controllerCode;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}
	    
	@XmlTransient
    public DeviceModel getModel() {
		return model;
	}

	public void setModel(DeviceModel model) {
		this.model = model;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Double getPowerConsumption() {
		return powerConsumption;
	}

	public void setPowerConsumption(Double powerConsumption) {
		this.powerConsumption = powerConsumption;
	}

	public Integer getEnergyEfficiency() {
		return energyEfficiency;
	}

	public void setEnergyEfficiency(Integer energyEfficiency) {
		this.energyEfficiency = energyEfficiency;
	}

	@XmlTransient
	public Code getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Code statusCode) {
		this.statusCode = statusCode;
	}

	public String getMacAddr() {
		return macAddr;
	}

	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}

	public Protocol getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(Protocol protocolType) {
		this.protocolType = protocolType;
	}
	
	
	public String getInstallDate() {
        return installDate;
    }

    public void setInstallDate(String installDate) {
        this.installDate = installDate;
    }
    
    public String getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(String manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    @XmlTransient
    public Set<Meter> getMeters() {
        return meters;
    }

    public void setMeters(Set<Meter> meters) {
        this.meters = meters;
    }

    @XmlTransient
    public Code getEnergyType() {
		return energyType;
	}

	public void setEnergyType(Code energyType) {
		this.energyType = energyType;
	}

	public Boolean getLoadControl() {
		return loadControl;
	}

	public void setLoadControl(Boolean loadControl) {
		this.loadControl = loadControl;
	}

	public Integer getDrLevel() {
		return drLevel;
	}

	public void setDrLevel(Integer drLevel) {
		this.drLevel = drLevel;
	}

	public Boolean getDrProgramMandatory() {
		return drProgramMandatory;
	}

	public void setDrProgramMandatory(Boolean drProgramMandatory) {
		this.drProgramMandatory = drProgramMandatory;
	}

	public SimpleSignalLevel getSimpleSignalLevel() {
		return simpleSignalLevel;
	}

	public void setSimpleSignalLevel(SimpleSignalLevel simpleSignalLevel) {
		this.simpleSignalLevel = simpleSignalLevel;
	}

	public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getModemId() {
        return modemId;
    }

    public void setModemId(Integer modemId) {
        this.modemId = modemId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public Integer getCategoryCodeId() {
        return categoryCodeId;
    }

    public void setCategoryCodeId(Integer categoryCodeId) {
        this.categoryCodeId = categoryCodeId;
    }

    public Integer getControllerCodeId() {
        return controllerCodeId;
    }

    public void setControllerCodeId(Integer controllerCodeId) {
        this.controllerCodeId = controllerCodeId;
    }

    public Integer getDeviceModelId() {
        return deviceModelId;
    }

    public void setDeviceModelId(Integer deviceModelId) {
        this.deviceModelId = deviceModelId;
    }

    public Integer getStatusCodeId() {
        return statusCodeId;
    }

    public void setStatusCodeId(Integer statusCodeId) {
        this.statusCodeId = statusCodeId;
    }

    public Integer getInstallStatusCodeId() {
        return installStatusCodeId;
    }

    public void setInstallStatusCodeId(Integer installStatusCodeId) {
        this.installStatusCodeId = installStatusCodeId;
    }

    @Override
    public String toString()
    {
        return "EndDevice " + toJSONString();
    }
    public String toJSONString() {
    	JSONStringer js = null;

    	try {
    		js = new JSONStringer();
    		js.object().key("id").value(this.id)
    				   .key("level").value("endDevice")
    				   .key("name").value(this.friendlyName).endObject();
    			
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    	return js.toString();
    }
    
    @Override
    public String getInstanceName() {
        return this.getFriendlyName();
    }
}