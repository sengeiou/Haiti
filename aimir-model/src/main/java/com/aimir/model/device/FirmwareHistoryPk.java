package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.model.BasePk;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>FirmwareHistory Class Entity Primary Key Define class</p>
 * 
 * @author goodjob
 *
 */
@Embeddable
public class FirmwareHistoryPk extends BasePk {

	private static final long serialVersionUID = 8574062243135610499L;

	@Column(name="TR_ID",length=16,nullable=false)
	private Long trId;	

/*	@Column(name="DEVICE_ID", length=30, nullable=false)
	private String deviceId;	*/	

    @Column(name="ISSUE_DATE",length=14, nullable=false)
    private String issueDate;
        
    @Column(name="IN_SEQ",length=10, nullable=false)
    private String inSeq;

    public Long getTrId() {
        return trId;
    }

	public void setTrId(Long trId) {
        this.trId = trId;
    }

/*    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }  */  

	public String getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
	
    public String getInSeq() {
		return inSeq;
	}

	public void setInSeq(String inSeq) {
		this.inSeq = inSeq;
	}
}