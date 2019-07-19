package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MeteringDataTHUPk extends MeteringTHUPk{

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