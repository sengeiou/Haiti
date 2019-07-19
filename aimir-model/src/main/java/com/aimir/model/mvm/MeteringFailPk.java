package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.model.BasePk;


/**
 * MeteringFail Class Entity의 주키 Primary Key를 표현하는 클래스
 * @author YeonKyoung Park(goodjob)
 *
 */
@Embeddable
public class MeteringFailPk extends BasePk {

	private static final long serialVersionUID = -5256059098680936071L;

	@Column(name="YYYYMMDD", length=8, nullable=false)
	private String yyyymmdd;
	
    @Column(name="mdev_type",length=20)
	@ColumnInfo(name="장비 아이디", descr="")
	private DeviceType mdevType;// MCU(0), Modem(1), Meter(2);
	
	@Column(name="mdev_id",length=20)
	@ColumnInfo(name="장비 아이디", descr="")
	private String mdevId;	

	public String getYyyymmdd() {
		return yyyymmdd;
	}
	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}
	
	public DeviceType getMDevType() {
		return mdevType;
	}
		
	public void setMDevType(String mdevType) {	
        this.mdevType = DeviceType.valueOf(mdevType);
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

}