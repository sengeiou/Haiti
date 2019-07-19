package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.model.BasePk;


/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * AsyncCommandLog 클래스의 Primary Key 정보를 정의한 클래스
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Embeddable
public class CommStatusByCommDevicePk extends BasePk {

	private static final long serialVersionUID = 7338257448464393299L;
	@Column(name="comm_dev_id", length=20, nullable=false)
	private String commDevId;
	
	@Column(name="YYYYMMDDHHMMSS")
	private String yyyymmddhhmmss;

	public String getCommDevId() {
		return commDevId;
	}

	public void setCommDevId(String commDevId) {
		this.commDevId = commDevId;
	}

	public String getYyyymmddhhmmss() {
		return yyyymmddhhmmss;
	}

	public void setYyyymmddhhmm(String yyyymmddhhmmss) {
		this.yyyymmddhhmmss = yyyymmddhhmmss;
	} 

}