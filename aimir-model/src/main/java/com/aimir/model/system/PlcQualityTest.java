package com.aimir.model.system;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.Index;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

@Entity
@Table(name="PLC_QUALITY_TEST")
@Index(name="IDX_PLC_QUALITY_TEST_01", columnNames={"COMPLETE_DATE","ZIG_NAME"})
public class PlcQualityTest extends BaseObject {

	private static final long serialVersionUID = -2381916563391390006L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PLC_QUALITY_TEST_SEQ")
	@SequenceGenerator(name="PLC_QUALITY_TEST_SEQ", sequenceName="PLC_QUALITY_TEST_SEQ", allocationSize=1) 
	private Integer id;

	@Column(name="ZIG_NAME")
	@ColumnInfo(name="zigName", descr="zigName")
	private String zigName;
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="zig_id")
	private List<PlcQualityTestDetail> plcQualityTestDetails = new ArrayList<PlcQualityTestDetail>();
	
	@Column(name="COMPLETE_DATE", length=14)
	@ColumnInfo(descr="테스트 완료 날짜(yyyymmddhhMMss)")
	private String completeDate;
	
	@Column(name="TOTAL_COUNT")
	@ColumnInfo(descr="테스트 총 대수")
	private Integer totalCount;
	
	@Column(name="SUCCESS_COUNT")
	@ColumnInfo(descr="테스트 성공 대수")
	private Integer successCount;	
	
	private Boolean testEnable;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}	
	public String getZigName() {
		return zigName;
	}
	public void setZigName(String zigName) {
		this.zigName = zigName;
	}
	@XmlTransient
	public List<PlcQualityTestDetail> getPlcQualityTestDetails() {
		return plcQualityTestDetails;
	}
	public void setContracts(List<PlcQualityTestDetail> plcQualityTestDetails) {
		this.plcQualityTestDetails = plcQualityTestDetails;
	}
	public String getCompleteDate() {
		return completeDate;
	}

	public void setCompleteDate(String completeDate) {
		this.completeDate = completeDate;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Integer successCount) {
		this.successCount = successCount;
	}

	public Boolean getTestEnable() {
		return testEnable;
	}
	public void setTestEnable(Boolean testEnable) {
		this.testEnable = testEnable;
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
