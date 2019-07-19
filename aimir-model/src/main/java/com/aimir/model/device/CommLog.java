package com.aimir.model.device;

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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Code;
import com.aimir.model.system.Location;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>CommLog 통신 이력 - 데이터 통신 서버와 Device(DCU or Modem) 간의 송수신 바이트 정보 및 대상에 대한 정보</p>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="COMMLOG")
@Indexes({
    @Index(name="IDX_COMMLOG_01", columnNames={"SUPPLIERED_ID", "SVC_TYPE_CODE", "START_DATE", "SENDER_ID", "LOCATION_ID"})
})
public class CommLog extends BaseObject {

	private static final long serialVersionUID = -1382111074919152428L;
	
	//추가 프로퍼티 for extjs ui display .   
	private String receiver;
	private String sender;
	private String result;
	private String strSendBytes = "";
	private String strReceiverBytes= "";
	private String time;
	private String strTotalCommTime;
	private String idx1;

	public String getIdx1()
	{
		return idx1;
	}

	public void setIdx1(String idx1)
	{
		this.idx1 = idx1;
	}

	public String getStrTotalCommTime()
	{
		return strTotalCommTime;
	}

	public void setStrTotalCommTime(String strTotalCommTime)
	{
		this.strTotalCommTime = strTotalCommTime;
	}

	public String getTime()
	{
		return time;
	}

	public void setTime(String time)
	{
		this.time = time;
	}

	public String getStrReceiverBytes()
	{
		return strReceiverBytes;
	}

	public void setStrReceiverBytes(String strReceiverBytes)
	{
		this.strReceiverBytes = strReceiverBytes;
	}

	public String getStrSendBytes()
	{
		return strSendBytes;
	}

	public void setStrSendBytes(String strSendBytes)
	{
		this.strSendBytes = strSendBytes;
	}

	public String getResult()
	{
		return result;
	}

	public void setResult(String result)
	{
		this.result = result;
	}

	public String getSender()
	{
		return sender;
	}

	public void setSender(String sender)
	{
		this.sender = sender;
	}

	public static long getSerialversionuid()
	{
		return serialVersionUID;
	}

	public String getReceiver()
	{
		return receiver;
	}

