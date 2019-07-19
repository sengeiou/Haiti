package com.aimir.model.system;

import javax.persistence.Entity;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;

/**
 *
 * 사용자 그룹(Role)별 허용가젯 정보 
 * 
 * 
 * 
 * 
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="GADGET_ROLE", uniqueConstraints = @UniqueConstraint(columnNames = {"role_id","gadget_id","supplier_id"}))
public class GadgetRole extends BaseObject {

	private static final long serialVersionUID = -7119538674532613070L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,  generator="GADGETROLE_SEQ")
	@SequenceGenerator(name="GADGETROLE_SEQ", sequenceName="GADGETROLE_SEQ", allocationSize=1) 
	private Integer id;				// id(PK)
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="role_id")
	@ReferencedBy(name="name")
	private Role role;				// role
	
	@Column(name="role_id", nullable=true, updatable=false, insertable=false)
	private Integer roleId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="gadget_id")
	@ReferencedBy(name="name")
	private Gadget gadget;			// gadget
	
	@Column(name="gadget_id", nullable=true, updatable=false, insertable=false)
	private Integer gadgetId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="supplier_id")
	@ReferencedBy(name="name")
	private Supplier supplier;		// supplier
	
	@Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
	private Integer supplierId;
	
	public Integer getId() {
		return id;
	}
	public void setRoled(Integer  id) {
		this.id = id;
	}	
	
	@XmlTransient
    public Role getRole() {
		return role;
	}
	public void setRole(Role role) {
		this.role = role;
	}
	
	@XmlTransient
	public Gadget getGadget() {
		return gadget;
	}
	public void setGadget(Gadget gadget) {
		this.gadget = gadget;
	}
	
	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	
	public Integer getRoleId() {
        return roleId;
    }
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
    public Integer getGadgetId() {
        return gadgetId;
    }
    public void setGadgetId(Integer gadgetId) {
        this.gadgetId = gadgetId;
    }
    public Integer getSupplierId() {
        return supplierId;
    }
    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
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
	