package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;


import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Location;
import com.aimir.model.system.Zone;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>EndDevice - End Device (Appliance Control Device에 연결되어 스스로가 아닌 ACD에 의해 전력 차단이나 사용량 측정이 되는 장치)</p>
 * 워터펌프, 가전기기 에어콘 등등 <br>
 * 
 * @author eunmiae
 */
@Entity
public class EndDeviceLog  extends BaseObject implements JSONString {

	private static final long serialVersionUID = -5284044128690521501L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ENDDEVICELOG_SEQ")
    @SequenceGenerator(name="ENDDEVICELOG_SEQ", sequenceName="ENDDEVICELOG_SEQ", allocationSize=1) 
	private Integer id;

    @Column(nullable = false)
	private String locationName;
    
    @ColumnInfo(name="friendlyName", descr="사용자가 지정한 이름")
    @Column(name="friendly_Name")
	private	String friendlyName;    
    
    @Column(nullable = false)
	private String categoryCode;
    
    @Column(nullable = false)
	private String preStatusCode;
    
    @Column(nullable = false)
	private String statusCode;
    
    @Column(length = 14, nullable = false)
	private String writeDatetime;  
    
    
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    //@Cascade(value=org.hibernate.annotations.CascadeType.DETACH)
	@JoinColumn(name = "enddevice_id")
    @ColumnInfo(name="엔드 디바이스 ")
    @ReferencedBy(name="serialNumber")    
	private EndDevice enddevice;
    
    @Column(name="enddevice_id", nullable=true, updatable=false, insertable=false)
    private Integer enddeviceId;
    
	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }    
    
    public String getCategoryCode() {
        return categoryCode;
    }
    
    @XmlTransient
    public EndDevice getEnddevice() {
		return enddevice;
	}

	public void setEnddevice(EndDevice enddevice) {
		this.enddevice = enddevice;
	}
	
    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }    
    
    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    
    public String getPreStatusCode() {
        return preStatusCode;
    }

    public void setPreStatusCode(String preStatusCode) {
        this.preStatusCode = preStatusCode;
    }
   
    public String getWriteDatetime() {
        return writeDatetime;
    }

    public void setWriteDatetime(String writeDatetime) {
        this.writeDatetime = writeDatetime;
    }
    
    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
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

    public Integer getEnddeviceId() {
        return enddeviceId;
    }

    public void setEnddeviceId(Integer enddeviceId) {
        this.enddeviceId = enddeviceId;
    }

    @Override
	public String toJSONString() {
    	JSONStringer js = new JSONStringer();
    	try {
    		js.object()
    			.key("id").value((this.id == null)? "-1":this.id)
    			.key("locationName").value((this.locationName == null)? "":this.locationName)
    			.key("friendlyName").value((this.friendlyName == null)? "":this.friendlyName)
    			.key("categoryCode").value((this.categoryCode == null)? "-1":this.categoryCode)
    			.key("preStatusCode").value((this.preStatusCode == null)? "-1":this.preStatusCode)
    			.key("statusCode").value((this.statusCode == null)? "-1":this.statusCode)
    			.key("writeDatetime").value((this.writeDatetime == null)? "":this.writeDatetime)
    			.key("location").value((this.location == null)? "":this.location.getName())
    			.key("zone").value((this.zone == null)? "":this.zone.getName())
    			.key("zoneId").value((this.zoneId == null)? "-1":this.zoneId)
    			.key("enddevice").value((this.enddevice == null)? "":this.enddevice.getFriendlyName())
    			.key("enddeviceId").value((this.enddeviceId == null)? "-1":this.enddeviceId);
    		js.endObject();
    		return js.toString();
    	} 
    	catch (Exception ignore) {
    		return "";
    	}
	}

	@Override
	public String toString() {
		return toJSONString();
	}

	@Override
	public boolean equals(Object o) {
		return this.equals(o);
	}

	@Override
	public int hashCode() {
		return this.toJSONString().hashCode();
	}
}