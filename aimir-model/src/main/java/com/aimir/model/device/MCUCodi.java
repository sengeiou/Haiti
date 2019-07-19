package com.aimir.model.device;


import javax.persistence.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONSerializer;
import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.Scope;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p> 집중기 (DCU) 내부 인터페이스에 장착되어 있는 Coordinator 정보</p>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="MCU_CODI")
public class MCUCodi extends BaseObject implements JSONString, IAuditable {
	
	private static final long serialVersionUID = -5723493667628682978L;
			
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MCU_CODI_SEQ")
    @SequenceGenerator(name="MCU_CODI_SEQ", sequenceName="MCU_CODI_SEQ", allocationSize=1) 
	private Integer id;
	
    @ColumnInfo(name="집중기아이디", descr="집중기 테이블의 ID 혹은  NULL")
    @OneToOne(mappedBy="mcuCodi", fetch=FetchType.LAZY)
    private MCU mcu;
    
    @ColumnInfo(name="집중기 코디 바인딩 아이디", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="집중기 바인딩 정보와 연결")
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="MCU_CODI_BINDING_ID")
	private MCUCodiBinding mcuCodiBinding;
    
    @Column(name="MCU_CODI_BINDING_ID", nullable=true, updatable=false, insertable=false)
    private Integer mcuCodiBindingId;
	
    @ColumnInfo(name="집중기 코디 장비 아이디", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="집중기 코디 장비 정보와 연결")
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="MCU_CODI_DEVICE_ID")
	private MCUCodiDevice mcuCodiDevice;
    
    @Column(name="MCU_CODI_DEVICE_ID", nullable=true, updatable=false, insertable=false)
    private Integer mcuCodiDeviceId;
	
    @ColumnInfo(name="집중기 코디 연관 아이디", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="집중기 코디 연결 정보와 연결")
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="MCU_CODI_NEIGHBOR_ID")
	private MCUCodiNeighbor mcuCodiNeighbor;
    
    @Column(name="MCU_CODI_NEIGHBOR_ID", nullable=true, updatable=false, insertable=false)
    private Integer mcuCodiNeighborId;
	
    @ColumnInfo(name="집중기 코디 메모리 아이디", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="집중기 코디 메모리 정보와 연결")
	@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name="MCU_CODI_MEMORY_ID")
	private MCUCodiMemory mcuCodiMemory;
    
    @Column(name="MCU_CODI_MEMORY_ID", nullable=true, updatable=false, insertable=false)
    private Integer mcuCodiMemoryId;
	
    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_MASK") 
	private Integer codiMask;

    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_INDEX") 
	private Integer codiIndex;

    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_STRING") 
	private String codiString;
    
    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_ID") 
	private String codiID;

    @ColumnInfo(name="CODI Type", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_TYPE") 
	private Integer codiType;

    @ColumnInfo(name="Short Id", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_SHORT_ID") 
	private Integer codiShortID;

    @ColumnInfo(name="FW ver.", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_FW_VER") 
	private String codiFwVer;

    @ColumnInfo(name="HW Ver.", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_HW_VER") 
	private String codiHwVer;

    @ColumnInfo(name="ZA IF Ver.", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_ZA_IF_VER") 
	private Integer codiZAIfVer;

    @ColumnInfo(name="ZZ IF Ver.", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_ZZ_IF_VER") 
	private Integer codiZZIfVer;

    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_FW_BUILD") 
	private String codiFwBuild;

    @ColumnInfo(name="Reset Kind", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_RESET_KIND") 
	private Integer codiResetKind;

    @ColumnInfo(name="Channel ID", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_CHANNEL") 
	private Integer codiChannel;

    @ColumnInfo(name="PAN ID", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_PAN_ID") 
	private String codiPanID;

    @ColumnInfo(name="Ext. PAN ID", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_EXT_PAN_ID") 
	private String codiExtPanId;

    @ColumnInfo(name="RF Power", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_RF_POWER") 
	private Integer codiRfPower;

    @ColumnInfo(name="TX Power", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_TX_POWER_MODE") 
	private Integer codiTxPowerMode;

    @ColumnInfo(name="Permission", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_PERMIT") 
	private Integer codiPermit;

    @ColumnInfo(name="Encrypt", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_ENABLE_ENCRYPT") 
	private Integer codiEnableEncrypt;

    @ColumnInfo(name="Link Key", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_LINK_KEY") 
	private String codiLinkKey;

    @ColumnInfo(name="Network Key", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_NETWORK_KEY") 
	private String codiNetworkKey;

    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_ROUTE_DISCOVERY") 
	private Boolean codiRouteDiscovery;

    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_MULTICAST_HOPS") 
	private Integer codiMulticastHops;
    
    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=false, devicecontrol=false), descr="자동 채널 세팅")
    @Column(name="CODI_AUTO_SETTING") 
	private Boolean codiAutoSetting;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @XmlTransient
    public MCUCodiBinding getMcuCodiBinding() {
        return mcuCodiBinding;
    }

    public void setMcuCodiBinding(MCUCodiBinding mcuCodiBinding) {
        this.mcuCodiBinding = mcuCodiBinding;
    }

    @XmlTransient
    public MCUCodiDevice getMcuCodiDevice() {
        return mcuCodiDevice;
    }

    public void setMcuCodiDevice(MCUCodiDevice mcuCodiDevice) {
        this.mcuCodiDevice = mcuCodiDevice;
    }

    @XmlTransient
    public MCUCodiNeighbor getMcuCodiNeighbor() {
        return mcuCodiNeighbor;
    }

    public void setMcuCodiNeighbor(MCUCodiNeighbor mcuCodiNeighbor) {
        this.mcuCodiNeighbor = mcuCodiNeighbor;
    }

    @XmlTransient
    public MCUCodiMemory getMcuCodiMemory() {
        return mcuCodiMemory;
    }

    public void setMcuCodiMemory(MCUCodiMemory mcuCodiMemory) {
        this.mcuCodiMemory = mcuCodiMemory;
    }

    public Integer getCodiMask() {
        return codiMask;
    }

    public void setCodiMask(Integer codiMask) {
        this.codiMask = codiMask;
    }

    public Integer getCodiIndex() {
        return codiIndex;
    }

    public void setCodiIndex(Integer codiIndex) {
        this.codiIndex = codiIndex;
    }

    public String getCodiString() {
        return codiString;
    }
    
    public void setCodiString(String codiString) {
        this.codiString = codiString;
    }
    
    public String getCodiID() {
        return codiID;
    }
    
    public void setCodiID(String codiID) {
        this.codiID = codiID;
    }

    public Integer getCodiType() {
        return codiType;
    }

    public void setCodiType(Integer codiType) {
        this.codiType = codiType;
    }

    public Integer getCodiShortID() {
        return codiShortID;
    }

    public void setCodiShortID(Integer codiShortID) {
        this.codiShortID = codiShortID;
    }

    public String getCodiFwVer() {
        return codiFwVer;
    }

    public void setCodiFwVer(String codiFwVer) {
        this.codiFwVer = codiFwVer;
    }

    public String getCodiHwVer() {
        return codiHwVer;
    }

    public void setCodiHwVer(String codiHwVer) {
        this.codiHwVer = codiHwVer;
    }

    public Integer getCodiZAIfVer() {
        return codiZAIfVer;
    }

    public void setCodiZAIfVer(Integer codiZAIfVer) {
        this.codiZAIfVer = codiZAIfVer;
    }

    public Integer getCodiZZIfVer() {
        return codiZZIfVer;
    }

    public void setCodiZZIfVer(Integer codiZZIfVer) {
        this.codiZZIfVer = codiZZIfVer;
    }

    public String getCodiFwBuild() {
        return codiFwBuild;
    }

    public void setCodiFwBuild(String codiFwBuild) {
        this.codiFwBuild = codiFwBuild;
    }

    public Integer getCodiResetKind() {
        return codiResetKind;
    }

    public void setCodiResetKind(Integer codiResetKind) {
        this.codiResetKind = codiResetKind;
    }

    public Integer getCodiChannel() {
        return codiChannel;
    }

    public void setCodiChannel(Integer codiChannel) {
        this.codiChannel = codiChannel;
    }

    public String getCodiPanID() {
        return codiPanID;
    }

    public void setCodiPanID(String codiPanID) {
        this.codiPanID = codiPanID;
    }

    public String getCodiExtPanId() {
        return codiExtPanId;
    }

    public void setCodiExtPanId(String codiExtPanId) {
        this.codiExtPanId = codiExtPanId;
    }

    public Integer getCodiRfPower() {
        return codiRfPower;
    }

    public void setCodiRfPower(Integer codiRfPower) {
        this.codiRfPower = codiRfPower;
    }

    public Integer getCodiTxPowerMode() {
        return codiTxPowerMode;
    }

    public void setCodiTxPowerMode(Integer codiTxPowerMode) {
        this.codiTxPowerMode = codiTxPowerMode;
    }

    public Integer getCodiPermit() {
        return codiPermit;
    }

    public void setCodiPermit(Integer codiPermit) {
        this.codiPermit = codiPermit;
    }

    public Integer getCodiEnableEncrypt() {
        return codiEnableEncrypt;
    }

    public void setCodiEnableEncrypt(Integer codiEnableEncrypt) {
        this.codiEnableEncrypt = codiEnableEncrypt;
    }

    public String getCodiLinkKey() {
        return codiLinkKey;
    }

    public void setCodiLinkKey(String codiLinkKey) {
        this.codiLinkKey = codiLinkKey;
    }

    public String getCodiNetworkKey() {
        return codiNetworkKey;
    }

    public void setCodiNetworkKey(String codiNetworkKey) {
        this.codiNetworkKey = codiNetworkKey;
    }

    public Boolean getCodiRouteDiscovery() {
        return codiRouteDiscovery;
    }

    public void setCodiRouteDiscovery(Boolean codiRouteDiscovery) {
        this.codiRouteDiscovery = codiRouteDiscovery;
    }

    public Integer getCodiMulticastHops() {
        return codiMulticastHops;
    }

    public void setCodiMulticastHops(Integer codiMulticastHops) {
        this.codiMulticastHops = codiMulticastHops;
    }
    

    public Boolean getCodiAutoSetting() {
		return codiAutoSetting;
	}

	public void setCodiAutoSetting(Boolean codiAutoSetting) {
		this.codiAutoSetting = codiAutoSetting;
	}

	public void setMcu(MCU mcu) {
        this.mcu = mcu;
    }

	@XmlTransient
    public MCU getMcu() {
        return mcu;
    }

    public Integer getMcuCodiBindingId() {
        return mcuCodiBindingId;
    }

    public void setMcuCodiBindingId(Integer mcuCodiBindingId) {
        this.mcuCodiBindingId = mcuCodiBindingId;
    }

    public Integer getMcuCodiDeviceId() {
        return mcuCodiDeviceId;
    }

    public void setMcuCodiDeviceId(Integer mcuCodiDeviceId) {
        this.mcuCodiDeviceId = mcuCodiDeviceId;
    }

    public Integer getMcuCodiNeighborId() {
        return mcuCodiNeighborId;
    }

    public void setMcuCodiNeighborId(Integer mcuCodiNeighborId) {
        this.mcuCodiNeighborId = mcuCodiNeighborId;
    }

    public Integer getMcuCodiMemoryId() {
        return mcuCodiMemoryId;
    }

    public void setMcuCodiMemoryId(Integer mcuCodiMemoryId) {
        this.mcuCodiMemoryId = mcuCodiMemoryId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        result = prime * result
                + ((codiChannel == null) ? 0 : codiChannel.hashCode());
        result = prime
                * result
                + ((codiEnableEncrypt == null) ? 0 : codiEnableEncrypt
                        .hashCode());
        result = prime * result
                + ((codiExtPanId == null) ? 0 : codiExtPanId.hashCode());
        result = prime * result
                + ((codiFwBuild == null) ? 0 : codiFwBuild.hashCode());
        result = prime * result
                + ((codiFwVer == null) ? 0 : codiFwVer.hashCode());
        result = prime * result
                + ((codiHwVer == null) ? 0 : codiHwVer.hashCode());
        result = prime * result
                + ((codiIndex == null) ? 0 : codiIndex.hashCode());
        result = prime * result
                + ((codiLinkKey == null) ? 0 : codiLinkKey.hashCode());
        result = prime * result
                + ((codiMask == null) ? 0 : codiMask.hashCode());
        result = prime
                * result
                + ((codiMulticastHops == null) ? 0 : codiMulticastHops
                        .hashCode());
        result = prime * result
                + ((codiNetworkKey == null) ? 0 : codiNetworkKey.hashCode());
        result = prime * result
                + ((codiPanID == null) ? 0 : codiPanID.hashCode());
        result = prime * result
                + ((codiPermit == null) ? 0 : codiPermit.hashCode());
        result = prime * result
                + ((codiResetKind == null) ? 0 : codiResetKind.hashCode());
        result = prime * result
                + ((codiRfPower == null) ? 0 : codiRfPower.hashCode());
        result = prime * result + ((codiRouteDiscovery==null) ? 1231 : 1237);
        result = prime * result
                + ((codiShortID == null) ? 0 : codiShortID.hashCode());
        result = prime * result
                + ((codiString == null) ? 0 : codiString.hashCode());
        result = prime * result
                + ((codiTxPowerMode == null) ? 0 : codiTxPowerMode.hashCode());
        result = prime * result
                + ((codiType == null) ? 0 : codiType.hashCode());
        result = prime * result
                + ((codiZAIfVer == null) ? 0 : codiZAIfVer.hashCode());
        result = prime * result
                + ((codiZZIfVer == null) ? 0 : codiZZIfVer.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((mcuCodiBinding == null) ? 0 : mcuCodiBinding.hashCode());
        result = prime * result
                + ((mcuCodiDevice == null) ? 0 : mcuCodiDevice.hashCode());
        result = prime * result
                + ((mcuCodiMemory == null) ? 0 : mcuCodiMemory.hashCode());
        result = prime * result
                + ((mcuCodiNeighbor == null) ? 0 : mcuCodiNeighbor.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        MCUCodi other = (MCUCodi) obj;
        if (codiChannel == null) {
            if (other.codiChannel != null)
                return false;
        } else if (!codiChannel.equals(other.codiChannel))
            return false;
        if (codiEnableEncrypt == null) {
            if (other.codiEnableEncrypt != null)
                return false;
        } else if (!codiEnableEncrypt.equals(other.codiEnableEncrypt))
            return false;
        if (codiExtPanId == null) {
            if (other.codiExtPanId != null)
                return false;
        } else if (!codiExtPanId.equals(other.codiExtPanId))
            return false;
        if (codiFwBuild == null) {
            if (other.codiFwBuild != null)
                return false;
        } else if (!codiFwBuild.equals(other.codiFwBuild))
            return false;
        if (codiFwVer == null) {
            if (other.codiFwVer != null)
                return false;
        } else if (!codiFwVer.equals(other.codiFwVer))
            return false;
        if (codiHwVer == null) {
            if (other.codiHwVer != null)
                return false;
        } else if (!codiHwVer.equals(other.codiHwVer))
            return false;
        if (codiIndex == null) {
            if (other.codiIndex != null)
                return false;
        } else if (!codiIndex.equals(other.codiIndex))
            return false;
        if (codiLinkKey == null) {
            if (other.codiLinkKey != null)
                return false;
        } else if (!codiLinkKey.equals(other.codiLinkKey))
            return false;
        if (codiMask == null) {
            if (other.codiMask != null)
                return false;
        } else if (!codiMask.equals(other.codiMask))
            return false;
        if (codiMulticastHops == null) {
            if (other.codiMulticastHops != null)
                return false;
        } else if (!codiMulticastHops.equals(other.codiMulticastHops))
            return false;
        if (codiNetworkKey == null) {
            if (other.codiNetworkKey != null)
                return false;
        } else if (!codiNetworkKey.equals(other.codiNetworkKey))
            return false;
        if (codiPanID == null) {
            if (other.codiPanID != null)
                return false;
        } else if (!codiPanID.equals(other.codiPanID))
            return false;
        if (codiPermit == null) {
            if (other.codiPermit != null)
                return false;
        } else if (!codiPermit.equals(other.codiPermit))
            return false;
        if (codiResetKind == null) {
            if (other.codiResetKind != null)
                return false;
        } else if (!codiResetKind.equals(other.codiResetKind))
            return false;
        if (codiRfPower == null) {
            if (other.codiRfPower != null)
                return false;
        } else if (!codiRfPower.equals(other.codiRfPower))
            return false;
        if (codiRouteDiscovery != other.codiRouteDiscovery)
            return false;
        if (codiShortID == null) {
            if (other.codiShortID != null)
                return false;
        } else if (!codiShortID.equals(other.codiShortID))
            return false;
        if (codiString == null) {
            if (other.codiString != null)
                return false;
        } else if (!codiString.equals(other.codiString))
            return false;
        if (codiTxPowerMode == null) {
            if (other.codiTxPowerMode != null)
                return false;
        } else if (!codiTxPowerMode.equals(other.codiTxPowerMode))
            return false;
        if (codiType == null) {
            if (other.codiType != null)
                return false;
        } else if (!codiType.equals(other.codiType))
            return false;
        if (codiZAIfVer == null) {
            if (other.codiZAIfVer != null)
                return false;
        } else if (!codiZAIfVer.equals(other.codiZAIfVer))
            return false;
        if (codiZZIfVer == null) {
            if (other.codiZZIfVer != null)
                return false;
        } else if (!codiZZIfVer.equals(other.codiZZIfVer))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (mcuCodiBinding == null) {
            if (other.mcuCodiBinding != null)
                return false;
        } else if (!mcuCodiBinding.equals(other.mcuCodiBinding))
            return false;
        if (mcuCodiDevice == null) {
            if (other.mcuCodiDevice != null)
                return false;
        } else if (!mcuCodiDevice.equals(other.mcuCodiDevice))
            return false;
        if (mcuCodiMemory == null) {
            if (other.mcuCodiMemory != null)
                return false;
        } else if (!mcuCodiMemory.equals(other.mcuCodiMemory))
            return false;
        if (mcuCodiNeighbor == null) {
            if (other.mcuCodiNeighbor != null)
                return false;
        } else if (!mcuCodiNeighbor.equals(other.mcuCodiNeighbor))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MCUCodi [codiChannel=" + codiChannel + ", codiEnableEncrypt="
                + codiEnableEncrypt + ", codiExtPanId=" + codiExtPanId
                + ", codiFwBuild=" + codiFwBuild + ", codiFwVer=" + codiFwVer
                + ", codiHwVer=" + codiHwVer + ", codiIndex=" + codiIndex
                + ", codiLineKey=" + codiLinkKey + ", codiMask=" + codiMask
                + ", codiMulticastHops=" + codiMulticastHops
                + ", codiNetworkKey=" + codiNetworkKey + ", codiPanID="
                + codiPanID + ", codiPermit=" + codiPermit + ", codiResetKind="
                + codiResetKind + ", codiRfPower=" + codiRfPower
                + ", codiRouteDiscovery=" + codiRouteDiscovery
                + ", codiShortID=" + codiShortID + ", codiString=" + codiString
                + ", codiTxPowerMode=" + codiTxPowerMode + ", codiType="
                + codiType + ", codiZAIfVer=" + codiZAIfVer + ", codiZZIfVer="
                + codiZZIfVer + ", id=" + id + ", mcuCodiBinding="
                + mcuCodiBinding + ", mcuCodiDevice=" + mcuCodiDevice
                + ", mcuCodiMemory=" + mcuCodiMemory + ", mcuCodiNeighbor="
                + mcuCodiNeighbor + "]";
    }

	@Override
	public String toJSONString() {
		JSONStringer js = null;

    	try {
    		js = new JSONStringer();
    		js.object().key("id").value(id)
    		.key("codiID").value(codiID)
		    .key("codiChannel").value(codiChannel)
		   .key("codiEnableEncrypt").value(codiEnableEncrypt)
		   .key("codiExtPanId").value(codiExtPanId)
		   .key("codiFwBuild").value(codiFwBuild)
		   .key("codiFwVer").value(codiFwVer)
		   .key("codiHwVer").value(codiHwVer)
		   .key("codiIndex").value(codiIndex)
		   .key("codiLinkKey").value(codiLinkKey)
		   .key("codiMask").value(codiMask)
		   .key("codiMulticastHops").value(codiMulticastHops)
		   .key("codiNetworkKey").value(codiNetworkKey)
		   .key("codiPanID").value(codiPanID)
		   .key("codiPermit").value(codiPermit)
		   .key("codiResetKind").value(codiResetKind)
		   .key("codiRfPower").value(codiRfPower)
		   .key("codiRouteDiscovery").value(codiRouteDiscovery)
		   .key("codiShortID").value(codiShortID)
		   .key("codiString").value(codiString)
		   .key("codiTxPowerMode").value(codiTxPowerMode)
		   .key("codiType").value(codiType)
		   .key("codiZAIfVer").value(codiZAIfVer)
		   .key("codiZZIfVer").value(codiZZIfVer)
		   
		   .key("mcuCodiBinding").value((mcuCodiBinding != null ? JSONSerializer.toJSON(mcuCodiBinding.toJSONString()) : ""))
		   .key("mcuCodiDevice").value((mcuCodiDevice != null ? JSONSerializer.toJSON(mcuCodiDevice.toJSONString()) : ""))
		   .key("mcuCodiMemory").value((mcuCodiMemory != null ? JSONSerializer.toJSON(mcuCodiMemory.toJSONString()) : ""))
		   .key("mcuCodiNeighbor").value((mcuCodiNeighbor != null ? JSONSerializer.toJSON(mcuCodiNeighbor.toJSONString()) : ""));
		   
		   js.endObject();

    	} catch (Exception e) {
    		e.printStackTrace();
    		System.out.println(e);
    		
    	}
    	return js.toString();
	}
	
	@Override
	public String getInstanceName() {
	    return this.getMcu().getSysID()+":"+this.getCodiID();
	}
}