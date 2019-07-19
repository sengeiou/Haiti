package com.aimir.mars.integration.bulkreading.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.model.BaseObject;


import net.sf.json.JSONString;

@Entity
@Table(name="MDM_BILLING_DAY_EM")
@IdClass(MDMBillingDayEMPK.class)
public class MDMBillingDayEM extends BaseObject implements JSONString {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="MDEV_ID",length=20)
	@ColumnInfo(name="MDEV_ID")	
	private String mdevId;
	
	@Id
	@Column(name="MDEV_TYPE",length=20)
	@ColumnInfo(name="MDEV_TYPE")	
	private DeviceType mdevType;
	
	@Id
	@Column(name="YYYYMMDD",length=8)
	@ColumnInfo(name="YYYYMMDD")	
	private String yyyymmdd;
	
	@Id
	@Column(name="HHMMSS",length=6)
	@ColumnInfo(name="HHMMSS")	
	private String hhmmss;
	
	@Column(name="CUMULACTIVEENGYIMPORT")
	@ColumnInfo(name="CUMULACTIVEENGYIMPORT")	
	private Double cumulativeActiveEnergyImport;
	
	@Column(name="CUMULACTIVEENGYIMPORTRATE1")
	@ColumnInfo(name="CUMULACTIVEENGYIMPORTRATE1")	
	private Double cumulativeActiveEnergyImportRate1;
		
	@Column(name="CUMULACTIVEENGYIMPORTRATE2")
	@ColumnInfo(name="CUMULACTIVEENGYIMPORTRATE2")	
	private Double cumulativeActiveEnergyImportRate2;
	
	@Column(name="CUMULACTIVEENGYIMPORTRATE3")
	@ColumnInfo(name="CUMULACTIVEENGYIMPORTRATE3")	
	private Double cumulativeActiveEnergyImportRate3;
	
	@Column(name="CUMULACTIVEENGYIMPORTRATE4")
	@ColumnInfo(name="CUMULACTIVEENGYIMPORTRATE4")	
	private Double cumulativeActiveEnergyImportRate4;
	
	@Column(name="CUMULACTIVEENGYIMPORTRATE5")
	@ColumnInfo(name="CUMULACTIVEENGYIMPORTRATE5")	
	private Double cumulativeActiveEnergyImportRate5;
	
	@Column(name="CUMULACTIVEENGYIMPORTRATE6")
	@ColumnInfo(name="CUMULACTIVEENGYIMPORTRATE6")	
	private Double cumulativeActiveEnergyImportRate6;
	
	@Column(name="CUMULACTIVEENGYIMPORTRATE7")
	@ColumnInfo(name="CUMULACTIVEENGYIMPORTRATE7")	
	private Double cumulativeActiveEnergyImportRate7;
	
	@Column(name="CUMULACTIVEENGYIMPORTRATE8")
	@ColumnInfo(name="CUMULACTIVEENGYIMPORTRATE8")	
	private Double cumulativeActiveEnergyImportRate8;
		
	@Column(name="CUMULREACTIVEENGYIMPORT")
	@ColumnInfo(name="CUMULREACTIVEENGYIMPORT")	
	private Double cumulativeReactiveEnergyImport;
	
	@Column(name="CUMULREACTIVEENGYIMPORTRATE1")
	@ColumnInfo(name="CUMULREACTIVEENGYIMPORTRATE1")	
	private Double cumulativeReactiveEnergyImportRate1;
		
	@Column(name="CUMULREACTIVEENGYIMPORTRATE2")
	@ColumnInfo(name="CUMULREACTIVEENGYIMPORTRATE2")	
	private Double cumulativeReactiveEnergyImportRate2;
	
	@Column(name="CUMULREACTIVEENGYIMPORTRATE3")
	@ColumnInfo(name="CUMULREACTIVEENGYIMPORTRATE3")	
	private Double cumulativeReactiveEnergyImportRate3;
	
	@Column(name="CUMULREACTIVEENGYIMPORTRATE4")
	@ColumnInfo(name="CUMULREACTIVEENGYIMPORTRATE4")	
	private Double cumulativeReactiveEnergyImportRate4;
	
	@Column(name="CUMULREACTIVEENGYIMPORTRATE5")
	@ColumnInfo(name="CUMULREACTIVEENGYIMPORTRATE5")	
	private Double cumulativeReactiveEnergyImportRate5;
	
	@Column(name="CUMULREACTIVEENGYIMPORTRATE6")
	@ColumnInfo(name="CUMULREACTIVEENGYIMPORTRATE6")	
	private Double cumulativeReactiveEnergyImportRate6;
	
	@Column(name="CUMULREACTIVEENGYIMPORTRATE7")
	@ColumnInfo(name="CUMULREACTIVEENGYIMPORTRATE7")	
	private Double cumulativeReactiveEnergyImportRate7;
	
	@Column(name="CUMULREACTIVEENGYIMPORTRATE8")
	@ColumnInfo(name="CUMULREACTIVEENGYIMPORTRATE8")	
	private Double cumulativeReactiveEnergyImportRate8;
	
	@Column(name="WRITEDATE")
	@ColumnInfo(name="WRITEDATE")	
	private String writedate;

	@Column(name="BATCH_ID")
	@ColumnInfo(name="BATCH_ID")	
	private Integer batchId;
	
	@Column(name="TRANSFER_DATETIME",length=14)
	@ColumnInfo(name="TRANSFER_DATETIME")	
	private String transferDatetime;
	
	@Column(name="INSERT_DATETIME",length=14)
	@ColumnInfo(name="INSERT_DATETIME")	
	private String insertDatetime;
	
	public String getMdevId() {
		return mdevId;
	}

	public void setMdevId(String mdevId) {
		this.mdevId = mdevId;
	}

	public DeviceType getMdevType() {
		return mdevType;
	}

	public void setMdevType(DeviceType mdevType) {
		this.mdevType = mdevType;
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

	public Double getCumulativeActiveEnergyImport() {
		return cumulativeActiveEnergyImport;
	}

	public void setCumulativeActiveEnergyImport(Double cumulativeActiveEnergyImport) {
		this.cumulativeActiveEnergyImport = cumulativeActiveEnergyImport;
	}

	public Double getCumulativeActiveEnergyImportRate1() {
		return cumulativeActiveEnergyImportRate1;
	}

	public void setCumulativeActiveEnergyImportRate1(Double cumulativeActiveEnergyImportRate1) {
		this.cumulativeActiveEnergyImportRate1 = cumulativeActiveEnergyImportRate1;
	}

	public Double getCumulativeActiveEnergyImportRate2() {
		return cumulativeActiveEnergyImportRate2;
	}

	public void setCumulativeActiveEnergyImportRate2(Double cumulativeActiveEnergyImportRate2) {
		this.cumulativeActiveEnergyImportRate2 = cumulativeActiveEnergyImportRate2;
	}

	public Double getCumulativeActiveEnergyImportRate3() {
		return cumulativeActiveEnergyImportRate3;
	}

	public void setCumulativeActiveEnergyImportRate3(Double cumulativeActiveEnergyImportRate3) {
		this.cumulativeActiveEnergyImportRate3 = cumulativeActiveEnergyImportRate3;
	}

	public Double getCumulativeActiveEnergyImportRate4() {
		return cumulativeActiveEnergyImportRate4;
	}

	public void setCumulativeActiveEnergyImportRate4(Double cumulativeActiveEnergyImportRate4) {
		this.cumulativeActiveEnergyImportRate4 = cumulativeActiveEnergyImportRate4;
	}

	public Double getCumulativeActiveEnergyImportRate5() {
		return cumulativeActiveEnergyImportRate5;
	}

	public void setCumulativeActiveEnergyImportRate5(Double cumulativeActiveEnergyImportRate5) {
		this.cumulativeActiveEnergyImportRate5 = cumulativeActiveEnergyImportRate5;
	}

	public Double getCumulativeActiveEnergyImportRate6() {
		return cumulativeActiveEnergyImportRate6;
	}

	public void setCumulativeActiveEnergyImportRate6(Double cumulativeActiveEnergyImportRate6) {
		this.cumulativeActiveEnergyImportRate6 = cumulativeActiveEnergyImportRate6;
	}

	public Double getCumulativeActiveEnergyImportRate7() {
		return cumulativeActiveEnergyImportRate7;
	}

	public void setCumulativeActiveEnergyImportRate7(Double cumulativeActiveEnergyImportRate7) {
		this.cumulativeActiveEnergyImportRate7 = cumulativeActiveEnergyImportRate7;
	}

	public Double getCumulativeActiveEnergyImportRate8() {
		return cumulativeActiveEnergyImportRate8;
	}

	public void setCumulativeActiveEnergyImportRate8(Double cumulativeActiveEnergyImportRate8) {
		this.cumulativeActiveEnergyImportRate8 = cumulativeActiveEnergyImportRate8;
	}

	public Double getCumulativeReactiveEnergyImport() {
		return cumulativeReactiveEnergyImport;
	}

	public void setCumulativeReactiveEnergyImport(Double cumulativeReactiveEnergyImport) {
		this.cumulativeReactiveEnergyImport = cumulativeReactiveEnergyImport;
	}

	public Double getCumulativeReactiveEnergyImportRate1() {
		return cumulativeReactiveEnergyImportRate1;
	}

	public void setCumulativeReactiveEnergyImportRate1(Double cumulativeReactiveEnergyImportRate1) {
		this.cumulativeReactiveEnergyImportRate1 = cumulativeReactiveEnergyImportRate1;
	}

	public Double getCumulativeReactiveEnergyImportRate2() {
		return cumulativeReactiveEnergyImportRate2;
	}

	public void setCumulativeReactiveEnergyImportRate2(Double cumulativeReactiveEnergyImportRate2) {
		this.cumulativeReactiveEnergyImportRate2 = cumulativeReactiveEnergyImportRate2;
	}

	public Double getCumulativeReactiveEnergyImportRate3() {
		return cumulativeReactiveEnergyImportRate3;
	}

	public void setCumulativeReactiveEnergyImportRate3(Double cumulativeReactiveEnergyImportRate3) {
		this.cumulativeReactiveEnergyImportRate3 = cumulativeReactiveEnergyImportRate3;
	}

	public Double getCumulativeReactiveEnergyImportRate4() {
		return cumulativeReactiveEnergyImportRate4;
	}

	public void setCumulativeReactiveEnergyImportRate4(Double cumulativeReactiveEnergyImportRate4) {
		this.cumulativeReactiveEnergyImportRate4 = cumulativeReactiveEnergyImportRate4;
	}

	public Double getCumulativeReactiveEnergyImportRate5() {
		return cumulativeReactiveEnergyImportRate5;
	}

	public void setCumulativeReactiveEnergyImportRate5(Double cumulativeReactiveEnergyImportRate5) {
		this.cumulativeReactiveEnergyImportRate5 = cumulativeReactiveEnergyImportRate5;
	}

	public Double getCumulativeReactiveEnergyImportRate6() {
		return cumulativeReactiveEnergyImportRate6;
	}

	public void setCumulativeReactiveEnergyImportRate6(Double cumulativeReactiveEnergyImportRate6) {
		this.cumulativeReactiveEnergyImportRate6 = cumulativeReactiveEnergyImportRate6;
	}

	public Double getCumulativeReactiveEnergyImportRate7() {
		return cumulativeReactiveEnergyImportRate7;
	}

	public void setCumulativeReactiveEnergyImportRate7(Double cumulativeReactiveEnergyImportRate7) {
		this.cumulativeReactiveEnergyImportRate7 = cumulativeReactiveEnergyImportRate7;
	}

	public Double getCumulativeReactiveEnergyImportRate8() {
		return cumulativeReactiveEnergyImportRate8;
	}

	public void setCumulativeReactiveEnergyImportRate8(Double cumulativeReactiveEnergyImportRate8) {
		this.cumulativeReactiveEnergyImportRate8 = cumulativeReactiveEnergyImportRate8;
	}
	
	public String getWritedate() {
		return writedate;
	}

	public void setWritedate(String writedate) {
		this.writedate = writedate;
	}

	public Integer getBatchId() {
		return batchId;
	}

	public void setBatchId(Integer batchId) {
		this.batchId = batchId;
	}

	public String getTransferDatetime() {
		return transferDatetime;
	}

	public void setTransferDatetime(String transferDatetime) {
		this.transferDatetime = transferDatetime;
	}

	public String getInsertDatetime() {
		return insertDatetime;
	}

	public void setInsertDatetime(String insertDatetime) {
		this.insertDatetime = insertDatetime;
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

}
