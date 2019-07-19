package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.model.BasePk;


/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * FirmwareIssue 클래스의 Primary Key 정보를 정의한 클래스
 * 
 * @author Elevas Park(elevas)
 *
 */
@Embeddable
public class FirmwareIssuePk extends BasePk {

	private static final long serialVersionUID = 3801120852112519366L;
	
	@Column(name="firmwareId",length=16,nullable=false)
	private Long firmwareId;	

	@Column(name="issueDate", length=14, nullable=false)
	private String issueDate;
	
	@Column(name="locationId", length=16, nullable=true)
	private Integer locationId;

    public Long getFirmwareId() {
        return firmwareId;
    }

    public void setFirmwareId(Long firmwareId) {
        this.firmwareId = firmwareId;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

}