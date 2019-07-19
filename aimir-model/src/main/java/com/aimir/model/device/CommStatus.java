package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.aimir.model.BaseObject;


/**
 * CommStatus - Communication 이력에 대한 통계 요약 정보
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="COMMSTATUS")
public class CommStatus extends BaseObject {

	private static final long serialVersionUID = 670721478784953689L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,  generator="COMMSTATUS_SEQ")
	@SequenceGenerator(name="COMMSTATUS_SEQ", sequenceName="COMMSTATUS_SEQ", allocationSize=1) 
	private Integer id;	//	ID(PK)

	@Column(name="YYYYMMDD")
	private String yyyymmdd;
	@Column(name="MTRRECEIVECOUNT")
	private Integer mtrReceiveCount;
	@Column(name="ONDEMANDCOUNT")
	private Integer onDemandCount;
	@Column(name="MTRSAVECOUNT")
	private Integer mtrSaveCount;
	@Column(name="EVENTRCVCOUNT")
	private Integer eventRcvCount;
	@Column(name="SUPPLIERID")
	private String supplierId;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getYyyymmdd() {
		return yyyymmdd;
	}
	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
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
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
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