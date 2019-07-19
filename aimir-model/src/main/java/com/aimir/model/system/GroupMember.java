package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;
/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 그룹 의 멤버 
 * 그룹에 속한 멤버 정보 
 * @author goodjob
 *
 */

@Entity
@Table(name = "GROUP_MEMBER")
public class GroupMember extends BaseObject{

	private static final long serialVersionUID = 7062998849779282269L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="GROUP_MEMBER_SEQ")
	@SequenceGenerator(name="GROUP_MEMBER_SEQ", sequenceName="GROUP_MEMBER_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;	//	ID(PK)
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="group_id")
    @ColumnInfo(name="그룹정보")
    private AimirGroup aimirGroup;
	
	@Column(name="group_id", nullable=true, updatable=false, insertable=false)
	private Integer groupId;

	@Column(nullable=false)
	private String member;	// business key
	
	@Column(name="WRITE_DATE",length=14,nullable=false)
	private String writeDate;
	
	@Column(name="isRegistration",nullable=true)
	@ColumnInfo(descr="집중기에 해당 멤버가 그룹으로 등록되어 있는지 여부")
	private Boolean isRegistration;
	
	@Column(name="LAST_SYNC_DATE",length=14,nullable=true)
	@ColumnInfo(descr="집중기에 그룹이 등록되었는지 싱크를 맞추어본 마지막 날짜")
	private String lastSyncDate;

	public Boolean getIsisRegistration() {
		return isRegistration;
	}

	public void setIsisRegistration(Boolean isRegistration) {
		this.isRegistration = isRegistration;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
		
	@XmlTransient
	public AimirGroup getGroup() {
		return aimirGroup;
	}

	public void setGroup(AimirGroup aimirGroup) {
		this.aimirGroup = aimirGroup;
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}
	
	public String getLastSyncDate() {
		return lastSyncDate;
	}

	public void setLastSyncDate(String lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}

	public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Override
	public String toString()
	{
	    String retValue = "";
		
	    retValue = "{"
	        + "id:'" + this.id 
	        + "',member:'" + this.member 
	        + "',writeDate:'" + this.writeDate 
	        + "',aimirGroup:'" + ((this.getGroup() == null)? "null" : aimirGroup.getId()) 
	        + "'}";
	    
	    return retValue;
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
	
}
	