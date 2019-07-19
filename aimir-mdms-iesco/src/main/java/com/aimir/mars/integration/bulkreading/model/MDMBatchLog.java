package com.aimir.mars.integration.bulkreading.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

import net.sf.json.JSONString;

@Entity
@Table(name="MDM_BATCH_LOG")
public class MDMBatchLog  extends BaseObject implements JSONString {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 2658401425319511460L;
	
	@Id
	@Column(name="BATCH_ID")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_MDM_BATCH")
    @SequenceGenerator(name="SEQ_MDM_BATCH", sequenceName="SEQ_MDM_BATCH", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
	private Integer batch_id;
	
	@Column(name="BATCH_TYPE",length=20)
	@ColumnInfo(name="BATCH TYPE", descr="METER, BILL_DAY, BILL_MONTH, LP_EM")	
	private String batchType;
	
	@Column(name="BATCH_STATUS",length=1)
	@ColumnInfo(name="BATCH_STATUS", descr="1-READY/2-COMPLETE")	
	private String batchStatus;
	
	@Column(name="BATCH_CNT",length=10)
	@ColumnInfo(name="BATCH_CNT")	
	private Integer batchCnt;
	
	@Column(name="BATCH_DATETIME", length=14, nullable=false)
	@ColumnInfo(name="서버저장시간", descr="YYYYMMDDHHMMSS")
	private String batchDatetime;	
	
	public Integer getBatch_id() {
		return batch_id;
	}

	public void setBatch_id(Integer batch_id) {
		this.batch_id = batch_id;
	}

	public String getBatchType() {
		return batchType;
	}

	public void setBatchType(String batchType) {
		this.batchType = batchType;
	}

	public String getBatchStatus() {
		return batchStatus;
	}

	public void setBatchStatus(String batchStatus) {
		this.batchStatus = batchStatus;
	}

	public Integer getBatchCnt() {
		return batchCnt;
	}

	public void setBatchCnt(Integer batchCnt) {
		this.batchCnt = batchCnt;
	}

	public String getBatchDatetime() {
		return batchDatetime;
	}

	public void setBatchDatetime(String batchDatetime) {
		this.batchDatetime = batchDatetime;
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
