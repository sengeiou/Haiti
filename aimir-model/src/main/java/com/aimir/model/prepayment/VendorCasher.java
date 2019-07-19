package com.aimir.model.prepayment;

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

import com.aimir.model.BaseObject;
import com.aimir.model.system.Operator;

@Entity
@Table(name="VENDOR_CASHER")
public class VendorCasher extends BaseObject{

	private static final long serialVersionUID = 943652653304862341L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="VENDOR_CASHER_SEQ")	
	@SequenceGenerator(name="VENDOR_CASHER_SEQ", sequenceName="VENDOR_CASHER_SEQ", allocationSize=1) 
	private Integer id;
	
	@Column(name="vendor_id", nullable=true, updatable=false, insertable=false)
	private Integer vendorId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="vendor_id")
	private Operator vendor;
	
	private String casherId;
	
	private String password;
	
	private String name;
	
	private Boolean isManager;
	
	private Integer  status;
	
	private String macAddress;
	
	private String lastUpdateDate;
	
	private Boolean isFirst;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getVendorId() {
		return vendorId;
	}

	public void setVendorId(Integer vendorId) {
		this.vendorId = vendorId;
	}

	public Operator getVendor() {
		return vendor;
	}

	public void setVendor(Operator vendor) {
		this.vendor = vendor;
	}

	public String getCasherId() {
		return casherId;
	}

	public void setCasherId(String casherId) {
		this.casherId = casherId;
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

	public Boolean getIsManager() {
		return isManager;
	}

	public void setIsManager(Boolean isManager) {
		this.isManager = isManager;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	public String getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Boolean getIsFirst() {
		return isFirst;
	}

	public void setIsFirst(Boolean isFirst) {
		this.isFirst = isFirst;
	}

	@Override
	public String toString() {
        StringBuffer str = new StringBuffer();
        
        str.append("{"
            + "id:" + this.id
            + ", casherId:" + this.casherId
            + ", isFirst:'" + this.isFirst
            + ", isManager:'" + this.isManager
            + ", lastUpdateDate:'" + this.lastUpdateDate
            + ", name:'" + this.name
            + ", status:'" + this.status
            + ", vendor:'" + this.vendor
            + "}");
        
        return str.toString();
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
