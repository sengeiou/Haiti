package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.model.BasePk;

/**
 * Billing Class의 Primary Key 정보를 정의한 Class
 * @author YeonKyoung Park(goodjob)
 *
 */
@Embeddable
public class BillingPk extends BasePk{

	private static final long serialVersionUID = 6421799583751475497L;

	@Column(name="mdev_type",length=20)
	@ColumnInfo(name="장비 아이디", descr="")
	private DeviceType mdevType;// MCU(0), Modem(1), Meter(2);
	
	@Column(name="mdev_id",length=20)
	@ColumnInfo(name="장비 아이디", descr="")
	private String mdevId;
	
	@Column(name="yyyymmdd",length=8,nullable=false)
	private String yyyymmdd;	
	
	@Column(length=6,nullable=false , columnDefinition="varchar(6) default '000000' " )
	@ColumnInfo(name="데이터 생성시간 ( Reset Time or  Read Time) ")
	private String hhmmss;
	
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
	
	public String getYyyymmdd() {
		return yyyymmdd;
	}
	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}

	public void setHhmmss(String hhmmss) {
		this.hhmmss = hhmmss;
	}

	public String getHhmmss() {
		return hhmmss;
	}	

}