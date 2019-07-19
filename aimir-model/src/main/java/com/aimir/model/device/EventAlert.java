package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.EventAlertType;
import com.aimir.constants.CommonConstants.MonitorType;
import com.aimir.constants.CommonConstants.SeverityType;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>EventAlert - Device혹은 system에서 발생한 이벤트에 대한 정보를 정의한 클래스, 이벤트 아이디, 이벤트 타입, 이벤트 우선순위 등의 정보가 정의</p>
 * <pre>
 * 이벤트와 장애를 하나의 클래스로 관리하고 유형으로 구분한다. 
 * 이벤트장애의 대상은 모든 장비가 될 수 있다. 
 * </pre>
 * @author 최은정(ej8486)
 *
 */
@Entity
@Table(name="EVENTALERT")
// @Cache(type=CacheType.SOFT)
public class EventAlert extends BaseObject implements JSONString {

	private static final long serialVersionUID = 13150985498127118L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,  generator="EVENTALERT_SEQ")
	@SequenceGenerator(name="EVENTALERT_SEQ", sequenceName="EVENTALERT_SEQ", allocationSize=1) 
	private Integer id;	//	ID(PK)

	@Column(name = "EVENTALERT_TYPE", nullable=false)
	@ColumnInfo(name="ALERT/EVENT", descr="")
	@Enumerated(EnumType.STRING)
	private EventAlertType eventAlertType;	

	@Column(name = "SEVERITY_TYPE", nullable=false)
	@ColumnInfo(name="SEVERITY LEVEL", descr="")
	@Enumerated(EnumType.STRING)
	private SeverityType severity;
	
	@Column(unique=true, nullable=false)
	@ColumnInfo(name="이름")
	private String name;
	
	//@Column(name="eventclassname")
	//@ColumnInfo(name="이벤트 클래스 명")
	//private String eventClassName;
	
	//@Column(name="faultclassname")
	//@ColumnInfo(name="fault class name")
	//private String faultClassName;
	
	@ColumnInfo(name="설명")
	private String descr;
	
	@Column(nullable=true)
	@ColumnInfo(name="메세지패턴")
	private String msgPattern;
	
	@Column(nullable=true)
	@ColumnInfo(name="대처방안")
	private String troubleAdvice;
	
	@Column(nullable=true) //system에서 처리하는 이벤트의 경우 oid가 정의되지 않았으므로 null을 허용한다.
	@ColumnInfo(name="이벤트코드")
	private String oid;
	
	@Column(name = "MONITOR_TYPE", nullable=false)
	@ColumnInfo(name="모니터링")
	@Enumerated(EnumType.STRING)
	private MonitorType monitor;
	
	@ColumnInfo(name="자동종료")
	private Integer autoClosed;
	
	private Integer timeout;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public EventAlertType getEventAlertType() {
		return eventAlertType;
	}

	public void setEventAlertType(String eventAlertType) {
		this.eventAlertType = EventAlertType.valueOf(eventAlertType);
	}

	public SeverityType getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = SeverityType.valueOf(severity);
	}

	public String getName() {
		return name;
	}	
	
	//public String getEventClassName() {
	//	return eventClassName;
	//}

	//public void setEventClassName(String eventClassName) {
	//	this.eventClassName = eventClassName;
	//}

	//public String getFaultClassName() {
	//	return faultClassName;
	//}

	//public void setFaultClassName(String faultClassName) {
	//	this.faultClassName = faultClassName;
	//}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getMsgPattern() {
		return msgPattern;
	}

	public void setMsgPattern(String msgPattern) {
		this.msgPattern = msgPattern;
	}

	public String getTroubleAdvice() {
		return troubleAdvice;
	}

	public void setTroubleAdvice(String troubleAdvice) {
		this.troubleAdvice = troubleAdvice;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public MonitorType getMonitor() {
		return monitor;
	}

	public void setMonitor(String monitor) {
		this.monitor = MonitorType.valueOf(monitor);
	}

	public Integer getAutoClosed() {
		return autoClosed;
	}

	public void setAutoClosed(Integer autoClosed) {
		this.autoClosed = autoClosed;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}	

	public String toJSONString() {
		JSONStringer js = null;
		
		js = new JSONStringer();
		js.object().key("id").value(this.id)
				   .key("eventAlertType").value((this.eventAlertType == null)? "null":this.eventAlertType.name())
				   .key("severity").value((this.severity == null)? "null":this.severity.name())
				   .key("name").value((this.name == null)? "null":this.name)
				   .key("descr").value((this.descr == null)? "null":this.descr)
				   .key("msgPattern").value((this.msgPattern == null)? "null":this.msgPattern)
				   .key("troubleAdvice").value((this.troubleAdvice == null)? "null":this.troubleAdvice)
				   .key("oid").value((this.oid == null)? "null": this.oid)
				   .key("monitor").value((this.monitor == null)? "null":this.monitor.name())
				   .key("autoClosed").value(this.autoClosed)
				   .key("timeout").value((this.timeout == null)? "null":this.timeout)
				   .endObject();

		return js.toString();
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EventAlert [autoClosed=" + autoClosed + ", descr=" + descr
				+ ", eventAlertType=" + eventAlertType + ", id=" + id
				+ ", monitor=" + monitor + ", msgPattern=" + msgPattern
				+ ", name=" + name + ", oid=" + oid + ", severity=" + severity
				+ ", timeout=" + timeout + ", troubleAdvice=" + troubleAdvice
				+ "]";
	}

	
}