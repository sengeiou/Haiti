package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BasePk;

@Embeddable
public class LpExPk extends BasePk {
	@Column(name="MDEV_ID",length=20,nullable=false)
	protected String mdevId;
	
	@Column(name="channel", length=3, nullable=false)
    @ColumnInfo(name="채널")
	protected Integer channel;
	   
	@Column(name="yyyymmddhhmiss",length=14,nullable=false)
	protected String yyyymmddhhmiss;

	public String getMdevId() {
		return mdevId;
	}

	public void setMdevId(String mdevId) {
		this.mdevId = mdevId;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public String getYyyymmddhhmiss() {
		return yyyymmddhhmiss;
	}

	public void setYyyymmddhhmiss(String yyyymmddhhmiss) {
		this.yyyymmddhhmiss = yyyymmddhhmiss;
	}	
	
}
