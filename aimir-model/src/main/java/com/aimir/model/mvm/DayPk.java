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