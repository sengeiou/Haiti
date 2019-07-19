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

/**
 * "Headend" manage more than one MCU(DCU)
 * MDIS를 위해 생성된 모델
 * 
 * Date          Version     Author   			Description
 * 2012. 08. 13  v1.0        김지애(jiae)         모델 생성
 *
 */

@Entity
@Table(name="MDIS_HEADEND")
public class Headend extends BaseObject {
	
	private static final long serialVersionUID = -7588707526107168442L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="HEADEND_SEQ")
	@SequenceGenerator(name="HEADEND_SEQ", sequenceName="HEADEND_SEQ", allocationSize=1) 
	@ColumnInfo(name="PK", descr="PK")
	private Integer id;
	
	@Column(name="timeout")
	@ColumnInfo(descr="Headend timeout(sec)")
	private Integer timeout;
	
	@Column(name="retry")
	@ColumnInfo(descr="Headend retry count")
	private Integer retry;
	
	@Column(name="write_date", length=14)
	@ColumnInfo(descr="Registed date(yyyyMMddhhmmss)")
	private String writeDate;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getTimeout() {
		return timeout;
	}
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	public Integer getRetry() {
		return retry;
	}
	public void setRetry(Integer retry) {
		this.retry = retry;
	}
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
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
