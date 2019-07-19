package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.sf.json.JSONString;

import com.aimir.model.BaseObject;

import org.eclipse.persistence.annotations.Index;
/**
 * SAP Integration 명령관련 모델 (COT OutBound File 참조)
 * <br> + validMeteringDate(5일간 유효)</br>
 * @author Ji Hoon KIM(jihoon)
 *
 */
@Entity
@Table(name = "SAPIntegrationLog")
@Index(name="IDX_SAPINTEGRATIONLOG_01", columnNames={"Meter_Reading_Order_Number"})
public class SAPIntegrationLog extends BaseObject implements JSONString{
	
	private static final long serialVersionUID = -6594382001300312223L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SAPIntegrationLog_SEQ")
    @SequenceGenerator(name="SAPIntegrationLog_SEQ", sequenceName="SAPIntegrationLog_SEQ", allocationSize=1) 
	private Integer id;
	
	@Column(name="Record_Number")
	private Integer recordNumber;
	
	/**
	 * Code Identifying Master Station
	 * <br> "MT" : fixed
	 */
	@Column(name="Order_Download_Platform_Code")
	private String orderDownloadPlatformCode;
	
	/**
	 * Meter group name
	 */
	@Column(name="Meter_Reading_Unit")
	private String meterReadingUnit;
	
	/**
	 * Sequence Number
	 */
	@Column(name="Sequence_Number")
	private String sequenceNumber;
	
	/**
	 * 명령 번호
	 * <br>해당 명령의 고유값, 중복을 허용하지 않는다.
	 * Unique Identifier on SAP (most important field)
	 */
	@Column(name="Meter_Reading_Order_Number")
	private String meterReadingOrderNumber;	
	
	/**
	 * metering point
	 */
	@Column(name="Installation_number")
	private String installationNumber;
	
	/**
	 * Division: Electricity
	 * Meter Type
	 */
	@Column(name="Division_Electricity")
	private String divisionElectricity;
	
	/**
	 * GIS Key
	 * GIS Key (We will add a new field for GIS Key
	 */
	@Column(name="GIS_Key")
	private String GISKey;
	
	/**
	 * Premise Number
	 */
	@Column(name="Premise_Number")
	private String premiseNumber;
	
	/**
	 * Erf No
	 */
	@Column(name="Erf_No")
	private Integer erfNumber;
	
	//Address
	/**
	 * House number
	 */
	@Column(name="House_Number")
	private String houseNumber;
	
	/**
	 * Street Name
	 */
	@Column(name="Street_Name")
	private String streetName;
	
	/**
	 * Building name
	 */
	@Column(name="Building_Name")
	private String buildingName;
	
	/**
	 * Complex Name
	 */
	@Column(name="Complex_Name")
	private String complexName;
	
	/**
	 * Suburb
	 */
	@Column(name="Suburb")
	private String suburb;
	//Address End
	
	/**
	 * Contract A/C Number
	 * <br>Account Number
	 */
	@Column(name="Account_Number")
	private String contractACNumber;

	/**
	 * Name 
	 */
	@Column(name="Name")
	private String name;

	/**
	 * Meter Reader Notes
	 * <br>검침실패시 이유(이벤트) 저장
	 */
	@Column(name="Meter_Reader_Notes")
	private String meterReaderNote;

	/**
	 * Device Location 
	 * <br>(GPIOX,GPIOY,GPIOZ)
	 */
	@Column(name="Device_Location")
	private String deviceLocation;

	/**
	 * Key Number 
	 */
	@Column(name="Key_Number")
	private String keyNumber;

	/**
	 * Access Code 
	 */
	@Column(name="Access_Code")
	private String accessCode;

	/**
	 * Remarks 4 – Not Currently Used
	 */
	@Column(name="Remarks_4")
	private String remarks4;

	/**
	 * Remarks 5 – Not Currently Used
	 */
	@Column(name="Remarks_5")
	private String remarks5;

	/**
	 * Remarks 6 – Not Currently Used
	 */
	@Column(name="Remarks_6")
	private String remarks6;

	/**
	 * Device Category
	 * <br>Meter Device Category Code (We will add a new field for Device Cagetory Code), CT, LPU, SPU,,,
	 */
	@Column(name="Device_Category")
	private String deviceCategory; //Unique

	/**
	 * Serial Number1 제작사 코드(4자리)
	 * <br>ThirdParty Serial Number
	 */
	@Column(name="Serial_Number1")
	private String serialNumber1;

	/**
	 * Serial Number 미터 넘버(8자리)
	 * <br>ThirdParty Serial Number
	 */
	@Column(name="Serial_Number2")
	private String serialNumber2;
	
