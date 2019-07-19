package com.aimir.model.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONSerializer;
import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;

/**
 * 공급사 서비스 타입
 * 공급사가 공급하는 에너지 타입 정보  
 * 공급자는 전기/가스/수도 등 여러가지 에너지를 공급할 수 있다.
 * 
 */
@Entity
public class SupplyType extends BaseObject implements JSONString, IAuditable {

	private static final long serialVersionUID = 5484283850274810363L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SUPPLYTYPE_SEQ")
	@SequenceGenerator(name="SUPPLYTYPE_SEQ", sequenceName="SUPPLYTYPE_SEQ", allocationSize=1) 
	@ColumnInfo(name="PK", descr="PK")
	private Integer id;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="supplier_id", nullable=false)
    @ColumnInfo(name="공급사")
	@ReferencedBy(name="name")
    private Supplier supplier;
	
	@Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
	private Integer supplierId;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "type_id")
	@ColumnInfo(name="서비스타입", descr="공급서비스 타입 전기,가스,수도,열 등")
	@ReferencedBy(name="code")
	private Code typeCode;
	
	@Column(name="type_id", nullable=true, updatable=false, insertable=false)
	private Integer typeCodeId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="co2formula_id")
	@ColumnInfo(name="탄소배출량")
	@ReferencedBy(name="name")
	private Co2Formula co2Formula;
	
	@Column(name="co2formula_id", nullable=true, updatable=false, insertable=false)
	private Integer co2FormulaId;
	
	@Column(length=8)
	@ColumnInfo(name="과금일",descr="요금에 대한 과금을 산정하는 기준 날짜")
	private String billDate;
	
	@OneToMany(mappedBy = "supplyType", cascade = CascadeType.REMOVE, fetch=FetchType.LAZY)
	private Set<SupplyTypeLocation> supplyTypeLocations = new HashSet<SupplyTypeLocation>(0);
	

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}
	
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
		// supplier.getSupplyTypes().add(this);
	}

	@XmlTransient
	public Code getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(Code typeCode) {
		this.typeCode = typeCode;
	}
	
	@XmlTransient
	public Co2Formula getCo2Formula() {
		return co2Formula;
	}
	public void setCo2Formula(Co2Formula co2Formula) {
		this.co2Formula = co2Formula;
	}
	public String getBillDate() {
		return billDate;
	}
	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	@XmlTransient
	public Set<SupplyTypeLocation> getSupplyTypeLocations() {
		return supplyTypeLocations;
	}
	public void setSupplyTypeLocations(Set<SupplyTypeLocation> supplyTypeLocations) {
		this.supplyTypeLocations = supplyTypeLocations;
	}
	
	public Integer getSupplierId() {
        return supplierId;
    }
    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }
    public Integer getTypeCodeId() {
        return typeCodeId;
    }
    public void setTypeCodeId(Integer typeCodeId) {
        this.typeCodeId = typeCodeId;
    }
    public Integer getCo2FormulaId() {
        return co2FormulaId;
    }
    public void setCo2FormulaId(Integer co2FormulaId) {
        this.co2FormulaId = co2FormulaId;
    }
    @Override
    public String toString()
    {
        return "Supply Type " + toJSONString();
    }
	
    public String toJSONString() {
    	JSONStringer js = null;

    	try {
    		js = new JSONStringer();
    		js.object().key("id").value(this.id)
    				   .key("supplier").value((this.supplier == null)? "null":this.supplier.getId())
    				   .key("type").value((this.typeCode == null)? "null":this.typeCode.getName())
    				   .key("typeDescr").value((this.typeCode == null)? "null":this.typeCode.getDescr())
    				   .key("typeCode").value((this.typeCode == null)? "null":this.typeCode.getCode())
    	    		   .key("co2Formula").array().value((this.co2Formula == null)? null:JSONSerializer.toJSON(this.co2Formula.toString())).endArray()
    				   .key("billDate").value((this.billDate == null)? "null":this.billDate).endObject();
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    	return js.toString();
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
        return this.getSupplier().getInstanceName();
    }
}
