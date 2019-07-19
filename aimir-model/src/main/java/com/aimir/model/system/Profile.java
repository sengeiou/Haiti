package com.aimir.model.system;

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

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.audit.IAuditable;
import com.aimir.constants.CommonConstants.EventAlertType;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.SeverityType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.model.BaseObject;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.MeterEvent;

/**
 * 이벤트 처리에 대한 개인별 프로파일 정보<br>
 * 이벤트 감시창에서 개별 시스템 사용자(로그인 사용자)가 이벤트 발생 시 처리관련 프로파일 정보<br>
 * 특정 사용자별, 이벤트 종류별 sound로 알리거나 우선순위에 대한 지정을 나타냄<br>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="PROFILE")
public class Profile extends BaseObject implements JSONString, IAuditable {

	private static final long serialVersionUID = -3713843501013837526L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PROFILE_SEQ")
	@SequenceGenerator(name="PROFILE_SEQ", sequenceName="PROFILE_SEQ", allocationSize=1)
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="OPERATOR_ID", nullable=false)
    @ColumnInfo(name="사용자정보")
	private Operator operator;
	
	@ColumnInfo(name="ALERT/EVENT", descr="")
	@Enumerated(EnumType.STRING)
	private EventAlertType eventAlertType;	

	@Column(name = "SEVERITY_TYPE")
	@ColumnInfo(name="SEVERITY LEVEL", descr="")
	@Enumerated(EnumType.STRING)
	private SeverityType severity;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="EVENTALERT_ID")
	@ColumnInfo(name="ALERT/EVENT")
	@ReferencedBy(name="name")
	private EventAlert eventAlert;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="METEREVENT_ID")
	@ColumnInfo(name="METEREVENT")
	@ReferencedBy(name="name")
	private MeterEvent meterEvent;
	
	@Column(name = "STATUS")
	@ColumnInfo(name="STATUS", descr="")
	@Enumerated(EnumType.STRING)
	private EventStatus status;

	@Column(name = "ACTIVATOR_TYPE")
	@ColumnInfo(name="발생대상타입")
	@Enumerated(EnumType.STRING)
	private TargetClass activatorType;
	
	@Column(length=100)
	@ColumnInfo(name="발생Id", descr="발생 대상의 IP")
	private String activatorId;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="LOCATION_ID")
    @ColumnInfo(name="발생지역")
    @ReferencedBy(name="name")
	private Location location;
	
	@Column(nullable=true)
	@ColumnInfo(name="팝업설정")
	private Boolean popup;
	
	@Column(nullable=true)
	@ColumnInfo(name="팝업개수설정")
	private Integer popupCnt;
	
	@Column(nullable=true)
	@ColumnInfo(name="소리설정")
	private Boolean sound;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public EventAlert getEventAlert() {
		return eventAlert;
	}

	
	
	public MeterEvent getMeterEvent() {
		return meterEvent;
	}

	public void setMeterEvent(MeterEvent meterEvent) {
		this.meterEvent = meterEvent;
	}

	public EventAlertType getEventAlertType() {
		return eventAlertType;
	}

	public void setEventAlertType(EventAlertType eventAlertType) {
		this.eventAlertType = eventAlertType;
	}

	public SeverityType getSeverity() {
		return severity;
	}

	public void setSeverity(SeverityType severity) {
		this.severity = severity;
	}

	public EventStatus getStatus() {
		return status;
	}

	public void setStatus(EventStatus status) {
		this.status = status;
	}

	public void setEventAlert(EventAlert eventAlert) {
		this.eventAlert = eventAlert;
	}

	public TargetClass getActivatorType() {
		return activatorType;
	}

	public void setActivatorType(String activatorType) {
		this.activatorType = TargetClass.valueOf(activatorType);
	}

	public String getActivatorId() {
		return activatorId;
	}

	public void setActivatorId(String activatorId) {
		this.activatorId = activatorId;
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Boolean getPopup() {
		return popup;
	}

	public void setPopup(Boolean popup) {
		this.popup = popup;
	}

	public Integer getPopupCnt() {
		return popupCnt;
	}

	public void setPopupCnt(Integer popupCnt) {
		this.popupCnt = popupCnt;
	}

	public Boolean getSound() {
		return sound;
	}

	public void setSound(Boolean sound) {
		this.sound = sound;
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	public String toJSONString() {
		JSONStringer js = null;
		
		js = new JSONStringer();
		js.object().key("id").value(this.id)
				   .key("type").value((this.eventAlertType == null)? "":this.getEventAlertType())
				   .key("severity").value((this.severity == null)? "":this.getSeverity())
				   .key("eventAlertClass").value((this.eventAlert == null)? "":this.eventAlert.getName())
				   .key("meterEventClass").value((this.meterEvent == null)? "":this.meterEvent.getName())
				   .key("status").value((this.status == null)? "":this.getStatus())
				   .key("activatorType").value((this.activatorType == null)? "":this.activatorType.name())
				   .key("activatorId").value((this.activatorId == null)? "":this.activatorId)
				   .key("location").value((this.location == null)? "":this.location.getName())
				   .key("popup").value((this.popup == null)? "":this.getPopup())
				   .key("popupCnt").value((this.popupCnt == null)? "":this.getPopupCnt())
				   .key("sound").value((this.sound == null)? "":this.getSound())
				   .endObject();

		return js.toString();
	}
	
	@Override
    public String getInstanceName() {
        return this.getOperator().getInstanceName();
    }
}
