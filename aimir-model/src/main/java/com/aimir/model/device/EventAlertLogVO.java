package com.aimir.model.device;


/**
 * EventAlertLog를 flex에 전달하는 객체
 * @author 최은정(ej8486)
 *
 */
public class EventAlertLogVO {
	/**
	 * 
	 */
	private String rank;				// 순번
	private String type;				// 이벤트 타입 (event/alert)
	private String severity;			// 이벤트 위험순위
	private String message;				// 이벤트 메세지
	private String location;			// 이벤트 발생 지역
	private String activatorId;			// 발생 장비 id
	private String activatorType;		// 발생 장비 타입
	private String activatorIp;			// 발생 장비 ip
	private String status;				// 이벤트 상태
	private String writeTime;			// 서버 저장 시간
	private String openTime;			// 이벤트 발생 시각
	private String closeTime;			// 이벤트 종료 시각
	private String duration;			// 이벤트 지속 시간
	private String idx; // index

	//EventAlert.id
	private String eventAlertId;

	//EventAlert.name
	private String eventAlertName;

    //EventAlertLog.id
    private String eventLogId;

    public String getEventAlertName() {
        return eventAlertName;
    }

    public void setEventAlertName(String eventAlertName) {
        this.eventAlertName = eventAlertName;
    }

    public String getEventAlertId() {
        return eventAlertId;
    }

    public void setEventAlertId(String eventAlertId) {
        this.eventAlertId = eventAlertId;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getActivatorId() {
        return activatorId;
    }

    public void setActivatorId(String activatorId) {
        this.activatorId = activatorId;
    }

    public String getActivatorType() {
        return activatorType;
    }

    public void setActivatorType(String activatorType) {
        this.activatorType = activatorType;
    }

    public String getActivatorIp() {
        return activatorIp;
    }

    public void setActivatorIp(String activatorIp) {
        this.activatorIp = activatorIp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(String writeTime) {
        this.writeTime = writeTime;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * @return the eventLogId
     */
    public String getEventLogId() {
        return eventLogId;
    }

    /**
     * @param eventLogId the eventLogId to set
     */
    public void setEventLogId(String eventLogId) {
        this.eventLogId = eventLogId;
    }
	
}