	public void setReceiver(String receiver)
	{
		this.receiver = receiver;
	}

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="COMM_LOG_SEQ")
	@SequenceGenerator(name="COMM_LOG_SEQ", sequenceName="COMM_LOG_SEQ", allocationSize=1) 
	private Long id;
    
    @ColumnInfo(name="공급사 아이디")
    @Column(name="SUPPLIERED_ID")
	private String suppliedId;

    @ColumnInfo(name="시작일", descr="데이터를 수신했을 때의 서버 시간(세션이 오픈되었을 당시의 날짜)(YYYYMMDD)")
    @Column(name="START_DATE", length=8)
    private String startDate;

    @ColumnInfo(name="시작시간", descr="데이터를 수신했을 때의 서버 시간(세션이 오픈되었을 당시의 시간)(HHMMSS)")
    @Column(name="START_TIME", length=6)
    private String startTime;

    @ColumnInfo(name="", descr="세션이 끊어졌을 당시의 서버 시간(yyyymmddhhmmss)")
    @Column(name="END_TIME",length=14)
    private String endTime;

    @ColumnInfo(name="", descr="YYYYMMDDHHMMSS")
    @Column(name="START_DATE_TIME",length=14)
    private String startDateTime;

    @ColumnInfo(name="인터페이스 타입 아이디", descr="코드 테이블의 ID나  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="INTERFACE_CODE")
    @ReferencedBy(name="code")
    private Code interfaceCode;
    
    @Column(name="INTERFACE_CODE", nullable=true, updatable=false, insertable=false)
    private Integer interfaceCodeId;

    @ColumnInfo(name="프로토콜타입 아이디", descr="코드 테이블의 ID나  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="PROTOCOL_CODE")
    @ReferencedBy(name="code")
    private Code protocolCode;
    
    @Column(name="PROTOCOL_CODE", nullable=true, updatable=false, insertable=false)
    private Integer protocolCodeId;
    
    @ColumnInfo(name="발신자 타입 아이디", descr="코드 테이블의 ID나  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SENDER_TYPE_CODE")
    @ReferencedBy(name="code")
    private Code senderTypeCode;
    
    @Column(name="SENDER_TYPE_CODE", nullable=true, updatable=false, insertable=false)
    private Integer senderTypeCodeId;

    @ColumnInfo(name="", descr="sender의 ID(MCU ID, Sensor EUI64, FEP IP등의 주소)")
    @Column(name="SENDER_ID")
    private String senderId;

    @ColumnInfo(name="", descr="Sender의 IP 주소")
    @Column(name="SENDER_IP")
    private String senderIp;

    @ColumnInfo(name="", descr="Sender가 데이터를 내린 Port 번호")
    @Column(name="SENDER_PORT")
    private String senderPort;

    @ColumnInfo(name="지역아이디", descr="지역 테이블의 ID나  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SENDER_LOCATION")
    @ReferencedBy(name="name")
    private Location senderLocation;
    
    @Column(name="SENDER_LOCATION", nullable=true, updatable=false, insertable=false)
    private Integer senderLocationId;

    @ColumnInfo(name="수신자 타입 아이디", descr="코드 테이블의 ID나  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="RECEIVER_TYPE_CODE")
    @ReferencedBy(name="code")
    private Code receiverTypeCode;
    
    @Column(name="RECEIVER_TYPE_CODE", nullable=true, updatable=false, insertable=false)
    private Integer receiverTypeCodeId;

    @ColumnInfo(name="", descr="receiver의 ID")
    @Column(name="RECEIVER_ID")
    private String receiverId;

    @ColumnInfo(name="", descr="receiver의 IP 주소")
    @Column(name="RECEIVER_IP")
    private String receiverIp;

    @ColumnInfo(name="", descr="receiver가 데이터를 받는 Port 번호")
    @Column(name="RECEIVER_PORT")
    private String receiverPort;

    @ColumnInfo(name="", descr="if 4-5 스펙 문서에 table 4에 기술된 서비스 필드값 - 서버에서 내려가는 명령어의 경우 command의 mib값을 이용해 cmdxxx 타입의 값이 들어감")
    @Column(name="OPERATION_CODE")
    private String operationCode;

    @ColumnInfo(name="", descr="endtime-starttime")
    @Column(name="TOTAL_COMM_TIME")
    private Integer totalCommTime;

    @ColumnInfo(name="", descr="데이터의 압축 여부")
    @Column(name="IS_COMPRESSED")
    private Integer isCompressed;

    @ColumnInfo(name="", descr="")
    @Column(name="UNCOM_PRESSED_SEND_BYTES")
    private Integer uncompressedSendBytes;

    @ColumnInfo(name="", descr="서버가 받은 경우  : 서버가 받은 데이터의 압축을 푼 후의 크기+EOT의 크기(14), 보낼 경우 : 내린 커맨드에 대한 응답으로 받은 데이터의 크기")
    @Column(name="UNCON_PRESSED_RCV_BYTES")
    private Integer unconPressedRcvBytes;

    @ColumnInfo(name="", descr="서버가 받은 경우 : ENQ+ACK의 크기(29), 보낼 경우 : 보낼 데이터 크기(디테일한 크기는 소스 참조)")
    @Column(name="SEND_BYTES")
    private Integer sendBytes;

    @ColumnInfo(name="", descr="서버가 받은 경우  : 서버가 받은 데이터의 크기+EOT의 크기(14), 보낼 경우 : 내린 커맨드에 대한 응답으로 받은 데이터의 크기")
    @Column(name="RCV_BYTES")
    private Integer rcvBytes;

    @ColumnInfo(name="", descr="0: 실패, 1: 성공")
    @Column(name="COMM_RESULT")
    private Integer commResult;

    @ColumnInfo(name="", descr="데이터가 검침데이터인 경우 전체 measument data의 개수")
    @Column(name="TOTAL_MEASUMENT_DATA_CNT")
    private Integer totalMeasumentDataCnt;

    @ColumnInfo(name="", descr="저장에 성공한 measument data의 개수")
    @Column(name="SUCC_MEASUMENT_DATA_CNT")
    private Integer succMeasumentDataCnt;

    @ColumnInfo(name="", descr="저장에 실패한 measument data의 개수")
    @Column(name="ERROR_MEASUMENT_DATA_CNT")
    private Integer errorMeasumentDataCnt;

    @ColumnInfo(name="", descr="저장에 실패한 경우에 대한 상세 기술 (구분자를 이용해 Error 개수 만큼 기술)")
    @Column(name="ERROR_REASON", length=2000)
    private String errorReason;

    @ColumnInfo(name="SVC타입 아이디", descr="코드 테이블의 ID나  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SVC_TYPE_CODE")
    @ReferencedBy(name="code")
    private Code svcTypeCode;
    
    @Column(name="SVC_TYPE_CODE", nullable=true, updatable=false, insertable=false)
    private Integer svcTypeCodeId;

    @ColumnInfo(name="", descr="")
    @Column(name="DESCR")
    private String descr;

    @ColumnInfo(name="지역아이디", descr="지역 테이블의 ID나  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="LOCATION_ID")
    @ReferencedBy(name="name")
    private Location location;
	
    @Column(name="LOCATION_ID", nullable=true, updatable=false, insertable=false)
    private Integer locationId;
    
	@Transient
	private String cnt;
	
	@Transient
	private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSuppliedId() {
        return suppliedId;
    }

    public void setSuppliedId(String suppliedId) {
        this.suppliedId = suppliedId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
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

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    @XmlTransient
    public Code getInterfaceCode() {
        return interfaceCode;
    }

    public void setInterfaceCode(Code interfaceCode) {
        this.interfaceCode = interfaceCode;
    }

    @XmlTransient
    public Code getProtocolCode() {
        return protocolCode;
    }

    public void setProtocolCode(Code protocolCode) {
        this.protocolCode = protocolCode;
    }

    @XmlTransient
    public Code getSenderTypeCode() {
        return senderTypeCode;
    }

    public void setSenderTypeCode(Code senderTypeCode) {
        this.senderTypeCode = senderTypeCode;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderIp() {
        return senderIp;
    }

    public void setSenderIp(String senderIp) {
        this.senderIp = senderIp;
    }

    public String getSenderPort() {
        return senderPort;
    }

    public void setSenderPort(String senderPort) {
        this.senderPort = senderPort;
    }

    @XmlTransient
    public Location getSenderLocation() {
        return senderLocation;
    }

    public void setSenderLocation(Location senderLocation) {
        this.senderLocation = senderLocation;
    }

    @XmlTransient
    public Code getReceiverTypeCode() {
        return receiverTypeCode;
    }

    public void setReceiverTypeCode(Code receiverTypeCode) {
        this.receiverTypeCode = receiverTypeCode;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverIp() {
        return receiverIp;
    }

    public void setReceiverIp(String receiverIp) {
        this.receiverIp = receiverIp;
    }

    public String getReceiverPort() {
        return receiverPort;
    }

    public void setReceiverPort(String receiverPort) {
        this.receiverPort = receiverPort;
    }

    public String getOperationCode() {
        return operationCode;
    }

    public void setOperationCode(String operationCode) {
        this.operationCode = operationCode;
    }

    public Integer getTotalCommTime() {
        return totalCommTime;
    }

    public void setTotalCommTime(Integer totalCommTime) {
        this.totalCommTime = totalCommTime;
    }

    public Integer getIsCompressed() {
        return isCompressed;
    }

    public void setIsCompressed(Integer isCompressed) {
        this.isCompressed = isCompressed;
    }

    public Integer getUncompressedSendBytes() {
        return uncompressedSendBytes;
    }

    public void setUncompressedSendBytes(Integer uncompressedSendBytes) {
        this.uncompressedSendBytes = uncompressedSendBytes;
    }

    public Integer getUnconPressedRcvBytes() {
        return unconPressedRcvBytes;
    }

    public void setUnconPressedRcvBytes(Integer unconPressedRcvBytes) {
        this.unconPressedRcvBytes = unconPressedRcvBytes;
    }

    public Integer getSendBytes() {
        return sendBytes;
    }

    public void setSendBytes(Integer sendBytes) {
        this.sendBytes = sendBytes;
    }

    public Integer getRcvBytes() {
        return rcvBytes;
    }

    public void setRcvBytes(Integer rcvBytes) {
        this.rcvBytes = rcvBytes;
    }

    public Integer getCommResult() {
        return commResult;
    }

    public void setCommResult(Integer commResult) {
        this.commResult = commResult;
    }

    public Integer getTotalMeasumentDataCnt() {
        return totalMeasumentDataCnt;
    }

    public void setTotalMeasumentDataCnt(Integer totalMeasumentDataCnt) {
        this.totalMeasumentDataCnt = totalMeasumentDataCnt;
    }

    public Integer getSuccMeasumentDataCnt() {
        return succMeasumentDataCnt;
    }

    public void setSuccMeasumentDataCnt(Integer succMeasumentDataCnt) {
        this.succMeasumentDataCnt = succMeasumentDataCnt;
    }

    public Integer getErrorMeasumentDataCnt() {
        return errorMeasumentDataCnt;
    }

    public void setErrorMeasumentDataCnt(Integer errorMeasumentDataCnt) {
        this.errorMeasumentDataCnt = errorMeasumentDataCnt;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    @XmlTransient
    public Code getSvcTypeCode() {
        return svcTypeCode;
    }

    public void setSvcTypeCode(Code svcTypeCode) {
        this.svcTypeCode = svcTypeCode;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    @XmlTransient
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCnt() {
        return cnt;
    }

    public void setCnt(String cnt) {
        this.cnt = cnt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getInterfaceCodeId() {
        return interfaceCodeId;
    }

    public void setInterfaceCodeId(Integer interfaceCodeId) {
        this.interfaceCodeId = interfaceCodeId;
    }

    public Integer getProtocolCodeId() {
        return protocolCodeId;
    }

    public void setProtocolCodeId(Integer protocolCodeId) {
        this.protocolCodeId = protocolCodeId;
    }

    public Integer getSenderTypeCodeId() {
        return senderTypeCodeId;
    }

    public void setSenderTypeCodeId(Integer senderTypeCodeId) {
        this.senderTypeCodeId = senderTypeCodeId;
    }

    public Integer getSenderLocationId() {
        return senderLocationId;
    }

    public void setSenderLocationId(Integer senderLocationId) {
        this.senderLocationId = senderLocationId;
    }

    public Integer getReceiverTypeCodeId() {
        return receiverTypeCodeId;
    }

    public void setReceiverTypeCodeId(Integer receiverTypeCodeId) {
        this.receiverTypeCodeId = receiverTypeCodeId;
    }

    public Integer getSvcTypeCodeId() {
        return svcTypeCodeId;
    }

    public void setSvcTypeCodeId(Integer svcTypeCodeId) {
        this.svcTypeCodeId = svcTypeCodeId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        result = prime * result + ((cnt == null) ? 0 : cnt.hashCode());
        result = prime * result
                + ((commResult == null) ? 0 : commResult.hashCode());
        result = prime * result + ((descr == null) ? 0 : descr.hashCode());
        result = prime * result + ((endTime == null) ? 0 : endTime.hashCode());
        result = prime
                * result
                + ((errorMeasumentDataCnt == null) ? 0 : errorMeasumentDataCnt
                        .hashCode());
        result = prime * result
                + ((errorReason == null) ? 0 : errorReason.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result
                + ((interfaceCode == null) ? 0 : interfaceCode.hashCode());
        result = prime * result
                + ((isCompressed == null) ? 0 : isCompressed.hashCode());
        result = prime * result
                + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result
                + ((operationCode == null) ? 0 : operationCode.hashCode());
        result = prime * result
                + ((protocolCode == null) ? 0 : protocolCode.hashCode());
        result = prime * result
                + ((rcvBytes == null) ? 0 : rcvBytes.hashCode());
        result = prime * result
                + ((receiverId == null) ? 0 : receiverId.hashCode());
        result = prime * result
                + ((receiverIp == null) ? 0 : receiverIp.hashCode());
        result = prime * result
                + ((receiverPort == null) ? 0 : receiverPort.hashCode());
        result = prime
                * result
                + ((receiverTypeCode == null) ? 0 : receiverTypeCode.hashCode());
        result = prime * result
                + ((sendBytes == null) ? 0 : sendBytes.hashCode());
        result = prime * result
                + ((senderId == null) ? 0 : senderId.hashCode());
        result = prime * result
                + ((senderIp == null) ? 0 : senderIp.hashCode());
        result = prime * result
                + ((senderLocation == null) ? 0 : senderLocation.hashCode());
        result = prime * result
                + ((senderPort == null) ? 0 : senderPort.hashCode());
        result = prime * result
                + ((senderTypeCode == null) ? 0 : senderTypeCode.hashCode());
        result = prime * result
                + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result
                + ((startDateTime == null) ? 0 : startDateTime.hashCode());
        result = prime * result
                + ((startTime == null) ? 0 : startTime.hashCode());
        result = prime
                * result
                + ((succMeasumentDataCnt == null) ? 0 : succMeasumentDataCnt
                        .hashCode());
        result = prime * result
                + ((suppliedId == null) ? 0 : suppliedId.hashCode());
        result = prime * result
                + ((svcTypeCode == null) ? 0 : svcTypeCode.hashCode());
        result = prime * result
                + ((totalCommTime == null) ? 0 : totalCommTime.hashCode());
        result = prime
                * result
                + ((totalMeasumentDataCnt == null) ? 0 : totalMeasumentDataCnt
                        .hashCode());
        result = prime
                * result
                + ((uncompressedSendBytes == null) ? 0 : uncompressedSendBytes
                        .hashCode());
        result = prime
                * result
                + ((unconPressedRcvBytes == null) ? 0 : unconPressedRcvBytes
                        .hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        CommLog other = (CommLog) obj;
        if (cnt == null) {
            if (other.cnt != null)
                return false;
        } else if (!cnt.equals(other.cnt))
            return false;
        if (commResult == null) {
            if (other.commResult != null)
                return false;
        } else if (!commResult.equals(other.commResult))
            return false;
        if (descr == null) {
            if (other.descr != null)
                return false;
        } else if (!descr.equals(other.descr))
            return false;
        if (endTime == null) {
            if (other.endTime != null)
                return false;
        } else if (!endTime.equals(other.endTime))
            return false;
        if (errorMeasumentDataCnt == null) {
            if (other.errorMeasumentDataCnt != null)
                return false;
        } else if (!errorMeasumentDataCnt.equals(other.errorMeasumentDataCnt))
            return false;
        if (errorReason == null) {
            if (other.errorReason != null)
                return false;
        } else if (!errorReason.equals(other.errorReason))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (interfaceCode == null) {
            if (other.interfaceCode != null)
                return false;
        } else if (!interfaceCode.equals(other.interfaceCode))
            return false;
        if (isCompressed == null) {
            if (other.isCompressed != null)
                return false;
        } else if (!isCompressed.equals(other.isCompressed))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (operationCode == null) {
            if (other.operationCode != null)
                return false;
        } else if (!operationCode.equals(other.operationCode))
            return false;
        if (protocolCode == null) {
            if (other.protocolCode != null)
                return false;
        } else if (!protocolCode.equals(other.protocolCode))
            return false;
        if (rcvBytes == null) {
            if (other.rcvBytes != null)
                return false;
        } else if (!rcvBytes.equals(other.rcvBytes))
            return false;
        if (receiverId == null) {
            if (other.receiverId != null)
                return false;
        } else if (!receiverId.equals(other.receiverId))
            return false;
        if (receiverIp == null) {
            if (other.receiverIp != null)
                return false;
        } else if (!receiverIp.equals(other.receiverIp))
            return false;
        if (receiverPort == null) {
            if (other.receiverPort != null)
                return false;
        } else if (!receiverPort.equals(other.receiverPort))
            return false;
        if (receiverTypeCode == null) {
            if (other.receiverTypeCode != null)
                return false;
        } else if (!receiverTypeCode.equals(other.receiverTypeCode))
            return false;
        if (sendBytes == null) {
            if (other.sendBytes != null)
                return false;
        } else if (!sendBytes.equals(other.sendBytes))
            return false;
        if (senderId == null) {
            if (other.senderId != null)
                return false;
        } else if (!senderId.equals(other.senderId))
            return false;
        if (senderIp == null) {
            if (other.senderIp != null)
                return false;
        } else if (!senderIp.equals(other.senderIp))
            return false;
        if (senderLocation == null) {
            if (other.senderLocation != null)
                return false;
        } else if (!senderLocation.equals(other.senderLocation))
            return false;
        if (senderPort == null) {
            if (other.senderPort != null)
                return false;
        } else if (!senderPort.equals(other.senderPort))
            return false;
        if (senderTypeCode == null) {
            if (other.senderTypeCode != null)
                return false;
        } else if (!senderTypeCode.equals(other.senderTypeCode))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (startDateTime == null) {
            if (other.startDateTime != null)
                return false;
        } else if (!startDateTime.equals(other.startDateTime))
            return false;
        if (startTime == null) {
            if (other.startTime != null)
                return false;
        } else if (!startTime.equals(other.startTime))
            return false;
        if (succMeasumentDataCnt == null) {
            if (other.succMeasumentDataCnt != null)
                return false;
        } else if (!succMeasumentDataCnt.equals(other.succMeasumentDataCnt))
            return false;
        if (suppliedId == null) {
            if (other.suppliedId != null)
                return false;
        } else if (!suppliedId.equals(other.suppliedId))
            return false;
        if (svcTypeCode == null) {
            if (other.svcTypeCode != null)
                return false;
        } else if (!svcTypeCode.equals(other.svcTypeCode))
            return false;
        if (totalCommTime == null) {
            if (other.totalCommTime != null)
                return false;
        } else if (!totalCommTime.equals(other.totalCommTime))
            return false;
        if (totalMeasumentDataCnt == null) {
            if (other.totalMeasumentDataCnt != null)
                return false;
        } else if (!totalMeasumentDataCnt.equals(other.totalMeasumentDataCnt))
            return false;
        if (uncompressedSendBytes == null) {
            if (other.uncompressedSendBytes != null)
                return false;
        } else if (!uncompressedSendBytes.equals(other.uncompressedSendBytes))
            return false;
        if (unconPressedRcvBytes == null) {
            if (other.unconPressedRcvBytes != null)
                return false;
        } else if (!unconPressedRcvBytes.equals(other.unconPressedRcvBytes))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "CommLog [cnt=" + cnt + ", commResult=" + commResult
                + ", descr=" + descr + ", endTime=" + endTime
                + ", errorMeasumentDataCnt=" + errorMeasumentDataCnt
                + ", errorReason=" + errorReason + ", id=" + id
                + ", interfaceCode=" + interfaceCode + ", isCompressed="
                + isCompressed + ", locationId=" + location + ", name="
                + name + ", operationCode=" + operationCode + ", protocolCode="
                + protocolCode + ", rcvBytes=" + rcvBytes + ", receiverId="
                + receiverId + ", receiverIp=" + receiverIp + ", receiverPort="
                + receiverPort + ", receiverTypeCode=" + receiverTypeCode
                + ", sendBytes=" + sendBytes + ", senderId=" + senderId
                + ", senderIp=" + senderIp + ", senderLocation="
                + senderLocation + ", senderPort=" + senderPort
                + ", senderTypeCode=" + senderTypeCode + ", startDate="
                + startDate + ", startDateTime=" + startDateTime
                + ", startTime=" + startTime + ", succMeasumentDataCnt="
                + succMeasumentDataCnt + ", suppliedId=" + suppliedId
                + ", svcTypeCode=" + svcTypeCode + ", totalCommTime="
                + totalCommTime + ", totalMeasumentDataCnt="
                + totalMeasumentDataCnt + ", uncompressedSendBytes="
                + uncompressedSendBytes + ", unconPressedRcvBytes="
                + unconPressedRcvBytes + "]";
    }
}
