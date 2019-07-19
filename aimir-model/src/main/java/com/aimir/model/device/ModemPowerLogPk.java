package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.model.BasePk;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>ModemPowerLog Class의 Primary Key정보를 정의한 클래스</p>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Embeddable
public class ModemPowerLogPk extends BasePk {

	private static final long serialVersionUID = -6537516512751335215L;

	@Column(name="device_type")
    @ColumnInfo(name="장비유형 ZEUPLS,ZBRepeater")
    @Enumerated(EnumType.STRING)
    private ModemType deviceType;
	
    @Column(name="device_id", length=20, nullable=false)
    @ColumnInfo(name="장비아이디, 비지니스 키가됨")
    private String deviceId;
    
	@Column(name="YYYYMMDD", length=8, nullable=false)
	private String yyyymmdd;
	
	@Column(name="HHMMSS", length=6, nullable=false)
	private String hhmmss;
	
	public ModemType getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = ModemType.valueOf(deviceType);
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getYyyymmdd() {
		return yyyymmdd;
	}
	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}
	public String getHhmmss() {
		return hhmmss;
	}
	public void setHhmmss(String hhmmss) {
		this.hhmmss = hhmmss;
	}
}