	/**
	 * Serial Number Check digit(1자리)
	 * <br>ThirdParty Serial Number
	 */
	@Column(name="Serial_Number3")
	private String serialNumber3;
	
	/**
	 * Rate Category
	 * <br>Tariff Index
	 */
	@Column(name="Rate_Category")
	private String rateCategory;

	/**
	 * Register No
	 * <br>The register number of the device register on SAP
	 */
	@Column(name="Register_No")
	private Integer registerNo;

	/**
	 * Unit of Measure
	 * <br>Type of register is being measured
	 */
	@Column(name="Unit_Of_Measure")
	private String unitOfMeasure;

	/**
	 * Register Type
	 */
	@Column(name="Register_Type")
	private String registerType;

	/**
	 * Register Factor
	 * <br>"1" : fixed
	 */
	@Column(name="Register_Factor")
	private Double registerFactor;

	/**
	 * Digits before the decimal point
	 */
	@Column(name="Digits_Before_Decimal")
	private Integer digitsBeforeTheDecimalPoint;

	/**
	 * Digits after the decimal points
	 */
	@Column(name="Digits_After_Decimal")
	private Integer digitsAfterTheDecimalPoint;

	/**
	 * Last Reading
	 * <br>Last Meter Reading Value
	 */
	@Column(name="Last_Reading")
	private String lastReading;

	/**
	 * Date of last MR
	 */
	@Column(name="Date_Of_Last_MR")
	private String dateOfLastMR;

	/**
	 * Scheduled Meter Reading Date
	 * <br>Bill Date
	 */
	@Column(name="Bill_Date")
	private String scheduledMeterReadingDate;

	/**
	 * Expected Meter reading
	 */
	@Column(name="Expected_Meter_Reading")
	private String expectedMeterReading;

	/**
	 * Upper Limit of Meter Reading
	 */
	@Column(name="Upper_Limit_Of_Meter_Reading")
	private String upperLimitOfMeterReading;

	/**
	 * Lower limit of meter reading
	 */
	@Column(name="Lower_Limit_Of_Meter_Reading")
	private String lowerLimitOfMeterReading;

	/**
	 * Installation Type
	 */
	@Column(name="Installation_Type")
	private String installationType;

	/**
	 * Mobile Number
	 */
	@Column(name="Mobile_Number")
	private String mobileNumber;

	/**
	 * Email Address
	 */
	@Column(name="Email_Address")
	private String emailAddress;

	/**
	 * Previous Meter reading type
	 */
	@Column(name="Previous_Meter_Reading_Type")
	private String previousMeterReadingType;

	/**
	 * Room Number
	 */
	@Column(name="Room_Number")
	private String roomNumber;
	
	/**
	 * Meter Reading Value
	 * <br>Inbound File
	 */
	@Column(name="Meter_Reading_Value")
	private String meterReadingValue;
	
	/**
	 * Meter Reading Date
	 * <br>Inbound File
	 */
	@Column(name="Meter_Reading_Date")
	private String metereadingDate;
	
	/**
	 * Meter Reading Time
	 * <br>Inbound File
	 */
	@Column(name="Meter_Reading_Time")
	private String meterReadingTime;
	
	/**
	 * Meter Reading Note
	 * <br>Inbound File
	 */
	@Column(name="Meter_Reading_Note")
	private String meterReadingNote;	

	/**
	 * Outbound 파일을 읽어서 로그로 생성한 날짜
	 */
	@Column(name="YYYYMMDDHHMMSS")
	private String yyyymmddhhmmss;
	
	/**
	 * 명령 및 업로드 성공여부
	 */
	@Column(name="Result_State")
	private Boolean resultState;
	
	/**
	 * Retry 상태
	 */
	@Column(name="NOT_Retry")
	private Boolean notRetry;
	
	/**
	 * 명령 수행 Deadline
	 */
	@Column(name="Deadline")
	private String deadline;
	
	/**
	 * 에러 사유
	 * @return
	 */
	@Column(name="ERROR_REASON", length=255)
	private String errorReason;
	
	/**
	 * Inbound 파일명
	 * @return
	 */
	@Column(name="INBOUND_FILENAME")
	private String inboundFileName;
	
	/**
     * Inbound 파일 생성 시간
     */
    @Column(name="INBOUND_WRITEDATE")
    private String inboundWriteDate;
    
	/**
	 * Outbound 파일명
	 * @return
	 */
	@Column(name="OUTBOUND_FILENAME")
	private String outboundFileName;
	
