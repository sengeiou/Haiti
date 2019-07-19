package com.aimir.model.device;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>Change Log - AIMIR Model 정보에서 관리하는 클래스의 instance 내용 변경시 이력으로 저장되는 내용을 담은 클래스</p>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
public class ChangeLog extends BaseObject {

	private static final long serialVersionUID = -7127561514740651044L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CHANGE_LOG_SEQ")
    @SequenceGenerator(name="CHANGE_LOG_SEQ", sequenceName="CHANGE_LOG_SEQ", allocationSize=1) 
	private Long id;
    
	private String changeDate;
	private String changeTime;
	private Integer totalChangeCnt;
	private Integer seq;
	private String operationCode;
	private Long operationLogId;
	private String operatorTypeCode;
	private String operatorId;
	private String targetTypeCode;
	private String target;
	private String property;
	private String previousVal;
	private String currentVal;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	public String getChangeDate() {
		return changeDate;
	}
	public void setChangeDate(String changeDate) {
		this.changeDate = changeDate;
	}
	public String getChangeTime() {
		return changeTime;
	}
	public void setChangeTime(String changeTime) {
		this.changeTime = changeTime;
	}
	public Integer getTotalChangeCnt() {
		return totalChangeCnt;
	}
	public void setTotalChangeCnt(Integer totalChangeCnt) {
		this.totalChangeCnt = totalChangeCnt;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public String getOperationCode() {
		return operationCode;
	}
	public void setOperationCode(String operationCode) {
		this.operationCode = operationCode;
	}
	public Long getOperationLogId() {
		return operationLogId;
	}
	public void setOperationLogId(Long operationLogId) {
		this.operationLogId = operationLogId;
	}
	public String getOperatorTypeCode() {
		return operatorTypeCode;
	}
	public void setOperatorTypeCode(String operatorTypeCode) {
		this.operatorTypeCode = operatorTypeCode;
	}
	public String getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}
	public String getTargetTypeCode() {
		return targetTypeCode;
	}
	public void setTargetTypeCode(String targetTypeCode) {
		this.targetTypeCode = targetTypeCode;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public String getPreviousVal() {
		return previousVal;
	}
	public void setPreviousVal(String previousVal) {
		this.previousVal = previousVal;
	}
	public String getCurrentVal() {
		return currentVal;
	}
	public void setCurrentVal(String currentVal) {
		this.currentVal = currentVal;
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
