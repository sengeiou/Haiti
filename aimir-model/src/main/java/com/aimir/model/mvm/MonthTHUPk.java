package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.annotation.ColumnInfo;

@Embeddable
public class MonthTHUPk extends MeteringTHUPk{

	private static final long serialVersionUID = 7753103428595452543L;
	
	@ColumnInfo(name="채널")
    private Integer channel;
	
	@Column(name="yyyymm",length=6,nullable=false)
	private String yyyymm;	
	
	public String getYyyymm() {
		return yyyymm;
	}
	public void setYyyymm(String yyyymm) {
		this.yyyymm = yyyymm;
	}
    public Integer getChannel() {
        return channel;
    }
    public void setChannel(Integer channel) {
        this.channel = channel;
    }
	
}