	/**
	 * Register Type에 대한 value
	 * @return
	 */
	@Column(name="REGISTER_VALUE")
	private Double registerValue;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRecordNumber() {
		return recordNumber;
	}

	public void setRecordNumber(Integer recordNumber) {
		this.recordNumber = recordNumber;
	}

	public String getOrderDownloadPlatformCode() {
		return orderDownloadPlatformCode;
	}

	public void setOrderDownloadPlatformCode(String orderDownloadPlatformCode) {
		this.orderDownloadPlatformCode = orderDownloadPlatformCode;
	}

	public String getMeterReadingUnit() {
		return meterReadingUnit;
	}

	public void setMeterReadingUnit(String meterReadingUnit) {
		this.meterReadingUnit = meterReadingUnit;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getMeterReadingOrderNumber() {
		return meterReadingOrderNumber;
	}

	public void setMeterReadingOrderNumber(String meterReadingOrderNumber) {
		this.meterReadingOrderNumber = meterReadingOrderNumber;
	}

	public String getInstallationNumber() {
		return installationNumber;
	}

	public void setInstallationNumber(String installationNumber) {
		this.installationNumber = installationNumber;
	}

	public String getDivisionElectricity() {
		return divisionElectricity;
	}

	public void setDivisionElectricity(String divisionElectricity) {
		this.divisionElectricity = divisionElectricity;
	}

	public String getGISKey() {
		return GISKey;
	}

	public void setGISKey(String gISKey) {
		GISKey = gISKey;
	}

	public String getPremiseNumber() {
		return premiseNumber;
	}

	public void setPremiseNumber(String premiseNumber) {
		this.premiseNumber = premiseNumber;
	}

	public Integer getErfNumber() {
		return erfNumber;
	}

	public void setErfNumber(Integer erfNumber) {
		this.erfNumber = erfNumber;
	}

	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}

	public String getComplexName() {
		return complexName;
	}

	public void setComplexName(String complexName) {
		this.complexName = complexName;
	}

