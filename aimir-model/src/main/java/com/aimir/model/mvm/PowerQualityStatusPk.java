package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.model.BasePk;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * PowerQualityStatus 클래스의 Primary Key 정보를 담은 클래스
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Embeddable
public class PowerQualityStatusPk extends BasePk{

	private static final long serialVersionUID = 5876454013530685332L;

	@Column(name="mdev_type",length=20)
	@ColumnInfo(name="장비 아이디", descr="")
	private DeviceType mdevType;// MCU(0), Modem(1), Meter(2);
	
	@Column(name="mdev_id",length=20)
	@ColumnInfo(name="장비 아이디", descr="")
	private String mdevId;
	
	@Column(name="yyyymmddhhmmss",length=10,nullable=false)
	private String yyyymmddhhmmss;
	
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
	
	public String getYyyymmddhhmmss() {
		return yyyymmddhhmmss;
	}
	public void setYyyymmddhhmmss(String yyyymmddhhmmss) {
		this.yyyymmddhhmmss = yyyymmddhhmmss;
	}	
}