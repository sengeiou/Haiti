package com.aimir.model.system;

import javax.persistence.Column;
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
 * <p>Copyright NuriTelecom Co.Ltd. since 2012</p>
 * SAPAssetInfos.java 
 * SAP에서 전달된 Outbound file의 정보를 저장한다.
 *
 * 
 * Date          Version    Author   Description
 * 2012. 6. 4.   v1.0       enj      initial version   
 *
 *
 * @author Mie Eun(enj)
 */
@Entity
@Table(name="SAP_ASSET_INFOS")
public class SAPAssetInfos extends BaseObject implements JSONString{

	static final long serialVersionUID = 2973162458901580968L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SAP_ASSET_INFOS_SEQ")
	@SequenceGenerator(name = "SAP_ASSET_INFOS_SEQ", sequenceName = "SAP_ASSET_INFOS_SEQ", allocationSize = 1)
    private Integer id;

	@Column(name = "METER_READER", length=3)
    @ColumnInfo(name="Order Download Platform Code ", descr="Mandla Technology : MT fixed")
    private String meterReader;
	
	
	@Column(name = "METER_READING_UNIT", length=8)
	@ColumnInfo(name = "Meter Reading Unit")
	private String meterReadingUnit;
	
	@Column(name="INSTALLATION_NUMBER", nullable=false, unique=true, length=10)
	@ColumnInfo(name="Installation number")
	private String installationNumber;
	
	@Column(name = "DIVISION", length=2)
	@ColumnInfo(name="Division: Electricity")
	private String division;
	
	@Column(name = "GIS_KEY", length=21)
	@ColumnInfo(name="GIS Key")	
	private String gisKey;
	
	@Column(name = "PREMIS_NUMBER", length=10)
	@ColumnInfo(name="Premise Number")		
	private String premiseNumber;
	
	@Column(name = "ERF_NO", length=5)
	@ColumnInfo(name="Erf No: (Note 2)")		
	private Integer erfNo;
	
	@Column(name = "HOUSE_NUMBER", length=10)
	@ColumnInfo(name="House number")	
	private String houseNumber;
	
	@Column(name = "STREET_NUMBER", length=60)
	@ColumnInfo(name="Street Name")	
	private String streetName;
	
	@Column(name = "BUILDING_NAME", length=40)
	@ColumnInfo(name="Building name")		
	private String buildingName;
	
	@Column(name = "COMPLEX_NAME", length=40)
	@ColumnInfo(name="Complex Name")	
	private String complexName;
	
	@Column(name = "SUBURB", length=40)
	@ColumnInfo(name="Suburb")	
	private String suburb;
	
	@Column(name = "CONTRACT_NUMBER", length=12)
	@ColumnInfo(name="Contract A/C Number")	
	private String contractNumber;
	
	@Column(name = "NAME", length=40)
	@ColumnInfo(name="Name")		
	private String name;
	
	@Column(name = "METER_READER_NOTES", length=50)
	@ColumnInfo(name="Meter Reader Notes")	
	private String meterReaderNotes;
	
	@Column(name = "DEVICE_LOCATION", length=50)
	@ColumnInfo(name="Device Location")		
	private String deviceLocation;
	
	@Column(name = "KEY_NUMBER", length=50)
	@ColumnInfo(name="Key Number")
	private String keyNumber;
	
	@Column(name = "ACCESS_CODE", length=50)
	@ColumnInfo(name="Access Code")	
	private String accessCode;
	
	@Column(name = "REMARKS4", length=50)
    @ColumnInfo(name = "Remarks 4 – Not Currently Used")
	private String remarks4;
	
	@Column(name = "REMARKS5", length=50)
	@ColumnInfo(name = "Remarks 5 – Not Currently Used")
	private String remarks5;
	
	@Column(name = "REMARKS6", length=50)
	@ColumnInfo(name = "Remarks 6 – Not Currently Used")	
	private String remarks6;
	
	@Column(name = "DEVICE_CATEGORY", length=18)
	@ColumnInfo(name="Device Category")		
	private String deviceCategroy;

	/**
	 * Serial Number1 제작사 코드(4자리)
	 * <br>ThirdParty Serial Number
	 */
	@Column(name = "SERIAL_NUMBER1", length=4)
	@ColumnInfo(name="Product Code of Meter")		
	private String serialNumber1;
	
	@Column(name = "SERIAL_NUMBER2", length=8)
	@ColumnInfo(name="Meter Serial Number")		
	private String serialNumber2;
	
	@Column(name = "SERIAL_NUMBER3", length=1)
	@ColumnInfo(name="identifier of Meter")		
	private String serialNumber3;
	
	@Column(name = "RATE_CATEGORY", length=18)
	@ColumnInfo(name="Rate Category")		
	private String rateCategory;
	
