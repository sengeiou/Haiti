package com.aimir.model.view;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.DeviceType;

@Embeddable
public class MonthViewPk {
	@Column(name="mdev_type",length=20)
	@Enumerated(EnumType.STRING)
	@ColumnInfo(name="장비 아이디", descr="")
	private DeviceType mdevType;// MCU(0), Modem(1), Meter(2);
	 
	@Column(name="mdev_id",length=20)
	@ColumnInfo(name="장비 아이디", descr="")
	private String mdevId;
	
	@Column(columnDefinition="INTEGER default 0", length=2)
	@ColumnInfo(name="DST", descr="Summer Time ex ) +1 -1 +0")
	private Integer dst;
	
	@Column(name="yyyymm",length=6,nullable=false)
	private String yyyymm;	
	
    @ColumnInfo(name="채널")
    private Integer channel;

	public DeviceType getMdevType() {
		return mdevType;
	}

	public void setMdevType(DeviceType mdevType) {
		this.mdevType = mdevType;
	}

	public String getMdevId() {
		return mdevId;
	}

	public void setMdevId(String mdevId) {
		this.mdevId = mdevId;
	}

	public Integer getDst() {
		return dst;
	}

	public void setDst(Integer dst) {
		this.dst = dst;
	}

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
