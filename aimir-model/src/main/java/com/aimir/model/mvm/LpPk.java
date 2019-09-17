package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.annotation.ColumnInfo;

/**
 * LoadProfile 정의한 클래스의 Primary Key를 정의한 클래스
 *  
 * @author YeonKyoung Park(goodjob)
 *
 */
@Embeddable
public class LpPk extends MeteringPk{

	private static final long serialVersionUID = -5852154185942553378L;
	
	@Column(name="channel", length=3, nullable=false)
    @ColumnInfo(name="채널")
    private Integer channel;
	   
	@Column(name="yyyymmddhhmiss",length=14,nullable=false)
	private String yyyymmddhhmiss;	

    public String getYyyymmddhhmiss() {
		return yyyymmddhhmiss;
	}
	public void setYyyymmddhhmiss(String yyyymmddhhmiss) {
		this.yyyymmddhhmiss = yyyymmddhhmiss;
	}
	public Integer getChannel() {
        return channel;
    }
    public void setChannel(Integer channel) {
        this.channel = channel;
    }
    
	public String getYyyymmdd() {
		return yyyymmddhhmiss.substring(0, 8);
	}
		
	public String getYyyymmddhh() {
		return yyyymmddhhmiss.substring(0, 10);
	}
}