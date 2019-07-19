package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BasePk;

/**
 * HeadendCtrl Class의 Primary Key 정보를 정의한 Class
 * 
 * Date          Version     Author   Description
 * 2012. 08. 13  v1.0        김지애(jiae)         모델 생성
 *
 */

@Embeddable
public class HeadendPK extends BasePk {
	
	private static final long serialVersionUID = 1L;

	@Column(name="CtrlID",length=8, nullable=false)
	@ColumnInfo(descr="Headend Control ID('ST' : Setting)")
	private String ctrlId;
	
	@Column(name="WRITE_DATE",length=14, nullable=false)
	@ColumnInfo(descr="Registed date(yyyyMMddhhmmss)")
	private String writeDate;
	
	public String getCtrlId() {
		return ctrlId;
	}
	public void setCtrlId(String ctrlId) {
		this.ctrlId = ctrlId;
	}
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}
	
	
}