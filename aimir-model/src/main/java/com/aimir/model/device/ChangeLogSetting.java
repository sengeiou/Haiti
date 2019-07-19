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
 * <p>Change Log Setting - AIMIR Model 정보에서 관리하는 클래스의 instance 내용 변경시 이력으로 저장되는 내용에 대해 설정하는 내용을 담은 클래스</p>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
public class ChangeLogSetting extends BaseObject {
	
	private static final long serialVersionUID = -2122883398746352567L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CHANGE_LOG_SETTING_SEQ")
    @SequenceGenerator(name="CHANGE_LOG_SETTING_SEQ", sequenceName="CHANGE_LOG_SETTING_SEQ", allocationSize=1) 
	private Long id;	
	
	private Long supplierId;
	private String targetCode;
	private String isLoggingCode;
	private String labelCode;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(Long supplierId) {
		this.supplierId = supplierId;
	}
	public String getTargetCode() {
		return targetCode;
	}
	public void setTargetCode(String targetCode) {
		this.targetCode = targetCode;
	}
	public String getIsLoggingCode() {
		return isLoggingCode;
	}
	public void setIsLoggingCode(String isLoggingCode) {
		this.isLoggingCode = isLoggingCode;
	}
	public String getLabelCode() {
		return labelCode;
	}
	public void setLabelCode(String labelCode) {
		this.labelCode = labelCode;
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