	@Column(name = "REGISTER_NUMBER", length=3)
	@ColumnInfo(name="Register Number")		
	private Integer registerNumber;
	
	@Column(name = "UNIT_OF_MEASURE", length=5)
	@ColumnInfo(name="Unit of Measure")	
	private String unitOfMeasure;
	
	@Column(name = "REGISTER_TYPE", length=50)
	@ColumnInfo(name="Register Type")		
	private String registerType;
	
	@Column(name = "REGISTER_FACTOR", length=12)
	@ColumnInfo(name="Register Factor")	
	private Double registerFactor;
	
	@Column(name = "DIGITS_BEFORE_DECIMAL", length=2)
	@ColumnInfo(name="Digits before the decimal points")	
	private Integer digitsBeforTheDecimalPoint;
	
	@Column(name = "DIGITS_AFTER_DECIMAL", length=2)
	@ColumnInfo(name="Digits after the decimal points")
	private Integer digitsAfterTheDecimalPoint;
	
	@Column(name = "LAST_MR", length=36)
	@ColumnInfo(name="Last Meter Reading")
	private String lastMR;
	
	@Column(name = "DATE_OF_LAST_MR", length=8)
	@ColumnInfo(name="Date of last Meter Reading")	
	private String dateOfLastMR;
	
	@Column(name = "INSTALLATION_TYPE", length=4)
	@ColumnInfo(name="Installation Type")	
	private String installationType;
	
	@Column(name = "MOBILE_NUMBER", length=30)
	@ColumnInfo(name="Mobile Number")		
	private String mobileNumber;
	
	@Column(name = "EMAIL_ADDRESS", length=241)
	@ColumnInfo(name="Email Address")	
	private String emailAddress;
	
	@Column(name = "PREVIOUS_METER_READING_TYPE", length=2)
	@ColumnInfo(name="Previous Meter reading type")	
	private String previousMeterReadingType;
	
	@Column(name = "ROOM_NUMBER", length=10)
	@ColumnInfo(name="Room Number")		
	private String roomNumber;
	
	@Column(name = "CREATE_DATE", length=14)
	@ColumnInfo(name="Create Date")		
	private String createDate;

	@Column(name = "WRITE_DATE", length=14)
	@ColumnInfo(name="Write Date")		
	private String writeDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMeterReader() {
		return meterReader;
	}

	public void setMeterReader(String meterReader) {
		this.meterReader = meterReader;
	}

	public String getMeterReadingUnit() {
		return meterReadingUnit;
	}

	public void setMeterReadingUnit(String meterReadingUnit) {
		this.meterReadingUnit = meterReadingUnit;
	}

	public String getInstallationNumber() {
		return installationNumber;
	}

	public void setInstallationNumber(String installationNumber) {
		this.installationNumber = installationNumber;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getGisKey() {
		return gisKey;
	}

	public void setGisKey(String gisKey) {
		this.gisKey = gisKey;
	}

	public String getPremiseNumber() {
		return premiseNumber;
	}

	public void setPremiseNumber(String premiseNumber) {
		this.premiseNumber = premiseNumber;
	}

	public Integer getErfNo() {
		return erfNo;
	}

	public void setErfNo(Integer erfNo) {
		this.erfNo = erfNo;
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

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMeterReaderNotes() {
		return meterReaderNotes;
	}

	public void setMeterReaderNotes(String meterReaderNotes) {
		this.meterReaderNotes = meterReaderNotes;
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

	public String getDeviceCategroy() {
		return deviceCategroy;
	}

	public void setDeviceCategroy(String deviceCategroy) {
		this.deviceCategroy = deviceCategroy;
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

	public Integer getRegisterNumber() {
		return registerNumber;
	}

	public void setRegisterNumber(Integer registerNumber) {
		this.registerNumber = registerNumber;
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

	public Integer getDigitsBeforTheDecimalPoint() {
		return digitsBeforTheDecimalPoint;
	}

	public void setDigitsBeforTheDecimalPoint(Integer digitsBeforTheDecimalPoint) {
		this.digitsBeforTheDecimalPoint = digitsBeforTheDecimalPoint;
	}

	public Integer getDigitsAfterTheDecimalPoint() {
		return digitsAfterTheDecimalPoint;
	}

	public void setDigitsAfterTheDecimalPoint(Integer digitsAfterTheDecimalPoint) {
		this.digitsAfterTheDecimalPoint = digitsAfterTheDecimalPoint;
	}

	public String getLastMR() {
		return lastMR;
	}

	public void setLastMR(String lastMR) {
		this.lastMR = lastMR;
	}

	public String getDateOfLastMR() {
		return dateOfLastMR;
	}

	public void setDateOfLastMR(String dateOfLastMR) {
		this.dateOfLastMR = dateOfLastMR;
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

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	@Override
	public String toJSONString() {
		// TODO Auto-generated method stub
		return null;
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
