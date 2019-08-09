package com.aimir.model.mvm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.aimir.annotation.ColumnInfo;

import org.eclipse.persistence.annotations.Index;
/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <pre>
 * 대성 실시간 원격 검침
 * </pre>
 * 
 * @author 박종성
 */
@Deprecated
//@Entity
//@Table(name = "B1_ELC_AMR_CURR_INFO")
//@Index(name="IDX_B1_ELC_AMR_CURR_INFO_01", columnNames={"AR_ID"})
public class DaesungMeteringData {	

	@EmbeddedId public DaesungMeteringPk id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_CKM_DT")
	@ColumnInfo(name="검침일자")
	private Date yyyymmddhhmmss;
	
	@Column(name="GAUG_NO")
	@ColumnInfo(name="미터번호")
	private String mdsId;
	
	@Column(name="MXLD_NDL_VAL")
	@ColumnInfo(name="최대부하실지침")
	private Integer maxValue;
	
	@Column(name="MDLD_NDL_VAL")
	@ColumnInfo(name="중간부하실지침")
	private Integer midValue;
	
	@Column(name="MNLD_NDL_VAL")
	@ColumnInfo(name="경부하실지침")
	private Integer minValue;
	
	@Column(name="CKM_RSLT_CD", length=1)
	@ColumnInfo(name="검침결과코드")
	private String resultCd;
	
	@Column(name="MDLD_NDL_VAL_2")
	@ColumnInfo(name="중부하 무효진상지침")
	private Integer midReactiveLeadValue;
	
	@Column(name="MDLD_NDL_VAL_S")
	@ColumnInfo(name="중부하 송전지침")
	private Integer midImportValue;
	
	@Column(name="MDLD_NDL_VAL_R")
	@ColumnInfo(name="중부하 수전지침")
	private Integer midExportValue;

	public DaesungMeteringData() {
		this.id = new DaesungMeteringPk();
	}
	
	public DaesungMeteringPk getId() {
		return id;
	}

	public void setId(DaesungMeteringPk id) {
		this.id = id;
	}

	public void setContractNumber(String contractNumber) {
		this.id.setContractNumber(contractNumber);
	}
	
	public String getContractNumber() {
		return this.id.getContractNumber();
	}
	
	public void setWdvFlag(String wdvFlag) {
		this.id.setWdvFlag(wdvFlag);
	}
	
	public String getWdvFlag() {
		return this.id.getWdvFlag();
	}
	
	public Date getYyyymmddhhmmss() {
		return yyyymmddhhmmss;
	}

	public void setYyyymmddhhmmss(Date yyyymmddhhmmss) {
		this.yyyymmddhhmmss = yyyymmddhhmmss;
	}

	public String getMdsId() {
		return mdsId;
	}

	public void setMdsId(String mdsId) {
		this.mdsId = mdsId;
	}

	public Integer getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Integer maxValue) {
		this.maxValue = maxValue;
	}

	public Integer getMidValue() {
		return midValue;
	}

	public void setMidValue(Integer midValue) {
		this.midValue = midValue;
	}

	public Integer getMinValue() {
		return minValue;
	}

	public void setMinValue(Integer minValue) {
		this.minValue = minValue;
	}

	public String getResultCd() {
		return resultCd;
	}

	public void setResultCd(String resultCd) {
		this.resultCd = resultCd;
	}

	public Integer getMidReactiveLeadValue() {
		return midReactiveLeadValue;
	}

	public void setMidReactiveLeadValue(Integer midReactiveLeadValue) {
		this.midReactiveLeadValue = midReactiveLeadValue;
	}

	public Integer getMidImportValue() {
		return midImportValue;
	}

	public void setMidImportValue(Integer midImportValue) {
		this.midImportValue = midImportValue;
	}

	public Integer getMidExportValue() {
		return midExportValue;
	}

	public void setMidExportValue(Integer midExportValue) {
		this.midExportValue = midExportValue;
	}
	
}
