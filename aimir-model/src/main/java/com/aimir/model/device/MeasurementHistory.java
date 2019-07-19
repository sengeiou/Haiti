package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

import org.eclipse.persistence.annotations.Index;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p> FEP와 DCU 혹은 모뎀으로부터 수신한 원시 데이터 정보를 가지는 클래스</p>
 * 
 * @author goodjob
 *
 */
@Entity
@Table(name = "MEASUREMENT_HISTORY")
@Index(name="IDX_MEASUREMENT_HISTORY_01", columnNames={"DEVICE_TYPE", "DEVICE_ID", "YYYYMMDD"})
public class MeasurementHistory extends BaseObject {

	private static final long serialVersionUID = -6648957129102667976L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MEASUREMENT_HISTORY_SEQ")
    @SequenceGenerator(name="MEASUREMENT_HISTORY_SEQ", sequenceName="MEASUREMENT_HISTORY_SEQ", allocationSize=1) 
	private Long id;

	@Column(name="DEVICE_TYPE", length=20)
    @ColumnInfo(name="장비유형")
    private String deviceType;
    
    @Column(name="DEVICE_ID", length=30)
    @ColumnInfo(name="장비아이디")
    private String deviceId;
    
    @Column(name="WRITE_DATE", length=14)
    @ColumnInfo(name="저장시각 연월일 시분초")
	private String writeDate;
    
    @Column(name="YYYYMMDD", length=8)
    @ColumnInfo(name="저장시 연월일")
	private String yyyymmdd;
    
    @Column(name="HHMMSS", length=6)
    @ColumnInfo(name="저장시 시분초")
	private String hhmmss;
    
    @Column(name="DATA_TYPE")
    @ColumnInfo(name="데이터 타입")
	private Integer dataType;
    
    @Column(name="DATA_COUNT")
    @ColumnInfo(name="Data Entry Count")
	private Integer dataCount;
	
    @Column(name="RAW_DATA")
    @ColumnInfo(name="원데이터")
    private byte[] rawData;    

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
	
	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public String getYyyymmdd() {
		return yyyymmdd;
	}

	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}

	public String getHhmmss() {
		return hhmmss;
	}

	public void setHhmmss(String hhmmss) {
		this.hhmmss = hhmmss;
	}

	public Integer getDataType() {
		return dataType;
	}

	public void setDataType(Integer dataType) {
		this.dataType = dataType;
	}

	public Integer getDataCount() {
		return dataCount;
	}

	public void setDataCount(Integer dataCount) {
		this.dataCount = dataCount;
	}

	public byte[] getRawData() {
		return rawData;
	}

	public void setRawData(byte[] rawData) {
		this.rawData = rawData;
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
