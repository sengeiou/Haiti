package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * JSGT MESSAGE LOG
 * @author 양철민(escapes)
 *
 */
@Entity
@Table(name="JSGT_MESSAGE_LOG")
public class JsgtMessageLog extends BaseObject {

	private static final long serialVersionUID = -299021006738263346L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="JSGT_MESSAGE_LOG_SEQ")
    @SequenceGenerator(name="JSGT_MESSAGE_LOG_SEQ", sequenceName="JSGT_MESSAGE_LOG_SEQ", allocationSize=1)
	private Integer id;

//    @Version
//    Integer version;

    @Column(length=14)
	@ColumnInfo(name="요청시간", descr="YYYYMMDDHHMMSS")
	private String requestDateTime;

    @Column(length=14)
	@ColumnInfo(name="응답시간", descr="YYYYMMDDHHMMSS")
	private String responseDateTime;

    @Column(length=255)
	private String processMethod;

    @Column(length=500)
	private String message;

    @Column(length=255)
	private String jmsMessageId;

    @Column(length=255)
	private String jmsCorrelationId;

    @Column(length=1)
	private String sended;

    @Column(length=14)
	@ColumnInfo(name="보낸시간", descr="YYYYMMDDHHMMSS")
	private String sendDateTime;

    @Column(length=20)
	@ColumnInfo(name="누리 미터 아이디", descr="Nuri Meter Id")
	private String nuriMeterId;

    @Column(length=20)
	@ColumnInfo(name="스마트 미터 아이디", descr="Smart Meter Id")
	private String smartMeterId;

    @Column(length=20)
	@ColumnInfo(name="LP Time", descr="LP의 시간(YYYYMMDDHHMM)")
	private String lPTime;

    @Column(length=30)
	@ColumnInfo(name="LP Value", descr="LP 값(LP, 누적 모두)")
	private String lPValue;

    @Column(length=3)
	@ColumnInfo(name="sendSeq", descr="15분 데이터 전송 seq 하루에 96개의 seq가 생김")
	private Integer sendSeq;

    @Column(length=20)
	@ColumnInfo(name="요청 시간", descr="요청 시간 (YYYYMMDDHH)")
	private String requestDateHour;


	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        result = prime * result + ((requestDateTime == null) ? 0 : requestDateTime.hashCode());
        result = prime * result
                + ((responseDateTime == null) ? 0 : responseDateTime.hashCode());
        result = prime
                * result
                + ((processMethod == null) ? 0 : processMethod.hashCode());
        result = prime * result
        		+ ((message == null) ? 0 : message.hashCode());
        result = prime * result
                + ((jmsMessageId == null) ? 0 : jmsMessageId.hashCode());
        result = prime * result + ((jmsCorrelationId == null) ? 0 : jmsCorrelationId.hashCode());
        result = prime * result + ((sended == null) ? 0 : sended.hashCode());
        result = prime * result + ((sendDateTime == null) ? 0 : sendDateTime.hashCode());
       // result = prime * result + ((version == null) ? 0 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        JsgtMessageLog other = (JsgtMessageLog) obj;
        if (requestDateTime == null) {
            if (other.requestDateTime != null)
                return false;
        } else if (!requestDateTime.equals(other.requestDateTime))
            return false;
        if (responseDateTime == null) {
            if (other.responseDateTime != null)
                return false;
        } else if (!responseDateTime.equals(other.responseDateTime))
            return false;
        if (processMethod == null) {
            if (other.processMethod != null)
                return false;
        } else if (!processMethod.equals(other.processMethod))
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (jmsMessageId == null) {
            if (other.jmsMessageId != null)
                return false;
        } else if (!jmsMessageId.equals(other.jmsMessageId))
            return false;
        if (jmsCorrelationId == null) {
            if (other.jmsCorrelationId != null)
                return false;
        } else if (!jmsCorrelationId.equals(other.jmsCorrelationId))
            return false;
        if (sended == null) {
            if (other.sended != null)
                return false;
        } else if (!sended.equals(other.sended))
            return false;
        if (sendDateTime == null) {
            if (other.sendDateTime != null)
                return false;
        } else if (!sendDateTime.equals(other.sendDateTime))
            return false;
