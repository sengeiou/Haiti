package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.constants.CommonConstants.FW_OTA;
import com.aimir.constants.CommonConstants.FW_STATE;
import com.aimir.constants.CommonConstants.FW_TRIGGER;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>Firmware Upgrade History</p>
 * 
 * @author goodjob
 *
 */
@Entity
@Table(name="FIRMWARE_HISTORY")
public class FirmwareHistory extends BaseObject {

	private static final long serialVersionUID = -2516486451321651376L;

	@EmbeddedId public FirmwareHistoryPk id;
	
    @ColumnInfo(name="MCU 아이디", descr="MCU 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)    
    @JoinColumn(name="MCU_ID")
    @ReferencedBy(name="sysID")
    private MCU mcu;
    
    @Column(name="MCU_ID", nullable=true, updatable=false, insertable=false)
    private Integer mcuId;

	@Column(name="TRIGGER_STEP")
    @Enumerated(EnumType.ORDINAL)
    private FW_TRIGGER triggerStep;

    @Column(name="TRIGGER_STATE")
    @Enumerated(EnumType.ORDINAL)
    private TR_STATE triggerState;

    @Column(name="OTA_STEP")
    @Enumerated(EnumType.ORDINAL)
    private FW_OTA otaStep;

    @Column(name="OTA_STATE")
    @Enumerated(EnumType.ORDINAL)
    private FW_STATE otaState;

    @Column(name="ERROR_CODE")
    private String errorCode;

    @Column(name="TRIGGER_HISTORY",length=255)
	private String triggerHistory;

    @Column(name="TRIGGER_CNT")
	private Integer triggerCnt;

    @ColumnInfo(name="장비유형(집중기,모뎀,미터 등)")
    @Column(name="EQUIP_KIND")
    private String equipKind;

    @ColumnInfo(name="장비 상세 타입 (모뎀이면 ZRU, ZEUPLS, ACD 등등)")
    @Column(name="EQUIP_TYPE")
    private String equipType;

    @ColumnInfo(name="장비 제조사 명")
    @Column(name="EQUIP_VENDOR")
    private String equipVendor;

    @ColumnInfo(name="장비 모델 명")
    @Column(name="EQUIP_MODEL")
    private String equipModel;

    @ColumnInfo(name="장비아이디 - 비지니스키")
    @Column(name="EQUIP_ID")
    private String equipId;
    
/*    @ColumnInfo(name="ISSUE_DATE")
    @Column(name="ISSUE_DATE")
    private String issueDate;

    @ColumnInfo(name="IN_SEQ")
    @Column(name="IN_SEQ")
    private String inSeq;*/

    
    public FirmwareHistory() {
        id = new FirmwareHistoryPk();
    }
    
    public void setTrId(Long trId) {
        id.setTrId(trId);
    }
    
    public Long getTrId() {
        return id.getTrId();
    }
    
    @XmlTransient
    public MCU getMcu() {
		return mcu;
	}

	public void setMcu(MCU mcu) {
		this.mcu = mcu;
	}
/*    public void setDeviceId(String deviceId) {
        id.setDeviceId(deviceId);
    }
    
    public String getDeviceId() {
        return id.getDeviceId();
    }*/
	
	public String getInSeq() {
		return id.getInSeq();
	}

	public void setInSeq(String inSeq) {
		this.id.setInSeq(inSeq);
	}
    
	public String getIssueDate() {
		return id.getIssueDate();
//		return this.issueDate;
	}


	public void setIssueDate(String issueDate) {
		this.id.setIssueDate(issueDate);
//		this.issueDate = issueDate;
	}

    public FirmwareHistoryPk getId() {
		return id;
	}

	public void setId(FirmwareHistoryPk id) {
		this.id = id;
	}

	public FW_TRIGGER getTriggerStep() {
		return triggerStep;
	}

	public void setTriggerStep(FW_TRIGGER triggerStep) {
		this.triggerStep = triggerStep;
	}

	public TR_STATE getTriggerState() {
		return triggerState;
	}

	public void setTriggerState(TR_STATE triggerState) {
		this.triggerState = triggerState;
	}

	public FW_OTA getOtaStep() {
		return otaStep;
	}

	public void setOtaStep(FW_OTA otaStep) {
		this.otaStep = otaStep;
	}

	public FW_STATE getOtaState() {
		return otaState;
	}

	public void setOtaState(FW_STATE otaState) {
		this.otaState = otaState;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getTriggerHistory() {
		return triggerHistory;
	}

	public void setTriggerHistory(String triggerHistory) {
		this.triggerHistory = triggerHistory;
	}

	public Integer getTriggerCnt() {
		return triggerCnt;
	}

	public void setTriggerCnt(Integer triggerCnt) {
		this.triggerCnt = triggerCnt;
	}

	public String getEquipKind() {
		return equipKind;
	}

	public void setEquipKind(String equipKind) {
		this.equipKind = equipKind;
	}

	public String getEquipType() {
		return equipType;
	}

	public void setEquipType(String equipType) {
		this.equipType = equipType;
	}

	public String getEquipVendor() {
		return equipVendor;
	}

	public void setEquipVendor(String equipVendor) {
		this.equipVendor = equipVendor;
	}

	public String getEquipModel() {
		return equipModel;
	}

	public void setEquipModel(String equipModel) {
		this.equipModel = equipModel;
	}

	public String getEquipId() {
		return equipId;
	}

	public void setEquipId(String equipId) {
		this.equipId = equipId;
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