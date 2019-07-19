package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.aimir.annotation.ColumnInfo;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>Integrated Energy Interface Unit (집단에너지에 들어가는 모뎀) 정보</p>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@DiscriminatorValue("IEIU")
public class IEIU extends Modem {

    private static final long serialVersionUID = -8972065131321718558L;
    
	@ColumnInfo(name="", descr="")
    @Column(name="ERROR_STATUS", length=10)
    private Integer errorStatus;	
	
    @ColumnInfo(name="groupNumber",descr="그룹번호 자계기인 경우 해당")
    @Column(name="GROUP_NUMBER")
    private Integer groupNumber;

	@ColumnInfo(name="memberNumber", descr="그룹 멤버 번호 자계기인 경우 해당")
    @Column(name="MEMBER_NUMBER")
    private Integer memberNumber;
       
	public Integer getErrorStatus() {
		return errorStatus;
	}

	public void setErrorStatus(Integer errorStatus) {
		this.errorStatus = errorStatus;
	}

    public Integer getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(Integer groupNumber) {
		this.groupNumber = groupNumber;
	}

	public Integer getMemberNumber() {
		return memberNumber;
	}

	public void setMemberNumber(Integer memberNumber) {
		this.memberNumber = memberNumber;
	}
	
}