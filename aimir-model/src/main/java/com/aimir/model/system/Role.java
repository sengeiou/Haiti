package com.aimir.model.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;
/**
 * 사용자 그룹 정보
 * 사용자 롤 그룹 
 * @author 강소이
 *
 */

@Entity
@Table(name = "ROLE")
public class Role extends BaseObject implements JSONString{
	
	private static final long serialVersionUID = -7110538674532613070L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ROLE_SEQ")
	@SequenceGenerator(name="ROLE_SEQ", sequenceName="ROLE_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;	//	ID(PK)

	//gadget
	//supplier
	@Column(unique=true, nullable=false)
	private String name;	// name
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="supplier_id")
	@ReferencedBy(name="name")
	private Supplier supplier;	// supplier
	
	@Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
	private Integer supplierId;
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="role_id")
	private Set<Operator> operators = new HashSet<Operator>(0);
	
	private String descr;	// description
	private Boolean loginAuthority;	// possibility for login
	private String mtrAuthority;	// Authority for reading or writing the metering data
	private String systemAuthority;	// Authority for reading or writing or command the systemInfo
	private String dlmsAuthority;	// Authority for reading or writing dlms for obisCommand.

	@ManyToMany
	private Set<Code> commands = new HashSet<Code>();	// executable command list 
	
	private Boolean hasDashboardAuth;	// authority for dashboard
	private Boolean customerRole;	// 
	
    @Column(nullable=true, length=10)
	private Integer maxMeters;
	/*
	 * 10.07.26 ej8486 주석처리
	 * 사유 : 하나의 가젯이 여러 룰을 가질 수 있다.
	 */
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="role_id")
	private Set<Gadget> permitedGadgets = new HashSet<Gadget>();

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

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@XmlTransient
	public Set<Operator> getOperators() {
		return operators;
	}

	public void setOperators(Set<Operator> operators) {
		this.operators = operators;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public Boolean getLoginAuthority() {
		return loginAuthority;
	}

	public void setLoginAuthority(Boolean loginAuthority) {
		this.loginAuthority = loginAuthority;
	}

	public String getMtrAuthority() {
		return mtrAuthority;
	}

	public void setMtrAuthority(String mtrAuthority) {
		this.mtrAuthority = mtrAuthority;
	}

	public String getSystemAuthority() {
		return systemAuthority;
	}

	public void setSystemAuthority(String systemAuthority) {
		this.systemAuthority = systemAuthority;
	}

	public String getDlmsAuthority() {
		return dlmsAuthority;
	}

	public void setDlmsAuthority(String dlmsAuthority) {
		this.dlmsAuthority = dlmsAuthority;
	}

	public Set<Code> getCommands() {
		return commands;
	}

	public void setCommands(Set<Code> commands) {
		this.commands = commands;
	}
	
	public void addCommand(Code command) {
		if (command == null)
			throw new IllegalArgumentException("command is null");
		
		if(commands == null)
			commands = new HashSet<Code>();

		this.commands.add(command);
	}
	
	public Boolean getHasDashboardAuth() {
		return hasDashboardAuth;
	}

	public void setHasDashboardAuth(Boolean hasDashboardAuth) {
		this.hasDashboardAuth = hasDashboardAuth;
	}

	public Boolean getCustomerRole() {
		return customerRole;
	}

	public void setCustomerRole(Boolean customerRole) {
		this.customerRole = customerRole;
	}

	@XmlTransient
	public Set<Gadget> getPermitedGadgets() {
		return permitedGadgets;
	}

	public void setPermitedGadgets(Set<Gadget> permitedGadgets) {
		this.permitedGadgets = permitedGadgets;
	}
	
	public void addPermitedGadget(Gadget gadget) {
		if (gadget == null)
			throw new IllegalArgumentException("gadget is null");		
		this.permitedGadgets.add(gadget);
	}

	public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getMaxMeters() {
		return maxMeters;
	}

	public void setMaxMeters(Integer maxMeter) {
		this.maxMeters = maxMeter;
	}

	@Override
	public String toString()
	{
	    return "Role "+toJSONString();
	}
	
	public String toJSONString() {
	    String retValue = "";
	    retValue = "{"
	        + "id:'" + this.id 
	        + "',name:'" + this.name 
	        + "',supplier:'" + ((this.supplier == null)? "null" : this.supplier.getId()) 
	        + "',loginAuthority:'" + this.loginAuthority 
	        + "',mtrAuthority:'" + this.mtrAuthority 
	        + "',systemAuthority:'" + this.systemAuthority
	        + "',dlmsAuthority:'" + this.dlmsAuthority
	        + "',hasDashboardAuth:'" + this.hasDashboardAuth  
	        + "',customerRole:'" + this.customerRole
	        + "',descr:'" + this.descr 
	        + "',maxMeters:'" + this.maxMeters
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
	