package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * MeteringDataXX Class의 Primary Key를 정의한 클래스
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Embeddable
public class MeteringDataPk extends MeteringPk{

	private static final long serialVersionUID = -2326981916512828373L;
	
	@Column(name="yyyymmddhhmmss",length=14,nullable=false)
	private String yyyymmddhhmmss;	
	
	public String getYyyymmddhhmmss() {
		return yyyymmddhhmmss;
	}
	public void setYyyymmddhhmmss(String yyyymmddhhmmss) {
		this.yyyymmddhhmmss = yyyymmddhhmmss;
	}	
}