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

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;

/**
 * Tariff 타입 (고객종별)
 * 에너지 사용에 요율을 고객 종별 기준으로 나타냅.
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="TARIFFTYPE")
public class TariffType extends BaseObject implements JSONString{

	private static final long serialVersionUID = 5039905265620686709L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TARIFFTYPE_SEQ")
	@SequenceGenerator(name="TARIFFTYPE_SEQ", sequenceName="TARIFFTYPE_SEQ", allocationSize=1)
	@ColumnInfo(name="PK", descr="PK")
	private Integer id;
	
	@Column(unique=true, nullable=true)
	private Integer code;
	
	@Column(unique=true, nullable=false, length=255)
	private String name;		
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="supplier_id", nullable=false)
	@ReferencedBy(name="name")
	private Supplier supplier;
	
	@Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
	private Integer supplierId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "servicetype_id", nullable=false)
	@ReferencedBy(name="code")
	private Code serviceTypeCode;
	
	@Column(name="servicetype_id", nullable=true, updatable=false, insertable=false)
	private Integer serviceTypeCodeId;
	
	@Column(name="description")
	@ColumnInfo(descr="계약종별에 대한 설명")
	private String description;
	
	
	public TariffType() {		
	}
	public TariffType(Integer id) {
		this.id = id;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	
	public Integer getCode() {
		return code;
	}
		
	@XmlTransient
    public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	
	@XmlTransient
	public Code getServiceTypeCode() {
		return serviceTypeCode;
	}
	public void setServiceTypeCode(Code serviceTypeCode) {
		this.serviceTypeCode = serviceTypeCode;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getSupplierId() {
        return supplierId;
    }
    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }
    
    public Integer getServiceTypeCodeId() {
        return serviceTypeCodeId;
    }
    public void setServiceTypeCodeId(Integer serviceTypeCodeId) {
        this.serviceTypeCodeId = serviceTypeCodeId;
    }
    @Override
	public String toString()
	{
	    return "TariffType "+toJSONString();
	}
	public String toJSONString() {
	    
	    String retValue = "";
		
	    retValue = "{"
	        + "id:'" + this.id 
	        + "',name:'" + this.name 
	        + "',serviceTypeCode:'" + ((this.serviceTypeCode == null)? "null":this.serviceTypeCode.getId())
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
