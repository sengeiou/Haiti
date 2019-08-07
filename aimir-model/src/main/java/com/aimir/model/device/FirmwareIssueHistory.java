package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;
import com.aimir.model.device.Device.DeviceType;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>Firmware Upgrade History</p>
 * 
 * @author Elevas Park
 *
 */
@Entity
@Table(name="FIRMWARE_ISSUE_HISTORY")
public class FirmwareIssueHistory extends BaseObject {

	private static final long serialVersionUID = -2516486451321651376L;

	@EmbeddedId public FirmwareIssueHistoryPk id;
	
	@Column(name="STEP", length=30)
    private String step;

    @Column(name="USE_BYPASS")
    @ColumnInfo(name="use Bypass. 1 = bypass, 0 = dcu")
    private Boolean uesBypass;
    
    @Column(name="RESULT_STATUS", length = 255)
    @ColumnInfo(name="Result Status of STEP")
    private String resultStatus;
	
    @Column(name="UPDATEDATE", length=14)
    private String updateDate;

    @XmlTransient
    @ManyToOne(fetch=FetchType.LAZY)    
    @JoinColumns({
        @JoinColumn(name="firmwareId", referencedColumnName="firmwareId", insertable=false, updatable=false),
        @JoinColumn(name="issueDate", referencedColumnName="issueDate", insertable=false, updatable=false),
        @JoinColumn(name="locationId", referencedColumnName="locationId", insertable=false, updatable=false)
        })
    private FirmwareIssue firmwareIssue;
    
    @Column(name="REQUESTID", length=30)
    private String requestId;
    
    @Column(name="DCU_ID", length=20)
    private String dcuId;
    
    public FirmwareIssueHistory() {
        id = new FirmwareIssueHistoryPk();
    }
    
	public FirmwareIssueHistoryPk getId() {
        return id;
    }

    public void setId(FirmwareIssueHistoryPk id) {
        this.id = id;
    }
    
    public Long getFirmwareId() {
        return this.id.getFirmwareId();
    }

    public void setFirmwareId(Long firmwareId) {
    	this.id.setFirmwareId(firmwareId);
    }

    public String getIssueDate() {
        return this.id.getIssueDate();
    }

    public void setIssueDate(String issueDate) {
        this.id.setIssueDate(issueDate);
    }

    public Integer getLocationId() {
        return this.id.getLocationId();
    }

    public void setLocationId(Integer locationId) {
        this.id.setLocationId(locationId);
    }

    public DeviceType getDeviceType() {
        return this.id.getDeviceType();
    }

    public void setDeviceType(DeviceType deviceType) {
        this.id.setDeviceType(deviceType);
    }

    public String getDeviceId() {
        return this.id.getDeviceId();
    }

    public void setDeviceId(String deviceId) {
        this.id.setDeviceId(deviceId);
    }
    
    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public FirmwareIssue getFirmwareIssue() {
        return firmwareIssue;
    }

    public void setFirmwareIssue(FirmwareIssue firmwareIssue) {
        this.firmwareIssue = firmwareIssue;
    }
    
	public Boolean getUesBypass() {
		return uesBypass;
	}

	public void setUesBypass(Boolean uesBypass) {
		this.uesBypass = uesBypass;
	}

	public String getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public String getDcuId() {
		return dcuId;
	}

	public void setDcuId(String dcuId) {
		this.dcuId = dcuId;
	}

	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        FirmwareIssueHistory other = (FirmwareIssueHistory) obj;
        if (id != other.id)
            return false;
        if (step == null) {
            if (other.step != null)
                return false;
        } else if (!step.equals(other.step))
            return false;        
        if (uesBypass != other.uesBypass){
            return false;
        }
        if (resultStatus == null) {
            if (other.resultStatus != null)
                return false;
        } else if (!resultStatus.equals(other.resultStatus)){
            return false; 
        }
        if (updateDate == null) {
            if (other.updateDate != null)
                return false;
        } else if (!updateDate.equals(other.updateDate))
            return false;
		if (requestId == null) {
			if (other.requestId != null)
				return false;
		} else if (!requestId.equals(other.requestId))
			return false;
		if (dcuId == null) {
			if (other.dcuId != null)
				return false;
		} else if (!dcuId.equals(other.dcuId))
			return false;
		
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((step == null) ? 0 : step.hashCode());
        result = prime * result + ((uesBypass == null) ? 0 : uesBypass.hashCode());
        result = prime * result + ((resultStatus == null) ? 0 : resultStatus.hashCode());
        result = prime * result + ((updateDate == null) ? 0 : updateDate.hashCode());
        result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
        result = prime * result + ((dcuId == null) ? 0 : dcuId.hashCode());
        return result;
    }

//    @Override
//    public String toString() {
//        return "FirmwareIssueHistory [firmwareId=" + id.getFirmwareId()
//                + ",issueDate" + id.getIssueDate()
//                + ",locationid" + id.getLocationId()
//                + ",deviceType=" + id.getDeviceType()
//                + ",deviceId=" + id.getDeviceId()
//                + ",step=" + step
//				+ ",excuteType=" + uesBypass
//                + ",updateDate=" + updateDate
//                + ", " + firmwareIssue.toString()
//                + "]";
//    }

    
    @Override
    public String toString() {
    	return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}