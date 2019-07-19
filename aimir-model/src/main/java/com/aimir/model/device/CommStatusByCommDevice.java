package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.aimir.model.BaseObject;

/**
 * CommStatus - Communication Status By Top Communication Device(DCU, Modem)
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="COMMSTATUS_BY_COMMDEVICE")
public class CommStatusByCommDevice extends BaseObject {

	private static final long serialVersionUID = -2416817150930573344L;
	
	@EmbeddedId
	public CommStatusByCommDevicePk id;
	
	@Column(name="YYYYMMDD")
	private String yyyymmdd;
	
	@Column(name="INFORECEIVECOUNT")
	private Integer infoReceiveCount;
	
	@Column(name="ONDEMANDCOUNT")
	private Integer onDemandCount;	

	@Column(name="MTRRECEIVECOUNT")
	private Integer mtrReceiveCount;
	
	@Column(name="MTRSAVECOUNT")
	private Integer mtrSaveCount;	
	
	@Column(name="EVENTRCVCOUNT")
	private Integer eventRcvCount;

	public CommStatusByCommDevicePk getId() {
		return id;
	}
	public void setId(CommStatusByCommDevicePk id) {
		this.id = id;
	}

	public String getYyyymmdd() {
		return yyyymmdd;
	}
	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}
	public Integer getInfoReceiveCount() {
		return infoReceiveCount;
	}
	public void setInfoReceiveCount(Integer infoReceiveCount) {
		this.infoReceiveCount = infoReceiveCount;
	}
	public Integer getMtrReceiveCount() {
		return mtrReceiveCount;
	}
	public void setMtrReceiveCount(Integer mtrReceiveCount) {
		this.mtrReceiveCount = mtrReceiveCount;
	}
	public Integer getOnDemandCount() {
		return onDemandCount;
	}
	public void setOnDemandCount(Integer onDemandCount) {
		this.onDemandCount = onDemandCount;
	}
	public Integer getMtrSaveCount() {
		return mtrSaveCount;
	}
	public void setMtrSaveCount(Integer mtrSaveCount) {
		this.mtrSaveCount = mtrSaveCount;
	}
	public Integer getEventRcvCount() {
		return eventRcvCount;
	}
	public void setEventRcvCount(Integer eventRcvCount) {
		this.eventRcvCount = eventRcvCount;
	}

    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }
}