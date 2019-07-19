package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.model.BasePk;

/**
 * Metering Data Class의 Primary key가 되는 정보를 정의한 클래스
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@MappedSuperclass
public abstract class MeteringPk extends BasePk{

    private static final long serialVersionUID = 3683526938894043858L;

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
	
	public DeviceType getMDevType() {
		return mdevType;
	}

	public void setMDevType(String mdevType){
		this.mdevType = DeviceType.valueOf(mdevType);
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