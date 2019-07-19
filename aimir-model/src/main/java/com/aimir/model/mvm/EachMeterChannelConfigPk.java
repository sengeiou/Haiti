package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.model.BasePk;

/**
 * LoadProfile 정의한 클래스의 Primary Key를 정의한 클래스
 *  
 * @author YeonKyoung Park(goodjob)
 *
 */
@Embeddable
public class EachMeterChannelConfigPk extends BasePk{

	private static final long serialVersionUID = 1399341356127473331L;
	
	@Column(name="mdev_type",length=20)
	@ColumnInfo(name="장비 아이디", descr="")
	private DeviceType mdevType;// MCU(0), Modem(1), Meter(2), EndDevice(3)
	
	@Column(name="mdev_id",length=20)
	@ColumnInfo(name="장비 아이디", descr="")
	private String mdevId;

	@Column(name="channel_index", nullable=false)
	@ColumnInfo(descr="채널 저장 및 표시하는 순서")
	private Integer channelIndex;
	   
	public DeviceType getMDevType() {
		return mdevType;
	}
		
	public void setMDevType(String mdevType) {	
		this.mdevType = DeviceType.valueOf(mdevType);
	}
	
	public String getMDevId() {
		return mdevId;
	}
	public void setMDevId(String mdevId) {
		this.mdevId = mdevId;
	}
	
	public Integer getChannelIndex() {
		return channelIndex;
	}

	public void setChannelIndex(Integer channelIndex) {
		this.channelIndex = channelIndex;
	}
}