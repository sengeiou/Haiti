package com.aimir.model.system;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * Message Log Data Bean Class
 * for ActiveMQ jms message
 * 
 * @author       박종성
 * @version 1.0
 */
@Entity
@Table(name="")
public class FireAlarmMessageLog extends BaseObject implements JSONString {
    private static final long serialVersionUID = 7835939935487701977L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="FIREALARMMESSAGELOG_SEQ")
    @SequenceGenerator(name="FIREALARMMESSAGELOG_SEQ", sequenceName="FIREALARMMESSAGELOG_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
    private Long id;
    
    @ColumnInfo(name="생성일")
    private String writeDate;
    @ColumnInfo(name="알람여부")
    private boolean alarm;
    @ColumnInfo(name="JMS메시지아이디")
    private String messageId;
    @ColumnInfo(name="JMS응답메시지아이디")
    private String correlationId;
    @ColumnInfo(name="메시지내용")
    private String message;
    @ColumnInfo(name="소스")
    private String source;
    @ColumnInfo(name="대상")
    private String target;
    @ColumnInfo(name="발생시각")
    private String timestamp;
    @ColumnInfo(name="온도")
    private String temperature;
    @ColumnInfo(name="배터리전압수준")
    private String batteryLevel;
    @ColumnInfo(name="알람유형")
    private String alarmType;
    @ColumnInfo(name="이벤트유형")
    private String eventType;
    @ColumnInfo(name="장비유형")
    private String unitType;
    @ColumnInfo(name="상태")
    private String status;
    @ColumnInfo(name="결과")
    private String result;
    @ColumnInfo(name="사유")
    private String reason;
    @ColumnInfo(name="전송영부")
    private boolean sended;
    @ColumnInfo(name="전송일시")
    private String sendDate;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(String batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public boolean isSended() {
        return sended;
    }

    public void setSended(boolean sended) {
        this.sended = sended;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        StringBuffer info = new StringBuffer();
        
        info
        .append(" id : ").append(id).append(" <BR> ")
        .append(" writeDate : ").append(writeDate).append(" <BR> ")
        .append(" alarm : ").append(alarm).append(" <BR> ")
        .append(" messageId : ").append(messageId).append(" <BR> ")
        .append(" correlationId : ").append(correlationId).append(" <BR> ")
        .append(" message : ").append(message).append(" <BR> ")
        .append(" source : ").append(source).append(" <BR> ")
        .append(" target : ").append(target).append(" <BR> ")
        .append(" timestamp : ").append(timestamp).append(" <BR> ")
        .append(" temperature : ").append(temperature).append(" <BR> ")
        .append(" batteryLevel : ").append(batteryLevel).append(" <BR> ")
        .append(" alarmType : ").append(alarmType).append(" <BR> ")
        .append(" eventType : ").append(eventType).append(" <BR> ")
        .append(" unitType : ").append(unitType).append(" <BR> ")
        .append(" status : ").append(status).append(" <BR> ")
        .append(" result : ").append(result).append(" <BR> ")
        .append(" reason : ").append(reason).append(" <BR> ")
        .append(" sendDate : ").append(sendDate).append(" <BR> ")
        .append(" sended : ").append(sended).append(" <BR> ");
        
        return info.toString();
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


    public String toJSONString() {
        StringBuffer buf = new StringBuffer();
        buf.append("{" 
                + "id:'" + id
                + "', writeDate:'" + writeDate
                + "', alarm:'" + alarm
                + "', messageId:'" + messageId
                + "', correlationId:'" + correlationId
                + "', message:'" + message
                + "', source:'" + source
                + "', target:'" + target
                + "', timestamp:'" + timestamp
                + "', temperature:'" + temperature
                + "', batteryLevel:'" + batteryLevel
                + "', alarmType:'" + alarmType
                + "', eventType:'" + eventType
                + "', unitType:'" + unitType
                + "', status:'" + status
                + "', result:'" + result
                + "', reason:'" + reason
                + "', sendDate:'" + sendDate
                + "', sended:'" + sended + "'}");
        return buf.toString();
    }
}    