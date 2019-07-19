package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BasePk;


/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>MeterEventLog Class의 Primary Key 정보를 정의한  클래스</p>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Embeddable
public class MeterEventLogPk extends BasePk {

	private static final long serialVersionUID = 9029977530554948477L;
	
    @Column(name="METEREVENT_ID", nullable=false, length=100)
    @ColumnInfo(name="미터이벤트코드", descr="이벤트의 아이디")
    private String meterEventId;
	
    @Column(name="OPEN_TIME",length=14, nullable=false)
    @ColumnInfo(name="발생시간", descr="YYYYMMDDHHMMSS")
    private String openTime;
    
    @Column(name="activator_id", length=100, nullable=false)
    @ColumnInfo(name="발생ID", descr="발생 대상의 ID")
    private String activatorId;

	public String getMeterEventId() {
		return meterEventId;
	}

	public void setMeterEventId(String meterEventId) {
		this.meterEventId = meterEventId;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getActivatorId() {
		return activatorId;
	}

	public void setActivatorId(String activatorId) {
		this.activatorId = activatorId;
	}
}