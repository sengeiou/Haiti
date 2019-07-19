package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;


/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * 수요반응(Demand Response) 가 DRAS로 부터 왔을때 AIMIR System에서 Demand Response Event(signal)을 수신받아서
 * 이벤트 내용을 이 클래스에 저장한다.
 * 이벤트 상태 변경에 대한 이벤트 상태 정보, DR Client 아이디, 이벤트 정보, Demand Response 프로그램명, Demand Response 참여 상태 등의 정보를 가진다.
 * 이벤트에 대한 저장은 Demand Response Client 모듈(AIMIR-SERVICE-SYSTEM) Package의 DR 관련 서비스에서 담당한다.
 *
 * 
 * @author 은미애(eunmiae)
 *
 */
@Entity
@Table(name = "DEMAND_RESPONSE_EVENT_LOG")
public class DemandResponseEventLog extends BaseObject {

	static final long serialVersionUID = -3900010223809724820L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DR_EVENT_LOG_SEQ")
	@SequenceGenerator(name = "DR_EVENT_LOG_SEQ", sequenceName = "DR_EVENT_LOG_SEQ", allocationSize = 1)
    private Integer id;

	@Column(name = "EVENT_STATE_ID")
    @ColumnInfo(name="Event State ID ")	
	private String eventStateId;

	@Column(name = "DRAS_CLIENT_ID")
    @ColumnInfo(name="DRAS Client ID ", descr="DRMS에서 이벤트를 생성한 유저 아이디")	
	private String drasClientId;

	@Column(name = "EVENT_IDENTIFIER", unique=false)
    @ColumnInfo(name="Event ID ", descr="이벤트 아이디")	
	private String eventIdentifier;
	
	@Column(name = "EVENT_MOD_NUMBER")
	private String eventModNumber;

	@Column(name = "PROGRAM_NAME")
	private String programName;
	
	@Column(name = "NOTIFICATION_TIME")
	private String notificationTime;
	
	@Column(name = "START_TIME")
	private String startTime;
	
	@Column(name = "END_TIME")
	private String endTime;

	@Column(name = "OPERATION_MODE_VALUE")
	private String operationModeValue;

	@Column(name = "EVENT_INFO_TYPE_ID")
	private String eventInfoTypeID;

	@Column(name = "EVENT_INFO_NAME")
	private String eventInfoName;
	
	@Column(name = "EVENT_INFO_VALUES")
	private String eventInfoValues;

	@Column(name = "OPTOUT_STATUS",columnDefinition= "INTEGER default 1")
    @ColumnInfo(name="DR 참여 상태", descr="Initialization(1), Ongoing(2), Participated(3), Rejected(4), Completed(5)")	
	private Integer optOutStatus;

	private String yyyymmdd;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="supplier_id")
	@ColumnInfo(name="공급사")
	@ReferencedBy(name="name")
	private Supplier supplier;

	@Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
	private Integer supplierId;
	
    @ColumnInfo(name="DR 참여 상태", descr="FAR, NEAR, ACTIVE")	
	private String eventStatus;
 
	public String getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(String eventStatus) {
		this.eventStatus = eventStatus;
	}

	public String getYyyymmdd() {
		return yyyymmdd;
	}

	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	public String getEventStateId() {
		return eventStateId;
	}


	public void setEventStateId(String eventStateId) {
		this.eventStateId = eventStateId;
	}


	public String getDrasClientId() {
		return drasClientId;
	}


	public void setDrasClientId(String drasClientId) {
		this.drasClientId = drasClientId;
	}


	public String getEventIdentifier() {
		return eventIdentifier;
	}


	public void setEventIdentifier(String eventIdentifier) {
		this.eventIdentifier = eventIdentifier;
	}


	public String getEventModNumber() {
		return eventModNumber;
	}


	public void setEventModNumber(String eventModNumber) {
		this.eventModNumber = eventModNumber;
	}


	public String getProgramName() {
		return programName;
	}


	public void setProgramName(String programName) {
		this.programName = programName;
	}


	public String getNotificationTime() {
		return notificationTime;
	}


	public void setNotificationTime(String notificationTime) {
		this.notificationTime = notificationTime;
	}


	public String getStartTime() {
		return startTime;
	}


	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public String getEndTime() {
		return endTime;
	}


	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}


	public String getOperationModeValue() {
		return operationModeValue;
	}


	public void setOperationModeValue(String operationModeValue) {
		this.operationModeValue = operationModeValue;
	}


	public String getEventInfoTypeID() {
		return eventInfoTypeID;
	}


	public void setEventInfoTypeID(String eventInfoTypeID) {
		this.eventInfoTypeID = eventInfoTypeID;
	}


	public String getEventInfoName() {
		return eventInfoName;
	}


	public void setEventInfoName(String eventInfoName) {
		this.eventInfoName = eventInfoName;
	}


	public String getEventInfoValues() {
		return eventInfoValues;
	}


	public void setEventInfoValues(String eventInfoValues) {
		this.eventInfoValues = eventInfoValues;
	}


	public Integer getOptOutStatus() {
		return optOutStatus;
	}


	public void setOptOutStatus(Integer optOutStatus) {
		this.optOutStatus = optOutStatus;
	}
	

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}
	
	public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
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

}
