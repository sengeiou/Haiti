package com.aimir.mars.integration.bulkreading.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;
import com.aimir.model.device.Meter;

import net.sf.json.JSONString;

@Entity
@Table(name="MDM_LP_EM")
@IdClass(MDMLpEMPK.class)
public class MDMLpEM extends BaseObject implements JSONString {
	
	@Id
	@Column(name="MDEV_ID",length=20)
	@ColumnInfo(name="MDEV_ID")	
	private String mdevId;
	
	@Id
	@Column(name="YYYYMMDDHHMMSS",length=14)
	@ColumnInfo(name="YYYYMMDDHHMMSS")	
	private String yyyymmddhhmmss;
	
	@Id
	@Column(name="MDEV_TYPE",length=20)
	@ColumnInfo(name="MDEV_TYPE")	
	private String mdevType;
	
	@Id
	@Column(name="DST",length=38)
	@ColumnInfo(name="DST")	
	private Integer dst;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "meter_id")
	@ColumnInfo(name = "미터")
	@ReferencedBy(name = "mdsId")
	private Meter meter;

	@Column(name = "meter_id", nullable = true, updatable = false, insertable = false)
	private Integer meterId;
	
	@Column(name="INSTALL_PROPERTY",length=10)
	@ColumnInfo(name="INSTALL_PROPERTY")	
	private String installProperty;
	
	@Column(name="CH1")
	@ColumnInfo(name="CH1")	
	private Double ch1;
	
	@Column(name="CH2")
	@ColumnInfo(name="CH2")	
	private Double ch2;
	
	@Column(name="CH3")
	@ColumnInfo(name="CH3")	
	private Double ch3;
	
	@Column(name="CH4")
	@ColumnInfo(name="CH4")	
	private Double ch4;
	
	@Column(name="CH5")
	@ColumnInfo(name="CH5")	
	private Double ch5;
	
	@Column(name="CH6")
	@ColumnInfo(name="CH6")	
	private Double ch6;
	
	@Column(name="CH7")
	@ColumnInfo(name="CH7")	
	private Double ch7;
	
	@Column(name="CH8")
	@ColumnInfo(name="CH8")	
	private Double ch8;
	
	@Column(name="CH9")
	@ColumnInfo(name="CH9")	
	private Double ch9;
	
	@Column(name="CH10")
	@ColumnInfo(name="CH10")	
	private Double ch10;
	
	@Column(name="CH11")
	@ColumnInfo(name="CH11")	
	private Double ch11;
	
	@Column(name="CH12")
	@ColumnInfo(name="CH12")	
	private Double ch12;
	
	@Column(name="CH13")
	@ColumnInfo(name="CH13")	
	private Double ch13;
	
	@Column(name="CH14")
	@ColumnInfo(name="CH14")	
	private Double ch14;
	
	@Column(name="CH15")
	@ColumnInfo(name="CH15")	
	private Double ch15;
	
	@Id
	@Column(name="WRITEDATE",length=14)
	@ColumnInfo(name="WRITEDATE")	
	private String writedate;
	
	@Column(name="BATCH_ID",length=10)
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

	public String getYyyymmddhhmmss() {
		return yyyymmddhhmmss;
	}

	public void setYyyymmddhhmmss(String yyyymmddhhmmss) {
		this.yyyymmddhhmmss = yyyymmddhhmmss;
	}

	public String getMdevType() {
		return mdevType;
	}

	public void setMdevType(String mdevType) {
		this.mdevType = mdevType;
	}

	public Integer getDst() {
		return dst;
	}

	public void setDst(Integer dst) {
		this.dst = dst;
	}
	
	@XmlTransient
	public Meter getMeter() {
		return meter;
	}

	public void setMeter(Meter meter) {
		this.meter = meter;
	}

	public String getInstallProperty() {
		return installProperty;
	}

	public void setInstallProperty(String installProperty) {
		this.installProperty = installProperty;
	}

	public Double getCh1() {
		return ch1;
	}

	public void setCh1(Double ch1) {
		this.ch1 = ch1;
	}

	public Double getCh2() {
		return ch2;
	}

	public void setCh2(Double ch2) {
		this.ch2 = ch2;
	}

	public Double getCh3() {
		return ch3;
	}

	public void setCh3(Double ch3) {
		this.ch3 = ch3;
	}

	public Double getCh4() {
		return ch4;
	}

	public void setCh4(Double ch4) {
		this.ch4 = ch4;
	}

	public Double getCh5() {
		return ch5;
	}

	public void setCh5(Double ch5) {
		this.ch5 = ch5;
	}

	public Double getCh6() {
		return ch6;
	}

	public void setCh6(Double ch6) {
		this.ch6 = ch6;
	}

	public Double getCh7() {
		return ch7;
	}

	public void setCh7(Double ch7) {
		this.ch7 = ch7;
	}

	public Double getCh8() {
		return ch8;
	}

	public void setCh8(Double ch8) {
		this.ch8 = ch8;
	}

	public Double getCh9() {
		return ch9;
	}

	public void setCh9(Double ch9) {
		this.ch9 = ch9;
	}

	public Double getCh10() {
		return ch10;
	}

	public void setCh10(Double ch10) {
		this.ch10 = ch10;
	}

	public Double getCh11() {
		return ch11;
	}

	public void setCh11(Double ch11) {
		this.ch11 = ch11;
	}

	public Double getCh12() {
		return ch12;
	}

	public void setCh12(Double ch12) {
		this.ch12 = ch12;
	}

	public Double getCh13() {
		return ch13;
	}

	public void setCh13(Double ch13) {
		this.ch13 = ch13;
	}

	public Double getCh14() {
		return ch14;
	}

	public void setCh14(Double ch14) {
		this.ch14 = ch14;
	}

	public Double getCh15() {
		return ch15;
	}

	public void setCh15(Double ch15) {
		this.ch15 = ch15;
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
