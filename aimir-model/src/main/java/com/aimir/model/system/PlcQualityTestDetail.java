package com.aimir.model.system;

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
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.Index;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;

@Entity
@Table(name="PLC_QUALITY_TEST_DETAIL")
@Index(name="IDX_PLC_QUALITY_TEST_DETAIL_01", columnNames={"COMPLETE_DATE","ZIG_ID"})
public class PlcQualityTestDetail extends BaseObject {

	private static final long serialVersionUID = -3248040699035316304L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PLC_QUALITY_TEST_DETAIL_SEQ")
	@SequenceGenerator(name="PLC_QUALITY_TEST_DETAIL_SEQ", sequenceName="PLC_QUALITY_TEST_DETAIL_SEQ", allocationSize=1) 
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="zig_id", nullable=false)
	@ReferencedBy(name="zigName")
	private PlcQualityTest plcQualityTest;	
	
	@Column(name="zig_id", nullable=false, updatable=false, insertable=false)
	private Integer zigId;
	
	@Column(name="TEST_RESULT")
	@ColumnInfo(name="testResult", descr="테스트 결과 저장, 성공일경우 true, 실패일 경우 false")
	private Boolean testResult;

	@Column(name="METER_SERIAL")
	@ColumnInfo(name="meterSerial", descr="meterSerial")
	private String meterSerial;
	
	@Column(name="MODEM_SERIAL")
	@ColumnInfo(name="modemSerial", descr="modemSerial")
	private String modemSerial;
	
	@Column(name="HW_VER", length=14)
	@ColumnInfo(descr="하드웨어 버전")
	private String hwVer;

	@Column(name="SW_VER")
	@ColumnInfo(descr="소프트웨어 버전")
	private String swVer;

	@Column(name="SW_BUILD")
	@ColumnInfo(descr="")
	private String swBuild;

	@Column(name="FAIL_REASON")
	@ColumnInfo(descr="")
	private String failReason;
	
	@Column(name="TEST_START_DATE", length=14)
	@ColumnInfo(descr="테스트 시작 날짜(yyyymmddhhMMss)")
	private String testStartDate;
	
	@Column(name="COMPLETE_DATE", length=14)
	@ColumnInfo(descr="테스트 완료 날짜(yyyymmddhhMMss)")
	private String completeDate;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	@XmlTransient
	public PlcQualityTest getPlcQualityTest() {
		return plcQualityTest;
	}
	public void setPlcQualityTest(PlcQualityTest plcQualityTest) {
		this.plcQualityTest = plcQualityTest;
	}
	public Integer getZigId() {
		return zigId;
	}
	public void setZigId(Integer zigId) {
		this.zigId = zigId;
	}
	public Boolean getTestResult() {
		return testResult;
	}
	public void setTestResult(Boolean testResult) {
		this.testResult = testResult;
	}
	public String getMeterSerial() {
		return meterSerial;
	}
	public void setMeterSerial(String meterSerial) {
		this.meterSerial = meterSerial;
	}
	public String getModemSerial() {
		return modemSerial;
	}
	public void setModemSerial(String modemSerial) {
		this.modemSerial = modemSerial;
	}
	public String getHwVer() {
		return hwVer;
	}
	public void setHwVer(String hwVer) {
		this.hwVer = hwVer;
	}
	public String getSwVer() {
		return swVer;
	}
	public void setSwVer(String swVer) {
		this.swVer = swVer;
	}
	public String getSwBuild() {
		return swBuild;
	}
	public void setSwBuild(String swBuild) {
		this.swBuild = swBuild;
	}
	public String getFailReason() {
		return failReason;
	}
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	public String getTestStartDate() {
		return testStartDate;
	}
	public void setTestStartDate(String testStartDate) {
		this.testStartDate = testStartDate;
	}
	public String getCompleteDate() {
		return completeDate;
	}
	public void setCompleteDate(String completeDate) {
		this.completeDate = completeDate;
	}
	
	@Override
	public String toString() {
		return null;
	}

	@Override
	public boolean equals(Object o) {
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
}
