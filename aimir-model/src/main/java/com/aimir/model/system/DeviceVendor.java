package com.aimir.model.system;

import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * <pre>
 * AIMIR System 에서 사용하는 Device들의 제조사 정보를 나타낸다.
 * Device의 제조사의 고유코드, 제조사명, 제조사 정보와 연관되는 제조사의 모델정보들을 가지고 있다.
 * 집중기, 모뎀, 미터, ACD, IHD 등 모든 장비에 대한 제조사 정보   
 * 집중기,  
 * 모뎀,  
 * 미터,  
 * ACD,  
 * IHD 등 모든 장비에 대한 제조사 정보 
 * supplierId는 해당 제조사를 사용하는 전력회사 가스 회사가 된다. 
 * Supplier 클래스의 주키가 된다. 
 * </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="DEVICEVENDOR", uniqueConstraints=@UniqueConstraint(columnNames={"supplier_id","name"}))
// @Cache(type=CacheType.SOFT)
public class DeviceVendor extends BaseObject implements JSONString, IAuditable {

	private static final long serialVersionUID = -9080093327001193722L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DEVICEVENDOR_SEQ")
	@SequenceGenerator(name="DEVICEVENDOR_SEQ", sequenceName="DEVICEVENDOR_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="supplier_id")
	@ColumnInfo(name="공급사")
	@ReferencedBy(name="name")
	private Supplier supplier;
	
	@Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
	private Integer supplierId;
	
	@Column
	@ColumnInfo(name="고유코드")
	private Integer code;
	
	@Column(nullable=false,length=100)
	@ColumnInfo(name="제조사명")
	private String name;
	
	@Column(length=200)
	@ColumnInfo(name="주소")
	private String address;
	
	@Column(length=300)
	@ColumnInfo(name="비고")
	private String descr;
	
	@OneToMany(mappedBy = "deviceVendor", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@ColumnInfo(name="장비모델")	
	private List<DeviceModel> deviceModels = new ArrayList<DeviceModel>(0);
	
	public DeviceVendor() {
	}
	
	public DeviceVendor(Integer id) {
		this.id = id;
	}
	
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
	}

	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
/*	
	@OneToMany(mappedBy = "deviceVendor", cascade = CascadeType.ALL)
	@ColumnInfo(name="장비모델")	
	*/
	@XmlTransient
	public List<DeviceModel> getDeviceModels() {
		return deviceModels;
	}
	public void setDeviceModels(List<DeviceModel> deviceModels) {
		this.deviceModels = deviceModels;
	}

	public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    @Override
	public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null || getClass() != obj.getClass() ) return false;

        final DeviceVendor other = (DeviceVendor) obj;

        if ( !id.equals( other.id ) ) return false;
        if ( !name.equals( other.name ) ) return false;

        return true;

	}
	
	@Override
	public String toString()
	{
	    return "DeviceVendor " + toJSONString();
	}

	public String toJSONString() {
	    String retValue = "";

	    retValue = "{"
	        + "id:'" + this.id 
	        + "',supplier:'" + ((this.supplier == null)? "null":supplier.getId()) 
	        + "',code:'" + this.code 
	        + "',name:'" + this.name 
	        + "',address:'" + this.address 
	        + "',descr:'" + this.descr 
	        + "'}";
	    
	    return retValue;
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