//        if (version == null) {
//            if (other.version != null)
//                return false;
//        } else if (!version.equals(other.version))
//            return false;
        return true;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JsgtMessageLog [id=" + id + ", requestDateTime=" + requestDateTime + ", responseDateTime="
				+ responseDateTime + ", processMethod=" + processMethod + ", message=" + message + ", jmsMessageId="
				+ jmsMessageId + ", jmsCorrelationId=" + jmsCorrelationId + ", sended=" + sended + ", sendDateTime="
				+ sendDateTime + ", nuriMeterId=" + nuriMeterId + ", smartMeterId=" + smartMeterId + ", lPTime="
				+ lPTime + ", lPValue=" + lPValue + ", sendSeq=" + sendSeq + ", requestDateHour=" + requestDateHour
				+ "]";
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

//	public Integer getVersion() {
//		return version;
//	}
//
//	public void setVersion(Integer version) {
//		this.version = version;
//	}

	public String getRequestDateTime() {
		return requestDateTime;
	}

	public void setRequestDateTime(String requestDateTime) {
		this.requestDateTime = requestDateTime;
	}

	public String getResponseDateTime() {
		return responseDateTime;
	}

	public void setResponseDateTime(String responseDateTime) {
		this.responseDateTime = responseDateTime;
	}

	public String getProcessMethod() {
		return processMethod;
	}

	public void setProcessMethod(String processMethod) {
		this.processMethod = processMethod;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setSendDateTime(String sendDateTime) {
		this.sendDateTime = sendDateTime;
	}

	public String getJmsMessageId() {
		return jmsMessageId;
	}

	public void setJmsMessageId(String jmsMessageId) {
		this.jmsMessageId = jmsMessageId;
	}

	public String getJmsCorrelationId() {
		return jmsCorrelationId;
	}

	public void setJmsCorrelationId(String jmsCorrelationId) {
		this.jmsCorrelationId = jmsCorrelationId;
	}

	public String getSended() {
		return sended;
	}

	public void setSended(String sended) {
		this.sended = sended;
	}

	public String getSendDateTime() {
		return sendDateTime;
	}

	/**
	 * @return the nuriMeterId
	 */
	public String getNuriMeterId() {
		return nuriMeterId;
	}

	/**
	 * @param nuriMeterId the nuriMeterId to set
	 */
	public void setNuriMeterId(String nuriMeterId) {
		this.nuriMeterId = nuriMeterId;
	}

	/**
	 * @return the smartMeterId
	 */
	public String getSmartMeterId() {
		return smartMeterId;
	}

	/**
	 * @param smartMeterId the smartMeterId to set
	 */
	public void setSmartMeterId(String smartMeterId) {
		this.smartMeterId = smartMeterId;
	}

	/**
	 * @return the lPTime
	 */
	public String getlPTime() {
		return lPTime;
	}

	/**
	 * @param lPTime the lPTime to set
	 */
	public void setlPTime(String lPTime) {
		this.lPTime = lPTime;
	}

	/**
	 * @return the lPValue
	 */
	public String getlPValue() {
		return lPValue;
	}

	/**
	 * @param lPValue the lPValue to set
	 */
	public void setlPValue(String lPValue) {
		this.lPValue = lPValue;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * @return the sendSeq
	 */
	public Integer getSendSeq() {
		return sendSeq;
	}

	/**
	 * @param sendSeq the sendSeq to set
	 */
	public void setSendSeq(Integer sendSeq) {
		this.sendSeq = sendSeq;
	}

	/**
	 * @return the requestDateHour
	 */
	public String getRequestDateHour() {
		return requestDateHour;
	}

	/**
	 * @param requestDateHour the requestDateHour to set
	 */
	public void setRequestDateHour(String requestDateHour) {
		this.requestDateHour = requestDateHour;
	}
}