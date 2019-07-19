package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

@Entity
public class OnDemandReadingOrder extends BaseObject implements JSONString,
		Cloneable {

	private static final long serialVersionUID = 2489524733358761669L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ONDEMANDREADINGORDER_SEQ")
	@SequenceGenerator(name = "ONDEMANDREADINGORDER_SEQ", sequenceName = "ONDEMANDREADINGORDER_SEQ", allocationSize = 1)
	@ColumnInfo(name = "PK", descr = "PK")
	private Integer id;

	@Column
	@ColumnInfo(name = "웹서비스 요청지")
	private String userName;

	@Column
	@ColumnInfo(name = "Order 관리번호")
	private Long referenceId;

	@Column
	@ColumnInfo(name = "대상 Meter Type")
	private Integer meterType;

	@Column
	@ColumnInfo(name = "대상 Meter seiral number")
	private String meterSerialNumber;

	@Column
	@ColumnInfo(name = "요청 날짜시간")
	private String meterValueDate;

	@Transient
	private String meterValueDateFrom;

	@Transient
	private String meterValueDateTo;

	/**
	 * 일반적인 전기/가스/수도 검침값 열량계의 에너지값
	 */
	@Column
	@ColumnInfo(name = "검침 값 에너지")
	private Double meterReadingEnergy;

	/**
	 * 일반적으로 사용하지 않으나 열량계 미터인 경우 볼륨값을 가진다. 가스나 수도인경우 meterReadingEnergy를 사용안하고
	 * meterReadingVolume을 사용해도 괜찮을 것 같다.
	 */
	@Column
	@ColumnInfo(name = "검침 값 볼륨")
	private Double meterReadingVolume;

	/**
	 * @see com.aimir.fep.command.ws.datatype.OrderStatus
	 */
	@Column
	@ColumnInfo(name = "오더 상태")
	private Integer orderStatus;

	/**
	 * @see com.aimir.fep.command.ws.datatype.FaultCode
	 */
	@Column
	@ColumnInfo(name = "Application fault code")
	private Integer applicationFault;

	@Column
	@ColumnInfo(name = "Callback 전달 여부")
	private boolean isSend;

	@Column
	@ColumnInfo(name = "Callback 전달 날짜시간")
	private String handleDate;

	@Column
	@ColumnInfo(name = "Callback 에러 메세지")
	private String failMessage;

	@Column
	@ColumnInfo(name = "웹서비스 콜백 테스트 확인용")
	private Integer testResult;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Long referenceId) {
		this.referenceId = referenceId;
	}

	public Integer getMeterType() {
		return meterType;
	}

	public void setMeterType(Integer meterType) {
		this.meterType = meterType;
	}

	public String getMeterSerialNumber() {
		return meterSerialNumber;
	}

	public void setMeterSerialNumber(String meterSerialNumber) {
		this.meterSerialNumber = meterSerialNumber;
	}

	public String getMeterValueDate() {
		return meterValueDate;
	}

	public void setMeterValueDate(String meterValueDate) {
		this.meterValueDate = meterValueDate;
	}

	public String getMeterValueDateFrom() {
		return meterValueDateFrom;
	}

	public void setMeterValueDateFrom(String meterValueDateFrom) {
		this.meterValueDateFrom = meterValueDateFrom;
	}

	public String getMeterValueDateTo() {
		return meterValueDateTo;
	}

	public void setMeterValueDateTo(String meterValueDateTo) {
		this.meterValueDateTo = meterValueDateTo;
	}

	public Double getMeterReadingEnergy() {
		return meterReadingEnergy;
	}

	public void setMeterReadingEnergy(Double meterReadingEnergy) {
		this.meterReadingEnergy = meterReadingEnergy;
	}

	public Double getMeterReadingVolume() {
		return meterReadingVolume;
	}

	public void setMeterReadingVolume(Double meterReadingVolume) {
		this.meterReadingVolume = meterReadingVolume;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getApplicationFault() {
		return applicationFault;
	}

	public void setApplicationFault(Integer applicationFault) {
		this.applicationFault = applicationFault;
	}

	public boolean isSend() {
		return isSend;
	}

	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}

	public int isSendInt() {
		if (isSend) {
			return 1;
		} else {
			return 0;
		}
	}

	public String getHandleDate() {
		return handleDate;
	}

	public void setHandleDate(String handleDate) {
		this.handleDate = handleDate;
	}

	public String getFailMessage() {
		return failMessage;
	}

	public void setFailMessage(String failMessage) {
		this.failMessage = failMessage;
	}

	public Integer getTestResult() {
		return testResult;
	}

	public void setTestResult(Integer testResult) {
		this.testResult = testResult;
	}

	@Override
	public String toJSONString() {
		return "{ 'id'=" + id + ", 'userName'='" + userName
				+ "', 'referenceId'=" + referenceId + ", 'meterType'="
				+ meterType + ", 'meterSerialNumber'='" + meterSerialNumber
				+ "', 'meterValueDate'='" + meterValueDate
				+ "', 'meterValueDateFrom'='" + meterValueDateFrom
				+ "', 'meterValueDateTo'='" + meterValueDateTo
				+ "', 'meterReadingEnergy'=" + meterReadingEnergy
				+ ", 'meterReadingVolume'=" + meterReadingVolume
				+ ", 'orderStatus'=" + orderStatus + ", 'applicationFault'="
				+ applicationFault + ", 'isSend'=" + isSend + ", 'handleDate'="
				+ handleDate + ", 'failMessage'='" + failMessage
				+ "', 'testResult'=" + testResult + "}";
	}

	@Override
	public String toString() {
		return this.getClass().getName() + " " + toJSONString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OnDemandReadingOrder other = (OnDemandReadingOrder) obj;
		if (applicationFault == null) {
			if (other.applicationFault != null)
				return false;
		} else if (!applicationFault.equals(other.applicationFault))
			return false;
		if (failMessage == null) {
			if (other.failMessage != null)
				return false;
		} else if (!failMessage.equals(other.failMessage))
			return false;
		if (handleDate == null) {
			if (other.handleDate != null)
				return false;
		} else if (!handleDate.equals(other.handleDate))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (isSend != other.isSend)
			return false;
		if (meterReadingEnergy == null) {
			if (other.meterReadingEnergy != null)
				return false;
		} else if (!meterReadingEnergy.equals(other.meterReadingEnergy))
			return false;
		if (meterReadingVolume == null) {
			if (other.meterReadingVolume != null)
				return false;
		} else if (!meterReadingVolume.equals(other.meterReadingVolume))
			return false;
		if (meterSerialNumber == null) {
			if (other.meterSerialNumber != null)
				return false;
		} else if (!meterSerialNumber.equals(other.meterSerialNumber))
			return false;
		if (meterType == null) {
			if (other.meterType != null)
				return false;
		} else if (!meterType.equals(other.meterType))
			return false;
		if (meterValueDate == null) {
			if (other.meterValueDate != null)
				return false;
		} else if (!meterValueDate.equals(other.meterValueDate))
			return false;
		if (meterValueDateTo == null) {
			if (other.meterValueDateTo != null)
				return false;
		} else if (!meterValueDateTo.equals(other.meterValueDateTo))
			return false;
		if (meterValueDateFrom == null) {
			if (other.meterValueDateFrom != null)
				return false;
		} else if (!meterValueDateFrom.equals(other.meterValueDateFrom))
			return false;
		if (orderStatus == null) {
			if (other.orderStatus != null)
				return false;
		} else if (!orderStatus.equals(other.orderStatus))
			return false;
		if (referenceId == null) {
			if (other.referenceId != null)
				return false;
		} else if (!referenceId.equals(other.referenceId))
			return false;
		if (testResult == null) {
			if (other.testResult != null)
				return false;
		} else if (!testResult.equals(other.testResult))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((applicationFault == null) ? 0 : applicationFault.hashCode());
		result = prime * result
				+ ((failMessage == null) ? 0 : failMessage.hashCode());
		result = prime * result
				+ ((handleDate == null) ? 0 : handleDate.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (isSend ? 1231 : 1237);
		result = prime
				* result
				+ ((meterReadingEnergy == null) ? 0 : meterReadingEnergy
						.hashCode());
		result = prime
				* result
				+ ((meterReadingVolume == null) ? 0 : meterReadingVolume
						.hashCode());
		result = prime
				* result
				+ ((meterSerialNumber == null) ? 0 : meterSerialNumber
						.hashCode());
		result = prime * result
				+ ((meterType == null) ? 0 : meterType.hashCode());
		result = prime * result
				+ ((meterValueDate == null) ? 0 : meterValueDate.hashCode());
		result = prime
				* result
				+ ((meterValueDateTo == null) ? 0 : meterValueDateTo
						.hashCode());
		result = prime
				* result
				+ ((meterValueDateFrom == null) ? 0 : meterValueDateFrom
						.hashCode());
		result = prime * result
				+ ((orderStatus == null) ? 0 : orderStatus.hashCode());
		result = prime * result
				+ ((referenceId == null) ? 0 : referenceId.hashCode());
		result = prime * result
				+ ((testResult == null) ? 0 : testResult.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
