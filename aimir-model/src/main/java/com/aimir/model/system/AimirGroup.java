package com.aimir.model.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * 그룹에 대한 정보를 가지고 있는 모델 클래스이다.<br>
 * 여기서 그룹은 AIMIR SYSTEM 내에서 Location, Operator, Contract, MCU, Modem, Meter, EndDevice 등으로 맵핑할 수 있는 그룹을 나타낸다.<br>
 * 그룹의 이름은 유일하며 중복될 수 없다.<br>
 * 
 * @author YeonKyoung Park(goodjob)
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "GroupName", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("AimirGroup")
public class AimirGroup extends BaseObject {

	private static final long serialVersionUID = 6279582965129053637L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AIMIRGROUP_SEQ")
	@SequenceGenerator(name = "AIMIRGROUP_SEQ", sequenceName = "AIMIRGROUP_SEQ", allocationSize = 1)
	@ColumnInfo(name = "PK", descr = "PK")
	private Integer id; // ID(PK)

	@Column(name = "NAME", unique = true, nullable = false)
	private String name; // name

	@Column(name = "WRITE_DATE", length = 14, nullable = false)
	private String writeDate;

	@Column(name = "GROUP_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	@ColumnInfo(name = "그룹 종류", descr = "Location, Operator, Contract, MCU, Modem, Meter, EndDevice")
	private GroupType groupType;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "operator_id")
	@ReferencedBy(name = "loginId")
	private Operator operator; // supplier

	@Column(name="operator_id", nullable=true, updatable=false, insertable=false)
	private Integer operatorId;
	
	@Column(name = "ALL_USERS_ACCESS")
	@ColumnInfo(descr = "공통으로 사용할 것인지 아니면 해당 사용자(로그인한) 사용자에게만 사용을 허용할 것인지")
	private Boolean allUserAccess;

	@ColumnInfo(name = "공급사아이디", view = @Scope(create = true, read = true, update = true), descr = "공급사 테이블의 ID 혹은  NULL")
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "SUPPLIER_ID")
	@ReferencedBy(name = "name")
	private Supplier supplier;

	@Column(name="SUPPLIER_ID", nullable=true, updatable=false, insertable=false)
	private Integer supplierId;
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private Set<GroupMember> members = new HashSet<GroupMember>(0);
	
	@ColumnInfo(name = "대표 번호")
	private String mobileNo;

	private String descr; // description

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public GroupType getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = GroupType.valueOf(groupType);
	}

	@XmlTransient
	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public Boolean getAllUserAccess() {
		return allUserAccess;
	}

	public void setAllUserAccess(Boolean allUserAccess) {
		this.allUserAccess = allUserAccess;
	}

	public Set<GroupMember> getMembers() {
		return members;
	}

	public void setMembers(Set<GroupMember> members) {
		this.members = members;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
	
	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}

	public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    @Override
	public String toString() {
		String retValue = "";

		retValue = "{" + "id:'" + this.id + "',name:'" + this.name
				+ "',descr:'" + this.descr + "',groupType:'" + this.groupType
				+ "',writeDate:'" + this.writeDate + "',allUersAccess:'"
				+ this.allUserAccess + "',members:'"
				+ ((this.members == null) ? "null" : members.size())
				+ "',operator:'"
				+ ((this.operator == null) ? "null" : operator.getId()) + "'}";

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
