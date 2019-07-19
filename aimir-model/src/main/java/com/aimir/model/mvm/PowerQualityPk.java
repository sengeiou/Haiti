package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.model.BasePk;


/**
 * PowerQuality class의 Primary Key정보를 가지는 클래스
 * @author YeonKyoung Park(goodjob)
 *
 */
@Embeddable
public class PowerQualityPk extends BasePk{

	private static final long serialVersionUID = 3623784179858757058L;
	
	@Column(name="mdev_type",length=20)
	@ColumnInfo(name="장비 아이디", descr="")
	private DeviceType mdevType;// MCU(0), Modem(1), Meter(2);
	
	@Column(name="mdev_id",length=20)
	@ColumnInfo(name="장비 아이디", descr="")
	private String mdevId;
	
	@Column(name="yyyymmddhhmm",length=12,nullable=false)
	private String yyyymmddhhmm;	
	
	@Column(columnDefinition="INTEGER default 0")
	@ColumnInfo(name="DST", descr="Summer Time ex ) +1 -1 +0")
	private Integer dst;
	
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
	
	public String getYyyymmddhhmm() {
		return yyyymmddhhmm;
	}
	public void setYyyymmddhhmm(String yyyymmddhhmm) {
		this.yyyymmddhhmm = yyyymmddhhmm;
	}	
	
	public Integer getDst() {
		return dst;
	}
	public void setDst(Integer dst) {
		this.dst = dst;
	}
}