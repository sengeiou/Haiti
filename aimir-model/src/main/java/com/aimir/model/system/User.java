/**
 * OperatorContract.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;

import net.sf.json.JSONString;

/**
 * Operator.java Description 
 * 시스템 로그인을 위한 사용자 정보 
 * 
 * Date          Version     Author   Description
 * -              V1.0       강소이         신규작성
 * 2011. 3. 23.   v1.1       eunmiae  HEMS 회원정보관리를 위한 항목 추가       
 * 2011. 4. 14.   v1.2       eunmiae  pucNumber 길이 100-> 250으로 변경     
 * 2016. 2. 23.   v1.3       lucky    Change table name for reserved word.
 *
 */
@MappedSuperclass
public abstract class User  extends BaseObject implements JSONString, IAuditable {

	private static final long serialVersionUID = 6284113740604163761L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AIMIR_USER_SEQ")
	@SequenceGenerator(name="AIMIR_USER_SEQ", sequenceName="AIMIR_USER_SEQ", allocationSize=1) 
    protected Integer id; //  ID(PK)
	
	/**
     * HEMS회원일 경우는 "customer"권한으로 등록한다.
     */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="role_id")
	@ReferencedBy(name="name")
	protected Role role;	//group id(role)
	
	@Column(name="role_id", nullable=true, updatable=false, insertable=false)
	protected Integer roleId;
	
	@Column(unique=true, length=100, nullable=true)
	protected String loginId;	//loginId
	protected String password;	//OPERATOR PASSWORD
	
	@Column(length=256, nullable=false)
	protected String name;	// name
	protected String aliasName;	// alias
	
	protected String email;	// email
	protected String telNo;	// telNo
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="supplier_id")
	@ReferencedBy(name="name")
	protected Supplier supplier;	// supplierId
	
	@Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
	protected Integer supplierId;
	
	@Column(length=14)
	protected String lastPasswordChangeTime;	// lastPasswordChangeTime
	
	@Transient
	protected String lastPasswordChangeTimeLocale;	// lastPasswordChangeTime by Locale
	
	@Column(length=14)
	protected String lastLoginTime;		// lastLoginTime
	
	protected Boolean loginDenied;		// isDenied
	protected String deniedReason;		// deniedReason
	protected Integer failedLoginCount;	// failedLoginCount
	protected String locale;				// User's locale information
	
	/*
	 * 10.08.05 ej8486
	 * 사용자가 표준대시보드를 볼 것인지에 대한 설정 정보
	 */
	
	@Column
	protected Boolean showDefaultDashboard;

    /* 2011. 3. 23 v1.1 HEMS 회원정보관리를 위한 항목 추가 ADD START eunmiae */

	/* 2011. 4. 14 v1.2 HEMS 회원정보관리를 위한 항목 추가 UPDATE START eunmiae */
	@Column(unique=false, length=250)
	/* 2011. 4. 14 v1.2 HEMS 회원정보관리를 위한 항목 추가 UPDATE END eunmiae */
    @ColumnInfo(name="식별아이디(주민등록번호)", descr="암호화하여 관리한다.")	
	protected String pucNumber;

	@Column(name = "OPERATOR_STATUS",columnDefinition= "INTEGER default 1")
    @ColumnInfo(name="회원가입 상태", descr="회원(1), 탈퇴(0)")
    protected Integer operatorStatus;

    @Column(length=10)
    @ColumnInfo(name="우편번호", descr="'-'포함한 값")
    protected String zipCode;

	@Column(length=150)
    @ColumnInfo(name="주소1", descr="우편번호검색으로 선택된 주소")
    protected String address1;

	@Column(length=150)
    @ColumnInfo(name="주소2(상세주소)", descr="입력한 상세주소")
    protected String address2;

	@Column(length=20)
    @ColumnInfo(name="핸드폰번호", descr="'-'포함한 값")
    protected String mobileNumber;

    @Column(columnDefinition= "INTEGER default 1")
    @ColumnInfo(name="SMS수신여부", descr="허락(1), 거부(0)")
	protected Integer smsYn;

    @Column(columnDefinition= "INTEGER default 1")
    @ColumnInfo(name="EMAIL수신여부", descr="허락(1), 거부(0)")
	protected Integer emailYn;

	@Column(name = "WRITE_DATE", length=14)
    @ColumnInfo(name="회원정보 등록 날짜", descr="yyyymmdddhhmmss")
	protected String writeDate;
	
	@Column(name = "LAST_CHARGE_DATE", length=14)
	@ColumnInfo(name="마지막 충전 일", descr="yyyymmddhhmmss")
	protected String lastChargeDate;

	@Column(name = "UPDATE_DATE", length=14)
    @ColumnInfo(name="회원정보 갱신 날짜", descr="yyyymmddhhmmss")
	protected String updateDate;

    @Column(name = "USE_LOCATION", length=1)
    @ColumnInfo(name="검침데이터 조회 시 로케이션 제한 여부")
    protected Boolean useLocation;

    /**
     * add this fiedl for pre-bid test in Thailand temporarily
     */
    @ColumnInfo(name="지역아이디", descr="지역 테이블의 ID나  NULL", view=@Scope(create=true, read=true, update=true) )
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="LOCATION_ID")
	@ReferencedBy(name="name")
	protected Location location;

	@Column(name="LOCATION_ID", nullable=true, updatable=false, insertable=false)
    protected Integer locationId;
	
	@Column(name="IS_FIRSTLOGIN", nullable=true)
	private Boolean isFirstLogin;
	
	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @XmlTransient
    public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Integer getLocationId() {
		return locationId;
	}

	public void setLocationId(Integer locationId) {
		this.locationId = locationId;
	}

	public String getPucNumber() {
		return pucNumber;
	}

	public void setPucNumber(String pucNumber) {
		this.pucNumber = pucNumber;
	}

	public Integer getOperatorStatus() {
		return operatorStatus;
	}

	public void setOperatorStatus(Integer operatorStatus) {
		this.operatorStatus = operatorStatus;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public Integer getSmsYn() {
		return smsYn;
	}

	public void setSmsYn(Integer smsYn) {
		this.smsYn = smsYn;
	}

	public Integer getEmailYn() {
		return emailYn;
	}

	public void setEmailYn(Integer emailYn) {
		this.emailYn = emailYn;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public String getLastChargeDate() {
		return lastChargeDate;
	}

	public void setLastChargeDate(String lastChargeDate) {
		this.lastChargeDate = lastChargeDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
    /* 2011. 3. 23 v1.1 HEMS 회원정보관리를 위한 항목 추가 ADD END eunmiae */

	public Boolean getShowDefaultDashboard() {
		return showDefaultDashboard;
	}
	public void setShowDefaultDashboard(Boolean showDefaultDashboard) {
		this.showDefaultDashboard = showDefaultDashboard;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	
	@XmlTransient
	public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAliasName() {
		return aliasName;
	}
	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelNo() {
		return telNo;
	}
	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	
	public String getLastPasswordChangeTime() {
		return lastPasswordChangeTime;
	}
	public void setLastPasswordChangeTime(String lastPasswordChangeTime) {
		this.lastPasswordChangeTime = lastPasswordChangeTime;
	}

	public String getLastPasswordChangeTimeLocale() {
		return lastPasswordChangeTimeLocale;
	}
	public void setLastPasswordChangeTimeLocale(String lastPasswordChangeTimeLocale) {
		this.lastPasswordChangeTimeLocale = lastPasswordChangeTimeLocale;
	}
	
	public Boolean getLoginDenied() {
		return loginDenied;
	}
	
	public void setLoginDenied(Boolean loginDenied) {
		this.loginDenied = loginDenied;
	}
	
	public String getDeniedReason() {
		return deniedReason;
	}
	public void setDeniedReason(String deniedReason) {
		this.deniedReason = deniedReason;
	}
	
	public String getLastLoginTime() {
		return lastLoginTime;
	}
	
	public void setLastLoginTime(String lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	
	public Integer getFailedLoginCount() {
		return failedLoginCount;
	}
	public void setFailedLoginCount(Integer failedLoginCount) {
		this.failedLoginCount = failedLoginCount;
	}

	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	
	public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    /**
     * @return the useLocation
     */
    public Boolean getUseLocation() {
        return useLocation;
    }

    /**
     * @param useLocation the useLocation to set
     */
    public void setUseLocation(Boolean useLocation) {
        this.useLocation = useLocation;
    }

    public Boolean getIsFirstLogin() {
		return isFirstLogin;
	}

	public void setIsFirstLogin(Boolean isFirstLogin) {
		this.isFirstLogin = isFirstLogin;
	}

	@Override
	public String toString()
	{
	    return "Operator "+toJSONString();
	}
	public String toJSONString() {
	    
	    String retValue = "";
		
	    retValue = "{"
	        + "role:'" + ((this.role == null)? "null" : this.role.getId()) 
	        + "',loginId:'" + this.loginId 
	        + "',password:'" + this.password 
	        + "',name:'" + this.name 
	        + "',aliasName:'" + this.aliasName
	        + "',email:'" + this.email
	        + "',telNo:'" + this.telNo
	        + "',supplier:'" + ((this.supplier == null)? "null" : this.supplier.getId())
	        + "',showDefaultDashboard:'" + this.showDefaultDashboard
	        + "',lastPasswordChangeTime:'" + this.lastPasswordChangeTime
	        + "',lastLoginTime:'" + this.lastLoginTime
	        + "',loginDenied:'" + this.loginDenied
	        + "',deniedReason:'" + this.deniedReason
	        + "',failedLoginCount:'" + this.failedLoginCount
	        + "',locale:'" + this.locale
	        /* 2011. 3. 23 v1.1 HEMS 회원정보관리를 위한 항목 추가 ADD START eunmiae */
	        + "',pucNumber:'" + this.pucNumber
	        + "',operatorStatus:'" + this.operatorStatus
	        + "',zipCode:'" + this.zipCode
	        + "',address1:'" + this.address1
	        + "',address2:'" + this.address2
	        + "',mobileNumber:'" + this.mobileNumber
	        + "',smsYn:'" + this.smsYn
	        + "',emailYn:'" + this.emailYn
	        + "',writeDate:'" + this.writeDate
	        + "',updateDate:'" + this.updateDate
	        /* 2011. 3. 23 v1.1 HEMS 회원정보관리를 위한 항목 추가 ADD END eunmiae */
	        + "',location:'" + ((this.location == null)? "null" : this.location.getId())
	        + "',useLocation:'" + this.useLocation
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
    
    @Override
    public String getInstanceName() {
        return this.getName();
    }
}
	
