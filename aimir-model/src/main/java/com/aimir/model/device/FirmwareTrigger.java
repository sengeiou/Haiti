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
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>Firmware Trigger</p> 
 * 
 * 펌웨어 업데이트 후 결과를 추적하기 위한 클래스<br>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="FIRMWARE_TRIGGER")
public class FirmwareTrigger extends BaseObject {

	private static final long serialVersionUID = 847794150120894220L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="FIRMWARE_TRIGGER_SEQ")
    @SequenceGenerator(name="FIRMWARE_TRIGGER_SEQ", sequenceName="FIRMWARE_TRIGGER_SEQ", allocationSize=1) 
	private Long id;

    @Column(name="CREATE_DATE",length=14, nullable=false)
    private String createDate;
    
    @Column(name="END_DATE",length=14)
    private String endDate;
    
    @Column(name="SRC_HWVER",length=20)
    private String srcHWVer;
    
    @Column(name="SRC_FWVER",length=20)
    private String srcFWVer;
    
    @Column(name="SRC_FWBUILD",length=20)
    private String srcFWBuild;
    
    @Column(name="SRC_FIRMWARE",length=100)
    private String srcFirmware;
    
    @Column(name="TARGET_FIRMWARE",length=100)
    private String targetFirmware;
    
    @Column(name="TARGET_HWVER",length=20)
    private String targetHWVer;
    
    @Column(name="TARGET_FWVER",length=20)
    private String targetFWVer;
    
    @Column(name="TARGET_FWBUILD",length=20)
    private String targetFWBuild;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getSrcFirmware() {
		return srcFirmware;
	}

	public void setSrcFirmware(String srcFirmware) {
		this.srcFirmware = srcFirmware;
	}

	public String getTargetFirmware() {
		return targetFirmware;
	}

	public void setTargetFirmware(String targetFirmware) {
		this.targetFirmware = targetFirmware;
	}

	public String getTargetHWVer() {
		return targetHWVer;
	}

	public void setTargetHWVer(String targetHWVer) {
		this.targetHWVer = targetHWVer;
	}

	public String getTargetFWVer() {
		return targetFWVer;
	}

	public void setTargetFWVer(String targetFWVer) {
		this.targetFWVer = targetFWVer;
	}

	public String getTargetFWBuild() {
		return targetFWBuild;
	}

	public void setTargetFWBuild(String targetFWBuild) {
		this.targetFWBuild = targetFWBuild;
	}

	public String getSrcHWVer() {
        return srcHWVer;
    }

    public void setSrcHWVer(String srcHWVer) {
        this.srcHWVer = srcHWVer;
    }

    public String getSrcFWVer() {
        return srcFWVer;
    }

    public void setSrcFWVer(String srcFWVer) {
        this.srcFWVer = srcFWVer;
    }

    public String getSrcFWBuild() {
        return srcFWBuild;
    }

    public void setSrcFWBuild(String srcFWBuild) {
        this.srcFWBuild = srcFWBuild;
    }

    @Override
	public boolean equals(Object obj) {
		return true;
	}

	@Override
	public int hashCode() {

		int result = 0;
		
		return result;
	}

	@Override
	public String toString() {
		return "";
	}

}