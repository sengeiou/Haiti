package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.annotation.ColumnInfo;


/**
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Embeddable
public class DayTHUPk extends MeteringTHUPk{

	private static final long serialVersionUID = 3801120852112519366L;
	
	@ColumnInfo(name="채널")
    private Integer channel;
	
	@Column(name="yyyymmdd",length=8,nullable=false)
	private String yyyymmdd;	
	
	public String getYyyymmdd() {
		return yyyymmdd;
	}
	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}
    public Integer getChannel() {
        return channel;
    }
    public void setChannel(Integer channel) {
        this.channel = channel;
    }	
	
}