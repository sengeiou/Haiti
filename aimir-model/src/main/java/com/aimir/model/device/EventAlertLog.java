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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.Index;

import net.sf.json.JSONString;
import net.sf.json.util.JSONBuilder;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.SeverityType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.util.TimeLocaleUtil;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>EventAlert Log</p>
 * @author 최은정(ej8486)
 *
 */
@Entity
@Table(name="EVENTALERTLOG")
@Index(name="IDX_EVENTALERTLOG_01", columnNames={"ACTIVATOR_TYPE", "activatorId", "eventAlert_id"})
public class EventAlertLog extends BaseObject implements JSONString {

    private static final long serialVersionUID = 905580435229726851L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EVENTALERTLOG_SEQ")
	@SequenceGenerator(name="EVENTALERTLOG_SEQ", sequenceName="EVENTALERTLOG_SEQ", allocationSize=1) 
    private Long id;    //  ID(PK)

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="EVENTALERT_ID", nullable=false)
    @ColumnInfo(name="ALERT/EVENT")
    @ReferencedBy(name="name")
    private EventAlert eventAlert;
    
    @Column(name="EVENTALERT_ID", nullable=true, updatable=false, insertable=false)
    private Integer eventAlertId;
    
    @Column(name = "SEVERITY_TYPE", nullable=false)
    @ColumnInfo(name="SEVERITY LEVEL", descr="")
    @Enumerated(EnumType.STRING)
    private SeverityType severity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="supplier_id")
    @ColumnInfo(name="공급사")
    @ReferencedBy(name="name")
    private Supplier supplier;
    
    @Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;
    
    @Column(length=14)
    @ColumnInfo(name="발생시간", descr="YYYYMMDDHHMMSS")
    @Index(name="IDX_OPENTIME")
    private String openTime;
    
    @Column(length=14)
    @ColumnInfo(name="종료시간", descr="YYYYMMDDHHMMSS")
    private String closeTime;
    
    @Column(length=14, nullable=false)
    @ColumnInfo(name="서버저장시간", descr="YYYYMMDDHHMMSS")
    private String writeTime;
    
    @ColumnInfo(name="지속시간")
    private String duration;
    
    @Column(length=1024)
    @ColumnInfo(name="알림메세지")
    private String message;
    
    @Column(name = "STATUS", nullable=false)
    @ColumnInfo(name="STATUS", descr="")
    @Enumerated(EnumType.STRING)
    private EventStatus status;
    
    @ColumnInfo(name="발생횟수", descr="동일 EVENT/ALERT 발생 시 총 횟수")
    private Integer occurCnt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="LOCATION_ID")
    @ColumnInfo(name="발생지역")
    @ReferencedBy(name="name")
    private Location location;
    
    @Column(name="LOCATION_ID", nullable=true, updatable=false, insertable=false)
    private Integer locationId;

    @Column(name = "ACTIVATOR_TYPE", nullable=false)
    @ColumnInfo(name="발생대상타입")
    @Enumerated(EnumType.STRING)
    private TargetClass activatorType;
    
    @Column(length=100)
    @ColumnInfo(name="발생IP", descr="발생 대상의 IP")
    private String activatorIp;
    
    @Column(length=100, nullable=false)
    @ColumnInfo(name="발생ID", descr="발생 대상의 ID")
    private String activatorId;
    
	private transient Set<EventAlertAttr> eventAlertAttrs = new HashSet<EventAlertAttr>(0);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventAlert getEventAlert() {
        return eventAlert;
    }

    public void setEventAlert(EventAlert eventAlert) {
        this.eventAlert = eventAlert;
    }

    public SeverityType getSeverity() {
        return severity;
    }

    public void setSeverity(SeverityType severity) {
        this.severity = severity;
    }
    
    public void setSeverity(String severity) {
        this.severity = SeverityType.valueOf(severity);
    }
    
    @XmlTransient
    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(String writeTime) {
        this.writeTime = writeTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EventStatus getStatus() {
        return status;
    }
    
    public void setStatus(EventStatus status) {
        this.status = status;
    }
    
    public void setStatus(String status) {
        this.status = EventStatus.valueOf(status);
    }

    public Integer getOccurCnt() {
        return occurCnt;
    }

    public void setOccurCnt(Integer occurCnt) {
        this.occurCnt = occurCnt;
    }

    @XmlTransient
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public TargetClass getActivatorType() {
        return activatorType;
    }

    public void setActivatorType(TargetClass activatorType) {
        this.activatorType = activatorType;
    }
    
    public void setActivatorType(String activatorType) {
        this.activatorType = TargetClass.valueOf(activatorType);
    }

    public String getActivatorIp() {
        return activatorIp;
    }

    public void setActivatorIp(String activatorIp) {
        this.activatorIp = activatorIp;
    }

    public String getActivatorId() {
        return activatorId;
    }

    public void setActivatorId(String activatorId) {
        this.activatorId = activatorId;
    }

    @XmlTransient
    public Set<EventAlertAttr> getEventAlertAttrs() {
		return eventAlertAttrs;
	}

	public void setEventAlertAttrs(Set<EventAlertAttr> eventAlertAttrs) {
		this.eventAlertAttrs = eventAlertAttrs;
	}

	public void append(EventAlertAttr attr) {
	    this.eventAlertAttrs.add(attr);
	}
	
	/**
     * get event attribute value
     * @param attrName -  attribute name
     * @return attribute value
     */
    public String getEventAttrValue(String attrName)
    {
        for(EventAlertAttr attr : eventAlertAttrs.toArray(new EventAlertAttr[0]))
        {
            if (attr.getAttrName().equals(attrName))
                return attr.getValue();
        }
        return "";
    }

    /**
     * get event attribute
     * @param attrName
     * @return
     */
    public EventAlertAttr getEventAttr(String attrName)
    {
        for(EventAlertAttr attr : eventAlertAttrs.toArray(new EventAlertAttr[0]))
        {
            if (attr.getAttrName().equals(attrName))
                return attr;
        }
        return null;
    }
    
    public void remove(String attrName)
    {
        for(EventAlertAttr attr : eventAlertAttrs.toArray(new EventAlertAttr[0]))
        {
            if (attr.getAttrName().equals(attrName))
                eventAlertAttrs.remove(attr);
        }
    }
    
	public Integer getEventAlertId() {
        return eventAlertId;
    }

    public void setEventAlertId(Integer eventAlertId) {
        this.eventAlertId = eventAlertId;
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

    @Override
    public String toJSONString() {
    	JSONStringer js = null;

    	js = new JSONStringer();
    	JSONBuilder jb = js.object()
	    	.key("id").value(this.id)
	    	.key("EventAlert").value((this.eventAlert == null)? "":this.eventAlert.getId())
	    	.key("eventAlertName").value((this.eventAlert == null)? "":this.eventAlert.getName())
	    	.key("severity").value((this.severity == null)? "":this.severity.name())
	    	.key("supplier").value((this.supplier == null)? "":this.supplier.getId())    	
	    	.key("duration").value((this.duration == null)? "":this.duration)
	    	.key("message").value((this.message == null)? "":this.message)
	    	.key("status").value((this.status == null)? "":this.status.name())
	    	.key("occurCnt").value(this.occurCnt)
	    	.key("location").value((this.location == null)? "":this.location.getName())
	    	.key("activatorType").value((this.activatorType == null)? "":this.activatorType.name())
	    	.key("activatorIp").value((this.activatorIp == null)? "":this.activatorIp)
	    	.key("activatorId").value((this.activatorId == null)? "":this.activatorId);
	    	
    	String ot = (this.openTime == null) ? "" : this.openTime;
    	String ct = (this.closeTime == null) ? "" : this.closeTime;
    	String wt = (this.writeTime == null) ? "" : this.writeTime;
    	
    	// Supplier가 있다면, 날자 포매팅을 적용한다.
    	if(this.supplier != null) {
    		String lang = supplier.getLang().getCode_2letter();
        	String country = supplier.getCountry().getCode_2letter();
        	if(lang != null && country != null) {
	        	if(ot.length() > 0) ot = TimeLocaleUtil.getLocaleDate(ot, lang, country);
	        	if(ct.length() > 0) ct = TimeLocaleUtil.getLocaleDate(ct, lang, country);
	        	if(wt.length() > 0) wt = TimeLocaleUtil.getLocaleDate(wt, lang, country);
        	}
    	}
    	jb.key("openTime").value(ot)
    		.key("closeTime").value(ct)
    		.key("writeTime").value(wt)
    		.endObject();

    	return js.toString();
    }

    @Override
    public boolean equals(Object o) {
    	if(o instanceof EventAlertLog) {
    		EventAlertLog el = (EventAlertLog) o;
   			return this.id.equals(el.getId());
   		}
   		else {
   			return false;
   		}
    }

    @Override
    public int hashCode() {
        return (getClass() + this.id.toString()).hashCode();
    }

    @Override
    public String toString() {
        return toJSONString();
    }
}