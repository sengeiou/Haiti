package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;
/**
 * SMS 전송 로그
 * @author 김재식(kaze)
 *
 */

@Entity
@DiscriminatorValue("SmsService")
public class SmsServiceLog extends BaseObject{

	private static final long serialVersionUID = 8887277736300230238L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SMSSERVICELOG_SEQ")
	@SequenceGenerator(name="SMSSERVICELOG_SEQ", sequenceName="SMSSERVICELOG_SEQ", allocationSize=1)
	private Long id;

	@Column(name="SENDTIME")
	@ColumnInfo(descr="sms 전송 시간(yyyymmddhh 형식)")
	private String sendTime;

	@Column(name="SEND_NO",length=30)
	@ColumnInfo(descr="보낸 사람 전화 번호")
	private String sendNo ;

	@Column(name="RECEIVE_NO",length=30)
	@ColumnInfo(descr="받는 사람 전화 번호")
	private String receiveNo ;

	@Column(name="MSG",length=200)
	@ColumnInfo(descr="전송 문자 메세지")
	private String msg ;

	@Column(name="RESULT",length=2000)
	@ColumnInfo(descr="sms 전송 결과")
	private String result ;
	
	@Column(name="EUI_ID",length=20)
	@ColumnInfo(descr="EUI ID")
	private String euiId ;
	
	@Column(name="MSG_ID",length=10)
	@ColumnInfo(descr="MESSAGE ID")
	private String msgId ;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the sendTime
	 */
	public String getSendTime() {
		return sendTime;
	}

	/**
	 * @param sendTime the sendTime to set
	 */
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	/**
	 * @return the sendNo
	 */
	public String getSendNo() {
		return sendNo;
	}

	/**
	 * @param sendNo the sendNo to set
	 */
	public void setSendNo(String sendNo) {
		this.sendNo = sendNo;
	}

	/**
	 * @return the receiveNo
	 */
	public String getReceiveNo() {
		return receiveNo;
	}

	/**
	 * @param receiveNo the receiveNo to set
	 */
	public void setReceiveNo(String receiveNo) {
		this.receiveNo = receiveNo;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	public String getEuiId() {
		return euiId;
	}

	public void setEuiId(String euiId) {
		this.euiId = euiId;
	}
	
	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.euiId = msgId;
	}

	/* (non-Javadoc)
	 * @see com.aimir.model.BaseObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.aimir.model.BaseObject#hashCode()
	 */
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.aimir.model.BaseObject#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
