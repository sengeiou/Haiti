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
	
    @ColumnInfo(name="채널")
    private Integer channel;
	   
	@Column(name="yyyymmddhh",length=10,nullable=false)
	private String yyyymmddhh;	
	
	public String getYyyymmddhh() {
		return yyyymmddhh;
	}
	public void setYyyymmddhh(String yyyymmddhh) {
		this.yyyymmddhh = yyyymmddhh;
	}
    public Integer getChannel() {
        return channel;
    }
    public void setChannel(Integer channel) {
        this.channel = channel;
    }
}