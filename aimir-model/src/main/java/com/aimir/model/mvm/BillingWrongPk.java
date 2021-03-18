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
public class BillingWrongPk extends BasePk{

	private static final long serialVersionUID = 6421799583751475497L;
	
	@Column(name="code",length=20)
	@ColumnInfo(name="Erroro Code, ECGBillingBlockTariffTask.java에 명시되어 있음")
    private String code;
	
	@Column(name="mdev_id",length=20)
	@ColumnInfo(name="장비 아이디", descr="")
	private String mdevId;
	
	@Column(name="yyyymmdd",length=8,nullable=false)
	private String yyyymmdd;	
	
	
	public String getCode() {
		return code;
	}
	
	public void setCode(String code) {
		this.code = code;
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

}