	public String getSuburb() {
		return suburb;
	}

	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}

	public String getContractACNumber() {
		return contractACNumber;
	}

	public void setContractACNumber(String contractACNumber) {
		this.contractACNumber = contractACNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMeterReaderNote() {
		return meterReaderNote;
	}

	public void setMeterReaderNote(String meterReaderNote) {
		this.meterReaderNote = meterReaderNote;
	}

	public String getDeviceLocation() {
		return deviceLocation;
	}

	public void setDeviceLocation(String deviceLocation) {
		this.deviceLocation = deviceLocation;
	}

	public String getKeyNumber() {
		return keyNumber;
	}

	public void setKeyNumber(String keyNumber) {
		this.keyNumber = keyNumber;
	}

	public String getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	public String getRemarks4() {
		return remarks4;
	}

	public void setRemarks4(String remarks4) {
		this.remarks4 = remarks4;
	}

	public String getRemarks5() {
		return remarks5;
	}

	public void setRemarks5(String remarks5) {
		this.remarks5 = remarks5;
	}

	public String getRemarks6() {
		return remarks6;
	}

	public void setRemarks6(String remarks6) {
		this.remarks6 = remarks6;
	}

	public String getDeviceCategory() {
		return deviceCategory;
	}

	public void setDeviceCategory(String deviceCategory) {
		this.deviceCategory = deviceCategory;
	}

	public String getSerialNumber1() {
		return serialNumber1;
	}

	public void setSerialNumber1(String serialNumber1) {
		this.serialNumber1 = serialNumber1;
	}

	public String getSerialNumber2() {
		return serialNumber2;
	}

	public void setSerialNumber2(String serialNumber2) {
		this.serialNumber2 = serialNumber2;
	}

	public String getSerialNumber3() {
		return serialNumber3;
	}

	public void setSerialNumber3(String serialNumber3) {
		this.serialNumber3 = serialNumber3;
	}

	public String getRateCategory() {
		return rateCategory;
	}

	public void setRateCategory(String rateCategory) {
		this.rateCategory = rateCategory;
	}

	public Integer getRegisterNo() {
		return registerNo;
	}

	public void setRegisterNo(Integer registerNo) {
		this.registerNo = registerNo;
	}

	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}

	public void setUnitOfMeasure(String unitOfMeasure) {
		this.unitOfMeasure = unitOfMeasure;
	}

	public String getRegisterType() {
		return registerType;
	}

	public void setRegisterType(String registerType) {
		this.registerType = registerType;
	}

	public Double getRegisterFactor() {
		return registerFactor;
	}

	public void setRegisterFactor(Double registerFactor) {
		this.registerFactor = registerFactor;
	}

	public Integer getDigitsBeforeTheDecimalPoint() {
		return digitsBeforeTheDecimalPoint;
	}

	public void setDigitsBeforeTheDecimalPoint(Integer digitsBeforeTheDecimalPoint) {
		this.digitsBeforeTheDecimalPoint = digitsBeforeTheDecimalPoint;
	}

	public Integer getDigitsAfterTheDecimalPoint() {
		return digitsAfterTheDecimalPoint;
	}

	public void setDigitsAfterTheDecimalPoint(Integer digitsAfterTheDecimalPoint) {
		this.digitsAfterTheDecimalPoint = digitsAfterTheDecimalPoint;
	}

	public String getLastReading() {
		return lastReading;
	}

	public void setLastReading(String lastReading) {
		this.lastReading = lastReading;
	}

	public String getDateOfLastMR() {
		return dateOfLastMR;
	}

	public void setDateOfLastMR(String dateOfLastMR) {
		this.dateOfLastMR = dateOfLastMR;
	}

	public String getScheduledMeterReadingDate() {
		return scheduledMeterReadingDate;
	}

	public void setScheduledMeterReadingDate(String scheduledMeterReadingDate) {
		this.scheduledMeterReadingDate = scheduledMeterReadingDate;
	}

	public String getExpectedMeterReading() {
		return expectedMeterReading;
	}

	public void setExpectedMeterReading(String expectedMeterReading) {
		this.expectedMeterReading = expectedMeterReading;
	}

	public String getUpperLimitOfMeterReading() {
		return upperLimitOfMeterReading;
	}

	public void setUpperLimitOfMeterReading(String upperLimitOfMeterReading) {
		this.upperLimitOfMeterReading = upperLimitOfMeterReading;
	}

	public String getLowerLimitOfMeterReading() {
		return lowerLimitOfMeterReading;
	}

	public void setLowerLimitOfMeterReading(String lowerLimitOfMeterReading) {
		this.lowerLimitOfMeterReading = lowerLimitOfMeterReading;
	}

	public String getInstallationType() {
		return installationType;
	}

	public void setInstallationType(String installationType) {
		this.installationType = installationType;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPreviousMeterReadingType() {
		return previousMeterReadingType;
	}

	public void setPreviousMeterReadingType(String previousMeterReadingType) {
		this.previousMeterReadingType = previousMeterReadingType;
	}

	public String getRoomNumber() {
		return roomNumber;
	}

	public void setRoomNumber(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	public String getMeterReadingValue() {
		return meterReadingValue;
	}

	public void setMeterReadingValue(String meterReadingValue) {
		this.meterReadingValue = meterReadingValue;
	}

	public String getMetereadingDate() {
		return metereadingDate;
	}

	public void setMetereadingDate(String metereadingDate) {
		this.metereadingDate = metereadingDate;
	}

	public String getMeterReadingTime() {
		return meterReadingTime;
	}

	public void setMeterReadingTime(String meterReadingTime) {
		this.meterReadingTime = meterReadingTime;
	}

	public String getMeterReadingNote() {
		return meterReadingNote;
	}

	public void setMeterReadingNote(String meterReadingNote) {
		this.meterReadingNote = meterReadingNote;
	}

	public String getYyyymmddhhmmss() {
		return yyyymmddhhmmss;
	}

	public void setYyyymmddhhmmss(String yyyymmddhhmmss) {
		this.yyyymmddhhmmss = yyyymmddhhmmss;
	}

	public Boolean getResultState() {
		return resultState;
	}

	public void setResultState(Boolean resultState) {
		this.resultState = resultState;
	}
	
	public Boolean getNotRetry() {
		return notRetry;
	}

	public void setNotRetry(Boolean notRetry) {
		this.notRetry = notRetry;
	}

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public String getInboundFileName() {
        return inboundFileName;
    }

    public void setInboundFileName(String inboundFileName) {
        this.inboundFileName = inboundFileName;
    }

    public String getOutboundFileName() {
        return outboundFileName;
    }

    public void setOutboundFileName(String outboundFileName) {
        this.outboundFileName = outboundFileName;
    }

    public String getInboundWriteDate() {
        return inboundWriteDate;
    }

    public void setInboundWriteDate(String inboundWriteDate) {
        this.inboundWriteDate = inboundWriteDate;
    }

    public Double getRegisterValue() {
        return registerValue;
    }

    public void setRegisterValue(Double registerValue) {
        this.registerValue = registerValue;
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
	public String toJSONString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
