package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.annotation.ColumnInfo;

/**
 * DayXX Class의 Primary Key 를 정의한 클래스
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Embeddable
public class DayPk extends MeteringPk{

	private static final long serialVersionUID = 3801120852112519366L;
	
	@Column(name="channel", length=3,nullable=false)
    @ColumnInfo(name="채널")
    private Integer channel;
	   
	@Column(name="yyyymmdd",length=8,nullable=false)
	private String yyyymmdd;
	
	@Column(name="hh", length=2,nullable=false)
	@ColumnInfo(name="검침시간")
	private String hh;
	
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
	public String getHh() {
		return hh;
	}
	public void setHh(String hh) {
		this.hh = hh;
	}
	
}