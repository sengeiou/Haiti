package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.model.BasePk;


@MappedSuperclass
public abstract class MeteringTHUPk extends BasePk{
	
	/**
     * 
     */
    private static final long serialVersionUID = 7859715304473693384L;

    @Column(name="mdev_type",length=20)
	@ColumnInfo(name="장비 아이디", descr="")
	private DeviceType mdevType;// MCU(0), Modem(1), Meter(2);
	
	@Column(name="mdev_id",length=20)
	@ColumnInfo(name="장비 아이디", descr="")
	private String mdevId;
	
	@Column(columnDefinition="INTEGER default 0")
	@ColumnInfo(name="DST", descr="Summer Time ex ) +1 -1 +0")
	private Integer dst;
		
	public DeviceType getMDevType() {
		return mdevType;
	}
		
	public void setMDevType(Integer mdevType) {	

		if(DeviceType.Modem.getCode().equals(mdevType)){
			this.mdevType = DeviceType.Modem;
		}
		if(DeviceType.Meter.getCode().equals(mdevType)){
			this.mdevType = DeviceType.Meter;
		}
		if(DeviceType.EndDevice.getCode().equals(mdevType)){
			this.mdevType = DeviceType.EndDevice;
		}
	}

	public void setMDevType(DeviceType mdevType) {	
		this.mdevType = mdevType;
	}

	public String getMDevId() {
		return mdevId;
	}
	public void setMDevId(String mdevId) {
		this.mdevId = mdevId;
	}
	
	public Integer getDst() {
		return dst;
	}
	public void setDst(Integer dst) {
		this.dst = dst;
	}	
	
}