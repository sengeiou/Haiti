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
public class PowerOnOffOrder extends BaseObject implements JSONString,
		Cloneable {

	private static final long serialVersionUID = 768637358134619828L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "POWERONOFFORDER_SEQ")
	@SequenceGenerator(name = "POWERONOFFORDER_SEQ", sequenceName = "POWERONOFFORDER_SEQ", allocationSize = 1)
	@ColumnInfo(name = "PK", descr = "PK")
	private Long id;

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
	@ColumnInfo(name = "Power control 명령")
	private Integer powerOperation;

	@Column
	@ColumnInfo(name = "요청 날짜시간")
	private String powerOperationDate;

	@Transient
	private String powerOperationDateFrom;

	@Transient
	private String powerOperationDateTo;

	
	@Column
	@ColumnInfo(name = "요청자 관련 정보")
	private String userReference;

	@Column
	@ColumnInfo(name = "요청자 날짜시간")
	private String userCreateDate;

	@Transient
	private String userCreateDateFrom;

	@Transient
	private String userCreateDateTo;

	/**
	 * 일반적인 전기 검침값
	 */
	@Column
	@ColumnInfo(name = "검침 값 에너지")
	private Double meterReading;
	
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public Integer getPowerOperation() {
		return powerOperation;
	}

	public void setPowerOperation(Integer powerOperation) {
		this.powerOperation = powerOperation;
	}

	public String getPowerOperationDate() {
		return powerOperationDate;
	}

	public void setPowerOperationDate(String powerOperationDate) {
		this.powerOperationDate = powerOperationDate;
	}

	public String getPowerOperationDateFrom() {
		return powerOperationDateFrom;
	}

	public void setPowerOperationDateFrom(String powerOperationDateFrom) {
		this.powerOperationDateFrom = powerOperationDateFrom;
	}

	public String getPowerOperationDateTo() {
		return powerOperationDateTo;
	}

	public void setPowerOperationDateTo(String powerOperationDateTo) {
		this.powerOperationDateTo = powerOperationDateTo;
	}

	public String getUserReference() {
		return userReference;
	}

	public void setUserReference(String userReference) {
		this.userReference = userReference;
	}

	public String getUserCreateDate() {
		return userCreateDate;
	}

	public void setUserCreateDate(String userCreateDate) {
		this.userCreateDate = userCreateDate;
	}

	public String getUserCreateDateFrom() {
		return userCreateDateFrom;
	}

	public void setUserCreateDateFrom(String userCreateDateFrom) {
		this.userCreateDateFrom = userCreateDateFrom;
	}

	public String getUserCreateDateTo() {
		return userCreateDateTo;
	}

	public void setUserCreateDateTo(String userCreateDateTo) {
		this.userCreateDateTo = userCreateDateTo;
	}

	public Double getMeterReading() {
		return meterReading;
	}

	public void setMeterReading(Double meterReading) {
		this.meterReading = meterReading;
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
		return "{ 'id=" + id + ", 'userName'='" + userName
				+ "', 'referenceId'=" + referenceId + ", 'meterType'=" + meterType
				+ ", 'meterSerialNumber'='" + meterSerialNumber
				+ "', powerOperation=" + powerOperation
				+ ", 'powerOperationDate'='" + powerOperationDate
				+ "', 'powerOperationDateFrom'='" + powerOperationDateFrom
				+ "', 'powerOperationDateTo'='" + powerOperationDateTo
				+ "', 'userReference'='" + userReference + "', 'userCreateDate'='"
				+ userCreateDate + "', 'userCreateDateFrom'='" + userCreateDateFrom
				+ "', 'userCreateDateTo'='" + userCreateDateTo + "', 'meterReading'='"
				+ meterReading + "', 'orderStatus'=" + orderStatus
				+ ", 'applicationFault'=" + applicationFault + ", 'isSend'="
				+ isSend + ", 'handleDate'='" + handleDate + "', 'failMessage'='"
				+ failMessage + "', 'testResult'=" + testResult + "}";
	}

	@Override
	public String toString() {
		return this.getClass().getName() + " " + toJSONString();
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
		result = prime * result
				+ ((meterReading == null) ? 0 : meterReading.hashCode());
		result = prime
				* result
				+ ((meterSerialNumber == null) ? 0 : meterSerialNumber
						.hashCode());
		result = prime * result
				+ ((meterType == null) ? 0 : meterType.hashCode());
		result = prime * result
				+ ((orderStatus == null) ? 0 : orderStatus.hashCode());
		result = prime * result
				+ ((powerOperation == null) ? 0 : powerOperation.hashCode());
		result = prime
				* result
				+ ((powerOperationDate == null) ? 0 : powerOperationDate
						.hashCode());
		result = prime
				* result
				+ ((powerOperationDateFrom == null) ? 0
						: powerOperationDateFrom.hashCode());
		result = prime
				* result
				+ ((powerOperationDateTo == null) ? 0 : powerOperationDateTo
						.hashCode());
		result = prime * result
				+ ((referenceId == null) ? 0 : referenceId.hashCode());
		result = prime * result
				+ ((testResult == null) ? 0 : testResult.hashCode());
		result = prime * result
				+ ((userCreateDate == null) ? 0 : userCreateDate.hashCode());
		result = prime
				* result
				+ ((userCreateDateFrom == null) ? 0 : userCreateDateFrom
						.hashCode());
		result = prime
				* result
				+ ((userCreateDateTo == null) ? 0 : userCreateDateTo.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		result = prime * result
				+ ((userReference == null) ? 0 : userReference.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PowerOnOffOrder other = (PowerOnOffOrder) obj;
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
		if (meterReading == null) {
			if (other.meterReading != null)
				return false;
		} else if (!meterReading.equals(other.meterReading))
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
		if (orderStatus == null) {
			if (other.orderStatus != null)
				return false;
		} else if (!orderStatus.equals(other.orderStatus))
			return false;
		if (powerOperation == null) {
			if (other.powerOperation != null)
				return false;
		} else if (!powerOperation.equals(other.powerOperation))
			return false;
		if (powerOperationDate == null) {
			if (other.powerOperationDate != null)
				return false;
		} else if (!powerOperationDate.equals(other.powerOperationDate))
			return false;
		if (powerOperationDateFrom == null) {
			if (other.powerOperationDateFrom != null)
				return false;
		} else if (!powerOperationDateFrom.equals(other.powerOperationDateFrom))
			return false;
		if (powerOperationDateTo == null) {
			if (other.powerOperationDateTo != null)
				return false;
		} else if (!powerOperationDateTo.equals(other.powerOperationDateTo))
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
		if (userCreateDate == null) {
			if (other.userCreateDate != null)
				return false;
		} else if (!userCreateDate.equals(other.userCreateDate))
			return false;
		if (userCreateDateFrom == null) {
			if (other.userCreateDateFrom != null)
				return false;
		} else if (!userCreateDateFrom.equals(other.userCreateDateFrom))
			return false;
		if (userCreateDateTo == null) {
			if (other.userCreateDateTo != null)
				return false;
		} else if (!userCreateDateTo.equals(other.userCreateDateTo))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		if (userReference == null) {
			if (other.userReference != null)
				return false;
		} else if (!userReference.equals(other.userReference))
			return false;
		return true;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
