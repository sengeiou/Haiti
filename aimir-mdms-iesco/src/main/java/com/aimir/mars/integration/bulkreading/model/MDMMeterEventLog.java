package com.aimir.mars.integration.bulkreading.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

import net.sf.json.JSONString;

@Entity
@Table(name="MDM_METEREVENT_LOG")
@IdClass(MDMMeterEventLogPK.class)
public class MDMMeterEventLog extends BaseObject implements JSONString {
	
	@Id
	@Column(name="ACTIVATOR_ID",length=100)
	@ColumnInfo(name="ACTIVATOR_ID")	
	private String activator_id;
	
	@Id
	@Column(name="METEREVENT_ID",length=100)
	@ColumnInfo(name="METEREVENT_ID")	
	private String meterevent_id;
	
	@Id
	@Column(name="OPEN_TIME",length=14)
	@ColumnInfo(name="OPEN_TIME")	
	private String open_time;
		
	@Column(name="MESSAGE",length=255)
	@ColumnInfo(name="MESSAGE")	
	private String message;
	
	@Id
	@Column(name="YYYYMMDD",length=8)
	@ColumnInfo(name="YYYYMMDD")	
	private String yyyymmdd;
	
	@Column(name="BATCH_ID")
	@ColumnInfo(name="BATCH_ID")	
	private Integer batchId;
	
	@Column(name="TRANSFER_DATETIME",length=14)
	@ColumnInfo(name="TRANSFER_DATETIME")	
	private String transferDatetime;
	
	@Column(name="INSERT_DATETIME",length=14)
	@ColumnInfo(name="INSERT_DATETIME")	
	private String insertDatetime;

	public String getActivator_id() {
		return activator_id;
	}

	public void setActivator_id(String activator_id) {
		this.activator_id = activator_id;
	}

	public String getMeterevent_id() {
		return meterevent_id;
	}

	public void setMeterevent_id(String meterevent_id) {
		this.meterevent_id = meterevent_id;
	}

	public String getOpen_time() {
		return open_time;
	}

	public void setOpen_time(String open_time) {
		this.open_time = open_time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getYyyymmdd() {
		return yyyymmdd;
	}

	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
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