package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * <p>
 * Copyright NuriTelecom Co.Ltd. since 2009
 * </p>
 * 
 * @author elevas park
 *
 */
@Entity
@Table(name = "FIRMWARE_ISSUE")
public class FirmwareIssue extends BaseObject {
	private static final long serialVersionUID = 1329672452910583447L;

	@EmbeddedId
	public FirmwareIssuePk id;

	@Column(length = 50)
	@ColumnInfo(name = "name")
	private String name;
	
	@XmlTransient
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "firmwareId", insertable = false, updatable = false)
	private Firmware firmware;

    @ColumnInfo(name="OTA Execute type", descr="OTA Execute Type - CLONE_OTA(0), EACH_BY_DCU(1), EACH_BY_HES(2)")
    @Column(name="EXECUTE_TYPE", length=5)
    private Integer otaExecuteType;
	
    @ColumnInfo(name="OTA Retry Count", descr="OTA retry count")
    @Column(name="RETRY_COUNT", length=5)
	private Integer otaRetryCount;
	
    @ColumnInfo(name="OTA Retry cycle", descr="OTA Retry cycle")
    @Column(name="RETRY_CYCLE", length=5)
	private Integer otaRetryCycle;
	
	@Column
	@ColumnInfo(name = "total count")
	private Integer totalCount;

	@Column
	@ColumnInfo(name = "step1 coount")
	private Integer step1Count;

	@Column
	@ColumnInfo(name = "step2 coount")
	private Integer step2Count;

	@Column
	@ColumnInfo(name = "step3 coount")
	private Integer step3Count;

	@Column
	@ColumnInfo(name = "step4 coount")
	private Integer step4Count;

	@Column
	@ColumnInfo(name = "step5 coount")
	private Integer step5Count;

	@Column
	@ColumnInfo(name = "step6 coount")
	private Integer step6Count;

	@Column
	@ColumnInfo(name = "step7 coount")
	private Integer step7Count;

	@ColumnInfo(name="Command type", descr="Command Type - MODEM_OTA(0), CLONE_ON(1), CLONE_OFF(2)")
	@Column(name="COMMAND_TYPE", length=5)
	private Integer commandType;
	  
	public FirmwareIssue() {
		id = new FirmwareIssuePk();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getStep1Count() {
		return step1Count;
	}

	public void setStep1Count(Integer step1Count) {
		this.step1Count = step1Count;
	}

	public Integer getStep2Count() {
		return step2Count;
	}

	public void setStep2Count(Integer step2Count) {
		this.step2Count = step2Count;
	}

	public Integer getStep3Count() {
		return step3Count;
	}

	public void setStep3Count(Integer step3Count) {
		this.step3Count = step3Count;
	}

	public Integer getStep4Count() {
		return step4Count;
	}

	public void setStep4Count(Integer step4Count) {
		this.step4Count = step4Count;
	}

	public Integer getStep5Count() {
		return step5Count;
	}

	public void setStep5Count(Integer step5Count) {
		this.step5Count = step5Count;
	}

	public Integer getStep6Count() {
		return step6Count;
	}

	public void setStep6Count(Integer step6Count) {
		this.step6Count = step6Count;
	}

	public Integer getStep7Count() {
		return step7Count;
	}

	public void setStep7Count(Integer step7Count) {
		this.step7Count = step7Count;
	}
	
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FirmwareIssue other = (FirmwareIssue) obj;
		if (firmware == null) {
			if (other.firmware != null)
				return false;
		} else if (!firmware.equals(other.firmware))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (otaExecuteType == null) {
			if (other.otaExecuteType != null)
				return false;
		} else if (!otaExecuteType.equals(other.otaExecuteType))
			return false;
		if (!otaRetryCount.equals(other.otaRetryCount))
			return false;
		if (!otaRetryCycle.equals(other.otaRetryCycle))
			return false;
		if (step1Count == null) {
			if (other.step1Count != null)
				return false;
		} else if (!step1Count.equals(other.step1Count))
			return false;
		if (step2Count == null) {
			if (other.step2Count != null)
				return false;
		} else if (!step2Count.equals(other.step2Count))
			return false;
		if (step3Count == null) {
			if (other.step3Count != null)
				return false;
		} else if (!step3Count.equals(other.step3Count))
			return false;
		if (step4Count == null) {
			if (other.step4Count != null)
				return false;
		} else if (!step4Count.equals(other.step4Count))
			return false;
		if (step5Count == null) {
			if (other.step5Count != null)
				return false;
		} else if (!step5Count.equals(other.step5Count))
			return false;
		if (step6Count == null) {
			if (other.step6Count != null)
				return false;
		} else if (!step6Count.equals(other.step6Count))
			return false;
		if (step7Count == null) {
			if (other.step7Count != null)
				return false;
		} else if (!step7Count.equals(other.step7Count))
			return false;
		if (totalCount == null) {
			if (other.totalCount != null)
				return false;
		} else if (!totalCount.equals(other.totalCount))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firmware == null) ? 0 : firmware.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((otaExecuteType == null) ? 0 : otaExecuteType.hashCode());
		result = prime * result + ((otaRetryCount == null) ? 0 : otaRetryCount.hashCode());
		result = prime * result + ((otaRetryCycle == null) ? 0 : otaRetryCycle.hashCode());
		result = prime * result + ((step1Count == null) ? 0 : step1Count.hashCode());
		result = prime * result + ((step2Count == null) ? 0 : step2Count.hashCode());
		result = prime * result + ((step3Count == null) ? 0 : step3Count.hashCode());
		result = prime * result + ((step4Count == null) ? 0 : step4Count.hashCode());
		result = prime * result + ((step5Count == null) ? 0 : step5Count.hashCode());
		result = prime * result + ((step6Count == null) ? 0 : step6Count.hashCode());
		result = prime * result + ((step7Count == null) ? 0 : step7Count.hashCode());
		result = prime * result + ((totalCount == null) ? 0 : totalCount.hashCode());
		return result;
	}
	
	//	@Override
	//	public String toString() {
	//		return "FirmwareIssue [id=" + id.getFirmwareId() + ", issueDate=" + id.getIssueDate() + ", locationId=" + id.getLocationId() + ", name=" + name + ", totalCount=" + totalCount + ", step1Count=" + step1Count + ", step2Count=" + step2Count + ", step3Count=" + step3Count + ", step4Count=" + step4Count + ", step5Count=" + step5Count + ", step6Count=" + step6Count + ", step7Count=" + step7Count
	//				+ ", " + firmware.toString() + "]";
	//	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public void setFirmware(Firmware firmware) {
		this.firmware = firmware;
	}

	public Integer getOtaExecuteType() {
		return otaExecuteType;
	}

	public void setOtaExecuteType(Integer otaExecuteType) {
		this.otaExecuteType = otaExecuteType;
	}

	public int getOtaRetryCount() {
		return otaRetryCount;
	}

	public void setOtaRetryCount(int otaRetryCount) {
		this.otaRetryCount = otaRetryCount;
	}

	public int getOtaRetryCycle() {
		return otaRetryCycle;
	}

	public void setOtaRetryCycle(int otaRetryCycle) {
		this.otaRetryCycle = otaRetryCycle;
	}

	public Integer getCommandType() {
		return commandType;
	}

	public void setCommandType(Integer commandType) {
		this.commandType = commandType;
	}
	
	
	
}
